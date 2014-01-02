/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.math.BigDecimal;
import java.util.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import netgest.utils.*;

/**
 * 
 * @author JMF
 */
public class boThread 
{
    private   HashMap p_tree      = new HashMap(5);
    
    public boThread()
    {
    }
    public void clear()
    {
        p_tree= new HashMap(5);
    }

    public synchronized void add( BigDecimal parent, BigDecimal child )
    {
    	HashMap key = (HashMap) p_tree.get( child );
        if ( key == null )
        {
        	HashMap x = new HashMap(5);
            x.put( parent, null );
            p_tree.put( child , x );
        }
        else
        {
           if ( !key.containsKey( parent ) )
           {
               key.put( parent, null );
           }
        }
    }
    
    
    public void setChanged(EboContext ctx, BigDecimal bouichanged ) throws boRuntimeException
    {
        //ArrayList parents = getParents( bouichanged );
    	HashMap parents = (HashMap) p_tree.get( bouichanged );
        
        if ( parents!=null )
        {
        	Iterator it = parents.keySet().iterator();
        	while( it.hasNext() )
            for (int i = 0; i < parents.size() ; i++) 
            {
                 boObject obj=null;
                 obj = boObject.getBoManager().loadObject( ctx,  ((BigDecimal)it.next()).longValue()  );
                 if( !obj.isChanged() )
                 {
                    obj.setChanged( true );
                 }
            }
        }
    }
    
    public Map getThreadObjects()
    {
      return this.p_tree;
    }
    
    
    
}