/*Enconding=UTF-8*/
package netgest.bo.controller.common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.io.IOException;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.controller.Controller;
import netgest.bo.controller.Navigator;
import netgest.bo.controller.Presentation;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.utils.ClassUtils;

public final class PathItem
{
    private Hashtable p_parameters;
    private long p_relatedBoui;
    private String p_url;
    private ArrayList p_parents;
    private ArrayList p_childs;
    private ArrayList p_childsContextualAttributeName;
    private ArrayList p_parentsContextualAttributeName;
    private int p_itemid;
    private docHTML p_doc;
    private long realObjectBoui = -1;
    private StringBuffer label = null;

    public PathItem(docHTML doc, int itemid, String url, Hashtable parameters, long relatedBoui)
    {
        p_parameters = parameters;
        p_url = url;
        p_doc = doc;
        p_relatedBoui = relatedBoui;
        p_itemid = itemid;
    }

    public PathItem(docHTML doc, int itemid, long relatedBoui)
    {
        p_relatedBoui = relatedBoui;
        p_itemid = itemid;
        p_doc = doc;
    }

    public int getId()
    {
        return p_itemid;
    }
    
    public long getRelatedBoui()
    {
        return p_relatedBoui;
    }
    public long getRealObjectBoui()
    {
        return realObjectBoui;
    }
    public void setRealObjectBoui(long realObjectBoui)
    {
        this.realObjectBoui = realObjectBoui;
        setLabel();
    }
    public StringBuffer getLabel() throws boRuntimeException
    {            
        if(this.label == null)
        {
            boObject o = p_doc.getObject(p_relatedBoui);
            return o.getCARDIDwNoIMG();
        }
        else
        {
            return this.label;
        }        
    }
    public void setLabel()
    {
        try 
        {
            setLabel(this.p_doc.getObject(realObjectBoui).getCARDIDwNoIMG());  
        }
        catch (Exception ex) 
        {
            setLabel(null);
        }
        
    }
    private void setLabel(StringBuffer label)
    {
        this.label = label;        
    }    

    public StringBuffer renderLink(PathItem lastItem) throws boRuntimeException
    {
        return this.renderLink(lastItem, false, null, false);
    }

    public StringBuffer renderLink(PathItem lastItem, boolean onlyClick, String fromWhere, boolean toCancel)throws boRuntimeException
    {
        //*****d
        //<a class='linkScreen' href="www.iol.pt" > <%=BOI.getCARDID()%> </a> >
        int childNr = -1;
        if (p_childs != null)
        {
            childNr = this.p_childs.indexOf(lastItem);
        }

        String attributeName = "";
        if (childNr > -1)
        {
            attributeName = ( String ) this.p_childsContextualAttributeName.get(childNr);
        }

        HttpServletRequest request = p_doc.getEboContext().getRequest();

        //analise para saber se deve fazer o save antes de mudar de página
        boolean saveBeforeNextPage = (request.getParameter("ctxParentIdx") != null) &&
            (request.getParameter("ctxParent") != null) &&
            (request.getParameter("addToCtxParentBridge") != null);

        StringBuffer toRet = new StringBuffer();
        if (!onlyClick)
        {
            toRet.append("<span class='linkScreen' style='padding:0px' onclick=\"");
        }

        if (p_parameters != null)
        {
            Enumeration par = p_parameters.keys();
            String url = "";

            while (par.hasMoreElements())
            {
                Object param = par.nextElement();
                Object value = p_parameters.get(param);
                boolean toCont = true;
                if( param.toString().equalsIgnoreCase("toExecute"))
                {
                    toCont=false;
                }
                else if (param.toString().equalsIgnoreCase("method") )
                {
                    value="edit";
                }
                
                
                
                if ( toCont )
                {
                    if (url.length() == 0)
                    {
                        url = param + "=" + value;
                    }
                    else
                    {
                        url += ("&" + param + "=" + value);
                    }
                }
                
            }

            url += ("&pathItem=" + getId());

            if ((fromWhere != null) && (fromWhere.length() > 0))
            {
                url += ("&fromWhere=" + fromWhere);
            }

            boObject o = p_doc.getObject(p_relatedBoui);
            if ( toCancel )
            {
                p_doc.getController().getNavigator().renderLinkToCancelBeforeNextPage(this,toRet,o,url);
//                toRet.append(
//                    "winmain().cancelAndNextPage( getIDX() ,'" + o.getName().toLowerCase() + "','edit','" +
//                    url + "');");
            }
            else if (saveBeforeNextPage )
            {
                p_doc.getController().getNavigator().renderLinkToSaveBeforeNextPage(this,toRet,o,url);
//                toRet.append(
//                    "winmain().saveAndNextPage( getIDX() ,'" + o.getName().toLowerCase() + "','edit','" +
//                    url + "');");
            }
            else
            {
                p_doc.getController().getNavigator().renderLinkToNextPage(this,toRet,o,url);
//                toRet.append(
//                    "winmain().nextPage( getIDX() ,'" + o.getName().toLowerCase() + "','edit','" + url +
//                    "');");                        
            }

            if (!onlyClick)
            {
                toRet.append("\">");
                if(label == null)
                {
                    toRet.append(o.getCARDIDwNoIMG());
                }
                else
                {
                    toRet.append(label);
                }
                if (o.getBoDefinition().hasAttribute(attributeName))
                {
                    toRet.append("(");
                    toRet.append(o.getBoDefinition().getAttributeRef(attributeName).getLabel());
                    toRet.append(")");
                }
            }
        }
        else
        {
            boObject o = p_doc.getObject(p_relatedBoui);

            String url = "method=edit&boui=" + o.getBoui() + "&docid=" + p_doc.getDocIdx() +
                "&pathItem=" + getId();
            p_doc.getController().getNavigator().renderLinkToNextPage(this,toRet,o,url);
//            toRet.append(
//                "winmain().nextPage( getIDX() ,'" + o.getName().toLowerCase() + "','edit','" + url +
//                "');");                                        


            if (!onlyClick)
            {
                toRet.append("\">");
//                if(getLabel() == null)
//                {
//                    toRet.append(o.getCARDIDwNoIMG());
//                }
//                else
//                {
                    toRet.append(getLabel());
//                }

                if (o.getBoDefinition().hasAttribute(attributeName))
                {
                    toRet.append("(");
                    toRet.append(o.getBoDefinition().getAttributeRef(attributeName).getLabel());
                    toRet.append(")");

                    //toRet.append(" ");
                }
            }
        }

        if (!onlyClick)
        {
            toRet.append("</span>");
        }

        return toRet;
    }

    public void addParentPathItem(PathItem parent, String AttributeName)
    {
        if (p_parents == null)
        {
            p_parents = new ArrayList();
            p_parentsContextualAttributeName = new ArrayList();
        }

        int nrparent = p_parents.indexOf(parent);
        if (nrparent == -1)
        {
            p_parents.add(parent);
            p_parentsContextualAttributeName.add(AttributeName);
        }
        else
        {
            p_parentsContextualAttributeName.set(nrparent, AttributeName);
        }
    }

    public void addChildPathItem(PathItem child, String AttributeName)
    {
        if (p_childs == null)
        {
            p_childs = new ArrayList();
            p_childsContextualAttributeName = new ArrayList();
        }

        int nrchild = p_childs.indexOf(child);
        if (nrchild == -1)
        {
            p_childs.add(child);
            p_childsContextualAttributeName.add(AttributeName);
        }
        else
        {
            p_childsContextualAttributeName.set(nrchild, AttributeName);
        }
    }

    public ArrayList getParents()
    {
        return p_parents;
    }
    public String getUrl()
    {
        return p_url;
    }
    public long getBoui()
    {
        return p_relatedBoui;
    }
}