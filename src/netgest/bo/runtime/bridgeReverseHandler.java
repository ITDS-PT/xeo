/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.math.BigDecimal;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Enumeration;

import netgest.bo.data.DataResultSet;
import netgest.bo.data.DataRow;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.MessageLocalizer;

import netgest.utils.ClassUtils;

/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 */
public class bridgeReverseHandler extends bridgeHandler {

    private DataResultSet p_node;
    private String p_fatherfield;
    private String p_childfield;
    private String p_childatt;
    private String p_name;
    private String p_objectname;
    private boolean p_isbridge;
    private boObject p_parent;

    private ArrayList p_rows = new ArrayList();

    public static final bridgeReverseHandler create(String name,boObject object,String objectname,String attname) throws boRuntimeException
    {

        boDefHandler def = boDefHandler.getBoDefinition(objectname);
        boDefAttribute xatt = null;

        String fn;
        String father_field;
        String table;
        StringBuffer sql = new StringBuffer();
        boolean isbridge=false;
        if ( objectname.equals("boObject") )
        {
            sql.append("SELECT distinct BOUI,REFBOUI$ FROM OEBO_REFERENCES WHERE EXISTS (SELECT UI$ FROM OEBO_REGISTRY WHERE UI$=OEBO_REFERENCES.BOUI ) AND REFBOUI$=").append(object.bo_boui);
            fn = "BOUI";
            father_field = "REFBOUI$";
            xatt = boDefHandler.getBoDefinition("Ebo_Perf").getAttributeRef("BOUI");
        }
        else
        {
            xatt = def.getAttributeRef(attname);
            if ( xatt.getBridge() != null )
            {
                table = xatt.getBridge().getBoMasterTable();
                fn = xatt.getBridge().getFatherFieldName();
                father_field = xatt.getBridge().getChildFieldName();
                isbridge=true;
            }
            else
            {
                table = def.getBoMasterTable();
                fn = "BOUI";
                father_field = xatt.getDbName();
            }
            sql.append("SELECT ").append(father_field).append(',').append(fn);
            if( isbridge )
            {
                sql.append(",LIN");
            }
            sql.append(" FROM ").append(table);
            sql.append(" ").append(table)
            .append(" WHERE ")
            .append(father_field).append('=').append(object.bo_boui);
        }
        int page = 1;
        int pagesize = boObjectList.PAGESIZE_DEFAULT;
        if( object.getEboContext().getRequest() != null )
        {

            page = ClassUtils.convertToInt(object.getEboContext().getRequest().getParameter("list_page"),page);
            pagesize = ClassUtils.convertToInt(object.getEboContext().getRequest().getParameter("list_pagesize"),pagesize);

        }



        DataResultSet data = boObjectListResultFactory.getResultSetBySQL(object.getEboContext(),
                                               sql.toString(),
                                               null,
                                               "",
                                               page,
                                               pagesize
                                                );

        return new bridgeReverseHandler(def,xatt,isbridge,name,fn,father_field,data,object,page,pagesize);
    }



    public bridgeReverseHandler(boDefHandler def ,boDefAttribute att , boolean isbridge ,String name ,String childfield ,String childattname ,DataResultSet node,boObject parent,int page, int pagesize)
    {
        super( name , node , childfield , parent , def , att , childfield );
        super.p_page = page;
        super.p_pagesize = pagesize;
        p_isbridge = isbridge;
        p_name = name;
        p_parent = parent;
        p_node = node;
        if( isbridge )
            p_fatherfield=att.getBridge().getChildFieldName();
        else
            p_fatherfield=att.getDbName();

        p_childfield = childfield;
        p_childatt = att.getName();

        this.refreshBridgeData();

        first();
        bindAttributes();
    }

    public void add(BigDecimal boui, byte type) throws boRuntimeException
    {
        add(boui,-1, type);
    }

    public void add(BigDecimal boui,int xnrow) throws boRuntimeException
    {
        add(boui, xnrow, AttributeHandler.INPUT_FROM_USER);
    }

    public void add(BigDecimal boui) throws boRuntimeException {
        add(boui,-1, AttributeHandler.INPUT_FROM_USER);
    }

    public void add(BigDecimal boui,int xnrow, byte type) throws boRuntimeException
    {
        boolean ok = true;
        int lin = -1;

        if ( p_isbridge )
        {
            bridgeHandler xbridge = getParent( ).getObject( boui.longValue() ).getBridge( p_childatt );
            xbridge.add( getParent().getBoui() );

            xbridge.getParent().setChanged( true );

            lin = xbridge.getRow();
            if( xbridge.getValueLong() != getParent().getBoui() )
            {
                ok = false;
            }
        }
        else
        {
            AttributeHandler att = getObject( boui.longValue() ).getAttribute( p_childatt );
            long lvalue =  att.getValueLong();
            att.setValueLong( getParent().getBoui(), type );
            if(att.getValueLong()==lvalue && boui != null)
            {
                ok = false;
            }
            att.getParent().setChanged( true );
        }
        if( ok )
        {

            getParent().setChanged( true );
            getParent().getUpdateQueue().add( boui.longValue() , boObjectUpdateQueue.MODE_SAVE_REVERSE );



            DataRow row =  getRslt().getDataSet().createRow();
            row.updateLong( p_fatherfield , p_parent.getBoui() );

            if( p_isbridge )
            {
                row.updateLong( "LIN" , lin );
            }

            row.updateBigDecimal( p_childfield , boui );

            boBridgeRow brow = new bridgeReverseRow( this, row );
            insertRow( brow );

            xnrow = getRowPos( brow );

            this.moveTo( xnrow );

        }
    }

    public void moveRowTo( int i )
    {
        // TODO: Implement moveRowTo
    }



    public BigDecimal getValue() throws boRuntimeException
    {
        return rows( this.getRow() ).getValue();
    }

    public void setValue(BigDecimal xboui) throws boRuntimeException
    {
        setValue(xboui, AttributeHandler.INPUT_FROM_USER);
    }

    public void setValue(BigDecimal xboui, byte type) throws boRuntimeException
    {
        rows( this.getRow() ).setValue( xboui, type );
    }

    public void bindAttributes()
    {

    }

    public boAttributesArray getLineAttributes()
    {
        return this.rows( this.getRow()  ).getLineAttributes();
    }

    public AttributeHandler getAttribute(String attname)
    {
        return rows( this.getRow() ).getAttribute( attname );
    }

    public boObjectList edit() throws boRuntimeException
    {
        throw new RuntimeException(MessageLocalizer.getMessage("EDIT_IN_DYNAMIC_BRIDGES_NOT_SUPPORTED_YET"));
    }

    public boolean remove() throws boRuntimeException
    {
        boolean ret = false;
        try
        {

            getParent().getUpdateQueue().add( p_node.getLong(p_childfield) , boObjectUpdateQueue.MODE_SAVE_FORCED );
            if ( p_isbridge )
            {
                bridgeHandler remote = getObject().getBridge(p_childatt);
                remote.moveTo( this.p_node.getInt("LIN") );
                remote.remove();
            }
            else
            {
                ((bridgeReverseRow)rows( this.getRow() - 1 )).getChildAttribute().setValueObject( null );
                p_rows.remove( rows( this.getRow() - 1 ) );
            }

            int crow = this.getRow();
            p_rows.remove( rows( crow ) );
            getRslt().getDataSet().deleteRow( crow );

            return ret;

        }
        catch (SQLException e)
        {
            throw new boRuntimeException2( e.getClass().getName() + ".remove() \n" + e.getMessage() );
        }
    }


    //override
    public  boolean validate() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return true;
    }
    public  boolean required() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return false;
    }
    public  boolean disableWhen() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return false;
    }
    public  boolean hiddenWhen() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return false;
    }
    public  String defaultValue() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return null;
    }
    public  String[] condition() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return null;
    }
    public  boolean canChangeLov() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return false;
    }

    public  String formula() throws boRuntimeException {
        // TODO:  Overrided on generated objects
        return null;
    }

    public boBridgeRow createRow(DataRow row)
    {
        return new bridgeReverseRow( this, row );
    }

    public class bridgeReverseRow extends boBridgeRow
    {
        public bridgeReverseRow( bridgeHandler bridge, DataRow row )
        {
            super( bridge, row );
            p_attributes = new boReverseAttributesArray();
            addLine();
        }

        public void addLine()
        {
            boReverseAttributesArray xatt = (boReverseAttributesArray)getLineAttributes();
            xatt.add( getBridge().getName() , getChildAttribute(), getRowCount() );
        }

        public void refreshLineAttributes()
        {
            Enumeration oEnum = getLineAttributes().elements();
            boReverseAttributesArray newatts = new boReverseAttributesArray();
            while( oEnum.hasMoreElements() )
            {
                newatts.add( (AttributeHandler)oEnum.nextElement() );
            }

            getLineAttributes().p_attributes.clear();
            getLineAttributes().p_attributes.putAll( newatts.p_attributes );

        }
        private AttributeHandler getChildAttribute()
        {
            try
            {
                if( p_isbridge )
                {
                    bridgeHandler xbridge = getParent().getObject( getDataRow().getLong( p_childfield ) ).getBridge( p_childatt );
                    return xbridge.rows( getDataRow().getInt( "LIN" ) ).getAttribute( p_childatt );
                }
                else
                {
                    return getParent().getObject( getDataRow().getLong(p_childfield) ).getAttribute( p_childatt );
                }
            }
            catch (Exception e)
            {
                throw new boRuntimeException2(MessageLocalizer.getMessage("ERROR_READING_DYNAMIC_BRIDGE_ATTRIBUTE")+".\n"+e.getClass().getName()+".getChildAttribute\n"+e.getMessage());
            }
        }

        public long getValueLong() throws boRuntimeException
        {
            return getChildAttribute().getValueLong();
        }

        public void setValue(long xboui) throws boRuntimeException
        {
            getChildAttribute().setValueLong( xboui );
        }

        public void setValue(BigDecimal xboui) throws boRuntimeException
        {
            getChildAttribute().setValueObject( xboui, AttributeHandler.INPUT_FROM_USER );
        }

        public BigDecimal getValue() throws boRuntimeException {
            return (BigDecimal)getChildAttribute().getValueObject();
        }

        public void setValue(long xboui, byte type) throws boRuntimeException {
            setValue(BigDecimal.valueOf(xboui), type);
        }

        public void setValue(BigDecimal xboui, byte type) throws boRuntimeException {
            getChildAttribute().setValueObject( xboui, AttributeHandler.INPUT_FROM_USER );
        }
    }
}
