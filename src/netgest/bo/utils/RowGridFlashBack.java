package netgest.bo.utils;

import java.util.HashMap;

/**
 * 
 * Represents a grid row 
 * 
 * @author Pedro Rio
 *
 */
public class RowGridFlashBack
{
	
	/**
	 * The mapping of column name to value for this row
	 */
	public HashMap<String,String> values;
	
	public RowGridFlashBack(HashMap<String,String> values)
	{
		this.values = values;
	}
	
	/**
	 * 
	 * Get the value of a given column
	 * 
	 * @param name The name of the column
	 * 
	 * @return The value for that collumn
	 */
	public String getRowValue(String name)
	{
		return this.values.get(name);
	}
	
}
