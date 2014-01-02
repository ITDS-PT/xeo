/*Enconding=UTF-8*/
package netgest.bo.def.v2;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import netgest.bo.boConfig;
import netgest.bo.builder.boBuilder;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefClsEvents;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefInterface;
import netgest.bo.def.boDefMethod;
import netgest.bo.def.boDefUtils;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.system.boContext;
import netgest.bo.system.boSession;
import netgest.bo.system.boSessionUser;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.Node;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class boDefInterfaceImpl extends boDefHandlerImpl implements boDefInterface
{
	
	int i=0;
	
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.def.boDefInterface");
    private String[] p_implObjects = null;
    private String[] p_extImplObjects = null;

    private boDefAttributeImpl[]    p_implAttributes = null;
    private boDefMethodImpl[]       p_implMethods = null;
    private boDefClsEventsImpl[]    p_implEvents = null;

    private String p_implName = null;

    public boDefInterfaceImpl(String interfaceName, XMLDocument xml)
    {
        super(xml, true);
        try
        {
            p_implName = interfaceName;
            if (xml != null)
            {
                ngtXMLHandler general = new ngtXMLHandler(((XMLNode) xml.getDocumentElement()).selectSingleNode(
                    "general"));
                if(general != null)
                {
                    ngtXMLHandler[] objects = general.getChildNodes();
                    for (int i = 0; i < objects.length; i++)
                    {
                        if("objects".equalsIgnoreCase(objects[i].getNodeName()))
                        {
                            ngtXMLHandler[] object = objects[i].getChildNodes();
                            ArrayList r = new ArrayList(object.length);
                            for (byte j = 0; j < object.length; j++)
                            {
                                String sObjectName = object[j].getText();
                                if(!r.contains( sObjectName ))
                                {
                                    if( boDefHandlerImpl.existsObject( sObjectName ) )
                                    {
                                        r.add( sObjectName );
                                    }
                                }
                                sObjectName = null;
                            }
                            p_implObjects = new String[r.size()];
                            for (byte j = 0; j < r.size(); j++)
                            {
                                p_implObjects[j] = (String)r.get(j);
                            }
                        }
                    }
                }

                ngtXMLHandler xchilds[];
                ngtXMLHandler xnode = new ngtXMLHandler(((XMLNode) xml.getDocumentElement()).selectSingleNode(
                    "methods"));

                if (xnode != null)
                {
                    xchilds = xnode.getChildNodes();
                    p_implMethods = new boDefMethodImpl[xchilds.length];

                    for (int i = 0; i < xchilds.length; i++)
                    {
                        p_implMethods[i] = new boDefMethodImpl( this, xchilds[i].getNode() );
                    }
                }

                xnode = getXmlNode().getChildNode("events");

                if (xnode != null)
                {
                    xchilds = xnode.getChildNodes();
                    p_implEvents = new boDefClsEventsImpl[xchilds.length];
                    for (int i = 0; i < xchilds.length; i++)
                    {
                        p_implEvents[i] = new boDefClsEventsImpl(this, xchilds[i].getNode() );
                    }
                }
                ngtXMLHandler attributes = new ngtXMLHandler(((XMLNode) xml.getDocumentElement()).selectSingleNode(
                    "attributes"));

                if (attributes != null)
                {
                    ngtXMLHandler[] atts = attributes.getChildNodes();

                    p_implAttributes = new boDefAttributeImpl[atts.length];

                    for (int i = 0; i < atts.length; i++)
                    {
                        p_implAttributes[i] = new boDefAttributeImpl( this, atts[i].getNode() );
                    }
                }
            }
        }
        catch (XSLException e)
        {
            logger.severe(e);
        }
    }
    /**
     * translation
     * 
     * 
     * 
     */
    public String getLabel(){						
		 return boDefHandlerImpl.getTranslation(this.getBoName(), p_implName,null,null,"label");  	
    }
    public String getDescription(){
	   return boDefHandlerImpl.getTranslation(this.getBoName(), this.p_implName, "description", this.getName(),"description");
    }
    ///////////////////
    
    
    /**
     * 
     * Returns a list of all XEO Models implementing this interface
     * (both the ones in the XML of the interface as well as the ones
     * in the XML of the XEO MOdels)
     * 
     * @return A list of XEO Model names
     */
    public String[] getAllObjectNamesImplementingInterface(){
    	
    	HashSet<boDefHandler> objectDefinition = getAllObjectDefHandlersImplementingInterface();
    	String[] objectNames = new String[objectDefinition.size()];
    	Iterator<boDefHandler> it = objectDefinition.iterator();
    	int k = 0;
    	while (it.hasNext()){
    		boDefHandler curr = it.next();
    		objectNames[k] = curr.getName();
    		k++;
    	}
    	return objectNames;
    }
    
    
    /**
     * 
     * Returns a list of all {@link boDefHandler} of XEO Models implementing this interface
     * (both the ones in the XML of the interface as well as the ones in the XML of the XEO MOdels)
     * 
     * @return A list of XEO Model names
     */
    public HashSet<boDefHandler> getAllObjectDefHandlersImplementingInterface(){
    	
    	HashSet<boDefHandler> result = new HashSet<boDefHandler>();
    	boDefHandler[] allObjects = boDefHandler.getAllBoDefinition();
    	for (boDefHandler defHandler : allObjects)
    	{
    		String[] interfaceImpls = defHandler.getImplements();
    		for (String interfaceName : interfaceImpls){
    			if (interfaceName.equalsIgnoreCase(p_implName))
    				result.add(defHandler);
    		}
    	}
    	String[] implementedInMe = getImplObjects();
    	for (String objectName: implementedInMe)
    		result.add(boDefHandler.getBoDefinition(objectName));
    	return result;
    }
    
    

    private static final void fillSystemAttributes(XMLDocument xml)
    {
        try
        {
            ngtXMLHandler atts = new ngtXMLHandler(((XMLNode) xml.getDocumentElement()).selectSingleNode(
                        "attributes"));
            if (atts.getChildNode("PARENT") == null)
            {
                atts.getNode().appendChild(
                            boDefUtils.createAttribute("PARENT", "PARENT",
                            "Objecto Pai", "attributeObject","object.boObject", 0,false,
                            atts.getNode().getOwnerDocument()));
            }

                if (atts.getChildNode("PARENTCTX") == null)
                {
                    atts.getNode().appendChild(
                            boDefUtils.createAttribute("PARENTCTX",
                            "PARENTCTX", "Contexto de Criação","attributeObject", "object.boObject",0,false,
                            atts.getNode().getOwnerDocument())
                            );
                }

                if (atts.getChildNode("TEMPLATE") == null)
                {
                    atts.getNode().appendChild(
                            boDefUtils.createAttribute("TEMPLATE",
                            "TEMPLATE", "Modelo","attributeObject", "object.Ebo_Template",0,false,
                            atts.getNode().getOwnerDocument()));
                }

                if (atts.getChildNode("BOUI") == null)
                {
                    atts.getNode().appendChild(
                            boDefUtils.createAttribute("BOUI", "BOUI",
                            "BOUI", "attributeNumber","",0,false, atts.getNode().getOwnerDocument()));
                }

                if (atts.getChildNode("CLASSNAME") == null)
                {
                    atts.getNode().appendChild(
                            boDefUtils.createAttribute("CLASSNAME",
                            "CLASSNAME", "Categoria do Objecto", "attributeText","",50,false,
                            atts.getNode().getOwnerDocument()));
                }

                if (atts.getChildNode("CREATOR") == null)
                {
                    atts.getNode().appendChild(
                            boDefUtils.createAttribute("CREATOR",
                            "CREATOR", "Criador","attributeObject", "object.iXEOUser",0,false,
                            atts.getNode().getOwnerDocument()));
                }

                if (atts.getChildNode("SYS_DTCREATE") == null)
                {
                    atts.getNode().appendChild(
                            boDefUtils.createAttribute("SYS_DTCREATE",
                            "SYS_DTCREATE", "Data de Criação","attributeDateTime" ,"",0,false,
                            atts.getNode().getOwnerDocument()));
                }

                if (atts.getChildNode("SYS_DTSAVE") == null)
                {
                    atts.getNode().appendChild(
                            boDefUtils.createAttribute("SYS_DTSAVE",
                            "SYS_DTSAVE", "Data da última actualização",
                            "attributeDateTime" ,"", 0,false, atts.getNode().getOwnerDocument()));
                }

                if (atts.getChildNode("SYS_ORIGIN") == null)
                {
                    atts.getNode().appendChild(
                            boDefUtils.createAttribute("SYS_ORIGIN",
                            "SYS_ORIGIN", "Origem dos dados", "attributeText" ,"",30,false,
                            atts.getNode().getOwnerDocument()));
                }
                if (atts.getChildNode("SYS_FROMOBJ") == null)
                {
                    atts.getNode().appendChild(
                            boDefUtils.createAttribute("SYS_FROMOBJ",
                            "SYS_FROMOBJ", "Objecto Origem", "attributeObject", "object.boObject",0,false,
                            atts.getNode().getOwnerDocument()));
                }
        }
        catch (XSLException e)
        {
            logger.severe(e);
        }
    }

    public String[] getExtImplObjects()
    {
        if(p_extImplObjects == null && p_implObjects != null)
        {
            boDefHandler bodef = null;
            boDefHandler bodefs[] = null;
            ArrayList r = new ArrayList();
            for (int i = 0; i < p_implObjects.length; i++)
            {
                r.add(p_implObjects[i]);

                bodef = boDefHandler.getBoDefinition(p_implObjects[i]);
                if(bodef != null)
                {
                    bodefs = bodef.getTreeSubClasses();
                    for (int j = 0; j < bodefs.length; j++)
                    {
                        r.add(bodefs[j].getName());
                    }
                }
            }
            p_extImplObjects = new String[r.size()];
            for (int i = 0; i < r.size(); i++)
            {
                p_extImplObjects[i] = (String)r.get(i);
            }

        }
        return p_extImplObjects;
    }

    public String[] getImplObjects()
    {
        return p_implObjects;
    }

    public boDefAttribute[] getImplAttributes()
    {
        return p_implAttributes;
    }

    public boDefMethod[] getImplMethods()
    {
        return p_implMethods;
    }

    public boDefClsEvents[] getImplEvents()
    {
        return p_implEvents;
    }

    public String getImplName()
    {
        return p_implName;
    }

    public static final boDefInterface getInterfaceDefinition(String interfaceName)
    {
        boDefInterfaceImpl ret = null;
        boConfig boconf = new boConfig();
        String path = boconf.getDeploymentDir() + interfaceName + ".xeoimodel";
        File xfile = new File(path);
        XMLDocument doc = null;

        if (xfile.exists())
        {
            doc = ngtXMLUtils.loadXMLFile(path);
            ret = new boDefInterfaceImpl(interfaceName, doc);
        }
       
        return ret;
    }
    
    
    public static final void fillSystemAttribute(String interfaceName)
    {
        boDefInterface ret = null;
        boConfig boconf = new boConfig();
        String path = boconf.getDeploymentDir() + interfaceName + ".xeoimodel";
        File xfile = new File(path);
        XMLDocument doc = null;

        if (xfile.exists())
        {
            doc = ngtXMLUtils.loadXMLFile(path);
            fillSystemAttributes(doc);
            ngtXMLUtils.saveXML(doc,path);
        }
    }

    public String getBoMasterTable()
    {
        /* if( p_bomastertable == null )
         {
             p_bomastertable = super.getChildNode( "General").getAttribute("mastertable", this.getBoName() );
         }
         return "O"+p_bomastertable;*/
        return "O" + this.getName();
    }

    public static final void writeToInterfaces(Hashtable table)
    {
        String intfName;
        boConfig boconf = new boConfig();
        String path = null, interfaceName = null;
        Enumeration oEnum = table.keys();
        while(oEnum.hasMoreElements())
        {
            interfaceName = (String)oEnum.nextElement();
            path = boconf.getDeploymentDir() + interfaceName + boBuilder.TYPE_INTERFACE;
            ArrayList listOfObj = (ArrayList)table.get(interfaceName);
            for (int i = 0; i < listOfObj.size(); i++)
            {
                addImplObject(path, (String)listOfObj.get(i));
            }
        }

    }

    public static boolean implObject(String xmlFile, String objName)
    {
        try
        {
            XMLDocument doc = ngtXMLUtils.loadXMLFile(xmlFile);
            ngtXMLHandler general = new ngtXMLHandler(((XMLNode) doc.getDocumentElement()).selectSingleNode(
                    "general"));
            if(general != null)
            {
                ngtXMLHandler[] objects = general.getChildNodes();
                boolean found = false;
                for (int i = 0; i < objects.length && !found; i++)
                {
                    if("objects".equalsIgnoreCase(objects[i].getNodeName()))
                    {
                        ngtXMLHandler[] childs = objects[i].getChildNodes();
                        Node aux;
                        for (int j = 0; j < childs.length && !found; j++)
                        {
                            if(objName.equals(childs[j].getText()))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        catch (XSLException e)
        {

        }
        return false;
    }

    public static void addImplObject(String xmlFile, String objName)
    {
        try
        {
            File xfile = new File( xmlFile );
            if( !xfile.exists() )
            {
                logger.warn(LoggerMessageLocalizer.getMessage("INTERFACE_REFERENCED_IN")+" "+objName+" "+LoggerMessageLocalizer.getMessage("DOESNT_EXIST_EXPECTED")+" ["+xmlFile+"]." );
                return;
            }

            XMLDocument doc = ngtXMLUtils.loadXMLFile(xmlFile);
            ngtXMLHandler general = new ngtXMLHandler(((XMLNode) doc.getDocumentElement()).selectSingleNode(
                    "general"));
            if(general != null)
            {
                ngtXMLHandler[] objects = general.getChildNodes();
                boolean found = false;
                for (int i = 0; i < objects.length && !found; i++)
                {
                    if("objects".equalsIgnoreCase(objects[i].getNodeName()))
                    {
                        ngtXMLHandler[] childs = objects[i].getChildNodes();
                        Node aux;
                        for (int j = 0; j < childs.length && !found; j++)
                        {
                            if(objName.equals(childs[j].getText()))
                            {
                                found = true;
                            }
                        }
                        if(!found)
                        {
                            XMLElement xe = (oracle.xml.parser.v2.XMLElement)objects[i].getNode().getOwnerDocument().createElement("object");
                            xe.addText(objName);
                            objects[i].getNode().appendChild(xe);
                        }
                        found = true;
                    }
                }
            }
            ngtXMLUtils.saveXML(doc, xmlFile);
        }
        catch (XSLException e)
        {

        }
    }

    public static void addImplObject(String bodefName, File f, Hashtable interfacedeploy, Hashtable objectInterf, File[] allXfiles)
    {
        try
        {
            ArrayList interfImpl;
            XMLDocument doc = ngtXMLUtils.loadXMLFile(f.getAbsolutePath());
            ngtXMLHandler general = new ngtXMLHandler(((XMLNode) doc.getDocumentElement()).selectSingleNode(
                    "general"));
            if(general != null)
            {
                ngtXMLHandler[] objects = general.getChildNodes();
                boolean found = false;
                String aux;

                for (int i = 0; i < objects.length && !found; i++)
                {
                    if("implements".equalsIgnoreCase(objects[i].getNodeName()))
                    {
                        found = true;
                        ngtXMLHandler[] childs = objects[i].getChildNodes();
                        for (int j = 0; j < childs.length; j++)
                        {
                            aux = childs[j].getText();
                            if((interfImpl = (ArrayList)interfacedeploy.get(bodefName)) != null)
                            {
                                if(!interfImpl.contains(aux))
                                {
                                    interfImpl.add(aux);
                                }
                            }
                            else
                            {
                                (interfImpl = new ArrayList()).add(aux);
                                interfacedeploy.put(bodefName, interfImpl);
                            }

                            if((interfImpl = (ArrayList)objectInterf.get(aux)) != null)
                            {
                                if(!interfImpl.contains(bodefName))
                                {
                                    interfImpl.add(bodefName);
                                }
                            }
                            else
                            {
                                (interfImpl = new ArrayList()).add(bodefName);
                                objectInterf.put(aux, interfImpl);
                            }
                        }
                    }
                }
            }
            if(allXfiles != null)
            {
                for (short i = 0; i < allXfiles.length; i++)
                {
                    if (allXfiles[i].getName().toLowerCase().endsWith(boBuilder.TYPE_INTERFACE))
                    {
                        if(implObject(allXfiles[i].getAbsolutePath(), bodefName))
                        {
                            String aux = allXfiles[i].getName().substring(0,
                                            allXfiles[i].getName().toLowerCase().indexOf(boBuilder.TYPE_INTERFACE));
                            if((interfImpl = (ArrayList)interfacedeploy.get(bodefName)) != null)
                            {
                                if(!interfImpl.contains(aux))
                                {
                                    interfImpl.add(aux);
                                }
                            }
                            else
                            {
                                (interfImpl = new ArrayList()).add(aux);
                                interfacedeploy.put(bodefName, interfImpl);
                            }

                            if((interfImpl = (ArrayList)objectInterf.get(aux)) != null)
                            {
                                if(!interfImpl.contains(bodefName))
                                {
                                    interfImpl.add(bodefName);
                                }
                            }
                            else
                            {
                                (interfImpl = new ArrayList()).add(bodefName);
                                objectInterf.put(aux, interfImpl);
                            }
                        }
                    }
                }
            }
        }
        catch (XSLException e)
        {

        }
    }

    //override
    public String[] canCastTo()
    {
        return null;
    }

    public String codeJavaScript(String viewerName)
    {
        return "";
    }

    public static boDefHandler[] getAllBoDefinition()
    {
        return new boDefHandler[0];
    }

    public Node getNode()
    {
        return super.getNode();
    }
}
