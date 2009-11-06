/*Enconding=UTF-8*/
package netgest.bo.ejb.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import netgest.bo.boConfig;
import netgest.bo.ejb.boManagerLocalHome;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boLoginLocalHome;
import netgest.bo.system.boSession;

import netgest.io.FSiFile;
import netgest.io.iFile;

import netgest.utils.IOUtils;

import org.apache.log4j.Logger;

import xeo.client.business.events.ClientEvents;
import xeo.client.business.events.FileSystemEvents;
import xeo.client.business.helper.ServiceHelper;


public class boClientRemoteBean implements SessionBean 
{
   private static Logger logger = Logger.getLogger("netgest.bo.ejb.impl.boClientRemoteBean");
    
    public void ejbCreate()
    {         
    }

    public void ejbActivate()
    {
    }

    public void ejbPassivate()
    {
    }

    public void ejbRemove()
    {
    }

    public void setSessionContext(SessionContext ctx)
    {
    }
 
    public void upload(String sessionId, String boui, byte[] buff) 
    {
        EboContext  boctx = null;
        try{      
            boctx = getEboContext(sessionId);
            boObject object = boObject.getBoManager().loadObject( boctx, Long.parseLong(boui));
            iFile ifile = object.getAttribute("file").getValueiFile();
            ifile.setBinaryStream( new ByteArrayInputStream(buff) );
        }
        catch(Exception e)
        {        
            throw new RuntimeException(e.getMessage());
        }  
        finally
        {
            boctx.close();            
        }        
                    
    }    
    public void upload(String sessionId, String boui, String fileName, byte[] buff) 
    {
        EboContext  boctx = null;
        try{      
            boctx = getEboContext(sessionId);
            boObject object = boObject.getBoManager().loadObject( boctx, Long.parseLong(boui));
            iFile file = new FSiFile(null,getFile(fileName,buff),null);        
            object.getAttribute("file").setValueiFile(file);
            object.update();            
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }    
        finally
        {
            boctx.close();            
        }        
    }
    public String newDocument(String sessionId, byte[] buff, String fileName, String template)
    {
        EboContext boctx = null;
        boObject object = null;
        try{      
            boctx = getEboContext(sessionId);
            object = boObject.getBoManager().createObject(boctx,"Ebo_Document");
            iFile file = new FSiFile(null,getFile(fileName,buff),null);        
            object.getAttribute("file").setValueiFile(file);
            object.getAttribute("docTemplate").setValueString(template);
            object.update();                
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.getMessage());   
        }
        finally
        {
            boctx.close();            
        }
        return String.valueOf(object.getBoui());
    }    
    public long getDocument(String sessionId, String client, String boui) 
    {        
        EboContext  boctx = null;
        byte[] ff = null;
        InputStream input = null;
        try
        {  
            final InitialContext context = new InitialContext();
            boctx = getEboContext(sessionId);
            boObject object = boObject.getBoManager().loadObject( boctx, Long.parseLong(boui));            
            iFile ifile = object.getAttribute("file").getValueiFile();
            input = ifile.getInputStream();
            if(input != null)
            {
                ff = IOUtils.copyByte(input);                        
//                FileSystemEvents fs = (FileSystemEvents)context.lookup("XEOClient/" + client + "/FileSystem");
                FileSystemEvents fs = ServiceHelper.getFileSystem(context,client);                                        
                fs.save(ifile.getName(),boui,ff);
//                fs.save(object.getAttribute("fileName").getValueString(),boui,ff);
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }   
        finally
        {
            try{if(input != null) input.close();}catch(Exception e){}
            boctx.close();            
        }
        return ff.length;
    }
    public long getDocument(String sessionId, String client, String boui, long timestamp)
    {
        long result = 0;
        EboContext  boctx = null;
        byte[] ff = null;
        InputStream input = null;
        try
        {  
            final InitialContext context = new InitialContext();
            boctx = getEboContext(sessionId);
            boObject object = boObject.getBoManager().loadObject( boctx, Long.parseLong(boui));
            
            iFile ifile = object.getAttribute("file").getValueiFile();
            input = ifile.getInputStream();
            if(input != null)
            {
                ff = IOUtils.copyByte(input);                 
                FileSystemEvents fs = ServiceHelper.getFileSystem(context,client);
                fs.save(ifile.getName(),boui,ff,timestamp);
//                fs.save(object.getAttribute("fileName").getValueString(),boui,ff,timestamp);
                result =  ff.length;
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }   
        finally
        {
            try{if(input != null) input.close();}catch(Exception e){}
            boctx.close();            
        }
        return result;
    }    
    private File getFile(String fileName ,byte[] input)
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
            throw new RuntimeException(e.getMessage());
        }
        catch(IOException e2)
        {
            throw new RuntimeException(e2.getMessage());
        }       
        return file;
    } 


    private EboContext getEboContext(String sessionId)
    {        
        boSession bosess = boApplication.getApplicationFromStaticContext("XEO").getSessions().getSessionById(sessionId);
        return bosess.createRequestContext(null,null,null);     
    }
    private EboContext getEboContext(String username,String password) throws boLoginException
    {     
        boSession bosess = boApplication.getApplicationFromStaticContext("XEO").boLogin(username,password);
        return bosess.createRequestContext(null,null,null);                 
    }    
    private boLoginLocalHome getboLoginLocalHome() throws NamingException
    {
        final InitialContext context = new InitialContext();
        return (boLoginLocalHome)context.lookup("java:comp/env/ejb/local/boLogin");
    }

    private boManagerLocalHome getboManagerLocalHome() throws NamingException
    {
        final InitialContext context = new InitialContext();
        return (boManagerLocalHome)context.lookup("java:comp/env/ejb/boManagerLocal");
    }       


    public boolean getNewVersion(String userHostClient)
    {
        boolean result = false;
        try
        {
            String path = boConfig.getWin32ClientConfig().getProperty("path");
            if(!path.endsWith(File.separator))
            {
                path += File.separator;
            }
            String fileName = boConfig.getWin32ClientConfig().getProperty("name");
            FileInputStream  in = new FileInputStream(path + fileName);                
            final InitialContext context = new InitialContext();
            byte[] buf = IOUtils.copyByte(in);
            
//            String serverVersionStr = getVersion();
//            serverVersionStr = serverVersionStr.replaceAll("\\.","");
//            long serverVersion = Long.parseLong(serverVersionStr);
//            if(serverVersion < 95)
//            {
//                FileSystemEvents fs = ServiceHelper.getFileSystem(context,userHostClient);
//                fs.updateVersion(fileName,buf);
//                result = true;                  
//            }
//            else
//            {
                ClientEvents client = ServiceHelper.getClient(context,userHostClient);
                client.updateVersion(fileName,buf);
                result = true;               
//            }            
        }
        catch(Exception e)
        {
            logger.error("Erro na actualização do netgest Win32 Client : " + userHostClient);
        }
        return result;
    }

    public long getDocument(EboContext ctx, String client, String boui, long timestamp)
    {
        byte[] ff = null;
        InputStream input= null;
        try
        {  
            boObject object = boObject.getBoManager().loadObject( ctx, Long.parseLong(boui));
            
            iFile ifile = object.getAttribute("file").getValueiFile();
            input = ifile.getInputStream();
            if(input != null)
            {
                ff = IOUtils.copyByte(input);     
                final InitialContext context = new InitialContext();
                FileSystemEvents fs = ServiceHelper.getFileSystem(context,client);
                fs.save(ifile.getName(),boui,ff,timestamp);
//                fs.save(object.getAttribute("fileName").getValueString(),boui,ff,timestamp);
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
        finally
        {
            try{if(input != null) input.close();}catch(Exception e){}
        }
        return ff.length;            
    }

    public String getVersion()
    {
        return boConfig.getWin32ClientConfig().getProperty("version");  
    }

    public byte[] getFile(String sessionId, long boui)
    {
        byte[] result = null;
        InputStream input= null;
        try
        {  
            EboContext ctx = getEboContext(sessionId);
            boObject object = boObject.getBoManager().loadObject(ctx, boui);            
            iFile ifile = object.getAttribute("file").getValueiFile();
            input = ifile.getInputStream();
            if(input != null)
            {
                result = IOUtils.copyByte(input);     
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
        finally
        {
            try{if(input != null) input.close();}catch(Exception e){}
        }
        return result;         
    }
}