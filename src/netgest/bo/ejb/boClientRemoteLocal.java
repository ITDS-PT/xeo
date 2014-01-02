/*Enconding=UTF-8*/
package netgest.bo.ejb;
import javax.ejb.EJBLocalObject;
import netgest.bo.runtime.EboContext;

public interface boClientRemoteLocal extends EJBLocalObject 
{
    long getDocument(String sessionId, String client, String boui, long timestamp);
    long getDocument(EboContext ctx, String client, String boui, long timestamp);

    String getVersion();

    byte[] getFile(String sessionId, long boui);



}