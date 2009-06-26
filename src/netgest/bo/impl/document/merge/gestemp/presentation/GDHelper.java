package netgest.bo.impl.document.merge.gestemp.presentation;
import netgest.bo.impl.document.merge.gestemp.GtCampo;
import netgest.bo.impl.document.merge.gestemp.GtCampoManual;
import netgest.bo.impl.document.merge.gestemp.GtParametro;
import netgest.bo.impl.document.merge.gestemp.GtQuery;
import netgest.bo.impl.document.merge.gestemp.GtTemplate;
import netgest.bo.impl.document.merge.gestemp.GtValue;
import netgest.bo.impl.document.merge.gestemp.Helper;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

public class GDHelper 
{
    private static final String PARAM_TEMPLATE_CODE = "modelo";
    private static final String PARAM_CHANNEL = "canal";
    
    public GDHelper()
    {
    }
    
    public static long getTemplateBoui(boObject owfActvTemplate) throws boRuntimeException
    {
        boBridgeIterator bit = owfActvTemplate.getBridge("tempParams").iterator();
        bit.beforeFirst();
        boObject aux;
        String tempCode;
        while(bit.next())
        {
            aux = bit.currentRow().getObject();
            if(PARAM_TEMPLATE_CODE.equals(aux.getAttribute("id").getValueString()))
            {
                tempCode = aux.getAttribute("value").getValueString();
                if(tempCode != null && !"".equals(tempCode))
                {
                    return Helper.getTemplateBouiByCode(owfActvTemplate.getEboContext(), tempCode);
                }
            }
        }
        return -1;
    }
    
    public static void fillTemplate(boObject owfActvTemplate, GtTemplate template) throws boRuntimeException
    {
        GtCampo manualList[] = template.getCamposManuais();
        GtCampo auxManual = null;
        GtQuery queriesList[] = template.getQueries();
        GtParametro auxParam = null;
        String value = null;
        
        for (int i = 0; i < manualList.length; i++) 
        {
            auxManual = manualList[i];
            value = getParamValue(owfActvTemplate, auxManual.getHTMLFieldName());
            if(value != null)
            {
                if("telefone".equalsIgnoreCase(auxManual.getNome()) || "fax".equalsIgnoreCase(auxManual.getNome()))
                {
                    value = ((GtCampoManual)auxManual).redifineValue(value);
                }
                if(Integer.parseInt(auxManual.getTipo()) == 4)
                {
                    if(value != null && !"".equals(value))
                    {
                        value = value.replaceAll("////", "-");
                    }
                }
                manualList[i].setValueString(value);
            }
        }
        
        for (int i = 0; i < queriesList.length; i++) 
        {
            auxParam = queriesList[i].getParametro();
            value = getParamValue(owfActvTemplate, auxParam.getHTMLFieldName());
            if(value != null)
            {
                GtValue v = new GtValue();
                v.addValue(boObject.getBoManager().loadObject(owfActvTemplate.getEboContext(), Long.parseLong(value)));
                auxParam.setValue(v);
            }
        }
    }
    
    
    public static boolean fillChannel(boObject owfActvTemplate, GtTemplate template) throws boRuntimeException
    {
        String value = getChannel(owfActvTemplate);
        if(value != null && !"".equals(value))
        {
            boolean carta = "carta".equals(value);
            boolean fax = "fax".equals(value);
            boolean email = "email".equals(value);
            boolean sms = "sms".equals(value);
            template.setTypeValues(owfActvTemplate.getEboContext(), carta, fax, email, sms);
            return true;
        }
        return false;
    }
    
    public static String getParamValue(boObject owfActvTemplate, String parameter)throws boRuntimeException
    {
        boBridgeIterator bit = owfActvTemplate.getBridge("tempParams").iterator();
        bit.beforeFirst();
        boObject aux;
        String tempCode;
        while(bit.next())
        {
            aux = bit.currentRow().getObject();
            if(parameter.equals(aux.getAttribute("id").getValueString()))
            {
                return aux.getAttribute("value").getValueString();
            }
        }
        return null;
    }
    
    public static String getChannel(boObject owfActvTemplate)throws boRuntimeException
    {
        return getParamValue(owfActvTemplate, PARAM_CHANNEL);
    }
}