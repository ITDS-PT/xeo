/*Enconding=UTF-8*/
package netgest.bo.runtime.attributes;

import netgest.bo.def.*;

import netgest.bo.runtime.*;

import netgest.utils.ClassUtils;

import java.sql.Timestamp;

import java.util.Date;
import java.util.Hashtable;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public final class boAttributeDate extends boAttributeBase
{
    /**
     *
     * @Company Enlace3
     * @since
     */
    private boolean isdbbinding = true;
    private String p_db_name;
    private String p_name;
    private Timestamp fieldvalue;

    public boAttributeDate(boObject parent, String attname)
    {
        super(parent, parent.getBoDefinition().getAttributeRef(attname));
        p_db_name = getDefAttribute().getDbName();
        isdbbinding = getDefAttribute().getDbIsBinding();
        p_name = attname;
    }

    public String getValueString() throws boRuntimeException
    {
        return boConvertUtils.convertToString(this.getValue(), this);
    }

    public Timestamp getValue() throws boRuntimeException
    {
        Timestamp ret = null;

        fireEvent( boEvent.EVENT_BEFORE_GETVALUE, null );

        if (getParent().isCheckSecurity() && !this.hasRights())
        {
            throw new boRuntimeException(boAttributeDate.class.getName() + ".getValue()", "BO-3230", null, "");
        }

        if (isdbbinding)
        {
            ret = this.getParent().getDataRow().getTimestamp( p_db_name );
        }
        else
        {
            ret = fieldvalue;
        }
        fireEvent( boEvent.EVENT_AFTER_GETVALUE, ret );

        return ret;
    }

    public void setValueFormula(Hashtable table, String[] dependence)
        throws boRuntimeException
    {
        if (getParent().alreadyCalculated(table, dependence))
        {
            if (getParent().isWaiting(table, p_name))
            {
                getParent().setCalculated(table, p_name);
                setValue(boConvertUtils.convertToTimestamp(formula(), this), false);
            }
            else if (!getParent().isCalculated(table, p_name))
            {
                getParent().setCalculated(table, p_name);
                setValue(boConvertUtils.convertToTimestamp(formula(), this), table);
            }
        }
        else if (getParent().dependsFromWaiting(table, dependence))
        {
            if (getParent().onChangeSubmit( p_name ))
            {
                getParent().setCalculated(table, p_name);
            }
            else
            {
                getParent().clear(table, p_name);
                setValue(boConvertUtils.convertToTimestamp(formula(), this), false);
            }
        }
        else
        {
            setValue(boConvertUtils.convertToTimestamp(formula(), this), false);

            if (getParent().onChangeSubmit( p_name ))
            {
                getParent().setWaiting(table, p_name);
                getParent().calculateFormula(new Hashtable(table), p_name);

                //getParent().setCalculated(table, p_name);
            }
        }
    }

    public void setValue(Timestamp newval) throws boRuntimeException
    {
        setValue(newval, true);
    }

    public void setValue(Timestamp newval, boolean recalc)
        throws boRuntimeException
    {
        if (getParent().isCheckSecurity() && !this.hasRights())
        {
            throw new boRuntimeException(boAttributeDate.class.getName() + ".setValue()", "BO-3230", null, "");
        }

        if (!ClassUtils.compare(newval, this.getValue()))
        {
            Timestamp chgval = this.getValue();

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
                    getParent().setCalculated(table, p_name);
                    getParent().calculateFormula(table, p_name);
                }

                //vou verificar se o objecto se encontra numa bridge
                if (getParent().p_parentBridgeRow != null)
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

    public void setValue(Timestamp newval, Hashtable table)
        throws boRuntimeException
    {
        if (getParent().isCheckSecurity() && !this.hasRights())
        {
            throw new boRuntimeException(boAttributeDate.class.getName() + ".setValue()", "BO-3230", null, "");
        }

        if (!ClassUtils.compare(newval, this.getValue()))
        {
            Timestamp chgval = this.getValue();

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
                    getParent().setCalculated(table, p_name);
                    getParent().calculateFormula(table, p_name);
                }

                //vou verificar se o objecto se encontra numa bridge
                if (getParent().p_parentBridgeRow != null)
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

    public void _setValue(Timestamp newval) throws boRuntimeException
    {
        if (isdbbinding)
        {
            this.getParent().getDataRow().updateTimestamp( p_db_name , newval);
            getParent().setChanged(true);
            super.setValid();
        }
        else
        {
            fieldvalue = newval;
        }
    }

    public void setValueString(String value) throws boRuntimeException
    {
        this.setValue(boConvertUtils.convertToTimestamp(value, this));
    }

    public void setValueObject(Object value) throws boRuntimeException
    {
        this.setValue((Timestamp) value);
    }

    public Object getValueObject() throws boRuntimeException
    {
        return this.getValue();
    }

    public Date getValueDate() throws boRuntimeException
    {
        return boConvertUtils.convertToDate(this.getValue(), this);
    }

    public void setValue(Date value) throws boRuntimeException
    {
        this.setValue(boConvertUtils.convertToTimestamp(value, this));
    }

    public void setValueDate(Date value) throws boRuntimeException
    {
        this.setValue(boConvertUtils.convertToTimestamp(value, this));
    }
}
