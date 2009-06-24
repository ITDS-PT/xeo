/*Enconding=UTF-8*/
package netgest.bo.utils;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.AttributeHandler;
/**
 * <p>Title: DifferenceElement </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class DifferenceElement 
{
    private String boObjectName;    
    private String attributeName;
    private Object srcValue;
    private Object dstValue;
    private String bridgeName;    
    private long boui;
    private boolean relation = false;
    private boolean multiValue = false;
    
    
    public DifferenceElement(String boObjectName,String attributeName, boObject srcObject, boObject dstObject,String bridgeName,long boui,boolean relation) throws boRuntimeException
    {
        this.boObjectName = boObjectName;
        this.attributeName = attributeName;
        this.srcValue = srcObject.getAttribute(attributeName).getValueObject();
        this.dstValue = dstObject.getAttribute(attributeName).getValueObject();
        this.bridgeName = bridgeName;
        this.boui = boui;
        this.relation = relation;                
    }
    public DifferenceElement(String boObjectName, String attributeName, AttributeHandler srcAttHandler, AttributeHandler dstAttHandler,String bridgeName,long boui,boolean relation) throws boRuntimeException
    {
        this.boObjectName = boObjectName;
        this.attributeName = attributeName;
        this.srcValue = srcAttHandler.getValueObject();
        this.dstValue = dstAttHandler.getValueObject();   
        this.bridgeName = bridgeName;
        this.boui = boui;
        this.relation = relation;
    }     
    public DifferenceElement(String boObjectName, String attributeName, boObject srcObject, boObject dstObject,String bridgeName) throws boRuntimeException
    {
        this.boObjectName = boObjectName;
        this.attributeName = attributeName;
        this.srcValue = srcObject.getAttribute(attributeName).getValueObject();
        this.dstValue = dstObject.getAttribute(attributeName).getValueObject();
        this.bridgeName = bridgeName;
    }
    public DifferenceElement(String boObjectName, String attributeName, boObject srcObject, boObject dstObject,boolean multiValue) throws boRuntimeException
    {
        this.boObjectName = boObjectName;
        this.attributeName = attributeName;
        if(srcObject != null)
        {
            this.srcValue = srcObject;
        }
        if(dstObject != null)
        {
            this.dstValue = dstObject;
        }
        this.multiValue = multiValue;
    }    
    public DifferenceElement(String boObjectName, long boui,String bridgeName) throws boRuntimeException
    {
        this.boObjectName = boObjectName;  
        this.boui = boui;
        this.bridgeName = bridgeName;
    }      

    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
    }


    public String getAttributeName()
    {
        return attributeName;
    }


    public void getSrcValue(Object srcValue)
    {
        this.srcValue = srcValue;
    }


    public Object getSrcValue()
    {
        return srcValue;
    }


    public void getDstValue(Object dstValue)
    {
        this.dstValue = dstValue;
    }


    public Object getDstValue()
    {
        return dstValue;
    }


    public void setBridgeName(String bridgeName)
    {
        this.bridgeName = bridgeName;
    }


    public String getBridgeName()
    {
        return bridgeName;
    }


    public void setSrcValue(Object srcValue)
    {
        this.srcValue = srcValue;
    }


    public void setDstValue(Object dstValue)
    {
        this.dstValue = dstValue;
    }


    public void setBoui(long boui)
    {
        this.boui = boui;
    }


    public long getBoui()
    {
        return boui;
    }


    public void setRelation(boolean relation)
    {
        this.relation = relation;
    }


    public boolean isRelation()
    {
        return relation;
    }


    public void setMultiValue(boolean multiValue)
    {
        this.multiValue = multiValue;
    }


    public boolean isMultiValue()
    {
        return multiValue;
    }


    public void setBoObjectName(String boObjectName)
    {
        this.boObjectName = boObjectName;
    }


    public String getBoObjectName()
    {
        return boObjectName;
    }
}