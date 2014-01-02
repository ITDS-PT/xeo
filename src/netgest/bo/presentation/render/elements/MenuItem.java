/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.Hashtable;

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
public class MenuItem implements Element {
    public static final String BUTTON_MENU_1 = "<img class=\"buttonMenu\" src=\"";
    public static final String BUTTON_MENU_2 = "\" WIDTH=\"6\" HEIGHT=\"3\">";
    public static final String MENU_VERT = "menu/menuVert.gif";
    public static final String PV = ";";
    private String imgURL = null;
    private String label = null;
    private String id = null;
    private String name = null;
    private String beforeRunCode = null;
    private String onclickCode = null;
    private String afterRunCode = null;
    private String chkId = null;
    private String chkName = null;
    private String title = null;
    private int tabIndex = 0;
    private Menu subMenu = null;
    private boolean checked = false;
    private boolean visible = true;
    private boolean disabled = false;
    private boolean vLine = false;
    private boolean hLine = false;
    private boolean searchComponent = false;
    private boolean customRenderer = false;
    private String searchComponentKey = null;
    private boolean expertSearchComponent = false;
    private boolean filterComponent = false;
    private StringBuffer filterCode = null;
    private boolean svExpComponent = false;
    private StringBuffer svExpCode = null;
    private Filter filter = null;
    private SavedExplorers svExpl = null;
    private Character accessKey = null;
    private String code = null;
    private String searchString = null;
    private String searchObjectName = null;
//boqlInitJsp JSP que inicia o boql do explorador
    private boolean boqlInitComponent = false;
    private String boqlInitJsp = null;
    private String boqlInitKey = null;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public MenuItem() {
    }

    public MenuItem(String code) {
        this.code = code;
    }

    public MenuItem(String id, String name, String label, String imgURL,
        String onclickCode) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.imgURL = imgURL;
        this.onclickCode = onclickCode;
    }

    public static MenuItem getHorizontalLineSeparator() {
        MenuItem line = new MenuItem();
        line.hLine = true;

        return line;
    }

    public static MenuItem getVerticalLineSeparator() {
        MenuItem line = new MenuItem();
        line.vLine = true;

        return line;
    }

    public static MenuItem getSearchComponent(String key) {
        MenuItem sear = new MenuItem();
        sear.searchComponent = true;
        sear.searchComponentKey = key;

        return sear;
    }

    public static MenuItem getExpertSearchComponent(String key, String searchObjectName) {
        MenuItem sear = new MenuItem();
        sear.expertSearchComponent = true;
        sear.searchComponentKey = key;
        sear.searchObjectName = searchObjectName;

        return sear;
    }
    public String getSearchObjName()
    {
        return searchObjectName;
    }

    public static MenuItem getFilterComponent(StringBuffer htmlCode) {
        MenuItem filter = new MenuItem();
        filter.filterComponent = true;
        filter.filterCode = htmlCode;

        return filter;
    }
    public static MenuItem getFilterComponent(Filter f) {
        MenuItem filter = new MenuItem();
        filter.filterComponent = true;
        filter.filter = f;
        return filter;
    }

    public static MenuItem getSavedExplorersComponent(StringBuffer htmlCode) {
        MenuItem svExpl = new MenuItem();
        svExpl.svExpComponent = true;
        svExpl.svExpCode = htmlCode;

        return svExpl;
    }
    public static MenuItem getSavedExplorersComponent(SavedExplorers s) {
        MenuItem svExpl = new MenuItem();
        svExpl.svExpComponent = true;
        svExpl.svExpl = s;
        return svExpl;
    }

    public static MenuItem getBoqlInitJspComponent(String key, String jspName) {
        MenuItem boqlInit = new MenuItem();
        boqlInit.boqlInitComponent = true;
        boqlInit.boqlInitJsp = jspName;
        boqlInit.boqlInitKey = key;
        return boqlInit;
    }

    public void setCustomRenderer( boolean customRenderer ) {
        this.customRenderer = customRenderer;
    }

    public boolean isCustomRenderer() {
        return this.customRenderer;
    }
    
    public void renderComponent(Menu mnu, MenuItem item,
        PrintWriter out, docHTML doc, docHTML_controler docList, PageController control, Hashtable param) {
        
    }
    
 
    //Métodos set's
    public void setId(String value) {
        if (isLine()) {
            return;
        }

        this.id = value;

        if (this.name == null) {
            this.name = value;
        }
    }

    public void setName(String value) {
        if (isLine()) {
            return;
        }

        this.name = value;

        if (this.id == null) {
            this.id = value;
        }
    }

    public void setLabel(String value) {
        if (isLine()) {
            return;
        }

        this.label = value;
    }

    public void setTitle(String value) {
        if (isLine()) {
            return;
        }

        this.title = value;
    }

    public void setAccessKey(char value) {
        if (isLine()) {
            return;
        }

        this.accessKey = new Character(value);
    }

    public void setImgURL(String value) {
        if (isLine()) {
            return;
        }

        this.imgURL = value;
    }

    public void setBeforeRunCode(String value) {
        if (isLine()) {
            return;
        }

        this.beforeRunCode = value;
    }

    public void setAfterRunCode(String value) {
        if (isLine()) {
            return;
        }

        this.afterRunCode = value;
    }

    public void setOnClickCode(String value) {
        this.onclickCode = value;
    }

    public void setTabIndex(int value) {
        if (isLine()) {
            return;
        }

        this.tabIndex = value;
    }

    public void addSubMenu(Menu list) {
        if (isLine()) {
            return;
        }

        this.subMenu = list;
    }

    public void setCheckFieldName(String value) {
        if (isLine()) {
            return;
        }

        this.chkName = value;

        if (this.chkId == null) {
            this.chkId = value;
        }
    }

    public void setCheckFieldId(String value) {
        if (isLine()) {
            return;
        }

        this.chkId = value;

        if (this.chkName == null) {
            this.chkName = value;
        }
    }

    public void setChecked(boolean value) {
        if (isLine()) {
            return;
        }

        this.checked = value;
    }

    public void setVisible(boolean value) {
        this.visible = value;
    }

    public void setDisabled(boolean value) {
        if (isLine()) {
            return;
        }

        this.disabled = value;
    }

    public void addSubMenuItem(MenuItem item) {
        if (isLine()) {
            return;
        }

        if (subMenu == null) {
            subMenu = new Menu();
        }

        subMenu.addMenuItem(item);
    }

    //Métodos get's
    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTitle() {
        return this.title;
    }

    public String getLabel() {
        if(label != null)
        {
           label = label.replaceAll(" ", "&nbsp;");
        }
        return this.label;
    }

    public char getAccessKey() {
        if (accessKey != null) {
            return accessKey.charValue();
        }

        return ' ';
    }

    public String getImgUrl() {
        return this.imgURL;
    }

    public String getBeforeRunCode() {
        return this.beforeRunCode;
    }

    public String getAfterRunCode() {
        return this.afterRunCode;
    }

    public String getOnClickCode() {
        return this.onclickCode;
    }

    public String getOnClickCode(Hashtable parameters)
    {
        String toRet = new String(onclickCode);
        if(parameters != null)
        {
            Enumeration oEnum = parameters.keys();
            String key;

            while(oEnum.hasMoreElements())
            {
                key = (String)oEnum.nextElement();
                String value = (String)parameters.get(key);
                toRet = toRet.replaceAll("\\$" + key, value);
            }
        }
        return toRet;
    }
    public int getTabIndex() {
        return this.tabIndex;
    }

    public Menu getSubMenu() {
        return this.subMenu;
    }

    public MenuItem getSubMenuItem(int index) {
        if ((subMenu != null) && (subMenu.size() > index)) {
            return subMenu.get(index);
        }

        return null;
    }

    public int getSubMenuSize() {
        return (subMenu == null) ? 0 : subMenu.size();
    }

    public boolean removeSubmenuItem(int index) {
        if ((subMenu != null) && (subMenu.size() > index)) {
            return (subMenu.remove(index) != null) ? true : false;
        }

        return false;
    }

    public boolean removeSubmenuItem(MenuItem item) {
        if (subMenu != null) {
            int pos = subMenu.indexOf(item);

            if (pos != -1) {
                return (subMenu.remove(pos) != null) ? true : false;
            }
        }

        return false;
    }

    public String getCheckName() {
        return this.chkName;
    }

    public String getCheckId() {
        return this.chkId;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public boolean isSearchComponent() {
        return this.searchComponent;
    }

    public String getSearchComponentKey() {
        return this.searchComponentKey;
    }

    public boolean isExpertSearchComponent() {
        return this.expertSearchComponent;
    }

    public String getExpertSearchComponentKey() {
        return this.searchComponentKey;
    }

    public boolean isBoqlInitJSPComponent() {
        return this.boqlInitComponent;
    }

    public String getBoqlInitJSPComponentKey() {
        return this.boqlInitKey;
    }

    public String getBoqlInitJSPComponentJSP() {
        return this.boqlInitJsp;
    }

    public boolean isLine() {
        return hLine || vLine;
    }

    public boolean isHorizontalLine() {
        return hLine;
    }

    public boolean isVerticalLine() {
        return vLine;
    }

    public boolean isFilterComponent() {
        return this.filterComponent;
    }
     public boolean isSvExplorerComponent() {
        return this.svExpComponent;
    }

    public char[] getCharFilterCode() {
        return filterCode.toString().toCharArray();
    }

     public char[] getCharSvExplorerCode() {
        return svExpCode.toString().toCharArray();
    }

    public void writeFilterCode(PrintWriter out, docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException
    {
        if(filterCode == null)
        {
            filter.writeHTML(out, doc, docList, control);
        }
        else
        {
            out.write(getCharFilterCode());
        }
    }

    public void writeSvExplorerCode(PrintWriter out, docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException
    {
        if(filterCode == null)
        {
            svExpl.writeHTML(out, doc, docList, control);
        }
        else
        {
            out.write(getCharFilterCode());
        }
    }

    public String getOnClickTotalCode(Hashtable parameters) {
        StringBuffer sb = new StringBuffer();

        if ((beforeRunCode != null) && (beforeRunCode.length() > 0)) {
            sb.append(beforeRunCode);

            if (!beforeRunCode.endsWith(PV)) {
                sb.append(PV);
            }
        }

        if ((onclickCode != null) && (onclickCode.length() > 0)) {
            sb.append(getOnClickCode(parameters));

            if (!onclickCode.endsWith(PV)) {
                sb.append(PV);
            }
        }

        if ((afterRunCode != null) && (afterRunCode.length() > 0)) {
            sb.append(afterRunCode);

            if (!afterRunCode.endsWith(PV)) {
                sb.append(PV);
            }
        }

        return (sb.length() == 0) ? null : sb.toString();
    }

    public String getCode() {
        return code;
    }
    public void setSearchText(String s)
    {
        searchString = s;
    }
    public String getSearchText()
    {
        return searchString;
    }
    //Items estáticos
    public static char[] getSubMenuIMG() throws boRuntimeException {
        StringBuffer sb = new StringBuffer(BUTTON_MENU_1);
        sb.append(Browser.getThemeDir()).append(MENU_VERT).append(BUTTON_MENU_2);

        return sb.toString().toCharArray();
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList,  PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writeMenuItem(out, this,docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getMenuItemHTML(this,docHTML, docList, control);
    }

    public void writeHTML(PrintWriter out, PageController control)
        throws IOException, boRuntimeException {
        writeHTML(out, null, null, control);
    }

    public String getHTML(PageController control)
        throws IOException, boRuntimeException {
        return getHTML(null, null, control);
    }

}
