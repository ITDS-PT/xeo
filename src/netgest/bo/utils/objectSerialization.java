/*Enconding=UTF-8*/
package netgest.bo.utils;
import java.io.*;
import java.util.*; 
import netgest.bo.*;
import netgest.bo.def.*;
import netgest.bo.impl.*;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;
import netgest.utils.*;
import javax.servlet.http.HttpServletResponse;
import oracle.xml.parser.v2.*;
import netgest.bo.services.*;


public class objectSerialization 
{

private static String[] reservedAttributes={"CLASSNAME","PARENTCTX","SYS_DTCREATE","SYS_DTSAVE",
                                      "KEYS","TEMPLATE"};
                                      
  public objectSerialization()
  {
  }
  
  public static XMLDocument boObjectToXML(boObject obj) throws boRuntimeException
  {
    exportObject exp = new exportObject(obj, null, true);
    return exp.saveXML(null);
    
  }
  
  public static boObject xmlToBoObject(XMLDocument doc, EboContext ebo_ctx) throws boRuntimeException
  {
    XEOHub xh = new XEOHub();
    Vector vo = parseXEOXML((XMLElement)doc.getDocumentElement(), null, null, ebo_ctx);
    
    boObject ret = null;
    if(vo.size() > 0)
      ret = (boObject)vo.firstElement();
    return ret;
  }
  
  private static Vector parseXEOXML(XMLElement doc,AttributeHandler parentAttr,boObject parentObject, EboContext ebo_ctx) throws boRuntimeException
  {
    Vector auxVec=new Vector();
    String docname = doc.getNodeName();
    if (doc!=null &&(doc.getNodeName().equalsIgnoreCase("xeo") || doc.getNodeName().equalsIgnoreCase("objectattribute"))) //is xeo Document
    {      
      XMLNodeList list=(XMLNodeList)doc.getChildrenByTagName("Object");      
      for (int i=0;i<list.getLength();i++)
      {
        boObject obj=null;
        XMLElement element=(XMLElement)list.item(i);
        String sboui=element.getAttribute("boui"); 
        String sname=element.getAttribute("name");
        long boui=0;
        if (sboui!=null && !sboui.equals(""))
        {
          boui=new Long(sboui).longValue();        
          if (boui>0)
            try{
              obj=boObject.getBoManager().loadObject(ebo_ctx,boui);
            }catch(Exception e)
            {
              obj = boObject.getBoManager().createObject(ebo_ctx, sname, boui);
            }
        }
        else
        {
            String classname=element.getAttribute("name");
            obj=boObject.getBoManager().createObject(ebo_ctx,classname);            
        }
        //faz binding dos atributos do XML com o objecto actual
        if (obj!=null && parentAttr==null)
          auxVec.add(bindObject(obj,parentAttr,parentObject,element, ebo_ctx));
        else 
          bindObject(obj,parentAttr,parentObject,element, ebo_ctx);
      }
    }
    else //not XEO
    {

    }
    return auxVec;
  }
  
  private static boObject bindObject(boObject obj,AttributeHandler parentAttr,boObject parentObject,XMLElement element, EboContext ebo_ctx) throws boRuntimeException
  {    
    XMLNodeList attrslist=(XMLNodeList)element.getChildrenByTagName("Attribute");
    XMLNodeList objectattrslist=(XMLNodeList)element.getChildrenByTagName("ObjectAttribute");   
    Vector attrsInBridge=new Vector();
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
        objattr=obj.getAttribute(attrName);
       if( objattr != null)
        setAttributeValue(objattr,attrName,attrValue,obj);
    }
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
        objattr=obj.getAttribute(attrName);      
      if (objattr!=null)
      {
        if(notSystemAttribute(attrName))
          parseXEOXML(currElement,objattr,obj, ebo_ctx);
      }/*
      else  throw new boRuntimeException("The Attribute "+attrName+" does not exist in "+obj.getName(),"", null,
                "" + obj.getBoui());*/
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
  
  private static boolean notSystemAttribute(String attrName)
  {
    boolean toRet=true;
    for (int i=0;i<reservedAttributes.length;i++)
    {
      if (attrName.equals(reservedAttributes[i]))
      {
        toRet=false;
        break;
      }      
    }
    return toRet;
  }

  private static void setAttributeValue(AttributeHandler objattr, String attrName,String attrValue,boObject obj) throws boRuntimeException
  {
    if(objattr!=null)
    { 
        if (notSystemAttribute(objattr.getName()))
        {
          byte attrtype=objattr.getDefAttribute().getValueType();
          if (attrtype==boDefAttribute.VALUE_BOOLEAN)
          {
            if (attrValue.equals("1") || attrValue.equalsIgnoreCase("yes")||attrValue.equalsIgnoreCase("true") )
                objattr.setValueString("1");
            if (attrValue.equals("0")|| attrValue.equalsIgnoreCase("no") || attrValue.equalsIgnoreCase("false"))
                objattr.setValueString("0");            
          }
          else objattr.setValueString(attrValue);          
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
}