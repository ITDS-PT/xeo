<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.io.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.system.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.impl.document.DocumentHelper"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession,netgest.io.*,com.oreilly.servlet.*"%>
<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

boSession bosession=null;

try
{
	bosession = (boSession)request.getSession().getAttribute("boSession");
}
catch(Exception x){}

EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=true;

try
{
	boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");

	if(bosession== null)
	{
    	bosession = boapp.boLogin("SYSTEM",boLoginBean.getSystemKey());
	}
	
	if(boctx==null)
	{
	    boctx = bosession.createRequestContext(request,response,pageContext);
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
    
    String look_object = "Ebo_Document";
    
    String look_parentObj       = request.getParameter("look_parentObj");
    String look_parentBoui      = request.getParameter("look_parentBoui");
    String look_parentAttribute = request.getParameter("look_parentAttribute");
    String clientIDX            = request.getParameter("clientIDX");
    String clientIDXtoClose     = request.getParameter("clientIDXtoClose");
    String checkIn              = request.getParameter("checkIn");
    
    
    iFile file=null;

    boObject obj = boObject.getBoManager().loadObject(boctx,Long.parseLong(look_parentBoui));
    ServletContext context = pageContext.getServletContext();
    file = obj.getAttribute("file").getValueiFile();
    if (file.exists() && file.isFile()) { 
        //long dtlastd = request.getDateHeader("If-Modified-Since"); 
        //  if ( file.lastModified() - dtlastd < 500 ) { 
        //    response.sendError(response.SC_NOT_MODIFIED,"Not modififed"); 
        //} else { 
        //    response.setDateHeader("Last-Modified",file.lastModified() ); 
            response.setHeader("Cache-Control","private"); 
            response.reset();
    
            String bfilename = file.getName(); 
            String mimetype = context.getMimeType(bfilename.toLowerCase());
    
            Long FileSize = new Long(file.length()); 
            int xfsize = FileSize.intValue(); 
    
            response.setContentType(mimetype); 
            response.setContentLength(xfsize); 
              
            response.setHeader("Content-Disposition","attachment; filename="+bfilename);
    
            int rb=0; 
            ServletOutputStream so = response.getOutputStream(); 
    
            try { 
                InputStream is=file.getInputStream();
                byte[] a=new byte[4*1024];
                while ((rb=is.read(a)) > 0) { 
                    so.write(a,0,rb); 
                } 
                is.close();
            } 
            catch (Exception e) 
            {            
            }
            
            so.close(); 
            return;
            
            
        } else { 
            response.sendError(response.SC_NOT_FOUND); 
            response.flushBuffer(); 
        }     
        return;
  }
  catch (Exception e) {
            response.sendError(response.SC_NOT_FOUND); 
            response.flushBuffer();   
} finally {
    if(initPage && boctx!=null) {boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
