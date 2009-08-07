/*Enconding=UTF-8*/
package netgest.bo.def;

import netgest.bo.def.boDefViewer;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.transformers.CastInterface;

import netgest.utils.ngtXMLHandler;

import oracle.xml.parser.v2.XMLDocument;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
//import netgest.bo.builder.plugins.boDefObjectDS;


public abstract class boDefHandler implements boDef
{
    public static final byte TYPE_CLASS = 0;
    public static final byte TYPE_ABSTRACT_CLASS = 1;
    public static final byte TYPE_INTERFACE = 2;
    
    public static final byte INTERFACE_NO = 0;
    public static final byte INTERFACE_STANDARD = 1;
    public static final byte INTERFACE_OPTIONAL = 2;
    
    public static final byte ASP_PRIVATE = 0;
    public static final byte ASP_SEMI_PRIVATE = 1;
    public static final byte ASP_GLOBAL = 2;
    public static final byte ASP_CONTROLLER = 3;
    
    public static final int  VERSION_1 = 0;
    public static final int  VERSION_2 = 1;
    public static int VERSION = VERSION_2;
    
    
    public static boDefHandler getBoDefinition( String boName )
    {
        return getBoDefinition( boName, VERSION );
    }
    public static boDefHandler getBoDefinition( String boName, int version )
    {
        switch( version )
        {
            case VERSION_1:
                return netgest.bo.def.v1.boDefHandlerImpl.getBoDefinition( boName );
            case VERSION_2:
                return netgest.bo.def.v2.boDefHandlerImpl.getBoDefinition( boName );
            default:
                return null;
        }
    }
    
    public static boDefHandler[] listBoDefinitions()
    {
        switch( VERSION )
        {
            case VERSION_1:
                return netgest.bo.def.v1.boDefHandlerImpl.listBoDefinitions();
            case VERSION_2:
                return netgest.bo.def.v2.boDefHandlerImpl.listBoDefinitions();
            default:
                return null;
        }
    }
    
    public static boDefHandler[] getAllBoDefinition()
    {
        switch( VERSION )
        {
            case VERSION_1:
                return netgest.bo.def.v1.boDefHandlerImpl.getAllBoDefinition();
            case VERSION_2:
                return netgest.bo.def.v2.boDefHandlerImpl.getAllBoDefinition();
            default:
                return null;
        }
    }
    
    public static boDefInterface getInterfaceDefinition( String interfaceName )
    {
        switch( VERSION )
        {
            case VERSION_1:
                return netgest.bo.def.v1.boDefInterfaceImpl.getInterfaceDefinition( interfaceName );
            case VERSION_2:
                return netgest.bo.def.v2.boDefInterfaceImpl.getInterfaceDefinition( interfaceName );
            default:
                return null;
        }
    }

    public static boDefClsState getClsState( String classStatename )
    {
        switch( VERSION )
        {
            case VERSION_1:
                return netgest.bo.def.v1.boDefClsStateImpl.loadClsStates( classStatename );
            case VERSION_2:
                return netgest.bo.def.v2.boDefClsStateImpl.loadClsStates( classStatename );
            default:
                return null;
        }
    }
    
    public static boDefHandler loadFromXml( XMLDocument doc )
    {
        return loadFromXml( doc, VERSION );
    }

    public static boDefHandler loadFromXml( XMLDocument doc, int version )
    {
        switch( version )
        {
            case VERSION_1:
                return new netgest.bo.def.v1.boDefHandlerImpl(doc,true);
            case VERSION_2:
                return new netgest.bo.def.v2.boDefHandlerImpl(doc,true);
            default:
                return null;
        }
    }

    public static boDefHandler loadInterfaceFromXml( String name, XMLDocument doc )
    {
        switch( VERSION )
        {
            case VERSION_1:
                return new netgest.bo.def.v1.boDefInterfaceImpl(name, doc );
            case VERSION_2:
                return new netgest.bo.def.v2.boDefInterfaceImpl(name, doc );
            default:
                return null;
        }
    }

    
    public static void clearCache()
    {
        switch( VERSION )
        {
            case VERSION_1:
                netgest.bo.def.v1.boDefHandlerImpl.clearCacheImpl();
                break;
            case VERSION_2:
                netgest.bo.def.v2.boDefHandlerImpl.clearCacheImpl();
                break;
            default:
                break;
        }
    }
    
    public static void clearStatesCache()
    {
        switch( VERSION )
        {
            case VERSION_1:
                netgest.bo.def.v1.boDefClsStateImpl.clearCache();
                break;
            case VERSION_2:
                netgest.bo.def.v2.boDefClsStateImpl.clearCache();
                break;
            default:
                break;
        }
    }

    public static void clearInterfacesCache()
    {
        switch( VERSION )
        {
            case VERSION_1:
                netgest.bo.def.v1.boDefInterfaceImpl.clearCache();
                break;
            case VERSION_2:
                netgest.bo.def.v2.boDefInterfaceImpl.clearCache();
                break;
            default:
                break;
        }
    }

    public abstract void     refresh();

    public abstract boDefAttribute[] getAttributesDef();
    
    public abstract boDefForwardObject[] getForwardObjects();

    public abstract boolean hasFwdMethods();

    public abstract boDefAttribute[] getAttributesDef(boolean includeSubClasses);

    public abstract ngtXMLHandler getPath(String xpath);

    public abstract boolean hasAttribute(String attributeName);

    public abstract boolean hasMethod(String methodName);

    public abstract boolean hasViewer(String viewerName);

    public abstract boolean hasEvent(String eventName);

    public abstract boDefViewer getViewer(String viewerName);

    public abstract boolean implementsSecurityRowObjects();

    public abstract boDefDatabaseObject[] getBoDatabaseObjects();

    public abstract boolean hasForm(String viewerName, String FormName);

    public abstract byte getAttributeType(String attributeName);

    public abstract boDefAttribute getAttributeRef(String attributeName);

    public abstract boDefAttribute getAttributeRef(String attributeName, boolean viewExtendedObject);
    
    public abstract String getBoLanguage();

    public abstract String getBoBirthPlace();

    public abstract String getBoName();

    public abstract String getBoDefaultLanguage();

    public abstract String getLabel();
    
    public abstract String getModifyProtocol();
    
    public abstract boolean getVersioning();

    public abstract String getDescription();

    public abstract String getCARDID();

    public abstract String[] getImplements();

    public abstract String[] canCastTo();
    
    public abstract String getCastToClassName();
    
    public abstract CastInterface getCastToClass() throws boRuntimeException;

    public abstract String getSrcForIcon16();

    public abstract String getSrcForIcon32();

    public abstract boDefClsState getBoClsState();

    public abstract boDefAttribute[] getBoAttributes();

    public abstract boDefMethod[] getBoMethods();

    public abstract boDefClsEvents[] getBoClsEvents();

    public abstract boDefClsEvents getBoClsEvent( String name );

    public abstract boDefOPL getBoOPL();

    public abstract boDefActivity[] getBoClsActivities();

    public abstract boDefMethod getBoMethod(String xmethod, Class[] assinature);
    
    public abstract boDefMethod getBoMethod(String xmethod);

    public abstract String getBoExtendedTable();

    public abstract String getBoMasterTable();
    
    public abstract String getBoPhisicalMasterTable();
    
    public abstract boolean getDataBaseManagerManageTables();

    public abstract boolean getDataBaseManagerManageViews();

    public abstract boolean getDataBaseManagerXeoCompatible();
    
	public abstract String 	getDataBaseManagerClassName();
    
    public abstract String getWordTemplate();
    
    public abstract byte getASPMode();
    
    public abstract String getBoDescription();

    public abstract String getStateNameRefer();

    public abstract boolean getBoMarkInputType();
    
    public abstract boolean getBoHaveMultiParent();

    public abstract boolean getBoCanBeOrphan();
    
    public abstract String getBoMajorVersion();

    public abstract String getBoMinorVersion();
    
    public abstract boolean getBoIsSubBo();
    
    public abstract String getBoSuperBo();

    public abstract String getBoVersion();

    public abstract boDefViewer[] getBoViewers();

    // Not implemented in this context
    public abstract String getChildFieldName();

    // Not implemented in this context
    public abstract String getFatherFieldName();
    
    // Not implemented in this context
    public abstract String getType();
    // Not implemented in this context
    public abstract String getDbName();

    public abstract String getName();

    public abstract String getBoExtendsClass();
    
    public abstract String[] getBoImplementsJavaInterfaces();
    
    public abstract boDefHandler[] getBoInterfaces();
    
    public abstract boolean getBoImplements(String interfacename);

    public abstract boDefHandler[] getBoSubClasses();
    
    public abstract boDefHandler[] getTreeSubClasses();

    public abstract boDefHandler[] getTreeSubClasses(boolean noCache);

    public abstract String[] getReferencedBy();
    
    public abstract String[] getReferences();
    
    public abstract boDefAttribute[] getReferencesAttributes();

    public abstract byte getClassType();
    
    public abstract byte getInterfaceType();
    
    public abstract String codeJavaScript(String viewerName);

    /**
     * Add childs attributes to textIndex? and how deep?
     * 
     * @see Ebo_TextIndexImpl
	 * @return deep
     */
    public abstract int getIfIndexChilds();
    
    public abstract boolean getIndexProcessChild(String attName);
    
    public abstract boolean indexOnlyCardID(String attName);
    
    public abstract String getObjectBinaryClass();
    
    public abstract String getObjectResClass();
    
    public abstract String getObjectVersionControlClass();
    
    public abstract boolean isSearchableWhithTextIndex();
    
    public abstract boolean isTextIndexActive();
    
    public abstract boolean haveVersionControl();
    
    public abstract Node getNode();
    
    public abstract String   getReferencedState();
    
    public abstract ngtXMLHandler   getChildNode( String name );
    public abstract Document        getDocument();

}
