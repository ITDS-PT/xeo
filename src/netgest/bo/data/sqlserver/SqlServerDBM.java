/**
 * 
 */
package netgest.bo.data.sqlserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.naming.InitialContext;

import netgest.bo.boConfig;
import netgest.bo.boDataSource;
import netgest.bo.builder.boBuildDB;
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.system.boApplicationConfig;
import netgest.bo.system.boRepository;
import netgest.utils.StringUtils;

/**
 * @author vcardoso
 * 
 */
public class SqlServerDBM extends OracleDBM {

	private static Logger logger = Logger
			.getLogger("netgest.bo.data.sqlserver.SqlServerDBM");

	private final static String TABLESPACE_NAME = boConfig.getTableSpace();
	public static String[] OBJECTTYPEORDER = { "T", "F", "IDX", "PK", "UN",
			"FK", "PCK", "V" };
	private EboContext p_ctx;
	private ResultSet p_rs;
	private static Hashtable p_connectionCache = new Hashtable();
	private PreparedStatement p_activeInsertPstm;
	private PreparedStatement p_activeUpdatePstm;
	private PreparedStatement p_activeUpdateClobPstm;
	private PreparedStatement p_activeDeletePstm;

	public SqlServerDBM() {
	}

	public void setEnvironment(EboContext ngtctx) throws SQLException {
		p_ctx = ngtctx;
	}

	public static final void hardClose2() {
		Enumeration oEnum = p_connectionCache.elements();
		while (oEnum.hasMoreElements()) {
			Connection cn = (Connection) oEnum.nextElement();
			try {
				cn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		p_connectionCache.clear();
	}

	public Connection getRepositoryConnection(boApplication app,
			String reposiToryName, int type) {
		String repositoryNameLower = reposiToryName.toLowerCase();
		Connection ret = (Connection) p_connectionCache.get(repositoryNameLower
				+ "[" + type + "]");
		try {
			if (ret == null || ret.isClosed()) {
				if (type == 1) // Connection to schema data
				{
					ret = boRepository.getRepository(app, repositoryNameLower)
							.getDedicatedConnection();
				} else if (type == 2) // Connection to schema def
				{
					ret = boRepository.getRepository(app, repositoryNameLower)
							.getDedicatedConnectionDef();
				}

				p_connectionCache.put(repositoryNameLower + "[" + type + "]",
						ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public void close() {
		hardClose2();
		try {
			if (p_activeDeletePstm != null) {
				p_activeDeletePstm.close();
			}
		} catch (SQLException e) {
			// ignore
		}

		try {
			if (p_activeInsertPstm != null) {
				p_activeInsertPstm.close();
			}
		} catch (SQLException e) {
			// ignore
		}

		try {
			if (p_activeUpdateClobPstm != null) {
				p_activeUpdateClobPstm.close();
			}
		} catch (SQLException e) {
			// ignore
		}

		try {
			if (p_activeUpdatePstm != null) {
				p_activeUpdatePstm.close();
			}
		} catch (SQLException e) {
			// ignore
		}
	}

	public void createDabaseObjects(EboContext ctx, ResultSet rs, String modo)
			throws SQLException {
		if (rs.isBeforeFirst() && rs.isAfterLast()) {
			return;
		}
		try {
			p_ctx = ctx;
			p_rs = rs;

			Vector tables = new Vector();
			int i = -1;

			if (modo.equals("3")) {
				i = OBJECTTYPEORDER.length;
			}

			for (;;) {
				if (modo.equals("3")) {
					i--;
				} else {
					i++;
				}

				if (modo.equals("3") && (i < 0)) {
					break;
				} else if (i >= OBJECTTYPEORDER.length) {
					break;
				}

				rs.beforeFirst();

				while (rs.next()) {
					rs.updateString("OBJECTNAME", rs.getString("OBJECTNAME")
							.toUpperCase());
					rs.updateString("TABLENAME", rs.getString("TABLENAME")
							.toUpperCase());
					rs.updateString("SCHEMA", rs.getString("SCHEMA")
							.toUpperCase());
					rs.updateRow();

					if (rs.getString("OBJECTTYPE").equals(OBJECTTYPEORDER[i])) {
						this.AddAction(rs, modo);

						if (OBJECTTYPEORDER[i].equals("T")
								|| OBJECTTYPEORDER[i].equals("F")) {
							if (tables.indexOf(rs.getString("TABLENAME")) == -1) {
								tables.add(rs.getString("TABLENAME"));
							}
						}
					}
				}
			}
		} finally {

			close();
		}
	}

	public void createDictionaryFromTable(String[] tables, String schema)
			throws SQLException {
		createDictionaryFromTable(tables, schema, p_ctx.getConnectionDef(),
				p_ctx.getConnectionData());
	}

	protected static void createDictionaryFromTable(String[] tables,
			String schema, Connection cndef, Connection cn) throws SQLException {
		try {
			PreparedStatement pstmdict = cndef
					.prepareStatement("select \"SCHEMA\",objectname,objecttype,fieldtype,fieldsize,tablename from NGTDIC where "
							+ "tablename = ? and objecttype = 'T' and objectname = ?  and \"SCHEMA\"=?");

			PreparedStatement pstmvtbt = cndef
					.prepareStatement("select count(*) from ngtvirtualtablesx where "
							+ "stablename = ? and svirtualtable=?");

			PreparedStatement insertps;

			for (int i = 0; i < tables.length; i++) {
				String table = tables[i];

				String prefix = "";

				if (schema.equals("DEF")) {
					prefix = "NGD_";
				} else if (schema.equals("SYS")) {
					prefix = "SYS_";
				}

				String vt = prefix + table;

				pstmdict.setString(1, table);
				pstmdict.setString(2, table);
				pstmdict.setString(3, schema);

				ResultSet rsltt = pstmdict.executeQuery();

				if (!rsltt.next()) {
					/*
					 * #sql [sqlctxdef] { INSERT INTO NGTDIC (SCHEMA,
					 * OBJECTNAME, OBJECTTYPE, TABLENAME ) VALUES ( :schema,
					 * :table, 'T', :table ) };
					 */
					insertps = cndef
							.prepareStatement("INSERT INTO NGTDIC (\"SCHEMA\", OBJECTNAME, OBJECTTYPE, TABLENAME) VALUES (?,?,?,?)");
					insertps.setString(1, schema);
					insertps.setString(2, table);
					insertps.setString(3, "T");
					insertps.setString(4, table);
					insertps.executeUpdate();
					insertps.close();
				}

				rsltt.close();

				pstmvtbt.setString(1, table);
				pstmvtbt.setString(2, vt);

				PreparedStatement pstm = cn
						.prepareStatement("select COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE from INFORMATION_SCHEMA.COLUMNS where "
								+ "TABLE_SCHEMA=? AND upper(TABLE_NAME)=?");
				pstm.setString(1, schema);
				pstm.setString(2, tables[i]);

				ResultSet rslt = pstm.executeQuery();

				while (rslt.next()) {
					String fname = rslt.getString(1);
					String fnamesql = "\"" + fname + "\"";

					if (!rslt.getString(2).equals("UNDEFINED")) {
						String ftype = getNgtFieldTypeFromDDL(rslt.getString(2));
						String fldsize = ftype.equals("N") ? (rslt.getString(4) + ((rslt
								.getInt(5) > 0) ? ("," + rslt.getInt(5)) : ""))
								: rslt.getString(3);

						if (ftype.equals("D")) {
							fldsize = "10";
						}

						String[] vtfields = new String[1];
						String[] vtftype = new String[1];
						String[] vtfsize = new String[1];
						String[] vtfdesc = { "" };
						String[] vtfdef = { "" };

						vtfields[0] = fname;
						vtfsize[0] = fldsize;
						vtftype[0] = ftype;

						checkDicFields(table, schema, vtfields, vtftype,
								vtfsize, vtfdesc, vtfdef, cndef, cn);
					}
				}

				rslt.close();
				pstm.close();
			}

			pstmvtbt.close();
			pstmdict.close();

			PreparedStatement pstmdic = cndef
					.prepareStatement("SELECT \"SCHEMA\",OBJECTNAME,TABLENAME,OBJECTTYPE FROM NGTDIC WHERE "
							+ "TABLENAME=? AND \"SCHEMA\"=?");

			for (int i = 0; i < tables.length; i++) {
				String table = tables[i];

				String prefix = "";

				if (schema.equals("DEF")) {
					prefix = "NGD_";
				} else if (schema.equals("SYS")) {
					prefix = "SYS_";
				}

				String vt = prefix + table;

				pstmdic.setString(1, table);
				pstmdic.setString(2, schema);

				ResultSet rsltdic = pstmdic.executeQuery();

				PreparedStatement pstmf = cn
						.prepareStatement("select count(*) from INFORMATION_SCHEMA.COLUMNS where upper(TABLE_NAME)=? and upper(COLUMN_NAME)=? AND TABLE_SCHEMA=?");
				PreparedStatement pstmt = cn
						.prepareStatement("select count(*) from INFORMATION_SCHEMA.TABLES where upper(TABLE_NAME)=? AND TABLE_SCHEMA=?");

				while (rsltdic.next()) {
					String xobjname = rsltdic.getString("OBJECTNAME");
					String xtablena = rsltdic.getString("TABLENAME");

					if (rsltdic.getString("OBJECTTYPE").equals("F")) {
						pstmf.setString(1, rsltdic.getString("TABLENAME"));
						pstmf.setString(2, rsltdic.getString("OBJECTNAME"));
						pstmf.setString(3, schema);

						ResultSet rsltf = pstmf.executeQuery();

						if (rsltf.next()) {
							if (rsltf.getInt(1) == 0) {
								/*
								 * #sql [sqlctxdef] { DELETE FROM NGTDIC WHERE
								 * OBJECTNAME=:xobjname AND OBJECTTYPE='F' AND
								 * SCHEMA=:schema AND TABLENAME=:xtablena };
								 */
								insertps = cndef
										.prepareStatement("DELETE FROM NGTDIC WHERE OBJECTNAME=? AND OBJECTTYPE='F' AND \"SCHEMA\"=?  AND TABLENAME=?");
								insertps.setString(1, xobjname);
								insertps.setString(2, schema);
								insertps.setString(3, xtablena);
								insertps.executeUpdate();
								insertps.close();
							}
						}

						rsltf.close();
					} else if (rsltdic.getString("OBJECTTYPE").equals("T")) {
						pstmt.setString(1, rsltdic.getString("OBJECTNAME"));
						pstmt.setString(2, schema);

						ResultSet rsltt = pstmt.executeQuery();

						if (rsltt.next() && (rsltt.getInt(1) == 0)) {
							/*
							 * #sql [sqlctxdef] { DELETE FROM NGTDIC WHERE
							 * TABLENAME=:xtablena };
							 */
							insertps = cndef
									.prepareStatement("DELETE FROM NGTDIC WHERE TABLENAME=?");
							insertps.setString(1, xtablena);
							insertps.executeUpdate();
							insertps.close();
						}

						rsltt.close();
					}
				}

				pstmf.close();
				pstmt.close();
				rsltdic.close();
			}

			pstmdic.close();
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
		}
	}

	public void setGrantsOnParent(String newSchema) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection parentcon = null;
		boBuildDB dbd;

		try {
			boRepository parentRepository = p_ctx.getBoSession()
					.getRepository();

			if (parentRepository != null) {
				parentcon = p_ctx.getConnectionDef();
				ps = parentcon
						.prepareStatement("select tablename from NGTDIC where objecttype = 'T'");
				rs = ps.executeQuery();

				while (rs.next()) {
					String dml = "grant select, insert, update, delete, references on "
							+ rs.getString(1) + " to " + newSchema;
					executeDDL(dml, parentRepository.getName());
				}
			}
		} catch (Exception e) {
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	public void createSchema(String schemaName, String pass,
			String objController, long boui) throws SQLException,
			boRuntimeException {
		try {
			String dml = null;
			String parentName = "default";
			String usingSchema = p_ctx.getBoSession().getRepository()
					.getSchemaName();
			String creationScript = null;
			boolean regist = false;

			if (!existsSchema(p_ctx, schemaName)) {
				creationScript = " USER " + schemaName + " IDENTIFIED BY "
						+ pass;
				creationScript += (" DEFAULT TABLESPACE " + TABLESPACE_NAME
						+ " QUOTA unlimited ON " + TABLESPACE_NAME);
				executeDDL(creationScript, p_ctx.getBoSession().getRepository()
						.getName());
				regist = true;
			}

			// GRANT
			dml = "GRANT connect,ctxapp, dba to " + schemaName;
			executeDDL(dml, p_ctx.getBoSession().getRepository().getName());

			// escreve no boconfig
			if (boConfig.getConfigRepository(schemaName) == null) {
				boDataSource bds = new boDataSource(schemaName, schemaName,
						schemaName, schemaName, "", "", parentName);
				bds.writeTo();
			}

			// register schema
			registerSchema(schemaName, objController, boui, creationScript,
					usingSchema);
		} finally {
		}
	}

	public void createSpecialTables(String schemaName) throws SQLException,
			boRuntimeException {

		if (!existsTable(p_ctx, schemaName, "NGTDIC")) {
			createNgtdic(schemaName);
		}

		if (!existsTable(p_ctx, schemaName, "EBO_TEXTINDEX")) {
			createTableIndex(schemaName);
		}

		try {
			createSequencesTablesAndFunc(schemaName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createEbo_TemplateIndex(EboContext p_ctx, String schemaName) {
		this.p_ctx = p_ctx;
		try {
			if (!existsIndex(p_ctx, schemaName, "SYS_IM_EBO_TEMPLATE")) {
				StringBuffer dml = new StringBuffer();
				String name = p_ctx.getBoSession().getRepository().getName();
				// Comentado para rever - Luï¿½s
				/*
				 * dml.append("create index ")
				 * .append("SYS_IM_EBO_TEMPLATE on ").append(schemaName).append(
				 * ".EBO_TEMPLATE(keywords) indextype is ctxsys.context parameters ('lexer xeo_lexer')"
				 * ); executeDDL(dml.toString(), name);
				 */
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void createNgtdic(String newSchemaName) throws SQLException {
		try {
			StringBuffer dml = new StringBuffer();
			String name = p_ctx.getBoSession().getRepository().getName();
			dml.append("create table ").append(newSchemaName).append(".NGTDIC")
					.append("(").append("TABLENAME      VARCHAR(50) not null,")
					.append("OBJECTNAME     VARCHAR(50) not null,").append(
							"OBJECTTYPE     VARCHAR(10) not null,").append(
							"REQUIRED       CHAR(1),").append(
							"FIELDSIZE      VARCHAR(10) ,").append(
							"FIELDTYPE      VARCHAR(50) ,").append(
							"FRIENDLYNAME   VARCHAR(500) ,").append(
							"EXPRESSION     TEXT,").append(
							"RULES          VARCHAR(255) ,").append(
							"PICTURE        VARCHAR(200) ,").append(
							"TABLEREFERENCE VARCHAR(100) ,").append(
							"FIELDREFERENCE VARCHAR(255) ,").append(
							"MACROFIELD     VARCHAR(50) ,").append(
							"SYS_ICN        INT,").append(
							"SYS_USER       VARCHAR(25) ,").append(
							"SYS_DTCREATE   DATETIME,").append(
							"SYS_DTSAVE     DATETIME,").append(
							"DEFAULTVALUE   VARCHAR(500) ,").append(
							"SEARCHDOMAIN   VARCHAR(30) ,").append(
							"\"SCHEMA\"     VARCHAR(30) not null,").append(
							"NGTTABLESPACE  VARCHAR(30) ,").append(
							"NGTINITIAL     VARCHAR(15) ,").append(
							"NGTNEXT        VARCHAR(15) ,").append(
							"NGTPCTINCREASE VARCHAR(15) ,").append(
							"NGTMINEXTENTS  VARCHAR(15) ,").append(
							"NGTMAXEXTENTS  VARCHAR(15) ,").append(
							"NGTPCTFREE     VARCHAR(15) ,").append(
							"NGTPCTUSED     VARCHAR(15) ,").append(
							"NGTINITRANS    VARCHAR(15) ,").append(
							"NGTMAXTRANS    VARCHAR(15) ,").append(
							"OWNER          VARCHAR(30) ,").append(
							"DELETECASCADE  CHAR(1) ,").append(
							"CACHETTL       INT default -1").append(");");
			// executeDDL(dml.toString(), name);

			// dml.delete(0, dml.length());
			dml
					.append("alter table ")
					.append(newSchemaName)
					.append(
							".NGTDIC add primary key (\"SCHEMA\",TABLENAME,OBJECTNAME,OBJECTTYPE);");
			// executeDDL(dml.toString(), name);

			// dml.delete(0, dml.length());
			dml.append("create index ").append("IDX_OBJECTTYPE on ").append(
					newSchemaName).append(".NGTDIC (OBJECTTYPE);");
			// executeDDL(dml.toString(), name);

			// dml.delete(0, dml.length());
			dml.append("create index ").append("IDX_TABLEREFERENCE on ")
					.append(newSchemaName).append(".NGTDIC (TABLEREFERENCE);");
			executeDDL(dml.toString(), name);
		} catch (SQLException e) {
			throw (e);
		} finally {
		}
	}

	private static final Timestamp toTimestamp(String value) {
		Timestamp toRet = null;

		try {
			if (value != null) {
				// dd-mm-yyyy hh24:mi:ss
				SimpleDateFormat df = new SimpleDateFormat(
						"dd-mm-yyyy HH:mm:ss");
				Date auxDate = df.parse(value);
				toRet = new Timestamp(auxDate.getTime());
			}
		} catch (ParseException e) {
			// ignore
		}

		return toRet;
	}

	private void createSequencesTablesAndFunc(final String schemaName) {
		String name = p_ctx.getBoSession().getRepository().getName();

		PreparedStatement pstm = null;
		ResultSet rslt = null;
		try {
			pstm = p_ctx
					.getConnectionData()
					.prepareStatement(
							"select * from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA=? and upper(TABLE_NAME)='SYS_SEQUENCES'");
			pstm.setString(1, schemaName);
			rslt = pstm.executeQuery();
			if (!rslt.next()) {
				createSequencesTable(schemaName);
			}
			pstm.close();
			rslt.close();

			pstm = p_ctx
					.getConnectionData()
					.prepareStatement(
							"select * from INFORMATION_SCHEMA.ROUTINES where ROUTINE_SCHEMA=? AND ROUTINE_TYPE='PROCEDURE' AND UPPER(ROUTINE_NAME)='NEXTVAL'");
			pstm.setString(1, schemaName);
			rslt = pstm.executeQuery();
			if (!rslt.next()) {
				createSequencesFunction(schemaName);
			}
			pstm.close();
			rslt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (pstm != null)
					pstm.close();
				if (rslt != null)
					rslt.close();
			} catch (SQLException e) {

			}
		}
	}

	private void createSequencesTable(String newSchemaName) throws SQLException {
		StringBuffer dml = new StringBuffer();
		String name = p_ctx.getBoSession().getRepository().getName();

		dml
				.append(" CREATE TABLE ")
				.append(newSchemaName)
				.append(".SYS_SEQUENCES ( ")
				.append("  \"sequence_name\" varchar(100)  NOT NULL, ")
				.append("  \"sequence_increment\" int DEFAULT 1, ")
				.append("  \"sequence_min_value\" int DEFAULT 1, ")
				.append(
						"  \"sequence_max_value\" bigint DEFAULT 1844674407370955161, ")
				.append("  \"sequence_cur_value\" bigint DEFAULT 1, ").append(
						"  \"sequence_cycle\" tinyint DEFAULT 0, ").append(
						"  PRIMARY KEY (\"sequence_name\") ").append(" ); ");
		executeDDL(dml.toString(), name);
	}

	private void createSequencesFunction(String newSchemaName)
			throws SQLException {
		StringBuffer sb = new StringBuffer();

		sb.append("CREATE PROCEDURE ").append(newSchemaName).append(
				".NEXTVAL(@seq_name varchar(100)) \n");
		sb.append("AS  \n");
		sb.append("BEGIN  \n");
		sb
				.append("DECLARE @cur_val bigint, @seq_inc int, @soma bigint, @seq_max bigint, @seq_cycle tinyint, \n");
		sb.append("@seq_min int \n");

		sb
				.append("SET @cur_val=(SELECT sequence_cur_value FROM sys_sequences WHERE sequence_name = @seq_name) \n");
		sb
				.append("SET @seq_inc=(SELECT sequence_increment FROM sys_sequences WHERE sequence_name = @seq_name) \n");
		sb
				.append("SET @seq_max=(SELECT sequence_max_value FROM sys_sequences WHERE sequence_name = @seq_name) \n");
		sb
				.append("SET @seq_cycle=(SELECT sequence_cycle FROM sys_sequences WHERE sequence_name = @seq_name) \n");
		sb
				.append("SET @seq_min=(SELECT sequence_min_value FROM sys_sequences WHERE sequence_name = @seq_name) \n");

		sb.append("BEGIN TRY \n");
		sb.append("BEGIN TRANSACTION; \n");

		sb.append("IF @cur_val IS NOT NULL \n");
		sb.append("begin \n");

		sb.append("set @soma = @seq_inc + @cur_val \n");

		sb.append("if @soma > @seq_max \n");
		sb.append("begin \n");
		sb.append("if @seq_cycle = 1 \n");
		sb.append("set @cur_val = @seq_min \n");
		sb.append("else  \n");
		sb.append("set @cur_val = null \n");
		sb.append("end \n");
		sb.append("else \n");
		sb.append("set @cur_val = @soma \n");

		sb
				.append("UPDATE sys_sequences SET sequence_cur_value = @cur_val WHERE sequence_name = @seq_name \n");
		sb.append("end; \n");
		sb.append("ELSE \n");
		sb.append("begin \n");
		sb.append("SET @cur_val = 1; \n");
		sb
				.append("insert into sys_sequences (sequence_name,sequence_cur_value) values (@seq_name,@cur_val) \n");
		sb.append("end \n");

		sb.append("COMMIT TRANSACTION; \n");
		sb.append("END TRY \n");

		sb.append("BEGIN CATCH \n");
		sb.append("ROLLBACK TRANSACTION; \n");
		sb.append("RAISERROR ('Error seq!',16,1) \n");
		sb.append("END CATCH \n");

		sb.append("return @cur_val \n");

		sb.append("END \n");

		executeDDL(sb.toString(), p_ctx.getBoSession().getRepository()
				.getName());

	}

	private void createTableIndex(String newSchemaName) throws SQLException {
		StringBuffer dml = new StringBuffer();
		String name = p_ctx.getBoSession().getRepository().getName();
		if (!existsCatalog())dml.append("create FULLTEXT catalog xeo as default;");
		dml.append("create table ").append(newSchemaName).append(
				".EBO_TEXTINDEX (BOUI bigint, text TEXT,").append(
				"SYS_ICN        NUMERIC(7),").append(
				"SYS_USER       VARCHAR(25) ,").append(
				"SYS_DTCREATE   DATETIME,").append("SYS_DTSAVE     DATETIME,")
				.append("SYS_ORIGIN     VARCHAR(30),").append(
						"PRIMARY KEY (\"BOUI\")").append(");");
		dml.append("create unique index unique_boui_ebo_textindex on ").append(
				newSchemaName).append(".EBO_TEXTINDEX (BOUI);");
		
		executeDDL(dml.toString(), name);
		dml = new StringBuffer();
		dml.append("create fulltext index on ").append(newSchemaName).append(
		".EBO_TEXTINDEX (text) key index unique_boui_ebo_textindex;");
		executeDDL(dml.toString(), name);
	}

	private boolean existsCatalog() throws SQLException
	{		
		
		Connection cn = null;
		ResultSet rslt = null;
		PreparedStatement pstm = null;

		try {
			cn = p_ctx.getDedicatedConnectionData();
			pstm = cn
					.prepareStatement("select count(*) from sys.fulltext_catalogs where is_default=1 ");
			

			rslt = pstm.executeQuery();

			if (rslt.next()) {
				if (rslt.getInt(1) > 0) {
					return true;
				}
			}

			// rslt.close();
			// pstm.close();

			return false;
		} catch (SQLException e) {
			throw (e);
		} finally {
			try {
				if (rslt != null) {
					rslt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (pstm != null) {
					pstm.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (cn != null) {
					cn.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	private boolean existsIndex(EboContext ctx, String schemaName,
			String indexName) throws SQLException {
		Connection cn = null;
		ResultSet rslt = null;
		PreparedStatement pstm = null;

		try {
			cn = ctx.getDedicatedConnectionData();
			pstm = cn
					.prepareStatement("select count(*) from SYSINDEXES where upper(NAME)=? ");
			pstm.setString(1, indexName.toUpperCase());

			rslt = pstm.executeQuery();

			if (rslt.next()) {
				if (rslt.getInt(1) > 0) {
					return true;
				}
			}

			// rslt.close();
			// pstm.close();

			return false;
		} catch (SQLException e) {
			throw (e);
		} finally {
			try {
				if (rslt != null) {
					rslt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (pstm != null) {
					pstm.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (cn != null) {
					cn.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	private boolean existsTable(EboContext ctx, String schemaName,
			String tableName) throws SQLException {
		Connection cn = null;
		ResultSet rslt = null;
		PreparedStatement pstm = null;

		try {
			cn = ctx.getDedicatedConnectionData();
			if (cn.getAutoCommit()) {
				cn.setAutoCommit(false);
			}
			pstm = cn
					.prepareStatement("select count(*) from information_schema.tables where TABLE_SCHEMA=? AND TABLE_NAME=?");
			pstm.setString(1, schemaName);
			pstm.setString(2, tableName.toUpperCase());
			rslt = pstm.executeQuery();

			if (rslt.next()) {
				if (rslt.getInt(1) > 0) {
					return true;
				}
			}

			// rslt.close();
			// pstm.close();

			return false;
		} catch (SQLException e) {
			throw (e);
		} finally {
			try {
				if (rslt != null) {
					rslt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (pstm != null) {
					pstm.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (cn != null) {
					cn.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	private String AddAction(ResultSet node, String modo) throws SQLException {
		Connection cn = null;
		Connection cndef = null;

		try {
			cn = this.getRepositoryConnection(p_ctx.getApplication(), p_ctx
					.getBoSession().getRepository().getName(), 1);
			cndef = this.getRepositoryConnection(p_ctx.getApplication(), p_ctx
					.getBoSession().getRepository().getName(), 2);

			String dml = null;
			String objecttype = node.getString("OBJECTTYPE");

			if (objecttype.equalsIgnoreCase("T")) {
				PreparedStatement pstm = cn
						.prepareStatement("select count(*) from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA=user AND upper(TABLE_NAME)=?");
				pstm.setString(1, node.getString("OBJECTNAME"));

				ResultSet rslt = pstm.executeQuery();
				rslt.next();

				boolean exists = rslt.getInt(1) != 0;
				rslt.close();
				pstm.close();

				String[] sysflds = { "SYS_USER", "SYS_ICN", "SYS_DTCREATE",
						"SYS_DTSAVE", "SYS_ORIGIN" };
				String[] sysfdef = { "VARCHAR(25)", "NUMERIC(7)", "DATETIME ",
						"DATETIME", "VARCHAR(30)" };
				String[] sysftyp = { "C", "N", "D", "D", "C" };
				String[] sysfsiz = { "25", "7", "", "", "30" };
				String[] sysfndef = { "", "", "", "", "" };
				String[] sysfdes = { "", "", "", "", "" };

				if (!exists && !modo.equals("3")) {
					dml = "CREATE TABLE " + node.getString("OBJECTNAME") + " (";

					for (int i = 0; i < sysflds.length; i++) {
						dml += (sysflds[i] + " " + sysfdef[i] + ((i < (sysflds.length - 1)) ? ","
								: ")"));
					}

					String vt = node.getString("OBJECTNAME");

					if (node.getString("SCHEMA").equals("DEF")) {
						vt = "NGD_" + vt;
					} else if (node.getString("SCHEMA").equals("SYS")) {
						vt = "SYS_" + vt;
					}

					executeDDL(dml, node.getString("SCHEMA"));
				}

				if (modo.equals("3") && exists) {
					executeDDL("DROP TABLE " + node.getString("OBJECTNAME"),
							node.getString("SCHEMA"));

					CallableStatement call = cndef
							.prepareCall("DELETE FROM NGTDIC WHERE TABLENAME=?");
					call.setString(1, node.getString("OBJECTNAME"));
					call.executeUpdate();
					call.close();
				}

				// checkVtblFields(node.getString("OBJECTNAME"),node.getString("SCHEMA"),sysflds,modo,true);
				checkDicFields(node.getString("OBJECTNAME"), node
						.getString("SCHEMA"), sysflds, sysftyp, sysfsiz,
						sysfndef, sysfdes);
			}

			if (objecttype.equalsIgnoreCase("F")) {
				boolean fldchg = false;
				boolean fldexi = false;
				PreparedStatement pstm = cn
						.prepareStatement("select COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE from INFORMATION_SCHEMA.COLUMNS "
								+ "where  TABLE_SCHEMA=user AND upper(TABLE_NAME)=? and upper(COLUMN_NAME)=?");
				pstm.setString(1, node.getString("TABLENAME"));
				pstm.setString(2, node.getString("OBJECTNAME"));

				ResultSet rslt = pstm.executeQuery();

				// Debug info...
				String otname = node.getString("TABLENAME");
				String ofname = node.getString("OBJECTNAME");
				String ofsize = node.getString("FIELDSIZE");

				if (rslt.next()) {
					// String fieldlen = rslt.getString(2);
					long fieldsiz = rslt.getLong(3);
					int fielddec = rslt.getInt(5);

					if (",C,N,".indexOf(","
							+ getNgtFieldTypeFromDDL(rslt.getString(2)) + ",") != -1) {
						if (getNgtFieldTypeFromDDL(rslt.getString(2)).equals(
								"C")) {
							fieldsiz = rslt.getInt(3);
						}

						if (fielddec != 0) {
							if (!(fieldsiz + "," + fielddec).equals(node
									.getString("FIELDSIZE"))) {
								fldchg = true;
							}
						} else {
							if (!((fieldsiz == 0) && ((node
									.getString("FIELDSIZE") == null) || (node
									.getString("FIELDSIZE").length() == 0)))) {
								if (!("" + fieldsiz).equals(node
										.getString("FIELDSIZE"))) {
									fldchg = true;
								}
							}
						}
					}

					fldexi = true;
				} else {
					fldexi = false;
				}

				rslt.close();
				pstm.close();

				boolean drop = false;

				if (("20".indexOf(modo) != -1) && !fldexi) {
					dml = "ALTER TABLE " + node.getString("TABLENAME")
							+ " add \"" + node.getString("OBJECTNAME") + "\" ";
				} else if (("20".indexOf(modo) != -1) && fldexi && fldchg) {
					dml = "ALTER TABLE " + node.getString("TABLENAME")
							+ " alter column \"" + node.getString("OBJECTNAME")
							+ "\" ";
				} else if (modo.equals("3") && fldexi) {
					dml = "ALTER TABLE " + node.getString("TABLENAME")
							+ " drop column \"" + node.getString("OBJECTNAME")
							+ "\" ";

					PreparedStatement pstmrelc = cn
							.prepareStatement("SELECT A.CONSTRAINT_NAME, B.COLUMN_KEY FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE A, INFORMATION_SCHEMA.COLUMNS B WHERE "
									+ "A.TABLE_SCHEMA=B.TABLE_SCHEMA AND A.TABLE_NAME=B.TABLE_NAME AND A.COLUMN_NAME=B.COLUMN_NAME AND upper(A.TABLE_NAME)=? AND upper(A.COLUMN_NAME)=?"
									+ "AND TABLE_SCHEMA=user");
					pstmrelc.setString(1, node.getString("TABLENAME"));
					pstmrelc.setString(2, node.getString("OBJECTNAME"));

					ResultSet rsltrelc = pstmrelc.executeQuery();

					while (rsltrelc.next()) {
						String constname = rsltrelc.getString(1);
						String consttype = rsltrelc.getString(2);
						PreparedStatement pstmdic = cndef
								.prepareStatement("DELETE FROM NGTDIC WHERE TABLENAME=? AND OBJECTTYPE=? AND OBJECTNAME=?");
						pstmdic.setString(1, node.getString("TABLENAME"));
						pstmdic.setString(2, consttype.equals("MUL") ? "FK"
								: "PK");
						pstmdic.setString(3, constname);

						int nrecs = pstmdic.executeUpdate();
						pstm.close();
						executeDDL("ALTER TABLE " + node.getString("TABLENAME")
								+ " DROP CONSTRAINT " + constname, node
								.getString("SCHEMA"));
					}

					rsltrelc.close();
					pstmrelc.close();
				}

				String colName = node.getString("OBJECTNAME").trim();

				if ((dml != null) && (dml.length() > 0) && !modo.equals("3")) {
					String mfield = node.getString("MACROFIELD");

					if ((mfield != null)
							&& !(!mfield.equals("TEXTOLIVRE")
									&& !mfield.equals("NUMEROLIVRE")
									&& !mfield.equals("LONGTEXT")
									&& !mfield.equals("BLOB") && !mfield
									.equals("MDATA"))) {
						String ngtft = "";

						if (mfield.equals("TEXTOLIVRE")) {
							ngtft = "C";
						} else if (mfield.equals("NUMEROLIVRE")) {
							ngtft = "N";
						} else if (mfield.equals("RAW")) {
							ngtft = "RAW";
						} else if (mfield.equals("TIMESTAMP")) {
							ngtft = "TIMESTAMP";
						} else if (mfield.equals("MDATA")) {
							ngtft = "D";
						} else if (mfield.equals("LONGTEXT")) {
							ngtft = "CL";
						} else if (mfield.equals("BLOB")) {
							ngtft = "BL";
						}

						if ("BOUI".equalsIgnoreCase(colName)
								|| colName.endsWith("$")) {
							dml += "BIGINT";
						} else {

							dml += getDDLFieldFromNGT(ngtft, node
									.getString("FIELDSIZE"));
						}
					} else if ((mfield != null) && (mfield.length() > 0)) {
						dml += getMacrofieldDef(cndef, node
								.getString("MACROFIELD"));
					} else {
						if ("BOUI".equalsIgnoreCase(colName)
								|| colName.endsWith("$")) {
							dml += "BIGINT";
						} else {
							dml += getDDLFieldFromNGT(node
									.getString("FIELDTYPE"), node
									.getString("FIELDSIZE"));
						}
					}

					if (("20".indexOf(modo) == -1) || !fldexi || !fldchg)
						dml += "";
				}

				String[] flds = new String[1];
				flds[0] = node.getString("OBJECTNAME");

				if (dml != null) {
					executeDDL(dml, node.getString("SCHEMA"));
				}

				// this.checkVtblFields(node.getString("TABLENAME"),node.getString("SCHEMA"),flds,modo,false);
			}

			if (objecttype.equalsIgnoreCase("V")) {
				String viewText = null;
				PreparedStatement pstmrelc = cn
						.prepareStatement("SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE upper(TABLE_NAME)=? AND TABLE_SCHEMA=user");
				pstmrelc.setString(1, node.getString("OBJECTNAME"));

				ResultSet rsltrelc = pstmrelc.executeQuery();
				boolean exists = false;

				if (rsltrelc.next()) {
					exists = true;
					viewText = rsltrelc.getString(1);
				}

				rsltrelc.close();
				pstmrelc.close();
				if (!modo.equals("3")) {
					String vExpression = node.getString("EXPRESSION");
					if (!vExpression.equals(viewText)) {
						dml = "DROP VIEW \"" + node.getString("OBJECTNAME")
								+ "\"";

						try {
							executeDDL(dml, node.getString("SCHEMA"));
						} catch (Exception e) {
							// ignore
						}

						dml = "CREATE VIEW \"" + node.getString("OBJECTNAME")
								+ "\" AS \n" + vExpression;
						executeDDL(dml, node.getString("SCHEMA"));

						pstmrelc = cn
								.prepareStatement("SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE upper(TABLE_NAME)=? AND TABLE_SCHEMA=user");
						pstmrelc.setString(1, node.getString("OBJECTNAME"));
						rsltrelc = pstmrelc.executeQuery();
						if (rsltrelc.next()) {
							node.updateString("EXPRESSION", rsltrelc
									.getString(1));
							node.updateRow();
						}
						rsltrelc.close();
						pstmrelc.close();
					}
				} else {
					if (exists) {
						dml = "DROP VIEW " + node.getString("OBJECTNAME");
						executeDDL(dml, node.getString("SCHEMA"));

						CallableStatement call = cndef
								.prepareCall("DELETE FROM NGTDIC WHERE TABLENAME=?");
						call.setString(1, node.getString("OBJECTNAME"));
						call.executeUpdate();
						call.close();
					}

					// this.checkVtblFields(node.getString("TABLENAME"),node.getString("SCHEMA"),null,modo,true);
				}
			}

			if (objecttype.startsWith("PCK")) {
				String templatestr = node.getString("EXPRESSION");
				String bstr = "/*begin_package*/";
				String estr = "/*end_package*/";

				if ("02".indexOf(modo) != -1) {
					if (templatestr.indexOf(bstr) != -1) {
						int defpos;
						dml = templatestr.substring(templatestr.indexOf(bstr),
								defpos = templatestr.indexOf(estr));
						dml = "create or replace package "
								+ node.getString("OBJECTNAME") + " is \n" + dml
								+ "end " + node.getString("OBJECTNAME") + ";\n";
						executeDDL(dml, node.getString("SCHEMA"));

						bstr = "/*begin_package_body*/";
						estr = "/*end_package_body*/";

						if (templatestr.indexOf(bstr, defpos) != -1) {
							dml = templatestr.substring(templatestr.indexOf(
									bstr, defpos), templatestr.indexOf(estr,
									defpos));
							dml = "create or replace package body "
									+ node.getString("OBJECTNAME") + " is \n"
									+ dml + "end "
									+ node.getString("OBJECTNAME") + ";\n";
							executeDDL(dml, node.getString("SCHEMA"));
						}
					} else {
					}
				}
			}

			if (objecttype.startsWith("PK") || objecttype.startsWith("UN")) {
				PreparedStatement pstm = cn
						.prepareStatement("select COLUMN_NAME from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where upper(table_name)=? and upper(constraint_name) =? "
								+ "AND TABLE_SCHEMA=user "
								+ "ORDER BY ORDINAL_POSITION");
				pstm.setString(1, node.getString("TABLENAME"));
				pstm.setString(2, node.getString("OBJECTNAME"));

				boolean isunique = objecttype.startsWith("UN");
				ResultSet rslt = pstm.executeQuery();

				// rslt.next();
				boolean exists = false;
				StringBuffer expression = new StringBuffer();

				while (rslt.next()) {
					if (exists) {
						expression.append(",");
					}

					exists = true;
					expression.append(rslt.getString(1));
				}

				String[] tmpStr = node.getString("EXPRESSION").split(",");
				String dicExpr = "";
				for (int k = 0; k < tmpStr.length; k++) {
					if (k > 0)
						dicExpr += ",";

					dicExpr += tmpStr[k].trim();
				}

				boolean diff = !expression.toString().equals(dicExpr);
				rslt.close();
				pstm.close();

				if ((modo.equals("3") || diff) && exists) /*
														 * || (exists &&
														 * !modo.equals("3"))
														 */
				{

					PreparedStatement pstmrefs = cn
							.prepareStatement("select constraint_name,table_name from INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE upper(constraint_name)=? AND TABLE_SCHEMA=user");
					pstmrefs.setString(1, node.getString("OBJECTNAME"));

					ResultSet rsltrefs = pstmrefs.executeQuery();

					while (rsltrefs.next()) {
						// Exists Foreign key's referenced by this primary key..
						// droping foreign keys
						PreparedStatement pstmdelref = cndef
								.prepareStatement("DELETE FROM NGTDIC WHERE OBJECTNAME=? AND \"SCHEMA\"=? AND TABLENAME=? AND OBJECTTYPE='FK'");
						pstmdelref.setString(1, rsltrefs.getString(1));
						pstmdelref.setString(2, node.getString("SCHEMA"));
						pstmdelref.setString(3, rsltrefs.getString(2));
						pstmdelref.executeUpdate();
						pstmdelref.close();
						executeDDL("alter table " + rsltrefs.getString(2)
								+ "  drop constraint " + rsltrefs.getString(1),
								node.getString("SCHEMA"));
					}

					rsltrefs.close();
					pstmrefs.close();

					String insql = "'"
							+ node.getString("EXPRESSION").toUpperCase()
									.replaceAll(",", "\\',\\'") + "'";

					pstmrefs = cn
							.prepareStatement("select constraint_name from INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA=user AND upper(table_name)=? and column_name in ("
									+ insql + ")");
					pstmrefs.setString(1, node.getString("TABLENAME"));
					rsltrefs = pstmrefs.executeQuery();

					while (rsltrefs.next()) {
						PreparedStatement pstmdelref = cndef
								.prepareStatement("DELETE NGTDIC WHERE OBJECTNAME=? AND \"SCHEMA\"=? AND TABLENAME=? AND OBJECTTYPE='FK'");
						pstmdelref.setString(1, rsltrefs.getString(1));
						pstmdelref.setString(2, node.getString("SCHEMA"));
						pstmdelref.setString(3, node.getString("TABLENAME"));
						pstmdelref.executeUpdate();
						pstmdelref.close();
						executeDDL("alter table " + node.getString("TABLENAME")
								+ " drop constraint " + rsltrefs.getString(1),
								node.getString("SCHEMA"));
					}

					rsltrefs.close();
					pstmrefs.close();

					if (exists && diff) {
						dml = "alter table " + node.getString("TABLENAME")
								+ " drop constraint "
								+ node.getString("OBJECTNAME");

						try {
							executeDDL(dml, node.getString("SCHEMA"));
						} catch (Exception e) {
							logger.warn(LoggerMessageLocalizer.getMessage("ERROR_EXCUTING_DDL")+" (" + dml + ") "
									+ e.getMessage());
						}
					}
				}

				if (!modo.equals("3") && (!exists || diff)) {
					if (isunique) {
						dml = "alter table " + node.getString("TABLENAME")
								+ " add constraint "
								+ node.getString("OBJECTNAME") + " unique ("
								+ node.getString("EXPRESSION") + ")";
					} else {

						String exp = node.getString("EXPRESSION");

						String[] cols = exp.split(",");
						makeColumnsNotNull(cndef, cols, node.getString("TABLENAME"),node.getString("SCHEMA"));

						dml = "alter table " + node.getString("TABLENAME")
								+ " add primary key ("
								+ node.getString("EXPRESSION") + ")";
					}

					executeDDL(dml, node.getString("SCHEMA"));
				}
			}

			if (objecttype.startsWith("FK")) {

				PreparedStatement pstm = cn
						.prepareStatement("select column_name from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where TABLE_SCHEMA=user AND upper(constraint_name)=? and upper(table_name)=? order by ordinal_position");
				pstm.setString(1, node.getString("OBJECTNAME"));
				pstm.setString(2, node.getString("TABLENAME"));
				// pstm.setString(3, node.getString("TABLEREFERENCE"));

				ResultSet rslt = pstm.executeQuery();
				boolean exists = false;
				String cExpress = "";
				String express = node.getString("EXPRESSION");
				if (rslt.next()) {
					exists = true;
					if (cExpress.length() > 0)
						cExpress += ",";
					cExpress += rslt.getString(1);
				}
				rslt.close();
				pstm.close();

				if (exists && !express.equals(cExpress)) {
					dml = "alter table " + node.getString("TABLENAME")
							+ " drop constraint "
							+ node.getString("OBJECTNAME");
					executeDDL(dml, node.getString("SCHEMA"));
				}

				if (!modo.equals("3") && (!exists || !express.equals(cExpress))) {

					dml = "alter table " + node.getString("TABLENAME")
							+ " add constraint " + node.getString("OBJECTNAME")
							+ " foreign key (" + node.getString("EXPRESSION")
							+ ") references "
							+ node.getString("TABLEREFERENCE") + "(BOUI)";
					executeDDL(dml, node.getString("SCHEMA"));
				}
			}

			if (objecttype.startsWith("IDX")) {
				// boolean unflag = objecttype.startsWith("UN");
				boolean unflag = false;
				PreparedStatement pstm = cn
						.prepareStatement("select is_unique from SYS.INDEXES where upper(NAME)=? ");
				pstm.setString(1, node.getString("OBJECTNAME"));

				ResultSet rslt = pstm.executeQuery();
				boolean drop = false;
				boolean exists = false;
				boolean dbunflag = false;
				String oldexpression = "";
				String newexpression = "";

				if (rslt.next()) {
					exists = true;

					if ((unflag && !(dbunflag = (rslt.getInt(1) == 0)))) {
						drop = true;
					}

					rslt.close();
					pstm.close();

					String indSql = "select col.name coluna from sys.indexes ind "
							+ "inner join sys.index_columns ic on ind.object_id = ic.object_id and ind.index_id = ic.index_id "
							+ "inner join sys.columns col on ic.object_id = col.object_id and ic.column_id = col.column_id "
							+ "where upper(ind.name) = ?";

					pstm = cn.prepareStatement(indSql);
					pstm.setString(1, node.getString("OBJECTNAME"));

					rslt = pstm.executeQuery();

					while (rslt.next()) {
						oldexpression += (((oldexpression.length() > 0) ? ","
								: "") + rslt.getString(1));
					}

					rslt.close();
					pstm.close();
				} else {
					rslt.close();
					pstm.close();
				}

				// Vector nexo = tools.Split(node.getString("EXPRESSION"), ",");
				String aux = node.getString("EXPRESSION");
				String[] nexo;
				if (aux != null) {
					nexo = node.getString("EXPRESSION").split(",");
				} else {
					nexo = new String[0];
				}
				for (byte i = 0; i < nexo.length; i++) {
					newexpression += (((newexpression.length() > 0) ? "," : "") + ((nexo[i])
							.toUpperCase().trim()));
				}

				if (!drop) {
					drop = (!newexpression.equals(oldexpression));
				}

				if (exists && (drop || modo.equals("3"))) {
					if (!dbunflag) {
						dml = "ALTER TABLE " + node.getString("TABLENAME")
								+ " DROP INDEX " + node.getString("OBJECTNAME");
					} else {
						dml = "ALTER TABLE " + node.getString("TABLENAME")
								+ " DROP CONSTRAINT "
								+ node.getString("OBJECTNAME");
					}

					executeDDL(dml, node.getString("SCHEMA"));
					exists = false;
				}

				if (!exists && !modo.equals("3") && !"".equals(newexpression)) {
					if (!unflag) {
						dml = "CREATE INDEX " + node.getString("OBJECTNAME")
								+ " ON " + node.getString("TABLENAME") + "("
								+ newexpression + ")";
					} else {
						dml = "ALTER TABLE " + node.getString("TABLENAME")
								+ " ADD CONSTRAINT "
								+ node.getString("OBJECTNAME") + " UNIQUE ("
								+ newexpression + ")";
					}

					executeDDL(dml, node.getString("SCHEMA"));
				}
			}

			updateDictionaryTable(node, modo);

			return dml;
		} catch (SQLException e) {
			throw (e);
		} finally {
		}
	}

	private void makeColumnsNotNull(Connection cn,String[] cols, String tablename,String schema) throws SQLException
	{
		String dml="";
		PreparedStatement pstmcol=null;
		ResultSet rs=null;
		try
		{
			for (int i = 0; i < cols.length; i++) {
	
				if ("BOUI".equalsIgnoreCase(cols[i])
						|| cols[i].endsWith("$")) {
					dml = "alter table "
							+ tablename
							+ " alter column " + cols[i]
							+ " bigint not null; ";
				} else {
					pstmcol = cn
					.prepareStatement("select COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE from INFORMATION_SCHEMA.COLUMNS "
							+ "where  TABLE_SCHEMA=user AND upper(TABLE_NAME)=? and upper(COLUMN_NAME)=?");
					
					pstmcol.setString(1, tablename);
					pstmcol.setString(2, cols[i]);
					rs=pstmcol.executeQuery();
					
					String coltype="float";
					String colsize="";
					if (rs.next())
					{
						coltype=rs.getString(2);
						colsize=rs.getString(3); 
						if (colsize!=null && !colsize.equals("") && !colsize.toUpperCase().equals("NULL"))
							coltype+="("+colsize+")";
					}
					rs.close();
					pstmcol.close();
					dml = "alter table "
							+ tablename
							+ " alter column " + cols[i]
							+ " "+coltype+" not null; ";
				}
				executeDDL(dml, schema);
			}
		}
		finally
		{
			if (pstmcol!=null) pstmcol.close();
		}
	}
	
	private void updateDictionaryTable(ResultSet node, String modo) {
		try {
			String repName = p_ctx.getBoSession().getRepository().getName();
			Connection cn = getRepositoryConnection(p_ctx.getApplication(),
					repName, 1);
			final String static_query = " WHERE \"SCHEMA\"=? AND TABLENAME=? AND OBJECTNAME=? AND OBJECTTYPE=?  ";
			PreparedStatement cntpstm = cn
					.prepareStatement("SELECT COUNT(*) FROM NGTDIC "
							+ static_query);
			cntpstm.setString(1, node.getString("SCHEMA"));
			cntpstm.setString(2, node.getString("TABLENAME"));
			cntpstm.setString(3, node.getString("OBJECTNAME"));
			cntpstm.setString(4, node.getString("OBJECTTYPE"));

			ResultSet cntrslt = cntpstm.executeQuery();
			cntrslt.next();

			if ((cntrslt.getLong(1) > 0) && modo.equals("2")) // Update ngt_dic
			{
				ResultSetMetaData meta = node.getMetaData();

				if (p_activeUpdatePstm == null) {
					StringBuffer insSql1 = new StringBuffer(" UPDATE NGTDIC ");
					insSql1.append(" SET ");

					int cc = meta.getColumnCount();
					int i = 0;

					for (i = 0; i < (cc - 1); i++) {
						insSql1.append(' ').append("\"").append(
								meta.getColumnName(i + 1)).append("\"").append(
								' ').append("=?,");
					}

					insSql1.append(' ').append("\"").append(
							meta.getColumnName(i + 1)).append("\"").append(' ')
							.append("=?");

					insSql1.append(static_query);
					p_activeUpdatePstm = cn
							.prepareStatement(insSql1.toString());
				}

				int colidx;
				int pstmpos = 0;
				ArrayList clobs = null;
				short i;

				for (i = 0; i < meta.getColumnCount(); i++) {
					pstmpos++;
					colidx = i + 1;

					Object value;

					if (((value = node.getObject(colidx)) == null)
							|| !(meta.getColumnClassName(colidx).equals(
									"netgest.bo.data.DataClob") && (node
									.getObject(colidx).toString().length() > 3000))) {
						if (value != null) {
							if (meta.getColumnClassName(colidx).equals(
									"netgest.bo.data.DataClob")) {
								p_activeUpdatePstm.setString(pstmpos, value
										.toString());
							} else {
								p_activeUpdatePstm.setObject(pstmpos, value);
							}
						} else {
							p_activeUpdatePstm.setNull(pstmpos, meta
									.getColumnType(colidx));
						}
					} else {
						if (clobs == null) {
							clobs = new ArrayList();
						}

						clobs.add(new Integer(colidx));
						p_activeUpdatePstm.setString(pstmpos, " ");
					}
				}

				p_activeUpdatePstm.setString(i + 1, node.getString("SCHEMA"));
				p_activeUpdatePstm
						.setString(i + 2, node.getString("TABLENAME"));
				p_activeUpdatePstm.setString(i + 3, node
						.getString("OBJECTNAME"));
				p_activeUpdatePstm.setString(i + 4, node
						.getString("OBJECTTYPE"));

				p_activeUpdatePstm.executeUpdate();

				if ((clobs != null) && (clobs.size() > 0)) {
					updateClobs(cn, node, clobs);
				}
			} else if ((cntrslt.getLong(1) == 0) && !modo.equals("3")) // Insert
			// record
			{
				ResultSetMetaData meta = node.getMetaData();

				if (p_activeInsertPstm == null) {
					StringBuffer insSql1 = new StringBuffer(
							"INSERT INTO NGTDIC (");
					StringBuffer insSql2 = new StringBuffer(" VALUES ( ");
					int cc = meta.getColumnCount();
					int i = 0;

					for (i = 0; i < (cc - 1); i++) {
						insSql1.append(' ').append("\"").append(
								meta.getColumnName(i + 1)).append("\"").append(
								' ').append(',');
						insSql2.append('?').append(',');
					}

					insSql1.append(' ').append("\"").append(
							meta.getColumnName(i + 1)).append("\"").append(' ')
							.append(')');
					insSql2.append('?').append(')');

					insSql1.append(insSql2);
					p_activeInsertPstm = cn
							.prepareStatement(insSql1.toString());
				}

				int pstmpos = 0;

				int colidx;

				ArrayList clobs = null;
				short i;

				for (i = 0; i < meta.getColumnCount(); i++) {
					pstmpos++;
					colidx = node.findColumn(meta.getColumnName(i + 1));

					Object value;

					if (((value = node.getObject(colidx)) == null)
							|| !(meta.getColumnClassName(colidx).equals(
									"netgest.bo.data.DataClob") && (node
									.getObject(colidx).toString().length() > 3000))) {
						if (value != null) {
							if (meta.getColumnClassName(colidx).equals(
									"netgest.bo.data.DataClob")) {
								p_activeInsertPstm.setString(pstmpos, value
										.toString());
							} else {
								p_activeInsertPstm.setObject(pstmpos, value,
										meta.getColumnType(colidx));
							}
						} else {
							p_activeInsertPstm.setNull(pstmpos, meta
									.getColumnType(colidx));
						}
					} else {
						if (clobs == null) {
							clobs = new ArrayList();
						}

						clobs.add(new Integer(colidx));
						p_activeInsertPstm.setString(pstmpos, " ");
					}
				}

				p_activeInsertPstm.executeUpdate();

				if ((clobs != null) && (clobs.size() > 0)) {
					updateClobs(cn, node, clobs);
				}
			} else if ((cntrslt.getLong(1) > 0) && modo.equals("3")) {
				if (p_activeDeletePstm == null) {
					StringBuffer insSql1 = new StringBuffer(
							" DELETE FROM NGTDIC ");
					insSql1.append(static_query);
					p_activeDeletePstm = cn
							.prepareStatement(insSql1.toString());
					p_activeDeletePstm.setString(1, node.getString("SCHEMA"));
					p_activeDeletePstm
							.setString(2, node.getString("TABLENAME"));
					p_activeDeletePstm.setString(3, node
							.getString("OBJECTNAME"));
					p_activeDeletePstm.setString(4, node
							.getString("OBJECTTYPE"));
					p_activeDeletePstm.execute();
				}
			}

			cntrslt.close();
			cntpstm.close();
			cn.commit();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(MessageLocalizer.getMessage("ERROR_UPDATING_NGTDIC")+" : "
					+ e.getClass().getName() + "\n" + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(MessageLocalizer.getMessage("ERROR_UPDATING_NGTDIC")+" : "
					+ e.getClass().getName() + "\n" + e.getMessage());
		}
	}

	private void updateClobs(Connection cn, ResultSet node, ArrayList clobs)
			throws SQLException, IOException {
		final String static_query = " WHERE \"SCHEMA\"=? AND TABLENAME=? AND OBJECTNAME=? AND OBJECTTYPE=?  ";
		StringBuffer sb = new StringBuffer("SELECT ");

		for (short i = 0; i < clobs.size(); i++) {
			if (i > 0) {
				sb.append(',');
			}

			sb.append("\"").append(
					node.getMetaData().getColumnName(
							((Integer) clobs.get(i)).intValue())).append("\"");
		}

		sb.append(" FROM NGTDIC ").append(static_query);

		PreparedStatement clobpstm = cn.prepareStatement(sb.toString());

		clobpstm.setString(1, node.getString("SCHEMA"));
		clobpstm.setString(2, node.getString("TABLENAME"));
		clobpstm.setString(3, node.getString("OBJECTNAME"));
		clobpstm.setString(4, node.getString("OBJECTTYPE"));

		ResultSet clobrslt = clobpstm.executeQuery();

		if (clobrslt.next()) {
			for (byte y = 0; y < clobs.size(); y++) {
				Clob clob = clobrslt.getClob(y + 1);
				clob.truncate(0);
				String strclob = node.getString(((Integer) clobs.get(y))
						.intValue());
				clob.setString(1, strclob);
				// clob.free();
				// CLOB clob = (CLOB) clobrslt.getClob(y + 1);
				// clob.open(CLOB.MODE_READWRITE);
				//
				// Writer wr = clob.getCharacterOutputStream();
				// String strclob = node.getString(((Integer)
				// clobs.get(y)).intValue());
				// int chunksize = clob.getChunkSize();
				// int offset = 0;
				// int endoffset;
				// int chunks = 1;
				//
				// while (offset < strclob.length())
				// {
				// endoffset = Math.min(chunksize * chunks, strclob.length());
				// chunks++;
				// wr.write(strclob.substring(offset, endoffset));
				// offset = endoffset;
				// }
				//
				// wr.flush();
				// clob.close();
			}

			clobrslt.close();
			clobpstm.close();
		}
	}

	public static boolean loggerInitialized = false;
	public static boolean loggerError = false;

	public void executeDDL(String dml, String schema) throws SQLException {
		dml += "\n";

		String str2WorkAround = "alter table EBO_REFERENCES add constraint FK_EBO_REFERENCES_BOUI foreign key (REFBOUI$) references EBO_REGISTRY(BOUI)";

		// martelada para criar a fk correctamente, se nÃ£o o sqlserver n cria fk
		// para a tabela EBO_REFERENCES
		// referente Ã  EBO_REGISTRY
		if (str2WorkAround.equals(dml)) {
			dml = "alter table EBO_REFERENCES add constraint FK_EBO_REFERENCES_BOUI foreign key (REFBOUI$) references EBO_REGISTRY(UI$)";
		}

		CallableStatement csm = null;
		boolean hasError = false;

		if ((dml != null) && (dml.length() != 0)) {
			// String schemaName =
			// p_ctx.getBoSession().getRepository().getName();
			Connection cn = null;
			try {
				cn = getRepositoryConnection(p_ctx.getApplication(), schema, 1);
				csm = cn.prepareCall(dml);
				csm.execute();
				log(dml, "OK");
			} catch (SQLException e) {
				hasError = true;
				log(dml, e.getClass().getName() + ":" + e.getErrorCode()
						+ " - " + e.getMessage());
				int error = e.getErrorCode();
				if (e.getErrorCode() == 2261) {
					// ignora: tentou colocar uma unique key que jÃ¡ existe com
					// nome diferente
				} else {
					throw new SQLException(MessageLocalizer.getMessage("ERROR_EXECUTING_DDL")+":"
							+ e.getMessage() + "\n"+MessageLocalizer.getMessage("DDL_STATEMENT_WAS")+":\n" + dml);
				}
			} catch (Exception e) {
				hasError = true;
				throw new SQLException(MessageLocalizer.getMessage("ERROR_EXECUTING_DDL")+":" + e.getMessage()
						+ "\n"+MessageLocalizer.getMessage("DDL_STATEMENT_WAS")+":\n" + dml);
			} finally {
				if (csm != null)
					csm.close();

				if (cn != null) {
					if (!hasError) {
						cn.commit();
					} else {
						cn.rollback();
					}
				}

			}

		}
	}

	public void checkDicFields(String tablename, String schema,
			String[] fields, String[] fieldtype, String[] fieldsize,
			String[] fielddefault, String[] fielddesc) throws SQLException {
		checkDicFields(tablename, schema, fields, fieldtype, fieldsize,
				fielddefault, fielddesc, this.getRepositoryConnection(p_ctx
						.getApplication(), schema, 2), this
						.getRepositoryConnection(p_ctx.getApplication(),
								schema, 1));
	}

	public static void checkDicFields(String tablename, String schema,
			String[] fields, String[] fieldtype, String[] fieldsize,
			String[] fielddefault, String[] fielddesc, Connection cndef,
			Connection cn) throws SQLException {
		try {
			PreparedStatement pstmdicf = cndef
					.prepareStatement("select \"SCHEMA\",objectname,objecttype,fieldtype,fieldsize,tablename from NGTDIC where "
							+ "tablename = ? and objecttype = 'F' and objectname = ? and \"SCHEMA\"=?");

			for (int i = 0; i < fields.length; i++) {
				String fname = fields[i];
				String ftype = fieldtype[i];
				String fsize = ((fieldsize == null) || (fieldsize[i] == null)) ? ""
						: fieldsize[i];
				String fdesc = ((fielddesc == null) || (fielddesc[i] == null)) ? ""
						: fielddesc[i];
				String fddef = ((fielddefault == null) || (fielddefault[i] == null)) ? ""
						: fielddefault[i];

				pstmdicf.setString(1, tablename);
				pstmdicf.setString(2, fname);
				pstmdicf.setString(3, schema);

				ResultSet rsltf = pstmdicf.executeQuery();

				if (rsltf.next()) {
					PreparedStatement pstm = cndef
							.prepareStatement("UPDATE NGTDIC SET FIELDTYPE=?, FIELDSIZE=? WHERE  \"SCHEMA\"=? AND OBJECTTYPE='F' AND OBJECTNAME=? AND TABLENAME=?");
					pstm.setString(1, ftype);
					pstm.setString(2, fsize);
					pstm.setString(3, schema);
					pstm.setString(4, fname);
					pstm.setString(5, tablename);
					pstm.executeUpdate();
					pstm.close();
				} else {
					PreparedStatement pstm = cndef
							.prepareStatement("INSERT INTO NGTDIC (\"SCHEMA\",OBJECTNAME,OBJECTTYPE,FIELDTYPE,FIELDSIZE,TABLENAME,DEFAULTVALUE,FRIENDLYNAME) VALUES (?,?,'F',?,?,?,?,?) ");
					pstm.setString(1, schema);
					pstm.setString(2, fname);
					pstm.setString(3, ftype);
					pstm.setString(4, fsize);
					pstm.setString(5, tablename);
					pstm.setString(6, fddef);
					pstm.setString(7, fddef);
					pstm.executeUpdate();
					pstm.close();
				}

				rsltf.close();
			}

			pstmdicf.close();
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		} finally {
			cndef.commit();
		}
	}

	// public void RegisterViewFields(String viewname, String schema)
	// throws SQLException
	// {
	// Connection cn = null;
	// Connection cndef = null;
	//
	// try
	// {
	// String prefix = "";
	//
	// if (schema.equals("DEF"))
	// {
	// prefix = "NGD_";
	// }
	// else if (schema.equals("SYS"))
	// {
	// prefix = "SYS_";
	// }
	//
	// String vt = prefix + viewname;
	// cndef = this.getRepositoryConnection(p_ctx.getApplication(),
	// schema, 2);
	//
	// cn = this.getRepositoryConnection(p_ctx.getApplication(), schema, 1);
	//
	// PreparedStatement pstm2 = cndef.prepareStatement(
	// "DELETE FROM NGTDIC WHERE TABLENAME=? and objecttype='F'");
	// pstm2.setString(1, viewname);
	// pstm2.executeUpdate();
	// pstm2.close();
	//
	// PreparedStatement pstm = cn.prepareStatement(
	// "select COLUMN_NAME,data_type,DATA_LENGTH,DATA_PRECISION,DATA_SCALE from user_tab_columns where table_name=?");
	// pstm.setString(1, viewname);
	//
	// ResultSet rslt = pstm.executeQuery();
	//
	// while (rslt.next())
	// {
	// String fname = rslt.getString(1);
	// String fnamesql = "\"" + fname + "\"";
	//
	// if (!rslt.getString(2).equals("UNDEFINED"))
	// {
	// String ftype = DataUtils.getNgtFieldTypeFromDDL(rslt.getString(
	// 2));
	// String fldsize = ftype.equals("N")
	// ? (rslt.getString(4) +
	// ((rslt.getInt(5) > 0) ? ("," + rslt.getInt(5)) : ""))
	// : rslt.getString(3);
	//
	// if (ftype.equals("D"))
	// {
	// fldsize = "10";
	// }
	//
	// pstm2 = cndef.prepareStatement(
	// "INSERT INTO NGTDIC (SCHEMA,OBJECTNAME,OBJECTTYPE,FIELDTYPE,FIELDSIZE,TABLENAME) VALUES ("
	// +
	// "?,?,?,?,?,?)");
	// pstm2.setString(1, schema);
	// pstm2.setString(2, fname);
	// pstm2.setString(3, "F");
	// pstm2.setString(4, ftype);
	// pstm2.setString(5, fldsize);
	// pstm2.setString(6, viewname);
	// pstm2.executeUpdate();
	// pstm2.close();
	// }
	// }
	//
	// rslt.close();
	// pstm.close();
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// throw new SQLException(e.getMessage());
	// }
	// finally
	// {
	// }
	// }

	public boolean existsSchema(String newSchemaName) throws SQLException {
		Connection cndef = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			cndef = this.getRepositoryConnection(p_ctx.getApplication(),
					"default", 2);

			pstm = cndef
					.prepareStatement("SELECT 1 FROM NGTDIC WHERE TABLENAME=? and objecttype='S'");
			pstm.setString(1, newSchemaName.toUpperCase());
			rs = pstm.executeQuery();

			if (rs.next()) {
				return true;
			}

			return false;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					// ignore
				}
			}

			if (pstm != null) {
				try {
					pstm.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	public void registerSchema(String newSchemaName, String objectControlller,
			long boui, String expression, String schema) throws SQLException {
		Connection cndef = null;
		PreparedStatement pstm = null;

		try {
			cndef = this.getRepositoryConnection(p_ctx.getApplication(),
					"default", 2); // this.getSchemaConnection("DEF");

			String friendlyName = "Schema created by object ["
					+ objectControlller + "] with boui [" + boui + "]";

			pstm = cndef
					.prepareStatement("DELETE FROM NGTDIC WHERE TABLENAME=? and objecttype='S'");
			pstm.setString(1, newSchemaName);
			pstm.executeUpdate();
			pstm.close();

			pstm = cndef
					.prepareStatement("INSERT INTO NGTDIC (\"SCHEMA\",OBJECTNAME,OBJECTTYPE,TABLENAME, "
							+ "FRIENDLYNAME, EXPRESSION) VALUES ("
							+ "?,?,?,?,?,?)");
			pstm.setString(1, schema);
			pstm.setString(2, newSchemaName);
			pstm.setString(3, "S");
			pstm.setString(4, newSchemaName);
			pstm.setString(5, friendlyName);
			pstm.setString(6, expression);
			pstm.executeUpdate();
			pstm.close();
			cndef.commit();
		} catch (Exception e) {
			cndef.rollback();
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} finally {
			if (pstm != null) {
				try {
					pstm.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	public static boolean existsSchema(EboContext p_eboctx, String schemaName)
			throws boRuntimeException {
		Connection cn = null;
		boolean ret = false;
		PreparedStatement pstm = null;
		ResultSet rslt = null;

		try {
			cn = p_eboctx.getDedicatedConnectionData();

			pstm = cn
					.prepareStatement("SELECT 1 from ALL_USERS WHERE USERNAME=?");
			pstm.setString(1, schemaName.toUpperCase());
			rslt = pstm.executeQuery();

			if (rslt.next()) {
				return true;
			}

			rslt.close();
			pstm.close();
		} catch (Exception e) {
			throw new boRuntimeException("boBuildDB.existsSchema", "BO-1304", e);
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (rslt != null) {
					rslt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			try {
				if (cn != null) {
					cn.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}

		return ret;
	}

	public static boolean renameTable(EboContext p_eboctx, String srcTableName,
			String destTableName) throws boRuntimeException {
		srcTableName = srcTableName.toUpperCase();
		destTableName = destTableName.toUpperCase();

		Connection cn = null;
		Connection cndef = null;
		boolean ret = false;

		try {
			boolean srcexists = false;
			boolean destexists = false;
			final InitialContext ic = new InitialContext();

			cndef = p_eboctx.getConnectionDef(); // ((DataSource)ic.lookup(p_eboctx.getSysUser().getConnectionStringdef()+"_nojta")).getConnection();

			PreparedStatement pstm = cn
					.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE upper(TABLE_NAME)=? AND TABLE_SCHEMA=user");
			pstm.setString(1, srcTableName);

			ResultSet rslt = pstm.executeQuery();

			if (rslt.next()) {
				srcexists = true;
			}

			rslt.close();
			pstm.setString(1, destTableName);
			rslt = pstm.executeQuery();

			if (rslt.next()) {
				destexists = true;
			}

			if (!destexists && srcexists) {
				logger.finest(LoggerMessageLocalizer.getMessage("RENAMING_TABLE_FROM")+" [" + srcTableName + "] "+LoggerMessageLocalizer.getMessage("TO")+" ["
						+ destTableName + "]");

				CallableStatement cstm = cn.prepareCall("ALTER TABLE "
						+ srcTableName + " RENAME TO " + destTableName);
				cstm.execute();
				cstm.close();
				logger.finest(LoggerMessageLocalizer.getMessage("UPDATING_NGTDIC"));
				SqlServerDBM.createDictionaryFromTable(
						new String[] { destTableName }, "DATA", cndef, cn);
				logger.finest(LoggerMessageLocalizer.getMessage("DONE_RENAMING"));
			} else {
				logger.finest(LoggerMessageLocalizer.getMessage("CANNOT_RENAME_TABLE")+" [" + srcTableName + "] "+LoggerMessageLocalizer.getMessage("TO")+" ["
						+ destTableName
						+ "] "+LoggerMessageLocalizer.getMessage("BECAUSE_ONE_OF_THEM_DOES_NOT_EXIST"));
			}
		} catch (Exception e) {
			try {
				cn.rollback();
			} catch (Exception z) {
				throw new boRuntimeException("boBuildDB.moveTable", "BO-1304",
						z);
			}

			throw new boRuntimeException("boBuildDB.moveTable", "BO-1304", e);
		} finally {
			try {
				cn.close();
			} catch (Exception e) {
			}

			try {
				cndef.close();
			} catch (Exception e) {
			}
		}

		return ret;
	}

	public static boolean tableExistsAndHaveQuery(EboContext p_eboctx,
			String tablename, String query) throws boRuntimeException {
		boolean ret = false;

		try {
			Connection cn = p_eboctx.getConnectionData();
			PreparedStatement pstm = cn
					.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE upper(TABLE_NAME)=? AND TABLE_SCHEMA=user");
			pstm.setString(1, tablename);

			ResultSet rslt = pstm.executeQuery();

			if (rslt.next()) {
				ret = true;
			}

			if (ret) {
				if ((query != null) && (query.length() > 0)) {
					PreparedStatement pstm2 = cn
							.prepareStatement("SELECT COUNT(*) FROM "
									+ tablename + " WHERE " + query);
					ResultSet rslt2 = pstm2.executeQuery();

					while (rslt2.next()) {
						ret = rslt2.getLong(1) > 0;
					}

					rslt2.close();
					pstm2.close();
				}
			}

			rslt.close();
			pstm.close();
		} catch (Exception e) {
			throw new boRuntimeException("dbmagf.tableExists", "BO-1304", e);
		} finally {
		}

		return ret;
	}

	public static boolean renameTableToBackupName(EboContext p_eboctx,
			String srcTableName) throws boRuntimeException {
		Connection cn = null;
		boolean ret = false;

		try {
			if (tableExistsAndHaveQuery(p_eboctx, srcTableName, null)) {
				int sec = 0;
				String newtable = boBuildDB.encodeObjectName("BK"
						+ StringUtils.padl(0 + "", 2, "0")
						+ srcTableName.toUpperCase());

				while (tableExistsAndHaveQuery(p_eboctx, newtable, null)
						&& (sec < 100)) {
					newtable = boBuildDB.encodeObjectName("BK"
							+ StringUtils.padl(sec + "", 2, "0")
							+ srcTableName.toUpperCase());
					sec++;

					if (sec == 100) {
						throw new RuntimeException(MessageLocalizer.getMessage("PLEASE_DELETE_BACKLUPO_FILES_CANNOT_CREATE_NEW"));
					}
				}

				logger.finest("---  "+LoggerMessageLocalizer.getMessage("RENAMING_TABLE_FROM")+"  [" + srcTableName
						+ "] "+LoggerMessageLocalizer.getMessage("TO")+" [" + newtable + "]");
				ret = renameTable(p_eboctx, srcTableName, newtable);
				logger.finest("---  "+LoggerMessageLocalizer.getMessage("END_RENAMING_TABLE_FROM")+" [" + srcTableName
						+ "] "+LoggerMessageLocalizer.getMessage("TO")+" [" + newtable + "]");
			}
		} catch (boRuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new boRuntimeException("boBuildDB.moveTable", "BO-1304", e);
		} finally {
			try {
				cn.close();
			} catch (Exception e) {
			}
		}

		return ret;
	}

	public static boolean createBackupTable(EboContext p_eboctx,
			String srcTableName) throws boRuntimeException {
		Connection cn = null;
		boolean ret = false;

		try {
			if (tableExistsAndHaveQuery(p_eboctx, srcTableName, null)) {
				int sec = 0;
				String newtable = boBuildDB.encodeObjectName("BK"
						+ StringUtils.padl(sec + "", 2, "0")
						+ srcTableName.toUpperCase());

				while (tableExistsAndHaveQuery(p_eboctx, newtable, null)
						&& (sec < 100)) {
					sec++;

					if (sec == 100) {
						throw new RuntimeException(MessageLocalizer.getMessage("PLEASE_DELETE_BACKLUPO_FILES_CANNOT_CREATE_NEW"));
					}

					newtable = boBuildDB.encodeObjectName("BK"
							+ StringUtils.padl(sec + "", 2, "0")
							+ srcTableName.toUpperCase());
				}

				logger.finest("---  "+LoggerMessageLocalizer.getMessage("CREATING_A_BACKUP_OF")+" [" + srcTableName
						+ "] "+LoggerMessageLocalizer.getMessage("TO")+" [" + newtable + "]");
				ret = copyDataToNewTable(p_eboctx, srcTableName, newtable,
						null, false, 0);

				// logger.finest("---  END BACKUP OF ["+srcTableName+"] TO ["+newtable+"]");
			}
		} catch (boRuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new boRuntimeException("boBuildDB.moveTable", "BO-1304", e);
		} finally {
			try {
				cn.close();
			} catch (Exception e) {
			}
		}

		return ret;
	}

	public static boolean copyDataToNewTable(EboContext p_eboctx,
			String srcTableName, String destTableName, String where,
			boolean log, int mode) throws boRuntimeException {
		srcTableName = srcTableName.toUpperCase();
		destTableName = destTableName.toUpperCase();

		Connection cn = null;
		Connection cndef = null;
		boolean ret = false;

		try {
			boolean srcexists = false;
			boolean destexists = false;
			final InitialContext ic = new InitialContext();

			cn = p_eboctx.getConnectionData(); // ((DataSource)ic.lookup(p_eboctx.getSysUser().getConnectionString()+"_nojta")).getConnection();
			cndef = p_eboctx.getConnectionDef(); // ((DataSource)ic.lookup(p_eboctx.getSysUser().getConnectionStringdef()+"_nojta")).getConnection();

			PreparedStatement pstm = cn
					.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE upper(TABLE_NAME)=?  AND TABLE_SCHEMA=user");
			pstm.setString(1, srcTableName);

			ResultSet rslt = pstm.executeQuery();

			if (rslt.next()) {
				srcexists = true;
			}

			rslt.close();
			pstm.setString(1, destTableName);
			rslt = pstm.executeQuery();

			if (rslt.next()) {
				destexists = true;
			}

			if (!destexists) {
				rslt.close();
				pstm.close();
				pstm = cn
						.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS WHERE upper(TABLE_NAME)=? AND TABLE_SCHEMA=user");
				pstm.setString(1, destTableName);
				rslt = pstm.executeQuery();

				if (rslt.next()) {
					CallableStatement cstm = cn.prepareCall("DROP VIEW "
							+ destTableName);
					cstm.execute();
					cstm.close();
				}
			}

			rslt.close();
			pstm.close();

			if (srcexists && !destexists) {
				if (log) {
					logger.finest(LoggerMessageLocalizer.getMessage("CREATING_AND_COPY_DATA_FROM")+" ["
							+ srcTableName + "] "+LoggerMessageLocalizer.getMessage("TO")+" [" + destTableName + "]");
				}

				CallableStatement cstm = cn
						.prepareCall("CREATE TABLE "
								+ destTableName
								+ " AS SELECT * FROM "
								+ srcTableName
								+ " "
								+ (((where != null) && (where.length() > 0)) ? (" WHERE " + where)
										: ""));
				cstm.execute();
				cstm.close();

				if (log) {
					logger.finest(LoggerMessageLocalizer.getMessage("UPDATING_NGTDIC"));
				}

				// if( log ) dbmagf.loadTableFromDB( new String[] {
				// destTableName } , "DATA", cndef , cn );
				cn.commit();
				ret = true;
			} else if (srcexists && destexists) {
				if (log) {
					logger.finest(LoggerMessageLocalizer.getMessage("COPY_DATA_FROM")+" [" + srcTableName + "] "+LoggerMessageLocalizer.getMessage("TO")+" ["
							+ destTableName + "]");
				}

				PreparedStatement pstm2 = cn
						.prepareStatement("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE upper(TABLE_NAME) = ? AND TABLE_SCHEMA=user");
				pstm2.setString(1, destTableName);

				ResultSet rslt2 = pstm2.executeQuery();
				StringBuffer fields = new StringBuffer();
				PreparedStatement pstm3 = cn
						.prepareStatement("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE upper(TABLE_NAME) = ? and upper(COLUMN_NAME)=? AND TABLE_SCHEMA=user");

				while (rslt2.next()) {
					pstm3.setString(1, srcTableName);
					pstm3.setString(2, rslt2.getString(1));

					ResultSet rslt3 = pstm3.executeQuery();

					if (rslt3.next()) {
						if (fields.length() > 0) {
							fields.append(',');
						}

						fields.append('"').append(rslt2.getString(1)).append(
								'"');
					}

					rslt3.close();
				}

				pstm3.close();
				rslt2.close();
				pstm2.close();

				// boolean srchavedata = tableExistsAndHaveQuery( p_eboctx,
				// srcTableName, where );
				// boolean desthavedata = tableExistsAndHaveQuery( p_eboctx,
				// destTableName, where );
				// if( desthavedata && srchavedata )
				// {
				// if( log )
				// logger.finest("Deleting destination table rows with query " +
				// where );
				// CallableStatement cstm =
				// cn.prepareCall("DELETE "+destTableName
				// +(where!=null&&where.length()>0?" WHERE " + where:""));
				// int recs = cstm.executeUpdate();
				// cstm.close();
				// if( log ) logger.finest("["+recs+"] deleted.");
				// }
				CallableStatement cstm;
				int recs = 0;

				if ((mode == 0) || (mode == 1)) {
					cstm = cn
							.prepareCall("INSERT INTO "
									+ destTableName
									+ "( "
									+ fields.toString()
									+ " ) ( SELECT "
									+ fields.toString()
									+ " FROM "
									+ srcTableName
									+ " "
									+ (((where != null) && (where.length() > 0)) ? (" WHERE " + where)
											: "") + ")");
					recs = cstm.executeUpdate();
					cstm.close();

					if (log) {
						logger.finest(LoggerMessageLocalizer.getMessage("DONE")+" [" + recs + "] "+LoggerMessageLocalizer.getMessage("RECORDS_COPIED"));
					}
				}

				// if( mode == 0 || mode == 2 )
				// {
				// if( log ) logger.finest("Deleting moved records");
				// cstm =
				// cn.prepareCall("delete "+srcTableName+" "+(where!=null&&where.length()>0?" WHERE "
				// + where:""));
				// recs = cstm.executeUpdate();
				// cstm.close();
				// if( log )
				// logger.finest("done. ["+recs+"] deleted from "+srcTableName);
				// }
				// if( log ) dbmagf.loadTableFromDB( new String[] {
				// destTableName } , "DATA", cndef , cn );
				cn.commit();

				ret = true;
			}
		} catch (Exception e) {
			try {
				cn.rollback();
			} catch (Exception z) {
				throw new boRuntimeException("boBuildDB.moveTable", "BO-1304",
						z);
			}

			throw new boRuntimeException("boBuildDB.moveTable", "BO-1304", e);
		} finally {
			try {
				cn.close();
			} catch (Exception e) {
			}

			try {
				cndef.close();
			} catch (Exception e) {
			}
		}

		return ret;
	}

	public static final String getNgtFieldTypeFromDDL(String oft)
			throws SQLException {
		String colType = oft.toUpperCase();
		if (colType.equals("VARCHAR") || colType.equals("CHAR"))
			return "C";
		else if (colType.equals("DATE") || colType.equals("DATETIME"))
			return "D";
		else if (colType.equals("NUMERIC"))
			return "N";
		else if (colType.equals("REAL"))
			return "N";
		else if (colType.equals("DECIMAL"))
			return "N";
		else if (colType.equals("BIGINT"))
			return "N";
		else if (colType.equals("FLOAT"))
			return "N";
		else if (colType.equals("DOUBLE"))
			return "N";
		else if (colType.equals("LONGTEXT"))
			return "CL";
		else if (colType.equals("TEXT"))
			return "CL";
		else if (colType.equals("LONGBLOB") || colType.equals("BLOB"))
			return "BL";
		else if (colType.equals("IMAGE"))
			return "BL";
		else if (colType.equals("BFILE"))
			return "BF";
		else if (colType.equals("VARBINARY") || colType.equals("MEDIUMBLOB"))
			return "RAW";
		else if (colType.startsWith("TIMESTAMP"))
			return "DATETIME";

		throw new SQLException("DBMAGF - "+MessageLocalizer.getMessage("UNKNOWN_DATA_TYPE")+" [" + oft
				+ "]");
	}

	public static String getDDLFieldFromNGT(String fieldtype, String fieldlen) {
		String ngtdatatype = null;
		if (fieldtype.equals("C"))
			ngtdatatype = "VARCHAR";
		else if (fieldtype.equals("N"))
			ngtdatatype = "FLOAT";
		else if (fieldtype.equals("D"))
			ngtdatatype = "DATETIME";
		else if (fieldtype.equals("CL"))
			ngtdatatype = "TEXT";
		else if (fieldtype.equals("BL"))
			ngtdatatype = "IMAGE";
		else if (fieldtype.equals("BF"))
			ngtdatatype = "VARBINARY";
		else if (fieldtype.equals("RAW"))
			ngtdatatype = "VARBINARY";
		else if (fieldtype.equals("TIMESTAMP"))
			ngtdatatype = "DATETIME";

		if ("N,C,RAW,".indexOf(fieldtype) != -1 && fieldlen != null
				&& fieldlen.length() != 0) {
			ngtdatatype += "(" + fieldlen + ")";
		}

		// if (fieldtype.equals("C")) {
		// ngtdatatype += " BINARY";
		// }
		return ngtdatatype;
	}

	public static String getMacrofieldDef(Connection cndef, String macrofield)
			throws SQLException {
		String ret = null;
		PreparedStatement pstm = cndef
				.prepareStatement("SELECT MFIELDTYPE,MFIELDSIZE FROM ngtmacrofields WHERE MFIELDNAME=?");
		pstm.setString(1, macrofield);
		ResultSet rslt = pstm.executeQuery();
		if (rslt.next()) {
			ret = rslt.getString(1);
			if (rslt.getString(2) != null && rslt.getString(2).length() > 0) {
				ret += "(" + rslt.getString(2) + ")";
			}
		}
		rslt.close();
		pstm.close();
		if (ret == null) {
			throw new SQLException("Macrofield [" + macrofield + "] "+MessageLocalizer.getMessage("DOESNT_EXIST"));
		}
		return ret;
	}

	public void log(String ddl, String result) {
		try {
			if (!loggerInitialized) {
				boApplication app = p_ctx.getApplication();
				boApplicationConfig appConf = app.getApplicationConfig();
				String home = appConf.getNgtHome();
				File ddllog = new File(home + File.separator + "log");
				if (ddllog.exists() || ddllog.mkdir()) {
					File logFile = new File(ddllog.getAbsolutePath()
							+ File.separator + "builder_ddl.log");
					FileWriter fout = new FileWriter(logFile, true);
					fout.write("\n\n");
					fout
							.write("============================================================================================================================================================\n");
					fout.write("Builder: " + new Date() + "\n");
					fout
							.write("============================================================================================================================================================\n");
					fout.close();
					loggerInitialized = true;
				}
			}

			if (loggerInitialized) {
				String home = p_ctx.getApplication().getApplicationConfig()
						.getNgtHome();
				File logFile = new File(home + File.separator + "log"
						+ File.separator + "builder_ddl.log");
				FileWriter fout = new FileWriter(logFile, true);
				fout
						.write("\n-------------------------------------------------------------------------------------------------------------------------------\n");
				fout.write(ddl);
				fout.write("-- "+MessageLocalizer.getMessage("EXECUTE_RESULT")+": [" + result + "]");
				fout.close();
			}

		} catch (Exception e) {
			if (!loggerError) {
				logger.warn(LoggerMessageLocalizer.getMessage("CANNOT_LOG_FILE_DDL"), e);
			}
			loggerError = true;
		}

	}
}
