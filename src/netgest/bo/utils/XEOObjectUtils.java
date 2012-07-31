package netgest.bo.utils;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boRuntimeException;

public class XEOObjectUtils {

	
	public String getStringRepresentation(AttributeHandler handler){
		try {
			if (handler.getObject() != null){
				return handler.getObject().getTextCARDID().toString();
			}
			return handler.getValueString();
		} catch ( boRuntimeException e ) { 
			return "";
		}
	}
	
}
