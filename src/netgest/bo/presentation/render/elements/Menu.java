/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Hashtable;

import java.util.List;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefViewer;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.Browser;
import netgest.bo.presentation.render.HTMLBuilder;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.MenuAdHocItem;
import netgest.bo.presentation.render.elements.MenuItem;
import netgest.bo.presentation.render.elements.SavedExplorers;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.security.securityRights;
import netgest.bo.localized.JSPMessages;

import netgest.utils.ngtXMLHandler;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class Menu implements Element {
    private ArrayList menuItems = null;
    private String mnuName="menu";
    private String mnuID=null;
    private boolean horizontalMenu = true;
    private String importCode = null;
    private Hashtable parameters = new Hashtable();
    private String p_menuHeight ="24px";
    private String p_menuStyle = null;
    private String p_textUserQuery = null;
    private boolean p_boqlInitSet = false;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public Menu(ArrayList menuItems) {
        this.menuItems = menuItems;
    }

    public Menu(ArrayList menuItems, String name, String id) {
        this.menuItems = menuItems;
        this.mnuName = name;
        this.mnuID = id;
    }

    public Menu() {
        this.menuItems = new ArrayList();
    }

    public void setMenuName(String name)
    {
        this.mnuName = name;
    }
    public String getMenuName()
    {
        return this.mnuName;
    }
    public void setMenuID(String id)
    {
        this.mnuID = id;
    }
    public void setVerticalMenu(boolean b)
    {
        this.horizontalMenu = !b;
    }
    public boolean isHorizontalMenu()
    {
        return this.horizontalMenu;
    }
    public void setImportCode(String code)
    {
        this.importCode = code;
    }
    public String getImportCode()
    {
        return this.importCode;
    }
    public String getMenuID()
    {
        return this.mnuID;
    }
    public void addMenuItem(MenuItem menuItem) {
        if (menuItems == null) {
            menuItems = new ArrayList();
        }

        menuItems.add(menuItem);
    }
    public void addMenuItem(MenuAdHocItem menuItem) {
        if (menuItems == null) {
            menuItems = new ArrayList();
        }

        menuItems.add(menuItem);
    }
    public void setMenuStyle( String mnuStyle )
    {
        p_menuStyle = mnuStyle;
    }
    public void setTextUserQuery( String textUserQuery )
    {
        p_textUserQuery = textUserQuery;
    }
    public void setBoqlInitJsp( boolean value )
    {
        p_boqlInitSet = value;
    }
    public void setMenuHeight( String mnuHeight )
    {
        p_menuHeight = mnuHeight;
    }
    public String getMenuHeight( )
    {
        return p_menuHeight;
    }
    public String getMenuStyle()
    {
        return p_menuStyle;
    }
    public String getTextUserQuery()
    {
        return p_textUserQuery;
    }
    public boolean isBoqlInitSet()
    {
        return p_boqlInitSet;
    }
    public MenuItem getMenuItemById(String id) {
        if (menuItems == null) {
            return null;
        }

        MenuItem aux;

        for (int i = 0; i < menuItems.size(); i++) {
            aux = (MenuItem) menuItems.get(i);

            if (aux.getId() != null && aux.getId().equals(id)) {
                return aux;
            }
        }

        return null;
    }

    public MenuItem getMenuItemByName(String name) {
        if (menuItems == null) {
            return null;
        }

        MenuItem aux;

        for (int i = 0; i < menuItems.size(); i++) {
            aux = (MenuItem) menuItems.get(i);

            if (aux.getName() != null && aux.getName().equals(name)) {
                return aux;
            }
        }

        return null;
    }

    public MenuItem getTreeMenuItemByName(String name) {
        if (menuItems == null) {
            return null;
        }

        MenuItem aux = getMenuItemByName(name);

        if (aux != null) {
            return aux;
        }

        for (int i = 0; i < menuItems.size(); i++) {
            aux = (MenuItem) menuItems.get(i);

            if (aux.getSubMenu() != null) {
                aux = aux.getSubMenu().getTreeMenuItemByName(name);

                if (aux != null) {
                    return aux;
                }
            }
        }

        return null;
    }

    public MenuItem getTreeMenuItemById(String id) {
        if (menuItems == null) {
            return null;
        }

        MenuItem aux = getMenuItemById(id);

        if (aux != null) {
            return aux;
        }

        for (int i = 0; i < menuItems.size(); i++) {
            aux = (MenuItem) menuItems.get(i);

            if (aux.getSubMenu() != null) {
                aux = aux.getSubMenu().getTreeMenuItemById(id);

                if (aux != null) {
                    return aux;
                }
            }
        }

        return null;
    }

    public int size() {
        return (menuItems == null) ? 0 : menuItems.size();
    }

    public MenuItem get(int index) {
        return (MenuItem) menuItems.get(index);
    }

    public MenuItem remove(int index) {
        return (MenuItem) menuItems.remove(index);
    }

    public int indexOf(MenuItem item) {
        if (menuItems == null) {
            return -1;
        }

        return menuItems.indexOf(item);
    }

    public ArrayList getMenuItems() {
        return menuItems;
    }

    public void rearrangeMenu() {
        int first = -1;
        MenuItem item;
        boolean stop = false;

        for (int i = 0; (i < menuItems.size()) && !stop; i++) {
            if(menuItems.get(i) instanceof MenuItem)
            {
                if ((item = (MenuItem) menuItems.get(i)).isFilterComponent()) {
                    if ((first != -1) && (first == item.hashCode())) {
                        stop = true;
                    } else {
                        first = menuItems.get(i).hashCode();
                    }
    
                    if (!stop && ((i + 1) != menuItems.size())) {
                        menuItems.remove(i);
                        menuItems.add(item);
                        i--;
                    }
                }
            }
        }

        first = -1;
        stop = false;
        for (int i = 0; (i < menuItems.size()) && !stop; i++) {
            if(menuItems.get(i) instanceof MenuItem)
            {
                if ((item = (MenuItem) menuItems.get(i)).isSearchComponent()) {
                    if ((first != -1) && (first == item.hashCode())) {
                        stop = true;
                    } else {
                        first = menuItems.get(i).hashCode();
                    }
    
                    if (!stop && ((i + 1) != menuItems.size())) {
                        menuItems.remove(i);
                        menuItems.add(item);
                        i--;
                    }
                }
            }
       }

        first = -1;
        stop = false;
        
        for (int i = 0; (i < menuItems.size()) && !stop; i++) {
            if(menuItems.get(i) instanceof MenuItem)
            {
                if ((item = (MenuItem) menuItems.get(i)).isExpertSearchComponent()) {
                    if ((first != -1) && (first == item.hashCode())) {
                        stop = true;
                    } else {
                        first = menuItems.get(i).hashCode();
                    }
    
                    if (!stop && ((i + 1) != menuItems.size())) {
                        menuItems.remove(i);
                        menuItems.add(item);
                        i--;
                    }
                }
            }
        }
    }

    public static Menu getExplorerExtensionMenu(EboContext ctx, String treeName, String pageName, boDefHandler bodef, String idx, Filter filter, SavedExplorers svExp, ExplorerOptions options, String boqlInitJsp) throws boRuntimeException {
        Menu result = null;
        ArrayList toRet = new ArrayList();
        toRet.add(MenuItem.getSearchComponent(treeName));
        result = new Menu(toRet, "explorerMenuFor" + treeName, "explorerMenuFor"+treeName);
        result.setOnClickParameters("idx", idx);
        result.setOnClickParameters("treeName", treeName);
        result.setMenuHeight("24px");
        return result;
    }
    
    public static Menu getExplorerMenu(EboContext ctx, String treeName, String pageName, boDefHandler bodef, String idx, Filter filter, SavedExplorers svExp, ExplorerOptions options, String boqlInitJsp) throws boRuntimeException 
    {
        Menu result = null;
        if(options.isMenuOptionsVisible())
        {
            boolean writeSeparator = false;
            ArrayList toRet = new ArrayList();
            MenuItem aux = null; 
            
            // psantos ini
            // boDefHandler subs[] = bodef.getTreeSubClasses();
            boDefHandler[]  subs = options.getNewSubClassOptionObjects();
            if( subs == null )
            {
                subs = bodef.getTreeSubClasses();
            }
            // psantos fim

            
            if(!"message".equals(bodef.getName()) && 
               !"xwfActivity".equals(bodef.getName()) && 
               !"xwfActivity".equals(bodef.getBoSuperBo()) &&
               !"ResDocumentos".equals(bodef.getName()) && 
               !"ResDocumentos".equals(bodef.getBoSuperBo()) &&
               !"ACS_RuntimeAlert".equals(bodef.getName()) &&
               !"Ebo_History".equals(bodef.getName()) &&
               !"Ebo_CheckOut".equals(bodef.getName()) &&
               !"xwfProgramRuntime".equals(bodef.getName()) &&
               (bodef.getInterfaceType() != boDefHandler.INTERFACE_STANDARD ) && 
               securityRights.canWrite(ctx, bodef.getName()) && 
               bodef.getBoCanBeOrphan()
            )
            {
                if(subs.length > 0)
                {
                    if(options.isNewOptionVisible())
                    {
                        aux = new MenuItem();
                        aux.setId("buttonNew");
                        aux.setImgURL(bodef.getSrcForIcon16());
                        aux.setLabel(JSPMessages.getString("Menu.12"));
                        // psantos ini
                        // aux.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;object=" + bodef.getName()+"')");
                        // se no xml existe o elemento newSubClassOption
                        if (options.isObjectsVisible())
                        {
                            if (options.getDefaultValueForNewButton() != null && 
                                !"".equals(options.getDefaultValueForNewButton()))
                            {//se existir um objecto definido por omissão
                                aux.setOnClickCode("winmain().openDoc('medium','" + options.getDefaultValueForNewButton().toLowerCase() +"','edit','method=new&amp;object=" + options.getDefaultValueForNewButton()+"')");
                            }
                            else if(options.newButtonClickable())
                            {//caso contrário usa o Super
                                aux.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;object=" + bodef.getName()+"')");
                            }
                        }
                        // caso contrario faz o que já fazia
                        else
                            aux.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +JSPMessages.getString("Menu.21") + bodef.getName()+"')");
                        // psantos fim
                        aux.setVisible(true);
                        aux.setDisabled(false);
                        aux.setAccessKey('R');
                        toRet.add(aux);
                    }
                    
                    if(options.isNewSubClassOptionVisible())
                    {         
                    
                        if(aux == null && subs.length > 0)
                        {
                            aux = new MenuItem();
                            aux.setId("buttonNew");
                            aux.setImgURL(bodef.getSrcForIcon16());
                            aux.setLabel(JSPMessages.getString("Menu.11"));
                            aux.setVisible(true);
                            aux.setDisabled(false);
                            aux.setAccessKey('R');
                            toRet.add(aux);                        
                        }
                        MenuItem sub;
                        for (int i = 0; i < subs.length; i++) 
                        {
                            sub = new MenuItem();
                            sub.setId("new" + subs[i].getName());
                            sub.setImgURL(subs[i].getSrcForIcon16());
                            sub.setLabel(subs[i].getLabel());
                            sub.setVisible(true);
                            sub.setDisabled(false);
                            sub.setOnClickCode("winmain().openDoc('medium','" + subs[i].getName().toLowerCase() +"','edit','method=new&amp;object=" + subs[i].getName()+"')");
                            aux.addSubMenuItem(sub);
                        }
                    }
                    
                    if(options.isNewFromTemplateOptionVisible())
                    {                
                        MenuItem mti = getFromTemplateMenu(ctx, bodef);
                        if(mti != null)
                        {
                            if(aux == null)
                            {
                                toRet.add(mti);                               
                            }
                            else
                            {
                                aux.addSubMenuItem(mti);
                            }
                            
                        }
                    }
                }
                else
                {
                    if(options.isNewOptionVisible())
                    {
                        aux = new MenuItem();
                        aux.setId("buttonNew");
                        aux.setImgURL(bodef.getSrcForIcon16());
                        aux.setLabel(JSPMessages.getString("Menu.2"));
                        aux.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;object=" + bodef.getName()+"')");
                        aux.setVisible(true);
                        aux.setDisabled(false);
                        aux.setAccessKey('R');
                        toRet.add(aux);
                    }
                    
                    if(options.isNewFromTemplateOptionVisible())
                    {                  
                        MenuItem mti = getFromTemplateMenu(ctx, bodef);
                        if(mti != null)
                        {
                            if(aux == null)
                            {
                                toRet.add(mti);                               
                            }
                            else
                            {
                                aux.addSubMenuItem(mti);
                            }
                        }
                    }
    
                    
                }
            }
            else if("message".equals(bodef.getName()))
            {
                if(options.isNewOptionVisible())
                {
                    newMessageMenuItem(ctx, toRet, bodef);
                }
            }
            else if("ResDocumentos".equals(bodef.getName()) ||
                    "ResDocumentos".equals(bodef.getBoSuperBo()))
            {
                if(options.isNewOptionVisible())
                {
                    newResDocumentosMenuItem(toRet, bodef);
                }
            }
            
            if(options.isDestroyOptionVisible())
            {        
                boolean delete = true;
                if(bodef.getBoClsState()!=null)
                {
                    String name = bodef.getBoClsState().getName();
                    
                    if(name.equalsIgnoreCase("stateControl"))
                        delete = false;
                }
                if(delete)
                {
                    MenuItem auxd = new MenuItem();
                    auxd.setId("buttonRemove");
                    auxd.setImgURL(Browser.getThemeDir() + "menu/16_delete.gif");
                    auxd.setTitle(JSPMessages.getString("Menu.10"));
                    auxd.setOnClickCode("removeSelectedLines()");
                    auxd.setVisible(true);
                    auxd.setDisabled(false);
                    auxd.setAccessKey('R');
                    toRet.add(auxd);       
                    
                    writeSeparator = true;
                }
            }            
            
            
            MenuItem aux2 = null;
            if(options.isPrintOptionVisible() || options.isExportOptionVisible())
            {          
                aux = new MenuItem();
                aux.setId("buttonPrint");
                aux.setImgURL(Browser.getThemeDir() + "menu/16_print.gif");
                aux.setLabel(JSPMessages.getString("Menu.44"));
                aux.setVisible(true);
                aux.setDisabled(false);
                aux.setAccessKey('I');
                toRet.add(aux);   
                
    //        aux2 = new MenuItem();
    //        aux2.setId("buttonExpListNoOption");
    //        aux2.setImgURL(Browser.getThemeDir() + "menu/prev-right.gif");
    //        aux2.setOnClickCode("winmain().openDocUrl('fixed,730px,450px','__chooseExportExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')");
    //        aux2.setLabel("Exportação de Listagem (S/ Opções)");
    //        aux2.setVisible(true);
    //        aux2.setDisabled(false);
    //        aux.addSubMenuItem(aux2);
                if(options.isPrintOptionVisible())
                { 
                    aux2 = new MenuItem();
                    aux2.setId("buttonImpListNoOption");
                    aux2.setImgURL(Browser.getThemeDir() + "menu/16_print.gif");
                    //aux2.setOnClickCode("winmain().open('__printExplorer.jsp?docid=$idx&viewer=$treeName','blank')");
                    aux2.setOnClickCode("winmain().openDocUrl('medium','__printExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')");
                    aux2.setLabel(JSPMessages.getString("Menu.48"));
                    aux2.setVisible(true);
                    aux2.setDisabled(false);
                    aux.addSubMenuItem(aux2);
                }
                if(options.isExportOptionVisible())
                { 
                    aux2 = new MenuItem();
                    aux2.setId("buttonImpExpListWOption");
                    aux2.setImgURL(Browser.getThemeDir() + "menu/prev-off.gif");
                    aux2.setOnClickCode("winmain().openDocUrl('medium','__chooseExportExplorerData.jsp','?pageName="+pageName+"&docid=$idx&treeKey=$treeName','lookup')");
                    aux2.setLabel(JSPMessages.getString("Menu.9"));
                    aux2.setVisible(true);
                    aux2.setDisabled(false);
                    aux.addSubMenuItem(aux2);
                }
                if(options.isSavePrintOptionVisible() && (options.isPrintOptionVisible() || options.isExportOptionVisible()))
                {        
                    aux2 = new MenuItem();
                    aux2.setId("buttonSaveListDef");
                    aux2.setImgURL(Browser.getThemeDir() + "menu/prev-off.gif");
                    aux2.setOnClickCode("winmain().openDocUrl('fixed,400px,150px,noresize','__saveExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')");
                    aux2.setLabel(JSPMessages.getString("Menu.57"));
                    aux2.setVisible(true);
                    aux2.setDisabled(false);
                    aux.addSubMenuItem(aux2);
                }
                writeSeparator = true;
            }
            
            if(writeSeparator)
            {
                toRet.add(MenuItem.getVerticalLineSeparator());
                writeSeparator = false;
            }

            //Métodos acessiveis pelo Explorer  
            String [] methods=svExp.getExplorer().getMethods();
            if(methods!=null)
            {        
                for (int i=0;i<methods.length;i++)
                {
                    String methodName=methods[i];
                    if (securityRights.canExecute (ctx,bodef.getName() ,methodName ))
                    {
                        MenuItem auxd = new MenuItem();
                        auxd.setId(methodName);
                        auxd.setImgURL(Browser.getThemeDir() + "menu/16_method.gif");
                        //Buscar o Metodo                    
                        auxd.setTitle(bodef.getBoMethod(methodName).getLabel());
                        auxd.setLabel(bodef.getBoMethod(methodName).getLabel());                    
                        
                        auxd.setOnClickCode("executeMethod('"+methodName+"','"+bodef.getName()+"','"+treeName+"')");
                        auxd.setVisible(true);
                        auxd.setDisabled(false);
                        //auxd.setAccessKey('R');
                        toRet.add(auxd);                           
                    }
                    
                }
                writeSeparator = true;
            }        
            
            if(writeSeparator)
            {
                toRet.add(MenuItem.getVerticalLineSeparator());
                writeSeparator = false;
            }
            
            if(options.isGroupsOptionVisible())
            {
                aux = new MenuItem();
                aux.setId("buttonGroup");
                aux.setImgURL(Browser.getThemeDir() + "menu/ghGroup.gif");
                aux.setOnClickCode("setGroup('" + treeName +"', 'explorerMenu');");
                aux.setVisible(true);
                aux.setDisabled(false);
                aux.setTitle(JSPMessages.getString("Menu.62"));
                toRet.add(aux);
                
                writeSeparator = true;
            }
            if(options.isParametersOptionVisible())
            {
                aux = new MenuItem();
                aux.setId("buttonParam");
                aux.setImgURL(Browser.getThemeDir() + "menu/ghParam.gif");
                aux.setOnClickCode("setParam('" + treeName +"', 'explorerMenu' );");
                aux.setVisible(true);
                aux.setDisabled(false);
                aux.setTitle(JSPMessages.getString("Menu.67"));
                toRet.add(aux);
                
                writeSeparator = true;
            }
            if(options.isSaveExplorerOptionVisible())
            {
                aux = new MenuItem();
                aux.setId(JSPMessages.getString("Menu.68"));
                aux.setImgURL(Browser.getThemeDir() + "menu/16_save.gif");
                aux.setOnClickCode("winmain().openDocUrl('fixed,400px,150px,noresize','__saveExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')");
                aux.setVisible(true);
                aux.setDisabled(false);
                aux.setTitle(JSPMessages.getString("Menu.71"));
                toRet.add(aux);
                
                writeSeparator = true;
            }
            
            if(options.isRefreshOptionVisible())
            {
                aux = new MenuItem();
                aux.setId("buttonRefresh");
                aux.setImgURL(Browser.getThemeDir() + "menu/ghRefresh.gif");
                aux.setOnClickCode("refreshExplorer('" + treeName +"');");
                aux.setVisible(true);
                aux.setDisabled(false);
                aux.setTitle(JSPMessages.getString("Menu.76"));
                toRet.add(aux);
                
                writeSeparator = true;
            }
            if(options.isPreviewRightOptionVisible() || options.isPreviewBottomOptionVisible())
            {
                aux = new MenuItem();
                aux.setId("buttonPanel");
                aux.setImgURL(Browser.getThemeDir() + "menu/prev-bott.gif");
                aux.setVisible(true);
                aux.setDisabled(false);
                aux.setTitle("Mostrar/Ocultar Pré-Visualização");
                toRet.add(aux);
                
                if(options.isPreviewRightOptionVisible())
                {
                    aux2 = new MenuItem();
                    aux2.setId("buttonPanelRight");
                    aux2.setImgURL(Browser.getThemeDir() + "menu/prev-right.gif");
                    aux2.setOnClickCode("setPreviewRight('" + treeName +"', 'explorerMenu' );");
                    aux2.setLabel(JSPMessages.getString("Menu.84"));
                    aux2.setVisible(true);
                    aux2.setDisabled(false);
                    aux.addSubMenuItem(aux2);
                }
                if(options.isPreviewBottomOptionVisible())
                {
                    aux2 = new MenuItem();
                    aux2.setId("buttonPanelBottom");
                    aux2.setImgURL(Browser.getThemeDir() + "menu/prev-bott.gif");
                    aux2.setOnClickCode("setPreviewDown('" + treeName +"', 'explorerMenu' );");
                    aux2.setLabel(JSPMessages.getString("Menu.89"));
                    aux2.setVisible(true);
                    aux2.setDisabled(false);
                    aux.addSubMenuItem(aux2);
                }
        
                aux2 = new MenuItem();
                aux2.setId("buttonPanelOff");
                aux2.setImgURL(Browser.getThemeDir() + "menu/prev-off.gif");
                aux2.setOnClickCode("setPreviewOff('" + treeName +"', 'explorerMenu' );");
                aux2.setLabel(JSPMessages.getString("Menu.8"));
                aux2.setVisible(true);
                aux2.setDisabled(false);
                aux.addSubMenuItem(aux2);
                
                writeSeparator = true;
            }
            
            if(writeSeparator)
            {           
                toRet.add(MenuItem.getVerticalLineSeparator());
                writeSeparator = false;
            }
            
            if(filter != null && options.isSavedFiltersOptionVisible())
            {
                toRet.add(MenuItem.getFilterComponent(filter));
            }
            if(svExp != null && options.isListSavedExploredOptionVisible())
            {
                toRet.add(MenuItem.getSavedExplorersComponent(svExp));
            }
            if(options.isFullTextSearchOptionVisible())
            {
                toRet.add(MenuItem.getSearchComponent(treeName));
            }
            if(options.isAdvancedSearchOptionVisible())
            {        
                toRet.add(MenuItem.getExpertSearchComponent(treeName, bodef.getName()));
            }
            if(boqlInitJsp != null && !"".equals(boqlInitJsp))
            {
                toRet.add(MenuItem.getBoqlInitJspComponent(treeName, boqlInitJsp));
            }
            
            result = new Menu(toRet, "explorerMenuFor" + treeName, "explorerMenuFor"+treeName);
            result.setOnClickParameters("idx", idx);
            result.setOnClickParameters("treeName", treeName);
            result.setMenuHeight("24px");
        }
        return result;
    }
    
    
    private static MenuItem getFromTemplateMenu(EboContext ctx, boDefHandler bodef) throws boRuntimeException
    {
        //Modelo Apartir de modelo
        boolean first = true;
        MenuItem toRet = null;
        boDefHandler subs[] = bodef.getTreeSubClasses();
        
        if(subs.length > 0)
        {
            MenuItem[] mi = new MenuItem[subs.length + 1];
            mi[0] = getTemplateMenuItem(ctx, bodef);

            for (int i = 0; i < subs.length; i++) 
            {
                mi[i+1] = getTemplateMenuItem(ctx, subs[i]);
            }
            
            for (int i = 0; i < mi.length; i++) 
            {
                if(mi[i] != null )
                {
                    if(toRet == null)
                    {
                        toRet = new MenuItem();
                        toRet.setId("newFromTempate");
                        toRet.setImgURL(boDefHandler.getBoDefinition("Ebo_Template").getSrcForIcon16());
                        toRet.setLabel(JSPMessages.getString("Menu.7"));
                        toRet.setOnClickCode(null);
                        toRet.setVisible(true);
                        toRet.setDisabled(false);
                        toRet.setAccessKey('M');                        
                    }
                    toRet.addSubMenuItem(mi[i]);
                    
                }
            }   
        }
        else
        {
            MenuItem m = getTemplateMenuItem(ctx, bodef);
            if(m != null)
            {
                toRet = new MenuItem();
                toRet.setId("newFromTempate");
                toRet.setImgURL(boDefHandler.getBoDefinition("Ebo_Template").getSrcForIcon16());
                toRet.setLabel(JSPMessages.getString("Menu.106"));
                toRet.setOnClickCode(null);
                toRet.setVisible(true);
                toRet.setDisabled(false);
                toRet.setAccessKey('M');
                toRet.addSubMenu(m.getSubMenu());
            }
        }
        return toRet;
    }
    
    private static MenuItem getTemplateMenuItem(EboContext ctx, boDefHandler bodef) throws boRuntimeException
    {
        StringBuffer boql_ = new StringBuffer("select Ebo_Template where masterObjectClass.name='"+ bodef.getName()+"' order by name");
        boObjectList l = boObjectList.list(ctx, boql_.toString());
        l.beforeFirst();
        boObject auxT;
        MenuItem menuT = null, subT = null;
        boolean first = true;
        while(l.next())
        {
            auxT = l.getObject();
            if(first)
            {
                menuT = new MenuItem();
                menuT.setId("newFromTempate" + bodef.getName());
                menuT.setImgURL(bodef.getSrcForIcon16());
                menuT.setLabel(bodef.getLabel());
                menuT.setVisible(true);
                menuT.setDisabled(false);
                menuT.setOnClickCode(null);
            }
            subT = new MenuItem();
            subT.setId("newTemp" + auxT.getBoui());
            subT.setImgURL(bodef.getSrcForIcon16());
            subT.setLabel(auxT.getAttribute("name").getValueString());
            subT.setVisible(true);
            subT.setDisabled(false);
            subT.setOnClickCode("winmain().openDoc('medium','"+bodef.getName().toLowerCase()+"','edit','method=newfromtemplate&parent_boui="+auxT.getBoui()+"')");
            menuT.addSubMenuItem(subT);

            first = false;
        }        
        return menuT;
    }
    
    private static void newMessageMenuItem(EboContext ctx, ArrayList toRet, boDefHandler bodef) throws boRuntimeException
    {
        boDefHandler subs[] = bodef.getTreeSubClasses();
        MenuItem aux = new MenuItem();
        aux.setId("buttonNew");
            
        aux.setImgURL(bodef.getSrcForIcon16());
        aux.setLabel(JSPMessages.getString("Menu.6"));
//        aux.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;object=" + bodef.getName()+"')");
        aux.setVisible(true);
        aux.setDisabled(false);
        aux.setAccessKey('N');
        toRet.add(aux);
            
        MenuItem receiveItem = new MenuItem();
        receiveItem.setImgURL(Browser.getThemeDir() + "menu/receivingMessage.gif");
        receiveItem.setLabel(JSPMessages.getString("Menu.5"));
//        receiveItem.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;object=" + bodef.getName()+"')");
        receiveItem.setVisible(true);
        receiveItem.setDisabled(false);
        receiveItem.setAccessKey('R');

        MenuItem sendItem = new MenuItem();
        sendItem.setImgURL(Browser.getThemeDir() + "menu/sendingMessage.gif");
        sendItem.setLabel(JSPMessages.getString("Menu.120"));
//        receiveItem.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;object=" + bodef.getName()+"')");
        sendItem.setVisible(true);
        sendItem.setDisabled(false);
        sendItem.setAccessKey('E');
        
        aux.addSubMenuItem(sendItem);
        aux.addSubMenuItem(receiveItem);
        
        
            
        MenuItem subToSend;
        MenuItem subToReceive;
        for (int i = 0; i < subs.length; i++) 
        {
            if(!"messageSystem".equals(subs[i].getName()))
            {
                if(!onlyInReceive(subs[i]))
                {
                    subToSend = new MenuItem();
                    subToSend.setId("new" + subs[i].getName());
                    subToSend.setImgURL(subs[i].getSrcForIcon16());
                    subToSend.setLabel(subs[i].getLabel());
                    subToSend.setVisible(true);
                    subToSend.setDisabled(false);
                    subToSend.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=" + subs[i].getName()+"','std')");
                    sendItem.addSubMenuItem(subToSend);
                }
                subToReceive = new MenuItem();
                subToReceive.setId("new" + subs[i].getName());
                subToReceive.setImgURL(subs[i].getSrcForIcon16());
                subToReceive.setLabel(subs[i].getLabel());
                subToReceive.setVisible(true);
                subToReceive.setDisabled(false);
                subToReceive.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=false&amp;embeddedProgram=true&amp;object=" + subs[i].getName()+"','std')");
                receiveItem.addSubMenuItem(subToReceive);
            }
        }
        subToSend = new MenuItem();
        subToReceive = new MenuItem();
        subToSend.setId("new" + bodef.getName());
        subToReceive.setId("new" + bodef.getName());
        subToSend.setImgURL(bodef.getSrcForIcon16());
        subToReceive.setImgURL(bodef.getSrcForIcon16());
        subToSend.setLabel("Mensagem");
        subToReceive.setLabel("Mensagem");
        subToSend.setVisible(true);
        subToReceive.setVisible(true);
        subToSend.setDisabled(false);
        subToReceive.setDisabled(false);
        subToSend.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=" + bodef.getName() +"','std')");
//        subToSend.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;masterdoc=true&amp;controllerName=XwfController&amp;toSend=true&amp;embeddedProgram=true&amp;object=" + bodef.getName()+"')");
        subToReceive.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=false&amp;embeddedProgram=true&amp;object=" + bodef.getName() +"','std')");
//        subToReceive.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;masterdoc=true&amp;controllerName=XwfController&amp;toSend=false&amp;embeddedProgram=true&amp;object=" + bodef.getName()+"')");
        sendItem.addSubMenuItem(subToSend);
        receiveItem.addSubMenuItem(subToReceive);
        
        //apartir de modelo
        MenuItem mti = getFromTemplateMenu(ctx, bodef);
        if(mti != null)
        {
            aux.addSubMenuItem(mti);
        }        
    }

    /**
     * Constroi o menu especifico do RES para a ANACOM.
     * @param toRet, ArrayList
     * @param bodef, boDefHandler
     */     
    private static void newResDocumentosMenuItem(ArrayList toRet, boDefHandler bodef)
    {
        if(!"ResDocumentos".equals(bodef.getName()))
        {
            MenuItem aux = new MenuItem();
            aux.setId("buttonNew");    
            if("ResSaidaOficio".equals(bodef.getName()))
            {
                aux.setLabel(JSPMessages.getString("Menu.4"));
                aux.setImgURL(Browser.getThemeDir() + "menu/sendingMessage.gif");
                aux.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=messageLetter','std')");
                aux.setAccessKey('R');
            }
            else if("ResSaidaEmail".equals(bodef.getName()))
            {
                aux.setLabel(JSPMessages.getString("Menu.143"));
                aux.setImgURL(Browser.getThemeDir() + "menu/sendingMessage.gif");
                aux.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=messageMail','std')");
                aux.setAccessKey('R');
            }
            else if("ResSaidaFax".equals(bodef.getName()))
            {
                aux.setLabel(JSPMessages.getString("Menu.147"));
                aux.setImgURL(Browser.getThemeDir() + "menu/sendingMessage.gif");
                aux.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=messageFax','std')");
                aux.setAccessKey('R');
            }
            else if("ResInternos".equals(bodef.getName()))
            {
                aux.setLabel(JSPMessages.getString("Menu.151"));
                aux.setImgURL(Browser.getThemeDir() + "menu/sendingMessage.gif");
                aux.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=messageInternal','std')");
                aux.setAccessKey('R');
            }
            else if("ResEntradas".equals(bodef.getName()))
            {
                aux.setLabel(JSPMessages.getString("Menu.155"));
                aux.setImgURL(Browser.getThemeDir() + "menu/receivingMessage.gif");
                aux.setAccessKey('R');
                
                MenuItem receive = null;
                receive = new MenuItem();
                receive.setId("newFax");
                receive.setImgURL("resources/messageFax/ico16.gif");
                receive.setLabel(JSPMessages.getString("Menu.3"));
                aux.setAccessKey('F');
                receive.setVisible(true);
                receive.setDisabled(false);
                receive.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=false&amp;embeddedProgram=true&amp;object=messageFax','std')");
                aux.addSubMenuItem(receive);
                
                receive = new MenuItem();
                receive.setId("newEmail");
                receive.setImgURL("resources/messageMail/ico16.gif");
                receive.setLabel(JSPMessages.getString("Menu.163"));
                aux.setAccessKey('E');
                receive.setVisible(true);
                receive.setDisabled(false);
                receive.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=false&amp;embeddedProgram=true&amp;object=messageMail','std')");
                aux.addSubMenuItem(receive);

                receive = new MenuItem();
                receive.setId("newFax");
                receive.setImgURL("resources/messageLetter/ico16.gif");
                receive.setLabel(JSPMessages.getString("Menu.167"));
                aux.setAccessKey('O');
                receive.setVisible(true);
                receive.setDisabled(false);
                receive.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=false&amp;embeddedProgram=true&amp;object=messageLetter','std')");
                aux.addSubMenuItem(receive);
                
            }
                                      
            aux.setVisible(true);
            aux.setDisabled(false);
            toRet.add(aux);
        }         
    }
    
    private static boolean onlyInReceive(boDefHandler bodef)
    {
        if("messageDelivered".equals(bodef.getName()) || "messageReceipt".equals(bodef.getName()))
        {
            return true;
        }
        return false;
    }
    
    public static Menu getMouseMenu(String treeName, boDefHandler bodef, String idx, Filter filter) throws boRuntimeException 
    {
        ArrayList toRet = new ArrayList();

        //novo
        MenuItem aux = null;
        
        //refresh
        aux = new MenuItem();
        aux.setId("buttonRefresh");
        aux.setImgURL(Browser.getThemeDir() + "mouseMenu/ghRefresh.gif");
        aux.setLabel(JSPMessages.getString("Menu.173"));
        aux.setOnClickCode("refreshExplorer();");
        //aux.setOnClickCode("reloadGrid()");
        
        aux.setVisible(true);
        aux.setDisabled(false);
        aux.setAccessKey('A');
        toRet.add(aux);
        
        toRet.add(MenuItem.getHorizontalLineSeparator());

        //remover
        aux = new MenuItem();
        aux.setId("buttonRemove");
        aux.setImgURL(Browser.getThemeDir() + "menu/16_delete.gif");
        aux.setLabel(JSPMessages.getString("Menu.177"));
        aux.setOnClickCode("alert('fazer a chamada para remover')");
        aux.setVisible(true);
        aux.setDisabled(false);
        aux.setAccessKey('R');
        toRet.add(aux);

        aux = new MenuItem();
        aux.setId("buttonPrint");
        aux.setImgURL(Browser.getThemeDir() + "menu/16_print.gif");
        aux.setLabel(JSPMessages.getString("Menu.181"));
        aux.setVisible(true);
        aux.setDisabled(false);
        aux.setAccessKey('I');
        toRet.add(aux);
        
      
        
        MenuItem aux2 = new MenuItem();
        aux2.setId("buttonExpListNoOption");
        aux2.setImgURL(Browser.getThemeDir() + "menu/prev-right.gif");
        aux2.setOnClickCode("winmain().openDocUrl('fixed,730px,450px','__chooseExportExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')");
        aux2.setLabel(JSPMessages.getString("Menu.185"));
        aux2.setVisible(true);
        aux2.setDisabled(false);

        aux.addSubMenuItem(aux2);

        aux2 = new MenuItem();
        aux2.setId("buttonImpListNoOption");
        aux2.setImgURL(Browser.getThemeDir() + "menu/prev-bott.gif");
        //aux2.setOnClickCode("winmain().open('__printExplorer.jsp?docid=$idx&viewer=$treeName','blank')");
        aux2.setOnClickCode("winmain().openDocUrl('medium','__printExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')");
        aux2.setLabel(JSPMessages.getString("Menu.189"));
        aux2.setVisible(true);
        aux2.setDisabled(false);

        aux.addSubMenuItem(aux2);

        aux2 = new MenuItem();
        aux2.setId("buttonImpExpListWOption");
        aux2.setImgURL(Browser.getThemeDir() + "menu/prev-off.gif");
        aux2.setOnClickCode("winmain().openDocUrl('medium','__chooseExportExplorerData.jsp','?docid=$idx&treeKey=$treeName','lookup')");
        aux2.setLabel(JSPMessages.getString("Menu.193"));
        aux2.setVisible(true);
        aux2.setDisabled(false);

        aux.addSubMenuItem(aux2);
        
        aux2 = new MenuItem();
        aux2.setId("buttonSaveListDef");
        aux2.setImgURL(Browser.getThemeDir() + "menu/prev-off.gif");
        aux2.setOnClickCode("winmain().openDocUrl('fixed,400px,150px,noresize','__saveExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')");
        aux2.setLabel(JSPMessages.getString("Menu.197"));
        aux2.setVisible(true);
        aux2.setDisabled(false);

        aux.addSubMenuItem(aux2);
        
        Menu m = new Menu(toRet, "explorerMouseMenu", "explorerMouseMenu");
        m.setVerticalMenu(true);
        return m;
    }
    
    public MenuItem getSearchComponent()
    {
        for (int i = 0; i < menuItems.size(); i++) 
        {
            if(((MenuItem)menuItems.get(i)).isSearchComponent())
            {
                return (MenuItem)menuItems.get(i); 
            }
        }
        return null;
    }

    public static Menu getTestMenu() throws boRuntimeException {
        ArrayList toRet = new ArrayList();

        //save
        MenuItem svItem = new MenuItem(
                "<TD tabindex=\"0\" class=\"mnuOption\" onmouseover=\"menuItemOn()\" onmouseout=\"menuItemOff()\" onclick=\"setPreviewDown();\">setDown</TD>");

        //        svItem.setId("save");
        //        svItem.setAfterRunCode("alert('after')");
        //        svItem.setBeforeRunCode("alert('before')");
        //        svItem.setImgURL(Browser.getThemeDir() + "templates/menu/16_save.gif");
        //        svItem.setLabel("S<u>a</u>ve");
        //        svItem.setOnClickCode("alert('onclick')");
        //        svItem.setVisible(true);
        //        svItem.setDisabled(false);
        //        svItem.setAccessKey('E');
        toRet.add(svItem);

        MenuItem svItem2 = MenuItem.getSearchComponent("tree");
        toRet.add(svItem2);

//        MenuItem svItem3 = MenuItem.getExpertSearchComponent("tree");
//        toRet.add(svItem3);

        //        MenuItem svItem4 = new MenuItem();
        //        svItem4.setId("save");
        //        svItem4.setAfterRunCode("alert('after')");
        //        svItem4.setBeforeRunCode("alert('before')");
        //        svItem4.setImgURL(Browser.getThemeDir() + "templates/menu/16_save.gif");
        //        svItem4.setLabel("S<u>a</u>ve");
        //        svItem4.setOnClickCode("alert('onclick')");
        //        svItem4.setVisible(true);
        //        svItem4.setDisabled(false);
        //        svItem4.setAccessKey('E');
        //        svItem.addSubMenuItem(svItem4);
        return new Menu(toRet);
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList,  PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writeMenu(out, this,docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getMenuHTML(this,docHTML, docList, control);
    }
    public void setOnClickParameters(String paramName, String value)
    {
        parameters.put(paramName, value);
    }
    public Hashtable getParameters()
    {
        return parameters;
    }
}
