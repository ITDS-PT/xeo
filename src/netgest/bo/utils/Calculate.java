/*Enconding=UTF-8*/
package netgest.bo.utils;

import java.math.BigDecimal;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import netgest.bo.runtime.*;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public final class Calculate
{
    public static final int IGNORECASE_BIGGER = 0;
    public static final int IGNORECASE_LESS = 1;
    public static final int IGNORECASE_EQUAL = 2;
    public static final int IGNORECASE_BIGGER_EQUAL = 3;
    public static final int IGNORECASE_LESS_EQUAL = 4;
    public static final int BIGGER = 5;
    public static final int LESS = 6;
    public static final int BIGGER_EQUAL = 7;
    public static final int LESS_EQUAL = 8;
    public static final int EQUAL = 9;
    public static final int NOT_EQUAL = 10;
    public static final int IGNORECASE_NOT_EQUAL = 11;


    //MULTIPLY    
    public static BigDecimal multiply(Object v1, Object v2)
    {
        return new BigDecimal(v1.toString()).multiply(new BigDecimal(
                v2.toString()));
    }

    //long    
    public static BigDecimal multiply(long v1, long v2)
    {
        return multiply(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal multiply(Object v1, long v2)
    {
        return multiply(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal multiply(long v1, Object v2)
    {
        return multiply(String.valueOf(v1), v2.toString());
    }

    //double    
    public static BigDecimal multiply(double v1, double v2)
    {
        return multiply(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal multiply(Object v1, double v2)
    {
        return multiply(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal multiply(double v1, Object v2)
    {
        return multiply(String.valueOf(v1), v2.toString());
    }

    //int    
    public static BigDecimal multiply(int v1, int v2)
    {
        return multiply(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal multiply(Object v1, int v2)
    {
        return multiply(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal multiply(int v1, Object v2)
    {
        return multiply(String.valueOf(v1), v2.toString());
    }

    //float    
    public static BigDecimal multiply(float v1, float v2)
    {
        return multiply(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal multiply(Object v1, float v2)
    {
        return multiply(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal multiply(float v1, Object v2)
    {
        return multiply(String.valueOf(v1), v2.toString());
    }

    //SUBTRACT    
    public static BigDecimal subtract(Object v1, Object v2)
    {
        return new BigDecimal(v1.toString()).subtract(new BigDecimal(
                v2.toString()));
    }

    //long    
    public static BigDecimal subtract(long v1, long v2)
    {
        return subtract(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal subtract(Object v1, long v2)
    {
        return subtract(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal subtract(long v1, Object v2)
    {
        return subtract(String.valueOf(v1), v2.toString());
    }

    //double    
    public static BigDecimal subtract(double v1, double v2)
    {
        return subtract(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal subtract(Object v1, double v2)
    {
        return subtract(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal subtract(double v1, Object v2)
    {
        return subtract(String.valueOf(v1), v2.toString());
    }

    //int    
    public static BigDecimal subtract(int v1, int v2)
    {
        return subtract(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal subtract(Object v1, int v2)
    {
        return subtract(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal subtract(int v1, Object v2)
    {
        return subtract(String.valueOf(v1), v2.toString());
    }

    //float    
    public static BigDecimal subtract(float v1, float v2)
    {
        return subtract(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal subtract(Object v1, float v2)
    {
        return subtract(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal subtract(float v1, Object v2)
    {
        return subtract(String.valueOf(v1), v2.toString());
    }

    //SUM    
    public static BigDecimal sum(Object v1, Object v2)
    {
        return new BigDecimal(v1.toString()).add(new BigDecimal(v2.toString()));
    }

    //long    
    public static BigDecimal sum(long v1, long v2)
    {
        return sum(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal sum(Object v1, long v2)
    {
        return sum(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal sum(long v1, Object v2)
    {
        return sum(String.valueOf(v1), v2.toString());
    }

    //double    
    public static BigDecimal sum(double v1, double v2)
    {
        return sum(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal sum(Object v1, double v2)
    {
        return sum(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal sum(double v1, Object v2)
    {
        return sum(String.valueOf(v1), v2.toString());
    }

    //int    
    public static BigDecimal sum(int v1, int v2)
    {
        return sum(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal sum(Object v1, int v2)
    {
        return sum(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal sum(int v1, Object v2)
    {
        return sum(String.valueOf(v1), v2.toString());
    }

    //float    
    public static BigDecimal sum(float v1, float v2)
    {
        return sum(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal sum(Object v1, float v2)
    {
        return sum(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal sum(float v1, Object v2)
    {
        return sum(String.valueOf(v1), v2.toString());
    }

    //DIVIDE    
    public static BigDecimal divide(Object v1, Object v2)
    {
        return new BigDecimal(v1.toString()).divide(new BigDecimal(
                v2.toString()), 30, BigDecimal.ROUND_HALF_EVEN);
    }

    //long    
    public static BigDecimal divide(long v1, long v2)
    {
        return divide(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal divide(Object v1, long v2)
    {
        return divide(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal divide(long v1, Object v2)
    {
        return divide(String.valueOf(v1), v2.toString());
    }

    //double    
    public static BigDecimal divide(double v1, double v2)
    {
        return divide(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal divide(Object v1, double v2)
    {
        return divide(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal divide(double v1, Object v2)
    {
        return divide(String.valueOf(v1), v2.toString());
    }

    //int    
    public static BigDecimal divide(int v1, int v2)
    {
        return divide(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal divide(Object v1, int v2)
    {
        return divide(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal divide(int v1, Object v2)
    {
        return divide(String.valueOf(v1), v2.toString());
    }

    //float    
    public static BigDecimal divide(float v1, float v2)
    {
        return divide(String.valueOf(v1), String.valueOf(v2));
    }

    public static BigDecimal divide(Object v1, float v2)
    {
        return divide(v1.toString(), String.valueOf(v2));
    }

    public static BigDecimal divide(float v1, Object v2)
    {
        return divide(String.valueOf(v1), v2.toString());
    }

    //-------Comparações
    //---------------------String
    private static boolean compare(String v1, String v2, int type)
    {
        //primeiro vou verificar se é um number ou se é data
        try
        {
            BigDecimal aux = new BigDecimal(v1);
            BigDecimal aux2 = new BigDecimal(v2);

            return compare(aux, aux2, type);
        }
        catch (Exception e)
        {
            try
            {
                Date aux = boConvertUtils.convertToDate(v1, null);
                Date aux2 = boConvertUtils.convertToDate(v2, null);

                return compare(aux, aux2, type);
            }
            catch (Exception e_)
            {
                //ignora
            }
        }

        if (type == IGNORECASE_BIGGER)
        {
            return v1.compareToIgnoreCase(v2) > 0;
        }

        if (type == IGNORECASE_LESS)
        {
            return v1.compareToIgnoreCase(v2) < 0;
        }

        if (type == IGNORECASE_BIGGER_EQUAL)
        {
            return v1.compareToIgnoreCase(v2) >= 0;
        }

        if (type == IGNORECASE_LESS_EQUAL)
        {
            return v1.compareToIgnoreCase(v2) <= 0;
        }

        if (type == IGNORECASE_EQUAL)
        {
            return v1.compareToIgnoreCase(v2) == 0;
        }

        if (type == BIGGER)
        {
            return v1.compareTo(v2) > 0;
        }

        if (type == LESS)
        {
            return v1.compareTo(v2) < 0;
        }

        if (type == BIGGER_EQUAL)
        {
            return v1.compareTo(v2) >= 0;
        }

        if (type == LESS_EQUAL)
        {
            return v1.compareTo(v2) <= 0;
        }

        if (type == EQUAL)
        {
            return v1.compareTo(v2) == 0;
        }

        if (type == NOT_EQUAL)
        {
            return v1.compareTo(v2) != 0;
        }

        if (type == IGNORECASE_NOT_EQUAL)
        {
            return v1.compareToIgnoreCase(v2) != 0;
        }

        return false;
    }

    private static boolean compare(Object v1, String v2, int type)
    {
        return compare(v1.toString(), v2, type);
    }

    private static boolean compare(String v1, Object v2, int type)
    {
        if ((v1 != null) && (v2 != null))
        {
            return compare(v1, v2.toString(), type);
        }
        else if ((v1 == null) && (v2 == null))
        {
            return true;
        }

        return false;
    }

    /*
        public static boolean compare(Object v1, Object v2, int type)
        {
            return compare(v1.toString(), v2.toString(), type);
        }
    */
    public static boolean compare(Object v1, Object v2, int type)
    {
        if ((v1 == null) || (v2 == null))
        {
            return compareNull(v1, v2, type);
        }

        try
        {
            BigDecimal aux;
            BigDecimal aux2;
            aux = new BigDecimal(v1.toString());
            aux2 = new BigDecimal(v2.toString());

            return compare(aux, aux2, type);
        }
        catch (Exception e)
        {
            //não é number verificar se é data
            try
            {
                Date aux;
                Date aux2;
                aux = boConvertUtils.convertToDate(v1.toString(), null);
                aux2 = boConvertUtils.convertToDate(v2.toString(), null);

                return compare(aux, aux2, type);
            }
            catch (Exception e_)
            {
                return compare(v1.toString(), v2.toString(), type);
            }
        }
    }

    private static boolean compare(String v1, long v2, int type)
    {
        long aux;

        try
        {
            aux = Long.parseLong(v1);
        }
        catch (Exception e)
        {
            return compare(v1, String.valueOf(v2), type);
        }

        return compare(aux, v2, type);
    }

    private static boolean compare(long v1, String v2, int type)
    {
        long aux;

        try
        {
            aux = Long.parseLong(v2);
        }
        catch (Exception e)
        {
            return compare(String.valueOf(v1), v2, type);
        }

        return compare(v1, aux, type);
    }

    private static boolean compare(String v1, int v2, int type)
    {
        int aux;

        try
        {
            aux = Integer.parseInt(v1);
        }
        catch (Exception e)
        {
            return compare(v1, String.valueOf(v2), type);
        }

        return compare(aux, v2, type);
    }

    private static boolean compare(int v1, String v2, int type)
    {
        int aux;

        try
        {
            aux = Integer.parseInt(v2);
        }
        catch (Exception e)
        {
            return compare(String.valueOf(v1), v2, type);
        }

        return compare(v1, aux, type);
    }

    private static boolean compare(String v1, double v2, int type)
    {
        double aux;

        try
        {
            aux = Double.parseDouble(v1);
        }
        catch (Exception e)
        {
            return compare(v1, String.valueOf(v2), type);
        }

        return compare(aux, v2, type);
    }

    private static boolean compare(double v1, String v2, int type)
    {
        double aux;

        try
        {
            aux = Double.parseDouble(v2);
        }
        catch (Exception e)
        {
            return compare(String.valueOf(v1), v2, type);
        }

        return compare(v1, aux, type);
    }

    private static boolean compare(String v1, float v2, int type)
    {
        double aux;

        try
        {
            aux = Float.parseFloat(v1);
        }
        catch (Exception e)
        {
            return compare(v1, String.valueOf(v2), type);
        }

        return compare(aux, v2, type);
    }

    private static boolean compare(float v1, String v2, int type)
    {
        double aux;

        try
        {
            aux = Float.parseFloat(v2);
        }
        catch (Exception e)
        {
            return compare(String.valueOf(v1), v2, type);
        }

        return compare(v1, aux, type);
    }

    private static boolean compare(BigDecimal v1, String v2, int type)
    {
        BigDecimal aux;

        try
        {
            aux = new BigDecimal(v2);
        }
        catch (Exception e)
        {
            return compare(v1.toString(), v2, type);
        }

        return compare(v1, aux, type);
    }

    private static boolean compare(String v1, BigDecimal v2, int type)
    {
        BigDecimal aux;

        try
        {
            aux = new BigDecimal(v1);
        }
        catch (Exception e)
        {
            return compare(v1, v2.toString(), type);
        }

        return compare(aux, v2, type);
    }

    //---------------------Date
    private static boolean compareNull(Object v1, Object v2, int type)
    {
        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return false;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return false;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return false;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return false;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            if (((v1 == null) && (v2 == null)) ||
                    ((v1 != null) && (v2 != null)))
            {
                return true;
            }

            return false;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            if (((v1 == null) && (v2 == null)) ||
                    ((v1 != null) && (v2 != null)))
            {
                return false;
            }

            return true;
        }

        return false;
    }

    private static boolean compare(Date v1, Date v2, int type)
    {
        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return v1.compareTo(v2) > 0;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return v1.compareTo(v2) >= 0;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return v1.compareTo(v2) < 0;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return v1.compareTo(v2) <= 0;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return v1.compareTo(v2) == 0;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return v1.compareTo(v2) != 0;
        }

        return false;
    }

    private static boolean compare(Date v1, long v2, int type)
    {
        return compare(v1.getTime(), v2, type);
    }

    private static boolean compare(long v1, Date v2, int type)
    {
        return compare(v1, v2.getTime(), type);
    }

    private static boolean compare(Date v1, int v2, int type)
    {
        return compare(v1.getTime(), v2, type);
    }

    private static boolean compare(int v1, Date v2, int type)
    {
        return compare(v1, v2.getTime(), type);
    }

    private static boolean compare(Date v1, float v2, int type)
    {
        return compare(v1.getTime(), v2, type);
    }

    private static boolean compare(float v1, Date v2, int type)
    {
        return compare(v1, v2.getTime(), type);
    }

    private static boolean compare(Date v1, BigDecimal v2, int type)
    {
        return compare(v1.getTime(), v2, type);
    }

    private static boolean compare(BigDecimal v1, Date v2, int type)
    {
        return compare(v1, v2.getTime(), type);
    }

    private static boolean compare(Date v1, double v2, int type)
    {
        return compare(v1.getTime(), v2, type);
    }

    private static boolean compare(double v1, Date v2, int type)
    {
        return compare(v1, v2.getTime(), type);
    }

    private static boolean compare(Date v1, Object v2, int type)
    {
        return compare(v1, v2.toString(), type);
    }

    private static boolean compare(Object v1, Date v2, int type)
    {
        return compare(v1.toString(), v2, type);
    }

    private static boolean compare(Date v1, String v2, int type)
    {
        return compare(v1, boConvertUtils.convertToDate(v2, null), type);
    }

    private static boolean compare(String v1, Date v2, int type)
    {
        return compare(boConvertUtils.convertToDate(v1, null), v2, type);
    }

    private static boolean compare(Date v1, Date v2, int type, String cmpUntil)
    {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(v1);
        c2.setTime(v2);

        Calendar toC = Calendar.getInstance();
        Calendar toC2 = Calendar.getInstance();
        toC.clear();
        toC2.clear();

        if (cmpUntil.indexOf("y") > -1)
        {
            toC.set(Calendar.YEAR, c1.get(Calendar.YEAR));
            toC2.set(Calendar.YEAR, c2.get(Calendar.YEAR));
        }

        if (cmpUntil.indexOf("M") > -1)
        {
            toC.set(Calendar.MONTH, c1.get(Calendar.MONTH));
            toC2.set(Calendar.MONTH, c2.get(Calendar.MONTH));
        }

        if (cmpUntil.indexOf("d") > -1)
        {
            toC.set(Calendar.DATE, c1.get(Calendar.DATE));
            toC2.set(Calendar.DATE, c2.get(Calendar.DATE));
        }

        if (cmpUntil.indexOf("h") > -1)
        {
            toC.set(Calendar.DATE, c1.get(Calendar.DATE));
            toC2.set(Calendar.DATE, c2.get(Calendar.DATE));
        }

        if (cmpUntil.indexOf("m") > -1)
        {
            toC.set(Calendar.DATE, c1.get(Calendar.DATE));
            toC2.set(Calendar.DATE, c2.get(Calendar.DATE));
        }

        if (cmpUntil.indexOf("s") > -1)
        {
            toC.set(Calendar.SECOND, c1.get(Calendar.SECOND));
            toC2.set(Calendar.SECOND, c2.get(Calendar.SECOND));
        }

        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return toC.after(toC2);
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return toC.after(toC2) || toC.equals(toC2);
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return toC.before(toC2);
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return toC.before(toC2) || toC.equals(toC2);
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return toC.equals(toC2);
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return !toC.equals(toC2);
        }

        return false;
    }

    private static boolean compare(Date v1, long v2, int type, String cmpUntil)
    {
        return compare(v1, new Date(v2), type, cmpUntil);
    }

    private static boolean compare(long v1, Date v2, int type, String cmpUntil)
    {
        return compare(new Date(v1), v2, type, cmpUntil);
    }

    private static boolean compare(Date v1, int v2, int type, String cmpUntil)
    {
        return compare(v1, new Date(v2), type, cmpUntil);
    }

    private static boolean compare(int v1, Date v2, int type, String cmpUntil)
    {
        return compare(new Date(v1), v2, type, cmpUntil);
    }

    private static boolean compare(Date v1, float v2, int type, String cmpUntil)
    {
        return compare(v1, new Date((long) v2), type, cmpUntil);
    }

    private static boolean compare(float v1, Date v2, int type, String cmpUntil)
    {
        return compare(new Date((long) v1), v2, type, cmpUntil);
    }

    private static boolean compare(Date v1, BigDecimal v2, int type,
        String cmpUntil)
    {
        return compare(v1, new Date(v2.longValue()), type, cmpUntil);
    }

    private static boolean compare(BigDecimal v1, Date v2, int type,
        String cmpUntil)
    {
        return compare(new Date(v1.longValue()), v2, type, cmpUntil);
    }

    private static boolean compare(Date v1, double v2, int type, String cmpUntil)
    {
        return compare(v1, new Date((long) v2), type, cmpUntil);
    }

    private static boolean compare(double v1, Date v2, int type, String cmpUntil)
    {
        return compare(new Date((long) v1), v2, type, cmpUntil);
    }

    private static boolean compare(Date v1, Object v2, int type, String cmpUntil)
    {
        return compare(v1, v2.toString(), type, cmpUntil);
    }

    private static boolean compare(Object v1, Date v2, int type, String cmpUntil)
    {
        return compare(v1.toString(), v2, type, cmpUntil);
    }

    private static boolean compare(Date v1, String v2, int type, String cmpUntil)
    {
        return compare(v1, boConvertUtils.convertToDate(v2, null), type,
            cmpUntil);
    }

    private static boolean compare(String v1, Date v2, int type, String cmpUntil)
    {
        return compare(boConvertUtils.convertToDate(v1, null), v2, type,
            cmpUntil);
    }

    //---------------------BigDecimal
    private static boolean compare(BigDecimal v1, BigDecimal v2, int type)
    {
        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return v1.compareTo(v2) > 0;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return v1.compareTo(v2) >= 0;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return v1.compareTo(v2) < 0;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return v1.compareTo(v2) <= 0;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return v1.compareTo(v2) == 0;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return v1.compareTo(v2) != 0;
        }

        return false;
    }

    private static boolean compare(BigDecimal v1, long v2, int type)
    {
        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return v1.compareTo(new BigDecimal(v2)) > 0;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) >= 0;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return v1.compareTo(new BigDecimal(v2)) < 0;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) <= 0;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) == 0;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) != 0;
        }

        return false;
    }

    private static boolean compare(long v1, BigDecimal v2, int type)
    {
        BigDecimal aux = new BigDecimal(v1);

        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return aux.compareTo(v2) > 0;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return aux.compareTo(v2) >= 0;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return aux.compareTo(v2) < 0;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return aux.compareTo(v2) <= 0;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return aux.compareTo(v2) == 0;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return aux.compareTo(v2) != 0;
        }

        return false;
    }

    private static boolean compare(BigDecimal v1, int v2, int type)
    {
        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return v1.compareTo(new BigDecimal(v2)) > 0;
        }

        if ((type == IGNORECASE_BIGGER) || (type == BIGGER_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) >= 0;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return v1.compareTo(new BigDecimal(v2)) < 0;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) <= 0;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) == 0;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) != 0;
        }

        return false;
    }

    private static boolean compare(int v1, BigDecimal v2, int type)
    {
        BigDecimal aux = new BigDecimal(v1);

        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return aux.compareTo(v2) > 0;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return aux.compareTo(v2) >= 0;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return aux.compareTo(v2) < 0;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return aux.compareTo(v2) <= 0;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return aux.compareTo(v2) == 0;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return aux.compareTo(v2) != 0;
        }

        return false;
    }

    private static boolean compare(BigDecimal v1, double v2, int type)
    {
        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return v1.compareTo(new BigDecimal(v2)) > 0;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) >= 0;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return v1.compareTo(new BigDecimal(v2)) < 0;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) <= 0;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) == 0;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) != 0;
        }

        return false;
    }

    private static boolean compare(double v1, BigDecimal v2, int type)
    {
        BigDecimal aux = new BigDecimal(v1);

        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return aux.compareTo(v2) > 0;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return aux.compareTo(v2) >= 0;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return aux.compareTo(v2) < 0;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return aux.compareTo(v2) <= 0;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return aux.compareTo(v2) == 0;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return aux.compareTo(v2) != 0;
        }

        return false;
    }

    private static boolean compare(BigDecimal v1, float v2, int type)
    {
        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return v1.compareTo(new BigDecimal(v2)) > 0;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) >= 0;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return v1.compareTo(new BigDecimal(v2)) < 0;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) <= 0;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) == 0;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return v1.compareTo(new BigDecimal(v2)) != 0;
        }

        return false;
    }

    private static boolean compare(float v1, BigDecimal v2, int type)
    {
        BigDecimal aux = new BigDecimal(v1);

        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return aux.compareTo(v2) > 0;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return aux.compareTo(v2) >= 0;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return aux.compareTo(v2) < 0;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return aux.compareTo(v2) <= 0;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return aux.compareTo(v2) == 0;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return aux.compareTo(v2) != 0;
        }

        return false;
    }

    private static boolean compare(BigDecimal v1, Object v2, int type)
    {
        return compare(v1, v2.toString(), type);
    }

    private static boolean compare(Object v1, BigDecimal v2, int type)
    {
        return compare(v1.toString(), v1, type);
    }

    //---------------------long
    public static boolean compare(long v1, long v2, int type)
    {
        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return v1 > v2;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return v1 >= v2;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return v1 < v2;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return v1 <= v2;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return v1 == v2;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return v1 != v2;
        }

        return false;
    }

    public static boolean compare(long v1, int v2, int type)
    {
        return compare(v1, (long) v2, type);
    }

    public static boolean compare(int v1, long v2, int type)
    {
        return compare((long) v1, v2, type);
    }

    public static boolean compare(long v1, double v2, int type)
    {
        return compare((double) v1, v2, type);
    }

    public static boolean compare(double v1, long v2, int type)
    {
        return compare(v1, (double) v2, type);
    }

    public static boolean compare(long v1, float v2, int type)
    {
        return compare((float) v1, v2, type);
    }

    private static boolean compare(float v1, long v2, int type)
    {
        return compare(v1, (float) v2, type);
    }

    public static boolean compare(long v1, Object v2, int type)
    {
        boolean result = false;
        if (v2 == null)
        {
            result = compareNull(new Long(v1), null, type);
        }
        else
        {
            result = compare(v1, v2.toString(), type);
        }
        return result;
        
        /*
        if (v2 == null)
        {
            return compareNull(new Long(v1), null, type);
        }

        return compare(v1, v2.toString(), type);
        */
    }

    public static boolean compare(Object v1, long v2, int type)
    {
        boolean result = false;
        if (v1 == null)
        {
            result = compareNull(null, new Long(v2), type);
        }
        else
        {
            result = compare(v1.toString(), v2, type); 
        }
        return result; 
        
        /*
        if (v1 == null)
        {
            return compareNull(null, new Long(v2), type);
        }

        return compare(v1.toString(), v2, type);
        */
    }

    public static boolean compare(long v1, Object v2, int type, String dtCmp)
    {
        boolean result = false;
        if (v2 == null)
        {
            result = compareNull(new Long(v1), null, type);
        }
        else
        {
            result = compare(new Date(v1), v2, type);       
        }
        return result;
        /*
        if (v2 == null)
        {
            return compareNull(new Long(v1), null, type);
        }

        return compare(new Date(v1), v2, type);
        */
    }

    public static boolean compare(Object v1, long v2, int type, String dtCmp)
    {
        boolean result = false;
        if (v1 == null)
        {
            result = compareNull(null, new Long(v2), type);
        }
        else
        {
            result = compare(v1, new Date(v2), type); 
        }
        return result;  
        /*
        if (v1 == null)
        {
            return compareNull(null, new Long(v2), type);
        }

        return compare(v1, new Date(v2), type);
        */
    }

    public static boolean compare(long v1, long v2, int type, String dtCmp)
    {
        return compare(new Date(v1), new Date(v2), type, dtCmp);
    }

    //---------------------double
    public static boolean compare(double v1, double v2, int type)
    {
        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return v1 > v2;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return v1 >= v2;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return v1 < v2;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return v1 <= v2;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return v1 == v2;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return v1 != v2;
        }

        return false;
    }

    public static boolean compare(double v1, float v2, int type)
    {
        return compare(v1, (double) v2, type);
    }

    public static boolean compare(float v1, double v2, int type)
    {
        return compare((double) v1, v2, type);
    }

    public static boolean compare(double v1, int v2, int type)
    {
        return compare(v1, (double) v2, type);
    }

    public static boolean compare(int v1, double v2, int type)
    {
        return compare((double) v1, v2, type);
    }

    public static boolean compare(double v1, Object v2, int type)
    {
        boolean result = false;
        if (v2 == null)
        {
            result = compareNull(new Double(v1), null, type);
        }
        else
        {
            result = compare(v1, v2.toString(), type);
        }
        return result;
        /*
        if (v2 == null)
        {
            compareNull(new Double(v1), null, type);
        }

        return compare(v1, v2.toString(), type);
        */
    }

    public static boolean compare(Object v1, double v2, int type)
    {
        boolean result = false;
        if (v1 == null)
        {
            result = compareNull(null, new Double(v2), type);
        }
        else
        {
            result = compare(v1.toString(), v1, type);
        }
        return result;        
        /*
        if (v1 == null)
        {
            compareNull(null, new Double(v2), type);
        }

        return compare(v1.toString(), v1, type);
        */
    }

    //---------------------float
    public static boolean compare(float v1, float v2, int type)
    {
        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return v1 > v2;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return v1 >= v2;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return v1 < v2;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return v1 <= v2;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return v1 == v2;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return v1 != v2;
        }

        return false;
    }

    public static boolean compare(float v1, int v2, int type)
    {
        return compare(v1, (float) v2, type);
    }

    public static boolean compare(int v1, float v2, int type)
    {
        return compare((float) v1, v2, type);
    }

    public static boolean compare(float v1, Object v2, int type)
    {
        boolean result = false;
        if (v2 == null)
        {
            result = compareNull(new Float(v1), null, type);
        }
        else
        {
            result = compare(v1, v2.toString(), type);       
        }
        return result;
        /*
        if (v2 == null)
        {
            compareNull(new Float(v1), null, type);
        }

        return compare(v1, v2.toString(), type);
        */
    }

    public static boolean compare(Object v1, float v2, int type)
    {
        boolean result = false;
        if (v1 == null)
        {
            result = compareNull(null, new Float(v2), type);
        }
        else
        {
            result = compare(v1.toString(), v1, type); 
        }
        return result;
        /*
        if (v1 == null)
        {
            compareNull(null, new Float(v2), type);
        }

        return compare(v1.toString(), v1, type);
        */
    }

    //---------------------int
    public static boolean compare(int v1, int v2, int type)
    {
        if ((type == IGNORECASE_BIGGER) || (type == BIGGER))
        {
            return v1 > v2;
        }

        if ((type == IGNORECASE_BIGGER_EQUAL) || (type == BIGGER_EQUAL))
        {
            return v1 >= v2;
        }

        if ((type == IGNORECASE_LESS) || (type == LESS))
        {
            return v1 < v2;
        }

        if ((type == IGNORECASE_LESS_EQUAL) || (type == LESS_EQUAL))
        {
            return v1 <= v2;
        }

        if ((type == IGNORECASE_EQUAL) || (type == EQUAL))
        {
            return v1 == v2;
        }

        if ((type == IGNORECASE_NOT_EQUAL) || (type == NOT_EQUAL))
        {
            return v1 != v2;
        }

        return false;
    }

    public static boolean compare(int v1, Object v2, int type)
    {
        boolean result = false;
        if (v2 == null)
        {
            result = compareNull(new Integer(v1), null, type);
        }
        else
        {
            result = compare(v1, v2.toString(), type);
        }
        return result;        
        /*
        if (v2 == null)
        {
            return compareNull(new Integer(v1), null, type);
        }

        return compare(v1, v2.toString(), type);
        */
    }

    public static boolean compare(Object v1, int v2, int type)
    {       
        boolean result = false;
        if (v1 == null)
        {
            result = compareNull(null, new Integer(v2), type);
        }
        else
        {
            result = compare(v1.toString(), v2, type);
        }
        return result;
        /*
        if (v1 == null)
        {
            return compareNull(null, new Integer(v2), type);
        }
        return compare(v1.toString(), v2, type);
        */
    }

    //------------------- to_date
    public static Date to_date(Date v, String format)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(format);

        return formatter.parse(formatter.format(v), new ParsePosition(0));
    }

    public static Date to_date(Object v, String format)
    {
        Date d = boConvertUtils.convertToDate(v.toString(), null);

        return to_date(d, format);
    }

    public static Date to_date(long v, String format)
    {
        return to_date(new Date(v), format);
    }

    public static String to_char(Date v, String format)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(format);

        return formatter.format(v);
    }

    //--------------------- ROUND
    public static BigDecimal round(String val, int places)
    {
        //Math.round(Math.round(total.value * 1000) / 10) / 100
        double factor = Math.pow(10, places);
        double factor2 = Math.pow(10, -1 * places);
        BigDecimal temp1 = new BigDecimal(String.valueOf(factor));
        BigDecimal temp4 = new BigDecimal(String.valueOf(factor2));
        BigDecimal temp2 = new BigDecimal(val);
        BigDecimal temp3 = temp2.multiply(temp1);
        double tmp = Math.round(temp3.doubleValue());
        temp3 = new BigDecimal(String.valueOf(tmp));

        return temp3.multiply(temp4);
    }

    //--------------------- CONCAT
    private static String concat(String val1, String val2)
    {
        return val1 + val2;
    }

    public static String concat(Object val1, Object val2)
    {
        return val1.toString() + val2.toString();
    }

    public static String concat(long val1, long val2)
    {
        return concat(String.valueOf(val1), String.valueOf(val2));
    }

    public static String concat(int val1, int val2)
    {
        return concat(String.valueOf(val1), String.valueOf(val2));
    }

    public static String concat(int val1, long val2)
    {
        return concat(String.valueOf(val1), String.valueOf(val2));
    }

    public static String concat(long val1, int val2)
    {
        return concat(String.valueOf(val1), String.valueOf(val2));
    }
}
