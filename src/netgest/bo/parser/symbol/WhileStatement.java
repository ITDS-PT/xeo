/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

/**
 * Representa uma instrução WHILE
 * <P>
 * @author Francisco Câmara
 */
public class WhileStatement implements StatementSymbol
{
    /**
     * Guarda a expressão da guarda
     */
    private ExpressionSymbol condition;

    /**
     * Guarda o corpo do ciclo
     */
    private StatementSymbol body;

    /**
     * Constructor
     */
    public WhileStatement()
    {
    }

    /**
     * Constructor
     * @param condition Guarda do ciclo
     * @param body Corpo do ciclo
     */
    public WhileStatement(ExpressionSymbol condition, StatementSymbol body)
    {
        this.condition = condition;
        this.body = body;
    }

    /**
     * @returns A Expressão da guarda
     */
    public ExpressionSymbol getCondition() {
        return condition;
    }

    /**
     * Define a expressão da guarda
     * @param newCondition Guarda do ciclo
     */
    public void setCondition(ExpressionSymbol newCondition) {
        condition = newCondition;
    }

    /**
     * @returns O corpo do ciclo
     */
    public StatementSymbol getBody() {
        return body;
    }

    /**
     * Define o corpo do ciclo
     * @param newBody O corpo do ciclo
     */
    public void setBody(StatementSymbol newBody) {
        body = newBody;
    }

    /**
     * @returns Retorna a representação JAVA desta instrução
     */
    public String toJAVA()
    {
        String retorno = "while (" + condition.toJAVA() + ")\n";

        if (body != null)
            retorno += " " + body.toJAVA();
        else
            retorno += ";";

        return retorno;
    }
}

