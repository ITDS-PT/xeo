/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.sql.SQLException;
import netgest.bo.data.DataRow;
import netgest.bo.def.*;

public abstract class BridgeAttributeHandler extends AttributeHandler implements boIBridgeAttribute
{

    private boBridgeRow p_bridgerow;
    
    public BridgeAttributeHandler(boObject parent,boBridgeRow bridge,boDefAttribute def) 
    {
        super(parent,def);
        p_bridgerow = bridge;
        
    }

    public String getName()
    {
        // TODO:  Override this netgest.bo.runtime.AttributeHandler method
        return getBridge().getDefAttribute().getName()+"."+super.getName()+"."+getLine();
    }

    public bridgeHandler getBridge() 
    {
        return p_bridgerow.getBridge();
    }
    
    public byte getInputType(  )
    {
        if( p_attinputtype == -1 )
        {
            DataRow row = p_bridgerow.getDataRow();
            p_attinputtype = boObjectUtils.getAttributeInputType( row , getName() );
        }
        return p_attinputtype;
    }
    public int getLine()
    {
        return p_bridgerow.getLine();
    }
}
    