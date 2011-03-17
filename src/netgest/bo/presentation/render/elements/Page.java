/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.presentation.render.HTMLBuilder;
import netgest.bo.presentation.render.PageController;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.Logger;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class Page implements Element {
    //os arrays crescem 5
    private static final int GROW = 5;
    private static Logger logger = Logger.getLogger(
            "netgest.bo.presentation.render.elements.Page");
    private ArrayList elements;
    private int[] scripts = null;
    private int ptScripts = 0;
    private int[] imports = null;
    private int ptImports = 0;
    private int[] functions = null;
    private int ptFunctions = 0;
    private String bodyParams = null;
    private String title = null;
    private String htmlError = null;
    private String toRender = null;
    private String idx = null;
    private long boui;
    public boolean refreshFrame = false;
    public boolean menuFrame = false;
    private ArrayList a_functions = new ArrayList();

    /**
     *
     * @Company Enlace3
     * @since
     */
    public Page(String title, ArrayList elements) {
        this.elements = elements;
        this.title = title;
    }

    public Page() {
        this.elements = new ArrayList();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void addElement(Element e) {
        if (elements == null) {
            elements = new ArrayList();
        }

        this.elements.add(e);
    }

    public void addFunction(String f) {
        a_functions.add(f);
    }

    public void addImport(int importCode) {
        if (imports == null) {
            imports = new int[5];
        }

        imports[ptImports] = importCode;
        ptImports++;
    }

    public void addScript(int scriptCode) {
        if (scripts == null) {
            scripts = new int[5];
        }

        scripts[ptScripts] = scriptCode;
        ptScripts++;
    }

    public void addFunction(int functionCode) {
        if (functions == null) {
            functions = new int[5];
        }

        if (functions.length == ptFunctions) {
            functions = grow(functions, ptFunctions);
            ptFunctions++;
        } else {
            functions[ptFunctions] = functionCode;
            ptFunctions++;
        }
    }

    public void setBodyParameters(String value) {
        this.bodyParams = value;
    }

    public void setHtmlError(String value) {
        this.htmlError = value;
    }

    public void setRefreshFrame(String idx, long boui) {
        this.idx = idx;
        this.boui = boui;
        refreshFrame = true;
    }
    
    public void setMenuFrame(String idx) {
        this.idx = idx;
        menuFrame = true;
    }    

    public void setToRender(String toRender) {
        this.toRender = toRender;
    }

    public int[] getImports() {
        return shrink(imports, ptImports);
    }

    public int[] getScript() {
        return shrink(scripts, ptScripts);
    }

    public int[] getFunction() {
        return shrink(functions, ptFunctions);
    }

    public String getBodyParameters() {
        return this.bodyParams;
    }

    public ArrayList getElements() {
        return elements;
    }

    public String getHtmlError() {
        return this.htmlError;
    }

    public String toRender() {
        return this.toRender;
    }

    public int getFunctionSize() {
        return a_functions.size();
    }

    public ArrayList getPosFunctions() {
        return a_functions;
    }
    
    public String getIDX()
    {
        return idx;
    }
    
    public String getBoui()
    {
        return String.valueOf(boui);
    }
    public boolean hasRefreshFrame() {
        return refreshFrame;
    }
    public boolean hasMenuFrame() {
        return menuFrame;
    }
    private static int[] grow(int[] from, int newValue) {
        int[] to;
        int length = (from == null) ? 0 : from.length;
        to = new int[length + GROW];

        if (length > 0) {
            System.arraycopy(from, 0, to, 1, length);
        }

        to[0] = newValue;

        return to;
    }

    private static int[] shrink(int[] from, int pos) {
        if ((from == null) || (from.length == 0) || (from.length == 1)) {
            return from;
        }

        int length = from.length;
        int[] to = new int[pos];
        System.arraycopy(from, 0, to, 0, pos);

        return to;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList,  PageController control)
        throws IOException, boRuntimeException {
        long ti = System.currentTimeMillis();
        HTMLBuilder.writePage(out, this,docHTML, docList, control);
        long tf = System.currentTimeMillis() - ti;
        String t = ((float) (Math.round((float) (tf) / 100f)) / 10f) + "s";
        logger.finer(LoggerMessageLocalizer.getMessage("PAGE_BUILDING_TIME")+": " + t);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getPageHTML(this,docHTML, docList, control);
    }
}
