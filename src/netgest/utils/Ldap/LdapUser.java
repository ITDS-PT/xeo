/*Enconding=UTF-8*/
package netgest.utils.Ldap;

import java.io.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;
import netgest.system.*;
import netgest.utils.*;
import netgest.bo.boConfig;

public class LdapUser extends InitialDirContext implements Serializable
{
	private Attributes attrs = new BasicAttributes(true);
	private Attribute attrObjClass = new BasicAttribute("objectclass");
	private String p_LdapUrl;

	private void initParentEnvironment() throws NamingException
	{
		String url = p_LdapUrl;
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		init(env);    
	}
  
	public LdapUser(String LdapUrl,Attributes attrList) throws NamingException
	{
		super(true);
		p_LdapUrl = LdapUrl;
		initParentEnvironment();
		attrs = attrList;
	}

	public Attributes getAttributes() 
	{
		return attrs;
	}
  
	public Vector getAttributeValues(String name) throws NamingException
	{
		if(name.equals("")) throw new NameNotFoundException();
		Attribute res = attrs.get(name);
		Vector auxVec = new Vector();

		if(res != null)
			for(int i=0;i<res.size();i++)
				auxVec.add(res.get(i));

		return res==null? null : auxVec;
	}
  
	public Attribute getAttributeRef(String name) throws NamingException
	{
		if(name.equals("")) throw new NameNotFoundException();
		return attrs.get(name);
	}

	public Attributes getAttributes(String name, String[] ids) throws NamingException
	{
		if(name.equals("")) throw new NameNotFoundException();
		Attributes answer = new BasicAttributes(true);
		Attribute target;
		for(int i = 0; i < ids.length; i++)
		{
			target = attrs.get(ids[i]);
			if (target != null)
			{
				answer.put(target);
			}
		}
		return answer;
	}

	public Attribute addAttribute(Attribute attr)
	{
		return attrs.put(attr);
	}

	public Attribute addAttribute(String attr, Object v)
	{
		Attribute r = attrs.put(attr,v);
		return r;
	}

	public String getLdapURL()
	{
		return this.p_LdapUrl;
	}

	public void setLdapURL(String ldapurl)
	{
		this.p_LdapUrl = ldapurl;
	}

	public Vector getUserGroups() throws NamingException
	{            
		Vector groups = new Vector();
		SearchResult l_searchResult;
		NamingEnumeration l_results;
		SearchControls l_searchControls = new SearchControls();
		
		l_searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);      
		String curruserDN = "cn=" + (String)this.getAttributeRef("cn").get() + "," + boConfig.getAuthentication().getProperty("ldapUsersDN");
		String filter = "(&(objectclass=groupOfUniqueNames)(uniquemember=" + curruserDN + "))";
		l_results = this.search(boConfig.getAuthentication().getProperty("ldapGroupsDN"),filter,l_searchControls);
		
		if (l_results != null) { //encontrou entrada
			int l_count = 0;
			while (l_results.hasMore())
			{ 
				l_searchResult = (SearchResult)l_results.next();
				groups.add((String)l_searchResult.getAttributes().get("cn").get());
			}
		}
		return groups;
	}
}