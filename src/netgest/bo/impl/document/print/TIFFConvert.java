package netgest.bo.impl.document.print;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;
import netgest.utils.IOUtils;
import netgest.utils.ngtXMLUtils;
import netgest.utils.ngtXMLHandler;
import oracle.xml.parser.v2.XMLElement;
import org.w3c.dom.Node;
import netgest.bo.system.boApplication;
import netgest.bo.boConfig;

public class TIFFConvert 
{
    public TIFFConvert()
    {
    }
    
    protected static String getTMPDirectory() 
    {
        String tmp = System.getProperty("java.io.tmpdir");
        File f = new File(tmp+File.separator+"XEOPREVIEWCACHE");
        if (!f.exists())
            f.mkdir();
        return tmp+File.separator+"XEOPREVIEWCACHE";
    }
    
    static String getTIFFDirectory() throws RuntimeException
    {
        ngtXMLHandler xnode;       
        try {
            boApplication.getApplicationFromStaticContext("XEO").addAContextToThread();
            ngtXMLHandler root = new ngtXMLHandler(ngtXMLUtils.loadXMLFile( boConfig.getNgtHome()+"boconfig.xml" ).getDocumentElement());
            xnode = root.getChildNode("tiff").getChildNode("path");
        } catch (Exception e) {
            xnode = null;   
        }
        
        if (xnode == null) 
        {
            throw new RuntimeException("Não foi indicado no ficheiro boconfig.xml a localização do conversor para Tiff.");
        }
               
        return xnode.getText();
    }

    public synchronized static File converIFile( iFile file, long docBoui)
    {
        return converIFile( file, docBoui, Boolean.TRUE);
    }
    public synchronized static File converIFile( iFile file, long docBoui, Boolean useCache )
    {
        try
        {           
            File dest = new File(getTMPDirectory() + File.separator + "tiff"+docBoui + "_" + file.getName() );
            File tiffFile = new File(getTMPDirectory() + File.separator + dest.getName().substring( 0, dest.getName().lastIndexOf('.') ) + "_0001.bmp" );
            
            File cache =  null;
            if(useCache.booleanValue())
            {
                cache =  CacheFind(tiffFile);
            }
            
            if (cache != null)
                return cache;

            if (CacheSize() >= 100)
                CacheDelete(1);

            
            byte[] buffer = new byte[8192];
            int    bread  = 0;
            InputStream is = file.getInputStream();
            FileOutputStream out = new FileOutputStream( dest );
            while( (bread = is.read( buffer )) > 0 )
            {
                out.write( buffer,0,bread );
            }
            out.close();
            is.close();
            
            String fileName = dest.getAbsolutePath();
            String programDef = getTIFFDirectory();
            fileName = fileName.replaceAll("\\\\", "\\\\\\\\");
            programDef = programDef.replaceFirst("#FILENAME#", fileName );
            
            
            Process proc = Runtime.getRuntime().exec(programDef);
            proc.waitFor();
            dest.delete();
            
            return tiffFile;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static int CacheSize()
    {
        File cache = new File(getTMPDirectory());
        File[] files = cache.listFiles(new Filtro(".bmp"));
        return files.length;
    }
    
    public static void CacheDelete(int num)
    {
        File cache = new File(getTMPDirectory());
        File[] files = cache.listFiles(new Filtro(".bmp"));
        File[] toDelete = new File[num];
        
        for (int i = 0; i < files.length; i++) 
        {
            if (i < num)
                toDelete[i] = files[i];
            else
            {
                for (int j = 0; j < toDelete.length; j++)
                {
                    if (toDelete[j].lastModified() > files[i].lastModified())
                    {
                        toDelete[j] = files[i];
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < toDelete.length; i++)
            toDelete[i].delete();
    }
    
    public static File CacheFind(File f)
    {
        return f.exists()?f:null;
    }
    
    public static boolean CacheDelete(long docBoui)
    {
        File cache = new File(getTMPDirectory());
        File[] files = cache.listFiles(new Filtro(".bmp"));
        
        for (int i = 0; i < files.length; i++) 
        {
            if (files[i].getName().indexOf(""+docBoui) != -1)
            {
                return files[i].delete();
            }
        }
        return false;
    }
    
}