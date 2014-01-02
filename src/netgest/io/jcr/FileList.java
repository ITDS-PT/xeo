/**
 * 
 */
package netgest.io.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import netgest.bo.boConfig;
import netgest.io.iFile;
import netgest.io.iFileList;

/**
 * 
 * Implements a paginated list of {@link iFile} elements, useful to
 * perform searches in the JCR repository
 * 
 * @author PedroRio
 *
 */
public class FileList implements iFileList {

	/**
	 * The query to execute
	 */
	private Query p_query;
	
	/**
	 * The number of records
	 */
	private int p_recordCount;
	
	/**
	 *  The node iterator 
	 */
	private NodeIterator p_nodesIterator; 
	
	/**
	 * The default page size
	 */
	private static final int PAGE_SIZE = 20;
	
	/**
	 * The page size, overrides the default
	 */
	private int p_pageSize;
	
	/**
	 * The number of the current page
	 */
	private long p_currentPage;
	
	/**
	 * The total number of pages of the iterator
	 */
	private long p_numPages;
	
	/**
	 * If the list is supposed to pass to the next page
	 */
	private boolean p_passToNextPage;
	
	/**
	 * The name of the repository used with this FileList
	 */
	private String p_repositoryName;
	
	
	/**
	 * 
	 * Constructor for a default (20 records per page) {@link FileList}
	 * 
	 * @param nodeIterator The node iterator with the records
	 * @param repositoryName The name of the repository used (if null, the default one is used)
	 * 
	 */
	public FileList(NodeIterator nodeIterator, String repositoryName){
		p_nodesIterator = nodeIterator;
		p_pageSize = PAGE_SIZE;
		p_numPages = p_nodesIterator.getSize() / p_pageSize;
		p_repositoryName = getRepositoryName(repositoryName);
		p_passToNextPage = false;
		p_currentPage = 1;
		p_query = null;
	}
	
	/**
	 * 
	 * Constructor from a query
	 * 
	 * @param q The query to execute
	 * @param repositoryName The name of the repository 
	 * @param pageSize The size of the pages
	 * 
	 */
	public FileList(Query q, String repositoryName, int pageSize){
		p_query = q;
		p_pageSize = pageSize;
		p_repositoryName = getRepositoryName(repositoryName);
		p_passToNextPage = false;
		p_currentPage = 1;
		p_nodesIterator = null;
	}
	
	
	
	/**
	 * 
	 * Constructor for a {@link FileList} with a specified page size
	 * 
	 * @param nodeIterator The node iterator with the records
	 * @param repositoryName The name of the repository used (if null, the default one is used)
	 * @param pageSize The page size of the list
	 * 
	 */
	public FileList(NodeIterator nodeIterator, String repositoryName, int pageSize){
		p_nodesIterator = nodeIterator;
		p_pageSize = pageSize;
		p_numPages = p_nodesIterator.getSize() / p_pageSize;
		p_repositoryName = getRepositoryName(repositoryName);
		p_passToNextPage = false;
		p_currentPage = 1;
	}
	
	/**
	 * 
	 * Check if the name of the repository passed in the constructor is valid
	 * (not null)
	 * 
	 * @param name The name of the repository
	 * 
	 * @return If the parameter is not null and represents a valid repository
	 * configured return the parameter, otherwise return the name
	 * of the default repository
	 */
	private String getRepositoryName(String name){
		if (name != null){
			if (boConfig.getApplicationConfig().getFileRepositoryConfiguration(p_repositoryName) != null)
				return name;
		}
		return boConfig.getApplicationConfig().getDefaultFileRepositoryConfiguration().getName();
	}
	
	/* (non-Javadoc)
	 * @see netgest.io.iFileList#beforeFirst()
	 */
	@Override
	public void beforeFirst() 
	{ 
		reset();
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#getFile(int)
	 */
	@Override
	public iFile getFile(int pos) {
		if (p_nodesIterator == null)
			reset();
			
		if (p_nodesIterator.getPosition() == 0)
			p_nodesIterator.skip(pos);
		else
			p_nodesIterator.skip(pos - p_nodesIterator.getPosition());
		Node current = p_nodesIterator.nextNode();
		return new FileJCR(current, p_repositoryName);
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#hasMorePages()
	 */
	@Override
	public boolean hasMorePages() {
		if (p_nodesIterator == null)
			reset();
		
		return p_currentPage <= p_numPages;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (p_nodesIterator == null)
			reset();
		//If we're at the end of a page, no more results
		long pos = p_nodesIterator.getPosition();
		long pagePosition = pos % p_pageSize;
		if ((pagePosition == 0 && pos != 0))
		{
			if (!p_passToNextPage)
				return false;
		}
		return p_nodesIterator.hasNext();
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#isFirstPage()
	 */
	@Override
	public boolean isFirstPage() {
		return p_currentPage == 1;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#isLastPage()
	 */
	@Override
	public boolean isLastPage() {
		return p_currentPage == p_numPages;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#next()
	 */
	@Override
	public iFile next() {
		if (p_nodesIterator == null)
			reset();
		
		boolean canMoveToNext = false;
		long pos = p_nodesIterator.getPosition();
		long pagePosition = pos % p_pageSize;
		if (((pagePosition) != 0 && pos > 0) || //If positioned anywhere
				(pagePosition == 0 && pos == 0) || //If first position 
				p_passToNextPage) //If we changed the page
		{
			canMoveToNext = true;
			//If we got here through a page move, reset the flag
			if (p_passToNextPage)
				p_passToNextPage = false;
		}
			
		if (canMoveToNext){
			Node toReturn = (Node) p_nodesIterator.next();
			return new FileJCR(toReturn,p_repositoryName);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see netgest.io.iFileList#nextPage()
	 */
	@Override
	public void nextPage() {
		if (p_nodesIterator == null)
			reset();
		//If we're not on the last page, advance the cursor
		//to the beginning of the next page
		if (!isLastPage())
		{
			//Move to the "next page"
			p_currentPage++;
			//Advance the cursor accordingly
			long positionsToNextPage = p_nodesIterator.getPosition() % p_pageSize;
			if (positionsToNextPage > 0)
			{
				p_nodesIterator.skip(positionsToNextPage);
				p_passToNextPage = true;
			}
			p_passToNextPage = true;
		}
		else 
			p_currentPage++;
	}

	@Override
	public void setPage(int pageNumber) {
		if (p_nodesIterator == null)
			reset();
		if (p_currentPage <= p_numPages){
			this.p_currentPage = pageNumber;
			p_passToNextPage = true;
		}
		else
			this.p_currentPage = p_numPages;
		
		
	}

	@Override
	public long getRecordCount() {
		return p_recordCount;
	}

	@Override
	public long getCurrentPageNumber() {
		return p_currentPage;
	}

	@Override
	public int getPageSize() {
		return p_pageSize;
	}

	@Override
	public void refresh() {
		reset();
		long skip = p_pageSize * (p_currentPage - 1);
		this.p_nodesIterator.skip(skip);
	}

	
	@Override
	public void reset() {
		try {
			QueryResult r = p_query.execute();
			p_nodesIterator = r.getNodes();
			int count = 0;
			while (p_nodesIterator.hasNext()){
				p_nodesIterator.next();
				count++;
			}
			p_recordCount = count;
			double val =  (double)count / (double) p_pageSize;
			p_numPages =  (long) (Math.ceil(val));
			p_nodesIterator = p_query.execute().getNodes();
		} catch (RepositoryException e) {
			p_nodesIterator = null;
		}
	}
}
