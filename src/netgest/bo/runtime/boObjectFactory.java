package netgest.bo.runtime;

import netgest.bo.data.DataSet;
import netgest.bo.def.boDefAttribute;

public interface boObjectFactory {
	
	public boObject getObject( EboContext ctx, boObjectFactoryData factoryData ) 				throws boRuntimeException;
	public boObject getAttributeObject( EboContext ctx, boObject parent, ObjAttHandler att ) 	throws boRuntimeException;
	public DataSet 	getBridgeData( EboContext ctx, boObject parent, boDefAttribute att ) 	throws boRuntimeException;

}
