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



public  class BCM_Contact extends boObject implements  Serializable {  

   
   
   
    public netgest.bo.runtime.attributes.boAttributeObject entity;    
    public netgest.bo.runtime.attributes.boAttributeString name;    
    public netgest.bo.runtime.attributes.boAttributeString cod_ent;    
    public netgest.bo.runtime.attributes.boAttributeNumber num_con;    
    public netgest.bo.runtime.attributes.boAttributeString type;    
    public netgest.bo.runtime.attributes.boAttributeString cargo;    
    public netgest.bo.runtime.attributes.boAttributeString contactName;    
    public netgest.bo.runtime.attributes.boAttributeNumber addressNumber;    
    public netgest.bo.runtime.attributes.boAttributeString street;    
    public netgest.bo.runtime.attributes.boAttributeString local;    
    public netgest.bo.runtime.attributes.boAttributeString postalCode;    
    public netgest.bo.runtime.attributes.boAttributeString postalLocal;    
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
   
     
    public BCM_Contact() {
        super();
        bo_version      = "1.0";
        bo_name         = "BCM_Contact";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("BCM_Contact");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
       

       
        
       
        atts.add(entity = new netgest.bo.runtime.attributes.boAttributeObject(this,"entity"));
        
        atts.add(name = new netgest.bo.runtime.attributes.boAttributeString(this,"name"));
        
        atts.add(cod_ent = new netgest.bo.runtime.attributes.boAttributeString(this,"cod_ent"));
        
        atts.add(num_con = new netgest.bo.runtime.attributes.boAttributeNumber(this,"num_con"));
        
        atts.add(type = new netgest.bo.runtime.attributes.boAttributeString(this,"type"));
        
        atts.add(cargo = new netgest.bo.runtime.attributes.boAttributeString(this,"cargo"));
        
        atts.add(contactName = new netgest.bo.runtime.attributes.boAttributeString(this,"contactName"));
        
        atts.add(addressNumber = new netgest.bo.runtime.attributes.boAttributeNumber(this,"addressNumber"));
        
        atts.add(street = new netgest.bo.runtime.attributes.boAttributeString(this,"street"));
        
        atts.add(local = new netgest.bo.runtime.attributes.boAttributeString(this,"local"));
        
        atts.add(postalCode = new netgest.bo.runtime.attributes.boAttributeString(this,"postalCode"));
        
        atts.add(postalLocal = new netgest.bo.runtime.attributes.boAttributeString(this,"postalLocal"));
        
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
           
           
           
                if( entity.haveDefaultValue()) 
                {
                	if(entity.defaultValue() != null)
                	{
                    	entity._setValue(boConvertUtils.convertToBigDecimal(entity.defaultValue(), entity));
                    }
                    else
                    {
                    	entity._setValue(null);
                    }
                    entity.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( cod_ent.haveDefaultValue()) 
                {
                	if(cod_ent.defaultValue() != null)
                	{
                    	cod_ent._setValue(boConvertUtils.convertToString(cod_ent.defaultValue(), cod_ent));
                    }
                    else
                    {
                    	cod_ent._setValue(null);
                    }
                    cod_ent.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( num_con.haveDefaultValue()) 
                {
                	if(num_con.defaultValue() != null)
                	{
                    	num_con._setValue(boConvertUtils.convertToBigDecimal(num_con.defaultValue(), num_con));
                    }
                    else
                    {
                    	num_con._setValue(null);
                    }
                    num_con.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( cargo.haveDefaultValue()) 
                {
                	if(cargo.defaultValue() != null)
                	{
                    	cargo._setValue(boConvertUtils.convertToString(cargo.defaultValue(), cargo));
                    }
                    else
                    {
                    	cargo._setValue(null);
                    }
                    cargo.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( contactName.haveDefaultValue()) 
                {
                	if(contactName.defaultValue() != null)
                	{
                    	contactName._setValue(boConvertUtils.convertToString(contactName.defaultValue(), contactName));
                    }
                    else
                    {
                    	contactName._setValue(null);
                    }
                    contactName.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( addressNumber.haveDefaultValue()) 
                {
                	if(addressNumber.defaultValue() != null)
                	{
                    	addressNumber._setValue(boConvertUtils.convertToBigDecimal(addressNumber.defaultValue(), addressNumber));
                    }
                    else
                    {
                    	addressNumber._setValue(null);
                    }
                    addressNumber.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( street.haveDefaultValue()) 
                {
                	if(street.defaultValue() != null)
                	{
                    	street._setValue(boConvertUtils.convertToString(street.defaultValue(), street));
                    }
                    else
                    {
                    	street._setValue(null);
                    }
                    street.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( local.haveDefaultValue()) 
                {
                	if(local.defaultValue() != null)
                	{
                    	local._setValue(boConvertUtils.convertToString(local.defaultValue(), local));
                    }
                    else
                    {
                    	local._setValue(null);
                    }
                    local.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( postalCode.haveDefaultValue()) 
                {
                	if(postalCode.defaultValue() != null)
                	{
                    	postalCode._setValue(boConvertUtils.convertToString(postalCode.defaultValue(), postalCode));
                    }
                    else
                    {
                    	postalCode._setValue(null);
                    }
                    postalCode.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( postalLocal.haveDefaultValue()) 
                {
                	if(postalLocal.defaultValue() != null)
                	{
                    	postalLocal._setValue(boConvertUtils.convertToString(postalLocal.defaultValue(), postalLocal));
                    }
                    else
                    {
                    	postalLocal._setValue(null);
                    }
                    postalLocal.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
    
            if("BCM_Contact".equals(this.getName()))
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
