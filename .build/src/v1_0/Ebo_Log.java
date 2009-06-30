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



public  class Ebo_Log extends boObject implements  Serializable {  

   
   
   
    public netgest.bo.runtime.attributes.boAttributeString attribute;    
    public netgest.bo.runtime.attributes.boAttributeString name;    
    public netgest.bo.runtime.attributes.boAttributeString value_String;    
    public netgest.bo.runtime.attributes.boAttributeNumber value_Long;    
    public netgest.bo.runtime.attributes.boAttributeDate value_Date;    
    public netgest.bo.runtime.attributes.boAttributeString value_CLOB;    
    public netgest.bo.runtime.attributes.boAttributeString value;    
    public netgest.bo.runtime.attributes.boAttributeString type;    
    public netgest.bo.runtime.attributes.boAttributeString action;    
    public netgest.bo.runtime.attributes.boAttributeNumber line;    
    public netgest.bo.runtime.attributes.boAttributeNumber version;    
    public netgest.bo.runtime.attributes.boAttributeNumber objectReference;    
    public netgest.bo.runtime.attributes.boAttributeObject PARENT;    
    public netgest.bo.runtime.attributes.boAttributeObject PARENTCTX;    
    public netgest.bo.runtime.attributes.boAttributeObject TEMPLATE;    
    public netgest.bo.runtime.attributes.boAttributeNumber BOUI;    
    public netgest.bo.runtime.attributes.boAttributeString CLASSNAME;    
    public netgest.bo.runtime.attributes.boAttributeObject CREATOR;    
    public netgest.bo.runtime.attributes.boAttributeDate SYS_DTCREATE;    
    public netgest.bo.runtime.attributes.boAttributeDate SYS_DTSAVE;    
    public netgest.bo.runtime.attributes.boAttributeString SYS_ORIGIN;    
    public netgest.bo.runtime.attributes.boAttributeObject SYS_FROMOBJ;    
   
     
    public Ebo_Log() {
        super();
        bo_version      = "1.0";
        bo_name         = "Ebo_Log";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("Ebo_Log");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
       

       
        
       
        atts.add(attribute = new netgest.bo.runtime.attributes.boAttributeString(this,"attribute"));
        
        atts.add(name = new netgest.bo.runtime.attributes.boAttributeString(this,"name"));
        
        atts.add(value_String = new netgest.bo.runtime.attributes.boAttributeString(this,"value_String"));
        
        atts.add(value_Long = new netgest.bo.runtime.attributes.boAttributeNumber(this,"value_Long"));
        
        atts.add(value_Date = new netgest.bo.runtime.attributes.boAttributeDate(this,"value_Date"));
        
        atts.add(value_CLOB = new netgest.bo.runtime.attributes.boAttributeString(this,"value_CLOB"));
        
        atts.add(value = new netgest.bo.runtime.attributes.boAttributeString(this,"value"));
        
        atts.add(type = new netgest.bo.runtime.attributes.boAttributeString(this,"type"));
        
        atts.add(action = new netgest.bo.runtime.attributes.boAttributeString(this,"action"));
        
        atts.add(line = new netgest.bo.runtime.attributes.boAttributeNumber(this,"line"));
        
        atts.add(version = new netgest.bo.runtime.attributes.boAttributeNumber(this,"version"));
        
        atts.add(objectReference = new netgest.bo.runtime.attributes.boAttributeNumber(this,"objectReference"));
        
        atts.add(PARENT = new netgest.bo.runtime.attributes.boAttributeObject(this,"PARENT"));
        
        atts.add(PARENTCTX = new netgest.bo.runtime.attributes.boAttributeObject(this,"PARENTCTX"));
        
        atts.add(TEMPLATE = new netgest.bo.runtime.attributes.boAttributeObject(this,"TEMPLATE"));
        
        atts.add(BOUI = new netgest.bo.runtime.attributes.boAttributeNumber(this,"BOUI"));
        
        atts.add(CLASSNAME = new netgest.bo.runtime.attributes.boAttributeString(this,"CLASSNAME"));
        
        atts.add(CREATOR = new netgest.bo.runtime.attributes.boAttributeObject(this,"CREATOR"));
        
        atts.add(SYS_DTCREATE = new netgest.bo.runtime.attributes.boAttributeDate(this,"SYS_DTCREATE"));
        
        atts.add(SYS_DTSAVE = new netgest.bo.runtime.attributes.boAttributeDate(this,"SYS_DTSAVE"));
        
        atts.add(SYS_ORIGIN = new netgest.bo.runtime.attributes.boAttributeString(this,"SYS_ORIGIN"));
        
        atts.add(SYS_FROMOBJ = new netgest.bo.runtime.attributes.boAttributeObject(this,"SYS_FROMOBJ"));
        



       
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
           
           
           
                if( attribute.haveDefaultValue()) 
                {
                	if(attribute.defaultValue() != null)
                	{
                    	attribute._setValue(boConvertUtils.convertToString(attribute.defaultValue(), attribute));
                    }
                    else
                    {
                    	attribute._setValue(null);
                    }
                    attribute.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( name.haveDefaultValue()) 
                {
                	if(name.defaultValue() != null)
                	{
                    	name._setValue(boConvertUtils.convertToString(name.defaultValue(), name));
                    }
                    else
                    {
                    	name._setValue(null);
                    }
                    name.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( value_String.haveDefaultValue()) 
                {
                	if(value_String.defaultValue() != null)
                	{
                    	value_String._setValue(boConvertUtils.convertToString(value_String.defaultValue(), value_String));
                    }
                    else
                    {
                    	value_String._setValue(null);
                    }
                    value_String.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( value_Long.haveDefaultValue()) 
                {
                	if(value_Long.defaultValue() != null)
                	{
                    	value_Long._setValue(boConvertUtils.convertToBigDecimal(value_Long.defaultValue(), value_Long));
                    }
                    else
                    {
                    	value_Long._setValue(null);
                    }
                    value_Long.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( value_Date.haveDefaultValue()) 
                {
                	if(value_Date.defaultValue() != null)
                	{
                    	value_Date._setValue(boConvertUtils.convertToTimestamp(value_Date.defaultValue(), value_Date));
                    }
                    else
                    {
                    	value_Date._setValue(null);
                    }
                    value_Date.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( value_CLOB.haveDefaultValue()) 
                {
                	if(value_CLOB.defaultValue() != null)
                	{
                    	value_CLOB._setValue(boConvertUtils.convertToString(value_CLOB.defaultValue(), value_CLOB));
                    }
                    else
                    {
                    	value_CLOB._setValue(null);
                    }
                    value_CLOB.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( value.haveDefaultValue()) 
                {
                	if(value.defaultValue() != null)
                	{
                    	value._setValue(boConvertUtils.convertToString(value.defaultValue(), value));
                    }
                    else
                    {
                    	value._setValue(null);
                    }
                    value.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( type.haveDefaultValue()) 
                {
                	if(type.defaultValue() != null)
                	{
                    	type._setValue(boConvertUtils.convertToString(type.defaultValue(), type));
                    }
                    else
                    {
                    	type._setValue(null);
                    }
                    type.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( action.haveDefaultValue()) 
                {
                	if(action.defaultValue() != null)
                	{
                    	action._setValue(boConvertUtils.convertToString(action.defaultValue(), action));
                    }
                    else
                    {
                    	action._setValue(null);
                    }
                    action.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( line.haveDefaultValue()) 
                {
                	if(line.defaultValue() != null)
                	{
                    	line._setValue(boConvertUtils.convertToBigDecimal(line.defaultValue(), line));
                    }
                    else
                    {
                    	line._setValue(null);
                    }
                    line.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( version.haveDefaultValue()) 
                {
                	if(version.defaultValue() != null)
                	{
                    	version._setValue(boConvertUtils.convertToBigDecimal(version.defaultValue(), version));
                    }
                    else
                    {
                    	version._setValue(null);
                    }
                    version.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( objectReference.haveDefaultValue()) 
                {
                	if(objectReference.defaultValue() != null)
                	{
                    	objectReference._setValue(boConvertUtils.convertToBigDecimal(objectReference.defaultValue(), objectReference));
                    }
                    else
                    {
                    	objectReference._setValue(null);
                    }
                    objectReference.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( PARENT.haveDefaultValue()) 
                {
                	if(PARENT.defaultValue() != null)
                	{
                    	PARENT._setValue(boConvertUtils.convertToBigDecimal(PARENT.defaultValue(), PARENT));
                    }
                    else
                    {
                    	PARENT._setValue(null);
                    }
                    PARENT.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( PARENTCTX.haveDefaultValue()) 
                {
                	if(PARENTCTX.defaultValue() != null)
                	{
                    	PARENTCTX._setValue(boConvertUtils.convertToBigDecimal(PARENTCTX.defaultValue(), PARENTCTX));
                    }
                    else
                    {
                    	PARENTCTX._setValue(null);
                    }
                    PARENTCTX.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( TEMPLATE.haveDefaultValue()) 
                {
                	if(TEMPLATE.defaultValue() != null)
                	{
                    	TEMPLATE._setValue(boConvertUtils.convertToBigDecimal(TEMPLATE.defaultValue(), TEMPLATE));
                    }
                    else
                    {
                    	TEMPLATE._setValue(null);
                    }
                    TEMPLATE.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( BOUI.haveDefaultValue()) 
                {
                	if(BOUI.defaultValue() != null)
                	{
                    	BOUI._setValue(boConvertUtils.convertToBigDecimal(BOUI.defaultValue(), BOUI));
                    }
                    else
                    {
                    	BOUI._setValue(null);
                    }
                    BOUI.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( CLASSNAME.haveDefaultValue()) 
                {
                	if(CLASSNAME.defaultValue() != null)
                	{
                    	CLASSNAME._setValue(boConvertUtils.convertToString(CLASSNAME.defaultValue(), CLASSNAME));
                    }
                    else
                    {
                    	CLASSNAME._setValue(null);
                    }
                    CLASSNAME.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( CREATOR.haveDefaultValue()) 
                {
                	if(CREATOR.defaultValue() != null)
                	{
                    	CREATOR._setValue(boConvertUtils.convertToBigDecimal(CREATOR.defaultValue(), CREATOR));
                    }
                    else
                    {
                    	CREATOR._setValue(null);
                    }
                    CREATOR.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( SYS_DTCREATE.haveDefaultValue()) 
                {
                	if(SYS_DTCREATE.defaultValue() != null)
                	{
                    	SYS_DTCREATE._setValue(boConvertUtils.convertToTimestamp(SYS_DTCREATE.defaultValue(), SYS_DTCREATE));
                    }
                    else
                    {
                    	SYS_DTCREATE._setValue(null);
                    }
                    SYS_DTCREATE.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( SYS_DTSAVE.haveDefaultValue()) 
                {
                	if(SYS_DTSAVE.defaultValue() != null)
                	{
                    	SYS_DTSAVE._setValue(boConvertUtils.convertToTimestamp(SYS_DTSAVE.defaultValue(), SYS_DTSAVE));
                    }
                    else
                    {
                    	SYS_DTSAVE._setValue(null);
                    }
                    SYS_DTSAVE.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( SYS_ORIGIN.haveDefaultValue()) 
                {
                	if(SYS_ORIGIN.defaultValue() != null)
                	{
                    	SYS_ORIGIN._setValue(boConvertUtils.convertToString(SYS_ORIGIN.defaultValue(), SYS_ORIGIN));
                    }
                    else
                    {
                    	SYS_ORIGIN._setValue(null);
                    }
                    SYS_ORIGIN.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( SYS_FROMOBJ.haveDefaultValue()) 
                {
                	if(SYS_FROMOBJ.defaultValue() != null)
                	{
                    	SYS_FROMOBJ._setValue(boConvertUtils.convertToBigDecimal(SYS_FROMOBJ.defaultValue(), SYS_FROMOBJ));
                    }
                    else
                    {
                    	SYS_FROMOBJ._setValue(null);
                    }
                    SYS_FROMOBJ.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
    
            if("Ebo_Log".equals(this.getName()))
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
	
	public void onAfterLoad(boEvent event) throws boRuntimeException {
	netgest.bo.events.logEvents.afterLoadLog( this );
}

    
    
}
