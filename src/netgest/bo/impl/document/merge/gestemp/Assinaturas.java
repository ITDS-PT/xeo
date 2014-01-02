package netgest.bo.impl.document.merge.gestemp;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.io.iFile;
public class Assinaturas 
{
    public Assinaturas()
    {
    }
    
    public static byte[] getAssinatura(EboContext boctx, long perfBoui) throws boRuntimeException
    {
        boObject perf = boObject.getBoManager().loadObject(boctx, perfBoui);
        String username = perf.getAttribute("username").getValueString();
        return getAssinatura(boctx, username);
    }
    
    public static byte[] getAssinatura(boObject perf) throws boRuntimeException
    {
        String username = perf.getAttribute("username").getValueString();
        return getAssinatura(perf.getEboContext(), username);
    }
    
    public static byte[] getAssinatura(EboContext boctx, String username)
    {
        byte[] toRet = null;
        boObject aux = null;
        InputStream is = null;
        try
        {
           boObjectList list = boObjectList.list(boctx, "select dmUser where username = ?", new Object[]{username},1,1,"");
           list.beforeFirst();
           if (list.next())
           {
                aux = list.getObject();
                if (aux.getAttribute("assinatura_bmp").getValueObject() != null && !"".equals(aux.getAttribute("assinatura_bmp").getValueObject()))
                {
                    iFile file = list.getObject().getAttribute("assinatura_bmp").getValueiFile();
                    if(file != null)
                    {
                        is = file.getInputStream();
                        toRet = new byte[ (int)file.length()];
                        is.read(toRet);
                    }
                }
           }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally{
            try{if(is != null) is.close();}catch(Exception e){/*IGNORE*/}
        }
        return toRet;
    }
    
    public static boolean hasAssinatura(EboContext boctx, long perfBoui) throws boRuntimeException
    {
        boObject perf = boObject.getBoManager().loadObject(boctx, perfBoui);
        String username = perf.getAttribute("username").getValueString();
        return hasAssinatura(boctx, username);
    }
    
    public static boolean hasAssinatura(boObject perf) throws boRuntimeException
    {
        String username = perf.getAttribute("username").getValueString();
        return hasAssinatura(perf.getEboContext(), username);
    }
    
    public static boolean hasAssinatura(EboContext boctx, String username)
    {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean toRet = false;
        try
        {
           con = boctx.getConnectionData();
           pst = con.prepareStatement("select 1 from odmuser where username = ? and assinatura_bmp is not null");
           pst.setString(1, username);
           rs = pst.executeQuery();
           if(rs.next())
           {
                toRet = true;
           }           
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally{
            try{if(rs != null) rs.close();}catch(Exception e){/*IGNORE*/}
            try{if(pst != null) pst.close();}catch(Exception e){/*IGNORE*/}
            try{if(con != null) con.close();}catch(Exception e){/*IGNORE*/}
        }
        return toRet;
    }
}