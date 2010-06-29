package netgest.bo.utils;

/**
 * 
 * Represents the old and new value of
 * an object attribute 
 * 
 * @author Pedro Pereira
 *
 */
public class ObjectAttributeValuePair {

		/**
    	 * The old value of the attribute
    	 */
    	private String oldVal;
    	
    	/**
    	 *  The new value of the attribute 
    	 */
    	private String newVal;
    	
    	/**
    	 * The name of the attribute
    	 */
    	private String attName;
    	
    	/**
    	 * 
    	 * Public constructor for a new pair of values
    	 * 
    	 * @param oldVal The old value for the attribute
    	 * @param newVal The new value for the attribute
    	 * @param attName The name of the attribute
    	 */
    	public ObjectAttributeValuePair(String oldVal, String newVal, String attName)
    	{
    		this.oldVal = oldVal;
    		this.newVal = newVal;
    		this.attName = attName;
    	}

		/**
		 * 
		 * Retrieves the old (i.e. saved in the database) value of the attribute
		 * 
		 * @return The old value of the attribute
		 */
		public String getOldVal() {
			return oldVal;
		}

		/**
		 * 
		 * Retrieves the new (i.e. binded in editing) value of the attribute
		 * 
		 * @return The new value of the attribute
		 */
		public String getNewVal() {
			return newVal;
		}

		/**
		 * 
		 * Retrieves the name of the attribute
		 * 
		 * @return The name of the attribute
		 */
		public String getAttName() 
		{
			return attName;
		}
		
		@Override
		public String toString()
		{
			return "| " +this.attName + " old: " + oldVal + " | new: " + newVal + " |"; 
		}
    
	
}
