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
public class Filter implements Element {
    private String code;
    private boolean display = true;
    private Explorer tree;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public Filter(String code) {
        this.code = code;
    }

    public Filter(Explorer tree) {
        this.tree = tree;
    }

    public String getCode() {
        return this.code;
    }

    public Explorer getExplorer() {
        return this.tree;
    }    

    public void setDisplay(boolean value) {
        this.display = value;
    }

    public boolean getDisplay() {
        return this.display;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writeFilter(out, this,docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList,PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getFilterHTML(this,docHTML, docList, control);
    }
}
