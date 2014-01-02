/*Enconding=UTF-8*/
package netgest.bo.message.utils;

public class Attach 
{

  private String name = null;
  private String location = null;
  private String id = null;
  private boolean delete=false;
  private boolean inline;
  private String inlineID = null;

  public Attach(String name)
  {
  }
  
  public Attach(String name, String location,String id)
  {
    this.name = name;
    this.location=location;
    this.id=id;
  }
  
  public Attach(String name, String location,String id,boolean deleteAfteruse)
  {
    this.name = name;
    this.location=location;
    this.id=id;
    delete=deleteAfteruse;
  }

  public Attach(String name, String location,String id,boolean deleteAfteruse,boolean inline)
  {
    this.name = name;
    this.location=location;
    this.id=id;
    delete=deleteAfteruse;
    this.inline=inline;
  }
  public String getName()
  {
    if(name == null)
    {
        return getNameFromlocation();
    }
    else
    {
        if(name.length() > 195)
        {
            name = name.substring(0, 195);
        }
    }
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getLocation()
  {
    return location;
  }

  public void setlocation(String newlocation)
  {
    location = newlocation;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String newid)
  {
    id = newid;
  }

  public boolean getDeleteAfterUse()
  {
    return delete;
  }

  public void setDeleteAfterUse(boolean deleteAfteruse)
  {
    delete = deleteAfteruse;
  }

  
  public String getNameFromlocation()
  {
    int i=this.location.lastIndexOf("/");
    int j=this.location.lastIndexOf("\\");
    int z=i;
    if (j>i) z=j;
    return this.location.substring(z+1,this.location.length());
  }

  public boolean isInline()
  {
    return inline;
  }

  public void setInline(boolean newInline)
  {
    inline = newInline;
  }

  public String getInlineID()
  {
    return inlineID;
  }

  public void setInlineID(String newInlineID)
  {
    inlineID = newInlineID;
  }
  
}