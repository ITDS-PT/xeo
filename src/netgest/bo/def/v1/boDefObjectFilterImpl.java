package netgest.bo.def.v1;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.def.boDefObjectFilter;

public class boDefObjectFilterImpl implements boDefObjectFilter 
{
    String p_xeoql;
    boDefXeoCode p_bolcode;
    String p_forObject;
    
    public boDefObjectFilterImpl( int bol_lang, String bol_condition, String xeoql, String forObject )
    {
        this.p_xeoql     = xeoql;
        this.p_forObject = forObject;
        if( bol_condition != null )
        {
            this.p_bolcode   = new boDefXeoCodeImpl( bol_lang, null, bol_condition );
        }
    }

    public String getForObject()
    {
        return this.p_forObject;
    }

    public String getXeoQL()
    {
        return this.p_xeoql;
    }

    public boDefXeoCode getCondition()
    {
        return null;
    }
}