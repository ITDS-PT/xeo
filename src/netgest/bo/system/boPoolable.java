/*Enconding=UTF-8*/
package netgest.bo.system;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;

public abstract class boPoolable implements Cloneable 
{

    protected String    ownerContext=null;
    private EboContext  eboContext = null;
    protected ArrayList owners = new ArrayList(1);
    protected Object[]	keys = null;		
    protected	boolean 	isWeak = false;
    protected	long 	lastUsedTime = System.currentTimeMillis();
    
    
    /**
     * Constructor for boPoolable Object
     * @param Context for the object
     */
    public boPoolable() 
    {
    }
    public boPoolable(EboContext boctx) {
        setEboContext(boctx);
    }
    
	private static final AtomicLong OBJECTID = new AtomicLong(0);
    

    /**
     *  Contains a unique id to identify the object in the pool.
     */
    private String p_uniqueid = this.getClass().getName() + ":" + OBJECTID.incrementAndGet();
    
    /**
     *  Flag with state of the Object.
     *  Statefull or Stateless
     */
    protected boolean IsStateFull;

    // Specific sub object passivate code
    
    /**
     * Abstract method to do some special duty before Passivate (Putted back in the bool for future used).
     * This method can be used to release resources when the client doesn't need it any longer.<br>
     * The object is putted back in the pool and need to be clean to serve another client.
     */
    public abstract void poolObjectPassivate();
    // Specific sub object activate code
    /**
     * Abstract method to do some special duty before Activate (Retreive from the pool to serve a new client).
     * This method can be used to restore resources when the client request the same kind of the object.<br>
     */
    public abstract void poolObjectActivate();

    
    /**
     *  Set the object to StateFull, After this the object persist between client requests.
     *  
     */
//    public void poolSetStateFull( String owner ) 
//    {
//        IsStateFull = true;
//        boPoolManager.setStateFull( owner ,this);
//    }

    public void poolSetStateFull( ) 
    {
        IsStateFull = true;
        if( isWeak && eboContext != null ) {
        	boPoolManager pm = eboContext.getApplication().getMemoryArchive().getPoolManager();
    		pm.putObject( this, this.keys );
        }
    }

    public void poolSetStateFull( String owner ) 
    {
        IsStateFull = true;
        if( this.owners.indexOf( owner ) == -1  ) 
        {
            throw new RuntimeException(MessageLocalizer.getMessage("CANNOT_SET_STATEFULL_TO_A_DIFFERENT_CONTEXT"));
        }
    }
    public boolean havePoolChilds()
    {
        return true;
    }
    public String poolOwnerContext() {
        return ownerContext;
    }
    /**
     *  Set the object to StateLess, the object is putted back in the pool after the request.
     *  and now can be used by another client's.
     */
    public final void poolUnSetStateFull() {
        IsStateFull = false;
        
    }
    /**
     * Get the current State of the object, StateLess or StateFull
     * @return true - for StateFull
     */
    public final boolean poolIsStateFull() {
        return IsStateFull;
    }
    
    /**
     * Gets a <i>Unique Id</i> for the object, this id is used in the pool to identify the object.
     * @return String - a Unique Id build based on the class name and hashcode.
     */
    public String poolUniqueId() {
        return p_uniqueid;
    }
    
    /**
     * Clone this Object, a give a new <i>Unique Id</i> to it.
     * @return the Cloned object
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        boPoolable ret = (boPoolable)super.clone();
        ret.p_uniqueid=ret.getClass().getName()+":"+ret.hashCode();
        return ret;
    }
    
    /**
     * Release all references to this object when java garbage collect call's finalize method.
     */
//    public final void finalize() {
//        boPoolManager.realeaseAllObjects(this.poolUniqueId());
//    }

    public final void poolDestroyObject() {
        getEboContext().getApplication().getMemoryArchive().getPoolManager().destroyObject(this);
    }

    /**
     * Get the current EboContext of the object
     * @return current EboContext of the object;
     */
    public EboContext getEboContext() {
        return eboContext;
    }
    /**
     * Set the EboContext for the current object
     * This method is called allways beteween client request even when the object is reserved, if you want to restore some object resources between client request
     * you can do it overwrinting the method
     * @param EboContext for the object
     */
    public void setEboContext(EboContext boctx) {
        if(boctx!=null) {
            eboContext = boctx;
//            boPoolManager.activateObjects(this.poolUniqueId(),boctx);
        }
    }
    /**
     * Remove the current context from the Object<p>
     * This method is called allways beteween client request even when the object is reserved, if you want to release some resources between client request you 
     * can do it overwrinting this method
     * @return The released context of the object
     */
    public EboContext removeEboContext() 
    {
        EboContext ret = eboContext;
        eboContext=null;
        return ret;
    }
    
}