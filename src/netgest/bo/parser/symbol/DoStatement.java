/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

/**
 * Representa uma instrução DO
 * <P>
 * @author Francisco Câmara
 */
public class DoStatement implements StatementSymbol
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
    public DoStatement()
    {
    }

    /**
     * Constructor
     * @param condition Guarda do ciclo
     * @param body Corpo do ciclo
     */
    public DoStatement(ExpressionSymbol condition, StatementSymbol body)
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
        return "do " + body.toJAVA() + " while (" + condition.toJAVA() + ");";
    }
}

