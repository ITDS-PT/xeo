/*Enconding=UTF-8*/
package netgest.bo.runtime;
import netgest.bo.def.*;
import java.util.*;
import java.sql.*;
import java.math.*;

public class boObjectStateHandler extends AttributeHandler {
    private boObject p_bobj;
    private boObjectStateHandler p_parent;
    private boObjectStateHandler[] p_childs;
    private boDefClsState p_def;
    private boolean changed = false;
    public boObjectStateHandler(boObject bobj,boDefClsState state) {
        super(bobj,state);
        p_bobj = bobj;
        p_def = state;
        
        
        boDefClsState[] xx =  p_def.getChildStates();
        p_childs = new boObjectStateHandler[xx.length];
        for (int i = 0; i < p_childs.length; i++)  {
            p_childs[i] = new boObjectStateHandler(p_bobj,xx[i]);
        }
    }
    public void setValue(String value) throws boRuntimeException {
        setValueString(value);
    }
    public String getValue() throws boRuntimeException {
        return getValueString();
    }
    
    
    public void setValueString(String valordesig) throws boRuntimeException 
    {
        nullAll();
        _setValueString( valordesig );
    }
    private void _setValueString(String valordesig) throws boRuntimeException {
        if(valordesig != null) {
            boObjectStateHandler[] childs = this.getChildStates();
            for (int i = 0; i < childs.length; i++)  {
                if((""+childs[i].getNumericForm()).equals(valordesig) || valordesig.equals(childs[i].getName()))
                {
                    if(this.getCurrentState()==null || this.getCurrentState().getNumericForm()!=childs[i].getNumericForm())
                    {
                        if ( p_def.getParent() != null && p_def.getParent().getParent() != null  )
                        {
                            getParent().getStateAttributes().get( p_def.getParent().getParent().getName() ).
                                    setValueString( "" + p_def.getParent().getNumericForm() );
                        }
                        p_bobj.getDataRow().updateInt(this.getName(),childs[i].getNumericForm());
                        getParent().setChanged( true );
                        break;
                    }
                }
            }
        } else {
            p_bobj.getDataRow().updateBigDecimal(this.getName(),null);
        }
    }
    private void nullAll() throws boRuntimeException
    {
//        boDefClsState states = p_bobj.getBoDefinition().getBoClsState();
//        if ( states != null  )
//        {
//            boDefClsState[]  childs = states.getChildStateAttributes();
//            for (int i = 0; i < childs.length; i++) 
//            {
//                (( boObjectStateHandler ) p_bobj.getStateAttributes().get( childs[i].getName() ))._setValueString( null );
//            }
//        }
        
    }
    public boObjectStateHandler getChildState( String statename ) 
    {
        for (byte i = 0; i < p_childs.length ; i++ ) 
        {
            if( p_childs[i].getName().equals( statename ) )
                return p_childs[i];
        }
        return null;      
    }
    public boObjectStateHandler getChildStateAtr( String statename ) 
    {
       return p_bobj.getStateAttribute(statename); 
        //return null;      
    }
    public boObjectStateHandler[] getChildStates() {
    
        return p_childs;
    }
    public String getName() {
        return p_def.getName();
    }
    public int getNumericForm() {
        return p_def.getNumericForm();
    }
    public boObjectStateHandler getCurrentState() {
        boObjectStateHandler ret=null;
        BigDecimal cstate = p_bobj.getDataRow().getBigDecimal(p_def.getName());
        if(cstate != null) {
            int x = cstate.intValue();
            for (int i = 0; i < p_childs.length; i++)  {
                if(x==p_childs[i].getNumericForm()) {
                    ret = p_childs[i];
                    break;
                }
            }
        }
        return ret;
    }
    public boObjectStateHandler[] getCurrentStates() {
        ArrayList ret= new ArrayList();
        int x = p_bobj.getDataRow().getInt(p_def.getName());
        for (int i = 0; i < p_childs.length; i++)  {
            if(x==p_childs[i].getNumericForm()) {
                ret.add(p_childs[i]);
            }
        }
        return (boObjectStateHandler[])ret.toArray(new boObjectStateHandler[0]);
    }
    public boDefClsState getDefinition() {
        return p_def;
    }
    public void computeStates() throws boRuntimeException {
//        if(p_bobj.getBoDefinition().getBoClsState().getName().equals(this.getName()))
//            computeState(p_bobj,this);
    }
    private static final void computeState(boObject bobj,boObjectStateHandler states) throws boRuntimeException 
    {
    }
    public boolean isValid() 
    {
        return false;
   }

    public String getValueString() throws boRuntimeException {
        String ret="";
        boObjectStateHandler state = this.getCurrentState();
        if(state != null)
            ret = state.getName();
        return ret;        
    }

    public boolean valid() {
        return true;
    }

    public String toString() {
        try {
            return this.getValueString();
        } catch (boRuntimeException e) {
            throw new boRuntimeException2("boObjectStateHandler.toString() "+ e.getMessage());
        }
    }
    public String getLabel() {
        return p_def.getLabel();
    }
    public Object getValueObject() throws boRuntimeException
    {
        return this.getValue();
    }
    public void setValueObject(Object value) throws boRuntimeException 
    {
        this.setValue((String)value);
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
    public  boolean haveDefaultValue(){
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
}