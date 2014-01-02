/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

import netgest.bo.parser.types.ClassType;
import netgest.bo.parser.symbol.*;

/**
 * Representa uma instrução CATCH
 * <P>
 * @author Francisco Câmara
 */
public class CatchStatement implements StatementSymbol
{
    /**
     * Guarda o parâmetro do catch
     */
    private ParameterDef param;

    /**
     * Guarda as instruções do corpo do catch
     */
    private StatementSymbol body;

    /**
     * Constructor
     */
    public CatchStatement()
    {
    }

    /**
     * @returns O corpo do Catch
     */
    public StatementSymbol getBody()
    {
        return body;
    }

    /**
     * Define o corpo do Catch
     * @param newBody O corpo do Catch
     */
    public void setBody(StatementSymbol newBody)
    {
        body = newBody;
    }

    /**
     * @returns O parâmetro do Catch
     */
    public ParameterDef getParam()
    {
        return param;
    }

    /**
     * Define o parâmetro do Catch
     * @param newParam O parâmetro do Catch
     */
    public void setParam(ParameterDef newParam)
    {
        param = newParam;
    }

    /**
     * @returns A representação JAVA desta instrução
     */
    public String toJAVA()
    {
        String retorno = "catch (" + param.toJAVA() + ")";

        if (body != null)
            retorno += body.toJAVA();
        else
            retorno += "{}";

        return retorno;
    }
}

