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



public  class xwfParticipant extends boObject implements  Serializable {  

   
   
   
    public netgest.bo.runtime.attributes.boAttributeString name;    
    public netgest.bo.runtime.attributes.boAttributeString label;    
    public netgest.bo.runtime.attributes.boAttributeString description;    
    public netgest.bo.runtime.attributes.boAttributeNumber mode;    
    public netgest.bo.runtime.attributes.boAttributeString required;    
    public netgest.bo.runtime.attributes.boAttributeString objectFilterQuery;    
    public netgest.bo.runtime.attributes.boAttributeNumber showMode;    
    public netgest.bo.runtime.attributes.boAttributeString input;    
    public netgest.bo.runtime.attributes.boAttributeString formula;    
    public netgest.bo.runtime.attributes.boAttributeObject value;    
    public netgest.bo.runtime.attributes.boAttributeString staticKey;    
    public netgest.bo.runtime.attributes.boAttributeObject subProgDef;    
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
   
    public Handlerstate state;    
     
    public xwfParticipant() {
        super();
        bo_version      = "1.0";
        bo_name         = "xwfParticipant";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("xwfParticipant");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
       

       
        
       
        atts.add(name = new netgest.bo.runtime.attributes.boAttributeString(this,"name"));
        
        atts.add(label = new netgest.bo.runtime.attributes.boAttributeString(this,"label"));
        
        atts.add(description = new netgest.bo.runtime.attributes.boAttributeString(this,"description"));
        
        atts.add(mode = new netgest.bo.runtime.attributes.boAttributeNumber(this,"mode"));
        
        atts.add(required = new netgest.bo.runtime.attributes.boAttributeString(this,"required"));
        
        atts.add(objectFilterQuery = new netgest.bo.runtime.attributes.boAttributeString(this,"objectFilterQuery"));
        
        atts.add(showMode = new netgest.bo.runtime.attributes.boAttributeNumber(this,"showMode"));
        
        atts.add(input = new netgest.bo.runtime.attributes.boAttributeString(this,"input"));
        
        atts.add(formula = new netgest.bo.runtime.attributes.boAttributeString(this,"formula"));
        
        atts.add(value = new netgest.bo.runtime.attributes.boAttributeObject(this,"value"));
        
        atts.add(staticKey = new netgest.bo.runtime.attributes.boAttributeString(this,"staticKey"));
        
        atts.add(subProgDef = new netgest.bo.runtime.attributes.boAttributeObject(this,"subProgDef"));
        
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
        



       
        atts.add(state = new Handlerstate(this));
        stat.add(state);
        
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
            
                if( label.haveDefaultValue()) 
                {
                	if(label.defaultValue() != null)
                	{
                    	label._setValue(boConvertUtils.convertToString(label.defaultValue(), label));
                    }
                    else
                    {
                    	label._setValue(null);
                    }
                    label.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( description.haveDefaultValue()) 
                {
                	if(description.defaultValue() != null)
                	{
                    	description._setValue(boConvertUtils.convertToString(description.defaultValue(), description));
                    }
                    else
                    {
                    	description._setValue(null);
                    }
                    description.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( mode.haveDefaultValue()) 
                {
                	if(mode.defaultValue() != null)
                	{
                    	mode._setValue(boConvertUtils.convertToBigDecimal(mode.defaultValue(), mode));
                    }
                    else
                    {
                    	mode._setValue(null);
                    }
                    mode.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( required.haveDefaultValue()) 
                {
                	if(required.defaultValue() != null)
                	{
                    	required._setValue(boConvertUtils.convertToString(required.defaultValue(), required));
                    }
                    else
                    {
                    	required._setValue(null);
                    }
                    required.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( objectFilterQuery.haveDefaultValue()) 
                {
                	if(objectFilterQuery.defaultValue() != null)
                	{
                    	objectFilterQuery._setValue(boConvertUtils.convertToString(objectFilterQuery.defaultValue(), objectFilterQuery));
                    }
                    else
                    {
                    	objectFilterQuery._setValue(null);
                    }
                    objectFilterQuery.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( showMode.haveDefaultValue()) 
                {
                	if(showMode.defaultValue() != null)
                	{
                    	showMode._setValue(boConvertUtils.convertToBigDecimal(showMode.defaultValue(), showMode));
                    }
                    else
                    {
                    	showMode._setValue(null);
                    }
                    showMode.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( input.haveDefaultValue()) 
                {
                	if(input.defaultValue() != null)
                	{
                    	input._setValue(boConvertUtils.convertToString(input.defaultValue(), input));
                    }
                    else
                    {
                    	input._setValue(null);
                    }
                    input.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( formula.haveDefaultValue()) 
                {
                	if(formula.defaultValue() != null)
                	{
                    	formula._setValue(boConvertUtils.convertToString(formula.defaultValue(), formula));
                    }
                    else
                    {
                    	formula._setValue(null);
                    }
                    formula.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( value.haveDefaultValue()) 
                {
                	if(value.defaultValue() != null)
                	{
                    	value._setValue(boConvertUtils.convertToBigDecimal(value.defaultValue(), value));
                    }
                    else
                    {
                    	value._setValue(null);
                    }
                    value.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( staticKey.haveDefaultValue()) 
                {
                	if(staticKey.defaultValue() != null)
                	{
                    	staticKey._setValue(boConvertUtils.convertToString(staticKey.defaultValue(), staticKey));
                    }
                    else
                    {
                    	staticKey._setValue(null);
                    }
                    staticKey.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( subProgDef.haveDefaultValue()) 
                {
                	if(subProgDef.defaultValue() != null)
                	{
                    	subProgDef._setValue(boConvertUtils.convertToBigDecimal(subProgDef.defaultValue(), subProgDef));
                    }
                    else
                    {
                    	subProgDef._setValue(null);
                    }
                    subProgDef.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
    
            if("xwfParticipant".equals(this.getName()))
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
   
    
   
    

   

   
    public final class Handlerstate extends boObjectStateHandler {
       
        public final String STATE_Inactive="0";
        
        public final String STATE_active="1";
        
        public Handlerstate(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("state"));
        }
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
