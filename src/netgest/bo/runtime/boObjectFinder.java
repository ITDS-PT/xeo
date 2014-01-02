/*Enconding=UTF-8*/
package netgest.bo.runtime;

public class boObjectFinder 
{
    public static final byte UNIQUE=0;
    public static final byte PRIMARY_KEY=1;
    public static final byte MULTI=2;
    
    
    public byte     p_type;
    public String   p_id;
    public String[] p_atts;
    public String[] p_label;
    
    public boObjectFinder( byte type, String id, String[] atts, String[] labels )
    {
        p_type  = type;
        p_id    = id;
        p_atts  = atts;
        p_label = labels;
    }

    public String[] getAttributes()
    {
        return p_atts;
    }

    public String getId()
    {
        return p_id;
    }

    public byte getType()
    {
        return p_type;
    }

    public String[] getLabels()
    {
        return p_label;
    }
}