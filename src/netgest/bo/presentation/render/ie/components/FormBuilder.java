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
import netgest.bo.presentation.render.elements.Form;
import netgest.bo.runtime.boRuntimeException;

/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class FormBuilder {
    private static final char[] FORM = "<form".toCharArray();
    private static final char[] CLASS = " class='".toCharArray();
    private static final char[] NAME = " name='".toCharArray();
    private static final char[] ID = " id='".toCharArray();
    private static final char[] METHOD = " method='".toCharArray();
    private static final char[] ACTION = " action='".toCharArray();
    private static final char[] GRID_KEY_1 = "<script> window.gridKey='".toCharArray();
    private static final char[] GRID_KEY_2 = "'</script>".toCharArray();
    private static final char[] FORM_END = "</form>".toCharArray();

    /**
     *
     * @Company Enlace3
     * @since
     */
    public FormBuilder() {
    }

    public static void writeForm(PrintWriter out, Form form,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        out.write(FORM);

        if ((form.getClassName() != null) && !"".equals(form.getClassName())) {
            out.write(CLASS);
            out.write(form.getClassName().toCharArray());
            out.write(HTMLCommon.SYMBOL_PLICA);
        }

        if ((form.getName() != null) && !"".equals(form.getName())) {
            out.write(NAME);
            out.write(form.getName().toCharArray());
            out.write(HTMLCommon.SYMBOL_PLICA);
        }

        if ((form.getAction() != null) && !"".equals(form.getAction())) {
            out.write(ACTION);
            out.write(form.getAction().toCharArray());
            out.write(HTMLCommon.SYMBOL_PLICA);
        }

        if ((form.getId() != null) && !"".equals(form.getId())) {
            out.write(ID);
            out.write(form.getId().toCharArray());
            out.write(HTMLCommon.SYMBOL_PLICA);
        }

        if ((form.getMethod() != null) && !"".equals(form.getMethod())) {
            out.write(METHOD);
            out.write(form.getMethod().toCharArray());
            out.write(HTMLCommon.SYMBOL_PLICA);
        }

        out.write(HTMLCommon.SYMBOL_GT);
        out.write(HTMLCommon.UTIL_NEW_LINE);

        ArrayList e = form.getElements();

        for (int i = 0; i < e.size(); i++) {
            ((Element) e.get(i)).writeHTML(out,doc, docList, control);
        }

        if(form.getWindowGridKey() != null)
        {
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(GRID_KEY_1);
            out.write(form.getWindowGridKey().toCharArray());
            out.write(GRID_KEY_2);
            out.write(HTMLCommon.UTIL_NEW_LINE);
        }
        out.write(FORM_END);
    }
}
