package netgest.bo.ql.V2;

public interface IQLObjectAttribute 
{
    public static final int VALUE_NUMBER=1;
    public static final int VALUE_CHAR=2;
    public static final int VALUE_DATE=3;
    public static final int VALUE_UNKNOWN=4;
    
    public String   getName();
    public String   getNativeName();

    public int      getValueType();
    public int      getAttributeType();
    
    public IQLObjectRelation getRelation();
    public boolean           isRelation();
    public IQLObject         getParent();
    
    public String[]          getReferencedObjects();

}