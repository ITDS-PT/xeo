/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;
import java.util.ArrayList;
/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class ExtendsClause implements ClauseDeclarationSymbol {
    private ArrayList classes = new ArrayList();
  /**
   * Constructor
   */
  public void ExtendsClause() {
  }

  public void setExtend(ExpressionSymbol e){
    classes.add(e);
  }

  public ArrayList getExtends(){
    return classes;
  }

  public int getNumExtends(){
    return classes.size();
  }

  public ExpressionSymbol getExtend(int i){
    return (ExpressionSymbol)classes.get(i);
  }

  public String toJAVA(){
    StringBuffer sb = new StringBuffer();
    if(classes.size() != 0){
        sb.append("extends ");
        for(int i = 0; i < classes.size(); i ++){
            if(i + 1 < classes.size())
                sb.append(((ExpressionSymbol)classes.get(i)).toJAVA()).append(", ");
            else
                sb.append(((ExpressionSymbol)classes.get(i)).toJAVA());
            return sb.toString();
        }
    }
    return "";

  }
}


