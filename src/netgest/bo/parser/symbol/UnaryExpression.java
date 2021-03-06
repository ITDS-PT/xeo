/*Enconding=UTF-8*/
/* Generated by Together */

package netgest.bo.parser.symbol;

public class UnaryExpression implements ExpressionSymbol {
    public ExpressionSymbol getExpression(){ return expression; }

    public void setExpression(ExpressionSymbol expression){ this.expression = expression; }

    public int getOperator(){
            return operator;
        }

    public void setOperator(int operator){
            this.operator = operator;
        }

    
    public String toJAVA(){
        StringBuffer sb = new StringBuffer();
        if(operator == JavaTokenTypes.UNARY_MINUS){
            sb.append("-");
            sb.append(expression.toJAVA());
        }
        else if(operator == JavaTokenTypes.UNARY_PLUS){
            sb.append("+");
            sb.append(expression.toJAVA());
        }
        else if(operator == JavaTokenTypes.LNOT){
            sb.append("!");
            sb.append(expression.toJAVA());
        }
        else if(operator == JavaTokenTypes.DEC)
            sb.append("--").append(expression.toJAVA());

        else if(operator == JavaTokenTypes.POST_DEC)
            sb.append(expression.toJAVA()).append("--");

        else if(operator == JavaTokenTypes.INC)
            sb.append("++").append(expression.toJAVA());

        else if(operator == JavaTokenTypes.POST_INC)
            sb.append(expression.toJAVA()).append("++");

        return sb.toString();
    }

    private ExpressionSymbol expression;
    private int operator;
}
