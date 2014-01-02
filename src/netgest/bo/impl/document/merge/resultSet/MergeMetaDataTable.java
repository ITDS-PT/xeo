/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge.resultSet;

import java.sql.ResultSetMetaData;

import java.util.ArrayList;
import netgest.bo.impl.document.merge.*;
import netgest.bo.localizations.MessageLocalizer;

/**
 * Esta classe implementa o ResultSet.
 * <P>
 * @author Francisco Câmara
 */
public class MergeMetaDataTable implements ResultSetMetaData
{
    private ArrayList header;
    private Tabela tabela;

    public MergeMetaDataTable(Tabela tabela)
    {
        this.header = tabela.getHeader();
        this.tabela = tabela;
    }

    public int getColumnCount()
    {
        return header.size();
    }

    public String getColumnName(int column)
    {
        return tabela.verifyValue((String) header.get(column));
    }

    public int getColumnType(int column)
    {
        return ((Integer) tabela.getSqlTypes().get(column - 1)).intValue();
    }

    //======================================================================
    // User do not have to implement the following methods for they
    // are not used in the i-net Crystal Clear system.
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
