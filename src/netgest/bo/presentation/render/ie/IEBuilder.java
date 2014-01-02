/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.SQLException;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.AdHocElement;
import netgest.bo.presentation.render.elements.Explorer;
import netgest.bo.presentation.render.elements.Filter;
import netgest.bo.presentation.render.elements.Form;
import netgest.bo.presentation.render.elements.Group;
import netgest.bo.presentation.render.elements.Input;
import netgest.bo.presentation.render.elements.Menu;
import netgest.bo.presentation.render.elements.MenuAdHocItem;
import netgest.bo.presentation.render.elements.MenuItem;
import netgest.bo.presentation.render.elements.MessageViewer;
import netgest.bo.presentation.render.elements.ObjectCardReport;
import netgest.bo.presentation.render.elements.Page;
import netgest.bo.presentation.render.elements.Parameters;
import netgest.bo.presentation.render.elements.Preview;
import netgest.bo.presentation.render.elements.SavedExplorers;
import netgest.bo.presentation.render.elements.Splitter;
import netgest.bo.presentation.render.elements.Tree;
import netgest.bo.presentation.render.ie.components.AdHocElementBuilder;
import netgest.bo.presentation.render.ie.components.ExplorerBuilder;
import netgest.bo.presentation.render.ie.components.FilterBuilder;
import netgest.bo.presentation.render.ie.components.FormBuilder;
import netgest.bo.presentation.render.ie.components.GroupBuilder;
import netgest.bo.presentation.render.ie.components.InputBuilder;
import netgest.bo.presentation.render.ie.components.MenuBuilder;
import netgest.bo.presentation.render.ie.components.MessageViewerBuilder;
import netgest.bo.presentation.render.ie.components.ObjectCardReportBuilder;
import netgest.bo.presentation.render.ie.components.PageBuilder;
import netgest.bo.presentation.render.ie.components.ParametersBuilder;
import netgest.bo.presentation.render.ie.components.PreviewBuilder;
import netgest.bo.presentation.render.ie.components.SavedExplorersBuilder;
import netgest.bo.presentation.render.ie.components.SplitterBuilder;
import netgest.bo.presentation.render.ie.components.TreeBuilder;
import netgest.bo.runtime.boRuntimeException;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class IEBuilder {
    //Style HTML para o IE
    //    private static final char[] STYLE_THEMES_START = "<style type=\"text/css\">".toCharArray();
    //    private static final char[] STYLE_THEMES_END = "</style>".toCharArray();
    //    
    //    private static final char[] HTML_META = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />".toCharArray();
    //    private static final char[] HTML_TITLE = "<title>nbo</title>".toCharArray();
    //    //Menu js
    //    private static final char[] JS_MENU = ("<script LANGUAGE=\"javascript\" SRC=\"" + Browser.getThemeDir()+ "menu/ui-menu.js\"></script>").toCharArray();
    //    private static final char[] XEO_JS = "<script LANGUAGE=\"javascript\" SRC=\"xeo.js\"></script>".toCharArray();
    //    
    //    private static final char[] F_CALCULATOP = "function calculatop(x_ele){var x_ret=0;if (x_ele.tagName=='BODY') return 0;else  x_ret=x_ele.offsetTop-x_ele.scrollTop+calculatop(x_ele.parentElement);return x_ret}".toCharArray();
    //    private static final char[] F_CALCULALEFT = "function calculaleft(x_ele){var x_ret=0;if (x_ele.tagName=='BODY') return 0;else x_ret=x_ele.offsetLeft-x_ele.scrollLeft+calculaleft(x_ele.parentElement);return x_ret;}".toCharArray();

    /**
     *
     * @Company Enlace3
     * @since
     */
    public IEBuilder() {
    }

    public static void writeExplorer(PrintWriter out, Explorer e,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        ExplorerBuilder.writeExplorer(out, e, doc, docList, control);
    }

    public static void writeFilter(PrintWriter out, Filter f,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        FilterBuilder.writeFilter(out, f, doc, docList,control);
    }

    public static void writeForm(PrintWriter out, Form f,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        FormBuilder.writeForm(out, f,doc, docList, control);
    }

    public static void writeGroup(PrintWriter out, Group g,
        PageController control) throws IOException, boRuntimeException {
        GroupBuilder.writeGroup(out, g, control);
    }

    public static void writeInput(PrintWriter out, Input i,
        PageController control) throws IOException, boRuntimeException {
        InputBuilder.writeInput(out, i, control);
    }

    public static void writeMenu(PrintWriter out, Menu m,
        docHTML doc, docHTML_controler docList, 
        PageController control) throws IOException, boRuntimeException {
        MenuBuilder.writeMenu(out, m, doc, docList, control);
    }

    public static void writePage(PrintWriter out, Page p,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        PageBuilder.writePage(out, p, doc, docList, control);
    }

    public static void writeMenuItem(PrintWriter out, MenuItem mi,
        PageController control) throws IOException, boRuntimeException {
        //MenuItem.ritePage(out, mi, control);
    }
    
    public static void writeMenuAdHocItem(PrintWriter out, MenuAdHocItem mi,
        PageController control) throws IOException, boRuntimeException {
        //MenuItem.ritePage(out, mi, control);
    }
    public static void writeAdHocElement(PrintWriter out, AdHocElement adElement,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        AdHocElementBuilder.writeAdHocElement(out, adElement, doc, docList, control);
    }

    public static void writeParameters(PrintWriter out, Parameters p,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        ParametersBuilder.writeParameters(out, p, doc, docList, control);
    }

    public static void writePreview(PrintWriter out, Preview p,
        docHTML doc, PageController control) throws IOException, boRuntimeException {
        PreviewBuilder.writePreview(out, p, doc, control);
    }

    public static void writeSplitter(PrintWriter out, Splitter s,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        SplitterBuilder.writeSplitter(out, s, doc, docList, control);
    }

    public static void writeTree(PrintWriter out, Tree t,
        docHTML doc, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException, SQLException {
        TreeBuilder.writeTree(out, t, doc, docList, control);
    }
    
    public static void writeObjectCardReport(PrintWriter out,
         ObjectCardReport oc, docHTML doc,PageController control)
        throws IOException, boRuntimeException {
        ObjectCardReportBuilder.writeCardReport(out, oc, doc, control);
    }
    public static void writeMessageViewer(PrintWriter out,
         MessageViewer mv, docHTML doc, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        MessageViewerBuilder.writeMessageViewer(out, mv, doc, docList, control);
    }
    
    public static void writeSavedExplorer(PrintWriter out, SavedExplorers f,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        SavedExplorersBuilder.writeSavedExplorers(out, f, doc, docList,control);
    }
}
