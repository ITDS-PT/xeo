/*Enconding=UTF-8*/
package netgest.bo.runtime.specific;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
/**
 * <p>Title: ObjectResImpl </p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @Company: Enlace3 </p>
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */
public class ObjectResImpl implements ObjectRes
{

    public static String RES_ATTRIBUTE_KEY = "resId";
    private boObject object = null;
    
    public void setContextObject(boObject object)
    {
        this.object = object;
    }
    public boObject getContextObject()
    {
        return this.object;
    }     
    public void generateCode()  throws boRuntimeException
    {
        // generate sequence
    }
    public Object getCode() throws boRuntimeException
    {
        Object result = null;
        AttributeHandler attr = getContextObject().getAttribute(RES_ATTRIBUTE_KEY);
        if(attr != null)
        {
            result = attr.getValueObject();
        }
        return result;    
    }
    public boolean setCode(Object code) throws boRuntimeException
    {
        boolean result = false;        
        AttributeHandler attr = getContextObject().getAttribute(RES_ATTRIBUTE_KEY);
        if(attr != null)
        {
            if(code instanceof boObject)
            {
                attr.setObject((boObject)code);   
            }               
            else
            {
                attr.setValueObject(code);
            }                                
            result = true;
        }                
        return result;
    }
    public boolean isValid() throws boRuntimeException
    {
        return true;
    }
    public boolean isDisabled()  throws boRuntimeException
    {
        return false;
    }    
}