package netgest.bo.impl.document.merge.gestemp.presentation;
import java.math.BigDecimal;
import java.text.NumberFormat;
import netgest.bo.impl.document.merge.gestemp.Segmento;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

public class GesDocNumber extends GesDocObj
{
    private boolean currency; 
    private long decimals = -1; 
    
    public GesDocNumber(GesDocViewer clfViewer, long gesDocboui, String internalName, String name, boolean currency, long decimals, boolean required, String validation)
    {
        this.name = name;
        this.currency = currency;
        this.decimals = decimals;
        this.gesDocBoui = gesDocboui;
        this.required = required;
        this.validation = validation;
        this.clfViewer = clfViewer;
        this.internalName = internalName;
    }
    public boolean isCurrency()
    {
        return this.currency;
    }
    
    public long getDecimals()
    {
        return this.decimals;
    }

    public void setValue(EboContext boctx, boBridgeIterator bit, String groupSeq)  throws boRuntimeException
    {
        bit.beforeFirst();
        while(bit.next())
        {
            if(groupSeq.equals(bit.currentRow().getAttribute("groupSeq").getValueString()))
            {
                if(bit.currentRow().getValueLong() == gesDocBoui)
                {
                    value = bit.currentRow().getAttribute("valueNumber").getValueString();
                }
            }
        }
    }
    
//    public void setClassification(GesDocViewer viewer, boObject document)  throws boRuntimeException
//    {
//        if(document != null && value != null && value.length() > 0)
//        {
//            boBridgeIterator bit = document.getBridge("classification").iterator();
//            bit.beforeFirst();
//            double auxD;
//            double valueDouble = getValueDouble();
//            long auxL;
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
//                        auxD = bit.currentRow().getAttribute("valueNumber").getValueDouble();
//                        if(auxD != valueDouble)
//                        {
//                           bit.currentRow().getAttribute("valueNumber").setValueDouble(valueDouble);
//                        }
//                    }
//                }
//            }
//            if(!found)
//            {
//                bridgeHandler bh = document.getBridge("classification");
//                bh.add(getGesDocBoui());
//                bh.getAttribute("valueNumber").setValueDouble(valueDouble);
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
            double auxD;
            double valueDouble = getValueDouble();
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
                            auxD = bit.currentRow().getAttribute("valueNumber").getValueDouble();
                            if(auxD != valueDouble)
                            {
                               bit.currentRow().getAttribute("valueNumber").setValueDouble(valueDouble);
                            }
                        }
                    }
                }
            }
            if(!found && !hasThisClassifs(viewer, document))
            {
                bridgeHandler bh = document.getBridge("classification");
                bh.add(getGesDocBoui());
                bh.getAttribute("valueNumber").setValueDouble(valueDouble);
                bh.getAttribute("valueClassification").setValueLong(viewer.getClassBoui());
                bh.getAttribute("groupSeq").setValueString(viewer.getGroupSequence());
                bh.getAttribute("segmento").setValueString(Segmento.getSegmento(document.getEboContext(),viewer.getClassBoui()));
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
            double auxD;
            double valueDouble = getValueDouble();
            while(bit.next())
            {
                auxL = bit.currentRow().getObject() != null ? bit.currentRow().getObject().getBoui():-1;
                if(auxL == getGesDocBoui())
                {
                    auxD = bit.currentRow().getAttribute("valueNumber").getValueDouble();
                    if(auxD == valueDouble)
                    {
                       return true;
                    }
                }
            }
        }
        return false;
    }
    
    private double getValueDouble()
    {
        double d = 0;

        try
        {
            if(value != null)
            {
                value = value.replaceAll("\\.", "");
                value = value.replaceAll(",", ".");
            }
            BigDecimal bd = new BigDecimal(value);
            d = bd.doubleValue();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return d;
    }
}