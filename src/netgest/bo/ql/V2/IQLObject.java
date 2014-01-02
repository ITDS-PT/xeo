package netgest.bo.ql.V2;

public interface IQLObject 
{

    
    public String                       getName();
    public String                       getNativeName( boolean extended );
    
    public IQLObjectResolver            getResolver();
    public IQLObjectRelation            getRelationWith( IQLObject object );
    public IQLObjectAttributeResolver   getAttributeResolver(  );
    public IQLObjectRelation            getParent();
    public IQLObjectAttribute[]         getDefaultAttributes();
    
    public IQLObjectAttribute[]         getKeys();
    
    public IQLObjectAttribute           resolveAttribute( boolean extended, String attName );
    public IQLObjectState               resolveState( boolean extended, String stateName );
    public boolean                      hasAttribute( boolean extended, String attName );

    public boolean                      implementsSecurityRowObjects();
    
    
}