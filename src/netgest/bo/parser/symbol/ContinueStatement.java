/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

/**
 * Representa uma instrução CONTINUE
 * <P>
 * @author Francisco Câmara
 */
public class ContinueStatement implements StatementSymbol
{
    /**
     * Guarda a label do continue
     */
    private String label;

    /**
     * Constructor
     */
    public ContinueStatement()
    {
    }

    /**
     * Constructor
     * @param label Label do continue
     */
    public ContinueStatement(String label)
    {
        this.label = label;
    }

    /**
     * @returns A label do continue
     */
    public String getLabel() {
        return label;
    }

    /**
     * Define a label do continue
     * @param newLabel A label do continue
     */
    public void setLabel(String newLabel) {
        label = newLabel;
    }

    /**
     * @returns Retorna a representação JAVA desta instrução
     */
    public String toJAVA()
    {
        String retorno = "continue";

        if (label != null)
            retorno += " " + label;

        return retorno + ";";
    }
}

