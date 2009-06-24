/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

import netgest.bo.parser.symbol.ExpressionSymbol;
import netgest.bo.parser.symbol.StatementSymbol;

/**
 * Representa uma instrução FOR
 * <P>
 * @author Francisco Câmara
 */
public class ForStatement implements StatementSymbol{
    /**
     * Guarda as instruções de inicialização
     */
    private StatementSymbol init;

    /**
     * Guarda a guarda do ciclo
     */
    private ExpressionSymbol condition;

    /**
     * Guarda o iterador do ciclo
     */
    private ExpressionSymbol increment;

    /**
     * Guarda as instruções do corpo do ciclo
     */
    private StatementSymbol body;

    /**
     * Constructor
     */
    public ForStatement()
    {
    }

    /**
     * @returns O corpo do ciclo
     */
    public StatementSymbol getBody() {
        return body;
    }

    /**
     * Define o corpo do ciclo
     * @param newBody Corpo do ciclo
     */
    public void setBody(StatementSymbol newBody) {
        body = newBody;
    }

    /**
     * @returns A guarda do ciclo
     */
    public ExpressionSymbol getCondition() {
        return condition;
    }

    /**
     * Define a guarda do ciclo
     * @param newCondition Guarda do ciclo
     */
    public void setCondition(ExpressionSymbol newCondition) {
        condition = newCondition;
    }

    /**
     * @returns O iterador do ciclo
     */
    public ExpressionSymbol getIterator() {
        return increment;
    }

    /**
     * Define o iterador do ciclo
     * @param newIterator Iterator  do ciclo
     */
    public void setIterator(ExpressionSymbol newIterator) {
        increment = newIterator;
    }

    /**
     * @returns A inicialização do ciclo
     */
    public StatementSymbol getInit() {
        return init;
    }

    /**
     * Define a inicialização do ciclo
     * @param newInit Inicialização do ciclo
     */
    public void setInit(StatementSymbol newInit) {
        init = newInit;
    }

    /**
     * @returns A representação JAVA desta instrução
     */
    public String toJAVA()
    {
        String retorno = "for (";

        if (init != null)
            retorno += init.toJAVA() + " ";
        else
            retorno += "; ";

        if (condition != null)
            retorno += condition.toJAVA();
        retorno += "; ";

        if (increment != null)
            retorno += increment.toJAVA();

        retorno += ") ";

        if (body != null)
            retorno += body.toJAVA();
        else
            retorno += ";";

        return retorno;
    }
}

