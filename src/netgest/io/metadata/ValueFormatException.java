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

	
	public ValueFormatException(Exception e)
	{
		super(e);
	}
	
}