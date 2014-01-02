/*Enconding=UTF-8*/
package netgest.bo.controller.basic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.boConfig;

import netgest.bo.controller.Controller;
import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.Navigator;
import netgest.bo.controller.common.PathItem;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.dochtml.docHTML;
import netgest.bo.message.MessageServer;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.utils.ClassUtils;

/**
 * <p>Title: BasicNavigator </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class BasicNavigator implements Navigator
{

    private Controller controller = null;
    
    //do docHTML_path
    private long rootObjectBoui = -1;    
    private PathItem firstPathItem;
//    private ArrayList p_userPath;
    private ArrayList p_items = new ArrayList();
    private int p_itemsnumber = 0;
    
    // do dochtml
    private  ArrayList p_pathHistory = null;
    private  int p_pathHistoryPointer = -1;
    
    
    public BasicNavigator(Controller controller) 
    {
        this.controller = controller;                        
    }    
    public Controller getController()
    {
        return controller;
    }
    private docHTML getDocHTML()
    {
        return controller.getDocHTML();
    }    
    public int getHistoryPointer()
    {
        return p_pathHistoryPointer;
    }
    public void setHistoryPointer(int position)
    {
        p_pathHistoryPointer = position;
    }    
    public ArrayList getHistory()
    {
        return p_pathHistory;
    }
    public ArrayList getPathItemStack()
    {
        return p_items;
    }
    public long getRootObjectBoui()
    {
        return rootObjectBoui;
    }
    public PathItem getPathItemById(String id)
    {
        if(p_items.size() > ClassUtils.convertToInt(id)-1)
        {
            return ( PathItem ) p_items.get(ClassUtils.convertToInt(id) - 1);
        }
        else
        {
            return null;
        }
    }    
    public void setRoot(boObject object, boolean isMasterDoc, HttpServletRequest request) throws boRuntimeException
    {        
        if ( object != null )
        {
            // Primeiro If é desnecessario????????????
            if (isMasterDoc && getDocHTML().getMasterObject().equals(object)) 
            {
                setRoot(object,object.getBoui(), request);
            }   
            if(getHistory() == null)
            {            
                setRoot(object,object.getBoui(), request);
            }
        }
    }
    public void setRoot( boObject obj ,long realObjectBoui,HttpServletRequest request ) throws boRuntimeException
    {        
        String method = getMethod(request);
        String requestedBoui = String.valueOf(obj.getBoui());
        Hashtable parameters = getParameters(request,requestedBoui,method);            
        String url = request.getRequestURI(); 
        request.setAttribute("pathItem","1");                                
        setRootItem(url,parameters,obj);        
        setHistoryPointer(0);
        p_pathHistory = new ArrayList();
        PathItem item = getPathItemById("1");
        if(item != null)
        {
            item.setRealObjectBoui(realObjectBoui);
            getHistory().add( item );
        }
    } 
    private void setRootItem( String url, Hashtable parameters, boObject object) throws boRuntimeException
    {                
        firstPathItem = newPathItem(url, parameters,object.getBoui());
        rootObjectBoui = object.getBoui();
        buildPriorPath(firstPathItem, rootObjectBoui);            
    }
    public PathItem newPathItem(String url, Hashtable parameters, long relatedBoui)
    {
        p_itemsnumber++;
        PathItem item = new PathItem(getDocHTML(), p_itemsnumber, url, parameters, relatedBoui);
        p_items.add(item);
        return item;
    }       
    public Hashtable getParameters(HttpServletRequest request, String requestedBoui, String method)  throws boRuntimeException
    {        
        Enumeration parms = request.getParameterNames();
        Hashtable parameters = new Hashtable();
        while ( parms.hasMoreElements() )
        {
            String pname =  parms.nextElement().toString();
            String parameter ="";
            boolean addParameter =
            !(
            pname.equals("myIDX") ||
            pname.equals("boFormSubmitMode") ||
            pname.equals("boiChanged") ||
            pname.equals("toClose" ) ||
            pname.equals("orphan") ||
            pname.equals("boFormSubmitSecurity") ||
            pname.equals("boFormSubmitId") ||
            pname.equals("editingTemplate") ||
            pname.equals("parent_boui") ||
            pname.equals("boFormSubmitXml")
            );
            if( addParameter )            
            {
                if ( pname.equalsIgnoreCase("method") )
                {                  
                    parameter = method;                    
                }
                else
                {
                    try
                    {
                        parameter = java.net.URLEncoder.encode( request.getParameter( pname ), boConfig.getEncoding() );
                    }
                    catch( Exception ex )
                    {
                        System.out.println(ex);
                    }
                }
                parameters.put(pname, parameter );
            }
        }   
        if ( requestedBoui != null && parameters.get( "boui" ) == null )
        {
            parameters.put("boui",requestedBoui );
        }
        
        if ( requestedBoui != null && parameters.get( "BOUI" ) == null )
        {
            parameters.put("BOUI",requestedBoui );
        }
        parameters.put("docid", String.valueOf(getDocHTML().getDocIdx()) );
        parameters.put("controllerName", getController().getName() );
        
        return parameters;
    }    
    public String getMethod(HttpServletRequest request)
    {
        String method = request.getParameter( "method" );            
        if( method !=null ) 
        { 
            if( method.equalsIgnoreCase("newfromtemplate") || method.equalsIgnoreCase("duplicate") 
                || method.equalsIgnoreCase("forward") || method.equalsIgnoreCase("new") 
                || getDocHTML().getMethod().equalsIgnoreCase("reply")
                || getDocHTML().getMethod().equalsIgnoreCase("replyAll"))  
            { 
                method = "edit";                
            }   
        }
        return method;
    }
    public PathItem getPathItem(HttpServletRequest request)
    {
        PathItem pathItem = null;
//        String forceNewPathItem = (String)request.getAttribute("forceNewPathItem");
//        if(forceNewPathItem == null || "".equals(forceNewPathItem))
//        {
            String id_pathItem = request.getParameter( "pathItem" );
            if ( id_pathItem == null )
            {
                id_pathItem = (String)request.getAttribute("pathItem");
            }
            if (id_pathItem != null)
            {
                long realPathIf = ClassUtils.convertToLong(id_pathItem,-1);
                if(realPathIf != -1)
                {
                    pathItem = getPathItemById( id_pathItem  );
                }
            }
//        }
        return pathItem;
    }    
    private void buildPriorPath(PathItem childItem, long childBoui) throws boRuntimeException
    {
        boObject o = getDocHTML().getObject(childBoui);
        boObject[] parents = o.getParents();

        for (int i = 0; i < parents.length; i++)
        {
            if(!"xwfActivity".equals(parents[i].getName()) 
                    && !"xwfActivity".equals(parents[i].getBoDefinition().getBoSuperBo()))
            {
                p_itemsnumber++;
    
                PathItem pp = new PathItem(getDocHTML(), p_itemsnumber, parents[i].getBoui());
                p_items.add(pp);
                childItem.addParentPathItem(pp, "PARENT");
                pp.addChildPathItem(childItem, "PARENT");
                if (parents[i].getBoui() != childBoui)
                {
                    String s = parents[i].getBoDefinition().getBoSuperBo();
                    
                        buildPriorPath(pp, parents[i].getBoui());
                }
                else
                {
                    //apagar ??  
                }
            }
        }
    }
    public boolean isPathObjectsChanged()
    {
        boolean ret = false;      
        for (int i = 0; i < p_itemsnumber; i++) 
        {
            PathItem pI = (PathItem) p_items.get(i);
            if(pI.getUrl() == null) continue;
        
            try
            {
                boObject obj =  getDocHTML().getObject(pI.getRelatedBoui());        
                ret |= obj.isChanged();
            }
            catch (boRuntimeException e){}
        }
        return ret;        
    }    
    // Separar este methodo
    public String processPathRequest(boObject object, boolean isMasterDoc, HttpServletRequest request) throws boRuntimeException
    {
        String result = null;
        if( object != null )
        {
            boolean existsPathItem = true;
            String id_pathItem = request.getParameter( "pathItem" );
            if ( id_pathItem == null )
            {
                existsPathItem = false;
                id_pathItem = (String)request.getAttribute( "pathItem" );
            }

            PathItem pathItem = getPathItem(request);
            if ( pathItem == null )
            {   
                // só entra aqui quando é a segunda vez        
                String method = getMethod(request);            
                String requestedBoui = null;
                if(object != null)
                {
                    requestedBoui = String.valueOf(object.getBoui());
                }
    
                Hashtable parameters = getParameters(request,requestedBoui,method);               
                String url = request.getRequestURI(); 
                
                if ( ClassUtils.convertToLong( requestedBoui ) > 0 )
                {
                    pathItem = newPathItem(  url, parameters, ClassUtils.convertToLong( requestedBoui ) );
                    request.setAttribute( "pathItem", String.valueOf(pathItem.getId()));
                }
    
            }
          
            
            String id_fromPathItem = request.getParameter( "fromPathItem" );
            String fromAttributeName = request.getParameter( "fromAttribute" );
            PathItem fromPathItem = null;
            if ( id_fromPathItem != null && !existsPathItem ) // novo ITEM PATH -- colocar parentesco
            {       
                fromPathItem = getPathItemById( id_fromPathItem  );
                
                fromPathItem.addChildPathItem( pathItem , fromAttributeName );
                pathItem.addParentPathItem( fromPathItem , fromAttributeName );
                
                if( object != null  )
                {
                    object.getThread().add( 
                            new BigDecimal(fromPathItem.getBoui()),
                            new BigDecimal(pathItem.getBoui())
                    );
                }
                
                while ( getHistory().size()-1 > getHistoryPointer() )
                {
                    getHistory().remove( getHistoryPointer() + 1 );
                }
                getHistory().add( pathItem );
                setHistoryPointer(getHistory().size()-1);
            }
            else
            {
                setHistoryPointer(getHistory().indexOf( pathItem ));
                if ( getHistoryPointer() == -1 && pathItem != null)
                {
                    getHistory().add( pathItem );
                    setHistoryPointer(getHistory().size()-1);
                }
                
            }
            
            result = getNextPage(object,pathItem,request);
        }
        return result;
    }
    public ArrayList getCompletePath(PathItem item)
    {
        ArrayList toRet = new ArrayList();
        ArrayList parentItems = item.getParents();        
        
        if( parentItems != null)
        {
            if ( parentItems.size() > 1 )
            {
                toRet.add("(");
                for (int i = 0; i < parentItems.size() ; i++) 
                {
                    toRet.add(  parentItems.get(i) );
                    toRet.add(  getCompletePath( (PathItem)parentItems.get(i)   ) );
                }
                
                toRet.add(")");
            }
            else if  ( parentItems.size() == 1 )
            {
                toRet.add( parentItems.get(0) );
                
                PathItem itemP = (PathItem)parentItems.get(0);
                if ( itemP.getParents() != null && itemP.getParents().size() > 0)
                {
                    toRet.add( getCompletePath( itemP ) );
                }
            }
        }
        
        return toRet;
    }
    
    public StringBuffer renderLinkForPriorPage() throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        if (getHistoryPointer() > 0)
        {
            PathItem it = ( PathItem ) getHistory().get(getHistoryPointer() - 1);
            toRet = it.renderLink(null, true, null, false);
        }
        return toRet;
    }

    public StringBuffer renderLinkForNextPage() throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        if (getHistoryPointer() < (getHistory().size() - 1))
        {
            PathItem it = ( PathItem ) getHistory().get(getHistoryPointer() + 1);
            PathItem itActual = ( PathItem ) getHistory().get(getHistoryPointer());

            boObject o = getDocHTML().getObject(itActual.getRelatedBoui());

            if (o.haveReferencesToObject_in_Memory(it.getRelatedBoui()))
            {
                toRet = it.renderLink(null, true, "fromNext", false);
            }
            else
            {
                while ((getHistory().size() - 1) > getHistoryPointer())
                {
                    getHistory().remove(getHistoryPointer() + 1);
                }
            }
        }
        return toRet;

    }

    public StringBuffer renderLinkForHomePage() throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        PathItem it = ( PathItem ) getHistory().get(getHistoryPointer());
        if (!("" + it.getId()).equals("1"))
        {
            toRet = this.getPathItemById("1").renderLink(null, true, null, false);
        }
        return toRet;
    }

    public StringBuffer renderLinkForCancelEdit() throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        if (getHistoryPointer() > 0)
        {
            PathItem it = ( PathItem ) getHistory().get(getHistoryPointer() - 1);
            toRet = it.renderLink(null, true, null, true);
        }
        return toRet;
    }

    public String getImageLinkForPriorPage()
    {
        String toRet = "16_prior_off.gif";
        if (getHistoryPointer() > 0)
        {
            toRet = "16_prior_on.gif";  
        }            
        return toRet;
    }

    public String getImageLinkForNextPage()
    {
        String toRet = "16_next_off.gif";
        if (getHistoryPointer() < (getHistory().size() - 1))
        {
            toRet = "16_next_on.gif";
        }
        return toRet;
    }

    public String getImageLinkForHomePage()
    {
        String toRet = "16_home_on.gif";
        PathItem it = ( PathItem ) getHistory().get(getHistoryPointer());
        if ((String.valueOf(it.getId())).equals("1"))
        {
            toRet = "16_home_off.gif";
        }

        return toRet;
    }
    
    public String getImageLinkForCancelEdit()
    {
        String toRet = "16_cancel_off.gif";
        if (getHistoryPointer() > 0)
        {
            toRet = "16_cancel_on.gif";
        }
        return toRet;
    } 
    public void renderLinkToCancelBeforeNextPage(PathItem item,StringBuffer toRet, boObject object, String url)
    {        
        toRet.append(
            "winmain().cancelAndNextPage( getIDX() ,'" + object.getName().toLowerCase() + "','edit','" +
             url + "');");
    }
    public void renderLinkToSaveBeforeNextPage(PathItem item,StringBuffer toRet, boObject object, String url)
    {
        toRet.append(
            "winmain().saveAndNextPage( getIDX() ,'" + object.getName().toLowerCase() + "','edit','" +
            url + "');");
        
    }
    public void renderLinkToNextPage(PathItem item,StringBuffer toRet, boObject object, String url)
    {
        toRet.append(
            "winmain().nextPage( getIDX() ,'" + object.getName().toLowerCase() + "','edit','" + url +
            "');");        
    }   
    
    private String getNextPage(boObject object, PathItem pathItem,HttpServletRequest request) throws boRuntimeException
    {
        String result = null;
        if ( request.getParameter( "nextPage" ) != null )
        {
            String newUrl = "";
            
            try
            {
                newUrl = java.net.URLDecoder.decode( request.getParameter("nextPage"), boConfig.getEncoding() );
            }
            catch( Exception ex )
            {
                System.out.println(ex);
            }
            
            if(newUrl.indexOf("?")==-1 ) 
            {
                 newUrl=newUrl+"?fromPathItem="+pathItem.getId();
                     
            }
            else
            { 
                newUrl = newUrl +"&fromPathItem="+pathItem.getId();
            }
            
            String[] parts=newUrl.split("\\?");            
            String[] parms=parts[1].split("\\&");

            String fromAttribute = null;
            String fromWhere = null;
            String toBoui=null;
            for (int i = 0; i < parms.length ; i++) 
            {
                if ( parms[i].startsWith( "addToCtxParentBridge" ) )
                {
                    fromAttribute = parms[i].split("=")[1];
                }
                else if ( parms[i].startsWith( "fromWhere" ) ) 
                {
                    fromWhere=parms[i].split("=")[1];
                }
                else if (  parms[i].startsWith( "boui" ) )
                {
                    toBoui=parms[i].split("=")[1];
                }
            }
            
             
            if ( fromWhere!= null && toBoui!=null && fromWhere.equals("fromNext") )
            {
              
              if ( !object.haveReferencesToObject_in_Memory( ClassUtils.convertToLong( toBoui ) ) )
              {
                   return null;
              }
              
              newUrl = newUrl.replaceAll("fromWhere=fromNext","" );
            }
            
            if( fromAttribute != null )
            {
                newUrl+="&fromAttribute="+fromAttribute;
            }            
                            
            result = newUrl;
//  fc: o código comentado servia pra efectuar o merge dos documentos numa msg sempre que o deliverymsg fosse alterado
//            if("XwfController".equals(object.getEboContext().getController().getName()))
//            {
//                if("deliveryMessage".equals(object.getName()) && object.isChanged())
//                {
//                    boObject message = boObject.getBoManager().loadObject(object.getEboContext(), Long.parseLong(toBoui));
//                    if("message".equals(message.getName()) || "message".equals(message.getBoDefinition().getBoSuperBo()))
//                    {
//                        MessageServer.mergeMessage((XwfController)object.getEboContext().getController(), message);
//                    }
//                }
//            }
        }          
        return result;
    }
    public void clear()
    {
        clearHistory();
        clearHistoryPointer();
        clearPathItemList();
        clearNumberOfItems();
        clearRootObjectBoui();
        clearFirstPathItem();
    }
    private void clearNumberOfItems()
    {
        this.p_itemsnumber = 0;
    }        
    private void clearPathItemList()
    {
        if(this.p_items != null)
        {    
            this.p_items.clear();
        }
    }    
    private void clearHistory()
    {
        if(this.p_pathHistory != null)
        {
            this.p_pathHistory.clear();   
        }        
    }
    private void clearHistoryPointer()
    {
        this.p_pathHistoryPointer = -1;
    }
    private void clearRootObjectBoui()
    {
        this.rootObjectBoui = -1;
    }
    private void clearFirstPathItem()
    {
        if(this.firstPathItem != null)
        {       
            this.firstPathItem = null;
        }
    }                
    
}