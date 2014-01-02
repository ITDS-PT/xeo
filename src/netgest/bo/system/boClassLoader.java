package netgest.bo.system;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class boClassLoader extends ClassLoader
{

    private boApplication       application;
    private boApplicationConfig applicationConfig;
    private String              classDir;
    
    public boClassLoader( ClassLoader parent, boApplication boapp )
    {
        super( parent );
        this.application        = boapp;
        this.applicationConfig  = boapp.getApplicationConfig();
        this.classDir           = applicationConfig.getDeploymentclassdir();
    }
    
    protected synchronized Class findClass(String name) throws ClassNotFoundException 
    {
        Class ret = findLoadedClass( name );
        if( ret == null )
        {
            String file = name.replace( '.' , '/' ) + ".class";
            File xfile = new File( this.classDir + file  );
            if( xfile.exists() ) 
            {
                try
                {
                    byte[] classBytes = new byte[ (int)xfile.length() ];
                    FileInputStream fin = new FileInputStream( xfile );
                    fin.read( classBytes );
                    ret = super.defineClass( name, classBytes, 0, classBytes.length );
                }
                catch (FileNotFoundException e)
                {
                    // 
                }
                catch (IOException e)
                {
                    //
                }
            }
            if( ret == null )
            {
                ret = super.findClass( name );
            }
        }
        return ret;
    }

    
}