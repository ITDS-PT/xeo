/*Enconding=UTF-8*/
package netgest.bo;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Hashtable;

import netgest.bo.localizations.MessageLocalizer;
import netgest.utils.StringUtils;

public class boException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    public static Hashtable p_errors;
    private String p_message;
    private Exception p_baseexception;
    private String p_code;
    public boException(String src,String code,Exception base) {
        init(src,code,base,(String[])null);
    }
    public boException(String src,String code,Exception base,String[] args) {
    	super( base );
        init(src,code,base,args);
    }
    public boException(String src,String code,Exception base,String args) {
    	super( base );
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
            p_errors.put("BO-1201",	MessageLocalizer.getMessage("ERROR_LOADING_FILE_BASEERROR"));
            p_errors.put("BO-1202",MessageLocalizer.getMessage("ERROR_INTEPRETING_FILE_BASEERROR"));
            p_errors.put("BO-1203",MessageLocalizer.getMessage("ERROR_READING_PARSING_FILE_FOR_BASEERROR"));

            // Erros boBuildDB
            p_errors.put("BO-1301",MessageLocalizer.getMessage("ERROR_ADDING_TO_NGTDIC_THE_TABLE_BASEERROR"));
            p_errors.put("BO-1302",MessageLocalizer.getMessage("ERROR_ADDING_TO_NGTDIC_THE_FIELD_BASEERROR"));
            p_errors.put("BO-1303",MessageLocalizer.getMessage("ERROR_ADDING_TO_NGTDIC_THE_FOREIGNKEY_BASEERROR"));
            p_errors.put("BO-1304",MessageLocalizer.getMessage("ERROR_GENERATING_SCRIPTS_FOR_THE_OBJECT_BASEERROR"));
            p_errors.put("BO-1305",MessageLocalizer.getMessage("UNSUPPORTED_DATA_TYPE_BASEERROR"));
            p_errors.put("BO-1306",MessageLocalizer.getMessage("SYNTAX_ERROR_IN_BASEERROR"));
            p_errors.put("BO-1307",MessageLocalizer.getMessage("ERROR_LOADING_THE_NGTDIC_BASEERROR"));

            // 
            p_errors.put("BO-1401",MessageLocalizer.getMessage("CURRENT_ATTRIBUTE_IS_NOT_A_OBJECT"));
            p_errors.put("BO-1402",MessageLocalizer.getMessage("ONLY_ORPHAN_OBJECTS_ARE_SUPPORTED_IN_BRIDGE_ATTRIBUTES"));
            p_errors.put("BO-1403",MessageLocalizer.getMessage("ATTRIBUTE_WITHOUT_TYPE_DEFINED"));


            // Errors Class Generator and compiler
            p_errors.put("BO-1501",MessageLocalizer.getMessage("ERROR_COPYING_XML_TO_DEPLOYMENT_DIR_OF_BUSINESS_OBJECT"));
            p_errors.put("BO-1502",MessageLocalizer.getMessage("ERROR_COPYING_TEMPLATES_FROM_TO_DEPLOYMENT_DIR"));

            // Error of class compiler
            p_errors.put("BO-1601",MessageLocalizer.getMessage("ERROR_COMPILING_CLASS_GENERATED_FOR"));

            // Erros of configuration
            p_errors.put("BO-1701",MessageLocalizer.getMessage("ERROR_EXECUTING_QUERY_OF_OBJECT_STATE_SOURCE_IS_IN_OBJ_BASEERROR"));
            p_errors.put("BO-1702",MessageLocalizer.getMessage("ERROR_EXECUTING_BOQL_TO_EVALUATE_A_BOOLEAN_SOURCE_IS"));


            // Error Runtime Class Errors
            p_errors.put("BO-2101",MessageLocalizer.getMessage("ERROR_LOADING_CLASS_FOR_OBJECT"));

        }    
    }

    public void printStackTrace() {
        // TODO:  Override this java.lang.Throwable method
        if(p_baseexception!=null) p_baseexception.printStackTrace();
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