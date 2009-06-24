/*Enconding=UTF-8*/
package netgest.bo.utils;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;

import netgest.bo.builder.boBuilder;
import netgest.bo.data.oracle.OracleDBM;


import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.boRepository;
import netgest.bo.system.boSession;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import org.apache.log4j.Logger;
import java.util.*;
import netgest.bo.*;
import netgest.bo.utils.*;
import netgest.utils.ngtXMLHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class IProfileUtils
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.utils.IProfileUtils");
    //default
    public static final String DEFAULT = "Default";
    public static final String DEFAULT_COMBO = "0;Default";

    /**
     *
     * @Company Enlace3
     * @since
     */
    public IProfileUtils()
    {
    }

    public static final String[] getIProfiles(boSession session)
    {
        String[] toRet = null;
        ArrayList r = new ArrayList();
        EboContext eboctx = null;

        try
        {
            long boui = session.getUser().getBoui();

            if (boui == 0)
            {
                return allIProfiles(session);
            }

            boolean existDefault = false;
            String aux;
            eboctx = session.createRequestContext(null, null, null);

            boObject user = boObject.getBoManager().loadObject(eboctx, boui);
            bridgeHandler bh = user.getBridge("iProfile");

            if (bh != null)
            {
                bh.beforeFirst();

                while (bh.next())
                {
                    aux = bh.getObject().getBoui() + ";" + bh.getObject().getAttribute("name").getValueString();
                    if (!r.contains(aux))
                    {
                        r.add(aux);
                    }
                }
                toRet = (String[])r.toArray(new String[r.size()]);

                //ordenar
                Arrays.sort(toRet);
            }
        }
        catch (boRuntimeException e)
        {
        }
        finally
        {
            if ( eboctx!= null) eboctx.close();
        }
        
        if(toRet == null || toRet.length == 0)
        {
            toRet = new String[]{DEFAULT_COMBO};
        }

        return toRet;
    }

    private static final String[] allIProfiles(boSession session)
    {
        String[] toRet = null;
        ArrayList r = new ArrayList();
        EboContext eboctx = null;

        try
        {
            eboctx = session.createRequestContext(null, null, null);
            boObjectList listIProfiles = boObjectList.list(eboctx, "select uiProfile where 1=1");
            listIProfiles.beforeFirst();
            while(listIProfiles.next())
            {
                r.add(listIProfiles.getObject().getAttribute("name").getValueString());
            }
            toRet = (String[])r.toArray(new String[r.size()]);
        }
        catch (boRuntimeException e)
        {
            
        }
        if(toRet == null || toRet.length == 0)
        {
            toRet = new String[]{DEFAULT_COMBO};
        }
        return toRet;
    }

    public static final boolean existSchema(EboContext ctx, String schemaName)
        throws boRuntimeException
    {
        boObject repository = boObject.getBoManager().loadObject(ctx,
                "Ebo_Repository", "SCHEMANAME='" + schemaName + "'");

        if ((repository != null) &&
                schemaName.equalsIgnoreCase(repository.getAttribute("name")
                                                          .getValueString()))
        {
            return true;
        }

        return false;
    }

    private static boolean existIn(String objName, String[] objsType)
    {
        for (int i = 0; i < objsType.length; i++)
        {
            if (objsType[i].equals(objName))
            {
                return true;
            }
        }

        return false;
    }
    
//    public static Hashtable getProfiles(EboContext ctx,WorkPlace wPlace) throws boRuntimeException
//    {
//        boObject profile = null;
//        String profileName = null;
//        ArrayList wProfiles = wPlace.getProfiles();
//        Hashtable profiles = new Hashtable(wProfiles.size());        
//        for (int i = 0; i < wProfiles.size(); i++) 
//        {
//            profileName = (String)wProfiles.get(i);
//            profile = getProfileByName(ctx,profileName);
//            if(profile != null)
//            {
//                profiles.put(profileName,String.valueOf(profile.getBoui()));
//            }
//        }
//        return profiles;
//    }
    public static boObject getProfileByName(EboContext ctx, String name) throws boRuntimeException
    {
        boObject profile = null;
        boObjectList profileList = boObjectList.list(ctx,"SELECT uiProfile WHERE name='"+ name +"'");
        profileList.beforeFirst();
        if(profileList.next())
        {            
            profile = profileList.getObject();
        }
        return profile;
    }
    
    
   
    
  
  
}
