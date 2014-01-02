/*Enconding=UTF-8*/
package netgest.bo.runtime;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import netgest.bo.boConfig;
import netgest.bo.def.boDefDocument;
import netgest.io.iFile;
import netgest.io.iFileConnector;
import netgest.io.iFileException;


public class boConvertUtils
{
    private static final SimpleDateFormat sdfts = new SimpleDateFormat(
            "dd-MM-yyyy'T'HH:mm:ss");
    private static final SimpleDateFormat sdfdt = new SimpleDateFormat(
            "dd-MM-yyyy");
    private static final SimpleDateFormat sdftsYFirst = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat sdfdtYFirst = new SimpleDateFormat(
            "yyyy-MM-dd");

    //    public static final Object converArgumentToObject(EboContext eboctx,Class type,String value) throws boRuntimeException {
    //        String argtype = type.getName();
    //        Object ret=null;
    //        try {
    //            if(argtype.endsWith("String")) {
    //                ret = value;
    //            } else if (argtype.endsWith("Integer") || argtype.endsWith("int")) {
    //                ret = new Integer(Integer.parseInt(value));
    //            } else if (argtype.endsWith("Long")    || argtype.endsWith("long")) {
    //                ret = new Long(Long.parseLong(value));
    //            } else if (argtype.endsWith("Float")   || argtype.endsWith("float")) {
    //                ret = new Float(Float.parseFloat(value));
    //            } else if (argtype.endsWith("Double")  || argtype.endsWith("double")) {
    //                ret = new Double(Double.parseDouble(value));
    //            } else if (argtype.endsWith("DataNode")) {
    //                ret = NGTDocumentParser.parseNGTEFORMXML(Utils.loadXML(value).getDocumentElement(),eboctx);
    //            } else {
    //                throw new boRuntimeException(boConvertUtils.class.getName()+"converArgument(String)","BO-3013",null,argtype);
    //            }
    //        } catch (NumberFormatException e) {
    //            throw new boRuntimeException(boConvertUtils.class.getName()+"converArgument(String)","BO-3012",e,argtype);
    //        } catch (SQLException e) {
    //            throw new boRuntimeException(boConvertUtils.class.getName()+"converArgument(String)","BO-3012",e,argtype);
    //        } catch (newNGTException e) {
    //            throw new boRuntimeException(boConvertUtils.class.getName()+"converArgument(String)","BO-3012",e,argtype);
    //        } catch (XSLException e) {
    //            throw new boRuntimeException(boConvertUtils.class.getName()+"converArgument(String)","BO-3012",e,argtype);
    //        } catch (NGTException e) {
    //            throw new boRuntimeException(boConvertUtils.class.getName()+"converArgument(String)","BO-3012",e,argtype);
    //        } catch (IOException e) {
    //            throw new boRuntimeException(boConvertUtils.class.getName()+"converArgument(String)","BO-3012",e,argtype);
    //        }
    //        return ret;
    //    }
    public static final String convertToString(String value,
        AttributeHandler attr)
    {
        if (value == null)
        {
            return "";
        }

        return value;
    }

    public static final String convertToString(String[] value,
        AttributeHandler attr)
    {
        if (value == null)
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        for (short i = 0; i < value.length; i++)
        {
            sb.append(value[i]).append('|');
        }

        return sb.toString();
    }

    public static final String convertToString(int value, AttributeHandler attr)
    {
        return "" + value;
    }

    public static final String convertToString(long value, AttributeHandler attr)
    {
        return "" + value;
    }

    public static final String convertToString(double value,
        AttributeHandler attr)
    {
        if (((int) value) == value)
        {
            return "" + (int) value;
        }

        return "" + value;
    }

    public static final String convertToString(BigDecimal value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            return value.toString();
        }

        return "";
    }

    public static final String convertToString(BigDecimal[] value,
        AttributeHandler attr)
    {
        if (value == null)
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        for (short i = 0; i < value.length; i++)
        {
            sb.append(value[i]).append(';');
        }

        return sb.toString();
    }

    public static final String convertToString(Timestamp[] value,
        AttributeHandler attr)
    {
        if (value == null)
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        for (short i = 0; i < value.length; i++)
        {
            sb.append(convertToString(value[i], attr)).append(';');
        }

        return sb.toString();
    }

    public static final String convertToString(iFile value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            return value.getURI();
        }

        return "";
    }

    public static final String convertToString(Object value,
        AttributeHandler attr)
    {
        if (value instanceof BigDecimal)
        {
            return convertToString((BigDecimal) value, attr);
        }

        if (value instanceof iFile)
        {
            return convertToString((iFile) value, attr);
        }

        if (value instanceof Date)
        {
            return convertToString((Date) value, attr);
        }

        if (value instanceof iFile[])
        {
            return convertToString((iFile[]) value, attr);
        }

        if (value instanceof Timestamp[])
        {
            return convertToString((Timestamp[]) value, attr);
        }

        if (value instanceof Timestamp)
        {
            return convertToString((Timestamp) value, attr);
        }

        if (value instanceof Long)
        {
            return ((Long) value).toString();
        }

        if (value instanceof String)
        {
            return ((String) value);
        }

        if (value instanceof Integer)
        {
            return ((Integer) value).toString();
        }

        if (value instanceof AttributeHandler)
        {
            try
            {
                return ((AttributeHandler) value).getValueString();
            }
            catch (boRuntimeException e)
            {
                //ignora
            }
        }

        return "";
    }

    public static final String[] convertToArrayOfString(iFile[] value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            String[] ret = new String[value.length];

            for (short i = 0; i < value.length; i++)
            {
                ret[i] = value[i].getURI();
            }

            return ret;
        }
        else
        {
            return null;
        }
    }

    public static final String convertToString(Date value, AttributeHandler attr)
    {
        if (value != null)
        {
            if (attr.getDefAttribute().getType().equalsIgnoreCase("datetime"))
            {
                return sdfts.format(value);
            }
            else
            {
                return sdfdt.format(value);
            }
        }

        return "";
    }

    public static final String convertToStringYFirst(Date value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            if (attr.getDefAttribute().getType().equalsIgnoreCase("datetime"))
            {
                return sdftsYFirst.format(value);
            }
            else
            {
                return sdfdtYFirst.format(value);
            }
        }

        return "";
    }

    public static final String convertToString(Calendar value,
        AttributeHandler attr)
    {
        return convertToString(value.getTime(), attr);
    }

    public static final Date[] convertToArrayOfDate(Timestamp[] value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            Date[] ret = new Date[value.length];

            for (short i = 0; i < value.length; i++)
            {
                ret[i] = new Date(value[i].getTime());
            }

            return ret;
        }
        else
        {
            return null;
        }
    }

    public static final Date convertToDate(Date value, AttributeHandler attr)
    {
        return value;
    }

    public static final Date convertToDate(java.sql.Timestamp value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            return new Date(value.getTime());
        }

        return null;
    }

    public static final String convertToString(java.sql.Timestamp value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            if (attr.getDefAttribute().getType().equalsIgnoreCase("date"))
            {
                return sdfdt.format(new Date(value.getTime()));
            }
            else
            {
                return sdfts.format(new Date(value.getTime()));
            }
        }

        return "";
    }

    public static final Timestamp[] convertToArrayOfTimestamp(String newvalue,
        AttributeHandler attr)
    {
        if (newvalue != null)
        {
            String[] value = newvalue.split(";");
            Timestamp[] ret = new Timestamp[value.length];

            for (short i = 0; i < value.length; i++)
            {
                ret[i] = convertToTimestamp(value[i], attr);
            }

            return ret;
        }
        else
        {
            return null;
        }
    }

    public static final Timestamp[] convertToArrayOfTimestamp(String[] value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            Timestamp[] ret = new Timestamp[value.length];

            for (short i = 0; i < value.length; i++)
            {
                ret[i] = convertToTimestamp(value[i], attr);
            }

            return ret;
        }
        else
        {
            return null;
        }
    }

    public static final Timestamp[] convertToArrayOfTimestamp(Date[] value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            Timestamp[] ret = new Timestamp[value.length];

            for (short i = 0; i < value.length; i++)
            {
                ret[i] = convertToTimestamp(value[i], attr);
            }

            return ret;
        }
        else
        {
            return null;
        }
    }

    public static final java.sql.Timestamp convertToTimestamp(String value,
        AttributeHandler attr)
    {
        java.sql.Timestamp ret = null;

        if (value != null)
        {
            Date date = convertToDate(value, attr);

            if (date != null)
            {
                ret = new java.sql.Timestamp(date.getTime());
            }
        }

        return ret;
    }

    public static final java.sql.Timestamp convertToTimestamp(
        java.sql.Timestamp value, AttributeHandler attr)
    {
        return value;
    }

    public static final double convertTodouble(BigDecimal value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            return value.doubleValue();
        }

        return 0;
    }

    public static final String[] convertToArrayOfString(String[] value,
        AttributeHandler attr)
    {
        return value;
    }

    public static final BigDecimal convertToBigDecimal(BigDecimal value,
        AttributeHandler attr)
    {
        return value;
    }

    public static final long convertTolong(BigDecimal value,
        AttributeHandler attr)
    {
        return value.longValue();
    }

    public static final BigDecimal convertToBigDecimal(double value,
        AttributeHandler attr)
    {
        return new BigDecimal(value);
    }

    public static final iFile[] convertToArrayOfiFile(String[] value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            iFile[] ret = new iFile[value.length];

            for (short i = 0; i < value.length; i++)
            {
                ret[i] = attr.getParent().getiFile(value[i]);
            }

            return ret;
        }
        else
        {
            return null;
        }
    }

    public static final iFile convertToiFile(String value, AttributeHandler attr)
    {
        if (attr.getDefAttribute().getECMDocumentDefinitions() != null){
        	if (attr.p_valueIFileECM != null)
        		return attr.p_valueIFileECM;
        	else if ( value != null && value.length() > 0 ) {
        		
        		boDefDocument ecmDef = attr.getDefAttribute().getECMDocumentDefinitions();
            	iFileConnector con = null;
            	iFile toReturn;
    			try {
    				
    				//Get the default repository name
    				String repName = boConfig.getApplicationConfig().
    					getDefaultFileRepositoryConfiguration().getName();
    				
    				//Check if this attribute uses a different repository
    				if (ecmDef.getRepositoryName()!= null)
    					repName = ecmDef.getRepositoryName();
    				
    				//Retrieve the FileConnector
    				con = boConfig.getApplicationConfig().
    					getFileRepositoryConfiguration(repName).getConnector(attr);
    				
    				//Return the iFile
    				toReturn = con.getIFile(value);
    	        	return toReturn;
    			} catch (iFileException e) {
    				e.printStackTrace();
    			} catch (boRuntimeException e) {
    				e.printStackTrace();
    			}
        	}
        	
        }
        else{
        	return attr.getParent().getiFile(value);
        }
        return null;
    }

    public static final BigDecimal convertToBigDecimal(long value,
        AttributeHandler attr)
    {
        return BigDecimal.valueOf(value);
    }

    public static final java.sql.Timestamp convertToTimestamp(
        java.util.Date x, AttributeHandler attr)
    {
        if (x != null)
        {
            return new java.sql.Timestamp(x.getTime());
        }

        return null;
    }

    public static final String[] convertToArrayOfString(String value,
        AttributeHandler attr)
    {
        return new String[] { value };
    }

    public static final BigDecimal[] convertToArrayOfBigDecimal(
        BigDecimal value, AttributeHandler attr)
    {
        return new BigDecimal[] { value };
    }

    public static final BigDecimal[] convertToArrayOfBigDecimal(String value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            String[] values = value.split(";");
            BigDecimal[] ret = new BigDecimal[values.length];

            for (short i = 0; i < values.length; i++)
            {
                ret[i] = new BigDecimal(values[i]);
            }

            return ret;
        }
        else
        {
            return null;
        }
    }

    public static final BigDecimal[] convertToArrayOfBigDecimal(
        double[] value, AttributeHandler attr)
    {
        if (value != null)
        {
            BigDecimal[] ret = new BigDecimal[value.length];

            for (short i = 0; i < value.length; i++)
            {
                ret[i] = new BigDecimal(value[i]);
            }

            return ret;
        }
        else
        {
            return null;
        }
    }

    public static final BigDecimal[] convertToArrayOfBigDecimal(
        String[] value, AttributeHandler attr)
    {
        if (value != null)
        {
            BigDecimal[] ret = new BigDecimal[value.length];

            for (short i = 0; i < value.length; i++)
            {
                ret[i] = new BigDecimal(value[i]);
            }

            return ret;
        }
        else
        {
            return null;
        }
    }

    public static final BigDecimal[] convertToArrayOfBigDecimal(
        BigDecimal[] value, AttributeHandler attr)
    {
        return value;
    }

    public static final int convertToint(String intval, AttributeHandler attr)
    {
        if ((intval != null) && (intval.trim().length() > 0))
        {
            return Integer.parseInt(intval);
        }
        else
        {
            return 0;
        }
    }

    public static final long convertTolong(String longval, AttributeHandler attr)
    {
        if ((longval != null) && (longval.trim().length() > 0))
        {
            return Long.parseLong(longval);
        }

        return 0;
    }

    public static final Integer convertToInteger(String integerval,
        AttributeHandler attr)
    {
        if ((integerval != null) && (integerval.trim().length() > 0))
        {
            return Integer.valueOf(integerval);
        }

        return null;
    }

    public static final Long convertToLong(String integerval,
        AttributeHandler attr)
    {
        if ((integerval != null) && (integerval.trim().length() > 0))
        {
            return Long.valueOf(integerval);
        }

        return null;
    }

    public static final Double convertToDouble(String xfldval,
        AttributeHandler attr)
    {
        if ((xfldval != null) && (xfldval.trim().length() > 0))
        {
            return new Double((new BigDecimal(xfldval.replace(',', '.'))).doubleValue());
        }

        return null;
    }

    public static final BigDecimal convertToBigDecimal(String xfldval,
        AttributeHandler attr)
    {
        if ((xfldval != null) && (xfldval.trim().length() > 0))
        {
            return new BigDecimal(xfldval.replace(',', '.'));
        }
        else
        {
            return null;
        }
    }

    public static final double convertTodouble(String xfldval,
        AttributeHandler attr)
    {
        if ((xfldval != null) && (xfldval.length() > 0))
        {
            return (new BigDecimal(xfldval.replace(',', '.'))).doubleValue();
        }

        return 0;
    }

    public static final double[] convertToArrayOfdouble(BigDecimal[] value,
        AttributeHandler attr)
    {
        if (value != null)
        {
            double[] ret = new double[value.length];

            for (short i = 0; i < value.length; i++)
            {
                ret[i] = value[i].doubleValue();
            }

            return ret;
        }
        else
        {
            return null;
        }
    }

    public static final java.util.Date convertToDate(String xfldval,
        AttributeHandler attr)
    {
        if ((xfldval != null) && (xfldval.trim().length() > 0))
        {
            if (xfldval.indexOf("-") == 4)
            {
                return convertToDateYfirst(xfldval, attr);
            }
            else
            {
                return convertToDateDfirst(xfldval, attr);
            }
        }

        return null;
    }

    public static final java.util.Date convertToDateDfirst(String xfldval,
        AttributeHandler attr)
    {
        if ((xfldval != null) && (xfldval.trim().length() > 0))
        {
            java.util.Date flddate = null;
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "dd-MM-yyyy'T'HH:mm:ss.SSS");
            ParsePosition pos = new ParsePosition(0);
            flddate = formatter.parse(xfldval, pos);

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("dd-MM-yyyy'T'HH");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("dd-MM-yyyy HH");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("dd-MM-yyyy");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                flddate = formatter.parse(xfldval, pos);
            }            

            return flddate;
        }

        return null;
    }

    public static final java.util.Date convertToDateYfirst(String xfldval,
        AttributeHandler attr)
    {
        if ((xfldval != null) && (xfldval.trim().length() > 0))
        {
            java.util.Date flddate = null;
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS");
            ParsePosition pos = new ParsePosition(0);
            flddate = formatter.parse(xfldval, pos);

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                flddate = formatter.parse(xfldval, pos);
            }

            return flddate;
        }

        return null;
    }
}
