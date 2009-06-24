package netgest.bo.impl.document.merge.gestemp.presentation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import netgest.bo.impl.document.merge.gestemp.Segmento;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

public class GesDocDate extends GesDocObj
{
    private String dtFormat = null; 
    
    public GesDocDate(GesDocViewer clfViewer, long gesDocboui, String internalName, String name, String dtFormat, boolean required, String validation)
    {
        this.name = name;
        this.dtFormat = dtFormat;
        this.gesDocBoui = gesDocboui;
        this.required = required;
        this.validation = validation;
        this.clfViewer = clfViewer;
        this.internalName = internalName;
    }

    public String getDateFormate()
    {
        return this.dtFormat;
    }
    
//    public void setClassification(GesDocViewer viewer, boObject document)  throws boRuntimeException
//    {
//        if(document != null && value != null && value.length() > 0)
//        {
//            boBridgeIterator bit = document.getBridge("classification").iterator();
//            bit.beforeFirst();
//            long auxL;
//            Date auxD;
//            Date valueDate = getValueDate(dtFormat, value);
//            boolean found = false;
//            while(bit.next() && !found)
//            {
//                auxL = bit.currentRow().getAttribute("valueClassification").getValueLong();
//                if(auxL == viewer.getClassBoui())
//                {
//                    auxL = bit.currentRow().getObject() != null ? bit.currentRow().getObject().getBoui():-1;
//                    if(auxL == getGesDocBoui())
//                    {
//                        found = true;
//                        auxD = bit.currentRow().getAttribute("valueDate").getValueDate();
//                        if(!valueDate.equals(auxD) )
//                        {
//                            bit.currentRow().getAttribute("valueDate").setValueDate(valueDate);
//                        }
//                    }
//                }
//            }
//            if(!found)
//            {
//                bridgeHandler bh = document.getBridge("classification");
//                bh.add(getGesDocBoui());
//                bh.getAttribute("valueDate").setValueDate(valueDate);
//                bh.getAttribute("valueClassification").setValueLong(viewer.getClassBoui());
//            }
//        }
//    }
    
    public void setClassification(GesDocViewer viewer, boObject document)  throws boRuntimeException
    {
        if(document != null && value != null && value.length() > 0)
        {
            boBridgeIterator bit = document.getBridge("classification").iterator();
            bit.beforeFirst();
            String auxGS;
            long auxL;
            Date auxD;
            Date valueDate = getValueDate(dtFormat, value);
            boolean found = false;
            if(viewer.isEditing())
            {
                while(bit.next() && !found)
                {
                    auxGS = bit.currentRow().getAttribute("groupSeq").getValueString();
                    if(auxGS.equals(viewer.getGroupSequence()))
                    {
                        auxL = bit.currentRow().getObject() != null ? bit.currentRow().getObject().getBoui():-1;
                        if(auxL == getGesDocBoui())
                        {
                            found = true;
                            auxD = bit.currentRow().getAttribute("valueDate").getValueDate();
                            if(!valueDate.equals(auxD) )
                            {
                                bit.currentRow().getAttribute("valueDate").setValueDate(valueDate);
                            }
                        }
                    }
                }
            }
            if(!found && !hasThisClassifs(viewer, document))
            {
                bridgeHandler bh = document.getBridge("classification");
                bh.add(getGesDocBoui());
                bh.getAttribute("valueDate").setValueDate(valueDate);
                bh.getAttribute("valueClassification").setValueLong(viewer.getClassBoui());
                bh.getAttribute("groupSeq").setValueString(viewer.getGroupSequence());
                bh.getAttribute("segmento").setValueString(Segmento.getSegmento(document.getEboContext(), viewer.getClassBoui()));
            }
        }
    }
    
    public boolean hasThisClassifs(GesDocViewer viewer, boObject document) throws boRuntimeException
    {
        if(document != null && value != null && value.length() > 0)
        {
            boBridgeIterator bit = document.getBridge("classification").iterator();
            bit.beforeFirst();
            long auxL;
            Date auxD;
            Date valueDate = getValueDate(dtFormat, value);
            while(bit.next())
            {
                auxL = bit.currentRow().getObject() != null ? bit.currentRow().getObject().getBoui():-1;
                if(auxL == getGesDocBoui())
                {
                    auxD = bit.currentRow().getAttribute("valueDate").getValueDate();
                    if(valueDate.equals(auxD) )
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static Date getValueDate(String dtFormat, String value)
    {
        Date d = null;
        try
        {
            SimpleDateFormat sdf = null;
            if("1".equals(dtFormat))
            {
                sdf = new SimpleDateFormat("dd/MM/yyyy");
            }
            else
            {
                sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            }
            d = sdf.parse(value);
            
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return d;
    }
    
    private static String getValueString(String dtFormat, Date d)
    {
        String toRet = null;
        try
        {
            SimpleDateFormat sdf = null;
            if("1".equals(dtFormat))
            {
                sdf = new SimpleDateFormat("dd/MM/yyyy");
            }
            else
            {
                sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss");
            }
            toRet = sdf.format(d);
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return toRet;
    }
    
//    public void setValue(EboContext boctx, boBridgeIterator bit, long classif)  throws boRuntimeException
//    {
//        bit.beforeFirst();
//        while(bit.next())
//        {
//            if(bit.currentRow().getAttribute("valueClassification").getValueLong() == classif)
//            {
//                if(bit.currentRow().getValueLong() == gesDocBoui)
//                {
//                    value = getValueString(dtFormat,bit.currentRow().getAttribute("valueDate").getValueDate());
//                }
//            }
//        }
//    }
    
    public void setValue(EboContext boctx, boBridgeIterator bit, String groupSeq)  throws boRuntimeException
    {
        bit.beforeFirst();
        while(bit.next())
        {
            if(groupSeq.equals(bit.currentRow().getAttribute("groupSeq").getValueString()))
            {
                if(bit.currentRow().getValueLong() == gesDocBoui)
                {
                    value = getValueString(dtFormat,bit.currentRow().getAttribute("valueDate").getValueDate());
                }
            }
        }
    }
    
    
    public void setValue(EboContext boctx)  throws boRuntimeException
    {
        HttpServletRequest request = boctx.getRequest();
        value = request.getParameter(getHTMLFieldName());
        if("2".equals(dtFormat))
        {
            String time=request.getParameter("_ignore_" + getHTMLFieldName());
            if(time != null && !"".equals(time))
            {
                value += " " + time;
            }
        }
    }
}