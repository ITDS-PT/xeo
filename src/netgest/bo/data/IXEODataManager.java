package netgest.bo.data;

import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
/**
 * 
 * This interface is used when in a XEO Model on the tag general/databaseManager\@dataManagerClass a class name is specified and
 * the tag \@xeoCompatible is set to <code>false<code>
 * 
 * This combination instructs XEO to use this interface to obtain data for the XEO Model Instances.
 * 
 * This class must fill the dataSets with the proper data for the XEO Model and create a temporary BOUI. This
 * temporary BOUI is used to refer the same object in the context of requests. When the context finish the temporary BOUI's 
 * are released, and the next time a new is created.
 * 
 * The temporary BOUI's are a long value starting in Long.MIN_VALUE + 1000 in increments of one.
 * 
 * To create a valid registered BOUI a class must extend {@link netgest.bo.data.XEODataManagerKey} and call the method registerKey( EboContext );
 * After that the BOUI field is filled with a valid register BOUI. TO get the generated BOUI call the method getBoui()
 * 
 * @author jcarreira
 *
 */
public interface IXEODataManager {

	/**
	 * This method is called to fill a DataSet for a instance of a XEO Model. 
	 * 
	 * This behavior occurs always when a external managed XEO Model instance is needed
	 * 
	 * @param ctx 
	 * 			The current <code>EboContext</code> involved in this request
	 * 
	 * @param emptyDataSet 
	 * 			A empty DataSet with the structure of the requested XEO Model.
	 * 
	 * @param dataManagerKey 
	 * 			The key reference previous created by the IXEODataManager implementation  
	 * 
	 * @throws boRuntimeException
	 * 			Throws a boRuntimeException if a error occurs filling the data.
	 */
	public void fillObjectDataSet( EboContext ctx, DataSet emptyDataSet, XEODataManagerKey dataManagerKey ) throws boRuntimeException;
	
	/**
	 * This method is called, when XEO need's a instance of a external data managed XEO Model referenced by a AttributeObject. The return key
	 * is used to obtain the object data referenced by this attribute.
	 * 
	 * This behavior occurs when a AttributeObject that refer a external data managed XEO Model
	 * 
	 * @param ctx
	 * 			The current <code>EboContext</code> involved in this request
	 * @param parent
	 * 			The XEO Model instance that owns the AttributeObject
	 * @param att
	 * 			
	 * The AttributeObject definition
	 * @return
	 * 			Returns a instance of a <code>XEODataManagerKey</code>
	 * @throws boRuntimeException
	 */
	public XEODataManagerKey getKeyForAttribute( EboContext ctx, boObject parent, boDefAttribute att ) throws boRuntimeException;
	
	/**
	 * This method is called when a AttributeCollection of a external data managed XEO Model is needed. This method must return a <code>DataSet</code> filled
	 * with the record of the bridge.
	 * 
	 *  This method must fill the column PARENT$ with the BOUI of the <code>parent</code> and 
	 *  the CHILD$ with a valid BOUI or registered remote BOUI.
	 * 
	 * @param ctx
	 * 		The current <code>EboContext</code> involved in this request
	 * @param emptyDataSet
	 * 		A empty DataSet with the structure of the AttributeCollection.
	 * @param parent
	 * 		The XEO Model instance than owns the AttributeCollection ( parent )
	 * @param att
	 * 		The attribute definition of the AttributeCollection
	 * @throws boRuntimeException
	 */
	public void fillBridgeDataSet( EboContext ctx, DataSet emptyDataSet, boObject parent, boDefAttribute att ) throws boRuntimeException;
	
	
	/**
	 * This method is called when a list of external managed XEO Models is needed. This method must fill the data set with the results obtained 
	 * by the <code>boql<code> parameter.
	 * 
	 * The column BOUI of the DataSet must be filled with a valid registered BOUI. 
	 *  
	 * @param ctx
	 * 		The current <code>EboContext</code> involved in this request
	 * @param emptyDataSet
	 * 		A empty DataSet with the structure of the requested XEO Model.
	 * @param parentList
	 * 		The boObjectList
	 * @param boql
	 * 		The XEOQL (BOQL) issued by the user or routine
	 * @param qArgs
	 * 		The SQL arguments for the query
	 * @param orderBy
	 * 		The order by of the result
	 * @param page
	 * 		The page requested
	 * @param pageSize
	 * 		The size of each page
	 * @param fullText
	 * 		The fulltext search to filter results
	 * @param p_letter_filter
	 * 		A String in on letter to filter the results. 
	 * @param userQuery
	 * 		A user query to filter the results
	 * @param usesecurity
	 * 		If the result must be filtered with the security permissions of each XEO Model Instance.
	 * @throws boRuntimeException
	 * 		When a error occurs
	 */
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
	
	
	/**
	 * This mehtod is called to get a count of a XEOQL query
	 * @param ctx
	 * 			The current <code>EboContext</code> involved in this request
	 * @param parentList
	 * 			The boObjectList involved
	 * @param boql
	 * 			The XEOQL (BOQL) issued by the user or routine
	 * @param qArgs
	 * 			The SQL arguments for the query
	 * @param fullText
	 * 			The fulltext search to filter results
	 * @param p_letter_filter
	 * 			A String in on letter to filter the results.
	 * @param userQuery
	 * 			A user query to filter the results
	 * @param usesecurity
	 * 			If the result must be filtered with the security permissions of each XEO Model Instance.
	 * @return	
	 * 			The total records of the query
	 */
	public long getRecordCountByBOQL(
			EboContext ctx, 
			boObjectList parentList, 
			String boql, 
			Object[] qArgs, 
			String fullText, 
			String[] p_letter_filter, 
			String userQuery,
			boolean usesecurity  
	);
	
	/**
	 * This method is called to persist the data of the a XEO Model instance.
	 * 
	 * @param ctx
	 * 		The current <code>EboContext</code> involved in this request
	 * @param dataSet
	 * 		The <code>DataSet</code> of the object data
	 * @param object
	 * 		The current XEO Model instance who the data belongs
	 * @throws boRuntimeException
	 * 		If a a error occurs
	 */
	public void updateDataSet( EboContext ctx, DataSet dataSet, boObject object ) throws boRuntimeException;
	
	/**
	 * This method is called to destroy the data of the a XEO Model instance.
	 * 
	 * @param ctx
	 * 		The current <code>EboContext</code> involved in this request
	 * @param dataSet
	 * 		The <code>DataSet</code> of the object data
	 * @param object
	 * 		The current XEO Model instance who the data belongs
	 * @throws boRuntimeException
	 * 		If a a error occurs
	 */
	public void destroyDataSet( EboContext ctx, DataSet dataSet, boObject object ) throws boRuntimeException;
	
	
}
