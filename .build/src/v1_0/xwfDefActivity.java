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



public  class xwfDefActivity extends boObject implements  Serializable {  

   
    public HandlerPARENT PARENT;       
   
   
    public netgest.bo.runtime.attributes.boAttributeString PRIVATE;    
    public netgest.bo.runtime.attributes.boAttributeString DELETED;    
    public netgest.bo.runtime.attributes.boAttributeObject program;    
    public netgest.bo.runtime.attributes.boAttributeObject fromActivity;    
    public netgest.bo.runtime.attributes.boAttributeObject toActivity;    
    public netgest.bo.runtime.attributes.boAttributeObject process;    
    public netgest.bo.runtime.attributes.boAttributeString optional;    
    public netgest.bo.runtime.attributes.boAttributeObject assignedQueue;    
    public netgest.bo.runtime.attributes.boAttributeObject to;    
    public netgest.bo.runtime.attributes.boAttributeObject performer;    
    public netgest.bo.runtime.attributes.boAttributeString name;    
    public netgest.bo.runtime.attributes.boAttributeString label;    
    public netgest.bo.runtime.attributes.boAttributeString description;    
    public netgest.bo.runtime.attributes.boAttributeNumber priority;    
    public netgest.bo.runtime.attributes.boAttributeString async;    
    public netgest.bo.runtime.attributes.boAttributeString type;    
    public netgest.bo.runtime.attributes.boAttributeString filter;    
    public netgest.bo.runtime.attributes.boAttributeString mode;    
    public netgest.bo.runtime.attributes.boAttributeString toReturn;    
    public netgest.bo.runtime.attributes.boAttributeObject reportType;    
    public netgest.bo.runtime.attributes.boAttributeString notified;    
    public netgest.bo.runtime.attributes.boAttributeDate beginDate;    
    public netgest.bo.runtime.attributes.boAttributeString setDuration;    
    public netgest.bo.runtime.attributes.boAttributeNumber duration;    
    public netgest.bo.runtime.attributes.boAttributeString setUtilDays;    
    public netgest.bo.runtime.attributes.boAttributeNumber utilDays;    
    public netgest.bo.runtime.attributes.boAttributeString setEndDate;    
    public netgest.bo.runtime.attributes.boAttributeDate endDate;    
    public netgest.bo.runtime.attributes.boAttributeString type_recursive;    
    public netgest.bo.runtime.attributes.boAttributeDate vig_beginDate;    
    public netgest.bo.runtime.attributes.boAttributeString vig_setDuration;    
    public netgest.bo.runtime.attributes.boAttributeNumber vig_duration;    
    public netgest.bo.runtime.attributes.boAttributeString vig_setUtilDays;    
    public netgest.bo.runtime.attributes.boAttributeNumber vig_utilDays;    
    public netgest.bo.runtime.attributes.boAttributeString vig_setOccur;    
    public netgest.bo.runtime.attributes.boAttributeNumber vig_occur;    
    public netgest.bo.runtime.attributes.boAttributeString vig_setEndDate;    
    public netgest.bo.runtime.attributes.boAttributeDate vig_endDate;    
    public netgest.bo.runtime.attributes.boAttributeString no_utilDays;    
    public netgest.bo.runtime.attributes.boAttributeString period;    
    public netgest.bo.runtime.attributes.boAttributeString hour_setPeriod;    
    public netgest.bo.runtime.attributes.boAttributeNumber hour_period;    
    public netgest.bo.runtime.attributes.boAttributeString hour_setNext;    
    public netgest.bo.runtime.attributes.boAttributeNumber hour_next;    
    public netgest.bo.runtime.attributes.boAttributeString daily_setPeriod;    
    public netgest.bo.runtime.attributes.boAttributeNumber daily_period;    
    public netgest.bo.runtime.attributes.boAttributeString daily_setNext;    
    public netgest.bo.runtime.attributes.boAttributeNumber daily_next;    
    public netgest.bo.runtime.attributes.boAttributeString weekly_setPeriod;    
    public netgest.bo.runtime.attributes.boAttributeString weekly_monday;    
    public netgest.bo.runtime.attributes.boAttributeString weekly_tuesday;    
    public netgest.bo.runtime.attributes.boAttributeString weekly_wednesday;    
    public netgest.bo.runtime.attributes.boAttributeString weekly_thursday;    
    public netgest.bo.runtime.attributes.boAttributeString weekly_friday;    
    public netgest.bo.runtime.attributes.boAttributeString weekly_saturday;    
    public netgest.bo.runtime.attributes.boAttributeString weekly_sunday;    
    public netgest.bo.runtime.attributes.boAttributeNumber weekly_period;    
    public netgest.bo.runtime.attributes.boAttributeString weekly_setNext;    
    public netgest.bo.runtime.attributes.boAttributeNumber weekly_next;    
    public netgest.bo.runtime.attributes.boAttributeString monthly_setPeriod;    
    public netgest.bo.runtime.attributes.boAttributeNumber monthly_day;    
    public netgest.bo.runtime.attributes.boAttributeString monthly_each;    
    public netgest.bo.runtime.attributes.boAttributeString monthly_setPeriod2;    
    public netgest.bo.runtime.attributes.boAttributeString monthly_l_day;    
    public netgest.bo.runtime.attributes.boAttributeNumber monthly_period_day;    
    public netgest.bo.runtime.attributes.boAttributeNumber monthly_period;    
    public netgest.bo.runtime.attributes.boAttributeString monthly_setNext;    
    public netgest.bo.runtime.attributes.boAttributeNumber monthly_next;    
    public netgest.bo.runtime.attributes.boAttributeString yearly_setPeriod;    
    public netgest.bo.runtime.attributes.boAttributeNumber yearly_day;    
    public netgest.bo.runtime.attributes.boAttributeString yearly_month;    
    public netgest.bo.runtime.attributes.boAttributeString yearly_setPeriod2;    
    public netgest.bo.runtime.attributes.boAttributeString yearly_each;    
    public netgest.bo.runtime.attributes.boAttributeString yearly_l_day;    
    public netgest.bo.runtime.attributes.boAttributeString yearly_setNext;    
    public netgest.bo.runtime.attributes.boAttributeNumber yearly_next;    
    public netgest.bo.runtime.attributes.boAttributeString taskReturn;    
    public netgest.bo.runtime.attributes.boAttributeObject taskReportType;    
    public netgest.bo.runtime.attributes.boAttributeString taskNotified;    
    public netgest.bo.runtime.attributes.boAttributeString forecastWorkDuration;    
    public netgest.bo.runtime.attributes.boAttributeString forecastTimeToComplete;    
    public netgest.bo.runtime.attributes.boAttributeObject defActivityOwner;    
    public netgest.bo.runtime.attributes.boAttributeString createdAll;    
    public netgest.bo.runtime.attributes.boAttributeDate lastruntime;    
    public netgest.bo.runtime.attributes.boAttributeDate nextruntime;    
    public netgest.bo.runtime.attributes.boAttributeString lastresultcode;    
    public netgest.bo.runtime.attributes.boAttributeString errormessage;    
    public netgest.bo.runtime.attributes.boAttributeNumber executiontime;    
    public netgest.bo.runtime.attributes.boAttributeNumber n_occur;    
    public netgest.bo.runtime.attributes.boAttributeString justification;    
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
    public HandleractiveStatus activeStatus;    
     
    public xwfDefActivity() {
        super();
        bo_version      = "1.0";
        bo_name         = "xwfDefActivity";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("xwfDefActivity");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        atts.add(PARENT = new HandlerPARENT(this));
        
        
        
        atts.add(new boBridgeMasterAttribute(this,this.getBoDefinition().getAttributeRef("KEYS")));  
        atts.add(new boBridgeMasterAttribute(this,this.getBoDefinition().getAttributeRef("KEYS_PERMISSIONS")));  
        atts.add(new boBridgeMasterAttribute(this,this.getBoDefinition().getAttributeRef("READLIST")));  
        atts.add(new boBridgeMasterAttribute(this,this.getBoDefinition().getAttributeRef("documents")));  
        atts.add(new boBridgeMasterAttribute(this,this.getBoDefinition().getAttributeRef("defActivityDepends"))); 

       
        
       
        atts.add(PRIVATE = new netgest.bo.runtime.attributes.boAttributeString(this,"PRIVATE"));
        
        atts.add(DELETED = new netgest.bo.runtime.attributes.boAttributeString(this,"DELETED"));
        
        atts.add(program = new netgest.bo.runtime.attributes.boAttributeObject(this,"program"));
        
        atts.add(fromActivity = new netgest.bo.runtime.attributes.boAttributeObject(this,"fromActivity"));
        
        atts.add(toActivity = new netgest.bo.runtime.attributes.boAttributeObject(this,"toActivity"));
        
        atts.add(process = new netgest.bo.runtime.attributes.boAttributeObject(this,"process"));
        
        atts.add(optional = new netgest.bo.runtime.attributes.boAttributeString(this,"optional"));
        
        atts.add(assignedQueue = new netgest.bo.runtime.attributes.boAttributeObject(this,"assignedQueue"));
        
        atts.add(to = new netgest.bo.runtime.attributes.boAttributeObject(this,"to"));
        
        atts.add(performer = new netgest.bo.runtime.attributes.boAttributeObject(this,"performer"));
        
        atts.add(name = new netgest.bo.runtime.attributes.boAttributeString(this,"name"));
        
        atts.add(label = new netgest.bo.runtime.attributes.boAttributeString(this,"label"));
        
        atts.add(description = new netgest.bo.runtime.attributes.boAttributeString(this,"description"));
        
        atts.add(priority = new netgest.bo.runtime.attributes.boAttributeNumber(this,"priority"));
        
        atts.add(async = new netgest.bo.runtime.attributes.boAttributeString(this,"async"));
        
        atts.add(type = new netgest.bo.runtime.attributes.boAttributeString(this,"type"));
        
        atts.add(filter = new netgest.bo.runtime.attributes.boAttributeString(this,"filter"));
        
        atts.add(mode = new netgest.bo.runtime.attributes.boAttributeString(this,"mode"));
        
        atts.add(toReturn = new netgest.bo.runtime.attributes.boAttributeString(this,"toReturn"));
        
        atts.add(reportType = new netgest.bo.runtime.attributes.boAttributeObject(this,"reportType"));
        
        atts.add(notified = new netgest.bo.runtime.attributes.boAttributeString(this,"notified"));
        
        atts.add(beginDate = new netgest.bo.runtime.attributes.boAttributeDate(this,"beginDate"));
        
        atts.add(setDuration = new netgest.bo.runtime.attributes.boAttributeString(this,"setDuration"));
        
        atts.add(duration = new netgest.bo.runtime.attributes.boAttributeNumber(this,"duration"));
        
        atts.add(setUtilDays = new netgest.bo.runtime.attributes.boAttributeString(this,"setUtilDays"));
        
        atts.add(utilDays = new netgest.bo.runtime.attributes.boAttributeNumber(this,"utilDays"));
        
        atts.add(setEndDate = new netgest.bo.runtime.attributes.boAttributeString(this,"setEndDate"));
        
        atts.add(endDate = new netgest.bo.runtime.attributes.boAttributeDate(this,"endDate"));
        
        atts.add(type_recursive = new netgest.bo.runtime.attributes.boAttributeString(this,"type_recursive"));
        
        atts.add(vig_beginDate = new netgest.bo.runtime.attributes.boAttributeDate(this,"vig_beginDate"));
        
        atts.add(vig_setDuration = new netgest.bo.runtime.attributes.boAttributeString(this,"vig_setDuration"));
        
        atts.add(vig_duration = new netgest.bo.runtime.attributes.boAttributeNumber(this,"vig_duration"));
        
        atts.add(vig_setUtilDays = new netgest.bo.runtime.attributes.boAttributeString(this,"vig_setUtilDays"));
        
        atts.add(vig_utilDays = new netgest.bo.runtime.attributes.boAttributeNumber(this,"vig_utilDays"));
        
        atts.add(vig_setOccur = new netgest.bo.runtime.attributes.boAttributeString(this,"vig_setOccur"));
        
        atts.add(vig_occur = new netgest.bo.runtime.attributes.boAttributeNumber(this,"vig_occur"));
        
        atts.add(vig_setEndDate = new netgest.bo.runtime.attributes.boAttributeString(this,"vig_setEndDate"));
        
        atts.add(vig_endDate = new netgest.bo.runtime.attributes.boAttributeDate(this,"vig_endDate"));
        
        atts.add(no_utilDays = new netgest.bo.runtime.attributes.boAttributeString(this,"no_utilDays"));
        
        atts.add(period = new netgest.bo.runtime.attributes.boAttributeString(this,"period"));
        
        atts.add(hour_setPeriod = new netgest.bo.runtime.attributes.boAttributeString(this,"hour_setPeriod"));
        
        atts.add(hour_period = new netgest.bo.runtime.attributes.boAttributeNumber(this,"hour_period"));
        
        atts.add(hour_setNext = new netgest.bo.runtime.attributes.boAttributeString(this,"hour_setNext"));
        
        atts.add(hour_next = new netgest.bo.runtime.attributes.boAttributeNumber(this,"hour_next"));
        
        atts.add(daily_setPeriod = new netgest.bo.runtime.attributes.boAttributeString(this,"daily_setPeriod"));
        
        atts.add(daily_period = new netgest.bo.runtime.attributes.boAttributeNumber(this,"daily_period"));
        
        atts.add(daily_setNext = new netgest.bo.runtime.attributes.boAttributeString(this,"daily_setNext"));
        
        atts.add(daily_next = new netgest.bo.runtime.attributes.boAttributeNumber(this,"daily_next"));
        
        atts.add(weekly_setPeriod = new netgest.bo.runtime.attributes.boAttributeString(this,"weekly_setPeriod"));
        
        atts.add(weekly_monday = new netgest.bo.runtime.attributes.boAttributeString(this,"weekly_monday"));
        
        atts.add(weekly_tuesday = new netgest.bo.runtime.attributes.boAttributeString(this,"weekly_tuesday"));
        
        atts.add(weekly_wednesday = new netgest.bo.runtime.attributes.boAttributeString(this,"weekly_wednesday"));
        
        atts.add(weekly_thursday = new netgest.bo.runtime.attributes.boAttributeString(this,"weekly_thursday"));
        
        atts.add(weekly_friday = new netgest.bo.runtime.attributes.boAttributeString(this,"weekly_friday"));
        
        atts.add(weekly_saturday = new netgest.bo.runtime.attributes.boAttributeString(this,"weekly_saturday"));
        
        atts.add(weekly_sunday = new netgest.bo.runtime.attributes.boAttributeString(this,"weekly_sunday"));
        
        atts.add(weekly_period = new netgest.bo.runtime.attributes.boAttributeNumber(this,"weekly_period"));
        
        atts.add(weekly_setNext = new netgest.bo.runtime.attributes.boAttributeString(this,"weekly_setNext"));
        
        atts.add(weekly_next = new netgest.bo.runtime.attributes.boAttributeNumber(this,"weekly_next"));
        
        atts.add(monthly_setPeriod = new netgest.bo.runtime.attributes.boAttributeString(this,"monthly_setPeriod"));
        
        atts.add(monthly_day = new netgest.bo.runtime.attributes.boAttributeNumber(this,"monthly_day"));
        
        atts.add(monthly_each = new netgest.bo.runtime.attributes.boAttributeString(this,"monthly_each"));
        
        atts.add(monthly_setPeriod2 = new netgest.bo.runtime.attributes.boAttributeString(this,"monthly_setPeriod2"));
        
        atts.add(monthly_l_day = new netgest.bo.runtime.attributes.boAttributeString(this,"monthly_l_day"));
        
        atts.add(monthly_period_day = new netgest.bo.runtime.attributes.boAttributeNumber(this,"monthly_period_day"));
        
        atts.add(monthly_period = new netgest.bo.runtime.attributes.boAttributeNumber(this,"monthly_period"));
        
        atts.add(monthly_setNext = new netgest.bo.runtime.attributes.boAttributeString(this,"monthly_setNext"));
        
        atts.add(monthly_next = new netgest.bo.runtime.attributes.boAttributeNumber(this,"monthly_next"));
        
        atts.add(yearly_setPeriod = new netgest.bo.runtime.attributes.boAttributeString(this,"yearly_setPeriod"));
        
        atts.add(yearly_day = new netgest.bo.runtime.attributes.boAttributeNumber(this,"yearly_day"));
        
        atts.add(yearly_month = new netgest.bo.runtime.attributes.boAttributeString(this,"yearly_month"));
        
        atts.add(yearly_setPeriod2 = new netgest.bo.runtime.attributes.boAttributeString(this,"yearly_setPeriod2"));
        
        atts.add(yearly_each = new netgest.bo.runtime.attributes.boAttributeString(this,"yearly_each"));
        
        atts.add(yearly_l_day = new netgest.bo.runtime.attributes.boAttributeString(this,"yearly_l_day"));
        
        atts.add(yearly_setNext = new netgest.bo.runtime.attributes.boAttributeString(this,"yearly_setNext"));
        
        atts.add(yearly_next = new netgest.bo.runtime.attributes.boAttributeNumber(this,"yearly_next"));
        
        atts.add(taskReturn = new netgest.bo.runtime.attributes.boAttributeString(this,"taskReturn"));
        
        atts.add(taskReportType = new netgest.bo.runtime.attributes.boAttributeObject(this,"taskReportType"));
        
        atts.add(taskNotified = new netgest.bo.runtime.attributes.boAttributeString(this,"taskNotified"));
        
        atts.add(forecastWorkDuration = new netgest.bo.runtime.attributes.boAttributeString(this,"forecastWorkDuration"));
        
        atts.add(forecastTimeToComplete = new netgest.bo.runtime.attributes.boAttributeString(this,"forecastTimeToComplete"));
        
        atts.add(defActivityOwner = new netgest.bo.runtime.attributes.boAttributeObject(this,"defActivityOwner"));
        
        atts.add(createdAll = new netgest.bo.runtime.attributes.boAttributeString(this,"createdAll"));
        
        atts.add(lastruntime = new netgest.bo.runtime.attributes.boAttributeDate(this,"lastruntime"));
        
        atts.add(nextruntime = new netgest.bo.runtime.attributes.boAttributeDate(this,"nextruntime"));
        
        atts.add(lastresultcode = new netgest.bo.runtime.attributes.boAttributeString(this,"lastresultcode"));
        
        atts.add(errormessage = new netgest.bo.runtime.attributes.boAttributeString(this,"errormessage"));
        
        atts.add(executiontime = new netgest.bo.runtime.attributes.boAttributeNumber(this,"executiontime"));
        
        atts.add(n_occur = new netgest.bo.runtime.attributes.boAttributeNumber(this,"n_occur"));
        
        atts.add(justification = new netgest.bo.runtime.attributes.boAttributeString(this,"justification"));
        
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
        
        atts.add(activeStatus = new HandleractiveStatus(this));
        stat.add(activeStatus);
        
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
           
                
                    if( PARENT.haveDefaultValue()) 
                    {
                         PARENT._setValues(boConvertUtils.convertToArrayOfBigDecimal(PARENT.defaultValue(), PARENT));
                         PARENT.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
           
           
                if( PRIVATE.haveDefaultValue()) 
                {
                	if(PRIVATE.defaultValue() != null)
                	{
                    	PRIVATE._setValue(boConvertUtils.convertToString(PRIVATE.defaultValue(), PRIVATE));
                    }
                    else
                    {
                    	PRIVATE._setValue(null);
                    }
                    PRIVATE.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( DELETED.haveDefaultValue()) 
                {
                	if(DELETED.defaultValue() != null)
                	{
                    	DELETED._setValue(boConvertUtils.convertToString(DELETED.defaultValue(), DELETED));
                    }
                    else
                    {
                    	DELETED._setValue(null);
                    }
                    DELETED.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( program.haveDefaultValue()) 
                {
                	if(program.defaultValue() != null)
                	{
                    	program._setValue(boConvertUtils.convertToBigDecimal(program.defaultValue(), program));
                    }
                    else
                    {
                    	program._setValue(null);
                    }
                    program.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( fromActivity.haveDefaultValue()) 
                {
                	if(fromActivity.defaultValue() != null)
                	{
                    	fromActivity._setValue(boConvertUtils.convertToBigDecimal(fromActivity.defaultValue(), fromActivity));
                    }
                    else
                    {
                    	fromActivity._setValue(null);
                    }
                    fromActivity.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( toActivity.haveDefaultValue()) 
                {
                	if(toActivity.defaultValue() != null)
                	{
                    	toActivity._setValue(boConvertUtils.convertToBigDecimal(toActivity.defaultValue(), toActivity));
                    }
                    else
                    {
                    	toActivity._setValue(null);
                    }
                    toActivity.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( process.haveDefaultValue()) 
                {
                	if(process.defaultValue() != null)
                	{
                    	process._setValue(boConvertUtils.convertToBigDecimal(process.defaultValue(), process));
                    }
                    else
                    {
                    	process._setValue(null);
                    }
                    process.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( optional.haveDefaultValue()) 
                {
                	if(optional.defaultValue() != null)
                	{
                    	optional._setValue(boConvertUtils.convertToString(optional.defaultValue(), optional));
                    }
                    else
                    {
                    	optional._setValue(null);
                    }
                    optional.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( assignedQueue.haveDefaultValue()) 
                {
                	if(assignedQueue.defaultValue() != null)
                	{
                    	assignedQueue._setValue(boConvertUtils.convertToBigDecimal(assignedQueue.defaultValue(), assignedQueue));
                    }
                    else
                    {
                    	assignedQueue._setValue(null);
                    }
                    assignedQueue.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( to.haveDefaultValue()) 
                {
                	if(to.defaultValue() != null)
                	{
                    	to._setValue(boConvertUtils.convertToBigDecimal(to.defaultValue(), to));
                    }
                    else
                    {
                    	to._setValue(null);
                    }
                    to.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( priority.haveDefaultValue()) 
                {
                	if(priority.defaultValue() != null)
                	{
                    	priority._setValue(boConvertUtils.convertToBigDecimal(priority.defaultValue(), priority));
                    }
                    else
                    {
                    	priority._setValue(null);
                    }
                    priority.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( async.haveDefaultValue()) 
                {
                	if(async.defaultValue() != null)
                	{
                    	async._setValue(boConvertUtils.convertToString(async.defaultValue(), async));
                    }
                    else
                    {
                    	async._setValue(null);
                    }
                    async.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( toReturn.haveDefaultValue()) 
                {
                	if(toReturn.defaultValue() != null)
                	{
                    	toReturn._setValue(boConvertUtils.convertToString(toReturn.defaultValue(), toReturn));
                    }
                    else
                    {
                    	toReturn._setValue(null);
                    }
                    toReturn.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( reportType.haveDefaultValue()) 
                {
                	if(reportType.defaultValue() != null)
                	{
                    	reportType._setValue(boConvertUtils.convertToBigDecimal(reportType.defaultValue(), reportType));
                    }
                    else
                    {
                    	reportType._setValue(null);
                    }
                    reportType.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( notified.haveDefaultValue()) 
                {
                	if(notified.defaultValue() != null)
                	{
                    	notified._setValue(boConvertUtils.convertToString(notified.defaultValue(), notified));
                    }
                    else
                    {
                    	notified._setValue(null);
                    }
                    notified.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( setDuration.haveDefaultValue()) 
                {
                	if(setDuration.defaultValue() != null)
                	{
                    	setDuration._setValue(boConvertUtils.convertToString(setDuration.defaultValue(), setDuration));
                    }
                    else
                    {
                    	setDuration._setValue(null);
                    }
                    setDuration.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( setUtilDays.haveDefaultValue()) 
                {
                	if(setUtilDays.defaultValue() != null)
                	{
                    	setUtilDays._setValue(boConvertUtils.convertToString(setUtilDays.defaultValue(), setUtilDays));
                    }
                    else
                    {
                    	setUtilDays._setValue(null);
                    }
                    setUtilDays.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( utilDays.haveDefaultValue()) 
                {
                	if(utilDays.defaultValue() != null)
                	{
                    	utilDays._setValue(boConvertUtils.convertToBigDecimal(utilDays.defaultValue(), utilDays));
                    }
                    else
                    {
                    	utilDays._setValue(null);
                    }
                    utilDays.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( setEndDate.haveDefaultValue()) 
                {
                	if(setEndDate.defaultValue() != null)
                	{
                    	setEndDate._setValue(boConvertUtils.convertToString(setEndDate.defaultValue(), setEndDate));
                    }
                    else
                    {
                    	setEndDate._setValue(null);
                    }
                    setEndDate.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( endDate.haveDefaultValue()) 
                {
                	if(endDate.defaultValue() != null)
                	{
                    	endDate._setValue(boConvertUtils.convertToTimestamp(endDate.defaultValue(), endDate));
                    }
                    else
                    {
                    	endDate._setValue(null);
                    }
                    endDate.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( type_recursive.haveDefaultValue()) 
                {
                	if(type_recursive.defaultValue() != null)
                	{
                    	type_recursive._setValue(boConvertUtils.convertToString(type_recursive.defaultValue(), type_recursive));
                    }
                    else
                    {
                    	type_recursive._setValue(null);
                    }
                    type_recursive.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( vig_beginDate.haveDefaultValue()) 
                {
                	if(vig_beginDate.defaultValue() != null)
                	{
                    	vig_beginDate._setValue(boConvertUtils.convertToTimestamp(vig_beginDate.defaultValue(), vig_beginDate));
                    }
                    else
                    {
                    	vig_beginDate._setValue(null);
                    }
                    vig_beginDate.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( vig_setDuration.haveDefaultValue()) 
                {
                	if(vig_setDuration.defaultValue() != null)
                	{
                    	vig_setDuration._setValue(boConvertUtils.convertToString(vig_setDuration.defaultValue(), vig_setDuration));
                    }
                    else
                    {
                    	vig_setDuration._setValue(null);
                    }
                    vig_setDuration.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( vig_duration.haveDefaultValue()) 
                {
                	if(vig_duration.defaultValue() != null)
                	{
                    	vig_duration._setValue(boConvertUtils.convertToBigDecimal(vig_duration.defaultValue(), vig_duration));
                    }
                    else
                    {
                    	vig_duration._setValue(null);
                    }
                    vig_duration.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( vig_setUtilDays.haveDefaultValue()) 
                {
                	if(vig_setUtilDays.defaultValue() != null)
                	{
                    	vig_setUtilDays._setValue(boConvertUtils.convertToString(vig_setUtilDays.defaultValue(), vig_setUtilDays));
                    }
                    else
                    {
                    	vig_setUtilDays._setValue(null);
                    }
                    vig_setUtilDays.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( vig_utilDays.haveDefaultValue()) 
                {
                	if(vig_utilDays.defaultValue() != null)
                	{
                    	vig_utilDays._setValue(boConvertUtils.convertToBigDecimal(vig_utilDays.defaultValue(), vig_utilDays));
                    }
                    else
                    {
                    	vig_utilDays._setValue(null);
                    }
                    vig_utilDays.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( vig_setOccur.haveDefaultValue()) 
                {
                	if(vig_setOccur.defaultValue() != null)
                	{
                    	vig_setOccur._setValue(boConvertUtils.convertToString(vig_setOccur.defaultValue(), vig_setOccur));
                    }
                    else
                    {
                    	vig_setOccur._setValue(null);
                    }
                    vig_setOccur.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( vig_occur.haveDefaultValue()) 
                {
                	if(vig_occur.defaultValue() != null)
                	{
                    	vig_occur._setValue(boConvertUtils.convertToBigDecimal(vig_occur.defaultValue(), vig_occur));
                    }
                    else
                    {
                    	vig_occur._setValue(null);
                    }
                    vig_occur.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( vig_setEndDate.haveDefaultValue()) 
                {
                	if(vig_setEndDate.defaultValue() != null)
                	{
                    	vig_setEndDate._setValue(boConvertUtils.convertToString(vig_setEndDate.defaultValue(), vig_setEndDate));
                    }
                    else
                    {
                    	vig_setEndDate._setValue(null);
                    }
                    vig_setEndDate.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( vig_endDate.haveDefaultValue()) 
                {
                	if(vig_endDate.defaultValue() != null)
                	{
                    	vig_endDate._setValue(boConvertUtils.convertToTimestamp(vig_endDate.defaultValue(), vig_endDate));
                    }
                    else
                    {
                    	vig_endDate._setValue(null);
                    }
                    vig_endDate.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( no_utilDays.haveDefaultValue()) 
                {
                	if(no_utilDays.defaultValue() != null)
                	{
                    	no_utilDays._setValue(boConvertUtils.convertToString(no_utilDays.defaultValue(), no_utilDays));
                    }
                    else
                    {
                    	no_utilDays._setValue(null);
                    }
                    no_utilDays.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( period.haveDefaultValue()) 
                {
                	if(period.defaultValue() != null)
                	{
                    	period._setValue(boConvertUtils.convertToString(period.defaultValue(), period));
                    }
                    else
                    {
                    	period._setValue(null);
                    }
                    period.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( hour_setPeriod.haveDefaultValue()) 
                {
                	if(hour_setPeriod.defaultValue() != null)
                	{
                    	hour_setPeriod._setValue(boConvertUtils.convertToString(hour_setPeriod.defaultValue(), hour_setPeriod));
                    }
                    else
                    {
                    	hour_setPeriod._setValue(null);
                    }
                    hour_setPeriod.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( hour_period.haveDefaultValue()) 
                {
                	if(hour_period.defaultValue() != null)
                	{
                    	hour_period._setValue(boConvertUtils.convertToBigDecimal(hour_period.defaultValue(), hour_period));
                    }
                    else
                    {
                    	hour_period._setValue(null);
                    }
                    hour_period.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( hour_setNext.haveDefaultValue()) 
                {
                	if(hour_setNext.defaultValue() != null)
                	{
                    	hour_setNext._setValue(boConvertUtils.convertToString(hour_setNext.defaultValue(), hour_setNext));
                    }
                    else
                    {
                    	hour_setNext._setValue(null);
                    }
                    hour_setNext.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( hour_next.haveDefaultValue()) 
                {
                	if(hour_next.defaultValue() != null)
                	{
                    	hour_next._setValue(boConvertUtils.convertToBigDecimal(hour_next.defaultValue(), hour_next));
                    }
                    else
                    {
                    	hour_next._setValue(null);
                    }
                    hour_next.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( daily_setPeriod.haveDefaultValue()) 
                {
                	if(daily_setPeriod.defaultValue() != null)
                	{
                    	daily_setPeriod._setValue(boConvertUtils.convertToString(daily_setPeriod.defaultValue(), daily_setPeriod));
                    }
                    else
                    {
                    	daily_setPeriod._setValue(null);
                    }
                    daily_setPeriod.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( daily_period.haveDefaultValue()) 
                {
                	if(daily_period.defaultValue() != null)
                	{
                    	daily_period._setValue(boConvertUtils.convertToBigDecimal(daily_period.defaultValue(), daily_period));
                    }
                    else
                    {
                    	daily_period._setValue(null);
                    }
                    daily_period.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( daily_setNext.haveDefaultValue()) 
                {
                	if(daily_setNext.defaultValue() != null)
                	{
                    	daily_setNext._setValue(boConvertUtils.convertToString(daily_setNext.defaultValue(), daily_setNext));
                    }
                    else
                    {
                    	daily_setNext._setValue(null);
                    }
                    daily_setNext.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( daily_next.haveDefaultValue()) 
                {
                	if(daily_next.defaultValue() != null)
                	{
                    	daily_next._setValue(boConvertUtils.convertToBigDecimal(daily_next.defaultValue(), daily_next));
                    }
                    else
                    {
                    	daily_next._setValue(null);
                    }
                    daily_next.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( weekly_setPeriod.haveDefaultValue()) 
                {
                	if(weekly_setPeriod.defaultValue() != null)
                	{
                    	weekly_setPeriod._setValue(boConvertUtils.convertToString(weekly_setPeriod.defaultValue(), weekly_setPeriod));
                    }
                    else
                    {
                    	weekly_setPeriod._setValue(null);
                    }
                    weekly_setPeriod.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( weekly_monday.haveDefaultValue()) 
                {
                	if(weekly_monday.defaultValue() != null)
                	{
                    	weekly_monday._setValue(boConvertUtils.convertToString(weekly_monday.defaultValue(), weekly_monday));
                    }
                    else
                    {
                    	weekly_monday._setValue(null);
                    }
                    weekly_monday.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( weekly_tuesday.haveDefaultValue()) 
                {
                	if(weekly_tuesday.defaultValue() != null)
                	{
                    	weekly_tuesday._setValue(boConvertUtils.convertToString(weekly_tuesday.defaultValue(), weekly_tuesday));
                    }
                    else
                    {
                    	weekly_tuesday._setValue(null);
                    }
                    weekly_tuesday.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( weekly_wednesday.haveDefaultValue()) 
                {
                	if(weekly_wednesday.defaultValue() != null)
                	{
                    	weekly_wednesday._setValue(boConvertUtils.convertToString(weekly_wednesday.defaultValue(), weekly_wednesday));
                    }
                    else
                    {
                    	weekly_wednesday._setValue(null);
                    }
                    weekly_wednesday.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( weekly_thursday.haveDefaultValue()) 
                {
                	if(weekly_thursday.defaultValue() != null)
                	{
                    	weekly_thursday._setValue(boConvertUtils.convertToString(weekly_thursday.defaultValue(), weekly_thursday));
                    }
                    else
                    {
                    	weekly_thursday._setValue(null);
                    }
                    weekly_thursday.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( weekly_friday.haveDefaultValue()) 
                {
                	if(weekly_friday.defaultValue() != null)
                	{
                    	weekly_friday._setValue(boConvertUtils.convertToString(weekly_friday.defaultValue(), weekly_friday));
                    }
                    else
                    {
                    	weekly_friday._setValue(null);
                    }
                    weekly_friday.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( weekly_saturday.haveDefaultValue()) 
                {
                	if(weekly_saturday.defaultValue() != null)
                	{
                    	weekly_saturday._setValue(boConvertUtils.convertToString(weekly_saturday.defaultValue(), weekly_saturday));
                    }
                    else
                    {
                    	weekly_saturday._setValue(null);
                    }
                    weekly_saturday.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( weekly_sunday.haveDefaultValue()) 
                {
                	if(weekly_sunday.defaultValue() != null)
                	{
                    	weekly_sunday._setValue(boConvertUtils.convertToString(weekly_sunday.defaultValue(), weekly_sunday));
                    }
                    else
                    {
                    	weekly_sunday._setValue(null);
                    }
                    weekly_sunday.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( weekly_period.haveDefaultValue()) 
                {
                	if(weekly_period.defaultValue() != null)
                	{
                    	weekly_period._setValue(boConvertUtils.convertToBigDecimal(weekly_period.defaultValue(), weekly_period));
                    }
                    else
                    {
                    	weekly_period._setValue(null);
                    }
                    weekly_period.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( weekly_setNext.haveDefaultValue()) 
                {
                	if(weekly_setNext.defaultValue() != null)
                	{
                    	weekly_setNext._setValue(boConvertUtils.convertToString(weekly_setNext.defaultValue(), weekly_setNext));
                    }
                    else
                    {
                    	weekly_setNext._setValue(null);
                    }
                    weekly_setNext.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( weekly_next.haveDefaultValue()) 
                {
                	if(weekly_next.defaultValue() != null)
                	{
                    	weekly_next._setValue(boConvertUtils.convertToBigDecimal(weekly_next.defaultValue(), weekly_next));
                    }
                    else
                    {
                    	weekly_next._setValue(null);
                    }
                    weekly_next.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( monthly_setPeriod.haveDefaultValue()) 
                {
                	if(monthly_setPeriod.defaultValue() != null)
                	{
                    	monthly_setPeriod._setValue(boConvertUtils.convertToString(monthly_setPeriod.defaultValue(), monthly_setPeriod));
                    }
                    else
                    {
                    	monthly_setPeriod._setValue(null);
                    }
                    monthly_setPeriod.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( monthly_day.haveDefaultValue()) 
                {
                	if(monthly_day.defaultValue() != null)
                	{
                    	monthly_day._setValue(boConvertUtils.convertToBigDecimal(monthly_day.defaultValue(), monthly_day));
                    }
                    else
                    {
                    	monthly_day._setValue(null);
                    }
                    monthly_day.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( monthly_each.haveDefaultValue()) 
                {
                	if(monthly_each.defaultValue() != null)
                	{
                    	monthly_each._setValue(boConvertUtils.convertToString(monthly_each.defaultValue(), monthly_each));
                    }
                    else
                    {
                    	monthly_each._setValue(null);
                    }
                    monthly_each.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( monthly_setPeriod2.haveDefaultValue()) 
                {
                	if(monthly_setPeriod2.defaultValue() != null)
                	{
                    	monthly_setPeriod2._setValue(boConvertUtils.convertToString(monthly_setPeriod2.defaultValue(), monthly_setPeriod2));
                    }
                    else
                    {
                    	monthly_setPeriod2._setValue(null);
                    }
                    monthly_setPeriod2.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( monthly_l_day.haveDefaultValue()) 
                {
                	if(monthly_l_day.defaultValue() != null)
                	{
                    	monthly_l_day._setValue(boConvertUtils.convertToString(monthly_l_day.defaultValue(), monthly_l_day));
                    }
                    else
                    {
                    	monthly_l_day._setValue(null);
                    }
                    monthly_l_day.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( monthly_period_day.haveDefaultValue()) 
                {
                	if(monthly_period_day.defaultValue() != null)
                	{
                    	monthly_period_day._setValue(boConvertUtils.convertToBigDecimal(monthly_period_day.defaultValue(), monthly_period_day));
                    }
                    else
                    {
                    	monthly_period_day._setValue(null);
                    }
                    monthly_period_day.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( monthly_period.haveDefaultValue()) 
                {
                	if(monthly_period.defaultValue() != null)
                	{
                    	monthly_period._setValue(boConvertUtils.convertToBigDecimal(monthly_period.defaultValue(), monthly_period));
                    }
                    else
                    {
                    	monthly_period._setValue(null);
                    }
                    monthly_period.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( monthly_setNext.haveDefaultValue()) 
                {
                	if(monthly_setNext.defaultValue() != null)
                	{
                    	monthly_setNext._setValue(boConvertUtils.convertToString(monthly_setNext.defaultValue(), monthly_setNext));
                    }
                    else
                    {
                    	monthly_setNext._setValue(null);
                    }
                    monthly_setNext.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( monthly_next.haveDefaultValue()) 
                {
                	if(monthly_next.defaultValue() != null)
                	{
                    	monthly_next._setValue(boConvertUtils.convertToBigDecimal(monthly_next.defaultValue(), monthly_next));
                    }
                    else
                    {
                    	monthly_next._setValue(null);
                    }
                    monthly_next.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( yearly_setPeriod.haveDefaultValue()) 
                {
                	if(yearly_setPeriod.defaultValue() != null)
                	{
                    	yearly_setPeriod._setValue(boConvertUtils.convertToString(yearly_setPeriod.defaultValue(), yearly_setPeriod));
                    }
                    else
                    {
                    	yearly_setPeriod._setValue(null);
                    }
                    yearly_setPeriod.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( yearly_day.haveDefaultValue()) 
                {
                	if(yearly_day.defaultValue() != null)
                	{
                    	yearly_day._setValue(boConvertUtils.convertToBigDecimal(yearly_day.defaultValue(), yearly_day));
                    }
                    else
                    {
                    	yearly_day._setValue(null);
                    }
                    yearly_day.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( yearly_month.haveDefaultValue()) 
                {
                	if(yearly_month.defaultValue() != null)
                	{
                    	yearly_month._setValue(boConvertUtils.convertToString(yearly_month.defaultValue(), yearly_month));
                    }
                    else
                    {
                    	yearly_month._setValue(null);
                    }
                    yearly_month.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( yearly_setPeriod2.haveDefaultValue()) 
                {
                	if(yearly_setPeriod2.defaultValue() != null)
                	{
                    	yearly_setPeriod2._setValue(boConvertUtils.convertToString(yearly_setPeriod2.defaultValue(), yearly_setPeriod2));
                    }
                    else
                    {
                    	yearly_setPeriod2._setValue(null);
                    }
                    yearly_setPeriod2.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( yearly_each.haveDefaultValue()) 
                {
                	if(yearly_each.defaultValue() != null)
                	{
                    	yearly_each._setValue(boConvertUtils.convertToString(yearly_each.defaultValue(), yearly_each));
                    }
                    else
                    {
                    	yearly_each._setValue(null);
                    }
                    yearly_each.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( yearly_l_day.haveDefaultValue()) 
                {
                	if(yearly_l_day.defaultValue() != null)
                	{
                    	yearly_l_day._setValue(boConvertUtils.convertToString(yearly_l_day.defaultValue(), yearly_l_day));
                    }
                    else
                    {
                    	yearly_l_day._setValue(null);
                    }
                    yearly_l_day.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( yearly_setNext.haveDefaultValue()) 
                {
                	if(yearly_setNext.defaultValue() != null)
                	{
                    	yearly_setNext._setValue(boConvertUtils.convertToString(yearly_setNext.defaultValue(), yearly_setNext));
                    }
                    else
                    {
                    	yearly_setNext._setValue(null);
                    }
                    yearly_setNext.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( yearly_next.haveDefaultValue()) 
                {
                	if(yearly_next.defaultValue() != null)
                	{
                    	yearly_next._setValue(boConvertUtils.convertToBigDecimal(yearly_next.defaultValue(), yearly_next));
                    }
                    else
                    {
                    	yearly_next._setValue(null);
                    }
                    yearly_next.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( taskReturn.haveDefaultValue()) 
                {
                	if(taskReturn.defaultValue() != null)
                	{
                    	taskReturn._setValue(boConvertUtils.convertToString(taskReturn.defaultValue(), taskReturn));
                    }
                    else
                    {
                    	taskReturn._setValue(null);
                    }
                    taskReturn.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( taskReportType.haveDefaultValue()) 
                {
                	if(taskReportType.defaultValue() != null)
                	{
                    	taskReportType._setValue(boConvertUtils.convertToBigDecimal(taskReportType.defaultValue(), taskReportType));
                    }
                    else
                    {
                    	taskReportType._setValue(null);
                    }
                    taskReportType.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( taskNotified.haveDefaultValue()) 
                {
                	if(taskNotified.defaultValue() != null)
                	{
                    	taskNotified._setValue(boConvertUtils.convertToString(taskNotified.defaultValue(), taskNotified));
                    }
                    else
                    {
                    	taskNotified._setValue(null);
                    }
                    taskNotified.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( forecastWorkDuration.haveDefaultValue()) 
                {
                	if(forecastWorkDuration.defaultValue() != null)
                	{
                    	forecastWorkDuration._setValue(boConvertUtils.convertToString(forecastWorkDuration.defaultValue(), forecastWorkDuration));
                    }
                    else
                    {
                    	forecastWorkDuration._setValue(null);
                    }
                    forecastWorkDuration.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( forecastTimeToComplete.haveDefaultValue()) 
                {
                	if(forecastTimeToComplete.defaultValue() != null)
                	{
                    	forecastTimeToComplete._setValue(boConvertUtils.convertToString(forecastTimeToComplete.defaultValue(), forecastTimeToComplete));
                    }
                    else
                    {
                    	forecastTimeToComplete._setValue(null);
                    }
                    forecastTimeToComplete.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( defActivityOwner.haveDefaultValue()) 
                {
                	if(defActivityOwner.defaultValue() != null)
                	{
                    	defActivityOwner._setValue(boConvertUtils.convertToBigDecimal(defActivityOwner.defaultValue(), defActivityOwner));
                    }
                    else
                    {
                    	defActivityOwner._setValue(null);
                    }
                    defActivityOwner.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( createdAll.haveDefaultValue()) 
                {
                	if(createdAll.defaultValue() != null)
                	{
                    	createdAll._setValue(boConvertUtils.convertToString(createdAll.defaultValue(), createdAll));
                    }
                    else
                    {
                    	createdAll._setValue(null);
                    }
                    createdAll.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( n_occur.haveDefaultValue()) 
                {
                	if(n_occur.defaultValue() != null)
                	{
                    	n_occur._setValue(boConvertUtils.convertToBigDecimal(n_occur.defaultValue(), n_occur));
                    }
                    else
                    {
                    	n_occur._setValue(null);
                    }
                    n_occur.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
    
            if("xwfDefActivity".equals(this.getName()))
            {
                calculateFormula(null);
            }
        }
    }
   
    public bridgeHandler KEYS()
    {
        return getBridge("KEYS");        
    }
    
    public bridgeHandler KEYS_PERMISSIONS()
    {
        return getBridge("KEYS_PERMISSIONS");        
    }
    
    public bridgeHandler READLIST()
    {
        return getBridge("READLIST");        
    }
    
    public bridgeHandler documents()
    {
        return getBridge("documents");        
    }
    
    public bridgeHandler defActivityDepends()
    {
        return getBridge("defActivityDepends");        
    }
    
    
    public bridgeHandler getBridge( String bridgeName ) 
    {
        boBridgesArray    brig = super.getBridges();        	
        bridgeHandler     ret  = brig.get(bridgeName);
        if( ret == null )
        {
        
        	if( bridgeName.equals("KEYS") && (ret=brig.get("KEYS"))==null )
        	{
            	brig.add( ret=new BridgeHandlerKEYS(new DataResultSet( getDataRow().getRecordChild( getEboContext() ,"KEYS") ) , this ) );        
        	}
        	if( bridgeName.equals("KEYS_PERMISSIONS") && (ret=brig.get("KEYS_PERMISSIONS"))==null )
        	{
            	brig.add( ret=new BridgeHandlerKEYS_PERMISSIONS(new DataResultSet( getDataRow().getRecordChild( getEboContext() ,"KEYS_PERMISSIONS") ) , this ) );        
        	}
        	if( bridgeName.equals("READLIST") && (ret=brig.get("READLIST"))==null )
        	{
            	brig.add( ret=new BridgeHandlerREADLIST(new DataResultSet( getDataRow().getRecordChild( getEboContext() ,"READLIST") ) , this ) );        
        	}
        	if( bridgeName.equals("documents") && (ret=brig.get("documents"))==null )
        	{
            	brig.add( ret=new BridgeHandlerdocuments(new DataResultSet( getDataRow().getRecordChild( getEboContext() ,"documents") ) , this ) );        
        	}
        	if( bridgeName.equals("defActivityDepends") && (ret=brig.get("defActivityDepends"))==null )
        	{
            	brig.add( ret=new BridgeHandlerdefActivityDepends(new DataResultSet( getDataRow().getRecordChild( getEboContext() ,"defActivityDepends") ) , this ) );        
        	}
        	if( ret == null )
        	{
				ret = super.getBridge( bridgeName );
        	}
        }
        return ret;
    }
   
    public final class HandlerPARENT extends ObjAttHandler {
        public HandlerPARENT(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("PARENT"));
        }

        
            public BigDecimal[] getValues() throws boRuntimeException {
                try
                {
                    if (getParent().isCheckSecurity() && !this.hasRights()) 	                
                    	throw new boRuntimeException(HandlerPARENT.class.getName() +
	        	".getValues()", "BO-3230", null, "");                      	                   	
                    fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                    DataSet xdataSet =  getParent().getDataRow().getRecordChild( getEboContext(), "PARENT" );
                    DataResultSet xnode = new DataResultSet( xdataSet );
                    xnode.beforeFirst();
                    BigDecimal[] toRet = null;
                    if( xnode.getRowCount() > 0 )
                    {
                        toRet = new BigDecimal[ xnode.getRowCount() ];
                        while( xnode.next() )
                        {
                            toRet[ xnode.getRow() - 1 ] = xnode.getBigDecimal("PARENT$");
                        }
                    }
                    fireEvent( boEvent.EVENT_BEFORE_GETVALUE, toRet );
                    return toRet;
                } catch(SQLException e) {
                    String[] args = {getParent().getName(),"PARENT"};
                    throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
                }
            }
            public void setValues(BigDecimal[] newvalue) throws boRuntimeException 
            {
            	if (getParent().isCheckSecurity() && !this.hasRights()) 	          
            	      throw new boRuntimeException(HandlerPARENT.class.getName() +
	        	".setValues(BigDecimal[])", "BO-3230", null, "");                      	
                if(!ClassUtils.compare(newvalue,this.getValues())) {
                    BigDecimal[] chgval = this.getValues();
                    BigDecimal[] newval = newvalue;
        
//                    boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//                    if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
//                    if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));

                    if ( fireBeforeChangeEvent( this, chgval, newval) )
                    {
                        _setValues( newvalue );
                        setInputType(AttributeHandler.INPUT_FROM_USER);
                        getParent().setChanged(true);
                        
                        fireAfterChangeEvent( this, chgval, newval );
                        
//                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//                        if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                    }
                }
            }
            public void _setValues( BigDecimal[] newvalues  ) throws boRuntimeException 
            {
                try
                {
                    DataSet xdataSet =  getParent().getDataRow().getRecordChild( getEboContext(), "PARENT" );
                    DataResultSet xnode = new DataResultSet( xdataSet ); 
                    
                    if( newvalues == null || newvalues.length == 0 )
                    {
                        while( xnode.getRowCount() > 0 )
                        {
                            xnode.first();
                            xnode.deleteRow();
                        }
                    }
                    else
                    {
                        int i;
                        for (i = 0; i < newvalues.length ; i++) 
                        {
                            if( xnode.absolute( i + 1 ) )
                            {
                                if( !ClassUtils.compare(newvalues,this.getValues()) )
                                {
                                    xnode.updateLong( "T$PARENT$" , getParent().getBoui() );
                                    xnode.updateBigDecimal( "PARENT$" , newvalues[i] );
                                    xnode.updateRow();
                                }
                            }
                            else
                            {
                                xnode.moveToInsertRow();
                                xnode.updateBigDecimal( "PARENT$" , newvalues[i] );
                                xnode.updateLong( "T$PARENT$" , getParent().getBoui() );
                                xnode.insertRow();
                            }
                        }
                        for ( ; i < xdataSet.getRowCount() ; i++ )
                        {
                            xdataSet.deleteRow( i + 1 );
                        }
                        
                    }

					if( getParent().getBridges().get( "PARENT" )!=null )
					{
						getParent().getBridge("PARENT").refreshBridgeData();
					}
                }
                catch (SQLException e)
                {
                    String[] args = {getParent().getName() ,"PARENT"};
                    throw new boRuntimeException(boObject.class.getName()+"set(long)","BO-3002",e,args);
                }
                finally
                {
                    
                }
            }
            public void setValuesString(String[] value)  throws boRuntimeException 
            {
                this.setValues(boConvertUtils.convertToArrayOfBigDecimal(value,this));
            }
            public void setValueString(String value) {
                try {
                    this.setValues(boConvertUtils.convertToArrayOfBigDecimal(value,this));
                } catch (Exception e) {
                    super.setInvalid(e.getMessage(),value);
                }
            }
            public String getValueString() throws boRuntimeException {
                try {
                    return boConvertUtils.convertToString(getValues(),this);
                } catch (Exception e) {
                    String[] args = { getParent().getName() ,"PARENT"};
                    throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
                }
            }
            
        
        

        
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
        
    }
    
    
    
    public final class BridgeHandlerKEYS extends bridgeHandler {
        private DataResultSet p_node;
        private String        p_fatherfield;
        private String        p_childfield;
        private boObject      p_parent;

        
        public BridgeHandlerKEYS(DataResultSet data,boObject parent) {
            super("KEYS",
                  data,
                  parent.getBoDefinition().getAttributeRef("KEYS").getBridge().getChildFieldName(),
                  parent
                 );

            p_fatherfield = parent.getBoDefinition().getAttributeRef("KEYS").getBridge().getFatherFieldName();
            p_childfield  = parent.getBoDefinition().getAttributeRef("KEYS").getBridge().getChildFieldName();
            
            p_parent = parent;
            p_node = data;

			refreshBridgeData();
            first();
        }
        public void add(BigDecimal boui, byte type) throws boRuntimeException {
            add(boui,-1, type);
        }
        public void add(BigDecimal boui,int xnrow, byte type) throws boRuntimeException {

              //tranform ao adicionar a brige
              netgest.bo.transformers.Transformer transfClass = null;
			  if ( getParent().getMode() != boObject.MODE_EDIT_TEMPLATE )
				{
				  if(( transfClass = this.getDefAttribute().getTransformClassMap()) != null)
				  {
					 if(boui != null)
					 {
						boui = new BigDecimal(transfClass.transform(getEboContext(), getParent(), boui.longValue()));
					 }
				  }
				}

			  boBridgeRow brow = createRow();
			  ObjAttHandler att = (ObjAttHandler)brow.getAttribute( this.getName() );

			  BigDecimal newval = boui;

//			  boolean allow=att.onBeforeChange(new boEvent(att,att.EVENT_BEFORE_CHANGE, null ,newval));
//			  if( newval != null ) allow=allow && att.onBeforeAdd(new boEvent(att, att.EVENT_BEFORE_ADD,newval));
              
			  if( att.fireBeforeChangeEvent( att, null, newval) ) 
			  {
				  
				  brow.getDataRow().updateBigDecimal(p_childfield,boui);
  		          this.insertRow( brow );
				  this.moveTo( this.getRowCount() );

				  if(xnrow !=-1)
                  {
                      this.moveRowTo(xnrow);
                  }

				  att.checkParent( null, newval );

				  getParent().setChanged( true );

                  if(getParent().onChangeSubmit("KEYS"))
                  {
                      getParent().calculateFormula("KEYS");
                  }
                  att.fireAfterChangeEvent( att, null, newval );
//				  if( newval != null ) att.onAfterAdd(new boEvent( att ,att.EVENT_AFTER_ADD,newval));
//				  att.onAfterChange(new boEvent( att ,att.EVENT_AFTER_CHANGE, null ,newval));
			  }

        }

		public boolean remove() throws boRuntimeException 
        {
            boolean ret = false;

          

                boBridgeRow rowtodelete = rows( this.getRow() );
				ObjAttHandler att = (ObjAttHandler)rowtodelete.getAttribute( this.getName() );

				BigDecimal chgval = this.getValue();

	
//				boolean allow=att.onBeforeChange(new boEvent(att,att.EVENT_BEFORE_CHANGE,chgval,null));
//                allow = allow && att.onBeforeRemove(new boEvent(att,att.EVENT_BEFORE_REMOVE,chgval));

                if( att.fireBeforeChangeEvent( att, chgval, null) ) 
                {
					rowtodelete.getDataRow().updateBigDecimal(p_childfield,null);
					att.checkParent( chgval, null );

				    getParent().setChanged( true );

                    Enumeration  lineatts = rowtodelete.getLineAttributes().elements();

                    _remove( rowtodelete );  // remove the line

					
                    while( lineatts.hasMoreElements() )
                    {
                        AttributeHandler xatt = ( AttributeHandler )lineatts.nextElement();
                        if ( xatt.getValueObject() != null )
                        {
                            xatt.setValueObject( null );
                        }
                    }
                    
                    ret = true;
					//calculateFormulas
					if(getParent().onChangeSubmit("KEYS"))
					{
					   getParent().calculateFormula("KEYS");
					}

                    att.fireAfterChangeEvent( att, chgval, null);
//					att.onAfterRemove(new boEvent(att,att.EVENT_AFTER_REMOVE,chgval));
//					att.onAfterChange(new boEvent(att,att.EVENT_AFTER_CHANGE,chgval,null));
                    
				}
           
            return ret;
        }




		public boBridgeRow createRow( netgest.bo.data.DataRow row )
		{
			return new BridgeHanlderKEYSRow( this, row );
		}

		public final class BridgeHanlderKEYSRow extends boBridgeRow
		{
			public BridgeHanlderKEYSRow( bridgeHandler bridge, netgest.bo.data.DataRow row )
			{
				super( bridge , row );
				addLine();
			}

			public void addLine() 
			{
				boAttributesArray xatt = super.getLineAttributes();
			
				if(this.getAttribute("KEYS")==null)
					xatt.add( new HandlerKEYS( getParent(), this ) );
				
		
			
				if(this.getAttribute("securityCode")==null)
					xatt.add( new HandlersecurityCode( getParent(), this ) );
				
				if(this.getAttribute("LIN")==null)
					xatt.add( new HandlerLIN( getParent(), this ) );
				
			} 


		}


        
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
       
        public final class HandlerKEYS extends BridgeObjAttributeHandler {
            public HandlerKEYS( boObject parent , boBridgeRow bridgerow ) {
                super( parent , bridgerow , bridgerow.getBridge().getDefAttribute().getName().equals("KEYS")?parent.getBoDefinition().getAttributeRef("KEYS"):bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("KEYS") );
            }
            public void setValueString(String value) {
                try {
                    this.setValue(boConvertUtils.convertToBigDecimal(value,this));
                    if(getParent().onChangeSubmit("KEYS"))
                    {
                        Hashtable table = new Hashtable();                    
                        getParent().setCalculated(table, "KEYS");
                        getParent().calculateFormula(table, "KEYS");
                    }
                } catch (Exception e) {
                    super.setInvalid(e.getMessage(),value);
                }
            }
            public String getValueString() throws boRuntimeException {
                try {
                    return boConvertUtils.convertToString(this.getValue(),this);
                } catch (Exception e) {
                    String[] args = {getParent().getName(),"KEYS"};
                    throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
                }
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "KEYS"))
                    {
                       getParent().setCalculated(table, "KEYS");
                       this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "KEYS"))
                    {
                        getParent().setCalculated(table, "KEYS");
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("KEYS"))
                    {
                        getParent().setCalculated(table, "KEYS"); 
                    }
                    else
                    {
                        getParent().clear(table, "KEYS"); 
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                }
                else
                {   
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    if(getParent().onChangeSubmit("KEYS"))
                    {                 
                        getParent().setWaiting(table, "KEYS");
                        getParent().calculateFormula(new Hashtable(table), "KEYS");
                        //getParent().setCalculated(table, "KEYS");
                    }                    
                }
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                this.setValue(newval, recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL);
	        	
	        	//setInputType( recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	        	
                if( recalc && getParent().onChangeSubmit("KEYS"))
                {
                    Hashtable table = new Hashtable();
                    getParent().setCalculated(table, "KEYS");
                    getParent().calculateFormula(table, "KEYS");
                }
        
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("KEYS");
				}
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                this.setValue(newval, AttributeHandler.INPUT_FROM_INTERNAL);
                //setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                if( getParent().onChangeSubmit("KEYS"))
                {
                    getParent().setCalculated(table, "KEYS");
                    getParent().calculateFormula(table, "KEYS");
                }
                
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("KEYS");
				}

            }
            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        

       
        public final class HandlersecurityCode extends BridgeAttributeHandler {

            private boolean isdbbinding=true;
            private BigDecimal fieldvalue;
            private boBridgeRow p_bridgerow;

			public HandlersecurityCode(boObject parent, boBridgeRow bridgerow ) {
                super( parent , bridgerow ,bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("securityCode") );
                p_bridgerow = bridgerow;
            }
            
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
					ret=p_bridgerow.getDataRow().getBigDecimal("SECURITYCODE");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            public void setValueObject(Object value) throws boRuntimeException {
                this.setValue((BigDecimal)value);
            }
            public Object getValueObject() throws boRuntimeException 
            {
                return this.getValue();
            }
            public String getValueString() throws boRuntimeException {
                return boConvertUtils.convertToString(this.getValue(),this);
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "securityCode"))
                    {
                       getParent().setCalculated(table, "securityCode");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "securityCode"))
                    {
                        getParent().setCalculated(table, "securityCode");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("securityCode"))
                    {
                        getParent().setCalculated(table, "securityCode"); 
                    }
                    else
                    {
                        getParent().clear(table, "securityCode"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                
                    if(getParent().onChangeSubmit("securityCode"))
                    {
                        getParent().setWaiting(table, "securityCode");
                        getParent().calculateFormula(new Hashtable(table), "securityCode");
                        //getParent().setCalculated(table, "securityCode");
                    }                    
                }
            }

            public void setValue(BigDecimal newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlersecurityCode.class.getName() +
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
	                    	                        
                                if( recalc && getParent().onChangeSubmit("securityCode"))
                                {
                                    Hashtable table = new Hashtable();
                                    getParent().setCalculated(table, "securityCode");
                                    getParent().calculateFormula(table, "securityCode");
                                }
	                        
                                //vou verificar se o objecto se encontra numa bridge
                                if ( getParent().p_parentBridgeRow != null )
                                {
                                    getParent().getParentBridgeRow().getBridge().lineChanged("securityCode");
                                }

                                fireAfterChangeEvent( this, chgval, newval);
//                                if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                                if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//        	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                            }
                    }
                }
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlersecurityCode.class.getName() +
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
	                        if( getParent().onChangeSubmit("securityCode"))
	                        {
	                            getParent().setCalculated(table, "securityCode");
	                            getParent().calculateFormula(table, "securityCode");
	                        }

							//vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("securityCode");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//							if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
	                    }
	                }
							}
            }

            public void _setValue(BigDecimal newval) throws boRuntimeException {
                if(isdbbinding) {

                        p_bridgerow.getDataRow().updateBigDecimal("SECURITYCODE",newval);
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

            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        
        public final class HandlerLIN extends BridgeAttributeHandler {

            private boolean isdbbinding=true;
            private BigDecimal fieldvalue;
            private boBridgeRow p_bridgerow;

			public HandlerLIN(boObject parent, boBridgeRow bridgerow ) {
                super( parent , bridgerow ,bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("LIN") );
                p_bridgerow = bridgerow;
            }
            
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
					ret=p_bridgerow.getDataRow().getBigDecimal("LIN");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            public void setValueObject(Object value) throws boRuntimeException {
                this.setValue((BigDecimal)value);
            }
            public Object getValueObject() throws boRuntimeException 
            {
                return this.getValue();
            }
            public String getValueString() throws boRuntimeException {
                return boConvertUtils.convertToString(this.getValue(),this);
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "LIN"))
                    {
                       getParent().setCalculated(table, "LIN");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "LIN"))
                    {
                        getParent().setCalculated(table, "LIN");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("LIN"))
                    {
                        getParent().setCalculated(table, "LIN"); 
                    }
                    else
                    {
                        getParent().clear(table, "LIN"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                
                    if(getParent().onChangeSubmit("LIN"))
                    {
                        getParent().setWaiting(table, "LIN");
                        getParent().calculateFormula(new Hashtable(table), "LIN");
                        //getParent().setCalculated(table, "LIN");
                    }                    
                }
            }

            public void setValue(BigDecimal newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlerLIN.class.getName() +
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
	                    	                        
                                if( recalc && getParent().onChangeSubmit("LIN"))
                                {
                                    Hashtable table = new Hashtable();
                                    getParent().setCalculated(table, "LIN");
                                    getParent().calculateFormula(table, "LIN");
                                }
	                        
                                //vou verificar se o objecto se encontra numa bridge
                                if ( getParent().p_parentBridgeRow != null )
                                {
                                    getParent().getParentBridgeRow().getBridge().lineChanged("LIN");
                                }

                                fireAfterChangeEvent( this, chgval, newval);
//                                if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                                if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//        	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                            }
                    }
                }
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlerLIN.class.getName() +
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
	                        if( getParent().onChangeSubmit("LIN"))
	                        {
	                            getParent().setCalculated(table, "LIN");
	                            getParent().calculateFormula(table, "LIN");
	                        }

							//vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("LIN");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//							if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
	                    }
	                }
							}
            }

            public void _setValue(BigDecimal newval) throws boRuntimeException {
                if(isdbbinding) {

                        p_bridgerow.getDataRow().updateBigDecimal("LIN",newval);
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

            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        
    } 
     
    public final class BridgeHandlerKEYS_PERMISSIONS extends bridgeHandler {
        private DataResultSet p_node;
        private String        p_fatherfield;
        private String        p_childfield;
        private boObject      p_parent;

        
        public BridgeHandlerKEYS_PERMISSIONS(DataResultSet data,boObject parent) {
            super("KEYS_PERMISSIONS",
                  data,
                  parent.getBoDefinition().getAttributeRef("KEYS_PERMISSIONS").getBridge().getChildFieldName(),
                  parent
                 );

            p_fatherfield = parent.getBoDefinition().getAttributeRef("KEYS_PERMISSIONS").getBridge().getFatherFieldName();
            p_childfield  = parent.getBoDefinition().getAttributeRef("KEYS_PERMISSIONS").getBridge().getChildFieldName();
            
            p_parent = parent;
            p_node = data;

			refreshBridgeData();
            first();
        }
        public void add(BigDecimal boui, byte type) throws boRuntimeException {
            add(boui,-1, type);
        }
        public void add(BigDecimal boui,int xnrow, byte type) throws boRuntimeException {

              //tranform ao adicionar a brige
              netgest.bo.transformers.Transformer transfClass = null;
			  if ( getParent().getMode() != boObject.MODE_EDIT_TEMPLATE )
				{
				  if(( transfClass = this.getDefAttribute().getTransformClassMap()) != null)
				  {
					 if(boui != null)
					 {
						boui = new BigDecimal(transfClass.transform(getEboContext(), getParent(), boui.longValue()));
					 }
				  }
				}

			  boBridgeRow brow = createRow();
			  ObjAttHandler att = (ObjAttHandler)brow.getAttribute( this.getName() );

			  BigDecimal newval = boui;

//			  boolean allow=att.onBeforeChange(new boEvent(att,att.EVENT_BEFORE_CHANGE, null ,newval));
//			  if( newval != null ) allow=allow && att.onBeforeAdd(new boEvent(att, att.EVENT_BEFORE_ADD,newval));
              
			  if( att.fireBeforeChangeEvent( att, null, newval) ) 
			  {
				  
				  brow.getDataRow().updateBigDecimal(p_childfield,boui);
  		          this.insertRow( brow );
				  this.moveTo( this.getRowCount() );

				  if(xnrow !=-1)
                  {
                      this.moveRowTo(xnrow);
                  }

				  att.checkParent( null, newval );

				  getParent().setChanged( true );

                  if(getParent().onChangeSubmit("KEYS_PERMISSIONS"))
                  {
                      getParent().calculateFormula("KEYS_PERMISSIONS");
                  }
                  att.fireAfterChangeEvent( att, null, newval );
//				  if( newval != null ) att.onAfterAdd(new boEvent( att ,att.EVENT_AFTER_ADD,newval));
//				  att.onAfterChange(new boEvent( att ,att.EVENT_AFTER_CHANGE, null ,newval));
			  }

        }

		public boolean remove() throws boRuntimeException 
        {
            boolean ret = false;

          

                boBridgeRow rowtodelete = rows( this.getRow() );
				ObjAttHandler att = (ObjAttHandler)rowtodelete.getAttribute( this.getName() );

				BigDecimal chgval = this.getValue();

	
//				boolean allow=att.onBeforeChange(new boEvent(att,att.EVENT_BEFORE_CHANGE,chgval,null));
//                allow = allow && att.onBeforeRemove(new boEvent(att,att.EVENT_BEFORE_REMOVE,chgval));

                if( att.fireBeforeChangeEvent( att, chgval, null) ) 
                {
					rowtodelete.getDataRow().updateBigDecimal(p_childfield,null);
					att.checkParent( chgval, null );

				    getParent().setChanged( true );

                    Enumeration  lineatts = rowtodelete.getLineAttributes().elements();

                    _remove( rowtodelete );  // remove the line

					
                    while( lineatts.hasMoreElements() )
                    {
                        AttributeHandler xatt = ( AttributeHandler )lineatts.nextElement();
                        if ( xatt.getValueObject() != null )
                        {
                            xatt.setValueObject( null );
                        }
                    }
                    
                    ret = true;
					//calculateFormulas
					if(getParent().onChangeSubmit("KEYS_PERMISSIONS"))
					{
					   getParent().calculateFormula("KEYS_PERMISSIONS");
					}

                    att.fireAfterChangeEvent( att, chgval, null);
//					att.onAfterRemove(new boEvent(att,att.EVENT_AFTER_REMOVE,chgval));
//					att.onAfterChange(new boEvent(att,att.EVENT_AFTER_CHANGE,chgval,null));
                    
				}
           
            return ret;
        }




		public boBridgeRow createRow( netgest.bo.data.DataRow row )
		{
			return new BridgeHanlderKEYS_PERMISSIONSRow( this, row );
		}

		public final class BridgeHanlderKEYS_PERMISSIONSRow extends boBridgeRow
		{
			public BridgeHanlderKEYS_PERMISSIONSRow( bridgeHandler bridge, netgest.bo.data.DataRow row )
			{
				super( bridge , row );
				addLine();
			}

			public void addLine() 
			{
				boAttributesArray xatt = super.getLineAttributes();
			
				if(this.getAttribute("KEYS_PERMISSIONS")==null)
					xatt.add( new HandlerKEYS_PERMISSIONS( getParent(), this ) );
				
		
			
				if(this.getAttribute("securityCode")==null)
					xatt.add( new HandlersecurityCode( getParent(), this ) );
				
				if(this.getAttribute("LIN")==null)
					xatt.add( new HandlerLIN( getParent(), this ) );
				
			} 


		}


        
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
       
        public final class HandlerKEYS_PERMISSIONS extends BridgeObjAttributeHandler {
            public HandlerKEYS_PERMISSIONS( boObject parent , boBridgeRow bridgerow ) {
                super( parent , bridgerow , bridgerow.getBridge().getDefAttribute().getName().equals("KEYS_PERMISSIONS")?parent.getBoDefinition().getAttributeRef("KEYS_PERMISSIONS"):bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("KEYS_PERMISSIONS") );
            }
            public void setValueString(String value) {
                try {
                    this.setValue(boConvertUtils.convertToBigDecimal(value,this));
                    if(getParent().onChangeSubmit("KEYS_PERMISSIONS"))
                    {
                        Hashtable table = new Hashtable();                    
                        getParent().setCalculated(table, "KEYS_PERMISSIONS");
                        getParent().calculateFormula(table, "KEYS_PERMISSIONS");
                    }
                } catch (Exception e) {
                    super.setInvalid(e.getMessage(),value);
                }
            }
            public String getValueString() throws boRuntimeException {
                try {
                    return boConvertUtils.convertToString(this.getValue(),this);
                } catch (Exception e) {
                    String[] args = {getParent().getName(),"KEYS_PERMISSIONS"};
                    throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
                }
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "KEYS_PERMISSIONS"))
                    {
                       getParent().setCalculated(table, "KEYS_PERMISSIONS");
                       this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "KEYS_PERMISSIONS"))
                    {
                        getParent().setCalculated(table, "KEYS_PERMISSIONS");
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("KEYS_PERMISSIONS"))
                    {
                        getParent().setCalculated(table, "KEYS_PERMISSIONS"); 
                    }
                    else
                    {
                        getParent().clear(table, "KEYS_PERMISSIONS"); 
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                }
                else
                {   
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    if(getParent().onChangeSubmit("KEYS_PERMISSIONS"))
                    {                 
                        getParent().setWaiting(table, "KEYS_PERMISSIONS");
                        getParent().calculateFormula(new Hashtable(table), "KEYS_PERMISSIONS");
                        //getParent().setCalculated(table, "KEYS_PERMISSIONS");
                    }                    
                }
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                this.setValue(newval, recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL);
	        	
	        	//setInputType( recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	        	
                if( recalc && getParent().onChangeSubmit("KEYS_PERMISSIONS"))
                {
                    Hashtable table = new Hashtable();
                    getParent().setCalculated(table, "KEYS_PERMISSIONS");
                    getParent().calculateFormula(table, "KEYS_PERMISSIONS");
                }
        
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("KEYS_PERMISSIONS");
				}
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                this.setValue(newval, AttributeHandler.INPUT_FROM_INTERNAL);
                //setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                if( getParent().onChangeSubmit("KEYS_PERMISSIONS"))
                {
                    getParent().setCalculated(table, "KEYS_PERMISSIONS");
                    getParent().calculateFormula(table, "KEYS_PERMISSIONS");
                }
                
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("KEYS_PERMISSIONS");
				}

            }
            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        

       
        public final class HandlersecurityCode extends BridgeAttributeHandler {

            private boolean isdbbinding=true;
            private BigDecimal fieldvalue;
            private boBridgeRow p_bridgerow;

			public HandlersecurityCode(boObject parent, boBridgeRow bridgerow ) {
                super( parent , bridgerow ,bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("securityCode") );
                p_bridgerow = bridgerow;
            }
            
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
					ret=p_bridgerow.getDataRow().getBigDecimal("SECURITYCODE");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            public void setValueObject(Object value) throws boRuntimeException {
                this.setValue((BigDecimal)value);
            }
            public Object getValueObject() throws boRuntimeException 
            {
                return this.getValue();
            }
            public String getValueString() throws boRuntimeException {
                return boConvertUtils.convertToString(this.getValue(),this);
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "securityCode"))
                    {
                       getParent().setCalculated(table, "securityCode");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "securityCode"))
                    {
                        getParent().setCalculated(table, "securityCode");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("securityCode"))
                    {
                        getParent().setCalculated(table, "securityCode"); 
                    }
                    else
                    {
                        getParent().clear(table, "securityCode"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                
                    if(getParent().onChangeSubmit("securityCode"))
                    {
                        getParent().setWaiting(table, "securityCode");
                        getParent().calculateFormula(new Hashtable(table), "securityCode");
                        //getParent().setCalculated(table, "securityCode");
                    }                    
                }
            }

            public void setValue(BigDecimal newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlersecurityCode.class.getName() +
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
	                    	                        
                                if( recalc && getParent().onChangeSubmit("securityCode"))
                                {
                                    Hashtable table = new Hashtable();
                                    getParent().setCalculated(table, "securityCode");
                                    getParent().calculateFormula(table, "securityCode");
                                }
	                        
                                //vou verificar se o objecto se encontra numa bridge
                                if ( getParent().p_parentBridgeRow != null )
                                {
                                    getParent().getParentBridgeRow().getBridge().lineChanged("securityCode");
                                }

                                fireAfterChangeEvent( this, chgval, newval);
//                                if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                                if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//        	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                            }
                    }
                }
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlersecurityCode.class.getName() +
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
	                        if( getParent().onChangeSubmit("securityCode"))
	                        {
	                            getParent().setCalculated(table, "securityCode");
	                            getParent().calculateFormula(table, "securityCode");
	                        }

							//vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("securityCode");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//							if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
	                    }
	                }
							}
            }

            public void _setValue(BigDecimal newval) throws boRuntimeException {
                if(isdbbinding) {

                        p_bridgerow.getDataRow().updateBigDecimal("SECURITYCODE",newval);
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

            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        
        public final class HandlerLIN extends BridgeAttributeHandler {

            private boolean isdbbinding=true;
            private BigDecimal fieldvalue;
            private boBridgeRow p_bridgerow;

			public HandlerLIN(boObject parent, boBridgeRow bridgerow ) {
                super( parent , bridgerow ,bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("LIN") );
                p_bridgerow = bridgerow;
            }
            
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
					ret=p_bridgerow.getDataRow().getBigDecimal("LIN");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            public void setValueObject(Object value) throws boRuntimeException {
                this.setValue((BigDecimal)value);
            }
            public Object getValueObject() throws boRuntimeException 
            {
                return this.getValue();
            }
            public String getValueString() throws boRuntimeException {
                return boConvertUtils.convertToString(this.getValue(),this);
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "LIN"))
                    {
                       getParent().setCalculated(table, "LIN");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "LIN"))
                    {
                        getParent().setCalculated(table, "LIN");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("LIN"))
                    {
                        getParent().setCalculated(table, "LIN"); 
                    }
                    else
                    {
                        getParent().clear(table, "LIN"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                
                    if(getParent().onChangeSubmit("LIN"))
                    {
                        getParent().setWaiting(table, "LIN");
                        getParent().calculateFormula(new Hashtable(table), "LIN");
                        //getParent().setCalculated(table, "LIN");
                    }                    
                }
            }

            public void setValue(BigDecimal newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlerLIN.class.getName() +
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
	                    	                        
                                if( recalc && getParent().onChangeSubmit("LIN"))
                                {
                                    Hashtable table = new Hashtable();
                                    getParent().setCalculated(table, "LIN");
                                    getParent().calculateFormula(table, "LIN");
                                }
	                        
                                //vou verificar se o objecto se encontra numa bridge
                                if ( getParent().p_parentBridgeRow != null )
                                {
                                    getParent().getParentBridgeRow().getBridge().lineChanged("LIN");
                                }

                                fireAfterChangeEvent( this, chgval, newval);
//                                if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                                if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//        	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                            }
                    }
                }
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlerLIN.class.getName() +
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
	                        if( getParent().onChangeSubmit("LIN"))
	                        {
	                            getParent().setCalculated(table, "LIN");
	                            getParent().calculateFormula(table, "LIN");
	                        }

							//vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("LIN");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//							if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
	                    }
	                }
							}
            }

            public void _setValue(BigDecimal newval) throws boRuntimeException {
                if(isdbbinding) {

                        p_bridgerow.getDataRow().updateBigDecimal("LIN",newval);
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

            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        
    } 
     
    public final class BridgeHandlerREADLIST extends bridgeHandler {
        private DataResultSet p_node;
        private String        p_fatherfield;
        private String        p_childfield;
        private boObject      p_parent;

        
        public BridgeHandlerREADLIST(DataResultSet data,boObject parent) {
            super("READLIST",
                  data,
                  parent.getBoDefinition().getAttributeRef("READLIST").getBridge().getChildFieldName(),
                  parent
                 );

            p_fatherfield = parent.getBoDefinition().getAttributeRef("READLIST").getBridge().getFatherFieldName();
            p_childfield  = parent.getBoDefinition().getAttributeRef("READLIST").getBridge().getChildFieldName();
            
            p_parent = parent;
            p_node = data;

			refreshBridgeData();
            first();
        }
        public void add(BigDecimal boui, byte type) throws boRuntimeException {
            add(boui,-1, type);
        }
        public void add(BigDecimal boui,int xnrow, byte type) throws boRuntimeException {

              //tranform ao adicionar a brige
              netgest.bo.transformers.Transformer transfClass = null;
			  if ( getParent().getMode() != boObject.MODE_EDIT_TEMPLATE )
				{
				  if(( transfClass = this.getDefAttribute().getTransformClassMap()) != null)
				  {
					 if(boui != null)
					 {
						boui = new BigDecimal(transfClass.transform(getEboContext(), getParent(), boui.longValue()));
					 }
				  }
				}

			  boBridgeRow brow = createRow();
			  ObjAttHandler att = (ObjAttHandler)brow.getAttribute( this.getName() );

			  BigDecimal newval = boui;

//			  boolean allow=att.onBeforeChange(new boEvent(att,att.EVENT_BEFORE_CHANGE, null ,newval));
//			  if( newval != null ) allow=allow && att.onBeforeAdd(new boEvent(att, att.EVENT_BEFORE_ADD,newval));
              
			  if( att.fireBeforeChangeEvent( att, null, newval) ) 
			  {
				  
				  brow.getDataRow().updateBigDecimal(p_childfield,boui);
  		          this.insertRow( brow );
				  this.moveTo( this.getRowCount() );

				  if(xnrow !=-1)
                  {
                      this.moveRowTo(xnrow);
                  }

				  att.checkParent( null, newval );

				  getParent().setChanged( true );

                  if(getParent().onChangeSubmit("READLIST"))
                  {
                      getParent().calculateFormula("READLIST");
                  }
                  att.fireAfterChangeEvent( att, null, newval );
//				  if( newval != null ) att.onAfterAdd(new boEvent( att ,att.EVENT_AFTER_ADD,newval));
//				  att.onAfterChange(new boEvent( att ,att.EVENT_AFTER_CHANGE, null ,newval));
			  }

        }

		public boolean remove() throws boRuntimeException 
        {
            boolean ret = false;

          

                boBridgeRow rowtodelete = rows( this.getRow() );
				ObjAttHandler att = (ObjAttHandler)rowtodelete.getAttribute( this.getName() );

				BigDecimal chgval = this.getValue();

	
//				boolean allow=att.onBeforeChange(new boEvent(att,att.EVENT_BEFORE_CHANGE,chgval,null));
//                allow = allow && att.onBeforeRemove(new boEvent(att,att.EVENT_BEFORE_REMOVE,chgval));

                if( att.fireBeforeChangeEvent( att, chgval, null) ) 
                {
					rowtodelete.getDataRow().updateBigDecimal(p_childfield,null);
					att.checkParent( chgval, null );

				    getParent().setChanged( true );

                    Enumeration  lineatts = rowtodelete.getLineAttributes().elements();

                    _remove( rowtodelete );  // remove the line

					
                    while( lineatts.hasMoreElements() )
                    {
                        AttributeHandler xatt = ( AttributeHandler )lineatts.nextElement();
                        if ( xatt.getValueObject() != null )
                        {
                            xatt.setValueObject( null );
                        }
                    }
                    
                    ret = true;
					//calculateFormulas
					if(getParent().onChangeSubmit("READLIST"))
					{
					   getParent().calculateFormula("READLIST");
					}

                    att.fireAfterChangeEvent( att, chgval, null);
//					att.onAfterRemove(new boEvent(att,att.EVENT_AFTER_REMOVE,chgval));
//					att.onAfterChange(new boEvent(att,att.EVENT_AFTER_CHANGE,chgval,null));
                    
				}
           
            return ret;
        }




		public boBridgeRow createRow( netgest.bo.data.DataRow row )
		{
			return new BridgeHanlderREADLISTRow( this, row );
		}

		public final class BridgeHanlderREADLISTRow extends boBridgeRow
		{
			public BridgeHanlderREADLISTRow( bridgeHandler bridge, netgest.bo.data.DataRow row )
			{
				super( bridge , row );
				addLine();
			}

			public void addLine() 
			{
				boAttributesArray xatt = super.getLineAttributes();
			
				if(this.getAttribute("READLIST")==null)
					xatt.add( new HandlerREADLIST( getParent(), this ) );
				
		
			
				if(this.getAttribute("LIN")==null)
					xatt.add( new HandlerLIN( getParent(), this ) );
				
			} 


		}


        
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
       
        public final class HandlerREADLIST extends BridgeObjAttributeHandler {
            public HandlerREADLIST( boObject parent , boBridgeRow bridgerow ) {
                super( parent , bridgerow , bridgerow.getBridge().getDefAttribute().getName().equals("READLIST")?parent.getBoDefinition().getAttributeRef("READLIST"):bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("READLIST") );
            }
            public void setValueString(String value) {
                try {
                    this.setValue(boConvertUtils.convertToBigDecimal(value,this));
                    if(getParent().onChangeSubmit("READLIST"))
                    {
                        Hashtable table = new Hashtable();                    
                        getParent().setCalculated(table, "READLIST");
                        getParent().calculateFormula(table, "READLIST");
                    }
                } catch (Exception e) {
                    super.setInvalid(e.getMessage(),value);
                }
            }
            public String getValueString() throws boRuntimeException {
                try {
                    return boConvertUtils.convertToString(this.getValue(),this);
                } catch (Exception e) {
                    String[] args = {getParent().getName(),"READLIST"};
                    throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
                }
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "READLIST"))
                    {
                       getParent().setCalculated(table, "READLIST");
                       this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "READLIST"))
                    {
                        getParent().setCalculated(table, "READLIST");
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("READLIST"))
                    {
                        getParent().setCalculated(table, "READLIST"); 
                    }
                    else
                    {
                        getParent().clear(table, "READLIST"); 
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                }
                else
                {   
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    if(getParent().onChangeSubmit("READLIST"))
                    {                 
                        getParent().setWaiting(table, "READLIST");
                        getParent().calculateFormula(new Hashtable(table), "READLIST");
                        //getParent().setCalculated(table, "READLIST");
                    }                    
                }
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                this.setValue(newval, recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL);
	        	
	        	//setInputType( recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	        	
                if( recalc && getParent().onChangeSubmit("READLIST"))
                {
                    Hashtable table = new Hashtable();
                    getParent().setCalculated(table, "READLIST");
                    getParent().calculateFormula(table, "READLIST");
                }
        
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("READLIST");
				}
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                this.setValue(newval, AttributeHandler.INPUT_FROM_INTERNAL);
                //setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                if( getParent().onChangeSubmit("READLIST"))
                {
                    getParent().setCalculated(table, "READLIST");
                    getParent().calculateFormula(table, "READLIST");
                }
                
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("READLIST");
				}

            }
            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        

       
        public final class HandlerLIN extends BridgeAttributeHandler {

            private boolean isdbbinding=true;
            private BigDecimal fieldvalue;
            private boBridgeRow p_bridgerow;

			public HandlerLIN(boObject parent, boBridgeRow bridgerow ) {
                super( parent , bridgerow ,bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("LIN") );
                p_bridgerow = bridgerow;
            }
            
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
					ret=p_bridgerow.getDataRow().getBigDecimal("LIN");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            public void setValueObject(Object value) throws boRuntimeException {
                this.setValue((BigDecimal)value);
            }
            public Object getValueObject() throws boRuntimeException 
            {
                return this.getValue();
            }
            public String getValueString() throws boRuntimeException {
                return boConvertUtils.convertToString(this.getValue(),this);
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "LIN"))
                    {
                       getParent().setCalculated(table, "LIN");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "LIN"))
                    {
                        getParent().setCalculated(table, "LIN");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("LIN"))
                    {
                        getParent().setCalculated(table, "LIN"); 
                    }
                    else
                    {
                        getParent().clear(table, "LIN"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                
                    if(getParent().onChangeSubmit("LIN"))
                    {
                        getParent().setWaiting(table, "LIN");
                        getParent().calculateFormula(new Hashtable(table), "LIN");
                        //getParent().setCalculated(table, "LIN");
                    }                    
                }
            }

            public void setValue(BigDecimal newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlerLIN.class.getName() +
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
	                    	                        
                                if( recalc && getParent().onChangeSubmit("LIN"))
                                {
                                    Hashtable table = new Hashtable();
                                    getParent().setCalculated(table, "LIN");
                                    getParent().calculateFormula(table, "LIN");
                                }
	                        
                                //vou verificar se o objecto se encontra numa bridge
                                if ( getParent().p_parentBridgeRow != null )
                                {
                                    getParent().getParentBridgeRow().getBridge().lineChanged("LIN");
                                }

                                fireAfterChangeEvent( this, chgval, newval);
//                                if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                                if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//        	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                            }
                    }
                }
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlerLIN.class.getName() +
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
	                        if( getParent().onChangeSubmit("LIN"))
	                        {
	                            getParent().setCalculated(table, "LIN");
	                            getParent().calculateFormula(table, "LIN");
	                        }

							//vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("LIN");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//							if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
	                    }
	                }
							}
            }

            public void _setValue(BigDecimal newval) throws boRuntimeException {
                if(isdbbinding) {

                        p_bridgerow.getDataRow().updateBigDecimal("LIN",newval);
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

            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        
    } 
     
    public final class BridgeHandlerdocuments extends bridgeHandler {
        private DataResultSet p_node;
        private String        p_fatherfield;
        private String        p_childfield;
        private boObject      p_parent;

        
        public BridgeHandlerdocuments(DataResultSet data,boObject parent) {
            super("documents",
                  data,
                  parent.getBoDefinition().getAttributeRef("documents").getBridge().getChildFieldName(),
                  parent
                 );

            p_fatherfield = parent.getBoDefinition().getAttributeRef("documents").getBridge().getFatherFieldName();
            p_childfield  = parent.getBoDefinition().getAttributeRef("documents").getBridge().getChildFieldName();
            
            p_parent = parent;
            p_node = data;

			refreshBridgeData();
            first();
        }
        public void add(BigDecimal boui, byte type) throws boRuntimeException {
            add(boui,-1, type);
        }
        public void add(BigDecimal boui,int xnrow, byte type) throws boRuntimeException {

              //tranform ao adicionar a brige
              netgest.bo.transformers.Transformer transfClass = null;
			  if ( getParent().getMode() != boObject.MODE_EDIT_TEMPLATE )
				{
				  if(( transfClass = this.getDefAttribute().getTransformClassMap()) != null)
				  {
					 if(boui != null)
					 {
						boui = new BigDecimal(transfClass.transform(getEboContext(), getParent(), boui.longValue()));
					 }
				  }
				}

			  boBridgeRow brow = createRow();
			  ObjAttHandler att = (ObjAttHandler)brow.getAttribute( this.getName() );

			  BigDecimal newval = boui;

//			  boolean allow=att.onBeforeChange(new boEvent(att,att.EVENT_BEFORE_CHANGE, null ,newval));
//			  if( newval != null ) allow=allow && att.onBeforeAdd(new boEvent(att, att.EVENT_BEFORE_ADD,newval));
              
			  if( att.fireBeforeChangeEvent( att, null, newval) ) 
			  {
				  
				  brow.getDataRow().updateBigDecimal(p_childfield,boui);
  		          this.insertRow( brow );
				  this.moveTo( this.getRowCount() );

				  if(xnrow !=-1)
                  {
                      this.moveRowTo(xnrow);
                  }

				  att.checkParent( null, newval );

				  getParent().setChanged( true );

                  if(getParent().onChangeSubmit("documents"))
                  {
                      getParent().calculateFormula("documents");
                  }
                  att.fireAfterChangeEvent( att, null, newval );
//				  if( newval != null ) att.onAfterAdd(new boEvent( att ,att.EVENT_AFTER_ADD,newval));
//				  att.onAfterChange(new boEvent( att ,att.EVENT_AFTER_CHANGE, null ,newval));
			  }

        }

		public boolean remove() throws boRuntimeException 
        {
            boolean ret = false;

          

                boBridgeRow rowtodelete = rows( this.getRow() );
				ObjAttHandler att = (ObjAttHandler)rowtodelete.getAttribute( this.getName() );

				BigDecimal chgval = this.getValue();

	
//				boolean allow=att.onBeforeChange(new boEvent(att,att.EVENT_BEFORE_CHANGE,chgval,null));
//                allow = allow && att.onBeforeRemove(new boEvent(att,att.EVENT_BEFORE_REMOVE,chgval));

                if( att.fireBeforeChangeEvent( att, chgval, null) ) 
                {
					rowtodelete.getDataRow().updateBigDecimal(p_childfield,null);
					att.checkParent( chgval, null );

				    getParent().setChanged( true );

                    Enumeration  lineatts = rowtodelete.getLineAttributes().elements();

                    _remove( rowtodelete );  // remove the line

					
                    while( lineatts.hasMoreElements() )
                    {
                        AttributeHandler xatt = ( AttributeHandler )lineatts.nextElement();
                        if ( xatt.getValueObject() != null )
                        {
                            xatt.setValueObject( null );
                        }
                    }
                    
                    ret = true;
					//calculateFormulas
					if(getParent().onChangeSubmit("documents"))
					{
					   getParent().calculateFormula("documents");
					}

                    att.fireAfterChangeEvent( att, chgval, null);
//					att.onAfterRemove(new boEvent(att,att.EVENT_AFTER_REMOVE,chgval));
//					att.onAfterChange(new boEvent(att,att.EVENT_AFTER_CHANGE,chgval,null));
                    
				}
           
            return ret;
        }




		public boBridgeRow createRow( netgest.bo.data.DataRow row )
		{
			return new BridgeHanlderdocumentsRow( this, row );
		}

		public final class BridgeHanlderdocumentsRow extends boBridgeRow
		{
			public BridgeHanlderdocumentsRow( bridgeHandler bridge, netgest.bo.data.DataRow row )
			{
				super( bridge , row );
				addLine();
			}

			public void addLine() 
			{
				boAttributesArray xatt = super.getLineAttributes();
			
				if(this.getAttribute("documents")==null)
					xatt.add( new Handlerdocuments( getParent(), this ) );
				
		
			
				if(this.getAttribute("LIN")==null)
					xatt.add( new HandlerLIN( getParent(), this ) );
				
			} 


		}


        
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
       
        public final class Handlerdocuments extends BridgeObjAttributeHandler {
            public Handlerdocuments( boObject parent , boBridgeRow bridgerow ) {
                super( parent , bridgerow , bridgerow.getBridge().getDefAttribute().getName().equals("documents")?parent.getBoDefinition().getAttributeRef("documents"):bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("documents") );
            }
            public void setValueString(String value) {
                try {
                    this.setValue(boConvertUtils.convertToBigDecimal(value,this));
                    if(getParent().onChangeSubmit("documents"))
                    {
                        Hashtable table = new Hashtable();                    
                        getParent().setCalculated(table, "documents");
                        getParent().calculateFormula(table, "documents");
                    }
                } catch (Exception e) {
                    super.setInvalid(e.getMessage(),value);
                }
            }
            public String getValueString() throws boRuntimeException {
                try {
                    return boConvertUtils.convertToString(this.getValue(),this);
                } catch (Exception e) {
                    String[] args = {getParent().getName(),"documents"};
                    throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
                }
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "documents"))
                    {
                       getParent().setCalculated(table, "documents");
                       this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "documents"))
                    {
                        getParent().setCalculated(table, "documents");
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("documents"))
                    {
                        getParent().setCalculated(table, "documents"); 
                    }
                    else
                    {
                        getParent().clear(table, "documents"); 
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                }
                else
                {   
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    if(getParent().onChangeSubmit("documents"))
                    {                 
                        getParent().setWaiting(table, "documents");
                        getParent().calculateFormula(new Hashtable(table), "documents");
                        //getParent().setCalculated(table, "documents");
                    }                    
                }
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                this.setValue(newval, recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL);
	        	
	        	//setInputType( recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	        	
                if( recalc && getParent().onChangeSubmit("documents"))
                {
                    Hashtable table = new Hashtable();
                    getParent().setCalculated(table, "documents");
                    getParent().calculateFormula(table, "documents");
                }
        
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("documents");
				}
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                this.setValue(newval, AttributeHandler.INPUT_FROM_INTERNAL);
                //setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                if( getParent().onChangeSubmit("documents"))
                {
                    getParent().setCalculated(table, "documents");
                    getParent().calculateFormula(table, "documents");
                }
                
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("documents");
				}

            }
            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        

       
        public final class HandlerLIN extends BridgeAttributeHandler {

            private boolean isdbbinding=true;
            private BigDecimal fieldvalue;
            private boBridgeRow p_bridgerow;

			public HandlerLIN(boObject parent, boBridgeRow bridgerow ) {
                super( parent , bridgerow ,bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("LIN") );
                p_bridgerow = bridgerow;
            }
            
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
					ret=p_bridgerow.getDataRow().getBigDecimal("LIN");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            public void setValueObject(Object value) throws boRuntimeException {
                this.setValue((BigDecimal)value);
            }
            public Object getValueObject() throws boRuntimeException 
            {
                return this.getValue();
            }
            public String getValueString() throws boRuntimeException {
                return boConvertUtils.convertToString(this.getValue(),this);
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "LIN"))
                    {
                       getParent().setCalculated(table, "LIN");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "LIN"))
                    {
                        getParent().setCalculated(table, "LIN");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("LIN"))
                    {
                        getParent().setCalculated(table, "LIN"); 
                    }
                    else
                    {
                        getParent().clear(table, "LIN"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                
                    if(getParent().onChangeSubmit("LIN"))
                    {
                        getParent().setWaiting(table, "LIN");
                        getParent().calculateFormula(new Hashtable(table), "LIN");
                        //getParent().setCalculated(table, "LIN");
                    }                    
                }
            }

            public void setValue(BigDecimal newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlerLIN.class.getName() +
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
	                    	                        
                                if( recalc && getParent().onChangeSubmit("LIN"))
                                {
                                    Hashtable table = new Hashtable();
                                    getParent().setCalculated(table, "LIN");
                                    getParent().calculateFormula(table, "LIN");
                                }
	                        
                                //vou verificar se o objecto se encontra numa bridge
                                if ( getParent().p_parentBridgeRow != null )
                                {
                                    getParent().getParentBridgeRow().getBridge().lineChanged("LIN");
                                }

                                fireAfterChangeEvent( this, chgval, newval);
//                                if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                                if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//        	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                            }
                    }
                }
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlerLIN.class.getName() +
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
	                        if( getParent().onChangeSubmit("LIN"))
	                        {
	                            getParent().setCalculated(table, "LIN");
	                            getParent().calculateFormula(table, "LIN");
	                        }

							//vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("LIN");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//							if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
	                    }
	                }
							}
            }

            public void _setValue(BigDecimal newval) throws boRuntimeException {
                if(isdbbinding) {

                        p_bridgerow.getDataRow().updateBigDecimal("LIN",newval);
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

            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        
    } 
     
    public final class BridgeHandlerdefActivityDepends extends bridgeHandler {
        private DataResultSet p_node;
        private String        p_fatherfield;
        private String        p_childfield;
        private boObject      p_parent;

        
        public BridgeHandlerdefActivityDepends(DataResultSet data,boObject parent) {
            super("defActivityDepends",
                  data,
                  parent.getBoDefinition().getAttributeRef("defActivityDepends").getBridge().getChildFieldName(),
                  parent
                 );

            p_fatherfield = parent.getBoDefinition().getAttributeRef("defActivityDepends").getBridge().getFatherFieldName();
            p_childfield  = parent.getBoDefinition().getAttributeRef("defActivityDepends").getBridge().getChildFieldName();
            
            p_parent = parent;
            p_node = data;

			refreshBridgeData();
            first();
        }
        public void add(BigDecimal boui, byte type) throws boRuntimeException {
            add(boui,-1, type);
        }
        public void add(BigDecimal boui,int xnrow, byte type) throws boRuntimeException {

              //tranform ao adicionar a brige
              netgest.bo.transformers.Transformer transfClass = null;
			  if ( getParent().getMode() != boObject.MODE_EDIT_TEMPLATE )
				{
				  if(( transfClass = this.getDefAttribute().getTransformClassMap()) != null)
				  {
					 if(boui != null)
					 {
						boui = new BigDecimal(transfClass.transform(getEboContext(), getParent(), boui.longValue()));
					 }
				  }
				}

			  boBridgeRow brow = createRow();
			  ObjAttHandler att = (ObjAttHandler)brow.getAttribute( this.getName() );

			  BigDecimal newval = boui;

//			  boolean allow=att.onBeforeChange(new boEvent(att,att.EVENT_BEFORE_CHANGE, null ,newval));
//			  if( newval != null ) allow=allow && att.onBeforeAdd(new boEvent(att, att.EVENT_BEFORE_ADD,newval));
              
			  if( att.fireBeforeChangeEvent( att, null, newval) ) 
			  {
				  
				  brow.getDataRow().updateBigDecimal(p_childfield,boui);
  		          this.insertRow( brow );
				  this.moveTo( this.getRowCount() );

				  if(xnrow !=-1)
                  {
                      this.moveRowTo(xnrow);
                  }

				  att.checkParent( null, newval );

				  getParent().setChanged( true );

                  if(getParent().onChangeSubmit("defActivityDepends"))
                  {
                      getParent().calculateFormula("defActivityDepends");
                  }
                  att.fireAfterChangeEvent( att, null, newval );
//				  if( newval != null ) att.onAfterAdd(new boEvent( att ,att.EVENT_AFTER_ADD,newval));
//				  att.onAfterChange(new boEvent( att ,att.EVENT_AFTER_CHANGE, null ,newval));
			  }

        }

		public boolean remove() throws boRuntimeException 
        {
            boolean ret = false;

          

                boBridgeRow rowtodelete = rows( this.getRow() );
				ObjAttHandler att = (ObjAttHandler)rowtodelete.getAttribute( this.getName() );

				BigDecimal chgval = this.getValue();

	
//				boolean allow=att.onBeforeChange(new boEvent(att,att.EVENT_BEFORE_CHANGE,chgval,null));
//                allow = allow && att.onBeforeRemove(new boEvent(att,att.EVENT_BEFORE_REMOVE,chgval));

                if( att.fireBeforeChangeEvent( att, chgval, null) ) 
                {
					rowtodelete.getDataRow().updateBigDecimal(p_childfield,null);
					att.checkParent( chgval, null );

				    getParent().setChanged( true );

                    Enumeration  lineatts = rowtodelete.getLineAttributes().elements();

                    _remove( rowtodelete );  // remove the line

					
                    while( lineatts.hasMoreElements() )
                    {
                        AttributeHandler xatt = ( AttributeHandler )lineatts.nextElement();
                        if ( xatt.getValueObject() != null )
                        {
                            xatt.setValueObject( null );
                        }
                    }
                    
                    ret = true;
					//calculateFormulas
					if(getParent().onChangeSubmit("defActivityDepends"))
					{
					   getParent().calculateFormula("defActivityDepends");
					}

                    att.fireAfterChangeEvent( att, chgval, null);
//					att.onAfterRemove(new boEvent(att,att.EVENT_AFTER_REMOVE,chgval));
//					att.onAfterChange(new boEvent(att,att.EVENT_AFTER_CHANGE,chgval,null));
                    
				}
           
            return ret;
        }




		public boBridgeRow createRow( netgest.bo.data.DataRow row )
		{
			return new BridgeHanlderdefActivityDependsRow( this, row );
		}

		public final class BridgeHanlderdefActivityDependsRow extends boBridgeRow
		{
			public BridgeHanlderdefActivityDependsRow( bridgeHandler bridge, netgest.bo.data.DataRow row )
			{
				super( bridge , row );
				addLine();
			}

			public void addLine() 
			{
				boAttributesArray xatt = super.getLineAttributes();
			
				if(this.getAttribute("defActivityDepends")==null)
					xatt.add( new HandlerdefActivityDepends( getParent(), this ) );
				
		
			
				if(this.getAttribute("LIN")==null)
					xatt.add( new HandlerLIN( getParent(), this ) );
				
			} 


		}


        
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
       
        public final class HandlerdefActivityDepends extends BridgeObjAttributeHandler {
            public HandlerdefActivityDepends( boObject parent , boBridgeRow bridgerow ) {
                super( parent , bridgerow , bridgerow.getBridge().getDefAttribute().getName().equals("defActivityDepends")?parent.getBoDefinition().getAttributeRef("defActivityDepends"):bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("defActivityDepends") );
            }
            public void setValueString(String value) {
                try {
                    this.setValue(boConvertUtils.convertToBigDecimal(value,this));
                    if(getParent().onChangeSubmit("defActivityDepends"))
                    {
                        Hashtable table = new Hashtable();                    
                        getParent().setCalculated(table, "defActivityDepends");
                        getParent().calculateFormula(table, "defActivityDepends");
                    }
                } catch (Exception e) {
                    super.setInvalid(e.getMessage(),value);
                }
            }
            public String getValueString() throws boRuntimeException {
                try {
                    return boConvertUtils.convertToString(this.getValue(),this);
                } catch (Exception e) {
                    String[] args = {getParent().getName(),"defActivityDepends"};
                    throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
                }
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "defActivityDepends"))
                    {
                       getParent().setCalculated(table, "defActivityDepends");
                       this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "defActivityDepends"))
                    {
                        getParent().setCalculated(table, "defActivityDepends");
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("defActivityDepends"))
                    {
                        getParent().setCalculated(table, "defActivityDepends"); 
                    }
                    else
                    {
                        getParent().clear(table, "defActivityDepends"); 
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                }
                else
                {   
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    if(getParent().onChangeSubmit("defActivityDepends"))
                    {                 
                        getParent().setWaiting(table, "defActivityDepends");
                        getParent().calculateFormula(new Hashtable(table), "defActivityDepends");
                        //getParent().setCalculated(table, "defActivityDepends");
                    }                    
                }
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                this.setValue(newval, recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL);
	        	
	        	//setInputType( recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	        	
                if( recalc && getParent().onChangeSubmit("defActivityDepends"))
                {
                    Hashtable table = new Hashtable();
                    getParent().setCalculated(table, "defActivityDepends");
                    getParent().calculateFormula(table, "defActivityDepends");
                }
        
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("defActivityDepends");
				}
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                this.setValue(newval, AttributeHandler.INPUT_FROM_INTERNAL);
                //setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                if( getParent().onChangeSubmit("defActivityDepends"))
                {
                    getParent().setCalculated(table, "defActivityDepends");
                    getParent().calculateFormula(table, "defActivityDepends");
                }
                
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("defActivityDepends");
				}

            }
            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        

       
        public final class HandlerLIN extends BridgeAttributeHandler {

            private boolean isdbbinding=true;
            private BigDecimal fieldvalue;
            private boBridgeRow p_bridgerow;

			public HandlerLIN(boObject parent, boBridgeRow bridgerow ) {
                super( parent , bridgerow ,bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("LIN") );
                p_bridgerow = bridgerow;
            }
            
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
					ret=p_bridgerow.getDataRow().getBigDecimal("LIN");
                } else {
                    ret = fieldvalue;
                }
                fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
                return ret;
            }
            public void setValueObject(Object value) throws boRuntimeException {
                this.setValue((BigDecimal)value);
            }
            public Object getValueObject() throws boRuntimeException 
            {
                return this.getValue();
            }
            public String getValueString() throws boRuntimeException {
                return boConvertUtils.convertToString(this.getValue(),this);
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "LIN"))
                    {
                       getParent().setCalculated(table, "LIN");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "LIN"))
                    {
                        getParent().setCalculated(table, "LIN");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("LIN"))
                    {
                        getParent().setCalculated(table, "LIN"); 
                    }
                    else
                    {
                        getParent().clear(table, "LIN"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                
                    if(getParent().onChangeSubmit("LIN"))
                    {
                        getParent().setWaiting(table, "LIN");
                        getParent().calculateFormula(new Hashtable(table), "LIN");
                        //getParent().setCalculated(table, "LIN");
                    }                    
                }
            }

            public void setValue(BigDecimal newval ) throws boRuntimeException
            {
                setValue( newval, true );
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlerLIN.class.getName() +
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
	                    	                        
                                if( recalc && getParent().onChangeSubmit("LIN"))
                                {
                                    Hashtable table = new Hashtable();
                                    getParent().setCalculated(table, "LIN");
                                    getParent().calculateFormula(table, "LIN");
                                }
	                        
                                //vou verificar se o objecto se encontra numa bridge
                                if ( getParent().p_parentBridgeRow != null )
                                {
                                    getParent().getParentBridgeRow().getBridge().lineChanged("LIN");
                                }

                                fireAfterChangeEvent( this, chgval, newval);
//                                if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                                if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//        	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                            }
                    }
                }
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                if (getParent().isCheckSecurity() && !this.hasRights())
                    	throw new boRuntimeException(HandlerLIN.class.getName() +
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
	                        if( getParent().onChangeSubmit("LIN"))
	                        {
	                            getParent().setCalculated(table, "LIN");
	                            getParent().calculateFormula(table, "LIN");
	                        }

							//vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("LIN");
							}
	
                            fireAfterChangeEvent( this, chgval, newval);
//							if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//	                        if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//	                        onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
	                    }
	                }
							}
            }

            public void _setValue(BigDecimal newval) throws boRuntimeException {
                if(isdbbinding) {

                        p_bridgerow.getDataRow().updateBigDecimal("LIN",newval);
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

            
            
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



            
        }
        
    } 
    
    

   

   
    public final class Handlerstate extends boObjectStateHandler {
       
        public final String STATE_active="1";
        
        public final String STATE_inactive="0";
        
        public Handlerstate(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("state"));
        }
    }

    
    public final class HandleractiveStatus extends boObjectStateHandler {
       
        public final String STATE_ready="0";
        
        public final String STATE_running="1";
        
        public final String STATE_finished="3";
        
        public final String STATE_witherror="2";
        
        public HandleractiveStatus(boObject bobj) {
            super(bobj,bobj.getBoDefinition().getBoClsState().getChildStateAttributes("activeStatus"));
        }
    }

    
        
    
	public boolean methodIsHidden(String methodName) throws boRuntimeException{
 if( methodName.equals("cancelar")) {return netgest.xwf.core.xwfDefActivity.cancelHiddenWhen(this); }
 return super.methodIsHidden(methodName);}
	public void cancelar() throws boRuntimeException {
	netgest.xwf.core.xwfDefActivity.cancel(this);
super.update();
	}
	public void update() throws boRuntimeException {
	
if( this.state.getValueString() == null || this.state.getValueString().length() == 0)
				{
					this.state.setValueString("1");
					if( this.activeStatus.getValueString() == null || !this.activeStatus.getValueString().equals("running") )
					{
						this.activeStatus.setValueString("0");
					}
				}
				super.update();
				
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
return new String[] {"type_recursive", "period"} ;
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
	
	public boolean onBeforeSave(boEvent event) throws boRuntimeException {
	if(this.getAttribute("nextruntime").getValueDate() == null)
{
	java.util.Date d = netgest.xwf.core.xwfDefActivity.calcNextRun(this, null, netgest.bo.runtime.robots.xwfDefActivAgent.SLEEP_TIME_IN_MILLS);
  this.getAttribute("nextruntime").setValueDate(d);
}
return true;
}

    
    
}
