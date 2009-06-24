/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.modifiers;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class Modifier{

 /**
     * Guarda o nome do tipo
     */
    protected String modifierName;

    /**
     * Constructor
     */
    protected Modifier()
    {
    }

    /**
     * Constructor
     * @param typeName Nome do tipo
     */
    protected Modifier(String modifierName)
    {
        this.modifierName = modifierName;
    }

    /**
     * @returns O nome do tipo
     */
    public String getName()
    {
        return modifierName;
    }

    /**
     * Define o nome do tipo
     * @param newName O nome do tipo
     */
    public void setName(String newName)
    {
        modifierName = newName;
    }
}

 