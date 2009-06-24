/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

import netgest.bo.parser.types.*;

/**
 * Representa uma declaração de um tipo
 * <P>
 * @author Francisco Câmara
 */
public class TypeClause implements ClauseDeclarationSymbol{
    private Type type;
    private boolean isArray = false;

    /**
     * Constructor
     */
    public TypeClause(String t)
    {
        setType(t);
    }

    public TypeClause(Type t)
    {
        setType(t);
    }

    public void setType(Type t)
    {
        type = t;
    }
    public void setType(String t)
    {
        if(t.equals("boolean"))
            type = PrimitiveType.BOOLEAN;
        else if(t.equals("byte"))
            type = PrimitiveType.BYTE;
        else if(t.equals("char"))
            type = PrimitiveType.CHAR;
        else if(t.equals("double"))
            type = PrimitiveType.DOUBLE;
        else if(t.equals("float"))
            type = PrimitiveType.FLOAT;
        else if(t.equals("int"))
            type = PrimitiveType.INT;
        else if(t.equals("long"))
            type = PrimitiveType.LONG;
        else if(t.equals("short"))
            type = PrimitiveType.SHORT;
        else if(t.equals("void"))
            type = PrimitiveType.VOID;
        else
            type = new ClassType(t);
    }

    public Type getType()
    {
        return type;
    }

    public boolean isIsArray()
    {
        return isArray;
    }

    public void setIsArray(boolean newIsArray)
    {
        isArray = newIsArray;
    }

    public String toJAVA()
    {
        if (isArray)
            return type.getName() + "[]";
        else
            return type.getName();
    }
}


