/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

import netgest.bo.def.boDefAttribute;
import netgest.utils.ClassUtils;

public class boBridgeMasterAttribute extends AttributeHandler {
    
    public boBridgeMasterAttribute(boObject parent,boDefAttribute def) {
        super(parent,def);
        
    }

    public boBridgeMasterAttribute(boObject parent,boDefAttribute def,String name) {
        super(parent,def);
    }
 
    /**
     * Coloca a bridge com os bouis que vem no value.Faz o ADD e o REMOVE se necessário.
     * No fim tenta deixar a BRIDGE na mesma posição relativa com que entrou.Se não conseguir
     * deixa a bridge em beforeFirst.
     * 
     * 
     * @param value     String com bouis separados por ;
     * @param type      Quem está a fazer este set (  AttributeHandler.INPUT_FROM_* )
     * @see AttributeHandler
     * @see AttributeHandler.static fields
     */
    public void setValueString(String value, byte type) throws boRuntimeException {
        try
        {
        
            bridgeHandler bridge = (bridgeHandler)getParent().getBridge(getName());
            int savePos = bridge.getRow();
            ArrayList processedrows=new ArrayList();
            if(value!=null && value.trim().length()>0) {
                String[] sbouis = value.split(";"); // ClassUtils.splitToArray(value,";");
                
                
                for (int i = 0; i < sbouis.length; i++)  {
                    BigDecimal bd = new BigDecimal(sbouis[i]);
                    long cboui = bd.longValue();
                    //long cboui = Long.parseLong(sbouis[i]);
                    boBridgeRow br = bridge.getRow( cboui );
                    Integer xrow = null;
                    if( br == null  ) { //!bridge.haveBoui(cboui)) {
                        bridge.first();
                        bridge.add(cboui);
                        xrow = new Integer( bridge.getRslt().getRow() );
                    }
                    else
                    {
                        xrow = new Integer( br.getLine() );
                        
                    }
                    
                    if(processedrows.indexOf(xrow)==-1)
                        processedrows.add(xrow);
                }
            }
            
            if(processedrows.size()!=bridge.getRowCount()) 
            {
                bridge.beforeFirst();
                while(bridge.next()) 
                {
                    Integer xrow = new Integer(bridge.getRslt().getRow());
                    if(processedrows.indexOf(xrow)==-1) 
                    {
                        int savePos2 = bridge.getRow();
                        boolean removed = bridge.remove();
                        int xx=bridge.getRow();
                        if ( bridge.moveTo(savePos2 ) )
                        {
                            if( removed )
                            {
                                bridge.previous();
                            }
                            processedrows.remove(xrow);
                            if( removed )
                            {
                                for (int i = 0; i < processedrows.size(); i++) 
                                { 
                                    if(((Integer)processedrows.get(i)).intValue() > bridge.getRslt().getRow())
                                        processedrows.set(i,new Integer(((Integer)processedrows.get(i)).intValue()-1)); 
                                }
                            }
                        }
                        else
                        {
                            //ficou em AFTERLAST ... apagaram o resto das linhas...
                            // não faz mais nada
                            break;
                        }
                    }
                }
            }
            if (!bridge.moveTo(savePos))
            {
                bridge.beforeFirst();
            }
        }
        catch (SQLException e)
        {
            throw new boRuntimeException2(this.getClass().getName()+"setValueString() error.\n"+e.getClass().getName()+"\n"+e.getMessage());
        }
        setInputType(type);
    }
    public void setValueString(String value) throws boRuntimeException {
        setValueString(value, AttributeHandler.INPUT_FROM_USER);
    }
    public String getValueString()  throws boRuntimeException {
        bridgeHandler bridge = (bridgeHandler)getParent().getBridge(getName());
        StringBuffer bouis=new StringBuffer();
        bridge.beforeFirst();
        int rc = bridge.getRowCount();
        short i=0;
        while( bridge.next()) {
            i++;
            bouis.append(bridge.getValue());
            if(i < rc) bouis.append(";");
        }
        return bouis.toString();
    }
    public long[] getValuesLong() throws boRuntimeException
    {
        String[] bouis = getValueString().split(";");
        
        long[] ret = new long[ bouis.length ];
        for (int i = 0; i < bouis.length ; i++) 
        {
            ret[i] = ClassUtils.convertToLong( bouis[i] );
        }
        return ret;
    }
    
    public Object getValueObject() throws boRuntimeException
    {
        return this.getValueString();
    }
    
    public void setValueObject(Object value, byte type) throws boRuntimeException
    {   
    	if (value != null){     
    		this.setValueString(value.toString(), type);
    	} else
    		this.setValueString((String)value, type);
    
    	//this.setValueString((String)value, type);
    }
    
    public void setValueObject(Object value) throws boRuntimeException 
    {
        setValueObject(value, AttributeHandler.INPUT_FROM_USER);   
    }
    
    @Override
    public  boolean valid() throws boRuntimeException {
    	bridgeHandler bridge = getParent().getBridge(getName());
    	if (!bridge.valid()){
    		setInvalid( bridge.getErrorMessage() );
    		return false;
    	}
        return true;
    }
    
//override
    public  boolean validate() throws boRuntimeException {
        // Overridden on generated objects
        return ((bridgeHandler)getParent().getBridge(getName())).validate();
    }
    public  boolean required() throws boRuntimeException {
        // Overridden on generated objects
        return ((bridgeHandler)getParent().getBridge(getName())).required();
    }
    public  boolean disableWhen() throws boRuntimeException {
        // Overridden on generated objects
        return ((bridgeHandler)getParent().getBridge(getName())).disableWhen();
    }
    public  boolean hiddenWhen() throws boRuntimeException {
        // Overridden on generated objects
        return ((bridgeHandler)getParent().getBridge(getName())).hiddenWhen();
    }
    public  boolean haveDefaultValue(){
        // Overridden on generated objects
        return false;
    }
    public  String defaultValue() throws boRuntimeException {
        // Overridden on generated objects
        return ((bridgeHandler)getParent().getBridge(getName())).defaultValue();
    }
    public  String[] condition() throws boRuntimeException {
        // Overridden on generated objects
        return ((bridgeHandler)getParent().getBridge(getName())).condition();
    }
    public  boolean canChangeLov() throws boRuntimeException {
        // Overridden on generated objects
        return ((bridgeHandler)getParent().getBridge(getName())).canChangeLov();
    }  
    
    public  String formula() throws boRuntimeException {
        // Overridden on generated objects
        return ((bridgeHandler)getParent().getBridge(getName())).formula();
    } 
    
    @Override
    public boolean equals(Object other){
    	if (other instanceof boBridgeMasterAttribute){
    		boBridgeMasterAttribute otherBridge = (boBridgeMasterAttribute) other;
    		if (otherBridge.getParent() == this.getParent()){
    			if (otherBridge.getDefAttribute().getName().equalsIgnoreCase( this.getDefAttribute().getName() )){
    					return true;
    			}
    		}
    		
    	}return false;
    		
    }
    
    @Override
    public int hashCode(){
    	return (int) getParent().getBoui() * getDefAttribute().getName().hashCode();
    }
}