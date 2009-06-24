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
    private String mnuName="menu"; //$NON-NLS-1$
    private String mnuID=null;
    private boolean horizontalMenu = true;
    private String importCode = null;
    private Hashtable parameters = new Hashtable();
    private String p_menuHeight ="24px"; //$NON-NLS-1$
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

            
            if(!"message".equals(bodef.getName()) &&  //$NON-NLS-1$
               !"xwfActivity".equals(bodef.getName()) &&  //$NON-NLS-1$
               !"xwfActivity".equals(bodef.getBoSuperBo()) && //$NON-NLS-1$
               !"ResDocumentos".equals(bodef.getName()) &&  //$NON-NLS-1$
               !"ResDocumentos".equals(bodef.getBoSuperBo()) && //$NON-NLS-1$
               !"ACS_RuntimeAlert".equals(bodef.getName()) && //$NON-NLS-1$
               !"Ebo_History".equals(bodef.getName()) && //$NON-NLS-1$
               !"Ebo_CheckOut".equals(bodef.getName()) && //$NON-NLS-1$
               !"xwfProgramRuntime".equals(bodef.getName()) && //$NON-NLS-1$
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
                        aux.setId("buttonNew"); //$NON-NLS-1$
                        aux.setImgURL(bodef.getSrcForIcon16());
                        aux.setLabel(JSPMessages.getString("Menu.12")); //$NON-NLS-1$
                        // psantos ini
                        // aux.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;object=" + bodef.getName()+"')");
                        // se no xml existe o elemento newSubClassOption
                        if (options.isObjectsVisible())
                        {
                            if (options.getDefaultValueForNewButton() != null && 
                                !"".equals(options.getDefaultValueForNewButton())) //$NON-NLS-1$
                            {//se existir um objecto definido por omissão
                                aux.setOnClickCode("winmain().openDoc('medium','" + options.getDefaultValueForNewButton().toLowerCase() +"','edit','method=new&amp;object=" + options.getDefaultValueForNewButton()+"')"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            }
                            else if(options.newButtonClickable())
                            {//caso contrário usa o Super
                                aux.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;object=" + bodef.getName()+"')"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            }
                        }
                        // caso contrario faz o que já fazia
                        else
                            aux.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +JSPMessages.getString("Menu.21") + bodef.getName()+"')"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
                            aux.setId("buttonNew"); //$NON-NLS-1$
                            aux.setImgURL(bodef.getSrcForIcon16());
                            aux.setLabel(JSPMessages.getString("Menu.11")); //$NON-NLS-1$
                            aux.setVisible(true);
                            aux.setDisabled(false);
                            aux.setAccessKey('R');
                            toRet.add(aux);                        
                        }
                        MenuItem sub;
                        for (int i = 0; i < subs.length; i++) 
                        {
                            sub = new MenuItem();
                            sub.setId("new" + subs[i].getName()); //$NON-NLS-1$
                            sub.setImgURL(subs[i].getSrcForIcon16());
                            sub.setLabel(subs[i].getLabel());
                            sub.setVisible(true);
                            sub.setDisabled(false);
                            sub.setOnClickCode("winmain().openDoc('medium','" + subs[i].getName().toLowerCase() +"','edit','method=new&amp;object=" + subs[i].getName()+"')"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
                        aux.setId("buttonNew"); //$NON-NLS-1$
                        aux.setImgURL(bodef.getSrcForIcon16());
                        aux.setLabel(JSPMessages.getString("Menu.2")); //$NON-NLS-1$
                        aux.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;object=" + bodef.getName()+"')"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
            else if("message".equals(bodef.getName())) //$NON-NLS-1$
            {
                if(options.isNewOptionVisible())
                {
                    newMessageMenuItem(ctx, toRet, bodef);
                }
            }
            else if("ResDocumentos".equals(bodef.getName()) || //$NON-NLS-1$
                    "ResDocumentos".equals(bodef.getBoSuperBo())) //$NON-NLS-1$
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
                    
                    if(name.equalsIgnoreCase("stateControl")) //$NON-NLS-1$
                        delete = false;
                }
                if(delete)
                {
                    MenuItem auxd = new MenuItem();
                    auxd.setId("buttonRemove"); //$NON-NLS-1$
                    auxd.setImgURL(Browser.getThemeDir() + "menu/16_delete.gif"); //$NON-NLS-1$
                    auxd.setTitle(JSPMessages.getString("Menu.10")); //$NON-NLS-1$
                    auxd.setOnClickCode("removeSelectedLines()"); //$NON-NLS-1$
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
                aux.setId("buttonPrint"); //$NON-NLS-1$
                aux.setImgURL(Browser.getThemeDir() + "menu/16_print.gif"); //$NON-NLS-1$
                aux.setLabel(JSPMessages.getString("Menu.44")); //$NON-NLS-1$
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
                    aux2.setId("buttonImpListNoOption"); //$NON-NLS-1$
                    aux2.setImgURL(Browser.getThemeDir() + "menu/16_print.gif"); //$NON-NLS-1$
                    //aux2.setOnClickCode("winmain().open('__printExplorer.jsp?docid=$idx&viewer=$treeName','blank')");
                    aux2.setOnClickCode("winmain().openDocUrl('medium','__printExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')"); //$NON-NLS-1$
                    aux2.setLabel(JSPMessages.getString("Menu.48")); //$NON-NLS-1$
                    aux2.setVisible(true);
                    aux2.setDisabled(false);
                    aux.addSubMenuItem(aux2);
                }
                if(options.isExportOptionVisible())
                { 
                    aux2 = new MenuItem();
                    aux2.setId("buttonImpExpListWOption"); //$NON-NLS-1$
                    aux2.setImgURL(Browser.getThemeDir() + "menu/prev-off.gif"); //$NON-NLS-1$
                    aux2.setOnClickCode("winmain().openDocUrl('medium','__chooseExportExplorerData.jsp','?pageName="+pageName+"&docid=$idx&treeKey=$treeName','lookup')"); //$NON-NLS-1$ //$NON-NLS-2$
                    aux2.setLabel(JSPMessages.getString("Menu.9")); //$NON-NLS-1$
                    aux2.setVisible(true);
                    aux2.setDisabled(false);
                    aux.addSubMenuItem(aux2);
                }
                if(options.isSavePrintOptionVisible() && (options.isPrintOptionVisible() || options.isExportOptionVisible()))
                {        
                    aux2 = new MenuItem();
                    aux2.setId("buttonSaveListDef"); //$NON-NLS-1$
                    aux2.setImgURL(Browser.getThemeDir() + "menu/prev-off.gif"); //$NON-NLS-1$
                    aux2.setOnClickCode("winmain().openDocUrl('fixed,400px,150px,noresize','__saveExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')"); //$NON-NLS-1$
                    aux2.setLabel(JSPMessages.getString("Menu.57")); //$NON-NLS-1$
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
                        auxd.setId(methodName); //$NON-NLS-1$
                        auxd.setImgURL(Browser.getThemeDir() + "menu/16_method.gif"); //$NON-NLS-1$
                        //Buscar o Metodo                    
                        auxd.setTitle(bodef.getBoMethod(methodName).getLabel()); //$NON-NLS-1$
                        auxd.setLabel(bodef.getBoMethod(methodName).getLabel());                    
                        
                        auxd.setOnClickCode("executeMethod('"+methodName+"','"+bodef.getName()+"','"+treeName+"')"); //$NON-NLS-1$
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
                aux.setId("buttonGroup"); //$NON-NLS-1$
                aux.setImgURL(Browser.getThemeDir() + "menu/ghGroup.gif"); //$NON-NLS-1$
                aux.setOnClickCode("setGroup('" + treeName +"', 'explorerMenu');"); //$NON-NLS-1$ //$NON-NLS-2$
                aux.setVisible(true);
                aux.setDisabled(false);
                aux.setTitle(JSPMessages.getString("Menu.62")); //$NON-NLS-1$
                toRet.add(aux);
                
                writeSeparator = true;
            }
            if(options.isParametersOptionVisible())
            {
                aux = new MenuItem();
                aux.setId("buttonParam"); //$NON-NLS-1$
                aux.setImgURL(Browser.getThemeDir() + "menu/ghParam.gif"); //$NON-NLS-1$
                aux.setOnClickCode("setParam('" + treeName +"', 'explorerMenu' );"); //$NON-NLS-1$ //$NON-NLS-2$
                aux.setVisible(true);
                aux.setDisabled(false);
                aux.setTitle(JSPMessages.getString("Menu.67")); //$NON-NLS-1$
                toRet.add(aux);
                
                writeSeparator = true;
            }
            if(options.isSaveExplorerOptionVisible())
            {
                aux = new MenuItem();
                aux.setId(JSPMessages.getString("Menu.68")); //$NON-NLS-1$
                aux.setImgURL(Browser.getThemeDir() + "menu/16_save.gif"); //$NON-NLS-1$
                aux.setOnClickCode("winmain().openDocUrl('fixed,400px,150px,noresize','__saveExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')"); //$NON-NLS-1$
                aux.setVisible(true);
                aux.setDisabled(false);
                aux.setTitle(JSPMessages.getString("Menu.71")); //$NON-NLS-1$
                toRet.add(aux);
                
                writeSeparator = true;
            }
            
            if(options.isRefreshOptionVisible())
            {
                aux = new MenuItem();
                aux.setId("buttonRefresh"); //$NON-NLS-1$
                aux.setImgURL(Browser.getThemeDir() + "menu/ghRefresh.gif"); //$NON-NLS-1$
                aux.setOnClickCode("refreshExplorer('" + treeName +"');"); //$NON-NLS-1$ //$NON-NLS-2$
                aux.setVisible(true);
                aux.setDisabled(false);
                aux.setTitle(JSPMessages.getString("Menu.76")); //$NON-NLS-1$
                toRet.add(aux);
                
                writeSeparator = true;
            }
            if(options.isPreviewRightOptionVisible() || options.isPreviewBottomOptionVisible())
            {
                aux = new MenuItem();
                aux.setId("buttonPanel"); //$NON-NLS-1$
                aux.setImgURL(Browser.getThemeDir() + "menu/prev-bott.gif"); //$NON-NLS-1$
                aux.setVisible(true);
                aux.setDisabled(false);
                aux.setTitle("Mostrar/Ocultar Pré-Visualização"); //$NON-NLS-1$
                toRet.add(aux);
                
                if(options.isPreviewRightOptionVisible())
                {
                    aux2 = new MenuItem();
                    aux2.setId("buttonPanelRight"); //$NON-NLS-1$
                    aux2.setImgURL(Browser.getThemeDir() + "menu/prev-right.gif"); //$NON-NLS-1$
                    aux2.setOnClickCode("setPreviewRight('" + treeName +"', 'explorerMenu' );"); //$NON-NLS-1$ //$NON-NLS-2$
                    aux2.setLabel(JSPMessages.getString("Menu.84")); //$NON-NLS-1$
                    aux2.setVisible(true);
                    aux2.setDisabled(false);
                    aux.addSubMenuItem(aux2);
                }
                if(options.isPreviewBottomOptionVisible())
                {
                    aux2 = new MenuItem();
                    aux2.setId("buttonPanelBottom"); //$NON-NLS-1$
                    aux2.setImgURL(Browser.getThemeDir() + "menu/prev-bott.gif"); //$NON-NLS-1$
                    aux2.setOnClickCode("setPreviewDown('" + treeName +"', 'explorerMenu' );"); //$NON-NLS-1$ //$NON-NLS-2$
                    aux2.setLabel(JSPMessages.getString("Menu.89")); //$NON-NLS-1$
                    aux2.setVisible(true);
                    aux2.setDisabled(false);
                    aux.addSubMenuItem(aux2);
                }
        
                aux2 = new MenuItem();
                aux2.setId("buttonPanelOff"); //$NON-NLS-1$
                aux2.setImgURL(Browser.getThemeDir() + "menu/prev-off.gif"); //$NON-NLS-1$
                aux2.setOnClickCode("setPreviewOff('" + treeName +"', 'explorerMenu' );"); //$NON-NLS-1$ //$NON-NLS-2$
                aux2.setLabel(JSPMessages.getString("Menu.8")); //$NON-NLS-1$
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
            if(boqlInitJsp != null && !"".equals(boqlInitJsp)) //$NON-NLS-1$
            {
                toRet.add(MenuItem.getBoqlInitJspComponent(treeName, boqlInitJsp));
            }
            
            result = new Menu(toRet, "explorerMenuFor" + treeName, "explorerMenuFor"+treeName); //$NON-NLS-1$ //$NON-NLS-2$
            result.setOnClickParameters("idx", idx); //$NON-NLS-1$
            result.setOnClickParameters("treeName", treeName); //$NON-NLS-1$
            result.setMenuHeight("24px"); //$NON-NLS-1$
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
                        toRet.setId("newFromTempate"); //$NON-NLS-1$
                        toRet.setImgURL(boDefHandler.getBoDefinition("Ebo_Template").getSrcForIcon16()); //$NON-NLS-1$
                        toRet.setLabel(JSPMessages.getString("Menu.7")); //$NON-NLS-1$
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
                toRet.setId("newFromTempate"); //$NON-NLS-1$
                toRet.setImgURL(boDefHandler.getBoDefinition("Ebo_Template").getSrcForIcon16()); //$NON-NLS-1$
                toRet.setLabel(JSPMessages.getString("Menu.106")); //$NON-NLS-1$
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
        StringBuffer boql_ = new StringBuffer("select Ebo_Template where masterObjectClass.name='"+ bodef.getName()+"' order by name"); //$NON-NLS-1$ //$NON-NLS-2$
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
                menuT.setId("newFromTempate" + bodef.getName()); //$NON-NLS-1$
                menuT.setImgURL(bodef.getSrcForIcon16());
                menuT.setLabel(bodef.getLabel());
                menuT.setVisible(true);
                menuT.setDisabled(false);
                menuT.setOnClickCode(null);
            }
            subT = new MenuItem();
            subT.setId("newTemp" + auxT.getBoui()); //$NON-NLS-1$
            subT.setImgURL(bodef.getSrcForIcon16());
            subT.setLabel(auxT.getAttribute("name").getValueString()); //$NON-NLS-1$
            subT.setVisible(true);
            subT.setDisabled(false);
            subT.setOnClickCode("winmain().openDoc('medium','"+bodef.getName().toLowerCase()+"','edit','method=newfromtemplate&parent_boui="+auxT.getBoui()+"')"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            menuT.addSubMenuItem(subT);

            first = false;
        }        
        return menuT;
    }
    
    private static void newMessageMenuItem(EboContext ctx, ArrayList toRet, boDefHandler bodef) throws boRuntimeException
    {
        boDefHandler subs[] = bodef.getTreeSubClasses();
        MenuItem aux = new MenuItem();
        aux.setId("buttonNew"); //$NON-NLS-1$
            
        aux.setImgURL(bodef.getSrcForIcon16());
        aux.setLabel(JSPMessages.getString("Menu.6")); //$NON-NLS-1$
//        aux.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;object=" + bodef.getName()+"')");
        aux.setVisible(true);
        aux.setDisabled(false);
        aux.setAccessKey('N');
        toRet.add(aux);
            
        MenuItem receiveItem = new MenuItem();
        receiveItem.setImgURL(Browser.getThemeDir() + "menu/receivingMessage.gif"); //$NON-NLS-1$
        receiveItem.setLabel(JSPMessages.getString("Menu.5")); //$NON-NLS-1$
//        receiveItem.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;object=" + bodef.getName()+"')");
        receiveItem.setVisible(true);
        receiveItem.setDisabled(false);
        receiveItem.setAccessKey('R');

        MenuItem sendItem = new MenuItem();
        sendItem.setImgURL(Browser.getThemeDir() + "menu/sendingMessage.gif"); //$NON-NLS-1$
        sendItem.setLabel(JSPMessages.getString("Menu.120")); //$NON-NLS-1$
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
            if(!"messageSystem".equals(subs[i].getName())) //$NON-NLS-1$
            {
                if(!onlyInReceive(subs[i]))
                {
                    subToSend = new MenuItem();
                    subToSend.setId("new" + subs[i].getName()); //$NON-NLS-1$
                    subToSend.setImgURL(subs[i].getSrcForIcon16());
                    subToSend.setLabel(subs[i].getLabel());
                    subToSend.setVisible(true);
                    subToSend.setDisabled(false);
                    subToSend.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=" + subs[i].getName()+"','std')"); //$NON-NLS-1$ //$NON-NLS-2$
                    sendItem.addSubMenuItem(subToSend);
                }
                subToReceive = new MenuItem();
                subToReceive.setId("new" + subs[i].getName()); //$NON-NLS-1$
                subToReceive.setImgURL(subs[i].getSrcForIcon16());
                subToReceive.setLabel(subs[i].getLabel());
                subToReceive.setVisible(true);
                subToReceive.setDisabled(false);
                subToReceive.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=false&amp;embeddedProgram=true&amp;object=" + subs[i].getName()+"','std')"); //$NON-NLS-1$ //$NON-NLS-2$
                receiveItem.addSubMenuItem(subToReceive);
            }
        }
        subToSend = new MenuItem();
        subToReceive = new MenuItem();
        subToSend.setId("new" + bodef.getName()); //$NON-NLS-1$
        subToReceive.setId("new" + bodef.getName()); //$NON-NLS-1$
        subToSend.setImgURL(bodef.getSrcForIcon16());
        subToReceive.setImgURL(bodef.getSrcForIcon16());
        subToSend.setLabel("Mensagem"); //$NON-NLS-1$
        subToReceive.setLabel("Mensagem"); //$NON-NLS-1$
        subToSend.setVisible(true);
        subToReceive.setVisible(true);
        subToSend.setDisabled(false);
        subToReceive.setDisabled(false);
        subToSend.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=" + bodef.getName() +"','std')"); //$NON-NLS-1$ //$NON-NLS-2$
//        subToSend.setOnClickCode("winmain().openDoc('medium','" + bodef.getName().toLowerCase() +"','edit','method=new&amp;masterdoc=true&amp;controllerName=XwfController&amp;toSend=true&amp;embeddedProgram=true&amp;object=" + bodef.getName()+"')");
        subToReceive.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=false&amp;embeddedProgram=true&amp;object=" + bodef.getName() +"','std')"); //$NON-NLS-1$ //$NON-NLS-2$
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
        if(!"ResDocumentos".equals(bodef.getName())) //$NON-NLS-1$
        {
            MenuItem aux = new MenuItem();
            aux.setId("buttonNew");     //$NON-NLS-1$
            if("ResSaidaOficio".equals(bodef.getName())) //$NON-NLS-1$
            {
                aux.setLabel(JSPMessages.getString("Menu.4")); //$NON-NLS-1$
                aux.setImgURL(Browser.getThemeDir() + "menu/sendingMessage.gif"); //$NON-NLS-1$
                aux.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=messageLetter','std')"); //$NON-NLS-1$
                aux.setAccessKey('R');
            }
            else if("ResSaidaEmail".equals(bodef.getName())) //$NON-NLS-1$
            {
                aux.setLabel(JSPMessages.getString("Menu.143")); //$NON-NLS-1$
                aux.setImgURL(Browser.getThemeDir() + "menu/sendingMessage.gif"); //$NON-NLS-1$
                aux.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=messageMail','std')"); //$NON-NLS-1$
                aux.setAccessKey('R');
            }
            else if("ResSaidaFax".equals(bodef.getName())) //$NON-NLS-1$
            {
                aux.setLabel(JSPMessages.getString("Menu.147")); //$NON-NLS-1$
                aux.setImgURL(Browser.getThemeDir() + "menu/sendingMessage.gif"); //$NON-NLS-1$
                aux.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=messageFax','std')"); //$NON-NLS-1$
                aux.setAccessKey('R');
            }
            else if("ResInternos".equals(bodef.getName())) //$NON-NLS-1$
            {
                aux.setLabel(JSPMessages.getString("Menu.151")); //$NON-NLS-1$
                aux.setImgURL(Browser.getThemeDir() + "menu/sendingMessage.gif"); //$NON-NLS-1$
                aux.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=true&amp;embeddedProgram=true&amp;object=messageInternal','std')"); //$NON-NLS-1$
                aux.setAccessKey('R');
            }
            else if("ResEntradas".equals(bodef.getName())) //$NON-NLS-1$
            {
                aux.setLabel(JSPMessages.getString("Menu.155")); //$NON-NLS-1$
                aux.setImgURL(Browser.getThemeDir() + "menu/receivingMessage.gif"); //$NON-NLS-1$
                aux.setAccessKey('R');
                
                MenuItem receive = null;
                receive = new MenuItem();
                receive.setId("newFax"); //$NON-NLS-1$
                receive.setImgURL("resources/messageFax/ico16.gif"); //$NON-NLS-1$
                receive.setLabel(JSPMessages.getString("Menu.3")); //$NON-NLS-1$
                aux.setAccessKey('F');
                receive.setVisible(true);
                receive.setDisabled(false);
                receive.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=false&amp;embeddedProgram=true&amp;object=messageFax','std')"); //$NON-NLS-1$
                aux.addSubMenuItem(receive);
                
                receive = new MenuItem();
                receive.setId("newEmail"); //$NON-NLS-1$
                receive.setImgURL("resources/messageMail/ico16.gif"); //$NON-NLS-1$
                receive.setLabel(JSPMessages.getString("Menu.163")); //$NON-NLS-1$
                aux.setAccessKey('E');
                receive.setVisible(true);
                receive.setDisabled(false);
                receive.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=false&amp;embeddedProgram=true&amp;object=messageMail','std')"); //$NON-NLS-1$
                aux.addSubMenuItem(receive);

                receive = new MenuItem();
                receive.setId("newFax"); //$NON-NLS-1$
                receive.setImgURL("resources/messageLetter/ico16.gif"); //$NON-NLS-1$
                receive.setLabel(JSPMessages.getString("Menu.167")); //$NON-NLS-1$
                aux.setAccessKey('O');
                receive.setVisible(true);
                receive.setDisabled(false);
                receive.setOnClickCode("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp','?method=new&amp;masterdoc=true&amp;toSend=false&amp;embeddedProgram=true&amp;object=messageLetter','std')"); //$NON-NLS-1$
                aux.addSubMenuItem(receive);
                
            }
                                      
            aux.setVisible(true);
            aux.setDisabled(false);
            toRet.add(aux);
        }         
    }
    
    private static boolean onlyInReceive(boDefHandler bodef)
    {
        if("messageDelivered".equals(bodef.getName()) || "messageReceipt".equals(bodef.getName())) //$NON-NLS-1$ //$NON-NLS-2$
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
        aux.setId("buttonRefresh"); //$NON-NLS-1$
        aux.setImgURL(Browser.getThemeDir() + "mouseMenu/ghRefresh.gif"); //$NON-NLS-1$
        aux.setLabel(JSPMessages.getString("Menu.173")); //$NON-NLS-1$
        aux.setOnClickCode("refreshExplorer();"); //$NON-NLS-1$
        //aux.setOnClickCode("reloadGrid()");
        
        aux.setVisible(true);
        aux.setDisabled(false);
        aux.setAccessKey('A');
        toRet.add(aux);
        
        toRet.add(MenuItem.getHorizontalLineSeparator());

        //remover
        aux = new MenuItem();
        aux.setId("buttonRemove"); //$NON-NLS-1$
        aux.setImgURL(Browser.getThemeDir() + "menu/16_delete.gif"); //$NON-NLS-1$
        aux.setLabel(JSPMessages.getString("Menu.177")); //$NON-NLS-1$
        aux.setOnClickCode("alert('fazer a chamada para remover')"); //$NON-NLS-1$
        aux.setVisible(true);
        aux.setDisabled(false);
        aux.setAccessKey('R');
        toRet.add(aux);

        aux = new MenuItem();
        aux.setId("buttonPrint"); //$NON-NLS-1$
        aux.setImgURL(Browser.getThemeDir() + "menu/16_print.gif"); //$NON-NLS-1$
        aux.setLabel(JSPMessages.getString("Menu.181")); //$NON-NLS-1$
        aux.setVisible(true);
        aux.setDisabled(false);
        aux.setAccessKey('I');
        toRet.add(aux);
        
      
        
        MenuItem aux2 = new MenuItem();
        aux2.setId("buttonExpListNoOption"); //$NON-NLS-1$
        aux2.setImgURL(Browser.getThemeDir() + "menu/prev-right.gif"); //$NON-NLS-1$
        aux2.setOnClickCode("winmain().openDocUrl('fixed,730px,450px','__chooseExportExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')"); //$NON-NLS-1$
        aux2.setLabel(JSPMessages.getString("Menu.185")); //$NON-NLS-1$
        aux2.setVisible(true);
        aux2.setDisabled(false);

        aux.addSubMenuItem(aux2);

        aux2 = new MenuItem();
        aux2.setId("buttonImpListNoOption"); //$NON-NLS-1$
        aux2.setImgURL(Browser.getThemeDir() + "menu/prev-bott.gif"); //$NON-NLS-1$
        //aux2.setOnClickCode("winmain().open('__printExplorer.jsp?docid=$idx&viewer=$treeName','blank')");
        aux2.setOnClickCode("winmain().openDocUrl('medium','__printExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')"); //$NON-NLS-1$
        aux2.setLabel(JSPMessages.getString("Menu.189")); //$NON-NLS-1$
        aux2.setVisible(true);
        aux2.setDisabled(false);

        aux.addSubMenuItem(aux2);

        aux2 = new MenuItem();
        aux2.setId("buttonImpExpListWOption"); //$NON-NLS-1$
        aux2.setImgURL(Browser.getThemeDir() + "menu/prev-off.gif"); //$NON-NLS-1$
        aux2.setOnClickCode("winmain().openDocUrl('medium','__chooseExportExplorerData.jsp','?docid=$idx&treeKey=$treeName','lookup')"); //$NON-NLS-1$
        aux2.setLabel(JSPMessages.getString("Menu.193")); //$NON-NLS-1$
        aux2.setVisible(true);
        aux2.setDisabled(false);

        aux.addSubMenuItem(aux2);
        
        aux2 = new MenuItem();
        aux2.setId("buttonSaveListDef"); //$NON-NLS-1$
        aux2.setImgURL(Browser.getThemeDir() + "menu/prev-off.gif"); //$NON-NLS-1$
        aux2.setOnClickCode("winmain().openDocUrl('fixed,400px,150px,noresize','__saveExplorer.jsp','?docid=$idx&viewer=$treeName','lookup')"); //$NON-NLS-1$
        aux2.setLabel(JSPMessages.getString("Menu.197")); //$NON-NLS-1$
        aux2.setVisible(true);
        aux2.setDisabled(false);

        aux.addSubMenuItem(aux2);
        
        Menu m = new Menu(toRet, "explorerMouseMenu", "explorerMouseMenu"); //$NON-NLS-1$ //$NON-NLS-2$
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
                "<TD tabindex=\"0\" class=\"mnuOption\" onmouseover=\"menuItemOn()\" onmouseout=\"menuItemOff()\" onclick=\"setPreviewDown();\">setDown</TD>"); //$NON-NLS-1$

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

        MenuItem svItem2 = MenuItem.getSearchComponent("tree"); //$NON-NLS-1$
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
