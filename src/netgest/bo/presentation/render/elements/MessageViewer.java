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
public class MessageViewer implements Element {
    private String name;
    private long programboui;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public MessageViewer(String name, long programboui) {
        this.name = name;
        this.programboui = programboui;
    }

    public String getName() {
        return this.name;
    }

    public long getProgramBoui() {
        return this.programboui;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writeMessageViewer(out, this, docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList,PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getMessageViewerHTML(this, docHTML, docList, control);
    }
}
