/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;

import java.io.PrintWriter;
import netgest.bo.presentation.render.HTMLBuilder;
import netgest.bo.presentation.render.PageController;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.report.*;

import netgest.bo.runtime.*;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.jsp.JspWriter;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class Preview implements Element {
    private String code;
    private boolean display = true;
    private boObject o;
    private long boui;
    private String viewerName;
    private String formName;
    private String previewId;
    private int idx = -1;
    private String parameters;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public Preview(String code) {
        this.code = code;
    }

    public Preview(boObject objToPreview, String viewerName, String formName) {
        this.o = objToPreview;
        if(objToPreview != null)
        {
            this.boui = objToPreview.getBoui();
        }
        this.viewerName = viewerName;
        this.formName = formName;
    }     

    public void setDisplay(boolean value) {
        this.display = value;
    }

    public void setPreviewID(String prvId) {
        this.previewId = prvId;
    }
    public String getPreviewID() {
        return this.previewId;
    }
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    public String getParameters() {
        return this.parameters;
    }
    
    public boolean getDisplay() {
        return this.display;
    }

    public boObject getObject() {
        return this.o;
    }
    public long getBoui() {
        return this.boui;
    }

    public void setObject(boObject objToPreview) {
        this.o = objToPreview;
        this.boui = objToPreview.getBoui();
    }
    public void setObject(long boui) {
        this.o = null;
        this.boui = boui;
    }

    public String getCode() {
        return this.code;
    }

    public String getFormName() {
        return this.formName;
    }

    public String getViewerName() {
        return this.viewerName;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList,  PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writePreview(out, this,docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getPreviewHTML(this,docHTML, docList, control);
    }
}
