/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

import java.util.ArrayList;

/**
 * Representa uma instrução FINALLY
 * <P>
 * @author Francisco Câmara
 */
public class FinallyStatement implements StatementSymbol
{
    /**
     * Guarda a clausula try correspondente
     */
    private TryStatement tryStatement;

    /**
     * Guarda as instruções do corpo do finally
     */
    private StatementSymbol body;

    /**
     * Constructor
     */
    public FinallyStatement()
    {
    }

    /**
     * @returns O corpo Finally
     */
    public StatementSymbol getBody()
    {
        return body;
    }

    /**
     * Define o corpo do Finally
     * @param newBody O corpo do Finally
     */
    public void setBody(StatementSymbol newBody)
    {
        body = newBody;
    }

    /**
     * @returns A cláusula try correspondente a esta instrução
     */
    public TryStatement getTryStatement()
    {
        return tryStatement;
    }

    /**
     * Define a cláusula try correspondente a esta instrução
     * @param newTryStatement A cláusula try correspondente a esta instrução
     */
    public void setTryStatement(TryStatement newTryStatement)
    {
        tryStatement = newTryStatement;
    }

    /**
     * @returns A representação JAVA desta instrução
     */
    public String toJAVA()
    {
        String retorno = tryStatement.toJAVA() + "\nfinally ";

        if (body != null)
            retorno += body.toJAVA();
        else
            retorno += "{}";

        return retorno;
    }
}

