package netgest.bo.impl.document.merge.gestemp;

import netgest.bo.impl.document.print.RemoteFileConversion;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefInterface;
import netgest.bo.dochtml.docHTML;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.impl.document.merge.resultSet.MergeResultSetBoObject;
import netgest.bo.impl.document.print.PrintJob;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.message.MessageServer;
import netgest.bo.message.server.mail.MailUtil;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.utils.DestroyBusinessObject;
import netgest.io.FSiFile;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;
import netgest.utils.ClassUtils;
import netgest.utils.TempFile;
import netgest.xwf.common.xwfFunctions;
import netgest.xwf.core.xwfManager;
import netgest.xwf.core.xwfMessage;
import netgest.bo.runtime.boConvertUtils;
import org.apache.log4j.Logger;

//TODO:Implement Interface LUSITANIA
//import pt.lusitania.events.Message;
//import pt.lusitania.gd.ctt.Codigo4Estados;
//import pt.lusitania.gd.ctt.NumeroRegistado;

public class Helper
{
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.impl.document.merge.gestemp.Helper"); //$NON-NLS-1$
    private static final String SOFT_HYPEN = ""+(char)173; //$NON-NLS-1$
    private static final String WORD_TAG = ""+(char)166; //$NON-NLS-1$
    private static Random RANDOM = new Random();

    public Helper()
    {
    }

    public static int getSqlTypeFromGesDocTipoCampoLov(int gesdocTipoCamposLOV, byte templateType, Object returnedObj)
    {
        if(GtTemplate.TEXT_TEMPLATE == templateType || (returnedObj != null && returnedObj instanceof String))
        {
            return Types.VARCHAR;
        }
        return getSqlTypeFromGesDocTipoCampoLov(gesdocTipoCamposLOV);
    }

    private static int getSqlTypeFromGesDocTipoCampoLov(int gesdocTipoCamposLOV)
    {
        switch(gesdocTipoCamposLOV)
        {
            case 1:
            case 5:
            case 2:
            case 3:
            case 7:
                 return Types.VARCHAR;
//            case 3:
//                 return Types.NUMERIC;
            case 4:
            case 6:
                 return Types.DATE;
            case 8:
                 return Types.BLOB;
            default:
                 return Types.VARCHAR;
        }
    }

    private static Object treatWordTags(EboContext boctx, Object value, ArrayList tags)throws boRuntimeException
    {
        GtTag aux = null;
        for (int i = 0; i < tags.size(); i++)
        {
            aux = ((GtTag)tags.get(i));
            if(aux.applyToWord())
            {
                value = aux.apply(boctx, value);
            }
        }

        if(value instanceof String)
        {
            String toRet = (String)value;
            String tag = null;
            for (int i = 0; i < tags.size(); i++)
            {
                aux = ((GtTag)tags.get(i));
                if(aux.isWordTag())
                {
                    tag = getWordTag(aux.getName());
                    toRet =  tag + toRet + tag;
                }
            }
            return toRet;
        }
        return value;
    }

    public static Object getReturnObject(EboContext boctx, int gesdocTipoCamposLOV, Object value, byte templateType, ArrayList tags) throws boRuntimeException
    {
        if(GtTemplate.TEXT_TEMPLATE == templateType)
        {
            return getReturnObjectForText(boctx, gesdocTipoCamposLOV, value, tags);
        }
        else
        {
            if(tags == null || tags.size() == 0)
            {
                return getReturnObject(boctx, gesdocTipoCamposLOV, value);
            }
            else
            {
                Object o = getReturnObject(boctx,gesdocTipoCamposLOV, value);
                return treatWordTags(boctx, o, tags);
            }
        }
    }
    private static Object getReturnObjectForText(EboContext boctx, int gesdocTipoCamposLOV, Object value, ArrayList tags)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
            SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm"); //$NON-NLS-1$
            if(value == null) return value;
            boolean tagsToapply = (tags != null && tags.size() > 0);
            switch(gesdocTipoCamposLOV)
            {
                case 1:
                case 5:if(!(value instanceof String))
                        return !tagsToapply ? String.valueOf(value):textApplyTags(boctx, value, tags);
                       return !tagsToapply ? value:textApplyTags(boctx, value, tags);
                case 7:
                     return !tagsToapply ? getDescriptionTipo7(boctx, (String)value):textApplyTags(boctx, getDescriptionTipo7(boctx, (String)value), tags);
                case 2:
                case 3:
                      return !tagsToapply ? value:textApplyTags(boctx, value, tags);
                case 4:
                     if(!(value instanceof String))
                        return !tagsToapply ? sdfTime.format((Date)value):textApplyTags(boctx, (Date)value, tags);
                     return !tagsToapply ? value:textApplyTags(boctx, value, tags);
                case 6:
                     if(!(value instanceof String))
                        return !tagsToapply ? sdf.format((Date)value):textApplyTags(boctx, (Date)value, tags);
                     return !tagsToapply ? value:textApplyTags(boctx, value, tags);
                case 8:
                     return ""; //$NON-NLS-1$
                default:
                     return !tagsToapply ? value:textApplyTags(boctx, value, tags);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    private static Object textApplyTags(EboContext boctx, Object value, ArrayList tags) throws boRuntimeException
    {

        for (int i = 0; i < tags.size(); i++)
        {
            if(((GtTag)tags.get(i)).applyToText())
            {
                value = ((GtTag)tags.get(i)).apply(boctx, value);
            }
        }

        return value;
    }

    private static boolean hasTextApplyTags(ArrayList tags) throws boRuntimeException
    {

        for (int i = 0; i < tags.size(); i++)
        {
            if(((GtTag)tags.get(i)).applyToText())
            {
               return true;
            }
        }

        return false;
    }

    private static String wordSpecialTreat(String word)
    {
        word = word.replaceAll("-", SOFT_HYPEN); //$NON-NLS-1$
        return word;
    }

    private static Object getReturnObject(EboContext boctx, int gesdocTipoCamposLOV, Object value)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
            SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm"); //$NON-NLS-1$
            if(value == null) return value;
            switch(gesdocTipoCamposLOV)
            {
                case 1:
                case 5:if(!(value instanceof String))
                        return wordSpecialTreat(String.valueOf(value));
                       return wordSpecialTreat((String)value);
                case 7:
                     return wordSpecialTreat(getDescriptionTipo7(boctx, (String)value));
                case 2:
                case 3:
                      return value;
                case 4:
                     if(value instanceof String)
                        return sdfTime.parse((String)value);
                     return value;
                case 6:
                     if(value instanceof String)
                     {
                        try
                        {
                            return sdf.parse((String)value);
                        }
                        catch( ParseException e )
                        {
                            // Tenta noutro formato
                            SimpleDateFormat asdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S"); //$NON-NLS-1$
                            return asdf.parse( (String)value );
                        }
                     }
                     return value;
                default:
                     return value;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    public static int getSqlType(AttributeHandler att, byte templateType)
    {
        return getSqlType(att.getDefAttribute(), templateType);
    }
    public static int getSqlType(boDefAttribute att, byte templateType)
    {
        if(GtTemplate.TEXT_TEMPLATE == templateType)
        {
            return Types.VARCHAR;
        }
        else
        {
            return getSqlType(att);
        }
    }

    public static Object getReturnObject(EboContext boctx, AttributeHandler att, Object value, byte templateType, ArrayList tags) throws boRuntimeException
    {
        return getReturnObject(boctx, att.getDefAttribute(), value, templateType, tags);
    }

    public static Object getReturnObject(EboContext boctx, boDefAttribute att, Object value, byte templateType, ArrayList tags) throws boRuntimeException
    {
        if(GtTemplate.TEXT_TEMPLATE == templateType)
        {
            if(hasTextApplyTags(tags))
                return textApplyTags(boctx, value, tags);
            return getReturnObjectForText(boctx, att, value);
        }
        else
        {
            if(tags == null || tags.size() == 0)
            {
                return value;
            }
            else
            {
                return treatWordTags(boctx, value, tags);
            }
        }
    }

    private static Object getReturnObjectForText(EboContext ctx, boDefAttribute att, Object valor) throws boRuntimeException
    {
         if ( att.getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE )
         {

            long b = 0;
            if ( valor == null )
            {
                b = 0;
            }
            else if( valor instanceof Long )
            {
                b = (valor == null ? 0 : ((Long)valor).longValue());
            }
            else if( valor instanceof BigDecimal )
            {
                b = (valor == null ? 0 : ((BigDecimal)valor).longValue());
            }


            if ( b > 0 )
            {
                boObject o = boObject.getBoManager().loadObject( ctx, b );
                return o.getCARDID().toString();
            }
            else return ""; //$NON-NLS-1$
         }
         else if("boolean".equalsIgnoreCase(att.getType())) //$NON-NLS-1$
         {
            String value = (valor == null ? "": (String)valor); //$NON-NLS-1$
            if("0".equals(value)) //$NON-NLS-1$
            {
                //falta verificar a lingua
                return Messages.getString("Helper.14"); //$NON-NLS-1$
            }
            else if("1".equals(value)) //$NON-NLS-1$
            {
                return Messages.getString("Helper.16"); //$NON-NLS-1$
            }
            return value;
         }
         else if(att.getLOVName() != null &&
            !"".equals(att.getLOVName())) //$NON-NLS-1$
         {
            String xlov = att.getLOVName();
            String value = (valor == null ? "": (String)valor); //$NON-NLS-1$
//            if(value != null && !"".equals(value))
//            {
//                boObject lov;
//                lov = obj.getBoManager().loadObject(obj.getEboContext(),"Ebo_LOV","name='"+xlov+"'");
//                if(lov.exists())
//                {
//                    bridgeHandler lovdetails= lov.getBridge("details");
//                    lovdetails.beforeFirst();
//                    boObject det;
//                    while(lovdetails.next())
//                    {
//                        det = lovdetails.getObject();
//                        if(value.equalsIgnoreCase(det.getAttribute("value").getValueString()))
//                        {
//                            return det.getAttribute("description").getValueString();
//                        }
//                    }
//                }
//            }
            return value;
         }
         else if("dateTime".equalsIgnoreCase(att.getType())) //$NON-NLS-1$
         {
            Date d = (Date)valor;
            if(d != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss"); //$NON-NLS-1$
                 return formatter.format(d);
            }
            return ""; //$NON-NLS-1$
         }
         else if("date".equalsIgnoreCase(att.getType())) //$NON-NLS-1$
         {
            Date d = (Date)valor;
            if(d != null)
            {
                 SimpleDateFormat formatter = new SimpleDateFormat ("dd/MM/yyyy"); //$NON-NLS-1$
                 return formatter.format(d);
            }
            return ""; //$NON-NLS-1$
         }
         else if("IFILE".equalsIgnoreCase(att.getType())) //$NON-NLS-1$
         {
            return ""; //$NON-NLS-1$
         }
         else
         {
            NumberFormat currencyFormatter = NumberFormat.getInstance();

            if(att.getDecimals() != 0)
            {
                if( valor instanceof BigDecimal )
                {
                    valor = new Double(((BigDecimal)valor).doubleValue());
                }
                Double value = (valor == null ? null: ((Double)valor));
                //currency
                currencyFormatter.setParseIntegerOnly(false);
                if("Y".equalsIgnoreCase(att.getGrouping())) //$NON-NLS-1$
                {
                    currencyFormatter.setGroupingUsed(true);
                }
                currencyFormatter.setMaximumFractionDigits(att.getDecimals());
                currencyFormatter.setMinimumFractionDigits(att.getMinDecimals());
                currencyFormatter.setMinimumIntegerDigits(1);
                return currencyFormatter.format(value);
            }
            else if("Y".equalsIgnoreCase(att.getGrouping())) //$NON-NLS-1$
            {
                Double value = (valor == null ? null: ((Double)valor));
                currencyFormatter.setParseIntegerOnly(false);
                currencyFormatter.setMinimumIntegerDigits(1);
                currencyFormatter.setGroupingUsed(true);
                return currencyFormatter.format(value);
            }
            return String.valueOf(valor);
         }
    }


    private static int getSqlType(boDefAttribute att)
    {
        String t = att.getType().toUpperCase();

        if ((t.indexOf("CHAR") != -1) || //$NON-NLS-1$
                ((att.getLOVName() != null) &&
                !"".equals(att.getLOVName()))) //$NON-NLS-1$
        {
            return Types.VARCHAR;
        }

        if (t.indexOf("NUMBER") != -1) //$NON-NLS-1$
        {
            return Types.NUMERIC;
        }

        if (t.indexOf("BOOLEAN") != -1) //$NON-NLS-1$
        {
            return Types.VARCHAR;
        }

        if (t.indexOf("OBJECT") != -1) //$NON-NLS-1$
        {
            return Types.VARCHAR;
        }

        if (t.indexOf("CLOB") != -1) //$NON-NLS-1$
        {
            return Types.VARCHAR;
        }

        if (t.indexOf("DATE") != -1) //$NON-NLS-1$
        {
            return Types.DATE;
        }

        if (t.indexOf("IFILE") != -1) //$NON-NLS-1$
        {
            return Types.BLOB;
        }

        return Types.VARCHAR;
    }


    public static boObject getGestempGenerated(EboContext boctx, GtTemplate template, String rosto, String doc, String text, String textAssunto) throws boRuntimeException
    {
        //vou gerar o objecto xeo com o resultado
        boObject result = null;
        long genBoui = template.getActvSendBoui();
        if(genBoui > 0)
        {
            result = boObject.getBoManager().loadObject(boctx, genBoui);
        }
        else
        {
            if(template.getChannel() == template.TYPE_EMAIL)
            {
                result = boObject.getBoManager().createObject(boctx, "GESTEMP_GeneratedMail"); //$NON-NLS-1$

                result.getAttribute("email").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("sms").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("carta").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("fax").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else if(template.getChannel() == template.TYPE_SMS)
            {
                result = boObject.getBoManager().createObject(boctx, "GESTEMP_GeneratedSMS"); //$NON-NLS-1$
                result.getAttribute("email").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("sms").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("carta").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("fax").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else if(template.getChannel() == template.TYPE_CARTA)
            {
                result = boObject.getBoManager().createObject(boctx, "GESTEMP_Generated"); //$NON-NLS-1$
                result.getAttribute("email").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("sms").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("carta").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("fax").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else if(template.getChannel() == template.TYPE_FAX)
            {
                result = boObject.getBoManager().createObject(boctx, "GESTEMP_Generated"); //$NON-NLS-1$
                result.getAttribute("email").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("sms").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("carta").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("fax").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                result = boObject.getBoManager().createObject(boctx, "GESTEMP_Generated"); //$NON-NLS-1$
                result.getAttribute("email").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("sms").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("carta").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                result.getAttribute("fax").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        result.getAttribute("usedTemplate").setValueLong(template.getBoui()); //$NON-NLS-1$
        result.getAttribute("generateDate").setValueDate(new Date()); //$NON-NLS-1$
        result.getAttribute("user").setValueLong(boctx.getBoSession().getPerformerBoui()); //$NON-NLS-1$

        if(template.getChannel() == template.TYPE_EMAIL)
        {
            result.getAttribute("textAssunto").setValueString(textAssunto); //$NON-NLS-1$
            result.getAttribute("textMail").setValueString(text); //$NON-NLS-1$
        }
        else if(template.getChannel() == template.TYPE_SMS)
        {
            result.getAttribute("textSms").setValueString(text); //$NON-NLS-1$
        }

        //docs
        boObject docRosto = null;
        boObject docTemplate = null;

        if(rosto != null)
        {
            iFile ifile = new FSiFile(null,new File(rosto),null);
            docRosto = boObject.getBoManager().createObject(boctx, "Ebo_Document"); //$NON-NLS-1$
            docRosto.getAttribute("description").setValueString(Messages.getString("Helper.90")); //$NON-NLS-1$ //$NON-NLS-2$
            docRosto.getAttribute("dtRegisto").setValueDate(new Date()); //$NON-NLS-1$
            docRosto.getAttribute("tipoDoc").setValueString("139"); //$NON-NLS-1$ //$NON-NLS-2$
            docRosto.getAttribute("dtDoc").setValueDate(result.getAttribute("dtImpressao").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
            docRosto.getAttribute("dtSaida").setValueDate(result.getAttribute("dtImpressao").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
            docRosto.getAttribute("segmento").setValueString(Modulo.getTestModulo()); //$NON-NLS-1$
            docRosto.getAttribute("file").setValueiFile(ifile); //$NON-NLS-1$
            docRosto.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(ifile.length())); //$NON-NLS-1$
            docRosto.getAttribute("fileName").setValueString(ifile.getName()); //$NON-NLS-1$
            result.getAttribute("rosto").setObject(docRosto); //$NON-NLS-1$
        }
        if(doc != null)
        {
            iFile ifile = new FSiFile(null,new File(doc),null);
            docTemplate = boObject.getBoManager().createObject(boctx, "Ebo_Document"); //$NON-NLS-1$
            docTemplate.getAttribute("description").setValueString("Documento"); //$NON-NLS-1$ //$NON-NLS-2$
            docTemplate.getAttribute("dtRegisto").setValueDate(new Date()); //$NON-NLS-1$
            docTemplate.getAttribute("tipoDoc").setValueString("139"); //$NON-NLS-1$ //$NON-NLS-2$
            docTemplate.getAttribute("dtDoc").setValueDate(result.getAttribute("dtImpressao").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
            docTemplate.getAttribute("dtSaida").setValueDate(result.getAttribute("dtImpressao").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
            docTemplate.getAttribute("segmento").setValueString(Modulo.getTestModulo()); //$NON-NLS-1$
            docTemplate.getAttribute("file").setValueiFile(ifile); //$NON-NLS-1$
            docTemplate.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(ifile.length())); //$NON-NLS-1$
            docTemplate.getAttribute("fileName").setValueString(ifile.getName()); //$NON-NLS-1$
            result.getAttribute("doc").setObject(docTemplate); //$NON-NLS-1$
        }

        boObject aux = null;
        result.getBridge("respostas").truncate(); //$NON-NLS-1$
        result.getBridge("objReferences").truncate(); //$NON-NLS-1$
        template.setAnswerBridge(result.getBridge("respostas")); //$NON-NLS-1$

        //resposta a Queries

        GtQuery [] queries = template.getQueries();
        for (int i = 0; i < queries.length;  i++)
        {
            aux = queries[i].getAnswerObject(boctx);
            if(aux != null)
            {
                result.getBridge("respostas").add(aux.getBoui()); //$NON-NLS-1$
            }
            //
            ArrayList dependentsList = queries[i].getListDependents();
            for (int j = 0; j < dependentsList.size(); j++)
            {
                aux = ((GtCampoNObjecto)dependentsList.get(j)).getAnswerObject(boctx);
                if(aux != null)
                {
                    result.getBridge("respostas").add(aux.getBoui()); //$NON-NLS-1$
                }
            }

            //classificação
            queries[i].setReferencias(result);
        }

        //resposta a campos manuais Simples
        GtCampoManual [] manuais = template.getCamposManuaisSimples();
        for (int i = 0; i < manuais.length; i++)
        {
            aux = manuais[i].getAnswerObject(boctx);
            if(aux != null)
            {
                result.getBridge("respostas").add(aux.getBoui()); //$NON-NLS-1$
            }
        }

        //resposta a campos manuais lista
        GtCampoNManual [] manuaisLista = template.getCamposManuaisLista();
        for (int i = 0; i < manuaisLista.length; i++)
        {
            aux = manuaisLista[i].getAnswerObject(boctx);
            if(aux != null)
            {
                result.getBridge("respostas").add(aux.getBoui()); //$NON-NLS-1$
            }
        }
        return result;
    }

//    public static boObject getMessage(EboContext boctx, GtTemplate template, boObject generated) throws boRuntimeException
//    {
//        //mensagem normal
//        boObject destinatario = null;
//        boObject mailobject = null;
//        boObject actvSend = null;
//
//        destinatario = getDestinatario(generated);
//        if(generated.getAttribute("actvSend").getObject() != null)
//        {
//            actvSend = generated.getAttribute("actvSend").getObject();
//        }
//        else if(template.getActvSendBoui() > 0)
//        {
//            actvSend = boObject.getBoManager().loadObject(boctx, template.getActvSendBoui());
//        }
//        if(actvSend != null)
//        {
//            boObject xwfVar = actvSend.getAttribute("message").getObject();
//            if(xwfVar != null)
//            {
//                boObject varValue = xwfVar.getAttribute("value").getObject();
//                if(varValue != null)
//                {
//                    mailobject = varValue.getAttribute("valueObject").getObject();
//                }
//            }
//        }
//        if(mailobject == null)
//        {
//            if(template.getChannel() == GtTemplate.TYPE_EMAIL)
//            {
//                mailobject = boObject.getBoManager().createObject(boctx,"messageMail");
//            }
//            else if(template.getChannel() == GtTemplate.TYPE_CARTA)
//            {
//                mailobject = boObject.getBoManager().createObject(boctx,"messageLetter");
//            }
//            else if(template.getChannel() == GtTemplate.TYPE_FAX)
//            {
//                mailobject = boObject.getBoManager().createObject(boctx,"messageFax");
//
//            }
//            else if(template.getChannel() == GtTemplate.TYPE_SMS)
//            {
//                mailobject = boObject.getBoManager().createObject(boctx,"messageSMS");
//            }
//        }
//
//        if(template.getChannel() == GtTemplate.TYPE_CARTA || template.getChannel() == GtTemplate.TYPE_FAX)
//        {
//            //binaryDocument
//            if(generated.getAttribute("rosto").getObject() != null)
//            {
//                mailobject.getBridge("binaryDocuments").truncate();
//                mailobject.getObjectBinary().setBinary(generated.getAttribute("rosto").getObject());
//            }
//            if(generated.getAttribute("doc").getObject() != null)
//            {
//                mailobject.getBridge("binaryDocuments").truncate();
//                mailobject.getObjectBinary().setBinary(generated.getAttribute("doc").getObject());
//            }
//        }
//
//        if(template.getChannel() == GtTemplate.TYPE_EMAIL || template.getChannel() == GtTemplate.TYPE_SMS)
//        {
//            try
//            {
//                iFile f = generated.getAttribute("doc").getObject().getAttribute("file").getValueiFile();
//                String value = getString(f.getInputStream());
//                mailobject.getAttribute("description").setValueString(value);
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//
//        //datas
////        mailobject.getAttribute("dtdoc").setValueDate(auxMsg.getSentDate());
//        //prioridade
//         mailobject.getAttribute("priority").setValueString("1");
//         //recibos de leitura
//         mailobject.getAttribute("send_read_receipt").setValueString("0");
//         //from
//         mailobject.getAttribute("from").setValueLong(boctx.getBoSession().getPerformerBoui());
//         //to
//         mailobject.getBridge("to").truncate();
//         mailobject.getBridge("to").add(destinatario.getBoui());
//         //cc - sem cc
//         //bcc
//         //attachedObjects
//         mailobject.getBridge("attachedObjects").truncate();
//         mailobject.getBridge("attachedObjects").add(generated.getBoui());
//         //assunto
//         mailobject.getAttribute("name").setValueString(getAssunto(template, generated));
//         //content
//         //vou verificar o tipo de template se fôr texto ou sms vou preencher os restantes é adicionar ao
//         if(template.getActvSendBoui() <= 0)
//         {
//            actvSend = createMessageSendProgram(boctx, mailobject);
//            template.setActvSendBoui(actvSend.getBoui());
//         }
////         mailobject.getObjectBinary().setBinary(null);
//         generated.getAttribute("actvSend").setObject(actvSend);
//         generated.getAttribute("msg").setObject(mailobject);
//         return actvSend;
//    }

    public static boObject getMessage(EboContext boctx, GtTemplate template, String rosto, String doc, String text, String textAssunto) throws boRuntimeException
    {
        //mensagem normal
//        logger.debug("--------------------------------Geração da Actividade-----------------------------------------");
        long ti = System.currentTimeMillis();
        boObject destinatario = null;
        boObject mailobject = null;
        boObject actvSend = null;
        boolean email = false, sms = false, fax = false, carta = false;
        boolean truncatedDocs = false;
        boolean reply = false;

        long actvBoui = template.getActvSendBoui();

        if(actvBoui > 0)
        {
            actvSend = boObject.getBoManager().loadObject(boctx, actvBoui);
            if(actvSend != null)
            {
                boObject xwfVar = actvSend.getAttribute("message").getObject(); //$NON-NLS-1$
                if(xwfVar != null)
                {
                    boObject varValue = xwfVar.getAttribute("value").getObject(); //$NON-NLS-1$
                    if(varValue != null)
                    {
                        mailobject = varValue.getAttribute("valueObject").getObject(); //$NON-NLS-1$
                    }
                }
            }
        }

        if(template.getChannel() == template.TYPE_EMAIL)
        {
            email = true;
            if(template.getSendType() != null)
            {
                reply = true;
                boObject message = boObject.getBoManager().loadObject(boctx, template.getMsgReplyFrom());
                if(mailobject == null)
                {
                    if("forward".equals(template.getSendType())) //$NON-NLS-1$
                    {
                        mailobject = boObject.getBoManager().createObject(boctx,"messageMail", message); //$NON-NLS-1$
                        mailobject.getAttribute("preferedMedia").setValueString("E-Mail"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        mailobject = boObject.getBoManager().createObject(boctx,"messageMail"); //$NON-NLS-1$
                    }
                }

                //TODO: Lusitania??
                /*
                MessageServer.setForwardFields(mailobject,
                                               message,
                                               "replyAll".equals(template.getSendType()) ? MessageServer.REPLAY_ALL
                                               :
                                               "reply".equals(template.getSendType()) ? MessageServer.REPLAY
                                               :
                                               MessageServer.FORWARD);
                */
//                String aux = mailobject.getAttribute("description").getValueString();
//                mailobject.getAttribute("description").setValueString(text + aux);
                mailobject.getAttribute("description").setValueString(text); //$NON-NLS-1$
            }
            else
            {
                if(mailobject == null)
                {
                    mailobject = boObject.getBoManager().createObject(boctx,"messageMail"); //$NON-NLS-1$
                }
                mailobject.getAttribute("name").setValueString(textAssunto); //$NON-NLS-1$
                mailobject.getAttribute("description").setValueString(text); //$NON-NLS-1$
            }
        }
        else if(template.getChannel() == template.TYPE_CARTA)
        {
            carta = true;

            if(template.getSendType() != null)
            {
                reply = true;
                boObject message = boObject.getBoManager().loadObject(boctx, template.getMsgReplyFrom());
                if(mailobject == null)
                {
                    if("forward".equals(template.getSendType())) //$NON-NLS-1$
                    {
                        mailobject = boObject.getBoManager().createObject(boctx,"messageLetter", message); //$NON-NLS-1$
                        mailobject.getAttribute("preferedMedia").setValueString("Letter"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        mailobject = boObject.getBoManager().createObject(boctx,"messageLetter"); //$NON-NLS-1$
                    }
                }

                //TODO: Lusitania??
                /*
                MessageServer.setForwardFields(mailobject,
                                               message,
                                               "replyAll".equals(template.getSendType()) ? MessageServer.REPLAY
                                               :
                                               "reply".equals(template.getSendType()) ? MessageServer.REPLAY
                                               :
                                               MessageServer.FORWARD);
                */
            }
            else
            {
                if(mailobject == null)
                {
                    mailobject = boObject.getBoManager().createObject(boctx,"messageLetter"); //$NON-NLS-1$
                }
                mailobject.getAttribute("name").setValueString(getAssunto(boctx, template)); //$NON-NLS-1$
            }
            //impressão local/central
            if(template.isOnlyCentralPrint())
            {
                mailobject.getAttribute("impCentral").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                mailobject.getAttribute("impCentral").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            //forma de envio
            mailobject.getAttribute("codeBarClienteCTT").setValueString(template.getCodeBarCliente()); //$NON-NLS-1$
            if(template.isRegistada())
            {
                mailobject.getAttribute("envioCarta").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
                mailobject.getAttribute("registoCTT").setValueString(template.getRegistoNr()); //$NON-NLS-1$
                mailobject.getAttribute("impCentral").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else if(template.isAviso())
            {
                mailobject.getAttribute("envioCarta").setValueString("2"); //$NON-NLS-1$ //$NON-NLS-2$
                mailobject.getAttribute("registoCTT").setValueString(template.getRegistoNr()); //$NON-NLS-1$
                mailobject.getAttribute("impCentral").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                mailobject.getAttribute("envioCarta").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                mailobject.getAttribute("registoCTT").setValueString(""); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        else if(template.getChannel() == template.TYPE_FAX)
        {
            fax = true;
            if(template.getSendType() != null)
            {
                reply = true;
                boObject message = boObject.getBoManager().loadObject(boctx, template.getMsgReplyFrom());
                if(mailobject == null)
                {
                    if("forward".equals(template.getSendType())) //$NON-NLS-1$
                    {
                        mailobject = boObject.getBoManager().createObject(boctx,"messageFax", message); //$NON-NLS-1$
                        mailobject.getAttribute("preferedMedia").setValueString("Fax"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        mailobject = boObject.getBoManager().createObject(boctx,"messageFax"); //$NON-NLS-1$
                    }
                }

                //TODO: Lusitania??
                /*
                MessageServer.setForwardFields(mailobject,
                                               message,
                                               "replyAll".equals(template.getSendType()) ? MessageServer.REPLAY_ALL //$NON-NLS-1$
                                               :
                                               "reply".equals(template.getSendType()) ? MessageServer.REPLAY //$NON-NLS-1$
                                               :
                                               MessageServer.FORWARD);
                */
            }
            else
            {
                if(mailobject == null)
                {
                    mailobject = boObject.getBoManager().createObject(boctx,"messageFax"); //$NON-NLS-1$
                }
                mailobject.getAttribute("name").setValueString(getAssunto(boctx, template)); //$NON-NLS-1$
            }
            //impressão local/central
            if(template.isOnlyCentralPrint())
            {
                mailobject.getAttribute("impCentral").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                mailobject.getAttribute("impCentral").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        else if(template.getChannel() == template.TYPE_SMS)
        {
            sms = true;
            if(mailobject == null)
            {
                mailobject = boObject.getBoManager().createObject(boctx,"messageSMS"); //$NON-NLS-1$
            }

            if(template.getSendType() != null)
            {
                boObject message = boObject.getBoManager().loadObject(boctx, template.getMsgReplyFrom());
                //assunto
                String subject = message.getAttribute("name").getValueString(); //$NON-NLS-1$
                String prefix = "RE:"; //$NON-NLS-1$
                if("forward".equals(template.getSendType())) //$NON-NLS-1$
                {
                    prefix = "FW:"; //$NON-NLS-1$
                    mailobject.getAttribute("forwardFrom").setValueLong(message.getBoui()); //$NON-NLS-1$
                }
                else
                {
                    mailobject.getAttribute("responseTo").setValueLong(message.getBoui()); //$NON-NLS-1$
                }
                if(subject== null || !subject.toUpperCase().startsWith(prefix))
                {
                    subject = subject==null?prefix:prefix + " " + subject;             //$NON-NLS-1$
                }
                mailobject.getAttribute("name").setValueString(subject); //$NON-NLS-1$
            }
            else
            {
                mailobject.getAttribute("name").setValueString(getAssunto(boctx, template)); //$NON-NLS-1$
            }
            mailobject.getAttribute("textSMS").setValueString(text); //$NON-NLS-1$
        }

        //attachedObjects
        mailobject.getBridge("documents").truncate(); //$NON-NLS-1$

        mailobject.getAttribute("docSeq").setValueString(template.getMsgID()); //$NON-NLS-1$
        destinatario = getDestinatario(boctx, template);
        if(destinatario == null)
        {
            throw new boRuntimeException("", Messages.getString("Helper.194"), null); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if(carta || fax)
        {
            clearBridge(mailobject, "binaryDocuments");  //$NON-NLS-1$
            //docs
            if(rosto != null)
            {
                boObject docRosto = null;
                iFile ifile = new FSiFile(null,new File(rosto),null);
                docRosto = boObject.getBoManager().createObject(boctx, "Ebo_Document"); //$NON-NLS-1$
                docRosto.getAttribute("description").setValueString("Folha de rosto do documento enviado para " + getToName(destinatario)); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtRegisto").setValueDate(new Date()); //$NON-NLS-1$
                docRosto.getAttribute("tipoDoc").setValueString("139"); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtDoc").setValueDate(mailobject.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtSaida").setValueDate(mailobject.getAttribute("dtEfectiv").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("registoCTT").setValueString(mailobject.getAttribute("registoCTT").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("codeBarClienteCTT").setValueString(mailobject.getAttribute("docSeq").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("segmento").setValueString(Modulo.getModulo()); //$NON-NLS-1$
                docRosto.getAttribute("file").setValueiFile(ifile); //$NON-NLS-1$
                docRosto.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(ifile.length())); //$NON-NLS-1$
                docRosto.getAttribute("fileName").setValueString(ifile.getName()); //$NON-NLS-1$
                docRosto.getAttribute("msg").setObject(mailobject); //$NON-NLS-1$
                docRosto.getAttribute("estado").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                mailobject.getBridge("binaryDocuments").add(docRosto.getBoui()); //$NON-NLS-1$
            }
            if(doc != null)
            {
                boObject docTemplate = null;
                iFile ifile = new FSiFile(null,new File(doc),null);
                docTemplate = boObject.getBoManager().createObject(boctx, "Ebo_Document"); //$NON-NLS-1$
                docTemplate.getAttribute("description").setValueString(Messages.getString("Helper.220") + getToName(destinatario)); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtRegisto").setValueDate(new Date()); //$NON-NLS-1$
                docTemplate.getAttribute("tipoDoc").setValueString("139"); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtDoc").setValueDate(mailobject.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtSaida").setValueDate(mailobject.getAttribute("dtEfectiv").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("registoCTT").setValueString(mailobject.getAttribute("registoCTT").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("codeBarClienteCTT").setValueString(mailobject.getAttribute("docSeq").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("segmento").setValueString(Modulo.getModulo()); //$NON-NLS-1$
                docTemplate.getAttribute("file").setValueiFile(ifile); //$NON-NLS-1$
                docTemplate.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(ifile.length())); //$NON-NLS-1$
                docTemplate.getAttribute("fileName").setValueString(ifile.getName()); //$NON-NLS-1$
                docTemplate.getAttribute("msg").setObject(mailobject); //$NON-NLS-1$
                docTemplate.getAttribute("estado").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                mailobject.getBridge("binaryDocuments").add(docTemplate.getBoui()); //$NON-NLS-1$
            }
        }
        if(email && (rosto != null || doc != null))
        {
            //docs
            if(rosto != null)
            {
                boObject docRosto = null;
                //vou transformar em PDF
                File pdfFile = convertToPdf(rosto, "rosto"); //$NON-NLS-1$
                iFile ifile = new FSiFile(null,pdfFile,null);
//                iFile ifile = new FSiFile(null,new File(rosto),null);
                docRosto = boObject.getBoManager().createObject(boctx, "Ebo_Document"); //$NON-NLS-1$
                docRosto.getAttribute("description").setValueString(Messages.getString("Helper.243") + getToName(destinatario)); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtRegisto").setValueDate(new Date()); //$NON-NLS-1$
                docRosto.getAttribute("tipoDoc").setValueString("139"); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtDoc").setValueDate(mailobject.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtSaida").setValueDate(mailobject.getAttribute("dtEfectiv").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("segmento").setValueString(Modulo.getModulo()); //$NON-NLS-1$
                docRosto.getAttribute("file").setValueiFile(ifile); //$NON-NLS-1$
                docRosto.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(ifile.length())); //$NON-NLS-1$
                docRosto.getAttribute("fileName").setValueString(ifile.getName()); //$NON-NLS-1$
                docRosto.getAttribute("msg").setObject(mailobject); //$NON-NLS-1$
                docRosto.getAttribute("estado").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                mailobject.getBridge("documents").add(docRosto.getBoui()); //$NON-NLS-1$
            }
            if(doc != null)
            {
                boObject docTemplate = null;
                //vou transformar em PDF
                File pdfFile = convertToPdf(doc, "documento"); //$NON-NLS-1$
                iFile ifile = new FSiFile(null,pdfFile,null);
//                iFile ifile = new FSiFile(null,new File(doc),null);
                docTemplate = boObject.getBoManager().createObject(boctx, "Ebo_Document"); //$NON-NLS-1$
                docTemplate.getAttribute("description").setValueString(Messages.getString("Helper.262") + getToName(destinatario)); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtRegisto").setValueDate(new Date()); //$NON-NLS-1$
                docTemplate.getAttribute("tipoDoc").setValueString("139"); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtDoc").setValueDate(mailobject.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtSaida").setValueDate(mailobject.getAttribute("dtEfectiv").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("segmento").setValueString(Modulo.getModulo()); //$NON-NLS-1$
                docTemplate.getAttribute("file").setValueiFile(ifile); //$NON-NLS-1$
                docTemplate.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(ifile.length())); //$NON-NLS-1$
                docTemplate.getAttribute("fileName").setValueString(ifile.getName()); //$NON-NLS-1$
                docTemplate.getAttribute("msg").setObject(mailobject); //$NON-NLS-1$
                docTemplate.getAttribute("estado").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                mailobject.getBridge("documents").add(docTemplate.getBoui()); //$NON-NLS-1$
            }
        }
        //usedTemplate
        mailobject.getAttribute("usedTemplate").setValueLong(template.getBoui()); //$NON-NLS-1$
        mailobject.getAttribute("modelo").setValueLong(getVersionParent(boctx, template.getBoui())); //$NON-NLS-1$
        //datas
//        mailobject.getAttribute("dtdoc").setValueDate(auxMsg.getSentDate());
        //prioridade
         mailobject.getAttribute("priority").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
         //recibos de leitura
         mailobject.getAttribute("send_read_receipt").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
         //from
         mailobject.getAttribute("from").setValueLong(boctx.getBoSession().getPerformerBoui()); //$NON-NLS-1$
         //to
         if(email || fax)
         {
            if("replyAll".equals(template.getSendType())) //$NON-NLS-1$
            {
                if(!hasDestinatario(mailobject, destinatario))
                {
                    mailobject.getBridge("to").add(destinatario.getBoui()); //$NON-NLS-1$
                }
            }
            else
            {
                mailobject.getBridge("to").truncate(); //$NON-NLS-1$
                mailobject.getBridge("to").add(destinatario.getBoui()); //$NON-NLS-1$
            }
         }
         else
         {
            mailobject.getBridge("to").truncate(); //$NON-NLS-1$
            mailobject.getBridge("to").add(destinatario.getBoui()); //$NON-NLS-1$
         }
         mailobject.getBridge("to").truncate(); //$NON-NLS-1$
         mailobject.getBridge("to").add(destinatario.getBoui()); //$NON-NLS-1$
         if(fax)
         {
            String faxNumber = template.getFaxNumber();
            if(faxNumber != null && !"".equals(faxNumber)) //$NON-NLS-1$
            {
                faxNumber = faxNumber.replaceAll(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$
                boBridgeIterator bit = mailobject.getBridge("to").iterator(); //$NON-NLS-1$
                bit.beforeFirst();
                if(bit.next())
                {
                    bit.currentRow().getObject().getAttribute("fax").setValueString(faxNumber); //$NON-NLS-1$
                }
            }
         }

         if(email)
         {
            String emailAddress = template.getEmailAddress();
            if(emailAddress != null && !"".equals(emailAddress)) //$NON-NLS-1$
            {
                boBridgeIterator bit = mailobject.getBridge("to").iterator(); //$NON-NLS-1$
                bit.beforeFirst();
                if(bit.next())
                {
                    bit.currentRow().getObject().getAttribute("email").setValueString(emailAddress); //$NON-NLS-1$
                }
            }
         }

         if(template.getFaxAnexos() != null && template.getFaxAnexos().length() > 0)
         {
            String []docsBouis = template.getFaxAnexos().split(";"); //$NON-NLS-1$
            for (int i = 0; i < docsBouis.length; i++)
            {
                if(docsBouis[i] != null && docsBouis[i].length() > 0)
                    mailobject.getBridge("documents").add(Long.parseLong(docsBouis[i])); //$NON-NLS-1$
            }
         }


        boObject aux = null;
        mailobject.getBridge("respostas").truncate(); //$NON-NLS-1$
        mailobject.getBridge("objReferences").truncate(); //$NON-NLS-1$
        template.setAnswerBridge(mailobject.getBridge("respostas")); //$NON-NLS-1$

        //resposta a Queries
        GtQuery [] queries = template.getQueries();
        for (int i = 0; i < queries.length;  i++)
        {
            aux = queries[i].getAnswerObject(boctx);
            if(aux != null)
            {
                mailobject.getBridge("respostas").add(aux.getBoui()); //$NON-NLS-1$
            }
            //
            ArrayList dependentsList = queries[i].getListDependents();
            for (int j = 0; j < dependentsList.size(); j++)
            {
                aux = ((GtCampoNObjecto)dependentsList.get(j)).getAnswerObject(boctx);
                if(aux != null)
                {
                    mailobject.getBridge("respostas").add(aux.getBoui()); //$NON-NLS-1$
                }
            }
            //classificação
            queries[i].setReferencias(mailobject);
        }

        //resposta a campos manuais Simples
        GtCampoManual [] manuais = template.getCamposManuaisSimples();
        for (int i = 0; i < manuais.length; i++)
        {
            aux = manuais[i].getAnswerObject(boctx);
            if(aux != null)
            {
                mailobject.getBridge("respostas").add(aux.getBoui()); //$NON-NLS-1$
            }
        }

        //resposta a campos manuais Simples
        GtCampoNManual [] manuaisLista = template.getCamposManuaisLista();
        for (int i = 0; i < manuaisLista.length; i++)
        {
            aux = manuaisLista[i].getAnswerObject(boctx);
            if(aux != null)
            {
                mailobject.getBridge("respostas").add(aux.getBoui()); //$NON-NLS-1$
            }
        }

        //vou validar o objecto mailObject
        validaMail(mailobject, template.getChannel());

        //Editável
        if(!template.getEdit())
        {
            mailobject.setDisabled();
        }

         if(actvSend == null)
         {
            if(template.getActvReplyFrom() == -1)
            {
                actvSend = createMessageSendProgram(boctx, mailobject, -1);
            }
            else
            {
                boObject actv = boObject.getBoManager().loadObject(boctx, template.getActvReplyFrom());
                actvSend = createMessageSendProgram(boctx, mailobject, actv.getAttribute("program").getValueLong()); //$NON-NLS-1$
            }
         }

         //ROBOT
//         actvSend.getAttribute("assignedQueue").setValueLong();
//         mailobject.getObjectBinary().setBinary(null);
        long tf = System.currentTimeMillis();
        logger.debug("Tempo Total Criação da Actividade (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)"); //$NON-NLS-1$ //$NON-NLS-2$
//        logger.debug("--------------------------------FIM Geração da Actividade-----------------------------------------");
         return actvSend;
    }

    private static File convertToPdf(String doc, String outPrefix)
    {
        String fName = null;
        if("rosto".equals(outPrefix)) //$NON-NLS-1$
            fName = getMailRostoDocumentName(false);
        else if("documento".equals(outPrefix)) //$NON-NLS-1$
            fName = getMailDocumentName(false);
        if(fName != null)
        {
            //File outFile = TempFile.createTempFile(outPrefix, "pdf");

            RemoteFileConversion rc = new RemoteFileConversion();
            File outFile = rc.converIFile(
                    new FSiFile(null,new File(doc),null),
                    System.currentTimeMillis(),
                    "pdf",  //$NON-NLS-1$
                    Boolean.TRUE )[0];
            //PDFConvert.convert(doc, outFile.getAbsolutePath(), false);
            return outFile;
        }
        return null;
    }

    private static void clearBridge(boObject mailobject, String bridgeName) throws boRuntimeException
    {
        ArrayList toDelete = new ArrayList();
        boBridgeIterator bit =mailobject.getBridge(bridgeName).iterator();
        while(bit.next())
        {
            toDelete.add(bit.currentRow().getObject());
        }
        mailobject.getBridge(bridgeName).truncate();
        boolean ok = false;
        for (int i = 0; i < toDelete.size(); i++)
        {
            try{mailobject.update();}catch(Exception e){logger.error("",e);} //$NON-NLS-1$
            ok = false;
            try{
                //TODO: Lusitania??
                //DestroyBusinessObject.destroyDocuments(toDelete, true);
                ok = true;
            }catch(Exception e)
            {logger.error("",e);ok = false;}
            if(!ok)
                try{((boObject)toDelete.get(i)).destroy();}catch(Exception e){logger.error("",e);ok = false;} //$NON-NLS-1$
            if(!ok)
                try{((boObject)toDelete.get(i)).destroyForce();ok =true;}catch(Exception e){logger.error("",e);ok = false;}             //$NON-NLS-1$
        }
    }

    private static boolean hasDestinatario(boObject mailObject, boObject destinatario) throws boRuntimeException
    {
        boBridgeIterator bit = mailObject.getBridge("to").iterator(); //$NON-NLS-1$
        while(bit.next())
        {
            if(bit.currentRow().getObject().getAttribute("refObj").getValueLong() == destinatario.getBoui()) //$NON-NLS-1$
            {
                return true;
            }
        }
        return false;
    }

    public static boObject getDestinatario(EboContext boctx, GtTemplate template) throws boRuntimeException
    {
       //resposta a Queries
        GtQuery [] queries = template.getQueries();
        boObject answer = null;
        for (int i = 0; i < queries.length;  i++)
        {

            if(queries[i].getParametro().destinatario())
            {
                answer = queries[i].getAnswerObject(boctx);
                if( answer != null )
                {
                    return answer.getAttribute("objecto").getObject(); //$NON-NLS-1$
                }
                return null;
            }
        }
        return null;
    }

    private static String getAssunto(EboContext boctx, GtTemplate template) throws boRuntimeException
    {
        boObject templateObj = boObject.getBoManager().loadObject(boctx, template.getBoui());
        return templateObj.getAttribute("nome").getValueString(); //$NON-NLS-1$
    }

    private  static boObject createMessageSendProgram(EboContext ctx, boObject message, long progBoui) throws boRuntimeException
   {
        boObject program = null;
        boolean showWorkflowArea = false;
        if(progBoui == -1)
        {
            program = boObject.getBoManager().createObject(ctx, "xwfProgramRuntime");         //$NON-NLS-1$
            String label = message.getAttribute("name").getValueString(); //$NON-NLS-1$
            if(label != null && label.length() >= 200)
            {
                label = label.substring(0, 190) + "(...)";  //$NON-NLS-1$
            }
            program.getAttribute("label").setValueString(label, AttributeHandler.INPUT_FROM_INTERNAL);         //$NON-NLS-1$
            if(message.getAttribute("SYS_DTCREATE").getValueDate() != null) //$NON-NLS-1$
            {
                program.getAttribute("beginDate").setValueDate(message.getAttribute("SYS_DTCREATE").getValueDate(), AttributeHandler.INPUT_FROM_INTERNAL); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                program.getAttribute("beginDate").setValueDate(new Date(), AttributeHandler.INPUT_FROM_INTERNAL); //$NON-NLS-1$
            }
        }
        else
        {
            showWorkflowArea = true;
            program = boObject.getBoManager().loadObject(ctx, progBoui);
        }
        boObject activity = null;
        xwfManager man = new xwfManager(ctx, program);

        boObject performer = boObject.getBoManager().loadObject(ctx, ctx.getBoSession().getPerformerBoui());
        if(performer.getAttribute("centrocusto") != null) //$NON-NLS-1$
        {
            boObject ccust = performer.getAttribute("centrocusto").getObject(); //$NON-NLS-1$
            long assignedBoui = -1, executerBoui = -1;
            if(ccust == null)
            {
                assignedBoui = performer.getBoui();
                executerBoui = performer.getBoui();
            }
            else
            {
                assignedBoui = ccust.getBoui();
                executerBoui = performer.getBoui();
            }
            //TODO: Lusitania?? Linha substituida!
            //boObject actv = man.createMessageActivity("xwfActivitySend", message.getAttribute("name").getValueString(), message,
            boObject actv = man.createMessageActivity("xwfActivitySend", message.getAttribute("name").getValueString(), message,             message.getBoDefinition().getName(), program.getBoui(), assignedBoui, false, false, false, showWorkflowArea);

            actv.getAttribute("performer").setValueLong(executerBoui);
            actv.getAttribute("classificado").setValueString("1");

//            xwfFunctions.setIntelligentLabel(man.getBoManager(), program, actv);
            return actv;
        }
        else
        {
            throw new boRuntimeException("", "Centro Custo não definido para o utilizador.Contacte a Administração.", null); //$NON-NLS-1$ //$NON-NLS-2$
        }
   }

   public static long getActSendBoui(boObject msg) throws boRuntimeException
   {
        boObjectList list = boObjectList.list(msg.getEboContext(), "select xwfActivitySend where message.value.valueObject=" + msg.getBoui()); //$NON-NLS-1$
        list.beforeFirst();
        if(list.next())
        {
            return list.getObject().getBoui();
        }
        return -1;
   }

   public static long getTemplateBoui(boObject msg) throws boRuntimeException
   {
        return msg.getAttribute("usedTemplate").getValueLong(); //$NON-NLS-1$
   }
   public static long getTemplateParentBoui(boObject msg) throws boRuntimeException
   {
        return msg.getAttribute("modelo").getValueLong(); //$NON-NLS-1$
   }

   public static void redirectToParamFill(boObject msg) throws boRuntimeException
   {
        if(!MessageUtils.alreadySend(msg))
        {
            if("XwfController".equals(msg.getEboContext().getController().getName())) //$NON-NLS-1$
            {
                XwfController control = (XwfController)msg.getEboContext().getController();
                StringBuffer sb = new StringBuffer("__gestTempFillParameters.jsp?actvSendBoui="); //$NON-NLS-1$
                sb.append(control.getRuntimeActivity().getBoui())
                .append("&inputObjectBoui=").append(getTemplateBoui(msg)) //$NON-NLS-1$
                .append("&controllerName=XwfController").append("&method=edit"); //$NON-NLS-1$ //$NON-NLS-2$
                if(control.getRuntimeActivity() != null)
                {
                   control.getRuntimeActivity().setSendRedirect(sb.toString());
                }
            }
        }
   }

   private static String getString(InputStream in) throws IOException
   {
        StringBuffer source = new StringBuffer();
        InputStreamReader inRead = new InputStreamReader(in);
        char[] cbuff = new char[4096];
        int br;
        while((br=inRead.read(cbuff))>0) {
            source.append(cbuff,0,br);
        }
        inRead.close();
        return source.toString();
   }

   public static boolean isMarkedForTemplate(GtQuery query, boObject obj, GtTemplate template) throws boRuntimeException
   {
        if(!"1".equals(obj.getAttribute("automatico").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return modeSetByUser(obj, template.getChannel());
        }
        else
        {
            if(obj.getAttribute("tipoSeleccao") == null || "1".equals(obj.getAttribute("tipoSeleccao").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            {
                return modeAutomatic(query, obj, template.getTemplateFields(), false);
            }
            else
            {
                return modeAutomatic(query, obj, template.getTemplateBookmarks(), true);
            }
        }
   }

   public static boolean isMarkedForTemplate(GtCampo campoBridge, boObject obj) throws boRuntimeException
   {
        GtTemplate template = campoBridge.getTemplate();
        if(!"1".equals(obj.getAttribute("automatico").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return modeSetByUser(obj, template.getChannel());
        }
        else
        {
            return modeAutomatic(campoBridge, obj, template.getTemplateBookmarks(), true);
        }
   }



   public static boolean modeSetByUser(boObject obj, byte selectedType) throws boRuntimeException
   {
        String attName = ""; //$NON-NLS-1$
        if(selectedType == GtTemplate.TYPE_CARTA)
        {
            attName = "carta"; //$NON-NLS-1$
        }
        else if(selectedType == GtTemplate.TYPE_FAX)
        {
            attName = "fax"; //$NON-NLS-1$
        }
        else if(selectedType == GtTemplate.TYPE_EMAIL)
        {
            attName = "email"; //$NON-NLS-1$
        }
        else if(selectedType == GtTemplate.TYPE_SMS)
        {
            attName = "sms"; //$NON-NLS-1$
        }
        if(obj.getAttribute(attName) != null && "1".equals(obj.getAttribute(attName).getValueString())) //$NON-NLS-1$
        {
            return true;
        }
        return false;
   }

   private static boolean isDestinario(boObject query, boObject obj) throws boRuntimeException
   {
        if("GESTEMP_Query".equals(obj.getName()) || "GESTEMP_Query".equals(obj.getBoDefinition().getBoSuperBo())) //$NON-NLS-1$ //$NON-NLS-2$
        {
            boObject param = obj.getAttribute("parametro").getObject(); //$NON-NLS-1$
            return "1".equals(param.getAttribute("destinatario").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if("GESTEMP_Parametro".equals(obj.getName())) //$NON-NLS-1$
        {
            return "1".equals(obj.getAttribute("destinatario").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if(query != null)
        {
            boObject param = query.getAttribute("parametro").getObject(); //$NON-NLS-1$
            return "1".equals(param.getAttribute("destinatario").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return false;
   }

   private static String getNameToSearch(String queryName, boObject obj) throws boRuntimeException
   {
        if("GESTEMP_Query".equals(obj.getName()) || "GESTEMP_Query".equals(obj.getBoDefinition().getBoSuperBo())) //$NON-NLS-1$ //$NON-NLS-2$
        {
            String _queryName = obj.getAttribute("nome").getValueString(); //$NON-NLS-1$
            return _queryName + "__"; //$NON-NLS-1$
        }
        else if("GESTEMP_Parametro".equals(obj.getName())) //$NON-NLS-1$
        {
            return queryName + "__"; //$NON-NLS-1$
        }
        else if("GESTEMP_Campo".equals(obj.getName()) || "GESTEMP_Campo".equals(obj.getBoDefinition().getBoSuperBo())) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if(queryName == null || queryName.length() == 0)
            {
                return obj.getAttribute("nome").getValueString(); //$NON-NLS-1$
            }
            else
            {
                if("1".equals(obj.getAttribute("tipoSeleccao").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    return queryName + "__" + obj.getAttribute("nome").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    return queryName + "__" + obj.getAttribute("nome").getValueString() + "__"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            }
        }
        return null;
   }

   private static boolean modeAutomatic(GtQuery query, boObject obj,ArrayList fields, boolean bookmark) throws boRuntimeException
   {

        String aux = null;
        if(query != null && query.getParametro() != null && query.getParametro().destinatario())
        {
            return true;
        }

        if("GESTEMP_Parametro".equals(obj.getName()) && "1".equals(obj.getAttribute("destinatario").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        {
            return true;
        }

        if("GESTEMP_CampoJava".equals(obj.getName()) || "GESTEMP_CampoFormula".equals(obj.getName()) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return true;
        }

        if("GESTEMP_NCampoJava".equals(obj.getName()) || "GESTEMP_NCampoFormula".equals(obj.getName()) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return true;
        }

        if("GESTEMP_Query".equals(obj.getName()) || "GESTEMP_JavaQuery".equals(obj.getName())) //$NON-NLS-1$ //$NON-NLS-2$
        {
            boObject param = obj.getAttribute("parametro").getObject(); //$NON-NLS-1$
            if(param != null &&  "1".equals(param.getAttribute("destinatario").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
            {
                return true;
            }
        }
        if(fields != null)
        {
            String nameToSearch = getNameToSearch(query == null ? null:query.getNome(), obj);
            for (int i = 0; i < fields.size(); i++)
            {
                if(!bookmark)
                    aux = (String)fields.get(i);
                else
                    aux = ((Bookmark)fields.get(i)).getBookmarkName();
                if(nameToSearch.endsWith("__")) //$NON-NLS-1$
                {
                    if(aux.startsWith(nameToSearch)) return true;
                    if(aux.equals(nameToSearch.substring(0, nameToSearch.length() - 2))) return true;
                }
                else if(nameToSearch.equals(aux))
                {
                    return true;
                }
            }
        }
        return false;
   }

   private static boolean modeAutomatic(GtCampo campoBridge, boObject obj,ArrayList fields, boolean bookmark) throws boRuntimeException
   {
        if(fields != null)
        {
            String aux = null;
            GtQuery query = campoBridge.getQuery();
            String bookmarkNameSearch = query.getNome() + "__" + campoBridge.getNome(); //$NON-NLS-1$
            String bookmarkFieldNameSearch =  obj.getAttribute("nome").getValueString(); //$NON-NLS-1$
            Bookmark book;
            for (int i = 0; i < fields.size(); i++)
            {
                book  = (Bookmark)fields.get(i);
                if(bookmarkNameSearch.equals(book.getBookmarkName()))
                {
                    String bfields[] = book.getFields();
                    for (int j = 0; j < bfields.length; j++)
                    {
                        if(bookmarkFieldNameSearch.equals(bfields[j]))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
   }

   public static void afterLoadMessage(boObject msg) throws boRuntimeException
   {
        boObject t = null;
        if((t = msg.getAttribute("usedTemplate").getObject()) != null) //$NON-NLS-1$
        {
              if(!"1".equals(t.getAttribute("editavel").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
              {
                msg.setDisabled();
              }
        }
   }

   public static boolean beforeSaveGenerated(boObject msg) throws boRuntimeException
   {
        return true;
   }

   public static boolean beforeSaveGESTEMP_Query(boObject query) throws boRuntimeException
   {
        String aux = query.getAttribute("nome").getValueString(); //$NON-NLS-1$
        boolean historico = "1".equals(query.getAttribute("historico").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
        if(aux != null && aux.split(" ").length != 1) //$NON-NLS-1$
        {
            query.addErrorMessage(Messages.getString("Helper.411")); //$NON-NLS-1$
            return false;
        }
        if(SpecialField.isSpecialField(aux))
        {
            query.addErrorMessage(Messages.getString("Helper.412")); //$NON-NLS-1$
            return false;
        }
        else if(!historico)
        {
            Connection con = null;
            PreparedStatement pst = null;
            ResultSet rs = null;

            try
            {
                con = query.getEboContext().getConnectionData();
                StringBuffer sql = new StringBuffer("select 1 from GESTEMP_Query where NOME = ? and boui <> ? and historico <> '1'"); //$NON-NLS-1$
                pst = con.prepareStatement(sql.toString());
                pst.setString(1, query.getAttribute("nome").getValueString()); //$NON-NLS-1$
                pst.setLong(2, query.getBoui());
                rs = pst.executeQuery();
                if(rs.next())
                {
                    query.addErrorMessage(Messages.getString("Helper.415")); //$NON-NLS-1$
                    return false;
                }
            }
            catch (SQLException e)
            {
                logger.error("", e); //$NON-NLS-1$
            }
            finally
            {
                try{if(rs != null) rs.close();}catch(Exception e){}
                try{if(pst != null) pst.close();}catch(Exception e){}
            }
        }
        boObject param = query.getAttribute("parametro").getObject(); //$NON-NLS-1$
        boolean dest = "1".equals(param.getAttribute("destinatario").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
        if(dest)
        {
            boObject eboClr = param.getAttribute("objecto").getObject(); //$NON-NLS-1$
            String name = eboClr.getAttribute("name").getValueString(); //$NON-NLS-1$
            boDefInterface boDefI = boDefHandler.getInterfaceDefinition("iContact"); //$NON-NLS-1$
            String objs[] = boDefI.getImplObjects();
            boolean isContact = false;
            if(objs != null)
            {
                for (int i = 0; i < objs.length && !isContact; i++)
                {
                    if(objs[i].equals(name)) isContact = true;
                }
            }
            if(!isContact)
            {
                query.addErrorMessage(Messages.getString("Helper.423")); //$NON-NLS-1$
                return false;
            }
        }
        return true;
   }

   public static boolean beforeSaveGESTEMP_Campo(boObject field) throws boRuntimeException
   {
        String aux = field.getAttribute("nome").getValueString(); //$NON-NLS-1$
        boolean historico = "1".equals(field.getAttribute("historico").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
        if(aux != null && aux.split(" ").length != 1) //$NON-NLS-1$
        {
            field.addErrorMessage(Messages.getString("Helper.13")); //$NON-NLS-1$
            return false;
        }
        else if(!historico)
        {
            if(SpecialField.isSpecialField(aux))
            {
                field.addErrorMessage(Messages.getString("Helper.12")); //$NON-NLS-1$
                return false;
            }
            Connection con = null;
            PreparedStatement pst = null;
            ResultSet rs = null;

            try
            {
                con = field.getEboContext().getConnectionData();
                StringBuffer sql = new StringBuffer("select boui from GESTEMP_CAMPO where classname = ? and NOME = ? and boui <> ? and historico <> '1'"); //$NON-NLS-1$
                boolean notManual = false;
                if(!"GESTEMP_CampoNManual".equals(field.getName()) && !"GESTEMP_CampoManual".equals(field.getName()) && !"GESTEMP_Parametro".equals(field.getName())) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                {
                    sql.append(" and parametro$ = ?"); //$NON-NLS-1$
                    notManual = true;
                }
                pst = con.prepareStatement(sql.toString());
                pst.setString(1, field.getName());
                pst.setString(2, field.getAttribute("nome").getValueString()); //$NON-NLS-1$
                pst.setLong(3, field.getBoui());
                if(notManual)
                {
                    pst.setLong(4, field.getAttribute("parametro").getValueLong()); //$NON-NLS-1$
                }
                rs = pst.executeQuery();
                if(rs.next())
                {
                    field.addErrorMessage(Messages.getString("Helper.437")); //$NON-NLS-1$
                    return false;
                }
            }
            catch (SQLException e)
            {
                logger.error("", e); //$NON-NLS-1$
            }
            finally
            {
                try{if(rs != null) rs.close();}catch(Exception e){}
                try{if(pst != null) pst.close();}catch(Exception e){}
            }
        }
        return true;
   }

    public static String getResultPage(HttpServletRequest request, docHTML doc, boObject result) throws boRuntimeException
    {
        StringBuffer sb = new StringBuffer();
        int docID = doc.getDocIdx();
        int idx = doc.getDocIdx();
        EboContext boctx = doc.getEboContext();

        if(result != null && result.getName().startsWith("GESTEMP_Generated")) //$NON-NLS-1$
        {
            if("GESTEMP_GeneratedMail".equals(result.getName())) //$NON-NLS-1$
            {
                sb.append("gestemp_generatedmail_generaledit.jsp?method=edit&boui="); //$NON-NLS-1$
            }
            else if("GESTEMP_GeneratedSMS".equals(result.getName())) //$NON-NLS-1$
            {
                sb.append("gestemp_generatedsms_generaledit.jsp?method=edit&boui="); //$NON-NLS-1$
            }
            else
            {
                sb.append("gestemp_generated_generaledit.jsp?method=edit&boui="); //$NON-NLS-1$
            }
            sb.append(result.getBoui())
            .append("&docid=").append(docID).append("&myIDX=").append(idx); //$NON-NLS-1$ //$NON-NLS-2$
            return sb.toString();
        }


//        if(!"XwfController".equals(boctx.getController().getName()))
//        {
//            sb.append("xwfactivitysend_generaledit.jsp?method=edit&boui=");
//            sb.append(result.getBoui())
//            .append("&docid=").append(docID).append("&myIDX=").append(idx);
//        }
//        else
//        {
              String programBouiStr = result.getAttribute("program").getValueString(); //$NON-NLS-1$
              if(programBouiStr != null && !"".equals(programBouiStr)) //$NON-NLS-1$
              {
                  sb.append("__xwfWorkPlace.jsp?method=edit&docid=") //$NON-NLS-1$
                    .append(idx);
                  Enumeration oEnum = request.getParameterNames();
                  while( oEnum.hasMoreElements() )
                  {
                      String pname = oEnum.nextElement().toString();
                      if( !pname.equalsIgnoreCase("method") && !pname.equalsIgnoreCase("docid") ) //$NON-NLS-1$ //$NON-NLS-2$
                      {
                          sb.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
                      }
                  }
                  sb.append("&closeWindowOnCloseDoc=false&masterdoc=true&runtimeActivityBoui="+result.getBoui()+"&runtimeProgramBoui="+programBouiStr); //$NON-NLS-1$ //$NON-NLS-2$
              }
//        }
        return sb.toString();
    }

    public static boolean editParamsHiddenWhen(boObject msg)
    {
        try
        {
            if(msg.getAttribute("usedTemplate").getValueLong() > 0 && !MessageUtils.alreadySend(msg)) //$NON-NLS-1$
            {
                String aprov = msg.getAttribute("aprovState").getValueString(); //$NON-NLS-1$
                if("1".equals(aprov) || "2".equals(aprov)) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    return true;
                }
                return false;
            }
        }
        catch (boRuntimeException e)
        {

        }
        return true;
    }

    public static boolean onBeforeDestroyMessage(boObject msg) throws boRuntimeException
    {
        if(msg.getAttribute("usedTemplate").getValueLong() > 0) //$NON-NLS-1$
        {
            if(MessageUtils.alreadySend(msg))
            {
                if("messageLetter".equals(msg.getName())) //$NON-NLS-1$
                {
                    //se tiver registoCTT é necessário desregistar o número
                    if(msg.getAttribute("registoCTT").getValueString() != null && //$NON-NLS-1$
                        !"".equals(msg.getAttribute("registoCTT").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
                    {
                        //chamar API para desregistar o este número

                        //TODO:Implement Interface LUSITANIA
                        //NumeroRegistado.rollBack(msg.getAttribute("registoCTT").getValueString());
                        logger.info("Desregistar este número:" + msg.getAttribute("registoCTT").getValueString());
                    }

                    //se for carta tenho que ver se é de impressão Central e se ainda não foi impresso
                    if("1".equals(msg.getAttribute("impCentral").getValueString()) && //$NON-NLS-1$ //$NON-NLS-2$
                        msg.getAttribute("dtEfectiv").getValueDate() == null //$NON-NLS-1$
                    )
                    {
                        PrintJob.dropJob( msg.getEboContext(), String.valueOf( msg.getBoui() ) );
                    }
                    return true;

                }
                throw new boRuntimeException("", Messages.getString("Helper.472"), new Exception(Messages.getString("Helper.473"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
        return true;
    }

    public static boolean validaMail(boObject mail, byte channel) throws boRuntimeException
    {
        if(!MessageServer.verifyChannel(mail))
        {
            String msgError = null;
            if(channel == GtTemplate.TYPE_EMAIL)
            {
//                msgError = "O destinatário não tem o endereço de e-mail definido.";
            }
            else if(channel == GtTemplate.TYPE_CARTA)
            {
                msgError = Messages.getString("Helper.474"); //$NON-NLS-1$
            }
            else if(channel == GtTemplate.TYPE_FAX)
            {
                msgError = Messages.getString("Helper.475"); //$NON-NLS-1$
            }
            else if(channel == GtTemplate.TYPE_SMS)
            {
//                msgError = "O destinatário não tem o número de telemóvel definido.";
            }
            if(msgError != null)
            {
                throw new boRuntimeException("", msgError, null); //$NON-NLS-1$
            }
        }
        return true;
    }

    public static long getTemplateBouiByCode(EboContext boctx, String code) throws boRuntimeException
    {
        boObjectList list = boObjectList.list(boctx, "select GESTEMP_Template where code = ? and historico = '1' and activo = '1'", new Object[]{code}); //$NON-NLS-1$
        list.beforeFirst();
        if(list.next())
        {
            return list.getObject().getBoui();
        }
        return -1;
    }

//    private static String valida(boctx)
    public static boolean beforeSaveTemplate(boObject gtTemplate)
    {
        try
        {
            String omisso = gtTemplate.getAttribute("canalOmisso").getValueString(); //$NON-NLS-1$
            if("1".equals(omisso) &&  //$NON-NLS-1$
                gtTemplate.getAttribute("tempCarta").getObject() == null && //$NON-NLS-1$
                gtTemplate.getAttribute("rostoCarta").getObject() == null //$NON-NLS-1$
            )
            {
                gtTemplate.addErrorMessage(Messages.getString("Helper.482")); //$NON-NLS-1$
                return false;
            }
            if("2".equals(omisso) &&  //$NON-NLS-1$
                gtTemplate.getAttribute("tempFax").getObject() == null && //$NON-NLS-1$
                gtTemplate.getAttribute("rostoFax").getObject() == null) //$NON-NLS-1$
            {
                gtTemplate.addErrorMessage(Messages.getString("Helper.482")); //$NON-NLS-1$
                return false;
            }
            if("3".equals(omisso) &&  //$NON-NLS-1$
                gtTemplate.getAttribute("tempEmail").getObject() == null) //$NON-NLS-1$
            {
                gtTemplate.addErrorMessage(Messages.getString("Helper.482")); //$NON-NLS-1$
                return false;
            }
            if("4".equals(omisso) &&  //$NON-NLS-1$
                gtTemplate.getAttribute("tempSMS").getObject() == null) //$NON-NLS-1$
            {
                gtTemplate.addErrorMessage(Messages.getString("Helper.482")); //$NON-NLS-1$
                return false;
            }

            if(("1".equals(gtTemplate.getAttribute("envioCarta").getValueString())  || //$NON-NLS-1$ //$NON-NLS-2$
                "2".equals(gtTemplate.getAttribute("envioCarta").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
                &&
                !"2".equals(gtTemplate.getAttribute("impLocal").getValueString()) //$NON-NLS-1$ //$NON-NLS-2$
            )
            {
                gtTemplate.addErrorMessage(Messages.getString("Helper.499")); //$NON-NLS-1$
                return false;
            }

            //vou verificar se tem um destinatário todos os modelos tem que têr um destinatário
            if(!foundDestinatario(gtTemplate, Messages.getString("Helper.500"))) //$NON-NLS-1$
            {
                gtTemplate.addErrorMessage(Messages.getString("Helper.501")); //$NON-NLS-1$
                return false;
            }
        }
        catch (boRuntimeException e)
        {
            logger.error("",e); //$NON-NLS-1$
            return false;
        }
        return true;
    }

    public static boolean beforeSaveTemplateVersion(boObject templateVersion)
    {
        try
        {
            int nActive = countActiveVersion(templateVersion.getEboContext(),
                               templateVersion.getParent().getBoui(),
                               templateVersion.getBoui());
            boolean activo = "1".equals(templateVersion.getAttribute("activo").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            if(activo && nActive > 0)
            {
                templateVersion.addErrorMessage(Messages.getString("Helper.505")); //$NON-NLS-1$
                return false;
            }
            //colocar na bridge do pai e filho os objectos
            boObject parentTemplate = null;
            if(templateVersion.exists())
            {
                parentTemplate = templateVersion.getParent();
            }
            boObject template = templateVersion.getAttribute("modelo").getObject(); //$NON-NLS-1$
            template.getAttribute("activo").setValueString(templateVersion.getAttribute("activo").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            template.getAttribute("ordem").setValueLong(templateVersion.getAttribute("ordem").getValueLong()); //$NON-NLS-1$ //$NON-NLS-2$
            boBridgeIterator bit = templateVersion.getBridge("share").iterator(); //$NON-NLS-1$
            bit.beforeFirst();
            template.getBridge("share").truncate(); //$NON-NLS-1$
            if(parentTemplate != null)
            {
                parentTemplate.getBridge("share").truncate(); //$NON-NLS-1$
            }
            while(bit.next())
            {
                long shareBoui = bit.currentRow().getValueLong();
                template.getBridge("share").add(shareBoui); //$NON-NLS-1$
                if(parentTemplate != null)
                {
                    parentTemplate.getBridge("share").add(shareBoui); //$NON-NLS-1$
                }
            }
        }
        catch (boRuntimeException e)
        {
            logger.error("",e); //$NON-NLS-1$
            return false;
        }
        return true;
    }


    private static boolean foundDestinatario(boObject gtTemplate, String brName) throws boRuntimeException
    {
        boBridgeIterator bit = gtTemplate.getBridge(brName).iterator();
        bit.beforeFirst();
        boObject query, parametro;
        boolean foundDest = false;
        while(bit.next() && !foundDest)
        {
            query = bit.currentRow().getObject();
            parametro = query.getAttribute("parametro").getObject(); //$NON-NLS-1$
            if("1".equals(parametro.getAttribute("destinatario").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
            {
                foundDest = true;
            }
        }
        return foundDest;
    }

    public static void redirectToClassification(boObject msg) throws boRuntimeException
   {
        try
        {
            boObjectList list = boObjectList.list(msg.getEboContext(), "Select Ebo_Document where msg = " + msg.getBoui()); //$NON-NLS-1$
            list.beforeFirst();
            long docBoui = -1;
            if(list.next())
            {
                docBoui = list.getObject().getBoui();
            }
            else
            {
                boObject mailDoc = boObject.getBoManager().createObject(msg.getEboContext(),"Ebo_Document"); //$NON-NLS-1$
                mailDoc.getAttribute("segmento").setValueString(Modulo.getModulo()); //$NON-NLS-1$
                mailDoc.getAttribute("description").setValueString(msg.getAttribute("name").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                mailDoc.getAttribute("dtRegisto").setValueDate(msg.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                mailDoc.getAttribute("dtDoc").setValueDate(msg.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                mailDoc.getAttribute("dtEntrada").setValueDate(msg.getAttribute("dtEfectiv").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                mailDoc.getAttribute("msg").setObject(msg); //$NON-NLS-1$
                mailDoc.getAttribute("estado").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                mailDoc.update();
                docBoui = mailDoc.getBoui();
            }
            XwfController control = (XwfController)msg.getEboContext().getController();
            StringBuffer sb = new StringBuffer("ebo_document_generaleditclassif.jsp?method=edit&boui="); //$NON-NLS-1$
            sb.append(docBoui);
            if(control.getRuntimeActivity() != null)
            {
               control.getRuntimeActivity().setSendRedirect(sb.toString());
            }
        }
        catch (Exception e)
        {

        }
   }

   public static long getMsgDocument(EboContext boctx, long msgBoui) throws boRuntimeException
   {
        long docBoui = -1;
        try
        {
            boObject msg = boObject.getBoManager().loadObject(boctx, msgBoui);
            boObjectList list = boObjectList.list(msg.getEboContext(), "Select Ebo_Document where msg = " + msgBoui); //$NON-NLS-1$
            list.beforeFirst();
            if(list.next())
            {
                docBoui = list.getObject().getBoui();
            }
            else
            {
                boolean docFound = false;

                boBridgeIterator it = msg.getBridge("binaryDocuments").iterator(); //$NON-NLS-1$
                while( it.next( ) )
                {
                    if ( it.currentRow().getObject().getAttribute("msg").getValueObject() != null ) //$NON-NLS-1$
                    {
                        docBoui = it.currentRow().getValueLong();
                        docFound = true;
                        break;
                    }
                }

                if( !docFound )
                {
                    boObject mailDoc = boObject.getBoManager().createObject(msg.getEboContext(),"Ebo_Document"); //$NON-NLS-1$
                    mailDoc.getAttribute("segmento").setValueString(Modulo.getModulo()); //$NON-NLS-1$
                    mailDoc.getAttribute("description").setValueString(msg.getAttribute("name").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                    mailDoc.getAttribute("dtRegisto").setValueDate(msg.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                    mailDoc.getAttribute("dtDoc").setValueDate(msg.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                    mailDoc.getAttribute("dtEntrada").setValueDate(msg.getAttribute("dtEfectiv").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                    mailDoc.getAttribute("msg").setObject(msg); //$NON-NLS-1$
                    mailDoc.getAttribute("estado").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                    mailDoc.update();
                    docBoui = mailDoc.getBoui();
                }
            }
        }
        catch (Exception e)
        {
            logger.error("",e); //$NON-NLS-1$
        }
        return docBoui;
   }

   public static boolean classificationHiddenWhen(boObject msg)
    {
        try
        {
            //if(!msg.exists() || msg.getAttribute("usedTemplate").getValueLong() > 0)
            if( msg.getAttribute("usedTemplate").getValueLong() > 0 ) //$NON-NLS-1$
            {
                return true;
            }
        }
        catch (boRuntimeException e)
        {

        }
        return false;
    }

    public static boolean beforeSaveSMSEmailTemp(boObject smsEmail) throws boRuntimeException
   {
        String aux = smsEmail.getAttribute("nome").getValueString(); //$NON-NLS-1$
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try
        {
            boolean historico = "1".equals(smsEmail.getAttribute("historico").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            if(!historico)
            {
                con = smsEmail.getEboContext().getConnectionData();
                StringBuffer sql = new StringBuffer("select 1 from GESTEMP_SMSTexto where NOME = ? and boui <> ? and historico <> '1'"); //$NON-NLS-1$
                boolean isSMSLivre = false;
                pst = con.prepareStatement(sql.toString());
                String clsName = smsEmail.getName();
                if("GESTEMP_SMSTemp".equals(clsName)) //$NON-NLS-1$
                {
                    clsName = "GESTEMP_SMSTexto"; //$NON-NLS-1$
                    isSMSLivre = true;
                }
                String ss = smsEmail.getAttribute("nome").getValueString(); //$NON-NLS-1$
                pst.setString(1, smsEmail.getAttribute("nome").getValueString()); //$NON-NLS-1$
                pst.setLong(2, smsEmail.getBoui());
                rs = pst.executeQuery();
                if(rs.next())
                {
                    if(isSMSLivre)
                    {
                        if("1".equals(smsEmail.getAttribute("tipo").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
                        {
                            smsEmail.addErrorMessage(Messages.getString("Helper.563")); //$NON-NLS-1$
                        }
                        else
                        {
                            smsEmail.addErrorMessage(Messages.getString("Helper.564")); //$NON-NLS-1$
                        }
                    }
                    else
                    {
                        smsEmail.addErrorMessage(Messages.getString("Helper.565")); //$NON-NLS-1$
                    }
                    return false;
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("", e); //$NON-NLS-1$
        }
        finally
        {
            try{if(rs != null) rs.close();}catch(Exception e){}
            try{if(pst != null) pst.close();}catch(Exception e){}
        }
        return true;
   }

   public static String getSMSLivreHeader(EboContext boctx)
   {
        String toRet = null;
        try
        {
            boObjectList l = boObjectList.list(boctx, "select GESTEMP_SMSTemp where tipo = ?", new Object[]{new String("1")}, 1, 1, true); //$NON-NLS-1$ //$NON-NLS-2$
            l.beforeFirst();
            if(l.next())
            {
                String tempText = l.getObject().getAttribute("texto").getValueString(); //$NON-NLS-1$
                boObject perf = boObject.getBoManager().loadObject(boctx, boctx.getBoSession().getPerformerBoui());
                MergeResultSetBoObject rsBo = new MergeResultSetBoObject(perf);
                TextTemplate tt = new TextTemplate();
                tt.open(tempText);
                tt.setDataSource(rsBo);
                tt.process();
                toRet = tt.getResult();
                rsBo.close();
            }
        }
        catch(Exception e)
        {
            logger.error(e);
        }
        return toRet;
   }

   public static String getSMSLivreFooter(EboContext boctx)
   {
        String toRet = null;
        try
        {
            boObjectList l = boObjectList.list(boctx, "select GESTEMP_SMSTemp where tipo = ?", new Object[]{new String("2")}, 1, 1, true); //$NON-NLS-1$ //$NON-NLS-2$
            l.beforeFirst();
            if(l.next())
            {
                String tempText = l.getObject().getAttribute("texto").getValueString(); //$NON-NLS-1$
                boObject perf = boObject.getBoManager().loadObject(boctx, boctx.getBoSession().getPerformerBoui());
                MergeResultSetBoObject rsBo = new MergeResultSetBoObject(perf);
                TextTemplate tt = new TextTemplate();
                tt.open(tempText);
                tt.setDataSource(rsBo);
                tt.process();
                toRet = tt.getResult();
                rsBo.close();
            }
        }
        catch(Exception e)
        {
            logger.error(e);
        }
        return toRet;
   }

   public static String getFaxNumber(EboContext boctx, GtTemplate template) throws boRuntimeException
   {
        String toRet = ""; //$NON-NLS-1$
        GtQuery []queries = template.getQueries();
        boolean foundDest = false;
        for (int i = 0; !foundDest && i < queries.length; i++)
        {
            if(queries[i].getParametro() != null && queries[i].getParametro().destinatario())
            {
                foundDest = true;
                if( queries[i].getParametro().getValue() != null )
                {
                    Long value = (Long)queries[i].getParametro().getValue().getValue();
                    if(value != null && value.longValue() > 0)
                    {
                        boObject o = boObject.getBoManager().loadObject(boctx, value.longValue());
                        if("Pessoa".equals(o.getName())) //$NON-NLS-1$
                        {
                            if(o.getAttribute("moradaPrincipal").getObject() != null) //$NON-NLS-1$
                            {
                                boObject morada = o.getAttribute("moradaPrincipal").getObject(); //$NON-NLS-1$
                                toRet = morada.getAttribute("telex_fax").getValueString(); //$NON-NLS-1$
                            }
                        }
                        else if("Ebo_Group_CC".equals(o.getName()) || "Ebo_Perf_Lus".equals(o.getName())) //$NON-NLS-1$ //$NON-NLS-2$
                        {
                            if(o.getAttribute("faxNumber").getValueString() != null && o.getAttribute("faxNumber").getValueString().trim().length() > 0) //$NON-NLS-1$ //$NON-NLS-2$
                            {
                                toRet = o.getAttribute("faxNumber").getValueString(); //$NON-NLS-1$
                            }
                            else if(o.getAttribute("pessoa").getObject() != null) //$NON-NLS-1$
                            {
                                boObject groupPessoa = o.getAttribute("pessoa").getObject(); //$NON-NLS-1$
                                if(groupPessoa.getAttribute("moradaPrincipal").getObject() != null) //$NON-NLS-1$
                                {
                                    boObject morada = groupPessoa.getAttribute("moradaPrincipal").getObject(); //$NON-NLS-1$
                                    toRet = morada.getAttribute("telex_fax").getValueString(); //$NON-NLS-1$
                                }
                            }
                        }
                        else
                        {
                            if(o.getAttribute("fax") != null) //$NON-NLS-1$
                                toRet = o.getAttribute("fax").getValueString(); //$NON-NLS-1$
                        }
                    }
                }
            }
        }
        return toRet;
   }
   public static String getEmailAddress(EboContext boctx, GtTemplate template) throws boRuntimeException
   {
        String toRet = ""; //$NON-NLS-1$
        GtQuery []queries = template.getQueries();
        boolean foundDest = false;
        for (int i = 0; !foundDest && i < queries.length; i++)
        {
            if(queries[i].getParametro() != null && queries[i].getParametro().destinatario())
            {
                foundDest = true;
                if( queries[i].getParametro() != null && queries[i].getParametro().getValue() != null )
                {
                    Long value = (Long)queries[i].getParametro().getValue().getValue();
                    if(value != null && value.longValue() > 0)
                    {
                        boObject o = boObject.getBoManager().loadObject(boctx, value.longValue());
                        if("Pessoa".equals(o.getName())) //$NON-NLS-1$
                        {
                            if(o.getAttribute("moradaPrincipal").getObject() != null) //$NON-NLS-1$
                            {
                                boObject morada = o.getAttribute("moradaPrincipal").getObject(); //$NON-NLS-1$
                                toRet = morada.getAttribute("e_mail").getValueString(); //$NON-NLS-1$
                            }
                        }
                        else if("Ebo_Group_CC".equals(o.getName()) || "Ebo_Perf_Lus".equals(o.getName())) //$NON-NLS-1$ //$NON-NLS-2$
                        {
                            if(o.getAttribute("email").getValueString() != null && o.getAttribute("email").getValueString().trim().length() > 0) //$NON-NLS-1$ //$NON-NLS-2$
                            {
                                toRet = o.getAttribute("email").getValueString(); //$NON-NLS-1$
                            }
                            else if(o.getAttribute("pessoa").getObject() != null) //$NON-NLS-1$
                            {
                                boObject groupPessoa = o.getAttribute("pessoa").getObject(); //$NON-NLS-1$
                                if(groupPessoa.getAttribute("moradaPrincipal").getObject() != null) //$NON-NLS-1$
                                {
                                    boObject morada = groupPessoa.getAttribute("moradaPrincipal").getObject(); //$NON-NLS-1$
                                    toRet = morada.getAttribute("e_mail").getValueString(); //$NON-NLS-1$
                                }
                            }
                        }
                        else
                        {
                            if(o.getAttribute("email") != null) //$NON-NLS-1$
                                toRet = o.getAttribute("email").getValueString(); //$NON-NLS-1$
                        }
                    }
                }
            }
        }
        return toRet;
   }

   public static void orderById(boObject lov) throws boRuntimeException
    {
        LovManager.orderBy(lov, "id"); //$NON-NLS-1$
    }

    public static void orderByTexto(boObject lov) throws boRuntimeException
    {
        LovManager.orderBy(lov, "texto"); //$NON-NLS-1$
    }
    public static boolean actSendCancelHiddenWhen(boObject actv)
    {
        try
        {
            //se a actv estiver fechada e a msg tiver sido gerada por um template
            //e estiver na fila impressão
            //então o método está disponível
            if(actv != null && "close".equals(actv.getStateAttribute("runningState").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
            {
                boObject msg = null;
                if(actv.getAttribute("message") != null) //$NON-NLS-1$
                {
                    boObject variable = actv.getAttribute("message").getObject(); //$NON-NLS-1$
                    if(variable != null)
                    {
                        boObject value = variable.getAttribute("value").getObject(); //$NON-NLS-1$
                        if(value != null)
                        {
                            msg = value.getAttribute("valueObject").getObject(); //$NON-NLS-1$
                            if(msg != null)
                            {
                                if("messageLetter".equals(msg.getName())) //$NON-NLS-1$
                                {
                                    //Se não tiver dtEfectiv é pq ainda não foi enviado
                                    //senão fôr impressão central posso cancelar
                                    //caso em que imprimo localmente e a carta tem um erro
                                    if(msg.getAttribute("dtEfectiv").getValueDate() == null || //$NON-NLS-1$
                                       !"1".equals(msg.getAttribute("impCentral").getValueString()) //$NON-NLS-1$ //$NON-NLS-2$
                                    )
                                    {
                                        return false;
                                    }
                                }
                                if("1".equals(msg.getAttribute("error").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
                                {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (boRuntimeException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public static ArrayList getCancelMotives(EboContext boctx) throws boRuntimeException
    {
        StringBuffer[] apresentationStr = null;
        ArrayList toRet = new ArrayList(2);
        StringBuffer[] valuesStr = null;
        boObjectList list = boObjectList.list(boctx, "select Ebo_LOV where name = 'gesMotivoCancel'", 1, 999999999); //$NON-NLS-1$
        list.beforeFirst();
        int i = 0, rc = 0;
        if(list.next())
        {
            boObject motLov = list.getObject();
            bridgeHandler bh = motLov.getBridge("details"); //$NON-NLS-1$

            toRet = new ArrayList(2);
            rc = (int)bh.getRecordCount();
            apresentationStr = new StringBuffer[rc];
            valuesStr = new StringBuffer[rc];
            boBridgeIterator bit = bh.iterator();
            bit.beforeFirst();

            while(bit.next())
            {
                apresentationStr[i] =  new StringBuffer(bit.currentRow().getObject().getAttribute("description").getValueString()); //$NON-NLS-1$
                valuesStr[i] = new StringBuffer(bit.currentRow().getObject().getAttribute("value").getValueString()); //$NON-NLS-1$
                i++;
            }
        }

        if(i == 0)
        {
            return null;
        }
        else if(i < rc)
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

    public static GtValue setImg(Object fieldValue) throws IOException, SQLException
    {
        GtValue toRet = new GtValue();
        if(fieldValue instanceof InputStream)
        {
            toRet.addValue(inputStreamToBytes((InputStream) fieldValue));
        }
        else if(fieldValue instanceof Blob)
        {
            InputStream is = null;
            try
            {
                is = ((Blob)fieldValue).getBinaryStream();
                toRet.addValue(inputStreamToBytes(is));
            }
            catch (Exception e)
            {
                logger.error("", e); //$NON-NLS-1$
            }
            finally
            {
                if(is != null) {try{is.close();}catch(Exception e){/*IGNORE*/}};
            }
        }
        else
        {
            toRet.addValue(fieldValue);
        }
        return toRet;
    }

    public static byte[] inputStreamToBytes(InputStream in) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        boolean empty = true;

        while((len = in.read(buffer)) >= 0)
        {
            out.write(buffer, 0, len);
            empty = false;
        }
        out.close();
        if(empty) return null;
        return out.toByteArray();
    }

    public static boObject setMessage(EboContext boctx, GtTemplate template, String rosto, String doc, String text, String textAssunto) throws boRuntimeException
    {
        //mensagem normal
//        logger.debug("--------------------------------Geração da Actividade-----------------------------------------");
        long ti = System.currentTimeMillis();
        boObject mailobject = null;
        boObject actvSend = null;
        boolean email = false, sms = false, fax = false, carta = false;
        boolean truncatedDocs = false;
        boolean reply = false;

        long actvBoui = template.getActvSendBoui();
        actvSend = boObject.getBoManager().loadObject(boctx, actvBoui);
        if(actvSend != null)
        {
            boObject xwfVar = actvSend.getAttribute("message").getObject(); //$NON-NLS-1$
            if(xwfVar != null)
            {
                boObject varValue = xwfVar.getAttribute("value").getObject(); //$NON-NLS-1$
                if(varValue != null)
                {
                    mailobject = varValue.getAttribute("valueObject").getObject(); //$NON-NLS-1$
                }
            }
        }

        if(template.getChannel() == template.TYPE_EMAIL)
        {
            email = true;
            mailobject.getAttribute("description").setValueString(text); //$NON-NLS-1$
        }
        else if(template.getChannel() == template.TYPE_CARTA)
        {
            carta = true;
        }
        else if(template.getChannel() == template.TYPE_FAX)
        {
            fax = true;
        }
        else if(template.getChannel() == template.TYPE_SMS)
        {
            sms = true;
            mailobject.getAttribute("textSMS").setValueString(text); //$NON-NLS-1$
        }

        if(carta || fax)
        {
            clearBridge(mailobject, "binaryDocuments");  //$NON-NLS-1$
            //docs
            if(rosto != null)
            {
                boObject docRosto = null;
                iFile ifile = new FSiFile(null,new File(rosto),null);
                docRosto = boObject.getBoManager().createObject(boctx, "Ebo_Document"); //$NON-NLS-1$
                docRosto.getAttribute("description").setValueString(Messages.getString("Helper.634") + getToNameFromMsg(mailobject)); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtRegisto").setValueDate(new Date()); //$NON-NLS-1$
                docRosto.getAttribute("tipoDoc").setValueString("139"); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtDoc").setValueDate(mailobject.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtSaida").setValueDate(mailobject.getAttribute("dtEfectiv").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("registoCTT").setValueString(mailobject.getAttribute("registoCTT").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("codeBarClienteCTT").setValueString(mailobject.getAttribute("docSeq").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("segmento").setValueString(Modulo.getModulo()); //$NON-NLS-1$
                docRosto.getAttribute("file").setValueiFile(ifile); //$NON-NLS-1$
                docRosto.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(ifile.length())); //$NON-NLS-1$
                docRosto.getAttribute("fileName").setValueString(ifile.getName()); //$NON-NLS-1$
                docRosto.getAttribute("msg").setObject(mailobject); //$NON-NLS-1$
                docRosto.getAttribute("estado").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                mailobject.getBridge("binaryDocuments").add(docRosto.getBoui()); //$NON-NLS-1$
            }
            if(doc != null)
            {
                boObject docTemplate = null;
                iFile ifile = new FSiFile(null,new File(doc),null);
                docTemplate = boObject.getBoManager().createObject(boctx, "Ebo_Document"); //$NON-NLS-1$
                docTemplate.getAttribute("description").setValueString(Messages.getString("Helper.656") + getToNameFromMsg(mailobject)); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtRegisto").setValueDate(new Date()); //$NON-NLS-1$
                docTemplate.getAttribute("tipoDoc").setValueString("139"); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtDoc").setValueDate(mailobject.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtSaida").setValueDate(mailobject.getAttribute("dtEfectiv").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("registoCTT").setValueString(mailobject.getAttribute("registoCTT").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("codeBarClienteCTT").setValueString(mailobject.getAttribute("docSeq").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("segmento").setValueString(Modulo.getModulo()); //$NON-NLS-1$
                docTemplate.getAttribute("file").setValueiFile(ifile); //$NON-NLS-1$
                docTemplate.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(ifile.length())); //$NON-NLS-1$
                docTemplate.getAttribute("fileName").setValueString(ifile.getName()); //$NON-NLS-1$
                docTemplate.getAttribute("msg").setObject(mailobject); //$NON-NLS-1$
                docTemplate.getAttribute("estado").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
                mailobject.getBridge("binaryDocuments").add(docTemplate.getBoui()); //$NON-NLS-1$
            }
        }
        if(email && (rosto != null || doc != null))
        {
            truncatedDocs = true;
            bridgeHandler bh = mailobject.getBridge("documents"); //$NON-NLS-1$
            boBridgeIterator bit = bh.iterator();
            boObject docObj = null;
            String auxS;
            while(bit.next())
            {
                docObj = bit.currentRow().getObject();
                auxS = docObj.getAttribute("description").getValueString();  //$NON-NLS-1$
                if(auxS != null &&
                   (auxS.startsWith(Messages.getString("Helper.678")) || //$NON-NLS-1$
                    auxS.startsWith(Messages.getString("Helper.679")) //$NON-NLS-1$
                   )
                )
                {
                    bh.moveTo( bit.getRow() );
                    bh.remove();
                    bit.previous();
                }
            }
            //docs
            if(rosto != null)
            {
                boObject docRosto = null;
                //vou transformar em PDF
                File pdfFile = convertToPdf(rosto, "rosto"); //$NON-NLS-1$
                iFile ifile = new FSiFile(null,pdfFile,null);
//                iFile ifile = new FSiFile(null,new File(rosto),null);
                docRosto = boObject.getBoManager().createObject(boctx, "Ebo_Document"); //$NON-NLS-1$
                docRosto.getAttribute("description").setValueString(Messages.getString("Helper.683") + getToNameFromMsg(mailobject)); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtRegisto").setValueDate(new Date()); //$NON-NLS-1$
                docRosto.getAttribute("tipoDoc").setValueString("139"); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtDoc").setValueDate(mailobject.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("dtSaida").setValueDate(mailobject.getAttribute("dtEfectiv").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("registoCTT").setValueString(mailobject.getAttribute("registoCTT").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("codeBarClienteCTT").setValueString(mailobject.getAttribute("docSeq").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docRosto.getAttribute("segmento").setValueString(Modulo.getModulo()); //$NON-NLS-1$
                docRosto.getAttribute("file").setValueiFile(ifile); //$NON-NLS-1$
                docRosto.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(ifile.length())); //$NON-NLS-1$
                docRosto.getAttribute("fileName").setValueString(ifile.getName()); //$NON-NLS-1$
                docRosto.getAttribute("msg").setObject(mailobject); //$NON-NLS-1$
                docRosto.getAttribute("estado").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$

                mailobject.getBridge("documents").add(docRosto.getBoui()); //$NON-NLS-1$
            }
            if(doc != null)
            {
                boObject docTemplate = null;
                //vou transformar em PDF
                File pdfFile = convertToPdf(doc, "documento"); //$NON-NLS-1$
                iFile ifile = new FSiFile(null,pdfFile,null);
//                iFile ifile = new FSiFile(null,new File(doc),null);
                docTemplate = boObject.getBoManager().createObject(boctx, "Ebo_Document"); //$NON-NLS-1$
                docTemplate.getAttribute("description").setValueString(Messages.getString("Helper.706") + getToNameFromMsg(mailobject)); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtRegisto").setValueDate(new Date()); //$NON-NLS-1$
                docTemplate.getAttribute("tipoDoc").setValueString("139"); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtDoc").setValueDate(mailobject.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("dtSaida").setValueDate(mailobject.getAttribute("dtEfectiv").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("registoCTT").setValueString(mailobject.getAttribute("registoCTT").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("codeBarClienteCTT").setValueString(mailobject.getAttribute("docSeq").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                docTemplate.getAttribute("segmento").setValueString(Modulo.getModulo()); //$NON-NLS-1$
                docTemplate.getAttribute("file").setValueiFile(ifile); //$NON-NLS-1$
                docTemplate.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(ifile.length())); //$NON-NLS-1$
                docTemplate.getAttribute("fileName").setValueString(ifile.getName()); //$NON-NLS-1$
                docTemplate.getAttribute("msg").setObject(mailobject); //$NON-NLS-1$
                docTemplate.getAttribute("estado").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$

                mailobject.getBridge("documents").add(docTemplate.getBoui()); //$NON-NLS-1$
            }
        }

        return actvSend;
    }

    public static String getToName(boObject destinatario) throws boRuntimeException
    {
        if(destinatario != null)
        {
            String nome = destinatario.getAttribute("nome").getValueString(); //$NON-NLS-1$
            return (nome == null || nome.length() == 0) ? Messages.getString("Helper.727"):ClassUtils.capitalize(nome); //$NON-NLS-1$
        }
        return Messages.getString("Helper.727"); //$NON-NLS-1$
    }

    public static String getToNameFromMsg(boObject msg) throws boRuntimeException
    {
        boBridgeIterator bit = msg.getBridge("to").iterator(); //$NON-NLS-1$
        bit.beforeFirst();
        if(bit.next())
        {
            boObject destinatario = bit.currentRow().getObject();
            String nome = destinatario.getAttribute("name").getValueString(); //$NON-NLS-1$
            return (nome == null || nome.length() == 0) ? Messages.getString("Helper.727"):ClassUtils.capitalize(nome); //$NON-NLS-1$
        }
        return Messages.getString("Helper.727"); //$NON-NLS-1$
    }

    public static String[] getCodPostal(EboContext boctx, GtTemplate template) throws boRuntimeException
    {
        boObject destinatario = getDestinatario(boctx, template);
        String toRet[]={"0000", "000"}; //$NON-NLS-1$ //$NON-NLS-2$
        if(destinatario == null)
        {
            throw new boRuntimeException("", Messages.getString("Helper.8"), null); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if("Pessoa".equals(destinatario.getName())) //$NON-NLS-1$
        {
            boObject morada = destinatario.getAttribute("moradaPrincipal").getObject(); //$NON-NLS-1$
            String aux = morada.getAttribute("codigo_postal").getValueString(); //$NON-NLS-1$
            if(aux != null && aux.length() == 4 && isNumber(aux))
            {
                toRet[0] = aux;
                aux = morada.getAttribute("sufixo_postal").getValueString(); //$NON-NLS-1$
                if(aux != null && aux.length() == 3 && isNumber(aux))
                {
                    toRet[1] = aux;
                }
            }
        }
        else if("Ebo_Perf_Lus".equals(destinatario.getName()) ||  //$NON-NLS-1$
                "dmUser".equals(destinatario.getName()))//é sempre gerado por template //$NON-NLS-1$
        {
            boObject ccust = destinatario.getAttribute("centrocusto").getObject(); //$NON-NLS-1$
            if(ccust != null)
            {
                boObject pessoa = ccust.getAttribute("pessoa").getObject(); //$NON-NLS-1$
                if(pessoa != null)
                {
                    boObject morada = pessoa.getAttribute("moradaPrincipal").getObject(); //$NON-NLS-1$
                    String aux = morada.getAttribute("codigo_postal").getValueString(); //$NON-NLS-1$
                    if(aux != null && aux.length() == 4 && isNumber(aux))
                    {
                        toRet[0] = aux;
                        aux = morada.getAttribute("sufixo_postal").getValueString(); //$NON-NLS-1$
                        if(aux != null && aux.length() == 3 && isNumber(aux))
                        {
                            toRet[1] = aux;
                        }
                    }
                }
            }
        }
        else
        {
            if (destinatario.getAttribute("correspondencia_address") != null && destinatario.getAttribute("correspondencia_address").getObject() != null) //$NON-NLS-1$ //$NON-NLS-2$
            {
                boObject correspAdd = destinatario.getAttribute("correspondencia_address").getObject(); //$NON-NLS-1$
                String cPostal = correspAdd.getAttribute("cpostal").getValueString(); //$NON-NLS-1$
                String country = correspAdd.getAttribute("country").getValueString(); //$NON-NLS-1$
                String aux = null;
                if(cPostal != null && !"".equals(cPostal)) //$NON-NLS-1$
                {
                    if(cPostal.indexOf("-") > -1 && cPostal.length() == 8) //completo //$NON-NLS-1$
                    {
                        aux = cPostal.split("-")[0]; //$NON-NLS-1$
                        if(aux != null && aux.length() == 4 && isNumber(aux))
                        {
                            toRet[0] = aux;
                            aux = cPostal.split("-")[1]; //$NON-NLS-1$
                            if(aux != null && aux.length() == 3 && isNumber(aux))
                            {
                                toRet[1] = aux;
                            }
                        }
                    }
                    else if(cPostal.indexOf("-") == -1 && cPostal.length() == 4) //só a primeira parte //$NON-NLS-1$
                    {
                        if(cPostal != null && cPostal.length() == 4 && isNumber(cPostal))
                        {
                            toRet[0] = cPostal;
                        }
                    }
                    else if(cPostal.indexOf("-") > -1 && cPostal.length() == 5) //$NON-NLS-1$
                    {
                        aux = cPostal.split("-")[0]; //$NON-NLS-1$
                        if(aux != null && aux.length() == 4 && isNumber(aux))
                        {
                            toRet[0] = aux;
                        }
                    }
                }
            }
        }
        return toRet;
    }

    private static boolean isNumber(String n)
    {
        try{Long.parseLong(n); return true;}catch(Exception e){}
        return false;
    }

    public static String getCodeBarClienteCTT(EboContext boctx, GtTemplate template) throws boRuntimeException
    {
        try
        {
            String cPostal[] = getCodPostal(boctx, template);
            //String addInfo = template.getMsgID();

            String toRet = "";
            //TODO:Implement Interface LUSITANIA
            //String toRet = Codigo4Estados.getCodigo(cPostal[0], cPostal[1] );
            if(toRet.startsWith("(") && toRet.endsWith(")")) //$NON-NLS-1$ //$NON-NLS-2$
                toRet = toRet.substring(1, toRet.length() - 1);
            return toRet;
        }
        catch (boRuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new boRuntimeException("", e.getMessage(), e); //$NON-NLS-1$
        }
    }

   public static boolean beforeSaveTag(boObject tag) throws boRuntimeException
   {
        String aux = tag.getAttribute("nome").getValueString(); //$NON-NLS-1$

        if(aux != null && aux.split(" ").length != 1) //$NON-NLS-1$
        {
            tag.addErrorMessage(Messages.getString("Helper.9")); //$NON-NLS-1$
            return false;
        }

        if(tag.getAttribute("javaCode").getValueString() != null &&  //$NON-NLS-1$
            !"".equals(tag.getAttribute("javaCode").getValueString().trim()) && //$NON-NLS-1$ //$NON-NLS-2$
            "0".equals(tag.getAttribute("applyWord").getValueString()) && //$NON-NLS-1$ //$NON-NLS-2$
            "0".equals(tag.getAttribute("applyText").getValueString()) //$NON-NLS-1$ //$NON-NLS-2$
        )
        {
            tag.addErrorMessage(Messages.getString("Helper.10")); //$NON-NLS-1$
            return false;
        }

        if(tag.getAttribute("javaCode").getValueString() == null ||  //$NON-NLS-1$
            "".equals(tag.getAttribute("javaCode").getValueString().trim()) //$NON-NLS-1$ //$NON-NLS-2$
        )
        {
            tag.getAttribute("applyWord").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
            tag.getAttribute("applyText").setValueString("0"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try
        {
            con = tag.getEboContext().getConnectionData();
            StringBuffer sql = new StringBuffer("select 1 from GESTEMP_Tag where NOME = ? and boui <> ?"); //$NON-NLS-1$
            pst = con.prepareStatement(sql.toString());
            pst.setString(1, aux);
            pst.setLong(2, tag.getBoui());
            rs = pst.executeQuery();
            if(rs.next())
            {
                tag.addErrorMessage(Messages.getString("Helper.782")); //$NON-NLS-1$
                return false;
            }
        }
        catch (SQLException e)
        {
            logger.error("", e); //$NON-NLS-1$
        }
        finally
        {
            try{if(rs != null) rs.close();}catch(Exception e){}
            try{if(pst != null) pst.close();}catch(Exception e){}
        }

        return true;
   }

   public static void setDocDates(boObject message) throws boRuntimeException
   {
        if("messageLetter".equals(message.getName()) || "messageFax".equals(message.getName())) //$NON-NLS-1$ //$NON-NLS-2$
        {
            boBridgeIterator bit = message.getBridge("binaryDocuments").iterator(); //$NON-NLS-1$
            bit.beforeFirst();
            boObject eboDoc = null;
            while(bit.next())
            {
                eboDoc = bit.currentRow().getObject();
                if(Helper.isLetterGeneratedDoc(eboDoc.getAttribute("fileName").getValueString())) //$NON-NLS-1$
                {
                    eboDoc.getAttribute("dtDoc").setValueDate(message.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                    eboDoc.getAttribute("dtSaida").setValueDate(message.getAttribute("dtEfectiv").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                    eboDoc.update();
                }
            }
        }
        else if("messageMail".equals(message.getName())) //$NON-NLS-1$
        {
            boBridgeIterator bit = message.getBridge("documents").iterator(); //$NON-NLS-1$
            bit.beforeFirst();
            boObject eboDoc = null;
            while(bit.next())
            {
                eboDoc = bit.currentRow().getObject();
                if(isMailGeneratedDoc(eboDoc.getAttribute("fileName").getValueString())) //$NON-NLS-1$
                {
                    eboDoc.getAttribute("dtDoc").setValueDate(message.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                    eboDoc.getAttribute("dtSaida").setValueDate(message.getAttribute("dtdoc").getValueDate()); //$NON-NLS-1$ //$NON-NLS-2$
                    eboDoc.update();
                }
            }
        }
   }

   public static String getDescriptionTipo7(EboContext boctx, String bouis)
   {
        if(bouis == null || bouis.length() == 0) return null;
        String[] toRet = getDescriptionTipo7(boctx, bouis.split(";")); //$NON-NLS-1$
        if(toRet == null) return bouis;
        if(toRet.length > 0) return toRet[0];
        return null;
   }

   public static String [] getDescriptionTipo7(EboContext boctx, String[] bouis)
   {
        if(bouis == null || bouis.length == 0) return new String[0];
        String[] toRet = new String[bouis.length] ;
        String description = null;
        if(bouis != null && bouis.length > 0)
        {
            boObject o = null;
            long b;
            for (int i = 0; i < bouis.length; i++)
            {
                try
                {
                    b = Long.parseLong(bouis[i]);
                    o = boObject.getBoManager().loadObject(boctx, b);
                    toRet[i] = o.getAttribute("texto").getValueString(); //$NON-NLS-1$
                }
                catch(Exception e)
                {
                    return null;
                }
            }
        }
        return toRet;
   }

   public static String defaultValueCampoParametroObjecto(boObject gestempCampoObj)
   {
        try
        {
            if("GESTEMP_Query".equals(gestempCampoObj.getParent().getName()) || //$NON-NLS-1$
                "GESTEMP_JavaQuery".equals(gestempCampoObj.getParent().getName()) //$NON-NLS-1$
            )
            {
                return boConvertUtils.convertToString(gestempCampoObj.getParent().getAttribute("parametro").getValueObject(), gestempCampoObj.getAttribute("parametro")); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return null;
        }
        catch (Exception e){}
        return null;
   }

   public static String defaultValueCampoObjecto(boObject gestempCampoObj)
   {
        try
        {
            if(gestempCampoObj.getAttribute("parametro").getObject() != null) //$NON-NLS-1$
            {
                return boConvertUtils.convertToString(gestempCampoObj.getAttribute("parametro").getObject().getAttribute("objecto").getValueObject(), gestempCampoObj.getAttribute("objecto")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            else if("GESTEMP_CampoNObjecto".equals(gestempCampoObj.getParent().getName())) //$NON-NLS-1$
            {
                boObject gestCampoListObj = gestempCampoObj.getParent();
                String clsName = gestCampoListObj.getAttribute("objecto").getObject().getAttribute("name").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                boDefHandler bodef = boDefHandler.getBoDefinition(clsName);
                String referenceObjName = bodef.getAttributeRef(gestCampoListObj.getAttribute("helper").getValueString()).getReferencedObjectName(); //$NON-NLS-1$
                boObjectList clsList = boObjectList.list(gestempCampoObj.getEboContext(), "select Ebo_ClsReg where name = '"+referenceObjName+"'", 1, 1); //$NON-NLS-1$ //$NON-NLS-2$
                clsList.beforeFirst();
                if(clsList.next())
                {
                    return boConvertUtils.convertToString(clsList.getObject().getBoui(), gestempCampoObj.getAttribute("objecto")); //$NON-NLS-1$
                }
            }
            else if("GESTEMP_CampoNFormula".equals(gestempCampoObj.getParent().getName())) //$NON-NLS-1$
            {
                boObject gestCampoListFormula = gestempCampoObj.getParent();
                return boConvertUtils.convertToString(gestCampoListFormula.getAttribute("objecto").getObject().getBoui(), gestempCampoObj.getAttribute("objecto")); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return null;
        }
        catch (Exception e){}
        return null;
   }

   public static void createVersion(boObject template) throws boRuntimeException
   {
        boObject tempVersion = cloneTemplate(template);
        boObject version = boObject.getBoManager().createObject(template.getEboContext(), "GESTEMP_TempVersion"); //$NON-NLS-1$
        long versionNumber = getVersionNumber(template.getEboContext(), template.getBoui());
        tempVersion.getAttribute("versao").setValueLong(versionNumber); //$NON-NLS-1$
        version.getAttribute("code").setValueString(template.getAttribute("code").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
        version.getAttribute("nome").setValueString(template.getAttribute("nome").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
        version.getAttribute("versao").setValueLong(versionNumber); //$NON-NLS-1$
        version.getAttribute("ordem").setValueLong(template.getAttribute("ordem").getValueLong()); //$NON-NLS-1$ //$NON-NLS-2$
        version.getAttribute("modelo").setObject(tempVersion); //$NON-NLS-1$
        boBridgeIterator bit = template.getBridge("share").iterator(); //$NON-NLS-1$
        bit.beforeFirst();
        while(bit.next())
        {
            version.getBridge("share").add(bit.currentRow().getValueLong()); //$NON-NLS-1$
        }
        template.getBridge("versoes").add(version.getBoui()); //$NON-NLS-1$
   }

   private static long getVersionNumber(EboContext boctx,  long templateBoui)
   {
        PreparedStatement pst = null;
        ResultSet rs = null;
        Connection con = null;

        try
        {
            con = boctx.getConnectionData();
            pst = con.prepareStatement("select max(v.versao) from GESTEMP_TempVersion v, GESTEMP_TEMPLATE$VERSOES tv where tv.parent$ = ? and tv.child$ = v.boui"); //$NON-NLS-1$
            pst.setLong(1, templateBoui);
            rs = pst.executeQuery();
            if(rs.next())
            {
                return rs.getLong(1) + 1;
            }
        }
        catch (SQLException e)
        {

        }
        finally
        {
            try{if(rs != null){rs.close();}}catch(Exception e){/*ignore*/}
            try{if(pst != null){pst.close();}}catch(Exception e){/*ignore*/}
        }
        return 1;
   }

   private static long getNewAttributeBoui(boObject template, String helper) throws boRuntimeException
   {
        if(helper != null && !"".equals(helper)) //$NON-NLS-1$
        {
            String path[] = helper.split("\\."); //$NON-NLS-1$
            if(path.length > 0 || path.length <= 3)
            {
                boBridgeIterator bit = template.getBridge("queries").iterator(); //$NON-NLS-1$
                bit.beforeFirst();
                boolean foundQ = false, foundC = false;
                while(bit.next() && !foundQ)
                {
                    boObject querie = bit.currentRow().getObject();
                    if(path[0].equals(querie.getAttribute("nome").getValueString())) //$NON-NLS-1$
                    {
                        foundQ = true;
                        if(path.length == 1)
                        {
                            logger.info(helper + "->" + querie.getBoui()); //$NON-NLS-1$
                            return querie.getBoui();
                        }
                        //agora vou andar nos campos da querie
                        boBridgeIterator bit2 = querie.getBridge("campos").iterator(); //$NON-NLS-1$
                        bit2.beforeFirst();
                        while(bit2.next() && !foundC)
                        {
                            boObject campo = bit2.currentRow().getObject();
                            if(path[1].equals(campo.getAttribute("nome").getValueString())) //$NON-NLS-1$
                            {
                                if(path.length == 2)
                                {
                                    foundC = true;
                                    logger.info(helper + "->" + campo.getBoui()); //$NON-NLS-1$
                                    return campo.getBoui();
                                }
                                else
                                {
                                    boBridgeIterator bit3 = campo.getBridge("campos").iterator(); //$NON-NLS-1$
                                    bit3.beforeFirst();
                                    while(bit3.next() && !foundC)
                                    {
                                        campo = bit3.currentRow().getObject();
                                        if(path[2].equals(campo.getAttribute("nome").getValueString())) //$NON-NLS-1$
                                        {
                                            foundC = true;
                                            logger.info(helper + "->" + campo.getBoui()); //$NON-NLS-1$
                                            return campo.getBoui();
                                        }
                                    }
                                    return -1;
                                }
                            }
                        }
                        return -1;
                    }
                }
                if(!foundQ && path.length == 1)
                {//pode ser campo manual
                    bit = template.getBridge("camposManuais").iterator(); //$NON-NLS-1$
                    bit.beforeFirst();
                    boObject campoManual = null;
                    while(bit.next() && !foundQ)
                    {
                        campoManual = bit.currentRow().getObject();
                        if(path[0].equals(campoManual.getAttribute("nome").getValueString())) //$NON-NLS-1$
                        {
                            logger.info(helper + "->" + campoManual.getBoui()); //$NON-NLS-1$
                            return campoManual.getBoui();
                        }
                    }
                }
            }
        }
        return -1;
   }

   public static boObject cloneTemplate(boObject template) throws boRuntimeException
   {
        boObject tempVersion = template.cloneObject();
        tempVersion.getAttribute("historico").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
        tempVersion.getBridge("versoes").truncate(); //$NON-NLS-1$

        //estes campos têm que ser clonados
        tempVersion.getBridge("queries").truncate(); //$NON-NLS-1$
        tempVersion.getBridge("camposManuais").truncate(); //$NON-NLS-1$

        boBridgeIterator bit = template.getBridge("queries").iterator(); //$NON-NLS-1$
        bit.beforeFirst();
        boObject aux = null;
        while(bit.next())
        {
            aux = bit.currentRow().getObject();
            tempVersion.getBridge("queries").add(cloneQuerie(aux).getBoui()); //$NON-NLS-1$
        }

        bit = template.getBridge("camposManuais").iterator(); //$NON-NLS-1$
        bit.beforeFirst();
        while(bit.next())
        {
            aux = bit.currentRow().getObject();
            tempVersion.getBridge("camposManuais").add(cloneCampo(aux).getBoui()); //$NON-NLS-1$
        }


        //classificações tenho que alterar o boui dos atributos do template para os novos
        boBridgeIterator bitClf = tempVersion.getBridge("mapeamentos").iterator(); //$NON-NLS-1$
        bitClf.beforeFirst();
        long newAtBoui = -1;
        aux = null;
        while(bitClf.next())
        {
            aux = bitClf.currentRow().getObject();
            newAtBoui = getNewAttributeBoui(tempVersion, aux.getAttribute("helper").getValueString()); //$NON-NLS-1$
            if(newAtBoui > -1)
            {
                aux.getAttribute("atributo").setValueLong(newAtBoui); //$NON-NLS-1$
            }
        }

        //fax
        if(template.getAttribute("tempFax").getValueLong() > 0) //$NON-NLS-1$
        {
            tempVersion.getAttribute("tempFax").setObject(cloneDoc(template.getAttribute("tempFax").getObject())); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if(template.getAttribute("rostoFax").getValueLong() > 0) //$NON-NLS-1$
        {
            tempVersion.getAttribute("rostoFax").setObject(cloneDoc(template.getAttribute("rostoFax").getObject())); //$NON-NLS-1$ //$NON-NLS-2$
        }
        //Carta
        if(template.getAttribute("tempCarta").getValueLong() > 0) //$NON-NLS-1$
        {
            tempVersion.getAttribute("tempCarta").setObject(cloneDoc(template.getAttribute("tempCarta").getObject())); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if(template.getAttribute("rostoCarta").getValueLong() > 0) //$NON-NLS-1$
        {
            tempVersion.getAttribute("rostoCarta").setObject(cloneDoc(template.getAttribute("rostoCarta").getObject())); //$NON-NLS-1$ //$NON-NLS-2$
        }
        //e-mail
        if(template.getAttribute("tempEmail").getValueLong() > 0) //$NON-NLS-1$
        {
            aux = template.getAttribute("tempEmail").getObject(); //$NON-NLS-1$
            boObject cAux = aux.cloneObject();
            cAux.getAttribute("historico").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
            tempVersion.getAttribute("tempEmail").setObject(cAux); //$NON-NLS-1$
        }
        //sms
        if(template.getAttribute("tempSMS").getValueLong() > 0) //$NON-NLS-1$
        {
            aux = template.getAttribute("tempSMS").getObject(); //$NON-NLS-1$
            boObject cAux = aux.cloneObject();
            cAux.getAttribute("historico").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
            tempVersion.getAttribute("tempSMS").setObject(cAux); //$NON-NLS-1$
        }
        //filaNormal
//        if(template.getAttribute("filaNormal").getValueLong() > 0)
//        {
//            aux = template.getAttribute("filaNormal").getObject();
//            boObject cAux = aux.cloneObject();
//            cAux.getAttribute("historico").setValueString("1");
//            tempVersion.getAttribute("filaNormal").setObject(cAux);
//        }
        //filaRegistado
//        if(template.getAttribute("filaRegistado").getValueLong() > 0)
//        {
//            aux = template.getAttribute("filaRegistado").getObject();
//            boObject cAux = aux.cloneObject();
//            cAux.getAttribute("historico").setValueString("1");
//            tempVersion.getAttribute("filaRegistado").setObject(cAux);
//        }
        //filaRegistadoAR
//        if(template.getAttribute("filaRegistadoAR").getValueLong() > 0)
//        {
//            aux = template.getAttribute("filaRegistadoAR").getObject();
//            boObject cAux = aux.cloneObject();
//            cAux.getAttribute("historico").setValueString("1");
//            tempVersion.getAttribute("filaRegistadoAR").setObject(cAux);
//        }
        return tempVersion;
   }

   public static boolean showVersionButton(boObject template) throws boRuntimeException
   {
        return false;
   }

   private static boObject cloneQuerie(boObject querie) throws boRuntimeException
   {
        boObject cloneQuerie = querie.cloneObject();
        cloneQuerie.getAttribute("historico").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
        if(cloneQuerie.getAttribute("parametro").getValueLong() > 0) //$NON-NLS-1$
        {
           cloneQuerie.getAttribute("parametro").getObject().getAttribute("historico").setValueString("1");          //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        //estes campos têm que ser clonados
        cloneQuerie.getBridge("campos").truncate(); //$NON-NLS-1$

        boBridgeIterator bit = querie.getBridge("campos").iterator(); //$NON-NLS-1$
        bit.beforeFirst();
        boObject aux;
        while(bit.next())
        {
            aux = bit.currentRow().getObject();
            cloneQuerie.getBridge("campos").add(cloneCampo(aux).getBoui()); //$NON-NLS-1$
        }
        return cloneQuerie;
   }

   private static boObject cloneCampo(boObject campo) throws boRuntimeException
   {
        boObject cCampo = campo.cloneObject();
        cCampo.getAttribute("historico").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
        if(cCampo.getAttribute("parametro") != null && cCampo.getAttribute("parametro").getValueLong() > 0) //$NON-NLS-1$ //$NON-NLS-2$
        {
           cCampo.getAttribute("parametro").getObject().getAttribute("historico").setValueString("1");          //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        boBridgeIterator bit = null;
        boObject aux;
        if(campo.getBridge("campos") != null) //$NON-NLS-1$
        {
            //estes campos têm que ser clonados
            cCampo.getBridge("campos").truncate(); //$NON-NLS-1$

            bit = campo.getBridge("campos").iterator(); //$NON-NLS-1$
            bit.beforeFirst();
            while(bit.next())
            {
                aux = bit.currentRow().getObject();
                cCampo.getBridge("campos").add(cloneCampo(aux).getBoui()); //$NON-NLS-1$
            }
        }

        return cCampo;
   }

   private static boObject cloneDoc(boObject doc) throws boRuntimeException
   {
        boObject cDoc = doc.cloneObject();
        cDoc.getAttribute("historico").setValueString("1"); //$NON-NLS-1$ //$NON-NLS-2$
        iFile ifile = doc.getAttribute("file").getValueiFile(); //$NON-NLS-1$

        if(ifile != null)
        {
            String filename = ifile.getName();
            String suffix = MailUtil.getExtension(filename);
            String prefix = MailUtil.getFirstPart(filename);

            File tempFile = MailUtil.createTempFile(prefix, suffix);
            FileOutputStream out = null;
            InputStream in = null;
            try
            {
                in = ifile.getInputStream();
                out = new FileOutputStream(tempFile);
                MailUtil.copyFile(in, out);
            }
            catch (Exception e)
            {
                logger.error(e);
            }
            finally
            {
                try{out.close();}catch (Exception e){}
                try{in.close();}catch (Exception e){}
            }
            cDoc.getAttribute("file").setValueiFile(new FSiFile(null,tempFile,null)); //$NON-NLS-1$
        }

        return cDoc;
   }

   private static int countActiveVersion(EboContext boctx, long templateBoui, long tempVersion) throws boRuntimeException
   {
        PreparedStatement pst = null;
        ResultSet rs = null;

        try
        {
            Connection con = boctx.getConnectionData();
            pst = con.prepareStatement("select count(*) from GESTEMP_Template t, gestemp_template$versoes tv, gestemp_tempversion v where t.boui = ? and v.boui <> ? and tv.parent$ = t.boui and tv.child$ = v.boui and v.activo = '1'"); //$NON-NLS-1$
            pst.setLong(1, templateBoui);
            pst.setLong(2, tempVersion);
            rs = pst.executeQuery();
            if(rs.next())
            {
                return rs.getInt(1);
            }
        }
        catch (Exception e)
        {
            logger.error("", e); //$NON-NLS-1$
        }
        finally
        {
            try{if(rs != null) {rs.close();}}catch(Exception e){}
            try{if(pst != null) {pst.close();}}catch(Exception e){}
        }
        return 0;
   }

   public static long getVersionParent(EboContext boctx, long templateVersion)
   {
        PreparedStatement pst = null;
        ResultSet rs = null;

        try
        {
            Connection con = boctx.getConnectionData();
            pst = con.prepareStatement("select t.boui from GESTEMP_Template t, gestemp_template$versoes tv, gestemp_tempversion v where v.modelo$ = ? and tv.parent$ = t.boui and tv.child$ = v.boui"); //$NON-NLS-1$
            pst.setLong(1, templateVersion);
            rs = pst.executeQuery();
            if(rs.next())
            {
                return rs.getInt(1);
            }
        }
        catch (Exception e)
        {
            logger.error("", e); //$NON-NLS-1$
        }
        finally
        {
            try{if(rs != null) {rs.close();}}catch(Exception e){}
            try{if(pst != null) {pst.close();}}catch(Exception e){}
        }
        return 0;
   }

   public static String getRostoDocumentName()
   {
        return "rosto" + getNumber() + ".doc"; //$NON-NLS-1$ //$NON-NLS-2$
   }

   public static String getDocumentName()
   {
        return "documento" + getNumber() + ".doc"; //$NON-NLS-1$ //$NON-NLS-2$
   }

   public static String getMailRostoDocumentName(boolean ext)
   {
        return ext ?
                "rosto" + getNumber() + ".pdf": //$NON-NLS-1$ //$NON-NLS-2$
                "rosto" + getNumber(); //$NON-NLS-1$

   }

   public static String getMailDocumentName(boolean ext)
   {
        return ext ?
                "documento" + getNumber() + ".pdf": //$NON-NLS-1$ //$NON-NLS-2$
                "documento" + getNumber(); //$NON-NLS-1$
   }

   public static String getDocumentTextName()
   {
        return "documento" + getNumber() + ".txt"; //$NON-NLS-1$ //$NON-NLS-2$
   }

   private synchronized static long getNumber()
   {
        long n = RANDOM.nextLong();
        return Math.abs(n);
   }

   public static boolean isGeneratedDoc(String docName)
   {
        return isLetterGeneratedDoc(docName) || isTXTGeneratedDoc(docName) || isMailGeneratedDoc(docName);
   }

   public static boolean isLetterGeneratedDoc(String docName)
   {
        if(docName != null && docName.lastIndexOf(".") > 0) //$NON-NLS-1$
        {
            if(docName.startsWith("documento") && docName.endsWith("doc"))  //$NON-NLS-1$ //$NON-NLS-2$
            {
                return true;
            }
            else if(docName.startsWith("rosto") && docName.endsWith("doc"))  //$NON-NLS-1$ //$NON-NLS-2$
            {
                return true;
            }
        }
        return false;
   }

   public static boolean isMailGeneratedDoc(String docName)
   {
        if(docName != null && docName.lastIndexOf(".") > 0) //$NON-NLS-1$
        {
            if(docName.startsWith("documento") && docName.endsWith("pdf"))  //$NON-NLS-1$ //$NON-NLS-2$
            {
                return true;
            }
            else if(docName.startsWith("rosto") && docName.endsWith("pdf"))  //$NON-NLS-1$ //$NON-NLS-2$
            {
                return true;
            }
        }
        return false;
   }

   public static boolean isTXTGeneratedDoc(String docName)
   {
        if(docName != null && docName.lastIndexOf(".") > 0) //$NON-NLS-1$
        {
            if(docName.startsWith("documento") && docName.endsWith("txt"))  //$NON-NLS-1$ //$NON-NLS-2$
            {
                return true;
            }
        }
        return false;
   }

   public static String getWordTag(String tagName)
   {
        return WORD_TAG + WORD_TAG + tagName.toLowerCase() + WORD_TAG + WORD_TAG;
   }

   public static boolean referencedByTemplate(GtQuery query, boObject obj,ArrayList fields, boolean bookmark) throws boRuntimeException
   {
        if(fields != null)
        {
            String aux = null;

            String nameToSearch = getNameToSearch(query == null ? null:query.getNome(), obj);
            for (int i = 0; i < fields.size(); i++)
            {
                if(!bookmark)
                    aux = (String)fields.get(i);
                else
                    aux = ((Bookmark)fields.get(i)).getBookmarkName();
                if(nameToSearch.endsWith("__")) //$NON-NLS-1$
                {
                    if(aux.startsWith(nameToSearch)) return true;
                    if(aux.equals(nameToSearch.substring(0, nameToSearch.length() - 2))) return true;
                }
                else if(nameToSearch.equals(aux))
                {
                    return true;
                }
            }
        }
        return false;
   }

   public static boolean referencedByTemplate(GtCampo campoBridge, boObject obj,ArrayList fields, boolean bookmark) throws boRuntimeException
   {
        return modeAutomatic(campoBridge, obj, fields, bookmark);
   }

   private static void clearDocs(boObject mailobject, ArrayList fwdDocs) throws boRuntimeException
   {
        bridgeHandler bh = mailobject.getBridge("documents"); //$NON-NLS-1$
        boBridgeIterator bit = bh.iterator();
        boObject docObj = null;
        String auxS;
        boolean found = false;
        String auxBoui;
        while(bit.next())
        {
            found = false;
            auxBoui = String.valueOf(bit.currentRow().getValueLong());
            if(fwdDocs != null && fwdDocs.indexOf(auxBoui) > -1)
            {
                found = true;
            }
            if(!found)
            {
                bh.moveTo( bit.getRow() );
                bh.remove();
                bit.previous();
            }
        }
   }
}