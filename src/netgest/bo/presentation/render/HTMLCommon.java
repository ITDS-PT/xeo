/*Enconding=UTF-8*/
package netgest.bo.presentation.render;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class HTMLCommon {
    //HTML comum
    public static final char[] HTML_BEGIN = "<HTML>".toCharArray();
    public static final char[] HTML_END = "</HTML>".toCharArray();
    public static final char[] HTML_BODY_BEGIN = "<BODY>".toCharArray();
    public static final char[] HTML_BODY_BEGIN_NOT_CLOSE = "<BODY ".toCharArray();
    public static final char[] HTML_BODY_END = "</BODY>".toCharArray();
    public static final char[] HTML_TBODY_BEGIN = "<TBODY>".toCharArray();
    public static final char[] HTML_TBODY_BEGIN_NOT_CLOSE = "<TBODY ".toCharArray();
    public static final char[] HTML_TBODY_END = "</TBODY>".toCharArray();
    public static final char[] HTML_STYLE_BEGIN = "<STYLE>".toCharArray();
    public static final char[] HTML_STYLE_BEGIN_NOT_CLOSE = "<STYLE ".toCharArray();
    public static final char[] HTML_STYLE_END = "</STYLE>".toCharArray();
    public static final char[] HTML_STYLE_TEXT_CSS_BEGIN = "<STYLE type=\"text/css\">".toCharArray();
    public static final char[] HTML_FORM_BEGIN = "<FORM>".toCharArray();
    public static final char[] HTML_FORM_BEGIN_NOT_CLOSE = "<FORM".toCharArray();
    public static final char[] HTML_FORM_END_HTML = "</FORM>".toCharArray();
    public static final char[] HTML_COLGROUP_BEGIN = "<COLGROUP>".toCharArray();
    public static final char[] HTML_COLGROUP_BEGIN_NOT_CLOSE = "</COLGROUP ".toCharArray();
    public static final char[] HTML_COLGROUP_END = "</COLGROUP>".toCharArray();
    public static final char[] HTML_HEAD_BEGIN = "<HEAD>".toCharArray();
    public static final char[] HTML_HEAD_BEGIN_NOT_CLOSE = "<HEAD ".toCharArray();
    public static final char[] HTML_HEAD_END = "</HEAD>".toCharArray();
    public static final char[] HTML_TITLE_BEGIN = "<TITLE>".toCharArray();
    public static final char[] HTML_TITLE_END = "</TITLE>".toCharArray();
    public static final char[] HTML_END_EMPTY_ELEMENT = " />".toCharArray();
    public static final char[] HTML_SPAN_BEGIN = "<SPAN>".toCharArray();
    public static final char[] HTML_SPAN_BEGIN_NOT_CLOSE = "<SPAN ".toCharArray();
    public static final char[] HTML_SPAN_END = "</SPAN>".toCharArray();
    public static final char[] HTML_TRUE = "true".toCharArray();
    public static final char[] HTML_FALSE = "false".toCharArray();
    public static final char[] HTML_TABLE_BEGIN = "<TABLE>".toCharArray();
    public static final char[] HTML_TABLE_BEGIN_NOT_CLOSE = "<TABLE ".toCharArray();
    public static final char[] HTML_TABLE_END = "</TABLE>".toCharArray();
    public static final char[] HTML_TABLE_LINE_BEGIN = "<TR>".toCharArray();
    public static final char[] HTML_TABLE_LINE_BEGIN_NOT_CLOSE = "<TR ".toCharArray();
    public static final char[] HTML_TABLE_LINE_END = "</TR>".toCharArray();
    public static final char[] HTML_TABLE_COLUMN_BEGIN = "<TD>".toCharArray();
    public static final char[] HTML_TABLE_COLUMN_BEGIN_NOT_CLOSE = "<TD ".toCharArray();
    public static final char[] HTML_TABLE_COLUMN_END = "</TD>".toCharArray();
    public static final char[] HTML_INPUT_NOT_CLOSE = "<INPUT ".toCharArray();
    public static final char[] HTML_BR = "<br>".toCharArray();
    public static final char[] HTML_BLANK_SPACE = "&nbsp;".toCharArray();
    public static final char[] HTML_COL_BEGIN = "<COL>".toCharArray();
    public static final char[] HTML_COL_END = "</COL>".toCharArray();
    public static final char[] HTML_COL_BEGIN_NOT_CLOSE = "<COL ".toCharArray();
    public static final char[] HTML_DIV_BEGIN = "<DIV>".toCharArray();
    public static final char[] HTML_DIV_END = "</DIV>".toCharArray();
    public static final char[] HTML_DIV_BEGIN_NOT_CLOSE = "<DIV ".toCharArray();
    public static final char[] HTML_SCRIPT_BEGIN = "<SCRIPT>".toCharArray();
    public static final char[] HTML_SCRIPT_END = "</SCRIPT>".toCharArray();
    public static final char[] HTML_ANCHOR_BEGIN = "<a>".toCharArray();
    public static final char[] HTML_ANCHOR_BEGIN_NOT_CLOSE = "<a ".toCharArray();
    public static final char[] HTML_ANCHOR_END = "</a>".toCharArray();
    public static final char[] HTML_IFRAME_BEGIN = "<iframe>".toCharArray();
    public static final char[] HTML_IFRAME_BEGIN_NOT_CLOSE = "<iframe ".toCharArray();
    public static final char[] HTML_IFRAME_END = "</iframe> ".toCharArray();
    public static final char[] HTML_BOLD_BEGIN = "<B> ".toCharArray();
    public static final char[] HTML_BOLD_END = "</B> ".toCharArray();

    //Words
    public static final char[] WORD_HEIGHT = "HEIGHT".toCharArray();
    public static final char[] WORD_WIDTH = "WIDTH".toCharArray();
    public static final char[] WORD_BORDER = "BORDER".toCharArray();
    public static final char[] WORD_CELLSPACING = "CELLSPACING".toCharArray();
    public static final char[] WORD_CELLPADDING = "CELLPADDING".toCharArray();
    public static final char[] WORD_TYPE = "TYPE".toCharArray();
    public static final char[] WORD_VALUE = "VALUE".toCharArray();
    public static final char[] WORD_CLASS = "CLASS".toCharArray();
    public static final char[] WORD_SUBMITFORM = "submitForm".toCharArray();
    public static final char[] WORD_COLOROPTION = "colorOption".toCharArray();
    public static final char[] WORD_TABINDEX = "tabindex".toCharArray();
    public static final char[] WORD_DISABLED = "disabled".toCharArray();
    public static final char[] WORD_CHECKBOX = "checkbox".toCharArray();
    public static final char[] WORD_ID = "id".toCharArray();
    public static final char[] WORD_NAME = "name".toCharArray();
    public static final char[] WORD_ONCLICK = "onclick".toCharArray();
    public static final char[] WORD_ACCESSKEY = "accessKey".toCharArray();
    public static final char[] WORD_TITLE = "title".toCharArray();
    public static final char[] WORD_STYLE = "style".toCharArray();
    public static final char[] WORD_BKG_COLOR = "background-color".toCharArray();
    public static final char[] WORD_VALIGN = "valign".toCharArray();
    public static final char[] WORD_ALIGN = "align".toCharArray();
    public static final char[] WORD_TOP = "top".toCharArray();

    //Simbolos
    public static final char[] SYMBOL_GT = ">".toCharArray();
    public static final char[] SYMBOL_PLICA = "'".toCharArray();
    public static final char[] SYMBOL_LCURLY = "(".toCharArray();
    public static final char[] SYMBOL_RCURLY = ")".toCharArray();
    public static final char[] SYMBOL_EQUAL = "=".toCharArray();
    public static final char[] SYMBOL_QUOTE = "\"".toCharArray();
    public static final char[] SYMBOL_DOUBLE_POINT = ":".toCharArray();
    public static final char[] SYMBOL_SLASH_DOT = ";".toCharArray();
    public static final char[] SYMBOL_SLASH = ",".toCharArray();
    public static final char[] SYMBOL_AND = "&".toCharArray();

    //UTILS
    public static final char[] UTIL_WHITE_SPACE = " ".toCharArray();
    public static final char[] UTIL_NEW_LINE = "\r\n".toCharArray();

    //NOMES UTILIZADOS
    public static final char[] BUTTON_SEARCH = "Pesquisar".toCharArray();
    public static final char[] BUTTON_OK = "OK".toCharArray();

    /**
     *
     * @Company Enlace3
     * @since
     */
    private HTMLCommon() {
    }
}
