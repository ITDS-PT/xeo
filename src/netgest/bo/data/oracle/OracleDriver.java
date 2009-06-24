/*Enconding=UTF-8*/
package netgest.bo.data.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import netgest.bo.data.Driver;
import netgest.bo.data.DriverUtils;
import netgest.bo.data.ReaderAdapter;
import netgest.bo.data.WriterAdapter;
import netgest.bo.runtime.EboContext;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class OracleDriver implements Driver {

	private String p_ddlds;
    private String p_dmlds;
    private String p_name;

    /**
     *
     * @since
     */
    public OracleDriver()
    {
    }

    public ReaderAdapter createReaderAdapter(EboContext ctx)
    {
        return new OracleReaderAdapter(p_dmlds);
    }

    public WriterAdapter createWriterAdapter(EboContext ctx)
    {
        return new OracleWriterAdapter(p_dmlds);
    }

    public void initializeDriver(String name, String dmlDataSource,
        String ddlDataSource)
    {
        p_ddlds = ddlDataSource;
        p_dmlds = dmlDataSource;
        p_name = name;
    }

    public DriverUtils getDriverUtils()
    {
        return new OracleUtils(p_dmlds);
    }

    private Connection getConnection(String dataSource, String username,
        String password)
    {
        final int retrycount = 5;
        int retries = 0;
        Connection ret = null;

        while (ret == null)
        {
            try
            {
                ret = getDataSource(dataSource).getConnection(username, password);
                // IMBR
//                ret.setAutoCommit(false);

                return ret;
            }
            catch (SQLException e)
            {
                if (retries >= retrycount)
                {
                    throw new RuntimeException(
                        " Failed to create connection. \n" +
                        e.getClass().getName() + "\n" + e.getMessage());
                }
            }

            retries++;
        }

        return ret;
    }

    private static final Connection getConnection(String dataSource)
    {
        final int retrycount = 5;
        int retries = 0;
        Connection ret = null;

        while (ret == null)
        {
            try
            {
                ret = getDataSource(dataSource).getConnection();
                // IMBR
//                ret.setAutoCommit(false);

                return ret;
            }
            catch (SQLException e)
            {
                ret = null;
                if (retries >= retrycount) 
                {
                    throw new RuntimeException(
                        " Failed to create connection. \n" +
                        e.getClass().getName() + "\n" + e.getMessage());
                }
            }

            retries++;
        }

        return ret;
    }

    private static final DataSource getDataSource(String dataSourceName)
    {
        try
        {
            final InitialContext ic = new InitialContext();

            return (DataSource) ic.lookup(dataSourceName);
        }
        catch (NamingException e)
        {
            throw new RuntimeException("Error looking for DataSource name [" +
                dataSourceName + "].\n" + e.getMessage());
        }
    }

    public Connection getDedicatedConnection()
    {
       Connection cn=null;
        try
        {
            cn = getConnection(p_ddlds);
            cn.setAutoCommit(false);
        }
        catch (SQLException e) {
            System.out.println("Failed to obtain Dedicated Connection or disable AutoCommit");
        }
        return cn;    
    }

    public Connection getConnection()
    {
        return getConnection(p_dmlds);
    }

    public Connection getDedicatedConnection(String username, String password)
    {
       Connection cn=null;
        try
        {
            cn = getConnection(p_ddlds, username, password);
            cn.setAutoCommit(false);
        }
        catch (SQLException e) {
            System.out.println("Failed to obtain Dedicated Connection or disable AutoCommit");
        }
        return cn;
    }

    public Connection getConnection(String username, String password)
    {
        return getConnection(p_dmlds, username, password);
    }

    public OracleDBM getDBM()
    {
        return new OracleDBM();
    }

    public String getName()
    {
        return p_name;
    }

    public void setName(String name)
    {
        p_name = name;
    }

	public String getEscapeCharStart() {
		return "\"";
	}

	public String getEscapeCharEnd() {
		// TODO Auto-generated method stub
		return "\"";
	}

    public long getDBSequence(Connection cn, String seqname, int operation) {
	try {
		if (cn.getAutoCommit()==true) 
			cn.setAutoCommit(false);
  
		long ret = 0;
		String seqFullTableName = "";
			
		String sql;
		if( operation == SEQUENCE_NEXTVAL )
			sql = "SELECT "+seqname+".nextval FROM DUAL";
		else
			sql = "SELECT "+seqname+".currval FROM DUAL";
				
  
		  PreparedStatement pstm = null;
		  try {
			  ResultSet rslt = (pstm=cn.prepareStatement(sql)).executeQuery();
			  if(rslt.next()) {
			      ret = rslt.getLong(1);
			
			      rslt.close();
			      pstm.close();
			      return ret; 
			  }
			  else {
			  	rslt.close();
			  	pstm.close();
			  	throw(new SQLException("Erro a obter sequencia ["+seqname+"]"));
			  }
		  } 
		  catch (SQLException e) 
		  {
			  if(e.getMessage().indexOf("08002")==-1) {
		  pstm.close();              
		  pstm = cn.prepareStatement("CREATE SEQUENCE "+seqname+" CACHE 20 NOCYCLE ORDER");
			  pstm.execute();
		      pstm.close();
		  }
		  if( operation == Driver.SEQUENCE_NEXTVAL ) {
			  pstm = cn.prepareStatement("SELECT "+seqname+".nextval FROM DUAL");
			  pstm.execute();
		      pstm.close();
		  }
		  ResultSet rslt = (pstm=cn.prepareStatement(sql)).executeQuery();
		  if(rslt.next()) {
		      ret = rslt.getLong(1);
		      //cn.commit();
		      rslt.close();
		      pstm.close();
		      return ret;
		  }
		  else {
		      rslt.close();
		      pstm.close();
		      throw(new SQLException("Erro a obter sequencia ["+seqname+"]"));
			  }
		  }
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    }

    public String getDatabaseTimeConstant() {
		return "SYSDATE";
	}

}
