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



public  class xwfActivityTransfer extends xwfActivity implements  Serializable {  

   
    public Handlerprogram program;       
   
   
    public netgest.bo.runtime.attributes.boAttributeObject assignedQueue;    
    public netgest.bo.runtime.attributes.boAttributeObject performer;    
    public netgest.bo.runtime.attributes.boAttributeObject to;    
    public netgest.bo.runtime.attributes.boAttributeString transfered;    
    public netgest.bo.runtime.attributes.boAttributeString errormessage;    
   
     
    public xwfActivityTransfer() {
        super();
        bo_version      = "1.0";
        bo_name         = "xwfActivityTransfer";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("xwfActivityTransfer");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        atts.add(program = new Handlerprogram(this));
        
        
       

       
        
       
        atts.add(assignedQueue = new netgest.bo.runtime.attributes.boAttributeObject(this,"assignedQueue"));
        
        atts.add(performer = new netgest.bo.runtime.attributes.boAttributeObject(this,"performer"));
        
        atts.add(to = new netgest.bo.runtime.attributes.boAttributeObject(this,"to"));
        
        atts.add(transfered = new netgest.bo.runtime.attributes.boAttributeString(this,"transfered"));
        
        atts.add(errormessage = new netgest.bo.runtime.attributes.boAttributeString(this,"errormessage"));
        



       
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
           
                
                    if( program.haveDefaultValue()) 
                    {
                    	if(program.defaultValue() != null)
                    	{
                        	program._setValue(verifyTransformer(program,boConvertUtils.convertToBigDecimal(program.defaultValue(), program)));
                        }
                        else
                        {
                       		program._setValue(null);
                       	}
                        program.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( transfered.haveDefaultValue()) 
                {
                	if(transfered.defaultValue() != null)
                	{
                    	transfered._setValue(boConvertUtils.convertToString(transfered.defaultValue(), transfered));
                    }
                    else
                    {
                    	transfered._setValue(null);
                    }
                    transfered.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
    
            if("xwfActivityTransfer".equals(this.getName()))
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
   
    public final class Handlerprogram extends ObjAttHandler {
        public Handlerprogram(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("program"));
        }

        
        public void setValueString(String value) {
            try {
                this.setValue(boConvertUtils.convertToBigDecimal(value,this));
                Hashtable table = new Hashtable();
                if(getParent().onChangeSubmit("program"))
                {
                    getParent().setCalculated(table, "program");
                    getParent().calculateFormula(table, "program");
                }
            } catch (Exception e) {
                super.setInvalid(e.getMessage(),value);
            }
        }
        public String getValueString() throws boRuntimeException {
            try {
                return boConvertUtils.convertToString(getValue(),this);
            } catch (Exception e) {
                String[] args = {getParent().getName(),"program"};
                throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
            }
        }
        public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
        {
            if(getParent().alreadyCalculated(table, dependence)) 
            {                       
                if(getParent().isWaiting(table, "program"))
                {
                    getParent().setCalculated(table, "program");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                }
                else if(!getParent().isCalculated(table, "program"))
                {
                    getParent().setCalculated(table, "program");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                }
            }
            else if(getParent().dependsFromWaiting(table, dependence))
            {
                if(getParent().onChangeSubmit("program"))
                {
                    getParent().setCalculated(table, "program");
                }
                else
                {
                    getParent().clear(table, "program");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                }
            }
            else
            {    
                this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                                
                if(getParent().onChangeSubmit("program"))
                {
                    getParent().setWaiting(table, "program");
                    getParent().calculateFormula(new Hashtable(table), "program");
                    //getParent().setCalculated(table, "program");
                }                
            }
        }

        public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
        {
        	
            this.setValue(newval, recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL); 
            
        	//setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
            

            if( recalc && getParent().onChangeSubmit("program"))
            {
                Hashtable table = new Hashtable();
                getParent().setCalculated(table, "program");
                getParent().calculateFormula(table, "program");
            }
            //vou verificar se o objecto se encontra numa bridge
			if ( getParent().p_parentBridgeRow != null )
	        {
				getParent().getParentBridgeRow().getBridge().lineChanged("program");
            }
		 }

        private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
        {
            this.setValue(newval, AttributeHandler.INPUT_FROM_INTERNAL);
            //setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
            if( getParent().onChangeSubmit("program"))
            {
                getParent().setCalculated(table, "program");
                getParent().calculateFormula(table, "program");
            }
            
            
			//vou verificar se o objecto se encontra numa bridge
			if ( getParent().p_parentBridgeRow != null )
	        {
				getParent().getParentBridgeRow().getBridge().lineChanged("program");
            }



        }
        
        

        
public  boolean disableWhen() throws boRuntimeException {try
    {
        if(this.getEboContext().getController() instanceof netgest.bo.controller.xwf.XwfController)
        {
            return true;
        }
        else if(this.getParent().exists() && this.getParent().getAttribute("program").getObject() != null)
        {
            return true;
        }
    }
    catch(Exception e)
    {}
    return false;}
public  String defaultValue() throws boRuntimeException {try
{
	if(this.getEboContext().getController() instanceof netgest.bo.controller.xwf.XwfController)
	{
		return boConvertUtils.convertToString(((netgest.bo.controller.xwf.XwfController)this.getEboContext().getController()).getRuntimeProgramBoui(), this);
	}
}
catch(Exception e){}
return null;}
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
	return netgest.xwf.events.xwfActivityEvent.beforeSaveTransferActivity( this );
}

    
    
}
