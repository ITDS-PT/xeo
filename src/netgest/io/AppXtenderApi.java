package netgest.io;
import com.develop.jawin.*;
import com.develop.jawin.win32.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.sql.*;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import netgest.bo.impl.document.print.GDOCXUtilsStub;
import netgest.bo.runtime.EboContext;
import com.otg.applicaxtender.AEXDBLib.*;
import netgest.utils.IOUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class AppXtenderApi 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    
    static final Logger logger = Logger.getLogger( AppXtenderApi.class );

    static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    static final SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yy");

    private String appx_userName;
    private String appx_password;
    
    private String appx_application;
    private String appx_dataSource;
    
    public static boolean useWs = true;
    
    public static Hashtable appXcache = new Hashtable();
    
    public AppXtenderApi( String userName, String password, String application, String dataSource )
    {

        this.appx_application = application;
        this.appx_dataSource  = dataSource;
        this.appx_userName = userName;
        this.appx_password = password;
     
        //TODO:Implement Interface LUSITANIA
        //CleanPoolObjects.checkStart();   
    }
    
    public File getFile( EboContext ctx, String query )
    {
        return null;   
    }
    
//    public static void main( String[] args )
//    {
//        try
//        {
//            AppXtenderApi obj = new AppXtenderApi("SYSOP","otg","SINISTRO","imagem");
//            String[] xx = obj.getPagesFiles( 237 );
//            for (int i = 0; i < xx.length; i++) 
//            {
//                System.out.println( xx[i] );
//            }
//            
//            obj = null;
//       }
//        catch (COMException e)
//        {
//            e.printStackTrace();   
//        }
//    }
    
    private long getDocId( EboContext ctx, String query, Object[] args ) throws SQLException
    {
        long ret = 0;
        Connection cn           = ctx.getConnectionData();
        PreparedStatement pstm  = cn.prepareStatement("SELECT DOCID FROM AEDT8 WHERE "+ query );
        for (int i = 0; i < args.length; i++) 
        {
            if( args[i] != null )
            {
                pstm.setObject( i + 1, args[i] );
            }
            else
            {
                pstm.setString( i + 1, null );
            }
        }
        
        ResultSet rslt          = pstm.executeQuery();
        if( rslt.next() )
        {
            ret = rslt.getLong( 1 );
        }
        rslt.close();
        pstm.close();
        return ret;
    }
    

    private static IDBConnection   axCon   = null;
    
    private long    CLEAN_CACHE_INTERVAL    = 600000;
    private long    LAST_CLEAN_CACHE        = System.currentTimeMillis();
    

    public String[] getPagesFiles( long docid ) throws COMException
    {
        if( docid == 0 ) return null;
        
        if( useWs )
        {
            try
            {
                if( System.currentTimeMillis() - LAST_CLEAN_CACHE > CLEAN_CACHE_INTERVAL )
                {
                    LAST_CLEAN_CACHE = System.currentTimeMillis();
                    synchronized( appXcache )
                    {
                        Enumeration enumcitems = appXcache.keys();
                        while( enumcitems.hasMoreElements() )
                        {
                            String keyItem = enumcitems.nextElement().toString();
                            CacheItem citem = (CacheItem)appXcache.get( keyItem );
                            if( citem.isExpired() )
                            {
                                appXcache.remove( keyItem );
                            }
                        }
                    }
                    
                }
            }
            catch( Exception e )
            {
                logger.error( "Erro a limpar cache de páginas", e );
            }

            try 
            { 
                String key = this.appx_application + "_" + docid;
                CacheItem citem = (CacheItem)appXcache.get( key );
                if( citem == null || citem.isExpired() )
                { 
                    citem = new CacheItem();
                    AppXtenderApiWSStub stub = new AppXtenderApiWSStub();
                    citem.pageFiles = stub.getPagesFiles( this.appx_application,new Long( docid ) );
                    appXcache.put( key, citem );
                }
                return citem.pageFiles;
            }
            catch( Exception e )
            {
                logger.error("Erro a obter páginas do Appx via WS",e);
            }
        }
        
       /* String[] ret;
        UnknownPtr ptr = new UnknownPtr("XEOAPPX.main");
        Object x = ptr.invokeN( "getPagesFiles", new Object[] 
            {
                new Long(docid), appx_userName, appx_password, appx_application, appx_dataSource
            }
            );
         ret = (String[]) x;   
         
         if (ret == null) 
         {
         throw new RuntimeException( "O DocID ["+docid+"] não tem páginas verfique por favor.");   
         }
        
        return ret;*/
        
        String[] ret = null;
        String erro = null;
        
        IApps           apps    = null;
        IAPP            app     = null;
        IDocs           docs    = null;
        IDOC            doc     = null;
        IPages          pages   = null;

        IPAGE           page = null;
        IPageVersions   pagevs = null;
        IPAGEVERSION    pagev =  null;

        try
        {
        
            if( axCon == null )
            {
                axCon = new IDBConnection( "ApplicationXtender.Database" );
                axCon.Login( this.appx_userName ,this.appx_password, null, this.appx_dataSource );
            }

            apps   = new IApps((IUnknown)axCon.getApps());
            app    = null;
            for (int i = 1; i <= ((Integer)apps.getCount()).intValue(); i++ ) 
            {
                app = new IAPP((IUnknown)apps.getItem( new Integer( i ) ));
                if( app.getName().toString().equalsIgnoreCase( this.appx_application ) )
                {
                    break;
                }
                app.close();
            }
            
            docs    = new IDocs( (IUnknown)app.getDocs() );
            try
            {
                doc     = new IDOC( (IUnknown)docs.Add( new Long( docid ) ) );
                pages   = new IPages( (IUnknown)doc.getPages() );       
                
                int nrPages = ((Integer)pages.getCount()).intValue();
                ret = new String[ nrPages ];
    
                for (int i = 1; i <= nrPages; i++) 
                {
                    
                    page        = new IPAGE( (IUnknown)pages.getItem( new Integer( i ) ) );
                    pagevs  = new IPageVersions( (IUnknown)page.getVersions() );
                    int int_page = ((Integer)pagevs.getCount()).intValue();
                    if( int_page > 0 )
                    {
                        pagev = new IPAGEVERSION( (IUnknown)pagevs.getItem( new Long( int_page ) ) );
                        if ("0".equals(pagev.getSubPageNum().toString()))
                            ret[ i - 1 ] = pagev.getObjectFile().toString();
                        else
                            ret[ i - 1 ] = pagev.getObjectFile().toString()+";"+pagev.getSubPageNum().toString();
                        pagev.close();
                    }
                    else
                    {
                        erro = "O DocID ["+docid+"] não tem páginas verfique por favor.";
                    }
                    pagevs.close();
                    page.close();
                }
                pages.close();
                doc.close();
            }
            catch( Exception e )
            {
                if( e.getMessage() == null || e.getMessage().indexOf( "80020009: DB-0011" ) == -1 )
                {
                    logger.warn("Erro a obter imagem do ApplicationXtender:", e);
                }
            }
            docs.close();
            app.close();
            apps.close();
//            try
//            {
//                axCon.Logout();
//            }
//            catch (COMException e ) 
//            {
//                e.printStackTrace();                
//            }
//            axCon.close();
//            axCon = null;
        }
        catch(Throwable e)
        {
                if( pagev!=null ) pagev.close();
                if( pagevs!=null ) pagevs.close();
                if( page!=null ) page.close();
                if( pages!=null ) pages.close();
                if( doc!=null ) doc.close();
        
            axCon = null;
            logger.warn("Erro a obter imagem do ApplicationXtender:", e);
        }
        finally
        {   
            //Ole32.CoUninitialize();
        }
        
        if (  erro != null )
        {
            throw new RuntimeException( erro );
        }
        
        return ret;
        
        
    }
  
    public static long importAnexo(EboContext ctx, DocumentInfoProducao docInfo, File[] files ) throws Exception
    {
        int i = 0;
        long ret = 0;
        String erros = null;
        
        // Cria uma direcoria para importar        
        String directory = "\\AppxtenderImport\\" + System.currentTimeMillis() + "\\";
        File f_dir = new File(directory);
        f_dir.mkdirs();
        
        ResultSet rslt = null; 
        PreparedStatement pstm = null;
        Connection cn = ctx.getConnectionData();
        try
        {
            pstm = cn.prepareStatement("SELECT docid from ae_dt3 where field10=?");
            pstm.setString(1, docInfo.referencia_externa );
            rslt = pstm.executeQuery();
            boolean exists = rslt.next();
            if (exists)
            {
                ret = rslt.getLong(1);
            }
            // Só importa se o ficheiro não existir no ApplicationXTender
            if (!exists)
            {
                // Fecha os cursores
                rslt.close();
                pstm.close();
                
                // Cria o ficheiro com os Dados recebidos no XML
                for (int k=0;k < files.length ; k++ ) 
                {
                    File fout = new File(directory + (i+k) + "_" + files[k].getName() );
                    IOUtils.copy( files[k], fout );
                }
                
                
                // Cria o ficheiro de importação para o ApplicationXtender
                String txt_filename =  "importa.txt";
                FileOutputStream fw = new FileOutputStream(directory+txt_filename);
                
                // Cria o registo a ser importado
                StringBuffer sb = new StringBuffer();
                sb.append("\"" + docInfo.ramo + "\"");
                sb.append(',');
                sb.append("\"" + docInfo.apolice + "\"");
                sb.append(',');
                sb.append("\"" + docInfo.c_custo + "\"");
                sb.append(',');
                sb.append("\"" + docInfo.tipodocumento +"\"");
                sb.append(',');
                sb.append("\"" + sdf2.format( docInfo.data_documento ) + "\"");
                sb.append(',');
                sb.append("\"" + sdf1.format( docInfo.data_arquivo ) + "\"");
                sb.append(',');
                sb.append("\"" + (docInfo.numdoc!=0?String.valueOf( docInfo.numdoc ):"") + "\"");
                sb.append(',');
                sb.append("\"" + sdf2.format( docInfo.data_entrada_documento )  + "\"");
                sb.append(',');
                sb.append("\"" + docInfo.agente_cobrador + "\"");
                sb.append(",S");
                sb.append(',');
                sb.append("\"" + docInfo.referencia_externa + "\"");
                for (int k=0;k < files.length ; k++ ) 
                {
                    sb.append("@@").append(directory + (i+k) + "_" + files[k].getName() );
                    sb.append("\r\n");
                }
                byte[] reg = sb.toString().getBytes("UTF-8");
                //byte[] reg = sb.toString().getBytes();
                fw.write(reg, 0, reg.length);
                fw.close();
                
                boolean okimport = false;
                for (int tentativas = 0 ;!okimport && tentativas < 2; tentativas ++) 
                {
                    // Chama o run.bat para importar o ficheiro importa.txt
                    Process process = Runtime.getRuntime().exec("\\AppxtenderImport\\runPRODUCAO.bat", null, f_dir);
                    BufferedOutputStream bout = new BufferedOutputStream(process.getOutputStream());
                    
                    // Cria um Sub Thread a verificar o processo.. e aguarda 60 segundos pela importação.
                    ProcessMonitor pmon = new ProcessMonitor(process);
                    pmon.start();
                    
                    // Tempo limite para a importação correr.
                    int maxTimetoRun = 7200000; 
                    long init = System.currentTimeMillis();
                    long timespend = 0;
                    
                    // Ciclo que verifica se a importação já terminou
                    boolean killed = false;
                    while (maxTimetoRun > timespend && !pmon.finished)
                    {
                        Thread.currentThread().sleep(500);
                        timespend = (System.currentTimeMillis() - init);
                        if( !killed && ( (maxTimetoRun/2) < timespend) )
                        {
                            killed = true;
                            (new GDOCXUtilsStub()).KillProcess( "AcroRd32.exe" );
                        }
                    }
                    
                    if (!pmon.finished)
                    {
                        // Interrompe a Sub Thread para que não fique internamente à espera.
                        pmon.interrupt();
                        
                        // Tenta destruir o processo que não terminou
                        process.destroy();
                        
                        // O processo de importação não terminou.... Devolve erro ao Workflow
                        erros = "Erro a importar imagens para o aplicationXtender: O processo não terminou em [" + (maxTimetoRun/1000) + "] seg's.";
                    }
                    else
                    {
                        okimport = true;                        
                    }
                }
                                
                if (erros == null)
                {
                    // Lê o ficheiro .rej criado pelo Appxtrender para verificar se houve erros na importação.            
                    File filerej = new File(directory + "importa.rej");
                    if (filerej.length() > 0)
                    {
                        FileReader fr = new FileReader(filerej);
                        char[] fileRejData = new char[(int)filerej.length()];
                        fr.read(fileRejData);
                        fr.close();
                        // Devolve o erro ao Workflow
                        erros = "Erro a importar:" + files[0].getName() + ":" + (new String(fileRejData));
                    }
                }
                
                if (erros == null)
                {

                    // Dupla verificação vai ler a tabela AE_DT8 para verficar se o ficheiro foi realmente importado.
                    pstm = cn.prepareStatement("SELECT docid from ae_dt8 where field12 = ?");
                    pstm.setString(1, docInfo.referencia_externa.toUpperCase() );
                    rslt = pstm.executeQuery();
                    // Se retornar false não encontrou o documento acabado de importar dá erro
                    if (!rslt.next())
                    {
                        // Se o ficheiro não foi realmente importado devolve erro
                        erros = "Registo de imagem não encontrado após a importação. Referencia:" + docInfo.referencia_externa;
                    }
                    else
                    {
                        ret = rslt.getLong(1);
                    }
                    // Fecha os cursores
                    rslt.close();
                    pstm.close();
                }
                if (erros != null)
                {
                    // Houve erros durante a importação retornar os erros ao Workflow
                    throw new RuntimeException(erros);
                }
            }
        }
        finally
        {   
            // Em todo o caso fecha os cursores
            try
            {
                if (rslt != null) rslt.close();
                if (pstm != null) pstm.close();
            }
            catch (Exception e)
            {
                return ret;
            }
        }
        // Remove a directoria e o conteúdo temporario 
        // usado para a importação das imagens
        File[] xfilestodelete = f_dir.listFiles();
        for (int k = 0; k <  xfilestodelete.length; k++) 
        {
            xfilestodelete[k].delete();
        }
        f_dir.delete();
        return ret;
    
    }

    
    public static long importAnexo(EboContext ctx, DocumentInfo docInfo, File[] files ) throws Exception
    {
        checkDocumentInfo( docInfo );
        int i = 0;
        long ret = 0;
        String erros = null;
        
        // Cria uma direcoria para importar        
        String directory = "\\AppxtenderImport\\" + System.currentTimeMillis() + "\\";
        File f_dir = new File(directory);
        f_dir.mkdirs();
        
        if (docInfo.fileext != null && docInfo.fileext.indexOf(".") > -1)
        {
            docInfo.fileext = docInfo.fileext.substring(docInfo.fileext.lastIndexOf("."));
        }

        ResultSet rslt = null; 
        PreparedStatement pstm = null;
        Connection cn = ctx.getConnectionData();
        try
        {
            pstm = cn.prepareStatement("SELECT docid from ae_dt8 where field12 = ?");
            pstm.setString(1, docInfo.referenciaExterna );
            rslt = pstm.executeQuery();
            boolean exists = rslt.next();
            if (exists)
            {
                ret = rslt.getLong(1);
            }
            // Só importa se o ficheiro não existir no ApplicationXTender
            if (!exists)
            {
                // Fecha os cursores
                rslt.close();
                pstm.close();
                
                // Cria o ficheiro com os Dados recebidos no XML
                for (int k=0;k < files.length ; k++ ) 
                {
                    File fout = new File(directory + (i+k) + "_" + files[k].getName() );
                    IOUtils.copy( files[k], fout );
                }
                
                
                // Cria o ficheiro de importação para o ApplicationXtender
                String txt_filename =  "importa.txt";
                FileOutputStream fw = new FileOutputStream(directory+txt_filename);
                
                // Cria o registo a ser importado
                StringBuffer sb = new StringBuffer();
                sb.append("\""+sdf1.format( docInfo.dataRegisto )+"\"");
                sb.append(',');
                sb.append("\""+sdf2.format( docInfo.dataEntrada )+"\"");
                sb.append(',');
                sb.append("\""+docInfo.tipoDocumento+"\"");
                sb.append(',');
                sb.append("\""+sdf2.format( docInfo.dataDocumento )+"\"");
                sb.append(',');
                sb.append("\""+docInfo.guardarCaixa+"\"");
                sb.append(',');
                sb.append("\""+docInfo.ramo+"\"");
                sb.append(',');
                sb.append("\""+docInfo.modalidadde+"\"");
                sb.append(',');
                sb.append("\""+docInfo.subModalidade+"\"");
                sb.append(',');
                sb.append("\""+docInfo.anoSinistro+"\"");
                sb.append(',');
                sb.append("\""+docInfo.sinistro+"\"");
                sb.append(',');
                sb.append("\""+docInfo.apolice+"\"");
                sb.append(',');
                sb.append("\""+docInfo.palavrasChave+"\""); 
                sb.append(',');
                sb.append("\""+docInfo.referenciaExterna+"\"");

                for (int k=0;k < files.length ; k++ ) 
                {
                    sb.append("@@").append(directory + (i+k) + "_" + files[k].getName() );
                    sb.append("\r\n");
                }

                byte[] reg = sb.toString().getBytes("UTF-8");
                //byte[] reg = sb.toString().getBytes();
                fw.write(reg, 0, reg.length);
                fw.close();
                
                boolean okimport = false;
                for (int tentativas = 0 ;!okimport && tentativas < 2; tentativas ++) 
                {
                    // Chama o run.bat para importar o ficheiro importa.txt
                    Process process = Runtime.getRuntime().exec("\\AppxtenderImport\\run.bat", null, f_dir);
                    BufferedOutputStream bout = new BufferedOutputStream(process.getOutputStream());
                    
                    // Cria um Sub Thread a verificar o processo.. e aguarda 60 segundos pela importação.
                    ProcessMonitor pmon = new ProcessMonitor(process);
                    pmon.start();
                    
                    // Tempo limite para a importação correr.
                    int maxTimetoRun = 7200000; 
                    long init = System.currentTimeMillis();
                    long timespend = 0;
                    
                    // Ciclo que verifica se a importação já terminou
                    
                    boolean killed = false;
                    
                    while (maxTimetoRun > timespend && !pmon.finished)
                    {
                        Thread.currentThread().sleep(500);
                        timespend = (System.currentTimeMillis() - init);
                        if( !killed && ( (maxTimetoRun/2) < timespend) )
                        {
                            killed = true;
                            (new GDOCXUtilsStub()).KillProcess( "AcroRd32.exe" );
                        }
                    }
                    
                    if (!pmon.finished)
                    {
                        // Interrompe a Sub Thread para que não fique internamente à espera.
                        pmon.interrupt();
                        
                        // Tenta destruir o processo que não terminou
                        process.destroy();
                        
                        // O processo de importação não terminou.... Devolve erro ao Workflow
                        erros = "Erro a importar imagens para o aplicationXtender: O processo não terminou em [" + (maxTimetoRun/1000) + "] seg's.";
                    }
                    else
                    {
                        okimport = true;                        
                    }
                }
                                
                if (erros == null)
                {
                    // Lê o ficheiro .rej criado pelo Appxtrender para verificar se houve erros na importação.            
                    File filerej = new File(directory + "importa.rej");
                    if (filerej.length() > 0)
                    {
                        FileReader fr = new FileReader(filerej);
                        char[] fileRejData = new char[(int)filerej.length()];
                        fr.read(fileRejData);
                        fr.close();
                        // Devolve o erro ao Workflow
                        erros = "Erro a importar:" + docInfo.filename + ":" + (new String(fileRejData));
                    }
                }
                
                if (erros == null)
                {

                    // Dupla verificação vai ler a tabela AE_DT8 para verficar se o ficheiro foi realmente importado.
                    pstm = cn.prepareStatement("SELECT docid from ae_dt8 where field12 = ?");
                    pstm.setString(1, docInfo.referenciaExterna.toUpperCase() );
                    rslt = pstm.executeQuery();
                    // Se retornar false não encontrou o documento acabado de importar dá erro
                    if (!rslt.next())
                    {
                        // Se o ficheiro não foi realmente importado devolve erro ao WorkFlow
                        erros = "Registo de imagem não encontrado após a importação. Id da peritagem:" + docInfo.referenciaExterna;
                    }
                    else
                    {
                        ret = rslt.getLong(1);
                    }
                    // Fecha os cursores
                    rslt.close();
                    pstm.close();
                }
                if (erros != null)
                {
                    // Houve erros durante a importação retornar os erros ao Workflow
                    throw new RuntimeException(erros);
                }
            }
        }
        finally
        {   
            // Em todo o caso fecha os cursores
            try
            {
                if (rslt != null) rslt.close();
                if (pstm != null) pstm.close();
            }
            catch (Exception e)
            {
                return ret;
            }
        }
        // Remove a directoria e o conteúdo temporario 
        // usado para a importação das imagens
        File[] xfilestodelete = f_dir.listFiles();
        for (int k = 0; k <  xfilestodelete.length; k++) 
        {
            xfilestodelete[k].delete();
        }
        f_dir.delete();
        return ret;
    
    }
    
    public static long importAnexo(EboContext ctx, DocumentInfo docInfo, byte[] data) throws Exception
    {
        File x = File.createTempFile( docInfo.filename, docInfo.fileext );
        FileOutputStream fout = new FileOutputStream( x );
        fout.write( data );
        fout.close();
        return importAnexo( ctx, docInfo, new File[] { x } );
    }

    /**
     * Class Para monitorizar um processo
     * @Company Enlace3
     * @author João Paulo Trindade Carreira
     * @version 1.0
     * @since 
     */
    public static class ProcessMonitor extends Thread
    {
        /**
         * Processo a ser monitorizado
         * @since 
         */
        Process  proc;
        /**
         * Variável que indica se o processo já terminou ou não.
         * @since 
         */
        public boolean finished = false;
        
        public ProcessMonitor(Process proc )
        {
            this.proc = proc;
        }
        public void run()
        {
            try
            {
                this.proc.waitFor();
                finished = true;
            }
            catch (InterruptedException e)
            {
            }
        }
    }
    
    public static void checkDocumentInfo( DocumentInfo docInfo )
    {
        docInfo.filename = checkField( "filename", docInfo.filename, false );
        docInfo.fileext = checkField( "fileext", docInfo.fileext, false );
        //docInfo.dataRegisto = checkField( "dataRegisto", dataRegisto, false );
        //docInfo.dataEntrada = checkField( "dataEntrada", dataEntrada, false );
        docInfo.tipoDocumento = checkField( "tipoDocumento", docInfo.tipoDocumento, false );
        //docInfo.dataDocumento = checkField( "dataDocumento", dataDocumento, false );
        docInfo.ramo = checkField( "ramo", docInfo.ramo, false );
        docInfo.modalidadde = checkField( "modalidadde", docInfo.modalidadde, false );
        docInfo.subModalidade = checkField( "subModalidade", docInfo.subModalidade, false );
        docInfo.anoSinistro = checkField( "anoSinistro", docInfo.anoSinistro, false );
        docInfo.sinistro = checkField( "sinistro", docInfo.sinistro, false );
        docInfo.apolice = checkField( "apolice", docInfo.apolice, false );
        docInfo.palavrasChave = checkField( "palavrasChave", docInfo.palavrasChave, false );
        docInfo.referenciaExterna = checkField( "referenciaExterna", docInfo.referenciaExterna, false );
    }
    
    public static class DocumentInfoProducao
    {
        public String ramo;
        public String apolice;
        public String tipodocumento;
        public String c_custo;
        public java.util.Date data_documento;
        public java.util.Date data_arquivo;
        public long numdoc;
        public java.util.Date data_entrada_documento;
        public String agente_cobrador;
        public String referencia_externa;
    }

    public static class DocumentInfo
    {
        public String   filename; 
        public String   fileext;
        public java.util.Date     dataRegisto;
        public java.util.Date     dataEntrada;
        public String   tipoDocumento;
        public java.util.Date     dataDocumento;
        public String   guardarCaixa = "BATCH";
        public String   ramo;
        public String   modalidadde;
        public String   subModalidade;
        public String   anoSinistro;
        public String   sinistro;
        public String   apolice;
        public String   palavrasChave;
        public String   referenciaExterna;
    }
     
    public static final String checkField( String fieldName, String value, boolean  required )
    {
        if( required && ( value == null || value.trim().length() == 0 ) )
        {
            throw new RuntimeException("AppXtenderApi: Error importing field ["+fieldName+"], value required." );
        }
        String ret = value==null?"":value;
        if( "palavrasChave".equals( fieldName ) )
        {
            ret = replaceChars( ret );
        }
        return ret;
    }
     
    public static final String replaceChars( String in )
    {
        if( in != null )
        {
            in=in.replace('Á','A');
            in=in.replace('À','A');
            in=in.replace('Ã','A');
            in=in.replace('Â','A');
            in=in.replace('Ä','A');
            in=in.replace('á','a');
            in=in.replace('à','a');
            in=in.replace('â','a');
            in=in.replace('ã','a');
            in=in.replace('ä','a');

            in=in.replace('É','E');
            in=in.replace('È','E');
            in=in.replace('Ê','E');
            in=in.replace('Ë','E');
            in=in.replace('é','e');
            in=in.replace('è','e');
            in=in.replace('ê','e');
            in=in.replace('ë','e');

            in=in.replace('Í','I');
            in=in.replace('Ì','I');
            in=in.replace('Î','I');
            in=in.replace('Ï','I');
            in=in.replace('í','i');
            in=in.replace('ì','i');
            in=in.replace('î','i');
            in=in.replace('ï','i');

            in=in.replace('Ó','O');
            in=in.replace('O','O');
            in=in.replace('Ô','O');
            in=in.replace('Õ','O');
            in=in.replace('Ö','O');
            in=in.replace('ó','o');
            in=in.replace('ò','o');
            in=in.replace('ô','o');
            in=in.replace('õ','o');
            in=in.replace('ö','o');

            in=in.replace('Ú','U');
            in=in.replace('Ù','U');
            in=in.replace('Û','U');
            in=in.replace('Ü','U');
            in=in.replace('ú','u');
            in=in.replace('ù','u');
            in=in.replace('û','u');
            in=in.replace('ü','u');

            in=in.replace('ç','c');
            in=in.replace('Ç','C');

        }
        return in;
    }

    
    public static void main( String[] args )
    {
        System.out.println(
            Collator.getInstance(Locale.FRENCH).getCollationKey("joão").compareTo( Collator.getInstance(Locale.FRENCH).getCollationKey("joao") )
        );
        
        
    }
    
    
    private static class CacheItem
    {
        String[]    pageFiles;
        long        creationTime = System.currentTimeMillis();
        
        public final boolean isExpired()
        {
            return (System.currentTimeMillis() - creationTime)  > 300000;
        }
    }
}