/*Enconding=UTF-8*/
package netgest.bo.controller.xwf;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.controller.Controller;
import netgest.bo.controller.Navigator;
import netgest.bo.controller.basic.BasicNavigator;
import netgest.bo.controller.common.PathItem;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.utils.ClassUtils;

/**
 * <p>Title: XwfNavigator </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class XwfNavigator implements Navigator
{
    private XwfController controller = null;
    private Hashtable baseNavigatorList = new Hashtable();
    private Hashtable indexBase = new Hashtable();
    
    
    public XwfNavigator(XwfController controller) 
    {
        this.controller = controller;                        
    }       
    public Controller getController()
    {
        return this.controller;
    }
    private String getNavigatorKey(String key)
    {
        String masterKey = null;
        if(indexBase.containsKey(key))
        {
            masterKey = (String)indexBase.get(key);
            masterKey = getNavigatorKey(masterKey);
        }
        else
        {
            masterKey = key;
        }
        return masterKey;
    }
    private BasicNavigator getNavigator(long boui)
    {
        BasicNavigator result = null;
        String aux = null;
        if(baseNavigatorList.containsKey(String.valueOf(boui)))
        {
            result = (BasicNavigator)baseNavigatorList.get(String.valueOf(boui));
        }
        else
        {
            aux = getNavigatorKey(String.valueOf(boui));
            if(aux != null)
            {
                  result = (BasicNavigator)baseNavigatorList.get(aux);
            }
        }     
        return result;   
    }
    private void setNavigator(long boui,BasicNavigator navigator)
    {
        this.baseNavigatorList.put(String.valueOf(boui),navigator);        
    }    
    public void clearNavigators()
    {
        baseNavigatorList.clear();
    }
    public StringBuffer renderLinkForPriorPage() throws boRuntimeException
    {        
        StringBuffer result = null; 
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.renderLinkForPriorPage();            
        }
        catch (Exception ex) 
        {            
        }
        return result;    
    }

    public StringBuffer renderLinkForNextPage() throws boRuntimeException
    {
        StringBuffer result = null; 
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.renderLinkForNextPage();            
        }
        catch (Exception ex) 
        {            
        }
        return result;    
    }

    public StringBuffer renderLinkForHomePage() throws boRuntimeException
    {
        StringBuffer result = null; 
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.renderLinkForNextPage();            
        }
        catch (Exception ex) 
        {            
        }
        return result;      
    }

    public StringBuffer renderLinkForCancelEdit() throws boRuntimeException
    {
        StringBuffer result = null; 
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.renderLinkForCancelEdit();            
        }
        catch (Exception ex) 
        {            
        }
        return result;    
    }

    public String getImageLinkForPriorPage()
    {
        String result = null; 
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.getImageLinkForPriorPage();            
        }
        catch (Exception ex) 
        {            
        }
        return result;
    }

    public String getImageLinkForNextPage()
    {
        String result = null;
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.getImageLinkForNextPage();            
        }
        catch (Exception ex) 
        {            
        }
        return result;
    }

    public String getImageLinkForHomePage()
    {
        String result = null;
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.getImageLinkForHomePage();            
        }
        catch (Exception ex) 
        {            
        }
        return result;
    }

    public String getImageLinkForCancelEdit()
    {
        String result = null;
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.getImageLinkForCancelEdit();            
        }
        catch (Exception ex) 
        {            
        }    
        return result;
    }

    public ArrayList getHistory()
    {
        ArrayList result = null;
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.getHistory();            
        }
        catch (Exception ex) 
        {            
        }        
        return result;
    }

    public int getHistoryPointer()
    {
        int result = -1;
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.getHistoryPointer();            
        }
        catch (Exception ex) 
        {            
        }        
        return result;
    }

    public void setHistoryPointer(int position)
    {
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            navigator.setHistoryPointer(position);            
        }
        catch (Exception ex) 
        {            
        }     
    }

    public void setRoot(boObject object, boolean isMasterDoc, HttpServletRequest request) throws boRuntimeException
    {    
        if(object != null)
        {
            BasicNavigator navigator = null;
//            String parentBoui = request.getParameter("parent_boui");
            String parentBoui = request.getParameter("ctxParent");
            if(parentBoui != null && !"".equals(parentBoui))
            {
                navigator = getNavigator(ClassUtils.convertToLong(parentBoui));   
                if(navigator != null)
                {                    
                    this.indexBase.put(String.valueOf(object.getBoui()),parentBoui);                    
                }
            }   
            else
            {
                navigator = getNavigator(object.getBoui());   
            }            
            if(navigator == null)
            {
                navigator = new BasicNavigator(controller);
                if(!"list".equalsIgnoreCase(request.getParameter("method")))
                {
                    setNavigator(object.getBoui(),navigator);
                }
            }
            navigator.setRoot(object, isMasterDoc, request);
        }
    }

    public boolean isPathObjectsChanged()
    {
        boolean result = false;
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.isPathObjectsChanged();            
        }
        catch (Exception ex) 
        {            
        }
        return result;
    }

    public PathItem getPathItemById(String id)
    {
        PathItem result = null;
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.getPathItemById(id);            
        }
        catch (Exception ex) 
        {            
        }       
        return result;
    }

    public String processPathRequest(boObject object, boolean isMasterDoc, HttpServletRequest request) throws boRuntimeException
    {
        String result = null;
        if(controller.getDocHTML().getMasterBoList() != null)
        {
            if(controller.getDocHTML().getMasterBoList().getObject() != null)
            {
                BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
                if(navigator != null)
                {
                    result = navigator.processPathRequest(object, isMasterDoc, request);
                }
            }
        }
        return result;
    }

    public ArrayList getCompletePath(PathItem item)
    {
        ArrayList result = null;
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            result = navigator.getCompletePath(item);            
        }
        catch (Exception ex) 
        {            
        }    
        return result;
    }

    public void renderLinkToNextPage(PathItem item, StringBuffer toRet, boObject object, String url)
    {
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            navigator.renderLinkToNextPage(item, toRet, object, url);            
        }
        catch (Exception ex) 
        {            
        }        
    }

    public void renderLinkToCancelBeforeNextPage(PathItem item, StringBuffer toRet, boObject object, String url)
    {
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            navigator.renderLinkToCancelBeforeNextPage(item, toRet, object, url);            
        }
        catch (Exception ex) 
        {            
        }    
    }

    public void renderLinkToSaveBeforeNextPage(PathItem item, StringBuffer toRet, boObject object, String url)
    {
        try 
        {
            BasicNavigator navigator = getNavigator(controller.getDocHTML().getMasterBoList().getObject().getBoui());
            navigator.renderLinkToSaveBeforeNextPage(item, toRet, object, url);            
        }
        catch (Exception ex) 
        {            
        }            
    }
   
}