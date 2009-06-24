/*Enconding=UTF-8*/
package netgest.bo.builder;
import java.io.File;
import java.util.Hashtable;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boPathProvider 
{
    protected static Hashtable p_filesPath; 
    /**
     * 
     * @since 
     */
    public boPathProvider(Hashtable pathTable)
    {
        this.p_filesPath = pathTable;
    }
    
    public boPathProvider()
    {
        if( p_filesPath == null )
            p_filesPath = new Hashtable();
    }
    
    public void put(File xfile)
    {
        p_filesPath.put( xfile.getName().toLowerCase(),  xfile.getParent() + File.separator );
    }
    
    public void put(File[] xfiles)
    {
        for(int i = 0; i < xfiles.length; i++)
        {
            if(xfiles[i].getName().toLowerCase().endsWith( boBuilder.TYPE_BO ) 
                    || xfiles[i].getName().toLowerCase().endsWith( boBuilder.TYPE_STATE )
                    || xfiles[i].getName().toLowerCase().endsWith( boBuilder.TYPE_DS )
                    || xfiles[i].getName().toLowerCase().endsWith( boBuilder.TYPE_LOV )
                    || xfiles[i].getName().toLowerCase().endsWith( boBuilder.TYPE_INTERFACE )) 
            {
                p_filesPath.put( xfiles[i].getName().toLowerCase(),  xfiles[i].getParent() + File.separator );
            }
        }
    }

//bo.xml    
    public String getBODirectory(String boname)
    {
        return (String)p_filesPath.get((boname + boBuilder.TYPE_BO).toLowerCase());
    }

    public String getBOPath(String boname)
    {
        String ret = (String)p_filesPath.get((boname + boBuilder.TYPE_BO).toLowerCase());
        if( ret == null )
        {
            ret = (String)p_filesPath.get((boname + boBuilder.TYPE_INTERFACE).toLowerCase());
        }
        return ret + boname + boBuilder.TYPE_BO;
    }
    
    public File getBOFile(String boname)
    {
        return new File(getBOPath(boname));
    }
//lov.xml    
    public String getLovDdirectory(String boname)
    {
        return (String)p_filesPath.get((boname + boBuilder.TYPE_LOV).toLowerCase());
    }

    public String getLovPath(String boname)
    {
        return (String)p_filesPath.get((boname + boBuilder.TYPE_LOV ).toLowerCase()) + boname + boBuilder.TYPE_LOV;
    }
    
    public File getLovFile(String boname)
    {
        return new File(getLovPath(boname));
    }
//state.xml    
    public String getStateDirectory(String boname)
    {
        return (String)p_filesPath.get((boname + boBuilder.TYPE_STATE).toLowerCase());
    }

    public String getStatePath(String boname)
    {
        return (String)p_filesPath.get((boname + boBuilder.TYPE_STATE).toLowerCase()) + boname + boBuilder.TYPE_STATE;
    }
    
    public File getStateFile(String boname)
    {
        return new File(getStatePath(boname));
    }
//ds.xml    
    public String getSCDirectory(String boname)
    {
        return (String)p_filesPath.get((boname + boBuilder.TYPE_SC).toLowerCase());
    }

    public String getSCPath(String boname)
    {
        return (String)p_filesPath.get((boname + boBuilder.TYPE_SC).toLowerCase()) + boname + boBuilder.TYPE_SC;
    }
    
    public File getSCFile(String boname)
    {
        return new File(getSCPath(boname));
    }
    
//normal no treatment    
    public String getPath(String boname)
    {
        return (String)p_filesPath.get((boname));
    }    
}