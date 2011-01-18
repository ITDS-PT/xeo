package netgest.io;


/**
 * 
 * Represents a list of paginated {@link iFileXEO} 
 * 
 * @author PedroRio
 *
 */
public interface iFileList {

	
	/**
	 * Positions the cursor in the beginning of the list (first page)
	 */
	public void beforeFirst();
	
	/**
	 * 
	 * Checks if there's a next {@link iFileXEO} in the position
	 * after the cursor's current position 
	 * 
	 * @return True if there's a next {@link iFileXEO} element
	 * and false otherwise
	 * 
	 */
	public boolean hasNext();
	
	/**
	 * 
	 * Advances the cursor to the next {@link iFileXEO} in the current
	 * page in the list
	 * 
	 * @return The next {@link iFileXEO} in current page in the list or null if
	 * the cursor was in the last position of the page
	 */
	public iFile next();
	
	/**
	 * 
	 * Checks if the list has more pages that the one
	 * where the cursor is in
	 * 
	 * @return True if there are more pages and false otherwise
	 */
	public boolean hasMorePages();
	
	/**
	 * Advances the cursor to the next available page (if there's one)
	 */
	public void nextPage();
	
	/**
	 * 
	 * Checks whether the cursor is in the last page of the list
	 * 
	 * @return True if the cursor is in the last page of the list
	 * and false otherwise
	 */
	public boolean isLastPage();
	
	/**
	 * 
	 * Checks whether the cursor is in the first page of the list
	 * 
	 * @return  True if the cursor is in the first page of the list
	 * and false otherwise
	 */
	public boolean isFirstPage();
	
	/**
	 * 
	 * Get a file given its position in the list
	 * (advances the cursor to the position) 
	 * 
	 * @param pos The position in the list
	 * 
	 * @return A reference to the {@link iFileXEO} if it exists, or null
	 * the position is not valid
	 */
	public iFile getFile(int pos);
	
	/**
	 * 
	 * Retrieves the number of records
	 * 
	 * @return The number of elements
	 */
	public long getRecordCount();
	
	/**
	 * Resets the file list
	 */
	public void reset();
	
	/**
	 * 
	 * Change the page number
	 * 
	 * @param pageNumber The page number where the file list should
	 * be
	 */
	public void setPage(int pageNumber);
	
	/**
	 * 
	 * Retrieves the current page number
	 * 
	 * @return The current page number
	 */
	public long getCurrentPageNumber();
	
	
	/**
	 * Refreshes the current data, should be used when the
	 * page number is changed) 
	 * 
	 */
	public void refresh();
	
	/**
	 * 
	 * Retrieves the page size (the number of
	 * items per page)
	 * 
	 * @return The number of items per page
	 */
	public int getPageSize();
	
}
