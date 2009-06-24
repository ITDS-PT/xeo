/*Enconding=UTF-8*/
package netgest.system;
import java.io.*;
import oracle.xml.parser.v2.*;
import javax.naming.*;
import java.io.Serializable;
import org.w3c.dom.NodeList;
import java.util.Properties;
import netgest.utils.ngtProperties;
import org.xml.sax.SAXException;

public class ngtconfig implements Serializable
{
  private static String p_UserHome=null;
  private static String p_SitePath=null;
  private static String p_SiteUrl=null;
  private static String p_SiteServlet=null;
  private static String p_SiteJSP=null;
  private static String p_ReportsDir=null;
  private static String p_ReportsURL=null;
  private static String p_ReportsServer=null;
  private static String p_ReportsClientDir=null;
  private static String p_pathtojavac=null;
  private static String p_pathserverscriptoutput=null;
  private static String p_pathnetgestjar=null;
  private static String p_ldapserver=null;
  private static String p_ldapusername=null;
  private static String p_ldapuserpass=null;
  private static String p_ldapuserdn=null;
  private static String p_ldapngtcontextdn=null;
  private static String p_ldapgroupdn=null;
  private static int p_ngtauthentication=0;
  private static String p_systemngtconnstr=null;
  private static String p_ldaporaclectxRoot=null;
  private static boolean useSSO=false;    
  private static boolean useWKFL=false;    
  private static boolean useIFS=false;      
//  private static boolean useNGTDATACACHE=false;        
//  private static boolean useNGTDOCCACHE=false;          
  private static boolean usengtcache=true;
  
  private static String configpath=null;
  public  static XMLDocument xmldoc;
  private static String      xmlfile;
  

  public ngtconfig(String app)
  {
    getConfig(app);
  }
  
  public ngtconfig()
  {
    getConfig(null);
  }

  private void getConfig(String app) 
  {
      if(xmldoc == null) {
          configpath = "netgest/";
          String filename   = "config.xml";
          String fullpath=null;
          try {
              if(System.getProperties().containsKey("oracle.j2ee.home")) {
                  configpath = (String)System.getProperties().get("oracle.j2ee.home");
              }
              if(System.getProperties().containsKey("netgest.home")) {
                  configpath = (String)System.getProperties().get("netgest.home");
              }
              if(configpath != null) {
                if(!configpath.endsWith("/") && !configpath.endsWith("\\"))
                    configpath += "/";
              }
              FileReader xfr;
              fullpath = configpath.trim()+filename.trim();
              xfr = new FileReader(fullpath);
              DOMParser xmlp = new DOMParser();
              xmlp.parse(xfr);
              xfr.close();
              xmldoc = xmlp.getDocument();
              xmlfile = fullpath;

              XMLNode xelem = (XMLNode)((XMLNode)xmldoc.getDocumentElement()).selectSingleNode("config");
              XMLNode xnode;
              xnode = (XMLNode)xelem.selectSingleNode("userhome");
              if (xnode!=null) p_UserHome= xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("sitepath");
              if (xnode!=null) p_SitePath= xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("siteurl");
              if (xnode!=null) p_SiteUrl= xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("siteservlet");
              if (xnode!=null) p_SiteServlet= xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("sitejsp");
              if (xnode!=null) p_SiteJSP= xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("reportsdir");
              if (xnode!=null) p_ReportsDir = xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("reportsclientdir");
              if (xnode!=null) p_ReportsClientDir = xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("reportsurl");
              if (xnode!=null) p_ReportsURL= xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("reportsserver");
              if (xnode!=null) p_ReportsServer= xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("pathjavac");
              if (xnode!=null) p_pathtojavac = xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("pathserverscriptoutput");
              if (xnode!=null) p_pathserverscriptoutput = xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("pathnetgestjar");
              if (xnode!=null) p_pathnetgestjar = xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("ldapserver");
              if (xnode!=null) p_ldapserver = xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("ldapusername");
              if (xnode!=null) p_ldapusername = xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("ldapuserpass");
              if (xnode!=null) p_ldapuserpass = xnode.getText();
          
              xnode = (XMLNode)xelem.selectSingleNode("ldapuserdn");
              if (xnode!=null) p_ldapuserdn = xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("ldapngtcontextdn");
              if (xnode!=null) p_ldapngtcontextdn = xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("ldapgroupdn");
              if (xnode!=null) p_ldapgroupdn = xnode.getText();

              xnode = (XMLNode)xelem.selectSingleNode("ldaporaclectxroot");
              if (xnode!=null) p_ldaporaclectxRoot = xnode.getText();

              String auxStr=null;
              xnode = (XMLNode)xelem.selectSingleNode("usesso");
              if (xnode!=null) auxStr = xnode.getText();

              if (auxStr!=null && auxStr.equalsIgnoreCase("TRUE")) this.useSSO=true;

              xnode = (XMLNode)xelem.selectSingleNode("ngtauthentication");
              if (xnode!=null) 
              {
                if (xnode.getText().equalsIgnoreCase("LDAP"))
                p_ngtauthentication = 1;
              }        

              xnode = (XMLNode)xelem.selectSingleNode("useifs");
              if (xnode!=null) auxStr = xnode.getText();
              if (auxStr!=null && auxStr.equalsIgnoreCase("TRUE")) this.useIFS=true;

              xnode = (XMLNode)xelem.selectSingleNode("usewkfl");
              if (xnode!=null) auxStr = xnode.getText();
              if (auxStr!=null && auxStr.equalsIgnoreCase("TRUE")) this.useWKFL=true;

              xnode = (XMLNode)xelem.selectSingleNode("usengtcache");
              if (xnode!=null) auxStr = xnode.getText();
              if (auxStr!=null && (new Boolean(auxStr).booleanValue()) ) {
                    this.usengtcache=true;
              } else if (auxStr!=null) {
                    this.usengtcache=false;
              }
                  

          
              xnode = (XMLNode)xelem.selectSingleNode("systemngtconnstr");
              if (xnode!=null) p_systemngtconnstr = xnode.getText(); 
              else p_systemngtconnstr="jdbc/systemngt";

          } catch (FileNotFoundException e){
              throw new RuntimeException("Ficheiro netgest de configuração não encontrado\n"+fullpath);
          }  
          catch (XSLException e){
              throw new RuntimeException("Erro a fazer o parse do XML de configuração\n"+fullpath);
          }       
          catch (IOException e){
              throw new RuntimeException("Erro no dispositivo de Armazenamento\n"+fullpath);
          } 
          catch (SAXException e){
              throw new RuntimeException("Erro a fazer o parse do XML de configuração\n"+fullpath);
          }
      }
  }
  public java.util.Properties getConfiguration(String xslpath) {
      try {
          Properties retprop = (Properties)new ngtProperties();
          NodeList nodelist = xmldoc.selectNodes(xslpath);
          for(int i=0;i<nodelist.getLength();i++) {
                XMLNode node = (XMLNode)nodelist.item(i);
                XMLNode nextnode = (XMLNode)node.getFirstChild();
                do {
                    for(int z=0;z<nextnode.getChildNodes().getLength();z++) {
                        retprop.setProperty(nextnode.getNodeName(),nextnode.getText());
                    }
                } while ((nextnode=(XMLNode)nextnode.getNextSibling())!=null);
          }
          return retprop;
      } catch (XSLException e) {
          return null;
      }
  }
  public String getNetgestHome() {return configpath;}
  public String getUserHome() { return p_UserHome;}
  public String getSitePath() { return p_SitePath;}
  public String getSiteUrl() { return p_SiteUrl;}
  public String getSiteServlet() { return p_SiteServlet;}
  public String getSiteJSP() { return p_SiteJSP;}
  public String getReportsDir() { return p_ReportsDir;}
  public String getReportsClientDir() { return p_ReportsClientDir;}
  public String getReportsURL() { return p_ReportsURL;}
  public String getReportsServer() { return p_ReportsServer;}
  public String getPathToJavaC() { return p_pathtojavac;} 
  public String getPathServerScriptOutput() { return p_pathserverscriptoutput;}
  public String getPathNetgestJar() { return p_pathnetgestjar;}
  public String getLdapServer() { return this.p_ldapserver;}
  public String getLdapUserName() { return this.p_ldapusername;}
  public String getLdapUserPass() { return this.p_ldapuserpass;}
  public String getLdapUserDN() { return this.p_ldapuserdn;}  
  public String getLdapngtContextDN() { return this.p_ldapngtcontextdn;}  
  public String getLdapGroupDN() { return this.p_ldapgroupdn;}  
  public String getLdapOracleCtxRoot() { return this.p_ldaporaclectxRoot;}    
  public int getngtAuthentication() { return this.p_ngtauthentication;}    
  public String getSystemngtConnStr() { return this.p_systemngtconnstr;}
  public boolean getUseSSO() { return this.useSSO;}  
  public boolean getUseIFS() { return this.useIFS;}  
  public boolean getUseWKFL() { return this.useWKFL;}  
  public boolean getUseNGTCACHE() { return this.usengtcache;}      
  
  public void setUserHome(String p_UserHome) { this.p_UserHome=p_UserHome;}
  public void setSitePath(String p_SitePath) { this.p_SitePath=p_SitePath;}
  public void setSiteUrl(String p_SiteUrl) { this.p_SiteUrl=p_SiteUrl;}
  public void setSiteServlet(String p_SiteServlet) { this.p_SiteServlet=p_SiteServlet;}
  public void setSiteJSP(String p_SiteJSP) { this.p_SiteJSP=p_SiteJSP;}
  public void setReportsDir(String p_ReportsDir) { this.p_ReportsDir=p_ReportsDir;}
  public void setReportsClientDir(String p_ReportsClientDir) { this.p_ReportsClientDir=p_ReportsClientDir;}  
  public void setReportsURL(String p_ReportsURL) { this.p_ReportsURL=p_ReportsURL;}
  public void setReportsServer(String p_ReportsServer) { this.p_ReportsServer=p_ReportsServer;}
  public void setLdapServer(String p_ldapserver) {this.p_ldapserver=p_ldapserver;}
  public void setLdapUserName(String p_ldapusername) {this.p_ldapusername=p_ldapusername;}
  public void setLdapUserPass(String p_ldapuserpass) {this.p_ldapuserpass=p_ldapuserpass;}
  public void setLdapUserDN(String p_ldapuserdn) {this.p_ldapuserdn=p_ldapuserdn;}
  public void setLdapngtContextDN(String p_ldapngtcontextdn) {this.p_ldapngtcontextdn=p_ldapngtcontextdn;}
  public void setLdapGroupDN(String p_ldapgroupdn) {this.p_ldapgroupdn=p_ldapgroupdn;}
  public void setLdapOracleCtxRoot(String p_ldaporaclectxRoot) {this.p_ldaporaclectxRoot=p_ldaporaclectxRoot;}  
  public void setngtAuthentication(int p_ngtauthentication) {this.p_ngtauthentication=p_ngtauthentication;}
  public void setSystemngtConnStr(String p_systemngtconnstr) {this.p_systemngtconnstr=p_systemngtconnstr;}  
}