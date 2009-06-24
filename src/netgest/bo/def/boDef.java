/*Enconding=UTF-8*/
package netgest.bo.def;

public interface boDef  
{
    public String getFatherFieldName();
    public String getChildFieldName();
    public String getBoMasterTable();
    public boolean hasAttribute(String attributeName);
    public byte getAttributeType(String attributeName);
    public boDefAttribute getAttributeRef(String attributeName);
    public boDefAttribute[] getBoAttributes();
}