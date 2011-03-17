/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge.gestemp.presentation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import netgest.bo.controller.Controller;
import netgest.bo.def.boDefAttribute;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_renderFields;
import netgest.bo.impl.document.merge.gestemp.GesClfTipoDocumento;
import netgest.bo.impl.document.merge.gestemp.Segmento;
import netgest.bo.impl.document.merge.gestemp.validation.Classificacao;
import netgest.bo.impl.document.merge.gestemp.validation.Contexto;
import netgest.bo.impl.document.merge.gestemp.validation.JavaExecuter;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.presentation.render.elements.ExplorerServer;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.utils.DataUtils;

import org.apache.log4j.Logger;

/**
 * <p>Title: GtTemplateViewer </p>
 * <p>Description: Classe responsável por apresentar os parâmetros de entrada para geração do template</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Francisco Câmara
 * @version 1.0
 */
public class GesDocViewer
{
    private static final String GROUP_SEQ_KEY = "GES_DOC_CLF_GROUPSEQ";
    private static Hashtable gesDoc = null;
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.document.merge.gestemp.presentation.GesDocViewer");
    
    //docType
    private long classBoui = -1;
    
    private String nome = null;
    
    //document
    private long docBoui = -1;
    
    //group
    private long group = -1;
    
    //ArrayList
    private ArrayList groupClassifications = new ArrayList();
    
    //validação
    private String validacao = null;
    
    //classification htmlID
    private static final String HTMLID = "docType";
    
    private boolean editing = false;
    
    private static final String TEMPL_SQL = "select GESDocClf where activo=1 and tipoClf <> '1' and ( share in ( select iXEOUser.queues where boui=CTX_PERFORMER_BOUI) or  share in ( select iXEOUser.groups where boui=CTX_PERFORMER_BOUI) or share in ( select iXEOUser.roles where boui=CTX_PERFORMER_BOUI) or share =CTX_PERFORMER_BOUI ) order by name";
    
    //classification groupSeq
    private String groupSequence = null;
    
    //classification groupSeq
    private long docRuntBoui = -1;
    
    public GesDocViewer(long boui)
    {
        docRuntBoui = boui;
    }
    
    public static GesDocViewer gesDocByBoui(docHTML doc,  long boui)
    {
        GesDocViewer ret;
        if(gesDoc == null)
        {
            gesDoc = new Hashtable();
        }
        ret = (GesDocViewer)gesDoc.get(String.valueOf(doc.getDocIdx()));
        if( ret == null)
        {
            ret = new GesDocViewer(boui);
            gesDoc.put(String.valueOf(doc.getDocIdx()), ret);
        }
        return ret;
    }
    
    public void setEditing( boolean isInEdit )
    {
        this.editing = isInEdit;
    }
    
    public static void releaseAll(EboContext boctx, int docIDX)
    {
         if(gesDoc != null)
        {
            gesDoc.remove(String.valueOf(docIDX));
        }
    }

    public void setClassBoui(EboContext boctx, long docType) throws boRuntimeException
    {
        clear();
        classBoui = docType;
        boObject clf = boObject.getBoManager().loadObject(boctx, docType);
        nome = clf.getAttribute("internalName").getValueString();
        validacao = clf.getAttribute("validacao").getValueString();
        setGroup(boctx);
    }
    
    public void setDocRuntBoui(long runBoui) throws boRuntimeException
    {
        docRuntBoui = runBoui;
    }
    
    public void setClassBoui(EboContext boctx, String docType) throws boRuntimeException
    {
        if(docType != null && !"".equals(docType.trim()))
        {
            try
            {
                clear();
                classBoui = Long.parseLong(docType);
                boObject clf = boObject.getBoManager().loadObject(boctx, classBoui);
                nome = clf.getAttribute("internalName").getValueString();
                validacao = clf.getAttribute("validacao").getValueString();
            }
            catch (Exception e)
            {
                logger.fatal(e);
            }
        }
        else
        {
            classBoui = -1;
            nome = null;
            validacao = null;
        }
        setGroup(boctx);
    }
    
    public long getClassBoui()
    {
        return classBoui;
    }

    public void setDocument(long doc)
    {
        docBoui = doc;
    }
    
    public long getDocument()
    {
        return docBoui;
    }
    
    public void setGroupSequence(String groupSeq)
    {
        this.groupSequence = groupSeq;
    }
    
    public String getGroupSequence()
    {
        return groupSequence;
    }
    
    public boolean isEditing()
    {
        return editing;
    }

    public void setDocument(String docStrBoui)
    {
        if(docStrBoui != null && !"".equals(docStrBoui.trim()))
        {
            try
            {
                docBoui = Long.parseLong(docStrBoui);
            }
            catch (Exception e)
            {
                logger.fatal(e);
            }
        }
        else
        {
            docBoui = -1;
        }
    }
    public static String writeJS() throws boRuntimeException
    {
        StringBuffer result = new StringBuffer();
        result.append("<script LANGUAGE=\"javascript\" SRC=\"xeo.js\"></script>")
        .append("<script LANGUAGE=\"javascript\" SRC=\"xeo.js\"></script>");
        
        return result.toString();
    }
    public void setClassification(docHTML doc, EboContext boctx) throws boRuntimeException
    {
        //documento
        boObject document = boObject.getBoManager().loadObject(boctx, docBoui);
        Object aux = null; 
        
        //novo
        if(!isEditing())
        {
            setGroupSequence(newGroupSequence(doc.getEboContext()));
        }
        
        
        for (int i = 0; i < groupClassifications.size(); i++) 
        {
            aux = groupClassifications.get(i);
            if(aux instanceof GesDocObject)
            {
                ((GesDocObject)aux).setClassification(this, document);
            }
            else if(aux instanceof GesDocText)
            {
                ((GesDocText)aux).setClassification(this, document);
            }
            else if(aux instanceof GesDocDate)
            {
                ((GesDocDate)aux).setClassification(this, document);
            }
            else if(aux instanceof GesDocLov)
            {
                ((GesDocLov)aux).setClassification(this, document);
            }
            else if(aux instanceof GesDocNumber)
            {
                ((GesDocNumber)aux).setClassification(this, document);
            }
        }
        
        //alterações do utilizador
        boObject runClass = doc.getObject(docRuntBoui);
        setRuntime(runClass);
        
        runClass.update();
        document.update();
        
        
    }
    
    public void setRuntimeRemove(EboContext boctx, long classif)  throws boRuntimeException
    {
        boObject runClass = boObject.getBoManager().createObject(boctx, "GESDocRunClf");
        runClass.getAttribute("tipo").setValueString("1");
        runClass.getAttribute("docType").setValueLong(classif);
        runClass.getAttribute("doc").setValueLong(docBoui);
        runClass.update();
    }
    
    public void setRuntime(boObject runClass)  throws boRuntimeException
    {
        if(runClass != null && classBoui > 0)
        {
            if(isEditing())
                runClass.getAttribute("tipo").setValueString("3");
            else
                runClass.getAttribute("tipo").setValueString("2");
            

            if(classBoui > 0)
            {
                runClass.getAttribute("docType").setValueLong(classBoui);
            }
            runClass.getAttribute("doc").setValueLong(docBoui);
            //respostas
            Object aux = null;
            for (int i = 0; i < groupClassifications.size(); i++) 
            {
                aux = groupClassifications.get(i);
                if(aux instanceof GesDocObject)
                {
                    ((GesDocObject)aux).setRuntime(runClass);
                }
                else if(aux instanceof GesDocText)
                {
                    ((GesDocText)aux).setRuntime(runClass);
                }
                else if(aux instanceof GesDocDate)
                {
                    ((GesDocDate)aux).setRuntime(runClass);
                }
                else if(aux instanceof GesDocLov)
                {
                    ((GesDocLov)aux).setRuntime(runClass);
                }
                else if(aux instanceof GesDocNumber)
                {
                    ((GesDocNumber)aux).setRuntime(runClass);
                }
            }
        }
    }
    
    public void setGroup(EboContext boctx) throws boRuntimeException
    {
        if(classBoui == -1)
        {
            group = -1;
            groupClassifications.clear();
        }
        else
        {
            boObject classObj = boObject.getBoManager().loadObject(boctx, classBoui);
            boObject groupObj = classObj.getAttribute("grupo").getObject();
            if(groupObj != null)
            {
                group = groupObj.getBoui();
                boBridgeIterator bit = groupObj.getBridge("classificacao").iterator();
                bit.beforeFirst();
                GesDocObj o;
                while(bit.next())
                {
                    if((o = getboObject(null, bit.currentRow().getObject())) != null )
                        groupClassifications.add(o);
                }
            }
        }

    }
    
    public long getGroup()
    {
        return group;
    }

    public static GesDocObj getboObject(GesDocViewer viewer, boObject gesdocObj) throws boRuntimeException
    {
        if(gesdocObj != null)
        {
            if("GESDocClfObject".equals(gesdocObj.getName()))
            {
                return new GesDocObject(viewer,
                    gesdocObj.getBoui(),
                    gesdocObj.getAttribute("internalName").getValueString(),
                    gesdocObj.getAttribute("name").getValueString(),
                    gesdocObj.getAttribute("objecto").getValueLong(),
                    "1".equals(gesdocObj.getAttribute("obrigatorio").getValueString()),
                    gesdocObj.getAttribute("validacao").getValueString()
                );
            }
            else if("GESDocClfText".equals(gesdocObj.getName()))
            {
                return new GesDocText(viewer,
                    gesdocObj.getBoui(),
                    gesdocObj.getAttribute("internalName").getValueString(),
                    gesdocObj.getAttribute("name").getValueString(),
                    "1".equals(gesdocObj.getAttribute("longText").getValueString()),
                    "1".equals(gesdocObj.getAttribute("obrigatorio").getValueString()),
                    gesdocObj.getAttribute("validacao").getValueString()
                );
            }
            else if("GESDocClfDate".equals(gesdocObj.getName()))
            {
                return new GesDocDate(viewer,
                    gesdocObj.getBoui(),
                    gesdocObj.getAttribute("internalName").getValueString(),
                    gesdocObj.getAttribute("name").getValueString(),
                    gesdocObj.getAttribute("dtFormat").getValueString(),
                    "1".equals(gesdocObj.getAttribute("obrigatorio").getValueString()),
                    gesdocObj.getAttribute("validacao").getValueString()
                );
            }
            else if("GESDocClfLov".equals(gesdocObj.getName()))
            {
                return new GesDocLov(viewer,
                    gesdocObj.getBoui(),
                    gesdocObj.getAttribute("internalName").getValueString(),
                    gesdocObj.getAttribute("name").getValueString(),
                    gesdocObj.getAttribute("lov").getValueLong(),
                    "1".equals(gesdocObj.getAttribute("obrigatorio").getValueString()),
                    gesdocObj.getAttribute("validacao").getValueString()
                );
            }
            else if("GESDocClfNumber".equals(gesdocObj.getName()))
            {
                return new GesDocNumber(viewer,
                    gesdocObj.getBoui(),
                    gesdocObj.getAttribute("internalName").getValueString(),
                    gesdocObj.getAttribute("name").getValueString(),
                    "1".equals(gesdocObj.getAttribute("currency").getValueString()),
                    gesdocObj.getAttribute("decimals").getValueLong(),
                    "1".equals(gesdocObj.getAttribute("obrigatorio").getValueString()),
                    gesdocObj.getAttribute("validacao").getValueString()
                );
            }
        }
        return null;
    }

    
    public void setValues(EboContext boctx)  throws boRuntimeException
    {
        for (int i = 0; i < groupClassifications.size(); i++) 
        {
            ((GesDocObj)groupClassifications.get(i)).setValue(boctx);
        }
    }
    
    public String bind(EboContext boctx, String operation)  throws boRuntimeException
    {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < groupClassifications.size(); i++) 
        {
            sb.append(
                ((GesDocObj)groupClassifications.get(i)).bindValues(null)
            );
        }
        return sb.toString();
    }
    
    public void render(Controller controller,PageContext pageContext,int idx) throws IOException,boRuntimeException
    {
        renderGroupParameters(controller,pageContext,idx, this);
    }
    
    public ArrayList getGroupClassification()
    {
        return groupClassifications;
    }
    

    public static String getFistFieldID(boObject groupClass)
    {
       return null;
    }
    
    private static void renderTableStart(StringBuffer toPrint)
    {
        toPrint.append("<br>");
        toPrint.append("<table valign=top ");
        toPrint.append(" cellSpacing='0' cellPadding='3' width='100%' ");
//        if(renderMode == 1)
//        {
//            toPrint.append("height='100%'");
//        }
        toPrint.append("><COLGROUP/><COL width='10px' /> <COL width='120' /><COL /><COL style=\"PADDING-LEFT: 5px\" width='70' /><COL /><tbody>");
        
    }
    
    private static void renderTableEnd(StringBuffer toPrint)
    {
        toPrint.append("</tbdoy></table>");
    }

    private static String getonfocus(String fieldName)
    {
        return " onfocus='setFieldWFocus(\"" + fieldName + "\")' ";
    }
    
    public static void renderAdicionarButton(StringBuffer toPrint, String label)
    {
/*        toPrint.append("<tr height=\"10px\"></tr>");
        toPrint.append("<tr></tr>");
        toPrint.append("<tr height=\"20px\">") 
        .append("<td>&nbsp;</td>") 
        .append("<td>&nbsp;</td>")
        .append("<td align=\"right\">")*/
        toPrint.append("<button tabIndex='2' style='height:18px;width:150px;' value = \"Adicionar\" onMouseDown=\"mouseDown(event, 'adicionar');\" onkeypress=\"keyPressed(event, 'adicionar');\">")
        .append(label)
        .append("</button>");
        //.append("</p></td><td></td><td></td></tr>");
        
    }
    
    private static void renderQuestion(StringBuffer toPrint, String label, boolean isRequired)  throws boRuntimeException
    {
        toPrint.append("<TD>&nbsp;</td><TD colspan=4 nowrap ");
        if(isRequired)
        {
            toPrint.append("style='FONT-WEIGHT: bold; color:#990000'");
        }
        toPrint.append(">");
        toPrint.append(label);        
        toPrint.append("</TD>");        
    }
    
    private static void renderGroupParameters(Controller controller,PageContext pageContext,int idx, GesDocViewer viewer) throws IOException, boRuntimeException
    {
        JspWriter out = pageContext.getOut();
        docHTML doc = controller.getDocHTML();
        boObject eboDoc = boObject.getBoManager().loadObject(doc.getEboContext(), viewer.getDocument());
        
        StringBuffer toPrint = new StringBuffer();
        Hashtable ht_variables = new Hashtable();
         
        long classificacao = viewer.getClassBoui();
//        boObject group = null;
//        if(viewer.getGroup() > 0)
//        {
//            group = boObject.getBoManager().loadObject(controller.getDocHTML().getEboContext(), viewer.getGroup());
//        }
        ArrayList validClass = getClassificationValues(doc, viewer, viewer.isEditing());
        StringBuffer[] apresentationStr = null; 
        StringBuffer[] valuesStr = null;
        boolean hasClass = false;
        
        if(validClass != null && validClass.size() > 0)
        {
            apresentationStr = (StringBuffer[])validClass.get(0);
            valuesStr = (StringBuffer[])validClass.get(1);
            if(apresentationStr != null && apresentationStr.length > 0)
            {
                hasClass = true;
            }
        }
        
        renderTableStart(toPrint);
        
        if(hasClass)
        {
            //primeiro a classificação
            toPrint.append("<tr>");
            renderQuestion(toPrint, Messages.getString("GesDocViewer.78"), true);
            toPrint.append("<td>"); 
            toPrint.append("<table>");
            renderClassificationsAsCombo(doc, toPrint, classificacao, viewer, apresentationStr, valuesStr);
            toPrint.append("</table>");
            toPrint.append("</td>"); 
            toPrint.append("</tr>");
             
            ArrayList toRender = viewer.getGroupClassification();
            for (int i = 0; i < toRender.size(); i++) 
            {            
                renderGroup(doc, toPrint, (GesDocObj)toRender.get(i), viewer);
            }
            if(toRender.size() > 0)
            { 
                //renderAdicionarButton(toPrint, viewer.isEditing() ? "Alterar":"Adicionar");
            }
        }
        else
        {
            //Não existem classificações
            toPrint.append("<tr>");
            renderQuestion(toPrint, Messages.getString("GesDocViewer.85"), true);
            toPrint.append("</tr>");
        }
        
        
//        if(group != null)
//        {
//            boBridgeIterator bit = group.getBridge("classificacao").iterator();
//            bit.beforeFirst();
//            boObject o;
//            while(bit.next())
//            {
//                renderGroup(doc, toPrint, bit.currentRow().getObject(), false);
//            }
//            
//            renderAdicionarButton(toPrint);
//        }
        renderTableEnd(toPrint);
        out.print(toPrint);
    }
    
    private static void renderGroup(docHTML doc, StringBuffer toPrint,GesDocObj gesdoc, GesDocViewer viewer) throws boRuntimeException
    {
        if(gesdoc != null)
        {
            gesdoc.setClfViewer( viewer );
            if(gesdoc instanceof GesDocObject)
            {
                renderGroupObjects(doc, toPrint, (GesDocObject)gesdoc);
            }
            else if(gesdoc instanceof GesDocText)
            {
                renderGroupText(doc, toPrint, (GesDocText)gesdoc);
            }
            else if(gesdoc instanceof GesDocDate)
            {
                renderGroupDate(doc, toPrint, (GesDocDate)gesdoc);
            }
            else if(gesdoc instanceof GesDocLov)
            {
                if( "tipo_documento".equals( gesdoc.internalName ) )
                {
                    renderGroupTipoDocumento( doc, toPrint, (GesDocLov)gesdoc, viewer );
                }
                else
                {
                    renderGroupLov(doc, toPrint, (GesDocLov)gesdoc);
                }
            }
            else if(gesdoc instanceof GesDocNumber)
            {
                renderGroupNumber(doc, toPrint, (GesDocNumber)gesdoc);
            }
        }
    }
    
    private static void renderGroupObjects(docHTML doc, StringBuffer toPrint,GesDocObject gesdocObj) throws boRuntimeException
    {
        String name = gesdocObj.getName();
        String fieldName = gesdocObj.getHTMLFieldName();
        boObject eboCls = boObject.getBoManager().loadObject(doc.getEboContext(), gesdocObj.getObjBoui());
        String objName = eboCls.getAttribute("name").getValueString();

        toPrint.append("<tr>");
        renderQuestion(toPrint,name, gesdocObj.getObrigatorio( doc.getEboContext() ));
        toPrint.append("<td>"); 
        toPrint.append("<table>");
        String valor = gesdocObj.getValue() == null ? "":gesdocObj.getValue();
        GtTemplateViewer.renderCommon(toPrint,fieldName,0,objName,doc,valor,false, gesdocObj.getObrigatorio(doc.getEboContext()), -1, Long.MIN_VALUE, Long.MAX_VALUE);
        toPrint.append("</table>");
        toPrint.append("</td>");
        toPrint.append("</tr>");
    }
    
    private static void renderGroupText(docHTML doc, StringBuffer toPrint,GesDocText gesdocText) throws boRuntimeException
    {
        boolean longText = gesdocText.isLong();
        String fieldName = gesdocText.getHTMLFieldName();
        boolean b = gesdocText.getObrigatorio(doc.getEboContext());
        toPrint.append("<tr>");
        renderQuestion(toPrint,gesdocText.getName(),b );
        toPrint.append("<td>");
        toPrint.append("<table>");
        String valor = gesdocText.getValue() == null ? "":gesdocText.getValue();
        GtTemplateViewer.renderCommon(
        		toPrint,
        		fieldName,
        		longText ? 7:1,
        		null,
        		doc,
        		valor,
        		false, 
        		gesdocText.getObrigatorio(doc.getEboContext()), -1, Long.MIN_VALUE, Long.MAX_VALUE);
        toPrint.append("</table>");
        toPrint.append("</td>");
        toPrint.append("</tr>");
    }
    

    private static void renderGroupTipoDocumento(docHTML doc, StringBuffer toPrint,GesDocLov gesdocText, GesDocViewer viewer ) throws boRuntimeException
    {
        String fieldName = gesdocText.getHTMLFieldName()+"_lovValue";
        boolean b = gesdocText.getObrigatorio(doc.getEboContext() );
        toPrint.append("<tr>");
        renderQuestion(toPrint,gesdocText.getName(),b );
        toPrint.append("<td>&nbsp;&nbsp;&nbsp;");
        
        String segmento = Segmento.getSegmento(doc.getEboContext(), viewer.getClassBoui());
        
        toPrint.append( GesClfTipoDocumento.getTipoDocumento( 
            doc.getEboContext(),
            fieldName,
            segmento,
            null,
            gesdocText.getValue(),
            "width:267px;"
            ) 
        );
        
        toPrint.append("</td>");
        toPrint.append("</tr>");
    }
    
    private static void renderGroupDate(docHTML doc, StringBuffer toPrint, GesDocDate gesdocDate) throws boRuntimeException
    {
        String dtFormat = gesdocDate.getDateFormate();
        String fieldName = gesdocDate.getHTMLFieldName();
        toPrint.append("<tr>");
        renderQuestion(toPrint,gesdocDate.getName(), gesdocDate.getObrigatorio(doc.getEboContext()));
        toPrint.append("<td>");
        toPrint.append("<table>");
        String valor = gesdocDate.getValue() == null ? "":gesdocDate.getValue();
        GtTemplateViewer.renderCommon(toPrint,fieldName, "1".equals(dtFormat) ? 6:4,null,doc,valor,false, gesdocDate.getObrigatorio(doc.getEboContext()), -1, Long.MIN_VALUE, Long.MAX_VALUE);
        toPrint.append("</table>");
        toPrint.append("</td>");
        toPrint.append("</tr>");
    }

    private static void renderGroupLov(docHTML doc, StringBuffer toPrint,GesDocLov gesdocLov) throws boRuntimeException
    {
        toPrint.append("<tr>");
        renderQuestion(toPrint,gesdocLov.getName(), gesdocLov.getObrigatorio(doc.getEboContext()));
        toPrint.append("<td>");
        toPrint.append("<table>");
        toPrint.append("<TD>&nbsp;</TD><TD colspan=2>");
        long lovBoui = gesdocLov.getLovBoui();
        boObject lovBoobj = doc.getObject(lovBoui);
        String lovName = lovBoobj.getAttribute("name").getValueString();
        lovObject lov_object = LovManager.getLovObject( doc.getEboContext() , lovName);
        String fieldName = gesdocLov.getHTMLFieldName();
        String valor = gesdocLov.getValue() == null ? "":gesdocLov.getValue();
        writeHTML_forCombo(
                        toPrint,
                        new StringBuffer(valor) ,
                        new StringBuffer(fieldName) ,
                        new StringBuffer(fieldName),
                        1,
                        lov_object ,
                        false,
                        false,
                        true,
                        false,
                        new StringBuffer("document.getElementById('"+fieldName+"_lovValue').value = document.getElementById('"+fieldName+"').returnValue;"),
                        gesdocLov.getObrigatorio(doc.getEboContext()),
                        true,
                        new Hashtable()
                        );
        toPrint.append("</TD>");
        toPrint.append("</table>");
        toPrint.append("</td>");
        toPrint.append("<tr>");
    }
    
    public void renderTipoDocLov(docHTML doc, StringBuffer toPrint) throws boRuntimeException
    {
        boObject eboDoc = boObject.getBoManager().loadObject(doc.getEboContext(), getDocument());
        AttributeHandler attr = eboDoc.getAttribute("tipoDoc");
        
        
        toPrint.append("<tr>");
        renderQuestion(toPrint, Messages.getString("GesDocViewer.132"), false);
        toPrint.append("<td>"); 
        toPrint.append("<table>");
        toPrint.append("<TD>&nbsp;</TD><TD colspan=2>");
        
        boDefAttribute attrDef   =   attr.getDefAttribute();
        String xlov=attrDef.getLOVName();
        lovObject lov_object = LovManager.getLovObject( doc.getEboContext() , xlov , attr.condition() );
        StringBuffer v = new StringBuffer( attr.getValueString() == null ? "":attr.getValueString() );
        String fieldName = "ebodocTipoDocumento";
        StringBuffer nameH       =   new StringBuffer(fieldName);
        
        writeHTML_forCombo(
                        toPrint,
                        v ,
                        nameH ,
                        nameH,
                        1,
                        lov_object ,
                        false,
                        false,
                        true,
                        false,
                        new StringBuffer("document.getElementById('"+fieldName+"_lovValue').value = document.getElementById('"+fieldName+"').returnValue;"),
                        false,
                        false,
                        new Hashtable()
                        );
        toPrint.append("</TD>");
        toPrint.append("</table>");
        toPrint.append("</td>"); 
        toPrint.append("</tr>");
    }
    
    
    private static ArrayList getClassificationValues(docHTML doc, GesDocViewer viewer, boolean editing) throws boRuntimeException
    {
        boObjectList list = boObjectList.list(doc.getEboContext(), TEMPL_SQL, 1, 999999999);
        ArrayList toRet = new ArrayList(2);
        
        ArrayList temp  = new ArrayList();
        
        StringBuffer[] apresentationStr = new StringBuffer[(int)list.getRecordCount()];
        StringBuffer[] valuesStr = new StringBuffer[(int)list.getRecordCount()];
        ArrayList docClassifications = getDocumentClassif(doc.getEboContext(), viewer.getDocument());
        
        boObject auxObj;
        int i = 0;
        while(list.next())
        {
            auxObj = list.getObject();
            if(editing || "1".equals(auxObj.getAttribute("vclass").getValueString()) ||
                (docClassifications == null) || docClassifications.indexOf(String.valueOf(auxObj.getBoui())) < 0)
//                !document.getBridge("classification").haveBoui(auxObj.getBoui()))
            {
                if( temp.indexOf( String.valueOf(auxObj.getBoui()) ) == -1 )
                {   
                    temp.add( String.valueOf(auxObj.getBoui()) );
                apresentationStr[i] = auxObj.getCARDIDwNoIMG();
                valuesStr[i] = new StringBuffer(String.valueOf(auxObj.getBoui()));
                i++;
            }
        }
        }
        if(i == 0)
        {
            return null;
        }
        else if(i < (int)list.getRecordCount())
        {
            apresentationStr = shrink(apresentationStr, i);
            valuesStr = shrink(valuesStr, i);
        }
        toRet.add(apresentationStr);
        toRet.add(valuesStr);
        return toRet;
    }
    
    private static StringBuffer[] shrink(StringBuffer[] arr, int lastPos) 
    {
        StringBuffer[] newValue = new StringBuffer[lastPos];
        System.arraycopy(arr, 0, newValue, 0, lastPos);
        return newValue;
    }
    
    private static void renderClassificationsAsCombo(docHTML doc, StringBuffer toPrint, long value, GesDocViewer viewer, StringBuffer[] apresentationStr, StringBuffer[] valuesStr) throws boRuntimeException
    {
        StringBuffer id = new StringBuffer(viewer.HTMLID);
        toPrint.append("<TD>&nbsp;</TD><TD colspan=2>");
        boolean disabled =  viewer.isEditing();
        docHTML_renderFields.writeHTML_forCombo(
            toPrint,
            new StringBuffer(value <=0 ? "":String.valueOf(value)),
            id ,
            id,
            1,
            apresentationStr,
            valuesStr,
            false,
            disabled,
            true,
            false,
            new StringBuffer("escolhaDocumento();"),
            false,
            true,
            new Hashtable()
        );
        toPrint.append("</TD>");
    }
    
    private static void renderGroupNumber(docHTML doc, StringBuffer toPrint,GesDocNumber gesdocNumber) throws boRuntimeException
    {
        toPrint.append("<tr>");
        renderQuestion(toPrint,gesdocNumber.getName(), gesdocNumber.getObrigatorio(doc.getEboContext()));
        toPrint.append("<td>");
        toPrint.append("<table>");
        toPrint.append("<TD>&nbsp;</td><TD colspan=2>");
        String decimals = "0";
        String grouping = "0";
        String minDecimals = "-99999999";
        
        if(gesdocNumber.isCurrency())
        {
            decimals = "2";
            minDecimals = "2";
            grouping = "1";
        }
        else if(gesdocNumber.getDecimals() > 0)
        {
            decimals = String.valueOf(gesdocNumber.getDecimals());
            minDecimals = String.valueOf(gesdocNumber.getDecimals());
        }
        String maxNumber = "99999999";
        String minNumber = "-99999999";
        String valor = gesdocNumber.getValue() == null ? "":gesdocNumber.getValue();
        docHTML_renderFields.writeHTML_forNumber(toPrint,
                                    new StringBuffer(valor),
                                    new StringBuffer(gesdocNumber.getHTMLFieldName()),
                                    new StringBuffer(gesdocNumber.getHTMLFieldName()),
                                    1,
                                    new StringBuffer(""),
                                    new StringBuffer(decimals),
                                    new StringBuffer(minDecimals),
                                    ("0".equals(grouping)) ?  false :  true,    
                                    new StringBuffer(maxNumber),
                                    new StringBuffer(minNumber),
                                    false,
                                    true,
                                    false,
                                    new StringBuffer(""),
                                    gesdocNumber.getObrigatorio(doc.getEboContext()),
                                    true,
                                    null
                                    );
        toPrint.append("</TD>");
        toPrint.append("</table>");
        toPrint.append("</td>");
        toPrint.append("</TR>");
        
    }
    
//    public static void getClassification(boBridgeIterator bit, ArrayList classObjs, ArrayList groupSeq) throws boRuntimeException
//    {
//        bit.beforeFirst();
//        boObject classObj;
//        while(bit.next())
//        {
//            classObj = bit.currentRow().getObject();
//            if("GESDocClf".equals(classObj.getName()))
//            {
//                classObjs.add(classObj);
//                groupSeq.add(bit.currentRow().getAttribute("groupSeq").getValueString());
//            }
//        }
//    }
    
    public static void getClassification(boObject doc, ArrayList classObjs, ArrayList groupSeq) throws boRuntimeException
    {
        ArrayList auxClassBouis = new ArrayList();
        getDocumentClassif(doc, auxClassBouis, groupSeq);
        if(auxClassBouis != null && auxClassBouis.size() > 0)
        {
            long auxL;
            for (int i = 0; i < auxClassBouis.size(); i++) 
            {
                auxL = Long.parseLong((String)auxClassBouis.get(i));
                classObjs.add(boObject.getBoManager().loadObject(doc.getEboContext(), auxL));
            }
        }
        
    }
    
    public static void getGroupClassification(boBridgeIterator bit, boObject classification, String groupSeq, ArrayList labels, ArrayList values) throws boRuntimeException
    {
        bit.beforeFirst();
        boObject classObj, auxDef = null, aux;
        StringBuffer sb = new StringBuffer();
        while(bit.next())
        {
            if(groupSeq.equals(bit.currentRow().getAttribute("groupSeq").getValueString()))
            {
                auxDef = bit.currentRow().getObject();
                sb.delete(0, sb.length());
                sb.append("<b>").append(auxDef.getAttribute("name").getValueString()).append(": ").append("</b>");                
                if("GESDocClfObject".equals(auxDef.getName()))
                {
                    aux = bit.currentRow().getAttribute("valueObject").getObject();
                    if(aux != null)
                    {
                        labels.add(sb.toString());
                        sb.delete(0, sb.length());
                        sb.append(aux.getCARDID().toString());
                        values.add(sb.toString());
                        sb.delete(0, sb.length());
                    }
                }
                else if("GESDocClfText".equals(auxDef.getName()))
                {
                    String value = bit.currentRow().getAttribute("valueText").getValueString();
                    if(value != null && value.length() > 0)
                    {
                        labels.add(sb.toString());
                        sb.delete(0, sb.length());
                        sb.append(bit.currentRow().getAttribute("valueText").getValueString());
                        values.add(sb.toString());
                        sb.delete(0, sb.length());
                    }
                }
                else  if("GESDocClfDate".equals(auxDef.getName()))
                {
                    Date d = bit.currentRow().getAttribute("valueDate").getValueDate();
                    if(d != null)
                    {
                        SimpleDateFormat sdf = null;
                        if("1".equals(auxDef.getAttribute("dtFormat").getValueString()))
                        {
                            sdf = new SimpleDateFormat("dd-MM-yyyy");
                        }
                        else
                        {
                            sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        }
                        labels.add(sb.toString());
                        sb.delete(0, sb.length());
                        sb.append(sdf.format(d));
                        values.add(sb.toString());
                        sb.delete(0, sb.length());
                    }
                }
                else  if("GESDocClfNumber".equals(auxDef.getName()))
                {
                    String s = bit.currentRow().getAttribute("valueNumber").getValueString();
                    if(s != null && s.length() > 0)
                    {
                        labels.add(sb.toString());
                        sb.delete(0, sb.length());
                        NumberFormat nf = NumberFormat.getInstance();
                        double d = bit.currentRow().getAttribute("valueNumber").getValueDouble();
                        boolean currency = "1".equals(auxDef.getAttribute("currency").getValueString());
                        if(currency)
                        {
                            //currency
                            nf.setParseIntegerOnly(false);
                            nf.setGroupingUsed(true);
                            nf.setMaximumFractionDigits(2);
                            nf.setMinimumFractionDigits(2);
                            nf.setMinimumIntegerDigits(1);
                            sb.append(nf.format(d) + " €");
                        }
                        else
                        {
                            int decimals = (int)auxDef.getAttribute("decimals").getValueLong();
                            decimals = decimals < 0 ? 0:decimals;
                            nf.setParseIntegerOnly(false);
                            nf.setGroupingUsed(false);
                            nf.setMaximumFractionDigits(decimals);
                            nf.setMinimumFractionDigits(decimals);
                            nf.setMinimumIntegerDigits(1);
                            sb.append(nf.format(d));
                            
                        }
                        values.add(sb.toString());
                        sb.delete(0, sb.length());
                    }
                }
                else  if("GESDocClfLov".equals(auxDef.getName()))
                {
                    String value = bit.currentRow().getAttribute("valueText").getValueString();
                    String name  = auxDef.getAttribute("internalName").getValueString();
                    
                    if(value != null && !"".equals(value))
                    {
                        if( "tipo_documento".equals( name ) )
                        {
                            boObjectList tdoc = boObjectList.list(
                                classification.getEboContext(),
                                "select GesDocClf_TDoc where value=?",
                                new Object[] { value }
                        
                            );
                            if( tdoc.next() )
                            {
                                labels.add(sb.toString());
                                sb.delete(0, sb.length());
                                sb.append( tdoc.getObject().getAttribute("description").getValueString() );
                                values.add(sb.toString());
                                sb.delete(0, sb.length());
                            }
                            else
                            {
                                values.add(value);
                            }
                        }
                        else
                        {
                        boObject lov = auxDef.getAttribute("lov").getObject();
                        if(lov.exists())
                        {
                            labels.add(sb.toString());
                            sb.delete(0, sb.length());
                            
                            lovObject lovit= LovManager.getLovObject( auxDef.getEboContext(), lov.getAttribute("name").getValueString() );
                            
                            lovit.beforeFirst();
                            boObject det;
                            boolean found = false;
                            while(lovit.next())
                            {
                                if(value.equalsIgnoreCase( lovit.getCode() ))
                                {
                                    sb.append( lovit.getDescription() );
                                    values.add(sb.toString());
                                    sb.delete(0, sb.length());
                                    found = true;
                                }
                            }
                            if(!found)
                            {
                                values.add(value);
                            }
                        }
                    }
                }
            }
        }
    }
    }
    
    public static void getGroupClassificationObj(boBridgeIterator bit, boObject classification, String groupSeq, ArrayList classBouis, ArrayList values) throws boRuntimeException
    {
        bit.beforeFirst();
        boObject classObj, auxDef = null, aux;
        StringBuffer sb = new StringBuffer();
        while(bit.next())
        {
            if(groupSeq.equals(bit.currentRow().getAttribute("groupSeq").getValueString()))
            {
                auxDef = bit.currentRow().getObject();
                if("GESDocClfObject".equals(auxDef.getName()))
                {
                    if(bit.currentRow().getAttribute("valueObject").getValueLong() > 0)
                    {
                        values.add(bit.currentRow().getAttribute("valueObject").getObject());
                        classBouis.add(new Long(auxDef.getBoui()));
                    }
                }
                else if("GESDocClfText".equals(auxDef.getName()))
                {
                    String value = bit.currentRow().getAttribute("valueText").getValueString();
                    if(value != null && value.length() > 0)
                    {
                        values.add(bit.currentRow().getAttribute("valueText").getValueString());
                        classBouis.add(new Long(auxDef.getBoui()));
                    }
                }
                else  if("GESDocClfDate".equals(auxDef.getName()))
                {
                    Date d = bit.currentRow().getAttribute("valueDate").getValueDate();
                    if(d != null)
                    {
                        values.add(d);
                        classBouis.add(new Long(auxDef.getBoui()));
                    }
                }
                else  if("GESDocClfNumber".equals(auxDef.getName()))
                {
                    String s = bit.currentRow().getAttribute("valueNumber").getValueString();
                    if(s != null && s.length() > 0)
                    {
                        NumberFormat nf = NumberFormat.getInstance();
                        double d = bit.currentRow().getAttribute("valueNumber").getValueDouble();
                        boolean currency = "1".equals(auxDef.getAttribute("currency").getValueString());
                        if(currency)
                        {
                            //currency
                            values.add(new Double(d));
                            classBouis.add(new Long(auxDef.getBoui()));
                        }
                        else
                        {
                            int decimals = (int)bit.currentRow().getAttribute("decimals").getValueLong();
                            decimals = decimals < 0 ? 0:decimals;
                            if(decimals <= 0)
                            {
                                values.add(new Long(String.valueOf(d)));
                                classBouis.add(new Long(auxDef.getBoui()));
                            }
                            else
                            {
                                values.add(new Double(d));
                                classBouis.add(new Long(auxDef.getBoui()));
                            }                            
                        }
                    }
                }
                else  if("GESDocClfLov".equals(auxDef.getName()))
                {
                    String value = bit.currentRow().getAttribute("valueText").getValueString();
                    if(value != null && !"".equals(value))
                    {
                        values.add(value);
                        classBouis.add(new Long(auxDef.getBoui()));
                    }
                }
            }
        }
    }
    
    public void remove(docHTML doc, EboContext boctx, String groupSeqToRemove) throws boRuntimeException
    {
        //vou remover as classificações do documento
        boObject document = boObject.getBoManager().loadObject(boctx, docBoui);
        boolean removeu = false;
        
        bridgeHandler bh = document.getBridge("classification");
        boBridgeIterator bit = bh.iterator();
        String[] groupS = groupSeqToRemove.split(";");
        String aux;
        long classRemoved = -1;
        for (int i = 0; i < groupS.length; i++) 
        {
            classRemoved = -1;
            bit.beforeFirst();
            while(bit.next())
            {
                aux = bit.currentRow().getAttribute("groupSeq").getValueString();
                if(aux.equals(groupS[i]))
                {
                    if(bit.currentRow().getAttribute("valueClassification").getValueLong() > 0 && classRemoved <= 0)
                    {
                        classRemoved = bit.currentRow().getAttribute("valueClassification").getValueLong();
                    }
                    bh.moveTo( bit.getRow() );
                    bh.remove();
                    bit.previous();
                    removeu = true;
                }
            }
            //vou registar que o utilizador removeu
            if(removeu && classRemoved > 0)
            {
                setRuntimeRemove(boctx, classRemoved);
                clear();
            }
        }
        //vou gravar as alterações
        if(removeu)
        {
            document.update();
        }
    }
    
    
//    public void remove(docHTML doc, EboContext boctx, String bouisToRemove) throws boRuntimeException
//    {
//        //vou remover as classificações do documento
//        boObject document = boObject.getBoManager().loadObject(boctx, docBoui);
//        boolean removeu = false;
//        
//        bridgeHandler bh = document.getBridge("classification");
//        boBridgeIterator bit = bh.iterator();
//        String[] bouis = bouisToRemove.split(";");
//        String aux;
//        for (int i = 0; i < bouis.length; i++) 
//        {
//            bit.beforeFirst();
//            while(bit.next())
//            {
//                aux = String.valueOf(bit.currentRow().getObject().getBoui());
//                if(aux.equals(bouis[i]))
//                {
//                    bh.moveTo( bit.getRow() );
//                    bh.remove();
//                    bit.previous();
//                    removeu = true;
//                }
//                else
//                {
//                    if(bit.currentRow().getAttribute("valueClassification").getValueLong() > 0)
//                    {
//                        aux = String.valueOf(bit.currentRow().getAttribute("valueClassification").getValueLong());
//                        if(aux.equals(bouis[i]))
//                        {
//                            bh.moveTo( bit.getRow() );
//                            bh.remove();
//                            bit.previous();
//                            removeu = true;
//                        }
//                    }
//                }
//            }
//            //vou registar que o utilizador removeu
//            if(removeu)
//            {
//                long auxL = Long.parseLong(bouis[i]);
//                if(auxL > 0)
//                {
//                    setRuntimeRemove(boctx, auxL);
//                    clear();
//                }
//            }
//        }
//        //vou registar que o utilizador removeu
//        if(removeu)
//        {
//            document.update();
//        }
//    }
    
//    public void alterar(docHTML doc, EboContext boctx, String classifToSet) throws boRuntimeException
//    {
//        group = -1;
//        groupClassifications.clear();
//        if(classifToSet.indexOf(";") > -1)
//        {
//            classifToSet = (classifToSet.split(";"))[0];
//        }
//        setClassBoui(boctx, classifToSet);
//        long clsBoui = Long.parseLong(classifToSet); 
//        boObject document = doc.getObject(docBoui);
//        boBridgeIterator bit = document.getBridge("classification").iterator();
//        for (int i = 0; i < groupClassifications.size(); i++) 
//        {
//            ((GesDocObj)groupClassifications.get(i)).setValue(boctx, bit, clsBoui);
//        }
//        editing = true;       
//    }

    public void changeTipoDoc(EboContext boctx, String value) throws boRuntimeException
    {
        boObject doc = boObject.getBoManager().loadObject(boctx, getDocument());
        doc.getAttribute("tipoDoc").setValueString(value);
        doc.update();
    }
    public void alterar(docHTML doc, EboContext boctx, String classifToSet) throws boRuntimeException
    {
        group = -1;
        groupClassifications.clear();

        String groupSeq = classifToSet;
        if(classifToSet.indexOf(";") > -1)
        {
            groupSeq = (classifToSet.split(";"))[0];
        }
        
        boObject document = doc.getObject(docBoui);
        boBridgeIterator bit = document.getBridge("classification").iterator();
        bit.beforeFirst();
        String aux = null;
        boolean found = false;
        long clsBoui = -1;
        while(bit.next() && !found)
        {
            aux = bit.currentRow().getAttribute("groupSeq").getValueString();
            if(aux.equals(groupSeq))
            {
                if((clsBoui = bit.currentRow().getAttribute("valueClassification").getValueLong()) > 0)
                {
                    setClassBoui(boctx, clsBoui);
                    setGroupSequence(aux);
                    found = true;
                }
            }
        }
        
        bit.beforeFirst();
        for (int i = 0; i < groupClassifications.size(); i++) 
        {
            ((GesDocObj)groupClassifications.get(i)).setValue(boctx, bit, groupSeq);
        }
        editing = true;       
    }
    
    public String getDocCardId(docHTML doc) throws boRuntimeException
    {
        boObject docum = doc.getObject(docBoui);
        return docum.getCARDID().toString();
    }
    
    public void validate(EboContext boctx, ArrayList erros) throws boRuntimeException
    {
        long ti = System.currentTimeMillis();
        logger.info("--------------------------------"+LoggerMessageLocalizer.getMessage("VALIDATION")+"-----------------------------------------");
        for (int i = 0; i < groupClassifications.size(); i++) 
        {
            ((GesDocObj)groupClassifications.get(i)).validate(boctx, erros);
        }
        if(erros.size() == 0 && validacao != null && validacao.length() > 0)
        {
            javaValidation(boctx, erros);
        }
        long tf = System.currentTimeMillis();
        logger.info(LoggerMessageLocalizer.getMessage("TOTAL_VALIDATION_TIME")+" (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)");
        logger.info("--------------------------------------------------------------------------------");
    }
    
    public void clear()
    {
        classBoui = -1;
        group = -1;
        groupSequence = null;
        groupClassifications.clear();
        validacao = null;
        nome = null;
        editing = false;
    }
    
    public static String newGroupSequence( EboContext ctx ) throws boRuntimeException
    {
        Connection cn = null;
        String toRet = null;
        String key = null;
        boolean sucess = false;
         
        try
        {
            cn = ctx.getDedicatedConnectionData();
            if (!ctx.isInTransaction()) ctx.beginContainerTransaction();
            int ano = Calendar.getInstance().get(Calendar.YEAR); 
            key = ano + "_" + GROUP_SEQ_KEY;
            long seq = DataUtils.GetSequenceNextVal(ctx.getApplication(), cn, key);
            toRet = ano + "_" + padding(9, seq);
            sucess = true;
        }
        catch (Exception e)
        {
            String[] args = { GROUP_SEQ_KEY };
            throw new boRuntimeException("pt.lusitania.events.boMessage.beforeSave","BO-3121",e,args);
        }
        finally
        {
            if(sucess)
            {
                try{ctx.commitContainerTransaction();}catch(Exception e){}
            }
            else
            {
                try{ctx.rollbackContainerTransaction();}catch(Exception e){}
            }
            try{if(cn != null) cn.close();}catch (Exception e){}
        }
        
        return toRet;
    }
    public static void writeHTML_forCombo(
          StringBuffer toPrint , 
          StringBuffer Value ,
          StringBuffer Name,
          StringBuffer id, 
          int tabIndex, 
          lovObject lov_obj  ,
          boolean allowValueEdit,
          boolean isDisabled ,
          boolean isVisible ,
          boolean inEditTemplate ,
          StringBuffer onChange,
          boolean isRequired,
          boolean isRecommend,
          Hashtable xattributes
          ) 
          throws boRuntimeException{
          
          toPrint.append("<SPAN class=selectBox "); 
          toPrint.append(" name = '");
          toPrint.append(Name);
          toPrint.append("' id = '");
          toPrint.append(id);
          toPrint.append('\'');
          
          StringBuffer onchangeHandler=new StringBuffer();
          
          if ( isDisabled ){
                toPrint.append(" disabled  ");
          }
          
          //focus
          if(isVisible && !isDisabled)
            toPrint.append(getonfocus(Name.toString()));
          
          if ( onChange.length() >0 ) {
                toPrint.append(" changeHandler='");
                if ( onChange.toString().indexOf("(") > -1 )
                {
                    onchangeHandler.append( "<script> function hlv" );
                    onchangeHandler.append( Name );
                    onchangeHandler.append( "(){" );
                    onchangeHandler.append( onChange );
                    onchangeHandler.append( "}</script>");
                    toPrint.append( "hlv" );
                    toPrint.append( Name );
                }
                else
                {
                    toPrint.append( onChange );
                }
                toPrint.append('\'');
          }
          
          if ( !isVisible ) {
             toPrint.append(" style='display:none' ");
          }
        
          toPrint.append(" tabbingIndex='");
          toPrint.append(tabIndex);
          if( allowValueEdit) toPrint.append("' allowValueEdit='true' ");
          else toPrint.append("'");
          toPrint.append(" value='");
          toPrint.append(Value);
          toPrint.append("'>");
          
 
          toPrint.append("<TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2><TBODY>");

          
          
          boObject det;
          if(!isRequired)
          {
            printLovValue(toPrint, "", 
                "&nbsp;");
          }
          boolean printed = false;
          String firstValue = "";
          boolean first = true;
          if( lov_obj.getSize() > 0){
                String v = null;
                lov_obj.beforeFirst();
                while (lov_obj.next())
                {
                    printed = true;
                    printLovValue(toPrint,lov_obj.getCode() , lov_obj.getDescription() );                        
                    if(first)
                    {
                        firstValue = lov_obj.getCode();
                        first = false;
                    }
                }
          }
          if(!printed && isRequired)
          {
             printLovValue(toPrint, "", "&nbsp;");
          }
    
          toPrint.append("</TBODY></TABLE>");
          toPrint.append("<INPUT type='hidden' id='")
          .append(Name).append("_lovValue' name='").append(Name).append("_lovValue' ")
          .append("value='").append((isRequired && (Value == null || Value.length()==0)) ? firstValue:Value.toString()).append("'/>");
          toPrint.append("</SPAN>");
          if ( onchangeHandler.length() > 0 )
          {
            toPrint.append(onchangeHandler );    
          }
          
          if(inEditTemplate){
            //toPrint.append("<img src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
          }


    }
    
    private static void printLovValue(StringBuffer toPrint, String value, String desc)
    {
        toPrint.append("<TR><TD val='");
        toPrint.append(value);
        if(desc!= null && desc.length() > 0 && !"&nbsp;".equals(desc))
        {
            toPrint.append("' title = '"+ desc +"'>");
        }
        else
        {
            toPrint.append("'>");
        }
        
        toPrint.append(desc);
        toPrint.append("</TD></TR>");
    }
    
    private static ArrayList getDocumentClassif(EboContext ctx, long documentBoui) throws boRuntimeException
    {
        boObject doc = boObject.getBoManager().loadObject(ctx, documentBoui);
        return getDocumentClassif(doc);
    }
    
    private static ArrayList getDocumentClassif(boObject doc) throws boRuntimeException
    {
        Connection cn = null;
        ArrayList toRet = new ArrayList();
        EboContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try
        {
            ctx = doc.getEboContext();
            cn = ctx.getConnectionData();
            String tablename = doc.getAttribute("classification").getDefAttribute().getBridge().getBoMasterTable();
            String sql_toexec = "select distinct valueclassification$ from "+tablename+" where parent$ = ?";
            pst =  cn.prepareStatement(sql_toexec);
            pst.setLong(1, doc.getBoui());
            rs = pst.executeQuery();
            while(rs.next())
            {
                toRet.add(rs.getString(1));
            } 
        }
        catch (Exception e)
        {
            String[] args = { GROUP_SEQ_KEY };
            throw new boRuntimeException("pt.lusitania.events.boMessage.beforeSave","BO-3121",e,args);
        }
        finally
        {
            try{if(rs != null) rs.close();}catch(Exception e){/*IGNORE*/}
            try{if(pst != null) pst.close();}catch(Exception e){/*IGNORE*/}
        }
        return toRet;
    }
    
    private static void getDocumentClassif(boObject doc, ArrayList classifs, ArrayList groupSeqs) throws boRuntimeException
    {
        Connection cn = null;
        EboContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try
        {
            ctx = doc.getEboContext();
            cn = ctx.getConnectionData();
            String tablename = doc.getAttribute("classification").getDefAttribute().getBridge().getBoMasterTable();
            String sql_toexec = "select distinct valueclassification$, groupSeq from "+tablename+" where parent$ = ? order by groupSeq";
            pst =  cn.prepareStatement(sql_toexec);
            pst.setLong(1, doc.getBoui());
            rs = pst.executeQuery();
            while(rs.next())
            {
                classifs.add(rs.getString(1));
                groupSeqs.add(rs.getString(2));
            } 
        }
        catch (Exception e)
        {
            String[] args = { GROUP_SEQ_KEY };
            throw new boRuntimeException("pt.lusitania.events.boMessage.beforeSave","BO-3121",e,args);
        }
        finally
        {
            try{if(rs != null) rs.close();}catch(Exception e){/*IGNORE*/}
            try{if(pst != null) pst.close();}catch(Exception e){/*IGNORE*/}
        }
    }
    private static String padding(int n, long data)
    {
        String s = new String();
        int l = String.valueOf(data).length();
        if(n >= l)
        {
            for (int i = 0; i < (n - l); i++)
            {
                s += "0";
            }
        }
        s += String.valueOf(data);
        return s;
    }
    
    public static ArrayList getLovCCustValues(EboContext boctx) throws boRuntimeException
    {
        boObjectList list = boObjectList.list(boctx, "select Ebo_Group_CC where 1=1 order by name", 1, 999999999);
        list.beforeFirst();
        ArrayList toRet = new ArrayList(2);
        StringBuffer[] apresentationStr = new StringBuffer[(int)list.getRecordCount()];
        StringBuffer[] valuesStr = new StringBuffer[(int)list.getRecordCount()];

        boObject auxObj;
        int i = 0;
        while(list.next())
        {
            auxObj = list.getObject();
            apresentationStr[i] = auxObj.getCARDIDwNoIMG();
            valuesStr[i] = new StringBuffer(String.valueOf(auxObj.getBoui()));
            i++;
        }
        if(i == 0)
        {
            return null;
        }
        else if(i < (int)list.getRecordCount())
        {
            apresentationStr = shrink(apresentationStr, i);
            valuesStr = shrink(valuesStr, i);
        }
        toRet.add(apresentationStr);
        toRet.add(valuesStr);
        return toRet;
    }
    
    public static ArrayList getLovUserFromCCustValues(EboContext boctx, String ccust) throws boRuntimeException
    {
        StringBuffer[] apresentationStr = null;
        ArrayList toRet = new ArrayList(2);
        StringBuffer[] valuesStr = null;
        if(ccust == null || "".equals(ccust))
        {
            apresentationStr = new StringBuffer[0];
            valuesStr = new StringBuffer[0];
        }
        else
        {
            boObjectList list = boObjectList.list(boctx, "select Ebo_Perf_Lus where centrocusto = "+ccust+" or groups = "+ccust+" order by name", 1, 999999999);
            list.beforeFirst();
            toRet = new ArrayList(2);
            apresentationStr = new StringBuffer[(int)list.getRecordCount()];
            valuesStr = new StringBuffer[(int)list.getRecordCount()];
    
            boObject auxObj;
            int i = 0;
            while(list.next())
            {
                auxObj = list.getObject();
                apresentationStr[i] = auxObj.getCARDIDwNoIMG();
                valuesStr[i] = new StringBuffer(String.valueOf(auxObj.getBoui()));
                i++;
            }
            if(i == 0)
            {
                return null;
            }
            else if(i < (int)list.getRecordCount())
            {
                apresentationStr = shrink(apresentationStr, i);
                valuesStr = shrink(valuesStr, i);
            }
        }
        toRet.add(apresentationStr);
        toRet.add(valuesStr);
        return toRet;
    } 
    
    private boolean javaValidation(EboContext boctx, ArrayList erros) throws boRuntimeException
    {
        JavaExecuter javaExec = new JavaExecuter(nome);
        //imports
        javaExec.addImport("java.sql");
        javaExec.addImport("java.lang");
        javaExec.addImport("netgest.bo");
        javaExec.addImport("netgest.bo.def");
        javaExec.addImport("netgest.utils");
        javaExec.addImport("netgest.bo.runtime");
        javaExec.addImport("netgest.bo.utils");
        javaExec.addImport("netgest.bo.impl.document.merge.gestemp");
    
        //variaveis
        Contexto contexto = new Contexto(boctx);
        javaExec.addTypedVariable( "contexto", Contexto.class, contexto, null);
        javaExec.addTypedVariable( nome, Classificacao.class, new Classificacao(boctx, this), null);
        

        //javaCode
        javaExec.setJavaCode(validacao);

        Object result = javaExec.execute();
        if(result != null && result instanceof Boolean)
        {
            if(!((Boolean)result).booleanValue())
            {
                for (int i = 0; i < contexto.getErros().size(); i++) 
                {
                    erros.add(contexto.getErros().get(i));
                }
                return false;
            }
        }
        return true;
    }
    
   public static boolean beforeSaveAttribute(boObject tag) throws boRuntimeException
   {
        String aux = tag.getAttribute("internalName").getValueString();
        if(aux != null && aux.split(" ").length != 1)
        {
            tag.addErrorMessage("O campo Nome Interno só pode conter uma única palavra.");
            return false;
        }
        return true;
   }
   
   public static boolean beforeSaveClf(boObject tag) throws boRuntimeException
   {
        String aux = tag.getAttribute("internalName").getValueString();
        if(aux != null && aux.split(" ").length != 1)
        {
            tag.addErrorMessage("O campo Nome Interno só pode conter uma única palavra.");
            return false;
        }
        if(tag.isChanged())
        {
            //LUSITANIA:FilterViewer.clearCache();
            ExplorerServer.clearCacheExplorerWPrefix("explorerDoc");            
        }
        return true;
   }
   
   public String getTipoDocValue(docHTML doc) throws boRuntimeException
   {
        boObject docObj = boObject.getBoManager().loadObject(doc.getEboContext(), getDocument());
        return docObj.getAttribute("tipoDoc").getValueString();
   }
   
   public boolean tipoDocFilled(docHTML doc) throws boRuntimeException
   {
        boObject docObj = boObject.getBoManager().loadObject(doc.getEboContext(), getDocument());
        return docObj.getAttribute("tipoDoc").getValueString() != null && !"".equals(docObj.getAttribute("tipoDoc").getValueString());
   }
}