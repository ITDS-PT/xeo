/*Enconding=UTF-8*/


package netgest.bo.parser.symbol;

/**
 * A Class class.
 * <P>
 * @author Francisco Câmara
 */
public class NumericLiteral implements LiteralExpression {

  /**
   * Constructor
   */
  public NumericLiteral() {
  }

  public void setValue(int value){
    this.value1 = value;
    intBool = true;
  }

  public void setValue(float value){
    this.value2 = value;
    floatBool = true;
  }

  public void setValue(double value){
    this.value3 = value;
    doubleBool = true;
  }

  public void setValue(long value){
    this.value4 = value;
    longBool = true;
  }

  public void setValue(short value){
    this.value5 = value;
    shortBool = true;
  }

  public void setValue(String value){
    this.value6 = value;
    stringBool = true;
  }

  public int getIntValue(){
    return value1;
  }

  public float getFloatValue(){
    return value2;
  }

  public double getDoubleValue(){
    return value3;
  }

  public long getLongValue(){
    return value4;
  }

  public short getShortValue(){
    return value5;
  }

  public String getStringValue(){
    return value6;
  }

  public boolean isInt(){
    return intBool;
  }

  public boolean isFloat(){
    return floatBool;
  }

  public boolean isDouble(){
    return doubleBool;
  }

  public boolean isLong(){
    return longBool;
  }

  public boolean isShort(){
    return shortBool;
  }

  public boolean isString(){
    return stringBool;
  }

  public String toJAVA(){
    if(intBool)
        return String.valueOf(value1);
    if(floatBool)
        return String.valueOf(value2);
    if(doubleBool)
        return String.valueOf(value3);
    if(longBool)
        return String.valueOf(value4);
    if(shortBool)
        return String.valueOf(value5);
    if(stringBool)
        return value6;
    return ""; 
  }

  private int value1;
  private float value2;
  private double value3;
  private long value4;
  private short value5;
  private String value6;

  private boolean intBool = false, floatBool = false, doubleBool = false,
                longBool = false, shortBool = false, stringBool = false;

}

