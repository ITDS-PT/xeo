/*Enconding=UTF-8*/


package netgest.bo.parser.symbol;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class StringLiteral implements LiteralExpression {

  /**
   * Constructor
   */
  public StringLiteral(String value){
    this.value = value;
  }

  public void setValue(String value){
    this.value = value;
  }

  public String getValue(){
    return value;
  }

  public String toJAVA(){
    return "\"" + value + "\"";
  }

  private String value;
}

 