/*Enconding=UTF-8*/
package netgest.bo.ejb;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;

public interface boClientRemote extends EJBObject 
{    
    void upload(String sessionId, String boui, byte[] buff) throws RemoteException;    
    long getDocument(String sessionId, String client, String boui) throws RemoteException;
    void upload(String sessionId, String boui, String fileName, byte[] buff) throws RemoteException;
    String newDocument(String sessionId, byte[] buff, String fileName, String template) throws RemoteException;
    long getDocument(String sessionId, String client, String boui, long timestamp) throws RemoteException;
    boolean getNewVersion(String userHostClient) throws RemoteException;
    String getVersion() throws RemoteException;

    byte[] getFile(String sessionId, long boui) throws RemoteException;



}