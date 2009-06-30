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



public  class xwfUserCallProgram extends xwfActivity implements  Serializable {  

   
   
   
    public netgest.bo.runtime.attributes.boAttributeString filter;    
    public netgest.bo.runtime.attributes.boAttributeString mode;    
    public netgest.bo.runtime.attributes.boAttributeString xmlStep;    
   
     
    public xwfUserCallProgram() {
        super();
        bo_version      = "1.0";
        bo_name         = "xwfUserCallProgram";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("xwfUserCallProgram");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
       

       
        
       
        atts.add(filter = new netgest.bo.runtime.attributes.boAttributeString(this,"filter"));
        
        atts.add(mode = new netgest.bo.runtime.attributes.boAttributeString(this,"mode"));
        
        atts.add(xmlStep = new netgest.bo.runtime.attributes.boAttributeString(this,"xmlStep"));
        



       
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
           
           
           
                if( filter.haveDefaultValue()) 
                {
                	if(filter.defaultValue() != null)
                	{
                    	filter._setValue(boConvertUtils.convertToString(filter.defaultValue(), filter));
                    }
                    else
                    {
                    	filter._setValue(null);
                    }
                    filter.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( mode.haveDefaultValue()) 
                {
                	if(mode.defaultValue() != null)
                	{
                    	mode._setValue(boConvertUtils.convertToString(mode.defaultValue(), mode));
                    }
                    else
                    {
                    	mode._setValue(null);
                    }
                    mode.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( xmlStep.haveDefaultValue()) 
                {
                	if(xmlStep.defaultValue() != null)
                	{
                    	xmlStep._setValue(boConvertUtils.convertToString(xmlStep.defaultValue(), xmlStep));
                    }
                    else
                    {
                    	xmlStep._setValue(null);
                    }
                    xmlStep.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
    
            if("xwfUserCallProgram".equals(this.getName()))
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
