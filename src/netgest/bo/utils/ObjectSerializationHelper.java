/*Enconding=UTF-8*/
package netgest.bo.utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import netgest.bo.def.boDefAttribute;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.services.XEOHub;

import netgest.io.*;
import netgest.utils.ClassUtils;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNodeList;

public class ObjectSerializationHelper 
{
    
    private static String[] reservedAttributes = {"CLASSNAME",
                                                  "PARENTCTX",
                                                  "SYS_DTCREATE",
                                                  "SYS_DTSAVE",
                                                  "KEYS",
                                                  "TEMPLATE"};
                                        
    public static XMLDocument serialize(boObject object) throws boRuntimeException
    {
        Hashtable pattern = new Hashtable();
        pattern.put("1","true");
        pattern.put("0","false");
        ObjectExport objectExport = new ObjectExport(object, null, true,pattern);
        return objectExport.saveXML(null);    
    }
  
    public static boObject deserialize(EboContext context,XMLDocument xmlDocument) throws boRuntimeException
    {
        boObject result = null;
        Vector vo = parseXEOXML((XMLElement)xmlDocument.getDocumentElement(), null, null, context);        
        if(vo.size() > 0)
        {
            result = (boObject)vo.firstElement();   
        }        
        return result;
    }
    public static boObject deserialize(EboContext context,XMLDocument xmlDocument, boObject object) throws boRuntimeException
    {      
        boObject result = null;
        XMLNodeList objectList = (XMLNodeList)((XMLElement)xmlDocument.getDocumentElement()).getChildrenByTagName("Object");
        if(objectList.getLength() == 1)
        {
            XMLElement element = (XMLElement)objectList.item(0);
            if(element != null)
            {
                if(object.getBoui() ==  ClassUtils.convertToLong(element.getAttribute("boui"),-1))
                {
                    result = bindObject(object,null,null,element, context);
                }
            }
        }
        return result;
    }    
  
    private static Vector parseXEOXML(XMLElement doc,AttributeHandler parentAttr,boObject parentObject, EboContext context) throws boRuntimeException
    {
        Vector result = new Vector();
        String docname = doc.getNodeName();
        if (doc!=null &&(doc.getNodeName().equalsIgnoreCase("xeo") || doc.getNodeName().equalsIgnoreCase("objectattribute"))) //is xeo Document
        {      
            XMLNodeList list = (XMLNodeList)doc.getChildrenByTagName("Object");
            boObject object = null;
            if(parentObject != null)
            {
                bridgeHandler bridge = parentObject.getBridge(parentAttr.getName());
                if(bridge != null)
                {
                    bridge.truncate(); /* TODO em vez de ir buscar as bridges, e depois fazer o truncate, por o objecto com bridges vazias */                    
                }
            }            
            for (int i=0;i<list.getLength();i++)
            {                
                XMLElement element = (XMLElement)list.item(i);                     
                object = getObject(context,element);
                //faz binding dos atributos do XML com o objecto actual
                if (object != null && parentAttr == null)
                {
                    result.add(bindObject(object,parentAttr,parentObject,element, context));    
                }                    
                else
                {
                    bindObject(object,parentAttr,parentObject,element, context);    
                }                    
            }
        }
        return result;
    }
    private static boObject bindObject(boObject object,AttributeHandler parentAttr,boObject parentObject,XMLElement element, EboContext ebo_ctx) throws boRuntimeException
    {   
        List attrsInBridge = new ArrayList();
        
        bindAttributes(object,element,parentObject,attrsInBridge);
        
        bindObjectAttributes(object,element,parentObject,attrsInBridge,ebo_ctx);

    
        if (parentAttr!=null)
        {
            if (parentAttr.isBridge())
            {
                bridgeHandler bridge=parentObject.getBridge(parentAttr.getName());
                boObject auxObj=locateInBridge(bridge,object.getBoui());
                
                if (auxObj==null)
                {
                    bridge.add(object.getBoui());        
                }
                for (int i=0;i<attrsInBridge.size();i++)
                {
                    XMLElement currElement=(XMLElement)attrsInBridge.get(i);
                    String attrName=currElement.getAttribute("name");
                    String attrValue=currElement.getText();
                    AttributeHandler objattr=bridge.getAttribute(attrName);
                    setAttributeValue(objattr,attrName,attrValue,object);
                }
            }
            else 
            {
                parentObject.getAttribute(parentAttr.getName()).setValueLong(object.getBoui());        
            }
            parentObject.getUpdateQueue().add(object, boObject.MODE_EDIT);
        }     
        return object;
    }
    /*
    private static boObject bindObject(boObject obj,AttributeHandler parentAttr,boObject parentObject,XMLElement element, EboContext ebo_ctx) throws boRuntimeException
    {    
        XMLNodeList attrslist = (XMLNodeList)element.getChildrenByTagName("Attribute");        
        Vector attrsInBridge = new Vector();
        //Get and set Attributes of simple type
        for (int i=0;i<attrslist.getLength();i++)
        {
            XMLElement currElement=(XMLElement)attrslist.item(i);
            String attrName=currElement.getAttribute("name");
            String isInBridge=currElement.getAttribute("isInBridge");
            String attrValue=currElement.getText();
            if(attrValue.equals(""))
                continue;
            AttributeHandler objattr=null;
            if ("yes".equals(isInBridge) && parentObject!=null)
            {
                attrsInBridge.add(currElement);
                continue;
            }
            else
            {
                objattr=obj.getAttribute(attrName);
            }
            if( objattr != null)
            {
                setAttributeValue(objattr,attrName,attrValue,obj);   
            }                
        }
        
        XMLNodeList objectattrslist = (XMLNodeList)element.getChildrenByTagName("ObjectAttribute");
        //Get and set Attributes of Object/bridge type
        for (int i=0;i<objectattrslist.getLength();i++)
        {     
            XMLElement currElement=(XMLElement)objectattrslist.item(i);
            String attrName=currElement.getAttribute("name");
            String isInBridge=currElement.getAttribute("isInBridge");
            String attrValue=currElement.getText();
            AttributeHandler objattr=null;
            if ("yes".equals(isInBridge) && parentObject!=null)
            {
                attrsInBridge.add(currElement);
                continue;
            }
            else
            {
                objattr=obj.getAttribute(attrName);
            }
            if (objattr!=null)
            {
                if(!isReservedAttribute(attrName))
                {
                    parseXEOXML(currElement,objattr,obj, ebo_ctx);   
                }                    
            }
        }
    
        if (parentAttr!=null)
        {
            if (parentAttr.isBridge())
            {
                bridgeHandler bridge=parentObject.getBridge(parentAttr.getName());
                boObject auxObj=locateInBridge(bridge,obj.getBoui());
                
                if (auxObj==null)
                {
                    bridge.add(obj.getBoui());        
                }
                for (int i=0;i<attrsInBridge.size();i++)
                {
                    XMLElement currElement=(XMLElement)attrsInBridge.get(i);
                    String attrName=currElement.getAttribute("name");
                    String attrValue=currElement.getText();
                    AttributeHandler objattr=bridge.getAttribute(attrName);
                    setAttributeValue(objattr,attrName,attrValue,obj);
                }
            }
            else 
            {
                parentObject.getAttribute(parentAttr.getName()).setValueLong(obj.getBoui());        
            }
            parentObject.getUpdateQueue().add(obj, boObject.MODE_EDIT);
        }     
        return obj;
    }
  */  
    private static boolean isReservedAttribute(String attrName)
    {
        boolean result = false;        
        for (int i = 0; i < reservedAttributes.length && !result; i++) 
        {
            if (attrName.equals(reservedAttributes[i]))
            {
                result = true;
            }      
        }
        return result;
    }
    
    private static void setAttributeValue(AttributeHandler attributeHandler, String attrName,String attrValue,boObject obj) throws boRuntimeException
    {
        if(attributeHandler!=null)
        { 
            if (!isReservedAttribute(attributeHandler.getName()))
            {
                byte attrtype = attributeHandler.getDefAttribute().getValueType();
                if (attrtype == boDefAttribute.VALUE_BOOLEAN)
                {
                    if (attrValue.equals("1") || 
                        attrValue.equalsIgnoreCase("yes") ||
                        attrValue.equalsIgnoreCase("true") )
                    {
                        attributeHandler.setValueString("1");   
                    }                        
                    if (attrValue.equals("0")|| 
                        attrValue.equalsIgnoreCase("no") || 
                        attrValue.equalsIgnoreCase("false"))
                    {
                        attributeHandler.setValueString("0");   
                    }                                    
                }
                else
                {
                    attributeHandler.setValueString(attrValue);
                }
            }
        } //Colocar excepção correcta
        else throw new boRuntimeException(MessageLocalizer.getMessage("THE_ATTRIBUTE")+" "+attrName+" "+MessageLocalizer.getMessage("DOES_NOT_EXIST_IN")+" "+obj.getName(),"", null,
            "" + obj.getBoui());         
    }
  
  //metodo necessário pq aparentemente o getObject(boui) da bridge não funciona correctamente
    private static boObject locateInBridge(bridgeHandler bridge,long boui) throws boRuntimeException
    { 
        boolean cont=true;
        while (cont)
        {      
            bridge.beforeFirst();    
            while (bridge.next())
            {
                bridge.getPages();
                boObject auxObj=bridge.getObject();
                if (auxObj.getBoui()==boui) 
                {
                    return auxObj;
                }
            }
            if (bridge.getPage()<bridge.getPages()) 
            {
                bridge.nextPage();
                cont=true;
            }
            else cont=false;
        }
        return null;
    }
    
    
    
    private static void bindAttributes(boObject object,XMLElement element,boObject parentObject,List attrsInBridge) throws boRuntimeException
    {       
        XMLElement currElement = null;
        String attrName = null;
        String isInBridge = null;
        String attrValue = null;
        AttributeHandler objAttr = null;        
        XMLNodeList attrslist = (XMLNodeList)element.getChildrenByTagName("Attribute");                        
        for (int i=0;i<attrslist.getLength();i++)
        {
            currElement=(XMLElement)attrslist.item(i);
            attrValue = currElement.getText();
            if(!"".equals(attrValue))
            {
                attrName = currElement.getAttribute("name");
                isInBridge = currElement.getAttribute("isInBridge");                
                if ("yes".equals(isInBridge) && parentObject != null)
                {
                    attrsInBridge.add(currElement);                    
                }
                else
                {
                    objAttr = object.getAttribute(attrName);
                    if( objAttr != null)
                    {
                        setAttributeValue(objAttr,attrName,attrValue,object);   
                    }                                    
                }                    
            }
        }
    }
    private static void bindObjectAttributes(boObject object,XMLElement element,boObject parentObject,List attrsInBridge, EboContext context) throws boRuntimeException
    {
        XMLElement currElement = null;
        String attrName = null;
        String isInBridge = null;
        String attrValue = null;
        AttributeHandler objAttr = null;            
        XMLNodeList objectAttrsList = (XMLNodeList)element.getChildrenByTagName("ObjectAttribute");
        for (int i = 0; i < objectAttrsList.getLength(); i++) 
        {            
            currElement = (XMLElement)objectAttrsList.item(i);
            attrName=currElement.getAttribute("name");
            isInBridge=currElement.getAttribute("isInBridge");
            attrValue=currElement.getText();            
            if ("yes".equals(isInBridge) && parentObject!=null)
            {
                attrsInBridge.add(currElement);                
            }
            else
            {
                objAttr = object.getAttribute(attrName);
                if (objAttr!=null)
                {
                    if(!isReservedAttribute(attrName))
                    {
                        parseXEOXML(currElement,objAttr,object, context);
                    }                    
                }                
            }
        }        
    }
    private static boObject getObject(EboContext context,XMLElement element)  throws boRuntimeException
    {
        boObject result = null;        
        if(element != null)
        {
            long boui = ClassUtils.convertToLong(element.getAttribute("boui"),-1);
            if(boui > 0)
            {
                try
                {
                    result = boObject.getBoManager().loadObject(context,boui);
                }
                catch(Exception e)
                {
                    result = boObject.getBoManager().createObject(context, element.getAttribute("name"), boui);
                }                        
            }
            else
            {                
                result = boObject.getBoManager().createObject(context,element.getAttribute("name"));            
            }
        }
        return result;
    }    
}