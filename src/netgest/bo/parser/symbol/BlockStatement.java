/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

import java.util.ArrayList;

/**
 * Representa um bloco de instruções
 * <P>
 * @author Francisco Câmara
 */
public class BlockStatement implements StatementSymbol
{
    /**
     * Guarda as instruções do bloco
     */
    private ArrayList statements;
    private String code = null;

    /**
     * Constructor
     */
    public BlockStatement()
    {
    }

    /**
     * Constructor
     * @param statements Instruções do bloco
     */
    public BlockStatement(ArrayList statements)
    {
        this.statements = statements;
    }
    /**
     * Constructor
     * @param code Código
     */
    public BlockStatement(String code)
    {
        this.code = code;
    }

    /**
     * Define as instruções do bloco
     * @param newStatements Instruções do bloco
     */
    public void setStatements(ArrayList newStatements)
    {
        statements = newStatements;
    }

    /**
     * @returns As instruções do bloco
     */
    public ArrayList getStatements()
    {
        return statements;
    }

    /**
     * @returns Retorna a representação JAVA desta instrução
     */
    public String toJAVA()
    {
        String retorno = "{\n";

        if (statements != null && statements.size() > 0)
        {
            for (int i = 0; i < statements.size(); i++) {
                retorno += ((Symbol)statements.get(i)).toJAVA() + "\n";
            }
        }
        else if(code != null && code.length() > 0)
        {
            retorno += code;
        }
        retorno += "}";

        return retorno;
    }
}

