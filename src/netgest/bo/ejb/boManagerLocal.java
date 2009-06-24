/*Enconding=UTF-8*/
package netgest.bo.ejb;
import javax.ejb.EJBLocalObject;

import netgest.bo.data.DataSet;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectFactory;
import netgest.bo.runtime.boObjectFactoryData;
import netgest.bo.runtime.boRuntimeException;

public interface boManagerLocal extends EJBLocalObject  {

    public boObject createObject(EboContext ctx,long fromClassBoui ) throws boRuntimeException;
    public boObject createObject(EboContext ctx,boObject fromObject) throws boRuntimeException;
    public boObject createObject(EboContext ctx, String newObjName, boObject fromObject) throws boRuntimeException;
    public boObject createObject(EboContext ctx,String name ) throws boRuntimeException;
    public boObject createObject(EboContext ctx,String name, long withBoui ) throws boRuntimeException;
    public boObject createObject(EboContext ctx,String name, boolean modeTemplate) throws boRuntimeException;
    public boObject createObject(EboContext ctx,String name,DataSet data) throws boRuntimeException;
    public boObject createObjectWithParent(EboContext ctx, String name, long parentBoui ) throws boRuntimeException;

    
    public boObject loadObject(EboContext ctx,long boui) throws boRuntimeException;
    public boObject loadObjectAs(EboContext ctx,long boui, String className) throws boRuntimeException;
    public boObject loadObject(EboContext ctx,String name,long boui) throws boRuntimeException;
    public boObject loadObject(EboContext ctx,String boql) throws boRuntimeException;
    public boObject loadObject(EboContext ctx, String name, String sql) throws boRuntimeException;
    public boObject loadObject(EboContext ctx, String boql, Object[] sqlargs) throws boRuntimeException;
    public boObject loadObject(EboContext ctx, String name, String sql, Object[] sqlargs) throws boRuntimeException;
    public boObject loadObject(EboContext ctx, String objName, long boui, DataSet data ) throws boRuntimeException;

    public void 	preLoadObjects( EboContext ctx, long[] bouis ) throws boRuntimeException;
    
    public boObject bindObject(EboContext ctx,String name,DataSet data) throws boRuntimeException;

    public boObject seedObject(EboContext ctx,boObject obj) throws boRuntimeException;

    public String 	getClassNameFromBOUI(EboContext ctx, long boui) throws boRuntimeException;

    public void		registerRemoteBoui( EboContext ctx, long boui, boObjectFactory f, boObjectFactoryData fd );


    public boObject updateObject(EboContext ctx, boObject bobj) throws  boRuntimeException;
    public boObject updateObject(EboContext ctx, boObject bobj, boolean runEvents, boolean forceAllInTrasaction) throws boRuntimeException;

    public void makeAllObject(EboContext eboctx) throws boRuntimeException;
    public void makeAllObject(EboContext eboctx, String schema) throws boRuntimeException;

    public boObject getObjectInContext(EboContext ctx, long boui) throws boRuntimeException;

    public boObject destroyForced(EboContext ctx, boObject bobj) throws boRuntimeException;

    public boObject lookByPrimaryKey(EboContext eboctx, String objectName, Object[] keys) throws boRuntimeException;

    void preLoadObjects(EboContext ctx, DataSet dataSet) throws boRuntimeException;

}