package netgest.bo.ql.V2;
import netgest.bo.def.boDefClsState;

public class QLObjectState implements IQLObjectState 
{
    private boDefClsState   state;
    private IQLObject       parent;
    private IQLObjectState  parentState;


    public QLObjectState( IQLObject parent, boDefClsState state )
    {
        this( parent, null, state );
    }

    public QLObjectState( IQLObject parent, IQLObjectState parentState, boDefClsState state )
    {
        this.state          = state;
        this.parent         = parent;
        this.parentState    = parentState;
    }

    public String getValue()
    {
        return String.valueOf( state.getNumericForm() );
    }

    public IQLObjectState getChildState(String stateName)
    {
        boDefClsState childState = state.getChildState( stateName );
        return childState==null?null:new QLObjectState( this.parent, this, childState );
    }

    public String getName()
    {
        return state.getName();
    }

    public String getNativeName()
    {
        return state.getParent().getDbName();
    }

    public int getValueType()
    {
        return VALUE_NUMBER;
    }

    public int getAttributeType()
    {
        return state.getAtributeType();
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
        return this.parent;
    }

    public String[] getReferencedObjects()
    {
        return null;
    }
}