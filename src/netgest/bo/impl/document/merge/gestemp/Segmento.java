package netgest.bo.impl.document.merge.gestemp;

import netgest.bo.runtime.*;

public class Segmento 
{
    public Segmento()
    {
    }
    
    public static String getSegmento(boObject obj) throws boRuntimeException
    {
        if("GESDocClf".equals(obj.getName()))
        {
            return obj.getAttribute("segmento").getValueString();
        }
        throw new boRuntimeException("", "Objecto inválido para obtenção do segmento.", null);
    }
    
    public static String getSegmento(EboContext boctx, long boui) throws boRuntimeException
    {
        return getSegmento(boObject.getBoManager().loadObject(boctx, boui));
    }
    
}