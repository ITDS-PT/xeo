package netgest.bo.data;

import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.ObjAttHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

public interface IXEODataManager {
	
	public void fillObjectDataSet( EboContext ctx, DataSet emptyDataSet, XEODataManagerKey factoryData ) throws boRuntimeException;
	
	public XEODataManagerKey getKeyForAttribute( EboContext ctx, boObject parent, boDefAttribute att ) throws boRuntimeException;
	
	public void fillBridgeDataSet( EboContext ctx, DataSet emptyDataSet, boObject parent, boDefAttribute att ) throws boRuntimeException;
	
	public void fillDataSetByBOQL( 
			EboContext ctx,
			DataSet emptyDataSet,
			boObjectList parentList, 
			String boql, 
			Object[] qArgs, 
			String orderBy, 
			int page, 
			int pageSize,
			String fullText, 
			String[] p_letter_filter, 
			String userQuery,
			boolean usesecurity  
		) throws boRuntimeException;
	
	public long getRecordCountByBOQL(boObjectList parentList, 
			EboContext ctx, 
			String boql, 
			Object[] qArgs, 
			String fullText, 
			String[] p_letter_filter, 
			String userQuery,
			boolean usesecurity  
	);
	
	public void updateDataSet( EboContext ctx, DataSet emptyDataSet, boObject object ) throws boRuntimeException;
	
	public void destroyDataSet( EboContext ctx, DataSet emptyDataSet, boObject object ) throws boRuntimeException;
	
	
}
