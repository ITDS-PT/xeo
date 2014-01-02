/*Enconding=UTF-8*/
package netgest.bo.system.login;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.LoginManager;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boRepository;
import netgest.utils.MD5Utils;

public class boDefaultLogin implements LoginManager
{
  public boDefaultLogin()
  {
  }
  
  public long boLogin( boApplication app, String repository , String username, long time, long timeCheck, HttpServletRequest request) throws boLoginException
  {
    //not implemented
    throw new boLoginException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
  }
  
  public long boLogin(boApplication app, String repository,  String username,String password,HttpServletRequest request) throws boLoginException
  {
        
        Connection cn			= null;
        ResultSet rslt			= null;
        PreparedStatement pstm	= null;
        long toReturn			= 0;
        EboContext ctx			= null;
        
        String md5pass = MD5Utils.toHexMD5(password);
        String encryptedMd5Password = MD5Utils.toHexMD5(LoginUtil.encryptPassword(password));
        
        try {
              
              boRepository rep = boRepository.getRepository( app , app.getDefaultRepositoryName()  );
              cn = rep.getConnection();
              QLParser qp = new QLParser();
              
              if( !password.equals(boLoginBean.getSystemKey() ) ){
            	  pstm = cn.prepareStatement(qp.toSql("SELECT iXEOUser WHERE USERNAME=? AND PASSWORD =?" , null ));
            	  toReturn = findUserWithPasswords( pstm, username, encryptedMd5Password, md5pass );
              }
              else {
            	  pstm = cn.prepareStatement(qp.toSql("SELECT iXEOUser WHERE USERNAME=?" , null ));
            	  toReturn = findUser(pstm, username);
              }
        }
        catch (SQLException e) {
            throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);
        }
        finally {
            closeDatabaseResources(cn, pstm, rslt);
            if(ctx !=null) 
            	ctx.close();
            
        }        
        return toReturn;
    }
  
  
  	/**
  	 * 
  	 * Attempts to login a user with a set of passwords, if none is valid throw an exception
  	 * 
  	 * @param loginStmt The statement to use to attempt the login
  	 * @param username The username
  	 * @param passwords The list of passwords to attempt
  	 * 
  	 * @return The identifier of the user
  	 * 
  	 * @throws boLoginException If login is invalid or and SQL exception occurs
  	 */
  	private long findUserWithPasswords(PreparedStatement loginStmt, String username, String... passwords) throws boLoginException{
  		
  		ResultSet rslt			= null;
  		boolean canLogin 		= false;
  		long bouiOfUser 		= 0;
  		try {
	  		for (String password : passwords){
	  			
  				loginStmt.setString(1,username);
				loginStmt.setString(2,password);
				
				rslt = loginStmt.executeQuery();
	  	        if(rslt.next()) {
	  	            bouiOfUser = rslt.getLong(1);
	  	            canLogin = true;
	  	            break;
	  	        } 
  			}
  		} 
		catch (SQLException e) {
			throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);
		} finally {
			closeDatabaseResources(null, loginStmt, rslt);
		}
  		
  		if (canLogin)
  			return bouiOfUser;
  		else
  			throw new boLoginException(boLoginException.INVALID_CREDENCIALS);
  		
  	}
  	
  	/**
  	 * 
  	 * Finds a user with a given username 
  	 * 
  	 * @param findUserStms The statement to find the user
  	 * @param username The username to find
  	 * @return The boui of the user or 0 if the user is not found
  	 * @throws boLoginException If the user is not found or the database cannot be queried
  	 */
  	private long findUser(PreparedStatement findUserStms, String username) throws boLoginException{
  		
  		ResultSet rslt			= null;
  		boolean canLogin 		= false;
  		long bouiOfUser 		= 0;
  		
		try {
			findUserStms.setString(1,username);
			rslt = findUserStms.executeQuery();
  	        if(rslt.next()) {
  	            bouiOfUser = rslt.getLong(1);
  	            canLogin = true;
  	        } 
		} 
		catch (SQLException e) {
			throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);
		}
		finally {
			closeDatabaseResources(null, findUserStms, rslt);
		}
  		
  		if (canLogin)
  			return bouiOfUser;
  		else
  			throw new boLoginException(boLoginException.INVALID_CREDENCIALS);
  		
  	}
  	
  	/**
  	 * 
  	 * Close database resources (if any of them exist)
  	 * 
  	 * @param connection The db connection (can be null)
  	 * @param stmt The statement (can be null)
  	 * @param result The result set (can be null(
  	 */
  	private void closeDatabaseResources(Connection connection, Statement stmt, ResultSet result){
  		try {
            if(result!=null) 
            	result.close();
        }
        catch (Exception e) {  
        	// Doesn't do anything continue to release resources;
        }
        try {
            if(stmt!=null) 
            	stmt.close();
        }
        catch (Exception e) {   
        	// Doesn't do anything continue to release resources;
        }
        try {
            if(connection!=null) 
            	connection.close();
        }
        catch (Exception e) {  
        	// Doesn't do anything continue to release resources;
        }
  	}
  
}