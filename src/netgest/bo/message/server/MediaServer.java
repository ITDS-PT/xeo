/*Enconding=UTF-8*/
package netgest.bo.message.server;

import netgest.bo.controller.xwf.XwfController;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public interface MediaServer
{
    public void read() throws boRuntimeException;

    public boolean mergeDocuments(boObject message) throws boRuntimeException;

    public boolean send(boObject message, boolean saveBinary) throws boRuntimeException;
    
    public boolean send(Object context, boObject message, boolean saveBinary) throws boRuntimeException;

    public boObject sendReceipt(boObject message, boObject performer) throws boRuntimeException;

    public boolean deleteMessage(String messageid) throws boRuntimeException;

    public boolean deleteMessage(boObject message) throws boRuntimeException;
    
    public boolean releaseMsg(boObject message, boObject performer) throws boRuntimeException;
}
