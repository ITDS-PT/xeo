/*Enconding=UTF-8*/
package netgest.bo.security;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Vector;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.utils.*;

public class runtimePolicyPerformer 
{   
    public long p_securityLevel;
    public long[] p_groups;
    public long[] p_flat_groups;
    public long[] p_compartments;
    public long[] p_packages;
    public long[] p_roles;
    public long[] p_queues;
    public long[] p_keys;
    public long[] p_flat_keys;
    public long[] p_keysXWF;
    public long[] p_same_group_users;
    
    public boolean isSupervisor=false;
    
    public runtimePolicyPerformer( boObject performerObj ) throws boRuntimeException,SQLException
    {
        isSupervisor=isSupervisor(performerObj);
        
        boObjectList xwfPerformerList= boObjectList.list( performerObj.getEboContext(),"select xwfPerformerConfig where performer="+performerObj.getBoui(),false );
        ArrayList xk = new ArrayList();
        xwfPerformerList.beforeFirst();
        boolean haveMyBoui=false;
        while ( xwfPerformerList.next() )
        {
            bridgeHandler qs = xwfPerformerList.getObject().getBridge("wfQueues");
            boBridgeIterator it = qs.iterator();
            while ( it.next() )
            {
                 
                 xk.add( it.currentRow().getValue() );
                 if ( it.currentRow().getValueLong() == performerObj.getBoui() )
                 {
                     haveMyBoui = true;
                 }
            }
            
        }
        p_keysXWF = new long[ xk.size()+ ( haveMyBoui?0:1) ];
        int ij=0;
        for (; ij < xk.size() ; ij++) 
        {
            p_keysXWF[ ij ] = (( BigDecimal )xk.get(ij)).longValue();
        }
        if ( !haveMyBoui)
        {
            p_keysXWF[ ij ] =performerObj.getBoui(); 
        }
        
        bridgeHandler roles=performerObj.getBridge("roles");
        int numberRoles=(int)roles.getRecordCount();
        
        p_roles=new long[ numberRoles ];
        roles.beforeFirst();
        int i=0;
        while ( roles.next() )
        {
            p_roles[ i++ ] = roles.getValueLong();
        }
        
        bridgeHandler queues=performerObj.getBridge("queues");
        int numberQueues=(int)queues.getRecordCount();
        
        
        p_queues=new long[ numberQueues ];
        queues.beforeFirst();
        i=0;
        while ( queues.next() )
        {
            p_queues[ i++ ] = queues.getObject().getBoui();
        }
        
        bridgeHandler groups=performerObj.getBridge("groups");
        int numberGroups=(int)groups.getRecordCount();
        
        
        p_flat_groups=new long[ numberGroups ];
        groups.beforeFirst();
        i=0;
        while ( groups.next() )
        {
            p_flat_groups[ i++ ] = groups.getValueLong();
        }
        
        
        p_securityLevel=performerObj.getAttribute("securityLevel").getValueLong();
        if ( p_securityLevel < 0 ) p_securityLevel=0;
        
        bridgeHandler compartments=performerObj.getBridge("compartments");
        int numbercompartments=(int)compartments.getRecordCount();
        
        p_compartments=new long[ numbercompartments ];
        compartments.beforeFirst();
        i=0;
        while ( compartments.next() )
        {
            p_compartments[ i++ ] = compartments.getValueLong();
        }
        
        //packages        
        p_packages = PackageUtils.getPackagesBouiFromBridgePackages(performerObj) ;

        //bridgeHandler groups=performerObj.getBridge("groups");
        groups.beforeFirst();
        i=0;
        
        int groupIndex=0;
        long[] xgroups = new long[ 2000 ];
        
      //  String xsql="select g.boui from ebo_group g,ebo_group$childgroups ch where "+
      //  " g.Boui = ch.EBO_GROUP$0(+) "+
      //  " start with  g.boui= ? "+
      //  " connect by prior ch.ebo_group$1 =g.boui ";
        
        //TODO: MYSQL
        /* String xsql="select g.boui from oebo_group g,oebo_group$childgroups ch where" +
        " g.Boui = ch.PARENT$(+) "+
        " start with  g.boui= ? "+
        " connect by prior ch.CHILD$ =g.boui ";
        */

        while ( groups.next() )
        {
            long gboui = groups.getValueLong();
            groupIndex = getChildGroups(  performerObj, xgroups, groupIndex, gboui);
        }

        xgroups[ groupIndex++]= -2;        
        p_groups = new long[ groupIndex ];
        System.arraycopy( xgroups,0,p_groups,0, groupIndex );
         
        
                            
        
         p_keys       = getAllKeys( performerObj, false );
         p_flat_keys  = getAllKeys( performerObj, true );
         
         buildSameGroupUsers( performerObj.getEboContext(), performerObj.getBoui() );
          
    }
    
    private int getChildGroups( boObject performerObj, long[] xgroups, int groupIndex, long gboui ) throws SQLException {
        Connection cn= performerObj.getEboContext().getConnectionData();
        PreparedStatement pr=null;
        ResultSet rslt=null;
        boolean found;
        
        found = false;
        
        for (int j = 0; !found && j < groupIndex; j++) 
        {
           if ( xgroups[ j ] == gboui )
           {
               found=true;
           }
           
        }
        
        if( !found )
        {
           if ( groupIndex >= xgroups.length )
           {
               long[] grp=new long[ xgroups.length+30];
               System.arraycopy( xgroups,0,grp,0,xgroups.length );
               xgroups=grp;
           }
           xgroups[ groupIndex++]= gboui;
        }
        
        try {
	        String xsql="select child$ from oebo_group$childgroups ch where parent$ = ? ";
	        pr=cn.prepareStatement( xsql , ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );
            pr.setLong(1, gboui );
            rslt= pr.executeQuery();
            while (rslt.next())
            {
                 long g=rslt.getLong(1);
                 found = false;
                 for (int j = 0; !found && j < groupIndex; j++) 
                 {
                    if ( xgroups[ j ] == g )
                    {
                        found=true;
                    }
                    
                 }
                 if( !found )
                 {
                    if ( groupIndex >= xgroups.length )
                    {
                        long[] grp=new long[ xgroups.length+30];
                        System.arraycopy( xgroups,0,grp,0,xgroups.length );
                        xgroups=grp;
                    }
                    xgroups[ groupIndex++]= g;
                    groupIndex = getChildGroups(performerObj, xgroups, groupIndex, g );
                    
                 }
                 if( groupIndex >= 49 ) {
                	 break;
                 }
            }
        }
        finally {
        	if( rslt != null )
        		rslt.close();
        	if( pr != null )
        		pr.close();
        }
    	return groupIndex;
    }
    
    
    public boolean isSupervisor(boObject performerObj)  throws boRuntimeException,SQLException
    {
        if ( 
        		("SYSUSER").equals(performerObj.getAttribute("username").getValueString())
        		||
        		("SYSTEM").equals(performerObj.getAttribute("username").getValueString())
        )
        {
            return true;
        }
        if(performerObj.getBridge("groups").haveBoui(-3))
        {
            return true;    
        }
        return false;
    }
    
    public long[] getAllKeys( boObject performerObj, boolean flat )
    {
        long[] ret=new long[ p_compartments.length+(flat?p_flat_groups.length:p_groups.length)+p_packages.length+p_roles.length+p_queues.length+ 2 ];
        int x=0;
        for (int j = 0; j < p_compartments.length ; j++) 
        {
            ret[x++] = p_compartments[j];
        }
        
        // Grupos com hierarquias
        if( !flat )
        {
            for (int j = 0; j < p_groups.length ; j++) 
            {
                ret[x++] = p_groups[j];
            }
        }
        else
        {
            // sem hierarquias
            for (int j = 0; j < p_flat_groups.length ; j++) 
            {
                ret[x++] = p_flat_groups[j];
            }
        }
            
        for (int j = 0; j < p_packages.length ; j++) 
        {
            ret[x++] = p_packages[ j ];
        }
        
        for (int j = 0; j < p_roles.length ; j++) 
        {
            ret[x++] = p_roles[j];
        }
        for (int j = 0; j < p_queues.length ; j++) 
        {
            ret[x++] = p_queues[j];
        }
        ret[x++]=performerObj.getBoui();
        ret[x++]=-2;
        return ret;
    }
    
    private void buildSameGroupUsers( EboContext ctx, long performer )
    {
        PreparedStatement pstm = null;
        ResultSet rslt = null;
        try
        {
            StringBuffer sb_sql = new StringBuffer();
            sb_sql.append("select distinct parent$ from oixeouser$groups ug where ug.CHILD$ in ("); 
            if( p_groups.length > 0 )
            {
                for (int i = 0; i < p_groups.length; i++) 
                {
                    if (i > 0)  sb_sql.append(',');
                    sb_sql.append( p_groups[i] );
                }
            }
            else
            {
                sb_sql.append('0');
            }
            sb_sql.append(')');
            
            pstm = ctx.getConnectionData().prepareStatement( sb_sql.toString() );
            //pstm.setLong( 1, ctx.getBoSession().getPerformerBoui() );
            rslt = pstm.executeQuery();
            StringBuffer sb = new StringBuffer();
            Vector v = new Vector();
            while( rslt.next() )
            {
                v.add( new Long( rslt.getLong(1) ) );
            }
            
            p_same_group_users = new long[ v.size() ];
            for (int i = 0; i < p_same_group_users.length; i++) 
            {
                p_same_group_users[i] = ((Long)v.get(i)).longValue();
            }
            
        }
        catch (SQLException e)
        {
            throw new RuntimeException( e );
        }
        finally
        {
            try
            {
                rslt.close();
                pstm.close();
            }
            catch (Exception e)
            {
                
            }
        }
    }
}