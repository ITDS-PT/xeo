/**
 * 
 */
package netgest.io.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

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
		//Cannot be implemented
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#getFile(int)
	 */
	@Override
	public iFile getFile(int pos) {
		if (p_nodesIterator.getPosition() > 0)
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
		return p_currentPage < p_numPages;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#hasNext()
	 */
	@Override
	public boolean hasNext() {
		//If we're at the end of a page, no more results
		if (p_nodesIterator.getPosition() % p_pageSize == 1)
			return false;
		else //In the middle of a page, return the next result
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
		boolean canMoveToNext = false;
		if ((p_nodesIterator.getPosition() % p_pageSize) != 1)
			canMoveToNext = true;
			
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
		//If we're not on the last page, advance the cursor
		//to the beginning of the next page
		if (!isLastPage())
		{
			//Move to the "next page"
			p_currentPage++;
			//Advance the cursor accordingly
			long positionsToNextPage = p_nodesIterator.getPosition() % p_pageSize;
			if (positionsToNextPage > 0)
				p_nodesIterator.skip(positionsToNextPage);
		}
	}

}
