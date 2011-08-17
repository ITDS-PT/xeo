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
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.system.spy.XEOSpyConnection;


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
    
    private static final boolean spyConnections = false; 

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
                
                if( spyConnections )
                	ret = new XEOSpyConnection(ret);
                
                // IMBR
                // ret.setAutoCommit(false);
                return ret;
            }
            catch (SQLException e)
            {
                if (retries >= retrycount)
                {
                    throw new RuntimeException(MessageLocalizer.getMessage("FAILED_TO_CREATE_CONNECTION")+
                        " \n" +
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
                if( spyConnections )
                	ret = new XEOSpyConnection(ret);
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
                    		MessageLocalizer.getMessage("FAILED_TO_CREATE_CONNECTION")+" \n" +
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
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_LOOKING_FOR_DATASOURCE_NAME")+" [" +
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
            System.out.println(MessageLocalizer.getMessage("FAILED_TO_OBTAIN_DEDICATED_CONNECTION_OR_DISABLE_AUTOCOMIT"));
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
            System.out.println(MessageLocalizer.getMessage("FAILED_TO_OBTAIN_DEDICATED_CONNECTION_OR_DISABLE_AUTOCOMIT"));
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

    public long getDBSequence(EboContext ctx, String seqname, int dsType, int operation) {
	try {
		Connection cn;
		switch( dsType ) {
			case Driver.SEQUENCE_SYSTEMDS:
				cn = ctx.getConnectionSystem();
				break;
			default:
				cn = ctx.getConnectionData();
				break;
		}
		
		if (cn.getAutoCommit()==true) 
			cn.setAutoCommit(false);
  
		long ret = 0;
			
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
			  	throw(new SQLException(MessageLocalizer.getMessage("ERROR_OBTAINING_SEQUENCE")+" ["+seqname+"]"));
			  }
		  } 
		  catch (SQLException e) 
		  {
			  if(e.getMessage().indexOf("08002")==-1) {
				  pstm.close();     
				  Connection cnded = null;
				  try {
					  switch( dsType ) {
						case Driver.SEQUENCE_SYSTEMDS:
							cnded = ctx.getConnectionManager().getSystemDedicatedConnection();
							break;
						default:
							cnded = ctx.getDedicatedConnectionData();
							break;
				      }
					  cnded = ctx.getDedicatedConnectionData();
					  pstm = cnded.prepareStatement("CREATE SEQUENCE "+seqname+" CACHE 20 NOCYCLE ORDER");
					  pstm.execute();
					  pstm.close();
				  }
				  finally {
					  if( cnded != null ) 
						  cnded.close();
				  }
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
			      throw(new SQLException(MessageLocalizer.getMessage("ERROR_OBTAINING_SEQUENCE")+" ["+seqname+"]"));
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
