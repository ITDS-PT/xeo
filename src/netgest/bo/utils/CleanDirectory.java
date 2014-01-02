package netgest.bo.utils;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;

public class CleanDirectory
{
    private static Hashtable dirs = new Hashtable();

    private static Logger logger = Logger.getLogger(CleanDirectory.class);           
    
    public static void main( String[] args )
    {
        run( new File("\\\\lusimagem2\\c$\\Documents and Settings\\Administrator.LUSITANIA\\Local Settings\\Temp\\ngtbo") );
    }
    
    public static void run( File dir )
    {
        try
        {
            long lastRun = 0;
            Long l_lastRun = (Long)dirs.get( dir.getAbsolutePath() );
            if( l_lastRun == null  )
            {
                dirs.put( dir.getAbsolutePath(), new Long( System.currentTimeMillis() ) );
            }
            else
            {
                lastRun = l_lastRun.longValue();
            }
        
            if( System.currentTimeMillis() - lastRun > (14400000) )
            {
                lastRun = System.currentTimeMillis();
                dirs.put( dir.getAbsolutePath(), new Long( lastRun ) );
                
                try
                {
                    cleanDir( dir );                
                }
                catch (Exception e)
                {
                    logger.severe( LoggerMessageLocalizer.getMessage("ERROR_DELETING_TEMPORARY_FILES1"), e );
                }
            }
        }
        catch (Exception e)
        {
            logger.severe( LoggerMessageLocalizer.getMessage("ERROR_DELETING_TEMPORARY_FILES2"), e );
        }
    }
    
    public static void cleanDir( File dir )
    {
        File[] files = dir.listFiles(  );
        
        long time = System.currentTimeMillis();
        
        for (int i = 0;files != null && i < files.length; i++) 
        {
            if( time - files[i].lastModified() > (259200000) )
            {
                boolean deleted;
                Date lastModified = new Date( files[i].lastModified() );
                
                if( files[i].isDirectory() )
                {  
                    deleteDir( files[i], 0 );
                }
                deleted = files[i].delete();
                if( i % 10 == 0 ) 
                    System.out.println( i + " - " + String.valueOf( lastModified ) + " - " + files[i].getName() + " - " + deleted );
            }
        }
    }
    
    
    public static void deleteDir( File dir, int deep )
    {
        if( deep > 3 )
        {
            return;    
        }
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) 
        {
            if( files[i].isDirectory() )
            {
                deleteDir( files[i], ++deep );
            }
            files[i].delete();
        }
    }
/*    
    public static class MyFile
    {
        public File     file;

        public MyFile( File file )
        {
            this.file = file;
            this.lastModified = file.lastModified();
        }
    }
*/    
}