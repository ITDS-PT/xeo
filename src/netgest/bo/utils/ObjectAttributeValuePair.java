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

		private String oldDisplayValue;

		private String newDisplayValue;
    	
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
    		this.oldDisplayValue = "";
    		this.newDisplayValue = "";
    	}
    	
    	/**
    	 * 
    	 * Public constructor for a new pair of values
    	 * 
    	 * @param oldVal The old value for the attribute
    	 * @param newVal The new value for the attribute
    	 * @param attName The name of the attribute
    	 */
    	public ObjectAttributeValuePair(String oldVal, String newVal, String oldDisplayValue, String newDisplayValue, String attName)
    	{
    		this.oldVal = oldVal;
    		this.newVal = newVal;
    		this.attName = attName;
    		this.oldDisplayValue = oldDisplayValue;
    		this.newDisplayValue = newDisplayValue;
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
		 * Returns the old (i.e. saved in the database) display value of the attribute
		 * The display value is used in situations such as objects and lovs
		 * 
		 * @return
		 */
		public String getOldDisplayValue(){
			return oldDisplayValue;
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
		 * Returns the new (i.e. binded in editing) display value of the attribute
		 * The display value is used in situations such as objects and lovs
		 * 
		 * @return
		 */
		public String getNewDisplayValue(){
			return newDisplayValue;
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
