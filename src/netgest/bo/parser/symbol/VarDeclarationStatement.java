/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

import java.util.ArrayList;

/**
 * Representa uma declaração de variável
 * <P>
 * @author Francisco Câmara
 */
public class VarDeclarationStatement implements StatementSymbol
{
    /**
     * Guarda os modificadores da variável
     */
    private ModifierClause modifiers;

    /**
     * Guarda o tipo da variavel
     */
    private TypeClause type;

    /**
     * Guarda o nome da variável
     */
    private String name;

    /**
     * Guarda a expressão de inicialização da variavel
     */
    private ExpressionSymbol init;

    /**
     * Constructor
     */
    public VarDeclarationStatement()
    {
    }

    /**
     * @returns A inicialização da variável
     */
    public ExpressionSymbol getInit()
    {
        return init;
    }

    /**
     * Define a inicialização da variável
     * @param newInit A inicialização da variável
     */
    public void setInit(ExpressionSymbol newInit)
    {
        init = newInit;
    }

    /**
     * @returns O nome da variável
     */
    public String getName()
    {
        return name;
    }

    /**
     * Define o nome da variável
     * @param newName O nome da variável
     */
    public void setName(String newName)
    {
        name = newName;
    }

    /**
     * @returns O tipo da variável
     */
    public TypeClause getType()
    {
        return type;
    }

    /**
     * Define o tipo da variável
     * @param newType O tipo da variável
     */
    public void setType(TypeClause newType)
    {
        type = newType;
    }

    /**
     * Associa os modificadores à variável
     * @param newModifiers Os modificadores da variável
     */
    public void setModifier(ModifierClause newModifiers)
    {
        modifiers = newModifiers;
    }

    /**
     * @returns Os modificadores da variável
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

        retorno += type.toJAVA() + " " + name;

        if (init != null)
            retorno += init.toJAVA();

        retorno += ";";

        return retorno;
    }
}

