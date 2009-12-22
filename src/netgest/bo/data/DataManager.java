/*Enconding=UTF-8*/
package netgest.bo.data;

import netgest.bo.runtime.EboContext;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import netgest.bo.system.Logger;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class DataManager
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.data.DataManager");

    /**
     *
     * @since
     */
    public static final DataSet executeNativeQuery(EboContext ctx,
        String dataSource, String query, ArrayList arguments)
    {
        return execute(ctx, null, dataSource, query, 1, Short.MAX_VALUE,
            arguments);
    }

    public static final DataSet executeNativeQuery(EboContext ctx,
        String dataSource, String query, int page, int pageSize,
        ArrayList arguments)
    {
        return execute(ctx, null, dataSource, query, page, pageSize, arguments);
    }

    public static final DataSet fecthMoreData(EboContext ctx,
        DataSet dataSetToAppend, String dataSource, String query, int page,
        int pageSize, ArrayList arguments)
    {
        return execute(ctx, dataSetToAppend, dataSource, query, page, pageSize,
            arguments);
    }

    private static final DataSet execute(EboContext ctx, DataSet dataSet,
        String dataSource, String query, int page, int pageSize,
        ArrayList arguments)
    {
        ReaderAdapter rd = prepareForQuery(ctx, dataSource, query, arguments);

        if (dataSet == null)
        {
            DataSetMetaData meta = rd.getMetaData();
            dataSet = new DataSet(meta);
        }

        fetchData(dataSet, new ReaderAdapter[] { rd }, page, pageSize);
        rd.close();

        return dataSet;
    }

    public static final DataSet executeNativeQuery(EboContext ctx,
        String[] dataSource, String[] query, int page, int pageSize,
        List[] arguments)
    {
        return executeNativeQuery(ctx, null, dataSource, query, page, pageSize,
            arguments);
    }

    public static final DataSet executeNativeQuery(EboContext ctx,
        DataSet dataSetToAppend, String[] dataSource, String[] query, int page,
        int pageSize, List[] arguments)
    {
        ReaderAdapter[] rd = new ReaderAdapter[dataSource.length];

        for (byte i = 0; i < dataSource.length; i++)
        {
            rd[i] = prepareForQuery(ctx, dataSource[i], query[i],
                    (arguments == null) ? null : arguments[i]);
        }

        DataSet dataSet;

        if (dataSetToAppend == null)
        {
            // Merge MetaData
            //...
            //
            DataSetMetaData meta = rd[0].getMetaData();
            dataSet = new DataSet(meta);
        }
        else
        {
            dataSet = dataSetToAppend;
        }

        fetchData(dataSet, rd, page, pageSize);

        for (byte i = 0; i < dataSource.length; i++)
        {
            rd[i].close();
        }

        return dataSet;
    }

    private static final ReaderAdapter prepareForQuery(EboContext ctx,
        String dataSource, String query, List arguments)
    {
        ReaderAdapter dm = ctx.getApplication().getDriverManager()
                              .createReaderAdapter(ctx, dataSource);
        dm.executeQuery(ctx, query, arguments);

        return dm;
    }

    private static final void fetchData(DataSet dataSet,
        ReaderAdapter[] adapters, int page, int pageSize)
    {
        boolean haveResults = true;
        int skipRows = (page - 1) * pageSize;

        for (int i = 0; i < skipRows; i++)
        {
            haveResults = adapters[0].next();
        }

        int fetched = 0;

        while (haveResults && (fetched < pageSize))
        {
            DataRow row = dataSet.createRowToFetch();
            haveResults = false;

            for (byte i = 0; i < adapters.length; i++)
            {
                if (adapters[i].fetchRow(row))
                {
                    haveResults = true;
                }
            }

            if (haveResults)
            {
                dataSet.insertRow(row);
            }

            fetched++;
        }
        if( haveResults ) {
        	dataSet.setParameter("HaveMoreData", Boolean.toString( haveResults ) );
        }
    }

    public static final int updateDataSet(EboContext ctx, DataSet dataSet,
        boolean forDelete) throws WriterException
    {
        return updateDataSet(ctx, dataSet, forDelete, 0);
    }

    private static int updateDataSet(EboContext ctx, DataSet dataSet,
        boolean forDelete, int rows) throws WriterException
    {
        DataSetWriteRelation[] relations = (DataSetWriteRelation[]) dataSet.p_relation.getWriteRelations();
        if( relations != null )
        {
            WriterAdapter[] wa = null;
    
            try
            {
                wa = new WriterAdapter[relations.length];
    
                for (byte i = 0; i < relations.length; i++)
                {
                    wa[i] = ctx.getApplication().getDriverManager()
                               .createWriterAdapter(ctx,
                            relations[i].getDataSource());
                    wa[i].setParameters(relations[i].getObjectName(),
                        relations[i].getSchemaName(), relations[i].getICNFields(),
                        relations[i].getLocalColumns(),
                        relations[i].getRemoteColumns(),
                        relations[i].getParentFields(),
                        relations[i].getChildFields());
                }
    
                byte phase = 0;
    
                for (int i = 0;; i++)
                {
                    if ((phase == 0) && (i >= dataSet.getDeletedRowsCount()))
                    {
                        i = 0;
                        phase = 1;
                    }
    
                    if ((!forDelete && (phase == 1)) ||
                            ((phase == 1) && (i >= dataSet.getRowCount())))
                    {
                        break;
                    }
    
                    DataRow row = (phase == 0) ? dataSet.deletedRows(i + 1)
                                               : dataSet.rows(i + 1);
    
                    if (!row.isNew())
                    {
                        if (row.getDataSet().p_childRelations != null)
                        {
                            Enumeration childNodes = row.getDataSet().p_childRelations.keys();
    
                            while (childNodes.hasMoreElements())
                            {
                                String nodename = (String) childNodes.nextElement();
                                DataSet child = row.getChildDataSet( ctx, nodename);
                                if ((child.getDeletedRowsCount() > 0) ||
                                        (child.getRowCount() > 0))
                                {
                                    updateDataSet(ctx, child, true, rows);
                                }
                            }
                        }
    
                        for (byte z = 0; z < wa.length; z++)
                        {
                            rows++;
//                            logger.finest("DELETING ROW IN [" +
//                                relations[z].getObjectName() + "]");
    
                            boolean ok = true;
    
                            if (row.wasChanged())
                            {
                                ok = wa[z].deleteRow(ctx,
                                        (row.getFlashBackRow() == null) ? row
                                                                        : row.getFlashBackRow());
                            }
                            else
                            {
                                ok = wa[z].deleteRow(ctx, row);
                            }
    
                            if (!ok)
                            {
                                throw new DataException("0000",
                                    "Invalid state of the data, Expeceted row doesn't exist.");
                            }
                        }
                    }
                }
    
                if (!forDelete)
                {
                    for (int i = 0; i < dataSet.getRowCount(); i++)
                    {
                        DataRow row = dataSet.rows(i + 1);
    
                        if (row.isNew() || row.wasChanged())
                        {
                            fillSystemFields(ctx, row);
    
                            for (byte z = 0; z < wa.length; z++)
                            {
                                if (row.isNew())
                                {
                                    rows++;
//                                    logger.finest("INSERTING ROW IN [" +
//                                        relations[z].getObjectName() + "]");
    
                                    if (!wa[z].insertRow(ctx, row))
                                    {
                                        throw new DataException("0000",
                                            "Invalid state of the data, Expeceted row doesn't exist.");
                                    }
                                }
                                else if (row.wasChanged())
                                {
                                    rows++;
//                                    logger.finest("UPDATING ROW IN [" +
//                                        relations[z].getObjectName() + "]");
//    
                                    if (!wa[z].updateRow(ctx, row))
                                    {
                                        throw new DataException("0000",
                                            "Invalid state of the data, Expeceted row doesn't exist.");
                                    }
                                }
                            }
                        }
    
                        DataSet[] childDataSet = row.getLoadedChildRows();
    
                        for (byte z = 0;
                                (childDataSet != null) &&
                                (z < childDataSet.length); z++)
                        {
                            updateDataSet(ctx, childDataSet[z], false, rows);
                        }
                    }
                }
    
                dataSet.reset();
            }
            finally
            {
                for (byte i = 0;wa != null && i < wa.length; i++)
                {
                    wa[i].close();
                }
            }
        }
        return rows;
    }

    private static final void fillSystemFields(EboContext ctx, DataRow row)
    {
        int colidx;

        if ((colidx = row.getDataSet().findColumn("SYS_USER")) > 0)
        {
            row.updateLong(colidx, ctx.getBoSession().getPerformerBoui());
        }

        if ((colidx = row.getDataSet().findColumn("SYS_DTCREATE")) > 0)
        {
            if (row.getTimestamp(colidx) == null)
            {
                row.updateTimestamp(colidx,
                    new Timestamp(System.currentTimeMillis()));
            }
        }

        if ((colidx = row.getDataSet().findColumn("SYS_DTSAVE")) > 0)
        {
            row.updateTimestamp(colidx,
                new Timestamp(System.currentTimeMillis()));
        }
    }
}
