package netgest.bo.impl.document.merge.gestemp.presentation;
import javax.servlet.http.HttpServletRequest;
import netgest.bo.impl.document.merge.gestemp.Segmento;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.bridgeHandler;

//TODO:Implement Interface LUSITANIA
// import pt.lusitania.gd.AutomaticClassifications;

public class GesDocObject extends GesDocObj
{
    private long objBoui = -1;
    
    public GesDocObject(GesDocViewer clfViewer, long gesDocboui, String internalName, String name, long boui, boolean required, String validation)
    {
        this.name = name;
        this.objBoui = boui;
        this.gesDocBoui = gesDocboui;
        this.required = required;
        this.validation = validation;
        this.clfViewer = clfViewer;
        this.internalName = internalName;
    }
    
    public long getObjBoui()
    {
        return this.objBoui;
    }
    
//    public void setClassification(GesDocViewer viewer, boObject document)  throws boRuntimeException
//    {
//        if(document != null && value != null && value.length() > 0)
//        {
//            boBridgeIterator bit = document.getBridge("classification").iterator();
//            bit.beforeFirst();
//            long auxL;
//            String auxS;
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
//                        auxS = bit.currentRow().getAttribute("valueObject").getValueString();
//                        if(!value.equalsIgnoreCase(auxS))
//                        {
//                            bit.currentRow().getAttribute("valueObject").setValueString(value);
//                        }
//                    }
//                }
//            }
//            if(!found)
//            {
//                bridgeHandler bh = document.getBridge("classification");
//                bh.add(getGesDocBoui());
//                bh.getAttribute("valueObject").setValueString(value);
//                bh.getAttribute("valueClassification").setValueLong(viewer.getClassBoui());
//            }
//        }
//    }
    
    public void setClassification(GesDocViewer viewer, boObject document)  throws boRuntimeException
    {
        if(document != null)
        {
            boBridgeIterator bit = document.getBridge("classification").iterator();
            bit.beforeFirst();
            String auxGS;
            long auxL;
            String auxS;
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
                            auxS = bit.currentRow().getAttribute("valueObject").getValueString();
                            if(!auxS.equalsIgnoreCase(value))
                            {
                                bit.currentRow().getAttribute("valueObject").setValueString(value);
                            }
                        }
                    }
                }
            }
            if(!found && !hasThisClassifs(viewer, document))
            {
                bridgeHandler bh = document.getBridge("classification");
                bh.add(getGesDocBoui());
                bh.getAttribute("valueObject").setValueString(value);
                bh.getAttribute("valueClassification").setValueLong(viewer.getClassBoui());
                bh.getAttribute("groupSeq").setValueString(viewer.getGroupSequence());
                String segmento = Segmento.getSegmento(document.getEboContext(), viewer.getClassBoui());
                bh.getAttribute("segmento").setValueString(segmento);

//TODO:Implement Interface LUSITANIA
//                if(value != null && !"".equals(value))
//                    AutomaticClassifications.setAutoClassif(boObject.getBoManager().loadObject(document.getEboContext(), Long.parseLong(value)), document, segmento);
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
            String auxS;
            while(bit.next())
            {
                auxL = bit.currentRow().getObject() != null ? bit.currentRow().getObject().getBoui():-1;
                if(auxL == getGesDocBoui())
                {
                    auxS = bit.currentRow().getAttribute("valueObject").getValueString();
                    if(value.equalsIgnoreCase(auxS))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
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
//                    value = String.valueOf(bit.currentRow().getAttribute("valueObject").getValueLong());
//                }
//            }
//        }
//    }

    public void setValue(EboContext boctx)  throws boRuntimeException
    {
        HttpServletRequest request = boctx.getRequest();
        if("-1".equals(request.getParameter(getHTMLFieldName())))
        {
           value=null;
        }
        else
        {
            value=request.getParameter(getHTMLFieldName());
        }
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
                    if(bit.currentRow().getAttribute("valueObject").getValueLong() > 0)
                    {
                        value = String.valueOf(bit.currentRow().getAttribute("valueObject").getValueLong());
                    }
                    else
                    {
                        value = null;
                    }
                }
            }
        }
    }
}