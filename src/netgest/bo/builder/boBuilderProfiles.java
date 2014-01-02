package netgest.bo.builder;


import netgest.bo.boConfig;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.utils.ngtXMLHandler;

public class boBuilderProfiles {

	
	public static void buildProfiles(EboContext ctx) throws boRuntimeException
	{
		 boConfig.getApplicationConfig().refresh();

         ngtXMLHandler[] profiles = boConfig.getProfiles();

         for (int i = 0; i < profiles.length; i++)
         {
        	 buildProfileFile(profiles[i], ctx);
         }
	}
	
	private static void buildProfileFile(ngtXMLHandler xml,EboContext ctx) throws boRuntimeException
	{
        ngtXMLHandler[] profiles = xml.getFirstChild().getChildNodes();

        for (int i = 0; i < profiles.length; i++)
        {
            if (profiles[i].getNodeName().equals("profile"))
            {
            	String id=profiles[i].getAttribute("id");
            	String description=profiles[i].getAttribute("description");
            	String defaultViewer=profiles[i].getAttribute("viewer");
            	
            	boObject workplace=getUIWorkPlace(ctx, id);
            	
            	boObject profile=getProfile(ctx,id);
            	profile.getAttribute("name").setValueString(id);
            	profile.getAttribute("description").setValueString(description);
            	
            	
            	
            	if (workplace.getAttribute("profile").getObject()==null )
            		workplace.getAttribute("profile").setValueLong(profile.getBoui());
            	workplace.getAttribute("defaultViewer").setValueString(defaultViewer);
            	profile.update();
            	workplace.update();
            }
        }
	}
	
	
    private static boObject getProfile(EboContext ctx, String profileName)
    throws boRuntimeException
	{
	    boObjectList listProfiles = boObjectList.list(
	            ctx, "select uiProfile where name='" + profileName + "'"
	        );
	    listProfiles.beforeFirst();
	
	    boObject profileToret = null;
	
	    if (!listProfiles.first())
	    {
	        profileToret = boObject.getBoManager().createObject(ctx, "uiProfile"); // A descrição tem de entrar	       
	    }
	    else
	    {
	        profileToret = listProfiles.getObject();
	    }
	
	    return profileToret;
	}
    
    private  static boObject getUIWorkPlace(EboContext ctx, String name)
    throws boRuntimeException
	{
	    boObjectList listWorkplaces = boObjectList.list(
	            ctx, "select uiWorkPlace where profile.name='" + name + "'"
	        );
	    listWorkplaces.beforeFirst();
	
	    boObject workplaceToret = null;
	
	    if (!listWorkplaces.first())
	    {
	    	workplaceToret = boObject.getBoManager().createObject(ctx, "uiWorkPlace"); 
	    }
	    else
	    {
	    	workplaceToret = listWorkplaces.getObject();
	    }
	
	    return workplaceToret;
	}
}
