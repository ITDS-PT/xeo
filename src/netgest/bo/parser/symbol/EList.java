/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

import java.util.ArrayList;

/**
 * Representa uma lista de expressões
 * <P>
 * @author Francisco Câmara
 */
public class EList implements ExpressionSymbol
{
    /**
     * Guarda a lista de expressões
     */
    private ArrayList list;

    /**
     * Constructor
     */
    public EList()
    {
        list = new ArrayList();
    }

    /**
     * @returns A lista de expressões
     */
    public ArrayList getList()
    {
        return list;
    }

    /**
     * Define a lista de expressões
     * @param newList A lista de expressões
     */
    public void setList(ArrayList newList)
    {
        list = newList;
    }

    /**
     * Adiciona uma expressão à lista
     * @param A expressão a adicionar
     */
    public void addExpr(ExpressionSymbol expr)
    {
        list.add(expr);
    }

    /**
     * @returns A representação JAVA da lista
     */
    public String toJAVA()
    {
        String retorno = ((ExpressionSymbol)list.get(0)).toJAVA();

        for (int i = 1; i < list.size(); i++)
            retorno += ", " + ((ExpressionSymbol)list.get(i)).toJAVA();

        return retorno;
    }
}

