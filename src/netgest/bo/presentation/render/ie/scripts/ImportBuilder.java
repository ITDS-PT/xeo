/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.scripts;

import java.io.IOException;
import java.io.PrintWriter;

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
public class ImportBuilder {
    //CSS
    public static final int IMP_CSS_SPLITTER = 0;
    private static final char[] IMP_CSS_SPLITTER_B = "@import url('ieLibrary/splitter/splitter.css');".toCharArray();
    public static final int IMP_CSS_UI_SPLITTER = 1;
    private static final char[] IMP_CSS_UI_SPLITTER_B = ("@import url('" +
        Browser.getLibraryDir() + "splitter/ui-splitter.css');").toCharArray();
    public static final int IMP_CSS_UI_GLOBAL = 2;
    private static final char[] IMP_CSS_UI_GLOBAL_B = ("@import url('" +
        Browser.getThemeDir() + "global/ui-global.css');").toCharArray();
    public static final int IMP_CSS_MENU = 3;
    private static final char[] IMP_CSS_MENU_B = ("@import url('" +
        Browser.getLibraryDir() + "menu/menu.css');").toCharArray();
    public static final int IMP_CSS_UI_MENU = 4;
    private static final char[] IMP_CSS_UI_MENU_B = ("@import url('" +
        Browser.getThemeDir() + "menu/ui-menu.css');").toCharArray();
    public static final int IMP_CSS_XEO_NEW = 5;
    private static final char[] IMP_CSS_XEO_NEW_B = "@import url('xeoNew.css');".toCharArray();
    public static final int IMP_CSS_EXPLORER = 6;
    private static final char[] IMP_CSS_EXPLORER_B = ("@import url('" +
        Browser.getThemeDir() + "explorer/explorer.css');").toCharArray();


    /**
     *
     * @Company Enlace3
     * @since
     */
    private ImportBuilder() {
    }

    public static boolean canWrite(int[] importCode, PageController control) {
        if (importCode != null) {
            for (int i = 0; i < importCode.length; i++) {
                if (canWrite(importCode[i], control)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean canWrite(int importCode, PageController control) {
        if (control.canWriteImport(importCode)) {
            switch (importCode) {
            case IMP_CSS_MENU:
            case IMP_CSS_SPLITTER:
            case IMP_CSS_UI_GLOBAL:
            case IMP_CSS_UI_MENU:
            case IMP_CSS_UI_SPLITTER:
            case IMP_CSS_EXPLORER:
            case IMP_CSS_XEO_NEW:
                return true;

            default:
                return false;
            }
        }

        return false;
    }

    public static boolean writeImport(PrintWriter out, int[] importCode,
        PageController control) throws IOException {
        boolean toRet = false;

        if (importCode != null) {
            for (int i = 0; i < importCode.length; i++) {
                if (writeImport(out, importCode[i], control)) {
                    out.write(HTMLCommon.UTIL_NEW_LINE);
                    toRet = true;
                }
            }
        }

        return toRet;
    }

    public static boolean writeImport(PrintWriter out, int importCode,
        PageController control) throws IOException {
        if (control.canWriteImport(importCode)) {
            switch (importCode) {
            case IMP_CSS_MENU:
                out.write(IMP_CSS_MENU_B);
                control.markWriteImport(IMP_CSS_MENU);

                break;

            case IMP_CSS_SPLITTER:
                out.write(IMP_CSS_SPLITTER_B);
                control.markWriteImport(IMP_CSS_SPLITTER);

                break;

            case IMP_CSS_UI_GLOBAL:
                out.write(IMP_CSS_UI_GLOBAL_B);
                control.markWriteImport(IMP_CSS_UI_GLOBAL);

                break;

            case IMP_CSS_UI_MENU:
                out.write(IMP_CSS_UI_MENU_B);
                control.markWriteImport(IMP_CSS_UI_MENU);

                break;

            case IMP_CSS_UI_SPLITTER:
                out.write(IMP_CSS_UI_SPLITTER_B);
                control.markWriteImport(IMP_CSS_UI_SPLITTER);

                break;

            case IMP_CSS_XEO_NEW:
                out.write(IMP_CSS_XEO_NEW_B);
                control.markWriteImport(IMP_CSS_XEO_NEW);
                break;
            case IMP_CSS_EXPLORER:
                out.write(IMP_CSS_EXPLORER_B);
                control.markWriteImport(IMP_CSS_EXPLORER);
                break;

            default:
                return false;
            }

            return true;
        }

        return false;
    }
}
