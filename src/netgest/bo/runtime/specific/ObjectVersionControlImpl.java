/*Enconding=UTF-8*/
package netgest.bo.runtime.specific;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.utils.ObjectSerializationHelper;


import netgest.io.iFile;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;

public class ObjectVersionControlImpl implements ObjectVersionControl
{
    private boObject object = null;
    private boolean deserialized = false;
    
    public void setContextObject(boObject object)
    {
        this.object = object;
    }
    public boObject getContextObject()
    {
        return this.object;
    }  
    public boolean checkOut() throws boRuntimeException 
    {
        boolean result = false;
        if(createCheckOutObject())
        {
            getContextObject().setEnabled(true);
            result = true;
        }        
        return result;
    }
    public boolean undoCheckOut() throws boRuntimeException
    {
        boolean result = false;
        if(canUndoCheckOut())
        {
            boObject checkOut = getCheckOutObject();
            if(checkOut != null)
            {
                ngtXMLHandler xmlHandler = new ngtXMLHandler( checkOut.getAttribute("history").getValueString());
                ObjectSerializationHelper.deserialize(getContext(),xmlHandler.getDocument(),getContextObject());                
                checkOut.destroy();
                result = true;
            }
        }
        return result;        
    }
    public boolean checkIn() throws boRuntimeException
    {
        boolean result = false;
        updateCheckOut();
        boObject checkOut = getCheckOutObject();
        if(checkOut != null)
        {
            ngtXMLHandler xmlHandler = new ngtXMLHandler( checkOut.getAttribute("serialized").getValueString());
            ObjectSerializationHelper.deserialize(getContext(),xmlHandler.getDocument(),getContextObject());
            createHistoryObject();
            checkOut.destroy();
            getContextObject().update();  
            getContextObject().setDisabled();
        }
        return result;        
    }
    
    public boolean canCheckOut() throws boRuntimeException
    {
        boolean result = false;
        if(getCheckOutObject() == null)
        {
            result = true;
        }        
        return result;          
    }
    public boolean canCheckIn() throws boRuntimeException
    {
        boolean result = false;
        boObject checkOut = getCheckOutObject();
        if(checkOut != null)
        {
            boObject user = checkOut.getAttribute("user").getObject();
            if(user.getBoui() == getContext().getBoSession().getPerformerBoui())
            {
                result = true;
            }
        }
        return result;          
    }
    public boolean canUndoCheckOut() throws boRuntimeException
    {
        boolean result = false;
        if(canCheckIn())
        {
            result = true;
        }
        return result;          
    }   
    
    public boolean updateCheckOut() throws boRuntimeException
    {
        boolean result = false;
        boObject checkOut = getCheckOutObject();
        if(checkOut != null)
        {
            XMLDocument xmlDocument = ObjectSerializationHelper.serialize(getContextObject());
            String strXml = ngtXMLUtils.getXML( xmlDocument );        
            checkOut.getAttribute("serialized").setValueString(strXml);            
            checkOut.update();            
            result = true;            
        }                 
        return result;
    }
    public boObject getObjectFromCheckOut() throws boRuntimeException
    {
        boObject result = null;
        boObject checkOut = getCheckOutObject();
        if(checkOut != null)
        {
            ngtXMLHandler xmlHandler = new ngtXMLHandler( checkOut.getAttribute("serialized").getValueString());            
            result = ObjectSerializationHelper.deserialize(getContext(),xmlHandler.getDocument(),getContextObject());
            this.deserialized = true;
        }                 
        return result;
    } 
    public boObject getObjectFromVersion(long version) throws boRuntimeException
    {
        boObject result = null;
        boObject versionObject = getVersionObject(version);
        if(versionObject != null)
        {
            ngtXMLHandler xmlHandler = new ngtXMLHandler( versionObject.getAttribute("serialized").getValueString());            
            result = ObjectSerializationHelper.deserialize(getContext(),xmlHandler.getDocument(),getContextObject());            
        }                 
        return result;
    }     
    private boObject getCheckOutObject() throws boRuntimeException
    {
        boObject result = null;
        boObjectList list = boObjectList.list(getContext(),"SELECT Ebo_CheckOut WHERE object = " + getContextObject().getBoui(),true,false);
        list.beforeFirst();
        if(list.next())
        {
            result = list.getObject();  
        }        
        return result;
    }
    private boObject getVersionObject(long version) throws boRuntimeException
    {
        boObject result = null;
        boObjectList list = boObjectList.list(getContext(),"SELECT Ebo_History WHERE object = " + getContextObject().getBoui() + " AND version = " + version,true,false);
        list.beforeFirst();
        if(list.next())
        {
            result = list.getObject();  
        }        
        return result;
    }    
    
    private boolean createCheckOutObject() throws boRuntimeException
    {
        boolean result = false;
        boObject contextObject = getContextObject();
        boObject checkOut = boObject.getBoManager().createObject(getContext(),"Ebo_CheckOut");
        
        checkOut.getAttribute("object").setValueLong(contextObject.getBoui());
        checkOut.getAttribute("version").setValueLong(getNextVersion("Ebo_CheckOut"));
        checkOut.getAttribute("versionDate").setValueDate(new Date());
        checkOut.getAttribute("user").setValueLong(getContext().getBoSession().getPerformerBoui());
        XMLDocument xmlDocument = ObjectSerializationHelper.serialize(contextObject);
        String strXml = ngtXMLUtils.getXML( xmlDocument );        
        checkOut.getAttribute("history").setValueString(strXml);
        
        Hashtable files = beforeSerialize();
        getContextObject().update();
        xmlDocument = ObjectSerializationHelper.serialize(contextObject);
        strXml = ngtXMLUtils.getXML( xmlDocument );                
        checkOut.getAttribute("serialized").setValueString(strXml);        
        afterSerialize(files);        
        checkOut.update();
//        getContextObject().update();                
        result = true;
        return result;        
    }
    private boolean createHistoryObject() throws boRuntimeException
    {
        boolean result = false;
        boObject checkOut = getCheckOutObject();
        if(checkOut != null)
        {
            boObject history = boObject.getBoManager().createObject(getContext(),"Ebo_History");
            history.getAttribute("object").setValueLong(getContextObject().getBoui());
            history.getAttribute("version").setValueLong(getNextVersion("Ebo_History"));
            history.getAttribute("versionDate").setValueDate(new Date());
            history.getAttribute("user").setValueLong(getContext().getBoSession().getPerformerBoui());
            history.getAttribute("serialized").setValueString(checkOut.getAttribute("history").getValueString());
            history.update();
            result = true;
        }
        return result;        
    }  
    private long getNextVersion(String objectName)
    {
        long result = -1;
        PreparedStatement pstm = null;
        ResultSet rslt = null;
        try
        {            
            pstm = getContext().getConnectionData().prepareStatement("SELECT MAX(version) FROM "+objectName+" WHERE object$ = ?");
            pstm.setObject( 1, String.valueOf(getContextObject().getBoui()) );
            rslt = pstm.executeQuery();
            if(rslt.next())
            {
                result = rslt.getLong(1);
                result++;
            }
            else
            {
                result = 1;
            }
        }
        catch (SQLException e)
        {
            //ignore
        }
        finally
        {
            try
            {
                if(pstm != null) pstm.close();  
                if(rslt != null) rslt.close();
            }
            catch (Exception e)
            {
                //ignore
            }
        }
        return result;        
    }
    private EboContext getContext()
    {
        return getContextObject().getEboContext();
    }
    public boolean isDeserialized()
    {
        return this.deserialized;
    }
    private Hashtable beforeSerialize() throws boRuntimeException
    {        
        Hashtable result = null;        
        List iFilesAttributes = getContextObject().getAttributes(boDefAttribute.VALUE_IFILELINK);
        if(iFilesAttributes.size() > 0)
        {
            AttributeHandler attrHandler = null;
            iFile ifile = null;
            iFile ifileCopy = null;
            result = new Hashtable(iFilesAttributes.size());
            for (int i = 0; i < iFilesAttributes.size() ; i++) 
            {
                attrHandler = (AttributeHandler)iFilesAttributes.get(i);
                ifile = attrHandler.getValueiFile();
                if(ifile != null && ifile.exists())
                {
                    result.put(attrHandler.getName(),ifile);
                    ifileCopy = ifile.getCopy();
                    attrHandler.setValueiFile(ifileCopy);
                }
            }

        }
        return result;
    }
    private void afterSerialize(Hashtable files)  throws boRuntimeException
    {
        boObject object = getContextObject();
        String key = null;
        Enumeration keys = files.keys();
        while(keys.hasMoreElements())
        {
            key = (String)keys.nextElement();
            object.getAttribute(key).setValueiFile((iFile)files.get(key));
        }        
    }
    
    public boolean delete() throws boRuntimeException 
    {
        boolean result = false;
        boObject object = null;
        boObjectList list = boObjectList.list(getContext(),"SELECT Ebo_History WHERE object = " + getContextObject().getBoui(),true,false);
        list.beforeFirst();
        while(list.next())
        {
            object = list.getObject();
            object.destroy();
        }
        
        list = boObjectList.list(getContext(),"SELECT Ebo_CheckOut WHERE object = " + getContextObject().getBoui(),true,false);
        list.beforeFirst();
        while(list.next())
        {
            object = list.getObject();
            object.destroy();
        }
        
        result = true;
        return result;
    }
}