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



public  class messageFax extends message implements  Serializable {  

   
    public HandlerletterFormat letterFormat;       
   
    public HandlerpreferedMedia preferedMedia;    
   
   
     
    public messageFax() {
        super();
        bo_version      = "1.0";
        bo_name         = "messageFax";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("messageFax");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        atts.add(letterFormat = new HandlerletterFormat(this));
        
        
       

       
        atts.add(preferedMedia = new HandlerpreferedMedia(this));
        
        
       



       
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
           
                
                    if( letterFormat.haveDefaultValue()) 
                    {
                    	if(letterFormat.defaultValue() != null)
                    	{
                        	letterFormat._setValue(verifyTransformer(letterFormat,boConvertUtils.convertToBigDecimal(letterFormat.defaultValue(), letterFormat)));
                        }
                        else
                        {
                       		letterFormat._setValue(null);
                       	}
                        letterFormat.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
           
                
                    if( preferedMedia.haveDefaultValue()) 
                    {
                    	if(preferedMedia.defaultValue() != null)
                    	{
                        	preferedMedia._setValue(boConvertUtils.convertToString(preferedMedia.defaultValue(), preferedMedia));
                        }
                        else
                        {
                        	preferedMedia._setValue(null);
                        }
                        preferedMedia.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
           
    
            if("messageFax".equals(this.getName()))
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
   
    public final class HandlerletterFormat extends ObjAttHandler {
        public HandlerletterFormat(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("letterFormat"));
        }

        
        public void setValueString(String value) {
            try {
                this.setValue(boConvertUtils.convertToBigDecimal(value,this));
                Hashtable table = new Hashtable();
                if(getParent().onChangeSubmit("letterFormat"))
                {
                    getParent().setCalculated(table, "letterFormat");
                    getParent().calculateFormula(table, "letterFormat");
                }
            } catch (Exception e) {
                super.setInvalid(e.getMessage(),value);
            }
        }
        public String getValueString() throws boRuntimeException {
            try {
                return boConvertUtils.convertToString(getValue(),this);
            } catch (Exception e) {
                String[] args = {getParent().getName(),"letterFormat"};
                throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
            }
        }
        public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
        {
            if(getParent().alreadyCalculated(table, dependence)) 
            {                       
                if(getParent().isWaiting(table, "letterFormat"))
                {
                    getParent().setCalculated(table, "letterFormat");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                }
                else if(!getParent().isCalculated(table, "letterFormat"))
                {
                    getParent().setCalculated(table, "letterFormat");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                }
            }
            else if(getParent().dependsFromWaiting(table, dependence))
            {
                if(getParent().onChangeSubmit("letterFormat"))
                {
                    getParent().setCalculated(table, "letterFormat");
                }
                else
                {
                    getParent().clear(table, "letterFormat");
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                }
            }
            else
            {    
                this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                                
                if(getParent().onChangeSubmit("letterFormat"))
                {
                    getParent().setWaiting(table, "letterFormat");
                    getParent().calculateFormula(new Hashtable(table), "letterFormat");
                    //getParent().setCalculated(table, "letterFormat");
                }                
            }
        }

        public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
        {
        	
            this.setValue(newval, recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL); 
            
        	//setInputType( recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
            

            if( recalc && getParent().onChangeSubmit("letterFormat"))
            {
                Hashtable table = new Hashtable();
                getParent().setCalculated(table, "letterFormat");
                getParent().calculateFormula(table, "letterFormat");
            }
            //vou verificar se o objecto se encontra numa bridge
			if ( getParent().p_parentBridgeRow != null )
	        {
				getParent().getParentBridgeRow().getBridge().lineChanged("letterFormat");
            }
		 }

        private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
        {
            this.setValue(newval, AttributeHandler.INPUT_FROM_INTERNAL);
            //setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
            if( getParent().onChangeSubmit("letterFormat"))
            {
                getParent().setCalculated(table, "letterFormat");
                getParent().calculateFormula(table, "letterFormat");
            }
            
            
			//vou verificar se o objecto se encontra numa bridge
			if ( getParent().p_parentBridgeRow != null )
	        {
				getParent().getParentBridgeRow().getBridge().lineChanged("letterFormat");
            }



        }
        
        

        
public  boolean disableWhen() throws boRuntimeException {
try {
if (netgest.bo.utils.Calculate.compare(this.getParent().getAttribute("preferedMedia").getValueString(), "Letter", 2)) 
{
return false;
} else {
return true;
}
}
catch (Exception e){}
return false;
}
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
try {
if (netgest.bo.utils.Calculate.compare(this.getParent().getAttribute("preferedMedia").getValueString(), "Letter", 2)) 
{
return boConvertUtils.convertToString(this.getValueObject(), this);
} else {
return null;
}
}
catch (Exception e){}
return null;
}



        
        
    }
    
    
   
    

   
    public final class HandlerpreferedMedia extends AttributeHandler {
        private boolean isdbbinding=true;
        private String fieldvalue;
        public HandlerpreferedMedia(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("preferedMedia"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public String getValue() throws boRuntimeException {
                String ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(HandlerpreferedMedia.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getString("PREFEREDMEDIA");
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
                    if(getParent().isWaiting(table, "preferedMedia"))
                    {
                       getParent().setCalculated(table, "preferedMedia");
                       setValue(boConvertUtils.convertToString(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "preferedMedia"))
                    {
                        getParent().setCalculated(table, "preferedMedia");
                        setValue(boConvertUtils.convertToString(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("preferedMedia"))
                    {
                        getParent().setCalculated(table, "preferedMedia"); 
                    }
                    else
                    {
                        getParent().clear(table, "preferedMedia"); 
                        setValue(boConvertUtils.convertToString(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToString(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("preferedMedia"))
                    {
                        getParent().setWaiting(table, "preferedMedia");
                        getParent().calculateFormula(new Hashtable(table), "preferedMedia");
                        //getParent().setCalculated(table, "preferedMedia");
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
	            	throw new boRuntimeException(HandlerpreferedMedia.class.getName() +
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
	                        
	                        if( recalc && getParent().onChangeSubmit("preferedMedia"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "preferedMedia");
	                            getParent().calculateFormula(table, "preferedMedia");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("preferedMedia");
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
	            	throw new boRuntimeException(HandlerpreferedMedia.class.getName() +
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
                        
                        if(getParent().onChangeSubmit("preferedMedia"))
                        {
                            getParent().setCalculated(table, "preferedMedia");
                            getParent().calculateFormula(table, "preferedMedia");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("preferedMedia");
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
                    this.getParent().getDataRow().updateString("PREFEREDMEDIA",newval);
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
return "Fax";
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
return new String[] {"to", "toRef", "preferedMedia", "letterFormat", "from", "cc", "bcc", "createdReceiveMsg", "faxFormat", "statusMessage"} ;
}
public  void calculateFormula(Hashtable table, String from) throws boRuntimeException {
if ((p_mode != MODE_EDIT_TEMPLATE)) 
{
callObjBridgeCalculate(from);
if ((from != null)) 
{
if (from.equals("preferedMedia")) 
{
this.letterFormat.setValueFormula(table, getDependences("letterFormat"));
}
} else {
this.statusMessage.setValueFormula(new Hashtable() , getDependences("statusMessage"));
this.letterFormat.setValueFormula(new Hashtable() , getDependences("letterFormat"));
}
}
}
	
	
    
    
}
