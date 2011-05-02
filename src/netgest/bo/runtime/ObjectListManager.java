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
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, boolean useSecurity) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, boolean useSecurity) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby,
			boolean useSecurity) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, int pagesize, String orderby,
			boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby, boolean useSecurity, boolean cache) {
		return null;
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
		return null;
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
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long boui, boObject parent, String attributeName) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] a_boui) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize, String orderby) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] bouis, int page, int pagesize) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] a_boui) {
		return null;
	}

	public static boObjectList list(EboContext ctx, long boui,
			boolean useSecurity) throws boRuntimeException {
		return null;
	}

	public static boObjectList list(EboContext ctx, long[] boui, int page,
			int pagesize, String orderby, boolean useSecurity)
			throws boRuntimeException {
		return null;
	}

	// Constructores with BOQL Statements
	public static boObjectList list(EboContext ctx, String boql,
			boolean useSecurity) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			boolean useSecurity) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql,
			Object[] boqlargs, int page, boolean useSecurity) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, boolean useSecurity) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, boolean useSecurity, boolean cache) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, boolean useSecurity, boolean cache) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby, boolean useSecurity) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String boql, int page,
			int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery, boolean useSecurity) {
		return null;
	}

	public static boObjectList listWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, String fulltext,
			String[] letter_filter, String userQuery, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long boui, boObject parent, String attributeName,
			boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] a_boui, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			long[] boui, int page, int pagesize, String orderby,
			boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] bouis, int page, int pagesize, boolean useSecurity) {
		return null;
	}

	public static boObjectList list(EboContext ctx, String objectname,
			String[] a_boui, boolean useSecurity) {
		return null;
	}

	// Constructores with list of BOUI Statement

	public static boObjectList list(EboContext ctx, String parentObjectName,
			String parentAttributeName, long parentBoui)
			throws boRuntimeException {
		return null;	}

	public static boObjectList list(EboContext ctx, String parentObjectName,
			String parentAttributeName, long parentBoui, boolean useSecurity)
			throws boRuntimeException {
		return null;
	}

	// --------------------------------------------------------------------------
	// ----------------------------//
	// construtores com o FORMAT ONE
	// --------------------------------------------------------------------------
	// ----------------------------//
	public static boObjectList edit(EboContext ctx, String objectname,
			String[] a_boui) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String objectname,
			String[] a_boui, boolean useSecurity) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String objectname,
			String[] bouis, int page, int pagesize) {
		long[] lbouis = new long[bouis.length];
		for (short i = 0; i < bouis.length; i++) {
			lbouis[i] = Long.parseLong(bouis[i]);
		}
		return null;
	}

	public static boObjectList edit(EboContext ctx, String objectname,
			String[] bouis, int page, int pagesize, boolean useSecurity) {
		return null;
	}

	// Constructores with BOQL Statements
	public static boObjectList edit(EboContext ctx, String boql) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String boql,
			boolean useSecurity) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String boql, int page) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String boql, int page,
			boolean useSecurity) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String boql,
			Object[] boqlargs) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String boql,
			Object[] boqlargs, boolean useSecurity) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String boql,
			Object[] boqlargs, int page) {
		return null;
	}

	public static boObjectList editWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String boql,
			Object[] boqlargs, int page, boolean useSecurity) {
		return null;
	}

	public static boObjectList editWFirstRows(EboContext ctx, String boql,
			Object[] boqlargs, int page, boolean useSecurity) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String boql, int page,
			int pagesize) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String boql, int page,
			int pagesize, boolean useSecurity) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String boql, int page,
			int pagesize, String orderby) {
		return null;
	}

	public static boObjectList editWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String boql, int page,
			int pagesize, String orderby, boolean useSecurity) {
		return null;
	}

	public static boObjectList editWFirstRows(EboContext ctx, String boql,
			int page, int pagesize, String orderby, boolean useSecurity) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String objName, long boui)
			throws boRuntimeException {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String objName, long boui,
			boolean useSecurity) throws boRuntimeException {
		return null;
	}

	public static boObjectList edit(EboContext ctx, long[] boui,
			String objName, int page, int pagesize, String orderby)
			throws boRuntimeException {

		return null;
	}

	public static boObjectList edit(EboContext ctx, long[] boui,
			String objName, int page, int pagesize, String orderby,
			boolean useSecurity) throws boRuntimeException {

		return null;
	}

	public static boObjectList edit(EboContext ctx, String objectname,
			long boui, boObject parent, String attributeName) {
		return null;
	}

	public static boObjectList edit(EboContext ctx, String objectname,
			long boui, boObject parent, String attributeName,
			boolean useSecurity) {
		return null;
	}

}
