package netgest.bo.presentation.render.elements;

import netgest.utils.ngtXMLHandler;
import netgest.bo.def.boDefHandler;

public class ExplorerOptions 
{
    // Menu
    private boolean menuOptionsVisible = true;
    private boolean newOptionVisible = true;
    
    // permite activar ou dasactivar o botão new do explorer
    private boolean newSubClassOptionVisible = true;
    
    private boolean newFromTemplateOptionVisible = true;
    private boolean printOptionVisible = true;
    private boolean exportOptionVisible = true;
    private boolean savePrintOptionVisible = true;
    private boolean destroyOptionVisible = true;
    private boolean groupsOptionVisible = true;
    private boolean parametersOptionVisible = true;
    private boolean refreshOptionVisible = true;
    private boolean fullTextSearchOptionVisible = true;
    private boolean advancedSearchOptionVisible = true;
    private boolean savedFiltersOptionVisible = true;
    private boolean previewRightOptionVisible = true;
    private boolean previewBottomOptionVisible = true;
    private boolean previewDisableOptionVisible = true;
    private boolean chooseColumnsOptionVisible = true;
    private boolean sortColumnsOptionVisible = true; 
    private boolean saveExplorerOptionVisible = true;
    private boolean listSavedExplorerOptionVisible = true;
    // psantos ini
    // permite colocar visivel ou invisivel cada item do menu do botão new do explorer
    private boDefHandler[]  newSubClassOptionObjects ;
    // permite saber qual é o valor por defeito para o botão novo
    private boolean clickableForNewButton = true;
    private String defaultValueForNewButton = null;
    private boolean objectsVisible = true;
    // psantos fim
    
    
    //Zone
    private boolean groupsZoneVisible = true;
    private boolean parametersZoneVisible = true;
    private boolean previousZoneVisible = true;
    private String previousZone = null;
    private boolean columnsHeadersZoneVisible = true;
    
    //Beahvior
    private String doubleClickCode = null;
    private boolean doubleClickActionDisable = false;
    private boolean selectActionDisable = false;
    private boolean showLinks = true;
    private boolean openDocuments = false;
    
    public ExplorerOptions(ngtXMLHandler treeUserdef)
    {
        getOptions(treeUserdef);
    }
    private void getOptions(ngtXMLHandler treeUserdef)
    {
        ngtXMLHandler explorerOptions = null;
        if(treeUserdef != null)
        {
             explorerOptions = treeUserdef.getChildNode("explorerOptions"); 
             if(explorerOptions != null)
             {
                 getMenuOptions(explorerOptions.getChildNode("menuOptions"));
                 getDefaultState(explorerOptions.getChildNode("defaultState"));
                 getBehavior(explorerOptions.getChildNode("behavior"));
             }
        }        
    }
    private void getMenuOptions(ngtXMLHandler handler)
    {
        ngtXMLHandler option = null;
        String optionValue = null;
        if(handler != null)
        {
            menuOptionsVisible = isValueFalse(handler.getAttribute("visible"));
            if(menuOptionsVisible)
            {
                newOptionVisible = isVisible(handler,"newOption");
                newSubClassOptionVisible = isVisible(handler,"newSubClassOption");
                
                // psantos ini
                ngtXMLHandler hdlnewSubClassOption = handler.getChildNode("newSubClassOption");
                if (hdlnewSubClassOption != null)
                {
                    clickableForNewButton = !isValueFalse(hdlnewSubClassOption.getAttribute("defaultObject"));
                    defaultValueForNewButton = hdlnewSubClassOption.getAttribute("defaultObject");
                    ngtXMLHandler hdlObjects = hdlnewSubClassOption.getChildNode("objects");
                    if (hdlObjects != null) 
                    {
                        ngtXMLHandler[] objects = hdlObjects.getChildNodes();
                        newSubClassOptionObjects = new boDefHandler[objects.length];
                        
                        for (int i=0; i<objects.length; i++) 
                        {
                            boDefHandler objectDef = boDefHandler.getBoDefinition( objects[i].getAttribute("name") );
                            if (objectDef != null)
                                newSubClassOptionObjects[i] = objectDef;
                        }
                        
                    }
                }
                objectsVisible = isVisible(handler,"objectsVisible");
                // psantos fim
                
                newFromTemplateOptionVisible = isVisible(handler,"newFromTemplateOption");
                printOptionVisible = isVisible(handler,"printOption");
                exportOptionVisible = isVisible(handler,"exportOption");
                savePrintOptionVisible = isVisible(handler,"savePrintOption");
                destroyOptionVisible = isVisible(handler,"destroyOption");
                groupsOptionVisible = isVisible(handler,"groupsOption");
                parametersOptionVisible = isVisible(handler,"parametersOption");
                refreshOptionVisible = isVisible(handler,"refreshOption");
                fullTextSearchOptionVisible = isVisible(handler,"fullTextSearchOption");
                advancedSearchOptionVisible = isVisible(handler,"advancedSearchOption");
                savedFiltersOptionVisible = isVisible(handler,"savedFiltersOption");
                previewRightOptionVisible = isVisible(handler,"previewRightOption");
                previewBottomOptionVisible = isVisible(handler,"previewBottomOption");
                previewDisableOptionVisible = isVisible(handler,"previewDisableOption");
                chooseColumnsOptionVisible = isVisible(handler,"chooseColumnsOption");
                sortColumnsOptionVisible = isVisible(handler,"sortColumnsOption");
                saveExplorerOptionVisible = isVisible(handler,"saveExplorerOption");
                listSavedExplorerOptionVisible = isVisible(handler,"listSavedExplorerOption");
            }
            else
            {
                newOptionVisible = false;
                newSubClassOptionVisible = false;
                newFromTemplateOptionVisible = false;
                printOptionVisible = false;
                exportOptionVisible = false;
                savePrintOptionVisible = false;
                destroyOptionVisible = false;
                groupsOptionVisible = false;
                parametersOptionVisible = false;
                refreshOptionVisible = false;
                fullTextSearchOptionVisible = false;
                advancedSearchOptionVisible = false;
                savedFiltersOptionVisible = false;
                previewRightOptionVisible = false;
                previewBottomOptionVisible = false;   
                previewDisableOptionVisible = false; 
                chooseColumnsOptionVisible = false;
                saveExplorerOptionVisible = false;
                listSavedExplorerOptionVisible = false;
                sortColumnsOptionVisible = false;
                // psantos ini
                objectsVisible = false;
                // psantos fim
            }
        }
    }
    private void getDefaultState(ngtXMLHandler handler)
    {
        ngtXMLHandler option = null;
        String optionValue = null;
        if(handler != null)
        {
            groupsZoneVisible = isVisible(handler,"groupsZone");
            parametersZoneVisible = isVisible(handler,"parametersZone");
            previousZoneVisible = isVisible(handler,"previousZone");
            previousZone = getValue(handler,"previousZone","zone");
            columnsHeadersZoneVisible = isVisible(handler,"columnsHeadersZone");
        }      
    }   
    private void getBehavior(ngtXMLHandler handler)
    {
        ngtXMLHandler option = null;
        String optionValue = null;
        if(handler != null)
        {
            doubleClickCode = getUserCode(handler, "userDoubleClickCode");
            if(doubleClickCode != null && doubleClickCode.trim().length() == 0 )
                doubleClickCode = null;     
            doubleClickActionDisable = isDisable(handler,"doubleClickAction");
            selectActionDisable = isDisable(handler,"selectAction");
            showLinks = isShowLinkEnabled(handler, "showLinks");
            openDocuments = isOpenDocumentsEnabled(handler, "openDocuments");
        }      
    }   
    
    
    private static boolean isVisible(ngtXMLHandler handler, String node)
    {
        return isValueFalse(getValue(handler,node,"visible"));
    }
    private static boolean isDisable(ngtXMLHandler handler, String node)
    {
        return !isValueFalse(getValue(handler,node,"disable"));
    }
    private static boolean isOpenDocumentsEnabled(ngtXMLHandler handler, String node)
    {
        boolean result = false;
        ngtXMLHandler option = handler.getChildNode(node);
        if(option != null)
        {        
            String value = option.getText() ;
            if("true".equalsIgnoreCase(value))
            {
                result = true;
            }
        }        
        return result;
    }
    private static String getUserCode(ngtXMLHandler handler, String node)
    {
        boolean result = false;
        ngtXMLHandler option = handler.getChildNode(node);
        if(option != null)
        {        
            return option.getText();
        }        
        return null;
    }
    
    private static boolean isShowLinkEnabled(ngtXMLHandler handler, String node)
    {
        boolean result = true;
        ngtXMLHandler option = handler.getChildNode(node);
        if(option != null)
        {        
            String value = option.getText() ;
            if("false".equalsIgnoreCase(value))
            {
                result = false;
            }
        }        
        return result;
    }
    private static String getValue(ngtXMLHandler handler, String node,String attribute)
    {
        String result = null;
        ngtXMLHandler option = handler.getChildNode(node);
        if(option != null)
        {        
            result = option.getAttribute(attribute);
        }        
        return result;
    }    
    private static boolean isValueFalse(String value)
    {
        boolean result = true;
        if(value != null && !"".equals(value))
        {
            if("false".equalsIgnoreCase(value))
            {
                result = false;
            }
        }
        return result;
    }
    
    //******************************************************************
    //Selectores da classe
    //******************************************************************  
    public boolean isMenuOptionsVisible()
    {
        return menuOptionsVisible;
    }
    public boolean isNewOptionVisible()
    {
        return newOptionVisible;
    }
    public boolean isNewSubClassOptionVisible()
    {
        return newSubClassOptionVisible;
    }
    public boolean isNewFromTemplateOptionVisible()
    {
        return newFromTemplateOptionVisible;
    }
    public boolean isPrintOptionVisible()
    {
        return printOptionVisible;
    }
    public boolean isExportOptionVisible()
    {
        return exportOptionVisible;
    }
    public boolean isDestroyOptionVisible()
    {
        return destroyOptionVisible;
    }
    public boolean isGroupsOptionVisible()
    {
        return groupsOptionVisible;
    }
    public boolean isParametersOptionVisible()
    {
        return parametersOptionVisible;
    }
    public boolean isRefreshOptionVisible()
    {
        return refreshOptionVisible;
    }
    public boolean isFullTextSearchOptionVisible()
    {
        return fullTextSearchOptionVisible;
    }
    public boolean isSavedFiltersOptionVisible()
    {
        return savedFiltersOptionVisible;
    }
    public boolean isPreviewRightOptionVisible()
    {
        return previewRightOptionVisible;
    }
    public boolean isPreviewBottomOptionVisible()
    {
        return previewBottomOptionVisible;
    }
    public boolean isGroupsZoneVisible()
    {
        return groupsZoneVisible;
    }
    public boolean isParametersZoneVisible()
    {
        return parametersZoneVisible;
    }
    public boolean isPreviousZoneVisible()
    {
        return previousZoneVisible;
    }
    public boolean isAdvancedSearchOptionVisible()
    {
        return advancedSearchOptionVisible;
    }
    public boolean isPreviewDisableOptionVisible()
    {
        return previewDisableOptionVisible;
    }
    public boolean isSavePrintOptionVisible()
    {
        return savePrintOptionVisible;
    }
    public String getPreviousZone()
    {
        return previousZone;
    }
    public boolean isChooseColumnsOptionVisible()
    {
        return chooseColumnsOptionVisible;
    }

    public boolean isSortColumnsOptionVisible()
    {
        return sortColumnsOptionVisible;
    }
    
    public boolean isColumnsHeadersZoneVisible()
    {
        return columnsHeadersZoneVisible;
    }
    public boolean isSaveExplorerOptionVisible()
    {
        return saveExplorerOptionVisible;
    }
    public boolean isListSavedExploredOptionVisible()
    {
        return listSavedExplorerOptionVisible;
    }
    public boolean isDoubleClickActionDisable()
    {
        return doubleClickActionDisable;
    }
    public String getDoubleClickActionCode()
    {
        return doubleClickCode;
    }
    public boolean isSelectActionDisable()
    {
        return selectActionDisable;
    }
    public boolean showLinks()
    {
        return showLinks;
    }
    public boolean openDocuments()
    {
        return openDocuments;
    }
    // psantos ini
    public boolean isObjectsVisible()
    {
        return objectsVisible;
    }
    
    public boolean newButtonClickable()
    {
        return clickableForNewButton;
    }
    
    
    // selector do atributo newSubClassOptionObjects
    public boDefHandler[] getNewSubClassOptionObjects()
    {
        return newSubClassOptionObjects;
    }
    
    // selector do atributo defaultValueForNewButton
    public String getDefaultValueForNewButton()
    {
        return defaultValueForNewButton;
    }
    // psantos fim
}