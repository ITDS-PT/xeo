/*Enconding=UTF-8*/
package netgest.bo.builder;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import netgest.bo.boConfig;


public class boClassCompiler  {

    public String beforecode="";
    public String execcode="";
    public String aftercode="";
    public Process proc;
    
    
    public void compile(String srcdir,File[] srccode,String outputdir) throws RuntimeException {
      try {
          String xjavac = null;
          String ngtjar = null;
          File clsdir = new File(outputdir);
          clsdir.mkdirs();
 
          xjavac =new boConfig().getCompilerdir(); 

          if (xjavac == null || xjavac.length()==0) 
              throw(new RuntimeException("Node (pathjavac) A Path para o compilador de java não foi especificada no boconfig.xml"));
          
          Runtime rt = Runtime.getRuntime();
          String classpath ="";
          
          boolean isWindows = true;
                    
          String osName = System.getProperty("os.name");
          if ( !osName.matches(".*(?i)(windows).*") ) {
              isWindows = false;
          }                  
          // JBOSS/WebLogic(and possibly others): Construct the classpath based on the xeoHome/lib folder          
          if ((System.getProperty("jboss.server.home.dir")!=null && 
            !"".equals(System.getProperty("jboss.server.home.dir"))) ||
            (System.getProperty("weblogic.home")!=null && !"".equals(System.getProperty("weblogic.home")))) {

            if( isWindows )
                classpath = "\"" + this.getClassPath(boConfig.getApplicationConfig().getLibDir(),classpath) + "\"";
            else
                classpath = this.getClassPath(boConfig.getApplicationConfig().getLibDir(),classpath);
          } 
          else 
          {
            if( isWindows )
                classpath = "\"" + System.getProperty("java.class.path") +"\"";    
            else
                classpath = System.getProperty("java.class.path");    
          }
          
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
              outnorm.destroy();
              outerr.destroy();
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
                throw(new Exception("Error a compilar class"));
          }
          String msgs = rd1.dataReaded;
          String error = rd2.dataReaded;

          if (error.length() > 0) {
               throw(new RuntimeException("Erro a compilar class:\n"+error));
          }

          return;
      } catch (Exception e) {
          e.printStackTrace();
          throw(new RuntimeException(e));
      }
    }      
      private String getClassPath(String beginpath, String classpath)
      {
          File xeoLib = new File(beginpath);          
          File[] libs = xeoLib.listFiles();
          for (int i=0; i<libs.length; i++) {
            
            if (libs[i].isDirectory())
              classpath+=  File.pathSeparator + this.getClassPath(libs[i].getAbsolutePath(),classpath);
            else if (libs[i].getAbsolutePath().indexOf(".jar")!=-1 || 
                     libs[i].getAbsolutePath().indexOf(".zip")!=-1 ||  
                     libs[i].getAbsolutePath().indexOf(".class")!=-1)
             classpath+= File.pathSeparator + libs[i].getAbsolutePath().replaceAll("\\s","\\\\");
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
                    proc.waitFor();
                } catch (InterruptedException e) {

                }
            }
      }    

    private class filenamefilter implements FilenameFilter {
        String p_classname;
        public filenamefilter(String classname) {
            p_classname = classname;
        }

        public boolean accept(File dir,String fn) {
            return fn.startsWith(p_classname) && fn.endsWith(".class");
        }
    }
}