/**
 * 
 */
package netgest.io.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import netgest.io.iFile;
import netgest.io.iFileList;

/**
 * 
 * Implements a 
 * 
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
	private static final int PAGE_SIZE = 50;
	
	/**
	 * The page size, overrides the default
	 */
	private int p_pageSize;
	
	private int p_currentPage;
	
	public FileList(NodeIterator nodeIterator){
		p_nodesIterator = nodeIterator;
		
	}
	
	/* (non-Javadoc)
	 * @see netgest.io.iFileList#beforeFirst()
	 */
	@Override
	public void beforeFirst() {	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#getFile(int)
	 */
	@Override
	public iFile getFile(int pos) {
		int p = 0;
		while ( p_nodesIterator.hasNext() && p < pos ){
			p++;
		}
		Node current = p_nodesIterator.nextNode();
			/*return new FileJCR(
					current, configFile, 
					configFolder, session, 
					metadata, isFolder, 
					current.getPath());*/
		return null;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#hasMorePages()
	 */
	@Override
	public boolean hasMorePages() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#isFirstPage()
	 */
	@Override
	public boolean isFirstPage() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#isLastPage()
	 */
	@Override
	public boolean isLastPage() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#next()
	 */
	@Override
	public iFile next() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileList#nextPage()
	 */
	@Override
	public void nextPage() {
		// TODO Auto-generated method stub

	}

}
