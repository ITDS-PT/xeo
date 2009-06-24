/*Enconding=UTF-8*/
package netgest.bo;
import java.util.*;
import netgest.utils.*;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.log4j.Logger;

public class boException extends RuntimeException {
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.boException");
    
    public static Hashtable p_errors;
    private String p_message;
    private Exception p_baseexception;
    private String p_code;
    public boException(String src,String code,Exception base) {
        init(src,code,base,(String[])null);
    }
    public boException(String src,String code,Exception base,String[] args) {
        init(src,code,base,args);
    }
    public boException(String src,String code,Exception base,String args) {
        String[] x= {args};
        init(src,code,base,x);
    }
    private void init(String src,String code,Exception base,String[] args) {
        if(p_errors == null ) initErrorCodes();
        p_code = code;
        p_baseexception = base;
        String xmsg = (String)p_errors.get(code);
        p_message = src+":"+code+":"+xmsg+(base!=null? "\nBase Error:"+base.getMessage():"");
        int i=0;
        if(args!=null) {
            for(i=0;i<args.length;i++) {
                p_message = StringUtils.replacestr(p_message,":"+(i+1),args[i]);
            }
        }
        p_message = StringUtils.replacestr(p_message,":"+(i+1),base!=null?base.getMessage():"");
    }
    private void initErrorCodes() {
       if(p_errors==null) {
            p_errors = new Hashtable();

            // Erros Mais genéricos
            p_errors.put("BO-1201","Erro a carregar o ficheiro :1 \n :baserror");
            p_errors.put("BO-1202","Erro a interpretar o ficheiro :1 \n :baserror");
            p_errors.put("BO-1203","Error reading/parsing file for [:1] \n :baserror");

            // Erros boBuildDB
            p_errors.put("BO-1301","Erro a adicionar no NGTDIC a tabela [:1] \n :baserror");
            p_errors.put("BO-1302","Erro a adicionar no NGTDIC o campo [:1] \n :baserror");
            p_errors.put("BO-1303","Erro a adicionar no NGTDIC o ForeignKey [:1] \n :baserror");
            p_errors.put("BO-1304","Erro a gerar Scripts para a o object [:1] \n :baserror");
            p_errors.put("BO-1305","Tipo de dados não suportado [:1] \n :baserror");
            p_errors.put("BO-1306","Erro de sintaxe no [:1] \n :baserror");
            p_errors.put("BO-1307","Erro a carregar o NGTDIC \n :baserror");

            // 
            p_errors.put("BO-1401","Current Attribute is not a object [:1]");
            p_errors.put("BO-1402","Only Orphan Objects are supported in bridge attributes [:1]");
            p_errors.put("BO-1403","Attribute without type defined [:1]");


            // Errors Class Generator and compiler
            p_errors.put("BO-1501","Error copying xml to deployment dir og business object [:1]");
            p_errors.put("BO-1502","Error copying template from (:1) to deployment dir (:2)");

            // Error of class compiler
            p_errors.put("BO-1601","Error compiling class generated for [:1]");

            // Erros of configuration
            p_errors.put("BO-1701","Error executing query of object State\nSource is [:1] in Object [:2] and the base error is [:3]");
            p_errors.put("BO-1702","Error executing BOQL to evalute a boolean source is [:1]");


            // Error Runtime Class Errors
            p_errors.put("BO-2101","Error Loading class for object [:1]");

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
}