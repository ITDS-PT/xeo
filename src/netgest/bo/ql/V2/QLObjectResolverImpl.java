package netgest.bo.ql.V2;
import netgest.bo.def.*;

public class QLObjectResolverImpl implements IQLObjectResolver 
{
    private QLParser qlp;

    public IQLObject resolveObject(String name)
    {
        boDefHandler def = boDefHandler.getBoDefinition( name );
        return def==null?null:new QLObjectImpl( this, def );
    }

    public IQLObject resolveObject( IQLObjectRelation relation, String name )
    {
        boDefHandler def = boDefHandler.getBoDefinition( name );
        return def==null?null:new QLObjectImpl( relation, this, def );
    }

    public QLParser getQLParser()
    {
        return this.qlp;
    }

    public void setQLParser( QLParser qp )
    {
        this.qlp = qp;
    }
    
}