/*Enconding=UTF-8*/
package netgest.bo.def;
public interface boDefOPL
{
    public String[] getReadKeyAttributes();
    
    public String[] getWriteKeyAttributes();
    
    public String[] getDeleteKeyAttributes();
    
    public String[] getFullControlKeyAttributes();

    public String[] getMethodsExecuteKeys();

    public String[] getEventsExecuteKeys();
    
    public String[] getClassKeys();
}