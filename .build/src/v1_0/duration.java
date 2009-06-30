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



public  class duration extends boObject implements  Serializable {  

   
   
    public HandlerworkDate workDate;    
   
    public netgest.bo.runtime.attributes.boAttributeNumber duration;    
    public netgest.bo.runtime.attributes.boAttributeString journal;    
    public netgest.bo.runtime.attributes.boAttributeObject performer;    
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
     
    public duration() {
        super();
        bo_version      = "1.0";
        bo_name         = "duration";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("duration");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
       

       
        atts.add(workDate = new HandlerworkDate(this));
        
        
       
        atts.add(duration = new netgest.bo.runtime.attributes.boAttributeNumber(this,"duration"));
        
        atts.add(journal = new netgest.bo.runtime.attributes.boAttributeString(this,"journal"));
        
        atts.add(performer = new netgest.bo.runtime.attributes.boAttributeObject(this,"performer"));
        
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
           
           
                
                    if( workDate.haveDefaultValue()) 
                    {
                    	if(workDate.defaultValue() != null)
                    	{
                        	workDate._setValue(boConvertUtils.convertToTimestamp(workDate.defaultValue(), workDate));
                        }
                        else
                        {
                        	workDate._setValue(null);
                        }
                        workDate.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
           
                if( duration.haveDefaultValue()) 
                {
                	if(duration.defaultValue() != null)
                	{
                    	duration._setValue(boConvertUtils.convertToBigDecimal(duration.defaultValue(), duration));
                    }
                    else
                    {
                    	duration._setValue(null);
                    }
                    duration.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( journal.haveDefaultValue()) 
                {
                	if(journal.defaultValue() != null)
                	{
                    	journal._setValue(boConvertUtils.convertToString(journal.defaultValue(), journal));
                    }
                    else
                    {
                    	journal._setValue(null);
                    }
                    journal.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
    
            if("duration".equals(this.getName()))
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
   
    
   
    

   
    public final class HandlerworkDate extends AttributeHandler {
        private boolean isdbbinding=true;
        private Timestamp fieldvalue;
        public HandlerworkDate(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("workDate"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public Timestamp getValue() throws boRuntimeException {
                Timestamp ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(HandlerworkDate.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getTimestamp("WORKDATE");
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
                    if(getParent().isWaiting(table, "workDate"))
                    {
                       getParent().setCalculated(table, "workDate");
                       setValue(boConvertUtils.convertToTimestamp(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "workDate"))
                    {
                        getParent().setCalculated(table, "workDate");
                        setValue(boConvertUtils.convertToTimestamp(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("workDate"))
                    {
                        getParent().setCalculated(table, "workDate"); 
                    }
                    else
                    {
                        getParent().clear(table, "workDate"); 
                        setValue(boConvertUtils.convertToTimestamp(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToTimestamp(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("workDate"))
                    {
                        getParent().setWaiting(table, "workDate");
                        getParent().calculateFormula(new Hashtable(table), "workDate");
                        //getParent().setCalculated(table, "workDate");
                    }                    
                }
            }
            public void setValue(Timestamp  newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }
            
            public void setValue(Timestamp newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(HandlerworkDate.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    Timestamp chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
    
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL))
											{
	                        _setValue(newval);
				        	setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	                        
	                        if( recalc && getParent().onChangeSubmit("workDate"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "workDate");
	                            getParent().calculateFormula(table, "workDate");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("workDate");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
											}
                    }
                }
            }
            
            private void setValue(Timestamp newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights()) 
	            	throw new boRuntimeException(HandlerworkDate.class.getName() +
	        	".setValue()", "BO-3230", null, "");                      	                   	                                 
                if(!ClassUtils.compare(newval,this.getValue())) {
                    Timestamp chgval = this.getValue();
                    
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));
                    
                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
											if(canAlter(AttributeHandler.INPUT_FROM_INTERNAL))
											{
                        _setValue(newval);
                        setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                        
                        if(getParent().onChangeSubmit("workDate"))
                        {
                            getParent().setCalculated(table, "workDate");
                            getParent().calculateFormula(table, "workDate");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("workDate");
						}

                        fireAfterChangeEvent( this, chgval, newval);
//                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                    }
									}
                }
            }
            
            public void _setValue(Timestamp newval) throws boRuntimeException {
                if(isdbbinding) {
                    this.getParent().getDataRow().updateTimestamp("WORKDATE",newval);
                    getParent().setChanged(true);
                    super.setValid();
                } else {
                    fieldvalue = newval;
                }
            }
        
        public void setValueString(String value)  throws boRuntimeException 
        {
            
                this.setValue(boConvertUtils.convertToTimestamp(value,this));
            
        }

        public void setValueObject(Object value) throws boRuntimeException {
            
                this.setValue((Timestamp)value);
            
        }
        public Object getValueObject() throws boRuntimeException 
        {
            return this.getValue();
        }

        //@IF SIMPLEDATATYPE
            
            public Date getValueDate() throws boRuntimeException {
                return boConvertUtils.convertToDate(this.getValue(),this);
            }
            public void setValue(Date value)  throws boRuntimeException 
            {
                this.setValue(boConvertUtils.convertToTimestamp(value,this));
            }
            public void setValueDate(Date value)  throws boRuntimeException 
            {
                this.setValue(boConvertUtils.convertToTimestamp(value,this));
            }
            
        //@ENDIF SIMPLEDATATYPE

        
public  boolean disableWhen() throws boRuntimeException {if( this.getParent().exists() ) return true; else return false;}
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
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
