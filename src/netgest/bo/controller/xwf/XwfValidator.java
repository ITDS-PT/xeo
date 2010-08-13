/*Enconding=UTF-8*/
package netgest.bo.controller.xwf;

import netgest.bo.def.boDefMethod;
import netgest.bo.dochtml.docHTML;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.xwf.EngineGate;
import netgest.xwf.common.xwfHelper;
import netgest.utils.ngtXMLHandler;

/**
 * <p>Title: XwfValidator </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public final class XwfValidator 
{
    private final static String MSG_ATTRIBUTE_FULFILL_REQUIRED = "Campo de preenchimento obrigatório";
    private final static String MSG_BRIDGE_FULFILL_REQUIRED = "Campo de preenchimento obrigatório";
    private final static String MSG_FULFILL_REQUIRED = "Campo de preenchimento obrigatório";
    private final static String MSG_EXECUTED_REQUIRED = "Acção obrigatória";
         
    public static boolean validate(docHTML doc, boObject object, String code) throws boRuntimeException
    {
        boolean result = true;
        if(XwfKeys.ACTION_OPEN_KEY.equals(code))
        {
            result = validForOpen(doc,object);
        }
        else if(XwfKeys.ACTION_CLOSE_KEY.equals(code))
        {
            result = validForClose(doc,object);
        }
        else if(XwfKeys.ACTION_CANCEL_KEY.equals(code) || code.startsWith(XwfKeys.ACTION_CANCEL_KEY))
        {
            result = validForCancel(doc,object);
        }        
        else if(XwfKeys.ACTION_SAVE_KEY.equals(code))
        {
             result = validForUpdate(doc,object);
        }
        else if(XwfKeys.ACTION_SEND_KEY.equals(code))
        {
             result = validActivitySend(doc,object);
        }     
        else if(code.startsWith(XwfKeys.ACTION_LAUNCH_KEY))
        {
             result = valid(doc,object);
        }      
        else if(code.startsWith(XwfKeys.ACTION_REASSIGN_KEY))
        {
             result = valid(doc,object);
        }    
        else if(code.startsWith(XwfKeys.ACTION_CREATE_PROGRAM_KEY))
        {
             result = valid(doc,object);
        }
        else if(code.startsWith(XwfKeys.ACTION_TRANSFER_PROGRAM_KEY))
        {
             result = valid(doc,object);
        }
        return result;
    }
    
    private static boolean validForOpen(docHTML doc, boObject activity) throws boRuntimeException
    {
        boolean result = false;
        if(validVariablesForOpen(doc,activity))
        {
            if(validMethodsForOpen(doc,activity))
            {
                result = true;
            }
        }
        return result;
    }

    private static boolean validForClose(docHTML doc, boObject activity) throws boRuntimeException
    {
        boolean result = false;
        if(validVariablesForClose(doc,activity))
        {
            if(validMethodsForClose(doc,activity))
            {
                result = true;
            }
        }        
        return result;
    }
    private static boolean validForCancel(docHTML doc, boObject activity) throws boRuntimeException
    {
        boolean result = false;
        if(validVariablesForCancel(doc,activity))
        {
            if(validMethodsForCancel(doc,activity))
            {
                result = true;
            }
        }        
        return result;
    }    
    private static boolean validForUpdate(docHTML doc, boObject activity) throws boRuntimeException
    {
        boolean result = true;
        if("xwfActivitySend".equals(activity.getName()))
        {
            result =  validActivitySend(doc,activity);
        }
        else
        {
            result =  validVariablesForUpdate(doc,activity);    
        }
        return result;
        
    }      
    private static boolean validActivitySend(docHTML doc, boObject activity) throws boRuntimeException
    {
        boObject variable = activity.getAttribute("message").getObject();
        return isObjectValid(doc,variable);
    }

    public static boolean valid(docHTML doc, boObject object) throws boRuntimeException
    {
        boolean result = true;
        if(!object.valid())
        {
            result = false;
            doc.renderErrors(object);            
        }
        return result;
    }          
    private static boolean validVariablesForOpen(docHTML doc, boObject activity) throws boRuntimeException
    {
        boolean result = true;
        
        return result;        
    }    
    private static boolean validMethodsForOpen(docHTML doc, boObject activity) throws boRuntimeException
    {
        boolean result = true;
        
        return result;        
    }        
    private static boolean validVariablesForClose(docHTML doc, boObject activity) throws boRuntimeException
    {
        boolean result = true;
        boObject variable = null;
        String required = null;
        bridgeHandler bridge = activity.getBridge("variables");
        bridge.beforeFirst();                
        while ( bridge.next()) 
        {               
            variable = bridge.getObject();
            if(isRequired(variable))
            {
                if(!isFilled(doc,variable))
                {                    
                    result = false;
                }
            }
            if(!isObjectValid(doc,variable))
            {                    
                result = false;
            }                
        }
        return result;        
    }   
    private static boolean validVariablesForUpdate(docHTML doc, boObject activity) throws boRuntimeException
    {
        boolean result = true;
        boObject variable = null;
        String required = null;
        bridgeHandler bridge = activity.getBridge("variables");
        bridge.beforeFirst();                
        while ( bridge.next()) 
        {               
            variable = bridge.getObject();
            boObject varValue = variable.getAttribute("value").getObject();
            String valueType = xwfHelper.getTypeName(varValue);
            long showMode = variable.getAttribute("showMode").getValueLong();
            if("valueObject".equals(valueType) && showMode == 0)
            {
                boObject value = varValue.getAttribute(valueType).getObject();
                if(value.exists())
                {
                    if(!isObjectValid(doc,variable))
                    {                    
                        result = false;
                    }
                }
            }
        }
        return result;        
    }        
    
    private static boolean validMethodsForClose(docHTML doc, boObject activity) throws boRuntimeException
    {
        boolean result = true;
        boObject variable = null;
        boObject method = null;
        bridgeHandler bridge = activity.getBridge("variables");
        bridge.beforeFirst();                
        while ( bridge.next()) 
        {               
            variable = bridge.getObject();
            if(!isMethodExecuted(doc,variable,"requireMethods"))
            {                
                result = false;
            }        
        }        
        return result;        
    }
    private static boolean validVariablesForCancel(docHTML doc, boObject activity) throws boRuntimeException
    {
        boolean result = true;
        
        return result;        
    }    
    private static boolean validMethodsForCancel(docHTML doc, boObject activity) throws boRuntimeException
    {
        boolean result = true;
        
        return result;        
    }
    private static boolean isFilled(docHTML doc,boObject variable) throws boRuntimeException
    {
        boolean result = false;
        boObject varValue = variable.getAttribute("value").getObject();
        String valueType = xwfHelper.getTypeName(varValue);
        EngineGate engine = ((XwfController)doc.getController()).getEngine();
        if("valueList".equals(valueType))
        {
            bridgeHandler list = varValue.getBridge(valueType);
            if(list.getRecordCount() > 0)
            {
                result = true;    
            }
            else
            {
                doc.addErrorMessage(buildMessage(variable.getAttribute("label").getValueString(),MSG_BRIDGE_FULFILL_REQUIRED));
            }            
        }        
        else        
        {
            Object value = engine.getBoManager().getValueObject(varValue);
//            Object value = varValue.getAttribute(valueType).getValueObject();
            if(value != null)
            {
                result = true;                
            }
            else
            {
                doc.addErrorMessage(buildMessage(variable.getAttribute("label").getValueString(),MSG_ATTRIBUTE_FULFILL_REQUIRED));
            }
        }
        return result;                
    }
    private static boolean isObjectValid(docHTML doc,boObject variable) throws boRuntimeException
    {
        boolean result = true;
        boObject varValue = variable.getAttribute("value").getObject();
        String valueType = xwfHelper.getTypeName(varValue);
        long showMode = variable.getAttribute("showMode").getValueLong();
        if("valueObject".equals(valueType) && showMode == 0)
        {
            boObject value = varValue.getAttribute(valueType).getObject();
            if(value != null)
            {
                if(!value.valid())
                {
                    result = false;    
                    doc.renderErrors(value);
                }                
            }
        }
        return result;        
    }
    public static boolean isRequired(boObject variable) throws boRuntimeException
    {
        boolean result = false;                   
        String input = variable.getAttribute("required").getValueString();   
        if("1".equals(input))
        {
            result = true;
        }        
        return result;
    }
    private static boolean isMethodExecuted(docHTML doc,boObject variable,String methodType) throws boRuntimeException
    {
        boolean result = true;                   
        String listOfMethods = variable.getAttribute(methodType).getValueString();
        if(listOfMethods != null && !"".equals(listOfMethods))
        {
            ngtXMLHandler source = new ngtXMLHandler(listOfMethods).getChildNode(methodType);
            if(source != null && !"".equals(source))
            {
                ngtXMLHandler[] methods = source.getChildNodes();
                for (int i = 0; i < methods.length; i++) 
                {            
                    if(!"1".equals(methods[i].getAttribute("executed")))
                    {                
                        result = false;
                        doc.addErrorMessage(buildMessage(getMethodLabel(variable,methods[i].getAttribute("name")),MSG_EXECUTED_REQUIRED));
                    }
                }
            }
        }
        return result;
    }        
    private static String getMethodLabel(boObject variable, String name) throws boRuntimeException
    {
        String label = null;
        boObject varValue = variable.getAttribute("value").getObject();
        boObject valueObject = varValue.getAttribute("valueObject").getObject();
        if(valueObject != null)
        {
            boDefMethod[] userMethods = valueObject.getBoDefinition().getBoMethods();
            for (int i=0; i< userMethods.length && label == null ; i++ ) 
            {
                if(name.equals(userMethods[i].getName()))
                {
                    label = userMethods[i].getLabel();
                }
            }                    
        }
        return label;
    }    
    private static String buildMessage(String label,String msg) throws boRuntimeException
    {
        return label + " : " + msg;
    }
}