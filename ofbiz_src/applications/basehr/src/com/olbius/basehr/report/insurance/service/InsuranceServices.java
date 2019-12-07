package com.olbius.basehr.report.insurance.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.report.ReportUtil;
import com.olbius.basehr.report.insurance.query.ImprovingHealthAllowanceImpl;
import com.olbius.basehr.report.insurance.query.ImprovingHealthAnalysisImpl;
import com.olbius.basehr.report.insurance.query.InsBenefitHealthImprovementImpl;
import com.olbius.basehr.report.insurance.query.InsBenefitSicknessPregnancyImpl;
import com.olbius.basehr.report.insurance.query.InsProfileRecordsImpl;
import com.olbius.basehr.report.insurance.query.SicknessPregnancyAllowanceImpl;
import com.olbius.basehr.report.insurance.query.SicknessPregnancyAnalysisImpl;
import com.olbius.basehr.report.insurance.query.timelyInsParticipateImpl;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.grid.OlapGrid;

import javolution.util.FastList;
import javolution.util.FastMap;

public class InsuranceServices {
	public static Map<String, Object> insBenefitSicknessPregnancyEmpl(DispatchContext ctx, Map<String, Object> context){
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		
		InsBenefitSicknessPregnancyImpl grid = new InsBenefitSicknessPregnancyImpl(delegator,locale);
		grid.setOlapResultType(OlapGrid.class);
		
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		
		Map<String, Object> result = grid.execute(context);
		return result;
	}
	
	public static Map<String, Object> insBenefitHealthImprovementSicknessPregnancy(DispatchContext ctx, Map<String, Object> context){
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		
		InsBenefitHealthImprovementImpl grid = new InsBenefitHealthImprovementImpl(delegator, locale);
		grid.setOlapResultType(OlapGrid.class);
		
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		
		Map<String, Object> result = grid.execute(context);
		return result;
		
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	public static Map<String, Object> sicknessPregnancyAllowanceFluct(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		String yearFrom = (String) context.get("yearFrom");
		String yearTo = (String) context.get("yearTo");
		List<String> month = (List<String>) context.get("month[]");
		String type = (String) context.get("type");
		String option = (String) context.get("option");
		Map<String, Object> result = new HashMap<String, Object>();
		
		int yearFrom_int = Integer.parseInt(yearFrom);
		int yearTo_int = Integer.parseInt(yearTo);
		List<Integer> listMonthInYear = FastList.newInstance();
		List<Integer> listYear = FastList.newInstance();
		if(UtilValidate.isNotEmpty(month)){
			for (String s : month) {
				int numb = ReportUtil.getMonth(s);
				listMonthInYear.add(numb);
			}
		}
		for (int i = 0; i <= (yearTo_int - yearFrom_int); i++) {
			int year = yearFrom_int + i;
			listYear.add(year);
		}
		
		if(UtilValidate.isEmpty(option)){
			option = "syn";
		}
		
		if(type.equals("Fluctuation")){
			SicknessPregnancyAllowanceImpl chart = new SicknessPregnancyAllowanceImpl(delegator);
			chart.setOlapResultType(OlapColumnChart.class);
			
			chart.putParameter(chart.YEAR, listYear);
			chart.putParameter(chart.MONTH, listMonthInYear);
			
			result = chart.execute(context);
		}else if(type.equals("Analysis")){
			SicknessPregnancyAnalysisImpl chart = new SicknessPregnancyAnalysisImpl(delegator);
			chart.setOlapResultType(OlapColumnChart.class);
			
			chart.putParameter(chart.YEAR, listYear);
			chart.putParameter(chart.MONTH, listMonthInYear);
			chart.putParameter(chart.OPTION, option);
			
			result = chart.execute(context);
		}
		return result;
		
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	public static Map<String, Object> insuranceRecords(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		List<String> partyId = (List<String>) context.get("partyId[]");
		InsProfileRecordsImpl grid = new InsProfileRecordsImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		grid.putParameter(grid.PARTYID, partyId);
		Map<String, Object> result = grid.execute(context);
		return result;
	}
	
	public static Map<String, Object> getEmplByOrg(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		//Get parameters
		String partyId = (String)context.get("partyId");
		
		//Set result
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
			List<GenericValue> listEmpl = buildOrg.getEmplInOrgAtPeriod(delegator, UtilDateTime.nowTimestamp(), null);
			if(UtilValidate.isNotEmpty(listEmpl)){
				for (GenericValue g : listEmpl) {
					Map<String, Object> map = FastMap.newInstance();
					String p = g.getString("partyId");
					String n = g.getString("partyName");
					map.put("value", p);
					map.put("text", n);
					listReturn.add(map);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		return successResult;
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	public static Map<String, Object> timelyInsParticipate(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		String status = (String) context.get("status");
		String org = (String) context.get("org");
		Timestamp date = (Timestamp) context.get("date");
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String root = PartyUtil.getRootOrganization(delegator, userLoginId);
		
		List<String> listEmplId = FastList.newInstance();
		try {
			if(UtilValidate.isNotEmpty(org)){
				Organization buildOrg = PartyUtil.buildOrg(delegator, org, true, false);
				List<GenericValue> listEmpl = buildOrg.getEmplInOrgAtPeriod(delegator, UtilDateTime.nowTimestamp(), null);
				listEmplId = EntityUtil.getFieldListFromEntityList(listEmpl, "partyId", true);
			}else{
				Organization buildOrg = PartyUtil.buildOrg(delegator, root, true, false);
				List<GenericValue> listEmpl = buildOrg.getEmplInOrgAtPeriod(delegator, UtilDateTime.nowTimestamp(), null);
				listEmplId = EntityUtil.getFieldListFromEntityList(listEmpl, "partyId", true);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		timelyInsParticipateImpl chart = new timelyInsParticipateImpl(delegator); 
		chart.setOlapResultType(OlapColumnChart.class);
		
		if(UtilValidate.isNotEmpty(status)){
			chart.putParameter(chart.STATUS, status);
		}
		
		if(UtilValidate.isNotEmpty(listEmplId)){
			chart.putParameter(chart.ORG, listEmplId);
		}
		if(UtilValidate.isNotEmpty(date)){
			chart.setFromDate(date);
		}
		
		Map<String, Object> result = chart.execute(context);
		
		Map<String,Object> yAxis = (Map<String,Object>) result.get("yAxis");
		List<Date> dateVal = (List<Date>) yAxis.get("default");
		List<Long> dateValue = FastList.newInstance();
		
		if(UtilValidate.isNotEmpty(dateVal)){
			for (Date d : dateVal) {
				long l = d.getTime();
				dateValue.add(l);
			}
		}
		Map<String, Object> resultReturn = FastMap.newInstance();
		Map<String, Object> yAxis_new = FastMap.newInstance();
		yAxis_new.put("time", dateValue);
		resultReturn.put("yAxis", yAxis_new);
		resultReturn.put("xAxis", result.get("xAxis"));
		
		return resultReturn;
	}
	@SuppressWarnings({ "static-access", "unchecked" })
	public static Map<String, Object> improvingHealthAllowanceFluct(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		String yearFrom = (String) context.get("yearFrom");
		String yearTo = (String) context.get("yearTo");
		List<String> month = (List<String>) context.get("month[]");
		String type = (String) context.get("type");
		String option = (String) context.get("option");
		Map<String, Object> result = new HashMap<String, Object>();
		
		int yearFrom_int = Integer.parseInt(yearFrom);
		int yearTo_int = Integer.parseInt(yearTo);
		List<Integer> listMonthInYear = FastList.newInstance();
		List<Integer> listYear = FastList.newInstance();
		if(UtilValidate.isNotEmpty(month)){
			for (String s : month) {
				int numb = ReportUtil.getMonth(s);
				listMonthInYear.add(numb);
			}
		}
		for (int i = 0; i <= (yearTo_int - yearFrom_int); i++) {
			int year = yearFrom_int + i;
			listYear.add(year);
		}
		
		if(UtilValidate.isEmpty(option)){
			option = "syn";
		}
		
		if(type.equals("Fluctuation")){
			ImprovingHealthAllowanceImpl chart = new ImprovingHealthAllowanceImpl(delegator);
			chart.setOlapResultType(OlapColumnChart.class);
			
			chart.putParameter(chart.YEAR, listYear);
			chart.putParameter(chart.MONTH, listMonthInYear);
			
			result = chart.execute(context);
		}else if(type.equals("Analysis")){
			ImprovingHealthAnalysisImpl chart = new ImprovingHealthAnalysisImpl(delegator);
			chart.setOlapResultType(OlapColumnChart.class);
			
			chart.putParameter(chart.YEAR, listYear);
			chart.putParameter(chart.MONTH, listMonthInYear);
			chart.putParameter(chart.OPTION, option);
			
			result = chart.execute(context);
		}
		return result;
	}
	
}
