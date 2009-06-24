/*Enconding=UTF-8*/
package netgest.bo.transformers;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;

/**
 * 
 * @author JMF
 * @version 
 * @see 
 */
public interface CastInterface 
{
    public void beforeCast(boObject obj) throws netgest.bo.runtime.boRuntimeException;
    public void afterCast(boObject obj) throws netgest.bo.runtime.boRuntimeException;
    public boolean isToRefresh(boObject obj) throws netgest.bo.runtime.boRuntimeException;
}