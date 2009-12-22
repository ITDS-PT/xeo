/*Enconding=UTF-8*/
package netgest.bo.builder.tasks;

import netgest.bo.builder.ITask;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.bo.runtime.robots.boTextIndexAgent;
import netgest.bo.system.boConnectionManager;

import netgest.io.iFile;

import netgest.utils.DataUtils;

import oracle.sql.BLOB;

import netgest.bo.system.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Enumeration;

import netgest.bo.data.Driver;
import netgest.bo.system.boApplication;


public class FileSystemMigration implements ITask
 {
    private static Logger logger = Logger.getLogger(netgest.bo.builder.tasks.FileSystemMigration.class);
    private boConnectionManager connection = null;

    public boolean done(EboContext context)
     {
        boolean result = true;
        Connection cn = null;

        try
         {
            cn = context.getConnectionData();
            result = !execute(cn, "SELECT type FROM dbfs_file", false);
        }
         catch (SQLException e)
         {
            //ignore
        }
         finally
         {
            try
             {
                if (cn != null)
                {
                    cn.close();
                }
            }
             catch (Exception e)
             {
            }
        }

        return result;
    }

    public boolean validation(EboContext context)
     {
        boolean result = false;
        boObjectList documentList = null;

        try
         {
            boObject document = null;
            iFile file = null;
            String path = null;
            documentList = boObjectList.list(context,
                    "SELECT Ebo_DocBase WHERE file LIKE '//ngtbo/%'", 1,
                    999999, null, false, false);
            documentList.beforeFirst();

            if (documentList.getRecordCount() > 0)
             {
                while (documentList.next())
                 {
                    document = documentList.getObject();

                    try
                     {
                        if (document.getAttribute("file") != null)
                         {
                            if (isValid(document, false))
                             {
                                result = true;
                            }
                        }
                    }
                     catch (Exception e)
                     {
                    }

                    if (!result)
                     {
                        logger.finest("Object invalid for migration: " +
                            document.getBoui() + ", ClassName : " +
                            document.getName());
                    }
                }
            }
            else
             {
                logger.finest(" Migration already done ");
            }
        }
         catch (boRuntimeException e)
         {
            e.printStackTrace();
        }

        if (result)
         {
            logger.finest("Validation successfull, you can now migrate.");
        }

        return result;
    }

    public boolean execute(EboContext context)
     {
        boolean result = false;
        connection = context.getConnectionManager();

        try
         {
            if (!done(context))
             {
                context.getApplication().suspendAgents();
                Connection cn = connection.getConnection();
                connection.beginContainerTransaction();
                logger.finer("Start migrating dbfs_file to new the version.");
                if(!execute(cn, "alter table dbfs_file add (KEY NUMBER)", false))
                {
                    execute(cn, "update dbfs_file set key = null", false);
                }

                if (!execute(cn, "select id from dbfs_file_aux", false))
                 {
                    execute(cn,
                        "create table dbfs_file_aux (ID NUMBER, FILENAME VARCHAR(250), BINDATA BLOB)",
                        false);
                }


                result = copyTable(cn, "dbfs_file", "dbfs_file_aux");

                if (result)
                 {
                    boObjectList list = alterDocuments(context, cn);

                    if (list != null)
                     {
                        result = alterTableName(cn, "dbfs_file", "dbfs_file_old");

                        if (result)
                         {
                            result = alterTableName(cn, "dbfs_version",
                                    "dbfs_version_old");

                            if (result)
                             {
                                result = alterTableName(cn, "dbfs_file_aux",
                                        "dbfs_file");
                                list.beforeFirst();

                                boObject d = null;

                                while (list.next())
                                 {
                                    try
                                     {
                                        d = list.getObject();

                                        if (isValid(d, true))
                                         {
                                            d.update();
                                        }
                                    }
                                     catch (Exception e)
                                     {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }               
                logger.finer("Ended migrating dbfs_file to new the version.");
            }
            else
             {
                result = true;
            }
        }
         catch (Exception e)
         {
            e.printStackTrace();
        }
         finally
         {
            try
             {
                if (result)
                 {
                    connection.commitContainterTransaction();
                }
                else
                 {
                    connection.rollbackContainerTransaction();
                }
                context.getApplication().startAgents();
            }
             catch (Exception e)
             {
            }
        }

        return result;
    }

    private boolean isValid(boObject object, boolean verbose)
     {
        boolean result = true;
        AttributeHandler attHandler = null;
        bridgeHandler bridge = null;
        boObject objAux = null;

        try
         {
            Enumeration oEnum = object.getAttributes().elements();

            while (oEnum.hasMoreElements() && result)
             {
                attHandler = (AttributeHandler) oEnum.nextElement();

                if (attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_OBJECTATTRIBUTE)
                 {
                    if (attHandler.getDefAttribute().getRelationType() == attHandler.getDefAttribute().RELATION_1_TO_1)
                     {
                        objAux = attHandler.getObject();

                        if (objAux != null)
                         {
                            if (!objAux.exists())
                             {
                                result = false;
                            }
                        }
                    }
                    else
                     {
                        bridge = object.getBridge(attHandler.getName());
                        bridge.beforeFirst();

                        while (bridge.next())
                         {
                            objAux = bridge.getObject();

                            if (!objAux.exists() && result)
                             {
                                result = false;
                            }
                        }
                    }
                }
            }
        }
         catch (Exception e)
         {
            result = false;
        }

        try
         {
            if (!object.valid())
             {
                result = false;
            }
        }
         catch (boRuntimeException e)
         {
            result = false;
        }

        if (!result && verbose)
         {
            logger.severe("Object attributes invalid : " + object.getBoui());
        }

        return result;
    }

    public boolean rollback(EboContext context)
     {
        boolean result = false;

        try
         {
            connection = context.getConnectionManager();

            boObjectList list = roolbackDocuments(context,
                    connection.getConnection());

            if (list != null)
             {
                result = execute(connection.getConnection(),
                        "drop table dbfs_file", true);

                if (result)
                 {
                    result = alterTableName(connection.getConnection(),
                            "dbfs_file_old", "dbfs_file");

                    if (result)
                     {
                        result = alterTableName(connection.getConnection(),
                                "dbfs_version_old", "dbfs_version");

                        if (result)
                         {
                            result = execute(connection.getConnection(),
                                    "update dbfs_file set key = null", true);
                            connection.commitContainterTransaction();
                            list.beforeFirst();

                            while (list.next())
                             {
                                list.getObject().update();
                            }
                        }
                    }
                }
            }
        }
         catch (Exception e)
         {
            e.printStackTrace();
        }
         finally
         {
            try
             {
                if (result)
                 {
                    connection.commitContainterTransaction();
                }
                else
                 {
                    connection.rollbackContainerTransaction();
                }
            }
             catch (Exception e)
             {
            }
        }

        return result;
    }

    private boolean alterTableName(Connection connection, String fromTableName,
        String toTableName) throws SQLException
     {
        boolean result = false;
        StringBuffer sql = new StringBuffer("alter table ");
        sql.append(fromTableName).append(" RENAME TO ").append(toTableName);

        PreparedStatement pstm = null;

        try
         {
            pstm = connection.prepareStatement(sql.toString());
            pstm.execute();
            result = true;
        }
         catch (SQLException e)
         {
            throw e;
        }
         finally
         {
            try
             {
                if (pstm != null)
                {
                    pstm.close();
                }
            }
             catch (SQLException e)
             {
                throw e;
            }
        }

        return result;
    }

    private boolean execute(Connection connection, String sql, boolean required)
        throws SQLException
     {
        boolean result = false;
        PreparedStatement pstm = null;

        try
         {
            pstm = connection.prepareStatement(sql);
            pstm.execute();
            result = true;
        }
         catch (SQLException e)
         {
            if (required)
             {
                throw e;
            }
        }
         finally
         {
            try
             {
                if (pstm != null)
                {
                    pstm.close();
                }
            }
             catch (SQLException e)
             {
                throw e;
            }
        }

        return result;
    }

    private boObjectList alterDocuments(EboContext context,
        Connection connection)
     {
        boolean result = false;
        boObjectList documentList = null;

        try
         {
            boObject document = null;
            iFile file = null;
            String path = null;
            documentList = boObjectList.list(context,
                    "SELECT Ebo_DocBase WHERE file LIKE '//%' ORDER BY BOUI ASC", 1, 999999,
                    null, false, false);
            documentList.beforeFirst();

            while (documentList.next())
             {
                document = documentList.getObject();

                try
                 {
                    if (document.valid())
                     {
                        if (document.getAttribute("file") != null)
                         {
                            file = document.getAttribute("file").getValueiFile();
                            if(file != null)
                            {

                            path = "//basic/" + file.getKey() + "/" +
                                file.getName();
                            logger.finer("Changing Ebo_DocBase : " +
                                document.getBoui() + " , File : " + path);
                            document.getAttribute("file").setValueString(path);

//                            logger.finer("updating dbfs_file_aux boui : " +
//                                document.getBoui() + " , key : " + file.getKey());
//                            execute(connection,"UPDATE dbfs_file_aux set BOUI = " + document.getBoui() +" WHERE id = "+ file.getKey(),true);
                            }
                        }
                    }
                }
                 catch (Exception e)
                 {
                    logger.severe("Cannot change document : " +
                        document.getBoui());
                }
            }

            result = true;
        }
         catch (boRuntimeException e)
         {
            e.printStackTrace();
        }

        return documentList;
    }
    private boObjectList roolbackDocuments(EboContext context,
        Connection connection)
     {
        boolean result = false;
        boObjectList documentList = null;

        try
         {
            boObject document = null;
            String path = null;
            documentList = boObjectList.list(context,
                    "SELECT Ebo_DocBase WHERE 1=1", false, false);
            documentList.beforeFirst();

            while (documentList.next())
             {
                document = documentList.getObject();
                path = "//ngtbo/Ebo_Documents/" + document.getBoui() + "/" +
                    document.getAttribute("fileName").getValueString();
                document.getAttribute("file").setValueString(path);
            }

            result = true;
        }
         catch (boRuntimeException e)
         {
            e.printStackTrace();
        }

        return documentList;
    }

    private boolean copyTable(Connection connection, String fromTableName,
        String toTableName) throws SQLException
     {
        boolean result = false;
        String fileName = null;
        long id = -1;
        long newId = -1;
        PreparedStatement pstm = null;
        ResultSet rslt = null;
        StringBuffer sql = new StringBuffer("SELECT ID,FILENAME FROM ");
        sql.append(fromTableName).append(" WHERE TYPE = 1 AND KEY IS NULL ORDER BY ID ASC");

        try
         {
            pstm = connection.prepareStatement(sql.toString());
            rslt = pstm.executeQuery();

            while (rslt.next())
             {
                id = rslt.getLong(1);
                fileName = rslt.getString(2);
                newId = DataUtils.getDataDBSequence( boApplication.currentContext().getEboContext() ,
                        fromTableName + "_SEQ", Driver.SEQUENCE_NEXTVAL);

                if (createKey(connection, newId, fileName))
                 {
                    if (copyStream(getInputStream(connection, id),getOutputStream(connection, newId)))
                    {
                        insertKey(connection, id, newId);
                        logger.finest("Already copy ID : " + id + " KEY : " + newId);
                    }
                }
            }

            result = true;
        }
         catch (SQLException e)
         {
            throw e;
        }
         finally
         {
            try
             {
                if (rslt != null)
                {
                    rslt.close();
                }
            }
             catch (SQLException e)
             {
            }

            ;

            try
             {
                if (pstm != null)
                {
                    pstm.close();
                }
            }
             catch (SQLException e)
             {
            }

            ;
        }

        return result;
    }

    private OutputStream getOutputStream(Connection connection, long id)
     {
        OutputStream result = null;
        BLOB blob = null;
        ResultSet rslt = null;
        PreparedStatement pstm = null;

        try
         {
            //JBOSS
            //connection.setAutoCommit(false);
            pstm = connection.prepareStatement(
                    "SELECT BINDATA FROM dbfs_file_aux WHERE ID = ? ");
            pstm.setLong(1, id);

            rslt = pstm.executeQuery();

            if (rslt.next())
             {
                blob = (BLOB) rslt.getBlob(1);
                result = blob.getBinaryOutputStream(0);

                // RCAMPOS -> removed due to deprecation, using newer method
                //   compatible with JDBC 3.0
                // result = blob.getBinaryOutputStream(0);
            }
        }
         catch (SQLException e)
         {
            e.printStackTrace();
        }
         finally
         {
            try
             {
                if (rslt != null)
                {
                    rslt.close();
                }
            }
             catch (SQLException e)
             {
            }

            ;

            try
             {
                if (pstm != null)
                {
                    pstm.close();
                }
            }
             catch (SQLException e)
             {
            }

            ;
        }

        return result;
    }

    private InputStream getInputStream(Connection connection, long id)
     {
        InputStream result = null;
        Blob blob = null;
        ResultSet rslt = null;
        PreparedStatement pstm = null;

        try
         {
            pstm = connection.prepareStatement(
                    "SELECT BINDATA FROM dbfs_version WHERE ID=? AND VERSION = (SELECT MAX(VERSION) FROM dbfs_version WHERE ID = ?) FOR UPDATE WAIT 300");
            pstm.setLong(1, id);
            pstm.setLong(2, id);
            rslt = pstm.executeQuery();

            if (rslt.next())
             {
                blob = rslt.getBlob(1);
                result = blob.getBinaryStream();
            }
        }
         catch (SQLException e)
         {
            e.printStackTrace();
        }
         finally
         {
            try
             {
                if (rslt != null)
                {
                    rslt.close();
                }
            }
             catch (SQLException e)
             {
            }

            ;

            try
             {
                if (pstm != null)
                {
                    pstm.close();
                }
            }
             catch (SQLException e)
             {
            }

            ;
        }

        return result;
    }

    private boolean createKey(Connection connection, long newId, String fileName)
     {
        boolean result = false;
        String sql = "INSERT INTO dbfs_file_aux(ID,FILENAME,BINDATA) VALUES (?,?,?)";
        PreparedStatement pstm = null;

        try
         {
            pstm = connection.prepareStatement(sql);
            pstm.setLong(1, newId);
            pstm.setString(2, fileName);
            pstm.setBlob(3, BLOB.empty_lob());
            pstm.execute();
            result = true;
        }
         catch (SQLException e)
         {
            e.printStackTrace();
        }
         finally
         {
            try
             {
                if (pstm != null)
                {
                    pstm.close();
                }
            }
             catch (Exception e)
             {
                e.printStackTrace();
            }
        }

        return result;
    }

    private boolean insertKey(Connection connection, long id, long key)
     {
        boolean result = false;
        String sql = "UPDATE dbfs_file SET KEY = ? WHERE ID = ?";
        PreparedStatement pstm = null;

        try
         {
            pstm = connection.prepareStatement(sql);
            pstm.setLong(1, key);
            pstm.setLong(2, id);
            pstm.execute();
            result = true;
        }
         catch (SQLException e)
         {
            e.printStackTrace();
        }
         finally
         {
            try
             {
                if (pstm != null)
                {
                    pstm.close();
                }
            }
             catch (Exception e)
             {
                e.printStackTrace();
            }
        }

        return result;
    }

    private boolean copyStream(InputStream input, OutputStream output)
     {
        boolean result = false;

        try
         {
            if ((input != null) && (output != null))
             {
                byte[] buff = new byte[8192];
                int br;

                while ((br = input.read(buff)) > 0)
                 {
                    output.write(buff, 0, br);
                }

                output.close();
                input.close();
                result = true;
            }
        }
         catch (IOException e)
         {
            e.printStackTrace();
        }

        return result;
    }
}

