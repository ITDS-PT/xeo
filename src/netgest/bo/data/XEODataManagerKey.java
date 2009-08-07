package netgest.bo.data;

import netgest.bo.plugins.IDataManager;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
/**
 * This abstract class is used in conjunction with {@linkplain IXEODataManagerKey} to hold the data necessary
 * to obtain remote managed XEO Model instance.
 * 
 * This class creates a register a valid BOUI. When XEO queries this type of BOUI a {@link IDataManager} is called with this class 
 * as a parameter.
 * 
 *  This Class is used to store Primary Keys or other need information to obtain the data of a object.
 * 
 *
 */
public abstract class XEODataManagerKey {
	
	private IXEODataManager		dataManager  = null;
	private	long				boui;
	private String				objectName;

	/**
	 * The constructor for this class
	 * @param dm
	 * 		The {@link IXEODataManager} who this key belongs
	 * @param objectName
	 * 		The XEO Model name witch this key refers 
	 */
	public XEODataManagerKey( IXEODataManager dm, String objectName ) {
		this.dataManager = dm;
		this.objectName = objectName;
	}
	
	/**
	 *	Getter for the XEO Model name
	 * @return
	 * 		Returns the XEO Model name of the Key 
	 */
	public final String getObjectName() {
		return this.objectName;
	}
	
	/**
	 * Getter for the generated BOUI. This BOUI is only generated after the method registerKey() is called
	 * @return
	 * 		Return the generated BOUI
	 */
	public final long getBoui() {
		return boui;
	}
	
	/**
	 * A setter for the BOUI associated with this {@link XEODataManagerKey}
	 * @param boui
	 * 		Set the BOUI associated with this Key. This method is only for internal used.
	 */
	
	public final void setBoui( long boui ) {
		this.boui = boui;
	}
	
	/**
	 * 
	 * @return
	 * 		Returns the {@link IXEODataManager} associated with this Key
	 */
	public final IXEODataManager getDataManager() {
		return  this.dataManager;
	}
	
	/**
	 * This method returns the user specific data to locate and load a XEO Model instance Data
	 * @return
	 * 		the data defined by the implementation
	 */
	public abstract Object getData();
	
	/**
	 * This method is called to transform the data need to locate and load a XEO Model into a valid String
	 * This String is used also to avoid duplicate BOUI's for the same objects. For the same object data this String must be equal.
	 * @return
	 */
	public abstract String serialize();

	/**
	 * This method is called deserialize a previous serialized string with the method serialize
	 * This String is used also to avoid duplicate BOUI's for the same objects. For the same object data this String must be equal.
	 * 	
	 */
	public abstract void deserialize( String data );

	/**
	 * Register a Key a associate a new temporary BOUI for this Key
	 * @param ctx
	 * 		The current {@link EboContext} involved in the request.
	 */
	public void registerKey( EboContext ctx ) {
		try {
			boObject.getBoManager().registerRemoteKey( ctx, this );
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}
	
}
