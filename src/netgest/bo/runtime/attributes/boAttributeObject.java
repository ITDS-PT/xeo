/*Enconding=UTF-8*/
package netgest.bo.runtime.attributes;

import java.math.BigDecimal;
import java.util.Hashtable;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.ObjAttHandler;
import netgest.bo.runtime.boConvertUtils;
import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.utils.ClassUtils;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public final class boAttributeObject extends ObjAttHandler
{
    /**
     *
     * @Company Enlace3
     * @since
     */
    private boolean isdbbinding = true;

    public boAttributeObject(boObject parent, String attname)
    {
        super(parent, parent.getBoDefinition().getAttributeRef(attname));
        isdbbinding = getDefAttribute().getDbIsBinding();
    }

    public String getValueString() throws boRuntimeException
    {
        return boConvertUtils.convertToString(this.getValue(), this);
    }

    public void setValueFormula(Hashtable table, String[] dependence)
        throws boRuntimeException
    {
        if (getParent().alreadyCalculated(table, dependence))
        {
            if (getParent().isWaiting(table, super.name))
            {
                getParent().setCalculated(table, super.name);
                setValue(boConvertUtils.convertToBigDecimal(formula(), this), false);
            }
            else if (!getParent().isCalculated(table, super.name))
            {
                getParent().setCalculated(table, super.name);
                setValue(boConvertUtils.convertToBigDecimal(formula(), this), table);
            }
        }
        else if (getParent().dependsFromWaiting(table, dependence))
        {
            if (getParent().onChangeSubmit(super.name))
            {
                getParent().setCalculated(table, super.name);
            }
            else
            {
                getParent().clear(table, super.name);
                setValue(boConvertUtils.convertToBigDecimal(formula(), this), false);
            }
        }
        else
        {
            setValue(boConvertUtils.convertToBigDecimal(formula(), this), false);

            if (getParent().onChangeSubmit(super.name))
            {
                getParent().setWaiting(table, super.name);
                getParent().calculateFormula(new Hashtable(table), super.name);

                //getParent().setCalculated(table, p_name);
            }
        }
    }

    public void setValue(BigDecimal newval) throws boRuntimeException
    {
        setValue(newval, true);
    }

    public void setValue(BigDecimal newval, boolean recalc)
        throws boRuntimeException
    {
        if (getParent().isCheckSecurity() && !this.hasRights())
        {
            throw new boRuntimeException(boAttributeObject.class.getName() + ".setValue()", "BO-3230", null, "");
        }
        
        netgest.bo.transformers.Transformer transfClass = null;
        if((transfClass = this.getDefAttribute().getTransformClassMap()) != null)
        {
            if(newval != null && this.getParent().getMode() != boObject.MODE_EDIT_TEMPLATE )
            {
                newval = BigDecimal.valueOf(transfClass.transform(getEboContext(), getParent(), newval.longValue()));
            }
        }

        if (!ClassUtils.compare(newval, this.getValue()))
        {
            BigDecimal chgval = this.getValue();

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

                if (recalc && getParent().onChangeSubmit(super.name))
                {
                    Hashtable table = new Hashtable();
                    getParent().setCalculated(table, super.name);
                    getParent().calculateFormula(table, super.name);
                }

                //vou verificar se o objecto se encontra numa bridge
                if (getParent().p_parentBridgeRow != null)
                {
                    getParent().getParentBridgeRow().getBridge().lineChanged(super.name);
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

    public void setValue(BigDecimal newval, Hashtable table)
        throws boRuntimeException
    {
        if (getParent().isCheckSecurity() && !this.hasRights())
        {
            throw new boRuntimeException(boAttributeObject.class.getName() + ".setValue()", "BO-3230", null, "");
        }

        if (!ClassUtils.compare(newval, this.getValue()))
        {
            BigDecimal chgval = this.getValue();

            boolean allow = onBeforeChange(new boEvent(this, EVENT_BEFORE_CHANGE, chgval, newval));

            if ((chgval == null) && (newval != null))
            {
                allow = onBeforeAdd(new boEvent(this, EVENT_BEFORE_ADD, newval));
            }

            if ((newval == null) && (chgval != null))
            {
                allow = onBeforeRemove(new boEvent(this, EVENT_BEFORE_REMOVE, chgval));
            }

            if (allow)
            {
            	if(canAlter(AttributeHandler.INPUT_FROM_INTERNAL))
                {
                _setValue(newval);
                setInputType(AttributeHandler.INPUT_FROM_INTERNAL);

                if (getParent().onChangeSubmit(super.name))
                {
                    getParent().setCalculated(table, super.name);
                    getParent().calculateFormula(table, super.name);
                }

                //vou verificar se o objecto se encontra numa bridge
                if (getParent().p_parentBridgeRow != null)
                {
                    getParent().getParentBridgeRow().getBridge().lineChanged(super.name);
                }

                if ((chgval != null) && (newval == null))
                {
                    onAfterRemove(new boEvent(this, EVENT_AFTER_REMOVE, chgval));
                }

                if ((chgval == null) && (newval != null))
                {
                    onAfterAdd(new boEvent(this, EVENT_AFTER_ADD, newval));
                }

                onAfterChange(new boEvent(this, EVENT_AFTER_CHANGE, chgval, newval));
                }
            }
        }
    }

    public void _setValue(BigDecimal newval) throws boRuntimeException
    {
        if (isdbbinding)
        {
            this.getParent().getDataRow().updateBigDecimal(super.fn, newval);
            getParent().setChanged(true);
            super.setValid();
        }
    }

    public void setValueString(String value) throws boRuntimeException
    {
        this.setValue(boConvertUtils.convertToBigDecimal(value, this));
    }

    public void setValueObject(Object value) throws boRuntimeException
    {
        this.setValue((BigDecimal) value);
    }

    public Object getValueObject() throws boRuntimeException
    {
        return this.getValue();
    }

    public double getValueDouble() throws boRuntimeException
    {
        return boConvertUtils.convertTodouble(this.getValue(), this);
    }

    public void setValue(double value) throws boRuntimeException
    {
        this.setValue(boConvertUtils.convertToBigDecimal(value, this));
    }

    public void setValuedouble(double value) throws boRuntimeException
    {
        this.setValue(boConvertUtils.convertToBigDecimal(value, this));
    }


    public boolean validate() throws boRuntimeException
    {
        return true;
    }

    public boolean required() throws boRuntimeException
    {
        return getDefAttribute().getRequired() != null && getDefAttribute().getRequired().getBooleanValue();
    }

    public boolean disableWhen() throws boRuntimeException
    {
        return getDefAttribute().getDisableWhen() != null && getDefAttribute().getDisableWhen().getBooleanValue();
    }

    public boolean hiddenWhen() throws boRuntimeException
    {
        return getDefAttribute().getHiddenWhen() != null && getDefAttribute().getHiddenWhen().getBooleanValue();
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
        return null;
    }

    public boolean canChangeLov() throws boRuntimeException
    {
        return getDefAttribute().getLovEditable()!=null && getDefAttribute().getLovEditable().getBooleanValue();
    }

    public String formula() throws boRuntimeException
    {
        return null;
    }    
}
