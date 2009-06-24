/*Enconding=UTF-8*/
package netgest.bo.runtime;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 */
public interface boIEvents 
{
    public static final String EVENT_BEFORE_ADD     = "OnBeforeAdd";
    public static final String EVENT_AFTER_ADD      = "OnAfterAdd";

    public static final String EVENT_BEFORE_REMOVE  = "OnBeforeRemove";
    public static final String EVENT_AFTER_REMOVE   = "OnAfterRemove";
    
    public static final String EVENT_BEFORE_CHANGE  = "OnBeforeChange";
    public static final String EVENT_AFTER_CHANGE   = "OnAfterChange";

    public static final String EVENT_BEFORE_CHANGEORDER  = "OnBeforeChangeOrder";
    public static final String EVENT_AFTER_CHANGEORDER   = "OnAfterChangeOrder";

    
    public boolean onBeforeChange(boEvent event)   throws boRuntimeException;

    public void    onAfterChange(boEvent event)    throws boRuntimeException;

    public boolean onBeforeRemove(boEvent event)   throws boRuntimeException;
    
    public void    onAfterRemove(boEvent event)    throws boRuntimeException;

    public boolean onBeforeAdd(boEvent event)      throws boRuntimeException;
    
    public void    onAfterAdd(boEvent event)       throws boRuntimeException;

    public boolean  onBeforeChangeOrder(boEvent event)       throws boRuntimeException; 
    public void     onAfterChangeOrder(boEvent event)       throws boRuntimeException;
    
}