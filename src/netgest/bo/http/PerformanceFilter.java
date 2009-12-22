package netgest.bo.http;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.servlet.ServletResponse;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.FilterChain;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import netgest.bo.boConfig;
import netgest.bo.system.boSession;
import netgest.bo.system.Logger;
import javax.servlet.ServletOutputStream;

import netgest.bo.message.server.mail.Mail;
import netgest.bo.message.server.mail.MailAddress;
import netgest.bo.message.server.mail.MailMessage;


public class PerformanceFilter implements Filter 
{

    private static final SimpleDateFormat   sdf         = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat   logSufix    = new SimpleDateFormat("yyyyMMdd");
    private static final Logger             logger      = Logger.getLogger( PerformanceFilter.class );
    private static int   logRequests = 0;
    
    protected static boolean METRIC_JSP  = false;
    protected static boolean METRIC_HTML = false;

    private static String          logDir;

    private FilterConfig    _filterConfig = null;
    private String          logSrvName;  
    private FileWriter      logSrvOut;

    private String          logCliName;  
    private FileWriter      logCliOut;
    
    protected static Hashtable       excludeHTML = new Hashtable();
    protected static Hashtable       excludeJSP = new Hashtable();

    public static final Hashtable ACTIVE_REQUESTS = new Hashtable();
    public static long MAX_REQUEST_TIME = 150000;

    public void init(FilterConfig filterConfig) throws ServletException
    {
        

        (new RequestMonitorItem()).start();
    	
        _filterConfig = filterConfig;
        logDir = filterConfig.getServletContext().getRealPath("/WEB-INF/");
        if( !logDir.endsWith( File.separator ) )
        {
            logDir += File.separator;
        }
        logSrvName = logSufix.format( new Date() )+"_perf.log";
        logCliName = logSufix.format( new Date() )+"_perf_cli.log";
        
        init();
        loadFromFile();
        
    }
    
    public void init()
    {
        excludeHTML = new Hashtable();
        excludeHTML.put( "__controlSessionXwf.jsp", Boolean.TRUE );
        excludeHTML.put( "__refresh.jsp", Boolean.TRUE );
        excludeHTML.put("__cmds.jsp",Boolean.TRUE );
        
        METRIC_JSP = false;
        METRIC_HTML = false;
         
    }

    public static void loadFromFile()
    {
        try
        {
            Properties props = new Properties();
            FileInputStream finp = new FileInputStream( logDir + "perfConfig.properties" );
            props.load( finp );
            finp.close();
            loadFromProperties( props );
        }
        catch( FileNotFoundException e )
        {
            logger.severe("Ficheiro de configuração PerformanceFilter não encontrado ["+ logDir + "perfConfig.properties" +"]"  );
        }
        catch( Exception e )
        {
            logger.severe("Erro a inicializar PerformanceFilter",e);            
        }
    }

    public static void loadFromProperties( Properties props )
    {
        try
        {
            METRIC_HTML = "true".equalsIgnoreCase( props.getProperty("metricHTML") );
            METRIC_JSP  = "true".equalsIgnoreCase( props.getProperty("metricJSP") );
            
            String stringExcludedJSP = props.getProperty("excludedJSP");
            excludeJSP = new Hashtable();
            if( stringExcludedJSP != null )
            {
                String[] arrayExcludedJSP = stringExcludedJSP.split("\\|");
                for (int i = 0; i < arrayExcludedJSP.length; i++) 
                {
                    excludeJSP.put( arrayExcludedJSP[i], Boolean.TRUE );
                }
            }
            
            String stringExcludedHTML = props.getProperty("excludedHTML");
            excludeHTML = new Hashtable();
            if( stringExcludedHTML != null )
            {
                String[] arrayExcludedHTML = stringExcludedHTML.split("\\|");
                for (int i = 0; i < arrayExcludedHTML.length; i++) 
                {
                    excludeHTML.put( arrayExcludedHTML[i], Boolean.TRUE );
                }
            }
        }
        catch( Throwable e )
        {
            e.printStackTrace();
        }   
    }

    public static void saveToFile()
    {
        try
        {
            Properties props = new Properties();
            
            props.setProperty( "metricJSP", String.valueOf( METRIC_JSP ) );
            props.setProperty( "metricHTML", String.valueOf( METRIC_HTML ) );
            
            Enumeration enumExcludeJSP =  excludeJSP.keys();
            StringBuffer sb = new StringBuffer();
            while( enumExcludeJSP.hasMoreElements() )
            {
                if( sb.length() > 0 ) sb.append("|");
                sb.append( enumExcludeJSP.nextElement() );
            }
            if( sb.length() > 0 ) props.setProperty( "excludedJSP", sb.toString() );
            
            sb = new StringBuffer();
            Enumeration enumExcludeHTML =  excludeHTML.keys();
            while( enumExcludeHTML.hasMoreElements() )
            {
                if( sb.length() > 0 ) sb.append("|");
                sb.append( enumExcludeHTML.nextElement() );
            }
            if( sb.length() > 0 ) props.setProperty( "excludedHTML", sb.toString() );
            
            FileOutputStream fout = new FileOutputStream( logDir + "perfConfig.properties" );
            props.store( fout, "Performance Filter Configuration" );
            fout.close();
            
            
        }
        catch( Throwable e )
        {
            
        }
    }


    public void destroy()
    {
        _filterConfig = null;
        
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
    	try {
	        long xx = System.currentTimeMillis();
	        String url = ((HttpServletRequest)request).getRequestURI().toString();
	        
	        ACTIVE_REQUESTS.put( request, new Long( xx ) );
	        
	        long initTime = System.currentTimeMillis();
	        url = ((HttpServletRequest)request).getServletPath();
	        
	        if( !"/servlet/Builder".equals( url ) && ( METRIC_JSP || METRIC_HTML ) )
	        {
	            if( url != null && url.endsWith("CollectClientStatistics") )
	            {
	                logClient( (HttpServletRequest)request );
	            }
	            else
	            {
	                if( url != null && ( url.endsWith(".jsp") || url.indexOf(".") == -1 ) )
	                {
	
	                    PerfResponseWrapper prfResp = new PerfResponseWrapper( (HttpServletResponse)response );
	                    String page = url.lastIndexOf('/') != -1?url.substring( url.lastIndexOf( "/" ) + 1 ):url;
	                    
	                    
	                    chain.doFilter(request, prfResp );  
	                    
	                    long writeToClient = System.currentTimeMillis();
	                    long processTime   = writeToClient - initTime;
	
	                    ServletOutputStream rout = response.getOutputStream();
	                    if ( METRIC_HTML && prfResp.content_type != null && prfResp.content_type.startsWith( "text/htm" ) && prfResp.out != null )
	                    {
	                        if( !excludeHTML.containsKey( page )  )
	                        {
	                            addPerformanceScript( ((HttpServletRequest)request).getContextPath(), url, rout, prfResp );
	                        }
	                        else
	                        {
	                            rout.write(  prfResp.out.toByteArray() );
	                        }
	                    }
	                    else
	                    { 
	                        rout.write(  prfResp.out.toByteArray() );
	                    }
	                    
	                    if( METRIC_JSP && !excludeJSP.containsKey( page ) )
	                    {
	                        logRequest( (HttpServletRequest)request, url, processTime, System.currentTimeMillis() - writeToClient );
	                    }
	                }
	                else
	                {
	                    chain.doFilter(request, response );        
	                }
	            }
	        }
	        else
	        {
	            chain.doFilter(request, response );
	        }

	        long timeTaken = System.currentTimeMillis() - xx;
	        if( timeTaken > 1500 )
	        {
	            logger.warn("Query Demorada ["+timeTaken+"]:" + url );
	        }
	    }
	    finally
	    {
	        ACTIVE_REQUESTS.remove( request );
	    }
    }
    
    public void addPerformanceScript( String contextPath, String url, ServletOutputStream rout, PerfResponseWrapper prfResp ) throws IOException
    {
        try
        {   
                rout.write( "<script>var prf_initime=new Date();</script>".getBytes() );
                rout.write(  prfResp.out.toByteArray() );
                String afterCode = 
                "<script>\n"+
                "function logClientTime()\n"+
                "{\n"+
                "window.setTimeout(\"sendClientTime()\",10);\n"+
                "}\n"+
                "function sendClientTime()\n"+
                "{\n"+
                "  var par = 'elapsed='+( new Date() - prf_initime )+'&url='+escape(window.location.href)+'&t='+ ( 9999999999999 - (new Date()) );\n"+
                "  var req = new ActiveXObject('Microsoft.XMLHTTP')\n"+
                "  req.open('GET','"+contextPath+"/CollectClientStatistics?' + par,true)\n"+
                "  req.send();\n"+
                "}\n"+
                "window.attachEvent(\"onload\",logClientTime);\n"+
                "</script>\n";
                rout.write( afterCode.getBytes() );
        }
        catch( Throwable e )
        {
            logCliOut = null;
        }
    }
    
    public final void logClient( HttpServletRequest request )
    {
        logRequests++;
        try
        {
            boSession   bsess       = (boSession)request.getSession(true).getAttribute("boSession");
            String      rTime       = sdf.format( new Date( System.currentTimeMillis() ) );
            String      rIp         = request.getRemoteAddr();
            String      rUserName   = bsess!=null?bsess.getUser().getUserName():"-";
            String      rElapsed    = request.getParameter("elapsed");
            String      rUrl        = request.getParameter("url");

            StringBuffer logLine = new StringBuffer();
            logLine.append( rTime );
            logLine.append(";");
            logLine.append( rIp );
            logLine.append(";"); 
            logLine.append( rUserName );
            logLine.append(";");
            logLine.append( rElapsed );
            logLine.append(";");
            logLine.append( rUrl );
            logLine.append(";\"");

            if( logCliOut == null )
            {
                File logFile = new File( logDir + logCliName );
                boolean exists = logFile.exists();

                logger.finer("Initialized new logger to file ["+logFile.getAbsolutePath()+"]");
                logCliOut = new FileWriter( logFile , true ); 
                if(!exists)  logCliOut.write( "Date Hora;IP do Cliente;Utilizador;Tempo de Render;Pagina\n" );
            }
            logLine.append("\n");
            logCliOut.write( logLine.toString() );
            if( logRequests % 10 == 0 )
            {
                logCliOut.flush(); 
                String preFix = logSufix.format( new Date( ) );
                if( !logCliName.startsWith( preFix ) )
                {
                    logCliOut.close();
                    logCliOut = null;
                }
            }
        }
        catch( Throwable e )
        {
            logger.warn( "Erro logging client metrics", e );
        }
    }
    
    public final void logRequest( HttpServletRequest request, String rPage, long time, long writeTime )
    {
        logRequests++;
        try
        {
            boSession   bsess       = (boSession)request.getSession(true).getAttribute("boSession");
            String      rTime       = sdf.format( new Date( System.currentTimeMillis() ) );
            String      rIp         = request.getRemoteAddr();
            String      rUserName   = bsess!=null?bsess.getUser().getUserName():"-";
            String      rElapsed    = String.valueOf( time );
            String      rMethod     = request.getMethod();
            String      rTMem       = String.valueOf( Runtime.getRuntime().totalMemory()/1024/1024 );
            String      rFMem       = String.valueOf( Runtime.getRuntime().freeMemory()/1024/1024 );
            String      rWTime      = String.valueOf( writeTime );
            
            StringBuffer logLine = new StringBuffer();
            logLine.append( rTime );
            logLine.append(";");
            logLine.append( rTMem );
            logLine.append(";");
            logLine.append( rFMem );
            logLine.append(";");
            logLine.append( rIp );
            logLine.append(";");
            logLine.append( rUserName );
            logLine.append(";");
            logLine.append( rElapsed );
            logLine.append(";");
            logLine.append( rWTime );
            logLine.append(";");
            logLine.append( rMethod );
            logLine.append(";");
            logLine.append( rPage );
            logLine.append(";\"");
        
            
            Enumeration enumParam = request.getParameterNames();
            boolean firstPar = true;
            while( enumParam.hasMoreElements() )
            {
                logLine.append( firstPar?"":"&" );
                firstPar = false;
                
                String      parName = enumParam.nextElement().toString();
                String[]    parValues = request.getParameterValues( parName );
                for (int i = 0; i < parValues.length; i++) 
                {
                    if( !"PASSWORD".equalsIgnoreCase( parName ) )
                    {
                        logLine.append( parName );
                        logLine.append( '=' );
                        if( parValues[i].length() > 30 )
                        {
                            parValues[i] = parValues[i].substring(0,30)+"...";
                        }
                        logLine.append( URLEncoder.encode( parValues[i], "UTF-8" ) );
                    }
                }
            }
            logLine.append( "\"" );
            
            if( logSrvOut == null && (METRIC_JSP || METRIC_HTML) )
            {
                File logFile = new File( logDir + logSrvName );
                boolean exists = logFile.exists();

                logger.finer("Initialized new logger to file ["+logFile.getAbsolutePath()+"]");
                logSrvOut = new FileWriter( logFile , true ); 
                if(!exists)  logSrvOut.write( "Date Hora;Memoria Total;Memoria Livre;IP do Cliente;Utilizador;Tempo de Resposta;Escrita da Resposta;Metodo;Pagina;Parametros\n" );
            }


            logLine.append("\n");
            logSrvOut.write( logLine.toString() );
            if( logRequests % 10 == 0 )
            {
                logSrvOut.flush(); 
                String preFix = logSufix.format( new Date( ) );
                if( !logSrvName.startsWith( preFix ) )
                {
                    logSrvOut.close();
                    logSrvOut = null;
                }
            }
        }
        catch( Throwable e )
        {
            logSrvOut = null;
        }
    }
    
    public class PerfResponseWrapper extends HttpServletResponseWrapper
    {
        String                  content_type = null;
        ByteArrayOutputStream   out = null;
        PrintWriter             pw  = null;
        
        public PerfResponseWrapper( HttpServletResponse response )
        {
            super( response );
        }
        
        
        private void initOut()
        {
            if( out == null )
            {
                out = new ByteArrayOutputStream();
            }
        }
        
        public PrintWriter getWriter() throws IOException
        {
            initOut();
            if( pw == null )
            {
                pw = new PrintWriter( out, true );               
            }
            return pw;
        }

        public ServletOutputStream getOutputStream() throws IOException
        {
            initOut();
            return new FilterServletOutputStream( out );
        }

        public void setContentType(String p0)
        {
            // TODO:  Override this javax.servlet.ServletResponseWrapper method
            this.content_type = p0;
            super.setContentType(p0);
        }
        
    }
    
    public class FilterServletOutputStream extends ServletOutputStream {
   
       private DataOutputStream stream;
   
       public FilterServletOutputStream(OutputStream output) {
           stream = new DataOutputStream(output);
       }
   
       public void write(int b) throws IOException {
           stream.write(b);
       }
  
       public void write(byte[] b) throws IOException {
           stream.write(b);
       }
  
       public void write(byte[] b, int off, int len) throws IOException {
           stream.write(b, off, len);
       }
   }
    
    
    public static class RequestMonitorItem extends Thread
    {
        public void run()
        {
            try
            {
                logger.finer( "Request Monitor Started" );
                while( !super.isInterrupted() )
                {
                    try
                    {
                        Enumeration activeRequestsEnum = ACTIVE_REQUESTS.keys();
                        while( activeRequestsEnum.hasMoreElements() )
                        {
                            HttpServletRequest request =  (HttpServletRequest)activeRequestsEnum.nextElement();
                            Long    requestInitTs     = (Long)ACTIVE_REQUESTS.get( request );
                            
                            // Request is active
                            if( requestInitTs != null )
                            {
                                long lElapsedTime = System.currentTimeMillis() - requestInitTs.longValue(); 
                                if( lElapsedTime > MAX_REQUEST_TIME )
                                {
                                    String sUrl;
                                    sUrl = request.getRequestURL().toString().toLowerCase(); 
                                    
                                    if( 
                                        !sUrl.endsWith("__explorer.jsp")
                                        &&
                                        sUrl.startsWith("http://lusworkflow")
                                    )
                                    {
                                        StringBuffer sb = new StringBuffer();
                                        
                                        sb.append("ATENÇÃO Pedido no servidor a demorar ["+ (lElapsedTime/1000) +" sec's]\n");
                                        sb.append("-----------------------------------------------------------").append("\n");
                                        sb.append("Request URL:\n" + request.getRequestURL() ).append("\n");
                                        sb.append("-----------------------------------------------------------").append("\n");
                                        sb.append("Request Headers:").append("\n");
                                        sb.append("-----------------------------------------------------------").append("\n");
                                        Enumeration headerNamesEnum = request.getHeaderNames();
                                        while( headerNamesEnum.hasMoreElements() )
                                        {
                                            String headerName =     (String)headerNamesEnum.nextElement();
                                            String headerValue =    request.getHeader( headerName );
                                            sb.append( headerName ).append( ":" ).append( headerValue ).append( "\n" );
                                        } 
                    
                                        sb.append("-----------------------------------------------------------").append("\n");
                                        sb.append("Request Parameters:").append("\n");
                                        sb.append("-----------------------------------------------------------").append("\n");
                                        Enumeration paramNamesEnum = request.getParameterNames();
                                        while( paramNamesEnum.hasMoreElements() )
                                        {
                                            String paramName =     (String)paramNamesEnum.nextElement();
                                            String paramValue =    request.getParameter( paramName );
                                            sb.append( paramName ).append( ":" ).append( paramValue ).append( "\n" );
                                        }
                                        
                                        if ( sendNotificaError( "joao.carreira@itds.pt;francisco.camara@lusitania.pt;miguel.alves@lusitania.pt", sb.toString() ) )
                                        {   // Ignora o pedido
                                            ACTIVE_REQUESTS.put( request, new Long(System.currentTimeMillis()*2) );
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch( Exception e )
                    {
                        logger.severe( "Erro na Thread de Monitorização de pedidos", e );
                    }
                    Thread.sleep(30000);
                }
            }            
            catch( InterruptedException e )
            {
                // Do nohting
                // Thread was interrupted!
            }
            
        }
        
    public static boolean sendNotificaError(String emailAddress, String text)
    {
        try
        {
            MailMessage mailmsg = MailMessage.getNewMailMessageToSent();

            mailmsg.setSMTPHost(boConfig.getMailConfig().getProperty("smtphost"));

            mailmsg.setPriority(Mail.NORMAL);
            mailmsg.setFrom(new MailAddress("noReply@lusitania.pt"));

            //to - é o email definido no boConfig.xml
            boolean atLeastOne = false;
            String tos[] = emailAddress.split(";");
            for (int i = 0; i < tos.length; i++) 
            {
                mailmsg.addTO("", tos[i]);
                atLeastOne = true;
            }
            if(!atLeastOne) return true;
            
            String sHostName = InetAddress.getLocalHost().getHostName().toLowerCase();            
            
            String msgStart = "Pedidos Demorados no Servidor ["+sHostName+"]";
            mailmsg.setSubject("XEO NTF: " + msgStart);
            mailmsg.setContent(msgStart + ":\n" + text);
            mailmsg.setAskForDeliveredReceipt(false);
            mailmsg.setAskForReadReceipt(false);
            mailmsg.addMailMessage(mailmsg);
            mailmsg.setSendMode("text/plain");
            mailmsg.send(false);
            return true;
        }
         catch (MessagingException e)
        {
            e.printStackTrace();
            return false;
        }
         catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
         catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
        
    }
    
}