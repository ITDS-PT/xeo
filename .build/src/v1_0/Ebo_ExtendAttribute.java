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



public  class Ebo_ExtendAttribute extends boObject implements  Serializable {  

   
   
    public Handlerdecimals decimals;    
    public HandlerminDecimals minDecimals;    
    public HandlerminNumber minNumber;    
    public HandlermaxNumber maxNumber;    
    public Handlergrouping grouping;    
   
    public netgest.bo.runtime.attributes.boAttributeString alias;    
    public netgest.bo.runtime.attributes.boAttributeString shortAlias;    
    public netgest.bo.runtime.attributes.boAttributeNumber attributeRequire;    
    public netgest.bo.runtime.attributes.boAttributeNumber attributeConstraints;    
    public netgest.bo.runtime.attributes.boAttributeNumber attributeCardinal;    
    public netgest.bo.runtime.attributes.boAttributeNumber attributeType;    
    public netgest.bo.runtime.attributes.boAttributeString changed;    
    public netgest.bo.runtime.attributes.boAttributeString quickView;    
    public netgest.bo.runtime.attributes.boAttributeObject object;    
    public netgest.bo.runtime.attributes.boAttributeObject lov;    
    public netgest.bo.runtime.attributes.boAttributeString valueText;    
    public netgest.bo.runtime.attributes.boAttributeNumber valueNumber;    
    public netgest.bo.runtime.attributes.boAttributeDate valueDate;    
    public netgest.bo.runtime.attributes.boAttributeDate valueDateTime;    
    public netgest.bo.runtime.attributes.boAttributeString valueBoolean;    
    public netgest.bo.runtime.attributes.boAttributeString valueLov;    
    public netgest.bo.runtime.attributes.boAttributeObject valueObject;    
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
     
    public Ebo_ExtendAttribute() {
        super();
        bo_version      = "1.0";
        bo_name         = "Ebo_ExtendAttribute";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("Ebo_ExtendAttribute");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
        
        atts.add(new boBridgeMasterAttribute(this,this.getBoDefinition().getAttributeRef("valueList"))); 

       
        atts.add(decimals = new Handlerdecimals(this));
        
        atts.add(minDecimals = new HandlerminDecimals(this));
        
        atts.add(minNumber = new HandlerminNumber(this));
        
        atts.add(maxNumber = new HandlermaxNumber(this));
        
        atts.add(grouping = new Handlergrouping(this));
        
        
       
        atts.add(alias = new netgest.bo.runtime.attributes.boAttributeString(this,"alias"));
        
        atts.add(shortAlias = new netgest.bo.runtime.attributes.boAttributeString(this,"shortAlias"));
        
        atts.add(attributeRequire = new netgest.bo.runtime.attributes.boAttributeNumber(this,"attributeRequire"));
        
        atts.add(attributeConstraints = new netgest.bo.runtime.attributes.boAttributeNumber(this,"attributeConstraints"));
        
        atts.add(attributeCardinal = new netgest.bo.runtime.attributes.boAttributeNumber(this,"attributeCardinal"));
        
        atts.add(attributeType = new netgest.bo.runtime.attributes.boAttributeNumber(this,"attributeType"));
        
        atts.add(changed = new netgest.bo.runtime.attributes.boAttributeString(this,"changed"));
        
        atts.add(quickView = new netgest.bo.runtime.attributes.boAttributeString(this,"quickView"));
        
        atts.add(object = new netgest.bo.runtime.attributes.boAttributeObject(this,"object"));
        
        atts.add(lov = new netgest.bo.runtime.attributes.boAttributeObject(this,"lov"));
        
        atts.add(valueText = new netgest.bo.runtime.attributes.boAttributeString(this,"valueText"));
        
        atts.add(valueNumber = new netgest.bo.runtime.attributes.boAttributeNumber(this,"valueNumber"));
        
        atts.add(valueDate = new netgest.bo.runtime.attributes.boAttributeDate(this,"valueDate"));
        
        atts.add(valueDateTime = new netgest.bo.runtime.attributes.boAttributeDate(this,"valueDateTime"));
        
        atts.add(valueBoolean = new netgest.bo.runtime.attributes.boAttributeString(this,"valueBoolean"));
        
        atts.add(valueLov = new netgest.bo.runtime.attributes.boAttributeString(this,"valueLov"));
        
        atts.add(valueObject = new netgest.bo.runtime.attributes.boAttributeObject(this,"valueObject"));
        
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
           
           
                
                    if( decimals.haveDefaultValue()) 
                    {
                    	if(decimals.defaultValue() != null)
                    	{
                        	decimals._setValue(boConvertUtils.convertToBigDecimal(decimals.defaultValue(), decimals));
                        }
                        else
                        {
                        	decimals._setValue(null);
                        }
                        decimals.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
                
                    if( minDecimals.haveDefaultValue()) 
                    {
                    	if(minDecimals.defaultValue() != null)
                    	{
                        	minDecimals._setValue(boConvertUtils.convertToBigDecimal(minDecimals.defaultValue(), minDecimals));
                        }
                        else
                        {
                        	minDecimals._setValue(null);
                        }
                        minDecimals.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
                
                    if( minNumber.haveDefaultValue()) 
                    {
                    	if(minNumber.defaultValue() != null)
                    	{
                        	minNumber._setValue(boConvertUtils.convertToBigDecimal(minNumber.defaultValue(), minNumber));
                        }
                        else
                        {
                        	minNumber._setValue(null);
                        }
                        minNumber.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
                
                    if( maxNumber.haveDefaultValue()) 
                    {
                    	if(maxNumber.defaultValue() != null)
                    	{
                        	maxNumber._setValue(boConvertUtils.convertToBigDecimal(maxNumber.defaultValue(), maxNumber));
                        }
                        else
                        {
                        	maxNumber._setValue(null);
                        }
                        maxNumber.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
                
                    if( grouping.haveDefaultValue()) 
                    {
                    	if(grouping.defaultValue() != null)
                    	{
                        	grouping._setValue(boConvertUtils.convertToString(grouping.defaultValue(), grouping));
                        }
                        else
                        {
                        	grouping._setValue(null);
                        }
                        grouping.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                    }
                
            
           
                if( alias.haveDefaultValue()) 
                {
                	if(alias.defaultValue() != null)
                	{
                    	alias._setValue(boConvertUtils.convertToString(alias.defaultValue(), alias));
                    }
                    else
                    {
                    	alias._setValue(null);
                    }
                    alias.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( shortAlias.haveDefaultValue()) 
                {
                	if(shortAlias.defaultValue() != null)
                	{
                    	shortAlias._setValue(boConvertUtils.convertToString(shortAlias.defaultValue(), shortAlias));
                    }
                    else
                    {
                    	shortAlias._setValue(null);
                    }
                    shortAlias.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( attributeRequire.haveDefaultValue()) 
                {
                	if(attributeRequire.defaultValue() != null)
                	{
                    	attributeRequire._setValue(boConvertUtils.convertToBigDecimal(attributeRequire.defaultValue(), attributeRequire));
                    }
                    else
                    {
                    	attributeRequire._setValue(null);
                    }
                    attributeRequire.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( attributeConstraints.haveDefaultValue()) 
                {
                	if(attributeConstraints.defaultValue() != null)
                	{
                    	attributeConstraints._setValue(boConvertUtils.convertToBigDecimal(attributeConstraints.defaultValue(), attributeConstraints));
                    }
                    else
                    {
                    	attributeConstraints._setValue(null);
                    }
                    attributeConstraints.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( attributeCardinal.haveDefaultValue()) 
                {
                	if(attributeCardinal.defaultValue() != null)
                	{
                    	attributeCardinal._setValue(boConvertUtils.convertToBigDecimal(attributeCardinal.defaultValue(), attributeCardinal));
                    }
                    else
                    {
                    	attributeCardinal._setValue(null);
                    }
                    attributeCardinal.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( attributeType.haveDefaultValue()) 
                {
                	if(attributeType.defaultValue() != null)
                	{
                    	attributeType._setValue(boConvertUtils.convertToBigDecimal(attributeType.defaultValue(), attributeType));
                    }
                    else
                    {
                    	attributeType._setValue(null);
                    }
                    attributeType.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( changed.haveDefaultValue()) 
                {
                	if(changed.defaultValue() != null)
                	{
                    	changed._setValue(boConvertUtils.convertToString(changed.defaultValue(), changed));
                    }
                    else
                    {
                    	changed._setValue(null);
                    }
                    changed.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( quickView.haveDefaultValue()) 
                {
                	if(quickView.defaultValue() != null)
                	{
                    	quickView._setValue(boConvertUtils.convertToString(quickView.defaultValue(), quickView));
                    }
                    else
                    {
                    	quickView._setValue(null);
                    }
                    quickView.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( object.haveDefaultValue()) 
                {
                	if(object.defaultValue() != null)
                	{
                    	object._setValue(boConvertUtils.convertToBigDecimal(object.defaultValue(), object));
                    }
                    else
                    {
                    	object._setValue(null);
                    }
                    object.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( lov.haveDefaultValue()) 
                {
                	if(lov.defaultValue() != null)
                	{
                    	lov._setValue(boConvertUtils.convertToBigDecimal(lov.defaultValue(), lov));
                    }
                    else
                    {
                    	lov._setValue(null);
                    }
                    lov.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( valueText.haveDefaultValue()) 
                {
                	if(valueText.defaultValue() != null)
                	{
                    	valueText._setValue(boConvertUtils.convertToString(valueText.defaultValue(), valueText));
                    }
                    else
                    {
                    	valueText._setValue(null);
                    }
                    valueText.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( valueNumber.haveDefaultValue()) 
                {
                	if(valueNumber.defaultValue() != null)
                	{
                    	valueNumber._setValue(boConvertUtils.convertToBigDecimal(valueNumber.defaultValue(), valueNumber));
                    }
                    else
                    {
                    	valueNumber._setValue(null);
                    }
                    valueNumber.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( valueDate.haveDefaultValue()) 
                {
                	if(valueDate.defaultValue() != null)
                	{
                    	valueDate._setValue(boConvertUtils.convertToTimestamp(valueDate.defaultValue(), valueDate));
                    }
                    else
                    {
                    	valueDate._setValue(null);
                    }
                    valueDate.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( valueDateTime.haveDefaultValue()) 
                {
                	if(valueDateTime.defaultValue() != null)
                	{
                    	valueDateTime._setValue(boConvertUtils.convertToTimestamp(valueDateTime.defaultValue(), valueDateTime));
                    }
                    else
                    {
                    	valueDateTime._setValue(null);
                    }
                    valueDateTime.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( valueBoolean.haveDefaultValue()) 
                {
                	if(valueBoolean.defaultValue() != null)
                	{
                    	valueBoolean._setValue(boConvertUtils.convertToString(valueBoolean.defaultValue(), valueBoolean));
                    }
                    else
                    {
                    	valueBoolean._setValue(null);
                    }
                    valueBoolean.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( valueLov.haveDefaultValue()) 
                {
                	if(valueLov.defaultValue() != null)
                	{
                    	valueLov._setValue(boConvertUtils.convertToString(valueLov.defaultValue(), valueLov));
                    }
                    else
                    {
                    	valueLov._setValue(null);
                    }
                    valueLov.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( valueObject.haveDefaultValue()) 
                {
                	if(valueObject.defaultValue() != null)
                	{
                    	valueObject._setValue(boConvertUtils.convertToBigDecimal(valueObject.defaultValue(), valueObject));
                    }
                    else
                    {
                    	valueObject._setValue(null);
                    }
                    valueObject.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
    
            if("Ebo_ExtendAttribute".equals(this.getName()))
            {
                calculateFormula(null);
            }
        }
    }
   
    public bridgeHandler valueList()
    {
        return getBridge("valueList");        
    }
    
    
    public bridgeHandler getBridge( String bridgeName ) 
    {
        boBridgesArray    brig = super.getBridges();        	
        bridgeHandler     ret  = brig.get(bridgeName);
        if( ret == null )
        {
        
        	if( bridgeName.equals("valueList") && (ret=brig.get("valueList"))==null )
        	{
            	brig.add( ret=new BridgeHandlervalueList(new DataResultSet( getDataRow().getRecordChild( getEboContext() ,"valueList") ) , this ) );        
        	}
        	if( ret == null )
        	{
				ret = super.getBridge( bridgeName );
        	}
        }
        return ret;
    }
   
    
    
    public final class BridgeHandlervalueList extends bridgeHandler {
        private DataResultSet p_node;
        private String        p_fatherfield;
        private String        p_childfield;
        private boObject      p_parent;

        
        public BridgeHandlervalueList(DataResultSet data,boObject parent) {
            super("valueList",
                  data,
                  parent.getBoDefinition().getAttributeRef("valueList").getBridge().getChildFieldName(),
                  parent
                 );

            p_fatherfield = parent.getBoDefinition().getAttributeRef("valueList").getBridge().getFatherFieldName();
            p_childfield  = parent.getBoDefinition().getAttributeRef("valueList").getBridge().getChildFieldName();
            
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

                  if(getParent().onChangeSubmit("valueList"))
                  {
                      getParent().calculateFormula("valueList");
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
					if(getParent().onChangeSubmit("valueList"))
					{
					   getParent().calculateFormula("valueList");
					}

                    att.fireAfterChangeEvent( att, chgval, null);
//					att.onAfterRemove(new boEvent(att,att.EVENT_AFTER_REMOVE,chgval));
//					att.onAfterChange(new boEvent(att,att.EVENT_AFTER_CHANGE,chgval,null));
                    
				}
           
            return ret;
        }




		public boBridgeRow createRow( netgest.bo.data.DataRow row )
		{
			return new BridgeHanldervalueListRow( this, row );
		}

		public final class BridgeHanldervalueListRow extends boBridgeRow
		{
			public BridgeHanldervalueListRow( bridgeHandler bridge, netgest.bo.data.DataRow row )
			{
				super( bridge , row );
				addLine();
			}

			public void addLine() 
			{
				boAttributesArray xatt = super.getLineAttributes();
			
				if(this.getAttribute("valueList")==null)
					xatt.add( new HandlervalueList( getParent(), this ) );
				
		
			
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



        
       
        public final class HandlervalueList extends BridgeObjAttributeHandler {
            public HandlervalueList( boObject parent , boBridgeRow bridgerow ) {
                super( parent , bridgerow , bridgerow.getBridge().getDefAttribute().getName().equals("valueList")?parent.getBoDefinition().getAttributeRef("valueList"):bridgerow.getBridge().getDefAttribute().getBridge().getAttributeRef("valueList") );
            }
            public void setValueString(String value) {
                try {
                    this.setValue(boConvertUtils.convertToBigDecimal(value,this));
                    if(getParent().onChangeSubmit("valueList"))
                    {
                        Hashtable table = new Hashtable();                    
                        getParent().setCalculated(table, "valueList");
                        getParent().calculateFormula(table, "valueList");
                    }
                } catch (Exception e) {
                    super.setInvalid(e.getMessage(),value);
                }
            }
            public String getValueString() throws boRuntimeException {
                try {
                    return boConvertUtils.convertToString(this.getValue(),this);
                } catch (Exception e) {
                    String[] args = {getParent().getName(),"valueList"};
                    throw new boRuntimeException(this.getClass().getName()+".load(EboContext,long)","BO-3003",e,args);
                }
            }
            
            public void setValueFormula(Hashtable table, String[]dependence) throws boRuntimeException
            {
                if(getParent().alreadyCalculated(table, dependence)) 
                {                       
                    if(getParent().isWaiting(table, "valueList"))
                    {
                       getParent().setCalculated(table, "valueList");
                       this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "valueList"))
                    {
                        getParent().setCalculated(table, "valueList");
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("valueList"))
                    {
                        getParent().setCalculated(table, "valueList"); 
                    }
                    else
                    {
                        getParent().clear(table, "valueList"); 
                        this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                }
                else
                {   
                    this.setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    if(getParent().onChangeSubmit("valueList"))
                    {                 
                        getParent().setWaiting(table, "valueList");
                        getParent().calculateFormula(new Hashtable(table), "valueList");
                        //getParent().setCalculated(table, "valueList");
                    }                    
                }
            }

            public void setValue(BigDecimal newval, boolean recalc) throws boRuntimeException 
            {
                this.setValue(newval, recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL);
	        	
	        	//setInputType( recalc ? AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL );
	        	
                if( recalc && getParent().onChangeSubmit("valueList"))
                {
                    Hashtable table = new Hashtable();
                    getParent().setCalculated(table, "valueList");
                    getParent().calculateFormula(table, "valueList");
                }
        
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("valueList");
				}
            }

            private void setValue(BigDecimal newval, Hashtable table) throws boRuntimeException 
            {
                this.setValue(newval, AttributeHandler.INPUT_FROM_INTERNAL);
                //setInputType( AttributeHandler.INPUT_FROM_INTERNAL );
                if( getParent().onChangeSubmit("valueList"))
                {
                    getParent().setCalculated(table, "valueList");
                    getParent().calculateFormula(table, "valueList");
                }
                
				//vou verificar se o objecto se encontra numa bridge
				if ( getParent().p_parentBridgeRow != null )
				{
					getParent().getParentBridgeRow().getBridge().lineChanged("valueList");
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
    
    

   
    public final class Handlerdecimals extends AttributeHandler {
        private boolean isdbbinding=true;
        private BigDecimal fieldvalue;
        public Handlerdecimals(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("decimals"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(Handlerdecimals.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getBigDecimal("DECIMALS");
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
                    if(getParent().isWaiting(table, "decimals"))
                    {
                       getParent().setCalculated(table, "decimals");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "decimals"))
                    {
                        getParent().setCalculated(table, "decimals");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("decimals"))
                    {
                        getParent().setCalculated(table, "decimals"); 
                    }
                    else
                    {
                        getParent().clear(table, "decimals"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("decimals"))
                    {
                        getParent().setWaiting(table, "decimals");
                        getParent().calculateFormula(new Hashtable(table), "decimals");
                        //getParent().setCalculated(table, "decimals");
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
	            	throw new boRuntimeException(Handlerdecimals.class.getName() +
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
	                        
	                        if( recalc && getParent().onChangeSubmit("decimals"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "decimals");
	                            getParent().calculateFormula(table, "decimals");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("decimals");
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
	            	throw new boRuntimeException(Handlerdecimals.class.getName() +
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
                        
                        if(getParent().onChangeSubmit("decimals"))
                        {
                            getParent().setCalculated(table, "decimals");
                            getParent().calculateFormula(table, "decimals");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("decimals");
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
                    this.getParent().getDataRow().updateBigDecimal("DECIMALS",newval);
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

        
public  boolean disableWhen() throws boRuntimeException {    if(!"4".equals( this.getParent().getAttribute("attributeType").getValueString()))
    {
this.setValueObject(null);return true;
    }return false;}
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
    }
    
    public final class HandlerminDecimals extends AttributeHandler {
        private boolean isdbbinding=true;
        private BigDecimal fieldvalue;
        public HandlerminDecimals(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("minDecimals"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(HandlerminDecimals.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getBigDecimal("MINDECIMALS");
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
                    if(getParent().isWaiting(table, "minDecimals"))
                    {
                       getParent().setCalculated(table, "minDecimals");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "minDecimals"))
                    {
                        getParent().setCalculated(table, "minDecimals");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("minDecimals"))
                    {
                        getParent().setCalculated(table, "minDecimals"); 
                    }
                    else
                    {
                        getParent().clear(table, "minDecimals"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("minDecimals"))
                    {
                        getParent().setWaiting(table, "minDecimals");
                        getParent().calculateFormula(new Hashtable(table), "minDecimals");
                        //getParent().setCalculated(table, "minDecimals");
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
	            	throw new boRuntimeException(HandlerminDecimals.class.getName() +
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
	                        
	                        if( recalc && getParent().onChangeSubmit("minDecimals"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "minDecimals");
	                            getParent().calculateFormula(table, "minDecimals");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("minDecimals");
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
	            	throw new boRuntimeException(HandlerminDecimals.class.getName() +
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
                        
                        if(getParent().onChangeSubmit("minDecimals"))
                        {
                            getParent().setCalculated(table, "minDecimals");
                            getParent().calculateFormula(table, "minDecimals");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("minDecimals");
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
                    this.getParent().getDataRow().updateBigDecimal("MINDECIMALS",newval);
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

        
public  boolean disableWhen() throws boRuntimeException {    if(!"4".equals( this.getParent().getAttribute("attributeType").getValueString()))
    {
this.setValueObject(null);return true;
    }return false;}
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
    }
    
    public final class HandlerminNumber extends AttributeHandler {
        private boolean isdbbinding=true;
        private BigDecimal fieldvalue;
        public HandlerminNumber(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("minNumber"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(HandlerminNumber.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getBigDecimal("MINNUMBER");
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
                    if(getParent().isWaiting(table, "minNumber"))
                    {
                       getParent().setCalculated(table, "minNumber");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "minNumber"))
                    {
                        getParent().setCalculated(table, "minNumber");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("minNumber"))
                    {
                        getParent().setCalculated(table, "minNumber"); 
                    }
                    else
                    {
                        getParent().clear(table, "minNumber"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("minNumber"))
                    {
                        getParent().setWaiting(table, "minNumber");
                        getParent().calculateFormula(new Hashtable(table), "minNumber");
                        //getParent().setCalculated(table, "minNumber");
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
	            	throw new boRuntimeException(HandlerminNumber.class.getName() +
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
	                        
	                        if( recalc && getParent().onChangeSubmit("minNumber"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "minNumber");
	                            getParent().calculateFormula(table, "minNumber");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("minNumber");
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
	            	throw new boRuntimeException(HandlerminNumber.class.getName() +
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
                        
                        if(getParent().onChangeSubmit("minNumber"))
                        {
                            getParent().setCalculated(table, "minNumber");
                            getParent().calculateFormula(table, "minNumber");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("minNumber");
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
                    this.getParent().getDataRow().updateBigDecimal("MINNUMBER",newval);
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

        
public  boolean disableWhen() throws boRuntimeException {    if(!"4".equals( this.getParent().getAttribute("attributeType").getValueString()))
    {
this.setValueObject(null);return true;
    }return false;}
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
    }
    
    public final class HandlermaxNumber extends AttributeHandler {
        private boolean isdbbinding=true;
        private BigDecimal fieldvalue;
        public HandlermaxNumber(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("maxNumber"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public BigDecimal getValue() throws boRuntimeException {
                BigDecimal ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(HandlermaxNumber.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getBigDecimal("MAXNUMBER");
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
                    if(getParent().isWaiting(table, "maxNumber"))
                    {
                       getParent().setCalculated(table, "maxNumber");
                       setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "maxNumber"))
                    {
                        getParent().setCalculated(table, "maxNumber");
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("maxNumber"))
                    {
                        getParent().setCalculated(table, "maxNumber"); 
                    }
                    else
                    {
                        getParent().clear(table, "maxNumber"); 
                        setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToBigDecimal(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("maxNumber"))
                    {
                        getParent().setWaiting(table, "maxNumber");
                        getParent().calculateFormula(new Hashtable(table), "maxNumber");
                        //getParent().setCalculated(table, "maxNumber");
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
	            	throw new boRuntimeException(HandlermaxNumber.class.getName() +
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
	                        
	                        if( recalc && getParent().onChangeSubmit("maxNumber"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "maxNumber");
	                            getParent().calculateFormula(table, "maxNumber");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("maxNumber");
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
	            	throw new boRuntimeException(HandlermaxNumber.class.getName() +
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
                        
                        if(getParent().onChangeSubmit("maxNumber"))
                        {
                            getParent().setCalculated(table, "maxNumber");
                            getParent().calculateFormula(table, "maxNumber");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("maxNumber");
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
                    this.getParent().getDataRow().updateBigDecimal("MAXNUMBER",newval);
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

        
public  boolean disableWhen() throws boRuntimeException {    if(!"4".equals( this.getParent().getAttribute("attributeType").getValueString()))
    {
this.setValueObject(null);return true;
    }return false;}
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
    }
    
    public final class Handlergrouping extends AttributeHandler {
        private boolean isdbbinding=true;
        private String fieldvalue;
        public Handlergrouping(boObject parent) {
            super(parent,parent.getBoDefinition().getAttributeRef("grouping"));
        }
        
        public String getValueString() throws boRuntimeException {
            return boConvertUtils.convertToString(this.getValue(),this);
        }
        
            public String getValue() throws boRuntimeException {
                String ret = null;
                
                if (getParent().isCheckSecurity() && !this.hasRights())
	            	throw new boRuntimeException(Handlergrouping.class.getName() +
	        	".getValue()", "BO-3230", null, "");                      	                   	                                 

                fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
                if(isdbbinding) {
                    ret = this.getParent().getDataRow().getString("GROUPING");
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
                    if(getParent().isWaiting(table, "grouping"))
                    {
                       getParent().setCalculated(table, "grouping");
                       setValue(boConvertUtils.convertToString(formula(), this), false );
                    }
                    else if(!getParent().isCalculated(table, "grouping"))
                    {
                        getParent().setCalculated(table, "grouping");
                        setValue(boConvertUtils.convertToString(formula(), this), table );
                    }
                }
                else if(getParent().dependsFromWaiting(table, dependence))
                {
                    if(getParent().onChangeSubmit("grouping"))
                    {
                        getParent().setCalculated(table, "grouping"); 
                    }
                    else
                    {
                        getParent().clear(table, "grouping"); 
                        setValue(boConvertUtils.convertToString(formula(), this), false );
                    } 
                }
                else
                {    
                    setValue(boConvertUtils.convertToString(formula(), this), false );                                    
                    if(getParent().onChangeSubmit("grouping"))
                    {
                        getParent().setWaiting(table, "grouping");
                        getParent().calculateFormula(new Hashtable(table), "grouping");
                        //getParent().setCalculated(table, "grouping");
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
	            	throw new boRuntimeException(Handlergrouping.class.getName() +
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
	                        
	                        if( recalc && getParent().onChangeSubmit("grouping"))
	                        {
	                            Hashtable table = new Hashtable();
	                            getParent().setCalculated(table, "grouping");
	                            getParent().calculateFormula(table, "grouping");
	                        }
						    //vou verificar se o objecto se encontra numa bridge
							if ( getParent().p_parentBridgeRow != null )
							{
								getParent().getParentBridgeRow().getBridge().lineChanged("grouping");
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
	            	throw new boRuntimeException(Handlergrouping.class.getName() +
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
                        
                        if(getParent().onChangeSubmit("grouping"))
                        {
                            getParent().setCalculated(table, "grouping");
                            getParent().calculateFormula(table, "grouping");
                        }

                        //vou verificar se o objecto se encontra numa bridge
						if ( getParent().p_parentBridgeRow != null )
						{
							getParent().getParentBridgeRow().getBridge().lineChanged("grouping");
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
                    this.getParent().getDataRow().updateString("GROUPING",newval);
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

        
public  boolean disableWhen() throws boRuntimeException {    if(!"4".equals( this.getParent().getAttribute("attributeType").getValueString()))
    {
this.setValueObject("0");return true;
    }return false;}
public  boolean haveDefaultValue() {
return false;
}
public  String formula() throws boRuntimeException {
return null;
}



        
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
return new String[] {"attributeType"} ;
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
	
	public boolean onBeforeDestroy(boEvent event) throws boRuntimeException {
	return netgest.bo.workflow.WFUpdateEvents.removeExtendAttr(this);
}

    
    
}
