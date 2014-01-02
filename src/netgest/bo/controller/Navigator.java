/*Enconding=UTF-8*/
package netgest.bo.controller;

import java.util.ArrayList;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.controller.common.PathItem;
import netgest.bo.dochtml.docHTML;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
/**
 * <p>Title: Navigator </p>
 * <p>Description: Todas os controladores de navegação entre boObject têm que implementar esta interface.</p>
 * @Copyright: Copyright (c) 2004</p>
 * @Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public interface Navigator 
{    
    public Controller getController();
    
    public StringBuffer renderLinkForPriorPage() throws boRuntimeException;
    public StringBuffer renderLinkForNextPage() throws boRuntimeException;    
    public StringBuffer renderLinkForHomePage() throws boRuntimeException;    
    public StringBuffer renderLinkForCancelEdit() throws boRuntimeException;
    
    public String getImageLinkForPriorPage();
    public String getImageLinkForNextPage();
    public String getImageLinkForHomePage();
    public String getImageLinkForCancelEdit();
    
    public ArrayList getHistory();
    public int getHistoryPointer();
    public void setHistoryPointer(int position);
    
    public void setRoot( boObject object , boolean isMasterDoc, HttpServletRequest request ) throws boRuntimeException;

    public boolean isPathObjectsChanged();    
    public PathItem getPathItemById(String id);
    public String processPathRequest(boObject object, boolean isMasterDoc, HttpServletRequest request) throws boRuntimeException;    
    
    public ArrayList getCompletePath(PathItem item );
    
    public void renderLinkToNextPage(PathItem item,StringBuffer toRet, boObject object, String url);
    public void renderLinkToCancelBeforeNextPage(PathItem item,StringBuffer toRet, boObject object, String url);
    public void renderLinkToSaveBeforeNextPage(PathItem item,StringBuffer toRet, boObject object, String url);
    
}