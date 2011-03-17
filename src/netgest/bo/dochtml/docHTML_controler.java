/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boMemoryArchive;
import netgest.bo.system.boPoolManager;
import netgest.bo.system.boPoolable;

import netgest.utils.ClassUtils;
import netgest.bo.system.Logger;

public final class docHTML_controler extends boPoolable {

    //logger
    private static final Logger logger = Logger.getLogger("netgest.bo.dochtml.docHTML_controler");
    
    private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    private Hashtable  p_ndl;
  //  private Hashtable  p_ndk;
	private EboContext p_boctx;
//    private docHTMLerrorHandler p_errorList=new docHTMLerrorHandler();

    //private Hashtable p_didx_querys=new Hashtable();
    
    private Hashtable  vuiControl;
    
    public Hashtable    closedDocs = new Hashtable();
    
    private static int p_nddcounter=0;
        
    public  int  vui=0; //HTML OBJECT SEQUENCE ID
    public  int  cvui=0; //HTML OBJECT SEQUENCE ID
    public  int  tabindex=1;
    public  int  countFields=1;
    
    public docHTML_controler() {
        super(null);
		p_ndl=new Hashtable();
      //  p_ndk=new Hashtable();
        vuiControl=new Hashtable();
        
    }

    public void closeDocIds(EboContext ctx, String[] docids)
    {
        int  docid       = 0;
        long currentTime = 0;
        
        ClosedDoc closeddoc = null;
        docHTML   doc       = null;
        
        
        currentTime = System.currentTimeMillis();
        
        for(int i=0; i < docids.length; i++) 
        {
            docid     = Integer.parseInt(docids[i]);
            closeddoc = (ClosedDoc)closedDocs.get(new Integer(docid));
            
            if(closeddoc == null)
            {
                //this.logger.finest("CLOSE_DOC: 01 docHTML with IDX " + String.valueOf(docid) + " will be marked as CLOSED.");
                
                doc = getDocByIDX(docid, ctx);
                
                if(doc != null)
                {
                    closeddoc = new ClosedDoc();
                    
                    closeddoc.dateToClose     = currentTime;
                    closeddoc.docIdx          = docid;
                    closeddoc.lastRequestDate = currentTime;
                    
                    closedDocs.put(new Integer(docid), closeddoc);

                    //this.logger.finest("CLOSE_DOC: 02 docHTML with PoolUniqueID " + doc.poolUniqueId() + " has been marked as a CLOSED Doc.");
                }
                
                doc = null;
            }
            else
            {
                closeddoc.lastRequestDate = currentTime;
                closeddoc.closeRequestsReceived++;
                
                //this.logger.finest("CLOSE_DOC: docHTML with IDX " + String.valueOf(docid) + " received another request for CLOSING. Currently it has received " + String.valueOf(closeddoc.closeRequestsReceived) + " requests for CLOSE state.");
            }
            
            closeddoc = null;
            docid     = 0;
        }
        
        closeExpiredDocs(ctx, false);
    }
    
    public static long  DOCTOCLOSEEXPIRATIONTIME = 1000 * 60 * 15;
    
    public void closeExpiredDocs( EboContext boctx, boolean force )
    {
        long   currentTime   = 0;
        long   lastUsageTime = 0;
        int    docIdx        = 0;
        String docPoolUId    = null;
        
        Enumeration enumClosedDocs = null;
        
        boApplication   boApp        = null;
        boMemoryArchive boMemArchive = null;
        boPoolManager   boPoolMgr    = null;
        
        ClosedDoc closeddoc = null;
        docHTML   doc       = null;
        
        if(this.closedDocs != null)
        {
            boApp        = boctx.getApplication();
            boMemArchive = boApp.getMemoryArchive();
            boPoolMgr    = boMemArchive.getPoolManager();
            
            currentTime    = System.currentTimeMillis();
            enumClosedDocs = this.closedDocs.elements();
            
            if(enumClosedDocs != null)
            {
                while( enumClosedDocs.hasMoreElements() )
                {
                    closeddoc     = (ClosedDoc)enumClosedDocs.nextElement();
                    docIdx        = closeddoc.docIdx;
                    lastUsageTime = currentTime - closeddoc.lastRequestDate;
                    
                    if(force || lastUsageTime > DOCTOCLOSEEXPIRATIONTIME)
                    {
                        //this.logger.finest("CLOSE_EXP_DOCS: 01 -> docHTML with IDX " + String.valueOf(docIdx) + ", defined as closed, has been FOUND in the closedDocs list.");
                        
                        doc = getDocByIDX(docIdx, boctx);

                        if( doc != null )
                        {
                            docPoolUId = doc.poolUniqueId();
                            boPoolMgr.realeaseAllObjects( docPoolUId );
                            boPoolMgr.destroyObject( doc );
                            docPoolUId = null;
                            //this.logger.finest("CLOSE_EXP_DOCS: 02 -> docHTML with PoolUniqueID " + String.valueOf(docPoolUId) + ", defined as closed, has been DESTROYED.");
                        }
                        
                        doc = null;
                        
                        this.closedDocs.remove( new Integer(docIdx) );
                        //this.logger.finest("CLOSED_EXP_DOCS: 03 -> docHTML with IDX " + String.valueOf(docIdx) + ", defined as closed, has been REMOVED from the closedDocs list.");
                    }
                    
                    lastUsageTime = 0;
                    docIdx        = 0;
                    closeddoc     = null;
                }
            }
            
            enumClosedDocs = null;
            currentTime    = 0;
            
            boPoolMgr    = null;
            boMemArchive = null;
            boApp        = null; 
        }
    }
    
    public void checkDoc( int idx )
    {
        ClosedDoc closeddoc = (ClosedDoc)closedDocs.get( new Integer(idx) );
        if( closeddoc != null )
        { 
            if( closeddoc.lastRequestDate == closeddoc.dateToClose )
            {
                logger.warn(
                    LoggerMessageLocalizer.getMessage("DOC_WAS_CLOSED")+" ["+idx+"] "+LoggerMessageLocalizer.getMessage("AT")+" " + new Date( closeddoc.dateToClose ) + " "+LoggerMessageLocalizer.getMessage("RECOVERED_DOC")
                );
            }
            closeddoc.lastRequestDate = System.currentTimeMillis();
        }
    }
        
    public String[] processRequest(EboContext boctx) throws java.io.IOException,ServletException,boRuntimeException,Exception 
    {
        HttpServletRequest  request     = null;
        HttpServletResponse response    = null;
        PageContext         pageContext = null;
        
        docHTML   dochtml    = null;
        String[]  toReturn   = new String[2];
        ClosedDoc oClosedDoc = null;
        
        int    DOCID  = 0;
        String sDocId = null;
        
        
        boctx.setPreferredPoolObjectOwner( this.poolUniqueId() );
        
        request     = boctx.getRequest();
        response    = boctx.getResponse();
        pageContext = boctx.getPageContext();
        
        try
        {
            dochtml = poolDocHTMLManager(boctx);
            
            if( dochtml == null )
            {
                sDocId = request.getParameter("docid");  //  Docid from the server;
                
                if(sDocId != null && sDocId.length() > 0)
                {
                    DOCID = ClassUtils.convertToInt(sDocId, -1);
                    
                    if(DOCID > -1)
                    {
                        oClosedDoc = (ClosedDoc)this.closedDocs.get(new Integer(DOCID));
                        
                        if(oClosedDoc != null)
                        {
                            logger.finest
                            (
                                LoggerMessageLocalizer.getMessage("THE_REQUEST_DOCID_IS_IN_THE_CLOSED_OBJECT_POOL_DOCID")+"[" + DOCID + "]: " +
                                LoggerMessageLocalizer.getMessage("THE_DOCUMENT_CLOSING_WAS_ALREADY_REQUESTED_BY_THE_CLIENT")+" \n" + 
                                "\n"+LoggerMessageLocalizer.getMessage("NUMBER_OF_CLOSING_REQUESTS")+": " + oClosedDoc.closeRequestsReceived +
                                "\n"+LoggerMessageLocalizer.getMessage("CLOSING_DATE")+"  :" + DATE_FORMATER.format(new Date(oClosedDoc.dateToClose)) +
                                "\n"+LoggerMessageLocalizer.getMessage("DOCUMENT_IDX")+"         :" + oClosedDoc.docIdx +
                                "\n"+LoggerMessageLocalizer.getMessage("LAST_REQUEST_WAS")+"      :" + DATE_FORMATER.format(new Date(oClosedDoc.lastRequestDate))
                            );
                        }
                        else
                        {
                            logger.finest
                            (
                                LoggerMessageLocalizer.getMessage("THE_REQUEST_DOCID_DOES_NOT_EXIST_IN_THE_CLOSED_OBJECT_POOL_DOCID")+"["+DOCID+"]: " +
                                LoggerMessageLocalizer.getMessage("NO_INFORMATION_WAS_FOUND_ABOUT_WHEN_THE_DOCUMENT_WAS_CLOSED")
                            );
                        }
                        
                        dochtml = createDocHtml(boctx, DOCID);
                    }
                    else
                        dochtml = poolDocHTMLManager(boctx);                    
                }
                
                sDocId = null;
            }
            checkDoc(dochtml.getDocIdx());
            
            toReturn = dochtml.process(boctx,this);
            p_ndl.put(new Integer(dochtml.getDocIdx()) ,dochtml);
        } 
        catch (Exception e) 
        {
            throw(e);
        }
        
        return toReturn;
    }


    public docHTML getDOC(int idx) {
        return (docHTML)p_ndl.get(new Integer(idx));
    }
    public Hashtable getDOCList() {
        return p_ndl;
    }

    public void releseObjects(EboContext boctx) {
        if(p_ndl!=null) p_ndl.clear();
        boApplication   boApp        = boctx.getApplication();
        boMemoryArchive boMemArchive = boApp.getMemoryArchive();
        boPoolManager   boPoolMgr    = boMemArchive.getPoolManager();
        
        if(this.poolUniqueId() == null)
            logger.severe("docHTML_controler.releseObjects -> "+LoggerMessageLocalizer.getMessage("CALL_TO_THISPOOLUNIQEID_RETURNED_NULL"));
        
        boPoolMgr.realeaseObjects(this.poolUniqueId(), boctx);
        boctx.setController(null);
    }

    public void poolObjectPassivate() {
        // TODO:  Implement this netgest.bo.system.boArchival abstract method
    }

    public void poolObjectActivate() {
        // TODO:  Implement this netgest.bo.system.boArchival abstract method
    }
    protected docHTML poolDocHTMLManager(EboContext boctx) 
    {
        HttpServletRequest request = boctx.getRequest();
        docHTML dochtml = null;
        int DOCID = ClassUtils.convertToInt(request.getParameter("docid"),-1);  //  Docid from the server;
        
        if(DOCID != -1) 
        {
            dochtml = getDocByIDX(DOCID,boctx);
        } 
        else 
        {
            synchronized( docHTML.class )
            {
                p_nddcounter++;
                dochtml = createDocHtml( boctx, p_nddcounter );
            }
        }
        return dochtml;
    }
    
    public docHTML createDocHtml( EboContext boctx, int idx )
    {
        boApplication   boApp        = boctx.getApplication();
        boMemoryArchive boMemArchive = boApp.getMemoryArchive();
        boPoolManager   boPoolMgr    = boMemArchive.getPoolManager();
        
        docHTML dochtml = new docHTML(boctx,idx);
        String fMode = boctx.getRequest().getParameter("formMode");
        
        if( fMode!=null && fMode.equalsIgnoreCase("quest") )
            dochtml.p_WebForm = true;
        
        dochtml.p_doccontroller = this;
        boPoolMgr.putObject(dochtml, new Object[] {"DOCHTML:IDX:" + idx});
        dochtml.poolSetStateFull(this.poolUniqueId());
        
        return dochtml;
    }
    
    public docHTML getDocByIDX(int DOCID,EboContext boctx) 
    {
        docHTML dochtml = (docHTML)boctx.getApplication().getMemoryArchive().getPoolManager().getObject(boctx,this.poolUniqueId(),"DOCHTML:IDX:"+DOCID);
        return dochtml;
    }

    public int getVui(int DOCID, long boui) 
    {
        return getVui(DOCID, boui, null);
    }
    public int getVui(int DOCID, long boui, String id) 
    {
        String key;
        if(id == null)
            key = DOCID + "_" + boui;
        else
            key = DOCID + "_" + boui + "_" +id;
        if(vuiControl.containsKey(key))
        {
            return ((Integer)vuiControl.get(key)).intValue(); 
        }
        else
        {
            int v = vui++;
            vuiControl.put(key, new Integer(v));
            return v;
        }
    }  
    
    public String getVuiClob(int DOCID, long boui, String name, String id) 
    {
        String key = DOCID + "_" + boui + "_" + name;
        if(vuiControl.containsKey(key))
        {
            return (String)vuiControl.get(key); 
        }
        else
        {
            vuiControl.put(key, id);
            return id;
        }
    }
    
    private class ClosedDoc
    {
        public int      docIdx = 0;
        public long     dateToClose = 0;
        public long     lastRequestDate = 0;
        public int      closeRequestsReceived = 0;
    }
    
}