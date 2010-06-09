/*Enconding=UTF-8*/
package netgest.bo.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import netgest.bo.boConfig;
import netgest.bo.boException;
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.def.boDef;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefClsEvents;
import netgest.bo.def.boDefForwardObject;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefInterface;
import netgest.bo.def.boDefLov;
import netgest.bo.def.boDefMethod;
import netgest.bo.def.boDefUtils;
import netgest.bo.def.v2.boDefLovImpl;
import netgest.bo.dochtml.docHTML_treeServer;
import netgest.bo.http.Builder;
import netgest.bo.parser.CodeJavaConstructor;
import netgest.bo.presentation.manager.uiObjectBuilder;
import netgest.bo.presentation.render.elements.ExplorerServer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.system.boApplicationConfig;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boSession;
import netgest.utils.IOUtils;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 
public class boBuilder {
	
	private static final Logger logger = Logger.getLogger(boBuilder.class);
    private static final org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger( Builder.class );

	private static boApplicationConfig p_bcfg = boApplication
			.getApplicationFromStaticContext("XEO").getApplicationConfig();
	private static boolean p_running = false;
	protected static Hashtable p_undeployeddefs = new Hashtable();

	public static final String TYPE_BO = ".xeomodel";
	public static final String TYPE_STATE = ".xeostate";
	public static final String TYPE_DS = ".xeods";
	public static final String TYPE_LOV = ".xeolov";
	public static final String TYPE_SC = ".xeodeploy";
	public static final String TYPE_INTERFACE = ".xeoimodel";
	public static final String TYPE_WSD = ".xeowds";

	private EboContext p_eboctx;
	private String p_dml;
	private Hashtable interfacedeploy = new Hashtable();

	private boBuilderProgress p_builderProgress = new boBuilderProgress();
	private boBuilderOptions p_builderOptions = new boBuilderOptions();
	
	
	private static long xeoStudioBuildLastRun=0;
	
	private boBuilder(EboContext ebo) {
		p_eboctx = ebo;
	}

	public static void main(String[] args) throws Exception {

		int exitStatus = 0;

		boBuilderProgress builderProgress = new boBuilderProgress();
		builderProgress.setLogToConsole(true);

		try {
			WriterAppender appender = null;
			try {
				PatternLayout layout = new PatternLayout("%d (%F:%L) - %m%n");
				appender = new WriterAppender(layout, System.out);
				appender.setName("Console");
			} catch (Exception e) {
				logger.severe("Error: ", e);
			}
			log4j.addAppender(appender);
			log4j.setLevel(Level.DEBUG);
			boApplication bapp = boApplication
					.getApplicationFromStaticContext("XEO");
			bapp.suspendAgents();
			boSession session = bapp.boLogin("SYSTEM", boLoginBean
					.getSystemKey());
			EboContext ctx = session.createRequestContext(null, null, null);

			boBuilderOptions buildOptions = new boBuilderOptions();

			buildOptions.setBuildDatabase(false);
			boBuilder.buildAll(ctx, buildOptions, builderProgress);
		} catch (Exception e) {
			e.printStackTrace(builderProgress.getLogWriter());
			throw e;
		}
		System.exit(exitStatus);
	}

	public static boolean requireAuthentication() {
		try {
			if (boDefHandler.getBoDefinition("iXEOUser") != null) {

				boApplication boapp = boApplication
						.getApplicationFromStaticContext("XEO");
				boSession bosession = boapp.boLogin("SYSUSER", boLoginBean
						.getSystemKey(), boapp.getDefaultRepositoryName());
				bosession.closeSession();

				return true;
			}
			return false;
		} catch (Exception e) {
			try {
			} catch (Exception ex) {
			}
			return false;
		}
	}

	public static void buildAll(EboContext ebo, boBuilderOptions buildOptions,
			boBuilderProgress buildProgress) throws boRuntimeException {

		boolean mybuild = false;

		boBuilder builder = new boBuilder(ebo);
		try {
			synchronized (boBuilder.class) {
				if (boBuilder.p_running) {
					return;
				}

				boBuilder.p_running = true;
				mybuild = true;
			}

			ebo.getApplication().getApplicationConfig().refresh();
			builder.p_builderProgress = buildProgress;
			builder.p_builderOptions = buildOptions;

			int buildTasks = 25;

			if (!buildOptions.getBuildDatabase())
				buildTasks -= 10;

			if (!buildOptions.getBuildWorkplaces())
				buildTasks--;

			if (!buildOptions.getMarkDeployedObjects())
				buildTasks--;

			if (!buildOptions.getRemoveUserWorkplaces())
				buildTasks++;

			if (!buildOptions.getGenerateAndCompileJava())
				buildTasks -= 2;

			if (buildOptions.getFullBuild())
				buildTasks++;
			
			// rest web services module is present
			if (Thread.currentThread().getContextClassLoader().getResource("xeo.rest.webservices") != null )
				buildTasks++;

			builder.p_builderProgress.setOverallTasks(buildTasks);
			builder.p_builderProgress
					.setOverallTaskName("Stopping XEO Threads...");
			ebo.getApplication().suspendAgents();

			if (buildOptions.getFullBuild()) {
				builder.p_builderProgress
						.setOverallTaskName("Cleaning Build Folders...");
				builder.p_builderProgress
						.setCurrentTaskName("Clean bodef-deployment...");
				builder.p_builderProgress.setCurrentTasks(3);

				builder.cleanboDefDeployment();

				if (buildOptions.getGenerateAndCompileJava()) {
					builder.p_builderProgress.addCurrentTaskProgress();
					builder.p_builderProgress
							.setCurrentTaskName("Clean Java Classes...");
					builder.cleanBuildClasses();
				}

				if (buildOptions.getGenerateAndCompileJava()) {
					builder.p_builderProgress.addCurrentTaskProgress();
					builder.p_builderProgress
							.setCurrentTaskName("Clean Java Sources...");
					builder.cleanBuildSource();
				}

				builder.p_builderProgress.addCurrentTaskProgress();
			}

			boDefHandler.clearCache();
			boBuildRepository.clearCache();
			
			boBuildRepository rep = null;
			if (buildOptions.getIntegrateWithXEOStudioBuilder())
				rep = new boBuildRepository(ebo.getBoSession()
						.getRepository(),true);				
			else			
				rep = new boBuildRepository(ebo.getBoSession()
					.getRepository());

			File[] toDepl = rep.getFilesToDeploy();
			if (toDepl.length > 0) {
				builder.buildObjects();
			}

			builder.p_builderProgress.addOverallProgress();
			builder.p_builderProgress
					.setOverallTaskName("Starting XEO Threads...");
			ebo.getApplication().startAgents();

			builder.p_builderProgress.addOverallProgress();
			builder.p_builderProgress
					.setOverallTaskName("XEO Builder Finished...");
			if (builder.p_builderOptions.getIntegrateWithXEOStudioBuilder())
				resetXEOStudioBuilderLastRun();
		} finally {
			if (mybuild) {
				boBuilder.p_running = false;
			}
			builder.p_builderProgress.finish();
		}
	}

	private static void buildStates(Hashtable todeploy, String name)
			throws boRuntimeException {
		boBuildStates bstates = new boBuildStates();
		boDefHandler xx = boDefHandler.getClsState(name).getBoDefHandler();
		bstates.build(xx);

		boDefHandler[] defs = boDefHandler.listBoDefinitions();

		for (short i = 0; i < defs.length; i++) {
			if (name.equals(defs[i].getReferencedState())) {
				if (todeploy.get(defs[i].getName()) == null) {
					todeploy.put(defs[i].getName(), new Boolean(false));
				}
			}
		}
	}

	private void buildLov(String name) throws boRuntimeException {
		boBuildLov blov = new boBuildLov(p_eboctx);
		boDefLov xx = boDefLovImpl.loadLov(name);
		blov.build(xx);
	}

	private static ngtXMLHandler[] getUpdates(Node doc) {
		ArrayList ret = new ArrayList();
		ngtXMLHandler[] retX;
		NodeList n = doc.getChildNodes();
		Node aux;

		for (int i = 0; i < n.getLength(); i++) {
			aux = n.item(i);
			if (aux.getNodeName().equalsIgnoreCase("scripts")) {
				n = aux.getChildNodes();

				for (int j = 0; j < n.getLength(); j++) {
					aux = n.item(j);

					if (aux.getNodeName().equalsIgnoreCase("updateSQL")) {
						ret.add(new ngtXMLHandler(aux));
					}
				}
			}
		}

		if (ret.size() == 0) {
			return null;
		}

		retX = new ngtXMLHandler[ret.size()];

		for (int i = 0; i < ret.size(); i++) {
			retX[i] = (ngtXMLHandler) ret.get(i);
		}

		return retX;
	}

	private void treatScript(EboContext ctx, String name)
			throws boRuntimeException {
		boBuildRefactoring br = new boBuildRefactoring(ctx);
		XMLDocument doc = ngtXMLUtils.loadXMLFile(ctx.getApplication()
				.getApplicationConfig().getDeploymentDir()
				+ name + TYPE_SC);
		ngtXMLHandler[] updates = getUpdates(doc);

		if ((updates != null) && (updates.length > 0)) {
			Arrays.sort(updates, new boRefactorComparator());
			br.build(updates);
		}
	}

	public void buildSpecialTables(EboContext ctx, String schemaName)
			throws boRuntimeException {
		OracleDBM odbm = null;

		try {
			odbm = ctx.getDataBaseDriver().getDBM();
			odbm.setEnvironment(ctx);
			odbm.createSpecialTables(schemaName);
		} catch (SQLException e) {
			throw new boRuntimeException("", e.getMessage(), null);
		} catch (boRuntimeException e) {
			throw new boRuntimeException("", e.getMessage(), null);
		} finally {
			odbm.close();
		}
	}

	private void buildObjects() throws boRuntimeException {

		long ms = System.currentTimeMillis();

		// Check all directories..
		File deployDir = null;
		deployDir = new File(p_eboctx.getApplication().getApplicationConfig()
				.getDeployJspDir());
		if (!deployDir.exists())
			deployDir.mkdirs();
		deployDir = new File(p_eboctx.getApplication().getApplicationConfig()
				.getDeploymentclassdir());
		if (!deployDir.exists())
			deployDir.mkdirs();
		deployDir = new File(p_eboctx.getApplication().getApplicationConfig()
				.getDeploymentDir());
		if (!deployDir.exists())
			deployDir.mkdirs();
		deployDir = new File(p_eboctx.getApplication().getApplicationConfig()
				.getDeploymentsrcdir());
		if (!deployDir.exists())
			deployDir.mkdirs();

		boBuildRepository repository = new boBuildRepository(p_eboctx
				.getBoSession().getRepository());
		File[] xfiles = repository.getFilesToDeploy();// REFORMULADO;

		String[] xfilesNames = new String[xfiles.length];
		for (int i = 0; i < xfilesNames.length; i++) {
			xfilesNames[i] = xfiles[i].getName();
		}

		File[] allXfiles = repository.getXMLFiles();

		String[] sxfilesNames = (String[]) xfilesNames.clone();
		Arrays.sort(sxfilesNames);

		ArrayList interfImpl = null;

		Hashtable objectInterf = new Hashtable(); // Contém as dependencias de
													// todos os interfaces

		// Apaga chache das definições
		p_undeployeddefs.clear();

		for (short i = 0; i < allXfiles.length; i++) {
			if (allXfiles[i].getName().toLowerCase().endsWith(TYPE_BO)) {
				String name = allXfiles[i].getName().substring(0,
						allXfiles[i].getName().toLowerCase().indexOf(TYPE_BO));
				// verificar se implementa
				netgest.bo.def.v2.boDefInterfaceImpl.addImplObject(name,
						allXfiles[i], interfacedeploy, objectInterf, allXfiles);
			} else if (allXfiles[i].getName().toLowerCase()
					.endsWith(TYPE_STATE)) {
				String name = allXfiles[i].getName().substring(
						0,
						allXfiles[i].getName().toLowerCase()
								.indexOf(TYPE_STATE));
				File deployFile = new File(p_bcfg.getDeploymentDir() + name
						+ TYPE_STATE);
				if (!deployFile.exists()) {
					IOUtils.copy(allXfiles[i], deployFile);
					deployFile
							.setLastModified(allXfiles[i].lastModified() - 600);
				}

			} else if (allXfiles[i].getName().toLowerCase().endsWith(
					TYPE_INTERFACE)) {

				String name = allXfiles[i].getName().substring(
						0,
						allXfiles[i].getName().toLowerCase().indexOf(
								TYPE_INTERFACE));

				int deployFilePosition = Arrays.asList(xfilesNames).indexOf(
						allXfiles[i].getName());
				if (deployFilePosition > -1) {
					File deployfile = new File(p_bcfg.getDeploymentDir() + name
							+ TYPE_INTERFACE);

					IOUtils.copy(xfiles[deployFilePosition], deployfile);

					deployfile.setLastModified(xfiles[deployFilePosition]
							.lastModified() - 600);

					netgest.bo.def.v2.boDefInterfaceImpl
							.fillSystemAttribute(name);

					deployfile.setLastModified(xfiles[deployFilePosition]
							.lastModified());
				}

				boDefInterface bdi = boDefHandler.getInterfaceDefinition(name);
				String objs[] = null;
				if ((objs = bdi.getImplObjects()) != null) {

					for (int j = 0; j < objs.length; j++) {
						// interface in object
						if ((interfImpl = (ArrayList) interfacedeploy
								.get(objs[j])) != null) {
							if (!interfImpl.contains(bdi.getName())) {
								interfImpl.add(bdi.getName());
							}
						} else {
							(interfImpl = new ArrayList()).add(bdi.getName());

							interfacedeploy.put(objs[j], interfImpl);
						}

						// object in interface
						if ((interfImpl = (ArrayList) objectInterf.get(bdi
								.getName())) != null) {
							if (!interfImpl.contains(objs[j])) {
								interfImpl.add(objs[j]);
							}
						} else {
							(interfImpl = new ArrayList()).add(objs[j]);
							objectInterf.put(bdi.getName(), interfImpl);
						}
					}
				}

			}
		}

		netgest.bo.def.v2.boDefInterfaceImpl.writeToInterfaces(objectInterf);

		File[] bofiles = repository.getFilesToDeploy();// REFORMULADO

		// WSD files
		ArrayList wsdFiles = new ArrayList();

		// Lov files
		ArrayList lovFiles = new ArrayList();

		// Script files
		ArrayList scriptFiles = new ArrayList();

		// Update bodef deployments
		boolean success = false;

		File xboObject = new File(boBuilder.p_bcfg.getDeploymentDir()
				+ "boObject.xeomodel");

		// File dboObject = new
		// File(this.p_bcfg.getDefinitiondir()+"boObject$bo.xml");
		// vai procurar na directoria XEO
		File dboObject = repository.getXMLFileFromDefinition("boObject");

		if (!xboObject.exists()
				|| (dboObject.lastModified() > xboObject.lastModified())) {
			IOUtils.copy(dboObject, xboObject);
		}

		p_builderProgress.appendInfoLog("Starting deploying objects...");

		p_builderProgress.addOverallProgress();
		p_builderProgress
				.setOverallTaskName("Checking for updated XEO descriptors...");

		boDefHandler.clearInterfacesCache();
		boBuilder.p_undeployeddefs.clear();

		// set de todos os packages
		File[] allXmlfiles = repository.getXMLFiles();
		Hashtable htdeploypackage = new Hashtable();
		boolean intf = false;

		p_builderProgress.setCurrentTasks(allXmlfiles.length * 2);

		for (short i = 0; i < allXmlfiles.length; i++) {
			intf = allXmlfiles[i].getName().toLowerCase().endsWith(
					TYPE_INTERFACE);
			if (allXmlfiles[i].getName().toLowerCase().endsWith(TYPE_BO)
					|| intf) {
				String name = allXmlfiles[i].getName().substring(
						0,
						allXmlfiles[i].getName().toLowerCase().indexOf(
								!intf ? TYPE_BO : TYPE_INTERFACE));

				if (!name.equalsIgnoreCase("boObject")) {
					htdeploypackage.put(name, getObjectPackage(allXmlfiles[i]));
				}
			}
			p_builderProgress.addCurrentTaskProgress();
		}

		Hashtable htdeploy = new Hashtable();

		for (int i = 0; i < bofiles.length; i++) {
			if (bofiles[i].getName().toLowerCase().endsWith(TYPE_BO)
					|| bofiles[i].getName().toLowerCase().endsWith(
							TYPE_INTERFACE)) {
				String name = null;
				boolean interf = false;
				if (bofiles[i].getName().toLowerCase().endsWith(TYPE_BO)) {
					name = bofiles[i].getName()
							.substring(
									0,
									bofiles[i].getName().toLowerCase().indexOf(
											TYPE_BO));
				} else {
					name = bofiles[i].getName().substring(
							0,
							bofiles[i].getName().toLowerCase().indexOf(
									TYPE_INTERFACE));
					interf = true;
				}

				if (!name.equalsIgnoreCase("boObject")) {

					File depfile = new File(p_bcfg.getDeploymentDir() + name
							+ (interf ? TYPE_INTERFACE : TYPE_BO));

					boolean exists = depfile.exists();
					htdeploy.put(name, new Boolean(exists));

					String sBackFileName = depfile.getName() + ".bak";

					if (exists) {
						IOUtils.copy(depfile, p_bcfg.getDeploymentDir()
								+ sBackFileName);
						p_builderProgress
								.appendInfoLog("New version detected [" + name
										+ "]");
					} else {
						p_builderProgress.appendInfoLog("New object detected ["
								+ name + "]");
						IOUtils.copy(bofiles[i], depfile).setLastModified(
								bofiles[i].lastModified() - 60000);
					}
				}
			} else if (bofiles[i].getName().toLowerCase().endsWith(TYPE_STATE)) {
				String name = bofiles[i].getName().substring(0,
						bofiles[i].getName().toLowerCase().indexOf(TYPE_STATE));
				File deployfile = new File(p_bcfg.getDeploymentDir() + name
						+ TYPE_STATE);

				IOUtils.copy(bofiles[i], deployfile);

				deployfile.setLastModified(bofiles[i].lastModified() - 600);

				p_builderProgress.appendInfoLog("Building StateObjectHandler:'"
						+ name + "'");

				if (p_builderOptions.getGenerateAndCompileJava()) {
					buildStates(htdeploy, name);
				}
				p_builderProgress.appendInfoLog("end...");

				deployfile.setLastModified(bofiles[i].lastModified());
			} else if (bofiles[i].getName().toLowerCase().endsWith(TYPE_LOV)) {
				lovFiles.add(new Integer(i));
			} else if (bofiles[i].getName().toLowerCase().endsWith(TYPE_WSD)) {
				wsdFiles.add(new Integer(i));
			} else if (bofiles[i].getName().toLowerCase().endsWith(TYPE_SC)) {
				scriptFiles.add(bofiles[i]);
			}
			p_builderProgress.addCurrentTaskProgress();
		}
		boDefHandler.clearCache();

		p_builderProgress.addOverallProgress();
		p_builderProgress
				.setOverallTaskName("Checking for XEO Models dependencies...");

		// Make BuildOrder
		Stack stack = new Stack();
		Enumeration bonames = htdeploy.keys();

		p_builderProgress.setCurrentTasks(htdeploy.size());
		while (bonames.hasMoreElements()) {
			String name = (String) bonames.nextElement();
			makeBuildDep(name, stack, !((Boolean) htdeploy.get(name))
					.booleanValue());

			p_builderProgress.addCurrentTaskProgress();
		}

		Vector todeploy = new Vector();
		boDefHandler def;

		// Clear local bodef-cache
		p_undeployeddefs.clear();

		boolean builcls1st = false;

		p_builderProgress.setCurrentTasks(stack.size());
		while (stack.size() > 0) {
			// VERIFICAR
			String name = (String) stack.pop();
			def = getUndeployedDefinitions(repository, name, interfacedeploy,
					false);

			if (def.getBoName().equals("Ebo_ClsReg")) {
				builcls1st = true;
			}

			if (def != null) {
				todeploy.add(def);
			}
			p_builderProgress.addCurrentTaskProgress();
		}

		try {
			p_builderProgress.appendInfoLog("Creating bodef-deployment xml...");

			p_builderProgress.addOverallProgress();
			p_builderProgress
					.setOverallTaskName("Creating XEO Models descriptors...");

			p_builderProgress.setCurrentTasks(todeploy.size());
			for (int i = 0; i < todeploy.size(); i++) {
				try {
					boDefHandler todepDef = (boDefHandler) todeploy.get(i);
					String xname = todepDef.getBoName();
					p_builderProgress.setCurrentTaskName(todepDef.getLabel()
							+ "(" + todepDef.getName() + ")");
					// File srcfile =new
					// File(this.p_bcfg.getDefinitiondir()+xname+TYPE_BO);
					// passou a ir buscar o ficheiro ao PathProvider
					File srcfile = repository.getXMLFile(xname);
					String fname = xname + TYPE_BO;
					if (srcfile == null) {
						srcfile = repository.getFile(xname, TYPE_INTERFACE);
						fname = xname + TYPE_INTERFACE;
					}
					
					if (todepDef.getClassType() == boDefHandler.TYPE_INTERFACE) {
						if (!fname.endsWith(TYPE_INTERFACE)) {
							p_builderProgress
									.appendErrorLog("Debug info. Object ["
											+ todepDef.getName()
											+ "] is interface and are ready to be writed as a object.");
						}
					}

					File deployfile = new File(p_eboctx.getApplication()
							.getApplicationConfig().getDeploymentDir()
							+ fname);

					FileOutputStream os = new FileOutputStream(deployfile);

					// Create XML with the full attributes
					boDefHandler bobjinherit = (boDefHandler) todeploy.get(i);

					// VERIFICAR
					buildInherit(repository, bobjinherit, interfacedeploy, false);
					((XMLDocument) bobjinherit.getNode().getOwnerDocument())
							.print(os);

					os.close();

					if (deployfile != null && srcfile != null) {
						deployfile
								.setLastModified(srcfile.lastModified() - 60000);
					}
				} catch (FileNotFoundException e) {
					throw new RuntimeException(
							"Unexpected java.io.FileNotFoundException cpoying files to deploy directory ["
									+ ((boDefHandler) todeploy.get(i))
											.getName() + "]\n" + e.getClass()
									+ "\n" + e.getMessage());
				} catch (IOException e) {
					throw new RuntimeException(
							"Erro copying files to deployment directory ["
									+ ((boDefHandler) todeploy.get(i))
											.getName() + "]\n" + e.getClass()
									+ "\n" + e.getMessage());
				}
				p_builderProgress.addCurrentTaskProgress();
			}
			p_undeployeddefs.clear();
			boDefHandler.clearCache();
			boDefLovImpl.clearCache();

			p_builderProgress.addOverallProgress();
			this.p_builderProgress
					.setOverallTaskName("Creating XEO Model forward methos...");
			p_builderProgress.setCurrentTasks(todeploy.size());
			for (int i = 0; i < todeploy.size(); i++) {
				try {
					if (((boDefHandler) todeploy.get(i)).hasFwdMethods()) {
						String xname = ((boDefHandler) todeploy.get(i))
								.getBoName();
						p_builderProgress.appendInfoLog(xname
								+ " has Foward Methods");

						// File srcfile =new
						// File(this.p_bcfg.getDefinitiondir()+xname+TYPE_BO);
						// passou a ir buscar o ficheiro ao PathProvider
						File srcfile = repository.getXMLFile(xname);

						// Create XML with the full attributes
						boDefHandler bobjinherit = (boDefHandler) todeploy
								.get(i);

						String fileName = p_eboctx.getApplication()
								.getApplicationConfig().getDeploymentDir()
								+ xname;
						if (bobjinherit.getClassType() == boDefHandler.TYPE_INTERFACE) {
							fileName += TYPE_INTERFACE;
						} else {
							fileName += TYPE_BO;
						}
						File deployfile = new File(fileName);

						FileOutputStream os = new FileOutputStream(deployfile);

						// VERIFICAR
						buildInherit(repository, bobjinherit, interfacedeploy,
								true);

						((XMLDocument) bobjinherit.getNode().getOwnerDocument())
								.print(os);
						os.close();

						deployfile
								.setLastModified(srcfile.lastModified() - 60000);

					}
				} catch (FileNotFoundException e) {
					throw new RuntimeException(
							"Unexpected java.io.FileNotFoundException cpoying files to deploy directory ["
									+ ((boDefHandler) todeploy.get(i))
											.getName() + "]\n" + e.getClass()
									+ "\n" + e.getMessage());
				} catch (IOException e) {
					throw new RuntimeException(
							"Erro copying files to deployment directory ["
									+ ((boDefHandler) todeploy.get(i))
											.getName() + "]\n" + e.getClass()
									+ "\n" + e.getMessage());
				}
				p_builderProgress.addCurrentTaskProgress();
			}

			p_undeployeddefs.clear();
			boDefHandler.clearCache();

			boDefHandler[] defs;
			defs = new boDefHandler[todeploy.size()];

			todeploy.toArray(defs);

			for (int i = 0; i < defs.length; i++) {
				// VERIFICAR
				defs[i] = getUndeployedDefinitions(repository, defs[i]
						.getName(), interfacedeploy, true);
			}

			// Creating array with Ebo_ClsReg BOUI to be filled in register
			// classes;
			long[] classesbouis = new long[defs.length];

			// Build Classes 1'st phase.....
			p_builderProgress.addOverallProgress();
			this.p_builderProgress
					.setOverallTaskName("Creating and Compiling XEO Model Java Classes...");
			this.p_builderProgress.setCurrentTasks(1);
			if (this.p_builderOptions.getGenerateAndCompileJava()) {
				boClassBuilder bcl = new boClassBuilder(p_builderProgress);

				p_builderProgress.appendInfoLog("Building Object Classes...");
				bcl.build(defs, classesbouis);

				this.p_builderProgress.addCurrentTaskProgress();
			}

			// Build Database Objects
			if (p_builderOptions.getBuildDatabase()) {
				buildDataBaseObjects(repository, defs, objectInterf, lovFiles,
						wsdFiles, scriptFiles, bofiles, htdeploypackage,
						builcls1st);
			}

			p_builderProgress.addOverallProgress();
			this.p_builderProgress
					.setOverallTaskName("Creating JSP's for XEO Models...");
			// jsp faz sempre
			boBuildJSP bjsp = null;

			p_builderProgress.setCurrentTasks(defs.length);
			// Build JSP's
			for (int i = 0; i < defs.length; i++) {
				p_builderProgress.setCurrentTaskName(defs[i].getLabel() + " ("
						+ defs[i].getName() + ")");
				p_builderProgress.appendInfoLog("Building JSP for:'"
						+ defs[i].getBoName() + "'");

				bjsp = new boBuildJSP();
				bjsp
						.generate(boDefHandler.getBoDefinition(defs[i]
								.getBoName()));

				p_builderProgress.addCurrentTaskProgress();

			}

			p_builderProgress.addOverallProgress();
			this.p_builderProgress
					.setOverallTaskName("Creating JSP's for XEO Model Interfaces...");
			p_builderProgress.setCurrentTasks(defs.length);
			if (objectInterf != null && objectInterf.size() > 0) {
				Enumeration oEnum = objectInterf.keys();
				String mandInterface = null;

				while (oEnum.hasMoreElements()) {
					mandInterface = (String) oEnum.nextElement();
					boDefHandler defI = boDefHandler
							.getBoDefinition(mandInterface);
					if (defI != null) {
						p_builderProgress.setCurrentTaskName(defI.getLabel()
								+ " (" + defI.getName() + ")");
						bjsp = new boBuildJSP();
						p_builderProgress
								.appendInfoLog("Building Mandatory Interface JSP for:'"
										+ mandInterface + "'");
						bjsp.generate(defI);
					}
					p_builderProgress.addCurrentTaskProgress();
				}
			}

			// criação da directoria resources
			for (short i = 0; i < defs.length; i++) {
				try {
					createResources(defs[i].getBoName());
				} catch (IOException e) {
					p_builderProgress
							.appendInfoLog("Error creating resources directory for object: "
									+ defs[i].getBoName());
				}
			}

			p_builderProgress.addOverallProgress();
			this.p_builderProgress
					.setOverallTaskName("Updating timestamp of deployed descriptors...");
			// Update timestamps of deployed files;
			p_builderProgress
					.appendInfoLog("Updating TimeStamps of the deployed files...");
			for (int i = 0; i < todeploy.size(); i++) {
				boDefHandler xdef = (boDefHandler) todeploy.get(i);
				String ext = (xdef.getClassType() == boDefHandler.TYPE_INTERFACE) ? TYPE_INTERFACE
						: TYPE_BO;
				String oname = xdef.getName();

				oname += ext;

				File back = new File(p_bcfg.getDeploymentDir() + oname + ".bak");
				File ofile = new File(p_bcfg.getDeploymentDir() + oname);

				File srcfile = repository.getFile(xdef.getName(), ext);

				if (back.exists()) {
					back.delete();
				}

				ofile.setLastModified(srcfile.lastModified());
			}

			p_undeployeddefs.clear();
			boDefHandler.clearCache();
			p_eboctx.getApplication().getMemoryArchive()
					.clearCachedEmptyDataSet();
			ExplorerServer.clearCache();
			
			// rest web services module is present
			if (Thread.currentThread().getContextClassLoader().getResource("xeo.rest.webservices") != null ) {
				// build rest web services
				try {
					Class<?> xclass = Class.forName("netgest.bo.webservices.rest.builder.RESTwebServicesBuilder");
					p_builderProgress.appendInfoLog("Building REST WebServices...");
					Constructor con =  xclass.getConstructor(new Class[] {  boBuilderProgress.class } );
					Object xclassInstance = con.newInstance( new Object[] { p_builderProgress }  );
					Method m = xclass.getMethod("build",new Class<?>[]{}); 
					m.invoke(xclassInstance,new Object[]{});
				} catch (Exception e) {
					p_builderProgress.appendInfoLog("___________________________________");
					p_builderProgress.appendInfoLog("Failed build of REST WebServices...");
					p_builderProgress.appendInfoLog("___________________________________");
					e.printStackTrace();
				}
			}

			p_builderProgress.appendInfoLog("Deploy take "
					+ ((System.currentTimeMillis() - ms) / 1000) + " seconds.");
			p_builderProgress.appendInfoLog("Deploy finished.");

			success = true;
		} finally {
			if (!success) {
				p_builderProgress.appendInfoLog("Error deploying Files...");
				p_builderProgress
						.appendInfoLog("Rolling back deployment files...");
				for (int i = 0; i < todeploy.size(); i++) {
					boDefHandler xdef = (boDefHandler) todeploy.get(i);
					String oname = xdef.getName();
					String ext = (xdef.getClassType() == boDefHandler.TYPE_INTERFACE) ? TYPE_INTERFACE
							: TYPE_BO;
					oname += ext;

					File back = new File(p_bcfg.getDeploymentDir() + oname
							+ ".bak");
					File ofile = new File(p_bcfg.getDeploymentDir() + oname);
					File dsfile = new File(p_bcfg.getDeploymentDir() + oname);

					if (back.exists()) {
						IOUtils.copy(back, ofile);
						back.delete();
					} else {
						for (short z = 0; z < bofiles.length; z++) {
							if (bofiles[z].getName().equals(ofile.getName())) {
								ofile.delete();
								if (dsfile.exists()) {
									dsfile.delete();
								}
								break;
							}
						}
					}
				}

				// interfaces
				for (int i = 0; i < xfiles.length; i++) {
					if (xfiles[i].getName().toLowerCase().endsWith(
							TYPE_INTERFACE)) {
						File oInterface = new File(p_bcfg.getDeploymentDir()
								+ xfiles[i].getName());
						if (oInterface.exists()) {
							oInterface.delete();
						}
					}
				}

				p_builderProgress.appendInfoLog("Done.");
			} else {

				p_eboctx.getApplication().releaseClassLoader();
				docHTML_treeServer.clearCache();

			}
		}

	}

	public void buildDataBaseObjects(boBuildRepository repository,
			boDefHandler[] defs, Map objectInterf, List lovFiles,
			List wsdFiles, List scriptFiles, File[] bofiles,
			Map htdeploypackage, boolean buildSystemObjs)
			throws boRuntimeException {

		// Create transaction
		boolean myTrans = !p_eboctx.getConnectionManager()
				.isContainerTransactionActive();
		if (myTrans) {
			try {
				p_eboctx.beginContainerTransaction();
				final InitialContext ic = new InitialContext();
				UserTransaction ut = (UserTransaction) ic
						.lookup("java:comp/UserTransaction");
				ut.setTransactionTimeout(6000000);
			} catch (NamingException e) {
				throw new RuntimeException(e);
			} catch (SystemException e) {
				throw new RuntimeException(e);
			}
		}

		boolean ok = false;

		try {
			p_builderProgress.addOverallProgress();
			p_builderProgress
					.setOverallTaskName("Creating System Database Objects");
			if (buildSystemObjs) {

				// build ngtdic e index
				buildSpecialTables(p_eboctx, repository.getSchemaName());

			}

			Hashtable buildedobjects = new Hashtable();
			buildedobjects.clear();

			if (buildSystemObjs) {
				// Make a deep rebuild including system tables.
				p_builderProgress.appendInfoLog("Creating System Tables...");

				boBuildDB bdb = new boBuildDB(p_eboctx);
				bdb.buildSystemStables();
				p_builderProgress.appendInfoLog("Done.");

				p_builderProgress
						.appendInfoLog("Creating Views for System Tables... ");
				bdb = new boBuildDB(p_eboctx, repository, interfacedeploy);
				bdb.buildSystemViews();
			}

			// Build Attributes and table for Ebo_ClsReg.
			for (int i = defs.length - 1; i >= 0; i--) {
				if (defs[i].getName().equalsIgnoreCase("Ebo_ClsReg")) {
					boBuildDB bdb = new boBuildDB(p_eboctx);
					bdb.buildObject(boDefHandler.getBoDefinition(defs[i]
							.getName()), false, boBuildDB.BUILD_ATTRIBUTES);

					break;
				}
			}

			p_builderProgress.addOverallProgress();
			p_builderProgress.setOverallTaskName("Creating Database Tables");

			p_builderProgress.setCurrentTasks(defs.length);
			for (short i = 0; i < defs.length; i++) {
				if (defs[i].getClassType() == boDefHandler.TYPE_CLASS
						&& defs[i].getDataBaseManagerManageTables()) {
					p_builderProgress.setCurrentTaskName(defs[i].getLabel()
							+ " (" + defs[i].getName() + ")");
					p_builderProgress
							.appendInfoLog("Building DB Attributes for:'"
									+ defs[i].getName() + "'");

					if (buildedobjects.get(defs[i].getName()) == null) {
						if (!defs[i].getName().equalsIgnoreCase("Ebo_ClsReg")) {
							// long init = System.currentTimeMillis();

							boBuildDB bdb = new boBuildDB(p_eboctx);
							bdb.buildObject(boDefHandler
									.getBoDefinition(defs[i].getName()), false,
									boBuildDB.BUILD_ATTRIBUTES);
							buildedobjects.putAll(bdb.getBuildedObjects());

							// System.out.println( System.currentTimeMillis() -
							// init );
						}
					}
				}
				p_builderProgress.addCurrentTaskProgress();
			}

			// Build Database Objects

			p_builderProgress.addOverallProgress();
			p_builderProgress
					.setOverallTaskName("Creating Database Constraints");
			p_builderProgress.setCurrentTasks(defs.length);

			if (buildSystemObjs) {
				// Make a deep rebuild including system tables.
				p_builderProgress
						.appendInfoLog("Creating System Constraints...");
				boBuildDB bdb = new boBuildDB(p_eboctx);
				bdb.buildSystemKeys();
			}

			buildedobjects.clear();

			for (short i = 0; i < defs.length; i++) {
				if (defs[i].getClassType() == boDefHandler.TYPE_CLASS
						&& defs[i].getDataBaseManagerManageTables()) {
					p_builderProgress.setCurrentTaskName(defs[i].getLabel()
							+ " (" + defs[i].getName() + ")");

					p_builderProgress
							.appendInfoLog("Building DB Constraints for:'"
									+ defs[i].getName() + "'");

					if (buildedobjects.get(defs[i].getName()) == null) {
						boBuildDB bdb = new boBuildDB(p_eboctx);
						bdb
								.buildObject(boDefHandler
										.getBoDefinition(defs[i].getName()),
										false, boBuildDB.BUILD_CONSTRAINTS);
						buildedobjects.putAll(bdb.getBuildedObjects());
					}
				}
				p_builderProgress.addCurrentTaskProgress();
			}

			p_builderProgress.addOverallProgress();
			p_builderProgress.setOverallTaskName("Creating Database Views");

			p_builderProgress.setCurrentTasks(defs.length);
			// garantir que as vies das interfaces são as ultimas, devido a
			// estarem dependetes de outras views
			Vector interfaceDefs = new Vector();
			// Build Database Views
			for (short i = 0; i < defs.length; i++) {
				p_builderProgress.setCurrentTaskName(defs[i].getLabel() + " ("
						+ defs[i].getName() + ")");
				if (defs[i].getClassType() == boDefHandler.TYPE_INTERFACE) {
					if (defs[i].getInterfaceType() == boDefHandler.INTERFACE_OPTIONAL) {
						interfaceDefs.add(defs[i]);
					}
				} else {
					if (defs[i].getDataBaseManagerManageViews()) {
						p_builderProgress.appendInfoLog("Building Views for:'"
								+ defs[i].getName() + "'");

						boBuildDB bdb = new boBuildDB(p_eboctx);
						bdb.createInheritViews(boDefHandler
								.getBoDefinition(defs[i].getBoName()));
					}
				}
				p_builderProgress.addCurrentTaskProgress();
			}

			p_builderProgress.addOverallProgress();
			p_builderProgress.setOverallTaskName("Create Interfaces Views");
			p_builderProgress
					.appendInfoLog("Building Special Views for:'Ebo_ClsReg' and 'Ebo_Package'");
			boBuildDB specialView = new boBuildDB(p_eboctx);
			specialView.createInheritViews(boDefHandler
					.getBoDefinition("Ebo_ClsReg"), true);
			specialView = new boBuildDB(p_eboctx);
			specialView.createInheritViews(boDefHandler
					.getBoDefinition("Ebo_Package"), true);
			// Build Database Views for interfaces

			p_builderProgress.setCurrentTasks(interfaceDefs.size());
			for (short i = 0; i < interfaceDefs.size(); i++) {
				boDefHandler intDef = (boDefHandler) interfaceDefs.get(i);

				p_builderProgress.setCurrentTaskName(intDef.getLabel() + " ("
						+ intDef.getName() + ")"); 

				p_builderProgress.appendInfoLog("Building Interface View for:'"
						+ intDef.getName() + "'");

				boBuildDB bdb = new boBuildDB(p_eboctx);
				bdb.createInheritViews(boDefHandler.getBoDefinition(intDef
						.getBoName()));

				p_builderProgress.addCurrentTaskProgress();

			}
			if (objectInterf != null && objectInterf.size() > 0) {
				Iterator oIt = objectInterf.keySet().iterator();
				String mandInterface = null;
				while (oIt.hasNext()) {
					try {
						mandInterface = (String) oIt.next();
						p_builderProgress
								.appendInfoLog("Building Mandatory Interface View for:'"
										+ mandInterface + "'");
						boBuildDB bdb = new boBuildDB(p_eboctx);
						bdb.createInheritViewsForMandatoryInterfaces(
								mandInterface, (ArrayList) objectInterf
										.get(mandInterface));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}

			// Treating Scripts
			for (int i = 0; i < scriptFiles.size(); i++) {
				File scriptFile = ((File) scriptFiles.get(i));

				String name = scriptFile.getName().substring(0,
						scriptFile.getName().toLowerCase().indexOf(TYPE_SC));
				File deployfile = new File(p_bcfg.getDeploymentDir() + name
						+ boBuilder.TYPE_SC);

				IOUtils.copy(scriptFile, deployfile);

				deployfile.setLastModified(scriptFile.lastModified() - 600);

				p_builderProgress
						.appendInfoLog("Treating ScriptObjectHandler:'" + name
								+ "'");

				treatScript(p_eboctx, name);

				p_builderProgress.appendInfoLog("end...");
				deployfile.setLastModified(scriptFile.lastModified());
			}

			p_builderProgress.addOverallProgress();
			p_builderProgress.setOverallTaskName("Registring packgages...");

			p_builderProgress.setCurrentTasks(repository.getPackages().size());
			for (short i = 0; i < repository.getPackages().size(); i++) {

				String packagename = (String) repository.getPackages().get(i);

				p_builderProgress.setCurrentTaskName(packagename);

				p_builderProgress
						.appendInfoLog("Registering Packages in Ebo_Package:'"
								+ packagename + "'");

				boObject pack = null;

				try {
					pack = boObject.getBoManager().loadObject(p_eboctx,
							"Ebo_Package", "NAME='" + packagename + "'");
				} catch (boRuntimeException e) {
					p_builderProgress.appendErrorLog(e);
				}

				try {
					if ((pack == null) || !pack.exists()) {
						pack = boObject.getBoManager().createObject(p_eboctx,
								"Ebo_Package");
					}

					pack.getAttribute("name").setValueString(packagename);
					if (pack.getAttribute("deployed") != null) {
						pack.getAttribute("deployed").setValueString("1");
					}
					pack.update();
				} catch (Exception e) {
					p_builderProgress.appendErrorLog(e);
					throw new boRuntimeException("", e.getMessage(), null);
				}
				p_builderProgress.addCurrentTaskProgress();
			}

			// Register classes in Ebo_ClsReg
			p_builderProgress.addOverallProgress();
			p_builderProgress.setOverallTaskName("Registring XEO Models...");

			p_builderProgress.setCurrentTasks(defs.length);
			for (short i = 0; i < defs.length; i++) {
				if (i % 20 == 0) {
					p_eboctx.getApplication().getMemoryArchive()
							.getPoolManager().realeaseAllObjects(
									p_eboctx.poolUniqueId());
				}

				p_builderProgress.setCurrentTaskName(defs[i].getLabel() + " ("
						+ defs[i].getName() + ")");
				p_builderProgress
						.appendInfoLog("Registering classes in Ebo_ClsReg:'"
								+ defs[i].getBoName() + "'");

				boObject clsreg = null;

				try {
					clsreg = boObject.getBoManager().loadObject(p_eboctx,
							"Ebo_ClsReg", "NAME='" + defs[i].getBoName() + "'");
				} catch (boRuntimeException e) {
					p_builderProgress.appendErrorLog(e);
				}

				if ((clsreg == null) || !clsreg.exists()) {
					clsreg = boObject.getBoManager().createObject(p_eboctx,
							"Ebo_ClsReg");
				}

				String xml = clsreg.getAttribute("xmlsource").getValueString();
				String newXml = ngtXMLUtils.getXML((XMLDocument) boDefHandler
						.getBoDefinition(defs[i].getBoName()).getNode()
						.getOwnerDocument());
				if (!xml.equals(newXml)) {
					clsreg.getAttribute("name").setValueString(
							defs[i].getBoName());
					clsreg.getAttribute("description").setValueString(
							defs[i].getDescription());
					clsreg.getAttribute("label").setValueString(
							defs[i].getLabel());
					clsreg.getAttribute("xmlsource").setValueString(newXml);
					clsreg.getAttribute("phisicaltable").setValueString(
							defs[i].getBoPhisicalMasterTable());
					if (clsreg.getAttribute("deployed") != null) {
						clsreg.getAttribute("deployed").setValueString("1");
					}

					boObject pack = boObject.getBoManager().loadObject(
							p_eboctx,
							"Ebo_Package",
							"NAME='"
									+ (String) htdeploypackage.get(defs[i]
											.getBoName()) + "'");

					if (pack != null) {
						clsreg.getAttribute("xeopackage").setValueLong(
								pack.bo_boui);
					}

					buildAttributes(boDefHandler.getBoDefinition(defs[i]
							.getName()), clsreg);
					buildMethods(boDefHandler
							.getBoDefinition(defs[i].getName()), clsreg);

					clsreg.update();
				}
				p_builderProgress.addCurrentTaskProgress();

				// Fill the array with class BOUI
			}
			p_builderProgress
					.appendInfoLog("Redoing Normal Views for:'Ebo_ClsReg' and 'Ebo_Package'");
			specialView = new boBuildDB(p_eboctx);
			specialView.createInheritViews(boDefHandler
					.getBoDefinition("Ebo_ClsReg"));
			specialView = new boBuildDB(p_eboctx);
			specialView.createInheritViews(boDefHandler
					.getBoDefinition("Ebo_Package"));

			// construção das Lov's
			p_builderProgress.addOverallProgress();
			p_builderProgress.setOverallTaskName("Creating/Updating LOVs...");

			p_builderProgress.setCurrentTasks(lovFiles.size());
			for (int i = 0; i < lovFiles.size(); i++) {
				int j = ((Integer) lovFiles.get(i)).intValue();
				String name = bofiles[j].getName().substring(
						0,
						bofiles[j].getName().toLowerCase().indexOf(
								boBuilder.TYPE_LOV));

				p_builderProgress.setCurrentTaskName(name);

				File deployfile = new File(p_bcfg.getDeploymentDir() + name
						+ boBuilder.TYPE_LOV);

				IOUtils.copy(bofiles[j], deployfile);

				deployfile.setLastModified(bofiles[j].lastModified() - 600);

				p_builderProgress.appendInfoLog("Building LovObjectHandler:'"
						+ name + "'");

				buildLov(name);

				p_builderProgress.appendInfoLog("end...");

				deployfile.setLastModified(bofiles[j].lastModified());

				p_builderProgress.addCurrentTaskProgress();
			}

			// construção das WSD's
			for (int i = 0; i < wsdFiles.size(); i++) {
				int j = ((Integer) wsdFiles.get(i)).intValue();
				String name = bofiles[j].getName().substring(0,
						bofiles[j].getName().toLowerCase().indexOf(TYPE_WSD));
				File deployfile = new File(p_bcfg.getDeploymentDir() + name
						+ boBuilder.TYPE_WSD);
				p_builderProgress.appendInfoLog("Building WSD:'" + name + "'");

				IOUtils.copy(bofiles[j], deployfile);
				deployfile.setLastModified(bofiles[j].lastModified());
			}

			// Set package descriptions
			setPackagesDescription(p_eboctx);

			// Create System Users and Groups
			createSystemUsersandGroups(p_eboctx);

			p_builderProgress.addOverallProgress();
			p_builderProgress.setOverallTaskName("Removing User Workplaces...");
			if (p_builderOptions.getRemoveUserWorkplaces()) {
				p_builderProgress
						.appendInfoLog("Starting Removing User WorkPlaces ...");
				removeUserWorkPlaces(p_eboctx);
				p_builderProgress
						.appendInfoLog("Ended Removing User WorkPlaces");
			}

			p_builderProgress.addOverallProgress();
			p_builderProgress
					.setOverallTaskName("Removing Default Workplaces...");
			if (p_builderOptions.getBuildWorkplaces()) {
				p_builderProgress
						.appendInfoLog("Starting Rebuild Default WorkPlaces ...");
				buildWorkPlaceDefault(p_eboctx);
				p_builderProgress
						.appendInfoLog("Ended Rebuild Default WorkPlaces");
			}

			p_builderProgress.addOverallProgress();
			p_builderProgress
					.setOverallTaskName("Marking Objects as deployed...");
			if (p_builderOptions.getMarkDeployedObjects()) {
				p_builderProgress.appendInfoLog("Marking deployed Objects...");
				setDeployedObjects();
				p_builderProgress.appendInfoLog("End Mark deployed Objects...");
			}

			ok = true;

		} finally {
			if (myTrans) {
				if (ok)
					p_eboctx.commitContainerTransaction();
				else
					p_eboctx.rollbackContainerTransaction();
			}
		}

	}

	private void setDeployedObjects() {
		try {
			boBuildRepository repository = new boBuildRepository(p_eboctx
					.getBoSession().getRepository());
			File[] f = repository.getXMLFilesFromDefinition();
			ArrayList packages = repository.getPackages();
			String name;
			StringBuffer sbClsrg = new StringBuffer(" where ");
			StringBuffer sbPack = new StringBuffer(" where ");
			boObject pack = null;
			boolean first = true;
			boolean intf = false;
			for (int i = 0; i < f.length; i++) {
				intf = f[i].getName().toLowerCase().endsWith(TYPE_INTERFACE);
				if (f[i].getName().toLowerCase().endsWith(TYPE_BO) || intf) {
					name = f[i].getName().substring(
							0,
							f[i].getName().toLowerCase().indexOf(
									!intf ? TYPE_BO : TYPE_INTERFACE));

					boObjectList clsRegList = boObjectList.list(  p_eboctx, "select Ebo_ClsReg where name=?", new Object[]{ name });
//						clsreg = boObject.getBoManager().loadObject(p_eboctx,
//								"Ebo_ClsReg", "NAME='" + name + "'");
					if (clsRegList.next()) {
						if (first) {
							sbClsrg.append(" boui = ").append(
									clsRegList.getCurrentBoui());
							first = false;
						} else {
							sbClsrg.append(" or boui = ").append(
									clsRegList.getCurrentBoui());
						}
					}
				}
			}
			first = true;
			for (int i = 0; i < packages.size(); i++) {
				try {
					pack = boObject.getBoManager().loadObject(p_eboctx,
							"Ebo_Package",
							"NAME='" + (String) packages.get(i) + "'");
					if (pack.exists()) {
						if (first) {
							sbPack.append(" boui = ").append(pack.getBoui());
							first = false;
						} else {
							sbPack.append(" or boui = ").append(pack.getBoui());
						}
					}
				} catch (boRuntimeException e) {
					p_builderProgress.appendErrorLog(e);
				}
			}
			ArrayList updates = new ArrayList();
			updates.add("update ebo_clsreg set deployed = '0'");
			updates.add("update ebo_package set deployed = '0'");
			updates.add("update ebo_clsreg set deployed = '1' "
					+ sbClsrg.toString());
			updates.add("update ebo_package set deployed = '1' "
					+ sbPack.toString());
			executeUpdate(this.p_eboctx, updates);
		} catch (boRuntimeException e) {
			p_builderProgress
					.appendErrorLog("Não foi possível efectuar o setDeployed.");
		}
	}

	private void executeUpdate(EboContext eboCtx, ArrayList updates)
			throws boRuntimeException {
		Connection con = null;
		CallableStatement csm = null;
		int n;
		try {
			con = eboCtx.getConnectionData();
			for (int i = 0; i < updates.size(); i++) {
				String sql = (String) updates.get(i);

				if ((sql != null) && (sql.length() > 0)) {
					csm = con.prepareCall(sql);
					n = csm.executeUpdate();
					csm.close();
					p_builderProgress
							.appendInfoLog("Executed Query to set Deployed Objects. updated "
									+ n + " records.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				// ignora
			}

			try {
				if (csm != null) {
					csm.close();
				}
			} catch (Exception e) {
				// ignora
			}
		}
	}

	private void makeBuildDep(String boname, Stack stack, boolean checkdep) {
		makeBuildDep(boname, stack, checkdep, false, true);
	}

	public static void buildMethods(boDefHandler bodef, boObject clsreg)
			throws boRuntimeException {
		
		  try { bridgeHandler bridge = clsreg.getBridge("methods"); boObject
		  method = null;
		  
		  boDefMethod[] a_met; a_met = bodef.getBoMethods();
		  
		  boDefMethod metdef;
		  
		  for (int i = 0; i < a_met.length; i++) { metdef = a_met[i];
		  
		  String x_name = metdef.getName();
		  
		  boolean find = false; bridge.beforeFirst();
		  
		  while (!find && bridge.next()) { method = bridge.getObject();
		  
		  if (method.getAttribute("name").getValueString()
		  .equalsIgnoreCase(x_name)) { find = true; method.edit(); } }
		  
		  if (!find) { method = bridge.addNewObject("Ebo_Method"); }
		  
		  String methodName = metdef.getName(); String mthLabel =
		  metdef.getLabel();
		  method.getAttribute("name").setValueString(methodName);
		  method.getAttribute("description").setValueString(methodName);
		  method.getAttribute("label").setValueString(mthLabel == null ?
		  methodName:mthLabel);
		  method.getAttribute("clsReg").setObject(clsreg); }
		  
		  bridge.beforeFirst();
		  
		  Hashtable auxToCreate = new Hashtable();
		  
		  while (bridge.next()) { boolean find = false; String name1 =
		  bridge.getObject().getAttribute("name") .getValueString();
		  
		  for (int i = 0; i < a_met.length; i++) { if
		  (a_met[i].getName().equals(name1)) { find = true; } }
		  
		  if (name1.equals("Print") || name1.equals("Tree") ||
		  name1.equals("Properties") || name1.equals("Graph") ||
		  name1.equals("CreateTemplate") || name1.equals("ExportObject")) {
		  find = true; auxToCreate.put(name1, name1); }
		  
		  if (!find) { bridge.remove(); } }
		  
		  if (auxToCreate.get("Print") == null) { method =
		  bridge.addNewObject("Ebo_Method");
		  method.getAttribute("name").setValueString("Print");
		  method.getAttribute("description").setValueString("Print Object");
		  method.getAttribute("clsReg").setObject(clsreg); }
		  
		  if (auxToCreate.get("Tree") == null) { method =
		  bridge.addNewObject("Ebo_Method");
		  method.getAttribute("name").setValueString("Tree");
		  method.getAttribute
		  ("description").setValueString("View Object Tree");
		  method.getAttribute("clsReg").setObject(clsreg); }
		  
		  if (auxToCreate.get("Properties") == null) { method =
		  bridge.addNewObject("Ebo_Method");
		  method.getAttribute("name").setValueString("Properties");
		  method.getAttribute
		  ("description").setValueString("View Object Properties");
		  method.getAttribute("clsReg").setObject(clsreg); }
		  
		  if (auxToCreate.get("Graph") == null) { method =
		  bridge.addNewObject("Ebo_Method");
		  method.getAttribute("name").setValueString("Graph");
		  method.getAttribute
		  ("description").setValueString("View Object Graph");
		  method.getAttribute("clsReg").setObject(clsreg); }
		  
		  if (auxToCreate.get("CreateTemplate") == null) { method =
		  bridge.addNewObject("Ebo_Method");
		  method.getAttribute("name").setValueString("CreateTemplate");
		  method.getAttribute
		  ("description").setValueString("Create Object Template");
		  method.getAttribute("clsReg").setObject(clsreg);
		  
		  }
		  
		  if (auxToCreate.get("ExportObject") == null) { method =
		  bridge.addNewObject("Ebo_Method");
		  method.getAttribute("name").setValueString("ExportObject");
		  method.getAttribute
		  ("description").setValueString("Export Object Info");
		  method.getAttribute("clsReg").setObject(clsreg); }
		  
		  if (bodef.getBoClsState() != null) { String[] stateMethods =
		  bodef.getBoClsState() .getAllStateMethods();
		  
		  if (stateMethods != null) { for (int i = 0; i < stateMethods.length;
		  i++) { String methodName = stateMethods[i]; boolean find = false;
		  bridge.beforeFirst();
		  
		  while (!find && bridge.next()) { method = bridge.getObject();
		  
		  if (method.getAttribute("name").getValueString()
		  .equalsIgnoreCase(methodName)) { find = true; method.edit(); } }
		  
		  if (!find) { method = bridge.addNewObject("Ebo_Method"); }
		  
		  method.getAttribute("name").setValueString(methodName);
		  method.getAttribute("description").setValueString(methodName);
		  method.getAttribute("clsReg").setObject(clsreg); } } } } catch
		  (boException e) { 
			  //throw(e); 
		  }
		 
	}

	public void buildAttributes(boDefHandler bodef, boObject clsreg)
			throws boRuntimeException {
		try {
			bridgeHandler bridge = clsreg.getBridge("attributes");
			boObject attribute = null;
			boDefAttribute[] a_att;
			a_att = bodef.getBoAttributes();

			boDefAttribute atrdef;

			for (int i = 0; i < a_att.length; i++) {
				atrdef = a_att[i];

				String x_name = atrdef.getName();

				boolean find = false;
				bridge.beforeFirst();

				while (!find && bridge.next()) {
					attribute = bridge.getObject();

					if (attribute.getAttribute("name").getValueString()
							.equalsIgnoreCase(x_name)) {
						find = true;
						attribute.edit();
					}
				}

				if (!find) {
					if (atrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
						attribute = bridge.addNewObject("Ebo_Attribute");
					} else if (atrdef.getAtributeType() == boDefAttribute.TYPE_STATEATTRIBUTE) {
						attribute = bridge.addNewObject("Ebo_Attribute");
					} else {
						attribute = bridge.addNewObject("Ebo_Attribute");
					}

					// falta desenvolver os sub-objectos do attributo
				}

				if (atrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
					if ((atrdef.getBridge() != null)
							&& atrdef.getBridge().haveBridgeAttributes()) {
						boDefAttribute[] b_batt = a_att[i].getBridge()
								.getBoAttributes();

						for (int z = 0; z < b_batt.length; z++) {
							// if(b_batt[z].getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE
							// &&
							// b_batt[z].getReferencedObjectName().equals(boname))
							// {
							// }
						}
					}
				}

				if (atrdef.getLovItems() != null) {
					String lovName = atrdef.getLOVName();
					boolean rtValues = atrdef.getLovRetainValues();
					boBuildLov l = new boBuildLov(p_eboctx);
					l.buildLov(lovName, rtValues, atrdef.getLovItems());
				}

				String attributeName = atrdef.getName();
				String attributeLabel = atrdef.getLabel();
				attribute.getAttribute("name").setValueString(atrdef.getName());
				attribute.getAttribute("description").setValueString(
						atrdef.getDescription());
				attribute.getAttribute("label")
						.setValueString(
								attributeLabel == null ? attributeName
										: attributeLabel);
				attribute.getAttribute("clsReg").setObject(clsreg);
			}

			bridge.beforeFirst();

			while (bridge.next()) {
				String name1 = bridge.getObject().getAttribute("name")
						.getValueString();

				if (!bodef.hasAttribute(name1)) {
					bridge.remove();
				}
			}
		} catch (boException e) {
			// throw(e);
		}
	}

	private void makeBuildDep(String boname, Stack stack, boolean checkdep,
			boolean changord, boolean checkDescendants) {
		boBuildRepository repository = new boBuildRepository(p_eboctx
				.getBoSession().getRepository());
		// VERIFICAR
		boDefHandler cdef = getUndeployedDefinitions(repository, boname,
				interfacedeploy, false);

		if (cdef == null) {
			return;
		}
		if ((stack.indexOf(boname) == -1) && !boname.equals("boObject")) {
			stack.push(boname);
			if (cdef.getBoSuperBo() != null) {
				makeBuildDep(cdef.getBoSuperBo(), stack, false, true, false);
			}

			if ((cdef.getBoInterfaces() != null)
					&& (cdef.getBoInterfaces().length > 0)) {
				for (byte i = 0; i < cdef.getBoInterfaces().length; i++) {
					makeBuildDep(cdef.getBoInterfaces()[i].getName(), stack,
							false, true, true);
				}
			}

			boDefHandler[] allbo = listUndeployedDefinitions(repository,
					interfacedeploy);
			boDefAttribute[] a_att;

			for (short j = 0; j < allbo.length; j++) {
				if ((allbo[j].getBoSuperBo() != null)
						&& allbo[j].getBoSuperBo().equals(boname)
						&& checkDescendants) {
					// if(stack.indexOf(allbo[j].getBoSuperBo())==-1)
					// {
					// p_builderProgress.appendInfoLog("Found reference for [" +
					// boname +
					// "] in :" + allbo[j].getName());
					makeBuildDep(allbo[j].getBoName(), stack, checkdep);

					// }
				}

				if (cdef.getClassType() == boDefHandler.TYPE_INTERFACE) {
					if (allbo[j].getBoImplements(boname)) {
						// p_builderProgress.appendInfoLog("Found reference for ["
						// + boname +
						// "] in :" + allbo[j].getName());
						makeBuildDep(allbo[j].getBoName(), stack, checkdep);
					}
				}

				if (checkdep) {
					a_att = allbo[j].getBoAttributes();

					for (int i = 0; i < a_att.length; i++) {
						if ((a_att[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
								&& a_att[i].getReferencedObjectName().equals(
										boname)) {
							if ((a_att[i].getBridge() != null)
									&& a_att[i].getBridge()
											.haveBridgeAttributes()) {
								boDefAttribute[] b_batt = a_att[i].getBridge()
										.getBoAttributes();

								for (int z = 0; z < b_batt.length; z++) {
									if ((b_batt[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
											&& b_batt[z]
													.getReferencedObjectName()
													.equals(boname)) {
										// if(stack.indexOf(b_batt[z].getReferencedObjectName())==-1)
										// {
										// p_builderProgress.appendInfoLog("Found reference for ["
										// +
										// boname + "] in :" +
										// b_batt[z].getBoDefHandler().getName());
										makeBuildDep(b_batt[z]
												.getReferencedObjectName(),
												stack, checkdep, false, true);

										// stack.push();
										// }
										// makeBuildDep(b_batt[z].getReferencedObjectName(),stack);
									}
								}
							}

							// if(stack.indexOf(allbo[j].getBoName())==-1)
							// {
							// p_builderProgress.appendInfoLog("Found reference for ["
							// + boname +
							// "] in :" + allbo[j].getBoName());

							makeBuildDep(allbo[j].getBoName(), stack, checkdep,
									false, true);

							// stack.push(allbo[j].getBoName());
							// }
							// makeBuildDep(allbo[j].getBoName(),stack);
						}
					}
				}
			}
		} 
		else 
		{
			if (changord && !boname.equals("boObject")) {
				stack.remove(boname);
				stack.push(boname);

				if (cdef.getBoSuperBo() != null) {
					makeBuildDep(cdef.getBoSuperBo(), stack, false, true, true);
				}
			}
		}
	}

	public String getDML() {
		return p_dml;
	}

	// Deprecated Code
	public static final void buildInterfaces(boBuildRepository repository,
			boDefHandler bodef, Hashtable interfaceMap) {
		boDefHandler[] interfs = bodef.getBoInterfaces();

		String attName = null;
		for (int i = 0; (interfs != null)
				&& (i < interfs.length && interfs[i].getInterfaceType() == boDefHandler.INTERFACE_OPTIONAL); i++) {
			// VERIFICAR
			inheritObject(interfs[i], bodef, true, true, false);

			ngtXMLHandler atts = bodef.getChildNode("attributes");
			attName = "implements_" + interfs[i].getName();
			if (bodef.getAttributeRef(attName) == null) {
				atts.getNode().appendChild(
						boDefUtils.createAttribute(attName, "I$"
								+ interfs[i].getName(), "Implements interface "
								+ interfs[i].getName(), "attributeText", "", 1,
								false, bodef.getDocument()));
			}
		}

		bodef.refresh();
		fillInterfaceAttributes(repository, bodef, interfaceMap);
		fillSystemAttributes(repository, bodef, interfaceMap);
	}

	private final void buildInherit(boBuildRepository repository, 
			boDefHandler bodef, 
			Hashtable objectInterfaceMap, 
			boolean createdFwdMethods ) throws boRuntimeException {
		
		if (bodef.getBoSuperBo() != null) {
			
			String superbo = bodef.getBoSuperBo();
			
			boDefHandler bo = getUndeployedDefinitions(
									repository, 
									superbo,
									objectInterfaceMap, 
									createdFwdMethods
							);
			
			_buildInherit(repository, bo, bodef, true, objectInterfaceMap,
					createdFwdMethods);
		} 
		else if (createdFwdMethods) 
		{
			createMethodsFromFwdObjects(bodef);
		}
		bodef.refresh();
		fillInterfaceAttributes(repository, bodef, objectInterfaceMap);
		fillSystemAttributes(repository, bodef, objectInterfaceMap);
		
	}

	private final void _buildInherit(boBuildRepository repository,
			boDefHandler superbo, boDefHandler subbo, boolean inherit,
			Hashtable objectInterfaceMap, boolean createdFwdMethods) {
		inheritObject(superbo, subbo, inherit, false, createdFwdMethods);

		if (superbo.getBoSuperBo() != null) {
			boDefHandler bo = getUndeployedDefinitions(repository, superbo
					.getBoSuperBo(), objectInterfaceMap, createdFwdMethods);
			_buildInherit(repository, bo, subbo, false, objectInterfaceMap,
					createdFwdMethods);
		}
	}
	
	 public static long getXEOStudioBuilderLastRun()
     {
		 if (xeoStudioBuildLastRun==0)
		 {
	    	File buildFlag  = new File( boConfig.getDeploymentDir()+"buildflag" );
	    	if (buildFlag.exists())
	    	{
	    		try
	    		{
	    			BufferedReader reader = new BufferedReader(new FileReader(buildFlag));	    					
	    			String value=reader.readLine();
	    			reader.close();
	    			xeoStudioBuildLastRun = new Long(value).longValue();

	    		}
	    		catch (Exception e)
	    		{
	    			xeoStudioBuildLastRun=buildFlag.lastModified();
	    		}
	    	}
		 }
		 return xeoStudioBuildLastRun;
     }
	 
	 public static void resetXEOStudioBuilderLastRun()
	 {
	    	File buildFlag  = new File( boConfig.getDeploymentDir()+"buildflag" );
	    	if (buildFlag.exists())
	    	{
	    		buildFlag.delete();
	    	}
	    	xeoStudioBuildLastRun = 0;
	 }

	public static final void fillInterfaceAttributes(
			boBuildRepository repository, boDefHandler bodef,
			Hashtable objectInterfaceMap) {
		try {
			if (bodef.getClassType() == boDefHandler.TYPE_INTERFACE) {
				return;
			}
			ngtXMLHandler atts = bodef.getChildNode("attributes");

			ArrayList alredyDone = new ArrayList();
			ArrayList arrayOfInterfaces = null;
			if (objectInterfaceMap != null
					&& (arrayOfInterfaces = (ArrayList) objectInterfaceMap
							.get(bodef.getName())) != null) {
				boDefInterface intf;
				String intfName;
				boolean changed = false;
				for (int i = 0; i < arrayOfInterfaces.size(); i++) {
					changed = false;
					intfName = (String) arrayOfInterfaces.get(i);
					if (!alredyDone.contains(intfName)) {
						intf = boDefHandler.getInterfaceDefinition(intfName);
						if (intf == null) {
							logger.warn("Interface " + intfName
									+ " does not exists.");
							continue;
						}

						boDefAttribute[] intfAtts = intf.getImplAttributes();
						if (intfAtts != null) {
							for (int j = 0; j < intfAtts.length; j++) {
								if (!bodef.hasAttribute(intfAtts[j].getName())) {
									Node auxN = atts.getNode()
											.getOwnerDocument()
											.importNode(intfAtts[j].getNode(),
													true);
									Attr atr = atts.getNode()
											.getOwnerDocument()
											.createAttribute("fromInterface");
									atr.setValue(intf.getName());
									((XMLElement) auxN).setAttributeNode(atr);
									atts.getNode().appendChild(auxN);
									changed = true;
								}
							}
						}
						alredyDone.add(intfName);
					}
					if (changed) {
						bodef.refresh();
					}
				}
			}
		} catch (ClassCastException e) {
			throw e;
		}
	}

	public static final void fillInterfaceMethdos(boBuildRepository repository,
			boDefHandler bodef, Hashtable objectInterfaceMap) {
		
		if (bodef.getClassType() == boDefHandler.TYPE_INTERFACE) {
			return;
		}
		
		ngtXMLHandler atts = bodef.getChildNode("methods");
		ArrayList alredyDone = new ArrayList();
		ArrayList arrayOfInterfaces = null;
		
		if ( objectInterfaceMap != null && (arrayOfInterfaces = (ArrayList) objectInterfaceMap.get(bodef.getName())) != null) 
		{
			boDefInterface intf;
			String intfName;
			for (int i = 0; i < arrayOfInterfaces.size(); i++) {
				
				intfName = (String) arrayOfInterfaces.get(i);
				
				if (!alredyDone.contains(intfName)) {
					
					intf = boDefHandler.getInterfaceDefinition(intfName);
					if (intf == null) {
						continue;
					}
					
					boDefMethod[] intfMethods = intf.getImplMethods();
					if (intfMethods != null) 
					{
						for (int j = 0; j < intfMethods.length; j++) 
						{
							if (!bodef.hasMethod(intfMethods[j].getName()))
							{
								Node auxN = atts.getNode().getOwnerDocument()
										.importNode(intfMethods[j].getNode(),
												true);
								Attr atr = atts.getNode().getOwnerDocument()
										.createAttribute("fromInterface");
								atr.setValue(intf.getName());
								((XMLElement) auxN).setAttributeNode(atr);
								atts.getNode().appendChild(auxN);
							}
						}
					}
					alredyDone.add(intfName);
				}
				// Update bodefinition with the new methods!
				bodef.refresh();
			}
		}
		bodef.refresh();
	}

	public static final void fillInterfaceEvents(boBuildRepository repository,
			boDefHandler bodef, Hashtable objectInterfaceMap) {
		if (bodef.getClassType() == boDefHandler.TYPE_INTERFACE) {
			return;
		}
		ngtXMLHandler atts = bodef.getChildNode("events");

		ArrayList alredyDone = new ArrayList();
		ArrayList arrayOfInterfaces = null;
		if (objectInterfaceMap != null
				&& (arrayOfInterfaces = (ArrayList) objectInterfaceMap
						.get(bodef.getName())) != null) {
			boDefInterface intf;
			String intfName;
			for (int i = 0; i < arrayOfInterfaces.size(); i++) {
				intfName = (String) arrayOfInterfaces.get(i);
				if (!alredyDone.contains(intfName)) {
					intf = boDefHandler.getInterfaceDefinition(intfName);
					if (intf == null) {
						continue;
					}
					boDefClsEvents[] intfEvents = intf.getImplEvents();
					if (intfEvents != null) {
						for (int j = 0; j < intfEvents.length; j++) {
							if (!bodef.hasEvent(intfEvents[j].getEventName())) {
								Node auxN = atts.getNode().getOwnerDocument()
										.importNode(intfEvents[j].getNode(),
												true);
								Attr atr = atts.getNode().getOwnerDocument()
										.createAttribute("fromInterface");
								atr.setValue(intf.getName());
								((XMLElement) auxN).setAttributeNode(atr);
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

	public static final void fillSystemAttributes(boBuildRepository repository,
			boDefHandler bodef, Hashtable objectInterfaceMap) {

		ngtXMLHandler atts = bodef.getChildNode("attributes");

		if (!bodef.hasAttribute("PARENT")) {
			boDefHandler xderiv = bodef;
			boolean multiparent = xderiv.getBoHaveMultiParent();

			while (!multiparent && (xderiv.getBoSuperBo() != null)
					&& (xderiv.getBoSuperBo().trim().length() > 0)) {
				// VERIFICAR
				String name = xderiv.getName();
				String xderivName = xderiv.getBoSuperBo();
				xderiv = getUndeployedDefinitions(repository, xderiv
						.getBoSuperBo(), objectInterfaceMap, false);

				if (xderiv == null) {
					throw new RuntimeException("Object [" + name
							+ "] extends a unknown object [" + xderivName + "]");
				}

				if (xderiv.getBoHaveMultiParent()) {
					multiparent = true;
				}
			}
			// VERIFICAR
			if (!bodef.getBoIsSubBo()
					|| (getUndeployedDefinitions(repository,
							bodef.getBoSuperBo(), objectInterfaceMap, false)
							.getBoHaveMultiParent() != multiparent)) {
				atts.getNode().appendChild(
						boDefUtils.createAttribute("PARENT", "PARENT$",
								"Objecto Pai", "attributeObject",
								"object.boObject", 0, multiparent, atts
										.getNode().getOwnerDocument()));
			}
		}

		if (!bodef.getBoIsSubBo()) {
			if (!bodef.hasAttribute("PARENTCTX")) {
				atts.getNode().appendChild(
						boDefUtils.createAttribute("PARENTCTX", "PARENTCTX$",
								"Contexto de Criação", "attributeObject",
								"object.boObject", 0, false, atts.getNode()
										.getOwnerDocument()));
			}

			if (!bodef.hasAttribute("TEMPLATE")
					&& !bodef.getName().equals("Ebo_Template")
					&& !bodef.getName().equals("Ebo_Map")) {
				atts.getNode().appendChild(
						boDefUtils.createAttribute("TEMPLATE", "TEMPLATE$",
								"Modelo", "attributeObject",
								"object.Ebo_Template", 0, false, atts.getNode()
										.getOwnerDocument()));
			}

			if (!bodef.hasAttribute("BOUI")) {
				atts.getNode().appendChild(
						boDefUtils.createAttribute("BOUI", "BOUI", "BOUI",
								"attributeNumber", "", 0, false, atts.getNode()
										.getOwnerDocument()));
			}

			if (!bodef.hasAttribute("CLASSNAME")) {
				atts.getNode().appendChild(
						boDefUtils.createAttribute("CLASSNAME", "CLASSNAME",
								"Categoria do Objecto", "attributeText", "",
								50, false, atts.getNode().getOwnerDocument()));
			}

			if (!bodef.hasAttribute("CREATOR")) {
				atts.getNode().appendChild(
						boDefUtils.createAttribute("CREATOR", "CREATOR$",
								"Criador", "attributeObject",
								"object.iXEOUser", 0, false, atts.getNode()
										.getOwnerDocument()));
			}

			if (!bodef.hasAttribute("SYS_DTCREATE")) {
				atts.getNode().appendChild(
						boDefUtils.createAttribute("SYS_DTCREATE",
								"SYS_DTCREATE", "Data de Criação",
								"attributeDateTime", "", 0, false, atts
										.getNode().getOwnerDocument()));
			}

			if (!bodef.hasAttribute("SYS_DTSAVE")) {
				atts.getNode().appendChild(
						boDefUtils.createAttribute("SYS_DTSAVE", "SYS_DTSAVE",
								"Data da última actualização",
								"attributeDateTime", "", 0, false, atts
										.getNode().getOwnerDocument()));
			}

			if (!bodef.hasAttribute("SYS_ORIGIN")) {
				atts.getNode().appendChild(
						boDefUtils.createAttribute("SYS_ORIGIN", "SYS_ORIGIN",
								"Origem dos dados", "attributeText", "", 30,
								false, atts.getNode().getOwnerDocument()));
			}
			if (!bodef.hasAttribute("SYS_FROMOBJ")) {
				atts.getNode().appendChild(
						boDefUtils.createAttribute("SYS_FROMOBJ",
								"SYS_FROMOBJ$", "Objecto Origem",
								"attributeObject", "object.boObject", 0, false,
								atts.getNode().getOwnerDocument()));
			}
		}

		fillBrigdesAtts(bodef);
		bodef.refresh();
	}

	private static final void fillBrigdesAtts(boDef def) {
		boDefAttribute[] atts = def.getBoAttributes();

		for (short i = 0; i < atts.length; i++) {
			if ((atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
					&& (atts[i].getMaxOccurs() > 1)) {
				if (!atts[i].getBridge().hasAttribute("LIN")) {
					if (atts[i].getChildNode("bridge") == null) {
						atts[i].getNode().appendChild(
								atts[i].getNode().getOwnerDocument()
										.createElement("bridge"));
					}

					if (atts[i].getChildNode("bridge").getChildNode(
							"attributes") == null) {
						atts[i].getChildNode("bridge").getNode().appendChild(
								atts[i].getNode().getOwnerDocument()
										.createElement("attributes"));
					}

					atts[i].getChildNode("bridge").getChildNode("attributes")
							.getNode().appendChild(
									boDefUtils.createAttribute("LIN", "LIN",
											"Linha", "attributeNumber",
											"NUMBER", 0, false, atts[i]
													.getNode()
													.getOwnerDocument()));
				}
			}
		}
	}

	private static final void createMethodsFromFwdObjects(boDefHandler bodef)
			throws boRuntimeException {
		try {
			// inherited fwdObjects
			boDefForwardObject[] fwdObjs = bodef.getForwardObjects();
			boDefForwardObject fwdObj;
			String code;
			Element method = null;
			Element methods = (Element) ((XMLElement) bodef.getNode())
					.selectSingleNode("Methods");
			if (fwdObjs != null) {
				CodeJavaConstructor cjc = new CodeJavaConstructor();
				cjc.setValues(bodef);
				for (int i = 0; i < fwdObjs.length; i++) {
					fwdObj = fwdObjs[i];
					if (bodef.getBoMethod(fwdObj.getMapMethodName()) == null) {
						code = cjc.getForwardMapCode(fwdObj.toBoObject());
						ArrayList paramNames = new ArrayList();
						paramNames.add("fwdBoui");
						ArrayList paramTypes = new ArrayList();
						paramTypes.add("long");
						String hiddenWhen = "CODE_JAVA(if(!this.exists()){return true;} return false;)";
						method = boDefUtils.createMethod(fwdObj
								.getMapMethodName(), "boObject", "fwdObject",
								fwdObj.toBoObject(), fwdObj.openDoc(), true,
								true, false, fwdObj.getLabel(), fwdObj
										.getLabel(), false, code, hiddenWhen,
								null, null, methods.getOwnerDocument());
						methods.appendChild(method);
						method = boDefUtils.createMethod(fwdObj
								.getBeforeMapMethodName(), "void", "fwdObject",
								null, false, false, false, false, "afterMap",
								"afterMap", false, fwdObj.getBeforeMapClass(),
								null, paramTypes, paramNames, methods
										.getOwnerDocument());
						methods.appendChild(method);
						method = boDefUtils.createMethod(fwdObj
								.getAfterMapMethodName(), "void", "fwdObject",
								null, false, false, false, false, "beforeMap",
								"beforeMap", false, fwdObj.getAfterMapClass(),
								null, paramTypes, paramNames, methods
										.getOwnerDocument());
						methods.appendChild(method);
					}
				}
				bodef.refresh();
			}
		} catch (XSLException e) {

		}

	}

	private static final void inheritObject(boDefHandler superbo,
			boDefHandler subbo, boolean inheritfrom, boolean interf,
			boolean createFwdMethods) {

		subbo.refresh();

		try {
			if (createFwdMethods) {
				try {
					createMethodsFromFwdObjects(subbo);
				} catch (boRuntimeException e) {
					logger.severe(e);
				}
				return;
			}

			if (superbo.getBoHaveMultiParent()) {
				((Element) ((XMLElement) subbo.getNode())
						.selectSingleNode("general")).setAttribute(
						"multiparent", "true");
			}

			if (inheritfrom && !interf) {

				Element xx = (Element) ((XMLElement) superbo.getNode())
						.selectSingleNode("states");
				Element yy = (Element) ((XMLElement) subbo.getNode())
						.selectSingleNode("states");

				if ((xx.getAttribute("refers")).length() > 0) {
					if ((yy != null)
							&& (yy.getAttribute("refers").length() == 0)) {
						yy.setAttribute("refers", xx.getAttribute("refers"));
					}
				}
			}

			boDefAttribute[] srcatts = superbo.getBoAttributes();

			for (short i = 0; i < srcatts.length; i++) {
				if (srcatts[i].getAtributeType() != boDefAttribute.TYPE_STATEATTRIBUTE) {

					if (!subbo.hasAttribute(srcatts[i].getName())) {
						Node nodeadded = subbo.getChildNode("attributes")
								.getNode().appendChild(
										subbo.getNode().getOwnerDocument()
												.importNode(
														srcatts[i].getNode(),
														true));

						if (superbo.getClassType() == boDefHandler.TYPE_CLASS) {
							// if( inheritf == null || inheritf.length()==0 )
							((Element) nodeadded).setAttribute("inheritfrom",
									superbo.getBoName());
						}

						if (superbo.getClassType() == boDefHandler.TYPE_INTERFACE) {
							String imp = ((Element) nodeadded)
									.getAttribute("implementedby");

							if ((imp == null) || (imp.length() == 0)) {
								imp = "";
							} else {
								imp += ",";
							}

							imp += superbo.getBoName();

							((Element) nodeadded).setAttribute("implementedby",
									imp);
						}
					} else {
						if (inheritfrom) {
							ngtXMLUtils.mergeNodes((XMLNode) srcatts[i]
									.getNode(), (XMLNode) subbo
									.getAttributeRef(srcatts[i].getName())
									.getNode(),
									ngtXMLUtils.MERGE_BY_ATTNAME_OR_NODENAME);
						}

						Element updnode = null;
						try {
							updnode = ((Element) (XMLNode) subbo
									.getAttributeRef(srcatts[i].getName())
									.getNode());
						} catch (Exception e) {
							e.printStackTrace();
							subbo.getAttributeRef(srcatts[i].getName());

						}

						if (superbo.getClassType() == boDefHandler.TYPE_CLASS) {
							// if(((Element)srcatts[i].getNode()).getAttribute("inheritfrom")
							// == null )
							updnode.setAttribute("inheritfrom", superbo
									.getBoName());
						}

						if (superbo.getClassType() == boDefHandler.TYPE_INTERFACE) {
							String imp = updnode.getAttribute("implementedby");

							if ((imp == null) || (imp.length() == 0)) {
								imp = "";
							} else {
								imp += ",";
							}

							imp += superbo.getBoName();

							updnode.setAttribute("implementedby", imp);
						}
					}
				}
			}

			// inherited fwdObjects
			boDefForwardObject[] superFwdObjs = superbo.getForwardObjects();
			boDefForwardObject[] subFwdObjs = subbo.getForwardObjects();

			if (superFwdObjs != null && superFwdObjs.length > 0) {
				if (subFwdObjs != null && subFwdObjs.length > 0) {
					String auxName;
					boolean found = false;
					for (int i = 0; i < superFwdObjs.length; i++) {
						found = false;
						auxName = superFwdObjs[i].toBoObject();
						for (int j = 0; j < subFwdObjs.length; j++) {
							if (auxName.equals(subFwdObjs[j].toBoObject())) {
								found = true;
							}
						}
						if (!found) {
							Node fwdNode = subbo.getChildNode("fwdObjects")
									.getNode();
							Node appendNode = fwdNode.appendChild(subbo
									.getNode().getOwnerDocument().importNode(
											superFwdObjs[i].getNode(), true));
							Attr inhreatedAtt = appendNode.getOwnerDocument()
									.createAttribute("inheritfrom");
							inhreatedAtt.setValue(superbo.getName());
						}
					}
				} else {
					if (subbo.getChildNode("fwdObjects") == null) {
						subbo.getNode().appendChild(
								subbo.getNode().getOwnerDocument().importNode(
										superbo.getChildNode("fwdObjects")
												.getNode(), true));
						ngtXMLHandler[] nodes = subbo
								.getChildNode("fwdObjects").getChildNodes();
						Attr inhreatedAtt;
						for (int i = 0; i < nodes.length; i++) {
							inhreatedAtt = nodes[i].getNode()
									.getOwnerDocument().createAttribute(
											"inheritfrom");
							inhreatedAtt.setValue(superbo.getName());
						}
					} else {
						Node fwdNode = subbo.getChildNode("fwdObjects")
								.getNode();
						Node appendNode;
						Attr inhreatedAtt;
						for (int i = 0; i < superFwdObjs.length; i++) {
							appendNode = fwdNode.appendChild(subbo.getNode()
									.getOwnerDocument().importNode(
											superFwdObjs[i].getNode(), true));
							inhreatedAtt = appendNode.getOwnerDocument()
									.createAttribute("inheritfrom");
							inhreatedAtt.setValue(superbo.getName());
						}

					}
				}
			}

			if (inheritfrom) {
				
				 boDefMethod[] methds = superbo.getBoMethods();
				
				 for (short i = 0; (methds != null) && (i < methds.length); i++)
				 {
					 boDefMethod cmeth;
					 if ((cmeth = subbo.getBoMethod(methds[i].getName(), methds[i].getAssinatureClasses())) != null)
					 {
						 if (!methds[i].getIsNative())
						 {
							 if(
									 !netgest.bo.def.v2.boDefMethodImpl.compareMethodAssinature(
									 cmeth.getAssinatureClasses(),
									 methds[i].getAssinatureClasses()))
							 {
								 subbo.getChildNode("methods").getNode()
								 .appendChild(subbo.getNode()
								 .getOwnerDocument()
								 .importNode(methds[i].getNode(),true));
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

				// *** deriv OPL
				if (!interf) {
					ngtXMLHandler superOPL = superbo.getChildNode("opl");
					ngtXMLHandler subOPL = subbo.getChildNode("opl");

					if (subOPL != null) {
						ngtXMLHandler classKeysSub = subOPL
								.getChildNode("classKeys");
						if (classKeysSub == null) {
							ngtXMLHandler classKeysSuper = superOPL
									.getChildNode("classKeys");
							if (classKeysSuper != null) {
								subOPL
										.getNode()
										.appendChild(
												subOPL
														.getDocument()
														.importNode(
																classKeysSuper
																		.getNode(),
																true));
							}
						}

						ngtXMLHandler attKeysSub = subOPL
								.getChildNode("attributeKeys");
						if (attKeysSub == null) {
							ngtXMLHandler attKeysSuper = superOPL
									.getChildNode("attributeKeys");
							if (attKeysSuper != null) {
								subOPL.getNode().appendChild(
										subOPL.getDocument().importNode(
												attKeysSuper.getNode(), true));
							}
						}
					}
				}

				if (!interf) {
					ngtXMLHandler superviews = superbo.getChildNode("Viewers");
					ngtXMLHandler subviews = subbo.getChildNode("Viewers");

					if (superviews != null) {
						if (subviews == null) {
							subbo.getNode().appendChild(
									subbo.getNode().getOwnerDocument()
											.importNode(superviews.getNode(),
													true));
						} else {
							ngtXMLUtils.mergeNodes((XMLNode) superviews
									.getNode(), (XMLNode) subviews.getNode(),
									ngtXMLUtils.MERGE_BY_ATTNAME_OR_NODEINDEX);
						}
					}
				}
			}
		} catch (XSLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static synchronized final boDefHandler getUndeployedDefinitions(
							boBuildRepository repository, 
							String boname,
							Hashtable objectInterfaceMap, 
							boolean createFwdMethods) 
	{
		
		boDefHandler bobj;
	
		synchronized (p_undeployeddefs) {
			
			if ((bobj = (boDefHandler) p_undeployeddefs.get(boname)) == null) {

				File xbofile = repository.getXMLFile(boname);
				if (xbofile == null || !xbofile.exists()) {
					xbofile = repository.getFile(boname,
							boBuilder.TYPE_INTERFACE);
					if (xbofile != null && xbofile.exists()) {
						XMLDocument xmldoc = ngtXMLUtils.loadXMLFile(xbofile
								.getAbsolutePath());
						bobj = boDefHandler
								.loadInterfaceFromXml(boname, xmldoc);
					} else {
						return null;
					}
				} 
				else 
				{
					XMLDocument xmldoc = ngtXMLUtils.loadXMLFile(xbofile
							.getAbsolutePath());
					bobj = boDefHandler.loadFromXml(xmldoc);
				}

				if (bobj.getClassType() == boDefHandler.TYPE_INTERFACE
						&& !xbofile.getName().toLowerCase().endsWith(
								".xeoimodel")) {
					throw new RuntimeException(
							"Object ["
									+ boname
									+ "] in bodef-deployment as wrong extension, must be .xeoimodel not xeomodel because is declared as a interface.");
				}
				if (!bobj.getName().equalsIgnoreCase(boname)) {
					throw new RuntimeException(
							"Erro in object definition:Object filename does not match with name specified in the XML. ["
									+ boname + "!=" + bobj.getName() + "].");
				}

				fillSystemAttributes(repository, bobj, objectInterfaceMap);
				fillInterfaceAttributes(repository, bobj, objectInterfaceMap);
				fillInterfaceMethdos(repository, bobj, objectInterfaceMap);
				fillInterfaceEvents(repository, bobj, objectInterfaceMap);
				buildInterfaces(repository, bobj, objectInterfaceMap);
				if (createFwdMethods) {
					try {
						createMethodsFromFwdObjects(bobj);
					} catch (boRuntimeException e) {
						logger.severe(e);
					}
				}
				p_undeployeddefs.put(boname, bobj);
			}
		}
		return bobj;
	}

	public static final boDefHandler[] listUndeployedDefinitions(
			boBuildRepository repository, Hashtable objectInterfaceMap) {
		// String[] xfiles = eboobjdir.list();
		File[] xmlfiles = repository.getXMLFiles();
		Vector defs = new Vector();
		String xfiles;

		for (int i = 0; i < xmlfiles.length; i++) {
			xfiles = xmlfiles[i].getName();

			if (xfiles.toLowerCase().endsWith(TYPE_BO)) {
				// VERIFICAR
				boDefHandler def = getUndeployedDefinitions(repository, xfiles
						.substring(0, xfiles.toLowerCase().indexOf(TYPE_BO)),
						objectInterfaceMap, false);
				defs.add(def);
			}
		}

		boDefHandler[] ret = new boDefHandler[defs.size()];
		defs.toArray(ret);

		return ret;
	}

	public static void forceDeploy(String s) {
		try {
			File bodef = new File(p_bcfg.getDeploymentDir() + s + TYPE_BO);

			if (bodef.exists()) {
				bodef.delete();
			}
		} catch (Exception e) {
			// ignora
		}
	}

	public void cleanboDefDeployment() {
		p_builderProgress.setCurrentTaskName("Cleaning bodef-deployment....");
		p_builderProgress.setCurrentTasks(1);
		File f = new File(p_bcfg.getDeploymentDir());
		if (f.exists() && f.canWrite()) {
			delete(f.listFiles());
		}
		p_builderProgress.addCurrentTaskProgress();
	}

	public void cleanBuildClasses() {
		p_builderProgress.setCurrentTaskName("Cleaning Java Classes....");
		p_builderProgress.setCurrentTasks(1);
		File f = new File(p_bcfg.getDeploymentclassdir());
		if (f.exists() && f.canWrite()) {
			delete(f.listFiles());
		}
		p_builderProgress.addCurrentTaskProgress();
	}

	public void cleanBuildSource() {
		p_builderProgress.setCurrentTaskName("Cleaning Java Sources....");
		p_builderProgress.setCurrentTasks(1);
		File f = new File(p_bcfg.getDeploymentsrcdir());
		if (f.exists() && f.canWrite()) {
			delete(f.listFiles());
		}
		p_builderProgress.addCurrentTaskProgress();
		p_builderProgress.setCurrentTaskName("");
	}

	private static void delete(File[] files) {
		File aux;

		for (int i = 0; i < files.length; i++) {
			aux = files[i];

			if (aux.isDirectory() && !isNumber(aux.getName())) {
				delete(aux.listFiles());
			}

			try {
				aux.delete();
			} catch (Exception e) {
				// ignora
			}
		}
	}

	private static boolean isNumber(String s) {
		try {
			Long.parseLong(s);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	private static void createResources(String boname) throws IOException {
		boolean exists = true;
		String path = p_bcfg.getDeployJspDir() + File.separator + "resources"
				+ File.separator + boname;
		File resources = new File(path);

		if (!resources.exists()) {
			resources.mkdirs();
			exists = false;
		}

		File iconsDir = new File(p_bcfg.getNgtHome() + File.separator + "icons");
		if (iconsDir.exists()) {
			File[] icons = iconsDir.listFiles();
			File[] resourcesIcons = null;
			File aux;
			File aux2;

			if (exists) {
				resourcesIcons = resources.listFiles();
			}

			for (int i = 0; (icons != null) && (i < icons.length); i++) {
				aux = icons[i];
				boolean found = false;
				for (int j = 0; (resourcesIcons != null)
						&& (j < resourcesIcons.length) && !found; j++) {
					aux2 = resourcesIcons[j];

					if (aux.getName().equals(aux2.getName())) {
						found = true;
					}
				}

				if (!found) {
					copyFile(aux, new File(path + File.separator
							+ aux.getName()));
				}
			}
		}
	}

	private static void copyFile(File from, File to) throws IOException {
		byte[] b = new byte[1024 * 10];
		int numBytes = 0;

		FileOutputStream fos = null;
		FileInputStream fis = null;

		try {
			fos = new FileOutputStream(to);
			fis = new FileInputStream(from);
			for (long i = 0; (numBytes = fis.read(b)) != -1; i++) {
				fos.write(b, 0, numBytes);
			}
		} finally {
			if (fos != null) {
				fos.close();
			}

			if (fis != null) {
				fos.close();
			}
		}
	}

	private static String getObjectPackage(File objectfile) {
		String file = objectfile.getAbsolutePath();
		int lof = file.lastIndexOf(File.separator);
		String dir = objectfile.getAbsolutePath().substring(0, lof);
		String cpackage = dir.substring(dir.lastIndexOf(File.separator) + 1,
				dir.length());

		return cpackage;
	}

	public void buildWorkPlaceDefault(EboContext ctx) {
		try {
			uiObjectBuilder.buildWorkPlaceDefault(ctx);
		} catch (Exception e) {
			logger.severe(e);
		} finally {
		}
	}

	public void removeUserWorkPlaces(EboContext ctx) {
		boolean mybuild = false;
		try {
			synchronized (boBuilder.class) {
				if (boBuilder.p_running) {
					return;
				}

				boBuilder.p_running = true;
				mybuild = true;
			}

			try {
				uiObjectBuilder.removeUserWorkPlaces(ctx);
			} catch (Exception e) {
				p_builderProgress.appendErrorLog(e);
			} finally {
			}
		} finally {
			if (mybuild) {
				boBuilder.p_running = false;
			}
		}
	}

	public void createSystemUsersandGroups(EboContext ctx) {
		try {
			// Create Users SYSUSER, ROBOT and group PUBLIC

			p_builderProgress
					.appendInfoLog("Checking if System Users and Groups exist");

			boObject perf = boObject.getBoManager().loadObject(ctx, "Ebo_Perf",
					"username='SYSUSER'");
			if (!perf.exists()) {
				perf = boObject.getBoManager().createObject(ctx, "Ebo_Perf");
				perf.getAttribute("name").setValueString("Default superuser");
				perf.getAttribute("id").setValueString("SYSUSER");
				perf.getAttribute("username").setValueString("SYSUSER");
				perf.getAttribute("password").setValueString("ABC");
				perf.update();
				p_builderProgress
						.appendInfoLog("Ebo_Perf "
								+ perf.getAttribute("id").getValueString()
								+ " Created");
			} else
				p_builderProgress.appendInfoLog("Ebo_Perf "
						+ perf.getAttribute("id").getValueString()
						+ " already exists");

			perf = boObject.getBoManager().loadObject(ctx, "Ebo_Perf",
					"username='ROBOT'");
			if (!perf.exists()) {
				perf = boObject.getBoManager().createObject(ctx, "Ebo_Perf");

				perf.getAttribute("name").setValueString("ROBOT");
				perf.getAttribute("id").setValueString("ROBOT");
				perf.getAttribute("username").setValueString("ROBOT");
				perf.getAttribute("password").setValueString("ROBOT");
				perf.update();
				p_builderProgress
						.appendInfoLog("Ebo_Perf "
								+ perf.getAttribute("id").getValueString()
								+ " Created");
			} else
				p_builderProgress.appendInfoLog("Ebo_Perf "
						+ perf.getAttribute("id").getValueString()
						+ " already exists");

			boObject group = boObject.getBoManager().loadObject(ctx,
					"Ebo_Group", "name='PUBLIC'");
			if (!group.exists()) {
				group = boObject.getBoManager().createObject(ctx, "Ebo_Group");

				group.getAttribute("name").setValueString("PUBLIC");
				group.getAttribute("id").setValueString("PUBLIC");
				group.update();
				p_builderProgress.appendInfoLog("Ebo_Group "
						+ group.getAttribute("id").getValueString()
						+ " Created");
			} else
				p_builderProgress.appendInfoLog("Ebo_Group "
						+ group.getAttribute("id").getValueString()
						+ " already exists");
		} catch (boRuntimeException e) {
			p_builderProgress
					.appendInfoLog("Error creating  System Users and Groups "
							+ e.getMessage());
		}
	}

	public static boolean isRunning() {
		return p_running;
	}

	public void setPackagesDescription(EboContext eboCtx) {
		boolean mybuild = false;
		try {
			synchronized (boBuilder.class) {
				if (boBuilder.p_running) {
					return;
				}

				boBuilder.p_running = true;
				mybuild = true;
			}
			File fPackSrc = new File(eboCtx.getApplication()
					.getApplicationConfig().getDefinitiondir()
					+ "packages.xeodesc");
			File fPackDpl = new File(eboCtx.getApplication()
					.getApplicationConfig().getDeploymentDir()
					+ "packages.xeodesc");
			if (fPackSrc.exists()
					&& (!fPackDpl.exists() || fPackSrc.lastModified() > fPackDpl
							.lastModified())) {
				ngtXMLHandler packXML = new ngtXMLHandler(ngtXMLUtils
						.loadXMLFile(fPackSrc.getAbsolutePath()));
				ngtXMLHandler packagesXML = packXML.getChildNode("Packages");
				ngtXMLHandler[] packs = packagesXML.getChildNodes();
				String auxPackName;
				String auxPackDesc;
				Connection con = null;
				PreparedStatement pst = null;
				boolean ok = false;
				try {
					final InitialContext ic = new InitialContext();
					UserTransaction ut = (UserTransaction) ic
							.lookup("java:comp/UserTransaction");
					ut.setTransactionTimeout(6000000);
					con = eboCtx.getConnectionData();
					String sql = "update ebo_package set description = ? where name = ?";
					pst = con.prepareStatement(sql);
					p_builderProgress
							.appendInfoLog("Setting Packages Descriptions");
					for (int i = 0; i < packs.length; i++) {
						auxPackName = packs[i].getAttribute("name");
						auxPackDesc = packs[i].getChildNode("description")
								.getText();

						pst.setString(1, auxPackDesc);
						pst.setString(2, auxPackName);
						pst.executeUpdate();
						p_builderProgress.appendInfoLog("Package ["
								+ auxPackName + "] setting description ["
								+ auxPackDesc + "]");
					}
					pst.close();
					// 1ºdelete from ebo_references ref where ref.refboui$ in
					// (select child$ from ebo_perf$packages t where t.child$
					// not in (select boui from oebo_package) and
					// t.parent$=1068)
					// 3ºdelete from ebo_perf$packages t where t.child$ not in
					// (select boui from oebo_package)
					StringBuffer delete = new StringBuffer(
							"delete from oebo_references where refboui$ in ");
					delete
							.append(
									"(select child$ from oebo_perf$packages t where t.child$ not in ")
							.append(
									"(select boui from oebo_package)) and oebo_references.refboui$ in ")
							.append(
									"(select parent$ from ebo_perf$packages t where t.child$ not in (select boui from oebo_package))");
					pst = con.prepareStatement(delete.toString());
					pst.executeUpdate();
					pst.close();
					delete.delete(0, delete.length());
					delete
							.append("delete from ebo_perf$packages where child$ not in (select boui from oebo_package)");
					pst = con.prepareStatement(delete.toString());
					pst.executeUpdate();

					ok = true;
					// IMBR
					// con.commit();
				} catch (Exception e) {
					p_builderProgress.appendErrorLog(e);
				} finally {
					try {
						if (pst != null) {
							pst.close();
						}
						if (con != null) {
							con.close();
						}
					} catch (Exception e) {
						// ignora
					}
					if (ok) {
						eboCtx.commitContainerTransaction();
						IOUtils.copy(fPackSrc, fPackDpl);
					} else {
						p_builderProgress
								.appendInfoLog("Não foi possível efectuar os set da descrição do Packages");
						eboCtx.rollbackContainerTransaction();
					}
					eboCtx.close();
				}
			} else {
				if (fPackSrc.exists()) {
					p_builderProgress
							.appendInfoLog("Não foi necessário efectuar o set das descrições dos packages");
				} else {
					p_builderProgress
							.appendInfoLog("Não existe o ficheiro de configuração das descrições dos packages.");
				}
			}
		} catch (boRuntimeException e) {
			p_builderProgress
					.appendErrorLog("Não foi possível efectuar o set dos Packages names.");
		} finally {
			if (mybuild) {
				boBuilder.p_running = false;
			}
		}
	}

}
