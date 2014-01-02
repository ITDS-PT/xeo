/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;
import java.io.IOException;
import java.io.PrintWriter;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.HTMLBuilder;
import netgest.bo.presentation.render.PageController;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class ObjectCardReport implements Element  
{
    private long p_boui =-1;
    private boObject p_obj = null;
    private String p_viewerName= null;
    private String p_formName= null;
    private String p_parentframeId= null;
    private boolean p_designPrint= true;
    private boolean p_designHeader= true;
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public ObjectCardReport(boObject o, String viewerName, String formName)
    {
        if(o != null)
        {
            p_boui = o.getBoui();
            p_obj = o;
        }
        p_viewerName = viewerName;
        p_formName = formName;
    }
    
    public ObjectCardReport(long boui, String viewerName, String formName)
    {
       p_boui = boui;
       p_viewerName = viewerName;
       p_formName = formName;
    }
    
    public ObjectCardReport(boObject o)
    {
       if(o != null)
        {
            p_boui = o.getBoui();
            p_obj = o;
        }
       p_viewerName = null;
       p_formName = null;
    }
    
    public ObjectCardReport(long boui)
    {
       p_boui = boui;
       p_viewerName = null;
       p_formName = null;
    }
    
    public void setObject(long boui)
    {
        if(this.p_boui != boui)
        {
            p_boui = boui;
            p_obj = null;
        }
    }
    public void setObject(boObject o)
    {
        if(o != null)
        {
            if(this.p_boui != o.getBoui())
            {
                p_boui = o.getBoui();
                p_obj = o;
            }
        }
        else
        {
            p_obj = null;
            p_boui = -1;
        }
    }
    public void designPrint(boolean v)
    {
        this.p_designPrint = v;
    }
    
    public void designHeader(boolean v)
    {
        this.p_designHeader = v;
    }
    
    public boolean designPrint()
    {
        return this.p_designPrint;
    }
    
    public boolean designHeader()
    {
        return this.p_designHeader;
    }
    public void setParentFrameId(String f)
    {
        this.p_parentframeId = f;
    }
    public String getParentFrameId()
    {
        return this.p_parentframeId;
    }
    public long getBoui()
    {
        return p_boui;
    }
    public boObject getObject()
    {
        return p_obj;
    }
    public String getViewerName()
    {
        return p_viewerName;
    }
    public String getFormName()
    {
        return p_formName;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writeObjectCardReport(out, this,docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList,PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getObjectCardReportHTML(this,docHTML, docList, control);
    }
}