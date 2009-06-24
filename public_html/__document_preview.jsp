<%@ page import="java.util.*,java.io.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.impl.document.DocumentHelper"%>
<%@ page import="netgest.bo.impl.document.merge.*"%>

<%@ page import="netgest.utils.*,netgest.bo.system.boSession,netgest.io.*,com.oreilly.servlet.*"%>
<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%


EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=true;
try {
    boSession bosession = (boSession)request.getSession().getAttribute("boSession");
    if(bosession== null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if(boctx==null) {
        boctx = bosession.createRequestContext(request,response,pageContext);
        request.setAttribute("a_EboContext",boctx);
    }

    int IDX;
    int cvui;
    boDefHandler bodef;
    //boDefAttribute atr;
    String idbolist;String[] ctrl;
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
    boObject BOI;
    if ( currObjectList == null ) BOI=null;
    else BOI=currObjectList.getObject();
    if( currObjectList != null ) currObjectList.first();    
            
        
    String look_parentBoui      = request.getParameter("look_parentBoui");
    boObject obj = DOC.getObject(Long.parseLong(look_parentBoui));
//    String fileName = obj.getCARDIDwNoIMG() + ".doc";
    String fileName = netgest.bo.impl.document.merge.TempFile.createNameForTempFile(null, ".doc");
    MergeHelper.mergeBoObject(obj,fileName);
    String path = netgest.bo.impl.document.Ebo_DocumentImpl.getTempDir();
    String s = path + File.separator + fileName;
    File file = new File(s);
    if (file.exists() && file.isFile()) 
    {    
        response.setHeader("Cache-Control","private");        
        String bfilename = file.getName();
        ServletContext context = pageContext.getServletContext();
        String mimetype = context.getMimeType(bfilename.toLowerCase());
        response.setHeader("Content-Disposition","attachment; filename="+bfilename);

        Long FileSize = new Long(file.length()); 
        int xfsize = FileSize.intValue(); 

        response.setContentType(mimetype); 
        response.setContentLength(xfsize); 

        int rb=0; 
        ServletOutputStream so = response.getOutputStream(); 

        try { 
            InputStream is=new FileInputStream(file);
            byte[] a=new byte[4*1024];
            while ((rb=is.read(a)) > 0) 
            { 
                so.write(a,0,rb); 
            } 
            is.close();
        } 
        catch (Exception e) 
        {
        }
                
        so.close(); 
        return;
    }
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}

%>
