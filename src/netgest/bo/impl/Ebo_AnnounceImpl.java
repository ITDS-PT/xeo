/*Enconding=UTF-8*/
package netgest.bo.impl;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import netgest.bo.def.*;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;
import netgest.io.*;
import netgest.utils.*;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 */
public abstract class Ebo_AnnounceImpl extends boObject
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
            
    
        if ( this.p_mode == boObject.MODE_DESTROY ) return;
        
        try
        {
          
            bridgeHandler b=this.getBridge("details");
            
            boolean found=false;
            b.beforeFirst();
            StringBuffer message= new StringBuffer();
            
            while ( !found && b.next() )
            {
                found=true;
            }
            
            if ( found && this.getAttribute("assignedQueue").getObject().getAttribute("notifica").getValueString().equals("1")  )
            {
                boObject mail=null;
                long tmpl=this.getAttribute("assignedQueue").getObject().getAttribute("templateEmail").getValueLong();
                boolean isTemplate=false;
                if ( tmpl > 0 )
                {
                    mail=this.getAttribute("assignedQueue").getObject().getAttribute("templateEmail").getObject();
                    Ebo_TemplateImpl xtmpl = ((Ebo_TemplateImpl)mail); 
                    mail = xtmpl.loadTemplate();
                    xtmpl.processTemplate( null , mail );
                    isTemplate=true;
                
                }
                else  mail= this.getBoManager().createObject(this.getEboContext(),"email");
                
                
                    
                message= new StringBuffer();

                bridgeHandler bRO=mail.getBridge("RO");
                b.beforeFirst();
                while ( b.next() )
                {
                    message.append("<li>").append(b.getObject().getAttribute("message").getValueString()).append(" <b>").append(b.getObject().getAttribute("referenceObject").getObject().getTextCARDID()).append("</b></li>") ; 
                    bRO.add( b.getObject().getAttribute("referenceObject").getValueLong() );
                }
            


                
            
                    
               
                boObject  mailto=this.getAttribute("assignedQueue").getObject();
                
                mail.getAttribute("assignedQueue").setValueLong( this.getEboContext().getBoSession().getPerformerBoui()  );
                
                boObject assigned=mail.getAttribute("assignedQueue").getObject();
                 
                mail.getBridge("mail_to").add( this.getAttribute("assignedQueue").getValueLong() );
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
                    mail.getAttribute("description").setValueString( message.toString());    
                }
                mail.getAttribute("performer").setValueLong( this.getEboContext().getBoSession().getPerformerBoui()  );
                mail.getAttribute("beginDate").setValueDate( new java.util.Date() );
                mail.getAttribute("endDate").setValueDate( new java.util.Date() );
                mail.getStateAttributes().get("primaryState").setValueString("close");
                mail.getStateAttributes().get("closeStatus").setValueString("softclose");
                mail.update();
                
                
                String address1=mailto.getAttribute("email").getValueString();
                String address2=assigned.getAttribute("email").getValueString();
                
                if ( address1!=null && !address1.equals("") && address2!=null && !address2.equals("") )
                {
                    Method ometh = mail.getClass().getMethod("send",new Class[0]);
                    ometh.invoke( mail ,new Object[0]);    
                }
  
            }
            
        }
        catch (InvocationTargetException e)
        {
            
        }
        catch (NoSuchMethodException e)
        {
            
        }
        catch (IllegalAccessException e)
        {
            
        }
        

   }

    public void poolObjectPassivate()
    {
        // TODO:  Override this netgest.bo.runtime.boObject method
        super.poolObjectPassivate();
        
    }
    
    public static void addAnnounce( boObject assignedQueue , boObject referenceObj , String message )
    {

        
    }
    

    public static void addAnnounce( boObject transactObject, boObject assignedQueue , boObject referenceObj , String message ) throws boRuntimeException
    {
        
        boObject actualPerf = transactObject.getBoManager().loadObject( transactObject.getEboContext() ,"iXEOUser", transactObject.getEboContext().getBoSession().getPerformerBoui());
        boolean add=true;
        long assignedBoui= assignedQueue.getBoui();
        if ( actualPerf.getBoui() == assignedBoui ||
             actualPerf.getBridge("groups").haveBoui( assignedBoui  ) ||
             actualPerf.getBridge("roles").haveBoui( assignedBoui) ||
             actualPerf.getBridge("queues").haveBoui( assignedBoui) )
             {
                 add=false;
             }
        
        if ( add )
        {
            
            boObject Announce=transactObject.getBoManager().loadObject( transactObject.getEboContext(),"select Ebo_Announce where assignedQueue="+assignedQueue.getBoui());
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
               if ( refo != null && refo == referenceObj )
               {
                   found=true;
               }
            
            }
            
            String oldMessage ="";
            boObject adet=null;
            if ( !found )
            {
                adet = b.addNewObject();
                adet.getAttribute("referenceObject").setValueLong( referenceObj.getBoui() );
                adet.getAttribute("assignedQueue").setValueLong( assignedQueue.getBoui() ); 
                Announce.setChanged(true );
            }
            else
            {
                adet=b.getObject();
                oldMessage=adet.getAttribute( "message" ).getValueString()+"\n" ;
            }
            
            
            adet.getAttribute("message").setValueString( oldMessage+ message  );
            
            Announce.getAttribute("assignedQueue").setValueLong( assignedQueue.getBoui() );
            transactObject.getUpdateQueue().add( Announce , boObjectUpdateQueue.MODE_SAVE_FORCED );
            
//            transactObject.getEboContext().getUpdateQueue().add( Announce , boObjectUpdateQueue.MODE_SAVE_FORCED );            
            
        }
                   
    }
    
    public static void removeAnnouncer( boObject object ) throws boRuntimeException
    {
    
        long bouitorem = object.getBoui(); 
        String boql = "SELECT Ebo_Announce where details.referenceObject="+bouitorem;
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
            String boql="select Ebo_Announce where assignedQueue=CTX_PERFORMER_BOUI or assignedQueue in ( select iXEOUser.queues where boui=CTX_PERFORMER_BOUI) or assignedQueue in ( select iXEOUser.groups where boui=CTX_PERFORMER_BOUI) or assignedQueue in ( select iXEOUser.roles where boui=CTX_PERFORMER_BOUI)";
            boObjectList bolist =  boObjectList.list( performer.getEboContext(), boql );
            bolist.beforeFirst();
            int count=0;
            while ( bolist.next() )
            {
                if ( bolist.getObject().getAttribute("assignedQueue").getObject().getName().equals(objNames[0]) && ( type & Ebo_AnnounceImpl.MY_MESSAGES ) >0   )
                {
                    bolist.getObject().getBridge("details").truncate();
                    bolist.getObject().update();
                }
                else if ( bolist.getObject().getAttribute("assignedQueue").getObject().getName().equals(objNames[1]) && (type & Ebo_AnnounceImpl.MY_ROLE_MESSAGES)>0  )
                {
                    bolist.getObject().getBridge("details").truncate();
                    bolist.getObject().update();
                }
                else if ( bolist.getObject().getAttribute("assignedQueue").getObject().getName().equals(objNames[2]) && (type & Ebo_AnnounceImpl.MY_GROUP_MESSAGES )>0 )
                {
                    bolist.getObject().getBridge("details").truncate();
                    bolist.getObject().update();
                }
                else if ( bolist.getObject().getAttribute("assignedQueue").getObject().getName().equals(objNames[3]) && (type & Ebo_AnnounceImpl.MY_WORKQUEUE_MESSAGES)>0  )
                {
                    bolist.getObject().getBridge("details").truncate();
                    bolist.getObject().update();
                }
                
            }
        
    }
    
    public static String getAnnounces(boObject performer)
    {
        try
        {
            String boql="select Ebo_Announce where assignedQueue=CTX_PERFORMER_BOUI or assignedQueue in ( select iXEOUser.queues where boui=CTX_PERFORMER_BOUI) or assignedQueue in ( select iXEOUser.groups where boui=CTX_PERFORMER_BOUI) or assignedQueue in ( select iXEOUser.roles where boui=CTX_PERFORMER_BOUI)";      
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
                if ( count ==1 ) toRet=MessageLocalizer.getMessage("YOU_HAVE_MESSAGE");
                else toRet=MessageLocalizer.getMessage("YOU_HAVE")+" "+count+" "+MessageLocalizer.getMessage("MESSAGES");
            }
            return toRet;
        }
        catch (boRuntimeException e)
        {
            return "";
        }
    }
    
}