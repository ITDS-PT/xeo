/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;
import java.util.Hashtable;

import netgest.bo.boConfig;

import netgest.utils.StringUtils;

public class boClassLoader extends ClassLoader {

    private static final boConfig p_bocfg=new boConfig();
    private static Hashtable p_classes=new Hashtable();

    public boClassLoader(ClassLoader classloader) 
    {
        super(classloader);        
    }

    public static void releaseLoadedClasses() 
    {
        p_classes.clear();
    }
    
    protected Class boFindLoadedClass(String name) throws ClassNotFoundException 
    {
    
        Class ret = (Class)p_classes.get(name);
        return ret;
    }
                   
    public Class findClass(String name) throws ClassNotFoundException 
    {
        Class ret;
        String fname = p_bocfg.getDeploymentclassdir()+StringUtils.replacestr(name,".",File.separator)+".class";
        File xsrc = new File(fname);
        if(xsrc.exists()) {
            try {
                byte[] classb = new byte[(int)xsrc.length()];
                FileInputStream fi = new FileInputStream(xsrc);
                fi.read(classb,0,classb.length);
                ret = super.defineClass(name,classb,0,classb.length);
                p_classes.put(name,ret);
            } catch (IOException e) {
                throw new ClassNotFoundException(e.getMessage());
            }
        } else {
            ret = super.findClass(name);
        }
        return ret;
    }
    private byte[] getClassBytes(InputStream is) 
    {
        int br =0;
        try
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            while((br=is.read(buff))>0) 
            {
                os.write(buff,0,br);
            }
            return os.toByteArray();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e.getMessage());            
        }
    }
    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException
    {
        // TODO:  Override this java.lang.ClassLoader method
        if(name.startsWith("v1_0")) 
        {
            Class ret = this.boFindLoadedClass(name);
            if(ret==null) 
            {
                ret = findClass(name);
            }
            if(ret==null) throw new ClassNotFoundException(name);
            return ret;
        }
        Class ret = (Class)boFindLoadedClass(name);
        if(ret == null) 
        {
            byte[] x=  this.getClassBytes(this.getParent().getResourceAsStream(name));
            ret = super.defineClass(name,x,0,x.length);
        }
        return ret;
    }

    public Class loadClass(String name) throws ClassNotFoundException
    {
        // TODO:  Override this java.lang.ClassLoader method
        return loadClass(name,false);
    }

    public synchronized void clearAssertionStatus()
    {
        // TODO:  Override this java.lang.ClassLoader method
        super.clearAssertionStatus();
    }

    protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException
    {
        // TODO:  Override this java.lang.ClassLoader method
        return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
    }

    protected String findLibrary(String libname)
    {
        // TODO:  Override this java.lang.ClassLoader method
        return super.findLibrary(libname);
    }

    protected URL findResource(String name)
    {
        // TODO:  Override this java.lang.ClassLoader method
        return super.findResource(name);
    }

    protected Enumeration findResources(String name) throws IOException
    {
        // TODO:  Override this java.lang.ClassLoader method
        return super.findResources(name);
    }

    protected Package getPackage(String name)
    {
        // TODO:  Override this java.lang.ClassLoader method
        return super.getPackage(name);
    }

    public InputStream getResourceAsStream(String name)
    {
        // TODO:  Override this java.lang.ClassLoader method
        return super.getResourceAsStream(name);
    }

    public synchronized void setClassAssertionStatus(String className, boolean enabled)
    {
        // TODO:  Override this java.lang.ClassLoader method
        super.setClassAssertionStatus(className, enabled);
    }

    public synchronized void setDefaultAssertionStatus(boolean enabled)
    {
        // TODO:  Override this java.lang.ClassLoader method
        super.setDefaultAssertionStatus(enabled);
    }

    public synchronized void setPackageAssertionStatus(String packageName, boolean enabled)
    {
        // TODO:  Override this java.lang.ClassLoader method
        super.setPackageAssertionStatus(packageName, enabled);
    }
}