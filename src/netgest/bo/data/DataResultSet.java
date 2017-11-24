/*Enconding=UTF-8*/
package netgest.bo.data;
import java.io.*;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.InputStream;
import java.sql.SQLWarning;
import java.sql.ResultSetMetaData;
import java.io.Reader;
import java.sql.Statement;
import java.util.Map;
import java.sql.Ref;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Array;
import java.util.Calendar;
import java.net.URL;

import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLXML;

import netgest.bo.localizations.MessageLocalizer;
import netgest.utils.ExpressionParser;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class DataResultSet implements ResultSet, Serializable 
{
    /**
     * 
     * @since 
     */
     private DataSet p_dataSet;
     private int     p_row;
     private DataRow p_insertrow;
     
     
     
    public DataResultSet( DataSet dataSet )
    {
        p_dataSet = dataSet;
    }
    
    // Extensions of ResultSet
    public final void newData( DataSet data, int position )
    {
        this.p_dataSet = data;
        this.p_row     = position;
    }
    
    public final void moveRowTo( int position )
    {
        p_dataSet.moveRow( p_row, position );
    }
    public final int getRowCount()
    {
        return p_dataSet.getRowCount();
    }
    
    public final DataRow getCurrentRow( )
    {
        return p_dataSet.rows( p_row );
    }
    
    public String getParameter( String name )
    {
        return p_dataSet.getParameter( name );
    }
    public void setParameter(String name, String value)
    {
        p_dataSet.setParameter( name, value );
    }

    public boolean locatefor(String expression) throws SQLException {
        return locatefor(expression,0);
    }
    public boolean locatefor(String expression,int pos) throws SQLException {
        if(pos==0)
            this.beforeFirst();
        else 
            this.absolute(pos);
            
        ExpressionParser exp = new ExpressionParser();
        while(this.next()) {
            if(((Boolean)exp.parseExpression(this,expression)).booleanValue())
                return true;
        }
        return false;
    }
    public boolean locatefor(String[] fields,String[] values) throws SQLException {
        return locatefor( fields,values,0 );
    }
    public boolean locatefor(String[] fields,String[] values,int pos) throws SQLException {
        int i;
        boolean boltoken = false;

        int[] fldpos = new int[fields.length];
        // Cache de fields positions
        for (i=0;i<fields.length;i++) {
            fldpos[i] = this.findColumn(fields[i]);
            if(fldpos[i]<1)
                throw new SQLException(MessageLocalizer.getMessage("THE_FIELD")+" ["+fields[i]+"] "+MessageLocalizer.getMessage("DOESNT_EXIST"));
        }
        if( pos==0 )
        {
            this.beforeFirst();
        }
        else
        {
            this.absolute(pos);
        }
        while (this.next()) {
            boltoken = true;
            for (i=0;i<fields.length && boltoken;i++) {
                if(this.getString(fldpos[i])==null) {
                    if(values[i]==null) boltoken = true;
                    else boltoken=false;
                }
                else if (getMetaData().getColumnClassName(fldpos[i]).equals("java.lang.String") || getMetaData().getColumnTypeName(fldpos[i]).equals("CHAR")) {
                    if (!this.getString(fldpos[i]).equals(values[i])) {
                        boltoken = false;
                    }
                } else if (getMetaData().getColumnClassName(fldpos[i]).equals("java.math.BigDecimal")) {
                    if (this.getDouble(fldpos[i]) != Double.parseDouble(values[i])) {
                        boltoken = false;
                    }
                } else {
                    throw(new SQLException("Locate "+MessageLocalizer.getMessage("IS_LIMITED_TO_STRING_AND_NUMBERS_NO_OTHER_TYPES_CAN_BE_SEARCHED")));
                }
            }
            if (boltoken) break;
        }
        return boltoken;
    }
    
    public final DataSet getDataSet()
    {
        return p_dataSet;
    }
    
    // End of extensions
    
    public final DataRow row( )
    {
        if( p_insertrow != null ) return p_insertrow;
        else return p_dataSet.rows( p_row );
    }
    

    public final  boolean next() throws SQLException
    {
        if( p_row < p_dataSet.getRowCount() )
        {
            p_row++;
            return true;
        }
        else 
        {   
            if ( p_row == p_dataSet.getRowCount() &&  p_dataSet.getRowCount() > 0 )
            {
                p_row++;
            }
            return false;
        }
    }

    public final void close() throws SQLException
    {
    }

    public final boolean wasNull() throws SQLException
    {
        return row().wasNull();
    }

    public final String getString(int columnIndex) throws SQLException
    {
        return row().getString( columnIndex );
    }

    public final boolean getBoolean(int columnIndex) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public final byte getByte(int columnIndex) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public final short getShort(int columnIndex) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public final int getInt(int columnIndex) throws SQLException
    {
        return row().getInt( columnIndex );
    }

    public final long getLong(int columnIndex) throws SQLException
    {
        return row().getLong( columnIndex );
    }

    public final float getFloat(int columnIndex) throws SQLException
    {
        return row().getFloat( columnIndex );
    }

    public final double getDouble(int columnIndex) throws SQLException
    {
        return row().getDouble( columnIndex );
    }

    public final BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException
    {
        return row().getBigDecimal( columnIndex );
    }

    public final byte[] getBytes(int columnIndex) throws SQLException
    {
        throw new SQLException("NGTBO:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Date getDate(int columnIndex) throws SQLException
    {
        Timestamp ret = row().getTimestamp( columnIndex );
        if( ret != null )
        {
            return new Date( ret.getTime() );
        }
        return null;
    }

    public final Time getTime(int columnIndex) throws SQLException
    {
        Timestamp ret = row().getTimestamp( columnIndex );
        if( ret != null )
        {
            return new Time( ret.getTime() );
        }
        return null;
    }

    public final Timestamp getTimestamp(int columnIndex) throws SQLException
    {
        return row().getTimestamp( columnIndex );
    }

    public final InputStream getAsciiStream(int columnIndex) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final InputStream getUnicodeStream(int columnIndex) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final InputStream getBinaryStream(int columnIndex) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final String getString(String columnName) throws SQLException
    {
        return row().getString( columnName );
    }

    public final boolean getBoolean(String columnName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final byte getByte(String columnName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final short getShort(String columnName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+".. ");
    }

    public final int getInt(String columnName) throws SQLException
    {
        return row().getInt( columnName );
    }

    public final long getLong(String columnName) throws SQLException
    {
        return row().getLong( columnName );
    }

    public final float getFloat(String columnName) throws SQLException
    {
        return row().getFloat( columnName );
    }

    public final double getDouble(String columnName) throws SQLException
    {
        return row().getDouble( columnName );
    }

    public final BigDecimal getBigDecimal(String columnName, int scale) throws SQLException
    {
        return row().getBigDecimal( columnName );
    }

    public final byte[] getBytes(String columnName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Date getDate(String columnName) throws SQLException
    {
        Timestamp ret = row().getTimestamp( columnName );
        if( ret != null )
        {
            return new Date( ret.getTime() );
        }
        return null;
    }

    public final Time getTime(String columnName) throws SQLException
    {
        Timestamp ret = row().getTimestamp( columnName );
        if( ret != null )
        {
            return new Time( ret.getTime() );
        }
        return null;
    }

    public final Timestamp getTimestamp(String columnName) throws SQLException
    {
        return row().getTimestamp( columnName );
    }

    public final InputStream getAsciiStream(String columnName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final InputStream getUnicodeStream(String columnName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final InputStream getBinaryStream(String columnName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final SQLWarning getWarnings() throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void clearWarnings() throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final String getCursorName() throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final ResultSetMetaData getMetaData() throws SQLException
    {
        return p_dataSet.getMetaData();
    }

    public final Object getObject(int columnIndex) throws SQLException
    {
        return row().getObject( columnIndex );
    }

    public final Object getObject(String columnName) throws SQLException
    {
        return row().getObject( columnName );
    }

    public final int findColumn(String columnName) throws SQLException
    {
        return p_dataSet.findColumn( columnName );
    }

    public final Reader getCharacterStream(int columnIndex) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Reader getCharacterStream(String columnName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final BigDecimal getBigDecimal(int columnIndex) throws SQLException
    {
        return row().getBigDecimal( columnIndex );
    }

    public final BigDecimal getBigDecimal(String columnName) throws SQLException
    {
        return row().getBigDecimal( columnName );
    }

    public final boolean isBeforeFirst() throws SQLException
    {
        if( p_row == 0 ) return true;
        else return false;
    }

    public final boolean isAfterLast() throws SQLException
    {
        if( p_row > p_dataSet.getRowCount() || p_dataSet.getRowCount() == 0 ) return true;
        else return false;
    }

    public final boolean isFirst() throws SQLException
    {
        return p_row == 1;
    }

    public final boolean isLast() throws SQLException
    {
        return p_row == p_dataSet.getRowCount();
    }

    public final void beforeFirst() throws SQLException
    {
        p_row = 0;
    }

    public final void afterLast() throws SQLException
    {
        p_row = p_dataSet.getRowCount() > 0?p_dataSet.getRowCount()+1:0;
    }

    public final boolean first() throws SQLException
    {
        p_row = p_dataSet.getRowCount() > 0?1:0;  
        return p_dataSet.getRowCount() > 0;
    }

    public final boolean last() throws SQLException
    {
        p_row = p_dataSet.getRowCount() > 0?p_dataSet.getRowCount():0;  
        return p_dataSet.getRowCount() > 0;
    }

    public final int getRow() throws SQLException
    {
        return p_row;
    }

    public final boolean absolute(int row) throws SQLException
    {
        boolean ret = false;
        if( row >= 0 && row <= p_dataSet.getRowCount() ) 
        {
            p_row = row;
            ret = true;
        }
        else if ( row > p_dataSet.getRowCount() )
        {
            p_row = p_dataSet.getRowCount() + 1;
            ret = false;
        }
        else if ( row < 1 )
        {
            p_row = 0;
            ret = false;
        }
        return ret;
    }

    public final boolean relative(int rows) throws SQLException
    {
        boolean ret = false;
        if( p_row+rows > 0 && p_row+rows <= p_dataSet.getRowCount() ) 
        {
            p_row = p_row+rows;
            ret = true;
        }
        return ret;
    }

    public final boolean previous() throws SQLException
    {
        if( p_row > 1 )
        {
            p_row--;
            return true;
        }
        else 
        {
            if( p_row > 0 )
            {
                p_row--;
            }
            return false;
        }
    }

    public final void setFetchDirection(int direction) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final int getFetchDirection() throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void setFetchSize(int rows) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final int getFetchSize() throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final int getType() throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final int getConcurrency() throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final boolean rowUpdated() throws SQLException
    {
        return row().wasChanged();
    }

    public final boolean rowInserted() throws SQLException
    {
        return row().isNew();
    }

    public final boolean rowDeleted() throws SQLException
    {
        return row().wasDeleted();
    }

    public final void updateNull(int columnIndex) throws SQLException
    {
        row().updateObject( columnIndex, null );
    }

    public final void updateBoolean(int columnIndex, boolean x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateByte(int columnIndex, byte x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateShort(int columnIndex, short x) throws SQLException
    {
        row().updateInt( columnIndex, x );
    }

    public final void updateInt(int columnIndex, int x) throws SQLException
    {
        row().updateInt( columnIndex, x );
    }

    public final void updateLong(int columnIndex, long x) throws SQLException
    {
        row().updateLong( columnIndex, x );
    }

    public final void updateFloat(int columnIndex, float x) throws SQLException
    {
        row().updateFloat( columnIndex, x );
    }

    public final void updateDouble(int columnIndex, double x) throws SQLException
    {
        row().updateDouble( columnIndex, x );
    }

    public final void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
    {
        row().updateBigDecimal( columnIndex, x );
    }

    public final void updateString(int columnIndex, String x) throws SQLException
    {
        row().updateString( columnIndex, x );
    }

    public final void updateBytes(int columnIndex, byte[] x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateDate(int columnIndex, Date x) throws SQLException
    {
        row().updateTimestamp( columnIndex, x!=null?new Timestamp(x.getTime()):null);
    }

    public final void updateTime(int columnIndex, Time x) throws SQLException
    {
        row().updateTimestamp( columnIndex, x!=null?new Timestamp(x.getTime()):null);
    }

    public final void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
    {
        row().updateTimestamp( columnIndex, x );
    }

    public final void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateObject(int columnIndex, Object x, int scale) throws SQLException
    {
        row().updateObject( columnIndex, x );
    }

    public final void updateObject(int columnIndex, Object x) throws SQLException
    {
        row().updateObject( columnIndex, x );
    }

    public final void updateNull(String columnName) throws SQLException
    {
        row().updateObject( columnName, null );
    }

    public final void updateBoolean(String columnName, boolean x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateByte(String columnName, byte x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateShort(String columnName, short x) throws SQLException
    {
        row().updateInt( columnName, x );
    }

    public final void updateInt(String columnName, int x) throws SQLException
    {
        row().updateInt( columnName, x );
    }

    public final void updateLong(String columnName, long x) throws SQLException
    {
        row().updateLong( columnName, x );
    }

    public final void updateFloat(String columnName, float x) throws SQLException
    {
        row().updateFloat( columnName, x );
    }

    public final void updateDouble(String columnName, double x) throws SQLException
    {
        row().updateDouble( columnName, x );
    }

    public final void updateBigDecimal(String columnName, BigDecimal x) throws SQLException
    {
        row().updateBigDecimal( columnName, x );
    }

    public final void updateString(String columnName, String x) throws SQLException
    {
        row().updateString( columnName, x );
    }

    public final void updateBytes(String columnName, byte[] x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateDate(String columnName, Date x) throws SQLException
    {
        row().updateTimestamp( columnName, x!=null?new Timestamp(x.getTime()):null);
    }

    public final void updateTime(String columnName, Time x) throws SQLException
    {
        row().updateTimestamp( columnName, x!=null?new Timestamp(x.getTime()):null);
    }

    public final void updateTimestamp(String columnName, Timestamp x) throws SQLException
    {
        row().updateTimestamp( columnName, x );
    }

    public final void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateObject(String columnName, Object x, int scale) throws SQLException
    {
        row().updateObject( columnName, x );
    }

    public final void updateObject(String columnName, Object x) throws SQLException
    {
        row().updateObject( columnName, x );
    }

    public final void insertRow() throws SQLException
    {
        if( p_insertrow != null ) 
        {
            p_dataSet.insertRow( p_insertrow );
            p_insertrow = null;
            p_row = p_dataSet.getRowCount();
        }
        
    }

    public final void updateRow() throws SQLException
    {
    }

    public final void deleteRow() throws SQLException
    {
        p_dataSet.deleteRow( p_row );
        p_row--;
    }

    public final void refreshRow() throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void cancelRowUpdates() throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void moveToInsertRow() throws SQLException
    {
        p_insertrow = p_dataSet.createRow();
    }

    public final void moveToCurrentRow() throws SQLException
    {
        p_insertrow = null;
    }

    public final Statement getStatement() throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Object getObject(int i, Map map) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Ref getRef(int i) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Blob getBlob(int i) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Clob getClob(int i) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Array getArray(int i) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Object getObject(String colName, Map map) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+".. ");
    }

    public final Ref getRef(String colName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Blob getBlob(String colName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Clob getClob(String colName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Array getArray(String colName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Date getDate(int columnIndex, Calendar cal) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Date getDate(String columnName, Calendar cal) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Time getTime(int columnIndex, Calendar cal) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Time getTime(String columnName, Calendar cal) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final URL getURL(int columnIndex) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final URL getURL(String columnName) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateRef(int columnIndex, Ref x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateRef(String columnName, Ref x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateBlob(int columnIndex, Blob x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateBlob(String columnName, Blob x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateClob(int columnIndex, Clob x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateClob(String columnName, Clob x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateArray(int columnIndex, Array x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    public final void updateArray(String columnName, Array x) throws SQLException
    {
        throw new SQLException("NGTBO: "+MessageLocalizer.getMessage("NOT_IMPLEMENTED")+"... ");
    }

    // Since JDK 1.6
    public void updateNClob(String p0, Reader p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateNClob(int p0, Reader p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    } 
    
    // Since JDK 1.6
    public void updateNClob(int p0, Reader p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    } 

    // Since JDK 1.6
    public void updateNClob(String p0, Reader p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }     

    // Since JDK 1.6
    public void updateClob(String p0, Reader p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public void updateClob(int p0, Reader p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }   

    // Since JDK 1.6
    public void updateClob(int p0, Reader p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    } 

    // Since JDK 1.6
    public void updateClob(String p0, Reader p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }  
    
    // Since JDK 1.6
    public void updateBlob(String p0, InputStream p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateBlob(int p0, InputStream p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }  

    // Since JDK 1.6
    public void updateBlob(String p0, InputStream p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateBlob(int p0, InputStream p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }     

    // Since JDK 1.6
    public void updateCharacterStream(String p0, Reader p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }    

    // Since JDK 1.6
    public void updateCharacterStream(int p0, Reader p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }  

    // Since JDK 1.6
    public void updateCharacterStream(int p0, Reader p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }     

    // Since JDK 1.6
    public void updateCharacterStream(String p0, Reader p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }   
    
    // Since JDK 1.6
    public void updateBinaryStream(String p0, InputStream p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateBinaryStream(int p0, InputStream p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateBinaryStream(String p0, InputStream p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateBinaryStream(int p0, InputStream p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public void updateAsciiStream(String p0, InputStream p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateAsciiStream(int p0, InputStream p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }  

    // Since JDK 1.6
    public void updateAsciiStream(String p0, InputStream p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateAsciiStream(int p0, InputStream p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }  
    
    // Since JDK 1.6
    public void updateNCharacterStream(String p0, Reader p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }     

    // Since JDK 1.6
    public void updateNCharacterStream(int p0, Reader p1) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }     
    
    // Since JDK 1.6
    public void updateNCharacterStream(String p0, Reader p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }     

    // Since JDK 1.6
    public void updateNCharacterStream(int p0, Reader p1, long p3) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }     

    // Since JDK 1.6
    public Reader getNCharacterStream(String p0) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }       

    // Since JDK 1.6
    public Reader getNCharacterStream(int p0) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }    
    
    // Since JDK 1.6
    public String getNString(String p0) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }     
    
    // Since JDK 1.6
    public String getNString(int p0) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public int getHoldability() {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public NClob getNClob(int columnIndex) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public NClob getNClob(String columnLabel)  {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public RowId getRowId(int columnIndex)  {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public RowId getRowId(String columnLabel) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public SQLXML getSQLXML(int columnIndex) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public SQLXML getSQLXML(String columnLabel)  {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public boolean isClosed() throws SQLException {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public void updateNClob(int columnIndex, NClob nClob)  {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateNClob(String columnLabel,
                            NClob nClob) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public void updateNString(int columnIndex,
                              String nString) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateNString(String columnLabel,
                              String nString) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public void updateRowId(int columnIndex, RowId x) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public void updateRowId(String columnLabel, RowId x) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateSQLXML(int columnIndex,
                             SQLXML xmlObject)  {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    // Since JDK 1.6
    public void updateSQLXML(String columnLabel,
                             SQLXML xmlObject) {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public boolean isWrapperFor(Class iface)  {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public Object unwrap(Class iface)  {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
	}    
}