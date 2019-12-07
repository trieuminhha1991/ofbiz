import java.sql.Timestamp;
import java.util.Calendar;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

if(emplId){
	partyId = emplId;
	Timestamp fromDate = UtilDateTime.getYearStart(UtilDateTime.nowTimestamp(), timeZone, locale);
	Timestamp thruDate = UtilDateTime.getYearEnd(UtilDateTime.nowTimestamp(), timeZone, locale);
	Map<String, Object> results = FastMap.newInstance();	
	results = dispatcher.runSync("getNbrDayLeaveEmplInfo", UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "thruDate", thruDate, "locale", locale, "timeZone", timeZone));
	context.numberDayLeaveUnpaid = results.get("numberDayLeaveUnpaid");
	context.numberDayLeave = results.get("numberDayLeave");
	//results = dispatcher.runSync("getDayLeaveRegulation", UtilMisc.toMap("partyId", partyId));
	context.dayLeaveRegulation = results.get("dayLeaveRegulation");
	context.dayLeaveRemain = results.get("dayLeaveRegulation") - results.get("numberDayLeave");
	 
	for(int i = 0; i < 12; i++){
		Timestamp temFromDate = UtilDateTime.getMonthStart(fromDate, 0, i, timeZone, locale);
		Timestamp tempThruDate = UtilDateTime.getMonthEnd(temFromDate, timeZone, locale);
		results = dispatcher.runSync("getNbrDayLeaveEmp", UtilMisc.toMap("partyId", partyId, "fromDate", temFromDate, "thruDate", tempThruDate, "userLogin", userLogin));
		
		switch (i) {
			case 0:
				context.dayLeaveJan= results.get("nbrDayLeave");
				break;
			case 1:
				context.dayLeaveFer= results.get("nbrDayLeave");
				break;
			case 2:
				context.dayLeaveMar = results.get("nbrDayLeave");
				break;			
			case 3:
				context.dayLeaveApr = results.get("nbrDayLeave");
				break;
			case 4:
				context.dayLeaveMay = results.get("nbrDayLeave");
				break;
			case 5:
				context.dayLeaveJune = results.get("nbrDayLeave");
				break;
			case 6:
				context.dayLeaveJuly = results.get("nbrDayLeave");
				break;
			case 7:
				context.dayLeaveAug = results.get("nbrDayLeave");
				break;
			case 8:
				context.dayLeaveSep = results.get("nbrDayLeave");
				break;
			case 9:
				context.dayLeaveOct = results.get("nbrDayLeave");
				break;
			case 10:
				context.dayLeaveNov = results.get("nbrDayLeave");
				break;
			case 11:
				context.dayLeaveDec= results.get("nbrDayLeave");
				break;
			default:
				break;
			}
	}
}