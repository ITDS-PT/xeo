/*Enconding=UTF-8*/
package netgest.bo.runtime;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class DefaultCloneListener implements CloneListener
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public DefaultCloneListener()
    {
    }
    
    public Object getValue(boObject clonningObject, AttributeHandler att) throws boRuntimeException
    {
        return att.getValueObject();
    }
    
    public Object getValue(boObject clonningObject, bridgeHandler bh, int line, AttributeHandler att) throws boRuntimeException
    {
        return att.getValueObject();
    }
    
    public Object getBridgeValue(boObject clonningObject, bridgeHandler bh, int line, boObject objValue) throws boRuntimeException
    {
        return objValue;
    }
}