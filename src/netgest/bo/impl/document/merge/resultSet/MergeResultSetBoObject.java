/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge.resultSet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;

import java.math.BigDecimal;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Date;
import netgest.bo.def.*;
import netgest.bo.impl.document.merge.resultSet.MergeMetaDataBoObject;
import netgest.bo.runtime.*;
import netgest.bo.impl.document.merge.*;
import netgest.io.*;
import netgest.utils.*;
import oracle.sql.*;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class MergeResultSetBoObject implements ResultSet
{
    /**
     * boObject.
     */
    private boObject data;

    /**
     * Linha actual.
     */
    private int line = -1;
    MergeMetaDataBoObject metaData;
    private boolean lastWasNull = false;
    private String prefix;
    private boDefAttribute[] attributes;
    private boBridgeIterator bridge;
    private String bridgeName;
    private ArrayList controller = new ArrayList();
    private boolean cutICP = false;
    private String treatingValue;

    /**
     * Constrói MergeResultSetBoObject.
     *
     * @param data Dados para elaboração do relatório
     */
    public MergeResultSetBoObject(boObject data)
    {
        this.data = data;
        refresh();
    }
    public MergeResultSetBoObject(boObject data, String bridgeName)
    {
        this.data = data;
        int l = 0;
        if( (l = repeteadBridge(bridgeName)) > 0)
        {
            this.bridgeName = bridgeName.substring(0, bridgeName.length() - (2 + l));
        }
        else
        {
            this.bridgeName = bridgeName;
        }
        refresh();
    }
    
    private static int repeteadBridge(String bridgeName)
    {
        if(bridgeName != null && bridgeName.length() > 0 && bridgeName.indexOf("__") > 0 && !bridgeName.endsWith("__"))
        {
            String number = ""; 
            for (int i = (bridgeName.length()-1); i >= 0 && bridgeName.charAt(i) != '_'; i--) 
            {
                if(bridgeName.charAt(i) >= '0' && bridgeName.charAt(i) <= '9')
                {
                    number =  bridgeName.charAt(i) + number;
                }
                else
                {
                    return -1;
                }
            }
            if(number.length() > 0)
            {
                return number.length();
            }
            
        }
        return -1;
    }
    
    public MergeResultSetBoObject(String prefix, boObject data)
    {
        this.data = data;
        this.prefix = prefix;
        refresh();
    }

    private void refresh()
    {
        if(metaData == null)
        {
            metaData = new MergeMetaDataBoObject(data, bridgeName);
        }
        if(bridgeName != null)
        {
            bridge = data.getBridge(bridgeName).iterator();
            bridge.beforeFirst();
        }
    }

    public ResultSetMetaData getMetaData()
    {        
        return metaData;
    }

    public boolean next()
    {
        if(bridge != null)
        {
            return bridge.next();
        }
        else if(line == -1)
        {
            line++;
            return true;
        }
        return false;
    }

    public Object getObject(int columnIndex) throws SQLException
    {
        try
        {
            if(controller.get(columnIndex - 1) == null)
            {
                return null;
            }
            String aux = (String)controller.get(columnIndex - 1);
            try
            {
                if(bridgeName == null)
                {
                    //martelada para substituir ICP por ANACOM
                    if(!cutICP)
                    {
                        return getValue(data, aux.split("\\."), 0);
                    }
                    else
                    {
                        String value = (String)getValue(data, aux.split("\\."), 0);
                        if(value != null && value.toUpperCase().startsWith("ICP-"))
                        {
                            return value.replaceAll("ICP", "ANACOM");
                        }
                        return value;
                    }
                }
                else
                    return getValue(bridge, aux.split("\\."), 0);
            }
            catch (boRuntimeException e)
            {
                return null;
            }
        }
        catch (Exception e)
        {
            // Coluna inexistente, devolve null
            throw new SQLException("Column [" + treatingValue + "] not found");
        }
    }

    public int findColumn(String columnName) throws SQLException
    {
        cutICP = false;
        treatingValue = columnName;
        if(prefix != null)
        {
            columnName = prefix + "." + columnName;
            treatingValue = columnName; 
        }
        columnName = columnName.replaceAll("__", ".");
        
        //martelada para substituir ICP por ANACOM
        if(columnName.toUpperCase().endsWith("RESID"))
        {
            cutICP = true;
        }
        String[] words = columnName.split("\\.");
        String aux;
        for (int i = 0; i < words.length; i++) 
        {
            words[i] = reservedWords(words[i]);            
        }
        columnName = join(words);
        if(!controller.contains(columnName))
        {
            controller.add(columnName);
        }
        metaData.setHeader(controller);
        int position = controller.indexOf(columnName);
        if(position == -1)
        {
            throw new SQLException("Column not found.");
        }
        return  position + 1;
    }
    
    private static String join(String[] words)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < words.length; i++) 
        {
            sb.append(words[i]);
            if((i + 1) < words.length)
            {
                sb.append(".");
            }
        }
        return sb.toString();
    }
    
    public static final Object getValue(boObject obj, String[] columnName, int apt) throws boRuntimeException
    {
        boDefAttribute att = obj.getAttribute(columnName[apt]).getDefAttribute();
        if(att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
        {
            boObject auxObj = obj.getAttribute(columnName[apt]).getObject();
            if(columnName.length == (apt + 1))
            {
                if(auxObj != null)
                {
                    return auxObj.getCARDIDwNoIMG() != null ? auxObj.getCARDIDwNoIMG().toString():null;
                }
                return null;
            }
            else
            {
                if(auxObj == null)
                {
                    return null;
                }
                return getValue(auxObj, columnName, (apt + 1));
            }
        }
        else
        {
            return getSimpleValue(obj.getAttribute(columnName[apt]));
        }
    }

    public static final Object getValue(boBridgeIterator it, String[] columnName, int apt) throws boRuntimeException
    {
        if(it.currentRow().getAttribute(columnName[apt]) != null)
        {
            boDefAttribute att = it.currentRow().getAttribute(columnName[apt]).getDefAttribute();
            if(att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
            {
                boObject auxObj = it.currentRow().getAttribute(columnName[apt]).getObject();
                if(columnName.length == (apt + 1))
                {
                    if(auxObj != null)
                    {
                        return auxObj.getCARDIDwNoIMG() != null ? auxObj.getCARDIDwNoIMG().toString():null;
                    }
                    return null;
                }
                else
                {
                    if(auxObj == null)
                    {
                        return null;
                    }
                    return getValue(auxObj, columnName, (apt + 1));
                }
            }
            else
            {
                return getSimpleValue(it.currentRow().getAttribute(columnName[apt]));
            }
        }
        else
        {
            return getValue(it.currentRow().getObject(), columnName, apt);
        }        
    }

    private static final Object getSimpleValue(AttributeHandler attr) throws boRuntimeException
    {
        if("boolean".equalsIgnoreCase(attr.getDefAttribute().getType()))
        {
            String value = attr.getValueString();
            if("0".equals(value))
            {
                //falta verificar a lingua
                return "Não";
            }
            else if("1".equals(value))
            {
                return "Sim";
            }
            return value;
       }             
       else if(attr.getDefAttribute().getLOVName() != null &&  
                !"".equals(attr.getDefAttribute().getLOVName()))
       {
            String xlov = attr.getDefAttribute().getLOVName(); 
            String value = attr.getValueString();
            if(value != null && !"".equals(value))
            {
                boObject lov;
                lov = attr.getParent().getBoManager().loadObject(attr.getParent().getEboContext(),"Ebo_LOV","name='"+xlov+"'");
                if(lov.exists())
                {
                    bridgeHandler lovdetails= lov.getBridge("details");
                    lovdetails.beforeFirst();
                    boObject det;
                    while(lovdetails.next())
                    {
                        det = lovdetails.getObject();
                        if(value.equalsIgnoreCase(det.getAttribute("value").getValueString()))
                        {
                            return det.getAttribute("description").getValueString();
                        }
                    }
                }
            }
            return attr.getValueString();
         }
         else if("date".equalsIgnoreCase(attr.getDefAttribute().getType()) ||
            "dateTime".equalsIgnoreCase(attr.getDefAttribute().getType()))
         {
            Date d = attr.getValueDate();
            if(d != null)
            {
                return new Timestamp(d.getTime());
            }
            return null;
         }         
         else if(attr.getDefAttribute().getType().toUpperCase().indexOf("NUMBER") != -1)
         {
            return new Double(attr.getValueDouble());
         }
         else if("clob".equalsIgnoreCase(attr.getDefAttribute().getType()))
         {
            if(attr.getValueString() == null) return null;
            try
            {
                return ClassUtils.htmlToText(attr.getValueString(), true);
            }
            catch (Exception e)
            {
                return attr.getValueString();
            }
         }
         else if("ifile".equalsIgnoreCase(attr.getDefAttribute().getType()))
         {
            try
            {
                iFile ifile = attr.getValueiFile();
                if(ifile != null)
                {
                    return getBytes(ifile.getInputStream());
                }
                else
                {
                    return null;
                }
            }
            catch (iFilePermissionDenied e)
            {
                throw new boRuntimeException("MergeResultSetBoObject", "getSimpleValue", e);
            }
            catch (Exception e)
            {
                throw new boRuntimeException("MergeResultSetBoObject", "getSimpleValue", e);
            }            
         }
         return attr.getValueString();
    }
    
    private static byte[] getBytes(InputStream in) throws Exception
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] page = new byte[4096];
			int read = in.read(page);
			while (read != -1)
			{
				out.write(page, 0, read);
				read = in.read(page);
			}
			return out.toByteArray();
		}
		finally
		{
			if(in!=null)
				in.close();
		}
	}
    
    private String reservedWords(String s)
    {
        return s;
    }

    public String getString(int index) throws SQLException
    {
        String o = (String) getObject(index);

        lastWasNull = false;
        return o;
    }

    public String getString(String columnName)
    {
        String o = getObject(columnName).toString();

        if (o == null)
        {
            lastWasNull = true;
        }
        else
        {
            lastWasNull = false;
        }

        return o;
    }

    public java.sql.Date getDate(int index) throws SQLException
    {
        Date d = (Date) getObject(index);

        if (d == null)
        {
            lastWasNull = true;

            return null;
        }
        else
        {
            lastWasNull = false;
        }

        return new java.sql.Date(d.getTime());
    }

    public java.sql.Date getDate(String columnName)
    {
        Date d = (Date) getObject(columnName);

        if (d == null)
        {
            lastWasNull = true;

            return null;
        }
        else
        {
            lastWasNull = false;
        }

        return new java.sql.Date(d.getTime());
    }

    public java.sql.Time getTime(int index) throws SQLException
    {
        Date d = (Date) getObject(index);

        if (d == null)
        {
            lastWasNull = true;

            return null;
        }
        else
        {
            lastWasNull = false;
        }

        return new Time(d.getTime());
    }

    public java.sql.Time getTime(String columnName)
    {
        Date d = (Date) getObject(columnName);

        if (d == null)
        {
            lastWasNull = true;

            return null;
        }
        else
        {
            lastWasNull = false;
        }

        return new Time(d.getTime());
    }

    public java.sql.Timestamp getTimestamp(int index) throws SQLException
    {
        Date d = (Date) getObject(index);

        if (d == null)
        {
            lastWasNull = true;

            return null;
        }
        else
        {
            lastWasNull = false;
        }

        return new Timestamp(d.getTime());
    }

    public java.sql.Timestamp getTimestamp(String columnName)
    {
        Date d = (Date) getObject(columnName);

        if (d == null)
        {
            lastWasNull = true;

            return null;
        }
        else
        {
            lastWasNull = false;
        }

        return new Timestamp(d.getTime());
    }

    public int getInt(int index) throws SQLException
    {
        int d = 0;
        Object auxOb = getObject(index);

        if (auxOb == null)
        {
            lastWasNull = true;

            return 0;
        }
        else
        {
            lastWasNull = false;
        }

        if (auxOb instanceof Integer)
        {
            d = ((Integer) getObject(index)).intValue();
        }
        else if (auxOb instanceof BigDecimal)
        {
            d = ((BigDecimal) auxOb).intValue();
        }

        return d;
    }

    public long getLong(int index) throws SQLException
    {
        long d = 0;
        Object auxOb = getObject(index);

        if (auxOb == null)
        {
            lastWasNull = true;

            return 0;
        }
        else
        {
            lastWasNull = false;
        }

        if (auxOb instanceof Long)
        {
            d = ((Long) getObject(index)).longValue();
        }
        else if (auxOb instanceof Integer)
        {
            d = ((Integer) getObject(index)).longValue();
        }
        else if (auxOb instanceof BigDecimal)
        {
            d = ((BigDecimal) auxOb).longValue();
        }

        return d;
    }

    public float getFloat(int index) throws SQLException
    {
        float d = 0;
        Object auxOb = getObject(index);

        if (auxOb == null)
        {
            lastWasNull = true;

            return 0;
        }
        else
        {
            lastWasNull = false;
        }

        if (auxOb instanceof Float)
        {
            d = ((Float) getObject(index)).floatValue();
        }
        else if (auxOb instanceof Long)
        {
            d = ((Long) getObject(index)).floatValue();
        }
        else if (auxOb instanceof Integer)
        {
            d = ((Integer) getObject(index)).floatValue();
        }
        else if (auxOb instanceof BigDecimal)
        {
            d = ((BigDecimal) auxOb).floatValue();
        }

        return d;
    }

    public double getDouble(int index) throws SQLException
    {
        Object auxOb = getObject(index);
        double d = 0;

        if (auxOb == null)
        {
            lastWasNull = true;

            return 0;
        }
        else
        {
            lastWasNull = false;
        }

        if (auxOb instanceof Double)
        {
            d = ((Double) auxOb).doubleValue();
        }
        else if (auxOb instanceof Float)
        {
            d = ((Float) getObject(index)).doubleValue();
        }
        else if (auxOb instanceof Long)
        {
            d = ((Long) getObject(index)).doubleValue();
        }
        else if (auxOb instanceof Integer)
        {
            d = ((Integer) getObject(index)).doubleValue();
        }
        else if (auxOb instanceof BigDecimal)
        {
            d = ((BigDecimal) auxOb).doubleValue();
        }

        return d;
    }

    public java.math.BigDecimal getBigDecimal(int index) throws SQLException
    {
        Object d = getObject(index);

        if (d == null)
        {
            lastWasNull = true;
        }
        else
        {
            lastWasNull = false;
        }

        return (BigDecimal) d;
    }

    public void close()
    {
        // Não faz nada
    }

    //======================================================================
    // User do not have to implement the following methods for they
    // are not used in the i-net Crystal Clear system.
    //======================================================================
    public Object getObject(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public double getDouble(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public float getFloat(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public long getLong(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public int getInt(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.io.Reader getCharacterStream(int columnIndex)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.io.Reader getCharacterStream(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.math.BigDecimal getBigDecimal(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.math.BigDecimal getBigDecimal(int columnIndex, int scale)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.math.BigDecimal getBigDecimal(String columnIndex, int scale)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean getBoolean(int index)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean getBoolean(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public byte getByte(int index)
    {
        throw new RuntimeException("Not Implemented");
    }

    public byte getByte(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public short getShort(int index)
    {
        throw new RuntimeException("Not Implemented");
    }

    public short getShort(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.io.InputStream getBinaryStream(int index)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.io.InputStream getBinaryStream(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.io.InputStream getUnicodeStream(int columnIndex)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.io.InputStream getUnicodeStream(String columnIndex)
    {
        throw new RuntimeException("Not Implemented");
    }

    public byte[] getBytes(int columnIndex) throws SQLException
    {
        byte[] o = (byte[]) getObject(columnIndex);

        if (o == null)
        {
            lastWasNull = true;
        }
        else
        {
            lastWasNull = false;
        }

        return o;
    }

    public byte[] getBytes(String columnName) throws SQLException
    {
        byte[] o = (byte[])getObject(columnName);

        if (o == null)
        {
            lastWasNull = true;
        }
        else
        {
            lastWasNull = false;
        }

        return o;
    }

    public java.io.InputStream getAsciiStream(int columnIndex)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.io.InputStream getAsciiStream(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean wasNull()
    {
        return lastWasNull;
    }

    public java.sql.SQLWarning getWarnings()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void clearWarnings()
    {
        throw new RuntimeException("Not Implemented");
    }

    public String getCursorName()
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isBeforeFirst()
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isAfterLast()
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isFirst()
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isLast()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void beforeFirst()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void afterLast()
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean first()
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean last()
    {
        throw new RuntimeException("Not Implemented");
    }

    public int getRow()
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean absolute(int row)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean relative(int rows)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean previous()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void setFetchDirection(int direction)
    {
        throw new RuntimeException("Not Implemented");
    }

    public int getFetchDirection()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void setFetchSize(int rows)
    {
        throw new RuntimeException("Not Implemented");
    }

    public int getFetchSize()
    {
        throw new RuntimeException("Not Implemented");
    }

    public int getType()
    {
        throw new RuntimeException("Not Implemented");
    }

    public int getConcurrency()
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean rowUpdated()
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean rowInserted()
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean rowDeleted()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateNull(int columnIndex)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateBoolean(int columnIndex, boolean x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateByte(int columnIndex, byte x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateShort(int columnIndex, short x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateInt(int columnIndex, int x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateLong(int columnIndex, long x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateFloat(int columnIndex, float x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateDouble(int columnIndex, double x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateBigDecimal(int columnIndex, java.math.BigDecimal x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateString(int columnIndex, String x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateBytes(int columnIndex, byte[] x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateDate(int columnIndex, java.sql.Date x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateTime(int columnIndex, java.sql.Time x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateTimestamp(int columnIndex, java.sql.Timestamp x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateAsciiStream(int columnIndex, java.io.InputStream x,
        int length)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateBinaryStream(int columnIndex, java.io.InputStream x,
        int length)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateCharacterStream(int columnIndex, java.io.Reader x,
        int length)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateObject(int columnIndex, Object x, int scale)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateObject(int columnIndex, Object x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateNull(String columnName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateBoolean(String columnName, boolean x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateByte(String columnName, byte x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateShort(String columnName, short x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateInt(String columnName, int x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateLong(String columnName, long x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateFloat(String columnName, float x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateDouble(String columnName, double x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateBigDecimal(String columnName, java.math.BigDecimal x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateString(String columnName, String x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateBytes(String columnName, byte[] x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateDate(String columnName, java.sql.Date x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateTime(String columnName, java.sql.Time x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateTimestamp(String columnName, java.sql.Timestamp x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateAsciiStream(String columnName, java.io.InputStream x,
        int length)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateBinaryStream(String columnName, java.io.InputStream x,
        int length)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateCharacterStream(String columnName, java.io.Reader reader,
        int length)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateObject(String columnName, Object x, int scale)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateObject(String columnName, Object x)
    {
        throw new RuntimeException("Not Implemented");
    }

    public void insertRow()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void updateRow()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void deleteRow()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void refreshRow()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void cancelRowUpdates()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void moveToInsertRow()
    {
        throw new RuntimeException("Not Implemented");
    }

    public void moveToCurrentRow()
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Statement getStatement()
    {
        throw new RuntimeException("Not Implemented");
    }

    public Object getObject(int i, java.util.Map map)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Ref getRef(int i)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Blob getBlob(int columnIndex) throws SQLException 
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Clob getClob(int i)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Array getArray(int i)
    {
        throw new RuntimeException("Not Implemented");
    }

    public Object getObject(String colName, java.util.Map map)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Ref getRef(String colName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Blob getBlob(String colName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Clob getClob(String colName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Array getArray(String colName)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Date getDate(int columnIndex, java.util.Calendar cal)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Time getTime(int col, java.util.Calendar c)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Time getTime(String col, java.util.Calendar c)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Date getDate(String col, java.util.Calendar c)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Timestamp getTimestamp(int col, java.util.Calendar c)
    {
        throw new RuntimeException("Not Implemented");
    }

    public java.sql.Timestamp getTimestamp(String col, java.util.Calendar c)
    {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.4
    public java.net.URL getURL(int p0) throws SQLException
    {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.4
    public java.net.URL getURL(String p0) throws SQLException
    {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.4
    public void updateRef(int p0, Ref p1) throws SQLException
    {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.4
    public void updateRef(String p0, Ref p1) throws SQLException
    {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.4
    public void updateBlob(int p0, Blob p1) throws SQLException
    {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.4
    public void updateBlob(String p0, Blob p1) throws SQLException
    {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.4
    public void updateClob(int p0, Clob p1) throws SQLException
    {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.4
    public void updateClob(String p0, Clob p1) throws SQLException
    {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.4
    public void updateArray(int p0, Array p1) throws SQLException
    {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.4
    public void updateArray(String p0, Array p1) throws SQLException
    {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateNClob(String p0, Reader p1) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateNClob(int p0, Reader p1) {
        throw new RuntimeException("Not Implemented");
    } 
    
    // Since JDK 1.6
    public void updateNClob(int p0, Reader p1, long p3) {
        throw new RuntimeException("Not Implemented");
    } 

    // Since JDK 1.6
    public void updateNClob(String p0, Reader p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }     

    // Since JDK 1.6
    public void updateClob(String p0, Reader p1) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void updateClob(int p0, Reader p1) {
        throw new RuntimeException("Not Implemented");
    }   

    // Since JDK 1.6
    public void updateClob(int p0, Reader p1, long p3) {
        throw new RuntimeException("Not Implemented");
    } 

    // Since JDK 1.6
    public void updateClob(String p0, Reader p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }  
    
    // Since JDK 1.6
    public void updateBlob(String p0, InputStream p1) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateBlob(int p0, InputStream p1) {
        throw new RuntimeException("Not Implemented");
    }  

    // Since JDK 1.6
    public void updateBlob(String p0, InputStream p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateBlob(int p0, InputStream p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }     

    // Since JDK 1.6
    public void updateCharacterStream(String p0, Reader p1) {
        throw new RuntimeException("Not Implemented");
    }    

    // Since JDK 1.6
    public void updateCharacterStream(int p0, Reader p1) {
        throw new RuntimeException("Not Implemented");
    }  

    // Since JDK 1.6
    public void updateCharacterStream(int p0, Reader p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }     

    // Since JDK 1.6
    public void updateCharacterStream(String p0, Reader p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }   
    
    // Since JDK 1.6
    public void updateBinaryStream(String p0, InputStream p1) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateBinaryStream(int p0, InputStream p1) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateBinaryStream(String p0, InputStream p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateBinaryStream(int p0, InputStream p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void updateAsciiStream(String p0, InputStream p1) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateAsciiStream(int p0, InputStream p1) {
        throw new RuntimeException("Not Implemented");
    }  

    // Since JDK 1.6
    public void updateAsciiStream(String p0, InputStream p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateAsciiStream(int p0, InputStream p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }  
    
    // Since JDK 1.6
    public void updateNCharacterStream(String p0, Reader p1) {
        throw new RuntimeException("Not Implemented");
    }     

    // Since JDK 1.6
    public void updateNCharacterStream(int p0, Reader p1) {
        throw new RuntimeException("Not Implemented");
    }     
    
    // Since JDK 1.6
    public void updateNCharacterStream(String p0, Reader p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }     

    // Since JDK 1.6
    public void updateNCharacterStream(int p0, Reader p1, long p3) {
        throw new RuntimeException("Not Implemented");
    }     

    // Since JDK 1.6
    public Reader getNCharacterStream(String p0) {
        throw new RuntimeException("Not Implemented");
    }       

    // Since JDK 1.6
    public Reader getNCharacterStream(int p0) {
        throw new RuntimeException("Not Implemented");
    }    
    
    // Since JDK 1.6
    public String getNString(String p0) {
        throw new RuntimeException("Not Implemented");
    }     
    
    // Since JDK 1.6
    public String getNString(int p0) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public int getHoldability() {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public NClob getNClob(int columnIndex) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public NClob getNClob(String columnLabel)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public RowId getRowId(int columnIndex)  {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public RowId getRowId(String columnLabel) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public SQLXML getSQLXML(int columnIndex) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public SQLXML getSQLXML(String columnLabel)  {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public boolean isClosed() throws SQLException {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void updateNClob(int columnIndex, NClob nClob)  {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateNClob(String columnLabel,
                            NClob nClob) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void updateNString(int columnIndex,
                              String nString) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateNString(String columnLabel,
                              String nString) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void updateRowId(int columnIndex, RowId x) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void updateRowId(String columnLabel, RowId x) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateSQLXML(int columnIndex,
                             SQLXML xmlObject)  {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void updateSQLXML(String columnLabel,
                             SQLXML xmlObject) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public boolean isWrapperFor(Class iface)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public Object unwrap(Class iface)  {
        throw new RuntimeException("Not Implemented");
    }     
}
