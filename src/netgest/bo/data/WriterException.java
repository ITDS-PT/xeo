/*Enconding=UTF-8*/
package netgest.bo.data;

import java.util.LinkedList;
import java.util.List;

import netgest.bo.localizations.MessageLocalizer;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class WriterException extends Exception 
{
    /**
     * 
     * @since 
     */
     public static final byte INCONSISTENT_DATA        = 0;
     public static final byte CONCURRENCY_FAILED       = 1;
     public static final byte UNKNOWN_EXECEPTION       = 2;
     public static final byte DUPLICATED_KEYS          = 3;
     public static final byte REFERENCED_CONTRAINTS    = 4;
     public static final byte UNIQUE_KEY_VIOLATED      = 5;
     
     
     
     public static final String[] descriptions = 
     {
         MessageLocalizer.getMessage("DATA_CANNOT_BE_UPDATED_BECAUSE_IT_IS_INCONSISTENT_W_DB"),
         MessageLocalizer.getMessage("DATA_WAS_CHANGED_BY_ANOTHER_USER"),
         MessageLocalizer.getMessage("AN_ERROR_OCCURS_UPDATING_DATA"),
         MessageLocalizer.getMessage("PRIMARYKEY_DUPLICATED_ERROR"),
         MessageLocalizer.getMessage("REFERENCED_KEYS_FOUND_TO_THE_OBJECT"),
         MessageLocalizer.getMessage("UNIQUEKEY_DUPLICATES_ERROR")
     };
     
     private Throwable p_cause;
     private byte      p_type;
	private List<String> p_fields = new LinkedList<String>();
     
	
	public WriterException(byte type, String message, Throwable exception, List<String> field )
    {
        super( descriptions[type] + "\n" + message, exception  );
        p_type = type;
        p_fields = field;
    }
	
    public WriterException(byte type, String message, Throwable exception )
    {
        this( type, message, exception, new LinkedList<String>() );
        p_type = type;
    }
    public WriterException(byte type, String message )
    {
        super( descriptions[type] + "\n" + message );
        p_type = type;
    }

    
    public WriterException(byte type, Throwable exception )
    {
        super( descriptions[type], exception );
        p_type = type;
    }
    
    public WriterException(byte type)
    {
        super( descriptions[type] );
        p_type = type;
    }
    public byte getType()
    {
        return p_type;
    }
    
    public List<String> getFields(){
    	return p_fields;
    }
}