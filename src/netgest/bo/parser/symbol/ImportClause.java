/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class ImportClause implements ClauseDeclarationSymbol {
    ExpressionSymbol e;
  /**
   * Constructor
   */
  public ImportClause() {
  }

  public void setImport(ExpressionSymbol e){
    this.e = e;
  }

  public ExpressionSymbol getImport(){
    return e;
  }

  public String toJAVA(){
    if(e != null)
        return "import " + e.toJAVA() + ";";
    return "";
  }
}

 