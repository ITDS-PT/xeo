package netgest.bo.ql.V2;

public interface IQLObjectRelation 
{
    public static final int RELATION_1_N = 0;
    public static final int RELATION_1_1 = 1;

    public IQLObjectAttribute getParent();
    
    public IQLObject getLocalObject();
    public IQLObject getRelatedObject();
    
    public IQLObjectAttribute[] getLocalKeys();
    public IQLObjectAttribute[] getRemoteKeys();
    
    public int  getRelationType();
    
    public boolean isNotNullRelation();

}