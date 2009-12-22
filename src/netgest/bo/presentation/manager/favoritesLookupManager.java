/*Enconding=UTF-8*/
package netgest.bo.presentation.manager;

import java.util.Enumeration;
import netgest.bo.boConfig;

import netgest.bo.controller.xwf.XwfController;

import netgest.bo.dochtml.docHTML;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Hashtable;


public final class favoritesLookupManager
{
    public static Hashtable p_favorites      = new Hashtable();
    public static String direct              = null;
    private static Logger logger             = Logger.getLogger(
            "netgest.bo.presentation.favoritesLookupManager"
        );
    private static Hashtable p_alreadySearch = new Hashtable();

    public static void addToFavorites(AttributeHandler attr, long favoriteBoui)
    {
        try
        {
            if (direct == null)
            {
                boConfig config = new boConfig();
                direct = config.getDeploymentDir();

                File file = new File(direct + File.separator + "favorites" + File.separator);
                file.mkdirs();
            }

            String key            = getkey(attr);
            favoriteAttribute fav = ( favoriteAttribute ) p_favorites.get(key);

            if (fav == null)
            {
                fav = readObject(key);

                if (fav == null)
                {
                    fav = new favoriteAttribute(key);
                }

                p_favorites.put(key, fav);
            }

            fav.add(favoriteBoui);

            FileOutputStream out = new FileOutputStream(
                    direct + File.separator + "favorites" + File.separator + key
                );
            ObjectOutputStream s = new ObjectOutputStream(out);
            s.writeObject(fav);
            s.flush();
            out.close();
        }
        catch (FileNotFoundException e)
        {
            logger.severe(e.getMessage());
        }
        catch (IOException e)
        {
            logger.severe(e.getMessage());
        }
    }

    private static favoriteAttribute readObject(String key)
    {
        favoriteAttribute toRet = null;

        if (p_alreadySearch.get(key) == null)
        {
            if (direct == null)
            {
                boConfig config = new boConfig();
                direct = config.getDeploymentDir();

                File file = new File(direct + File.separator + "favorites" + File.separator);
                file.mkdirs();
            }

            p_alreadySearch.put(key, direct);

            try
            {
                FileInputStream in  = new FileInputStream(
                        direct + File.separator + "favorites" + File.separator + key
                    );
                ObjectInputStream s = new ObjectInputStream(in);
                toRet               = ( favoriteAttribute ) s.readObject();

                in.close();

                return toRet;
            }
            catch (FileNotFoundException e)
            {
                return null;
            }
            catch (IOException e)
            {
                logger.severe(e.getMessage());
            }
            catch (ClassNotFoundException e)
            {
                logger.severe(e.getMessage());
            }

            //String today = (String)s.readObject();
            //Date date = (Date)s.readObject();
        }

        return null;
    }

    public static void addToFavorites(AttributeHandler attr, long[] favoriteBouis)
    {
        try
        {
            if (direct == null)
            {
                boConfig config = new boConfig();
                direct = config.getDeploymentDir();

                File file = new File(direct + File.separator + "favorites" + File.separator);
                file.mkdirs();
            }

            String key            = getkey(attr);
            favoriteAttribute fav = ( favoriteAttribute ) p_favorites.get(key);

            if (fav == null)
            {
                fav = readObject(key);

                if (fav == null)
                {
                    fav = new favoriteAttribute(key);
                    p_favorites.put(key, fav);
                }
                else
                {
                    p_favorites.put(key, fav);
                }

            }

            fav.add(favoriteBouis);

            FileOutputStream out = new FileOutputStream(
                    direct + File.separator + "favorites" + File.separator + key
                );
            ObjectOutputStream s = new ObjectOutputStream(out);
            s.writeObject(fav);
            s.flush();
            out.close();
        }
        catch (FileNotFoundException e)
        {
            logger.severe(e.getMessage());
        }
        catch (IOException e)
        {
            logger.severe(e.getMessage());
        }
    }

    public static StringBuffer getHTMLFavorites(AttributeHandler attr, StringBuffer name)
        throws boRuntimeException
    {
        String key            = getkey(attr);
        StringBuffer toRet    = new StringBuffer();
        if(p_favorites != null)
        {
            favoriteAttribute fav = ( favoriteAttribute ) p_favorites.get(key);

            if (fav == null)
            {
                fav = readObject(key);
                if( fav != null )
                {
                    p_favorites.put(key,fav);
                }
            }

            if (fav != null)
            {
                toRet = fav.getHTMLFavorites(attr, name);
            }
        }
        return toRet;
    }

    private static String getkey(AttributeHandler attr)
    {
        String objName     = attr.getParent().getName();
        String atrName     = attr.getName();
        String subkey      = "";
        long performerBoui = attr.getParent().getEboContext().getBoSession().getPerformerBoui();

        if (attr.getParent().getEboContext().getController().getName().equalsIgnoreCase("XwfController"))
        {
            try
            {
				boObject activity = (( XwfController ) attr.getParent().getEboContext().getController()).getRuntimeActivity();
				if(activity != null)
				{
					subkey = activity.getName();
				}
            }
            catch (Exception e)
            {
                //
            }
        }

        return performerBoui + "_" + subkey + objName + "_" + atrName;

        //attr.getParent().getEboContext().getController().
    }

    public static void cleanFavourites()
    {
        p_favorites.clear();
    }

    public static void cleanFavourites(long user)
    {
        if(p_favorites != null)
        {
            Enumeration oEnum = p_favorites.keys();
            String key = null;
            while (oEnum.hasMoreElements())
            {
                key = (String) oEnum.nextElement();
                if(key != null && key.startsWith(user + "_"))
                {
                    p_favorites.remove(key);
                }
            }
        }
    }
}
