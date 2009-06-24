<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.impl.document.merge.gestemp.presentation.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try 
{
    boSession bosession = (boSession)request.getSession().getAttribute("boSession");
    if(bosession== null) 
    {
        response.sendRedirect("login.jsp");
        return;
    }
    if(boctx==null) 
    {
        boctx = bosession.createRequestContext(request,response,pageContext);
        request.setAttribute("a_EboContext",boctx);
    }
    
    int IDX;
    int cvui;
    
    boDefHandler bodef;
    boDefAttribute atr;
    String idbolist;
    String[] ctrl;
    docHTML_section sec;
    docHTML_grid grid;
    Hashtable xattributes;    
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    String bouis=request.getParameter("bouis");
    String paramCheckedbouis=request.getParameter("checkedbouis");
    if("null".equals(paramCheckedbouis)) paramCheckedbouis=null; 
    String checkedBouis[] = (paramCheckedbouis == null || paramCheckedbouis.length() == 0) ? null:paramCheckedbouis.split(";");
    String iindividuals[] = (bouis == null || bouis.length() == 0) ? null:bouis.split(";");
    ArrayList rClassObj = new ArrayList();
    ArrayList rGroupSeq = new ArrayList();
    ArrayList rCheckeds = new ArrayList();
    String myIDX                = request.getParameter("docid");
    String document_boui           = request.getParameter("document_boui");    
    String parent_boui=request.getParameter("parent_boui");
    docHTML DOC = DOCLIST.getDOC(IDX);
    boObject parentObj=DOC.getObject(Long.parseLong(parent_boui));
    boObject docObj=DOC.getObject(Long.parseLong(document_boui));
    bridgeHandler bhParent = parentObj.getBridge("movements");
    boObject auxObj;
    StringBuffer init = new StringBuffer();
    StringBuffer settingBouis = new StringBuffer();
    boolean remove = "true".equals(request.getParameter("remove"));
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Hashtable groupClass = new Hashtable();
    if(docObj != null)
    {
        //checked objects
        for(int i = 0; checkedBouis!= null &&  i < checkedBouis.length;i++)
        {
            try
            {
                rCheckeds.add(checkedBouis[i]);            
            }
            catch(Exception e){/*IGNORE*/}
        }
        
        boBridgeIterator bhit = docObj.getBridge("classification").iterator();
        
        //primeiro vou obter todos as classificações - GESDocClf
        GesDocViewer.getClassification(docObj, rClassObj, rGroupSeq);
        //por cada classificacao o grupo de respostas
        ArrayList auxL = null;
        ArrayList auxV = null;
        String key;
        for (int i = 0; i < rClassObj.size(); i++) 
        {
            auxL = new ArrayList();
            auxV = new ArrayList();
            GesDocViewer.getGroupClassification(bhit, (boObject)rClassObj.get(i), (String)rGroupSeq.get(i), auxL, auxV);
            if(auxL != null && auxL.size() > 0)
            {
                key = String.valueOf(((boObject)rClassObj.get(i)).getBoui()) + "_" + (String)rGroupSeq.get(i);
                groupClass.put(key+"_labels", auxL);
                groupClass.put(key+"_values", auxV);
            }
        }
    }
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Lista de Software</title>
    <style type="text/css">@import url('xeo.css');</style>

<style type="text/css">
BODY
{
    BACKGROUND-COLOR: #fff8e0;
}

.gBodyLines_std
{
    BACKGROUND-COLOR: #fff8e0;
}
</style>

</head>
<script LANGUAGE="javascript" SRC="xeo.js"></script>
<script>

parent.checkedBouis = "";

function BuildXml()
{
    return "";
}
function init()
{
  
}
function onCheckBoxClick(boui, groupS, checkName)
{
    parent.checkedBouis = groupS;
/*
    var checkedBouis;    
    try
    {
        if(!parent.checkedBouis)
        {
            parent.checkedBouis = '';
        }
        checkedBouis = parent.checkedBouis;
    }
    catch(e){parent.checkedBouis = '';checkedBouis = parent.checkedBouis;}
    bouis = checkedBouis.split(";");
    if(document.getElementById(checkName).checked)
    {
       var i = 0;
       var found = false;
       while(!found && i < bouis.length)
       {
//          if(bouis[i] == boui)
          if(bouis[i] == groupS)
          {
            found = true;          
          }
          i++;
       }
       if(!found)
       {
//         bouis[bouis.length] = boui;
         bouis[bouis.length] = groupS;
       }
       i = 0;
       var join = '';
       while(i < bouis.length)
       {
            if(bouis[i] != '')
            {
                join = join + bouis[i] + ';';
            }
            i++;
       }
       parent.checkedBouis = join;
    }
    else
    {
       var i = 0;
       var found = false;
       var join = '';
       while(!found && i < bouis.length)
       {
//          if(bouis[i] != boui && bouis[i] != '')
          if(bouis[i] != groupS && bouis[i] != '')
          {
            join = join + bouis[i] + ';';
          }
          i++;
       }
       parent.checkedBouis = join;
    }
*/    
}
</script>
<body onload="init();" scroll="no">
        <TABLE cellSpacing='0' cellPadding='0' style='height:100%;width:100%;table-layout:fixed'>
            <TR>
                <TD style='height:100%;width:100%' >
                    <DIV style='width:100%;height:100%;overflow-x:auto'>
                        <TABLE style='height:100%;width:100%;' class='g_std' cellSpacing='0' cellPadding='0' width='100%'>
                            <TBODY>
                               
                                <TR>
                                    <TD>
                                        <DIV  id='g3104_divc' class='gContainerLines_std'>                                        
                                    <% if(rClassObj == null || rClassObj.size() == 0)
                                    {
                                    %>
                                            <TABLE style='height:100%' id='g3104_body' container='1' style='' cellpadding='2' cellspacing='0'  mode=''  options=''  letter_field=''  class='gBodyLines_std'>
                                            <COLGROUP/>
                                            <COL width='20'/>
                                            <COL width='20'/>
                                            <COL />
                                            <COL width='202' />
                                            <TR id='3104__TI_SwApp__null' exists=no >
                                                <TD select='none' COLSPAN='5'>
                                                    <TABLE id='g3104' select='none' style='height:100%;width:100%;border:0px' morerecords='0'>
                                                        <TBODY>
                                                            <TR>
                                                                <TD select='none' style='COLOR: #999999; BORDER:0px' align='middle' height='100%' width='100%'>Sem classificação</TD>
                                                            </TR>
                                                        </TBODY>
                                                    </TABLE>
                                                </TD>
                                            </TR>
                                    <%
                                    }
                                    else
                                    {
                                        %>
                                        <TABLE id='g3104_body' container='1' style='' cellpadding='0' cellspacing='0'  mode=''  options=''  letter_field=''  class='gBodyLines_std'>
                                            <COLGROUP/>
                                            <COL width='15'/>
                                            <COL width='20'/>
                                            <COL />
                                        <%
                                        boolean checked = false;
                                        String[] values;
                                        long classBoui;
                                        String groupS = null;
                                        ArrayList auxL = null;
                                        ArrayList auxV = null;
                                        boolean last = false;

                                        for (int i = 0; i < rClassObj.size(); i++) 
                                        {
                                            auxObj = (boObject)rClassObj.get(i);
                                            groupS = (String)rGroupSeq.get(i);
                                            checked = rCheckeds.contains(String.valueOf(auxObj.getBoui()));
                                            auxL = (ArrayList)groupClass.get(String.valueOf(auxObj.getBoui()+"_"+groupS+"_labels") );
                                            auxV = (ArrayList)groupClass.get(String.valueOf(auxObj.getBoui()+"_"+groupS+"_values") );
                                            %>
                                                <TR id='iClass__<%=auxObj.getBoui()%>' valign="bottom"> 
                                            <%
                                            if(checked)
                                            {
                                                %>
                                                    <TD class='gCell_std'><INPUT class='rad' type='radio' onClick='onCheckBoxClick(<%=auxObj.getBoui()%>,"<%=groupS%>","checkBox_<%=auxObj.getBoui()%>_<%=groupS%>");' name='radio' id='checkBox_<%=auxObj.getBoui()%>_<%=groupS%>'/></TD>
                                                <%
                                            } 
                                            else
                                            {
                                                %>
                                                    <TD class='gCell_std'><INPUT class='rad' type='radio' onClick='onCheckBoxClick(<%=auxObj.getBoui()%>,"<%=groupS%>","checkBox_<%=auxObj.getBoui()%>_<%=groupS%>");' name='radio' id='checkBox_<%=auxObj.getBoui()%>_<%=groupS%>'/></TD>
                                                <%
                                            }
                                                %>
                                                    <TD class='gCell_std'><IMG src='resources/none.gif' height=5 width=5 /></TD>
                                                    <TD class='gCell_std'>
                                                        <table cellpadding="2" cellspacing="0">
                                                            <tr>
                                                                <td align='left'> <B>Classificação:</B></TD>
                                                                <td><%=auxObj.getCARDID()%></TD>
                                                            </tr>
                                                <% 
                                            for (int j = 0;auxL!=null && j < auxL.size(); j++) 
                                            {
                                                %>
                                                            <TR id='iClass__<%=auxObj.getBoui()%>'>
                                                                <TD align='left'> <B><%=(String)auxL.get(j)%></B></TD>
                                                                <TD><%=(String)auxV.get(j)%></TD>
                                                            </TR>
                                                <%
                                            }
                                            %>
                                                </table>
                                                </TD>
                                            </TR>
                                            <%
                                        }
                                    }
                                    %>
                                        </TABLE>
                                        </DIV>
                                    </TD>
                                </TR>
                            </TBODY>
                        </TABLE>
                    </DIV>
                </TD>
            </TR>
        </TABLE>
</body>
</html>
<%
} finally {
    boctx.close();DOCLIST.releseObjects(boctx);
}
%>
