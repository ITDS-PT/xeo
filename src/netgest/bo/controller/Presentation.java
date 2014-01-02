/*Enconding=UTF-8*/
package netgest.bo.controller;

import java.io.IOException;

import java.lang.StringBuffer;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

/**
 * <p>Title: Presentation </p>
 * <p>Description: Todas os controladores de fluxo de ecrãs têm que implementar esta interface.</p>
 * @Copyright: Copyright (c) 2004</p>
 * @Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public interface Presentation 
{
    public docHTML getDocHTML();
    public Controller getController();
    public String getType();
    public void setType(String type);
    public String writeJspHeader() throws boRuntimeException;
    public String writeJspFooter(boObject object,boObjectList currObjectList, Hashtable options, boolean isMasterDoc,HttpServletRequest request) throws boRuntimeException;    
//    public String writeJspFooter(boObject object,boObjectList currObjectList, String method, String inputMethod, String requestedBoui, boolean isMasterDoc,String p_typeForm, HttpServletRequest request,String jspName) throws boRuntimeException;
    public String writeCSS() throws boRuntimeException;
    public String writeJS() throws boRuntimeException;        
    public void writeToolBar(docHTML_controler DOCLIST, PageContext pageContext,boObjectList currObjectList) throws boRuntimeException,IOException;
    public void writeHeaderHandler(docHTML_controler DOCLIST,PageContext pageContext) throws boRuntimeException,IOException;
    public String writeFooterHandler() throws boRuntimeException;    
    public StringBuffer renderPath( HttpServletRequest request ) throws boRuntimeException;
}