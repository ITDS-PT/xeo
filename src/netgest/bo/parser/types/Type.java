/*Enconding=UTF-8*/
package netgest.bo.parser.types;
/**
 * Representa um tipo de dados
 * <P>
 * @author Francisco Câmara
 */
public class Type
{
    /**
     * Guarda o nome do tipo
     */
    protected String typeName;

    /**
     * Constructor
     */
    protected Type()
    {
    }

    /**
     * Constructor
     * @param typeName Nome do tipo
     */
    protected Type(String typeName)
    {
        this.typeName = typeName;
    }

    /**
     * @returns O nome do tipo
     */
    public String getName()
    {
        return typeName;
    }

    /**
     * Define o nome do tipo
     * @param newName O nome do tipo
     */
    public void setName(String newName)
    {
        typeName = newName;
    }
}

