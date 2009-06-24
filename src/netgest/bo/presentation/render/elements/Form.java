/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.HTMLBuilder;
import netgest.bo.presentation.render.PageController;
import netgest.bo.runtime.boRuntimeException;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class Form implements Element {
    private String name;
    private String action;
    private ArrayList inputs;
    private String className;
    private String id;
    private String method;
    private String windowKey;
    private ArrayList elements = new ArrayList();

    /**
     *
     * @Company Enlace3
     * @since
     */
    public Form(String name, String action, String className, String id,
        String method) {
        this.name = name;
        this.action = action;
        this.className = className;
        this.id = id;
        this.method = method;
    }

    public void setWindowGridKey(String key) {
        this.windowKey = key;
    }
    public String getWindowGridKey() {
        return windowKey;
    }

    public String getName() {
        return this.name;
    }

    public String getAction() {
        return this.action;
    }

    public String getClassName() {
        return this.className;
    }

    public String getId() {
        return this.id;
    }

    public String getMethod() {
        return this.method;
    }

    public void addElement(Element e) {
        elements.add(e);
    }

    public ArrayList getElements() {
        return elements;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writeForm(out, this, docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList,PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getFormHTML(this, docHTML, docList, control);
    }
}
