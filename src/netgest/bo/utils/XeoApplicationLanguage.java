package netgest.bo.utils;

/**
 * 
 * 
 * Represents an application language (i.e a language in which a XEO Model translation (and viewers) is available
 * 
 *
 */
public class XeoApplicationLanguage {
	/**
	 * A representation of the language (PT, PT_BR, EN, EN_UK)
	 */
	private String code;
	/**
	 * A description of the language
	 */
	private String description;
	
	
	/**
	 * 
	 * 
	 * @param cod
	 * @param des
	 */
	public XeoApplicationLanguage(String cod, String des){
		this.code=cod;
		this.description=des;
	}
	
	public String getCode(){
		return new String(code);
	}
	
	
	public String getDescription(){
		return new String(description);
	}
}
