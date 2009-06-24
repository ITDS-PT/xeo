/*Enconding=UTF-8*/
package netgest.xwf.core;

import netgest.bo.controller.xwf.XwfController;
import netgest.bo.message.MessageServer;
import netgest.xwf.common.*;
import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.*;
import netgest.bo.impl.Ebo_AnnounceImpl;

import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;

import netgest.xwf.core.*;

/**
 * Classe responsavel por gerir o processamento dos alertas das tarefas
 */
public class xwfAlertManager 
{
  /**
   * Construtor por defeito
   */
  public xwfAlertManager()
  {
  }
  /**
     * Fará com que seja lançado uma mensagem(anuncio) de alerta avisando sobre um determinado evento importante para o utilizador
     * @throws netgest.bo.runtime.boRuntimeException
     * @return devolve o objecto anuncio criado
     * @param xm        boManager do programa
     * @param actv      actividade sobre a qual incide o alerta
     * @param user      utilizador a ser informado
     * @param message   mensagem de aviso do alerta
     */
  public static boObject sendAlert(String message, boObject user, boObject actv, xwfBoManager xm)throws boRuntimeException
  {
        
        boolean add=true;
        long assignedBoui= user.getBoui();
        
            
        boObject Announce= xm.loadObject("select xwfAnnounce where assignedQueue="+user.getBoui());
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
        
        
        adet.getAttribute("message").setValueString( oldMessage+ message  );
        
        Announce.getAttribute("assignedQueue").setValueLong( user.getBoui() );
//        actv.getUpdateQueue().add( Announce , boObjectUpdateQueue.MODE_SAVE_FORCED );
//        xm.updateObject(user);
//        xm.updateObject(actv);
        xm.updateObject(Announce);
        afterAdd(Announce, adet, xm);
        return Announce;
//            transactObject.getEboContext().getUpdateQueue().add( Announce , boObjectUpdateQueue.MODE_SAVE_FORCED );            
            
        
  }
  /**
     * Depois de adicionar um alerta pode ser necessário notificar o utilziador utilizando outro meio (SMS, Email)
     * @throws netgest.bo.runtime.boRuntimeException
     * @param xm        boManager do programa
     * @param detail    objecto com os detalhes do anuncio 
     * @param ann       objecto anuncio
     */
  private static void afterAdd(boObject ann, boObject detail, xwfBoManager xm)throws boRuntimeException
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
                    isTemplate=true;
                
                }
                else  mail= ann.getBoManager().createObject(ann.getEboContext(),"messageSgis");
                
                
                    
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
                
                mail.getAttribute("from").setValueLong( xm.getPerformerBoui()  );
                
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
                    mail.getAttribute("description").setValueString( detail.getAttribute("message").getValueString() );    
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
            }
    }
  /**
     * Gestão de um alerta que impõe um reassignamento da tarefa
     * @throws netgest.bo.runtime.boRuntimeException
     * @param actv  actividade do alerta que agora deve ser reassignada
     * @param reass objecto de reassignamento
     */
  public static void reassignTask(boObject reass, boObject actv)throws boRuntimeException
  {
    xwfActionHelper.reassignActivity(actv, reass);
  }
  
}