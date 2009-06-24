package netgest.bo.ql.V2;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;

public class QLObjectAttributeImpl implements IQLObjectAttribute 
{
    
    private     boDefAttribute  def;
    private     IQLObject       qlobj;
    protected   boolean         isbridge;
    
    public QLObjectAttributeImpl( IQLObject qlobj, boDefAttribute def )
    {
        this.def = def;
        this.qlobj = qlobj;
    }

    public String getName()
    {
        return def.getName();
    }

    public String getNativeName()
    {
        return "\""+def.getDbName()+"\"";
    }

    public int getValueType()
    {
        switch( def.getValueType() ) 
        {
            case boDefAttribute.VALUE_BLOB:
            case boDefAttribute.VALUE_BOOLEAN:
            case boDefAttribute.VALUE_CHAR:
            case boDefAttribute.VALUE_CLOB:
            case boDefAttribute.VALUE_IFILELINK:
                return VALUE_CHAR;
            
            case boDefAttribute.VALUE_DATE:
            case boDefAttribute.VALUE_DATETIME:
                return VALUE_DATE;

            case boDefAttribute.VALUE_DURATION:
            case boDefAttribute.VALUE_NUMBER:
            case boDefAttribute.VALUE_CURRENCY:
            case boDefAttribute.VALUE_SEQUENCE:
                return VALUE_NUMBER;
            case boDefAttribute.VALUE_UNKNOWN:
            default:
                return VALUE_UNKNOWN;
        }
    }

    public int getAttributeType()
    {
        return def.getAtributeType();
    }

    public IQLObjectRelation getRelation()
    {
        if( !this.isbridge )
        {
            switch( def.getRelationType() )
            {
                case boDefAttribute.RELATION_1_TO_1:
                case boDefAttribute.RELATION_1_TO_N:
                    return new QLObjectRelationImpl( false, this, def );
                case boDefAttribute.RELATION_1_TO_N_WBRIDGE:
                    return new QLObjectRelationBridgeImpl( QLObjectRelationBridgeImpl.TYPE_OBJECT_TO_BRIDGE, this, def );
            }
        }
        else
        {
            if( 
                this.def.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE
                &&
                this.def.getReferencedObjectDef() != null && !"boObject".equals( this.def.getReferencedObjectDef() ) 
            
            )
            {
                return new QLObjectRelationBridgeImpl( QLObjectRelationBridgeImpl.TYPE_BRIDGE_TO_OBJECT, this, def );
            }
        }
        return null;
    }

    public boolean isRelation()
    {
        boolean ret = false;
        // É sempre um relação
        if( this.def.getMaxOccurs() > 1 || def.getDbIsTabled() )
            ret = true;
        else
            ret = (def.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && def.getReferencedObjectDef() != null && !def.getReferencedObjectDef().getName().equalsIgnoreCase("boObject") );
            
        return ret;
    }

    public String[] getReferencedObjects()
    {
        String[] ret = null;
        boDefHandler[] relObjs = this.def.getObjects();
        if( relObjs != null )
        {
            ret = new String[ relObjs.length ];
            for (int i = 0; i < relObjs.length; i++) 
            {
                ret[i] = relObjs[i].getName();
            }
        }
        return ret;
    }

    public IQLObject getParent()
    {
        return qlobj;
    }
    
}