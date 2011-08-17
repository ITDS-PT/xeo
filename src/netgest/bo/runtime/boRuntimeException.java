/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.util.*;
import netgest.utils.*;
import java.io.PrintStream;
import java.io.PrintWriter;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.system.Logger;

public class boRuntimeException extends Exception {
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.boRuntimeException");
    
    public static Hashtable p_errors;
    private String p_message;
    private Throwable p_baseexception;
    private String p_code;
    private boObject p_srcobj;
    
    public boRuntimeException(String src,String code,Throwable base) {
    	super( base );
        init(src,code,base,(String[])null);
    }
    public boRuntimeException(String src,String code,Throwable base,String[] args) {
    	super( base );
        init(src,code,base,args);
    }
    public boRuntimeException(String src,String code,Throwable base,String args) {
    	super( base );
        String[] x= {args};
        init(src,code,base,x);
    }    
    public boRuntimeException(boObject srcobject,String src,String code,Throwable base) {
    	super( base );
        p_srcobj=srcobject;
        init(src,code,base,(String[])null);
    }
    public boRuntimeException(boObject srcobject,String src,String code,Throwable base,String[] args) {
    	super( base );
        p_srcobj=srcobject;
        init(src,code,base,args);
    }
    public boRuntimeException(boObject srcobject,String src,String code,Throwable base,String args) {
    	super( base );
        String[] x= {args};
        p_srcobj=srcobject;
        init(src,code,base,x);
    }
    
    private void init(String src,String code,Throwable base,String[] args) {
        if(p_errors == null ) initErrorCodes();
        p_code = code;
        p_baseexception = base;
        String xmsg = (String)p_errors.get(code);
        p_message = src+":"+code+":"+(xmsg!=null? xmsg:"")+(base!=null? "\n"+MessageLocalizer.getMessage("BASE_ERROR")+":"+base.getMessage():"");
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
            p_errors.put("BO-3001",MessageLocalizer.getMessage("ERROR_BUILDING_OBJECTS"));
            p_errors.put("BO-3001",MessageLocalizer.getMessage("ERROR_LOADING_OBJECT_WITH_QUERY"));
            p_errors.put("BO-3002",MessageLocalizer.getMessage("ERROR_UPDATING_ATTRIBUTE_ON"));
            p_errors.put("BO-3003",MessageLocalizer.getMessage("ERROR_READING_ATTRIBUTE_ON"));
            p_errors.put("BO-3004",MessageLocalizer.getMessage("ERROR_ADDING_MULTI_OBJECT_ATTRIBUTE_ON"));
            p_errors.put("BO-3005",MessageLocalizer.getMessage("ERROR_REMOVING_MULTI_OBJECT_ATTRIBUTE_ON"));
            p_errors.put("BO-3006",MessageLocalizer.getMessage("ERROR_MARSHALLING_OBJECT"));
            p_errors.put("BO-3007",MessageLocalizer.getMessage("ERROR_SERIALIZING_XML"));
            p_errors.put("BO-3008",MessageLocalizer.getMessage("ERROR_UNMARSHALING_OBJECT"));
            p_errors.put("BO-3009",MessageLocalizer.getMessage("ERROR_BINDING_FIELD_IN_OBJECT"));
            p_errors.put("BO-3010",MessageLocalizer.getMessage("OBJECT_REGISTRY_FAILED_FOR_OBJECT"));

            p_errors.put("BO-3011",MessageLocalizer.getMessage("BOKERNEL_ERROR_PARSING_ARGUMENT_TYPE"));
            p_errors.put("BO-3012",MessageLocalizer.getMessage("ERROR_CONVERTING_ARGUMENT_VALUE_FROM_STRING"));
            p_errors.put("BO-3013",MessageLocalizer.getMessage("ERROR_CONVERTING_ARGUMENT_VALUE_DATA_TYPE_NOT_SUPPORTED"));

            p_errors.put("BO-3014",MessageLocalizer.getMessage("ERROR_MARSHALING_OBJECT_ARGUMENTS"));
            p_errors.put("BO-3015",MessageLocalizer.getMessage("OBJECT_WITH_BOUI_NOT_FOUND"));

            p_errors.put("BO-3016",MessageLocalizer.getMessage("ERROR_UPDATING_OBJECT"));
            p_errors.put("BO-3017",MessageLocalizer.getMessage("CANNOT_LOAD_A_OBJECT_WITH_MULTI_RESULT_OBJECT"));
            p_errors.put("BO-3018",MessageLocalizer.getMessage("CANNOT_LOAD_A_OBJECT_WITH_BOUI0"));

            p_errors.put("BO-3019",MessageLocalizer.getMessage("OBJECT_DOESNT_EXIST"));
            p_errors.put("BO-3020",MessageLocalizer.getMessage("CLASS_WITH_BOUI_DOES_NOT_EXIST"));
        
            p_errors.put("BO-3021",MessageLocalizer.getMessage("CANNOT_SAVE_OBJECT_BECAUSE_THERE_ARE_INVALID_ATTRIBUTES"));
            p_errors.put("BO-3022",MessageLocalizer.getMessage("CANNOT_SAVE_OBJECT_BECAUSE_IT_WAS_CHANGED_BY_ANOTHER_USER"));
            p_errors.put("BO-3023",MessageLocalizer.getMessage("CANNOT_DESTROI_OBJECT_BECAUSE_THERE_ARE_DATABASE_REF_KEY_FOR_THIS"));

            p_errors.put("BO-3024",MessageLocalizer.getMessage("CANNOT_READ_A_SIGLE_VALUE_FROM_A_MULTI_VALUE_ATTRIBUTE"));

            p_errors.put("BO-3025",MessageLocalizer.getMessage("ERROR_CREATING_A_OBJECT_FROM_A_EXISTING"));

            p_errors.put("BO-3050",MessageLocalizer.getMessage("ERROR_ENQUEING_OBJECT_OPERATION"));
            p_errors.put("BO-3051",MessageLocalizer.getMessage("ERROR_CREATING_SAVEPOINT"));
            p_errors.put("BO-3052",MessageLocalizer.getMessage("ERROR_ROLLING_BACK_OBJECT"));


//  Not used           p_errors.put("BO-3053","Cannot insert, object generate a duplicated Primary Keys in the table while inserting. \n [:1]");
            p_errors.put("BO-3054",MessageLocalizer.getMessage("CANNOT_UPDATEINSERT_OBJECT_GENERATE_DUPLICATE_UNIKEKEY_IN_"));
            p_errors.put("BO-3055",MessageLocalizer.getMessage("ERROR_UPDATING_OBJECT_DATA"));


            p_errors.put("BO-3056",MessageLocalizer.getMessage("ERROR_REGISTERING_EXTERNAL_OBJECT_DATA"));

            p_errors.put("BO-3999",MessageLocalizer.getMessage("UNEXPECTED_ERROR_"));

            
            p_errors.put("BO-3100",MessageLocalizer.getMessage("ERROR_LOADING_VIEWER"));
            p_errors.put("BO-3110",MessageLocalizer.getMessage("ERROR_LOADING_MASTERBOLIST"));
            p_errors.put("BO-3120",MessageLocalizer.getMessage("ERROR_INVOKING_METHOD"));
            p_errors.put("BO-3121",MessageLocalizer.getMessage("ERROR_CREATING_TRANSATIONAL"));
            p_errors.put("BO-3150",MessageLocalizer.getMessage("ERROR_TRANSACTION"));
            p_errors.put("BO-3200",MessageLocalizer.getMessage("ERROR_UPDATING_OBJECT_NOT_ENOUGH_PERMISSIONS"));
            p_errors.put("BO-3210",MessageLocalizer.getMessage("ERROR_DELETING_OBJECT_NOT_ENOUGH_PERMISSIONS"));
            p_errors.put("BO-3220",MessageLocalizer.getMessage("ERROR_READING_OBJECT_NOT_ENOUGH_PERMISSIONS"));
            p_errors.put("BO-3230",MessageLocalizer.getMessage("ERROR_NOT_ENOUGH_PERMISSIONS"));
            
            p_errors.put("BO-3300","Object reference error. The attribute [:1] on the object [:2](:3) has a invalid boui [:4]");
            
            // ERRORS for WORKFLOW
            p_errors.put("WKFL-000",MessageLocalizer.getMessage("UPDATING_WF"));
            p_errors.put("WKFL-001",MessageLocalizer.getMessage("CANNOT_CHANGE_STATE_FROM_TO"));
            
            p_errors.put("WKFL-002",MessageLocalizer.getMessage("CANNOT_ADD_DEPEDENCY_TO_THIS_OBJECT"));
            
       }
    }

    public void printStackTrace() {
        // TODO:  Override this java.lang.Throwable method
        if(p_baseexception!=null) p_baseexception.printStackTrace();
        logger.severe(LoggerMessageLocalizer.getMessage("NEXTED_EXCEPTION_IS"), this);
        super.printStackTrace();
    }

    public void printStackTrace(PrintStream s) {
        // TODO:  Override this java.lang.Throwable method
        if(p_baseexception!=null) p_baseexception.printStackTrace(s);
        s.println(LoggerMessageLocalizer.getMessage("NEXTED_EXCEPTION_IS"));
        super.printStackTrace(s);
    }

    public void printStackTrace(PrintWriter s) {
        // TODO:  Override this java.lang.Throwable method
        if(p_baseexception!=null) p_baseexception.printStackTrace(s);
        s.println(LoggerMessageLocalizer.getMessage("NEXTED_EXCEPTION_IS"));
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
