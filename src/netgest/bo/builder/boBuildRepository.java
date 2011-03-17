package netgest.bo.builder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import netgest.bo.boConfig;
import netgest.bo.data.Driver;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.boRepository;

import netgest.bo.system.Logger;



public class boBuildRepository
{

    private static Logger logger = Logger.getLogger( boBuildRepository.class );

    public static final char VERSION_SEPARATOR = '$';


    private static Hashtable packagesHash 					= null;
    private static Hashtable filesHash    					= null;
    private static Hashtable allFiles     					= null;
    private static Hashtable<String,RepositoryFile> allTranslations 	= null;
    

    private boRepository    p_repository;
    private File            deployDir;
    private boolean integrateWithXEOStudioBuilder = false;

    public boBuildRepository(boRepository repository)     		
    {
        p_repository = repository;
        if( packagesHash == null )
        {
            packagesHash 		= new Hashtable();
            filesHash    		= new Hashtable();
            allFiles     		= new Hashtable();
            allTranslations 	= new Hashtable<String, RepositoryFile>();
            refresh();
        }
    }

    public boBuildRepository(boRepository repository, 
    		boolean integrateWithXEOStudioBuilder)
    {
    	this.integrateWithXEOStudioBuilder = integrateWithXEOStudioBuilder;
        p_repository = repository;
        if( packagesHash == null )
        {
            packagesHash = new Hashtable();
            filesHash    = new Hashtable();
            allFiles     = new Hashtable();
            allTranslations 	= new Hashtable<String, RepositoryFile>();
            refresh();
        }
    }    

    public static void clearCache()
    {
        packagesHash = null;
        filesHash    = null;
        allFiles     = null;
        allTranslations = null;
    }

    public static void main( String[] args  )
    {
        (new boBuildRepository(null)).refresh();
    }

    public void refresh()
    {

        File xfile = new File( boConfig.getDefinitiondir() );
        deployDir  = new File( boConfig.getDeploymentDir() );

        RepositoryPackage pack = new RepositoryPackage();
        pack.packageId = "";
        pack.packageName = "";
        pack.packageVersion = "";
        putPackage( pack );
        loadDir( xfile, pack.packageId );

        buildAllFilesTable(  );
    }

    private void buildAllFilesTable(  )
    {
        Enumeration packEnum = packagesHash.elements();
        while( packEnum.hasMoreElements() )
        {
            RepositoryPackage pack = (RepositoryPackage)packEnum.nextElement();
            Enumeration oEnum = pack.packageFiles.elements();
            while( oEnum.hasMoreElements() )
            {
                RepositoryFile file = (RepositoryFile)oEnum.nextElement();
                if( allFiles.containsKey( file.id ) )
                {
                    if( file.id.endsWith( boBuilder.TYPE_BO )
                        ||
                        file.id.endsWith( boBuilder.TYPE_INTERFACE )
                    )
                    {
                        logger.warn(LoggerMessageLocalizer.getMessage("MULIPLE_OBJECTS_WITH_SAME_NAME_FOUND_IN_DIF_PACK_THIS_ERROR_WAS_FIRED_")+" ["+file.filePath.getAbsolutePath()+"], "+LoggerMessageLocalizer.getMessage("CHANGES_MAY_NOT_BE_DETECTED_OR_WRONG_FILE_MAY_BE_DEPLOYED"));
                    }
                }
                allFiles.put( file.id, file );
                if ( file.id.endsWith("properties"))
                	allTranslations.put(file.id, file);
            }
        }
    }

    public File[] getXMLFilesFromDefinition()
    {
        return getXMLFiles();
    }

    public ArrayList getPackages()
    {
        ArrayList ret = new ArrayList( packagesHash.size() );
        Enumeration oEnum = packagesHash.keys();
        while( oEnum.hasMoreElements() )
        {
            String pack = (String)oEnum.nextElement();
            ret.add( pack );
        }
        return ret;
    }

    public File[] getXMLFiles()
    {
        Vector ret = new Vector( allFiles.size() );
        Enumeration oEnum = allFiles.elements();
        while( oEnum.hasMoreElements() )
        {
            RepositoryFile file = (RepositoryFile)oEnum.nextElement();
            ret.add( file.filePath );
        }
        return (File[])ret.toArray( new File[ ret.size() ] );
    }

    public File getDataSourceFileFromDefinition( String name )
    {
        return getFile( name, boBuilder.TYPE_DS );
    }

    public File getDataSourceFile( String name )
    {
        return getFile( name, boBuilder.TYPE_DS );
    }


    public File getXMLFileFromDefinition( String name )
    {
        return getFile( name, boBuilder.TYPE_BO );
    }


    public File getXMLFile(String objName)
    {
        return getFile( objName, boBuilder.TYPE_BO );
    }

    public File getFile( String name, String type )
    {
        RepositoryFile file = (RepositoryFile)getFile( name + type );
        if( file != null ) return file.filePath;
        return null;
    }
    public RepositoryFile getFile( String id )
    {
        RepositoryFile file = (RepositoryFile)allFiles.get( id );
        return file;
    }

    public void loadDir( File dir, String packageId )
    {
    	
        File[] dirFiles = dir.listFiles();
        
        Arrays.sort( dirFiles, 
            new Comparator<File>()
            {
                public int compare(File left, File right )
                {
                    return left.getName().compareTo( right.getName() );
                }
            }
        );

        RepositoryPackage filePackage = getPackage( packageId );
        for (int i = 0;dirFiles != null && i < dirFiles.length; i++)
        {
            File dirFile = dirFiles[i];
            
        	if( ".svn".equals( dirFile.getName() ) ) {
        		continue;
        	}
            
            if( dirFile.isDirectory() )
            {
                String dirName = dirFile.getName();
                String packVer= "";
                String pack   = dirName;
                if( dirName.indexOf( VERSION_SEPARATOR ) != -1 )
                {
                    pack    = dirName.substring( 0, dirName.indexOf( VERSION_SEPARATOR ) );
                    packVer = dirName.substring( dirName.indexOf( VERSION_SEPARATOR ) + 1 );
                }

                RepositoryPackage packObj = getPackage( dirName );
                if( packObj == null )
                {
                    packObj = new RepositoryPackage();
                    packObj.packageId       = dirName;
                    packObj.packageName     = pack;
                    packObj.packageVersion  = packVer;
                }
                putPackage( packObj );
                loadDir( dirFile, packObj.packageId );
            }
            else
            {
                if("ITeste.xeoimodel".equalsIgnoreCase( dirFile.getName() ) )
                {
                    boolean toBreak = true;
                }
                File deployFile     = new File( deployDir.getAbsolutePath() + File.separator + dirFile.getName() );
                RepositoryFile file = new RepositoryFile();
                file.packageName = filePackage.packageId;
                file.filePath       = dirFile;
                file.id             = dirFile.getName();
                if (this.integrateWithXEOStudioBuilder)
                {
                	long buildLastRun= boBuilder.getXEOStudioBuilderLastRun();
                	file.changed        = !deployFile.exists() || ( buildLastRun>0 && dirFile.lastModified() > buildLastRun );
                }
                else
                	file.changed        = !deployFile.exists() || ( deployFile.lastModified() < dirFile.lastModified() );
                addFileVersion( file, filePackage );
            }
        }
    }

    public RepositoryPackage getPackage( String id )
    {
        return (RepositoryPackage)packagesHash.get( id );
    }

    public void putPackage( RepositoryPackage pack )
    {
        packagesHash.put( pack.packageId, pack );
    }

    public void addFileVersion( RepositoryFile file, RepositoryPackage pack )
    {
        boolean isLastestVersion = true;
        Enumeration oEnum = packagesHash.elements();
        while( oEnum.hasMoreElements() )
        {
            RepositoryPackage scanObj = (RepositoryPackage)oEnum.nextElement();
            if( scanObj.packageName.equals( pack.packageName ) )
            {
                if( !scanObj.packageId.equals( pack.packageId ) )
                {
                    if( scanObj.packageVersion.compareTo( pack.packageVersion ) < 0  )
                    {
                        scanObj.packageFiles.remove( file.id );
                    }
                    else
                    {
                        isLastestVersion = false;
                    }
                }
            }
        }
        if( isLastestVersion )
        {
            pack.packageFiles.put( file.id, file );
        }
    }

    public String getSchemaName()
    {
        return p_repository.getSchemaName();
    }

    public File[] getFileTranslations(){
    	
    	Vector<File> ret = new Vector<File>();
        Enumeration oEnum = allTranslations.elements();
        while( oEnum.hasMoreElements() )
        {
            RepositoryFile file = ((RepositoryFile)oEnum.nextElement());
            ret.add( file.filePath );
        }
        return (File[])ret.toArray( new File[ ret.size() ]  );
    }

    public File[] getFilesToDeploy()
    {
        Vector ret = new Vector();
        Enumeration oEnum = allFiles.elements();
        while( oEnum.hasMoreElements() )
        {
            RepositoryFile file = ((RepositoryFile)oEnum.nextElement());
            if( file.id.indexOf("Ebo_PerfAnacom") != -1 )
            {
                boolean toBreak = true;
            }
            if( file.changed  )
            {
                ret.add( file.filePath );
            }
        }
        return (File[])ret.toArray( new File[ ret.size() ]  );
    }


    public Driver getDefDriver()
    {
        return p_repository.getDefDriver();
    }

    public Driver getDriver()
    {
        return p_repository.getDriver();
    }
    public String getName()
    {
        return p_repository.getName();
    }

    public String getUserName()
    {
        return p_repository.getUserName();
    }

    public String getPassword()
    {
        return p_repository.getPassword();
    }

    public boRepository getParentRepository(  )
    {
        return p_repository.getParentRepository();
    }

    public boRepository getRepository(  )
    {
        return p_repository;
    }

    public String getDeploymentDir()
    {
        return p_repository.getDeploymentDir();
    }

    public String getDefinitionDir()
    {
        return p_repository.getDefinitionDir();
    }


    public static class RepositoryPackage
    {
        public String       packageId;
        public String       packageName;
        public String       packageVersion;
        public Hashtable    packageFiles = new Hashtable();
    }

    public static class RepositoryFile
    {
        public String       packageName;
        public String       id;
        public File         filePath;
        public boolean      changed;
    }
    
   

}