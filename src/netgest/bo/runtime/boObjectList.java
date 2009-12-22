/*Enconding=UTF-8*/
package netgest.bo.runtime;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import netgest.bo.data.DataResultSet;
import netgest.bo.data.DataSet;
import netgest.bo.data.IXEODataManager;
import netgest.bo.data.ObjectDataManager;
import netgest.bo.def.boDefHandler;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.sorter.AttributeSorter;
import netgest.bo.runtime.sorter.CardidSorter;
import netgest.bo.runtime.sorter.ClassSorter;
import netgest.bo.runtime.sorter.SorterNode;
import netgest.bo.system.boPoolable;
import netgest.utils.ExpressionParser;
import netgest.utils.ParametersHandler;

import netgest.bo.system.Logger;

public class boObjectList extends boPoolable {

	// SORTER ALGORITHMS
	public static final int ORDER_LIST = 0;
	public static final int BUBBLE_SORTER = 1;
	public static final int QUICK_SORTER = 2;
	public static final int SHAKER_SORTER = 3;
	public static final int BIDIR_BUBBLE_SORTER = 4;
	public static final int QUBBLESORT = 5;
	public static final int HEAPSORTALGORITHM = 6;
	public static final int COMBSORT11ALGORITHM = 7;
	public static final int INSERTIONSORTALGORITHM = 8;
	public static final int SELECTIONSORT = 9;

	private static final float SHRINKFACTOR = (float) 1.3;

	// logger
	private static Logger logger = Logger
			.getLogger("netgest.bo.runtime.boObjectList");

	public static final byte FORMAT_MANY = 1;
	public static final byte FORMAT_ONE = 0;

	private DataResultSet p_resultset;

	protected int p_page = 1;
	protected int p_pagesize = Integer.MAX_VALUE;
	private long p_nrrecords = Long.MIN_VALUE;

	private QLParser p_qlp;
	private String p_boql;
	private String p_sql;

	private Object[] p_sqlargs;

	private boDefHandler p_bodef;
	private boDefHandler p_selbodef;
	// private boObject[] p_bobjpointers;
	private boObject p_parent;
	private ParametersHandler p_parameters = new ParametersHandler();

	private byte p_format;

	private String p_parentAttributeName;

	private boolean p_usesecurity = true;

	private String p_fieldname = "BOUI";

	private String p_orderby = "";
	private boolean p_ordered = false;
	private String p_filter = null;

	private String p_fulltext = "";
	private String[] p_letter_filter;

	private String p_userQuery;
	private Object[] p_userqueryargs;

	private boObjectContainer p_container;
	// public boolean p_vl=false;

	private boolean p_readOnly = false;

	private int p_objectpreloaded = 0;
	
	private IXEODataManager p_legacydatamanager;

	public static final byte PAGESIZE_DEFAULT = 50;

	private static boObjectList getObjectList(EboContext ctx, String boql,
			Object[] sqlargs, int page, int pagesize, String orderby,
			String fulltext, String[] letter_filter, String userQuery,
			byte format, boolean useSecurity, boolean cache, boolean firstRows) {
		StringBuffer sb = new StringBuffer();

		if (firstRows && boql != null && boql.length() > 0
				&& boql.toUpperCase().indexOf("FIRST_ROWS") == -1) {
			int i = boql.toUpperCase().indexOf("SELECT", 0);
			if (i > -1) {
				boql = "SELECT /*+ FIRST_ROWS */ " + boql.substring(i + 6);
			}
		}

		for (byte i = 0; sqlargs != null && i < sqlargs.length; i++)
			sb.append(sqlargs[i]).append(",");
		String bolistui = "BOOBJECTLIST:BOQL["
				+ boql
				+ ":ARGUMENTS["
				+ sb
				+ "]]:PAGE["
				+ page
				+ "]:PAGESIZE["
				+ pagesize
				+ "]:ORDERBY["
				+ orderby
				+ "]:FULLTEXT["
				+ fulltext
				+ "]:USERQUERY["
				+ (userQuery != null ? userQuery : "")
				+ "]:LETTER["
				+ (letter_filter != null ? letter_filter[0] + letter_filter[1]
						: "") + "]FORMAT[" + format + "]";

		// boObjectList ret =
		// (boObjectList)boPoolManager.getObject(ctx,bolistui);
		boObjectList ret = (boObjectList) ctx.getApplication()
				.getMemoryArchive().getPoolManager().getObject(ctx, bolistui);

		if (ret == null) {
			ret = new boObjectList(ctx, boql, sqlargs, page, pagesize, orderby,
					fulltext, letter_filter, userQuery, format, useSecurity);
			if (cache) {
				ctx.getApplication().getMemoryArchive().getPoolManager()
						.putObject(ret, bolistui);
			}
		}
		ret.beforeFirst();
		return ret;
	}

	private static boObjectList getObjectList(EboContext ctx,
			String objectname, long[] bouis, int page, int pagesize,
			byte format, boolean securityOn) {
		StringBuffer sb = new StringBuffer();
		for (byte i = 0; bouis != null && i < bouis.length; i++)
			sb.append(bouis[i]).append(",");
		String bolistui = "BOOBJECTLIST:BOUIS[" + sb + "]:PAGE[" + page
				+ "]:PAGESIZE[" + pagesize + "]:FORMAT[" + format + "]";
		boObjectList ret = (boObjectList) ctx.getApplication()
				.getMemoryArchive().getPoolManager().getObject(ctx, bolistui);

		if (ret == null) {
			ret = new boObjectList(ctx, objectname, bouis, page, pagesize,
					format, securityOn);
			ctx.getApplication().getMemoryArchive().getPoolManager().putObject(
					ret, bolistui);
			// boPoolManager.putObject(ret,bolistui);
		}
		return ret;
	}

	private static boObjectList getObjectList(EboContext ctx,
			String objectname, long[] bouis, boObject parent,
			String attributeName, int page, int pagesize, byte format,
			boolean useSecurity) {
		return new boObjectList(ctx, objectname, bouis, parent, attributeName,
				page, pagesize, format, useSecurity);
	}

	// protected boObjectList(EboContext ctx,ResultSet rslt,byte format) {
	// super(ctx);
	// p_resultset = new DataRowSet();
	// p_format=format;
	// try {
	// ((DataRowSet)p_resultset).defineResultSet(rslt.getMetaData());
	// } catch (SQLException e) {throw new RuntimeException(e.getMessage());}
	// refreshData();
	// }

	protected boObjectList(EboContext ctx, DataResultSet data, boObject parent,
			boDefHandler refobjdef, String fieldname, String attname,
			AttributeHandler parentatt, byte format, boolean useSecurity) {
		super(ctx);
		p_parent = parent;
		p_resultset = data;
		p_fieldname = fieldname;
		p_parentAttributeName = attname;
		
		p_bodef = refobjdef;
		p_selbodef = refobjdef;
		
		p_usesecurity = useSecurity;
		p_format = format;
		refreshData();
		// p_selected.p_fieldname=this.p_fieldname;
	}

	private boObjectList(EboContext ctx, String boql, Object[] sqlargs,
			int page, int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery, byte format,
			boolean useSecurity) {
		super(ctx);
		p_boql = boql;
		p_page = page;
		p_pagesize = pagesize;
		p_orderby = orderby;
		p_format = boObjectList.FORMAT_MANY;
		p_qlp = new QLParser();
		p_qlp.toSql(boql, ctx);
		p_sqlargs = sqlargs;
		p_format = format;
		
		p_bodef = p_qlp.getObjectDef();
		p_selbodef = p_qlp.getSelectedObjectDef();
		
		p_fulltext = fulltext;
		p_letter_filter = letter_filter;
		p_userQuery = userQuery;
		p_usesecurity = useSecurity;
		refreshData();
		// p_selected = new boObjectList(ctx,p_resultset,format);
		// p_selected.p_fieldname=this.p_fieldname; 
	}

	private boObjectList(EboContext ctx, String objectname, long[] bouis,
			int page, int pagesize, byte format, boolean useSecurity) {
		super(ctx);
		p_page = page;
		p_pagesize = pagesize;
		
		p_bodef = boDefHandler.getBoDefinition(objectname);
		p_selbodef = p_bodef;		
		
		p_format = format;
		p_usesecurity = useSecurity;
		StringBuffer sb = new StringBuffer();

		if (!p_bodef.getBoName().equals("boObject")) {
			sb.append("SELECT BOUI FROM ").append(p_bodef.getBoMasterTable());
			sb.append(" WHERE BOUI IN (");
		} else {
			sb.append("SELECT UI$ as BOUI FROM OEBO_REGISTRY WHERE UI$ IN (");
		}
		short i;
		for (i = 0; i < bouis.length - 1; i++)
			sb.append(bouis[i]).append(",");
		sb.append(bouis[i]).append(")");
		p_sql = sb.toString();
		refreshData();
	}

	private boObjectList(EboContext ctx, String objectname, long[] bouis,
			boObject parent, String attributeName, int page, int pagesize,
			byte format, boolean useSecurity) {
		super(ctx);
		p_page = page;
		p_pagesize = pagesize;
		p_bodef = boDefHandler.getBoDefinition(objectname);
		p_selbodef = p_bodef;
		p_parent = parent;
		p_parentAttributeName = attributeName;
		p_format = format;
		p_usesecurity = useSecurity;
		StringBuffer sb = new StringBuffer();

		if (!p_bodef.getBoName().equals("boObject")) {
			sb.append("SELECT BOUI FROM ").append(p_bodef.getBoMasterTable());
			sb.append(" WHERE BOUI IN (");
		} else {
			sb.append("SELECT UI$ as BOUI FROM OEBO_REGISTRY WHERE UI$ IN (");
		}

		short i;
		for (i = 0; i < bouis.length - 1; i++)
			sb.append(bouis[i]).append(",");
		sb.append(bouis[i]).append(")");
		p_sql = sb.toString();
		refreshData();
	}

	public static boObjectList list(EboContext ctx, long boui)
			throws boRuntimeException {
		long[] a_boui = new long[1];
		a_boui[0] = boui;
		return list(ctx, a_boui, 1, boObjectList.PAGESIZE_DEFAULT, "");
	}

	public static boObjectList list(EboContext ctx, long[] boui, int page,
			int pagesize, String orderby) throws boRuntimeException {
		boObject _parent = boObject.getBoManager().loadObject(ctx, boui[0]);
		return getObjectList(ctx, _parent.getName(), boui, page, pagesize,
				boObjectList.FORMAT_MANY, true);
	}

	// Constructores with BOQL Statements
	public static boObjectList list(EboContext ctx, String boql) {
		return list(ctx, boql, 1, boObjectList.PAGESIZE_DEFAULT, "");
	}

	/**
	 * Devolve um boObjectList em resultado do boql.
	 * 
	 * @param ctx
	 *            , Contexto
	 * @param boql
	 *            , boql a executar
	 * @param useSecurity
	 *            , TRUE se é para usar seguranças, FALSE caso contrário
	 * @param cache
	 *            , TRUE se é para por na cache, FALSE caso contrário
	 */
	public static boObjectList list(EboContext ctx, String boql,
			boolean useSecurity, boolean cache) {
		return list(ctx, boql, 1, boObjectList.PAGESIZE_DEFAULT, "",
				useSecurity, cache);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			boolean useSecurity, boolean cache) {
		return listWFirstRows(ctx, boql, 1, boObjectList.PAGESIZE_DEFAULT, "",
				useSecurity, cache);
	}

	public static boObjectList list(EboContext ctx, String boql, int page) {
		return list(ctx, boql, (Object[]) null, page);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page) {
		return listWFirstRows(ctx, boql, (Object[]) null, page);
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, boolean useSecurity) {
		return list(ctx, boql, boqlargs, 1, boObjectList.PAGESIZE_DEFAULT, "",
				useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, boolean useSecurity) {
		return listWFirstRows(ctx, boql, boqlargs, 1,
				boObjectList.PAGESIZE_DEFAULT, "", useSecurity);
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs) {
		return list(ctx, boql, boqlargs, 1, boObjectList.PAGESIZE_DEFAULT, "",
				true);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs) {
		return listWFirstRows(ctx, boql, boqlargs, 1,
				boObjectList.PAGESIZE_DEFAULT, "", true);
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page) {
		return getObjectList(ctx, boql, boqlargs, page,
				boObjectList.PAGESIZE_DEFAULT, "", "", null, null,
				boObjectList.FORMAT_MANY, true, true, false);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page) {
		return getObjectList(ctx, boql, boqlargs, page,
				boObjectList.PAGESIZE_DEFAULT, "", "", null, null,
				boObjectList.FORMAT_MANY, true, true, true);
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize) {
		return list(ctx, boql, page, pagesize, "");
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize) {
		return listWFirstRows(ctx, boql, page, pagesize, "");
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, boolean useSecurity) {
		return list(ctx, boql, boqlargs, page, pagesize, "", useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, boolean useSecurity) {
		return listWFirstRows(ctx, boql, boqlargs, page, pagesize, "",
				useSecurity);
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby) {
		return list(ctx, boql, boqlargs, page, pagesize, orderby, true);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby) {
		return listWFirstRows(ctx, boql, boqlargs, page, pagesize, orderby,
				true);
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby,
			boolean useSecurity) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, boqlargs, page, pagesize, orderby,
				"", null, null, boObjectList.FORMAT_MANY, useSecurity, true,
				false);
		return toReturn;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby,
			boolean useSecurity) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, boqlargs, page, pagesize, orderby,
				"", null, null, boObjectList.FORMAT_MANY, useSecurity, true,
				true);
		return toReturn;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby, "",
				null, null, boObjectList.FORMAT_MANY, true, true, false);
		return toReturn;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby, "",
				null, null, boObjectList.FORMAT_MANY, true, true, true);
		return toReturn;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby, boolean useSecurity, boolean cache) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby, "",
				null, null, boObjectList.FORMAT_MANY, useSecurity, cache, false);
		return toReturn;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, boolean useSecurity,
			boolean cache) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby, "",
				null, null, boObjectList.FORMAT_MANY, useSecurity, cache, true);
		return toReturn;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery) {
		boObjectList toReturn;
		fulltext = arrangeFulltext( ctx, fulltext);
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby,
				fulltext, letter_filter, userQuery, boObjectList.FORMAT_MANY,
				true, true, false);
		return toReturn;
	}

	public static boObjectList listNoSecurity(EboContext ctx, String boql,
			int page, int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery) {
		boObjectList toReturn;
		fulltext = arrangeFulltext(ctx, fulltext);
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby,
				fulltext, letter_filter, userQuery, boObjectList.FORMAT_MANY,
				false, true, false);
		return toReturn;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery) {
		boObjectList toReturn;
		fulltext = arrangeFulltext(ctx, fulltext);
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby,
				fulltext, letter_filter, userQuery, boObjectList.FORMAT_MANY,
				true, true, true);
		return toReturn;
	}

	public static boObjectList listOnlyUsingAlias(EboContext ctx,
			String objName, int page, int pagesize, String orderby,
			String fulltext, String[] letter_filter) {
		boObjectList toReturn = boObjectList.list(ctx, "select " + objName
				+ " where 1=0");
		logger.finest("STARTED listOnlyUsingAlias");
		long t1 = System.currentTimeMillis();
		String alias[] = arrangeAlias(fulltext);
		if (alias != null && alias.length > 0) {
			StringBuffer sbBoql = new StringBuffer("select Ebo_Alias where ");
			for (int i = 0; i < alias.length; i++) {
				if (i != 0) {
					sbBoql.append(" or ");
				}
				sbBoql.append("upper(alias) = upper('").append(
						arrangeFulltext(ctx, alias[i])).append("')");
			}
			boObjectList aliasList = list(ctx, sbBoql.toString(), 999999999,
					999999999);
			aliasList.beforeFirst();

			sbBoql.delete(0, sbBoql.length());
			sbBoql.append("select " + objName + " where");
			int i = 0;
			while (aliasList.next()) {
				if (i == 0) {
					sbBoql.append(" (boui = ");
				} else {
					sbBoql.append(" or boui = ");
				}
				try {
					sbBoql.append(aliasList.getObject().getAttribute("ui")
							.getValueLong());
				} catch (boRuntimeException e) {
					// ignore
				}
				i++;
			}
			if (i != 0) {
				sbBoql.append(")");
				toReturn = getObjectList(ctx, sbBoql.toString(), null, page,
						pagesize, orderby, null, letter_filter, null,
						boObjectList.FORMAT_MANY, true, true, true);
				long t2 = System.currentTimeMillis();
				logger.finest("listOnlyUsingAlias took "
						+ (float) (Math.round((float) (t2 - t1) / 100f)) / 10f
						+ "s");
				return toReturn;
			}
		}
		return toReturn;
	}

	public static boObjectList listUsingAlias(EboContext ctx, String objName,
			int page, int pagesize, String orderby, String fulltext,
			String[] letter_filter) {
		boObjectList toReturn = boObjectList.list(ctx, "select " + objName
				+ " where 1=0");
		;
		long t1 = System.currentTimeMillis();
		String alias[] = arrangeAlias(fulltext);
		logger.finer("STARTED listUsingAlias");
		if (alias != null && alias.length > 0) {
			StringBuffer sbBoql = new StringBuffer("select Ebo_Alias where ");
			for (int i = 0; i < alias.length; i++) {
				if (i != 0) {
					sbBoql.append(" or ");
				}
				sbBoql.append("upper(alias) = upper('").append(
						arrangeFulltext(ctx, alias[i])).append("')");
			}
			boObjectList aliasList = list(ctx, sbBoql.toString(), 999999999,
					999999999);
			aliasList.beforeFirst();

			sbBoql.delete(0, sbBoql.length());
			sbBoql.append("select " + objName + " where");
			int i = 0;
			while (aliasList.next()) {
				if (i == 0) {
					sbBoql.append(" (boui = ");
				} else {
					sbBoql.append(" or boui = ");
				}
				try {
					sbBoql.append(aliasList.getObject().getAttribute("ui")
							.getValueLong());
				} catch (boRuntimeException e) {
					// ignore
				}
				i++;
			}
			if (i == 0) {
				sbBoql.delete(0, sbBoql.length());
				sbBoql.append("select " + objName + " where contains ('"
						+ treatFullText(ctx, fulltext) + "')");
			} else {
				sbBoql.append(") or boui in (select " + objName
						+ " where contains '" + treatFullText(ctx, fulltext) + "')");
			}
			toReturn = getObjectList(ctx, sbBoql.toString(), null, page,
					pagesize, orderby, null, letter_filter, null,
					boObjectList.FORMAT_MANY, true, true, true);
			return toReturn;
		} else {
			fulltext = arrangeFulltext(ctx, fulltext);
			toReturn = getObjectList(ctx, "select " + objName + " where 1=1",
					null, page, pagesize, orderby, fulltext, letter_filter,
					null, boObjectList.FORMAT_MANY, true, true, true);
			long t2 = System.currentTimeMillis();
			logger.finer("listUsingAlias took "
					+ (float) (Math.round((float) (t2 - t1) / 100f)) / 10f
					+ "s");
			return toReturn;
		}
	}

	private static String treatFullText(EboContext ctx, String fullText) {
		String[] keys = fullText.split(";");
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (int i = 0; i < keys.length; i++) {
			if (!"".equals(keys[i].trim())) {
				if (first) {
					sb.append(arrangeFulltext(ctx, keys[i].trim()));
				} else {
					sb.append(" OR ").append(arrangeFulltext(ctx, keys[i]));
				}
				first = false;
			}
		}
		return sb.toString();
	}

	public static boObjectList list(EboContext ctx, String parentAttributeName,
			long parentBoui) throws boRuntimeException {
		boObject _parent = boObject.getBoManager().loadObject(ctx, parentBoui);
		bridgeHandler parentBridge = _parent.getBridges().get(
				parentAttributeName);
		if (parentBridge == null)
			return ((ObjAttHandler) _parent.getAttribute(parentAttributeName))
					.edit();
		else
			return parentBridge.list();
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long boui, boObject parent, String attributeName) {
		long[] a_boui = new long[1];
		a_boui[0] = boui;
		return getObjectList(ctx, objectname, a_boui, parent, attributeName, 1,
				boObjectList.PAGESIZE_DEFAULT, boObjectList.FORMAT_MANY, true);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] a_boui) {
		return list(ctx, objectname, a_boui, 1, boObjectList.PAGESIZE_DEFAULT);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize) {
		return getObjectList(ctx, objectname, boui, page, pagesize,
				boObjectList.FORMAT_MANY, true);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize, String orderby) {
		return getObjectList(ctx, objectname, boui, page, pagesize,
				boObjectList.FORMAT_MANY, true);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] bouis, int page, int pagesize) {
		long[] lbouis = new long[bouis.length];
		for (short i = 0; i < bouis.length; i++) {
			lbouis[i] = Long.parseLong(bouis[i]);
		}
		return getObjectList(ctx, objectname, lbouis, page, pagesize,
				boObjectList.FORMAT_MANY, true);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] a_boui) {
		return list(ctx, objectname, a_boui, 1, boObjectList.PAGESIZE_DEFAULT);
	}

	public static boObjectList list(EboContext ctx, long boui,
			boolean useSecurity) throws boRuntimeException {
		long[] a_boui = new long[1];
		a_boui[0] = boui;
		return list(ctx, a_boui, 1, boObjectList.PAGESIZE_DEFAULT, "",
				useSecurity);
	}

	public static boObjectList list(EboContext ctx, long[] boui, int page,
			int pagesize, String orderby, boolean useSecurity)
			throws boRuntimeException {
		boObject _parent = boObject.getBoManager().loadObject(ctx, boui[0]);
		return getObjectList(ctx, _parent.getName(), boui, page, pagesize,
				boObjectList.FORMAT_MANY, useSecurity);
	}

	// Constructores with BOQL Statements
	public static boObjectList list(EboContext ctx, String boql,
			boolean useSecurity) {
		return list(ctx, boql, 1, boObjectList.PAGESIZE_DEFAULT, "",
				useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			boolean useSecurity) {
		return listWFirstRows(ctx, boql, 1, boObjectList.PAGESIZE_DEFAULT, "",
				useSecurity);
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			boolean useSecurity) {
		return list(ctx, boql, (Object[]) null, page, useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, boolean useSecurity) {
		return listWFirstRows(ctx, boql, (Object[]) null, page, useSecurity);
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, boolean useSecurity) {
		return getObjectList(ctx, boql, boqlargs, page,
				boObjectList.PAGESIZE_DEFAULT, "", "", null, null,
				boObjectList.FORMAT_MANY, useSecurity, true, false);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, boolean useSecurity) {
		return getObjectList(ctx, boql, boqlargs, page,
				boObjectList.PAGESIZE_DEFAULT, "", "", null, null,
				boObjectList.FORMAT_MANY, useSecurity, true, true);
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, boolean useSecurity) {
		return list(ctx, boql, page, pagesize, "", useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, boolean useSecurity) {
		return listWFirstRows(ctx, boql, page, pagesize, "", useSecurity);
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, boolean useSecurity, boolean cache) {
		return getObjectList(ctx, boql, null, page, pagesize, "", "", null,
				null, boObjectList.FORMAT_MANY, useSecurity, cache, false);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, boolean useSecurity, boolean cache) {
		return getObjectList(ctx, boql, null, page, pagesize, "", "", null,
				null, boObjectList.FORMAT_MANY, useSecurity, cache, true);
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby, boolean useSecurity) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby, "",
				null, null, boObjectList.FORMAT_MANY, useSecurity, true, false);
		return toReturn;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, boolean useSecurity) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby, "",
				null, null, boObjectList.FORMAT_MANY, useSecurity, true, true);
		return toReturn;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery, boolean useSecurity) {
		boObjectList toReturn;
		fulltext = arrangeFulltext(ctx, fulltext);
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby,
				fulltext, letter_filter, userQuery, boObjectList.FORMAT_MANY,
				useSecurity, true, false);
		return toReturn;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery, boolean useSecurity) {
		boObjectList toReturn;
		fulltext = arrangeFulltext(ctx, fulltext);
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby,
				fulltext, letter_filter, userQuery, boObjectList.FORMAT_MANY,
				useSecurity, true, true);
		return toReturn;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long boui, boObject parent, String attributeName,
			boolean useSecurity) {
		long[] a_boui = new long[1];
		a_boui[0] = boui;
		return getObjectList(ctx, objectname, a_boui, parent, attributeName, 1,
				boObjectList.PAGESIZE_DEFAULT, boObjectList.FORMAT_MANY,
				useSecurity);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] a_boui, boolean useSecurity) {
		return list(ctx, objectname, a_boui, 1, boObjectList.PAGESIZE_DEFAULT,
				useSecurity);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize, boolean useSecurity) {
		return getObjectList(ctx, objectname, boui, page, pagesize,
				boObjectList.FORMAT_MANY, useSecurity);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize, String orderby,
			boolean useSecurity) {
		return getObjectList(ctx, objectname, boui, page, pagesize,
				boObjectList.FORMAT_MANY, useSecurity);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] bouis, int page, int pagesize, boolean useSecurity) {
		long[] lbouis = new long[bouis.length];
		for (short i = 0; i < bouis.length; i++) {
			lbouis[i] = Long.parseLong(bouis[i]);
		}
		return getObjectList(ctx, objectname, lbouis, page, pagesize,
				boObjectList.FORMAT_MANY, useSecurity);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] a_boui, boolean useSecurity) {
		return list(ctx, objectname, a_boui, 1, boObjectList.PAGESIZE_DEFAULT,
				useSecurity);
	}

	// Constructores with list of BOUI Statement

	public static boObjectList list(EboContext ctx, String parentObjectName,
			String parentAttributeName, long parentBoui)
			throws boRuntimeException {
		boObject _parent = boObject.getBoManager().loadObject(ctx,
				parentObjectName, parentBoui);
		bridgeHandler parentBridge = _parent.getBridges().get(
				parentAttributeName);
		return parentBridge.list();
	}

	public static boObjectList list(EboContext ctx, String parentObjectName,
			String parentAttributeName, long parentBoui, boolean useSecurity)
			throws boRuntimeException {
		boObject _parent = boObject.getBoManager().loadObject(ctx,
				parentObjectName, parentBoui);
		bridgeHandler parentBridge = _parent.getBridges().get(
				parentAttributeName);
		return parentBridge.list();
	}

	//--------------------------------------------------------------------------
	// ----------------------------//
	// construtores com o FORMAT ONE
	//--------------------------------------------------------------------------
	// ----------------------------//
	public static boObjectList edit(EboContext ctx, String objectname,
			String[] a_boui) {
		return edit(ctx, objectname, a_boui, 1, boObjectList.PAGESIZE_DEFAULT);
	}

	public static boObjectList edit(EboContext ctx, String objectname,
			String[] a_boui, boolean useSecurity) {
		return edit(ctx, objectname, a_boui, 1, boObjectList.PAGESIZE_DEFAULT,
				useSecurity);
	}

	public static boObjectList edit(EboContext ctx, String objectname,
			String[] bouis, int page, int pagesize) {
		long[] lbouis = new long[bouis.length];
		for (short i = 0; i < bouis.length; i++) {
			lbouis[i] = Long.parseLong(bouis[i]);
		}
		return getObjectList(ctx, objectname, lbouis, page, pagesize,
				boObjectList.FORMAT_ONE, true);
	}

	public static boObjectList edit(EboContext ctx, String objectname,
			String[] bouis, int page, int pagesize, boolean useSecurity) {
		long[] lbouis = new long[bouis.length];
		for (short i = 0; i < bouis.length; i++) {
			lbouis[i] = Long.parseLong(bouis[i]);
		}
		return getObjectList(ctx, objectname, lbouis, page, pagesize,
				boObjectList.FORMAT_ONE, useSecurity);
	}

	// Constructores with BOQL Statements
	public static boObjectList edit(EboContext ctx, String boql) {
		return edit(ctx, boql, 1, boObjectList.PAGESIZE_DEFAULT, "");
	}

	public static boObjectList edit(EboContext ctx, String boql,
			boolean useSecurity) {
		return edit(ctx, boql, 1, boObjectList.PAGESIZE_DEFAULT, "",
				useSecurity);
	}

	public static boObjectList edit(EboContext ctx, String boql, int page) {
		return edit(ctx, boql, (Object[]) null, page);
	}

	public static boObjectList edit(EboContext ctx, String boql, int page,
			boolean useSecurity) {
		return edit(ctx, boql, (Object[]) null, page, useSecurity);
	}

	public static boObjectList edit(EboContext ctx, String boql,
			Object[] boqlargs) {
		return edit(ctx, boql, 1, boObjectList.PAGESIZE_DEFAULT, "");
	}

	public static boObjectList edit(EboContext ctx, String boql,
			Object[] boqlargs, boolean useSecurity) {
		return edit(ctx, boql, 1, boObjectList.PAGESIZE_DEFAULT, "",
				useSecurity);
	}

	public static boObjectList edit(EboContext ctx, String boql,
			Object[] boqlargs, int page) {
		return getObjectList(ctx, boql, boqlargs, page,
				boObjectList.PAGESIZE_DEFAULT, "", "", null, null,
				boObjectList.FORMAT_ONE, true, true, false);
	}

	public static boObjectList editWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page) {
		return getObjectList(ctx, boql, boqlargs, page,
				boObjectList.PAGESIZE_DEFAULT, "", "", null, null,
				boObjectList.FORMAT_ONE, true, true, true);
	}

	public static boObjectList edit(EboContext ctx, String boql,
			Object[] boqlargs, int page, boolean useSecurity) {
		return getObjectList(ctx, boql, boqlargs, page,
				boObjectList.PAGESIZE_DEFAULT, "", "", null, null,
				boObjectList.FORMAT_ONE, useSecurity, true, false);
	}

	public static boObjectList editWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, boolean useSecurity) {
		return getObjectList(ctx, boql, boqlargs, page,
				boObjectList.PAGESIZE_DEFAULT, "", "", null, null,
				boObjectList.FORMAT_ONE, useSecurity, true, true);
	}

	public static boObjectList edit(EboContext ctx, String boql, int page,
			int pagesize) {
		return edit(ctx, boql, page, pagesize, "");
	}

	public static boObjectList edit(EboContext ctx, String boql, int page,
			int pagesize, boolean useSecurity) {
		return edit(ctx, boql, page, pagesize, "", useSecurity);
	}

	public static boObjectList edit(EboContext ctx, String boql, int page,
			int pagesize, String orderby) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby, "",
				null, null, boObjectList.FORMAT_ONE, true, true, false);
		return toReturn;
	}

	public static boObjectList editWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby, "",
				null, null, boObjectList.FORMAT_ONE, true, true, true);
		return toReturn;
	}

	public static boObjectList edit(EboContext ctx, String boql, int page,
			int pagesize, String orderby, boolean useSecurity) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby, "",
				null, null, boObjectList.FORMAT_ONE, useSecurity, true, false);
		return toReturn;
	}

	public static boObjectList editWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, boolean useSecurity) {
		boObjectList toReturn;
		toReturn = getObjectList(ctx, boql, null, page, pagesize, orderby, "",
				null, null, boObjectList.FORMAT_ONE, useSecurity, true, true);
		return toReturn;
	}

	public static boObjectList edit(EboContext ctx, String objName, long boui)
			throws boRuntimeException {
		long[] a_boui = new long[1];
		a_boui[0] = boui;
		return edit(ctx, a_boui, objName, 1, boObjectList.PAGESIZE_DEFAULT, "");
	}

	public static boObjectList edit(EboContext ctx, String objName, long boui,
			boolean useSecurity) throws boRuntimeException {
		long[] a_boui = new long[1];
		a_boui[0] = boui;
		return edit(ctx, a_boui, objName, 1, boObjectList.PAGESIZE_DEFAULT, "",
				useSecurity);
	}

	public static boObjectList edit(EboContext ctx, long[] boui,
			String objName, int page, int pagesize, String orderby)
			throws boRuntimeException {

		return getObjectList(ctx, objName, boui, page, pagesize,
				boObjectList.FORMAT_ONE, true);
	}

	public static boObjectList edit(EboContext ctx, long[] boui,
			String objName, int page, int pagesize, String orderby,
			boolean useSecurity) throws boRuntimeException {

		return getObjectList(ctx, objName, boui, page, pagesize,
				boObjectList.FORMAT_ONE, useSecurity);
	}

	public static boObjectList edit(EboContext ctx, String objectname,
			long boui, boObject parent, String attributeName) {
		long[] a_boui = new long[1];
		a_boui[0] = boui;
		return getObjectList(ctx, objectname, a_boui, parent, attributeName, 1,
				boObjectList.PAGESIZE_DEFAULT, boObjectList.FORMAT_ONE, true);
	}

	public static boObjectList edit(EboContext ctx, String objectname,
			long boui, boObject parent, String attributeName,
			boolean useSecurity) {
		long[] a_boui = new long[1];
		a_boui[0] = boui;
		return getObjectList(ctx, objectname, a_boui, parent, attributeName, 1,
				boObjectList.PAGESIZE_DEFAULT, boObjectList.FORMAT_ONE,
				useSecurity);
	}

	public byte getFormat() {
		return p_format;
	}

	public QLParser getQLParser() {
		return p_qlp;
	}

	// public String getName() {
	// return p_bodef.getBoName();
	// }

	public boDefHandler getBoDef() throws boRuntimeException {
		if (p_bodef == null) {
			p_bodef = this.getObject().getBoDefinition();
		}

		return p_bodef;

	}

	public boDefHandler getSelectedBoDef() throws boRuntimeException {
		if (p_selbodef == null) {
			p_selbodef = getBoDef();
		}
		return p_selbodef;
	}
	
	public String getName() {
		return "boObjectList_" + this.hashCode();
	}

	public boObject getParent() {
		return p_parent;
	}

	public String getParentAtributeName() {
		return p_parentAttributeName;
	}

	public boolean currentObjectIsSelected() {
		return false;
	}

	// public boolean currentObjectIsSelected(){
	// boolean toRet=false;
	// if( p_selected != null){
	// toRet=p_selected.haveBoui(getCurrentBoui());
	// }
	// return toRet;
	// }

	// public void select(String[] bouis) {
	// for(short i =0;i<bouis.length;i++) {
	// select(bouis[i]);
	// }
	// }
	// public void select(String boui) {
	// if(p_selected==null) {
	// p_selected = new
	// boObjectList(getEboContext(),p_resultset,boObjectList.FORMAT_MANY);
	// p_selected.p_fieldname=this.p_fieldname;
	// }
	// try {
	// p_selected.p_resultset.moveToInsertRow();
	// p_selected.p_resultset.updateLong(p_selected.p_fieldname,Long.parseLong(
	// boui));
	// p_selected.p_resultset.insertRow();
	// } catch (SQLException e) {throw new RuntimeException(e.getMessage());}
	// }
	// public boObjectList getSelectedObjectList() {
	// return p_selected;
	// }
	//    
	// public void clearSelected() {
	// try {
	// while(p_selected.getRowCount()>0) {
	// p_selected.p_resultset.deleteRow();
	// }
	// } catch (SQLException e) {throw new RuntimeException(e.getMessage());}
	// }
	//
	// public boObjectList getSelectedObjects(){
	// return this;
	// }

	public long getCurrentBoui() {
		try {
			return this.p_resultset.getLong(this.p_fieldname);
		} catch (SQLException e) {
			throw new boRuntimeException2("boObjectList.getCurrentBoui() "
					+ e.getMessage());
		}
	}

	public void setRowProperty(String property, String value) {
		((DataResultSet) this.p_resultset).getCurrentRow().setParameter(
				property, value);
	}

	public String getRowProperty(String propertyName) {
		return ((DataResultSet) this.p_resultset).getCurrentRow().getParameter(
				propertyName);
	}

	public boolean haveBoui(long boui) {
		
		DataSet dataSet;
		int     colIdx;
		int 	rowCount;
		BigDecimal oBoui;
		
		oBoui = new BigDecimal( boui );
		dataSet = this.p_resultset.getDataSet();
		colIdx = dataSet.getMetaData().findColumn( this.p_fieldname );
		rowCount = dataSet.getRowCount();
		
		for( int i=1; i <= rowCount; i++ ) {
			if( oBoui.equals( dataSet.rows( i ).getBigDecimal( colIdx ) ) ) {
				this.moveTo( i );
				return true;
			}
		}
		return false;
		
		/*
		
		try {
			return ((DataResultSet) this.p_resultset).locatefor(
					new String[] { this.p_fieldname },
					new String[] { boui + "" });
		} catch (SQLException e) {
			throw new boRuntimeException2(this.getClass().getName()
					+ "haveBoui(" + boui + " ) " + e.getMessage());
		}
		*/

	}

	public String getSearchLetter() {
		if (p_letter_filter != null) {
			return p_letter_filter[1];
		}
		return null;
	}

	public String getUserQuery() {
		if (p_userQuery != null) {
			return p_userQuery;
		}
		return null;
	}

    public Object[] getUserQueryArgs() {
        if (p_userQuery!=null && p_userqueryargs!=null ) {
            return p_userqueryargs;
        }
        return (Object[])null;
    }

    public String getBOQL() {
    	return this.p_boql;
    }

    public Object[] getBOQLArgs() {
    	return this.p_sqlargs;
    }
        
	public void setUserQuery(String userQuery, Object[] userQueryParam) {
		this.p_userqueryargs = userQueryParam;
		this.p_userQuery = userQuery;

	}

	public boolean haveMorePages() {
		String hm = p_resultset.getParameter("HaveMoreData");
		if (hm != null && hm.equals("true")) {
			return true;
		}
		return false;
	}

	private boolean refreshData = true;
	
	public void setRefreshData( boolean value ) {
		this.refreshData = false;
	}
	
	public void refreshData() {
		
		if( refreshData ) {
			if( this.p_boql != null || this.p_sql != null ) {
				
				p_nrrecords = Long.MIN_VALUE;
				try {
					
					Object[] qArgs;
					if (p_userqueryargs != null) {
						ArrayList parList = new ArrayList();
		
						if (this.p_sqlargs != null)
							parList.addAll(Arrays.asList(this.p_sqlargs));
		
						parList.addAll(Arrays.asList(this.p_userqueryargs));
		
						qArgs = parList.toArray();
					} else {
						qArgs = this.p_sqlargs;
					}
		
					if( this.getBoDef() == null || this.getBoDef().getDataBaseManagerXeoCompatible() ) {
			
						if (p_qlp != null || p_resultset == null) {
							if (p_boql != null) {
								p_resultset = boObjectListResultFactory.getResultSetByBOQL(
										getEboContext(), this.p_boql, qArgs,
										this.p_orderby, this.p_page, this.p_pagesize,
										p_fulltext, p_letter_filter, p_userQuery,
										p_usesecurity);
							} else {
								p_resultset = boObjectListResultFactory.getResultSetBySQL(
										getEboContext(), this.p_sql, qArgs, this.p_orderby,
										this.p_page, this.p_pagesize, p_usesecurity);
							}
						}
						// p_bobjpointers = new
						// boObject[((DataRowSet)p_resultset).getRowCount()];
						p_resultset.beforeFirst();
						p_legacydatamanager = null;
					}
					else {
						if ( p_legacydatamanager == null ) {
							p_legacydatamanager = getEboContext().getApplication()
										.getXEODataManager( this.getBoDef() );
						}

						DataSet emptyDataSet = ObjectDataManager.createEmptyObjectDataSet( getEboContext(),  getBoDef() );
						p_legacydatamanager.fillDataSetByBOQL(
								getEboContext(),
								emptyDataSet,
								this, 
								this.p_boql, 
								qArgs,
								this.p_orderby, this.p_page, this.p_pagesize,
								p_fulltext, p_letter_filter, p_userQuery,
								p_usesecurity
							);
						
						p_resultset = new DataResultSet( emptyDataSet );
					}
				} catch (Exception e) {
					throw new boRuntimeException2( e );
				}
			}
		}
	}

	public boolean onlyOne() {
		return (this.p_page == 1 && getRowCount() == 1);
	}

	public boolean isEmpty() {
		// return (this.p_page==1 && ( getRowCount()==0 || p_vl) );
		return (this.p_page == 1 && getRowCount() == 0);
	}

	public void setFilter(String filter) {
		p_filter = filter;
	}

	public void removeFilter() {
		p_filter = null;
	}

	public String getFilter() {
		return p_filter;
	}

	public boObject getObject(long boui) throws boRuntimeException {
		try {
			int row = this.p_resultset.getRow();
			boObject ret = null;
			if (p_resultset.locatefor(p_fieldname + "=" + boui)) {// new
																	// String[]
																	// {
																	// p_fieldname
																	// },new
																	// String
																	// []{""
																	// +boui}))
																	// {
				ret = this.getObject();
			}
			this.p_resultset.absolute(row);
			return ret;
		} catch (SQLException e) {
			throw new boRuntimeException2("boObjectList.getObject( " + boui
					+ ")" + e.getMessage());
		}
	}

	public boObject getObject() throws boRuntimeException {
		boObject ret = null;

		try {

			// Check if the resultset is in a valid position
			if (p_resultset.isBeforeFirst())
				p_resultset.first();
			if (p_resultset.isAfterLast())
				p_resultset.last();

			int rc;
			rc = ((DataResultSet) p_resultset).getRowCount();

			if (rc > 0) // && !p_vl /*&& p_resultset.getLong(p_fieldname)!=0*/
			{
				// try {
				
				//if( p_legacydatamanager != null ) {
					
				//	return p_legacydatamanager.getObject();
					
				//}
				//else {
	
					if ((p_objectpreloaded < p_resultset.getRow()) && rc > 1) {
						int tofetch = Math
								.min(
										(p_resultset.getRowCount() - p_objectpreloaded) + 1,
										100);
						int row = p_resultset.getRow();
						long[] bouis = new long[tofetch];
						if (tofetch > 1) {
							p_resultset.absolute(p_objectpreloaded);
							int cnt = 0;
							while (cnt < 100 && p_resultset.next()) {
								bouis[cnt++] = p_resultset.getLong(p_fieldname);
							}
	
							p_objectpreloaded += cnt;
	
							boObject.getBoManager().preLoadObjects(getEboContext(),
									bouis);
							p_resultset.absolute(row);
						}
					}
	
					/*
					 * if(p_bobjpointers.length<p_resultset.getRow()) { boObject[]
					 * oldarr = p_bobjpointers; p_bobjpointers = new
					 * boObject[((DataRowSet)p_resultset).getRowCount()];
					 * System.arraycopy(oldarr,0,p_bobjpointers,0,oldarr.length); }
					 * 
					 * if(p_bobjpointers[p_resultset.getRow()-1]==null) {
					 */
					// ret =
					// (boObject)boPoolManager.getObject(super.getEboContext(),
					// "BOBJECT:"+p_resultset.getLong(p_fieldname));
					// if(ret==null) {
					// String lctx=null;
					// if(this.poolIsStateFull()) {
					// lctx =
					// super.getEboContext().setPreferredPoolObjectOwner(this.
					// poolUniqueId());
					// }
					// if( !p_vl )
					// {
					if (this.getObjectContainer() != null
							&& this.getObjectContainer().poolIsStateFull()) {
						ret = this.getObjectContainer().getObject(
								p_resultset.getLong(p_fieldname));
					} else {
						ret = boObject.getBoManager().loadObject(getEboContext(),
								p_resultset.getLong(p_fieldname));
					}
					// }
					// else
					// {
					// if ( p_resultset.getLong( p_fieldname ) == 0 )
					// {
					// String nameobj = p_bodef.getBoName();
					// if ( nameobj.equalsIgnoreCase("boObject")
					// {
					// nameobj =
					// p_parentAttribute.getDefAttribute().getObjects()[0];
					// }
					//                                
					//                                
					// }
					// ret =
					// boObject.getBoManager().loadObject(getEboContext(),p_resultset
					// .getLong(p_fieldname));
					// era asssim
					// try
					// {
					// ret =
					// boObject.getBoManager().loadObject(getEboContext(),p_resultset
					// .getLong(p_fieldname));
					// }
					// catch( boRuntimeException e )
					// {
					// if ( p_bodef.getBoName().equalsIgnoreCase("boObject") )
					// {
					// //MARTELADA JMF a REVER
					// ret =
					// boObject.getBoManager().createObject(getEboContext(),p_parent
					// .getBoDefinition().getName());
					// }
					// else
					// {
					// //ret =
					// boObject.getBoManager().createObject(getEboContext(),p_bodef
					// .getBoName());
					// if ( modeEditTemplate )
					// ret =
					// boObject.getBoManager().createObject(getEboContext(),p_bodef
					// .getBoName(), true);
					// else
					// ret =
					// boObject.getBoManager().createObject(getEboContext(),p_bodef
					// .getBoName());
					// }
					//                                
					// p_resultset.updateLong(p_fieldname,ret.getBoui());
					// p_resultset.updateRow();
					//                                
					// }
					// }
	
					// boPoolManager.putObject(ret,"BOBJECT:"+p_resultset.getLong(
					// p_fieldname));
					// }
					// if(this.poolIsStateFull()) {
	
					// if(!ret.poolIsStateFull()) ret.poolSetStateFull();
	
					// super.getEboContext().setPreferredPoolObjectOwner(lctx);
					// }
					// p_bobjpointers[p_resultset.getRow()-1] = ret;
					/*
					 * } else { ret = p_bobjpointers[p_resultset.getRow()-1]; }
					 */
					if (ret!=null && ret.getBoui() != p_resultset.getLong(p_fieldname)) {
						if (ret.getBoui() == 0) {
							throw new RuntimeException(
									"Internal error!!!! cannot add a object with boui [0]");
						}
						p_resultset.updateLong(p_fieldname, ret.getBoui());
						p_resultset.updateRow();
					}
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
				}
			//}
			// else {
			// if(rc ==0 )
			// {
			// p_resultset.moveToInsertRow();
			// }
			// if ( p_bodef.getBoName().equalsIgnoreCase("boObject") )
			// {
			// //MARTELADA JMF a REVER
			// ret =
			// boObject.getBoManager().createObject(getEboContext(),p_parent
			// .getBoDefinition().getName());
			// }
			// else
			// {
			// //ret =
			// boObject.getBoManager().createObject(getEboContext(),p_bodef
			// .getBoName());
			// if ( modeEditTemplate )
			// ret =
			// boObject.getBoManager().createObject(getEboContext(),p_bodef
			// .getBoName(), true);
			// else
			// ret =
			// boObject.getBoManager().createObject(getEboContext(),p_bodef
			// .getBoName());
			//                    
			// }
			//                
			// p_resultset.updateLong(p_fieldname,ret.getBoui());
			// if(rc == 0 ) {
			// p_resultset.insertRow();
			// p_resultset.first();
			// } else {
			// p_resultset.updateRow();
			// }
			//                
			// ret.poolSetStateFull();
			//                
			// p_vl = true;
			// }
		} catch (SQLException e) {
			throw new boRuntimeException2("boObjectList.getObject()"
					+ e.getMessage());
		}
		return ret;
	}

	public int getRowCount() {
		return p_resultset.getRowCount();
	}

	public long getRecordCount() {
		if (p_qlp != null) {
			if (p_nrrecords == Long.MIN_VALUE) {
				if ( p_legacydatamanager != null ) {
					p_nrrecords = p_legacydatamanager.getRecordCountByBOQL(
							getEboContext(), 
							this, 
							p_boql, 
							p_sqlargs, 
							p_fulltext, 
							p_letter_filter, 
							p_userQuery, 
							p_usesecurity
					);
				}
				else 
				{
					Object[] qArgs;
					if (p_userqueryargs != null) {
						ArrayList parList = new ArrayList();
	
		
						if (this.p_sqlargs != null)
							parList.addAll(Arrays.asList(this.p_sqlargs));
	
						parList.addAll(Arrays.asList(this.p_userqueryargs));
		
						qArgs = parList.toArray();
					} else {
						qArgs = this.p_sqlargs;
					}
					
					
					p_nrrecords = boObjectListResultFactory.getRecordCount(
							getEboContext(), p_boql, qArgs, p_fulltext, p_letter_filter, p_userQuery, p_usesecurity);
				}
			}
			return p_nrrecords;
		}
		return getRowCount();
	}

	/*
	 * fcamara: Não estava a passar os argumentos public long getRecordCount() {
	 * if(p_qlp!=null) { if(p_nrrecords==Long.MIN_VALUE) {
	 * p_nrrecords=boObjectListResultFactory
	 * .getRecordCount(getEboContext(),p_boql, p_usesecurity ); } return
	 * p_nrrecords; } return getRowCount(); }
	 */
	public int getPages() {
		int ret;
		if ((getRecordCount() % p_pagesize) == 0) {
			ret = (int) (getRecordCount() / p_pagesize);
		} else {
			ret = ((int) (getRecordCount() / p_pagesize)) + 1;
		}
		return ret;
	}

	public int getPageSize() {
		return p_pagesize;
	}

	public int getPage() {
		return p_page;
	}

	public String getValueString() throws boRuntimeException {
		String res = "";
		this.beforeFirst();
		for (int i = 0; i < this.getRowCount(); i++) {
			res += this.getObject().getBoui() + ";";
			this.next();
		}
		if (res.equals(""))
			return null;
		else
			return res;
	}

	public boolean next() {
		boolean ret = false;
		try {
			if (p_filter != null) {
				ret = p_resultset.next();
				if (ret) {
					ExpressionParser x = new ExpressionParser();
					Boolean ok = (Boolean) x.parseExpression(p_resultset,
							p_filter);
					while (!ok.booleanValue()) {
						if (p_resultset.next()) {
							ok = (Boolean) x.parseExpression(p_resultset,
									p_filter);
						} else {
							break;
						}

					}
					ret = ok.booleanValue();
				}

			} else {
				ret = p_resultset.next();
			}

			return ret;

		} catch (SQLException e) {
			throw new boRuntimeException2("boObjectList.next() "
					+ e.getMessage());
		}
	}

	public boolean moveTo(int recno) {
		try {
			return p_resultset.absolute(recno);
		} catch (SQLException e) {
			throw new boRuntimeException2("boObjectList.moveTo(" + recno + ")"
					+ e.getMessage());
		}
	}

	public int getRow() {
		try {
			return p_resultset.getRow();
		} catch (SQLException e) {
			throw new boRuntimeException2("boObjectList.getRow()"
					+ e.getMessage());
		}
	}

	public void nextPage() {
		if (p_page < getPages()) {
			p_page++;
			refreshData();
		}
	}

	public void previousPage() {
		if (p_page > 1) {
			p_page--;
			refreshData();
		}
	}

	public void firstPage() {
		if (p_page != 1 && p_page > 0) {
			p_page = 1;
			refreshData();
		}
	}

	public void lastPage() {
		if (p_page != this.getPages()) {
			p_page = this.getPages();
			refreshData();
		}
	}

	public boolean previous() {
		try {
			return p_resultset.previous();
		} catch (SQLException e) {
			throw new boRuntimeException2("boObjectList.previous() "
					+ e.getMessage());
		}
	}

	public boolean first() {
		try {
			return p_resultset.first();
		} catch (SQLException e) {
			throw new boRuntimeException2("boObjectList.first() "
					+ e.getMessage());
		}
	}

	public void beforeFirst() {
		try {
			p_resultset.beforeFirst();
		} catch (SQLException e) {
			throw new boRuntimeException2("boObjectList.beforeFirst() "
					+ e.getMessage());
		}
	}

	public boolean last() {
		try {
			return p_resultset.last();
		} catch (SQLException e) {
			throw new boRuntimeException2("boObjectList.last() "
					+ e.getMessage());
		}
	}

	public boObjectList list() {
		return this;
	}

	public String getOrderBy() {
		return p_orderby;
	}

	public void setQueryOrderBy(String orderBy) {
		this.p_orderby = orderBy;
	}

	public boolean ordered() {
		return p_ordered;
	}

	public void setOrderBy(String column) throws boRuntimeException {
		p_orderby = column;
		if (p_resultset != null && p_resultset.getRowCount() > 0) {
			try {
				long t0 = System.currentTimeMillis();
				ClassSorter cs = null;
				if (this.getObject().getAttribute(p_orderby) != null) {
					cs = new AttributeSorter(getBoDef().getName(), p_orderby);
				} else if ("CARDID".equals(column)) {
					cs = new CardidSorter();
				} else {
					cs = (ClassSorter) Class.forName(p_orderby).newInstance();
				}
				// if(!cs.boObjectListOrder()) return;
				p_ordered = true;
				SorterNode[] helper = cs.getValues(this);
				cs.sort(helper);
				long boui;
				String[] phisicalRow = this.getValueString().split(";");
				if (sameOrder(phisicalRow, helper))
					return;
				int pos;
				for (int i = 0; i < helper.length; i++) {
					boui = helper[i].getBoui();
					pos = findKey(phisicalRow, String.valueOf(boui));
					if ((pos + 1) != (i + 1)) {
						this.moveTo(pos + 1);
						this.moveRowTo(i + 1);
						phisicalRow = setPhisicalRow(phisicalRow, pos, i);
					}
				}
				long t1 = System.currentTimeMillis();
				logger.finer("Tempo total " + (t1 - t0));
			} catch (Exception e) {
				logger.severe("", e);
			}
		}
	}

	private boolean sameOrder(String[] phisicalRow, SorterNode[] helper) {
		for (int i = 0; i < phisicalRow.length; i++) {
			if (!phisicalRow[i].equals(String.valueOf(helper[i].getBoui()))) {
				return false;
			}
		}
		return true;
	}

	private String[] setPhisicalRow(String[] a, int from, int to) {
		String aux = a[to], aux2 = null;
		a[to] = a[from];
		for (int i = (to + 1); i < (from + 1); i++) {
			aux2 = a[i];
			a[i] = aux;
			aux = aux2;
		}
		return a;
	}

	private static int findKey(String[] a, String key) {
		if (key == null)
			return -1;
		for (int i = 0; i < a.length; i++) {
			if (key.equalsIgnoreCase(a[i])) {
				return i;
			}
		}
		return -1;
	}

	public void poolObjectActivate() {
		
		this.refreshData();

	}

	public void poolObjectPassivate() {
	
		this.p_container = null;
		// p_vl = false;
		p_resultset = null;
		p_filter = null;

	}

	public void inserRow(long boui) {
		try {
			p_resultset.moveToInsertRow();
			p_resultset.updateLong(p_fieldname, boui);
			p_resultset.insertRow();
		} catch (SQLException e) {
			throw new boRuntimeException2(" boObjectList.insertRow(" + boui
					+ ")" + e.getMessage());
		}
	}

	public void setObjectContainer(boObjectContainer container) {
		p_container = container;
	}

	public boObjectContainer getObjectContainer() {
		return p_container;
	}

	public ParametersHandler getParametersHandler() {
		return p_parameters;
	}

	public String[] getParametersNames() {
		return p_parameters.getParametersNames();
	}

	public String[] getParametersValues() {
		return p_parameters.getParametersValues();
	}

	public String getParameter(String parametername) {
		String ret = p_parameters.getParameter(parametername);
		if (ret == null)
			ret = p_parameters.getParameter(parametername);
		return ret;
	}

	public void setParameter(String parametername, String parametervalue) {
		p_parameters.setParameter(parametername, parametervalue);
	}

	public void setParameters(String[] parametersnames,
			String[] parametersvalues) {
		p_parameters.setParameters(parametersnames, parametersvalues);
	}

	// public boolean haveVL()
	// {
	// return p_vl || this.getRowCount() == 0;
	// }

	public EboContext removeEboContext() {
		p_objectpreloaded = 0;
		return super.removeEboContext();
	}

	public void setReadOnly(boolean value) {
		p_readOnly = value;
	}

	public boolean isReadOnly() {
		return p_readOnly;
	}

	public boolean containsChangedObjects() throws boRuntimeException {
		boolean toRet = false;
		int row = this.getRow();
		this.beforeFirst();
		while (next() && !toRet) {
			boObject linObj = 
				boObject.getBoManager().getObjectInContext(getEboContext(),  getCurrentBoui() );
			//getObject().isChanged();
			if( linObj != null ) {
				toRet = linObj.isChanged();  
			}
		}

		this.moveTo(row);
		return toRet;
	}

	public void removeCurrent() throws boRuntimeException {
		if (this.p_resultset.getCurrentRow().getRow() > 0) {
			if (p_resultset.getRowCount() > 0) {
				int row = this.getRow();

				DataResultSet dataSet = this.p_resultset;
				dataSet.moveRowTo(row);
				try {
					dataSet.deleteRow();
					if (dataSet.getRowCount() < row)
						/*
						 * dataSet.moveRowTo(row); else
						 */
						dataSet.afterLast();
				} catch (Exception e) {
					throw new boRuntimeException("removeCurrent",
							"boObjectList", e);
				}
			}
		}
	}

	public void add(long obj_boui) throws boRuntimeException {
		try {
			this.p_resultset.moveToInsertRow();
			this.p_resultset.insertRow();
		} catch (Exception e) {
			throw new boRuntimeException("boObjectList", "add", e);
		}
		this.p_resultset.getCurrentRow().updateLong(1, obj_boui);
	}
/*
	public static final String putClassesOnFullText(boDefHandler def,
			String fulltext) {
		return putClassesOnFullText(def, fulltext, false);
	}

	public static final String putClassesOnFullText(boDefHandler def,
			String fulltext, boolean arrangeText) {
		StringBuffer query = new StringBuffer();
		// if ( XEOSpyConnection.SPY_MAX_DEBUG_STATEMENTS == 200 )
		// {
		//  Desligar optimização dos Text 
		// query.append('(');
		// ArrayList classes = QLParser.getAllClassNames( def );
		// for (int i = 0; i < classes.size(); i++)
		// {
		// if( i > 0 ) query.append( '|' );
		// query.append( '{' );
		// query.append( classes.get( i ) );
		// query.append( '}' );
		// }
		// query.append( ") & " );
		// }
		query.append(arrangeText ? arrangeFulltext(fulltext)
				: fulltext);
		return query.toString();
	}
*/

	public static String arrangeFulltext( EboContext ctx, String fulltext) {
		return ctx.getDataBaseDriver().getDriverUtils().arranjeFulltextSearchText( fulltext );
	}

	public static String[] arrangeAlias(String alias) {
		if (alias == null || alias.length() == 0)
			return null;
		String[] toRet = alias.split(";");

		return toRet;
	}

	public boolean havePoolChilds() {
		// Override this netgest.bo.system.boPoolable method
		return false;
	}

	public DataResultSet getRslt() {
		return this.p_resultset;
	}

	public String getBQL() {
		return p_boql;
	}

	public void moveRowTo(int row) throws boRuntimeException {
		this.p_resultset.moveRowTo(row + 1);
	}

	// public void orderList (Comparator c) throws boRuntimeException
	// {
	// long t0 = System.currentTimeMillis();
	// this.beforeFirst();
	// int compval;
	// for(int i=0; i < this.getRowCount(); i++)
	// {
	// this.moveTo(i+1);
	//        
	// boObject[] arg_obj = new boObject[2];
	// arg_obj[0] = getObject();
	// while(this.next())
	// {
	// arg_obj[1] = getObject();
	// try{
	// if(c.compare(arg_obj[0], arg_obj[1]) > 0)
	// {
	// arg_obj[0] = getObject();
	// moveRowTo(i+1);
	// }
	// }catch(Exception e){logger.severe("",e);}
	// }
	// }
	// long t1 = System.currentTimeMillis();
	// logger.finer("Tempo total "+ (t1-t0));
	// }

	public void BidirectionalBubbleSortAlgorithm(Comparator c)
			throws boRuntimeException {
		long t0 = System.currentTimeMillis();
		this.beforeFirst();
		int j;
		int limit = this.getRowCount() + 1;
		int st = 0;
		boolean flipped = false;
		Object o1 = null, o2 = null;
		while (st < limit) {
			flipped = false;
			st++;
			limit--;
			for (j = st; j <= limit; j++) {
				this.moveTo(j);
				o1 = this.getObject();
				this.moveTo(j + 1);
				o2 = this.getObject();
				if (c.compare(o1, o2) > 0) {
					swap(j, j + 1);
					flipped = true;
				}
			}
			if (!flipped) {
				return;
			}
			for (j = limit; --j >= st;) {
				this.moveTo(j);
				o1 = this.getObject();
				this.moveTo(j + 1);
				o2 = this.getObject();
				if (c.compare(o1, o2) > 0) {
					swap(j, j + 1);
					flipped = true;
				}
			}
			if (!flipped) {
				return;
			}
		}
		long t1 = System.currentTimeMillis();
		logger.finer("Tempo total " + (t1 - t0));
	}

	public void selectionSort(Comparator c) throws boRuntimeException {
		int length = this.getRowCount();
		Object o1 = null, o2 = null;
		for (int i = 1; i <= length; i++) {
			int min = i;
			int j;

			/*
			 * Find the smallest element in the unsorted list
			 */
			for (j = i + 1; j < length; j++) {
				this.moveTo(j);
				o1 = this.getObject();
				this.moveTo(min);
				o2 = this.getObject();
				if (c.compare(o1, o2) < 0) {
					min = j;
				}
			}

			/*
			 * Swap the smallest unsorted element into the end of the sorted
			 * list.
			 */
			swap(min, i);
		}
	}

	public void InsertionSortAlgorithm(Comparator c) throws boRuntimeException {
		int length = this.getRowCount();
		Object o1 = null, o2 = null;
		for (int i = 2; i <= length; i++) {
			int j = i;
			this.moveTo(i - 1);
			o1 = this.getObject();
			this.moveTo(i);
			o2 = this.getObject();
			while ((j > 1) && (c.compare(o1, o2) > 0)) {
				swap(j, j - 1);
				j--;
			}
		}

	}

	public void CombSort11Algorithm(Comparator c) throws boRuntimeException {
		boolean flipped = false;
		int gap, top;
		int i, j;
		int length = this.getRowCount();
		Object o1 = null, o2 = null;

		gap = length + 1;
		do {
			gap = (int) ((float) gap / SHRINKFACTOR);
			switch (gap) {
			case 0: /* the smallest gap is 1 - bubble sort */
				gap = 1;
				break;
			case 9: /* this is what makes this Combsort11 */
			case 10:
				gap = 11;
				break;
			default:
				break;
			}
			flipped = false;
			top = (length + 1) - gap;
			for (i = 1; i < top; i++) {
				j = i + gap;
				this.moveTo(i);
				o1 = this.getObject();
				this.moveTo(j);
				o2 = this.getObject();
				if (c.compare(o1, o2) > 0) {
					swap(i, j);
					flipped = true;
				}
			}
		} while (flipped || (gap > 1));
	}

	// ---HeapSortAlgorithm
	public void heapSortAlgorithm(Comparator c) throws boRuntimeException {
		int n = this.getRowCount() + 1;
		for (int k = n / 2; k > 1; k--) {
			downheap(c, k, n);
		}
		do {
			swap(1, n - 1);
			n = n - 1;
			downheap(c, 2, n);
		} while (n > 2);
	}

	private void downheap(Comparator c, int k, int N) throws boRuntimeException {
		this.moveTo(k - 1);
		Object o1 = this.getObject();
		Object o2 = null, o3 = null;
		while (k <= N / 2) {
			int j = k + k;
			this.moveTo(j);
			o2 = this.getObject();
			this.moveTo(j - 1);
			o3 = this.getObject();
			if ((j < N) && (c.compare(o3, o2) < 0)) {
				j++;
			}
			if (c.compare(o1, o3) >= 0) {
				break;
			} else {
				swap(k - 1, j - 1);
				k = j;
			}
		}
	}

	// ------

	// QubbleSortAlgorithm
	public void qubbleSort(Comparator c) throws boRuntimeException {
		qubbleSort(c, 1, this.getRowCount());
	}

	public void qubbleSort(Comparator c, int lo0, int hi0)
			throws boRuntimeException {
		int lo = lo0;
		int hi = hi0;
		/*
		 * Bubble sort if the number of elements is less than 6
		 */
		if ((hi - lo) <= 6) {
			bsort(c, lo, hi);
			return;
		}
		/*
		 * Pick a pivot and move it out of the way
		 */
		Object pivot = null, o1 = null, o2 = null;
		int n = (lo + hi) / 2;
		this.moveTo(n);
		pivot = this.getObject();
		swap(n, hi);
		while (lo < hi) {
			/*
			 * Search forward from a[lo] until an element is found that is
			 * greater than the pivot or lo >= hi
			 */
			this.moveTo(lo);
			o1 = this.getObject();
			while (c.compare(o1, pivot) <= 0 && lo < hi) {
				lo++;
			}

			/*
			 * Search backward from a[hi] until element is found that is less
			 * than the pivot, or hi <= lo
			 */
			this.moveTo(hi);
			o2 = this.getObject();
			while (c.compare(pivot, o2) <= 0 && lo < hi) {
				hi--;
			}

			/*
			 * Swap elements a[lo] and a[hi]
			 */
			if (lo < hi) {
				swap(lo, hi);
			}
		}

		/*
		 * Put the median in the "center" of the list
		 */
		swap(hi0, hi);
		// a[hi0] = a[hi];
		// a[hi] = pivot;

		/*
		 * Recursive calls, elements a[lo0] to a[lo-1] are less than or equal to
		 * pivot, elements a[hi+1] to a[hi0] are greater than pivot.
		 */
		qubbleSort(c, lo0, lo - 1);
		qubbleSort(c, hi + 1, hi0);
	}

	private void bsort(Comparator c, int lo, int hi) throws boRuntimeException {
		Object o1 = null, o2 = null;
		for (int j = hi; j > lo; j--) {
			for (int i = lo; i < j; i++) {
				this.moveTo(i);
				o1 = this.getObject();
				this.moveTo(i + 1);
				o2 = this.getObject();
				if (c.compare(o1, o2) > 0) {
					swap(i, i + 1);
				}
			}
		}
	}

	// ------------------------------

	public void bubbleSort(Comparator c) throws boRuntimeException {
		long t0 = System.currentTimeMillis();
		this.beforeFirst();
		boolean change = true;
		boObject o1 = null, o2 = null;
		while (change) {
			logger.finer("1st");
			print();
			change = false;
			for (int i = 1; i < this.getRowCount(); i++) {
				this.moveTo(i);
				o1 = getObject();
				this.moveTo(i + 1);
				o2 = getObject();
				if (c.compare(o1, o2) > 0) {
					logger.finer("2st (" + (i) + "," + (i + 1) + ")");
					print();
					swap(i, i + 1);
					change = true;
					print();
				}
			}
		}
		print();
		long t1 = System.currentTimeMillis();
		logger.finer("Tempo total " + (t1 - t0));
	}


	private void swap(int pos1, int pos2) throws boRuntimeException {
		if ((pos2 - pos1) == 1 || (pos2 - pos1) == -1) {
			this.moveTo(pos1);
			this.moveRowTo(pos2);
		} else {
			this.moveTo(pos1);
			this.moveRowTo(pos2);
			this.moveTo(pos2);
			this.moveRowTo(pos1);
		}
	}

	private void print() {
		StringBuffer sb = new StringBuffer();
		try {
			this.beforeFirst();
			while (this.next()) {
				sb.append(this.getObject().getAttribute("id").getValueString())
						.append("|");
			}
			logger.finer(sb.toString());
		} catch (boRuntimeException e) {

		}

	}

	public String getFullTextSearch() {
		return p_fulltext;
	}

	public void setFullTextSearch(String p_fulltext) {
		this.p_fulltext = p_fulltext;
	}
	
}