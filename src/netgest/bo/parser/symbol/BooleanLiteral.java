/*Enconding=UTF-8*/

package netgest.bo.parser.symbol;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class BooleanLiteral implements LiteralExpression {

  /**
   * Constructor
   */
  public BooleanLiteral() {
  }

  public void setValue(boolean value){
    this.value = value;
  }

  public void setValue(String value){
    this.value = value.equals("true");
  }

  public boolean getBoolValue(){
    return value;
  }

  public String getStringValue(){
    return (value) ? "true" : "false";
  }

  public String toJAVA(){
    return (value) ? "true" : "false";
  }

  private boolean value;
}

 