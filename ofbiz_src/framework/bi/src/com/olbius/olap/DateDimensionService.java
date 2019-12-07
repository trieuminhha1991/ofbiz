package com.olbius.olap;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class DateDimensionService {

	public static Map<String, Object> createDateDimension(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();

		Timestamp timestamp = (Timestamp)context.get("dateValue");

		SimpleDateFormat monthNameFormat = new SimpleDateFormat("MMMM");
		SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEEE");
		SimpleDateFormat dayDescriptionFormat = new SimpleDateFormat("MMMM d, yyyy");
		SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		java.sql.Date currentDate = new java.sql.Date(calendar.getTimeInMillis());
		GenericValue dateValue = null;
		try {
			dateValue = EntityUtil.getFirst(delegator.findByAnd("DateDimension",
					UtilMisc.toMap("dateValue", currentDate), null, false));
		} catch (GenericEntityException gee) {
			return ServiceUtil.returnError(gee.getMessage());
		}
		boolean newValue = (dateValue == null);
		if (newValue) {
			dateValue = delegator.makeValue("DateDimension");
			dateValue.set("dimensionId", delegator.getNextSeqId("DateDimension"));
			dateValue.set("dateValue", new java.sql.Date(currentDate.getTime()));
		}
		dateValue.set("description", dayDescriptionFormat.format(currentDate));
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		dateValue.set("dayName", dayNameFormat.format(currentDate));
		dateValue.set("dayOfMonth", new Long(calendar.get(Calendar.DAY_OF_MONTH)));
		dateValue.set("dayOfYear", new Long(calendar.get(Calendar.DAY_OF_YEAR)));
		dateValue.set("monthName", monthNameFormat.format(currentDate));

		dateValue.set("monthOfYear", new Long(calendar.get(Calendar.MONTH) + 1));
		dateValue.set("yearName", new Long(calendar.get(Calendar.YEAR)));
		dateValue.set("weekOfMonth", new Long(calendar.get(Calendar.WEEK_OF_MONTH)));
		dateValue.set("weekOfYear", new Long(calendar.get(Calendar.WEEK_OF_YEAR)));
		dateValue.set("weekdayType", (dayOfWeek == 1 || dayOfWeek == 7 ? "Weekend" : "Weekday"));
		dateValue.set("yearMonthDay", yearMonthDayFormat.format(currentDate));
		dateValue.set("yearAndMonth", yearMonthFormat.format(currentDate));

		try {
			if (newValue) {
				dateValue.create();
			} else {
				dateValue.store();
			}
		} catch (GenericEntityException gee) {
			return ServiceUtil.returnError(gee.getMessage());
		}
		Map<String, Object> map = FastMap.newInstance();
		map.put("dimensionId", dateValue.get("dimensionId"));
		map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return map;
	}
}
