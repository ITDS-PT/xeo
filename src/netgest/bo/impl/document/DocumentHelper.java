/*Enconding=UTF-8*/
package netgest.bo.impl.document;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.math.BigDecimal;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.dochtml.docHTML;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.boSession;

import netgest.io.FSiFile;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;

import netgest.utils.ngtXMLHandler;

import netgest.bo.system.Logger;

import xeo.client.business.events.ClientEvents;
import xeo.client.business.events.FileSystemEvents;
import xeo.client.business.helper.ServiceHelper;

/**
 * <p>Title: DocumentHelper </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class DocumentHelper extends Ebo_DocumentImpl{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.document.DocumentHelper");

    public static final String CHECK_OUT = "checkOut";
    public static final String CHECK_IN = "checkIn";
    public static final String UNDO_CHECK_OUT = "undoCheckOut";
    public static final String GET_LATEST_VERSION = "getLatestVersion";
    public static final String IFILE_SERVICE_NAME="ngtbo";
    private static ArrayList subClasses = new ArrayList();

    static
    {
        try
        {
            boDefHandler docHandler = boDefHandler.getBoDefinition("Ebo_DocBase");
            boDefHandler defs[] = docHandler.getTreeSubClasses();
            for (int i = 0; i < defs.length; i++)
            {
                if(subClasses.indexOf(defs[i].getName()) < 0)
                {
                    subClasses.add(defs[i].getName());
                }
            }
        }
        catch (Exception e)
        {
            //ignore
        }
    }


    public static void checkOut(boObject object, String bridge)
    {
        long userBoui = 0;
        List iFileList = null;
        try{
            EboContext ctx = object.getEboContext();
            userBoui = ctx.getBoSession().getPerformerBoui();
            DocumentContainer docContainer = new DocumentContainer();
            iFileList = getFileList(ctx,CHECK_OUT ,userBoui);
            commitCheck(iFileList,CHECK_OUT ,userBoui);
            String docName = mergeAttributes(object.getBoDefinition().getCARDID(),object).toString();
            if(iFileList.size() > 1){
                docContainer.setFileTmp(getTempDirHelper() + "\\" + userBoui + "_" + object.getBoui() + ".tmp");
                setCompressedInputStream(ctx,iFileList,docContainer);
                docContainer.setFilename(createFileName(docName,iFileList.size(),getNumberOfDocuments(ctx,object, bridge),".zip"));
                docContainer.setSizeFromTmpFile();
            }else if(iFileList.size() == 1){
                iFile ifile = (iFile)iFileList.get(0);
                docContainer.setInputStream(ifile.getInputStream());
                docContainer.setFilename(ifile.getName());
                docContainer.setSize(ifile.length());
            }
            ctx.getRequest().setAttribute("docContainer",docContainer);
            object.update();
        }catch(Exception e){
            try{
                commitCheck(iFileList,CHECK_IN,userBoui);
            }catch(Exception ex){
                object.addErrorMessage(ex.getMessage());
                ex.printStackTrace();
            }
            object.addErrorMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void checkIn(boObject object,File file, boolean decompress, String bridge) {
        try{
            long userBoui = object.getEboContext().getBoSession().getPerformerBoui();
            if(decompress)
            {
                setUpLoadFiles(object,String.valueOf(userBoui),file, bridge);
            }
            else
            {
                if(isDocument(object.getName()))
                {
                    FileInputStream input = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(input);
                    iFileUpdate(false,bis,object,String.valueOf(userBoui),file.getName(),file, null, null,bridge);
                    bis.close();
                    input.close();
                }
                else
                {
                    setUpLoadFile(object,String.valueOf(userBoui),file,bridge);
                }
            }
        }catch(Exception e){
            object.addErrorMessage(e.getMessage());
        }
        try{
            object.poolSetStateFull();
            object.update();
        }catch (boRuntimeException ex){
                object.addErrorMessage(ex.getMessage());
        }

    }
    public static void getLatestVersion(boObject object, String bridge) {
        try{
            EboContext ctx = object.getEboContext();
            long userBoui = ctx.getBoSession().getPerformerBoui();
            DocumentContainer docContainer = new DocumentContainer();
            List iFileList = iFileList = getFileList(ctx,GET_LATEST_VERSION,userBoui);
            if(iFileList.size() > 1){
                docContainer.setFileTmp(getTempDirHelper() + "\\" + userBoui + "_" + object.getBoui() + ".tmp");
                setCompressedInputStream(ctx,iFileList,docContainer);
                //String docName = object.getAttribute("description").getValueString();
                String docName = mergeAttributes(object.getBoDefinition().getCARDID(),object).toString();
                docContainer.setFilename(createFileName(docName,iFileList.size(),getNumberOfDocuments(ctx,object,bridge),".zip"));
                docContainer.setSizeFromTmpFile();
            }else if(iFileList.size() == 1){
                iFile ifile = (iFile)iFileList.get(0);
                docContainer.setInputStream(ifile.getInputStream());
                docContainer.setFilename(ifile.getName());
                docContainer.setSize(ifile.length());
            }
            ctx.getRequest().setAttribute("docContainer",docContainer);
        }catch(Exception e){
            object.addErrorMessage(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void undoCheckOut(boObject object)
    {
        try{
            EboContext ctx = object.getEboContext();
            long userBoui = ctx.getBoSession().getPerformerBoui();
            List iFileList = getFileList(ctx,UNDO_CHECK_OUT,userBoui);
            commitCheck(iFileList,UNDO_CHECK_OUT,userBoui);
        }catch(Exception e){
            object.addErrorMessage(e.getMessage());
            e.printStackTrace();
        }
    }
    private static List getFileList(EboContext ctx,String method, long userBoui) throws boRuntimeException
    {
        Enumeration oEnum = ctx.getRequest().getParameterNames();
        List iFileList = new ArrayList();
        String att;
        while(oEnum.hasMoreElements())
        {
            att = (String)oEnum.nextElement();
            String[] str = null;
            if(att.startsWith(method)){
                str = att.split("__");
                if(isDocument(str[1])){
                    addFile(ctx, Long.parseLong(str[2]),iFileList,method,userBoui);
                }else{
                    getOtherDocumentsFiles(ctx, Long.parseLong(str[2]),iFileList,method,userBoui);
                }
            }
        }
        return iFileList;
    }
    private static void getOtherDocumentsFiles(EboContext ctx,long boui,List iFileList, String method, long userBoui) throws boRuntimeException
    {
        boObject object = boObject.getBoManager().loadObject(ctx,boui);
        bridgeHandler bHandler = object.getBridge("details");
        bHandler.beforeFirst();
        while ( bHandler.next() )
        {
             boObject objHandler = bHandler.getObject();
             if(objHandler.getBoDefinition().getName().equals(object.getBoDefinition().getName()))
             {
                getOtherDocumentsFiles(ctx,objHandler.getBoui(),iFileList,method,userBoui);
             }
             else
             {
                 addFile(ctx, objHandler.getBoui(),iFileList,method,userBoui);
             }
        }
    }
    private static void addFile(EboContext ctx,long boui,List iFileList, String method, long userBoui)throws boRuntimeException
    {
        iFile file = boObject.getBoManager().loadObject(ctx,boui).getAttribute("file").getValueiFile();
        if(file != null)
        {
            if(CHECK_OUT.equals(method)){
                if(file.isCheckedIn()){
                    if(!contains(iFileList,file)){
                        iFileList.add(file);
                    }
                }
            }else if(CHECK_IN.equals(method)){
                if(file.isCheckedOut() &&  Long.parseLong(file.getCheckOutUser()) == userBoui ){
                    if(!contains(iFileList,file)){
                        iFileList.add(file);
                    }
                }
            } else {
                if(!contains(iFileList,file)){
                    iFileList.add(file);
                }
            }
        }
    }
    private static boolean contains(List iFileList,iFile file)
    {
        boolean result = false;
        iFile ifile;
        for (Iterator objects = iFileList.iterator(); objects.hasNext() ;) {
            ifile = (iFile)objects.next();
            if(ifile.getAbsolutePath().equals(file.getAbsolutePath()))
            {
                return true;
            }

        }
        return result;
    }
    private static void commitCheck(List iFileList,String method, long userBoui) throws iFilePermissionDenied
    {
        iFile file;
        for (Iterator objects = iFileList.iterator(); objects.hasNext() ;) {
            file = (iFile)objects.next();
            if(CHECK_OUT.equals(method)){
                if(file.isCheckedIn()){
                    file.setCheckOutUser(String.valueOf(userBoui));
                    file.checkOut();
                }else{
                    iFileList.remove(file);
                }
            }else{
                if(file.isCheckedOut()){
                    file.setVersionUser(String.valueOf(userBoui));
                    file.checkIn();
                }
            }
        }
    }
    private static long getNumberOfDocuments(EboContext ctx,boObject object , String bridge) throws boRuntimeException
    {
        long count = 0;
        bridgeHandler bHandler = object.getBridge(bridge);
        bHandler.beforeFirst();
        while ( bHandler.next() )
        {
             boObject objHandler = bHandler.getObject();
             if(isDocument(objHandler.getBoDefinition().getName()))
             {
                 count ++;
             }else{
                count += getNumberOfDocuments(ctx,objHandler,"details");
             }
        }
        return count;
    }
    public static List getDocuments(EboContext ctx,boObject object , String method, String bridge) throws boRuntimeException
    {
        //long count = 0;
        List iFileList = new ArrayList();
        bridgeHandler bHandler = object.getBridge(bridge);
        bHandler.beforeFirst();
        while ( bHandler.next() )
        {
             boObject objHandler = bHandler.getObject();
             if(objHandler.exists()){
                 if(isDocument(objHandler.getBoDefinition().getName()))
                 {
                    iFile file = objHandler.getAttribute("file").getValueiFile();
                    if ( file !=null )
                    {
                        if(CHECK_OUT.equals(method)){
                            if(file.isCheckedIn()){
                                //count ++;
                                iFileList.add(objHandler);

                            }
                        } else if(CHECK_IN.equals(method) || UNDO_CHECK_OUT.equals(method)){
                            if(file.isCheckedOut() &&  Long.parseLong(file.getCheckOutUser()) == ctx.getBoSession().getPerformerBoui() ){
                                //count ++;
                                iFileList.add(objHandler);
                            }
                        } else {
                             //count ++;
                             iFileList.add(objHandler);
                        }

                    }
                 } else {
                    //count += getNumberOfDocuments(ctx,objHandler, method, "details");
                    iFileList.addAll(getDocuments(ctx,objHandler, method, "details"));
                 }
             }
        }
        //return count;
        return iFileList;
    }
    private static void setCompressedInputStream(EboContext ctx,List iFileList,DocumentContainer docContainer) throws boRuntimeException,IOException,iFilePermissionDenied
    {
        StringBuffer comments = new StringBuffer();
        String filetmp = docContainer.getFileTmp();
        FileOutputStream f = new FileOutputStream(filetmp);
        CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
        /*ZipOutputStream zos = new ZipOutputStream(csum);
        BufferedOutputStream out =  new BufferedOutputStream(zos);  */
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(csum));
        long tInicial = System.currentTimeMillis();
        long tFinal = 0;
        for(int i = 0; i < iFileList.size(); i++) {
            iFile file = (iFile)iFileList.get(i);
            createComments(ctx,file,comments);
            InputStream in = file.getInputStream();
            out.putNextEntry(new ZipEntry(file.getName()));
            int c;
            while((c = in.read()) != -1)
            {
                out.write(c);
            }
            in.close();
        }
        out.setComment(comments.toString());
        out.close();

        FileInputStream  input = new FileInputStream(filetmp);
        InputStream bufferedIn = new BufferedInputStream( input );
        docContainer.setInputStream(bufferedIn);
        tFinal = System.currentTimeMillis();
        logger.finer("Tempo Total CheckOut: " + (tFinal-tInicial)/1000 +"s" );
    }
    private static void setUpLoadFiles(boObject object,String userBoui,File upLoadFile, String bridge) throws boRuntimeException,IOException,iFilePermissionDenied
    {
        boolean compressed = false;
        FileInputStream fi = new FileInputStream(upLoadFile);
        CheckedInputStream csumi =  new CheckedInputStream(fi, new Adler32());
        ZipInputStream in = new ZipInputStream(csumi);
        BufferedInputStream bis = new BufferedInputStream(in);
        ZipEntry zfile;
        long tInicial = System.currentTimeMillis();
        long tFinal = 0;
        while((zfile = in.getNextEntry()) != null) {
            compressed = true;
            String[] docTree = zfile.getName().split("/");
            if (!zfile.isDirectory())
            {
                if(docTree.length > 1){
                    iFileUpdate(true,bis,object,userBoui,docTree[docTree.length -1],null,zfile,docTree, bridge);
                }else{
                    iFileUpdate(true,bis,object,userBoui,zfile.getName(),null,zfile,docTree, bridge);
                }
            }else
            {
                boObject fatherObject = getDocument(object,docTree,bridge);
                if("Ebo_Folder".equals(fatherObject.getBoDefinition().getName()))
                {
                    if(!fatherObject.getAttribute("description").getValueString().equals(docTree[docTree.length - 1]))
                    {
                        documentCreate(fatherObject,docTree[docTree.length - 1],bridge);
                    }
                }
                else
                {
                    documentCreate(fatherObject,docTree[docTree.length - 1],bridge);
                }
            }
        }
        bis.close();
        fi.close();
        if(!compressed)
        {
           setUpLoadFile(object,userBoui,upLoadFile,bridge);
        }
        tFinal = System.currentTimeMillis();
        logger.finer("Tempo Total CheckIn: " + (tFinal-tInicial)/1000 +"s" );

    }
    private static void setUpLoadFile(boObject object,String userBoui,File upLoadFile, String bridge) throws boRuntimeException,IOException,iFilePermissionDenied
    {
            FileInputStream fi2 = new FileInputStream(upLoadFile);
            BufferedInputStream in2 = new BufferedInputStream(fi2);
            iFileUpdate(true,in2,object,userBoui,upLoadFile.getName(),upLoadFile, null, null,bridge);
            in2.close();
            fi2.close();
    }

    private static void iFileUpdate(boolean document,BufferedInputStream in,boObject object,String userBoui,String fileName, File uploadFile,ZipEntry zfile,String[] dirTree, String bridge) throws boRuntimeException,IOException,iFilePermissionDenied
    {
        BufferedOutputStream out = null;
        iFile file = null;
        if(document){
            file = getFileFromDocument(object,fileName,bridge);
        }else{
            file = object.getAttribute("file").getValueiFile();
        }
        if(file != null){
            if(file.isCheckedOut() && userBoui.equals(file.getCheckOutUser()))
            {
                file.setVersionUser(userBoui);
                file.setBinaryStream( in );
                file.checkIn();
            }
        }
        else // Novo file
        {
            if(zfile == null)
            {
                iFileCreate(object,uploadFile,bridge);
            }
            else
            {
                iFileCreate(getDocument(object,dirTree,bridge),fileCreate(zfile,in),bridge);
            }
        }
    }
    private static iFile getFileFromDocument(boObject object, String fileName, String bridge)throws boRuntimeException
    {
        iFile file = null;
        bridgeHandler bHandler = object.getBridge(bridge);
        bHandler.beforeFirst();
        while ( bHandler.next() )
        {
             boObject objHandler = bHandler.getObject();
             if(isDocument(objHandler.getBoDefinition().getName()))
             {
                 file = objHandler.getAttribute("file").getValueiFile();
                 if(fileName.equals(file.getName()))
                 {
                     return file;
                 }
             }
             else
             {
                file = getFileFromDocument(objHandler,fileName,"details");
                if(file != null) return file;
             }

        }
        return null;
    }
    private static boObject getDocument(boObject object, String[] docTree,String bridge)throws boRuntimeException
    {
        int treeSize = docTree.length;
        int treeIndex = 0;
        bridgeHandler bHandler = object.getBridge(bridge);
        bHandler.beforeFirst();
        while ( bHandler.next() )
        {
             boObject objHandler = bHandler.getObject();
             //if(objHandler.getBoDefinition().getName().equals(object.getBoDefinition().getName()))
             if("Ebo_Folder".equals(objHandler.getBoDefinition().getName()))
             {
                if(objHandler.getAttribute("description").getValueString().equals(docTree[treeIndex]))
                {
                    treeIndex ++;
                    String[] subDocTree = new String[treeSize - 1];
                    System.arraycopy(docTree,treeIndex,subDocTree,0,treeSize - 1);
                    if(subDocTree.length > 0)
                    {
                        return getDocument(objHandler,subDocTree,"details");
                    }
                    else
                    {
                        return objHandler;
                    }
                }
             }
        }
        return object;
    }
    public static long getFolderSize(boObject object, String bridge) throws boRuntimeException
    {
        long size = 0;
        iFile file = null;
        bridgeHandler bHandler = object.getBridge(bridge);
        bHandler.beforeFirst();
        while ( bHandler.next() )
        {
             boObject objHandler = bHandler.getObject();
             if(objHandler.exists()){
                 if(isDocument(objHandler.getBoDefinition().getName()))
                 {
                    file = objHandler.getAttribute("file").getValueiFile();
                    size += file.length();
                 }
                 else
                 {
                    size += getFolderSize(objHandler,"details");
                 }
             }
        }
        return size;
    }
    public static long getNumberOfDocuments(boObject object, String bridge) throws boRuntimeException
    {
        long size = 0;
        bridgeHandler bHandler = object.getBridge(bridge);
        bHandler.beforeFirst();
        while ( bHandler.next() )
        {
             boObject objHandler = bHandler.getObject();
             if(objHandler.exists()){
                 if(isDocument(objHandler.getBoDefinition().getName()))
                 {
                    size ++;
                 }
                 else
                 {
                    size += getNumberOfDocuments(objHandler,"details");
                 }
             }
        }
        return size;
    }
    private static void iFileCreate(boObject object, File uploadFile,String bridge) throws boRuntimeException,iFilePermissionDenied,IOException
    {
        //bridgeHandler bHandler = object.getBridge("details");
        if("Ebo_Folder".equals(object.getBoDefinition().getName()))
        {
            bridge = "details";
        }
        bridgeHandler bHandler = object.getBridge(bridge);
        boObject bo = bHandler.addNewObject("Ebo_Document");
        iFile file = new FSiFile(null,uploadFile,null);
        bo.getAttribute("file").setValueiFile(file);
        bo.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(file.length()));
        bo.getAttribute("fileName").setValueString(file.getName());
        //Será necessário?
        object.poolSetStateFull();
        object.update();
    }
    private static void documentCreate(boObject object, String docName,String bridge) throws boRuntimeException,iFilePermissionDenied,IOException
    {
        if("Ebo_Folder".equals(object.getBoDefinition().getName()))
        {
            bridge = "details";
        }
        bridgeHandler bHandler = object.getBridge(bridge);
        boObject bo = bHandler.addNewObject("Ebo_Folder");
        bo.getAttribute("description").setValueString(docName);
        //Será necessário?
        object.poolSetStateFull();
        object.update();
    }
    private static File fileCreate(ZipEntry zEntry,BufferedInputStream in) throws boRuntimeException,IOException
    {
        String[] docTree = zEntry.getName().split("/");
        String fileName = docTree[docTree.length - 1];
        File file = new File(getTempDirHelper()+ "\\" + fileName);
        file.setLastModified(zEntry.getTime());
        int x;
        // write the current file to disk
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream dest = new BufferedOutputStream(fos);
        // read and write until last byte is encountered
        while((x = in.read()) != -1){
            dest.write(x);
        }
        dest.flush();
        dest.close();
        return file;
    }
    private static void createComments( EboContext ctx, iFile file, StringBuffer sb)
    {
        try{
            sb.append("file : " + file.getName() + "\n");
            sb.append("version : " + file.getVersion() + "\n");
            sb.append("version user : " + boObject.getBoManager().loadObject(ctx,Long.parseLong(file.getVersionUser())).getAttribute("name").getValueString() + "\n");
            sb.append("\n");
        }catch (boRuntimeException e){
        }
    }
    /**
     * Constroi o nome do ficheiro comprimido para download.
     *
     * @param description Nome Identificativo do Objecto.
     * @param fnumber Número de ficheiros incluidos no ficheiro.
     * @param ftotal Número total de ficheiros que o documento tem.
     * @param ext Extensão do ficheiro.
     * @return  fileName, nome do ficheiro.
     * @throws boRuntimeException
     */
    private static String createFileName(String description, long fnumber, long ftotal, String ext) throws boRuntimeException
    {
        StringBuffer fileName = new StringBuffer();
        fileName.append(description);
        fileName.append("_").append((fnumber == ftotal) ? "full" : "partial");
        fileName.append("_").append(fnumber).append("(").append(ftotal).append(")");
        fileName.append(ext);
        return fileName.toString();
    }

    private static void renderMenuHeader(StringBuffer out,EboContext boctx,boObjectList detailList,PageContext pageContext,boObject object,String bridge,boolean active) throws boRuntimeException
    {
        long docBoui = object.getBoui();
        out.append("   <TABLE cellSpacing='0' cellPadding='0' style='height:100%;width:100%;table-layout:fixed'><!-- BEGIN MENU -->");
        out.append("	<TR height='24'><TD>");
        out.append("	<table style='z-Index:1000' class='layout' cellSpacing='0' cellPadding='0'>");
        out.append("      <tbody>");
        out.append("	  <tr height='24'>");
        out.append("	  <td>");
        out.append("	    <table class='mnubarFlat' id='mnuBar1' cellSpacing='0' cellPadding='0'>");
        out.append("	    <tbody>");
        out.append("	       <tr>");
        out.append("          <td width='9'><img hspace='3' src='templates/menu/std/mnu_vSpacer.gif' WIDTH='5' HEIGHT='18'/></td>");
        out.append("          <td class='icMenu' noWrap='1'>");
        out.append("          <span class='menu' tabIndex='0' accessKey='A'  menu='SUBMENU_ACCOES'  ><u>A</u>cções<table class='mnuList' id='SUBMENU_ACCOES' cellSpacing='0' cellPadding='3'>");
        out.append("          <colgroup/>");
        out.append("          <col class='mnuLeft'/>");
        out.append("          <col/><tbody>");
        out.append("          <tr tabIndex='0'  onclick=\"deleteSelected(16796536)\"  >");
        out.append("            <td>&nbsp;</td>");
        out.append("            <td class='mnuItm'>Apagar Selecção</td>");
        out.append("          </tr>          ");
        if(!detailList.isEmpty() &&  active){
            if(DocumentHelper.getDocuments(boctx,object,GET_LATEST_VERSION,bridge).size() > 0) {
                out.append("              <tr tabIndex='0'  onclick=\"parent.downloadDocument(16796536,'getLatestVersion','"+ bridge +"')\"  >");
                out.append("                <td>&nbsp;</td>");
                out.append("                <td class='mnuItm'>Última Versão</td>");
                out.append("              </tr>                 ");
            }
            if(DocumentHelper.getDocuments(boctx,object,CHECK_OUT,bridge).size() > 0) {
                out.append("              <tr tabIndex='0'  onclick=\"parent.downloadDocument(16796536,'checkOut','"+bridge+"')\"  >                    ");
                out.append("                    <td>&nbsp;</td>");
                out.append("                    <td class='mnuItm'>Check Out</td>");
                out.append("              </tr>                        ");
            }
            if(DocumentHelper.getDocuments(boctx,object,CHECK_IN,bridge).size() > 0) {
                out.append("                <tr tabIndex='0'  onclick=\"markChecked(16796536,'checkIn')\"  >                    ");
                out.append("                    <td>&nbsp;</td>");
                out.append("                    <td class='mnuItm'>Check In</td>");
                out.append("                </tr>                        ");
                out.append("                <tr tabIndex='0'  onclick=\"markChecked(16796536,'undoCheckOut')\"  >                                                     ");
                out.append("                    <td>&nbsp;</td>");
                out.append("                    <td class='mnuItm'>Undo Check Out</td>");
                out.append("                </tr>                        ");
            }
        }
        out.append("          </tbody></table>");
        out.append("          </span>");
        out.append("          <span class='menu' tabIndex='0' accessKey='C'  menu='SUBMENU_CRIAR'  ><u>C</u>riar");
        out.append("            <table class='mnuList' id='SUBMENU_CRIAR' cellSpacing='0' cellPadding='3'><colgroup/><col class='mnuLeft'/><col/><tbody>");
        out.append("              <tr tabIndex='0'  onclick=\"winmain().newPage(getIDX(),'ebo_document','edit','method=new&relatedClientIDX='+getIDX()+'&ctxParentIdx='+getDocId()+'&docid='+getDocId()+'&ctxParent="+docBoui+"&object=Ebo_Document&addToCtxParentBridge="+bridge+"');\"  ><td>&nbsp;</td><td class='mnuItm'>Documento</td></tr>          ");
        out.append("              <tr tabIndex='0'  onclick=\"winmain().newPage(getIDX(),'ebo_folder','edit','method=new&relatedClientIDX='+getIDX()+'&ctxParentIdx='+getDocId()+'&docid='+getDocId()+'&ctxParent="+docBoui+"&object=Ebo_Folder&addToCtxParentBridge="+bridge+"');\"  ><td>&nbsp;</td><td class='mnuItm'>Directório</td></tr>");
        out.append("          </tbody></table>");
        out.append("          </span>");
        out.append("          <span class='menu' tabIndex='0' accessKey='D'  menu='SUBMENU_CRIAR' onclick=\"winmain().newPage(getIDX(),'ebo_document','edit','method=new&relatedClientIDX='+getIDX()+'&docid='+getDocId()+'&ctxParentIdx='+getDocId()+'&ctxParent="+docBoui+"&object=Ebo_Document&addToCtxParentBridge="+bridge+"');\" >Criar <u>D</u>ocumento </span>");
        out.append("          </td>");
        pageContext.setAttribute("otherJsp","ebo_folder_generallist_geral.jsp");
        out.append("          <TD class='icMenu mnuRight' noWrap><SPAN class=menu title='LookupObjects(  , multi , boObject , "+object.getName()+" , "+ docBoui +" , "+ bridge +" , 1 )' onclick=\"LookupObjects('','multi','boObject','"+object.getName()+"','"+ docBoui +"','"+ bridge +"','1')\" tabindex='0' ><IMG class=mnuBtn src='resources/boObject/ico16.gif'>Adicionar </SPAN></TD>");
        out.append("          <td class='mnuTitle mnuRight' id='tdTitle' noWrap='1'></td></tr>");
        out.append("      </tbody></table><div class='barInterval'></div></td></tr></tbody>");
        out.append("      </table></TD></TR> <!--END MENU -->");
    }
    private static void renderMenuFooter(StringBuffer out,String bridge,boolean glv,boolean cout,boolean cin)  throws boRuntimeException
    {
        out.append("<!-- Document Controls Begin -->");
     //   out.append("<TR height='24'><TD colspan="+(size+5+2) +">");
        out.append("<table style='z-Index:1000' class='layout' cellSpacing='0' cellPadding='0' >");
        out.append("<tbody><tr height='24'><td>");
        out.append("<table style=\"border:0px ;solid: #CCCCCC;align:absmiddle;width:100%;\" bgcolor=#ffffff>");
        out.append("<tr>");
        if(glv) {
            out.append("<td><button onclick=\"parent.downloadDocument(16796536,'getLatestVersion','"+ bridge +"')\">Última Versão</button></td>");
        }
        if(cout) {
            out.append("<td><button onclick=\"parent.downloadDocument(16796536,'checkOut','"+ bridge + "')\">Check Out</button></td>");
        }
        if(cin) {
                out.append("<td><button onclick=\"markChecked(16796536,'checkIn')\">Check In</button></td>");
                out.append("<td><button onclick=\"markChecked(16796536,'undoCheckOut')\">Undo Check Out</button></td>");
        }
        out.append("<td style='width:100%'></td>");
        out.append("</tr>");
        out.append("</table>");
        out.append("</td></tr></tbody></table>");
        // out.append("</TD></TR>");
        out.append("<!-- Document Controls End -->");
    }
    private static String getAttributeValue(boObject object, String attrName) throws boRuntimeException
    {
        String value;
        String tmp = "";
        AttributeHandler attrHandler = object.getAttribute(attrName);
        if(attrHandler.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
        {
            if(attrHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1){
                tmp = attrHandler.getObject().getCARDID().toString();
            }
        }
        else
        {
            tmp = attrHandler.getValueString();
        }
        if(!"".equals(tmp))
        {
            value = tmp;
        }
        else
        {
            value="&nbsp;";
        }
        return value;
    }
    private static void renderColumnSpan(StringBuffer out, ArrayList columns) throws boRuntimeException,IOException
    {
        DocumentContainer.GridDefinition definition = null;

        definition = (DocumentContainer.GridDefinition)columns.get(0);
        out.append("<COL width='2'/>");
        out.append("<COL width='").append( Integer.parseInt(definition.getSize())-2 ).append("'/>");
       out.append("<COL />");
        for(int i=1; i < columns.size() ; i++)
        {
            definition = (DocumentContainer.GridDefinition)columns.get(i);
            out.append("<COL width='2'/>");
            out.append("<COL width='").append(definition.getSize()).append("'/>");
            //out.append("<COL />");
        }


    }
    private static void renderColumnsWidth(StringBuffer out, ArrayList columns) throws boRuntimeException,IOException
    {
        DocumentContainer.GridDefinition definition = null;


        for(int i=1; i < columns.size() ; i++)
        {
            definition = (DocumentContainer.GridDefinition)columns.get(i);
            out.append("<COL width='").append(definition.getSize()).append("'/>");
            //out.append("<COL />");
        }


    }
    private static void renderColumnHeader(StringBuffer out, ArrayList columns) throws boRuntimeException,IOException
    {
        DocumentContainer.GridDefinition definition = null;
        definition = (DocumentContainer.GridDefinition)columns.get(0);
        out.append("<TD id='g5953458_ExpanderParent' colspan = 2 class='ghSort_std'>");
        out.append(definition.getAttrLabel());
        out.append("</TD>");
        out.append("<TD class='ghSep_std' >&nbsp;</TD>");

        for(int i=1; i < columns.size() ; i++)
        {
            definition = (DocumentContainer.GridDefinition)columns.get(i);
            out.append("<TD class='gh_std'>");
            out.append(definition.getAttrLabel());
            out.append("</TD>");
            out.append("<TD class='ghSep_std' >&nbsp;</TD>");
        }
    }
    private static void renderColumnValues(StringBuffer out, boObject object, ArrayList columns) throws boRuntimeException,IOException
    {
        DocumentContainer.GridDefinition definition = null;
        for(int i=0; i < columns.size() ; i++)
        {
            if(isDocument(object.getName()))
            {
                definition = (DocumentContainer.GridDefinition)columns.get(i);
                out.append("<TD class='gCell_std'>");
                out.append(getAttributeValue(object,definition.getAttrName()));
                out.append("</TD>");
            }
            else
            {
                if(i == 0)
                {
                    out.append("<TD class='gCell_std'>");
                    out.append(object.getAttribute("description").getValueString());
                    out.append("</TD>");
                }
                else
                {
                    out.append("\n<TD class='gCell_std'>&nbsp;</TD>");
                }
            }
        }
    }
    public static void renderGrid(PageContext pageContext,boObject obj,docHTML doc,String bridge,String active) throws boRuntimeException,IOException
    {
        boolean docActive = ("true".equalsIgnoreCase(active)) ? true : false;
        EboContext boctx = obj.getEboContext();
        StringBuffer out = new StringBuffer();
//        long docBoui = obj.getBoui();
        boObjectList detailList = obj.getBridge(bridge);

        boolean glv = DocumentHelper.getDocuments(boctx,obj,GET_LATEST_VERSION,bridge).size() > 0 ? true : false;
        boolean cout = DocumentHelper.getDocuments(boctx,obj,CHECK_OUT,bridge).size() > 0 ? true : false;
        boolean cin = DocumentHelper.getDocuments(boctx,obj,CHECK_IN,bridge).size() > 0 ? true : false;

        // Menu Header da Tabela
        renderMenuHeader(out,boctx,detailList,pageContext,obj,bridge,docActive);

        ArrayList columns = (ArrayList)getColumnDefinition();

        out.append("	<TR>");
        out.append("	<TD style='height:100%;width:100%' >");
        if(!detailList.isEmpty() && docActive && (glv || cout || cin))
        {
            out.append("	<table CLASS='layout' cellSpacing='0' cellPadding='0'><tr style='height:100%'><td>");
        }

        out.append("	<DIV style='width:100%;height:100%;overflow-x:auto'>");


        out.append("	<TABLE style='height:100%;width:100%;' class='g_std' cellSpacing='0' cellPadding='0' width='100%'>");
        out.append("	<TBODY>");
        out.append("	<TR height='20'>");
        out.append("	<TD >");
        out.append("	<TABLE id='g5953458_body' onmouseover=\"so('g5953458');onOver_GridHeader_std(event);\" onmouseout=\"so('g5953458');onOut_GridHeader_std(event);\" onclick=\"so('g5953458');onClick_GridHeader_std(event);\" cellpadding='2' cellspacing='0' style=\"height:25px\" class='gh_std'>");
        out.append("	<COLGROUP/>");

        out.append("	<COL width='25'/>");
        out.append("	<COL width='20'/>");
        out.append("	<COL width='20'/>");
        out.append("	<COL width='20'/>");
        out.append("	<COL width='18'/>");

        renderColumnSpan(out,columns);
        if(docActive)
        {
            out.append("<COL width='2'/>");
            out.append("<COL width='").append("48").append("'/>");
            out.append("<COL width='2'/>");
            out.append("<COL width='").append("98").append("'/>");

        }
        out.append("<COL width='2'/>");
        out.append("<COL width='15'/>");

        out.append("	<TBODY>");
        out.append("    <TR> ");

        //init mode template
         boObject objParent = obj;

            if( objParent!=null && objParent.getMode() == boObject.MODE_EDIT_TEMPLATE  )
                {
                // && xattributes.get("noRenderTemplate")==null){
                StringBuffer toPrint = new StringBuffer();
                toPrint.append("<img onclick=\"openAttributeMap('");
                String atrName = bridge;
                StringBuffer nameH = new StringBuffer();
                nameH.append( objParent.getName() ).append( "__" ).append( objParent.bo_boui ).append("__").append( atrName );


                toPrint.append( nameH );
                toPrint.append( "','" );
                toPrint.append( doc.getDocIdx() );
                toPrint.append( "','" );
                toPrint.append( objParent.getAttribute("TEMPLATE").getValueString() );
                toPrint.append("')\" src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
                out.append("<TD class='gh_std' >");
                out.append( toPrint.toString() );
                out.append("</TD>");
              }
              else
              {
                out.append("      <TD class='gh_std' >&nbsp;</TD>");
              }
        ///fim do modo template

        out.append("      <TD class='gh_std'><INPUT id='g16796536_check' class='rad' type='checkBox'/></TD>");
        out.append("      <TD class='gh_std' >&nbsp;</TD> ");
        out.append("      <TD class='gh_std' >&nbsp;</TD>");
        out.append("      <TD class='gh_std' >&nbsp;</TD> ");
        out.append("      <TD class='ghSep_std' >&nbsp;</TD>");

        renderColumnHeader(out,columns);
        if(docActive)
        {
            out.append("<TD class='gh_std'>");
            out.append("Versão");
            out.append("</TD>");
            out.append("<TD class='ghSep_std' >&nbsp;</TD>");
            out.append("<TD class='gh_std'>");
            out.append("&nbsp;");
            out.append("</TD>");
            out.append("<TD class='ghSep_std' >&nbsp;</TD>");
        }

        out.append("\n  <TD class='gh_std' width='14'><img onclick='reloadGrid();' title='Clique aqui para actualizar a lista' src='templates/grid/std/ghRefresh.gif' width='13' height='13' /></TD>");

        out.append("  </TR>");
        out.append("  </TBODY>");
        out.append("  </TABLE></TD></TR>");
        out.append("  <TR><TD><DIV id='g16796536_divc' class='gContainerLines_std'>  ");
        if(!detailList.isEmpty()){
            out.append("  <TABLE id='g16796536_body' container='1' onmouseover=\"so('g16796536');onOver_GridBody_std(event)\" onmouseout=\"so('g16796536');onOut_GridBody_std(event)\" ondblclick=\"so('g16796536');onDoubleClick_GridBody(event)\" onclick=\"so('g16796536');onClick_GridBody_std(event)\" cellpadding='2' cellspacing='0'  options=''  mode='normal'  letter_field='null'  class='gBodyLines_std'>");
        } else {
            out.append("  <TABLE id='g16796536_body' container='1' style='height:100%' onmouseover=\"so('g16796536');onOver_GridBody_std(event)\" onmouseout=\"so('g16796536');onOut_GridBody_std(event)\" onclick=\"so('g16796536');onClick_GridBody_std(event)\" cellpadding='2' cellspacing='0'  options=''  mode='normal'  letter_field='null'  class='gBodyLines_std'>");
        }
        out.append("  <COLGROUP/><COL width='25'/><COL width='20'/><COL width='20'/><COL width='20'/><COL width='20'/><COL />");

        renderColumnsWidth(out,columns);
         if(docActive)
        {

            out.append("<COL width='").append("50").append("'/>");
            out.append("<COL width='").append("100").append("'/>");

        }
        boObject object = null;
        if(!detailList.isEmpty()){
            int i = 1;
            detailList.beforeFirst();
            while ( detailList.next() ) {

                object = detailList.getObject();

                out.append("  <TR  "+( !obj.exists()?" exists=no ":"") +" ondragleave='grid_DragLeave()' ondrop='grid_Drop()' ondragover='grid_DragOver()' ondragenter='grid_DragEnter()' id='16796536__"+ object.getName() +"__"+ object.getBoui() +"'>");
                out.append("    <TD style='' class='gCellNumber_std'><img width=18 title='Seleccione a linha e depois clique arraste para mover a linha'  class=\"numberLine\" ondragstart=\"grid_StartMoveLine()\"  height=16 lin="+ i +" src=resources/numbers/"+ i++ +".gif></TD>");
                out.append("    <TD class='gCell_std'><INPUT class='rad' type='checkBox' name='"+ object.getName() +"__"+ object.getBoui() +"'/></TD>");
                out.append("    <TD class='gCell_std'><IMG src='templates/grid/std/quickview.gif' width='13' height='13'/></TD>");
                out.append("    <TD class='gCell_std'><IMG title='Imagem representativa do objecto Ficheiro' src='resources/"+ object.getName() +"/ico16.gif' ondragstart=\"startDragObject( '"+ object.getName() +"',"+ object.getBoui() +","+ object.exists() +",16796536 )\"  height='16' width='16'/></TD>");
                out.append("    <TD class='gCell_std'><IMG src='resources/none.gif' height=16 width=16 /></TD>");
                renderColumnValues(out,object,columns);

                String objName = object.getName();

                if(docActive)
                {
                    if(isDocument(objName))
                    {
                        iFile ifile = object.getAttribute("file").getValueiFile();
                        if(ifile != null && ifile.getAbsolutePath().startsWith("/")) {
                            out.append("<TD  class='gCell_std'>"+ ifile.getVersion() +"</TD>");
                        } else {
                            out.append("<TD  class='gCell_std'>&nbsp;</TD>");
                        }
                    }
                    else
                    {
                        out.append("<TD  class='gCell_std'>&nbsp;</TD>");
                    }

                    if(isDocument(objName))
                    {
                        iFile ifile = object.getAttribute("file").getValueiFile();
                        if(ifile != null && ifile.isCheckedOut()){
                            out.append("<TD class='gCell_std'>"+ boObject.getBoManager().loadObject(boctx,Long.parseLong(ifile.getCheckOutUser())).getCARDID()+" </TD>");
                        } else {
                            out.append("<TD class='gCell_std'>&nbsp;</TD>");
                        }
                    }
                    else
                    {
                        out.append("<TD class='gCell_std'>&nbsp;</TD>");
                    }
                }
                out.append("</TR>");
            }

        } else {
            int colspan = columns.size()+5;
            if(docActive) colspan += 2;
            out.append("      <TR ondragleave='grid_DragLeave()' ondrop='grid_Drop()' ondragover='grid_DragOver()' ondragenter='grid_DragEnter()' id='16796536__"+ obj.getName() +"__"+ obj.getBoui() +"' exists=no>");
            out.append("      <TD select='none' COLSPAN='"+colspan+"'>");
            out.append("      <TABLE id='g16796536' select='none' style='height:100%;width:100%;border:0px' morerecords='0'>");
            out.append("        <TBODY>");
            out.append("        <TR>                  ");
            out.append("          <TD select='none' style='COLOR: #999999; BORDER:0px' align=middle width='100%' >Não existem objectos do tipo Documento ou Directoria com os parametros actuais</TD>");
            out.append("        </TR>");
            out.append("        </TBODY>");
            out.append("      </TABLE>");
            out.append("      </TD>");
            out.append("      </TR> ");
        }
        out.append("  </TABLE> ");
        out.append("  </DIV></TD></TR>");
        out.append("  </TBODY>");

        out.append("</TABLE>");
        out.append("</DIV>");

        if(!detailList.isEmpty() && docActive && (glv || cout || cin))
        {
            out.append("	</td></tr>");
            out.append("<tr style=height='24px'><td>");
            // Menu Footer da Tabela
            renderMenuFooter(out,bridge,glv,cout,cin );
            out.append("</td></tr></table>") ;
        }


        out.append("</TD></TR>");
        out.append("<script>window.recs=1;window.onload=actNumberOfArea</script>");
        out.append("</TABLE>");

        JspWriter realOutput = pageContext.getOut();
        realOutput.print(out.toString());
    }
    private static List getColumnDefinition() throws boRuntimeException{
        DocumentContainer container = new DocumentContainer();
//        String attrName = null;
        DocumentContainer.GridDefinition gdef = null;
        boDefHandler bDefHandler = boDefHandler.getBoDefinition("Ebo_Document");
        ngtXMLHandler[] columns = bDefHandler.getViewer("general").getForm("list").getChildNode("grid").getChildNode("cols").getChildNodes();
        List definition = new ArrayList(columns.length);
        for (int i = 0; i < columns.length; i++)
        {
            gdef = container.getGridDefinition();
            gdef.setSize(columns[i].getAttribute("width"));
            gdef.setAttrName(columns[i].getChildNode("attribute").getText());
            gdef.setAttrLabel(bDefHandler.getAttributeRef(gdef.getAttrName()).getLabel());
            definition.add(gdef);
        }
        return definition;
    }
    /*  apartir daqui */
    public static String getFileMimeType(EboContext ctx, String fileName)
    {
        return ctx.getPageContext().getServletContext().getMimeType(fileName.toLowerCase());
    }
    public static boolean isMSWordFile(EboContext ctx, String fileName)
    {
        boolean result = false;
        String mimeType = getFileMimeType(ctx,fileName);
        if(mimeType != null)
        {
            if("application/msword".equals(mimeType) ||
               "application/rtf".equals(mimeType))
            {
                result = true;
            }
        }
        else
        {
            if(fileName.endsWith(".dot"))
            {
                result = true;
            }
        }
        return result;
    }
    public static boolean isMSWordFile(boObject document) throws boRuntimeException
    {
        boolean result = false;
        String objClassName = null;
        if(document != null)
        {
            objClassName = document.getName();
//            if("Ebo_DocBase".equals(objClassName) ||
//               "Ebo_Document".equals(objClassName) ||
//               "Ebo_DocumentTemplate".equals(objClassName) ||
//               "Ebo_WordTemplate".equals(objClassName))
            if(isDocument(objClassName))
            {
                if(document.getAttribute("file").getValueiFile() != null)
                {
                    result = isMSWordFile(document.getEboContext(), document.getAttribute("file").getValueiFile().getName());
                }
            }
        }
        return result;
    }
    public static boolean isMSWordFile(EboContext ctx, long boui) throws boRuntimeException
    {
        boolean result = false;
        if(boui != 0 && boui != -1)
        {
            result = isMSWordFile(boObject.getBoManager().loadObject(ctx,boui));
        }
        return result;
    }
    public final static boolean isTemplate(String objClassName)
    {
        boolean result = false;
        if( "Ebo_DocumentTemplate".equals(objClassName) ||
            "Ebo_WordTemplate".equals(objClassName))
        {
            result = true;
        }
        return result;

    }
    public final static boolean isDocument(String objClassName)
    {
        boolean result = false;
        if( subClasses.contains(objClassName) ||
            isTemplate(objClassName))
        {
            result = true;
        }
        else
        {
            if(subClasses.indexOf(objClassName)>-1)
            {
                return true;
            }
        }
        return result;
    }
    public final static boolean isIFile(boObject object) throws boRuntimeException
    {
        boolean result = false;
        Object ifile = object.getAttribute("file").getValueObject();
        if(ifile != null)
        {
            result = true;
        }
        return result;
    }
    public final static boolean existsDocuments(boObject object)  throws boRuntimeException
    {
        boolean result = false;
        String objClassName = null;
        boObject attObject = null;
        AttributeHandler attHandler = null;
//        iFile ifile = null;
        Enumeration oEnum = object.getAttributes().elements();
        while( oEnum.hasMoreElements() && !result )
        {
            attHandler = (AttributeHandler)oEnum.nextElement();
            if(attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_OBJECTATTRIBUTE)
            {

                if(attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1)
                {
                        attObject = attHandler.getObject();
                        if(attObject != null)
                        {
                            objClassName = attObject.getName();
                            if(isDocument(objClassName) && isIFile(attObject))
                            {
                                result = true;
                            }
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
                            objClassName = attObject.getName();
                            if(isDocument(objClassName) && isIFile(attObject))
                            {
                                result = true;
                            }
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
                        objClassName = attObject.getName();
                        if(isDocument(objClassName) && isIFile(attObject))
                        {
                            result = true;
                        }
                    }
                }
            }
        }
        return result;
    }
    public final static List getAllObjectDocuments(EboContext ctx, long boui) throws boRuntimeException
    {
        return getAllObjectDocuments(boObject.getBoManager().loadObject(ctx,boui));
    }
    public final static List getAllObjectDocuments(boObject object) throws boRuntimeException
    {
        List list = new ArrayList();
        if( true ) return list;
//        iFile ifile = null;
        String objClassName = null;
        boObject attObject = null;
        AttributeHandler attHandler = null;
        Enumeration oEnum = object.getAttributes().elements();
//        boolean isMessage = "message".equals(object.getName()) || "message".equals(object.getBoDefinition().getBoSuperBo());
        while( oEnum.hasMoreElements()  )
        {
            attHandler = (AttributeHandler)oEnum.nextElement();
            if(attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_OBJECTATTRIBUTE)
            {
                if(attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1)
                {
                    attObject = attHandler.getObject();
                    if(attObject != null)
                    {
                        objClassName = attObject.getName();
                        if(isDocument(objClassName) && isIFile(attObject))
                        {
                            list.add(attObject);
                        }
                    }
                }
                else if(attHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_MULTI_VALUES)
                {
                    boObject[] attBoObjects = attHandler.getObjects();
                    if(attBoObjects != null)
                    {
                        for(int i = 0 ; i < attBoObjects.length ; i++)
                        {
                            try
                            {
                                attObject = attBoObjects[i];
                                objClassName = attObject.getName();
                                if(isDocument(objClassName) && isIFile(attObject))
                                {
                                    list.add(attObject);
                                }
                            }
                            catch (Exception e)
                            {
                                //ignora e passa a frente
                            }
                        }
                    }
                }
                else
                {
                    bridgeHandler bridge = object.getBridge(attHandler.getName());
                    bridge.beforeFirst();
                    while(bridge.next())
                    {
                        try
                        {
                            attObject = bridge.getObject();
                            objClassName = attObject.getName();
                            if(isDocument(objClassName) && isIFile(attObject))
                            {
                                list.add(attObject);
                            }
                        }
                        catch (Exception e)
                        {
                            //ignora e passa a frente
                        }
                    }
                }
            }
        }
        return list;
    }


    /* não está a ser utilizado, agora está no boObject.onAfterDestroy
    public void destroyBindData() throws boRuntimeException
    {
        iFile file = this.getAttribute("file").getValueiFile();
        if( file != null )
        {
            try
            {
                file.delete();
                file.getParentFile().delete();
            }
            catch (iFilePermissionDenied e)
            {
                logger.warn( e );
            }
        }
    }

    */


    public static boolean canClientOpenExtension(EboContext ctx, long documentBoui ) throws boRuntimeException
    {
        boolean result = false;
        try
        {
            if(documentBoui > 0)
            {
                boObject document = boObject.getBoManager().loadObject(ctx,documentBoui);
                if(document != null && document.exists() && document.getAttribute("file") != null)
                {
                    String fileName = null;
                    if(document.getAttribute("file").getValueiFile() != null)
                    {
                        fileName = document.getAttribute("file").getValueiFile().getName();
                    }
                    if(fileName != null && !"".equals(fileName))
                    {
                        String[] values = fileName.split("\\.");
                        if(values.length == 2)
                        {
                            InitialContext context = new InitialContext();
                            String xeoWin32ClientAddress = (String)ctx.getRequest().getSession().getAttribute("XeoWin32Client_address");
                            FileSystemEvents fs = ServiceHelper.getFileSystem(context,xeoWin32ClientAddress);
                            result = fs.hasExtension(values[1]);
                        }
                    }
                }
            }
        }
        catch (NamingException e)
        {
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static void open(boObject object) throws boRuntimeException
    {
        try
        {
            String xeoWin32ClientAddress = (String)object.getEboContext().getRequest().getSession().getAttribute("XeoWin32Client_address");
            if(xeoWin32ClientAddress != null)
            {
                boSession bosession = (boSession)object.getEboContext().getRequest().getSession().getAttribute("boSession");
                ClientEvents client = ServiceHelper.getClient(new InitialContext(),xeoWin32ClientAddress);
                if(object.getAttribute("file") != null)
                {
                    iFile file = object.getAttribute("file").getValueiFile();
                    client.open(bosession.getId(),file.getName(),object.getBoui());
                }
            }
        }
        catch (NamingException e)
        {
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    public static void print(boObject object) throws boRuntimeException
    {
        try
        {
            String xeoWin32ClientAddress = (String)object.getEboContext().getRequest().getSession().getAttribute("XeoWin32Client_address");
            if(xeoWin32ClientAddress != null)
            {
                boSession bosession = (boSession)object.getEboContext().getRequest().getSession().getAttribute("boSession");
                ClientEvents client = ServiceHelper.getClient(new InitialContext(),xeoWin32ClientAddress);
                if(object.getAttribute("file") != null)
                {
                    iFile file = object.getAttribute("file").getValueiFile();
                    client.print(bosession.getId(),file.getName(),object.getBoui());
                }
            }
        }
        catch (NamingException e)
        {
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    public static void print( boObject parent, String attr) throws boRuntimeException
    {
        if(parent.getAttribute(attr).isBridge())
        {
            boBridgeIterator bit = parent.getBridge(attr).iterator();
            bit.beforeFirst();
            while(bit.next())
            {
                print(bit.currentRow().getObject());
            }
        }
        else
        {
            boObject object = boObject.getBoManager().loadObject(parent.getEboContext(),parent.getAttribute(attr).getValueLong());
            print(object);
        }
    }

    public static void openDocumentInClient( boObject parent , String documentBoui ) throws boRuntimeException
    {
        boObject document = boObject.getBoManager().loadObject(parent.getEboContext(),Long.parseLong(documentBoui));
        open(document);
    }
        /*     Esta no plugin!
    public static void openDocumentInClient( boObject document ) throws boRuntimeException
    {
        if(document != null)
        {
            if(isMSWordFile(document))
            {
                TemplateHelper.editWordDocumentInClient(document);
            }
            else
            {
                openFileInClient(document);
            }
        }
    }
    private static void openFileInClient( boObject document ) throws boRuntimeException
    {
        try
        {
            String fileName = null;
            InitialContext context = new InitialContext();
            boSession bosession = (boSession)document.getEboContext().getRequest().getSession().getAttribute("boSession");
            String xeoWin32ClientAddress = (String)document.getEboContext().getRequest().getSession().getAttribute("XeoWin32Client_address");
            long timestamp = System.currentTimeMillis();

            FileSystemEvents fs = RegistryHelper.getFileSystem(context,xeoWin32ClientAddress);
            if(document.getAttribute("file").getValueiFile() != null)
            {
                fileName = document.getAttribute("file").getValueiFile().getName();
            }
            boClientRemote remote = RegistryHelper.getClientRemote(context);
            remote.getDocument(bosession.getId() ,xeoWin32ClientAddress , String.valueOf(document.getBoui()),timestamp );
            fs.open(fileName,String.valueOf(document.getBoui()),timestamp);
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
    }
    */
    public static String getTempDir()
    {
        return getTempDirHelper();
    }
    public static String getTempDirHelper()
    {
        String stmpdir=System.getProperty("java.io.tmpdir","./tmp/");

        char lastchar=stmpdir.charAt( stmpdir.length()-1  );
        if ( lastchar!='/' && lastchar!='\\' )
        {
            stmpdir+='/';
        }
            stmpdir=stmpdir+"ngtbo"+java.io.File.separator;

        java.io.File tmpdir = new java.io.File(stmpdir);
        if(!tmpdir.exists()) {
            tmpdir.mkdirs();
        }
        return tmpdir.getAbsolutePath();
    }

}