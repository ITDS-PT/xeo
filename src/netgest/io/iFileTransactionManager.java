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

/**
 * 
 * 
 * The {@link iFileTransactionManager} is an auxiliary class to deal with the Commit/Rollback of files saved
 * in XEO transaction. {@link iFileTransactionManager#registerIFile(iFile, String, EboContext)} allows to register
 * an {@link iFile} to mark it as part of a transaction, and the {@link iFileTransactionManager#commitIFile(iFile, Connection)}
 * operation marks the file as successfully saved while the {@link iFileTransactionManager#rollbackIFile(String, String, Connection)}
 * operation is used to delete a file that was created, in order to maintain consistency
 * 
 * @author LuisBarreira
 * @author PedroRio
 *
 */
public class iFileTransactionManager extends Thread {

    /**
     * A reference to the {@link boApplication}
     */
    private boApplication p_app;
    /**
     * The logger for this class
     */
    private static Logger logger = Logger.getLogger("netgest.io.iFileTransactionManager");
    /**
     * The time between the consecutive executions of this thread
     */
    private static long WAIT_TIME = 15000;
    
    /**
     * The name of the database table where the {@link iFile} instances in transaction are kept
     * until they're committed
     */
    private static final String IFILE_TRANSACTION_MANAGER_TBL = "ifileTransaction"; 
    /**
     * 
     * Constructor for a new {@link iFileTransactionManager}
     * 
     * @param group
     * @param app The {@link boApplication}
     * @param name 
     */
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
			  				
			  	            String sqlQuery = "select FILEID where STATE='0' and INSERT_DATE<?";
			  				
			  				ps = ctx.getConnectionData().prepareStatement(sqlQuery);
			  				Calendar c = Calendar.getInstance();
			  				c.add(Calendar.MINUTE, -5);
			  				java.sql.Date sqlDate = new java.sql.Date(c.getTimeInMillis());
			  				ps.setDate(1, sqlDate);
			  				
			  				rs = ps.executeQuery();
			  				while(rs.next()) {
			  					String fileId = rs.getString(1);
			  					
			  					rollbackIFile(fileId, null, ctx);
			  				}
			  				
			  			} catch (SQLException e) {
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
		        	e.printStackTrace();
		        }
		        catch (Throwable e)
		        {
		            logger.severe( "Error cleaning iFile \n" +e.getMessage(), e );
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
    
    /* (non-Javadoc)
     * @see java.lang.Thread#start()
     */
    public synchronized void start()
    {
        super.start();
    }
    
	/**
	 * 
	 * Registers a given {@link iFile} as "in transaction" so that when the XEO commit operation fails, the files
	 * that must be deleted are known. The register process is inserting a entry in the Transaction
	 * 
	 * @param file The {@link iFile} to register
	 * @param connectorClass The fully qualified name of the java class that implements the {@link iFileConnector} interface
	 * that is responsible for creating correct instances of this {@link iFile} instance passed as parameter 
	 * @param ctx The {@link EboContext} for this {@link iFile}
	 * 
	 * @throws iFileException If something goes wrong registering the file in the database table
	 */
	public static void registerIFile(iFile file, String connectorClass, EboContext ctx) throws iFileException{
		
		long user = ctx.getBoSession().getPerformerBoui();
		String fileId = file.getId();
		Connection conn = ctx.getConnectionSystem();
		Driver driver = ctx.getBoSession().getRepository().getDriver();
		PreparedStatement ps = null;
		
		try {
			
			String sqlInsert = "INSERT INTO "+IFILE_TRANSACTION_MANAGER_TBL+" (FILEID, INSERT_DATE, AUTHOR, IFILECONNCLASS, STATE, ONCOMMIT, ONROLLBACK) values (?," +
								driver.getDatabaseTimeConstant() +
								",?,?,?,?,?)";
			
			ps = conn.prepareStatement(sqlInsert);
			ps.setString(1, fileId);
			ps.setLong(2, user);
			ps.setString(3, connectorClass);
			ps.setString(4, "0");
			String[] files2Delete = file.getFileToDeleteOnCommit();
			if(files2Delete!=null && files2Delete.length>0) {
				String oncommit = "";
				for (int i = 0; i < files2Delete.length; i++) 
					oncommit += files2Delete[i] + ";";
				ps.setString(5, oncommit);
			}
			else
				ps.setString(5, null);
			files2Delete = file.getFileToDeleteOnRollback();
			if(files2Delete!=null && files2Delete.length>0) {
				String onrollback = "";
				for (int i = 0; i < files2Delete.length; i++) 
					onrollback += files2Delete[i] + ";";
				ps.setString(6, onrollback);
			}
			else
				ps.setString(6, null);
			
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
					throw new iFileException(e);
				}
		}
	}
	
	/**
	 * 
	 * Commits a given {@link iFile}. Deletes the entry from the database table to mark the file as committed
	 * 
	 * @param file The {@link iFile} to commit
	 * @param conn The Database connection to issue the SQL query
	 * 
	 * @throws iFileException If something goes wrong with the SQL delete operation
	 */
	public static void commitIFile(iFile file, EboContext ctx) throws iFileException {
		
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			String sqlSelect = "SELECT IFILECONNCLASS, ONCOMMIT FROM "+ IFILE_TRANSACTION_MANAGER_TBL+" where FILEID = ?";
			ps = ctx.getConnectionData().prepareStatement(sqlSelect);
			ps.setString(1, file.getId());
			ps.executeQuery();
			
			rs = ps.getResultSet();
			if (rs.next()){

				String connectorClass = rs.getString(1);
				String oncommit = rs.getString(2);
				if(oncommit!=null) {
					iFileConnector fileConn = (iFileConnector)Class.forName(connectorClass).newInstance();
					String[] idToRemove = oncommit.split(";");
					for (int i = 0; i < idToRemove.length; i++)
						fileConn.deleteIFile(idToRemove[i]);					
				}				
				
				
				String sqlDelete = "DELETE "+ IFILE_TRANSACTION_MANAGER_TBL+" where FILEID = ?";
				
				ps2 = ctx.getConnectionData().prepareStatement(sqlDelete);
				ps2.setString(1, file.getId());				
				ps2.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (iFilePermissionDenied e) {
			e.printStackTrace();
		}
		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(ps2!=null)
				try {
					ps2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * 
	 * Rollback an {@link iFile} 
	 * 
	 * @param fileID The {@link iFile} identifier
	 * @param fileClass The fully qualified class implementing the {@link iFileConnector} for the kind of {@link iFile} passed
	 * as parameter
	 * @param conn a connection to the database
	 * 
	 * 
	 * @throws iFileException If any other thing goes wrong
	 */
	public static void rollbackIFile(String fileID, iFile ifile, EboContext ctx) throws iFileException {
		
		if(ifile!=null)
			ifile.rollback(ctx);
		if(fileID==null && ifile!=null)
			fileID = ifile.getId();
		
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			String sqlSelect = "SELECT IFILECONNCLASS, ONROLLBACK FROM "+ IFILE_TRANSACTION_MANAGER_TBL+" where STATE='0' AND FILEID = ?";
			ps = ctx.getConnectionData().prepareStatement(sqlSelect);
			ps.setString(1, fileID);
			ps.executeQuery();
			
			rs = ps.getResultSet();
			if (rs.next()){

				String connectorClass = rs.getString(1);
				String onrollback = rs.getString(2);
				if(onrollback!=null) {
					iFileConnector fileConn = (iFileConnector)Class.forName(connectorClass).newInstance();
					String[] idToRemove = onrollback.split(";");
					for (int i = 0; i < idToRemove.length; i++)
						fileConn.deleteIFile(idToRemove[i]);					
				}				
				
				
				String sqlUpdate = "UPDATE "+ IFILE_TRANSACTION_MANAGER_TBL+" set STATE='9' where FILEID = ?";
				
				ps2 = ctx.getConnectionData().prepareStatement(sqlUpdate);
				ps2.setString(1, fileID);				
				ps2.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (iFilePermissionDenied e) {
			e.printStackTrace();
		}
		finally {
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(ps2!=null)
				try {
					ps2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
}
