/*Enconding=UTF-8*/
package netgest.bo.transformers;
import java.sql.Connection;
import java.sql.SQLException;
import netgest.utils.DataUtils;
import netgest.bo.runtime.boObject;
/**
 * 
 * @author JMF
 * @version 
 * @see 
 */
public class ReclamationCast implements CastInterface
{
    /**
     * 
     * @see 
     */
    public ReclamationCast()
    {
    }
    
    public void beforeCast(boObject object) throws netgest.bo.runtime.boRuntimeException
    {        
    }
    public void afterCast(boObject object) throws netgest.bo.runtime.boRuntimeException
    {   Connection con = object.getEboContext().getDedicatedConnectionData();
        try{
			if(object.getAttribute("nrdoc").getValueLong()<=0)
            {
				long nrdoc = DataUtils.GetSequenceNextVal(object.getEboContext().getApplication(),  con, object.getName());
				object.getAttribute("nrdoc").setValueLong(nrdoc);
			}
        }finally
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {
                //ignore
            }
        }
    }
    
    public boolean isToRefresh(boObject obj) throws netgest.bo.runtime.boRuntimeException
    {
        return false;
    }
}