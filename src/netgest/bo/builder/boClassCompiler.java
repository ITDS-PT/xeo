/*Enconding=UTF-8*/
package netgest.bo.builder;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.system.Logger;

import netgest.bo.boConfig;


public class boClassCompiler  {

    public String beforecode="";
    public String execcode="";
    public String aftercode="";
    public Process proc;
  
    private static Logger logger = Logger.getLogger("netgest.bo.builder.boBuilder");
    public void compile(String srcdir,File[] srccode,String outputdir) throws RuntimeException {
    	this.compile(srcdir, srccode, outputdir,null);
    }
    
    public void compile(String srcdir,File[] srccode,String outputdir,String additionalClassPath) throws RuntimeException {
      try {
          String xjavac = null;
          File clsdir = new File(outputdir);
          clsdir.mkdirs();
 
          xjavac =boConfig.getCompilerdir(); 
          File xjavacFile = new File( xjavac );
          
          boolean isWindows = true;
          
          String osName = System.getProperty("os.name");
          if ( !osName.matches(".*(?i)(windows).*") ) {
              isWindows = false;
          }                  
          
          if (xjavac == null || xjavac.trim().length()==0 || !xjavacFile.exists() || xjavacFile.isDirectory() )  {
        	  
        	  String javacName = "bin" + File.separator + "javac" + (isWindows?".exe":"");
        	  String javaHome = System.getProperty("java.home");
        	  File	 javaFile = new File( javaHome + File.separator + javacName );
        	  if( javaFile.exists() ) {
        		  xjavac = javaFile.getAbsolutePath();
        		  xjavacFile = javaFile;
        	  }
        	  
        	  javaHome = (new File( javaHome ).getParent()) +  File.separator;
        	  javaFile = new File( javaHome + File.separator + javacName );
        	  if( javaFile.exists() ) {
        		  if( !(xjavac == null || xjavac.trim().length()==0 || xjavacFile.isDirectory()) )
        			  logger.warn(LoggerMessageLocalizer.getMessage("JAVAC_IN_BOCONFIG_NOT_FOUND_USING_THE_DEFAULT")+" [" + javaFile.getAbsolutePath() + "] ");
        		  xjavac = javaFile.getAbsolutePath();
        		  xjavacFile = javaFile; 
        	  }
          }
         
          if (xjavac == null || xjavac.trim().length()==0 )  {
              throw(new RuntimeException(MessageLocalizer.getMessage("NODE_THE_PATH_FOR_THE_JAVA_COMPILER")));
          }
          else if ( !xjavacFile.exists() )   {
              throw(new RuntimeException(MessageLocalizer.getMessage("NODE_JAVAC_NOT_FOUND")+" [" + xjavacFile.getAbsolutePath() + "] "));
          }
          
          
          Runtime rt = Runtime.getRuntime();
          String classpath ="";
          
          // JBOSS/WebLogic(and possibly others): Construct the classpath based on the xeoHome/lib folder          
          if ((System.getProperty("jboss.server.home.dir")!=null && 
            !"".equals(System.getProperty("jboss.server.home.dir"))) ||
            (System.getProperty("weblogic.home")!=null && !"".equals(System.getProperty("weblogic.home")))) {        	  
        	  
        	  classpath = this.getClassPath(boConfig.getApplicationConfig().getLibDir(),classpath);                
          } 
          else 
          {
                classpath = System.getProperty("java.class.path");    
          }
          
          if (additionalClassPath!=null && !additionalClassPath.equals(""))
        	  classpath+=File.pathSeparator+additionalClassPath;
          
          if (isWindows)
        	  classpath="\""+classpath+"\"";
          
          String srcs = "";
          for (int i = 0; i < srccode.length; i++)  {
              srcs += " " + srccode[i].getAbsolutePath();
          }
          String runcmd;
          String encoding = boConfig.getEncoding();
          
          String java_15_option="";
          
          java_15_option=" -source 1.4 -target 1.4 ";
          if(encoding!=null) 
            runcmd = xjavac+java_15_option+" -encoding "+encoding+" -g -classpath "+classpath+" -sourcepath "+srcdir+" -d "+ outputdir +srcs;
          else
            runcmd = xjavac+java_15_option+" -g -classpath "+classpath+" -sourcepath "+srcdir+" -d "+ outputdir +srcs;

          proc = rt.exec(runcmd);

          InputStream compis = proc.getErrorStream();
          InputStream output = proc.getInputStream();
          ReadStreams rd1 = new ReadStreams(output);
          ReadStreams rd2 = new ReadStreams(compis);
          Thread outnorm = new Thread(rd1);
          Thread outerr = new Thread(rd2);
          outnorm.start();
          outerr.start();
          final int MAXTIMERUN = 120000;
          final int MAXTIMEEXIT= 10000;
          int time=0;
          while(outnorm.isAlive() && outerr.isAlive() && time < MAXTIMERUN ) {
              Thread.sleep(1000);
              time +=1000;
          }
          if(time >= MAXTIMERUN) {

          }
          //Wait for process exit.
          Thread we = new Thread(new waiforproc(proc));
          time = 0;
          while(we.isAlive() && time < MAXTIMEEXIT ) {
                Thread.sleep(1000);
                time+=1000;
          }
          if(time >= MAXTIMEEXIT) {
                proc.destroy();
                throw(new Exception(MessageLocalizer.getMessage("ERROR_COMPILING_CLASS")));
          }

          String error = rd2.dataReaded;

          if (error.length() > 0) {
               throw(new RuntimeException(MessageLocalizer.getMessage("ERROR_COMPILING_CLASS")+":\n"+error));
          }

          return;
      } catch (Exception e) {
          e.printStackTrace();
          throw(new RuntimeException(e));
      }
    }      
      private String getClassPath(String beginpath, String classpath)
      {
    	  String[] beginPaths = beginpath.split( File.pathSeparator ); 
		  for( String path : beginPaths ) {
	          File xeoLib = new File(path);
	          File[] libs = xeoLib.listFiles();
	          for (int i=0;libs != null && i<libs.length; i++) {
	            if (libs[i].isDirectory()) {
	            	if( classpath != null && classpath.length() > 0 )
	            		classpath +=  File.pathSeparator;
	            	
	              classpath +=  this.getClassPath(libs[i].getAbsolutePath(),classpath);
	            }
	            else if (libs[i].getAbsolutePath().indexOf(".jar")!=-1 || 
	                     libs[i].getAbsolutePath().indexOf(".zip")!=-1 ||  
	                     libs[i].getAbsolutePath().indexOf(".class")!=-1) {
	            	
	            	if( classpath != null && classpath.length() > 0 )
	            		classpath +=  File.pathSeparator;
	            	
	            	classpath += libs[i].getAbsolutePath().replaceAll("\\s","\\\\");
	            }
	          }
		  }
          return classpath;
      }
      
    private class ReadStreams implements Runnable {
        public String dataReaded = "";
        public InputStream p_is;
        public ReadStreams(InputStream is) {
            p_is = is;
        }
        public void run() {
            try {
                byte[] buff = new byte[4096];
                int br=0;
                while((br=p_is.read(buff))>0) {
                    dataReaded += new String(buff,0,br);
                }
            } catch (IOException e){
            }
        }
    }
    private class waiforproc implements Runnable {
      private Process p_proc;
      public waiforproc(Process proc) {
          p_proc = proc;
      }
      public void run() {
                try {
                	p_proc.waitFor();
                } catch (InterruptedException e) {

                }
            }
      }    


}