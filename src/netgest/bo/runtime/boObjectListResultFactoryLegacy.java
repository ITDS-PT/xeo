package netgest.bo.runtime;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import netgest.bo.data.DataManager;
import netgest.bo.data.DataResultSet;
import netgest.bo.data.DataRow;
import netgest.bo.data.DataSet;
import netgest.bo.data.ObjectDataManager;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefDatabaseObject;
import netgest.bo.def.boDefHandler;
import netgest.bo.ejb.boManagerLocal;
import netgest.bo.ql.QLParser;
import netgest.bo.system.boApplication;
import netgest.bo.system.boPoolable;

public class boObjectListResultFactoryLegacy implements boObjectFactory {
	
	
	public static long tempBouis = Long.MIN_VALUE + 1;
	
	public static final Hashtable bouiToObject = new Hashtable();
	public static final Hashtable boObjectList_keysToObject = new Hashtable();
	
	public DataResultSet getResultSetByBOQL( boObjectList parentList, EboContext ctx, String boql, Object[] qArgs,
			String orderBy, int page, int pageSize,
			String fullText, String[] p_letter_filter, String userQuery,
			boolean usesecurity ) throws boRuntimeException {
		
		QLParser	  		qp=null;
		boDefDatabaseObject	pk=null;
		String[]			pk_fields=null;
		ArrayList			qList=null;
		int 				bouiColIdx;			
		qp = new QLParser();
		
		String keyInMap = parentList.getBoDef()!=null?parentList.getBoDef().getName():boql; 
		Map objKeysMap = null;
		
		synchronized ( boObjectList_keysToObject ) {
			if( parentList != null )
				objKeysMap = (Map)boObjectList_keysToObject.get( keyInMap );
			
				if( objKeysMap == null ) {
					objKeysMap = new Hashtable();
					boObjectList_keysToObject.put( keyInMap, objKeysMap );
				}
		}
		String sql = qp.toSql( boql, ctx );
		
		if( userQuery!=null && userQuery.length() > 0 ) {
			sql = "select * from (" + sql + ") where " + userQuery;
		}
		
		if( orderBy != null && orderBy.length() > 0 ) {
			sql = "select * from (" + sql + ") ORDER BY " + orderBy;
		}
		
		boDefHandler qdef = qp.getObjectDef();
		
		if( pk == null ) {
			boDefDatabaseObject[] dbobjs = qdef.getBoDatabaseObjects();
			for( int k=0; k < dbobjs.length; k++ ) {
				if( dbobjs[k].getType() == boDefDatabaseObject.DBOBJECT_PRIMARY ) {
					pk = dbobjs[k];
					break;
				}
			}
		}
		
		if( pk_fields == null ) {
			pk_fields = pk.getExpression().split(",");
		}
		
		ArrayList qargsList = null;
		
		if( qArgs != null ) {
			qargsList = new ArrayList();
			qargsList.addAll( Arrays.asList( qArgs ) );
		}
		
		DataSet result = ObjectDataManager.createEmptyObjectDataSet( ctx , qdef );
		
		try {
			DataManager.fecthMoreData( ctx, result,"DATA",sql,page, pageSize, qargsList);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace(); 
		}
		
		bouiColIdx = result.findColumn( "BOUI" );
		
		StringBuffer objName = new StringBuffer( qp.getObjectName() );

		if( qList == null ) {
			 qList = new ArrayList( pk_fields.length );
		}
		
		boManagerLocal manager = boObject.getBoManager();
		
		for(int i=1; i <= result.getRowCount(); i++ ) {
			StringBuffer sb = new StringBuffer();
			for( int k=0; k < pk_fields.length; k++ ) {
				if( qList.size() <= k ) {
					qList.add( result.rows(i).getObject( pk_fields[k].trim() ) );
				}
				else {
					qList.set( k, result.rows(i).getObject( pk_fields[k].trim() ) );
				}
				sb.append( result.rows(i).getObject( pk_fields[k].trim() ) );
				
			}
			
			Long existingObj = (Long)objKeysMap.get( sb.toString() );
			if( existingObj != null ) {
				result.rows( i ).updateLong( bouiColIdx, existingObj.longValue() );
				/*
				manager.registerRemoteBoui( ctx, existingObj.longValue(), this, 
						new boObjectLegacyFactoryData( objName, existingObj.longValue(), qList.toArray() ) );
				*/
			} else {
				long boui;
				synchronized( boObjectListResultFactoryLegacy.class )  { 
					boui = ++tempBouis; 
				};
				objKeysMap.put( sb.toString(), new Long(boui) );
				result.rows( i ).updateLong( bouiColIdx, boui );
				manager.registerRemoteBoui( ctx, boui, this, new boObjectLegacyFactoryData( objName, boui, qList.toArray() ) );
			}
		}
		
		// Preload objects
		try {
			DataSet clonedDataSet = (DataSet)result.clone();
			
			if( pageSize < 1000 ) {
				DataSet[] dataSet = clonedDataSet.split();
				for( int i=0; i < dataSet.length; i++ ) 
				{
					boObject obj = boObject.getBoManager().loadObject(ctx, objName.toString(), dataSet[ i ].rows( 1 ).getLong(bouiColIdx), dataSet[ i ] );
					//bouiToObject.put(  new Long( obj.getBoui() ), obj);

					
					/*String key="";
					for( int k=0; k < pk_fields.length; k++ ) {
						key += dataSet[i].rows( 1 ).getObject( pk_fields[k].trim() );
					}
					if( keysToObject.get( key ) == null ) {
					*/
					/*
					}
					*/
				}
			}
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new DataResultSet( result );
	}
	
	public boObject getObject( EboContext ctx, boObjectFactoryData factoryData ) throws boRuntimeException {
		/*
		boObject ret = ((boObject)bouiToObject.get( new Long( factoryData.getBoui() ) ));
		if( ret != null ) {
			ret.setEboContext( boApplication.currentContext().getEboContext() );
    	}
    	return ret;
		*/
		
		boObject ret = null;
			
		boDefDatabaseObject 	pk = null;
		boDefHandler			qdef = boDefHandler.getBoDefinition( factoryData.getObjectName() );
		
		StringBuffer			pk_sql;
		String[]				pk_fields;
		
		boDefDatabaseObject[] dbobjs = qdef.getBoDatabaseObjects();
		for( int k=0; k < dbobjs.length; k++ ) {
			if( dbobjs[k].getType() == boDefDatabaseObject.DBOBJECT_PRIMARY ) {
				pk = dbobjs[k];
				break;
			}
		}

		pk_sql = new StringBuffer();
		pk_fields = pk.getExpression().split(",");
		for( int i=0; i < pk_fields.length; i++ ) {
			if( i > 0 ) {
				pk_sql.append( " AND " );
			}
			pk_sql.append( pk_fields[i] ).append( '=' ).append( '?' );
		}
		
		ArrayList qList = new ArrayList( pk_fields.length );
		
		Object[] pkData   = (Object[])factoryData.getData();
		
		qList.addAll( Arrays.asList( pkData ) );
		
		DataSet result = ObjectDataManager.createEmptyObjectDataSet( ctx, qdef );
		DataManager.fecthMoreData( ctx, result, "DATA","select * from " + qdef.getBoMasterTable() + " where  " + pk_sql.toString(), 1, Integer.MAX_VALUE,qList );
		
		ret = boObject.getBoManager().loadObject( ctx, qdef.getName(), factoryData.getBoui(), result );
		boObject.getBoManager().registerRemoteBoui(ctx, factoryData.getBoui(), this, factoryData );

		System.out.println( factoryData.getBoui() + "="  + pk_fields[0] + " --> " + pkData[0] );
		
		return ret;
		
	}
	
	public boObject getAttributeObject(EboContext ctx, boObject parent,
			ObjAttHandler att) throws boRuntimeException {
		
		
		Map objKeysMap = null;
		
		synchronized ( boObjectList_keysToObject ) {
			if( parent != null )
				objKeysMap = (Map)boObjectList_keysToObject.get( parent.getName() );
			
				if( objKeysMap == null ) {
					objKeysMap = new Hashtable();
					boObjectList_keysToObject.put( parent.getName(), objKeysMap );
				}
		}
		
		StringBuffer sb = new StringBuffer();
		
		Long existingObj = (Long)objKeysMap.get( sb.toString() );
		
		
		String[] keys 			= att.getDefAttribute().getDbRelationKeys();
		Object[] keysValues		= new Object[ keys.length ];
		
		for( int i=0; i < keys.length; i++ ) {
			AttributeHandler keyAtt = parent.getAttribute( keys[i] );
			if( keyAtt != null ) {
				keysValues[i] = keyAtt.getValueObject();
				sb.append( keysValues[i] );
			}
			else {
				throw new RuntimeException( "Cannot relate object because the attribute [" + keys[i] + "] doesn't exist." );
			}
		}
		
		boObjectLegacyFactoryData fd = (boObjectLegacyFactoryData)objKeysMap.get( sb.toString() );
		if( fd == null ) {
			fd = new boObjectLegacyFactoryData( 
					new StringBuffer( 
							att.getDefAttribute().getReferencedObjectName()), 
							++tempBouis, 
							keysValues 
						);
			objKeysMap.put( sb.toString(), fd );
			boObject.getBoManager().registerRemoteBoui( ctx , fd.getBoui(), this, fd);
		}
		return getObject( ctx, fd );
	}

	public DataSet getBridgeData(EboContext ctx, boObject parent, boDefAttribute att) 
					throws boRuntimeException {
		
		
		boDefHandler refDef = att.getReferencedObjectDef();
		
		boDefDatabaseObject	pk = null;
		
		boDefDatabaseObject[] dbobjs = refDef.getBoDatabaseObjects();
		for( int k=0; k < dbobjs.length; k++ ) {
			if( dbobjs[k].getType() == boDefDatabaseObject.DBOBJECT_PRIMARY ) {
				pk = dbobjs[k];
				break;
			}
		}

		StringBuffer pk_sql = new StringBuffer();
		
		String[] keys 			= att.getDbRelationKeys();
		Object[] keysValues		= new Object[ keys.length ];
		String[] pk_fields 		= pk.getExpression().split(",");
		for( int i=0; i < keys.length; i++ ) {
			
			if( i > 0 ) {
				pk_sql.append( ',' );
			}
			
			pk_sql.append( pk_fields[i] ).append( '=' ).append( '?' );
			AttributeHandler keyAtt = parent.getAttribute( keys[i] );
			if( keyAtt != null ) {
				keysValues[i] = keyAtt.getValueObject();
			}
			else {
				throw new RuntimeException( "Cannot relate object because the attribute [" + keys[i] + "] doesn't exist." );
			}

		}
		
		DataResultSet r = getResultSetByBOQL( null,ctx,
					"select "+ att.getReferencedObjectName() + " where " + pk_sql.toString(), 
					keysValues, 
					"", 
					1, 
					Integer.MAX_VALUE, 
					null, 
					null, 
					null, 
					false
				);
		
		try {
			DataSet retDataSet = ObjectDataManager.createEmptyObjectBridgeDataSet( ctx, parent.getBoDefinition(), att);
			r.beforeFirst();
			while( r.next() ) {
				DataRow dr = retDataSet.createRow();
				dr.updateLong( "PARENT$" , parent.getBoui() );
				dr.updateLong( "CHILD$" , r.getLong("BOUI") );
				retDataSet.insertRow( dr );
			}
			
			
			return retDataSet;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}

	public class boObjectLegacyFactoryData implements boObjectFactoryData {
		private StringBuffer 	objectName;
		private Object[] 		objectKeyValues;
		private long			boui;
		
		public boObjectLegacyFactoryData( StringBuffer objectName, long boui, Object[] objectKeyValues ) {
			this.objectName = objectName;
			this.objectKeyValues = objectKeyValues;
			this.boui = boui;
		}
		
		public String getObjectName() {
			return objectName.toString();
		}
		
		public Object[] getObjectKey() {
			return this.objectKeyValues;
		}
		
		public String serialize() {
			return null;
		}
		
		public long   getBoui() {
			return boui;
		}

		public Object getData() {
			return this.objectKeyValues;
		}
		
		public void deserialize( String data ) {
			
		}
		
	}

}
