/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import java.util.ArrayList;

import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.Browser;
import netgest.bo.presentation.render.HTMLCommon;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.MessageViewer;
import netgest.bo.presentation.render.ie.scripts.ImportBuilder;
import netgest.bo.presentation.render.ie.scripts.ScriptBuilder;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.security.securityOPL;

import netgest.utils.ClassUtils;

import netgest.bo.system.Logger;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class MessageViewerBuilder {
    private static Logger logger = Logger.getLogger("netgest.bo.presentation.render.ie.components.MessageViewerBuilder");
    
    private static final char[] SCRIPT = "<script src=\"xeo.js\"></script>".toCharArray();
    private static final char[] FUNCTION = "function showHideMsgText(msgId){if(document.getElementById(msgId).style.display != 'none'){document.getElementById('span_'+msgId).innerHTML='Ver Texto';document.getElementById(msgId).style.display='none';}else{document.getElementById('span_'+msgId).innerHTML='Esconder Texto';document.getElementById(msgId).style.display='';}}".toCharArray();
    private static final char[] REPORT_EMPTY = "<html></html>".toCharArray();
    private static final char[] PAGE_BEGIN_1 = "<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n".toCharArray();
    private static final char[] PAGE_BEGIN_2 = "\n<title>Forum de Mensagens</title>\n</head>\n".toCharArray();
    private static final char[] DIV_AREASEL = "<div class=\"areaSEL\">\n".toCharArray();
    private static final char[] DIV_REFRESH = "<div align=\"right\" style=\"width:100%\"  >\n".toCharArray();
    private static final char[] REFRESH = "\n[<a style='color:green' href='javascript:document.location.reload()'>Actualizar</a>]&nbsp;&nbsp;&nbsp;&nbsp;\n".toCharArray();
    private static final char[] EMPTY_DIV_ALIGNRIGHT = "\n<div align=\"right\" style=\"width:100%\"  ></div>\n".toCharArray();
    private static final char[] STARTING_DIV_INFO = "\n<div style=\"border:1px solid #6283A3;\" >\n".toCharArray();
    private static final char[] OBJ_LABEL = ("\nvar objLabel=\"<img style='cursor:hand' hspace='3' border='0' align='absmiddle' class='lui' title='Mensagens' src='" + Browser.getThemeDir() + "msgPost/message.gif' width='16' height='16'/><span title='Mensagens'>Mensagens</span>\";\n").toCharArray();
    private static final char[] OBJ_STATUS = "\nvar objStatus=\"&nbsp;ESTADO : <b>Em edição</b> ( ".toCharArray();
    private static final char[] OBJ_STATUS_1 = ") Criado por :".toCharArray();
    private static final char[] TABLE_1 = "\n<table class=\"toolAreaGrid\" width=\"100%\"  cellpadding=\"5\" cellspacing=\"0\">\n".toCharArray();
    private static final char[] TD_50WD =  "\n<td width=\"50px\">\n".toCharArray();
    private static final char[] CREATOR =  "\n<b>Criador:</b>\n".toCharArray();
    private static final char[] TD_COLSPAN =  "\n<td colspan=\"2\">\n".toCharArray();
    private static final char[] DT_CREATION =  "Dt. Criação:".toCharArray();
    private static final char[] ASSUNTO=  "\n<b>Assunto:</b>\n".toCharArray();
    private static final char[] DT_SAIDA_ENTRADA=  "\n<b>Data Saída/Entrada:</b>\n".toCharArray();
    private static final char[] PGR_NAME=  "\n<b>Programa:</b>\n".toCharArray();
    private static final char[] CRIAR_MSG= ("\n<td align=\"right\"><span class='lui' onclick=\"parent.setActionCode('sendMessage');parent.setStateActivity('');parent.boForm.BindValues(true);\"><img align=\"absmiddle\" src=\"" + Browser.getThemeDir() + "msgPost/message.gif\" />&nbsp;<span title='Enviar mensagem'>Enviar mensagem</span></span></td>\n").toCharArray();
    private static final char[] SEPARATOR= "\n<td  colspan=\"4\" style=\"border-top:1px solid #6283A3;border-right:1px solid #6283A3\" >\n".toCharArray();
    private static final char[] TD_TOP = "\n<td valign=\"TOP\">\n".toCharArray();
    private static final char[] TH_INF_REL = "\n<th>Inf.Relacionada</th>\n".toCharArray();
    private static final char[] TH_DOC = "\n<th>Documentos</th>\n".toCharArray();
    private static final char[] DIV_MSGS = "\n<div stylea=\"border-left:1px solid #6283A3;border-right:1px solid #6283A3\" >\n".toCharArray();
    private static final char[] TABLE_MSGS = "\n<table bgcolor=\"White\" width=\"100%\" class=\"gh_std\"  cellpadding=\"0\" cellspacing=\"0\">\n".toCharArray();
    private static final char[] TD_COLHEADER = "\n<td align=\"left\" class=\"colHeader\" style=\"border-left:1px solid #6283A3;border-bottom:1px solid #6283A3;border-top:1px solid #6283A3\" valign=\"top\">\n".toCharArray();
    private static final char[] TABLE_MSGS_LINE1 = "\n<table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">\n".toCharArray();
    private static final char[] TD_WIDTH_TOP = "\n<td width=\"85%\" valign=\"TOP\" height=\"100%\" style=\"border-right:1px solid #6283A3;border-bottom:1px solid #6283A3;border-top:1px solid #6283A3\">\n".toCharArray();
    private static final char[] TABLE_MSGS_LINE2 = "\n<table cellpadding=\"2\" cellspacing=\"0\" width=\"100%\" height=\"100%\">\n".toCharArray(); 
    private static final char[] MSG_FROM = "Mensagem enviada por ".toCharArray();
    private static final char[] TD_WHITE = "\n<td bgcolor=\"White\" style=\"border-top:1px solid #6283A3;border-left:1px solid #6283A3\" colspan=\"2\">\n".toCharArray();
    private static final char[] END = "\n<td style='areaSEL' height=\"10px\"><div class='areaSEL' style='width:100%;height:10px;overflow:hidden'></td>".toCharArray();
    private static final char[] TO=  "\n<b>Para:</b>\n".toCharArray();
    private static final char[] CC=  "\n<b>CC:</b>\n".toCharArray();
    private static final char[] BCC=  "\n<b>BCC:</b>\n".toCharArray();
    private static final char[] INF_REL=  "\n<b>Inf. Relacionada:</b>\n".toCharArray();
    private static final char[] DOCUMENTOS=  "\n<b>Documentos:</b>\n".toCharArray();
    private static final char[] CONTEUDO=  "\n<b>Conteúdo:</b>\n".toCharArray();
    private static final char[] RESPONDER = "\n<span class='lui' onclick=\"parent.setActionCode('reply[".toCharArray();
    private static final char[] RESPONDER_1 = ("]');parent.setStateActivity('');parent.boForm.BindValues();\"><img title='Responder' align=\"absmiddle\" src=\"" + Browser.getThemeDir() + "msgPost/reply.gif\" /></span>\n").toCharArray();
    private static final char[] RESPONDER_TODOS = "\n<span class='lui' onclick=\"parent.setActionCode('replyAll[".toCharArray();
    private static final char[] RESPONDER_TODOS_1 = ("]');parent.setStateActivity('');parent.boForm.BindValues();\"><img title='Responder a Todos' align=\"absmiddle\" src=\"" + Browser.getThemeDir() + "msgPost/replyall.gif\" /></span>\n").toCharArray();
    private static final char[] REENCAMINHAR = "\n<span class='lui' onclick=\"parent.setActionCode('forward[".toCharArray();
    private static final char[] REENCAMINHAR_1 = ("]');parent.setStateActivity('');parent.boForm.BindValues();\"><img title='Reencaminhar' align=\"absmiddle\" src=\"" + Browser.getThemeDir() + "msgPost/forward.gif\" /></span>\n").toCharArray();
    private static final char[] SENT_MSG = "Envio".toCharArray();
    private static final char[] RCV_MSG = "Recepção".toCharArray();
    private static final char[] SENT_RCV_MSG = "Envio e Recepção".toCharArray();
    private static final char[] TO_HIDE_BEGIN = "<tr style=\"display:none\" id=\"".toCharArray();
    private static final char[] TO_HIDE_END = "\">".toCharArray();
    
    private static final String SQL_CR = "select xwfCreateReceivedMessage where program = ";
    private static final String SQL_R = "select xwfActivityReceive where program = ";
    private static final String SQL_ASSIGNED = " and ( performer = CTX_PERFORMER_BOUI or (   (  assignedQueue in ( select iXEOUser.queues where boui=CTX_PERFORMER_BOUI) or assignedQueue in ( select iXEOUser.groups where boui=CTX_PERFORMER_BOUI) or assignedQueue in ( select iXEOUser.roles where boui=CTX_PERFORMER_BOUI) or assignedQueue=CTX_PERFORMER_BOUI )) )";    
    private static final String SQL_S = "select xwfActivitySend where program = ";
    private static final String SQL_1 = " and message.value.valueObject = ";    

    private static final int[] MV_SCRIPT = {
        ScriptBuilder.SC_JS_XEO 
    };

    private static final int[] MV_IMPORT={
        ImportBuilder.IMP_CSS_XEO_NEW
    };
    
    /**
     *
     * @Company Enlace3
     * @since
     */
    public MessageViewerBuilder() {
    }

    public static void writeMessageViewer(PrintWriter out, MessageViewer mv,
        docHTML doc, docHTML_controler docList, PageController control) throws IOException, boRuntimeException {
        boolean received = false;
        boolean sent = false;

        if(mv == null || mv.getProgramBoui() <= 0)
        {
            out.write(REPORT_EMPTY);
        }
        else
        {
            EboContext ctx = doc.getEboContext();
            boObject program = boObject.getBoManager().loadObject(ctx, mv.getProgramBoui());
            boObject auxObj = null;
            
            out.write(PAGE_BEGIN_1);
            out.write(PAGE_BEGIN_2);
            
            ScriptBuilder.writeScript(out, MV_SCRIPT, control);
            if (ImportBuilder.canWrite(MV_IMPORT, control)) 
            {
                out.write(HTMLCommon.HTML_STYLE_TEXT_CSS_BEGIN);
                out.write(HTMLCommon.UTIL_NEW_LINE);
                ImportBuilder.writeImport(out, MV_IMPORT, control);
                out.write(HTMLCommon.HTML_STYLE_END);
                out.write(HTMLCommon.UTIL_NEW_LINE);
            }
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(doc.getController().getPresentation().writeJS().toCharArray());
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(HTMLCommon.HTML_BODY_BEGIN);
            out.write(DIV_AREASEL);
            out.write(DIV_REFRESH);
            out.write(REFRESH);
            out.write(HTMLCommon.HTML_DIV_END);
            out.write(EMPTY_DIV_ALIGNRIGHT);
            out.write(STARTING_DIV_INFO);
            out.write(HTMLCommon.HTML_SCRIPT_BEGIN);
            out.write(FUNCTION);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(OBJ_LABEL);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(OBJ_STATUS);
            StringBuffer sb = new StringBuffer();
            if(program.getAttribute("CREATOR").getObject() != null)
            {
                sb.append(program.getAttribute("CREATOR").getObject().getCARDID());
            }
            else
            {
                sb.append(" Sistema");
            }
            sb.append(" Em ");
            if(program.getAttribute("SYS_DTCREATE").getValueDate() == null)
            {
                sb.append(" Sistema".toCharArray());
            }
            else
            {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
                sb.append(sdf.format(program.getAttribute("SYS_DTCREATE").getValueDate()));
            }
            sb.append("\";");
            out.write(sb.toString().toCharArray());
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(HTMLCommon.HTML_SCRIPT_END);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            
            out.write(TABLE_1);
            out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
            out.write(TD_50WD);
            out.write(CREATOR);
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
            if(program.getAttribute("CREATOR").getObject() == null)
            {
                out.write(" Sistema".toCharArray());
            }
            else
            {
                out.write(program.getAttribute("CREATOR").getObject().getCARDID().toString().toCharArray());
            }
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(TD_COLSPAN);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            out.write(HTMLCommon.HTML_BOLD_BEGIN);
            out.write(DT_CREATION);
            out.write(HTMLCommon.HTML_BOLD_BEGIN);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            if(program.getAttribute("SYS_DTCREATE").getValueDate() == null)
            {
                out.write(" Sistema".toCharArray());
            }
            else
            {
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                out.write(sdf2.format(program.getAttribute("SYS_DTCREATE").getValueDate()).toCharArray());
            }
            
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(HTMLCommon.HTML_TABLE_LINE_END);
            out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
            out.write(TD_50WD);
            out.write(PGR_NAME);
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(TD_COLSPAN);
            out.write(program.getAttribute("label").getValueString().toCharArray());
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(CRIAR_MSG);
            out.write(HTMLCommon.HTML_TABLE_LINE_END);
            out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
            out.write(SEPARATOR);
            out.write(HTMLCommon.HTML_TABLE_BEGIN_NOT_CLOSE);
            out.write(HTMLCommon.WORD_WIDTH);
            out.write(HTMLCommon.SYMBOL_EQUAL);
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write("100%".toCharArray());
            out.write(HTMLCommon.SYMBOL_QUOTE);
            out.write(HTMLCommon.SYMBOL_GT);
            out.write(HTMLCommon.UTIL_NEW_LINE);
            
            out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
            out.write(TD_WHITE);
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(HTMLCommon.HTML_TABLE_LINE_END);
            out.write(HTMLCommon.HTML_TABLE_END);
            out.write(HTMLCommon.HTML_DIV_END);
            out.write(HTMLCommon.HTML_BR);
            
            //mensagens            
            boObject msg, msgRel;            
            out.write(DIV_MSGS);
            
            bridgeHandler bhPrgMsg = program.getBridge("message");
            ArrayList r = getMasterMessagesOrdered(bhPrgMsg);
            for (int i = 0; i < r.size(); i++) 
            {
                writeMsgBox(out, program, (boObject)r.get(i), 0);
                verifyMessages(out, program, (boObject)r.get(i), bhPrgMsg, 0);
            }


            out.write(HTMLCommon.HTML_DIV_END);
            out.write(DIV_REFRESH);
            out.write(REFRESH);
            out.write(HTMLCommon.HTML_DIV_END);
                
            out.write(HTMLCommon.HTML_BODY_END);
            out.write(HTMLCommon.HTML_END);
        }        
    }
    
    private static ArrayList getMasterMessagesOrdered(bridgeHandler bhPrgMsg) throws boRuntimeException
    {
        boBridgeIterator messageIt = new boBridgeIterator(bhPrgMsg, "dtdoc");
        ArrayList toRet = new ArrayList();
        boObjectList actvReceivedList = null;
        boObjectList actvSentList = null;
        messageIt.beforeFirst();
        boObject msg;
        while(messageIt.next())
        {
            msg = messageIt.currentRow().getObject();

            if(securityOPL.canRead(msg))
            {
                if(msg.getAttribute("responseTo").getValueObject() == null &&
                    msg.getAttribute("forwardFrom").getValueObject() == null
                )
                {
                    toRet.add(msg);
                }
            }
        }
        return toRet;
    }
    
    private static ArrayList getRelatedMessagesOrdered(boObject masterMsg, bridgeHandler bhPrgMsg) throws boRuntimeException
    {
        ArrayList toRet = new ArrayList();
        boObject msgRel;
        boBridgeIterator messageRelIt =  new boBridgeIterator(bhPrgMsg, "dtdoc");
        messageRelIt.beforeFirst();
        while(messageRelIt.next())
        {
            msgRel = messageRelIt.currentRow().getObject();
            if(securityOPL.canRead(msgRel) && (msgRel.getAttribute("responseTo").getValueObject() != null ||
                msgRel.getAttribute("forwardFrom").getValueObject() != null)
                )
            {
                long msgBoui = masterMsg.getBoui();
                long msgRelBoui = msgRel.getAttribute("responseTo").getObject() != null ? 
                                  msgRel.getAttribute("responseTo").getObject().getBoui():
                                  msgRel.getAttribute("forwardFrom").getObject().getBoui();
                if(msgBoui == msgRelBoui)
                {
                    toRet.add(msgRel);
                }
            }
        }
        return toRet;
    }
    
    private static void verifyMessages(PrintWriter out, boObject program, boObject msg, bridgeHandler msgBh, int level) throws IOException, boRuntimeException
    {
    
        ArrayList r = getRelatedMessagesOrdered(msg, msgBh);
        for (int i = 0; i < r.size(); i++) 
        {
            writeMsgBox(out, program, (boObject)r.get(i), level + 1);
            verifyMessages(out, program, (boObject)r.get(i), msgBh, level + 1);
        }
    }
    
    private static void writeMsgBox(PrintWriter out, boObject program, boObject msg, int level) throws IOException, boRuntimeException
    {
        boolean first;
        boolean empty;
        
        StringBuffer sb;
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        boolean received = false, sent = false;

        boObjectList actvReceivedList = boObjectList.list(msg.getEboContext(), SQL_R + program.getBoui() + SQL_1 + msg.getBoui() + SQL_ASSIGNED);
        boObjectList actvCreateReceivedList = boObjectList.list(msg.getEboContext(), SQL_CR + program.getBoui() + SQL_1 + msg.getBoui() + SQL_ASSIGNED);
        boObjectList actvSentList = boObjectList.list(msg.getEboContext(), SQL_S + program.getBoui() + SQL_1 + msg.getBoui() + SQL_ASSIGNED);

        long boi = -1;
        if(actvReceivedList.getRecordCount() > 0)
        {
            actvReceivedList.beforeFirst();
            actvReceivedList.next();
            boi = actvReceivedList.getObject().getBoui();
            received = true;
        }
        
        if(!received && actvCreateReceivedList.getRecordCount() > 0)
        {
            actvCreateReceivedList.beforeFirst();
            actvCreateReceivedList.next();
            boi = actvCreateReceivedList.getObject().getBoui();
            received = true;
        }

        if(actvSentList.getRecordCount() > 0)
        {
            actvSentList.beforeFirst();
            actvSentList.next();
            boi = actvSentList.getObject().getBoui();
            sent = true;
        }
        
        out.write(TABLE_MSGS);
        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
        for (int i = 0; i < level; i++) 
        {
            out.write("<td bgcolor=\"#F2F7FA\" width='15px' ></td>".toCharArray());
        }
        out.write(TD_COLHEADER);
        out.write(TABLE_MSGS_LINE1);
        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
        if(sent && received)
        {
            out.write(SENT_RCV_MSG);
        }
        else if (sent)
        {
            out.write(SENT_MSG);
        }
        else
        {
            out.write(RCV_MSG);
        }

        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        out.write(HTMLCommon.HTML_TABLE_LINE_END);
        //boui
//        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
//        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
//        out.write(String.valueOf(msg.getBoui()).getBytes());
//        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
//        out.write(HTMLCommon.HTML_TABLE_LINE_END);
        
        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
        out.write(DT_CREATION);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        out.write(HTMLCommon.HTML_TABLE_LINE_END);
        
        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN); 
        if(program.getAttribute("SYS_DTCREATE").getValueDate() == null)
            out.write(" Sistema".toCharArray());
        else
            out.write(sdf2.format(msg.getAttribute("SYS_DTCREATE").getValueDate()).toCharArray());
        
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        out.write(HTMLCommon.HTML_TABLE_LINE_END);
        
        out.write(HTMLCommon.HTML_TABLE_END);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        
        out.write(TD_WIDTH_TOP);
        out.write(TABLE_MSGS_LINE2);

        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN);
        
        out.write(MSG_FROM);
        StringBuffer from = new StringBuffer();
        if(msg.getAttribute("from") != null)
        {
            if(msg.getAttribute("from").getObject().getAttribute("refObj") != null)
            {
                from = msg.getAttribute("from").getObject().getAttribute("refObj").getObject().getCARDIDwLink();
            }
            else
            {
                String name = msg.getAttribute("from").getObject().getAttribute("name").getValueString(); 
                
                if(msg.getAttribute("from").getObject().getAttribute("lastname").getValueString() != null)
                {
                    name = name + " " + 
                            msg.getAttribute("from").getObject().getAttribute("lastname").getValueString();
                }
                from.append(name);
            }            
        }
        else
        {
            from.append(" Desconhecido ");
        }
        out.write(from.toString().toCharArray());
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_BEGIN_NOT_CLOSE);
        out.write(HTMLCommon.WORD_ALIGN);
        out.write(HTMLCommon.SYMBOL_EQUAL);
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write("right".toCharArray());
        out.write(HTMLCommon.SYMBOL_QUOTE);
        out.write(HTMLCommon.SYMBOL_GT);
        if(received && !"messageSystem".equals(msg.getBoDefinition().getName()) &&
            !"messageSystem".equals(msg.getBoDefinition().getBoSuperBo()))
        {
            //reply
            out.write(RESPONDER);
            out.write(String.valueOf(boi).toCharArray());
            out.write(RESPONDER_1);
            
            //replyAll
            out.write(RESPONDER_TODOS);
            out.write(String.valueOf(boi).toCharArray());
            out.write(RESPONDER_TODOS_1);
        }
        
        //FORWARD
        out.write(REENCAMINHAR);
        out.write(String.valueOf(boi).toCharArray());
        out.write(REENCAMINHAR_1);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        
        
        out.write(HTMLCommon.HTML_TABLE_LINE_END);
        
        //Data de Saída/Entrada do email
        if(msg.getAttribute("dtdoc").getValueDate() != null)
        {
            out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
            out.write(TD_COLSPAN);
            out.write(DT_SAIDA_ENTRADA);
            out.write(
                sdf2.format(msg.getAttribute("dtdoc").getValueDate()).toCharArray()
                );
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(HTMLCommon.HTML_TABLE_LINE_END);
        }

        //Assunto
        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
        out.write(TD_COLSPAN);
        out.write(ASSUNTO);
        if(msg.getAttribute("name").getValueString() != null)
        {
            out.write(msg.getAttribute("name").getValueString().toCharArray());
        }
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        out.write(HTMLCommon.HTML_TABLE_LINE_END);
        
        //to
        first = true;
        empty = true;
        boBridgeIterator deliverIt = msg.getBridge("to").iterator();
        deliverIt.beforeFirst();
        StringBuffer deliverSb;
        while(deliverIt.next())
        {
            if(first)
            {
                empty = false;
                out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
                out.write(TD_COLSPAN);
                out.write(TO);
            }
            if(deliverIt.currentRow().getObject().getAttribute("refObj") != null)
            {
                sb = deliverIt.currentRow().getObject().getAttribute("refObj").getObject().getCARDIDwLink();
            }
            else
            {
                sb = new StringBuffer();
                String name = deliverIt.currentRow().getObject().getAttribute("name").getValueString();
                if(deliverIt.currentRow().getObject().getAttribute("lastname").getValueString() != null)
                {
                    name = name + " " +
                            deliverIt.currentRow().getObject().getAttribute("lastname").getValueString();
                }
                sb.append(name);
                
            }
            out.write(sb.toString().toCharArray());
            out.write(";".toCharArray());
            first = false;
            //documento com conteúdo;
            
        }
        if(!empty)
        {
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(HTMLCommon.HTML_TABLE_LINE_END);
        }
        
        //cc
        first = true;
        empty = true;
        deliverIt = msg.getBridge("cc").iterator();
        deliverIt.beforeFirst();
        while(deliverIt.next())
        {
            if(first)
            {
                empty = false;
                out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
                out.write(TD_COLSPAN);
                out.write(CC);
            }
            if(deliverIt.currentRow().getObject().getAttribute("refObj") != null)
            {
                sb = deliverIt.currentRow().getObject().getAttribute("refObj").getObject().getCARDIDwLink();                
            }
            else
            {
                sb = new StringBuffer();
                String name = deliverIt.currentRow().getObject().getAttribute("name").getValueString();
                if(deliverIt.currentRow().getObject().getAttribute("lastname").getValueString() != null)
                {
                    name = name + " " +
                            deliverIt.currentRow().getObject().getAttribute("lastname").getValueString();
                }
                sb.append(name);
            }
            out.write(sb.toString().toCharArray());
            out.write(";".toCharArray());
            first = false;
        }
        if(!empty)
        {
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(HTMLCommon.HTML_TABLE_LINE_END);
        }
        
        //bcc
        first = true;
        empty = true;
        deliverIt = msg.getBridge("bcc").iterator();
        deliverIt.beforeFirst();
        while(deliverIt.next())
        {
            if(first)
            {
                empty = false;
                out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
                out.write(TD_COLSPAN);
                out.write(BCC);
            }
            if(deliverIt.currentRow().getObject().getAttribute("refObj") != null)
            {
                sb = deliverIt.currentRow().getObject().getAttribute("refObj").getObject().getCARDIDwLink();                        
            }
            else
            {
                sb = new StringBuffer();
                sb.append(deliverIt.currentRow().getObject().getAttribute("name").getValueString());
                if(deliverIt.currentRow().getObject().getAttribute("lastname").getValueString() != null)
                {
                    sb.append(" ")
                      .append(deliverIt.currentRow().getObject().getAttribute("lastname").getValueString());
                }
            }
            out.write(sb.toString().toCharArray());
            out.write(";".toCharArray());
            first = false;
        }
        if(!empty)
        {
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(HTMLCommon.HTML_TABLE_LINE_END);
        }
        
        //inf. relacionada
        first = true;
        empty = true;
        if(msg.getAttribute("attachedObjects") != null)
        {            
            bridgeHandler docBh = msg.getBridge("attachedObjects");
            boBridgeIterator bit = docBh.iterator();
            bit.beforeFirst();
            while(bit.next())
            {
                if(first)
                {
                    empty = false;
                    out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
                    out.write(TD_COLSPAN);
                    out.write(INF_REL);
                }
                out.write(bit.currentRow().getObject().getCARDIDwLink().toString().toCharArray());
                out.write(";".toCharArray());
                first = false;
            }
        }
        if(!empty)
        {
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(HTMLCommon.HTML_TABLE_LINE_END);
        }
        
        //documentos
        first = true;
        empty = true;
        if(msg.getAttribute("documents") != null)
        {
            bridgeHandler docBh = msg.getBridge("documents");
            boBridgeIterator bit = docBh.iterator();
            bit.beforeFirst();
            while(bit.next())
            {
                if(first)
                {
                    empty = false;
                    out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
                    out.write(TD_COLSPAN);
                    out.write(DOCUMENTOS);
                }
                out.write(bit.currentRow().getObject().getCARDIDwLink().toString().toCharArray());
                out.write(";".toCharArray());
                first = false;
            }
        }
//        ArrayList generatedDoc = MessageUtils.getGeneratedDoc(msg);
//        for (int i = 0; i < generatedDoc.size() ; i++) 
//        {
//            out.write(((boObject)generatedDoc.get(i)).getCARDIDwLink().toString().toCharArray());
//            out.write(";".toCharArray());  
//        }
        if(!empty)
        {
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(HTMLCommon.HTML_TABLE_LINE_END);
        }
        
        //conteudo binaryDocuments
        
        first = true;
        empty = true;
        if(msg.getAttribute("binaryDocuments") != null)
        {
            bridgeHandler docBh = msg.getBridge("binaryDocuments");
            boBridgeIterator bit = docBh.iterator();
            boObject o;
            bit.beforeFirst();
            while(bit.next())
            {
                if(first)
                {
                    empty = false;
                    out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
                    out.write(TD_COLSPAN);
                    out.write(CONTEUDO);
                }
                o = bit.currentRow().getObject();
                if(o.getAttribute("srcObj").getValueObject() != null)
                {
                    if(o.getAttribute("srcObj").getObject().getAttribute("refObj").getValueObject() != null)
                    {
                        out.write((
                            o.getAttribute("srcObj").getObject().getAttribute("refObj")
                            .getObject().getCARDIDwLink().toString()
                            ).toCharArray());
                    }
//                    else
//                    {
//                        out.write("Desconhecido".toCharArray());
//                    }
                }
                out.write("[".toCharArray());
                out.write((o.getCARDIDwLink().toString()).toCharArray());
                out.write("]".toCharArray());
                out.write(";".toCharArray());
                first = false;
            }
        }
        if(!empty)
        {
            out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
            out.write(HTMLCommon.HTML_TABLE_LINE_END);
        }
        
        //ver texto
        out.write(HTMLCommon.HTML_TABLE_LINE_BEGIN);
        out.write(TD_COLSPAN);
        String msgText=msg.getAttribute("description").getValueString();
        if( msgText != null && !"".equals( msgText ) )
        {
            out.write(("<span class='lui' id='span_msg_"+msg.getBoui()+"' onclick=\"showHideMsgText('msg_"+msg.getBoui()+"')\">Ver Texto</span>").toCharArray());
        }
        else
        {
            out.write(("<b>[Sem Texto]</b>").toCharArray());
        }
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        out.write(HTMLCommon.HTML_TABLE_LINE_END);
        
        
        //texto
        out.write(TO_HIDE_BEGIN);
        out.write(("msg_" + msg.getBoui()).toCharArray());
        out.write(TO_HIDE_END);
        out.write(TD_WHITE);
        
        if( msgText != null)
        {
          String aa= "s"+msg.getAttribute("description").hashCode();  
          out.write("<iframe style='width:100%;height:200px' onload='this.contentWindow.document.body.innerHTML=".toCharArray() );
          out.write(aa.toCharArray() );
          out.write(".innerHTML' src='about:blank'></iframe>".toCharArray() );
          out.write("<xml id=".toCharArray() );
          out.write(aa.toCharArray());
          out.write(">".toCharArray());
          out.write( ClassUtils.removeHtmlGarbage(msgText).toCharArray() );
          out.write("</xml>".toCharArray());
        }
        
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        out.write(HTMLCommon.HTML_TABLE_LINE_END);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        out.write(HTMLCommon.HTML_TABLE_LINE_END);
        out.write(HTMLCommon.HTML_TABLE_END);
        out.write(HTMLCommon.HTML_TABLE_COLUMN_END);
        out.write(HTMLCommon.HTML_TABLE_LINE_END);
                
        out.write(END);
        out.write(END);
        for (int i = 0; i < level; i++) 
        {
            out.write(END);
        }
        out.write(HTMLCommon.HTML_TABLE_END);
    }
}
