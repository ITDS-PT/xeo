/*Enconding=UTF-8*/
package netgest.bo.impl.document.merge;

import bsh.EvalError;
import bsh.Interpreter;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import java.math.BigDecimal;

import java.sql.Types;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class DsField
{
    /**
     *
     * @Company Enlace3
     * @since
     */
    private String alias;

    /**
     *
     * @Company Enlace3
     * @since
     */
    private String format;

    /**
     *
     * @Company Enlace3
     * @since
     */
    private String type;

    /**
     *
     * @Company Enlace3
     * @since
     */
    private String value;

    /**
     *
     * @Company Enlace3
     * @since
     */
    private String repeatBlock;
    private String initValue;

    public DsField(String alias, String format, String type, String value,
        String repeatBlock, String initValue)
    {
        this.alias = alias;
        this.format = format;
        this.type = type.toUpperCase();
        this.value = value;
        this.repeatBlock = repeatBlock;
        this.initValue = initValue;
    }

    private void start(Object toRet)
    {
        if (this.type != null)
        {
            String init;

            if ("LONG".equals(this.type))
            {
                init = ((this.initValue == null) || "".equals(this.initValue))
                    ? "0" : this.initValue;
                toRet = new Long(init);
            }
            else if ("INT".equals(this.type) || "INTEGER".equals(this.type))
            {
                init = ((this.initValue == null) || "".equals(this.initValue))
                    ? "0" : this.initValue;
                toRet = new Integer(init);
            }
            else if ("DOUBLE".equals(this.type))
            {
                init = ((this.initValue == null) || "".equals(this.initValue))
                    ? "0" : this.initValue;
                toRet = new Double(init);
            }
            else if ("FLOAT".equals(this.type))
            {
                init = ((this.initValue == null) || "".equals(this.initValue))
                    ? "0" : this.initValue;
                toRet = new Float(init);
            }
            else if ("BIGDECIMAL".equals(this.type))
            {
                init = ((this.initValue == null) || "".equals(this.initValue))
                    ? "0" : this.initValue;
                toRet = new BigDecimal(init);
            }
            else if ("STRING".equals(this.type))
            {
                init = (this.initValue == null) ? "" : this.initValue;
                toRet = new String(init);
            }
            else if ("BOOLEAN".equals(this.type))
            {
                boolean initf = ((this.initValue == null) ||
                    "".equals(this.initValue)) ? false
                                               : Boolean.getBoolean(initValue);
                toRet = new Boolean(initf);
            }
            else
            {
                init = (this.initValue == null) ? "" : this.initValue;
                toRet = new String(init);
            }
        }
    }

    public String getAlias()
    {
        return alias;
    }

    public String getFormat()
    {
        return format;
    }

    public String getType()
    {
        return type;
    }

    public String getValue()
    {
        return value;
    }

    public String getRepeatBlock()
    {
        return repeatBlock;
    }

    //        public void getData(bridgeHandler bh, Tabela tabela) throws boRuntimeException
    //        {            
    //            try
    //            {
    //                start();
    //                boObject obj = bh.getObject();
    //                Interpreter jeval = new Interpreter();
    //                jeval.set("object", obj);
    //                jeval.set(alias, toRet);
    //                Object ret = jeval.eval(value);
    //                tabela.insert(ret, alias, getSqlType());
    //            }
    //            catch (EvalError e)
    //            {
    //                throw new boRuntimeException("DsField", "getData", e);
    //            }
    //        }
    public void getData(bridgeHandler bh, Tabela tabela)
        throws boRuntimeException
    {
        try
        {
            Object toRet = null;
            start(toRet);

            if ((repeatBlock == null) || "".equals(repeatBlock))
            {
                Interpreter jeval = new Interpreter();
                jeval.set("bridge", bh);
                jeval.set(alias, toRet);

                Object ret = jeval.eval(value);
                tabela.insert(ret, alias, getSqlType());
            }
            else
            {
                //não faz sentido
            }
        }
        catch (EvalError e)
        {
            throw new boRuntimeException("DsField", "getData", e);
        }
    }

    public void getData(boObject obj, Tabela tabela) throws boRuntimeException
    {
        try
        {
            Object toRet = null;
            start(toRet);

            if ((repeatBlock == null) || "".equals(repeatBlock))
            {
                Interpreter jeval = new Interpreter();
                jeval.set("object", obj);

                Object ret = jeval.eval(value);
                tabela.insert(ret, alias, getSqlType());
            }
            else
            {
                bridgeHandler bh = obj.getBridge(repeatBlock);
                Interpreter jeval = new Interpreter();
                int svRow = bh.getRow();
                bh.beforeFirst();

                while (bh.next())
                {
                    Object o = calc(bh.getObject(), toRet);
                    jeval.set("object", bh.getObject());
                    jeval.set(alias, toRet);

                    toRet = jeval.eval(value);
                }

                bh.moveTo(svRow);
                tabela.insert(toRet, alias, getSqlType());
            }
        }
        catch (EvalError e)
        {
            throw new boRuntimeException("DsField", "getData", e);
        }
    }

    private static Object calc(Object object, Object sumTotal125)
        throws boRuntimeException
    {
        try
        {
            long sumTotal125Aux = ((Long) sumTotal125).longValue();
            netgest.bo.runtime.boObject diurno = ((netgest.bo.runtime.boObject) object).getAttribute(
                    "diurno").getObject();
            long extra1 = (diurno == null) ? 0
                                           : ((diurno.getAttribute("extra1")
                                                     .getValueObject() == null)
                ? 0 : diurno.getAttribute("extra1").getValueLong());

            return new Long(sumTotal125Aux + extra1);
        }
        catch (Exception e)
        {
            return sumTotal125;
        }
    }

    private int getSqlType()
    {
        String t = type.toUpperCase();

        if (t.indexOf("STRING") != -1)
        {
            return Types.VARCHAR;
        }

        if ((t.indexOf("LONG") != -1) || (t.indexOf("INTEGER") != -1) ||
                (t.indexOf("FLOAT") != -1) || (t.indexOf("DOUBLE") != -1) ||
                (t.indexOf("BIGDECIAML") != -1))
        {
            return Types.NUMERIC;
        }

        if (t.indexOf("BOOLEAN") != -1)
        {
            return Types.VARCHAR;
        }

        if (t.indexOf("OBJECT") != -1)
        {
            return Types.VARCHAR;
        }

        if (t.indexOf("CLOB") != -1)
        {
            return Types.VARCHAR;
        }

        if (t.indexOf("DATE") != -1)
        {
            return Types.DATE;
        }

        return Types.VARCHAR;
    }
}
