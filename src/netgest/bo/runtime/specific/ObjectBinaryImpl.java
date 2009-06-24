/*Enconding=UTF-8*/
package netgest.bo.runtime.specific;

import java.util.ArrayList;
import java.util.List;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.*;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.io.iFile;
import netgest.utils.ClassUtils;
/**
 * <p>Title: ObjectBinaryImpl </p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @Company: Enlace3 </p>
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */
public class ObjectBinaryImpl implements ObjectBinary 
{
    public static String BINARY_ATTRIBUTE_KEY = "binaryDocuments";
    private static String SRCOBJ_ATTRIBUTE_KEY = "srcObj";
    
    private boObject object = null;
    
    public void setContextObject(boObject object)
    {
        this.object = object;
    }
    public boObject getContextObject()
    {
        return this.object;
    }    
    public List getBinary() throws boRuntimeException
    {
        List result = null;
        bridgeHandler bridge = object.getBridge(BINARY_ATTRIBUTE_KEY);
        if(bridge != null && bridge.getRecordCount() > 0)
        {            
            result = new ArrayList(bridge.getRowCount());
            bridge.beforeFirst();
            while(bridge.next())
            {
                result.add(bridge.getObject());
            }                        
        }   
        return result;
    }
    public boObject getBinary(long boui) throws boRuntimeException
    {
        boObject result = null;
        boObject binary = null;
        boolean found = false;
        AttributeHandler attr = null;
        bridgeHandler bridge = object.getBridge(BINARY_ATTRIBUTE_KEY);
        bridge.beforeFirst();
        while(bridge.next() && !found)
        {
            binary = bridge.getObject();
            if(binary.getBoui() == boui)
            {
                result = binary;
                found = true;
            }
            else
            {                
                attr = binary.getAttribute(SRCOBJ_ATTRIBUTE_KEY);
                if(attr != null)
                {
                    binary = bridge.getObject();
                    if(binary != null && binary.getBoui() == boui)
                    {
                        result = binary;
                        found = true;
                    }                                        
                }                                
            }
        }                           
        return result;
    }    
    public void setBinary(List binarys) throws boRuntimeException
    {                
        for (int i = 0; i < binarys.size(); i++) 
        {
            setBinary(binarys.get(i));            
        }        
    }
    public void setBinary(Object binary) throws boRuntimeException
    {        
        bridgeHandler bridge = object.getBridge(BINARY_ATTRIBUTE_KEY);
        long binaryBoui = -1;            
        if(binary instanceof boObject)
        {
            binaryBoui = ((boObject)binary).getBoui();
        }
        else if(binary instanceof iFile)
        {
            boObject document = boObject.getBoManager().createObject(this.object.getEboContext(),"Ebo_Document");            
            document.getAttribute("file").setValueiFile((iFile)binary);
            binaryBoui = document.getBoui();
        }
        else 
        {
            binaryBoui = ClassUtils.convertToLong(binary.toString(),-1);
        }
        if(binaryBoui > 0 && !bridge.haveBoui(binaryBoui))
        {
            bridge.add(binaryBoui);
        }        
    }
    public void remove(Object binary) throws boRuntimeException
    {
        bridgeHandler bridge = this.object.getBridge(BINARY_ATTRIBUTE_KEY);        
        long binaryBoui = -1;            
        if(binary instanceof boObject)
        {
            binaryBoui = ((boObject)binary).getBoui();
        }
        else 
        {
            binaryBoui = ClassUtils.convertToLong(binary.toString(),-1);
        }
        if(binaryBoui != -1 && bridge.haveBoui(binaryBoui))
        {            
            boolean found = false;
            boObject aux = null;
            bridge.beforeFirst();
            while(bridge.next() && !found)
            {
                aux = bridge.getObject();
                if(aux.getBoui() == binaryBoui)
                {
                    bridge.remove();
                    bridge.previous();
                    found = true;
                }
            }
        }                
    }
    public Object getFisical(long boui) throws boRuntimeException
    {
        Object result = null;
        boolean found = false;
        boObject binary = getBinary(boui);
        AttributeHandler attr = null;
        attr = binary.getAttribute("file");
        if(attr != null)
        {
            result = attr.getValueiFile();                
            found = true;
        }                    
        return result;
    }    
}