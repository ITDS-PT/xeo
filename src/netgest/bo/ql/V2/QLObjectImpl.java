package netgest.bo.ql.V2;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefClsState;
import netgest.bo.def.boDefDatabaseObject;
import netgest.bo.def.boDefHandler;
import netgest.bo.plugins.DataPluginManager;
import netgest.bo.plugins.IDataPlugin;
import netgest.bo.plugins.data.MapType2Def;

public class QLObjectImpl implements IQLObject, IQLObjectAttributeResolver 
{

    private boDefHandler            def;
    private QLObjectResolverImpl    resolver; 
    private IQLObjectRelation       parent;

    public QLObjectImpl(IQLObjectRelation parent, QLObjectResolverImpl resolver, boDefHandler def )
    {
        this.constructor( parent, resolver, def ); 
    }
    public QLObjectImpl(QLObjectResolverImpl resolver, boDefHandler def )
    {
        this.constructor( null, resolver, def ); 
    }
    
    private void constructor(IQLObjectRelation parent, QLObjectResolverImpl resolver, boDefHandler def )
    {
        this.def        = def;
        this.resolver   = resolver;
        this.parent     = parent;
    }

    public String getName()
    {
        return def.getName();
    }

    public String getNativeName( boolean extended )
    {
        IDataPlugin dataPlugIn = DataPluginManager.getPlugIns()[1];
        if( false && dataPlugIn.getDataManager( this.def ) != null )
        {
            MapType2Def mdef        = MapType2Def.getDataSourceDefinition( this.def );
            MapType2Def.ObjectDS[] ds = mdef.getObjectDataSources().getDataSources();
            
            return ds[0].getSourceObject();
        }
        else
        {
            return extended?def.getBoExtendedTable():def.getBoMasterTable();
        }
    }

    public IQLObjectRelation getRelationWith(IQLObject object)
    {
        return null;
    }

    public IQLObjectAttributeResolver getAttributeResolver()
    {
        return this;
    }

    public boolean hasAttribute(boolean extended, String attName)
    {
        return def.hasAttribute( attName );
    }

    public boolean implementsSecurityRowObjects()
    {
        return this.def.implementsSecurityRowObjects() && this.def.getClassType() != boDefHandler.TYPE_ABSTRACT_CLASS;
    }

    public boDefHandler getDef()
    {
        return def;
    }

    public IQLObjectAttribute resolveAttribute(boolean extended, String attName )
    {
        boDefAttribute defatt = this.def.getAttributeRef( attName );
        if( defatt == null && extended )
        {
            boDefHandler[] subs = def.getBoSubClasses();
            for (int i = 0;defatt == null && subs != null && i < subs.length; i++) 
            {
                defatt = subs[i].getAttributeRef( attName );
            }
        }
        
        if( defatt != null )  
        {
            return new QLObjectAttributeImpl( this, defatt );
        }
        return null;
    }

    public IQLObjectRelation getParent()
    {
        return parent;
    }

    public IQLObjectAttribute[] getKeys()
    {
        return new IQLObjectAttribute[] { resolveAttribute(false, "BOUI") };
    }
    
    public IQLObjectResolver getResolver()
    {
        return this.resolver;
    }
    
    public IQLObjectAttribute[] getDefaultAttributes()
    {
    	if ( this.def.getDataBaseManagerXeoCompatible() )
    		return new IQLObjectAttribute[] { resolveAttribute( false, "BOUI" ) };
    	else {
    		IQLObjectAttribute[] ret = null;
    		boDefDatabaseObject[] dbobjs = this.def.getBoDatabaseObjects();
    		for( int i=0; i < dbobjs.length; i++ ) {
    			if( dbobjs[i].getType() == boDefDatabaseObject.DBOBJECT_PRIMARY ) {
    				
    				String[] sKeysValues = dbobjs[i].getExpression().split(",");
    				
    				ret = new IQLObjectAttribute[ sKeysValues.length + 1 ];
    				int k;
    				for( k=0; k < sKeysValues.length; k++ ) {
    					ret[k] = resolveAttribute( false, sKeysValues[k].trim() );
    				}
    				
    				ret[k] = new IQLObjectAttribute() {
    					public IQLObjectRelation getRelation() {
    						return null;
    					}
    					public int getAttributeType() {
    						return boDefAttribute.TYPE_ATTRIBUTE;
    					}
    					public String getName() {
    						return "BOUI";
    					}
    					public String getNativeName() {
    						return  Long.MIN_VALUE + " AS BOUI";
    					}
    					public IQLObject getParent() {
    						// TODO Auto-generated method stub
    						return null;
    					}
    					public String[] getReferencedObjects() {
    						// TODO Auto-generated method stub
    						return null;
    					}
    					public int getValueType() {
    						// TODO Auto-generated method stub
    						return IQLObjectAttribute.VALUE_UNKNOWN;
    					}
    					public boolean isRelation() {
    						// TODO Auto-generated method stub
    						return false;
    					}
    				};
    				break;
    			}
    		}
    		if( ret == null ) {
    			throw new RuntimeException( "Cannot select objects not compatible with xeo withou a primary defined" );
    		}
    		return ret;
    	}
    }

    public IQLObjectState resolveState(boolean extended, String stateName)
    {
        boDefClsState state = def.getBoClsState().getChildState( stateName );
        return state==null?null:new QLObjectState( this, state );
    }
        
}