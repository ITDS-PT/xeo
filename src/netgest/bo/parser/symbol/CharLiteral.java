/*Enconding=UTF-8*/

package netgest.bo.parser.symbol;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class CharLiteral implements LiteralExpression {

  /**
   * Constructor
   */
  public CharLiteral(){
  }

  public void setValue(char value){
    this.value = String.valueOf(value);
  }

  public void setValue(String value){
    this.value = value;
  }

  public String getValue(){
    return value;
  }

  public String toJAVA(){
    return String.valueOf(value);
  }

  private String value;
}

