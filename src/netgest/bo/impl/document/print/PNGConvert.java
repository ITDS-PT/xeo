package netgest.bo.impl.document.print;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import netgest.io.iFile;

public class PNGConvert 
{
    
    public static Hashtable PNG_CAHCE = new Hashtable();
    public static long lastcleandoc;
        
    public static PNGDocument convertToPNG( String id, iFile ifile ) 
    {
        cleanDocs();    
        PNGDocument doc = (PNGDocument)PNG_CAHCE.get( id );
        if( doc == null || (System.currentTimeMillis() - doc.generated) > 15000 )
        {
            if( doc != null )
            {
                cleanDoc( doc );
            }
            try
            {
                String tmpDir =  System.getProperty("java.io.tmpdir")+System.currentTimeMillis() +"_"+id+"\\";
                (new File(tmpDir)).mkdirs();
                
                InputStream is = ifile.getInputStream();
                File outFile1 = File.createTempFile( "pvw", ifile.getName() );
                netgest.utils.IOUtils.copy( is, outFile1 );
                is.close();
                String ret = netgest.bo.impl.document.merge.gestemp.GtTemplate.runGDUtilsMethod( "getPng", 
                            new String[] {
                                outFile1.getAbsolutePath(), 
                                tmpDir
                            }
                            ,
                            60000
                        );
                outFile1.delete();
    
                doc = new PNGDocument();
                doc.dir     = tmpDir;
                doc.id      = id;
                doc.pages   = ret.split("\\|");
                
                PNG_CAHCE.put( id, doc );
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return doc;
    }
    
    private static void cleanDocs()
    {
        if( (System.currentTimeMillis() - lastcleandoc) > 60000 )
        {
            lastcleandoc = System.currentTimeMillis();
            synchronized( PNGConvert.class )
            {
                java.util.Enumeration enumDocs = PNG_CAHCE.elements();
                while( enumDocs.hasMoreElements() )
                {
                    PNGDocument doc = (PNGDocument)enumDocs.nextElement();
                    if( (System.currentTimeMillis() - doc.generated) > 30000 )
                    {
                        cleanDoc( doc );
                        PNG_CAHCE.remove( doc.id );
                    }
                }
            }
        }
    }
    
    
    private static final void cleanDoc( PNGDocument doc )
    {
        File dir = new File( doc.dir );
        if( dir != null && dir.exists() )
        {
            File[] files = dir.listFiles();
            for (int i = 0;files != null && i < files.length; i++) 
            {
                files[i].delete();
            }
            dir.delete();
        }
    }
    
    public static class PNGDocument
    {
        public String      id  = null;
        public String      dir = null;
        public String[]  pages = null;
        public long    generated = System.currentTimeMillis();
    }
}