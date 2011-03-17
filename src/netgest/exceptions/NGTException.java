/*Enconding=UTF-8*/
package netgest.exceptions;

import netgest.bo.localizations.MessageLocalizer;
import netgest.utils.*;

public class NGTException extends Exception
{
  private String p_errorCode = null;
  private String p_param1 = null;
  private String p_param2 = null;
  private Exception p_javaException = null;
  private String[] p_errdesc;
  public NGTException(String errorCode,String param1)
  {
       super(errorCode);
       this.p_param1 = param1;
       this.p_errorCode = errorCode;
  }
  public String getMessage() {
       String xret ="";
       int i=0; // Default messages for Errors
       p_errdesc = new String[23];

       // Errors handled by updates bean
       p_errdesc[i++] = "NGT-01003: "+MessageLocalizer.getMessage("THE_FIELD_IS_REPEATED");
   	   p_errdesc[i++] = "NGT-01001: "+MessageLocalizer.getMessage("NUMBER_WITH_INVALID_FORMAT_IN_FIELD");
   	   p_errdesc[i++] = "NGT-01002: "+MessageLocalizer.getMessage("INVALID_DATE_IN_FIELD");
   	   p_errdesc[i++] = "NGT-01004: "+MessageLocalizer.getMessage("COULD_NOT_INSERT_IN_A_CLOB");
   	   p_errdesc[i++] = "NGT-01005: "+MessageLocalizer.getMessage("COULD_NOT_FIND_MASTER_SECTION_IN_DOCUMENT");
   	   p_errdesc[i++] = "NGT-01006: "+MessageLocalizer.getMessage("THE_FIELD_OF_PRIMARY_KEY_HAS_TO_BE_FILLED");
   	   p_errdesc[i++] = "NGT-01007: "+MessageLocalizer.getMessage("THE_FIELD_OF_PRIMARY_KEY_HAS_TO_BE_PART_OF_VIRTUALTABLE");
   	   p_errdesc[i++] = "NGT-01008: "+MessageLocalizer.getMessage("THE_FIELDS_OF_THE_PRIMARY_KEY_HAVE_TO_BE_FILLED");
   	   p_errdesc[i++] = "NGT-01009: "+MessageLocalizer.getMessage("UNABLE_TO_SAVE_DAUGHTER_SECTIONS_OF_MASTER_ONCE_IT_");
   	   p_errdesc[i++] = "NGT-01010: "+MessageLocalizer.getMessage("DOES_NOT_HAVE_SUFFICIENT_PRIVILEGES_ON_TABLE");
   	   p_errdesc[i++] = "NGT-01011: "+MessageLocalizer.getMessage("THE_TABE_HAS_NO_DEFINED_PRIMARY_KEY");
   	   p_errdesc[i++] = "NGT-01012: "+MessageLocalizer.getMessage("THE_VIRTUAL_TABLE_DOES_NOT_EXIST");
   	   p_errdesc[i++] = "NGT-01013: "+MessageLocalizer.getMessage("REGISTRY_HAS_BEEN_CHANGED_BY_ANOTHER_USER");
   	   p_errdesc[i++] = "NGT-01014: "+MessageLocalizer.getMessage("CANNOT_SAVE_A_DOCUMENT_WITH_EMPTY_MASTER_SECTION");
   	   p_errdesc[i++] = "NGT-01015: "+MessageLocalizer.getMessage("THE_DOCUMENT_IS_EMPTY");
   	   p_errdesc[i++] = "NGT-01016: "+MessageLocalizer.getMessage("INVALID_MODE_FOR_SAVING");


       // Errors handled by SQL server
   	   p_errdesc[i++] = "NGT-01100: "+MessageLocalizer.getMessage("SERVER_TO_BUSY");
   	   p_errdesc[i++] = "NGT-01101: "+MessageLocalizer.getMessage("KEY_ALREADY_EXISTS");
   	   p_errdesc[i++] = "NGT-01102: "+MessageLocalizer.getMessage("EMPTY_OBLIGATORY_FIELD");
   	   p_errdesc[i++] = "NGT-01103: "+MessageLocalizer.getMessage("ERROR_SQL");
   	   p_errdesc[i++] = "NGT-01104: "+MessageLocalizer.getMessage("ERROR_XSU");

       // Server side Execution erros
   	   p_errdesc[i++] = "NGT-01200: "+MessageLocalizer.getMessage("THAT_EJB_DOES_NOT_EXIST");
   	   p_errdesc[i++] = "NGT-01201: "+MessageLocalizer.getMessage("THE_METHOD_FROM_THAT_EJB_DOES_NOT_EXIST");


       for (i=0;i<p_errdesc.length;i++) {
           if (p_errdesc[i].indexOf(this.p_errorCode)>-1) {
                xret = p_errdesc[i];
                break;
           }
       }
       if (i >= p_errdesc.length) xret = p_param1;
       if (xret.indexOf("?")>-1 && p_param1 != null) {
            xret = StringUtils.replacestr(xret,"?",p_param1);
       }
       return xret;
  }

}