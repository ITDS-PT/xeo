/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.HTMLCommon;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Element;
import netgest.bo.presentation.render.elements.Page;
import netgest.bo.presentation.render.ie.scripts.FunctionBuilder;
import netgest.bo.presentation.render.ie.scripts.ImportBuilder;
import netgest.bo.presentation.render.ie.scripts.ScriptBuilder;
import netgest.bo.runtime.boRuntimeException;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class PageBuilder {
    //os arrays crescem 5
    private static final int GROW = 5;
    private static final char[] PAGE_META = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />".toCharArray();
    private static final char[] PAGE_RF_FR = "<iframe style=\"display:none\" id = refreshframe src=\"__refresh.jsp?docid=".toCharArray();
    private static final char[] PAGE_RF_FR_1 = "&BOUI=".toCharArray();
    private static final char[] PAGE_RF_FR_2 = "\"></iframe>".toCharArray();
    private static final char[] PAGE_MN_FR = "<iframe style=\"display:none\" id = menuframe src=\"__menu.jsp?docid=".toCharArray();
    private static final char[] PAGE_MN_FR_1 = "\"></iframe>".toCharArray();
    private static final int[] PAGE_FUNTIONS = null;
    private static final int[] PAGE_SCRIPTS = { ScriptBuilder.SC_JS_XEO };
    private static final int[] PAGE_IMPORTS = { ImportBuilder.IMP_CSS_UI_GLOBAL };

    /**
     *
     * @Company Enlace3
     * @since
     */
    private PageBuilder(String title) {
    }

    public static void writePage(PrintWriter out, Page page, docHTML docHTML,
        docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        ArrayList elements = page.getElements();
        out.write(HTMLCommon.HTML_BEGIN);
        out.write(HTMLCommon.UTIL_NEW_LINE);
        out.write(HTMLCommon.HTML_HEAD_BEGIN);
        out.write(HTMLCommon.UTIL_NEW_LINE);
        out.write(PAGE_META);
        out.write(HTMLCommon.UTIL_NEW_LINE);

        if (page.getTitle() != null) {
            out.write(HTMLCommon.HTML_TITLE_BEGIN);
            out.write(page.getTitle().toCharArray());
            out.write(HTMLCommon.HTML_TITLE_END);
        }

        if (ImportBuilder.canWrite(PAGE_IMPORTS, control) ||
                ImportBuilder.canWrite(page.getImports(), control)) {
            out.write(HTMLCommon.HTML_STYLE_TEXT_CSS_BEGIN);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            ImportBuilder.writeImport(out, PAGE_IMPORTS, control);
            ImportBuilder.writeImport(out, page.getImports(), control);
            out.write(HTMLCommon.HTML_STYLE_END);
            out.write(HTMLCommon.UTIL_NEW_LINE);
        }

        out.write(HTMLCommon.HTML_HEAD_END);
        out.write(HTMLCommon.UTIL_NEW_LINE);

        ScriptBuilder.writeScript(out, PAGE_SCRIPTS, control);
        ScriptBuilder.writeScript(out, page.getScript(), control);
        out.write(HTMLCommon.UTIL_NEW_LINE);

        if (FunctionBuilder.canWrite(PAGE_FUNTIONS, control) ||
                FunctionBuilder.canWrite(page.getFunction(), control)) {
            out.write(HTMLCommon.HTML_SCRIPT_BEGIN);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            FunctionBuilder.writeFunction(out, PAGE_FUNTIONS, control);
            FunctionBuilder.writeFunction(out, page.getFunction(), control);
            out.write(HTMLCommon.HTML_SCRIPT_END);
            out.write(HTMLCommon.UTIL_NEW_LINE);
        }

        if (page.getFunctionSize() > 0) {
            ArrayList l = page.getPosFunctions();
            out.write(HTMLCommon.HTML_SCRIPT_BEGIN);
            out.write(HTMLCommon.UTIL_NEW_LINE);

            for (int i = 0; i < l.size(); i++) {
                out.write(((String) l.get(i)).toCharArray());
                out.write(HTMLCommon.UTIL_NEW_LINE);
            }

            out.write(HTMLCommon.HTML_SCRIPT_END);
            out.write(HTMLCommon.UTIL_NEW_LINE);
        }

        if (page.getBodyParameters() == null) {
            out.write(HTMLCommon.HTML_BODY_BEGIN);
        } else {
            out.write(HTMLCommon.HTML_BODY_BEGIN_NOT_CLOSE);
            out.write(page.getBodyParameters().toCharArray());
            out.write(HTMLCommon.SYMBOL_GT);
            out.write(HTMLCommon.UTIL_NEW_LINE);
        }
        if(page.hasRefreshFrame())
        {
            out.write(PAGE_RF_FR);
            out.write(page.getIDX().toCharArray());
            out.write(PAGE_RF_FR_1);
            out.write(page.getBoui().toCharArray());
            out.write(PAGE_RF_FR_2);
            out.write(HTMLCommon.UTIL_NEW_LINE);
        }
        if(page.hasMenuFrame())
        {
            out.write(PAGE_MN_FR);
            out.write(page.getIDX().toCharArray());
            out.write(PAGE_MN_FR_1);
        }

        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                ((Element) elements.get(i)).writeHTML(out, docHTML, docList, control);
            }
        }

        out.write(HTMLCommon.UTIL_NEW_LINE);
        out.write(HTMLCommon.HTML_BODY_END);
        out.write(HTMLCommon.UTIL_NEW_LINE);
        out.write(HTMLCommon.HTML_END);
    }
}
