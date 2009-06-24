package netgest.bo.system;
import java.util.Vector;
import netgest.bo.runtime.EboContext;

public class boContext 
{
    private Vector          allEboContexts;
    
    private boApplication   application;
    private EboContext      eboContext;
    
    public boContext( boApplication application )
    {
        this.application = application;
    }
    
    public boApplication getApplication()
    {
        return application;
    }
    
    public EboContext getEboContext()
    {
        return eboContext;       
    }
    
    public EboContext[] getEboContexts()
    {
        if( allEboContexts != null )
        {
            return (EboContext[])allEboContexts.toArray( new EboContext[ allEboContexts.size() ] );
        }
        return null;
    }
    
    public void addEboContext( EboContext oEboContext ) {
        if( allEboContexts == null ) {
            allEboContexts = new Vector();
        }
        if( allEboContexts.indexOf( oEboContext ) == -1 ) {
            allEboContexts.add( oEboContext );
        }
        if( this.eboContext == null ) {
            this.eboContext = oEboContext;
        }
    }
    
}