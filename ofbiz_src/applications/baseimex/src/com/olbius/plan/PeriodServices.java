package com.olbius.plan;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;

import javolution.util.FastList;

public class PeriodServices {
	public static final String module = PeriodServices.class.getName();
	public static final String resource = "BasePOUiLabels";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListCommercialPeriod(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "COMMERCIAL_YEAR"));
		listAllConditions
				.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));

		List<GenericValue> dataList;
		try {
			dataList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(listAllConditions), null,
					listSortFields, opts, false);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		successResult.put("listIterator", dataList);
		return successResult;
	}

	@SuppressWarnings("deprecation")
	public static Map<String, Object> createCommercialPeriodExe(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String yearPeriodRaw = (String) context.get("yearPeriod");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		int yearPeriod = Integer.parseInt(yearPeriodRaw);
		int state = 1;
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);

		List<GenericValue> listCreatedYear = delegator.findList("CustomTimePeriod",
				EntityCondition.makeCondition(
						UtilMisc.toMap("periodTypeId", "COMMERCIAL_YEAR", "organizationPartyId", organizationPartyId)),
				null, null, null, false);
		for (GenericValue y : listCreatedYear) {
			Date fromYearValidate = y.getDate("fromDate");
			Date thruYearValidate = y.getDate("thruDate");
			int fromYearValidateValue = fromYearValidate.getYear();
			int thruYearValidateValue = thruYearValidate.getYear();
			if ((yearPeriod - 1900) == fromYearValidateValue && (yearPeriod - 1900) == thruYearValidateValue) {
				state = 0;
				break;
			}
		}
		String customTimePeriodIdYear = "";
		if (state == 0) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CreateCommercialPeriodCoincidence",
					(Locale) context.get("locale")));
		} else {
			Calendar calendar = Calendar.getInstance();
			Date date = new Date();
			calendar.setTime(date);
			calendar.set(Calendar.YEAR, yearPeriod);
			calendar.set(Calendar.MONTH, 11);
			calendar.set(Calendar.DAY_OF_MONTH, 31);
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			java.sql.Date endYear = new java.sql.Date(calendar.getTimeInMillis());
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			java.sql.Date newYear = new java.sql.Date(calendar.getTimeInMillis());
			customTimePeriodIdYear = delegator.getNextSeqId("CustomTimePeriod");
			// create custom time year
			// fix company...
			createCustomtimePeriod2(delegator, customTimePeriodIdYear, null, "COMMERCIAL_YEAR", "" + yearPeriod,
					newYear, endYear, organizationPartyId);
			for (int k = 0; k < 12; k++) {
				calendar.set(Calendar.MONTH, k);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				java.sql.Date firstDayOfMonth = new java.sql.Date(calendar.getTimeInMillis());
				int dateEndMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				calendar.set(Calendar.DAY_OF_MONTH, dateEndMonth);
				java.sql.Date endDayOfMonth = new java.sql.Date(calendar.getTimeInMillis());
				// create new customTimeperiod of month
				String customTimePeriodIdMonth = delegator.getNextSeqId("CustomTimePeriod");
				createCustomtimePeriod2(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "COMMERCIAL_MONTH",
						"" + (k + 1), firstDayOfMonth, endDayOfMonth, organizationPartyId);

				// fix ngay 26
				calendar.set(Calendar.DAY_OF_MONTH, dateEndMonth);
				java.sql.Date curMonth = new java.sql.Date(calendar.getTimeInMillis());

				calendar.set(Calendar.DAY_OF_MONTH, 1);
				java.sql.Date prevMonth = new java.sql.Date(calendar.getTimeInMillis());
				while (!curMonth.equals(prevMonth)) {
					int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
					prevMonth = new java.sql.Date(calendar.getTimeInMillis());
					// fix thu 2
					if (dayWeek == 2) {
						int dateW = calendar.get(Calendar.DATE);
						int monthW = k + 1;
						int yearW = calendar.get(Calendar.YEAR);
						String customTimePeriodIdWeek = delegator.getNextSeqId("CustomTimePeriod");
						calendar.add(Calendar.DATE, 7);
						java.sql.Date endW = new java.sql.Date(calendar.getTimeInMillis());
						// create custom time week
						createCustomtimePeriod2(delegator, customTimePeriodIdWeek, customTimePeriodIdMonth,
								"COMMERCIAL_WEEK", "" + dateW + "-" + monthW + "-" + yearW, prevMonth, endW,
								organizationPartyId);
						calendar.add(Calendar.DATE, -7);
					}
					calendar.add(Calendar.DATE, 1);
				}
			}
		}
		successResult.put("customTimePeriodId", customTimePeriodIdYear);
		return successResult;
	}

	public static void createCustomtimePeriod2(Delegator delegator, String customTimePeriodId, String parentPeriodId,
			String periodTypeId, String periodName, Date fromDate, Date thruDate, String organizationPartyId)
			throws GenericEntityException {
		GenericValue customTime = delegator.makeValue("CustomTimePeriod");
		customTime.put("customTimePeriodId", customTimePeriodId);
		customTime.put("parentPeriodId", parentPeriodId);
		customTime.put("periodTypeId", periodTypeId);
		customTime.put("periodName", periodName);
		customTime.put("fromDate", fromDate);
		customTime.put("thruDate", thruDate);
		customTime.put("organizationPartyId", organizationPartyId);
		delegator.create(customTime);
	}
	
	public static String getCurrentPartyPeriodType(Delegator delegator, String partyId) throws GenericEntityException {
		String periodTypeId = null;
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> listPeriods = FastList.newInstance();
		try {
			listPeriods = delegator.findList("PartyPeriodType", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList PartyPeriodType: " + e.toString();
			Debug.logError(e, errMsg, module);
			return null;
		}
		if (!listPeriods.isEmpty()) periodTypeId = listPeriods.get(0).getString("periodTypeId");
		return periodTypeId;
	}
	
	
}
