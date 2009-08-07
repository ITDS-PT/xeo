package netgest.bo.data;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

public abstract class XEODataManagerKey {
	private IXEODataManager		dataManager  = null;
	private	long				boui;
	private String				objectName;
	
	public XEODataManagerKey( IXEODataManager dm, String objectName ) {
		this.dataManager = dm;
		this.objectName = objectName;
	}
	
	public final String getObjectName() {
		return this.objectName;
	}
	
	public final long getBoui() {
		return boui;
	}
	
	public final void setBoui( long boui ) {
		this.boui = boui;
	}
	
	public final IXEODataManager getDataManager() {
		return  this.dataManager;
	}

	public abstract Object getData();
	public abstract String serialize();
	public abstract void deserialize( String data );
	
	public void registerKey( EboContext ctx ) {
		try {
			boObject.getBoManager().registerRemoteKey( ctx, this );
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}
	
}
