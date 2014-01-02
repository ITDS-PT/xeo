/*Enconding=UTF-8*/
package netgest.bo.system;
import java.io.*;
import java.util.Hashtable;

import netgest.bo.localizations.MessageLocalizer;

public class boLoginException extends Exception  {
    public static Hashtable ht = new Hashtable();
    
    public static final String INVALID_CREDENCIALS = "BO-4000";
    public static final String UNEXPECTED_ERROR    = "BO-4001";
    
    public String p_errcode;
    
    static {
        ht.put("BO-4000",MessageLocalizer.getMessage("INVALID_CREDENTIALS"));
    }
    public String getErrorCode() {
        return p_errcode;
    }
    public boLoginException(String errcode) {
        super(errorToMessage(errcode,null));
        p_errcode = errcode;
    }
    public boLoginException(String errcode,Throwable base) {
        super(errorToMessage(errcode,base));
        p_errcode = errcode;
    }
    public static final String errorToMessage(String code,Throwable base) {
        String description = MessageLocalizer.getMessage("DESCRIPTION_NOT_AVAILABLE");
        if(base!=null) {
            CharArrayWriter cr = new CharArrayWriter();
            PrintWriter pw = new PrintWriter(cr);
            base.printStackTrace(pw);
            pw.close();
            cr.close();
            description = cr.toString();
        }
        String ret = (String)ht.get(code);
        if(ret == null) {
            ret = code + " - " + description;
        } else {
            ret = code + " - " + ret;
        }
        return ret;
    }
}