/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;
import java.util.ArrayList;

/**
 * Representa uma lista de declarações de parâmetros
 * <P>
 * @author Francisco Câmara
 */
public class Parameters implements ClauseDeclarationSymbol
{
    /**
     * Guarda a lista de parâmetros
     */
    private ArrayList list;

    /**
     * Constructor
     */
    public Parameters()
    {
        list = new ArrayList();
    }

    /**
     * @returns A lista de parâmetros
     */
    public ArrayList getList()
    {
        return list;
    }

    /**
     * Define a lista de parâmetros
     * @param newList A lista de parâmetros
     */
    public void setList(ArrayList newList)
    {
        list = newList;
    }

    /**
     * Adiciona um parâmetro à lista
     * @param para O parâmetro a adicionar
     */
    public void addParam(ParameterDef param)
    {
        list.add(param);
    }

    /**
     * @returns A representação JAVA da lista
     */
    public String toJAVA()
    {
        String retorno = ((ParameterDef)list.get(0)).toJAVA();

        for (int i = 1; i < list.size(); i++)
            retorno += ", " + ((ParameterDef)list.get(i)).toJAVA();

        return retorno;
    }
}

