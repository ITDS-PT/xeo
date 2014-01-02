/*Enconding=UTF-8*/
package netgest.bo.data;
import java.util.Hashtable;

import netgest.bo.localizations.MessageLocalizer;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class DataException extends RuntimeException 
{
    /**
     * 
     * @since 
     */
     private String p_message;
     private String p_code;
    public DataException(String code, String[] argumensts)
    {
        super(code);
        p_message = argumensts[0];
        p_code    = code;
    }
    public DataException(String code, String argumensts)
    {
        super(code);
        p_message = argumensts;
        p_code    = code;
    }
    static 
    {
        Hashtable ht = new Hashtable();
        ht.put("5001",MessageLocalizer.getMessage("CANNOT_CONVERT_VALUE_FROM_TO"));
    }

    public String getLocalizedMessage()
    {
        return p_code + " - " + p_message;
    }

    public String getMessage()
    {
        return p_code + " - " + p_message;
    }
    
    
}