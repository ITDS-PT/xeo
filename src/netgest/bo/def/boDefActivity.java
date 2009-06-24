package netgest.bo.def;

public interface boDefActivity 
{

    public static final byte TYPE_INSTANCE=0;
    public static final byte TYPE_STATIC=1;
    

    public String getId();
    
    public String getDescription();
    
    public String getScheduleType();
    
    public String getStartDate();
    
    public String getEndDate();
    
    public String getEvery();
    
    public String getAt();
    
    public String getPerformer();
    
    public String getClassName();
    
    public String getPriority();
    
    public byte getActivityType();
    
    public boDefClsState getClsState();
    
    public boDefClsState getClsStateAttribute();
    
    public String getBody();
    
    public String getName();
    
}