/*Enconding=UTF-8*/
package netgest.utils.Ldap;

import java.io.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;
import netgest.system.*;

public class LdapGroup extends InitialDirContext implements Serializable
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
  
	public LdapGroup(String LdapUrl,Attributes attrList) throws NamingException
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

	public Vector getGroupUsersDN() throws NamingException
	{
		Vector users = this.getAttributeValues("uniquemember");
		return users;
	}

	public Vector getGroupUsers() throws NamingException
	{
		Vector users = this.getAttributeValues("uniquemember");
		if(users != null)
		for(int i=0;i<users.size();i++)
		{
			String curruser = (String)users.get(i);
			users.set(i,curruser.substring(3,curruser.indexOf(",")));
		}
		return users;
	}

	public Vector getGroupAppsDN() throws NamingException
	{
		Vector apps = this.getAttributeValues("ngtGroupsApp");
		return apps;
	}

	public Vector getGroupApps() throws NamingException
	{	
		Vector apps = this.getAttributeValues("ngtGroupsApp");
		if(apps!=null)
		for (int i=0;i<apps.size();i++)
		{
			String currapp = (String)apps.get(i);
			apps.set(i,currapp.substring(3,currapp.indexOf(",")));
		}
		return apps;
	}
}