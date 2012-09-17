/*Enconding=UTF-8*/
package netgest.bo.data;
import netgest.bo.runtime.EboContext;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public interface WriterAdapter 
{
    public void setParameters( String objectName , String schemaName, String[] icnFields, String[] internalAttributes , String[] externalAttributes , String[] parentFields, String childFields[] );
    public boolean insertRow( EboContext ctx, DataRow dataRow ) throws WriterException;
    public boolean updateRow( EboContext ctx, DataRow dataRow, boolean checkICN ) throws WriterException;
    public boolean deleteRow( EboContext ctx, DataRow dataRow ) throws WriterException;
    public void close();
    
        
}