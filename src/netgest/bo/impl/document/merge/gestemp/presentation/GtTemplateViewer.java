/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge.gestemp.presentation;

import java.io.IOException;

import java.lang.StringBuffer;

import java.net.URLEncoder;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable; 

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import netgest.bo.controller.Controller;
import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_renderFields;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.impl.document.merge.gestemp.*;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.presentation.render.Browser;
import netgest.bo.presentation.render.elements.AdHocElement;
import netgest.bo.presentation.render.elements.Element;
import netgest.bo.presentation.render.elements.Preview;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectStateHandler;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.utils.ClassUtils;

import netgest.utils.ngtXMLHandler;
import netgest.xwf.common.xwfBoManager;
import netgest.xwf.common.xwfHelper;
import netgest.xwf.core.xwfECMAevaluator;

import netgest.bo.system.Logger;

import xeo.client.business.events.ClientEvents;
import xeo.client.business.helper.RegistryHelper;
import xeo.client.business.helper.ServiceHelper;

/**
 * <p>Title: GtTemplateViewer </p>
 * <p>Description: Classe responsável por apresentar os parâmetros de entrada para geração do template</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Francisco Câmara
 * @version 1.0
 */
public class GtTemplateViewer 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.document.merge.gestemp.presentation.GtTemplateViewer");
    
//    public static void renderActivity(Controller controller,PageContext pageContext,int idx, long templateBoui) throws IOException, boRuntimeException
//    {
//        JspWriter out = pageContext.getOut();    
//        docHTML doc = controller.getDocHTML();
//        StringBuffer toPrint = new StringBuffer();
//        
//        renderTableEnd(toPrint);
//        out.print(toPrint);
//    }

    public static String getFistFieldID(GtTemplate template)
    {
        GtQuery[] gtQuery = template.getQueries();
        for (int i = 0; i < gtQuery.length; i++) 
        {
            GtParametro parametro = gtQuery[i].getParametro();
            if(parametro.getFormula() == null || "".equals(parametro.getFormula()))
            {
                return "IMG"+parametro.getHTMLFieldName();
            }
        }
        GtCampo[] gtManual = template.getCamposManuais();
        for (int i = 0; i < gtManual.length; i++) 
        {
            return gtManual[i].getHTMLFieldName();
        }
        return null;
    }
    
    public static void renderParameters(Controller controller,PageContext pageContext,int idx, GtTemplate template) throws IOException, boRuntimeException
    {
        JspWriter out = pageContext.getOut();
        docHTML doc = controller.getDocHTML();
        
        StringBuffer toPrint = new StringBuffer();
        Hashtable ht_variables = new Hashtable();
        
        boolean showListFill = 
                        template.hasSelected() && 
                        template.hasFillParams() && 
                        template.hasListDependents() && 
                        !template.hasFillListParams();
                        
        boolean showAnexosFill = 
                        template.hasSelected() && 
                        template.getChannel() != GtTemplate.TYPE_SMS && 
                        template.hasFillParams() && 
                        (!template.hasFillListParams() || template.hasFillListParams());
        
        renderTableStart(toPrint);
        //Template
        String label = template.getCode() + " - " + template.getNome();
        renderSeparatorLabel(toPrint, label, template.getHelpURL(), template.getBoui());
        
        //description
        renderInfo(toPrint, template.getDescricao());
        //Render o canal
        if(!template.hasSelected() && hasMoreThanOneOption(template))
        {
            renderQuestion(toPrint,Messages.getString("GtTemplateViewer.4"), true);
            renderChannelChecks(controller.getDocHTML().getEboContext(), toPrint,template, false);
            renderTypeButton(toPrint);
            renderTableEnd(toPrint);
            out.print(toPrint);
            return;
        }
        else if(!(template.hasSelected() && template.getChannel() == GtTemplate.TYPE_FAX && template.hasFillParams()))
        {
            if(!showAnexosFill && !showListFill)
            {
                renderQuestion(toPrint,Messages.getString("GtTemplateViewer.5"), true);
                renderChannelChecks(controller.getDocHTML().getEboContext(),toPrint,template, true);
            }
        }
        //se fôr carta vai escolher a forma de envio se o template permitir
        if(template.hasSelected() && template.getChannel() == GtTemplate.TYPE_CARTA)
        {
            if(template.hasSelectedLetterType())
            {
                if(!showAnexosFill && !showListFill)
                {
                    renderQuestion(toPrint,Messages.getString("GtTemplateViewer.6"), true);
                    renderLetterChecks(controller.getDocHTML().getEboContext(),toPrint,template, true);
                }
            }
            else
            {
                renderQuestion(toPrint,Messages.getString("GtTemplateViewer.7"), true);
                renderLetterChecks(controller.getDocHTML().getEboContext(), toPrint,template, false);
                renderLetterTypeButton(toPrint);
                renderTableEnd(toPrint);
                out.print(toPrint);
                return;
            }
        }
        //se o template tiver lista dependentes das queries e com a escolha do utilizador
        if(showListFill)
        {
            GtQuery[] gtQuery = template.getQueries();
            long auxObjBoui = -1;
            boolean show = true;
            boolean automatic = false;
            for (int i = 0; i < gtQuery.length; i++) 
            {
                if(gtQuery[i].hasListDependents())
                {
                    GtParametro parametro = gtQuery[i].getParametro();
                    renderQuestion(toPrint,Messages.getString("GtTemplateViewer.8")+parametro.getPergunta()+"]", false);
                    ArrayList listObjects = gtQuery[i].getListDependents();
                    for (int k = 0; k < listObjects.size(); k++) 
                    {
                        GtCampoNObjecto lObj = (GtCampoNObjecto)listObjects.get(k);
                        renderQuestion(toPrint,lObj.getPergunta(), lObj.getObrigatorio());
                
                        auxObjBoui = lObj.getObjecto(); 
                        boObject tipo = boObject.getBoManager().loadObject(doc.getEboContext(), auxObjBoui);
                        String clsName = tipo.getAttribute("name").getValueString();
                        boDefHandler bodef = boDefHandler.getBoDefinition(clsName);
                        String referenceObjName = bodef.getAttributeRef(lObj.getHelper()).getReferencedObjectName();
        
                        String value = "";
                        if(lObj.getValue() != null)
                        {
                           GtValue v = lObj.getValue();
                           if(v.getValues() != null)
                           {
                             ArrayList auxR = ((ArrayList)v.getValues());
                             for (int n = 0; n < auxR.size(); n++) 
                             {
                                value = value + ((Long)auxR.get(n)).toString() + ";";
                             }
                           }
                        }
                        String boqlFilter = lObj.getBoqlFilter(controller.getDocHTML().getEboContext());
                        renderObjects(toPrint, lObj.getHTMLFieldName(),
                            true, true, 0 , referenceObjName,value, controller.getDocHTML(), 
                            controller.getDocHTML().getDocIdx(), false, false, lObj.getObrigatorio(), -1,
                            Long.MIN_VALUE, Long.MAX_VALUE, boqlFilter, 0, 0
                            );
                    }    
                }
            }
            
            if(template.getChannel() != template.TYPE_SMS )
            {
                
                if( 
                    template.getAllowAttachs() == template.ATTACH_FORBIDDEN && 
                    template.getChannel() == template.TYPE_CARTA 
                )
                {
                    renderListButton(toPrint);
                }
                else
                {
                    renderGenerateButton(toPrint, "generateAnexos");
                }
            }
            else
            {
                renderListButton(toPrint);
            }
            renderTableEnd(toPrint);
            out.print(toPrint);
            return;
        }
        //se fôr fax vai escolher
        if(showAnexosFill)
        {
//            renderQuestion(toPrint,"Fax", false);
            //checks Fax's
//            renderFaxChecks(controller.getDocHTML().getEboContext(), toPrint,template, false);
            //número do Fax
            if(template.getChannel() == GtTemplate.TYPE_FAX)
            {
                renderQuestion(toPrint,Messages.getString("GtTemplateViewer.14"), true);
                String faxNumber = template.getFaxNumber();
                if(template.getFaxNumber() == null || "".equals(template.getFaxNumber()))
                {
                    faxNumber = Helper.getFaxNumber(controller.getDocHTML().getEboContext(), template);
                }
                renderCommon(toPrint, "fax_number", 1, null, doc, faxNumber , false, true, -1, -1, -1);
            }
            
            /*
            if(template.getChannel() == GtTemplate.TYPE_EMAIL)
            {
                renderQuestion(toPrint,Messages.getString("GtTemplateViewer.17"), true);
                String emailAddress = template.getEmailAddress();
                if(template.getEmailAddress() == null || "".equals(template.getEmailAddress()))
                {
                    emailAddress = Helper.getEmailAddress(controller.getDocHTML().getEboContext(), template);
                }
                renderCommon(toPrint, "email_address", 1, null, doc, emailAddress, false, true, -1, -1, -1);
            }
            */
            
            if( template.getAllowAttachs() > GtTemplate.ATTACH_FORBIDDEN )
            {
                renderQuestion(toPrint,Messages.getString("GtTemplateViewer.20"), template.getAllowAttachs() == GtTemplate.ATTACH_REQUIRED );
                toPrint.append("<tr><td>&nbsp;</td><td width='100%' colspan=2>");
                StringBuffer anexosBouis = new StringBuffer("");
                if(template.getFaxAnexos() != null && template.getFaxAnexos().length() > 0)
                    anexosBouis.append(template.getFaxAnexos());
                writeHTML_lookupN(toPrint, null, null, null, anexosBouis, 
                                  new StringBuffer("faxAnexos"), 
                                  new StringBuffer("tblLookfaxAnexos"), 1, controller.getDocHTML(), false, true, false, false, false, true, 
                                  null, null, null, new StringBuffer("Ebo_Document"), null);
                toPrint.append("</td></tr>");
            }
            
            renderFaxButton(toPrint);
            renderTableEnd(toPrint);
            out.print(toPrint);
            return;
        }
        
        // Query's
        GtQuery[] gtQuery = template.getQueries();
        long auxObjBoui = -1;
        boolean show = true;
        boolean automatic = false;
        for (int i = 0; i < gtQuery.length; i++) 
        {
            show = true;
            automatic = false;
            GtParametro parametro = gtQuery[i].getParametro();
            if(parametro.getFormula() != null && !"".equals(parametro.getFormula()))
            {
                automatic = true;
                
                // Comentado para dar a possibilidade de controlar o recalculo no código
                // Exemplo. sempre que se faz editar parametros mudar o remetente.
                //if(parametro.getValue() == null || parametro.getValue().getValue() == null )
                //{
                    parametro.calculateAutomicFields(controller.getDocHTML().getEboContext(), template, gtQuery[i]);
                //}
                if(!parametro.askUser())
                {
                    show = false;
                }
            }
            if(show)
            {
                renderQuestion(toPrint,parametro.getPergunta(), parametro.getObrigatorio());
                auxObjBoui = parametro.getObjecto(); 
                
                int    iTipo       = 0;
                String sObjectName = null;
                if( auxObjBoui != 0 )
                {
                    boObject oTipo = boObject.getBoManager().loadObject(doc.getEboContext(), auxObjBoui);
                    sObjectName = oTipo.getAttribute("name").getValueString();
                    iTipo = 0;
                }
                else
                {
                    iTipo = 1;//Integer.parseInt( parametro.getTipo() );
                }

                String value = "";
                if(parametro.getValue() != null)
                {
                   GtValue v = parametro.getValue();
                   if(v.getValue() != null)
                   {
                        value = v.getValue().toString();
                   }
                }
                renderObjects(toPrint, parametro.getHTMLFieldName(),
                    true, false, iTipo , sObjectName ,value, controller.getDocHTML(), 
                    controller.getDocHTML().getDocIdx(), automatic, false, parametro.getObrigatorio(), -1,
                    Long.MIN_VALUE, Long.MAX_VALUE, null, parametro.getSize(), parametro.getWidth() 
                    );
            }
        }
        
        //Campos Manuais
        GtCampo[] gtManual = template.getCamposManuais();
        for (int i = 0; i < gtManual.length; i++) 
        {
            renderQuestion(toPrint,gtManual[i].getPergunta(), gtManual[i].getObrigatorio());
            long tipo = Long.parseLong(gtManual[i].getTipo());
            long textos = gtManual[i].getTextos();
            boolean isList = !"1".equals(gtManual[i].getTipoSeleccao());
            String nome = gtManual[i].getNome();
            String value = "";
            if(gtManual[i].getValue() != null)
            {
               GtValue v = gtManual[i].getValue();
               if(!isList)
               {
                   if(v.getValue() != null)
                   {
                     value = (String)v.getValue();
                   }
               }
               else
               {
                   if(v.getValues() != null)
                   {
                     ArrayList r = v.getValues();
                     for (int j = 0; j < r.size(); j++) 
                     {
                        if( r.get(j) instanceof Long )
                        {
                            value = value + (((Long)r.get(j)).toString()) + ";";
                        }
                        else
                        {
                            r.remove( j );
                            j--;
                        }
                     }
                   }
               }
            }
            renderObjects(toPrint, gtManual[i].getHTMLFieldName(),
                true, isList, tipo , null, value, controller.getDocHTML(), 
                controller.getDocHTML().getDocIdx(), false, false, gtManual[i].getObrigatorio(), textos,
                gtManual[i].getMin(),gtManual[i].getMax(), null, 0 ,0 
            ); 
        }
        String buttonDesc =  "generate";
        if(template.hasListDependents())
        {
            buttonDesc =  "listFill";
        }
        else if(
                template.getChannel() != template.TYPE_SMS && 
                ( 
                    template.getAllowAttachs() != template.ATTACH_FORBIDDEN 
                    || 
                    template.getChannel() == GtTemplate.TYPE_FAX 
                )
                )
        {
            buttonDesc =  "generateAnexos";
        }
        renderGenerateButton(toPrint, buttonDesc); 
        renderTableEnd(toPrint);
        out.print(toPrint);
    }
    
    
    private static void renderObjects(StringBuffer toPrint, String fieldName, boolean objType, boolean list,
            long type, String objName, String value,
            docHTML doc, int idx,boolean forceDisabled, boolean showAttributes, boolean isRequired, long textos,
            long min, long max, String boqlFilter, int size, int width
    )  throws boRuntimeException,IOException
    {
        Controller controller = doc.getController();
        boObject valueObject = null;
        long maxoccurs =  -1;
        long showMode = -1;
        if(objType)
        {
            if(!list)
            {                              
                renderCommon(toPrint,fieldName,type,objName,doc,value,forceDisabled, isRequired, textos, min, max, size, width);                    
            }
            else
            {   
                renderList(toPrint,fieldName,type,objName,doc,value,forceDisabled, isRequired, textos, min, max, boqlFilter);
            }                
        }
        else 
        {
//                toPrint.append("<tr>");
            renderCommon(toPrint,fieldName,type,objName,doc,value,forceDisabled,isRequired, textos, min, max);                       
//                toPrint.append("</tr>");
        }
    }
    private static void renderList(StringBuffer toPrint, String fieldName, long type, String objName,
                                     docHTML doc, String value, boolean forceDisabled, boolean isRequired, long lovText,
                                     long minNumber, long maxNumber, String boqlFilter
                                    )  throws boRuntimeException
    {
        if(type == 7)
        {
            toPrint.append("<tr height='85px'><td>&nbsp;</td><td width='100%' colspan=2>");    
        }
        else
        {
            toPrint.append("<tr><td>&nbsp;</td><td width='100%' colspan=2>");        
        }
        
        Controller controller =  doc.getController();

        StringBuffer nameH = new StringBuffer(fieldName);
        StringBuffer id = new StringBuffer();
        if(type == 0)
            id.append( fieldName);
        else
            id.append( fieldName);
        
        boolean isDisabled = false;
        boolean isVisible = true;        
        boolean isRecommend = false;        
        
        
        if(type == 0)
        {
            StringBuffer sbValue = new StringBuffer("");
            if(value != null && value.length() > 0)
                sbValue.append(value);
            writeHTML_lookupN(toPrint, null, null, null, sbValue, 
                              new StringBuffer(fieldName), 
                              new StringBuffer("tblLook"+fieldName), 1, doc, forceDisabled, true, false, isRequired, false, true, 
                              null, null, null, new StringBuffer(objName), boqlFilter);
            toPrint.append("</td></tr>");
        }
        else
        {
            Hashtable xatt = new Hashtable();
            xatt.put("height", "80px");
            writeHTML_text(
                    toPrint,
                    new StringBuffer(value),
                    nameH,
                    id,
                    1,
                    isDisabled,
                    isVisible,
                    false,
                    new StringBuffer(""),
                    isRequired,
                    isRecommend,
                    3999,
                    fieldName,
                    doc,
                    lovText,
                    xatt, 
                    true
                    );
        }
        toPrint.append("</td></tr>"); 
        
    }
    public static void renderCommon(StringBuffer toPrint, String fieldName,
            long type, String objName,
            docHTML doc, String value, boolean forceDisabled, boolean isRequired, long lovText,
            long minNumber, long maxNumber
    )  throws boRuntimeException
    {              
        renderCommon( toPrint, fieldName,
                    type, objName,
                    doc, value, forceDisabled, isRequired, lovText,
                    minNumber, maxNumber, 0, 0 );
    }
    public static void renderCommon(StringBuffer toPrint, String fieldName,
            long type, String objName,
            docHTML doc, String value, boolean forceDisabled, boolean isRequired, long lovText,
            long minNumber, long maxNumber, int size, int width
    )  throws boRuntimeException
    {              
        if( size == 0 )
        {
            size = 255;
        }
        if(type == 7)
        {
            toPrint.append("<tr height='85px'><td>&nbsp;</td><td width='100%' colspan=2>");    
        }
        else
        {
            toPrint.append("<tr><td>&nbsp;</td><td width='"+(width==0?"100%":width+"px")+"' colspan=2>");        
        }
        
        Controller controller =  doc.getController();

        StringBuffer nameH = new StringBuffer(fieldName);
        StringBuffer id = new StringBuffer();
        if(type == 0)
            id.append( fieldName);
        else
            id.append( fieldName);
        
        boolean isDisabled = false;
        boolean isVisible = true;        
        boolean isRecommend = false;                   
        
        if(type == 0)
        {
                                
          writeHTML_lookup(
            new StringBuffer( objName),
            toPrint,
            null,
            null,
            new StringBuffer(value),
            nameH,
            id,
            1,
            doc,
            forceDisabled,
            isVisible,            
            isRequired,
            isRecommend,
            fieldName
          );
        }                
        else if(type == 5)
        {
            docHTML_renderFields.writeHTML_forBoolean(toPrint,
                                            new StringBuffer(value),
                                            nameH,
                                            id,
                                            1,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            null
                                            );                                                                 
        }
        else if(type == 2 || type == 3)
        {
          
             String decimals = "0";
             String grouping = "0";
             String minDecimals = "-99999999";
             if(type == 2)
             {
                 decimals = "2";
                 minDecimals = "2";
                 grouping = "1";
             }
            docHTML_renderFields.writeHTML_forNumber(toPrint,
                                            new StringBuffer(value),
                                            nameH,
                                            id,
                                            1,
                                            new StringBuffer(""),
                                            new StringBuffer(decimals),
                                            new StringBuffer(minDecimals),
                                            ("0".equals(grouping)) ?  false :  true,    
                                            new StringBuffer(String.valueOf(maxNumber)),
                                            new StringBuffer(String.valueOf(minNumber)),
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            null
                                            );                                                                                                                                                 
                                            
        }
        else if(type == 4)
        {
            if(value != null && value.length() > 0)
            {
                try
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    SimpleDateFormat sdfts = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss");
                    Date d = sdf.parse(value);
                    value = sdfts.format(d);
                }
                catch (Exception e)
                {
                    /*IGNORE*/
                }
            }
            docHTML_renderFields.writeHTML_forDateTime(toPrint,
                                            new StringBuffer(value),
                                            nameH,
                                            id,
                                            1,
                                            true,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            null
                                            );                                                                 
        }
        else if(type == 6)
        {
            docHTML_renderFields.writeHTML_forDate(toPrint,
                                            new StringBuffer(value),
                                            nameH,
                                            id,
                                            1,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            null
                                            );                                                                 
        }       
        else if(type == 1 || type == 7)
        { 
            int chaNumber = 255;
            Hashtable xatt = new Hashtable();
            boolean hasLovText = lovText > 0;
            if(type == 7)
            {
                chaNumber = 3999;
                xatt.put("height", "80px");
            }
            
            if(!hasLovText)
            {
                docHTML_renderFields.writeHTML_text(toPrint,
                                            new StringBuffer(value),
                                            nameH,
                                            id,
                                            1,
                                            isDisabled,
                                            isVisible,
                                            false,
                                            new StringBuffer(""),
                                            isRequired,
                                            isRecommend,
                                            chaNumber,
                                            xatt
                                            );
            }
            else
            {
                writeHTML_text(
                                toPrint,
                                new StringBuffer(value),
                                nameH,
                                id,
                                1,
                                isDisabled,
                                isVisible,
                                false,
                                new StringBuffer(""),
                                isRequired,
                                isRecommend,
                                chaNumber,
                                fieldName,
                                doc,
                                lovText,
                                xatt,
                                false
                                );
            }            
        }
        else if(type == 12)
        {
            //LOV NOT IMPLEMENTED
            lovObject lObj = LovManager.getLovObject(doc.getEboContext(), lovText);

            docHTML_renderFields.writeHTML_forCombo(
                toPrint,
                new StringBuffer(value),
                id ,
                id,
                1,
                lObj,
                false,
                isDisabled,
                isVisible,
                false,
                new StringBuffer(),
                isRequired,
                isRecommend,
                new Hashtable()
            );
        }
        else if(type == 8)
        {
            //apontador para um ficheiro
            writeHTML_FileLink(
                toPrint,
                new StringBuffer(value),
                id ,
                id,
                1,
                isDisabled,
                isVisible,
                false,
                new StringBuffer(),
                isRequired,
                isRecommend,
                fieldName,
                doc,
                new Hashtable()
            );
        }
        toPrint.append("</td></tr>");        
    }
//    public static void writeHTML_lookup(        
//        StringBuffer toPrint,        
//        AttributeHandler atrParent,
//        long variableBoui,
//        long activityBoui,
//        docHTML doc
//        )
//        
//        throws boRuntimeException{
//        
//            boObject varvalue = atrParent.getParent();
//            boObject variable = boObject.getBoManager().loadObject(varvalue.getEboContext(),variableBoui);
//            boObject activity = boObject.getBoManager().loadObject(variable.getEboContext(),activityBoui);
//            renderCommon(toPrint,atrParent,doc,false,false);
//        }    
    public static void writeHTML_lookup(
        StringBuffer clss,
        StringBuffer toPrint,
        boObject objParent,
        AttributeHandler atrParent,
        StringBuffer value,
        StringBuffer name,
        StringBuffer id,
        int tabIndex,
        docHTML doc,
        boolean isDisabled ,
        boolean isVisible ,
        boolean isRequired,
        boolean isRecommend,
        String fielName
        )
        
        throws boRuntimeException{


        long xRefBoui=ClassUtils.convertToLong(value,-1);
        boObject xref=null;
        if(xRefBoui>0)
        {
            xref=boObject.getBoManager().loadObject(doc.getEboContext(),xRefBoui );
            clss.delete(0, clss.length());
            clss.append(xref.getName());
        } 
                
        toPrint.append("<table id='");
        toPrint.append("tblLook");
        toPrint.append(id);
        toPrint.append("' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>");
                        
        if(xRefBoui!=-1 && xRefBoui!=0)
        {
            toPrint.append("<div class='lu ro lui' ");
        }
        else
        {
            toPrint.append("<div style='overflow:hidden' class='lu ro lui' ");
        }
        
        toPrint.append("valido='");                 
        toPrint.append( clss );                 
        toPrint.append("' >");
        

        String  XeoWin32Client_address = null;
        if(!DocumentHelper.isDocument(clss.toString()) || 
            !DocumentHelper.isMSWordFile( xRefBoui==-1||xRefBoui==0?null:doc.getObject( xRefBoui ) ) ||
             XeoWin32Client_address==null ||
            !RegistryHelper.isClientConnected(XeoWin32Client_address))           
        {          
        
            writeHTML_lookupObject(toPrint,
                                                       objParent,
                                                       atrParent,
                                                       value,
                                                       tabIndex,
                                                       doc,
                                                       isDisabled,
                                                       isVisible,
                                                       xRefBoui,
                                                       false,
                                                       fielName,
                                                       clss.toString()
                                                       );
        }
        else
        {                   
            docHTML_renderFields.writeHTML_lookupDocument(toPrint,
                                                          objParent,  
                                                          atrParent,
                                                          value,
                                                          name,
                                                          tabIndex,
                                                          doc,
                                                          isDisabled,
                                                          isVisible,
                                                          xRefBoui,
                                                          clss.toString(),
                                                          false);
        }

        
		toPrint.append("</div>");
        toPrint.append("</td>");
        toPrint.append("<td style='");
        if(xRefBoui<=0 || isDisabled)
        {
            toPrint.append("display=none;");
        }
        toPrint.append("width=16px;'>")
        .append(Messages.getString("GtTemplateViewer.78"))
        .append(
            getOnclick(clss.toString(), "", fielName,clss.toString(), "single", String.valueOf(doc.getDocIdx()), ""))
        .append("' border='0' src='");
        toPrint.append("templates/form/std/remove.gif' width='16px' height='16px'/></td>");
        toPrint.append("<td style='TEXT-ALIGN: right' id='tdRubber' width='25'><img class='lu' id='IMG"+id+"' style='CURSOR: default' tabIndex='");
        toPrint.append(tabIndex);
        
        if ( isDisabled ) {
                toPrint.append("' disabled src='templates/form/std/btn_dis_lookup.gif' lookupstyle='single' ");
        }
        else {
//            if(atrParent.getDefAttribute().getRelationType()==boDefAttribute.RELATION_1_TO_1){
                toPrint.append("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='single' ");
//            }
//            else{
//                toPrint.append("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='multi' ");
//            }
            
            
            if ( !isVisible ) {
                    toPrint.append(" style='display:none' ");
            }
        }
        toPrint.append(" shownew='");
        toPrint.append("1'");
        toPrint.append(" parentBoui='");
//        toPrint.append(objParent.bo_boui);
        toPrint.append("' parentObj='");
        toPrint.append(clss.toString());
        toPrint.append("' parentAttribute='");
        toPrint.append(fielName );
        toPrint.append("' object='");
        toPrint.append(clss);
        toPrint.append("'  docid='");
        toPrint.append(doc.getDocIdx());
        toPrint.append("' lookAction='lookupsingleupdateparam.jsp");
        toPrint.append("' width='21' height='19'><input type='hidden' value='");
        toPrint.append(value);
        toPrint.append("' name='");
        toPrint.append(name);
        toPrint.append("' req='");
        if ( isRequired ) toPrint.append(1);
        else  toPrint.append(0);
        toPrint.append("' boType='lu'>");
        
        
        toPrint.append("</td></tr></tbody></table>");
              
    }
    
    public static void writeHTML_lookupN(
        StringBuffer toPrint,
        boObject objParent,
        bridgeHandler bridge,
        AttributeHandler atrParent,
        StringBuffer Value,
        StringBuffer Name,
        StringBuffer id,
        int tabIndex,
        docHTML doc,
        boolean isDisabled ,
        boolean isVisible ,
        boolean inEditTemplate ,
        boolean isRequired,
        boolean isRecommend,
        boolean showLink, 
        Hashtable xattributes,
        boDefAttribute boDefAttr,
        String lookupDetachField,
        StringBuffer clss,
        String boqlFilter
        )
        
        throws boRuntimeException{
        boolean docs = false;
        boDefAttribute defAttribute = null;
        if(atrParent != null)
        {
            defAttribute = atrParent.getDefAttribute();
        }
        else
        {
            defAttribute = boDefAttr;
        }
        
        toPrint.append("<table id='");
        toPrint.append(id);
        toPrint.append("' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>");
        
        String[] bouis = null;
        int rc = 0;
        if(bridge != null)
        {
            bridge.beforeFirst();                 
            rc=bridge.getRowCount();            
        }
        else
        {             
            if(Value != null && Value.length() > 0)
            {
                bouis = Value.toString().split(";");
                rc = bouis.length;
            }
            
        }

        
        if ( rc>0 )
        {
            boObject o = null;
            String objClassName = null;
            toPrint.append("<div class='lu ro'>");
            String  XeoWin32Client_address = doc.getEboContext().getXeoWin32Client_adress();
            if(bridge != null)
            {
                while ( bridge.next() )
                {                    
                    o = bridge.getObject();
                    objClassName = o.getName();    
                    
                    if(!DocumentHelper.isDocument(objClassName) || 
                        !DocumentHelper.isMSWordFile( o.getBoui()==-1||o.getBoui()==0?null:doc.getObject( o.getBoui() ) ) ||
                         XeoWin32Client_address==null ||
                        !RegistryHelper.isClientConnected(XeoWin32Client_address))           
                    {
                        writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,o.getBoui(), true, Name.toString(), objClassName);
                    }
                    else
                    {
                        docs = true;
                        docHTML_renderFields.writeHTML_lookupDocument(toPrint,objParent,atrParent,Value,Name,tabIndex,doc,isDisabled,isVisible,showLink, o.getBoui(),objClassName, true);
                    }
                }
            }
            else if( bouis != null)
            {
                long xboui = -1;
                for (int i = 0; i < bouis.length; i++) 
                {
                    xboui = ClassUtils.convertToLong(bouis[i],-1);
                    if(xboui != -1)
                    {
                        o = boObject.getBoManager().loadObject(doc.getEboContext(),xboui);
                        objClassName = o.getName();
                        if(!DocumentHelper.isDocument(objClassName))   
                        {
                            writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,o.getBoui(), true, Name.toString(), objClassName);
                        }
                        else // Documents
                        {
                            docs = true;
                            if(DocumentHelper.isMSWordFile( o ) && XeoWin32Client_address!=null && RegistryHelper.isClientConnected(XeoWin32Client_address))
                            {
                                docHTML_renderFields.writeHTML_lookupDocument(toPrint,objParent,atrParent,Value,Name,tabIndex,doc,isDisabled,isVisible,showLink, o.getBoui(),objClassName, true);
                            }   
                            else
                            {
                                writeHTML_lookupObject(toPrint,objParent,atrParent,Value,tabIndex,doc,isDisabled,isVisible,o.getBoui(), true, Name.toString(), objClassName);
                            }
                        }                        
                    }                    
                }            
            }
        }
        else
        {
               toPrint.append("<div style='overflow:hidden' class='lu ro'>");
                toPrint.append("<span class='lui' >");
                
                toPrint.append("</span>");
//                toPrint.append("<input "+(isDisabled ? "disabled":"")+" tabindex="+tabIndex+" original='" + Value + "' style='width:100%;border:0' onblur='this.parentElement.parentElement.parentElement.children[1].firstChild.fromInput(this);' />");
                toPrint.append("<input disabled tabindex="+tabIndex+" original='" + Value + "' style='width:100%;border:0' onblur='this.parentElement.parentElement.parentElement.children[1].firstChild.fromInput(this);' />");

                
        }
		toPrint.append("</div>");
        toPrint.append("</td>");
        //botão de limpar campo
        String auxLookupStyle="multi";
        toPrint.append("<td style='");
        if(rc<=0 || isDisabled)
        {
            toPrint.append("display=none;");
        }
        toPrint.append(" width=16px'>"); 
        //if(DocumentHelper.isDocument(clss.toString()))
        //{
            toPrint.append("<img class='lu' id title='Clique para limpar o campo' onclick='")
            .append(
                getOnclickExplorerChoose(clss.toString(), "", Name.toString(),clss.toString(), "multi", String.valueOf(doc.getDocIdx()), "", Value, true)
                )
            .append("' border='0' src='");
        //}
        //else
        //{
        //    toPrint.append("<img class='lu' id title='Clique para limpar o campo' onclick='")
        //    .append(onclickRemoveButton(clss.toString(), "", Name.toString(),clss.toString(), "multi", String.valueOf(doc.getDocIdx()))
        //        )
        //    .append("' border='0' src='");
        //}
        toPrint.append("templates/form/std/remove.gif' width='16px' height='16px'/></td>");
        //------------------------------
        toPrint.append("<td style='TEXT-ALIGN: right' width='25'><img id style='CURSOR: default' tabIndex='");
        toPrint.append(tabIndex);
        
        if ( isDisabled ) {                
                toPrint.append("' disabled src='templates/form/std/btn_dis_lookup.gif' lookupstyle='multi' ");                
        }
        else {
            toPrint.append("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='multi' ");
            
            
            if ( !isVisible ) {
                    toPrint.append(" style='display:none' ");
            }
        }
        if(!isDisabled)
        {
            
            if(DocumentHelper.isDocument(clss.toString()))
            {
                toPrint.append("onclick='");
                toPrint.append(getOnclickExplorerChoose(clss.toString(), "", Name.toString(),clss.toString(), "multi", String.valueOf(doc.getDocIdx()), "", Value, false));
                toPrint.append("'");
            }
            else
            {
                toPrint.append(" class='lu' ");
                toPrint.append("shownew='");
                toPrint.append("1'");
                if(lookupDetachField != null)
                {
                    toPrint.append(" lookupDetachField='");
                    toPrint.append(lookupDetachField);
                    toPrint.append("'");
                }        
                if(objParent != null)
                {
                    toPrint.append(" parentBoui='").append(objParent.bo_boui).append("' ");
                    toPrint.append(" parentObj='");
                    toPrint.append(objParent.getName());
                }
                else
                {
                    toPrint.append(" parentBoui='' ");
                    toPrint.append(" parentObj='");
                    toPrint.append(clss.toString());
                }
                toPrint.append("' options='_");
                toPrint.append(Value.toString() );
                if(boqlFilter != null && boqlFilter.length() > 0)
                {
                    toPrint.append("' lookupQuery='").append(boqlFilter);
                }
                toPrint.append("' lookAction='lookupmultiupdateparam.jsp");
                toPrint.append("' parentAttribute='");
                toPrint.append(Name.toString() );
                toPrint.append("' object='");
                toPrint.append(clss);
                toPrint.append("'  docid='");
                toPrint.append(doc.getDocIdx());
                toPrint.append("'");
            }
        }
        toPrint.append(" width='21' height='19'/><input original='" +Value + "' type='hidden' value='");
        toPrint.append(Value);
        toPrint.append("' name='");
        toPrint.append(Name);
        toPrint.append("' object='");
        toPrint.append(clss);
        toPrint.append("' req='");
        
        if ( isRequired )  toPrint.append(1);
        else  toPrint.append(0);
        
        toPrint.append("' boType='lu'/>");
        toPrint.append("</td>");        
        toPrint.append("</tr></tbody></table>");

        if(inEditTemplate){
            //toPrint.append("<img src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
        }
        
    }
    
     private static String onclickRemoveButton(String parentObjName, String parentBoui, String parentAtt, String objName, 
        String lookupStyle, String docID)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("winmain().openDoc(\"tall\", \"")
        .append(objName)
        .append("\", \"\", \"\", \"lookup\", \"lookup")
        .append(lookupStyle)
        .append(".jsp?look_object=")
        .append(objName)
        .append("&showNew=false&docid=")
        .append(docID)
        .append("&fromSection=y&clientIDX=\"+getIDX()+\"")
        .append("&look_parentObj=")
        .append(parentObjName)
        .append("&look_parentBoui=")
        .append(parentBoui)
        .append("&searchString=b:&look_parentAttribute=")
        .append(parentAtt)
        .append("&look_action=lookupsingleupdateparam.jsp")
        .append("\");");
        return sb.toString();
    }
    
    private static String getOnclick(String parentObjName, String parentBoui, String parentAtt, String objName, 
        String lookupStyle, String docID, String idx)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("winmain().openDoc(\"tall\", \"")
        .append(objName)
        .append("\", \"\", \"\", \"lookup\", \"lookup")
        .append(lookupStyle)
        .append(".jsp?look_object=")
        .append(objName)
        .append("&showNew=false&docid=")
        .append(docID)
        .append("&fromSection=y&clientIDX=\"+getIDX()+\"")
        .append("&look_parentObj=")
        .append(parentObjName)
        .append("&look_parentBoui=")
        .append(parentBoui)
        .append("&searchString=b:&look_parentAttribute=")
        .append(parentAtt)
        .append("&look_action=lookupsingleupdateparam.jsp")
        .append("\");");
        return sb.toString();
    }
    
    private static void renderSeparatorLabel(StringBuffer toPrint, String label, String helpUrl, long boui)  throws boRuntimeException
    {
        if(helpUrl == null || "".equals(helpUrl))
        {
//              Visualizar Modelo foi comentado por causa de um erro
//            toPrint.append("<TR><TD colspan=5 nowrap style=\"border-bottom:2 groove\" ><B>")
//            .append(label)
//            .append("<img style='cursor:hand' onclick='window.top.XEOControl.DocumentManager.PreviewFile("+boui+")' align='absmiddle' src='resources/gestaoDocumental/visualizar.gif' width='16' height='16'")
//            .append("alt='Clique para visualizar o Modelo' border='0' hspace='6'>")
//            .append("</B></TD></TR>");
            toPrint.append("<TR><TD colspan=5 nowrap style=\"border-bottom:2 groove\" ><B>")
            .append(label)
            .append("</B></TD></TR>");
        }
        else
        {
//              Visualizar Modelo foi comentado por causa de um erro
//            toPrint.append("<TR>")
//            .append("<TD colspan=5 >")
//            .append("<table style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'>")
//            .append("<TR>")
//            .append("<TD nowrap style='width:100%;border-bottom:2 groove'><B>")
//            .append(label)
//            .append("<img style='cursor:hand' onclick='window.top.XEOControl.DocumentManager.PreviewFile("+boui+")' align='absmiddle' src='resources/gestaoDocumental/visualizar.gif' width='16' height='16'")
//            .append("alt='Clique para visualizar o Modelo' border='0' hspace='6'>")
//            .append("</B></TD>")
//            .append("<TD nowrap align='right' style='width:185px;border-bottom:2 groove'><DIV onclick='window.open(\"")
//            .append(helpUrl)
//            .append("\");' style='WIDTH:185px;HEIGHT:30px;COLOR:#4A869C;FONT:normal normal x-small verdana;' >")
//            .append("Clique para obter ajuda<img align='absmiddle' src='resources/help.gif' width='17' height='17'")
//            .append("alt='Imagem de um ponto de interrogação que nos apresenta uma nota explicativa dos modelos,")
//            .append("se selecionada' border='0' hspace='6'></DIV></TD>")
//            .append("</TR></TABLE></TD></TR>");
            
            toPrint.append("<TR>")
            .append("<TD colspan=5 >")
            .append("<table style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'>")
            .append("<TR>")
            .append("<TD nowrap style='width:100%;border-bottom:2 groove'><B>")
            .append(label)
            .append("</B></TD>")
            .append("<TD nowrap align='right' style='width:185px;border-bottom:2 groove'><DIV onclick='window.open(\"")
            .append(helpUrl)
            .append("\");' style='WIDTH:185px;HEIGHT:30px;COLOR:#4A869C;FONT:normal normal x-small verdana;' >")
            .append(Messages.getString("GtTemplateViewer.2"))
            .append(Messages.getString("GtTemplateViewer.3"))
            .append(Messages.getString("GtTemplateViewer.1"))
            .append("</TR></TABLE></TD></TR>");
        }
    }
    private static void renderInfo(StringBuffer toPrint, String desc)  throws boRuntimeException
    {
        if(desc != null && !"".equals(desc))
        {
            toPrint.append("<TR><TD style=\"border-bottom:2 groove\">&nbsp;</td><TD colspan=2 style=\"border-bottom:2 groove\" >")
            .append("<DIV style='background-color:#FFFFCC;WIDTH:100%;COLOR:#4A869C;FONT:normal normal x-small verdana;'>")
            .append(desc).append("<br><br>")        
            .append("</div></TD><td style=\"border-bottom:2 groove\">&nbsp;</td><td style=\"border-bottom:2 groove\">&nbsp;</td></TR>");
        }
    }
    
    private static void renderQuestion(StringBuffer toPrint, String label, boolean isRequired)  throws boRuntimeException
    {
        toPrint.append("<TR><TD>&nbsp;</td><TD colspan=4 nowrap ");
        if(isRequired)
        {
            toPrint.append("style='FONT-WEIGHT: bold; color:#990000'");
        }
        toPrint.append(">");
        toPrint.append(label);        
        toPrint.append("</TD></TR>");        
    }
    
    private static boolean hasMoreThanOneOption(GtTemplate template)
    {
        int v = 0;
        if(template.getTempCarta() != null || template.getRostoCarta() != null) v++;
        if(template.getTempFax() != null || template.getRostoFax() != null) v++;
        if(template.getTempEmail() != null && template.getTempEmail().length() > 0) v++;
        if(template.getTempSMS() != null && template.getTempSMS().length() > 0) v++;
        if(v > 1)
        {
            return true;
        }
        return false;
    }
    
    private static void renderChannelChecks(EboContext boctx, StringBuffer toPrint, GtTemplate template, boolean disabled) throws boRuntimeException
    {
        boolean carta = (template.getTempCarta() != null || template.getRostoCarta() != null);
        boolean fax = (template.getTempFax() != null || template.getRostoFax() != null);
        boolean email = (template.getTempEmail() != null && template.getTempEmail().length() > 0);
        boolean sms = (template.getTempSMS() != null && template.getTempSMS().length() > 0);
        byte channel = template.getChannel();
        if(channel == -1)
        {
            if(carta) channel = GtTemplate.TYPE_CARTA;
            else if(fax) channel = GtTemplate.TYPE_FAX;
            else if(email) channel = GtTemplate.TYPE_EMAIL;
            else if(sms) channel = GtTemplate.TYPE_SMS;
        }
        toPrint.append("<TR><TD>&nbsp;</td><TD colspan=4>");
        if(carta)
        {
            StringBuffer value = new StringBuffer("");
            if((!fax && !email && !sms) || channel == GtTemplate.TYPE_CARTA)
            {
                value.append("1"); 
            }
            docHTML_renderFields.writeHTML_forBooleanAsCheck(
                Messages.getString("GtTemplateViewer.205"),
                toPrint,
                value,
                new StringBuffer("letter_channel"),
                new StringBuffer("letter_channel"),
                1,
                disabled,
                true,
                false,
                new StringBuffer("uncheckOthers(\"letter_channel\");"),
                false,
                false,
                new Hashtable()
            );
            toPrint.append("&nbsp;&nbsp;&nbsp;");
        }
        if(fax)
        {
            StringBuffer value = new StringBuffer("");
            if((!carta && !email && !sms) || channel == GtTemplate.TYPE_FAX)
            {
                value.append("1"); 
            }
            docHTML_renderFields.writeHTML_forBooleanAsCheck(
                Messages.getString("GtTemplateViewer.212"),
                toPrint,
                value,
                new StringBuffer("fax_channel"),
                new StringBuffer("fax_channel"),
                1,
                disabled,
                true,
                false,
                new StringBuffer("uncheckOthers(\"fax_channel\");"),
                false,
                false,
                new Hashtable()
            );
            toPrint.append("&nbsp;&nbsp;&nbsp;");
        }
        if(email)
        {
            StringBuffer value = new StringBuffer("");
            if((!fax && !carta && !sms) || channel == GtTemplate.TYPE_EMAIL)
            {
                value.append("1"); 
            }
            docHTML_renderFields.writeHTML_forBooleanAsCheck(
                Messages.getString("GtTemplateViewer.219"),
                toPrint,
                value,
                new StringBuffer("email_channel"),
                new StringBuffer("email_channel"),
                1,
                disabled,
                true,
                false,
                new StringBuffer("uncheckOthers(\"email_channel\");"),
                false,
                false,
                new Hashtable()
            );
            toPrint.append("&nbsp;&nbsp;&nbsp;");
        }
        if(sms)
        {
            StringBuffer value = new StringBuffer("");
            if((!fax && !email && !carta) || channel == GtTemplate.TYPE_SMS)
            {
                value.append("1"); 
            }
            docHTML_renderFields.writeHTML_forBooleanAsCheck(
                Messages.getString("GtTemplateViewer.226"),
                toPrint,
                value,
                new StringBuffer("sms_channel"),
                new StringBuffer("sms_channel"),
                1,
                disabled,
                true,
                false,
                new StringBuffer("uncheckOthers(\"sms_channel\");"),
                false,
                false,
                new Hashtable()
            );
        }
        if(disabled && !template.hasSelected())
        {
            template.setTypeValues(boctx, carta, fax, email, sms);
        }
        
        toPrint.append("</td></TR>");
        
    }
    private static void renderJustification(docHTML doc, StringBuffer toPrint,boObject activity, boolean disabled, long boui) throws boRuntimeException
    {
        Hashtable attributes = new Hashtable();
        AttributeHandler attrHandler = activity.getAttribute("justification");
        StringBuffer nameH = new StringBuffer();
        StringBuffer id = new StringBuffer();
        nameH.append( activity.getName() ).append( "__" ).append( activity.bo_boui ).append("__justification");
        id.append("tblLook").append( activity.getName() ).append( "__" ).append( activity.bo_boui ).append("__justification");
        
        renderSeparatorLabel(toPrint,attrHandler.getDefAttribute().getLabel(), null, boui);
        
        toPrint.append("<TR><TD colspan=5>&nbsp;</TD></TR>");
        
        
        toPrint.append("<TR>");
        toPrint.append("<td colspan='5' style='padding-left:30px;padding-right:30px'>");                   

        attributes.put("height","250px");
        docHTML_renderFields.writeHTML_forHTMLEDITOR(toPrint,
                                                     activity,
                                                     attrHandler,
                                                     doc,
                                                     new StringBuffer(attrHandler.getValueString()),
                                                     nameH,
                                                     id,
                                                     1,
                                                     disabled,
                                                     true,
                                                     false,
                                                     new StringBuffer(""),
                                                     false,
                                                     true,
                                                     attributes,
                                                    "html"
                                                     );  
        toPrint.append("</td>");     
        toPrint.append("</TR>");
    }
    private static boolean isDisabled(boObject activity, boObject variable,boolean forceDisable) throws boRuntimeException
    {
        boolean result = false;
        if(forceDisable)
        {
            result = true;
        }
        else
        {
            long mode = variable.getAttribute("mode").getValueLong();   
            if(mode == 0)
            {
                result = true;
            }
    
            String input = variable.getAttribute("input").getValueString();   
            if("1".equals(input))
            {
                result = false;
            }
            if(activity != null)
            {
                boObjectStateHandler pstate = activity.getStateAttribute( "runningState" );
                if (pstate != null && pstate.getValueString().equals("close"))
                {        
                    result = true;
                }                    
            }
        }
        return result;
    }
//    private static void renderTableStart(StringBuffer toPrint,int renderMode)
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
    private static void renderGenerateButton(StringBuffer toPrint, String generateType)
    {
        String labelButton = Messages.getString("GtTemplateViewer.0");
        toPrint.append("<tr height=\"10px\"></tr>");
        toPrint.append("<tr></tr>");
        toPrint.append("<tr height=\"20px\">")
        .append("<td>&nbsp;</td>")
        .append("<td>&nbsp;</td>")
        .append("<td align=\"right\">");
        if("generateAnexos".equals(generateType))
        {
            labelButton = Messages.getString("GtTemplateViewer.264");
        }
        else if("listFill".equals(generateType))
        {
            labelButton = Messages.getString("GtTemplateViewer.266");
        }
        toPrint.append(Messages.getString("GtTemplateViewer.471")+generateType+"');\" onkeypress=\"keyPressed(event, '"+generateType+"');\">"+labelButton+"</button></p></td><td></td><td></td></tr>");
        
    }
    
    private static void renderTypeButton(StringBuffer toPrint)
    {
        toPrint.append("<tr height=\"10px\"></tr>");
        toPrint.append("<tr></tr>");
        toPrint.append("<tr height=\"20px\">")
        .append("<td>&nbsp;</td>")        
        .append("<td align=\"right\">")
        .append("<button tabIndex='2' style='height:100%;width:150px;' value = \"OK\" onMouseDown=\"mouseDown(event, 'typeSelected');\" onkeypress=\"keyPressed(event, 'typeSelected');\">OK</button></p></td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
    }
    
    private static void renderLetterTypeButton(StringBuffer toPrint)
    {
        toPrint.append("<tr height=\"10px\"></tr>");
        toPrint.append("<tr></tr>");
        toPrint.append("<tr height=\"20px\">")
        .append("<td>&nbsp;</td>")        
        .append("<td align=\"right\">")
        .append("<button tabIndex='2' style='height:100%;width:150px;' value = \"OK\" onMouseDown=\"mouseDown(event, 'letterTypeSelected');\" onkeypress=\"keyPressed(event, 'letterTypeSelected');\">OK</button></p></td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
    }
    
    private static void renderTableSpace(StringBuffer toPrint)
    {
        toPrint.append("<tr><td colspan='5'><br></td></tr>");
    }
  
    public static Element getEmptyPreview() throws boRuntimeException
    {
        Element preview = new Preview(null,"preview",null);
        return preview;
    }
    
     public static void writeHTML_lookupObject(StringBuffer toPrint,
                                              boObject objParent,
                                              AttributeHandler atrParent,
                                              StringBuffer Value,
                                              int tabIndex,
                                              docHTML doc,
                                              boolean isDisabled ,
                                              boolean isVisible ,
                                              long xRefBoui , 
                                              boolean isMulti,
                                              String attName,
                                              String referenceObj
                                              ) throws boRuntimeException
    {
       
        toPrint.append("<span class='lui' onclick=\"");
        StringBuffer toPass = new StringBuffer();
        //openDoc("medium","activity","edit","method=edit&boui=1264",null,null,null);
        boObject xref=null;
        boolean sameWindow = false;
        if(xRefBoui!=-1 && xRefBoui!=0)
        {
//             xref=objParent.getBoManager().loadObject(objParent.getEboContext(),xRefBoui );
             xref = boObject.getBoManager().loadObject(doc.getEboContext(),xRefBoui );
        }
        if ( xref != null )
        {
            if ( xref.getBoDefinition().getBoCanBeOrphan()  && 
                (xref.exists() || ("XwfController".equalsIgnoreCase(doc.getController().getName()) && ((XwfController)doc.getController()).getEngine().getExecutionMode() == xwfHelper.PROGRAM_EXEC_TEST_MODE)) 
            )
            {
                toPrint.append("winmain().openDoc('medium','");
            }
            else
            {
                toPrint.append("winmain().newPage(getIDX(),'"); 
//                sameWindow = true;
            }
        }
        else
        {
            toPrint.append("winmain().openDoc('medium','");    
        }
        
        
       
        toPrint.append(referenceObj.toLowerCase() );
        
        toPrint.append("','edit','method=edit&boui=");
        if ( xref!= null )
        {
            toPrint.append( xref.getBoui() );
            toPass.append("boui=").append( xref.getBoui() );
        }
        else
        {
            toPrint.append( Value );
            toPass.append("boui=").append( Value );
        }
        if(objParent != null)
        {
            toPrint.append("&actRenderObj=");
            toPrint.append(objParent.bo_boui);
            toPass.append("&actRenderObj=").append( objParent.bo_boui );
        }
        
         if ( !(sameWindow  ||  ( xref!= null ) )  )
        {
                toPrint.append("&actRenderDocid=");
                toPrint.append( doc.getDocIdx() );
                toPass.append("&actRenderDocid=").append( doc.getDocIdx() );
                
//                toPrint.append("&docid=").append( doc.getDocIdx() );
                
                toPrint.append("&actRenderAttribute=");
                toPrint.append( attName);
                toPrint.append("&actIdxClient='+getIDX()+'");
                toPass.append("&actRenderAttribute=").append( attName )
                    .append("&actIdxClient='+getIDX()+'");
        }

              
//         if ( sameWindow  ||  ( xref!= null && !xref.exists() ) )
//        {
//            
//             toPrint.append("&parentAttribute=").append(attName)
//                .append("&relatedClientId=").append(doc.getDocIdx() )
//                .append("&ctxParent=").append( objParent.getBoui() )
//                .append("&ctxParentIdx=").append( doc.getDocIdx() )
//                .append("&addToCtxParentBridge=").append( attName )
//                .append("&docid=").append( doc.getDocIdx() );
//             toPass.append("&parentAttribute=").append(attName)
//                .append("&relatedClientId=").append(doc.getDocIdx() )
//                .append("&ctxParent=").append( objParent.getBoui() )
//                .append("&ctxParentIdx=").append( doc.getDocIdx() )
//                .append("&addToCtxParentBridge=").append( attName )
//                .append("&docid=").append( doc.getDocIdx() );
//            
//        }
        
        //PODE ESTAR NO 2?
        toPrint.append("&actRenderAttribute=");
        toPrint.append( attName);
        toPrint.append("&actRenderDocid=");
        toPrint.append( doc.getDocIdx() );
        toPrint.append("&actIdxClient='+getIDX()+'");
        
        toPass.append("&actRenderAttribute=");
        toPass.append( attName);
        toPass.append("&actRenderDocid=");
        toPass.append( doc.getDocIdx() );
        toPass.append("&actIdxClient='+getIDX()+'");
        

      
        toPrint.append("')\"");

        toPrint.append(" boui='");
        toPrint.append(Value);
        toPrint.append("' object='");
        toPrint.append(referenceObj);
        toPrint.append("'>");

        if(xRefBoui!=-1 && xRefBoui!=0){
             toPrint.append(xref.getCARDIDwLink(true, false, toPass.toString()));
             toPrint.append("</span>");
        }
        else
        {
            toPrint.append("</span>");
            if ( !isDisabled )
            {
                    toPrint.append("<input tabindex="+tabIndex+" original='" + Value + "' style='width:100%;border:0' onblur='this.parentElement.parentElement.parentElement.children[2].firstChild.fromInput(this);' />");
            }
        }    
    }
    
    
    public static String writeJS() throws boRuntimeException
    {
        StringBuffer result = new StringBuffer();
        result.append("<script LANGUAGE=\"javascript\" SRC=\"xeo.js\"></script>")
        .append("<script LANGUAGE=\"javascript\" SRC=\"xeo.js\"></script>");
        
        return result.toString();
    }
    
    public static String getBoui(String objName, String attName, String xml)
    {
        try
        {
            ngtXMLHandler xmlHandler = new ngtXMLHandler(xml);
            ngtXMLHandler[] childnodes = xmlHandler.getChildNodes();
            String valueBoui = childnodes[0].getChildNodes()[0].getText();
            if(valueBoui != null && !"".equals(valueBoui))
            {
                return valueBoui;
            }
        }
        catch (Exception e)
        {
            //ignore
        }
        return null;
    }
    
    public static String[] getBouis(String objName, String attName, String xml)
    {
        try
        {
            ngtXMLHandler xmlHandler = new ngtXMLHandler(xml);
            ngtXMLHandler[] childnodes = xmlHandler.getChildNodes();
            String valueBoui = childnodes[0].getChildNodes()[0].getText();
            if(valueBoui != null && !"".equals(valueBoui))
            {
                return valueBoui.split(";");
            }
        }
        catch (Exception e)
        {
            //ignore
        }
        return null;
    }

    public static void writeHTML_text(
        StringBuffer toPrint,
        StringBuffer Value,
        StringBuffer Name,
        StringBuffer id,
        int tabIndex,
        boolean isDisabled ,
        boolean isVisible ,
        boolean inEditTemplate,
        StringBuffer onChange,
        boolean isRequired,
        boolean isRecommend,
        int charLen,
        String fielName,
        docHTML doc,
        long textosBoui,
        Hashtable xattributes,
        boolean moreThanOne
        ){

        toPrint.append("<table border='0' id='");
        toPrint.append("fieldValuesCall");
        toPrint.append(id);
        toPrint.append("' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='1' height='100%' width='100%'><tbody><tr><td width='100%'>");
        if(charLen >= 4000)
        {
            toPrint.append("<textarea style='height=100%' maxlength='" + charLen + "' class='text' ");
        }
        else
        {
            String h = (String)xattributes.get("height");
            String w = (String)xattributes.get("width");
            
            if(h == null || "".equals(h))
            {
                toPrint.append("<textarea style='height=20px' maxlength='" + charLen + "' class='text' ");
            }
            else
            {
                if(w == null || "".equals(w))
                {
                    toPrint.append("<textarea style='height=100%' maxlength='" + charLen + "' class='text' ");
                }
                else
                {
                    toPrint.append("<textarea style='height=100%;width="+w+"'  maxlength='" + charLen + "' class='text' ");
                }
            }
        }
        
        toPrint.append("' id='viewer");
        toPrint.append( id );
        toPrint.append('\'');
        
        if ( isRequired )
        {
            toPrint.append(" req=1 ");
        }
        
        if ( isRecommend )
        {
            toPrint.append(" rcm=1 ");
        }
        
//        if ( isDisabled )
//        {
            toPrint.append(" disabled  ");
//        }
        
        if ( !isVisible )
        {
            toPrint.append(" style='display:none' ");
        }
           
        //focus
        if(isVisible && !isDisabled)
            toPrint.append(getonfocus(Name.toString()));
           
        if ( onChange.length()>0 )
        {
            toPrint.append(" onchange='");
            toPrint.append(onChange);
            toPrint.append('\'');
        }
        
        StringBuffer descritivo = new StringBuffer("");
        if(Value != null && Value.length() > 0)
        {
            String[] bouis = Value.toString().split(";");
            long b;
            boObject o;
            for (int i = 0; i < bouis.length; i++) 
            {
                try
                {
                    b = Long.parseLong(bouis[i]);
                    o = boObject.getBoManager().loadObject(doc.getEboContext(), b);
                    if(moreThanOne)
                        descritivo.append(i+1).append(" - ");
                    descritivo.append(o.getAttribute("texto").getValueString());
                    if((i+1) < bouis.length)
                        descritivo.append(" / ");
                }
                catch(Exception e){/*ignore*/}
                }
        }
        
        toPrint.append(" ONKEYPRESS=\" return verifySize(this, ");
        toPrint.append(charLen).append("); \"");
        toPrint.append(" onbeforepaste=\" return doBeforePaste(this, ");
        toPrint.append(charLen).append("); \"");
        toPrint.append(" onpaste=\" return doPaste(this, ");
        toPrint.append(charLen).append("); \"");
        toPrint.append(" name = 'viewer");
        toPrint.append( Name );
        toPrint.append("' tabindex='"+tabIndex+"'/>");
        toPrint.append(descritivo.length() == 0 ? Value:descritivo);
        toPrint.append("</textarea>");
        //hidden que passa o valor
        toPrint.append("<INPUT type='hidden' name='");
        toPrint.append(Name);
        toPrint.append("' id='");
        toPrint.append(id);
        toPrint.append("' value='");
        toPrint.append(Value);
        toPrint.append("'/>");

        toPrint.append("</td><td align='left' valign='bottom' width='22px'>");
        
        StringBuffer onClick = null;
        if(moreThanOne)
        {
            onClick = new StringBuffer("winmain().openDoc(\"tall\",\"\",\"\",\"\",\"lookup\",\"lookupmultiText.jsp?lookupTextBoui=");
        }
        else
        {
            onClick = new StringBuffer("winmain().openDoc(\"tall\",\"\",\"\",\"\",\"lookup\",\"lookupText.jsp?lookupTextBoui=");
        }
        onClick.append(textosBoui)
            .append("&docid=")
            .append(doc.getDocIdx())
            .append("&fieldID="+id.toString()+"&clientIDX=\"+getIDX()+\"&bouisValues=\"+document.getElementById(\""+id.toString()+"\").value);");
        toPrint.append("<img id='IMG"+id+"' style='CURSOR: default' tabIndex='");
        toPrint.append(tabIndex);
        if ( isDisabled ) {
            toPrint.append("' disabled src='templates/form/std/btn_dis_lookup.gif' ");
        }
        else {
            toPrint.append("' src='templates/form/std/btn_off_lookup.gif' ");
            if ( !isVisible ) {
                    toPrint.append(" style='display:none' ");
            }
        }
        toPrint.append("attributeName='");
        toPrint.append(fielName );
        toPrint.append("'  fieldID='");
        toPrint.append(id.toString());
        toPrint.append("'  docid='");
        toPrint.append(doc.getDocIdx());
        toPrint.append("' width='21' height='19' onclick='"+onClick.toString()+"'><input type='hidden' value='");
        toPrint.append(1);
        toPrint.append("' name='");
        toPrint.append(1);
        toPrint.append("' req='");
        if ( isRequired ) toPrint.append(1);
        else  toPrint.append(0);
        toPrint.append("' boType='lu'>");
        toPrint.append("</td></tr></tbody></table>");
	}    
    private static String getonfocus(String fieldName)
    {
        return " onfocus='setFieldWFocus(\"" + fieldName + "\")' ";
    }
    
    public static void writeHTML_FileLink(
        StringBuffer toPrint,
        StringBuffer Value,
        StringBuffer Name,
        StringBuffer id,
        int tabIndex,
        boolean isDisabled ,
        boolean isVisible ,
        boolean inEditTemplate,
        StringBuffer onChange,
        boolean isRequired,
        boolean isRecommend,
        String fielName,
        docHTML doc,
        Hashtable xattributes
        ){

//        StringBuffer value=new StringBuffer();
        toPrint.append("<table id='table_")
        .append(id)
        .append("' style='TABLE-LAYOUT:fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>")
        .append("<input type='file' style='width:100%' value='").append(Value.toString()).append("' name='")
        .append(id)
        .append("'").append(" id='").append(id).append("'>");
        toPrint.append("</tr></tbody></table>");
	}  
    public static void open(boObject object) throws boRuntimeException
    {
//        try
//        {                                     
//            String xeoWin32ClientAddress = (String)object.getEboContext().getRequest().getSession().getAttribute("XeoWin32Client_address");
//            if(xeoWin32ClientAddress != null)
//            {
//                boSession bosession = (boSession)object.getEboContext().getRequest().getSession().getAttribute("boSession");                
//                ClientEvents client = ServiceHelper.getClient(new InitialContext(),xeoWin32ClientAddress);
//                if(object.getAttribute("file") != null)
//                {
//                    iFile file = object.getAttribute("file").getValueiFile();
//                    client.open(bosession.getId(),file.getName(),object.getBoui());
//                }
//            }
//        }
//        catch (NamingException e)
//        {            
//            e.printStackTrace();
//        }
//        catch (RemoteException e)
//        {            
//            e.printStackTrace();
//        }
    }
    
    private static void renderFaxChecks(EboContext boctx, StringBuffer toPrint, GtTemplate template, boolean disabled) throws boRuntimeException
    {
        boolean urgent = template.isUrgent();
        boolean review = template.isReview();
        boolean coment = template.isComent();
        boolean answer = template.isAnswer();
        
        toPrint.append("<TR><TD>&nbsp;</td><TD colspan=4>");
        
        docHTML_renderFields.writeHTML_forBooleanAsCheck(
            Messages.getString("GtTemplateViewer.394"),
            toPrint,
            urgent ? new StringBuffer("1"):new StringBuffer(""),
            new StringBuffer("fax_urgent"),
            new StringBuffer("fax_urgent"),
            1,
            disabled,
            true,
            false,
            new StringBuffer(""),
            false,
            false,
            new Hashtable()
        );
        toPrint.append("&nbsp;&nbsp;&nbsp;");
       
        docHTML_renderFields.writeHTML_forBooleanAsCheck(
            Messages.getString("GtTemplateViewer.401"),
            toPrint,
            review ? new StringBuffer("1"):new StringBuffer(""),
            new StringBuffer("fax_review"),
            new StringBuffer("fax_review"),
            1,
            disabled,
            true,
            false,
            new StringBuffer(""),
            false,
            false,
            new Hashtable()
        );
        toPrint.append("&nbsp;&nbsp;&nbsp;");
        
        docHTML_renderFields.writeHTML_forBooleanAsCheck(
            Messages.getString("GtTemplateViewer.408"),
            toPrint,
            coment ? new StringBuffer("1"):new StringBuffer(""),
            new StringBuffer("fax_coment"),
            new StringBuffer("fax_coment"),
            1,
            disabled,
            true,
            false,
            new StringBuffer(""),
            false,
            false,
            new Hashtable()
        );
        toPrint.append("&nbsp;&nbsp;&nbsp;");
        
        docHTML_renderFields.writeHTML_forBooleanAsCheck(
            Messages.getString("GtTemplateViewer.415"),
            toPrint,
            answer ? new StringBuffer("1"):new StringBuffer(""),
            new StringBuffer("fax_answer"),
            new StringBuffer("fax_answer"),
            1,
            disabled,
            true,
            false,
            new StringBuffer(""),
            false,
            false,
            new Hashtable()
        );

//        if(disabled && !template.hasSelectedFaxType())
//        {
//            template.setFaxTypeValues(boctx, urgent, review, coment, answer);
//        }

        toPrint.append("</td></TR>");
        
    }
    private static void renderFaxButton(StringBuffer toPrint)
    {
        toPrint.append("<tr height=\"10px\"></tr>");
        toPrint.append("<tr></tr>");
        toPrint.append("<tr height=\"20px\">")
        .append("<td>&nbsp;</td>")        
        .append("<td align=\"right\">")
        .append("<button tabIndex='2' style='height:100%;width:150px;' value = \"OK\" onMouseDown=\"mouseDown(event, 'faxTypeSelected');\" onkeypress=\"keyPressed(event, 'faxTypeSelected');\">")
        .append( Messages.getString( "GtTemplateViewer.465" )   )
        .append("</button></p></td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
    }
    
    private static void renderListButton(StringBuffer toPrint)
    {
        toPrint.append("<tr height=\"10px\"></tr>");
        toPrint.append("<tr></tr>");
        toPrint.append("<tr height=\"20px\">")
        .append("<td>&nbsp;</td>")        
        .append("<td align=\"right\">")
        .append(Messages.getString("GtTemplateViewer.472"));
    }
    
    private static String getOnclickExplorerChoose(String parentObjName, String parentBoui, String parentAtt, String objName, 
        String lookupStyle, String docID, String idx, StringBuffer values, boolean cleanField)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("winmain().openDoc(\"tall\", \"")
        .append(objName)
        .append("\", \"\", \"\", \"lookup\", \"__explorerChooseWorkPlace.jsp")
        .append(Messages.getString("GtTemplateViewer.473"))
        .append("&look_object=")
        .append(objName)
        .append("&showNew=false&docid=")
        .append(docID)
        .append("&fromSection=y&clientIDX=\"+getIDX()+\"")
        .append("&look_parentObj=")
        .append(parentObjName)
        .append("&look_parentBoui=")
        .append(parentBoui);
        if(cleanField)
        {
            sb.append("&searchString=b:");
        }
        sb.append("&look_parentAttribute=")
        .append(parentAtt)
        .append("&type=")
        .append(lookupStyle)
        .append("&look_action=lookupmultiupdateparam.jsp");
        if(values != null && values.length() > 0)
        {
            sb.append("&values=").append(values.toString());
        }
        sb.append("\");");
        return sb.toString();
    }
    
    private static void renderLetterChecks(EboContext boctx, StringBuffer toPrint, GtTemplate template, boolean disabled) throws boRuntimeException
    {
        boolean registada = template.isRegistada();
        boolean aviso = template.isAviso();
        boolean simples = template.isSimples();
        StringBuffer value = new StringBuffer("");
        toPrint.append("<TR><TD>&nbsp;</td><TD colspan=4>");
        docHTML_renderFields.writeHTML_forBooleanAsCheck(
            Messages.getString("GtTemplateViewer.450"),
            toPrint,
            simples ? new StringBuffer("1"):new StringBuffer(""),
            new StringBuffer("simples_letter"),
            new StringBuffer("simples_letter"),
            1,
            disabled,
            true,
            false,
            new StringBuffer("uncheckOthers(\"simples_letter\");"),
            false,
            false,
            new Hashtable()
        );
        toPrint.append("&nbsp;&nbsp;&nbsp;");
            
        docHTML_renderFields.writeHTML_forBooleanAsCheck(
            Messages.getString("GtTemplateViewer.457"),
            toPrint,
            registada ? new StringBuffer("1"):new StringBuffer(""),
            new StringBuffer("registada_letter"),
            new StringBuffer("registada_letter"),
            1,
            disabled,
            true,
            false,
            new StringBuffer("uncheckOthers(\"registada_letter\");"),
            false,
            false,
            new Hashtable()
        );
        toPrint.append("&nbsp;&nbsp;&nbsp;");

        docHTML_renderFields.writeHTML_forBooleanAsCheck(
            Messages.getString("GtTemplateViewer.464"),
            toPrint,
            aviso ? new StringBuffer("1"):new StringBuffer(""),
            new StringBuffer("aviso_letter"),
            new StringBuffer("aviso_letter"),
            1,
            disabled,
            true,
            false,
            new StringBuffer("uncheckOthers(\"aviso_letter\");"),
            false,
            false,
            new Hashtable()
        );

        toPrint.append("</td></TR>");
        
    }
}