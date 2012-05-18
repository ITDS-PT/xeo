package netgest.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;


public class LovUtils {

	private static final Logger logger = Logger.getLogger( LovUtils.class );
	
	/**
	 * 
	 * Creates a Map from an existing Lov to display in the ChooseLovValue.xvw
	 * 
	 * @param ctx Context to load the map
	 * @param lovName The name of the lov
	 * 
	 * @return A map with the entries of the lov
	 */
	public static Map<String, String> createMapFromLov( EboContext ctx,  String lovName ) {
		Map<String,String> result = new LinkedHashMap<String, String>();
		try {
			lovObject lov = LovManager.getLovObject( ctx, lovName );
			if( lov != null ) {
				lov.beforeFirst();
				while (lov.next()){
					result.put(lov.getCode(), lov.getDescription());
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * Given a lov entry value (or values) and the lov name, load and retrieve the label
	 * 
	 * @param lovName The name of the lov
	 * @param value The value for the entry in the lov (or values, in a comma separated format)
	 * 
	 * @return The corresponding description/label of the value
	 */
	public static String getDescriptionForLovValues(EboContext ctx, String lovName, String values){
		StringBuilder b = new StringBuilder();
		String[] newValues = values.split( "," );
		String toAppend = "";
		try{
			lovObject lov = LovManager.getLovObject( ctx, lovName );
			lov.beforeFirst();
			for (String current : newValues){
				if (StringUtils.hasValue( current )){
					b.append(toAppend);
					b.append(lov.getDescriptionByCode( current.trim() ));
					toAppend = ", ";
				}
			}
		} catch (boRuntimeException e){
			e.printStackTrace();
		}
		return b.toString();
	}
	
}
