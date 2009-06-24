/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;
import java.io.PrintWriter;

import netgest.bo.dochtml.docHTML;
import netgest.bo.presentation.render.HTMLCommon;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Preview;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class PreviewBuilder {
    private static final char[] PRV_PRESENTATION_IFRAME = "<iframe name=\"".toCharArray();
    private static final char[] PRV_PRESENTATION_IFRAME_1 ="\" NORESIZE SCROLLING=\"AUTO\" height=\"100%\" width=\"100%\" BORDER=0 TARGET=\"_SELF\" FRAMEBORDER=\"NO\" src=\"__buildPreview.jsp?bouiToPreview=".toCharArray();
    private static final char[] PRV_DOCID_PARAMETER ="&docid=".toCharArray();
    private static final char[] PRV_ID =" id=\"".toCharArray();
    /**
     *
     * @Company Enlace3
     * @since
     */
    public PreviewBuilder() {
    }

    public static void writePreview(PrintWriter out, Preview preview,
        docHTML doc, PageController control) throws IOException, boRuntimeException {
        if(preview.getObject() == null && preview.getBoui() > 0)
        {
            preview.setObject(boObject.getBoManager().loadObject(doc.getEboContext(),preview.getBoui()));
        }
        if (preview.getCode() != null) {
            out.write(preview.getCode().toCharArray());
        } else
        {
            out.write(HTMLCommon.HTML_DIV_BEGIN);
            out.write(PRV_PRESENTATION_IFRAME);
            out.write(preview.getViewerName().toCharArray());
            out.write(PRV_PRESENTATION_IFRAME_1);
            if(preview.getObject() != null)
            {
                out.write(String.valueOf(preview.getBoui()).toCharArray());
            }
            else
            {
                out.write(String.valueOf(-1).toCharArray());
            }
            out.write(PRV_DOCID_PARAMETER);
            out.write(String.valueOf(doc.getDocIdx()));
            if(preview.getParameters() != null && !"".equals(preview.getParameters()))
            {
                if(!preview.getParameters().startsWith("&"))
                {
                    out.write(HTMLCommon.SYMBOL_AND);
                }
                out.write(preview.getParameters().toCharArray());
            }
            out.write(HTMLCommon.SYMBOL_QUOTE);
            if(preview.getPreviewID() != null && !"".equals(preview.getPreviewID()))
            {
                out.write(PRV_ID);
                out.write(preview.getPreviewID().toCharArray());
                out.write(HTMLCommon.SYMBOL_QUOTE);
            }
            out.write(HTMLCommon.SYMBOL_GT);
            out.write(HTMLCommon.HTML_IFRAME_END);
            out.write(HTMLCommon.HTML_DIV_END);            
        }
    }
}
