/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;
import java.io.OutputStream;

import java.io.PrintWriter;
import netgest.bo.dochtml.*;
import netgest.bo.presentation.render.HTMLCommon;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Splitter;
import netgest.bo.presentation.render.ie.scripts.ImportBuilder;
import netgest.bo.runtime.boRuntimeException;

/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class SplitterBuilder {
    private static final char[] SPL_SCRIPT_START = "<style type=\"text/css\">".toCharArray();
    private static final char[] SPL_SCRIPT_END = "</style>".toCharArray();
    private static final char[] SPL_TABLE_START = "<table AQUI=1 cellSpacing=\"0\" cellPadding=\"0\" style=\"height:100%;width:100%\">".toCharArray();
    private static final char[] SPL_VLINE_SEPARATOR_1 = "<td id='tdResizeRight' width='1px'><img class=\"rszLeft ui-rszLeft\" src=\"".toCharArray();
    private static final char[] SPL_VLINE_SEPARATOR_1_NDISPLAY = "<td id='tdResizeRight' width='1px' style=\"display:none\"><img class=\"rszLeft ui-rszLeft\" src=\"".toCharArray();
    private static final char[] SPL_VLINE_SEPARATOR_2 = "\" WIDTH=\"4px\" HEIGHT=\"1px\"></td>".toCharArray();
    private static final char[] SPL_HLINE_SEPARATOR_1 = "<tr id=\"trResizeBottom\" height=\"1px\">".toCharArray();
    private static final char[] SPL_HLINE_SEPARATOR_1_NDISPLAY = "<tr id=\"trResizeBottom\" height=\"1px\" style=\"display:none\">".toCharArray();
    private static final char[] SPL_HLINE_SEPARATOR_2 = "<td colspan='1'><img class=\"rszUp ui-rszUp\" src=\"".toCharArray();
    private static final char[] SPL_HLINE_SEPARATOR_3 = "\" WIDTH=\"1px\" HEIGHT=\"4px\"></td></tr>".toCharArray();
    private static final int[] SPL_IMPORTS = {
        ImportBuilder.IMP_CSS_UI_SPLITTER, ImportBuilder.IMP_CSS_MENU,
        ImportBuilder.IMP_CSS_SPLITTER
    };
    private static final char[] SPL_DISPLAY_NONE = "display:none".toCharArray();
    private static final char[] SPL_IFRAME = "<iframe frameBorder=\"0\" height='100%' width='100%'>".toCharArray();

    /**
     *
     * @Company Enlace3
     * @since
     */
    private SplitterBuilder() {
    }

    public static void writeSplitter(PrintWriter out, Splitter split,
        docHTML doc, docHTML_controler docList,PageController control) throws IOException, boRuntimeException {
        writeStyle(out, control);
        out.write(SPL_TABLE_START);

        if (split.isVerticalSplit()) {
            out.write( HTMLCommon.HTML_TABLE_LINE_BEGIN );
            writeVerticalSplit(out, split, doc, docList, control);
            out.write( HTMLCommon.HTML_TABLE_LINE_END );    
        } else {
            writeHorizontalSplit(out, split, doc, docList, control);
        }
        
        out.write(HTMLCommon.HTML_TABLE_END);
    }

    private static void writeVerticalSplit(PrintWriter out, Splitter split,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN_NOT_CLOSE);

        if (split.getUpLeftName() != null) {
            out.write(HTMLCommon.WORD_ID);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(split.getUpLeftName().toCharArray());
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
            out.write(HTMLCommon.WORD_NAME);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(split.getUpLeftName().toCharArray());
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
        }

        out.write(HTMLCommon.WORD_STYLE);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.WORD_BKG_COLOR);
        out.write(HTMLCommon.SYMBOL_DOUBLE_POINT);
        out.write(split.getUpLeftBackColor().toCharArray());
        out.write(HTMLCommon.SYMBOL_SLASH_DOT);
        out.write(HTMLCommon.WORD_WIDTH);
        out.write(HTMLCommon.SYMBOL_DOUBLE_POINT);
        out.write(split.getUpLeftBackSize().toCharArray());

        if (!split.isUpLeftDisplay()) {
            out.write(HTMLCommon.SYMBOL_SLASH_DOT);
            out.write(SPL_DISPLAY_NONE);
        }

        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.SYMBOL_GT);

        if (split.getUpLeftElement() != null) {
            out.write(HTMLCommon.UTIL_NEW_LINE);
            split.getUpLeftElement().writeHTML(out,doc,docList, control);
        }

        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);

        out.write(HTMLCommon.UTIL_NEW_LINE);

        if (!split.isUpLeftDisplay() || !split.isDownRightDisplay()) {
            out.write(SPL_VLINE_SEPARATOR_1_NDISPLAY);
        } else {
            out.write(SPL_VLINE_SEPARATOR_1);
        }

        out.write(split.getSplitImageUrl().toCharArray());
        out.write(SPL_VLINE_SEPARATOR_2);
        out.write(HTMLCommon.UTIL_NEW_LINE);

        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN_NOT_CLOSE);

        if (split.getUpLeftName() != null) {
            out.write(HTMLCommon.WORD_ID);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(split.getDownRightName().toCharArray());
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
            out.write(HTMLCommon.WORD_NAME);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(split.getDownRightName().toCharArray());
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
        }

        out.write(HTMLCommon.WORD_STYLE);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.WORD_BKG_COLOR);
        out.write(HTMLCommon.SYMBOL_DOUBLE_POINT);
        out.write(split.getDownRightBackColor().toCharArray());
        out.write(HTMLCommon.SYMBOL_SLASH_DOT);

        //out.write(HTMLCommon.WORD_WIDTH);
        //out.write(HTMLCommon.SYMBOL_DOUBLE_POINT);
        //out.write(split.getDownRightBackSize().toCharArray());
        //out.write(HTMLCommon.SYMBOL_QUOTE);
        if (!split.isDownRightDisplay()) {
            out.write(HTMLCommon.SYMBOL_SLASH_DOT);
            out.write(SPL_DISPLAY_NONE);
        }

        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.SYMBOL_GT);

        if (split.getDownRightElement() != null) {
            out.write(HTMLCommon.UTIL_NEW_LINE);
            split.getDownRightElement().writeHTML(out,doc, docList, control);
        }

        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
    }

    private static void writeHorizontalSplit(PrintWriter out, Splitter split,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN_NOT_CLOSE);

        if (split.getUpLeftName() != null) {
            out.write(HTMLCommon.WORD_ID);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(split.getUpLeftName().toCharArray());
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
            out.write(HTMLCommon.WORD_NAME);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(split.getUpLeftName().toCharArray());
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
        }

        out.write(HTMLCommon.WORD_STYLE);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.WORD_BKG_COLOR);
        out.write(HTMLCommon.SYMBOL_DOUBLE_POINT);
        out.write(split.getUpLeftBackColor().toCharArray());
        out.write(HTMLCommon.SYMBOL_SLASH_DOT);
        out.write(HTMLCommon.WORD_HEIGHT);
        out.write(HTMLCommon.SYMBOL_DOUBLE_POINT);
        out.write(split.getUpLeftBackSize().toCharArray());

        if (!split.isUpLeftDisplay()) {
            out.write(HTMLCommon.SYMBOL_SLASH_DOT);
            out.write(SPL_DISPLAY_NONE);
        }

        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.SYMBOL_GT);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);

        //        out.write(HTMLCommon.HTML_DIV_BEGIN);
        //        out.write(SPL_IFRAME);
        if (split.getUpLeftElement() != null) {
            out.write(HTMLCommon.UTIL_NEW_LINE);
            split.getUpLeftElement().writeHTML(out,doc, docList, control);
        }

        //        out.write(HTMLCommon.HTML_IFRAME_END);
        //        out.write(HTMLCommon.HTML_DIV_END);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        out.write(HTMLCommon.HTML_TABLE_LINE_END);

        out.write(HTMLCommon.UTIL_NEW_LINE);

        if (!split.isUpLeftDisplay() || !split.isDownRightDisplay()) {
            out.write(SPL_HLINE_SEPARATOR_1_NDISPLAY);
        } else {
            out.write(SPL_HLINE_SEPARATOR_1);
        }

        out.write(SPL_HLINE_SEPARATOR_2);
        out.write(split.getSplitImageUrl().toCharArray());
        out.write(SPL_HLINE_SEPARATOR_3);
        out.write(HTMLCommon.UTIL_NEW_LINE);

        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN_NOT_CLOSE);

        if (split.getDownRightName() != null) {
            out.write(HTMLCommon.WORD_ID);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(split.getDownRightName().toCharArray());
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
            out.write(HTMLCommon.WORD_NAME);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(split.getDownRightName().toCharArray());
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
        }

        out.write(HTMLCommon.WORD_STYLE);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.WORD_BKG_COLOR);
        out.write(HTMLCommon.SYMBOL_DOUBLE_POINT);
        out.write(split.getDownRightBackColor().toCharArray());

        //        out.write(HTMLCommon.SYMBOL_SLASH_DOT);
        //        out.write(HTMLCommon.WORD_HEIGHT);
        //        out.write(HTMLCommon.SYMBOL_DOUBLE_POINT);
        //        out.write(split.getUpLeftBackSize().toCharArray());
        //        out.write(HTMLCommon.SYMBOL_QUOTE);        
        if (!split.isDownRightDisplay()) {
            out.write(HTMLCommon.SYMBOL_SLASH_DOT);
            out.write(SPL_DISPLAY_NONE);
        }

        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.SYMBOL_GT);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);

        if (split.getDownRightElement() != null) {
            out.write(HTMLCommon.UTIL_NEW_LINE);
            split.getDownRightElement().writeHTML(out,doc, docList, control);
        }

        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        out.write(HTMLCommon.HTML_TABLE_LINE_END);
    }

    private static void writeStyle(PrintWriter out, PageController control)
        throws IOException {
        boolean write = ImportBuilder.canWrite(SPL_IMPORTS, control);

        if (write) {
            out.write(SPL_SCRIPT_START);
            ImportBuilder.writeImport(out, SPL_IMPORTS, control);
            out.write(SPL_SCRIPT_END);
        }
    }
}
