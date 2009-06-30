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



public  class address extends boObject implements  Serializable {  

   
   
   
    public netgest.bo.runtime.attributes.boAttributeString tipomorada;    
    public netgest.bo.runtime.attributes.boAttributeString description;    
    public netgest.bo.runtime.attributes.boAttributeString email;    
    public netgest.bo.runtime.attributes.boAttributeString fax;    
    public netgest.bo.runtime.attributes.boAttributeString telefone;    
    public netgest.bo.runtime.attributes.boAttributeString rua;    
    public netgest.bo.runtime.attributes.boAttributeString localidade;    
    public netgest.bo.runtime.attributes.boAttributeString cpostal;    
    public netgest.bo.runtime.attributes.boAttributeString localcpostal;    
    public netgest.bo.runtime.attributes.boAttributeString freguesia;    
    public netgest.bo.runtime.attributes.boAttributeNumber concelho;    
    public netgest.bo.runtime.attributes.boAttributeNumber distrito;    
    public netgest.bo.runtime.attributes.boAttributeString country;    
    public netgest.bo.runtime.attributes.boAttributeString regiao;    
    public netgest.bo.runtime.attributes.boAttributeString continente;    
    public netgest.bo.runtime.attributes.boAttributeString cod_ent;    
    public netgest.bo.runtime.attributes.boAttributeNumber ide_reg_ent;    
    public netgest.bo.runtime.attributes.boAttributeNumber num_mor;    
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
   
    public HandlerprimaryState primaryState;    
    public HandlercreatedStatus createdStatus;    
    public Handleropen_levels open_levels;    
    public Handlerhold hold;    
    public Handlerworking working;    
    public HandlerwaitFor waitFor;    
    public HandlerredAlert redAlert;    
    public HandlerOrangeAlert OrangeAlert;    
    public HandlerGreen Green;    
    public HandlercloseStatus closeStatus;    
     
    public address() {
        super();
        bo_version      = "1.0";
        bo_name         = "address";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("address");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
       

       
        
       
        atts.add(tipomorada = new netgest.bo.runtime.attributes.boAttributeString(this,"tipomorada"));
        
        atts.add(description = new netgest.bo.runtime.attributes.boAttributeString(this,"description"));
        
        atts.add(email = new netgest.bo.runtime.attributes.boAttributeString(this,"email"));
        
        atts.add(fax = new netgest.bo.runtime.attributes.boAttributeString(this,"fax"));
        
        atts.add(telefone = new netgest.bo.runtime.attributes.boAttributeString(this,"telefone"));
        
        atts.add(rua = new netgest.bo.runtime.attributes.boAttributeString(this,"rua"));
        
        atts.add(localidade = new netgest.bo.runtime.attributes.boAttributeString(this,"localidade"));
        
        atts.add(cpostal = new netgest.bo.runtime.attributes.boAttributeString(this,"cpostal"));
        
        atts.add(localcpostal = new netgest.bo.runtime.attributes.boAttributeString(this,"localcpostal"));
        
        atts.add(freguesia = new netgest.bo.runtime.attributes.boAttributeString(this,"freguesia"));
        
        atts.add(concelho = new netgest.bo.runtime.attributes.boAttributeNumber(this,"concelho"));
        
        atts.add(distrito = new netgest.bo.runtime.attributes.boAttributeNumber(this,"distrito"));
        
        atts.add(country = new netgest.bo.runtime.attributes.boAttributeString(this,"country"));
        
        atts.add(regiao = new netgest.bo.runtime.attributes.boAttributeString(this,"regiao"));
        
        atts.add(continente = new netgest.bo.runtime.attributes.boAttributeString(this,"continente"));
        
        atts.add(cod_ent = new netgest.bo.runtime.attributes.boAttributeString(this,"cod_ent"));
        
        atts.add(ide_reg_ent = new netgest.bo.runtime.attributes.boAttributeNumber(this,"ide_reg_ent"));
        
        atts.add(num_mor = new netgest.bo.runtime.attributes.boAttributeNumber(this,"num_mor"));
        
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
        



       
        atts.add(primaryState = new HandlerprimaryState(this));
        stat.add(primaryState);
        
        atts.add(createdStatus = new HandlercreatedStatus(this));
        stat.add(createdStatus);
        
        atts.add(open_levels = new Handleropen_levels(this));
        stat.add(open_levels);
        
        atts.add(hold = new Handlerhold(this));
        stat.add(hold);
        
        atts.add(working = new Handlerworking(this));
        stat.add(working);
        
        atts.add(waitFor = new HandlerwaitFor(this));
        stat.add(waitFor);
        
        atts.add(redAlert = new HandlerredAlert(this));
        stat.add(redAlert);
        
        atts.add(OrangeAlert = new HandlerOrangeAlert(this));
        stat.add(OrangeAlert);
        
        atts.add(Green = new HandlerGreen(this));
        stat.add(Green);
        
        atts.add(closeStatus = new HandlercloseStatus(this));
        stat.add(closeStatus);
        
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
           
           
           
                if( tipomorada.haveDefaultValue()) 
                {
                	if(tipomorada.defaultValue() != null)
                	{
                    	tipomorada._setValue(boConvertUtils.convertToString(tipomorada.defaultValue(), tipomorada));
                    }
                    else
                    {
                    	tipomorada._setValue(null);
                    }
                    tipomorada.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( email.haveDefaultValue()) 
                {
                	if(email.defaultValue() != null)
                	{
                    	email._setValue(boConvertUtils.convertToString(email.defaultValue(), email));
                    }
                    else
                    {
                    	email._setValue(null);
                    }
                    email.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( fax.haveDefaultValue()) 
                {
                	if(fax.defaultValue() != null)
                	{
                    	fax._setValue(boConvertUtils.convertToString(fax.defaultValue(), fax));
                    }
                    else
                    {
                    	fax._setValue(null);
                    }
                    fax.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( telefone.haveDefaultValue()) 
                {
                	if(telefone.defaultValue() != null)
                	{
                    	telefone._setValue(boConvertUtils.convertToString(telefone.defaultValue(), telefone));
                    }
                    else
                    {
                    	telefone._setValue(null);
                    }
                    telefone.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( rua.haveDefaultValue()) 
                {
                	if(rua.defaultValue() != null)
                	{
                    	rua._setValue(boConvertUtils.convertToString(rua.defaultValue(), rua));
                    }
                    else
                    {
                    	rua._setValue(null);
                    }
                    rua.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( localidade.haveDefaultValue()) 
                {
                	if(localidade.defaultValue() != null)
                	{
                    	localidade._setValue(boConvertUtils.convertToString(localidade.defaultValue(), localidade));
                    }
                    else
                    {
                    	localidade._setValue(null);
                    }
                    localidade.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( cpostal.haveDefaultValue()) 
                {
                	if(cpostal.defaultValue() != null)
                	{
                    	cpostal._setValue(boConvertUtils.convertToString(cpostal.defaultValue(), cpostal));
                    }
                    else
                    {
                    	cpostal._setValue(null);
                    }
                    cpostal.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( localcpostal.haveDefaultValue()) 
                {
                	if(localcpostal.defaultValue() != null)
                	{
                    	localcpostal._setValue(boConvertUtils.convertToString(localcpostal.defaultValue(), localcpostal));
                    }
                    else
                    {
                    	localcpostal._setValue(null);
                    }
                    localcpostal.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( freguesia.haveDefaultValue()) 
                {
                	if(freguesia.defaultValue() != null)
                	{
                    	freguesia._setValue(boConvertUtils.convertToString(freguesia.defaultValue(), freguesia));
                    }
                    else
                    {
                    	freguesia._setValue(null);
                    }
                    freguesia.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( concelho.haveDefaultValue()) 
                {
                	if(concelho.defaultValue() != null)
                	{
                    	concelho._setValue(boConvertUtils.convertToBigDecimal(concelho.defaultValue(), concelho));
                    }
                    else
                    {
                    	concelho._setValue(null);
                    }
                    concelho.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( distrito.haveDefaultValue()) 
                {
                	if(distrito.defaultValue() != null)
                	{
                    	distrito._setValue(boConvertUtils.convertToBigDecimal(distrito.defaultValue(), distrito));
                    }
                    else
                    {
                    	distrito._setValue(null);
                    }
                    distrito.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( country.haveDefaultValue()) 
                {
                	if(country.defaultValue() != null)
                	{
                    	country._setValue(boConvertUtils.convertToString(country.defaultValue(), country));
                    }
                    else
                    {
                    	country._setValue(null);
                    }
                    country.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( regiao.haveDefaultValue()) 
                {
                	if(regiao.defaultValue() != null)
                	{
                    	regiao._setValue(boConvertUtils.convertToString(regiao.defaultValue(), regiao));
                    }
                    else
                    {
                    	regiao._setValue(null);
                    }
                    regiao.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( continente.haveDefaultValue()) 
                {
                	if(continente.defaultValue() != null)
                	{
                    	continente._setValue(boConvertUtils.convertToString(continente.defaultValue(), continente));
                    }
                    else
                    {
                    	continente._setValue(null);
                    }
                    continente.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( ide_reg_ent.haveDefaultValue()) 
                {
                	if(ide_reg_ent.defaultValue() != null)
                	{
                    	ide_reg_ent._setValue(boConvertUtils.convertToBigDecimal(ide_reg_ent.defaultValue(), ide_reg_ent));
                    }
                    else
                    {
                    	ide_reg_ent._setValue(null);
                    }
                    ide_reg_ent.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( num_mor.haveDefaultValue()) 
                {
                	if(num_mor.defaultValue() != null)
                	{
                    	num_mor._setValue(boConvertUtils.convertToBigDecimal(num_mor.defaultValue(), num_mor));
                    }
                    else
                    {
                    	num_mor._setValue(null);
                    }
                    num_mor.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
    
            if("address".equals(this.getName()))
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
   
    
   
    

   

   
    public final class HandlerprimaryState extends boObjectStateHandler {
       
        public final String STATE_create="0";
        
        public final String STATE_open="1";
        
        public final String STATE_close="90";
        
        public final String STATE_archive="99";
        
        public HandlerprimaryState(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("primaryState"));
        }
    }

    
    public final class HandlercreatedStatus extends boObjectStateHandler {
       
        public final String STATE_notStart="1";
        
        public HandlercreatedStatus(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("createdStatus"));
        }
    }

    
    public final class Handleropen_levels extends boObjectStateHandler {
       
        public final String STATE_openStatus="0";
        
        public final String STATE_openAlerts="0";
        
        public Handleropen_levels(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("open_levels"));
        }
    }

    
    public final class Handlerhold extends boObjectStateHandler {
       
        public Handlerhold(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("hold"));
        }
    }

    
    public final class Handlerworking extends boObjectStateHandler {
       
        public Handlerworking(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("working"));
        }
    }

    
    public final class HandlerwaitFor extends boObjectStateHandler {
       
        public HandlerwaitFor(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("waitFor"));
        }
    }

    
    public final class HandlerredAlert extends boObjectStateHandler {
       
        public HandlerredAlert(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("redAlert"));
        }
    }

    
    public final class HandlerOrangeAlert extends boObjectStateHandler {
       
        public HandlerOrangeAlert(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("OrangeAlert"));
        }
    }

    
    public final class HandlerGreen extends boObjectStateHandler {
       
        public HandlerGreen(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("Green"));
        }
    }

    
    public final class HandlercloseStatus extends boObjectStateHandler {
       
        public final String STATE_cancel="91";
        
        public final String STATE_softclose="92";
        
        public final String STATE_hardclose="93";
        
        public HandlercloseStatus(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("closeStatus"));
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
