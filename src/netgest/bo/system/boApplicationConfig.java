/*Enconding=UTF-8*/
package netgest.bo.system;

import java.io.*;

import java.nio.charset.*;

import java.util.*;

import java.util.regex.Pattern;
import netgest.bo.presentation.render.*;
import netgest.bo.runtime.*;
import netgest.bo.*;

import netgest.utils.*;

import oracle.xml.parser.v2.*;

import netgest.bo.system.Logger;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
//import netgest.system.ngtconfig;


public class boApplicationConfig
{
    private XMLDocument xmldoc          = null;
    private String p_definitiondir;
    private String p_uiDefinitiondir;
    private String p_libdir;
    private String p_deploymentclassdir;
    private String p_moduleswebdir;
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
    private String p_aspMode            = "no";
    private String p_encoding           = null; // A function determines the System default encoding
    private boolean p_developerMode=true;    

    //browser
    private String p_browserName                = "IE";
    private String p_browserDir                 = "ie";
    private int p_browserCode                   = -1;
    private String p_browserTheme               = "0";
    private ngtXMLHandler[] p_defaultWorkplaces = null;
    
    private Properties  p_wordTemplateProp;
    private Properties  p_win32ClientProp;
    
    private String[] p_threadsName;
    private String[] p_threadsClass;
    private String[] p_threadsEjbName;
    private String[] p_threadsInterval;
    
    private String p_threadsType="USERTHREADS";
    
    private String p_cscriptPath = null;
    private String p_vbprogPath = null;

    private Properties[] p_repositories = null;
    
    private boApplicationLoggerConfig[] p_loggerConfig = null;
    

    //deploy de esquemas
    private String p_tablespace;

        public boApplicationConfig( String home )
        {
            refresh( home );
        }


        public  String getDefinitiondir()
        {
            return p_definitiondir;
        }
    
        public  String getUiDefinitiondir()
        {
            return p_uiDefinitiondir;
        }
    
        public  String getDeployJspDir()
        {
            return p_deployjspdir;
        }
    
        public  String getDeploymentclassdir()
        {
            return p_deploymentclassdir;
        }
        
        public String getModuleWebBaseDir() {
        	return p_moduleswebdir;
        }

        public  String getCompilerdir()
        {
            return p_compilerdir;
        }

        public String getLibDir()
        {
            if (p_libdir.startsWith("."))p_libdir=p_ngthome+p_libdir;
            return p_libdir;
        }

        
        public  boolean isDeveloperMode()
        {
            return p_developerMode;
        }
        public  String getDeploymentsrcdir()
        {
            return p_deploymentsrcdir;
        }
    
        public  String getDeploymentDir()
        {
            return p_deploymentdir;
        }
    
        public  String getNgtHome()
        {
            return p_ngthome;
        }
    
        public  String getTemplatesDir()
        {
            return p_templatesdir;
        }
    
        public  boConfigRepository getConfigRepository(String name)
        {
            return ( boConfigRepository ) p_repositorys.get(name);
        }
    
        public  String getDefaultRepository()
        {
            return p_defaultrep;
        }
    
        public  boolean aspmodeOn()
        {
            return "yes".equalsIgnoreCase(p_aspMode) || "y".equalsIgnoreCase(p_aspMode);
        }
    
        public  String getBrowserName()
            throws boRuntimeException
        {
            return Browser.getBrowserName(p_browserCode);
        }
        
        public String getVBProgPath()
        {
            return p_vbprogPath;
        }
        
        public String getCScriptPath()
        {
            return p_cscriptPath;
        }
    
        public  String getBrowserDirPrefix()
            throws boRuntimeException
        {
            return p_browserDir;
        }
    
        public  String getBrowserTheme()
            throws boRuntimeException
        {
            return p_browserTheme;
        }
    
        public  int getBrowserCode()
        {
            return p_browserCode;
        }
        
        public  String getSystemEncoding()
        {
            String encoding = null;
            try
            {
                FileWriter fw = new FileWriter("out");                
                encoding = fw.getEncoding();
                fw.close();
                Charset c = Charset.forName(encoding);
                encoding = c.name();
            }
            catch (IOException e)
            {
                //ignore
            }
            return encoding;
        }
    
        public  String getEncoding()
        {
            if(p_encoding == null)
            {
                p_encoding = getSystemEncoding();
            }
            return p_encoding;
        }
    
        public  String getTableSpace()
        {
            return p_tablespace;
        }
    
        public  void refresh()
        {
            if( p_cfgFile == null )
            {
                throw new RuntimeException("Error cannot reload a unitialized config...");
            }
            refresh( p_cfgFile );
        }
        
        public  void refresh( String configFile )
        {
            // Inicialização do Objecto
        	
            p_repositorys     = new Hashtable();
            
            p_cfgFile         = configFile;
            
            File config = new File( configFile );

            p_ngthome = config.getParent();

            p_ngthome = fixRelativePath( "", p_ngthome );
    
            try
            {
                xmldoc = ngtXMLUtils.loadXMLFile( configFile );
            }
            catch (RuntimeException e)
            {
                String[] emsg = { configFile };
                throw new boException("netgest.bo.builder._init()", "BO-1201", e, emsg);
            }
    
            try
            {
            	
            	
            	
                XMLNode xnode = ( XMLNode ) xmldoc.selectSingleNode("//logConfig");
                
                if( xnode != null ) {
                	parseLogConfig( (XMLElement)xnode );
                }
                
                
            	xnode = ( XMLNode ) xmldoc.selectSingleNode("//definitiondir");
                if (xnode != null)
                {
                    p_definitiondir = getNodeText( xnode );
                }
    
                xnode = ( XMLNode ) xmldoc.selectSingleNode("//uidefinitiondir");
    
                if (xnode != null)
                {
                    p_uiDefinitiondir = getNodeText( xnode );
                }
    
                xnode = (XMLNode) xmldoc.selectSingleNode("//developerMode");
    
                if (xnode != null)
                {
                    p_developerMode = new Boolean(getNodeText( xnode )).booleanValue();
                }
                
                xnode = ( XMLNode ) xmldoc.selectSingleNode("//aspmode");
    
                if (xnode != null)
                {
                    p_aspMode = (getNodeText( xnode ) != null)
                        ? getNodeText( xnode ) : "no";
                }
    
                xnode = ( XMLNode ) xmldoc.selectSingleNode("//encoding");
    
                if (xnode != null)
                {
                    p_encoding = getNodeText( xnode );
                }
    
                xnode = ( XMLNode ) xmldoc.selectSingleNode("//webcontextroot");
    
                if (xnode != null)
                {
                    webContextRoot = getNodeText( xnode );
                }
                
                xnode = (XMLNode)xmldoc.selectSingleNode("//threads");
                if ( xnode!= null )
                {
                    //get implementation type
                    
                    NamedNodeMap threadsAtt=xnode.getAttributes();
                    if (threadsAtt!=null && threadsAtt.getNamedItem("type")!=null)
                    {
                      p_threadsType=threadsAtt.getNamedItem("type").getNodeValue();
                    }
                    
                    NodeList x= xnode.getChildNodes();
                    ArrayList threadsName=new ArrayList();
                    ArrayList threadsClass=new ArrayList(); 
                    ArrayList threadsEjbName=new ArrayList(); 
                    ArrayList threadsInterval=new ArrayList(); 
                    if (x!=null)
                    {
                        for (int i = 0; i < x.getLength() ; i++) 
                        {
                            Node t = x.item( i );
                            NamedNodeMap attrs= t.getAttributes();
                            if (attrs!=null)
                            {
                                if ( attrs.getNamedItem("name")!= null && attrs.getNamedItem("class")!= null )
                                {
                                    threadsName.add( attrs.getNamedItem("name").getNodeValue());
                                    threadsClass.add( attrs.getNamedItem("class").getNodeValue());
                                }
                                if ( attrs.getNamedItem("ejb-name")!= null)
                                {
                                    threadsEjbName.add( attrs.getNamedItem("ejb-name").getNodeValue());
                                }
                                else threadsEjbName.add(null);
                                
                                if ( attrs.getNamedItem("interval")!= null)
                                {
                                    threadsInterval.add( attrs.getNamedItem("interval").getNodeValue());
                                }
                                else threadsInterval.add(null);
                            }
                            
                        }
                        p_threadsName = (String[]) threadsName.toArray(new String[threadsName.size()]);
                        p_threadsClass = (String[]) threadsClass.toArray(new String[threadsClass.size()]);
                        p_threadsEjbName = (String[]) threadsEjbName.toArray(new String[threadsEjbName.size()]);
                        p_threadsInterval = (String[]) threadsInterval.toArray(new String[threadsInterval.size()]);
                    }
                }
                else
                {
                    p_threadsName = new String[0];
                    p_threadsClass = new String[0];
                    p_threadsEjbName = new String[0];
                    p_threadsInterval = new String[0];              
                }
                
    
                xnode = ( XMLNode ) xmldoc.selectSingleNode("//deployment");
    
                if (xnode != null)
                {
                    p_deploymentclassdir     = (xnode.selectSingleNode("class_dir") != null)
                        ? getNodeText( xnode.selectSingleNode("class_dir") ) : "";
                        
                    p_moduleswebdir     = (xnode.selectSingleNode("obj_moduleswebdir") != null)
                        ? getNodeText( xnode.selectSingleNode("obj_moduleswebdir") ) : "";
                    
                    p_deploymentsrcdir     = (xnode.selectSingleNode("src_dir") != null)
                        ? getNodeText( xnode.selectSingleNode("src_dir") ) : "";
                    p_deploymentdir     = (xnode.selectSingleNode("obj_dir") != null)
                        ? getNodeText( xnode.selectSingleNode("obj_dir") ) : "";
                    p_compilerdir     = (xnode.selectSingleNode("obj_compiler") != null)
                        ? getNodeText( xnode.selectSingleNode("obj_compiler") ) : "";
                    p_templatesdir     = (xnode.selectSingleNode("obj_templates") != null)
                        ? getNodeText( xnode.selectSingleNode("obj_templates") ) : "";
                    p_deployjspdir     = (xnode.selectSingleNode("obj_deployjspdir") != null)
                        ? getNodeText( xnode.selectSingleNode("obj_deployjspdir") ) : "";
    
                    p_libdir     = (xnode.selectSingleNode("lib_dir") != null)
                        ? getNodeText( xnode.selectSingleNode("lib_dir") )
                        : (p_ngthome + "lib");
    
                    p_tablespace = (xnode.selectSingleNode("tablespace") != null)
                        ? getNodeText( xnode.selectSingleNode("tablespace") ) : "";
                }
    
                xnode = ( XMLNode ) xmldoc.selectSingleNode("//browser");
    
                if (xnode != null)
                {
                    p_browserName     = (xnode.selectSingleNode("name") != null)
                        ? getNodeText( xnode.selectSingleNode("name") ) : p_browserName;
                    p_browserDir     = (xnode.selectSingleNode("dir_prefix") != null)
                        ? getNodeText( xnode.selectSingleNode("dir_prefix") ) : p_browserDir;
                    p_browserCode      = Browser.getBrowserCode(p_browserName);
                    p_browserTheme     = (xnode.selectSingleNode("theme") != null)
                        ? getNodeText( xnode.selectSingleNode("theme") ) : p_browserTheme;
                }
                
                xnode = ( XMLNode ) xmldoc.selectSingleNode("//tratadoc");
    
                if (xnode != null)
                {
                    p_cscriptPath     = (xnode.selectSingleNode("cscriptPath") != null)
                        ? getNodeText( xnode.selectSingleNode("cscriptPath") ) : p_cscriptPath;
                    p_vbprogPath     = (xnode.selectSingleNode("vbprogPath") != null)
                        ? getNodeText( xnode.selectSingleNode("vbprogPath") ) : p_vbprogPath;
                }
    
                xnode = ( XMLNode ) xmldoc.selectSingleNode("//Repositories");
    
                if (xnode != null)
                {
                    NodeList nreps    = xnode.getChildNodes();
                    NodeList nrep;
                    Node rep          = null;
                    String repName    = null;
                    NamedNodeMap nnm  = null;
                    boolean isDefault = false;
                    String schemaName = null;
    
                    for (byte i = 0; i < nreps.getLength(); i++)
                    {
                        rep = nreps.item(i);
    
                        if ("Repository".equalsIgnoreCase(rep.getNodeName()))
                        {
                            isDefault     = false;
                            nrep          = rep.getChildNodes();
    
                            for (byte k = 0; k < nrep.getLength(); k++)
                            {
                                if ("Name".equalsIgnoreCase(nrep.item(k).getNodeName()))
                                {
                                    repName = getNodeText( nrep.item(k) );
    
                                    if ("default".equalsIgnoreCase(repName))
                                    {
                                        isDefault = true;
                                    }
                                }
                                else if ("Schema".equalsIgnoreCase(nrep.item(k).getNodeName()))
                                {
                                    schemaName = getNodeText( nrep.item(k) );
                                }
                            }
    
                            if (p_defaultrep == null)
                            {
                                p_defaultrep = schemaName;
                            }
    
                            p_repositorys.put(repName, new boConfigRepository(repName, rep));
                        }
                    }
                }
                
                // Word Template
                p_wordTemplateProp = new Properties();
                NodeList nodelist  = xmldoc.selectNodes("//bo-config/wordTemplate");
    
                for (int i = 0; i < nodelist.getLength(); i++)
                {
                    XMLNode node     = ( XMLNode ) nodelist.item(i);
                    XMLNode nextnode = ( XMLNode ) node.getFirstChild();
    
                    do
                    {
                        for (int z = 0; z < nextnode.getChildNodes().getLength(); z++)
                        {
                            p_wordTemplateProp.setProperty(nextnode.getNodeName(), nextnode.getText());
                        }
                    }
                    while ((nextnode = ( XMLNode ) nextnode.getNextSibling()) != null);
                }
                
                // Client 32 Properties
                p_win32ClientProp = new Properties();
                nodelist  = xmldoc.selectNodes("//bo-config/win32Client");
                if (nodelist!=null)
                {
                    for (int i = 0; i < nodelist.getLength(); i++)
                    {
                        XMLNode node     = ( XMLNode ) nodelist.item(i);
                        XMLNode nextnode = ( XMLNode ) node.getFirstChild();
        
                        do
                        {
                            for (int z = 0; z < nextnode.getChildNodes().getLength(); z++)
                            {
                                p_win32ClientProp.setProperty(nextnode.getNodeName(), nextnode.getText());
                            }
                        }
                        while ((nextnode = ( XMLNode ) nextnode.getNextSibling()) != null);
                    }
                }
                
                p_win32ClientProp.put( "path", fixRelativePath( p_ngthome, p_win32ClientProp.getProperty("path") ) );
                p_wordTemplateProp.put( "path", fixRelativePath( p_ngthome, p_wordTemplateProp.getProperty("path") ) );

                p_definitiondir         = fixRelativePath( p_ngthome, p_definitiondir );
                p_uiDefinitiondir       = fixRelativePath( p_ngthome, p_uiDefinitiondir );
                p_deploymentclassdir    = fixRelativePath( p_ngthome, p_deploymentclassdir );
                p_deploymentsrcdir      = fixRelativePath( p_ngthome, p_deploymentsrcdir );
                p_deploymentdir         = fixRelativePath( p_ngthome, p_deploymentdir );
                p_compilerdir           = fixRelativePath( p_ngthome, p_compilerdir, false );
                p_templatesdir          = fixRelativePath( p_ngthome, p_templatesdir );
                p_deployjspdir          = fixRelativePath( p_ngthome, p_deployjspdir );
                p_moduleswebdir			= fixRelativePath( p_ngthome, p_moduleswebdir );

            }
            catch (Exception e)
            {
                String[] emsg = { configFile };
                throw new boException("netgest.bo.builder._init()", "BO-1202", e, emsg);
            }
        }
    
        public  Properties getAuthentication()
        {
             
            try
            {
                Properties retprop = new Properties();
                NodeList nodelist  = xmldoc.selectNodes("//bo-config/authentication");
    
                for (int i = 0; i < nodelist.getLength(); i++)
                {
                    XMLNode node     = ( XMLNode ) nodelist.item(i);
                    XMLNode nextnode = ( XMLNode ) node.getFirstChild();
    
                    do
                    {
                        for (int z = 0; z < nextnode.getChildNodes().getLength(); z++)
                        {
                            retprop.setProperty(nextnode.getNodeName(), nextnode.getText());
                        }
                    }
                    while ((nextnode = ( XMLNode ) nextnode.getNextSibling()) != null);
                }
    
                return retprop;
            }
            catch (XSLException e)
            {
                return null;
            }
        }
    
        public  Properties getMailConfig()
        {
             
            try
            {
                Properties retprop = new Properties();
                NodeList nodelist  = xmldoc.selectNodes("//bo-config/mail");
    
                for (int i = 0; i < nodelist.getLength(); i++)
                {
                    XMLNode node     = ( XMLNode ) nodelist.item(i);
                    XMLNode nextnode = ( XMLNode ) node.getFirstChild();
    
                    do
                    {
                        for (int z = 0; z < nextnode.getChildNodes().getLength(); z++)
                        {
                            retprop.setProperty(nextnode.getNodeName(), nextnode.getText());
                        }
                    }
                    while ((nextnode = ( XMLNode ) nextnode.getNextSibling()) != null);
                }
    
                return retprop;
            }
            catch (XSLException e)
            {
                return null;
            }
        }
        
        public  Properties getFaxConfig()
        {
             
            try
            {
                Properties retprop = new Properties();
                NodeList nodelist  = xmldoc.selectNodes("//bo-config/fax");
    
                for (int i = 0; i < nodelist.getLength(); i++)
                {
                    XMLNode node     = ( XMLNode ) nodelist.item(i);
                    XMLNode nextnode = ( XMLNode ) node.getFirstChild();
    
                    do
                    {
                        for (int z = 0; z < nextnode.getChildNodes().getLength(); z++)
                        {
                            retprop.setProperty(nextnode.getNodeName(), nextnode.getText());
                        }
                    }
                    while ((nextnode = ( XMLNode ) nextnode.getNextSibling()) != null);
                }
    
                return retprop;
            }
            catch (XSLException e)
            {
                return null;
            }
        }
    
        public  Properties getSecurityConfig()
        {
             
            try
            {
                Properties retprop = new Properties();
                NodeList nodelist  = xmldoc.selectNodes("//bo-config/security");
    
                for (int i = 0; i < nodelist.getLength(); i++)
                {
                    XMLNode node     = ( XMLNode ) nodelist.item(i);
                    XMLNode nextnode = ( XMLNode ) node.getFirstChild();
    
                    do
                    {
                        for (int z = 0; z < nextnode.getChildNodes().getLength(); z++)
                        {
                            retprop.setProperty(nextnode.getNodeName(), nextnode.getText());
                        }
                    }
                    while ((nextnode = ( XMLNode ) nextnode.getNextSibling()) != null);
                }
    
                return retprop;
            }
            catch (XSLException e)
            {
                return null;
            }
        }
        
        public  Properties getContentMngmConfig()
        {
             
            try
            {
                Properties retprop = new Properties();
                NodeList nodelist = xmldoc.selectNodes("//bo-config/Content_Manager");
    
                for (int i = 0; i < nodelist.getLength(); i++)
                {
                    XMLNode node = (XMLNode) nodelist.item(i);
                    XMLNode nextnode = (XMLNode) node.getFirstChild();
    
                    do
                    {
                        for (int z = 0; z < nextnode.getChildNodes().getLength();
                                z++)
                        {
                            retprop.setProperty(nextnode.getNodeName(),
                                nextnode.getText());
                        }
                    }
                    while ((nextnode = (XMLNode) nextnode.getNextSibling()) != null);
                }
    
                return retprop;
            }
            catch (XSLException e)
            {
                return null;
            }
        }
        
        public  Properties getDocumentationConfig()
        {
             
            try
            {
                Properties retprop = new Properties();
                NodeList nodelist  = xmldoc.selectNodes("//bo-config/documentation");
    
                for (int i = 0; i < nodelist.getLength(); i++)
                {
                    XMLNode node     = ( XMLNode ) nodelist.item(i);
                    XMLNode nextnode = ( XMLNode ) node.getFirstChild();
    
                    do
                    {
                        for (int z = 0; z < nextnode.getChildNodes().getLength(); z++)
                        {
                            retprop.setProperty(nextnode.getNodeName(), nextnode.getText());
                        }
                    }
                    while ((nextnode = ( XMLNode ) nextnode.getNextSibling()) != null);
                }
    
                return retprop;
            }
            catch (XSLException e)
            {
                return null;
            }
        }
    
        public  Properties getWin32ClientConfig()
        {
            return p_win32ClientProp;
        }
    
    
        public  String getWebContextRoot()
        {
             
            return webContextRoot;
        }
        
    public Properties[] getRepositories( ) 
    {
    	if( p_repositories == null ) {
      try {
          NodeList repnodelist = xmldoc.selectNodes("//bo-config/Repositories");
          Properties[] ret = new Properties[ repnodelist.item( 0 ).getChildNodes().getLength() ]; 
          for (int k = 0; k < repnodelist.getLength(); k++) 
          {
             NodeList nodelist = repnodelist.item( k ).getChildNodes();
             Properties retprop = null;
              for(int i=0;i<nodelist.getLength();i++) 
              {
                  retprop = new Properties();
                    XMLNode node = (XMLNode)nodelist.item(i);
                    XMLNode nextnode = (XMLNode)node.getFirstChild();
                    do {
                        for(int z=0;z<nextnode.getChildNodes().getLength();z++) {
                            retprop.setProperty(nextnode.getNodeName(),nextnode.getText());
                        }
                    } while ((nextnode=(XMLNode)nextnode.getNextSibling())!=null);
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
   
    public Properties getWordTemplateConfig()
    {
        return p_wordTemplateProp;
    }

    public String[] getThreadsName()
    {
        return p_threadsName;
    }
    
    public String[] getThreadsClass()
    {
        return p_threadsClass;
    }

    public String[] getThreadsInterval()
    {
        return p_threadsInterval;
    }

    public String[] getThreadsEjbName()
    {
        return p_threadsEjbName;
    }

    public String getThreadsType()
    {
      return p_threadsType;
    }
    
    public boApplicationLoggerConfig[] getLoggersConfig() {
    	return p_loggerConfig;
    }
    
    
    private static final Properties[] removeDuplicated(Properties[] ret)
    {
        if(ret == null) return ret;
        ArrayList dupl = new ArrayList();
        ArrayList objToRet = new ArrayList();
        Properties[] toRet;
        
        for(int i = 0; i < ret.length; i++)
        {
            if(dupl.indexOf(ret[i].getProperty("Name")) < 0)
            {
                dupl.add(ret[i].getProperty("Name"));
                objToRet.add(ret[i]);
            }
        }
        toRet = new Properties[objToRet.size()];
        for(int i = 0; i < objToRet.size(); i++)
        {
            toRet[i] = (Properties)objToRet.get(i);
        }
        return toRet;
    }

    public ngtXMLHandler[] getWorkPlaces()
    {
        ArrayList wkplcs = new ArrayList(); 
        File f       = new File(p_uiDefinitiondir);
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++)
        {
            if (files[i].getName().toUpperCase().endsWith("$WORKPLACE.XML"))
            {
                ngtXMLHandler wkpXML = new ngtXMLHandler(ngtXMLUtils.loadXMLFile( files[i].getAbsolutePath()));
                String xml = ngtXMLUtils.getXML( wkpXML.getDocument() );
                wkplcs.add( wkpXML );
            }
        }
        p_defaultWorkplaces = (ngtXMLHandler[] ) wkplcs.toArray( new ngtXMLHandler[ wkplcs.size() ] );
        return p_defaultWorkplaces;
    }
    
    public XMLElement getDataSourcesNode()
    {
        try
        {
            XMLElement root = (XMLElement) xmldoc.getDocumentElement();
            XMLElement nds = (XMLElement) root.selectSingleNode("DataSources");
            return nds;
        }
        catch (XSLException e)
        {
            throw new RuntimeException( e );
        }
    }
    
    public static final String fixRelativePath( String base, String path)
    {
        return fixRelativePath( base, path, true );
    }
    public static final String fixRelativePath( String base, String path, boolean appendSeparator )
    {
        if( path != null ) 
        {
        	path = path.replace('\\', File.separatorChar );
        	path = path.replace('/', File.separatorChar );
        	
            if( !Pattern.matches( "^((.\\:)|(\\/))(.*)$", path ) )
            {
                if( path.startsWith(".\\") || path.startsWith("./") )
                {
                    path = path.substring( 2 );
                }
                path = base + path;
            }

            if( appendSeparator )
            {
                if (!(path.endsWith("/") || path.endsWith("/") || path.endsWith( File.separator )) )
                {
                    path += File.separator;
                }
            }
        }
        return path;
    }
    
    private static final String getNodeText( Node node )
    {
        return ((XMLNode)node).getText();
    }
    
    private void parseLogConfig( XMLElement logConfig ) {
    	
    	NodeList 	loggers 		= logConfig.getElementsByTagName("logger");
    	ArrayList 	loggersArray 	= new ArrayList();
    	for( int i=0; i < loggers.getLength(); i++ ) {
    		XMLElement logger = (XMLElement)loggers.item( i );
    		
    		String[] forPackages = logger.getAttribute( "for" ).split( "," );
    		for(int k=0; k < forPackages.length; k++  ) {
    			
    			String forPackage = forPackages[k];
    			
				boApplicationLoggerConfig loggerConfig = new boApplicationLoggerConfig();
				loggerConfig.setActive( Boolean.parseBoolean( logger.getAttribute( "active" ) ) );
				loggerConfig.setForClasses( forPackage );
				loggerConfig.setLevel( logger.getAttribute( "level" ) );
				loggerConfig.setPattern( "%d %5p [%t] (%F:%L) - %m%n" );
	    		NodeList loggerAppenders = logger.getChildNodes();
	    		
				for( int z=0; z < loggerAppenders.getLength(); z++ ) {
					if( loggerAppenders.item( z ).getNodeType() == Node.ELEMENT_NODE ) {
						
						Element logAppender = (Element)loggerAppenders.item( z );
						String	logAppName = logAppender.getNodeName();
						if( "console".equals( logAppName ) ) {
							
							boApplicationLoggerConfig.ConsoleProperties capp
								= new boApplicationLoggerConfig.ConsoleProperties();
							capp.setActive( Boolean.parseBoolean( logAppender.getAttribute("active") ) );
							loggerConfig.setConsoleProperties( capp );
							
						}
						else if( "file".equals( logAppName ) ) {
							boApplicationLoggerConfig.FileProperties capp
								= new boApplicationLoggerConfig.FileProperties();
							
							capp.setActive( Boolean.parseBoolean( logAppender.getAttribute("active") ) );
							capp.setLogFile( fixRelativePath( p_ngthome, logAppender.getAttribute("logFile"), false ) );
							
							capp.setMaxSize( logAppender.getAttribute("maxSize") );
							capp.setHistoryFiles( Integer.parseInt( logAppender.getAttribute("backupFiles") ) );
							capp.setLogStandardOutput( logAppender.getAttribute("logStandardOutput") );
							capp.setLogErrorOutput( logAppender.getAttribute("logErrorOutput") );
							loggerConfig.setFileProperties( capp );
						}
						else if( "email".equals( logAppName ) ) {
							boApplicationLoggerConfig.EmailProperties capp
								= new boApplicationLoggerConfig.EmailProperties();
							capp.setActive( Boolean.parseBoolean( logAppender.getAttribute("active") ) );
							capp.setBuffer( Integer.parseInt( logAppender.getAttribute("buffer") ) );
							capp.setFrom( logAppender.getAttribute("from") );
							capp.setSmtpHost( logAppender.getAttribute("smtpHost") );
							capp.setSubject( logAppender.getAttribute("subject") );
							capp.setTo( logAppender.getAttribute("to") );
							loggerConfig.setEmailProperties( capp );
						}
					}
				}
				loggersArray.add( loggerConfig );
    		}
    	}
        p_loggerConfig = (boApplicationLoggerConfig[])loggersArray.toArray( new boApplicationLoggerConfig[ loggersArray.size() ] );
    }
}
