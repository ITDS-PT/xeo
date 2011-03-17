/*Enconding=UTF-8*/
package netgest.bo.system.login;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.*;

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
        final InitialContext ic;
        Connection cn=null;
        ResultSet rslt=null;
        PreparedStatement pstm=null;
        long toReturn=0;
        EboContext ctx=null;
        String md5pass = MD5Utils.toHexMD5(password);
        try {
              ic = new InitialContext();
              
              String repos=app.getDefaultRepositoryName();
              boRepository rep = boRepository.getRepository( app , app.getDefaultRepositoryName()  );
              cn = rep.getConnection();
              QLParser qp = new QLParser();
              
              if( !password.equals(boLoginBean.getSystemKey() ) )
              {
                  pstm = cn.prepareStatement(qp.toSql("SELECT iXEOUser WHERE USERNAME=? AND PASSWORD=?" , null ));
                  pstm.setString(1,username);
                  pstm.setString(2,md5pass);
              }
              else
              {
                  pstm = cn.prepareStatement(qp.toSql("SELECT iXEOUser WHERE USERNAME=?" , null ));
                  pstm.setString(1,username);
              }
              rslt = pstm.executeQuery();
              if(rslt.next()) {
                  long perfboui = rslt.getLong(1);
                  toReturn = perfboui;
              } else {
                  throw new boLoginException(boLoginException.INVALID_CREDENCIALS);
              }
        }
        catch (SQLException e) {
            throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);
        }
        catch (NamingException e) {
            throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);
        }
        finally {
            try {
                if(rslt!=null) rslt.close();
            }
            catch (Exception e) {
                // Doesn't do anything continue to realease resources;
            }
            try {
                if(pstm!=null) pstm.close();
            }
            catch (Exception e) {
                // Doesn't do anything continue to realease resources;
            }
            try {
                if(cn!=null) cn.close();
            }
            catch (Exception e) {
                // Doesn't do anything continue to realease resources;
            }
            if(ctx !=null) ctx.close();
            
        }        
        return toReturn;
    }
}