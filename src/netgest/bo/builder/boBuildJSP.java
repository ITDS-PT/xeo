/*Enconding=UTF-8*/
package netgest.bo.builder;

import com.ibm.regex.REUtil;
import com.ibm.regex.RegularExpression;

import netgest.bo.boConfig;

import netgest.bo.controller.basic.BasicPresentation;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefViewer;

import netgest.bo.runtime.EboContext;

import netgest.utils.StringUtils;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;
//import netgest.utils.tools;

import oracle.xml.parser.v2.XMLDocument;

import netgest.bo.system.Logger;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.io.*;

import java.net.URLEncoder;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import netgest.bo.controller.*;


public final class boBuildJSP {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.builder.boBuildJSP");
    private static final byte TYPE_EDIT = 0;
    public static final byte TYPE_LIST = 1;
    private static final byte TYPE_QUICKVIEW = 2;
    private static final String[] setAttributeTabIndex = { "button" };
    EboContext p_eboctx;
    private byte p_paneis = 0;
    private boDefHandler p_bodef;
    private String p_jspName;
    private String p_url_to_deploy;
    private String p_url_proto;
    private String p_jspNameRelative;
    private Vector xatts = new Vector();
    private String p_formName;
    private String p_path;
    private boolean p_haveAreas = false;
    private byte p_typeForm;
    private String lastAreaId;
    private boolean p_iswizard = false;
    private boolean p_isFirstTable = true;

    public boBuildJSP() {
        boConfig bcfg = (new boConfig());
        p_url_to_deploy = bcfg.getDeployJspDir();

        //       p_url_proto=bcfg.getTemplatesDir();
        p_url_proto = bcfg.getDeployJspDir();

        //EboContext eboctx
        //  p_eboctx=eboctx;
    }

    public void generate(boDefHandler bodef) {
        p_bodef = bodef;

        boDefViewer[] vi = bodef.getBoViewers();

        for (int i = 0; (vi != null) && (i < vi.length); i++) {
            ngtXMLHandler[] frms = vi[i].getForms();

            for (int j = 0; (frms != null) && (j < frms.length); j++) {
                buildjsp(vi[i], frms[j]);
            }
        }
    }

    private void buildjsp(boDefViewer viewer, ngtXMLHandler form) {
        try {
            //StringBuffer jspName;
            //jspName=new StringBuffer();
            String aux;
            String encoding = (new boConfig()).getEncoding();
            p_formName =form.getAttribute("name");
            //jspName.append(this.p_url_to_deploy).append(p_bodef.getBoName()).append('_').append(viewer.getNodeName()).append(form.getNodeName()).append(".jsp");
            p_jspNameRelative = p_bodef.getBoName() + "_" +
                viewer.getViewerName() + p_formName + ".jsp";
            if("wizard".equalsIgnoreCase( p_formName ))
            {
                p_iswizard = true;
                p_isFirstTable = true;
            }
            else
            {
                p_iswizard = false;
            }

            p_haveAreas = false;

            if ((form.getAttribute("buildjsp") != null) &&
                    form.getAttribute("buildjsp").equalsIgnoreCase("no"))
            {
                return;
            }

            if (((form.getAttribute("formtype") != null) &&
                    form.getAttribute("formtype").equalsIgnoreCase("EDIT")) ||
                    p_formName.equalsIgnoreCase("EDIT") ||
                    p_formName.equalsIgnoreCase("WIZARD")) {
                p_typeForm = this.TYPE_EDIT;
            } else {
                p_typeForm = this.TYPE_LIST;
            }

            String bridgeName = form.getAttribute("forBridge");
            boDefAttribute bridgeAtr = null;
            boDefHandler runtimeBodef = p_bodef;
            boDefAttribute atrdef = null;

            if (bridgeName != null && p_bodef.getAttributeRef(bridgeName) != null ) {
                //jspName.append(p_bodef.getName()).append('_').append(atr.getName()).append('_').append(xvName).append( (String)xExpr.get(1) ).append(".jsp");
                String typef = (p_typeForm == this.TYPE_EDIT) ? "edit" : "list";
                bridgeAtr = p_bodef.getAttributeRef(bridgeName);
                runtimeBodef = bridgeAtr.getReferencedObjectDef();

                atrdef = p_bodef.getAttributeRef(form.getAttribute("forBridge"));

                if (p_formName.endsWith("_" + typef)) {
                    p_jspNameRelative = p_bodef.getName() + "_" +
                        atrdef.getName() + "_" + viewer.getViewerName() + typef +
                        ".jsp";
                } else {
                    p_jspNameRelative = p_bodef.getName() + "_" +
                        atrdef.getName() + "_" + viewer.getViewerName() +
                        p_formName.substring(p_formName.indexOf("_") + 1) +
                        ".jsp";
                }
            }

            String toBuild = "";

            ngtXMLHandler[] childs = form.getChildNodes();
            p_paneis = 0;

            XMLDocument xBody;
            Element xnode;
            xBody = new XMLDocument();
            xnode = xBody.createElement("form");

            //<body onload=\"<%=DOC.getActionOnClient()%>\">\n
            xnode.setAttribute("class", "objectForm");
            xnode.setAttribute("name", "boForm");
            xnode.setAttribute("id", "#BEGIN#=IDX#END#");

            xBody.appendChild(xnode);

            Hashtable codeController = new Hashtable();
            Hashtable importController = new Hashtable();

            for (int i = 0; i < childs.length; i++) {
                p_path = viewer.getViewerName() + ".forms." +
                    p_formName;

                if ("code".equalsIgnoreCase(childs[i].getNodeName())) {
                    codeController.put(childs[i].getAttribute("intersectPoint"),
                        childs[i].getText());

                    ngtXMLHandler[] imports = childs[i].getChildNodes();

                    for (int j = 0; j < imports.length; j++) {
                        if ("imports".equalsIgnoreCase(imports[j].getNodeName())) {
                            ngtXMLHandler[] importChild = imports[j].getChildNodes();

                            for (int k = 0; k < importChild.length; k++) {
                                if ("import".equalsIgnoreCase(
                                            importChild[k].getNodeName())) {
                                    if (!importController.contains(
                                                importChild[k].getText())) {
                                        importController.put(importChild[k].getText(),
                                            importChild[k].getText());
                                    }
                                }
                            }
                        }
                    }
                } else {
                    recursiveForm(xBody, xnode, viewer, childs[i], "");
                }
            }

            String b = ngtXMLUtils.getXML(xBody);

            //refreshFrame
            //<% if(!(request.getParameter("toClose") != null && !DOC.haveErrors())) {%>
            //<iframe style=\"display:none\" id = refreshframe src=\"__refresh.jsp?IDX=<%=IDX%>&BOUI=<%=BOI.getBoui()%>\"></iframe>
            StringBuffer refreshFrameCode = new StringBuffer();
            refreshFrameCode.append("<% if(BOI != null) { %>");
            refreshFrameCode.append("\n");
            refreshFrameCode.append(
                "<iframe style=\"display:none\" id = refreshframe src=\"__refresh.jsp?docid=<%=IDX%>&BOUI=<%=BOI.getBoui()%>\"></iframe>");
            refreshFrameCode.append("\n");
            refreshFrameCode.append("<% } %>");
            refreshFrameCode.append("\n");

/*
            toBuild = refreshFrameCode.toString() + b;
            toBuild = tools.replacestr(toBuild, "#BEGIN_INCLUDE#",
                    " <jsp:include ");
            toBuild = tools.replacestr(toBuild, "#END_INCLUDE#", " />");

            toBuild = tools.replacestr(toBuild, "#END_TAG#", ">");
            toBuild = tools.replacestr(toBuild, "#BEGIN_TAG#", "<");
            toBuild = tools.replacestr(toBuild, "#BEGIN_ANCHOR#", "<a ");

            toBuild = tools.replacestr(toBuild, "#BEGIN#", "<%");
            toBuild = tools.replacestr(toBuild, "#END#", "%>");
            toBuild = tools.replacestr(toBuild, "<JSP_EXPRESSION>", "");
            toBuild = tools.replacestr(toBuild, "</JSP_EXPRESSION>", "");
            toBuild = tools.replacestr(toBuild, "#AMP#", "&");
            toBuild = tools.replacestr(toBuild, "#QUOT#", "\"");
*/
            toBuild = refreshFrameCode.toString() + b;
            toBuild =toBuild.replaceAll("#BEGIN_INCLUDE#"," <jsp:include ");
            toBuild = toBuild.replaceAll("#END_INCLUDE#"," />");

            toBuild = toBuild.replaceAll("#END_TAG#", ">");
            toBuild = toBuild.replaceAll("#BEGIN_TAG#", "<");
            toBuild = toBuild.replaceAll( "#BEGIN_ANCHOR#", "<a ");

            toBuild = toBuild.replaceAll( "#BEGIN#", "<%");
            toBuild = toBuild.replaceAll( "#END#", "%>");
            toBuild = toBuild.replaceAll( "<JSP_EXPRESSION>", "");
            toBuild = toBuild.replaceAll( "</JSP_EXPRESSION>", "");
            toBuild = toBuild.replaceAll( "#AMP#", "&");

            //teste
            toBuild = StringUtils.replacestr(toBuild, "#AMP#", "&");

            toBuild = toBuild.replaceAll( "#QUOT#", "\"");

            //var y=new RegExp(deli1+"(.*?)"+deli2,'gi');
            //var xstr=xstr.replace(y,'textotosubsituto');
            //         RegularExpression xRE=new RegularExpression(">(.*?)<img");
            RegularExpression xRE = new RegularExpression(">(.\\s*?)<img", "s");
            toBuild = REUtil.substitute(xRE, "><img", true, toBuild);

            xRE = new RegularExpression(">(.\\s*?)</td", "s");
            toBuild = REUtil.substitute(xRE, "></td", true, toBuild);

            xRE = new RegularExpression("SPAN>(.\\s*?)<", "s");
            toBuild = REUtil.substitute(xRE, "SPAN><", true, toBuild);

            xRE = new RegularExpression(">(.\\s*?)<SPAN", "s");
            toBuild = REUtil.substitute(xRE, "><SPAN", true, toBuild);

            /*
                        </SPAN><%}%><%if
            */
            xRE = new RegularExpression("</SPAN><%}%>(.\\s*?)<%if", "s");
            toBuild = REUtil.substitute(xRE, "</SPAN><%}%><%if", true, toBuild);

            //                                   <TD>
            //                           <%if (DOC.hasCategoryRights("identification")){ %>
            xRE = new RegularExpression("<TD>(.\\s*?)<%if", "s");
            toBuild = REUtil.substitute(xRE, "<TD><%if", true, toBuild);

            //final se´r assim
            //    xRE=new RegularExpression(">(.\\s*?)<","s");
            //   toBuild=REUtil.substitute(xRE,"><",true,toBuild);
            //
            //BUILD JSP HEADER
            StringBuffer JSPHeader = new StringBuffer();

            JSPHeader.append(
                "<%@ page contentType=\"text/html;charset=" + encoding + "\"%>");
            JSPHeader.append("\n");
            JSPHeader.append("<%@ page import=\"java.util.*\"%>\n");
            JSPHeader.append("<%@ page import=\"netgest.bo.*\"%>\n");
            JSPHeader.append("<%@ page import=\"netgest.bo.dochtml.*\"%>\n");
            JSPHeader.append("<%@ page import=\"netgest.bo.runtime.*\"%>\n");
            JSPHeader.append("<%@ page import=\"netgest.bo.def.*\"%>\n");
            JSPHeader.append("<%@ page import=\"netgest.bo.security.*\"%>\n");
            JSPHeader.append(
                "<%@ page import=\"netgest.utils.*,netgest.bo.system.*\"%>\n");

            // import generate
            if (!importController.isEmpty()) {
                JSPHeader.append("<%@ page import=\"");

                Enumeration e = importController.elements();

                while (e.hasMoreElements()) {
                    JSPHeader.append((String) e.nextElement());

                    if (e.hasMoreElements()) {
                        JSPHeader.append(",");
                    }
                }

                JSPHeader.append("\"%>\n");
            }

            JSPHeader.append("\n");
            JSPHeader.append(
                "<jsp:useBean id=\"DOCLIST\" scope=\"session\" class=\"netgest.bo.dochtml.docHTML_controler\"></jsp:useBean>");
            JSPHeader.append("\n");
            JSPHeader.append("<%");
            JSPHeader.append("\n");

            ///JSPHeader.append("EboContext boctx = (EboContext)request.getAttribute(\"a_EboContext\");\nboolean initPage=true;\n");
            JSPHeader.append("response.setDateHeader (\"Expires\", -1);\n");

            JSPHeader.append(
                "EboContext boctx = (EboContext)request.getAttribute(\"a_EboContext\");\n");
            JSPHeader.append("try {\n");
            JSPHeader.append("boolean masterdoc=false;\n");
            JSPHeader.append(
                "if( request.getParameter(\"docid\")==null ||request.getParameter(\"masterdoc\")!=null ){\n");
            JSPHeader.append("        masterdoc=true;\n");
            JSPHeader.append("}\n");

            JSPHeader.append(
                "boSession bosession = (boSession)request.getSession().getAttribute(\"boSession\");\n");
            JSPHeader.append("if(bosession== null) {\n");
            JSPHeader.append(
                "    response.sendRedirect(\"login.jsp?returnPage=" +
                p_jspNameRelative.toLowerCase() + "\");\n");
            JSPHeader.append("    return;\n");
            JSPHeader.append("}\n");
            JSPHeader.append("if(boctx==null) {\n");
            JSPHeader.append(
                "    boctx = bosession.createRequestContext(request,response,pageContext);\n");
            JSPHeader.append(
                "    request.setAttribute(\"a_EboContext\",boctx);\n");
            JSPHeader.append("}\n");

            // Extra Code : beforeProcess
            JSPHeader.append((codeController.get("beforeProcess") != null)
                ? codeController.get("beforeProcess") : "");
            JSPHeader.append("\n");

            //JSPHeader.append("if(request.getAttribute(\"initPageRequest\")==null && request.getParameter(\"notInit\")==null ){    request.setAttribute(\"initPageRequest\",\"Y\")\n;    initPage=true\n;}else {    initPage=false;\n}int IDX;int cvui;\nboDefHandler bodef;\nboDefAttribute atr;\nString idbolist;\nString[] ctrl;\n");
            JSPHeader.append(
                "int IDX;int cvui;\nboDefHandler bodef;\nboDefAttribute atr;\nString idbolist;\nString[] ctrl;\n");

            //JSPHeader.append("docParameter attr;\n");
            JSPHeader.append("docHTML_section sec;\n");
            JSPHeader.append("docHTML_grid grid;\n");

            //JSPHeader.append("docHTML_groupGrid gridG;\n");
            JSPHeader.append("Hashtable xattributes;\n");
            JSPHeader.append("ctrl= DOCLIST.processRequest(boctx);\n");
            JSPHeader.append("IDX= ClassUtils.convertToInt(ctrl[0],-1);\n");
            JSPHeader.append(
                "String parent_boui=request.getParameter(\"parent_boui\");\n");
            JSPHeader.append("idbolist=ctrl[1];\n");

            //            JSPHeader.append(\"}\n");
            JSPHeader.append("docHTML DOC = DOCLIST.getDOC(IDX);\n");

            //            aux=form.getAttribute("width");
            //            if(aux!=null) JSPHeader.append("DOC.width=\""+aux+"\";\n");
            //            aux=form.getAttribute("height");
            //            if(aux!=null) JSPHeader.append("DOC.height=\""+aux+"\";\n");
            //
            JSPHeader.append(
                "boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);\n");

            JSPHeader.append("boObject BOI;\n");
            JSPHeader.append(
                "String method=request.getParameter( \"method\" );\n");
            JSPHeader.append(
                "String inputMethod=request.getParameter( \"method\" );\n");
            JSPHeader.append(
                "String requestedBoui=request.getParameter( \"boui\" );\n");

            JSPHeader.append("if ( currObjectList == null ) BOI=null;\n");
            JSPHeader.append("else BOI=currObjectList.getObject();\n");

            JSPHeader.append(
                "if(request.getParameter(\"objectBoui\")!=null){\n");
            JSPHeader.append(
                "if(request.getParameter(\"objectBoui\").indexOf(';') < 0)\n");
            JSPHeader.append(
                "{BOI = boObject.getBoManager().loadObject(boctx, Long.parseLong(request.getParameter(\"objectBoui\")));\n");
            JSPHeader.append(
                "long[] a_boui = {BOI.getBoui()};\n");
            JSPHeader.append(
                "currObjectList = boObjectList.list(boctx, BOI.getName(),a_boui);\n");
            JSPHeader.append(
                "if(!currObjectList.haveBoui(a_boui[0]))\n");
            JSPHeader.append(
                "currObjectList.inserRow(a_boui[0]);}\n");
            JSPHeader.append(
                "else{StringTokenizer st = new StringTokenizer(request.getParameter(\"objectBoui\"), \";\", false);\n");
            JSPHeader.append(
                "String newB = null; while(st.hasMoreElements()){\n");
            JSPHeader.append(
                "newB = st.nextToken();\n");
            JSPHeader.append(
                "if(newB != null && newB.length() > 1)    {\n");
            JSPHeader.append(
                "currObjectList.inserRow(Long.parseLong(newB));    } BOI = boObject.getBoManager().loadObject(boctx, Long.parseLong(newB));}}}\n");



            JSPHeader.append("\n DOC.setMasterObject(BOI,masterdoc);\n");

            JSPHeader.append(
                " DOC.getController().getNavigator().setRoot( BOI, masterdoc, request);\n");

            JSPHeader.append("\nString redirectUrl = DOC.getController().getNavigator().processPathRequest( BOI, masterdoc, request);");
            JSPHeader.append("\nif(!DOC.haveErrors() && BOI != null && BOI.getSendRedirect() == null && redirectUrl != null)\n");
            JSPHeader.append("{\n");
            JSPHeader.append("    BOI.setSendRedirect(redirectUrl);\n");
            JSPHeader.append("}\n");



            JSPHeader.append("\nif(BOI != null && BOI.getSendRedirect() != null){")
                .append("response.sendRedirect( BOI.getSendRedirect() + \"&docid=\" + IDX + \"&myIDX=\" + IDX  );")
                .append("BOI.cleanSendRedirect();")
                .append("return;")
            .append("}\n");

            JSPHeader.append("if( method !=null ) { \n");
            JSPHeader.append(
                "   if( method.equalsIgnoreCase(\"newfromtemplate\") || method.equalsIgnoreCase(\"forward\") ||");
            JSPHeader.append(" method.equalsIgnoreCase(\"execute\") || method.equalsIgnoreCase(\"reply\") || ")
                .append("method.equalsIgnoreCase(\"replyAll\")|| method.equalsIgnoreCase(\"duplicate\") ")
                .append("|| method.equalsIgnoreCase(\"new\") ) { \n");
            JSPHeader.append("       method=\"edit\";\n");
            JSPHeader.append("       requestedBoui = \"\"+BOI.getBoui();\n");
            JSPHeader.append("    }\n");
            JSPHeader.append("}\n");


//            JSPHeader.append("\n DOC.setMasterObject(BOI,masterdoc);\n");


//            JSPHeader.append(
//                " DOC.getController().getNavigator().setRoot( BOI, masterdoc, request);\n");


//            JSPHeader.append(
//                " String redirectUrl = DOC.getController().getNavigator().processPathRequest( BOI, masterdoc, request);\n");
//            JSPHeader.append(
//                " if ( redirectUrl != null && !DOC.haveErrors() )\n");
//            JSPHeader.append(" {\n");
//            JSPHeader.append("     response.sendRedirect( redirectUrl  );\n");
//            JSPHeader.append("     return;\n");
//            JSPHeader.append(" }\n");

            //JSPHeader.append("\nDOC.getController().load(request,response);\n\n");

            if (p_typeForm == this.TYPE_EDIT) {
                JSPHeader.append(
                    "if ((BOI==null && securityRights.hasRights(boctx,\"" +
                    p_bodef.getBoName() + "\")) || \n");
                JSPHeader.append(
                    "(BOI!=null && BOI.exists() && securityRights.hasRights(boctx,\"" +
                    p_bodef.getBoName() +
                    "\") && securityOPL.canRead(BOI)) || \n");
                JSPHeader.append(
                    "(BOI!=null && !BOI.exists() && securityRights.hasRights(boctx,\"" +
                    p_bodef.getBoName() + "\",securityRights.ADD)) \n");

                if (p_bodef.getModifyProtocol() != null) {
                    JSPHeader.append(
                        " || (BOI!=null && !BOI.exists() && securityRights.hasRights(boctx,\"" +
                        p_bodef.getModifyProtocol() +
                        "\",securityRights.ADD)) \n");
                }

                JSPHeader.append(") {\n");
            } else {
                JSPHeader.append("if(request.getParameter(\"look_object\")==null || securityRights.hasRights(boctx,request.getParameter(\"look_object\"))){");
            }

            if (p_typeForm == this.TYPE_EDIT) {
                JSPHeader.append(
                    "if (BOI!=null && BOI.exists() && !BOI.isEnabledforRequest &&(!securityRights.hasRights(boctx,\"" +
                    p_bodef.getBoName() +
                    "\",securityRights.WRITE) ||  !securityOPL.canWrite(BOI) && !BOI.isEnabledforRequest)) { \n");
                JSPHeader.append("  BOI.setDisabled(); }\n");
                JSPHeader.append(
                    "else if (BOI!=null && !BOI.exists() && securityRights.hasRights(boctx,\"" +
                    p_bodef.getModifyProtocol() +
                    "\",securityRights.WRITE)) { \n");
                JSPHeader.append("  BOI.setEnabled(true); }\n");

                //                JSPHeader.append("if( request.getParameter( \"method\" )!=null && (request.getParameter( \"method\" ).equalsIgnoreCase(\"new\") || \n");
                //                JSPHeader.append("                                                  request.getParameter( \"method\" ).equalsIgnoreCase(\"duplicate\")))\n");
                //                JSPHeader.append("{\n");
                //                JSPHeader.append("    StringBuffer url = new StringBuffer(\"?method=edit&boui=\"+BOI.getBoui()+\"&docid=\"+IDX);\n");
                //                JSPHeader.append("    if ( masterdoc ) url.append(\"&masterdoc=true\");\n");
                //                JSPHeader.append("    Enumeration oEnum = request.getParameterNames();\n");
                //                JSPHeader.append("    while( oEnum.hasMoreElements() )\n");
                //                JSPHeader.append("    {\n");
                //                JSPHeader.append("        String pname = oEnum.nextElement().toString();\n");
                //                JSPHeader.append("        if( !pname.equalsIgnoreCase(\"method\") && !pname.equalsIgnoreCase(\"docid\") )\n");
                //                JSPHeader.append("        {\n");
                //                JSPHeader.append("            url.append(\"&\").append( pname ).append( \"=\" ).append( java.net.URLEncoder.encode( request.getParameter( pname ) ) );\n");
                //                JSPHeader.append("        }\n");
                //                JSPHeader.append("    }\n");
                //                JSPHeader.append("    response.sendRedirect( request.getRequestURI()+url.toString());\n");
                //                JSPHeader.append("    return;\n");
                //                JSPHeader.append("}\n");
                JSPHeader.append("if ( BOI.exists() ){ \n");
                JSPHeader.append(" BOI.markAsRead();\n");
                JSPHeader.append("} \n");

                JSPHeader.append(
                    "String parent_attribute=request.getParameter(\"parent_attribute\");\n");
                JSPHeader.append(
                    " if( parent_attribute != null && parent_boui != null ) \n" +
                    " { \n"+
                    "   boObject pObject = boObject.getBoManager().loadObject( boctx, Long.parseLong( parent_boui ) ); \n"+
                    "   AttributeHandler oParentAttribute = pObject.getAttribute( parent_attribute );\n" +
                    "   if( oParentAttribute != null )\n" +
                    "   {\n"+
                    "       if( oParentAttribute.disableWhen() && BOI.isEnabled ) \n"+
                    "       {\n"+
                    "           BOI.setDisabled();\n"+
                    "       }\n"+
                    "   }\n"+
                    " \n"+
                    " }\n"+
                    " \n"+
                    " \n"
                    );

            }

            if (p_bodef.getBoName().equalsIgnoreCase("Ebo_Template") &&
                    (p_typeForm == this.TYPE_EDIT)) {
                JSPHeader.append(
                    "if ( request.getParameter(\"toClose\") == null || DOC.haveErrors())\n");
                JSPHeader.append("{\n");

                JSPHeader.append(
                    "String masterObjectBoui=BOI.getAttribute( \"masterObjectClass\" ).getValueString();\n");
                JSPHeader.append(
                    "boObject masterObject= netgest.bo.runtime.boObject.getBoManager().loadObject( DOC.getEboContext(),netgest.utils.ClassUtils.convertToLong(masterObjectBoui ));\n");
                JSPHeader.append(
                    "String nameCls=masterObject.getAttribute(\"name\").getValueString();\n");

                //JSPHeader.append("if ( BOI.getParameter(\"relatedObjBOUI\")==null ){\n");
                JSPHeader.append(
                    "if ( request.getParameter(\"fromObj\")==null ){\n");
                JSPHeader.append("String __url;\n");
                JSPHeader.append(
                    "if ( BOI.getParameter(\"relatedObjBOUI\")!=null )\n");
                JSPHeader.append("{\n");
                JSPHeader.append(
                    "    __url=OracleJspRuntime.toStr(nameCls.toLowerCase()+\"_generaledit.jsp\");\n");
                JSPHeader.append(
                    "    __url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {\"edit_template_boui\",\"method\",\"boui\",\"docid\" } ,\n");
                JSPHeader.append(
                    "    new String[] {OracleJspRuntime.toStr(BOI.getBoui()), \"EDIT\",BOI.getParameter(\"relatedObjBOUI\"),OracleJspRuntime.toStr(IDX) } );\n");

                JSPHeader.append("}\n");
                JSPHeader.append("else\n");
                JSPHeader.append("{\n");
                JSPHeader.append(
                    "    __url=OracleJspRuntime.toStr(nameCls.toLowerCase()+\"_generaledit.jsp\");\n");
                JSPHeader.append(
                    "    __url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {\"edit_template_boui\", \"boql\",\"docid\" } ,\n");
                JSPHeader.append(
                    "    new String[] {OracleJspRuntime.toStr(BOI.getBoui()), OracleJspRuntime.toStr(\"select \"+nameCls+\" where 0=1\"),OracleJspRuntime.toStr(IDX) } );\n");
                JSPHeader.append("}\n");

                // Forward
                JSPHeader.append("    out.clear();\n");
                JSPHeader.append("    pageContext.forward( __url);\n");
                JSPHeader.append("    return;\n");
                JSPHeader.append("}\n");
                JSPHeader.append("}\n");
            }

            /*
            JSPHeader.append("String[] attributesToRender=new String[");
            JSPHeader.append(xatts.size());
            JSPHeader.append(']');
            JSPHeader.append(';');
            JSPHeader.append('\n');
            for (int i = 0; i < xatts.size(); i++)  {
                JSPHeader.append("attributesToRender[");
                JSPHeader.append(i);
                JSPHeader.append("]=");
                JSPHeader.append('"');
                JSPHeader.append(xatts.get(i));
                JSPHeader.append('"');
                JSPHeader.append(';');
                JSPHeader.append('\n');
            }
            JSPHeader.append("DOC.iniRender(currObjectList,BOI,attributesToRender);\n");


            JSPHeader.append("\n");
            */
            JSPHeader.append(
                " if( currObjectList != null ) currObjectList.first();\n");
            JSPHeader.append("\n");

            // Extra Code : afterProcess
            JSPHeader.append((codeController.get("afterProcess") != null)
                ? codeController.get("afterProcess") : "");

            // JSPHeader.append(buildCode(form));
            JSPHeader.append("\n");
            JSPHeader.append("\n");
            JSPHeader.append("%>");
            JSPHeader.append("\n");

            ///       JSPHeader.append("<% if(initPage){ %>\n");
            JSPHeader.append("<html>\n");
            JSPHeader.append("<head>\n");
            JSPHeader.append(
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\"/>\n");
            JSPHeader.append("<title>\n");
            JSPHeader.append("nbo «" + p_bodef.getBoName() + "»");
            JSPHeader.append("</title>\n");

            JSPHeader.append("\n<%= DOC.getController().getPresentation().writeCSS() %>\n");
            JSPHeader.append("<%= DOC.getController().getPresentation().writeJS() %>\n\n");


//            JSPHeader.append("<%if(\"BasicController\".equals(DOC.getController().getName())){%>\n");
            if(form.getAttribute("headers","version1").equalsIgnoreCase("version2") )
            {
                JSPHeader.append("<%@ include file='boheaders2.jsp'%>\n");
            }
            else
            {
                JSPHeader.append("<%@ include file='boheaders.jsp'%>\n");
            }

//            JSPHeader.append("<%}else{%>\n");
//            JSPHeader.append("<%@ include file='boheaders2.jsp'%>\n");
//            JSPHeader.append("<%}%>\n");

            JSPHeader.append("<script>\n");
            JSPHeader.append("<%=DOC.getScriptToRunOnClient()%>");
            if(p_iswizard)
            {
                JSPHeader.append("  function runStart(){\n");
                JSPHeader.append("     if(document.getElementById(\"refreshframe\") != null)\n");
                JSPHeader.append("     document.getElementById(\"refreshframe\").contentWindow.BindToWizzard('start');\n");
                JSPHeader.append("  }\n");
                JSPHeader.append("  function runPrevious(){\n");
                JSPHeader.append("     if(document.getElementById(\"refreshframe\") != null)\n");
                JSPHeader.append("     document.getElementById(\"refreshframe\").contentWindow.BindToWizzard('buttonPrevious');\n");
                JSPHeader.append("  }\n");
                JSPHeader.append("  function runNext(){\n");
                JSPHeader.append("     if(document.getElementById(\"refreshframe\") != null)\n");
                JSPHeader.append("     document.getElementById(\"refreshframe\").contentWindow.BindToWizzard('buttonNext');\n");
                JSPHeader.append("  }\n");
                JSPHeader.append("  function runEnd(){\n");
                JSPHeader.append("     if(boForm != null)\n");
                JSPHeader.append("     boForm.SaveAndClose(); savePressed(true);wait();\n");
                JSPHeader.append("  }\n");
            }
            JSPHeader.append("</script>\n");

            if (((form.getAttribute("formtype") != null) &&
                    form.getAttribute("formtype").equalsIgnoreCase("EDIT")) ||
                    p_formName.equalsIgnoreCase("EDIT")) {
                String lab = form.getAttribute("label");

                if (lab == null) {
                    JSPHeader.append("<script>");
                    JSPHeader.append("var objLabel=\"<%=BOI.getCARDID()%>\";");
                } else {
                    JSPHeader.append("<%\n");
                    JSPHeader.append("String labelAux = \"").append(lab).append("\";\n");
                    JSPHeader.append("%>\n");
                    JSPHeader.append("<script>");
                    JSPHeader.append(
                        "var objLabel=\"<%=boObject.mergeAttributes(labelAux,BOI)%>\";");
                }

                JSPHeader.append("var objStatus=\"<%=BOI.getSTATUS()%>\";");
            } else {
                JSPHeader.append("<script>var objLabel='Lista de  " +
                    p_bodef.getLabel() + "';");
            }

            //JSPHeader.append("var objDescription='"+p_bodef.getDescription()+"';");
            JSPHeader.append("</script>\n");

            //            JSPHeader.append("<body onload=\"<%=DOC.getActionOnClient()%>\">\n");
            JSPHeader.append("<% String scriptToRun=null;\n");
            JSPHeader.append(
                "if ( request.getParameter(\"boFormSubmitXml\")!= null && ( request.getParameter(\"addToCtxParentBridge\") != null || request.getParameter(\"relatedParentBridge\") != null ))\n");
            JSPHeader.append(" {\n");
            JSPHeader.append(
                "    String look_parentBoui      = request.getParameter(\"ctxParent\");\n");
            JSPHeader.append(
                "    String look_parentAttribute = request.getParameter(\"addToCtxParentBridge\");\n");
            JSPHeader.append(
                "    String clientIDX            = request.getParameter(\"relatedClientIDX\");\n");
            JSPHeader.append(
                "    String relDocid             = request.getParameter(\"ctxParentIdx\");\n");
            JSPHeader.append("if ( look_parentAttribute== null )\n");
            JSPHeader.append(" {\n");
            JSPHeader.append(
                "   look_parentAttribute = request.getParameter(\"relatedParentBridge\");\n");
            JSPHeader.append(
                "   look_parentBoui      = request.getParameter(\"relatedParent\");\n");
            JSPHeader.append(
                "   relDocid             = request.getParameter(\"relatedParentDocid\");\n");
            JSPHeader.append(" }\n");
            JSPHeader.append(
                "    docHTML xd=DOCLIST.getDocByIDX( netgest.utils.ClassUtils.convertToInt(relDocid),DOC.getEboContext());\n");
            JSPHeader.append(
                "    boObject obj=xd.getObject(Long.parseLong(look_parentBoui));\n");
            JSPHeader.append("    StringBuffer toP=new StringBuffer();\n");

            JSPHeader.append("     StringBuffer nameH = new StringBuffer();\n");
            JSPHeader.append(
                "    boDefAttribute atrDef= obj.getBoDefinition().getAttributeRef(look_parentAttribute );\n");
            JSPHeader.append(
                "    nameH.append( obj.getName() ).append( \"__\" ).append( obj.bo_boui ).append(\"__\").append( look_parentAttribute );\n");
            JSPHeader.append("    if(!xd.parentRefresh())\n");
            JSPHeader.append("    {\n");
            JSPHeader.append(
                "        scriptToRun=\"updateLookupAttribute()\";\n");
            JSPHeader.append("    }\n");
            JSPHeader.append("    else\n");
            JSPHeader.append("    {\n");
            JSPHeader.append(
                "        scriptToRun=\"updateLookupAttribute(); callParentRefreshFrame()\";\n");
            JSPHeader.append("    }\n");

            JSPHeader.append(" %>\n");
            JSPHeader.append("<script language=\"javascript\">\n");
            JSPHeader.append("function updateFrame(wFrm)\n");
            JSPHeader.append("{\n");
            JSPHeader.append("   wDoc=wFrm.contentWindow;\n");
            JSPHeader.append("   var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');\n");
            JSPHeader.append("   for(var z=0;  z < wDocfrms.length ; z++)\n");
            JSPHeader.append("   {\n");
            JSPHeader.append("      if (  wDocfrms[z].id == \"inc_<%=nameH%>\" )\n");
            JSPHeader.append("      {\n");

//            JSPHeader.append("            wDocfrms[z].contentWindow.location.reload();\n");
//            JSPHeader.append(
//                "            var xhref=wDocfrms[z].contentWindow.location.href;\n");
//            JSPHeader.append(
//                "            wDocfrms[z].contentWindow.location.href=setUrlAttribute(xhref,'boFormSubmitXml','');\n");
            JSPHeader.append("  wDocfrms[z].contentWindow.submitGrid();\n");
            JSPHeader.append("            return;\n");
            JSPHeader.append("      }\n");
            JSPHeader.append("      else\n");
            JSPHeader.append("      {\n");
            JSPHeader.append("         updateFrame(wDocfrms[z]);\n");
            JSPHeader.append("      }\n");
            JSPHeader.append("   }\n");

            JSPHeader.append("}\n");

            JSPHeader.append(" function updateLookupAttribute()\n");
            JSPHeader.append(" {\n");
            JSPHeader.append("   var windowToUpdate=\"<%=clientIDX%>\";\n");
            JSPHeader.append("   var w=parent.ndl[windowToUpdate];\n");

            JSPHeader.append("    if(w)\n");
            JSPHeader.append("    {\n");
        //    JSPHeader.append("        var ifrm=w.htm.getElementsByTagName('IFRAME');\n");
            JSPHeader.append("        var ifrm=w.htm;\n");

            JSPHeader.append("       var xw;\n");
            //JSPHeader.append("       for(var i=0; i < ifrm.length ; i++)\n");
            //JSPHeader.append("       {\n");
            //JSPHeader.append( "           if ( ifrm[i].id == \"frm$<%=clientIDX%>\" )\n");
            JSPHeader.append( "           if ( ifrm.id == \"frm$<%=clientIDX%>\" )\n");
            JSPHeader.append("           {\n");
            //JSPHeader.append("                updateFrame(ifrm[i]);\n");
            JSPHeader.append("                updateFrame(ifrm);\n");
            JSPHeader.append("           }\n");
            //JSPHeader.append("       }\n");
            JSPHeader.append("   }\n");

            JSPHeader.append(" }\n");
            JSPHeader.append(" function callParentRefreshFrame()\n");
            JSPHeader.append(" {\n");
            JSPHeader.append("   var windowToUpdate=\"<%=clientIDX%>\";\n");
            JSPHeader.append("   var w=parent.ndl[windowToUpdate];\n");

            JSPHeader.append("    if(w)\n");
            JSPHeader.append("    {\n");
            //JSPHeader.append("       var ifrm=w.htm.getElementsByTagName('IFRAME');\n");
            JSPHeader.append("       var ifrm=w.htm;\n");
            //JSPHeader.append("       for(var i=0; i < ifrm.length ; i++)\n");
            //JSPHeader.append("       {\n");
            //JSPHeader.append("           if ( ifrm[i].id == \"frm$<%=clientIDX%>\" )\n");
            //JSPHeader.append("           if ( ifrm.id == \"frm$<%=clientIDX%>\" )\n");
            //JSPHeader.append("           {\n");
            //JSPHeader.append("                var wDocfrms=ifrm[i].contentWindow.document.getElementsByTagName('IFRAME');\n");
            JSPHeader.append("                var wDocfrms=ifrm.contentWindow.document.getElementsByTagName('IFRAME');\n");
            JSPHeader.append("                for(var z=0;  z < wDocfrms.length ; z++)\n");
            JSPHeader.append("                {\n");
            JSPHeader.append("                    if (  wDocfrms[z].id == \"refreshframe\" )\n");
            JSPHeader.append("                    {\n");
            JSPHeader.append("                        wDocfrms[z].contentWindow.refreshValues();\n");
            JSPHeader.append("                    }\n");
            JSPHeader.append("                }\n");
            //JSPHeader.append("           }\n");
            //JSPHeader.append("       }\n");
            JSPHeader.append("   }\n");
            JSPHeader.append("}\n");

            JSPHeader.append("</script>\n");
            JSPHeader.append("<%}%>\n");

            JSPHeader.append("<% \n");
            JSPHeader.append(
                "if ( request.getParameter(\"boFormSubmitXml\")!= null && request.getParameter(\"searchClientIdx\") != null && BOI != null && BOI.exists()){\n");
            JSPHeader.append(
                "  String searchClientIdx=request.getParameter(\"searchClientIdx\");\n");
            JSPHeader.append("  scriptToRun=\"updateObj()\";\n");
            JSPHeader.append("%>\n");

            JSPHeader.append("<script language=\"javascript\">\n");
            /*JSPHeader.append("function updateObj()\n");
            JSPHeader.append("{\n");
            JSPHeader.append("  var w=winmain().dialogArguments;\n");
            JSPHeader.append("  if( w )w.findframe.submitSelectOne2(<%=BOI.bo_boui%>);\n");
            JSPHeader.append(" }\n");*/

            /*
            JSPHeader.append("function updateObj(){\n");
            JSPHeader.append("  var w=parent.ndl[<%=searchClientIdx%>];\n");
            JSPHeader.append("  if( w ){\n");
            JSPHeader.append("      var ifrm=w.htm.getElementsByTagName('IFRAME');\n");
            JSPHeader.append("      var xw;\n");
            JSPHeader.append("      ifrm[0].contentWindow.findframe.submitSelectOne2(<%=BOI.bo_boui%>);\n");
            JSPHeader.append("  }\n");
            JSPHeader.append("}\n");
            */

            JSPHeader.append("function updateObj(){\n");
            JSPHeader.append("  var w=parent.ndl[<%=searchClientIdx%>];\n");
            JSPHeader.append("  if( w ){\n");
            JSPHeader.append("      var ifrm=w.htm;\n");
            JSPHeader.append("      var xw;\n");
            JSPHeader.append("      if(ifrm.contentWindow.resultframe != null)");
            JSPHeader.append("      {");
            JSPHeader.append("          ifrm.contentWindow.findframe.submitBridge2(<%=BOI.bo_boui%>);");
            JSPHeader.append("      }");
            JSPHeader.append("      else if(ifrm.contentWindow.findframe != null)");
            JSPHeader.append("      {");
            JSPHeader.append("          ifrm.contentWindow.findframe.submitSelectOne2(<%=BOI.bo_boui%>);\n");
            JSPHeader.append("      }");
            JSPHeader.append("  }\n");
            JSPHeader.append("}\n");

            JSPHeader.append("</script>\n");

            JSPHeader.append("<%}%>\n");

            JSPHeader.append("<% StringBuffer toP=new StringBuffer();\n");
            JSPHeader.append(
                "if ( request.getParameter(\"boFormSubmitXml\")!= null && request.getParameter(\"actRenderObj\") != null && ( BOI!=null && BOI.exists() ) ){\n");

            JSPHeader.append(
                "  String clientIDX=request.getParameter(\"actIdxClient\");\n");
            JSPHeader.append(
                "  String actRenderObj=request.getParameter(\"actRenderObj\");\n");
            JSPHeader.append(
                "  String actRenderAttribute=request.getParameter(\"actRenderAttribute\");\n");

            JSPHeader.append("  scriptToRun=\"updateLookupAttribute()\";\n");

            JSPHeader.append(
                " int actRenderDocid= netgest.utils.ClassUtils.convertToInt(request.getParameter(\"actRenderDocid\"),-1);\n");
            JSPHeader.append(" boObject obj=null;\n");
            JSPHeader.append(" if ( actRenderDocid != -1 )\n");
            JSPHeader.append(" {\n");
            JSPHeader.append(
                " netgest.bo.dochtml.docHTML docr=DOCLIST.getDocByIDX( actRenderDocid, DOC.getEboContext() );\n");
            JSPHeader.append(" if( docr!= null )\n");
            JSPHeader.append(" {\n");
            JSPHeader.append(
                " obj=docr.getObject(Long.parseLong(actRenderObj));\n");
            JSPHeader.append(" }\n");
            JSPHeader.append(" }\n");
            JSPHeader.append(" else\n");
            JSPHeader.append(" {\n");
            JSPHeader.append(
                " obj=DOC.getObject(Long.parseLong(actRenderObj));\n");
            JSPHeader.append(" }\n");
            JSPHeader.append(" StringBuffer v= new StringBuffer( );\n");
            JSPHeader.append(" StringBuffer nameH = new StringBuffer();\n");
            JSPHeader.append(" AttributeHandler attr=null;\n");
            JSPHeader.append(" if ( obj!= null )\n");
            JSPHeader.append(" {\n");

            JSPHeader.append("  attr= obj.getAttribute(actRenderAttribute);\n");
            JSPHeader.append(
                "  v= new StringBuffer( attr.getValueString() );\n");

            JSPHeader.append("  StringBuffer id = new StringBuffer();\n");
            JSPHeader.append(
                "  boDefAttribute atrDef= obj.getBoDefinition().getAttributeRef(actRenderAttribute );\n");

            JSPHeader.append(
                "  nameH.append( obj.getName() ).append( \"__\" ).append( obj.bo_boui ).append(\"__\").append( actRenderAttribute );\n");
            JSPHeader.append(
                "  id.append(\"tblLook\").append( obj.getName() ).append( \"__\" ).append( obj.bo_boui ).append(\"__\").append( actRenderAttribute );\n");
            JSPHeader.append(
                "  netgest.bo.dochtml.docHTML_renderFields.writeHTML_lookup(\n");
            JSPHeader.append("        toP,\n");
            JSPHeader.append("        obj,\n");
            JSPHeader.append("        attr,\n");
            JSPHeader.append("        v,\n");
            JSPHeader.append("        nameH,\n");
            JSPHeader.append("        id,\n");
            JSPHeader.append("        1,\n");
            JSPHeader.append("        DOC,\n");
            JSPHeader.append("        attr.disableWhen(),\n");
            JSPHeader.append("        !attr.hiddenWhen(),\n");
            JSPHeader.append(
                "        obj.getMode() == boObject.MODE_EDIT_TEMPLATE,false,false,null\n");
            JSPHeader.append("        );\n");
            JSPHeader.append("}\n");

            JSPHeader.append("%>\n");
            JSPHeader.append("<script language=\"javascript\">\n");

            JSPHeader.append("function updateFrame(wFrm)\n");
            JSPHeader.append("{\n");
            JSPHeader.append("   wDoc=wFrm.contentWindow;\n");
            JSPHeader.append("   var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');\n");
            JSPHeader.append("   toRet=false;\n");
            JSPHeader.append("   for(var z=0;  z < wDocfrms.length ; z++)\n");
            JSPHeader.append("   {\n");
            JSPHeader.append("      var xok=false;\n");
            JSPHeader.append("      xw=wDocfrms[z].contentWindow.document.all;\n");
            JSPHeader.append("      var toR=[];\n");
            JSPHeader.append("      for(var j=0; j< xw.length; j++){\n");
            JSPHeader.append("        if ( xw[j].id == \"tblLook<%=nameH%>\" ){\n");
            JSPHeader.append("         toR[toR.length]=xw[j];\n");
            JSPHeader.append("        }\n");
            JSPHeader.append("      }\n");
            JSPHeader.append("      for(var y=0; y < toR.length; y++){\n");
            JSPHeader.append("            toR[y].outerHTML=toRender.innerHTML;\n");
            JSPHeader.append("      }\n");

            //toRet=
            JSPHeader.append("         updateFrame(wDocfrms[z]);\n");

            JSPHeader.append("   }\n");

            //return toRet
            JSPHeader.append(" }\n");
/*
            JSPHeader.append("function updateInFrames()\n");
            JSPHeader.append(" {\n");
            JSPHeader.append("   var windowToUpdate=\"<%=clientIDX%>\";\n");
            JSPHeader.append("   var w=parent.ndl[windowToUpdate];\n");
            JSPHeader.append("   if(w)\n");
            JSPHeader.append("   {\n");
            JSPHeader.append("       var ifrm=w.htm.getElementsByTagName('IFRAME');\n");
            JSPHeader.append("       var xw;\n");
            JSPHeader.append("       for(var i=0; i < ifrm.length ; i++)\n");
            JSPHeader.append("       {\n");
            JSPHeader.append("           var xok=updateFrame(ifrm[i]);\n");
            JSPHeader.append("           if ( xok ) break;\n");
            JSPHeader.append("       }\n");
            JSPHeader.append("   }\n");

            JSPHeader.append(" }\n");
*/
            JSPHeader.append("  function updateLookupAttribute(){\n");

            JSPHeader.append("   var windowToUpdate=\"<%=clientIDX%>\";\n");
            JSPHeader.append("   var xok=false;\n");
            JSPHeader.append("   var w=parent.ndl[windowToUpdate];\n");
            JSPHeader.append("   if(w){\n");
         //   JSPHeader.append("       var ifrm=w.htm.getElementsByTagName('IFRAME');\n");
            JSPHeader.append("       var ifrm=w.htm;\n");
            JSPHeader.append("       var xw;\n");
            //JSPHeader.append("       for(var i=0; i < ifrm.length ; i++){\n");
            //JSPHeader.append("          xw=ifrm[i].contentWindow.document.all;\n");
            JSPHeader.append("          xw=ifrm.contentWindow.document.all;\n");
            JSPHeader.append("          var toR=[];\n");
            JSPHeader.append("          for(var z=0; z< xw.length; z++){\n");
            JSPHeader.append("            if ( xw[z].id == \"tblLook<%=nameH%>\" ){\n");
            JSPHeader.append("             toR[toR.length]=xw[z];\n");
            JSPHeader.append("            }\n");
            JSPHeader.append("          }\n");
            JSPHeader.append("          for(var y=0; y < toR.length; y++){\n");
            JSPHeader.append("                toR[y].outerHTML=toRender.innerHTML;\n");
          //  JSPHeader.append("                var xele=ifrm[i].contentWindow.document.getElementsByName('<%=nameH%>');\n");
            JSPHeader.append("                var xele=ifrm.contentWindow.document.getElementsByName('<%=nameH%>');\n");
            JSPHeader.append("                for( var z=0; z<xele.length;z++)\n");
            JSPHeader.append("                      xele[z].original=xele[0].value;\n");
            JSPHeader.append("                xok=true;\n");
            JSPHeader.append("          }\n");
            //JSPHeader.append("       }\n");
           //JSPHeader.append("       if ( !xok ) updateInFrames();\n");
             JSPHeader.append("       if ( !xok ) updateFrame(ifrm);\n");
            JSPHeader.append("   }\n");

            JSPHeader.append(" }\n");

            JSPHeader.append("</script>\n");
            JSPHeader.append("<%}%>\n");

            JSPHeader.append("<script>\n");
            JSPHeader.append(p_bodef.codeJavaScript(p_formName));
            JSPHeader.append("</script>\n");

            JSPHeader.append("<%if (scriptToRun==null) {%>\n");
            JSPHeader.append( "<body ondragenter=\"activethis()\" onload=\" <%=DOC.getActionOnClient()%>; runAfterMethodExec();"+(p_iswizard?"runStart();":"")+" treatFocus(); \">\n");
            JSPHeader.append("<%}\n");
            JSPHeader.append("else{%>\n");
            JSPHeader.append(
                "<body ondragenter=\"activethis()\" onload=\" <%=scriptToRun%>; runAfterMethodExec();"+(p_iswizard?"runStart();":"")+" treatFocus(); \">\n");
            JSPHeader.append("<%\n");
            JSPHeader.append("}\n");
            JSPHeader.append("%>\n");

            JSPHeader.append(
                "<%if ( request.getParameter(\"boFormSubmitXml\")!= null && request.getParameter(\"actRenderObj\") != null && ( BOI!=null && BOI.exists() )){%>\n");
            JSPHeader.append("    <div id=\"toRender\" style='display:none'>\n");
            JSPHeader.append("    <%=toP%>\n");
            JSPHeader.append("</div>\n");
            JSPHeader.append("<%}%>\n");

            StringBuffer JSPFooter = new StringBuffer();

            if (p_typeForm == this.TYPE_EDIT) {
                JSPHeader.append(
                    "<%boObject xobj=(boObject)currObjectList.getParent();\n");

                ///-1   JSPHeader.append("if(request == null) {\n");
                JSPHeader.append(
                    "if( request.getParameter(\"toClose\") == null || DOC.haveErrors() ) {\n");
                JSPHeader.append("if(xobj == null) {\n");
                JSPHeader.append(
                    "if (request.getParameter(\"menu\")==null || (request.getParameter(\"menu\").equalsIgnoreCase(\"yes\"))){%>\n");
                JSPHeader.append(
                    "<TABLE id=\"dBody\" class=\"layout\" cellspacing=\"0\" cellpadding=\"0\">\n");
                JSPHeader.append("<TBODY>\n");

//                JSPHeader.append("<TR height=\"<%=DOC.getHEIGHT_HTMLforToolbar(pageContext,currObjectList)%>\">\n");
//                JSPHeader.append("<TD ><%DOC.writeHTMLforToolbar(DOCLIST, pageContext,currObjectList);  %></TD>\n");
//                JSPHeader.append("</TR>\n");
                if(!p_iswizard)
                {
                    JSPHeader.append("<%if( !DOC.p_WebForm ){ %>\n");
                    JSPHeader.append("<%DOC.getController().getPresentation().writeToolBar(DOCLIST, pageContext,currObjectList);%>\n");
                    JSPHeader.append("<%}%>\n");
                }

                JSPHeader.append("<%DOC.getController().getPresentation().writeHeaderHandler(DOCLIST,pageContext);%>\n");
                JSPHeader.append("<%=DOC.getController().getPresentation().renderPath( request )%>\n");

                JSPHeader.append("<%if(DOC.haveErrors()) {%>\n");
                JSPHeader.append("<TR><TD class='error'><div class='error'>\n");
                JSPHeader.append("<%=DOC.getHTMLErrors()%>\n");
                JSPHeader.append("</div></TD></TR>\n");

                JSPHeader.append("<%}%>\n");

                if (form.getAttribute("style") == null) {
                    JSPHeader.append("<TR height=\"100%\" ><TD valign=top align=left>\n");
                } else {
                    if ((form.getAttribute("style") == null) && !p_haveAreas) {
                        JSPHeader.append(
                            "<TR><TD valign=top style=\"padding:10px;\" align=left>\n");
                    } else {
                        JSPHeader.append("<TR><TD valign=top style=\"" +
                            form.getAttribute("style") + "\" align=left>\n");
                    }
                }

                JSPHeader.append("<%}}%>\n");

                ///---------------------------foote
                JSPFooter.append(
                    "<%if (request.getParameter(\"menu\")==null || (request.getParameter(\"menu\").equalsIgnoreCase(\"yes\"))){%>\n");
                JSPFooter.append("</TD></TR>");
                JSPFooter.append("<%= DOC.getController().getPresentation().writeFooterHandler() %>");
                JSPFooter.append("</TBODY></TABLE>\n");
                JSPFooter.append("<%}%>\n");
                JSPFooter.append("<%} else {%>\n");
                JSPFooter.append(
                    "<font size='5' color='Green' >Gravação OK</font>\n");
                JSPFooter.append("<%}%>\n");

                JSPFooter.append(
                    "<FORM name='boFormSubmit' method='post' action='");

                //                JSPFooter.append("<%=BOI.getMode()==boObject.MODE_EDIT_TEMPLATE?\"ebo_template_generaledit.jsp\" :\"");
                JSPFooter.append(p_jspNameRelative.toLowerCase());
                JSPFooter.append("'>\n");
            } else {
                JSPHeader.append(
                    "<% if(!(request.getParameter(\"toClose\") != null && !DOC.haveErrors())) {%>\n");
                JSPHeader.append("<%if(DOC.haveErrors()) {%>\n");
                JSPHeader.append("<div class='error'>\n");
                JSPHeader.append(" <%=DOC.getHTMLErrors()%>\n");
                JSPHeader.append("</div>\n");
                JSPHeader.append("<%}%>\n");

                ///----------------------footer
                JSPFooter.append("<%}%>\n");

                //                JSPFooter.append("<FORM name='boFormSubmit' method='get'>\n");
                JSPFooter.append(
                    "<FORM name='boFormSubmit' method='post' action='");
                JSPFooter.append(p_jspNameRelative.toLowerCase());
                JSPFooter.append("'>\n");
            }



            JSPFooter.append("\n<%\n");
            JSPFooter.append(" java.util.Hashtable options = new java.util.Hashtable();\n");

            JSPFooter.append("if(method != null){\n");
            JSPFooter.append("      options.put(\"").append(BasicPresentation.OPTION_METHOD).append("\",method);\n");
            JSPFooter.append("}\n");
            JSPFooter.append("if(inputMethod != null){\n");
            JSPFooter.append("      options.put(\"").append(BasicPresentation.OPTION_INPUT_METHOD).append("\",inputMethod);\n");
            JSPFooter.append("}\n");
            JSPFooter.append("if(requestedBoui != null){\n");
            JSPFooter.append("      options.put(\"").append(BasicPresentation.OPTION_REQUESTED_BOUI).append("\",requestedBoui);\n");
            JSPFooter.append("}\n");
            JSPFooter.append(" options.put(\"").append(BasicPresentation.OPTION_TYPE_FORM).append("\",\"").append(String.valueOf(p_typeForm)).append("\");\n");
            JSPFooter.append(" options.put(\"").append(BasicPresentation.OPTION_JSP_NAME).append("\",this.getClass().getName());\n");
			JSPFooter.append("if (request.getParameter(\"userQuery\")!=null)\n {");
            JSPFooter.append("out.println(\"<INPUT type='hidden' value=\\\"\"+request.getParameter(\"userQuery\")+\"\\\" name='userQuery' />\");");
            JSPFooter.append("\n}");
            JSPFooter.append("%>\n");


            JSPFooter.append("\n<%= DOC.getController().getPresentation().writeJspFooter(BOI ,currObjectList,options,masterdoc,request) %>\n\n");

            /*
            if (p_typeForm == TYPE_LIST)
            {
                JSPFooter.append("\n<%= DOC.getController().getPresentation().writeJspFooter(BOI ,currObjectList,method,inputMethod, requestedBoui,masterdoc,\"").append(String.valueOf(p_typeForm)).append("\",request,this.getClass().getName()) %>\n\n");
            }
            else
            {
                JSPFooter.append("\n<%= DOC.getController().getPresentation().writeJspFooter(BOI ,currObjectList,method,inputMethod, requestedBoui,masterdoc,\"").append(String.valueOf(p_typeForm)).append("\",request,this.getClass().getName()) %>\n\n");
            }
            */

            JSPFooter.append("</FORM>");

            JSPFooter.append("</body>");
            JSPFooter.append("</html>");

            ///          JSPFooter.append("<%}\n");
            JSPFooter.append("<%\n");
            JSPFooter.append(
                "} else response.sendRedirect(\"dialogBoxSecurityWarning.htm\"); } finally {\n"); //end initPage

            ///            JSPFooter.append("if(initPage) {boctx.close();");
            JSPFooter.append("if (boctx!=null)boctx.close();if (DOCLIST!=null && boctx!=null)DOCLIST.releseObjects(boctx);\n");

            ///      JSPFooter.append("DOCLIST.releseObjects(boctx);}");
            JSPFooter.append("}%>\n");



            /*FileWriter file = new FileWriter(this.p_url_to_deploy +
                    p_jspNameRelative.toLowerCase(), false);*/

            String xREPLACEFILE = "";

            if (form.getChildNodes().length > 1) {
                xREPLACEFILE = form.getChildNodes()[0].getNodeName();
            }

            File dir = new File( this.p_url_to_deploy );
            
            if( !dir.exists() ) {
            	dir.mkdirs();
            }
            
            //PrintWriter pw = new PrintWriter(file);
            OutputStreamWriter file;
            if(encoding!=null)
              file= new OutputStreamWriter(new FileOutputStream(this.p_url_to_deploy + p_jspNameRelative.toLowerCase(), false), encoding);
            else
              file= new OutputStreamWriter(new FileOutputStream(this.p_url_to_deploy + p_jspNameRelative.toLowerCase(), false));

            if (xREPLACEFILE.equalsIgnoreCase("include-file")) {
                file.write("<%@include file='" +
                    form.getChildNodes()[0].getText() + "'%>");
            } else {
                file.write(JSPHeader.toString());
                file.write("\n");
                file.write(toBuild);
                file.write(JSPFooter.toString());
            }

            //pw.close();
            file.close();
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
    }

    private void newPage(String jspName, XMLDocument xBodyBefore,
        Element xBodyApp, boDefViewer viewer, ngtXMLHandler form,
        String bo_NodeCat) {
        try {
            String encoding = (new boConfig()).getEncoding();
            XMLDocument xBody;
            Element xnode;
            xBody = new XMLDocument();
            xnode = xBody.createElement("form");

            xnode.setAttribute("class", "objectForm");
            xnode.setAttribute("name", "boForm");
            xnode.setAttribute("id", "#BEGIN#=IDX#END#");

            xBody.appendChild(xnode);

            recursiveForm(xBody, xnode, viewer, form, bo_NodeCat);

            String b = ngtXMLUtils.getXML(xBody);

            String toBuild = b;
            toBuild = toBuild.replaceAll( "#BEGIN_INCLUDE#",
                    " <jsp:include ");
            toBuild = toBuild.replaceAll( "#END_INCLUDE#", " />");

            toBuild = toBuild.replaceAll( "#END_TAG#", ">");
            toBuild = toBuild.replaceAll( "#BEGIN_TAG#", "<");
            toBuild = toBuild.replaceAll( "#BEGIN_ANCHOR#", "<a ");

            toBuild = toBuild.replaceAll( "#BEGIN#", "<%");
            toBuild = toBuild.replaceAll( "#END#", "%>");
            toBuild = toBuild.replaceAll( "<JSP_EXPRESSION>", "");
            toBuild = toBuild.replaceAll( "</JSP_EXPRESSION>", "");
            toBuild = toBuild.replaceAll( "#AMP#", "&");
            toBuild = toBuild.replaceAll( "#QUOT#", "\"");

            RegularExpression xRE = new RegularExpression(">(.\\s*?)<img", "s");
            toBuild = REUtil.substitute(xRE, "><img", true, toBuild);

            xRE = new RegularExpression(">(.\\s*?)</td", "s");
            toBuild = REUtil.substitute(xRE, "></td", true, toBuild);

            xRE = new RegularExpression("SPAN>(.\\s*?)<", "s");
            toBuild = REUtil.substitute(xRE, "SPAN><", true, toBuild);

            xRE = new RegularExpression(">(.\\s*?)<SPAN", "s");
            toBuild = REUtil.substitute(xRE, "><SPAN", true, toBuild);
            xRE = new RegularExpression("</SPAN><%}%>(.\\s*?)<%if", "s");
            toBuild = REUtil.substitute(xRE, "</SPAN><%}%><%if", true, toBuild);

            xRE = new RegularExpression("<TD>(.\\s*?)<%if", "s");
            toBuild = REUtil.substitute(xRE, "<TD><%if", true, toBuild);

            StringBuffer JSPHeader = new StringBuffer();

            JSPHeader.append(
                "<%@ page contentType=\"text/html;charset=" + encoding + "\"%>");
            JSPHeader.append("\n");
            JSPHeader.append("<%@ page import=\"java.util.*\"%>\n");
            JSPHeader.append("<%@ page import=\"netgest.bo.*\"%>\n");
            JSPHeader.append("<%@ page import=\"netgest.bo.dochtml.*\"%>\n");
            JSPHeader.append("<%@ page import=\"netgest.bo.runtime.*\"%>\n");
            JSPHeader.append("<%@ page import=\"netgest.bo.def.*\"%>\n");
            JSPHeader.append(
                "<%@ page import=\"netgest.utils.*,netgest.bo.system.*\"%>\n");

            JSPHeader.append("\n");
            JSPHeader.append(
                "<jsp:useBean id=\"DOCLIST\" scope=\"session\" class=\"netgest.bo.dochtml.docHTML_controler\"></jsp:useBean>");
            JSPHeader.append("\n");
            JSPHeader.append("<%");
            JSPHeader.append("\n");

            ///JSPHeader.append("EboContext boctx = (EboContext)request.getAttribute(\"a_EboContext\");\nboolean initPage=true;\n");
            JSPHeader.append("response.setDateHeader (\"Expires\", -1);\n");

            JSPHeader.append(
                "EboContext boctx = (EboContext)request.getAttribute(\"a_EboContext\");\n");
            JSPHeader.append("try {\n");
            JSPHeader.append("boolean masterdoc=false;\n");
            JSPHeader.append("if( request.getParameter(\"docid\")==null){\n");
            JSPHeader.append("        masterdoc=true;\n");
            JSPHeader.append("}\n");

            JSPHeader.append(
                "boSession bosession = (boSession)request.getSession().getAttribute(\"boSession\");\n");
            JSPHeader.append("if(bosession== null) {\n");
            JSPHeader.append(
                "    response.sendRedirect(\"login.jsp?returnPage=" +
                p_jspNameRelative.toLowerCase() + "\");\n");
            JSPHeader.append("    return;\n");
            JSPHeader.append("}\n");
            JSPHeader.append("if(boctx==null) {\n");
            JSPHeader.append(
                "    boctx = bosession.createRequestContext(request,response,pageContext);\n");
            JSPHeader.append(
                "    request.setAttribute(\"a_EboContext\",boctx);\n");
            JSPHeader.append("}\n");
            JSPHeader.append(
                "int IDX;int cvui;\nboDefHandler bodef;\nboDefAttribute atr;\nString idbolist;\nString[] ctrl;\n");
            JSPHeader.append("docHTML_section sec;\n");
            JSPHeader.append("docHTML_grid grid;\n");
            JSPHeader.append("docHTML_groupGrid gridG;\n");
            JSPHeader.append("Hashtable xattributes;\n");
            JSPHeader.append("ctrl= DOCLIST.processRequest(boctx);\n");
            JSPHeader.append("IDX= ClassUtils.convertToInt(ctrl[0],-1);\n");
            JSPHeader.append("idbolist=ctrl[1];\n");

            JSPHeader.append("docHTML DOC = DOCLIST.getDOC(IDX);\n");
            JSPHeader.append("\n");
            JSPHeader.append("%>");
            JSPHeader.append("\n");
            JSPHeader.append("<html>\n");
            JSPHeader.append("<head>\n");
            JSPHeader.append(
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\"/>\n");
            JSPHeader.append("<META HTTP-EQUIV='Refresh' CONTENT='3000'>");
            JSPHeader.append("<title>\n");
            JSPHeader.append("</title>\n");

            JSPHeader.append("<%@ include file='boheaders.jsp'%>");

            StringBuffer JSPFooter = new StringBuffer();

            JSPFooter.append("</body>");
            JSPFooter.append("</html>");

            JSPFooter.append("<%\n");
            JSPFooter.append("} finally {\n"); //end initPage

            JSPFooter.append("if (boctx!=null)boctx.close();if (DOCLIST!=null && boctx!=null)DOCLIST.releseObjects(boctx);\n");
            JSPFooter.append("}%>\n");

            OutputStreamWriter file = null;
            if(encoding != null)
            {
                file= new OutputStreamWriter(new FileOutputStream(this.p_url_to_deploy + jspName.toLowerCase(), false), encoding);
            }
            else
            {
                file = new OutputStreamWriter(new FileOutputStream(this.p_url_to_deploy + jspName.toLowerCase(), false));
            }

            file.write(JSPHeader.toString());
            file.write("\n");
            file.write(toBuild);
            file.write(JSPFooter.toString());

            file.close();
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
    }

    /**
     * Method Recursive Form
     *
     *
     *
     *
     */
    private void recursiveForm(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        String xTagName = form.getNodeName();
        String xpath = p_path;
        p_path = xpath + "." + xTagName;

        if (xTagName.equalsIgnoreCase("panel")) {
            buildPanel(xBody, xBodyApp, viewer, form, bo_NodeCat);

            /*  if ( form.next() != null )
              {
                 recursiveForm(xBody,xBodyApp,viewer,form.next(),bo_NodeCat);
              }*/
        } else if (xTagName.equalsIgnoreCase("areas")) {
            buildAreas(xBody, xBodyApp, viewer, form, bo_NodeCat);

            /*  if ( form.next() != null )
            {
               recursiveForm(xBody,xBodyApp,viewer,form.next(),bo_NodeCat);
            }*/
        } else if (xTagName.equalsIgnoreCase("frame")) {
            buildFrame(xBody, xBodyApp, viewer, form, bo_NodeCat);

            /*  if ( form.next() != null )
             {
                recursiveForm(xBody,xBodyApp,viewer,form.next(),bo_NodeCat);
             }*/
        } else if (xTagName.equalsIgnoreCase("grid")) {
            buildGrid(xBody, xBodyApp, viewer, form, bo_NodeCat);
        } else if (xTagName.equalsIgnoreCase("gridGroup")) {
            buildGridGroup(xBody, xBodyApp, viewer, form, bo_NodeCat);
        } else if (xTagName.equalsIgnoreCase("treeView")) {
            buildTreeView(xBody, xBodyApp, viewer, form, bo_NodeCat);
        } else if (xTagName.equalsIgnoreCase("explorer")) {
            buildExplorer(xBody, xBodyApp, viewer, form, bo_NodeCat);
        } else if (xTagName.equalsIgnoreCase("object")) {
            buildStaticObject(xBody, xBodyApp, viewer, form, bo_NodeCat);
        } else if (xTagName.equalsIgnoreCase("box")) {
            buildBox(xBody, xBodyApp, viewer, form, bo_NodeCat);
        } else if (xTagName.equalsIgnoreCase("include-file")) {
            buildIncludeFile(xBody, xBodyApp, viewer, form, bo_NodeCat);
        } else if (xTagName.equalsIgnoreCase("include-frame")) {
            buildIncludeFrame(xBody, xBodyApp, viewer, form, bo_NodeCat);
       } else if (xTagName.equalsIgnoreCase("menuWebForm")) {
            buildMenuWebForm(xBody, xBodyApp, viewer, form, bo_NodeCat);
        } else if (xTagName.equalsIgnoreCase("section")) {
            buildSection(xBody, xBodyApp, viewer, form, bo_NodeCat);
        }else  if (xTagName.equalsIgnoreCase("order")) {
            //ignore
        }
        else {
            // não é boPANEL nem grid
            Element xEle;
            Element xInstr = null;
            Element xInstr2 = null;
            String xInstrText = "";
            String xInstrText2 = "";
            String xRepeat;
            String xBOUI;
            String xNodeName = form.getNodeName();
            String xbo_NodeCat;
            boolean repeat;
            xbo_NodeCat = form.getAttribute("bo_node");

            if (xbo_NodeCat != null) {
                xInstr = xBody.createElement("JSP_EXPRESSION");
                xBodyApp.appendChild(xInstr);
                xInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if(DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\")){ #END#"));
            } else {
                xbo_NodeCat = bo_NodeCat;
            }

            //beginRepeat
            xRepeat = form.getAttribute("bo_repeat");
            repeat = false;

            if (xRepeat != null) {
                if (xRepeat.equalsIgnoreCase("y")) {
                    repeat = true;
                }
            }

            if (repeat) {
                xInstr2 = xBody.createElement("JSP_EXPRESSION");
                xBodyApp.appendChild(xInstr2);

                xInstr2.appendChild(xBody.createTextNode(
                            "#BEGIN# currObjectList.first();do {BOI=currObjectList.getObject(); #END#"));
            }

            xEle = xBody.createElement(xNodeName);

            Attr[] xlist = form.getAttributes();
            String xstrText = null;
            String name = null;
            boolean hasTabIndex = false;

            for (int i = 0; i < xlist.length; i++) {
                xstrText = parseExpr(form, viewer, bo_NodeCat,
                        xlist[i].getValue());
                xEle.setAttribute(xlist[i].getName(), xstrText);

                if ("name".equalsIgnoreCase(xlist[i].getName())) {
                    name = xstrText;
                }

                if ("tabindex".equalsIgnoreCase(xlist[i].getName())) {
                    hasTabIndex = true;
                }
            }

            if (!hasTabIndex && setAttributeTabIndex(xNodeName)) {
                StringBuffer sb = new StringBuffer();
                sb.append(
                    "#BEGIN#=DOC.getTabindex(DOC.IFRAME, BOI==null? #QUOT#" +
                    p_bodef.getBoName() +
                    "#QUOT# : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), #QUOT#");

                if (name != null) {
                    sb.append(name);
                } else {
                    sb.append(xNodeName);
                }

                sb.append(
                    "#QUOT# + String.valueOf(BOI==null?0:BOI.bo_boui), DOCLIST)#END#");
                xEle.setAttribute("tabIndex", sb.toString());
            }

            xBodyApp.appendChild(xEle);

            xstrText = form.getText();

            if (xstrText != null) {
                xstrText = parseExpr(form, viewer, bo_NodeCat, xstrText);
                xEle.appendChild(xBody.createTextNode(xstrText));
            }

            ngtXMLHandler[] childsForm = form.getChildNodes();

            if(p_iswizard && p_isFirstTable && "table".equalsIgnoreCase(xNodeName))
            {
                //vou desenhar a message zone
                Element trElem = xBody.createElement("TR");
                Element tdElem = xBody.createElement("TD");
                trElem.appendChild(tdElem);
                Element tableElem = xBody.createElement("table");
                tableElem.setAttribute("class", "messageZone");
                tableElem.setAttribute("id", "messageZone");
                tdElem.appendChild(tableElem);
                Element trElem2 = xBody.createElement("TR");
                tableElem.appendChild(trElem2);
                Element tdElem2 = xBody.createElement("TD");
                trElem2.appendChild(tdElem2);
                xEle.appendChild(trElem);
                p_isFirstTable = false;
            }

            for (int i = 0; i < childsForm.length; i++) {
                recursiveForm(xBody, xEle, viewer, childsForm[i], xbo_NodeCat);
            }
            if(p_iswizard && "table".equalsIgnoreCase(xNodeName))
            {
                Element trElem = xBody.createElement("TR");
                Element tdElem = xBody.createElement("TD");
                trElem.appendChild(tdElem);
                Element tableElem = xBody.createElement("table");
                tableElem.setAttribute("class", "section");
                tableElem.setAttribute("cellspacing", "0");
                tableElem.setAttribute("cellpadding", "1");
                tdElem.appendChild(tableElem);
                Element trElem2 = xBody.createElement("TR");
                tableElem.appendChild(trElem2);
                Element tdElem2 = xBody.createElement("TD");
                tdElem2.setAttribute("height", "24px");
                tdElem2.appendChild(xBody.createTextNode("#AMP#nbsp"));
                trElem2.appendChild(tdElem2);
                tdElem2 = xBody.createElement("TD");
                tdElem2.setAttribute("height", "24px");
                tdElem2.setAttribute("valign", "top");
                tdElem2.setAttribute("align", "right");
                Element buttonEl = xBody.createElement("button");
                buttonEl.setAttribute("id","buttonPrevious");
                buttonEl.setAttribute("style","background:url(resources/wizPrevious.gif) no-repeat center left;filter:0; width:100%");
                buttonEl.setAttribute("onClick","runPrevious();");
                buttonEl.appendChild(xBody.createTextNode("Anterior"));
                tdElem2.appendChild(buttonEl);
                trElem2.appendChild(tdElem2);
                tdElem2 = xBody.createElement("TD");
                tdElem2.setAttribute("height", "24px");
                tdElem2.setAttribute("valign", "top");
                tdElem2.setAttribute("align", "right");
                buttonEl = xBody.createElement("button");
                buttonEl.setAttribute("id","buttonNext");
                buttonEl.setAttribute("style","background:url(resources/wizNext.gif) no-repeat center right;filter:0; width:100%");
                buttonEl.setAttribute("onClick","runNext();");
                buttonEl.appendChild(xBody.createTextNode("Próximo"));
                tdElem2.appendChild(buttonEl);
                trElem2.appendChild(tdElem2);
                tdElem2 = xBody.createElement("TD");
                tdElem2.setAttribute("height", "24px");
                tdElem2.setAttribute("valign", "top");
                tdElem2.setAttribute("align", "right");
                buttonEl = xBody.createElement("button");
                buttonEl.setAttribute("id","buttonEnd");
                buttonEl.setAttribute("style","background:url(resources/wizEnd.gif) no-repeat center left;filter:0; width:100%");
                buttonEl.setAttribute("onClick","runEnd();");
                buttonEl.appendChild(xBody.createTextNode("Terminar"));
                tdElem2.appendChild(buttonEl);
                trElem2.appendChild(tdElem2);
                tdElem2 = xBody.createElement("TD");
                tdElem2.setAttribute("height", "24px");
                tdElem2.setAttribute("valign", "top");
                tdElem2.setAttribute("align", "right");
                buttonEl = xBody.createElement("button");
                buttonEl.setAttribute("id","buttonCancel");
                buttonEl.setAttribute("style","background:url(resources/wizCancel.gif) no-repeat center left;filter:0; width:100%");
                buttonEl.setAttribute("onClick","boForm.Close();");
                buttonEl.appendChild(xBody.createTextNode("Cancelar"));
                tdElem2.appendChild(buttonEl);
                trElem2.appendChild(tdElem2);

                xEle.appendChild(trElem);
            }

            if (repeat) {
                xInstr2 = xBody.createElement("JSP_EXPRESSION");
                xBodyApp.appendChild(xInstr2);
                xInstr2.appendChild(xBody.createTextNode(
                        "#BEGIN# } while (currObjectList.next()); #END#"));
            }

            //end repeat
            if (xInstr != null) {
                xInstr = xBody.createElement("JSP_EXPRESSION");
                xBodyApp.appendChild(xInstr);
                xInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#")); //END getScripttoRunOnclient
            }
        }

        p_path = xpath;
    }

    private void buildIncludeFile(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        //<%@ include file='boheaders.jsp'%>
        Element xPanelInstr;
        String xPanelInstrText = "";

        xPanelInstr = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xPanelInstr);
        xPanelInstrText = "#BEGIN#";
        xPanelInstrText += ("@ include file='" + form.getText() + "'");
        xPanelInstrText += "#END#";
        xPanelInstr.appendChild(xBody.createTextNode(xPanelInstrText));
    }

    private void buildMenuWebForm(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        Element xPanelInstr;
        String xPanelInstrText = "";

        String x = form.getText();
        if( "".equals( x) || x==null ) x="Submeter Formulário";
        xPanelInstr = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xPanelInstr);
        xPanelInstrText = "#BEGIN#=";
        xPanelInstrText += "DOC.getMenuWebForm( BOI , request, DOCLIST,\""+x+"\")";
       xPanelInstrText += "#END#";
        xPanelInstr.appendChild(xBody.createTextNode(xPanelInstrText));
    }

    private void buildIncludeFrame(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        Element xPanelInstr;
        String xPanelInstrText = "";

        String __url = form.getText();

        xPanelInstr = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xPanelInstr);

        ngtXMLHandler xatt;
        ngtXMLHandler[] xChilds = null;
        ngtXMLHandler xparam = form.getChildNode("parameters");
        String attr = null;

        if (xparam != null) {
            attr = form.getAttribute("name");
        }

        if ((attr == null) || "".equals(attr)) {
            attr = "parameters";
        }

        String random = String.valueOf(System.currentTimeMillis());

        if (xparam != null) {
            xChilds = xparam.getChildNodes();

            String nodeName;
            String xvalue;
            String parameters = "String " + attr + random + " = \"";

            for (int i = 0; i < xChilds.length; i++) {
                xatt = xChilds[i];

                nodeName = xatt.getNodeName();

                if (i != 0) {
                    parameters += ("+\"" + "#AMP#");
                }

                parameters += nodeName;
                parameters += "=\" + (";

                //parameters+= "=\" + ";
                parameters += getFrameParameters(nodeName, xatt.getText());
                parameters += ")";
            }

            nodeName = "docid";

            if (xChilds.length > 0) {
                parameters += ("+\"" + "#AMP#");
            }

            parameters += nodeName;
            parameters += "=\" + ( DOC.getDocIdx() ) ";

            parameters += ";";

            xPanelInstrText = "#BEGIN#\n";
            xPanelInstrText += parameters;
            xPanelInstrText += "\n#END#";

            xPanelInstrText += "\n";
        }

        xPanelInstrText += "#BEGIN_TAG#";
        xPanelInstrText += "IFRAME ";

        if (__url.indexOf("extend") == -1) {
            xPanelInstrText += (" src=\"" + __url);

            if (xparam != null) {
                xPanelInstrText += "?";
                xPanelInstrText += ("#BEGIN#" + "=" + attr + random + "#END#");
            }

            xPanelInstrText += "\"";
        }

        xPanelInstrText += (" xsrc=\"" + __url);

        if (xparam != null) {
            xPanelInstrText += "?";
            xPanelInstrText += ("#BEGIN#" + "=" + attr + random + "#END#");
        }

        xPanelInstrText += "\"";

        // Atributos da tag iframe
        Attr[] ifAttr = form.getAttributes();
        String id = "";
        String idAux = "";

        for (int i = 0; i < ifAttr.length; i++) {
            Attr attx = ifAttr[i];
            xPanelInstrText += (" " + attx.getName());

            if ("id".equalsIgnoreCase(attx.getName())) {
                id = attx.getValue().replaceAll("this",
                        "#BEGIN#= String.valueOf(BOI.getBoui()) #END#");
                idAux = attx.getValue().replaceAll("this",
                        "\" + String.valueOf( (parent_boui==null?( BOI==null?0 : BOI.getBoui()  ): new Long(parent_boui).longValue() ) ) + \"");
                xPanelInstrText += ("='" +
                attx.getValue().replaceAll("this",
                    "#BEGIN#= String.valueOf( (parent_boui==null?( BOI==null?0 : BOI.getBoui()  ): new Long(parent_boui).longValue() ) ) #END#") +
                "'");
            } else {
                xPanelInstrText += ("='" + attx.getValue() + "'");
            }
        }

        //tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "detail", DOCLIST)%>'
        xPanelInstrText += (" tabindex='#BEGIN_TAG#%=DOC.getTabindex(DOC.IFRAME, BOI==null? #QUOT#" +
        attr +
        "#QUOT# : BOI.getName(), String.valueOf(parent_boui==null?( BOI==null?0 : BOI.getBoui()  ): new Long(parent_boui).longValue()), \"" +
        idAux + "\", DOCLIST)%#END_TAG#'");
        xPanelInstrText += "#END_TAG#";
        xPanelInstrText += ("#BEGIN_TAG#" + "/IFRAME" + "#END_TAG#");

        xPanelInstr.appendChild(xBody.createTextNode(xPanelInstrText));
    }

    private String getFrameParameters(String param, String value)
    {
        if( value != null && value.startsWith("[") && value.endsWith("]") )
        {

            return value.substring(1,value.length()-1);

        }
        StringBuffer sb = new StringBuffer();
        StringBuffer sbcopy = new StringBuffer();
        StringTokenizer st = new StringTokenizer(value, ".");
        String token;
        boDefAttribute atr;
        int ntoken = st.countTokens();
        int size = st.countTokens();
        boolean flag = true;
        int obj = -1;
        int atrib = 0;

        while (st.hasMoreTokens()) {
            token = st.nextToken();
            atr = p_bodef.getAttributeRef(token);

            if (ntoken == 1) {
                if ("this".equals(token)) {
                    sbcopy.append("String.valueOf(BOI.getBoui())");
                } else {
                    sbcopy.append(token);
                }
            } else {
                if ("this".equals(token) && flag) {
                    sb.append("BOI");
                    obj = 0;
                    flag = false;
                    size--;
                } else if (obj == -1) {
                    sb.append(token);
                    obj = 0;
                    flag = false;
                    size--;
                }

                if ((obj == 0) && flag) {
                    sb.append(".getAttribute(\"");
                    sb.append(token);

                    if (atr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                        sb.append("\").getObject()");
                    } else {
                        sb.append("\")");
                        atrib = 1;
                    }


                    obj = 1;
                    flag = false;
                    size--;
                }

                if (size == 0) {
                    if (atr != null) {
                        if (atr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                            sbcopy.append(sb.toString()).append(" != null ? ")
                                  .append("String.valueOf(").append(sb.toString());
                            sbcopy.append(".getBoui())");
                        } else if (atr.getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) {
                            sbcopy.append(sb.toString()).append(" != null ? ")
                                  .append(sb.toString());

                            if (atrib == 0) {
                                sbcopy.append(".getAttribute(\"").append(token)
                                      .append("\")");
                            }

                            sbcopy.append(".getValueString()");
                        }

                        sbcopy.append(" : \"\"");
                    }
                }

                if ((size == 1) && flag) {
                    sbcopy.append(sb.toString()).append(" != null ? ").append(sb.toString());

                    if (atr != null) {
                        if (atr.getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) {
                            sbcopy.append(".getAttribute(\"").append(token)
                                  .append("\")");
                            sbcopy.append(".getValueString()");
                        }

                        sbcopy.append(" : \"\"");
                    }
                }

                flag = true;
            }
        }

        return sbcopy.toString();
    }

    private void buildPanel(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        Element xPanelInstr;
        String xPanelInstrText = "";

        xPanelInstr = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xPanelInstr);
        xPanelInstrText = "#BEGIN#";

        //            xPanelInstrText+="DOCLIST.vui++;DOCLIST.cvui=DOCLIST.vui;";
        if (lastAreaId == null) {
            xPanelInstrText += "DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui(), null);";
        } else {
            xPanelInstrText += ("DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui(), " +
            lastAreaId + ");");
        }

        lastAreaId = null;
        xPanelInstrText += "#END#";
        xPanelInstr.appendChild(xBody.createTextNode(xPanelInstrText));

        xPanelInstr = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xPanelInstr);
        xPanelInstrText = "";

        Element xPanel = xBody.createElement("TABLE");
        xPanel.setAttribute("id", "#BEGIN#=DOCLIST.cvui#END#");
        xPanel.setAttribute("class", "layout");

        ngtXMLHandler[] xTABS = form.getChildNodes();

        String xbo_NodeCat;
        xbo_NodeCat = form.getAttribute("bo_node");

        if (xbo_NodeCat == null) {
            xbo_NodeCat = bo_NodeCat;
        }

        Attr[] xlist = form.getAttributes();

        for (int i = 0; i < xlist.length; i++) {
            xPanel.setAttribute(xlist[i].getName(), xlist[i].getValue());
        }

        /*
         * <TABLE id='1780' height="50%" cellSpacing=0 cellPadding=0 width="100%">
                <TBODY>
                <TR height=25>
                  <TD>
                     <TABLE cellpadding='0' cellspacing='0' id="1780_body" onkeyup="so('1780');onKeyUpTab_std(event)" onmouseover="so('1780');onOverTab_std(event)" onmouseout="so('1780');onOutTab_std(event)" onclick="so('1780');onClickTab_std(event)" class=tabBar cellSpacing=0 cellPadding=0>
                      <TBODY>
                      <TR>
                        <TD style='padding:0px' id=tabs vAlign=bottom noWrap>
                                                <SPAN class="tab tabOn" id="1780_tabheader_0" tabIndex=0 ><img src="resources/activity/ico16.gif" height="12" width="12">Attachment</SPAN>
                                                <SPAN class="tab"  tabIndex=0 id="1780_tabheader_1">2Attachment</SPAN>
                                        </TD>
                                   </TR>
                                  </TBODY>
                                </TABLE>
                    <HR class=tabGlow id=hrSelTab1780>
                    <HR class=tabGlow id=hrTab1780>
                  </TD></TR>
                <TR>
                  <TD>
                    <DIV class=tab id=1780_tabbody_0 >
                     ipoipo


                                 </DIV>
                                 <DIV class=tab id=1780_tabbody_1>
                     2reererer
                                 </DIV>
                                </TD>
                        </TR>
                   </TBODY>
        </TABLE>

         */
        if (xPanel.getAttribute("width") == null) {
            xPanel.setAttribute("width", "100%");
        }

        if (xPanel.getAttribute("height") == null) {
            xPanel.setAttribute("height", "100%");
        }

        xPanel.setAttribute("cellspacing", "0");
        xPanel.setAttribute("cellpadding", "0");

        Element TBODY_panel = xBody.createElement("TBODY");
        xPanel.appendChild(TBODY_panel);

        Element TR_header_panel = xBody.createElement("TR");
        TBODY_panel.appendChild(TR_header_panel);
        TR_header_panel.setAttribute("height", "25");

        Element TD_header_panel = xBody.createElement("TD");
        TR_header_panel.appendChild(TD_header_panel);

        Element TABLE_header_panel = xBody.createElement("TABLE");
        TD_header_panel.appendChild(TABLE_header_panel);

        TABLE_header_panel.setAttribute("cellpadding", "0");
        TABLE_header_panel.setAttribute("cellspacing", "0");
        TABLE_header_panel.setAttribute("class", "tabBar");
        TABLE_header_panel.setAttribute("id", "#BEGIN#=DOCLIST.cvui#END#_body");
        TABLE_header_panel.setAttribute("onkeyup",
            "so('#BEGIN#=DOCLIST.cvui#END#');onKeyUpTab_std(event)");
        TABLE_header_panel.setAttribute("onmouseover",
            "so('#BEGIN#=DOCLIST.cvui#END#');onOverTab_std(event)");
        TABLE_header_panel.setAttribute("onmouseout",
            "so('#BEGIN#=DOCLIST.cvui#END#');onOutTab_std(event)");
        TABLE_header_panel.setAttribute("ondragenter",
            "so('#BEGIN#=DOCLIST.cvui#END#');ondragenterTab_std(event)");
        TABLE_header_panel.setAttribute("ondragover",
            "so('#BEGIN#=DOCLIST.cvui#END#');ondragoverTab_std(event)");
        TABLE_header_panel.setAttribute("onclick",
            "so('#BEGIN#=DOCLIST.cvui#END#');onClickTab_std(event)");

        Element TABLE_BODY_header_panel = xBody.createElement("TBODY");
        TABLE_header_panel.appendChild(TABLE_BODY_header_panel);

        Element TABLE_BODY_TR_header_panel = xBody.createElement("TR");
        TABLE_BODY_header_panel.appendChild(TABLE_BODY_TR_header_panel);

        Element TD_cont_spans = xBody.createElement("TD");
        TABLE_BODY_TR_header_panel.appendChild(TD_cont_spans);

        //          <TD style='padding:0px' id=tabs vAlign=bottom noWrap>
        TD_cont_spans.setAttribute("style", "padding:0px");
        TD_cont_spans.setAttribute("id", "#BEGIN#=DOCLIST.cvui#END#_tabs");
        TD_cont_spans.setAttribute("valign", "bottom");
        TD_cont_spans.setAttribute("noWrap", "yes");

        Element xBeginTabInstr;
        Element xEndTabInstr;
        Element xSpan;
        String xConstraint;

        for (int i = 0; i < xTABS.length; i++) {
            xConstraint = xTABS[i].getAttribute("constraint");
            xbo_NodeCat = xTABS[i].getAttribute("bo_node");

            if (xbo_NodeCat == null) {
                xbo_NodeCat = bo_NodeCat;
            }

            xBeginTabInstr = xBody.createElement("JSP_EXPRESSION");
            TD_cont_spans.appendChild(xBeginTabInstr);

            if ("when_object_is_extend".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\") #AMP##AMP# BOI.getMode()!=netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE #AMP##AMP# !BOI.getBridge(\"extendAttribute\").isEmpty()){ #END#"));
            } else if ("when_object_is_notextend".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\") #AMP##AMP# (BOI.getMode()==netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE || (BOI.getMode()!=netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE #AMP##AMP# BOI.getBridge(\"extendAttribute\").isEmpty()))){ #END#"));
            }
            else if ("when_object_in_template_mode".equalsIgnoreCase(xConstraint)) {
             //   isTemplate = true;
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (BOI.getMode()==netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE ){ #END#"));

            } else if ("when_no_parent".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\") #AMP##AMP# (parent_boui == null || #QUOT##QUOT#.equals(parent_boui))){ #END#"));
            }else if ("when_responsabApolice".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\") #AMP##AMP# (BOI.getAttribute(#QUOT#apoliceResponsability#QUOT#).getValueObject()!=null)){ #END#"));
            }else if ("when_damageApolice".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\") #AMP##AMP# (#QUOT#1#QUOT#.equals(BOI.getAttribute(#QUOT#damageApolice#QUOT#).getValueString()))){ #END#"));
            } else if ("when_dfa4".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if ( netgest.bo.security.securityRights.hasRightsToPackage( netgest.bo.runtime.boObject.getBoManager().loadObject(boctx, bosession.getPerformerBoui()), \"SAL$1.0\" )){ #END#"));
            } else {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\")){ #END#"));
            }

            if (xPanelInstrText.equals("")) {
                xPanelInstrText = "#BEGIN#if(DOC.hasCategoryRights(\"" +
                    xbo_NodeCat + "\")";
            } else {
                xPanelInstrText += ("|| DOC.hasCategoryRights(\"" +
                xbo_NodeCat + "\")");
            }

            //					<SPAN class="tab tabOn" id="1780_tabheader_0" tabIndex=0 ><imAttachment</SPAN>
            //					<SPAN class="tab"  tabIndex=0 id="1780_tabheader_1">2Attachment</SPAN>
            xSpan = xBody.createElement("SPAN");

            if (i == 0) {
                xSpan.setAttribute("class", "tab tabOn");
            } else {
                xSpan.setAttribute("class", "tab");
            }

            xSpan.setAttribute("id", "#BEGIN#=DOCLIST.cvui#END#_tabheader_" +
                i);

            String tabName = xTABS[i].getAttribute("name");

            if (tabName != null) {
                xSpan.setAttribute("name", tabName);
            }

            xSpan.setAttribute("tabNumber", "#BEGIN#=DOCLIST.cvui#END#");

            //xSpan.setAttribute("tabIndex","#BEGIN#=DOCLIST.tabindex++#END#");
            StringBuffer getTab = new StringBuffer();
            getTab.append(
                "#BEGIN_TAG#%=DOC.getTabindex(DOC.PANEL, BOI==null?#QUOT#" +
                tabName +
                "#QUOT# : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), ")
                  .append("String.valueOf(DOCLIST.cvui) + #QUOT#_tabheader_" +
                i + "#QUOT#").append(", DOCLIST)%#END_TAG#");
            xSpan.setAttribute("tabIndex", getTab.toString());

            if (xTABS[i].getAttribute("label") != null) {
                xSpan.appendChild(xBody.createTextNode(xTABS[i].getAttribute(
                            "label")));
            } else {
                xSpan.appendChild(xBody.createTextNode(
                        "#BEGIN#=DOC.getCategoryLabel_for_TAB_Header(\"" +
                        xbo_NodeCat + "\")#END#"));
            }

            TD_cont_spans.appendChild(xSpan);

            xEndTabInstr = xBody.createElement("JSP_EXPRESSION");
            xEndTabInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
            TD_cont_spans.appendChild(xEndTabInstr);
        }

        xPanelInstrText += "){#END#";
        xPanelInstr.appendChild(xBody.createTextNode(xPanelInstrText));

        Element HR1_header_panel = xBody.createElement("HR");
        TD_header_panel.appendChild(HR1_header_panel);
        HR1_header_panel.setAttribute("class", "tabGlow");
        HR1_header_panel.setAttribute("id", "hrSelTab#BEGIN#=DOCLIST.cvui#END#");

        Element HR2_header_panel = xBody.createElement("HR");
        TD_header_panel.appendChild(HR2_header_panel);
        HR2_header_panel.setAttribute("class", "tabGlow");
        HR2_header_panel.setAttribute("id", "hrTab#BEGIN#=DOCLIST.cvui#END#");

        /*
                  <TR>
                  <TD>
                    <DIV class=tab id=1780_tabbody_0 >
                     ipoipo


                                 </DIV>
                                 <DIV class=tab id=1780_tabbody_1>
                     2reererer
                                 </DIV>
                                </TD>
                        </TR>
        */
        Element TR_body_panel = xBody.createElement("TR");
        TBODY_panel.appendChild(TR_body_panel);

        Element TD_cont_divs = xBody.createElement("TD");
        TR_body_panel.appendChild(TD_cont_divs);

        Element xTABdiv;

        for (int i = 0; i < xTABS.length; i++) {
            xConstraint = xTABS[i].getAttribute("constraint");
            xbo_NodeCat = xTABS[i].getAttribute("bo_node");

            if (xbo_NodeCat == null) {
                xbo_NodeCat = bo_NodeCat;
            }

            xBeginTabInstr = xBody.createElement("JSP_EXPRESSION");
            TD_cont_divs.appendChild(xBeginTabInstr);

            if ("when_object_is_extend".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\") #AMP##AMP# BOI.getMode()!=netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE #AMP##AMP# !BOI.getBridge(\"extendAttribute\").isEmpty()){ #END#"));
            } else if ("when_object_is_notextend".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\") #AMP##AMP# (BOI.getMode()==netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE || (BOI.getMode()!=netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE #AMP##AMP# BOI.getBridge(\"extendAttribute\").isEmpty()))){ #END#"));
            } else if ("when_no_parent".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\") #AMP##AMP# (parent_boui == null || #QUOT##QUOT#.equals(parent_boui))){ #END#"));
            }else if ("when_responsabApolice".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\") #AMP##AMP# (BOI.getAttribute(#QUOT#apoliceResponsability#QUOT#).getValueObject()!=null)){ #END#"));
            }else if ("when_damageApolice".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\") #AMP##AMP# (#QUOT#1#QUOT#.equals(BOI.getAttribute(#QUOT#damageApolice#QUOT#).getValueString()))){ #END#"));
            }
            else if ("when_object_in_template_mode".equalsIgnoreCase(xConstraint)) {
             //   isTemplate = true;
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (BOI.getMode()==netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE ){ #END#"));
            }
            else {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\")){ #END#"));
            }

            xTABdiv = xBody.createElement("DIV");
            TD_cont_divs.appendChild(xTABdiv);

            xTABdiv.setAttribute("id", "#BEGIN#=DOCLIST.cvui#END#_tabbody_" +
                i);
            xTABdiv.setAttribute("class", "tab");

            // overflow?
            String xStyle = xTABS[i].getAttribute("style");

            if (xStyle != null) {
                xTABdiv.setAttribute("style", xStyle);
            }

            // call Recursive form
            ngtXMLHandler xTDbody = new ngtXMLHandler(xTABdiv);
            if ("when_object_in_template_mode".equalsIgnoreCase(
                        xConstraint)) {
                String objName = "Ebo_Template";
                String formName = "edit";
                String jspName = objName + "_" + "general" + formName + ".jsp";

                Element xi;

                xi = xBody.createElement("JSP_EXPRESSION");

                //String x="#BEGIN_TAG#IFRAME   onload=\"winmain().loaded(getIDX())\" id='inc_TEMPLATE__#BEGIN#=BOI.bo_boui#END#' xsrc='#BEGIN#=\""+jspName.toString().toLowerCase()+"?docid=\"+IDX+\"#AMP#menu=no#AMP#fromObj=y#AMP#method=edit#AMP#boui=\"+BOI.getAttribute(\"TEMPLATE\").getValueString()+\"#AMP#ctx_boui=\"+BOI.bo_boui #END#' frameBorder=0 width='100%' scrolling=no height='100%'>#BEGIN_TAG#/IFRAME#END_TAG#";
                StringBuffer x = new StringBuffer();
                x.append(
                    "#BEGIN_TAG#IFRAME   onload=\"winmain().loaded(getIDX())\" id='inc_TEMPLATE__#BEGIN#=BOI.bo_boui#END#' xsrc='#BEGIN#=\"" +
                    jspName.toString().toLowerCase() +
                    "?docid=\"+IDX+\"#AMP#menu=no#AMP#fromObj=y#AMP#method=edit#AMP#boui=\"+BOI.getAttribute(\"TEMPLATE\").getValueString()+\"#AMP#ctx_boui=\"+BOI.bo_boui #END#' frameBorder=0 width='100%' scrolling=no height='100%' ")
                 .append("tabindex='#BEGIN_TAG#%=DOC.getTabindex(DOC.AREA, BOI.getName(), String.valueOf(BOI.bo_boui), \"")
                 .append("inc_TEMPLATE__\" + String.valueOf(BOI.bo_boui)")
                 .append(", DOCLIST)%#END_TAG#'").append(">#BEGIN_TAG#/IFRAME#END_TAG#");
                xi.appendChild(xBody.createTextNode(x.toString()));
                xTABdiv.appendChild(xi);
            } else {
            if ((xTABS[i].getChildNodes().length > 0) &&
                    (xTABS[i].getChildNodes()[0] != null)) {
                Element xi = xBody.createElement("JSP_EXPRESSION");
                xi.appendChild(xBody.createTextNode("#BEGIN#{ int cvui" +
                        xTDbody.hashCode() + "=DOCLIST.cvui;#END#"));
                xTABdiv.appendChild(xi);

                for (int j = 0; j < xTABS[i].getChildNodes().length; j++) {
                    recursiveForm(xBody, xTABdiv, viewer,
                        xTABS[i].getChildNodes()[j], xbo_NodeCat);
                }

                xi = xBody.createElement("JSP_EXPRESSION");
                xi.appendChild(xBody.createTextNode("#BEGIN#DOCLIST.cvui=cvui" +
                        xTDbody.hashCode() + ";}#END#"));
                xTABdiv.appendChild(xi);
            }

            //                (XMLDocument xBody,boDefHandler bodef,ngtXMLHandler viewer,ngtXMLHandler form,String bo_NodeCat)
            //--------------------

            }
             xEndTabInstr = xBody.createElement("JSP_EXPRESSION");
            xEndTabInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
            TD_cont_divs.appendChild(xEndTabInstr);
        }

        xBodyApp.appendChild(xPanel);

        xEndTabInstr = xBody.createElement("JSP_EXPRESSION");
        xEndTabInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
        xBodyApp.appendChild(xEndTabInstr);
    }

    private void buildBox(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        //     Element xPanelInstr;
        //     String xPanelInstrText="";
        //      xPanelInstr=xBody.createElement("JSP_EXPRESSION");
        //     xBodyApp.appendChild(xPanelInstr);
        //    xPanelInstr=xBody.createElement("JSP_EXPRESSION");
        //     xBodyApp.appendChild(xPanelInstr);
        //     xPanelInstrText="";
        Element xBox = xBody.createElement("TABLE");
        xBox.setAttribute("height", form.getAttribute("height", "20%"));
        xBox.setAttribute("cellSpacing", "0");
        xBox.setAttribute("cellPadding", "0");
        xBox.setAttribute("width", form.getAttribute("width", "100%"));

        Element xcolGroup = xBody.createElement("colgroup");
        xBox.appendChild(xcolGroup);

        Element xcol1 = xBody.createElement("col");
        xcol1.setAttribute("width", "16");
        xBox.appendChild(xcol1);

        Element xcol2 = xBody.createElement("col");
        xBox.appendChild(xcol2);

        Element xcol3 = xBody.createElement("col");
        xcol3.setAttribute("width", "22");
        xBox.appendChild(xcol3);

        Element xb = xBody.createElement("TBODY");
        xBox.appendChild(xb);

        Element xtrh = xBody.createElement("tr");
        xb.appendChild(xtrh);
        xtrh.setAttribute("height", "20");

        Element xtd1 = xBody.createElement("TD");
        xtrh.appendChild(xtd1);
        xtd1.setAttribute("class", "box_header");

        Element ximg = xBody.createElement("IMG");
        xtd1.appendChild(ximg);
        ximg.setAttribute("src", "templates/boxinfo/std/box_on_left.gif");
        ximg.setAttribute("width", "16");
        ximg.setAttribute("height", "20");

        Element xtdh = xBody.createElement("TD");
        xtrh.appendChild(xtdh);
        xtdh.setAttribute("class", "box_header");
        xtdh.setAttribute("noWrap", "1");
        xtdh.appendChild(xBody.createTextNode(form.getAttribute("title", " ")));

        Element xtdr = xBody.createElement("TD");
        xtrh.appendChild(xtdr);
        xtdr.setAttribute("class", "box_header");
        xtdr.setAttribute("style", "CURSOR:HAND");
        xtdr.setAttribute("onclick", "showboxinfo(this)");

        Element ximgr = xBody.createElement("img");
        xtdr.appendChild(ximgr);
        ximgr.setAttribute("src", "templates/boxinfo/std/box_on_right_up.gif");
        ximgr.setAttribute("width", "22");
        ximgr.setAttribute("height", "20");

        Element xtrb = xBody.createElement("TR");
        xb.appendChild(xtrb);

        Element xtdbody = xBody.createElement("TD");
        xtrb.appendChild(xtdbody);
        xtdbody.setAttribute("colspan", "3");
        xtdbody.setAttribute("valign", "top");
        xtdbody.setAttribute("style",
            "height:" +
            form.getChildNode("content").getAttribute("height", "100px"));
        xtdbody.setAttribute("class", "box_body");

        recursiveForm(xBody, xtdbody, viewer,
            form.getChildNode("content").getChildNodes()[0], bo_NodeCat);

        xBodyApp.appendChild(xBox);

        //    <table height="1%" cellSpacing="0" cellPadding="0" width="100%">
        //     <colgroup>
        //     <col width="16">
        //     <col>
        //     <col width="22">
        //     <tbody>
        //     <tr height="20">
        //       <td><img src="templates/boxinfo/std/box_on_left.gif" WIDTH="16" HEIGHT="20"></td>
        //       <td style="FONT-WEIGHT: bold; FONT-SIZE: 12px; VERTICAL-ALIGN: middle; COLOR: #ffffff; BACKGROUND-COLOR: #889dc2" noWrap>Os meus Atalhos</td>
        //       <td style="CURSOR: hand" onclick="showboxinfo(this);"><img src="templates/boxinfo/std/box_on_right_up.gif" WIDTH="22" HEIGHT="20"></td></tr>
        //     <tr>
        //        <td valign='top' style="height:150px;padding:5px;BORDER-RIGHT: #889dc2 1px solid; BORDER-TOP: #889dc2 1px solid; BORDER-LEFT: #889dc2 1px solid; BORDER-BOTTOM: #889dc2 1px solid; BACKGROUND-COLOR: #ffffff" colSpan="3">
        //        teste erireo eroireu
        //       </td></tr></tbody></table>
    }

    private void buildFrame(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        Element xFrame = xBody.createElement("DIV");
        xBodyApp.appendChild(xFrame);
        xFrame.setAttribute("class", "frame");

        if (form.getAttribute("height") != null) {
            xFrame.setAttribute("height", form.getAttribute("height", "100%"));
        }

        if (form.getAttribute("style") != null) {
            xFrame.setAttribute("style", form.getAttribute("style"));
        }

        Element xtable = xBody.createElement("TABLE");
        xFrame.appendChild(xtable);

        xtable.setAttribute("height", "100%");
        xtable.setAttribute("width", "100%");
        xtable.setAttribute("cellSpacing", "0");
        xtable.setAttribute("cellPadding", "0");

        Element xbody = xBody.createElement("TBODY");
        xtable.appendChild(xbody);

        Element TRtop = xBody.createElement("TR");
        TRtop.setAttribute("height", "100%");
        xbody.appendChild(TRtop);

        Element TD1 = xBody.createElement("TD");
        TRtop.appendChild(TD1);

        Element TABLE1 = xBody.createElement("TABLE");
        TD1.appendChild(TABLE1);
        TABLE1.setAttribute("style", "TABLE-LAYOUT:fixed;height:100%;width:100%");
        TABLE1.setAttribute("cellSpacing", "0");
        TABLE1.setAttribute("cellPadding", "0");

        Element colgroup = xBody.createElement("COLGROUP");
        TABLE1.appendChild(colgroup);

        Element xcol;
        xcol = xBody.createElement("COL");
        xcol.setAttribute("width", "42");
        TABLE1.appendChild(xcol);
        xcol = xBody.createElement("COL");
        TABLE1.appendChild(xcol);
        xcol = xBody.createElement("COL");
        xcol.setAttribute("width", "6");
        TABLE1.appendChild(xcol);

        Element TABLE1_BODY = xBody.createElement("TBODY");
        TABLE1.appendChild(TABLE1_BODY);

        Element TR;
        Element TD;
        Element IMG;
        TR = xBody.createElement("TR");
        TR.setAttribute("height", "13");
        TR.setAttribute("colspan", "3");
        TABLE1_BODY.appendChild(TR);
        TD = xBody.createElement("TD");
        TR.appendChild(TD);
        IMG = xBody.createElement("IMG");
        TD.appendChild(IMG);
        IMG.setAttribute("src",
            form.getAttribute("img", "templates/frame/std/header") +
            "_top.gif");
        IMG.setAttribute("width", "42");
        IMG.setAttribute("height", "13");
        TR = xBody.createElement("TR");
        TABLE1_BODY.appendChild(TR);
        TR.setAttribute("height", "20");
        TD = xBody.createElement("TD");
        TR.appendChild(TD);
        IMG = xBody.createElement("IMG");
        TD.appendChild(IMG);
        IMG.setAttribute("src",
            form.getAttribute("img", "templates/frame/std/header") +
            "_bottom.gif");
        IMG.setAttribute("width", "42");
        IMG.setAttribute("height", "20");
        TD = xBody.createElement("TD");
        TR.appendChild(TD);
        TD.appendChild(xBody.createTextNode(form.getAttribute("title", "")));
        TD.setAttribute("class", "frameTitle");

        TD = xBody.createElement("TD");
        TR.appendChild(TD);
        TD.setAttribute("align", "right");
        IMG = xBody.createElement("IMG");
        TD.appendChild(IMG);
        IMG.setAttribute("src",
            form.getAttribute("img", "templates/frame/std/header") +
            "_right.gif");
        IMG.setAttribute("width", "6");
        IMG.setAttribute("height", "20");

        TR = xBody.createElement("TR");
        TABLE1_BODY.appendChild(TR);
        TR.setAttribute("height", "100%");

        Element TD_BODY_OUT = xBody.createElement("TD");
        TR.appendChild(TD_BODY_OUT);
        TD_BODY_OUT.setAttribute("class", "frame_out");
        TD_BODY_OUT.setAttribute("colspan", "3");

        Element table_Bo = xBody.createElement("TABLE");
        TD_BODY_OUT.appendChild(table_Bo);
        table_Bo.setAttribute("height", "100%");
        table_Bo.setAttribute("width", "100%");
        table_Bo.setAttribute("cellSpacing", "0");
        table_Bo.setAttribute("cellPadding", "0");
        colgroup = xBody.createElement("COLGROUP");
        table_Bo.appendChild(colgroup);
        xcol = xBody.createElement("COL");
        table_Bo.appendChild(xcol);
        xcol.setAttribute("width", "100%");
        xcol = xBody.createElement("COL");
        table_Bo.appendChild(xcol);
        xcol.setAttribute("width", "115");

        Element ttbody = xBody.createElement("TBODY");
        table_Bo.appendChild(ttbody);

        Element TR135 = xBody.createElement("TR");
        ttbody.appendChild(TR135);
        TD = xBody.createElement("TD");
        TR135.appendChild(TD);

        /*
        Element ttTABLE=xBody.createElement("TABLE");TD.appendChild(ttTABLE);
        ttTABLE.setAttribute("height","100%");
        ttTABLE.setAttribute("width","100%");
        ttTABLE.setAttribute("cellSpacing","0");
        ttTABLE.setAttribute("cellPadding","0");

        Element ttTABLE_BODY=xBody.createElement("TBODY");ttTABLE.appendChild(ttTABLE_BODY);
        TR=xBody.createElement("TR");ttTABLE_BODY.appendChild(TR);
        Element PRINCIPAL_BODY=xBody.createElement("TD");TR.appendChild(PRINCIPAL_BODY);
        PRINCIPAL_BODY.setAttribute("colspan","2");
        PRINCIPAL_BODY.setAttribute("align","left");
        PRINCIPAL_BODY.setAttribute("valign","top");

        */
        ngtXMLHandler xContent = form.getChildNode("content");
        String jspName = xContent.getAttribute("name").toLowerCase() + ".jsp";

        //String x="#BEGIN_TAG#IFRAME   id=' ' xsrc='#BEGIN#=\""+jspName.toString().toLowerCase()+"?docid=\"+IDX#END#' frameBorder=0 width='100%' scrolling=no height='100%'>#BEGIN_TAG#/IFRAME#END_TAG#";
        StringBuffer x = new StringBuffer();
        x.append("#BEGIN_TAG#IFRAME   id=' ' xsrc='#BEGIN#=\"")
         .append(jspName.toString().toLowerCase()).append("?docid=\"+IDX#END#' ")
         .append(" frameBorder=0 width='100%' scrolling=no height='100%'")
         .append("tabindex='#BEGIN_TAG#%=DOC.getTabindex(DOC.IFRAME, BOI==null?#QUOT#" +
            jspName.toString().toLowerCase() +
            "#QUOT# + String.valueOf(IDX) : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), \"  \"")
         .append(", DOCLIST)%#END_TAG#'").append(">#BEGIN_TAG#/IFRAME#END_TAG#");

        //PRINCIPAL_BODY.appendChild(xBody.createTextNode(x));
        TD.appendChild(xBody.createTextNode(x.toString()));

        //newPage(jspName,xBody,PRINCIPAL_BODY,viewer,xContent.getFirstChild(),bo_NodeCat);
        newPage(jspName, xBody, TD, viewer, xContent.getFirstChild(), bo_NodeCat);

        /*
        <DIV style="height:100%;PADDING-RIGHT: 20px; PADDING-LEFT: 20px; PADDING-BOTTOM: 20px; PADDING-TOP: 10px; BACKGROUND-COLOR: #eeeeee">
        <TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%">
          <TBODY>
          <TR id=topTR height="100%">
            <TD>
              <TABLE style="TABLE-LAYOUT: fixed" height="100%" cellSpacing=0 cellPadding=0 width="100%">
                <COLGROUP>
                <COL width=42>
                <COL>
                <COL width=6>
                <TBODY>
                <TR height=13 colspan="3">
                  <TD><IMG src="lixo/todo_header_topp.gif" width="42" height="13"></TD>
                </TR>
                <TR height=20>
                  <TD><IMG src="lixo/to-do_header_bottom.gif" width="42" height="20"></TD>
                  <TD style="PADDING-LEFT: 4px; FONT-WEIGHT: bold; COLOR: #ffffff" bgColor=red>TITULO </TD>
                  <TD align=right><IMG
                  src="lixo/todo_head-er_right.gif" width="6" height="20"></TD></TR>

                <TR height="100%">
                  <TD style="BORDER: red 1px solid; BACKGROUND-COLOR: #eeeeee" colSpan=3>
                    <TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%">
                      <COLGROUP>
                      <COL>
                      <COL width=135>
                      <TBODY>
                      <TR>
                        <TD>
                          <TABLE height="100%" cellSpacing=0 cellPadding=0  width="100%">
                          <TBODY>
                            <TR>
                              <TD colSpan=2 align=left valign=top>
                              <!--BEGIN BODY -->

                              7676
                               <!--END BODY -->
                              </TD></TR>
                                                        </TBODY></TABLE></TD>
                                */

        //AGORA É O MENU RIGHT
        Element TDm = xBody.createElement("TD");
        TR135.appendChild(TDm);
        TDm.setAttribute("style",
            "PADDING-RIGHT: 7px; PADDING-LEFT: 7px; PADDING-BOTTOM: 10px; PADDING-TOP: 0px");
        TDm.setAttribute("vAlign", "top");

        Element TABLEm = xBody.createElement("TABLE");
        TDm.appendChild(TABLEm);

        TABLEm.setAttribute("style", "WIDTH: 115px;HEIGHT: 100%");
        TABLEm.setAttribute("class", "frameMenu");
        TABLEm.setAttribute("cellpadding", "3");
        TABLEm.setAttribute("cellSpacing", "0");
        colgroup = xBody.createElement("COLGROUP");
        TABLEm.appendChild(colgroup);
        xcol = xBody.createElement("COL");
        xcol.setAttribute("width", "18");
        TABLEm.appendChild(xcol);
        xcol = xBody.createElement("COL");
        xcol.setAttribute("width", "97");
        TABLEm.appendChild(xcol);

        Element BODYm = xBody.createElement("TBODY");
        TABLEm.appendChild(BODYm);

        //   buildFrameMenu(Element BODYm,XMLDocument xBody,ngtXMLHandler form);
        ngtXMLHandler xmenu = form.getChildNode("menus");
        ngtXMLHandler[] xmenus = xmenu.getChildNodes();
        ngtXMLHandler[] options;

        Element TDH;
        Element TROPTION;
        Element TDI;

        Element TDT;
        Element TABLEsubm;
        Element BODYsubm;
        String xH;
        Element parent = BODYm;

        for (int i = 0; i < xmenus.length; i++) {
            xH = xmenus[i].getAttribute("header");

            String title = null;

            if ((xH != null) && xH.startsWith("STD:print")) {
                title = "Imprimir/Exportar";
            }

            options = xmenus[i].getChildNodes();
            TR = xBody.createElement("TR");
            parent.appendChild(TR);
            TDH = xBody.createElement("TD");
            TDH.setAttribute("class", "frameMenuHeader");
            TDH.setAttribute("colspan", "2");
            TDH.appendChild(xBody.createTextNode((title == null) ? xH : title));
            TR.appendChild(TDH);

            if ((xH != null) && xH.startsWith("STD:print")) {
                String vName = xH.substring(10, xH.length() - 1);
                StringBuffer sb = new StringBuffer();
                Element xi;

                //Impressão de Listagem (S/ Opções)
                xi = xBody.createElement("JSP_EXPRESSION");
                sb.append(
                    "#BEGIN_TAG#TR class=\"hand\" onmouseover=\"fmenuOn(this)\" onmouseout=\"fmenuOff(this)\" title=\"Imprimir Listagem\" onclick=\"winmain().open('__printTree.jsp?docid=#BEGIN#=DOC.getDocIdx()#END##AMP#viewer=" +
                    vName +
                    "#BEGIN#=bosession.getPerformerBoui()#END#','blank')\"#END_TAG# ")
                  .append("#BEGIN_TAG#TD#END_TAG#")
                  .append("#BEGIN_TAG#IMG src=\"templates/printexp/std/print24.gif\" tabIndex=\"resources/print16.gif\"/#END_TAG#")
                  .append("#BEGIN_TAG#/TD#END_TAG#")
                  .append("#BEGIN_TAG#TD#END_TAG#Imprimir Listagem#BEGIN_TAG#/TD#END_TAG#")
                  .append("#BEGIN_TAG#/TR#END_TAG#");
                xi.appendChild(xBody.createTextNode(sb.toString()));
                parent.appendChild(xi);

                //Exportação de Listagem (S/ Opções)
                sb = new StringBuffer();
                xi = xBody.createElement("JSP_EXPRESSION");
                sb.append(
                    "#BEGIN_TAG#TR class=\"hand\" onmouseover=\"fmenuOn(this)\" onmouseout=\"fmenuOff(this)\" title=\"Exportar Listagem\" onclick=\"winmain().openDocUrl('fixed,730px,450px','__choseExportList.jsp','?docid=#BEGIN#=DOC.getDocIdx()#END##AMP#viewer=" +
                    vName +
                    "#BEGIN#=bosession.getPerformerBoui()#END#','lookup')\"#END_TAG# ")
                  .append("#BEGIN_TAG#TD#END_TAG#")
                  .append("#BEGIN_TAG#IMG src=\"templates/printexp/std/export16.gif\" tabIndex=\"resources/export16.gif\"/#END_TAG#")
                  .append("#BEGIN_TAG#/TD#END_TAG#")
                  .append("#BEGIN_TAG#TD#END_TAG#Exportar Listagem#BEGIN_TAG#/TD#END_TAG#")
                  .append("#BEGIN_TAG#/TR#END_TAG#");
                xi.appendChild(xBody.createTextNode(sb.toString()));
                parent.appendChild(xi);

                //Impressão ou Exportação de Listagens (C/ Opções)
                sb = new StringBuffer();
                xi = xBody.createElement("JSP_EXPRESSION");
                sb.append(
                    "#BEGIN_TAG#TR class=\"hand\" onmouseover=\"fmenuOn(this)\" onmouseout=\"fmenuOff(this)\" title=\"Impressão/Exportação Avançada\" onclick=\"winmain().openDocUrl('medium','__chooseExportData.jsp','?docid=#BEGIN#=DOC.getDocIdx()#END##AMP#treeKey=" +
                    vName +
                    "#BEGIN#=bosession.getPerformerBoui()#END#','lookup')\"#END_TAG# ")
                  .append("#BEGIN_TAG#TD#END_TAG#")
                  .append("#BEGIN_TAG#IMG src=\"templates/printexp/std/avanc16.gif\" tabIndex=\"resources/avanc16.gif\"/#END_TAG#")
                  .append("#BEGIN_TAG#/TD#END_TAG#")
                  .append("#BEGIN_TAG#TD#END_TAG#Avançada#BEGIN_TAG#/TD#END_TAG#")
                  .append("#BEGIN_TAG#/TR#END_TAG#");
                xi.appendChild(xBody.createTextNode(sb.toString()));
                parent.appendChild(xi);

                //Gravar Definição de Listagens
                xi = xBody.createElement("JSP_EXPRESSION");
                sb = new StringBuffer();
                sb.append(
                    "#BEGIN#if(securityRights.hasRights(boctx,\"Ebo_treeDef\",securityRights.WRITE)){#END#")
                  .append("#BEGIN_TAG#TR class=\"hand\" onmouseover=\"fmenuOn(this)\" onmouseout=\"fmenuOff(this)\" title=\"Gravar Definições da Listagem\" onclick=\"winmain().openDocUrl('fixed,400px,150px,noresize','__saveTree.jsp','?docid=#BEGIN#=DOC.getDocIdx()#END##AMP#viewer=" +
                    vName +
                    "#BEGIN#=bosession.getPerformerBoui()#END#','lookup')\"#END_TAG# ")
                  .append("#BEGIN_TAG#TD#END_TAG#")
                  .append("#BEGIN_TAG#IMG src=\"templates/printexp/std/save16.gif\" tabIndex=\"resources/save16.gif\"/#END_TAG#")
                  .append("#BEGIN_TAG#/TD#END_TAG#")
                  .append("#BEGIN_TAG#TD#END_TAG#Gravar Definição#BEGIN_TAG#/TD#END_TAG#")
                  .append("#BEGIN_TAG#/TR#END_TAG#").append("#BEGIN#}#END#");
                xi.appendChild(xBody.createTextNode(sb.toString()));
                parent.appendChild(xi);
            }

            for (int z = 0; z < options.length; z++) {
                TROPTION = xBody.createElement("TR");
                parent.appendChild(TROPTION);
                TROPTION.setAttribute("class", "hand");
                TROPTION.setAttribute("onmouseover", "fmenuOn(this)");
                TROPTION.setAttribute("onmouseout", "fmenuOff(this)");
                TROPTION.setAttribute("title",
                    options[z].getAttribute("title", ""));
                TROPTION.setAttribute("onclick",
                    options[z].getChildNode("action").getText());
                TDI = xBody.createElement("TD");
                TROPTION.appendChild(TDI);
                IMG = xBody.createElement("IMG");
                TDI.appendChild(IMG);
                IMG.setAttribute("src", options[z].getChildNode("img").getText());
                IMG.setAttribute("tabIndex",
                    options[z].getChildNode("img").getText());
                TDT = xBody.createElement("TD");
                TROPTION.appendChild(TDT);
                TDT.appendChild(xBody.createTextNode(options[z].getChildNode(
                            "text").getText()));
            }

            if ((i + 1) < xmenus.length) {
                TR = xBody.createElement("TR");
                BODYm.appendChild(TR);

                TDm = xBody.createElement("TD");
                TR.appendChild(TDm);
                TDm.setAttribute("style",
                    "PADDING-RIGHT: 0px; PADDING-LEFT: 0px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px");
                TDm.setAttribute("colspan", "2");
                TDm.setAttribute("vAlign", "top");
                TABLEsubm = xBody.createElement("TABLE");
                TDm.appendChild(TABLEsubm);

                TABLEsubm.setAttribute("style", "WIDTH: 115px;HEIGHT: 100%");
                TABLEsubm.setAttribute("class", "frameMenu");
                TABLEsubm.setAttribute("cellspacing", "0");
                TABLEsubm.setAttribute("cellPadding", "3");

                colgroup = xBody.createElement("COLGROUP");
                TABLEsubm.appendChild(colgroup);
                xcol = xBody.createElement("COL");
                xcol.setAttribute("width", "18");
                TABLEsubm.appendChild(xcol);
                xcol = xBody.createElement("COL");
                xcol.setAttribute("width", "97");
                TABLEsubm.appendChild(xcol);
                BODYsubm = xBody.createElement("TBODY");
                TABLEsubm.appendChild(BODYsubm);
                parent = BODYsubm;
            }
        }

        Element TR100 = xBody.createElement("TR");
        BODYm.appendChild(TR100);
        TR100.setAttribute("height", "100%");
        TR100.setAttribute("class", "framemenuLastRow");
        TD = xBody.createElement("TD");
        TD.setAttribute("colspan", "2");
        TR100.appendChild(TD);
        TD.appendChild(xBody.createTextNode("#AMP#nbsp;"));

        //TD_BODY_OUT.setAttribute

        /*

                        <TD style="PADDING-RIGHT: 10px; PADDING-LEFT: 10px; PADDING-BOTTOM: 10px; PADDING-TOP: 0px" vAlign=top>
                          <TABLE style="WIDTH: 135px; COLOR: #666666; HEIGHT: 100%" cellSpacing=0 cellPadding=3>
                            <COLGROUP>
                            <COL width=18>
                            <COL width=117>
                            <TBODY>
                            <TR><TD class=frameMenuHeader colSpan=2>TITULO1</TD></TR>
                            <TR class=hand onmouseover=on(this);   title="Create a new Task" onclick="alert('1');" onmouseout=off(this);>
                              <TD><IMG src="lixo/ico_16_134.gif" width="16" height="16"></TD>
                              <TD>Task</TD>
                            </TR>
                            <TR class=hand onmouseover=on(this); title="Create a new Fax" onclick=openObj(136); onmouseout=off(this);>
                              <TD><IMG src="lixo/ico_16_136.gif" width="16" height="16"></TD>
                              <TD>Fax</TD>
                            </TR>

                            <TR>
                               <TD style="PADDING-RIGHT: 0px; PADDING-LEFT: 0px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px" colSpan=2>
                                 <TABLE class=add id=tblCreateRecords cellSpacing=0 cellPadding=3>
                                    <COLGROUP>
                                    <COL width=18>
                                    <COL width=117>
                                   <TBODY>
                                   <TR>
                                      <TD class=header style="PADDING-TOP: 10px" colSpan=2>TITULO2</TD>
                                   </TR>
                                  <TR class=hand onmouseover=on(this); title="Create a new Lead" onclick=openObj(4); onmouseout=off(this);>
                                    <TD><IMG src="lixo/ico_16_4.gif" width="16" height="16"></TD>
                                    <TD>Lead</TD>
                                  </TR>
                                   </TBODY></TABLE></TD></TR>

                           <TR height="100%">
                              <TD colSpan=2>&nbsp;</TD>
                           </TR>
                            </TBODY></TABLE></TD></TR></TBODY></TABLE></TD></TR></TBODY></TABLE></TD></TR>
                </TBODY>
                </TABLE>
        </DIV>
        */
    }

    /*    private void    buildFrameMenu(Element BODYm,XMLDocument xBody,ngtXMLHandler form)
        {
           ngtXMLHandler xmenu=form.getChildNode("menus");
           ngtXMLHandler[] xmenus=xmenu.getChildNodes();

        }*/
    private void buildAreas(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        /*
        <TABLE id='1111' class='layout' cellSpacing=0 cellPadding=0>
          <COLGROUP>
          <COL width='140'>
          <COL>
          <TBODY>
          <TR height='48'>

                        <td colspan=2>

                                 menu

                        </td>
          </TR>
          <TR>
            <TD  class=leftBar>

              <SPAN class=leftBar id=crmNavBar defaultArea="navInfo">

              //@FOREACH AREA
              <DIV class=lbItem id=navInfo
              relatedAreaId="areaForm"
              title="View general information about this record"
              onclick="so(1111);loadArea('areaForm');" tabIndex=0><IMG
              src="c:/tabls/leftbar_ficheiros/ico_18_1.gif" align=absMiddle>&nbsp;
              Information</DIV>
              //@FOREACH AREA

              </SPAN>
            </TD>


                <TD id=tdAreas>
              //@FOREACH AREA
              <DIV class=area id=areaForm>

              </DIV>
             //@ENDFOREACHAREA
                </TD>

                </TR>
          <TR>
            <TD class=statusBar colSpan=2>

            <B>Status:</B>Existing

            </TD>
           </TR>
        </TBODY>

        </TABLE>
        */
        p_haveAreas = true;

        Element xAreasInstr;
        String xAreasInstrText = "";

        xAreasInstr = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xAreasInstr);
        xAreasInstrText = "#BEGIN#";
        xAreasInstrText += "DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui());";

        //            xAreasInstrText+="DOCLIST.vui++;";
        //            xAreasInstrText+="DOCLIST.cvui=DOCLIST.vui;";
        xAreasInstrText += "#END#";
        xAreasInstr.appendChild(xBody.createTextNode(xAreasInstrText));

        xAreasInstr = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xAreasInstr);
        xAreasInstrText = "";

        /*
        <TABLE id='1111' class='layout' cellSpacing=0 cellPadding=0>
          <COLGROUP>
          <COL width='140'>
          <COL>
          <TBODY>
          <TR height='48'>

                        <td colspan=2>

                                 menu

                        </td>
          </TR>
        */
        Element xAreas = xBody.createElement("TABLE");
        xAreas.setAttribute("id", "princ_#BEGIN#=DOCLIST.cvui#END#");
        xAreas.setAttribute("class", "layout");
        xAreas.setAttribute("cellspacing", "0");
        xAreas.setAttribute("cellpadding", "0");

        ngtXMLHandler[] xAREAS = form.getChildNodes();

        String xbo_NodeCat;
        xbo_NodeCat = form.getAttribute("bo_node");

        if (xbo_NodeCat == null) {
            xbo_NodeCat = bo_NodeCat;
        }

        Attr[] xlist = form.getAttributes();

        for (int i = 0; i < xlist.length; i++) {
            xAreas.setAttribute(xlist[i].getName(), xlist[i].getValue());
        }

        if (xAreas.getAttribute("class") == null) {
            xAreas.setAttribute("class", "layout");
        }

        if (xAreas.getAttribute("cellspacing") == null) {
            xAreas.setAttribute("cellspacing", "0");
        }

        if (xAreas.getAttribute("cellpadding") == null) {
            xAreas.setAttribute("cellpadding", "0");
        }

        Element tColGroup = xBody.createElement("COLGROUP");
        xAreas.appendChild(tColGroup);

        Element col1 = xBody.createElement("COL");
        xAreas.appendChild(col1);
        col1.setAttribute("width", "140");

        Element col2 = xBody.createElement("COL");
        xAreas.appendChild(col2);

        Element TBODY_areas = xBody.createElement("TBODY");
        xAreas.appendChild(TBODY_areas);

        //   Element TR_menu_areas=xBody.createElement("TR");TBODY_areas.appendChild(TR_menu_areas);
        //   TR_menu_areas.setAttribute("height","#BEGIN#=DOC.getHEIGHT_HTMLforToolbar(pageContext,currObjectList)#END#");
        //   Element TD_menu_areas=xBody.createElement("TD");TR_menu_areas.appendChild(TD_menu_areas);
        //   TD_menu_areas.setAttribute("colspan","2");
        //   String TD_menu_expr="#BEGIN#DOC.writeHTMLforToolbar(pageContext,currObjectList); #END#";
        //    TD_menu_areas.appendChild(xBody.createTextNode(TD_menu_expr ));
        // fim menu

        /*
         * <TR>
           <TD  class=leftBar>

             <SPAN class=leftBar id=crmNavBar defaultArea="navInfo">

             //@FOREACH AREA
             <DIV class=lbItem id=navInfo
             relatedAreaId="areaForm"
             title="View general information about this record"
             onclick="so(1111);loadArea('areaForm');" tabIndex=0><IMG
             src="c:/tabls/leftbar_ficheiros/ico_18_1.gif" align=absMiddle>&nbsp;
             Information</DIV>
             //@FOREACH AREA

             </SPAN>
           </TD>


               <TD id=tdAreas>
             //@FOREACH AREA
             <DIV class=area id=areaForm>

             </DIV>
            //@ENDFOREACHAREA
               </TD>

               </TR>
        */
        Element TR_pbody_areas = xBody.createElement("TR");
        TBODY_areas.appendChild(TR_pbody_areas);

        Element TD_pheader_areas = xBody.createElement("TD");
        TR_pbody_areas.appendChild(TD_pheader_areas);
        TD_pheader_areas.setAttribute("class", "leftbar");

        Element SPAN_cont_divs = xBody.createElement("SPAN");
        TD_pheader_areas.appendChild(SPAN_cont_divs);
        SPAN_cont_divs.setAttribute("class", "leftbar");
        SPAN_cont_divs.setAttribute("id", "#BEGIN#=DOCLIST.cvui#END#");
        SPAN_cont_divs.setAttribute("defaultArea",
            "area_#BEGIN#=DOCLIST.cvui#END#_0");

        Element xBeginTabInstr;
        Element xEndTabInstr;
        Element xDiv;
        Element xIMG;
        String xConstraint;
        boolean hasActionArea = false;
        Element tbodyactionarea = null;

        for (int i = 0; i < xAREAS.length; i++) {
            if (xAREAS[i].getNodeName().equalsIgnoreCase("area")) {
                xbo_NodeCat = xAREAS[i].getAttribute("bo_node");

                if (xbo_NodeCat == null) {
                    xbo_NodeCat = bo_NodeCat;
                }

                xBeginTabInstr = xBody.createElement("JSP_EXPRESSION");
                SPAN_cont_divs.appendChild(xBeginTabInstr);

                xConstraint = xAREAS[i].getAttribute("constraint", "no");

                if ("when_object_in_template_mode".equalsIgnoreCase(xConstraint)) {
                    xBeginTabInstr.appendChild(xBody.createTextNode(
                            "#BEGIN#if (BOI.getMode()==netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE ){ #END#"));
                } else if ("when_extend_attribute_in_template_mode".equalsIgnoreCase(
                            xConstraint)) {
                    xBeginTabInstr.appendChild(xBody.createTextNode(
                            "#BEGIN#if (BOI.getMode()==netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE ){ #END#"));
                } else if ("WHEN_MAKE_REQUEST".equalsIgnoreCase(xConstraint)) {
                    xBeginTabInstr.appendChild(xBody.createTextNode(
                            "#BEGIN#if (BOI.getMode()==netgest.bo.runtime.boObject.MODE_MAKE_REQUEST ){ #END#"));
                } else if ((xConstraint != null) &&
                        xConstraint.toUpperCase().startsWith("INTERFACE:") && !p_iswizard) {
                    String intf = xConstraint.split(":")[1];
                    xBeginTabInstr.appendChild(xBody.createTextNode(
                            "#BEGIN#if (BOI.getAttribute(\"implements_" + intf +
                            "\").getValueString().equals(\"S\") ){ #END#"));
                } else {
                    xBeginTabInstr.appendChild(xBody.createTextNode(
                            "#BEGIN#if (DOC.hasCategoryRights(\"" +
                            xbo_NodeCat + "\")){ #END#"));

                    if (xAreasInstrText.equals("")) {
                        xAreasInstrText = "#BEGIN#if(DOC.hasCategoryRights(\"" +
                            xbo_NodeCat + "\")";
                    } else {
                        xAreasInstrText += ("|| DOC.hasCategoryRights(\"" +
                        xbo_NodeCat + "\")");
                    }
                }

                /*
                      <DIV class=lbItem id=navInfo
                      relatedAreaId="areaForm"
                      title="View general information about this record"
                      onclick="so(1111);loadArea('areaForm');" tabIndex=0><IMG
                      src="c:/tabls/leftbar_ficheiros/ico_18_1.gif" align=absMiddle>&nbsp;
                      Information</DIV>
                */
                xDiv = xBody.createElement("DIV");
                xDiv.setAttribute("class", "lbItem");

                String areaName = xAREAS[i].getAttribute("name");

                if (areaName != null) {
                    xDiv.setAttribute("name", areaName);
                }

                xDiv.setAttribute("id", "area_#BEGIN#=DOCLIST.cvui#END#_" + i);

                //xDiv.setAttribute("tabIndex","#BEGIN#=DOCLIST.tabindex++#END#");
                StringBuffer getTab = new StringBuffer();
                getTab.append(
                    "#BEGIN_TAG#%=DOC.getTabindex(DOC.AREA, BOI==null?#QUOT#area_#QUOT# + String.valueOf(DOCLIST.cvui) + #QUOT#_" +
                    i +
                    "#QUOT# : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), ")
                      .append("#QUOT#area_#QUOT# + String.valueOf(DOCLIST.cvui) + #QUOT#_" +
                    i + "#QUOT#").append(", DOCLIST)%#END_TAG#");
                xDiv.setAttribute("tabIndex", getTab.toString());
                xDiv.setAttribute("relatedAreaId",
                    "barea_#BEGIN#=DOCLIST.cvui#END#_" + i);
                xDiv.setAttribute("areaNumber", "#BEGIN#=DOCLIST.cvui#END#");
                xDiv.setAttribute("onclick",
                    "so('#BEGIN#=DOCLIST.cvui#END#');loadArea('barea_#BEGIN#=DOCLIST.cvui#END#_" +
                    i + "');");

                //                xDiv.appendChild(xBody.createTextNode("#BEGIN#=DOC.getCategoryLabel_for_TAB_Header(\""+xbo_NodeCat+"\")#END#"));
                xIMG = xBody.createElement("IMG");
                xDiv.appendChild(xIMG);

                if (xAREAS[i].getAttribute("img") != null) {
                    xIMG.setAttribute("src", xAREAS[i].getAttribute("img"));
                } else {
                    xIMG.setAttribute("src", "resources/area.gif");
                }

                String xlabel = xAREAS[i].getAttribute("label", "");
                String xLabelText = parseExpr(form, viewer, xbo_NodeCat, xlabel);

                // xLabelText=tools.replacestr(xLabelText,"#BEGIN#=","");
                // xLabelText=tools.replacestr(xLabelText,"#END#","");
                xDiv.appendChild(xBody.createTextNode(xLabelText));

                SPAN_cont_divs.appendChild(xDiv);

                xEndTabInstr = xBody.createElement("JSP_EXPRESSION");
                xEndTabInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
                SPAN_cont_divs.appendChild(xEndTabInstr);
            } else if (xAREAS[i].getNodeName().equalsIgnoreCase("actionarea")) {
                if (!hasActionArea) {
                    Element newtable = xBody.createElement("TABLE");
                    TD_pheader_areas.appendChild(newtable);
                    newtable.setAttribute("style", "WIDTH: 150px;HEIGHT: 10%");
                    newtable.setAttribute("class", "frameMenu");
                    newtable.setAttribute("cellpadding", "3");
                    newtable.setAttribute("cellSpacing", "0");

                    Element colGroup = xBody.createElement("COLGROUP");
                    newtable.appendChild(colGroup);

                    Element col = xBody.createElement("COL");
                    newtable.appendChild(col);
                    col.setAttribute("width", "18");
                    col = xBody.createElement("COL");
                    newtable.appendChild(col);
                    col.setAttribute("width", "97");
                    tbodyactionarea = xBody.createElement("TBODY");
                    newtable.appendChild(tbodyactionarea);

                    Element tr = xBody.createElement("TR");
                    tbodyactionarea.appendChild(tr);

                    Element td = xBody.createElement("TD");
                    tbodyactionarea.appendChild(td);
                    td.setAttribute("class", "frameMenuHeader");
                    td.setAttribute("colspan", "2");
                    td.appendChild(xBody.createTextNode("Acções"));
                    hasActionArea = true;
                }

                Element newtr = xBody.createElement("TR");
                tbodyactionarea.appendChild(newtr);

                String icon = xAREAS[i].getAttribute("icon");
                String action = xAREAS[i].getAttribute("action");
                String label = xAREAS[i].getAttribute("label");

                if ((icon != null) && (action != null)) {
                    newtr.setAttribute("class", "hand");
                    newtr.setAttribute("onmouseover", "fmenuOn(this)");
                    newtr.setAttribute("onmouseout", "fmenuOff(this)");
                    newtr.setAttribute("title", label);
                    newtr.setAttribute("onclick", action);

                    Element newtd = xBody.createElement("TD");
                    newtr.appendChild(newtd);

                    Element img = xBody.createElement("IMG");
                    newtd.appendChild(img);
                    img.setAttribute("src", icon);
                    img.setAttribute("onclick", action);
                    img.setAttribute("tabIndex", icon);
                    newtd = xBody.createElement("TD");
                    newtr.appendChild(newtd);
                    newtd.appendChild(xBody.createTextNode(label));
                }
            }
        }

        xAreasInstrText += "){#END#";
        xAreasInstr.appendChild(xBody.createTextNode(xAreasInstrText));

        /*
                <TD id=tdAreas>
              //@FOREACH AREA
              <DIV class=area id=areaForm>

              </DIV>
             //@ENDFOREACHAREA
                </TD>
        */
        Element TD_cont_divs = xBody.createElement("TD");
        TR_pbody_areas.appendChild(TD_cont_divs);
        TD_cont_divs.setAttribute("id", "Areas_#BEGIN#=DOCLIST.cvui#END#");

        Element xAREAdiv;

        for (int i = 0; i < xAREAS.length; i++) {
            xbo_NodeCat = xAREAS[i].getAttribute("bo_node");

            if (xbo_NodeCat == null) {
                xbo_NodeCat = bo_NodeCat;
            }

            xBeginTabInstr = xBody.createElement("JSP_EXPRESSION");
            TD_cont_divs.appendChild(xBeginTabInstr);

            boolean isTemplate = false;

            xConstraint = xAREAS[i].getAttribute("constraint", "no");

            if ("when_object_in_template_mode".equalsIgnoreCase(xConstraint)) {
                isTemplate = true;
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (BOI.getMode()==netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE ){ #END#"));
            } else if ("when_extend_attribute_in_template_mode".equalsIgnoreCase(
                        xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (BOI.getMode()==netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE ){ #END#"));
            } else if ("WHEN_MAKE_REQUEST".equalsIgnoreCase(xConstraint)) {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (BOI.getMode()==netgest.bo.runtime.boObject.MODE_MAKE_REQUEST ){ #END#"));
            } else if ((xConstraint != null) &&
                    xConstraint.toUpperCase().startsWith("INTERFACE:") && !p_iswizard) {
                String intf = xConstraint.split(":")[1];
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (BOI.getAttribute(\"implements_" + intf +
                        "\").getValueString().equals(\"S\") ){ #END#"));
            } else {
                xBeginTabInstr.appendChild(xBody.createTextNode(
                        "#BEGIN#if (DOC.hasCategoryRights(\"" + xbo_NodeCat +
                        "\")){ #END#"));
            }

            xAREAdiv = xBody.createElement("DIV");
            TD_cont_divs.appendChild(xAREAdiv);
            lastAreaId = "\"barea_\" + DOCLIST.cvui + \"_" + i + "\"";
            xAREAdiv.setAttribute("id", "barea_#BEGIN#=DOCLIST.cvui#END#_" + i);

            if (i == 0) {
                xAREAdiv.setAttribute("class", "areaSEL");
            } else {
                xAREAdiv.setAttribute("class", "area");
            }

            // call Recursive form
            ngtXMLHandler xTDbody = new ngtXMLHandler(xAREAdiv);

            if ("WHEN_MAKE_REQUEST".equalsIgnoreCase(xConstraint) &&
                    (p_bodef.getModifyProtocol() != null)) {
                Element xi;
                xi = xBody.createElement("JSP_EXPRESSION");

                //String x="#BEGIN_TAG#IFRAME id='inc_requestControler__#BEGIN#=BOI.bo_boui#END#' xsrc='#BEGIN#=\"requestControler.jsp?docid=\"+IDX+\"#AMP#changedObject=\"+BOI.getBoui()+\"\"#END#' frameBorder=0 width='100%' scrolling=yes height='100%'>#BEGIN_TAG#/IFRAME#END_TAG#";
                //tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "detail", DOCLIST)%>'
                StringBuffer x = new StringBuffer();
                x.append(
					"#BEGIN_TAG#IFRAME id='inc_requestControler__#BEGIN#=BOI.bo_boui#END#' xsrc='#BEGIN#=\""+p_bodef.getModifyProtocol()+"_requestControler.jsp?docid=\"+IDX+\"#AMP#changedObject=\"+BOI.getBoui()+\"\"#END#' frameBorder=0 width='100%' scrolling=yes height='100%' ")
                 .append("tabindex='#BEGIN_TAG#%=DOC.getTabindex(DOC.AREA, BOI.getName(), String.valueOf(BOI.bo_boui), \"")
                 .append("inc_requestControler__\" + String.valueOf(BOI.bo_boui) + \"\"")
                 .append(", DOCLIST)%#END_TAG#'").append(">#BEGIN_TAG#/IFRAME#END_TAG#");
                xi.appendChild(xBody.createTextNode(x.toString()));
                xAREAdiv.appendChild(xi);
            } else if ((xConstraint != null) &&
                    xConstraint.toUpperCase().startsWith("INTERFACE:")) {
                String intf = xConstraint.split(":")[1];
                boDefHandler intDef = boDefHandler.getBoDefinition(intf);
                if( intDef == null )
                {
                    boolean toBreak = true;
                }
                ngtXMLHandler intEdit = intDef.getViewer("general").getForm("edit");
                if ((intEdit.getChildNodes().length > 0) &&
                        (intEdit.getChildNodes()[0] != null)) {
                    Element xi = xBody.createElement("JSP_EXPRESSION");
                    xi.appendChild(xBody.createTextNode("#BEGIN#{ int cvui" +
                            xTDbody.hashCode() + "=DOCLIST.cvui;#END#"));
                    xAREAdiv.appendChild(xi);

                    recursiveForm(xBody, xAREAdiv, viewer,
                        intEdit.getChildNodes()[0], xbo_NodeCat);
                    xi = xBody.createElement("JSP_EXPRESSION");
                    xi.appendChild(xBody.createTextNode(
                            "#BEGIN#DOCLIST.cvui=cvui" + xTDbody.hashCode() +
                            ";}#END#"));
                    xAREAdiv.appendChild(xi);
                }
            } else if ("when_object_in_template_mode".equalsIgnoreCase(
                        xConstraint)) {
                String objName = "Ebo_Template";
                String formName = "edit";
                String jspName = objName + "_" + "general" + formName + ".jsp";

                Element xi;

                xi = xBody.createElement("JSP_EXPRESSION");

                //String x="#BEGIN_TAG#IFRAME   onload=\"winmain().loaded(getIDX())\" id='inc_TEMPLATE__#BEGIN#=BOI.bo_boui#END#' xsrc='#BEGIN#=\""+jspName.toString().toLowerCase()+"?docid=\"+IDX+\"#AMP#menu=no#AMP#fromObj=y#AMP#method=edit#AMP#boui=\"+BOI.getAttribute(\"TEMPLATE\").getValueString()+\"#AMP#ctx_boui=\"+BOI.bo_boui #END#' frameBorder=0 width='100%' scrolling=no height='100%'>#BEGIN_TAG#/IFRAME#END_TAG#";
                StringBuffer x = new StringBuffer();
                x.append(
                    "#BEGIN_TAG#IFRAME   onload=\"winmain().loaded(getIDX())\" id='inc_TEMPLATE__#BEGIN#=BOI.bo_boui#END#' xsrc='#BEGIN#=\"" +
                    jspName.toString().toLowerCase() +
                    "?docid=\"+IDX+\"#AMP#menu=no#AMP#fromObj=y#AMP#method=edit#AMP#boui=\"+BOI.getAttribute(\"TEMPLATE\").getValueString()+\"#AMP#ctx_boui=\"+BOI.bo_boui #END#' frameBorder=0 width='100%' scrolling=no height='100%' ")
                 .append("tabindex='#BEGIN_TAG#%=DOC.getTabindex(DOC.AREA, BOI.getName(), String.valueOf(BOI.bo_boui), \"")
                 .append("inc_TEMPLATE__\" + String.valueOf(BOI.bo_boui)")
                 .append(", DOCLIST)%#END_TAG#'").append(">#BEGIN_TAG#/IFRAME#END_TAG#");
                xi.appendChild(xBody.createTextNode(x.toString()));
                xAREAdiv.appendChild(xi);
            } else {
                if ((xAREAS[i].getChildNodes().length > 0) &&
                        (xAREAS[i].getChildNodes()[0] != null)) {
                    Element xi = xBody.createElement("JSP_EXPRESSION");
                    xi.appendChild(xBody.createTextNode("#BEGIN#{ int cvui" +
                            xTDbody.hashCode() + "=DOCLIST.cvui;#END#"));
                    xAREAdiv.appendChild(xi);

                    recursiveForm(xBody, xAREAdiv, viewer,
                        xAREAS[i].getChildNodes()[0], xbo_NodeCat);
                    xi = xBody.createElement("JSP_EXPRESSION");
                    xi.appendChild(xBody.createTextNode(
                            "#BEGIN#DOCLIST.cvui=cvui" + xTDbody.hashCode() +
                            ";}#END#"));
                    xAREAdiv.appendChild(xi);
                }
            }

            //                (XMLDocument xBody,boDefHandler bodef,ngtXMLHandler viewer,ngtXMLHandler form,String bo_NodeCat)
            //--------------------
            xEndTabInstr = xBody.createElement("JSP_EXPRESSION");
            xEndTabInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
            TD_cont_divs.appendChild(xEndTabInstr);
        }

        /*
         * <TR>
            <TD class=statusBar colSpan=2>

            <B>Status:</B>Existing

            </TD>
           </TR>

         */
        Element TR_status_areas = xBody.createElement("TR");

        // TBODY_areas.appendChild(TR_status_areas); nao inclui status na area, fica a outo nivel
        Element TD_status_areas = xBody.createElement("TD");
        TR_status_areas.appendChild(TD_status_areas);
        TD_status_areas.setAttribute("colspan", "2");
        TD_status_areas.setAttribute("class", "statusBar");

        String TD_status_expr = "#BEGIN#=DOC.getHTMLstatus()#END#";
        TD_status_areas.appendChild(xBody.createTextNode(TD_status_expr));

        xBodyApp.appendChild(xAreas);

        xEndTabInstr = xBody.createElement("JSP_EXPRESSION");
        xEndTabInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
        xBodyApp.appendChild(xEndTabInstr);
    }

    private void buildGridGroup(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        Element xEle;
        Element xInstr = null;
        Element xInstr2 = null;
        String xInstrText = "";
        String xInstrText2 = "";
        String xBOUI;
        String xNodeName = form.getNodeName();
        String xbo_NodeCat;
        boolean repeat;
        xbo_NodeCat = form.getAttribute("bo_node");

        if (xbo_NodeCat != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode(
                    "#BEGIN#if(DOC.hasCategoryRights(\"" + xbo_NodeCat +
                    "\")){ #END#"));
        } else {
            xbo_NodeCat = bo_NodeCat;
        }

        ngtXMLHandler xcols = form.getChildNode("cols");
        ngtXMLHandler[] xChilds = xcols.getChildNodes();

        xInstr2 = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xInstr2);

        StringBuffer xCode = new StringBuffer();

        xCode.append("#BEGIN#\n");
        xCode.append("gridG = DOC.getGroupGRID(\"" + "g" + form.hashCode() +
            "\");\n");
        xCode.append("if(gridG==null) { \n");
        xCode.append("  gridG = DOC.createGroupGRID(\"" + "g" +
            form.hashCode() + "\");\n");
        xCode.append("");

        ngtXMLHandler xnode;
        String xtype;
        ngtXMLHandler xattribute;
        String xnameAtr;
        String xwidth;
        String xviewmode;
        String xmethod;
        Attr[] xAttrList;
        boolean continua = true;

        //            String[] modes;
        for (int i = 0; i < xChilds.length; i++) {
            xnode = xChilds[i];

            if (xnode.getNodeName().equalsIgnoreCase("col")) {
                xtype = xnode.getAttribute("type");
                xattribute = xnode.getChildNode("attribute");

                if (xattribute != null) {
                    xnameAtr = xattribute.getText();
                    continua = true;
                    xAttrList = xnode.getAttributes();
                    xCode.append("xattributes=new Hashtable();\n");

                    for (int z = 0; z < xAttrList.length; z++) {
                        xCode.append("xattributes.put(\"" +
                            xAttrList[z].getName() + "\",\"" +
                            xAttrList[z].getValue() + "\");");
                    }

                    xwidth = xnode.getAttribute("width", "40");
                    xCode.append("gridG.addCol(\"" + xnameAtr + "\"," + xwidth +
                        ",xattributes);\n");
                }
            }
        }

        ngtXMLHandler xgroups = form.getChildNode("groups");
        xChilds = xgroups.getChildNodes();

        ngtXMLHandler xBOQL;
        ngtXMLHandler xHeader;

        for (int i = 0; i < xChilds.length; i++) {
            xnode = xChilds[i];

            if (xnode.getNodeName().equalsIgnoreCase("group")) {
                xtype = xnode.getAttribute("type");
                xBOQL = xnode.getChildNode("boql");
                xHeader = xnode.getChildNode("header");

                if (xBOQL != null) {
                    xAttrList = xHeader.getAttributes();
                    xCode.append("xattributes=new Hashtable();\n");

                    for (int z = 0; z < xAttrList.length; z++) {
                        xCode.append("xattributes.put(\"" +
                            xAttrList[z].getName() + "\",\"" +
                            xAttrList[z].getValue() + "\");");
                    }

                    xCode.append("gridG.addGroup(\"" + xBOQL.getText() +
                        "\",\"" + xHeader.getText() + "\",xattributes);\n");
                }
            }
        }

        xCode.append("} //endCreateGroupgrid\n");
        xCode.append("gridG.render(pageContext,DOC,DOCLIST);");
        xCode.append("#END#\n");
        xInstr2.appendChild(xBody.createTextNode(xCode.toString()));

        if (xInstr != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
        }
    }

    private void buildTreeView(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        Element xEle;
        Element xInstr = null;
        Element xInstr2 = null;
        String xInstrText = "";
        String xInstrText2 = "";
        String xBOUI;
        String xNodeName = form.getNodeName();
        String xbo_NodeCat;
        boolean repeat;
        xbo_NodeCat = form.getAttribute("bo_node");

        if (xbo_NodeCat != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode(
                    "#BEGIN#if(DOC.hasCategoryRights(\"" + xbo_NodeCat +
                    "\")){ #END#"));
        } else {
            xbo_NodeCat = bo_NodeCat;
        }

        String view = viewer.getViewerName();
        boDefViewer xv = p_bodef.getViewer(view);

        xInstr2 = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xInstr2);

        StringBuffer xCode = new StringBuffer();

        //  docHTML_treeServer.getTree(boDefHandler bodef , ngtXMLHandler defTree,docHTML DOC )
        //  boDefHandler xbodef = boDefHandler.getBoDefinition( this.p_bodef.getName() );
        //   boDefViewer xv=xbodef.getViewer("General");
        //   xv.getForm("myWork" )
        //   viewer.getViewerName()
        //  boDefHandler xbodef = boDefHandler.getBoDefinition( this.p_bodef.getName() );
        //  docHTML_treeRuntime xtree=docHTML_treeServer.getTree(xbodef , xbodef.getPath("Viewers."+p_path), DOC );
        //  docHTML_treeView.render(xtree, pageContext.getPage() ,DOC,DOCLIST );
        xCode.append("#BEGIN#\n");
        xCode.append("boDefHandler xbodef = boDefHandler.getBoDefinition( \"" +
            this.p_bodef.getName() + "\");\n");
        xCode.append(
            "docHTML_treeRuntime xtree=docHTML_treeServer.getTree(xbodef , xbodef.getPath(\"Viewers." +
            p_path + "\"), DOC );\n");
        xCode.append(
            "docHTML_treeView.render(xtree, pageContext ,DOC,DOCLIST );\n");
        xCode.append("#END#\n");
        xCode.append("#BEGIN_TAG#script#END_TAG# window.gridKey='" +
            xCode.hashCode() + "'#BEGIN_TAG#/script#END_TAG#  \n");
        xInstr2.appendChild(xBody.createTextNode(xCode.toString()));

        if (xInstr != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
        }
    }
     private void buildExplorer(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        Element xEle;
        Element xInstr = null;
        Element xInstr2 = null;
        String xInstrText = "";
        String xInstrText2 = "";
        String xBOUI;
        String xNodeName = form.getNodeName();
        String xbo_NodeCat;
        boolean repeat;
        xbo_NodeCat = form.getAttribute("bo_node");

        if (xbo_NodeCat != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode(
                    "#BEGIN#if(DOC.hasCategoryRights(\"" + xbo_NodeCat +
                    "\")){ #END#"));
        } else {
            xbo_NodeCat = bo_NodeCat;
        }

        String view = viewer.getViewerName();
        boDefViewer xv = p_bodef.getViewer(view);

        xInstr2 = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xInstr2);

        StringBuffer xCode = new StringBuffer();

        xCode.append("#BEGIN_TAG#div width=\"100%\" height=\"100%\"#END_TAG#")
        .append("#BEGIN_TAG#IFRAME")
        .append("    src=\"__explorer.jsp?docid=#BEGIN_TAG#%=DOC.getDocIdx()%#END_TAG##AMP#pageName=#BEGIN_TAG#%=pageContext.getPage().getClass().getName()%#END_TAG##AMP#objectName="+this.p_bodef.getName() +"#AMP#form="+p_formName+"\" id='explorer' frameBorder='0' width='100%' height='100%' scrolling='no' tabindex='0'#END_TAG##BEGIN_TAG#/IFRAME#END_TAG#")
        .append("#BEGIN_TAG#/div#END_TAG#");

        xCode.append("#BEGIN_TAG#script#END_TAG# window.gridKey='" +
            xCode.hashCode() + "'#BEGIN_TAG#/script#END_TAG#  \n");
        xInstr2.appendChild(xBody.createTextNode(xCode.toString()));

        if (xInstr != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
        }
    }
    private void buildGrid(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        Element xEle;
        Element xInstr = null;
        Element xInstr2 = null;
        String xInstrText = "";
        String xInstrText2 = "";
        String xBOUI;
        String xNodeName = form.getNodeName();
        String xbo_NodeCat;
        boolean repeat;
        xbo_NodeCat = form.getAttribute("bo_node");

        if (xbo_NodeCat != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode(
                    "#BEGIN#if(DOC.hasCategoryRights(\"" + xbo_NodeCat +
                    "\")){ #END#"));
        } else {
            xbo_NodeCat = bo_NodeCat;
        }

        String xlabel = form.getAttribute("label", "");
        String xname = form.getAttribute("name", "");
        String xLabelText = parseExpr(form, viewer, bo_NodeCat, xlabel);
        xLabelText = xLabelText.replaceAll("#BEGIN#=", "");
        xLabelText = xLabelText.replaceAll("#END#=", "");

        String xShowLabel = form.getAttribute("showlabel","yes");

        ngtXMLHandler xcols = form.getChildNode("cols");
        ngtXMLHandler[] xChilds = xcols.getChildNodes();

        xInstr2 = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xInstr2);

        StringBuffer xCode = new StringBuffer();
        boolean isGeneralObject = false;
        boolean isForBridge = false;
        String bridgeName = form.getAttribute("forBridge");
        boDefAttribute bridgeAtr = null;
        boDefHandler runtimeBodef = p_bodef;

        if (bridgeName != null) {
            isForBridge = true;
            bridgeAtr = p_bodef.getAttributeRef(bridgeName);
            runtimeBodef = bridgeAtr.getReferencedObjectDef();
        }

        xCode.append("#BEGIN#\n");

        xCode.append("BOI=currObjectList.getObject();");
        if (runtimeBodef.getName().equalsIgnoreCase("boObject")) {
            isGeneralObject = true;
        }

        xCode.append("grid = DOC.getGRID(\"" + "g" + form.hashCode() +
            "\");\n");
        xCode.append("if(grid==null) { \n");
        xCode.append("  bodef=boDefHandler.getBoDefinition(\"" +
            runtimeBodef.getBoName() + "\");");

        //xCode.append("  boDefAttribute atr;");
        if (isForBridge) {
            xCode.append(
                "  boDefBridge bridgeDef=boDefHandler.getBoDefinition(\"" +
                p_bodef.getBoName() + "\").getAttributeRef(\"" + bridgeName +
                "\").getBridge();");
            xCode.append("  grid = DOC.createGRID(\"" + "g" + form.hashCode() +
                "\",\"" + form.getAttribute("template", "") + "\",true);\n");
        } else {
            xCode.append("  grid = DOC.createGRID(\"" + "g" + form.hashCode() +
                "\",\"" + form.getAttribute("template", "") + "\");\n");
        }

        xCode.append("  grid.setTitle(" + xLabelText + ");  \n");
        xCode.append("");

        ngtXMLHandler xnode;
        String xtype;
        ngtXMLHandler xattribute;
        String xnameAtr;
        boDefAttribute atr;
        String xwidth;
        String xviewmode;
        String xmethod;
        Attr[] xAttrList;
        boolean continua = true;

        //            String[] modes;
        boolean isBridgeAttribute = false;
        boolean isSpecialName = false;
        String specialName = null;

        for (int i = 0; i < xChilds.length; i++) {
            xnode = xChilds[i];
            isBridgeAttribute = false;
            isSpecialName = false;
            specialName = null;

            if (xnode.getNodeName().equalsIgnoreCase("col")) {
                xtype = xnode.getAttribute("type");
                xattribute = xnode.getChildNode("attribute");

                if (xattribute != null) {
                    xnameAtr = xattribute.getText();

                    /**
                     * Verificar se é specialName
                     **/
                    if (xnameAtr.equalsIgnoreCase("childObject.cardid")) {
                        isSpecialName = true;
                        specialName = "childObject.cardId";
                    }
                    // psantos ini 20061206
                    else if (xnameAtr.toLowerCase().startsWith("childobject.attributes"))
                    {
                        isSpecialName = true;
                        specialName = xnameAtr;
                    }
                    // psantos fim 20061206
                    else if (xnameAtr.equalsIgnoreCase("method")) {
                        isSpecialName = true;
                        specialName = xnameAtr;
                    }

                    atr = p_bodef.getAttributeRef(xnameAtr);

                    if ((atr == null) && isForBridge) {
                        boDefBridge bridgeDef = bridgeAtr.getBridge();
                        boDefHandler bref = bridgeAtr.getReferencedObjectDef();

                        if (bref == null) {
                            break;
                        }

                        atr = bref.getAttributeRef(xnameAtr);
                    }

                    if ((atr == null) && isForBridge) {
                        boDefBridge bridgeDef = bridgeAtr.getBridge();
                        atr = bridgeDef.getAttributeRef(xnameAtr);
                        isBridgeAttribute = true;
                    }

                    if ((atr != null) || isGeneralObject || isSpecialName) {
                        continua = true;

                        if (atr != null) {
                            if (atr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                boDefHandler xref = atr.getReferencedObjectDef();

                                if ((xref == null) /*||
                                        xref.getName().equalsIgnoreCase("boObject")*/) {
                                    continua = false;
                                }
                            }
                        }

                        if (continua) {
                            if (!isGeneralObject && !isBridgeAttribute &&
                                    (specialName == null)) {
                                // xCode.append("if(BOI.getAttribute(\""+xnameAtr+"\").hasRights()){\n");
                                xCode.append(
                                    "if ( ( BOI != null #AMP##AMP# BOI.getAttribute(\"" +
                                    xnameAtr +
                                    "\").hasRights() ) || securityRights.hasRights( DOC.getEboContext() , currObjectList.getBoDef().getName() ,\"" +
                                    xnameAtr + "\", securityRights.READ )){\n");
                            }

                            xAttrList = xnode.getAttributes();
                            xCode.append("xattributes=new Hashtable();\n");

                            for (int z = 0; z < xAttrList.length; z++) {
                                xCode.append("xattributes.put(\"" +
                                    xAttrList[z].getName() + "\",\"" +
                                    xAttrList[z].getValue() + "\");");
                            }

                            xwidth = xnode.getAttribute("width", "40");

                            if (specialName != null) {
                                xCode.append("grid.addColSpecial(\"" +
                                    specialName + "\"," + xwidth +
                                    ",xattributes);\n");
                            } else if (atr.getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) {
                                if (isBridgeAttribute) {
                                    xCode.append(
                                        "grid.addColBridgeAtr(bridgeDef.getAttributeRef(\"" +
                                        atr.getName() + "\")," + xwidth +
                                        ",xattributes);\n");
                                } else {
                                    if (isGeneralObject) {
                                        xCode.append(
                                            "grid.addColAbstractAtr(\"" +
                                            atr.getName() + "\"," + xwidth +
                                            ",xattributes);\n");
                                    } else {
                                        xCode.append(
                                            "grid.addColAtr(bodef.getAttributeRef(\"" +
                                            atr.getName() + "\")," + xwidth +
                                            ",xattributes);\n");
                                    }
                                }
                            } else if (atr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                xviewmode = xattribute.getAttribute("viewmode",
                                        ""); //linkto ,+showid

                                //modes=ClassUtils.splitToArray(xviewmode,"+");
                                xmethod = xattribute.getAttribute("method", "");

                                if (isBridgeAttribute) {
                                    xCode.append(
                                        "grid.addColBridgeAtr(bridgeDef.getAttributeRef(\"" +
                                        atr.getName() + "\")," + xwidth +
                                        ",\"" + xmethod + "\",\"" + xviewmode +
                                        "\",xattributes);\n");
                                } else {
                                    if (isGeneralObject) {
                                        xCode.append(
                                            "grid.addColAbstracttAtr(\"" +
                                            atr.getName() + "\")," + xwidth +
                                            ",\"" + xmethod + "\",\"" +
                                            xviewmode + "\",xattributes);\n");
                                    } else {
                                        xCode.append(
                                            "grid.addColAtr(bodef.getAttributeRef(\"" +
                                            atr.getName() + "\")," + xwidth +
                                            ",\"" + xmethod + "\",\"" +
                                            xviewmode + "\",xattributes);\n");
                                    }
                                }
                            }

                            if (!isGeneralObject && !isBridgeAttribute &&
                                    (specialName == null)) {
                                xCode.append("}");
                            }
                        }
                    }
                }
            }
        }

        xCode.append("} //endCreategrid\n");
        xCode.append("grid.render(pageContext,currObjectList,DOC, DOCLIST);");
        xCode.append("#END#\n");

        xInstr2.appendChild(xBody.createTextNode(xCode.toString()));

        if (xInstr != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
        }
    }

    private void buildStaticObject(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        Element xEle;
        Element xInstr = null;
        String xInstrText = "";

        String xNodeName = form.getNodeName();
        String xbo_NodeCat;
        boolean repeat;
        xbo_NodeCat = form.getAttribute("bo_node");

        if (xbo_NodeCat != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode(
                    "#BEGIN#if(DOC.hasCategoryRights(\"" + xbo_NodeCat +
                    "\")){ #END#"));
        } else {
            xbo_NodeCat = bo_NodeCat;
        }

        ngtXMLHandler[] obj = form.getChildNodes();
        String objName = obj[0].getNodeName();
        boDefHandler objDef = boDefHandler.getBoDefinition(objName);

        if (objDef != null) {
            ngtXMLHandler[] childs = obj[0].getChildNodes();
            ngtXMLHandler[] par = obj[0].getChildNode("parameters")
                                        .getChildNodes();

            String method = obj[0].getAttribute("method");
            String[] parameters = new String[par.length];
            String[] values = new String[par.length];
            String formName = "";

            for (int i = 0; i < par.length; i++) {
                if (par[i].getNodeName().equalsIgnoreCase("form")) {
                    formName = par[i].getText();
                }

                parameters[i] = par[i].getNodeName();
                values[i] = par[i].getText();

                if (values[i] == null) {
                    values[i] = "";
                }
            }

            StringBuffer code = new StringBuffer();
            String qry = "";

            if (!formName.equals("")) {
                code.append("#BEGIN_TAG#IFRAME id=");
                code.append("'inc_" + p_bodef.getName() + "_" + objName);
                code.append("' src='");

                String jspName = objName + "_" + "general" + formName + ".jsp";
                code.append(jspName.toLowerCase());
                code.append("?docid=#BEGIN#=IDX#END#");

                for (int i = 0; i < parameters.length; i++) {
                    code.append("#AMP#");

                    code.append(parameters[i]);
                    code.append("=");

                    String strEncoded = "";
                    try {
                        strEncoded = URLEncoder.encode( values[i], boConfig.getEncoding() );
                    } catch( Exception ex ) {
                        strEncoded = values[i];
                    }
                    code.append( strEncoded );
                }

                //code.append("' frameBorder=0 width='100%' scrolling=no height='100%'>#BEGIN_TAG#/IFRAME#END_TAG#");
                String height = form.getAttribute("height", "100%");
                String width = form.getAttribute("width", "100%");

                code.append("' frameBorder=0 width='" + width +
                    "' scrolling=no height='" + height + "' ")
                    .append("tabindex='#BEGIN_TAG#%=DOC.getTabindex(DOC.IFRAME, \"inc_" +
                    p_bodef.getName() + "_" + objName + "\"")
                    .append(", DOCLIST)%#END_TAG#'").append(">#BEGIN_TAG#/IFRAME#END_TAG#");

                Element xInstr2 = xBody.createElement("JSP_EXPRESSION");
                xBodyApp.appendChild(xInstr2);
                xInstr2.appendChild(xBody.createTextNode(code.toString()));
            }
        } else {
            logger.warn("Object " + objName + " referenced in " +
                this.p_jspName + " not deployed");
        }

        if (xInstr != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
        }
    }

    private void buildSection(XMLDocument xBody, Element xBodyApp,
        boDefViewer viewer, ngtXMLHandler form, String bo_NodeCat) {
        Element xEle;
        Element xInstr = null;
        Element xInstr2 = null;
        String xInstrText = "";
        String xInstrText2 = "";
        String xBOUI;
        String xNodeName = form.getNodeName();
        String xbo_NodeCat;
        boolean repeat;
        xbo_NodeCat = form.getAttribute("bo_node");

        if (xbo_NodeCat != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode(
                    "#BEGIN#if(DOC.hasCategoryRights(\"" + xbo_NodeCat +
                    "\")){ #END#"));
        } else {
            xbo_NodeCat = bo_NodeCat;
        }

        String xlabel = form.getAttribute("label", "");

        String xname = form.getAttribute("name", "");
        String xid = form.getAttribute("id", "");
        String xLabelText = parseExpr(form, viewer, xbo_NodeCat, xlabel);
        xLabelText = xLabelText.replaceAll("#BEGIN#=", "");
        xLabelText = xLabelText.replaceAll("#END#", "");

        String xShowLabel = form.getAttribute("showlabel","yes");
        String xFormat = form.getAttribute("format");
        String xHeight = form.getAttribute("height");
        String xWidth = form.getAttribute("width");
        String xConstraint = form.getAttribute("constraint");
        ngtXMLHandler xrows = form.getChildNode("rows");
        ngtXMLHandler[] xChilds = xrows != null ?xrows.getChildNodes():null;

        xInstr2 = xBody.createElement("JSP_EXPRESSION");
        xBodyApp.appendChild(xInstr2);

        StringBuffer xCode = new StringBuffer();

        xCode.append("#BEGIN#\n");

        xCode.append("BOI=currObjectList.getObject();");

        if ((xConstraint != null) &&
                        xConstraint.toUpperCase().startsWith("INTERFACE:"))
        {
                    String path[] = xConstraint.split(":");
                    String intf = path[1];
                    String int_viewer = path.length == 3 ? path[2]:"edit";
                    if(!p_iswizard)
                    {
                        xCode.append(
                                "if (BOI.getAttribute(\"implements_" + intf +
                                "\").getValueString().equals(\"S\") ){");
                    }
                    boDefHandler bodef = boDefHandler.getBoDefinition(intf);
                    xrows = bodef.getViewer( "general" ).getForm( int_viewer ).getChildNode("rows");
                    xChilds = xrows != null ?xrows.getChildNodes():null;
        }

        xCode.append("sec = DOC.getSection(\"" + "s" + form.hashCode() +
            "\");\n");

        //if the section is already created the set tile in not invoced
        xCode.append("if(sec != null)\n");

        if (xlabel.equals(xLabelText)) {
            //the case where the section has the name of parent attribute label
            if (xlabel.equals("parentAttribute.label")) {
                xCode.append(
                    "  sec.setTitle(request.getParameter(\"parent_attribute_label\"));\n");
            } else {
                xCode.append("  sec.setTitle(\"" + xLabelText + "\");\n");
            }
        } else
        {
            xCode.append("  sec.setTitle(" + xLabelText + ");\n");
        }

        //
        xCode.append("if(sec==null) { \n");

        xCode.append("  sec = DOC.createSection(\"" + "s" + form.hashCode() +
            "\",\"" + form.getAttribute("template", "") + "\");\n");

        xCode.append("  sec.p_name = \"" + xname + "\";\n");
        if(xid == null || "".equals(xid))
        {
            xCode.append("  sec.p_id = \"" + xname + "\";\n");
        }
        else
        {
            xCode.append("  sec.p_id = \"" + xid + "\";\n");
        }

        if (xShowLabel.equalsIgnoreCase("no") ||
                xShowLabel.equalsIgnoreCase("n")) {
            xCode.append("sec.p_showLabel=false;\n");
        }

        xCode.append("  bodef=boDefHandler.getBoDefinition(\"" +
            p_bodef.getBoName() + "\");");

        if (xlabel.equals(xLabelText)) {
            if (xlabel.equals("parentAttribute.label")) {
                xCode.append(
                    "  sec.setTitle(request.getParameter(\"parent_attribute_label\"));\n");
            } else {
                xCode.append("  sec.setTitle(\"" + xLabelText + "\");\n");
            }
        } else {
            xCode.append("  sec.setTitle(" + xLabelText + ");\n");
        }

        if (xHeight != null) {
            xCode.append("  sec.p_height=\"" + xHeight + "\";\n");
        }

        if (xWidth != null) {
            xCode.append("  sec.p_width=\"" + xWidth + "\";\n");
        }
        if ( xFormat!= null )
        {
            xCode.append("  sec.setFormat(\"" + xFormat + "\");\n");
        }

        xCode.append("  docHTML_sectionRow row; \n");
        xCode.append("");

        ngtXMLHandler xnode;
        String xtype;
        ngtXMLHandler xattribute;
        String xnameAtr;
        boDefAttribute atr;
        String xwidth;
        String xviewmode;
        String xmethod;
        ngtXMLHandler[] xCells;
        Attr[] xAttrList;

        //            String[] modes;
        boolean continua;
        Hashtable renderAtr = new Hashtable();

        ngtXMLHandler xText;
        ngtXMLHandler xTag;


        for (int i = 0; xChilds != null && i < xChilds.length; i++) {
            xnode = xChilds[i];

            if (xnode.getNodeName().equalsIgnoreCase("row")) {
                xCells = xnode.getChildNodes();

                int nrAttr = 0;
                int nrCells = xCells.length;
                boolean hasDef = false;

                StringBuffer beforeRow = new StringBuffer();
                StringBuffer beforeRow2 = new StringBuffer();

                for (int j = 0; j < xCells.length; j++) {
                    xnameAtr = null;
                    xattribute = xCells[j].getChildNode("attribute");

                    xText = xCells[j].getChildNode("text");
                    xTag  = xCells[j].getChildNode("tag");

                    if (xattribute != null) {
                        xnameAtr = xattribute.getText();
                    } else if (xText != null) {
                        xnameAtr = xText.getAttribute("relatedAttribute");
                    }
                    else if ( xTag != null )
                    {
                        xnameAtr = xTag.getAttribute("relatedAttribute");
                    }

                    if (xnameAtr != null) {
                        hasDef = true;
                        atr = p_bodef.getAttributeRef(xnameAtr);

                        boolean attrOk = false;

                        if (atr != null) {
                            if (atr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                boDefHandler xref = atr.getReferencedObjectDef();

                                if (xref != null) {
                                    attrOk = true;
                                }
                            } else {
                                attrOk = true;
                            }
                        }

                        if (attrOk) {
                            //  xnrAttr++;
                            if (renderAtr.get(xnameAtr) == null) {
                                beforeRow.append("boolean canAccess");
                                beforeRow.append(xnameAtr);
                                beforeRow.append("=");
                                beforeRow.append("BOI.getAttribute(\"");
                                beforeRow.append(xnameAtr);
                                beforeRow.append("\").canAccess();\n");
                                renderAtr.put(xnameAtr, new Boolean(true));
                            }

                            if (beforeRow2.length() == 0) {
                                beforeRow2.append("if (");
                            } else {
                                beforeRow2.append(" || ");
                            }

                            beforeRow2.append("canAccess");
                            beforeRow2.append(xnameAtr);
                        }
                    }
                }

                if (beforeRow.length() > 0) {
                    xCode.append(beforeRow);
                    xCode.append("\n");
                }

                if (beforeRow2.length() > 0) {
                    xCode.append(beforeRow2);
                    xCode.append(" ) { \n");
                }

                xCode.append("row=sec.addRow();\n");

                for (int j = 0; j < xCells.length; j++) {
                    xtype = xnode.getAttribute("type");
                    xattribute = xCells[j].getChildNode("attribute");

                    xnameAtr = null;
                    xattribute = xCells[j].getChildNode("attribute");
                    xText = xCells[j].getChildNode("text");
                    xTag  = xCells[j].getChildNode("tag");


                    if (xattribute != null) {
                        xnameAtr = xattribute.getText();
                    } else if (xText != null) {
                        xnameAtr = xText.getAttribute("relatedAttribute");
                    }
                    else if ( xTag != null )
                    {
                        xnameAtr = xTag.getAttribute("relatedAttribute");
                    }

                    if (xnameAtr != null) {
                        atr = p_bodef.getAttributeRef(xnameAtr);

                        if (atr != null) {
                            continua = true;

                            if (atr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                boDefHandler xref = atr.getReferencedObjectDef();

                                if (xref == null) {
                                    continua = false;
                                }
                            }

                            if (continua) {
                                // xCode.append("if(BOI.getAttribute(\""+xnameAtr+"\").hasRights()){\n");
                                xCode.append("if( canAccess" + xnameAtr +
                                    ") {\n");

                                if (xattribute != null) {
                                    xAttrList = xattribute.getAttributes();
                                }
                                else if ( xText != null )
                                {
                                    xAttrList = xText.getAttributes();
                                }
                                else if ( xTag != null )
                                {
                                    xAttrList = xTag.getAttributes();
                                }
                                else
                                {
                                    xAttrList = null;
                                }

                                xCode.append("xattributes=new Hashtable();\n");

                                for (int z = 0; z < xAttrList.length; z++) {
                                    xCode.append("xattributes.put(\"" +
                                        xAttrList[z].getName() + "\",\"" +
                                        xAttrList[z].getValue() + "\");");
                                }

                                if (xattribute != null) {
                                    if (atr.getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) {
                                        xCode.append(
                                            "row.addCell(bodef.getAttributeRef(\"" +
                                            atr.getName() +
                                            "\"),xattributes);\n");
                                    } else if (atr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                        xviewmode = xattribute.getAttribute("viewmode",
                                                "");

                                        //linkto ,+showid
                                        //modes=ClassUtils.splitToArray(xviewmode,"+");
                                        xmethod = xattribute.getAttribute("method",
                                                "");
                                        xCode.append(
                                            "row.addCell(bodef.getAttributeRef(\"" +
                                            atr.getName() +
                                            "\"),xattributes,\"" + xmethod +
                                            "\",\"" + xviewmode + "\");\n");
                                    }

                                    xCode.append("} else row.addCell();\n");
                                } else if (xText != null) {
                                    xCode.append("row.addCell(\"" +
                                        xText.getText() +
                                        "\",bodef.getAttributeRef(\"" +
                                        atr.getName() + "\"),xattributes);\n");
                                    xCode.append("} else row.addCell();\n");
                                }
                                else if ( xTag != null )
                                {
                                    xCode.append( "ICustomField classInst = (ICustomField)Class.forName(\""+xTag.getAttribute("class","")+"\" ).newInstance();");
                                    xCode.append( "row.addCell(classInst,bodef.getAttributeRef(\"" +
                                        atr.getName() + "\"),xattributes);\n\r" );
                                    xCode.append("} else row.addCell();\n");
                                }

                            } else {
                                xCode.append("row.addCell();\n");
                            }
                        } else {
                            xCode.append("row.addCell();\n");
                        }
                    } else {
                        xCode.append("row.addCell();\n");
                    }
                }

                if (beforeRow2.length() > 0) {
                    xCode.append("} \n");
                }
            }
        }

        xCode.append("} //endCreateSection\n");
        xCode.append("sec.render(pageContext,currObjectList,DOCLIST,DOC);");
        if ((xConstraint != null) &&
                        xConstraint.toUpperCase().startsWith("INTERFACE:") && !p_iswizard)
        {
            xCode.append("}");
        }
        xCode.append("#END#\n");

        xInstr2.appendChild(xBody.createTextNode(xCode.toString()));

        if (xInstr != null) {
            xInstr = xBody.createElement("JSP_EXPRESSION");
            xBodyApp.appendChild(xInstr);
            xInstr.appendChild(xBody.createTextNode("#BEGIN#}#END#"));
        }
    }

    private String parseExpr(ngtXMLHandler form, boDefViewer viewer,
        String bo_NodeCat, String toParse) {
        String toReturn = toParse;
        //Vector xExpr = tools.Split(toParse, ".");
        String[] xExpr = toParse.split("\\.");
        boolean not_deployed = false;

        if (( xExpr[0]).equalsIgnoreCase("node")) {
            String catExpr = xExpr[1];

            if (catExpr.equalsIgnoreCase("label")) {
                toReturn = "#BEGIN#=DOC.getCategoryLabel(\"" +
                    viewer.getViewerName() + "\",\"" + bo_NodeCat + "\")#END#";

                if (p_bodef.hasAttribute(bo_NodeCat)) {
                    if ((p_bodef.getAttributeRef(bo_NodeCat).getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                            (p_bodef.getAttributeRef(bo_NodeCat)
                                        .getRelationType() != boDefAttribute.RELATION_1_TO_1)) {
                        toReturn = "#BEGIN#=DOC.getCategoryLabel(\"" +
                            viewer.getViewerName() + "\",\"" + bo_NodeCat +
                            "\")+\"#BEGIN_TAG#span class='area_numbers' id='" +
                            bo_NodeCat +
                            "_\"+BOI.getBoui()+\"'#END_TAG#(\"+BOI.getBridge(\"" +
                            bo_NodeCat +
                            "\").getRowCount()+\")#BEGIN_TAG#/span#END_TAG#\"#END#";

                        //+"<span id='documents_"+BOI.getBoui()+"'>("+BOI.getBridge("documents").getRowCount()+")</span>"
                    }
                }
            } else if (catExpr.equalsIgnoreCase("description")) {
                toReturn = "#BEGIN#=DOC.getCategoryDescription(#QUOT#" +
                    viewer.getViewerName() + "#QUOT#,#QUOT#" + bo_NodeCat +
                    "#QUOT#)#END#";
            } else if (catExpr.equalsIgnoreCase("tooltip")) {
                toReturn = "#BEGIN#=DOC.getCategoryToolTip(\"" +
                    viewer.getViewerName() + "\",\"" + bo_NodeCat + "\")#END#";
            }
        } else {
            StringBuffer jspName = new StringBuffer(0);

            if (toParse.indexOf(':') > -1) {
                String[] toview = toParse.substring(0, toParse.indexOf(':'))
                                         .split("\\.");

                String method = toParse.substring(toParse.indexOf(':') + 1);
                method = method.replaceAll("\"", "\\\\\"");

                boDefHandler objdef = boDefHandler.getBoDefinition(toview[0]);

                if (objdef != null) {
                    String bridgename = "dynbrige_" + toview[0];

                    if (!toview[0].equals("boObject")) {
                        if (toview.length < 3) {
                            jspName.append(objdef.getName()).append('_')
                                   .append(viewer.getViewerName())
                                   .append(toview[1]).append(".jsp");
                        } else {
                            jspName.append(objdef.getName()).append('_')
                                   .append(toview[1]).append('_')
                                   .append(viewer.getViewerName())
                                   .append(toview[2]).append(".jsp");
                        }
                    } else {
                        jspName.append("__listcardid.jsp");
                    }

                    String strEncoded = "";
                    try {
                        strEncoded = URLEncoder.encode( method, boConfig.getEncoding() );
                    } catch( Exception ex ) {
                        strEncoded = method;
                    }

                    //toReturn="#BEGIN_TAG#IFRAME id='inc_"+objdef.getName()+"__#BEGIN#=BOI.bo_boui#END#__"+bridgename+"' xsrc='#BEGIN#=\""+jspName.toString().toLowerCase()+"?docid=\"+IDX+\"#AMP#method=list#AMP#list_frommethod="+method+"#AMP#parent_attribute="+bridgename+"#AMP#parent_boui=\"+BOI.bo_boui#END#' frameBorder=0 width='100%' scrolling=no height='100%'>#BEGIN_TAG#/IFRAME#END_TAG#";
                    toReturn = "#BEGIN_TAG#IFRAME id='inc_" + viewer.getBoDefHandler().getName() +
                        "__#BEGIN#=BOI.bo_boui#END#__" + bridgename +
                        "' xsrc='#BEGIN#=\"" +
                        jspName.toString().toLowerCase() +
                        "?docid=\"+IDX+\"#AMP#method=list#AMP#list_frommethod=" +
                        strEncoded +
                        "#AMP#parent_attribute=" + bridgename +
                        "#AMP#parent_boui=\"+BOI.bo_boui#END#' frameBorder=0 width='100%' scrolling=no height='100%'";
                    toReturn += ("tabindex='#BEGIN_TAG#%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), \"inc_" +
                    objdef.getName() +
                    "__\" + String.valueOf(BOI.bo_boui) + \"__" + bridgename +
                    "\"");
                    toReturn += ", DOCLIST)%#END_TAG#'";
                    toReturn += ">#BEGIN_TAG#/IFRAME#END_TAG#";
                }
            } else {
                boDefAttribute atr = null;
                atr = p_bodef.getAttributeRef( xExpr[0]);

                if ((atr != null) && (xExpr.length > 1)) {
                    toReturn = "#BEGIN# if(BOI.getAttribute(\"" +
                        atr.getName() + "\").hasRights()){#END# ";

                    if (atr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                        boDefHandler xref = atr.getReferencedObjectDef();
                        String xFormName = xExpr[1];

                        if (xref != null) {
                            boolean OK = false;
                            String xvName = viewer.getViewerName();
                            byte relType = atr.getRelationType();

                            if ((relType == boDefAttribute.RELATION_1_TO_N_WBRIDGE) ||
                                    (relType == boDefAttribute.RELATION_1_TO_N)) {
                                boDefHandler[] xobjects = atr.getObjects();
                                boDefAttribute[] atrDefs = atr.getBridge()
                                                              .getBoAttributes();

                                if (p_bodef.hasForm(xvName,
                                            atr.getName() + "_" + xFormName)) {
                                    jspName.append(p_bodef.getName()).append('_')
                                           .append(atr.getName()).append('_')
                                           .append(xvName)
                                           .append(xExpr[1])
                                           .append(".jsp");
                                }
                            }

                            if (xref.hasViewer(xvName)) {
                                OK = true;
                            } else {
                                xvName = "General";

                                if (xref.hasViewer(xvName)) {
                                    OK = true;
                                }
                            }

                            if (OK || (jspName.length() > 0)) {
                                if ((atr.getRelationType() == boDefAttribute.RELATION_1_TO_1) &&
                                        (xFormName.equalsIgnoreCase("label") ||
                                        xFormName.equalsIgnoreCase("field") ||
                                        xFormName.equalsIgnoreCase(
                                            "description") ||
                                        xFormName.equalsIgnoreCase("tooltip") ||
                                        xFormName.equalsIgnoreCase("fieldtext"))) {
                                    /*
                                      xatts.add(atr.getName());
                                      if(xFormName.equalsIgnoreCase("label"))
                                          toReturn+="#BEGIN#=DOC.render(currObjectList,BOI."+atr.getName()+",docHTML.RENDER_LABEL)#END#";
                                      else if (xFormName.equalsIgnoreCase("description"))
                                          toReturn+="#BEGIN#=DOC.render(currObjectList,BOI."+atr.getName()+",docHTML.RENDER_DESCRIPTION)#END#";
                                      else if (xFormName.equalsIgnoreCase("tooltip"))
                                          toReturn+="#BEGIN#=DOC.render(currObjectList,BOI."+atr.getName()+",docHTML.RENDER_TOOLTIP)#END#";
                                      else if (xFormName.equalsIgnoreCase("field"))
                                          toReturn+="#BEGIN#=DOC.render(currObjectList,BOI."+atr.getName()+",docHTML.RENDER_FIELD)#END#";
                                      */
                                } else {
                                    if ((jspName.length() > 0) ||
                                            xref.hasForm(xvName, xFormName)) {
                                        if (jspName.length() == 0) {
                                            jspName.append(atr.getReferencedObjectName())
                                                   .append('_').append(xvName)
                                                   .append(xFormName).append(".jsp");
                                        }

                                        if (atr.getRelationType() == boDefAttribute.RELATION_1_TO_1) {
                                            //toReturn+="#BEGIN_TAG#IFRAME id='inc_"+p_bodef.getName()+"__#BEGIN#=BOI.bo_boui#END#"+"__"+atr.getName()+"' xsrc='#BEGIN#=\""+jspName.toString().toLowerCase()+"?docid=\"+IDX+\"#AMP#method=edit#AMP#parent_attribute="+atr.getName()+"#AMP#parent_attribute_label="+atr.getLabel().replaceAll(" ","%20")+"#AMP#parent_boui=\"+BOI.bo_boui #END#' frameBorder=0 width='100%' scrolling=no height='100%'>#BEGIN_TAG#/IFRAME#END_TAG#";
                                            toReturn += ("#BEGIN_TAG#IFRAME id='inc_" +
                                            p_bodef.getName() +
                                            "__#BEGIN#=BOI.bo_boui#END#" +
                                            "__" + atr.getName() +
                                            "' xsrc='#BEGIN#=\"" +
                                            jspName.toString().toLowerCase() +
                                            "?docid=\"+IDX+\"#AMP#method=edit#AMP#parent_attribute=" +
                                            atr.getName() +
                                            "#AMP#parent_attribute_label=" +
                                            atr.getLabel().replaceAll(" ", "%20") +
                                            "#AMP#parent_boui=\"+BOI.bo_boui #END#' frameBorder=0 width='100%' scrolling=no height='100%' ");
                                            toReturn += ("tabindex='#BEGIN_TAG#%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), \"inc_" +
                                            p_bodef.getName() +
                                            "__\" + BOI.bo_boui + \"__" +
                                            atr.getName() + "\"");
                                            toReturn += ", DOCLIST)%#END_TAG#'";
                                            toReturn += ">#BEGIN_TAG#/IFRAME#END_TAG#";
                                        } else {
                                            //toReturn+="#BEGIN_TAG#IFRAME id='inc_"+atr.getBoDefHandler().getName()+"__#BEGIN#=BOI.bo_boui#END#"+"__"+atr.getName()+"' xsrc='#BEGIN#=\""+jspName.toString().toLowerCase()+"?docid=\"+IDX+\"#AMP#method=list#AMP#parent_attribute="+xExpr.get(0)+"#AMP#parent_boui=\"+BOI.bo_boui #END#' frameBorder=0 width='100%' scrolling=no height='100%'>#BEGIN_TAG#/IFRAME#END_TAG#";
                                            //toReturn+="#BEGIN_TAG#IFRAME id='inc_"+p_bodef.getName()+"__#BEGIN#=BOI.bo_boui#END#"+"__"+atr.getName()+"' xsrc='#BEGIN#=\""+jspName.toString().toLowerCase()+"?docid=\"+IDX+\"#AMP#method=list#AMP#parent_attribute="+xExpr.get(0)+"#AMP#parent_boui=\"+BOI.bo_boui #END#' frameBorder=0 width='100%' scrolling=no height='100%'>#BEGIN_TAG#/IFRAME#END_TAG#";
                                            ngtXMLHandler order = form.getChildNode("order");
                                            String orderBy="";
                                            if(order != null)
                                            {
                                                String attName=order.getChildNode("attribute").getText();
                                                if(attName != null && !"".equals(attName))
                                                {
                                                    orderBy="#AMP#list_orderby="+attName;
                                                }
                                            }
                                            toReturn += ("#BEGIN_TAG#IFRAME id='inc_" +
                                            p_bodef.getName() +
                                            "__#BEGIN#=BOI.bo_boui#END#" +
                                            "__" + atr.getName() +
                                            "' xsrc='#BEGIN#=\"" +
                                            jspName.toString().toLowerCase() +
                                            "?docid=\"+IDX+\"#AMP#method=list"+orderBy+"#AMP#parent_attribute=" +
                                            xExpr[0] +
                                            "#AMP#parent_boui=\"+BOI.bo_boui #END#' frameBorder=0 width='100%' scrolling=no height='100%' ");
                                            toReturn += ("tabindex='#BEGIN_TAG#%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), \"inc_" +
                                            p_bodef.getName() +
                                            "__\" + BOI.bo_boui + \"__" +
                                            atr.getName() + "\"");
                                            toReturn += ", DOCLIST)%#END_TAG#'";
                                            toReturn += ">#BEGIN_TAG#/IFRAME#END_TAG#";
                                        }

                                        xatts.add(atr.getName());
                                    } else {

                                        toReturn += ("#BEGIN# /* object " +
                                        atr.getName() + ":" +
                                        atr.getReferencedObjectName() +
                                        " not HAVE VIEWER.FORM « " + xvName +
                                        "." + xFormName + " »*/ #END# ");
                                        logger.warn("Requested FORM  " +
                                            atr.getName() + ":" +
                                            atr.getReferencedObjectName() +
                                            "  « " + xvName + "." + xFormName +
                                            " » in " + this.p_jspName +
                                            " NOT FOUND ");
                                    }
                                }
                            } else {
                                if("boObject".equals(atr.getReferencedObjectName() ))
                                {

                                    if (boDefAttribute.RELATION_1_TO_1 == atr.getRelationType())
                                    {
                                        StringBuffer sb = new StringBuffer();
                                        sb.append("\n#BEGIN#");
                                        sb.append("boolean render =  false;\n");
                                        sb.append("String jspName =  null;\n");

                                        sb.append("boObject obj = BOI.getAttribute(\"").append(atr.getName()).append("\").getObject();\n");
                                        sb.append("if(obj != null)\n");
                                        sb.append("{\n");
                                        sb.append("\t render = true;\n");
                                        sb.append("\t jspName =   obj.getBoDefinition().getName().toLowerCase();\n");
                                        sb.append("\t jspName += \"").append("_").append(xvName.toLowerCase()).append(xFormName).append(".jsp\";\n");
                                        sb.append("}\n");

                                        sb.append("if(render)");
                                        sb.append("{");
                                        sb.append("#END#\n");
                                        toReturn += sb.toString();


                                        toReturn += ("#BEGIN_TAG#IFRAME id='inc_" +
                                        p_bodef.getName() +
                                        "__#BEGIN#=BOI.bo_boui#END#" +
                                        "__" + atr.getName() +
                                        "' xsrc='#BEGIN#=" +
                                        "jspName"+
                                        "+\"?docid=\"+IDX+\"#AMP#method=edit#AMP#parent_attribute=" +
                                        atr.getName() +
                                        "#AMP#parent_attribute_label=" +
                                        atr.getLabel().replaceAll(" ", "%20") +
                                        "#AMP#parent_boui=\"+BOI.bo_boui #END#' frameBorder=0 width='100%' scrolling=no height='100%' ");
                                        toReturn += ("tabindex='#BEGIN_TAG#%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), \"inc_" +
                                        p_bodef.getName() +
                                        "__\" + BOI.bo_boui + \"__" +
                                        atr.getName() + "\"");
                                        toReturn += ", DOCLIST)%#END_TAG#'";
                                        toReturn += ">#BEGIN_TAG#/IFRAME#END_TAG#";


                                        toReturn +="\n#BEGIN#";
                                        toReturn +="\n}";
                                        toReturn +="\n#END#";

                                    }
                                    else
                                    {
                                        ngtXMLHandler order = form.getChildNode("order");
                                        String orderBy="";
                                        if(order != null)
                                        {
                                            String attName=order.getChildNode("attribute").getText();
                                            if(attName != null && !"".equals(attName))
                                            {
                                                orderBy="#AMP#list_orderby="+attName;
                                            }
                                        }
                                        toReturn += ("#BEGIN_TAG#IFRAME id='inc_" +
                                        p_bodef.getName() +
                                        "__#BEGIN#=BOI.bo_boui#END#" +
                                        "__" + atr.getName() +
                                        "' xsrc='#BEGIN#=" +
                                        "\"__listcardid.jsp?docid=\"+IDX+\"#AMP#method=list"+orderBy+"#AMP#parent_attribute=" +
                                        xExpr[0] +
                                        "#AMP#parent_boui=\"+BOI.bo_boui #END#' frameBorder=0 width='100%' scrolling=no height='100%' ");
                                        toReturn += ("tabindex='#BEGIN_TAG#%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), \"inc_" +
                                        p_bodef.getName() +
                                        "__\" + BOI.bo_boui + \"__" +
                                        atr.getName() + "\"");
                                        toReturn += ", DOCLIST)%#END_TAG#'";
                                        toReturn += ">#BEGIN_TAG#/IFRAME#END_TAG#";

                                    }
                                }
                                else
                                {
                                    toReturn += ("#BEGIN# /* object " +
                                    atr.getName() + ":" +
                                    atr.getReferencedObjectName() +
                                    " not HAVE VIEWER « " + xvName + " »*/ #END# ");
                                    logger.warn("Requested VIEWER  " +
                                        atr.getName() + ":" +
                                        atr.getReferencedObjectName() + "  « " +
                                        xvName + " » in " + this.p_jspName +
                                        " NOT FOUND ");
                                }
                            }
                        } else {
                            toReturn += ("#BEGIN# /* object " + atr.getName() +
                            ":" + atr.getReferencedObjectName() +
                            " not DEPLOYED */ #END# ");
                            logger.warn("Requested object " + atr.getName() +
                                ":" + atr.getReferencedObjectName() + " in " +
                                this.p_jspName + " not DEPLOYED ");
                            not_deployed = true;
                        }
                    } else {
                        String xExpression = xExpr[1];
                        xatts.add(atr.getName());

                        if (xExpression.equalsIgnoreCase("label")) {
                            toReturn += ("#BEGIN#=DOC.render(currObjectList,BOI.getAttribute(\"" +
                            atr.getName() + "\"),docHTML.RENDER_LABEL)#END#");
                        } else if (xExpression.equalsIgnoreCase("description")) {
                            toReturn += ("#BEGIN#=DOC.render(currObjectList,BOI.getAttribute(\"" +
                            atr.getName() +
                            "\"),docHTML.RENDER_DESCRIPTION)#END#");
                        } else if (xExpression.equalsIgnoreCase("tooltip")) {
                            toReturn += ("#BEGIN#=DOC.render(currObjectList,BOI.getAttribute(\"" +
                            atr.getName() + "\"),docHTML.RENDER_TOOLTIP)#END#");
                        } else if (xExpression.equalsIgnoreCase("field")) {
                            toReturn += ("#BEGIN#=DOC.render(currObjectList,BOI.getAttribute(\"" +
                            atr.getName() + "\"),docHTML.RENDER_FIELD)#END#");
                        } else if (xExpression.equalsIgnoreCase("fieldtext")) {
                            toReturn += ("#BEGIN#=DOC.render(currObjectList,BOI.getAttribute(\"" +
                            atr.getName() +
                            "\"),docHTML.RENDER_FIELDTEXT)#END#");
                        }
                    }

                    toReturn += "#BEGIN# } #END# ";

                    if (not_deployed) {
                        toReturn = "";
                    }
                } else {
                    if (toParse.equalsIgnoreCase("#BOUI")) {
                        toReturn += "#BEGIN#=BOI.bo_boui#END#";
                    } else if (toParse.equalsIgnoreCase("SELECTBOUI")) {
                        toReturn = "#BEGIN_TAG#input type=\"checkbox\" #END_TAG#";
                    } else if (toParse.equalsIgnoreCase("HEADERSELECTBOUI")) {
                        toReturn = "#BEGIN_TAG#input type=\"checkbox\" #END_TAG#";
                    }
                }
            }
        }

        return toReturn;
    }

    private static final boolean setAttributeTabIndex(String nodeName) {
        for (int i = 0; i < setAttributeTabIndex.length; i++) {
            if (setAttributeTabIndex[i].equalsIgnoreCase(nodeName)) {
                return true;
            }
        }

        return false;
    }

}
