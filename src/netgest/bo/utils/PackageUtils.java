/*Enconding=UTF-8*/
package netgest.bo.utils;
import java.util.ArrayList;
import netgest.bo.runtime.*;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class PackageUtils 
{
    public static ArrayList getPackagesFromApplication(boObject eboApplication) throws boRuntimeException
    {
        ArrayList toRet = new ArrayList();
        boBridgeIterator pckit = eboApplication.getBridge("packages").iterator();
        pckit.beforeFirst();
        while(pckit.next())
        {
            toRet.add(pckit.currentRow().getObject());
        }
        return toRet;
    }
    
    public static long[] getPackagesBouisFromApplication(boObject eboApplication) throws boRuntimeException
    {
        boBridgeIterator pckit = eboApplication.getBridge("packages").iterator();
        int numberPackages=(int)eboApplication.getBridge("packages").getRecordCount();
        
        long packages[] = new long[numberPackages];
        pckit.beforeFirst();
        int i = 0;
        while(pckit.next())
        {
            packages[i++] = pckit.currentRow().getObject().getBoui();
        }
        return packages;
    }
    
    public static ArrayList getPackagesNamesFromXEOUser(boObject xeoUser) throws boRuntimeException
    {
        boBridgeIterator pckit = xeoUser.getBridge("packages").iterator();
        pckit.beforeFirst();
        boObject aux = null;
        ArrayList auxArr = null;
        ArrayList toRet = new ArrayList();
        while(pckit.next())
        {
            aux = pckit.currentRow().getObject();
            if("Ebo_Package".equalsIgnoreCase(aux.getName()))
            {
                toRet.add(aux.getAttribute("name").getValueString());
            }
            else//então é Ebo_Application
            {
                auxArr = getPackagesFromApplication(aux);
                if(auxArr != null && auxArr.size() > 0)
                {
                    for (int j=0;j<auxArr.size();j++) 
                    {
                        toRet.add(new Long(((boObject)auxArr.get(j)).getAttribute("name").getValueString()));
                    }
                }
            }
        }
        return toRet;
    }
    
    public static long[] getPackagesBouiFromBridgePackages(boObject xeoUser) throws boRuntimeException
    {
        long toRet[] = null; 
        boBridgeIterator pckit = xeoUser.getBridge("packages").iterator();
        pckit.beforeFirst();
        boObject aux = null;
        ArrayList auxArr = null;
        ArrayList pckBouis = new ArrayList();
        while(pckit.next())
        {
            aux = pckit.currentRow().getObject();
            if("Ebo_Package".equalsIgnoreCase(aux.getName()))
            {
                pckBouis.add(new Long(aux.getBoui()));
            }
            else//então é Ebo_Application
            {
                auxArr = getPackagesFromApplication(aux);
                if(auxArr != null && auxArr.size() > 0)
                {
                    for (int j=0;j<auxArr.size();j++) 
                    {
                        pckBouis.add(new Long(((boObject)auxArr.get(j)).getBoui()));
                    }
                }
            }
        }
        int numberpackages= (int) pckBouis.size();
        toRet=new long[ numberpackages ];
        for (int j = 0; j < pckBouis.size(); j++) 
        {
            toRet[ j ] = ((Long)pckBouis.get(j)).longValue();
        }
        return toRet;
    }

}