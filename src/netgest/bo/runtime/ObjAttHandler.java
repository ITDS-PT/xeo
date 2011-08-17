/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.math.BigDecimal;

import java.util.Hashtable;

import netgest.bo.data.DataRow;
import netgest.bo.data.DataSet;
import netgest.bo.data.IXEODataManager;
import netgest.bo.data.XEODataManagerKey;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.impl.Ebo_TemplateImpl;
import netgest.bo.impl.templates.*;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.utils.ClassUtils;

import netgest.bo.system.Logger;

public abstract class ObjAttHandler extends AttributeHandler {
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.ObjAttHandler");
    
    private boObject parent;

    protected String   name;
    protected String   fn;
    
    public ObjAttHandler(boObject parent,boDefAttribute def) {
        super(parent,def);
        this.name=def.getName();
        this.parent = parent;
        this.fn = def.getDbName();
    }
    
    public void setValue(boObject newobj, byte type) throws boRuntimeException 
    {
        if(newobj != null)
            this.setValue(newobj.getBoui(), type);
        else 
            this.setValue((BigDecimal)null, type);
    }
    
    public void setValue(boObject newobj) throws boRuntimeException {
        setValue(newobj, AttributeHandler.INPUT_FROM_USER);
    }
    public Object getValueObject() throws boRuntimeException 
    {
        if( super.getDefAttribute().getDbIsTabled() )
        {
            return this.getValues();
        }
        else
        {
            return this.getValue();
        }
    }

    public void setValueObject(Object value, byte type) throws boRuntimeException 
    {
        if( super.getDefAttribute().getDbIsTabled() )
        {
            this.setValues((BigDecimal[])value, type);
        }
        else
        {
            this.setValue((BigDecimal)value, type);
        }
    }
    
    public void setValueObject(Object value) throws boRuntimeException 
    {
        setValueObject(value, AttributeHandler.INPUT_FROM_USER);
    }

    public void setValue(long boui, byte type) throws boRuntimeException
    {
        this.setValue(BigDecimal.valueOf(boui), type);
    }
    
    public void setValue(long boui) throws boRuntimeException
    {
        setValue(boui, AttributeHandler.INPUT_FROM_USER);
    }

    public void setValue(BigDecimal boui) throws boRuntimeException 
    {
        setValue(boui, AttributeHandler.INPUT_FROM_USER);
        
    }

    public void setValue(BigDecimal boui, byte type) throws boRuntimeException 
    {
//        if( this.getValue() != null ) logger.finest("Antigo :["+this.getName()+"]  " + getParent().getObject( this.getValue().longValue() ).getName() );
//        if( boui != null ) logger.finest("Novo:  ["+this.getName()+"]  " + getParent().getObject( boui.longValue() ).getName() );
        //tranform ao adicionar a brige
        netgest.bo.transformers.Transformer transfClass = null;
        if((transfClass = this.getDefAttribute().getTransformClassMap()) != null)
        {
            if(boui != null && this.getParent().getMode() != boObject.MODE_EDIT_TEMPLATE )
            {
                boui = BigDecimal.valueOf(transfClass.transform(getEboContext(), getParent(), boui.longValue()));
            }
        }

    
        if(!ClassUtils.compare(boui,this.getValue())) 
        {
            BigDecimal chgval = this.getValue();
            BigDecimal newval = boui;

//            boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
//            if(chgval==null && newval != null) 
//            {
//                boEvent event= new boEvent(this,EVENT_BEFORE_ADD,newval); 
//                allow=allow && onBeforeAdd(  event );
//
//            }
//            if(newval==null && chgval!=null) allow = allow && onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));

//            if (allow)
            if ( fireBeforeChangeEvent( this, chgval, newval) )
            {
                if(canAlter(type))
                {
                    _setValue(boui);
                    setInputType( type );
                    calculateFormulas();
               
                    if( chgval != null || boui != null )
                    {
                        checkParent( chgval , boui );
                    }
                    
                    getParent().setChanged(true);
                    
//                    if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
//                    if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
//                    onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                    fireAfterChangeEvent( this, chgval, newval);
                }
            }
        }
    }


    
    public void _setValue(BigDecimal boui) throws boRuntimeException 
    {
        if(!ClassUtils.compare(boui,this.getValue())) {
            parent.getDataRow().updateBigDecimal(this.fn,boui);
        }
    }
    
    public BigDecimal getValue() throws boRuntimeException 
    {
        fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
        BigDecimal ret = parent.getDataRow().getBigDecimal(this.fn);
        if(ret == null)
        {
            //fc:retirei pois acho que não faz sentido...
//            String aux = defaultValue();
//            if(aux != null)
//            {
//                ret = boConvertUtils.convertToBigDecimal(aux, this);
//                parent.getDataRow().updateBigDecimal(this.fn,ret);
//            }
        }
        fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
        
        return ret; 

    }
    
    public void addValueObject( Object value ) throws boRuntimeException
    {
        BigDecimal[] values  = this.getValues();
        BigDecimal[] newvalue;
        BigDecimal xvalue = (BigDecimal)value;
        if( values != null )
        {
            boolean exists = false;
            for (short i = 0; i < values.length; i++) 
            {
                if( values[i].equals( xvalue ) )
                {
                    exists = true;
                }
            }
            if( !exists )
            {
                newvalue = new BigDecimal[values.length];
                System.arraycopy( values , 0 , newvalue , 0 , values.length );
                newvalue[values.length-1] = xvalue;
                setValues( newvalue );
            }
        }
        else
        {
            setValues( new BigDecimal[] { (BigDecimal)value } );
        }
    }

    public void setValues(BigDecimal[] boui, byte type) throws boRuntimeException 
    {
        if(!ClassUtils.compare(boui,this.getValues())) {
            BigDecimal[] chgval = this.getValues();
            BigDecimal[] newval = boui;

            boolean allow=onBeforeChange(new boEvent(this,EVENT_BEFORE_CHANGE,chgval,newval));
            if(chgval==null && newval != null) allow=onBeforeAdd(new boEvent(this,EVENT_BEFORE_ADD,newval));
            if(newval==null && chgval!=null) allow =onBeforeRemove(new boEvent(this,EVENT_BEFORE_REMOVE,chgval));

            if (allow)
            {
                if(canAlter(type))
                {
                    _setValues( boui );
                    setInputType( type );
                    getParent().setChanged(true);
                    if(chgval!=null && newval == null) onAfterRemove(new boEvent(this,EVENT_AFTER_REMOVE,chgval));
                    if(chgval==null && newval != null) onAfterAdd(new boEvent(this,EVENT_AFTER_ADD,newval));
                    onAfterChange(new boEvent(this,EVENT_AFTER_CHANGE,chgval,newval));
                    calculateFormulas();
                }
            }
        }
    }

    public void setValues(BigDecimal[] boui) throws boRuntimeException 
    {
        setValues(boui, AttributeHandler.INPUT_FROM_USER);
    }

    public void _setValues( BigDecimal[] newvalues  ) throws boRuntimeException 
    {
        DataSet xnode =  parent.getDataRow().getChildDataSet( parent.getEboContext(), this.name );
        if( newvalues == null || newvalues.length == 0 )
        {
            while( xnode.getRowCount() > 0 )
            {
                checkParent( xnode.rows( 1 ).getBigDecimal( this.fn ), null );
                xnode.deleteRow( 1 );
            }
        }
        else
        {
            for (int i = 0; i < newvalues.length ; i++) 
            {
                if( xnode.getRowCount() > ( i + 1 ) )
                {
                    if( xnode.rows( i + 1 ).getLong( this.fn ) != newvalues[i].longValue() )
                    {

                        checkParent( xnode.rows( i + 1 ).getBigDecimal( this.fn ) , newvalues[i] );

                        xnode.rows( i + 1 ).updateBigDecimal( this.fn , newvalues[i] );

                    }
                }
                else
                {

                    checkParent( null , newvalues[i] );

                    DataRow row = xnode.createRow();
                    row.updateBigDecimal( this.fn , newvalues[i] );
                    row.updateLong( "T$PARENT$" , getParent().getBoui() );
                    
                    xnode.insertRow( row );
                }
            }
        }
    }
    
    public BigDecimal[] getValues() throws boRuntimeException {
        DataSet xnode =  parent.getDataRow().getChildDataSet( parent.getEboContext(), this.name );
        BigDecimal[] toRet = null;
        if( xnode.getRowCount() > 0 )
        {
            toRet = new BigDecimal[ xnode.getRowCount() ];
            for (int i = 0; i < xnode.getRowCount(); i++) 
            {
                toRet[ i ] = xnode.rows( i+1 ).getBigDecimal(this.fn);
            }
        }
        return toRet;
    }
    
    public long getValueLong() throws boRuntimeException {
        long ret=0;
        BigDecimal val = this.getValue();
        if(val!=null)
            ret = val.longValue();
            
        return ret;
   }
    public boObject[] getObjects() throws boRuntimeException {
        BigDecimal[] values = getValues();
        boObject[] toRet = null;
        if( values != null )
        {
            toRet = new boObject[ values.length ];
            for (short i = 0; i < values.length; i++) 
            {
                   toRet[i] = (values[i] == null || values[i].longValue() == 0 ) ? null : getParent().getObject( values[i].longValue() );  
            }
        }
        return toRet;
    }
    public boObject getObject() throws boRuntimeException {
        if( !this.getDefAttribute().getDbIsTabled() )
        {
        	boDefHandler refDef = getDefAttribute().getReferencedObjectDef();
        	if( this.getValueObject() == null && !refDef.getDataBaseManagerXeoCompatible() ) {
        		
        		IXEODataManager 		dm 	= getEboContext().getApplication().getXEODataManager( refDef );
        		
        		XEODataManagerKey		key = dm.getKeyForAttribute( getEboContext(), getParent(), getDefAttribute() );
        		key.registerKey( getEboContext() );
        		
        		return boObject.getBoManager().loadObject(  getEboContext(), key.getBoui() );
        		
        	}
        	try {
        		return this.getValueLong() == 0 ? null : getParent().getObject(this.getValueLong());
        	}
        	catch( boRuntimeException e ) {
        		throw new boRuntimeException( getParent(), "ObjAttHandler.getObject", "BO-3300", e, 
        				new String[] { 
        					getName(), 
        					getParent().getName(),
        					Long.toString( getParent().getBoui() ),
        					Long.toString( this.getValueLong() )
        				} 
        		);
        	}
        }
        else
        {
            throw new boRuntimeException(this.parent,"getObject()","BO-3024",null,this.name);
        }
    }
    public String getNameAttribute(){
        return name;
    }
    public boObjectList edit() throws boRuntimeException {
        return edit( getDefAttribute().getReferencedObjectName() );
    }

    public boObjectList edit( String objectName ) throws boRuntimeException {
        long refboui = this.getValueLong(); 
        boObjectList xobjlist = this.edit(objectName,refboui);
        xobjlist.first();
        boObject obj = xobjlist.getObject();
        
        boObject objAux = null;
        if("Ebo_Template".equals(obj.getName()) && !"boObject".equals(objectName))
        {            
            objAux = boObject.getBoManager().createObject(obj.getEboContext(),objectName);
            if(!objAux.getBoDefinition().getBoCanBeOrphan())
            {
                objAux.getAttribute("TEMPLATE").setValueLong(obj.getBoui());            
                ((Ebo_TemplateImpl)obj).loadTemplate(objAux);
                obj = objAux;
                xobjlist = this.edit(objectName,objAux.getBoui());
            }
        }
        checkParent( null, BigDecimal.valueOf(obj.getBoui()) , true);
        
//        getParent().getUpdateQueue().add( obj , boObjectUpdateQueue.MODE_SAVE );
        
        obj.poolSetStateFull();
        
        if( !this.getParent().poolIsStateFull() )
        {
            this.getParent().poolSetStateFull();
        }
        
        this.setValue(obj.getBoui());
        if(refboui==0 && !this.getParent().poolIsStateFull()) 
        {
            this.getParent().poolSetStateFull();
        }
        return xobjlist;
    }
    
    protected void postAddBridge() throws boRuntimeException {
    	
    	long refboui = this.getValueLong();
    	
    	boObject obj = getParent().getObject( refboui );
    	
    	String objectName = obj.getName();
    	
        boObject objAux = null;
        if("Ebo_Template".equals(obj.getName()) && !"boObject".equals(objectName))
        {            
            objAux = boObject.getBoManager().createObject(obj.getEboContext(),objectName);
            if(!objAux.getBoDefinition().getBoCanBeOrphan())
            {
                objAux.getAttribute("TEMPLATE").setValueLong(obj.getBoui());            
                ((Ebo_TemplateImpl)obj).loadTemplate(objAux);
                obj = objAux;
            }
        }
        checkParent( null, BigDecimal.valueOf(obj.getBoui()) , true);
        obj.poolSetStateFull();
        if( !this.getParent().poolIsStateFull() )
        {
            this.getParent().poolSetStateFull();
        }
        
        this.setValue(obj.getBoui());
        if(refboui==0 && !this.getParent().poolIsStateFull()) 
        {
            this.getParent().poolSetStateFull();
        }

    }
    
    private boObjectList edit(String objectName, long refboui ) throws boRuntimeException 
    {
        boObjectList xobjlist = boObjectList.edit(this.parent.getEboContext(), objectName , refboui , this.parent , this.name );
        if(!xobjlist.haveBoui(refboui)) 
        {
            if(refboui == 0) // File doesn't have a BOUI yet, so create a new boui for this object.
            {
                xobjlist.inserRow(boObject.getBoManager().createObject(getParent().getEboContext(), objectName ).getBoui());
            }
            else
            {
                xobjlist.inserRow(refboui);
            }
        }   
        return xobjlist;
    }   
//    public void checkParent( BigDecimal oldvalue, BigDecimal newvalue ) throws boRuntimeException
//    {
//        try
//        {
//            boObject obj = null;
//            
//            if( newvalue != null && newvalue.longValue() > 0 )
//            {
//                obj = getParent().getObject( newvalue.longValue() );
//            }
//            else if ( oldvalue != null && oldvalue.longValue() > 0 )
//            {
//                obj = getParent().getObject( oldvalue.longValue() );
//            }
//            
//            if ( obj != null && getDefAttribute().getSetParent()!=boDefAttribute.SET_PARENT_NO && !getName().equals("PARENT") ) 
//            {
//                if ( !getDefAttribute().getChildIsOrphan() || ( getDefAttribute().getSetParent()==boDefAttribute.SET_PARENT_YES ) || !obj.getBoDefinition().getBoCanBeOrphan() )
//                {
//                    if( newvalue != null && newvalue.longValue() > 0 )
//                    {
//                        obj.addParent( getParent() );
//                    }
//                    
//                    //** Work around to avoid problems cloning objects.
//                    //  
//                    //
//                    if( getParent().exists() )
//                    {
//                        if( oldvalue != null && oldvalue.longValue() > 0 )
//                        {
//                           obj.removeParent( getParent() , getDefAttribute().getChildIsOrphan()  );
//                           //obj.removeParent(  );
//                        }
//                    }
//                } 
////                if ( oldvalue != null && !obj.getBoDefinition().getBoCanBeOrphan()  )
////                {
////                    getParent().getUpdateQueue().add( oldvalue.longValue() , boObjectUpdateQueue.MODE_DESTROY );
////                }
//            } 
//            
//        }
//        catch (boRuntimeException e)
//        {
//            if (!(newvalue==null && e.getErrorCode().equals("BO-3015")) ) throw e;
//            else
//            {
//                throw new boRuntimeException2("objAttHandler.checkParent "+e.getClass().getName()+"\n"+e.getMessage());
//            }
//                
//        }
//    }

    private void calculateFormulas() throws boRuntimeException
    {
        if(getParent().onChangeSubmit(this.getName()))
        {
            Hashtable table = new Hashtable();
            getParent().setCalculated(table, this.getName());
            getParent().calculateFormula(table, this.getName());
        } 
    }
    public void checkParent( BigDecimal oldvalue, BigDecimal newvalue ) throws boRuntimeException
    {
        checkParent(oldvalue,newvalue,false);
    }
    private void checkParent( BigDecimal oldvalue, BigDecimal newvalue , boolean fromEditObject ) throws boRuntimeException
    {
            if ( newvalue != null && getDefAttribute().getSetParent()!=boDefAttribute.SET_PARENT_NO && !getName().equals("PARENT") ) 
            {
            boObject objNew = null;
            if( newvalue != null && newvalue.longValue() != 0 )
            {
                objNew = getParent().getObject( newvalue.longValue() );
            }
            if( objNew != null ) {
            
                if ( 
            		!getDefAttribute().getChildIsOrphan() || ( getDefAttribute().getSetParent()==boDefAttribute.SET_PARENT_YES ) 
            		|| 
            		!objNew.getBoDefinition().getBoCanBeOrphan() )
                {
                    parent = getParent();
                    objNew.addParent( parent );
                    if(fromEditObject)
                    {
                        if(boTemplateManager.isParentInTemplateMode(parent))
                        {           
                            if(objNew.getMode() != boObject.MODE_EDIT_TEMPLATE)
                            {
                                boTemplateManager.setTemplatesMode(objNew,false);
                            }
                        }
                        else
                        {
                            AttributeHandler attrHandler = parent.getAttribute("TEMPLATE");
                            if(attrHandler != null)
                            {
                                boObject parentTemplate = attrHandler.getObject();
                                if(parentTemplate != null)
                                {
                                    attrHandler = objNew.getAttribute("TEMPLATE");
                                    if(attrHandler != null)
                                    {
                                        boObject template = attrHandler.getObject();
                                        if(template == null)
                                        {
                                            template = boTemplateManager.getTemplate(parentTemplate,objNew);
                                            if(template != null)
                                            {
                                                objNew.applyTemplate(objNew.getName(),template.getBoui());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }                    
                }
            }

            } 
            
            if ( oldvalue != null && oldvalue.longValue() != 0 ) { 
	            if ( getDefAttribute().getSetParent()!=boDefAttribute.SET_PARENT_NO && !getName().equals("PARENT") ) 
	            {
	            	boObject objOld;
                    objOld = getParent().getObject( oldvalue.longValue() );
                    if( objOld != null ) {
		                if ( !getDefAttribute().getChildIsOrphan() || ( getDefAttribute().getSetParent()==boDefAttribute.SET_PARENT_YES ) || !objOld.getBoDefinition().getBoCanBeOrphan() )
		                {
		                    objOld.removeParent( getParent() , getDefAttribute().getChildIsOrphan()  );
		                }
                    }
                }
            }         
    }    
}
 