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



public  class xwfReassign extends boObject implements  Serializable {  

   
    public Handlerfrom from;       
    public HandlerCREATOR CREATOR;       
   
    public Handlerreassigned reassigned;    
   
    public netgest.bo.runtime.attributes.boAttributeObject to;    
    public netgest.bo.runtime.attributes.boAttributeString description;    
    public netgest.bo.runtime.attributes.boAttributeDate SYS_DTCREATE;    
    public netgest.bo.runtime.attributes.boAttributeString assignType;    
    public netgest.bo.runtime.attributes.boAttributeString errormessage;    
    public netgest.bo.runtime.attributes.boAttributeObject PARENT;    
    public netgest.bo.runtime.attributes.boAttributeObject PARENTCTX;    
    public netgest.bo.runtime.attributes.boAttributeObject TEMPLATE;    
    public netgest.bo.runtime.attributes.boAttributeNumber BOUI;    
    public netgest.bo.runtime.attributes.boAttributeString CLASSNAME;    
    public netgest.bo.runtime.attributes.boAttributeDate SYS_DTSAVE;    
    public netgest.bo.runtime.attributes.boAttributeString SYS_ORIGIN;    
    public netgest.bo.runtime.attributes.boAttributeObject SYS_FROMOBJ;    
   
     
    public xwfReassign() {
        super();
        bo_version      = "1.0";
        bo_name         = "xwfReassign";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("xwfReassign");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        atts.add(from = new Handlerfrom(this));
        
        atts.add(CREATOR = new HandlerCREATOR(this));
        
        
       

       
        atts.add(reassigned = new Handlerreassigned(this));
        
        
       
        atts.add(to = new netgest.bo.runtime.attributes.boAttributeObject(this,"to"));
        
        atts.add(description = new netgest.bo.runtime.attributes.boAttributeString(this,"description"));
        
        atts.add(SYS_DTCREATE = new netgest.bo.runtime.attributes.boAttributeDate(this,"SYS_DTCREATE"));
        
        atts.add(assignType = new netgest.bo.runtime.attributes.boAttributeString(this,"assignType"));
        
        atts.add(errormessage = new netgest.bo.runtime.attributes.boAttributeString(this,"errormessage"));
        
        atts.add(PARENT = new netgest.bo.runtime.attributes.boAttributeObject(this,"PARENT"));
        
        atts.add(PARENTCTX = new netgest.bo.runtime.attributes.boAttributeObject(this,"PARENTCTX"));
        
        atts.add(TEMPLATE = new netgest.bo.runtime.attributes.boAttributeObject(this,"TEMPLATE"));
        
        atts.add(BOUI = new netgest.bo.runtime.attributes.boAttributeNumber(this,"BOUI"));
        
        atts.add(CLASSNAME = new netgest.bo.runtime.attributes.boAttributeString(this,"CLASSNAME"));
        
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
           
                
                    if( from.haveDefaultValue()) 
                    {
                    	if(from.defaultValue() != null)
                    	{
                        	from._setValue(verifyTransformer(from,boConvertUtils.convertToBigDecimal(from.defaultValue(), from)));
                        }
                        else
                        {
                       		from._setValue(null);
                       	}
                        from.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
                
                    if( CREATOR.haveDefaultValue()) 
                    {
                    	if(CREATOR.defaultValue() != null)
                    	{
                        	CREATOR._setValue(verifyTransformer(CREATOR,boConvertUtils.convertToBigDecimal(CREATOR.defaultValue(), CREATOR)));
                        }
                        else
                        {
                       		CREATOR._setValue(null);
                       	}
                        CREATOR.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
           
                
                    if( reassigned.haveDefaultValue()) 
                    {
                    	if(reassigned.defaultValue() != null)
                    	{
                        	reassigned._setValue(boConvertUtils.convertToBigDecimal(reassigned.defaultValue(), reassigned));
                        }
                        else
                        {
                        	reassigned._setValue(null);
                        }
                        reassigned.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( assignType.haveDefaultValue()) 
                {
                	if(assignType.defaultValue() != null)
                	{
                    	assignType._setValue(boConvertUtils.convertToString(assignType.defaultValue(), assignType));
                    }
                    else
                    {
                    	assignType._setValue(null);
                    }
                    assignType.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
    
            if("xwfReassign".equals(this.getName()))
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
   
    public final class Handlerfrom extends ObjAttHandler {
        public Handlerfrom(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("from"));
        }

        
        public void setValueString(String value) {
            try {
                this.setValue(boConvertUtils.convertToBigDecimal(value,this));
                Hashtable table = new Hashtable();
                if(getParent().onChangeSubmit("from"))
                {
                    getParent().setCalculated(table, "from");
                    getParent().calculateFormula(table, "from");
                }
            } catch (Exception e) {
                super.setInvalid(e.getMessage(),value);
            }
        }
        public String getValueString() throws boRuntimeException {
            try {
                return boConvertUtils.convertToString(getValue(),this);
            } catch (Exception e) {
                String[] args = {getParent().getName(),"from"};
                throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
            }
        }
        public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
        {
            if(getParent().alreadyCalculated(table, dependence)) 
            {                       
                if(getParent().isWaiting(table, "from"))
                {
                    getParent().setCalculated(table, "from");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                }
                else if(!getParent().isCalculated(table, "from"))
                {
                    getParent().setCalculated(table, "from");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                }
            }
            else if(getParent().dependsFromWaiting(table, dependence))
            {
                if(getParent().onChangeSubmit("from"))
                {
                    getParent().setCalculated(table, "from");
                }
                else
                {
                    getParent().clear(table, "from");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                }
            }
            else
            {    
                this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                                
                if(getParent().onChangeSubmit("from"))
                {
                    getParent().setWaiting(table, "from");
                    getParent().calculateFormula(new Hashtable(table), "from");
                    //getParent().setCalculated(table, "from");
                }                
            }
        }

        public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
        {
        	
            this.setValue(newval, recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL); 
            
        	//setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
            

            if( recalc && getParent().onChangeSubmit("from"))
            {
                Hashtable table = new Hashtable();
                getParent().setCalculated(table, "from");
                getParent().calculateFormula(table, "from");
            }
            //vou verificar se o objecto se encontra numa bridge
			if ( getParent().p_parentBridgeRow != null )
	        {
				getParent().getParentBridgeRow().getBridge().lineChanged("from");
            }
		 }

        private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
        {
            this.setValue(newval, AttributeHandler.INPUT_FROM_INTERNAL);
            //setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
            if( getParent().onChangeSubmit("from"))
            {
                getParent().setCalculated(table, "from");
                getParent().calculateFormula(table, "from");
            }
            
            
			//vou verificar se o objecto se encontra numa bridge
			if ( getParent().p_parentBridgeRow != null )
	        {
				getParent().getParentBridgeRow().getBridge().lineChanged("from");
            }



        }
        
        

        
public  String defaultValue() throws boRuntimeException {try {
long assValue = ((netgest.bo.controller.xwf.XwfController)getEboContext().getController()).getRuntimeActivity().getAttribute("assignedQueue").getValueLong();
return assValue == -1 ? boConvertUtils.convertToString(getEboContext().getBoSession().getPerformerBoui(), this):String.valueOf(assValue); 
}
catch (Exception e){}
return null;}
public  boolean haveDefaultValue() {
return true;
}
public  String formula() throws boRuntimeException {
return null;
}



        
        
    }
    
    public final class HandlerCREATOR extends ObjAttHandler {
        public HandlerCREATOR(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("CREATOR"));
        }

        
        public void setValueString(String value) {
            try {
                this.setValue(boConvertUtils.convertToBigDecimal(value,this));
                Hashtable table = new Hashtable();
                if(getParent().onChangeSubmit("CREATOR"))
                {
                    getParent().setCalculated(table, "CREATOR");
                    getParent().calculateFormula(table, "CREATOR");
                }
            } catch (Exception e) {
                super.setInvalid(e.getMessage(),value);
            }
        }
        public String getValueString() throws boRuntimeException {
            try {
                return boConvertUtils.convertToString(getValue(),this);
            } catch (Exception e) {
                String[] args = {getParent().getName(),"CREATOR"};
                throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
            }
        }
        public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
        {
            if(getParent().alreadyCalculated(table, dependence)) 
            {                       
                if(getParent().isWaiting(table, "CREATOR"))
                {
                    getParent().setCalculated(table, "CREATOR");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                }
                else if(!getParent().isCalculated(table, "CREATOR"))
                {
                    getParent().setCalculated(table, "CREATOR");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                }
            }
            else if(getParent().dependsFromWaiting(table, dependence))
            {
                if(getParent().onChangeSubmit("CREATOR"))
                {
                    getParent().setCalculated(table, "CREATOR");
                }
                else
                {
                    getParent().clear(table, "CREATOR");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                }
            }
            else
            {    
                this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                                
                if(getParent().onChangeSubmit("CREATOR"))
                {
                    getParent().setWaiting(table, "CREATOR");
                    getParent().calculateFormula(new Hashtable(table), "CREATOR");
                    //getParent().setCalculated(table, "CREATOR");
                }                
            }
        }

        public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
        {
        	
            this.setValue(newval, recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL); 
            
        	//setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
            

            if( recalc && getParent().onChangeSubmit("CREATOR"))
            {
                Hashtable table = new Hashtable();
                getParent().setCalculated(table, "CREATOR");
                getParent().calculateFormula(table, "CREATOR");
            }
            //vou verificar se o objecto se encontra numa bridge
			if ( getParent().p_parentBridgeRow != null )
	        {
				getParent().getParentBridgeRow().getBridge().lineChanged("CREATOR");
            }
		 }

        private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
        {
            this.setValue(newval, AttributeHandler.INPUT_FROM_INTERNAL);
            //setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
            if( getParent().onChangeSubmit("CREATOR"))
            {
                getParent().setCalculated(table, "CREATOR");
                getParent().calculateFormula(table, "CREATOR");
            }
            
            
			//vou verificar se o objecto se encontra numa bridge
			if ( getParent().p_parentBridgeRow != null )
	        {
				getParent().getParentBridgeRow().getBridge().lineChanged("CREATOR");
            }



        }
        
        

        
public  String defaultValue() throws boRuntimeException {
try {
return boConvertUtils.convertToString(getEboContext().getBoSession().getPerformerBoui(), this);
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
    
    
   
    

   
    public final class Handlerreassigned extends AttributeHandler {
        private boolean isdbbinding=true;
        private BigDecimal fieldvalue;
        public Handlerreassigned(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("reassigned"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(Handlerreassigned.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getBigDecimal("REASSIGNED");
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
                    if(getParent().isWaiting(table, "reassigned"))
                    {
                       getParent().setCalculated(table, "reassigned");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "reassigned"))
                    {
                        getParent().setCalculated(table, "reassigned");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("reassigned"))
                    {
                        getParent().setCalculated(table, "reassigned"); 
                    }
                    else
                    {
                        getParent().clear(table, "reassigned"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("reassigned"))
                    {
                        getParent().setWaiting(table, "reassigned");
                        getParent().calculateFormula(new Hashtable(table), "reassigned");
                        //getParent().setCalculated(table, "reassigned");
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
	            	throw new boRuntimeException(Handlerreassigned.class.getName() +
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
	                        
	                        if( recalc && getParent().onChangeSubmit("reassigned"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "reassigned");
	                            getParent().calculateFormula(table, "reassigned");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("reassigned");
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
	            	throw new boRuntimeException(Handlerreassigned.class.getName() +
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
                        
                        if(getParent().onChangeSubmit("reassigned"))
                        {
                            getParent().setCalculated(table, "reassigned");
                            getParent().calculateFormula(table, "reassigned");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("reassigned");
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
                    this.getParent().getDataRow().updateBigDecimal("REASSIGNED",newval);
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
	
	public boolean onBeforeSave(boEvent event) throws boRuntimeException {
	return netgest.xwf.events.xwfActivityEvent.beforeSaveReassign( this );
}

    
    
}
