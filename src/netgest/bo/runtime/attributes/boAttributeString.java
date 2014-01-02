/*Enconding=UTF-8*/
package netgest.bo.runtime.attributes;

import netgest.bo.runtime.*;
import netgest.utils.ClassUtils;
import java.util.Hashtable;


public final class boAttributeString extends boAttributeBase
{
    private boolean isdbbinding;
    private String fieldvalue;
    private String p_db_name;
    private String p_name;

    public boAttributeString(boObject parent, String attname)
    {
        super(parent, parent.getBoDefinition().getAttributeRef( attname ));
        p_db_name   = getDefAttribute().getDbName();
        isdbbinding = getDefAttribute().getDbIsBinding();
        p_name      = attname;
    }

    public final String getValueString() throws boRuntimeException
    {
        return boConvertUtils.convertToString(this.getValue(), this);
    }

    public final String getValue() throws boRuntimeException
    {
        String ret = null;

        if (getParent().isCheckSecurity() && !this.hasRights())
        {
            throw new boRuntimeException(netgest.bo.runtime.attributes.boAttributeString.class.getName() + ".getValue()", "BO-3230", null, "");
        }

        fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );
        if (isdbbinding)
        {
            ret = this.getParent().getDataRow().getString( p_db_name );
        }
        else
        {
            ret = fieldvalue;
        }
        fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );
        return ret;
    }

    public final void setValueFormula(Hashtable table, String[] dependence)
        throws boRuntimeException
    {
        if (getParent().alreadyCalculated(table, dependence))
        {
            if (getParent().isWaiting(table, p_name ))
            {
                getParent().setCalculated(table, p_name );
                setValue(boConvertUtils.convertToString(formula(), this), false);
            }
            else if (!getParent().isCalculated(table, p_name ))
            {
                getParent().setCalculated(table, p_name );
                setValue(boConvertUtils.convertToString(formula(), this), table);
            }
        }
        else if (getParent().dependsFromWaiting(table, dependence))
        {
            if (getParent().onChangeSubmit( p_name ))
            {
                getParent().setCalculated(table, p_name );
            }
            else
            {
                getParent().clear(table, p_name );
                setValue(boConvertUtils.convertToString(formula(), this), false);
            }
        }
        else
        {
            setValue(boConvertUtils.convertToString(formula(), this), false);

            if (getParent().onChangeSubmit( p_name ))
            {
                getParent().setWaiting(table, p_name );
                getParent().calculateFormula(new Hashtable(table), p_name );
            }
        }
    }

    public final void setValue(String newval) throws boRuntimeException
    {
        setValue(newval, true);
    }

    public final void setValue(String newval, boolean recalc)
        throws boRuntimeException
    {
        if (getParent().isCheckSecurity() && !this.hasRights())
        {
            throw new boRuntimeException(netgest.bo.runtime.attributes.boAttributeString.class.getName() + ".setValue()", "BO-3230", null, "");
        }

        if (!ClassUtils.compare(newval, this.getValue()))
        {
            String chgval = this.getValue();

//            boolean allow = onBeforeChange(new boEvent(this, EVENT_BEFORE_CHANGE, chgval, newval));
//
//            if ((chgval == null) && (newval != null))
//            {
//                allow = onBeforeAdd(new boEvent(this, EVENT_BEFORE_ADD, newval));
//            }
//
//            if ((newval == null) && (chgval != null))
//            {
//                allow = onBeforeRemove(new boEvent(this, EVENT_BEFORE_REMOVE, chgval));
//            }

            if ( fireBeforeChangeEvent( this, chgval, newval) )
            {
            	if(canAlter(recalc?AttributeHandler.INPUT_FROM_USER:AttributeHandler.INPUT_FROM_INTERNAL))
                {
                _setValue(newval);
                setInputType(recalc ? AttributeHandler.INPUT_FROM_USER : AttributeHandler.INPUT_FROM_INTERNAL);

                if (recalc && getParent().onChangeSubmit( p_name ))
                {
                    Hashtable table = new Hashtable();
                    getParent().setCalculated(table, p_name );
                    getParent().calculateFormula(table, p_name );
                }

                //vou verificar se o objecto se encontra numa bridge
                if (getParent().p_parentBridgeRow != null)
                {
                    getParent().getParentBridgeRow().getBridge().lineChanged( p_name );
                }

                if ((chgval != null) && (newval == null))
                {
                    onAfterRemove(new boEvent(this, EVENT_AFTER_REMOVE, chgval));
                }

                if ((chgval == null) && (newval != null))
                {
                    onAfterAdd(new boEvent(this, EVENT_AFTER_ADD, newval));
                }

//                onAfterChange(new boEvent(this, EVENT_AFTER_CHANGE, chgval, newval));
                fireAfterChangeEvent( this, chgval, newval);
                }
            }
        }
    }

    public final void setValue(String newval, Hashtable table)
        throws boRuntimeException
    {
        if (getParent().isCheckSecurity() && !this.hasRights())
        {
            throw new boRuntimeException(netgest.bo.runtime.attributes.boAttributeString.class.getName() + ".setValue()", "BO-3230", null, "");
        }

        if (!ClassUtils.compare(newval, this.getValue()))
        {
            String chgval = this.getValue();

//            boolean allow = onBeforeChange(new boEvent(this, EVENT_BEFORE_CHANGE, chgval, newval));
//
//            if ((chgval == null) && (newval != null))
//            {
//                allow = onBeforeAdd(new boEvent(this, EVENT_BEFORE_ADD, newval));
//            }
//
//            if ((newval == null) && (chgval != null))
//            {
//                allow = onBeforeRemove(new boEvent(this, EVENT_BEFORE_REMOVE, chgval));
//            }

            if ( fireBeforeChangeEvent( this, chgval, newval) )
            {
            	if(canAlter(AttributeHandler.INPUT_FROM_INTERNAL))
                {
                _setValue(newval);
                                
                setInputType(AttributeHandler.INPUT_FROM_INTERNAL);

                if (getParent().onChangeSubmit( p_name ))
                {
                    getParent().setCalculated(table, p_name );
                    getParent().calculateFormula(table, p_name );
                }

                //vou verificar se o objecto se encontra numa bridge
                if ( getParent().p_parentBridgeRow != null )
                {
                    getParent().getParentBridgeRow().getBridge().lineChanged( p_name );
                }

//                if ((chgval != null) && (newval == null))
//                {
//                    onAfterRemove(new boEvent(this, EVENT_AFTER_REMOVE, chgval));
//                }
//
//                if ((chgval == null) && (newval != null))
//                {
//                    onAfterAdd(new boEvent(this, EVENT_AFTER_ADD, newval));
//                }
//
//                onAfterChange(new boEvent(this, EVENT_AFTER_CHANGE, chgval, newval));
                fireAfterChangeEvent( this, chgval, newval);
                }
            }
        }
    }

    public final void _setValue(String newval) throws boRuntimeException
    {
        if (isdbbinding)
        {
            this.getParent().getDataRow().updateString( p_db_name , newval);
            getParent().setChanged(true);
            super.setValid();
        }
        else
        {
            fieldvalue = newval;
        }
    }

    public final void _setValues(String[] newval) throws boRuntimeException
    {
        
    }

    public final void setValueString(String value) throws boRuntimeException
    {
        this.setValue(boConvertUtils.convertToString(value, this));
    }

    public final void setValueObject(Object value) throws boRuntimeException
    {
        this.setValue((String) value);
    }

    public final Object getValueObject() throws boRuntimeException
    {
        return this.getValue();
    }
}
