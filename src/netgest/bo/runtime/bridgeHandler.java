/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.io.Serializable;
import java.math.BigDecimal;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import netgest.bo.data.DataResultSet;
import netgest.bo.data.DataRow;
import netgest.bo.data.DataSet;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.security.securityRights;

    public abstract class bridgeHandler extends boObjectList {
    
        private String          p_name;
        private DataResultSet   p_data;
        private boObject        p_parent;
        private boDefAttribute  p_defatt;
        private List         	p_rows = new ArrayList();
        
        public bridgeHandler(String name,DataResultSet data, String childfield, boObject parent ) {
            super(parent.getEboContext(),
                    data,
                    parent,parent.getBoDefinition().getAttributeRef(name).getReferencedObjectDef(),
                    parent.getBoDefinition().getAttributeRef(name).getBridge().getChildFieldName(),
                    name,
                    parent.getAttribute(name),
                    boObjectList.FORMAT_MANY, true);
                    
            this.p_defatt = parent.getBoDefinition().getAttributeRef(name);
            this.p_name=name;
            this.p_data= data;
            this.p_parent=parent;
            super.setObjectContainer(this.getParent());
        }

        public bridgeHandler(String name,DataResultSet data, String childfield, boObject parent, boolean useSecurity ) {
            super(parent.getEboContext(),
                    data,
                    parent,parent.getBoDefinition().getAttributeRef(name).getReferencedObjectDef(),
                    parent.getBoDefinition().getAttributeRef(name).getBridge().getChildFieldName(),
                    name,
                    parent.getAttribute(name),
                    boObjectList.FORMAT_MANY, useSecurity);
                    
            this.p_defatt = parent.getBoDefinition().getAttributeRef(name);
            this.p_name=name;
            this.p_data= data;
            this.p_parent=parent;
            super.setObjectContainer(this.getParent());
        }

        public bridgeHandler(String name,DataResultSet node,String childfield,boObject parent,boDefHandler refobj,boDefAttribute att,String fieldname) {
            super(parent.getEboContext(),
                    node,
                    parent,
                    refobj,
                    fieldname,
                    name,
                    parent.getAttribute(name),
                    boObjectList.FORMAT_MANY, true);

            this.p_defatt = att;
            this.p_name=name;
            this.p_data=node;
            this.p_parent=parent;
            super.setObjectContainer(this.getParent());
        }

        public bridgeHandler(String name,DataResultSet node,String childfield,boObject parent,boDefHandler refobj,boDefAttribute att,String fieldname, boolean useSecurity) {
            super(parent.getEboContext(),
                    node,
                    parent,
                    refobj,
                    fieldname,
                    name,
                    parent.getAttribute(name),
                    boObjectList.FORMAT_MANY, useSecurity);

            this.p_defatt = att;
            this.p_name=name;
            this.p_data=node;
            this.p_parent=parent;
            super.setObjectContainer(this.getParent());
        }

        public DataResultSet getRslt(){
            return p_data;
        }
        
        public boBridgeRow rows( int rowidx )
        {
            return (boBridgeRow)p_rows.get( rowidx - 1 );
        }
        
        public boAttributesArray getAllAttributes() {

            boAttributesArray ret  = new boAttributesArray();
            for( int i = 1 ; i <= p_rows.size() ; i++ ) 
            {
                Enumeration atts = rows( i ).getLineAttributes().elements();
                while(atts.hasMoreElements()) {
                    ret.add((AttributeHandler)atts.nextElement());
                }
            }
            return ret;
        }

        public String getAttributeName(){
            return p_name;
        }

        public void add() throws boRuntimeException 
        {
            add(null);
        }
        public void add(long boui, byte type) throws boRuntimeException 
        {
            add(BigDecimal.valueOf(boui), type);
        }

        public void add(long boui) throws boRuntimeException 
        {
            add(boui, AttributeHandler.INPUT_FROM_USER);
        }
        public void add(long boui,int row, byte type) throws boRuntimeException 
        {
            add(BigDecimal.valueOf(boui), row, type);
        }                
        public void add(long boui,int row) throws boRuntimeException 
        {
            add(boui, row, AttributeHandler.INPUT_FROM_USER);
        }

        public void add(BigDecimal boui) throws boRuntimeException
        {
            add(boui, AttributeHandler.INPUT_FROM_USER);
        }

        public boolean isChanged() {
        	try {
				return 
					p_data.getDataSet().wasChanged()
					|| 
					super.containsChangedObjects();
				
			} catch (Exception e) {
				throw new RuntimeException( e );
			}
        }

        public abstract void add(BigDecimal boui, byte type) throws boRuntimeException;
        public abstract void add(BigDecimal boui,int row, byte type) throws boRuntimeException;
        public abstract boolean remove() throws boRuntimeException;
        
//        public boolean remove() throws boRuntimeException 
//        {
//            boolean ret = false;
////            if(!this.p_vl && this.getRowCount() > 0 ) {
//            if( this.p_rows.size() > 0 ) 
//            {    
//             //   BigDecimal xvalue = this.getValue(); // Save the current value.
//                boBridgeRow rowtodelete = rows( this.getRow() );
//                
//                rowtodelete.setValue(null);  // Try to remove the row
//                
//                if( rowtodelete.getValue() == null )  // Succeeded
//                {
//                    Enumeration  lineatts = rowtodelete.getLineAttributes().elements();
//                    _remove( rowtodelete );  // remove the line
//                    while( lineatts.hasMoreElements() )
//                    {
//                        AttributeHandler att = ( AttributeHandler )lineatts.nextElement();
//                        if ( att.getValueObject() != null )
//                        {
//                            att.setValueObject( null );
//                        }
//                    }
//                    //TOREVIEW MUDEI  A POSICAO PARA CIMA - JMF
//                    //_remove( rowtodelete );  // remove the line
//                    ret = true;
//                }
//            }
//            else
//            {
//                logger.warn("Trying to remove line from empty Bridge ","bridgeHandler","remove");
//            }
//
//            //calculateFormulas
//            if(this.getParent().onChangeSubmit(this.getName()))
//            {
//                this.getParent().calculateFormula(this.getName()); 
//            }
//            return ret;
//        }
        
        public int getRowPos( boBridgeRow row )
        {
            int toRet = p_rows.indexOf( row ) ;
            return toRet+1;
        }
        
        public boBridgeRow getRow( long bouiToSearch ) throws boRuntimeException
        {
            boBridgeRow toRet = null;
            try
            {
                DataResultSet data = this.getRslt();
                int pos = data.getRow();
                data.beforeFirst();
                
                String ls_childFieldName = p_defatt.getBridge().getChildFieldName();
                while (data.next())
                {
                    if (data.getLong( ls_childFieldName ) == bouiToSearch )
                    {
                        toRet = rows( data.getRow() );
                        break;
                    }
                }
    
                data.absolute(pos);
            }
            catch (SQLException e)
            {
                throw new boRuntimeException("bridgeHandler.getRow Searching boui ->"+bouiToSearch, "BO-3999", e);
            }
            return toRet;
        }
        
        
        public void _remove( boBridgeRow bridgerow ) throws boRuntimeException 
        {
            if( getRowPos( bridgerow ) > 0 )
            {
                if( p_rows.size() > 0 ) 
                {
                    int row = this.getRow();
                    
                    DataSet dataSet = this.p_data.getDataSet();
                    dataSet.deleteRow( row );
                    p_rows.remove( bridgerow.getLine() - 1 );
                    refreshLineAttributes( row );
                    try
                    {
                        this.p_data.absolute( row );
                    }
                    catch (SQLException e)
                    {
                        
                    }
                    
                }
            }
        }
        

        public boolean hasRights() throws boRuntimeException
        {
            long performer=p_parent.getEboContext().getBoSession().getPerformerBoui();
            return securityRights.hasRights( this.p_parent, this.p_parent.getName() , p_defatt.getName() , performer  );
        }

        public boDefAttribute getDefAttribute() {
            return p_defatt;
        }
        public boolean valid()
        { //throws boRuntimeException {
        //    try {
              //  int rec = this.p_data.getRow();
                
//                if(p_vl) 
//                {
//                    this.p_data.last();
//                    this.p_data.deleteRow();
//                    p_vl = false;
//                }
/*                String xval;
                boolean ok=true;
                this.p_data.beforeFirst();
                while((!ok || this.p_data.next()) && !this.p_data.isAfterLast() ) {
                    ok = true;
                    for (int i = 0; i < this.p_data.getColumnCount(); i++)  {
                        if(!this.p_data.getColumnName(i+1).equalsIgnoreCase(p_fatherfield)) {
                            if(!((xval=this.p_data.getString(1+i))==null || xval.length()==0)) {
                                ok = false;
                                break;
                            }
                        }
                    }
//                    if(!this.getObject().exists()){
//                        ok=true;
//                        this.p_data.deleteRow();
//                    } else {
//                        ok = true;
//                    }
                }
                this.p_data.absolute(rec);*/
                return true;
//            } catch (SQLException e) {
//                throw new boRuntimeException(this.getClass().getName(),"XXXX",e);
//            }
        }

//        public String getMyOwner() {
//            // TODO:  Override this netgest.bo.runtime.boObjectList method
//            return boPoolManager.getMyOwner( getParent() );
//        }
        public boolean isEmpty()
        {
            return this.getRowCount()==0; 
        }

    public void beforeFirst()
    {
        // TODO:  Override this netgest.bo.runtime.boObjectList method
        super.beforeFirst();
    }

    public boolean first()
    {
        // TODO:  Override this netgest.bo.runtime.boObjectList method
        boolean ret= super.first();
        return ret;
    }

    public boolean last()
    {
        // TODO:  Override this netgest.bo.runtime.boObjectList method
        boolean ret = super.last();
        return ret;
    }

    public boolean next()
    {
        // TODO:  Override this netgest.bo.runtime.boObjectList method
        boolean ret= super.next();
        return ret;
    }
    
    public boolean moveTo(int recno)
    {
        // TODO:  Override this netgest.bo.runtime.boObjectList method
        boolean ret= super.moveTo( recno );
        return ret;
    }
    public void truncate() throws boRuntimeException
    {
        while( this.getRowCount() > 0 )
        {
            this.moveTo( this.getRowCount() );
            this.remove();
        }
    }
    
    
    public void moveRowTo( int row ) throws boRuntimeException 
    {
        if( row != this.getRow() )
        {
            int crow = this.getRow()-1;
            row--;
            
            boBridgeRow brow = rows( this.getRow() );

            BridgeObjAttributeHandler att = (BridgeObjAttributeHandler)brow.getAttribute( this.getDefAttribute().getName() );
            if( att.onBeforeChangeOrder(new boEvent( this , AttributeHandler.EVENT_BEFORE_CHANGEORDER , new Integer( crow + 1 ) , new Integer( row + 1 ) ) ) )
            {
                p_rows.add( row  , p_rows.remove( crow  ) );
                p_data.moveRowTo( row + 1 );
                
                refreshLineAttributes( Math.min( row + 1, crow + 1 ) );
                

//                Object xx = p_lineatts.get( crow );
//                
//                boolean ascend =  crow > row;
//                
//                int max = Math.max( crow , row );
//                int i;
//                for (i = Math.min( row , crow ) ; i < max ; i++ )
//                {
//                    if( ascend )
//                    {
//                        p_lineatts.set( max - (i - row) , p_lineatts.get( max - ( i - row )  - 1 ) );
//                    }
//                    else 
//                    {
//                        p_lineatts.set( i , p_lineatts.get( i+1 ) );
//                    }
//                    
//                    refreshLineAttributes( ascend ? max - (i - row) : i );
//                }
//                if( ascend )
//                {
//                    p_lineatts.set( row , xx );
//                }
//                else
//                {
//                    p_lineatts.set( i , xx );
//                }
                
                att.onAfterChangeOrder(new boEvent( this , AttributeHandler.EVENT_BEFORE_CHANGEORDER , new Integer( crow + 1 ) , new Integer( row + 1 ) ) );
            }
        }
    }
    
    protected void refreshLineAttributes( int startIdx )
    {
        for (int i = startIdx; i <= p_rows.size(); i++) 
        {
            rows( i ).refreshLineAttributes( i );
        }
        
//        boAttributesArray newlineatt = new boAttributesArray();
//        Enumeration enum2 = ( ( boAttributesArray )p_lineatts.get( row ) ).elements();
//        while( enum2.hasMoreElements() )
//        {
//            boIBridgeAttribute batt =  ( boIBridgeAttribute )enum2.nextElement();
//            batt.setLine( row + 1 );
//            newlineatt.add( (AttributeHandler)batt );
//        }
//        p_lineatts.set( row , newlineatt );
    }
    
    
    public boObject addNewObject() throws boRuntimeException {
        return addNewObject(this.getDefAttribute().getReferencedObjectName());
    }
    public boObject addNewObject(String objname) throws boRuntimeException {

        boObject ret = boObject.getBoManager().createObject(getEboContext(),objname);
        this.add(ret.getBoui());
        rows( this.getRowCount() ).postAddBridge();

        return ret;
    }

    public EboContext getEboContext()
    {
        // TODO:  Override this netgest.bo.system.boPoolable method
        return p_parent.getEboContext();
    }
    public String getName() 
    {
        return p_name;
    }
 
    public boObject getParent()
    {
      return this.p_parent;
    }

    public void setParent(boObject parent)
    {
      this.p_parent=parent;
    }
 
    public void lineChanged(String attName) throws boRuntimeException
    {
        String aux = attName != null ? getName() + "." + attName : getName();
        if (getParent().onChangeSubmit(aux))
        {
            getParent().calculateFormula(aux);
        }
    }
    
    public AttributeHandler getAttribute( String attname )
    {
        return rows( this.getRow() ).getAttribute( this.getName() + "." + attname + "." + this.getRow() );
    }
    
    public boAttributesArray getLineAttributes()
    {
        return rows( this.getRow() ).getLineAttributes();
    }
    
    public BigDecimal getValue() throws boRuntimeException
    {
        return rows( this.getRow() ).getValue();
    }
    public long getValueLong() throws boRuntimeException
    {
        return rows( this.getRow() ).getValueLong();
    }
    public void setValue( long value ) throws boRuntimeException
    {
        rows( this.getRow() ).setValue( value );
    }
    
    public boObjectList edit()  throws boRuntimeException
    {
        return rows( this.getRow() ).edit();
    }
    

    public boolean validate() throws boRuntimeException
    {
        boolean ret = true;
        boDefXeoCode code = p_defatt.getValid();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addVariableString( "message", null );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this.getParent().getAttribute( this.getName() ) );
                eval.eval();
                ret = eval.getReturnBoolean();
                if( !ret )
                {
                    this.getParent().addErrorMessage( this.getParent().getAttribute( this.getName() ), String.valueOf( eval.getVariable("message") ) );
                }
            }
        }
        return ret;
    }
    public boolean required() throws boRuntimeException
    {   
        boolean ret = false;
        boDefXeoCode code = p_defatt.getRequired();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this.getParent().getAttribute( this.getName() ) );
                eval.eval();
                ret = eval.getReturnBoolean();
            }
            else
            {
                return code.getBooleanValue();
            }
        }
        return ret;
    }
    public boolean disableWhen() throws boRuntimeException
    {
        boolean ret = false;
        boDefXeoCode code = p_defatt.getDisableWhen();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this.getParent().getAttribute( this.getName() ) );
                eval.eval();
                ret = eval.getReturnBoolean();
            }
            else
            {
                return code.getBooleanValue();
            }
        }
        return ret;
    }
    public boolean hiddenWhen() throws boRuntimeException
    {
        boolean ret = false;
        boDefXeoCode code = p_defatt.getHiddenWhen();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this.getParent().getAttribute( this.getName() ) );
                eval.eval();
                ret = eval.getReturnBoolean();
            }
            else
            {
                return code.getBooleanValue();
            }
        }
        return ret;
    }
    
    public boolean canChangeLov() throws boRuntimeException
    {
        return false;
    }
    
    public String defaultValue() throws boRuntimeException
    {
        String ret = null;
        boDefXeoCode code = p_defatt.getDefaultValue();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this.getParent().getAttribute( this.getName() ) );
                Object evalRet = eval.eval();
                if( evalRet != null )
                {
                    ret = String.valueOf( evalRet );
                }
            }
        }
        return ret;
    }

    public String formula() throws boRuntimeException
    {
        String ret = null;
        boDefXeoCode code = p_defatt.getFormula();
        if( code != null )
        {
            if( code.getLanguage() == boDefXeoCode.LANG_XEP )
            {
                boXEPEval eval      = new boXEPEval( code, this.getEboContext() );
                eval.addThisObject( this.getParent() );
                eval.addObjectAttributeValue( "value", this.getParent().getAttribute( this.getName() ) );
                Object evalRet      = eval.eval();
                if( evalRet != null )
                {
                    ret = String.valueOf( evalRet );
                }
            }
        }
        return ret;
    }
    public String[] condition() throws boRuntimeException
    {
        //TODO:Lov Condition
        return null;
    }
    
    public boolean haveDefaultValue()
    {
        boDefXeoCode code = p_defatt.getDefaultValue();
        if( code != null )
        {
            return true;
        }
        return false;
    }
    
/*    //to Override

    public boolean validate() throws boRuntimeException
    {
        return true;
    }
    public boolean required() throws boRuntimeException
    {
        return false;
    }
    public boolean disableWhen() throws boRuntimeException
    {
        return false;
    }
    public boolean hiddenWhen() throws boRuntimeException
    {
        return false;
    }
    public String defaultValue() throws boRuntimeException
    {
        return null;
    }
    public boolean haveDefaultValue()
    {
        return false;
    }
    
    public String[] condition() throws boRuntimeException
    {
        //TODO:Lov Condition
        return null;
    }
    public boolean canChangeLov() throws boRuntimeException
    {
        return false;
    }
    public String formula() throws boRuntimeException
    {
        return null;
    }*/
    
//    
//    public abstract boolean validate() throws boRuntimeException;     
//    public abstract boolean required() throws boRuntimeException;
//    public abstract boolean disableWhen() throws boRuntimeException;
//    public abstract boolean hiddenWhen() throws boRuntimeException;
//    public abstract String defaultValue() throws boRuntimeException;
//    public abstract String[] condition() throws boRuntimeException;
//    public abstract boolean canChangeLov() throws boRuntimeException;
//    public abstract String formula() throws boRuntimeException;
//    
    
    public boBridgeRow createRow()
    {
        return createRow( p_data.getDataSet().createRow() );
    }
    public abstract boBridgeRow createRow( DataRow row );
    
    
    public void insertRow( boBridgeRow row )
    {
        p_rows.add( row );
        p_data.getDataSet().insertRow( row.getDataRow() );
        row.refreshLineAttributes( p_rows.size() );
    }
    
    public void refreshBridgeData()
    {
        DataSet dataSet = p_data.getDataSet();
        boBridgeRow brow;
        int i = 1;
        for (; i <= dataSet.getRowCount(); i++) 
        {
            if( p_rows.size() < i )
            {
                p_rows.add( brow=createRow( dataSet.rows( i ) ) );
                brow.refreshLineAttributes( p_rows.size() );
            }
            else
            {
                rows( i ).setDataRow( dataSet.rows( i ) );
                rows( i ).refreshLineAttributes( i );
            }
        }
        while( p_rows.size() >= i )
        {
            p_rows.remove( p_rows.size() - 1 );
        }
    }

    public boObject getObject() throws boRuntimeException
    {
        boObject ret = super.getObject();
        if ( this.getRowCount() > 0 )
        {
            ret.p_parentBridgeRow =  rows( this.getRow() );
        }
        return ret;
    }

//    public boObject getObject(boolean modeEditTemplate) throws boRuntimeException
//    {
//        boObject ret = super.getObject( modeEditTemplate );
//        if( ret != null )
//        {
//            ret.setParentBridgeRow( rows( this.getRow() ) );
//            getParent().getThread().add( new BigDecimal( this.getParent().getBoui()), new BigDecimal( ret.getBoui() ) );
//        }
//        return ret;
//    }
    
    public boBridgeIterator iterator()
    {
        return new boBridgeIterator( this );
    }

//    public synchronized void refreshBridgeData() throws boRuntimeException
//    {
//        // TODO:  Override this netgest.bo.runtime.boObjectList method
//        try
//        {
//            int pos = p_data.getRow();
//            p_data.beforeFirst();
//            while( p_data.next() )
//            {
//                if( p_lineatts.size() < p_data.getRow() )
//                {
//                    this.addLine();
//                }
//            }
//            while( p_lineatts.size() > p_data.getRowCount() )
//            {
//                p_lineatts.remove( p_data.getRowCount() );
//            }
//            p_data.absolute( pos );
//        }
//        catch (SQLException e)
//        {
//            throw new RuntimeException("Error refreshing bridge data in ["+p_parent.getName()+"."+this.getName()+"]\n"
//                                        +e.getClass().getName()+"\n"
//                                        +e.getMessage()
//                                      );            
//        }
//    }
}