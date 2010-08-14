/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Hashtable;

import netgest.bo.boConfig;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.localized.JSPMessages;
import netgest.bo.presentation.render.Browser;
import netgest.bo.presentation.render.HTMLCommon;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Menu;
import netgest.bo.presentation.render.elements.MenuAdHocItem;
import netgest.bo.presentation.render.elements.MenuItem;
import netgest.bo.presentation.render.ie.scripts.FunctionBuilder;
import netgest.bo.presentation.render.ie.scripts.ImportBuilder;
import netgest.bo.presentation.render.ie.scripts.ScriptBuilder;
import netgest.bo.presentation.utils.Bytes;
import netgest.bo.runtime.boRuntimeException;

/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class MenuBuilder {
    //Menu Options
    private static final int MNU_SAVE = 0;
    private static final int MNU_SAVE_CLOSE = 1;
    private static final int MNU_PRINT = 2;
    private static final int MNU_REMOVE = 3;
    private static final int MNU_BACK = 4;
    private static final int MNU_FORWARD = 5;
    private static final int MNU_SEARCH = 6;

    //MENU_TYPE
    public static final int TYPE_MNUBAR_MENU = 0;
    public static final int TYPE_MNULIST_MENU = 0;
    private static final char[] MNU_MNUBAR_MENU = "mnubar menu".toCharArray();
    private static final char[] MNU_MNULIST_MENU = "mnuList menu".toCharArray();
    private static final char[] MNU_WMENU = " menu".toCharArray();
    private static final char[] MNU_BAR = "BAR".toCharArray();
    private static final char[] WORD_CLASS = "class".toCharArray();
    private static final char[] MNU_LIST = "LIST".toCharArray();
    private static final char[] MNU_VSPACE = ("<img hspace=\"3\" src=\"" +
        Browser.getThemeDir() + "menu/mnu_vSpacer.gif\" WIDTH=\"5\" HEIGHT=\"18\">").toCharArray();
    private static final char[] MNU_VSPACER = ("<td style='padding-top:4px;padding-bottom:4px' ><img src='" +
        Browser.getThemeDir() +
        "menu/mnu_hSpacer.gif' WIDTH='2' HEIGHT='100%'/></td>").toCharArray();
    private static final char[] MNU_TABINDEX = "tabindex=\"0\"".toCharArray();
    private static final char[] MNU_ACCESSKEY = "accessKey=".toCharArray();
    private static final char[] MNU_THIRTY = "30".toCharArray();
    private static final char[] MNU_NINE = "9".toCharArray();
    private static final char[] MNU_LABEL = "class=\"mnuLabel\"".toCharArray();
    private static final char[] MNU_OPTION = "class=\"mnuOption\"".toCharArray();
    private static final char[] MNU_ITEM = "class=\"mnuItem\"".toCharArray();
    private static final char[] MNU_HSPACER = "<tr class=\"mnuSpacer\"><td>&nbsp;</td><td><hr class=\"mnuSpacer\"></td></tr>".toCharArray();
    private static final char[] MNU_MENU = "menu=\"".toCharArray();
    private static final char[] MNU_LIST_COL = "<colgroup><col class=\"mnuLeft\"/><col/></colgroup>".toCharArray();
    private static final char[] MNU_IMG_START = "<img class=\"buttonMenu\" src=\"".toCharArray();
    private static final char[] MNU_IMG_END = "\" WIDTH=\"16\" HEIGHT=\"16\"".toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_0 = "<td style='width:170px' ><table  class='explorerfullTextSearch' cellpadding=0 cellspacing=0 width='100%'><tr>".toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_1 = "<td class=\"mnuLabel\" style=\"padding-left:5px;padding-right:5px\">".toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_2 = (JSPMessages.getString("MenuBuilder.24")+
    													"</td><td class='tdTextFull'>").toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_3 = "<input class='explorerfullTextSearch' value='".toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_3_1 = "' id='TEXTSEARCH' key='".toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_4 = "' onchange='setExplorerFullTextGroup(\"".toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_5 = "\",TEXTSEARCH.value )' name = 'TEXTSEARCH' tabIndex='".toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_6 = "'/></td>".toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_7 = ("<td class='tdImgTextFull' ><img  title='"
    	+JSPMessages.getString("MenuBuilder.32")+
    	"' onclick='setExplorerFullTextGroup(\"").toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_8 = ("\",\"\" )' style='' border='0' src='" +
													        Browser.getThemeDir() +
													        "menu/fulltextdelete.gif' width='10' height='10'/></td></tr></table>").toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_9 = "</td><td style='width:60px;padding-left:5px' ><button id='BTNSRCH".toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_10 = ("' title='"+
														    JSPMessages.getString("MenuBuilder.23")+
														    "' tabindex = '").toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_11 = "' onclick='setExplorerFullTextGroup(\"".toCharArray();
    private static final char[] MNU_SEARCH_COMPONENT_12 = ("\",TEXTSEARCH.value )' style='height:20px;width:60px;'>"+
    														JSPMessages.getString("MenuBuilder.42")+
    														"</button></td>").toCharArray();
    private static final char[] MNU_EXPERT_SEARCH_COMPONENT_1 = "<td style='width:62px;padding-left:5px' ><button tabIndex='".toCharArray();
    private static final char[] MNU_EXPERT_SEARCH_COMPONENT_2 = ("' title='"+
    															JSPMessages.getString("MenuBuilder.46")+
    															"' style='height:20px;width:62px;'").toCharArray();
    private static final char[] MNU_EXPERT_SEARCH_COMPONENT_2_1 = "class='filterSet'".toCharArray();
    private static final char[] MNU_EXPERT_SEARCH_COMPONENT_3 = " onclick=\"winmain().openDocUrl(',800,580','__queryBuilder.jsp','?".toCharArray();
    private static final char[] MNU_EXPERT_SEARCH_COMPONENT_4 = "docid='+getDocId()+'&explorer=true&relatedIDX='+getIDX()+'&object=".toCharArray();
    private static final char[] MNU_EXPERT_SEARCH_COMPONENT_4_1 = "&referenceFrame='+getReferenceFrame()+'&reference=".toCharArray();
    private static final char[] MNU_EXPERT_SEARCH_COMPONENT_5 = ("','lookup')\">"+
    																JSPMessages.getString("MenuBuilder.53")+
    																"</button></td>").toCharArray();
    private static final char[] MNU_FILTER_COMPONENT_1 = "<td style='width:130px' ><span style=''>".toCharArray();
    private static final char[] MNU_FILTER_COMPONENT_2 = "</td></span>".toCharArray();
    private static final char[] MNU_EMPTY_COLUMN = "<td style=\"width:100%\">&nbsp;</td>".toCharArray();
    private static final char[] MNU_TABLE_SP0_PAD0 = "<table cellSpacing=\"0\" cellPadding=\"0\">".toCharArray();
    private static final char[] MNU_SUBM_GIF_CLASS = "class=\"OPENGIF\">".toCharArray();
    private static final char[] MNU_XML_FILTER = "&xmlFilter=".toCharArray();
    private static final int[] MNU_FUNTIONS = {FunctionBuilder.FUNC_START_FILTER};
    private static final int[] MNU_SCRIPTS = {};
    private static final int[] MNU_IMPORTS = {
        ImportBuilder.IMP_CSS_UI_GLOBAL, ImportBuilder.IMP_CSS_MENU,
        ImportBuilder.IMP_CSS_UI_MENU
    };
    private static final char[] MNU_NAME = "name=\"".toCharArray();
    private static final char[] MNU_ID = "id=\"".toCharArray();


    private static final char[] MNU_BOQL_INIT_COMPONENT_1 = "<td style='width:62px;padding-left:5px' ><button tabIndex='".toCharArray();
    private static final char[] MNU_BOQL_INIT_COMPONENT_2 = ("' title='"+
    															JSPMessages.getString("MenuBuilder.65")+
    															"' style='height:20px;width:62px;'").toCharArray();
    private static final char[] MNU_BOQL_INIT_COMPONENT_2_1 = "class='filterSet'".toCharArray();
    private static final char[] MNU_BOQL_INIT_COMPONENT_7 = " onclick=\"startInitialFilter(".toCharArray();
    private static final char[] MNU_BOQL_INIT_COMPONENT_7_1 = ");".toCharArray();
    private static final char[] MNU_BOQL_INIT_COMPONENT_7_2 = ("\">"+JSPMessages.getString("MenuBuilder.71")+"</button></td>").toCharArray();
    
    private static final char[] MNU_BOQL_INIT_CMPN_1 = "<TD tabindex=\"".toCharArray(); 
    private static final char[] MNU_BOQL_INIT_CMPN_2 = ("\" title=\""+
    														JSPMessages.getString("MenuBuilder.75")+
    														"\" class=\"mnuOption\"").toCharArray();
    private static final char[] MNU_BOQL_INIT_CMPN_7 = " onclick=\"startInitialFilter(".toCharArray();
    private static final char[] MNU_BOQL_INIT_CMPNT_7_1 = ");".toCharArray();
    private static final char[] MNU_BOQL_INIT_CMPN_7_2 = "\"><img class=\"buttonMenu\" src=\"templates/form/std/search16.gif\" WIDTH=\"16\" HEIGHT=\"16\"></td>".toCharArray();
    
    
//    private static final char[] MNU_BOQL_INIT_COMPONENT_3 = " onclick=\"winmain().openDocUrl(',800,580','".toCharArray();
//    private static final char[] MNU_BOQL_INIT_COMPONENT_4 = "','?explorerKey=".toCharArray();
//    private static final char[] MNU_BOQL_INIT_COMPONENT_5 = "&docid='+getDocId()+'&relatedIDX='+getIDX()+'&referenceFrame='+getReferenceFrame()".toCharArray();
//    private static final char[] MNU_BOQL_INIT_COMPONENT_6 = ",'lookup')\">Filtro Inicial</button></td>".toCharArray();

    /**
     *
     * @Company Enlace3
     * @since
     */
    private MenuBuilder() {
    }

    public static final void writeMenu(PrintWriter out, Menu menu,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        menu.rearrangeMenu();
        menu.setOnClickParameters("idx", String.valueOf(doc.getDocIdx()));

        if(menu.getImportCode() != null)
        {
            out.write(menu.getImportCode().toCharArray());
        }
        else if (ImportBuilder.canWrite(MNU_IMPORTS, control)) {
            out.write(HTMLCommon.HTML_STYLE_TEXT_CSS_BEGIN);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            ImportBuilder.writeImport(out, MNU_IMPORTS, control);
            out.write(HTMLCommon.HTML_STYLE_END);
            out.write(HTMLCommon.UTIL_NEW_LINE);
        }

        ScriptBuilder.writeScript(out, MNU_SCRIPTS, control);
        out.write(HTMLCommon.UTIL_NEW_LINE);

        if (FunctionBuilder.canWrite(MNU_FUNTIONS, control)) {
            out.write(HTMLCommon.HTML_SCRIPT_BEGIN);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            FunctionBuilder.writeFunction(out, MNU_FUNTIONS, control);
            out.write(HTMLCommon.HTML_SCRIPT_END);
            out.write(HTMLCommon.UTIL_NEW_LINE);
        }

        writeMenu(out, menu, "menu", !menu.isHorizontalMenu(), doc, docList, control, menu.getParameters());
    }

    private static final void writeMenu(PrintWriter out, Menu mnu,
        String mnuName, boolean subMenu, docHTML doc, docHTML_controler docList, 
        PageController control, Hashtable parameters) throws IOException, boRuntimeException {
        ArrayList menusItens = mnu.getMenuItems();
        out.write(HTMLCommon.UTIL_NEW_LINE);
        out.write(HTMLCommon.HTML_SPAN_BEGIN_NOT_CLOSE);
        if(mnu.getMenuID() != null)
        {
            out.write(HTMLCommon.WORD_ID);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(mnu.getMenuID().toCharArray());
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
        }
        if(mnu.getMenuID() != null)
        {
            out.write(HTMLCommon.WORD_NAME);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(mnu.getMenuName().toCharArray());
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
        }
        out.write(HTMLCommon.WORD_CLASS);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        
        
        if ( mnu.getMenuStyle() == null )
        {
            if (subMenu)
            {
                out.write(MNU_MNULIST_MENU);
            } else {
                out.write(MNU_MNUBAR_MENU);
            }
        }
        else
        {
            out.write(mnu.getMenuStyle().toCharArray());
            out.write( MNU_WMENU );
        }

        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.UTIL_WHITE_SPACE);
        out.write(HTMLCommon.WORD_COLOROPTION);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);

        if (subMenu) {
            out.write(MNU_LIST);
        } else {
            out.write(MNU_BAR);
        }

        out.write(HTMLCommon.SYMBOL_QUOTE);

        if (subMenu) {
            out.write(HTMLCommon.UTIL_WHITE_SPACE);
            out.write(HTMLCommon.WORD_ID);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_PLICA);
            out.write(mnuName.toCharArray());
            out.write(HTMLCommon.SYMBOL_PLICA);
        }

        out.write(HTMLCommon.SYMBOL_GT);

        out.write(HTMLCommon.UTIL_NEW_LINE);
        out.write(HTMLCommon.HTML_TABLE_BEGIN_NOT_CLOSE);
        if ( mnu.getMenuStyle() != null )
        {
            out.write( HTMLCommon.UTIL_WHITE_SPACE );
            out.write( WORD_CLASS );
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_PLICA);
            out.write(mnu.getMenuStyle().toCharArray());
            out.write(HTMLCommon.SYMBOL_PLICA);
            out.write( HTMLCommon.UTIL_WHITE_SPACE );
        }
        out.write(HTMLCommon.WORD_CELLPADDING);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        if ( subMenu ) out.write("3".toCharArray());
        else out.write("0".toCharArray());
        
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.UTIL_WHITE_SPACE);
        out.write(HTMLCommon.WORD_CELLSPACING);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write("0".toCharArray());
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.SYMBOL_GT);

        out.write(HTMLCommon.UTIL_NEW_LINE);

        if (subMenu) {
            out.write(MNU_LIST_COL);
            out.write(HTMLCommon.UTIL_NEW_LINE);
        }

        out.write(HTMLCommon.HTML_TBODY_BEGIN);

        if (!subMenu) {
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN_NOT_CLOSE);
            out.write(HTMLCommon.WORD_HEIGHT);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            
            out.write(mnu.getMenuHeight().toCharArray());
            
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.SYMBOL_GT);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN_NOT_CLOSE);
            out.write(HTMLCommon.WORD_HEIGHT);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(MNU_NINE);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(" onclick='setDefaultExplorer();'".toCharArray());
            out.write(HTMLCommon.SYMBOL_GT);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(MNU_VSPACE);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        }

        MenuItem item;
        MenuAdHocItem itemAdHoc;
        String subMenuName = mnuName + "";

        for (int i = 0; i < menusItens.size(); i++) {
            subMenuName = null;
            if(menusItens.get(i) instanceof MenuItem)
            {
                item = (MenuItem) menusItens.get(i);

                if (item.getSubMenuSize() > 0) {
                    subMenuName = mnuName + "_" + (i + 1);
                }
                out.write(HTMLCommon.UTIL_NEW_LINE);

                if (!subMenu) {
                    writeHorizontalMenuItem(mnu, item, out, subMenuName, doc, docList,control, parameters);
                } else {
                    writeVerticalMenuItem(item, out, subMenuName, parameters);
                }
    
                out.write(HTMLCommon.UTIL_NEW_LINE);
            }
            else
            {
                itemAdHoc = (MenuAdHocItem)menusItens.get(i);
                writeMenuAdHocItem(itemAdHoc, out);
            }
        }

        if (!subMenu) {
            out.write(MNU_EMPTY_COLUMN);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(HTMLCommon.HTML_TABLE_LINE_END);
        }

        out.write(HTMLCommon.HTML_TBODY_END);
        out.write(HTMLCommon.UTIL_NEW_LINE);
        out.write(HTMLCommon.HTML_TABLE_END);
        out.write(HTMLCommon.UTIL_NEW_LINE);
        out.write(HTMLCommon.HTML_SPAN_END);

        for (int i = 0; i < menusItens.size(); i++) 
        {
            subMenuName = null;
            if(menusItens.get(i) instanceof MenuItem)
            {
                item = (MenuItem) menusItens.get(i);
    
                if ((item.getSubMenuSize() > 0) && item.isVisible() &&
                        !item.isDisabled()) {
                    subMenuName = mnuName + "_" + (i + 1);
                    writeMenu(out, item.getSubMenu(), subMenuName, true, doc, docList, control, parameters);
                }
            }
        }
    }

    private static final void writeMenuAdHocItem(MenuAdHocItem item,
        PrintWriter out) throws IOException
    {
        out.write(item.getCode().toCharArray());
    }
    
    private static final void writeVerticalMenuItem(MenuItem item,
        PrintWriter out, String subMnuName, Hashtable param) throws IOException {
        if (item.isSearchComponent() || item.isExpertSearchComponent() ||
                item.isVerticalLine()) {
            return;
        } else if (item.isHorizontalLine()) {
            if (item.isVisible()) {
                out.write(MNU_HSPACER);
            }
        } else if (item.isVisible()) {
                out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN_NOT_CLOSE);
                out.write(HTMLCommon.WORD_TABINDEX);
                out.write(HTMLCommon.SYMBOL_EQUAL);
                out.write(HTMLCommon.SYMBOL_QUOTE);
                out.write(String.valueOf(item.getTabIndex()).toCharArray());
                out.write(HTMLCommon.SYMBOL_QUOTE);

                if (item.getAccessKey() != ' ') {
                    out.write(HTMLCommon.UTIL_WHITE_SPACE);
                    out.write(HTMLCommon.WORD_ACCESSKEY);
                    out.write(HTMLCommon.SYMBOL_EQUAL);
                    out.write(HTMLCommon.SYMBOL_QUOTE);
                    out.write(String.valueOf(item.getAccessKey()).toCharArray());
                    out.write(HTMLCommon.SYMBOL_QUOTE);
                }

                if (item.getTitle() != null) {
                    out.write(HTMLCommon.UTIL_WHITE_SPACE);
                    out.write(HTMLCommon.WORD_TITLE);
                    out.write(HTMLCommon.SYMBOL_EQUAL);
                    out.write(HTMLCommon.SYMBOL_QUOTE);
                    out.write(String.valueOf(item.getTitle()).toCharArray());
                    out.write(HTMLCommon.SYMBOL_QUOTE);
                }

                out.write(HTMLCommon.UTIL_WHITE_SPACE);
                out.write(MNU_OPTION);

                if (item.getSubMenuSize() > 0) {
                    out.write(HTMLCommon.UTIL_WHITE_SPACE);
                    out.write(MNU_MENU);
                    out.write(subMnuName.toCharArray());
                    out.write(HTMLCommon.SYMBOL_QUOTE);
                }

                out.write(HTMLCommon.UTIL_WHITE_SPACE);

                if (item.isDisabled()) {
                    out.write(HTMLCommon.WORD_DISABLED);
                }

                out.write(HTMLCommon.SYMBOL_GT);
                out.write(HTMLCommon.UTIL_NEW_LINE);

                if (item.getCheckName() != null) {
                    //<td><input type='checkBox' / id='checkBox'1 name='checkBox'1></td>
                    out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
                    out.write(HTMLCommon.HTML_INPUT_NOT_CLOSE);
                    out.write(HTMLCommon.WORD_TYPE);
                    out.write(HTMLCommon.SYMBOL_EQUAL);
                    out.write(HTMLCommon.SYMBOL_PLICA);
                    out.write(HTMLCommon.WORD_CHECKBOX);
                    out.write(HTMLCommon.SYMBOL_PLICA);
                    out.write(HTMLCommon.UTIL_WHITE_SPACE);
                    out.write(HTMLCommon.WORD_ID);
                    out.write(HTMLCommon.SYMBOL_EQUAL);
                    out.write(HTMLCommon.SYMBOL_PLICA);
                    out.write(item.getCheckId().toCharArray());
                    out.write(HTMLCommon.SYMBOL_PLICA);
                    out.write(HTMLCommon.UTIL_WHITE_SPACE);
                    out.write(HTMLCommon.WORD_NAME);
                    out.write(HTMLCommon.SYMBOL_EQUAL);
                    out.write(HTMLCommon.SYMBOL_PLICA);
                    out.write(item.getCheckName().toCharArray());
                    out.write(HTMLCommon.SYMBOL_PLICA);
                    out.write(HTMLCommon.SYMBOL_GT);
                    out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
                } else if (item.getImgUrl() != null) {
                    out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
                    out.write(MNU_IMG_START);
                    out.write(item.getImgUrl().toCharArray());
                    out.write(MNU_IMG_END);
                    out.write(HTMLCommon.HTML_END_EMPTY_ELEMENT);
                    out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
                } else {
                    out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
                    out.write(HTMLCommon.HTML_BLANK_SPACE);
                    out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
                }

                out.write(HTMLCommon.UTIL_NEW_LINE);

                //<td class="mnuItm">Contents</td>
                out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN_NOT_CLOSE);

                String code = null;

                if ((code = item.getOnClickTotalCode(param)) != null) {
                    out.write(HTMLCommon.UTIL_WHITE_SPACE);
                    out.write(HTMLCommon.WORD_ONCLICK);
                    out.write(HTMLCommon.SYMBOL_EQUAL);
                    out.write(HTMLCommon.SYMBOL_QUOTE);
                    out.write(code.toCharArray());
                    out.write(HTMLCommon.SYMBOL_QUOTE);
                }

                out.write(HTMLCommon.SYMBOL_GT);
                if(item.getCode() == null)
                {
                    out.write(item.getLabel().toCharArray());
                }
                else
                {
                    out.write(item.getCode().toCharArray());
                }
                out.write(HTMLCommon.UTIL_NEW_LINE);
                out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
                out.write(HTMLCommon.UTIL_NEW_LINE);
                out.write(HTMLCommon.HTML_TABLE_LINE_END);            
        }
    }

    private static final void writeHorizontalMenuItem(Menu mnu, MenuItem item,
        PrintWriter out, String subMnuName, 
        docHTML doc, docHTML_controler docList, PageController control, Hashtable param)
        throws IOException, boRuntimeException {
        if (!item.isHorizontalLine() && item.isVisible()) {
            if (item.isVerticalLine()) {
                out.write(MNU_VSPACER);
            }
            else if( item.isCustomRenderer() ) {
                item.renderComponent( mnu, item, out, doc, docList, control, param );
            } else if (item.isSearchComponent()) {
                if (item.isVisible()) {
//                    if (item.getCode() == null) {
                        out.write(MNU_SEARCH_COMPONENT_0);
                        out.write(MNU_SEARCH_COMPONENT_1);
                        out.write(MNU_SEARCH_COMPONENT_2);
                        out.write(MNU_SEARCH_COMPONENT_3);
                        if(item.getSearchText() != null)
                        {
                           out.write(item.getSearchText().toCharArray()); 
                        }
                        out.write(MNU_SEARCH_COMPONENT_3_1);
                        char[] key = item.getSearchComponentKey().toCharArray();
                        out.write(key);
                        out.write(MNU_SEARCH_COMPONENT_4);
                        out.write(key);
                        out.write(MNU_SEARCH_COMPONENT_5);
                        out.write(String.valueOf(item.getTabIndex()).toCharArray());
                        out.write(MNU_SEARCH_COMPONENT_6);
                        out.write(MNU_SEARCH_COMPONENT_7);
                        out.write(key);
                        out.write(MNU_SEARCH_COMPONENT_8);
                        out.write(MNU_SEARCH_COMPONENT_9);
                        out.write(key);
                        out.write(MNU_SEARCH_COMPONENT_10);
                        out.write(String.valueOf(item.getTabIndex()).toCharArray());
                        out.write(MNU_SEARCH_COMPONENT_11);
                        out.write(key);
                        out.write(MNU_SEARCH_COMPONENT_12);
//                    } else {
//                        out.write(item.getCode().toCharArray());
//                    }
                }
            } else if (item.isExpertSearchComponent()) {
                if (item.isVisible()) {
//                    if (item.getCode() == null) {
                        out.write(MNU_EXPERT_SEARCH_COMPONENT_1);
                        out.write(String.valueOf(item.getTabIndex()).toCharArray());
                        out.write(MNU_EXPERT_SEARCH_COMPONENT_2);
                        if(mnu.getTextUserQuery() != null && !"".equals(mnu.getTextUserQuery()))
                        {
                            out.write(MNU_EXPERT_SEARCH_COMPONENT_2_1);
                        }
//                        out.write(HTMLCommon.SYMBOL_PLICA);
                        out.write(MNU_EXPERT_SEARCH_COMPONENT_3);
                        out.write(MNU_EXPERT_SEARCH_COMPONENT_4);
                        out.write(item.getSearchObjName().toCharArray());
                        out.write(MNU_EXPERT_SEARCH_COMPONENT_4_1);

                        char[] key = item.getSearchComponentKey().toCharArray();
                        out.write(key);
                        if(mnu.getTextUserQuery() != null && !"".equals(mnu.getTextUserQuery()))
                        {
                            out.write(MNU_XML_FILTER);
                            
                            try
                            {
                                out.write( (java.net.URLEncoder.encode(mnu.getTextUserQuery(), boConfig.getEncoding()) ).toCharArray());
                            }
                            catch( Exception ex )
                            {
                                System.out.println( ex );
                                out.write((mnu.getTextUserQuery()).toCharArray());
                            }
                            
                        }
                        out.write(MNU_EXPERT_SEARCH_COMPONENT_5);
//                    } else {
//                        out.write(item.getCode().toCharArray());
//                    }
                }
            } else if (item.isFilterComponent()) {
                if (item.isVisible()) {
//                    if (item.getCode() == null) {
//                        out.write(MNU_FILTER_COMPONENT_1);
                        item.writeFilterCode(out, doc, docList, control);
//                        out.write(item.getBytesFilterCode());
//                        out.write(MNU_FILTER_COMPONENT_2);
//                    } else {
//                        out.write(item.getCode().toCharArray());
//                    }
                }
            } else if (item.isSvExplorerComponent()) {
                if (item.isVisible()) {
//                    if (item.getCode() == null) {
//                        out.write(MNU_FILTER_COMPONENT_1);
                        item.writeSvExplorerCode(out, doc, docList, control);
//                        out.write(item.getBytesFilterCode());
//                        out.write(MNU_FILTER_COMPONENT_2);
//                    } else {
//                        out.write(item.getCode().toCharArray());
//                    }
                }
            } else if (item.isBoqlInitJSPComponent()) {
                if (item.isVisible()) {
//                        out.write(MNU_BOQL_INIT_COMPONENT_1);
//                        out.write(String.valueOf(item.getTabIndex()).toCharArray());
//                        out.write(MNU_BOQL_INIT_COMPONENT_2);
//                        if(mnu.isBoqlInitSet())
//                        {
//                            out.write(MNU_BOQL_INIT_COMPONENT_2_1);
//                        }
//                          out.write(MNU_BOQL_INIT_COMPONENT_7);  
//                          out.write(HTMLCommon.SYMBOL_PLICA);
//                          out.write(item.getBoqlInitJSPComponentJSP().toCharArray());
//                          out.write(HTMLCommon.SYMBOL_PLICA);
//                          out.write(HTMLCommon.SYMBOL_SLASH);
//                          out.write(HTMLCommon.SYMBOL_PLICA);
//                          out.write(item.getBoqlInitJSPComponentKey().toCharArray());
//                          out.write(HTMLCommon.SYMBOL_PLICA);
//                          out.write(MNU_BOQL_INIT_COMPONENT_7_1);
//                          out.write(MNU_BOQL_INIT_COMPONENT_7_2);

                        out.write(MNU_BOQL_INIT_CMPN_1);
                        out.write(String.valueOf(item.getTabIndex()).toCharArray());
                        out.write(MNU_BOQL_INIT_CMPN_2);
                        if(mnu.isBoqlInitSet())
                        {
//                            out.write(MNU_BOQL_INIT_COMPONENT_2_1);
                        }
                          out.write(MNU_BOQL_INIT_CMPN_7);  
                          out.write(HTMLCommon.SYMBOL_PLICA);
                          out.write(item.getBoqlInitJSPComponentJSP().toCharArray());
                          out.write(HTMLCommon.SYMBOL_PLICA);
                          out.write(HTMLCommon.SYMBOL_SLASH);
                          out.write(HTMLCommon.SYMBOL_PLICA);
                          out.write(item.getBoqlInitJSPComponentKey().toCharArray());
                          out.write(HTMLCommon.SYMBOL_PLICA);
                          out.write(MNU_BOQL_INIT_CMPNT_7_1);
                          out.write(MNU_BOQL_INIT_CMPN_7_2);

                }
            } else {
//                if (item.getCode() == null) {
                    out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN_NOT_CLOSE);
                    out.write(HTMLCommon.WORD_TABINDEX);
                    out.write(HTMLCommon.SYMBOL_EQUAL);
                    out.write(HTMLCommon.SYMBOL_QUOTE);
                    out.write(String.valueOf(item.getTabIndex()).toCharArray());
                    out.write(HTMLCommon.SYMBOL_QUOTE);

                    if (item.getAccessKey() != ' ') {
                        out.write(HTMLCommon.UTIL_WHITE_SPACE);
                        out.write(HTMLCommon.WORD_ACCESSKEY);
                        out.write(HTMLCommon.SYMBOL_EQUAL);
                        out.write(HTMLCommon.SYMBOL_QUOTE);
                        out.write(String.valueOf(item.getAccessKey()).toCharArray());
                        out.write(HTMLCommon.SYMBOL_QUOTE);
                    }

                    if (item.getTitle() != null) {
                        out.write(HTMLCommon.UTIL_WHITE_SPACE);
                        out.write(HTMLCommon.WORD_TITLE);
                        out.write(HTMLCommon.SYMBOL_EQUAL);
                        out.write(HTMLCommon.SYMBOL_QUOTE);
                        out.write(String.valueOf(item.getTitle()).toCharArray());
                        out.write(HTMLCommon.SYMBOL_QUOTE);
                    }

                    out.write(HTMLCommon.UTIL_WHITE_SPACE);
                    out.write(MNU_OPTION);

                    if (item.getSubMenuSize() > 0) {
                        out.write(HTMLCommon.UTIL_WHITE_SPACE);
                        out.write(MNU_MENU);
                        out.write(subMnuName.toCharArray());
                        out.write(HTMLCommon.SYMBOL_QUOTE);
                    }

                    out.write(HTMLCommon.UTIL_WHITE_SPACE);

                    if (item.isDisabled()) {
                        out.write(HTMLCommon.WORD_DISABLED);
                        out.write(HTMLCommon.UTIL_WHITE_SPACE);
                    }

                    String code = null;

                    if ((code = item.getOnClickTotalCode(param)) != null) {
                        out.write(HTMLCommon.WORD_ONCLICK);
                        out.write(HTMLCommon.SYMBOL_EQUAL);
                        out.write(HTMLCommon.SYMBOL_QUOTE);
                        out.write(code.toCharArray());
                        out.write(HTMLCommon.SYMBOL_QUOTE);
                    }

                    out.write(HTMLCommon.SYMBOL_GT);
                    out.write(HTMLCommon.UTIL_NEW_LINE);

                    if (item.getSubMenuSize() == 0) {
                        if (item.getImgUrl() != null) {
                            out.write(MNU_IMG_START);
                            out.write(item.getImgUrl().toCharArray());
                            out.write(MNU_IMG_END);
                            out.write(HTMLCommon.SYMBOL_GT);
                        }
                        if(item.getCode() != null)
                        {
                            out.write(item.getCode().toCharArray());
                        }
                        else if (item.getLabel() != null) {
                            out.write(item.getLabel().toCharArray());
                        }
                    } else {
                        out.write(MNU_TABLE_SP0_PAD0);
                        out.write(HTMLCommon.UTIL_NEW_LINE);
                        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
                        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN_NOT_CLOSE);
                        out.write(HTMLCommon.UTIL_WHITE_SPACE);
                        out.write(MNU_MENU);
                        out.write(subMnuName.toCharArray());
                        out.write(HTMLCommon.SYMBOL_QUOTE);
                        out.write(HTMLCommon.SYMBOL_GT);
                        out.write(HTMLCommon.UTIL_NEW_LINE);
                        out.write(MNU_IMG_START);
                        out.write(item.getImgUrl().toCharArray());
                        out.write(MNU_IMG_END);
                        out.write(HTMLCommon.SYMBOL_GT);
                        if(item.getCode() != null)
                        {
                            out.write(item.getCode().toCharArray());
                        }
                        else if (item.getLabel() != null) 
                        {
                            out.write(item.getLabel().toCharArray());
                        }

                        out.write(HTMLCommon.UTIL_NEW_LINE);
                        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
                        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN_NOT_CLOSE);
                        out.write(MNU_SUBM_GIF_CLASS);
//                        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
                        out.write(HTMLCommon.HTML_BLANK_SPACE);
                        out.write(HTMLCommon.HTML_BLANK_SPACE);
                        out.write(MenuItem.getSubMenuIMG());
                        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
                        out.write(HTMLCommon.HTML_TABLE_LINE_END);
                        out.write(HTMLCommon.UTIL_NEW_LINE);
                        out.write(HTMLCommon.HTML_TABLE_END);
                    }

                    out.write(HTMLCommon.UTIL_NEW_LINE);
                    out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
//                } else {
//                    out.write(item.getCode().toCharArray());
//                }
            }
        }
    }
}
