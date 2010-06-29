package netgest.bo.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Represents a grid (bridge) from
 * with the rows that were deleted/added
 * 
 * @author Pedro Rio
 *
 */
public class GridFlashBack
{
	
	/**
	 * The list of added rows in the bridge
	 */
	public List<RowGridFlashBack> addedRows;
	
	/**
	 * The list of deleted rows in the bridge
	 */
	public List<RowGridFlashBack> deletedRows;
	
	/**
	 * The list of columns names
	 */
	public List<String> columnNames;
	
	/**
	 * Public constructor
	 */
	public GridFlashBack()
	{
		this.addedRows = new ArrayList<RowGridFlashBack>();
		this.deletedRows = new ArrayList<RowGridFlashBack>();
		this.columnNames = new LinkedList<String>();
	}
	
	/**
	 * 
	 * Add a row that was added to the bridge
	 * 
	 * @param row The added row
	 */
	public void addNewRow(RowGridFlashBack row)
	{
		this.addedRows.add(row);
	}
	
	/**
	 * 
	 * Add a new columns name
	 * 
	 * @param name The name of the column
	 */
	public void addColumnName(String name)
	{
		this.columnNames.add(name);
	}
	
	/**
	 * 
	 * Add a row that was deleted from the bridge
	 * 
	 * @param row The deleted row
	 */
	public void addDeletedRow(RowGridFlashBack row)
	{
		this.deletedRows.add(row);
	}
	
	
	/**
	 * 
	 * Get the list of added rows from the grid
	 * 
	 * @return A list of rows added in the edit process
	 */
	public List<RowGridFlashBack> getAddedRows()
	{
		return addedRows;
	}
	
	/**
	 * 
	 * Get the list of deleted rows from the grid
	 * 
	 * @return A list of rows deleted in the edit process
	 */
	public List<RowGridFlashBack> getDeletedRows()
	{
		return deletedRows;
	}
	
	/**
	 * 
	 * Retrieve the list of column names for each row
	 * 
	 * @return
	 */
	public List<String> getCollumns()
	{
		return columnNames;
	}
	
	
	
}
