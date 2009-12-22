/*Enconding=UTF-8*/
package netgest.bo.system;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectContainer;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boThread;
import netgest.bo.system.boPoolOwner;

import netgest.bo.system.Logger;

public final class userThread extends boObjectContainer implements boPoolOwner
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.system.userThread");
    private static int counter = 0;

    private int p_id;
    private String p_poolUniqueId;
    private boThread p_thread;

    public userThread(EboContext boctx, int docidx)
    {
        super(boctx);
        p_id     = docidx;
        p_poolUniqueId     = "userThread[" + p_id + "]" + this.hashCode();

        counter++;
  

    }

   
    public int getDocIdx()
    {

        return p_id;
    }

    public void poolObjectActivate()
    {
    }

    public void poolObjectPassivate()
    {
    }

    public EboContext removeEboContext()
    {
        return super.removeEboContext();
    }

    public void setEboContext(EboContext boctx)
    {
        // TODO:  Override this netgest.bo.system.boPoolable method
        super.setEboContext(boctx);
     
    }

    public boObject getObject(long boui)
        throws boRuntimeException
    {
        // TODO:  Override this netgest.bo.runtime.boObjectContainer method
        String l_eboctx = super.getEboContext().setPreferredPoolObjectOwner(this.poolUniqueId());
        boObject ret    = null;
        try
        {
            ret = super.getObject(boui);
        }
        finally
        {
            super.getEboContext().setPreferredPoolObjectOwner(l_eboctx);
        }

        return ret;
    }

 

    public boThread getThread()
    {
        if (p_thread == null)
        {
            p_thread = new boThread();
        }

        return p_thread;
    }

    public String poolUniqueId()
    {
        //  Override this netgest.bo.system.boPoolable method
        return p_poolUniqueId;
    }
}
