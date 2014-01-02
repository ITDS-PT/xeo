package netgest.bo.configUtils;

import java.util.HashMap;

import netgest.utils.ngtXMLHandler;

/**
 * 
 * Represents a configuration for List of Values to be used
 * in the JCR component  
 * 
 * @author PedroRio
 *
 */
public class LovConfig {

	/**
	 * 
	 * A list of lov types, XEOLov and JCR lov are built-in types.
	 * For other types of Lovs a property "className" must exist in the "Properties"
	 * 
	 * @author PedroRio
	 *
	 */
	public enum LOV_TYPES{
		XEOLOV,
		JCRLOV,
		OTHERLOV
	}
	
	/**
	 * The LOV type of this Lov
	 */
	private LOV_TYPES p_type;
	
	/**
	 * The classname
	 */
	private String p_classname;
	
	/**
	 * The xml handler from where to extract the information
	 */
	private ngtXMLHandler p_xmlHandler;
	
	/**
	 * The parameters to pass to the implementations
	 */
	private HashMap<String,Object> p_parameters;
	
	/**
	 * 
	 * Public constructor from a XML Handler
	 * 
	 * @param xmlHandler
	 */
	public LovConfig(ngtXMLHandler xmlHandler){
		p_parameters = new HashMap<String, Object>();
		p_xmlHandler = xmlHandler;
		load();
	}
		
	
	private void load(){
		ngtXMLHandler h = p_xmlHandler.getChildNode("parameters");
		ngtXMLHandler[] propsXml = h.getChildNodes();
		for (ngtXMLHandler currProp : propsXml){
			
			String key = currProp.getAttribute("name");
			String value = currProp.getAttribute("value");
			p_parameters.put(key, value);
		}
		
		String className = p_xmlHandler.getAttribute("className");
		if (className.equalsIgnoreCase("xeoLov")){
			p_type = LOV_TYPES.XEOLOV;
			p_classname = "xeoLov";
		}
		else if (className.equalsIgnoreCase("jcrLov")){
			p_type = LOV_TYPES.JCRLOV;
			p_classname = "xeoLov";
		}
		else{
			p_type = LOV_TYPES.OTHERLOV;
			p_classname = className;
		}
			
	}
	

	/**
	 * 
	 * Retrieves the parameters for the Lov
	 * 
	 * @return A map of key/value pairs with the parameters
	 */
	public HashMap<String,Object> getParameters(){
		return p_parameters;
	}
	
	
	/**
	 * 
	 * Retrieves the Lov type for this Lov
	 * 
	 * @return
	 */
	public LOV_TYPES getLovType(){
		return p_type;
	}
	
	/**
	 * 
	 * Retrieves the classname implementin the JCRLov interface
	 * Can be "xeoLov" and "jcrLov" for XEO Lovs and JCR lovs, for other types must be
	 * the fully qualified name of the class implementing the JCR lov interface
	 * 
	 * @return
	 */
	public String getClassName(){
		return p_classname;
	}
	
	
}
