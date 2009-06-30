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



public  class xwfActivityDecision extends xwfActivity implements  Serializable {  

   
   
   
    public netgest.bo.runtime.attributes.boAttributeObject no;    
    public netgest.bo.runtime.attributes.boAttributeObject yes;    
    public netgest.bo.runtime.attributes.boAttributeString question;    
    public netgest.bo.runtime.attributes.boAttributeString answer;    
    public netgest.bo.runtime.attributes.boAttributeString partName;    
    public netgest.bo.runtime.attributes.boAttributeString justification;    
    public netgest.bo.runtime.attributes.boAttributeObject pollObj;    
   
     
    public xwfActivityDecision() {
        super();
        bo_version      = "1.0";
        bo_name         = "xwfActivityDecision";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("xwfActivityDecision");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
       

       
        
       
        atts.add(no = new netgest.bo.runtime.attributes.boAttributeObject(this,"no"));
        
        atts.add(yes = new netgest.bo.runtime.attributes.boAttributeObject(this,"yes"));
        
        atts.add(question = new netgest.bo.runtime.attributes.boAttributeString(this,"question"));
        
        atts.add(answer = new netgest.bo.runtime.attributes.boAttributeString(this,"answer"));
        
        atts.add(partName = new netgest.bo.runtime.attributes.boAttributeString(this,"partName"));
        
        atts.add(justification = new netgest.bo.runtime.attributes.boAttributeString(this,"justification"));
        
        atts.add(pollObj = new netgest.bo.runtime.attributes.boAttributeObject(this,"pollObj"));
        



       
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
           
           
           
                if( no.haveDefaultValue()) 
                {
                	if(no.defaultValue() != null)
                	{
                    	no._setValue(boConvertUtils.convertToBigDecimal(no.defaultValue(), no));
                    }
                    else
                    {
                    	no._setValue(null);
                    }
                    no.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( yes.haveDefaultValue()) 
                {
                	if(yes.defaultValue() != null)
                	{
                    	yes._setValue(boConvertUtils.convertToBigDecimal(yes.defaultValue(), yes));
                    }
                    else
                    {
                    	yes._setValue(null);
                    }
                    yes.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( question.haveDefaultValue()) 
                {
                	if(question.defaultValue() != null)
                	{
                    	question._setValue(boConvertUtils.convertToString(question.defaultValue(), question));
                    }
                    else
                    {
                    	question._setValue(null);
                    }
                    question.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( answer.haveDefaultValue()) 
                {
                	if(answer.defaultValue() != null)
                	{
                    	answer._setValue(boConvertUtils.convertToString(answer.defaultValue(), answer));
                    }
                    else
                    {
                    	answer._setValue(null);
                    }
                    answer.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( partName.haveDefaultValue()) 
                {
                	if(partName.defaultValue() != null)
                	{
                    	partName._setValue(boConvertUtils.convertToString(partName.defaultValue(), partName));
                    }
                    else
                    {
                    	partName._setValue(null);
                    }
                    partName.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( justification.haveDefaultValue()) 
                {
                	if(justification.defaultValue() != null)
                	{
                    	justification._setValue(boConvertUtils.convertToString(justification.defaultValue(), justification));
                    }
                    else
                    {
                    	justification._setValue(null);
                    }
                    justification.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( pollObj.haveDefaultValue()) 
                {
                	if(pollObj.defaultValue() != null)
                	{
                    	pollObj._setValue(boConvertUtils.convertToBigDecimal(pollObj.defaultValue(), pollObj));
                    }
                    else
                    {
                    	pollObj._setValue(null);
                    }
                    pollObj.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
    
            if("xwfActivityDecision".equals(this.getName()))
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
