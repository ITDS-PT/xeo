/*Enconding=UTF-8*/
package netgest.bo.http;
import java.io.*;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import netgest.bo.system.*;

import netgest.utils.*;
import netgest.bo.system.Logger;

/**
 * 
 * Filtro que pode ser activado para permitir que as JSP's (Geradas pelo XEO) e os resources dos objectos
 * possam estar numa directoria à parte do webserver.<p>
 * 
 * Este filtro verifica a directoria de obj_deployjspdir está a apontar para fora do WebServer se sim
 * automaticamente activa-se, se não fica inactivo.<p>
 * 
 * Este filtro tambem tem um array ( reverseDeployFiles ) onde são especificados os ficheiros necessários
 * aos includes das JSP's. Para os includes funcionarem tem que existir previamente o ficheiro no local 
 * esperado. Está pré-definido neste array o ficheiro boheaders.jsp. Estes ficheiros também são alvo de
 * comparação no intervalo definido pela variável REVERSE_FILES_CHECK_INTERVAL.<p>
 *
 *Para começar a trabalhar com o filtro basta mudar a configuração obj_deployjspdir (boconfig.xml) para
 *        uma directoria que esteja fora do WebServer. ( Eg: c:\netgestBO\webresources\ ) e de seguinda
 *        fazer build total e copiar a directoria resources para c:\netgestBO\webresources\resources.<P>
 *        
 * Directorias utilizadas pelo filtro:<br>
 *     - <WebRoot>  - Directoria raíz do WebServer ( No contexto de uma aplicação eg:/ngt_server/ é raíz)<br>
 *                    Quando chega um pedido a esta directoria o filtro verifica se o ficheiro existe<br>
 *                    na obj_deployjspdir e se existir actualiza-o na directoria xeodeploy e faz<br>
 *                    o foward do request.<br>
 *     - <WebRoot>/xeodeploy - Directoria para onde são copiados os ficheiros que se encontram em <br>
 *                    obj_deployjspdir. Se um pedido for feito directamente a esta directoria o<br>
 *                    filtro automaticamente verifica se o ficheiro se encontra actualizado.<br>
 *     - <WebRoot/resources  - Directoria onde se encontram os <i>resources</i> dos objectos. Todos os<br>
 *                    pedidos que chegam a esta directoria são redirecionado para <br>
 *                    /xeodeploy/resources se o ficheiro pedido existir em obj_deployjspdir. <br>
 *                    
 *     - obj_deployjspdir - Directoria para a qual se deve copiar os Resources e para onde o
 *                   XEO faz o deploy dos viewers dos objectos.<br>
 * <p>       
 * 
 * Configurarar o filtro no WEB-INF/web.xml
 *<code>
 *   &lt;filter>
 *    &lt;filter-name>DeployFilter&lt;/filter-name>
 *    &lt;filter-class>netgest.bo.http.DeployFilter&lt;/filter-class>
 *  &lt;/filter>
 *  &lt;filter-mapping>
 *    &lt;filter-name>DeployFilter&lt;/filter-name>
 *    &lt;url-pattern>/*&lt;/url-pattern>
 *  &lt;/filter-mapping>
 *</code>
 * 
 * Notas:<br>
 *      - Se o ficheiro existir na <WebRoot> e na directoria obj_deployjspdir é utilizado o da obj_deployjspdir<br>
 *      - CHECK_INTERVAL define o intervalo de tempo por cada verificação do ficheiro.<br>
 *      - O filtro apaga os ficheiros que existam debaixo da directoria <WebRoot>/xeodeploy e não existam
 *        em obj_deployjspdir. ( Quando estes são requisitados pelo browser ).<br>
 *      - A comparação dos ficheiros é feita com base na Data e no Tamanho. Se existir um ficheiro mais
 *        recente em <WebRoot>/xeodeploy é subscrito mesmo por um mais antigo.
 *        ( Forma de garantir que a directoria se encontra sempre actualizada com o conteúdo da obj_deployjspdir )
 * <p>      
 *      
 */
public class DeployFilter implements Filter 
{
    // Directoria para onde são copiados os viewers.
    private static final String          deployRoot                 = ".xeodeploy";
    // Includes (JSP) necessários aos viewers. Tem de ser copiados para a directoria de deploy.
    private static final String[]        reverseDeployFiles         = { "boheaders.jsp","boheaders2.jsp"  };
    // Directorias às quais deve fazer reverseDeploy
    private static final String[]        reverseDeployDirs          =  { "resources" , deployRoot };
    // Flag que indica se deve remover a directoria para verificar o ficheiro
    private static final boolean[]       reverseDeployRemoveDir     = { false      , true       };
    
    // Directorias onde o WebServer espera encontrar os viewers
    private static final String[]        deployDirs                 = { "" };
    // Chave a ser colocada no request se este já sofreu um forward anterior
    private static final String          forwaredKey                = "__XEOForwared";
    // Objecto para servir de valor na forwardKey. Este objecto é apenas uma optimização
    // para utilizar apenas uma referencia a um objecto e não criar novos.
    private static final Object          auxObject                  = new Object();
    
    // Intervalo de tempo pelo qual é verificado a existencia de uma nova 
    // versão do ficheiro na directoria fonte.
    public static final int             CHECK_INTERVAL              = 2000;

    // Intervalo de tempo para fazer verficação aos ficheiros reverseDeployFiles
    private static final int            REVERSE_FILES_CHECK_INTERVAL= 20000;
    // Ultima vez que ocorreu uma verificação
    private long                        lastReverseFileCheck        = 0;

    
    // Hashtable que guarda todos os pedidos através do object KnownPath
    // Serve para optimizar e saber qual foi a ultima vez que o ficheiro foi verificado
    private Hashtable       wellKnownPath   = new Hashtable();
    
    // Filter config Objecto Standard J2EE
    private FilterConfig    _filterConfig   = null;
    
    // Directoria de deploy criada a partir do contexto do WebServer. 
    private String          deployDir       = null;
    
    // Variável que guarda a directoria fonte, para onde o XEO compila as JSP's
    // esta variável é o resultado do parametro do boconfig obj_deployjspdir;
    private String[]        srcDir          = null;
    
    // String que guarda a localização fisica dos ficheiro do WebServer.
    private String          webRoot         = null;
    
    private boolean         enable          = true;
    
    // Resultados dos pedidos 
    public static final byte UNKNOWN = 9;
    public static final byte TYPE_XEO_FILE = 0;
    public static final byte TYPE_WEB_FILE = 1;
    
    private static Logger logger = Logger.getLogger( DeployFilter.class.getName() );
    
    public void init(FilterConfig filterConfig) throws ServletException
    {
        _filterConfig   = filterConfig;
        
        File srcDirFile = getViewersDirectory();
        
        if( srcDirFile != null ) {
	        ArrayList<String> webDirs = new ArrayList<String>(); 
	        
	        
	        String extraDirs = _filterConfig.getInitParameter("extraDirs");
	        
	        File moduleDirFile = getModulesWebDir();
	        File[] moduleDir = moduleDirFile.listFiles();
	        if( moduleDir == null ) {
	        	moduleDir = new File[0];
	        }
	        if( extraDirs != null ) {
	        	List<File> srcDirs = new ArrayList<File>( Arrays.asList( moduleDir ) );
	        	srcDirs.add( new File( extraDirs ) );
	        	moduleDir = srcDirs.toArray( new File[ srcDirs.size() ] );
	        }
	        
	        
	        if( moduleDir != null ) {
	        	for( int i=0;i < moduleDir.length; i++ ) {
	        		webDirs.add( moduleDir[i].getAbsolutePath() );
	        	}
	        }
	        webDirs.add( srcDirFile.getAbsolutePath() );
	        
	        srcDir = (String[])webDirs.toArray( new String[ webDirs.size() ] );
	        
	        webRoot = _filterConfig.getServletContext().getRealPath("/");
	        deployDir =  webRoot + File.separator + deployRoot; 
	        
	        // Verifica se as JSP's estão a ser compiladas 
	        // directamente para o WebServer. se Sim faz o disable ao Filtro
	        if( deployDir.toLowerCase().startsWith( srcDir[0].toLowerCase() ) )
	        {
	            enable = false;    
	        }
	        else
	        {
	            checkDeployDir();
	        }
	        
	    	logger.finer("Starting deploying modules webfiles ..." );
	        for( int i=0; i < srcDir.length; i++ ) {
	        	File file = new File( srcDir[i] );
	        	logger.finer("Deployig root web files for:" + file.getName() );
	        	deployDir( file, deployDir );
	        }
	    	logger.finer("Finished deploying modules webfiles ..." );
        }
        else {
        	System.err.println("DeployFilter disabled because XEO Failed to initialize!");
	    	logger.severe("Cannoad load XEO Application..." );
        }
    }
    
    public void deployDir( File srcDir, String destDir ) {
    	File[] dirFiles = srcDir.listFiles();
    	for( int k=0;dirFiles != null && k < dirFiles.length; k++ ) {
    		if( !dirFiles[k].getName().startsWith(".") ) {
	    		if( dirFiles[k].isFile() ) {
	    	        File deployFile = new File( destDir + File.separator + dirFiles[k].getName() );
	    	        if( dirFiles[k].lastModified() != deployFile.lastModified() || dirFiles[k].length() != deployFile.length()  ) {
	    	        	IOUtils.copy( dirFiles[k], deployFile );
	    	        }
	    		} else if ( dirFiles[k].isDirectory() ) {
	    			String newDestDir = destDir + File.separator + dirFiles[k].getName();
	    			deployDir( dirFiles[k], newDestDir );
	    		}
    		}
    	}
    }
    
    /**
     * Verifica se a directoria de deploy existe no WebServer
     */
    private final void checkDeployDir()
    {
        // 
        File deployDirFile = new File( deployDir );
        if( !deployDirFile.exists() )
        {
            deployDirFile.mkdir();
        }
        
        // Copia os ficheiros necessários aos incluides dos viewers para 
        // a directoria de deploy do WebServer.
        if( System.currentTimeMillis() - this.lastReverseFileCheck > REVERSE_FILES_CHECK_INTERVAL )
        {
            // Actualiza o timestamp
            this.lastReverseFileCheck = System.currentTimeMillis();
            
//            for (int i = 0; i < reverseDeployFiles.length; i++) 
//            {
//                File reverseOrig = new File( webRoot + File.separator + reverseDeployFiles[i] );
//                File reverseDest = new File( deployDir + File.separator + reverseDeployFiles[i] );
//                //if( reverseOrig.exists() )
//                //{
//                    if( !compareFiles( reverseOrig, reverseDest ) )
//                    {
//                        IOUtils.copy( reverseOrig , reverseDest );
//                    }
//                //}
//                //else
//                //{
//                    // Para não haver confusões apaga o ficheiro.
//                //    reverseDest.delete();
//                //}
//            }
        }
        
    }
    
    
    /**
     * Obtem a directoria do boConfig para onde o XEO compila os Viewers.
     * @return File que representa a directoria.
     */
    private File getViewersDirectory()
    {
        boApplication boapp = boApplication.getApplicationFromStaticContext( "XEO" );
        if( boapp != null ) {
	        File deployedFile = new File( boapp.getApplicationConfig().getDeployJspDir() );
        	return deployedFile;
        }
        return null;
    }

    /**
     * Obtem a directoria dos modulos para onde o XEO compila os Viewers.
     * @return File que representa a directoria.
     */
    private File getModulesWebDir()
    {
        boApplication boapp = boApplication.getApplicationFromStaticContext( "XEO" );
        File deployedFile = new File( boapp.getApplicationConfig().getModuleWebBaseDir() );
        return deployedFile;
    }
    
    /**
     * Copia o ficheiros da directoria <pre>obj_deployjspdir</pre> para
     * a directoria do WebServer.
     * Só copia se o ficheiro não existir ainda na directoria do WebServer ou se
     * entretanto foi alterado na directoria do XEO
     * 
     * @return true     - Se encontrou o ficheiro na directoria obj_deployjspdir
     * @param fileName  - Ficheiro a ser verificado.
     */
    private final boolean deployFile( String fileName )
    {
        boolean ret     = false;
        for( int i=0; i < srcDir.length; i++ ) {
	        File srcFile    = new File( srcDir[i] + fileName );
	        File deployFile = new File( deployDir + fileName );
	        if( srcFile.exists() )
	        {
//	            if( !compareFiles( srcFile, deployFile ) )
//	            {	
//	                checkDeployDir();
//	                IOUtils.copy( srcFile, deployFile );
//	            }
	            ret = true;
	            break;
	        }
        }
        
        return ret;
    }
    
    /**
     * Copia o ficheiros da directoria <pre>obj_deployjspdir</pre> para
     * a directoria do WebServer.
     * 
     * Muito parecido com o método <pre>deployFile<pre> mas faz a verificação ao contrário porque
     * o WebServer está a pedir directamente um ficheiro que veio obj_obj_deployjspdir.
     * 
     * Só copia se o ficheiro não existir ainda na directoria do WebServer ou se
     * entretanto foi alterado na directoria do XEO
     * 
     * @return true     - Se encontrou o ficheiro na directoria obj_deployjspdir
     * @param fileName  - Ficheiro a ser verificado.
     */
    private boolean reverseDeployFile( String requestedFile )
    {
        boolean ret = false;
        for( int i=0; i < srcDir.length; i++ ) {
	        File srcFile    = new File( srcDir[i] + requestedFile );
	        File deployFile = new File( deployDir + requestedFile );
	        if( srcFile.exists() && srcFile.getAbsolutePath().endsWith( requestedFile ) )
	        {	
	        	/*
	            if( !compareFiles( srcFile, deployFile ) )
	            {
	                File parentDir  = deployFile.getParentFile();
	                if( !parentDir.isDirectory() && !parentDir.exists() )
	                {
	                    parentDir.mkdirs();
	                }
	                checkDeployDir();
	                IOUtils.copy( srcFile, deployFile );
	            }
	            */
	            ret = true;
	            break;
	        }
        }
        return ret;
    }
    
    /**
     * Devolve um object que representa o pedido já cohecido pelo filtro. 
     * @return O object KnownPath
     * @param   request - O HttpServletRequest do pedido
     */
    private final KnownPath getKnownPath( HttpServletRequest request )
    {
        String path = request.getServletPath();
        KnownPath ret = (KnownPath)wellKnownPath.get( path );
        if( ret == null )
        {
            synchronized ( this )
            {
                // Dupla verificação. Para evitar que duas Thread's crieem
                // dois ojectos diferentes para os mesmo pedido.
                ret = (KnownPath)wellKnownPath.get( path );
                if( ret == null )
                {
                    ret = new KnownPath( path );
                    wellKnownPath.put( path, ret );
                    //wellKnownPath.clear();
                }
            }
        }
        return ret;
    }
    
    /**
     * Faz o lock ao acesso a determinado ficheiro para evitar operações concurrentes.
     * Se o ficheiro ao fim de 900ms não estiver desbloqueado. Faz o lock e deixa continuar
     * @param kpath - O Object KnownFile do pedido.
     */
    private final void lock( KnownPath kpath )
    {
        int retCnt = 0;
        
        // Sincroniza pedidos para o mesmo URI
        synchronized ( kpath )
        {
            while( kpath.locked && retCnt < 60 )
            {
                retCnt ++;
                try
                {
                    Thread.sleep( 15 );
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }
            kpath.locked = true;
        }
    }
    
    /**
     * Faz o Unlock de um ficheiro anteriormente bloqueado pelo método lock
     * @param kpath - O Object KnownFile do pedido.
     */
    private final void unLock( KnownPath kpath )
    {
        kpath.locked = false;
    }
    
    
    /**
     * Rotina Core do Filtro. Responsável por encaminhar o pedido e verificar se os ficheiros 
     * foram alterados desde a ultima verificação.
     * 
     * @param request   - HttpServletRequest do pedido
     * @param response  - HttpServletResponse do pedido
     * @return false se o pedido continua via percurso convencional. true se o pedido foi 
     *            reencaminhado.
     *            
     * @param kpath     - Objecto que representa o pedido.
     */
    private boolean fowardToXeoFile( KnownPath kpath, HttpServletRequest request, ServletResponse response ) throws ServletException
    {
        // Verifica o Timestamp da última verficação. Se o ficheiro estiver locked
        // continua mesmo que seja uma versão antiga. 
        if( !kpath.locked  && ( System.currentTimeMillis() - kpath.lastCheck ) >= CHECK_INTERVAL )   
        {
            // Faz o lock ao ficheiro
            lock( kpath );
        
            // Determina a path do ficheiro para 
            // saber se está a ser monitorizada.
            String      path     = null;
            String      file     = kpath.path;
            String 		fsFile 	 = kpath.path.replace( '/' , File.separatorChar );
            
            String[]    auxArr   = file.split("/");
            if( auxArr.length <= 2 )
            {
                path = "";
            }
            else
            {
                path = auxArr[1];
            }
            
            try
            {
                // Flag para verificar se o ficheiro está sobre o controlo do filtro.
                boolean isunderfilter = false;
                // Verifica se o ficheiro se encontra na Directoria do XEO
                for (int i = 0; i <  srcDir.length; i++) 
                {
                	File srcFile =  new File( srcDir[i] + file );
                    if( srcFile.exists() && srcFile.getAbsolutePath().endsWith( fsFile )  )
                    {
                        isunderfilter = true;
                        kpath.forwaredPath  = "/" + deployRoot + file;
                        kpath.xeoForwared = deployFile( fsFile );
                        break;
                    }
                }
                // Verifica se o ficheiro já se encontra nas directorias
                // do WebServer mas que a origem é do XEO
                for (int i = 0;!isunderfilter && i < reverseDeployDirs.length; i++) 
                {
                    if( reverseDeployDirs[i].equals( path ) )
                    {
                        isunderfilter = true;
                        String fileToCheck;
                        if( reverseDeployRemoveDir[i] )
                        {
                            fileToCheck = file.replaceFirst( "/"+path+"/","/" );
                            fileToCheck = fileToCheck.replace( '/' ,  File.separatorChar );
                            kpath.xeoForwared = false;
                            if( !reverseDeployFile( fileToCheck ) )
                            {
                                kpath.forwaredPath  = fileToCheck;
                                kpath.xeoForwared = true;
                            }
                        }
                        else
                        {
                            fileToCheck = file;
                            fileToCheck = fileToCheck.replace( '/' ,  File.separatorChar );
                            kpath.xeoForwared = false;
                            if( reverseDeployFile( fileToCheck ) )
                            {
                                kpath.forwaredPath  = "/" + deployRoot + file;
                                kpath.xeoForwared = true;
                            }
                            
                        }
                        break;
                    }
                }
                // Actualiza o objecto KnownPath com resultado das verficações.
                kpath.lastCheck     = System.currentTimeMillis();
                kpath.resourceType  = isunderfilter?TYPE_XEO_FILE:TYPE_WEB_FILE;
            }
            finally
            {
                // Desbloqueia o acesso ao ficheiro.
                unLock( kpath );
            }
        }
        
        // Verifica se o ficheiro deve sofer um reencaminhamento
        if( kpath.xeoForwared )        
        {
            // Ficheiro não se encontra na directioria na qual foi pedido. mas sim na
            // directoria de deploy no WebServer.
            try
            {
                // Cria um novo dispatcher para o nova localização
                RequestDispatcher dispatch = request.getRequestDispatcher( kpath.forwaredPath.replace( '\\' , '/') );
                // Verifica se conseguiu um dispatcher para a localização
                if( dispatch != null )
                {
                    request.setAttribute( forwaredKey , auxObject );
                    dispatch.forward( request, response );
                }
                else
                {
                    kpath.xeoForwared = false;
                }
            }
            catch (IOException e)
            {
                // Erro IOException ou ServletException no reenchaminhamento 
                // do pedido.
                throw new RuntimeException( e );
            }
            catch (ServletException e)
            {
                // Erro IOException ou ServletException no reenchaminhamento 
                // do pedido. 
                
                if( e.getRootCause() != null )
                {
                    logger.finest( "Erro na Servlet "+request.getRequestURI()+":", e.getRootCause() );
                }
                else
                {
                    logger.finest( "Erro na Servlet "+request.getRequestURI()+":", e );
                }
                
                throw e;

                /*
                if( e.getRootCause() != null )
                {
                    response.resetBuffer();
                    try
                    {
                        e.printStackTrace(
                            response.getWriter()
                        );
                    }
                    catch (Exception ex)
                    {
                        throw new RuntimeException( e.getRootCause() );
                    }
                }
                else
                {
                    if( e.getRootCause() != null )
                    {
                        throw new RuntimeException( e.getCause() );
                    }
                    else
                    {
                        throw new RuntimeException( e );
                    }
                    
                }
                */
            }
        }
        // Devolve se o pedido foi reencaminhado ou não.
        return kpath.xeoForwared;
    }
    
    

    /**
     * Método que serve entrada no filtro segundo o interface implementado
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @param chain
     * @param response
     * @param request
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if( this.enable )
        {
        	
            HttpServletRequest  hrequest  = (HttpServletRequest)request;
    
            // Object o objecto que representa o pedido
            KnownPath kpath = getKnownPath( hrequest );
            
            // Check if the URL was already forwared
            if( request.getAttribute( forwaredKey ) == null && kpath.resourceType != TYPE_WEB_FILE )
            {
                
                // Tenta fazer o foward para um ficheiro XEO
                // Devolve true/false. true se o pedido foi reenviado para outro endereço.
                if( srcDir != null && !fowardToXeoFile( kpath, hrequest, response ) )
                {
            		chain.doFilter( request , response);
                }
            }
            else
            {
                // O pedido é do tipo WEB_FILE então não verifica nada e deixa o servidor
                // continuar normalmente
                chain.doFilter( request , response);
            }
        }
        else
        {
            chain.doFilter( request , response);
        }
    }

    /**
     * Método que server para fazer o destroy ao filtro segundo o interface.
     */
    public void destroy()
    {
        _filterConfig           = null;
        deployDir               = null;
        srcDir                  = null;
        webRoot                 = null;
        lastReverseFileCheck    = 0;
        wellKnownPath           = new Hashtable();
   }
    
    /**
     * Class que guarda as propriedades dos pedidos a medida que vão sendo conhecidos
     */
    private class KnownPath
    {
        /**
         * Ultima vez que este pedido foi verficado
         */
        public long     lastCheck       = 0;
        /**
         * Tipo de recurso obtido depois de análisado
         */
        public byte     resourceType    = UNKNOWN;
        /**
         * ServletPath do pedifo
         */
        public String   path            = null;
        /**
         * Se o pedido foi reencaminhado para um destino alternativo.
         */
        public boolean  xeoForwared     = false;
        /**
         * Caminho para o qual foi reenchaminhado
         */
        public String   forwaredPath    = null;
        /**
         * Se o recurso está bloqueado.
         */
        public boolean  locked          = false;
        /**
         * Constructor do Obejto
         * @param path - ServletPath do pedido
         */
        public KnownPath( String path )
        {
            this.path = path;
        }
    }
    
    
    private static final boolean compareFiles( File file1, File file2 )
    {
        boolean ret = true;
        if( file1.getName().equals( file2.getName() ) ) {
        	ret = false;
        }
        else if( file1.lastModified() != file2.lastModified()  )
        {
            ret = false;
        }
        else if ( file1.length() != file2.length() )
        {
            ret = false;
        }
        return ret;
    }

}