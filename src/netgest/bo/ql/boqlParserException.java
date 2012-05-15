/*Enconding=UTF-8*/
package netgest.bo.ql;
import netgest.bo.boException;

public class boqlParserException extends boException {
    
	private static final long serialVersionUID = 1L;
	
	/**
	 * The error message
	 */
	private String errorMessage = "";
	/**
	 * The place where the error occurs
	 */
	private String errorSpot = "";
	
    public boqlParserException(String src,String code,Exception base) {
        super(src,code,base);
    }
    
    public boqlParserException(String src,String code,Exception base, String errorMessage, String errorSpot) {
        super(src,code,base);
        this.errorMessage = errorMessage;
        this.errorSpot = errorSpot;
    }
    
    public boqlParserException(String src,String code,Exception base,String[] args) {
         super(src,code,base,args);
    }
    public boqlParserException(String src,String code,Exception base,String args) {
          super(src,code,base,args);
    }

    public String getErrorMessage() {
		return errorMessage;
	}

	public String getErrorSpot() {
		return errorSpot;
	}

}