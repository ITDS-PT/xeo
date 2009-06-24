/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.util.*;
import netgest.utils.*;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.log4j.Logger;

public class boRuntimeException extends Exception {
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.boRuntimeException");
    
    public static Hashtable p_errors;
    private String p_message;
    private Throwable p_baseexception;
    private String p_code;
    private boObject p_srcobj;
    
    public boRuntimeException(String src,String code,Throwable base) {
        init(src,code,base,(String[])null);
    }
    public boRuntimeException(String src,String code,Throwable base,String[] args) {
        init(src,code,base,args);
    }
    public boRuntimeException(String src,String code,Throwable base,String args) {
        String[] x= {args};
        init(src,code,base,x);
    }    
    public boRuntimeException(boObject srcobject,String src,String code,Throwable base) {
        p_srcobj=srcobject;
        init(src,code,base,(String[])null);
    }
    public boRuntimeException(boObject srcobject,String src,String code,Throwable base,String[] args) {
        p_srcobj=srcobject;
        init(src,code,base,args);
    }
    public boRuntimeException(boObject srcobject,String src,String code,Throwable base,String args) {
        String[] x= {args};
        p_srcobj=srcobject;
        init(src,code,base,x);
    }
    private void init(String src,String code,Throwable base,String[] args) {
        if(p_errors == null ) initErrorCodes();
        p_code = code;
        p_baseexception = base;
        String xmsg = (String)p_errors.get(code);
        p_message = src+":"+code+":"+(xmsg!=null? xmsg:"")+(base!=null? "\nErro Base:"+base.getMessage():"");
        int i=0;
        if(args!=null) {
            for(i=0;i<args.length;i++) {
//                p_message = p_message.replaceAll(":"+(i+1), args[i]);            
                p_message = StringUtils.replacestr(p_message,":"+(i+1),args[i]);
            }
        }
//        p_message = p_message.replaceAll(":"+(i+1),base!=null?base.getMessage():"");
        p_message = StringUtils.replacestr(p_message,":"+(i+1),base!=null?base.getMessage():"");
    }
    private void initErrorCodes() {
       if(p_errors==null) {
            p_errors = new Hashtable();
            p_errors.put("BO-3001","Error building objects\n :baserror");
            p_errors.put("BO-3001","Error loading object [:1] with query [:2]\n :baserror");
            p_errors.put("BO-3002","Error updating attribute [:1] on [:2]\n :baserror");
            p_errors.put("BO-3003","Error reading attribute [:1] on [:2]\n :baserror");
            p_errors.put("BO-3004","Error adding multi object attribute [:1] on [:2]\n :baserror");
            p_errors.put("BO-3005","Error removing multi object attribute [:1] on [:2]\n :baserror");
            p_errors.put("BO-3006","Error marshalling Object [:1] \n :baserror");
            p_errors.put("BO-3007","Error Serializing XML \n :baserror");
            p_errors.put("BO-3008","Error unmarshaling Object [:1] \n :baserror");
            p_errors.put("BO-3009","Error binding field [:1] in Object [:2] \n :baserror");
            p_errors.put("BO-3010","Object registry failed for Object [:1] \n :baserror");

            p_errors.put("BO-3011","BoKernel Error parsing Argument type [:1]");
            p_errors.put("BO-3012","Error converting argument value from String to [:1] \n :baseerror");
            p_errors.put("BO-3013","Error converting argument value.\n Data type not supported [:1] \n :baseerror");

            p_errors.put("BO-3014","Error marshalling Object arguments \n :baserror");
            p_errors.put("BO-3015","Object with boui [:1] not found.");

            p_errors.put("BO-3016","Error updating Object [:1] \n :baserror");
            p_errors.put("BO-3017","Cannot load a Object with multi results Object [:1] \n :baserror");
            p_errors.put("BO-3018","Cannot load a Object with BOUI = 0.\n :baserror");

            p_errors.put("BO-3019","Object doesn't exist [:1]");
            p_errors.put("BO-3020","Class with boui [:1] does not exist.");
        
            p_errors.put("BO-3021","Cannot save object because there are invalid attributes.\n [:1]");
            p_errors.put("BO-3022","Cannot save object because it was changed by writed by another user \n [:1]");
            p_errors.put("BO-3023","Cannot destroy object because there are database references keys for this. ( Foreign Keys ) \n [:1]");

            p_errors.put("BO-3024","Cannot read a single value from a multi value attrribute [:1] ");

            p_errors.put("BO-3025","Error creating a Object from a exsting. :baserror");

            p_errors.put("BO-3050","Erro enqueing Object Operation \n :baserror");
            p_errors.put("BO-3051","Error creating savepoint \n :baserror");
            p_errors.put("BO-3052","Error rolling back Object \n :baserror");


//  Not used           p_errors.put("BO-3053","Cannot insert, object generate a duplicated Primary Keys in the table while inserting. \n [:1]");
            p_errors.put("BO-3054","Cannot update/insert, object generate a duplicated Unique Key in the table while updating/inserting. \n [:1]");
            p_errors.put("BO-3055","Error updating object data. \n :baseerror");


            p_errors.put("BO-3056","Error registering external object data \n :baseerror");

            p_errors.put("BO-3999","Unexpected Error!!! \n :baseerror");

            
            p_errors.put("BO-3100","Error loading viewer [:1]\n :baserror");
            p_errors.put("BO-3110","Error loading MasterBolist [:1]\n :baserror");
            p_errors.put("BO-3120","Error invoking method [:1]\n :baserror");
            p_errors.put("BO-3121","Error creating transational [:1] \n :baserror");
            p_errors.put("BO-3150","Error [:1] transaction \n :baserror");
            p_errors.put("BO-3200","Error updating object [:1]. Not enough permissions.\n :baserror");
            p_errors.put("BO-3210","Error deleting object [:1]. Not enough permissions.\n :baserror");
            p_errors.put("BO-3220","Error reading object [:1]. Not enough permissions.\n :baserror");
            p_errors.put("BO-3230","Error [:1]. Not enough permissions.\n :baserror");
        
            // ERRORS for WORKFLOW
            p_errors.put("WKFL-000","A actualizar WF \n :baserror ");
            p_errors.put("WKFL-001","Não é possível executar mudança de estado de [:1] para [:2] ");
            
            p_errors.put("WKFL-002","Não é possível adicionar uma dependencia neste objecto [:1] ");
            
       }
    }

    public void printStackTrace() {
        // TODO:  Override this java.lang.Throwable method
        if(p_baseexception!=null) p_baseexception.printStackTrace();
        logger.error("Nexted expcetion is:", this);
        super.printStackTrace();
    }

    public void printStackTrace(PrintStream s) {
        // TODO:  Override this java.lang.Throwable method
        if(p_baseexception!=null) p_baseexception.printStackTrace(s);
        s.println("Nexted expcetion is:");
        super.printStackTrace(s);
    }

    public void printStackTrace(PrintWriter s) {
        // TODO:  Override this java.lang.Throwable method
        if(p_baseexception!=null) p_baseexception.printStackTrace(s);
        s.println("Nexted expcetion is:");
        super.printStackTrace(s);
    }

    public String getMessage() {
        // TODO:  Override this java.lang.Throwable method
        return this.p_message;
    }
    public String getErrorCode() {
        return p_code;
    }
    public boObject getSrcObject() 
    {
        return p_srcobj;            
    }
}
