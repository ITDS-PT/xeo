/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

import java.util.ArrayList;

/**
 * Representa a inicialização de uma lista de declarações de variáveis
 * <P>
 * @author Francisco Câmara
 */
public class VarDeclarationListStatement implements StatementSymbol
{
    /**
     * Guarda a lista de variáveis
     */
    private ArrayList list;

    /**
     * Constructor
     */
    public VarDeclarationListStatement()
    {
        list = new ArrayList();
    }

    /**
     * @returns A lista de variáveis
     */
    public ArrayList getList()
    {
        return list;
    }

    /**
     * Define a lista de variáveis
     * @var newList A lista de variáveis
     */
    public void setList(ArrayList newList)
    {
        list = newList;
    }

    /**
     * Adiciona um variável à lista
     * @var var A variável a adicionar
     */
    public void addVar(VarDeclarationStatement var)
    {
        list.add(var);
    }

    /**
     * @returns A representação JAVA da lista
     */
    public String toJAVA()
    {
        VarDeclarationStatement var = ((VarDeclarationStatement)list.get(0));

        String retorno = var.getType().toJAVA() + " " + var.getName();

        if (var.getInit() != null)
            retorno += " = " + var.getInit().toJAVA();

        for (int i = 1; i < list.size(); i++) {
            var = ((VarDeclarationStatement)list.get(i));
            retorno += ", " + var.getName();

            if (var.getInit() != null)
                retorno += " = " + var.getInit().toJAVA();
        }

        return retorno + ";";
    }
}

