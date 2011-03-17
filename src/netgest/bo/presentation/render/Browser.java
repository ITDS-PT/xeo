/*Enconding=UTF-8*/
package netgest.bo.presentation.render;

import netgest.bo.boConfig;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.boRuntimeException;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class Browser {
    //browser's
    public static final int IE = 0;
    public static final int MOZILLA = 1;

    //static string
    private static final String IE_1 = "IE";
    private static final String IE_2 = "INTERNET EXPLORER";
    private static final String IE_3 = "MICROSOFT INTERNET EXPLORER";
    private static final String MOZILLA_1 = "MOZILLA";
    private static final String MOZILLA_2 = "NETSCAPE";

    //themes directory
    private static final String THEME_DIR = "Themes/";

    //library directory
    private static final String LIBRARY_DIR = "Library/";

    //subdirectories
    public static final int BROWSER_FORM = 0;
    private static final String FORM_DIR = "form/";
    public static final int BROWSER_BOXINFO = 1;
    private static final String BOXINFO_DIR = "boxinfo/";
    public static final int BROWSER_DIALOG = 2;
    private static final String DIALOG_DIR = "dialog/";
    public static final int BROWSER_DOC = 3;
    private static final String DOC_DIR = "doc/";
    public static final int BROWSER_FRAME = 4;
    private static final String FRAME_DIR = "frame/";
    public static final int BROWSER_GRID = 5;
    private static final String GRID_DIR = "grid/";
    public static final int BROWSER_GROUPGRID = 6;
    private static final String GROUPGRID_DIR = "groupgrid/";
    public static final int BROWSER_MENU = 7;
    private static final String MENU_DIR = "menu/";
    public static final int BROWSER_MENUTREE = 8;
    private static final String MENUTREE_DIR = "menutree/";
    public static final int BROWSER_PRINTEXP = 9;
    private static final String PRINTEXP_DIR = "printexp/";
    public static final int BROWSER_TABS = 10;
    private static final String TABS_DIR = "boxinfo/";
    public static final int BROWSER_WKFL = 11;
    private static final String WKFL_DIR = "wkfl/";

    //Browser actual
    private static int PRODUCING_FOR_BROWSER_CODE = -1;
    private static String PRODUCING_FOR_BROWSER_NAME = null;
    private static String PRODUCING_FOR_BROWSER_THEME = null;
    private static String PRODUCING_FOR_BROWSER_DIR_PR = null;
    private static String PRODUCING_FOR_BROWSER_THEME_DIR = null;
    private static String PRODUCING_FOR_BROWSER_LIBRARY_DIR = null;

    /**
     *
     * @Company Enlace3
     * @since
     */
    private Browser() {
    }

    public static int getBrowserCode() {
        try {
            if (PRODUCING_FOR_BROWSER_CODE == -1) {
                PRODUCING_FOR_BROWSER_CODE = boConfig.getBrowserCode();
                PRODUCING_FOR_BROWSER_NAME = getBrowserName(boConfig.getBrowserCode());
                PRODUCING_FOR_BROWSER_DIR_PR = boConfig.getBrowserDirPrefix();
                PRODUCING_FOR_BROWSER_THEME = boConfig.getBrowserTheme();
            }

            return PRODUCING_FOR_BROWSER_CODE;
        } catch (boRuntimeException e) {
        }

        return -1;
    }

    public static String getBrowserName() {
        try {
            if (PRODUCING_FOR_BROWSER_CODE == -1) {
                getBrowserCode();
            }

            return PRODUCING_FOR_BROWSER_NAME;
        } catch (Exception e) {
        }

        return null;
    }

    public static String getBrowserTheme() {
        try {
            if (PRODUCING_FOR_BROWSER_CODE == -1) {
                getBrowserCode();
            }

            return PRODUCING_FOR_BROWSER_THEME;
        } catch (Exception e) {
        }

        return null;
    }

    public static String getBrowserDirPrefix() {
        try {
            if (PRODUCING_FOR_BROWSER_CODE == -1) {
                getBrowserCode();
            }

            return PRODUCING_FOR_BROWSER_DIR_PR;
        } catch (Exception e) {
        }

        return null;
    }

    private static String getDir(int code) {
        switch (code) {
        case BROWSER_FORM:
            return FORM_DIR;

        case BROWSER_BOXINFO:
            return BOXINFO_DIR;

        case BROWSER_DIALOG:
            return DIALOG_DIR;

        case BROWSER_DOC:
            return DOC_DIR;

        case BROWSER_FRAME:
            return FRAME_DIR;

        case BROWSER_GRID:
            return GRID_DIR;

        case BROWSER_GROUPGRID:
            return GROUPGRID_DIR;

        case BROWSER_MENU:
            return MENU_DIR;

        case BROWSER_MENUTREE:
            return MENUTREE_DIR;

        case BROWSER_PRINTEXP:
            return PRINTEXP_DIR;

        case BROWSER_TABS:
            return TABS_DIR;

        case BROWSER_WKFL:
            return WKFL_DIR;

        default:
            return null;
        }
    }

    public static String getThemeDir() {
        try {
            if (PRODUCING_FOR_BROWSER_CODE == -1) {
                getBrowserCode();
            }

            if (PRODUCING_FOR_BROWSER_THEME_DIR == null) {
                StringBuffer sb = new StringBuffer();
                sb.append(PRODUCING_FOR_BROWSER_DIR_PR).append(THEME_DIR)
                  .append(PRODUCING_FOR_BROWSER_THEME).append("/");
                PRODUCING_FOR_BROWSER_THEME_DIR = sb.toString();
            }

            return PRODUCING_FOR_BROWSER_THEME_DIR;
        } catch (Exception e) {
        }

        return null;
    }

    public static String getLibraryDir() {
        if (PRODUCING_FOR_BROWSER_CODE == -1) {
            getBrowserCode();
        }

        if (PRODUCING_FOR_BROWSER_LIBRARY_DIR == null) {
            StringBuffer sb = new StringBuffer();
            sb.append(PRODUCING_FOR_BROWSER_DIR_PR).append(LIBRARY_DIR);
            PRODUCING_FOR_BROWSER_LIBRARY_DIR = sb.toString();
        }

        return PRODUCING_FOR_BROWSER_LIBRARY_DIR;
    }

    public static int getBrowserCode(String browser) throws boRuntimeException {
        String aux = browser.toUpperCase();

        if (IE_1.equals(aux) || IE_2.equals(aux) || IE_3.equals(aux)) {
            return IE;
        }

        if (MOZILLA_1.equals(aux) || MOZILLA_2.equals(aux)) {
            return MOZILLA;
        }

        throw new boRuntimeException("", MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"), null);
    }

    public static String getBrowserName(int code) throws boRuntimeException {
        switch (code) {
        case IE:
            return "Microsoft Internet Explorer";

        case MOZILLA:
            return "MOZILLA";
        }

        throw new boRuntimeException("",  MessageLocalizer.getMessage("BROWSER_NOT_SUPPORTED_OR_UNKNOWN"), null);
    }
}
