package netgest.io.metadata;

/**
 * 
 * Represents a query parameter over a given property
 * i.e. for example: <code>PROPERTY_NAME >= PROPERTY_VALUE</code>
 * or <code>PROPERTY_NAME <> PROPERTY_VALUE</code>
 * 
 * @author PedroRio
 *
 */
public interface iSearchParameter 
{
	
	/**
	 * 
	 * Enumeration with the types of logical
	 * operators available
	 * 
	 * @author PedroRio
	 *
	 */
	public enum LOGICAL_OPERATOR{
		BIGGER,
		BIGGER_OR_EQUAL,
		EQUAL,
		LESS,
		LESS_OR_EQUAL,
		DIFFERENT
	}
	
	
	/**
	 * 
	 * Enumeration with the data types
	 * available 
	 * 
	 * @author PedroRio
	 *
	 */
	public enum DATA_TYPE{
		STRING,
		BOOLEAN,
		DATE,
		NUMBER,
		ARRAY
	}
	
	/**
	 * 
	 * Retrieves the name of the property
	 * 
	 * @return The name of the property
	 */
	public String getPropertyName();
	
	/**
	 * 
	 * Retrieves the data type of the property
	 * 
	 * @return A string with the data type (String, Boolean, Long)
	 */
	public DATA_TYPE getPropertyDataType();
	
	/**
	 * Retrieve the value of the property as a string
	 * 
	 * @return A string representation of the value
	 * of the property
	 */
	public Object getPropertyValue();
	
	/**
	 * 
	 * Retrieve the logical operation to use in a search
	 * query
	 * 
	 * @return A string with the 
	 */
	public LOGICAL_OPERATOR getLogicalOperator();
}
