/*Enconding=UTF-8*/
package netgest.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.Time;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.Vector;
import netgest.bo.ql.Triple;
import netgest.bo.runtime.*;

import netgest.bo.system.boApplication;



public class WorkPlaceDateUtils 
{
//  private boolean[] workingDays = new boolean[7];
  private long[] workingHours = new long[7];
  private Vector[] workingPeriod = new Vector[7];
  private long totalworking = 0;
  private int total_work_days = 0;
  public static int[] daysWeek = {Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, 
                                    Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};
  private Calendar[] holidays = null;
  private Calendar user_cal=null;
  
  public WorkPlaceDateUtils(boObject user)
  {
    try{
      boObject wsch = getWorkSchedule(user);
      user_cal = getCalendar(user);
      initDayWork( wsch.getAttribute("day1").getObject(), 1);
      initDayWork( wsch.getAttribute("day2").getObject(), 2);
      initDayWork( wsch.getAttribute("day3").getObject(), 3);
      initDayWork( wsch.getAttribute("day4").getObject(), 4);
      initDayWork( wsch.getAttribute("day5").getObject(), 5);
      initDayWork( wsch.getAttribute("day6").getObject(), 6);
      initDayWork( wsch.getAttribute("day7").getObject(), 7);
      
      
      initHolidays(user);
    }catch(boRuntimeException e){}
  }
  
  private void initHolidays(boObject user)throws boRuntimeException
  {
    boObject workp = getWorkplace(user);
    bridgeHandler bfer = workp.getBridge("fixedHolidays");
    bfer.beforeFirst();
    Calendar[] aux = new Calendar[bfer.getRowCount()];
    int i = 0;
    while(bfer.next())
    {
      String def = bfer.getObject().getAttribute("defined").getValueString();
      if(def != null && def.equals(""))
      {
        Calendar c = (Calendar)user_cal.clone();
        Date curr = bfer.getObject().getAttribute("day").getValueDate();
        c.setTime(truncateHour(curr));
        aux[i] = c;
        i++;
      }
    }
    holidays = aux;
  }
  
  private void initDayWork(boObject day_work, int day_number) throws boRuntimeException
  {
    int working = 0;
    workingPeriod[day_number-1] = new Vector();
    if(day_work == null)
    {
      working = 0;
    }
    else
    {
      bridgeHandler bper = day_work.getBridge("periods");
      working = 0;
      bper.beforeFirst();
      while(bper.next())
      {
        boObject per = bper.getObject();
        if("1".equals( per.getAttribute("workTime").getValueString()))
        {
          
          String start = per.getAttribute("beginTimePeriod").getValueString();
          String end = per.getAttribute("endTimePeriod").getValueString();
          int spos = start.indexOf(":");
          if(spos < 1)
          {
            continue;
          }
          String shour = start.substring(0, spos);
          String smin = start.substring(spos+1);
          int ishour = Integer.parseInt(shour);
          int ismin = Integer.parseInt(smin);
          
          spos = end.indexOf(":");
          if(spos < 1)
          {
            continue;
          }
          String ehour = end.substring(0, spos);
          String emin = end.substring(spos+1);
          int iehour = Integer.parseInt(ehour);
          int iemin = Integer.parseInt(emin);
          Calendar c1 = (Calendar)user_cal.clone();
          Calendar c2 = (Calendar)user_cal.clone();
          c1.set(Calendar.AM_PM, Calendar.AM);
          c1.set(Calendar.HOUR, ishour);
          c1.set(Calendar.MINUTE, ismin);
          c1.set(Calendar.SECOND, 0);
          c1.set(Calendar.MILLISECOND, 0);
          
          c2.set(Calendar.AM_PM, Calendar.AM);
          c2.set(Calendar.HOUR, iehour);
          c2.set(Calendar.MINUTE, iemin);
          c2.set(Calendar.SECOND, 0);
          c2.set(Calendar.MILLISECOND, 0);
          long w = c2.getTimeInMillis() - c1.getTimeInMillis();
          Triple tw = new Triple(c1,c2, new Long(w));
          addWorkingPeriod(tw, day_number-1 );
          working += w;
        }
      }
      
    }
    totalworking += working;
    if(working != 0)
      total_work_days++;
    workingHours[day_number-1] = working;
  }
  
  private void addWorkingPeriod(Triple tripleW, int day_number) 
  {
    Vector v = workingPeriod[day_number];
    Calendar c = (Calendar)tripleW.getFirst();
    Date ddd = c.getTime();
    for(int i=0; i < v.size(); i++)
    {
      Calendar cv = (Calendar)((Triple)v.get(i)).getFirst();
      if(c.getTimeInMillis() < cv.getTimeInMillis())
      {
        v.add(i, tripleW);
        return;
      }
    }
    v.add(tripleW);
  }
  
  private static boObject getWorkplace(boObject user) throws boRuntimeException
  {
    if(user == null)
      return null;
    boObject workp = user.getAttribute("workplace").getObject();
    if(workp == null)
    {
      workp = user.getEboContext().getApplication().getApplicationWorkplace(user.getEboContext());
    }
    return workp;
  }
  
  private static boObject getWorkSchedule(boObject user) throws boRuntimeException
  {
    if(user == null)
      return null;
    boObject workw = user.getAttribute("workSchedule").getObject();
    if(workw == null)
    {
      boObject workp = getWorkplace(user);
      workw = workp.getAttribute("workSchedule").getObject();
    }
    return workw;
  }

  private static Calendar getCalendar(boObject user) throws boRuntimeException
  {
    Calendar c=null;
    boObject workp = getWorkplace(user);
    if(workp == null)
      c = Calendar.getInstance();
    else
    {
      String tz = workp.getAttribute("timeZone").getValueString();
      TimeZone tz1 = TimeZone.getTimeZone(tz);
//      TimeZone tz2 = TimeZone.getTimeZone("EST");
      c = Calendar.getInstance(tz1);
    }
    c.setFirstDayOfWeek(Calendar.SUNDAY);
    return c;
  }
  
  private static Calendar getCalendar(boApplication boApp, EboContext ctx) throws boRuntimeException
  {
    boObject workp = boApp.getApplicationWorkplace(ctx);
    if(workp == null)
      return Calendar.getInstance();
    else
    {
      String tz = workp.getAttribute("timeZone").getValueString();
      return Calendar.getInstance(TimeZone.getTimeZone(tz));
    }
  }
  
  public static Date getToday(boObject user) throws boRuntimeException
    {
      return getToday(getCalendar(user));
    }
    
  private static Date getToday(Calendar c) throws boRuntimeException
    {
        c.clear(c.HOUR);
        c.clear(c.MINUTE);
        c.clear(c.SECOND);
        c.clear(c.MILLISECOND);

        return new Date(c.getTimeInMillis());
    }
    
    public static Date getNowHours(boObject user) throws boRuntimeException
    {
      return getNowHours(getCalendar(user));
    }

    private static Date getNowHours(Calendar c)
    {
        c.clear(c.MINUTE);
        c.clear(c.SECOND);
        c.clear(c.MILLISECOND);

        return new Date(c.getTimeInMillis());
    }
    
    public static Date getNowMinutes(boObject user) throws boRuntimeException
    {
      return getNowMinutes(getCalendar(user));
    }

    public static Date getNowMinutes(Calendar c)
    {
        c.clear(c.SECOND);
        c.clear(c.MILLISECOND);

        return new Date(c.getTimeInMillis());
    }
    
    public static Date getNow(boObject user) throws boRuntimeException
    {
      return getNow(getCalendar(user));
    }

    public static Date getNow(Calendar c)
    {
        c.clear(c.MILLISECOND);
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        df.setCalendar(c);
        try 
        {
          String format = df.format(new Date(c.getTimeInMillis()));
          return df.parse(format);
        } catch (Exception ex) 
        {
          return new Date(c.getTimeInMillis());
        } 
    }
    
    public static Date sumToDate(boObject user, java.util.Date date, long year, long month,
        long day, long hour, long minutes, long sec)throws boRuntimeException
    {
        return sumToDate(getCalendar(user), date, year, month, day, hour, minutes, sec);
    }
    
    private static Date sumToDate(Calendar c, java.util.Date date, long year, long month,
        long day, long hour, long minutes, long sec)
    {
        c.setTime(date);
        c.add(Calendar.YEAR, (int)year);
        c.add(Calendar.MONTH, (int)month);
        c.add(Calendar.DATE, (int)day);
        c.add(Calendar.HOUR, (int)hour);
        c.add(Calendar.MINUTE, (int)minutes);
        c.add(Calendar.SECOND, (int)sec);

        return new Date(c.getTimeInMillis());
    }
 /*   
    public static Date subtractToDate(boObject user, java.util.Date date, long year, long month,
        long day, long hour, long minutes, long sec)throws boRuntimeException
    {
        return subtractToDate(getCalendar(user), date, year, month, day, hour, minutes, sec);
    }
    
    private static Date subtractToDate(Calendar c, java.util.Date date, long year, long month,
        long day, long hour, long minutes, long sec)
    {
        c.setTime(date);
        c.add(Calendar.YEAR, (int)-year);
        c.add(Calendar.MONTH, (int)-month);
        c.add(Calendar.DATE, (int)-day);
        c.add(Calendar.HOUR, (int)-hour);
        c.add(Calendar.MINUTE, (int)-minutes);
        c.add(Calendar.SECOND, (int)-sec);

        return new Date(c.getTimeInMillis());
    }
    */
    
    public Date sumToUtilDate(boObject user, java.util.Date date, long year, long month,
        long day, long hour, long minutes, long sec)
    {
        long days = calculateDays(date, year, month, day);
        Date ndate = addUtilDate(user, date, days);
        long mins =  calculateMinutes(ndate, hour, minutes, sec);
        ndate = addUtilMinutes(user, date, mins);
        Calendar c = Calendar.getInstance();
        c.setTime(ndate);
  
        return new Date(c.getTimeInMillis());
    }
    

    private long calculateDays(java.util.Date date, long year, long month, long day)
    {
      long days=0;
      Calendar c = user_cal;if(year == 0 && month == 0 && day == 0)return 0;
      Date date2 = sumToDate(c, date, year, month, day, 0, 0, 0 );
      return diffInDays(truncateHour(date), truncateHour(date2))+1;
    }
    
    private long calculateMinutes(java.util.Date date, long hour, long minutes, long sec)
    {
      long days=0;
      Calendar c = user_cal;
      Date date2 = sumToDate(c, date, 0, 0, 0, hour, minutes, sec );
//      long millsMin = 60000; 
      
      return Math.abs((date2.getTime() - date.getTime()));
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
    
    private boolean isLeapYear(int year)
    {
      return year % 4 == 0;
    }
   
    private Date addUtilDate(boObject user, Date d, long dias)
    {
        
        if(dias == 0) return d;
//        if(dias < 0) return subtractUtilDate(d, -dias, ctx);

        Calendar c = user_cal;
        c.setTime(d);
        
        c.add(Calendar.DATE, (int)dias);
        
        int nSatSund = numberOfRestDays(d, c.getTime(), dias);
        int nHoli = numberOfHolidays(user, d, c.getTime());
        int toAdd = nSatSund + nHoli;
        for (long i = 0; i < toAdd; i++)
        {
            c.add(Calendar.DATE, 1);

            if (!isWorkingDay(user, c.getTime()))
            {
                i--;
            }
        }

        return c.getTime();
    }
    
    private Date addUtilMinutes(boObject user, Date d, long minutes)
    {
      Calendar c = (Calendar)user_cal.clone();
      c.setTime(d);
      c.set(Calendar.AM_PM, Calendar.AM);
      int dw = c.get(Calendar.DAY_OF_WEEK)-1;
      
      long mins = minutes;
      long min_to_add = 0;
      while(mins > 0)
      {
        Vector v = workingPeriod[dw];      
        for(int i = 0; i < v.size(); i++)
        {
          Triple t = (Triple)v.get(i);
          Calendar cstart = (Calendar)t.getFirst();
          Date ds = cstart.getTime();
          cstart.set(Calendar.YEAR, c.get(Calendar.YEAR));
          cstart.set(Calendar.MONTH, c.get(Calendar.MONTH));
          cstart.set(Calendar.DATE, c.get(Calendar.DATE));
          Calendar cend = (Calendar)t.getSecond();
          Date de = cend.getTime();
          cend.set(Calendar.YEAR, c.get(Calendar.YEAR));
          cend.set(Calendar.MONTH, c.get(Calendar.MONTH));
          cend.set(Calendar.DATE, c.get(Calendar.DATE));
          ds = cstart.getTime();
          de = cend.getTime();
          if(c.getTimeInMillis() >= cstart.getTimeInMillis())
          {
            if(c.getTimeInMillis() < cend.getTimeInMillis())  
            {
              min_to_add = cend.getTimeInMillis() - c.getTimeInMillis();
              if(mins < min_to_add)
                c.add(Calendar.MILLISECOND, (int)mins);
              else
                c.add(Calendar.MILLISECOND, (int)min_to_add);
              mins -= min_to_add;
              if(mins > 0 && v.size() > i+1 && minutes != mins)
              {
                t = (Triple)v.get(i+1);
                Calendar ct = (Calendar)t.getFirst();
                Date dct = ct.getTime();
                ct.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                c = (Calendar)ct.clone();
                dct = c.getTime();
                dct = null;
              }
            }
            else
            {
              if(v.size() > i+1)
              {
                t = (Triple)v.get(i+1);
                cstart = (Calendar)t.getFirst();
                cstart.set(Calendar.YEAR, c.get(Calendar.YEAR));
                cstart.set(Calendar.MONTH, c.get(Calendar.MONTH));
                cstart.set(Calendar.DATE, c.get(Calendar.DATE));
                Date dss = cstart.getTime();
                if(c.getTimeInMillis() < cstart.getTimeInMillis())
                {
                  cstart.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                  c = (Calendar)cstart.clone();
                  Date dct = c.getTime();
                  dct = null;
                }
              }
            }
            if(mins <= 0)
              break;
          }
          else
          {
            if(v.size() > i+1 && minutes != mins)
            {
              t = (Triple)v.get(i+1);
              Calendar ct = (Calendar)t.getFirst();
              ct.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)+1);
              c = (Calendar)ct.clone();
              Date dct = c.getTime();
              dct = null;
            }
          }
        }
        
        if(mins > 0)
        {
          Date dct=null;
          do{
            dw = (dw + 1) % 7;
            v = workingPeriod[dw];      
            
            if(v.size() > 0)
            {
              Triple t = (Triple)v.firstElement();
              Calendar ct = (Calendar)t.getFirst();
              dct = ct.getTime();
              c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)+1, 
                    ct.get(Calendar.HOUR), ct.get(Calendar.MINUTE), ct.get(Calendar.SECOND));
              dct = c.getTime();
              t = null;
            }
            else
              c.add(Calendar.DATE, 1);
          }while(v.size() == 0 || !isWorkingDay(user, dct));
        }
      }
      return c.getTime();
    }
    
    
    
    private int numberOfRestDays(Date d1, Date d2)
    {
        if(d1.before(d2))
        {
            return numberOfRestDays(d1, d2, diffInDays(d1, d2));
        }
        return numberOfRestDays(d2, d1, diffInDays(d1, d2));
    }

    private int numberOfRestDays(Date d1, Date d2, long days)
    {
        return (int)days - numberOfBusinessDays(d1, d2, days);
    }

    private static Date truncateHour(Date d)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.AM_PM, Calendar.AM);
        c.set(c.HOUR_OF_DAY, 0);
        c.set(c.MINUTE, 0);
        c.set(c.SECOND, 0);
        c.set(c.MILLISECOND, 0);
        return c.getTime();
        
    }
    
    public int numberOfHolidays(boObject user, Date from, Date to)
    {
        int count=0;
        try
        {
            

            //contar feriados especiais
            boObject workp = getWorkplace(user);
            
            if("1".equals(workp.getAttribute("christianHolidays").getValueString()))
              count += numberOfSpecialHolidays(from, to);
            Date tfrom = truncateHour(from);
            Date tto = truncateHour(to);
            
            Calendar calFrom = Calendar.getInstance();
            Calendar calTo = Calendar.getInstance();
            calFrom.setTime(from);
            calTo.setTime(to);
            
            for (int i = calFrom.get(Calendar.YEAR) + 1900; i <= calTo.get(Calendar.YEAR) + 1900 ; i++) 
            {
              for(int j = 0; j < holidays.length; j++)
              {
                Calendar cc = holidays[j];
                if(cc != null)
                {
                  cc.set(Calendar.YEAR, i);
                  if(cc.getTimeInMillis() >= tfrom.getTime() && cc.getTimeInMillis() <= tto.getTime() && isWeekDay(cc)) 
                    count ++;
                }
              }
            }
            
            
        }
        catch (boRuntimeException e)
        {
            //ignora
        }  
        
        return count;
    }
    
    private int numberOfSpecialHolidays(Date inic, Date fim)
    {
      int count = 0;
      Date easter = null;
      Date sextaS = null;
      Date corpoD = null;
      
      Calendar calInic = Calendar.getInstance();
      Calendar calFim  = Calendar.getInstance();
      calInic.setTime(inic);
      calFim.setTime(fim);
      
      for (int i = calInic.get(Calendar.YEAR) + 1900; i <= calFim.get(Calendar.YEAR) + 1900 ; i++) 
      {
        easter = getEaster(i);
        sextaS = sumToDate(user_cal, easter,0,0,-2,0,0,0);
        corpoD = sumToDate(user_cal, easter,0,0,60,0,0,0);
        Calendar c = (Calendar)user_cal.clone();    
        c.setTime(easter);
        //páscoa é sempre a um Domingo
        if(isWeekDay(c))
        {
          if( (easter.after(inic) && easter.before(fim)) || easter.equals(inic) || easter.equals(fim))
            count++;
        }
        c.setTime(sextaS);
        //sexta feira santa é sempre uma sexta
        if(isWeekDay(c))
        {
          if((sextaS.after(inic) && sextaS.before(fim)) || sextaS.equals(inic) || sextaS.equals(fim))
            count++;
        }
        c.setTime(sextaS);
        //corpo de deus é sempre uma quinta
        if(isWeekDay(c))
        {
          if((corpoD.after(inic) && corpoD.before(fim)) || corpoD.equals(inic) || corpoD.equals(fim))
            count++;
        }                  
      }
      
      return count;
      
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
      cal.set( ano - 1900, mes - 1, dia);
      
      return( cal.getTime() );      
    }

    
    public boolean isHoliday(boObject user, Date d)
    {
      boolean ret = false;
        try
        {
            ret =  isSpecialHoliday(d);
            if(ret)
              return  ret;
            
            Date tfrom = truncateHour(d);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(tfrom);
            
              for(int j = 0; j < holidays.length; j++)
              {
                Calendar cc = holidays[j];
                if(cc != null)
                {
                  cc.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1900);
                  if(cc.getTimeInMillis() == tfrom.getTime()) 
                    return true;        
                }
              }
            
        }
        catch (Exception e)
        {
            
        }
        return false;
    }
    
    private boolean isSpecialHoliday(Date d)
    {
      Calendar cal = Calendar.getInstance();
      cal.setTime(d);
    
      Date easter = getEaster( cal.get(Calendar.YEAR) + 1900 );
      Date sextaS = sumToDate(user_cal, easter,0,0,-2,0,0,0);
      Date corpoD = sumToDate(user_cal, easter,0,0,60,0,0,0);
      Date td = truncateHour(d);
      return td.equals(easter) || td.equals(sextaS) || td.equals(corpoD);
    }
    
    private int numberOfBusinessDays(Date d1, Date d2, long days)
    {
        Calendar cEnd = user_cal;
        cEnd.setTime(d2);
        
        int weeks = (int)days/7;
        int extraDays = (int)(days - weeks * 7);
        int businessDays = weeks * total_work_days;
        cEnd.add(Calendar.DATE, 1);
        for(int i = 0; i < extraDays; i++)
        {
            cEnd.add(Calendar.DATE, -1);
            if (isWeekDay(cEnd))
            {
                businessDays ++;
            }
        }
        return businessDays;
    }
    
    public boolean isWeekDay(Date date)
    {
        Calendar d = Calendar.getInstance();
        d.setTime(date);
        return isWeekDay(d);
    }

    public boolean isWeekDay(Calendar date)
    {
        boolean isWeekDay = false;
        long weekDay = date.get(Calendar.DAY_OF_WEEK);

        if (this.workingHours[(int)weekDay-1] != 0)
        {
            isWeekDay = true;
        }

        return isWeekDay;
    }
    
    private boolean isWorkingDay(boObject user, Date date)
    {
      return isWeekDay(date) && !isHoliday(user, date);
    }
    
    public long diffInUtilsDays(Date date1, Date date2, boObject user)
    {
        if(date1 != null && date2!= null)
        {
            
            Calendar c = null;
            
              c = (Calendar)user_cal.clone();
            
            c.setTime(date1);
            
            int nSatSund; 
            int nHoli;
            int total;
            if(date1.before(date2))
            {
                nSatSund =  numberOfRestDays(date1, date2);
                nHoli = numberOfHolidays(user, date1, date2);
                total = (int)diffInDays(date1, date2);
                return total - nSatSund - nHoli;
            }
            else
            {
                nSatSund =  numberOfRestDays(date2, date1);
                nHoli = numberOfHolidays(user, date2, date1);
                total = (int)diffInDays(date2, date1);
                return -(total - nSatSund - nHoli);
            }
        }
        return 0;
    }
}