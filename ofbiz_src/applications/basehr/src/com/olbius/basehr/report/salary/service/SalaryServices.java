package com.olbius.basehr.report.salary.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.olbius.basehr.kpiperfreview.helper.PerfReviewKPIHelper;
import com.olbius.basehr.report.ReportUtil;
import com.olbius.basehr.report.salary.query.ResultPayrollFormulaByChar;
import com.olbius.basehr.report.salary.query.ResultPayrollItemType;
import com.olbius.basehr.report.salary.query.SalaryOlapChartFlucImpl;
import com.olbius.basehr.report.salary.query.SalaryOlapChartImpl;
import com.olbius.basehr.report.salary.query.SalaryOlapImpl;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.grid.OlapGrid;

public class SalaryServices {
	@SuppressWarnings({ "unchecked", "static-access" })
	public static Map<String, Object> salaryDetailReport(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		String month = (String) context.get("month");
		String year = (String) context.get("year");
		List<String> employeeIdList = (List<String>) context.get("employeeId[]");
		int month_int = ReportUtil.getMonth(month);
		int year_int = Integer.parseInt(year);
		Timestamp month_tmp = PerfReviewKPIHelper.startDayMonth(year_int, month_int);
		Timestamp month_start = UtilDateTime.getMonthStart(month_tmp);
		
		SalaryOlapImpl grid = new SalaryOlapImpl(locale, timeZone, ctx);
		SalaryOlapImpl.getData getData = grid.new getData();
		
		OlapGrid olap = new OlapGrid(grid, getData);
		grid.setOlapResult(olap);
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		grid.setFromDate(month_start);
		grid.putParameter(grid.EMPLOYEE, employeeIdList);
		grid.putParameter(grid.FROMDATE, month_start);
		
		Map<String, Object> result = grid.execute((Map<String, Object>)context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "static-access", "unused" })
	public static Map<String, Object> salaryCompareChart(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String year = (String) context.get("yearChart");
		String orgId = (String) context.get("organization");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		
		String rootOrg = PartyUtil.getRootOrganization(delegator, userLoginId);
		
		Organization buildOrg = PartyUtil.buildOrg(delegator, rootOrg, true, false);
		List<GenericValue> listDepart = buildOrg.getDirectChildList(delegator);
		List<String> departmenList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(listDepart)){
			departmenList = EntityUtil.getFieldListFromEntityList(listDepart, "partyId", true);
		}
		
		buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
		List<String> employeeIdList = FastList.newInstance();
		List<GenericValue> listEmp = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
		if(UtilValidate.isNotEmpty(listEmp)){
			listEmp = EntityUtil.getFieldListFromEntityList(listEmp, "partyId", true);
		}
		
		int year_int = Integer.parseInt(year);
		List<String> month = (List<String>) context.get("monthChart[]");
		List<Timestamp> fromDateList = FastList.newInstance();
		for (String s : month) {
			int m = ReportUtil.getMonth(s);
			Timestamp date_tmp = PerfReviewKPIHelper.startDayMonth(year_int, m);
			Timestamp monthFrom = UtilDateTime.getMonthStart(date_tmp);
			fromDateList.add(monthFrom);
		}
		SalaryOlapChartImpl chart = new SalaryOlapChartImpl();
		SalaryOlapChartImpl.SalaryComparePie pie = chart.new SalaryComparePie();
		SalaryOlapChartImpl.SalaryComparePieOut pieOut = chart.new SalaryComparePieOut(chart, pie);
		
		chart.setOlapResult(pieOut);
		
		chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		chart.putParameter(chart.FROMDATE, fromDateList);
		chart.putParameter(chart.ROOT, rootOrg);
		chart.putParameter(chart.ORG, orgId);
		chart.putParameter(chart.DEPARTMENT, departmenList);
		chart.putParameter(chart.EMPLOYEE, listEmp);
		
		Map<String, Object> result = chart.execute();
		return result;
	}
	
	@SuppressWarnings("static-access")
	public static Map<String, Object> salaryFluctuationChart(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String yearFrom = (String) context.get("fromYear");
		String yearThru = (String) context.get("thruYear");
		String orgId = (String) context.get("organization");
		
		int year_start = Integer.parseInt(yearFrom);
		int year_end = Integer.parseInt(yearThru);
		List<Integer> listMap = FastList.newInstance();
		for(int i= 0; i <= (year_end - year_start); i++){
			int year = year_start + i;
			listMap.add(year);
		}
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		
		String rootOrg = PartyUtil.getRootOrganization(delegator, userLoginId);
		
		
		SalaryOlapChartFlucImpl chart = new SalaryOlapChartFlucImpl(delegator);
		
		chart.setOlapResultType(OlapColumnChart.class);
		
		chart.putParameter(chart.DEPATMENTID, orgId);
		chart.putParameter(chart.FROMDATE, listMap);
		chart.putParameter(chart.ROOT, rootOrg);
		Map<String, Object> result = chart.execute(context);
		return result;
	}
	
	@SuppressWarnings("static-access")
	public static Map<String, Object> getPayrollTableReport(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String yearFrom = (String) context.get("fromYear");
		String yearThru = (String) context.get("thruYear");
		String orgId = (String) context.get("organization");
		
		int year_start = Integer.parseInt(yearFrom);
		int year_end = Integer.parseInt(yearThru);
		List<Integer> listMap = FastList.newInstance();
		for(int i= 0; i <= (year_end - year_start); i++){
			int year = year_start + i;
			listMap.add(year);
		}
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		
		String rootOrg = PartyUtil.getRootOrganization(delegator, userLoginId);
		
		SalaryOlapChartFlucImpl chart = new SalaryOlapChartFlucImpl(delegator);
		
		chart.setOlapResultType(OlapColumnChart.class);
		
		chart.putParameter(chart.DEPATMENTID, orgId);
		chart.putParameter(chart.FROMDATE, listMap);
		chart.putParameter(chart.ROOT, rootOrg);
		Map<String, Object> result = chart.execute(context);
		return result;
	}
	
	public static Map<String, Object> getPayrollItemTypeDimensionList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		String isParent = (String)context.get("isParent");
		ResultPayrollItemType payrollItemTypeQuery = new ResultPayrollItemType(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), isParent);
		retMap.put("listReturn", payrollItemTypeQuery.getListPayrollItemType());
		return retMap;
	}
	public static Map<String, Object> getPayrollCharacteristicDimensionList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		String payrollCharacteristicId = (String)context.get("payrollCharacteristicId");
		ResultPayrollFormulaByChar payrollFormulaQuery = new ResultPayrollFormulaByChar(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), payrollCharacteristicId);
		retMap.put("listReturn", payrollFormulaQuery.getListPayrollFormulaByChar());
		return retMap;
	}
	
}
