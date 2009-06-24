/*Enconding=UTF-8*/
package netgest.bo.data;

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
         "Data connot be updated because it is inconsistent with the database.",
         "Data was changed by another user.",
         "An error occurs updating data.",
         "PrimaryKey duplicated error.",
         "Referenced keys found to the object.",
         "UniqueKey duplicates error."
     };
     
     private Throwable p_cause;
     private byte      p_type;
     
    public WriterException(byte type, String message, Throwable exception )
    {
        super( descriptions[type] + "\n" + message, exception  );
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
}