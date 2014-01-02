/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge.resultSet;

import java.sql.ResultSetMetaData;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import netgest.bo.def.*;
import netgest.bo.runtime.*;
import netgest.bo.impl.document.merge.*;
import netgest.bo.localizations.MessageLocalizer;
/**
 * Esta classe implementa o ResultSet.
 * <P>
 * @author Francisco Câmara
 */
public class MergeMetaDataBoObject implements ResultSetMetaData
{
    private ArrayList header;
    private boObject data;
    private String bridgeName;
    Hashtable types = new Hashtable();

    public MergeMetaDataBoObject(boObject data, String bridgeName)
    {
        this.data = data;
        this.bridgeName = bridgeName;
    }

    public void setHeader(ArrayList header)
    {
        this.header = header;
    }
    public int getColumnCount()
    {
        return header.size();
    }

    public String getColumnName(int column)
    {
        return (String) header.get(column);
    }

    public int getColumnType(int column)
    {
        if(types.get(new Integer(column)) == null)
        {
            try
            {
                if(bridgeName != null && !"".equals(bridgeName))
                {
                    types.put(new Integer(column), getType(data, ((String)header.get(column - 1)).split("\\."), 0, bridgeName));
                }
                else
                {
                    types.put(new Integer(column), getType(data, ((String)header.get(column - 1)).split("\\."), 0));
                }
            }
            catch (boRuntimeException e)
            {
                return Types.VARCHAR;
            }   
        }
        return ((Integer) types.get(new Integer(column))).intValue();
    }

    private static final Integer getType(boObject obj, String[] columnName, int apt, String bridgeName) throws boRuntimeException
    {
         boDefBridge brAtt = obj.getBridge(bridgeName).getDefAttribute().getBridge();
         boDefAttribute att = brAtt.getAttributeRef(columnName[apt]);
         if(att != null)
         {
            if(columnName.length == (apt + 1))
            {
                return getSqlType(att);
            }
            return getType(att, columnName, apt + 1);
         }
         return getType(obj.getBridge(bridgeName).getDefAttribute(), columnName, apt);
    }
    private static final Integer getType(boObject obj, String[] columnName, int apt) throws boRuntimeException
    {
        if(obj.getAttribute(columnName[apt]) == null)
        {
            throw new boRuntimeException("MergeMetaDataBoObject.getType", MessageLocalizer.getMessage("INEXISTENT_COLUMN")+": " + columnName[apt], null);
        }
        boDefAttribute att = obj.getAttribute(columnName[apt]).getDefAttribute();
        if(columnName.length == (apt + 1))
        {
            return getSqlType(att);
        }
        return getType(att, columnName, apt + 1);
    }
    
    private static final Integer getType(boDefAttribute act, String[] columnName, int apt) throws boRuntimeException
    {
        boDefAttribute att = act.getReferencedObjectDef().getAttributeRef(columnName[apt]);
        if(att == null)
        {
            return new Integer(Types.VARCHAR);
        }
        if(att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
        {
            boDefHandler auxObj = att.getReferencedObjectDef();
            if(columnName.length == (apt + 1))
            {
                return getSqlType(att);
            }
            else
            {
                if(auxObj == null)
                {
                    return null;
                }
                return getType(att, columnName, (apt + 1));
            }
        }
        else
        {
            return getSqlType(att);
        }
    }

    private static Integer getSqlType(boDefAttribute att)
    {
        String t = att.getType().toUpperCase();

        if ((t.indexOf("CHAR") != -1) ||
                ((att.getLOVName() != null) &&
                !"".equals(att.getLOVName())))
        {
            return new Integer(Types.VARCHAR);
        }

        if (t.indexOf("NUMBER") != -1)
        {
            return new Integer(Types.NUMERIC);
        }

        if (t.indexOf("BOOLEAN") != -1)
        {
            return new Integer(Types.VARCHAR);
        }

        if (t.indexOf("OBJECT") != -1)
        {
            return new Integer(Types.VARCHAR);
        }

        if (t.indexOf("CLOB") != -1)
        {
            return new Integer(Types.VARCHAR);
        }

        if (t.indexOf("DATE") != -1)
        {
            return new Integer(Types.DATE);
        }
        
        if (t.indexOf("IFILE") != -1)
        {
            return new Integer(Types.BLOB);
        }

        return new Integer(Types.VARCHAR);
    }

    //======================================================================
    // User do not have to implement the following methods for they
    // are not used in the word writer system.
    //======================================================================
    public String getColumnTypeName(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public int getPrecision(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public int getColumnDisplaySize(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public int getScale(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public int isNullable(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public String getColumnLabel(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public boolean isSigned(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public String getSchemaName(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public String getTableName(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public String getCatalogName(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public boolean isReadOnly(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public boolean isWritable(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public boolean isDefinitelyWritable(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public boolean isAutoIncrement(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public boolean isCaseSensitive(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public boolean isSearchable(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public boolean isCurrency(int column)
    {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }

    public String getColumnClassName(int column)
    {
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
}
