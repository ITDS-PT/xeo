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



public  class Ebo_Schedule extends boObject implements  Serializable {  

   
   
    public Handlerinterval interval;    
    public Handlertype type;    
    public Handlerevery every;    
    public Handlereveryweek everyweek;    
    public Handlereverymonth everymonth;    
    public Handlerwhen when;    
    public HandleractiveStatus activeStatus;    
   
    public netgest.bo.runtime.attributes.boAttributeString id;    
    public netgest.bo.runtime.attributes.boAttributeString description;    
    public netgest.bo.runtime.attributes.boAttributeString javaclass;    
    public netgest.bo.runtime.attributes.boAttributeString parameters;    
    public netgest.bo.runtime.attributes.boAttributeString onerrorjavaclass;    
    public netgest.bo.runtime.attributes.boAttributeString onerrorpar;    
    public netgest.bo.runtime.attributes.boAttributeObject performer;    
    public netgest.bo.runtime.attributes.boAttributeDate startdate;    
    public netgest.bo.runtime.attributes.boAttributeDate enddate;    
    public netgest.bo.runtime.attributes.boAttributeDate lastruntime;    
    public netgest.bo.runtime.attributes.boAttributeDate nextruntime;    
    public netgest.bo.runtime.attributes.boAttributeString lastresultcode;    
    public netgest.bo.runtime.attributes.boAttributeString errormessage;    
    public netgest.bo.runtime.attributes.boAttributeNumber executiontime;    
    public netgest.bo.runtime.attributes.boAttributeNumber timeout;    
    public netgest.bo.runtime.attributes.boAttributeString lastid;    
    public netgest.bo.runtime.attributes.boAttributeNumber state;    
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
   
     
    public Ebo_Schedule() {
        super();
        bo_version      = "1.0";
        bo_name         = "Ebo_Schedule";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("Ebo_Schedule");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
       

       
        atts.add(interval = new Handlerinterval(this));
        
        atts.add(type = new Handlertype(this));
        
        atts.add(every = new Handlerevery(this));
        
        atts.add(everyweek = new Handlereveryweek(this));
        
        atts.add(everymonth = new Handlereverymonth(this));
        
        atts.add(when = new Handlerwhen(this));
        
        atts.add(activeStatus = new HandleractiveStatus(this));
        
        
       
        atts.add(id = new netgest.bo.runtime.attributes.boAttributeString(this,"id"));
        
        atts.add(description = new netgest.bo.runtime.attributes.boAttributeString(this,"description"));
        
        atts.add(javaclass = new netgest.bo.runtime.attributes.boAttributeString(this,"javaclass"));
        
        atts.add(parameters = new netgest.bo.runtime.attributes.boAttributeString(this,"parameters"));
        
        atts.add(onerrorjavaclass = new netgest.bo.runtime.attributes.boAttributeString(this,"onerrorjavaclass"));
        
        atts.add(onerrorpar = new netgest.bo.runtime.attributes.boAttributeString(this,"onerrorpar"));
        
        atts.add(performer = new netgest.bo.runtime.attributes.boAttributeObject(this,"performer"));
        
        atts.add(startdate = new netgest.bo.runtime.attributes.boAttributeDate(this,"startdate"));
        
        atts.add(enddate = new netgest.bo.runtime.attributes.boAttributeDate(this,"enddate"));
        
        atts.add(lastruntime = new netgest.bo.runtime.attributes.boAttributeDate(this,"lastruntime"));
        
        atts.add(nextruntime = new netgest.bo.runtime.attributes.boAttributeDate(this,"nextruntime"));
        
        atts.add(lastresultcode = new netgest.bo.runtime.attributes.boAttributeString(this,"lastresultcode"));
        
        atts.add(errormessage = new netgest.bo.runtime.attributes.boAttributeString(this,"errormessage"));
        
        atts.add(executiontime = new netgest.bo.runtime.attributes.boAttributeNumber(this,"executiontime"));
        
        atts.add(timeout = new netgest.bo.runtime.attributes.boAttributeNumber(this,"timeout"));
        
        atts.add(lastid = new netgest.bo.runtime.attributes.boAttributeString(this,"lastid"));
        
        atts.add(state = new netgest.bo.runtime.attributes.boAttributeNumber(this,"state"));
        
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
           
           
                
                    if( interval.haveDefaultValue()) 
                    {
                    	if(interval.defaultValue() != null)
                    	{
                        	interval._setValue(boConvertUtils.convertToBigDecimal(interval.defaultValue(), interval));
                        }
                        else
                        {
                        	interval._setValue(null);
                        }
                        interval.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
                
            
                
                    if( every.haveDefaultValue()) 
                    {
                    	if(every.defaultValue() != null)
                    	{
                        	every._setValue(boConvertUtils.convertToString(every.defaultValue(), every));
                        }
                        else
                        {
                        	every._setValue(null);
                        }
                        every.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
                
                    if( everyweek.haveDefaultValue()) 
                    {
                    	if(everyweek.defaultValue() != null)
                    	{
                        	everyweek._setValue(boConvertUtils.convertToString(everyweek.defaultValue(), everyweek));
                        }
                        else
                        {
                        	everyweek._setValue(null);
                        }
                        everyweek.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
                
                    if( everymonth.haveDefaultValue()) 
                    {
                    	if(everymonth.defaultValue() != null)
                    	{
                        	everymonth._setValue(boConvertUtils.convertToString(everymonth.defaultValue(), everymonth));
                        }
                        else
                        {
                        	everymonth._setValue(null);
                        }
                        everymonth.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
                
                    if( when.haveDefaultValue()) 
                    {
                    	if(when.defaultValue() != null)
                    	{
                        	when._setValue(boConvertUtils.convertToString(when.defaultValue(), when));
                        }
                        else
                        {
                        	when._setValue(null);
                        }
                        when.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
                
                    if( activeStatus.haveDefaultValue()) 
                    {
                    	if(activeStatus.defaultValue() != null)
                    	{
                        	activeStatus._setValue(boConvertUtils.convertToBigDecimal(activeStatus.defaultValue(), activeStatus));
                        }
                        else
                        {
                        	activeStatus._setValue(null);
                        }
                        activeStatus.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
           
                if( id.haveDefaultValue()) 
                {
                	if(id.defaultValue() != null)
                	{
                    	id._setValue(boConvertUtils.convertToString(id.defaultValue(), id));
                    }
                    else
                    {
                    	id._setValue(null);
                    }
                    id.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( javaclass.haveDefaultValue()) 
                {
                	if(javaclass.defaultValue() != null)
                	{
                    	javaclass._setValue(boConvertUtils.convertToString(javaclass.defaultValue(), javaclass));
                    }
                    else
                    {
                    	javaclass._setValue(null);
                    }
                    javaclass.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( parameters.haveDefaultValue()) 
                {
                	if(parameters.defaultValue() != null)
                	{
                    	parameters._setValue(boConvertUtils.convertToString(parameters.defaultValue(), parameters));
                    }
                    else
                    {
                    	parameters._setValue(null);
                    }
                    parameters.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( onerrorjavaclass.haveDefaultValue()) 
                {
                	if(onerrorjavaclass.defaultValue() != null)
                	{
                    	onerrorjavaclass._setValue(boConvertUtils.convertToString(onerrorjavaclass.defaultValue(), onerrorjavaclass));
                    }
                    else
                    {
                    	onerrorjavaclass._setValue(null);
                    }
                    onerrorjavaclass.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( onerrorpar.haveDefaultValue()) 
                {
                	if(onerrorpar.defaultValue() != null)
                	{
                    	onerrorpar._setValue(boConvertUtils.convertToString(onerrorpar.defaultValue(), onerrorpar));
                    }
                    else
                    {
                    	onerrorpar._setValue(null);
                    }
                    onerrorpar.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( performer.haveDefaultValue()) 
                {
                	if(performer.defaultValue() != null)
                	{
                    	performer._setValue(boConvertUtils.convertToBigDecimal(performer.defaultValue(), performer));
                    }
                    else
                    {
                    	performer._setValue(null);
                    }
                    performer.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( startdate.haveDefaultValue()) 
                {
                	if(startdate.defaultValue() != null)
                	{
                    	startdate._setValue(boConvertUtils.convertToTimestamp(startdate.defaultValue(), startdate));
                    }
                    else
                    {
                    	startdate._setValue(null);
                    }
                    startdate.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( enddate.haveDefaultValue()) 
                {
                	if(enddate.defaultValue() != null)
                	{
                    	enddate._setValue(boConvertUtils.convertToTimestamp(enddate.defaultValue(), enddate));
                    }
                    else
                    {
                    	enddate._setValue(null);
                    }
                    enddate.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( lastruntime.haveDefaultValue()) 
                {
                	if(lastruntime.defaultValue() != null)
                	{
                    	lastruntime._setValue(boConvertUtils.convertToTimestamp(lastruntime.defaultValue(), lastruntime));
                    }
                    else
                    {
                    	lastruntime._setValue(null);
                    }
                    lastruntime.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( nextruntime.haveDefaultValue()) 
                {
                	if(nextruntime.defaultValue() != null)
                	{
                    	nextruntime._setValue(boConvertUtils.convertToTimestamp(nextruntime.defaultValue(), nextruntime));
                    }
                    else
                    {
                    	nextruntime._setValue(null);
                    }
                    nextruntime.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( lastresultcode.haveDefaultValue()) 
                {
                	if(lastresultcode.defaultValue() != null)
                	{
                    	lastresultcode._setValue(boConvertUtils.convertToString(lastresultcode.defaultValue(), lastresultcode));
                    }
                    else
                    {
                    	lastresultcode._setValue(null);
                    }
                    lastresultcode.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( errormessage.haveDefaultValue()) 
                {
                	if(errormessage.defaultValue() != null)
                	{
                    	errormessage._setValue(boConvertUtils.convertToString(errormessage.defaultValue(), errormessage));
                    }
                    else
                    {
                    	errormessage._setValue(null);
                    }
                    errormessage.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( executiontime.haveDefaultValue()) 
                {
                	if(executiontime.defaultValue() != null)
                	{
                    	executiontime._setValue(boConvertUtils.convertToBigDecimal(executiontime.defaultValue(), executiontime));
                    }
                    else
                    {
                    	executiontime._setValue(null);
                    }
                    executiontime.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( timeout.haveDefaultValue()) 
                {
                	if(timeout.defaultValue() != null)
                	{
                    	timeout._setValue(boConvertUtils.convertToBigDecimal(timeout.defaultValue(), timeout));
                    }
                    else
                    {
                    	timeout._setValue(null);
                    }
                    timeout.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( lastid.haveDefaultValue()) 
                {
                	if(lastid.defaultValue() != null)
                	{
                    	lastid._setValue(boConvertUtils.convertToString(lastid.defaultValue(), lastid));
                    }
                    else
                    {
                    	lastid._setValue(null);
                    }
                    lastid.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( state.haveDefaultValue()) 
                {
                	if(state.defaultValue() != null)
                	{
                    	state._setValue(boConvertUtils.convertToBigDecimal(state.defaultValue(), state));
                    }
                    else
                    {
                    	state._setValue(null);
                    }
                    state.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
    
            if("Ebo_Schedule".equals(this.getName()))
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
   
    
   
    

   
    public final class Handlerinterval extends AttributeHandler {
        private boolean isdbbinding=true;
        private BigDecimal fieldvalue;
        public Handlerinterval(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("interval"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(Handlerinterval.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getBigDecimal("INTERVAL");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "interval"))
                    {
                       getParent().setCalculated(table, "interval");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "interval"))
                    {
                        getParent().setCalculated(table, "interval");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("interval"))
                    {
                        getParent().setCalculated(table, "interval"); 
                    }
                    else
                    {
                        getParent().clear(table, "interval"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("interval"))
                    {
                        getParent().setWaiting(table, "interval");
                        getParent().calculateFormula(new Hashtable(table), "interval");
                        //getParent().setCalculated(table, "interval");
                    }                    
                }
            }
            public void setValue(BigDecimal  newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }
            
            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlerinterval.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    BigDecimal chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
    
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL))
											{
	                        _setValue(newval);
				        	setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	                        
	                        if( recalc && getParent().onChangeSubmit("interval"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "interval");
	                            getParent().calculateFormula(table, "interval");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("interval");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
											}
                    }
                }
            }
            
            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlerinterval.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    BigDecimal chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(AttributeHandler.INPUT_FROM_INTERNAL))
											{
                        _setValue(newval);
                        setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                        
                        if(getParent().onChangeSubmit("interval"))
                        {
                            getParent().setCalculated(table, "interval");
                            getParent().calculateFormula(table, "interval");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("interval");
						}

                        fireAfterChangeEvent( this, chgval, newval);
//                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                    }
									}
                }
            }
            
            public void _setValue(BigDecimal newval) throws boRuntimeException {
                if(isdbbinding) {
                    this.getParent().getDataRow().updateBigDecimal("INTERVAL",newval);
                    getParent().setChanged(true);
                    super.setValid();
                } else {
                    fieldvalue = newval;
                }
            }
        
        public void setValueString(String value)  throws boRuntimeException 
        {
            
                this.setValue(boConvertUtils.convertToBigDecimal(value,this));
            
        }

        public void setValueObject(Object value) throws boRuntimeException {
            
                this.setValue((BigDecimal)value);
            
        }
        public Object getValueObject() throws boRuntimeException 
        {
            return this.getValue();
        }

        //@IF SIMPLEDATATYPE
            
            public long getValueLong() throws boRuntimeException {
                return boConvertUtils.convertTolong(this.getValue(),this);
            }
            public void setValue(long value)  throws boRuntimeException 
            {
                this.setValue(boConvertUtils.convertToBigDecimal(value,this));
            }
            public void setValuelong(long value)  throws boRuntimeException 
            {
                this.setValue(boConvertUtils.convertToBigDecimal(value,this));
            }
            
        //@ENDIF SIMPLEDATATYPE

        
public  boolean hiddenWhen() throws boRuntimeException {String tipoAg=getAttribute("type").getValueString();
if (tipoAg!=null && tipoAg.equals("REPEATLY")) return false;
else return true;}
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
    }
    
    public final class Handlertype extends AttributeHandler {
        private boolean isdbbinding=true;
        private String fieldvalue;
        public Handlertype(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("type"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public String getValue() throws boRuntimeException {
                String ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(Handlertype.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getString("TYPE");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "type"))
                    {
                       getParent().setCalculated(table, "type");
                       setValue(boConvertUtils.convertToString(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "type"))
                    {
                        getParent().setCalculated(table, "type");
                        setValue(boConvertUtils.convertToString(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("type"))
                    {
                        getParent().setCalculated(table, "type"); 
                    }
                    else
                    {
                        getParent().clear(table, "type"); 
                        setValue(boConvertUtils.convertToString(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToString(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("type"))
                    {
                        getParent().setWaiting(table, "type");
                        getParent().calculateFormula(new Hashtable(table), "type");
                        //getParent().setCalculated(table, "type");
                    }                    
                }
            }
            public void setValue(String  newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }
            
            public void setValue(String newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlertype.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    String chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
    
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL))
											{
	                        _setValue(newval);
				        	setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	                        
	                        if( recalc && getParent().onChangeSubmit("type"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "type");
	                            getParent().calculateFormula(table, "type");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("type");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
											}
                    }
                }
            }
            
            private void setValue(String newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlertype.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    String chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(AttributeHandler.INPUT_FROM_INTERNAL))
											{
                        _setValue(newval);
                        setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                        
                        if(getParent().onChangeSubmit("type"))
                        {
                            getParent().setCalculated(table, "type");
                            getParent().calculateFormula(table, "type");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("type");
						}

                        fireAfterChangeEvent( this, chgval, newval);
//                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                    }
									}
                }
            }
            
            public void _setValue(String newval) throws boRuntimeException {
                if(isdbbinding) {
                    this.getParent().getDataRow().updateString("TYPE",newval);
                    getParent().setChanged(true);
                    super.setValid();
                } else {
                    fieldvalue = newval;
                }
            }
        
        public void setValueString(String value)  throws boRuntimeException 
        {
            
                this.setValue(boConvertUtils.convertToString(value,this));
            
        }

        public void setValueObject(Object value) throws boRuntimeException {
            
                this.setValue((String)value);
            
        }
        public Object getValueObject() throws boRuntimeException 
        {
            return this.getValue();
        }

        // SIMPLEDATATYPE

        
public  String defaultValue() throws boRuntimeException {
try {
return "REPEATLY";
}
catch (Exception e){}
return null;
}
public  boolean haveDefaultValue() {
return true;
}
public  String formula() throws boRuntimeException {
return null;
}



        
    }
    
    public final class Handlerevery extends AttributeHandler {
        private boolean isdbbinding=true;
        private String fieldvalue;
        public Handlerevery(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("every"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public String getValue() throws boRuntimeException {
                String ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(Handlerevery.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getString("EVERY");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "every"))
                    {
                       getParent().setCalculated(table, "every");
                       setValue(boConvertUtils.convertToString(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "every"))
                    {
                        getParent().setCalculated(table, "every");
                        setValue(boConvertUtils.convertToString(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("every"))
                    {
                        getParent().setCalculated(table, "every"); 
                    }
                    else
                    {
                        getParent().clear(table, "every"); 
                        setValue(boConvertUtils.convertToString(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToString(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("every"))
                    {
                        getParent().setWaiting(table, "every");
                        getParent().calculateFormula(new Hashtable(table), "every");
                        //getParent().setCalculated(table, "every");
                    }                    
                }
            }
            public void setValue(String  newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }
            
            public void setValue(String newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlerevery.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    String chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
    
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL))
											{
	                        _setValue(newval);
				        	setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	                        
	                        if( recalc && getParent().onChangeSubmit("every"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "every");
	                            getParent().calculateFormula(table, "every");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("every");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
											}
                    }
                }
            }
            
            private void setValue(String newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlerevery.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    String chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(AttributeHandler.INPUT_FROM_INTERNAL))
											{
                        _setValue(newval);
                        setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                        
                        if(getParent().onChangeSubmit("every"))
                        {
                            getParent().setCalculated(table, "every");
                            getParent().calculateFormula(table, "every");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("every");
						}

                        fireAfterChangeEvent( this, chgval, newval);
//                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                    }
									}
                }
            }
            
            public void _setValue(String newval) throws boRuntimeException {
                if(isdbbinding) {
                    this.getParent().getDataRow().updateString("EVERY",newval);
                    getParent().setChanged(true);
                    super.setValid();
                } else {
                    fieldvalue = newval;
                }
            }
        
        public void setValueString(String value)  throws boRuntimeException 
        {
            
                this.setValue(boConvertUtils.convertToString(value,this));
            
        }

        public void setValueObject(Object value) throws boRuntimeException {
            
                this.setValue((String)value);
            
        }
        public Object getValueObject() throws boRuntimeException 
        {
            return this.getValue();
        }

        // SIMPLEDATATYPE

        
public  boolean hiddenWhen() throws boRuntimeException {String tipoAg=getAttribute("type").getValueString();
if (tipoAg==null  || tipoAg.equals("") || !tipoAg.equals("DAILY")) return true;
else return false;}
public  String defaultValue() throws boRuntimeException {
try {
return "ALLDAYS";
}
catch (Exception e){}
return null;
}
public  boolean haveDefaultValue() {
return true;
}
public  String formula() throws boRuntimeException {
return null;
}



        
    }
    
    public final class Handlereveryweek extends AttributeHandler {
        private boolean isdbbinding=true;
        private String fieldvalue;
        public Handlereveryweek(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("everyweek"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public String getValue() throws boRuntimeException {
                String ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(Handlereveryweek.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getString("EVERYWEEK");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "everyweek"))
                    {
                       getParent().setCalculated(table, "everyweek");
                       setValue(boConvertUtils.convertToString(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "everyweek"))
                    {
                        getParent().setCalculated(table, "everyweek");
                        setValue(boConvertUtils.convertToString(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("everyweek"))
                    {
                        getParent().setCalculated(table, "everyweek"); 
                    }
                    else
                    {
                        getParent().clear(table, "everyweek"); 
                        setValue(boConvertUtils.convertToString(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToString(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("everyweek"))
                    {
                        getParent().setWaiting(table, "everyweek");
                        getParent().calculateFormula(new Hashtable(table), "everyweek");
                        //getParent().setCalculated(table, "everyweek");
                    }                    
                }
            }
            public void setValue(String  newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }
            
            public void setValue(String newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlereveryweek.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    String chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
    
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL))
											{
	                        _setValue(newval);
				        	setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	                        
	                        if( recalc && getParent().onChangeSubmit("everyweek"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "everyweek");
	                            getParent().calculateFormula(table, "everyweek");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("everyweek");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
											}
                    }
                }
            }
            
            private void setValue(String newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlereveryweek.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    String chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(AttributeHandler.INPUT_FROM_INTERNAL))
											{
                        _setValue(newval);
                        setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                        
                        if(getParent().onChangeSubmit("everyweek"))
                        {
                            getParent().setCalculated(table, "everyweek");
                            getParent().calculateFormula(table, "everyweek");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("everyweek");
						}

                        fireAfterChangeEvent( this, chgval, newval);
//                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                    }
									}
                }
            }
            
            public void _setValue(String newval) throws boRuntimeException {
                if(isdbbinding) {
                    this.getParent().getDataRow().updateString("EVERYWEEK",newval);
                    getParent().setChanged(true);
                    super.setValid();
                } else {
                    fieldvalue = newval;
                }
            }
        
        public void setValueString(String value)  throws boRuntimeException 
        {
            
                this.setValue(boConvertUtils.convertToString(value,this));
            
        }

        public void setValueObject(Object value) throws boRuntimeException {
            
                this.setValue((String)value);
            
        }
        public Object getValueObject() throws boRuntimeException 
        {
            return this.getValue();
        }

        // SIMPLEDATATYPE

        
public  boolean hiddenWhen() throws boRuntimeException {String tipoAg=getAttribute("type").getValueString();
if (tipoAg==null  || tipoAg.equals("") || !tipoAg.equals("WEEKLY")) return true;
else return false;}
public  String defaultValue() throws boRuntimeException {
try {
return "MO";
}
catch (Exception e){}
return null;
}
public  boolean haveDefaultValue() {
return true;
}
public  String formula() throws boRuntimeException {
return null;
}



        
    }
    
    public final class Handlereverymonth extends AttributeHandler {
        private boolean isdbbinding=true;
        private String fieldvalue;
        public Handlereverymonth(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("everymonth"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public String getValue() throws boRuntimeException {
                String ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(Handlereverymonth.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getString("EVERYMONTH");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "everymonth"))
                    {
                       getParent().setCalculated(table, "everymonth");
                       setValue(boConvertUtils.convertToString(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "everymonth"))
                    {
                        getParent().setCalculated(table, "everymonth");
                        setValue(boConvertUtils.convertToString(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("everymonth"))
                    {
                        getParent().setCalculated(table, "everymonth"); 
                    }
                    else
                    {
                        getParent().clear(table, "everymonth"); 
                        setValue(boConvertUtils.convertToString(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToString(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("everymonth"))
                    {
                        getParent().setWaiting(table, "everymonth");
                        getParent().calculateFormula(new Hashtable(table), "everymonth");
                        //getParent().setCalculated(table, "everymonth");
                    }                    
                }
            }
            public void setValue(String  newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }
            
            public void setValue(String newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlereverymonth.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    String chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
    
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL))
											{
	                        _setValue(newval);
				        	setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	                        
	                        if( recalc && getParent().onChangeSubmit("everymonth"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "everymonth");
	                            getParent().calculateFormula(table, "everymonth");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("everymonth");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
											}
                    }
                }
            }
            
            private void setValue(String newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlereverymonth.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    String chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(AttributeHandler.INPUT_FROM_INTERNAL))
											{
                        _setValue(newval);
                        setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                        
                        if(getParent().onChangeSubmit("everymonth"))
                        {
                            getParent().setCalculated(table, "everymonth");
                            getParent().calculateFormula(table, "everymonth");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("everymonth");
						}

                        fireAfterChangeEvent( this, chgval, newval);
//                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                    }
									}
                }
            }
            
            public void _setValue(String newval) throws boRuntimeException {
                if(isdbbinding) {
                    this.getParent().getDataRow().updateString("EVERYMONTH",newval);
                    getParent().setChanged(true);
                    super.setValid();
                } else {
                    fieldvalue = newval;
                }
            }
        
        public void setValueString(String value)  throws boRuntimeException 
        {
            
                this.setValue(boConvertUtils.convertToString(value,this));
            
        }

        public void setValueObject(Object value) throws boRuntimeException {
            
                this.setValue((String)value);
            
        }
        public Object getValueObject() throws boRuntimeException 
        {
            return this.getValue();
        }

        // SIMPLEDATATYPE

        
public  boolean hiddenWhen() throws boRuntimeException {String tipoAg=getAttribute("type").getValueString();
if (tipoAg==null  || tipoAg.equals("") || !tipoAg.equals("MONTHLY")) return true;
else return false;}
public  String defaultValue() throws boRuntimeException {
try {
return "1";
}
catch (Exception e){}
return null;
}
public  boolean haveDefaultValue() {
return true;
}
public  String formula() throws boRuntimeException {
return null;
}



        
    }
    
    public final class Handlerwhen extends AttributeHandler {
        private boolean isdbbinding=true;
        private String fieldvalue;
        public Handlerwhen(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("when"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public String getValue() throws boRuntimeException {
                String ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(Handlerwhen.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getString("WHEN");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "when"))
                    {
                       getParent().setCalculated(table, "when");
                       setValue(boConvertUtils.convertToString(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "when"))
                    {
                        getParent().setCalculated(table, "when");
                        setValue(boConvertUtils.convertToString(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("when"))
                    {
                        getParent().setCalculated(table, "when"); 
                    }
                    else
                    {
                        getParent().clear(table, "when"); 
                        setValue(boConvertUtils.convertToString(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToString(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("when"))
                    {
                        getParent().setWaiting(table, "when");
                        getParent().calculateFormula(new Hashtable(table), "when");
                        //getParent().setCalculated(table, "when");
                    }                    
                }
            }
            public void setValue(String  newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }
            
            public void setValue(String newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlerwhen.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    String chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
    
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL))
											{
	                        _setValue(newval);
				        	setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	                        
	                        if( recalc && getParent().onChangeSubmit("when"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "when");
	                            getParent().calculateFormula(table, "when");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("when");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
											}
                    }
                }
            }
            
            private void setValue(String newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(Handlerwhen.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    String chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(AttributeHandler.INPUT_FROM_INTERNAL))
											{
                        _setValue(newval);
                        setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                        
                        if(getParent().onChangeSubmit("when"))
                        {
                            getParent().setCalculated(table, "when");
                            getParent().calculateFormula(table, "when");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("when");
						}

                        fireAfterChangeEvent( this, chgval, newval);
//                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                    }
									}
                }
            }
            
            public void _setValue(String newval) throws boRuntimeException {
                if(isdbbinding) {
                    this.getParent().getDataRow().updateString("WHEN",newval);
                    getParent().setChanged(true);
                    super.setValid();
                } else {
                    fieldvalue = newval;
                }
            }
        
        public void setValueString(String value)  throws boRuntimeException 
        {
            
                this.setValue(boConvertUtils.convertToString(value,this));
            
        }

        public void setValueObject(Object value) throws boRuntimeException {
            
                this.setValue((String)value);
            
        }
        public Object getValueObject() throws boRuntimeException 
        {
            return this.getValue();
        }

        // SIMPLEDATATYPE

        
public  boolean validate() throws boRuntimeException {String tipoAg=getAttribute("type").getValueString();
if (tipoAg!=null && tipoAg.equals("REPEATLY")) return true;
else 
{
	try
	{
	  boolean ret=true;
	  String pwhen=this.getValueString();
	  if (pwhen!=null && !pwhen.equals(""))
	  {
		if (pwhen.indexOf(":")>-1)
		{
		   String vec[]=pwhen.split(":");
		   if (vec.length==2)
		   {
		     int h=new Integer(vec[0]).intValue();
			 int m=new Integer(vec[1]).intValue();
		   }
		   else ret=false;
		}
		else ret=false;
	  }
	  else ret=false;
	  if (!ret) addErrorMessage(this,"Formato Inválido");
	  return ret;
	}
	catch(Exception e)
	{
		return false;
	}
}}
public  boolean hiddenWhen() throws boRuntimeException {String tipoAg=getAttribute("type").getValueString();
if (tipoAg!=null && tipoAg.equals("REPEATLY")) return true;
else return false;}
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
    }
    
    public final class HandleractiveStatus extends AttributeHandler {
        private boolean isdbbinding=true;
        private BigDecimal fieldvalue;
        public HandleractiveStatus(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("activeStatus"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(HandleractiveStatus.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getBigDecimal("ACTIVESTATUS");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "activeStatus"))
                    {
                       getParent().setCalculated(table, "activeStatus");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "activeStatus"))
                    {
                        getParent().setCalculated(table, "activeStatus");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("activeStatus"))
                    {
                        getParent().setCalculated(table, "activeStatus"); 
                    }
                    else
                    {
                        getParent().clear(table, "activeStatus"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("activeStatus"))
                    {
                        getParent().setWaiting(table, "activeStatus");
                        getParent().calculateFormula(new Hashtable(table), "activeStatus");
                        //getParent().setCalculated(table, "activeStatus");
                    }                    
                }
            }
            public void setValue(BigDecimal  newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }
            
            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(HandleractiveStatus.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    BigDecimal chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
    
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL))
											{
	                        _setValue(newval);
				        	setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	                        
	                        if( recalc && getParent().onChangeSubmit("activeStatus"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "activeStatus");
	                            getParent().calculateFormula(table, "activeStatus");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("activeStatus");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
											}
                    }
                }
            }
            
            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(HandleractiveStatus.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    BigDecimal chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(AttributeHandler.INPUT_FROM_INTERNAL))
											{
                        _setValue(newval);
                        setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                        
                        if(getParent().onChangeSubmit("activeStatus"))
                        {
                            getParent().setCalculated(table, "activeStatus");
                            getParent().calculateFormula(table, "activeStatus");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("activeStatus");
						}

                        fireAfterChangeEvent( this, chgval, newval);
//                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                    }
									}
                }
            }
            
            public void _setValue(BigDecimal newval) throws boRuntimeException {
                if(isdbbinding) {
                    this.getParent().getDataRow().updateBigDecimal("ACTIVESTATUS",newval);
                    getParent().setChanged(true);
                    super.setValid();
                } else {
                    fieldvalue = newval;
                }
            }
        
        public void setValueString(String value)  throws boRuntimeException 
        {
            
                this.setValue(boConvertUtils.convertToBigDecimal(value,this));
            
        }

        public void setValueObject(Object value) throws boRuntimeException {
            
                this.setValue((BigDecimal)value);
            
        }
        public Object getValueObject() throws boRuntimeException 
        {
            return this.getValue();
        }

        //@IF SIMPLEDATATYPE
            
            public double getValueDouble() throws boRuntimeException {
                return boConvertUtils.convertTodouble(this.getValue(),this);
            }
            public void setValue(double value)  throws boRuntimeException 
            {
                this.setValue(boConvertUtils.convertToBigDecimal(value,this));
            }
            public void setValuedouble(double value)  throws boRuntimeException 
            {
                this.setValue(boConvertUtils.convertToBigDecimal(value,this));
            }
            
        //@ENDIF SIMPLEDATATYPE

        
public  String defaultValue() throws boRuntimeException {
try {
return String.valueOf(0);
}
catch (Exception e){}
return null;
}
public  boolean haveDefaultValue() {
return true;
}
public  String formula() throws boRuntimeException {
return null;
}



        
    }
    

   
        
    
	public void update() throws boRuntimeException {
	
				
				if( this.activeStatus.getValueString() == null )
				{
					this.activeStatus.setValueString("0");
				}
				super.update();
				
	}
	public void runNow() throws boRuntimeException {
	      
			try {
          		this.getAttribute("activeStatus").setValueString("1");
          		this.update();
				ThreadGroup xeoGroup = new ThreadGroup("XEO Agents");          netgest.bo.runtime.robots.boScheduleThread activity = new netgest.bo.runtime.robots.boScheduleThread(xeoGroup,  "XEO Schedule Thread for "+this.getAttribute("description").getValueString()+"["+this.getBoui()+"]", this.getEboContext().getApplication() );
          		activity.setSchedule( this );                        
          		activity.start();
      		} catch (Exception e) {
            	e.printStackTrace();
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
return new String[] {"type"} ;
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
