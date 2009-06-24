/*Enconding=UTF-8*/
package netgest.bo.runtime;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 */
public class boEvent 
{
    public static final byte EVENT_AFTER_ADD=0;
    public static final byte EVENT_AFTER_CHANGE=1;
    public static final byte EVENT_AFTER_CHANGE_ORDER=2;
    public static final byte EVENT_AFTER_REMOVE=3;
    public static final byte EVENT_AFTER_GETVALUE = 4;
    public static final byte EVENT_AFTER_LOADBRIDGE = 5;
    
    public static final byte EVENT_AFTER_SAVE = 6;
    public static final byte EVENT_AFTER_CREATE = 7;
    public static final byte EVENT_AFTER_DESTROY = 8;
    public static final byte EVENT_AFTER_LOAD = 9;
    public static final byte EVENT_AFTER_CLONE = 10;
    
    public static final byte EVENT_BEFORE_ADD=11;
    public static final byte EVENT_BEFORE_CHANGE=12;
    public static final byte EVENT_BEFORE_CHANGE_ORDER=13;
    public static final byte EVENT_BEFORE_REMOVE=14;
    public static final byte EVENT_BEFORE_GETVALUE = 15;
    public static final byte EVENT_BEFORE_LOADBRIDGE = 16;
    
    public static final byte EVENT_BEFORE_SAVE = 17;
    public static final byte EVENT_BEFORE_CREATE = 18;
    public static final byte EVENT_BEFORE_DESTROY = 19;
    public static final byte EVENT_BEFORE_LOAD = 20;
    public static final byte EVENT_BEFORE_CLONE = 21;
    

    
    public static final String[] EVENT_NAME = { 
                                    "afterAdd",
                                    "afterChange",
                                    "afterChangeOrder",
                                    "afterRemove",
                                    "afterGetValue",
                                    "afterLoadBridge",
                                    "afterSave",
                                    "afterCreate",
                                    "afterDestroy",
                                    "afterLoad",
                                    "afterClone",
                                    "beforeAdd",
                                    "beforeChange",
                                    "beforeChangeOrder",
                                    "beforeRemove",
                                    "beforeGetValue",
                                    "beforeLoadBridge",
                                    "beforeSave",
                                    "beforeCreate",
                                    "beforeDestroy",
                                    "beforeLoad",
                                    "beforeClone"
                            };
                            
    private static final boolean[] CAN_CANCEL_EVENT = {
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    true,
                                    true,
                                    true,
                                    true,
                                    false,
                                    true,
                                    true,
                                    true,
                                    true,
                                    true,
                                    true
                            };

    private byte    p_event;
    private Object  p_value;
    private Object  p_newvalue;
    private Object  p_oldvalue;
    private Object  p_sourceobject;
    private String  p_name;
    
    private boolean wasCanceled;

    public boEvent(Object sourceobject, String eventName, Object oldvalue,Object newvalue)
    {
        p_sourceobject = sourceobject;
        p_newvalue=newvalue;
        p_oldvalue=oldvalue;
        p_value = newvalue!=null?newvalue:oldvalue;
        p_name  = eventName;
    }

    public boEvent(Object sourceobject, String eventName, Object value)
    {
        p_sourceobject = sourceobject;
        p_value = value;
        p_name  = eventName;
    }
    
    public boEvent(byte event, Object sourceobject, Object oldvalue,Object newvalue)
    {
        p_sourceobject = sourceobject;
        p_event = event;
        p_newvalue=newvalue;
        p_oldvalue=oldvalue;
        p_value = newvalue!=null?newvalue:oldvalue;
        p_name  = EVENT_NAME[ event ];
    }
    public boEvent(byte event, Object sourceobject, Object value)
    {
        p_sourceobject = sourceobject;
        p_event = event;
        p_value = value;
        p_name  = EVENT_NAME[ event ];
    }

    public Object getValue() 
    {
        return p_value;
    }
    public Object getNewValue() 
    {
        return p_newvalue;
    }
    public Object getPreviousValue() 
    {
        return p_oldvalue;
    }
    public Object getSourceObject() 
    {
        return p_sourceobject;
    }
    
    public void cancelEvent()
    {
        this.wasCanceled = true;        
    }
    
    public boolean wasCanceled()
    {
        return this.wasCanceled;
    }
    
    public String getName()
    {
        return p_name;
    }
    
    public byte getEvent()
    {
        return p_event;
    }
}