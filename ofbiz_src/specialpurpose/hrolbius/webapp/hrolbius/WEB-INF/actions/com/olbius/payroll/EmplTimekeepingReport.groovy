import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

import com.olbius.util.PartyUtil;

year = parameters.year;
month = parameters.month;
Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
Calendar cal = Calendar.getInstance();
cal.setTime(nowTimestamp);
if(UtilValidate.isEmpty(year)){
	year = cal.get(Calendar.YEAR);
}else{
	year = Integer.parseInt(year);
}
if(UtilValidate.isEmpty(month)){
	month = cal.get(Calendar.MONTH);
}else{
	month = Integer.parseInt(month) - 1;
}
cal.set(Calendar.YEAR, year);
cal.set(Calendar.MONTH, month);
Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
Timestamp startMonth = UtilDateTime.getMonthStart(timestamp);
Timestamp endMonth = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
Timestamp tmpTimestime = startMonth;
List<Date> dateOfMonth = FastList.newInstance();

while(startMonth.before(endMonth)){
	dateOfMonth.add(new Date(startMonth.getTime()));						
	startMonth = UtilDateTime.getNextDayStart(startMonth);	
}
context.dateOfMonth = dateOfMonth;
context.month = String.valueOf(month);
context.year = String.valueOf(year) ;
