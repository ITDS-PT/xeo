/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class StaticDef implements ClauseDeclarationSymbol {

  private StatementSymbol code;

  public void setCode(StatementSymbol code){
    this.code = code;
  }

  public StatementSymbol getCode(){
    return code;
  }

  public String toJAVA(){
    StringBuffer sb = new StringBuffer();

    sb.append("static");
    sb.append(code.toJAVA());
    return sb.toString();

  }
}

 