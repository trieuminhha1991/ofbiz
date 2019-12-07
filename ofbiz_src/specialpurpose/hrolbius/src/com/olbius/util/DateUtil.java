package com.olbius.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

public class DateUtil {
	public static final long ONE_DAY_MILLIS = 24*60*60*1000;
	public static String STARTMODE = "START";
	public static String ENDMODE = "END";
	
	public static Timestamp getBiggerDateTime(Timestamp datetime1, Timestamp datetime2){
		if(datetime1 == null){
			return datetime2;
		}
		if (datetime2 == null) {
			return datetime1;
		}
		return datetime1.compareTo(datetime2) <= 0 ? datetime2 : datetime1;
	}

	public static boolean checkMonthPeriod(Timestamp fromDate, Timestamp thruDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		int fromHour = cal.get(Calendar.HOUR);
		int fromDay = cal.get(Calendar.DAY_OF_MONTH);
		
		cal.setTime(thruDate);
		int thruHour = cal.get(Calendar.HOUR);
		int thruDay = cal.get(Calendar.DAY_OF_MONTH);
		
		if(fromHour == thruHour && fromDay == thruDay){
			return true;
		}else {
			return false;
		}
	}
	
	public static boolean checkDailyPeriod(Timestamp fromDate, Timestamp thruDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		int fromHour = cal.get(Calendar.HOUR);
		
		cal.setTime(thruDate);
		int thruHour = cal.get(Calendar.HOUR);
		
		if(fromHour == thruHour){
			return true;
		}else {
			return false;
		}
	}
	
	public static boolean checkWeekPeriod(Timestamp fromDate, Timestamp thruDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		int fromHour = cal.get(Calendar.HOUR);
		int fromDay = cal.get(Calendar.DAY_OF_WEEK);
		
		cal.setTime(thruDate);
		int thruHour = cal.get(Calendar.HOUR);
		int thruDay = cal.get(Calendar.DAY_OF_WEEK);
		
		if(fromHour == thruHour && fromDay == thruDay){
			return true;
		}else {
			return false;
		}
	}
	
	public static boolean checkQuarterPeriod(Timestamp fromDate, Timestamp thruDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		int fromHour = cal.get(Calendar.HOUR);
		int fromDay = cal.get(Calendar.DAY_OF_MONTH);
		int fromMonth = cal.get(Calendar.MONTH);
		
		cal.setTime(thruDate);
		int thruHour = cal.get(Calendar.HOUR);
		int thruDay = cal.get(Calendar.DAY_OF_MONTH);
		int thruMonth = cal.get(Calendar.MONTH);
		
		if(fromHour == thruHour && fromDay == thruDay && (thruMonth % fromMonth) == 0 && (thruMonth / fromMonth ) % 3 == 0){
			return true;
		}else {
			return false;
		}
	}
	public static boolean checkYearPeriod(Timestamp fromDate, Timestamp thruDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		int fromHour = cal.get(Calendar.HOUR);
		int fromDay = cal.get(Calendar.DAY_OF_MONTH);
		int fromMonth = cal.get(Calendar.MONTH);
		
		cal.setTime(thruDate);
		int thruHour = cal.get(Calendar.HOUR);
		int thruDay = cal.get(Calendar.DAY_OF_MONTH);
		int thruMonth = cal.get(Calendar.MONTH);
		if(fromHour == thruHour && fromDay == thruDay && fromMonth == thruMonth){
			return true;
		}else {
			return false;
		}
	}
	
	public static boolean checkDateTime(Timestamp fromDate, Timestamp thruDate){
		Calendar calFrom = Calendar.getInstance();
		Calendar calThru = Calendar.getInstance();
		calFrom.setTime(fromDate);
		calThru.setTime(thruDate);
		
		if (calFrom.after(calThru)||calFrom.equals(calThru))
			return false;
		else return true; 
	}
	
	public static int getWeekPeriodNumber(Timestamp fromDate, Timestamp thruDate){
		DateTime dateTime1 = new DateTime(fromDate.getTime());
		DateTime dateTime2 = new DateTime(thruDate.getTime());
		return Weeks.weeksBetween(dateTime1, dateTime2).getWeeks();
	}
	
	public static int getMonthPeriodNumber(Timestamp fromDate, Timestamp thruDate){
		DateTime dateTime1 = new DateTime(fromDate.getTime());
		DateTime dateTime2 = new DateTime(thruDate.getTime());
		return Months.monthsBetween(dateTime1, dateTime2).getMonths();
	}
	
	public static int getQuarterPeriodNumber(Timestamp fromDate, Timestamp thruDate){
		return getMonthPeriodNumber(fromDate, thruDate)/3;
	}
	
	public static int getYearPeriodNumber(Timestamp fromDate, Timestamp thruDate){
		DateTime dateTime1 = new DateTime(fromDate.getTime());
		DateTime dateTime2 = new DateTime(thruDate.getTime());
		return Years.yearsBetween(dateTime1, dateTime2).getYears();
	}
	
	public static int getWorkingDaysBetweenTwoDates(Timestamp startDate, Timestamp endDate) {
	    Calendar startCal = Calendar.getInstance();
	    startCal.setTime(startDate);        

	    Calendar endCal = Calendar.getInstance();
	    endCal.setTime(endDate);

	    int workDays = 0;

	    //Return 0 if start and end are the same
	    if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
	        return 0;
	    }

	    if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
	        startCal.setTime(endDate);
	        endCal.setTime(startDate);
	    }
	    
	    do {
	       //excluding start date
	        startCal.add(Calendar.DAY_OF_MONTH, 1);
	        if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
	            ++workDays;
	        }
	    } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

	    return workDays;
	}
	
	//FIXME Need executing hour
	//FIXME Need configure hour, start working time, end working time, break time
	public static int getWorkingHourBetweenTwoDates(Timestamp startDate, Timestamp endDate) {

	    return getWorkingDaysBetweenTwoDates(startDate, endDate)*8;
	}
	
	public static boolean beforeOrEquals(Date date1, Date date2){
		return date1.before(date2) || date1.equals(date2);
	}
	
	public static boolean afterOrEquals(Date date1, Date date2){
		return date1.after(date2) || date1.equals(date2);
	}
	
	//Convert date
	public static String convertDate(Timestamp datetime){
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		return format.format(datetime);
		
	}
	
	//Convert date
		public static String convertDate(Date date){
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			return format.format(date);
			
		}
	
	//Convert date
	public static String parseAndConvertDate(String datetime){
			SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			try {
				return convertDate(new Timestamp(format.parse(datetime).getTime()));
			} catch (ParseException e) {
				Debug.log(e.getMessage());
				return null;
			}
			
	}
	public static Timestamp getHourStart(Timestamp timestamp, Locale locale, TimeZone timeZone, int hourLater){
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.HOUR, hourLater);
		Timestamp retTimestamp = new Timestamp(cal.getTimeInMillis());
		return retTimestamp;
	}
	public static Timestamp getHourEnd(Timestamp timestamp, Locale locale, TimeZone timeZone){
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		//cal.add(Calendar.HOUR, hourLater);
		Timestamp retTimestamp = new Timestamp(cal.getTimeInMillis());
		return retTimestamp;
	}
	
	public static Timestamp getQuarterStart(Timestamp timestamp, Locale locale, TimeZone timeZone, int quarterLater){
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		int quarter = cal.get(Calendar.MONTH / 3) + 1 + quarterLater % 4;
		switch (quarter) {
		case 1:
			cal.set(cal.get(Calendar.YEAR), Calendar.JANUARY, 1, 0, 0, 0);
			break;
		case 2:
			cal.set(cal.get(Calendar.YEAR), Calendar.APRIL, 1, 0, 0, 0);
			break;
		case 3:
			cal.set(cal.get(Calendar.YEAR), Calendar.JULY, 1, 0, 0, 0);
			break;
		case 4:	
			cal.set(cal.get(Calendar.YEAR), Calendar.OCTOBER, 1, 0, 0, 0);
			break;
		default:
			break;
		}		
		//cal.add(Calendar.MONTH, quarterLater + 3);
		Timestamp retTimestamp = new Timestamp(cal.getTimeInMillis());
		return retTimestamp;
	}
	public static Timestamp getQuarterEnd(Timestamp timestamp, Locale locale, TimeZone timeZone){
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		int quarter = cal.get(Calendar.MONTH / 3) + 1;
		switch (quarter) {
		case 1:
			cal.set(cal.get(Calendar.YEAR), Calendar.MARCH, 31, 23, 59, 59);
			break;
		case 2:
			cal.set(cal.get(Calendar.YEAR), Calendar.JUNE, 30, 23, 59, 59);
			break;
		case 3:
			cal.set(cal.get(Calendar.YEAR), Calendar.SEPTEMBER, 31, 23, 59, 59);
			break;
		case 4:	
			cal.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 31, 23, 59, 59);
			break;
		default:
			break;
		}		
		Timestamp retTimestamp = new Timestamp(cal.getTimeInMillis());
		return retTimestamp;
	}
	
	public static EntityCondition getDateValidConds(Timestamp fromDate, Timestamp thruDate){
		return getDateValidConds(fromDate, thruDate, false);
	}
	
	public static EntityCondition getDateValidConds(Timestamp fromDate, Timestamp thruDate, boolean condsThruDateNull){
		List<EntityCondition> dateConds = FastList.newInstance();
		if(thruDate == null){
			EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityJoinOperator.NOT_EQUAL, null),
																	  EntityJoinOperator.AND,
																	  EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			if(condsThruDateNull){
				condition = EntityCondition.makeCondition(condition, EntityJoinOperator.OR, EntityCondition.makeCondition("thruDate", null));
			}
			dateConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
														EntityJoinOperator.OR,
														condition));
			
    	}else{
    		EntityCondition condition1 = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.NOT_EQUAL, null),
					EntityOperator.AND,
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
			condition1 = EntityCondition.makeCondition(condition1, EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			
			EntityCondition condition2 = EntityCondition.makeCondition("thruDate", null);
			condition2 = EntityCondition.makeCondition(condition2, EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			
			dateConds.add(EntityCondition.makeCondition(condition1, EntityOperator.OR, condition2));
    	}
		return EntityCondition.makeCondition(dateConds);
	}
	public static String getCurrentYear(){
		Calendar cal = Calendar.getInstance();
		return Integer.toString(cal.get(Calendar.YEAR));
	}
	public static String getCurrentDate(){
		Calendar cal = Calendar.getInstance();
		return Integer.toString(cal.get(Calendar.DATE));
	}
	public static String getCurrentMonth(){
		Calendar cal = Calendar.getInstance();
		return Integer.toString(cal.get(Calendar.MONTH) + 1);
	}
	
	public static String getDateMonthYearDesc(Timestamp timestamp){
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		return getDateMonthYearDesc(cal);
	}
	
	public static String getDateMonthYearDesc(Calendar cal){
		int month = cal.get(Calendar.MONTH) + 1;
		String monthStr = String.valueOf(month);
		if(month < 10){
			monthStr = "0" + monthStr;
		}
		int date = cal.get(Calendar.DATE);
		String dateStr = String.valueOf(date);
		if(date < 10){
			dateStr = "0" + dateStr;
		}
		return dateStr + "/" + monthStr + "/" + cal.get(Calendar.YEAR);
	}
	
	public static Time convertTime(Calendar cal, Time time){
		if(time == null){
			return null;
		}
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(time);
		cal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, tempCal.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, 0);
		Time retVal = new Time(cal.getTimeInMillis());
		return retVal;
	}

	public static List<Map<String, Timestamp>> splitFromThruDateMap(List<Map<String, Timestamp>> list, Timestamp fromDate, Timestamp thruDate) {
		List<Map<String, Timestamp>> retList = FastList.newInstance();
		retList.addAll(list);
		for(Map<String, Timestamp> entry: list){
			Timestamp tempFromDate = entry.get("fromDate");
			Timestamp tempThruDate = entry.get("thruDate");
			if(thruDate == null){
				retList.remove(entry);
				if(fromDate.after(tempFromDate)){
					Map<String, Timestamp> tempMap = FastMap.newInstance();
					tempMap.put("fromDate", tempFromDate);
					tempMap.put("thruDate", UtilDateTime.getDayEnd(fromDate, -1L));
					retList.add(tempMap);
				}
			}else{
				if(tempThruDate == null){
					retList.remove(entry);
					if(fromDate.after(tempFromDate)){
						Map<String, Timestamp> tempMap = FastMap.newInstance();
						tempMap.put("fromDate", tempFromDate);
						tempMap.put("thruDate", UtilDateTime.getDayEnd(fromDate, -1L));
						retList.add(tempMap);
					}
					Map<String, Timestamp> tempMap = FastMap.newInstance();
					tempMap.put("fromDate", UtilDateTime.getDayStart(thruDate, 1));
					tempMap.put("thruDate", tempThruDate);
					retList.add(tempMap);
				}else{
					if(!(DateUtil.beforeOrEquals(thruDate, tempFromDate) || DateUtil.afterOrEquals(fromDate,tempThruDate))){
						retList.remove(entry);
						if((DateUtil.beforeOrEquals(fromDate, tempFromDate) && DateUtil.beforeOrEquals(thruDate, tempThruDate))){
							Map<String, Timestamp> tempMap = FastMap.newInstance();
							tempMap.put("fromDate", UtilDateTime.getDayStart(thruDate, 1));
							tempMap.put("thruDate", tempThruDate);
							retList.add(tempMap);
						}else if(DateUtil.afterOrEquals(fromDate, tempFromDate) && DateUtil.afterOrEquals(thruDate, tempThruDate)){
							Map<String, Timestamp> tempMap = FastMap.newInstance();
							tempMap.put("fromDate", tempFromDate);
							tempMap.put("thruDate", UtilDateTime.getDayEnd(tempFromDate, -1L));
							retList.add(tempMap);
						}else if(DateUtil.afterOrEquals(fromDate, tempFromDate) && DateUtil.beforeOrEquals(thruDate, tempThruDate)){
							Map<String, Timestamp> tempMap = FastMap.newInstance();
							tempMap.put("fromDate", tempFromDate);
							tempMap.put("thruDate", UtilDateTime.getDayEnd(tempFromDate, -1L));
							retList.add(tempMap);
							Map<String, Timestamp> tempMap1 = FastMap.newInstance();
							tempMap1.put("fromDate", UtilDateTime.getDayStart(thruDate, 1));
							tempMap1.put("thruDate", tempThruDate);
							retList.add(tempMap1);
						}
					}
				}
			}
		}
		return retList;
	}
	
	public static Timestamp getTimestampAfter(Timestamp timestamp1, Timestamp timestamp2) {
		if(timestamp1 == null){
			return timestamp2;
		}
		if(timestamp2 == null){
			return timestamp1;
		}
		if(timestamp1.after(timestamp2)){
			return timestamp1;
		}
		if(timestamp2.after(timestamp1)){
			return timestamp2;
		}
		return timestamp1;
	}
	
	public static List<java.sql.Date> createDateList(Timestamp fromDate, Timestamp thruDate){
		List<java.sql.Date> retList = FastList.newInstance();
		while (fromDate.before(thruDate)) {
			retList.add(new java.sql.Date(fromDate.getTime()));
			fromDate = UtilDateTime.getNextDayStart(fromDate);
		}
		return retList;
	}

	public static boolean checkTimestampInPeriod(Timestamp timestamp,
			Timestamp fromDatePeriod, Timestamp thruDatePeriod) {
		if(thruDatePeriod == null){
			if(fromDatePeriod.before(timestamp) || fromDatePeriod.equals(timestamp)){
				return true;
			}else{
				return false;
			}
		}else{
			if((fromDatePeriod.before(timestamp) || fromDatePeriod.equals(timestamp)) 
					&& (thruDatePeriod.after(timestamp) || thruDatePeriod.equals(timestamp))){
				return true;
			}else{
				return false;
			}
		}
	}
	
	public static String getDayName(java.sql.Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return getDayName(cal.get(Calendar.DAY_OF_WEEK));
	}

	public static String getDayName(int dayWeekId) {
		switch (dayWeekId) {
		case 1:
			return "SUNDAY";
		case 2:
			return "MONDAY";
		case 3:
			return "TUESDAY";
		case 4:
			return "WEDNESDAY";
		case 5:
			return "THURSDAY";
		case 6:
			return "FRIDAY";
		case 7:
			return "SATURDAY";
		default:
			return null;			
		}
	}

	public static Time getMiddleTimeBetweenTwoTime(Time startTime, Time endTime) {
		Long diffMillis = endTime.getTime() - startTime.getTime();
		if(diffMillis < 0){
			diffMillis += ONE_DAY_MILLIS;
		}
		return new Time(startTime.getTime() + diffMillis/2);
	}

	public static long calculateHoursBetweenTimes(Time startTime, Time endTime) {
		Long diffMillis = endTime.getTime() - startTime.getTime();
		if(diffMillis < 0){
			diffMillis += ONE_DAY_MILLIS;
		}
		float nbrHours = (float)diffMillis/(60 * 60 *1000);
		return Math.round(nbrHours);
	}
}
