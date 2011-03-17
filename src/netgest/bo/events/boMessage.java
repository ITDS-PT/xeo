/*Enconding=UTF-8*/
package netgest.bo.events;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import netgest.bo.controller.Controller;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.*;
import netgest.utils.DataUtils;
import netgest.xwf.core.*;
import netgest.bo.system.Logger;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class boMessage 
{
    private static Logger logger = Logger.getLogger("netgest.bo.events.boMessage");
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private boMessage()
    {
    }

/**
 * Map to ISR
 */
    public static void beforeMapClassToISR(boObject message, long fwdBoui)
    {
        System.out.println("EI");
    }

    public static void afterMapClassToISR(boObject message, long fwdBoui)
    {
        try
        {
            boObject object = boObject.getBoManager().loadObject(message.getEboContext(), fwdBoui);
            String value = message.getAttribute("preferedMedia").getValueString();
            if("E-Mail".equals(value))
            {
                object.getAttribute("media").setValueString("email");
            }
            else if("Fax".equals(value))
            {
                object.getAttribute("media").setValueString("fax");
            }
            else if("Letter".equals(value))
            {
                object.getAttribute("media").setValueString("letter");
            }
            else if("Phone".equals(value))
            {
                object.getAttribute("media").setValueString("phone");
            }
            else if("Sgis".equals(value))
            {
                object.getAttribute("media").setValueString("SGIS");
            }
            else if("Conversation".equals(value))
            {
                object.getAttribute("media").setValueString("presencial");
            }
            else if("Prefered".equals(value))
            {
                object.getAttribute("media").setValueString("email");
            }
            boObject runtimeObj = message.getAttribute("from").getObject();
            if(runtimeObj != null)
            {
            
                String email = runtimeObj.getAttribute("email").getValueString();
                BigDecimal boui = null;
                if(email != null && !"".equals(email.trim()))
                {
                    boui = findWorker(object.getEboContext(), email);
                }
                if(boui != null)
                {
                    object.getAttribute("mastersolicitor").setValueObject(boui);
                }
            }
        }
        catch (SQLException e)
        {
            logger.severe("", e);
        }
        catch (boRuntimeException e)
        {
            logger.severe("", e);
        }
    }

/**
 * Map to ESR (claim, information, suggestion)
 */
    
    public static void beforeMapClassToESR(boObject message, long fwdBoui)
    {
        System.out.println("EI");
    }

    public static void afterMapClassToESR(boObject message, long fwdBoui)
    {
        EboContext ctx = message.getEboContext();
        Connection con = ctx.getDedicatedConnectionData();        
        try
        {
            if (!ctx.isInTransaction()) ctx.beginContainerTransaction();
            boObject object = boObject.getBoManager().loadObject(message.getEboContext(), fwdBoui);
            long nrdoc = DataUtils.GetSequenceNextVal(object.getEboContext().getApplication(),  con, object.getName());
            object.getAttribute("nrdoc").setValueLong(nrdoc);
            String value = message.getAttribute("preferedMedia").getValueString();
            if("E-Mail".equals(value))
            {
                object.getAttribute("media").setValueString("email");
            }
            else if("Fax".equals(value))
            {
                object.getAttribute("media").setValueString("fax");
            }
            else if("Letter".equals(value))
            {
                object.getAttribute("media").setValueString("letter");
            }
            else if("Phone".equals(value))
            {
                object.getAttribute("media").setValueString("phone");
            }
            else if("Sgis".equals(value))
            {
                object.getAttribute("media").setValueString("SGIS");
            }
            else if("Conversation".equals(value))
            {
                object.getAttribute("media").setValueString("presencial");
            }
            else if("Prefered".equals(value))
            {
                object.getAttribute("media").setValueString("email");
            }

            boObject runtimeObj = message.getAttribute("from").getObject();
            if(runtimeObj != null)
            {
                String email = runtimeObj.getAttribute("email").getValueString();
                BigDecimal boui = null;
                if(email != null && !"".equals(email.trim()))
                {
                    boui = findEntity(object.getEboContext(), email);
                }
                if(boui == null)
                {
                    if(email != null && !"".equals(email.trim()))
                    {
                        boui = createEntity(object.getEboContext(), runtimeObj.getAttribute("email").getValueString(), runtimeObj.getAttribute("name").getValueString(), runtimeObj.getAttribute("lastname").getValueString());
                    }
                    else
                    {
                        boui = createEntity(object.getEboContext(), null, runtimeObj.getAttribute("name").getValueString(), runtimeObj.getAttribute("lastname").getValueString());
                    }
                }
                object.getAttribute("entity").setValueObject(boui);
            }
            
            //to
            bridgeHandler bh = message.getBridge("to");
            boBridgeIterator it = bh.iterator();
            
            //utilizador
            long auxBoui = message.getEboContext().getBoSession().getPerformerBoui();
            if(isAssignedTo(it, auxBoui))
            {
                object.getAttribute("assignedQueue").setValueLong(auxBoui);
            }
            else
            {
                boObject perf = message.getBoManager().loadObject(message.getEboContext(), auxBoui);
                bridgeHandler auxBH = perf.getBridge("groups");
                boBridgeIterator auxBHIt = auxBH.iterator();
                auxBHIt.beforeFirst();
                boolean found = false;
                while(auxBHIt.next())
                {
                    auxBoui = auxBHIt.currentRow().getObject().getBoui();
                    if(isAssignedTo(it, auxBoui))
                    {
                        object.getAttribute("assignedQueue").setValueLong(auxBoui);
                        found = true;
                    }
                }
                if(!found)
                {
                    //pool de recursos
                    auxBH = perf.getBridge("queues");
                    auxBHIt = auxBH.iterator();
                    auxBHIt.beforeFirst();
                    while(auxBHIt.next())
                    {
                        auxBoui = auxBHIt.currentRow().getObject().getBoui();
                        if(isAssignedTo(it, auxBoui))
                        {
                            object.getAttribute("assignedQueue").setValueLong(auxBoui);
                            found = true;
                        }
                    }
                    if(!found)
                    {
                        auxBH = perf.getBridge("roles");
                        auxBHIt = auxBH.iterator();
                        auxBHIt.beforeFirst();
                        while(auxBHIt.next())
                        {
                            auxBoui = auxBHIt.currentRow().getObject().getBoui();
                            if(isAssignedTo(it, auxBoui))
                            {
                                object.getAttribute("assignedQueue").setValueLong(auxBoui);
                                found = true;
                            }
                        }
                    }
                }
                if(!found)
                {
                    object.getAttribute("assignedQueue").setObject(perf);
                }
            }            
        }
        catch (SQLException e)
        {
            logger.severe("", e);
        }
        catch (boRuntimeException e)
        {
            logger.severe("", e);
        }
        finally
        {
            try
            {
               ctx.commitContainerTransaction();
            }
            catch (Exception e)
            {
                logger.severe("", e);
            }
            try
            {
                con.close();
            }
            catch (SQLException e)
            {
                logger.severe("", e);
            }
        }
    }

/**
 * Helper's: createEntity, findEntity, findWorker
 */
    
    private static BigDecimal createEntity(EboContext ctx, String email, String name, String lastName) throws boRuntimeException
    {
        boObject entityobj = boObject.getBoManager().createObject( ctx, "ANC_entity" );
        if((name != null && name.length() > 0) || (lastName != null && lastName.length() > 0))
        {
            String _name = "";
            if(name != null) _name = name.trim();
            if(lastName != null) _name += " " + lastName.trim();
            entityobj.getAttribute( "name" ).setValueString( name );
        }
        else
        {
            entityobj.getAttribute( "name" ).setValueString( email );
        }
        boObject address = boObject.getBoManager().createObject( ctx, "address" );
        if(email != null)
        {
            address.getAttribute( "email" ).setValueString( email );
            entityobj.getAttribute( "email" ).setValueString( email );            
        }
        entityobj.getAttribute( "correspondencia_address" ).setValueLong( address.getBoui() );
        //id que vai ser sincronizado pelo SyncEntidades
        entityobj.getAttribute( "id" ).setValueString( "0" );
        return BigDecimal.valueOf( entityobj.getBoui() );
    }
    
    private static BigDecimal findEntity( EboContext ctx, String email ) throws SQLException
    { 
        email    = email.trim().toUpperCase();
        
        PreparedStatement pstm = null;
        ResultSet         rslt = null;

        String fnd = null;
        String typ = null;
        
        BigDecimal ret = null;

        try
        {
            pstm = ctx.getConnectionData().prepareStatement("SELECT cod_ent,type FROM v_mapentidadesmoradas WHERE UPPER( EMAIL ) LIKE ? ");
            pstm.setString( 1, email );
            rslt = pstm.executeQuery();
            String nfnd;
            while( rslt.next() )
            {
                nfnd = rslt.getString(1);
                
                typ  = rslt.getString(2);
                
                if( fnd != null && !fnd.equals( nfnd ) )
                {
                    fnd = null;
                    break;
                }
                fnd = nfnd;
            }
            pstm.close();
            rslt.close();
            if( fnd != null )
            {
                pstm = ctx.getConnectionData().prepareStatement("SELECT boui FROM ANC_ENTITY WHERE COD_ENT$L=? AND TYPE$L=?");
                pstm.setString( 1, fnd );
                pstm.setString( 2, typ ); 
                rslt = pstm.executeQuery();
                if( rslt.next() )
                {
                    ret = rslt.getBigDecimal( 1 );
                }
            }
        }
        finally
        {
            if( rslt != null )
            {
                rslt.close();
            }
            if( pstm != null )
            {
                pstm.close();
            }
        }
        
        
        return ret;
    }
    private static BigDecimal findWorker( EboContext ctx, String email ) throws SQLException
    { 
        email    = email.trim().toUpperCase();
        
        PreparedStatement pstm = null;
        ResultSet         rslt = null;

        long fnd = -1;
        String typ = null;
        
        BigDecimal ret = null;

        try
        {
            pstm = ctx.getConnectionData().prepareStatement("SELECT boui FROM OEbo_Contact WHERE UPPER( EMAIL ) LIKE ? ");
            pstm.setString( 1, email );
            rslt = pstm.executeQuery();
            long nfnd;
            while( rslt.next() )
            {
                nfnd = rslt.getLong(1);

                if( fnd != -1 && fnd != nfnd )
                {
                    fnd = -1;
                    break;
                }
                fnd = nfnd;
            }
            pstm.close();
            rslt.close();
            if( fnd != -1 )
            {
                ret = BigDecimal.valueOf(fnd);
            }
        }
        finally
        {
            if( rslt != null )
            {
                rslt.close();
            }
            if( pstm != null )
            {
                pstm.close();
            }
        }
        
        
        return ret;
    }
    
    private static boolean isAssignedTo(boBridgeIterator messageTo, long auxBoui)
    {
        try
        {
            messageTo.beforeFirst();
            while(messageTo.next())
            {
                if(messageTo.currentRow().getObject().getAttribute("refObj").getValueObject() != null)
                {
                    if(messageTo.currentRow().getObject().getAttribute("refObj").getObject().getBoui() ==
                        auxBoui
                    )
                    {
                        return true; 
                    }
                }
            }
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);            
        }
        return false;
    }
    
    
    /*******evento before save */
    public static boolean beforeSave( boObject message ) throws boRuntimeException
    {
        //from - 1
        //to -bridge
        //cc- n
        //replyto - 1
        //bcc - N
        
        copyAttribute(  message , "from" );
        copyBridge(  message , "to" );
        copyBridge(  message , "cc" );
        copyBridge(  message , "bcc" );
        copyAttribute(  message , "replyTo" );
        //novo campo para o tratamento do fax e letter
        if(message.getAttribute("dtEfectiv") != null && message.getAttribute("dtEfectiv").getValueDate() == null)
        {
            //se fôr diferente de Fax e Letter com impressão Central então dtEfectiv é igual ao dtdoc
            if(!"messageFax".equals(message.getName()))
            {
                if(!"messageLetter".equals(message.getName()) ||
                    message.getAttribute("impCentral") == null ||
                    !"1".equals(message.getAttribute("impCentral").getValueString()))
                {
                    message.getAttribute("dtEfectiv").setValueDate(message.getAttribute("dtdoc").getValueDate());
                }
            }
        }
        Boolean b = (Boolean)message.getEboContext().getBoSession().getProperty("creatingWaitMsg");
        if((b == null || !b.booleanValue()) && 
            MessageUtils.alreadySend(message) &&
            message.getAttribute("waitForResponse").getValueString() != null && 
            !"".equals(message.getAttribute("waitForResponse").getValueString()))
//        if(false)
        {
            Controller control = message.getEboContext().getController();
            String boql;
            if("0".equals(message.getAttribute("waitForResponse").getValueString()))
            {
                boql = "select xwfWaitResponse where (runningState <> 90 and runningState <> 20) and sendActivity in (select xwfActivitySend where message.value.valueObject = " + message.getBoui() + ")";
            }
            else
            {
                boql = "select xwfWaitResponse where runningState = 20 and sendActivity in (select xwfActivitySend where message.value.valueObject = " + message.getBoui() + ")";
            }
            boObjectList waitList =
                boObjectList.list(message.getEboContext(), boql,false,false);
            waitList.beforeFirst();
            boObject waitObj;
            boolean found = false;        
            while(waitList.next())
            {
                found = true;
                if(!MessageUtils.isToWaitResponse(message))
                {
                    //cancelada
                    waitList.getObject().getStateAttribute("runningState").setValue("cancel");
                }
                else
                {
                    //aberto
                    waitList.getObject().getStateAttribute("runningState").setValue("create");
                }
                waitList.getObject().update();
            }
            if(!found && MessageUtils.isToWaitResponse(message))
            {
                waitList = boObjectList.list(message.getEboContext(), "select xwfWaitResponse where sendActivity in (select xwfActivitySend where message.value.valueObject = " + message.getBoui() + ")",false,false);
                waitList.beforeFirst();
                if(!waitList.next())
                {
                    long receivers[] = MessageUtils.getToReceivers(message);
                    if(MessageUtils.isToWaitResponse(message) && receivers.length > 0)
                    {
                        logger.finer(LoggerMessageLocalizer.getMessage("GOING_TO_CREATE_WAIT_FOR_MSG"));
                        message.getEboContext().getBoSession().setProperty("creatingWaitMsg", Boolean.TRUE);
                        try
                        {
                            boObjectList listActvSend = boObjectList.list(message.getEboContext(), "select xwfActivitySend where message.value.valueObject = " + message.getBoui(),false,false);
                            listActvSend.beforeFirst();
                            boObject actv = null;
                            boObject program = null;
                            xwfManager man = null;
                            while(listActvSend.next())
                            {
                                actv = listActvSend.getObject();
                                program = actv.getAttribute("program").getObject();
                                man = new xwfManager(message.getEboContext(), program);
                                xwfMessage.createWaitActv(man, receivers, message, program.getBoui(), actv.getBoui());
                                program.update();
                            }
                        }
                        catch(boRuntimeException e)
                        {
                            throw e;
                        }
                        finally
                        {
                            message.getEboContext().getBoSession().removeProperty("creatingWaitMsg");
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private static void copyAttribute( boObject message , String attrName ) throws boRuntimeException
    {
        
        boObject o = message.getAttribute(attrName).getObject();
        if ( o != null )
        {
            boObject ref = o.getAttribute("refObj").getObject();
            if ( ref!= null )
            {
                message.getAttribute(attrName+"Ref").setValueLong( ref.getBoui() );
            }
            else
            {
                message.getAttribute(attrName+"Ref").setValueObject( null );
            }
        }
        else
        {
            message.getAttribute(attrName+"Ref").setValueObject( null );
        }
        message.computeSecurityKeys( true );
    }
    private static void copyBridge( boObject message , String attrName ) throws boRuntimeException
    {
        boObject object = null;
        AttributeHandler attrHandler = null;
        bridgeHandler b = message.getBridge( attrName );
        StringBuffer bouis = new StringBuffer();
        
        boBridgeIterator bi = b.iterator();
        bi.beforeFirst();
        while ( bi.next() )
        {
            object = bi.currentRow().getObject();
            if(object != null)
            {
                attrHandler = object.getAttribute("refObj");
                if(attrHandler != null)
                {
                    object = attrHandler.getObject();
                    if(object != null)
                    {
                        if( bouis.length() > 0 )
                        {
                            bouis.append(';');
                        }
                        bouis.append(object.getBoui());
                    }
                }
            }
        }
        message.getAttribute( attrName+"Ref").setValueString( bouis.toString() );                
    }
}