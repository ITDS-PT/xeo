package netgest.bo.def.v2;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.def.boDefObjectFilter;

public class boDefObjectFilterImpl implements boDefObjectFilter 
{
    String  p_forObject;
    String  p_xeoql;
    
    boDefXeoCodeImpl    p_condition;
    
    public boDefObjectFilterImpl( String forObject, String xeoql, boDefXeoCodeImpl condition )
    {
        this.p_forObject = forObject;
        this.p_xeoql     = xeoql;
        this.p_condition = condition;
    }

    public String getForObject()
    {
        return p_forObject;
    }

    public String getXeoQL()
    {
        return p_xeoql;
    }

    public boDefXeoCode getCondition()
    {
        return p_condition;
    }
}