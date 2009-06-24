/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

/**
 * Representa uma instrução BREAK
 * <P>
 * @author Francisco Câmara
 */
public class BreakStatement implements StatementSymbol
{
    /**
     * Guarda a label do break
     */
    private String label;

    /**
     * Constructor
     */
    public BreakStatement()
    {
    }

    /**
     * Constructor
     * @param label Label do break
     */
    public BreakStatement(String label)
    {
        this.label = label;
    }

    /**
     * @returns A label do break
     */
    public String getLabel() {
        return label;
    }

    /**
     * Define a label do break
     * @param newLabel A label do break
     */
    public void setLabel(String newLabel) {
        label = newLabel;
    }

    /**
     * @returns Retorna a representação JAVA desta instrução
     */
    public String toJAVA()
    {
        String retorno = "break";

        if (label != null)
            retorno += " " + label;

        return retorno + ";";
    }
}

