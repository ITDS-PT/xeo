package netgest.bo.runtime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import netgest.bo.data.DataException;
import netgest.bo.data.DataRow;
import netgest.bo.data.DataSet;
import netgest.bo.def.boDefAttribute;
import netgest.bo.utils.GridFlashBack;
import netgest.bo.utils.ObjectAttributeValuePair;
import netgest.bo.utils.RowGridFlashBack;
import netgest.utils.LovUtils;
import netgest.utils.StringUtils;

/**
 * 
 * 
 * The {@link boFlashBackHandler} class is responsible for returning a copy of 
 * a boObject (which is being edited) which contains the previous values 
 * (i.e. saved in the database). The flashBack object is used to check differences between
 * an edited object and a saved object 
 * 
 * @author Pedro Rio
 * @company ITDS
 * @version 1.0
 *
 */
public class boFlashBackHandler 
{
	
	/**
	 * The object to create the flashback from
	 */
	boObject current;
	
	/**
	 * The flashback object
	 */
	boObject flashBack;
	
	public boObject getFlashBack() {
		return flashBack;
	}
	
	/**
	 * 
	 * Public constructor
	 * 
	 * @param object
	 */
	public boFlashBackHandler(boObject object){
		this.current = object;
		try{
			this.flashBack = getFlashBackObject();
		} 
		catch (boRuntimeException e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * Retrieves the flash back object
	 * 
	 * @return A clone of the object with the values of the attributes
	 * being the ones stored in the database
	 * 
	 * @throws boRuntimeException
	 */
	public boObject getFlashBackObject() throws boRuntimeException{
		//Clone the object and retrieve its attributes
		//if (current.getDataRow().getFlashBackRow() != null)
		if (current.isChanged()){
	    	boObject flashBackObject = current.cloneObject();
	    	
	    	// Iterate through all non object collection attributes
	    	// and set the values to their previous (database saved) ones
	    	boAttributesArray arrayAttributes = flashBackObject.getAllAttributes();
	    	Enumeration listOfAttributes = arrayAttributes.elements();
	    	while (listOfAttributes.hasMoreElements()){
	    		 
	    		AttributeHandler attHandler = (AttributeHandler) listOfAttributes.nextElement();
	    		String attributeType = attHandler.getDefAttribute().getAtributeDeclaredType();
	    		if (!boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION.equalsIgnoreCase(attributeType) && current.getDataRow().getFlashBackRow() != null)
	    			this.setAttributeValueFlashBack(attHandler);
	    	}
	    	
	    	//Iterate through all object collection attributes
	    	//and set the values to their previous ones
	    	boBridgesArray bridgeArray = flashBackObject.getBridges();
	    	Enumeration bridgeList = bridgeArray.elements();
	    	while (bridgeList.hasMoreElements()) {
				bridgeHandler currBridgeHandler = (bridgeHandler) bridgeList.nextElement();
				this.setBridgeValueFlashBack(currBridgeHandler);
			}
	    	this.flashBack = flashBackObject;
	    	return this.flashBack;
    	}
    	else
    		return null;
	}
	
	/**
	 * 
	 * Reverts the values of a bridge to the ones saved in the database
	 * 
	 * @param changed The bridgeHandler to revert
	 */
	private void setBridgeValueFlashBack(bridgeHandler changed){
			String name = changed.getDefAttribute().getName();
			//Get the previous values
    		DataSet currentSet = current.getDataRow().getChildDataSet(current.getEboContext(), name);
    		
    		HashSet<BigDecimal> bouiSet = new HashSet<BigDecimal>();
    		int count = currentSet.getRowCount();
	    	int deletedCount = currentSet.getDeletedRowsCount();
    		
    		//Get all BOUIs from the flash back object
    		for (int k = 1; k <= count ; k++){
    			DataRow row = currentSet.rows(k);
    			if (!row.isNew()){
	    			bouiSet.add(row.getBigDecimal("CHILD$"));
    			}
    		}
    		for (int k = 1 ; k <= deletedCount ; k++){
    			DataRow row = currentSet.deletedRows(k);
    			bouiSet.add(row.getFlashBackRow().getBigDecimal("CHILD$"));
    		}
    		
    		try{
    			changed.beforeFirst();
	    		//Remove all existing BOUIs from the bridge
	    		while (changed.next()){
	    			changed.removeCurrent();
	    		}
	    		
	    		//Add all the previous BOUIs from
	    		Iterator<BigDecimal> it = bouiSet.iterator();
	    		while (it.hasNext()){
	    			BigDecimal currentBOUI = it.next();
	    			changed.add(currentBOUI);
	    		}
    		}
    		catch (boRuntimeException e){
    			e.printStackTrace();
    		}
    	
	}
	
	 /**
     * 
     * Sets the value of a given attribute, to its previous value
     * 
     * @param changed The attribute to change the value
     *  
     * @throws boRuntimeException If an attribute is set with an invalid value
     */
    private void setAttributeValueFlashBack(AttributeHandler changed) throws boRuntimeException{
    	String attName = changed.getName();
    	String attributeType = changed.getDefAttribute().getAtributeDeclaredType();
    	DataRow flashBackRow = current.getDataRow().getFlashBackRow();
    	
    	//Find the type of attribute and set the value
    	try{
	    	if (boDefAttribute.ATTRIBUTE_TEXT.equals(attributeType)){
	    		String val = flashBackRow.getString(attName);
	    		changed.setValueString(val);
	    	}
	    	else if (boDefAttribute.ATTRIBUTE_DATE.equals(attributeType)){
	    		Date val = flashBackRow.getDate(attName);
	    		changed.setValueDate(val);
	    	}
	    	else if (boDefAttribute.ATTRIBUTE_DATETIME.equals(attributeType)){
	    		Date val = flashBackRow.getDate(attName);
	    		changed.setValueDate(val);
	    	}
	    	else if (boDefAttribute.ATTRIBUTE_BOOLEAN.equals(attributeType)){
	    		BigDecimal val = flashBackRow.getBigDecimal(attName);
	    		if (val != null){
		    		if (val.intValue() == 1)
		    			changed.setValueBoolean(new Boolean(true));
		    		else
		    			changed.setValueBoolean(new Boolean(false));
	    		} else {
	    			if (current.getAttribute( attName ).getValueObject()!= null)
	    				changed.setValueBoolean(new Boolean(false));
	    		}
	    	}
	    	else if (boDefAttribute.ATTRIBUTE_OBJECT.equals(attributeType)){
	    		String columName = changed.getDefAttribute().getDbName();
	    		Object val = flashBackRow.getObject(columName);
	    		changed.setValueObject(val);
	    	}
	    	else if (boDefAttribute.ATTRIBUTE_LONGTEXT.equals(attributeType)){
	    		String val = flashBackRow.getString(attName);
	    	//	if (val != null)
	    			changed.setValueString(val);
	    	}
	    	else if (boDefAttribute.ATTRIBUTE_DURATION.equals(attributeType)){
	    		Date val = flashBackRow.getDate(attName);
	    //		if (val != null)
	    			changed.setValueDate(val);
	    	}
	    	else if (boDefAttribute.ATTRIBUTE_SEQUENCE.equals(attributeType)){
	    		BigDecimal val = flashBackRow.getBigDecimal(attName);
	    		if (val != null)
	    			changed.setValueLong(val.longValue());
	    		else {
	    			changed.setValueObject(  null );
	    		}
	    	}
	    	else if (boDefAttribute.ATTRIBUTE_CURRENCY.equals(attributeType)){
	    		BigDecimal val = flashBackRow.getBigDecimal(attName);
	    		if (val != null)
	    			changed.setValueLong(val.longValue());
	    		else {
	    			changed.setValueObject(  null );
	    		}
	    	}
	    	else if (boDefAttribute.ATTRIBUTE_NUMBER.equals(attributeType)){
	    		Object val = flashBackRow.getObject(attName);
	    		//if (val != null)
	    			changed.setValueObject(val);
	    	}
	    	else if (boDefAttribute.ATTRIBUTE_BINARYDATA.equals(attributeType)){
                  Object val = flashBackRow.getObject(attName);
               //   if (val != null)
                         changed.setValueObject(val);
            }
    	} catch (DataException e){
    		//This means that a given column could not be found in FlashBack
    		//probably someone changed the boObject and is trying to render differences with something that
    		//did not exist, we should continue to show the differences, still
    		
    	}
    	
    }
    
    /**
     * 
     * Retrieves a map with the non-bridge-attributes that have different values in the
     * object and its flash back object
     * 
     * @return A map with the list of attribute which have a different value of the
     * one stored in the database
     */
    public HashMap<String, ObjectAttributeValuePair> getAttributeDiference(){
    	HashMap<String, ObjectAttributeValuePair> result = new HashMap<String, ObjectAttributeValuePair>();
    	
    	//Get All non bridgge
    	boAttributesArray arrayAtts = this.current.getAttributes();
    	Enumeration listOfAttributes = arrayAtts.elements();
    	
    	while (listOfAttributes.hasMoreElements()){
    		AttributeHandler attHandler = (AttributeHandler) listOfAttributes.nextElement();
    		if ( attributeNotCollectionNorState( attHandler ) ){
	    		AttributeHandler previousValueHandler = flashBack.getAttribute(attHandler.getName());
	    		try{
	    			String currentValue = attHandler.getValueString();
	    			String previousValue = previousValueHandler.getValueString();
	    			
	    			//The BOUI is always different so it cannot enter the list
	    			if (!isSystemAttribute( attHandler.getName() ) && attributeValueChanged( currentValue, previousValue) ){
	    				String attLabel = attHandler.getDefAttribute().getLabel();
	    				if ( isObjectAttribute( attHandler ) ){
	    					
	    					boObject currentValueObject = attHandler.getObject();
	    					boObject oldValueObject = previousValueHandler.getObject();

	    					String currentDisplayValue = "";
	    					if (currentValueObject != null){
	    						currentDisplayValue = currentValueObject.getTextCARDID().toString();
	    					}
	    					String previousDisplayValue = "";
	    					if (oldValueObject != null){
	    						previousDisplayValue = oldValueObject.getTextCARDID().toString();
	    					}

	    					result.put( attLabel, new ObjectAttributeValuePair( previousValue, currentValue, previousDisplayValue,
	    							currentDisplayValue, attLabel ));
	    					
	    				} else if ( StringUtils.hasValue( attHandler.getDefAttribute().getLOVName())){
	    					String lovName = attHandler.getDefAttribute().getLOVName();
	    					String newValueLov = LovUtils.getDescriptionForLovValues(current.getEboContext(), lovName, currentValue);
	    					String oldValueLov = LovUtils.getDescriptionForLovValues(current.getEboContext(), lovName, previousValue);

	    					result.put(attLabel, new ObjectAttributeValuePair(previousValue, currentValue, oldValueLov, newValueLov, attLabel));
	    				} else {
	    					result.put(attLabel, new ObjectAttributeValuePair(previousValue, currentValue, attLabel));
	    				}
	    			}
	    		} 
				catch (boRuntimeException e) 
				{
					//Continue to the next attribute
					e.printStackTrace();
				}
    		}
    	}
    	return result;
    }

	private boolean attributeValueChanged(String newValue, String oldValue) {
		return !newValue.equalsIgnoreCase(oldValue);
	}

	private boolean isObjectAttribute(AttributeHandler attHandler) {
		return boDefAttribute.ATTRIBUTE_OBJECT.equals(attHandler.getDefAttribute().getAtributeDeclaredType());
	}

	private boolean attributeNotCollectionNorState(AttributeHandler attHandler) {
		return !boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION.equalsIgnoreCase(attHandler.getDefAttribute().getAtributeDeclaredType())
			&& !(boDefAttribute.TYPE_STATEATTRIBUTE == attHandler.getDefAttribute().getAtributeType());
	}
    
    /**
     * 
     * Retrieves a map with the non-bridge-attributes that have different values in the
     * object and its flash back object
     * 
     * @return A map with the list of attribute which have a different value of the
     * one stored in the database
     */
    public HashMap<String, ObjectAttributeValuePair> getAttributeDiferenceByName()
    {
    	HashMap<String, ObjectAttributeValuePair> result = new HashMap<String, ObjectAttributeValuePair>();
    	
    	//Get All non bridge
    	boAttributesArray arrayAtts = this.current.getAttributes();
    	Enumeration listOfAttributes = arrayAtts.elements();
    	
    	while (listOfAttributes.hasMoreElements()){
    		//Percorrer todos os atributos do objecto clonado
    		AttributeHandler attHandler = (AttributeHandler) listOfAttributes.nextElement();
    		if (attributeNotCollectionNorState(attHandler) 	){	
	    		AttributeHandler flashBackHandler = flashBack.getAttribute(attHandler.getName());
	    		
	    		String newValue;
				try {
					newValue = attHandler.getValueString();
					String oldValue = flashBackHandler.getValueString();
					//The BOUI is always different so it cannot enter the list
					if (!isSystemAttribute(attHandler.getName())){
						if (attributeValueChanged(newValue, oldValue)){
							result.put(attHandler.getName(), new ObjectAttributeValuePair(oldValue, newValue, attHandler.getName()));
			    		}
					}
				} 
				catch (boRuntimeException e) {
					//Continue to the next attribute
					e.printStackTrace();
				}
    		}
    	}
    	return result;
    }
    
    /**
     * 
     * Checks if a given attribute name is a system attribute
     * 
     * @param name
     * @return
     */
    private boolean isSystemAttribute(String name){
    	if (name.equalsIgnoreCase("BOUI") ||
    			name.equalsIgnoreCase("SYS_DTCREATE") ||
    			name.equalsIgnoreCase("SYS_DTSAVE") ||
    			name.equalsIgnoreCase("CREATOR") ||
    			name.equalsIgnoreCase("SYS_USER") ||
    			name.equalsIgnoreCase("CLASSNAME") ||
    			name.equalsIgnoreCase("TEMPLATE") ||
    			name.equalsIgnoreCase("PARENTCTX") ||
    			name.equalsIgnoreCase("SYS_ORIGIN") ||
    			name.equalsIgnoreCase("SYS_FROMOBJ")) 
    		return true;
    	else
    		return false;
    }
    
    /**
     * 
     * Retrieves a map with the list of of differences in bridges	
     * 
     * @return A mapping for each bridge with the list of added and deleted rows
     */
    public HashMap<String, GridFlashBack> getBridgeDifference()
    {
    	boBridgesArray bridgesArray = this.current.getBridges();
    	HashMap<String,GridFlashBack> result = new HashMap<String, GridFlashBack>();
    	HashSet<String> currentBouisSet = new HashSet<String>();
    	try 
    	{
    		
	    	Enumeration listOfBridges = bridgesArray.elements();
	    	while (listOfBridges.hasMoreElements())
	    	{
	    		//Percorrer todos os atributos do objecto clonado
	    		bridgeHandler attHandler = (bridgeHandler) listOfBridges.nextElement();
	    		bridgeHandler flashBackHandler = flashBack.getBridge(attHandler.getName());
	    		GridFlashBack currentBridgeFlashBack = new GridFlashBack();
	    		
	    		//Vou comparar o corrente
	    		attHandler.beforeFirst();
	    		
	    		while (attHandler.next()){
					boObject currentObject = attHandler.getObject();
					long bouiCurrent = currentObject.getBoui();
					//Add the boui to the set, to be later reused
					currentBouisSet.add(String.valueOf(bouiCurrent));
					if (!flashBackHandler.haveBoui(bouiCurrent)){	//We have a deleted row
						HashMap<String, String> values = new HashMap<String, String>();
						boAttributesArray currObjAtts = currentObject.getAttributes();
						Enumeration allAttributesEnum = currObjAtts.elements();
						while (allAttributesEnum.hasMoreElements()){
							AttributeHandler handlerCurrentAttribute = (AttributeHandler)allAttributesEnum.nextElement();
							values.put(handlerCurrentAttribute.getName(), handlerCurrentAttribute.getValueString());
							currentBridgeFlashBack.addColumnName(handlerCurrentAttribute.getDefAttribute().getLabel());
						}
						values.put("CARDID", currentObject.getTextCARDID().toString());
						values.put("SYS_CARDID", currentObject.getTextCARDID().toString());
						RowGridFlashBack row = new RowGridFlashBack(values);
						currentBridgeFlashBack.addNewRow(row);
					}
				}
	    		
	    		flashBackHandler.beforeFirst();
	    		while (flashBackHandler.next()){
	    			boObject currentFlashBack = flashBackHandler.getObject();
	    			long bouiCurrent = currentFlashBack.getBoui();
	    			if (!currentBouisSet.contains(String.valueOf(bouiCurrent))){
	    				//We have an added row, so we must add it
	    				HashMap<String, String> values = new HashMap<String, String>();
						boAttributesArray currObjAtts = currentFlashBack.getAttributes();
						Enumeration allAttributesEnum = currObjAtts.elements();
						while (allAttributesEnum.hasMoreElements()){
							AttributeHandler handlerCurrentAttribute = (AttributeHandler)allAttributesEnum.nextElement();
							values.put(handlerCurrentAttribute.getName(), handlerCurrentAttribute.getValueString());
						}
						values.put("CARDID", currentFlashBack.getTextCARDID().toString());
						values.put("SYS_CARDID", currentFlashBack.getTextCARDID().toString());
						RowGridFlashBack row = new RowGridFlashBack(values);
						currentBridgeFlashBack.addDeletedRow(row);
	    			}
	    		}
	    		if (currentBridgeFlashBack.hasValues())
	    			result.put(attHandler.getName(), currentBridgeFlashBack);
	    	}
    	}
    	catch (boRuntimeException e) {
    		throw new RuntimeException(e);
    	}
    		
    	return result;
    }
	
    
    
   
    
    
    
	
}
