/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class IndexOpExStatement implements ExpressionSymbol
{
    private ExpressionSymbol exp;
    private ExpressionSymbol var;

   public void setExpression(ExpressionSymbol exp){
        this.exp = exp;
   }

   public ExpressionSymbol getExpression(){
     return exp;
   }

   public void setVar(ExpressionSymbol exp){
        this.var = exp;
   }

   public ExpressionSymbol getVar(){
     return var;
   }

   public String toJAVA()
   {
        StringBuffer retorno = new StringBuffer();

        retorno.append(var.toJAVA());
        retorno.append("[");
        retorno.append(exp.toJAVA());
        retorno.append("]");

        return retorno.toString();
    }
}

 