/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.sql.SQLException;
import netgest.bo.data.*;
import netgest.bo.def.*;
import netgest.bo.localizations.MessageLocalizer;

import java.math.BigDecimal;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public  class ObjectMultiValues extends bridgeHandler
{
    /**
     * 
     * @since 
     */
    DataResultSet  p_data; 
    boDefAttribute p_defatt;
    public ObjectMultiValues( boObject parent, boDefAttribute att, DataResultSet data )
    {
        super( att.getName(), 
               data,
               att.getDbName(),
               parent,
               att.getReferencedObjectDef(),
               att,
               att.getDbName()
             );

        p_data   = data;
        p_defatt = att;
        super.refreshBridgeData();
        
    }

    public void add(BigDecimal boui) throws boRuntimeException
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_SUPPORTED_IN_MULTI_VALUE_ATTRIBUTES"));
    }

    public void add(BigDecimal boui, byte type) throws boRuntimeException
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_SUPPORTED_IN_MULTI_VALUE_ATTRIBUTES"));
    }

    public void add(BigDecimal boui, int row, byte type) throws boRuntimeException
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_SUPPORTED_IN_MULTI_VALUE_ATTRIBUTES"));
    }

    public void add(BigDecimal boui, int row) throws boRuntimeException
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_SUPPORTED_IN_MULTI_VALUE_ATTRIBUTES"));
    }
    
    public boolean remove() throws boRuntimeException
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_SUPPORTED_IN_MULTI_VALUE_ATTRIBUTES"));
        
    }

    public void setValue(BigDecimal xboui, byte type) throws boRuntimeException
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_SUPPORTED_IN_MULTI_VALUE_ATTRIBUTES"));
    }

    public void setValue(BigDecimal xboui) throws boRuntimeException
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_SUPPORTED_IN_MULTI_VALUE_ATTRIBUTES"));
    }

    //fcamara: dúvidas
    public BigDecimal getValue() throws boRuntimeException
    {
        try
        {
            return p_data.getBigDecimal( p_defatt.getDbName() );
        }
        catch (SQLException e)
        {
            throw new boRuntimeException2("ObjectMultiValues.getValue() "+ e.getMessage() );   
        }
    }
    
    //override
    public  boolean validate() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return true;
    }
    public  boolean required() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return false;
    }
    public  boolean disableWhen() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return false;
    }
    public  boolean hiddenWhen() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return false;
    }
    public  String defaultValue() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return null;
    }
    public  String[] condition() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return null;
    }
    public  boolean canChangeLov() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return false;
    }  
    
    public  String formula() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return null;
    }    

    public void addLine() throws boRuntimeException
    {
        // TODO:  Implement this netgest.bo.runtime.bridgeHandler abstract method
    }
    public boBridgeRow createRow()
    {
        return createRow( p_data.getDataSet().createRow() );
       
    }
    
//    public boBridgeRow createRow( DataRow row )
//    {
//        //return super.createRow( p_data.getDataSet().createRow() );
//        return null;
//       
//    }
//    public abstract boBridgeRow createRow( DataRow row );
    public boBridgeRow createRow( DataRow row )
    {
        
        return new ObjectMultiValuesRow( this, row );
//         return null;
    }
    public class ObjectMultiValuesRow extends boBridgeRow
    {
        public ObjectMultiValuesRow( bridgeHandler bridge, DataRow row )
        {
            super( bridge, row );
        }
        public void addLine()
        { 
            // TODO:  Override this netgest.bo.runtime.boBridgeRow method
        }

        public void refreshLineAttributes()
        {
            // TODO:  Override this netgest.bo.runtime.boBridgeRow method
        }
    }

    protected void refreshLineAttributes(int startIdx)
    {
        // TODO:  Override this netgest.bo.runtime.bridgeHandler method
    }
}