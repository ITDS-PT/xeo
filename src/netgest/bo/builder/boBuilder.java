/*Enconding=UTF-8*/
package netgest.bo.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import javax.naming.InitialContext;

import javax.transaction.UserTransaction;

import netgest.bo.boConfig;
import netgest.bo.boException;
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.def.boDef;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefClsEvents;
import netgest.bo.def.boDefClsState;
import netgest.bo.def.boDefForwardObject;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefInterface;
import netgest.bo.def.boDefLov;
import netgest.bo.def.boDefMethod;
import netgest.bo.def.boDefUtils;
import netgest.bo.def.v2.boDefLovImpl;
import netgest.bo.dochtml.docHTML_treeServer;
import netgest.bo.parser.CodeJavaConstructor;
import netgest.bo.presentation.manager.uiObjectBuilder;
import netgest.bo.presentation.render.elements.ExplorerServer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.runtime.robots.blogic.boTextIndexAgentBussinessLogic;
import netgest.bo.system.boConnectionManager;

import netgest.utils.IOUtils;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;

import org.apache.log4j.Logger;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class boBuilder
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.builder.boBuilder");

    private static boConfig p_bcfg = new boConfig();
    private static boolean p_running = false;
    protected static Hashtable p_undeployeddefs = new Hashtable();
    private final static int GAP = 10;

    public static final String TYPE_BO = ".xeomodel";
    public static final String TYPE_STATE = ".xeostate";
    public static final String TYPE_DS = ".xeods";
    public static final String TYPE_LOV = ".xeolov";
    public static final String TYPE_SC = ".xeodeploy";
    public static final String TYPE_INTERFACE = ".xeoimodel";
    public static final String TYPE_WSD = ".xeowds";

    private EboContext p_eboctx;
    private String p_dml;
    private Vector p_buildqueue = new Vector();
    private Vector p_depqueue = new Vector();
    private boolean p_register = true;
    private Hashtable interfacedeploy = new Hashtable();

    private boBuilder(EboContext ebo)
    {
        p_eboctx = ebo;
    }

    public static void setDeployed(EboContext ebo, String module)
    {
        boolean mybuild = false;
        try
        {
             synchronized (boBuilder.class)
            {
                if (boBuilder.p_running)
                {
                    return;
                }

                boBuilder.p_running = true;
                mybuild = true;
            }
            setDeployedObject(ebo, module );
        }
        finally
        {
            if (mybuild)
            {
                boBuilder.p_running = false;
            }
        }
    }

    public static void setPackagesDescription(EboContext eboCtx)
    {
        boolean mybuild = false;
        try
        {
             synchronized (boBuilder.class)
            {
                if (boBuilder.p_running)
                {
                    return;
                }

                boBuilder.p_running = true;
                mybuild = true;
            }
            File fPackSrc = new File(eboCtx.getApplication().getApplicationConfig().getDefinitiondir() +
                    "packages.xeodesc");
            File fPackDpl = new File(eboCtx.getApplication().getApplicationConfig().getDeploymentDir() +
                    "packages.xeodesc");
            if(fPackSrc.exists() && (!fPackDpl.exists() || fPackSrc.lastModified() > fPackDpl.lastModified()))
            {
                ngtXMLHandler packXML = new ngtXMLHandler(ngtXMLUtils.loadXMLFile(fPackSrc.getAbsolutePath()));
                ngtXMLHandler packagesXML = packXML.getChildNode("Packages");
                String nacionalityDefault = packagesXML.getAttribute("nacionalityDefault", "pt");
                ngtXMLHandler[] packs = packagesXML.getChildNodes();
                String auxPackName;
                String auxPackDesc;
                Connection con = null;
                PreparedStatement pst = null;
                boolean ok = false;
                try
                {
                    final InitialContext ic = new InitialContext();
                    UserTransaction ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
                    ut.setTransactionTimeout(6000000);
                    String update = "";
                    con = eboCtx.getDedicatedConnectionData();
                    String sql = "update ebo_package set description = ? where name = ?";
                    pst = con.prepareStatement(sql);
                    logger.info("Setting Packages Descriptions");
                    for (int i = 0; i < packs.length; i++)
                    {
                        auxPackName = packs[i].getAttribute("name");
                        auxPackDesc = packs[i].getChildNode("description").getText();

                        pst.setString(1, auxPackDesc);
                        pst.setString(2, auxPackName);
                        pst.executeUpdate();
                        logger.info("Package [" + auxPackName + "] setting description [" + auxPackDesc +"]" );
                    }
                    pst.close();
                    //1ºdelete from ebo_references ref where ref.refboui$ in (select child$ from ebo_perf$packages t where t.child$ not in (select boui from oebo_package) and t.parent$=1068)
                    //3ºdelete from ebo_perf$packages t where t.child$ not in (select boui from oebo_package)
                    StringBuffer delete = new StringBuffer("delete from oebo_references where refboui$ in ");
                    delete.append("(select child$ from oebo_perf$packages t where t.child$ not in ")
                        .append("(select boui from oebo_package)) and oebo_references.refboui$ in ")
                        .append("(select parent$ from ebo_perf$packages t where t.child$ not in (select boui from oebo_package))");
                    pst = con.prepareStatement(delete.toString());
                    pst.executeUpdate();
                    pst.close();
                    delete.delete(0, delete.length());
                    delete.append("delete from ebo_perf$packages where child$ not in (select boui from oebo_package)");
                    pst = con.prepareStatement(delete.toString());
                    pst.executeUpdate();

                    ok = true;
                    // IMBR
                    //con.commit();
                }
                catch (Exception e)
                {
                    logger.error(e);
                }
                finally
                {
                    try
                    {
                        if (pst != null)
                        {
                            pst.close();
                        }
                        if (con != null)
                        {
                          con.close();
                        }
                    }
                    catch (Exception e)
                    {
                        //ignora
                    }
                    if(ok)
                    {
                        eboCtx.commitContainerTransaction();
                        IOUtils.copy(fPackSrc, fPackDpl);
                    }
                    else
                    {
                        logger.info("Não foi possível efectuar os set da descrição do Packages");
                        eboCtx.rollbackContainerTransaction();
                    }
                    eboCtx.close();
                }
            }
            else
            {
                if(fPackSrc.exists())
                {
                    logger.info("Não foi necessário efectuar o set das descrições dos packages");
                }
                else
                {
                    logger.info("Não existe o ficheiro de configuração das descrições dos packages.");
                }
            }
        }
        catch (boRuntimeException e)
        {
            logger.error("Não foi possível efectuar o set dos Packages names.");
        }
        finally
        {
            if (mybuild)
            {
                boBuilder.p_running = false;
            }
        }
    }

    public static void buildAll(EboContext ebo) throws boRuntimeException
    {
        boolean mybuild = false;
        String module = ebo.getRequest().getParameter("module");
        boolean buildInterfaceFlag = module != null && module.indexOf("interface") != -1;
        try
        {
            synchronized (boBuilder.class)
            {
                if (boBuilder.p_running)
                {
                    return;
                }

                boBuilder.p_running = true;
                mybuild = true;
            }

            ebo.getApplication().getApplicationConfig().refresh();

            boBuilder builder = new boBuilder(ebo);

            ebo.getApplication().suspendAgents();

            boDefHandler.clearCache();
            boBuildRepository.clearCache();


//            File ebotemp = new File(p_bcfg.getTemplatesDir() +
//                    "boTemplate.java");
//            File ebodepl = new File(p_bcfg.getDeploymentDir() +
//                    "boTemplate.java");
//
            boolean deployall = false;
//
//            if ((ebotemp.lastModified() > ebodepl.lastModified()) ||
//                    !ebodepl.exists())
//            {
//                deployall = true;
//            }
//
//            File eboobjdir = new File(p_bcfg.getDefinitiondir());

            //build do repositório default
            boBuildRepository rep = new boBuildRepository(ebo.getBoSession()
                                                             .getRepository());

            //build ngtdic e index
            builder.buildSpecialTables(ebo, rep.getSchemaName());

            //File[] toDepl = rep.getFilesToDeploy(null, deployall, null, buildInterfaceFlag);
            File[] toDepl = rep.getFilesToDeploy( deployall );//REFORMULADO

            boolean fullTextIndex = false;

            if (toDepl.length > 0)
            {
                fullTextIndex = true;

                //                boAgentsControler.suspendAgents();
                builder.buildObjects(null, deployall,module);
            }

            // Update the template file in deployment directory
//            if (deployall)
//            {
//                IOUtils.copy(ebotemp, ebodepl);
//            }

            if (fullTextIndex)
            {
//                ebo.getApplication().startAgents();

                //                boAgentsControler.startAgents();
//                logger.debug("Wainting for fulltext index finish...");
//
//                while (boTextIndexAgent.queue.getQueueSize() > 0)
//                {
//                    logger.debug(boTextIndexAgent.queue.getQueueSize() +
//                        " to finish");
//
//                    try
//                    {
//                        Thread.sleep(1000);
//                    }
//                    catch (InterruptedException e)
//                    {
//                    }
//                }
            }
        }
        finally
        {
            if (mybuild)
            {
                boBuilder.p_running = false;
            }
        }
    }

    public static void buildAll(EboContext ebo, String repositoryName)
        throws boRuntimeException
    {
        boolean mybuild = false;
        String module = ebo.getRequest().getParameter("module");
        boolean buildInterfaceFlag = module != null && module.indexOf("interface") != -1;
        try
        {
            synchronized (boBuilder.class)
            {
                if (boBuilder.p_running)
                {
                    return;
                }

                boBuilder.p_running = true;
                mybuild = true;
            }

            ebo.getApplication().getApplicationConfig().refresh();

            boBuilder builder = new boBuilder(ebo);		
				
            boDefHandler.clearCache();

            File ebotemp = new File(p_bcfg.getTemplatesDir() +
                    "boTemplate.java");
            File ebodepl = new File(p_bcfg.getDeploymentDir() +
                    "boTemplate.java");

            boolean deployall = false;

            if ((ebotemp.lastModified() > ebodepl.lastModified()) ||
                    !ebodepl.exists())
            {
                deployall = true;
            }

            File eboobjdir = new File(p_bcfg.getDefinitiondir());

            //build do repositório default
            boBuildRepository rep = new boBuildRepository(ebo.getBoSession()
                                                             .getRepository());

            //build ngtdic e index
            builder.buildSpecialTables(ebo, rep.getSchemaName());

            File[] toDepl = rep.getFilesToDeploy( deployall );//REFORMULADO
            boolean fullTextIndex = false;

            if (toDepl.length > 0)
            {
                fullTextIndex = true;

                //                boAgentsControler.suspendAgents();
                builder.buildObjects(null, deployall,null);
            }

            // Update the template file in deployment directory
            if (deployall)
            {
                IOUtils.copy(ebotemp, ebodepl);
            }

            if (fullTextIndex)
            {
                //                boAgentsControler.startAgents();
                logger.debug("Wainting for fulltext index finish...");

                while (boTextIndexAgentBussinessLogic.queue.getQueueSize( ebo ) > 0)
                {
                    logger.debug(boTextIndexAgentBussinessLogic.queue.getQueueSize( ebo ) +
                        " to finish");

                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
            }
        }
        finally
        {
            if (mybuild)
            {
                boBuilder.p_running = false;
            }
        }
    }

    private static void buildStates(Hashtable todeploy, String name)
        throws boRuntimeException
    {
        boBuildStates bstates = new boBuildStates();
        boDefHandler xx = boDefHandler.getClsState( name ).getBoDefHandler();
        bstates.build(xx);

        boDefHandler[] defs = boDefHandler.listBoDefinitions();

        for (short i = 0; i < defs.length; i++)
        {
            if( name.equals( defs[i].getReferencedState() ) )
            {
                if (todeploy.get(defs[i].getName()) == null)
                {
                    todeploy.put(defs[i].getName(), new Boolean(false));
                }
            }
        }
    }

    private void buildLov(String name) throws boRuntimeException
    {
        boBuildLov blov = new boBuildLov(p_eboctx);
        boDefLov xx = boDefLovImpl.loadLov(name);
        blov.build(xx);
    }

    private static ngtXMLHandler[] getUpdates(Node doc)
    {
        ArrayList ret = new ArrayList();
        ngtXMLHandler[] retX;
        NodeList n = doc.getChildNodes();
        Node aux;

        for (int i = 0; i < n.getLength(); i++)
        {
            aux = n.item(i);

            String s = aux.getNodeName();

            if (aux.getNodeName().equalsIgnoreCase("scripts"))
            {
                n = aux.getChildNodes();

                for (int j = 0; j < n.getLength(); j++)
                {
                    aux = n.item(j);

                    if (aux.getNodeName().equalsIgnoreCase("updateSQL"))
                    {
                        ret.add(new ngtXMLHandler(aux));
                    }
                }
            }
        }

        if (ret.size() == 0)
        {
            return null;
        }

        retX = new ngtXMLHandler[ret.size()];

        for (int i = 0; i < ret.size(); i++)
        {
            retX[i] = (ngtXMLHandler) ret.get(i);
        }

        return retX;
    }

    private void treatScript(EboContext ctx, String name)
        throws boRuntimeException
    {
        boBuildRefactoring br = new boBuildRefactoring(ctx);
        XMLDocument doc = ngtXMLUtils.loadXMLFile(ctx.getApplication()
                                                     .getApplicationConfig()
                                                     .getDeploymentDir() +
                name + TYPE_SC);
        ngtXMLHandler[] updates = getUpdates(doc);

        if ((updates != null) && (updates.length > 0))
        {
            Arrays.sort(updates, new boRefactorComparator());
            br.build(updates);
        }
    }

    public void buildSpecialTables(EboContext ctx, String schemaName)
        throws boRuntimeException
    {
        OracleDBM odbm = null;

        try
        {
            odbm = ctx.getDataBaseDriver().getDBM();
            odbm.setEnvironment(ctx);
            odbm.createSpecialTables(schemaName);
        }
        catch (SQLException e)
        {
            throw new boRuntimeException("", e.getMessage(), null);
        }
        catch (boRuntimeException e)
        {
            throw new boRuntimeException("", e.getMessage(), null);
        }
        finally
        {
            odbm.close();
        }
    }


    private void buildObjects(String objName, boolean deployAll, String module)
        throws boRuntimeException
    {


        // Check all directories..
        File deployDir = null;
        deployDir = new File( p_eboctx.getApplication().getApplicationConfig().getDeployJspDir() );
        if( !deployDir.exists() ) deployDir.mkdirs();
        deployDir = new File( p_eboctx.getApplication().getApplicationConfig().getDeploymentclassdir() );
        if( !deployDir.exists() ) deployDir.mkdirs();
        deployDir = new File( p_eboctx.getApplication().getApplicationConfig().getDeploymentDir() );
        if( !deployDir.exists() ) deployDir.mkdirs();
        deployDir = new File( p_eboctx.getApplication().getApplicationConfig().getDeploymentsrcdir() );
        if( !deployDir.exists() ) deployDir.mkdirs();

        boBuildRepository repository = new boBuildRepository(p_eboctx.getBoSession()
                                                                     .getRepository());
//        boolean buildInterfaceFlag = module != null && module.indexOf("interface") != -1;
// -------------------------- Referente as Interfaces
        File[] xfiles = repository.getFilesToDeploy( deployAll );//REFORMULADO;

        String[] xfilesNames = new String[ xfiles.length ];
        for (int i = 0; i < xfilesNames.length; i++)
        {
            xfilesNames[i] = xfiles[i].getName();
        }

        File[] allXfiles = repository.getXMLFiles( );

        String[] sxfilesNames = (String[])xfilesNames.clone();
        Arrays.sort( sxfilesNames );



        ArrayList interfImpl = null;

        Hashtable objectInterf = new Hashtable();   // Cont?m as dependencias de todos os interfaces

        this.p_undeployeddefs.clear();

        for (short i = 0; i < allXfiles.length; i++)
        {
            if (allXfiles[i].getName().toLowerCase().endsWith(TYPE_BO))
            {
                String name = allXfiles[i].getName().substring(0,
                        allXfiles[i].getName().toLowerCase().indexOf(TYPE_BO));
                //verificar se implementa
                netgest.bo.def.v2.boDefInterfaceImpl.addImplObject(name, allXfiles[i], interfacedeploy, objectInterf, allXfiles);
            }
            else if (allXfiles[i].getName().toLowerCase().endsWith( TYPE_STATE ))
            {
                String name = allXfiles[i].getName().substring(0, allXfiles[i].getName().toLowerCase().indexOf(TYPE_STATE));
                File deployFile = new File(p_bcfg.getDeploymentDir() + name +TYPE_STATE );
                if( !deployFile.exists() )
                {
                    IOUtils.copy( allXfiles[i],  deployFile );
                    deployFile.setLastModified(allXfiles[i].lastModified() - 600);
                }

            }
            else if (allXfiles[i].getName().toLowerCase().endsWith(TYPE_INTERFACE))
            {

                String name = allXfiles[i].getName().substring(0,
                        allXfiles[i].getName().toLowerCase().indexOf(TYPE_INTERFACE));

                int deployFilePosition = Arrays.asList(xfilesNames).indexOf( allXfiles[ i ].getName() );
                if( deployFilePosition > -1 )
                {
                    File deployfile = new File(p_bcfg.getDeploymentDir() + name +
                            TYPE_INTERFACE);

                    IOUtils.copy(xfiles[deployFilePosition], deployfile);

                    deployfile.setLastModified(xfiles[deployFilePosition].lastModified() - 600);

                    netgest.bo.def.v2.boDefInterfaceImpl.fillSystemAttribute(name);

                    deployfile.setLastModified(xfiles[deployFilePosition].lastModified());
                }

                boDefInterface bdi = boDefHandler.getInterfaceDefinition(name);
                String objs[] = null;
                if((objs = bdi.getImplObjects()) != null)
                {

                    for (int j = 0; j < objs.length; j++)
                    {
                        //interface in object
                        if((interfImpl = (ArrayList)interfacedeploy.get(objs[j])) != null)
                        {
                            if(!interfImpl.contains(bdi.getName()))
                            {
                                interfImpl.add(bdi.getName());
                            }
                        }
                        else
                        {
                            (interfImpl = new ArrayList()).add(bdi.getName());

                            interfacedeploy.put(objs[j], interfImpl);
                        }

                        //object in interface
                        if((interfImpl = (ArrayList)objectInterf.get(bdi.getName())) != null)
                        {
                            if(!interfImpl.contains(objs[j]))
                            {
                                interfImpl.add(objs[j]);
                            }
                        }
                        else
                        {
                            (interfImpl = new ArrayList()).add(objs[j]);
                            objectInterf.put(bdi.getName(), interfImpl);
                        }
                    }
                }

            }
        }

/*        for (short i = 0; i < allXfiles.length; i++)
        {
            if (allXfiles[i].getName().toLowerCase().endsWith( TYPE_BO ))
            {
                String name = allXfiles[i].getName().substring(0,
                        allXfiles[i].getName().toLowerCase().indexOf(TYPE_BO));

                //verificar se implementa
                netgest.bo.def.v2.boDefInterfaceImpl.addImplChangedInterface(name, allXfiles[i], interfacedeploy, objectInterf);
            }
        }
*/
        netgest.bo.def.v2.boDefInterfaceImpl.writeToInterfaces(objectInterf);
//-------------------------------------------------------------------------------

        File[] bofiles = repository.getFilesToDeploy( deployAll );//REFORMULADO

        //WSD files
        ArrayList wsdFiles = new ArrayList();

        //Lov files
        ArrayList lovFiles = new ArrayList();

        //Script files
        ArrayList scriptFiles = new ArrayList();

        // Update bodef deployments
        boolean success = false;

        File xboObject = new File(this.p_bcfg.getDeploymentDir() +
                "boObject.xeomodel");

        //File dboObject = new File(this.p_bcfg.getDefinitiondir()+"boObject$bo.xml");
        //vai procurar na directoria XEO
        File dboObject = repository.getXMLFileFromDefinition("boObject");

        if (!xboObject.exists() ||
                (dboObject.lastModified() > xboObject.lastModified()))
        {
            IOUtils.copy(dboObject, xboObject);
        }

        long ms = System.currentTimeMillis();
        logger.debug("Starting deploying objects...");

        boDefHandler.clearInterfacesCache();
        this.p_undeployeddefs.clear();

        //set de todos os packages
        File[] allXmlfiles = repository.getXMLFiles();
        Hashtable htdeploypackage = new Hashtable();
        boolean intf = false;

        for (short i = 0; i < allXmlfiles.length; i++)
        {
            intf = allXmlfiles[i].getName().toLowerCase().endsWith( TYPE_INTERFACE );
            if (allXmlfiles[i].getName().toLowerCase().endsWith( TYPE_BO ) ||
                intf)
            {
                String name = allXmlfiles[i].getName().substring(0,
                        allXmlfiles[i].getName().toLowerCase().indexOf(!intf ? TYPE_BO:TYPE_INTERFACE));

                if (!name.equalsIgnoreCase("boObject"))
                {
                    htdeploypackage.put(name, getObjectPackage(allXmlfiles[i]));
                }
            }
        }
//        logger.debug("Building InterfaceHandler:'" + name + "'");
//
//        logger.debug("end...");

        Hashtable htdeploy = new Hashtable();

        for (short i = 0; i < bofiles.length; i++)
        {
            if( bofiles[i].getName().toLowerCase().endsWith(TYPE_BO) || bofiles[i].getName().toLowerCase().endsWith(TYPE_INTERFACE) )
            {
                String name = null;
                boolean interf = false;
                if( bofiles[i].getName().toLowerCase().endsWith(TYPE_BO) )
                {
                    name = bofiles[i].getName().substring(0,bofiles[i].getName().toLowerCase().indexOf(TYPE_BO));
                }
                else
                {
                    name = bofiles[i].getName().substring(0,bofiles[i].getName().toLowerCase().indexOf(TYPE_INTERFACE));
                    interf  = true;
                }

                if (!name.equalsIgnoreCase("boObject") )
                {

                    File depfile = new File(this.p_bcfg.getDeploymentDir()
                                    + name + (interf?TYPE_INTERFACE:TYPE_BO) );

                    boolean exists = depfile.exists();
                    htdeploy.put(name, new Boolean(exists));

                    String sBackFileName = depfile.getName() + ".bak";

                    if (exists)
                    {
                        File backup = IOUtils.copy(depfile,
                                p_bcfg.getDeploymentDir() + sBackFileName );
                        logger.debug("New version detected [" + name + "]");
                    }
                    else
                    {
                        logger.debug("New object detected [" + name + "]");
                        IOUtils.copy(bofiles[i], depfile).setLastModified(bofiles[i].lastModified() -
                            60000);
                    }
                }
            }
            else if (bofiles[i].getName().toLowerCase().endsWith( TYPE_STATE ))
            {
                String name = bofiles[i].getName().substring(0,
                        bofiles[i].getName().toLowerCase().indexOf( TYPE_STATE ));
                File deployfile = new File(p_bcfg.getDeploymentDir() + name +
                        TYPE_STATE );

                IOUtils.copy(bofiles[i], deployfile);

                deployfile.setLastModified(bofiles[i].lastModified() - 600);

                logger.debug("Building StateObjectHandler:'" + name + "'");

                buildStates(htdeploy, name);

                logger.debug("end...");

                deployfile.setLastModified(bofiles[i].lastModified());
            }
            else if (bofiles[i].getName().toLowerCase().endsWith( TYPE_LOV ))
            {
                lovFiles.add(new Integer(i));
            }
            else if (bofiles[i].getName().toLowerCase().endsWith( TYPE_WSD ))
            {
                wsdFiles.add(new Integer(i));
            }
            else if (bofiles[i].getName().toLowerCase().endsWith( TYPE_SC ))
            {
                scriptFiles.add(new Integer(i));
            }
        }

        boDefHandler.clearCache();

        // Make BuildOrder
        Stack stack = new Stack();

        Enumeration bonames = htdeploy.keys();

        while (bonames.hasMoreElements())
        {
            String name = (String) bonames.nextElement();
            makeBuildDep(name, stack,
                !((Boolean) htdeploy.get(name)).booleanValue());
        }

        Vector todeploy = new Vector();
        boDefHandler def;

        p_undeployeddefs.clear();

        boolean builcls1st = false;

        while (stack.size() > 0)
        {
            //VERIFICAR
            String name = (String) stack.pop();
            def = getUndeployedDefinitions(repository, name, interfacedeploy, false);

            if( def == null )
            {
                def = def;
            }

            if (def.getBoName().equals("Ebo_ClsReg"))
            {
                builcls1st = true;
            }

            if (def != null)
            {
                todeploy.add(def);
            }
        }

        try
        {
            logger.debug("Creating bodef-deployment xml...");

            for (int i = 0; i < todeploy.size(); i++)
            {
                try
                {
                    boDefHandler todepDef = (boDefHandler) todeploy.get(i);
                    String xname = todepDef.getBoName();

                    //File srcfile   =new File(this.p_bcfg.getDefinitiondir()+xname+TYPE_BO);
                    //passou a ir buscar o ficheiro ao PathProvider
                    File    srcfile = repository.getXMLFile(xname);
                    String  fname   = xname + TYPE_BO;
                    if( srcfile == null )
                    {
                        srcfile = repository.getFile(xname,TYPE_INTERFACE);
                        fname = xname + TYPE_INTERFACE;
                    }
                    if( todepDef.getClassType() == boDefHandler.TYPE_INTERFACE )
                    {
                        if( !fname.endsWith( TYPE_INTERFACE ) )
                        {
                            logger.error("Debug info. Object ["+ todepDef.getName() +"] is interface and are ready to be writed as a object.");
                        }
                    }


                    File deployfile = new File( p_eboctx.getApplication()
                                                       .getApplicationConfig()
                                                       .getDeploymentDir() + fname
                                                );

                    FileOutputStream os = new FileOutputStream(deployfile);

                    // Create XML with the full attributes
                    boDefHandler bobjinherit = (boDefHandler) todeploy.get(i);

//VERIFICAR
                    buildInherit(repository, bobjinherit, interfacedeploy, false);

                    ((XMLDocument) bobjinherit.getNode().getOwnerDocument()).print(os);
                    os.close();

                    if( deployfile == null || srcfile == null )
                    {
                        boolean todebug = true;
                    }
                    if( deployfile != null && srcfile != null ) {
                    deployfile.setLastModified(srcfile.lastModified() - 60000);
                }
                }
                catch (FileNotFoundException e)
                {
                    throw new RuntimeException(
                        "Unexpected java.io.FileNotFoundException cpoying files to deploy directory [" +
                        ((boDefHandler) todeploy.get(i)).getName() + "]\n" +
                        e.getClass() + "\n" + e.getMessage());
                }
                catch (IOException e)
                {
                    throw new RuntimeException(
                        "Erro copying files to deployment directory [" +
                        ((boDefHandler) todeploy.get(i)).getName() + "]\n" +
                        e.getClass() + "\n" + e.getMessage());
                }
            }
            p_undeployeddefs.clear();
            boDefHandler.clearCache();
            boDefLovImpl.clearCache();

            for (int i = 0; i < todeploy.size(); i++)
            {
                try
                {
                    if(((boDefHandler) todeploy.get(i)).hasFwdMethods())
                    {
                        String xname = ((boDefHandler) todeploy.get(i)).getBoName();
                        logger.info(xname + " has Foward Methods");


                        //File srcfile   =new File(this.p_bcfg.getDefinitiondir()+xname+TYPE_BO);
                        //passou a ir buscar o ficheiro ao PathProvider
                        File srcfile = repository.getXMLFile(xname);


                        // Create XML with the full attributes
                        boDefHandler bobjinherit = (boDefHandler) todeploy.get(i);

                        String fileName = p_eboctx.getApplication()
                                                           .getApplicationConfig()
                                                           .getDeploymentDir() +
                                            xname;
                        if( bobjinherit.getClassType() == boDefHandler.TYPE_INTERFACE )
                        {
                            fileName += TYPE_INTERFACE;
                        }
                        else
                        {
                            fileName += TYPE_BO;
                        }
                        File deployfile = new File( fileName );

                        FileOutputStream os = new FileOutputStream(deployfile);

//VERIFICAR
                        buildInherit(repository, bobjinherit, interfacedeploy, true);

                        ((XMLDocument) bobjinherit.getNode().getOwnerDocument()).print(os);
                        os.close();

                        deployfile.setLastModified(srcfile.lastModified() - 60000);

                    }
                }
                catch (FileNotFoundException e)
                {
                    throw new RuntimeException(
                        "Unexpected java.io.FileNotFoundException cpoying files to deploy directory [" +
                        ((boDefHandler) todeploy.get(i)).getName() + "]\n" +
                        e.getClass() + "\n" + e.getMessage());
                }
                catch (IOException e)
                {
                    throw new RuntimeException(
                        "Erro copying files to deployment directory [" +
                        ((boDefHandler) todeploy.get(i)).getName() + "]\n" +
                        e.getClass() + "\n" + e.getMessage());
                }
            }

            p_undeployeddefs.clear();
            boDefHandler.clearCache();

            boDefHandler[] defs;
            defs = new boDefHandler[todeploy.size()];

            todeploy.toArray(defs);

            for (int i = 0; i < defs.length; i++)
            {
//VERIFICAR
                defs[i] = getUndeployedDefinitions(repository, defs[i].getName(), interfacedeploy, true);
            }
            if(module == null || module.indexOf("jsp") == -1)
            {
            // Creating array with Ebo_ClsReg BOUI to be filled in register classes;
            long[] classesbouis = new long[defs.length];

            // Build Classes 1'st phase.. only needed when Ebo_ClsReg is not yet deployed.
            boClassBuilder bcl = new boClassBuilder();

            if (builcls1st)
            {
                // Make a deep rebuild including system tables.
                logger.debug("Creating System Tables...");

                boBuildDB bdb = new boBuildDB(p_eboctx);
                bdb.buildSystemStables();
                logger.debug("Done.");

                logger.debug("Creating Views for System Tables... ");
                bdb = new boBuildDB(p_eboctx, repository, interfacedeploy);
                bdb.buildSystemViews();
            }
            
            logger.debug("Building Object Classes...");
            bcl.build(defs, classesbouis);
            
            // Build EboClsReg
            for (int i = defs.length - 1; i >= 0; i--)
            {
                if (defs[i].getName().equalsIgnoreCase("Ebo_ClsReg"))
                {
                    boBuildDB bdb = new boBuildDB(p_eboctx);
                    bdb.buildObject(boDefHandler.getBoDefinition(
                            defs[i].getName()), false,
                        boBuildDB.BUILD_ATTRIBUTES);

                    break;
                }
            }


            Hashtable buildedobjects = new Hashtable();

            // Check data to migrate
            //            logger.debug("Checking DataBase Objects...");
            //            {
            //                boBuildDB bdb = new boBuildDB(p_eboctx);
            //                bdb.migrateToNewTables();
            //            }
            // Build Database Objects
            buildedobjects.clear();

            for (short i = 0; i < defs.length; i++)
            {
                if (defs[i].getClassType() == boDefHandler.TYPE_CLASS && defs[i].getDataBaseManagerManageTables() )
                {
                    logger.debug("Building DB Attributes for:'" +
                        defs[i].getName() + "'");

                    if (buildedobjects.get(defs[i].getName()) == null)
                    {
                        if (!defs[i].getName().equalsIgnoreCase("Ebo_ClsReg"))
                        {
                            //long init = System.currentTimeMillis();

                            boBuildDB bdb = new boBuildDB(p_eboctx);
                            bdb.buildObject(boDefHandler.getBoDefinition(
                                    defs[i].getName()), false,
                                boBuildDB.BUILD_ATTRIBUTES);
                            buildedobjects.putAll(bdb.getBuildedObjects());

                            //System.out.println( System.currentTimeMillis() - init );
                        }
                    }
                }
            }

            // Build Database Objects
            if (builcls1st)
            {
                // Make a deep rebuild including system tables.
                logger.debug("Creating System Constraints...");

                boBuildDB bdb = new boBuildDB(p_eboctx);
                bdb.buildSystemKeys();
            }

            buildedobjects.clear();

            for (short i = 0; i < defs.length; i++)
            {
                if (defs[i].getClassType() == boDefHandler.TYPE_CLASS && defs[i].getDataBaseManagerManageTables() )
                {
                    logger.debug("Building DB Constraints for:'" +
                        defs[i].getName() + "'");

                    if (buildedobjects.get(defs[i].getName()) == null)
                    {
                        boBuildDB bdb = new boBuildDB(p_eboctx);
                        bdb.buildObject(boDefHandler.getBoDefinition(
                                defs[i].getName()), false,
                            boBuildDB.BUILD_CONSTRAINTS);
                        buildedobjects.putAll(bdb.getBuildedObjects());
                    }
                }
            }


            //garantir que as vies das interfaces são as ultimas, devido a estarem dependetes de outras views
            Vector interfaceDefs = new Vector();
            // Build Database Views
            for (short i = 0; i < defs.length; i++)
            {
                if(defs[i].getClassType() == boDefHandler.TYPE_INTERFACE)
                {
                    if( defs[i].getInterfaceType() == boDefHandler.INTERFACE_OPTIONAL )
                    {
                        interfaceDefs.add(defs[i]);
                    }
                }
                else
                {
                	if( defs[i].getDataBaseManagerManageViews() ) {
                    logger.debug("Building Views for:'" + defs[i].getName() +
                        "'");

                    boBuildDB bdb = new boBuildDB(p_eboctx);
                    bdb.createInheritViews(boDefHandler.getBoDefinition(
                            defs[i].getBoName()));
                }
            }
            }
            logger.debug("Building Special Views for:'Ebo_ClsReg' and 'Ebo_Package'");
            boBuildDB specialView = new boBuildDB(p_eboctx);
            specialView.createInheritViews(boDefHandler.getBoDefinition("Ebo_ClsReg"), true);
            specialView = new boBuildDB(p_eboctx);
            specialView.createInheritViews(boDefHandler.getBoDefinition("Ebo_Package"), true);
            // Build Database Views for interfaces
            for (short i = 0; i < interfaceDefs.size(); i++)
            {
                boDefHandler intDef = (boDefHandler)interfaceDefs.get(i);
                logger.debug("Building Interface View for:'" + intDef.getName() +"'");

                boBuildDB bdb = new boBuildDB(p_eboctx);
                bdb.createInheritViews(boDefHandler.getBoDefinition(
                        intDef.getBoName()));
            }
            if( objectInterf  != null && objectInterf.size() > 0 )
            {
                Enumeration oEnum = objectInterf.keys();
                String mandInterface=null;
                while(oEnum.hasMoreElements())
                {
                    try
                    {
                        mandInterface = (String)oEnum.nextElement();
                        logger.debug("Building Mandatory Interface View for:'" + mandInterface +"'");
                        boBuildDB bdb = new boBuildDB(p_eboctx);
                        bdb.createInheritViewsForMandatoryInterfaces( mandInterface, (ArrayList)objectInterf.get(mandInterface) );
                    }
                    catch( Exception e )
                    {
                        throw new RuntimeException(e);
                    }
                }
            }

            //             Build Eforms of the objects
            //            for (short i = 0; i < defs.length; i++)
            //            {
            //                if(defs[i].getClassType()==boDefHandler.TYPE_CLASS) {
            //                    logger.debug("Building Eform for:'"+defs[i].getBoName()+"'");
            //                    boBuildEform beform = new boBuildEform(p_eboctx);
            //                    beform.build(boDefHandler.getBoDefinition(defs[i].getName()));
            //                }
            //            }
            // Treating Scripts
            for (int i = 0; i < scriptFiles.size(); i++)
            {
                int j = ((Integer) scriptFiles.get(i)).intValue();
                String name = bofiles[j].getName().substring(0,
                        bofiles[j].getName().toLowerCase().indexOf( TYPE_SC ));
                File deployfile = new File(p_bcfg.getDeploymentDir() + name +
                        boBuilder.TYPE_SC);

                IOUtils.copy(bofiles[j], deployfile);

                deployfile.setLastModified(bofiles[j].lastModified() - 600);

                logger.debug("Treating ScriptObjectHandler:'" + name + "'");

                treatScript(p_eboctx, name);

                logger.debug("end...");
                deployfile.setLastModified(bofiles[j].lastModified());
            }

            for (short i = 0; i < repository.getPackages().size(); i++)
            {
                String packagename = (String) repository.getPackages().get(i);
                logger.debug("Registering Packages in Ebo_Package:'" +
                    packagename + "'");

                boObject pack = null;

                try
                {
                    pack = boObject.getBoManager().loadObject(p_eboctx,
                            "Ebo_Package", "NAME='" + packagename + "'");
                }
                catch (boRuntimeException e)
                {
                    logger.error(e);
                }

                try
                {
                    if ((pack == null) || !pack.exists())
                    {
                        pack = boObject.getBoManager().createObject(p_eboctx,
                                "Ebo_Package");
                    }

                    pack.getAttribute("name").setValueString(packagename);
                    if(pack.getAttribute("deployed") != null )
                    {
                        pack.getAttribute("deployed").setValueString("1");
                    }
//                pack.getAttribute("description").setValueString(packagename);
                    pack.update();
                }
                catch (Exception e)
                {
                    logger.error(e);
                    throw new boRuntimeException("", e.getMessage(), null);
                }

            }

            // Register classes in Ebo_ClsReg
            for (short i = 0; i < defs.length; i++)
            {
                if( i%20 == 0 )
                {
                    p_eboctx.getApplication().getMemoryArchive().getPoolManager().realeaseAllObjects( p_eboctx.poolUniqueId() );
                }

                logger.debug("Registering classes in Ebo_ClsReg:'" +
                    defs[i].getBoName() + "'");

                boObject clsreg = null;

                try
                {
                    clsreg = boObject.getBoManager().loadObject(p_eboctx,
                            "Ebo_ClsReg", "NAME='" + defs[i].getBoName() + "'");
                }
                catch (boRuntimeException e)
                {
                    logger.error(e);
                }

                if ((clsreg == null) || !clsreg.exists())
                {
                    clsreg = boObject.getBoManager().createObject(p_eboctx,
                            "Ebo_ClsReg");
                }

                String xml      = clsreg.getAttribute("xmlsource").getValueString();
                String newXml   = ngtXMLUtils.getXML(
                        (XMLDocument) boDefHandler.getBoDefinition(
                            defs[i].getBoName()).getNode().getOwnerDocument());
                if( !xml.equals( newXml ) )
                {
                    clsreg.getAttribute("name").setValueString(defs[i].getBoName());
                    clsreg.getAttribute("description").setValueString(defs[i].getDescription());
                    clsreg.getAttribute("label").setValueString(defs[i].getLabel());
                    clsreg.getAttribute("xmlsource").setValueString( newXml );
                    clsreg.getAttribute("phisicaltable").setValueString(defs[i].getBoPhisicalMasterTable());
                    if(clsreg.getAttribute("deployed") != null )
                    {
                        clsreg.getAttribute("deployed").setValueString("1");
                    }

                    boObject pack = boObject.getBoManager().loadObject(p_eboctx,
                            "Ebo_Package",
                            "NAME='" +
                            (String) htdeploypackage.get(defs[i].getBoName()) +
                            "'");

                    if (pack != null)
                    {
                        clsreg.getAttribute("xeopackage").setValueLong(pack.bo_boui);
                    }


                    buildAttributes(boDefHandler.getBoDefinition(defs[i].getName()),
                        clsreg);
                    buildMethods(boDefHandler.getBoDefinition(defs[i].getName()),
                        clsreg);

                    clsreg.update();
                }

                // Fill the array with class BOUI
                classesbouis[i] = clsreg.getBoui();

                //               netgest.bo.system.boPoolManager.realeaseAllObjects(p_eboctx.poolUniqueId());
            }
/*
            //register Interface in Ebo_Clrseg
            if(objectInterf != null && objectInterf.size() > 0)
            {
                Enumeration oEnum = objectInterf.keys();
                String mandInterface=null;

                while(oEnum.hasMoreElements())
                {
                    mandInterface = (String)oEnum.nextElement();
                    logger.debug("Registering interface in Ebo_Reg:'" + mandInterface + "'");
                    boObject clsreg = null;
                    try
                    {
                        boDefInterface bdefI = (boDefInterface)boDefHandler.getBoDefinition(mandInterface);
                        if( bdefI != null )
                        {
                            try
                            {
                                clsreg = boObject.getBoManager().loadObject(p_eboctx,
                                        "Ebo_ClsReg", "NAME='" + mandInterface + "'");
                            }
                            catch (boRuntimeException e)
                            {
                                logger.error(e);
                            }
                            if ((clsreg == null) || !clsreg.exists())
                            {
                                clsreg = boObject.getBoManager().createObject(p_eboctx,
                                        "Ebo_ClsReg");
                            }

                            clsreg.getAttribute("name").setValueString(mandInterface);
                            clsreg.getAttribute("description").setValueString(bdefI.getDescription());
                            clsreg.getAttribute("xmlsource").setValueString( null );
                            if(clsreg.getAttribute("deployed") != null )
                            {
                                clsreg.getAttribute("deployed").setValueString("1");
                            }
                            boObject pack = boObject.getBoManager().loadObject(p_eboctx,
                                "Ebo_Package",
                                "NAME='" +
                                (String) htdeploypackage.get(mandInterface) +
                                "'");
                            if (pack != null)
                            {
                                clsreg.getAttribute("xeopackage").setValueLong(pack.bo_boui);
                            }
                            clsreg.update();
                        }
                    }
                    catch( Exception e )
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
*/
              logger.debug("Redoing Normal Views for:'Ebo_ClsReg' and 'Ebo_Package'");
              specialView = new boBuildDB(p_eboctx);
              specialView.createInheritViews(boDefHandler.getBoDefinition("Ebo_ClsReg"));
              specialView = new boBuildDB(p_eboctx);
              specialView.createInheritViews(boDefHandler.getBoDefinition("Ebo_Package"));            
            }

            // Update timestamps of deployed files;
            logger.debug("Updating TimeStamps of the deployed files...");

            for (int i = 0; i < todeploy.size(); i++)
            {
                boDefHandler xdef = (boDefHandler) todeploy.get(i);
                String ext = (xdef.getClassType() == boDefHandler.TYPE_INTERFACE)?TYPE_INTERFACE:TYPE_BO;
                String oname = xdef.getName();

                oname += ext;

                File back = new File(p_bcfg.getDeploymentDir() + oname + ".bak");
                File ofile = new File(p_bcfg.getDeploymentDir() + oname );

                File srcfile = repository.getFile( xdef.getName(), ext );

                if (back.exists())
                {
                    back.delete();
                }

                ofile.setLastModified(srcfile.lastModified());
            }

            //jsp faz sempre
            boBuildJSP bjsp =null;

            // Build JSP's
            for (short i = 0; i < defs.length; i++)
            {
                logger.debug("Building JSP for:'" + defs[i].getBoName() +
                    "'");

                bjsp = new boBuildJSP();
                bjsp.generate(boDefHandler.getBoDefinition(defs[i].getBoName()));
            }

            if(objectInterf != null && objectInterf.size() > 0)
            {
                Enumeration oEnum = objectInterf.keys();
                String mandInterface=null;

                while(oEnum.hasMoreElements())
                {
                    mandInterface = (String)oEnum.nextElement();
                    bjsp = new boBuildJSP();
                    logger.debug("Building Mandatory Interface JSP for:'" + mandInterface + "'");
                    boDefHandler defI = boDefHandler.getBoDefinition(mandInterface);
                    if( defI != null )
                    {
                        bjsp.generate( defI );
                    }
                }
            }


            if(module == null || module.indexOf("jsp") == -1)
            {
                //criação da directoria resources
                for (short i = 0; i < defs.length; i++)
                {
                    try
                    {
                        createResources(defs[i].getBoName());
                    }
                    catch (IOException e)
                    {
                        logger.debug(
                            "Error creating resources directory for object: " +
                            defs[i].getBoName());
                    }
                }
            }

            //construção das Lov's
            for (int i = 0; i < lovFiles.size(); i++)
            {
                int j = ((Integer) lovFiles.get(i)).intValue();
                String name = bofiles[j].getName().substring(0,
                        bofiles[j].getName().toLowerCase().indexOf( boBuilder.TYPE_LOV ));
                File deployfile = new File(p_bcfg.getDeploymentDir() + name +
                        boBuilder.TYPE_LOV );

                IOUtils.copy(bofiles[j], deployfile);

                deployfile.setLastModified(bofiles[j].lastModified() - 600);

                logger.debug("Building LovObjectHandler:'" + name + "'");

                buildLov(name);

                logger.debug("end...");

                deployfile.setLastModified(bofiles[j].lastModified());
            }

            //construção das WSD's
            for (int i = 0; i < wsdFiles.size(); i++)
            {
                int j = ((Integer) wsdFiles.get(i)).intValue();
                String name = bofiles[j].getName().substring(0,
                        bofiles[j].getName().toLowerCase().indexOf(TYPE_WSD));
                File deployfile = new File(p_bcfg.getDeploymentDir() + name +
                        boBuilder.TYPE_WSD);
                logger.debug("Building WSD:'" + name + "'");

                IOUtils.copy(bofiles[j], deployfile);
                deployfile.setLastModified(bofiles[j].lastModified());
            }


            p_undeployeddefs.clear();
            boDefHandler.clearCache();
            p_eboctx.getApplication().getMemoryArchive().clearCachedEmptyDataSet();
            ExplorerServer.clearCache();
            
            logger.debug("Deploy take " +
                ((System.currentTimeMillis() - ms) / 1000) + " seconds.");
            logger.debug("Deploy finished.");

            success = true;
        }
        finally
        {
                if (!success)
            {
                logger.debug("Error deploying Files...");
                logger.debug("Rolling back deployment files...");
                for (int i = 0; i < todeploy.size(); i++)
                {
                    boDefHandler xdef = (boDefHandler) todeploy.get(i);
                    String oname = xdef.getName();
                    String ext = (xdef.getClassType() == boDefHandler.TYPE_INTERFACE)?TYPE_INTERFACE:TYPE_BO;
                    oname += ext;

                    File back = new File(p_bcfg.getDeploymentDir() + oname +
                            ".bak");
                    File ofile = new File(p_bcfg.getDeploymentDir() + oname );
                    File dsfile = new File(p_bcfg.getDeploymentDir() + oname );

                    if (back.exists())
                    {
                        IOUtils.copy(back, ofile);
                        back.delete();
                    }
                    else
                    {
                        for (short z = 0; z < bofiles.length; z++)
                        {
                            if (bofiles[z].getName().equals(ofile.getName()))
                            {
                                ofile.delete();
                                if(dsfile.exists())
                                {
                                    dsfile.delete();
                                }
                                break;
                            }
                        }
                    }
                }

                //interfaces
                for (int i = 0; i < xfiles.length; i++)
                {
                    if (xfiles[i].getName().toLowerCase().endsWith(TYPE_INTERFACE))
                    {
                        File oInterface = new File(p_bcfg.getDeploymentDir() + xfiles[i].getName());
                        if(oInterface.exists())
                        {
                            oInterface.delete();
                        }
                    }
                }                                

                logger.debug("Done.");
            }
            else
            {

                p_eboctx.getApplication().releaseClassLoader();
                docHTML_treeServer.clearCache();

            }
        }
    }

    private static void setDeployedObject(EboContext ctx, String module)
    {
        try
        {
            boBuildRepository repository = new boBuildRepository(ctx.getBoSession().getRepository());
        //setting deployed Ebo_clsreg and Ebo_package
            if(module != null && module.indexOf("setDeployedObjects") != -1)
            {
                File[] f = repository.getXMLFilesFromDefinition();
                ArrayList packages = repository.getPackages();
                String name;
                StringBuffer sbClsrg = new StringBuffer(" where ");
                StringBuffer sbPack = new StringBuffer(" where ");
                boObject pack = null;
                boolean first = true;
                boolean intf = false;
                for(int i = 0; i < f.length; i++)
                {
                    intf = f[i].getName().toLowerCase().endsWith(TYPE_INTERFACE);
                    if (f[i].getName().toLowerCase().endsWith(TYPE_BO) ||
                        intf)
                    {
                        name = f[i].getName().substring(0,
                            f[i].getName().toLowerCase().indexOf(!intf ? TYPE_BO:TYPE_INTERFACE));
                        boObject clsreg = null;

                        try
                        {
                            clsreg = boObject.getBoManager().loadObject(ctx,
                                    "Ebo_ClsReg", "NAME='" + name + "'");
                            if(clsreg.exists())
                            {
                                if(first)
                                {
                                    sbClsrg.append(" boui = ").append(clsreg.getBoui());
                                    first = false;
                                }
                                else
                                {
                                    sbClsrg.append(" or boui = ").append(clsreg.getBoui());
                                }
                            }
                        }
                        catch (boRuntimeException e)
                        {
                            logger.error(e);
                        }
                    }
                }
                first = true;
                for (int i = 0; i < packages.size(); i++)
                {
                    try
                    {
                        pack = boObject.getBoManager().loadObject(ctx,
                                "Ebo_Package", "NAME='" + (String)packages.get(i) + "'");
                        if(pack.exists())
                        {
                            if(first)
                            {
                                sbPack.append(" boui = ").append(pack.getBoui());
                                first = false;
                            }
                            else
                            {
                                sbPack.append(" or boui = ").append(pack.getBoui());
                            }
                        }
                    }
                    catch (boRuntimeException e)
                    {
                        logger.error(e);
                    }
                }
                ArrayList updates = new ArrayList();
                updates.add("update ebo_clsreg set deployed = '0'");
                updates.add("update ebo_package set deployed = '0'");
                updates.add("update ebo_clsreg set deployed = '1' " + sbClsrg.toString());
                updates.add("update ebo_package set deployed = '1' " + sbPack.toString());
                executeUpdate(ctx, updates);
            }
        }
        catch (boRuntimeException e)
        {
            logger.error("Não foi possível efectuar o setDeployed.");
        }
    }

    private static void executeUpdate(EboContext eboCtx, ArrayList updates) throws boRuntimeException
    {
        Connection con = null;
        CallableStatement csm = null;
        boolean ok = false;
        int n;

        try
        {
            final InitialContext ic = new InitialContext();
            UserTransaction ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            ut.setTransactionTimeout(6000000);
            String update = "";
            con = eboCtx.getDedicatedConnectionData();


            for (int i = 0; i < updates.size(); i++)
            {
                String sql = (String)updates.get(i);

                if ((sql != null) && (sql.length() > 0))
                {
                    csm = con.prepareCall(sql);
                    n = csm.executeUpdate();
                    csm.close();
                    logger.debug("Executed Query (" +
                            sql + ") updated " + n +
                            " records.");
                }
            }
            ok = true;
            con.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(ok) eboCtx.commitContainerTransaction();
            else eboCtx.rollbackContainerTransaction();
            eboCtx.close();
            try
            {
                if (con != null)
                {
                    con.rollback();
                    con.close();
                }
            }
            catch (Exception e)
            {
                //ignora
            }

            try
            {
                if (csm != null)
                {
                    csm.close();
                }
            }
            catch (Exception e)
            {
                //ignora
            }
        }
    }
    private void makeBuildDep(String boname, Stack stack, boolean checkdep)
    {
        makeBuildDep(boname, stack, checkdep, false,true);
    }

    public static void buildMethods(boDefHandler bodef, boObject clsreg)
        throws boRuntimeException
    {
        try
        {
            bridgeHandler bridge = clsreg.getBridge("methods");
            boObject method = null;

            boDefMethod[] a_met;
            String objName = bodef.getName();
            a_met = bodef.getBoMethods();

            boDefMethod metdef;

            for (int i = 0; i < a_met.length; i++)
            {
                metdef = a_met[i];

                String x_name = metdef.getName();

                boolean find = false;
                bridge.beforeFirst();

                while (!find && bridge.next())
                {
                    method = bridge.getObject();

                    if (method.getAttribute("name").getValueString()
                                  .equalsIgnoreCase(x_name))
                    {
                        find = true;
                        method.edit();
                    }
                }

                if (!find)
                {
                    method = bridge.addNewObject("Ebo_Method");
                }

                String methodName = metdef.getName();
                String mthLabel = metdef.getLabel();
                method.getAttribute("name").setValueString(methodName);
                method.getAttribute("description").setValueString(methodName);
                method.getAttribute("label").setValueString(mthLabel == null ? methodName:mthLabel);
                method.getAttribute("clsReg").setObject(clsreg);
            }

            bridge.beforeFirst();

            Hashtable auxToCreate = new Hashtable();

            while (bridge.next())
            {
                boolean find = false;
                String name1 = bridge.getObject().getAttribute("name")
                                     .getValueString();

                for (int i = 0; i < a_met.length; i++)
                {
                    if (a_met[i].getName().equals(name1))
                    {
                        find = true;
                    }
                }

                if (name1.equals("Print") || name1.equals("Tree") ||
                        name1.equals("Properties") || name1.equals("Graph") ||
                        name1.equals("CreateTemplate") ||
                        name1.equals("ExportObject"))
                {
                    find = true;
                    auxToCreate.put(name1, name1);
                }

                if (!find)
                {
                    bridge.remove();
                }
            }

            if (auxToCreate.get("Print") == null)
            {
                method = bridge.addNewObject("Ebo_Method");
                method.getAttribute("name").setValueString("Print");
                method.getAttribute("description").setValueString("Print Object");
                method.getAttribute("clsReg").setObject(clsreg);
            }

            if (auxToCreate.get("Tree") == null)
            {
                method = bridge.addNewObject("Ebo_Method");
                method.getAttribute("name").setValueString("Tree");
                method.getAttribute("description").setValueString("View Object Tree");
                method.getAttribute("clsReg").setObject(clsreg);
            }

            if (auxToCreate.get("Properties") == null)
            {
                method = bridge.addNewObject("Ebo_Method");
                method.getAttribute("name").setValueString("Properties");
                method.getAttribute("description").setValueString("View Object Properties");
                method.getAttribute("clsReg").setObject(clsreg);
            }

            if (auxToCreate.get("Graph") == null)
            {
                method = bridge.addNewObject("Ebo_Method");
                method.getAttribute("name").setValueString("Graph");
                method.getAttribute("description").setValueString("View Object Graph");
                method.getAttribute("clsReg").setObject(clsreg);
            }

            if (auxToCreate.get("CreateTemplate") == null)
            {
                method = bridge.addNewObject("Ebo_Method");
                method.getAttribute("name").setValueString("CreateTemplate");
                method.getAttribute("description").setValueString("Create Object Template");
                method.getAttribute("clsReg").setObject(clsreg);

            }

            if (auxToCreate.get("ExportObject") == null)
            {
                method = bridge.addNewObject("Ebo_Method");
                method.getAttribute("name").setValueString("ExportObject");
                method.getAttribute("description").setValueString("Export Object Info");
                method.getAttribute("clsReg").setObject(clsreg);
            }

            if (bodef.getBoClsState() != null)
            {
                String[] stateMethods = bodef.getBoClsState()
                                             .getAllStateMethods();

                if (stateMethods != null)
                {
                    for (int i = 0; i < stateMethods.length; i++)
                    {
                        String methodName = stateMethods[i];
                        boolean find = false;
                        bridge.beforeFirst();

                        while (!find && bridge.next())
                        {
                            method = bridge.getObject();

                            if (method.getAttribute("name").getValueString()
                                          .equalsIgnoreCase(methodName))
                            {
                                find = true;
                                method.edit();
                            }
                        }

                        if (!find)
                        {
                            method = bridge.addNewObject("Ebo_Method");
                        }

                        method.getAttribute("name").setValueString(methodName);
                        method.getAttribute("description").setValueString(methodName);
                        method.getAttribute("clsReg").setObject(clsreg);
                    }
                }
            }
        }
        catch (boException e)
        {
            //throw(e);
        }
    }

    public void buildAttributes(boDefHandler bodef, boObject clsreg)
        throws boRuntimeException
    {
        try
        {
            bridgeHandler bridge = clsreg.getBridge("attributes");
            boObject attribute = null;
            boDefAttribute[] a_att;
            String objName = bodef.getName();
            a_att = bodef.getBoAttributes();

            boDefAttribute atrdef;

            for (int i = 0; i < a_att.length; i++)
            {
                atrdef = a_att[i];

                String x_name = atrdef.getName();

                boolean find = false;
                bridge.beforeFirst();

                while (!find && bridge.next())
                {
                    attribute = bridge.getObject();

                    if (attribute.getAttribute("name").getValueString()
                                     .equalsIgnoreCase(x_name))
                    {
                        find = true;
                        attribute.edit();
                    }
                }

                if (!find)
                {
                    if (atrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                    {
                        attribute = bridge.addNewObject("Ebo_Attribute");
                    }
                    else if (atrdef.getAtributeType() == boDefAttribute.TYPE_STATEATTRIBUTE)
                    {
                        attribute = bridge.addNewObject("Ebo_Attribute");
                    }
                    else
                    {
                        attribute = bridge.addNewObject("Ebo_Attribute");
                    }

                    //falta desenvolver os sub-objectos do attributo
                }

                if (atrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                {
                    if ((atrdef.getBridge() != null) &&
                            atrdef.getBridge().haveBridgeAttributes())
                    {
                        boDefAttribute[] b_batt = a_att[i].getBridge()
                                                          .getBoAttributes();

                        for (int z = 0; z < b_batt.length; z++)
                        {
                            //    if(b_batt[z].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE && b_batt[z].getReferencedObjectName().equals(boname))
                            //    {
                            //   }
                        }
                    }
                }

                if (atrdef.getLovItems() != null)
                {
                    String lovName = atrdef.getLOVName();
                    boolean rtValues = atrdef.getLovRetainValues();
                    boBuildLov l = new boBuildLov(p_eboctx);
                    l.buildLov(lovName, rtValues, atrdef.getLovItems());
                }

                String attributeName = atrdef.getName();
                String attributeLabel = atrdef.getLabel();
                attribute.getAttribute("name").setValueString(atrdef.getName());
                attribute.getAttribute("description").setValueString(atrdef.getDescription());
                attribute.getAttribute("label").setValueString(attributeLabel == null ? attributeName:attributeLabel);
                attribute.getAttribute("clsReg").setObject(clsreg);
            }

            bridge.beforeFirst();

            while (bridge.next())
            {
                String name1 = bridge.getObject().getAttribute("name")
                                     .getValueString();

                if (!bodef.hasAttribute(name1))
                {
                    bridge.remove();
                }
            }
        }
        catch (boException e)
        {
            //throw(e);
        }
    }

    private void makeBuildDep(String boname, Stack stack, boolean checkdep,
        boolean changord,boolean checkDescendants)
    {
        boBuildRepository repository = new boBuildRepository(p_eboctx.getBoSession()
                                                                     .getRepository());
//VERIFICAR
        boDefHandler cdef = getUndeployedDefinitions(repository, boname, interfacedeploy, false);

        if( cdef == null )
        {
            boolean toBreak = true;
            return;
        }
        if ((stack.indexOf(boname) == -1) && !boname.equals("boObject"))
        {
            stack.push(boname);
            if (cdef.getBoSuperBo() != null)
            {
                makeBuildDep(cdef.getBoSuperBo(), stack, false, true,false);
            }

            if ((cdef.getBoInterfaces() != null) &&
                    (cdef.getBoInterfaces().length > 0))
            {
                for (byte i = 0; i < cdef.getBoInterfaces().length; i++)
                {
                    makeBuildDep(cdef.getBoInterfaces()[i].getName(), stack,
                        false, true,true);
                }
            }

            boDefHandler[] allbo = listUndeployedDefinitions(repository, interfacedeploy);
            boDefAttribute[] a_att;

            for (short j = 0; j < allbo.length; j++)
            {
                if ((allbo[j].getBoSuperBo() != null) &&
                        allbo[j].getBoSuperBo().equals(boname) && checkDescendants)
                {
                    //if(stack.indexOf(allbo[j].getBoSuperBo())==-1)
                    //{
                    logger.debug("Found reference for [" + boname +
                        "] in :" + allbo[j].getName());
                    makeBuildDep(allbo[j].getBoName(), stack, checkdep);

                    //}
                }

                if (cdef.getClassType() == boDefHandler.TYPE_INTERFACE)
                {
                    if (allbo[j].getBoImplements(boname))
                    {
                        logger.debug("Found reference for [" + boname +
                            "] in :" + allbo[j].getName());
                        makeBuildDep(allbo[j].getBoName(), stack, checkdep);
                    }
                }

                if (checkdep)
                {
                    a_att = allbo[j].getBoAttributes();

                    for (int i = 0; i < a_att.length; i++)
                    {
                        if ((a_att[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                a_att[i].getReferencedObjectName().equals(boname))
                        {
                            if ((a_att[i].getBridge() != null) &&
                                    a_att[i].getBridge().haveBridgeAttributes())
                            {
                                boDefAttribute[] b_batt = a_att[i].getBridge()
                                                                  .getBoAttributes();

                                for (int z = 0; z < b_batt.length; z++)
                                {
                                    if ((b_batt[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                                            b_batt[z].getReferencedObjectName()
                                                         .equals(boname))
                                    {
                                        //if(stack.indexOf(b_batt[z].getReferencedObjectName())==-1)
                                        //{
                                        logger.debug("Found reference for [" +
                                            boname + "] in :" +
                                            b_batt[z].getBoDefHandler().getName());
                                        makeBuildDep(b_batt[z].getReferencedObjectName(),
                                            stack, checkdep, false,true);

                                        //stack.push();
                                        //}
                                        //makeBuildDep(b_batt[z].getReferencedObjectName(),stack);
                                    }
                                }
                            }

                            //if(stack.indexOf(allbo[j].getBoName())==-1)
                            //{
                            logger.debug("Found reference for [" + boname +
                                "] in :" + allbo[j].getBoName());

                            makeBuildDep(allbo[j].getBoName(), stack, checkdep,
                                false,true);

                            //                                stack.push(allbo[j].getBoName());
                            //}
                            //makeBuildDep(allbo[j].getBoName(),stack);
                        }
                    }
                }

                /*boDefHandler def = this.getUndeployedDefinitions(boname);
                if(def!=null) {
                    a_att = def.getBoAttributes();
                    for(int i=0;i<a_att.length;i++) {
                        if(a_att[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                            if(a_att[i].getBridge().haveBridgeAttributes()) {
                                boDefAttribute[] b_batt = a_att[i].getBridge().getBoAttributes();
                                for(int z=0;z<b_batt.length;z++) {
                                    if(b_batt[z].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                        makeBuildDep(b_batt[z].getReferencedObjectName(),stack);
                                    }
                                }
                            }
                            makeBuildDep(a_att[i].getReferencedObjectName(),stack);
                        }
                    }
                }*/
            }
        }
        else
        {
            if (changord && !boname.equals("boObject"))
            {
                stack.remove(boname);
                stack.push(boname);

                if (cdef.getBoSuperBo() != null)
                {
                    makeBuildDep(cdef.getBoSuperBo(), stack, false, true,true);
                }
            }
        }
    }

    /*    private void buildIfModified(String boname) throws boRuntimeException {
                File src = new File(p_bcfg.getDefinitiondir()+boname+TYPE_BO);
                File dest = new File(p_bcfg.getDeploymentDir()+boname+TYPE_BO);
                if (src.lastModified()>dest.lastModified() || !dest.exists()) {
                    deploy(boname,true);
                }
        }*/
    /*    private void deploy(String boname,boolean full) throws boRuntimeException {
            try {
                boDefAttribute[] a_att;

                // Cannot deploy system objects with p_registry switched to true

                if((boname.equalsIgnoreCase("Ebo_Registry") || boname.equalsIgnoreCase("Ebo_ClsReg")
                     || boname.equalsIgnoreCase("Ebo_Schedule") ) && p_register) {
                    return;
                }

                if(p_buildqueue.indexOf(boname)==-1) {
                    if(!boname.equalsIgnoreCase("boObject")) {
                        p_buildqueue.add(boname);
                        File deployfile = new File(p_bcfg.getDeploymentDir()+boname+TYPE_BO);
                        // Get de definitions e directory
                        File sbofile = new File(p_bcfg.getDefinitiondir()+boname+TYPE_BO);
                        boDefHandler bobj = getUndeployedDefinitions(boname);

                        // Definitions doesn't exist do nothing
                        if(bobj==null) return;

                        // Create Deployment XML with inherit Attributes and ...
                        boDefHandler bobjinherit = getUndeployedDefinitions(boname);
                        buildInherit(bobjinherit);

                        // Copy file with inherit's to the dployment directory
                        FileOutputStream os = new FileOutputStream(deployfile);
                        ((XMLDocument)bobjinherit.getNode().getOwnerDocument()).print(os);
                        os.close();
                        deployfile.setLastModified(sbofile.lastModified()-60000);

                        a_att = bobj.getBoAttributes();

                        // Check if the superclass need a build
                        if(bobj.getBoSuperBo()!=null) {
                            this.buildIfModified(bobj.getBoSuperBo());
                        }

                        // Check relations of the Object and Build Modified
    /*                    if(p_register) {
                            for(int i=0;i<a_att.length;i++) {
                                if(a_att[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                    if(a_att[i].getBridge().haveBridgeAttributes()) {
                                        boDefAttribute[] b_batt = a_att[i].getBridge().getBoAttributes();
                                        for(int z=0;z<b_batt.length;z++) {
                                            if(b_batt[z].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                                this.buildIfModified(b_batt[z].getReferencedObjectName());
                                            }
                                        }
                                    }
                                    this.buildIfModified(a_att[i].getReferencedObjectName());
                                }
                            }
                        }*--/

                        v1_0.Ebo_ClsReg clsreg=null;
                        if(p_register) {
                            try {
                                clsreg = (v1_0.Ebo_ClsReg)boObject.getBoManager().loadObject(p_eboctx,"Ebo_ClsReg","NAME='"+boname+"'");
                            } catch (boRuntimeException e) {
                                clsreg = (v1_0.Ebo_ClsReg)boObject.getBoManager().createObject(p_eboctx,"Ebo_ClsReg");
                            }
                        }

                        // Build Java Classes
                        logger.debug("Building Class for:'"+boname+"'");
    //                    if(full) {
                            boClassBuilder bcl = new boClassBuilder();
                            if(p_register) {
                                bcl.build(bobj,clsreg.getBoui());
                            } else {
                                bcl.build(bobj,0);
                            }
    //                    }

    /*                    // Save orgininal XML
                        CharArrayWriter cw = new CharArrayWriter();
                        PrintWriter pw = new PrintWriter(cw);
                        ((XMLDocument)bobj.getNode().getOwnerDocument()).print(pw);
                        pw.close();
                        cw.close();*--/

    /--*                    // Create Deployment XML with inherit Attributes and ...
                        buildInherit(bobj);*--/

                        logger.debug("Building DB for:'"+boname+"'");
                        // Build Database Objects
                        boBuildDB bdb = new boBuildDB(p_eboctx);
                        bdb.buildObject(bobj,false,p_register);

                        // Build JSP
                        logger.debug("Building JSP for:'"+boname+"'");
                        boBuildJSP bjsp = new boBuildJSP();
                        bjsp.generate(bobjinherit);

                        // Build Eforms
                        //if(full) {
                            logger.debug("Building Eform for:'"+boname+"'");
                            boBuildEform befm = new boBuildEform(p_eboctx);
                            befm.build(bobj);
                        //}

                        //if(full) {
                            /*if(p_register) {
                                logger.debug("Registering Activities for:'"+boname+"'");
                                boBuildActivities bactv = new boBuildActivities(p_eboctx);
                                bactv.build(bobjinherit);
                            }*/

    //}
    // Copy files to deploypment dir and register object in Ebo_ClsReg
    // Check all subclasses of this object and build them

    /*                    if(p_register) {
                            boDefHandler[] allbo = boDefHandler.listBoDefinitions();
                            for (short j = 0; j < allbo.length; j++)  {
                                if(allbo[j].getBoSuperBo()!=null && allbo[j].getBoSuperBo().equals(boname)) {
                                    this.deploy(allbo[j].getBoName(),true);
                                }
                                a_att = allbo[j].getBoAttributes();
                                logger.debug("Check dep for "+boname+" in :"+allbo[j].getBoName());
                                for(int i=0;i<a_att.length;i++) {
                                    if(a_att[i].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE && a_att[i].getReferencedObjectName().equals(boname)) {
                                        if(a_att[i].getBridge().haveBridgeAttributes()) {
                                            boDefAttribute[] b_batt = a_att[i].getBridge().getBoAttributes();
                                            for(int z=0;z<b_batt.length;z++) {
                                                if(b_batt[z].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                                    //if(p_depqueue.indexOf(boname+"-->"+allbo[j].getBoName())== -1) {
                                                    //    p_depqueue.add(boname+"-->"+allbo[j].getBoName());
                                                        this.deploy(allbo[j].getBoName(),full);
                                                   // }
                                                }
                                            }
                                        }
                                        //if(p_depqueue.indexOf(boname+"-->"+allbo[j].getBoName())== -1) {
                                            //p_depqueue.add(boname+"-->"+allbo[j].getBoName());
                                            //this.p_buildqueue.remove(allbo[j].getBoName());
                                            this.deploy(allbo[j].getBoName(),false);
                                        //}
                                    }
                                }

                            }
                        }*--/
                        if(p_register) {
                            clsreg.setAttributename(boname);
                            clsreg.setAttributedescription(bobj.getBoDescription());
                            clsreg.setAttributexmlsource(ngtXMLUtils.getXML((XMLDocument)bobj.getNode().getOwnerDocument()));
                            clsreg.update();
                        }

                        // Update deployment file;
                        if(p_register) {
                            os = new FileOutputStream(deployfile);
                            ((XMLDocument)bobjinherit.getNode().getOwnerDocument()).print(os);
                            os.close();
                        }
                    }
                    logger.debug("-- END BUILD "+boname);
                }
                boDefHandler.clearCache();

            } catch (IOException e) {
                throw new boException(this.getClass().getName()+".deploy(String)","BO-1501",e,boname);
            }
        }*/
    public void deployClassRegistry() throws boRuntimeException
    {
        /*        try {
                    ScheduleAgentThread.suspendAgent();
                    this.p_register=false;
                    String[] boname = {"Ebo_Map","Ebo_Template","Ebo_Perf","Ebo_ClsReg","Ebo_TextIndex","Ebo_Registry","Ebo_Schedule"};
                    boDefHandler.clearCache();
                    for (byte i = 0; i < boname.length; i++)  {
                        this.deploy(boname[i],true);
                    }
                    this.p_register=true;
                    boBuildActivities bact = new boBuildActivities(this.p_eboctx);
                    for (byte i = 0; i < boname.length; i++)  {
                        boDefHandler xdef = boDefHandler.getBoDefinition(boname[i]);
        //                bact.build(xdef);
                    }
                     //boClassBuilder.compileClasses(p_bcfg);
               } finally {
                    ScheduleAgentThread.resumeAgent();
                }*/
    }

    public String getDML()
    {
        return p_dml;
    }

    // Deprecated Code
    public static final void buildInterfaces(boBuildRepository repository,
        boDefHandler bodef, Hashtable interfaceMap)
    {
        boDefHandler[] interfs = bodef.getBoInterfaces();

        String attName = null;
        for (int i = 0; (interfs != null) && ( i < interfs.length && interfs[i].getInterfaceType()==boDefHandler.INTERFACE_OPTIONAL ); i++)
        {
//VERIFICAR
            inheritObject(interfs[i], bodef, true, true, false);

            ngtXMLHandler atts = bodef.getChildNode("attributes");
            attName = "implements_" +interfs[i].getName();
            if(bodef.getAttributeRef(attName) == null)
            {
                atts.getNode().appendChild(
                        boDefUtils.createAttribute( attName, "I$" + interfs[i].getName(),
                        "Implements interface " + interfs[i].getName(), "attributeText","",1,false,bodef.getDocument()) );
            }
        }

        bodef.refresh();
        fillInterfaceAttributes(repository, bodef, interfaceMap);
        fillSystemAttributes(repository, bodef, interfaceMap);
    }

    private static final void buildInherit(boBuildRepository repository,
        boDefHandler bodef, Hashtable objectInterfaceMap, boolean createdFwdMethods) throws boRuntimeException
    {
        if (bodef.getBoSuperBo() != null)
        {
            String superbo = bodef.getBoSuperBo();
            boDefHandler bo = boBuilder.getUndeployedDefinitions(repository,
                    superbo, objectInterfaceMap, createdFwdMethods);
            _buildInherit(repository, bo, bodef, true, objectInterfaceMap, createdFwdMethods);
        }
        else if(createdFwdMethods)
        {
            createMethodsFromFwdObjects(bodef);
        }
        bodef.refresh();
        fillInterfaceAttributes(repository, bodef, objectInterfaceMap);
        fillSystemAttributes(repository, bodef, objectInterfaceMap);
    }

    private static final void _buildInherit(boBuildRepository repository,
        boDefHandler superbo, boDefHandler subbo, boolean inherit, Hashtable objectInterfaceMap, boolean createdFwdMethods)
    {
        inheritObject(superbo, subbo, inherit, false, createdFwdMethods);

        if (superbo.getBoSuperBo() != null)
        {
            boDefHandler bo = boBuilder.getUndeployedDefinitions(repository,
                    superbo.getBoSuperBo(), objectInterfaceMap, createdFwdMethods);
            _buildInherit(repository, bo, subbo, false, objectInterfaceMap, createdFwdMethods);
        }
    }

    public static final void fillInterfaceAttributes(
        boBuildRepository repository, boDefHandler bodef, Hashtable objectInterfaceMap)
    {
        try
        {
            if (bodef.getClassType() == boDefHandler.TYPE_INTERFACE)
            {
                return;
            }
            ngtXMLHandler atts = bodef.getChildNode("attributes");

            String[] objImpl = null;
            ArrayList alredyDone = new ArrayList();
            ArrayList arrayOfInterfaces = null;
            if(objectInterfaceMap != null && (arrayOfInterfaces = (ArrayList)objectInterfaceMap.get(bodef.getName())) != null)
            {
                boDefInterface intf;
                String intfName;
                boolean changed = false;
                for (int i = 0; i < arrayOfInterfaces.size(); i++)
                {
                    changed = false;
                    intfName = (String)arrayOfInterfaces.get(i);
                    if(!alredyDone.contains(intfName))
                    {
                        intf = boDefHandler.getInterfaceDefinition(intfName);
                        if( intf == null )
                        {
                            logger.warn("Interface "+intfName+" does not exists.");
                            continue;
                        }

                        boDefAttribute[] intfAtts = intf.getImplAttributes();
                        if(intfAtts != null)
                        {
                            for (int j = 0; j < intfAtts.length; j++)
                            {
                                if ( !bodef.hasAttribute( intfAtts[j].getName() ) )
                                {
                                    Node auxN =  atts.getNode().getOwnerDocument().importNode(intfAtts[j].getNode(), true);
                                    Attr atr = atts.getNode().getOwnerDocument().createAttribute("fromInterface");
                                    atr.setValue(intf.getName());
                                    ((XMLElement)auxN).setAttributeNode(atr);
                                    atts.getNode().appendChild(auxN);
                                    changed = true;
                                }
                            }
                        }
                        alredyDone.add(intfName);
                    }
                    if(changed)
                    {
                        bodef.refresh();
                    }
                }
            }
        }
        catch( ClassCastException e )
        {
            throw e;
        }
    }

    public static final void fillInterfaceMethdos(
        boBuildRepository repository, boDefHandler bodef, Hashtable objectInterfaceMap)
    {
        if (bodef.getClassType() == boDefHandler.TYPE_INTERFACE)
        {
            return;
        }
        ngtXMLHandler atts = bodef.getChildNode("methods");

        String[] objImpl = null;
        ArrayList alredyDone = new ArrayList();
        ArrayList arrayOfInterfaces = null;
        if(objectInterfaceMap != null && (arrayOfInterfaces = (ArrayList)objectInterfaceMap.get(bodef.getName())) != null)
        {
            boDefInterface intf;
            String intfName;
            for (int i = 0; i < arrayOfInterfaces.size(); i++)
            {
                intfName = (String)arrayOfInterfaces.get(i);
                if(!alredyDone.contains(intfName))
                {
                    intf = boDefHandler.getInterfaceDefinition(intfName);
                    if( intf == null )
                    {
                        continue;
                    }
                    boDefMethod[] intfMethods = intf.getImplMethods();
                    if(intfMethods != null)
                    {
                        for (int j = 0; j < intfMethods.length; j++)
                        {
                            if (!bodef.hasMethod( intfMethods[j].getName() ))
                            {
                                Node auxN =  atts.getNode().getOwnerDocument().importNode(intfMethods[j].getNode(), true);
                                Attr atr = atts.getNode().getOwnerDocument().createAttribute("fromInterface");
                                atr.setValue(intf.getName());
                                ((XMLElement)auxN).setAttributeNode(atr);
                                atts.getNode().appendChild(auxN);
                            }
                        }
                    }
                    alredyDone.add(intfName);
                }
            }
        }
        bodef.refresh();
    }

    public static final void fillInterfaceEvents(
        boBuildRepository repository, boDefHandler bodef, Hashtable objectInterfaceMap)
    {
        if (bodef.getClassType() == boDefHandler.TYPE_INTERFACE)
        {
            return;
        }
        ngtXMLHandler atts = bodef.getChildNode("events");

        String[] objImpl = null;
        ArrayList alredyDone = new ArrayList();
        ArrayList arrayOfInterfaces = null;
        if(objectInterfaceMap != null && (arrayOfInterfaces = (ArrayList)objectInterfaceMap.get(bodef.getName())) != null)
        {
            boDefInterface intf;
            String intfName;
            for (int i = 0; i < arrayOfInterfaces.size(); i++)
            {
                intfName = (String)arrayOfInterfaces.get(i);
                if(!alredyDone.contains(intfName))
                {
                    intf = boDefHandler.getInterfaceDefinition(intfName);
                    if( intf == null )
                    {
                        continue;
                    }
                    boDefClsEvents[] intfEvents = intf.getImplEvents();
                    if(intfEvents != null)
                    {
                        for (int j = 0; j < intfEvents.length; j++)
                        {
                            if (!bodef.hasEvent( intfEvents[j].getEventName() ) )
                            {
                                Node auxN =  atts.getNode().getOwnerDocument().importNode(intfEvents[j].getNode(), true);
                                Attr atr = atts.getNode().getOwnerDocument().createAttribute("fromInterface");
                                atr.setValue(intf.getName());
                                ((XMLElement)auxN).setAttributeNode(atr);
                                atts.getNode().appendChild(auxN);
                            }
                        }
                    }
                    alredyDone.add(intfName);
                }
            }
        }
        bodef.refresh();
    }

    public static final void fillSystemAttributes(
        boBuildRepository repository, boDefHandler bodef, Hashtable objectInterfaceMap)
    {
        /*if (bodef.getClassType() == boDefHandler.TYPE_INTERFACE)
        {
            return;
        }*/

        ngtXMLHandler atts = bodef.getChildNode("attributes");

        if (!bodef.hasAttribute("PARENT"))
        {
            boDefHandler xderiv = bodef;
            boolean multiparent = xderiv.getBoHaveMultiParent();

            while (!multiparent && (xderiv.getBoSuperBo() != null) &&
                    (xderiv.getBoSuperBo().trim().length() > 0))
            {
//VERIFICAR
                String name       = xderiv.getName();
                String xderivName = xderiv.getBoSuperBo();
                xderiv = getUndeployedDefinitions(repository,
                        xderiv.getBoSuperBo(), objectInterfaceMap, false);

                if( xderiv == null )
                {
                    throw new RuntimeException("Object ["+name+"] extends a unknown object ["+ xderivName+"]");
                }


                if (xderiv.getBoHaveMultiParent())
                {
                    multiparent = true;
                }
            }
//VERIFICAR
            if (!bodef.getBoIsSubBo() ||
                    (getUndeployedDefinitions(repository, bodef.getBoSuperBo(), objectInterfaceMap, false)
                             .getBoHaveMultiParent() != multiparent))
            {
                atts.getNode().appendChild(
                        boDefUtils.createAttribute("PARENT", "PARENT$",
                        "Objecto Pai", "attributeObject","object.boObject", 0,multiparent,
                        atts.getNode().getOwnerDocument()));
            }
        }

        if (!bodef.getBoIsSubBo())
        {
            if (!bodef.hasAttribute("PARENTCTX"))
            {
                atts.getNode().appendChild(boDefUtils.createAttribute("PARENTCTX",
                        "PARENTCTX$", "Contexto de Criação", "attributeObject","object.boObject",0,false,
                        atts.getNode().getOwnerDocument()));
            }

            if (!bodef.hasAttribute("TEMPLATE") &&
                    !bodef.getName().equals("Ebo_Template") &&
                    !bodef.getName().equals("Ebo_Map"))
            {
                atts.getNode().appendChild(boDefUtils.createAttribute("TEMPLATE",
                        "TEMPLATE$", "Modelo","attributeObject", "object.Ebo_Template",0,false,
                        atts.getNode().getOwnerDocument()));
            }

            if (!bodef.hasAttribute("BOUI"))
            {
                atts.getNode().appendChild(boDefUtils.createAttribute("BOUI", "BOUI",
                        "BOUI", "attributeNumber","",0,false, atts.getNode().getOwnerDocument()));
            }

            if (!bodef.hasAttribute("CLASSNAME"))
            {
                atts.getNode().appendChild(boDefUtils.createAttribute("CLASSNAME",
                        "CLASSNAME", "Categoria do Objecto","attributeText", "",50,false,
                        atts.getNode().getOwnerDocument()));
            }

            if (!bodef.hasAttribute("CREATOR"))
            {
                atts.getNode().appendChild(boDefUtils.createAttribute("CREATOR",
                        "CREATOR$", "Criador","attributeObject", "object.iXEOUser",0,false,
                        atts.getNode().getOwnerDocument()));
            }

            if (!bodef.hasAttribute("SYS_DTCREATE"))
            {
                atts.getNode().appendChild(boDefUtils.createAttribute("SYS_DTCREATE",
                        "SYS_DTCREATE", "Data de Criação", "attributeDateTime","",0,false,
                        atts.getNode().getOwnerDocument()));
            }

            if (!bodef.hasAttribute("SYS_DTSAVE"))
            {
                atts.getNode().appendChild(boDefUtils.createAttribute("SYS_DTSAVE",
                        "SYS_DTSAVE", "Data da última actualização","attributeDateTime",
                        "",0,false, atts.getNode().getOwnerDocument()));
            }

            if (!bodef.hasAttribute("SYS_ORIGIN"))
            {
                atts.getNode().appendChild(boDefUtils.createAttribute("SYS_ORIGIN",
                        "SYS_ORIGIN", "Origem dos dados", "attributeText","",30,false,
                        atts.getNode().getOwnerDocument()));
            }
            if (!bodef.hasAttribute("SYS_FROMOBJ"))
            {
                atts.getNode().appendChild(boDefUtils.createAttribute("SYS_FROMOBJ",
                        "SYS_FROMOBJ$", "Objecto Origem", "attributeObject","object.boObject",0,false,
                        atts.getNode().getOwnerDocument()));
            }
        }

        fillBrigdesAtts(bodef);
        bodef.refresh();
    }

    private static final void fillBrigdesAtts(boDef def)
    {
        boDefAttribute[] atts = def.getBoAttributes();

        for (short i = 0; i < atts.length; i++)
        {
            if ((atts[i].getAtributeType() == atts[i].TYPE_OBJECTATTRIBUTE) &&
                    (atts[i].getMaxOccurs() > 1))
            {
                if (!atts[i].getBridge().hasAttribute("LIN"))
                {
                    if (atts[i].getChildNode("bridge") == null)
                    {
                        atts[i].getNode().appendChild(atts[i].getNode()
                                                             .getOwnerDocument()
                                                             .createElement("bridge"));
                    }

                    if (atts[i].getChildNode("bridge").getChildNode("attributes") == null)
                    {
                        atts[i].getChildNode("bridge").getNode().appendChild(atts[i].getNode()
                                                                                    .getOwnerDocument()
                                                                                    .createElement("attributes"));
                    }

                    atts[i].getChildNode("bridge").getChildNode("attributes")
                           .getNode().appendChild(boDefUtils.createAttribute("LIN", "LIN",
                            "Linha", "attributeNumber","NUMBER",0,false,
                            atts[i].getNode().getOwnerDocument()));
                }
            }
        }
    }

    private static final void createMethodsFromFwdObjects(boDefHandler bodef) throws boRuntimeException
    {
        try
        {
            //inherited fwdObjects
            boDefForwardObject[] fwdObjs = bodef.getForwardObjects();
            boDefForwardObject fwdObj;
            String code;
            Element method = null;
            Element methods = (Element) ((XMLElement) bodef.getNode()).selectSingleNode(
                            "Methods");
            if(fwdObjs != null)
            {
                CodeJavaConstructor cjc = new CodeJavaConstructor();
                cjc.setValues(bodef);
                for (int i = 0; i < fwdObjs.length; i++)
                {
                    fwdObj = fwdObjs[i];
                    if(bodef.getBoMethod(fwdObj.getMapMethodName()) == null)
                    {
                        code = cjc.getForwardMapCode(fwdObj.toBoObject());
                        ArrayList paramNames = new ArrayList();
                        paramNames.add("fwdBoui");
                        ArrayList paramTypes = new ArrayList();
                        paramTypes.add("long");
                        String hiddenWhen = "CODE_JAVA(if(!this.exists()){return true;} return false;)";
                        method = boDefUtils.createMethod(
                                fwdObj.getMapMethodName(), "boObject",
                                "fwdObject", fwdObj.toBoObject(),
                            fwdObj.openDoc(), true, true, false, fwdObj.getLabel(),
                            fwdObj.getLabel(), false, code, hiddenWhen, null, null,methods.getOwnerDocument());
                        methods.appendChild(method);
                        method = boDefUtils.createMethod(fwdObj.getBeforeMapMethodName(), "void",
                            "fwdObject", null,
                            false, false, false, false, "afterMap",
                            "afterMap", false, fwdObj.getBeforeMapClass(), null, paramTypes, paramNames,methods.getOwnerDocument());
                        methods.appendChild(method);
                        method = boDefUtils.createMethod(fwdObj.getAfterMapMethodName(), "void",
                            "fwdObject", null,
                            false, false, false, false, "beforeMap",
                            "beforeMap", false, fwdObj.getAfterMapClass(), null, paramTypes, paramNames,methods.getOwnerDocument());
                        methods.appendChild(method);
                    }
                }
                bodef.refresh();
            }
        }
        catch (XSLException e)
        {

        }


    }

    private static final void inheritObject(boDefHandler superbo,
        boDefHandler subbo, boolean inheritfrom, boolean interf, boolean createFwdMethods)
    {
        //        if(superbo.getBoIsSubBo()) {
        //            inheritObject(boBuilder.getUndeployedDefinitions(superbo.getBoSuperBo()),subbo);
        //        }
        subbo.refresh();
        String suname = subbo.getName();
        String supname = superbo.getName();

        try
        {
            if(createFwdMethods)
            {
                try
                {
                    createMethodsFromFwdObjects(subbo);
                }
                catch (boRuntimeException e)
                {
                    logger.error(e);
                }
                return;
            }

            if (superbo.getBoHaveMultiParent())
            {
                ((Element) ((XMLElement) subbo.getNode()).selectSingleNode(
                    "general")).setAttribute("multiparent", "true");
            }

            if (inheritfrom && !interf)
            {
                // Inherit States
                boDefClsEvents[] supev = superbo.getBoClsEvents();
                boDefClsEvents[] subev = subbo.getBoClsEvents();

                boDefClsState substates = subbo.getBoClsState();
                boDefClsState superstates = superbo.getBoClsState();
                Element xx = (Element) ((XMLElement) superbo.getNode()).selectSingleNode(
                        "states");
                Element yy = (Element) ((XMLElement) subbo.getNode()).selectSingleNode(
                        "states");
                String ref;

                if ((ref = xx.getAttribute("refers")).length() > 0)
                {
                    if ((yy != null) &&
                            (yy.getAttribute("refers").length() == 0))
                    {
                        yy.setAttribute("refers", xx.getAttribute("refers"));
                    }
                }
            }

            boDefAttribute[] srcatts = superbo.getBoAttributes();

            for (short i = 0; i < srcatts.length; i++)
            {
                if (srcatts[i].getAtributeType() != boDefAttribute.TYPE_STATEATTRIBUTE)
                {
                    Node destnode;
                    String attname = srcatts[i].getName();

                    if (!subbo.hasAttribute(srcatts[i].getName()))
                    {
                        Node nodeadded = subbo.getChildNode("attributes")
                                              .getNode().appendChild(subbo.getNode()
                                                                          .getOwnerDocument()
                                                                          .importNode(srcatts[i].getNode(),
                                    true));
                        String inheritf = ((Element) srcatts[i].getNode()).getAttribute(
                                "inheritfrom");

                        if (superbo.getClassType() == boDefHandler.TYPE_CLASS)
                        {
                            //if( inheritf == null || inheritf.length()==0  )
                            ((Element) nodeadded).setAttribute("inheritfrom",
                                superbo.getBoName());
                        }

                        if (superbo.getClassType() == boDefHandler.TYPE_INTERFACE)
                        {
                            String imp = ((Element) nodeadded).getAttribute(
                                    "implementedby");

                            if ((imp == null) || (imp.length() == 0))
                            {
                                imp = "";
                            }
                            else
                            {
                                imp += ",";
                            }

                            imp += superbo.getBoName();

                            ((Element) nodeadded).setAttribute("implementedby",
                                imp);
                        }
                    }
                    else
                    {
                        if (inheritfrom)
                        {
                            ngtXMLUtils.mergeNodes((XMLNode) srcatts[i].getNode(),
                                (XMLNode) subbo.getAttributeRef(
                                    srcatts[i].getName()).getNode(),ngtXMLUtils.MERGE_BY_ATTNAME_OR_NODENAME );
                        }

                        Element updnode = null;
						try {
							updnode = ((Element) (XMLNode) subbo.getAttributeRef(srcatts[i].getName())
                                                                    .getNode());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							subbo.getAttributeRef(srcatts[i].getName());
							
						}

                        if (superbo.getClassType() == boDefHandler.TYPE_CLASS)
                        {
                            //if(((Element)srcatts[i].getNode()).getAttribute("inheritfrom") == null )
                            updnode.setAttribute("inheritfrom",
                                superbo.getBoName());
                        }

                        if (superbo.getClassType() == boDefHandler.TYPE_INTERFACE)
                        {
                            String imp = updnode.getAttribute("implementedby");

                            if ((imp == null) || (imp.length() == 0))
                            {
                                imp = "";
                            }
                            else
                            {
                                imp += ",";
                            }

                            imp += superbo.getBoName();

                            updnode.setAttribute("implementedby", imp);
                        }
                    }
                }
            }

            //inherited fwdObjects
            boDefForwardObject[] superFwdObjs = superbo.getForwardObjects();
            boDefForwardObject[] subFwdObjs = subbo.getForwardObjects();

            if(superFwdObjs != null && superFwdObjs.length > 0)
            {
                if(subFwdObjs != null && subFwdObjs.length > 0)
                {
                    String auxName;
                    boolean found = false;
                    for (int i = 0; i < superFwdObjs.length; i++)
                    {
                        found = false;
                        auxName = superFwdObjs[i].toBoObject();
                        for (int j = 0; j < subFwdObjs.length; j++)
                        {
                            if(auxName.equals(subFwdObjs[j].toBoObject()))
                            {
                                found = true;
                            }
                        }
                        if(!found)
                        {
                            Node fwdNode = subbo.getChildNode("fwdObjects").getNode();
                            Node appendNode = fwdNode.appendChild(subbo.getNode().getOwnerDocument()
                                                             .importNode(superFwdObjs[i].getNode(), true));
                            Attr inhreatedAtt = appendNode.getOwnerDocument().createAttribute("inheritfrom");
                            inhreatedAtt.setValue(superbo.getName());
                        }
                    }
                }
                else
                {
                    if(subbo.getChildNode("fwdObjects") == null)
                    {
                        subbo.getNode().appendChild(subbo.getNode().getOwnerDocument()
                                                         .importNode(superbo.getChildNode("fwdObjects").getNode(), true));
                       ngtXMLHandler[] nodes = subbo.getChildNode("fwdObjects").getChildNodes();
                       Attr inhreatedAtt;
                       for (int i = 0; i < nodes.length; i++)
                       {
                            inhreatedAtt = nodes[i].getNode().getOwnerDocument().createAttribute("inheritfrom");
                            inhreatedAtt.setValue(superbo.getName());
                       }
                    }
                    else
                    {
                        Node fwdNode = subbo.getChildNode("fwdObjects").getNode();
                        Node appendNode;
                        Attr inhreatedAtt;
                        for (int i = 0; i < superFwdObjs.length; i++)
                        {
                            appendNode = fwdNode.appendChild(subbo.getNode().getOwnerDocument()
                                                         .importNode(superFwdObjs[i].getNode(), true));
                            inhreatedAtt = appendNode.getOwnerDocument().createAttribute("inheritfrom");
                            inhreatedAtt.setValue(superbo.getName());
                        }

                    }
                }
            }

            if (inheritfrom)
            {
                boDefMethod[] methds = superbo.getBoMethods();

                for (short i = 0; (methds != null) && (i < methds.length);
                        i++)
                {
                    boDefMethod cmeth;

                    if ((cmeth = subbo.getBoMethod(methds[i].getName(),
                                    methds[i].getAssinatureClasses())) != null)
                    {
                        if (!methds[i].getIsNative())
                        {
                            if (!netgest.bo.def.v2.boDefMethodImpl.compareMethodAssinature(
                                        cmeth.getAssinatureClasses(),
                                        methds[i].getAssinatureClasses()))
                            {
                                subbo.getChildNode("methods").getNode()
                                     .appendChild(subbo.getNode()
                                                       .getOwnerDocument()
                                                       .importNode(methds[i].getNode(),
                                        true));
                            }
                        }
                    }
                    else
                    {
                        if (!methds[i].getIsNative())
                        {
                            subbo.getChildNode("methods").getNode().appendChild(subbo.getNode()
                                                                                     .getOwnerDocument()
                                                                                     .importNode(methds[i].getNode(),
                                    true));
                        }
                    }
                }

                //*** deriv OPL
                if (!interf)
                {
                    ngtXMLHandler superOPL = superbo.getChildNode("opl");
                    ngtXMLHandler subOPL = subbo.getChildNode("opl");

                    ngtXMLHandler superG = superbo.getChildNode("General");
                    ngtXMLHandler subG = subbo.getChildNode("General");


                    /*
                    if ((superG.getAttribute("implementsSecurityRowObjects") != null) &&
                            (subG.getAttribute("implementsSecurityRowObjects") == null))
                    {
                        ((Element) ((XMLElement) subbo.getNode()).selectSingleNode(
                            "General")).setAttribute("implementsSecurityRowObjects",
                            superG.getAttribute("implementsSecurityRowObjects"));
                    }
                    */

                    if (subOPL != null)
                    {
                        ngtXMLHandler classKeysSub = subOPL.getChildNode("classKeys");
                        if( classKeysSub == null )
                        {
                            ngtXMLHandler classKeysSuper = superOPL.getChildNode("classKeys");
                            if( classKeysSuper != null )
                            {
                                subOPL.getNode().appendChild(
                                    subOPL.getDocument().importNode( classKeysSuper.getNode(), true )
                                    );
                            }
                        }

                        ngtXMLHandler attKeysSub = subOPL.getChildNode("attributeKeys");
                        if( attKeysSub == null )
                        {
                            ngtXMLHandler attKeysSuper = superOPL.getChildNode("attributeKeys");
                            if( attKeysSuper != null )
                            {
                                subOPL.getNode().appendChild(
                                    subOPL.getDocument().importNode( attKeysSuper.getNode(),true )
                                    );
                            }
                        }

//                        ngtXMLHandler[] subKeys = subOPL.getChildNodes();
//                        if ((subKeys == null) || (subKeys.length == 0))
//                        {
//                            subbo.getNode().removeChild(subOPL.getNode());
//                            subOPL = null;
//                        }
                    }
/*
                    if (superOPL != null)
                    {
                        if (subOPL == null)
                        {
                            subbo.getNode().appendChild(subbo.getNode()
                                                             .getOwnerDocument()
                                                             .importNode(superOPL.getNode(),
                                    true));
                        }
                    }*/
                }

                if (!interf)
                {
                    ngtXMLHandler superviews = superbo.getChildNode("Viewers");
                    ngtXMLHandler subviews = subbo.getChildNode("Viewers");

                    if (superviews != null)
                    {
                        if (subviews == null)
                        {
                            subbo.getNode().appendChild(subbo.getNode()
                                                             .getOwnerDocument()
                                                             .importNode(superviews.getNode(),
                                    true));
                        }
                        else
                        {
                            ngtXMLUtils.mergeNodes((XMLNode) superviews.getNode(),
                                (XMLNode) subviews.getNode(), ngtXMLUtils.MERGE_BY_ATTNAME_OR_NODEINDEX );
                        }
                    }
                }
            }
        }
        catch (XSLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static synchronized final boDefHandler getUndeployedDefinitions(
        boBuildRepository repository, String boname, Hashtable objectInterfaceMap, boolean createFwdMethods)
    {
        boDefHandler bobj;

        if ((bobj = (boDefHandler) p_undeployeddefs.get(boname)) == null)
        {
            //String sbofile = new boConfig().getDefinitiondir()+boname+TYPE_BO;
            //passou a ir buscar o ficheiro ao PathProvider
            // String sbofile = repository.getPathProvider().getBOPath(boname);

            File xbofile = repository.getXMLFile( boname );
            if( xbofile == null || !xbofile.exists() )
            {
                xbofile = repository.getFile( boname, boBuilder.TYPE_INTERFACE );
                if( xbofile != null && xbofile.exists() )
                {
                    XMLDocument xmldoc = ngtXMLUtils.loadXMLFile(xbofile.getAbsolutePath());
                    bobj = boDefHandler.loadInterfaceFromXml( boname, xmldoc );
                }
                else
                {
                    return null;
                }
            }
            else
            {
                XMLDocument xmldoc = ngtXMLUtils.loadXMLFile(xbofile.getAbsolutePath());
                bobj = boDefHandler.loadFromXml( xmldoc );
            }

            if( bobj.getClassType() == boDefHandler.TYPE_INTERFACE && !xbofile.getName().toLowerCase().endsWith(".xeoimodel") )
            {
                throw new RuntimeException("Object ["+boname+"] in bodef-deployment as wrong extension, must be .xeoimodel not xeomodel because is declared as a interface.");
            }
            if( !bobj.getName().equalsIgnoreCase( boname ) )
            {
                throw new RuntimeException("Erro in object definition:Object filename does not match with name specified in the XML. ["+boname+"!="+bobj.getName()+"].");
            }

            fillSystemAttributes(repository, bobj, objectInterfaceMap);
            fillInterfaceAttributes(repository, bobj, objectInterfaceMap);
            fillInterfaceMethdos(repository, bobj, objectInterfaceMap);
            fillInterfaceEvents(repository, bobj, objectInterfaceMap);
            buildInterfaces(repository, bobj, objectInterfaceMap);
            if(createFwdMethods)
            {
                try
                {
                    createMethodsFromFwdObjects(bobj);
                }
                catch (boRuntimeException e)
                {
                    logger.error(e);
                }
            }
            p_undeployeddefs.put(boname, bobj);
        }

        return bobj;
    }

    public static final boDefHandler[] listUndeployedDefinitions(
        boBuildRepository repository, Hashtable objectInterfaceMap)
    {
        //String[] xfiles = eboobjdir.list();
        File[] xmlfiles = repository.getXMLFiles();
        Vector defs = new Vector();
        String xfiles;

        for (int i = 0; i < xmlfiles.length; i++)
        {
            xfiles = xmlfiles[i].getName();

            if (xfiles.toLowerCase().endsWith(TYPE_BO))
            {
//VERIFICAR
                boDefHandler def = getUndeployedDefinitions(repository,
                        xfiles.substring(0, xfiles.toLowerCase().indexOf(TYPE_BO)), objectInterfaceMap, false);
                defs.add(def);
            }
        }

        boDefHandler[] ret = new boDefHandler[defs.size()];
        defs.toArray(ret);

        return ret;
    }



    public static void forceDeploy(String s)
    {
        try
        {
            File bodef = new File(p_bcfg.getDeploymentDir() + s + TYPE_BO);

            if (bodef.exists())
            {
                bodef.delete();
            }
        }
        catch (Exception e)
        {
            //ignora
        }
    }

    public static void cleanboDefDeployment()
    {
        File f = new File(p_bcfg.getDeploymentDir());

        if (f.exists() && f.canWrite())
        {
            delete(f.listFiles());
        }
    }

    private static void delete(File[] files)
    {
        File aux;

        for (int i = 0; i < files.length; i++)
        {
            aux = files[i];

            if (aux.isDirectory() && !isNumber(aux.getName()))
            {
                delete(aux.listFiles());
            }

            try
            {
                aux.delete();
            }
            catch (Exception e)
            {
                //ignora
            }
        }
    }

    private static boolean isNumber(String s)
    {
        try
        {
            Long.parseLong(s);
        }
        catch (NumberFormatException e)
        {
            return false;
        }

        return true;
    }

    private static void createResources(String boname)
        throws IOException
    {
        boolean exists = true;
        String path = p_bcfg.getDeployJspDir() + File.separator + "resources" +
            File.separator + boname;
        File resources = new File(path);

        if (!resources.exists())
        {
            resources.mkdirs();
            exists = false;
        }

        File iconsDir = new File(p_bcfg.getNgtHome() + File.separator + "icons");
        if (iconsDir.exists())
        {
            File[] icons = iconsDir.listFiles();
            File[] resourcesIcons = null;
            File aux;
            File aux2;

            if (exists)
            {
                resourcesIcons = resources.listFiles();
            }

            for (int i = 0; (icons != null) && (i < icons.length); i++)
            {
                aux = icons[i];
               boolean found = false;
                for (int j = 0;
                        (resourcesIcons != null) &&
                        (j < resourcesIcons.length) && !found; j++)
                {
                    aux2 = resourcesIcons[j];

                    if (aux.getName().equals(aux2.getName()))
                    {
                        found = true;
                    }
                }

                if (!found)
                {
                    copyFile(aux,
                        new File(path + File.separator + aux.getName()));
                }
            }
        }
    }

    private static void copyFile(File from, File to) throws IOException
    {
        byte[] b = new byte[1024 * 10];
        int numBytes = 0;

        FileOutputStream fos = null;
        FileInputStream fis = null;

        try
        {
            fos = new FileOutputStream(to);
            fis = new FileInputStream(from);

            for (long i = 0; (numBytes = fis.read(b)) != -1; i++)
            {
                fos.write(b, 0, numBytes);
            }
        }
        finally
        {
            if (fos != null)
            {
                fos.close();
            }

            if (fis != null)
            {
                fos.close();
            }
        }
    }

    private static String getObjectPackage(File objectfile)
    {
        String file = objectfile.getAbsolutePath();
        int lof = file.lastIndexOf(objectfile.separator);
        String dir = objectfile.getAbsolutePath().substring(0, lof);
        String cpackage = dir.substring(dir.lastIndexOf(objectfile.separator) +
                1, dir.length());

        return cpackage;
    }

//    public static boolean containsInterface(ArrayList l, String intfName)
//    {
//        for (int i = 0; i < l.size() ; i++)
//        {
//            if(intfName.equals(((boDefInterface)l.get(i)).getName()))
//            {
//                return true;
//            }
//        }
//        return false;
//    }

    public static void buildWorkPlaceDefault(EboContext ctx)
    {
        boolean result = false;
        boolean mybuild = false;
        try
        {
            synchronized (boBuilder.class)
            {
                if (boBuilder.p_running)
                {
                    return;
                }

                boBuilder.p_running = true;
                mybuild = true;
            }

            boConnectionManager connManager = ctx.getConnectionManager();
            try
            {
                connManager.beginContainerTransaction();

                //IProfileUtils.buildWorkPlaceDefault(ctx);
                uiObjectBuilder.buildWorkPlaceDefault( ctx );

                result = true;
            }
            catch (Exception e)
            {
                logger.error(e);
            }
            finally
            {
                if(result)
                {
                    connManager.commitContainterTransaction();
                }
                else
                {
                    connManager.rollbackContainerTransaction();
                }
            }
        }
        catch (boRuntimeException e)
        {
            logger.error("Não foi possível efectuar o set dos Packages names.");
        }
        finally
        {
            if (mybuild)
            {
                boBuilder.p_running = false;
            }
        }
    }

    public static void removeUserWorkPlaces(EboContext ctx)
    {
        boolean result = false;
        boolean mybuild = false;
        try
        {
            synchronized (boBuilder.class)
            {
                if (boBuilder.p_running)
                {
                    return;
                }

                boBuilder.p_running = true;
                mybuild = true;
            }

            boConnectionManager connManager = ctx.getConnectionManager();
            try
            {
                connManager.beginContainerTransaction();
                uiObjectBuilder.removeUserWorkPlaces(ctx);
                result = true;
            }
            catch (Exception e)
            {
                logger.error(e);
            }
            finally
            {
                if(result)
                {
                    connManager.commitContainterTransaction();
                }
                else
                {
                    connManager.rollbackContainerTransaction();
                }
            }
        }
        catch (boRuntimeException e)
        {
            logger.error("Não foi possível efectuar o set dos Packages names.");
        }
        finally
        {
            if (mybuild)
            {
                boBuilder.p_running = false;
            }
        }
    }

    public static void createSystemUsersandGroups(EboContext ctx)
    {
      try
      {
       //Create Users SYSUSER, ROBOT and group PUBLIC
            
        logger.debug("Checking if System Users and Groups exist");
        
        boObject perf =boObject.getBoManager().loadObject(ctx,"Ebo_Perf","username='SYSUSER'");
        if(!perf.exists()) {
          perf =boObject.getBoManager().createObject(ctx,"Ebo_Perf");
          perf.getAttribute("name").setValueString("Default superuser");
          perf.getAttribute("id").setValueString("SYSUSER");
          perf.getAttribute("username").setValueString("SYSUSER");
          perf.getAttribute("password").setValueString("ABC");
          perf.update();
          logger.debug("Ebo_Perf "+perf.getAttribute("id").getValueString() +" Created");                  
        }
        else logger.debug("Ebo_Perf "+perf.getAttribute("id").getValueString() +" already exists");                  

        perf =boObject.getBoManager().loadObject(ctx,"Ebo_Perf","username='ROBOT'");
        if(!perf.exists()) {
          perf =boObject.getBoManager().createObject(ctx,"Ebo_Perf");

          perf.getAttribute("name").setValueString("ROBOT");
          perf.getAttribute("id").setValueString("ROBOT");
          perf.getAttribute("username").setValueString("ROBOT");
          perf.getAttribute("password").setValueString("ROBOT");
          perf.update();
          logger.debug("Ebo_Perf "+perf.getAttribute("id").getValueString() +" Created");                  
        }
        else logger.debug("Ebo_Perf "+perf.getAttribute("id").getValueString() +" already exists");  

        boObject group =boObject.getBoManager().loadObject(ctx,"Ebo_Group","name='PUBLIC'");
        if(!group.exists()) {
          group =boObject.getBoManager().createObject(ctx,"Ebo_Group");

          group.getAttribute("name").setValueString("PUBLIC");
          group.getAttribute("id").setValueString("PUBLIC");
          group.update();
          logger.debug("Ebo_Group "+group.getAttribute("id").getValueString() +" Created");                  
        }
        else logger.debug("Ebo_Group "+group.getAttribute("id").getValueString() +" already exists");            
      }
      catch (boRuntimeException e)
      {
        logger.debug("Error creating  System Users and Groups "+e.getMessage());
      }
    }


    public static boolean isRunning()
    {
        return p_running;
    }
}
