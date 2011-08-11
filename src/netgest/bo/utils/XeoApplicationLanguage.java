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
	public String code;
	/**
	 * A description of the language
	 */
	public String description;
	
	
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
}
