/*Enconding=UTF-8*/
package netgest.bo.def.v1;

import java.io.File;
import java.io.FileFilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import netgest.bo.boConfig;
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
import netgest.bo.def.v1.boDefAttributeImpl;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boRuntimeException2;
import netgest.bo.transformers.CastInterface;
import netgest.bo.utils.SchemaUtils;

import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLNode;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import netgest.bo.builder.plugins.boDefObjectDS;


public class boDefHandlerImpl extends boDefHandler
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.def.boDefHandler");

    public boolean getDataBaseManagerManageTables() {
		return false;
	}

	public boolean getDataBaseManagerManageViews() {
		return false;
	}

	public boolean getDataBaseManagerXeoCompatible() {
		return false;
	}

	private static Hashtable 		p_cachedef = new Hashtable();
    private static boDefHandler[] 	p_definitions = null;

    // NÃO ESQUECER DE INICIALIZAR AS VARIAVEIS NO MÉTODO REFRESH();
    private String  				p_name;
    private byte    				p_interfaceType;
    private boDefAttribute[] 		p_attributes;
    private boDefMethodImpl[] 		p_methods;
    private boDefClsEvents[] 		p_events;
    private boDefViewer[] 			p_viewers;
    private boDefHandler[] 			p_implements;
    private boDefActivity[] 		p_activities;
    private boDefClsState 			p_clsstate;
    private boDefDatabaseObject[] 	p_dbobject;
    private String[] 				p_referencedby;
    private String[] 				p_references;
    private boDefAttribute[] 		p_references_atts;
    private boDefHandler[] 			p_subobjects;
    public boConfig 				boconf;
    private boDefOPLImpl 			p_OPL;
    private boolean 				p_secureObjects;
    private String 					p_bomastertable;
    private String 					p_majorversion;
    private String 					p_minorversion;
    private Boolean 				p_indexValue = null;
    private boDefHandler[] 			p_allsubbos = null;
    private boDefForwardObject[] 	p_fwdObject = null;
    private ngtXMLHandler 			xmlNode;
    
    public boDefHandlerImpl(Node node, boolean parse)
    {
        xmlNode = new ngtXMLHandler( node );
        xmlNode.goDocumentElement();

        if (parse)
        {
            refresh(false);
        }

        p_name = xmlNode.getNodeName();
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
    }


    public synchronized static boDefHandler getBoDefinition(String boname)
    {
        return getBoDefinition(boname, true);
    }
    
    public String[] getBoImplementsJavaInterfaces()
    {
        return new String[] { "Serializable" };
    }
    
    public synchronized static boDefHandlerImpl getBoDefinition(String boname, boolean useCache)
    {
        if (boname.length() == 0)
        {
            return null;
        }

        boDefHandlerImpl ret = useCache ? (boDefHandlerImpl)p_cachedef.get(boname):null;

        if (ret == null)
        {
            String file = boConfig.getDeploymentDir() + boname + "$bo" +
                ".xml";
            File ofile = new File(file);

            if (ofile.exists())
            {
                XMLDocument doc = ngtXMLUtils.loadXMLFile(file);
                ret = new boDefHandlerImpl(doc, false);
                p_cachedef.put(boname, ret);
                ret.refresh();
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
            bdh = (boDefHandler) e.nextElement();

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
                        file.getName().toLowerCase().endsWith("$bo.xml");
                    }
                });

        //boDefHandler[] defs = new boDefHandler[files.length];
        Vector defs = new Vector();

        for (short i = 0; i < files.length; i++)
        {
            boDefHandler xdef = boDefHandler.getBoDefinition(files[i].getName()
                                                                     .substring(0,
                        files[i].getName().indexOf("$bo.xml")));

            if (xdef != null)
            {
                defs.add(xdef);
            }
        }

        boDefHandler[] ret = new boDefHandler[defs.size()];
        defs.toArray(ret);

        return ret;
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
                    if (xchilds[i].getAttribute("inheritfrom", "").length() > 0)
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

    public void refresh()
    {
        refresh(false);
    }

    public void refresh(boolean isstate)
    {
        p_name = null;
        p_attributes = null;
        p_attributes = null;
        p_methods = null;
        p_events = null;
        p_viewers = null;
        p_implements = null;
        p_activities = null;
        p_clsstate = null;
        p_dbobject = null;
        p_referencedby = null;
        p_references = null;
        p_references_atts = null;
        p_subobjects = null;
        boconf = null;
        p_OPL = null;
        p_secureObjects = false;
//        p_datasources = null;
        p_bomastertable = null;
        p_majorversion = null;
        p_minorversion = null;
        p_allsubbos = null;
        p_fwdObject = null;

        ngtXMLHandler xnode = null;
        ngtXMLHandler[] xchilds = null;

        xnode = xmlNode.getChildNode("ClsStates");

        if (xnode != null)
        {
            if (isstate)
            {
                if ((xnode != null) && (xnode.getFirstChild() != null))
                {
                    p_clsstate = new boDefClsStateImpl(this,
                            xnode.getFirstChild().getNode(), null);
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
                        p_clsstate = new boDefClsStateImpl(this,
                                xnode.getFirstChild().getNode(), null);
                    }
                }
            }
        }

        xnode = xmlNode.getChildNode("Attributes");

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
                if (xchilds[i].getAttribute("inheritfrom", "").length() > 0)
                {
                    inheritFromMaster = (boDefHandlerImpl)boDefHandlerImpl.getBoDefinition(xchilds[(i)].getAttribute("inheritfrom", ""));
                    inheritFromMaster = inheritFromMaster == null ? master:inheritFromMaster;            
                    p_attributes[i] = new boDefAttributeImpl(inheritFromMaster,
                            xchilds[i].getNode());
                }
                else
                {
                    p_attributes[i] = new boDefAttributeImpl(master,
                            xchilds[i].getNode());
                }
            }

            for (int i = 0; i < plus; i++)
            {
                p_attributes[xchilds.length + i] = (boDefClsStateImpl)xstates[i];
            }
        }

        xnode = xmlNode.getChildNode("general");

        if (xnode != null)
        {
            xnode = xnode.getChildNode("database");

            if (xnode != null)
            {
                NodeList xlist = xnode.getNode().getChildNodes();
                p_dbobject = new boDefDatabaseObject[xlist.getLength()];

                for (byte i = 0; i < p_dbobject.length; i++)
                {
                    p_dbobject[i] = new boDefDatabaseObjectImpl(this, xlist.item(i));
                }
            }
            
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

            xnode = xmlNode.getChildNode("general");
            xnode = xnode.getChildNode("business_implements");

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

        xnode = xmlNode.getChildNode("methods");

        if (xnode != null)
        {
            xchilds = xnode.getChildNodes();
            p_methods = new boDefMethodImpl[xchilds.length];

            for (int i = 0; i < xchilds.length; i++)
            {
                p_methods[i] = new boDefMethodImpl(this, xchilds[i].getNode());
            }

            p_methods = boDefMethodImpl.checkNativeMethods(p_methods, this);
        }

        xnode = xmlNode.getChildNode("Events");

        if (xnode != null)
        {
            xchilds = xnode.getChildNodes();
            p_events = new boDefClsEventsImpl[xchilds.length];

            for (int i = 0; i < xchilds.length; i++)
            {
                p_events[i] = new boDefClsEventsImpl(this, xchilds[i].getNode());
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

        xnode = xmlNode.getChildNode("general");

        String secureObjects = xnode.getAttribute("implementsSecurityRowObjects",
                "no");

        if (secureObjects.equalsIgnoreCase("yes"))
        {
            p_secureObjects = true;
        }
        else
        {
            p_secureObjects = false;
        }

        if (p_secureObjects)
        {
            xnode = xmlNode.getChildNode("OPL");

            if (xnode != null)
            {
                p_OPL = new boDefOPLImpl(this, xnode.getNode());
            }
        }
    }

    public ngtXMLHandler getPath(String xpath)
    {
        String[] xp = xpath.split("\\.");
        ngtXMLHandler toRet = xmlNode.getChildNode(xp[0]);

        for (int i = 1; i < xp.length; i++)
        {
            toRet = toRet.getChildNode(xp[i]);
        }

        return toRet;
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

    public boDefAttribute getAttributeRef(String attributeName)
    {
        for (int i = 0; i < p_attributes.length; i++)
        {
            if (p_attributes[i].getName().equalsIgnoreCase(attributeName))
            {
                return p_attributes[i];
            }
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
        return this.getBoDefaultLanguage();
    }

    public String getBoBirthPlace()
    {
        return this.getBoDefaultLanguage();
    }

    public String getBoName()
    {
        if (p_name == null)
        {
            p_name = xmlNode.getNodeName();
        }

        //return xmlNode.getNodeName();
        return p_name;
    }

    public String getBoDefaultLanguage()
    {
        return xmlNode.getChildNode("General").getAttribute("nacionalityDefault");
    }

    public String getLabel()
    {
        String ret = null;
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("label");

        if ((labnode.getChildNode(this.getBoLanguage())) != null)
        {
            ret = (labnode.getChildNode(this.getBoLanguage())).getText();
        }
        else if (labnode.getChildNode(this.getBoDefaultLanguage()) != null)
        {
            ret = (labnode.getChildNode(this.getBoDefaultLanguage())).getText();
        }
        if( ret == null )
        {
            ret = "";
        }

        return ret;
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
        boolean ret = false;
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("versioning");

        if (labnode != null)
        {
            if (labnode.getText() != null)
            {
                if (labnode.getText().equalsIgnoreCase("Y"))
                {
                    ret = true;
                }
            }
        }

        return ret;
    }

    public String getDescription()
    {
        String ret = null;
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("description");

        if ((labnode.getChildNode(this.getBoLanguage())) != null)
        {
            ret = (labnode.getChildNode(this.getBoLanguage())).getText();
        }
        else if (labnode.getChildNode(this.getBoDefaultLanguage()) != null)
        {
            ret = (labnode.getChildNode(this.getBoDefaultLanguage())).getText();
        }

        return ret;
    }

    //    public byte getObjectType() {
    //        byte ret=this.TYPE_PASSIVE;
    //        String atr = xmlNode.getChildNode("General").getAttribute("bo_type","");
    //        if ( atr.equalsIgnoreCase("ACTIVE") ) ret=this.TYPE_ACTIVE;
    //        return ret;      
    //    }
    public String getCARDID()
    {
        String ret = null;
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("cardID");

        if (labnode != null)
        {
            if ((labnode.getChildNode(this.getBoLanguage())) != null)
            {
                ret = (labnode.getChildNode(this.getBoLanguage())).getText();
            }
            else if (labnode.getChildNode(this.getBoDefaultLanguage()) != null)
            {
                ret = (labnode.getChildNode(this.getBoDefaultLanguage())).getText();
            }
        }
        else
        {
            logger.warn("cardID for: " + this.getName() + " not defined");
        }

        return ret;
    }

    public String[] getImplements()
    {
        String[] ret = null;
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("implements");

        if (labnode != null)
        {
            ngtXMLHandler[] childs =  labnode.getChildNodes();
            if(childs != null)
            {
                ret = new String[childs.length];
                for (int i = 0; i < childs.length; i++) 
                {
                    ret[i] = childs[i].getText();
                }
            }
        }
        return ret;
    }

    public String[] canCastTo()
    {
        String[] ret = null;
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("castTo");

        if (labnode != null)
        {
            ngtXMLHandler[] childs =  labnode.getChildNodes();
            if(childs != null)
            {
                ret = new String[childs.length];
                for (int i = 0; i < childs.length; i++) 
                {
                    ret[i] = childs[i].getText();
                }
            }
        }
        return ret;
    }

    public String getCastToClassName()
    {
        ngtXMLHandler labnode = xmlNode.getChildNode("General").getChildNode("castTo");

        if (labnode != null)
        {
            return labnode.getAttribute("class", "");
        }
        return "";
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
            logger.fatal("Transformer não identificado (" + getCastToClassName() + ")");
            throw new boRuntimeException2("Transformer não identificado (" + getCastToClassName() + ")");
        }
        catch (IllegalAccessException e)
        {
            logger.fatal("Transformer não identificado (" + getCastToClassName() + ")");
            throw new boRuntimeException2("Transformer não identificado (" + getCastToClassName() + ")");
        }
        catch (ClassNotFoundException e)
        {
            logger.fatal("Transformer não identificado (" + getCastToClassName() + ")");
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

    public boDefClsEvents[] getBoAllClsEvents()
    {
        return p_events;
    }

    public boDefClsEvents getBoClsEvent( String name )
    {
        return null;
    }
    public boDefClsEvents[] getBoClsEvents()
    {
        Vector toRet = new Vector();
        
        for (int i = 0;p_events != null && i < p_events.length; i++) 
        {
            if( p_events[i].getEventName().indexOf(".") == -1 )
            {
                toRet.add( p_events[i] );                
            }
        }
        if( toRet.size() > 0 )
        return (boDefClsEvents[])toRet.toArray( new boDefClsEvents[ toRet.size() ] );
        return null;
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

        ;

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

        ;

        return ret;
    }

//    public boDefObjectDS getBoDataSources()
//    {
//        if (p_datasources == null)
//        {
//            p_datasources = boDefObjectDS.getDataSourceDefinition(this);
//        }
//
//        return p_datasources;
//    }

    public String getBoExtendedTable()
    {
        /* if( p_bomastertable == null )
         {
             p_bomastertable = xmlNode.getChildNode( "General").getAttribute("mastertable", this.getBoName() );
         }
         return "O"+p_bomastertable;*/
        if(getClassType()==TYPE_INTERFACE)
          return "O"+this.getName();
        else
          return "OE"+this.getName();

    }

    public String getBoMasterTable()
    {
        /* if( p_bomastertable == null )
         {
             p_bomastertable = xmlNode.getChildNode( "General").getAttribute("mastertable", this.getBoName() );
         }
         return "O"+p_bomastertable;*/
        return "O" + this.getName();
    }

    public String getBoPhisicalMasterTable()
    {
        if (p_bomastertable == null)
        {
            p_bomastertable = xmlNode.getChildNode("General").getAttribute("mastertable",
                    this.getBoName());


//            IDataPlugin[] plugins = DataPluginManager.getPlugIns();
//            for (int i = 0; i < plugins.length; i++) 
//            {
//                IDataBuilderDB bdb = plugins[i].getBuilderDB( null, this );
//                if( bdb != null )
//                {
//                    String pname = bdb.getPhisicalTableName( this );
//                    if( pname != null )
//                    {
//                        p_bomastertable = p_bomastertable;
//                    }
//                }
//            }

        }
        
        
        return p_bomastertable;
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
        return xmlNode.getChildNode("General").getAttribute("description",
            this.getBoName());
    }

    public String getStateNameRefer()
    {
        ngtXMLHandler x = xmlNode.getChildNode("ClsStates");

        if (x != null)
        {
            return x.getAttribute("refers", "");
        }
        else
        {
            return "";
        }
    }

    public boolean getBoMarkInputType()
    {
        // na versão anterior estava sempre activo
        return true;
    }
    public boolean getBoHaveMultiParent()
    {
        String ret = xmlNode.getChildNode("General").getAttribute("multiparent",
                "N");

        if (ret.equalsIgnoreCase("yes") || ret.equalsIgnoreCase("y"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean getBoCanBeOrphan()
    {
        String ret = xmlNode.getChildNode("General").getAttribute("orphan", "Yes");

        if (ret.equalsIgnoreCase("yes") || ret.equalsIgnoreCase("y"))
        {
            return true;
        }
        else
        {
            return false;
        }
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
        String xstr = xmlNode.getChildNode("General").getChildNodeText("derivedFrom",
                null);

        if ((xstr == null) || (xstr.trim().length() == 0))
        {
            return false;
        }

        return true;
    }

    public String getBoSuperBo()
    {
        return xmlNode.getChildNode("General").getChildNodeText("derivedFrom",
            null);
    }

    public String getBoVersion()
    {
        return getBoMajorVersion() + "." + getBoMinorVersion();
    }

    public String getBoEformId()
    {
        return this.getBoName() + this.getBoMajorVersion() + "_" +
        this.getBoMinorVersion();
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

    public boolean getBoManageTables() {
		return true;
	}

	public boolean getBoManageTablesXeoCompatible() {
		return true;
	}

	public String getName()
    {
        return this.getBoName();
    }

    public String getBoExtendsClass()
    {
        ngtXMLHandler genral = xmlNode.getChildNode("General");

        return (genral == null) ? null
                                : genral.getChildNodeText("classextends", null);
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

        ;

        return false;
    }

    public boDefHandler[] getBoSubClasses()
    {
        // Build Array with extended classes
        if (this.p_subobjects == null)
        {
            Vector xsub = new Vector();

            //boDefHandler[] alldefs = boDefHandler.listBoDefinitions();
            if (netgest.bo.def.v1.boDefHandlerImpl.p_definitions == null)
            {
                netgest.bo.def.v1.boDefHandlerImpl.p_definitions = (boDefHandlerImpl[])boDefHandler.listBoDefinitions();
            }

            for (short i = 0; i < netgest.bo.def.v1.boDefHandlerImpl.p_definitions.length; i++)
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

        if (netgest.bo.def.v1.boDefHandlerImpl.p_definitions == null)
        {
            netgest.bo.def.v1.boDefHandlerImpl.p_definitions = (boDefHandler[])boDefHandler.listBoDefinitions();
        }

        for (short i = 0; i < netgest.bo.def.v1.boDefHandlerImpl.p_definitions.length; i++)
        {
            if ((boDefHandlerImpl.p_definitions[i].getBoSuperBo() != null) &&
                    boDefHandlerImpl.p_definitions[i].getBoSuperBo().equals(this.getBoName()))
            {
                xsub.add(boDefHandlerImpl.p_definitions[i]);

                Vector x = ((boDefHandlerImpl)netgest.bo.def.v1.boDefHandlerImpl.p_definitions[(i)]).getBoAllSubClasses();

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
    
    public byte getInterfaceType()
    {
        return p_interfaceType;
    }

    public byte getClassType()
    {
        byte ret = TYPE_CLASS;
        ngtXMLHandler general = xmlNode.getChildNode("General");

        if (general != null)
        {
            if (general.getAttribute("bo_class", "").equalsIgnoreCase("abstract"))
            {
                ret = TYPE_ABSTRACT_CLASS;
            }
            else if (general.getAttribute("bo_class", "").equalsIgnoreCase("interface")||
                    general.getAttribute("bo_class", "").equalsIgnoreCase("mandatory_interface"))
            {
                ret = TYPE_INTERFACE;
                p_interfaceType = general.getAttribute("bo_class", "").equalsIgnoreCase("mandatory_interface")?INTERFACE_STANDARD:INTERFACE_OPTIONAL;
            }
            
        }

        return ret;
    }
    
    public boolean isMandatoryInterface()
    {
        ngtXMLHandler general = xmlNode.getChildNode("General");
        if (general != null)
        {
            if (general.getAttribute("bo_class", "").equalsIgnoreCase("mandatory_interface"))
            {
                return true;
            }
        }

        return false;
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
		ngtXMLHandler index = xmlNode.getChildNode("General").getChildNode("textIndex");
		if(index != null)
		{
			if(index.getChildNode("appendChilds")!= null && (index.getChildNode("appendChilds").getText().equalsIgnoreCase("yes") || index.getChildNode("appendChilds").getText().equalsIgnoreCase("y")))
			{
				if(index.getChildNode("deep") != null) return Integer.parseInt(index.getChildNode("deep").getText()); 
			}
		}
		return 0;
    }
    

    public String[] getIndexProcessChild()
    {
        String ret[] = null;
        ngtXMLHandler index = xmlNode.getChildNode("General").getChildNode("textIndex");
        if(index != null)
        {
            if(index.getChildNode("appendChilds")!= null && (index.getChildNode("appendChilds").getText().equalsIgnoreCase("yes") || index.getChildNode("appendChilds").getText().equalsIgnoreCase("y")))
            {
                if(index.getChildNode("process") != null) 
                {
                    ngtXMLHandler atts[] = index.getChildNode("process").getChildNodes();
                    ret = new String[ atts.length ];
                    for (int i = 0; i < atts.length; i++)
                    {
                        ret[i] = atts[i].getText();
                    }
                }
            }
        }
        return ret;
    }

    public boolean getIndexProcessChild(String attName)
    {
        ngtXMLHandler index = xmlNode.getChildNode("General").getChildNode("textIndex");
        if(index != null)
        {
            if(index.getChildNode("appendChilds")!= null && (index.getChildNode("appendChilds").getText().equalsIgnoreCase("yes") || index.getChildNode("appendChilds").getText().equalsIgnoreCase("y")))
            {
                if(index.getChildNode("process") != null) 
                {
                    ngtXMLHandler atts[] = index.getChildNode("process").getChildNodes();

                    for (int i = 0; i < atts.length; i++)
                    {
                        if(atts[i].getNodeName().equalsIgnoreCase("Attribute") && atts[i].getText().equals(attName))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public boolean indexOnlyCardID(String attName)
    {
        ngtXMLHandler index = xmlNode.getChildNode("General").getChildNode("textIndex");
        if(index != null)
        {
            if(index.getChildNode("appendChilds")!= null && (index.getChildNode("appendChilds").getText().equalsIgnoreCase("yes") || index.getChildNode("appendChilds").getText().equalsIgnoreCase("y")))
            {
                if(index.getChildNode("process") != null) 
                {
                    ngtXMLHandler atts[] = index.getChildNode("process").getChildNodes();

                    for (int i = 0; i < atts.length; i++)
                    {
                        if(atts[i].getNodeName().equalsIgnoreCase("Attribute") && atts[i].getText().equals(attName))
                        {
                            String attValue = atts[i].getAttribute("onlyCardID", "N");
                            if("yes".equalsIgnoreCase(attValue) || "y".equalsIgnoreCase(attValue))
                            {
                                return true;
                            }
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    
    public boolean isTextIndexActive()
    {
        if(p_indexValue == null)
        {
            ngtXMLHandler index = xmlNode.getChildNode("General").getChildNode("textIndex");
            if(index != null)
            {
                if(index.getText() != null)
                {
                    if("yes".equalsIgnoreCase(index.getText()) || "y".equalsIgnoreCase(index.getText()))
                    {
                        p_indexValue = Boolean.TRUE;
                    }
                    else if("no".equalsIgnoreCase(index.getText()) || "n".equalsIgnoreCase(index.getText()))
                    {
                        p_indexValue = Boolean.FALSE;
                    } 
                }
                else if(index.getChildNode("index")!= null)
                {
                    if("yes".equalsIgnoreCase(index.getChildNode("index").getText()) || 
                        "y".equalsIgnoreCase(index.getChildNode("index").getText()))
                    {
                        p_indexValue = Boolean.TRUE;
                    }
                    else if("no".equalsIgnoreCase(index.getChildNode("index").getText()) || 
                             "n".equalsIgnoreCase(index.getChildNode("index").getText()))
                    {
                        p_indexValue = Boolean.FALSE;
                    }
                }
                else
                {
                    p_indexValue = Boolean.TRUE;
                }
            }
            else
            {
                p_indexValue = Boolean.TRUE;
            }
        }
		return p_indexValue.booleanValue();
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

	public String getDataBaseManagerClassName() {
		// TODO Auto-generated method stub
		return null;
	}
    
    
}
