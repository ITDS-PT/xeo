package netgest.io;

import com.develop.jawin.COMException;
import com.develop.jawin.UnknownPtr;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.log4j.Logger;

public class AppxiFile implements iFile
{  

    Logger logger = Logger.getLogger(AppxiFile.class);
    
    public static final String IFILE_SERVICE_NAME = "appx";
    
    private AppxiFileProvider provider=null;
    private String appxUriPath=null;
    private String filePath=null;
    private String fileName=null;
    private String appxPath=null;
    private String app=null;
    private String[] pageFiles;
    private boolean cached = false;
    private long docid=-1;
    private int pagenum=-1;
    
    private static final int TYPE_PDF = 7;
    private static final int TYPE_HTML = 5;
    private static final int TYPE_JPG = 32769;
    private static final int TYPE_BMP = 4097;
    private static final int TYPE_TXT = 0;
    private static final int TYPE_GIF = 24577;
    private static final int TYPE_OLE = 1; // Se for xls tambem devolve 1, mas não existe xls no appx
    private static final int TYPE_TIFF1 = 12289;
    private static final int TYPE_TIFF2 = 12290;
    
    
    private static String tmpDirectory = null;
    
    protected static String getTMPDirectory() 
    {
        if( tmpDirectory == null )
        {
            
            String tmp = System.getProperty("java.io.tmpdir");
            if( !tmp.endsWith( File.separator ) )
            {
                tmp += File.separator;
            }
            
            File f = new File( tmp +"XEOAPPXCACHE" );
            if (!f.exists())
            {
                f.mkdir();
            }
            tmpDirectory = f.getAbsolutePath();
        }
        return tmpDirectory;
    }
    
    public static int CacheSize()
    {
        File cache = new File(getTMPDirectory());
        File[] files = cache.listFiles();
        return files==null?0:files.length;
    }
    
    public static void CacheDelete(int num)
    {
        File cache = new File(getTMPDirectory());
        File[] files = cache.listFiles();
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


    public AppxiFile(AppxiFileProvider provider,String path)
    {
        this(provider, path, 1);
    }    
    
        
    public AppxiFile(AppxiFileProvider provider,String path, int pagnum)
    {
        init( provider , path, pagnum, false );
    }
    
    public void init(AppxiFileProvider provider,String path, int pagnum,boolean cacheFile)
    {
        this.pagenum = pagnum;
        appxUriPath = path;
        String p = path;
        cached = cached || cacheFile;
        
        try {
            if (this.provider == null)
                this.provider = provider;
            this.filePath = path.substring(0, path.lastIndexOf('/')+1);
            
            //path entra com //appx/sinistro/12345
            if (p.endsWith("/"))
                p = p.substring(0, p.length()-1);
                
            // temp fica com sinistro/12345
            String temp = p.substring(2+IFILE_SERVICE_NAME.length()+1, p.length());
            // app fica com sinistro
            app = temp.substring(0,temp.indexOf('/'));
            // temp fica com 12345
            temp = temp.substring(temp.indexOf('/')+1, temp.length());
            // docid fica com 12345
            docid = Long.parseLong(temp);
            
            fileName = "";
            if( pageFiles == null )
            {
                pageFiles =  provider.getAPI().getPagesFiles( docid );
            }
            if(pageFiles != null)
            {
                this.appxPath = pageFiles[pagnum-1] ;
                if (CacheSize() >= 100)
                  CacheDelete(1);
                
                int commaPos = this.appxPath.lastIndexOf(';');
                int page     = 0;
                if( commaPos != -1 )
                {
                    page = Integer.valueOf( this.appxPath.substring(commaPos + 1) ).intValue();
                    // New APPX accepts the page number on the path, and doesn't work without it, so, removing
                    //  the old solution
                    //this.appxPath = this.appxPath.substring( 0, commaPos );
                }

                AppxThread appxt = new AppxThread();
                appxt.appxPath = this.appxPath;
                appxt.cacheFile = cacheFile;
                appxt.pagnum = String.valueOf( pagnum );
                
                appxt.start();
                int counter = 0;
                while( appxt.isAlive() )
                {   
                    counter++;
                    Thread.sleep(10);
                    if( counter > 1000 )
                    {
                        appxt.stop();
                        appxt.interrupt();
                        break;   
                    }
                }
                this.fileName = appxt.fileName;
                this.filePath = appxt.filePath;
                
                if( page > 0 && this.fileName.toLowerCase().endsWith(".pdf") && cacheFile )
                {
                    // Load existing PDF
                    String pdfPath = this.appxPath;
                    if( commaPos != -1 )
                    {
                        pdfPath = this.appxPath.substring( 0, commaPos );
                    }
                    
                    PdfReader oReader = new PdfReader(pdfPath);
                    
                    // Get the specific page and load its size
                    Rectangle oRect = oReader.getPageSize(page);
                    
                    // Create a new Blank PDF
                    Document  oPdfOut = new Document(new Rectangle(oRect.getWidth(), oRect.getHeight()));
                    PdfWriter oWriter = PdfWriter.getInstance(oPdfOut, new FileOutputStream(getTMPDirectory() + "\\" + this.fileName));
                    
                    oPdfOut.open();
                    
                    // Append the page to the PDF
                    if( oPdfOut.newPage() )
                    {
                        PdfContentByte  oCByte = oWriter.getDirectContent();
                        PdfImportedPage oPage  = oWriter.getImportedPage(oReader, page);
                        
                        oCByte.addTemplate(oPage, 0, 0);
                    }
                    
                    // Write the new PDF to disk
                    oPdfOut.close();
                     
                    // Close all references
                    oReader.close();
                    oWriter.close();
                    
                    // Update the fileName and filePath
                    //this.fileName = this.fileName;
                    this.filePath = getTMPDirectory() + "\\" + this.fileName;
                }
                
//                try
//                {
//                    Object x = ptr.invokeN( "getAppxDocument", new Object[] { this.appxPath, getTMPDirectory(), docid+"_"+pagnum } );
//                    i = Integer.parseInt(x.toString());
//                    switch (i)
//                    {
//                        case TYPE_BMP:  s = getTMPDirectory()+File.separator+docid+"_"+pagnum+".bmp";  break;
//                        case TYPE_PDF:  s = getTMPDirectory()+File.separator+docid+"_"+pagnum+".pdf";  break;
//                        case TYPE_JPG:  s = getTMPDirectory()+File.separator+docid+"_"+pagnum+".jpg";  break;
//                        case TYPE_TXT:  
//                            s = getTMPDirectory()+File.separator+docid+"_"+pagnum+".txt";  
//                            UnknownPtr ptrVc = new UnknownPtr("ApplicationXtender.ViewControl");
//                            ptrVc.invoke("DisplayImageFile", this.appxPath );
//                            ptrVc.invoke("AnnoLoad", "" );
//                            ptrVc.invoke( "SaveImage", s, new Integer(5), new Integer(0) );
//                            break;
//                        case TYPE_GIF:  s = getTMPDirectory()+File.separator+docid+"_"+pagnum+".gif";  break;
//                        case TYPE_HTML: s = getTMPDirectory()+File.separator+docid+"_"+pagnum+".htm";  break;
//                        case TYPE_OLE:
//                            String path_ = getTMPDirectory()+File.separator; 
//                            Object y = ptr.invokeN( "getDocFileName", new Object[] { this.appxPath, path_ } );
//                            s = y.toString(); 
//                            File fo = new File(s);
//                            this.fileName = fo.getName();
//                            this.filePath = fo.getAbsolutePath().substring(0,fo.getAbsolutePath().lastIndexOf("\\")+1);
//                            return;
//                        case TYPE_TIFF1: s = getTMPDirectory()+File.separator+docid+"_"+pagnum+".tiff"; break;
//                        case TYPE_TIFF2: s = getTMPDirectory()+File.separator+docid+"_"+pagnum+".tiff"; break;
//                        default:        s = getTMPDirectory()+File.separator+docid+"_"+pagnum+".bin"; break;
//                    }
//                }
//                finally
//                {
//                    if( ptr != null ) ptr.close();
//                }
//                
//                int commaPos = this.appxPath.lastIndexOf(';');
//                if( commaPos != -1 )
//                {
//                    this.appxPath = this.appxPath.substring( 0, commaPos );
//                }
//                
//                File fo = new File(s);
//                File fi = new File(this.appxPath);
//                FileOutputStream fos = new FileOutputStream(fo);
//                FileInputStream fis = new FileInputStream(fi);
//                byte[] b = new byte[(int)fi.length()];
//                fis.read(b);
//                fos.write(b);
//                fos.close();
//                fis.close();
//                this.fileName = fo.getName();
//                this.filePath = fo.getAbsolutePath().substring(0,fo.getAbsolutePath().lastIndexOf("\\")+1);
                
            }
        } 
       catch (Exception e)
        {
            throw new RuntimeException(e);    
        }    
    }
    
    public AppxiFile getPage( int page )
    {
        return new AppxiFile( getProvider(), this.appxUriPath , page );
    }
    
    public AppxiFile[] getPages()
    {
        
        AppxiFile[] ret = null;
        
        try
        { 
            if( pageFiles == null )
                pageFiles = getProvider().getAPI().getPagesFiles( this.docid );
            if( pageFiles != null )
            {
                ret   = new AppxiFile[ pageFiles.length ];
                for (int i = 0; i < pageFiles.length; i++) 
                {
                    ret[ i ] = getPage( i + 1 );
                }
            }
        }
        catch (COMException e)
        {
            throw new RuntimeException(e);
        }
        return ret;
   }
    
    
    public boolean  canRead()
    {
        throw new RuntimeException("canWrite:Not Implemented");    
    }
    
    public boolean  canWrite()
    {
        throw new RuntimeException("canWrite:Not Implemented");        
    }
    
    public boolean  createNewFile() throws IOException,iFilePermissionDenied
    {
        throw new RuntimeException("createNewFile:Not Implemented");        
    }
    
    public boolean  delete() throws iFilePermissionDenied
    {
        return true;
//        throw new RuntimeException("delete:Not Implemented");        
    }
    
    public boolean  exists()
    {
        if( !this.cached )  
            init( this.provider, this.appxUriPath, this.pagenum>0?pagenum:1, true );
        return (new File(this.filePath)).exists();
        
    }
    
    public String   getAbsolutePath()
    {
        if( !this.cached )  
            init( this.provider, this.appxUriPath, this.pagenum>0?pagenum:1, true );
        return filePath;
    }
    
    public String   getName()
    {
        if( !this.cached )  
            init( this.provider, this.appxUriPath, this.pagenum>0?pagenum:1, true );
        return this.fileName;
    }
    
    public String   getParent()
    {
        throw new RuntimeException("getParent:Not Implemented");        
    }
    
    public iFile    getParentFile()
    {
        throw new RuntimeException("getParentFile:Not Implemented");        
    }
    
    public String   getPath()
    {
        if( !this.cached )  
            init( this.provider, this.appxUriPath, this.pagenum>0?pagenum:1, true );
        return this.filePath;        
    }
    
    public boolean  isDirectory()
    {
        if( !this.cached )  
            init( this.provider, this.appxUriPath, this.pagenum>0?pagenum:1, true );
        return (new File(this.filePath)).isDirectory();        
    }
    
    public boolean  isFile()
    {
        if( !this.cached )  
            init( this.provider, this.appxUriPath, this.pagenum>0?pagenum:1, true );
        return (new File(this.filePath)).isFile();
    }
    
    public long     lastModified()
    {
        if( !this.cached )  
            init( this.provider, this.appxUriPath, this.pagenum>0?pagenum:1, true );
        return (new File(this.filePath)).lastModified();
    }
    
    public long     length()
    {
        if( !cached ) 
            init( this.provider, this.appxUriPath, this.pagenum>0?pagenum:1, true );
        return (new File(this.filePath)).length();
    }
    
    public String[] list() throws iFilePermissionDenied
    {
        throw new RuntimeException("list:Not Implemented");        
    }
    
    public String[] list(iFilenameFilter filter) throws iFilePermissionDenied
    {
        throw new RuntimeException("list:Not Implemented");        
    }
    
    public iFile[]  listFiles() throws iFilePermissionDenied
    {
        throw new RuntimeException("listFiles:Not Implemented");        
    }
    
    public iFile[]  listFiles(iFileFilter filter) throws iFilePermissionDenied
    {
        throw new RuntimeException("listFiles:Not Implemented");        
    }
    
    public boolean  mkdir() throws iFilePermissionDenied
    {
        throw new RuntimeException("mkdir:Not Implemented");        
    }
    
    public boolean  mkdirs() throws iFilePermissionDenied
    {
        throw new RuntimeException("mkdirs:Not Implemented");        
    }
    
    public boolean  renameTo(iFile newfile) throws iFilePermissionDenied
    {
        throw new RuntimeException("renameTo:Not Implemented");        
    }
    
    public boolean  setReadOnly() throws iFilePermissionDenied
    {
        throw new RuntimeException("setReadOnly:Not Implemented");        
    }
    
    public OutputStream getOutputStream() throws iFilePermissionDenied
    {

        
        throw new RuntimeException("getOutputStream:Not Implemented");     

    }
    
    public InputStream getInputStream() throws iFilePermissionDenied
    {
        try {
            if( !cached ) 
                init( this.provider, this.appxUriPath, this.pagenum>0?pagenum:1, true );
            File f = new File(this.getAbsolutePath());
            return (new FileInputStream(f));
        } catch(FileNotFoundException e) {
            throw(new RuntimeException(e.getMessage()));
        }
        
    }
    
    public boolean checkIn() throws iFilePermissionDenied
    {
        throw new RuntimeException("checkIn:Not Implemented");        
    }
    
    public boolean checkOut() throws iFilePermissionDenied
    {
        throw new RuntimeException("checkOut:Not Implemented");        
    }
    
    public boolean isCheckedIn()
    {
        throw new RuntimeException("isCheckedIn:Not Implemented");        
    }
    
    public boolean isCheckedOut()
    {
        throw new RuntimeException("isCheckedOut:Not Implemented");        
    }
    
    public boolean isVersioned()
    {
        throw new RuntimeException("isVersioned:Not Implemented");        
    }
    
    public boolean makeVersioned() throws iFilePermissionDenied
    {
        throw new RuntimeException("makeVersioned:Not Implemented");        
    }

    public void     setDescription(String description)
    {
        throw new RuntimeException("setDescription:Not Implemented");        
    }
    
    public String   getDescription()
    {
        throw new RuntimeException("getDescription:Not Implemented");        
    }
    
    public void     setAuthor(String author)
    {
        throw new RuntimeException("setAuthor:Not Implemented");        
    }
    
    public String   getAuthor()
    {
        throw new RuntimeException("getAuthor:Not Implemented");        
    }
    
    public void     setCategory(String author)
    {
        throw new RuntimeException("setCategory:Not Implemented");        
    }
    
    public String   getCategory()
    {
        throw new RuntimeException("getCategory:Not Implemented");        
    }
    
    public String   getURI()
    {
        return this.getAbsolutePath();        
    }
    
    public void     setVersionUser(String user)
    {
        throw new RuntimeException("setVersionUser:Not Implemented");        
    }
    
    public String   getVersionUser()
    {
        throw new RuntimeException("getVersionUser:Not Implemented");        
    }
    
    public void     setCheckOutUser(String user)
    {
        throw new RuntimeException("setCheckOutUser:Not Implemented");        
    }
    
    public String   getCheckOutUser()
    {
        throw new RuntimeException("getCheckOutUser:Not Implemented");        
    }
    
    public long     getVersion()
    {
        throw new RuntimeException("getVersion:Not Implemented");        
    }
    
    public long     getKey()
    {
        throw new RuntimeException("getKey:Not Implemented");        
    }
    
    public iFile    getCopy()
    {
        throw new RuntimeException("getCopy:Not Implemented");        
    }
    
    public static void main (String[] args) throws Exception
    {
                AppxiFileProvider provider = new AppxiFileProvider();
                provider.open("sinistro","imagem");
                AppxiFile appx = new AppxiFile(provider, "//appx/sinistro/55305/");
                InputStream fo = appx.getInputStream();
                

    }


    public void setProvider(AppxiFileProvider provider)
    {
        this.provider = provider;
    }


    public AppxiFileProvider getProvider()
    {
        return provider;
    }


    public void setDocid(long docid)
    {
        this.docid = docid;
    }


    public long getDocid()
    {
        return docid;
    }
    
    private class AppxThread extends Thread
    {
        String appxPath = null;
        String pagnum   = null;
        boolean cacheFile = false;
        
        String fileName = null;
        String filePath = null;
        
        public void run()
        {   
            UnknownPtr ptr = null;
            try
            {
                ptr = new UnknownPtr("XEO.Utils");
                String s="";
                String[] names = (String[])ptr.invokeN( "getAppxDocument", new Object[] { this.appxPath, getTMPDirectory(), docid+"_"+pagnum, Boolean.valueOf( cacheFile ) } );
                this.fileName  = names[0];
                this.filePath  = names[1];
            }
            catch (Exception e)
            {
                logger.error("Erro a obter ficheiro do Appx" , e );
            }
            ptr.close();
        }
    }

}