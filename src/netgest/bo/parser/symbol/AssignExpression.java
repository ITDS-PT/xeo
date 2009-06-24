/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;
/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class AssignExpression implements ExpressionSymbol {
    private ExpressionSymbol exp;
  /**
   * Constructor
   */
  public AssignExpression() {
  }

  public void setExpression(ExpressionSymbol expression){ this.exp = expression; }

  public ExpressionSymbol getExpression(){return exp;}

  public String toJAVA(){
        StringBuffer sb = new StringBuffer();
        if(exp != null){
            sb.append(" = ").append(exp.toJAVA());
            return sb.toString();
        }
        return "";
    }
}

