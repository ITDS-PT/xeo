/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge;

import com.softartisans.wordwriter.WordTemplate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.SQLException;

import java.util.ArrayList;

import netgest.bo.boConfig;
import netgest.bo.def.boDefPrinterDefinitions;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.io.FSiFile;
import netgest.io.iFile;

import netgest.utils.IOUtils;

import org.apache.log4j.Logger;


/**
 *
 * @author Pedro Castro Campos
 * @version 1.0
 */
public final class MergeHelper
{
    private final static String STRING_NULL = "";
    private final static String WORDPROCESS_SERIAL_KEY = "PFKFGU-54LF-KFEE-W5LBMA";

    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.document.merge.MergeHelper");

    public final static String mergeBoObject(boObject object, String fileName)
        throws boRuntimeException
    {
        FileInputStream file = null;
        FileOutputStream out = null;
        InputStream input = null;
        String filePath = null;
        File ff = null;

        try
        {
            String path = boConfig.getWordTemplateConfig().getProperty("path");
            boDefPrinterDefinitions printerDef = boDefPrinterDefinitions.loadPrinterDefinitions(object.getName());
            String tempFileName = printerDef.getDefaultTemplate();

            file = new FileInputStream(path + File.separator + tempFileName);
            input = new BufferedInputStream(file);

            WordTemplate template = new WordTemplate();
            template.setLicenseKey(WORDPROCESS_SERIAL_KEY);
            template.open(input);
            setDataSource(template, object, tempFileName);
            template.process();

            String dir = DocumentHelper.getTempDir();
            File ndir = new File(dir);

            if (!ndir.exists())
            {
                ndir.mkdirs();
            }

            ff = new File(dir + File.separator + fileName);
            out = new FileOutputStream(ff);
            template.save(out);
            filePath = ff.getAbsolutePath();
        }
        catch(boRuntimeException e)
        {
            throw e;
        }
        catch (Exception ex)
        {
            throw new boRuntimeException("", ex.getMessage(), ex);
        }
        finally
        {
            try
            {
                if (input != null)
                {
                    input.close();
                }

                if (file != null)
                {
                    file.close();
                }

                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
                logger.error("", e);
            }
        }

        return filePath;
    }

    public final static String mergeBoObject(boObject object, String templateFilePath, String fileName)
        throws boRuntimeException
    {
        File _file = null;
        FileInputStream file = null;
        FileOutputStream out = null;
        InputStream input = null;
        String filePath = null;
        File ff = null;

        try
        {
            _file = new File(templateFilePath);
            file = new FileInputStream(_file);
            input = new BufferedInputStream(file);

            WordTemplate template = new WordTemplate();
            template.setLicenseKey(WORDPROCESS_SERIAL_KEY);
            template.open(input);
            setDataSource(template, object, _file.getName());
            template.process();

            String dir = DocumentHelper.getTempDir();
            File ndir = new File(dir);

            if (!ndir.exists())
            {
                ndir.mkdirs();
            }

            ff = new File(dir + File.separator + fileName);
            if(!ff.getParentFile().exists())
            {
                ff.getParentFile().mkdirs();
            }
            out = new FileOutputStream(ff);
            template.save(out);
            filePath = ff.getAbsolutePath();
        }
        catch(boRuntimeException e)
        {
            throw e;
        }
        catch (Exception ex)
        {
            throw new boRuntimeException("", ex.getMessage(), ex);
        }
        finally
        {
            try
            {
                if (input != null)
                {
                    input.close();
                }

                if (file != null)
                {
                    file.close();
                }

                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
                logger.error("", e);
            }
        }

        return filePath;
    }

    public final static String mergeBoObject(boObject object, InputStream templateInputStream, String fileName)
        throws boRuntimeException
    {
        return mergeBoObject(object, null, templateInputStream, fileName);
    }
    public final static String mergeBoObject(boObject object, String templateName, InputStream templateInputStream, String fileName)
        throws boRuntimeException
    {
        FileOutputStream out = null;
        String filePath = null;
        File ff = null;

        try
        {

            WordTemplate template = new WordTemplate();
            template.setLicenseKey(WORDPROCESS_SERIAL_KEY);
            template.open(templateInputStream);
            if(templateName == null)
            {
                setDataSource(template, object, null);
            }
            else
            {
                setDataSource(template, object, templateName);
            }
            template.process();

            String dir = DocumentHelper.getTempDir();
            File ndir = new File(dir);

            if (!ndir.exists())
            {
                ndir.mkdirs();
            }

            ff = new File(dir + File.separator + fileName);
            if(!ff.getParentFile().exists())
            {
                ff.mkdirs();
            }
            out = new FileOutputStream(ff);
            template.save(out);
            filePath = ff.getAbsolutePath();
        }
        catch(boRuntimeException e)
        {
            throw e;
        }
        catch (Exception ex)
        {
            throw new boRuntimeException("", ex.getMessage(), ex);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
                logger.error("", e);
            }
        }

        return filePath;
    }

    private final static void setDataSource(WordTemplate template,
        boObject object, String templateName) throws boRuntimeException
    {
        boDefPrinterDefinitions boDefPD = boDefPrinterDefinitions.loadPrinterDefinitions(object.getName());
        if(boDefPD != null)
        {
            setDataSourceWDefinitions(template, object, templateName, boDefPD);
        }
        else
        {
            setDataSourceNdefinitions(template, object);
        }
    }

    private final static void setDataSourceWDefinitions(WordTemplate template,
        boObject object, String templateName, boDefPrinterDefinitions boDefPD) throws boRuntimeException
    {
        String[] allBookmarks = null;
        String[] fieldMarkers = null;
        Object[] dtSourcePrefixs = null;
        ArrayList bookMarked = new ArrayList();
        Tabela blockTab = null;

        try
        {
            allBookmarks = template.getBookmarks();
            fieldMarkers = template.getFieldMarkers();
            dtSourcePrefixs = getPrefixs(fieldMarkers);
        }
        catch (Exception e)
        {
            logger.error("", e);
        }
        if (boDefPD.isWordTemplate(templateName))
        {
            TemplateWord tempHtml = boDefPD.getTemplateWord(templateName);
            ArrayList reqDs = tempHtml.getRequiredDS();
            Tabela tab;
            DS auxDs;

            for (int i = 0; i < reqDs.size(); i++)
            {
                auxDs = (DS) reqDs.get(i);

                tab = auxDs.getData(object);

                if ("block".equalsIgnoreCase(auxDs.getType()))
                {
                    try
                    {
                        template.setDataSource(MergeResultSetFactory.getResultSet(tab));
                        blockTab = auxDs.getData(object);
                    }
                    catch (Exception e)
                    {
                        logger.error("", e);
                    }
                }
                else if ("repeatBlock".equalsIgnoreCase(auxDs.getType()))
                {
                    try
                    {
                        bookMarked.add(auxDs.getName());
                        template.setRepeatBlock(MergeResultSetFactory.getResultSet(tab),
                            auxDs.getName());
                    }
                    catch (Exception e)
                    {
                        logger.error("", e);
                    }
                }
            }
        }
        else
        {
            //HTML TEMPLATE
        }
        if(dtSourcePrefixs != null)
        {
            for (int i = 0; i < dtSourcePrefixs.length; i++)
            {
                try
                {
                    template.setDataSource(MergeResultSetFactory.getResultSet((String)dtSourcePrefixs[i], blockTab),(String)dtSourcePrefixs[i]);
                }
                catch (Exception e)
                {
                    //ignore
                }
            }

        }
        if(allBookmarks != null)
        {
            for (int i = 0; i < allBookmarks.length; i++)
            {
                if(bookMarked.indexOf(allBookmarks[i]) == -1)
                {
                    try
                    {
                        template.setRepeatBlock(MergeResultSetFactory.getResultSet(new Tabela(object)),
                                allBookmarks[i]);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }

        }
    }

    private final static void setDataSourceNdefinitions(WordTemplate template,
        boObject object) throws boRuntimeException
    {
        String[] allBookmarks = null;
        String[] fieldMarkers = null;
        Object[] dtSourcePrefixs = null;
        ArrayList bookMarked = new ArrayList();
        try
        {
            allBookmarks = template.getBookmarks();
            fieldMarkers = template.getFieldMarkers();
            dtSourcePrefixs = getPrefixs(fieldMarkers);
        }
        catch (Exception e)
        {
            logger.error("", e);
        }

        try
        {
            //os fields
            template.setDataSource(MergeResultSetFactory.getResultSet(object));
            //fields w prefix
            if(dtSourcePrefixs != null)
            {
                for (int i = 0; i < dtSourcePrefixs.length; i++)
                {
                    try
                    {
                        template.setDataSource(MergeResultSetFactory.getResultSet((String)dtSourcePrefixs[i], object), (String)dtSourcePrefixs[i]);
                    }
                    catch (Exception e)
                    {
                        //ignore
                    }
                }

            }
        }
        catch (SQLException e)
        {

        }
        catch (Exception e)
        {

        }
        if(allBookmarks != null)
        {
            for (int i = 0; i < allBookmarks.length; i++)
            {
                if(bookMarked.indexOf(allBookmarks[i]) == -1)
                {
                    try
                    {
                        template.setRepeatBlock(MergeResultSetFactory.getResultSet(object, allBookmarks[i]),
                                allBookmarks[i]);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        }
    }

    private static Object[] getPrefixs(String[] dtSources)
    {
        ArrayList toRet = new ArrayList();
        String aux;
        if(dtSources != null)
        {
            for (int i = 0; i < dtSources.length; i++)
            {
                aux = dtSources[i];
                if(aux.indexOf(".") != -1)
                {
                    toRet.add(aux.substring(0, aux.indexOf(".")));
                }
            }
        }
        return toRet.toArray();
    }

    public static long merge(boObject object,boObject template) throws boRuntimeException
    {
        long newDocumentBoui = -1;
        InputStream in = null;
        ByteArrayInputStream bais = null;
        try
        {
            String templateBoui = String.valueOf(template.getBoui());

            String fileName = template.getAttribute("fileName").getValueString();
            iFile ifile = template.getAttribute("file").getValueiFile();
            in = ifile.getInputStream();
            bais = new ByteArrayInputStream(IOUtils.copyByte(in));
            String prefix = null;
            if("message".equals(object.getName()) || "message".equals(object.getBoDefinition().getBoSuperBo()))
            {
                boBridgeIterator it = object.getBridge("to").iterator();
                it.beforeFirst();
                if(it.next())
                {
                    prefix = it.currentRow().getObject().getAttribute("name").getValueString();
                }
            }
            String fileTempName = netgest.bo.impl.document.merge.TempFile.createNameForTempFile(prefix, ".doc");
            String newFile = mergeBoObject(object, bais, fileTempName);
            boObject doc = object.getBoManager().createObject(object.getEboContext(), "Ebo_Document");
            doc.getAttribute("file").setValueiFile(new FSiFile(null,new File(newFile),null));
//            ifile = doc.getAttribute("file").getValueiFile();
//            ifile.getOutputStream().write(RegistryHelper.copyByte(new FileInputStream(newFile)));
            doc.getAttribute( "description" ).setValueString( template.getAttribute("description").getValueString() );
            doc.getAttribute( "fileName" ).setValueString( fileTempName );
            doc.getAttribute( "docTemplate" ).setObject(template);
            newDocumentBoui = doc.getBoui();
            doc.update();
        }
        catch(boRuntimeException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new boRuntimeException("", e.getMessage(), e);
        }
        finally
        {
            try{if(bais != null) bais.close();}catch(Exception e){}
            try{if(in != null) in.close();}catch(Exception e){}
        }
        return newDocumentBoui;
    }
    public static long reMerge(boObject object,boObject template, boObject doc) throws boRuntimeException
    {
        long newDocumentBoui = -1;
        InputStream in = null;
        ByteArrayInputStream bais = null;
        try
        {
            String templateBoui = String.valueOf(template.getBoui());

            String fileName = template.getAttribute("fileName").getValueString();
            iFile ifile = template.getAttribute("file").getValueiFile();
            in = ifile.getInputStream();
            bais = new ByteArrayInputStream(IOUtils.copyByte(in));
            String fileTempName = netgest.bo.impl.document.merge.TempFile.createNameForTempFile(null, ".doc");
            String newFile = mergeBoObject(object, bais, fileTempName);
            in.close();
            if(newFile != null)
            {
                doc.getAttribute("file").setValueiFile(new FSiFile(null,newFile,null));
                newDocumentBoui = doc.getBoui();
                doc.update();
            }
        }
        catch(boRuntimeException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new boRuntimeException("", e.getMessage(), e);
        }
        finally
        {
            try{if(bais != null) bais.close();}catch(Exception e){}
            try{if(in != null) in.close();}catch(Exception e){}
        }
        return newDocumentBoui;
    }

    private static void copyFile(File from, iFile to) throws Exception{
        byte[] b = new byte[1024*10];
        int numBytes = 0;

        BufferedOutputStream fos = null;
        FileInputStream fis = null;
        try{
            fos = new BufferedOutputStream(to.getOutputStream());
            fis = new FileInputStream(from);

            for(long i = 0; (numBytes = fis.read(b)) != -1;i++) {
                fos.write(b,0,numBytes);
            }
        }
        finally{
            if(fos != null){
                fos.close();
            }

            if(fis != null){
                fis.close();
            }
        }
    }

    // Passar primeiro pelo PrintHelper e este pode ser ou não static
    //    private final static void setDataSource(WordTemplate template, boObject object) throws boRuntimeException
    //    {
    //        try
    //        {
    //            template.setDataSource(new DataResultSet(ObjectDataManager.executeBOQL(object.getEboContext(),"SELECT "+ object.getName() +" WHERE BOUI = "+object.getBoui())));
    //        }
    //        catch (SQLException e)
    //        {
    //
    //        }
    //        boDefWds wds = boDefWds.loadWds(object.getBoDefinition().getWordTemplate().split("\\.")[0]);//.getBoDefHandler();
    //        boObject attObject = null;
    //        AttributeHandler attHandler = null;
    //        boAttributesArray boArray = object.getAttributes();
    //        Enumeration oEnum = boArray.elements();
    //        while( oEnum.hasMoreElements()  )
    //        {
    //            attHandler = (AttributeHandler)oEnum.nextElement();
    //
    //            if(attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_OBJECTATTRIBUTE)
    //            {
    //
    //                if(attHandler.getDefAttribute().getRelationType() != boDefAttribute.RELATION_1_TO_1)
    //                {
    //                    if(wds.isInTemplateDefinition(attHandler.getName()))
    //                    {
    //                        Hashtable t = new Hashtable();
    //                        Hashtable b = new Hashtable();
    //                        BigDecimal boui = null;
    //                        DataResultSet drs = null;
    //                        bridgeHandler bridge = object.getBridge(attHandler.getName());
    //                        bridge.beforeFirst();
    //                        while(bridge.next())
    //                        {
    //                            attObject = bridge.getObject();
    //                            t.put(attObject.getBoDefinition().getBoPhisicalMasterTable(),attObject.getBoDefinition().getBoPhisicalMasterTable());
    //                            boui = new BigDecimal(attObject.getBoui());
    //                            b.put(boui,boui);
    //                        }
    //
    //                        String tables = "";
    //                        String bridgesAnd = "";
    //                        String bouisList = "";
    //                        Enumeration enum33 = t.elements();
    //                        int size = t.size();
    //                        int count = 0;
    //                        while(enum33.hasMoreElements())
    //                        {
    //                            String table = enum33.nextElement().toString();
    //                            String bName = object.getBoDefinition().getBoPhisicalMasterTable() + "$" + attHandler.getName();
    //                            tables += table + "," + bName;
    //                            count ++;
    //                            bridgesAnd +=  table + ".BOUI = " + bName + ".CHILD$;";
    //                            if(count != size)
    //                            {
    //                                tables += ",";
    //                            }
    //                        }
    //                        bridgesAnd = bridgesAnd.replaceAll(";"," AND ");
    //                        enum33 = b.elements();
    //                        size = b.size();
    //                        count = 0;
    //                        while(enum33.hasMoreElements())
    //                        {
    //                            bouisList += enum33.nextElement().toString();
    //                            count ++;
    //                            if(count != size) bouisList += ",";
    //                        }
    //                        Connection con = null;
    //                        PreparedStatement ps = null;
    //                            try
    //                            {
    //                                con = object.getEboContext().getConnectionData();  //fechar
    //                                ps = con.prepareStatement("SELECT * FROM " + tables + " WHERE " + bridgesAnd + " BOUI in (" + bouisList + ")");
    //                                ResultSet rs = ps.executeQuery();
    //                                ResultSetMetaData ss =  rs.getMetaData();
    //                                template.setRepeatBlock(rs,attHandler.getName());
    //                            }
    //                            catch (SQLException e)
    //                            {
    //                               logger.error("", e);
    //                            }
    //                            catch (Exception e)
    //                            {
    //
    //                            }
    //                            finally
    //                            {
    //                                try
    //                                {
    //                                    /*if(ps != null) ps.close();
    //                                    if(con != null) con.close();                                */
    //                                }
    //                                catch (Exception e)
    //                                {
    //
    //                                }
    //                            }
    //
    //                    }
    //                }
    //            }
    //
    //        }
    //    }
}


/*


   public final static void setDataSource(WordTemplate template, boObject object) throws boRuntimeException, Exception
    {
        template.setDataSource(new DataResultSet(ObjectDataManager.executeBOQL(object.getEboContext(),"SELECT "+ object.getName() +" WHERE BOUI = "+object.getBoui())));
        boObject attObject = null;
        AttributeHandler attHandler = null;
        boAttributesArray boArray = object.getAttributes();
        Enumeration oEnum = boArray.elements();
        while( oEnum.hasMoreElements()  )
        {
            attHandler = (AttributeHandler)oEnum.nextElement();

            if(attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_OBJECTATTRIBUTE)
            {

                if(attHandler.getDefAttribute().getRelationType() != boDefAttribute.RELATION_1_TO_1)
                {
                    if("DAO".equals(attHandler.getName()))
                    {
                        Hashtable t = new Hashtable();
                        Hashtable b = new Hashtable();
                        BigDecimal boui = null;
                        String tables = "";
                        String bouisList = "";
                        DataResultSet drs = null;
                        bridgeHandler bridge = object.getBridge(attHandler.getName());
//                        DataSetMetaData dsmeta = getBridgeMetaData(bridge);
//                        DataSet ds = new DataSet(dsmeta);
                        bridge.beforeFirst();
                        while(bridge.next())
                        {
                            attObject = bridge.getObject();
                            t.put(attObject.getBoDefinition().getBoPhisicalMasterTable(),attObject.getBoDefinition().getBoPhisicalMasterTable());
                            boui = new BigDecimal(attObject.getBoui());
                            b.put(boui,boui);
//                            ds.insertRow(((boObject)bridge.getObject()).getDataRow());
                        }
                        Enumeration enum33 = t.elements();
                        int size = t.size();
                        int count = 0;
                        while(enum33.hasMoreElements())
                        {
                            tables += enum33.nextElement().toString();
                            count ++;
                            if(count != size) tables += ",";
                        }
                        enum33 = b.elements();
                        size = b.size();
                        count = 0;
                        while(enum33.hasMoreElements())
                        {
                            bouisList += enum33.nextElement().toString();
                            count ++;
                            if(count != size) bouisList += ",";
                        }
                        //drs = new DataResultSet(ds);
                        //template.setRepeatBlock(bridge.getRslt(),attHandler.getName());
                        //drs = new DataResultSet(boObjectList.gexecuteBOQL(object.getEboContext(),"SELECT * FROM " + tables + " WHERE BOUI in (" + bouisList + ")"));
                        Connection con = object.getEboContext().getConnectionData();

                        PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tables + " WHERE BOUI in (" + bouisList + ")");
                        template.setRepeatBlock(ps.executeQuery(),attHandler.getName());
                    }
                }
            }

        }
    }
    private static final DataSetMetaData getBridgeMetaData(bridgeHandler bridge) throws boRuntimeException
    {
        DataSetMetaData aux = null;

        Hashtable columnName = new Hashtable();
        ArrayList columnClassName = new ArrayList();
        ArrayList columnDisplaySize  = new ArrayList();
        ArrayList columnType = new ArrayList();
        ArrayList columnTypeName= new ArrayList();
        int countTotal = 0;
        int count = 0;
        DataSetMetaData dsMetaData = null;
        boObject attObject = null;
        bridge.beforeFirst();
        while(bridge.next())
        {
            attObject = bridge.getObject();
            aux = attObject.getDataSet().getMetaData();
            count = aux.getColumnCount();
            for (int i = 1; i < count + 1; i++)
            {
                if(columnName.containsKey(aux.getColumnName(i)) == false)
                {
                    Integer c = new Integer(i - 1);
                    columnName.put(aux.getColumnName(i),c);
                    columnClassName.add(c.intValue(),aux.getColumnClassName(i));
                    columnDisplaySize.add(c.intValue(),new Integer(aux.getColumnDisplaySize(i)));
                    columnType.add(c.intValue(),new Integer(aux.getColumnType(i)));
                    columnTypeName.add(c.intValue() ,aux.getColumnTypeName(i));
                }
            }
        }
        String[] columnNameStr = new String[columnName.size()];
        String[] columnClassNameStr = new String[columnName.size()];
        int[] columnTypeStr = new int[count];
        String[] columnTypeNameStr = new String[columnName.size()];
        int[] columnDisplaySizeStr = new int[columnName.size()];

        Enumeration oEnum = columnName.keys();
        String key = null;
        while(oEnum.hasMoreElements())
        {
            key = oEnum.nextElement().toString();
            int v = Integer.parseInt(columnName.get(key).toString());
            columnNameStr[v] = key;
            columnClassNameStr[v] = columnClassName.get(v).toString();
            columnDisplaySizeStr[v] = ((Integer)columnDisplaySize.get(v)).intValue();
            columnTypeStr[v] = new Integer(columnType.get(v).toString()).intValue();
            columnTypeNameStr[v] = columnTypeName.get(v).toString();

        }

        dsMetaData = new DataSetMetaData(columnName.size(),columnNameStr,columnClassNameStr,columnDisplaySizeStr,columnTypeStr,columnTypeNameStr);
        return dsMetaData;
    }

    public void setDataSourceXX(WordTemplate template, boObject object) throws boRuntimeException, Exception
    {
        boObject attObject = null;
        AttributeHandler attHandler = null;
        boAttributesArray boArray = object.getAttributes();
        Enumeration oEnum = boArray.elements();
        int position = 0;
        String[] attrName = new String[boArray.size()];
        Object[] attrValue = new Object[boArray.size()];
        while( oEnum.hasMoreElements()  )
        {
            attHandler = (AttributeHandler)oEnum.nextElement();

            if(attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_OBJECTATTRIBUTE)
            {

                if(attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1)
                {
                    attrName[position] = attHandler.getName();
                    attObject = attHandler.getObject();
                    if(attObject != null)
                    {
                        attrValue[position] = attObject.getCARDID();
                    }
                    else
                    {
                        attrValue[position] = STRING_NULL;
                    }
                }
                else if(attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_MULTI_VALUES)
                {
                    boObject[] attBoObjects = attHandler.getObjects();
                    if(attBoObjects != null)
                    {
                        for(int i = 0 ; i < attBoObjects.length ; i++)
                        {
                            attObject = attBoObjects[i];
                        }
                    }
                }
                else
                {

                    bridgeHandler bridge = object.getBridge(attHandler.getName());
                    bridge.beforeFirst();
                    while(bridge.next())
                    {
                        attObject = bridge.getObject();
                    }
                }

            }
            else
            {
                attrName[position] = attHandler.getName();
                attrValue[position] = attHandler.getValueString();
            }

            position ++;
        }
        template.setDataSource(attrValue,attrName);
    }
}


                     if("documents".equals(attHandler.getName()))
                    {

                        DataResultSet drs = null;
                        bridgeHandler bridge = object.getBridge(attHandler.getName());
                        DataSetMetaData dsmeta = getBridgeMetaData(bridge);
                        DataSet ds = new DataSet(dsmeta);
                        bridge.beforeFirst();
                        while(bridge.next())
                        {
                            ds.insertRow(((boObject)bridge.getObject()).getDataRow());
                        }

                        drs = new DataResultSet(ds);
                        //template.setRepeatBlock(bridge.getRslt(),attHandler.getName());
                        //drs = new DataResultSet(ObjectDataManager.executeBOQL(object.getEboContext(),"SELECT "+classes+" WHERE BOUI in (" +bouistr + ")"));
                        template.setRepeatBlock(drs,attHandler.getName());
                    }
*/
