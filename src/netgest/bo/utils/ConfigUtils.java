package netgest.bo.utils;
import java.io.File;
import netgest.bo.boConfig;
import netgest.bo.presentation.manager.favoritesLookupManager;
import netgest.bo.presentation.render.elements.ExplorerServer;

public class ConfigUtils 
{
    private static final String FAVOURITES_DIR = "favorites";
    public ConfigUtils()
    {
    }
    
    public static final void setDefaultExplorer()
    {
        ExplorerServer.setDefaultExplorer();
    }
    
    public static final void setDefaultExplorer(long user)
    {
        ExplorerServer.setDefaultExplorer(user);
    }
    
    public static final void setDefaultExplorer(String keyTree)
    {
        ExplorerServer.setDefaultExplorer(keyTree);
    }
    
    public static final void setDefaultExplorer(String keyTree,long user)
    {
        ExplorerServer.setDefaultExplorer(keyTree, user);
    }
    
    public static final void cleanFavorites(long user)
    {
        //primeiro vou apagar todos os ficheiros nas directorias de definições favoritos
        boConfig config=new boConfig();
        String xDir=config.getDeploymentDir();
        File favouritesDir = new File(xDir+File.separator+FAVOURITES_DIR);
        if(favouritesDir.exists() && favouritesDir.isDirectory())
        {
            File all[] = favouritesDir.listFiles();
            String auxName;
            for (int i = 0; i < all.length; i++) 
            {
                if(all[i].isFile() && all[i].canWrite())
                {
                    auxName = all[i].getName();
                    if(auxName != null && auxName.startsWith(user + "_"))
                    {
                        all[i].delete();
                    }
                }
            }
        }
        //vou limpar a cache de favoritos
        favoritesLookupManager.cleanFavourites(user);
    }
    
    public static final void cleanFavorites()
    {
        //primeiro vou apagar todos os ficheiros nas directorias de definições favoritos
        boConfig config=new boConfig();
        String xDir=config.getDeploymentDir();
        File favouritesDir = new File(xDir+File.separator+FAVOURITES_DIR);
        if(favouritesDir.exists() && favouritesDir.isDirectory())
        {
            File all[] = favouritesDir.listFiles();
            for (int i = 0; i < all.length; i++) 
            {
                if(all[i].isFile() && all[i].canWrite())
                {
                    all[i].delete();
                }
            }
        }
        //vou limpar a cache de favoritos
        favoritesLookupManager.cleanFavourites();
    }
}