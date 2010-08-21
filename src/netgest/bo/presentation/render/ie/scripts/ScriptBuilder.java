/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.scripts;

import java.io.IOException;
import java.io.PrintWriter;

import netgest.bo.def.boDefHandler;
import netgest.bo.presentation.render.Browser;
import netgest.bo.presentation.render.HTMLCommon;
import netgest.bo.presentation.render.PageController;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class ScriptBuilder {
    public static final int SC_JS_MENU = 0;
    private static final char[] SC_JS_MENU_B = ("<script LANGUAGE=\"javascript\" SRC=\"" +
        Browser.getThemeDir() + "menu/ui-menu.js\"></script>").toCharArray();
    public static final int SC_JS_XEO = 1;
    public static final int SC_JS_XEO_LANG = 11; 
    public static final char[] SC_JS_XEO_LANG_B = "<script LANGUAGE=\"javascript\" SRC=\"jsmessages/jsmessages.jsp\"></script>".toCharArray();
    private static final char[] SC_JS_XEO_B = "<script LANGUAGE=\"javascript\" SRC=\"xeo.js\"></script>".toCharArray();
    public static final int SC_JS_EXPLORER = 2;
    private static final char[] SC_JS_EXPLORER_B = ("<script LANGUAGE=\"javascript\" SRC=\"" +
        Browser.getLibraryDir() + "explorer/bo_explorer_std.js\"></script>").toCharArray();

    public static final int SC_MOUSEMENU_1 = 3;
    private static final char[] SC_MOUSEMENU_1_B = ("<script LANGUAGE=\"javascript\">ns4=(document.layers)?true:false;</script>").toCharArray();
    public static final int SC_MOUSEMENU_2 = 6;
    private static final char[] SC_MOUSEMENU_2_B = ("<script LANGUAGE=\"javascript\" src=\""+Browser.getLibraryDir() + "mouseMenu/contextmenu.js\"></script>").toCharArray();
    /**
     *
     * @Company Enlace3
     * @since
     */
    public ScriptBuilder() {
    }

    public static String getExplorerLabel(String objName, String label, String imagem)
    {
        if(objName != null && objName.length() > 0)
        {
            if(label == null || label.length() == 0)
            {
                boDefHandler bodef = boDefHandler.getBoDefinition(objName);
                label = bodef.getLabel();
            }
            if(imagem == null || imagem.length() == 0)
            {
                boDefHandler bodef = boDefHandler.getBoDefinition(objName);
                imagem = bodef.getSrcForIcon16();
            }
            if(objName != null && objName.length() > 0 && label != null && label.length() > 0)
            {
                StringBuffer sb = new StringBuffer();
                sb.append("var objLabel=\"<SPAN title='")
                .append(label)
                 .append("'><img style='cursor:hand' hspace='3' border='0' align='absmiddle' class='lui'")
                  .append(" src='");
                if(imagem != null && imagem.length() > 0)
                {
                    sb.append(imagem);
                }
                else
                {
                    sb.append("resources/" + objName + "/ico16.gif");
                }
                sb.append("' width='16' height='16'/>")
                  .append(label)
                  .append("</span>\"");
                return sb.toString();
            }
            else
            {
            
            }
        }
        return "";
    }
    
    public static boolean canWrite(int[] scriptCode, PageController control) {
        if (scriptCode != null) {
            for (int i = 0; i < scriptCode.length; i++) {
                if (canWrite(scriptCode[i], control)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean canWrite(int scriptCode, PageController control) {
        if (control.canWriteScript(scriptCode)) {
            switch (scriptCode) {
            case SC_JS_MENU:
            case SC_JS_XEO:
            case SC_JS_EXPLORER:
            case SC_MOUSEMENU_1:
            case SC_MOUSEMENU_2:
                return true;

            default:
                return false;
            }
        }

        return false;
    }

    public static boolean writeScript(PrintWriter out, int[] scriptCode,
        PageController control) throws IOException {
        boolean toRet = false;

        if (scriptCode != null) {
            for (int i = 0; i < scriptCode.length; i++) {
                if (writeScript(out, scriptCode[i], control)) {
                    out.write(HTMLCommon.UTIL_NEW_LINE);
                    toRet = true;
                }
            }
        }

        return toRet;
    }

    public static boolean writeScript(PrintWriter out, int scriptCode,
        PageController control) throws IOException {
        if (control.canWriteScript(scriptCode)) {
            switch (scriptCode) {
            case SC_JS_MENU:
                out.write(SC_JS_MENU_B);
                control.markWriteScript(SC_JS_XEO);

                break;
            case SC_JS_XEO_LANG:
                out.write( SC_JS_XEO_LANG_B );
                control.markWriteScript(SC_JS_XEO_LANG);
                break;
                
            case SC_JS_XEO:
                out.write(SC_JS_XEO_B);
                control.markWriteScript(SC_JS_XEO);

                break;
            
            case SC_JS_EXPLORER:
                out.write(SC_JS_EXPLORER_B);
                control.markWriteScript(SC_JS_EXPLORER);
                break;
                
            case SC_MOUSEMENU_1:
                out.write(SC_MOUSEMENU_1_B);
                control.markWriteScript(SC_MOUSEMENU_1);
                break;
                
            case SC_MOUSEMENU_2:
                out.write(SC_MOUSEMENU_2_B);
                control.markWriteScript(SC_MOUSEMENU_2);
                break;

            default:
                return false;
            }

            return true;
        }

        return false;
    }
}
