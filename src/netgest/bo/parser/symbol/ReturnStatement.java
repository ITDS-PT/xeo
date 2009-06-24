/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

/**
 * Representa uma instrução de RETURN
 * <P>
 * @author Francisco Câmara
 */
public class ReturnStatement implements StatementSymbol{
    /**
     * Guarda a expressão do retorno
     */
    private ExpressionSymbol returnExp;

    /**
     * Constructor
     */
    public ReturnStatement()
    {
    }

    /**
     * Constructor
     * @param statements Instruções do bloco
     */
    public ReturnStatement(ExpressionSymbol returnExp)
    {
        this.returnExp = returnExp;
    }

    /**
     * @returns a expressão do retorno
     */
    public ExpressionSymbol getReturnExp()
    {
        return returnExp;
    }

    /**
     * Define a expressão do retorno
     * @param newReturnExp A expressão do retorno
     */
    public void setReturnExp(ExpressionSymbol newReturnExp)
    {
        returnExp = newReturnExp;
    }

    /**
     * @returns Retorna a representação JAVA desta instrução
     */
    public String toJAVA()
    {
        String retorno = "return";

        if (returnExp != null)
            retorno += " " + returnExp.toJAVA();

        retorno += ";";

        return retorno;
    }
}

