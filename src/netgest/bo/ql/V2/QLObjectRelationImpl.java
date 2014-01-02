package netgest.bo.ql.V2;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;

public class QLObjectRelationImpl implements IQLObjectRelation 
{
    private boDefAttribute          def;
    private QLObjectAttributeImpl   parent;
    private boolean                 isBridge;
    
    public QLObjectRelationImpl( boolean isbridge, QLObjectAttributeImpl parent, boDefAttribute def)
    {
        this.def    = def;
        this.parent = parent;
    }

    public IQLObjectAttribute getParent()
    {
        return parent;
    }

    public IQLObject getLocalObject()
    {
        return getParent().getParent();
    }

    public IQLObject getRelatedObject()
    {
        return parent.getParent().getResolver().resolveObject( this, def.getReferencedObjectName() );
    }

    public int getRelationType()
    {
        return IQLObjectRelation.RELATION_1_N;
    } 

    public IQLObjectAttribute[] getLocalKeys()
    {
        return new IQLObjectAttribute[] { new QLObjectAttributeImpl( parent.getParent(), def ) };
    }

    public IQLObjectAttribute[] getRemoteKeys()
    {
        return new IQLObjectAttribute[] { 
                getParent().getParent().getResolver().getQLParser().getObjectResolver().resolveObject( this, def.getReferencedObjectName() ).resolveAttribute( false, "BOUI" ) 
            };
    }

    public boolean isNotNullRelation()
    {
        return true;
    }
}