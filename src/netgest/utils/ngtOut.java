/*Enconding=UTF-8*/
package netgest.utils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import netgest.bo.*;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;
public class ngtOut  
{
    private static Logger logger = Logger.getLogger("netgest.utils.ngtOut");

    private static SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
    
    public static final void DebugInf(String msg){
        if(logger == null)
        {
            System.out.println(dtf.format(new Date())+" - "+"DBG:"+msg);
        }
        else
        {
            //pq do builder
            System.out.println(dtf.format(new Date())+" - "+"DBG:"+msg);
            logger.finest(msg);
        }
    }
    public static final void WarningInf(String msg){
        if(logger == null)
        {
            System.out.println(dtf.format(new Date())+" - "+"WRN:"+msg);
        }
        else
        {
            //pq do builder
            System.out.println(dtf.format(new Date())+" - "+"WRN:"+msg);
            logger.warn(msg);
        }
    }
    public static final void criticalInf(String msg){
        if(logger == null)
        {
            System.out.println(dtf.format(new Date())+" - "+"*** CRITICAL *** :"+msg);
        }
        else
        {
            //pq do builder
            System.out.println(dtf.format(new Date())+" - "+"*** CRITICAL *** :"+msg);
            logger.severe(msg);
        }
    }

    public static final void errorInf(String msg){
        if(logger == null)
        {
            System.out.println(dtf.format(new Date())+" - "+"ERROR :"+msg);
        }
        else
        {
            logger.severe(msg);
        }
    }    

    public static final void info(String msg){
        if(logger == null)
        {
            System.out.println(dtf.format(new Date())+" - "+"INFO :"+msg);
        }
        else
        {
            logger.finer(msg);
        }
    } 

//------ writing class and method name
    public static final void DebugInf(String msg,String ClassName,String Method){        
        if(logger == null)
        {
            System.out.print(dtf.format(new Date())+" - "+"DBG:"+msg);
            System.out.println(" >>>> From  "+ClassName+"."+Method);
        }
        else
        {
            //pq do builder
            System.out.print(dtf.format(new Date())+" - "+"DBG:"+msg);
            System.out.println(" >>>> "+LoggerMessageLocalizer.getMessage("FROM")+"  "+ClassName+"."+Method);
            logger.finest(msg + " >>>> "+LoggerMessageLocalizer.getMessage("FROM")+"  "+ClassName+"."+Method);
        }
    }
    public static final void WarningInf(String msg,String ClassName,String Method){
        if(logger == null)
        {
            System.out.print(dtf.format(new Date())+" - "+"WRN:"+msg);
            System.out.println(" >>>> From  "+ClassName+"."+Method);
        }
        else
        {
            //pq do builder
            System.out.print(dtf.format(new Date())+" - "+"WRN:"+msg);
            System.out.println(" >>>> From  "+ClassName+"."+Method);
            logger.warn(msg + " >>>> From  "+ClassName+"."+Method);
        }
    }
     public static final void criticalInf(String msg,String ClassName,String Method){        
        if(logger == null)
        {
            System.out.print(dtf.format(new Date())+" - "+"DBG:"+msg);
            System.out.println(" >>>> "+LoggerMessageLocalizer.getMessage("FROM")+"  "+ClassName+"."+Method);
        }
        else
        {
            logger.severe(msg + " >>>> "+LoggerMessageLocalizer.getMessage("FROM")+"  "+ClassName+"."+Method);
        }
    }
    public static final void errorInf(String msg,String ClassName,String Method){
        if(logger == null)
        {
            System.out.print(dtf.format(new Date())+" - "+"WRN:"+msg);
            System.out.println(" >>>> "+LoggerMessageLocalizer.getMessage("FROM")+"  "+ClassName+"."+Method);
        }
        else
        {
            logger.severe(msg + " >>>> "+LoggerMessageLocalizer.getMessage("FROM")+"  "+ClassName+"."+Method);
        }
    }
     public static final void info(String msg,String ClassName,String Method){        
        if(logger == null)
        {
            System.out.print(dtf.format(new Date())+" - "+"DBG:"+msg);
            System.out.println(" >>>> "+LoggerMessageLocalizer.getMessage("FROM")+"  "+ClassName+"."+Method);
        }
        else
        {
            logger.finer(msg + " >>>> "+LoggerMessageLocalizer.getMessage("FROM")+"  "+ClassName+"."+Method);
        }
    }
//--------------------------------------
//--------writing exception
    public static final void DebugInf(String msg, Throwable exception){
        if(logger == null)
        {
             exception.printStackTrace(System.out);
        }
        else
        {
            logger.finest(msg, exception);
        }
    }
    public static final void WarningInf(String msg, Throwable exception){
        if(logger == null)
        {
             exception.printStackTrace(System.out);
        }
        else
        {
            logger.warn(msg, exception);
        }
    }
    public static final void criticalInf(String msg, Throwable exception){
        if(logger == null)
        {
             exception.printStackTrace(System.out);
        }
        else
        {
            logger.severe(msg, exception);
        }
    }

    public static final void errorInf(String msg, Throwable exception){
        if(logger == null)
        {
             exception.printStackTrace(System.out);
        }
        else
        {
            logger.severe(msg, exception);
        }
    }    

    public static final void info(String msg, Throwable exception){
        if(logger == null)
        {
             exception.printStackTrace(System.out);
        }
        else
        {
            logger.finer(msg, exception);
        }
    }
    
    //no message
        public static final void DebugInf(Throwable exception){
        if(logger == null)
        {
            exception.printStackTrace(System.out);
        }
        else
        {
            logger.finest("", exception);
        }
    }
    public static final void WarningInf(Throwable exception){
        if(logger == null)
        {
             exception.printStackTrace(System.out);
        }
        else
        {
            logger.warn("", exception);
        }
    }
    public static final void criticalInf(Throwable exception){
        if(logger == null)
        {
             exception.printStackTrace(System.out);
        }
        else
        {
            logger.severe("", exception);
        }
    }

    public static final void errorInf(Throwable exception){
        if(logger == null)
        {
             exception.printStackTrace(System.out);
        }
        else
        {
            logger.severe("", exception);
        }
    }    

    public static final void info(Throwable exception){
        if(logger == null)
        {
             exception.printStackTrace(System.out);
        }
        else
        {
            logger.finer("", exception);
        }
    }

    
//------------------------------
    public static final void writeToDebugFile(String msg)
    {
        logger.severe(msg);
//          String configpath = "netgest/";
//          String filename   = "ngterror.log";
//          String fullpath=null;
//          if(System.getProperties().containsKey("oracle.j2ee.home")) {
//              configpath = (String)System.getProperties().get("oracle.j2ee.home");
//          }
//          if(System.getProperties().containsKey("netgest.home")) {
//              configpath = (String)System.getProperties().get("netgest.home");
//          }
//          if(configpath != null) {
//            if(!configpath.endsWith("/") && !configpath.endsWith("\\"))
//                configpath += "/";
//          }
//          FileWriter xfr=null;
//          try
//          {
//              xfr = new FileWriter( configpath.trim()+filename.trim() ,true );
//              xfr.write(dtf.format(new Date())+" - "+":"+msg);
//              xfr.close();
//          }
//          catch (IOException e)
//          {
//              WarningInf("Error wrinting ngterror.log\n"+e.getClass().getName()+"\n"+e.getMessage());
//          }
//          finally
//          {
//              try
//              {
//                  if( xfr != null ) xfr.close();              
//              }
//              catch (Exception e)
//              {
//                  
//              }
//          }
        
    }
}