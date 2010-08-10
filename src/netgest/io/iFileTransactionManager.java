package netgest.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import netgest.bo.data.Driver;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;

public class iFileTransactionManager extends Thread {

    private boApplication p_app;
    private static Logger logger = Logger.getLogger("netgest.io.iFileTransactionManager");
    private static long WAIT_TIME = 15000;
    
    public iFileTransactionManager(ThreadGroup group, boApplication app, String name )
    {
        super( group , "iFile Transaction Manager Rollback Thread" );
        p_app  = app;
    }

    public void run()
    { 
    	  EboContext ctx = null;
          boSession session = null;
          try 
          {
          	if( boDefHandler.getBoDefinition( "iXEOUser" ) != null ) {
  	            session =  p_app.boLogin( "SYSTEM", boLoginBean.getSystemKey(), p_app.getDefaultRepositoryName() );
  	            ctx = session.createRequestContext(null,null,null);
  	            
	  	        try
	  	        {
	  	            while( !super.isInterrupted() )
	  	            {  	            
	  	            
			  	        PreparedStatement ps = null;
			  			ResultSet rs = null;
			  			try {
			  				
			  	            String sqlQuery = "select FILEID, IFILECONNCLASS where STATE='0' and INSERT_DATE<?";
			  				
			  				ps = ctx.getConnectionData().prepareStatement(sqlQuery);
			  				Calendar c = Calendar.getInstance();
			  				c.add(Calendar.MINUTE, -5);
			  				java.sql.Date sqlDate = new java.sql.Date(c.getTimeInMillis());
			  				ps.setDate(1, sqlDate);
			  				
			  				rs = ps.executeQuery();
			  				while(rs.next()) {
			  					String fileId = rs.getString(1);
			  					String fileClass = rs.getString(2);
			  					
			  					rollbackIFile(fileId, fileClass, ctx.getConnectionData());
			  				}
			  				
			  			} catch (SQLException e) {
			                logger.severe(e.getMessage());
			  			} catch (InstantiationException e) {
			                logger.severe(e.getMessage());
						} catch (IllegalAccessException e) {
			                logger.severe(e.getMessage());
						} catch (ClassNotFoundException e) {
			                logger.severe(e.getMessage());
						} catch (iFilePermissionDenied e) {
			                logger.severe(e.getMessage());
						} catch (iFileException e) {
			                logger.severe(e.getMessage());
						}
			  			finally {
			  				if(ps!=null)
			  					try {
			  						ps.close();
			  					} catch (SQLException e) {
			  		                logger.severe(e.getMessage());
			  					}
			  				if(rs!=null)
			  					try {
			  					rs.close();
			  					} catch (SQLException e) {
			  		                logger.severe(e.getMessage());
			  					}
			  			}
	  	            
		                sleep( WAIT_TIME );
		                
	  	            }
		        }
		        catch (InterruptedException e)
		        {
		        }
		        catch (Throwable e)
		        {
		            logger.severe( "Error cleaning iFile \n"+e.getMessage(), e );
		        }
		        finally
		        {                  
		        }
  				
  				
          	}
          }
          catch (boLoginException e) {        
              logger.severe(e.getMessage());
          }
          finally
          {  
          	if( ctx != null )
          		ctx.close();
          	if( session != null )
          		session.closeSession();
          }
    }
    
    public synchronized void start()
    {
        super.start();
    }
    
	public static void registerIFile(iFile file, String connectorClass, EboContext ctx) throws iFileException{
		
		long user = ctx.getBoSession().getPerformerBoui();
		String fileId = file.getId();
		Connection conn = ctx.getConnectionSystem();
		Driver driver = ctx.getBoSession().getRepository().getDriver();
		PreparedStatement ps = null;
		
		try {
			
			String sqlInsert = "insert into ifileTransaction (FILEID, INSERT_DATE, AUTHOR, IFILECONNCLASS, STATE) values (?," +
								driver.getDatabaseTimeConstant() +
								",?,?,?)";
			
			ps = conn.prepareStatement(sqlInsert);
			ps.setString(1, fileId);
			ps.setLong(2, user);
			ps.setString(3, connectorClass);
			ps.setString(4, "0");
			
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new iFileException(e);
		}
		finally {
			if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static void commitIFile(iFile file, Connection conn) throws iFileException {
		String fileId = file.getId();
		PreparedStatement ps = null;
		try {
			
			String sqlInsert = "delete ifileTransaction where FILEID = ?";
			
			ps = conn.prepareStatement(sqlInsert);
			ps.setString(1, fileId);
			
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new iFileException(e);
		}
		finally {
			if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static void rollbackIFile(String fileID, String fileClass, Connection conn) throws InstantiationException, IllegalAccessException, ClassNotFoundException, iFilePermissionDenied, iFileException {
		
		iFileConnector fileConn = (iFileConnector)Class.forName(fileClass).newInstance();
		fileConn.deleteIFile(fileID);
	}
}
