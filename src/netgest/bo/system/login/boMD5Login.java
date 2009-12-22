/*Enconding=UTF-8*/
package netgest.bo.system.login;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.LoginManager;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boRepository;

import netgest.utils.MD5Utils;
import netgest.bo.system.Logger;

public class boMD5Login implements LoginManager
{
    
  private static final int SECONDS_GAP = 10; //segundos
  //logger
  private static Logger logger = Logger.getLogger("netgest.bo.runtime.boObjectList");
  
  public boMD5Login()
  {
  }
  public long boLogin(boApplication app, String repository,  String username,String password,HttpServletRequest request) throws boLoginException
  {
    //not implemented
    throw new boLoginException("Not implemented");
  }
  public long boLogin(boApplication app, String repository, String username, long time, long timeCheck, HttpServletRequest request) throws boLoginException
  {
        final InitialContext ic;
        Connection cn=null;
        ResultSet rslt=null;
        PreparedStatement pstm=null;
        long toReturn=0;
        EboContext ctx=null;
        Calendar c;
        long min_time;
        long max_time;
        try {
              SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS");              
              c = Calendar.getInstance();
              logger.finer("Tempo de calculo ->" + c.getTimeInMillis() + ":" +formatter.format(new Date(c.getTimeInMillis())));
              Calendar auxC = (Calendar)c.clone();
              auxC.add(Calendar.SECOND, SECONDS_GAP);
              max_time =  auxC.getTimeInMillis();
              logger.finer("Tempo de calculo (MAX)->" + max_time+ ":" +formatter.format(new Date(max_time)));
              c.add(Calendar.SECOND, -SECONDS_GAP);
              min_time =  c.getTimeInMillis();
              logger.finer("Tempo de calculo (MIN)->" + min_time+ ":" +formatter.format(new Date(min_time)));
              long timeCheckCalc = MD5Utils.getCheckTime(time); 
              logger.finer("Tempo enviado ->" + time + ":" +formatter.format(new Date(time)));
              logger.finer("Tempo Check enviado ->" + timeCheck);
              
              logger.finer("Tempo Check calculado ->" + timeCheckCalc);
              
              
              if(MD5Utils.getCheckTime(time) == timeCheck &&  time>=min_time && time<=max_time)
              {
                  ic = new InitialContext();
                  String repos=app.getDefaultRepositoryName();
                  boRepository rep = boRepository.getRepository( app , app.getDefaultRepositoryName()  );
                  cn = rep.getConnection();
                  QLParser qp = new QLParser();
                  pstm = cn.prepareStatement(qp.toSql("SELECT iXEOUser WHERE upper(MD5Code) = upper(?)" , null ));
                  pstm.setString(1,username);
                  rslt = pstm.executeQuery();
                  if(rslt.next()) 
                  {
                    logger.finer("Encontrou o Utilizador");
                    toReturn = rslt.getLong(1);
                  }
                  else 
                  {
                    logger.severe("Não encontrou o Utilizador");
                    throw new boLoginException(boLoginException.INVALID_CREDENCIALS);
                  }
              }
              else 
              {
                logger.severe("Não passou do if");
                throw new boLoginException(boLoginException.INVALID_CREDENCIALS);
              }
        }
        catch (SQLException e) {
            logger.severe(e);
            throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);
        }
        catch (NamingException e) {
            logger.severe(e);
            throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);
        }
        catch (Exception e) {
            logger.severe(e);
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