package netgest.bo.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefDatabaseObject;
import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

public class XEODataManagerForLegacyTables implements IXEODataManager {
	
	public void fillDataSetByBOQL(
				EboContext ctx, 
				DataSet emptyDataSet,
				boObjectList parentList, 
				String boql, 
				Object[] qArgs,
				String orderBy, 
				int page, int pageSize, 
				String fullText,
				String[] pLetterFilter, 
				String userQuery, boolean usesecurity
			) throws boRuntimeException {
		
		QLParser	  		qp=null;
		boDefDatabaseObject	pk=null;
		String[]			pk_fields=null;
		ArrayList			qList=null;
		int 				bouiColIdx;			
		qp = new QLParser();
		
		String sql = qp.toSql( boql, ctx );
		if( userQuery!=null && userQuery.length() > 0 ) {
			sql = "select * from (" + sql + ") AS SQL_QUERY where " + userQuery;
		}
		
		if( orderBy != null && orderBy.length() > 0 ) {
			sql = "select * from (" + sql + ") AS SQL_QUERY ORDER BY " + orderBy;
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
		
		try {
			DataManager.fecthMoreData( ctx, emptyDataSet ,"DATA",sql,page, pageSize, qargsList);
		} catch (Exception e1) {
			e1.printStackTrace(); 
		}
		
		bouiColIdx = emptyDataSet.findColumn( "BOUI" );
		
		String objName = qp.getObjectName();

		if( qList == null ) {
			 qList = new ArrayList( pk_fields.length );
		}
		
		for(int i=1; i <= emptyDataSet.getRowCount(); i++ ) {
			StringBuffer sb = new StringBuffer();
			for( int k=0; k < pk_fields.length; k++ ) {
				if( qList.size() <= k ) {
					qList.add( emptyDataSet.rows(i).getObject( pk_fields[k].trim() ) );
				}
				else {
					qList.set( k, emptyDataSet.rows(i).getObject( pk_fields[k].trim() ) );
				}
				sb.append( emptyDataSet.rows(i).getObject( pk_fields[k].trim() ) );
				
			}
			
			boObjectLegacyFactoryData fd = new boObjectLegacyFactoryData( this, objName, qList.toArray() );
			fd.registerKey( ctx );
			emptyDataSet.rows( i ).updateLong( bouiColIdx, fd.getBoui() );
			
		}
		
		// Preload objects.. optimization
		try {
			DataSet clonedDataSet = (DataSet)emptyDataSet.clone();
			
			if( pageSize < 1000 ) {
				DataSet[] dataSet = clonedDataSet.split();
				for( int i=0; i < dataSet.length; i++ ) 
				{
					boObject.getBoManager().loadObject(ctx, objName.toString(), dataSet[ i ].rows( 1 ).getLong(bouiColIdx), dataSet[ i ] );
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void fillObjectDataSet(EboContext ctx, DataSet emptyDataSet, XEODataManagerKey factoryData) 
						throws boRuntimeException {

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
		
		DataManager.fecthMoreData( ctx, emptyDataSet, "DATA","select * from " + qdef.getBoMasterTable() + " where  " + pk_sql.toString(), 1, Integer.MAX_VALUE,qList );
		
	}
	
	
	public XEODataManagerKey getKeyForAttribute(EboContext ctx,
			boObject parent, boDefAttribute att)
			throws boRuntimeException {
		
		StringBuffer sb = new StringBuffer();
		String[] keys 			= att.getDbRelationKeys();
		Object[] keysValues		= new Object[ keys.length ];
		
		for( int i=0; i < keys.length; i++ ) {
			AttributeHandler keyAtt = parent.getAttribute( keys[i] );
			if( keyAtt != null ) {
				keysValues[i] = keyAtt.getValueObject();
				sb.append( keysValues[i] );
			}
			else {
				throw new RuntimeException(  MessageLocalizer.getMessage("CANNOT_RELATE_BECAUSE_THE_ATTRIBUTE")+" [" + keys[i] + "] "+MessageLocalizer.getMessage("DOESNT_EXIST")+"." );
			}
		}
		
		boObjectLegacyFactoryData fd = (boObjectLegacyFactoryData)
				new boObjectLegacyFactoryData(
						this,
						att.getReferencedObjectName(), 
						keysValues 
					);
		return fd;
	}
	
	
	public void fillBridgeDataSet(EboContext ctx, DataSet emptyDataSet,
			boObject parent, boDefAttribute att) throws boRuntimeException {
		
		
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
		
		
		if( keys != null && pk != null ) {
			
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
					throw new RuntimeException( MessageLocalizer.getMessage("CANNOT_RELATE_BECAUSE_THE_ATTRIBUTE")+" [" + keys[i] + "] "+MessageLocalizer.getMessage("DOESNT_EXIST")+"." );
				}
	
			}
			
			DataSet tempDataSet = ObjectDataManager.createEmptyObjectDataSet(ctx, att.getReferencedObjectDef() );
			fillDataSetByBOQL( ctx,
					tempDataSet,
					null,
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
	
			DataResultSet r = new DataResultSet( tempDataSet ); 
			
			try {
				r.beforeFirst();
				while( r.next() ) {
					DataRow dr = emptyDataSet.createRow();
					dr.updateLong( "PARENT$" , parent.getBoui() );
					dr.updateLong( "CHILD$" , r.getLong("BOUI") );
					emptyDataSet.insertRow( dr );
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public class boObjectLegacyFactoryData extends XEODataManagerKey {
		private String			serializedString;
		private Object[] 		objectKeyValues;
		
		public boObjectLegacyFactoryData( IXEODataManager dm, String objectName, Object[] objectKeyValues ) {
			super( dm, objectName );
			this.objectKeyValues = objectKeyValues;
		}
		
		public Object[] getObjectKey() {
			return this.objectKeyValues;
		}
		
		public String serialize() {
			if( serializedString == null ) {
				StringBuffer sb = new StringBuffer();
				for( int i=0; i < objectKeyValues.length; i++ ) {
					if( i > 0 ) {
						sb.append( "||" );
					}
					sb.append( objectKeyValues[i] );
				}
				serializedString = sb.toString();
			}
			return serializedString;
		}
		
		public Object getData() {
			return this.objectKeyValues;
		}
		
		public void deserialize(String data) {
			this.objectKeyValues = data.split( "\\|\\|" );
		}
		
	}

	public long getRecordCountByBOQL(
			EboContext ctx,
			boObjectList parentList, 
			String boql, 
			Object[] qArgs, 
			String fullText,
			String[] pLetterFilter, 
			String userQuery, 
			boolean usesecurity
		) {
		
		long ret = 0;
		
		QLParser	  		qp=null;
		qp = new QLParser();
		
		String sql = qp.toSql( boql, ctx );
		if( userQuery!=null && userQuery.length() > 0 ) {
			sql = "select count(*) from (" + sql + ") COUNT where " + userQuery;
		}
		else {
			sql = "select count(*) from (" + sql + ") COUNT";
		}
		
		PreparedStatement pstm = null;
		ResultSet		  rslt = null;
		
		try {
			pstm = ctx.getConnectionData().prepareStatement( sql );
			if( qArgs != null ) {
				for( int i=0; i < qArgs.length; i++ ) {
					pstm.setObject(  i+1, qArgs[i] );
				}
			}
			rslt = pstm.executeQuery();
			rslt.next();
			ret = rslt.getLong( 1 );
		}
		catch( SQLException e ) {
			throw new RuntimeException( e );
		}
		finally {
			try {
				if( pstm != null )
					pstm.close();
				if( rslt != null )
					rslt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
		
	}

	

	public void destroyDataSet(EboContext ctx, DataSet emptyDataSet, boObject object) 
								throws boRuntimeException {
		
		System.out.print( MessageLocalizer.getMessage("DESTROY_OBJECT"));
		
	}


	public void updateDataSet(EboContext ctx, DataSet emptyDataSet,
			boObject object) 	throws boRuntimeException {

		System.out.print( MessageLocalizer.getMessage("UPDATE_OBJECT"));
		
	}

}
