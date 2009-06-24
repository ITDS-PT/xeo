/*Enconding=UTF-8*/
package netgest.bo.controller.basic;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.controller.Controller;
import netgest.bo.controller.Navigator;
import netgest.bo.controller.Presentation;
import netgest.bo.controller.basic.BasicPresentation;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.elements.ExplorerServer;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

/**
 * <p>Title: BasicController </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class BasicController implements Controller 
{

    private docHTML dochtml = null;        
    private Presentation presentation = null;
    private Navigator navigator = null;
    public HttpServletRequest request = null;
   
    public BasicController(docHTML dochtml)
    {
        this.dochtml = dochtml; 
        this.presentation = new BasicPresentation(this);
        this.navigator = new BasicNavigator(this);
    }      
    public void bindData(String XMLValues,docHTML_controler DOCLIST, HttpServletRequest request) throws boRuntimeException
    {
        if(!XMLValues.toLowerCase().startsWith("<explorer"))
        {
            getDocHTML().bindData(XMLValues,DOCLIST,request);
        }
        else
        {
            ExplorerServer.bindData(XMLValues);
        }        
    }
    public void buildRequestBoList(docHTML_controler DOCLIST,HttpServletRequest request) throws boRuntimeException
    {
        getDocHTML().buildRequestBoList(DOCLIST);
    }   
    
    public docHTML getDocHTML()
    {
       return dochtml;
    }
    
    public String getName()
    {
       return "BasicController";
    }
    public void setPresentation(Presentation presentation)
    {
        this.presentation = presentation;
    }
    public Presentation getPresentation()
    {
        return presentation;
    }
    public void setNavigator(Navigator navigator)
    {
        this.navigator = navigator;
    }    
    public Navigator getNavigator()
    {
        return navigator;
    }    
    public boObject getObject(long boui) throws boRuntimeException
    {
        return dochtml.getObject(boui);
    }
    public void readRequest(HttpServletRequest request)
    {
        dochtml.readRequest(request);
        this.request = request;        
    }
    public void cleanCache()
    {
    }
    public HttpServletRequest getRequest()
    {
        return this.request;
    }
}