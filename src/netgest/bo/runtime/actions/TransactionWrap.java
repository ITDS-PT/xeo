package netgest.bo.runtime.actions;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.XEO;

/**
 * Class that allows to run code inside a transaction, to do that
 * you can extend the class and override the doWork method
 * You can also override the {@link #onCommit()} method to perform
 * actions after the transaction commit and the {@link #onRollBack(Exception)} method 
 * to perform anything after the transaction rollback 
 * 
 */
public abstract class TransactionWrap {
	
	
	protected static final Logger logger = Logger.getLogger( TransactionWrap.class );
	
	
	public TransactionWrap(Object... args){
		this( XEO.getCurrentContext(), args );
	}
	
	public TransactionWrap(EboContext ctx, Object... args){
		try{
			ctx.beginContainerTransaction();
			doWork(args);
			ctx.commitContainerTransaction();
			onCommit(args);
		} catch (Exception e){
			try {
				ctx.rollbackContainerTransaction();
				onRollBack( e, args );
			} catch ( boRuntimeException e1 ) {
				logger.warn( "Could not rollback transaction from user %s" , e );
			}
		}
		
		
	}
	
	/**
	 * Implement this method to perform the intended actions
	 * @param args Arguments passed in the constructor
	 * */
	public abstract void doWork(Object... args);
	
	/**
	 * Method invoked if the transaction was successfully committed
	 */
	public void onCommit(Object... args){}
	
	/**
	 * Method invoked if the transaction was rollback'd
	 */
	public void onRollBack(Exception e, Object... args){}

}
