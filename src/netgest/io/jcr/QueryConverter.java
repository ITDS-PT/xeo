package netgest.io.jcr;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import netgest.bo.boConfig;
import netgest.bo.configUtils.FileNodeConfig;
import netgest.bo.configUtils.NodePropertyDefinition;
import netgest.bo.configUtils.RepositoryConfig;
import netgest.io.metadata.iSearchParameter;

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
			List<iSearchParameter> properties) {

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
			if (properties.size() > 0)
				b.append("[");
		} else if (properties.size() > 0)
			b.append(" and ");

		while (it.hasNext()) {
			iSearchParameter currentParam = it.next();
			b.append(currentParam.getPropertyName());
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
			return param.getPropertyValue();
		else if (param.getPropertyDataType().equals(iSearchParameter.DATA_TYPE.BOOLEAN))
			return param.getPropertyValue();
		else if (param.getPropertyDataType().equals(iSearchParameter.DATA_TYPE.DATE)){
			
			String correctDate = param.getPropertyValue();
			correctDate = getDateTimeRepresentation(correctDate);
			return "xs:dateTime('"+correctDate+"');";
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

	
	private String getDateTimeRepresentation(String dateToConvert){
		
		try {
			//String format = "2002-05-30T09:00:00.0";
			//String format = "YYYY-MO-DDTHH:MM:SS.;MILI";
			DateFormat df = DateFormat.getDateInstance();
			Date curr = df.parse(dateToConvert);
			Calendar cal = Calendar.getInstance();
			cal.setTime(curr);
			
			StringBuilder b = new StringBuilder();
			
			b.append(cal.get(Calendar.YEAR));
			b.append("-");
			b.append(cal.get(Calendar.MONTH));
			b.append("-");
			b.append(cal.get(Calendar.DAY_OF_MONTH));
			b.append("T");
			b.append(cal.get(Calendar.HOUR_OF_DAY));
			b.append(":");
			b.append(cal.get(Calendar.MINUTE));
			b.append(":");
			b.append(cal.get(Calendar.SECOND));
			b.append(".");
			b.append(cal.get(Calendar.MILLISECOND));
			
			return b.toString();
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
	
}
