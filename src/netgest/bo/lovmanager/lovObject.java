/*Enconding=UTF-8*/
package netgest.bo.lovmanager;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import com.ibm.regex.*;

import java.util.Hashtable;



public class lovObject
{
    private ArrayList p_lov_cod         = new ArrayList();
    private ArrayList p_lov_description = new ArrayList();
    private int p_pointer               = -1;
    private int p_count                 = 0;
    private long p_lovboui = -1;

    public lovObject(EboContext ctx, String name, String[] onlyThisValues)
        throws boRuntimeException
    {
        boObject lov;
        lov = boObject.getBoManager().loadObject(ctx, "Ebo_LOV", "name='" + name + "'");
        
        if (lov.exists())
        {
            p_lovboui = lov.getBoui();
            bridgeHandler lovdetails = lov.getBridge("details");

            lovdetails.beforeFirst();

            // Ebo_LOVDetails det;
            boObject det;

            if (lovdetails.getRowCount() > 0)
            {
                String v = null;

                while (lovdetails.next())
                {
                    det = lovdetails.getObject();

                    String xcod   = det.getAttribute("value").getValueString();
                    String xlabel = det.getAttribute("description").getValueString();
                    boolean toAdd = true;

                    if (onlyThisValues != null)
                    {
                        toAdd = false;

                        for (int i = 0; (i < onlyThisValues.length) && !toAdd; i++)
                        {
                            if (xcod.equals(onlyThisValues[i]))
                            {
                                toAdd = true;
                            }
                        }
                    }

                    if (toAdd)
                    {
                        add(xcod, xlabel);
                    }
                }
            }
        }
    }
    
    // psantos ini
    public lovObject(   EboContext ctx,
                        long lovBoui,
                        String name, 
                        String sql, 
                        String field_description, 
                        String field_cod, 
                        Hashtable queryParameters, 
                        String[] onlyThisValues )
    {
        PreparedStatement pstm = null;
        ResultSet         rslt = null;
        Connection        cn   = null;
        try 
        {
            p_lovboui = lovBoui;
            cn = ctx.getConnectionData();
            RegularExpression regex = new RegularExpression( "(:([a-zA-Z0-9_$]+))" );
            ArrayList values = new ArrayList();
            Match match = new Match();
            while( regex.matches( sql, match ) )
            {
                String parName = match.getCapturedText( 2 ).toUpperCase();
                values.add( queryParameters.get( parName ) );
                sql = sql.substring( 0, match.getBeginning( 1 ) )
                        + "?" +
                      sql.substring( match.getEnd( 1 ) );
            }
            pstm = cn.prepareStatement( sql );
            for (int i = 0; i < values.size(); i++) 
            {
                if( values.get( i ) == null )
                {
                    pstm.setString( i + 1, null );
                }
                else
                {
                    pstm.setObject( i + 1, values.get( i ) );
                }
            }
            rslt = pstm.executeQuery();
            while (rslt.next())
            {                
                Object o = rslt.getObject(field_cod);
                String xcod  = "";
                if( o != null)
                {
                    xcod                 = o.toString();   
                }                
                String[] fieldsDescription = field_description.split(",");
                StringBuffer str           = new StringBuffer();

                for (int i = 0; i < fieldsDescription.length; i++)
                {
                    str.append(rslt.getObject( field_description )).append(" ");
                }
                
                boolean toAdd = true;

                if (onlyThisValues != null)
                {
                    toAdd = false;

                    for (int i = 0; (i < onlyThisValues.length) && !toAdd; i++)
                    {
                        if (xcod.equals(onlyThisValues[i]))
                        {
                            toAdd = true;
                        }
                    }
                }

                if (toAdd)
                {
                    add(xcod, str.toString(), true);
                }
            }
        }
        catch (Exception ex) 
        {
            ex.printStackTrace();
        } 
        finally 
        {
            try 
            {
                rslt.close();
                pstm.close();
            } catch (Exception ex) 
            { 
                ex.printStackTrace();
            } finally 
            {
            }
        }
        
    }
    
     public lovObject(   EboContext ctx,
                        long lovBoui,
                        String name, 
                        String sql, 
                        String field_description, 
                        String field_cod, 

                        String[] onlyThisValues )
    {
        PreparedStatement pstm = null;
        ResultSet         rslt = null;
        Connection        cn   = null;
        try 
        {
            p_lovboui = lovBoui;
            cn = ctx.getConnectionData();
            RegularExpression regex = new RegularExpression( "(:([a-zA-Z0-9_$]+))" );
             Match match = new Match();
            
            pstm = cn.prepareStatement( sql );
            rslt = pstm.executeQuery();
            while (rslt.next())
            {                
                Object o = rslt.getObject(field_cod);
                String xcod  = "";
                if( o != null)
                {
                    xcod                 = o.toString();   
                }                
                String[] fieldsDescription = field_description.split(",");
                StringBuffer str           = new StringBuffer();

                for (int i = 0; i < fieldsDescription.length; i++)
                {
                    str.append(rslt.getObject( field_description )).append(" ");
                }
                
                boolean toAdd = true;

                if (onlyThisValues != null)
                {
                    toAdd = false;

                    for (int i = 0; (i < onlyThisValues.length) && !toAdd; i++)
                    {
                        if (xcod.equals(onlyThisValues[i]))
                        {
                            toAdd = true;
                        }
                    }
                }

                if (toAdd)
                {
                    add(xcod, str.toString(), true);
                }
            }
        }
        catch (Exception ex) 
        {
            ex.printStackTrace();
        } 
        finally 
        {
            try 
            {
                if (rslt!= null) rslt.close();
                if (pstm!= null) pstm.close();
            } catch (Exception ex) 
            { 
                ex.printStackTrace();
            } finally 
            {
            }
        }
        
    }


    // psantos fim

    public lovObject(
        EboContext ctx, long lovBoui, String name, String tableName, String whereClause, String field_description,
        String field_cod, String[] onlyThisValues
    )
        throws boRuntimeException
    {
        PreparedStatement pst = null;
        ResultSet rslt = null;
        try
        {
            p_lovboui = lovBoui;
            Connection cn;
            cn = ctx.getConnectionData();
            
            String sql = "";
            String[] fields1 = field_description.split(",");
            String[] fields2 = field_cod.split(",");
            
            if ("".equals(whereClause) || (whereClause == null))
            
            {
                String f="";
                for (int i = 0; i < fields1.length; i++) 
                {
                    f+= fields1[i];
                    if ( i+1 < fields1.length )
                    {
                        f+=",";
                    }
                }
                for (int i = 0; i < fields2.length; i++) 
                {
                    if ( f.toUpperCase().indexOf( fields2[i].toUpperCase() ) == -1 )
                    {
                        
                        f+=","+fields2[i];
                    }
                }
                
                sql = "select " + f + " from " + tableName + " order by " +
                    field_description;
            }
            else
            {
                sql = "select " + field_description + "," + field_cod + " from " + tableName + " where " +
                    whereClause + " order by " + field_description;
            }

            pst       = cn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rslt     = pst.executeQuery();

            //boolean haveResults = rslt.next();
            while (rslt.next())
            {                
                Object o = rslt.getObject(field_cod);
                String xcod  = "";
                if( o != null)
                {
                    xcod                 = o.toString();   
                }                
                String[] fieldsDescription = field_description.split(",");
                StringBuffer str           = new StringBuffer();

                for (int i = 0; i < fieldsDescription.length; i++)
                {
                    str.append(rslt.getObject(fieldsDescription[i])).append(" ");
                }

                
                
                boolean toAdd = true;

                if (onlyThisValues != null)
                {
                    toAdd = false;

                    for (int i = 0; (i < onlyThisValues.length) && !toAdd; i++)
                    {
                        if (xcod.equals(onlyThisValues[i]))
                        {
                            toAdd = true;
                        }
                    }
                }

                if (toAdd)
                {
                    add(xcod, str.toString());
                }
            }
        }
        catch (SQLException e)
        {
            throw new boRuntimeException("Error Creating LovObject", "", e);
        }
        finally
        {
            try{if(rslt != null) rslt.close();}catch(Exception e){}
            try{if(pst != null) pst.close();}catch(Exception e){}
        }
    }
    
    // psantos ini
     private void add(String cod, String description, boolean allvalues )
    {
        if (allvalues || !p_lov_cod.contains(cod))
        {
            p_lov_cod.add(cod);
            p_lov_description.add(description);
            p_count++;
        }
    }
    // psantos fim

    private void add(String cod, String description)
    {
        if (!p_lov_cod.contains(cod))
        {
            p_lov_cod.add(cod);
            p_lov_description.add(description);
            p_count++;
        }
    }
    
    public int getSize()
    {
        return p_count;
    }
    public boolean beforeFirst()
    {
        p_pointer = -1;

        return true;
    }

    public boolean first()
    {
        if (p_count > 0)
        {
            p_pointer = 1;

            return true;
        }

        return false;
    }

    public boolean next()
    {
        p_pointer++;

        if (p_pointer >= p_count)
        {
            return false;
        }

        return true;
    }

    public boolean previous()
    {
        p_pointer--;

        if (p_pointer < 0)
        {
            p_pointer = -1;

            return false;
        }

        return true;
    }

    public String getCode()
    {
        if ((p_pointer == -1) || (p_pointer >= p_count))
        {
            return null;
        }

        return ( String ) p_lov_cod.get(p_pointer);
    }

    public String getDescription()
    {
        if ((p_pointer == -1) || (p_pointer >= p_count))
        {
            return null;
        }

        return ( String ) p_lov_description.get(p_pointer);
    }
    
    public long getLovBoui()
    {
        return p_lovboui;
    }
    
    public boolean findLovItemByCode(String code) {
        boolean toRet=false;
        beforeFirst();        
        while (next()) {
            if (getCode().equals(code)) toRet=true;
        }
        return toRet;
    }

    /**
     * Pedro Rio
     * 
     * Retrieves an item description, given its code
     * 
     * @param code The code of the  
     * 
     * @return A string with the description of the given code
     */
    public String getDescriptionByCode(String code){
    	beforeFirst();
    	while (next())
    	{
    		if (getCode().equals(code))
    			return getCode();
    	}
    	return null;
    }
    
    
    public boolean findLovItemByDescription(String description) {
        boolean toRet=false;
        beforeFirst();        
        while (next()) {
            if (getDescription().equals(description))toRet=true;
        }
        return toRet;
    }    
}
