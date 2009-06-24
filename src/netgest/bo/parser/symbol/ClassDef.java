/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class ClassDef implements ClauseDeclarationSymbol {
    ClauseDeclarationSymbol implementsDeclaration;
    ClauseDeclarationSymbol extendsDeclaration;
    ClauseDeclarationSymbol modifiers;
    ExpressionSymbol name;
    StatementSymbol code;

  /**
   * Constructor
   */
  public ClassDef() {
  }

  public void setImplements(ClauseDeclarationSymbol impl){
    implementsDeclaration = impl;
  }

  public ClauseDeclarationSymbol getImplements(){
    return implementsDeclaration;
  }

  public void setExtends(ClauseDeclarationSymbol ext){
    extendsDeclaration = ext;
  }

  public ClauseDeclarationSymbol getExtends(){
    return extendsDeclaration;
  }

  public void setModifiers(ClauseDeclarationSymbol mod){
    modifiers = mod;
  }

  public ClauseDeclarationSymbol getModifiers(){
    return modifiers;
  }

  public void setName(ExpressionSymbol name){
    this.name = name;
  }

  public ExpressionSymbol getName(){
    return name;
  }

  public void setCode(StatementSymbol code){
    this.code = code;
  }

  public StatementSymbol getCode(){
    return code;
  }

  public String toJAVA(){
    StringBuffer sb = new StringBuffer();
    if(modifiers != null)
        sb.append(modifiers.toJAVA());
    sb.append("class ").append(name.toJAVA()).append(" ");
    if(extendsDeclaration != null)
        sb.append(extendsDeclaration.toJAVA()).append(" ");
    if(implementsDeclaration != null)
        sb.append(implementsDeclaration.toJAVA());
    sb.append(code.toJAVA());
    return sb.toString();

  }
}

 