/*Enconding=UTF-8*/
package netgest.bo.impl.document.print;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.rmi.RemoteException;

import java.util.Calendar;
import java.util.List;

import javax.ejb.CreateException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import netgest.bo.boConfig;
import netgest.bo.ejb.boClientRemote;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.impl.document.merge.MergeHelper;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boSession;

import netgest.io.iFile;

import netgest.utils.IOUtils;

import xeo.client.business.events.ClientEvents;
import xeo.client.business.events.FileSystemEvents;
import xeo.client.business.events.PrinterEvents;
import xeo.client.business.events.OfficeEvents;
import xeo.client.business.helper.ServiceHelper;

/**
 * <p>Title: PrintHelper </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public final class PrintHelper 
{

    public static void printAllDocuments(boObject object) throws boRuntimeException
    {
        List documents = DocumentHelper.getAllObjectDocuments(object);
        for (int i = 0; i < documents.size(); i++) 
        {
            printDocument((boObject)documents.get(i));   
        }
    }
    public static void printDocuments(List documents) throws boRuntimeException
    {        
        for (int i = 0; i < documents.size(); i++) 
        {
            printDocument((boObject)documents.get(i));   
        }
    }    
    public static void printDocument( boObject parent, String attr) throws boRuntimeException
    {                
        boObject document = boObject.getBoManager().loadObject(parent.getEboContext(),parent.getAttribute(attr).getValueLong());
        printDocument(document);
    }    
    public static void printDocument( boObject document ) throws boRuntimeException
    {
        DocumentHelper.print(document);
    }
    /*
        try
        {            
            iFile ifile = null;
            String fileName = null; 
            long timestamp = System.currentTimeMillis();
            InitialContext context = new InitialContext();
            boClientRemote remote = ServiceHelper.getClientRemote(context);                
            boSession bosession = (boSession)document.getEboContext().getRequest().getSession().getAttribute("boSession");
            String XeoWin32Client_address = (String)document.getEboContext().getRequest().getSession().getAttribute("XeoWin32Client_address");
            if(DocumentHelper.isMSWordFile(document))
            {
                OfficeEvents we = ServiceHelper.getOfficeWord(context,XeoWin32Client_address);            
                if(!we.isOpen(String.valueOf(document.getBoui())))
                {                                    
                    long size = remote.getDocument(bosession.getId() ,XeoWin32Client_address , String.valueOf(document.getBoui() ),timestamp);
                    ifile = document.getAttribute("file").getValueiFile();
                    fileName = ifile.getName(); 
//                    fileName = document.getAttribute("fileName").getValueString();
                    we.print(fileName, String.valueOf(document.getBoui()),timestamp);
                }
            }
            else
            {
                ClientEvents clientInfo = ServiceHelper.getClient(context,XeoWin32Client_address);
                PrinterEvents psys = ServiceHelper.getPrinter(context,XeoWin32Client_address,clientInfo.getDefaultPrinter());
                long size = remote.getDocument(bosession.getId() ,XeoWin32Client_address , String.valueOf(document.getBoui() ),timestamp);
                ifile = document.getAttribute("file").getValueiFile();
                fileName = ifile.getName();
//                fileName = document.getAttribute("fileName").getValueString();
                psys.print(fileName,timestamp);
            }
        }
        catch (CreateException e)
        {
            
            e.printStackTrace();
            
        }
        catch (NamingException e)
        {
            
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            
            e.printStackTrace();
        }        
    }*/
    
    public static void printObject( boObject object, String reportName ) throws boRuntimeException
    {
        try
        {              
            InitialContext context = new InitialContext();
            long timestamp = System.currentTimeMillis();
            String fileName = null; 
            if(object.getCARDIDwNoIMG() != null && object.getCARDIDwNoIMG().length() > 0)
            {
                fileName = object.getCARDIDwNoIMG() + ".doc";
            }
            else
            {
                Calendar c = Calendar.getInstance();
                fileName = c.getTimeInMillis() + ".doc";
            }
            String temPath = boConfig.getWordTemplateConfig().getProperty("path");
            String path = MergeHelper.mergeBoObject(object, temPath + reportName, fileName);
            String XeoWin32Client_address = (String)object.getEboContext().getRequest().getSession().getAttribute("XeoWin32Client_address");            
            FileSystemEvents fs = ServiceHelper.getFileSystem(context,XeoWin32Client_address);            
            FileInputStream ddf = new FileInputStream(path);                        
            byte[] buf = IOUtils.copyByte(ddf);     
            fs.save(fileName,String.valueOf(timestamp),buf,timestamp);
            OfficeEvents we = ServiceHelper.getOfficeWord(context,XeoWin32Client_address);
            we.print(fileName,String.valueOf(timestamp),timestamp);    
        }
        catch (NamingException e)
        {            
            e.printStackTrace();
        }
        catch (RemoteException e)
        {            
            e.printStackTrace();
        }        
        catch (FileNotFoundException e)
        {
            e.printStackTrace();  
        }        
        catch (IOException e)
        {
            e.printStackTrace();  
        }         
    }
    
    public static void printObject( boObject object ) throws boRuntimeException
    {
        try
        {              
            InitialContext context = new InitialContext();
            long timestamp = System.currentTimeMillis();
            String fileName = null; 
            if(object.getCARDIDwNoIMG() != null && object.getCARDIDwNoIMG().length() > 0)
            {
                fileName = object.getCARDIDwNoIMG() + ".doc";
            }
            else
            {
                Calendar c = Calendar.getInstance();
                fileName = c.getTimeInMillis() + ".doc";
            }
            String path = MergeHelper.mergeBoObject(object,fileName);
            String XeoWin32Client_address = (String)object.getEboContext().getRequest().getSession().getAttribute("XeoWin32Client_address");            
            FileSystemEvents fs = ServiceHelper.getFileSystem(context,XeoWin32Client_address);            
            FileInputStream ddf = new FileInputStream(path);                        
            byte[] buf = IOUtils.copyByte(ddf);     
            fs.save(fileName,String.valueOf(timestamp),buf,timestamp);
            OfficeEvents we = ServiceHelper.getOfficeWord(context,XeoWin32Client_address);
            we.print(fileName,String.valueOf(timestamp),timestamp);    
        }
        catch (NamingException e)
        {            
            e.printStackTrace();
        }
        catch (RemoteException e)
        {            
            e.printStackTrace();
        }        
        catch (FileNotFoundException e)
        {
            e.printStackTrace();  
        }        
        catch (IOException e)
        {
            e.printStackTrace();  
        }         
    }
    public static final boolean isXeoControlActive( EboContext ctx )
      {
          boolean result = false;
          if( ctx.getRequest() != null )
          {
            if( ctx.getRequest().getCookies() != null )
            {
                for (int i = 0; i < ctx.getRequest().getCookies().length; i++) 
                {
                    if( "XeoControl".equals(ctx.getRequest().getCookies()[i].getName() ) )
                    {
                        result = true;
                        break;
                    }
                }
            }
          }
          return result;
      }
}