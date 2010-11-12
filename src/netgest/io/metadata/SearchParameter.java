package netgest.io.metadata;

/**
 * 
 * Implementation of the {@link iSearchParameter} inteface
 * 
 * @author PedroRio
 *
 */
public class SearchParameter implements iSearchParameter {

	
	/**
	 * The name of the parameter
	 */
	private String p_paramName;
	
	/**
	 * The data type of the parameter
	 */
	private DATA_TYPE p_paramDataType;
	
	/**
	 * The value of the parameter as a string
	 */
	private String p_paramValue;
	
	/**
	 * The logical operator associated with this parameter
	 */
	private LOGICAL_OPERATOR p_paramOperator;
	
	public SearchParameter(String name, DATA_TYPE type, String value, LOGICAL_OPERATOR op){
		
		p_paramName = name;
		p_paramDataType = type;
		p_paramValue = value;
		p_paramOperator = op;
		
	}
	
	@Override
	public String getPropertyName() {
		return p_paramName;
	}

	@Override
	public DATA_TYPE getPropertyDataType() {
		return p_paramDataType;
	}

	@Override
	public String getPropertyValue() {
		return p_paramValue;
	}

	@Override
	public LOGICAL_OPERATOR getLogicalOperator() {
		return p_paramOperator;
	}

}
