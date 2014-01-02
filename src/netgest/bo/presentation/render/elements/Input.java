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
public class Input implements Element {
    private String type;
    private String name;
    private String value;
    private String code;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public Input(String code) {
        this.code = code;
    }

    public Input(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList,  PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writeInput(out, this,docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getInputHTML(this,docHTML, docList, control);
    }

    public void writeHTML(PrintWriter out, PageController control)
        throws IOException, boRuntimeException {
        writeHTML(out, null, null, control);
    }

    public String getHTML(PageController control)
        throws IOException, boRuntimeException {
        return getHTML(null, null, control);
    }
}
