/*Enconding=UTF-8*/
package netgest.bo.system;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.Logger;

/**
 * 
 * @author JMF
 */
@SuppressWarnings("unchecked")
public class boPoolManager {
	// logger
	private static Logger logger = Logger
			.getLogger("netgest.bo.system.boPoolManager");

	private int GLOBAL_POOL_TARGET_SIZE = 10000;
	private int GLOBAL_POOL_SHRINK_TARGET = 3000;
	private int GLOBAL_POOL_MAX_UNUSED_TIME = (8 * 60 * 60 * 1000);

	private int GLOBAL_POOL_CLEAN_INTERVAL = 5 * 60 * 1000;
	private int GLOBAL_POOL_FORCED_CLEAN_INTERVAL = 10000;

	private long GLOBAL_POOL_LASTCLEAN = System.currentTimeMillis();

    private AtomicInteger 	POOL_SIZE = new AtomicInteger( 0 );
    private AtomicBoolean 	SYNCHRONIZED_POOLCLEAN = new AtomicBoolean( false );

	private ReferenceQueue WeakObjectQueue = new ReferenceQueue();
	public Hashtable WeakObjectPool = new Hashtable();

	public Hashtable ObjectPool = new Hashtable();
	public Hashtable ObjectPoolUSERNAMES = new Hashtable();
	// onde estão os objectos com o unique id
	public Hashtable ContextKeys = new Hashtable();
	// cada Owner tem uma entrada nesta Hastable , com outra hashtable com o
	// BOUI ( no caso dos objects )

	public WeakHashMap TimeoutContext = new WeakHashMap();
	// que por sua vez tem um ArrayList com as referencias que apontam pra o
	// mesmo objecto

	public Hashtable OwnedObjects = new Hashtable();

	// objectos com a key Owner , ie todos os objectos que aquele owner tem
	// (Arraylist de uniqueIDS )

	public final void putObject(boPoolable object, Object key) {

		putObject(object, new Object[] { key }, false);

	}

	public final void putObject(boPoolable object, Object key, boolean release) {

		putObject(object, new Object[] { key }, release);

	}

	public final void putObject(boPoolable object, Object[] keys) {
		putObject(object, keys, false);
	}

	public final void putObject(boPoolable object, Object[] keys,
			boolean release) {
		Vector list = object.getEboContext().getSharedOwners();
		for (int i = 0; i < list.size(); i++) {
			_putObject((String) list.get(i), object, keys, release);
		}
	}

	public final Enumeration getPoolObjects() {
		return ObjectPool.keys();
	}

    private final void _putObject(String owner, boPoolable object,Object[] keys, boolean release )
    {
    	
        Hashtable htkeys = (Hashtable)ContextKeys.get( owner );
        if( htkeys == null )
        {
        	synchronized( ContextKeys ) {
        		htkeys = (Hashtable)ContextKeys.get( owner );
        		if(htkeys == null ) {
        			ContextKeys.put( owner, htkeys=new Hashtable() );
        		}
        	}
        }
        
        for(int i=0;i<keys.length;i++) 
        {
            if(keys[i]!=null) 
            {
                ArrayList refs;
                if(!release)
                {
                    if((refs=(ArrayList)htkeys.get( keys[i] )) == null) 
                    {
                    	synchronized (htkeys) {
                    		if( htkeys.get( keys[i] ) == null ) {
                    			refs = new ArrayList(1);
                    			htkeys.put(keys[i],refs);
                    		}
						}
                    }
                    refs.add( object.poolUniqueId() );
                }
                else
                {
                	synchronized (htkeys) {
	                	refs = new ArrayList(1);
	                    htkeys.put(keys[i],refs);
	                    refs.add( object.poolUniqueId() );
                	}
                }
            }
        }

        object.keys = keys;
        putInMemoryArchive( owner, object );
    }

	private final void putInMemoryArchive(String owner, boPoolable object) {
		// First operation... there is a risc of the new object in the pool
		// Be purged immediately
		shrinkGlobalObjectPool();

		if (object.isWeak) {
			object.isWeak = false;
			WeakObjectPool.remove(object.poolUniqueId());
		}

		EboContext ctx = object.getEboContext();
		if (object.ownerContext == null) {
			ctx.ObjectsInContext.add(object.poolUniqueId());
			object.ownerContext = ctx.poolUniqueId();
		}
		if (!object.owners.contains(owner)) {
			object.owners.add(owner);
		}

		ObjectPool.put(object.poolUniqueId(), object);
        POOL_SIZE.set( ObjectPool.size() );
		ObjectPoolUSERNAMES.put(object.poolUniqueId(), ctx.getBoSession()
				.getUser().getName());

		HashMap list = (HashMap) OwnedObjects.get(owner);
		if (list == null) {
			OwnedObjects.put(owner, list = new HashMap());
		}
		if (!list.containsKey(object.poolUniqueId())) {
			list.put(object.poolUniqueId(), null);
			// shrinkThreadObjectPool(list);
		}
	}

	private final void shrinkGlobalObjectPool() {
		boolean clean;
    	
    	clean = false;
        if( POOL_SIZE.get() > GLOBAL_POOL_TARGET_SIZE ) {
    		if ( System.currentTimeMillis() - GLOBAL_POOL_LASTCLEAN > GLOBAL_POOL_FORCED_CLEAN_INTERVAL ) {
    			clean = true;
    		}
        }
        else {
    		if ( System.currentTimeMillis() - GLOBAL_POOL_LASTCLEAN > GLOBAL_POOL_CLEAN_INTERVAL ) {
    			clean = true;
    		}
        }
        if( clean && !SYNCHRONIZED_POOLCLEAN.get() ) {

        	synchronized ( SYNCHRONIZED_POOLCLEAN ) {
				try {
					SYNCHRONIZED_POOLCLEAN.set( true );
					
					int target = ObjectPool.size() - (GLOBAL_POOL_TARGET_SIZE - GLOBAL_POOL_SHRINK_TARGET);
					
	    			
					long currentTime = System.currentTimeMillis();
					GLOBAL_POOL_LASTCLEAN = currentTime;

	    			int removeCount = 0;
		        	
		        	Object[] poolObjects;
		        	
		        	synchronized (ObjectPool) {
		        		poolObjects = ObjectPool.values().toArray();
					}
					
		        	for( Object poolObject : poolObjects ) {
		        		boPoolable p = (boPoolable)poolObject;
			    		if( p != null ) {
			    			if ( !p.poolIsStateFull() || ((currentTime - p.lastUsedTime) > GLOBAL_POOL_MAX_UNUSED_TIME)) {
					    		p = (boPoolable)ObjectPool.remove( p.poolUniqueId() );
					    		if( p != null ) {
					    			p.isWeak = true;
				    				WeakObjectPool.put( p.poolUniqueId(), new PoolWeakReference( p.keys, p, WeakObjectQueue ) );
					    		}
				    			removeCount++;
				    			if( removeCount >= target ) {
				    				break;
				    			}
			    			}
			    		}
		        	}

		        	PoolWeakReference purgeRef;
					while ((purgeRef = (PoolWeakReference) WeakObjectQueue
							.poll()) != null) {
						WeakObjectPool.remove(purgeRef.uniqueId);
						ObjectPoolUSERNAMES.remove(purgeRef.uniqueId);
						if (purgeRef.owners != null
								&& purgeRef.owners.size() > 0) {
							Iterator ownerIt = purgeRef.owners.iterator();
							while (ownerIt.hasNext()) {
								removeObjectKeysFromOwner(purgeRef.uniqueId,
										purgeRef.keys, ownerIt.next());
							}
						}
					}
					
					POOL_SIZE.set( ObjectPool.size() );
					
					if (  removeCount < target ) {
						GLOBAL_POOL_TARGET_SIZE += Math.max( 0,(target - removeCount) );
					}
				} finally {
					SYNCHRONIZED_POOLCLEAN.set(false);
				}
			}
		}
	}

	public final boPoolable getObject(EboContext boctx, String owner, Object key) {
		return _getObject(boctx, owner, key);
	}

	public final boPoolable getObject(EboContext boctx, Object key) {
		return _getObject(boctx, boctx.getPreferredPoolObjectOwner(), key);
	}

	private final boPoolable _getObject(EboContext boctx, String owner,
			Object key) {

		boPoolable ret = __getObject(boctx, owner, key);
		if (ret != null) {
			int cntr = 0;
			int xtimeout = 0;
			while (!boctx.poolUniqueId().equals(ret.ownerContext)) {
				// wait until another threath realese the object
				cntr++;
				try {
					Thread.sleep(10);
					if (cntr == 1 && xtimeout > 0) {
						logger.warn("Waiting for " + key + " USER: "
								+ boctx.getBoSession().getUser().getName()
								+ "  in Thread = " + ret.ownerContext
								+ " current Thread = " + boctx.poolUniqueId()
								+ " OWNER:" + owner);
					}

					ret = __getObject(boctx, owner, key);
				} catch (InterruptedException e) {

				}

				// Object was removed from the pool
				if (ret == null) {
					return null;
				}

				if (!boctx.poolUniqueId().equals(ret.ownerContext)
						&& TimeoutContext.containsKey(boctx.poolUniqueId())) {
					logger.warn("EboContext marked as Timeout, no Wait - "
							+ key + " USER: "
							+ boctx.getBoSession().getUser().getName()
							+ "  in Thread = " + ret.ownerContext
							+ " current Thread = " + boctx.poolUniqueId()
							+ " OWNER:" + owner);
					ret.ownerContext = null;
					ret = __getObject(boctx, owner, key);
					continue;
				}

				if (cntr > 1000) { // 10 segundos
					cntr = 0;
					xtimeout++;
					if (xtimeout > 4) // ao fim de 40 segundos aborta
					{
						TimeoutContext.put(boctx.poolUniqueId(), new Boolean(
								true));
						ret.ownerContext = null;
						logger
								.warn("ERROR IN EBO_CONTEXT : "
										+ key
										+ " "
										+ boctx.getBoSession().getUser()
												.getName()
										+ " to be release by another context in another Thread ");
						ret = __getObject(boctx, owner, key);
					}
				}
			}
		}
		return ret;

	}

	private final boPoolable __getObject(EboContext boctx, String owner,
			Object key) {
		boPoolable ret = null;
		Hashtable keysht = (Hashtable) ContextKeys.get(owner);
		if (keysht != null) {
			ArrayList list = (ArrayList) keysht.get(key); // saca as
			// referencias
			if (list != null) {
				int size = list.size();
				for (int i = 0; i < size; i++) // verifica se alguma
				// referencia está livre
				{
					ret = (boPoolable) getFromMemoryArchive(boctx, owner,
							(String) list.get(i));
					if (ret != null) {
						return ret;
					}
				}
			}
		}
		return ret;
	}

	private final boPoolable getFromMemoryArchive(EboContext ctx, String owner,
			String uniqueId) {
		
    	boPoolable ret = getObjectFromPool( uniqueId );
        if( ret != null )
        {
        	synchronized (ret) {
	            if( ret.ownerContext == null  )
	            {
	            	ret.ownerContext = ctx.poolUniqueId();
		            ret.setEboContext( ctx );
		            ret.lastUsedTime = System.currentTimeMillis();
		            
		            if ( !ret.owners.contains( owner )) {
		                ret.owners.add( owner );
		            }
		        
		        	HashMap list;
		            list = (HashMap)OwnedObjects.get( owner );
		            if( list == null ) {
		                OwnedObjects.put( owner, list = new HashMap() );
		            }
		            if ( !list.containsKey( ret.poolUniqueId()  ) ) {
		                list.put( ret.poolUniqueId(), null );
		            }
		            
		            ctx.ObjectsInContext.add( ret.poolUniqueId() );
	        	}
	       }
        }
		return ret;
	}

	private final boPoolable getObjectFromPool(Object uniqueId) {
		boPoolable ret = (boPoolable) ObjectPool.get(uniqueId);
		if (ret == null) {
			WeakReference wrObj = (WeakReference) WeakObjectPool.get(uniqueId);
			if (wrObj != null) {
				ret = (boPoolable) wrObj.get();
			}
		}
		return ret;
	}

	private final boPoolable removeObjectFromPool(Object uniqueId) {
		boPoolable ret = (boPoolable) ObjectPool.remove(uniqueId);
		if (ret == null) {
			WeakReference wrObj = (WeakReference) WeakObjectPool.remove(uniqueId);
			if (wrObj != null) {
				ret = (boPoolable) wrObj.get();
			}
		}
        else {
        	POOL_SIZE.set( ObjectPool.size() );
        }
		
		if (ret != null && ret.keys != null) {
			if (ret.owners != null && ret.owners.size() > 0) {
				Iterator it = ret.owners.iterator();
				while (it.hasNext()) {
					Object ownerKey = it.next();
					removeObjectKeysFromOwner(ret.poolUniqueId(), ret.keys,
							ownerKey);
				}
			}
			ObjectPoolUSERNAMES.remove(ret.poolUniqueId());
		}
		return ret;
	}

	private final void removeObjectKeysFromOwner(Object uniqueId,
			Object[] keys, Object ownerKey) {
		Hashtable ownerKeysMap = (Hashtable) ContextKeys.get(ownerKey);
		if (ownerKeysMap != null && keys != null) {
			for (int i = 0; i < keys.length; i++) {
				if (keys[i] != null)
					ownerKeysMap.remove(keys[i]);
			}
			if (ownerKeysMap.size() == 0) {
				ContextKeys.remove(ownerKey);
			}
		}
		HashMap m = (HashMap) OwnedObjects.get(ownerKey);
		if (m != null) {
			m.remove(uniqueId);
		}
	}

	public final void realeaseObjects(String owner, EboContext ctx) {
		if (ctx.ObjectsInContext != null) {
			for (int i = 0; i < ctx.ObjectsInContext.size(); i++) {
				Object xremoved = ctx.ObjectsInContext.get(i);
				boPoolable object = getObjectFromPool(xremoved);

				if (object != null) {
					if (object.poolIsStateFull()) {
						object.removeEboContext();
						object.ownerContext = null;
					} else {
						object.owners.remove(owner);
						removeObjectKeysFromOwner(object.poolUniqueId(),
								object.keys, owner);
						object.owners.remove(ctx.poolUniqueId());
						removeObjectKeysFromOwner(object.poolUniqueId(),
								object.keys, ctx.poolUniqueId());

						if (!ctx.poolUniqueId().equals(
								ctx.getPreferredPoolObjectOwner())) {
							object.owners.remove(ctx
									.getPreferredPoolObjectOwner());
							removeObjectKeysFromOwner(object.poolUniqueId(),
									object.keys, ctx
											.getPreferredPoolObjectOwner());
						}

						if (object.owners.size() == 0) {
							removeObjectFromPool(object.poolUniqueId());
							object.owners.remove(owner);
							ctx.ObjectsInContext.remove(i);
							i--;
						}
					}
					// if( object.havePoolChilds() )
					// {
					// realeaseObjects( object.poolUniqueId(), ctx );
					// }
				} else {
					ctx.ObjectsInContext.remove(i);
					i--;
				}
			}
		}
	}

	public final void realeaseAllObjects(String owner) {
    	boolean objectRemoved = false;
		ContextKeys.remove(owner);
		HashMap objectToRelease = (HashMap) OwnedObjects.remove(owner);
		if (objectToRelease != null) {
			Iterator it = objectToRelease.keySet().iterator();
			while (it.hasNext()) {
				boPoolable objtorem = getObjectFromPool(it.next());
				if (objtorem != null) {
					objtorem.owners.remove(owner);
					if (objtorem.owners.size() == 0) {

						Object ret = ObjectPool.remove( objtorem.poolUniqueId() );
                        objectRemoved = objectRemoved || ret != null;
						
						WeakObjectPool.remove(objtorem.poolUniqueId());
						ObjectPoolUSERNAMES.remove(objtorem.poolUniqueId());
						OwnedObjects.remove(objtorem.poolUniqueId());
						if (objtorem.havePoolChilds()) {
							realeaseAllObjects(objtorem.poolUniqueId());
						}
					}
				}
			}
		}
        if( objectRemoved ) {
        	POOL_SIZE.set( ObjectPool.size() );
        }
		
	}

	public final void destroyObject(boPoolable object) {
		removeObjectFromPool(object.poolUniqueId());
		OwnedObjects.remove(object);
		ContextKeys.remove(object);
	}

	public final boPoolable getObjectById(String id) {
		return getObjectFromPool(id);
	}

	private static class PoolWeakReference extends WeakReference {
		Object[] keys;
		List owners;
		String uniqueId;

		public PoolWeakReference(Object[] keys, boPoolable referent,
				ReferenceQueue queue) {
			super(referent, queue);
			this.keys = keys;
			this.owners = referent.owners;
			this.uniqueId = referent.poolUniqueId();
		}
	}

	public final String dumpPool() throws boRuntimeException {
		try {
			CharArrayWriter w = new CharArrayWriter();
			dumpPool(w);
			return w.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public final void dumpPool(Writer toRet) throws boRuntimeException,
			IOException {
		Enumeration xobjpool = ObjectPool.keys();

		toRet.write("<h1>Objectos na pool</h1>");
		while (xobjpool.hasMoreElements()) {
			Object key = xobjpool.nextElement();
			boPoolable o = getObjectFromPool(key);
			String username = (String) ObjectPoolUSERNAMES.get(key);
			if (o instanceof boObject) {
				boObject bo = ((boObject) o);
				toRet.write("<br>"
						+ key
						+ " -> "
						+ bo.getBoui()
						+ " <span style='color:blue'>"
						+ bo.toString()
						+ "</span> "
						+ (bo.IsStateFull ? "<b>statefull</b>" : "")
						+ (bo.isChanged() ? "<b> changed</b>" : "")
						+ (o.ownerContext != null ? " context <b> "
								+ o.ownerContext + "</b> ) " : "") + " owner "
						+ o.owners.toString()
						+ "<span style='font:8px arial;color:green' >"
						+ username + "</span>");
				if (o.getEboContext() != null) {
					toRet.write("<span style='color:red'>"
							+ o.getEboContext().poolUniqueId() + "</span>");
				}

			} else {
				toRet.write("<br>"
						+ key
						+ " -> "
						+ (o.ownerContext != null ? " context <b> "
								+ o.ownerContext + "</b> ) " : "") + " owner "
						+ o.owners.toString()
						+ "<span style='font:8px arial;color:green' >"
						+ username + "</span>");
				if (o.getEboContext() != null) {
					toRet.write("<span style='color:red'>"
							+ o.getEboContext().poolUniqueId() + "</span>");
				}
			}
		}

		Enumeration xx = ContextKeys.keys();
		toRet.write("<h1>Context Keys</h1>");
		while (xx.hasMoreElements()) {
			Object key = xx.nextElement();
			Hashtable ownertable = (Hashtable) ContextKeys.get(key);

			toRet.write(" <b>OWNER  : " + key + "</b><br><hr>");
			Enumeration xx2 = ownertable.keys();
			while (xx2.hasMoreElements()) {
				Object key2 = xx2.nextElement();
				toRet.write(" " + key2 + " <br> ");

				ArrayList arraykeys = (ArrayList) ownertable.get(key);
				if (arraykeys != null) {
					for (int i = 0; i < arraykeys.size(); i++) {
						toRet.write("<br>REFERENCIA " + arraykeys.get(i));
					}
				}

			}
			HashMap xo = (HashMap) OwnedObjects.get(key);
			if (xo != null) {
				toRet.write("<span style='color:green'>OWN :" + xo.toString()
						+ "</span>");
			}
			toRet.write("<br>");
		}
	}

}
