/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.math.BigDecimal;
import java.util.Enumeration;

import netgest.bo.data.DataRow;
import netgest.bo.def.boDefAttribute;

/**
 *
 * @author JMF
 */
public abstract class boBridgeRow
{
    private DataRow           p_row;
    private bridgeHandler     p_bridge;
    private int				  p_line = -1;
    protected boAttributesArray p_attributes = new boAttributesArray();

    public boBridgeRow( bridgeHandler bridge, DataRow row  )
    {
        p_row     = row;
        p_bridge  = bridge;
    }

    public int getLine()
    {
    	if( p_line == -1 ) {
    		p_line = p_bridge.getRowPos( this );
    	}
    	return p_line;
    }

    public bridgeHandler getBridge()
    {
        return p_bridge;
    }

    public boObject getParent()
    {
        return p_bridge.getParent();
    }

    public DataRow getDataRow()
    {
        return p_row;
    }

    public void setDataRow( DataRow row )
    {
        p_row = row;
    }


    public AttributeHandler getAttribute(String attname) {

    	char[] buff = new char[ 128 ];
    	String bName = p_bridge.getName();
    	int offset = bName.length() + 1;
    	System.arraycopy( bName.toCharArray(), 0, buff, 0, bName.length());
    	buff[offset-1] = '.' ;
    	
    	String s = String.valueOf( buff,0,bName.length() + 1 );
    	
        if( !attname.startsWith( s ) )
        {
        	String sLine = Integer.toString( this.getLine() );
        	System.arraycopy( attname.toCharArray(), 0, buff, offset, attname.length());
        	offset += attname.length();
        	buff[ offset ] = '.';
        	offset ++;
        	System.arraycopy( sLine.toCharArray(), 0, buff, offset, sLine.length());
        	offset += sLine.length();
        	
        	s = String.valueOf( buff, 0, offset);
        	return p_attributes.get( s );
        	
        }
        return p_attributes.get( attname );
    }

    public boAttributesArray getLineAttributes()
    {
        return p_attributes;
    }


    public long getValueLong() throws boRuntimeException
    {
        return this.getValue().longValue();
    }

    public void setValue(long xboui) throws boRuntimeException
    {
        setValue(xboui, AttributeHandler.INPUT_FROM_USER);
    }

    public void setValue(BigDecimal xboui) throws boRuntimeException
    {
        setValue(xboui, AttributeHandler.INPUT_FROM_USER);
    }

    public BigDecimal getValue() throws boRuntimeException {

        BridgeObjAttributeHandler att = (BridgeObjAttributeHandler)this.getAttribute(this.getBridge().getDefAttribute().getName());

        if ( getParent().isCheckSecurity() && !att.hasRights() )
            throw new boRuntimeException(this.getClass().getName() + ".getValue()", "BO-3230", null, "" );

        return att.getValue();
    }

    public void setValue(long xboui, byte type) throws boRuntimeException {
        setValue(BigDecimal.valueOf(xboui), type);
    }

    public void setValue(BigDecimal xboui, byte type) throws boRuntimeException {
        BridgeObjAttributeHandler att = (BridgeObjAttributeHandler)getAttribute( p_bridge.getName() );
        if (getParent().isCheckSecurity() && !att.hasRights())
            throw new boRuntimeException(this.getClass().getName() +".setValue(BigDecimal)", "BO-3230", null, "");

        att.setValue( xboui, type );
    }

    public abstract void addLine() throws boRuntimeException;


    public boObject getObject() throws boRuntimeException
    {
        boObject obj= this.getAttribute( p_bridge.getName() ).getObject();
        if ( obj != null )  obj.setParentBridgeRow( this );
        return obj;
    }

    public boObjectList edit() throws boRuntimeException
    {
        return ( (ObjAttHandler) this.getAttribute( p_bridge.getDefAttribute().getName() ) ).edit();
    }

    protected void postAddBridge() throws boRuntimeException {
        ( (ObjAttHandler) this.getAttribute( p_bridge.getDefAttribute().getName() ) ).postAddBridge();
    }

    public void refreshLineAttributes( int line )
    {
    	p_line = line;
        Enumeration oEnum = p_attributes.elements();
        boAttributesArray newatts = new boAttributesArray();
        while( oEnum.hasMoreElements() )
        {
            newatts.add( (AttributeHandler)oEnum.nextElement() );
        }
        p_attributes = newatts;
        if( p_bridge.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_N
            &&
            getDataRow().getLong( "LIN" ) != line
        )
        {
            try
            {
                boObject refObj = getObject();
                refObj.getDataSet().rows( 1 ).updateInt("LIN", getLine() );
                refObj.setChanged( true );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        getDataRow().updateInt( "LIN", line );
        getDataRow().updateLong( p_bridge.getDefAttribute().getBridge().getFatherFieldName() , this.getParent().getBoui() );

    }

}