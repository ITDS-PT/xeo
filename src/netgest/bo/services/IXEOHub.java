/*Enconding=UTF-8*/
package netgest.bo.services;
/**
 * Generated by the Oracle JDeveloper 10g Web Services Interface Generator
 * Date Created: Wed Oct 20 15:20:55 GMT 2004
 * 
 * This interface lists the subset of public methods that you
 * selected for inclusion in your web service's public interface.
 * It is referenced in the web.xml deployment descriptor for this service.
 * 
 * This file should not be edited.
 */

public interface IXEOHub 
{
  public String authenticate(String username, String password);

  public String update(String xeodocument, String sessionid) throws Exception;

  public String destroy(String xeodocument, String sessionid) throws Exception;

  public String getNewObject(String name, String sessionid) throws Exception;

  public String getObjects(String name, String qry, String sessionid) throws Exception;

  public String getRequestChangedObject(String name, String qry, String sessionid) throws Exception;

  public String getObject(long boui, String sessionid) throws Exception;

  public String execute(String xeoql, String sessionid) throws Exception;
}
