/*Enconding=UTF-8*/
package netgest.exceptions;
import java.util.*;
import java.io.*;
import netgest.utils.*;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.system.Logger;

public class ngtSystemException extends RuntimeException {
    //logger
    private static Logger logger = Logger.getLogger("netgest.exceptions.ngtSystemException");

    public static Hashtable p_errors;
    private String p_message;
    private Exception p_baseexception;
    public ngtSystemException(String src,String code,Exception base) {
        init(src,code,base,(String[])null);
    }
    public ngtSystemException(String src,String code,Exception base,String[] args) {
        init(src,code,base,args);
    }
    private void init(String src,String code,Exception base,String[] args) {
        if(p_errors == null ) initErrorCodes();
        p_baseexception = base;
        String xmsg = (String)p_errors.get(code);
        p_message = src+":"+code+":"+xmsg+"\n"+MessageLocalizer.getMessage("BASE_ERROR")+":"+base.getMessage();
        int i=0;
        if(args!=null) {
            for(i=0;i<args.length;i++) {
                p_message = StringUtils.replacestr(p_message,":"+(i+1),args[i]);
            }
        }
        p_message = StringUtils.replacestr(p_message,":"+(i+1),base.getMessage());
    }
    private void initErrorCodes() {
       if(p_errors==null) {
            p_errors = new Hashtable();
            // Workspace Erros
            p_errors.put("NGT-3001",MessageLocalizer.getMessage("WORKSPACE_NOT_FOUND"));
       }    
    }

    public void printStackTrace() {
        // TODO:  Override this java.lang.Throwable method
        if(p_baseexception!=null) p_baseexception.printStackTrace();
        logger.severe(LoggerMessageLocalizer.getMessage("NEXTED_EXCEPTION_IS")+":", this);
        super.printStackTrace();
    }

    public void printStackTrace(PrintStream s) {
        // TODO:  Override this java.lang.Throwable method
        if(p_baseexception!=null) p_baseexception.printStackTrace(s);
        s.println(LoggerMessageLocalizer.getMessage("NEXTED_EXCEPTION_IS")+":");
        super.printStackTrace(s);
    }

    public void printStackTrace(PrintWriter s) {
        // TODO:  Override this java.lang.Throwable method
        if(p_baseexception!=null) p_baseexception.printStackTrace(s);
        s.println(LoggerMessageLocalizer.getMessage("NEXTED_EXCEPTION_IS")+":");
        super.printStackTrace(s);
    }

    public String getMessage() {
        // TODO:  Override this java.lang.Throwable method
        return this.p_message;
    }
}