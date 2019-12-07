package com.olbius.crm.report;

import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.report.CSReportChartImpl;
import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.crm.report.CommunicationCampaignOlapImpl.PieOlapResultQuery;
import com.olbius.crm.report.CommunicationCampaignOlapImpl.PieResult;
import com.olbius.crm.report.CommunicationEmployeeOlapImpl.PieEmployeeOlapResultQuery;
import com.olbius.crm.report.CommunicationEmployeeOlapImpl.PieEmployeeResult;

public class ResultEnumType {
	private final OlbiusQuery query;
	private SQLProcessor processor;
	
	public ResultEnumType(SQLProcessor processor) {
		this.processor = processor;
		query = (OlbiusQuery) new OlbiusQuery(processor).select("DISTINCT(marketing_campaign_id)")
				.from("communication_campaign_fact").where(Condition.make("marketing_campaign_id is NOT NULL"));
	}
	
	public List<String> getListResultEnumType() {
		List<String> list = new ArrayList<String>();
		try {
			ResultSet resultSet = query.getResultSet();
			while(resultSet.next()) {
				list.add(resultSet.getString("marketing_campaign_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(processor != null) {
				 try {
					processor.close();
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	public static Map<String, Object> getListResultEnumType(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		ResultEnumType type = new ResultEnumType(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listResultEnumType", type.getListResultEnumType());
		return result;
	}
	
	public static Map<String, Object> getCommunicationCampaignReport(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		CommunicationCampaignOlapImpl grid = new CommunicationCampaignOlapImpl();
		
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		OlapGrid gridResult = new OlapGrid(grid, grid.new CommunicationCampaign());
		grid.setOlapResult(gridResult);
		
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        Boolean isChart = (Boolean) context.get("isChart");
        String customTime = (String) context.get("customTime");
        Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		Date beginDate = null;
		
		int monthInput = cal.get(Calendar.MONTH);
		int quarterInput = (monthInput / 3) + 1;
		int yearInput = cal.get(Calendar.YEAR);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if("dd".equals(customTime)){
			beginDate = currentDate;
		} else if ("ww".equals(customTime)){
			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			beginDate = cal.getTime();
		} else if ("mm".equals(customTime)){
			cal.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = cal.getTime();
		} else if ("qq".equals(customTime)){
			if(quarterInput == 1){
				beginDate = sdf.parse("01/01/" + yearInput);
			} else if (quarterInput == 2){
				beginDate = sdf.parse("01/04/" + yearInput);
			} else if (quarterInput == 3){
				beginDate = sdf.parse("01/07/" + yearInput);
			} else if (quarterInput == 4){
				beginDate = sdf.parse("01/10/" + yearInput);
			}
		} else if("yy".equals(customTime)){
			beginDate = sdf.parse("01/01/" + yearInput);
		}
        
		if("oo".equals(customTime)){
			grid.setFromDate(fromDate);
			grid.setThruDate(thruDate);
		} else {
			grid.setFromDate(beginDate);
			grid.setThruDate(currentDate);
		}
        grid.putParameter("isChart", isChart);
        grid.putParameter(CommunicationCampaignOlapImpl.ORG, organization);
        
		Map<String, Object> result = grid.execute(context);
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> getCommunicationEmployeeReport(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		CommunicationEmployeeOlapImpl grid = new CommunicationEmployeeOlapImpl();
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		OlapGrid gridResult = new OlapGrid(grid, grid.new CommunicationEmployee());
		grid.setOlapResult(gridResult);
		
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        Boolean isChart = (Boolean) context.get("isChart");
        String customTime = (String) context.get("customTime");
        Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		Date beginDate = null;
		
		int monthInput = cal.get(Calendar.MONTH);
		int quarterInput = (monthInput / 3) + 1;
		int yearInput = cal.get(Calendar.YEAR);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if("dd".equals(customTime)){
			beginDate = currentDate;
		} else if ("ww".equals(customTime)){
			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			beginDate = cal.getTime();
		} else if ("mm".equals(customTime)){
			cal.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = cal.getTime();
		} else if ("qq".equals(customTime)){
			if(quarterInput == 1){
				beginDate = sdf.parse("01/01/" + yearInput);
			} else if (quarterInput == 2){
				beginDate = sdf.parse("01/04/" + yearInput);
			} else if (quarterInput == 3){
				beginDate = sdf.parse("01/07/" + yearInput);
			} else if (quarterInput == 4){
				beginDate = sdf.parse("01/10/" + yearInput);
			}
		} else if("yy".equals(customTime)){
			beginDate = sdf.parse("01/01/" + yearInput);
		}
        
		if("oo".equals(customTime)){
			grid.setFromDate(fromDate);
			grid.setThruDate(thruDate);
		} else {
			grid.setFromDate(beginDate);
			grid.setThruDate(currentDate);
		}
        
        grid.putParameter("isChart", isChart);
        grid.putParameter(CommunicationEmployeeOlapImpl.ORG, organization);
        
		Map<String, Object> result = grid.execute(context);
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> getCommunicationCampaignChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		CommunicationCampaignOlapImpl grid = new CommunicationCampaignOlapImpl();
		
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String marketingCampaignId = (String) context.get("marketingCampaignId");
        Boolean isChart = (Boolean) context.get("isChart");
        String customTime = (String) context.get("customTime");
        Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		Date beginDate = null;
		
		int monthInput = cal.get(Calendar.MONTH);
		int quarterInput = (monthInput / 3) + 1;
		int yearInput = cal.get(Calendar.YEAR);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if("dd".equals(customTime)){
			beginDate = currentDate;
		} else if ("ww".equals(customTime)){
			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			beginDate = cal.getTime();
		} else if ("mm".equals(customTime)){
			cal.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = cal.getTime();
		} else if ("qq".equals(customTime)){
			if(quarterInput == 1){
				beginDate = sdf.parse("01/01/" + yearInput);
			} else if (quarterInput == 2){
				beginDate = sdf.parse("01/04/" + yearInput);
			} else if (quarterInput == 3){
				beginDate = sdf.parse("01/07/" + yearInput);
			} else if (quarterInput == 4){
				beginDate = sdf.parse("01/10/" + yearInput);
			}
		} else if("yy".equals(customTime)){
			beginDate = sdf.parse("01/01/" + yearInput);
		}
        
		if("oo".equals(customTime)){
			grid.setFromDate(fromDate);
			grid.setThruDate(thruDate);
		} else {
			grid.setFromDate(beginDate);
			grid.setThruDate(currentDate);
		}
        
        grid.putParameter("marketingCampaignId", marketingCampaignId);
        grid.putParameter("isChart", isChart);
        grid.putParameter(CommunicationCampaignOlapImpl.ORG, organization);
        
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		PieOlapResultQuery query = grid.new PieOlapResultQuery();
		Locale locale = (Locale) context.get("locale");
		query.setLocale(locale);
		PieResult pieResult = grid.new PieResult(grid, query);
		grid.setOlapResult(pieResult);
		
		Map<String, Object> result = grid.execute(context);
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> getCommunicationEmployeeChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		CommunicationEmployeeOlapImpl grid = new CommunicationEmployeeOlapImpl();
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String partyId = (String) context.get("partyId");
        Boolean isChart = (Boolean) context.get("isChart");
        
        String customTime = (String) context.get("customTime");
        Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		Date beginDate = null;
		
		int monthInput = cal.get(Calendar.MONTH);
		int quarterInput = (monthInput / 3) + 1;
		int yearInput = cal.get(Calendar.YEAR);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if("dd".equals(customTime)){
			beginDate = currentDate;
		} else if ("ww".equals(customTime)){
			cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
			beginDate = cal.getTime();
		} else if ("mm".equals(customTime)){
			cal.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = cal.getTime();
		} else if ("qq".equals(customTime)){
			if(quarterInput == 1){
				beginDate = sdf.parse("01/01/" + yearInput);
			} else if (quarterInput == 2){
				beginDate = sdf.parse("01/04/" + yearInput);
			} else if (quarterInput == 3){
				beginDate = sdf.parse("01/07/" + yearInput);
			} else if (quarterInput == 4){
				beginDate = sdf.parse("01/10/" + yearInput);
			}
		} else if("yy".equals(customTime)){
			beginDate = sdf.parse("01/01/" + yearInput);
		}
        
		if("oo".equals(customTime)){
			grid.setFromDate(fromDate);
			grid.setThruDate(thruDate);
		} else {
			grid.setFromDate(beginDate);
			grid.setThruDate(currentDate);
		}
        grid.putParameter("partyId", partyId);
        grid.putParameter("isChart", isChart);
        grid.putParameter(CommunicationEmployeeOlapImpl.ORG, organization);
        
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		PieEmployeeOlapResultQuery query = grid.new PieEmployeeOlapResultQuery();
		Locale locale = (Locale) context.get("locale");
		query.setLocale(locale);
		PieEmployeeResult pieResult = grid.new PieEmployeeResult(grid, query);
		grid.setOlapResult(pieResult);
		grid.putParameter(CommunicationEmployeeOlapImpl.ORG, organization);
		Map<String, Object> result = grid.execute(context);
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
