/*Enconding=UTF-8*/
package netgest.bo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

/**
 * <p>Title: Controller </p>
 * <p>Description: Todas os controladores de fluxo de negócio têm que implementar esta interface.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public interface Controller 
{   
    //New
    public docHTML getDocHTML();
    public void setPresentation(Presentation presentation);
    public Presentation getPresentation();
    public void setNavigator(Navigator navigator);
    public Navigator getNavigator();
    public String getName();
    public void cleanCache();
    public HttpServletRequest getRequest();
    //From the dochtml
    public void bindData(String XMLValues,docHTML_controler DOCLIST, HttpServletRequest request) throws boRuntimeException;
    public void buildRequestBoList(docHTML_controler DOCLIST,HttpServletRequest request) throws boRuntimeException;
    public boObject getObject(long boui) throws boRuntimeException;
    public void readRequest(HttpServletRequest request);
}