package netgest.bo.ql.V2;
import netgest.bo.def.boDefAttribute;

public class QLObjectRelationBridgeImpl implements IQLObjectRelation 
{
    public static final int TYPE_OBJECT_TO_BRIDGE = 1;
    public static final int TYPE_BRIDGE_TO_OBJECT = 2;

    private boDefAttribute          def;
    private QLObjectAttributeImpl   parent;
    private int                     type;
    
    public QLObjectRelationBridgeImpl( int type, QLObjectAttributeImpl parent, boDefAttribute def)
    {
        this.def    = def;
        this.parent = parent;
        this.type   = type;
    }

    public IQLObjectAttribute getParent()
    {
        return parent;
    }

    public IQLObject getLocalObject()
    {
        return parent.getParent();
    }

    public IQLObject getRelatedObject()
    {
        switch( this.type )
        {
            case TYPE_OBJECT_TO_BRIDGE:
                return new QLObjectBridgeImpl( getParent().getParent().getResolver(), this, getParent().getParent(), def );
            case TYPE_BRIDGE_TO_OBJECT:
            default:
                return parent.getParent().getResolver().resolveObject( def.getReferencedObjectName() );
        }
    }

    public IQLObjectAttribute[] getLocalKeys()
    {
        if( !"boObject".equalsIgnoreCase( def.getReferencedObjectName() ) )
        {
            switch( this.type )
            {
                case TYPE_OBJECT_TO_BRIDGE:
                    return new IQLObjectAttribute[] { parent.getParent().resolveAttribute( false, "BOUI" ) };
                case TYPE_BRIDGE_TO_OBJECT:
                default:
                    return new IQLObjectAttribute[] { new QLObjectAttributeGenericImpl( parent.getParent(), def.getBridge().getChildFieldName() ) };
            }
        }
        return new IQLObjectAttribute[] { parent.getParent().resolveAttribute( false, "BOUI" ) };
    }

    public IQLObjectAttribute[] getRemoteKeys()
    {
        switch( this.type )
        {
            case TYPE_OBJECT_TO_BRIDGE:
                return new IQLObjectAttribute[] { new QLObjectAttributeGenericImpl( parent.getParent(), def.getBridge().getFatherFieldName() ) };
            case TYPE_BRIDGE_TO_OBJECT:
            default:
                return new IQLObjectAttribute[] { getRelatedObject().resolveAttribute( false, "BOUI" ) };
        }
    }

    public int getRelationType()
    {
        return 0;
    }
    
    public boolean isNotNullRelation()
    {
        return true;
    }


    public void setType(int type)
    {
        this.type = type;
    }


    public int getType()
    {
        return type;
    }
}