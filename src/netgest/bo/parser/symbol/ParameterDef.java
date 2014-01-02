/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

import java.util.ArrayList;
import netgest.bo.parser.symbol.*;

/**
 * Representa uma declaração de um parâmetro
 * @author Francisco Câmara
 */
public class ParameterDef implements ClauseDeclarationSymbol
{
    /**
     * Guarda os modificadores do parâmetro
     */
    private ModifierClause modifiers;

    /**
     * Guarda o tipo do parâmetro
     */
    private TypeClause type;

    /**
     * Guarda o nome do parâmetro
     */
    private String name;

    /**
     * Constructor
     */
    public ParameterDef()
    {
    }

    /**
     * @returns O nome do parâmetro
     */
    public String getName()
    {
        return name;
    }

    /**
     * Define o nome do parâmetro
     * @param newName O nome do parâmetro
     */
    public void setName(String newName)
    {
        name = newName;
    }

    /**
     * @returns O tipo do parâmetro
     */
    public TypeClause getType()
    {
        return type;
    }

    /**
     * Define o tipo do parâmetro
     * @param newType O tipo do parâmetro
     */
    public void setType(TypeClause newType)
    {
        type = newType;
    }

    /**
     * Associa os modificadores ao parâmetro
     * @param newModifiers Os modificadores do parâmetro
     */
    public void setModifier(ModifierClause newModifiers)
    {
        modifiers = newModifiers;
    }

    /**
     * @returns Os modificadores do parâmetro
     */
    public ModifierClause getModifiers()
    {
        return modifiers;
    }

    /**
     * @returns A representação JAVA desta instrução
     */
    public String toJAVA()
    {
        String retorno = "";

        if (modifiers != null)
            retorno = modifiers.toJAVA();

        return retorno + type.toJAVA() + " " + name;
    }
}

