/*Enconding=UTF-8*/
package netgest.io;
import java.io.File;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import netgest.bo.boConfig;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.system.boSession;
import netgest.system.ngtconfig;

import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.NodeList;
import java.util.Iterator;

public class iFileServer  {
    
    private static final String fileName = "ifileservices.xml";

    private static Map servicesDef		 = null;
    
    private Hashtable p_ifs=new Hashtable(); 
    
    
    
    public iFileService getFileService(String path) {
        path = path.trim();
        if(!path.startsWith("//") || path.length()<=2) {
            throw(new RuntimeException(MessageLocalizer.getMessage("INVALID_PATH")));
        }
        String service = "";
        if(path.indexOf("/",2)==-1) {
             service = path.substring(2);
        } else {
            service = path.substring(2,path.indexOf("/",2));
        }
        return this.getService(service);
    }
    public iFile getFile(String path) {
        if(!path.startsWith("//") || path.length()<=2) {
            throw(new RuntimeException(MessageLocalizer.getMessage("INVALID_PATH")));
        }
        String xpath = "";
        if(path.indexOf("/",2)==-1) {
             xpath   = "";
        } else {
            xpath = path.substring(path.indexOf("/",2)+1);
        }
        return getFileService(path).getFile(xpath);
    }
    public iFileService getService(String name) {
        iFileService ret = (iFileService)p_ifs.get(name);
        if(ret==null) {
            throw(new RuntimeException(MessageLocalizer.getMessage("SERVICE")+" ["+name+"] "+MessageLocalizer.getMessage("DOESNT_EXIST")+"."));
        }
        return ret;
    }
//    public void mount(ngtContext p_ctx,String ifsurl) {
//        mount(p_ctx.getSysUser().getUserName(),p_ctx.getSysUser().getPassword(),p_ctx.getSysUser().getUserGroup(),p_ctx);
//    }
//    public void mount(ngtContext p_ctx) {
//        mount(p_ctx.getSysUser().getUserName(),p_ctx.getSysUser().getPassword(),p_ctx.getSysUser().getUserGroup(),p_ctx);
//    }
    public void mount(String ifsurl) {
    }
    public void mount() {
        mount((String)null,(String)null,null,null);

    }
    public void unmount() {
        iFileService[] ifs = this.listServices();
        for(int i=0;i<ifs.length;i++)
            ifs[i].close();
    }
    /*
    public void mount(String a_username,String a_password,Vector usrgrp,netgest.bo.system.boSession p_ctx) {
        XMLDocument services;
        try {
        
            if(p_ctx==null) {
                ngtconfig ngtcfg = new ngtconfig();
                String fn = ngtcfg.getConfiguration("netgest/iFileServer").getProperty("configFile");
                if(fn==null) 
                {
                      if(System.getProperties().containsKey("netgest.home")) {
                          fn = (String)System.getProperties().get("netgest.home");
                      }
                      if(fn != null) {
                        if(!fn.endsWith("/") && !fn.endsWith("\\"))
                            fn += File.separator ;
                        fn+="ifileservices.xml" ;
                      }
                }
                services = ngtXMLUtils.loadXMLFile_FileReader(fn);
            } else {
                // TODO: remove ngtContext
                Properties props = null;// p_ctx.getNgtConfig().getConfiguration("netgest/iFileServer");
                String fn = props.getProperty("configfile");
                if(fn==null)
                    throw(new RuntimeException("Configuração não existe no Registo do Netgest\nnetgest/iFileServer/configfile"));
                services = ngtXMLUtils.loadXMLFile_FileReader(fn);
            }
                
        } catch ( RuntimeException e) {
            throw(new RuntimeException("Erro no ficheiro de configuração.\n"+e.getMessage()));
        }
        try {
            XMLNode ifsnode = (XMLNode)services.selectSingleNode("NGTIFS");
            NodeList ifs_services = ifsnode.selectNodes("SERVICE");
            XMLNode servicenode;
            for(int i=0;i<ifs_services.getLength();i++) {
                mountService((XMLNode)ifs_services.item(i),a_username,a_password,usrgrp);
            }
        } catch (XSLException e) {

        }
    }
    */
    public void mount(String a_username,String a_password,Vector usrgrp,boSession p_ctx) 
    {
    	initialize();
    	Iterator it = servicesDef.values().iterator();
    	while( it.hasNext() ) {
    		mountService( (ServiceDefinition)it.next(),a_username,a_password,usrgrp);
    	}
    }    

    public iFileService[] listServices() {
        iFileService[] ifs = new iFileService[p_ifs.size()];
        Collection coll = p_ifs.values();
        return (iFileService[])coll.toArray(ifs);
    }
    
    private void initialize() {
        
    	if( servicesDef == null ) {
	    	
    		servicesDef = new LinkedHashMap();
    		
        XMLDocument services = null;
        try 
        {        
            services = ngtXMLUtils.loadXMLFile(boConfig.getNgtHome() + File.separator + fileName);                
        } 
        catch ( RuntimeException e) 
        {
            throw(new RuntimeException(MessageLocalizer.getMessage("ERROR_IN_CONFIGURATION_FILE")+".\n"+e.getMessage()));
        }
        try {
            XMLNode ifsnode = (XMLNode)services.selectSingleNode("NGTIFS");
            NodeList ifs_services = ifsnode.selectNodes("SERVICE");
            for(int i=0;i<ifs_services.getLength();i++) {
	        		ServiceDefinition servDef = getServiceDefinition( (XMLNode)ifs_services.item(i) );
	        		servicesDef.put(  servDef.name, servDef );
            }
        } catch (XSLException e) {
            throw(new RuntimeException(MessageLocalizer.getMessage("ERROR_IN_CONFIGURATION_FILE")+".\n"+e.getMessage()));
        }
    }    
    }

    
    private void mountService( ServiceDefinition servDef, String a_username, String a_password, Vector usrgrp) {
        iFileService ifs = new iFileService(
        		servDef.className,
        		servDef.name,
        		servDef.description,
        		a_username==null?servDef.userName:a_username,
        		a_password==null?servDef.password:a_password,
				servDef.path,
				servDef.connectionstring,
				servDef.adminGroup,
				servDef.icon
        );
        
        Iterator it = servDef.permissions.values().iterator();
            boolean valid=false;
        while( it.hasNext() ) {
            //XMLNode xnode = (XMLNode)pernodes.item(i);
        	ServicePermissions servPermissions = (ServicePermissions)it.next();

            ifs.setPermissions(
            		servPermissions.group,
            		servPermissions.browse,
            		servPermissions.read,
            		servPermissions.write,
            		servPermissions.createnew,
            		servPermissions.delete,
            		servPermissions.versioncontrol
            );

                    for(int k=0;usrgrp!=null && k<usrgrp.size();k++) {
                if((String)usrgrp.get(k)!=null && ((String)usrgrp.get(k)).equalsIgnoreCase(servPermissions.group))
                            valid = true;
                    }
            if((servPermissions.group.equals("*") || servPermissions.group.equals("public")) && valid == false) {
                        valid = true;
                    }
            }
            if(valid) {
                ifs.open();
            p_ifs.put(servDef.name,ifs);
        }
    }
    
    public ServiceDefinition getServiceDefinition( XMLNode node ) throws XSLException  {
    	
    	ServiceDefinition servDef = new ServiceDefinition();
    	
    	servDef.className=node.selectSingleNode("CLASS")!=null?
            ((XMLNode)node.selectSingleNode("CLASS")).getText():null;
            
        servDef.name=node.selectSingleNode("NAME")!=null?
            ((XMLNode)node.selectSingleNode("NAME")).getText():null;
	                
        servDef.description=node.selectSingleNode("DESCRIPTION")!=null?
            ((XMLNode)node.selectSingleNode("DESCRIPTION")).getText():null;
            
        servDef.userName=node.selectSingleNode("USERNAME")!=null?
	                ((XMLNode)node.selectSingleNode("USERNAME")).getText():null;
	                
	    servDef.password=node.selectSingleNode("PASSWORD")!=null?
	                ((XMLNode)node.selectSingleNode("PASSWORD")).getText():null;
	    servDef.path=node.selectSingleNode("PATH")!=null?
	                ((XMLNode)node.selectSingleNode("PATH")).getText():null;
	    servDef.connectionstring=node.selectSingleNode("CONNECTIONSTRING")!=null?
	                ((XMLNode)node.selectSingleNode("CONNECTIONSTRING")).getText():null;
	    servDef.adminGroup=node.selectSingleNode("ADMINGROUP")!=null?
	                ((XMLNode)node.selectSingleNode("ADMINGROUP")).getText():null;
	    servDef.icon=node.selectSingleNode("ICON")!=null?
	                ((XMLNode)node.selectSingleNode("ICON")).getText():null;
	                
	    NodeList pernodes=node.selectNodes("PERMISSIONS");
        boolean valid=false;
        for(int i=0;i<pernodes.getLength();i++) {
        	ServicePermissions servPerm = new ServicePermissions();
            XMLNode xnode = (XMLNode)pernodes.item(i);
            servPerm.group = xnode.selectSingleNode("group")!=null?
                    ((XMLNode)xnode.selectSingleNode("group")).getText():null;
            servPerm.browse = xnode.selectSingleNode("browse")!=null?
                    (new Boolean(((XMLNode)xnode.selectSingleNode("browse")).getText())).booleanValue():true;
            servPerm.read = xnode.selectSingleNode("read")!=null?
                    (new Boolean(((XMLNode)xnode.selectSingleNode("read")).getText())).booleanValue():true;
            servPerm.write = xnode.selectSingleNode("write")!=null?
                    (new Boolean(((XMLNode)xnode.selectSingleNode("write")).getText())).booleanValue():false;
            servPerm.createnew = xnode.selectSingleNode("new")!=null?
                    (new Boolean(((XMLNode)xnode.selectSingleNode("new")).getText())).booleanValue():false;
            servPerm.delete = xnode.selectSingleNode("delete")!=null?
                    (new Boolean(((XMLNode)xnode.selectSingleNode("delete")).getText())).booleanValue():false;
            servPerm.versioncontrol = xnode.selectSingleNode("versioncontrol")!=null?
            		(new Boolean(((XMLNode)xnode.selectSingleNode("versioncontrol")).getText())).booleanValue():false;
            
            servDef.permissions.put( servPerm.group, servPerm );
	                
            }
        
        return servDef;

        }
    
    
    public static class ServiceDefinition {
    	String 	className;
    	String 	name;
    	String 	description;
    	String 	userName;
    	String 	password;
    	String 	path;
    	String 	connectionstring;
    	String 	adminGroup;
    	String 	icon;
    	Map 	permissions = new LinkedHashMap(1);
    };
    
    public static class ServicePermissions {
    	String	group;
    	boolean browse;
    	boolean read;
    	boolean write;
    	boolean createnew;
    	boolean delete;
    	boolean versioncontrol;
    }
    
    
    public void destroy() {
        this.unmount();
    }
}