/**
 * 
 */
package netgest.io.metadata;

/**
 * 
 * Represents an exception of incompatible value conversion
 * A given method is supposed to return a String value and attempts
 * to convert a given object's value to String and that Object's value
 * is not a string.
 * 
 * @author PedroRio
 *
 */
public class ValueFormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2572776360512185045L;

	
	/**
	 * 
	 * Throws a value format exception based on another exception
	 * 
	 * @param e The base exception
	 */
	public ValueFormatException(Exception e)
	{
		super(e);
	}
	
	/**
	 * 
	 * Throws a value format exception with a given message
	 * 
	 * @param message The message to display
	 */
	public ValueFormatException(String message){
		super(message);
	}
	
}