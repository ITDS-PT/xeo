/*Enconding=UTF-8*/
package netgest.xwf.core;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.sql.SQLException;
import java.util.*;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.def.*;
import netgest.bo.message.MessageServer;
import netgest.bo.runtime.*;
import netgest.bo.runtime.boAttributesArray;
import netgest.io.*;
import netgest.utils.*;
import netgest.xwf.common.xwfBoManager;


/**
 * Classe responsável pela gestão dos anuncios gerados pelo lanaçamento de tarefas. 
 * @author Ricardo Andrade
 */
public abstract class xwfAnnounceImpl extends boObject
{   
    public final static byte MY_MESSAGES=1;
    public final static byte MY_GROUP_MESSAGES=2;
    public final static byte MY_ROLE_MESSAGES=4;
    public final static byte MY_WORKQUEUE_MESSAGES=8;
    
    
    public void init() throws boRuntimeException
    {
        // TODO:  Override this netgest.bo.runtime.boObject method
        super.init();


    }
    
    
    public boolean onBeforeSave( boEvent event ) throws boRuntimeException
    {
            
        
        return true;

   }
   public void onAfterSave( boEvent event ) throws boRuntimeException
    {
            
    
       
        

   }

    public void poolObjectPassivate()
    {
        // TODO:  Override this netgest.bo.runtime.boObject method
        super.poolObjectPassivate();
        
    }
    
    public static void addAnnounce( boObject assignedQueue , boObject referenceObj , String message )
    {

        
    }
    public static void addAnnounce(String message, boObject user, boObject actv, xwfBoManager xm)throws boRuntimeException
    {
        addAnnounce(message, user, actv, xm,false);
    }
    public static void addAnnounce(String message, boObject user, boObject actv, xwfBoManager xm, boolean error)throws boRuntimeException
    {
        addAnnounce(message, user, actv, xm, error, null);
    }
    public static void addAnnounce(String message, boObject user, boObject actv, xwfBoManager xm, boolean error, boObject objMessage)throws boRuntimeException
    {
        boolean add=true;
        if(!error)
        {
            long assignedBoui= user.getBoui();
            boObject performer = xm.getObject(xm.getPerformerBoui());
            if ( xm.getPerformerBoui() == assignedBoui ||
                 performer.getBridge("groups").haveBoui( assignedBoui  ) ||
                 performer.getBridge("roles").haveBoui( assignedBoui) ||
                 performer.getBridge("queues").haveBoui( assignedBoui) )
                 {
                     add=false;
                 }
        }
        
        if ( add )
        {
            
            boObjectList announceList= xm.listObject("select xwfAnnounce where assignedQueue="+user.getBoui());
            announceList.beforeFirst();
            boObject Announce = null;
            if(announceList.next())
            {
                Announce = announceList.getObject();
            }
            else
            {
                Announce = xm.createObject("xwfAnnounce");
            }
            bridgeHandler b= Announce.getBridge("details");
            b.beforeFirst();
            boolean found=false;
            while ( !found &&!b.isEmpty() && b.next() )
            {
            
               boObject refo =null;  
               try
               {
                   refo = b.getObject().getAttribute("referenceObject").getObject();
               }
               catch (Exception e)
               {
                   b.remove();
               }
               if ( refo != null && refo == actv )
               {
                   found=true;
               }
            
            }
            
            String oldMessage ="";
            boObject adet=null;
            if ( !found )
            {
                adet = b.addNewObject();
                adet.getAttribute("referenceObject").setValueLong( actv.getBoui() );
                adet.getAttribute("assignedQueue").setValueLong( user.getBoui() ); 
    //            Announce.setChanged(true );
            }
            else
            {
                adet=b.getObject();
                oldMessage=adet.getAttribute( "message" ).getValueString()+"\n" ;
            }
            
            String no_message = oldMessage+ message;
            if(no_message!= null && no_message.length() > 4000)
            {
                no_message = no_message.substring(0,3999);
            }
            adet.getAttribute("message").setValueString( no_message );
            if(objMessage != null)
            {
                adet.getAttribute("objMessage").setObject(objMessage);
            }
            
            Announce.getAttribute("assignedQueue").setValueLong( user.getBoui() );
    //        actv.getUpdateQueue().add( Announce , boObjectUpdateQueue.MODE_SAVE_FORCED );
            xm.updateObject(Announce);
    //        afterAdd(Announce, xm);
        }
        
                   
    }
    
    private static void afterAdd(boObject ann, xwfBoManager xm)throws boRuntimeException
    {
            bridgeHandler b=ann.getBridge("details");
            
            boolean found=false;
            b.beforeFirst();
            StringBuffer message= new StringBuffer();
            
            while ( !found && b.next() )
            {
                found=true;
            }
            
            if ( found && ann.getAttribute("assignedQueue").getObject().getAttribute("notifica").getValueString().equals("1")  )
            {
                boObject mail=null;
                long tmpl=ann.getAttribute("assignedQueue").getObject().getAttribute("templateEmail").getValueLong();
                boolean isTemplate=false;
                if ( tmpl > 0 )
                {
//                    mail=ann.getAttribute("assignedQueue").getObject().getAttribute("templateEmail").getObject();
//                    Ebo_TemplateImpl xtmpl = ((Ebo_TemplateImpl)mail); 
//                    mail = xtmpl.loadTemplate();
//                    xtmpl.processTemplate( null , mail );
//                    isTemplate=true;
                
                }
                else  mail= ann.getBoManager().createObject(ann.getEboContext(),"message");
                
                
                    
//                message= new StringBuffer();
/*
                bridgeHandler bRO=mail.getBridge("RO");
                b.beforeFirst();
                while ( b.next() )
                {
                    message.append("<li>").append(b.getObject().getAttribute("message").getValueString()).append(" <b>").append(b.getObject().getAttribute("referenceObject").getObject().getTextCARDID()).append("</b></li>") ; 
                    bRO.add( b.getObject().getAttribute("referenceObject").getValueLong() );
                }
            

*/
               
                boObject  mailto=ann.getAttribute("assignedQueue").getObject();
                
                mail.getBridge("from").add( ann.getEboContext().getBoSession().getPerformerBoui()  );
                
//                boObject assigned=mail.getAttribute("assignedQueue").getObject();
                 
                mail.getBridge("to").add( mailto.getBoui() );
                if(isTemplate)
                {
                    String xmsg=mail.getAttribute("description").getValueString();
                    if ( xmsg.indexOf("#BODY#") > -1 )
                    {
                        mail.getAttribute("description").setValueString( xmsg.replaceAll("#BODY#", message.toString() ) );
                    }
                    else  mail.getAttribute("description").setValueString( message.toString());
                }
                else
                {
                    mail.getAttribute("name").setValueString("Notificação do SGIS");
                    mail.getAttribute("description").setValueString( ann.getAttribute("message").getValueString() );    
                }
//                mail.getAttribute("performer").setValueLong( this.getEboContext().getBoSession().getPerformerBoui()  );
//                mail.getAttribute("beginDate").setValueDate( new java.util.Date() );
//                mail.getAttribute("endDate").setValueDate( new java.util.Date() );
//                mail.getStateAttributes().get("primaryState").setValueString("close");
//                mail.getStateAttributes().get("closeStatus").setValueString("softclose");
                mail.update();
                
                
//                String address1=mailto.getAttribute("email").getValueString();
//                String address2=assigned.getAttribute("email").getValueString();
//                
//                if ( address1!=null && !address1.equals("") && address2!=null && !address2.equals("") )
//                {
//                    Method ometh = mail.getClass().getMethod("send",new Class[0]);
//                    ometh.invoke( mail ,new Object[0]);    
//                }

//                MessageServer.deliverMessage(new XwfController(xm.getDocHTML(), ""+xm.getProgBoui()), mail);
                  xwfMessage.sendMessage(mail);  
            }
    }
    
    public static void removeAnnouncer( boObject object ) throws boRuntimeException
    {
    
        long bouitorem = object.getBoui(); 
        String boql = "SELECT xwfAnnounce where details.referenceObject="+bouitorem;
        boObjectList bolist =  boObjectList.list( object.getEboContext(), boql );

        bolist.beforeFirst();
        while( bolist.next() )
        {
            boObject ann  = bolist.getObject();
            if( ann != null )
            {
                bridgeHandler bridge = ann.getBridge("details");
                bridge.beforeFirst();
                while (  !bridge.isEmpty() && bridge.next() )
                {
                    if ( bridge.getObject().getAttribute("referenceObject").getValueLong() == bouitorem  )
                    {
                        bridge.remove();
                    }
                }
                object.getUpdateQueue().add( ann, boObjectUpdateQueue.MODE_SAVE_FORCED );
                //ann.update();
            }
            
        }
        
    }

    public static void removeAnnounces( boObject performer ,byte type ) throws boRuntimeException
    {
      String[] XEOUSER_Objs = XEOUserUtils.getXEOUserObjectsNames();
      String[] objNames = new String[XEOUSER_Objs.length + 3];
      int i = 0;
      for (; i < XEOUSER_Objs.length; i++) 
      {
        objNames[i] = XEOUSER_Objs[i];
      }
      objNames[i] = "Ebo_Role";
      i++;
      objNames[i] = "Ebo_Group";
      i++;
      objNames[i] = "workQueue";
      removeAnnounces( performer ,type , objNames);
    }
    
    public static void removeAnnounces( boObject performer ,byte type , String[] objNames) throws boRuntimeException
    {
            String boql="select xwfAnnounce where assignedQueue=CTX_PERFORMER_BOUI or assignedQueue in ( select iXEOUser.queues where boui=CTX_PERFORMER_BOUI) or assignedQueue in ( select iXEOUser.groups where boui=CTX_PERFORMER_BOUI) or assignedQueue in ( select iXEOUser.roles where boui=CTX_PERFORMER_BOUI)";
            boObjectList bolist =  boObjectList.list( performer.getEboContext(), boql );
            bolist.beforeFirst();
            int count=0;
            while ( bolist.next() )
            {
                for(int i = 0; i < objNames.length; i++)
                {
                    if ( bolist.getObject().getAttribute("assignedQueue").getObject().getName().equals(objNames[i]) && ( type & xwfAnnounceImpl.MY_MESSAGES ) >0   )
                    {
                        bolist.getObject().getBridge("details").truncate();
                        bolist.getObject().update();
                    }
                }
            }
    }
    
    public static String getAnnounces(boObject performer)
    {
        try
        {
            String boql="select xwfAnnounce where assignedQueue=CTX_PERFORMER_BOUI or assignedQueue in ( select iXEOUser.queues where boui=CTX_PERFORMER_BOUI) or assignedQueue in ( select iXEOUser.groups where boui=CTX_PERFORMER_BOUI) or assignedQueue in ( select iXEOUser.roles where boui=CTX_PERFORMER_BOUI)";      
            String toRet="";
            boObjectList bolist =  boObjectList.list( performer.getEboContext(), boql );
            bolist.beforeFirst();
            int count=0;
            while ( bolist.next() )
            {
                count+=bolist.getObject().getBridge("details").getRowCount();   
            }
            if ( count > 0 )
            {
                if ( count ==1 ) toRet="Tem 1 mensagem ";
                else toRet="Tem "+count+" mensagens ";
            }
            return toRet;
        }
        catch (boRuntimeException e)
        {
            return "";
        }
    }
    
}