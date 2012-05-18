/*Enconding=UTF-8*/
package netgest.bo.system;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import netgest.bo.boConfigRepository;
import netgest.bo.boException;
import netgest.bo.configUtils.RepositoryConfig;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.presentation.render.Browser;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.utils.XeoApplicationLanguage;
import netgest.bo.utils.XeoUserTheme;
import netgest.bo.utils.XeoUserThemeFile;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class boApplicationConfig {
	
	
	private static final Logger logger =  Logger.getLogger( boApplication.class.toString() );
		//Logger.getLogger(boApplicationConfig.class); 
		//O logger do XEO não pode ser usado no boCondig.

	static {
		ConsoleHandler h = new ConsoleHandler();
		h.setLevel( Level.CONFIG );
		logger.setLevel( Level.CONFIG );
		logger.addHandler( h ); // A a default logger to the console, for config messages;
	}
	
	private XMLDocument xmldoc = null;
	private String p_definitiondir;
	private String p_uiDefinitiondir;
	private String p_libdir;
	private String p_deploymentclassdir;
	private String p_moduleswebdir;
	private String p_modulesdir;
	private String p_deploymentsrcdir;
	private String p_deploymentdir;
	private String p_compilerdir;
	private String p_templatesdir;
	private String p_deployjspdir;
	private Hashtable p_repositorys;
	private String p_defaultrep;
	private String p_ngthome;
	private String p_cfgFile;
	private String webContextRoot;
	private String p_aspMode = "no";
	private String p_encoding = null; // A function determines the System
										// default encoding
	private boolean p_developerMode = true;

	/**
	 * The class that implements access to the SYS datasource
	 */
	private String p_sysDataSourceName;
	
	/**
	 * The class that implements access to the DATA datasource 
	 */
	private String p_dataDataSourceName;
	
	// Conversor de documentos
	public String p_macrosws = "";
	public String p_convertws = "";
	public String p_importws = "";

	// browser
	private String p_browserName = "IE";
	private String p_browserDir = "ie";
	private int p_browserCode = -1;
	private String p_browserTheme = "0";
	private ngtXMLHandler[] p_defaultWorkplaces = null;
	private ngtXMLHandler[] p_XEOProfiles = null;

	private Properties p_wordTemplateProp;
	private Properties p_win32ClientProp;

	// languages
	
	private Properties prop;//used to store the application language
	/**
	 * Keeps all languages of the application
	 */
	private HashSet<XeoApplicationLanguage> p_languages=new HashSet<XeoApplicationLanguage>();
	
	/**
	 * Keeps all themes of the application
	 */
	private Map<String,XeoUserTheme> p_themes = new HashMap<String, XeoUserTheme>();
	
	/**
	 * The default theme
	 */
	private XeoUserTheme p_defaultTheme = null;

	private String[] p_threadsName;
	private String[] p_threadsClass;
	private String[] p_threadsEjbName;
	private String[] p_threadsInterval;

	private String p_threadsType = "USERTHREADS";

	private String p_cscriptPath = null;
	private String p_vbprogPath = null;

	private Properties[] p_repositories = null;

	private boApplicationLoggerConfig[] p_loggerConfig = null;

	// ECM Repositores
	/**
	 * The configuration for all repositories
	 */
	private HashMap<String, RepositoryConfig> p_ecmRepositories;

	// deploy de esquemas
	private String p_tablespace;

	public XMLDocument getXmldoc() {
		return xmldoc;
	}

	// ////////////////////////////////
	/**
	 * @return (String)the used language
	 */
	
	
	public String getLanguage() {
		//boSessionUser bo= boApplication.currentContext().getEboContext().getBoSession().getUser();		
		String ret = prop.getProperty("language");
		return ret;
	}
	/**
	 * 
	 * @return (HashSet)all available languages
	 */
	public HashSet<String> getAvailableLanguages(){
		HashSet<String> hs=new HashSet<String>();
		XeoApplicationLanguage applL;
		Iterator it=p_languages.iterator();
		while(it.hasNext()){
			applL=(XeoApplicationLanguage) it.next();
			hs.add((String) applL.getCode());
		}
		return hs;
	}
	public HashSet<XeoApplicationLanguage> getAllLanguages(){
		return p_languages;
	}
	
	/**
	 * 
	 * Retrieves all application themes
	 * 
	 * @return
	 */
	public Map<String,XeoUserTheme> getThemes(){
		return p_themes;
	}
	
	public XeoUserTheme getDefaultTheme(){
		return p_defaultTheme;
	}
	
	/////////////////////////
	
	
	public boApplicationConfig(String home) {
		p_ecmRepositories = new HashMap<String, RepositoryConfig>();
		refresh(home);
	}

	public String getDefinitiondir() {
		return p_definitiondir;
	}

	public String getUiDefinitiondir() {
		return p_uiDefinitiondir;
	}

	public String getDeployJspDir() {
		return p_deployjspdir;
	}

	public String getDeploymentclassdir() {
		return p_deploymentclassdir;
	}

	public String getModuleWebBaseDir() {
		return p_moduleswebdir;
	}

	public String getModulesDir() {
		return p_modulesdir;
	}

	public String getCompilerdir() {
		return p_compilerdir;
	}

	public String getLibDir() {
		if (p_libdir.startsWith("."))
			p_libdir = p_ngthome + p_libdir;
		return p_libdir;
	}

	public boolean isDeveloperMode() {
		return p_developerMode;
	}

	public String getDeploymentsrcdir() {
		return p_deploymentsrcdir;
	}

	public String getDeploymentDir() {
		return p_deploymentdir;
	}

	public String getNgtHome() {
		return p_ngthome;
	}

	public String getTemplatesDir() {
		return p_templatesdir;
	}

	public boConfigRepository getConfigRepository(String name) {
		return (boConfigRepository) p_repositorys.get(name);
	}

	public String getDefaultRepository() {
		return p_defaultrep;
	}

	public boolean aspmodeOn() {
		return "yes".equalsIgnoreCase(p_aspMode)
				|| "y".equalsIgnoreCase(p_aspMode);
	}

	public String getBrowserName() throws boRuntimeException {
		return Browser.getBrowserName(p_browserCode);
	}

	public String getVBProgPath() {
		return p_vbprogPath;
	}

	public String getCScriptPath() {
		return p_cscriptPath;
	}

	/**
	 * 
	 * Retrieves an ECM Repository Configuration given its name
	 * 
	 * @param name
	 *            The name of the repository
	 * 
	 * @return A {@link RepositoryConfig} instance with the configurations for
	 *         the repository
	 */
	public RepositoryConfig getFileRepositoryConfiguration(String name) {
		return p_ecmRepositories.get(name);
	}

	/**
	 * 
	 * Retrieves a list with all the (registered) ECM Repository names
	 * 
	 * @return A list with all the ECM Repository Names registered in the
	 *         application
	 */
	public List<String> getFileRepositoryNames() {
		return new ArrayList<String>(p_ecmRepositories.keySet());
	}

	/**
	 * 
	 * Retrieves the default repository configuration
	 * 
	 * @return A reference to the default repository configuration or null if no
	 *         repository configurations exist
	 * 
	 */
	public RepositoryConfig getDefaultFileRepositoryConfiguration() {
		if (p_ecmRepositories != null) {
			Iterator<String> it = this.p_ecmRepositories.keySet().iterator();
			while (it.hasNext()) {
				String repositoryName = (String) it.next();
				RepositoryConfig current = p_ecmRepositories
						.get(repositoryName);
				if (current.isDefault())
					return current;
			}
		}

		return null;
	}

	public String getBrowserDirPrefix() throws boRuntimeException {
		return p_browserDir;
	}

	public String getBrowserTheme() throws boRuntimeException {
		return p_browserTheme;
	}

	public int getBrowserCode() {
		return p_browserCode;
	}

	public String getSystemEncoding() {
		String encoding = null;
		try {
			FileWriter fw = new FileWriter("out");
			encoding = fw.getEncoding();
			fw.close();
			Charset c = Charset.forName(encoding);
			encoding = c.name();
		} catch (IOException e) {
			// ignore
		}
		return encoding;
	}

	public String getEncoding() {
		if (p_encoding == null) {
			p_encoding = getSystemEncoding();
		}
		return p_encoding;
	}

	public String getTableSpace() {
		return p_tablespace;
	}

	public void refresh() {
		if (p_cfgFile == null) {
			throw new RuntimeException(MessageLocalizer.getMessage("ERROR_CANNOT_RELOAD_A_UNINITIALIZED_CONFIG"));
		}
		refresh(p_cfgFile);
	}

	public void refresh(String configFile) {
		// InicializaÃ§Ã£o do Objecto

		p_repositorys = new Hashtable();

		p_cfgFile = configFile;

		File config = new File(configFile);

		p_ngthome = config.getParent();

		p_ngthome = fixRelativePath("", p_ngthome);

		try {
			xmldoc = ngtXMLUtils.loadXMLFile(configFile);
		} catch (RuntimeException e) {
			throw e;
		}

		try {

			XMLNode xnode = (XMLNode) xmldoc.selectSingleNode("//logConfig");

			if (xnode != null) {
				parseLogConfig((XMLElement) xnode);
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//definitiondir");
			if (xnode != null) {
				p_definitiondir = getNodeText(xnode);
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//uidefinitiondir");

			if (xnode != null) {
				p_uiDefinitiondir = getNodeText(xnode);
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//developerMode");

			if (xnode != null) {
				p_developerMode = new Boolean(getNodeText(xnode))
						.booleanValue();
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//aspmode");

			if (xnode != null) {
				p_aspMode = (getNodeText(xnode) != null) ? getNodeText(xnode)
						: "no";
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//encoding");

			if (xnode != null) {
				p_encoding = getNodeText(xnode);
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//webcontextroot");

			if (xnode != null) {
				webContextRoot = getNodeText(xnode);
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//threads");
			if (xnode != null) {
				// get implementation type

				NamedNodeMap threadsAtt = xnode.getAttributes();
				if (threadsAtt != null
						&& threadsAtt.getNamedItem("type") != null) {
					p_threadsType = threadsAtt.getNamedItem("type")
							.getNodeValue();
				}

				NodeList x = xnode.getChildNodes();
				ArrayList<String> threadsName = new ArrayList<String>();
				ArrayList threadsClass = new ArrayList();
				ArrayList threadsEjbName = new ArrayList();
				ArrayList threadsInterval = new ArrayList();
				if (x != null) {
					for (int i = 0; i < x.getLength(); i++) {
						Node t = x.item(i);
						NamedNodeMap attrs = t.getAttributes();
						if (attrs != null) {
							if (attrs.getNamedItem("name") != null
									&& attrs.getNamedItem("class") != null) {
								threadsName.add(attrs.getNamedItem("name")
										.getNodeValue());
								threadsClass.add(attrs.getNamedItem("class")
										.getNodeValue());
							}
							if (attrs.getNamedItem("ejb-name") != null) {
								threadsEjbName.add(attrs.getNamedItem(
										"ejb-name").getNodeValue());
							} else
								threadsEjbName.add(null);

							if (attrs.getNamedItem("interval") != null) {
								threadsInterval.add(attrs.getNamedItem(
										"interval").getNodeValue());
							} else
								threadsInterval.add(null);
						}

					}
					p_threadsName = (String[]) threadsName
							.toArray(new String[threadsName.size()]);
					p_threadsClass = (String[]) threadsClass
							.toArray(new String[threadsClass.size()]);
					p_threadsEjbName = (String[]) threadsEjbName
							.toArray(new String[threadsEjbName.size()]);
					p_threadsInterval = (String[]) threadsInterval
							.toArray(new String[threadsInterval.size()]);
				}
			} else {
				p_threadsName = new String[0];
				p_threadsClass = new String[0];
				p_threadsEjbName = new String[0];
				p_threadsInterval = new String[0];
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//deployment");

			if (xnode != null) {
				p_deploymentclassdir = (xnode.selectSingleNode("class_dir") != null) ? getNodeText(xnode
						.selectSingleNode("class_dir"))
						: "";

				p_moduleswebdir = (xnode.selectSingleNode("obj_moduleswebdir") != null) ? getNodeText(xnode
						.selectSingleNode("obj_moduleswebdir"))
						: "";

				p_modulesdir = (xnode.selectSingleNode("obj_modulesdir") != null) ? getNodeText(xnode
						.selectSingleNode("obj_modulesdir"))
						: "";

				p_deploymentsrcdir = (xnode.selectSingleNode("src_dir") != null) ? getNodeText(xnode
						.selectSingleNode("src_dir"))
						: "";
				p_deploymentdir = (xnode.selectSingleNode("obj_dir") != null) ? getNodeText(xnode
						.selectSingleNode("obj_dir"))
						: "";
				p_compilerdir = (xnode.selectSingleNode("obj_compiler") != null) ? getNodeText(xnode
						.selectSingleNode("obj_compiler"))
						: "";
				p_templatesdir = (xnode.selectSingleNode("obj_templates") != null) ? getNodeText(xnode
						.selectSingleNode("obj_templates"))
						: "";
				p_deployjspdir = (xnode.selectSingleNode("obj_deployjspdir") != null) ? getNodeText(xnode
						.selectSingleNode("obj_deployjspdir"))
						: "";

				p_libdir = (xnode.selectSingleNode("lib_dir") != null) ? getNodeText(xnode
						.selectSingleNode("lib_dir"))
						: (p_ngthome + "lib");

				p_tablespace = (xnode.selectSingleNode("tablespace") != null) ? getNodeText(xnode
						.selectSingleNode("tablespace"))
						: "";
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//browser");

			if (xnode != null) {
				p_browserName = (xnode.selectSingleNode("name") != null) ? getNodeText(xnode
						.selectSingleNode("name"))
						: p_browserName;
				p_browserDir = (xnode.selectSingleNode("dir_prefix") != null) ? getNodeText(xnode
						.selectSingleNode("dir_prefix"))
						: p_browserDir;
				p_browserCode = Browser.getBrowserCode(p_browserName);
				p_browserTheme = (xnode.selectSingleNode("theme") != null) ? getNodeText(xnode
						.selectSingleNode("theme"))
						: p_browserTheme;
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//tratadoc");

			if (xnode != null) {
				p_cscriptPath = (xnode.selectSingleNode("cscriptPath") != null) ? getNodeText(xnode
						.selectSingleNode("cscriptPath"))
						: p_cscriptPath;
				p_vbprogPath = (xnode.selectSingleNode("vbprogPath") != null) ? getNodeText(xnode
						.selectSingleNode("vbprogPath"))
						: p_vbprogPath;
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//GDWebServices");
			if (xnode != null) {
				p_macrosws = (xnode.selectSingleNode("WordMacroWSEndPoint") != null) ? getNodeText(xnode
						.selectSingleNode("WordMacroWSEndPoint"))
						: p_macrosws;
				p_convertws = (xnode
						.selectSingleNode("ConvertImagesWSEndPoint") != null) ? getNodeText(xnode
						.selectSingleNode("ConvertImagesWSEndPoint"))
						: p_convertws;
				p_importws = (xnode
						.selectSingleNode("IndexImageImportWSEndPoint") != null) ? getNodeText(xnode
						.selectSingleNode("IndexImageImportWSEndPoint"))
						: p_importws;
			}

			xnode = (XMLNode) xmldoc.selectSingleNode("//Repositories");

			if (xnode != null) {
				NodeList nreps = xnode.getChildNodes();
				NodeList nrep;
				Node rep = null;
				String repName = null;
				NamedNodeMap nnm = null;
				boolean isDefault = false;
				String schemaName = null;

				for (byte i = 0; i < nreps.getLength(); i++) {
					rep = nreps.item(i);

					if ("Repository".equalsIgnoreCase(rep.getNodeName())) {
						isDefault = false;
						nrep = rep.getChildNodes();

						for (byte k = 0; k < nrep.getLength(); k++) {
							if ("Name".equalsIgnoreCase(nrep.item(k)
									.getNodeName())) {
								repName = getNodeText(nrep.item(k));

								if ("default".equalsIgnoreCase(repName)) {
									isDefault = true;
								}
							} else if ("Schema".equalsIgnoreCase(nrep.item(k)
									.getNodeName())) {
								schemaName = getNodeText(nrep.item(k));
							}
						}

						if (p_defaultrep == null) {
							p_defaultrep = schemaName;
						}

						p_repositorys.put(repName, new boConfigRepository(
								repName, rep));
					}
				}
			}

			// Word Template
			p_wordTemplateProp = new Properties();
			NodeList nodelist = xmldoc.selectNodes("//bo-config/wordTemplate");

			for (int i = 0; i < nodelist.getLength(); i++) {
				XMLNode node = (XMLNode) nodelist.item(i);
				XMLNode nextnode = (XMLNode) node.getFirstChild();

				do {
					for (int z = 0; z < nextnode.getChildNodes().getLength(); z++) {
						p_wordTemplateProp.setProperty(nextnode.getNodeName(),
								nextnode.getText());
					}
				} while ((nextnode = (XMLNode) nextnode.getNextSibling()) != null);
			}

			// Client 32 Properties
			p_win32ClientProp = new Properties();
			nodelist = xmldoc.selectNodes("//bo-config/win32Client");
			if (nodelist != null) {
				for (int i = 0; i < nodelist.getLength(); i++) {
					XMLNode node = (XMLNode) nodelist.item(i);
					XMLNode nextnode = (XMLNode) node.getFirstChild();

					do {
						for (int z = 0; z < nextnode.getChildNodes()
								.getLength(); z++) {
							p_win32ClientProp.setProperty(nextnode
									.getNodeName(), nextnode.getText());
						}
					} while ((nextnode = (XMLNode) nextnode.getNextSibling()) != null);
				}
			}

			p_win32ClientProp.put("path", fixRelativePath(p_ngthome,
					p_win32ClientProp.getProperty("path")));
			p_wordTemplateProp.put("path", fixRelativePath(p_ngthome,
					p_wordTemplateProp.getProperty("path")));

			p_definitiondir = fixRelativePath(p_ngthome, p_definitiondir);
			p_uiDefinitiondir = fixRelativePath(p_ngthome, p_uiDefinitiondir);
			p_deploymentclassdir = fixRelativePath(p_ngthome,
					p_deploymentclassdir);
			p_deploymentsrcdir = fixRelativePath(p_ngthome, p_deploymentsrcdir);
			p_deploymentdir = fixRelativePath(p_ngthome, p_deploymentdir);
			p_compilerdir = fixRelativePath(p_ngthome, p_compilerdir, false);
			p_templatesdir = fixRelativePath(p_ngthome, p_templatesdir);
			p_deployjspdir = fixRelativePath(p_ngthome, p_deployjspdir);
			p_moduleswebdir = fixRelativePath(p_ngthome, p_moduleswebdir);
			if (p_modulesdir.length() > 0)
				p_modulesdir = fixRelativePath(p_ngthome, p_modulesdir);
			else
				p_modulesdir = p_moduleswebdir + ".." + File.separator
						+ "modules" + File.separator;

			// ECM Repositories
			nodelist = xmldoc.selectNodes("//bo-config//ecmRepository");
			if (nodelist != null) {
				for (int i = 0; i < nodelist.getLength(); i++) {
					XMLNode node = (XMLNode) nodelist.item(i);
					// Debug
					StringBuffer buf = new StringBuffer();
					ngtXMLUtils.print(node, buf);
					// End debug
					ngtXMLHandler handler = new ngtXMLHandler(node);
					RepositoryConfig currentConfig = new RepositoryConfig(
							handler);
					this.p_ecmRepositories.put(currentConfig.getName(),
							currentConfig);
				}
			}

			///////// -------------------------------------------------------
			// -------------------------------------------------------
			// Gets the application language and all available languages
			 
			prop = new Properties();
			NodeList nodeList;
			if (xmldoc.selectNodes("//languages")!=null){
				NodeList nodeL = xmldoc.selectNodes("//languages");
				XMLNode nodex = (XMLNode) nodeL.item(0);
				if (nodex==null){
					logger.config("The language should be configured in boconfig.xml! Assuming PT as default");
					prop.put("language", "PT");
				}
				else{
				XMLNode nextNode = (XMLNode) nodex.getFirstChild();	
				prop.put("language", nextNode.getText());
				
				nodeL = (NodeList) xmldoc.selectNodes("//bo-config/languages");
				Node node = (XMLNode) nodeL.item(0);
				nodex = (XMLNode) node.getFirstChild();			
				nodeL = node.getChildNodes();
				node = nodeL.item(1);			
				nodeL = (NodeList) node.getChildNodes();
				for (int i = 0; i < nodeL.getLength(); i++) {	
					
					nodex = (XMLNode) nodeL.item(i);
					nodeList = nodex.getChildNodes();
					nodex=(XMLNode) nodeList.item(0);
					String code=nodex.getText();
					nextNode=(XMLNode) nodeList.item(1);
					String description=nextNode.getText();
					XeoApplicationLanguage apl=new XeoApplicationLanguage(code,description);
					Iterator itter= p_languages.iterator();
					
					boolean in=false;
					while (itter.hasNext()){
						XeoApplicationLanguage xeoAL=(XeoApplicationLanguage) itter.next();					
						if (xeoAL.getCode().equals(apl.getCode()))
							in=true;
					}
						if(in==false)			
					p_languages.add(apl);
					
				}}}
			else{
				prop.put("language", "PT");		
				}
			// ----------------------------------------------------------
			// -------------------------------------------------------
			// -------------------------------------------------------
			
			
			//Start processing the available themes
			/*
			 * <themes>
			 *   <theme name='gray' description='Gray Theme'>
	    	 *	   <files>
	    	 *		  <file path='resources/css/xtheme-gray.css' description='Gray Theme' id='css_gray'></file>
	    	 *	   </files>
	    	 *	  </theme>
			 * </themes>
			 * 
			 * */
			if (xmldoc.selectNodes("//themes")!=null){
				NodeList nodeL = xmldoc.selectNodes("//themes");
				if( nodeL.getLength() > 0 ) {
					NodeList children = nodeL.item(0).getChildNodes();
					for (int i = 0; i < children.getLength(); i++) {
						
						XMLElement currentTheme = (XMLElement) children.item(i);
						String themeName = currentTheme.getAttribute("name");
						String themeDescription = currentTheme.getAttribute("description");
						String themeIsActive = currentTheme.getAttribute("default");
						boolean themeIsActiveBol = false;
						if (themeIsActive != null)
							themeIsActiveBol = Boolean.parseBoolean(themeIsActive);
						
						NodeList nl = currentTheme.getChildNodes();
						XeoUserThemeFile[] filesToInclude = new XeoUserThemeFile[0];
						if (nl != null){
							XMLElement files = (XMLElement) nl.item(0);
							if (files != null){
								NodeList invidiualFiles = files.getChildNodes();
								filesToInclude = new XeoUserThemeFile[invidiualFiles.getLength()];
								for (int k = 0; k < invidiualFiles.getLength(); k++){
									
									//<file path='' description='' id=''></file>
									XMLElement fileInclude = (XMLElement) invidiualFiles.item(k);
									String path = fileInclude.getAttribute("path");
									String description = fileInclude.getAttribute("description");
									String id = fileInclude.getAttribute("id");
									XeoUserThemeFile finalFile = new XeoUserThemeFile(path, description, id);
									filesToInclude[k] = finalFile;
								}
							}
						}
						
						XeoUserTheme theme = new XeoUserTheme(themeName, themeDescription, themeIsActiveBol, filesToInclude);
						p_themes.put(theme.getName(), theme);
						if (themeIsActiveBol)
							p_defaultTheme = theme;
					}
				}
			}
			
			//End of processing the themes
			
			//Process the DataSources
			XMLElement root = (XMLElement) xmldoc.getDocumentElement();
			XMLElement nds = (XMLElement) root.selectSingleNode("DataSources");
			NodeList ldrivers = nds.selectNodes("DataSource");
            int nd = ldrivers.getLength();
            
            for (int i = 0; i < nd; i++)
            {
                XMLElement xnodeDs = (XMLElement) ldrivers.item(i);
                String name = xnodeDs.getAttribute("name");
                if (name.equalsIgnoreCase("SYS"))
                	p_sysDataSourceName = xnodeDs.selectSingleNode("Driver").
                		getFirstChild().getNodeValue();
                if (name.equalsIgnoreCase("DATA"))
                	p_dataDataSourceName = xnodeDs.selectSingleNode("Driver").
                		getFirstChild().getNodeValue();
            }

		} catch (Exception e) {
			String[] emsg = { configFile };
			throw new boException("netgest.bo.builder._init()", "BO-1202", e,
					emsg);
		}
	}

	public Properties getAuthentication() {

		try {
			Properties retprop = new Properties();
			NodeList nodelist = xmldoc
					.selectNodes("//bo-config/authentication");

			for (int i = 0; i < nodelist.getLength(); i++) {
				XMLNode node = (XMLNode) nodelist.item(i);
				XMLNode nextnode = (XMLNode) node.getFirstChild();

				do {
					for (int z = 0; z < nextnode.getChildNodes().getLength(); z++) {
						retprop.setProperty(nextnode.getNodeName(), nextnode
								.getText());
					}
				} while ((nextnode = (XMLNode) nextnode.getNextSibling()) != null);
			}

			return retprop;
		} catch (XSLException e) {
			return null;
		}
	}

	public Properties getMailConfig() {

		try {
			Properties retprop = new Properties();
			NodeList nodelist = xmldoc.selectNodes("//bo-config/mail");

			for (int i = 0; i < nodelist.getLength(); i++) {
				XMLNode node = (XMLNode) nodelist.item(i);
				XMLNode nextnode = (XMLNode) node.getFirstChild();

				do {
					for (int z = 0; z < nextnode.getChildNodes().getLength(); z++) {
						retprop.setProperty(nextnode.getNodeName(), nextnode
								.getText());
					}
				} while ((nextnode = (XMLNode) nextnode.getNextSibling()) != null);
			}

			return retprop;
		} catch (XSLException e) {
			return null;
		}
	}

	public Properties getFaxConfig() {

		try {
			Properties retprop = new Properties();
			NodeList nodelist = xmldoc.selectNodes("//bo-config/fax");

			for (int i = 0; i < nodelist.getLength(); i++) {
				XMLNode node = (XMLNode) nodelist.item(i);
				XMLNode nextnode = (XMLNode) node.getFirstChild();

				do {
					for (int z = 0; z < nextnode.getChildNodes().getLength(); z++) {
						retprop.setProperty(nextnode.getNodeName(), nextnode
								.getText());
					}
				} while ((nextnode = (XMLNode) nextnode.getNextSibling()) != null);
			}

			return retprop;
		} catch (XSLException e) {
			return null;
		}
	}

	public Properties getSecurityConfig() {

		try {
			Properties retprop = new Properties();
			NodeList nodelist = xmldoc.selectNodes("//bo-config/security");

			for (int i = 0; i < nodelist.getLength(); i++) {
				XMLNode node = (XMLNode) nodelist.item(i);
				XMLNode nextnode = (XMLNode) node.getFirstChild();

				do {
					for (int z = 0; z < nextnode.getChildNodes().getLength(); z++) {
						retprop.setProperty(nextnode.getNodeName(), nextnode
								.getText());
					}
				} while ((nextnode = (XMLNode) nextnode.getNextSibling()) != null);
			}

			return retprop;
		} catch (XSLException e) {
			return null;
		}
	}

	public Properties getContentMngmConfig() {

		try {
			Properties retprop = new Properties();
			NodeList nodelist = xmldoc
					.selectNodes("//bo-config/Content_Manager");

			for (int i = 0; i < nodelist.getLength(); i++) {
				XMLNode node = (XMLNode) nodelist.item(i);
				XMLNode nextnode = (XMLNode) node.getFirstChild();

				do {
					for (int z = 0; z < nextnode.getChildNodes().getLength(); z++) {
						retprop.setProperty(nextnode.getNodeName(), nextnode
								.getText());
					}
				} while ((nextnode = (XMLNode) nextnode.getNextSibling()) != null);
			}

			return retprop;
		} catch (XSLException e) {
			return null;
		}
	}

	public Properties getDocumentationConfig() {

		try {
			Properties retprop = new Properties();
			NodeList nodelist = xmldoc.selectNodes("//bo-config/documentation");

			for (int i = 0; i < nodelist.getLength(); i++) {
				XMLNode node = (XMLNode) nodelist.item(i);
				XMLNode nextnode = (XMLNode) node.getFirstChild();

				do {
					for (int z = 0; z < nextnode.getChildNodes().getLength(); z++) {
						retprop.setProperty(nextnode.getNodeName(), nextnode
								.getText());
					}
				} while ((nextnode = (XMLNode) nextnode.getNextSibling()) != null);
			}

			return retprop;
		} catch (XSLException e) {
			return null;
		}
	}

	public Properties getWin32ClientConfig() {
		return p_win32ClientProp;
	}

	public String getWebContextRoot() {

		return webContextRoot;
	}

	public Properties[] getRepositories() {
		if (p_repositories == null) {
			try {
				NodeList repnodelist = xmldoc
						.selectNodes("//bo-config/Repositories");
				Properties[] ret = new Properties[repnodelist.item(0)
						.getChildNodes().getLength()];
				for (int k = 0; k < repnodelist.getLength(); k++) {
					NodeList nodelist = repnodelist.item(k).getChildNodes();
					Properties retprop = null;
					for (int i = 0; i < nodelist.getLength(); i++) {
						retprop = new Properties();
						XMLNode node = (XMLNode) nodelist.item(i);
						XMLNode nextnode = (XMLNode) node.getFirstChild();
						do {
							for (int z = 0; z < nextnode.getChildNodes()
									.getLength(); z++) {
								retprop.setProperty(nextnode.getNodeName(),
										nextnode.getText());
							}
						} while ((nextnode = (XMLNode) nextnode
								.getNextSibling()) != null);
						ret[i] = retprop;
					}
				}
				p_repositories = removeDuplicated(ret);
			} catch (XSLException e) {
				return null;
			}
		}
		return p_repositories;
	}

	public Properties getWordTemplateConfig() {
		return p_wordTemplateProp;
	}

	public String[] getThreadsName() {
		return p_threadsName;
	}

	public String[] getThreadsClass() {
		return p_threadsClass;
	}

	public String[] getThreadsInterval() {
		return p_threadsInterval;
	}

	public String[] getThreadsEjbName() {
		return p_threadsEjbName;
	}

	public String getThreadsType() {
		return p_threadsType;
	}

	public boApplicationLoggerConfig[] getLoggersConfig() {
		return p_loggerConfig;
	}

	private static final Properties[] removeDuplicated(Properties[] ret) {
		if (ret == null)
			return ret;
		ArrayList dupl = new ArrayList();
		ArrayList objToRet = new ArrayList();
		Properties[] toRet;

		for (int i = 0; i < ret.length; i++) {
			if (dupl.indexOf(ret[i].getProperty("Name")) < 0) {
				dupl.add(ret[i].getProperty("Name"));
				objToRet.add(ret[i]);
			}
		}
		toRet = new Properties[objToRet.size()];
		for (int i = 0; i < objToRet.size(); i++) {
			toRet[i] = (Properties) objToRet.get(i);
		}
		return toRet;
	}

	public ngtXMLHandler[] getWorkPlaces() {
		ArrayList wkplcs = new ArrayList();
		File f = new File(p_uiDefinitiondir);
		File[] files = f.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().toUpperCase().endsWith("$WORKPLACE.XML")) {
				ngtXMLHandler wkpXML = new ngtXMLHandler(ngtXMLUtils
						.loadXMLFile(files[i].getAbsolutePath()));
				String xml = ngtXMLUtils.getXML(wkpXML.getDocument());
				wkplcs.add(wkpXML);
			}
		}
		p_defaultWorkplaces = (ngtXMLHandler[]) wkplcs
				.toArray(new ngtXMLHandler[wkplcs.size()]);
		return p_defaultWorkplaces;
	}

	public ngtXMLHandler[] getProfiles() {
		ArrayList wkplcs = new ArrayList();
		File f = new File(p_uiDefinitiondir);
		File[] files = f.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".xeoprofiles")) {
				ngtXMLHandler wkpXML = new ngtXMLHandler(ngtXMLUtils
						.loadXMLFile(files[i].getAbsolutePath()));
				String xml = ngtXMLUtils.getXML(wkpXML.getDocument());
				wkplcs.add(wkpXML);
			}
		}
		p_XEOProfiles = (ngtXMLHandler[]) wkplcs
				.toArray(new ngtXMLHandler[wkplcs.size()]);
		return p_XEOProfiles;
	}

	public XMLElement getDataSourcesNode() {
		try {
			XMLElement root = (XMLElement) xmldoc.getDocumentElement();
			XMLElement nds = (XMLElement) root.selectSingleNode("DataSources");
			return nds;
		} catch (XSLException e) {
			throw new RuntimeException(e);
		}
	}

	public static final String fixRelativePath(String base, String path) {
		return fixRelativePath(base, path, true);
	}

	public static final String fixRelativePath(String base, String path,
			boolean appendSeparator) {
		if (path != null) {
			path = path.replace('\\', File.separatorChar);
			path = path.replace('/', File.separatorChar);

			if (!Pattern.matches("^((.\\:)|(\\/))(.*)$", path)) {
				if (path.startsWith(".\\") || path.startsWith("./")) {
					path = path.substring(2);
				}
				path = base + path;
			}

			if (appendSeparator) {
				if (!(path.endsWith("/") || path.endsWith("/") || path
						.endsWith(File.separator))) {
					path += File.separator;
				}
			}
		}
		return path;
	}

	private static final String getNodeText(Node node) {
		return ((XMLNode) node).getText();
	}

	private void parseLogConfig(XMLElement logConfig) {

		NodeList loggers = logConfig.getElementsByTagName("logger");
		ArrayList loggersArray = new ArrayList();
		for (int i = 0; i < loggers.getLength(); i++) {
			XMLElement logger = (XMLElement) loggers.item(i);

			String[] forPackages = logger.getAttribute("for").split(",");
			for (int k = 0; k < forPackages.length; k++) {

				String forPackage = forPackages[k];

				boApplicationLoggerConfig loggerConfig = new boApplicationLoggerConfig();
				loggerConfig.setActive(Boolean.parseBoolean(logger
						.getAttribute("active")));
				loggerConfig.setForClasses(forPackage);
				loggerConfig.setLevel(logger.getAttribute("level"));
				loggerConfig.setPattern("%d %5p [%t] (%F:%L) - %m%n");
				NodeList loggerAppenders = logger.getChildNodes();

				for (int z = 0; z < loggerAppenders.getLength(); z++) {
					if (loggerAppenders.item(z).getNodeType() == Node.ELEMENT_NODE) {

						Element logAppender = (Element) loggerAppenders.item(z);
						String logAppName = logAppender.getNodeName();
						if ("console".equals(logAppName)) {

							boApplicationLoggerConfig.ConsoleProperties capp = new boApplicationLoggerConfig.ConsoleProperties();
							capp.setActive(Boolean.parseBoolean(logAppender
									.getAttribute("active")));
							loggerConfig.setConsoleProperties(capp);

						} else if ("file".equals(logAppName)) {
							boApplicationLoggerConfig.FileProperties capp = new boApplicationLoggerConfig.FileProperties();

							capp.setActive(Boolean.parseBoolean(logAppender
									.getAttribute("active")));
							capp
									.setLogFile(fixRelativePath(
											p_ngthome,
											logAppender.getAttribute("logFile"),
											false));

							capp
									.setMaxSize(logAppender
											.getAttribute("maxSize"));
							capp.setHistoryFiles(Integer.parseInt(logAppender
									.getAttribute("backupFiles")));
							capp.setLogStandardOutput(logAppender
									.getAttribute("logStandardOutput"));
							capp.setLogErrorOutput(logAppender
									.getAttribute("logErrorOutput"));
							loggerConfig.setFileProperties(capp);
						} else if ("email".equals(logAppName)) {
							boApplicationLoggerConfig.EmailProperties capp = new boApplicationLoggerConfig.EmailProperties();
							capp.setActive(Boolean.parseBoolean(logAppender
									.getAttribute("active")));
							capp.setBuffer(Integer.parseInt(logAppender
									.getAttribute("buffer")));
							capp.setFrom(logAppender.getAttribute("from"));
							capp.setSmtpHost(logAppender
									.getAttribute("smtpHost"));
							capp
									.setSubject(logAppender
											.getAttribute("subject"));
							capp.setTo(logAppender.getAttribute("to"));
							loggerConfig.setEmailProperties(capp);
						}
					}
				}
				loggersArray.add(loggerConfig);
			}
		}
		p_loggerConfig = (boApplicationLoggerConfig[]) loggersArray
				.toArray(new boApplicationLoggerConfig[loggersArray.size()]);
	}

	/**
	 * 
	 * Retrieves the class name for the SYS Data Source
	 * 
	 * 
	 * @return The name of the class that implements access to the SYS
	 * data source
	 */
	public String getSysDataSourceClassName(){
		return p_sysDataSourceName;
	}
	
	/**
	 * 
	 * Retrieves the class name for the DATA Data Source
	 * 
	 * @return The name of the class that implements access to the DATA
	 * data source
	 */
	public String getDataDataSourceClassName(){
		return p_dataDataSourceName;
	}
	
	public String getConvertImagesEndPoint() {
		return p_convertws;
	}
}
