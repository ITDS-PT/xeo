/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;
import java.util.ArrayList;
/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class ImplementsClause implements ClauseDeclarationSymbol {
    private ArrayList classes = new ArrayList();
  /**
   * Constructor
   */
  public ImplementsClause() {
  }

  public void setImplement(ExpressionSymbol e){
    classes.add(e);
  }

  public ArrayList getImplements(){
    return classes;
  }

  public int getNumImplements(){
    return classes.size();
  }

  public ExpressionSymbol getImplement(int i){
    return (ExpressionSymbol)classes.get(i);
  }

  public String toJAVA(){
    StringBuffer sb = new StringBuffer();
    if(classes.size() != 0){
        sb.append("implements ");
        for(int i = 0; i < classes.size(); i ++){
            if(i + 1 < classes.size())
                sb.append(((ExpressionSymbol)classes.get(i)).toJAVA()).append(", ");
            else
                sb.append(((ExpressionSymbol)classes.get(i)).toJAVA());
        }
    }
    if(sb.length() == 0) return "";
    return sb.toString();
  }
}

 