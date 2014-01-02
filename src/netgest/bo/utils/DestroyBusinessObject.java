/*Enconding=UTF-8*/
package netgest.bo.utils;

import java.util.ArrayList;
import java.util.List;

import netgest.bo.impl.document.merge.gestemp.GtTemplate;
import netgest.bo.impl.document.merge.gestemp.Helper;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.xwf.EngineGate;
import netgest.xwf.core.xwfAnnounceImpl;
import netgest.xwf.xwfEngineGate;

import netgest.bo.system.Logger;

/**
 * Classe responsável pela remoção de um objecto de negócio.  
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */
public final class DestroyBusinessObject 
{
    private static Logger logger = Logger.getLogger("netgest.bo.utils.DeleteBusinessObject");
 
    /**
     * Remove qualquer objecto de negócio.
     * @param object Objecto a remover.
     * @return TRUE caso remova , FALSE caso contário
     * @throws netgest.bo.runtime.boRuntimeException 
     */
    public static boolean destroy(boObject object)  throws boRuntimeException
    {
        boolean result = false;
        logger.finer(LoggerMessageLocalizer.getMessage("STARTING_DETROYING_OBJECT")+" " + object.getBoui());
        if("message".equals(object.getName()) || 
           "message".equals(object.getBoDefinition().getBoSuperBo()) ||
           "messageSystem".equals(object.getBoDefinition().getBoSuperBo())
          )
        {
            result = destroyMessage(object,true);
        }
        else if("activity".equalsIgnoreCase(object.getBoDefinition().getBoPhisicalMasterTable()))
        {
            result = destroyActivity(object);
        }
        else if("xwfProgramRuntime".equalsIgnoreCase(object.getName()))
        {
            result = destroyProgram(object,true);
        }        
        else
        {
            result = destroyObject(object);
        }            
        logger.finer(LoggerMessageLocalizer.getMessage("ENDING_DETROYING_OBJECT")+" " + object.getBoui());
        return result;
    }
    /**
     * Remove um boObject genérico.
     * @param object boObject genérico a remover.
     * @return TRUE caso remova , FALSE caso contário
     * @throws netgest.bo.runtime.boRuntimeException
     */
    private static boolean destroyObject(boObject object)  throws boRuntimeException
    {
        boolean result = false;
        if(object != null)
        {
            boObjectList list = boObjectList.list(object.getEboContext(),"SELECT /*NO_SECURITY*/ xwfActivity WHERE variables.value.valueObject = " + object.getBoui() +" or  variables.value.valueList  = " + object.getBoui(), true,false );                                                                                
            if(list.getRecordCount() == 0)
            {
                object.destroyForce();
                result = true;
            }                     
        }
        return result;
    }
    /**
     * Remove mensagens e outros objectos no seu contexto.
     * @param message Mensagem a remover.
     * @param first  Indica se a mensagem é objecto inicial ou não, do processo de remoção.
     * @return TRUE caso remova , FALSE caso contário
     * @throws netgest.bo.runtime.boRuntimeException
     */
    private static boolean destroyMessage(boObject message, boolean first)  throws boRuntimeException
    {
        boolean result = false;
        List binary = null;        
        boObject activity = null;
        boObjectList list = null;
        if(message != null)
        {
            List documents = new ArrayList();
            list = boObjectList.list(message.getEboContext(),"SELECT /*NO_SECURITY*/ xwfActivity ext WHERE message.value.valueObject = " + message.getBoui(), true,false );
            if(list.getRecordCount() == 0)
            {
                
                binary = message.getObjectBinary().getBinary();
                boolean usedTemplate = message.getAttribute("usedTemplate").getValueLong() > 0; 
                if(binary != null) documents.addAll(binary);
                bridgeHandler anexos = message.getBridge("documents");
                if(anexos != null)
                {
                    anexos.beforeFirst();
                    while(anexos.next())
                    {
                        documents.add(anexos.getObject());
                    }
                }
                
                message.destroyForce();
                
                if(!message.exists() && documents.size() > 0)
                {
                    destroyDocuments(documents, usedTemplate);
                }
                
                result = true;                
            }
            else if(first && list.getRecordCount() == 1)
            {
                list.beforeFirst();
                list.next();
                activity = list.getObject();
                result = destroyActivity(activity);                                                            
            }            
        }
        return result;
    }
    /**
     * Remove actividades do workflow.
     * @param activity  Actividade do Workflow a remover.
     * @return TRUE caso remova , FALSE caso contário
     * @throws netgest.bo.runtime.boRuntimeException
     */
    private static boolean destroyActivity(boObject activity)  throws boRuntimeException
    {
        boolean result = false;
        if(activity != null)
        {
            EngineGate engine = new xwfEngineGate(activity.getEboContext());
            List values = new ArrayList();
            bridgeHandler bridge = null;            
            boObject variable = null;
            boObject varValue = null;
            boObject message = null;
                                    
            if(activity.getAttribute("message") != null)
            {
                variable = activity.getAttribute("message").getObject();  
                if(variable != null)
                {
                    values.add(String.valueOf(variable.getBoui()));
                    varValue = variable.getAttribute("value").getObject();  
                    if(varValue != null)
                    {
                        values.add(String.valueOf(varValue.getBoui()));
                        message = varValue.getAttribute("valueObject").getObject();
                        Helper.onBeforeDestroyMessage(message);
                    }                               
                }                
            }            
            
            long boui = 0;            
            if("xwfWaitResponse".equals(activity.getName())) 
            {
                variable = activity.getAttribute("saveOn").getObject();
                if(variable != null)
                {
                    values.add(String.valueOf(variable.getBoui()));
                    boui = variable.getAttribute("value").getValueLong();
                    if(boui > 0)
                    {
                        values.add(String.valueOf(boui));
                    }                                                                       
                }
            }
            else if("xwfActivityChoice".equals(activity.getName()))
            {
                bridge  = activity.getBridge("options"); 
                bridge.beforeFirst();
                while(bridge.next())
                {
                    values.add(String.valueOf(bridge.getObject().getBoui()));
                }                
            }
            else if("xwfActivityDecision".equals(activity.getName()))
            {
                boui = activity.getAttribute("no").getValueLong();
                if(boui > 0)
                {
                    values.add(String.valueOf(boui));
                }
                boui = activity.getAttribute("yes").getValueLong();
                if(boui > 0)
                {
                    values.add(String.valueOf(boui));
                }                                            
            }     
            
            
            bridge = activity.getBridge("variables");
            bridge.beforeFirst();
            while(bridge.next())
            {
                variable = bridge.getObject();
                values.add(String.valueOf(variable.getBoui()));
                boui = variable.getAttribute("value").getValueLong();
                if(boui > 0)
                {
                    values.add(String.valueOf(boui));
                }                                
            }
            
            xwfAnnounceImpl.removeAnnouncer(activity);
            
            values.add(String.valueOf(activity.getBoui()));
            
            destroy(engine,values);

            if(message != null) 
            {
                destroyMessage(message,false);
            }                        
            
            AttributeHandler programAttr = activity.getAttribute("program");
            if(programAttr != null)
            {            
                destroyProgram(programAttr.getObject(), false);
            }
            
            result = true;
        }   
        return result;
    }
    /**
     * Remove os Programas de Workflow.
     * @param program  Programa de Workflow a remover.
     * @param first  Indica se o programa é objecto inicial ou não, do processo de remoção.
     * @return TRUE caso remova , FALSE caso contário
     * @throws netgest.bo.runtime.boRuntimeException
     */
    private static boolean destroyProgram(boObject program, boolean first) throws boRuntimeException
    {        
        boolean result = false;
        if(program != null)
        {
            boObjectList activitys = boObjectList.list(program.getEboContext(),"SELECT /*NO_SECURITY*/ xwfActivity WHERE program = " + program.getBoui(),true,false);
            if(activitys.getRecordCount() == 0)
            {     
                boObject variable = null;
                boObject varValue = null;
                ArrayList objectsToDestroy = new ArrayList();
                EngineGate engine = new xwfEngineGate(program.getEboContext(),program);                

                // Participantes
                bridgeHandler bridge = program.getBridge("participants");
                bridge.beforeFirst();
                while ( bridge.next() )
                {
                    variable = bridge.getObject();
                    objectsToDestroy.add( variable );
                    varValue = variable.getAttribute("value").getObject();
                    if ( varValue!= null )
                    {
                        objectsToDestroy.add( varValue );
                    }                    
                }
                
                // Variaveis
                bridge = program.getBridge("variables");
                bridge.beforeFirst();                
                while ( bridge.next() )
                {
                    variable = bridge.getObject();
                    objectsToDestroy.add( variable );
                    varValue = variable.getAttribute("value").getObject();
                    if ( varValue!= null )
                    {
                        objectsToDestroy.add( varValue );
                    }
                }
            
                // Waits
                boObjectList list = engine.getBoManager().listObject("SELECT /*NO_SECURITY*/ xwfWait WHERE program = " + program.getBoui(),false,false);
                list.beforeFirst();
                while(list.next())
                {
                    objectsToDestroy.add( list.getObject() );
                }
            
                // SerialObjects
                list = engine.getBoManager().listObject("SELECT /*NO_SECURITY*/ xwfSerialObject WHERE program = " + program.getBoui(),false,false);
                list.beforeFirst();
                while(list.next())
                {
                    objectsToDestroy.add( list.getObject() );
                }
            
                // Messagens
                bridge = program.getBridge("message");
                bridge.beforeFirst();
                ArrayList messages = new ArrayList();
                while ( bridge.next() )
                {
                    variable = bridge.getObject();
                    messages.add( variable );
                }
                
                // ReadList
                bridge =  program.getBridge("READLIST");
                if(bridge != null)
                {
                    bridge.truncate();
                }                                                
                
                
                for (int i = 0; i < objectsToDestroy.size(); i++) 
                {
                    ( (boObject) objectsToDestroy.get(i)).destroyForce();
                }
                
                program.destroyForce();
                
                for (int i = 0; i < messages.size(); i++) 
                {
                    destroyMessage((boObject)messages.get(i),false);
                }      
                
                result = true;
            }  
            else if(first)
            {
                boObject activity = null;
                activitys.beforeFirst();
                while(activitys.next())
                {
                    activity = activitys.getObject();
                    result = destroyActivity(activity);
                    if(!result) result = false;                    
                }
            }
        }
        return result;
    }    
    
    /**
     * Remove uma lista de documentos.
     * @param documents, lista de documentos a remover.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public static void destroyDocuments(List documents, boolean usedTemplate) throws boRuntimeException
    {
        boObject document = null;
        for (int i = 0; i < documents.size(); i++) 
        {
            document = (boObject)documents.get(i);
            if(document.getAttribute("fileName").getValueString().endsWith(".eml"))
            {
                logger.finest(LoggerMessageLocalizer.getMessage("DESTROY_EML"));                    
                document.destroy();                                    
            }
            else if(usedTemplate &&
                (Helper.isGeneratedDoc(document.getAttribute("fileName").getValueString()))
            )
            {
                document.destroy();          
            }
            else
            {
                Object creator =  document.getAttribute("CREATOR").getValueObject();
                if(creator == null)
                {
                    logger.finest(LoggerMessageLocalizer.getMessage("DESTROY_ATTACH"));
                    boObject[] referencedBy = document.getReferencedByObjects();
                    if( referencedBy != null && referencedBy.length == 0 )
                    {
                        document.destroy();
                    }
                }
            }                                                               
        }        
    }
    /**
     * Remove uma dada lista de bouis.
     * @param engine ligação ao motor do workflow
     * @param bouis lista de bouis para remover
     * @throws netgest.bo.runtime.boRuntimeException
     */
    private static void destroy(EngineGate engine, List bouis) throws boRuntimeException
    {
        boObject object = null;
        List binary = null;
        for (int i = 0; i < bouis.size(); i++) 
        {            
            object = engine.getObject(Long.parseLong((String)bouis.get(i)));
            if("message".equals(object.getName()) || 
               "message".equals(object.getBoDefinition().getBoSuperBo()) ||
               "messageSystem".equals(object.getBoDefinition().getBoSuperBo())
            )
            {
                binary = object.getObjectBinary().getBinary();
            }
            
            object.destroyForce();
            
            if(!object.exists() && binary != null)
            {                        
                destroyDocuments(binary, true);                                
            }                
        }    
    }
}