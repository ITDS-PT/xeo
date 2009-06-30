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



public  class Ebo_Login extends boObject implements  Serializable {  

   
   
   
    public netgest.bo.runtime.attributes.boAttributeObject user;    
    public netgest.bo.runtime.attributes.boAttributeDate data;    
    public netgest.bo.runtime.attributes.boAttributeString movementType;    
    public netgest.bo.runtime.attributes.boAttributeString repository;    
    public netgest.bo.runtime.attributes.boAttributeObject iProfile;    
    public netgest.bo.runtime.attributes.boAttributeString remoteAddr;    
    public netgest.bo.runtime.attributes.boAttributeString remoteHost;    
    public netgest.bo.runtime.attributes.boAttributeString remoteUser;    
    public netgest.bo.runtime.attributes.boAttributeString remoteSessionId;    
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
   
     
    public Ebo_Login() {
        super();
        bo_version      = "1.0";
        bo_name         = "Ebo_Login";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("Ebo_Login");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
       

       
        
       
        atts.add(user = new netgest.bo.runtime.attributes.boAttributeObject(this,"user"));
        
        atts.add(data = new netgest.bo.runtime.attributes.boAttributeDate(this,"data"));
        
        atts.add(movementType = new netgest.bo.runtime.attributes.boAttributeString(this,"movementType"));
        
        atts.add(repository = new netgest.bo.runtime.attributes.boAttributeString(this,"repository"));
        
        atts.add(iProfile = new netgest.bo.runtime.attributes.boAttributeObject(this,"iProfile"));
        
        atts.add(remoteAddr = new netgest.bo.runtime.attributes.boAttributeString(this,"remoteAddr"));
        
        atts.add(remoteHost = new netgest.bo.runtime.attributes.boAttributeString(this,"remoteHost"));
        
        atts.add(remoteUser = new netgest.bo.runtime.attributes.boAttributeString(this,"remoteUser"));
        
        atts.add(remoteSessionId = new netgest.bo.runtime.attributes.boAttributeString(this,"remoteSessionId"));
        
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
           
           
           
                if( user.haveDefaultValue()) 
                {
                	if(user.defaultValue() != null)
                	{
                    	user._setValue(boConvertUtils.convertToBigDecimal(user.defaultValue(), user));
                    }
                    else
                    {
                    	user._setValue(null);
                    }
                    user.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( data.haveDefaultValue()) 
                {
                	if(data.defaultValue() != null)
                	{
                    	data._setValue(boConvertUtils.convertToTimestamp(data.defaultValue(), data));
                    }
                    else
                    {
                    	data._setValue(null);
                    }
                    data.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( movementType.haveDefaultValue()) 
                {
                	if(movementType.defaultValue() != null)
                	{
                    	movementType._setValue(boConvertUtils.convertToString(movementType.defaultValue(), movementType));
                    }
                    else
                    {
                    	movementType._setValue(null);
                    }
                    movementType.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( repository.haveDefaultValue()) 
                {
                	if(repository.defaultValue() != null)
                	{
                    	repository._setValue(boConvertUtils.convertToString(repository.defaultValue(), repository));
                    }
                    else
                    {
                    	repository._setValue(null);
                    }
                    repository.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( iProfile.haveDefaultValue()) 
                {
                	if(iProfile.defaultValue() != null)
                	{
                    	iProfile._setValue(boConvertUtils.convertToBigDecimal(iProfile.defaultValue(), iProfile));
                    }
                    else
                    {
                    	iProfile._setValue(null);
                    }
                    iProfile.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( remoteAddr.haveDefaultValue()) 
                {
                	if(remoteAddr.defaultValue() != null)
                	{
                    	remoteAddr._setValue(boConvertUtils.convertToString(remoteAddr.defaultValue(), remoteAddr));
                    }
                    else
                    {
                    	remoteAddr._setValue(null);
                    }
                    remoteAddr.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( remoteHost.haveDefaultValue()) 
                {
                	if(remoteHost.defaultValue() != null)
                	{
                    	remoteHost._setValue(boConvertUtils.convertToString(remoteHost.defaultValue(), remoteHost));
                    }
                    else
                    {
                    	remoteHost._setValue(null);
                    }
                    remoteHost.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( remoteUser.haveDefaultValue()) 
                {
                	if(remoteUser.defaultValue() != null)
                	{
                    	remoteUser._setValue(boConvertUtils.convertToString(remoteUser.defaultValue(), remoteUser));
                    }
                    else
                    {
                    	remoteUser._setValue(null);
                    }
                    remoteUser.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( remoteSessionId.haveDefaultValue()) 
                {
                	if(remoteSessionId.defaultValue() != null)
                	{
                    	remoteSessionId._setValue(boConvertUtils.convertToString(remoteSessionId.defaultValue(), remoteSessionId));
                    }
                    else
                    {
                    	remoteSessionId._setValue(null);
                    }
                    remoteSessionId.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
    
            if("Ebo_Login".equals(this.getName()))
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
