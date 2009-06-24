/*Enconding=UTF-8*/
package netgest.bo;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boApplicationConfig;
import java.util.Properties;
import netgest.utils.ngtXMLHandler;

public class boConfig
{
    
    public static final boApplicationConfig getApplicationConfig()
    {
        return boApplication.currentContext().getApplication().getApplicationConfig();
    }


    public static final String getDefinitiondir()
    {
        return getApplicationConfig().getDefinitiondir();
    }

    public static final String getUiDefinitiondir()
    {
        return getApplicationConfig().getUiDefinitiondir();
    }

    public static final String getDeployJspDir()
    {
        return getApplicationConfig().getDeployJspDir();
    }

    public static final String getDeploymentclassdir()
    {
        return getApplicationConfig().getDeploymentclassdir();
    }

    public static final String getCompilerdir()
    {
        return getApplicationConfig().getCompilerdir();
    }
    
    public static final boolean isDeveloperMode()
    {
        return getApplicationConfig().isDeveloperMode();
    }
    public static final String getDeploymentsrcdir()
    {
        return getApplicationConfig().getDeploymentsrcdir();
    }

    public static final String getDeploymentDir()
    {
        return getApplicationConfig().getDeploymentDir();
    }

    public static final String getNgtHome()
    {
        return getApplicationConfig().getNgtHome();
    }

    public static final String getTemplatesDir()
    {
        return getApplicationConfig().getTemplatesDir();
    }

    public static final boConfigRepository getConfigRepository(String name)
    {
        return getApplicationConfig().getConfigRepository( name );
    }

    public static final String getDefaultRepository()
    {
        return getApplicationConfig().getDefaultRepository();
    }

    public static final boolean aspmodeOn()
    {
        return getApplicationConfig().aspmodeOn();
    }

    public static final String getBrowserName() throws boRuntimeException
    {
        return getApplicationConfig().getBrowserName();
    }

    public static final String getBrowserDirPrefix() throws boRuntimeException
    {
        return getApplicationConfig().getBrowserDirPrefix();
    }

    public static final String getBrowserTheme() throws boRuntimeException
    {
        return getApplicationConfig().getBrowserTheme();
    }

    public static final int getBrowserCode()
    {
        return getApplicationConfig().getBrowserCode();
    }
    
    public static final String getSystemEncoding()
    {
        return getApplicationConfig().getSystemEncoding();
    }

    public static final String getEncoding()
    {
        return getApplicationConfig().getEncoding();
    }

    public static final String getTableSpace()
    {
        return getApplicationConfig().getTableSpace();
    }

    public static final void refresh( String ngtHome )
    {
        getApplicationConfig().refresh( ngtHome );
    }

    public static final  Properties getAuthentication()
    {
        return getApplicationConfig().getAuthentication();
    }

    public static final  Properties getMailConfig()
    {
        return getApplicationConfig().getMailConfig();
    }
    
    public static final  String getMailPrefix()
    {
        String prfx = getApplicationConfig().getMailConfig().getProperty("xeoPrefix");
        if(prfx == null || "".equals(prfx))
        {
            return "XEO";
        }
        return prfx.trim();
    }
    
    public static final  Properties getFaxConfig()
    {
        return getApplicationConfig().getFaxConfig();
    }

    public static final  Properties getSecurityConfig()
    {
        return getApplicationConfig().getSecurityConfig();
    }
    
    public static final  Properties getContentMngmConfig()
    {
        return getApplicationConfig().getContentMngmConfig();
    }
    
    public static final  Properties getDocumentationConfig()
    {
        return getApplicationConfig().getDocumentationConfig();
    }

    public static final  Properties getWin32ClientConfig()
    {
        return getApplicationConfig().getWin32ClientConfig();
    }

    public static final String getWebContextRoot()
    {
        return getApplicationConfig().getWebContextRoot();
    }
    
    public static final Properties getWordTemplateConfig()
    {
        return getApplicationConfig().getWordTemplateConfig();
    }

    public static final ngtXMLHandler[] getWorkPlaces()
    {
        return getApplicationConfig().getWorkPlaces();
    }
    
    public static String getCScriptPath()
    {
        return getApplicationConfig().getCScriptPath();
    }

    public static String getVBProgPath()
    {
        return getApplicationConfig().getVBProgPath();
    }
}
