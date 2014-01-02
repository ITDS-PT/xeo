package netgest.io.jcr;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import netgest.bo.boConfig;
import netgest.bo.configUtils.FileNodeConfig;
import netgest.bo.configUtils.NodePropertyDefinition;
import netgest.bo.configUtils.RepositoryConfig;
import netgest.io.metadata.iSearchParameter;
import netgest.io.metadata.iSearchParameter.DATA_TYPE;

/**
 * 
 * This query is responsible for converting a text based query and
 * 
 * @author PedroRio
 * 
 */
public class QueryConverter {

	public QueryConverter() {}

	public String getQuery(String repositoryName, String path,
			boolean searchContent, String contentToSearch,
			List<iSearchParameter> properties, String filename) {

		StringBuilder b = new StringBuilder();

		RepositoryConfig repoConf = boConfig.getApplicationConfig()
				.getFileRepositoryConfiguration(repositoryName);

		String fileNodeType = repoConf.getFileConfig().getNodeType();

		if (path == null)
			b.append("//element(*," + fileNodeType + ")");
		else {
			b.append(cleanPath(path));
			b.append("//element(*," + fileNodeType + ")");
		}

		boolean hasConditions = false;
		if (searchContent) {
			String propName = getBinaryPropertyNameFromFile(repoConf
					.getFileConfig());
			hasConditions = true;
			b.append("[");
			b.append("jcr:contains(" + propName + ",'" + contentToSearch + "')");
		}
		
		Iterator<iSearchParameter> it = properties.iterator();
		if (!hasConditions) {
			if (properties.size() > 0){
				b.append("[");
				hasConditions = true;
			}
		} else if (properties.size() > 0)
			b.append(" and ");

		while (it.hasNext()) {
			iSearchParameter currentParam = it.next();
			if (currentParam.getPropertyDataType() == DATA_TYPE.ARRAY){
				
				String[] values = ((String)currentParam.getPropertyValue()).split(";");
				int k = 0;
				int size = values.length;
				for (String current : values){
					b.append("@"+currentParam.getPropertyName());
					b.append(" ");
					b.append("=");
					b.append(" ");
					b.append("'"+current+"'");
					if (k < size-1){
						b.append(" and ");
						k++;
					}
				}
				
			}
			else{
				//FIXME: E se forem aquelas cenas de propriedade dentro de propriedade
				//tipo meta/@propriedade ?
				b.append("@"+currentParam.getPropertyName());
				b.append(" ");
				String operator = getCorrectLogicalOperatorRepresentation(currentParam);
				if (operator == null)
					return null;
				b.append(operator);
				b.append(" ");
				String dataValue = getCorrectValueRepresentation(currentParam);
				if (dataValue == null)
					return null;
				b.append(dataValue);
				if (it.hasNext())
					b.append(" and ");
			}
			
		}

		if (filename != null && filename.length() > 0 ){
			if (!hasConditions)
				b.append("[");
			else
				b.append(" and ");
			b.append( "fn:name() = '" + filename + "'");
			hasConditions = true;
		}
		
		if (hasConditions)
			b.append("]");

		return b.toString();
	}

	private String getBinaryPropertyNameFromFile(FileNodeConfig fileConf) {

		String binPropName = fileConf.getBinaryPropertyName();
		NodePropertyDefinition nodePropDef = fileConf.getProperties().get(
				binPropName);
		if (nodePropDef.isMainNode()) {
			return ".";
		} else
			return "./*";
	}

	/**
	 * 
	 * Given an {@link iSearchParameter}, returns the correct representation for
	 * its value when using a comparison in a query. For example, if the value
	 * is a string it returns 'value' if the value is a number it returns the
	 * number if the value is a date returns "xs:datetime(date)"
	 * 
	 * @param param
	 *            The parameter to return the correct value
	 * @return 
	 */
	private String getCorrectValueRepresentation(iSearchParameter param) {
		
		if (param.getPropertyDataType().equals(iSearchParameter.DATA_TYPE.STRING))
			return "'"+param.getPropertyValue()+"'";
		else if (param.getPropertyDataType().equals(iSearchParameter.DATA_TYPE.NUMBER))
			return param.getPropertyValue().toString();
		else if (param.getPropertyDataType().equals(iSearchParameter.DATA_TYPE.BOOLEAN))
		{
			if (param.getPropertyValue().toString().equalsIgnoreCase("1"))
				return "'true'";
			else if (param.getPropertyValue().toString().equalsIgnoreCase("0"))
				return "'false'";
			else 
				return param.getPropertyValue().toString();
		}
		else if (param.getPropertyDataType().equals(iSearchParameter.DATA_TYPE.DATE)){
			
			Timestamp correctDate = (Timestamp) param.getPropertyValue();
			String sCorrectDate = getDateTimeRepresentation(correctDate);
			return "xs:dateTime('"+sCorrectDate+"')";
		}
		
		return null;
		
	}

	/**
	 * 
	 * Returns the logical operator of a parameter as a string
	 *  
	 * @param param the parameter to retrieve the logical operator
	 * 
	 * @return A string representing the operator (<,<=,=,etc..)
	 * or null if no operator is found
	 */
	private String getCorrectLogicalOperatorRepresentation(
			iSearchParameter param) {
			
		if (param.getLogicalOperator().equals(
				iSearchParameter.LOGICAL_OPERATOR.BIGGER))
			return ">";
		else if (param.getLogicalOperator().equals(
				iSearchParameter.LOGICAL_OPERATOR.BIGGER_OR_EQUAL))
				return ">=";
		else if (param.getLogicalOperator().equals(
				iSearchParameter.LOGICAL_OPERATOR.EQUAL))
				return "=";
		else if (param.getLogicalOperator().equals(
				iSearchParameter.LOGICAL_OPERATOR.LESS))
				return "<";
		else if (param.getLogicalOperator().equals(
				iSearchParameter.LOGICAL_OPERATOR.LESS_OR_EQUAL))
				return "<=";
		else if (param.getLogicalOperator().equals(
				iSearchParameter.LOGICAL_OPERATOR.BIGGER_OR_EQUAL))
				return ">=";
		return null;
		
	}
	
	/**
	 * 
	 * Cleans the path string
	 * 
	 * @param path the path to clean
	 * 
	 * @return A path string without 
	 */
	private String cleanPath(String path){
		
		if (path.endsWith("/")){
			path = path.substring(0, path.length()-1);
		}
		return path;
	}

	
	/**
	 * 
	 * Converts a timestamp to the xs:dateTime representation
	 * 
	 * @param dateToConvert
	 * @return
	 */
	private String getDateTimeRepresentation(Timestamp dateToConvert){
		
				
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateToConvert);
			
			StringBuilder b = new StringBuilder();
			
			b.append(cal.get(Calendar.YEAR));
			b.append("-");
			
			if (cal.get(Calendar.MONTH) < 10)
				b.append("0"+cal.get(Calendar.MONTH));
			else
				b.append(cal.get(Calendar.MONTH));
			
			b.append("-");
			
			if (cal.get(Calendar.DAY_OF_MONTH) < 10)
				b.append("0"+cal.get(Calendar.DAY_OF_MONTH));
			else
				b.append(cal.get(Calendar.DAY_OF_MONTH));
			
			b.append("T");
			
			if (cal.get(Calendar.HOUR_OF_DAY) < 10)
				b.append("0"+cal.get(Calendar.HOUR_OF_DAY));
			else
				b.append(cal.get(Calendar.HOUR_OF_DAY));
			
			b.append(":");
			
			if (cal.get(Calendar.MINUTE) < 10)
				b.append("0"+cal.get(Calendar.MINUTE));
			else
				b.append(cal.get(Calendar.MINUTE));
			
			b.append(":");
			
			if (cal.get(Calendar.SECOND) < 10)
				b.append("0"+cal.get(Calendar.SECOND));
			else
				b.append(cal.get(Calendar.SECOND));
			
			b.append(".");
			
			b.append("000Z");
			//b.append(cal.get(Calendar.MILLISECOND));
			
			return b.toString();
		
		
	}
	
}
