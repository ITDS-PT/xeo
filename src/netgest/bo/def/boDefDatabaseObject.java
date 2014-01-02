/*Enconding=UTF-8*/
package netgest.bo.def;

public interface boDefDatabaseObject 
{
    public static final byte DBOBJECT_UNIQUEKEY=1;
    public static final byte DBOBJECT_INDEX=2;
    public static final byte DBOBJECT_PRIMARY=3;

    public String getExpression();
    
    public String getId();
    
    public String getLabel();
    
    public byte getType();
}