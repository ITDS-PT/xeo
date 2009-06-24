/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

/**
 * Representa uma statement wrapper de uma expressão
 * <P>
 * @author Francisco Câmara
 */
public class ExpressionStatement implements StatementSymbol
{
    /**
     * Guarda a expressão
     */
    private ExpressionSymbol expr;

    /**
     * Constructor
     */
    public ExpressionStatement()
    {
    }

    /**
     * Constructor
     * @param expr A Expressão
     */
    public ExpressionStatement(ExpressionSymbol expr)
    {
        this.expr = expr;
    }

    /**
     * @returns A expressão
     */
    public Symbol getExpression()
    {
        return expr;
    }

    /**
     * Define a expressão
     * @param newExpre A expressão
     */
    public void setExpression(ExpressionSymbol newExpr) {
        expr = newExpr;
    }

    /**
     * @returns Retorna a representação JAVA desta instrução
     */
    public String toJAVA()
    {
        return expr.toJAVA() + ";";
    }
}

