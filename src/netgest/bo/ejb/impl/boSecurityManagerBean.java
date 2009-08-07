/*Enconding=UTF-8*/
package netgest.bo.ejb.impl;

import java.math.BigDecimal;

import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import netgest.bo.data.DataSet;
import netgest.bo.data.IXEODataManager;
import netgest.bo.data.XEODataManagerKey;
import netgest.bo.ejb.boManagerLocal;
import netgest.bo.plugins.DataPluginManager;
import netgest.bo.plugins.IDataPlugin;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.security.securityOPL;
import netgest.bo.security.securityRights;


public class boSecurityManagerBean implements SessionBean, boManagerLocal
{

	private boolean versioning = true;

    public void ejbActivate()
    {
    }

    public void ejbPassivate()
    {
    }

    public void ejbRemove()
    {
    }

    public void ejbCreate()
    {
    }

    public void setSessionContext(SessionContext ctx)
    {
    }

    private static boManagerLocal getBoManager() throws boRuntimeException {
            return new boManagerBean();
    
//        try {
//            return ((boManagerLocalHome)boContextFactory.getContext().lookup("java:comp/env/ejb/boManagerLocal")).create();
//        } catch (NamingException e) {
//            throw new RuntimeException(e.getMessage());
//        } catch (CreateException e) {
//            throw new RuntimeException(e.getMessage());
//        }
    }

    public boObject createObject(EboContext ctx, long classboui) throws boRuntimeException
    {        
       boObject object = this.getBoManager().loadObject(ctx, "Ebo_ClsReg", classboui);
       if (securityRights.hasRights(object,object.getName(),ctx.getBoSession().getPerformerBoui(),securityRights.ADD) ) 
       {
         object.setCheckSecurity(true);
         return object;
       }
       else 
        {
          /* CRIAR NOVA EXCEPCAO */
          throw new boRuntimeException(boSecurityManagerBean.class.getName() +
              ".createObject(EboContext,long)", "BO-3220", null, "" + classboui);              
        }
    }
    
    public boObject createObjectWithParent(EboContext ctx, String name, long parentBoui )
        throws boRuntimeException
    {
        //TODO:Implement mehto
        return null;
    }
    

    public boObject createObject(EboContext ctx, boObject objectFrom) throws boRuntimeException
    {
       boObject object=null;
       if (securityRights.hasRights(objectFrom,objectFrom.getName(),ctx.getBoSession().getPerformerBoui(),securityRights.ADD) ) 
       {
        object=this.getBoManager().createObject(ctx,objectFrom);
        object.setCheckSecurity(true);
        return object;
       }
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".createObject(EboContext,boObject)", "BO-3220", null, "" + objectFrom.getBoui());
       }                
    }
    public boObject createObject(EboContext ctx, String newObjName, boObject objectFrom) throws boRuntimeException
    {
       boObject object=null;
       if (securityRights.hasRights(objectFrom,objectFrom.getName(),ctx.getBoSession().getPerformerBoui(),securityRights.ADD) ) 
       {
        object=this.getBoManager().createObject(ctx,objectFrom);
        object.setCheckSecurity(true);
        return object;
       }
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".createObject(EboContext,boObject)", "BO-3220", null, "" + objectFrom.getBoui());
       }                
    }

    public boObject createObject(EboContext ctx, String name) throws boRuntimeException
    {
       boObject object=this.getBoManager().createObject(ctx,name);
       if (securityRights.hasRights(object,object.getName(),ctx.getBoSession().getPerformerBoui(),securityRights.ADD) ) 
       {
        object.setCheckSecurity(true);
        return object;
       }
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".createObject(EboContext,String)", "BO-3220", null, "" + object.getBoui());
       }                
    }

    public boObject createObject(EboContext ctx, String name, long withBoui) throws boRuntimeException
    {
       boObject object=this.getBoManager().createObject(ctx,name,withBoui);
       if (securityRights.hasRights(object,object.getName(),ctx.getBoSession().getPerformerBoui(),securityRights.ADD) ) 
       {
        object.setCheckSecurity(true);       
        return object;
       }
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".createObject(EboContext,String,long)", "BO-3220", null, "" + object.getBoui());
       }           
    }


    public boObject createObject(EboContext ctx, String name, boolean modeTemplate)
        throws boRuntimeException
    {
       boObject object=this.getBoManager().createObject(ctx,name,modeTemplate);
       if (securityRights.hasRights(object,object.getName(),ctx.getBoSession().getPerformerBoui(),securityRights.ADD) ) 
       {
        object.setCheckSecurity(true);       
        return object;
       }
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".createObject(EboContext,String,boolean)", "BO-3220", null, "" + object.getBoui());
       }                 
    }

    public boObject createObject(EboContext ctx, String name, DataSet data)
        throws boRuntimeException
    {
       boObject object=this.getBoManager().createObject(ctx,name,data);
       if (securityRights.hasRights(object,object.getName(),ctx.getBoSession().getPerformerBoui(),securityRights.ADD) ) 
       {
        object.setCheckSecurity(true);       
        return object;
       }
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".createObject(EboContext,String,DataSet)", "BO-3220", null, "" + object.getBoui());
       }                            
    }

    public boObject loadObject(EboContext ctx, String name, long boui) throws boRuntimeException
    {
      boObject object=this.getBoManager().loadObject(ctx,name,boui);
       if (securityRights.hasRights(object,object.getName(),ctx.getBoSession().getPerformerBoui()) 
           && securityOPL.canRead(object)) 
       {
         object.setCheckSecurity(true);           
         checkObject( ctx, object );
         return object;
       }
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".loadObject(EboContext,String,long)", "BO-3220", null, "" + object.getBoui());
       }                            
      
    }

    public boObject loadObject(EboContext ctx, String boql, Object[] sqlargs)
        throws boRuntimeException
    {
      boObject object=this.getBoManager().loadObject(ctx,boql,sqlargs);
       if (securityRights.hasRights(object,object.getName(),ctx.getBoSession().getPerformerBoui()) 
           && securityOPL.canRead(object)) 
       {
         object.setCheckSecurity(true);           
         checkObject( ctx, object );
         return object;
       }
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".loadObject(EboContext,String,Object[])", "BO-3220", null, "" + object.getBoui());
       }                            
    }

    public boObject loadObject(EboContext ctx, String boql) throws boRuntimeException
    {
      boObject object=this.getBoManager().loadObject(ctx,boql);
       if (securityRights.hasRights(object,object.getName(),ctx.getBoSession().getPerformerBoui()) 
           && securityOPL.canRead(object)) 
       {
        object.setCheckSecurity(true);           
        checkObject( ctx, object );
        return object;
       }
       else
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".loadObject(EboContext,String)", "BO-3220", null, "" + object.getBoui());
       }                            
    }

    public boObject loadObject(EboContext ctx, String name, String sql, Object[] sqlargs)
        throws boRuntimeException
    {
      boObject object=this.getBoManager().loadObject(ctx,name,sql,sqlargs);
       if (securityRights.hasRights(object,object.getName(),ctx.getBoSession().getPerformerBoui()) 
           && securityOPL.canRead(object)) 
       {
        object.setCheckSecurity(true);           
        checkObject( ctx, object );
        return object;
       }
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".loadObject(EboContext,String,String,Object[])", "BO-3220", null, "" + object.getBoui());
       }                            
    }

    public boObject loadObject(EboContext ctx, String name, String sql) throws boRuntimeException
    {
        return loadObject(ctx, name, sql, null);
    }

    public void preLoadObjects(EboContext ctx, DataSet dataSet ) throws boRuntimeException
    {
        this.getBoManager().preLoadObjects( ctx , dataSet );
    }

    public void preLoadObjects(EboContext ctx, long[] bouis) throws boRuntimeException
    {
        this.getBoManager().preLoadObjects(ctx,bouis);        
    }

    public boObject loadObject(EboContext ctx, long boui) throws boRuntimeException
    {
       boObject object=this.getBoManager().loadObject(ctx,boui);
       if (securityRights.hasRights(object,object.getName(),ctx.getBoSession().getPerformerBoui()) 
           && securityOPL.canRead(object)) 
       {
          object.setCheckSecurity(true);                    
          checkObject( ctx, object );
          return object;
       }
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".loadObject(EboContext,long)", "BO-3220", null, "" + object.getBoui());
       }                            

    }

    public boObject loadObjectAs(EboContext ctx, long boui, String className) throws boRuntimeException
    {
       boObject object=this.getBoManager().loadObjectAs(ctx,boui, className);
       if (securityRights.hasRights(object,object.getName(),ctx.getBoSession().getPerformerBoui()) 
           && securityOPL.canRead(object)) 
       {
          object.setCheckSecurity(true);                    
          checkObject( ctx, object );
          return object;
       }
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".loadObject(EboContext,long)", "BO-3220", null, "" + object.getBoui());
       }                            

    }

    public String getClassNameFromBOUI(EboContext ctx, long boui) throws boRuntimeException
    {
      return this.getBoManager().getClassNameFromBOUI(ctx,boui);
    }

    public boObject seedObject(EboContext ctx, boObject obj) throws boRuntimeException
    {
      return this.getBoManager().seedObject(ctx,obj);
    }

    public boObject bindObject(EboContext ctx, String name, DataSet data) throws boRuntimeException
    {
        return this.getBoManager().bindObject(ctx,name,data);
    }

    public boObject updateObject(EboContext ctx, boObject bobj) throws boRuntimeException
    {
    	return updateObject(ctx, bobj, true, false);
    }
     public boObject destroyForced(EboContext ctx, boObject bobj) throws boRuntimeException
    {
    	return this.getBoManager().destroyForced(ctx,bobj );
    }

    public boObject updateObject(EboContext ctx, boObject bobj, boolean runEvents, boolean forceAllInTransaction) throws boRuntimeException
    {
      boolean can=false;      
      if (bobj.getMode() == boObject.MODE_NEW) 
      {
        can=securityRights.hasRights(bobj,bobj.getName(),ctx.getBoSession().getPerformerBoui(),securityRights.ADD);
      }
      else if (bobj.getMode() == boObject.MODE_EDIT || bobj.getMode() == boObject.MODE_EDIT_TEMPLATE) 
      {
        can=(securityRights.hasRights(bobj,bobj.getName(),ctx.getBoSession().getPerformerBoui(),securityRights.WRITE) &&
            (securityOPL.canWrite(bobj) || securityOPL.hasFullControl(bobj)) && securityOPL.canRead(bobj));
      }
      else if (bobj.getMode() == boObject.MODE_DESTROY) 
      {
        can=(securityRights.hasRights(bobj,bobj.getName(),ctx.getBoSession().getPerformerBoui(),securityRights.DELETE) &&
            (securityOPL.canDelete(bobj) || securityOPL.hasFullControl(bobj) && securityOPL.canRead(bobj)));
      }

      if (can) 
        return this.getBoManager().updateObject(ctx,bobj, runEvents, forceAllInTransaction);
       else 
       {
         /* CRIAR NOVA EXCEPCAO */
         String error=null;
         if (bobj.getMode() == boObject.MODE_NEW || bobj.getMode() == boObject.MODE_EDIT || bobj.getMode() == boObject.MODE_EDIT_TEMPLATE) 
          error="BO-3200";
         else error="BO-3210";
         throw new boRuntimeException(boSecurityManagerBean.class.getName() +
             ".updateObject(EboContext,boObject)", error, null, "" + bobj.getBoui());
       }                            
    }

    public void makeAllObject(EboContext eboctx) throws boRuntimeException
    {
        this.getBoManager().makeAllObject(eboctx);
    }
    public void makeAllObject(EboContext eboctx, String schema) throws boRuntimeException
    {
        this.getBoManager().makeAllObject(eboctx, schema);
    }
    
    public boObject getObjectInContext( EboContext ctx, long boui ) throws boRuntimeException
    {
        return this.getBoManager().getObjectInContext( ctx, boui );
    }    
    public boObject lookByPrimaryKey(EboContext eboctx, String objectName, Object[] keys) throws boRuntimeException
    {
        boObject ret = null;
        IDataPlugin[] plugins = DataPluginManager.getPlugIns();
        for (int i = 0; i < plugins.length; i++) 
        {
            ret = plugins[i].lookByPrimaryKey( eboctx, objectName, keys );
        }
        if( ret == null )
        {
            if( keys.length == 1 )
            {
                if ( keys[0] instanceof Long )
                {
                    return loadObject( eboctx, ((Long)keys[1]).longValue() );
                }
                else if ( keys[0] instanceof BigDecimal )
                {
                    return loadObject( eboctx, ((BigDecimal)keys[1]).longValue() );
                }
                else if ( keys[0] instanceof String )
                {
                    try
                    {
                        long boui = Long.parseLong( keys[0].toString() );
                        return loadObject( eboctx, boui );
                    }
                    catch (NumberFormatException ex)
                    {
                            
                    }
                    
                }
            }
        }
        return ret;
    }
    
    private static final void checkObject( EboContext ctx, boObject object ) throws boRuntimeException
    {
         if( !securityRights.canWrite(ctx, object.getName() ) || !securityOPL.canWrite(object) )
         {
             object.setDisabled( false );
         }
    }

    public EJBLocalHome getEJBLocalHome() throws EJBException
    {
        return null;
    }

    public Object getPrimaryKey() throws EJBException
    {
        return null;
    }

    public void remove() throws RemoveException, EJBException
    {
    }

    public boolean isIdentical(EJBLocalObject p0) throws EJBException
    {
        return false;
    }
    

    public boObject loadObject(EboContext ctx, String objName, long boui,
			DataSet data) throws boRuntimeException {
    	return getBoManager().loadObject( ctx , objName, boui, data);
    }

	public void registerRemoteKey(EboContext ctx , XEODataManagerKey fd) throws boRuntimeException {
		getBoManager().registerRemoteKey( ctx, fd );
	}

}