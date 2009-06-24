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
public class AdHocElement implements Element
{
    private String code; 
//    private Menu subMenu = null;
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public AdHocElement(String code)
    {
        this.code = code;
    }
    
    public String getCode()
    {
        return this.code;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList,  PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writeAdHocElement(out, this,docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getAdHocElementHTML(this, docHTML, docList, control);
    }
    public void writeHTML(PrintWriter out,PageController control)
        throws IOException, boRuntimeException {
        writeHTML(out, null, null, control);
    }

    public String getHTML(PageController control)
        throws IOException, boRuntimeException {
        return getHTML(null, null, control);
    }
}