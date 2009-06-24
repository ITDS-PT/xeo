/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

import java.util.ArrayList;

/**
 * Representa uma instrução TRY
 * <P>
 * @author Francisco Câmara
 */
public class TryStatement implements StatementSymbol
{
    /**
     * Guarda as instruções do corpo do try
     */
    private StatementSymbol body;

    /**
     * Guarda as clausulas CATCH correspondentes ao bloco try
     */
    private ArrayList catchClauses;

    /**
     * Constructor
     */
    public TryStatement()
    {
        catchClauses = new ArrayList();
    }

    /**
     * @returns O corpo do Try
     */
    public StatementSymbol getBody()
    {
        return body;
    }

    /**
     * Define o corpo do Try
     * @param newBody O corpo do Try
     */
    public void setBody(StatementSymbol newBody)
    {
        body = newBody;
    }

    /**
     * @returns As cláusulas Catch, null caso não existam
     */
    public ArrayList getCatchClauses()
    {
        return catchClauses;
    }

    /**
     * Define as cláusulas catch do Try
     * @param newCatchClauses As cláusulas catch do Try
     */
    public void setCatchClauses(ArrayList newCatchClauses)
    {
        catchClauses = newCatchClauses;
    }

    /**
     * Adiciona uma cláusula catch ao Try
     * @param catchClause A cláusula a adicionar
     */
    public void addCatchClause(CatchStatement catchClause)
    {
        catchClauses.add(catchClause);
    }

    /**
     * @returns A representação JAVA desta instrução
     */
    public String toJAVA()
    {
        String retorno = "try ";

        if (body != null)
            retorno += body.toJAVA();
        else
            retorno += "{}";

        for(int i=0; i < catchClauses.size(); i++)
            retorno += "\n" + ((CatchStatement)catchClauses.get(i)).toJAVA();

        return retorno;
    }
}

