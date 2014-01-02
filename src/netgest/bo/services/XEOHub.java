/*Enconding=UTF-8*/
package netgest.bo.services;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import org.w3c.dom.*;
import oracle.xml.parser.v2.*;
import org.xml.sax.helpers.*;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;
import java.util.*;
import netgest.bo.def.*;
import netgest.bo.*;
import netgest.bo.system.*;
import javax.naming.*;
import java.rmi.*;
import javax.ejb.*;
import netgest.bo.ejb.boManagerLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import netgest.bo.utils.*;

public class XEOHub 
{
  private EboContext ebo_ctx=null;
  private String[] reservedAttributes={"CLASSNAME","PARENTCTX","SYS_DTCREATE","SYS_DTSAVE",
                                      "CREATOR","KEYS","TEMPLATE"};
                                      
  private byte currentAction=1;
  
  public XEOHub()
  {
  }
  
  /**
   * 
   * @webmethod 
   */
   public String authenticate(String username, String password)
   {
    String toRet=null;
    try
    {
      EboContext ectx=null;
      String repos=boConfig.getDefaultRepository();
      boConfigRepository rep = boConfig.getConfigRepository(repos);
      boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
      boSession session = boapp.boLogin(username,password);          
      
      ectx = session.createRequestContext(null,null,null);        
      this.ebo_ctx=ectx;
      toRet=session.getId();
      
      HubSession hsession=new HubSession(session,session.getId(),ectx,new Date());
      HubSessions.addHubSession(hsession);
    }
    catch(boLoginException e)
    {
      return e.getMessage();
    }
    return toRet;
  }
  /**
   * 
   */
  public Element update(Element xeodocument,String sessionid)
  {
    try
    {      
      Element toRet=this.checkSession(sessionid);
      if (toRet==null){
        toRet=this.action(xeodocument,boObject.MODE_EDIT);      
        HubSessions.updateSessionTimeStamp(sessionid);
      }
      return toRet;
    }
    catch(boRuntimeException e)
    {
      return this.createExceptionXML(e.getErrorCode(),e.getMessage());      
    }
  }


  /**
   * 
   */
  public Element destroy(Element xeodocument,String sessionid)
  {
    try
    {      
      Element toRet=this.checkSession(sessionid);
      if (toRet==null){
        toRet=this.action(xeodocument,boObject.MODE_DESTROY);      
        HubSessions.updateSessionTimeStamp(sessionid);
      }
      return toRet;
    }
    catch(boRuntimeException e)
    {
      return this.createExceptionXML(e.getErrorCode(),e.getMessage());      
    }
  }

  private Element action(Element xeodocument,byte action)
  {
   Vector objs=null;
   try
   {   
    this.currentAction=action;
    objs=this.parseXEOXML((XMLElement)xeodocument,null,null);
    for (int i=0;i<objs.size();i++)
    {
      boObject currObj=(boObject)objs.get(i);      
      currObj.setUpdateMode(action);         
      boObject.getBoSecurityManager().updateObject(ebo_ctx,currObj);
    }    
   }
   catch (boRuntimeException e)
   {
    return this.createExceptionXML(e.getErrorCode(),e.getMessage());
   }
    return createReturnMsgXML(objs);
  }
  
  /**
   * 
   * @webmethod 
   */
  public Element getNewObject(String name,String sessionid)
  {
    try
    {
      Element toRet=this.checkSession(sessionid);
      if (toRet==null){      
        boObject bobj=boObject.getBoManager().createObject(this.ebo_ctx,name);
        exportObject expobj=new exportObject(bobj,null,true);
        XMLDocument retDoc=new XMLDocument();
        XMLElement xmlelem = (XMLElement)retDoc.createElement("XEO"); 
        XMLDocument doc=expobj.saveXML("");          
        XMLElement elemen=(XMLElement)doc.getLastChild().getLastChild();        
        Node auxNode=retDoc.importNode(elemen,true);
        xmlelem.appendChild(auxNode);                  
        toRet=xmlelem;
        HubSessions.updateSessionTimeStamp(sessionid);
      }
      return toRet;
    }
    catch (boRuntimeException e)
    {
     return this.createExceptionXML(e.getErrorCode(),e.getMessage());
    }    
  }
  
  /**
   * 
   * @webmethod 
   */
  public Element getObjects(String name, String qry,String sessionid)
  {
    try
    {
      Element toRet=this.checkSession(sessionid);
      if (toRet==null){      
        String boql="Select "+name+" where "+qry;
        boObjectList list=boObjectList.list(this.ebo_ctx,boql);
        toRet=this.ObjectListToXML(list);
        HubSessions.updateSessionTimeStamp(sessionid);
      }
      return toRet;
    }
    catch (boRuntimeException e)
    {
     return this.createExceptionXML(e.getErrorCode(),e.getMessage());
    }
  }

  /**
   * 
   * @webmethod 
   */
  public Element getRequestChangedObject(String name, String qry,String sessionid)
  {
    try
    {
      Element toRet=this.checkSession(sessionid);
      if (toRet==null){      
        String boql="Select "+name+" where "+qry;
        boObject obj = boObject.getBoManager().loadObject(this.ebo_ctx,boql);
          
        toRet=this.ObjectToXML(netgest.bo.utils.boRequest.getChangedObject(obj));
        
        HubSessions.updateSessionTimeStamp(sessionid);
      }
      return toRet;
    }
    catch (boRuntimeException e)
    {
     return this.createExceptionXML(e.getErrorCode(),e.getMessage());
    }
  }

  /**
   * 
   * @webmethod 
   */
  public Element getObject(long boui,String sessionid)
  {
    try
    {
      Element toRet=this.checkSession(sessionid);
      if (toRet==null){
        boObjectList list=boObjectList.list(this.ebo_ctx,boui);
        toRet=this.ObjectListToXML(list);
        HubSessions.updateSessionTimeStamp(sessionid);
      }
      return toRet;
    }    
    catch(boRuntimeException e)
    {
      return this.createExceptionXML(e.getErrorCode(),e.getMessage());      
    } 
  }

  /**
   * 
   * @webmethod 
   */
  public Element execute(String xeoql,String sessionid)
  {
    try
    {
      Element toRet=this.checkSession(sessionid);
      if (toRet==null){
        boObjectList list=boObjectList.list(this.ebo_ctx,xeoql);
        toRet=this.ObjectListToXML(list);
        HubSessions.updateSessionTimeStamp(sessionid);
      }
      return toRet;
    }
    catch (boRuntimeException e)
    {
      return this.createExceptionXML(e.getErrorCode(),e.getMessage());
    }
  }
  
  private Vector parseXEOXML(XMLElement doc,AttributeHandler parentAttr,boObject parentObject) throws boRuntimeException
  {
    Vector auxVec=new Vector();
    if (doc!=null &&(doc.getNodeName().equalsIgnoreCase("xeo") || doc.getNodeName().equalsIgnoreCase("objectattribute"))) //is xeo Document
    {      
      XMLNodeList list=(XMLNodeList)doc.getChildrenByTagName("Object");      
      for (int i=0;i<list.getLength();i++)
      {
        boObject obj=null;
        XMLElement element=(XMLElement)list.item(i);
        String sboui=element.getAttribute("boui");        
        long boui=0;
        if (sboui!=null && !sboui.equals(""))
        {
          boui=new Long(sboui).longValue();        
          if (boui>0)obj=boObject.getBoSecurityManager().loadObject(ebo_ctx,boui);
        }
        else
        {
            String classname=element.getAttribute("name");
            obj=boObject.getBoSecurityManager().createObject(ebo_ctx,classname);            
        }
        //faz binding dos atributos do XML com o objecto actual
        if (obj!=null && parentAttr==null)auxVec.add(bindObject(obj,parentAttr,parentObject,element));
        else bindObject(obj,parentAttr,parentObject,element);
      }
    }
    else //not XEO
    {
      
    }
    return auxVec;
  }
  
  private boObject bindObject(boObject obj,AttributeHandler parentAttr,boObject parentObject,XMLElement element) throws boRuntimeException
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
       AttributeHandler objattr=null;
       if ("yes".equals(isInBridge) && parentObject!=null)
       {
        attrsInBridge.add(currElement);
        continue;
       }
       else
        objattr=obj.getAttribute(attrName);
        
       this.setAttributeValue(objattr,attrName,attrValue,obj);
    }
    //Get and set Attributes of Object/bridge type
    for (int i=0;i<objectattrslist.getLength();i++)
    {     
      XMLElement currElement=(XMLElement)objectattrslist.item(i);
      String attrName=currElement.getAttribute("name");
      AttributeHandler objattr=obj.getAttribute(attrName);      
      if (objattr!=null)
      {
        if(notSystemAttribute(attrName))parseXEOXML(currElement,objattr,obj);
      }
      else  throw new boRuntimeException(MessageLocalizer.getMessage("THE_ATTRIBUTE")+" "+attrName+" "+MessageLocalizer.getMessage("DOES_NOT_EXIST_IN")+" "+obj.getName(),"", null,
                "" + obj.getBoui());
    }
    
    if (parentAttr!=null)
    {
      if (parentAttr.isBridge())
      {
        bridgeHandler bridge=parentObject.getBridge(parentAttr.getName());
        boObject auxObj=this.locateInBridge(bridge,obj.getBoui());
        
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
          this.setAttributeValue(objattr,attrName,attrValue,obj);
        }
      }
      else 
      {
        parentObject.getAttribute(parentAttr.getName()).setValueLong(obj.getBoui());        
      }
      parentObject.getUpdateQueue().add(obj, currentAction);
    }     
    return obj;
  }

  private boManagerLocalHome getboManagerLocalHome() throws NamingException
  {
    final InitialContext context = new InitialContext();
    return (boManagerLocalHome)context.lookup("java:comp/env/ejb/local/boManagerLocal");
  }

  private boolean notSystemAttribute(String attrName)
  {
    boolean toRet=true;
    for (int i=0;i<this.reservedAttributes.length;i++)
    {
      if (attrName.equals(this.reservedAttributes[i]))
      {
        toRet=false;
        break;
      }      
    }
    return toRet;
  }

  private void setAttributeValue(AttributeHandler objattr, String attrName,String attrValue,boObject obj) throws boRuntimeException
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
  private boObject locateInBridge(bridgeHandler bridge,long boui) throws boRuntimeException
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
  
  private Element createExceptionXML(String code,String message)
  {
      XMLDocument xmldoc = new XMLDocument();
      XMLElement xmlelem = (XMLElement)xmldoc.createElement("xeo");   
      
      XMLElement xmlelem1 = (XMLElement)xmldoc.createElement("xeoerror");   
      
      XMLElement xmlelem2 = (XMLElement)xmldoc.createElement("code"); 
      xmlelem2.addText(code);
      xmlelem1.appendChild(xmlelem2);
      
      xmlelem2 = (XMLElement)xmldoc.createElement("message");   
      xmlelem2.addText(message);
      xmlelem1.appendChild(xmlelem2);    
      xmlelem.appendChild(xmlelem1);    
      xmldoc.appendChild(xmlelem);
      return xmldoc.getDocumentElement();         
  }
  
 private Element ObjectListToXML(boObjectList list)
 {
    try
    {
      XMLDocument retDoc=new XMLDocument();
      XMLElement xmlelem = (XMLElement)retDoc.createElement("XEO"); 
      boolean cont=true;
      while (cont)
      {
        list.beforeFirst();
        while (list.next())
        {
          boObject currObject=list.getObject();
          exportObject expobj=new exportObject(currObject,null,true);
          XMLDocument doc=expobj.saveXML("");          
          XMLElement elemen=(XMLElement)doc.getLastChild().getLastChild();        
          Node auxNode=retDoc.importNode(elemen,true);
          xmlelem.appendChild(auxNode);          
        }
        if (list.getPage()<list.getPages()) 
        {
          list.nextPage();
          cont=true;
        }
        else cont=false;        
      }
      return xmlelem;
    }
    catch(boRuntimeException e)
    {
      return this.createExceptionXML(e.getErrorCode(),e.getMessage());      
    }  
 }

 private Element ObjectToXML(boObject obj)
 {
    try
    {
      XMLDocument retDoc=new XMLDocument();
      XMLElement xmlelem = (XMLElement)retDoc.createElement("XEO"); 
      boolean cont=true;
      exportObject expobj=new exportObject(obj,null,true);
      XMLDocument doc=expobj.saveXML("");          
      XMLElement elemen=(XMLElement)doc.getLastChild().getLastChild();        
      Node auxNode=retDoc.importNode(elemen,true);
      xmlelem.appendChild(auxNode);          

      return xmlelem;
    }
    catch(boRuntimeException e)
    {
      return this.createExceptionXML(e.getErrorCode(),e.getMessage());      
    }  
 } 
   private Element createReturnMsgXML(Vector objs)
  {
      XMLDocument xmldoc = new XMLDocument();
      XMLElement xmlelem = (XMLElement)xmldoc.createElement("xeo");   
      
      XMLElement xmlelem1 = (XMLElement)xmldoc.createElement("returnStatus");   
      
      XMLElement xmlelem2 = (XMLElement)xmldoc.createElement("message"); 
      xmlelem2.addText("Action "+parseCurrentAction()+" Succeeded");
      xmlelem1.appendChild(xmlelem2);
      
      xmlelem2 = (XMLElement)xmldoc.createElement("ObjectsAffected");   
      for (int i=0;i<objs.size();i++)
      {
        XMLElement xmlelem3 = (XMLElement)xmldoc.createElement("Object"); 
        long currBoui=((boObject)objs.get(i)).getBoui();
        xmlelem3.setAttribute("boui",new Long(currBoui).toString());
        xmlelem2.appendChild(xmlelem3);
      }
      xmlelem1.appendChild(xmlelem2);    
      xmlelem.appendChild(xmlelem1);    
      xmldoc.appendChild(xmlelem);
      return xmldoc.getDocumentElement();         
  }
  
  private String parseCurrentAction()
  {
    if (this.currentAction==boObject.MODE_DESTROY) return "delete";
    else if (this.currentAction==boObject.MODE_EDIT) return "add/update";
    else return "";
  }
 
  private Element checkSession(String sessionid)
  {
    try
    {
      HubSession session=HubSessions.getHubSession(sessionid);
      this.ebo_ctx = session.getEboContext();
      return null;
    }
    catch(boRuntimeException e)
    {
      return this.createExceptionXML(e.getErrorCode(),e.getMessage());      
    }  
  }
}