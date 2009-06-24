/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge.resultSet;

import java.sql.ResultSetMetaData;

import java.util.ArrayList;
import netgest.bo.impl.document.merge.*;

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
        throw new RuntimeException("Not Implemented");
    }

    public int getPrecision(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public int getColumnDisplaySize(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public int getScale(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public int isNullable(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public String getColumnLabel(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isSigned(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public String getSchemaName(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public String getTableName(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public String getCatalogName(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isReadOnly(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isWritable(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isDefinitelyWritable(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isAutoIncrement(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isCaseSensitive(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isSearchable(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isCurrency(int column)
    {
        throw new RuntimeException("Not Implemented");
    }

    public String getColumnClassName(int column)
    {
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
