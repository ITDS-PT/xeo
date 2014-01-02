package netgest.bo.runtime;


/**
 * 
 * ObjectListManager allows to create instances of boObjectList
 * by issuing BOQL (XEOQL) queries against the datasource.
 * boObjectList instances are paginated lists of {@link boObject}
 *  
 * 
 */
public class ObjectListManager {

	public static boObjectList list(EboContext ctx, long boui)
			throws boRuntimeException {
		return boObjectList.list(ctx, boui);
	}

	public static boObjectList list(EboContext ctx, long[] boui, int page,
			int pagesize, String orderby) throws boRuntimeException {
		return boObjectList.list(ctx, boui,page,pagesize,orderby);
	}

	public static boObjectList list(EboContext ctx, String boql) {
		return boObjectList.list(ctx, boql);
	}
	
	public static boObjectList list(EboContext ctx, String boql,
			boolean cache, Object[] boqlargs) {
			return boObjectList.list(ctx,boql,cache,boqlargs);
			
	}

	/**
	 * Devolve um boObjectList em resultado do boql.
	 * 
	 * @param ctx
	 *            , Contexto
	 * @param boql
	 *            , boql a executar
	 * @param useSecurity
	 *            , TRUE se é para usar seguranças, FALSE caso contrário
	 * @param cache
	 *            , TRUE se é para por na cache, FALSE caso contrário
	 */
	public static boObjectList list(EboContext ctx, String boql,
			boolean useSecurity, boolean cache) {
		return boObjectList.list(ctx, boql,useSecurity, cache);
	}
	
	

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			boolean useSecurity, boolean cache) {
		return boObjectList.listWFirstRows(ctx, boql, useSecurity);
	}

	public static boObjectList list(EboContext ctx, String boql, int page) {
		return boObjectList.list(ctx, boql,page);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, boolean useSecurity) {
		return boObjectList.list(ctx,boql);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs) {
		return boObjectList.list(ctx,boql,boqlargs);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page) {
		return boObjectList.list(ctx,boql,boqlargs,page);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize) {
		return boObjectList.list(ctx,boql,page,pagesize);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, boolean useSecurity) {
		return boObjectList.list(ctx,boql,boqlargs,page,pagesize,useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby) {
		return boObjectList.list(ctx,boql,boqlargs,page,pagesize,orderby);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby,
			boolean useSecurity) {
		return boObjectList.list(ctx,boql,boqlargs,page,pagesize,orderby);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby,
			boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby) {
		return boObjectList.list(ctx,boql,page,pagesize,orderby);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby, boolean useSecurity, boolean cache) {
		return boObjectList.list(ctx,boql,page,pagesize,orderby,useSecurity,cache);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, boolean useSecurity,
			boolean cache) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery) {
		return null;
	}

	public static boObjectList listNoSecurity(EboContext ctx, String boql,
			int page, int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery) {
		return boObjectList.list(ctx,boql,page,pagesize,orderby,fulltext,letter_filter,userQuery);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery) {
		return null;
	}

	
	
	public static boObjectList listOnlyUsingAlias(EboContext ctx,
			String objName, int page, int pagesize, String orderby,
			String fulltext, String[] letter_filter) {
		return null;
	}

	public static boObjectList listUsingAlias(EboContext ctx, String objName,
			int page, int pagesize, String orderby, String fulltext,
			String[] letter_filter) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String parentAttributeName,
			long parentBoui) throws boRuntimeException {
		return boObjectList.list(ctx,parentAttributeName,parentBoui);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long boui, boObject parent, String attributeName) {
		return boObjectList.list(ctx,objectname,boui,parent,attributeName);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] a_boui) {
		return boObjectList.list(ctx,objectname,a_boui);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize) {
		return boObjectList.list(ctx,objectname,boui,page,pagesize);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize, String orderby) {
		return boObjectList.list(ctx,objectname,boui,page,pagesize,orderby);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] bouis, int page, int pagesize) {
		return boObjectList.list(ctx,objectname,bouis,page,pagesize);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] a_boui) {
		return boObjectList.list(ctx,objectname,a_boui);
	}

	public static boObjectList list(EboContext ctx, long boui,
			boolean useSecurity) throws boRuntimeException {
		return boObjectList.list(ctx,boui,useSecurity);
	}

	public static boObjectList list(EboContext ctx, long[] boui, int page,
			int pagesize, String orderby, boolean useSecurity)
			throws boRuntimeException {
		return boObjectList.list(ctx,boui,page,pagesize,orderby);
	}

	// Constructores with BOQL Statements
	public static boObjectList list(EboContext ctx, String boql,
			boolean useSecurity) {
		return boObjectList.list(ctx,boql,useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			boolean useSecurity) {
		return boObjectList.list(ctx,boql,page,useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, boolean useSecurity) {
		return boObjectList.list(ctx,boql,boqlargs,page,useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, boolean useSecurity) {
		return boObjectList.list(ctx,boql,page,pagesize,useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, boolean useSecurity, boolean cache) {
		return boObjectList.list(ctx,boql,page,pagesize,useSecurity,cache);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, boolean useSecurity, boolean cache) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby, boolean useSecurity) {
		return boObjectList.list(ctx,boql,page,pagesize,orderby,useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery, boolean useSecurity) {
		return boObjectList.list(ctx,boql,page,pagesize,orderby,fulltext,letter_filter,userQuery,useSecurity);
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long boui, boObject parent, String attributeName,
			boolean useSecurity) {
		return boObjectList.list(ctx,objectname,boui,parent,attributeName,useSecurity);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] a_boui, boolean useSecurity) {
		return boObjectList.list(ctx,objectname,a_boui,useSecurity);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize, boolean useSecurity) {
		return boObjectList.list(ctx,objectname,boui,page,pagesize,useSecurity);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize, String orderby,
			boolean useSecurity) {
		return boObjectList.list(ctx,objectname,boui,page,pagesize,orderby,useSecurity);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] bouis, int page, int pagesize, boolean useSecurity) {
		return boObjectList.list(ctx,objectname,bouis,page,pagesize,useSecurity);
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] a_boui, boolean useSecurity) {
		return boObjectList.list(ctx,objectname,a_boui,useSecurity);
	}

	// Constructores with list of BOUI Statement

	public static boObjectList list(EboContext ctx, String parentObjectName,
			String parentAttributeName, long parentBoui)
			throws boRuntimeException {
		return boObjectList.list(ctx,parentObjectName,parentAttributeName,parentBoui);	
	}

	public static boObjectList list(EboContext ctx, String parentObjectName,
			String parentAttributeName, long parentBoui, boolean useSecurity)
			throws boRuntimeException {
		return boObjectList.list(ctx,parentObjectName,parentAttributeName,parentBoui,useSecurity);
	}

}
