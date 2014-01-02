/**
 * 
 */
package netgest.bo.runtime;

import netgest.bo.security.securityOPL;
import netgest.bo.security.securityRights;
import netgest.bo.system.Logger;

/**
 * 
 * SecurityManager allows to query if a certain action (read/write) can be
 * performed on a given instance
 * 
 */
public class SecurityManager {

	public static Logger log = Logger.getLogger(SecurityManager.class);

	/**
	 * 
	 * Checks whether the user can read the instance or not
	 * 
	 * @param obj
	 *            The instance to be read
	 * 
	 * @return True if the instance can be read and false otherwise
	 */
	public boolean canRead(boObject obj) {
		try {
			return securityOPL.canRead(obj)
					&& securityRights.canRead(obj.getEboContext(),
							obj.getName());
		} catch (boRuntimeException e) {
			log.severe(e);
			return false;
		}
	}

	/**
	 * 
	 * Checks whether the user can write the instance or not
	 * 
	 * @param obj
	 *            The instance to be written
	 * @return True if the instance can be written and false otherwise
	 */
	public boolean canWrite(boObject obj) {
		try {
			return securityOPL.canWrite(obj)
					&& securityRights.canWrite(obj.getEboContext(),
							obj.getName());
		} catch (boRuntimeException e) {
			log.severe(e);
			return false;
		}
	}

	/**
	 * 
	 * Checks whether the user can delete the instance or not
	 * 
	 * @param obj The instance to delete
	 * @return True if the instance can be deleted and false otherwise
	 */
	public boolean canDelete(boObject obj) {
		try {
			return securityOPL.canDelete(obj)
					&& securityRights.canDelete(obj.getEboContext(),
							obj.getName());
		} catch (boRuntimeException e) {
			log.severe(e);
			return false;
		}
	}
	
	
	/**
	 * 
	 * Checks whether a given method in a instance can be executed
	 * 
	 * @param obj The instance
	 * @param methodName The name of the method
	 * 
	 * @return True if the method can be executed and false otherwise
	 * 
	 */
	public boolean canExecute(boObject obj, String methodName){
		
		try {
			return securityRights.canExecute(obj.getEboContext(), obj.getName(), methodName);
		} catch (boRuntimeException e) {
			log.severe(e);
			return false;
		}
	}
	
	/**
	 * 
	 * Checks whether or not the use has full control
	 * for the given instance
	 * 
	 * @param obj The instance
	 * @return True if the user has full control and false otherwise
	 */
	public boolean hasFullControl(boObject obj){
		try {
			return securityOPL.hasFullControl(obj);
		} catch (boRuntimeException e) {
			log.severe(e);
			return false;
		}
	}

}
