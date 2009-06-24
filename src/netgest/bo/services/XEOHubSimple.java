/*Enconding=UTF-8*/
package netgest.bo.services;
import netgest.utils.*;
import org.w3c.dom.*;
import oracle.xml.parser.v2.*;
import org.xml.sax.helpers.*;
import java.io.*;

public class XEOHubSimple 
{
  public XEOHubSimple()
  {
  }
  
  /**
   * 
   * @webmethod 
   */
   public String authenticate(String username, String password)
   {
    XEOHub hub=new XEOHub();
    return hub.authenticate(username,password);
   }
   
  /**
   * 
   * @webmethod 
   */
  public String update(String xeodocument,String sessionid) throws Exception
  {
    XEOHub hub=new XEOHub();
    Element elem=ngtXMLUtils.loadXML(xeodocument).getDocumentElement();        
    elem=hub.update(elem,sessionid);
    return  ngtXMLUtils.getXML((XMLDocument)elem.getOwnerDocument());
  }
  
  /**
   * 
   * @webmethod 
   */
  public String destroy(String xeodocument,String sessionid) throws Exception
  { 
    XEOHub hub=new XEOHub();
    Element doc=ngtXMLUtils.loadXML(xeodocument).getDocumentElement();
    doc=hub.destroy(doc,sessionid);
    return  ngtXMLUtils.getXML((XMLDocument)doc.getOwnerDocument());
  }
  
  /**
   * 
   * @webmethod 
   */
  public String getNewObject(String name,String sessionid) throws Exception
  {
    XEOHub hub=new XEOHub();
    Element elem=hub.getNewObject(name,sessionid);      
    return  this.toRet(elem);
  }
  
  /**
   * 
   * @webmethod 
   */
  public String getObjects(String name, String qry,String sessionid) throws Exception
  {
    XEOHub hub=new XEOHub();
    Element doc=hub.getObjects(name,qry,sessionid);
    return  this.toRet(doc);
  }

  /**
   * 
   * @webmethod 
   */
  public String getRequestChangedObject(String name, String qry,String sessionid) throws Exception
  {
    XEOHub hub=new XEOHub();
    Element doc=hub.getRequestChangedObject(name,qry,sessionid);
    return  this.toRet(doc);
  }  
  /**
   * 
   * @webmethod 
   */
  public String getObject(long boui,String sessionid) throws Exception
  {
    XEOHub hub=new XEOHub();
    Element doc=hub.getObject(boui,sessionid);
    return  this.toRet(doc);
  }
  
  /**
   * 
   * @webmethod 
   */
  public String execute(String xeoql,String sessionid) throws Exception
  {
    XEOHub hub=new XEOHub();
    Element doc=hub.execute(xeoql,sessionid);
    return this.toRet(doc);
  }
 
  private String toRet(Element elem) throws Exception
  {
    XMLDocument xmldoc = new XMLDocument();
    Node node1=xmldoc.importNode(elem,true);
    xmldoc.appendChild(node1);
    return ngtXMLUtils.getXML(xmldoc);
  }
}