/*Enconding=UTF-8*/
package netgest.bo.parser.symbol;

/**
 * Representa uma instrução IF
 * <P>
 * @author Francisco Câmara
 */
public class IfStatement implements StatementSymbol {
    /**
     * Guarda a expressão da guarda
     */
    private ExpressionSymbol condition;

    /**
     * Guarda as instruções do ramo "then"
     */
    private StatementSymbol thenStatement;

    /**
     * Guarda as instruções do ramo "else"
     */
    private StatementSymbol elseStatement;

    /**
     * Constructor
     */
    public IfStatement()
    {
    }

    /**
     * Constructor
     * @param condition Guarda do if
     * @param thenStatement Instruções do ramo then
     */
    public IfStatement(ExpressionSymbol condition, StatementSymbol thenStatement)
    {
        this.condition = condition;
        this.thenStatement = thenStatement;
    }

    /**
     * Constructor
     * @param condition Guarda do if
     * @param thenStatement Instruções do ramo then
     * @param elseStatement Instruções do ramo else
     */
    public IfStatement(ExpressionSymbol condition, StatementSymbol thenStatement,
        StatementSymbol elseStatement)
    {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    /**
     * @returns As instruções do ramo else, null caso não existam
     */
    public StatementSymbol getElseStatement()
    {
        return elseStatement;
    }

    /**
     * Define as instruções do ramo else
     * @param newElseStatement Instruções do ramo else
     */
    public void setElseStatement(StatementSymbol newElseStatement) {
        elseStatement = newElseStatement;
    }

    /**
     * @returns A Expressão da guarda
     */
    public ExpressionSymbol getCondition() {
        return condition;
    }

    /**
     * Define a expressão da guarda
     * @param newCondition Guarda do if
     */
    public void setCondition(ExpressionSymbol newCondition) {
        condition = newCondition;
    }

    /**
     * @returns As instruções do ramo then
     */
    public StatementSymbol getThenStatement() {
        return thenStatement;
    }

    /**
     * Define as instruções do ramo then
     * @param newThenStatement Instruções do ramo then
     */
    public void setThenStatement(StatementSymbol newThenStatement) {
        thenStatement = newThenStatement;
    }

    /**
     * @returns Retorna a representação JAVA desta instrução
     */
    public String toJAVA()
    {
        if (elseStatement == null)
        {
            if(thenStatement instanceof BlockStatement)
                return "if (" + condition.toJAVA() + ") \n" + thenStatement.toJAVA();
            else
                return "if (" + condition.toJAVA() + ") \n{\n" + thenStatement.toJAVA() + "\n}\n";
        }
        else
        {
            StringBuffer sb = new StringBuffer();
            sb.append("if (" + condition.toJAVA() + ") \n");
            if(thenStatement instanceof BlockStatement)
                sb.append(thenStatement.toJAVA());
            else{
                sb.append("{\n");
                sb.append(thenStatement.toJAVA());
                sb.append("\n}\n");
            }
            sb.append(" else ");
            if(elseStatement instanceof BlockStatement)
            {
                sb.append(elseStatement.toJAVA());
            }
            else{
                sb.append("{\n");
                sb.append(elseStatement.toJAVA());
                sb.append("\n}\n");
            }    
//            return "if (" + condition.toJAVA() + ") " + thenStatement.toJAVA()
//                + " else " + elseStatement.toJAVA();
            return sb.toString();
        }
    }
}

