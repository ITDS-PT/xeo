package netgest.bo.models;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.login.LoginUtil;
import netgest.utils.MD5Utils;
import netgest.utils.StringUtils;

/**
 * 
 * Operations on User Models
 *
 */
public class UserOperations {

	/**
	 * Attribute with the password
	 */
	private static final String PASSWORD_ATT = "password";
	
	/**
	 * Max password size (MD5 size)
	 */
	private static final int MAX_PASSWORD_SIZE = 32;
	
	/**
	 * 
	 * Processes the user password
	 * 
	 * Password must be < 32 chars (MD5 hash).
	 * We detect a password is entered by checking the length of the value
	 * 
	 * If less than 32, encrypt it, then md5
	 * If bigger than 32, error (or else we couldn't detect a new password being entered)
	 * If equal to 32 same password 
	 * 
	 * @param obj The user object
	 * 
	 * @return True if anything
	 * 
	 * @throws boRuntimeException
	 */
	public static boolean processPass(boObject obj) throws boRuntimeException{
		String passwordValue = obj.getAttribute(PASSWORD_ATT).getValueString();
		int passwordLength = 0;
		if ( !StringUtils.isEmpty( passwordValue ) ){
			passwordLength = passwordValue.length();
			if (passwordLength < MAX_PASSWORD_SIZE ){
				String newPassword = MD5Utils.toHexMD5(LoginUtil.encryptPassword(passwordValue));
				obj.getAttribute(PASSWORD_ATT).setValueString(newPassword);
			} else if (passwordLength > MAX_PASSWORD_SIZE ){
				obj.addErrorMessage(obj.getAttribute(PASSWORD_ATT),MessageLocalizer.getMessage("PASSWORD_MAX_SIZE"));
				return false;
			}
		} else {
			obj.getAttribute(PASSWORD_ATT).setValueString(null);
		}
		
		return true;
	}
	 
		
	
}
