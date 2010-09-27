/**
 * 
 */
package netgest.io.metadata;

import java.io.InputStream;
import java.util.Date;


/**
 * 
 * Represents a property for a given {@link iMetadataItem}.
 * Properties are key/value pairs where values must have a data type
 * (list of type in the <code>METADATA_TYPE</code> enumeration in
 * {@link iMetadataProperty.METADATA_TYPE}
 * 
 *
 * 
 * @author PedroRio
 *
 */
public interface iMetadataProperty 
{
	/**
	 * 
	 * The list of valid metadata property data types
	 * 
	 * @author PedroRio
	 *
	 */
	public static enum METADATA_TYPE 
	{ 
		STRING, 
		LONG, 
		DOUBLE, 
		TIME, 
		DATE,
        DATETIME, 
        BOOLEAN,
        BINARY, //Binary content
        REFERENCE, //Reference to another item
        STRING_ARRAY,
        OTHER
    }
	
	/**
	 * 
	 * Retrieves the metadata type for this property
	 * 
	 * @return One of the types from the <code>METADATA_TYPE</code> enum
	 */
	public String getMetadataType();
	
	/**
	 * 
	 * Retrieves the property name
	 * 
	 * @return A string with the name
	 */
	public String getPropertyIdentifier();
	
	
	/**
	 * Retrieve the value of this property as a String
	 * 
	 * @return A string with the value of the property
	 * 
	 * @throws ValueFormatException If the property value cannot
	 * be converted to String
	 */
	public String getValueString() throws ValueFormatException;
	
	/**
	 * Retrieve the value of this property as a String array
	 * 
	 * @return An array with the value of the property
	 * 
	 * @throws ValueFormatException If the property value cannot
	 * be converted to String[]
	 */
	public String[] getValuesString() throws ValueFormatException;
	
	/**
	 * 
	 * Sets the value of the property as String
	 * 
	 * @param newValue The value of the property
	 */
	public void setValueString(String newValue);
	
	/**
	 * Retrieve the value of this property a Long
	 * 
	 * 
	 * @return The property of the value as long
	 * 
	 * @throws ValueFormatException If the property value cannot be
	 * converted to Long
	 */
	public Long getValueLong() throws ValueFormatException;
	
	
	/**
	 * Retrieve the value of this property as a long array
	 * 
	 * @return An array with the value of the property
	 * 
	 * @throws ValueFormatException If the property value cannot
	 * be converted to Long array
	 */
	public Long[] getValuesLong() throws ValueFormatException;
	
	/**
	 * 
	 * Sets the value of the property as a long value
	 * 
	 * @param newValue The value of the property
	 */
	public void setValueLong(Long newValue);
	
	
	/**
	 * 
	 * Retrieve the value of this property as a Boolean
	 * 
	 * @return True or False
	 * 
	 *  @throws ValueFormatException If the property value cannot be
	 * converted to Boolean
	 */
	public boolean getValueBoolean() throws ValueFormatException;;
	
	
	/**
	 * Retrieve the value of this property as a boolean array
	 * 
	 * @return An array with the value of the property
	 * 
	 * @throws ValueFormatException If the property value cannot
	 * be converted to String
	 */
	public boolean[] getValuesBoolean() throws ValueFormatException;
	
	/**
	 * 
	 * Set the value of this property as a boolean
	 * 
	 * @param newValue The value of the property
	 */
	public void setValueBoolean(boolean newValue);
	
	
	/**
	 * 
	 * Sets the value of the property as generic object
	 * 
	 * @param newValue The object
	 */
	public void setValueObject(Object newValue);
	
	/**
	 * 
	 * Sets the value of the property as an array 
	 * 
	 * @param newValue
	 */
	public void setValueObjects(Object[] newValue);
	
	/**
	 * 
	 * Retrieves the value of the property as an object
	 *  
	 * @return Retrieves the value of the property as an object
	 */
	public Object getValueObject();
	
	/**
	 * 
	 * Retrieves the value of property as an object[]
	 * 
	 * @return
	 */
	public Object[] getValueObjects() throws ValueFormatException;
	
	/**
	 * 
	 * Retrieve the value of this property as a Date
	 * 
	 * @return The value of the property as a Date
	 * 
	 *  @throws ValueFormatException If the property value cannot be
	 * converted to Date
	 */
	public Date getValueDate() throws ValueFormatException;;
	
	/**
	 * Retrieve the value of this property as a Date array
	 * 
	 * @return An array with the value of the property
	 * 
	 * @throws ValueFormatException If the property value cannot
	 * be converted to Date array
	 */
	public Date[] getValuesDate() throws ValueFormatException;
	
	/**
	 * 
	 * Sets the value of this property as a Date
	 * 
	 * @param newValue The value of the property
	 */
	public void setValueDate(Date newValue);
	
	
	/**
	 * 
	 * Retrieve the value of this property as a reference to
	 * an existing item 
	 * 
	 * @return A reference to an existing item
	 * 
	 *  @throws ValueFormatException If the property value cannot be
	 * converted to a {@link iMetadataItem}
	 */
	public iMetadataItem getReference() throws ValueFormatException;
	
	/**
	 * 
	 * Sets the value of this property as a reference to an
	 * existing item
	 * 
	 * @param newReference A reference to an existing item
	 */
	public void setValueReference(iMetadataItem newReference);

	
	/**
	 * 
	 * Retrieves the content of this property as an input stream
	 * to the binary content
	 * 
	 * @return An input stream to the content of the property
	 * 
	 *  @throws ValueFormatException If the property value cannot be
	 * converted to an input stream
	 */
	public InputStream getValueBinary() throws ValueFormatException;;
	
	/**
	 * 
	 * Sets the value of this property as binary content
	 * 
	 * @param newValue
	 */
	public void setValueBinary(InputStream newValue);
	
}
