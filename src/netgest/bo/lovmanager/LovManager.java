/*Enconding=UTF-8*/
package netgest.bo.lovmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.boApplication;


public class LovManager
{
    private static Hashtable p_lovs = new Hashtable();

    public LovManager()
    {
    }
    
    
    public static lovObject getLovObject(EboContext ctx, long lovBoui)
        throws boRuntimeException
    {
        return getLovObject(ctx, lovBoui, null, null);
    }

    public static lovObject getLovObject(EboContext ctx, long lovBoui, String[] onlyThisvalues)
        throws boRuntimeException
    {
        return getLovObject(ctx, lovBoui, onlyThisvalues, null);
    }
    
    public static lovObject getLovObject(EboContext ctx, long lovBoui, String[] onlyThisvalues, Hashtable parameters)
        throws boRuntimeException
    {
        String language="";
        String extrakey="";
        if ((onlyThisvalues != null) && (onlyThisvalues.length == 0))
        {
            onlyThisvalues = null;
        }
        if ( onlyThisvalues!= null)
        {
            for (int i = 0; i < onlyThisvalues.length ; i++) 
            {
                extrakey= extrakey+onlyThisvalues[i];
            }
            
        }

        lovObject toRet = ( lovObject ) p_lovs.get(lovBoui+extrakey );
        if (toRet == null || (parameters != null && parameters.size() > 0))
        {
            boObject lov;
            lov = boObject.getBoManager().loadObject(ctx, "Ebo_LOV", "boui=" + lovBoui + "");
          ///////
            if(lov.getAttribute("lang").getValueString()!=null){
                
            	language=lov.getAttribute("lang").getValueString();
            }
            if (lov.exists())
            {
                String name          = lov.getAttribute("name").getValueString();
                String tableName     = lov.getAttribute("fromTable").getValueString();
                String sqlQuery      = lov.getAttribute("sqlQuery").getValueString();
                String whereClause   = lov.getAttribute("whereClause").getValueString();
                String fieldForLabel = lov.getAttribute("fieldForLabel").getValueString();
                String fieldForCod   = lov.getAttribute("fieldForCode").getValueString();
                lovObject lovo;
                
                if( sqlQuery.length() > 0 )
                {
                    lovo = new lovObject(
                            ctx, lovBoui, name, sqlQuery, fieldForLabel, fieldForCod, parameters, onlyThisvalues
                        );
                    
                }
                else if ((tableName != null) && (tableName.length() > 0))
                {
                    lovo = new lovObject(
                            ctx, lovBoui, name, tableName, whereClause, fieldForLabel, fieldForCod, onlyThisvalues
                        );
                }
                else
                {
                    lovo = new lovObject(ctx, name, onlyThisvalues);
                }
                if (language!="")
                    lovo.setLanguage(language);
                    else
                    	lovo.setLanguage(boApplication.currentContext().getEboContext().getApplication().getApplicationLanguage());
                
                
                p_lovs.put(lovBoui+extrakey, lovo);
                toRet=lovo;
            }
        }

        return toRet;
    }
    
    // psantos
        public static lovObject getLovObject(EboContext ctx, String name, String[] onlyThisvalues, Hashtable parameters)
        throws boRuntimeException
    {
        String language="";
        String extrakey="";
        if ((onlyThisvalues != null) && (onlyThisvalues.length == 0))
        {
            onlyThisvalues = null;
        }
        if ( onlyThisvalues!= null)
        {
            for (int i = 0; i < onlyThisvalues.length ; i++) 
            {
                extrakey= extrakey+onlyThisvalues[i];
            }
            
        }

        lovObject toRet = ( lovObject ) p_lovs.get(name+extrakey );
        if (toRet == null || (parameters != null && parameters.size() > 0))
        {
            boObject lov;
            lov = boObject.getBoManager().loadObject(ctx, "Ebo_LOV", "name='" + name + "'");
            ///////
            if(lov.getAttribute("lang").getValueString()!=null){
                
            	language=lov.getAttribute("lang").getValueString();
            }
            
            if (lov.exists())
            {
                String tableName     = lov.getAttribute("fromTable").getValueString();
                String sqlQuery      = lov.getAttribute("sqlQuery").getValueString();
                String whereClause   = lov.getAttribute("whereClause").getValueString();
                String fieldForLabel = lov.getAttribute("fieldForLabel").getValueString();
                String fieldForCod   = lov.getAttribute("fieldForCode").getValueString();
                long lovBoui = lov.getBoui();
                lovObject lovo;
                
                if( sqlQuery.length() > 0 )
                {
                    lovo = new lovObject(
                            ctx, lovBoui, name, sqlQuery, fieldForLabel, fieldForCod, parameters, onlyThisvalues
                        );
                    
                }
                else if ((tableName != null) && (tableName.length() > 0))
                {
                    lovo = new lovObject(
                            ctx, lovBoui, name, tableName, whereClause, fieldForLabel, fieldForCod, onlyThisvalues
                        );
                }
                else
                {
                    lovo = new lovObject(ctx, name, onlyThisvalues);
                }
                if (language!="")
                    lovo.setLanguage(language);
                    else
                    	lovo.setLanguage(boApplication.currentContext().getEboContext().getApplication().getApplicationLanguage());
                
                p_lovs.put(name+extrakey, lovo);
                toRet=lovo;
            }
        }

        return toRet;
    }
    // psantos fim

    public static lovObject getLovObject(EboContext ctx, String name, String[] onlyThisvalues)
        throws boRuntimeException
    {
    	String language="";
        String extrakey="";
        if ((onlyThisvalues != null) && (onlyThisvalues.length == 0))
        {
            onlyThisvalues = null;
        }
        if ( onlyThisvalues!= null)
        {
            for (int i = 0; i < onlyThisvalues.length ; i++) 
            {
                extrakey= extrakey+onlyThisvalues[i];
            }
            
        }
        lovObject toRet = ( lovObject ) p_lovs.get(name+extrakey );
        if (toRet == null)
        {
            boObject lov;
            lov = boObject.getBoManager().loadObject(ctx, "Ebo_LOV", "name='" + name + "'");
           ///////
            if(lov.getAttribute("lang").getValueString()!=null){
            
            	language=lov.getAttribute("lang").getValueString();
            }
            if (lov.exists())
            {            
                String tableName     = lov.getAttribute("fromTable").getValueString();
                String sqlQuery      = lov.getAttribute("sqlQuery").getValueString();
                String whereClause   = lov.getAttribute("whereClause").getValueString();
                String fieldForLabel = lov.getAttribute("fieldForLabel").getValueString();
                String fieldForCod   = lov.getAttribute("fieldForCode").getValueString();
                long lovBoui         = lov.getBoui();
                lovObject lovo;
                
                if( sqlQuery.length() > 0 )
                {
                    lovo = new lovObject(
                            ctx, lovBoui, name, sqlQuery, fieldForLabel, fieldForCod, onlyThisvalues
                        );
                   
                }
                else if ((tableName != null) && (tableName.length() > 0))
                {
                    lovo = new lovObject(
                            ctx, lovBoui, name, tableName, whereClause, fieldForLabel, fieldForCod, onlyThisvalues
                        );
                }
                else
                {
                    lovo = new lovObject(ctx, name, onlyThisvalues);
                }

                if (language!="")
                    lovo.setLanguage(language);
                    else
                    	lovo.setLanguage(boApplication.currentContext().getEboContext().getApplication().getApplicationLanguage());
                
                p_lovs.put(name+extrakey, lovo);
                toRet=lovo;
            }
        }

        return toRet;
    }
    
    // psantos ini
    public static lovObject getLovObject(EboContext ctx, String name, Hashtable parameters)
        throws boRuntimeException
    {
        return getLovObject(ctx, name, new String[0], parameters );
    }
    // psantos fim

    public static lovObject getLovObject(EboContext ctx, String name)
        throws boRuntimeException
    {
        return getLovObject(ctx, name, new String[0]);
    }

    public static void removeLovObject(String name)
    {
        p_lovs.remove(name);
    }
    
    public static void orderByValue(boObject lov) throws boRuntimeException
    {
        orderBy(lov, "value");
    }
    
    public static void orderByDescription(boObject lov) throws boRuntimeException
    {
        orderBy(lov, "description");
    }
    
    public static void orderBy(boObject lov, String fieldName) throws boRuntimeException
    {
        bridgeHandler bh = lov.getBridge("details");
        boBridgeIterator bit = bh.iterator();
        ArrayList r = new ArrayList();
        while(bit.next())
        {
//            r.add(bit.currentRow().getObject().getAttribute(fieldName).getValueString());
//          Using lower case, to make sure it gets ordered as the user wants and not from a Mathematical point of view.
            r.add(bit.currentRow().getObject().getAttribute(fieldName).getValueString().toLowerCase());
        }
        if(r.size() > 1)
        {
            String[] orderList = (String[])r.toArray(new String[r.size()]);
            Arrays.sort(orderList);
            String aux, aux2;
            boolean found = false;
            for (int i = 0; i < orderList.length; i++) 
            {
                aux = orderList[i];
                bh.beforeFirst();
                while(bh.next())
                {
                    found = false;
//                    aux2 = bh.getObject().getAttribute(fieldName).getValueString();
                    aux2 = bh.getObject().getAttribute(fieldName).getValueString().toLowerCase();
                    if(aux.equals(aux2) && !found)
                    {
                        found = true;
                        bh.moveRowTo(i+1);
                    }
                }
            }
        }
    }
}
