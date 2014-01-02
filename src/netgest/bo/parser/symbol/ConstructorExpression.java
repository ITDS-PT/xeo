/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;
import java.util.ArrayList;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class ConstructorExpression implements ExpressionSymbol {

  /**
   * Constructor
   */
  public ConstructorExpression() {
  }

  public void setExpression(ExpressionSymbol exp){
    this.exp = exp;
  }

  public void setParameters(ExpressionSymbol exp){
    this.parameters = exp;
  }

  public void setArrayInit(ExpressionSymbol exp){
    arrBool = true;
    this.arrayInit = exp;
  }

  public void setArray(ExpressionSymbol type){
    arrBool = true;
    this.arrExp = type;
  }

  public boolean isArray(){
    return arrBool;
  }

  public ExpressionSymbol getArray(){
    return arrExp;
  }

  public ExpressionSymbol getExpression(){
    return exp;
  }

  public ExpressionSymbol getParameters(){
    return parameters;
  }

  public ExpressionSymbol getArrayInit(){
    return arrayInit;
  }

  public String toJAVA(){
    StringBuffer sb = new StringBuffer();

    if(exp != null){
        sb.append("new ").append(exp.toJAVA());
        if(arrBool){
            if(arrExp == null)
                sb.append("[] ");
            else
                sb.append("[").append(arrExp.toJAVA()).append("] ");
            if(arrayInit != null){
                sb.append("{");
                sb.append(arrayInit.toJAVA());
                sb.append("} ");
            }
        }
        else
            if(parameters == null)
                sb.append("() ");
            else
                sb.append("(").append(parameters.toJAVA()).append(")");
        return sb.toString();
    }
    return "";
  }

  private ExpressionSymbol exp;
  private ExpressionSymbol arrExp;
  private ExpressionSymbol parameters;
  private ExpressionSymbol arrayInit = null;
  private boolean arrBool = false;
}

