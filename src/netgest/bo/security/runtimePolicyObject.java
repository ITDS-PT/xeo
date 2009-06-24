/*Enconding=UTF-8*/
package netgest.bo.security;

import netgest.bo.runtime.*;

import java.sql.SQLException;

import java.util.*;


public class runtimePolicyObject
{
    public static Hashtable policyObjects;
    public static final byte READ     =1;
    public static final byte WRITE    =2;
    public static final byte ADD      =3;
    public static final byte DELETE   =4;
    public static final byte EXECUTE   =5;    
    
    static
    {
        policyObjects = new Hashtable();
    }

    public long p_securityLevel=0;
    public long[] p_groups;
    public long[] p_compartments;
    public long[] p_roles;
    public long[] p_packages;

    public runtimePolicyObject(boObject obj, String className,
        String attributeName,byte action) throws boRuntimeException, SQLException
    {
        p_groups = new long[5];
        p_compartments = new long[2];
        p_packages = new long[2];
        p_roles = new long[2];

        int gIndex = 0;
        int cIndex = 0;
        int pIndex = 0;
        int rIndex = 0;
        boObject objCls = obj.getBoManager().loadObject(obj.getEboContext(),
                "select Ebo_ClsReg where name='" + className + "'");

        //               synchronized ( this)
        //              {
        if (attributeName != null)
        {
            bridgeHandler attributes =  null;
            
            //Check if its an attribute or a method
            if (action==runtimePolicyObject.EXECUTE) attributes=objCls.getBridge("methods");
            else attributes=objCls.getBridge("attributes");
            
            attributes.beforeFirst();

            boObject batr;
            boolean found = false;

            while (!found && attributes.next())
            {
                batr = attributes.getObject();

                if (batr.getAttribute("name").getValueString().equals(attributeName))
                {
                    objCls = batr;
                    found = true;
                }
            }

            if (!found)
            {
                p_groups = new long[0];
                p_compartments = new long[0];
                p_packages = new long[0];
                p_roles = new long[0];
                p_securityLevel = 0;

                return;
            }
        }

        bridgeHandler security = objCls.getBridge("security");

        security.beforeFirst();

        while (security.next())
        {
            boObject pObj = security.getObject();

            String policyRuleName = ""+pObj.getBoui();

            runtimePolicy xpolicy = (runtimePolicy) runtimePolicyObject.policyObjects.get(policyRuleName+action);

            if (xpolicy == null)
            {
                xpolicy = new runtimePolicy(pObj,action);
                runtimePolicyObject.policyObjects.put(policyRuleName+action, xpolicy);
            }

            if (p_securityLevel < xpolicy.p_securityLevel)
            {
                p_securityLevel = xpolicy.p_securityLevel;
            }

            //-------------groups
            for (int i = 0; i < xpolicy.p_groups.length; i++)
            {
                boolean found = false;
                long group = xpolicy.p_groups[i];

                for (int j = 0; !found && (j < gIndex); j++)
                {
                    if (group == p_groups[j])
                    {
                        found = true;
                    }
                }

                if (!found)
                {
                    if (gIndex > (p_groups.length - 1))
                    {
                        long[] xgroup = new long[p_groups.length + 10];
                        System.arraycopy(p_groups, 0, xgroup, 0, gIndex);
                        p_groups = xgroup;
                    }

                    p_groups[gIndex++] = group;
                }
            }

            //----------------------Compartments
            for (int i = 0; i < xpolicy.p_compartments.length; i++)
            {
                boolean found = false;
                long compartment = xpolicy.p_compartments[i];

                for (int j = 0; !found && (j < cIndex); j++)
                {
                    if (compartment == p_compartments[j])
                    {
                        found = true;
                    }
                }

                if (!found)
                {
                    if (cIndex > (p_compartments.length - 1))
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
            for (int i = 0; i < xpolicy.p_packages.length; i++)
            {
                boolean found = false;
                long packagec = xpolicy.p_packages[i];

                for (int j = 0; !found && (j < pIndex); j++)
                {
                    if (packagec == p_packages[j])
                    {
                        found = true;
                    }
                }

                if (!found)
                {
                    if (pIndex > (p_packages.length - 1))
                    {
                        long[] xpackage = new long[p_packages.length + 10];
                        System.arraycopy(p_packages, 0, xpackage, 0, pIndex);
                        p_packages = xpackage;
                    }

                    p_packages[pIndex++] = packagec;
                }
            }

            //---------------------Roles
            for (int i = 0; i < xpolicy.p_roles.length; i++)
            {
                boolean found = false;
                long role = xpolicy.p_roles[i];

                for (int j = 0; !found && (j < rIndex); j++)
                {
                    if (role == p_roles[j])
                    {
                        found = true;
                    }
                }

                if (!found)
                {
                    if (rIndex > (p_roles.length - 1))
                    {
                        long[] xrole = new long[p_roles.length + 10];
                        System.arraycopy(p_roles, 0, xrole, 0, rIndex);
                        p_roles = xrole;
                    }

                    p_roles[rIndex++] = role;
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

}
