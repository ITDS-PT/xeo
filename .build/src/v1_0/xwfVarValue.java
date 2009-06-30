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



public  class xwfVarValue extends boObject implements  Serializable {  

   
   
   
    public netgest.bo.runtime.attributes.boAttributeNumber type;    
    public netgest.bo.runtime.attributes.boAttributeString linkVar;    
    public netgest.bo.runtime.attributes.boAttributeString linkAttribute;    
    public netgest.bo.runtime.attributes.boAttributeObject object;    
    public netgest.bo.runtime.attributes.boAttributeNumber minoccurs;    
    public netgest.bo.runtime.attributes.boAttributeNumber maxoccurs;    
    public netgest.bo.runtime.attributes.boAttributeString valueText;    
    public netgest.bo.runtime.attributes.boAttributeString valueClob;    
    public netgest.bo.runtime.attributes.boAttributeNumber valueNumber;    
    public netgest.bo.runtime.attributes.boAttributeDate valueDate;    
    public netgest.bo.runtime.attributes.boAttributeDate valueDateTime;    
    public netgest.bo.runtime.attributes.boAttributeString valueBoolean;    
    public netgest.bo.runtime.attributes.boAttributeString valueLov;    
    public netgest.bo.runtime.attributes.boAttributeObject valueObject;    
    public netgest.bo.runtime.attributes.boAttributeString unique_sid;    
    public netgest.bo.runtime.attributes.boAttributeNumber program;    
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
   
     
    public xwfVarValue() {
        super();
        bo_version      = "1.0";
        bo_name         = "xwfVarValue";
        bo_classregboui = "#BO.CLSREGBOUI#";
        bo_definition   = boDefHandler.getBoDefinition("xwfVarValue");
        bo_statemanager = bo_definition.getBoClsState() != null ? bo_definition.getBoClsState().getStateManager( this ) : null;  

        boAttributesArray atts = super.getAttributes();
        boAttributesArray stat = super.getStateAttributes();

       
        
        
        atts.add(new boBridgeMasterAttribute(this,this.getBoDefinition().getAttributeRef("valueList"))); 

       
        
       
        atts.add(type = new netgest.bo.runtime.attributes.boAttributeNumber(this,"type"));
        
        atts.add(linkVar = new netgest.bo.runtime.attributes.boAttributeString(this,"linkVar"));
        
        atts.add(linkAttribute = new netgest.bo.runtime.attributes.boAttributeString(this,"linkAttribute"));
        
        atts.add(object = new netgest.bo.runtime.attributes.boAttributeObject(this,"object"));
        
        atts.add(minoccurs = new netgest.bo.runtime.attributes.boAttributeNumber(this,"minoccurs"));
        
        atts.add(maxoccurs = new netgest.bo.runtime.attributes.boAttributeNumber(this,"maxoccurs"));
        
        atts.add(valueText = new netgest.bo.runtime.attributes.boAttributeString(this,"valueText"));
        
        atts.add(valueClob = new netgest.bo.runtime.attributes.boAttributeString(this,"valueClob"));
        
        atts.add(valueNumber = new netgest.bo.runtime.attributes.boAttributeNumber(this,"valueNumber"));
        
        atts.add(valueDate = new netgest.bo.runtime.attributes.boAttributeDate(this,"valueDate"));
        
        atts.add(valueDateTime = new netgest.bo.runtime.attributes.boAttributeDate(this,"valueDateTime"));
        
        atts.add(valueBoolean = new netgest.bo.runtime.attributes.boAttributeString(this,"valueBoolean"));
        
        atts.add(valueLov = new netgest.bo.runtime.attributes.boAttributeString(this,"valueLov"));
        
        atts.add(valueObject = new netgest.bo.runtime.attributes.boAttributeObject(this,"valueObject"));
        
        atts.add(unique_sid = new netgest.bo.runtime.attributes.boAttributeString(this,"unique_sid"));
        
        atts.add(program = new netgest.bo.runtime.attributes.boAttributeNumber(this,"program"));
        
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
           
           
           
                if( type.haveDefaultValue()) 
                {
                	if(type.defaultValue() != null)
                	{
                    	type._setValue(boConvertUtils.convertToBigDecimal(type.defaultValue(), type));
                    }
                    else
                    {
                    	type._setValue(null);
                    }
                    type.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( linkVar.haveDefaultValue()) 
                {
                	if(linkVar.defaultValue() != null)
                	{
                    	linkVar._setValue(boConvertUtils.convertToString(linkVar.defaultValue(), linkVar));
                    }
                    else
                    {
                    	linkVar._setValue(null);
                    }
                    linkVar.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( linkAttribute.haveDefaultValue()) 
                {
                	if(linkAttribute.defaultValue() != null)
                	{
                    	linkAttribute._setValue(boConvertUtils.convertToString(linkAttribute.defaultValue(), linkAttribute));
                    }
                    else
                    {
                    	linkAttribute._setValue(null);
                    }
                    linkAttribute.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( minoccurs.haveDefaultValue()) 
                {
                	if(minoccurs.defaultValue() != null)
                	{
                    	minoccurs._setValue(boConvertUtils.convertToBigDecimal(minoccurs.defaultValue(), minoccurs));
                    }
                    else
                    {
                    	minoccurs._setValue(null);
                    }
                    minoccurs.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
                }
            
                if( maxoccurs.haveDefaultValue()) 
                {
                	if(maxoccurs.defaultValue() != null)
                	{
                    	maxoccurs._setValue(boConvertUtils.convertToBigDecimal(maxoccurs.defaultValue(), maxoccurs));
                    }
                    else
                    {
                    	maxoccurs._setValue(null);
                    }
                    maxoccurs.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( valueClob.haveDefaultValue()) 
                {
                	if(valueClob.defaultValue() != null)
                	{
                    	valueClob._setValue(boConvertUtils.convertToString(valueClob.defaultValue(), valueClob));
                    }
                    else
                    {
                    	valueClob._setValue(null);
                    }
                    valueClob.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
                if( unique_sid.haveDefaultValue()) 
                {
                	if(unique_sid.defaultValue() != null)
                	{
                    	unique_sid._setValue(boConvertUtils.convertToString(unique_sid.defaultValue(), unique_sid));
                    }
                    else
                    {
                    	unique_sid._setValue(null);
                    }
                    unique_sid.setInputType( AttributeHandler.INPUT_FROM_DEFAULT );
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
            
    
            if("xwfVarValue".equals(this.getName()))
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
