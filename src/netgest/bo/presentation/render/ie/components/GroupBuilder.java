/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;
import java.io.PrintWriter;

import netgest.bo.localized.JSPMessages;
import netgest.bo.presentation.render.HTMLCommon;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Explorer;
import netgest.bo.presentation.render.elements.Group;
import netgest.bo.runtime.boRuntimeException;

/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class GroupBuilder {
    private static final char[] GROUP_1_BEGIN = "<span id='0' class='colGroupInterval' >&nbsp;</span>".toCharArray();
    private static String GROUP_1_BEGIN_1str="<a title='"+JSPMessages.getString("GroupBuilder1")+"' relatedTree='";
    private static final char[] GROUP_1_BEGIN_1 = GROUP_1_BEGIN_1str.toCharArray();
    private static final char[] GROUP_1_BEGIN_2 = "' class='colGroup' href='javascript:toggleExplorerOrderGroup(\"".toCharArray();
    private static final char[] GROUP_1_BEGIN_3 = "\",\"".toCharArray();
    private static final char[] GROUP_1_BEGIN_4 = "\")' id='".toCharArray();
    private static final char[] GROUP_1_BEGIN_5 = "'>".toCharArray();
    private static final char[] GROUP_2_BEGIN = "<span id='".toCharArray();
    private static final char[] GROUP_2_BEGIN_1 = "' relatedTree='".toCharArray();
    private static final char[] GROUP_2_BEGIN_2 = "' class='colGroupInterval' >&nbsp;</span>".toCharArray();
    private static final char[] GROUP_ANCHOR = "<a  relatedTree='".toCharArray();
    private static String GROUP_ANCHOR_1str = "' title='"+JSPMessages.getString("GroupBuilder2")+"' class='colGroupNone' href='javascript:' id='";
    private static final char[] GROUP_ANCHOR_1 = GROUP_ANCHOR_1str.toCharArray();
    private static final char[] GROUP_WORD_NONE = "NoNe".toCharArray();
    private static final char[] GROUP_WORD_GRUPOS = JSPMessages.getString("docHTML_treeView.466").toCharArray();

    /**
     *
     * @Company Enlace3
     * @since
     */
    public GroupBuilder() {
    }

    public static void writeGroup(PrintWriter out, Group elementGoup,
        PageController control) throws IOException, boRuntimeException {
        if (elementGoup.getCode() != null) {
            out.write(elementGoup.getCode().toCharArray());

            return;
        }

        Explorer tree = elementGoup.getTreeRuntime();
        out.write(GROUP_1_BEGIN);

        for (int i = 0; i < tree.getGroupProvider().groupSize(); i++) {
            out.write(GROUP_1_BEGIN_1);
            out.write(tree.p_key.toCharArray());
            out.write(GROUP_1_BEGIN_2);
            out.write(tree.p_key.toCharArray());
            out.write(GROUP_1_BEGIN_3);
            out.write(String.valueOf(i).toCharArray());
            out.write(GROUP_1_BEGIN_4);
            out.write(tree.getGroupProvider().getGroup(i).getName().toCharArray());
            out.write(GROUP_1_BEGIN_5);
            out.write(tree.getImageSort(tree.getGroupProvider().getGroupOrder(i)).toCharArray());

            out.write(tree.getGroupLabel(i).toCharArray());
            out.write(HTMLCommon.HTML_ANCHOR_END);

            out.write(GROUP_2_BEGIN);
            out.write(String.valueOf(i + 1).toCharArray());
            out.write(GROUP_2_BEGIN_1);
            out.write(tree.p_key.toCharArray());
            out.write(GROUP_2_BEGIN_2);
        }

        out.write(GROUP_ANCHOR);
        out.write(tree.p_key.toCharArray());
        out.write(GROUP_ANCHOR_1);
        out.write(GROUP_WORD_NONE);
        out.write(GROUP_1_BEGIN_5);
        out.write(GROUP_WORD_GRUPOS);
        out.write(HTMLCommon.HTML_ANCHOR_END);
    }
}
