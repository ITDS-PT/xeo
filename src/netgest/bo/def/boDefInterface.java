/*Enconding=UTF-8*/
package netgest.bo.def;

import java.util.HashSet;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.transformers.CastInterface;

import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Node;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public interface boDefInterface
{ 

    public String   getName();
    
    public abstract byte getInterfaceType();
    
    public String[] getExtImplObjects();
    
    public String[] getImplObjects();
    
    public boDefAttribute[] getImplAttributes();

    public boDefMethod[] getImplMethods();
    
    public boDefClsEvents[] getImplEvents();

    public String getImplName();

    public String getBoMasterTable();

    public String codeJavaScript(String viewerName);
    
    public  boDefAttribute[] getAttributesDef();
    
    public  boDefAttribute[] getAttributesDef(boolean includeSubClasses);

    public  boolean hasAttribute(String attributeName);

    public  boolean hasMethod(String methodName);

    public  boolean hasViewer(String viewerName);

    public  boDefViewer getViewer(String viewerName);

    public  boolean hasForm(String viewerName, String FormName);

    public  byte getAttributeType(String attributeName);

    public  boDefAttribute getAttributeRef(String attributeName);

    public  boDefAttribute getAttributeRef(String attributeName, boolean viewExtendedObject);
    
    public  String getBoLanguage();

    public  String getBoBirthPlace();

    public  String getBoName();

    public  String getBoDefaultLanguage();

    public  String getLabel();
    
    public  String          getDescription();

    public  String[]        getImplements();

    public  String          getCastToClassName();
    
    public  CastInterface   getCastToClass() throws boRuntimeException;

    public  String getSrcForIcon16();

    public  String getSrcForIcon32();

    public  boDefClsState getBoClsState();

    public  boDefAttribute[] getBoAttributes();

    public  boDefMethod[] getBoMethods();

    public  boDefClsEvents[] getBoClsEvents();

    public  String getBoExtendedTable();

    public  String getBoPhisicalMasterTable();
    
    public  String getBoDescription();

    public  String getBoMajorVersion();

    public  String getBoMinorVersion();
    
    public  boolean getBoIsSubBo();
    
    public  String getBoSuperBo();

    public  String getBoVersion();

    public  boDefViewer[] getBoViewers();

    public  boDefHandler[] getBoInterfaces();
    
    public  String[] getReferencedBy();
    
    public  String[] getReferences();
    
    public  boDefAttribute[] getReferencesAttributes();
    
    /**
     * 
     * Returns a list of all XEO Models implementing this interface
     * (both the ones in the XML of the interface as well as the ones
     * in the XML of the XEO MOdels)
     * 
     * @return A list of XEO Model names
     */
    public String[] getAllObjectNamesImplementingInterface();
    
    /**
     * 
     * Returns a list of all {@link boDefHandler} of XEO Models implementing this interface
     * (both the ones in the XML of the interface as well as the ones in the XML of the XEO MOdels)
     * 
     * @return A list of XEO Model names
     */
    public HashSet<boDefHandler> getAllObjectDefHandlersImplementingInterface();
    
    public abstract boolean getDataBaseManagerManageTables();

    public abstract boolean getDataBaseManagerManageViews();

    public abstract boolean getDataBaseManagerXeoCompatible();

}
