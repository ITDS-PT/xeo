/*Enconding=UTF-8*/
package netgest.utils;
import java.io.UnsupportedEncodingException;

public class StringFormatter
{
  private static StringFormatter instance = null;

  protected StringFormatter() {
    // Exists only to defeat instantiation.
  }
  
  public static StringFormatter getInstance() {
    if(instance == null) {
      instance = new StringFormatter();
    }
    return instance;
  }
  
  public byte[] convertCharsetToByteArray(String str_to_format, String encoding) 
  {
    try 
    {
      return str_to_format.getBytes(encoding);
    }
    catch(UnsupportedEncodingException e) 
    {
      return str_to_format.getBytes();
    }
  }
}