import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;

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
context.month = String.valueOf(month);
context.year = String.valueOf(year) ;
