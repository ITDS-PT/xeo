/*Enconding=UTF-8*/
package netgest.bo.system;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boSessionUser 
{

    String userName;
    String name;
    String srName;
    String email;
    String notify;
    int securityLevel;
    long[] groups;
    long[] queues;
    long[] roles;
    long[] applications;
    long boui;
    long[] mailboxes;
    
    boolean isAdministrator = false;

    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public boSessionUser()
    {
    }

    public String getUserName()
    {
        return userName;
    }


    public String getName()
    {
        return name;
    }


    public String getSrName()
    {
        return srName;
    }


    public String getEmail()
    {
        return email;
    }


    public String getNotify()
    {
        return notify;
    }


    public int getSecurityLevel()
    {
        return securityLevel;
    }


    public long[] getGroups()
    {
        return groups;
    }


    public long[] getQueues()
    {
        return queues;
    }


    public long[] getRoles()
    {
        return roles;
    }


    public long[] getApplications()
    {
        return applications;
    }


    public long getBoui()
    {
        return boui;
    }


    public long[] getMailboxes()
    {
        return mailboxes;
    }

    public boolean isAdministrator()
    {
        return isAdministrator;
    }






}