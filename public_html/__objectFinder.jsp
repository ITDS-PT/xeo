<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.bo.presentation.render.*"%>
<%@ page import="java.net.URLEncoder"%>
<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
    EboContext boctx = (EboContext)request.getAttribute("a_EboContext");

    try {
        boSession bosession = (boSession)request.getSession().getAttribute("boSession");
        if(bosession == null)
        {
            
                StringBuffer url = new StringBuffer("?");
                Enumeration oEnum = request.getParameterNames();
                while( oEnum.hasMoreElements() )
                {
                    String pname = oEnum.nextElement().toString();
                    url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ),"UTF-8" ) );
                }
                response.sendRedirect("login.jsp?returnToPage=__objectFinder.jsp"+ java.net.URLEncoder.encode(url.toString(),"UTF-8" ));
                return;
        
           
        }
        if(boctx==null) 
        {
            boctx = bosession.createRequestContext(request,response,pageContext);
            request.setAttribute("a_EboContext",boctx);
        }

        int IDX;            
        boDefAttribute atr;
        String idbolist;
        String[] ctrl;

        ctrl= DOCLIST.processRequest(boctx);
        IDX     = ClassUtils.convertToInt(ctrl[0],-1);
        idbolist=ctrl[1];
        docHTML DOC = DOCLIST.getDOC(IDX);        
        String look_object          = request.getParameter("look_object");        
        
        boDefHandler bodef = boDefHandler.getBoDefinition( look_object );
        
        
        int height = 0;
        String value = null;
        StringBuffer id = null;
        StringBuffer query = new StringBuffer();
        StringBuffer form = new StringBuffer();
        StringBuffer script = new StringBuffer();
        StringBuffer toPrint = new StringBuffer();
        toPrint.append("<FORM name='finderForm'>");
        toPrint.append("<table valign=top class='section'  cellSpacing='0' cellPadding='3' width='100%'><COLGROUP/><COL width='120' /><COL /><COL style=\"PADDING-LEFT: 5px\" width='70' /><COL /><tbody>");
        
        byte type;        
        boolean addAndQuery = false;
        Hashtable attributes = new Hashtable();                
        
        boDefAttribute doDefAttr = null; 
        if(bodef != null)
        {
            boDefAttribute[] attrList = bodef.getBoAttributes();
            for (int i = 0; i < attrList.length; i++) 
            {
                doDefAttr = attrList[i];
                if(doDefAttr.isFinder())
                {    
                    id = new StringBuffer("attr__" + doDefAttr.getName());
                    height = height + 32;
                    type = doDefAttr.getValueType();
                    value = request.getParameter(id.toString());  
                    if(value == null)
                    {
                        value = "";
                    }
                    form.append("<INPUT type='hidden' name='").append(id).append("'/>\n");                
                    toPrint.append("<TR>");
                    toPrint.append("<TD>");
                    toPrint.append("<label  for='"+ "nameH" +"'>");       
                    toPrint.append(doDefAttr.getLabel());
                    toPrint.append("</label>");
                    toPrint.append("</TD>");             
                    toPrint.append("<TD colspan='3'>");  
                    if(doDefAttr.renderAsLov() || !"".equals(doDefAttr.getLOVName()))
                    {
                        script.append("boFormSubmit.").append(id).append(".value = document.getElementById('").append(doDefAttr.getName()).append("').returnValue;\n");
                        if(value != null && !"".equals(value))
                        {                        
                            if(addAndQuery)
                            {
                                query.append(" AND ");    
                            }                    
                            if(type == boDefAttribute.VALUE_CHAR)
                            {
                                query.append(" ").append(doDefAttr.getName()).append(" = '").append(value).append("' ");
                            }
                            else
                            {
                                query.append(" ").append(doDefAttr.getName()).append(" = ").append(value).append(" ");
                            }
                            addAndQuery = true;
                        }                
                        docHTML_renderFields.writeHTML_forCombo(
                                                        toPrint,
                                                        new StringBuffer(value),
                                                        new StringBuffer(doDefAttr.getName()),
                                                        id,
                                                        1,
                                                        netgest.bo.lovmanager.LovManager.getLovObject( DOC.getEboContext(), doDefAttr.getLOVName() ),
                                                        false,
                                                        false,
                                                        true,
                                                        false,
                                                        new StringBuffer(""),
                                                        false,
                                                        false,
                                                        attributes        
                                                        );
                            
                    }
                    else if(doDefAttr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                    {
                        script.append("boFormSubmit.").append(id).append(".value = document.getElementById('").append(doDefAttr.getName()).append("').value;\n");
                    
                        if(doDefAttr.getRelationType() == boDefAttribute.RELATION_1_TO_1)
                        {
                            if(value != null && !"".equals(value))
                            {                        
                                if(addAndQuery)
                                {
                                    query.append(" AND ");    
                                }                    
                                query.append(" ").append(doDefAttr.getName()).append(" = ").append(value).append(" ");
                                addAndQuery = true;
                            }                
                        
                            docHTML_renderFields.writeHTML_lookup(toPrint,
                                                                  null,
                                                                  null,
                                                                  new StringBuffer(value),
                                                                  new StringBuffer(doDefAttr.getName()),
                                                                  id,
                                                                  1,
                                                                  DOC,
                                                                  false,
                                                                  true,
                                                                  false,
                                                                  false,
                                                                  false,
                                                                  attributes,
                                                                  doDefAttr,
                                                                  id.toString());                                                                                                                      
                                                                  
                        }
                        else
                        {
                        
                            if(value != null && !"".equals(value))
                            {                                          
                                if(addAndQuery)
                                {
                                    query.append(" AND ");    
                                }                    
                                query.append(" ").append(doDefAttr.getName()).append(" in ( ").append(value.replaceAll(";",",")).append(" ) ");
                                addAndQuery = true;
                            }                
                            
                            docHTML_renderFields.writeHTML_lookupN(toPrint,
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  new StringBuffer(value),
                                                                  new StringBuffer(doDefAttr.getName()),
                                                                  id,
                                                                  1,
                                                                  DOC,
                                                                  false,
                                                                  true,
                                                                  false,
                                                                  false,
                                                                  false,
                                                                  attributes,
                                                                  doDefAttr,
                                                                  id.toString());                                                                                                                                          
                        }                          
                    }
                    else
                    {
                        if(type == boDefAttribute.VALUE_CHAR)
                        {             
                            script.append("boFormSubmit.").append(id).append(".value = document.getElementById('").append(doDefAttr.getName()).append("').value;\n");
                            if(value != null && !"".equals(value))
                            {                            
                                if(addAndQuery)
                                {
                                    query.append(" AND ");    
                                }                    
                                query.append(" ").append(doDefAttr.getName()).append(" like '").append(value).append("' ");
                                addAndQuery = true;
                            }                
                            docHTML_renderFields.writeHTML_text(toPrint,
                                                    new StringBuffer(value),
                                                    new StringBuffer(doDefAttr.getName()),
                                                    id,
                                                    1,
                                                    false,
                                                    true,
                                                    false,
                                                    new StringBuffer(""),
                                                    false,
                                                    false,                                            
                                                    doDefAttr.getLen(),                                            
                                                    attributes
                                                    );
                        }
                        else if(type == boDefAttribute.VALUE_BOOLEAN)
                        {
                            script.append("if(document.getElementById('").append(id).append("0').checked){\n");                    
                            script.append("\tboFormSubmit.attr__").append(doDefAttr.getName()).append(".value = 1\n");
                            script.append("}else if(document.getElementById('").append(id).append("1').checked){\n");
                            script.append("\tboFormSubmit.attr__").append(doDefAttr.getName()).append(".value = 0\n");
                            script.append("}\n");
                            if(value != null && !"".equals(value))
                            {                            
                                if(addAndQuery)
                                {
                                    query.append(" AND ");    
                                }
                                query.append(" ").append(doDefAttr.getName()).append(" = '").append(value).append("' ");
                                addAndQuery = true;
                            }                 
                            docHTML_renderFields.writeHTML_forBoolean(toPrint,
                                                    new StringBuffer(value),
                                                    new StringBuffer(doDefAttr.getName()),
                                                    id,
                                                    1,
                                                    false,
                                                    true,
                                                    false,
                                                    new StringBuffer(""),
                                                    false,
                                                    false,
                                                    attributes
                                                    );                                                                 
                        }
                        else if(type == boDefAttribute.VALUE_NUMBER)
                        {
                 
                            script.append("boFormSubmit.").append(id).append(".value = document.getElementById('").append(doDefAttr.getName()).append("').value;\n");
                            if(value != null && !"".equals(value))
                            {                            
                                if(addAndQuery)
                                {
                                    query.append(" AND ");    
                                }                    
                                query.append(" ").append(doDefAttr.getName()).append(" = ").append(value).append(" ");
                                addAndQuery = true;
                            }                                               
        
                             String decimals = "0";
                             String minDecimals = "-99999999";
                             String maxNumber = "99999999";
                             String minNumber = "-99999999";
                             String grouping = "0";
                 
                 
                             docHTML_renderFields.writeHTML_forNumber(toPrint,
                                                            new StringBuffer(value),
                                                            new StringBuffer(doDefAttr.getName()),
                                                            id,
                                                            1,
                                                            new StringBuffer(doDefAttr.getType().toString()),
                                                            new StringBuffer(decimals),
                                                            new StringBuffer(minDecimals),
                                                            ("0".equals(grouping)) ?  false :  true,    
                                                            new StringBuffer(maxNumber),
                                                            new StringBuffer(minNumber),                                                    false,
                                                            true,
                                                            false,
                                                            new StringBuffer(""),
                                                            false,
                                                            false,
                                                            attributes
                                                            );                                                                                                                                                 
                                                            
                        } 
                        else if(type == boDefAttribute.VALUE_DATETIME)
                        {
                            script.append("boFormSubmit.").append(id).append(".value = document.getElementById('").append(doDefAttr.getName()).append("').returnValue;\n");                    
                            if(value != null && !"".equals(value))
                            {                            
                                if(addAndQuery)
                                {
                                    query.append(" AND ");    
                                }                                            
                                value = value.replace('T',' ');
                                value = value.substring(0,value.length() - 3);
                                query.append("  TO_DATE(TO_CHAR(").append(doDefAttr.getName()).append(",'YYYY-MM-DD HH:MI'),'YYYY-MM-DD HH:MI') = TO_DATE('").append(value).append("','YYYY-MM-DD HH:MI') ");
                                addAndQuery = true;
                            }                   
                            docHTML_renderFields.writeHTML_forDateTime(toPrint,
                                                            new StringBuffer(value),
                                                            new StringBuffer(doDefAttr.getName()),
                                                            id,
                                                            1,
                                                            true,
                                                            false,
                                                            true,
                                                            false,
                                                            new StringBuffer(""),
                                                            false,
                                                            false,
                                                            attributes
                                                            );                                                                 
                        }       
                        else if(type == boDefAttribute.VALUE_DATE)
                        {
                            script.append("boFormSubmit.").append(id).append(".value = document.getElementById('").append(doDefAttr.getName()).append("').value;\n");
                            if(value != null && !"".equals(value))
                            {
                                if(addAndQuery)
                                {
                                    query.append(" AND ");    
                                }                    
                                query.append(" ").append(doDefAttr.getName()).append(" = TO_DATE('").append(value).append("','DD-MM-YYYY') ");
                                addAndQuery = true;
                            }                   
                            docHTML_renderFields.writeHTML_forDate(toPrint,
                                                            new StringBuffer(value),
                                                            new StringBuffer(doDefAttr.getName()),
                                                            id,
                                                            1,
                                                            false,
                                                            true,
                                                            false,
                                                            new StringBuffer(""),
                                                            false,
                                                            false,
                                                            attributes
                                                            );                                                                 
                        }
                    }                                            
                    toPrint.append("</TD>");
                    toPrint.append("</TR>");
                }
            } 
        }
        toPrint.append("</tbdoy></table>");
        toPrint.append("</FORM>");
                        
        
        
        
        StringBuffer finder = new StringBuffer("<table bgcolor='#FFFFFF' cellpading=0 cellspacing=0 style='width=100%;height:100%'>");        
        finder.append("<tr>");
        finder.append("  <td style='width=100%;height:").append(height).append("px' valign=top>");
        finder.append(toPrint.toString()); 
        finder.append("  </td>");
        finder.append("</tr>");
        finder.append("<tr>");
        finder.append("  <td style='height:20px;width=100%' valign=top>");
        finder.append("     <table bgcolor='#FFFFFF' cellpading=0 cellspacing=0 style='width=100%'>");
        finder.append("       <tr>");
        finder.append("         <td style='width=100%' valign=top>");
        finder.append("         </td>");
        finder.append("         <td style='width=30px' valign=top>");
        finder.append("             <button onclick='findObjects();listFrame.wait();'>Pesquisar</button>"); 
        finder.append("         </td>");
        finder.append("       </tr>");
        finder.append("     </table>");
        finder.append(" </td>");
        finder.append("</tr>");
        finder.append("<tr>");
        finder.append("  <td style='width=100%;height=100%' valign=top>");

        String jspName = "__list.jsp";
        StringBuffer jspResults = new StringBuffer();
        jspResults.append(jspName);
        jspResults.append("?docid=").append(IDX);
        jspResults.append("&method=").append("list");
        jspResults.append("&menu=").append("yes");
        jspResults.append("&object=").append(look_object);
        jspResults.append("&look_object=").append(look_object);
        jspResults.append("&look_query=").append(URLEncoder.encode( query.toString()));
        jspResults.append("&findOutSide=yes");        
        Enumeration oEnum = request.getParameterNames();
        while( oEnum.hasMoreElements() )
        {
            String pname = oEnum.nextElement().toString();
            if( !pname.equalsIgnoreCase("method") || 
                !pname.equalsIgnoreCase("docid") ||
                !pname.equalsIgnoreCase("IDX") ||
                !pname.equalsIgnoreCase("myIDX") ||
                !"clientIDXtoClose".equalsIgnoreCase(pname))             
            {
                jspResults.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ) ) );
            }   
        }
        jspResults.append("&clientIDXtoClose=").append( request.getParameter("myIDX") );
        
        finder.append("<IFRAME id='listFrame' src='").append(jspResults).append("'  frameBorder='0' width='100%' scrolling=no height='100%'></IFRAME>");
        finder.append(" </td>");
        finder.append("</tr>");        
        finder.append("</table>");
        

    %>
<html>
<head>
<SCRIPT LANGUAGE=javascript FOR=document EVENT=onkeypress>
<!--
 document_onkeypress()
//-->
</SCRIPT>
<script LANGUAGE="javascript" >
function findObjects()
{    
<%=script.toString()%>
boFormSubmit.submit();  
}
function document_onkeypress() {

    var keycode;
    if (window.event) keycode = window.event.keyCode;
    if (keycode == 13) 
    {
        listFrame.wait();
        findObjects();
    }    

}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Object Finder</title>
<base target="_self">
<%@ include file='boheaders.jsp'%>
<script>
var objLabel='<img align="absmiddle" hspace=3  src="resources/<%=bodef.getName()%>/ico16.gif">Procurar [<%=bodef.getLabel()%>] ';
var objDescription='';
</script>
<body scroll="no">
<%=finder.toString()%>
<FORM name='boFormSubmit'  method='get'>
    <INPUT type='hidden' name='boFormSubmitXml' />            
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=look_object%>' name='look_object' />
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />    
<%=form.toString()%>
</FORM>
</body>
</html>
<%
    } 
    finally 
    {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>

