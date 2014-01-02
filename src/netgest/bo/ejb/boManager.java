/*Enconding=UTF-8*/
package netgest.bo.ejb;
import java.rmi.RemoteException;

import javax.ejb.EJBObject;

import netgest.bo.data.DataSet;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

public interface boManager extends EJBObject  {

    public boObject createObject(EboContext ctx,String name) throws boRuntimeException,RemoteException;
    public boObject createObject(EboContext ctx,boObject fromObject) throws boRuntimeException,RemoteException;
    public boObject createObject(EboContext ctx, String newObjName, boObject fromObject) throws boRuntimeException,RemoteException;
    public boObject createObject(EboContext ctx,String name, boolean modeTemplate) throws boRuntimeException,RemoteException;
    public boObject createObject(EboContext ctx,long fromClassBoui ) throws boRuntimeException,RemoteException;
    public boObject createObject(EboContext ctx,String name,DataSet data) throws boRuntimeException,RemoteException;
    
    public boObject loadObject(EboContext ctx,long boui) throws boRuntimeException,RemoteException;
    public boObject loadObjectAs(EboContext ctx,long boui, String className) throws RemoteException, boRuntimeException;
    public boObject loadObject(EboContext ctx,String boql) throws RemoteException, boRuntimeException;
    public boObject loadObject(EboContext ctx,String name,long boui) throws boRuntimeException,RemoteException;
    public boObject loadObject(EboContext ctx, String name, String sql) throws RemoteException, boRuntimeException;
    public boObject loadObject(EboContext ctx, String boql, Object[] sqlargs) throws RemoteException, boRuntimeException;
    public boObject loadObject(EboContext ctx, String name, String sql, Object[] sqlargs) throws RemoteException, boRuntimeException;    

    public void preLoadObjects( EboContext ctx, long[] bouis ) throws RemoteException, boRuntimeException;

    public boObject bindObject(EboContext ctx,String name,DataSet data) throws boRuntimeException,RemoteException;

    public boObject seedObject(EboContext ctx,boObject obj) throws RemoteException,boRuntimeException;

    public String getClassNameFromBOUI(EboContext ctx, long boui) throws RemoteException, boRuntimeException;



    public boObject updateObject(EboContext ctx, boObject bobj) throws  RemoteException, boRuntimeException;
    public boObject updateObject(EboContext ctx, boObject bobj, boolean runEvents, boolean forceAllInTrasaction) throws RemoteException,boRuntimeException;



    public void makeAllObject(EboContext eboctx) throws boRuntimeException,RemoteException;

    public boObject destroyForced(EboContext ctx, boObject bobj) throws RemoteException, boRuntimeException;

    public boObject lookByPrimaryKey(EboContext eboctx, String objectName, Object[] keys) throws RemoteException, boRuntimeException;

    void preLoadObjects(EboContext ctx, DataSet dataSet) throws RemoteException, boRuntimeException;



}