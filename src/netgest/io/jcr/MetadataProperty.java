package netgest.io.jcr;

import java.io.InputStream;
import java.util.Date;

import netgest.io.metadata.ValueFormatException;
import netgest.io.metadata.iMetadataItem;
import netgest.io.metadata.iMetadataProperty;

/**
 * 
 * 
 * Implementation of a metadata property
 * 
 * @author Pedro
 *
 */
public class MetadataProperty implements iMetadataProperty {

	
	
	/**
	 * The data type of the property
	 */
	private MetadataProperty.METADATA_TYPE p_dataType;
	
	/**
	 * Holds the value of the property
	 */
	private Object p_value;
	
	/**
	 * The name of the property
	 */
	private String p_name;
	
	/**
	 * If the property has multiple values
	 */
	private boolean p_hasMultipleValues;
	
	/**
	 * 
	 * Public constructor for a new String property
	 * 
	 * @param name The name of the property
	 * @param value The String value of the property
	 */
	public MetadataProperty(String name, String value){
		this.p_value = value;
		this.p_name = name; 
		this.p_dataType = METADATA_TYPE.STRING;
		this.p_hasMultipleValues = false;
	}
	
	
	/**
	 * 
	 * Public constructor for a new[] String property
	 * 
	 * @param name The name of the property
	 * @param value The String[] value of the property
	 */
	public MetadataProperty(String name, String[] value){
		this.p_value = value;
		this.p_name = name; 
		this.p_dataType = METADATA_TYPE.STRING;
		this.p_hasMultipleValues = true;
	}
	
	/**
	 * 
	 * Public constructor for a new Date property
	 * 
	 * @param name The name of the property
	 * @param value The Date value of the property
	 */
	public MetadataProperty(String name, Date value){
		this.p_value = value;
		this.p_name = name;
		this.p_dataType = METADATA_TYPE.DATE;
		this.p_hasMultipleValues = false;
	}
	
	/**
	 * 
	 * Public constructor for a new Date[] property
	 * 
	 * @param name The name of the property
	 * @param value The Date[] value of the property
	 */
	public MetadataProperty(String name, Date[] value){
		this.p_value = value;
		this.p_name = name;
		this.p_dataType = METADATA_TYPE.DATE;
		this.p_hasMultipleValues = true;
	}
	
	
	
	/**
	 * 
	 * Public constructor for a new Long property
	 * 
	 * @param name The name of the property
	 * @param value The Long value of the property
	 */
	public MetadataProperty(String name, Long value){
		this.p_value = value;
		this.p_name = name;
		this.p_dataType = METADATA_TYPE.LONG;
		this.p_hasMultipleValues = false;
	}
	
	/**
	 * 
	 * Public constructor for a new Long[] property
	 * 
	 * @param name The name of the property
	 * @param value The Long[] value of the property
	 */
	public MetadataProperty(String name, Long[] value){
		this.p_value = value;
		this.p_name = name;
		this.p_dataType = METADATA_TYPE.LONG;
		this.p_hasMultipleValues = true;
	}
	
	
	/**
	 * 
	 * Public constructor for a new Boolean property
	 * 
	 * @param name The name of the property
	 * @param value The Boolean value of the property
	 */
	public MetadataProperty(String name, Boolean value){
		this.p_value = value;
		this.p_name = name;
		this.p_dataType = METADATA_TYPE.BOOLEAN;
		this.p_hasMultipleValues = false;
	}
	
	/**
	 * 
	 * Public constructor for a new Boolean[] property
	 * 
	 * @param name The name of the property
	 * @param value The Boolean[] value of the property
	 */
	public MetadataProperty(String name, Boolean[] value){
		this.p_value = value;
		this.p_name = name;
		this.p_dataType = METADATA_TYPE.BOOLEAN;
		this.p_hasMultipleValues = true;
	}
	
	/**
	 * 
	 * Public constructor for a new Binary property
	 * 
	 * @param name The name of the property
	 * @param value The Binary value of the property
	 */
	public MetadataProperty(String name, InputStream value){
		this.p_value = value;
		this.p_name = name;
		this.p_dataType = METADATA_TYPE.BINARY;
		this.p_hasMultipleValues = false;
	}
	
	/**
	 * 
	 * Public constructor for a new Binary[] property
	 * 
	 * @param name The name of the property
	 * @param value The Binary[] value of the property
	 */
	public MetadataProperty(String name, InputStream[] value){
		this.p_value = value;
		this.p_name = name;
		this.p_dataType = METADATA_TYPE.BINARY;
		this.p_hasMultipleValues = true;
	}
	
	
	
	/**
	 * 
	 * Public constructor for a new reference property
	 * 
	 * @param name The name of the property
	 * @param value The reference value of the property
	 */
	public MetadataProperty(String name, iMetadataItem value){
		this.p_value = value;
		this.p_name = name;
		this.p_dataType = METADATA_TYPE.REFERENCE;
		this.p_hasMultipleValues = false;
	}
	
	/**
	 * 
	 * Public constructor for a new reference[] property
	 * 
	 * @param name The name of the property
	 * @param value The reference[] value of the property
	 */
	public MetadataProperty(String name, iMetadataItem[] value){
		this.p_value = value;
		this.p_name = name;
		this.p_dataType = METADATA_TYPE.REFERENCE;
		this.p_hasMultipleValues = true;
	}
	
	
	@Override
	public String getMetadataType() {
		return p_dataType.name();
	}
	
	/**
	 * 
	 * Whether the property has multiple values or not
	 * 
	 * @return True if the property has multiple values and false otherwise
	 */
	public boolean hasMultipleValues(){
		return p_hasMultipleValues;
	}

	@Override
	public String getPropertyIdentifier() {
		return p_name;
	}

	@Override
	public iMetadataItem getReference() throws ValueFormatException {
		try{ return (iMetadataItem) p_value; }
		catch (ClassCastException e) { throwInvalidDataTypeConversion("iMetadataItem"); }
		return null;
		
	}

	@Override
	public InputStream getValueBinary() throws ValueFormatException {
		try{ return (InputStream) p_value; }
		catch (ClassCastException e) { throwInvalidDataTypeConversion("InputStream"); }
		return null;
	}

	@Override
	public boolean getValueBoolean() throws ValueFormatException {
		try{ return (Boolean) p_value; }
		catch (ClassCastException e) { throwInvalidDataTypeConversion("Boolean"); }
		return false;
	}

	@Override
	public Date getValueDate() throws ValueFormatException {
		try{ return (Date) p_value; }
		catch (ClassCastException e) { throwInvalidDataTypeConversion("Date"); }
		return null;
	}

	@Override
	public Long getValueLong() throws ValueFormatException {
		try{ return (Long) p_value; }
		catch (ClassCastException e) { throwInvalidDataTypeConversion("Long"); }
		return null;
	}

	@Override
	public String getValueString() throws ValueFormatException {
		try{ return (String) p_value; }
		catch (ClassCastException e) { throwInvalidDataTypeConversion("String"); }
		return null;
	}

	@Override
	public void setValueBinary(InputStream newValue) {
		this.p_value = newValue;
		this.p_dataType = iMetadataProperty.METADATA_TYPE.BINARY;
	}

	@Override
	public void setValueBoolean(boolean newValue) {
		this.p_value = newValue;
		this.p_dataType = iMetadataProperty.METADATA_TYPE.BOOLEAN;
	}

	@Override
	public void setValueDate(Date newValue) {
		this.p_value = newValue;
		this.p_dataType = iMetadataProperty.METADATA_TYPE.DATE;
	}

	@Override
	public void setValueLong(Long newValue) {
		this.p_value = newValue;
		this.p_dataType = iMetadataProperty.METADATA_TYPE.LONG;
	}

	@Override
	public void setValueReference(iMetadataItem newReference) {
		this.p_value = newReference;
		this.p_dataType = iMetadataProperty.METADATA_TYPE.REFERENCE;
	}

	@Override
	public void setValueString(String newValue) {
		this.p_value = newValue;
		this.p_dataType = iMetadataProperty.METADATA_TYPE.STRING;
	}

	@Override
	public boolean[] getValuesBoolean() throws ValueFormatException {
		return (boolean[])p_value;
	}

	@Override
	public Date[] getValuesDate() throws ValueFormatException {
		try{ return (Date[]) p_value; }
		catch (ClassCastException e) { throwInvalidDataTypeConversion("Date[]"); }
		return null;
			
	}

	@Override
	public Long[] getValuesLong() throws ValueFormatException {
		try{ return (Long[]) p_value; }
		catch (ClassCastException e) { throwInvalidDataTypeConversion("Long[]"); }
		return null;
	}

	@Override
	public String[] getValuesString() throws ValueFormatException {
		try{ return (String[]) p_value; }
		catch (ClassCastException e) { throwInvalidDataTypeConversion("String[]"); }
		return null;
	}
	
	@Override
	public String toString(){
		return this.p_name + " - " + this.getMetadataType() + " - " + p_value.toString();
		
	}

	@Override
	public Object getValueObject(){
		return p_value;
	}

	@Override
	public Object[] getValueObjects() throws ValueFormatException{
		try{ return (Object[]) p_value; }
		catch (ClassCastException e) { throwInvalidDataTypeConversion("Object[]"); }
		return null;
	}

	@Override
	public void setValueObject(Object newValue) {
		p_value = newValue;
		
	}

	@Override
	public void setValueObjects(Object[] newValue) {
		p_value = newValue;
	}
	
	/**
	 * 
	 * Throws a value format exception when an conversion to the wrong type occurs
	 * 
	 * @param convertionAttempt The name of the attempted conversion
	 * 
	 * @throws ValueFormatException 
	 */
	public void throwInvalidDataTypeConversion(String convertionAttempt) throws ValueFormatException{
		throw new ValueFormatException("The value of property " + p_name +"("+ 
				p_dataType.name() + ") is not of type " + convertionAttempt);
	}

}
