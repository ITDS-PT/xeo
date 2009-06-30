package v1_0;

import netgest.bo.runtime.*;
import netgest.bo.data.*;
import netgest.bo.def.*;
import netgest.bo.security.*;
import netgest.exceptions.*;
import netgest.utils.*;
import netgest.io.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import java.io.*;
import java.math.*;



public  class xwfWaitResponse extends xwfActivity implements  Serializable {  

   
   
   
    public netgest.bo.runtime.attributes.boAttributeObject sendActivity;    
    public netgest.bo.runtime.attributes.boAttributeObject waitFrom;    
    public netgest.bo.runtime.attributes.boAttributeObject receiveActivity;    
    public netgest.bo.runtime.attributes.boAttributeObject saveOn;    
    public netgest.bo.runtime.attributes.boAttributeDate beginDate;    
   
     
    public xwfWaitResponse() {
        super();
        bo_version      = "1.0";
        bo_name         = "xwfWaitResponse";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("xwfWaitResponse");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
       

       
        
       
        atts.add(sendActivity = new netgest.bo.runtime.attributes.boAttributeObject(this,"sendActivity"));
        
        atts.add(waitFrom = new netgest.bo.runtime.attributes.boAttributeObject(this,"waitFrom"));
        
        atts.add(receiveActivity = new netgest.bo.runtime.attributes.boAttributeObject(this,"receiveActivity"));
        
        atts.add(saveOn = new netgest.bo.runtime.attributes.boAttributeObject(this,"saveOn"));
        
        atts.add(beginDate = new netgest.bo.runtime.attributes.boAttributeDate(this,"beginDate"));
        



       
    }
		private BigDecimal verifyTransformer(ObjAttHandler att, BigDecimal boui)
    {
        try
        {
            netgest.bo.transformers.Transformer transfClass = null;
            if((transfClass = att.getDefAttribute().getTransformClassMap()) != null)
            {
                if(boui != null && att.getParent().getMode() != boObject.MODE_EDIT_TEMPLATE )
                {
                    boui = new BigDecimal(transfClass.transform(getEboContext(), att.getParent(), boui.longValue()));
                }
            }
        }
        catch (boRuntimeException e)
        {
            
        }
        return boui;
    }
    public void init() throws boRuntimeException {
        super.init();
        setModeView();
        if(p_mode == MODE_NEW && !p_clone)
        {
           
           
           
                if( sendActivity.haveDefaultValue()) 
                {
                	if(sendActivity.defaultValue() != null)
                	{
                    	sendActivity._setValue(boConvertUtils.convertToBigDecimal(sendActivity.defaultValue(), sendActivity));
                    }
                    else
                    {
                    	sendActivity._setValue(null);
                    }
                    sendActivity.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( waitFrom.haveDefaultValue()) 
                {
                	if(waitFrom.defaultValue() != null)
                	{
                    	waitFrom._setValue(boConvertUtils.convertToBigDecimal(waitFrom.defaultValue(), waitFrom));
                    }
                    else
                    {
                    	waitFrom._setValue(null);
                    }
                    waitFrom.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( receiveActivity.haveDefaultValue()) 
                {
                	if(receiveActivity.defaultValue() != null)
                	{
                    	receiveActivity._setValue(boConvertUtils.convertToBigDecimal(receiveActivity.defaultValue(), receiveActivity));
                    }
                    else
                    {
                    	receiveActivity._setValue(null);
                    }
                    receiveActivity.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( saveOn.haveDefaultValue()) 
                {
                	if(saveOn.defaultValue() != null)
                	{
                    	saveOn._setValue(boConvertUtils.convertToBigDecimal(saveOn.defaultValue(), saveOn));
                    }
                    else
                    {
                    	saveOn._setValue(null);
                    }
                    saveOn.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( beginDate.haveDefaultValue()) 
                {
                	if(beginDate.defaultValue() != null)
                	{
                    	beginDate._setValue(boConvertUtils.convertToTimestamp(beginDate.defaultValue(), beginDate));
                    }
                    else
                    {
                    	beginDate._setValue(null);
                    }
                    beginDate.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
    
            if("xwfWaitResponse".equals(this.getName()))
            {
                calculateFormula(null);
            }
        }
    }
   
    
    public bridgeHandler getBridge( String bridgeName ) 
    {
        boBridgesArray    brig = super.getBridges();        	
        bridgeHandler     ret  = brig.get(bridgeName);
        if( ret == null )
        {
        
        	if( ret == null )
        	{
				ret = super.getBridge( bridgeName );
        	}
        }
        return ret;
    }
   
    
   
    

   

   
        
    
	public void load(long xboui) throws boRuntimeException {
	super.load(xboui);
	}
	public void load(java.lang.String xboql) throws boRuntimeException {
	super.load(xboql);
	}
	public void create(long xboui) throws boRuntimeException {
	super.create(xboui);
	}
	public void create(long xboui,netgest.bo.data.DataSet xdata) throws boRuntimeException {
	super.create(xboui,xdata);
	}
	public void update() throws boRuntimeException {
	super.update();
	}
	public void edit() throws boRuntimeException {
	super.edit();
	}
	public void revertToSaved() throws boRuntimeException {
	super.revertToSaved();
	}
	public void saveAsTemplate() throws boRuntimeException {
	super.saveAsTemplate();
	}
public  String[] getDependences(String attributeName) {
return null;
}
public  String[] addDefaultDependencesFields() {
return null;
}
public  void calculateFormula(Hashtable table, String from) throws boRuntimeException {
if ((p_mode != MODE_EDIT_TEMPLATE)) 
{
callObjBridgeCalculate(from);
if ((from != null)) 
{
}
}
}
	
	
    
    
}
