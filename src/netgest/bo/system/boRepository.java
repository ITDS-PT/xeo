/*Enconding=UTF-8*/

package netgest.bo.system;
import java.sql.Connection;

import java.util.Properties;

import netgest.bo.data.Driver;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boRepository
{

    private boSession p_bosession;
    
    private Driver p_datadriver;
    private Driver p_defdriver;
    
    private String p_username;
    private String p_password;
    private String p_drivername;
    private String p_schema;
    private boApplication p_app;
    private String p_name;
    
    private String p_parent;

    /**
     * 
     * @Company Enlace3
     * @since 
     */
     
     public static final boRepository getRepository(boApplication app, String boreposname )
     {
         Properties[] pros = app.getApplicationConfig().getRepositories();
         for (int i = 0; i < pros.length ; i++) 
         {
            if( pros[i].getProperty("Name") != null && pros[i].getProperty( "Name" ).equalsIgnoreCase( boreposname ) )
            {
                String userName      = pros[i].getProperty("UserName");
                String password      = pros[i].getProperty("Password");
                String dataSource    = pros[i].getProperty("DataSource");
                String dataSourcedef = pros[i].getProperty("DataSourceDef");
                String parent        = pros[i].getProperty("Parent");
                String schema        = pros[i].getProperty("Schema");
                if( dataSourcedef == null )
                { 
                    dataSourcedef = dataSource;              
                }
                return new boRepository(boreposname, app , userName, password, schema , dataSource, dataSourcedef, parent );
            }
         }
         return  null;         
     }

     public static final boolean existsRepository(boApplication app, String boreposname )
     {
         Properties[] pros = app.getApplicationConfig().getRepositories();
         for (int i = 0; i < pros.length ; i++) 
         {
            if( pros[i].getProperty("Name") != null && pros[i].getProperty( "Name" ).equalsIgnoreCase( boreposname ) )
            {
                return true;
            }
         }
         return  false;         
     }

     public static final boRepository getDefaultRepository(boApplication app)
     {
        return getRepository(app, "default");
     }

     public static final String getDefaultSchemaName(boApplication app)
     {
        return getRepository(app, "default").getSchemaName();
     }

     public static final boRepository[] getAllRepositories(boApplication app)
     {
         Properties[] pros = app.getApplicationConfig().getRepositories();
         boRepository[] toRet = new boRepository[pros == null ? 0:pros.length];
         String userName, password, dataSource, dataSourcedef, parent, schema, name;
         for (int i = 0; i < pros.length ; i++) 
         {
            name      = pros[i].getProperty("Name");
            userName      = pros[i].getProperty("UserName");
            password      = pros[i].getProperty("Password");
            dataSource    = pros[i].getProperty("DataSource");
            dataSourcedef = pros[i].getProperty("DataSourceDef");
            parent        = pros[i].getProperty("Parent");
            schema        = pros[i].getProperty("Schema");
            if( dataSourcedef == null )
            { 
                dataSourcedef = dataSource;              
            }
            toRet[i] = new boRepository(name, app , userName, password, schema , dataSource, dataSourcedef, parent );
         }
         return  toRet;         
     }


    protected boRepository(String repname, boApplication app , String userName, String password,String schema, String dataSourceName, String dataSourceDefName, String parent )
    {
        p_username   = userName;
        p_password   = password;
        p_drivername = dataSourceName;
        if(dataSourceName != null && !"".equals(dataSourceName))
        {
            p_datadriver = app.getDriverManager().getDriver( dataSourceName );
        }
        else
        {
            if(parent != null)
            {
                p_datadriver = boRepository.getRepository(app, parent).getDriver();
            }
        }
        p_schema     = schema;
        p_name       = repname;
        p_parent     = parent;
        p_app = app;
        
        if( dataSourceDefName != null && !"".equals(dataSourceName))
        {
            p_defdriver = app.getDriverManager().getDriver( dataSourceDefName );
        }
        else
        {
            if(parent != null)
            {
                p_defdriver = boRepository.getRepository(app, parent).getDefDriver();
            }
            else
            {
                p_defdriver = p_datadriver;
            }
        }
    }
    
    public Connection getConnection()
    {
        if( p_username != null )
        {
            return p_datadriver.getConnection( p_username, p_password );
        }
        else
        {
            return p_datadriver.getConnection( );
        }
    }
    public Connection getDedicatedConnection()
    {
        if( p_username != null )
        {
            return p_datadriver.getDedicatedConnection( p_username, p_password );
        }
        else
        {
            return p_datadriver.getDedicatedConnection();
        }
    }

    public Connection getDedicatedConnectionDef()
    {
        if( p_username != null )
        {
            return p_datadriver.getDedicatedConnection( p_username, p_password );
        }
        else
        {
            return p_datadriver.getDedicatedConnection();
        }
    }
    
    public Connection getConnectionDef()
    {
        if( p_username != null )
        {
            return p_defdriver.getConnection( p_username, p_password );
        }
        else
        {
            return p_defdriver.getConnection( );
        }
    }
    
    public Driver getDefDriver()
    {
        return p_defdriver;
    }
    
    public Driver getDriver()
    {
        return p_datadriver;
    }
    
    public String getName()
    {
        return p_name;
    }

    public String getSchemaName()
    {
        return p_schema;
    }

    public String getUserName()
    {
        return p_username;
    }

    public String getPassword()
    {
        return p_password;
    }
    
    public boRepository getParentRepository(  )
    {
        boRepository ret = null;
        if( p_parent != null && p_parent.length() > 0 )
        {
            if(this.p_bosession == null)
            {
                if(p_app.getSessions().getActiveSessions() != null && 
                    p_app.getSessions().getActiveSessions().length > 0)
                {
                    p_bosession = p_app.getSessions().getActiveSessions()[0];
                }
                else
                {
                    return null;
                }
            }
            ret = getRepository( this.p_bosession.getApplication(), p_parent );
        }
        return ret;
    }
    
    public String getDeploymentDir()
    {
        return p_app.getApplicationConfig().getDeploymentDir();
    }

    public String getDefinitionDir()
    {
        return p_app.getApplicationConfig().getDefinitiondir();
    }
}