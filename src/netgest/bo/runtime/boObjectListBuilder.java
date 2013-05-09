package netgest.bo.runtime;

import java.util.ArrayList;

import netgest.bo.system.boApplication;

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
	private Object[] args = new Object[0];
	
	/**
	 * Individual Arguments
	 */
	private ArrayList<Object> individualArguments = new ArrayList<Object>();
	
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
	 * FullText query
	 */
	private String fullText = "";
	
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
	 * 
	 * Creates a boObjectListBuilder in the default context with a boql expression
	 * 
	 * @param boql The boql expression
	 */
	public boObjectListBuilder(String boql){
		this.ctx = boApplication.currentContext().getEboContext();
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
	 * Set the arguments through varargs
	 *  
	 * @param args the Arguments
	 * 
	 * */
	public boObjectListBuilder argsList(Object... args){
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
	 * Sets the fulltext parameter
	 * 
	 * @param fullText
	 * @return
	 */
	public boObjectListBuilder fullText(String fullText){
		this.fullText = fullText;
		return this;
	}
	
	/**
	 * 
	 * Adds a single arguments
	 * 
	 * @param value
	 * @return
	 */
	public boObjectListBuilder arg(Object value){
		this.individualArguments.add(value);
		return this;
	}
	
	/**
	 * 
	 * Generates a list from the current
	 * 
	 * @return
	 */
	public boObjectList build(){
		return boObjectList.list(getCtx()
			,boql
			,getArgs()
			,getPage()
			,getPageSize()
			,getOrderBy()
			,getFullText()
			,null
			,null
			,useSecurity
			,useCache);
	}

	/**
	 *  Retrieve the EboContext associated 
	 * 
	 * @return
	 */
	public EboContext getCtx() {
		if (ctx == null)
			ctx = boApplication.currentContext().getEboContext();
		return ctx;
	}

	/**
	 * 
	 * Retrieve the boql expression
	 * 
	 * @return
	 */
	public String getBoql() {
		return boql;
	}
	
	public String getFullText(){
		return fullText;
	}

	/**
	 * 
	 * Retrieve the list of arguments
	 * 
	 * @return
	 */
	public Object[] getArgs() {
		
		if (this.individualArguments.size() == 0)
			return args;
		if (this.args.length == 0)
			return individualArguments.toArray();
		
		Object[] totalArgs = new Object[this.individualArguments.size() + this.args.length];
		int k = 0;
		for (Object curr : args){
			if (curr instanceof boObject){
				totalArgs[k] = ((boObject)curr).getBoui();
			} else {
				totalArgs[k] = curr;
			}
			k++;
		}
		for (Object curr : individualArguments){
			if (curr instanceof boObject){
				totalArgs[k] = ((boObject)curr).getBoui();
			} else {
				totalArgs[k] = curr;
			}
			k++;
		}
		
		return totalArgs;
	}
	

	/**
	 * 
	 * Whether cache is being used (defaults to true)
	 * 
	 * @return
	 */
	public boolean isUseCache() {
		return useCache;
	}

	/**
	 * Whether security is being used in the query (defaults to true)
	 * 
	 * @return
	 */
	public boolean isUseSecurity() {
		return useSecurity;
	}

	/**
	 * 
	 * The page number to position the result (defaults to 1 - first page)
	 * 
	 * @return
	 */
	public int getPage() {
		return page;
	}

	/**
	 * 
	 * The page size for the query (defaults to 50)
	 * 
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 
	 * The order by string (defaults to empty string)
	 * 
	 * @return
	 */
	public String getOrderBy() {
		return orderBy;
	}
	
}
