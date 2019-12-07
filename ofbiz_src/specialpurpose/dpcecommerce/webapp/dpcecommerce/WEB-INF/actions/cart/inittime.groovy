import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import java.sql.Timestamp;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;

Calendar cal = Calendar.getInstance();
Long today = cal.getTimeInMillis();
Long cur = today;
List<Map<String, Object>> days = FastList.newInstance();
uiLabelMap = UtilProperties.getResourceBundleMap("DpcEcommerceUiLabels", locale);
for(int i = 1 ; i <= 7; i++){
	Timestamp tmp = new Timestamp(cur);
	int day = tmp.getDay();
	String daytmp = "";
	Map<String, Object> dm = FastMap.newInstance();
	switch(day){
		case 0:
			daytmp = uiLabelMap.Sunday;
			break;
		case 1:
			daytmp = uiLabelMap.Monday;
			break;
		case 2:
			daytmp = uiLabelMap.Tuesday;
			break;
		case 3:
			daytmp = uiLabelMap.Wednesday;
			break;
		case 4:
			daytmp = uiLabelMap.Thursday;
			break;
		case 5:
			daytmp = uiLabelMap.Friday;
			break;
		case 6:
			daytmp = uiLabelMap.Saturday;
			break;
	}
	if(cur == today){
		daytmp = uiLabelMap.Today;
	}
	int month = tmp.getMonth() + 1;
	int date = tmp.getDate();
	String m = month < 10 ? "0" + month.toString() : month.toString();
	String d = date < 10 ? "0" + date.toString() : date.toString();
	dm.put("day", daytmp + " " + d + "/" + m);
	dm.put("key", cur);
	days.add(dm);
	cur += i * 86400000;
}
context.days = days;
