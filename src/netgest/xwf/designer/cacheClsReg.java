/*Enconding=UTF-8*/
package netgest.xwf.designer;
import netgest.bo.runtime.*;

/**
 * 
 * @author JMF
 * @version 
 * @see 
 */
public class cacheClsReg 
{
    /**
     * 
     * @see 
     */
     private static String[] classBouis=null;
    public cacheClsReg()
    {
    }
    
    public static String[] getBouiClassName( EboContext boctx ) throws boRuntimeException
    {
        String[] toRet;
        if ( classBouis == null )
        {
            
            
            boObjectList listCls = boObjectList.list( boctx,"select Ebo_ClsReg ",new Object[]{},1,1000,"name",false);
            toRet= new String[listCls.getRowCount()];
            int i=0;
            while ( listCls.next() ) 
            {
                toRet[i++] = listCls.getObject().getAttribute("name")+":"+listCls.getObject().getBoui();
            }
            classBouis=toRet;
        }
        else
        {
            toRet=classBouis;
        }
        
        return toRet;
    }
    
    
}