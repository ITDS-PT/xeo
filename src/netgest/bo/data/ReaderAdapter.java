/*Enconding=UTF-8*/
package netgest.bo.data;
import java.util.ArrayList;
import java.util.List;

import netgest.bo.runtime.EboContext;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public interface ReaderAdapter 
{
     
     public ReaderAdapter executeQuery(EboContext ctx, String query , List arguments,int page,int pagesize );
     public DataSetMetaData getMetaData();
     public boolean fetchRow( DataRow row , int rows );
     public boolean fetchRow( DataRow dataRow );
     public boolean next();
     public boolean skip( int numberOfRows );
     public void close();
     
}