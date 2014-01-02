/*Enconding=UTF-8*/
package netgest.bo.system;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.boPoolable;

import netgest.utils.ClassUtils;

import netgest.bo.system.Logger;

import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;


/**
 * Classe responsável por controlar os objectos userThread da sessão
 * @author JMF
 * @version
 * @see
 */
public final class boSessionControler extends boPoolable
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.system.boSessionControler");
    private static int p_userThreadCounter = 0;
    private Hashtable p_utl;
    private EboContext p_boctx;

    public boSessionControler()
    {
        super(null);
        p_utl = new Hashtable();
        
    }

    public String[] processRequest(EboContext boctx)
        throws java.io.IOException, ServletException, boRuntimeException, Exception
    {
        boctx.setPreferredPoolObjectOwner(this.poolUniqueId());

        // HttpServletRequest request=boctx.getRequest();
        // HttpServletResponse response=boctx.getResponse();
        //  PageContext pageContext=boctx.getPageContext();
        userThread ust    = null;
        String[] toReturn = new String[2];
        try
        {
            ust = poolUserThreadManager(boctx);
            p_utl.put(new Integer(ust.getDocIdx()), ust);
        }
        catch (Exception e)
        {
            throw (e);
        }
        finally
        {
        }

        return toReturn;
    }

    public userThread getUserThread(int idx)
    {
        return ( userThread ) p_utl.get(new Integer(idx));
    }

    public Hashtable getUserThreadList()
    {
        return p_utl;
    }

    public void releseObjects(EboContext boctx)
    {
        if (p_utl != null)
        {
            p_utl.clear();
        }

        boctx.getApplication().getMemoryArchive().getPoolManager().realeaseObjects(
            this.poolUniqueId(), boctx
        );
    }

    public void poolObjectPassivate()
    {
        // TODO:  Implement this netgest.bo.system.boArchival abstract method
    }

    public void poolObjectActivate()
    {
        // TODO:  Implement this netgest.bo.system.boArchival abstract method
    }

    protected userThread poolUserThreadManager(EboContext boctx)
    {
        HttpServletRequest request = boctx.getRequest();
        userThread uti             = null;
        int UTI                    = ClassUtils.convertToInt(request.getParameter("UTI"), -1);
        if (UTI != -1)
        {
            uti = getUserThreadByIDX(UTI, boctx);
        }
        else
        {
            p_userThreadCounter++;
            uti = new userThread(boctx, p_userThreadCounter);

            boctx.getApplication().getMemoryArchive().getPoolManager().putObject(
                uti, new Object[] { "USERTHREAD:IDX:" + p_userThreadCounter }
            );
            uti.poolSetStateFull(this.poolUniqueId());
        }

        return uti;
    }

    public userThread getUserThreadByIDX(int IDX, EboContext boctx)
    {
        return ( userThread ) boctx.getApplication().getMemoryArchive().getPoolManager().getObject(
            boctx, this.poolUniqueId(), "USERTHREAD:IDX:" + IDX
        );
    }
}
