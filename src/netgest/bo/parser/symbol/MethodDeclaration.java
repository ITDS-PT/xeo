/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;
import java.util.ArrayList;
import netgest.bo.parser.symbol.*;
import netgest.bo.parser.types.*;
/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class MethodDeclaration implements ClauseDeclarationSymbol {

    private ArrayList throwsArray = new ArrayList();
    private ClauseDeclarationSymbol modifiers;
    private TypeClause tipo;
    private String name;
    private Parameters parameters;
    private StatementSymbol code;
    private String codeJava;
    private boolean isCodeJava = false;

  /**
   * Constructor
   */
  public MethodDeclaration() {
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

  public void setType(TypeClause tipo){
    this.tipo = tipo;
  }

  public TypeClause getType(){
    return tipo;
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

  public void setCode(String code){
    this.codeJava = code;
    this.isCodeJava = true;
  }

  public boolean isCodeJava(){
    return this.isCodeJava;
  }
  
  public StatementSymbol getCode(){
    return code;
  }

  public String toJAVA(){
    StringBuffer sb = new StringBuffer();
    if(modifiers != null)
        sb.append(modifiers.toJAVA()).append(" ");
    sb.append(tipo.toJAVA()).append(" ");
    sb.append(name).append("(");
    if(parameters != null)
        sb.append(parameters.toJAVA());

    sb.append(") ");
    if(throwsArray.size() != 0){
        sb.append("throws ");
        for(int i = 0; i < throwsArray.size(); i++)
            sb.append(((ExpressionSymbol)throwsArray.get(i)).toJAVA());
        sb.append(" ");
    }
    if(isCodeJava)
    {
        sb.append("{");
        sb.append(codeJava);
        sb.append("}");
    }
    else
    {
        sb.append(code.toJAVA());
    }
    return sb.toString();

  }
}

 