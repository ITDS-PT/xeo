/*Enconding=UTF-8*/
package netgest.bo.security;
import java.util.*;
import netgest.bo.runtime.*;
import java.sql.*;
import java.sql.SQLException;
import netgest.bo.utils.PackageUtils;

public class runtimePolicyRule 
{   
    public long p_securityLevel=0;
    public long[] p_groups;
    public long[] p_compartments;
    public long[] p_packages;
    public long[] p_roles;
    private byte p_typePolicy;
    
    public static final byte READ     =1;
    public static final byte WRITE    =2;
    public static final byte ADD      =3;
    public static final byte DELETE   =4;
    public static final byte EXECUTE   =5;    
    
    public runtimePolicyRule( boObject policyObjRule ) throws boRuntimeException,SQLException
    {
        
        bridgeHandler roles=policyObjRule.getBridge("roles");
        int numberRoles=(int)roles.getRecordCount();
        
        p_roles=new long[ numberRoles ];
        roles.beforeFirst();
        int i=0;
        while ( roles.next() )
        {
            p_roles[ i++ ] = roles.getObject().getBoui();
        }
        
        p_securityLevel=policyObjRule.getAttribute("securityLevel").getValueLong();

        String typePolicy=policyObjRule.getAttribute("typePolicy").getValueString();        
        
        if (typePolicy.equalsIgnoreCase("read")) this.p_typePolicy=this.READ;
        else if (typePolicy.equalsIgnoreCase("write")) this.p_typePolicy=this.WRITE;
        else if (typePolicy.equalsIgnoreCase("add")) this.p_typePolicy=this.ADD;
        else if (typePolicy.equalsIgnoreCase("delete")) this.p_typePolicy=this.DELETE;
        else if (typePolicy.equalsIgnoreCase("execute")) this.p_typePolicy=this.EXECUTE;
        
        
        bridgeHandler compartments=policyObjRule.getBridge("compartments");
        int numbercompartments=(int)compartments.getRecordCount();
        
        p_compartments=new long[ numbercompartments ];
        compartments.beforeFirst();
        i=0;
        while ( compartments.next() )
        {
            p_compartments[ i++ ] = compartments.getObject().getBoui();
        }
        
        //packages        
        p_packages = PackageUtils.getPackagesBouiFromBridgePackages(policyObjRule) ;
        
        
        bridgeHandler groups=policyObjRule.getBridge("groups");
        int numberGroups=(int)groups.getRecordCount();
        
        p_groups=new long[ numberGroups ];
        groups.beforeFirst();
        i=0;
        while ( groups.next() )
        {
            p_groups[ i++ ] = groups.getObject().getBoui();
        }        
    }
    
  public long getSecurityLevel()   
  {
    return this.p_securityLevel;
  }

  public byte getTypePolicy()
  {
    return p_typePolicy;
  }

  public void setTypePolicy(byte typePolicy)
  {
    this.p_typePolicy = typePolicy;
  }
    
}