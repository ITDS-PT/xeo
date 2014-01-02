/*Enconding=UTF-8*/
package netgest.bo.security;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import netgest.bo.boConfig;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.utils.PackageUtils;

public final class securityRights 
{
    private static Hashtable policyObjects; 
    private static Hashtable performerPolicyObjects;
    private static boolean matchallkeys;
    
    public static final byte READ     =1;
    public static final byte WRITE    =2;
    public static final byte ADD      =3;
    public static final byte DELETE   =4;
    public static final byte EXECUTE   =5;
    
    
    static
    {
        policyObjects             = new Hashtable();
        performerPolicyObjects    = new Hashtable();
        Properties prop=boConfig.getSecurityConfig();
        boolean matchallkeys=new Boolean((String)prop.getProperty("matchallkeys")).booleanValue();
        
        
    }
    public securityRights()
    {
    }     
        
    public static boolean hasRights( EboContext ctx , String className) throws boRuntimeException
    {
      boObject obj=boObject.getBoManager().loadObject(ctx,ctx.getBoSession().getPerformerBoui()); 
      return hasRights(obj,className,ctx.getBoSession().getPerformerBoui());
    }
    public static boolean hasRights( EboContext ctx , String className,byte action) throws boRuntimeException
    {
      boObject obj=boObject.getBoManager().loadObject(ctx,ctx.getBoSession().getPerformerBoui()); 
      return hasRights(obj,className,ctx.getBoSession().getPerformerBoui(),action);
    }
    
    public static boolean hasRights( EboContext ctx , String className,String attributename) throws boRuntimeException
    {
      boObject obj=boObject.getBoManager().loadObject(ctx,ctx.getBoSession().getPerformerBoui()); 
      return hasRights(obj,className,attributename,ctx.getBoSession().getPerformerBoui());
    }

    public static boolean hasRights( EboContext ctx , String className,String attributename,byte action) throws boRuntimeException
    {
      boObject obj=boObject.getBoManager().loadObject(ctx,ctx.getBoSession().getPerformerBoui());     
      return hasRights(obj,className,attributename,ctx.getBoSession().getPerformerBoui(),action);
    }

    public static boolean hasRightsToMethod( EboContext ctx , String className,String methodname) throws boRuntimeException
    {
      boObject obj=boObject.getBoManager().loadObject(ctx,ctx.getBoSession().getPerformerBoui());     
      return hasRightsToMethod(obj,className,methodname,ctx.getBoSession().getPerformerBoui());
    }
    
    
    public static boolean hasRights( boObject ctxObj , String className , long performerBoui ) throws boRuntimeException
    {
      try
      {
        return  _hasRights( ctxObj, className , null , performerBoui,READ );
      }
      catch (SQLException e)
      {
        /* CRIAR NOVA EXCEPCAO */
        throw new boRuntimeException(securityRights.class.getName() +
        ".hasRights(boObject,classname,long)", "BO-3999", null, "");              
      }
    }

    public static boolean hasRightsToMethod( boObject ctxObj , String className ,String methodname, long performerBoui ) throws boRuntimeException
    {
      try
      {
        return  _hasRights( ctxObj, className , methodname , performerBoui,EXECUTE );
      }
      catch (SQLException e)
      {
        /* CRIAR NOVA EXCEPCAO */
        throw new boRuntimeException(securityRights.class.getName() +
        ".hasRights(boObject,classname,long)", "BO-3999", null, "");              
      }
    }

    
    public static boolean hasRights( boObject ctxObj , String className , String attributeName , long performerBoui ) throws boRuntimeException
    {
      try
      {
        if(performerBoui <= 0) return true;
        return  _hasRights( ctxObj, className , attributeName , performerBoui,READ );
      }
      catch (SQLException e)
      {
        /* CRIAR NOVA EXCEPCAO */
        throw new boRuntimeException(securityRights.class.getName() +
        ".hasRights(boObject,classname,attributeName,long)", "BO-3999", null, "");              
      }
    }

    public static boolean hasRights( boObject ctxObj , String className , long performerBoui, byte action ) throws boRuntimeException
    {  
       return hasRights( ctxObj, className , null , performerBoui,action );
    }

    
    public static boolean hasRights( boObject ctxObj , String className , String attributeName , long performerBoui,byte action ) throws boRuntimeException
    {
      try
      {
        boolean retVal=false;
        if (action!=securityRights.READ)
          if (!_hasRights( ctxObj, className , attributeName , performerBoui,securityRights.READ )) 
            return false;        
        
        retVal=_hasRights( ctxObj, className , attributeName , performerBoui,action );             
        return retVal;
      }
      catch (SQLException e)
      {
        /* CRIAR NOVA EXCEPCAO */
        throw new boRuntimeException(securityRights.class.getName() +
        ".hasRights(boObject,classname,attributename,long,byte)", "BO-3999", null, "");              
      }        
    }

    
    public static void cleanPolicies()
    {
		runtimePolicyObject.policyObjects.clear();
		runtimePolicy.objectPolicyRules.clear();
        securityRights.policyObjects.clear();
        securityRights.performerPolicyObjects.clear();
		
    }

    public static void cleanObjectPolicies()
    {
		runtimePolicyObject.policyObjects.clear();
        securityRights.policyObjects.clear();
		runtimePolicy.objectPolicyRules.clear();
    }
    
    public static void cleanPerfomerPolicies()
    {
        securityRights.performerPolicyObjects.clear();
    }    
    
    public static boolean hasRightsToPackage( boObject perf , String packageName ) throws boRuntimeException
    {
       ArrayList packNames = PackageUtils.getPackagesNamesFromXEOUser(perf);
       if(packNames != null)
       {
        for (int i = 0; i < packNames.size(); i++) 
        {
            if(((String)packNames.get(i)).equalsIgnoreCase(packageName))
            {
                return true;
            }
        }
       }
        return false;
    }
    public static boolean hasRightsToRole( boObject perf , String roleName ) throws boRuntimeException
    {
        boolean found=false;
        bridgeHandler bp=perf.getBridge("roles");
        bp.beforeFirst();
        while ( bp.next() && !found)
        {
            if ( bp.getObject().getAttribute("name").getValueString().equalsIgnoreCase(roleName) )
            {
                found=true;
            }
        }
        return found;
  
    }
    
    public static long[] getSameGroupPerformers(EboContext ctx,long performerBoui ) throws boRuntimeException,SQLException
    {
        runtimePolicyPerformer rpp=( runtimePolicyPerformer ) securityRights.performerPolicyObjects.get(""+performerBoui);
        if ( rpp == null )
        {
            boObject oPerf=boObject.getBoManager().loadObject( ctx, performerBoui );
            rpp = new runtimePolicyPerformer( oPerf );
            securityRights.performerPolicyObjects.put(""+performerBoui, rpp );
        }
        return rpp.p_same_group_users;
    }
    

    public static long[] getPerformerGroupsKeys( EboContext ctx,long performerBoui ) throws boRuntimeException,SQLException
    {
        runtimePolicyPerformer rpp=( runtimePolicyPerformer ) securityRights.performerPolicyObjects.get(""+performerBoui);
        if ( rpp == null )
        {
            boObject oPerf=boObject.getBoManager().loadObject( ctx, performerBoui );
            rpp = new runtimePolicyPerformer( oPerf );
            securityRights.performerPolicyObjects.put(""+performerBoui, rpp );
        }
        return rpp.p_groups;
    }

    public static long[] getPerformerFlatGroupsKeys( EboContext ctx,long performerBoui ) throws boRuntimeException,SQLException
    {
        runtimePolicyPerformer rpp=( runtimePolicyPerformer ) securityRights.performerPolicyObjects.get(""+performerBoui);
        if ( rpp == null )
        {
            boObject oPerf=boObject.getBoManager().loadObject( ctx, performerBoui );
            rpp = new runtimePolicyPerformer( oPerf );
            securityRights.performerPolicyObjects.put(""+performerBoui, rpp );
            
        }
        return rpp.p_flat_groups;
    }

    public static long[] getPerformerAllKeys( EboContext ctx,long performerBoui ) throws boRuntimeException,SQLException
    {
        runtimePolicyPerformer rpp=( runtimePolicyPerformer ) securityRights.performerPolicyObjects.get(""+performerBoui);
        
        if ( rpp == null )
        {
                boObject oPerf=boObject.getBoManager().loadObject( ctx, performerBoui );
                rpp = new runtimePolicyPerformer( oPerf );
                securityRights.performerPolicyObjects.put(""+performerBoui, rpp );
            
        }
        return rpp.p_keys;
    }

    public static long[] getPerformerFlatKeys( EboContext ctx,long performerBoui ) throws boRuntimeException,SQLException
    {
        runtimePolicyPerformer rpp=( runtimePolicyPerformer ) securityRights.performerPolicyObjects.get(""+performerBoui);
        
        if ( rpp == null )
        {
                boObject oPerf=boObject.getBoManager().loadObject( ctx, performerBoui );
                rpp = new runtimePolicyPerformer( oPerf );
                securityRights.performerPolicyObjects.put(""+performerBoui, rpp );
            
        }
        return rpp.p_flat_keys;
    }

    
    public static long[] getPerformerXWF( EboContext ctx,long performerBoui ) throws boRuntimeException,SQLException
    {
        runtimePolicyPerformer rpp=( runtimePolicyPerformer ) securityRights.performerPolicyObjects.get(""+performerBoui);
        
        if ( rpp == null )
        {
                boObject oPerf=boObject.getBoManager().loadObject( ctx, performerBoui );
                rpp = new runtimePolicyPerformer( oPerf );
                securityRights.performerPolicyObjects.put(""+performerBoui, rpp );
            
        }
        return rpp.p_keysXWF;
    }
    private static boolean _hasRights( boObject ctxObj ,  String className , String attributeName , long performerBoui,byte action ) throws boRuntimeException,SQLException
    {          
        
        runtimePolicyObject rpo=null;

        runtimePolicyPerformer rpp=( runtimePolicyPerformer ) securityRights.performerPolicyObjects.get(""+performerBoui);
        
        if ( rpp == null )
        {
                
                boObject oPerf=ctxObj.getBoManager().loadObject( ctxObj.getEboContext(), performerBoui );
                boolean sec = oPerf.isCheckSecurity();
                oPerf.setCheckSecurity( false );
                rpp = new runtimePolicyPerformer( oPerf );
                oPerf.setCheckSecurity( sec );
                securityRights.performerPolicyObjects.put(""+performerBoui, rpp );
        }
        
        if ( rpp.isSupervisor )
        {
            return true;
        }        

        
        if ( attributeName == null)
        {
            rpo=( runtimePolicyObject ) securityRights.policyObjects.get( className+action );    
        }
        else
        {
            rpo=( runtimePolicyObject ) securityRights.policyObjects.get( className+":"+attributeName+action );
        }
            
        if ( rpo==null )
        {
        
                rpo = new runtimePolicyObject( ctxObj , className , attributeName,action  );
                
                if ( attributeName == null )
                {
                    securityRights.policyObjects.put( className+action , rpo );
                }
                else
                {
                    securityRights.policyObjects.put( className+":"+attributeName+action , rpo );
                }        
        }
        
        //--------------Verificar Security Level
        if ( rpp.p_securityLevel < rpo.p_securityLevel )
        {
            return false;
        }        
        
        //Se ainda não estiverem sido definidas permissões para o Objecto e para a acção em causa então a acção pode ser realizada
        
        if (rpo.p_compartments.length==0 && rpo.p_groups.length==0 && rpo.p_packages.length==0 && rpo.p_roles.length==0)
          return true;         
        
        
        //-------------Verificar compartments 
        
        for (int i = 0; i < rpo.p_compartments.length ; i++) 
        {
            boolean found=false;
            for (int j = 0; !found && j < rpp.p_compartments.length ; j++) 
            {
               if ( rpp.p_compartments[j] == rpo.p_compartments[i] )
               {
                    found=true;     
                    if (!matchallkeys) return true;
               }
            }
            if( !found && matchallkeys)
            {
                return false;
            }
        }
        
        //-----------Verificar Groups
        
        for (int i = 0; i < rpo.p_groups.length ; i++) 
        {
            boolean found=false;
            for (int j = 0; !found && j < rpp.p_groups.length ; j++) 
            {
               if ( rpp.p_groups[j] == rpo.p_groups[i] )
               {
                    found=true;                   
                    if (!matchallkeys) return true;                    
               }
            }
            if( !found && matchallkeys )
            {
                return false;
            }
        }
        
        //-----------Verificar packages
        for (int i = 0; i < rpo.p_packages.length ; i++) 
        {
            boolean found=false;
            for (int j = 0; !found && j < rpp.p_packages.length ; j++) 
            {
               if ( rpp.p_packages[j] == rpo.p_packages[i] )
               {
                    found=true;                   
                    if (!matchallkeys) return true;                    
               }
            }
            if( !found && matchallkeys)
            {
                return false;
            }
        }
        
        //------ Verificar Roles
        for (int i = 0; i < rpo.p_roles.length ; i++) 
        {
            boolean found=false;
            for (int j = 0; !found && j < rpp.p_roles.length ; j++) 
            {
               if ( rpp.p_roles[j] == rpo.p_roles[i] )
               {
                    found=true;                   
                    if (!matchallkeys) return true;                    
               }
            }
            if( !found && matchallkeys )
            {
                return false;
            }
        }
        
        
        
    
        if (matchallkeys)return true;
        else return false;
        
    }
    
    public static long[] getPerformerKeys( EboContext ctx) throws boRuntimeException
    {
      try
      {
        runtimePolicyPerformer rpp=( runtimePolicyPerformer ) securityRights.performerPolicyObjects.get(""+ctx.getBoSession().getPerformerBoui());
        
        if ( rpp == null )
        {
               boObject oPerf=boObject.getBoManager().loadObject(ctx,ctx.getBoSession().getPerformerBoui()); 
               
               rpp = new runtimePolicyPerformer( oPerf );
               securityRights.performerPolicyObjects.put(""+oPerf.getBoui(), rpp );
            
        }
        return rpp.p_keys;        
      }
      catch (SQLException e)
      {
        /* CRIAR NOVA EXCEPCAO */
        throw new boRuntimeException(securityRights.class.getName() +
        ".hasRights(boObject,classname,attributename,long,byte)", "BO-3999", null, "");                      
      }
    }
    
    public static long[] getPerformerKeys( boObject ctxObj , long performerBoui ) throws boRuntimeException
    {
      try
      {
        runtimePolicyPerformer rpp=( runtimePolicyPerformer ) securityRights.performerPolicyObjects.get(""+performerBoui);
        
        if ( rpp == null )
        {
               boObject oPerf=ctxObj.getBoManager().loadObject( ctxObj.getEboContext(), performerBoui );
               
               rpp = new runtimePolicyPerformer( oPerf );
               securityRights.performerPolicyObjects.put(""+performerBoui, rpp );
            
        }
        return rpp.p_keys;
      }
      catch (SQLException e)
      {
        /* CRIAR NOVA EXCEPCAO */
        throw new boRuntimeException(securityRights.class.getName() +
        ".hasRights(boObject,classname,attributename,long,byte)", "BO-3999", null, "");                      
      }        
    }
    
    public static boolean isSupervisor(boObject perf) throws boRuntimeException
    {
      try
      {
        long performerBoui=perf.getBoui();
        runtimePolicyPerformer rpp=( runtimePolicyPerformer ) securityRights.performerPolicyObjects.get(""+performerBoui);        
        if ( rpp == null )
        {
               rpp = new runtimePolicyPerformer( perf );
               securityRights.performerPolicyObjects.put(""+performerBoui, rpp );
        }     
        return rpp.isSupervisor;
      }
      catch (SQLException e)
      {
        /* CRIAR NOVA EXCEPCAO */
        throw new boRuntimeException(securityRights.class.getName() +
        ".hasRights(boObject,classname,attributename,long,byte)", "BO-3999", null, "");                      
      }
    }
      
    public static boolean isSupervisor(EboContext boctx) throws boRuntimeException
    {
      try
      {
        long performerBoui=boctx.getBoSession().getPerformerBoui();
        runtimePolicyPerformer rpp=( runtimePolicyPerformer ) securityRights.performerPolicyObjects.get(""+performerBoui);        
        if ( rpp == null )
        {
               boObject perf=boObject.getBoManager().loadObject( boctx, performerBoui );
               rpp = new runtimePolicyPerformer( perf );
               securityRights.performerPolicyObjects.put(""+performerBoui, rpp );
        }     
        return rpp.isSupervisor;
      }
      catch (SQLException e)
      {
        /* CRIAR NOVA EXCEPCAO */
        throw new boRuntimeException(securityRights.class.getName() +
        ".hasRights(boObject,classname,attributename,long,byte)", "BO-3999", null, "");                      
      }      
    }
    

    public static boolean canRead(EboContext ctx,String classname) throws boRuntimeException
    {
      return canRead(ctx,classname,null);
    }
    
    public static boolean canRead(EboContext ctx,String classname,String attributename)throws boRuntimeException
    {
        return hasRights(ctx,classname,attributename,securityRights.READ);
    }

    public static boolean canWrite(EboContext ctx,String classname) throws boRuntimeException
    {
        return canWrite(ctx,classname,null);     
    }

    public static boolean canWrite(EboContext ctx,String classname,String attributename) throws boRuntimeException
    {
      return hasRights(ctx,classname,attributename,securityRights.WRITE);
    }

    public static boolean canDelete(EboContext ctx,String classname) throws boRuntimeException
    {
      return canDelete(ctx,classname,null);      
    }
    public static boolean canDelete(EboContext ctx,String classname,String attributename) throws boRuntimeException
    {
      return hasRights(ctx,classname,attributename,securityRights.DELETE);      
    }
    
    public static boolean canAdd(EboContext ctx,String classname) throws boRuntimeException
    {
      return canAdd(ctx,classname,null);      
    }

    public static boolean canAdd(EboContext ctx,String classname,String attributename) throws boRuntimeException
    {
      return hasRights(ctx,classname,attributename,securityRights.ADD);      
    }

    public static boolean canExecute(EboContext ctx,String classname,String methodname) throws boRuntimeException
    {
      return hasRightsToMethod(ctx,classname,methodname);
    }
    
/*    public static String[] cantReadClasses(EboContext ctx) throws boRuntimeException
    {
      return cantReadClasses(ctx,null);
    } */
    
    public static String[] cantReadClasses(EboContext ctx,String mainClass) throws boRuntimeException
    {
      String[] toRet=new String[500];
      boDefHandler def=boDefHandler.getBoDefinition(mainClass);
      int j=0;
      if (!canRead(ctx,mainClass)) toRet[0]=def.getName();
      boDefHandler[] subC=def.getTreeSubClasses();
      for (int i=0;i<subC.length;i++)
      {        
        String cName=subC[i].getName();
        if (!canRead(ctx,cName))
        {
          if (i>0 || (i==0 && toRet[0]!=null))j++;
          toRet[j]=cName;
        }
      }
      String[] aux=new String[j+1];
      System.arraycopy(toRet,0,aux,0,j+1);      
      return aux;
    }
}