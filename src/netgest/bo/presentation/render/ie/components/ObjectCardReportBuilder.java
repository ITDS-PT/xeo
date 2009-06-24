/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;
import java.io.IOException;
import java.io.PrintWriter;

import netgest.bo.dochtml.docHTML;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.ObjectCardReport;
import netgest.bo.runtime.boObject;

import org.apache.log4j.Logger;
//import org.w3c.tidy.*;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class ObjectCardReportBuilder 
{
    private static Logger logger = Logger.getLogger("netgest.bo.presentation.render.ie.components.ObjectCardReportBuilder");
    private static final char[] REPORT_EMPTY = "<html></html>".toCharArray();
    private static final String CARD_NAO = "Não";
    private static final String CARD_SIM = "Sim";
    private static final String CARD_Y = "Y";
    
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private ObjectCardReportBuilder()
    {
    }

    public static final void writeCardReport(PrintWriter out, ObjectCardReport oc, docHTML doc, PageController control) throws IOException
    {
        netgest.bo.presentation.render.html32.components.ObjectCardReportBuilder.writeCardReport(out, oc, doc, control);
    }

    public static final void writeHTML(PrintWriter out, boObject o, String viewerName, 
        String formName, docHTML doc, PageController control) throws IOException
    {
        netgest.bo.presentation.render.html32.components.ObjectCardReportBuilder.writeHTML(out, o, viewerName, formName, doc, control);
    }
}