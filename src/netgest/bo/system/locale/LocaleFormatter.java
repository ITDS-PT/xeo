package netgest.bo.system.locale;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

public interface LocaleFormatter {

	 public enum DateTimeLengh{
		 FULL(DateFormat.FULL)
		 ,LONG(DateFormat.LONG)
		 ,MEDIUM(DateFormat.MEDIUM)
		 ,SHORT(DateFormat.SHORT)
		 ,DEFAULT(DateFormat.DEFAULT);
		 
		 private int format;
		 
		 private DateTimeLengh(int format) {
			 this.format = format;
		 }
		 
		 public int getFormat() {
			 return this.format;
		 }
	 }
	
	 public enum CurrencyPosition{
			LEFT,
			RIGHT;
			
			public static CurrencyPosition fromString(String candidate) { 
				for ( CurrencyPosition current : values() ) {
					if (current.name().equalsIgnoreCase( candidate  ))
						return current;
				}
				return RIGHT;
			}
		}
	 
     public String formatDate(Date toFormat);
     public String formatDate(Date toFormat, DateTimeLengh length);
     
     public String formatTime(Date toFormat);
     public String formatTime(Date toFormat, DateTimeLengh length);
     public String formatHourMinute(Date date);
     
     public String formatDateTime(Date toFormat);
     public String formatDateTime(Date toFormat, DateTimeLengh dateLength
    		 , DateTimeLengh timeLength);
     
     public String formatNumber(long toFormat);
     public String formatPercent(float toFormat);
     public String formatCurrency(long toFormat);

     public Date parseDate(String date) throws ParseException;
     public Date parseDateHourMinute(String dateWithTime) throws ParseException; 
     public Date parseDateWithCompleteTime(String dateWithTime) throws ParseException;
     public Date parseDateWithoutTimezone(String date) throws ParseException;
     
     public String getDateFormat(DateTimeLengh length);
     public String getTimeFormat(DateTimeLengh length);
     public String getHourMinuteFormat(DateTimeLengh default1);
     public char getGroupSeparator();
     public char getDecimalSeparator();
     public String getCurrencySymbol();
     
	 public DecimalFormat getNumberFormatter();
	 public DecimalFormat getCurrencyFormatter();
	 public CurrencyPosition getCurrencyPosition();
}


