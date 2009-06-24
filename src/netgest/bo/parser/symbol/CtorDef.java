/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;
import java.util.ArrayList;
/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class CtorDef implements ClauseDeclarationSymbol {
    private ArrayList throwsArray = new ArrayList();
    private ClauseDeclarationSymbol modifiers;
    private String name;
    private Parameters parameters;
    private StatementSymbol code;

  /**
   * Constructor
   */
  public CtorDef() {
  }

  public void setThrow(ExpressionSymbol e){
    throwsArray.add(e);
  }

  public ArrayList getThrows(ExpressionSymbol e){
    return throwsArray;
  }

  public ExpressionSymbol getThrow(int i){
    return (ExpressionSymbol)throwsArray.get(i);
  }

  public void setModifiers(ClauseDeclarationSymbol mc){
    modifiers = mc;
  }

  public ClauseDeclarationSymbol getModifiers(){
    return modifiers;
  }

  public void setName(String name){
    this.name = name;
  }

  public String getName(){
    return name;
  }

  public void setParameters(Parameters parameters){
    this.parameters = parameters;
  }

  public Parameters getParameters(){
    return parameters;
  }

  public void setCode(StatementSymbol code){
    this.code = code;
  }

  public String toJAVA(){
    StringBuffer sb = new StringBuffer();
    if(modifiers != null)
        sb.append(modifiers.toJAVA()).append(" ");
    sb.append(name).append("(");
    if(parameters != null)
        sb.append(parameters.toJAVA());

    sb.append(")");
    sb.append(code.toJAVA());
    return sb.toString();

  }

}

 