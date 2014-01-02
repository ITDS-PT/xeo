/*Enconding=UTF-8*/
package netgest.bo.impl;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;
import javax.mail.MessagingException;
import javax.mail.internet.*;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;
import netgest.bo.security.*;
import netgest.utils.*;
import netgest.utils.mail.*;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class Ebo_forumtopicImpl 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
     
     public static final String FROM_ADDRESS="xforuns@iefp.pt";
     
     public static final void onBeforeSave( boObject forumtopic ) throws boRuntimeException
     {
        boolean isPublic = forumtopic.getAttribute("ispublic").getValueString().equalsIgnoreCase("1");

        if( isPublic )
        {
            bridgeHandler bkp = forumtopic.getBridge("KEYS_PERMISSIONS");
            if( !bkp.haveBoui( -2 ) )
            {
                bkp.add( -2, AttributeHandler.INPUT_FROM_INTERNAL );
                bkp.getAttribute("securityCode").setValueLong( securityOPL.WRITE_KEY,AttributeHandler.INPUT_FROM_INTERNAL );
            }
        }
     
         if( !forumtopic.exists() )
         {
             forumtopic.getAttribute( "subscribers" ).setValueObject( null );
         }
         
         boolean send=false;
         boObjectStateHandler primaryState  = forumtopic.getStateAttribute( "primaryState" ).getCurrentState();
         boObjectStateHandler createdStatus = forumtopic.getStateAttribute( "createdStatus" ).getCurrentState();
         boObjectStateHandler openStatus    = forumtopic.getStateAttribute( "openStatus" ).getCurrentState();
         boolean needsApprove = false;
          
         if( !forumtopic.exists() )
         {
             if( forumtopic.getAttribute("category").getValueObject() != null )
             {
                 boObject cat = forumtopic.getAttribute("category").getObject();
                 if( cat.getAttribute("needapproval").getValueObject() != null && cat.getAttribute("needapproval").getValueString().equals("1") )
                 {
                      if( primaryState.getNumericForm() == 0 )
                      {
                          needsApprove = true;
                      }
                      else if ( primaryState.getNumericForm() == 1 )
                      {
                          needsApprove = true;
                      }
                 }
             }
             if( needsApprove )
             {
                  forumtopic.getAttribute("topicstate").setValueLong(0);   
             }
             else
             {
                  forumtopic.getAttribute("topicstate").setValueLong(1);   
             }
         }
         
         long cstate = forumtopic.getAttribute("topicstate").getValueLong();
         
         primaryState  = forumtopic.getStateAttribute( "primaryState" ).getCurrentState();
         createdStatus = forumtopic.getStateAttribute( "createdStatus" ).getCurrentState();
         openStatus    = forumtopic.getStateAttribute( "openStatus" ).getCurrentState();
         
         
         if( primaryState != null )
         {
         
            if( primaryState.getNumericForm()==0 && createdStatus != null && createdStatus.getNumericForm() == 2 )
            {
                send = true;
            }
            if ( primaryState.getNumericForm()==1 && openStatus  != null && ( openStatus.getNumericForm() != 4 || openStatus.getNumericForm() != 2 )  )
            {
                send = true;  
            }
         }
         else
         {
             send = false;
         }
         if( send && cstate == 1 )
         {
             bridgeHandler notified = forumtopic.getBridge("notifiedlist");
             bridgeHandler members = forumtopic.getBridge("audiencia");
             members.beforeFirst();
//             if ( !members.haveVL() )
//             {
                 while( members.next() )
                 {
                     notified.beforeFirst();
                     if( !notified.haveBoui( members.getValueLong() ) )
                     {
//                          boObject task = boObject.getBoManager().createObject( forumtopic.getEboContext(),  "task" );
//                          task.getAttribute("assignedQueue").setValueLong( members.getValueLong() );
//                          if( members.getName().equals("Ebo_Perf")  )
//                          {
//                              task.getAttribute("performer").setValueLong( members.getValueLong() );
//                          }
//                          task.getAttribute("name").setValueString("Convite para participar no fórum:[" + forumtopic.getAttribute("name").getValueString() + "]" );
//                          task.getAttribute("description").setValueString("Foi convidado a participar no fórum [<span style='color:blue' unselectable='on' CONTENTEDITABLE='false'>" + forumtopic.getAttribute("name").getValueString()+ "</span>] criado por <span style='color:blue' unselectable='on' CONTENTEDITABLE='false'>[ "+forumtopic.getAttribute("CREATOR").getObject().getCARDID().toString()+" ]</span><br>"
//                          +" <A unselectable='on' CONTENTEDITABLE='false' target='_parent' href='javascript:winmain().openDocUrl(\"large\",\"forumposts.jsp\",\"?topic="+forumtopic.getBoui()+"\");'>Para participar clique aqui</A>"
//                          );
                        String subject = MessageLocalizer.getMessage("INVITATION_TO_PARTICIPATE_IN_THE_TOPIC")+" ["+forumtopic.getAttribute("name").getValueString()+"]";
                        sendEmail( forumtopic.getEboContext(), members.getObject().getAttribute("name").getValueString(), 
                                   members.getObject().getAttribute("email").getValueString(),
                                   subject,
                                   parseTopicMessageTemplate( INVITE_TOPIC, null, forumtopic, null ),
                                   subject,
                                   FROM_ADDRESS
                                 );


                          
                         // bridgeHandler ro = task.getBridge("RO");
                          //ro.add( forumtopic.getBoui() );                      
                          
                          //forumtopic.getUpdateQueue().add( task, forumtopic.getUpdateQueue().MODE_SAVE );
                          notified.add( members.getValueLong() );
                     }
                 }
        }
        boObject catObj = forumtopic.getAttribute("category").getObject();
        if( catObj != null && forumtopic.exists() )
        {
            bridgeHandler kpb = catObj.getBridge( "KEYS_PERMISSIONS" );
            boBridgeIterator it = kpb.iterator();
            ArrayList sended = new ArrayList();
            while( it.next() )
            {
                long perf     = it.currentRow().getAttribute("KEYS_PERMISSIONS").getValueLong();
                String notify = it.currentRow().getAttribute("notify").getValueString();
                if( notify.equalsIgnoreCase("1") )
                {
                    if( !sended.contains( new Long( perf ) ) )
                    {
                        String subject = MessageLocalizer.getMessage("NEW_TOPIC_IN_CATEGORY")+" ["+catObj.getAttribute("name").getValueString()+"]";
                        boObject perfobj = boObject.getBoManager().loadObject( forumtopic.getEboContext(), perf );
                        sendEmail( forumtopic.getEboContext(), 
                                    perfobj.getAttribute("name").getValueString(),
                                    perfobj.getAttribute("email").getValueString(),
                                    subject,
                                    parseTopicMessageTemplate( NEW_TOPIC, null, forumtopic, catObj ),
                                    "",
                                    FROM_ADDRESS
                                );
                        sended.add( new Long( perf ) );
                    }
                }
            }
        }
            
//             }
    }

    public static final void sendNotificationOnSave( boObject forumPost ) throws boRuntimeException
    { 
        String name    = forumPost.getAttribute("name").getValueString();

        String message = removeQuotedText( forumPost.getAttribute("description").getValueString(), forumPost.getBoui() );
        forumPost.getAttribute("description").setValueString( message );
        
        boObject forumTopic = forumPost.getAttribute("forumtopic").getObject();
        
        if ( forumPost.getPrimaryState().getValueString().equals("open") )
        {
            boolean toSend = !forumPost.exists();
            if( !toSend )
            {
                if( forumPost.getDataRow().getFlashBackRow() != null )
                {
                    toSend = ( !ClassUtils.cmpString( forumPost.getDataRow().getString("PRIMARYSTATE"), forumPost.getDataRow().getFlashBackRow().getString("PRIMARYSTATE") ) );
                    
                }
            }
            if( toSend )
            {
        
                
                ArrayList notified = sendNotificationsToThread( forumPost, forumTopic, forumPost, null  );
                
                boObject[] subscribers = forumTopic.getAttribute( "subscribers" ).getObjects();
                for (int i = 0;subscribers!=null && i < subscribers.length; i++) 
                {
                    if ( subscribers[i].getBoui() != forumPost.getEboContext().getBoSession().getPerformerBoui() )
                    {
                        if( !notified.contains( new Long( subscribers[i].getBoui() ) ) )
                        {
    //                        Ebo_AnnounceImpl.addAnnounce( forumPost, subscribers[i], forumTopic, "Nova mensagem em ["+forumTopic.getAttribute("name").getValueString()+"] colocada por "+forumPost.getAttribute("CREATOR").getObject().getAttribute("name").getValueString() );
     
                            String subject = "Nova mensagem no tópico ["+forumTopic.getAttribute("name").getValueString()+"]";
    
                            sendEmail( forumPost.getEboContext(), subscribers[i].getAttribute("name").getValueString(), 
                                       subscribers[i].getAttribute("email").getValueString(),
                                       subject,
                                       parseTopicMessageTemplate(REPLY_TOPIC, forumPost, forumTopic, null ),
                                       subject,
                                       FROM_ADDRESS
                                     );
    
                        }
                    }
                }
            }
        }
        if( !forumPost.exists() )
        {
            boObject catObj = forumTopic.getAttribute("category").getObject();
            if( catObj != null )
            {
                bridgeHandler kpb = catObj.getBridge( "KEYS_PERMISSIONS" );
                boBridgeIterator it = kpb.iterator();
                ArrayList sended = new ArrayList();
                while( it.next() )
                {
                    long perf     = it.currentRow().getAttribute("KEYS_PERMISSIONS").getValueLong();
                    String notify = it.currentRow().getAttribute("notify").getValueString();
                    if( notify.equalsIgnoreCase("1") )
                    {
                        if( !sended.contains( new Long( perf ) ) )
                        {
                            String subject = MessageLocalizer.getMessage("NEW_MESSAGE_IN_TOPIC")+" ["+forumTopic.getAttribute("name").getValueString()+"]";
                            boObject perfobj = boObject.getBoManager().loadObject( forumTopic.getEboContext(), perf );
                            sendEmail( forumTopic.getEboContext(), 
                                        perfobj.getAttribute("name").getValueString(),
                                        perfobj.getAttribute("email").getValueString(),
                                        subject,
                                        parseTopicMessageTemplate( NEW_POST, forumPost, forumTopic, catObj ),
                                        "",
                                        FROM_ADDRESS
                                    );
                            sended.add( new Long( perf ) );
                        }
                    }
                }
            }
            
        }
    }
    
    public static final ArrayList sendNotificationsToThread( boObject activepost,boObject forumtopic,boObject post ,ArrayList notifiedList ) throws boRuntimeException
    {
        if( notifiedList == null )
        {
            notifiedList = new ArrayList(); 
        }
        if ( post.getAttribute("threadpost") != null )
        {
            Object replytov = post.getAttribute("threadpost").getValueObject();
            if( replytov != null ) 
            {
                boObject replyTo = post.getAttribute("threadpost").getObject();
                if( post.getAttribute("notifyAnswer").getValueObject() != null && post.getAttribute("notifyAnswer").getValueString().equals("1") )
                {
                    boObject to = post.getAttribute("CREATOR").getObject();
                    if ( to.getBoui() != activepost.getEboContext().getBoSession().getPerformerBoui() )
                    {
                        if( !notifiedList.contains( new Long(to.getBoui() ) ) )
                        { 
                            notifiedList.add( new Long( to.getBoui() ) );
 
//                            Ebo_AnnounceImpl.addAnnounce( activepost, to , "Nova mensagem em ["+forumtopic.getAttribute("name").getValueString()+"] colocada por "+post.getAttribute("CREATOR").getObject().getAttribute("name").getValueString() );
                            
                            String subject = MessageLocalizer.getMessage("ANSWER_TO_TOPIC_MESSAGE")+" ["+forumtopic.getAttribute("name").getValueString()+"]";
                            
                            sendEmail( activepost.getEboContext(), to.getAttribute("name").getValueString(), 
                                       to.getAttribute("email").getValueString(),
                                       subject,
                                       parsePostMessageTemplate( REPLY_POST, activepost.getAttribute("threadpost").getObject(), activepost, post ),
                                       subject,
                                       FROM_ADDRESS
                                     );
                        } 
                    }
                }
                sendNotificationsToThread( activepost, forumtopic, replyTo, notifiedList );
            }
        }
        return notifiedList;
    }
    
    public static final void sendResponseNotification( boObject forumPost ) throws boRuntimeException
    { 

        String message = removeQuotedText( forumPost.getAttribute("description").getValueString(), forumPost.getBoui() );

        if( !forumPost.exists() )
        {
            boObject forumTopic = forumPost.getAttribute("forumtopic").getObject();
            boObject[] subscribers = forumTopic.getAttribute( "subscribers" ).getObjects();
            for (int i = 0;subscribers!=null && i < subscribers.length; i++) 
            {
                if ( subscribers[i].getBoui() != forumPost.getEboContext().getBoSession().getPerformerBoui() )
                {
                    Ebo_AnnounceImpl.addAnnounce( forumPost, subscribers[i], forumTopic, MessageLocalizer.getMessage("NEW_MESSAGE_IN")+" ["+forumTopic.getAttribute("name").getValueString()+"] colocada por "+forumPost.getAttribute("CREATOR").getObject().getAttribute("name").getValueString() );
                }
            }
        }
    }
    
    
    public static final void subscribe( boObject forumTopic, long performer ) throws boRuntimeException
    {
        
        BigDecimal[] subscribers =  (BigDecimal[])forumTopic.getAttribute( "subscribers" ).getValueObject();
        int newsize = subscribers==null?1:subscribers.length + 1;             
        BigDecimal[] subs = new BigDecimal[ newsize ];
        if( newsize > 1 )
        {
            System.arraycopy( subscribers, 0, subs, 0, subscribers.length );
        }
        subs[ subs.length - 1 ] = BigDecimal.valueOf( performer );
        forumTopic.getAttribute( "subscribers" ).setValueObject( subs );
        
    }
    
    public static final void unSubscribe( boObject forumTopic, long performer ) throws boRuntimeException
    {
        BigDecimal[] subscribers =  (BigDecimal[])forumTopic.getAttribute( "subscribers" ).getValueObject();
        if( subscribers!= null && isSubscriber( forumTopic, performer ) )
        {
            if( subscribers.length > 1 )
            {
                int subscribpos = Arrays.binarySearch( subscribers,BigDecimal.valueOf( performer ) ) + 1 ;
                BigDecimal[] subs = new BigDecimal[ subscribers.length - 1 ];
                if( subscribpos > 1 )
                {
                    System.arraycopy( subscribers, 0, subs, 0, subscribpos - 1 );
                }
                if( subscribpos < subscribers.length )
                {
                    System.arraycopy( subscribers, subscribpos, subs, subscribpos - 1, subs.length - (subscribpos - 1) );
                }
                forumTopic.getAttribute( "subscribers" ).setValueObject( null );
                forumTopic.getAttribute( "subscribers" ).setValueObject( subs );
            }
            else
            {
                forumTopic.getAttribute( "subscribers" ).setValueObject( null );
            }
        }
    }
    public static final boolean isSubscriber( boObject forumTopic, long performer ) throws boRuntimeException
    {
        boolean ret = false;
        long[] subscribers =  forumTopic.getAttribute( "subscribers" ).getValuesLong();
        if( subscribers!= null )
        {
            ret = 
                Arrays.binarySearch( subscribers, forumTopic.getEboContext().getBoSession().getPerformerBoui() ) >= 0 ;
        }
        return ret;
    }
    public static final boObject createPostMessage( boObject forumTopic, long replyto ) throws boRuntimeException
    {         
        boObject xobj = boObject.getBoManager( ).createObject( forumTopic.getEboContext(), "forumpost" );
        xobj.getAttribute( "forumtopic" ).setValueLong( forumTopic.getBoui() );
        
        
        final SimpleDateFormat sdfts = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        String subject = ""; 
        String message = "";
        String user = "";
        String time = "";
        if( replyto != 0 )
        {
            xobj.getAttribute( "threadpost" ).setValueLong( replyto );
            boObject replyobj = boObject.getBoManager().loadObject( forumTopic.getEboContext(), replyto );
            subject = replyobj.getAttribute( "name" ).getValueString();
            message = replyobj.getAttribute( "description" ).getValueString();
            user    = replyobj.getAttribute( "CREATOR" ).getObject().getAttribute("name").getValueString();
            time    = sdfts.format( replyobj.getAttribute( "SYS_DTCREATE" ).getValueDate() );
        }
        else
        {
            subject = forumTopic.getAttribute( "name" ).getValueString();
            message = forumTopic.getAttribute( "description" ).getValueString();
            user    = forumTopic.getAttribute( "CREATOR" ).getObject().getAttribute("name").getValueString();
            time    = sdfts.format( forumTopic.getAttribute( "SYS_DTCREATE" ).getValueDate() );
        }
        if( !subject.toUpperCase().startsWith("RE:") )
        {
            subject = "RE:"+subject;
        }
        xobj.getAttribute("name").setValueString( subject );

        xobj.getAttribute("description").setValueString( composeQuotedMessage( user, time  , message, xobj.getBoui() ) );

        return xobj;
    } 
    
    public static final String composeQuotedMessage( String from, String date, String message, long boui )
    {
        String[] tags = getQuotedTextTags( boui );
        StringBuffer sb = new StringBuffer( "<BR/><BR/>" );
        sb.append( tags[0] );
        sb.append( "<BLOCKQUOTE dir=ltr style='PADDING-RIGHT: 0px; PADDING-LEFT: 5px; MARGIN-LEFT: 15px; BORDER-LEFT: #000000 2px solid; MARGIN-RIGHT: 0px'>-----Mensagem Original----<br>" );
        sb.append("<b>De:</b>").append( from ).append("<br/>");        
        sb.append("<b>Colocada em:</b>").append( date ).append("<br/><BR/>");        
        sb.append( message );
        sb.append("<p>");
        sb.append( "</BLOCKQUOTE>" );
        sb.append( tags[1] );
        return sb.toString();
    }
    
    public static final String[] getQuotedTextTags( long boui )
    {
        String start_tag = "<DIV contentEditable=false unselectable=\"on\">\r\n<DIV style=\"FONT: 1pt verdana; COLOR: white\">ANSWER_QUOTED_MESSAGE_"+boui+"</DIV>";
        String end_tag   = "<DIV style=\"FONT: 1pt verdana; COLOR: white\">ANSWER_QUOTED_MESSAGE_"+boui+"</DIV></DIV>";
        return new String[] { start_tag, end_tag };
    }
    
    public static final String removeQuotedText( String message , long boui )
    {
        String[] tags = getQuotedTextTags( boui );
        int idx;        
        
        if( (idx=message.indexOf( tags[0] )) > -1 )
        {
            int lastidx = message.indexOf( tags[1] ) + tags[1].length();
            
            message =   message.substring(0,idx)
                      + message.substring( lastidx );
        }
        return message;
        
    }
    
    
    public static final void sendEmail( EboContext ctx,String toname, String toaddress, String subject,String message, String fromname, String fromaddress )
    {
        
        try
        {
            try
            {
                // Check if email is correct
                InternetAddress intaddress = new InternetAddress( toaddress );
                
                mailMessage xmail = new mailMessage();
                xmail.setFrom( new mailAddress( "Forum [XPTO]",fromaddress) );
                xmail.addRecipient( new mailAddress( toname, toaddress ) );
                xmail.setSubject( subject );
                xmail.setBody( message );
                xmail.setBodyHtml( message );
                xmail.addMailMessage( xmail );
                xmail.setSMTPHost( ctx.getApplication().getApplicationConfig().getMailConfig().getProperty("smtphost") );
                xmail.send();
            }
            catch (AddressException e)
            {
                
            }
        
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static final String buildForunsURL( boObject topic, boObject post )
    {
        String baseUrl = topic.getParameter( "TOPICS_LINK" )!=null?topic.getParameter( "TOPICS_LINK" ).toString():null;
        if( baseUrl == null && post != null )
        {
            baseUrl = post.getParameter( "TOPICS_LINK" )!=null?post.getParameter( "TOPICS_LINK" ).toString():null;
        }
        if( baseUrl != null ) 
        {
//            StringBuffer sb = new StringBuffer( baseUrl );
//            sb.append( "&T_topic=" ).append( topic.getBoui() );
//            if( post != null )
//            {
//                sb.append( "&T_focus=" ).append( post.getBoui() );
//            }
//            baseUrl = sb.toString();
        }
        else
        {
            baseUrl = "javascript:alert('"+MessageLocalizer.getMessage("LINK_NOT_AVAILABLE")+"');";
        }
        return baseUrl;
    }
    
    
    
    public static String parseTopicMessageTemplate( String message, boObject post, boObject topic, boObject cat) throws boRuntimeException
    {
        String ret = message;
        ret = StringUtils.replacestr( ret, "#TOPIC.name#", topic.getAttribute("name").getValueString() );
        ret = StringUtils.replacestr( ret, "#TOPIC.CREATOR#", topic.getAttribute("CREATOR").getObject().getAttribute("name").getValueString() );
        ret = StringUtils.replacestr( ret, "#TOPIC.CREATOR.name#", topic.getAttribute("CREATOR").getObject().getAttribute("name").getValueString() );
        ret = StringUtils.replacestr( ret, "#TOPIC.description#", topic.getAttribute("description").getValueString() );
        ret = StringUtils.replacestr( ret, "#TOPIC.SYS_DTCREATE#", sdfts.format(  new Date() ) );
        
        if( cat != null )
        {
            ret = StringUtils.replacestr( ret, "#CAT.name#", topic.getAttribute("name").getValueString() );
        }
        
        if( post != null )
        {
            ret = StringUtils.replacestr( ret, "#POST.CREATOR.name#", post.getAttribute("CREATOR").getObject().getAttribute("name").getValueString() );
            ret = StringUtils.replacestr( ret, "#POST.SYS_DTCREATE#", sdfts.format(  new Date() ) );
            ret = StringUtils.replacestr( ret, "#POST.name#", post.getAttribute("name").getValueString() );
            ret = StringUtils.replacestr( ret, "#POST.description#", post.getAttribute("description").getValueString() );
        }
        ret = StringUtils.replacestr( ret, "#MESSAGEHREF#", buildForunsURL( topic, post ) ); 
        return ret;
    }
    
    
    
    public static String parsePostMessageTemplate(String message, boObject post, boObject reply, boObject originPost ) throws boRuntimeException
    {
        boObject topic = post.getAttribute("forumtopic").getObject();
        String ret = message;
        
        ret = StringUtils.replacestr( ret, "#ORIGINPOST.name#", originPost.getAttribute("name").getValueString() );
        ret = StringUtils.replacestr( ret, "#ORIGINPOST.forumtopic.name#", topic.getAttribute("name").getValueString() );
        ret = StringUtils.replacestr( ret, "#POST.CREATOR.name#", reply.getAttribute("CREATOR").getObject().getAttribute("name").getValueString() );
        ret = StringUtils.replacestr( ret, "#POST.SYS_DTCREATE#", sdfts.format(  new Date() ) );
        ret = StringUtils.replacestr( ret, "#POST.name#", reply.getAttribute("name").getValueString() );
        ret = StringUtils.replacestr( ret, "#POST.description#", reply.getAttribute("description").getValueString() ); 
        ret = StringUtils.replacestr( ret, "#REPLYPOST.name#", post.getAttribute("description").getValueString() );
        ret = StringUtils.replacestr( ret, "#REPLYPOST.description#", post.getAttribute("description").getValueString() );
        ret = StringUtils.replacestr( ret, "#MESSAGEHREF#", buildForunsURL( topic , reply ) ); 
        return ret;
    }
    
    private static final SimpleDateFormat sdfts = new SimpleDateFormat("dd/MM/yyyy HH:mm");
     
    static String REPLY_POST;

static
{
String[] lines =
{
"<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.0 Transitional//EN'>",
"<HTML><HEAD>",
"<META http-equiv=Content-Type content='text/html; charset=UTF-8'>",
"<META content='MSHTML 6.00.2800.1400' name=GENERATOR>",
"<STYLE></STYLE>",
"</HEAD>",
"<BODY bgColor=#ffffff>",
"<DIV><FONT face=Verdana size=2>Resposta à mensagem «#ORIGINPOST.name#»&nbsp;no ",
"tópico «#ORIGINPOST.forumtopic.name#»</FONT></DIV>",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV>",
"<DIV><FONT size=2><FONT face=Verdana>Mensagem colocada por ",
"#POST.CREATOR.name#&nbsp;ás #POST.SYS_DTCREATE#</FONT></DIV>",
"<DIV><FONT face=Verdana></FONT>&nbsp;</DIV>",
"<DIV><FONT face=Verdana>Para aceder aos foruns [ <A href='#MESSAGEHREF#'>clique aqui</A> ]</FONT></DIV>",
"<DIV><FONT face=Verdana></FONT>&nbsp;</DIV></FONT>",
"<DIV><STRONG><FONT face=Verdana size=2>Assunto: ",
"#POST.name#</FONT></STRONG></DIV>",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV>",
"<DIV><FONT face=Verdana size=2>#POST.description#</FONT></DIV>",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV>",
"<DIV><FONT size=2><FONT face=Verdana>",
"<HR>",
"Em resposta&nbsp;à mensagem colocada por #POST.CREATOR.name#</FONT></DIV>",
"<DIV><FONT face=Verdana></FONT>&nbsp;</DIV></FONT>",
"<DIV>",
"<DIV><STRONG><FONT face=Verdana ",
"size=2>Assunto:#REPLYPOST.name#</FONT></STRONG></DIV>",
"<DIV><STRONG><FONT face=Verdana size=2></FONT></STRONG>&nbsp;</DIV>",
"<DIV><FONT face=Verdana size=2>#REPLYPOST.description#</FONT></DIV></DIV>",
"<DIV><FONT size=2>",
"<DIV><FONT face=Verdana></FONT>&nbsp;</DIV>",
"<DIV><FONT face=Verdana>",
"<HR>",
"</FONT></DIV></FONT></DIV>",
"<DIV><FONT face=Verdana size=2><EM>Notificação enviada automáticamente via XEO ",
"Foruns.</EM></FONT></DIV>",
"<DIV><EM><FONT face=Verdana size=2>Não responder a esta ",
"mensagem.</FONT></EM></DIV></BODY></HTML>"
};        

StringBuffer sb = new StringBuffer(); 
for (int i = 0; i < lines.length; i++) 
{
    sb.append( lines[i] ).append("\r\n");
}

        REPLY_POST = sb.toString();
    
    }
    

    static String  REPLY_TOPIC;
    static 
    {
String[] lines =
{
"<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.0 Transitional//EN'>",
"<HTML><HEAD>",
"<META http-equiv=Content-Type content='text/html; charset=UTF-8'>",
"<META content='MSHTML 6.00.2800.1400' name=GENERATOR>",
"<STYLE></STYLE>",
"</HEAD>",
"<BODY bgColor=#ffffff>",
"<DIV><FONT face=Verdana size=2>Nova&nbsp;mensagem no tópico ",
"[#TOPIC.name#]</FONT></DIV>",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV><FONT size=2>",
"<DIV><FONT face=Verdana>Para aceder aos foruns [ <A href='#MESSAGEHREF#'>clique ",
"aqui</A> ]</FONT></DIV>",
"<DIV><FONT face=Verdana></FONT>&nbsp;</DIV>",
"<DIV><FONT face=Verdana>",
"<DIV><FONT size=2><FONT face=Verdana>Mensagem colocada por por ",
"[#POST.CREATOR.name#]&nbsp;: [#POST.SYS_DTCREATE#]</FONT></FONT></DIV>",
"<DIV><FONT size=2>&nbsp;</DIV></FONT></FONT></DIV></FONT>",
"<DIV><STRONG><FONT face=Verdana size=2>Assunto: ",
"#POST.name#</FONT></STRONG></DIV>",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV>",
"<DIV><FONT face=Verdana size=2>#POST.description#</FONT></DIV>",
"<DIV><FONT size=2><FONT face=Verdana></FONT></FONT>&nbsp;</DIV>",
"<DIV><FONT size=2><FONT face=Verdana><FONT face=Arial></FONT>&nbsp;</DIV>",
"<DIV>",
"<HR>",
"</DIV></FONT></FONT>",
"<DIV><FONT face=Verdana size=2><EM>Notificação enviada automáticamente via XEO ",
"Foruns.</EM></FONT></DIV>",
"<DIV><EM><FONT face=Verdana size=2>Não responder a esta ",
"mensagem.</FONT></EM></DIV></BODY></HTML>"
};
StringBuffer sb = new StringBuffer(); 
for (int i = 0; i < lines.length; i++) 
{
    sb.append( lines[i] ).append("\r\n");
}
        REPLY_TOPIC = sb.toString();
    }
    
    
    static String  INVITE_TOPIC;
    static 
    {
String[] lines =
{
"<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.0 Transitional//EN'>",
"<HTML><HEAD>",
"<META http-equiv=Content-Type content='text/html; charset=UTF-8'>",
"<META content='MSHTML 6.00.2800.1400' name=GENERATOR>",
"<STYLE></STYLE>",
"</HEAD>",
"<BODY bgColor=#ffffff>",
"<DIV><FONT face=Verdana size=2>Foi convidado a participar ",
"no&nbsp;tópico&nbsp;[#TOPIC.name#] por [#TOPIC.CREATOR#]</FONT></DIV>",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV><FONT size=2>",
"<DIV><FONT face=Verdana>Para aceder ao&nbsp;tópico&nbsp;[ <A ",
"href='#MESSAGEHREF#'>clique aqui</A> ]</FONT></DIV>",
"<DIV><FONT face=Arial></FONT>&nbsp;</DIV>",
"<DIV></FONT><STRONG><FONT face=Verdana size=2>",
"<HR>",
"Assunto: #TOPIC.name#</FONT></STRONG></DIV>",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV>",
"<DIV><FONT face=Verdana size=2>#TOPIC.description#</FONT></DIV>",
"<DIV><FONT size=2><FONT face=Verdana><FONT face=Arial></FONT>&nbsp;</DIV>",
"<DIV>",
"<HR>",
"</DIV></FONT></FONT>",
"<DIV><FONT face=Verdana size=2><EM>Notificação enviada automáticamente via XEO Foruns.</EM></FONT></DIV>",
"<DIV><EM><FONT face=Verdana size=2>Não responder a esta mensagem.</FONT></EM></DIV></BODY></HTML>",
};
StringBuffer sb = new StringBuffer(); 
for (int i = 0; i < lines.length; i++) 
{
    sb.append( lines[i] ).append("\r\n");
}
        INVITE_TOPIC = sb.toString();
    }

    static String  NEW_POST;
    static 
    {
String[] lines =
{
"<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.0 Transitional//EN'>								",
"<HTML><HEAD>                                                                               ",
"<META http-equiv=Content-Type content='text/html; charset=UTF-8'>                     ",
"<META content='MSHTML 6.00.2800.1400' name=GENERATOR>                                      ",
"<STYLE></STYLE>                                                                            ",
"</HEAD>                                                                                    ",
"<BODY bgColor=#ffffff>                                                                     ",
"<DIV><FONT face=Arial size=2>                                                              ",
"<DIV><FONT face=Verdana size=2>Nova mensagem [#POST.name# ]&nbsp;no tópico           ",
"[#TOPIC.name#]</FONT></DIV>                                                ",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV>                                         ",
"<DIV><FONT size=2><FONT face=Verdana>Mensagem colocada por por                             ",
"[#POST.CREATOR.name#]&nbsp;: [#POST.SYS_DTCREATE#]</FONT></DIV>                            ",
"<DIV><FONT face=Verdana></FONT>&nbsp;</DIV>                                                ",
"<DIV><FONT face=Verdana>Para aceder aos foruns [ <A href='#MESSAGEHREF#'>clique aqui</A> ]</FONT></DIV>                 ",
"<DIV><FONT face=Verdana></FONT>&nbsp;</DIV></FONT>                                         ",
"<DIV><STRONG><FONT face=Verdana size=2>Assunto:                                            ",
"#POST.name#</FONT></STRONG></DIV>                                                          ",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV>                                         ",
"<DIV><FONT face=Verdana size=2>#POST.description#</FONT></DIV>                             ",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV>                                         ",
"<DIV><FONT size=2>                                                                         ",
"<DIV><FONT face=Verdana>                                                                   ",
"<HR>                                                                                       ",
"</FONT></DIV></FONT></DIV>                                                                 ",
"<DIV><FONT face=Verdana size=2><EM>Noficação enviada automáticamente via XEO               ",
"Foruns.</EM></FONT></DIV>                                                                  ",
"<DIV><EM><FONT face=Verdana size=2>Não responder a esta                                    ",
"mensagem.</FONT></EM></DIV></FONT></DIV></BODY></HTML>                                     "
};
StringBuffer sb = new StringBuffer(); 
for (int i = 0; i < lines.length; i++) 
{
    sb.append( lines[i] ).append("\r\n");
}
        NEW_POST = sb.toString();
    }

    static String  NEW_TOPIC;
    static 
    {
String[] lines =
{
"<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.0 Transitional//EN'>					",
"<HTML><HEAD>                                                                   ",
"<META http-equiv=Content-Type content='text/html; charset=UTF-8'>         ",
"<META content='MSHTML 6.00.2800.1400' name=GENERATOR>                          ",
"<STYLE></STYLE>                                                                ",
"</HEAD>                                                                        ",
"<BODY bgColor=#ffffff>                                                         ",
"<DIV><FONT face=Arial size=2>                                                  ",
"<DIV><FONT face=Verdana size=2>Novo tópico&nbsp;[#TOPIC.name# ]&nbsp;na        ",
"categoria [#CAT.name#]</FONT></DIV>                                            ",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV>                             ",
"<DIV><FONT size=2><FONT face=Verdana>Tópico Criado por                         ",
"[#TOPIC.CREATOR.name#]&nbsp;: [#TOPIC.SYS_DTCREATE#]</FONT></DIV>              ",
"<DIV><FONT face=Verdana></FONT>&nbsp;</DIV>                                    ",
"<DIV><FONT face=Verdana>Para aceder aos foruns [ <A href='#MESSAGEHREF#'>clique aqui</A> ]</FONT></DIV>     ",
"<DIV><FONT face=Verdana></FONT>&nbsp;</DIV></FONT>                             ",
"<DIV><STRONG><FONT face=Verdana size=2>Assunto:                                ",
"#TOPIC.name#</FONT></STRONG></DIV>                                             ",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV>                             ",
"<DIV><FONT face=Verdana size=2>#TOPIC.description#</FONT></DIV>                ",
"<DIV><FONT face=Verdana size=2></FONT>&nbsp;</DIV>                             ",
"<DIV><FONT size=2>                                                             ",
"<DIV><FONT face=Verdana>                                                       ",
"<HR>                                                                           ",
"</FONT></DIV></FONT></DIV>                                                     ",
"<DIV><FONT face=Verdana size=2><EM>Noficação enviada automáticamente via XEO   ",
"Foruns.</EM></FONT></DIV>                                                      ",
"<DIV><EM><FONT face=Verdana size=2>Não responder a esta                        ",
"mensagem.</FONT></EM></DIV></FONT></DIV></BODY></HTML>                         "
};

StringBuffer sb = new StringBuffer(); 
for (int i = 0; i < lines.length; i++) 
{
    sb.append( lines[i] ).append("\r\n");
}
        NEW_TOPIC = sb.toString();
    }



}

