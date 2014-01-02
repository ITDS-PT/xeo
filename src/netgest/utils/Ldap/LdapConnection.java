/*Enconding=UTF-8*/
package netgest.utils.Ldap;

import javax.naming.*;
import javax.naming.directory.*;
import java.io.*;
import java.util.Vector;
import java.util.Properties;
import java.util.Hashtable;
import netgest.bo.*;
import netgest.system.*;

public class LdapConnection implements Serializable
{
	private String p_ldap_URL;
	private InitialDirContext l_dirctx = null;
	private int p_searchscope = SearchControls.ONELEVEL_SCOPE;
	private static final int LDAPUSER=0;
	private static final int LDAPGROUP=1;
	private static final int LDAPAPP=2;
	
	
	public LdapConnection()
	{
		p_ldap_URL = boConfig.getAuthentication().getProperty("ldapServer");
	}

	public LdapConnection(String ldap_URL)
	{
		p_ldap_URL = ldap_URL;
	}

	public void LdapConnect(String l_username, String l_password) throws AuthenticationException
	{ 
		LdapConnect(l_username,l_password,true);
	}

  public void LdapExternalConnect(String userdn,String password)   throws AuthenticationException
  {
		String l_ldapurl = p_ldap_URL;
		// Intial context for OiD server
		Hashtable env = new Hashtable();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, l_ldapurl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL,userdn);
		env.put(Context.SECURITY_CREDENTIALS, password);
		// Authenticate the user by creating the intial context
		try
		{
			l_dirctx = new InitialDirContext(env);
		} 
		catch (NamingException e)
		{
			throw new AuthenticationException(e.getMessage());
		}        
  }
	public void LdapConnect(String l_username, String l_password,boolean putUserDN) throws AuthenticationException
	{
		// Distinguished name
		String l_dn = "cn=" + l_username;
		if (putUserDN) l_dn += ","+boConfig.getAuthentication().getProperty("ldapUsersDN");
		// LDAP url 
		String l_ldapurl = p_ldap_URL;
		// Intial context for OiD server
		Hashtable env = new Hashtable();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, l_ldapurl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL,l_dn);
		env.put(Context.SECURITY_CREDENTIALS, l_password);
		// Authenticate the user by creating the intial context
		try
		{
			l_dirctx = new InitialDirContext(env);
		} 
		catch (NamingException e)
		{
			throw new AuthenticationException(e.getMessage());
		}
	}

	public LdapUser getLdapUser(String username)
	{
		return (LdapUser)this.getLdapObject(null,username,this.LDAPUSER);
	}

	public LdapGroup getLdapGroup(String group)
	{
		return (LdapGroup)this.getLdapObject(null,group,this.LDAPGROUP);
	}

	public LdapUser getLdapUser(String username,String userrootdn)
	{
		return (LdapUser)this.getLdapObject(userrootdn,username,this.LDAPUSER);
	}

	public LdapGroup getLdapGroup(String group,String userrootdn)
	{
		return (LdapGroup)this.getLdapObject(userrootdn,group,this.LDAPGROUP);
	}
  
	public void setSearchScope(int searchscope)
	{
		this.p_searchscope=searchscope;
	}
  
	public int getSearchScope()
	{
		return this.p_searchscope;
	}
  
  public Object getLdapObject(String rootDN,String searchstring)
  {
		Object myobject = null;
		try
		{
			SearchControls l_searchControls;      
			SearchResult l_searchResult;
			NamingEnumeration l_results;
			
			
			l_searchControls = new SearchControls();
			l_searchControls.setSearchScope(this.p_searchscope); 
			l_results = l_dirctx.search(rootDN,searchstring,l_searchControls);
			
			if (l_results != null) 
			{ //encontrou entrada
				int l_count = 0;
				while(l_results.hasMore())
				{ 
					l_searchResult = (SearchResult)l_results.next();
          myobject = l_searchResult;
				}
			}
			return myobject;
		}
		catch (NamingException e)
		{
			return myobject;
		}    
  }
  
	public Object getLdapObject(String rootDN,String searchstring,int ObjectType)
	{
		Object myobject = null;
		try
		{
			SearchControls l_searchControls;      
			SearchResult l_searchResult;
			NamingEnumeration l_results;
			
			if((rootDN == null || rootDN.equals("")) && ObjectType == this.LDAPUSER) 
				rootDN = boConfig.getAuthentication().getProperty("ldapUsersDN");
			else if((rootDN == null || rootDN.equals("")) && ObjectType == this.LDAPGROUP) 
				rootDN = boConfig.getAuthentication().getProperty("ldapGroupsDN");
			
			l_searchControls = new SearchControls();
			l_searchControls.setSearchScope(this.p_searchscope); 
			l_results = l_dirctx.search(rootDN,"cn="+searchstring,l_searchControls);
			
			if (l_results != null) 
			{ //encontrou entrada
				int l_count = 0;
				while(l_results.hasMore())
				{ 
					l_searchResult = (SearchResult)l_results.next();
					if(ObjectType == this.LDAPUSER)
						myobject = new LdapUser(this.p_ldap_URL,l_searchResult.getAttributes());
					else if(ObjectType == this.LDAPGROUP)
						myobject = new LdapGroup(this.p_ldap_URL,l_searchResult.getAttributes());
					else myobject = l_searchResult;
				}
			}
			return myobject;
		}
		catch (NamingException e)
		{
			return myobject;
		}
	}
  
	public String getLdapURL()
	{
		return this.p_ldap_URL;
	}

	public void setLdapURL(String ldapurl)
	{
		this.p_ldap_URL = ldapurl;
	}
	
	public void close() throws NamingException
	{
		if(this.l_dirctx != null) this.l_dirctx.close();
	}
	
	public void createLdapUser(LdapUser user) throws NamingException
	{
      try{
       this.createLdapUser(user,boConfig.getAuthentication().getProperty("ldapUsersDN"));
      }
      catch(NamingException e){
       e.printStackTrace();
       throw e;
      }
	}
	public void createLdapUser(LdapUser user,String ldapUsersDN) throws NamingException
	{
		try 
		{
			l_dirctx.bind("cn=" + (String)user.getAttributeValues("cn").get(0) + "," + ldapUsersDN,user,user.getAttributes());
		}
		catch(NamingException e)
		{
			e.printStackTrace();
      throw e;
		}
	}

	public void createLdapObject(LdapUser user,String destinationDN,String destinationAttribute)
	{
		try 
		{
			l_dirctx.bind(destinationAttribute+"=" + (String)user.getAttributeValues(destinationAttribute).get(0) + "," + destinationDN,user,user.getAttributes());
		}
		catch(NamingException e)
		{
			e.printStackTrace();
		}
	}
  
	public void modifyLdapUser(LdapUser user, Attributes attrs) throws NamingException
	{
    try{
      this.modifyLdapUser(user,attrs,boConfig.getAuthentication().getProperty("ldapUsersDN"));
    }
		catch(NamingException e)
		{
			e.printStackTrace();
      throw e;
		}
	}

	public void modifyLdapUser(LdapUser user, Attributes attrs,String ldapUsersDN) throws NamingException
	{
		try 
		{
			l_dirctx.modifyAttributes("cn=" + (String)user.getAttributeValues("cn").get(0) + "," + ldapUsersDN,DirContext.REPLACE_ATTRIBUTE,attrs);
		}
		catch(NamingException e)
		{
			e.printStackTrace();
      throw e;
		}
	}

	public void modifyLdapObject(SearchResult object, Attributes attrs,String destinationDN,String destinationUniqueIdentifier)
	{
		try 
		{
			l_dirctx.modifyAttributes(destinationUniqueIdentifier+"=" + (String)object.getAttributes().get(destinationUniqueIdentifier).get() + "," + destinationDN,DirContext.REPLACE_ATTRIBUTE,attrs);
		}
		catch(NamingException e)
		{
			e.printStackTrace();
		}
	}
  
	public void removeLdapUser(String user)
	{
     this.removeLdapUser(user,boConfig.getAuthentication().getProperty("ldapUsersDN"));
	}
  
 	public void removeLdapUser(String user,String ldapUsersDN)
	{
		try 
		{
			l_dirctx.unbind("cn=" + user + "," + ldapUsersDN);
		}
		catch(NamingException e)
		{
			e.printStackTrace();
		}
	}

	public void createLdapGroup(LdapGroup group)
	{
    this.createLdapGroup(group,boConfig.getAuthentication().getProperty("ldapGroupsDN"));
	}

	public void createLdapGroup(LdapGroup group,String ldapGroupsDN)
	{
		try 
		{
			l_dirctx.bind("cn=" + (String)group.getAttributeValues("cn").get(0) + "," + ldapGroupsDN,group,group.getAttributes());
		}
		catch(NamingException e)
		{
			e.printStackTrace();
		}
	}

  
	public void modifyLdapGroup(LdapGroup group, Attributes attrs)
	{
    this.modifyLdapGroup(group,attrs,boConfig.getAuthentication().getProperty("ldapGroupsDN"));
	}
  
	public void modifyLdapGroup(LdapGroup group, Attributes attrs,String ldapGroupsDN)
	{
		try 
		{
			l_dirctx.modifyAttributes("cn=" + (String)group.getAttributeValues("cn").get(0) + "," + ldapGroupsDN,DirContext.REPLACE_ATTRIBUTE,attrs);
		}
		catch(NamingException e)
		{
			e.printStackTrace();
		}
	}
  
  
	public void removeLdapGroup(String group)
	{
    this.removeLdapGroup(group,boConfig.getAuthentication().getProperty("ldapGroupsDN"));
	}


	public void removeLdapGroup(String group,String ldapGroupsDN)
	{
		try 
		{
			l_dirctx.unbind("cn=" + group + "," + ldapGroupsDN);
		}
		catch(NamingException e)
		{
			e.printStackTrace();
		}
	}

	public void removeLdapObject(String dn)
	{
		try 
		{
			l_dirctx.unbind(dn);
		}
		catch(NamingException e)
		{
			e.printStackTrace();
		}
	}

	public void removeUserFromGroup(String username,String groupname) throws NamingException
	{
    this.removeUserFromGroup(username,groupname,boConfig.getAuthentication().getProperty("ldapUsersDN"),
                              boConfig.getAuthentication().getProperty("ldapGroupsDN"));
	}

	
	public void removeUserFromGroup(String username,String groupname,String ldapUsersDN,String ldapGroupsDN) throws NamingException
	{
		Attributes attrsToModify = new BasicAttributes(true);
		Attribute GroupUsers = new BasicAttribute("uniquemember");
		LdapGroup group = this.getLdapGroup(groupname);
		String currUserDN = "cn=" + username + "," + ldapUsersDN;
		Vector groupUsers = group.getGroupUsersDN();
		if(groupUsers != null)
			for(int i = 0;i < groupUsers.size();i++)
			{
				String actUser = (String)groupUsers.get(i);
				if(!actUser.equalsIgnoreCase(currUserDN)) GroupUsers.add(actUser);
			}
		attrsToModify.remove("objectclass");
		attrsToModify.put(GroupUsers);
		l_dirctx.modifyAttributes("cn=" + groupname + "," + ldapGroupsDN,DirContext.REPLACE_ATTRIBUTE,attrsToModify);
		group.close();
	}
	
	public void addUserToGroup(String username,String groupname) throws NamingException
	{
  
    this.addUserToGroup(username,groupname,boConfig.getAuthentication().getProperty("ldapUsersDN"),
                        boConfig.getAuthentication().getProperty("ldapGroupsDN"));
	}

	public void addUserToGroup(String username,String groupname,String ldapUsersDN,String ldapGroupsDN) throws NamingException
	{
		Attributes attrsToModify = new BasicAttributes(true);
		Attribute GroupUsers = new BasicAttribute("uniquemember");
		LdapGroup group = this.getLdapGroup(groupname);
		String currUserDN = "cn=" + username + "," + ldapUsersDN;
		Vector groupUsers = group.getGroupUsersDN();
		boolean addGroup = true;
		if(groupUsers != null)
			for(int i = 0;i < groupUsers.size();i++)
			{
				String actUser = (String)groupUsers.get(i);
				if(actUser.equalsIgnoreCase(currUserDN)) addGroup = false;
				GroupUsers.add(actUser);
				
			}
		if(addGroup == true) GroupUsers.add(currUserDN);
		attrsToModify.remove("objectclass");
		attrsToModify.put(GroupUsers);
		l_dirctx.modifyAttributes("cn=" + groupname + "," + ldapGroupsDN,DirContext.REPLACE_ATTRIBUTE,attrsToModify);
		group.close();
	}

	public void removeGroupFromGroup(String parent,String child) throws NamingException
	{
    this.removeGroupFromGroup(parent,child,boConfig.getAuthentication().getProperty("ldapGroupsDN"));
	}

	public void removeGroupFromGroup(String parent,String child,String ldapGroupsDN) throws NamingException
	{
		Attributes attrsToModify = new BasicAttributes(true);
		Attribute GroupUsers = new BasicAttribute("uniquemember");
		LdapGroup group = this.getLdapGroup(parent);
		String currGroupDN = "cn=" + child + "," + ldapGroupsDN;
		Vector groupUsers = group.getGroupUsersDN();
		if(groupUsers != null)
			for(int i = 0;i < groupUsers.size();i++)
			{
				String actUser = (String)groupUsers.get(i);
				if(!actUser.equalsIgnoreCase(currGroupDN)) GroupUsers.add(actUser);
			}
		attrsToModify.remove("objectclass");
		attrsToModify.put(GroupUsers);
		l_dirctx.modifyAttributes("cn=" + parent + "," + ldapGroupsDN,DirContext.REPLACE_ATTRIBUTE,attrsToModify);
		group.close();
	}
	
	public void addGroupToGroup(String parent,String child) throws NamingException
	{
		Attributes attrsToModify = new BasicAttributes(true);
		Attribute GroupUsers = new BasicAttribute("uniquemember");
		LdapGroup group = this.getLdapGroup(parent);
		String currGroupDN = "cn=" + child + "," + boConfig.getAuthentication().getProperty("ldapGroupsDN");
		Vector groupUsers = group.getGroupUsersDN();
		boolean addGroup = true;
		if(groupUsers != null)
			for(int i = 0;i < groupUsers.size();i++)
			{
				String actUser = (String)groupUsers.get(i);
				if(actUser.equalsIgnoreCase(currGroupDN)) addGroup = false;
				GroupUsers.add(actUser);
				
			}
		if(addGroup == true) GroupUsers.add(currGroupDN);
		attrsToModify.remove("objectclass");
		attrsToModify.put(GroupUsers);
		l_dirctx.modifyAttributes("cn=" + parent + "," + boConfig.getAuthentication().getProperty("ldapGroupsDN"),DirContext.REPLACE_ATTRIBUTE,attrsToModify);
		group.close();
	}

  public NamingEnumeration search(String rootdn,String filter,int searchScope) throws NamingException
  {            
      Vector groups=new Vector();
      SearchResult l_searchResult;
      NamingEnumeration l_results;
      SearchControls l_searchControls = new SearchControls();
      l_searchControls.setSearchScope(searchScope);    
      l_searchControls.setReturningObjFlag(true);
      l_results = l_dirctx.search(rootdn,filter,l_searchControls);    
      return l_results;
  }  
}