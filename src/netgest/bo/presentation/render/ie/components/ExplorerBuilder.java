/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;
import java.io.PrintWriter;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.HTMLCommon;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Element;
import netgest.bo.presentation.render.elements.Explorer;
import netgest.bo.presentation.render.elements.Splitter;
import netgest.bo.presentation.render.ie.scripts.FunctionBuilder;
import netgest.bo.presentation.render.ie.scripts.ScriptBuilder;
import netgest.bo.runtime.boRuntimeException;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class ExplorerBuilder
{
    private static final char[] EXP_STARTING_TABLE_BEGIN = "<TABLE  cellpadding=\"0\" cellspacing=\"1\" class=\"layout containerexp\">".toCharArray();

    //    private static final char[] EXP_MENU_TABLE_BEGIN = "<TABLE id=\"menubar\" cellpadding='0' cellspacing='0'>".toCharArray();
    //    private static final char[] EXP_PARAM_TABLE_BEGIN = "<TABLE id=\"parameterbar\" cellpadding='0' cellspacing='0'>".toCharArray();
    //    private static final char[] EXP_DATA_TABLE_BEGIN = "<TABLE id=\"databar\" cellpadding='0' cellspacing='0' onselectstart=\"return false;\">".toCharArray();
    private static final char[] EXP_100            = "100%".toCharArray();
    //private static final char[] EXP_30PX           = "30px".toCharArray();
    private static final char[] EXP_24PX           = "24px".toCharArray();
    private static final char[] EXP_60PX           = "60px".toCharArray();
    private static final char[] EXP_140PX          = "140px".toCharArray();
    private static final char[] EXP_DISPLAY_NONE   = "style=\"display:none\"".toCharArray();
    private static final char[] EXP_WORD_MENU_BAR  = "menubar".toCharArray();
    private static final char[] EXP_WORD_GROUP_BAR = "groupbar".toCharArray();
    private static final char[] EXP_WORD_PARAM_BAR = "parambar".toCharArray();
    private static final char[] EXP_WORD_DATA_BAR  = "databar".toCharArray();
    private static final char[] EXP_WITHOUT_TREE   = "<tr><td></td></tr>".toCharArray();

    //    private static final char[] OBJ_LABEL = "\nvar objLabel=\"<img style='cursor:hand' hspace='3' border='0' align='absmiddle' class='lui' title='".toCharArray();
    //    private static final char[] OBJ_LABEL_1 = "' src='".toCharArray();
    //    private static final char[] OBJ_LABEL_2 = "' width='16' height='16'/><span title='".toCharArray();
    //    private static final char[] OBJ_LABEL_3 = "'>".toCharArray();
    //    private static final char[] OBJ_LABEL_END = "</span>\";\n".toCharArray();
    private static final int[] EXP_FUNTIONS = 
        {
            FunctionBuilder.FUNC_SETPREVIEWDOWN, FunctionBuilder.FUNC_SETPREVIEWRIGHT
        };
    private static final int[] EXP_SCRIPT = { ScriptBuilder.SC_MOUSEMENU_1//  ,
        //   ScriptBuilder.SC_MOUSEMENU_2
        };

    /**
     *
     * @Company Enlace3
     * @since
     */
    public ExplorerBuilder()
    {
    }

    public static void writeExplorer(
        PrintWriter out, Explorer exp, docHTML doc, docHTML_controler docList, PageController control
    )
        throws IOException, boRuntimeException
    {
        if (exp.haveParameters(doc.getEboContext()))
        {
            exp.getParameters().setDisplay(true);
        }
        else
        {
            exp.getParameters().setDisplay(false);
        }

        boolean b = exp.haveBlankParameters(doc.getEboContext());

        if (!b && !exp.analizeSql(doc.getEboContext()))
        {
            b = true;
        }

        if (b)
        {
            exp.setParameterDesignMode(true);
        }
        else
        {
            exp.setParameterDesignMode(false);
        }

        //        No style 
        ScriptBuilder.writeScript(out, EXP_SCRIPT, control);

        //        writeStyle(out, control)
        //        if (ImportBuilder.canWrite(EXP_IMPORT, control)) {
        //            out.write(HTMLCommon.HTML_STYLE_TEXT_CSS_BEGIN);
        //            out.write(HTMLCommon.UTIL_NEW_LINE);
        //            ImportBuilder.writeImport(out, EXP_IMPORT, control);
        //            out.write(HTMLCommon.HTML_STYLE_END);
        //            out.write(HTMLCommon.UTIL_NEW_LINE);
        //        }
        if (FunctionBuilder.canWrite(EXP_FUNTIONS, control))
        {
            out.write(HTMLCommon.HTML_SCRIPT_BEGIN);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            FunctionBuilder.writeFunction(out, EXP_FUNTIONS, control);
            out.write(HTMLCommon.HTML_SCRIPT_END);
            out.write(HTMLCommon.UTIL_NEW_LINE);
        }

        //        out.write(HTMLCommon.HTML_SCRIPT_BEGIN);
        //        out.write(OBJ_LABEL);
        //        out.write(exp.getId().toCharArray());
        //        out.write(OBJ_LABEL_1);
        //        out.write(exp.getImg().toCharArray());
        //        out.write(OBJ_LABEL_2);
        //        out.write(exp.getId().toCharArray());
        //        out.write(OBJ_LABEL_3);
        //        out.write(exp.getId().toCharArray());
        //        out.write(OBJ_LABEL_END);
        //        out.write(HTMLCommon.HTML_SCRIPT_END);
        writeCode(out, exp, doc, docList, control);
    }

    private static void writeStyle(PrintWriter out, PageController control)
        throws IOException
    {
    }

    private static void writeCode(
        PrintWriter out, Explorer exp, docHTML doc, docHTML_controler docList, PageController control
    )
        throws IOException, boRuntimeException
    {
        out.write(HTMLCommon.UTIL_NEW_LINE);

        out.write(EXP_STARTING_TABLE_BEGIN);

        writeTr(out, exp.getMenu(), true, doc, docList, control, EXP_WORD_MENU_BAR, EXP_24PX);

        // exp.getMenu().writeHTML(out, doc, docList, control);
        //exp.getMouseMenu().writeHTML(out, doc, docList, control);
        //        writeTr(out, exp.getMenu(), true, control, EXP_WORD_MENU_BAR, EXP_30PX);
        out.write(HTMLCommon.UTIL_NEW_LINE);

        out.write(HTMLCommon.UTIL_NEW_LINE);
        writeTr(
            out, exp.getGroup(), (exp.getGroup() == null) ? false : exp.getGroup().getDisplay(), doc, docList,
            control, EXP_WORD_GROUP_BAR, "1%".toCharArray()
        );

        out.write(HTMLCommon.UTIL_NEW_LINE);

        writeTr(
            out, exp.getParameters(), (exp.getParameters() == null) ? false : exp.getParameters().getDisplay(),
            doc, docList, control, EXP_WORD_PARAM_BAR, (!exp.getParameterDesignMode()) ? EXP_140PX : EXP_100
        );
        out.write(HTMLCommon.UTIL_NEW_LINE);

        if (!exp.getParameterDesignMode())
        {
            Splitter expSplit = Splitter.getExplorerSplitter(
                    exp.getTree(), exp.getPreviewRight(), exp.getPreviewDown(), exp.isPreviewOn(),
                    exp.isPreviewOnRight()
                );
            writeTr(out, expSplit, true, doc, docList, control, EXP_WORD_DATA_BAR, EXP_100);
        }
        else
        {
            out.write(EXP_WITHOUT_TREE);
        }

        out.write(HTMLCommon.UTIL_NEW_LINE);
        out.write(HTMLCommon.HTML_TABLE_END);
    }

    private static void writeTr(
        PrintWriter out, Element e, boolean display, docHTML doc, docHTML_controler docList,
        PageController control, char[] word, char[] size
    )
        throws IOException, boRuntimeException
    {
        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN_NOT_CLOSE);
        out.write(HTMLCommon.WORD_CLASS);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(word);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.UTIL_WHITE_SPACE);
        out.write(HTMLCommon.WORD_ID);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(word);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.UTIL_WHITE_SPACE);
        out.write(HTMLCommon.WORD_HEIGHT);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(size);
        out.write(HTMLCommon.SYMBOL_QUOTE);

        if ((e == null) || !display)
        {
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
            out.write(EXP_DISPLAY_NONE);
        }

        out.write(HTMLCommon.SYMBOL_GT);
        out.write(HTMLCommon.UTIL_NEW_LINE);

        //        if(!(e instanceof Splitter))
        //        {
        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
        out.write(HTMLCommon.UTIL_NEW_LINE);

        //        }        
        if (e != null)
        {
            e.writeHTML(out, doc, docList, control);
        }

        //        if(!(e instanceof Splitter))
        //        {
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);

        //        }
        out.write(HTMLCommon.UTIL_NEW_LINE);
        out.write(HTMLCommon.HTML_TABLE_LINE_END);
        out.write(HTMLCommon.UTIL_NEW_LINE);
    }
}
