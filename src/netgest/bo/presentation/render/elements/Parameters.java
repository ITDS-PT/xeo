/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;

import java.io.IOException;
import java.io.PrintWriter;

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
public class Parameters implements Element {
    private String code;
    private boolean display = true;
    private Explorer tree;
    private String pageName;
    private String value = null;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public Parameters(String code) {
        this.code = code;
    }

    public Parameters(Explorer tree, String pageName) {
        this.tree = tree;
        this.pageName = pageName;
    }

    public Explorer getTreeRuntime() {
        return this.tree;
    }

    public String getPageName() {
        return this.pageName;
    }

    public String getCode() {
        return this.code;
    }

    public void setDisplay(boolean value) {
        this.display = value;
    }

    public boolean getDisplay() {
        return this.display;
    }

    public String getValue()
    {
        return this.value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList,  PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writeParameters(out, this,docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getParametersHTML(this,docHTML, docList, control);
    }
    
}
