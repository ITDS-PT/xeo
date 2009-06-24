/*Enconding=UTF-8*/
package netgest.bo.system;
import java.rmi.RemoteException;

import javax.ejb.EJBObject;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.system.boSession;

public interface boLogin extends EJBObject  {
    public boSession boLogin(boApplication app, String repository, String clientName, String username, String password)
        throws RemoteException, boLoginException;

    public boSession boLogin(boApplication app, String repository, String clientName, String username, String password, HttpServletRequest request)
        throws RemoteException,boLoginException;
        
}