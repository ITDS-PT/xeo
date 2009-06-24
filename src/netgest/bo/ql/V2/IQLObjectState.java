package netgest.bo.ql.V2;

public interface IQLObjectState extends IQLObjectAttribute
{
    public String         getValue();
    public IQLObjectState getChildState( String stateName );
}