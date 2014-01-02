/*Enconding=UTF-8*/
package netgest.bo.data.postgre;

import netgest.bo.data.postgre.jdbc.PostGresConnection;

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
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boRepository;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class PostGreDriver implements Driver {

	private String p_ddlds;
    private String p_dmlds;
    private String p_name;
    
    /**
     *
     * @since
     */
    public PostGreDriver()
    {
    }

    public ReaderAdapter createReaderAdapter(EboContext ctx)
    {
        return new PostGreReaderAdapter(p_dmlds);
    }

    public WriterAdapter createWriterAdapter(EboContext ctx)
    {
        return new PostGreWriterAdapter(p_dmlds);
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
        return new PostGreUtils(p_dmlds);
    }

    private Connection getConnection(String dataSource, String username,
        String password)
    {
        final int retrycount = 5;
        int retries = 0;
        Connection ret = null;
        PreparedStatement ps = null;
        String schema=boRepository.getDefaultSchemaName(boApplication.getDefaultApplication());
        while (ret == null)
        {
            try
            {
                ret = getDataSource(dataSource).getConnection(username, password);
                if (!validateConnection(ret))
               	 ret.rollback();
                // IMBR
                 if (ret.getAutoCommit()) ret.setAutoCommit(false);
                 ps=ret.prepareStatement("set search_path to "+schema);
                 ps.execute();
                 ps.close();                
                 return new PostGresConnection(ret);
            }
            catch (SQLException e)
            {

            	if (ps!=null)
					try {
						ps.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                if (retries >= retrycount)
                {
                    throw new RuntimeException(MessageLocalizer.getMessage("FAILED_TO_CREATE_CONNECTION")+
                        " \n" +
                        e.getClass().getName() + "\n" + e.getMessage());
                }
            }

            retries++;
        }

        return new PostGresConnection(ret);
    }

    private  final Connection getConnection(String dataSource)
    {
        final int retrycount = 5;
        int retries = 0;
        Connection ret = null;
        PreparedStatement ps = null;
        String schema=boRepository.getDefaultSchemaName(boApplication.getDefaultApplication());
        while (ret == null)
        {
            try
            {
                ret = getDataSource(dataSource).getConnection(); 
                if (!validateConnection(ret))
               	 ret.rollback();
                // IMBR
                if (ret.getAutoCommit()) ret.setAutoCommit(false);                                
                ps=ret.prepareStatement("set search_path to "+schema);
                ps.execute();
                ps.close();
                return new PostGresConnection(ret);
            }
            catch (SQLException e)
            {
            	if (ps!=null)
					try {
						ps.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
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

        return new PostGresConnection(ret);
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
            if (cn.getAutoCommit()) cn.setAutoCommit(false);
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
            if (cn.getAutoCommit()) cn.setAutoCommit(false);
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
        return new PostGreDBM();
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
		cn = ctx.getDedicatedConnectionData();
		if (cn.getAutoCommit()) 
			cn.setAutoCommit(false);
  
		long ret = 0;
			
		String sql;
		if( operation == SEQUENCE_NEXTVAL )
			sql = "SELECT  nextval('"+seqname+"')";
		else
			sql = "SELECT last_value from "+seqname;
				
  
		  PreparedStatement pstm = null;
		  try {
			  ResultSet rslt = (pstm=cn.prepareStatement(sql)).executeQuery();
			  if(rslt.next()) {
			      ret = rslt.getLong(1);
			
			      rslt.close();
			      pstm.close();
			      cn.close();
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
			  cn.rollback();
			  cn.close();
			  if(e.getMessage().indexOf("08002")==-1) {
				  pstm.close();     
				  Connection cnded = null;
				  try {
					  cnded = ctx.getDedicatedConnectionData();	
					  pstm = cnded.prepareStatement("CREATE SEQUENCE "+seqname+" CACHE 20 NO CYCLE");
					  pstm.execute();
					  cnded.commit();
					  pstm.close();
					  ResultSet rslt = (pstm=cnded.prepareStatement(sql)).executeQuery();
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
				  
				  finally {
					  if (cn!=null) cn.close();
					  if( cnded != null ) 
						  cnded.close();
				  }
			  }
		  }
		  if (cn!=null) cn.close();
		  return ret;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    }

    public String getDatabaseTimeConstant() {
		return "NOW()";
	}
      
    public boolean validateConnection(Connection cn)
    {
    	boolean toRet=true;
    	PreparedStatement ps=null;
    	ResultSet rs=null;
    	String schema=boRepository.getDefaultSchemaName(boApplication.getDefaultApplication());
    	try {
    		ps=cn.prepareStatement("select 1");
    		rs=ps.executeQuery();
    		ps.close();
    		ps=cn.prepareStatement("set search_path to "+schema);
            ps.execute();
		} catch (SQLException e) {
			toRet=false;
		}
    	finally{
    		try {
	    		if (rs!=null) rs.close();					
	    		if (ps!=null) ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    	return toRet;
    }
}
