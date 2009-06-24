package netgest.bo.ql.V2;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefHandler;


public class QLObjectBridgeImpl implements IQLObject, IQLObjectAttributeResolver
{
    
    private boDefBridge                 bridge;
    private boDefHandler                relObj;
    private boDefAttribute              defAtt;
    private QLObjectRelationBridgeImpl  parent;
    private IQLObjectResolver           resolver;
    
    public QLObjectBridgeImpl(IQLObjectResolver resolver, QLObjectRelationBridgeImpl parent, IQLObject qlobj, boDefAttribute def )
    {
        this.defAtt     = def;
        this.bridge     = defAtt.getBridge();
        this.parent     = parent;
        this.resolver   = resolver;
    }

    public IQLObjectRelation getParent()
    {
        return parent;
    }

    public String getName()
    {
        return bridge.getName();
    }

    public String getNativeName( boolean extended )
    {
        return extended?bridge.getBoMasterTable():bridge.getBoMasterTable();
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
        boolean ret = relObj.hasAttribute( attName );
        if( !ret )
            ret = bridge.hasAttribute( attName );
        
        return ret;
    }

    public boolean implementsSecurityRowObjects()
    {
        return false;
    }


    public IQLObjectAttribute resolveAttribute(String attName)
    {
        if( attName.equalsIgnoreCase( bridge.getName() ) )
        {
            QLObjectAttributeImpl matt = new QLObjectAttributeImpl( this, this.defAtt );
            matt.isbridge = true;
            return matt;
        }
        else
        {
            boDefAttribute att = bridge.getAttributeRef( bridge.getName() );
            if( att == null )
            {
                QLObjectAttributeImpl matt = new QLObjectAttributeImpl( this, this.defAtt );
                matt.isbridge = true;
                return matt.getRelation().getRelatedObject().getAttributeResolver().resolveAttribute( false, attName );
            }
            else
            {
                QLObjectAttributeImpl qlatt = new QLObjectAttributeImpl( this, att );
                return qlatt.getRelation().getRelatedObject().getAttributeResolver().resolveAttribute( false, attName );
            }
        }
    }

    public IQLObjectResolver getResolver()
    {
        return resolver;
    }

    public IQLObjectAttribute[] getKeys()
    {
        return new IQLObjectAttribute[] { new QLObjectAttributeImpl( this,  this.defAtt ) };
    }
    
    public IQLObjectAttribute[] getDefaultAttributes()
    {
        return new IQLObjectAttribute[] { new QLObjectAttributeGenericImpl( this, "CHILD$" ) };
    }

    public IQLObjectState resolveState(boolean extended, String stateName)
    {
        return null;
    }

    public IQLObjectAttribute resolveAttribute(boolean extended, String attName)
    {
        return resolveAttribute( attName );
    }
    
}