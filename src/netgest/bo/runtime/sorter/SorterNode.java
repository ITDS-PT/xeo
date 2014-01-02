/*Enconding=UTF-8*/
package netgest.bo.runtime.sorter;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class SorterNode 
{
    private long boui = 0;
    private Object value = null;
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public SorterNode(long boui, Object value)
    {
        this.boui = boui;
        this.value = value;
    }
    
    public long getBoui()
    {
        return this.boui;
    }
    
    public Object getValue()
    {
        return this.value;
    }
    
    public static final SorterNode[] grow(SorterNode[] Array, int inc)
    {
        SorterNode[] toRet = new SorterNode[Array.length + inc];
        System.arraycopy(Array, 0, toRet, 0, Array.length);

        return toRet;
    }
    
    public static final SorterNode[] setSize(SorterNode[] Array, int size)
    {
        SorterNode[] toRet = new SorterNode[size];
        System.arraycopy(Array, 0, toRet, 0, size);

        return toRet;
    }
}