/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.math.BigDecimal;
import java.sql.SQLException;
import netgest.bo.data.DataRow;
import netgest.bo.def.boDefAttribute;

public abstract class BridgeObjAttributeHandler extends ObjAttHandler implements boIBridgeAttribute {

    private boBridgeRow p_bridgerow;
    private boDefAttribute p_def;
    private String p_fn;
    
    public BridgeObjAttributeHandler( boObject parent , boBridgeRow bridgerow , boDefAttribute def ) 
    {
        
        super(parent,def);
        p_def = def;
        p_bridgerow = bridgerow;
        p_fn = (def == bridgerow.getBridge().getDefAttribute())?bridgerow.getBridge().getDefAttribute().getBridge().getChildFieldName():def.getDbName();
        
    }

    public bridgeHandler getBridge() 
    {
        return p_bridgerow.getBridge();
    }
    
    public void _setValue(BigDecimal boui) throws boRuntimeException 
    {
//        try {

            BigDecimal chgval = this.getValue();
            if( chgval != null || boui != null )
            {
                checkParent( chgval, boui );
            }
//            int line =p_bridge.getRslt().getRow(); 
//            this.p_bridge.getRslt().absolute(this.p_line);
//            this.p_bridge.getRslt().updateBigDecimal(p_fn,boui);
//            this.p_bridge.getRslt().updateRow();
//            if(boui!= null && boui.longValue()!=0)
//                p_bridge.p_vl=false;

            p_bridgerow.getDataRow().updateBigDecimal( p_fn, boui );

//            p_bridge.getRslt().absolute(line);    
//        } catch (SQLException e) {
//            String[] args = {p_fn,p_def.getName()};
//            throw new boRuntimeException(boObject.class.getName()+"set(long)","BO-3002",e,args);
//        }
    }

    public BigDecimal getValue() throws boRuntimeException 
    {
        return p_bridgerow.getDataRow().getBigDecimal( p_fn );
        
//        try {
//            int line =p_bridge.getRslt().getRow(); 
//            this.p_bridge.getRslt().absolute(this.p_line);
//            BigDecimal ret = this.p_bridge.getRslt().getBigDecimal(p_fn);
//            p_bridge.getRslt().absolute(line);
//            return ret;
//        } catch (SQLException e) {
//            String[] args = {p_fn,p_def.getName()};
//            throw new boRuntimeException(boObject.class.getName()+"get()","BO-3003",e,args);
//        }
    }

    public String getName()
    {
        // TODO:  Override this netgest.bo.runtime.AttributeHandler method
        return getBridge().getDefAttribute().getName()+"."+super.getName()+"."+getLine();
    }
    public Object getValueObject() throws boRuntimeException 
    {
        return this.getValue();
    }

    public void setValueObject(Object value, byte type) throws boRuntimeException 
    {
        this.setValue((BigDecimal)value, type);
    }
    
    public void setValueObject(Object value) throws boRuntimeException 
    {
        setValueObject(value, AttributeHandler.INPUT_FROM_USER);   
    }

    public boObjectList edit() throws boRuntimeException
    {
        // TODO:  Override this netgest.bo.runtime.ObjAttHandler method
         boObjectList toRet=super.edit();
         toRet.getObject().setParentBridgeRow( this.p_bridgerow );
        return toRet;
    }
    
    
    
    public byte getInputType(  )
    {
//        try
//        {
            if( p_attinputtype == -1 )
            {
                DataRow row = p_bridgerow.getDataRow();
                p_attinputtype = boObjectUtils.getAttributeInputType( row , getName() );
            }
            return p_attinputtype;
//        }
//        catch (SQLException e)
//        {
//            throw new RuntimeException ( "SQLException reading attribute type:\n "+e.getMessage() );    
//        }
        
    }
    public int getLine()
    {
        return p_bridgerow.getLine();
    }
    
    
}
