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
		this.p_dataType = METADATA_TYPE.STRING;
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
	}
	
	/**
	 * 
	 * Public constructor for a new reference property
	 * 
	 * @param name The name of the property
	 * @param value The String value of the property
	 */
	public MetadataProperty(String name, iMetadataItem value){
		this.p_value = value;
		this.p_name = name;
		this.p_dataType = METADATA_TYPE.REFERENCE;
	}
	
	@Override
	public String getMetadataType() {
		return p_dataType.name();
	}

	@Override
	public String getPropertyIdentifier() {
		return p_name;
	}

	@Override
	public iMetadataItem getReference() throws ValueFormatException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getValueBinary() throws ValueFormatException {
		return (InputStream) p_value;
	}

	@Override
	public boolean getValueBoolean() throws ValueFormatException {
		return (Boolean) p_value;
	}

	@Override
	public Date getValueDate() throws ValueFormatException {
		return (Date) p_value;
	}

	@Override
	public Long getValueLong() throws ValueFormatException {
		return (Long) p_value;
	}

	@Override
	public String getValueString() throws ValueFormatException {
		return (String) p_value;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date[] getValuesDate() throws ValueFormatException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long[] getValuesLong() throws ValueFormatException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getValuesString() throws ValueFormatException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString(){
		return this.p_name + " - " + this.getMetadataType() + " - " + p_value.toString();
		
	}

	@Override
	public Object getValueObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getValueObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValueObject(Object newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValueObjects(Object[] newValue) {
		// TODO Auto-generated method stub
		
	}

}
