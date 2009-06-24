<%@ page import="java.util.*,java.io.*,java.awt.image.*"%> 
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*,javax.imageio.*,javax.imageio.stream.*"%>
<%@ page import="netgest.bo.impl.document.DocumentHelper"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession,netgest.io.*,com.oreilly.servlet.*"%>
<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=true; 
try 
{
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
    if(request.getParameter("att_download")!=null && request.getParameter("att_download").equals("y")) {
        boolean display = request.getParameter("att_display")!=null && request.getParameter("att_display").equalsIgnoreCase("y"); 
        boObject obj = DOC.getObject(Long.parseLong(look_parentBoui));
        ServletContext context = pageContext.getServletContext();
        if(look_parentAttribute == null || "".equals(look_parentAttribute))
        {
            file = obj.getAttribute("file").getValueiFile();
        }
        else
        {
            file = obj.getAttribute(look_parentAttribute).getValueiFile();
        }
        if (file.exists() && file.isFile()) 
        { 
            String bfilename = file.getName(); 
            if( "netgest.io.AppxiFile".equals( file.getClass().getName() ) )
            {
                if( obj.getAttribute("fileName") != null )
                {
                    bfilename = obj.getAttribute("fileName").getValueString();
                    if( "".equals( bfilename ) )
                    {
                        bfilename = file.getName();
                    }
                }
            }
            String desformat = request.getParameter("format");
            String desformatMime = context.getMimeType( "x." + desformat );

            String mimetype = context.getMimeType(bfilename.toLowerCase());

            response.setHeader("Cache-Control","private");     
            response.reset();
            ServletOutputStream so = response.getOutputStream(); 
            
            if (!display)
                response.setHeader("Content-Disposition","attachment; filename="+bfilename);

            Long FileSize = new Long(file.length()); 
            int xfsize = FileSize.intValue(); 

            response.setContentType(mimetype); 
            response.setContentLength(xfsize); 
            int rb=0; 
            InputStream is= null;
            try { 
                is = file.getInputStream();
                byte[] a=new byte[4*1024];
                while ((rb=is.read(a)) > 0) {  
                    so.write(a,0,rb); 
                } 
                is.close();
            } 
            catch (Exception e) 
            {
            }
            finally
            {
                if( is != null ) is.close();
            }
            so.close(); 
            return;
        } 
        else 
        { 
            response.sendError(response.SC_NOT_FOUND); 
            response.flushBuffer(); 
        } 
    }      

    if("POST".equals(request.getMethod())) 
    {
        if(checkIn == null)
        {
            boObject refobj = DOC.getObject(netgest.utils.ClassUtils.convertToLong(look_parentBoui,-1));
            String tmpFolder = netgest.bo.impl.document.Ebo_DocumentImpl.getTempDir();
            if(tmpFolder.endsWith("\\") || tmpFolder.endsWith("/"))
            {
               tmpFolder =  tmpFolder + System.currentTimeMillis() + File.separator;
            }
            else
            {
               tmpFolder =  tmpFolder + File.separator + System.currentTimeMillis();
            }
            java.io.File tmpdir = new java.io.File(tmpFolder);
            if(!tmpdir.exists()) 
            {
                tmpdir.mkdirs();
            }
            MultipartRequest mr=new MultipartRequest(request,tmpFolder,30*1024*1024);

            file = new FSiFile(null, mr.getFile("FILEBROWSE"),null);
            AttributeHandler att = refobj.getAllAttributes().get(look_parentAttribute);
            att.setValueiFile(file);
            DOC.poolSetStateFull(DOCLIST.poolUniqueId());
            refobj.poolSetStateFull(DOC.poolUniqueId());
        }
        else
        {            
            boObject refobj = DOC.getObject(netgest.utils.ClassUtils.convertToLong(look_parentBoui,-1));
            MultipartRequest mr=new MultipartRequest(request,DocumentHelper.getTempDirHelper(),15*1024*1024);
            Enumeration oEnum=mr.getFileNames();   
            String decompressStr = mr.getParameter("DECOMPRESS");            
            boolean decompress = true;
            if(decompressStr != null && "1".equals(decompressStr)) decompress = false;            
            DocumentHelper.checkIn(refobj,mr.getFile("FILEBROWSE"),decompress,look_parentAttribute);
        }
%>

    <html>
    <head>
    <meta http-equiv="Content-Type" http-equiv="expires" content="text/html; charset=UTF-8"/>
    <title>LOOKUP OBJECTS</title>
    <%@ include file='boheaders.jsp'%><script>
    
    var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search16.gif">Anexar Ficheiro ';
    var objDescription='Escolha um ficheiro que esteja no seu computador, de modo a enviá-lo para o server';
    </script>
    <body onload="onloadclose()" >
    <script language="javascript">
        function onloadclose() {            
            window.setTimeout("winmain().ndl[<%=clientIDX%>].refresh();winmain().ndl[getIDX()].close();",100);            
        }
    </script>    
    </body>
    </html>
<%        
        return;
    }
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<%@ include file='boheaders.jsp'%><script>

var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search16.gif">Anexar Ficheiro ';
var objDescription='Escolha um ficheiro que esteja no seu computador, de modo a enviá-lo para o server';
</script>
<body onload="document.getElementById('FILEBROWSE').focus();if(document.getElementById('DECOMPRESS1')!=null)document.getElementById('DECOMPRESS1').checked = true;">
<script language="javascript">
    function setUrlAttribute(href,attname,value) {
        var xqry = href.substring(href.indexOf("?")+1);
        var xargs = xqry.split("&");
        var fnd = false;
        for(var i=0;i<xargs.length;i++) {
            if(xargs[i].split("=")[0]==attname) {
                xargs[i] = attname+"="+encodeURIComponent(value);
                fnd = true;
            }
        }
        if(!fnd) xargs[xargs.length] = attname+"="+encodeURIComponent(value);
        return href.substring(0,href.indexOf("?")+1)+xargs.join("&"); 
        
    }
</script>
<FORM id='boFormSubmit' accept-charset="ISO-8859-15" method="POST" enctype="multipart/form-data" >
   <table cellpadding=0 cellspacing=0 style='height:"100%"'>
      <tr>
          <td style='height:40px;width="100%"' valign="top">
          <%
  docHTML_section sec = DOC.createSection("lookupEbo_Document"+look_parentAttribute,"",false,5);
  
  bodef=boDefHandler.getBoDefinition("Ebo_Document");
  docHTML_sectionRow row; 

/*  row=sec.addRow();
  row.addCell(
            docHTML_sectionField.newCombo(
            new StringBuffer("1"),
            new StringBuffer("idtipo"),
            new StringBuffer("Tipo "),
            xInternal[0],
            xExternal,
            xInternal,
            false,
            new StringBuffer("changeobject"),null,null
            ) 
            ,xattributes
            );
*/

  Hashtable xattributes=new Hashtable();

  row=sec.addRow();
  
  //docHTML_sectionCell cell = row.addCellMultiField();
  
  /*cell.addField(docHTML_sectionField.newText(
                new StringBuffer("TEXTSEARCH"),
                new StringBuffer("TEXTSEARCH"),
                new StringBuffer("Selecione o ficheiro:"),
                new StringBuffer(""))
                ,"100%",xattributes);*/

    
  row.addCell(docHTML_sectionField.newFileBrowse(
                new StringBuffer("FILEBROWSE"),
                new StringBuffer("FILEBROWSE"),
                new StringBuffer("Selecione o ficheiro "),
                new StringBuffer(""),
                new StringBuffer(""),
                new StringBuffer(""),
                new StringBuffer("")) 
                ,xattributes);
                
    sec.render(pageContext,DOC,DOCLIST);                
  


if(checkIn != null){  
/*
    xattributes=new Hashtable();
    sec = DOC.createSection("__lookupEbo_Document"+look_parentAttribute,"",false,5);
    sec.setTitle("&nbsp;");     
    row=sec.addRow();    
            
    docHTML_sectionCell cellCom = row.addCellMultiField();      
    xattributes.put("colspan","10");
    xattributes.put("height","100%");
    xattributes.put("width","100%");    
    cellCom.addField(docHTML_sectionField.newText(
                new StringBuffer("COMMENT"),
                new StringBuffer("COMMENT"),
                new StringBuffer("Comentário"),
                new StringBuffer(""))
                ,"100%",xattributes
                ) ;    
    
    sec.render(pageContext,DOC,DOCLIST);
  
  */
    sec = DOC.createSection("__lookupEbo_Document"+look_parentAttribute,"",false,5);
    sec.setTitle("&nbsp;");
    row=sec.addRow();
    docHTML_sectionCell cellZip = row.addCellMultiField();  
    xattributes.put("attr_td"," align='center' ");    
    cellZip.addField(docHTML_sectionField.newBoolean(
                new StringBuffer("DECOMPRESS"),
                new StringBuffer("DECOMPRESS"),
                new StringBuffer("Descomprimir"),
                new StringBuffer(""),
                new StringBuffer(""),
                new StringBuffer(""),
                new StringBuffer(""))
                ,"",xattributes
                ) ;          
    sec.render(pageContext,DOC,DOCLIST);
}


    sec = DOC.createSection("__lookupEbo_Document"+look_parentAttribute,"",true,5);
    sec.setTitle("&nbsp;");
    row=sec.addRow();
    
    docHTML_sectionCell cell = row.addCellMultiField();
    
    xattributes.put("attr_td"," align='center' ");
    cell.addField( docHTML_sectionField.newButton(
                new StringBuffer("confirm"),
                new StringBuffer("Anexar"),
                new StringBuffer("filesubmit()"))
                ,"",xattributes
                ) ;

    cell.addField( docHTML_sectionField.newButton(
                new StringBuffer("cancel"),
                new StringBuffer("Cancelar"),
                new StringBuffer("winmain().ndl[getIDX()].close();"))
                ,"",xattributes
                ) ;
  sec.render(pageContext,DOC,DOCLIST);

%>

          </td>
     </tr>

   </table>
<script>
    function filesubmit() {
        if(boFormSubmit.FILEBROWSE.value.length > 0) {    
            if(document.getElementById("DECOMPRESS0")!=null){
                if(document.getElementById("DECOMPRESS0").checked){
                    boFormSubmit.DECOMPRESS.value = 0;            
                }else{
                    boFormSubmit.DECOMPRESS.value = 1;            
                }
            }
            boFormSubmit.submit();
        } else {
            alert("Tem de selecionar um ficheiro.");
        }
    }

        
</script>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='look_parentObj' value='<%=look_parentObj%>' />
    <INPUT type='hidden' name='look_parentBoui' value='<%=look_parentBoui%>' />
    <INPUT type='hidden' name='look_parentAttribute' value='<%=look_parentAttribute%>' />
    <INPUT type='hidden' name='clientIDX' value='<%=clientIDX%>' />
    <INPUT type='hidden' name='clientIDXtoClose' value='<%=clientIDXtoClose%>'/>
    <INPUT type='hidden' name='boFormLookup' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
</FORM>
</body>
</html>
<%
} finally {
    if(initPage) {if (boctx!=null)boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
