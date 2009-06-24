<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>



<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

boolean initPage=true;
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");

    try {
        boSession bosession = (boSession)request.getSession().getAttribute("boSession");
        if(bosession== null) {
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
  
    String idbolist;
    String[] ctrl;
    
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    
    
    
    
    String sid=request.getParameter("sid");
    String programBoui = request.getParameter("programBoui");
    String keys  =request.getParameter("aKeys");
    boolean hasKeys = false;
    if(keys != null && !"".equals(keys))
    {
        hasKeys = true;
    }
    String templateBoui  =request.getParameter("template");
    
    
    boDefHandler bodefObject = null;
    ArrayList r = new ArrayList();
    String[] k = keys.split(";");
    for (int i = 0; i < k.length; i++) 
    {
        r.add(k[i]);
    }
    
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    idbolist=ctrl[1];
    
           
%>
<% if( true ){%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>WorkFlow Designer - Helper for CallProgram</title>

<style>
 @import url('ieThemes/0/global/ui-global.css');
 
 @import url('ieLibrary/form/form.css');
 @import url('ieThemes/0/form/ui-form.css');
 @import url('xeo.css');
 
 
 </style>

</head>

<script LANGUAGE="javascript" SRC="xeo.js"></script>
<script LANGUAGE="javascript" SRC="ieLibrary/wkfl/element.js"></script>
<script LANGUAGE="javascript" SRC="templates/form/std/jsObjects.js"></script>
</head>
<script>

TREE_TEMA_DIR = 'ieThemes/0/tree/';
WKFL_TEMA_DIR = 'ieThemes/0/wkfl/';
TREEROW_COLOR_SEL ="#DAE6E9";

<% if(!hasKeys)
{%>
var keys = [];
<%
}
else
{
%>
var keys = "<%=keys%>".split(";");
<%
}
%>
var _hsos=[];
var _hso=null;
var sid=<%=sid%>;
var ii=0;
function getElement(e){
  if(e&&e.target) return e.target;
  return window.event.srcElement;
}
 
function getToElement(e){
  if(e&&e.target) return e.relatedTarget;
  return window.event.toElement;
}

function getRuntimeStyle(o){
  if(o.runtimeStyle) return o.runtimeStyle;
  else return o.style;
}


function so(id){
  if(!_hsos[id]){
   _hsos[id]=new Object();	
   _hsos[id].hb=document.getElementById(id+'_body');
   _hsos[id].id=id;
   }
   _hso=_hsos[id];
}



document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA" &&  !(s=="DIV" && event.srcElement.contentEditable)) event.returnValue = false;}
 
function init()
{  
  var htm =[];
  var j=0;
  var rkeys = document.getElementById("keys");
  <%if(hasKeys)
  {
  %>
    document.getElementById('noKeys').style.display = 'none';
    document.getElementById('hasKeys').style.display = '';
  <%
  }
  else
  {
  %>
    document.getElementById('noKeys').style.display = '';
    document.getElementById('hasKeys').style.display = 'none';
  <%
  }
  %>
  for ( var i=0 ; i < keys.length ; i++ )
  {
    if(keys[i] != "")
    {
        var r = rkeys.insertRow();
        r.key=keys[i];
        var c = r.insertCell();
        
        c.innerHTML ="<img  title='Clique para eliminar esta linha' onclick='deleteLine(\"" + keys[i] + "\")' style='' border='0' src='ieThemes/0/wkfl/key_delete.gif' width='10' height='10'/>";
        var c = r.insertCell();
        c.innerHTML = "<span>"+keys[i]+"</span>";
    }
  }
  var t = document.getElementById("tdTemplate");
  t.innerHTML =
    createDetachFieldLookup(
			"<%=(templateBoui == null || "".equals(templateBoui)) ? "":templateBoui%>",				//Value
			"template",		//Name
			"template",	//id
			"Ebo_Template",			//listOfValidObjects
			"Ebo_Template",				//objectName
			"Modelo",			//objectLabel
			<%=DOC.getDocIdx()%>,						//docID 
			"single",			//lookupStyle->single or multi
			1,			//tabIndex
			false ,					//isDisabled
			true, 					//isVisible
            "newTemplate(\""+sid+"\")"
			);
}

function add()
{
    var elem = document.getElementById('keyInsert');
    if(elem.value.length > 0)
    {
        var found = false;
        for(var j = 0; j < keys.length; j++)
        {
            if(keys[j] == elem.value)
            {
                found = true;
            }
        }
        if(!found)
        {
            var i = keys.length;
            keys[i] = elem.value;
            var rkeys = document.getElementById("keys");
            var r = rkeys.insertRow();
            r.key=elem.value;
            var c = r.insertCell();
              
            
            c.innerHTML ="<img  title='Clique para eliminar esta linha' onclick='deleteLine(\"" + keys[i] + "\")' style='' border='0' src='ieThemes/0/wkfl/key_delete.gif' width='10' height='10'/>";
            var c = r.insertCell();
            c.innerHTML = "<span>"+elem.value+"</span>";
            elem.value='';
            document.getElementById('noKeys').style.display = 'none';
            document.getElementById('hasKeys').style.display = '';
        }
        else
        {
            alert('Palavra já existe.');
        }
    }
    else
    {
        alert('Introduza a palavra chave.');
    }
}
function deleteLine(word)
{
    for(var j = 0; j < keys.length; j++)
    {
        if(keys[j] == word)
        {
            var rkeys = document.getElementById("keys");
            rkeys.deleteRow(j);
            if(rkeys.rows.length > 0)
            {
                var auxKeys = [];
                var k = j;
                for(var i = 0; i < keys.length; i++)
                {
                    if(i < j)
                    {
                        auxKeys[i] = keys[i];
                    }
                    else if (i >= j && (k+1) < keys.length)
                    {
                        auxKeys[i] = keys[k + 1];
                        k++;
                    }
                }
                keys = auxKeys;
            }
            else
            {
                keys = []; 
                document.getElementById('noKeys').style.display = '';
                document.getElementById('hasKeys').style.display = 'none';
            }
        }
    }
}

</script>


<body scroll="no" onload="init()">
<form id="wForm">
    <TABLE cellSpacing='0' cellPadding='1'  >
        <colgroup>
        <col width="70%"/>
        <col width="30%"/>
        <tbody>
        <tr>
            <td colspan=2>
                <table>
                    <tr id='trtemplate'>
                    <td>Modelo</td>
                    <td  id ='tdTemplate'>&nbsp;</td></tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan=2>
                <div style='font-weight:bold'>Palavras Chaves</div>
            </td>
        </tr>
        <tr>
            <td>
               <input class='text' value='' id='keyInsert' maxlength='50' name = 'keyInsert'>
            </td>
            <td>
               <button id='entrRapida' onclick='add();' tabindex='91'>Adicionar</button>
            </td>
        </tr>
        <tr id="noKeys" style='display:none'>
            <td colspan=2>
                <table cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;color:#000000;width:100%">
                    <td><span>Não existem palavras chaves.</span></td>
                </table>
            </td>
        </tr>
        <tr id="haskeys" style='display:none'>
            <table id='keys' cellSpacing="0" cellPadding="0" style="table-layout:fixed;color:#000000;width:100%">
                <col width=30px/>
                <col width=100%/>
            </table>
        </tr>
    </TABLE>
</form>
<FORM name='boFormSubmit' action="" method='post'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='boFormLookup' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' value='<%=programBoui%>' name='programBoui' />
    <INPUT type='hidden' value='<%=IDX%>' name='clientIDX' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
</FORM>


</FORM>
</BODY></HTML>
<% } %>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
