package netgest.bo.ql.V2;
import java.util.Hashtable;

public class QLFunctionResolverImpl implements IQLFunctionResolver
{
    static Hashtable    functionsTable = new Hashtable();
    
    public QLFunctionResolverImpl()
    {
//        functionsTable.put( "" )
    }
    
    public IQLFunction resolveFunction( String functionName )
    {
        return (IQLFunction)functionsTable.get( functionName );
    }
}