/*Enconding=UTF-8*/
package netgest.bo.presentation.render.html32.components;

import java.io.CharArrayWriter;
import netgest.bo.boConfig;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefClsState;
import netgest.bo.def.boDefHandler;

//import org.w3c.tidy.*;
import netgest.bo.dochtml.ICustomField;
import netgest.bo.dochtml.docHTML;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.message.utils.MessageUtils;

import netgest.bo.presentation.render.Browser;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.ObjectCardReport;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boBridgesArray;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectUtils;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.bo.utils.DifferenceContainer;
import netgest.bo.utils.DifferenceElement;
import netgest.bo.utils.DifferenceHelper;

import netgest.bo.workflow.DocWfHTML;

import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XSLException;

import netgest.bo.system.Logger;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.SQLException;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class ObjectCardReportBuilder {
    private static Logger logger = Logger.getLogger(
            "netgest.bo.presentation.render.ie.components.ObjectCardReportBuilder");
    private static final char[] REPORT_EMPTY = "<html><head><title>Relatório de Impressão</title><style type=\"text/css\">@import url(ieThemes/0/report/report.css);</style><script>function init(){}</script></head><body></body></html>".toCharArray();
    private static final String CARD_NAO = "Não";
    private static final String CARD_SIM = "Sim";
    private static final String CARD_Y = "Y";
    private static final String COMMENT_PRINT_START = "<!--commentPrintStart-->";
    private static final String COMMENT_PRINT_END = "<!--commentPrintEnd-->";
    private static final String COMMENT_TITLE_START = "<!--commentTitleReportStart-->";
    private static final String COMMENT_TITLE_END = "<!--commentTitleReportEnd-->";
    private static final String COMMENT_REPORT_START = "<!--commentIfStartReportStart-->";
    private static final String COMMENT_REPORT_END = "<!--commentIfStartReportEnd-->";
    private static final String COMMENT_BODY_START = "<!--commentIfReportBodyStart-->";
    private static final String COMMENT_BODY_END = "<!--commentIfReportBodyEnd-->";
    private static final String COMMENT_REPORT_BODY_START = "<!--commentIfStartReportAndReportBodyStart-->";
    private static final String COMMENT_REPORT_BODY_END = "<!--commentIfStartReportReportBodyEnd-->";
    private static final String EMPTY_INIT = "function init(){}";
    private static final String INIT = "function init(){parent.document.getElementById(\"";
    private static final String INIT_1 = "\").style.height=container.offsetHeight+20;\n;if(parent.document.body.id!='preview'){document.body.scroll='no';} }\n";
    private static final String COMMENT_BEGIN = "<!--";
    private static final String COMMENT_END = "-->";
    private static final String NOT_SAVE = "NOTA : Alguns destes dados ainda não estão gravados";
    private static final String LOW_IMP = "Esta mensagem foi enviada com o grau de importância baixa.";
    private static final String HIGH_IMP = "Esta mensagem foi enviada com o grau de importância alta.";
    private static final String CORRECT_SIGNED = "O email foi assinado e a assinatura foi verificada.";
    private static final String INCORRECT_SIGNED = "O email foi assinado no entanto a assinatura falhou.";
    private static final String OBJ_LABEL_START = "var objLabel='";
    private static final String OBJ_LABEL_END = "';";

    /**
     *
     * @Company Enlace3
     * @since
     */
    private ObjectCardReportBuilder() {
    }

    public static final void writeCardReport(PrintWriter out,
        ObjectCardReport oc, docHTML doc, PageController control)
        throws IOException {
        try {
            if ((oc.getBoui() <= 0) && (oc.getObject() == null)) {
                out.write(REPORT_EMPTY);
            } else if ((oc.getBoui() > 0) &&
                    ((oc.getObject() == null) ||
                    (oc.getObject().getBoui() != oc.getBoui()))) {
                boObject o = boObject.getBoManager().loadObject(doc.getEboContext(),
                        oc.getBoui());
                writeHTML(out, o, oc.getViewerName(), oc.getFormName(), doc,
                    control, oc.getParentFrameId(), oc.designHeader(),
                    oc.designPrint());
            } else {
                writeHTML(out, oc.getObject(), oc.getViewerName(),
                    oc.getFormName(), doc, control, oc.getParentFrameId(),
                    oc.designHeader(), oc.designPrint());
            }
        } catch (boRuntimeException e) {
            out.write(REPORT_EMPTY);
        }
    }

    public static final void writeHTML(PrintWriter out, boObject o,
        String viewerName, String formName, docHTML doc, PageController control)
        throws IOException {
        writeHTML(out, o, viewerName, formName, doc, control, null, true, true);
    }

    private static boolean isHtmlCode(String str, int startPos) {
        for (int i = (startPos + 1); i < str.length(); i++) {
            if ((str.charAt(i) == ';') && (i > (startPos + 1))) {
                return true;
            } else if (((str.charAt(i) == '#') && (i != (startPos + 1))) ||
                    ((str.charAt(i) != '#') &&
                    !(((str.charAt(i) >= 'a') && (str.charAt(i) <= 'z')) ||
                    ((str.charAt(i) >= '0') && (str.charAt(i) <= '9'))))) {
                return false;
            }
        }

        return false;
    }

    private static String changeAmp(String s) {
        int from = 0;
        String toRet = s;
        String aux;
        boolean foundAmp = (from = s.indexOf("&")) >= 0;

        while (foundAmp) {
            if (!isHtmlCode(toRet, from)) {
                if (from == 0) {
                    toRet = "&amp;" +
                        ((toRet.length() > 1) ? toRet.substring(1) : "");
                } else {
                    aux = toRet;
                    toRet = toRet.substring(0, from);
                    toRet = toRet + "&amp;";
                    toRet = toRet +
                        ((aux.length() > (from + 1)) ? aux.substring(from + 1)
                                                     : "");
                }
            }

            from = from + 1;
            foundAmp = (from = toRet.indexOf("&", from)) > 0;
        }

        return toRet;
    }

    public static final void writeHTML(PrintWriter out, boObject o,
        String viewerName, String formName, docHTML doc,
        PageController control, String parentFrameId, boolean designHeader,
        boolean designPrint) throws IOException {
        try {
            boConfig cnf = new boConfig();
            String s = Browser.getThemeDir() + "report/report.css";
            String aux;

            String template = cnf.getDeployJspDir() +
                "templateReports//reportHTML.xml";
            XMLDocument xmlTemplate = ngtXMLUtils.loadXMLFile(template);
            String xml = ngtXMLUtils.getXML(xmlTemplate);

            xml = xml.replaceAll("#CSS_URL#", s);

            if (!designPrint) {
                xml = xml.replaceAll(COMMENT_PRINT_START, COMMENT_BEGIN);
                xml = xml.replaceAll(COMMENT_PRINT_END, COMMENT_END);
            }

            String mydate = DateFormat.getDateTimeInstance(DateFormat.FULL,
                    DateFormat.FULL).format(new java.util.Date());
            xml = xml.replaceAll("#DATE#", mydate);

            boObject performer = null;
            if(o.getEboContext() != null && o.getEboContext().getBoSession().getPerformerBoui() > 0)
            {
                performer = boObject.getBoManager().loadObject(o.getEboContext(),
                        o.getEboContext().getBoSession().getPerformerBoui());
            }
            xml = xml.replaceAll("#USERNAME#",
                    performer == null ? "":performer.getAttribute("name").getValueString());

            if (designHeader) {
                if (o.getMode() == o.MODE_EDIT_TEMPLATE) {
                    boObject tmpl = o.getAttribute("TEMPLATE").getObject();

                    xml = xml.replaceAll("#REPORTNAME#",
                            "Ficha do Modelo  " +
                            tmpl.getAttribute("name").getValueString());
                } else {
                    xml = xml.replaceAll("#REPORTNAME#",
                            "Ficha de " + o.getBoDefinition().getLabel());
                }

                xml = xml.replaceAll("#URL#",
                        o.getEboContext().getApplicationUrl());
                xml = xml.replaceAll("#reportBody#", "reportBody");
            } else {
                xml = xml.replaceAll(COMMENT_TITLE_START, COMMENT_BEGIN);
                xml = xml.replaceAll(COMMENT_TITLE_END, COMMENT_END);
                xml = xml.replaceAll("#reportBody#", "reportBodyNHeader");
            }

            aux = o.getCARDID(false).toString();
            aux = aux.replaceAll("\\$", "\\\\\\$");
            aux = aux.replaceAll("\\?", "\\\\\\?");
            aux = aux.replaceAll("\\^", "\\\\\\^");
            aux = aux.replaceAll("-", "&#45;");
            xml = xml.replaceAll("#CARDID#", aux);
            String varObjLabel = OBJ_LABEL_START + aux + OBJ_LABEL_END;

            if ((parentFrameId != null) && !"".equals(parentFrameId)) {
                String init = varObjLabel + INIT + parentFrameId + INIT_1;
                xml = xml.replaceAll("#init#", init);
                xml = xml.replaceAll(COMMENT_BODY_START, COMMENT_BEGIN);
                xml = xml.replaceAll(COMMENT_BODY_END, COMMENT_END);
                xml = xml.replaceAll(COMMENT_REPORT_BODY_START, COMMENT_BEGIN);
                xml = xml.replaceAll(COMMENT_REPORT_BODY_END, COMMENT_END);
            } else if (!designHeader) {
                xml = xml.replaceAll("#init#", varObjLabel + EMPTY_INIT);
                xml = xml.replaceAll(COMMENT_BODY_START, COMMENT_BEGIN);
                xml = xml.replaceAll(COMMENT_BODY_END, COMMENT_END);
                xml = xml.replaceAll(COMMENT_REPORT_START, COMMENT_BEGIN);
                xml = xml.replaceAll(COMMENT_REPORT_END, COMMENT_END);
            } else {
                xml = xml.replaceAll("#init#", varObjLabel + EMPTY_INIT);
                xml = xml.replaceAll(COMMENT_REPORT_START, COMMENT_BEGIN);
                xml = xml.replaceAll(COMMENT_REPORT_END, COMMENT_END);
                xml = xml.replaceAll(COMMENT_REPORT_BODY_START, COMMENT_BEGIN);
                xml = xml.replaceAll(COMMENT_REPORT_BODY_END, COMMENT_END);
            }
            xml = changeAmp(xml);

            xml = xml.replace( (char)0, ' ' );
            ngtXMLHandler xmlToPrint = new ngtXMLHandler(xml);

            //Node x= xmlToPrint.getDocument().selectSingleNode("//p[@id='reportName']");
            // Node reportName = xmlTemplate.createTextNode(" TESTE ");
            //x.appendChild( reportName );
            Node body = null;

            if ((parentFrameId != null) && !"".equals(parentFrameId)) {
                body = xmlToPrint.getDocument().selectSingleNode("//div[@class='reportInnerBody']");
            } else if (!designHeader) {
                body = xmlToPrint.getDocument().selectSingleNode("//div[@class='reportBodyNHeader']");
            } else {
                body = xmlToPrint.getDocument().selectSingleNode("//div[@class='reportBody']");
            }

            boolean foundViewer = false;

            if ((viewerName == null) && (formName == null)) {
                if (o.getBoDefinition().hasForm("General", "preview")) {
                    viewerName = "General";
                    formName = "preview";
                    foundViewer = true;
                } else if (o.getBoDefinition().hasForm("General", "edit")) {
                    viewerName = "General";
                    formName = "edit";
                    foundViewer = true;
                }
            }

            if (foundViewer) {
                //uild( docHTML doc, boObject o, XMLDocument dom  ,XMLNode node ,  ngtXMLHandler xmlForm )
                if ("message".equals(o.getName()) ||
                        o.getName().startsWith("message") ||
                        "message".equals(o.getBoDefinition().getBoSuperBo())) {
                    if (o.isChanged()) {
                        Element e = xmlToPrint.getDocument().createElement("div");
                        e.setAttribute("class", "note");
                        e.appendChild(xmlToPrint.getDocument().createTextNode(NOT_SAVE));
                        body.appendChild(e);
                    }

                    if ("0".equals(o.getAttribute("priority").getValueString())) {
                        Element e = xmlToPrint.getDocument().createElement("div");
                        e.setAttribute("class", "low");
                        e.appendChild(xmlToPrint.getDocument().createTextNode(LOW_IMP));
                        body.appendChild(e);
                    }

                    if ("2".equals(o.getAttribute("priority").getValueString())) {
                        Element e = xmlToPrint.getDocument().createElement("div");
                        e.setAttribute("class", "high");
                        e.appendChild(xmlToPrint.getDocument().createTextNode(HIGH_IMP));
                        body.appendChild(e);
                    }

                    if ("0".equals(o.getAttribute("signedMsg").getValueString())) {
                        Element e = xmlToPrint.getDocument().createElement("div");
                        e.setAttribute("class", "signed");
                        e.appendChild(xmlToPrint.getDocument().createTextNode(INCORRECT_SIGNED));
                        body.appendChild(e);
                    }

                    if ("1".equals(o.getAttribute("signedMsg").getValueString())) {
                        Element e = xmlToPrint.getDocument().createElement("div");
                        e.setAttribute("class", "signed");
                        e.appendChild(xmlToPrint.getDocument().createTextNode(CORRECT_SIGNED));
                        body.appendChild(e);
                    }

                    if ("1".equals(o.getAttribute("send_read_receipt")
                                        .getValueString())) {
                        if( o.getEboContext().getBoSession().getPerformerBoui() > 0)
                        {
                            Element e = xmlToPrint.getDocument().createElement("div");
                            e.setAttribute("class", "receipt");

                            boObject oPerf = boObject.getBoManager().loadObject(o.getEboContext(),
                                    o.getEboContext().getBoSession()
                                     .getPerformerBoui());
                            e = MessageUtils.getReadReceiptsElement(xmlToPrint, e,
                                    o, oPerf);

                            //                            e.appendChild( xmlToPrint.getDocument().createTextNode(aux) );
                            body.appendChild(e);
                        }
                    }

                    if(MessageUtils.isBlockMsg(o))
                    {
                        Element e = xmlToPrint.getDocument().createElement("div");
                        e.setAttribute("class", "receipt");
                        e =  MessageUtils.getBlockMsgElement(xmlToPrint,e, o);
                        body.appendChild(e);
                    }

                    if(o.getAttribute("markedSendUser") != null &&
                       o.getAttribute("markedSendUser").getObject() != null)
                    {
                        boObject perfMark = o.getAttribute("markedSendUser").getObject();
                        Element e = xmlToPrint.getDocument().createElement("div");
                        e.setAttribute("class", "greenNote");
                        e.appendChild(xmlToPrint.getDocument().createTextNode("Esta mensagem não foi enviada pelo sistema, mas foi marcada como enviada por "));
                        e = perfMark.getCARDID(xmlToPrint, e, false);
                        body.appendChild(e);
                    }


                    if((o.getAttribute("error") != null &&
                        "1".equals(o.getAttribute("error").getValueString()) ||
                        (o.getAttribute("errorMsg") != null && o.getAttribute("errorMsg").getValueString() != null &&
                           o.getAttribute("errorMsg").getValueString().length() > 0)
                        )
                    )
                    {
                        Element e = xmlToPrint.getDocument().createElement("div");
                        e.setAttribute("class", "note");
                        if(o.getAttribute("errorMsg") != null &&
                           o.getAttribute("errorMsg").getValueString() != null &&
                           o.getAttribute("errorMsg").getValueString().length() > 0)
                        {
                            e.appendChild(xmlToPrint.getDocument().createTextNode("Erro no envio: " + o.getAttribute("errorMsg").getValueString()));
                        }
                        else
                        {
                            e.appendChild(xmlToPrint.getDocument().createTextNode("Erro no envio: Erro desconhecido. Tente o envio de uma nova mensagem."));
                        }

                        body.appendChild(e);
                    }
                    if(o.getAttribute("cancelada") != null &&
                        "1".equals(o.getAttribute("cancelada").getValueString())
                    )
                    {
                        Element e = xmlToPrint.getDocument().createElement("div");
                        e.setAttribute("class", "note");
                        String motivo =null;
                        Date dtCancel = o.getAttribute("cancelDate") == null ? null:o.getAttribute("cancelDate").getValueDate();
                        String lovV = o.getAttribute("motivoCancel").getValueString();
                        String lovname = o.getAttribute("motivoCancel").getDefAttribute().getLOVName();
                        if ((lovname != null) && !lovname.equalsIgnoreCase("")) {
                            motivo = boObjectUtils.getLovDescription(o.getEboContext(),
                                    lovname, lovV);
                        }
                        boObject perfCancel = o.getAttribute("cancelUtilizador") == null ? null:o.getAttribute("cancelUtilizador").getObject();
                        e.appendChild( xmlToPrint.getDocument().createTextNode("Esta mensagem foi cancelada") );
                        if(perfCancel != null)
                        {
                            e.appendChild( xmlToPrint.getDocument().createTextNode(" por ") );
                            e = perfCancel.getCARDID(xmlToPrint, e, false);
                        }
                        if(dtCancel != null)
                        {
                            SimpleDateFormat formatter = new SimpleDateFormat(
                            "dd-MM-yyyy HH:mm:ss");
                            e.appendChild( xmlToPrint.getDocument().createTextNode(" em  " +  formatter.format(dtCancel)) );
                        }
                        if(motivo != null)
                        {
                            e.appendChild( xmlToPrint.getDocument().createTextNode(" pelo motivo  " +  motivo));
                        }
                        body.appendChild(e);
                    }
                } else if (o.isChanged()) {
                    Element e = xmlToPrint.getDocument().createElement("div");
                    e.setAttribute("class", "note");
                    e.appendChild(xmlToPrint.getDocument().createTextNode("NOTA : Alguns destes dados ainda não estão gravados"));
                    body.appendChild(e);
                }

                buildCard(doc, o, null, null, viewerName, formName,
                    xmlToPrint.getDocument(), body,
                    o.getBoDefinition().getViewer(viewerName).getForm(formName),
                    "");

                if (o.getMode() == o.MODE_EDIT_TEMPLATE) {
                    Element e = xmlToPrint.getDocument().createElement("div");
                    e.setAttribute("class", "templateTitle");
                    e.appendChild(xmlToPrint.getDocument().createTextNode("Resumo do Modelo"));
                    body.appendChild(e);

                    boObject tmpl = o.getAttribute("TEMPLATE").getObject();
                    formName = "edit";
                    buildCard(doc, tmpl, null, null, viewerName, formName,
                        xmlToPrint.getDocument(), body,
                        tmpl.getBoDefinition().getViewer(viewerName).getForm(formName),
                        "");
                }

                if (o.isChanged()) {
                    Element e = xmlToPrint.getDocument().createElement("div");
                    e.setAttribute("class", "note");
                    e.appendChild(xmlToPrint.getDocument().createTextNode("NOTA : Alguns destes dados ainda não estão gravados"));
                    body.appendChild(e);
                }
            } else {
                Node reportName = xmlToPrint.getDocument().createTextNode(" Definições não encontradas?!!");
                body.appendChild(reportName);
            }

            xml = ngtXMLUtils.getXML(xmlToPrint.getDocument());

            //]]>
            xml = xml.replaceAll("<!\\[CDATA\\[", "");
            xml = xml.replaceAll("\\]\\]>", "");
            xml = xml.replaceAll("NBSP", "&nbsp;");

            xml = xml.replaceAll("#STATE#", o.getICONComposedState());
            xml = xml.replaceAll("#STATUS#",
                    o.getSTATUS().toString() + "  [" + o.getBoui() + "]");

            //  CharArrayReader cr = new CharArrayReader( xml.toCharArray() );
            out.write(xml.toCharArray());

            //InputSource ip = new InputSource( cr );
            //  ReportParser parser = ReportParser.getInstance();
            //  PDF  pdf = parser.parse( ip );
            //  pdf.render(out);
        }
        //            catch (IOException e)
        //            {
        //                e.printStackTrace();
        //            }
        //  catch (SAXException e)
        //  {
        //      e.printStackTrace();
        //  }
        catch (boRuntimeException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (XSLException e) {
            e.printStackTrace();
        }
    }

    private static void buildCard(docHTML doc, boObject bo,
        boObjectList bolist, String order, String viewerName, String formName,
        XMLDocument domToBuild, Node nodeToBuild, ngtXMLHandler xmlForm,
        String fromLabel) throws SQLException, boRuntimeException {
        ngtXMLHandler[] childs = xmlForm.getChildNodes();
        Node xx = xmlForm.getNode();
        int nodeType = xx.getNodeType();
        String lastlabel = "";

        for (int i = 0; i < childs.length; i++) {
            String nodeName = childs[i].getNodeName();

            if (nodeName.equalsIgnoreCase("Areas") ||
                    nodeName.equalsIgnoreCase("Panel")) {
                ngtXMLHandler[] blockChilds = childs[i].getChildNodes();

                for (int j = 0; j < blockChilds.length; j++) {
                    ngtXMLHandler formBlock = blockChilds[j];
                    String blockName = formBlock.getNodeName();
                    String category = formBlock.getAttribute("bo_node");
                    boolean hasRights = true;
                    boolean toContinue = true;

                    //                    if ( category != null )
                    //                    {
                    //                        hasRights = doc.hasCategoryRights( category , bo );
                    //                    }
                    toContinue = hasRights;

                    String formConstraint = formBlock.getAttribute("constraint");

                    if (formConstraint != null) {
                        if (formConstraint.equalsIgnoreCase(
                                    "when_object_is_extend")) {
                            if (bo != null) {
                                toContinue = toContinue &&
                                    !bo.getBridge("extendAttribute").isEmpty();
                            }
                        } else if (formConstraint.equalsIgnoreCase(
                                    "when_object_is_notextend")) {
                            if (bo != null) {
                                toContinue = toContinue &&
                                    bo.getBridge("extendAttribute").isEmpty();
                            }
                        } else if (formConstraint.equalsIgnoreCase(
                                    "when_extend_attribute_in_template_mode")) {
                            if (bo != null) {
                                toContinue = toContinue &&
                                    (bo.getMode() == boObject.MODE_EDIT_TEMPLATE);
                            }
                        } else if (formConstraint.equalsIgnoreCase(
                                    "when_object_in_template_mode")) {
                            toContinue = toContinue &&
                                (bo.getMode() == boObject.MODE_EDIT_TEMPLATE);
                        }
                    }

                    if (toContinue) //&& formBlock.getAttribute("forTemplate")==null )
                     {
                        //when_object_is_extend
                        //                        when_object_is_notextend
                        //                        when_object_in_template_mode
                        //                        BOI.getBridge("extendAttribute").isEmpty()
                        String label = formBlock.getAttribute("label", "");

                        if (blockName.equals("tab") && (label.length() == 0)) {
                            label = doc.getCategoryLabel_for_TAB_Header(category,
                                    bo);
                        } else if ((label.length() > 0) && (category != null)) {
                            label = doc.getCategoryLabel(bo, viewerName,
                                    category);
                        }

                        String xlabel = label;

                        if (label.equals(fromLabel)) {
                            xlabel = "";
                        }

                        Node xnode = buildBlock(blockName, xlabel, doc, bo,
                                domToBuild, nodeToBuild, formBlock);
                        buildCard(doc, bo, bolist, order, viewerName, formName,
                            domToBuild, xnode, formBlock, label);
                    }
                }
            } else if (nodeName.equalsIgnoreCase("section")) {
                ngtXMLHandler formBlock = childs[i];
                String blockName = formBlock.getNodeName();
                String category = formBlock.getAttribute("bo_node");
                boolean hasRights = true;
                boolean toContinue = true;

                //                    if ( category != null )
                //                    {
                //                        hasRights = doc.hasCategoryRights( category , bo );
                //                    }
                toContinue = hasRights;

                String formConstraint = formBlock.getAttribute("constraint");

                if (formConstraint != null) {
                    if (formConstraint.equalsIgnoreCase("when_object_is_extend")) {
                        if (bo != null) {
                            toContinue = toContinue &&
                                !bo.getBridge("extendAttribute").isEmpty();
                        }
                    } else if (formConstraint.equalsIgnoreCase(
                                "when_object_is_notextend")) {
                        if (bo != null) {
                            toContinue = toContinue &&
                                bo.getBridge("extendAttribute").isEmpty();
                        }
                    } else if (formConstraint.equalsIgnoreCase(
                                "when_extend_attribute_in_template_mode")) {
                        if (bo != null) {
                            toContinue = toContinue &&
                                (bo.getMode() == boObject.MODE_EDIT_TEMPLATE);
                        }
                    } else if (formConstraint.equalsIgnoreCase(
                                "when_object_in_template_mode")) {
                        toContinue = toContinue &&
                            (bo.getMode() == boObject.MODE_EDIT_TEMPLATE);
                    }
                }

                if (toContinue) //&& formBlock.getAttribute("forTemplate")==null )
                 {
                    String label = formBlock.getAttribute("label", "");

                    if ((label.length() > 0) && (category != null)) {
                        label = doc.getCategoryLabel(bo, viewerName, category);
                    }

                    buildSection(label, doc, bo, domToBuild, nodeToBuild,
                        formBlock);
                }
            } else if (nodeName.equalsIgnoreCase("include-frame")) {
                ngtXMLHandler formBlock = childs[i];
                String jspName = formBlock.getText();

                if (jspName.equals("__extendAttribute.jsp")) {
                    if (!bo.getBridge("extendAttribute").isEmpty() &&
                            (bo.getMode() != boObject.MODE_EDIT_TEMPLATE)) {
                        renderExtendAttributesForPrint("", doc, bo, domToBuild,
                            nodeToBuild, formBlock);
                    }
                } else {
                    renderIFRAME("frame" + System.currentTimeMillis(), doc, bo,
                        domToBuild, nodeToBuild, formBlock);

                    //                        String iframe = "<div class=extendList><IFRAME id='inc_" + extAttr.getName() + "__" +extAttr.getBoui() + "__valueList' src='__extendAttributeList.jsp?docid="+IDX+"&method=list&parent_attribute=valueList&parent_boui="+extAttr.getBoui()+"' frameBorder='0' width='100%'  scrolling=no height='180px'  ></IFRAME></div>";
                    //                        out.print(iframe);
                }
            } else if (nodeName.equalsIgnoreCase("grid") && (bolist != null)) {
                ngtXMLHandler formBlock = childs[i];
                String blockName = formBlock.getNodeName();
                String category = formBlock.getAttribute("bo_node");
                boolean hasRights = true;
                boolean toContinue = true;

                //                    if ( category != null )
                //                    {
                //                        hasRights = doc.hasCategoryRights( category , bo );
                //                    }
                toContinue = hasRights;

                String formConstraint = formBlock.getAttribute("constraint");

                if (formConstraint != null) {
                    if (formConstraint.equalsIgnoreCase("when_object_is_extend")) {
                        if (bo != null) {
                            toContinue = toContinue &&
                                !bo.getBridge("extendAttribute").isEmpty();
                        }
                    } else if (formConstraint.equalsIgnoreCase(
                                "when_object_is_notextend")) {
                        if (bo != null) {
                            toContinue = toContinue &&
                                bo.getBridge("extendAttribute").isEmpty();
                        }
                    } else if (formConstraint.equalsIgnoreCase(
                                "when_extend_attribute_in_template_mode")) {
                        if (bo != null) {
                            toContinue = toContinue &&
                                (bo.getMode() == boObject.MODE_EDIT_TEMPLATE);
                        }
                    } else if (formConstraint.equalsIgnoreCase(
                                "when_object_in_template_mode")) {
                        toContinue = toContinue &&
                            (bo.getMode() == boObject.MODE_EDIT_TEMPLATE);
                    }
                }

                if (toContinue) //&& formBlock.getAttribute("forTemplate")==null )
                 {
                    String label = formBlock.getAttribute("label", "");

                    if ((label.length() > 0) && (category != null)) {
                        label = doc.getCategoryLabel(bo, viewerName, category);
                    }

                    buildGrid(label, doc, bo, bolist, order, domToBuild,
                        nodeToBuild, formBlock);
                }
            } else if (nodeName.equalsIgnoreCase("div")) {
                String xdiv = childs[i].getText();
                String _order = null;

                if (childs[i].getChildNode("order") != null) {
                    _order = childs[i].getChildNode("order")
                                      .getChildNode("attribute").getText();
                }

                if (xdiv.indexOf('.') > -1) {
                    String[] xd = xdiv.split("\\.");
                    String xd1 = xd[0];
                    String childFormName = xd[1];
                    AttributeHandler attr = bo.getAttribute(xd[0]);

                    if ((attr != null) && attr.hasRights()) {
                        boDefAttribute attrdef = attr.getDefAttribute();

                        if (attrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                            if (attrdef.getRelationType() == boDefAttribute.RELATION_1_TO_1) {
                                boObject o = attr.getObject();

                                if (o != null) {
                                    buildCard(doc, o, null, _order, viewerName,
                                        childFormName, domToBuild, nodeToBuild,
                                        o.getBoDefinition().getViewer(viewerName)
                                         .getForm(childFormName), "");
                                }
                            } else {
                                bridgeHandler bridge = bo.getBridge(xd[0]);
                                bridge.beforeFirst();
                                String fname = attr.getName() + "_" +
                                    childFormName;

                                if (bo.getBoDefinition().hasForm(viewerName,
                                            fname) &&
                                        !fname.equals("documents_list")) {
                                    buildCard(doc, bo, bridge, _order,
                                        viewerName, childFormName, domToBuild,
                                        nodeToBuild,
                                        bo.getBoDefinition()
                                          .getViewer(viewerName).getForm(fname),
                                        "");
                                } else {
                                    boObject o = null;
                                    if(bridge.next())
                                    {
                                        o = bridge.getObject();
                                    }
                                    if (o != null) {
                                        buildCard(doc, bo, bridge, _order,
                                            viewerName, childFormName,
                                            domToBuild, nodeToBuild,
                                            o.getBoDefinition()
                                             .getViewer(viewerName).getForm(childFormName),
                                            "");
                                    } else {
                                        if (bridge.getBoDef().hasForm(viewerName,
                                                    childFormName)) {
                                            buildCard(doc, bo, bridge, _order,
                                                viewerName, childFormName,
                                                domToBuild, nodeToBuild,
                                                bridge.getBoDef()
                                                      .getViewer(viewerName)
                                                      .getForm(childFormName),
                                                "");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                buildCard(doc, bo, bolist, order, viewerName, formName,
                    domToBuild, nodeToBuild, childs[i], "");
            }
        }

        String xxx = ngtXMLUtils.getXML(domToBuild);
        int dd = 1;
    }

    private static Node buildBlock(String blockName, String label, docHTML doc,
        boObject o, XMLDocument dom, Node node, ngtXMLHandler xmlForm) {
        Element area = dom.createElement("div");
        boolean isReportBodyNHeader = isReportBodyNHeaderFirstSon(node);

        if (isReportBodyNHeader) {
            area.setAttribute("class", blockName + "_title_NHeader");
        } else {
            area.setAttribute("class", blockName + "_title");
        }

        Node node1 = node.appendChild(area);
        Element tit = dom.createElement("p");

        tit.appendChild(dom.createTextNode("" + label));
        node1.appendChild(tit);

        return node;
    }

    private static Text getValueAttribute(XMLDocument dom, boObject parent,
        AttributeHandler attr, String order)
        throws SQLException, boRuntimeException {
        String value = null;
        Text toRet = null;
        boolean iscdata = false;

        if (attr != null) {
            if (attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                if (attr.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1) {
                    if (attr.getObject() != null) {
                        if ("Ebo_Document".equals(attr.getObject().getName()) ||
                                "Ebo_DocumentTemplate".equals(
                                    attr.getObject().getName()) ||
                                "Ebo_Document".equals(
                                    attr.getObject().getBoDefinition()
                                            .getBoSuperBo()) ||
                                "Ebo_DocumentTemplate".equals(
                                    attr.getObject().getBoDefinition()
                                            .getBoSuperBo())) {
                            String s=null;
                            if(attr.getObject().exists())
                            {
                                s = attr.getObject().getCARDIDwLink()
                                           .toString();
                            }
                            else
                            {
                                s = attr.getObject().getCARDID().toString();
                            }
                            value = setTry(s);
                        } else {
                            value = attr.getObject().getCARDID(false).toString();
                        }

                        iscdata = true;
                    }
                } else {
                    bridgeHandler bridge = parent.getBridge(attr.getName());
                    boBridgeIterator it;

                    if (order != null) {
                        it = new boBridgeIterator(bridge, order);
                    } else {
                        it = new boBridgeIterator(bridge);
                    }

                    it.beforeFirst();
                    value = "";

                    boObject aux;

                    while (it.next()) {
                        aux = it.currentRow().getObject();

                        if ("Ebo_Document".equals(aux.getName()) ||
                                "Ebo_DocumentTemplate".equals(aux.getName()) ||
                                "Ebo_Document".equals(
                                    aux.getBoDefinition().getBoSuperBo()) ||
                                "Ebo_DocumentTemplate".equals(
                                    aux.getBoDefinition().getBoSuperBo())) {
                            //                            String s = bridge.getObject().getCARDIDwLink().toString();
                            //                            value += s.replaceAll("winmain", "parent.winmain");
                            String s = null;
                            if(aux.exists())
                            {
                                s = aux.getCARDIDwLink().toString();
                            }
                            else
                            {
                                s = aux.getCARDID().toString();
                            }

                            value += setTry(s);
                        } else {
                            value += aux.getCARDID(false).toString();
                        }

                        iscdata = true;
                    }
                }
            } else {
                StringBuffer iscDate = new StringBuffer(1);
                value = getValue(parent, attr, iscDate);

                if ((attr.getDefAttribute().getValueType() == boDefAttribute.VALUE_CLOB) ||
                        "Y".equals(iscDate.toString())) {
                    iscdata = true;

                    //
                    if (!"Y".equals(iscDate.toString()) && !"".equals(value)) {
                        //é CLOB vou fazer render dentro de iframe
                        StringBuffer xx = new StringBuffer();
                        String aa = "s" + value.hashCode();
                        aa = aa.replaceAll("-", "");
                        xx.append("<script>function rez").append(aa)
                          .append("(){var o=document.getElementById('f")
                          .append(aa)
                          .append("');o.style.height=o.Document.body.scrollHeight+20;o.onresize=rez")
                          .append(aa).append("}</script>");
                        xx.append("<iframe id=f").append(aa).append(" style='width:100%;height:39px;overflow:visible' scrolling='auto' marginwidth='0' marginheight='0' frameborder='0' vspace='0' hspace='0' onload='");

                        //                        xx.append(aa);
                        xx.append("try{window.setTimeout(rez").append(aa)
                          .append(",200)}catch(e){}' src=''></iframe>");

                        //                        xx.append("<xml id=");
                        //                        xx.append(aa);
                        //                        xx.append(">");
                        xx.append(ClassUtils.removeHtmlGarbage(value));

                        //                        xx.append("</xml>");
                        value = xx.toString();
                    }

                    //
                }
            }

            if ((value == null) || (value.length() == 0)) {
                value = "NBSP";
            }

            if (attr.getParent().getName().equals("Ebo_Map")) {
                if (attr.getName().equals("objectAttributeName") ||
                        attr.getName().equals("value")) {
                    boObject o2 = attr.getParent();
                    String objectName = o2.getParentBridgeRow().getParent()
                                          .getAttribute("masterObjectClass")
                                          .getObject().getAttribute("name")
                                          .getValueString();

                    boDefHandler defobject = boDefHandler.getBoDefinition(objectName);
                    boDefAttribute atrdef = null;
                    String nameAttribute = attr.getParent()
                                               .getAttribute("objectAttributeName")
                                               .getValueString();

                    if (defobject != null) {
                        if ((value.indexOf(".") > -1) &&
                                attr.getName().equals("objectAttributeName")) {
                            value = value.split("\\.")[1];
                        }

                        if (nameAttribute.indexOf(".") > -1) {
                            nameAttribute = nameAttribute.split("\\.")[1];
                        }

                        atrdef = defobject.getAttributeRef(nameAttribute);
                    }

                    if (atrdef != null) {
                        if (attr.getName().equals("objectAttributeName")) {
                            value = atrdef.getLabel();
                        } else {
                            if (atrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                                if (value.indexOf(".") > -1) {
                                    String[] values = value.split("\\.");
                                    value = "";

                                    for (int i = 0; i < values.length; i++) {
                                        try {
                                            boObject o3 = o2.getObject(ClassUtils.convertToLong(
                                                        values[i]));
                                            value += o3.getCARDID(false)
                                                       .toString();
                                            iscdata = true;
                                        } catch (Exception e) {
                                            logger.warn(LoggerMessageLocalizer.getMessage("OBJECT_WITH_BOUI")+" " +
                                                values[i] +
                                                " "+LoggerMessageLocalizer.getMessage("NOT_FOUND_TEMPLATE_REFERENCE"));
                                        }
                                    }
                                } else {
                                    try {
                                        boObject o3 = o2.getObject(ClassUtils.convertToLong(
                                                    value));
                                        value = o3.getCARDID(false).toString();
                                        iscdata = true;
                                    } catch (Exception e) {
                                        logger.warn(LoggerMessageLocalizer.getMessage("OBJECT_WITH_BOUI")+" " +
                                            value +
                                            " "+LoggerMessageLocalizer.getMessage("NOT_FOUND_TEMPLATE_REFERENCE"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            value = "NBSP";
        }

        if (iscdata) {
            toRet = dom.createCDATASection(value);
        } else {
            toRet = dom.createTextNode(value);
        }

        return toRet;
    }

    private static String setTry(String s) {
        int pos = s.indexOf("onclick=\"");

        if (pos >= 0) {
            String toRet = s.substring(0, pos + 9);
            String toSub = s.substring(pos + 9, s.indexOf("\"", pos + 9));
            toSub = toSub.replaceAll("winmain", "parent.winmain");
            toRet += ("try{" + toSub + ";}catch(e){}" +
            s.substring(s.indexOf("\"", pos + 9)));

            return toRet;
        }

        return s;
    }

    private static void renderExtendAttributesForPrint(String label,
        docHTML doc, boObject o, XMLDocument dom, Node node,
        ngtXMLHandler xmlForm) throws SQLException, boRuntimeException {
        bridgeHandler bridge = o.getBridge("extendAttribute");
        boBridgeIterator it = bridge.iterator();

        Element table = dom.createElement("table");
        node.appendChild(table);
        table.setAttribute("align", "top");
        table.setAttribute("class", "section");
        table.setAttribute("cellSpacing", "0");
        table.setAttribute("cellPadding", "3");
        table.setAttribute("width", "100%");

        Element colgroup = dom.createElement("COLGROUP");
        table.appendChild(colgroup);

        Element col = dom.createElement("COL");
        col.setAttribute("width", "120");
        table.appendChild(col);
        col = dom.createElement("COL");
        table.appendChild(col);
        col = dom.createElement("COL");
        col.setAttribute("style", "PADDING-LEFT:5px");
        col.setAttribute("width", "70px");
        table.appendChild(col);
        col = dom.createElement("COL");
        table.appendChild(col);

        Element tbody = dom.createElement("TBODY");
        table.appendChild(tbody);

        while (it.next()) {
            boObject extAttr = (boObject) it.currentRow().getObject();

            long attributeType = extAttr.getAttribute("attributeType")
                                        .getValueLong();
            long cadinalidade = extAttr.getAttribute("attributeCardinal")
                                       .getValueLong();

            if (cadinalidade == 1) {
                Element TR = dom.createElement("TR");
                tbody.appendChild(TR);

                Element TD1 = dom.createElement("td");
                TD1.appendChild(dom.createTextNode(extAttr.getAttribute("alias")
                                                          .getValueString()));
                TD1.setAttribute("class", "label");
                TD1.setAttribute("width", "10%");

                Element TD2 = dom.createElement("td");

                TD2.appendChild(getValueExtendedAttribute(dom, extAttr, doc));

                TD2.setAttribute("width", "90%");
                TD2.setAttribute("colspan", "3");
                TD2.setAttribute("class", "input");
                TR.appendChild(TD1);
                TR.appendChild(TD2);
            } else {
                Element TR = dom.createElement("TR");
                tbody.appendChild(TR);

                Element TD1 = dom.createElement("td");
                TD1.setAttribute("colspan", "4");
                TD1.appendChild(buildExtendedAttributeList(dom, extAttr, doc));
                TR.appendChild(TD1);

                //                        out.print("<tr><td colspan=4 height=190px >");
                //                        String iframe = "<div class=extendList><IFRAME id='inc_" + extAttr.getName() + "__" +extAttr.getBoui() + "__valueList' src='__extendAttributeList.jsp?docid="+IDX+"&method=list&parent_attribute=valueList&parent_boui="+extAttr.getBoui()+"' frameBorder='0' width='100%'  scrolling=no height='180px'  ></IFRAME></div>";
                //                        out.print(iframe);
                //                        out.print("\n");
                //                        out.print("</td></tr>");
            }
        }

        //           out.print("</tbdoy></table>");
    }

    private static void renderIFRAME(String id, docHTML doc, boObject o,
        XMLDocument dom, Node node, ngtXMLHandler form)
        throws SQLException, boRuntimeException {
        try {
            String __url = form.getText();
            String parameters = null;

            ngtXMLHandler xatt;
            ngtXMLHandler[] xChilds = null;
            ngtXMLHandler xparam = form.getChildNode("parameters");

            if (xparam != null) {
                xChilds = xparam.getChildNodes();

                String nodeName;
                String xvalue;
                parameters = "";

                for (int i = 0; i < xChilds.length; i++) {
                    xatt = xChilds[i];

                    nodeName = xatt.getNodeName();

                    if (i != 0) {
                        parameters += "&";
                    }

                    parameters += nodeName;
                    parameters += "=";

                    //parameters+= "=\" + ";
                    parameters += getFrameParameters(o.getBoDefinition(), o,
                        nodeName, xatt.getText());
                }

                nodeName = "docid";

                if (xChilds.length > 0) {
                    parameters += ("&");
                }

                parameters += nodeName;
                parameters += ("=" + doc.getDocIdx());

                parameters += ";";
            }

            // Atributos da tag iframe
            Attr[] ifAttr = form.getAttributes();
            String idAux = "";
            boolean hasID = false;
            Element div = dom.createElement("div");
            Element iframe = dom.createElement("IFRAME");
            Comment coment = dom.createComment(
                    "ie bug iframe must have a child");
            iframe.appendChild(coment);
            div.appendChild(iframe);

            node.appendChild(div);

            for (int i = 0; i < ifAttr.length; i++) {
                Attr attx = ifAttr[i];

                if ("id".equalsIgnoreCase(attx.getName())) {
                    hasID = true;
                    id = attx.getValue().replaceAll("this",
                            String.valueOf(o.getBoui()));
                    iframe.setAttribute("id", id);
                } else {
                    iframe.setAttribute(attx.getName(), attx.getValue());
                }
            }

            if (!hasID) {
                iframe.setAttribute("id", id);
            }

            if (parameters == null) {
                iframe.setAttribute("src", __url + "?frameID=" + id);
            } else {
                iframe.setAttribute("src",
                    __url + "?frameID=" + id + "&" + parameters);
            }
        } catch (Exception e) {
            logger.severe(e);
        }
    }

    private static Element buildExtendedAttributeList(XMLDocument dom,
        boObject extAttr, docHTML doc) throws SQLException, boRuntimeException {
        //header
        Element toRet = dom.createElement("div");
        toRet.setAttribute("class", "extendList");

        boolean[] renderCols = new boolean[1];
        String[] renderColsNames = new String[1];
        renderCols[0] = true;
        renderColsNames[0] = "cardid";

        //         for (int i = 0; i < cols.length ; i++)
        //         {
        //             String AttributeName = cols[i].getFirstChild().getText();
        //             renderCols[i] = true;
        //             if ( bolist.getName().equals("DAO") && AttributeName.equals("name") )
        //             {
        //                 renderCols[i]=false;
        //             }
        //         }
        Element table = dom.createElement("table");
        toRet.appendChild(table);
        table.setAttribute("class", "grid");

        Element tableH = dom.createElement("thead");
        table.appendChild(tableH);

        Element tableB = dom.createElement("tbody");
        table.appendChild(tableB);

        Element TR = dom.createElement("TR");
        tableH.appendChild(TR);

        Element TH1 = dom.createElement("TH");
        TH1.setAttribute("align", "left");
        TR.appendChild(TH1);

        //         TH1.appendChild(dom.createTextNode("NBSP"));
        String title = extAttr.getAttribute("alias").getValueString();
        TH1.appendChild(dom.createTextNode(title));
        TH1.setAttribute("class", "gridCHeader");

        //         for (int i = 0; i < renderCols.length ; i++)
        //         {
        //             if ( renderCols[i] )
        //             {
        //                 Element TH = dom.createElement("TH");
        //                 TR.appendChild( TH );
        //                 String AttributeName = renderColsNames[0];
        //
        //                 String title="NBSP";
        //
        //                 title= extAttr.getAttribute("alias").getValueString();
        //
        //                 TH.setAttribute("class","gridCHeader");
        //                 TH.appendChild( dom.createTextNode( title )  );
        //             }
        //
        //         }
        bridgeHandler bolist = extAttr.getBridge("valueList");

        bolist.beforeFirst();

        while (bolist.next()) {
            Element TRB = dom.createElement("TR");
            boObject o = bolist.getObject();
            tableB.appendChild(TRB);

            Element TD1 = dom.createElement("TD");

            // if ( !bolist.haveVL() )
            TD1.appendChild(dom.createCDATASection(o.getCARDIDwState().toString()));

            // else
            // TD1.appendChild( dom.createTextNode( "NBSP" ) );
            TRB.appendChild(TD1);
            TD1.setAttribute("class", "gridCBody");

            //                 for (int i = 0; i < cols.length ; i++)
            //                 {
            //                     if ( renderCols[i])
            //                     {
            //                         boolean iscdata=false;
            //                         Element TD = dom.createElement("TD");
            //                         TRB.appendChild( TD );
            //                         String AttributeName = cols[i].getFirstChild().getText();
            //                         AttributeHandler attr= o.getAttribute( AttributeName );
            //                         Text value=null;
            //
            //                         if ( attr != null )
            //                         {
            //
            //                             value=getValueAttribute( dom , attr );
            //
            //                         }
            //                         else
            //                         {
            //                             attr=((bridgeHandler) bolist).getAttribute( AttributeName );
            //                             value=getValueAttribute( dom , attr );
            //
            //                         }
            //                         TD.setAttribute("class","gridCBody");
            //                         TD.appendChild( value );
            //
            //
            //                     }
            //
            //                }
        }

        return toRet;
    }

    private static Text getValueExtendedAttribute(XMLDocument dom,
        boObject extAttr, docHTML doc) throws SQLException, boRuntimeException {
        String value = null;
        Text toRet = null;
        boolean iscdata = false;

        if (extAttr != null) {
            long attributeType = extAttr.getAttribute("attributeType")
                                        .getValueLong();
            String attrName = DocWfHTML.getExtendAttributeName(extAttr);
            AttributeHandler attrHandler = extAttr.getAttribute(attrName);
            String valueObject = "";

            if (extAttr.getAttribute(attrName).getValueString() != null) {
                valueObject = extAttr.getAttribute(attrName).getValueString();
            }

            if (attributeType == 0) {
                if (valueObject.length() > 0) {
                    boObject o = doc.getObject(ClassUtils.convertToLong(
                                valueObject));

                    if (o != null) {
                        value = o.getCARDID(false).toString();
                    }

                    iscdata = true;
                }
            } else {
                value = valueObject; //  attr.getValueString();

                if (attributeType == 1) // boolean
                 {
                    int i0 = 1;
                } else if (attributeType == 4) {
                } //number
                else if (attributeType == 5) {
                } //datetime
                else if (attributeType == 6) {
                } //date
                else if (attributeType == 9) //text
                 {
                    iscdata = true;
                } else if (attributeType == 12) {
                    String[] values = null;
                    long lovBoui = extAttr.getAttribute("lov").getValueLong();
                    boObject lov = null;

                    if (lovBoui != 0) {
                        lov = boObject.getBoManager().loadObject(doc.getEboContext(),
                                lovBoui);
                    }

                    bridgeHandler lovHandler = null;

                    if (lov != null) {
                        lovHandler = lov.getBridge("details");
                        lovHandler.beforeFirst();

                        while (lovHandler.next()) {
                            if (lovHandler.getObject().getAttribute("value")
                                              .getValueString().equals(value)) {
                                value = lovHandler.getObject()
                                                  .getAttribute("value")
                                                  .getValueString();
                            }
                        }
                    }
                }
            }

            if ((value == null) || (value.length() == 0)) {
                value = "NBSP";
            }
        } else {
            value = "NBSP";
        }

        if (iscdata) {
            toRet = dom.createCDATASection(value);
        } else {
            toRet = dom.createTextNode(value);
        }

        return toRet;
    }

    private static void buildSection(String label, docHTML doc, boObject o,
        XMLDocument dom, Node node, ngtXMLHandler xmlForm)
        throws SQLException, boRuntimeException {
        ngtXMLHandler[] rows = null;
        String xConstraint = xmlForm.getAttribute("constraint", "");

        if (xConstraint.indexOf("INTERFACE:") != -1) {
            String intf = xConstraint.split(":")[1];
            boDefHandler intDef = boDefHandler.getBoDefinition(intf);
            ngtXMLHandler intEdit = intDef.getViewer("general")
                                          .getChildNode("forms").getChildNode("edit");
            rows = intEdit.getChildNode("rows").getChildNodes();
        } else {
            rows = new ngtXMLHandler[0];
            if(xmlForm.getChildNode("rows") != null)
            {
                rows = xmlForm.getChildNode("rows").getChildNodes();
            }
        }

        boolean renderSection = false;

        boolean[] renderRows = new boolean[rows.length];

        /*verificar seguranças */
        for (int i = 0; i < rows.length; i++) {
            ngtXMLHandler[] cells = rows[i].getChildNodes();

            String attr1Name = null;
            String attr2Name = null;

            ngtXMLHandler attNode = cells[0].getFirstChild();
            if ( attNode.getNodeName().equals("tag") )
            {
                attr1Name = attNode.getAttribute("relatedAttribute");
            }
            else
            {
                attr1Name = attNode.getText();
            }

            AttributeHandler attr1 = null;
            AttributeHandler attr2 = null;

            if (attr1Name != null) {
                attr1 = o.getAttribute(attr1Name);

            }

            if (cells.length == 2) {
                attNode = cells[1].getFirstChild();
                if(attNode == null)
                {
                    attr2Name = null;
                }
                else if ( attNode.getNodeName().equals("tag") )
                {
                    attr2Name = attNode.getAttribute("relatedAttribute");
                }
                else
                {
                    attr2Name = attNode.getText();
                }

                if (attr2Name != null) {
                    attr2 = o.getAttribute(attr2Name);
                }
            }

            renderRows[i] = false;


            if ((attr1 != null) && attr1.hasRights()) {
                renderSection = true;
                renderRows[i] = true;
            }

            if ((attr2 != null) && attr2.hasRights()) {
                renderSection = true;
                renderRows[i] = true;
            }
        }

        /*fim de verifcar segurity */
        if (renderSection) {
            if (!xmlForm.getAttribute("showlabel", "no").equalsIgnoreCase("no")) {
                Element area = dom.createElement("div");
                area.setAttribute("class", "section_title");

                Node node1 = node.appendChild(area);
                Element tit = dom.createElement("p");

                tit.appendChild(dom.createTextNode("" + label));
                node1.appendChild(tit);
            }

            Element table = dom.createElement("table");
            table.setAttribute("class", "section");

            node.appendChild(table);

            Element TRx = dom.createElement("tr");
            table.appendChild(TRx);

            Element TDx = dom.createElement("td");
            TDx.setAttribute("width", "120px");
            TRx.appendChild(TDx);
            TDx = dom.createElement("td");
            TDx.setAttribute("style", "\"PADDING-LEFT: 5px\"");
            TRx.appendChild(TDx);
            TDx = dom.createElement("td");
            TDx.setAttribute("width", "70px");
            TRx.appendChild(TDx);

            TDx = dom.createElement("td");
            TRx.appendChild(TDx);

            for (int i = 0; i < rows.length; i++) {
                if (renderRows[i]) {
                    Element TR = dom.createElement("tr");

                    table.appendChild(TR);

                    ngtXMLHandler[] cells = rows[i].getChildNodes();
                    String attr1Name = null;
                    String attr2Name = null;

                    String attr1Tag = null;
                    String attr2Tag = null;

//                    attr1Name = (cells[0].getFirstChild() != null)
//                        ? cells[0].getFirstChild().getText() : null;


                    if( cells[0].getFirstChild() != null )
                    {
                        ngtXMLHandler attNode = cells[0].getFirstChild();
                        if ( attNode.getNodeName().equals("tag") )
                        {
                            attr1Name = attNode.getAttribute("relatedAttribute");
                            attr1Tag  = attNode.getAttribute("class");

                        }
                        else
                        {
                            attr1Name = attNode.getText();
                        }
                    }


                    AttributeHandler attr1 = null;
                    AttributeHandler attr2 = null;

                    if (attr1Name != null) {
                        attr1 = o.getAttribute(attr1Name);
                    }

                    if (cells.length == 2) {
                        attr2Name = (cells[1].getFirstChild() != null)
                            ? cells[1].getFirstChild().getText() : null;


                        if( cells[1].getFirstChild() != null )
                        {
                            ngtXMLHandler attNode = cells[1].getFirstChild();
                            if ( attNode.getNodeName().equals("tag") )
                            {
                                attr2Name = attNode.getAttribute("relatedAttribute");
                                attr2Tag  = attNode.getAttribute("class");

                            }
                            else
                            {
                                attr2Name = attNode.getText();
                            }
                        }

                        if (attr2Name != null) {
                            attr2 = o.getAttribute(attr2Name);
                        }
                    }

                    if ((attr1 != null) && !attr1.canAccess()) {
                        attr1 = null;
                    }

                    if ((attr2 != null) && !attr2.canAccess()) {
                        attr2 = null;
                    }

                    if (cells.length == 1) {
                        if (attr1 != null) {
                            String xx = cells[0].getFirstChild().getNodeName();
                            boolean showLabel = cells[0].getFirstChild()
                                                        .getAttribute("showlabel",
                                    "no").equalsIgnoreCase("yes");

                            if (showLabel) {
                                Element TD1 = dom.createElement("td");
                                TD1.appendChild(dom.createTextNode(
                                        attr1.getDefAttribute().getLabel()));
                                TD1.setAttribute("class", "label");
                                TD1.setAttribute("width", "10%");

                                Element TD2 = dom.createElement("td");

                                TD2.appendChild(
                                    attr1Tag == null?
                                    getValueAttribute(dom, o, attr1, null)
                                    :
                                    getValueTag( dom, attr1Tag, o.getEboContext(), doc, o, attr1 )
                                );

                                TD2.setAttribute("width", "90%");
                                TD2.setAttribute("colspan", "3");
                                TD2.setAttribute("class", "input");
                                TR.appendChild(TD1);
                                TR.appendChild(TD2);
                            } else {
                                Element TD2 = dom.createElement("td");
                                TD2.appendChild(
                                    attr1Tag == null?
                                    getValueAttribute(dom, o, attr1, null)
                                    :
                                    getValueTag( dom, attr1Tag, o.getEboContext(), doc, o, attr1 )
                                );
                                TD2.setAttribute("width", "100%");
                                TD2.setAttribute("colspan", "4");
                                TD2.setAttribute("class", "input");
                                TR.appendChild(TD2);
                            }
                        } else {
                            Element TD = dom.createElement("td");
                            TD.setAttribute("class", "label");
                            TD.setAttribute("width", "100%");
                            TD.setAttribute("colspan", "4");
                            TR.appendChild(TD);
                        }
                    } else // cells.length=2
                     {
                        if (attr1 != null) {
                            boolean showLabel = cells[0].getFirstChild()
                                                        .getAttribute("showlabel",
                                    "no").equalsIgnoreCase("yes");

                            if (showLabel) {
                                Element TD1 = dom.createElement("td");
                                TD1.appendChild(dom.createTextNode(
                                        attr1.getDefAttribute().getLabel()));
                                TD1.setAttribute("class", "label");
                                TD1.setAttribute("width", "10%");

                                Element TD2 = dom.createElement("td");

                                TD2.appendChild(
                                    attr1Tag == null?
                                    getValueAttribute(dom, o, attr1, null)
                                    :
                                    getValueTag( dom, attr1Tag, o.getEboContext(), doc, o, attr1 )
                                );

                                TD2.setAttribute("width", "40%");
                                TD2.setAttribute("class", "input");
                                TR.appendChild(TD1);
                                TR.appendChild(TD2);
                            } else {
                                Element TD2 = dom.createElement("td");
                                TD2.appendChild(
                                    attr1Tag == null?
                                    getValueAttribute(dom, o, attr1, null)
                                    :
                                    getValueTag( dom, attr1Tag, o.getEboContext(), doc, o, attr1 )
                                );

                                TD2.setAttribute("width", "50%");
                                TD2.setAttribute("colspan", "2");
                                TD2.setAttribute("class", "input");
                                TR.appendChild(TD2);
                            }
                        } else {
                            Element TD = dom.createElement("td");
                            TD.setAttribute("class", "label");
                            TD.setAttribute("width", "50%");
                            TD.setAttribute("colspan", "2");
                            TR.appendChild(TD);
                        }

                        if (attr2 != null) {
                            boolean showLabel = cells[1].getFirstChild()
                                                        .getAttribute("showlabel",
                                    "no").equalsIgnoreCase("yes");

                            if (showLabel) {
                                Element TD1 = dom.createElement("td");
                                TD1.appendChild(dom.createTextNode(
                                        attr2.getDefAttribute().getLabel()));
                                TD1.setAttribute("class", "label");
                                TD1.setAttribute("width", "10%");

                                Element TD2 = dom.createElement("td");
                                TD2.appendChild(
                                    attr1Tag == null?
                                    getValueAttribute(dom, o, attr2, null)
                                    :
                                    getValueTag( dom, attr2Tag, o.getEboContext(), doc, o, attr2 )
                                );
                                TD2.setAttribute("width", "40%");
                                TD2.setAttribute("class", "input");
                                TR.appendChild(TD1);
                                TR.appendChild(TD2);
                            } else {
                                Element TD2 = dom.createElement("td");
                                TD2.appendChild(
                                    attr1Tag == null?
                                    getValueAttribute(dom, o, attr2, null)
                                    :
                                    getValueTag( dom, attr2Tag, o.getEboContext(), doc, o, attr2 )
                                );

                                TD2.setAttribute("width", "50%");
                                TD2.setAttribute("colspan", "2");
                                TD2.setAttribute("class", "input");
                                TR.appendChild(TD2);
                            }
                        } else {
                            Element TD = dom.createElement("td");
                            TD.setAttribute("class", "label");
                            TD.setAttribute("width", "50%");
                            TD.setAttribute("colspan", "2");
                            TR.appendChild(TD);
                        }
                    }
                }
            }
        }
    }

    private static void buildGrid(String label, docHTML doc, boObject parent,
        boObjectList bolist, String order, XMLDocument dom, Node node,
        ngtXMLHandler xmlForm) throws SQLException, boRuntimeException {
        ngtXMLHandler[] cols = xmlForm.getChildNode("cols").getChildNodes();

        boolean[] renderCols = new boolean[cols.length];

        for (int i = 0; i < cols.length; i++) {
            String AttributeName = cols[i].getFirstChild().getText();
            renderCols[i] = true;

            if (bolist.getName().equals("DAO") && AttributeName.equals("name")) {
                renderCols[i] = false;
            }
        }

        //header
        Element table = dom.createElement("table");
        table.setAttribute("class", "grid");
        node.appendChild(table);

        Element tableH = dom.createElement("thead");
        table.appendChild(tableH);

        Element tableB = dom.createElement("tbody");
        table.appendChild(tableB);

        Element TR = dom.createElement("TR");
        tableH.appendChild(TR);
        bolist.beforeFirst();
        bolist.next();

        boObject o = bolist.getObject();
        Element TH1 = dom.createElement("TH");
        TR.appendChild(TH1);
        TH1.appendChild(dom.createTextNode("NBSP"));
        TH1.setAttribute("class", "gridCHeader");

        boDefHandler def = bolist.getBoDef();

        for (int i = 0; i < cols.length; i++) {
            if (renderCols[i]) {
                Element TH = dom.createElement("TH");
                TR.appendChild(TH);

                String AttributeName = cols[i].getFirstChild().getText();

                boDefAttribute attrdef = def.getAttributeRef(AttributeName);

                String title = "NBSP";

                if (attrdef != null) {
                    title = attrdef.getLabel();
                } else {
                    if (!bolist.isEmpty()) {
                        AttributeHandler attr = ((bridgeHandler) bolist).getAttribute(AttributeName);

                        if (attr != null) {
                            title = attr.getDefAttribute().getLabel();
                        }
                    }
                }

                TH.setAttribute("class", "gridCHeader");
                TH.appendChild(dom.createTextNode(title));
            }
        }

        if (bolist.first()) {
            if (order != null) {
                if (!bolist.ordered()) {
                    bolist.setOrderBy(order);
                }
            }

            bolist.beforeFirst();

            while (bolist.next()) {
                Element TRB = dom.createElement("TR");
                o = bolist.getObject();
                tableB.appendChild(TRB);

                Element TD1 = dom.createElement("TD");

                // if ( !bolist.haveVL() )
                //                    String s = o.getCARDIDwStatewLink().toString();
                //                    s = s.replaceAll("winmain", "parent.winmain");
                String s =null;
                if(o.exists())
                {
                    s = o.getCARDIDwStatewLink().toString();
                }
                else
                {
                    s = o.getCARDIDwState().toString();
                }
                s = setTry(s);
                TD1.appendChild(dom.createCDATASection(s));

                //                     TD1.appendChild( dom.createCDATASection( o.getCARDIDwState().toString() ) );
                // else
                // TD1.appendChild( dom.createTextNode( "NBSP" ) );
                TRB.appendChild(TD1);
                TD1.setAttribute("class", "gridCBody");

                for (int i = 0; i < cols.length; i++) {
                    if (renderCols[i]) {
                        boolean iscdata = false;
                        Element TD = dom.createElement("TD");
                        TRB.appendChild(TD);

                        String AttributeName = cols[i].getFirstChild().getText();
                        AttributeHandler attr = o.getAttribute(AttributeName);
                        Text value = null;

                        if (attr != null) {
                            value = getValueAttribute(dom, parent, attr, order);
                        } else {
                            attr = ((bridgeHandler) bolist).getAttribute(AttributeName);
                            value = getValueAttribute(dom, parent, attr, order);
                        }

                        TD.setAttribute("class", "gridCBody");
                        TD.appendChild(value);
                    }
                }
            }
        } else {
            Element TRB = dom.createElement("TR");

            tableB.appendChild(TRB);

            Element TD1 = dom.createElement("TD");
            TD1.setAttribute("colspan", "" + cols.length + 1);
            TD1.appendChild(dom.createCDATASection("Sem registos"));

            TRB.appendChild(TD1);
            TD1.setAttribute("class", "gridCBody");
        }
    }

    // show difference
    private static void buildCard(DifferenceContainer dc, docHTML doc,
        boObject srcBo, boObject dstBo, boObjectList bolist,
        boObjectList bolistDst, String viewerName, String formName,
        XMLDocument domToBuild, Node nodeToBuild, ngtXMLHandler xmlForm)
        throws SQLException, boRuntimeException {
        ngtXMLHandler[] childs = xmlForm.getChildNodes();
        Node xx = xmlForm.getNode();
        int nodeType = xx.getNodeType();

        for (int i = 0; i < childs.length; i++) {
            String nodeName = childs[i].getNodeName();

            if (nodeName.equalsIgnoreCase("Areas") ||
                    nodeName.equalsIgnoreCase("Panel")) {
                ngtXMLHandler[] blockChilds = childs[i].getChildNodes();

                for (int j = 0; j < blockChilds.length; j++) {
                    ngtXMLHandler formBlock = blockChilds[j];
                    String blockName = formBlock.getNodeName();
                    String category = formBlock.getAttribute("bo_node");
                    boolean hasRights = true;

                    /*if ( category != null )
                    {
                        hasRights = doc.hasCategoryRights( category , srcBo );
                    }*/
                    if (hasRights &&
                            (formBlock.getAttribute("forTemplate") == null)) {
                        String label = formBlock.getAttribute("label", "");

                        if (blockName.equals("tab") && (label.length() == 0)) {
                            label = doc.getCategoryLabel_for_TAB_Header(category,
                                    srcBo);
                        } else if ((label.length() > 0) && (category != null)) {
                            label = doc.getCategoryLabel(srcBo, viewerName,
                                    category);
                        }

                        Node xnode = buildBlock(blockName, label, doc, srcBo,
                                domToBuild, nodeToBuild, formBlock);
                        buildCard(dc, doc, srcBo, dstBo, bolist, bolistDst,
                            viewerName, formName, domToBuild, xnode, formBlock);
                    }
                }
            } else if (nodeName.equalsIgnoreCase("section")) {
                ngtXMLHandler formBlock = childs[i];
                String blockName = formBlock.getNodeName();
                String category = formBlock.getAttribute("bo_node");
                boolean hasRights = true;

                // TODO pq?

                /*                   if ( category != null )
                                   {
                                       hasRights = doc.hasCategoryRights( category , srcBo );
                                   }
                  */
                if (hasRights) {
                    String label = formBlock.getAttribute("label", "");

                    if ((label.length() > 0) && (category != null)) {
                        label = doc.getCategoryLabel(srcBo, viewerName, category);
                    }

                    buildSection(dc, label, doc, srcBo, dstBo, domToBuild,
                        nodeToBuild, formBlock);
                }
            } else if (nodeName.equalsIgnoreCase("grid") && (bolist != null)) {
                ngtXMLHandler formBlock = childs[i];
                String blockName = formBlock.getNodeName();
                String category = formBlock.getAttribute("bo_node");
                boolean hasRights = true;

                /* if ( category != null )
                 {
                     hasRights = doc.hasCategoryRights( category , srcBo );
                 }*/
                if (hasRights) {
                    String label = formBlock.getAttribute("label", "");

                    if ((label.length() > 0) && (category != null)) {
                        label = doc.getCategoryLabel(srcBo, viewerName, category);
                    }

                    buildGrid(dc, label, doc, srcBo, bolist, bolistDst,
                        domToBuild, nodeToBuild, formBlock);
                }
            } else if (nodeName.equalsIgnoreCase("div")) {
                String xdiv = childs[i].getText();

                if (xdiv.indexOf('.') > -1) {
                    String[] xd = xdiv.split("\\.");
                    String xd1 = xd[0];
                    String childFormName = xd[1];
                    AttributeHandler attr = srcBo.getAttribute(xd[0]);
                    AttributeHandler attrDst = null;

                    if (dstBo != null) {
                        attrDst = dstBo.getAttribute(xd[0]); //novo
                    }

                    if ((attr != null) && attr.hasRights()) {
                        boDefAttribute attrdef = attr.getDefAttribute();

                        if (attrdef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                            if (attrdef.getRelationType() == boDefAttribute.RELATION_1_TO_1) {
                                boObject o = attr.getObject();
                                boObject oDst = null;

                                if (attrDst != null) {
                                    oDst = attrDst.getObject();
                                }

                                if (o != null) {
                                    buildCard(dc, doc, o, oDst, null, null,
                                        viewerName, childFormName, domToBuild,
                                        nodeToBuild,
                                        o.getBoDefinition().getViewer(viewerName)
                                         .getForm(childFormName));
                                }
                            } else {
                                bridgeHandler bridge = srcBo.getBridge(xd[0]);
                                bridgeHandler bridgeDst = null;

                                if (dstBo != null) {
                                    bridgeDst = dstBo.getBridge(xd[0]);
                                }

                                String fname = attr.getName() + "_" +
                                    childFormName;

                                if (srcBo.getBoDefinition().hasForm(viewerName,
                                            fname)) {
                                    buildCard(dc, doc, srcBo, dstBo, bridge,
                                        bridgeDst, viewerName, childFormName,
                                        domToBuild, nodeToBuild,
                                        srcBo.getBoDefinition()
                                             .getViewer(viewerName).getForm(fname));
                                } else {
                                    boObject o = bridge.getObject();

                                    if (o != null) {
                                        buildCard(dc, doc, srcBo, dstBo,
                                            bridge, bridgeDst, viewerName,
                                            childFormName, domToBuild,
                                            nodeToBuild,
                                            o.getBoDefinition()
                                             .getViewer(viewerName).getForm(childFormName));
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                buildCard(dc, doc, srcBo, dstBo, bolist, bolistDst, viewerName,
                    formName, domToBuild, nodeToBuild, childs[i]);
            }
        }

        String xxx = ngtXMLUtils.getXML(domToBuild);
        int dd = 1;
    }

    private static void buildGrid(DifferenceContainer diffContainer,
        String label, docHTML doc, boObject parent, boObjectList bolist,
        boObjectList bolistDst, XMLDocument dom, Node node,
        ngtXMLHandler xmlForm) throws SQLException, boRuntimeException {
        ngtXMLHandler[] cols = xmlForm.getChildNode("cols").getChildNodes();

        boolean[] renderCols = new boolean[cols.length];

        for (int i = 0; i < cols.length; i++) {
            String AttributeName = cols[i].getFirstChild().getText();
            renderCols[i] = true;

            if (bolist.getName().equals("DAO") && AttributeName.equals("name")) {
                renderCols[i] = false;
            }
        }

        //header
        Element table = dom.createElement("table");
        table.setAttribute("class", "grid");
        node.appendChild(table);

        Element tableH = dom.createElement("thead");
        table.appendChild(tableH);

        Element tableB = dom.createElement("tbody");
        table.appendChild(tableB);

        Element TR = dom.createElement("TR");
        tableH.appendChild(TR);
        bolist.beforeFirst();
        bolist.next();

        boObject o = bolist.getObject();
        Element TH1 = dom.createElement("TH");
        TR.appendChild(TH1);
        TH1.appendChild(dom.createTextNode("NBSP"));
        TH1.setAttribute("class", "gridCHeader");

        for (int i = 0; i < cols.length; i++) {
            if (renderCols[i]) {
                Element TH = dom.createElement("TH");
                TR.appendChild(TH);

                String AttributeName = cols[i].getFirstChild().getText();
                boDefAttribute attrdef = bolist.getBoDef().getAttributeRef(AttributeName);

                //AttributeHandler attr= o.getAttribute( AttributeName );
                String title = "NBSP";

                if (attrdef != null) {
                    title = attrdef.getLabel();
                } else {
                    if (!bolist.isEmpty()) {
                        AttributeHandler attr = ((bridgeHandler) bolist).getAttribute(AttributeName);

                        if (attr != null) {
                            title = attr.getDefAttribute().getLabel();
                        }
                    }
                }

                TH.setAttribute("class", "gridCHeader");
                TH.appendChild(dom.createTextNode(title));
            }
        }

        DifferenceElement diffElem = null;
        bridgeHandler bridgeDst = null;

        //         if(dstBo != null)
        //         {
        //           bridgeDst = dstBo.getBridge( bolist.getName() );
        //         }
        //         if(bridgeDst == null)
        //         {
        //             bridgeDst = (bridgeHandler)bolistDst;
        //         }
        bridgeDst = (bridgeHandler) bolistDst;

        if (bridgeDst != null) {
            bridgeDst.beforeFirst();
        }

        bolist.beforeFirst();

        while (bolist.next()) {
            long bouiNew = 0;
            o = bolist.getObject();

            boolean exists = false;

            if (bridgeDst != null) {
                exists = bridgeDst.haveBoui(o.getBoui());
            }

            Element TRB = dom.createElement("TR");

            //if(!bolist.haveVL())
            tableB.appendChild(TRB);

            Element TD1 = dom.createElement("TD");

            //                 if ( !bolist.haveVL() )
            TD1.appendChild(dom.createCDATASection(o.getCARDIDwState().toString()));

            //  else
            //     TD1.appendChild( dom.createTextNode( "NBSP" ) );
            TRB.appendChild(TD1);

            //                 TD1.setAttribute("class", (exists || bolist.haveVL()) ? "gridCBody" : "gridCBodySrc");
            TD1.setAttribute("class", (exists) ? "gridCBody" : "gridCBodySrc");

            for (int i = 0; i < cols.length; i++) {
                if (renderCols[i]) {
                    boolean iscdata = false;
                    Element TD = dom.createElement("TD");
                    TRB.appendChild(TD);

                    String AttributeName = cols[i].getFirstChild().getText();
                    diffElem = diffContainer.getBridgeDiffElement(AttributeName,
                            bolist.getName(), o.getBoui());

                    AttributeHandler attr = o.getAttribute(AttributeName);

                    Text value = null;

                    if (attr != null) {
                        value = getValueAttribute(dom, parent, attr, null);
                    } else {
                        attr = ((bridgeHandler) bolist).getAttribute(AttributeName);
                        value = getValueAttribute(dom, parent, attr, null);
                    }

                    if (diffElem != null) {
                        TD.setAttribute("class", "gridCBodySrc");
                        bouiNew = diffElem.getBoui();
                    } else {
                        //                            TD.setAttribute("class", (exists || bolist.haveVL()) ? "gridCBody" : "gridCBodySrc");
                        TD.setAttribute("class",
                            (exists) ? "gridCBody" : "gridCBodySrc");
                    }

                    TD.appendChild(value);
                }
            }

            if (bouiNew != 0) {
                // linha só com os atributos alterados
                bridgeDst.beforeFirst();

                boObject objNew = bridgeDst.getObject(bouiNew);

                TRB = dom.createElement("TR");
                tableB.appendChild(TRB);

                TD1 = dom.createElement("TD");

                //                     if ( !bolist.haveVL() )
                TD1.appendChild(dom.createCDATASection(
                        objNew.getCARDIDwState().toString()));

                //                     else
                //                        TD1.appendChild( dom.createTextNode( "NBSP" ) );
                TRB.appendChild(TD1);
                TD1.setAttribute("class", "gridCBody");

                for (int i = 0; i < cols.length; i++) {
                    if (renderCols[i]) {
                        boolean iscdata = false;
                        Element TD = dom.createElement("TD");
                        TRB.appendChild(TD);

                        String AttributeName = cols[i].getFirstChild().getText();
                        diffElem = diffContainer.getBridgeDiffElement(AttributeName,
                                bolist.getName(), objNew.getBoui());

                        AttributeHandler attr = objNew.getAttribute(AttributeName);

                        Text value = null;

                        if (attr != null) {
                            value = getValueAttribute(dom, parent, attr, null);
                        } else {
                            attr = ((bridgeHandler) bolist).getAttribute(AttributeName);
                            value = getValueAttribute(dom, parent, attr, null);
                        }

                        if (diffElem != null) {
                            TD.setAttribute("class", "gridCBodyDst");
                        } else {
                            TD.setAttribute("class", "gridCBody");
                        }

                        TD.appendChild(value);
                    }
                }
            }
        }

        if ((bridgeDst != null) && (diffContainer.getBridgeDstDiffSize() > 0)) {
            bridgeDst.beforeFirst();

            while (bridgeDst.next()) {
                o = bridgeDst.getObject();
                diffElem = diffContainer.getBridgeDstDiffElem(bridgeDst.getName(),
                        o.getBoui());

                if (diffElem != null) {
                    Element TRB = dom.createElement("TR");
                    tableB.appendChild(TRB);

                    Element TD1 = dom.createElement("TD");

                    //                         if ( !bridgeDst.haveVL() )
                    TD1.appendChild(dom.createCDATASection(
                            o.getCARDIDwState().toString()));

                    //                         else
                    //                            TD1.appendChild( dom.createTextNode( "NBSP" ) );
                    TRB.appendChild(TD1);
                    TD1.setAttribute("class", "gridCBodyDst");

                    for (int i = 0; i < cols.length; i++) {
                        if (renderCols[i]) {
                            boolean iscdata = false;
                            Element TD = dom.createElement("TD");
                            TRB.appendChild(TD);

                            String AttributeName = cols[i].getFirstChild()
                                                          .getText();
                            AttributeHandler attr = o.getAttribute(AttributeName);

                            Text value = null;

                            if (attr != null) {
                                value = getValueAttribute(dom, parent, attr,
                                        null);
                            } else {
                                attr = ((bridgeHandler) bridgeDst).getAttribute(AttributeName);
                                value = getValueAttribute(dom, parent, attr,
                                        null);
                            }

                            TD.setAttribute("class", "gridCBodyDst");
                            TD.appendChild(value);
                        }
                    }
                }
            }
        }
    }

    private static void buildSection(DifferenceContainer diffContainer,
        String label, docHTML doc, boObject srcBo, boObject dstBo,
        XMLDocument dom, Node node, ngtXMLHandler xmlForm)
        throws SQLException, boRuntimeException {
        ngtXMLHandler[] rows = xmlForm.getChildNode("rows").getChildNodes();
        boolean renderSection = false;
        AttributeHandler attrDst = null;

        boolean[] renderRows = new boolean[rows.length];

        /*verificar seguranças */
        for (int i = 0; i < rows.length; i++) {
            ngtXMLHandler[] cells = rows[i].getChildNodes();
            String attr1Name = null;
            String attr2Name = null;

            attr1Name = (cells[0].getFirstChild() != null)
                ? cells[0].getFirstChild().getText() : null;

            AttributeHandler attr1 = null;
            AttributeHandler attr2 = null;

            if (attr1Name != null) {
                attr1 = srcBo.getAttribute(attr1Name);
            }

            if (cells.length == 2) {
                attr2Name = (cells[1].getFirstChild() != null)
                    ? cells[1].getFirstChild().getText() : null;

                if (attr2Name != null) {
                    attr2 = srcBo.getAttribute(attr2Name);
                }
            }

            renderRows[i] = false;

            if ((attr1 != null) && attr1.hasRights()) {
                renderSection = true;
                renderRows[i] = true;
            }

            if ((attr2 != null) && attr2.hasRights()) {
                renderSection = true;
                renderRows[i] = true;
            }
        }

        /*fim de verifcar segurity */
        if (renderSection) {
            if (!xmlForm.getAttribute("showlabel", "no").equalsIgnoreCase("no")) {
                Element area = dom.createElement("div");
                area.setAttribute("class", "section_title");

                Node node1 = node.appendChild(area);
                Element tit = dom.createElement("p");

                tit.appendChild(dom.createTextNode("" + label));
                node1.appendChild(tit);
            }

            Element table = dom.createElement("table");
            table.setAttribute("class", "section");

            node.appendChild(table);

            Element TRx = dom.createElement("tr");
            table.appendChild(TRx);

            Element TDx = dom.createElement("td");
            TDx.setAttribute("width", "10%");
            TRx.appendChild(TDx);
            TDx = dom.createElement("td");
            TDx.setAttribute("width", "40%");
            TRx.appendChild(TDx);
            TDx = dom.createElement("td");
            TDx.setAttribute("width", "10%");
            TRx.appendChild(TDx);

            TDx = dom.createElement("td");
            TDx.setAttribute("width", "40%");
            TRx.appendChild(TDx);

            for (int i = 0; i < rows.length; i++) {
                if (renderRows[i]) {
                    Element TR = dom.createElement("tr");
                    table.appendChild(TR);

                    ngtXMLHandler[] cells = rows[i].getChildNodes();
                    String attr1Name = null;
                    String attr2Name = null;

                    attr1Name = (cells[0].getFirstChild() != null)
                        ? cells[0].getFirstChild().getText() : null;

                    AttributeHandler attr1 = null;
                    AttributeHandler attr2 = null;

                    if (attr1Name != null) {
                        attr1 = srcBo.getAttribute(attr1Name);
                    }

                    if (cells.length == 2) {
                        attr2Name = (cells[1].getFirstChild() != null)
                            ? cells[1].getFirstChild().getText() : null;

                        if (attr2Name != null) {
                            attr2 = srcBo.getAttribute(attr2Name);
                        }
                    }

                    if ((attr1 != null) && !attr1.canAccess()) {
                        attr1 = null;
                    }

                    if ((attr2 != null) && !attr2.canAccess()) {
                        attr2 = null;
                    }

                    DifferenceElement diffElem = null;

                    if (cells.length == 1) {
                        if (attr1 != null) {
                            String xx = cells[0].getFirstChild().getNodeName();
                            diffElem = diffContainer.getDifferenceElement(attr1.getName(),
                                    srcBo.getBoui());

                            boolean showLabel = cells[0].getFirstChild()
                                                        .getAttribute("showlabel",
                                    "no").equalsIgnoreCase("yes");

                            if (showLabel) {
                                Element TD1 = dom.createElement("td");
                                TD1.appendChild(dom.createTextNode(
                                        attr1.getDefAttribute().getLabel()));

                                // TD1.setAttribute("class",(de == null) ? "label" : "labelSrc");
                                TD1.setAttribute("width", "10%");

                                Element TD2 = dom.createElement("td");
                                TD2.setAttribute("width", "90%");
                                TD2.setAttribute("colspan", "3");

                                boolean atObj = setValueAttribute(TD1, TD2,
                                        false, dom, srcBo, dstBo, attr1,
                                        diffElem, diffContainer, "inputSrc");

                                //TD2.setAttribute("class",(de == null) ? "input" : "inputSrc");
                                TR.appendChild(TD1);
                                TR.appendChild(TD2);

                                if ((diffElem != null) || atObj) {
                                    Element TD = dom.createElement("td");
                                    TD.setAttribute("class", "label");
                                    TD.setAttribute("width", "100%");
                                    TD.setAttribute("colspan", "4");
                                    TR.appendChild(TD);

                                    TR = dom.createElement("tr");
                                    table.appendChild(TR);

                                    if (attr1Name != null) {
                                        attrDst = dstBo.getAttribute(attr1Name);
                                    }

                                    Element TD1Dst = dom.createElement("td");

                                    //TD1Dst.appendChild(dom.createTextNode( attrDst.getDefAttribute().getLabel() ) );
                                    //TD1Dst.setAttribute("class","labelDst");
                                    TD1Dst.setAttribute("width", "10%");

                                    Element TD2Dst = dom.createElement("td");
                                    setValueAttribute(null, TD2Dst, true, dom,
                                        dstBo, srcBo, attrDst, diffElem,
                                        diffContainer, "inputDst");

                                    //TD2Dst.appendChild( getValueAttribute( dom , dstBo, attrDst ) );
                                    TD2Dst.setAttribute("width", "90%");
                                    TD2Dst.setAttribute("colspan", "3");

                                    //TD2Dst.setAttribute("class","inputDst");
                                    TR.appendChild(TD1Dst);
                                    TR.appendChild(TD2Dst);
                                }
                            } else {
                                Element TD2 = dom.createElement("td");
                                boolean atObj = setValueAttribute(null, TD2,
                                        false, dom, srcBo, dstBo, attr1,
                                        diffElem, diffContainer, "inputSrc");

                                //TD2.appendChild( getValueAttribute( dom , srcBo, attr1 ) );
                                TD2.setAttribute("width", "100%");
                                TD2.setAttribute("colspan", "4");

                                //TD2.setAttribute("class",(de == null) ? "input" : "inputSrc");
                                TR.appendChild(TD2);

                                if ((diffElem != null) || atObj) {
                                    Element TD = dom.createElement("td");
                                    TD.setAttribute("class", "label");
                                    TD.setAttribute("width", "100%");
                                    TD.setAttribute("colspan", "4");
                                    TR.appendChild(TD);

                                    TR = dom.createElement("tr");
                                    table.appendChild(TR);

                                    if (attr1Name != null) {
                                        attrDst = dstBo.getAttribute(attr1Name);
                                    }

                                    Element TD2Dst = dom.createElement("td");

                                    // TD2Dst.appendChild( getValueAttribute( dom , dstBo, attrDst ) );
                                    setValueAttribute(null, TD2Dst, true, dom,
                                        srcBo, dstBo, attrDst, diffElem,
                                        diffContainer, "inputDst");
                                    TD2Dst.setAttribute("width", "100%");
                                    TD2Dst.setAttribute("colspan", "4");

                                    //TD2Dst.setAttribute("class","inputDst");
                                    TR.appendChild(TD2Dst);
                                }
                            }
                        } else if (diffElem == null) {
                            Element TD = dom.createElement("td");
                            TD.setAttribute("class", "label");
                            TD.setAttribute("width", "100%");
                            TD.setAttribute("colspan", "4");
                            TR.appendChild(TD);
                        }
                    } else // cells.length=2
                     {
                        boolean atObjCol1 = false;
                        boolean atObjCol2 = false;

                        if (attr1 != null) {
                            boolean showLabel = cells[0].getFirstChild()
                                                        .getAttribute("showlabel",
                                    "no").equalsIgnoreCase("yes");
                            diffElem = diffContainer.getDifferenceElement(attr1.getName(),
                                    srcBo.getBoui());

                            if (showLabel) {
                                Element TD1 = dom.createElement("td");
                                TD1.appendChild(dom.createTextNode(
                                        attr1.getDefAttribute().getLabel()));
                                TD1.setAttribute("class",
                                    (diffElem == null) ? "label" : "labelSrc");
                                TD1.setAttribute("width", "10%");

                                Element TD2 = dom.createElement("td");
                                atObjCol1 = setValueAttribute(TD1, TD2, false,
                                        dom, srcBo, dstBo, attr1, diffElem,
                                        diffContainer, "inputSrc");

                                //TD2.appendChild( getValueAttribute( dom , srcBo, attr1 ) );
                                TD2.setAttribute("width", "40%");

                                //TD2.setAttribute("class",(de == null) ? "input" : "inputSrc");
                                TR.appendChild(TD1);
                                TR.appendChild(TD2);
                            } else {
                                Element TD2 = dom.createElement("td");

                                //TD2.appendChild( getValueAttribute( dom ,srcBo, attr1 ) );
                                atObjCol1 = setValueAttribute(null, TD2, false,
                                        dom, srcBo, dstBo, attr1, diffElem,
                                        diffContainer, "inputSrc");
                                TD2.setAttribute("width", "50%");
                                TD2.setAttribute("colspan", "2");
                                TD2.setAttribute("class",
                                    (diffElem == null) ? "input" : "inputSrc");
                                TR.appendChild(TD2);
                            }
                        } else {
                            Element TD = dom.createElement("td");
                            TD.setAttribute("class", "label");
                            TD.setAttribute("width", "50%");
                            TD.setAttribute("colspan", "2");
                            TR.appendChild(TD);
                        }

                        DifferenceElement diffElem2 = null;

                        if (attr2 != null) {
                            diffElem2 = diffContainer.getDifferenceElement(attr2.getName(),
                                    srcBo.getBoui());

                            boolean showLabel = cells[1].getFirstChild()
                                                        .getAttribute("showlabel",
                                    "no").equalsIgnoreCase("yes");

                            if (showLabel) {
                                Element TD1 = dom.createElement("td");
                                TD1.appendChild(dom.createTextNode(
                                        attr2.getDefAttribute().getLabel()));
                                TD1.setAttribute("class",
                                    (diffElem2 == null) ? "label" : "labelSrc");
                                TD1.setAttribute("width", "10%");

                                Element TD2 = dom.createElement("td");

                                //TD2.appendChild( getValueAttribute( dom , dstBo, attr2 ) );
                                atObjCol2 = setValueAttribute(TD1, TD2, true,
                                        dom, dstBo, srcBo, attr2, diffElem2,
                                        diffContainer, "inputSrc");
                                TD2.setAttribute("width", "40%");

                                //TD2.setAttribute("class",(de2 == null) ? "input" : "inputSrc");
                                TR.appendChild(TD1);
                                TR.appendChild(TD2);
                            } else {
                                Element TD2 = dom.createElement("td");

                                //TD2.appendChild( getValueAttribute( dom , dstBo, attr2 ) );
                                atObjCol2 = setValueAttribute(null, TD2, true,
                                        dom, dstBo, srcBo, attr2, diffElem2,
                                        diffContainer, "inputSrc");
                                TD2.setAttribute("width", "50%");
                                TD2.setAttribute("colspan", "2");

                                //TD2.setAttribute("class",(de2 == null) ? "input" : "inputSrc");
                                TR.appendChild(TD2);
                            }
                        } else {
                            Element TD = dom.createElement("td");
                            TD.setAttribute("class", "label");
                            TD.setAttribute("width", "50%");
                            TD.setAttribute("colspan", "2");
                            TR.appendChild(TD);
                        }

                        boolean noCell = false;

                        // Differencas
                        if (attr1 != null) {
                            if ((diffElem != null) || atObjCol1) {
                                TR = dom.createElement("tr");
                                table.appendChild(TR);

                                if (attr1Name != null) {
                                    attrDst = dstBo.getAttribute(attr1Name);
                                }

                                Element TD1Dst = dom.createElement("td");

                                // TD1Dst.appendChild(dom.createTextNode( attrDst.getDefAttribute().getLabel() ) );
                                //TD1Dst.setAttribute("class","labelDst");
                                TD1Dst.setAttribute("width", "10%");

                                Element TD2Dst = dom.createElement("td");

                                //TD2Dst.appendChild( getValueAttribute( dom ,dstBo, attrDst ) );
                                setValueAttribute(null, TD2Dst, true, dom,
                                    dstBo, srcBo, attrDst, diffElem,
                                    diffContainer, "inputDst");
                                TD2Dst.setAttribute("width", "40%");

                                //TD2Dst.setAttribute("class","inputDst");
                                TR.appendChild(TD1Dst);
                                TR.appendChild(TD2Dst);
                            }

                            noCell = true;
                        } else {
                            TR = dom.createElement("tr");
                            table.appendChild(TR);

                            Element TD = dom.createElement("td");
                            TD.setAttribute("class", "label");
                            TD.setAttribute("width", "50%");
                            TD.setAttribute("colspan", "2");
                            TR.appendChild(TD);
                        }

                        if (attr2 != null) {
                            if (diffElem2 != null) {
                                // tem de haver uma condiçao se attr1 == null para escrecer isto aqui!!!!
                                if (noCell) {
                                    TR = dom.createElement("tr");
                                    table.appendChild(TR);

                                    Element TD = dom.createElement("td");
                                    TD.setAttribute("class", "label");
                                    TD.setAttribute("width", "50%");
                                    TD.setAttribute("colspan", "2");
                                    TR.appendChild(TD);
                                }

                                if (attr2Name != null) {
                                    attrDst = dstBo.getAttribute(attr2Name);
                                }

                                Element TD1Dst = dom.createElement("td");

                                //TD1Dst.appendChild(dom.createTextNode( attrDst.getDefAttribute().getLabel() ) );
                                //TD1Dst.setAttribute("class","labelDst");
                                TD1Dst.setAttribute("width", "10%");

                                Element TD2Dst = dom.createElement("td");

                                //TD2Dst.appendChild( getValueAttribute( dom , dstBo, attrDst ) );
                                setValueAttribute(null, TD2Dst, true, dom,
                                    dstBo, srcBo, attrDst, diffElem2,
                                    diffContainer, "inputDst");
                                TD2Dst.setAttribute("width", "40%");

                                //TD2Dst.setAttribute("class","inputDst");
                                TR.appendChild(TD1Dst);
                                TR.appendChild(TD2Dst);
                            }
                        } else {
                            Element TD = dom.createElement("td");
                            TD.setAttribute("class", "label");
                            TD.setAttribute("width", "50%");
                            TD.setAttribute("colspan", "2");
                            TR.appendChild(TD);
                        }
                    }
                }
            }
        }
    }

    private static boolean setValueAttribute(Element td1, Element td2,
        boolean difference, XMLDocument dom, boObject srcBo, boObject dstBo,
        AttributeHandler attHandler, DifferenceElement de,
        DifferenceContainer diffContainer, String labelClass)
        throws SQLException, boRuntimeException {
        if (attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_OBJECTATTRIBUTE) {
            if (attHandler.getDefAttribute().getRelationType() == attHandler.getDefAttribute().RELATION_1_TO_1) {
                if (td1 != null) {
                    td1.setAttribute("class",
                        ((de == null) && (dstBo != null)) ? "label" : "labelSrc");
                }

                td2.setAttribute("class",
                    ((de == null) && (dstBo != null)) ? "input" : labelClass);
                td2.appendChild(getValueAttribute(dom, srcBo, attHandler, null));

                return false;
            } else if (attHandler.getDefAttribute().getRelationType() == attHandler.getDefAttribute().RELATION_MULTI_VALUES) {
                setMultiValues(td1, td2, dom, difference, de, attHandler,
                    dstBo.getAttribute(attHandler.getName()), labelClass);

                return true;
            } else {
                Element TDaux;
                Element TABLE = dom.createElement("TABLE");
                TABLE.setAttribute("cellpadding", "0pt");
                TABLE.setAttribute("cellspacing", "0pt");
                TABLE.setAttribute("class", "gridTD");

                Element TBODY = dom.createElement("TBODY");
                Element TR = dom.createElement("TR");
                DifferenceElement diffElem = null;
                String value = null;
                bridgeHandler bHandler = srcBo.getBridge(attHandler.getName());
                bHandler.beforeFirst();

                bridgeHandler bridgeDst = null;
                bridgeDst = dstBo.getBridge(attHandler.getName());
                bridgeDst.beforeFirst();

                long size = bHandler.getRecordCount();
                boolean markNoExistence = false;
                boolean exists = false;

                while (bHandler.next()) {
                    boObject objHandler = bHandler.getObject();

                    if (!difference) {
                        diffElem = diffContainer.getBridgeDiffElement(objHandler.getName(),
                                attHandler.getName(), objHandler.getBoui(),
                                false);
                    }

                    exists = bridgeDst.haveBoui(objHandler.getBoui());

                    if (!exists) {
                        markNoExistence = true;
                    }

                    if (!difference || (!exists && difference)) {
                        value = objHandler.getCARDID(false).toString();
                        TDaux = dom.createElement("TD");
                        TDaux.setAttribute("nowrap", "");
                        TDaux.setAttribute("class",
                            (exists) ? "input" : labelClass);
                        TDaux.appendChild(dom.createCDATASection((value != null)
                                ? value : "NBSP"));
                        TR.appendChild(TDaux);
                        size--;

                        if (size == 0) {
                            TDaux.setAttribute("width", "100%");
                        }
                    }
                }

                TBODY.appendChild(TR);
                TABLE.appendChild(TBODY);

                if (bHandler.getRecordCount() > 0) {
                    if (td1 != null) {
                        td1.setAttribute("class",
                            (!markNoExistence) ? "label" : "labelSrc");
                    }

                    td2.appendChild(TABLE);
                } else if ((!difference && (bHandler.getRecordCount() == 0)) ||
                        ((bridgeDst.getRecordCount() > 0) && difference)) {
                    if (td1 != null) {
                        td1.setAttribute("class",
                            (bridgeDst.getRecordCount() == 0) ? "label"
                                                              : "labelSrc");
                    }

                    td2.setAttribute("class",
                        (de == null) ? "input" : labelClass);
                    td2.appendChild(dom.createTextNode("NBSP"));
                }

                return true;
            }
        } else if (attHandler.getDefAttribute().getAtributeType() == attHandler.getDefAttribute().TYPE_ATTRIBUTE) {
            if (td1 != null) {
                td1.setAttribute("class",
                    ((de == null) && (dstBo != null)) ? "label" : "labelSrc");
            }

            td2.setAttribute("class",
                ((de == null) && (dstBo != null)) ? "input" : labelClass);
            td2.appendChild(getValueAttribute(dom, srcBo, attHandler, null));

            return false;
        }

        if (td1 != null) {
            td1.setAttribute("class", (de == null) ? "label" : "labelSrc");
        }

        td2.setAttribute("class", (de == null) ? "input" : labelClass);
        td2.appendChild(dom.createTextNode("NBSP"));

        return false;
    }

    private static void setMultiValues(Element td1, Element td2,
        XMLDocument dom, boolean difference, DifferenceElement diffElem,
        AttributeHandler srcAttHandler, AttributeHandler dstAttHandler,
        String labelClass) throws boRuntimeException {
        boObject srcBo = null;
        boObject dstBo = null;
        boObject[] srcAttBoObjects = srcAttHandler.getObjects();
        boObject[] dstAttBoObjects = dstAttHandler.getObjects();
        long boui;

        if ((srcAttBoObjects != null) && (dstAttBoObjects != null)) {
            String value = null;
            boolean markNoExistence = false;
            Element TDaux;
            Element TABLE = dom.createElement("TABLE");
            TABLE.setAttribute("cellpadding", "0pt");
            TABLE.setAttribute("cellspacing", "0pt");
            TABLE.setAttribute("class", "gridTD");

            Element TBODY = dom.createElement("TBODY");
            Element TR = dom.createElement("TR");

            for (int i = 0; i < srcAttBoObjects.length; i++) {
                TDaux = dom.createElement("TD");
                TDaux.setAttribute("nowrap", "");
                srcBo = srcAttBoObjects[i];
                value = srcBo.getCARDID(false).toString();
                boui = srcBo.getBoui();

                if (DifferenceHelper.existMultiValue(dstAttBoObjects, boui)) {
                    //if(!difference)TDaux.setAttribute("class","input");
                    TDaux.setAttribute("class", "input");
                } else {
                    markNoExistence = true;
                    TDaux.setAttribute("class", labelClass);
                }

                TDaux.appendChild(dom.createCDATASection((value != null)
                        ? value : "NBSP"));
                TR.appendChild(TDaux);

                if (i == (srcAttBoObjects.length - 1)) {
                    TDaux.setAttribute("width", "100%");
                }
            }

            TBODY.appendChild(TR);
            TABLE.appendChild(TBODY);

            if (srcAttBoObjects.length > 0) {
                if (td1 != null) {
                    boolean moreMultiValues = false;

                    for (int i = 0; i < dstAttBoObjects.length; i++) {
                        dstBo = dstAttBoObjects[i];
                        boui = dstBo.getBoui();

                        if (!DifferenceHelper.existMultiValue(srcAttBoObjects,
                                    boui)) {
                            moreMultiValues = true;
                        }
                    }

                    td1.setAttribute("class",
                        (markNoExistence || moreMultiValues) ? "labelSrc"
                                                             : "label");
                }

                td2.appendChild(TABLE);
            } else if ((!difference && (srcAttBoObjects.length == 0)) ||
                    ((dstAttBoObjects.length > 0) && difference)) {
                td2.setAttribute("class",
                    (diffElem == null) ? "input" : labelClass);
                td2.appendChild(dom.createTextNode("NBSP"));
            }
        } else if ((srcAttBoObjects == null) && (dstAttBoObjects == null) &&
                !difference) {
            td2.setAttribute("class", "input");
            td2.appendChild(dom.createTextNode("NBSP"));
        } else if ((srcAttBoObjects != null) && (dstAttBoObjects == null) &&
                !difference) {
            String value = null;
            Element TDaux;
            Element TABLE = dom.createElement("TABLE");
            TABLE.setAttribute("cellpadding", "0pt");
            TABLE.setAttribute("cellspacing", "0pt");
            TABLE.setAttribute("class", "gridTD");

            Element TBODY = dom.createElement("TBODY");
            Element TR = dom.createElement("TR");

            for (int i = 0; i < srcAttBoObjects.length; i++) {
                TDaux = dom.createElement("TD");
                TDaux.setAttribute("nowrap", "");
                srcBo = srcAttBoObjects[i];
                value = srcBo.getCARDID(false).toString();
                TDaux.setAttribute("class", labelClass);
                TDaux.appendChild(dom.createCDATASection((value != null)
                        ? value : "NBSP"));
                TR.appendChild(TDaux);

                if (i == (srcAttBoObjects.length - 1)) {
                    TDaux.setAttribute("width", "100%");
                }
            }

            TBODY.appendChild(TR);
            TABLE.appendChild(TBODY);

            if (srcAttBoObjects.length > 0) {
                if (td1 != null) {
                    td1.setAttribute("class", "label");
                }

                td2.appendChild(TABLE);
            }
        } else if ((srcAttBoObjects == null) && (dstAttBoObjects != null) &&
                !difference) {
            if (td1 != null) {
                td1.setAttribute("class", "labelSrc");
            }

            td2.setAttribute("class", "inputSrc");
            td2.appendChild(dom.createTextNode("NBSP"));
        } else if ((srcAttBoObjects != null) && (dstAttBoObjects == null) &&
                difference) {
            String value = null;
            Element TDaux;
            Element TABLE = dom.createElement("TABLE");
            TABLE.setAttribute("cellpadding", "0pt");
            TABLE.setAttribute("cellspacing", "0pt");
            TABLE.setAttribute("class", "gridTD");

            Element TBODY = dom.createElement("TBODY");
            Element TR = dom.createElement("TR");

            for (int i = 0; i < srcAttBoObjects.length; i++) {
                TDaux = dom.createElement("TD");
                TDaux.setAttribute("nowrap", "");
                srcBo = srcAttBoObjects[i];
                value = srcBo.getCARDID(false).toString();
                TDaux.setAttribute("class", labelClass);
                TDaux.appendChild(dom.createCDATASection((value != null)
                        ? value : "NBSP"));
                TR.appendChild(TDaux);

                if (i == (srcAttBoObjects.length - 1)) {
                    TDaux.setAttribute("width", "100%");
                }
            }

            TBODY.appendChild(TR);
            TABLE.appendChild(TBODY);
            td2.appendChild(TABLE);
        }
    }

    private static void buildDifferenceResume(docHTML doc,
        DifferenceContainer diffContainer, boObject srcBo, boObject dstBo,
        XMLDocument dom, Node node, EboContext boctxDst)
        throws SQLException, boRuntimeException {
        // Attribute Resume
        buildAttributeResume(diffContainer, srcBo, dstBo, dom, node);

        // Bridge Resume
        buildBridgeResume(diffContainer, srcBo, dstBo, dom, node);

        // Childs
        buildChildsResume(doc, diffContainer, dom, node, boctxDst);
    }

    private static void buildChildsResume(docHTML doc,
        DifferenceContainer diffContainer, XMLDocument dom, Node node,
        EboContext boctxDst) throws SQLException, boRuntimeException {
        boObject srcBo = null;
        boObject dstBo = null;
        DifferenceContainer dContChild = null;

        for (Iterator objects = diffContainer.getDiffChildsIterator();
                objects.hasNext();) {
            dContChild = (DifferenceContainer) objects.next();
            srcBo = doc.getObject(dContChild.getBouiSrc());
            dstBo = boObject.getBoManager().loadObject(boctxDst,
                    dContChild.getBouiDst());
            buildDifferenceResume(doc, dContChild, srcBo, dstBo, dom, node,
                boctxDst);
        }
    }

    private static void buildAttributeResume(
        DifferenceContainer diffContainer, boObject srcBo, boObject dstBo,
        XMLDocument dom, Node node) throws SQLException, boRuntimeException {
        Element table = dom.createElement("table");
        table.setAttribute("class", "section");
        node.appendChild(table);

        Element TRx = dom.createElement("tr");
        table.appendChild(TRx);

        Element TDx = dom.createElement("td");
        TDx.setAttribute("width", "10%");
        TRx.appendChild(TDx);
        TDx = dom.createElement("td");
        TDx.setAttribute("width", "40%");
        TRx.appendChild(TDx);
        TDx = dom.createElement("td");
        TDx.setAttribute("width", "10%");
        TRx.appendChild(TDx);
        TDx = dom.createElement("td");
        TDx.setAttribute("width", "40%");
        TRx.appendChild(TDx);

        DifferenceElement diffElem = null;
        Iterator att = diffContainer.getAttributeDiffIterator();

        while (att.hasNext()) {
            diffElem = (DifferenceElement) att.next();

            if ("".equals(diffElem.getBridgeName()) ||
                    (diffElem.getBridgeName() == null)) {
                Element TR = dom.createElement("tr");
                table.appendChild(TR);

                Element TD1 = dom.createElement("td");
                TD1.appendChild(dom.createTextNode(srcBo.getAttribute(
                            diffElem.getAttributeName()).getDefAttribute()
                                                        .getLabel()));
                TD1.setAttribute("class", "label");
                TD1.setAttribute("width", "10%");

                Element TD2 = dom.createElement("td");
                TD2.setAttribute("width", "90%");
                TD2.setAttribute("colspan", "3");
                TD2.appendChild(getValueAttribute(dom, srcBo,
                        srcBo.getAttribute(diffElem.getAttributeName()), null));
                TD2.setAttribute("class", "inputSrc");
                TR.appendChild(TD1);
                TR.appendChild(TD2);

                Element TRDst = dom.createElement("tr");
                table.appendChild(TRDst);

                Element TD1Dst = dom.createElement("td");
                TD1Dst.setAttribute("width", "10%");

                Element TD2Dst = dom.createElement("td");
                TD2Dst.setAttribute("width", "90%");
                TD2Dst.setAttribute("colspan", "3");
                TD2Dst.appendChild(getValueAttribute(dom, srcBo,
                        dstBo.getAttribute(diffElem.getAttributeName()), null));
                TD2Dst.setAttribute("class", "inputDst");
                TRDst.appendChild(TD1Dst);
                TRDst.appendChild(TD2Dst);
            }
        }

        att = diffContainer.getBridgeDiffIterator();

        String mvName = null;
        boolean haveLabel = false;

        while (att.hasNext()) {
            diffElem = (DifferenceElement) att.next();

            if (diffElem.isMultiValue()) {
                Element TR = dom.createElement("tr");

                if (mvName == null) {
                    mvName = diffElem.getAttributeName();
                }

                if (!mvName.equals(diffElem.getAttributeName())) {
                    mvName = diffElem.getAttributeName();
                    haveLabel = false;
                } else if (!haveLabel) {
                    table.appendChild(TR);

                    Element TD1 = dom.createElement("td");
                    TD1.appendChild(dom.createTextNode(
                            srcBo.getAttribute(diffElem.getAttributeName())
                                 .getDefAttribute().getLabel()));
                    TD1.setAttribute("class", "label");
                    TD1.setAttribute("width", "10%");
                    TR.appendChild(TD1);
                    haveLabel = true;
                } else {
                    table.appendChild(TR);

                    Element TD1 = dom.createElement("td");
                    TD1.setAttribute("class", "label");
                    TD1.setAttribute("width", "10%");
                    TR.appendChild(TD1);
                }

                Element TD2 = dom.createElement("td");

                if (diffElem.getSrcValue() != null) {
                    TD2.setAttribute("width", "90%");
                    TD2.setAttribute("colspan", "3");

                    //attr.getObject().getCARDID().toString();
                    TD2.appendChild(dom.createCDATASection(
                            ((boObject) diffElem.getSrcValue()).getCARDID(false)
                             .toString()));

                    //TD2.appendChild(getValueAttribute(dom,srcBo.getAttribute(diffElem.getAttributeName())));
                    TD2.setAttribute("class", "inputSrc");
                    TR.appendChild(TD2);
                }

                if (diffElem.getDstValue() != null) {
                    TD2.setAttribute("width", "90%");
                    TD2.setAttribute("colspan", "3");
                    TD2.appendChild(dom.createCDATASection(
                            ((boObject) diffElem.getDstValue()).getCARDID(false)
                             .toString()));

                    //TD2Dst.appendChild(dom.createTextNode(diffElem.getDstValue().toString() ) );
                    //TD2Dst.appendChild(getValueAttribute(dom,dstBo.getAttribute(diffElem.getAttributeName())));
                    TD2.setAttribute("class", "inputDst");
                    TR.appendChild(TD2);
                }
            }
        }
    }

    private static void buildBridgeResume(DifferenceContainer diffContainer,
        boObject srcBo, boObject dstBo, XMLDocument dom, Node node)
        throws SQLException, boRuntimeException {
        bridgeHandler bHandler = null;
        Iterator att = null;
        DifferenceElement diffElem = null;
        boBridgesArray bridgesArray = srcBo.getBridges();
        Enumeration oEnum = bridgesArray.elements();

        while (oEnum.hasMoreElements()) {
            boolean labelOn = false;
            bHandler = (bridgeHandler) oEnum.nextElement();

            Element table = dom.createElement("table");

            // Atributos
            att = diffContainer.getAttributeDiffIterator();

            while (att.hasNext()) {
                diffElem = (DifferenceElement) att.next();

                if ((!"".equals(diffElem.getBridgeName()) ||
                        (diffElem.getBridgeName() != null)) &&
                        bHandler.getName().equals(diffElem.getBridgeName())) {
                    if (!labelOn) {
                        labelOn = true;

                        Element area = dom.createElement("div");
                        area.setAttribute("class", "area_title");

                        Node node1 = node.appendChild(area);
                        Element tit = dom.createElement("p");
                        node1.appendChild(tit);
                        tit.appendChild(dom.createTextNode(
                                bHandler.getDefAttribute().getLabel()));

                        table = dom.createElement("table");
                        table.setAttribute("class", "section");
                        node.appendChild(table);

                        Element TRx = dom.createElement("tr");
                        table.appendChild(TRx);

                        Element TDx = dom.createElement("td");
                        TDx.setAttribute("width", "10%");
                        TRx.appendChild(TDx);
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width", "40%");
                        TRx.appendChild(TDx);
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width", "10%");
                        TRx.appendChild(TDx);
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width", "40%");
                        TRx.appendChild(TDx);
                    }

                    Element TR = dom.createElement("tr");
                    table.appendChild(TR);

                    Element TD1 = dom.createElement("td");
                    TD1.appendChild(dom.createTextNode(
                            srcBo.getAttribute(diffElem.getAttributeName())
                                 .getDefAttribute().getLabel()));
                    TD1.setAttribute("class", "label");
                    TD1.setAttribute("width", "10%");

                    Element TD2 = dom.createElement("td");
                    TD2.setAttribute("width", "90%");
                    TD2.setAttribute("colspan", "2");

                    //TD2.appendChild(dom.createTextNode(diffElem.getSrcValue().toString() ) );
                    TD2.appendChild(getValueAttribute(dom, srcBo,
                            srcBo.getAttribute(diffElem.getAttributeName()),
                            null));
                    TD2.setAttribute("class", "inputSrc");
                    TR.appendChild(TD1);
                    TR.appendChild(TD2);

                    Element TRDst = dom.createElement("tr");
                    table.appendChild(TRDst);

                    Element TD1Dst = dom.createElement("td");
                    TD1Dst.setAttribute("width", "10%");

                    Element TD2Dst = dom.createElement("td");
                    TD2Dst.setAttribute("width", "90%");
                    TD2Dst.setAttribute("colspan", "3");

                    //TD2Dst.appendChild(dom.createTextNode(diffElem.getDstValue().toString() ) );
                    TD2Dst.appendChild(getValueAttribute(dom, dstBo,
                            dstBo.getAttribute(diffElem.getAttributeName()),
                            null));
                    TD2Dst.setAttribute("class", "inputDst");
                    TRDst.appendChild(TD1Dst);
                    TRDst.appendChild(TD2Dst);
                }
            }

            // Bridges Antigas
            Iterator bridge = diffContainer.getBridgeSrcDiffIterator();

            while (bridge.hasNext()) {
                diffElem = (DifferenceElement) bridge.next();

                if ((!"".equals(diffElem.getBridgeName()) ||
                        (diffElem.getBridgeName() != null)) &&
                        bHandler.getName().equals(diffElem.getBridgeName())) {
                    if (!labelOn) {
                        labelOn = true;

                        Element area = dom.createElement("div");
                        area.setAttribute("class", "area_title");

                        Node node1 = node.appendChild(area);
                        Element tit = dom.createElement("p");
                        node1.appendChild(tit);
                        tit.appendChild(dom.createTextNode(
                                bHandler.getDefAttribute().getLabel()));

                        table = dom.createElement("table");
                        table.setAttribute("class", "section");
                        node.appendChild(table);

                        Element TRx = dom.createElement("tr");
                        table.appendChild(TRx);

                        Element TDx = dom.createElement("td");
                        TDx.setAttribute("width", "10%");
                        TRx.appendChild(TDx);
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width", "40%");
                        TRx.appendChild(TDx);
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width", "10%");
                        TRx.appendChild(TDx);
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width", "40%");
                        TRx.appendChild(TDx);
                    }

                    Element TR = dom.createElement("tr");
                    table.appendChild(TR);

                    Element TD2 = dom.createElement("td");
                    TD2.setAttribute("width", "100%");
                    TD2.setAttribute("colspan", "4");
                    TD2.appendChild(dom.createCDATASection(
                            srcBo.getObject(diffElem.getBoui()).getCARDID(false)
                                 .toString()));
                    TD2.setAttribute("class", "inputSrc");
                    TR.appendChild(TD2);
                }
            }

            // Bridges Novas
            bridge = diffContainer.getBridgeDstDiffIterator();

            while (bridge.hasNext()) {
                diffElem = (DifferenceElement) bridge.next();

                if ((!"".equals(diffElem.getBridgeName()) ||
                        (diffElem.getBridgeName() != null)) &&
                        bHandler.getName().equals(diffElem.getBridgeName())) {
                    if (!labelOn) {
                        labelOn = true;

                        Element area = dom.createElement("div");
                        area.setAttribute("class", "area_title");

                        Node node1 = node.appendChild(area);
                        Element tit = dom.createElement("p");
                        node1.appendChild(tit);
                        tit.appendChild(dom.createTextNode(
                                bHandler.getDefAttribute().getLabel()));

                        table = dom.createElement("table");
                        table.setAttribute("class", "section");
                        node.appendChild(table);

                        Element TRx = dom.createElement("tr");
                        table.appendChild(TRx);

                        Element TDx = dom.createElement("td");
                        TDx.setAttribute("width", "10%");
                        TRx.appendChild(TDx);
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width", "40%");
                        TRx.appendChild(TDx);
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width", "10%");
                        TRx.appendChild(TDx);
                        TDx = dom.createElement("td");
                        TDx.setAttribute("width", "40%");
                        TRx.appendChild(TDx);
                    }

                    Element TR = dom.createElement("tr");
                    table.appendChild(TR);

                    Element TD2 = dom.createElement("td");
                    TD2.setAttribute("width", "100%");
                    TD2.setAttribute("colspan", "4");
                    TD2.appendChild(dom.createCDATASection(
                            srcBo.getObject(diffElem.getBoui()).getCARDID(false)
                                 .toString()));
                    TD2.setAttribute("class", "inputDst");
                    TR.appendChild(TD2);
                }
            }
        }
    }

    private static boolean isReportBodyNHeaderFirstSon(Node node) {
        if ((node != null) && "div".equalsIgnoreCase(node.getNodeName())) {
            NamedNodeMap divAttr = node.getAttributes();
            Node n = divAttr.getNamedItem("class");

            if (n != null) {
                String aux = n.getNodeValue();

                if ("reportBodyNHeader".equals(n.getNodeValue()) &&
                        (node.getChildNodes().getLength() == 0)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static String getValue(boObject parent, AttributeHandler attr,
        StringBuffer sb) throws boRuntimeException {
        if ("boolean".equalsIgnoreCase(attr.getDefAttribute().getType())) {
            String value = attr.getValueString();

            if ("0".equals(value)) {
                //falta verificar a lingua
                return CARD_NAO;
            } else if ("1".equals(value)) {
                return CARD_SIM;
            }

            return value;
        } else if ((attr.getDefAttribute().getLOVName() != null) &&
                !"".equals(attr.getDefAttribute().getLOVName())) {
            String xlov = attr.getDefAttribute().getLOVName();
            String value = attr.getValueString();

            if ((value != null) && !"".equals(value)) {
                lovObject lovObj = LovManager.getLovObject(attr.getParent().getEboContext(), xlov);
                if(lovObj != null)
                {
                    lovObj.beforeFirst();
                    while(lovObj.next())
                    {
                        if(value.equalsIgnoreCase(lovObj.getCode()))
                        {
                            return lovObj.getDescription();
                        }
                    }
                }
            }

            return attr.getValueString();
        } else if ("dateTime".equalsIgnoreCase(attr.getDefAttribute().getType())) {
            Date d = null;

            if ((d = attr.getValueDate()) != null) {
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "dd-MM-yyyy HH:mm:ss");

                return formatter.format(d);
            }

            return "";
        } else if ("date".equalsIgnoreCase(attr.getDefAttribute().getType())) {
            Date d = null;

            if ((d = attr.getValueDate()) != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                return formatter.format(d);
            }

            return "";
        } else if ("operator".equals(attr.getName()) &&
                (attr.getParent() != null) &&
                "Ebo_FilterQuery".equals(attr.getParent().getName())) {
            return getOperatorValue(attr.getValueString());
        } else if ("joinQuery".equals(attr.getName()) &&
                (attr.getParent() != null) &&
                "Ebo_FilterQuery".equals(attr.getParent().getName())) {
            return getJoinValue(attr.getValueString());
        } else if ("attributeName".equals(attr.getName()) &&
                (attr.getParent() != null) &&
                "Ebo_FilterQuery".equals(attr.getParent().getName())) {
            return getAttributeDescription(parent, attr);
        } else if ("value".equals(attr.getName()) &&
                (attr.getParent() != null) &&
                "Ebo_FilterQuery".equals(attr.getParent().getName())) {
            StringBuffer v = new StringBuffer();

            if (getAttributeValue(parent, attr, v)) {
                sb.append("Y");
            }

            return v.toString();
        } else {
            NumberFormat currencyFormatter = NumberFormat.getInstance();

            if (attr.getDefAttribute().getDecimals() != 0) {
                //currency
                currencyFormatter.setParseIntegerOnly(false);

                if (CARD_Y.equalsIgnoreCase(attr.getDefAttribute().getGrouping())) {
                    currencyFormatter.setGroupingUsed(true);
                }

                currencyFormatter.setMaximumFractionDigits(attr.getDefAttribute()
                                                               .getDecimals());
                currencyFormatter.setMinimumFractionDigits(attr.getDefAttribute()
                                                               .getMinDecimals());
                currencyFormatter.setMinimumIntegerDigits(1);

                return currencyFormatter.format(attr.getValueDouble());
            } else if (CARD_Y.equalsIgnoreCase(attr.getDefAttribute()
                                                       .getGrouping())) {
                currencyFormatter.setParseIntegerOnly(false);
                currencyFormatter.setMinimumIntegerDigits(1);
                currencyFormatter.setGroupingUsed(true);

                return currencyFormatter.format(attr.getValueDouble());
            }

            if ((attr.getValueString() != null) &&
                    "".equals(attr.getValueString().trim())) {
                return "";
            }

            if(attr.getDefAttribute().getValueType() != boDefAttribute.VALUE_CLOB)
            {
                return treatSpecialCharacters(attr.getValueString(), sb);
            }
            return attr.getValueString();
        }
    }

    private static String treatSpecialCharacters(String strValue, StringBuffer sb)
    {
        if(strValue.indexOf("\n") >= 0)
        {
            sb.append("Y");
            return strValue.replaceAll("\n", "<br/>");
        }
        return strValue;
    }
    private static String getFrameParameters(boDefHandler p_bodef, boObject o,
        String param, String value) throws boRuntimeException {
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
        boObject object = o;
        AttributeHandler atrObj = null;

        while (st.hasMoreTokens()) {
            token = st.nextToken();
            atr = p_bodef.getAttributeRef(token);

            if (ntoken == 1) {
                if ("this".equals(token)) {
                    sbcopy.append(object.getBoui());
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
                    sb.append(token);

                    if (atr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                        object = object.getAttribute(token).getObject();
                    } else {
                        atrObj = object.getAttribute(token);
                        atrib = 1;
                    }

                    obj = 1;
                    flag = false;
                    size--;
                }

                if (size == 0) {
                    if (atr != null) {
                        if (atr.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                            if (object != null) {
                                sbcopy.append(object.getBoui());
                            } else {
                                sbcopy.append("\"\"");
                            }
                        } else if (atr.getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE) {
                            if (atrObj != null) {
                                if (atrib == 0) {
                                    sbcopy.append(object.getAttribute(token)
                                                        .getValueString());
                                } else {
                                    sbcopy.append(atrObj.getValueString());
                                }
                            } else {
                                sbcopy.append("\"\"");
                            }
                        }
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

    private static boolean getAttributeValue(boObject eboFilter,
        AttributeHandler attr, StringBuffer value) {
        try {
            boObject eboClsreg = eboFilter.getAttribute("masterObjectClass")
                                          .getObject();
            String objName = eboClsreg.getAttribute("name").getValueString();
            boDefHandler bodef = boDefHandler.getBoDefinition(objName);
            String attName = attr.getParent().getAttribute("attributeName")
                                 .getValueString();
            boDefAttribute attDef = bodef.getAttributeRef(attName);

            if (attDef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) {
                String s = attr.getValueString();
                long l = 0;

                try {
                    l = Long.parseLong(s);

                    boObject obj = boObject.getBoManager().loadObject(eboFilter.getEboContext(),
                            l);

                    if (obj != null) {
                        value.append(obj.getCARDID(false).toString());

                        return true;
                    }

                    value.append("");

                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (attDef.getAtributeType() == boDefAttribute.TYPE_STATEATTRIBUTE) {
                boDefClsState xsta = (boDefClsState) attDef;
                boDefClsState[] xstaChilds = xsta.getChildStates();
                String xvalue = attr.getValueString();
                String[] xvals = xvalue.split(";");
                List l = Arrays.asList(xvals);

                if ((xvalue != null) && !"".equals(xvalue)) {
                    for (int i = 0; i < xstaChilds.length; i++) {
                        if (l.contains(String.valueOf(
                                        xstaChilds[i].getNumericForm()))) {
                            value.append(xstaChilds[i].getLabel());
                            value.append(";");
                        }
                    }
                }

                return false;
            }

            if ((attDef.getLOVName() != null) &&
                    !"".equals(attDef.getLOVName())) {
                String xlov = attDef.getLOVName();
                String xvalue = attr.getValueString();
                String[] xvals = xvalue.split(";");
                List l = Arrays.asList(xvals);

                if ((xvalue != null) && !"".equals(xvalue)) {
                    lovObject lovObj = LovManager.getLovObject(attr.getParent().getEboContext(), xlov);
                    if(lovObj != null)
                    {
                        lovObj.beforeFirst();
                        ArrayList founded = new ArrayList();
                        while(lovObj.next())
                        {
                            if (l.contains(lovObj.getCode()))
                            {
                                founded.add(lovObj.getCode());
                                value.append(lovObj.getDescription());
                                value.append(";");
                            }
                        }
                        if (l.size() > founded.size()) {
                            for (int i = 0; i < l.size(); i++) {
                                if (founded.contains((String) l.get(i))) {
                                    value.append((String) l.get(i));
                                    value.append(";");
                                }
                            }
                        }

                        return false;
                    }

                    value.append(attr.getValueString());

                    return false;
                } else {
                    value.append("");

                    return false;
                }
            }

            NumberFormat currencyFormatter = NumberFormat.getInstance();

            if (attr.getDefAttribute().getDecimals() != 0) {
                //currency
                currencyFormatter.setParseIntegerOnly(false);

                if (CARD_Y.equalsIgnoreCase(attr.getDefAttribute().getGrouping())) {
                    currencyFormatter.setGroupingUsed(true);
                }

                currencyFormatter.setMaximumFractionDigits(attr.getDefAttribute()
                                                               .getDecimals());
                currencyFormatter.setMinimumFractionDigits(attr.getDefAttribute()
                                                               .getMinDecimals());
                currencyFormatter.setMinimumIntegerDigits(1);
                value.append(currencyFormatter.format(attr.getValueDouble()));

                return false;
            } else if (CARD_Y.equalsIgnoreCase(attr.getDefAttribute()
                                                       .getGrouping())) {
                currencyFormatter.setParseIntegerOnly(false);
                currencyFormatter.setMinimumIntegerDigits(1);
                currencyFormatter.setGroupingUsed(true);
                value.append(currencyFormatter.format(attr.getValueDouble()));

                return false;
            }

            if ((attr.getValueString() != null) &&
                    "".equals(attr.getValueString().trim())) {
                value.append("");

                return false;
            }

            value.append(attr.getValueString());

            return false;
        } catch (Exception e) {
        }

        return false;
    }

    private static String getAttributeDescription(boObject eboFilter,
        AttributeHandler att) {
        try {
            String attName = att.getValueString();
            boObject eboClsreg = eboFilter.getAttribute("masterObjectClass")
                                          .getObject();
            String objName = eboClsreg.getAttribute("name").getValueString();
            boDefHandler bodef = boDefHandler.getBoDefinition(objName);

            return bodef.getAttributeRef(attName).getDescription();
        } catch (Exception e) {
        }

        return "";
    }

    private static String getOperatorValue(String operator) {
        if ("EQUAL".equals(operator)) {
            return "Igual a";
        } else if ("NOTEQUAL".equals(operator)) {
            return "diferente de";
        } else if ("START".equals(operator)) {
            return "a começar por";
        } else if ("FINISH".equals(operator)) {
            return "a acabar em";
        } else if ("IN".equals(operator)) {
            return "contendo";
        } else if ("NOTIN".equals(operator)) {
            return "não contem";
        } else if ("GREATER".equals(operator)) {
            return "maior que";
        } else if ("GREATER_EQUAL".equals(operator)) {
            return "maior ou igual que";
        } else if ("LESSER".equals(operator)) {
            return "menor que";
        } else if ("LESSER_EQUAL".equals(operator)) {
            return "menor ou igual que";
        } else if ("ISNOTNULL".equals(operator)) {
            return "contem dados";
        } else if ("ISNULL".equals(operator)) {
            return "não contem dados";
        }

        return (operator == null) ? "" : operator;
    }

    private static String getJoinValue(String join) {
        if ("EMPTY".equals(join)) {
            return "";
        } else if ("AND".equals(join)) {
            return "E";
        } else if ("OR".equals(join)) {
            return "OU";
        } else if ("LPAR".equals(join)) {
            return "(";
        } else if ("RPAR".equals(join)) {
            return ")";
        } else if ("ELPAR".equals(join)) {
            return "E(";
        } else if ("ERPAR".equals(join)) {
            return ")E";
        } else if ("EBPAR".equals(join)) {
            return ")E(";
        } else if ("OLPAR".equals(join)) {
            return "OU(";
        } else if ("ORPAR".equals(join)) {
            return ")OU";
        } else if ("OBPAR".equals(join)) {
            return ")OU(";
        } else if ("null".equals(join)) {
            return "";
        }

        return (join == null) ? "" : join;
    }

    private static final CDATASection getValueTag( XMLDocument dom, String className, EboContext ctx, docHTML doc, boObject obj, AttributeHandler att )
    {
        CharArrayWriter cw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter( cw );
        try
        {
            ((ICustomField)Class.forName( className ).newInstance()).render( ctx, null, doc, obj, pw, att );
        }
        catch (Exception e)
        {

        }
        pw.close();
        cw.close();
        return dom.createCDATASection( cw.toString() );
    }
}
