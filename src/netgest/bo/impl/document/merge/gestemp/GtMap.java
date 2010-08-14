package netgest.bo.impl.document.merge.gestemp;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import java.util.Comparator;
import java.util.Date;

import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

public class GtMap 
{
    private long tempAtrib = -1;
    private long clf = -1;
    private String helper = null;
    private long clfAtrib = -1;
    private String brAtt = null;
    private boolean transform = false;
    
    public GtMap(boObject map) throws boRuntimeException
    {
        boObject clfAtribObj = map.getAttribute("atribClf").getObject(); 
        tempAtrib = map.getAttribute("atributo").getValueLong();
        helper = map.getAttribute("helper").getValueString();
        clf = map.getAttribute("classificacao").getValueLong();
        clfAtrib  = clfAtribObj.getBoui();
        if("GESDocClfObject".equals(clfAtribObj.getName()))
        {
            brAtt = "valueObject";
                
        }
        else if("GESDocClfText".equals(clfAtribObj.getName()))
        {
            transform = true;
            brAtt = "valueText";
        }
        else if("GESDocClfDate".equals(clfAtribObj.getName()))
        {
            brAtt = "valueDate";
        }
        else if("GESDocClfLov".equals(clfAtribObj.getName()))
        {
            brAtt = "valueText";
        }
        else if("GESDocClfNumber".equals(clfAtribObj.getName()))
        {
            brAtt = "valueNumber";
        }
    }
    
    public long getTempAtrib()
    {
        return tempAtrib;
    }
    
    public long getClassif()
    {
        return clf;
    }
    
    public long getClassifAtribBoui()
    {
        return clfAtrib;
    }
    
    public String getHelper()
    {
        return helper;
    }
    
    public void setClassification(boObject document, Object value, String groupSeq, String segmento )  throws boRuntimeException
    {
        if(document != null && value != null)
        {//falta transformar para String qdo não é compatível e o atributo é texto
            if(!hasThisClassifs(document, value))
            {
                bridgeHandler bh = document.getBridge("classification");
                bh.add(clfAtrib);
                if(transform)
                {
                    bh.getAttribute(brAtt).setValueObject(transformToString(value));
                }
                else
                {
                    bh.getAttribute(brAtt).setValueObject(value);
                }
                bh.getAttribute("valueClassification").setValueLong(clf);
                bh.getAttribute("groupSeq").setValueString(groupSeq);
                //Segmento lógico
                bh.getAttribute("segmento").setValueString(Segmento.getSegmento(document.getEboContext(), clf));
            }
        }
    }
    
    private static String transformToString(Object value)
    {
        if(value != null)
        {
            if(value instanceof Date)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format((Date)value);
            }
            else 
            {
                return value.toString();
            }
        }
        return null;
    }
    
     public boolean hasThisClassifs(boObject document, Object value) throws boRuntimeException
    {
        if(document != null && value != null)
        {
            boBridgeIterator bit = document.getBridge("classification").iterator();
            bit.beforeFirst();
            long auxL = -1;
            while(bit.next())
            {
                auxL = bit.currentRow().getObject() != null ? bit.currentRow().getObject().getBoui():-1;
                if(auxL == clfAtrib)
                {
                    if(transform)
                    {
                        if(bit.currentRow().getAttribute(brAtt).getValueString().equalsIgnoreCase(transformToString(value)))
                        {
                            return true;
                        }
                    }
                    else
                    {
                        if(value instanceof Date)
                        {
                            Date auxD = bit.currentRow().getAttribute(brAtt).getValueDate();
                            if(((Date)value).equals(auxD) )
                            {
                                return true;
                            }
                        }
                        else if(value instanceof BigDecimal)
                        {
                            if("valueObject".equals(brAtt))
                            {
                                long l = bit.currentRow().getAttribute(brAtt).getValueLong();
                                if(l == ((BigDecimal)value).longValue())
                                {
                                    return true;
                                }
                            }
                            else
                            {
                                Object aux = bit.currentRow().getAttribute(brAtt).getValueObject();
                                if(aux != null)
                                {
                                    if(aux instanceof Double)
                                    {
                                        if(((Double)aux).doubleValue() == ((BigDecimal)value).doubleValue())
                                        {
                                            return true;
                                        }
                                    }
                                    else if(aux instanceof Long)
                                    {
                                        if(((Long)aux).longValue() == ((BigDecimal)value).longValue())
                                        {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        else if(value instanceof String)
                        {
                            String aux = bit.currentRow().getAttribute(brAtt).getValueString();
                            if(((String)value).equalsIgnoreCase(aux))
                            {
                                return true;
                            }
                        }
                        else
                        {
                            String aux1 = value.toString();
                            String aux = bit.currentRow().getAttribute(brAtt).getValueString();
                            if(aux1.equalsIgnoreCase(aux))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static class GtMapComparator implements Comparator
    {
        public final int compare(Object a, Object b)
        {
            if(((GtMap)a).getClassif() >((GtMap)b).getClassif())
            {
                return 1;
            }
            else if(((GtMap)a).getClassif() <((GtMap)b).getClassif())
            {
                return -1;
            }
            return 0;
        }
    }
}
