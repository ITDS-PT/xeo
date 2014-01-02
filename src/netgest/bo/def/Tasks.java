/*Enconding=UTF-8*/
package netgest.bo.def;

import java.io.File;

import java.util.Enumeration;
import java.util.Hashtable;

import netgest.bo.boConfig;
import netgest.bo.builder.ITask;

import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;

public class Tasks  
{
    private static  Logger logger = Logger.getLogger(Tasks.class);
    private static  Tasks instance = null;
    private static  final String fileName = "tasks.xml";
    private static  Hashtable definition = new Hashtable();  
    private static  Hashtable activeDefinition = new Hashtable();
    private boolean definitionLoaded = false;
    private         ngtXMLHandler xmlhand = null;    
    
    private Tasks()
    {
        init();
    }
    
    public static Tasks getInstance()
    {        
        Tasks instance = new Tasks();            
        instance.init();
        return instance;
    }    
    
    private void init() 
    {        
        try 
        {            
            if(!definitionLoaded) 
            {                
                XMLDocument doc = ngtXMLUtils.loadXMLFile(boConfig.getNgtHome() + File.separator + fileName);
                xmlhand = new ngtXMLHandler( doc.getDocumentElement() );
                TaskProperties properties = null;
                ngtXMLHandler[] xnodes = xmlhand.getChildNodes();
                for (int i = 0; i < xnodes.length; i++) 
                {
                    properties = getProperties(xnodes[i]);
                    if(properties != null)
                    {
                        definition.put(properties.getName(),properties);
                        if(properties.isActive())
                        {
                            this.activeDefinition.put(properties.getName(),properties);                            
                        }
                    }                
                }     
                definitionLoaded = true;
            }            
        } 
        catch (Exception e) 
        {
            logger.severe( LoggerMessageLocalizer.getMessage("THIS_INDICATES_A_BAD_DEPLOYMENT_OR_INEXISTENT_TASK_FILE")+" : " + boConfig.getNgtHome() + File.separator + fileName);
        }
    }
    private TaskProperties getProperties(ngtXMLHandler node)
    {
        TaskProperties properties = null;
        if(node != null)
        {            
            properties = new TaskProperties();               
            properties.setName(node.getAttribute("name", ""));
            properties.setVersion(node.getAttribute("version", ""));
            properties.setClassName(node.getAttribute("class", ""));            
            properties.setActive(node.getAttribute("active", ""));
        }
        return properties;
    }    
    public Enumeration getTaksNames()
    {        
        return this.definition.keys();
    }
    public Enumeration getActiveTasksNames()
    {        
        return this.activeDefinition.keys();
    }    
    public ITask getClass(String migration)
    {        
        return (ITask)getObject(getClassName(migration));
    }
    public boolean haveActiveMigrations()
    {
        boolean result = false;
        if(this.activeDefinition.size() > 0)
        {
            result = true;
        }
        return result;
    }
    private String getClassName(String migration)
    {        
        return ((TaskProperties)this.definition.get(migration)).getClassName();
    } 
    private static Object getObject(String className )
    {
        Object object = null;
        try
        {
            Class classRef = Class.forName( className );
            object = classRef.newInstance();                       
        }
        catch (InstantiationException e)
        {
            logger.severe( null, e ); 
        }
        catch (IllegalAccessException e)
        {
            logger.severe( null, e );
        }
        catch (ClassNotFoundException e)
        {
            logger.severe( LoggerMessageLocalizer.getMessage("THIS_INDICATES_A_BAD_DEPLOYMENT_OR_INEXISTENT_TASK_FILE")+" : " + className , e );            
        }        
        return object;
    }     
    public class TaskProperties 
    {
        private String name = null;
        private String version = null;
        private String className = null;    
        private boolean active = false;   
        
        public String getName()
        {
            return this.name;
        }
        public String getVersion()
        {
            return this.version;
        }
        public String getClassName()
        {
            return this.className;
        }
        public void setName(String name)
        {
            this.name = name;
        }
        public void setVersion(String version)
        {
            this.version = version;
        }
        public void setClassName(String className)
        {
            this.className = className;
        }
        public void setActive(String active)
        {
            if("y".equalsIgnoreCase(active) ||
               "yes".equalsIgnoreCase(active) ||
               "true".equalsIgnoreCase(active) )
            {
                this.active = true;          
            }        
        }    
        public boolean isActive()
        {
            return this.active;
        }        
    }
}