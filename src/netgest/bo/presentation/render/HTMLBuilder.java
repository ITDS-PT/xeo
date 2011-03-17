/*Enconding=UTF-8*/
package netgest.bo.presentation.render;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.SQLException;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.localizations.MessageLocalizer;
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
import netgest.bo.presentation.render.ie.IEBuilder;
import netgest.bo.runtime.boRuntimeException;

/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class HTMLBuilder {
    /**
     *
     * @Company Enlace3
     * @since
     */
    public HTMLBuilder() {
    }

    //----------------------
    public static void writeExplorer(PrintWriter out, Explorer e,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeExplorer(out, e, docHTML,docList, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }

    public static String getExplorerHTML(Explorer e, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeExplorer(pw, e, docHTML,docList, control);

        return caw.toString();
    }

    public static void writeFilter(PrintWriter out, Filter f,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeFilter(out, f, docHTML,docList, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }

    public static String getFilterHTML(Filter f, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeFilter(pw, f, docHTML,docList, control);

        return caw.toString();
    }

    public static void writeForm(PrintWriter out, Form f,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeForm(out, f, docHTML,docList, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }

    public static String getFormHTML(Form f, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeForm(pw, f, docHTML,docList, control);

        return caw.toString();
    }

    public static void writeGroup(PrintWriter out, Group g,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeGroup(out, g, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }

    public static String getGroupHTML(Group g, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeGroup(pw, g, docHTML,docList, control);

        return caw.toString();
    }

    public static void writeInput(PrintWriter out, Input i,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeInput(out, i, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }


    public static String getInputHTML(Input i, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeInput(pw, i, docHTML,docList, control);

        return caw.toString();
    }

    public static void writeMenu(PrintWriter out, Menu m,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeMenu(out, m, docHTML, docList, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }

    public static String getMenuHTML(Menu m, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeMenu(pw, m, docHTML,docList, control);

        return caw.toString();
    }
    
    public static void writeMenuAdHocItem(PrintWriter out, MenuAdHocItem m,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeMenuAdHocItem(out, m,control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }


    public static String getMenuAdHocItemHTML(MenuAdHocItem m, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeMenuAdHocItem(pw, m, docHTML,docList, control);

        return caw.toString();
    }
        
        

    public static void writePage(PrintWriter out, Page p,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writePage(out, p, docHTML,docList, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }


    public static String getPageHTML(Page p, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writePage(pw, p, docHTML,docList, control);

        return caw.toString();
    }

    public static void writeMenuItem(PrintWriter out, MenuItem mi,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:

            //MenuItem.ritePage(out, mi, docHTML,docList, control);
            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }


    public static String getMenuItemHTML(MenuItem m, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeMenuItem(pw, m, docHTML,docList, control);

        return caw.toString();
    }

    public static void writeParameters(PrintWriter out, Parameters p,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeParameters(out, p, docHTML,docList, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }


    public static String getParametersHTML(Parameters p, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeParameters(pw, p, docHTML,docList, control);

        return caw.toString();
    }

    public static void writePreview(PrintWriter out, Preview p,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writePreview(out, p, docHTML,control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }


    public static String getPreviewHTML(Preview p, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter c = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(c);
        writePreview(pw, p, docHTML,docList, control);

        return c.toString();
    }

    public static void writeSplitter(PrintWriter out, Splitter s,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeSplitter(out, s, docHTML,docList, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }


    public static String getSplitterHTML(Splitter s, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeSplitter(pw, s, docHTML,docList, control);

        return caw.toString();
    }

    public static void writeTree(PrintWriter out, Tree t,
        docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException, SQLException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeTree(out, t, docHTML,docList, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }


    public static String getTreeHTML(Tree t, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException, SQLException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeTree(pw, t, docHTML,docList, control);

        return caw.toString();
    }
    
    public static void writeObjectCardReport(PrintWriter out, ObjectCardReport oc,
        docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException{
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeObjectCardReport(out, oc, docHTML,control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }


    public static String getObjectCardReportHTML(ObjectCardReport oc, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeObjectCardReport(pw, oc, docHTML, docList, control);

        return caw.toString();
    }
    
    public static void writeAdHocElement(PrintWriter out, AdHocElement m,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeAdHocElement(out, m, docHTML, docList, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }


    public static String getAdHocElementHTML(AdHocElement m, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeAdHocElement(pw, m, docHTML,docList, control);

        return caw.toString();
    }
    
    public static void writeMessageViewer(PrintWriter out, MessageViewer mv,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeMessageViewer(out, mv, docHTML, docList, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }


    public static String getMessageViewerHTML(MessageViewer mv, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeMessageViewer(pw, mv, docHTML,docList, control);

        return caw.toString();
    }
    
    public static void writeSavedExplorer(PrintWriter out, SavedExplorers f,
        docHTML docHTML, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        switch (control.getBrowserCode()) {
        case Browser.IE:
            IEBuilder.writeSavedExplorer(out, f, docHTML,docList, control);

            break;

        //            case MOZILLA: 
        //                    break;
        default:
            throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"),
                null);
        }
    }

    public static String getSavedExplorerHTML(SavedExplorers f, docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        writeSavedExplorer(pw, f, docHTML,docList, control);

        return caw.toString();
    }
}
