package netgest.bo.impl.document.merge.gestemp;

import netgest.bo.runtime.*;

public class Modulo 
{
    //MODULOS XEO -> APPX
    private static final String GD_REPOSITORY = "10";
    private static final String GD_TEST_REPOSITORY = "10";
    
    public static String getModulo() throws boRuntimeException
    {
        return GD_REPOSITORY;
    }
    
    public static String getTestModulo() throws boRuntimeException
    {
        return GD_TEST_REPOSITORY;
    }
    
    
    public static String getApplicationName(String segmento)
    {
        String toRet = segmento;
        if("1".equals(segmento))
        {
            toRet = "SINISTRO";
        }
        else if("2".equals(segmento))
        {
            toRet = "PRODUCAO";
        }
        else if("3".equals(segmento))
        {
            toRet = "RECLAMA";
        }
        else if("4".equals(segmento))
        {
            toRet = "F_FERIAS";
        }
        else if("7".equals(segmento))
        {
            toRet = "OUTROS";
        }
        else if("10".equals(segmento))
        {
            toRet = "GD_REPOSITORY";
        }
        return toRet;
    
    }
    
    public static boolean hasModulo(boObject doc)
    {
        try
        {
            return doc.getAttribute("segmento").getValueString() != null && 
                   !"".equals(doc.getAttribute("segmento").getValueString());
        }
        catch (boRuntimeException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean importToAppx(boObject doc)
    {
        try
        {
            return GD_REPOSITORY.equals(doc.getAttribute("segmento").getValueString());
        }
        catch (boRuntimeException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}