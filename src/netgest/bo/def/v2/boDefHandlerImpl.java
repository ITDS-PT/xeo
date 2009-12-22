/*Enconding=UTF-8*/
package netgest.bo.def.v2;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import netgest.bo.boConfig;
import netgest.bo.builder.boBuildDB;
import netgest.bo.def.boDefActivity;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefClsEvents;
import netgest.bo.def.boDefClsState;
import netgest.bo.def.boDefDatabaseObject;
import netgest.bo.def.boDefForwardObject;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefMethod;
import netgest.bo.def.boDefOPL;
import netgest.bo.def.boDefViewer;
import netgest.bo.impl.Ebo_TextIndexImpl;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boRuntimeException2;
import netgest.bo.transformers.CastInterface;
import netgest.bo.utils.SchemaUtils;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLNode;

import netgest.bo.system.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class boDefHandlerImpl extends boDefHandler
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.def.boDefHandler");

    private static Hashtable p_cachedef = new Hashtable();
    private static Hashtable p_invalidcachedef = new Hashtable();
    private static boDefHandler[] p_definitions = null;

    // NÃO ESQUECER DE INICIALIZAR AS VARIAVEIS NO MÉTODO REFRESH();
    private String p_name;
    private boDefAttribute[]    p_attributes;
    private boDefMethodImpl[]   p_methods;
    private boDefClsEvents[]    p_events;
    private Hashtable           p_eventsHash;
    private boDefViewer[]       p_viewers;
    private boDefHandler[]      p_implements;
    private boDefActivity[]     p_activities;
    private boDefClsState       p_clsstate;
    private boDefDatabaseObject[] p_dbobject;
    private String[]            p_referencedby;
    private String[]            p_references;
    private boDefAttribute[]    p_references_atts;
    private boDefHandler[]      p_subobjects;
    public boConfig             boconf;
    private boDefOPLImpl        p_OPL;
    private boolean             p_secureObjects;
//    private boDefObjectDS p_datasources;
    private String              p_bomastertable;
    
    private boolean             p_databaseManager_manageTables;
    private boolean             p_databaseManager_xeocompatible;
    private boolean             p_databaseManager_manageViews;
    private String 	 			p_databasemanager_class;
    
    private String              p_majorversion;
    private String              p_minorversion;
    private boDefHandler[]      p_allsubbos = null;
    private boDefForwardObject[] p_fwdObject = null;

    private ngtXMLHandler       xmlNode;

    private byte    p_type;
    private byte    p_interfaceType;
    
    private boolean p_orphan;
    private boolean p_multiparent;
    private String  p_extends;
    private String  p_extendsJavaClass;
    private String[] p_implementsJavaInterface;
    private String  p_label;
    private String  p_description;
    private String  p_cardId;
    
    private String  p_local_language;
    private boolean p_versioning;
    
    private String[] p_castTo;
    private String   p_castToClassName;
    
    private boolean     p_textIndex_active;
    private boolean     p_textIndex_appendChilds;
    private int         p_textIndex_deep;
    private Hashtable   p_textIndex_process;
    
    //flag que activa ou não a criação do campo SYS_FLDINPUTTYPE
    private boolean p_markInputType;
    
    public boDefHandlerImpl(Node node, boolean parse)
    {
        xmlNode = new ngtXMLHandler( node );
        xmlNode.goDocumentElement();

        if (parse)
        {
            refresh(false);
        }

        //p_name = xmlNode.getNodeName();
    }

    public boDefHandlerImpl(Node node, boolean parse, boolean isstate)
    {
        xmlNode = new ngtXMLHandler( node );
        xmlNode.goDocumentElement();

        if (parse)
        {
            refresh(isstate);
        }

        p_name = xmlNode.getNodeName();
    }
    
    public static void clearCacheImpl()
    {
        p_cachedef.clear();
        p_invalidcachedef.clear();
    }


    public static boDefHandler getBoDefinition(String boname)
    {
        return getBoDefinition(boname, true);
    }
    
    public static boolean existsObject( String sObjectName )
    {
        return getBoDefinition( sObjectName ) != null;
    }
    
    public static boDefHandlerImpl getBoDefinition(String boname, boolean useCache)
    {
        if (boname.length() == 0)
        {
            return null;
        }

        boDefHandlerImpl ret = ( boDefHandlerImpl )(useCache?p_cachedef.get(boname):null);
        
        if( useCache && p_invalidcachedef.containsKey( boname ) )
        {
            return null;
        }

        if (ret == null)
        {
            synchronized( boDefHandlerImpl.class )
            {
                if( useCache )
                {
                    ret = ( boDefHandlerImpl )(useCache?p_cachedef.get(boname):null);
                    if( ret != null )
                    {
                        return getBoDefinition( boname, useCache );
                    }
                }

                String file = boConfig.getDeploymentDir() + boname + ".xeomodel";
                File ofile = new File(file);
    
                if (ofile.exists())
                {
                    XMLDocument doc = ngtXMLUtils.loadXMLFile(file);
                    ret = new boDefHandlerImpl(doc, false);
                    p_cachedef.put(boname, ret);
                    ret.refresh();
                    if( ret.getClassType() == boDefHandler.TYPE_INTERFACE )
                    {
                        throw new RuntimeException("Object ["+boname+"] in bodef-deployment as wrong extension, must be .xeoimodel not xeomodel because is declared as a interface.");
                    }
                    if( !ret.getName().equalsIgnoreCase( boname ) )
                    {
                        throw new RuntimeException("Erro in object definition:Object filename does not match with node name in the XML. ["+boname+"!="+ret.getName()+"].");
                    }
                }
                else
                {
                    ret = (boDefHandlerImpl)boDefInterfaceImpl.getInterfaceDefinition(boname);
                    if(ret != null)
                    {
                        p_cachedef.put(boname, ret);
                        ret.refresh();
                    }
                    else
                    {
                        p_invalidcachedef.put( boname, Object.class );
                    }
                }
            }
        }

        return ret;
    }

    public static boDefHandler[] getAllBoDefinition()
    {
        int c = 0;
        boDefHandler[] xobjs = new boDefHandler[p_cachedef.size() - 1];
        boDefHandler bdh = null;
        Enumeration e = p_cachedef.elements();

        while (e.hasMoreElements())
        {
            bdh = (boDefHandler)e.nextElement();
            if (!"boObject".equals(bdh.getName()))
            {
                xobjs[c] = bdh;
                c++;
            }
        }

        Arrays.sort(xobjs, new boDefHandlerImpl.labelComparator());

        return xobjs;
    }

    public static boDefHandler[] listBoDefinitions()
    {
        java.io.File dir = new File(boConfig.getDeploymentDir());
        File[] files = dir.listFiles(new FileFilter()
                {
                    public boolean accept(File file)
                    {
                        return file.isFile() &&
                        file.getName().toLowerCase().endsWith(".xeomodel");
                    }
                });

        //boDefHandler[] defs = new boDefHandler[files.length];
        Vector defs = new Vector();

        for (short i = 0; i < files.length; i++)
        {
            boDefHandler xdef = boDefHandler.getBoDefinition(
                files[i].getName().substring(0,files[i].getName().indexOf(".xeomodel"))
            );
            if (xdef != null)
            {
                defs.add(xdef);
            }
        }

        boDefHandler[] ret = new boDefHandler[defs.size()];
        defs.toArray(ret);

        return ret;
    }

    public void refresh()
    {
        refresh(false);
    }

    public void refresh(boolean isstate)
    {
        ngtXMLHandler xnode = null;
        ngtXMLHandler[] xchilds = null;

        this.p_attributesMap = null;
        this.p_attributesMapLower = null;

        xnode = xmlNode.getChildNode("general");
        if (xnode != null)
        {
            p_name             = xnode.getAttribute("name"); 
            
            p_type             = GeneralParseUtils.parseType( xnode.getAttribute("type") );
            
            if( p_type == boDefHandler.TYPE_INTERFACE )
            {
                p_interfaceType    = GenericParseUtils.parseBoolean( xnode.getAttribute("optionalInterface","false") )?
                                     INTERFACE_OPTIONAL:INTERFACE_STANDARD;
            }
                                 
            p_bomastertable    = xnode.getAttribute("mastertable",p_name).toUpperCase();
            
            ngtXMLHandler 	   tableManagerXmlHdlr = xnode.getChildNode("databaseManager");
            if( tableManagerXmlHdlr != null ) {
            	p_databaseManager_manageTables  = Boolean.valueOf( tableManagerXmlHdlr.getAttribute("manageTables", "true" )).booleanValue();
            	p_databaseManager_manageViews   = Boolean.valueOf( tableManagerXmlHdlr.getAttribute("manageViews", "true" )).booleanValue();
            	p_databaseManager_xeocompatible = Boolean.valueOf( tableManagerXmlHdlr.getAttribute("xeoCompatible", "true" )).booleanValue();
            	p_databasemanager_class			= tableManagerXmlHdlr.getAttribute("dataManagerClass", "netgest.bo.data.XEODataManagerForLegacyTables" );
            }
            else {
            	p_databaseManager_manageTables = true;
            	p_databaseManager_manageViews = true;
            	p_databaseManager_xeocompatible = true;
            	p_databasemanager_class = null;
            }

            p_bomastertable    = xnode.getAttribute("mastertable",p_name).toUpperCase();
            
            p_orphan           = GenericParseUtils.parseBoolean( xnode.getAttribute("orphan", "true") );
            p_markInputType      = GenericParseUtils.parseBoolean( xnode.getAttribute("markInputType", "false") );
            p_multiparent      = GenericParseUtils.parseBoolean( xnode.getAttribute("multiparent") );
            p_extends          = xnode.getAttribute( "extends", "boObject".equals( p_name )?null:"boObject" );
            p_extendsJavaClass          = xnode.getAttribute("extendsJavaClass", null );
            
            String impl        = xnode.getAttribute("implementsJavaClass", null );
            if( impl != null )
            {
                p_implementsJavaInterface = new String[] { "Serializable", impl };
            }
            else
            {
                p_implementsJavaInterface = new String[] { "Serializable" };
            }
            
            p_description      = xnode.getChildNodeText("description", p_name );
            p_cardId           = xnode.getChildNodeText("cardID", p_description );
            p_label            = xnode.getChildNodeText("label", p_description );

            ngtXMLHandler localNode = xnode.getChildNode("locale");
            p_local_language        = localNode.getAttribute("language");
            
            // DATABASE
            xnode = xmlNode.getChildNode("general");
            xnode = xnode.getChildNode("database");
            if (xnode != null)
            {
                NodeList xlist = xnode.getNode().getChildNodes();
                p_dbobject = new boDefDatabaseObject[ xlist.getLength() ];
                for (byte i = 0; i < p_dbobject.length; i++)
                {
                    p_dbobject[i] = new boDefDatabaseObjectImpl(this, (Element)xlist.item(i));
                }
            }
            
            // IMPLEMENTS INTERFACES
            xnode = xmlNode.getChildNode("general");
            xnode = xnode.getChildNode("implements");
            if (xnode != null)
            {
                NodeList xlist = xnode.getNode().getChildNodes();
                Vector intefs = new Vector();
                for (byte i = 0; i < xlist.getLength(); i++)
                {
                    String name = ((XMLNode) xlist.item(i)).getText();
                    if (name != null)
                    {
                        boDefHandler defh = boDefHandler.getBoDefinition(name);
                        if (defh != null)
                        {
                            intefs.add(defh);
                        }
                    }
                }
                p_implements = (boDefHandlerImpl[])intefs.toArray(new boDefHandlerImpl[(intefs.size())]);
            }

            // IMPLEMENTS INTERFACES
            xnode = xmlNode.getChildNode("general");
            xnode = xnode.getChildNode("versioning");
            if (xnode != null)
            {
                p_versioning = GenericParseUtils.parseBoolean( xnode.getAttribute("active") );
            }
            
            
            // TEXT INDEX
            xnode = xmlNode.getChildNode("general");
            xnode = xnode.getChildNode("textIndex");
            if(xnode != null)
            {
                p_textIndex_active          = GenericParseUtils.parseBoolean( xnode.getAttribute("active") ); 
                p_textIndex_appendChilds    = GenericParseUtils.parseBoolean( xnode.getAttribute("appendChilds") );
                p_textIndex_deep            = GenericParseUtils.parseInt( xnode.getAttribute("deep") );
                
                xnode = xnode.getChildNode("process");
                if(xnode != null) 
                {
                    p_textIndex_process = new Hashtable();
                    ngtXMLHandler atts[] = xnode.getChildNodes();
                    for (int i = 0; i < atts.length; i++)
                    {
                        Object[] processProps = new Object[2];
                        processProps[0] = atts[i].getText();
                        processProps[1] = new Boolean(GenericParseUtils.parseBoolean(atts[i].getAttribute( "onlyCardID" , "false" )));
                        p_textIndex_process.put( processProps[0], processProps );
                    }
                }
                
            }
            
            
            // CAN CAST TO
            xnode = xmlNode.getChildNode("general");
            xnode = xnode.getChildNode("castTo");
            if( xnode != null )  
            {
                p_castToClassName = xnode.getAttribute( "class", "" );
                if (xnode != null)
                {
                    ngtXMLHandler[] childs =  xnode.getChildNodes();
                    if(childs != null)
                    {
                        p_castTo = new String[ childs.length ];
                        for (int i = 0; i < childs.length; i++) 
                        {
                            p_castTo[i] = childs[i].getText();
                        }
                    }
                }
            }
        }
        




        // opl
        xnode = xmlNode.getChildNode("opl");
        if (xnode != null)
        {
            p_secureObjects = GenericParseUtils.parseBoolean( xnode.getAttribute( "active" ) );
            if( p_secureObjects )
            {
                p_OPL = new boDefOPLImpl(this, xnode.getNode());
            }
        }


        // States
        xnode = xmlNode.getChildNode("states");
        if (xnode != null)
        {
            if (isstate)
            {
                if ((xnode != null) && (xnode.getFirstChild() != null))
                {
                    p_clsstate = new boDefClsStateImpl( this, xnode.getFirstChild().getNode(), null );
                }
            }
            else
            {
                String refers;
                if ((refers = xnode.getAttribute("refers", "")).length() > 0)
                {
                    p_clsstate = boDefClsStateImpl.loadClsStates(refers);
                }
                else
                {
                    if ((xnode != null) && (xnode.getFirstChild() != null))
                    {
                        p_clsstate = new boDefClsStateImpl(this, xnode.getNode().getFirstChild(), null);
                    }   
                }
            }
        }
        
        // Methods
        xnode = xmlNode.getChildNode("methods");
        if (xnode != null)
        {
            xchilds = xnode.getChildNodes();
            p_methods = new boDefMethodImpl[xchilds.length];
            for (int i = 0; i < xchilds.length; i++)
            {
                p_methods[i] = new boDefMethodImpl( this, xchilds[i].getNode() );
            }
            //p_methods = boDefMethodImpl.checkNativeMethods(p_methods, this);
        }
        
        // Events
        xnode = xmlNode.getChildNode("events");
        if (xnode != null)
        {
            xchilds = xnode.getChildNodes();
            p_events        = new boDefClsEventsImpl[xchilds.length];
            p_eventsHash    = new Hashtable( xchilds.length ) ;

            for (int i = 0; i < xchilds.length; i++)
            {
                p_events[i] = new boDefClsEventsImpl(this, xchilds[i].getNode());
                p_eventsHash.put( p_events[i].getEventName().substring(2).toUpperCase(), p_events[i] );
            }
        }
        
        // Forward Object
        xnode = xmlNode.getChildNode("fwdObjects");
        if (xnode != null)
        {
            
            xchilds = xnode.getChildNodes();
            p_fwdObject = new boDefForwardObject[xchilds.length];
            boDefHandlerImpl master = this;
            boDefHandlerImpl inheritFromMaster = this;
            
            for (int i = 0; i < xchilds.length; i++)
            {
            	String ineritFrom = xchilds[i].getAttribute("inheritfrom", ""); 
                if ( ineritFrom.length() > 0 && !"boObject".equals( ineritFrom ) )
                {
                    inheritFromMaster   = (boDefHandlerImpl) boDefHandlerImpl.getBoDefinition(xchilds[(i)].getAttribute("inheritfrom", ""));
                    inheritFromMaster   = inheritFromMaster == null ? master:inheritFromMaster;            
                    p_fwdObject[i]     = new boDefForwardObjectImpl( (boDefHandlerImpl)inheritFromMaster, xchilds[i].getNode() );
                }
                else
                {
                    p_fwdObject[i]      = new boDefForwardObjectImpl( master , xchilds[i].getNode() );
                }
            }
        }
        else
        {
            p_fwdObject = new boDefForwardObject[0];
        }


        xnode = xmlNode.getChildNode("attributes");
        if (xnode != null)
        {
            xchilds = xnode.getChildNodes();
            byte plus = 0;
            
            boDefClsState[] xstates = null;
            if (p_clsstate != null)
            {
                xstates = (boDefClsState[])p_clsstate.getChildStateAttributes();
                plus = (byte) xstates.length;
            }
            
            p_attributes = new boDefAttributeImpl[xchilds.length + plus];
            boDefHandlerImpl master = this;
            boDefHandlerImpl inheritFromMaster = this; 
            for (int i = 0; i < xchilds.length; i++)
            {
            	String ineritFrom = xchilds[i].getAttribute("inheritfrom", ""); 
                if ( ineritFrom.length() > 0 && !"boObject".equals( ineritFrom ) )
                {
                    inheritFromMaster = (boDefHandlerImpl)boDefHandlerImpl.getBoDefinition(xchilds[(i)].getAttribute("inheritfrom", ""));
                    inheritFromMaster = inheritFromMaster == null ? master:inheritFromMaster;            
                    p_attributes[i] = new boDefAttributeImpl( inheritFromMaster, xchilds[i].getNode() );
                }
                else
                {
                    p_attributes[i] = new boDefAttributeImpl( master, xchilds[i].getNode() );
                }
            }

            for (int i = 0; i < plus; i++)
            {
                p_attributes[xchilds.length + i] = (boDefClsStateImpl)xstates[i];
            }
        }


        xnode = xmlNode.getChildNode("viewers");

        if (xnode != null)
        {
            xchilds = xnode.getChildNodes();
            p_viewers = new boDefViewer[xchilds.length];

            for (int i = 0; i < xchilds.length; i++)
            {
                p_viewers[i] = new boDefViewerImpl(this, xchilds[i].getNode());
            }
        }

        xnode = xmlNode.getChildNode("activities");

        if (xnode != null)
        {
            ngtXMLHandler[] xx = xnode.getChildNodes();

            ArrayList xactvs = new ArrayList();

            for (byte i = 0; i < xx.length; i++)
            {
                ngtXMLHandler[] cact = xx[i].getChildNodes();
                String statename = xx[i].getNodeName();

                if (this.getBoClsState() != null)
                {
                    boDefClsState[] states = this.getBoClsState()
                                                 .getChildStateAttributes();
                    boDefClsStateImpl state = null;
                    boDefClsStateImpl stateatt = null;

                    for (byte z = 0;
                            (states != null) && (state == null) &&
                            (z < states.length); z++)
                    {
                        state = (boDefClsStateImpl)states[z].getChildState(statename);
                        stateatt = (boDefClsStateImpl)states[z];
                    }

                    for (byte z = 0; z < cact.length; z++)
                    {
                        if (state != null)
                        {
                            xactvs.add(new boDefActivityImpl(this,
                                    cact[z].getNode(), state, stateatt,
                                    statename));
                        }
                        else
                        {
                            xactvs.add(new boDefActivityImpl(this,
                                    cact[z].getNode(), null, null, statename));
                        }
                    }

                    p_activities = (boDefActivity[]) xactvs.toArray(new boDefActivity[xactvs.size()]);
                }
                else
                {
                    // to avoid null pointer exceptions.
                    p_activities = new boDefActivityImpl[0];
                }
            }
        }
    }
    
    public boDefAttribute[] getAttributesDef()
    {
        return p_attributes;
    }
    
    
    public boDefForwardObject[] getForwardObjects()
    {
        if(p_fwdObject == null)
        {
            ngtXMLHandler xnode = null;
            ngtXMLHandler[] xchilds = null;
            xnode = xmlNode.getChildNode("fwdObjects");
    
            if (xnode != null)
            {
                xchilds = xnode.getChildNodes();
    
                p_fwdObject = new boDefForwardObject[xchilds.length];
                boDefHandlerImpl master = this;
                boDefHandlerImpl inheritFromMaster = this;
                for (int i = 0; i < xchilds.length; i++)
                {
                	String ineritFrom = xchilds[i].getAttribute("inheritfrom", ""); 
                    if ( ineritFrom.length() > 0 && !"boObject".equals( ineritFrom ) )
                    {
                        inheritFromMaster = (boDefHandlerImpl)boDefHandlerImpl.getBoDefinition(xchilds[(i)].getAttribute("inheritfrom", ""));
                        inheritFromMaster = inheritFromMaster == null ? master:inheritFromMaster;            
                        p_attributes[i] = new boDefAttributeImpl( (boDefHandlerImpl)inheritFromMaster, xchilds[i].getNode() );
                    }
                    else
                    {
                        p_fwdObject[i] = new boDefForwardObjectImpl(master, xchilds[i].getNode());
                    }
                }
            }
            else
            {
                p_fwdObject = new boDefForwardObject[0];
            }
        }
        return p_fwdObject;
    }

    public boolean hasFwdMethods()
    {
        if(p_fwdObject == null)
        {
            getForwardObjects();
        }
        return p_fwdObject.length > 0; 
    }

    public boDefAttribute[] getAttributesDef(boolean includeSubClasses)
    {
        if (!includeSubClasses)
        {
            return p_attributes;
        }
        else
        {
            boDefHandler[] defs = this.getTreeSubClasses();
            Vector allattrib = new Vector();
            HashSet namesattr = new HashSet();

            for (int i = 0; i < p_attributes.length; i++)
            {
                namesattr.add(p_attributes[i].getName());
                allattrib.add(p_attributes[i]);
            }

            for (int i = 0; i < defs.length; i++)
            {
                boDefAttribute[] a = defs[i].getAttributesDef();

                for (int j = 0; j < a.length; j++)
                {
                    if (!namesattr.contains(a[j].getName()))
                    {
                        namesattr.add(a[j].getName());
                        allattrib.add(a[j]);
                    }
                }
            }

            return (boDefAttribute[]) allattrib.toArray(new boDefAttribute[allattrib.size()]);
        }
    }
    
    public boolean hasAttribute(String attributeName)
    {
        for (int i = 0; i < p_attributes.length; i++)
        {
            if (p_attributes[i].getName().equalsIgnoreCase(attributeName))
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasMethod(String methodName)
    {
        for (int i = 0; i < p_methods.length; i++)
        {
            if (p_methods[i].getName().equals(methodName))
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasEvent( String eventName )
    {
        for (int i = 0; i < p_events.length; i++)
        {
            if (p_events[i].getEventName().equals( eventName ))
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasViewer(String viewerName)
    {
        for (int i = 0; i < p_viewers.length; i++)
        {
            if (p_viewers[i].getViewerName().equalsIgnoreCase(viewerName))
            {
                return true;
            }
        }

        return false;
    }

    public boDefViewer getViewer(String viewerName)
    {
        for (int i = 0; i < p_viewers.length; i++)
        {
            if (p_viewers[i].getViewerName().equalsIgnoreCase(viewerName))
            {
                return p_viewers[i];
            }
        }

        return null;
    }

    public boolean implementsSecurityRowObjects()
    {
        return p_secureObjects;
    }

    public boDefDatabaseObject[] getBoDatabaseObjects()
    {
        return p_dbobject;
    }

    public boolean hasForm(String viewerName, String FormName)
    {
        for (int i = 0; i < p_viewers.length; i++)
        {
            if (p_viewers[i].getViewerName().equalsIgnoreCase(viewerName))
            {
                return p_viewers[i].HasForm(FormName);
            }
        }

        return false;
    }

    public byte getAttributeType(String attributeName)
    {
        for (int i = 0; i < p_attributes.length; i++)
        {
            if (p_attributes[i].getName().equalsIgnoreCase(attributeName))
            {
                return p_attributes[i].getAtributeType();
            }
        }

        return -1;
    }

    private Map p_attributesMapLower;
    private Map p_attributesMap;
    
    public boDefAttribute getAttributeRef(String attributeName)
    {
    	if( attributeName != null ) {
	    	if( p_attributesMap == null ) {
	    		synchronized( this ) {
	    			p_attributesMap = new HashMap();
	    			p_attributesMapLower = new HashMap();
	    			for (int i = 0; i < p_attributes.length; i++)
	    	        {
	    				p_attributesMap.put( p_attributes[i].getName(), p_attributes[ i ]);
	    				p_attributesMapLower.put( p_attributes[i].getName().toLowerCase(), p_attributes[ i ]);
	    	        }
	    		}
	    	}
	    	
	    	boDefAttribute ret = (boDefAttribute)p_attributesMap.get( attributeName );
	    	if( ret == null ) {
	        	ret = (boDefAttribute)p_attributesMapLower.get( attributeName.toLowerCase() );
	    	}
	        return ret;
    	}
	    return null;
    }

    public boDefAttribute getAttributeRef(String attributeName,
        boolean viewExtendedObject)
    {
        String[] names = attributeName.split("\\.");
        boDefAttribute[] attrs = p_attributes;

        if (viewExtendedObject)
        {
            attrs = this.getAttributesDef(true);
        }

        for (int z = 0; z < names.length; z++)
        {
            for (int i = 0; i < attrs.length; i++)
            {
                if (attrs[i].getName().equalsIgnoreCase(names[z]))
                {
                    if ((z + 1) < names.length)
                    {
                        String subatr = "";

                        for (int j = z + 1; j < names.length; j++)
                        {
                            subatr += names[j];

                            if ((j + 1) < names.length)
                            {
                                subatr += ".";
                            }
                        }

                        boDefHandler od1 = attrs[i].getReferencedObjectDef();

                        if (od1 != null)
                        {
                            return od1.getAttributeRef(subatr,
                                viewExtendedObject);
                        }
                        else
                        {
                            return null;
                        }
                    }
                    else
                    {
                        return attrs[i];
                    }
                }
            }
        }

        return null;
    }

    public String getBoLanguage()
    {
        return p_local_language;
    }

    public String getBoBirthPlace()
    {
        return this.getBoDefaultLanguage();
    }

    public String getBoName()
    {
        return p_name;
    }

    public String getBoDefaultLanguage()
    {
        return p_local_language;
    }

    public String getLabel()
    {
        return p_label;
    }

    public String getModifyProtocol()
    {
        String ret = null;
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("modifyProtocol");

        if (labnode != null)
        {
            if (labnode.getText() != null)
            {
                if (labnode.getText().length() > 0)
                {
                    ret = labnode.getText();
                }
            }
        }

        return ret;
    }

    public boolean getVersioning()
    {
        return p_versioning;
    }

    public String getDescription()
    {
        return p_description;
    }

    public String getCARDID()
    {
        return p_cardId;
    }

    public String[] getImplements()
    {
        String[] ret = null;
        if( p_implements != null )
        {
            ret = new String[ p_implements.length ];
            for (int i = 0;i < p_implements.length; i++ )
            {
                ret[ i ] = p_implements[i].getName();
            }
        }
        return ret;
    }

    public String[] canCastTo()
    {
        return p_castTo;
    }

    public String getCastToClassName()
    {
        return p_castToClassName;
    }
    
    public CastInterface getCastToClass() throws boRuntimeException
    {
        try
        {
            Class c = Class.forName(getCastToClassName());
            return (CastInterface)c.newInstance();
        }
        catch (InstantiationException e)
        {
            logger.severe("Transformer não identificado (" + getCastToClassName() + ")");
            throw new boRuntimeException2("Transformer não identificado (" + getCastToClassName() + ")");
        }
        catch (IllegalAccessException e)
        {
            logger.severe("Transformer não identificado (" + getCastToClassName() + ")");
            throw new boRuntimeException2("Transformer não identificado (" + getCastToClassName() + ")");
        }
        catch (ClassNotFoundException e)
        {
            logger.severe("Transformer não identificado (" + getCastToClassName() + ")");
            throw new boRuntimeException2("Transformer não identificado (" + getCastToClassName() + ")");
        }
    }


    public String getSrcForIcon16()
    {
        return "resources/" + this.getName() + "/ico16.gif";
    }

    public String getSrcForIcon32()
    {
        return "resources/" + this.getName() + "/ico32.gif";
    }

    public boDefClsState getBoClsState()
    {
        return p_clsstate;
    }

    public boDefAttribute[] getBoAttributes()
    {
        return this.p_attributes;
    }

    public boDefMethod[] getBoMethods()
    {
        return this.p_methods;
    }

    public boDefClsEvents getBoClsEvent( String name )
    {
        if( p_eventsHash != null )
        {
            return (boDefClsEvents)p_eventsHash.get( name );
        }
        return null;
    }

    public boDefClsEvents[] getBoClsEvents()
    {
        return this.p_events;
    }

    public boDefOPL getBoOPL()
    {
        return p_OPL;
    }

    public boDefActivity[] getBoClsActivities()
    {
        return this.p_activities;
    }

    public boDefMethod getBoMethod(String xmethod, Class[] assinature)
    {
        boDefMethod ret = null;

        for (byte i = 0; i < this.p_methods.length; i++)
        {
            if (this.p_methods[i].getName().equalsIgnoreCase(xmethod) &&
                    boDefMethodImpl.compareMethodAssinature(
                        this.p_methods[i].getAssinatureClasses(), assinature))
            {
                ret = this.p_methods[i];

                break;
            }
        }
        return ret;
    }

    public boDefMethod getBoMethod(String xmethod)
    {
        boDefMethod ret = null;

        for (byte i = 0; i < this.p_methods.length; i++)
        {
            if (this.p_methods[i].getName().equalsIgnoreCase(xmethod))
            {
                ret = this.p_methods[i];

                break;
            }
        }
        return ret;
    }

    public String getBoExtendedTable()
    {
    	String ret;
    	if( getDataBaseManagerXeoCompatible() ) {
	        if(getClassType()==TYPE_INTERFACE)
	          ret = "O"+this.getName();
	        else
	          ret = "OE"+this.getName();
    	} else {
    		ret =  p_bomastertable;
    	}
        if (ret.length() <= 30)
        {
            return ret;
        }
        else
        {
            return boBuildDB.encodeObjectName_25(ret);
        }
    }

    
    public String getBoMasterTable()
    {
    	String ret;
    	
    	if( getDataBaseManagerXeoCompatible() ) {
    		ret = "O" + this.getName();
    	}
    	else {
    		ret = p_bomastertable;
    	}
    	
        if (ret.length() <= 30)
        {
            return ret;
        }
        else
        {
            return boBuildDB.encodeObjectName_25(ret);
        }
    }

    public String getBoPhisicalMasterTable()
    {
        return p_bomastertable;
    }
    
	public boolean getDataBaseManagerManageTables() {
		return p_databaseManager_manageTables;
	}

	public boolean getDataBaseManagerManageViews() {
		return p_databaseManager_manageViews;
	}

	public boolean getDataBaseManagerXeoCompatible() {
		return p_databaseManager_xeocompatible;
	}

	public String  getDataBaseManagerClassName() {
		return p_databasemanager_class;
	}
	
	public String getWordTemplate()
    {
        String ret = null;
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("templates");

        if (labnode != null)
        {            
            ret = (labnode.getChildNode("msword")).getText();
        }
        return ret;
    }

    public byte getASPMode()
    {
        if (SchemaUtils.definedAsSemiPrivate(getName()))
        {
            return ASP_SEMI_PRIVATE;
        }

        if (SchemaUtils.definedAsPrivate(getName()))
        {
            return ASP_PRIVATE;
        }

        String aux = xmlNode.getChildNode("General").getAttribute("aspMode", "");

        if ("PRIVATE".equalsIgnoreCase(aux))
        {
            return ASP_PRIVATE;
        }

        if ("GLOBAL".equalsIgnoreCase(aux))
        {
            return ASP_GLOBAL;
        }

        if ("SEMI-PRIVATE".equalsIgnoreCase(aux))
        {
            return ASP_SEMI_PRIVATE;
        }

        if ("CONTROLLER".equalsIgnoreCase(aux))
        {
            return ASP_CONTROLLER;
        }

        return ASP_GLOBAL;
    }

    public String getBoDescription()
    {
        return p_description;
    }

    public String getStateNameRefer()
    {
        ngtXMLHandler x = xmlNode.getChildNode("states");

        if (x != null)
        {
            return x.getAttribute("refers", "");
        }
        else
        {
            return "";
        }
    }

    public boolean getBoHaveMultiParent()
    {
        return p_multiparent;
    }
    
    public boolean getBoMarkInputType()
    {
        return p_markInputType;
    }

    public boolean getBoCanBeOrphan()
    {
        return p_orphan;
    }

    public String getBoMajorVersion()
    {
        if (p_majorversion == null)
        {
            p_majorversion = xmlNode.getChildNode("General").getChildNodeText("cls_majorVersion",
                    "1");
        }

        return p_majorversion;
    }

    public String getBoMinorVersion()
    {
        if (p_minorversion == null)
        {
            p_minorversion = xmlNode.getChildNode("General").getChildNodeText("cls_minorVersion",
                    "0");
        }

        return p_minorversion;
    }

    public boolean getBoIsSubBo()
    {
        return p_extends != null && !"boObject".equals( p_extends );
    }

    public String getBoSuperBo()
    {
        return p_extends;
    }

    public String getBoVersion()
    {
        return getBoMajorVersion() + "." + getBoMinorVersion();
    }

    public boDefViewer[] getBoViewers()
    {
        return p_viewers;
    }

    // Not implemented in this context
    public String getChildFieldName()
    {
        return null;
    }

    // Not implemented in this context
    public String getFatherFieldName()
    {
        return null;
    }

    // Not implemented in this context
    public String getType()
    {
        return null;
    }

    // Not implemented in this context
    public String getDbName()
    {
        return null;
    }

    public String getName()
    {
        return this.getBoName();
    }

    public String getBoExtendsClass()
    {
        return p_extendsJavaClass;
    }
    
    public String[] getBoImplementsJavaInterfaces()
    {
        return p_implementsJavaInterface;
    }

    public boDefHandler[] getBoInterfaces()
    {
        return p_implements;
    }

    public boolean getBoImplements(String interfacename)
    {
        for (byte i = 0; (p_implements != null) && (i < p_implements.length);
                i++)
        {
            if (p_implements[i].getBoName().equals(interfacename))
            {
                return true;
            }
        }
        return false;
    }

    public boDefHandler[] getBoSubClasses()
    {
        // Build Array with extended classes
        if (this.p_subobjects == null)
        {
            Vector xsub = new Vector();

            //boDefHandler[] alldefs = boDefHandler.listBoDefinitions();
            if (netgest.bo.def.v2.boDefHandlerImpl.p_definitions == null)
            {
                netgest.bo.def.v2.boDefHandlerImpl.p_definitions = (boDefHandlerImpl[])boDefHandler.listBoDefinitions();
            }

            for (short i = 0; i < netgest.bo.def.v2.boDefHandlerImpl.p_definitions.length; i++)
            {
                if ((boDefHandlerImpl.p_definitions[i].getBoSuperBo() != null) &&
                        boDefHandlerImpl.p_definitions[i].getBoSuperBo().equals(this.getBoName()))
                {
                    xsub.add(boDefHandlerImpl.p_definitions[i]);
                }
            }

            p_subobjects = new boDefHandlerImpl[xsub.size()];
            xsub.toArray(p_subobjects);
        }
        return this.p_subobjects;
    }

    private Vector getBoAllSubClasses()
    {
        // Build Vector with extended classes
        Vector xsub = new Vector();

        if (netgest.bo.def.v2.boDefHandlerImpl.p_definitions == null)
        {
            netgest.bo.def.v2.boDefHandlerImpl.p_definitions = (boDefHandler[])boDefHandler.listBoDefinitions();
        }

        for (short i = 0; i < netgest.bo.def.v2.boDefHandlerImpl.p_definitions.length; i++)
        {
            if ((boDefHandlerImpl.p_definitions[i].getBoSuperBo() != null) &&
                    boDefHandlerImpl.p_definitions[i].getBoSuperBo().equals(this.getBoName()))
            {
                xsub.add(boDefHandlerImpl.p_definitions[i]);

                Vector x = ((boDefHandlerImpl)netgest.bo.def.v2.boDefHandlerImpl.p_definitions[(i)]).getBoAllSubClasses();

                for (int j = 0; j < x.size(); j++)
                {
                    xsub.add(x.get(j));
                }
            }
        }

        return xsub;
    }
    
    public boDefHandler[] getTreeSubClasses()
    {
        return getTreeSubClasses(false);
    }

    public boDefHandler[] getTreeSubClasses(boolean noCache)
    {
        if (p_allsubbos == null || noCache)
        {
            Vector vect = getBoAllSubClasses();
            p_allsubbos = (boDefHandler[]) vect.toArray(new boDefHandler[vect.size()]);
        }

        return p_allsubbos;
    }

    public String[] getReferencedBy()
    {
        if (this.p_referencedby == null)
        {
            boDefHandler[] alldefs = boDefHandler.listBoDefinitions();
            Vector refs = new Vector();

            for (short k = 0; k < alldefs.length; k++)
            {
                if (alldefs[k].getBoName().equals(this.getBoName()))
                {
                    continue;
                }

                boDefAttribute[] a_att = alldefs[k].getBoAttributes();

                for (short i = 0; i < a_att.length; i++)
                {
                    if (a_att[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                    {
                        if (a_att[i].getBridge().haveBridgeAttributes())
                        {
                            boDefAttribute[] b_batt = a_att[i].getBridge()
                                                              .getBoAttributes();

                            for (int z = 0; z < b_batt.length; z++)
                            {
                                if ((b_batt[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                        !a_att[i].getReferencedObjectName()
                                                     .equals("boObject") &&
                                        !b_batt[z].getReferencedObjectName()
                                                      .equals(this.getBoName()))
                                {
                                    if (b_batt[z].getReferencedObjectDef() != null)
                                    {
                                        refs.add(b_batt[z].getReferencedObjectName());
                                    }
                                }
                            }
                        }

                        if (!a_att[i].getReferencedObjectName().equals(this.getBoName()) &&
                                !a_att[i].getReferencedObjectName().equals("boObject"))
                        {
                            if (a_att[i].getReferencedObjectDef() != null)
                            {
                                refs.add(a_att[i].getReferencedObjectName());
                            }
                        }
                    }
                }
            }

            p_referencedby = new String[refs.size()];
            refs.toArray(p_referencedby);
        }

        return this.p_referencedby;
    }

    public String[] getReferences()
    {
        // Build array to compute all refereces of this object
        if (p_references == null)
        {
            Vector refs = new Vector();
            boDefAttribute[] a_att = this.getBoAttributes();

            for (short i = 0; i < a_att.length; i++)
            {
                if (a_att[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                {
                    if ((a_att[i].getBridge() != null) &&
                            a_att[i].getBridge().haveBridgeAttributes())
                    {
                        boDefAttribute[] b_batt = a_att[i].getBridge()
                                                          .getBoAttributes();

                        for (int z = 0; z < b_batt.length; z++)
                        {
                            if (b_batt[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                            {
                                if (!a_att[i].getReferencedObjectName().equals("boObject") &&
                                        !a_att[i].getReferencedObjectName()
                                                     .equals(this.getBoName()) &&
                                        (b_batt[z].getReferencedObjectDef() != null))
                                {
                                    refs.add(b_batt[z]);
                                }
                            }
                        }
                    }

                    if (!a_att[i].getReferencedObjectName().equals("boObject") &&
                            !a_att[i].getReferencedObjectName().equals(this.getBoName()) &&
                            (a_att[i].getReferencedObjectDef() != null))
                    {
                        refs.add(a_att[i]);
                    }
                }
            }

            p_references = new String[refs.size()];
            p_references_atts = new boDefAttribute[refs.size()];
            refs.toArray(p_references_atts);

            for (short i = 0; i < p_references_atts.length; i++)
            {
                p_references[i] = p_references_atts[i].getReferencedObjectName();
            }
        }

        return p_references;
    }

    public boDefAttribute[] getReferencesAttributes()
    {
        if (p_references_atts == null)
        {
            this.getReferences();
        }

        return p_references_atts;
    }

    public byte getClassType()
    {
        return p_type;
    }
    
    public byte getInterfaceType()
    {
        return p_interfaceType;
    }
    
    
    public boolean isMandatoryInterface()
    {
        return getClassType() == TYPE_INTERFACE;
    }

    public String codeJavaScript(String viewerName)
    {
        return codeJavaScriptBeforeMethod(viewerName) +
        codeJavaScriptAfterMethod(viewerName);
    }

    private String codeJavaScriptAfterMethod(String viewerName)
    {
        boDefMethod[] method = getBoMethods();
        StringBuffer sb = new StringBuffer();
        String javaScript;
        String cabecalho = ("function runAfterMethodExec()\n{\n");
        if (method.length > 0)
        {
            for (int i = 0; i < method.length; i++)
            {
                javaScript = method[i].getJavaScriptToRunAfter(viewerName);

                if ((javaScript != null) && !"".equals(javaScript))
                {
                    sb.append("        if (methodName == \"")
                      .append(method[i].getName()).append("\")\n        {\n")
                      .append("            ").append(javaScript).append(";")
                      .append("\n        }\n");
                }
            }

            if (sb.length() > 0)
            {
                StringBuffer aux = new StringBuffer();
                aux.append(cabecalho);
                aux.append("    if(getIDX() == null) return;\n")
                   .append("    if(winmain().ndl[getIDX()].lastMethodToRun != null &&\n")
                   .append("            winmain().ndl[getIDX()].lastMethodToRun &&\n")
                   .append("            winmain().ndl[getIDX()].lastMethodToRun != \"\")\n    {\n")
                   .append("        var methodName = winmain().ndl[getIDX()].lastMethodToRun;\n")
                   .append(sb).append("    }\n");
                aux.append("    setLastMethodToRun(\"\");\n");
                aux.append("}\n");

                return aux.toString();
            }
        }
        return cabecalho + "setLastMethodToRun(\"\");}\n";
    }

    private String codeJavaScriptBeforeMethod(String viewerName)
    {
        boDefMethod[] method = getBoMethods();
        String javaScript;
        StringBuffer sb = new StringBuffer();
        sb.append("function runBeforeMethodExec(methodName)\n{\n");

        if (method.length > 0)
        {
            for (int i = 0; i < method.length; i++)
            {
                javaScript = method[i].getJavaScriptToRunBefore(viewerName);

                if ((javaScript != null) && !"".equals(javaScript))
                {
                    sb.append("    if (methodName == \"")
                      .append(method[i].getName()).append("\")\n        {\n")
                      .append("            ").append(javaScript).append(";")
                      .append("\n        }\n");
                }
            }
        }

        sb.append("}\n");

        return sb.toString();
    }


    /**
     * Add childs attributes to textIndex? and how deep?
     * 
     * @see Ebo_TextIndexImpl
	 * @return deep
     */
    public int getIfIndexChilds()
    {
        return p_textIndex_deep;
    }
    
    public boolean getIndexProcessChild(String attName)
    {
        if( this.p_textIndex_appendChilds ) 
        {
            return p_textIndex_process != null && p_textIndex_process.containsKey( attName );
        }
        return false;
    }
    
    public boolean indexOnlyCardID(String attName)
    {
        if( this.p_textIndex_appendChilds ) 
        {
            if ( p_textIndex_process != null && p_textIndex_process.containsKey( attName ) )
            {
                Object[] props = (Object[])p_textIndex_process.get( attName );
                return ((Boolean)(props[1])).booleanValue();
            }
        }
        return false;
    }
    
    
    public boolean isTextIndexActive()
    {
		return p_textIndex_active;
    }

    public String getObjectBinaryClass()
    {
        String result = getSpecificClass("binaryClass");
        if(result == null)
        {
            result = "netgest.bo.runtime.specific.ObjectBinaryImpl";
        }
        return result;        
    }    
    public String getObjectResClass()
    {    
        String result = getSpecificClass("resClass");
        if(result == null)
        {
            result = "netgest.bo.runtime.specific.ObjectResImpl";
        }
        return result;
    }
    public String getObjectVersionControlClass()
    {    
        String result = getSpecificClass("versionControlClass");
        if(result == null)
        {
            result = "netgest.bo.runtime.specific.ObjectVersionControlImpl";
        }
        return result;
    }    
    private String getSpecificClass(String attrName)
    {
        String result = null;
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("specific");        
        if (labnode != null && (labnode.getChildNode(attrName)) != null)
        {
            result = labnode.getChildNode(attrName).getText();
        }     
        return result;
    }
    public boolean isSearchableWhithTextIndex()
    {    
        boolean result = true;
        String text = null;
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("search");
        if (labnode != null)
        {
            text = labnode.getAttribute( "textIndex",null);
            if(text != null && !"".equals(text) && ("n".equalsIgnoreCase(text) || "no".equalsIgnoreCase(text)))
            {
                result = false;
            }
        }
        return result;
    }       
    public boolean haveVersionControl()
    {
        boolean result = false;        
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("versionControl");
        if (labnode != null)
        {
            String active = labnode.getAttribute( "active",null);        
            if(active != null &&
               ("y".equalsIgnoreCase("y") || "yes".equalsIgnoreCase("yes") || "true".equalsIgnoreCase("true")))
            {
                result = true;       
            }
        }
        return result;
    }


    public ngtXMLHandler getXmlNode()
    {
        return xmlNode;
    }

    private static class labelComparator implements Comparator
    {
        public final int compare(Object a, Object b)
        {
            return ((String) ((boDefHandler) a).getLabel()).compareTo((String) ((boDefHandler) b).getLabel());
        }
    }

    public Node getNode()
    {
        return xmlNode.getNode();
    }

    public ngtXMLHandler getChildNode( String nodeName )
    {
        return xmlNode.getChildNode( nodeName );
    }

    public Document getDocument()
    {
        return xmlNode.getDocument();
    }

    public String getReferencedState()
    {
        ngtXMLHandler xmlstate = xmlNode.getChildNode("ClsStates");
        if ((xmlstate != null)  )
        {
            return xmlstate.getAttribute("refers", "");
        }
        return null;
    }
    
    // Obsoletas, a rever
    public ngtXMLHandler getPath(String xpath)
    {
        String[] xp = xpath.split("\\.");
        ngtXMLHandler toRet = xmlNode.getChildNode(xp[0]);
        ngtXMLHandler pToRet;
        for (int i = 1; i < xp.length; i++)
        {
            // Substituição automatica devido ao novo formato do XML
            if( xp[i].equalsIgnoreCase("general") ) xp[i]="viewer";
            pToRet = toRet;
            if( toRet == null )
            {
                throw new boRuntimeException2("Error viewer ["+xpath+"] not found in object ["+this.getName()+"]");
            }
            toRet = toRet.getChildNode(xp[i]);
            // Se não encontrar o filho então tenta pelo attributo name.
            if( toRet == null )
            {
                ngtXMLHandler childs[] = pToRet.getChildNodes();
                for (int z = 0;childs!= null && z < childs.length; z++) 
                {
                    String name = childs[z].getAttribute( "name" );
                    if( xp[i].equalsIgnoreCase( name ) )
                    {
                        toRet = childs[z];
                    }
                }
            }
        }
        if( toRet == null )
        {
            
        }
        return toRet;
    }

    
}
