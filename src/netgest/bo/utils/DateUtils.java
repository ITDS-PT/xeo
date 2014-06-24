/*Enconding=UTF-8*/
package netgest.bo.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class DateUtils
{
    /**
     *
     * @since
     */
    private DateUtils()
    {
    }

    public static Date getToday()
    {
        Calendar c = Calendar.getInstance();
        c.clear(c.HOUR_OF_DAY);
        c.clear(c.HOUR);
        c.clear(c.MINUTE);
        c.clear(c.SECOND);
        c.clear(c.MILLISECOND);

        return new Date(c.getTimeInMillis());
    }

    public static Date getNowHours()
    {
        Calendar c = Calendar.getInstance();
        c.clear(c.MINUTE);
        c.clear(c.SECOND);
        c.clear(c.MILLISECOND);

        return new Date(c.getTimeInMillis());
    }

    public static Date getNowMinutes()
    {
        Calendar c = Calendar.getInstance();
        c.clear(c.SECOND);
        c.clear(c.MILLISECOND);

        return new Date(c.getTimeInMillis());
    }

    public static Date getNow()
    {
        Calendar c = Calendar.getInstance();
        c.clear(c.MILLISECOND);

        return new Date(c.getTimeInMillis());
    }

    public static Date sumToDate(java.util.Date date, long year)
    {
        return sumToDate(date, year, 0, 0, 0, 0, 0);
    }

    public static Date sumToDate(java.util.Date date, long year, long month)
    {
        return sumToDate(date, year, month, 0, 0, 0, 0);
    }

    public static Date sumToDate(java.util.Date date, long year, long month,
        long day)
    {
        return sumToDate(date, year, month, day, 0, 0, 0);
    }

    public static Date sumToDate(java.util.Date date, long year, long month,
        long day, long hour)
    {
        return sumToDate(date, year, month, day, hour, 0, 0);
    }

    public static Date sumToDate(java.util.Date date, long year, long month,
        long day, long hour, long minutes)
    {
        return sumToDate(date, year, month, day, hour, minutes, 0);
    }

    //long
    public static Date sumToDate(long date, long year)
    {
        return sumToDate(new Date(date), year, 0, 0, 0, 0, 0);
    }

    public static Date sumToDate(long date, long year, long month)
    {
        return sumToDate(new Date(date), year, month, 0, 0, 0, 0);
    }

    public static Date sumToDate(long date, long year, long month, long day)
    {
        return sumToDate(new Date(date), year, month, day, 0, 0, 0);
    }

    public static Date sumToDate(long date, long year, long month, long day,
        long hour)
    {
        return sumToDate(new Date(date), year, month, day, hour, 0, 0);
    }

    public static Date sumToDate(long date, long year, long month, long day,
        long hour, long minutes)
    {
        return sumToDate(new Date(date), year, month, day, hour, minutes, 0);
    }

    public static Date sumToDate(long date, long year, long month, long day,
        long hour, long minutes, long sec)
    {
        return sumToDate(new Date(date), year, month, day, hour, minutes, sec);
    }

    //object
    public static Date sumToDate(Object date, long year)
    {
        return sumToDate((Date) date, year, 0, 0, 0, 0, 0);
    }

    public static Date sumToDate(Object date, long year, long month)
    {
        return sumToDate((Date) date, year, month, 0, 0, 0, 0);
    }

    public static Date sumToDate(Object date, long year, long month, long day)
    {
        return sumToDate((Date) date, year, month, day, 0, 0, 0);
    }

    public static Date sumToDate(Object date, long year, long month, long day,
        long hour)
    {
        return sumToDate((Date) date, year, month, day, hour, 0, 0);
    }

    public static Date sumToDate(Object date, long year, long month, long day,
        long hour, long minutes)
    {
        return sumToDate((Date) date, year, month, day, hour, minutes, 0);
    }

    public static Date sumToDate(Object date, long year, long month, long day,
        long hour, long minutes, long sec)
    {
        return sumToDate((Date) date, year, month, day, hour, minutes, sec);
    }

    public static Date sumToDate(java.util.Date date, long year, long month,
        long day, long hour, long minutes, long sec)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, (int)year);
        c.add(Calendar.MONTH, (int)month);
        c.add(Calendar.DATE, (int)day);
        c.add(Calendar.HOUR, (int)hour);
        c.add(Calendar.MINUTE, (int)minutes);
        c.add(Calendar.SECOND, (int)sec);

        return new Date(c.getTimeInMillis());
    }

    //SUBTRACT
    public static Date subtractToDate(java.util.Date date, long year)
    {
        return sumToDate(date, year, 0, 0, 0, 0, 0);
    }

    public static Date subtractToDate(java.util.Date date, long year, long month)
    {
        return sumToDate(date, year, month, 0, 0, 0, 0);
    }

    public static Date subtractToDate(java.util.Date date, long year, long month,
        long day)
    {
        return sumToDate(date, year, month, day, 0, 0, 0);
    }

    public static Date subtractToDate(java.util.Date date, long year, long month,
        long day, long hour)
    {
        return sumToDate(date, year, month, day, hour, 0, 0);
    }

    public static Date subtractToDate(java.util.Date date, long year, long month,
        long day, long hour, long minutes)
    {
        return sumToDate(date, year, month, day, hour, minutes, 0);
    }

    public static Date subtractToDate(java.util.Date date, long year, long month,
        long day, long hour, long minutes, long sec)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, (int)-year);
        c.add(Calendar.MONTH, (int)-month);
        c.add(Calendar.DATE, (int)-day);
        c.add(Calendar.HOUR, (int)-hour);
        c.add(Calendar.MINUTE, (int)-minutes);
        c.add(Calendar.MINUTE, (int)-sec);

        return new Date(c.getTimeInMillis());
    }

    //long
    public static Date subtractToDate(long date, long year)
    {
        return subtractToDate(new Date(date), year, 0, 0, 0, 0, 0);
    }

    public static Date subtractToDate(long date, long year, long month)
    {
        return subtractToDate(new Date(date), year, month, 0, 0, 0, 0);
    }

    public static Date subtractToDate(long date, long year, long month, long day)
    {
        return subtractToDate(new Date(date), year, month, day, 0, 0, 0);
    }

    public static Date subtractToDate(long date, long year, long month, long day,
        long hour)
    {
        return subtractToDate(new Date(date), year, month, day, hour, 0, 0);
    }

    public static Date subtractToDate(long date, long year, long month, long day,
        long hour, long minutes)
    {
        return subtractToDate(new Date(date), year, month, day, hour, minutes, 0);
    }

    public static Date subtractToDate(long date, long year, long month, long day,
        long hour, long minutes, long sec)
    {
        return subtractToDate(new Date(date), year, month, day, hour, minutes,
            sec);
    }

    //object
    public static Date subtractToDate(Object date, long year)
    {
        return subtractToDate((Date) date, year, 0, 0, 0, 0, 0);
    }

    public static Date subtractToDate(Object date, long year, long month)
    {
        return subtractToDate((Date) date, year, month, 0, 0, 0, 0);
    }

    public static Date subtractToDate(Object date, long year, long month, long day)
    {
        return subtractToDate((Date) date, year, month, day, 0, 0, 0);
    }

    public static Date subtractToDate(Object date, long year, long month,
        long day, long hour)
    {
        return subtractToDate((Date) date, year, month, day, hour, 0, 0);
    }

    public static Date subtractToDate(Object date, long year, long month,
        long day, long hour, long minutes)
    {
        return subtractToDate((Date) date, year, month, day, hour, minutes, 0);
    }

    public static Date subtractToDate(Object date, long year, long month,
        long day, long hour, long minutes, long sec)
    {
        return subtractToDate((Date) date, year, month, day, hour, minutes, sec);
    }

    //adicionar e sutrair dias uteis
    public static Date sumToUtilDate(java.util.Date date, long year, EboContext ctx)
    {
        return sumToUtilDate(date, year, 0, 0, 0, 0, 0, ctx);
    }

    public static Date sumToUtilDate(java.util.Date date, long year, long month, EboContext ctx)
    {
        return sumToUtilDate(date, year, month, 0, 0, 0, 0, ctx);
    }

    public static Date sumToUtilDate(java.util.Date date, long year, long month,
        long day, EboContext ctx)
    {
        return sumToUtilDate(date, year, month, day, 0, 0, 0, ctx);
    }

    //long
    public static Date sumToUtilDate(long date, long year, EboContext ctx)
    {
        return sumToUtilDate(new Date(date), year, 0, 0, 0, 0, 0, ctx);
    }

    public static Date sumToUtilDate(long date, long year, long month, EboContext ctx)
    {
        return sumToUtilDate(new Date(date), year, month, 0, 0, 0, 0, ctx);
    }

    public static Date sumToUtilDate(long date, long year, long month, long day, EboContext ctx)
    {
        return sumToUtilDate(new Date(date), year, month, day, 0, 0, 0, ctx);
    }

    public static Date sumToUtilDate(long date, long year, long month, long day,
        long hora, EboContext ctx)
    {
        return sumToUtilDate(new Date(date), year, month, day, hora, 0, 0, ctx);
    }

    //object
    public static Date sumToUtilDate(Object date, long year, EboContext ctx)
    {
        return sumToUtilDate((Date) date, year, 0, 0, 0, 0, 0, ctx);
    }

    public static Date sumToUtilDate(Object date, long year, long month, EboContext ctx)
    {
        return sumToUtilDate((Date) date, year, month, 0, 0, 0, 0, ctx);
    }

    public static Date sumToUtilDate(Object date, long year, long month, long day, EboContext ctx)
    {
        return sumToUtilDate((Date) date, year, month, day, 0, 0, 0, ctx);
    }

    public static Date sumToUtilDate(java.util.Date date, long year, long month,
        long day, long hora, long minutes, long sec, EboContext ctx)
    {
        return addUtilDate(addMonthUtilDate(addYearUtilDate(date, year, ctx), month, ctx),
            day, ctx);
    }

    //SUBTRACT
    public static Date subtractToUtilDate(java.util.Date date, long year, EboContext ctx)
    {
        return sumToUtilDate(date, year, 0, 0, 0, 0, 0, ctx);
    }

    public static Date subtractToUtilDate(java.util.Date date, long year,
        long month, EboContext ctx)
    {
        return sumToUtilDate(date, year, month, 0, 0, 0, 0, ctx);
    }

    public static Date subtractToUtilDate(java.util.Date date, long year,
        long month, long day, EboContext ctx)
    {
        return sumToUtilDate(date, year, month, day, 0, 0, 0, ctx);
    }

    public static Date subtractToUtilDate(java.util.Date date, long year,
        long month, long day, long hora, EboContext ctx)
    {
        return sumToUtilDate(date, year, month, day, hora, 0, 0, ctx);
    }

    public static Date subtractToUtilDate(java.util.Date date, long year,
        long month, long day, long hora, long minutes, EboContext ctx)
    {
        return sumToUtilDate(date, year, month, day, hora, minutes, 0, ctx);
    }

    public static Date subtractToUtilDate(java.util.Date date, long year,
        long month, long day, long hora, long minutes, long sec, EboContext ctx)
    {
        return subtractUtilDate(subtractMonthUtilDate(subtractYearUtilDate(
                    date, year, ctx), month, ctx), day, ctx);
    }

    //long
    public static Date subtractToUtilDate(long date, long year, EboContext ctx)
    {
        return subtractToUtilDate(new Date(date), year, 0, 0, 0, 0, 0, ctx);
    }

    public static Date subtractToUtilDate(long date, long year, long month, EboContext ctx)
    {
        return subtractToUtilDate(new Date(date), year, month, 0, 0, 0, 0, ctx);
    }

    public static Date subtractToUtilDate(long date, long year, long month,
        long day, EboContext ctx)
    {
        return subtractToUtilDate(new Date(date), year, month, day, 0, 0, 0, ctx);
    }

    //object
    public static Date subtractToUtilDate(Object date, long year, EboContext ctx)
    {
        return subtractToUtilDate((Date) date, year, 0, 0, 0, 0, 0, ctx);
    }

    public static Date subtractToUtilDate(Object date, long year, long month, EboContext ctx)
    {
        return subtractToUtilDate((Date) date, year, month, 0, 0, 0, 0, ctx);
    }

    public static Date subtractToUtilDate(Object date, long year, long month,
        long day, EboContext ctx)
    {
        return subtractToUtilDate((Date) date, year, month, day, 0, 0, 0, ctx);
    }

    //-----
    private static Date addYearUtilDate(Date d, long year, EboContext ctx)
    {
        long nDias = year * 364;

        return addUtilDate(d, nDias, ctx);
    }

    private static Date addMonthUtilDate(Date d, long month, EboContext ctx)
    {
        long nDias = month * 31;

        return addUtilDate(d, nDias, ctx);
    }

    private static Date addUtilDate(Date d, long dias, EboContext ctx)
    {
        
        if(dias == 0) return d;
        if(dias < 0) return subtractUtilDate(d, -dias, ctx);

        Calendar c = Calendar.getInstance();
        c.setTime(d);
        
        c.add(Calendar.DATE, (int)dias);
        
        int nSatSund = numberOfSatSundays(d, c.getTime(), ctx);
        int nHoli = numberOfHolidays(d, c.getTime(), true, ctx);
        int toAdd = nSatSund + nHoli;
//        if(!isWorkingDay(c, ctx))
//        {
//            toAdd++;
//        }

        for (long i = 0; i < toAdd; i++)
        {
            c.add(Calendar.DATE, 1);

            if (!isWorkingDay(c, ctx))
            {
                i--;
            }
        }

        return c.getTime();
    }

    private static int numberOfBusinessDays(Date d1, Date d2, EboContext ctx)
    {
        if(d1.before(d2))
        {
            return numberOfBusinessDays(d1, d2, diffInDays(d1, d2), ctx);
        }
        return numberOfBusinessDays(d2, d1, diffInDays(d1, d2), ctx);
    }

    private static int numberOfBusinessDays(Date d1, Date d2, long days,EboContext ctx)
    {
        Calendar cEnd = Calendar.getInstance();
        cEnd.setTime(d2);
        
        int weeks = (int)days/7;
        int extraDays = (int)(days - weeks * 7);
        int businessDays = weeks * 5;
        cEnd.add(Calendar.DATE, 1);
        for(int i = 0; i < extraDays; i++)
        {
            cEnd.add(Calendar.DATE, -1);
            if (isWeekDay(cEnd, ctx))
            {
                businessDays ++;
            }
        }
        return businessDays;
    }

    private static int numberOfSatSundays(Date d1, Date d2, EboContext ctx)
    {
        if(d1.before(d2))
        {
            return numberOfSatSundays(d1, d2, diffInDays(d1, d2), ctx);
        }
        return numberOfSatSundays(d2, d1, diffInDays(d1, d2), ctx);
    }

    private static int numberOfSatSundays(Date d1, Date d2, long days,EboContext ctx)
    {
        return (int)days - numberOfBusinessDays(d1, d2, days, ctx);
    }

    private static Date subtractYearUtilDate(Date d, long year, EboContext ctx)
    {
        long nDias = year * 364;

        return subtractUtilDate(d, nDias, ctx);
    }

    private static Date subtractMonthUtilDate(Date d, long month, EboContext ctx)
    {
        long nDias = month * 31;

        return subtractUtilDate(d, nDias, ctx);
    }

    private static Date subtractUtilDate(Date d, long dias, EboContext ctx)
    {
        if(dias == 0) return d;
        if(dias < 0) return addUtilDate(d, -dias, ctx);

        Calendar c = Calendar.getInstance();
        c.setTime(d);
        
        c.add(Calendar.DATE, (int)-dias);
        
        int nSatSund = numberOfSatSundays(c.getTime(), d, ctx);
        int nHoli = numberOfHolidays(c.getTime(), d, true, ctx);
        int toAdd = nSatSund + nHoli;

        for (long i = 0; i < toAdd; i++)
        {
            c.add(Calendar.DATE, -1);

            if (!isWorkingDay(c, ctx))
            {
                i--;
            }
        }

        return c.getTime();
    }

    public static boolean isWeekDay(Date date, EboContext ctx)
    {
        Calendar d = Calendar.getInstance();
        d.setTime(date);
        return isWeekDay(d, ctx);
    }

    public static boolean isWeekDay(Calendar date, EboContext ctx)
    {
        boolean isWeekDay = false;
        long weekDay = date.get(Calendar.DAY_OF_WEEK);

        if ((weekDay != Calendar.SATURDAY) && (weekDay != Calendar.SUNDAY))
        {
            isWeekDay = true;
        }

        return isWeekDay;
    }

    public static boolean isWorkingDay(Date date, EboContext ctx)
    {
        Calendar d = Calendar.getInstance();
        d.setTime(date);
        return isWorkingDay(d, ctx);
    }

    public static boolean isWorkingDay(Calendar date, EboContext ctx)
    {
        boolean isWorkDay = false;
        long workDay = date.get(Calendar.DAY_OF_WEEK);

        if ((workDay != Calendar.SATURDAY) && (workDay != Calendar.SUNDAY) &&
                !isHoliday(date.getTime(), ctx))
        {
            isWorkDay = true;
        }

        return isWorkDay;
    }

    // Este algoritmo é de J.-M.Oudin (1940) e foi impresso no Explanatory
    // Supplement to the Astronomical Almanac, ed. P.K. Seidelmann (1992).
    private static Date getEaster(int year)
    {
      int ano = year;
      
      int var1 = ano/100;
      int var2 = ano - 19*(ano/19);
      int var3 = (var1 - 17)/25;
      int var4 = var1 - var1/4 - (var1-var3)/3 + 19*var2 + 15;
      var4 = var4 - 30*(var4/30);
      var4 = var4 - (var4/28)*(1-(1/28)*(29/(var4+1))*((21-var2)/11));
      int var5 = ano + ano/4 + var4 + 2 -var1 + var1/4;
      var5 = var5 - 7*(var5/7);
      int var6 = var4 - var5;
      int mes = 3 + (var6+40)/44;
      int dia = var6 + 28 - 31*(mes/4);
      
      Calendar cal = Calendar.getInstance();
      cal.set(ano-1900, mes-1, dia);
      
      return (cal.getTime());      
    }
    
    private static boolean isSpecialHoliday(Date d, String name)
    {
      if("pascoa".equals(name))
      {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return getEaster( cal.get(Calendar.YEAR) + 1900 ).equals(d);
      }
      else if("sextaSanta".equals(name))
      {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return subtractToDate(getEaster( cal.get(Calendar.YEAR) + 1900 ),0,0,2).equals(d);
      }
      else if("corpoDeus".equals(name))
      {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return sumToDate(getEaster( cal.get(Calendar.YEAR) + 1900 ),0,0,62).equals(d);
      }
      
      return false;
    }
    
    private static int getSpecialHolidays(Date inic, Date fim, String names, boolean countOnlyWeekDays)
    {
      int count = 0;
      Date easter = null;
      Date sextaS = null;
      Date corpoD = null;
      
      Calendar calInic = Calendar.getInstance();
      Calendar calFim = Calendar.getInstance();
      calInic.setTime(inic);
      calFim.setTime(fim);
      
      for (int i = calInic.get(Calendar.YEAR) + 1900; i <= calFim.get(Calendar.YEAR) + 1900 ; i++) 
      {
        easter = getEaster(i);
        sextaS = subtractToDate(easter.getTime(),0,0,2);
        corpoD = sumToDate(easter.getTime(),0,0,60);
            
        //páscoa é sempre a um Domingo
        if(!countOnlyWeekDays && names.indexOf("pascoa")>-1)
        {
          if( (easter.after(inic) && easter.before(fim)) || easter.equals(inic) || easter.equals(fim))
            count++;
        }
        
        //sexta feira santa é sempre uma sexta
        if(names.indexOf("sextaSanta")>-1)
        {
          if((sextaS.after(inic) && sextaS.before(fim)) || sextaS.equals(inic) || sextaS.equals(fim))
            count++;
        }
        
        //corpo de deus é sempre uma quinta
        if(names.indexOf("corpoDeus")>-1)
        {
          if((corpoD.after(inic) && corpoD.before(fim)) || corpoD.equals(inic) || corpoD.equals(fim))
            count++;
        }                  
      }
      
      return count;
      
    }
    
    public static boolean isHoliday(Date d, EboContext ctx)
    {
        Connection cn = null;
        boolean holiday = false;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rslt = null;
        ResultSet rslt2 = null;
        ResultSet rslt3 = null;
        
        try
        {
            cn= ctx.getConnectionData();

            //verificar se é um feriado especial
            String sql = "SELECT defined FROM oholiday WHERE defined IS NOT NULL";
            ps=cn.prepareStatement(sql);
            rslt= ps.executeQuery();
            while(rslt.next())
            {
                if(isSpecialHoliday(d, rslt.getString(1)))
                  return true;
            }
            
            //verificar se é feriado repetitivo
            SimpleDateFormat formatter = new SimpleDateFormat("MMdd");
            String dSt = formatter.format(d);
            sql = "SELECT count(*) from oholiday WHERE repeat=1 AND to_char(day,'MMDD')="+ dSt;
            ps2=cn.prepareStatement(sql);
            rslt2= ps2.executeQuery();
            if(rslt2.next())
            {
                if(rslt2.getInt(1) != 0)
                  return true;
            }
          
            
            formatter = new SimpleDateFormat("yyyyMMdd");
            dSt = formatter.format(d);
            sql = "SELECT count(*) from oholiday WHERE  (repeat=0 OR repeat is null) AND to_char(day,'YYYYMMDD')=" + dSt;
            ps3=cn.prepareStatement(sql);
            rslt3= ps3.executeQuery();
            if(rslt3.next())
            {
                return rslt3.getInt(1) != 0;
            }
            //boObjectList list = boObjectList.list(ctx,boql.toString());
            //return list == null ? false: (list.getRecordCount() == 0) ? false:true;            
        }
        catch (Exception e)
        {
            //ignora
        }
        finally
        {
            try{
                if(rslt != null)
                {
                    rslt.close();
                }
                if(rslt2 != null)
                {
                    rslt2.close();
                }
                if(rslt3 != null)
                {
                    rslt3.close();
                }
            }catch(Exception e){}
            try{
                if(ps != null)
                {
                    ps.close();
                }
                if(ps2 != null)
                {
                    ps2.close();
                }
                if(ps3 != null)
                {
                    ps3.close();
                }
            }catch(Exception e){}
        }        
        return false;
    }

    public static int numberOfHolidays(Calendar from, Calendar to, boolean countOnlyWeekDays, EboContext ctx)
    {
        return numberOfHolidays(from.getTime(), to.getTime(), countOnlyWeekDays, ctx);
    }

    public static int numberOfHolidays(Date from, Date to, boolean countOnlyWeekDays, EboContext ctx)
    {
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rslt = null;
        ResultSet rslt2 = null;
        ResultSet rslt3 = null;
        Connection cn;
        int count=0;
        try
        {
            cn= ctx.getConnectionData();


            //contar feriados especiais
            String sql = "SELECT defined FROM oholiday WHERE defined IS NOT NULL";
            ps=cn.prepareStatement(sql);
            rslt= ps.executeQuery();
            String nomes = null;
            while(rslt.next())
            {
                if(nomes==null)
                  nomes=rslt.getString(1);
                else
                  nomes = nomes.concat("-").concat(rslt.getString(1));
            }
            if(nomes!=null)
              count += getSpecialHolidays(from, to, nomes, countOnlyWeekDays);
            
            String weekDay = "";
            if(countOnlyWeekDays)
             weekDay = "\nand to_char(day, 'D') <> 1 and to_char(day, 'D') <> 7" ;
             
            //contar feriados repetitivos
            SimpleDateFormat formatter = new SimpleDateFormat("MMdd");
            String dfromSt = formatter.format(from);
            String dtoSt = formatter.format(to);
            
            Calendar calTo   = Calendar.getInstance();
            Calendar calFrom = Calendar.getInstance();
            calTo.setTime(to);
            calTo.setTime(from);
            
            if( calTo.get(Calendar.YEAR) == calFrom.get(Calendar.YEAR) )
            {
              sql = "SELECT count(*) from oholiday\nWHERE to_char(day,'MMDD')>="+ dfromSt +"\nAND to_char(day,'MMDD')<="+dtoSt + weekDay;
            }
            else
            {
              sql = "SELECT SUM(data) FROM (\n"+
                      "(SELECT count(*) data from oholiday WHERE repeat=1\nAND to_char(day,'MMDD')>="+ dfromSt + weekDay + ")\n"+
                    "UNION\n"+
                      "(SELECT count(*) data from oholiday WHERE repeat=1\nAND to_char(day,'MMDD')<="+ dtoSt + weekDay + ")\n"+
                    "UNION\n"+
                      "(SELECT (count(*)*"+ (calTo.get(Calendar.YEAR)-calFrom.get(Calendar.YEAR)-1) +") data from oholiday WHERE repeat=1"+ weekDay  + ")\n"+
                    ")";
            }
            ps2=cn.prepareStatement(sql);
            rslt2= ps2.executeQuery();
            if(rslt2.next())
            {
                count += rslt2.getInt(1);
            }


            StringBuffer boql = new StringBuffer();
            formatter = new SimpleDateFormat("yyyyMMdd");
            dfromSt = formatter.format(from);
            dtoSt = formatter.format(to);
            boql.append("SELECT count(*) from oholiday WHERE (repeat=0 OR repeat is null) AND day > to_date('");
            boql.append(dfromSt).append("', 'YYYYMMDD')")
                .append(" and day < (to_date('")
                .append(dtoSt).append("', 'YYYYMMDD') + 1)");
            if(countOnlyWeekDays)
            {
                boql.append(weekDay);
            }
        
            ps3=cn.prepareStatement(boql.toString());
            rslt3= ps3.executeQuery();
            if(rslt3.next())
            {
                count += rslt3.getInt(1);
            }
//            boObjectList list = boObjectList.list(ctx,boql.toString());
//            return list == null ? 0:(int)list.getRecordCount();
        }
        catch (Exception e)
        {
            //ignora
        }  
        finally
        {
            try{
                if(rslt != null)
                {
                    rslt.close();
                }
                if(rslt2 != null)
                {
                    rslt2.close();
                }
                if(rslt3 != null)
                {
                    rslt3.close();
                }
            }catch(Exception e){}
            try{
                if(ps != null)
                {
                    ps.close();
                }
                if(ps2 != null)
                {
                    ps2.close();
                }
                if(ps3 != null)
                {
                    ps3.close();
                }
            }catch(Exception e){}
        }
        return count;
    }

    public static int _numberOfHolidays(Date _from, Date _to, boolean countOnlyWeekDays, EboContext ctx)
    {
        try
        {
            Date from = truncateHour(_from);
            Date to = truncateHour(_to);
            StringBuffer boql = new StringBuffer();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String dfromSt = formatter.format(from);
            String dtoSt = formatter.format(to);
            
            boql.append("SELECT holiday WHERE 1 = 1");
/*            
            boql.append("SELECT holiday WHERE day > to_date('");
            boql.append(dfromSt).append("', 'YYYYMMDD')")
                .append(" and day < (to_date('")
                .append(dtoSt).append("', 'YYYYMMDD') + 1)");
*/
/*
            if(countOnlyWeekDays)
            {
                boql.append(" and to_char(day, 'D') <> 1 and to_char(day, 'D') <> 7");
            }
*/        
            boObjectList list = boObjectList.list(ctx,boql.toString());
//            if(!countOnlyWeekDays)
//            {
//                return list == null ? 0:(int)list.getRecordCount();
//            }            
            boObject aux;
            Date d;
            Calendar h = Calendar.getInstance();
            int ret = 0;
            while(list != null && list.next())
            {
                aux = list.getObject();
                d = aux.getAttribute("day").getValueDate();
                if(d != null)
                {
                    d = truncateHour(d);
                    h.setTime(d);
                    if(!countOnlyWeekDays)
                    {
                        if(d.after(from) && (d.before(to) || d.equals(to)))
                        {
                            ret++;
                        }
                    }
                    else
                    {
                        if(h.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                            h.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY &&
                            d.after(from) && (d.before(to) || d.equals(to)) 
                            )
                        {
                            ret++;
                        }
                    }
                }
            }
            return ret;            
        }
        catch (Exception e)
        {
            //ignora
        }        
        return 0;
    }

    public static Date truncateHour(Date d)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        
        c.set(c.HOUR_OF_DAY, 0);
        c.set(c.MINUTE, 0);
        c.set(c.SECOND, 0);
        c.set(c.MILLISECOND, 0);
        return c.getTime();
        
    }

	public static int diffInIntDays(Date date1, Date date2)
    {
        Long l = new Long(diffInDays(date1, date2));
        Integer i = new Integer(l.toString());
        return i.intValue();
    }
	
    public static long diffInDays(Date date1, long date2)
    {
        return diffInDays(date1, new Date(date2));
    }

    public static long diffInDays(long date1, Date date2)
    {
        return diffInDays(new Date(date1), date2);
    }
    
    public static long diffInDays(Date date1, Date date2)
    {
        if(date1 != null && date2!= null)
        {
            Date d1 = truncateHour(date1);
            Date d2 = truncateHour(date2);
            long millsDia = 86400000; // um dia tem 86400000 ms
    
            if(d1.before(d2))
                return Math.abs((d2.getTime() - d1.getTime())/millsDia);
            else
                return -Math.abs((d1.getTime() - d2.getTime())/millsDia);
        }
        return 0;
    }
	public static int diffInIntUtilsDays(Date date1, Date date2, EboContext ctx)
    {
        Long l = new Long(diffInUtilsDays(date1, date2, ctx));
        Integer i = new Integer(l.toString());
        return i.intValue();
    }
    public static long diffInUtilsDays(long date1, Date date2, EboContext ctx)
    {
        return diffInUtilsDays(new Date(date1), date2, ctx);   
    }
    
    public static long diffInUtilsDays(Date date1, long date2, EboContext ctx)
    {
        return diffInUtilsDays(date1, new Date(date2), ctx);        
    }

    public static long diffInUtilsDays(Date date1, Date date2, EboContext ctx)
    {
        if(date1 != null && date2!= null)
        {
            Calendar c = Calendar.getInstance();
            c.setTime(date1);
            
            int nSatSund; 
            int nHoli;
            int total;
            if(date1.before(date2))
            {
                nSatSund =  numberOfSatSundays(date1, date2, ctx);
                nHoli = numberOfHolidays(date1, date2, true, ctx);
                total = (int)diffInDays(date1, date2);
                return total - nSatSund - nHoli;
            }
            else
            {
                nSatSund =  numberOfSatSundays(date2, date1, ctx);
                nHoli = numberOfHolidays(date2, date1, true, ctx);
                total = (int)diffInDays(date2, date1);
                return -(total - nSatSund - nHoli);
            }
        }
        return 0;
    }

    public static boolean isHoliday(Calendar d, EboContext ctx)
    {
        return isHoliday(d.getTime(), ctx);
    }
    
    public static Date sumDurationToDate(String durationValue, Date dateToSum)
    {
        if(dateToSum == null) return null;
        if(durationValue != null && !"".equals(durationValue))
        {
            Calendar c = Calendar.getInstance();
            c.setTime(dateToSum);
            String[] realDuration = durationValue.split("\\.");
            if(realDuration.length == 1)
            {
                int m = Integer.parseInt(durationValue);
                c.add(Calendar.MINUTE, m);
            }
            else 
            {
                int m = Integer.parseInt(realDuration[0]);
                c.add(Calendar.MINUTE,m);

                int s = Integer.parseInt(realDuration[1]);      
                s = s*30/5;
                c.add(Calendar.SECOND,s);                                                                                 
                
            }
            return c.getTime();
        }
        return dateToSum;
    }
    
    public static Date subtractDurationToDate(String durationValue, Date dateToSum)
    {
        if(durationValue != null && !"".equals(durationValue))
        {
            Calendar c = Calendar.getInstance();
            String[] realDuration = durationValue.split("\\.");
            if(realDuration.length == 1)
            {
                int m = Integer.parseInt(durationValue);
                c.add(Calendar.MINUTE, -m);
            }
            else 
            {
                int m = Integer.parseInt(realDuration[0]);
                c.add(Calendar.MINUTE,-m);

                int s = Integer.parseInt(realDuration[1]);      
                s = s*30/5;
                c.add(Calendar.SECOND,-s);                                                                                 
                
            }
            return c.getTime();
        }
        return dateToSum;
    }
    
}
