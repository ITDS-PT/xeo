/*Enconding=UTF-8*/
package netgest.bo.security;

import netgest.bo.runtime.*;

import java.sql.*;
import java.sql.SQLException;

import java.util.*;


public class runtimePolicy
{
    public static Hashtable objectPolicyRules;
    public static final byte READ     =1;
    public static final byte WRITE    =2;
    public static final byte ADD      =3;
    public static final byte DELETE   =4;
    public static final byte EXECUTE   =5;    
    
    static
    {
        objectPolicyRules = new Hashtable();
    }

    public long p_securityLevel = 0;
    public long[] p_groups;
    public long[] p_compartments;
    public long[] p_packages;
    public long[] p_roles;
    
/*    public runtimePolicy(boObject policyObj) throws boRuntimeException, SQLException
    {
      this(policyObj,runtimePolicy.READ);
    } */

    public runtimePolicy(boObject policyObj,byte action)
        throws boRuntimeException, SQLException
    {
        p_groups = new long[10];
        p_compartments = new long[10];
        p_packages = new long[10];
        p_roles = new long[10];

        int gIndex = 0;
        int cIndex = 0;
        int pIndex = 0;
        int rIndex = 0;

        bridgeHandler rules = policyObj.getBridge("rule");
        rules.beforeFirst();

        while (rules.next())
        {
            boObject boRule = rules.getObject();
            String ruleName = ""+boRule.getBoui();
            if (this.checkRule(boRule.getAttribute("typePolicy").getValueString())==action)
            {
              runtimePolicyRule ruleObj = (runtimePolicyRule) runtimePolicy.objectPolicyRules.get(ruleName+action);
  
              if (ruleObj == null)
              {
                  ruleObj = new runtimePolicyRule(boRule);
                  runtimePolicy.objectPolicyRules.put(ruleName+action, ruleObj);
              }
  
              if (p_securityLevel < ruleObj.p_securityLevel)
              {
                  p_securityLevel = ruleObj.p_securityLevel;
              }
  
              //-------------groups
              for (int i = 0; i < ruleObj.p_groups.length; i++)
              {
                  boolean found = false;
                  long group = ruleObj.p_groups[i];
  
                  for (int j = 0; !found && (j < gIndex); j++)
                  {
                      if (group == p_groups[j])
                      {
                          found = true;
                      }
                  }
  
                  if (!found)
                  {
                      if (gIndex > p_groups.length)
                      {
                          long[] xgroup = new long[p_groups.length + 10];
                          System.arraycopy(p_groups, 0, xgroup, 0, gIndex);
                          p_groups = xgroup;
                      }
  
                      p_groups[gIndex++] = group;
                  }
              }
  
              //----------------------Compartments
              for (int i = 0; i < ruleObj.p_compartments.length; i++)
              {
                  boolean found = false;
                  long compartment = ruleObj.p_compartments[i];
  
                  for (int j = 0; !found && (j < cIndex); j++)
                  {
                      if (compartment == p_compartments[j])
                      {
                          found = true;
                      }
                  }
  
                  if (!found)
                  {
                      if (cIndex > p_compartments.length)
                      {
                          long[] xcompartment = new long[p_compartments.length +
                              10];
                          System.arraycopy(p_compartments, 0, xcompartment, 0,
                              cIndex);
                          p_compartments = xcompartment;
                      }
  
                      p_compartments[cIndex++] = compartment;
                  }
              }
  
              //---------------------Packages
              for (int i = 0; i < ruleObj.p_packages.length; i++)
              {
                  boolean found = false;
                  long packagec = ruleObj.p_packages[i];
  
                  for (int j = 0; !found && (j < pIndex); j++)
                  {
                      if (packagec == p_packages[j])
                      {
                          found = true;
                      }
                  }
  
                  if (!found)
                  {
                      if (pIndex > p_packages.length)
                      {
                          long[] xpackage = new long[p_packages.length + 10];
                          System.arraycopy(p_packages, 0, xpackage, 0, pIndex);
                          p_packages = xpackage;
                      }
  
                      p_packages[pIndex++] = packagec;
                  }
              }
  
              //---------------------Roles
              for (int i = 0; i < ruleObj.p_roles.length; i++)
              {
                  boolean found = false;
                  long role = ruleObj.p_roles[i];
  
                  for (int j = 0; !found && (j < rIndex); j++)
                  {
                      if (role == p_roles[j])
                      {
                          found = true;
                      }
                  }
  
                  if (!found)
                  {
                      if (rIndex > p_roles.length)
                      {
                          long[] xrole = new long[p_roles.length + 10];
                          System.arraycopy(p_roles, 0, xrole, 0, pIndex);
                          p_roles = xrole;
                      }
  
                      p_roles[rIndex++] = role;
                  }
              }
            }
        }

        long[] xgroup = new long[gIndex];
        System.arraycopy(p_groups, 0, xgroup, 0, gIndex);
        p_groups = xgroup;

        long[] xcompartment = new long[cIndex];
        System.arraycopy(p_compartments, 0, xcompartment, 0, cIndex);
        p_compartments = xcompartment;

        long[] xrole = new long[rIndex];
        System.arraycopy(p_roles, 0, xrole, 0, rIndex);
        p_roles = xrole;

        long[] xpackage = new long[pIndex];
        System.arraycopy(p_packages, 0, xpackage, 0, pIndex);
        p_packages = xpackage;
    }
    
    private byte checkRule(String rule)
    {
        byte toRet=this.READ;
        if (rule.equalsIgnoreCase("read")) toRet=this.READ;
        else if (rule.equalsIgnoreCase("write")) toRet=this.WRITE;
        else if (rule.equalsIgnoreCase("add"))  toRet=this.ADD;
        else if (rule.equalsIgnoreCase("delete")) toRet=this.DELETE;
        else if (rule.equalsIgnoreCase("execute")) toRet=this.EXECUTE;        
        return toRet;
    }
}
