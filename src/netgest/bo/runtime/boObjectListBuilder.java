package netgest.bo.runtime;

/**
 * 
 * Implements the builder pattern to create instances
 * of {@link boObjectList}. Usage would be:
 * 
 * boObjectList list = new boObjectListBuilder(ctx,"select XEOModel").cache(false).security(true).pagesize(40).build();
 * 
 * 
 *
 */
public class boObjectListBuilder {

	/**
	 * The context for the query
	 */ 
	private EboContext ctx;
	
	/**
	 * The boql expression 
	 */
	private String boql = "";
	
	/**
	 * The query arguments
	 */
	private Object[] args;
	
	/**
	 * Whether to use cache or not
	 */
	private boolean useCache = true;
	
	/**
	 * Whether to use security or not
	 */
	private boolean useSecurity = true;
	
	/**
	 * Default page
	 */
	private int page = 1;
	
	/**
	 * The default page size
	 */
	private int pageSize = boObjectList.PAGESIZE_DEFAULT;
	
	/**
	 * Default order by
	 */
	private String orderBy = "";
	
	/**
	 * 
	 * Creates a boObjectListBuilder with a context and boql expression
	 * 
	 * @param ctx The context to execute the query
	 * @param boql The boql expression
	 */
	public boObjectListBuilder(EboContext ctx, String boql){
		this.ctx = ctx;
		this.boql = boql;
	}
	
	/**
	 * Sets the query arguments
	 * 
	 * @param args The arguments
	 * 
	 */
	public boObjectListBuilder args(Object[] args){
		this.args = args;
		return this;
	}
	
	/**
	 * 
	 * Set the page number (defaults to 1)
	 * 
	 * @param page The page number
	 * @return
	 */
	public boObjectListBuilder page(int page){
		this.page = page;
		return this;
	}
	
	/**
	 * Set the page size
	 * 
	 * @param pageSize The page size (defaults to page size declared in {@link boObjectList#})
	 * @return
	 */
	public boObjectListBuilder pageSize(int pageSize){
		this.pageSize = pageSize;
		return this;
	}
	
	/**
	 * 
	 * Set the cache parameter (defaults to true)
	 * 
	 * @param cache True to use the cache and false to not use the cache
	 * 
	 * @return
	 */
	public boObjectListBuilder cache(boolean cache){
		this.useCache = cache;
		return this;
	}
	
	/**
	 * Sets the security parameter
	 * 
	 * @param security True to use the security and false otherwise
	 * @return
	 */
	public boObjectListBuilder security(boolean security){
		this.useSecurity = security;
		return this;
	}
	
	/**
	 * 
	 * Sets the order by parameter
	 * 
	 * @param orderBy
	 * @return
	 */
	public boObjectListBuilder orderBy(String orderBy){
		this.orderBy = orderBy;
		return this;
	}
	
	
	/**
	 * 
	 * Generates a list from the current
	 * 
	 * @return
	 */
	public boObjectList build(){
		return boObjectList.list(ctx, boql,args,page,pageSize,orderBy,"",null,null,useSecurity,useCache);
	}

	
	public static void main(String[] args){
		EboContext ctx = null;
		Object[] arguments = new Object[]{"arg1","arg2"};
		
		boObjectList list = new boObjectListBuilder(ctx, "select Objecto").build();
		
		boObjectList list2 = new boObjectListBuilder(ctx, "select Objecto").pageSize(3).cache(false).build();
		
		boObjectList list3 = new boObjectListBuilder(ctx, "select Objecto").cache(false).args(arguments).security(false).build();
		
		boObjectList list4 = new boObjectListBuilder(ctx, "select Objecto").cache(false).args(arguments).security(false).pageSize(3).page(2).build();
		
	}
	
}
