/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.math.BigDecimal;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import netgest.bo.def.boDefAttribute;

import netgest.bo.runtime.sorter.AttributeSorter;
import netgest.bo.runtime.sorter.CardidSorter;
import netgest.bo.runtime.sorter.SorterNode;
import org.apache.log4j.Logger;
import netgest.bo.runtime.sorter.ClassSorter;

/**
 * 
 * @author JMF
 */
public final class boBridgeIterator 
{
    private bridgeHandler p_bridge;
    private int           p_row;
    private String        p_orderBy = null;
    private int[]           p_virtualRow = null;
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.boBridgeIterator");
    public boBridgeIterator( bridgeHandler bridge )
    {
        p_bridge = bridge;
        p_row = 0;
    }
    
    public boBridgeIterator( bridgeHandler bridge, String orderBy)
    {
        p_bridge = bridge;
        p_row = 0;
        p_orderBy = orderBy;
        if(orderBy != null)
        {
            try
            {
                long ti = System.currentTimeMillis();
                boolean attribute = false;
                if(!bridge.isEmpty())
                {
                    ClassSorter cs = null;
                    bridge.beforeFirst();
                    bridge.next();
                    try
                    {
                        if(bridge.getObject().getAttribute(orderBy) != null)
                        {
                            cs = new AttributeSorter(bridge.getObject().getName(), orderBy);
                        }
                        else if(!"CARDID".equals(orderBy))
                        {
                            cs = new CardidSorter();
                        }
                        else
                        {
                            cs = (ClassSorter)Class.forName(orderBy).newInstance();
                        }
                    }
                    catch (InstantiationException e)
                    {
                        logger.error(e);
                    }
                    catch (IllegalAccessException e)
                    {
                        logger.error(e);
                    }
                    catch (ClassNotFoundException e)
                    {
                        logger.error(e);
                    }
                    SorterNode[] helper = cs.getValues(bridge);
                    cs.sort(helper);
                    String strBouis = bridge.getValueString();
                    String[] phisicalRow = strBouis.split(";");
                    
                    p_virtualRow = new int[helper.length];
                    String boui;
                    int pos;
                    for (int i = 0; i < helper.length; i++) 
                    {
                        boui = String.valueOf(helper[i].getBoui());
                        pos = findKey(phisicalRow, boui);
                        phisicalRow[pos] = null;
                        p_virtualRow[i] = pos+1;
                    }
                }
                logger.info("Total time:" + (System.currentTimeMillis() - ti));
            }
            catch (boRuntimeException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static int findKey(String[] a, String key)
    {
        if(key == null) return -1;
        for (int i = 0; i < a.length; i++) 
        {
            if(key.equalsIgnoreCase(a[i]))
            {
                return i;
            }
        }
        return -1;
    }

    private Object getDefaultValue(byte type)
    {
        switch(type)
        {
            case boDefAttribute.VALUE_DATE:
            case boDefAttribute.VALUE_DATETIME:
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, 1900);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.DATE, 1);
                    return new Timestamp(c.getTimeInMillis());
            case boDefAttribute.VALUE_NUMBER:
            case boDefAttribute.VALUE_DURATION:
            case boDefAttribute.VALUE_SEQUENCE:
                    return BigDecimal.valueOf(0);
            default:
                 return "";
        }
    }
    
    public int translateToPhisicalRow(int row)
    {
        if(p_virtualRow != null)
        {
            return p_virtualRow[row-1];
        }
        return row;
    }
    
    public final boolean isBeforeFirst()
    {
        if( p_row == 0 ) return true;
        else return false;
    }

    public final boolean isAfterLast()
    {
        if( p_row > p_bridge.getRowCount() || p_bridge.getRowCount() == 0 ) return true;
        else return false;
    }

    public final boolean isFirst()
    {
        return p_row == 1;
    }

    public final boolean isLast()
    {
        return p_row == p_bridge.getRowCount();
    }

    public final void beforeFirst()
    {
        p_row = 0;
    }

    public final void afterLast()
    {
        p_row = p_bridge.getRowCount() > 0?p_bridge.getRowCount()+1:0;
    }

    public final boolean first()
    {
        p_row = p_bridge.getRowCount() > 0?1:0;  
        return p_bridge.getRowCount() > 0;
    }

    public final boolean last()
    {
        p_row = p_bridge.getRowCount() > 0?p_bridge.getRowCount():0;  
        return p_bridge.getRowCount() > 0;
    }

    public final int getRow()
    {
        return p_row;
    }

    public final boolean absolute(int row)
    {
        boolean ret = false;
        if( row >= 0 && row <= p_bridge.getRowCount() ) 
        {
            p_row = row;
            ret = true;
        }
        else if ( row > p_bridge.getRowCount() )
        {
            p_row = p_bridge.getRowCount() + 1;
            ret = false;
        }
        else if ( row < 1 )
        {
            p_row = 0;
            ret = false;
        }
        return ret;
    }

    public final boolean relative(int rows)
    {
        boolean ret = false;
        if( p_row+rows > 0 && p_row+rows <= p_bridge.getRowCount() ) 
        {
            p_row = p_row+rows;
            ret = true;
        }
        return ret;
    }

    public final  boolean next()
    {
        if( p_row < p_bridge.getRowCount() )
        {
            p_row++;
            return true;
        }
        else 
        {   
            if ( p_row == p_bridge.getRowCount() &&  p_bridge.getRowCount() > 0 )
            {
                p_row++;
            }
            return false;
        }
    }

    public final boolean previous()
    {
        if( p_row > 1 )
        {
            p_row--;
            return true;
        }
        else 
        {
            if( p_row > 0 )
            {
                p_row--;
            }
            return false;
        }
    }
    
    public final boBridgeRow currentRow()
    {
        if( p_row < 1 || p_row > p_bridge.getRowCount() )
        {
            throw new RuntimeException("BridgeIterator is before first, after last or is empty. ");
        }
        return p_bridge.rows( translateToPhisicalRow(p_row) );
    }
    
    public final bridgeHandler getBridgeHandler()
    {
        if( p_bridge.getRowCount() > 0 && p_orderBy != null)
        {
            p_bridge.moveTo(translateToPhisicalRow(p_row == 0 ? 1:p_row));
        }
        return p_bridge;
    }
    
    
    
}