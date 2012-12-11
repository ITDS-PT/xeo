/**
 * 
 */
package netgest.bo.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import netgest.bo.def.boDefAttribute;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.utils.StringUtils;


/**
 * 
 * Creates the difference between two objects
 * 
 * @author Pedro Pereira
 *
 */
public class ObjectDifference 
{
	
	/**
	 * The base boObject
	 */
	private	boObject baseObject;
	
	/**
	 * The object to compare
	 */
	private boObject toCompareObject;
	
	/**
	 * The map of attribute differences
	 */
	private HashMap<String, ObjectAttributeValuePair> attDiff;
	
	/**
	 * The map of bridges
	 */
	private HashMap<String,GridFlashBack> bridgeDiff;
	
	/**
	 * 
	 * Public constructor
	 * 
	 * @param base The base object
	 * @param toCompare The object to compare
	 */
	public ObjectDifference(boObject base, boObject toCompare)
	{
		this.baseObject = base;
		this.toCompareObject = toCompare;
		this.attDiff = getAttributeDifferences();
		this.bridgeDiff = getBridgeDifferences();
	}
	
	
	/**
	 * 
	 * Retrieves a list of attribute differences between 
	 * 
	 * @return A map of attribute/ pair of new value and old value
	 */
	private HashMap<String,ObjectAttributeValuePair> getAttributeDifferences()
	{
		HashMap<String, ObjectAttributeValuePair> result = new HashMap<String, ObjectAttributeValuePair>();
    	
    	//Get All non bridgge
    	boAttributesArray arrayAtts = baseObject.getAttributes();
    	Enumeration listOfAttributes = arrayAtts.elements();
    	
    	while (listOfAttributes.hasMoreElements())
    	{
    		//Percorrer todos os atributos do objecto clonado
    		AttributeHandler attHandler = (AttributeHandler) listOfAttributes.nextElement();
    		if (!boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION
    				.equalsIgnoreCase( attHandler.getDefAttribute().getAtributeDeclaredType()))
    		{	
	    		AttributeHandler flashBackHandler = toCompareObject.getAttribute(attHandler.getName());
	    		
	    		String newValue;
				try 
				{
					newValue = attHandler.getValueString();
					String oldValue = flashBackHandler.getValueString();
					
					if (boDefAttribute.ATTRIBUTE_OBJECT.equalsIgnoreCase( attHandler.getDefAttribute().getAtributeDeclaredType() )){
						boObject newObject = attHandler.getObject();
						if (newObject != null)
							newValue = newObject.getTextCARDID().toString();
						boObject oldObject = flashBackHandler.getObject();
						if (oldObject != null)
							oldValue = oldObject.getTextCARDID().toString();
					}
					
					if (StringUtils.hasValue( attHandler.getDefAttribute().getLOVName() ) ){
						lovObject obj = LovManager.getLovObject( baseObject.getEboContext()	, attHandler.getDefAttribute().getLOVName() );
						if (StringUtils.hasValue( newValue ))
							newValue = obj.getDescriptionByCode( newValue );
						if (StringUtils.hasValue( oldValue ))
							oldValue = obj.getDescriptionByCode( oldValue );
					}
		    		
					//The BOUI is always different so it cannot enter the list
					if (isAttributeValidForDifference(attHandler.getName()))
					{
						if (!newValue.equalsIgnoreCase(oldValue))
			    		{
							String attLabel = attHandler.getDefAttribute().getLabel();
			    			result.put(attLabel, new ObjectAttributeValuePair(oldValue, newValue, attLabel));
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
	
	/**
	 * 
	 * Checks if an attribute name is a system attribute
	 * 
	 * @param name The name of the attribute
	 * @return True if the attribute name is of a system attribute
	 * and false otherwise
	 */
	private boolean isAttributeValidForDifference(String name)
	{
		if (	name.equalsIgnoreCase("BOUI") 
			||	name.equalsIgnoreCase("SYS_DTCREATE")
			|| 	name.equalsIgnoreCase("SYS_DTSAVE")
			|| 	name.equalsIgnoreCase("SYS_USER")
			|| 	name.equalsIgnoreCase("SYS_ORIGIN")
			|| 	name.equalsIgnoreCase("TEMPLATE")
			|| 	name.equalsIgnoreCase("PARENTCTX")
			|| 	name.equalsIgnoreCase("CLASSNAME")
			|| 	name.equalsIgnoreCase("CREATOR")
			|| 	name.equalsIgnoreCase("SYS_FROMOBJ")	)
			return false;
		
		return true;
	}
	
	private HashMap<String,GridFlashBack> getBridgeDifferences()
	{
		HashMap<String,GridFlashBack> result = new HashMap<String, GridFlashBack>();
    	HashSet<String> currentBouisSet = new HashSet<String>();
    	try 
    	{
    		
    		Enumeration listOfAtts = baseObject.getAllAttributes().elements();
	    	//Enumeration listOfBridges = bridgesArray.elements();
	    	//while (listOfBridges.hasMoreElements())
    		while (listOfAtts.hasMoreElements())
	    	{
	    		//Percorrer todos os atributos do objecto clonado
    			AttributeHandler attHandlerNow = (AttributeHandler) listOfAtts.nextElement();
    			
    			if (attHandlerNow.isBridge())
    			{
    				bridgeHandler attHandler = baseObject.getBridge(attHandlerNow.getName());
    				//bridgeHandler attHandler = (bridgeHandler) listOfBridges.nextElement();
    				if (attHandler != null){
    					
    				
    	    		bridgeHandler flashBackHandler = toCompareObject.getBridge(attHandler.getName());
    	    		GridFlashBack currentBridgeFlashBack = new GridFlashBack();
    	    		
    	    		attHandler.beforeFirst();
    	    		
    	    		while (attHandler.next())
    	    		{
    					boObject currentObject = attHandler.getObject();
    					long bouiCurrent = currentObject.getBoui();
    					//Add the boui to the set, to be later reused
    					currentBouisSet.add(String.valueOf(bouiCurrent));
    					if (!flashBackHandler.haveBoui(bouiCurrent))
    					{	//We have a deleted row
    						HashMap<String, String> values = new HashMap<String, String>();
    						boAttributesArray currObjAtts = currentObject.getAttributes();
    						Enumeration allAttributesEnum = currObjAtts.elements();
    						while (allAttributesEnum.hasMoreElements())
    						{
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
    	    		while (flashBackHandler.next())
    	    		{
    	    			boObject currentFlashBack = flashBackHandler.getObject();
    	    			long bouiCurrent = currentFlashBack.getBoui();
    	    			if (!currentBouisSet.contains(String.valueOf(bouiCurrent)))
    	    			{
    	    				//We have an added row, so we must add it
    	    				HashMap<String, String> values = new HashMap<String, String>();
    						boAttributesArray currObjAtts = currentFlashBack.getAttributes();
    						Enumeration allAttributesEnum = currObjAtts.elements();
    						while (allAttributesEnum.hasMoreElements())
    						{
    							AttributeHandler handlerCurrentAttribute = (AttributeHandler)allAttributesEnum.nextElement();
    							values.put(handlerCurrentAttribute.getName(), handlerCurrentAttribute.getValueString());
    						}
    						values.put("CARDID", currentFlashBack.getTextCARDID().toString());
    						values.put("SYS_CARDID", currentFlashBack.getTextCARDID().toString());
    						RowGridFlashBack row = new RowGridFlashBack(values);
    						currentBridgeFlashBack.addDeletedRow(row);
    	    			}
    	    		}
    	    		result.put(attHandler.getName(), currentBridgeFlashBack);
    				}
    			}
    			
	    	}
    	}
	    	catch (boRuntimeException e) 
	    	{
				throw new RuntimeException(e);
			}
    		
    	return result;
	}
	
	/**
	 * 
	 * Checks whether the objects are comparable or not
	 * (i.e. have the same attributes)
	 * 
	 * @return True if the objects are comparable and false otherwise
	 */
	public boolean areObjectsComparable()
	{
		//TODO: Implement this, 
		//must check if the same 
		return false;
	}
	
	public HashMap<String,ObjectAttributeValuePair> getAttributeDifferencesOfObjects()
	{
		return this.attDiff;
	}
	
	
	public HashMap<String,GridFlashBack> getBridgeDifferencesOfObjects()
	{
		return this.bridgeDiff;
	}
}
