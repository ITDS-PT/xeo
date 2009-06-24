/*Enconding=UTF-8*/
package netgest.utils.mail;

public class Attach 
{
 
  private String Location;
  private String Id;
  private boolean delete=false;
  private boolean inline;
  private String inlineID;

  public Attach()
  {
  }
  
  public Attach(String Location,String Id)
  {
    this.Location=Location;
    this.Id=Id;
  }
  
  public Attach(String Location,String Id,boolean deleteAfteruse)
  {
    this.Location=Location;
    this.Id=Id;
    delete=deleteAfteruse;
  }

  public Attach(String Location,String Id,boolean deleteAfteruse,boolean inline)
  {
    this.Location=Location;
    this.Id=Id;
    delete=deleteAfteruse;
    this.inline=inline;
  }
  public String getLocation()
  {
    return Location;
  }

  public void setLocation(String newLocation)
  {
    Location = newLocation;
  }

  public String getId()
  {
    return Id;
  }

  public void setId(String newId)
  {
    Id = newId;
  }

  public boolean getDeleteAfterUse()
  {
    return delete;
  }

  public void setDeleteAfterUse(boolean deleteAfteruse)
  {
    delete = deleteAfteruse;
  }

  
  public String getName()
  {
    int i=this.Location.lastIndexOf("/");
    int j=this.Location.lastIndexOf("\\");
    int z=i;
    if (j>i) z=j;
    return this.Location.substring(z+1,this.Location.length());
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