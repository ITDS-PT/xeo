package netgest.bo.ql.V2;

public interface IQLObjectResolver 
{
    public IQLObject resolveObject( IQLObjectRelation parent, String name );
    public IQLObject resolveObject( String name );
    
    public QLParser  getQLParser();
    public void      setQLParser( QLParser qp );  
    
}