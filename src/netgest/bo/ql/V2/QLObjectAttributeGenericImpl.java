package netgest.bo.ql.V2;

public class QLObjectAttributeGenericImpl implements IQLObjectAttribute 
{
    private String      name;
    private IQLObject   parent;

    public QLObjectAttributeGenericImpl( IQLObject parent, String name )
    {
        this.name   = name;
        this.parent = parent;
    }

    public String getName()
    {
        return name;
    }

    public String getNativeName()
    {
        return name;
    }

    public int getValueType()
    {
        return 0;
    }

    public int getAttributeType()
    {
        return 0;
    }

    public IQLObjectRelation getRelation()
    {
        return null;
    }

    public boolean isRelation()
    {
        return false;
    }

    public IQLObject getParent()
    {
        return parent;
    }

    public String[] getReferencedObjects()
    {
        return null;
    }
}