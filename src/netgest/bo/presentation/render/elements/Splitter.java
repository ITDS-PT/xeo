/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;

import java.io.IOException;
import java.io.PrintWriter;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.Browser;
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
public class Splitter implements Element {
    private boolean hSplit = true;
    private String color = null;
    private String upLeftName = null;
    private String downRightName = null;
    private String upLeftBackColor = "#ffffff";
    private String downRightBackColor = "#ffffff";
    private String upLeftBackSize = "50%";
    private String downRightBackSize = "50%";
    private String splitImgUrl = Browser.getThemeDir() +
        "splitter/resize-dot.gif";
    private Splitter split = null;
    private boolean upLeftDisplay = true;
    private boolean downRightDisplay = true;

    //Falta o conteudo
    private Element upLeftContext = null;
    private Element downRightContext = null;

    /**
     *
     * @Company Enlace3
     * @since
     */
    private Splitter() {
    }

    public static Splitter splitHorizontal(String upName, String downName,
        String splitImgUrl, String upBackColor, String upBackHeight,
        String downBackColor, String downBackHeight) {
        Splitter toRet = new Splitter();
        toRet.hSplit = true;
        toRet.upLeftName = upName;
        toRet.downRightName = downName;

        if (upBackColor != null) {
            toRet.upLeftBackColor = upBackColor;
        }

        if (downBackColor != null) {
            toRet.downRightBackColor = downBackColor;
        }

        if (upBackHeight != null) {
            toRet.upLeftBackSize = upBackHeight;
        }

        if (downBackHeight != null) {
            toRet.downRightBackSize = downBackHeight;
        }

        if (splitImgUrl != null) {
            toRet.splitImgUrl = splitImgUrl;
        }

        return toRet;
    }

    public static Splitter splitVertical(String leftName, String rightName,
        String splitImgUrl, String upBackColor, String upBackHeight,
        String downBackColor, String downBackHeight) {
        Splitter toRet = new Splitter();
        toRet.hSplit = false;
        toRet.upLeftName = leftName;
        toRet.downRightName = rightName;

        if (upBackColor != null) {
            toRet.upLeftBackColor = upBackColor;
        }

        if (downBackColor != null) {
            toRet.downRightBackColor = downBackColor;
        }

        if (upBackHeight != null) {
            toRet.upLeftBackSize = upBackHeight;
        }

        if (downBackHeight != null) {
            toRet.downRightBackSize = downBackHeight;
        }

        if (splitImgUrl != null) {
            toRet.splitImgUrl = splitImgUrl;
        }

        return toRet;
    }

    public static Splitter getExplorerSplitter(boolean isPreviewOn,
        boolean previewOnright) {
        Splitter h = Splitter.splitHorizontal(null, "showBottom", null,
                null, "50%", null, null);
        Splitter v = Splitter.splitVertical("tree", "showRight", null, null,
                "50%", null, null);

        if (!isPreviewOn) {
            h.upLeftDisplay = true;
            h.downRightDisplay = false;
            v.upLeftDisplay = true;
            v.downRightDisplay = false;
        } else if (previewOnright) {
            h.upLeftDisplay = true;
            h.downRightDisplay = false;
            v.upLeftDisplay = true;
            v.downRightDisplay = true;
        } else {
            h.upLeftDisplay = true;
            h.downRightDisplay = true;
            v.upLeftDisplay = true;
            v.downRightDisplay = false;
        }

        h.addUpSplitElement(v);

        return h;
    }

    public static Splitter getExplorerSplitter(Element tree, Element previewRight,
        Element previewDown,
        boolean isPreviewOn, boolean previewOnright) {
        Splitter h = Splitter.splitHorizontal(null, "previewBottom", null,
                null, "50%", null, null);
        Splitter v = Splitter.splitVertical("tree", "previewRight", null, null,
                "50%", null, null);

        if (!isPreviewOn) {
            h.upLeftDisplay = true;
            h.downRightDisplay = false;
            v.upLeftDisplay = true;
            v.downRightDisplay = false;
        } else if (previewOnright) {
            h.upLeftDisplay = true;
            h.downRightDisplay = false;
            v.upLeftDisplay = true;
            v.downRightDisplay = true;
        } else {
            h.upLeftDisplay = true;
            h.downRightDisplay = true;
            v.upLeftDisplay = true;
            v.downRightDisplay = false;
        }

        h.addDownSplitElement(previewDown);
        v.addLeftSplitElement(tree);
        v.addRightSplitElement(previewRight);
        h.addUpSplitElement(v);

        return h;
    }

    public void setSplitImageUrl(String img) {
        this.splitImgUrl = img;
    }

    public void setUpBackColor(String color) {
        this.upLeftBackColor = color;
    }

    public void setDownBackColor(String color) {
        this.upLeftBackColor = color;
    }

    public void setLeftBackColor(String color) {
        this.downRightBackColor = color;
    }

    public void setRightBackColor(String color) {
        this.downRightBackColor = color;
    }

    public void setUpBackSize(String Size) {
        this.upLeftBackSize = Size;
    }

    public void setdownBackSize(String Size) {
        this.upLeftBackSize = Size;
    }

    public void setLeftBackSize(String Size) {
        this.downRightBackSize = Size;
    }

    public void setRightBackSize(String Size) {
        this.downRightBackSize = Size;
    }

    public void addUpSplitElement(Element e) {
        this.upLeftContext = e;
    }

    public void addLeftSplitElement(Element e) {
        this.upLeftContext = e;
    }

    public void addDownSplitElement(Element e) {
        this.downRightContext = e;
    }

    public void addRightSplitElement(Element e) {
        this.downRightContext = e;
    }

    public String getSplitImageUrl() {
        return this.splitImgUrl;
    }

    public String getUpLeftName() {
        return this.upLeftName;
    }

    public String getDownRightName() {
        return this.downRightName;
    }

    public String getUpLeftBackColor() {
        return this.upLeftBackColor;
    }

    public String getDownRightBackColor() {
        return this.downRightBackColor;
    }

    public String getUpLeftBackSize() {
        return this.upLeftBackSize;
    }

    public String getDownRightBackSize() {
        return this.downRightBackSize;
    }

    public Element getUpLeftElement() {
        return this.upLeftContext;
    }

    public Element getDownRightElement() {
        return this.downRightContext;
    }

    public boolean isVerticalSplit() {
        return !hSplit;
    }

    public boolean isHorizontalSplit() {
        return hSplit;
    }

    public void setUpDisplay(boolean value) {
        upLeftDisplay = value;
    }

    public void setDownDisplay(boolean value) {
        downRightDisplay = value;
    }

    public void setLeftDisplay(boolean value) {
        upLeftDisplay = value;
    }

    public void setRightDisplay(boolean value) {
        downRightDisplay = value;
    }

    public boolean isUpLeftDisplay() {
        return upLeftDisplay;
    }

    public boolean isDownRightDisplay() {
        return downRightDisplay;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList,  PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writeSplitter(out, this,docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getSplitterHTML(this, docHTML, docList,control);
    }

    public void writeHTML(PrintWriter out, PageController control)
        throws IOException, boRuntimeException {
        writeHTML(out, null, null, control);
    }

    public String getHTML(PageController control)
        throws IOException, boRuntimeException {
        return getHTML(null, null, control);
    }
    
    public static Splitter getSplitter(Element up,Element downLeft,Element downRight) 
    {
        return getSplitter(up,downLeft,"50%",downRight,"50%");
    }
    public static Splitter getSplitter(Element up,Element downLeft,String downLeftHeight,Element downRight,String downRightHeight) 
    {
        Splitter h = Splitter.splitHorizontal("up","down" , null, null, "50%", null, null);
        Splitter v = Splitter.splitVertical("left", "right", null, null,downLeftHeight, null, downRightHeight);

        h.addUpSplitElement(up);
        v.addLeftSplitElement(downLeft);
        v.addRightSplitElement(downRight);
        h.addDownSplitElement(v);
        return h;
    }    
    public static Splitter getSplitter(Element up,Element down) 
    {
        return getSplitter(up,"50%",down,"50%");
    }      
    public static Splitter getSplitter(Element up,String upBackHeight,Element down,String downBackHeight) 
    {
        Splitter h = Splitter.splitHorizontal("up","down", null, null, upBackHeight, null, downBackHeight);
        h.addUpSplitElement(up);
        h.addDownSplitElement(down);
        return h;
    }       
}
