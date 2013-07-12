package netgest.bo.system;

import netgest.bo.def.boDefHandler;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectListBuilder;
import netgest.bo.runtime.boRuntimeException;

/**
 * Facade for the most common operations regarding instances of XEO Models
 */
public class XEO {
	
	static EboContext getEboContext(){
		return boApplication.currentContext().getEboContext();
	}
	
	//boObject Methods
	public static boObject loadWithQuery(String boql, Object... args) throws boRuntimeException {
		return boObject.getBoManager().loadObject( getEboContext() , boql , args );
	}
	
	public static boObject loadWithQuery(EboContext ctx, String boql, Object... args) throws boRuntimeException {
		return boObject.getBoManager().loadObject( ctx, boql , args );
	}
	
	public static boObject load(long boui) throws boRuntimeException {
		return boObject.getBoManager().loadObject( getEboContext() , boui );
	}
	
	public static boObject load(EboContext ctx, long boui) throws boRuntimeException {
		return boObject.getBoManager().loadObject( ctx, boui );
	}
	
	public static boObject create(String objectName) throws boRuntimeException {
		return boObject.getBoManager().createObject( getEboContext() , objectName );
	}
	
	public static boObject create(EboContext ctx, String objectName) throws boRuntimeException {
		return boObject.getBoManager().createObject( ctx , objectName );
	}
	
	public static boObject createWithParent(String objectName, long parent) throws boRuntimeException {
		return boObject.getBoManager().createObjectWithParent( getEboContext() , objectName, parent );
	}
	
	public static boObject createWithParent(EboContext ctx, String objectName, long parent) throws boRuntimeException {
		return boObject.getBoManager().createObjectWithParent( ctx , objectName, parent );
	}
	
	// boObjectList Methods
	public static boObjectList list(String boql){
		return boObjectList.list( getEboContext() , boql );
	}
	
	public static boObjectList list(EboContext ctx, String boql){
		return boObjectList.list( ctx , boql );
	}
	
	public static boObjectList list(String boql, Object... args){
		return boObjectList.list( getEboContext() , boql,  args );
	}
	
	public static boObjectList list(EboContext ctx, String boql, Object... args){
		return boObjectList.list( ctx , boql,  args );
	}
	
	public static boObjectListBuilder builder(String boql){
		return boObjectList.builder( boql );
	}
	
	//Lov Object Methods
	public static lovObject getLov(String lovName) throws boRuntimeException {
		return LovManager.getLovObject( getEboContext() , lovName );
	}
	
	public static lovObject getLov(EboContext ctx, String lovName) throws boRuntimeException {
		return LovManager.getLovObject( ctx , lovName );
	}
	
	
	//boDefHandler Methods
	public static boDefHandler getMetadata(String objectName){
		return boDefHandler.getBoDefinition( objectName );
	}
	
}
