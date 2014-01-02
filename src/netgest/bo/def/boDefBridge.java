/*Enconding=UTF-8*/
package netgest.bo.def;



public interface boDefBridge
{
    //public boDefAttribute[] p_attributes = null;
    public boDefAttribute[] getBridgeAttributes();

    public boDefMethod[] getMethods();

    public boolean haveBridgeAttributes();

    public String getName();
    
    public String getBoPhisicalMasterTable();

    public String getBoMasterTable();

    public boDefAttribute[] getBoAttributes();

    public String getFatherFieldName();

    public String getChildFieldName();

    public String OLDgetFatherFieldName();
    
    public String OLDgetChildFieldName();
    
    public boolean hasAttribute(String attributeName);

    public byte getAttributeType(String attributeName);

    public boDefAttribute getAttributeRef(String attributeName);

    // Not implemented in this context
    public String getType();

    // Not implemented in this context
    public String getDbName();

    public String getExtendsClass();

    public String getClassName();
}
