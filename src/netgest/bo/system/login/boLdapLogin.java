/*Enconding=UTF-8*/
package netgest.bo.system.login;
import java.util.Properties;
import java.util.Vector;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.boConfig;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.ObjAttHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.LoginManager;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import netgest.utils.Ldap.LdapConnection;
import netgest.utils.Ldap.LdapUser;

public class boLdapLogin implements LoginManager
{
  public boLdapLogin()
  {
  }
  public long boLogin( boApplication app, String repository , String username, long time, long timeCheck, HttpServletRequest request) throws boLoginException
  {
    //not implemented
    throw new boLoginException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
  }
  public long boLogin(boApplication app, String repository, String username ,String password , HttpServletRequest request) throws boLoginException
  {
    long toReturn=0;
    Properties prop=boConfig.getAuthentication();
    LdapConnection ldapconn=null;
    try
    {
      ldapconn=new LdapConnection();          
      boolean usesso=new Boolean((String)prop.getProperty("usesso")).booleanValue();
      if (usesso)
      {
        if (request.getRemoteUser() != null &&  request.getRemoteUser().length()>0 &&
              request.getHeader("Osso-User-Dn").length()>0 && 
              request.getHeader("Osso-User-Guid").length()>0 ) {       
              username = request.getRemoteUser();
        }
        ldapconn.LdapConnect((String)prop.getProperty("ldapadmin"),(String)prop.getProperty("ldapadminpassword"),true);          
      }
      else ldapconn.LdapConnect(username,password,true);        
      toReturn=this.createOrUpdateEBOPerf(app, repository ,username,password,ldapconn);
      return toReturn;
    }
    catch (AuthenticationException e)
    {
      throw new boLoginException(boLoginException.INVALID_CREDENCIALS);
    }
    finally
    {
      try
      {
        ldapconn.close();
      }
      catch (NamingException e)
      {
        throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);                        
      }          
    }
  }
  
  private long createOrUpdateEBOPerf(boApplication app, String repository, String username,String password,LdapConnection ldapconn) throws boLoginException
  {
    EboContext ctx=null;
    try
    {      
      boSession session = app.boLogin( "SYSTEM", boLoginBean.getSystemKey(), repository );
      ctx = session.createRequestContext(null,null,null);  
      
      boObject newuser = boObject.getBoManager().loadObject(ctx,"SELECT iXEOUser WHERE username = '"+username.toUpperCase()+"'");
      if (!newuser.exists()) newuser=boObject.getBoManager().createObject(ctx,"Ebo_Perf");
      LdapUser ldapuser=ldapconn.getLdapUser(username);
      
      //create user Groups
      Vector groups=ldapuser.getUserGroups();
      for (int i=0;i<groups.size();i++)
      {
        String currgroup=(String)groups.get(i);
        boObject group=boObject.getBoManager().loadObject(ctx,"Ebo_Group","ID='"+currgroup.toUpperCase()+"'");
        if (!group.exists())
        {
          group=newuser.getBridge("groups").addNewObject();
          group.getAttribute("id").setValueString(currgroup.toUpperCase());
          group.getAttribute("name").setValueString(currgroup.toUpperCase());
        }
        else
        {
          if (!newuser.getBridge("groups").haveBoui(group.getBoui())) 
            newuser.getBridge("groups").add(group.getBoui());
        }
      }
      //boObject groups=newuser.getBridge("groups").addNewObject();
      
      Attribute auxatr=ldapuser.getAttributeRef("cn");
      newuser.getAttribute("username").setValueString(auxatr==null?"":((String)auxatr.get()).toUpperCase());      
      newuser.getAttribute("password").setValueString(password);
      auxatr=ldapuser.getAttributeRef("givenname");
      newuser.getAttribute("name").setValueString(auxatr==null?"":(String)auxatr.get());
      auxatr=ldapuser.getAttributeRef("mail");
      newuser.getAttribute("email").setValueString(auxatr==null?"":(String)auxatr.get());
      auxatr=ldapuser.getAttributeRef("middleName");
      newuser.getAttribute("lastname").setValueString(auxatr==null?"":(String)auxatr.get());      
      //Morada      
      boObject address=((ObjAttHandler)newuser.getAttribute("home_address")).edit().getObject();
      auxatr=ldapuser.getAttributeRef("street");
      address.getAttribute("rua").setValueString(auxatr==null?"":(String)auxatr.get());
      auxatr=ldapuser.getAttributeRef("telephoneNumber");
      address.getAttribute("telefone").setValueString(auxatr==null?"":(String)auxatr.get());
      auxatr=ldapuser.getAttributeRef("st");
      address.getAttribute("cpostal").setValueString(auxatr==null?"":(String)auxatr.get());
      auxatr=ldapuser.getAttributeRef("l");
      address.getAttribute("localcpostal").setValueString(auxatr==null?"":(String)auxatr.get());
      auxatr=ldapuser.getAttributeRef("l");
      address.getAttribute("localidade").setValueString(auxatr==null?"":(String)auxatr.get());
      newuser.getAttribute("home_address").setValueLong(address.bo_boui);          
      newuser.update();          
      
      session.closeSession();
      
      return newuser.bo_boui;      
    }   
    catch(NamingException e)
    {
      throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);             
    }
    catch(boLoginException e)
    {
       throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);             
    }                
    catch(boRuntimeException e)
    {
      throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);              
    }          
    finally
    {
       ctx.close();
    }   
  } 
}