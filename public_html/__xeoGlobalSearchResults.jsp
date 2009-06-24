<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="java.io.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.bo.presentation.render.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.bo.controller.basic.*"%>
<%@ page import="netgest.xwf.common.xwfHelper"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="netgest.bo.localized.*"%>



<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
Connection cn = null;
PreparedStatement pstm = null;
ResultSet rslt = null;
PreparedStatement pstm1 = null;
ResultSet rslt1 = null;
try {
 boolean masterdoc=false;
 if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null ){
        masterdoc=true;
 }
 boSession bosession = (boSession)request.getSession().getAttribute("boSession");
 if(bosession== null)
 {
    response.sendRedirect("login.jsp");
    return; 
 }
 if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
 }
 int IDX;int cvui;
 boDefHandler bodef;
 boDefAttribute atr;
 String idbolist;
 String[] ctrl;
 Hashtable xattributes;
 ctrl= DOCLIST.processRequest(boctx);
 IDX= ClassUtils.convertToInt(ctrl[0],-1);
 idbolist=ctrl[1];
 boolean error=false;
 docHTML DOC = DOCLIST.getDOC(IDX);
 long timeItotal = System.currentTimeMillis();
 double timeFtotal=0;
 StringBuffer sql=null;
    String searchString         = request.getParameter("searchString");
    
    String[] objects = null;
    boObject object = null;
    int num=0;
    if(searchString != null && !"".equals(searchString))
    {
        objects = new String[50];
        
        StringBuffer cls= new StringBuffer("select name from oebo_clsreg clsreg where not exists (select child$ from oixeouser$packages where parent$='").
        append( bosession.getPerformerBoui() ).append("' and clsreg.xeopackage$ = child$) and clsreg.xeopackage$ not in"). 
        append("(select boui from oebo_package where name like 'XEO$%' or name like 'XEODOC$%' or name like '_SYSTEM$%' or name like 'SGIS$%'").
        append("    or name like 'XEOMESSAGE$%' or name like 'XWF$%')");
        StringBuffer exclude= new StringBuffer();
        cn = boctx.getConnectionData();
        if ( !bosession.getUser().isAdministrator() )
        {
        pstm1 = cn.prepareStatement(cls.toString());
        rslt1 = pstm1.executeQuery();
      
        boolean initS = false;
        while ( rslt1.next() )
        {
            if ( !initS )
            {
                exclude.append(" and uiclass not in(");
                exclude.append("'");
                exclude.append( rslt1.getString(1) );
                exclude.append("'");
                initS=true;
            }
            else
            {
                exclude.append(",'");
                exclude.append( rslt1.getString(1) );
                exclude.append("'");
            }

        }
        if ( initS )
        {
            exclude.append(")");
        }
        }
        String words = boObjectList.arrangeFulltext( boctx, searchString);        
        sql = new StringBuffer("SELECT /*FIRST_ROWS*/ ui$,SCORE(1) FROM  Ebo_TextIndex WHERE contains(TEXT, '");
        sql.append(words);        
        sql.append("',1) > 0 ").append( exclude ).append(" ORDER BY 2 DESC,uiclass desc");    
                                        
        pstm = cn.prepareStatement(sql.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        pstm.setMaxRows(100);               
        try
        {         
            int index = 0;  
            boolean end = false;
            rslt = pstm.executeQuery();
            while(!end)
            {
                while ( rslt.next() && num < 50)
                {
                    
                    String label="";
                    try{
                        object = DOC.getObject( rslt.getLong(1) );
                        if(netgest.bo.security.securityOPL.canRead(object))
                        {
                            label = object.getBoDefinition().getLabel() ;
                            objects[ num++ ] =  label+"-i:-"+rslt.getString(1);
                        }
                    }
                    catch(Exception e)
                    {
                       objects[ num++ ] =  "Z"; 
                    }                      
                }
                if(num == 50)
                {
                    end = true;
                }
                else
                {
                    index = index + 100;     
                    if(rslt != null) rslt.close();
                    pstm.setMaxRows(index + 100);
                    rslt = pstm.executeQuery();
                    if(!rslt.absolute(index))
                    {
                        end = true;
                    }
                }
            }            
        }
        catch( Exception e )
        {
            error =true;
        }
        String[] objects1=new String[num];
        
        System.arraycopy( objects,0,objects1,0,num);
        objects=objects1;
        Arrays.sort( objects );
    }    
    
    
%>
<html>
<head>
<style type="text/css">
@import url('ieThemes/0/wkfl/ui-wkfl.css');
</style>
</head>
<%@ include file='boheaders2.jsp'%>
<body ondragenter="activethis()"  >

    <table style='width:100%;height:100%' cellpadding=4 cellspacing=0>
        <tr height='100%'>
            <td valign=top align=left style='width:50%'>
                <div style='width:100%;height:100%;overflow:auto'>
                    <table style='table-layout:fixed;' cellpadding=2 cellspacing=0>
                        <colgroup>
                        <col width='30px' />
                        <col />
                        <col width='100px' />
                        </colgroup>
                    <% 
                        String key = null;
                        String score = null;                        
                        boolean haveResults = false;
                        int count=0;
                        String lastName = "";
                        String objectName = "";
                        while(rslt != null && count <= num )
                        {         
                           try
                           {
                                count++;
                                object = DOC.getObject( ClassUtils.convertToLong( objects[ count-1].split("-i:-")[1] ) );
                                objectName = object.getName() ;
                                //score = rslt.getString(2);
                                haveResults=true;
                            %>
                            <% if( count==1){ %>
                            
                            <tr height='26px'>  
                                 <td colspan=2 style='font-size:13px' >
                                 <% if( num==50){ %>
                                    <%=JSPMessages.getString("GlobalSearch.3")%>
                                 <%}else{%>
                                 <%=JSPMessages.getString("GlobalSearch.4")%> <%=num%> <%=JSPMessages.getString("GlobalSearch.5")%>
                                 <%}%>
                                 </td>
                                 <td style='font-size:13px' ><%=JSPMessages.getString("GlobalSearch.6")%></td>
                            </tr>
                            
                            <%}%>
                            <% if( !lastName.equals( objectName )) {%>
                                <tr height='26px'>
                                <td colspan=3 class='underline' style='font-weight:bolder'><%=netgest.bo.def.boDefHandler.getBoDefinition(objectName ).getLabel()%></td>
                                </tr>
                            <%
                            lastName=objectName;
                            }%> 
                           <tr>
                           
                                <td class='underline' ><div style='text-align:center;background-color:#EEEEEE;border:1px solid #6297e5;font-size:12px'><%=count%></div></td>
                                <td class='underline'><%= object.getCARDIDwStatewLink()%></td>
                                <td class='underline'><nobr><%= object.getAttribute("SYS_DTSAVE").getValueString().replaceAll("T"," ").substring(0,16) %></nobr></td>                                
                            </tr>
                        <%}catch(Exception e){}%>                            
                    <%}%>  
                    <%if( !haveResults ){%>
                        <tr height='100%'>  
                                <%if( rslt!=null )
                                {%>
                                <td  colspan=3 align='center'><%=JSPMessages.getString("GlobalSearch.7")%></td>
                                <%}
                                else
                                {
                                    if ( error )
                                    {
                                    %>
                                        <td  colspan=3 align='center' style='color:red'><%=JSPMessages.getString("GlobalSearch.8")%></td>
                                    <%}else
                                    {%>
                                        <td  colspan=3 align='center'><%=JSPMessages.getString("GlobalSearch.9")%></td>    
                                    <%}
                                    
                                    %>
                                
                                
                                <%}%>
                            </tr>
                    <%}else{
                   timeFtotal = (System.currentTimeMillis() - timeItotal)/1000;
                   
                     %>
                      <tr>
                   <td colspan='2'  ><%=JSPMessages.getString("GlobalSearch.10")%> <%=timeFtotal%> <%=JSPMessages.getString("GlobalSearch.11")%> </td>
                        </tr>
                    <%} %>
                    </table>
                </div>
            </td>
        </tr>
    </table>
<!--<%=sql%> -->
<script language="jscript">
function BuildXml()
{       
    var xmlQuery = document.boFormSubmit.boFormSubmitXml.value;        
    return xmlQuery;  
}
</script>
<FORM name='boFormSubmit' method='post'>
    <%
        java.util.Hashtable options = new java.util.Hashtable();    
        if(request.getParameter( "method" ) != null){
            options.put(BasicPresentation.OPTION_METHOD,request.getParameter( "method" ));
        }
        if(request.getParameter( "method" ) != null){
            options.put(BasicPresentation.OPTION_INPUT_METHOD,request.getParameter( "method" ));
        }
        if(request.getParameter( "boui" ) != null){
            options.put(BasicPresentation.OPTION_REQUESTED_BOUI,request.getParameter( "boui" ));
        }
        options.put(BasicPresentation.OPTION_TYPE_FORM,"1");
        options.put(BasicPresentation.OPTION_JSP_NAME,this.getClass().getName());                 
    %>    
    <%= DOC.getController().getPresentation().writeJspFooter(null ,null,options,masterdoc,request) %>  
</FORM> 
</body>
</html>
<%
} finally {
if (boctx!=null)boctx.close();if(DOCLIST!=null && boctx!=null)DOCLIST.releseObjects(boctx);
if(rslt != null) rslt.close();
if(pstm != null) pstm.close();        
if(rslt1 != null) rslt1.close();
if(pstm1 != null) pstm1.close();
}%>
