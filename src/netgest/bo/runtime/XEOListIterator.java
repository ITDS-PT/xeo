package netgest.bo.runtime;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class XEOListIterator implements Iterator< boObject > {

	private boObjectList results;
	private int index = 0;

	public XEOListIterator( final boObjectList list ) {
		this.results = list;
		this.results.beforeFirst();
	}
	
	@Override
	public boolean hasNext() {
		boolean ret = index < results.getRowCount();
		if( !ret ) {
			ret = results.haveMorePages();
		}
		return ret;
	}

	@Override
	public boObject next() {
		if( hasNext() ) {
			index++;
			if( index > results.getRowCount() ) {
				this.results.nextPage();
				if ( this.results.getRowCount() <= 0 ) {
					throw new ConcurrentModificationException();					
				}
				index = 1;
			}
			this.results.moveTo( index );
			try {
				return results.getObject();
			} catch (boRuntimeException e) {
				throw new RuntimeException( e );
			}
		}
		else {
			throw new NoSuchElementException();
		}
	}
	
	@Override
	public void remove() { throw new RuntimeException( "Remove not implemented" );	}



}

