/*Enconding=UTF-8*/
package netgest.bo.parser.types;

/**
 * Representa um tipo primitivo
 * <P>
 * @author Francisco Câmara
 */
public class PrimitiveType extends Type
{
    /**
     * Constante para o tipo primitivo VOID
     */
    public static final PrimitiveType VOID = new PrimitiveType("void");

    /**
     * Guarda o código do tipo BOOLEAN
     */
    public static final PrimitiveType BOOLEAN = new PrimitiveType("boolean");

    /**
     * Guarda o código do tipo BYTE
     */
    public static final PrimitiveType BYTE = new PrimitiveType("byte");

    /**
     * Guarda o código do tipo CHAR
     */
    public static final PrimitiveType CHAR = new PrimitiveType("char");

    /**
     * Guarda o código do tipo SHORT
     */
    public static final PrimitiveType SHORT = new PrimitiveType("short");

    /**
     * Guarda o código do tipo INT
     */
    public static final PrimitiveType INT = new PrimitiveType("int");

    /**
     * Guarda o código do tipo LONG
     */
    public static final PrimitiveType LONG = new PrimitiveType("long");

    /**
     * Guarda o código do tipo FLOAT
     */
    public static final PrimitiveType FLOAT = new PrimitiveType("float");

    /**
     * Guarda o código do tipo DOUBLE
     */
    public static final PrimitiveType DOUBLE = new PrimitiveType("double");

    /**
     * Constructor
     * @param typeName Nome do tipo
     */
    private PrimitiveType(String typeName)
    {
        super(typeName);
    }
}

