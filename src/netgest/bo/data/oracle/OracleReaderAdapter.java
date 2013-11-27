/*Enconding=UTF-8*/
package netgest.bo.data.oracle;

import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import netgest.bo.data.DataClob;
import netgest.bo.data.DataException;
import netgest.bo.data.DataRow;
import netgest.bo.data.DataSetMetaData;
import netgest.bo.data.DataTypes;
import netgest.bo.data.ReaderAdapter;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.XEO;
import netgest.bo.system.locale.XEOLocaleProvider;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class OracleReaderAdapter implements ReaderAdapter
{
    /**
     *
     * @since
     */
    private ResultSet activeRslt;
    private PreparedStatement activePstm;
    private Connection activeCn;
    private ResultSetMetaData activeRsltMetaData;
    private PreparedStatement activeUpdatePstm;
    private PreparedStatement activeInsertPstm;
    private PreparedStatement activeDeletePstm;
    private Connection activeUpdateConnection;
    private String dmlDs;

    public OracleReaderAdapter(String dmlds)
    {
        dmlDs = dmlds;
    }

    public final ReaderAdapter executeQuery(EboContext ctx, String query,
        List arguments,int page, int pagesize)
    {
        _executeQuery(ctx, query, arguments,page,pagesize);

        return this;
    }

    public final ResultSet getResultSet()
    {
        return activeRslt;
    }

    public final DataSetMetaData getMetaData()
    {
        return createDataSetMetaData(activeRsltMetaData);
    }

    private void _executeQuery(EboContext ctx, String query, List arguments,int page,int pagesize)
    {
        try
        {
            if (dmlDs.toUpperCase().indexOf("DEF") > 0)
            {
                activeCn = ctx.getConnectionDef();
            }
            else
            {
                activeCn = ctx.getConnectionData();
            }

            activePstm = activeCn.prepareStatement(query);
            int toadd=1;
            if (pagesize==Integer.MAX_VALUE)
            	activePstm.setMaxRows(0);
            else
            	activePstm.setMaxRows(page*pagesize+toadd);
            if ((arguments != null) && (arguments.size() > 0))
            {
                for (short i = 0; i < arguments.size(); i++)
                {
                    Object xarg = arguments.get(i);

                    if (xarg != null)
                    {
                        activePstm.setObject(i + 1, xarg);
                    }
                    else
                    {
                        activePstm.setString(i + 1, null);
                    }
                }
            }

            activeRslt = activePstm.executeQuery();
            activeRsltMetaData = activeRslt.getMetaData();
        }
        catch (SQLException e)
        {
            try{if(activeRslt != null){activeRslt.close();}}catch(Exception _e){/*IGNORE*/}
            try{if(activePstm != null){activePstm.close();}}catch(Exception _e){/*IGNORE*/}
            throw new DataException("0000",MessageLocalizer.getMessage("ERROR_EXECUTING_SQL")+
                "["+query+"]\n" + e.getMessage());
        }
        finally
        {
        }
    }

    public boolean fetchRow(DataRow row, int rows)
    {
        boolean ret = false;

        try
        {
            ResultSetMetaData meta = activeRslt.getMetaData();

            int[] columnIdx = new int[ meta.getColumnCount() + 1 ];
            Arrays.fill(columnIdx, 0);

            for (int i = 0; ((rows == -1) || (i < rows)) && activeRslt.next();
                    i++)
            {
                ret = true;

                for (short z = 1; z <= meta.getColumnCount(); z++)
                {
                	if( columnIdx[z] == 0 ) {
                		columnIdx[z] = row.getDataSet().findColumn( meta.getColumnName( z ) );
                	}
                	if( columnIdx[z] > 0 ) {
                    int type = meta.getColumnType(z);

                    switch (type)
                    {
                    case Types.CHAR:
                    case Types.LONGVARCHAR:
                    case Types.VARCHAR:
                    case Types.DECIMAL:
                    case Types.BIGINT:
                    case Types.DOUBLE:
                    case Types.FLOAT:
                    case Types.INTEGER:
                    case Types.NUMERIC:
                    case Types.TINYINT:
	                        row.fetchColumn( columnIdx[z], activeRslt.getObject(z));

                        break;

                    case Types.TIMESTAMP:
                    case Types.DATE:
                    	try {
                    		Timestamp value = activeRslt.getTimestamp(z);
                    		row.fetchColumn( columnIdx[z], value);
                    	} catch (IllegalArgumentException e){
                    		//In some situations, which cannot be reproduced consistently
                    		//between different machines, getTimestamp without a Calendar
                    		//will fail at an internal JVM class (at sun.util.calendar.ZoneInfo.getOffset(ZoneInfo.java:358))
                    		//This catch is here to try and prevent that error, but the cause remains unknown
                    		//In the database, the data seems to be correct (example 20/11/2013) but when
                    		//getObject(int column) is used, the date is in 1913 (with negative number of miliseconds)
                    		//In upper layers, the number of miliseconds is used to build a new date object
                    		//and at that time, the date will be correct.
                    		GregorianCalendar m_cal = new GregorianCalendar(TimeZone.getDefault());
                    		Timestamp value = activeRslt.getTimestamp(z,m_cal);
                    		row.fetchColumn( columnIdx[z], value);
                    	}

                        break;

                    case Types.CLOB:

                        Clob srcclob =  activeRslt.getClob(z);

                        if (srcclob != null)
                        {
                            Reader reader = srcclob.getCharacterStream();
                            DataClob desclob = new DataClob(reader,
                                     8192);
                            srcclob = null;
	                            row.fetchColumn(columnIdx[z], desclob);
                        }
                        else
                        {
	                            row.fetchColumn(columnIdx[z], null);
                        }

                        break;

                    case -3:
                    case Types.BINARY:
	                        row.fetchColumn(columnIdx[z], activeRslt.getBytes(z));

                        break;

                    case Types.BLOB:
                        throw new DataException("0000",MessageLocalizer.getMessage("BLOB_NOT_SUPORTED"));

                    default:
                        throw new DataException("0000",MessageLocalizer.getMessage("TYPE")+
                            " [" + meta.getColumnType(z) +
                            "] "+MessageLocalizer.getMessage("SUPPORED_BY")+" [" + meta.getColumnClassName(z) +
                            "] "+MessageLocalizer.getMessage("NOT_SUPPORTED"));
                    }
                	}
                }
            }
        }
        catch (SQLException e)
        {
            throw new DataException("0000", e.getMessage());
        }

        return ret;
    }

    public final boolean fetchRow(DataRow dataRow)
    {
        return fetchRow(dataRow, 1);
    }

    public static final DataSetMetaData createDataSetMetaData(
        ResultSetMetaData rsltMetaData)
    {
        try
        {
            int count = rsltMetaData.getColumnCount();
            String[] columnName = new String[count];
            String[] columnClassName = new String[count];
            int[] columnDisplaySize = new int[count];
            int[] columnType = new int[count];
            String[] columnTypeName = new String[count];

            for (int i = 0; i < count; i++)
            {
                String className = rsltMetaData.getColumnClassName(i + 1);
                columnName[i] = rsltMetaData.getColumnName(i + 1);
                columnType[i] = rsltMetaData.getColumnType(i + 1);
                columnTypeName[i] = rsltMetaData.getColumnTypeName(i + 1);

                if (className.equals("java.lang.String"))
                {
                    columnClassName[i] = "java.lang.String";
                    columnDisplaySize[i] = rsltMetaData.getColumnDisplaySize(i +
                            1);
                    columnType[i] = DataTypes.VARCHAR;
                }
                else if (className.equals("java.math.BigDecimal"))
                {
                    columnClassName[i] = "java.math.BigDecimal";
                    columnType[i] = DataTypes.NUMERIC;
                }
                else if (className.equals("java.sql.Timestamp"))
                {
                    columnClassName[i] = "java.sql.Timestamp";
                    columnType[i] = DataTypes.TIMESTAMP;
                }
                else if (className.equals("oracle.sql.CLOB")
                		|| className.equals("oracle.jdbc.OracleClob")
                		|| className.equals("java.sql.Clob")
                		)
                {
                    columnClassName[i] = "netgest.bo.data.DataClob";
                    columnType[i] = DataTypes.CLOB;
                }
                else if (className.equals("oracle.sql.BLOB"))
                {
                    columnClassName[i] = "netgest.bo.data.DataBlob";
                    columnType[i] = DataTypes.BLOB;
                }
                else if (className.equals("byte[]"))
                {
                    columnClassName[i] = "byte[]";
                    columnType[i] = DataTypes.BINARY;
                }
                else
                {
                    throw new DataException("0000",MessageLocalizer.getMessage("BO_ORACLE_DRIVER_DOESNT_SUPPORT_THIS_DATA_TYPE")+
                        " [" +
                        className + "] ");
                }
            }

            return new DataSetMetaData(count, columnName, columnClassName,
                columnDisplaySize, columnType, columnTypeName);
        }
        catch (SQLException e)
        {
            throw new DataException("0000",MessageLocalizer.getMessage("ERROR_CREATING_RESULTSETMETADATA"));
        }
    }

    public void close()
    {
        if (activeRslt != null)
        {
            try
            {
                activeRslt.close();
            }
            catch (SQLException e)
            {
            }

            ;
        }

        if (activePstm != null)
        {
            try
            {
                activePstm.close();
            }
            catch (SQLException e)
            {
            }

            ;
        }

        activeRslt = null;
        activePstm = null;
        activeCn = null;
        activeRsltMetaData = null;
    }

    public boolean next()
    {
        try
        {
            return activeRslt.next();
        }
        catch (SQLException e)
        {
            throw new DataException("0000",MessageLocalizer.getMessage("ERROR_CALLING_NEXT") );
        }
    }

    public boolean skip(int rowsToSkip)
    {
        try
        {
            int i;

            for (i = 0; activeRslt.next() && (i < rowsToSkip); i++)
                ;

            return i == rowsToSkip;
        }
        catch (SQLException e)
        {
            throw new DataException("0000", MessageLocalizer.getMessage("ERROR_CALLING_NEXT"));
        }
    }
}
