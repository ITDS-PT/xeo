/*Enconding=UTF-8*/
package netgest.bo.ql;
import java.util.*;
import netgest.utils.*;
import java.io.PrintStream;
import java.io.PrintWriter;
import netgest.bo.*;
import netgest.bo.localizations.MessageLocalizer;

public class boqlParserException extends boException {
    
    public boqlParserException(String src,String code,Exception base) {
        super(src,code,base);
    }
    public boqlParserException(String src,String code,Exception base,String[] args) {
         super(src,code,base,args);
    }
    public boqlParserException(String src,String code,Exception base,String args) {
          super(src,code,base,args);
    }

    private void initErrorCodes() {
       if(p_errors==null) {
            p_errors = new Hashtable();

            // Erros Mais genéricos
            p_errors.put("BOQL",":: ");
            p_errors.put("BOQL-1000",MessageLocalizer.getMessage("ERROR_PARSING_THE_QUERY"));
            p_errors.put("BOQL-1001",MessageLocalizer.getMessage("ERROR_PARSING_THE_QUERY_ERROR_IN_EXPRESSION"));
            p_errors.put("BOQL-1002",MessageLocalizer.getMessage("ERROR_PARSING_THE_QUERY_MAX_NR_OF_CONNECTIONS"));

            // Erros boBuildDB
            p_errors.put("BO-1301",MessageLocalizer.getMessage("ERROR_ADDING_TO_NGTDIC_THE_TABLE_BASEERROR"));
            p_errors.put("BO-1302",MessageLocalizer.getMessage("ERROR_ADDING_TO_NGTDIC_THE_FIELD_BASEERROR"));
            p_errors.put("BO-1303",MessageLocalizer.getMessage("ERROR_ADDING_TO_NGTDIC_THE_FOREIGNKEY_BASEERROR"));
            p_errors.put("BO-1304",MessageLocalizer.getMessage("ERROR_GENERATING_SCRIPTS_FOR_THE_OBJECT_BASEERROR"));
            p_errors.put("BO-1305",MessageLocalizer.getMessage("UNSUPPORTED_DATA_TYPE_BASEERROR"));
            p_errors.put("BO-1306",MessageLocalizer.getMessage("SYNTAX_ERROR_IN_BASEERROR"));

            // 
            p_errors.put("BO-1401",MessageLocalizer.getMessage("CURRENT_ATTRIBUTE_IS_NOT_A_OBJECT"));
            p_errors.put("BO-1402",MessageLocalizer.getMessage("ONLY_ORPHAN_OBJECTS_ARE_SUPPORTED_IN_BRIDGE_ATTRIBUTES"));
            p_errors.put("BO-1403",MessageLocalizer.getMessage("ATTRIBUTE_WITHOUT_TYPE_DEFINED"));


            // Errors Class Generator and compiler
            p_errors.put("BO-1501",MessageLocalizer.getMessage("ERROR_COPYING_XML_TO_DEPLOYMENT_DIR_OF_BUSINESS_OBJECT"));

            // Error of class compiler
            p_errors.put("BO-1601",MessageLocalizer.getMessage("ERROR_COMPILING_CLASS_GENERATED_FOR"));

            // Error Runtime Class Errors
            p_errors.put("BO-2101",MessageLocalizer.getMessage("ERROR_LOADING_CLASS_FOR_OBJECT"));

        }    
    }

}