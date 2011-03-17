package netgest.bo.impl.document.merge.gestemp.client;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Date;
import netgest.bo.dochtml.docHTML;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.system.*;
import netgest.io.FSiFile;
import netgest.io.iFile;
import netgest.utils.IOUtils;
import netgest.bo.system.Logger;

public class ClientHelper  
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.document.merge.gestemp.GtCampoManual");
    
    public ClientHelper()
    {
    }
    
    /**
     * 
     * @webmethod 
     */
    public static byte[] getDocumentByBoui(long boui)
    {
        boSession session = null;
        InputStream input = null;
        iFile ifile = null;
        byte[] toRet = null;
        EboContext boctx = null;
        try
        {
            session = boApplication.getApplicationFromStaticContext("XEO").boLogin("SYSUSER",boLoginBean.getSystemKey());
            boctx = session.createRequestContext(null, null, null);
            
            boObject object = boObject.getBoManager().loadObject( boctx, boui);

            ifile = object.getAttribute("file").getValueiFile();
            input = ifile.getInputStream();
            if(input != null)
            {
                toRet = IOUtils.copyByte(input);
            }
        }
        catch (boLoginException e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNABLE_TO_LOGIN"), e);
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNEXPECTED_ERROR")+":", e);
        }
        finally
        {
            try
            {
                if(input != null)
                {
                    input.close();
                }
            }
            catch (Exception e)
            {
                //ignore
            }
            try{boctx.close();}catch (Exception e){}
            try{session.closeSession();}catch (Exception e){}
        }
        return toRet;
    }
    
    /**
     * 
     * @webmethod 
     */
    public static void setDocumentByBoui(long boui, byte[] buff)
    {
        boSession session = null;
        EboContext boctx = null;
        try
        {
            session = boApplication.getApplicationFromStaticContext("XEO").boLogin("SYSUSER",boLoginBean.getSystemKey());
            boctx = session.createRequestContext(null, null, null);
            
            boObject object = boObject.getBoManager().loadObject( boctx, boui);
            String filename = object.getAttribute("fileName").getValueString();
            if(filename == null || "".equalsIgnoreCase(filename))
            {
                iFile oFile = object.getAttribute("file").getValueiFile();
                if( oFile != null )
                {
                    filename = oFile.getName();
                }
                else
                {
                    filename = "filename";
                }
            }
            iFile file = new FSiFile(null,getFile(filename,buff),null);
            object.getAttribute("file").setValueiFile(file);
        }
        catch (boLoginException e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNABLE_TO_LOGIN"), e);
            throw new RuntimeException(MessageLocalizer.getMessage("UNABLE_TO_LOGIN"));
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNEXPECTED_ERROR")+":", e);
            throw new RuntimeException(MessageLocalizer.getMessage("SERVER_ERROR_TRY_AGAIN"));
        }
        finally
        {
            try{boctx.close();}catch (Exception e){}
            try{session.closeSession();}catch (Exception e){}
        }
    }
    
    
    /**
     * 
     * @webmethod 
     */
    public static byte[] getDocument(String sessionID, String transactionID, long boui)
    {
        boSession session = null;
        InputStream input = null;
        iFile ifile = null;
        byte[] toRet = null;
        EboContext boctx = null;
        String params[] = transactionID.split("\\|");
        
        try
        {
            session = boApplication.getApplicationFromStaticContext("XEO").getSessions().getSessionById( sessionID );
            if(session != null)
            {
                boctx = session.createRequestContext(null, null, null);
                boctx.setPreferredPoolObjectOwner( params[0] );
                docHTML docHtml = (docHTML)boApplication.getApplicationFromStaticContext("XEO").getMemoryArchive().
                    getPoolManager().getObject(boctx,params[0], "DOCHTML:IDX:"+params[1]);
                boctx.setPreferredPoolObjectOwner(docHtml.poolUniqueId());
            
                boObject object = boObject.getBoManager().loadObject( boctx, boui);
    
                ifile = object.getAttribute("file").getValueiFile();
                input = ifile.getInputStream();
                if(input != null)
                {
                    toRet = IOUtils.copyByte(input);
                }
                
                docHtml.getDochtmlController().releseObjects( boctx );
                
            }
            else
            {
                logger.severe(LoggerMessageLocalizer.getMessage("USER_SESSION_WAS_CLOSED"));
                throw new RuntimeException(MessageLocalizer.getMessage("USER_SESSION_WAS_CLOSED"));
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(MessageLocalizer.getMessage("SERVER_ERROR_TRY_AGAIN"));
        }
        finally
        {
            try
            {
                if(input != null)
                {
                    input.close();
                }
            }
            catch (Exception e)
            {
                //ignore
            } 
            
            try{
            boctx.close();
            boctx.getApplication().getMemoryArchive().getPoolManager().realeaseObjects( params[0],boctx);
            }catch (Exception e){}
        }
        return toRet;
    }
    
    /**
     * 
     * @webmethod 
     */
    public static void setDocument(String sessionID, String transactionID, long boui, byte[] buff)
    {
        boSession session = null;
        EboContext boctx = null;
        String params[] = transactionID.split("\\|");
        try
        {
            //session = boApplication.getApplicationFromStaticContext("XEO").boLogin("SYSUSER",boLoginBean.getSystemKey());
            session = boApplication.getApplicationFromStaticContext("XEO").getSessions().getSessionById(  sessionID );
            if(session != null)
            {
                boctx = session.createRequestContext(null, null, null);
                boctx.setPreferredPoolObjectOwner( params[0] );
                docHTML docHtml = (docHTML)boApplication.getApplicationFromStaticContext("XEO").getMemoryArchive().
                    getPoolManager().getObject(boctx, params[0], "DOCHTML:IDX:"+params[1]);
                boctx.setPreferredPoolObjectOwner(docHtml.poolUniqueId());
                
                boObject object = boObject.getBoManager().loadObject( boctx, boui);
                String filename = object.getAttribute("fileName").getValueString();
                if(filename == null || "".equalsIgnoreCase(filename))
                {
                    iFile oFile = object.getAttribute("file").getValueiFile();
                    if( oFile != null )
                    {
                        filename = oFile.getName();
                    }
                    else
                    {
                        filename = "filename";
                    }
                }
                iFile file = new FSiFile(null,getFile(filename,buff),null);
                object.getAttribute("file").setValueiFile(file);

                docHtml.getDochtmlController().releseObjects( boctx );

            }
            else
            {
                logger.severe(LoggerMessageLocalizer.getMessage("USER_SESSION_WAS_CLOSED"));
                throw new RuntimeException(MessageLocalizer.getMessage("USER_SESSION_WAS_CLOSED"));
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNEXPECTED_ERROR")+":", e);
            throw new RuntimeException(MessageLocalizer.getMessage("SERVER_ERROR_TRY_AGAIN"));
        }
        finally
        {
            boctx.close();
            boctx.getApplication().getMemoryArchive().getPoolManager().realeaseObjects( params[0],boctx);
        }
    }
    
    private static File getFile(String fileName ,byte[] input)
    {
        File file = null;
        FileOutputStream out = null;
        try{
            out = new FileOutputStream(DocumentHelper.getTempDirHelper() + File.separator + fileName);    
            out.write(input);            
            out.close();                        
            file = new File(DocumentHelper.getTempDirHelper() + File.separator + fileName);        
        }
        catch(FileNotFoundException e)
        {
            throw new RuntimeException(MessageLocalizer.getMessage("FILE_NOT_FOUND"));
        }
        catch(IOException e2)
        {
            throw new RuntimeException(MessageLocalizer.getMessage("SERVER_ERROR_TRY_AGAIN"));
        }       
        return file;
    }
    
    /**
     * 
     * @webmethod 
     */
    public static void setPrinted(String sessionID, String transactionID, long parent, long boui)
    {
        boSession session = null;
        EboContext boctx = null;
        String params[] = transactionID.split("\\|"); 
        try
        {
            //session = boApplication.getApplicationFromStaticContext("XEO").boLogin("SYSUSER",boLoginBean.getSystemKey());
            session = boApplication.getApplicationFromStaticContext("XEO").getSessions().getSessionById(  sessionID );
            if(session != null)
            {
                boctx = session.createRequestContext(null, null, null);
                boctx.setPreferredPoolObjectOwner( params[0] );
                docHTML docHtml = (docHTML)boApplication.getApplicationFromStaticContext("XEO").getMemoryArchive().
                    getPoolManager().getObject(boctx, params[0], "DOCHTML:IDX:"+params[1]);
                boctx.setPreferredPoolObjectOwner(docHtml.poolUniqueId());
                
                boObject object = boObject.getBoManager().loadObject( boctx, parent);
                if("messageLetter".equals(object.getName()))
                {
                    object.getAttribute("dtEfectiv").setValueDate(new Date());
                }

                docHtml.getDochtmlController().releseObjects( boctx );

            }
            else
            {
                logger.severe(LoggerMessageLocalizer.getMessage("USER_SESSION_WAS_CLOSED"));
                throw new RuntimeException(MessageLocalizer.getMessage("USER_SESSION_WAS_CLOSED"));
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNEXPECTED_ERROR")+":", e); 
            throw new RuntimeException(MessageLocalizer.getMessage("SERVER_ERROR_TRY_AGAIN"));
        }
        finally
        {
            boctx.close();
            boctx.getApplication().getMemoryArchive().getPoolManager().realeaseObjects( params[0],boctx);
        }
    }
    /**
     * 
     * @webmethod 
     */
    public static String getAllTemplates()
    {
        boSession session = null;
        StringBuffer buffer = new StringBuffer();
        EboContext boctx = null;
        
        buffer.append("<templates>");
        try
        {
            session = boApplication.getApplicationFromStaticContext("XEO").boLogin("SYSUSER",boLoginBean.getSystemKey());
            boctx = session.createRequestContext(null, null, null);
            
            boObjectList templates = boObjectList.list(boctx,"select GESTEMP_Template where historico='0' order by code", 1, 9999 );
            templates.beforeFirst();
            while (templates.next())
            {
                boObject object = templates.getObject();
                
                String code = object.getAttribute("code").getValueString();
                String name = object.getAttribute("nome").getValueString();
                String description = object.getAttribute("descricao").getValueString();
                
                buffer.append("<template>");
                buffer.append("<code>");
                buffer.append(code);
                buffer.append("</code>");
                buffer.append("<name>");
                buffer.append(name);
                buffer.append("</name>");
                buffer.append("<description>");
                buffer.append(description);
                buffer.append("</description>");
                buffer.append("</template>");
                
            }
        }
        catch (boLoginException e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNABLE_TO_LOGIN"), e);
        }
        catch (Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNEXPECTED_ERROR")+":", e);
        }
        finally
        {
            boctx.close();
            session.closeSession();
        }
        buffer.append("</templates>");
        return buffer.toString();
    }
    
        /**
     * 
     * @webmethod 
     */
    public long createDocument(String fileName, byte[] buff)
    {
        boSession session = null;
        EboContext boctx = null;
        boObject object = null;
        try
        {
            session = boApplication.getApplicationFromStaticContext("XEO").boLogin("SYSUSER",boLoginBean.getSystemKey());
            boctx = session.createRequestContext(null, null, null);
            
            object = boObject.getBoManager().createObject(boctx, "Ebo_Document");
            
            iFile file = new FSiFile(null,getFile(fileName,buff),null);
            object.getAttribute("file").setValueiFile(file);
            object.getAttribute("fileSize").setValueLong(file.length());
            object.getAttribute("fileName").setValueString(fileName);
            object.getAttribute("description").setValueString("Gestão Documental - Ficheiro Genérico");
            object.update();
        }
        catch (boLoginException e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNABLE_TO_LOGIN"), e);
            throw new RuntimeException(MessageLocalizer.getMessage("UNABLE_TO_LOGIN"));
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNEXPECTED_ERROR")+":", e);
            throw new RuntimeException(MessageLocalizer.getMessage("SERVER_ERROR_TRY_AGAIN"));
        }
        finally
        {
            try{boctx.close();}catch (Exception e){}
            try{session.closeSession();}catch (Exception e){}
        }
        if (object != null)
            return object.getBoui();
        else
            return -1;
    }
    
    /**
     * 
     * @webmethod 
     */
    public long createDocumentInSession(String sessionID, String fileName, byte[] buff)
    {
        boSession session = null;
        EboContext boctx = null;

        boObject object = null;
        try
        {
            session = boApplication.getApplicationFromStaticContext("XEO").getSessions().getSessionById( sessionID );
            
            if(session != null)
            {
                boctx = session.createRequestContext(null,null,null);  
                                
            
                object = boObject.getBoManager().createObject(boctx, "Ebo_Document");
                
                iFile file = new FSiFile(null,getFile(fileName,buff),null);
                object.getAttribute("file").setValueiFile(file);
                object.getAttribute("fileSize").setValueLong(file.length());
                object.getAttribute("fileName").setValueString(fileName);
                object.getAttribute("description").setValueString("Gestão Documental - Ficheiro Genérico");
                object.update();
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNEXPECTED_ERROR")+":", e);
            throw new RuntimeException(MessageLocalizer.getMessage("SERVER_ERROR_TRY_AGAIN"));
        }
        finally
        {
            if (boctx!= null)
                boctx.close();
        }
        if (object != null)
            return object.getBoui();
        else
            return -1;
        
    }
    
    /**
     * 
     * @webmethod 
     */
    public void removeDocument(String sessionID, long boui)
    {
        boSession session = null;
        EboContext boctx = null;

        boObject object = null;
        try
        {
            session = boApplication.getApplicationFromStaticContext("XEO").getSessions().getSessionById( sessionID );
            
            if(session != null)
            {
                boctx = session.createRequestContext(null,null,null);  
                                
            
                object = boObject.getBoManager().loadObject(boctx, boui);
                
                object.destroy();
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("UNEXPECTED_ERROR")+":", e);
            throw new RuntimeException(MessageLocalizer.getMessage("SERVER_ERROR_TRY_AGAIN"));
        }
        finally
        {
            if (boctx!= null)
                boctx.close();
        }
    }

    
}