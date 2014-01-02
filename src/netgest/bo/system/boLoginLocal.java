/*Enconding=UTF-8*/
package netgest.bo.system;
import javax.ejb.EJBLocalObject;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.system.boSession;

public interface boLoginLocal extends EJBLocalObject  {
    public boSession boLogin(boApplication app, String repository, String clientName, String username, String password)
        throws boLoginException;

    public boSession boLogin(boApplication app, String repository, String clientName, String username, String password, HttpServletRequest request)
        throws boLoginException;

    public boSession boLogin(boApplication app, String repository, String clientName, String username, long time, long timeCheck)
        throws boLoginException;

    public boSession boLogin(boApplication app, String repository, String clientName, String username, long time, long timeCheck, HttpServletRequest request)
        throws boLoginException;
}