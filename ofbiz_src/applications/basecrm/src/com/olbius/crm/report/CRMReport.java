package com.olbius.crm.report;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.crm.util.PeriodUtil;

public class CRMReport {
	public static Map<String, Object> getTotalCommunicationBySchedule(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String period = (String) context.get("period");
			Timestamp fromDate = new Timestamp(System.currentTimeMillis());
			Timestamp thruDate = new Timestamp(System.currentTimeMillis());
			switch (period) {
			case "TODAY":
				fromDate = PeriodUtil.getStartOfDay();
				thruDate = PeriodUtil.getEndOfDay();
				break;
			default:
				break;
			}
			CommunicationSchedule.NextSchedule nextSchedule = new CommunicationSchedule().new NextSchedule(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), userLogin.getString("partyId"), fromDate, thruDate);
			result.put("value", nextSchedule.getNextScheduleTotal());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> getTotalCommunication(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Timestamp currentDate = PeriodUtil.getCurrentDate();
			CommunicationSchedule.Communication nextSchedule = new CommunicationSchedule().new Communication(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), userLogin.getString("partyId"), currentDate);
			result.put("value", nextSchedule.getCommunicationTotal());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> getTurnoverOrderByCaller(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			String period = (String) context.get("period");
			Timestamp fromDate = new Timestamp(System.currentTimeMillis());
			Timestamp thruDate = new Timestamp(System.currentTimeMillis());
			switch (period) {
			case "TODAY":
				fromDate = PeriodUtil.getStartOfDay();
				thruDate = PeriodUtil.getEndOfDay();
				break;
			default:
				break;
			}
			SalesOrderByCaller.Turnover nextSchedule = new SalesOrderByCaller().new Turnover(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), delegator, organizationId, userLogin.getString("partyId"), fromDate, thruDate);
			result.put("value", nextSchedule.getSOTurnover());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> getTotalOrderByCaller(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			String period = (String) context.get("period");
			Timestamp fromDate = new Timestamp(System.currentTimeMillis());
			Timestamp thruDate = new Timestamp(System.currentTimeMillis());
			switch (period) {
			case "TODAY":
				fromDate = PeriodUtil.getStartOfDay();
				thruDate = PeriodUtil.getEndOfDay();
				break;
			default:
				break;
			}
			SalesOrderByCaller.Total nextSchedule = new SalesOrderByCaller().new Total(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), delegator, organizationId, userLogin.getString("partyId"), fromDate, thruDate);
			result.put("value", nextSchedule.getSOTotal());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
