/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;
import java.io.PrintWriter;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.AdHocElement;
import netgest.bo.runtime.boRuntimeException;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class AdHocElementBuilder {

    /**
     *
     * @Company Enlace3
     * @since
     */
    private AdHocElementBuilder() {
    }

    public static void writeAdHocElement(PrintWriter out, AdHocElement element, docHTML docHTML,
        docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        out.write(element.getCode().toCharArray());
    }
}
