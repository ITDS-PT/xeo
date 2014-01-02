/*Enconding=UTF-8*/

// Copyright (c) 2001 IIES
package netgest.bo.parser.symbol;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class PackageClause implements ClauseDeclarationSymbol {
    private ExpressionSymbol e;
  /**
   * Constructor
   */
  public PackageClause() {
  }

  public void setPackage(ExpressionSymbol e){
    this.e = e;
  }

  public ExpressionSymbol getPackage(){return e;}

  public String toJAVA(){
    if(e != null)
        return "package " + e.toJAVA() + ";";
    return "";
  }

}

 