package com.olbius.salesmtl.report;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.report.EvaluateRevenuePieChartImpl;
import com.olbius.basesales.report.SalesCustomerOlapImplv2;
import com.olbius.basesales.report.SalesOlapImplByChannelMultiv2;
import com.olbius.basesales.report.SalesOlapMultiImplv2;
import com.olbius.basesales.report.SalesOlapTopProductImplv2;
import com.olbius.basesales.report.SynthesisTurnoverReportBySalesExecutiveOlapImpl;
import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.salesmtl.util.MTLUtil;

import javolution.util.FastList;

public class SalesMtlGridServices {
	
	// GRID
	public static Map<String, Object> evaluateSalesExecutiveT(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		OrderVolumeImpl grid = new OrderVolumeImpl();
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		OlapGrid gridResult = new OlapGrid(grid, grid.new SaEx());
		grid.setOlapResult(gridResult);

		String partyId = (String) context.get("partyId");
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");

        grid.setFromDate(fromDate);
        grid.setThruDate(thruDate);
        grid.putParameter("partyId", partyId);
        
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateSalesExecutiveC(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		TopCustomerBySalesmanImpl grid = new TopCustomerBySalesmanImpl();
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		OlapGrid gridResult = new OlapGrid(grid, grid.new ResultCusTurnoverByS());
		grid.setOlapResult(gridResult);

		String partyId = (String) context.get("partyId");
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String confirm = "SM";

        grid.setFromDate(fromDate);
        grid.setThruDate(thruDate);
        grid.putParameter("partyId", partyId);
        grid.putParameter("confirm", confirm);
        
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateSalesSM(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOlapMultiImplv2 grid = new SalesOlapMultiImplv2(delegator);
		grid.setOlapResultType(OlapGrid.class);
        
		//get list SM
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALESMAN_EMPL"));
		List<String> listEmployee = null;
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> productStoreId = (List<String>) context.get("productStore[]");
		List<String> categoryId = (List<String>) context.get("category[]");
		String sortId = (String) context.get("sortId");
		List<String> status = (List<String>) context.get("orderStatus[]");
		String flagSM = "SM";
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
		
//		grid.setFromDate(fromDate);
//		grid.setThruDate(thruDate);
		grid.putParameter(SalesOlapMultiImplv2.PRODUCT_STORE, productStoreId);
		grid.putParameter(SalesOlapMultiImplv2.CATEGORY, categoryId);
		grid.putParameter(SalesOlapMultiImplv2.ORG, organization);
		grid.putParameter(SalesOlapMultiImplv2.SORT, sortId);
		grid.putParameter(SalesOlapMultiImplv2.ORDER_STATUS, status);
		grid.putParameter(SalesOlapMultiImplv2.PARTY, listEmployee);
		grid.putParameter(SalesOlapMultiImplv2.FLAGSM, flagSM);
 
		Map<String, Object> result = grid.execute((Map<String, Object>) context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateTurnoverDistributor(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userId = (String) userLogin.get("partyId");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOlapMultiImplv2 grid = new SalesOlapMultiImplv2(delegator);
		grid.setOlapResultType(OlapGrid.class);
		List<String> listEmployee = PartyWorker.getSalesExecutiveIdsByDistributor(delegator, userId);
		String all = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", (Locale)context.get("locale"));
        
		
		String customTime = (String) context.get("customTime");
		List<String> agencyId = (List<String>) context.get("agency[]");
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
		
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> productStoreId = (List<String>) context.get("productStore[]");
		List<String> categoryId = (List<String>) context.get("category[]");
		String sortId = (String) context.get("sortId");
		List<String> status = (List<String>) context.get("orderStatus[]");
		String flagSM = "SM";
		
		if("oo".equals(customTime)){
			grid.setFromDate(fromDate);
			grid.setThruDate(thruDate);
		} else {
			grid.setFromDate(beginDate);
			grid.setThruDate(currentDate);
		}
		grid.putParameter(SalesOlapMultiImplv2.PRODUCT_STORE, productStoreId);
		grid.putParameter(SalesOlapMultiImplv2.CATEGORY, categoryId);
		grid.putParameter(SalesOlapMultiImplv2.ORG, organization);
		grid.putParameter(SalesOlapMultiImplv2.SORT, sortId);
		grid.putParameter(SalesOlapMultiImplv2.ORDER_STATUS, status);
		grid.putParameter(SalesOlapMultiImplv2.PARTY, listEmployee);
		grid.putParameter(SalesOlapMultiImplv2.FLAGSM, flagSM);
		grid.putParameter(SalesOlapMultiImplv2.AGENCY_ID, agencyId);
		grid.putParameter(SalesOlapMultiImplv2.ALL, all);
 
		Map<String, Object> result = grid.execute();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateSynthesisRevenue(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
//		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SynthesisRevenueImpl grid = new SynthesisRevenueImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
        
		String dsaId = userLogin.getString("partyId");
		List<String> dep = PartyUtil.getDepartmentOfEmployee(delegator, dsaId, UtilDateTime.nowTimestamp());
		String flag2 = null;
		String depp = dep.get(0);
		String levelId = (String) context.get("levelId");
		Boolean flag = true;
		
		if(UtilValidate.isNotEmpty(levelId)){
			if(levelId.contains("CSM")) {
				flag2 = "CSMFlag";
			} else if(levelId.contains("RSM")) {
				flag2 = "RSMFlag";
			}else if(levelId.contains("ASM")) {
				flag2 = "ASMFlag";
			} else if(levelId.contains("SUP")) {
				flag2 = "SUPFlag";
			} else if(levelId.contains("DSA")) {
				flag2 = "DSAFlag";
			}
		} else {
			if(dsaId.contains("CSM")) {
				flag2 = "CSMFlag";
			} else if(dsaId.contains("RSM")) {
				flag2 = "RSMFlag";
			}else if(dsaId.contains("ASM")) {
				flag2 = "ASMFlag";
			} else if(dsaId.contains("SUP")) {
				flag2 = "SUPFlag";
			} else if(dsaId.contains("DSA")) {
				flag2 = "DSAFlag";
			}
		}
		
		grid.putParameter(SynthesisRevenueImpl.LEVEL, levelId);
		if(UtilValidate.isNotEmpty(levelId)){
			grid.putParameter(SynthesisRevenueImpl.DEP_ID, levelId);
			flag = true;
		} else {
			if(depp.contains("DSA")){
				grid.putParameter(SynthesisRevenueImpl.DEP_ID, depp);
				flag = false;
			} else {
				grid.putParameter(SynthesisRevenueImpl.DEP_ID, levelId);
				flag = true;
			}
		}
		grid.putParameter(SynthesisRevenueImpl.FLAG_CHILD, flag);
		grid.putParameter(SynthesisRevenueImpl.FLAG_PARENT, flag2);
 
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateSalesByChannelSM(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOlapImplByChannelMultiv2 grid = new SalesOlapImplByChannelMultiv2(delegator);
		grid.setOlapResultType(OlapGrid.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> storeChannelId = (List<String>) context.get("storeChannel[]");
		List<String> categoryId = (List<String>) context.get("category[]");
		String sortId = (String) context.get("sortId");
		String status = (String) context.get("orderStatus");
		String flagSM = "SM";
		
		//get list SM
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALESMAN_EMPL"));
		List<String> listEmployee = null;
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
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
		
		grid.putParameter(SalesOlapImplByChannelMultiv2.STORE_CHANNEL, storeChannelId);
		grid.putParameter(SalesOlapImplByChannelMultiv2.CATEGORY, categoryId);
		grid.putParameter(SalesOlapImplByChannelMultiv2.ORDER_STATUS, status);
		grid.putParameter(SalesOlapImplByChannelMultiv2.ORG, organization);
		grid.putParameter(SalesOlapImplByChannelMultiv2.SORT, sortId);
		grid.putParameter(SalesOlapImplByChannelMultiv2.PARTY, listEmployee); 
		grid.putParameter(SalesOlapImplByChannelMultiv2.FLAGSM, flagSM);

		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateTopProductSM(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOlapTopProductImplv2 grid = new SalesOlapTopProductImplv2(delegator);
		grid.setOlapResultType(OlapGrid.class);
//		SalesOlapTopProductImplv2.ResultReport test2 = grid.new ResultReport();
//		OlapGrid olapGrid = new OlapGrid(grid, test2);
		
//		grid.setOlapResult(olapGrid);
		
		//get list salesman
//		List<EntityCondition> listAllConditions = FastList.newInstance();
//        List<String> parties = FastList.newInstance();
//        if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
//        	parties = UtilMisc.toList(userLogin.getString("partyId"));
//		} else {
//			parties = EntityUtil.getFieldListFromEntityList(
//					PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
//					, "partyId", true);
//		}
//		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
//		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
//				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
//		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
//				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
//        List<String> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
		List<String> listEmployee = null;
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String topProduct = (String) context.get("topProduct");
		String statusSales = (String) context.get("statusSales");
		String status = (String) context.get("orderStatus");
		String all = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", (Locale)context.get("locale"));
		String storeChannelId = (String) context.get("storeChannel");
		List<String> categoryId = (List<String>) context.get("category[]");
		String flagSM = "SM";
		
//		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		grid.putParameter(SalesOlapTopProductImplv2.TOP_PRODUCT, topProduct);
		grid.putParameter(SalesOlapTopProductImplv2.STATUS_SALES, statusSales);
		grid.putParameter(SalesOlapTopProductImplv2.ORDER_STATUS, status);
		grid.putParameter(SalesOlapTopProductImplv2.STORE_CHANNEL, storeChannelId);
		grid.putParameter(SalesOlapTopProductImplv2.CATEGORY, categoryId);
		grid.putParameter(SalesOlapTopProductImplv2.ORG, organization);
		grid.putParameter(SalesOlapTopProductImplv2.ALL, all);
		grid.putParameter(SalesOlapTopProductImplv2.PARTY, listEmployee);
		grid.putParameter(SalesOlapTopProductImplv2.FLAGSM, flagSM);
		 
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateCustomerSM(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesCustomerOlapImplv2 grid = new SalesCustomerOlapImplv2(delegator);
		grid.setOlapResultType(OlapGrid.class);
//		SalesCustomerOlapImplv2.ResultReport test2 = grid.new ResultReport();
//		OlapGrid olapGrid = new OlapGrid(grid, test2);
//		grid.setOlapResult(olapGrid);
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String store = (String) context.get("store");
		String status = (String) context.get("orderStatus");
		String flagSM = "SM";
		
		//get list salesman
//		List<EntityCondition> listAllConditions = FastList.newInstance();
//        List<String> parties = FastList.newInstance();
//        if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
//        	parties = UtilMisc.toList(userLogin.getString("partyId"));
//		} else {
//			parties = EntityUtil.getFieldListFromEntityList(
//					PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
//					, "partyId", true);
//		}
//		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
//		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
//				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
//		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
//				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
//        List<String> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
		List<String> listEmployee = null;
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
		
//		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		grid.putParameter(SalesCustomerOlapImplv2.PRODUCT_STORE, store);
		grid.putParameter(SalesCustomerOlapImplv2.ORDER_STATUS, status);
		grid.putParameter(SalesCustomerOlapImplv2.ORGANIZATION, organization);
		grid.putParameter(SalesCustomerOlapImplv2.PARTY, listEmployee);
		grid.putParameter(SalesCustomerOlapImplv2.FLAGSM, flagSM);
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> synthesisReportBySalesExecutiveSM(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		SynthesisTurnoverReportBySalesExecutiveOlapImpl grid = new SynthesisTurnoverReportBySalesExecutiveOlapImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		//		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
//		OlapGrid gridResult = new OlapGrid(grid, grid.new TuReSaEx());
//		grid.setOlapResult(gridResult);

		List<String> status = (List<String>) context.get("orderStatus[]");
		String partyId = (String) context.get("partyId");
		String channel = (String) context.get("channel");
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String flagSM = "SM";
		
		//get list salesman
//		List<EntityCondition> listAllConditions = FastList.newInstance();
//        List<String> parties = FastList.newInstance();
//        if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
//        	parties = UtilMisc.toList(userLogin.getString("partyId"));
//		} else {
//			parties = EntityUtil.getFieldListFromEntityList(
//					PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
//					, "partyId", true);
//		}
//		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
//		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
//				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
//		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
//				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
//        List<String> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
        
        LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
		List<String> listEmployee = null;
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}

        grid.setFromDate(fromDate);
        grid.setThruDate(thruDate);
        grid.putParameter(SynthesisTurnoverReportBySalesExecutiveOlapImpl.ORDER_STATUS, status);
        grid.putParameter(SynthesisTurnoverReportBySalesExecutiveOlapImpl.CHANNEL, channel);
		grid.putParameter(SynthesisTurnoverReportBySalesExecutiveOlapImpl.PARTY, listEmployee); 
		grid.putParameter(SynthesisTurnoverReportBySalesExecutiveOlapImpl.FLAGSM, flagSM);
        grid.putParameter("partyId", partyId);
        
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	//get complete order volume 
//	public static Map<String, Object> getOrderQuantityCompletedv2(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, GenericEntityException {
//		Delegator delegator = ctx.getDelegator();
//		GenericValue userLogin = (GenericValue) context.get("userLogin");
//		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//		ResultValueSalesman grid = new ResultValueSalesman(delegator);
//		grid.setOlapResultType(OlapGrid.class);
//		
//		List<EntityCondition> listAllConditions = FastList.newInstance();
//		List<String> parties = FastList.newInstance();
//		
//		if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
//			parties = UtilMisc.toList(userLogin.getString("partyId"));
//		} else {
//			String userLogId = (String) userLogin.get("partyId");
//			parties = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
//		}
//		
//		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
//		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
//				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
//		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
//				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
//		
//		List<String> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
//		
//		grid.putParameter(ResultValueSalesman.SALESMAN, listSalesmanId);
// 
//		Map<String, Object> result = grid.execute(context);
//		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
//		return result;
//	}
	
	public static Map<String, Object> evaluateRegisterSpecialPromotion(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		EvaluateRegisterSpecialPromotion grid = new EvaluateRegisterSpecialPromotion(delegator);
		grid.setOlapResultType(OlapGrid.class);
		
		String getMonth = (String) context.get("monthh");
		String getYear = (String) context.get("yearr");
		String distributorId = userLogin.getString("partyId");
		
		
		grid.putParameter(EvaluateRegisterSpecialPromotion.MONTHH, getMonth);
		grid.putParameter(EvaluateRegisterSpecialPromotion.YEARR, getYear);
		grid.putParameter(EvaluateRegisterSpecialPromotion.DISTRIBUTOR, distributorId);
		
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> getTopProductBySalesman(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		TopProductBySalesman grid = new TopProductBySalesman(delegator);
		grid.setOlapResultType(OlapGrid.class);
		
		String getMonth = (String) context.get("monthh");
		String getYear = (String) context.get("yearr");
		String distributorId = userLogin.getString("partyId");
		List<String> listSalesmanId = PartyWorker.getSalesExecutiveIdsByDistributor(delegator, distributorId);
		
		grid.putParameter(TopProductBySalesman.MONTHH, getMonth);
		grid.putParameter(TopProductBySalesman.YEARR, getYear);
		grid.putParameter(TopProductBySalesman.SALESMAN, listSalesmanId); 

		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	// END GRID
	
	// CHART
	public static Map<String, Object> evaluateTopSalesmanChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String quantity0 = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMTurnover", (Locale)context.get("locale"));
		OrderVolumeImpl grid = new OrderVolumeImpl();
		String userLogId = (String) userLogin.get("partyId");
		List<String> listSalesmanId = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
		
		grid.setOlapResult(new OlapColumnChart(grid, grid.new TSColumn()));
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String isChart = "Y";
		
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		grid.putParameter(OrderVolumeImpl.ORG, organization);
		grid.putParameter(OrderVolumeImpl.TURNOVER, quantity0);
		grid.putParameter(OrderVolumeImpl.CHART, isChart);
		grid.putParameter(OrderVolumeImpl.SALESMAN, listSalesmanId);
		
		Map<String, Object> result = grid.execute();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateTopSalesmanv2(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String quantity0 = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMTurnover", (Locale)context.get("locale"));
		TopSalesman chart = new TopSalesman(delegator);
		chart.setOlapResultType(OlapColumnChart.class);
		String userLogId = (String) userLogin.get("partyId");
		String getMonth = (String) context.get("monthh");
		String getYear = (String) context.get("yearr");
		String limitSalesman = (String) context.get("topSalesman");
		String position = (String) context.get("position");
		List<String> listSalesmanId = FastList.newInstance();
		if("distributor_true".equals(position)){
			listSalesmanId = PartyWorker.getSalesExecutiveIdsByDistributor(delegator, userLogId);
		} else {
			listSalesmanId = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
		}
		
		chart.putParameter(TopSalesman.ORG, organization);
		chart.putParameter(TopSalesman.TURNOVER, quantity0);
		chart.putParameter(TopSalesman.SALESMAN, listSalesmanId);
		chart.putParameter(TopSalesman.YEARR, getYear);
		chart.putParameter(TopSalesman.MONTHH, getMonth);
		chart.putParameter(TopSalesman.LIMIT_SALESMAN, limitSalesman); 
		
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateTopCustomerv2(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String quantity0 = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMTurnover", (Locale)context.get("locale"));
		TopSalesman chart = new TopSalesman(delegator);
		chart.setOlapResultType(OlapColumnChart.class);
		String userLogId = (String) userLogin.get("partyId");
		String getMonth = (String) context.get("monthh");
		String getYear = (String) context.get("yearr");
		String limitCustomer = (String) context.get("topCustomer");
		String position = (String) context.get("position");
		List<String> listSalesmanId = FastList.newInstance();
		if("distributor_true".equals(position)){
			listSalesmanId = PartyWorker.getSalesExecutiveIdsByDistributor(delegator, userLogId);
		} else {
			listSalesmanId = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
		}
		
		//type person: true-customer false-salesman
		Boolean flagTypePerson = true;
		
		chart.putParameter(TopSalesman.ORG, organization);
		chart.putParameter(TopSalesman.TURNOVER, quantity0);
		chart.putParameter(TopSalesman.SALESMAN, listSalesmanId);
		chart.putParameter(TopSalesman.YEARR, getYear);
		chart.putParameter(TopSalesman.MONTHH, getMonth);
		chart.putParameter(TopSalesman.LIMIT_SALESMAN, limitCustomer); 
		chart.putParameter(TopSalesman.FLAG_TYPE_PERSON, flagTypePerson);  
		
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateTopSalesmanChartDis(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String quantity0 = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMTurnover", (Locale)context.get("locale"));
		OrderVolumeImpl grid = new OrderVolumeImpl();
//		String userLogId = (String) userLogin.get("partyId");
//		List<String> listSalesmanId = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
		String userLogId = (String) userLogin.get("partyId");
		List<String> listSupId = PartyWorker.getSupIdsByDistributor(delegator, userLogId);
		String supId = listSupId.get(0);
		List<String> tmp1 = PartyUtil.getDepartmentOfEmployee(delegator, supId, UtilDateTime.nowTimestamp(), null);
		String tmp_first = tmp1.get(0);
		Organization buildOrg = PartyUtil.buildOrg(delegator, tmp_first, true, false);
		List<GenericValue> listEmpl = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
		List<String> salesman = EntityUtil.getFieldListFromEntityList(listEmpl, "partyId", true);
		
		grid.setOlapResult(new OlapColumnChart(grid, grid.new TSColumn()));
		
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String isChart = "Y";
		
        grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

        grid.setFromDate(fromDate);
        grid.setThruDate(thruDate);
		grid.putParameter(OrderVolumeImpl.ORG, organization);
		grid.putParameter(OrderVolumeImpl.TURNOVER, quantity0);
		grid.putParameter(OrderVolumeImpl.CHART, isChart);
		grid.putParameter(OrderVolumeImpl.SALESMAN, salesman);
        
		Map<String, Object> result = grid.execute();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateTopSalesmanChartFromSM(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String quantity0 = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMTurnover", (Locale)context.get("locale"));
		OrderVolumeImpl grid = new OrderVolumeImpl();
//		String userLogId = (String) userLogin.get("partyId");
//		List<String> listSalesmanId = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
		String userLogId = (String) userLogin.get("partyId");
//		String distributorId = PartyWorker.getDistributorIdBySalesExecutive(delegator, userLogId);
		
		List<String> listSupId = PartyWorker.getSupIdsByDistributor(delegator, userLogId);
		String supId = listSupId.get(0);
		List<String> tmp1 = PartyUtil.getDepartmentOfEmployee(delegator, supId, UtilDateTime.nowTimestamp(), null);
		String tmp_first = tmp1.get(0);
		Organization buildOrg = PartyUtil.buildOrg(delegator, tmp_first, true, false);
		List<GenericValue> listEmpl = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
		List<String> salesman = EntityUtil.getFieldListFromEntityList(listEmpl, "partyId", true);
		
		grid.setOlapResult(new OlapColumnChart(grid, grid.new TSColumn()));
		
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String isChart = "Y";
		
        grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

        grid.setFromDate(fromDate);
        grid.setThruDate(thruDate);
		grid.putParameter(OrderVolumeImpl.ORG, organization);
		grid.putParameter(OrderVolumeImpl.TURNOVER, quantity0);
		grid.putParameter(OrderVolumeImpl.CHART, isChart);
		grid.putParameter(OrderVolumeImpl.SALESMAN, salesman);
        
		Map<String, Object> result = grid.execute();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	
	public static Map<String, Object> salesmanOrderTotal(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String dateType= (String) context.get("dateType");
		String partyId = (String) context.get("partyId");
        Boolean taxFlag = (Boolean) context.get("taxFlag");		
        String flag= (String) context.get("flag");
        String show = (String) context.get("show");
        List<?> type = (List<?>) context.get("type[]");
        Map<String, String> map = new HashMap<String, String>();
        List<String> listSalesmanId = null;
        if("b".equals(flag)){
//	        Map<String, Object> abc = DistributorServices.listSalesman(ctx, context);
	        List<EntityCondition> listAllConditions = FastList.newInstance();
	        List<String> parties = FastList.newInstance();
	        
	        if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
	        	parties = UtilMisc.toList(userLogin.getString("partyId"));
			} else {
				parties = EntityUtil.getFieldListFromEntityList(
						PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
						, "partyId", true);
			}
	        
			listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
					"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
			List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
					EntityCondition.makeCondition(listAllConditions), null, null, null, false);
	        
	        listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
        } else if("a".equals(flag)){
        	String userLogId = (String) userLogin.get("partyId");
        	listSalesmanId = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
        }
//        
//		List<String> listSupId = PartyWorker.getSupIdsByDistributor(delegator, userLogId);
//		String supId = listSupId.get(0);
//		List<String> tmp1 = PartyUtil.getDepartmentOfEmployee(delegator, supId, UtilDateTime.nowTimestamp(), null);
//		String tmp_first = tmp1.get(0);
//		Organization buildOrg = PartyUtil.buildOrg(delegator, tmp_first, true, false);
//		List<GenericValue> listEmpl = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
//		List<String> salesman = EntityUtil.getFieldListFromEntityList(listEmpl, "partyId", true);
        
        for(Object s : type) {
        	String tmp = (String) s;
        	tmp = (String) context.get(tmp);
        	map.put((String) s, tmp);
        }
        
        String currency = (String) context.get("currency");
        Boolean quantity = (Boolean) context.get("quantity");
        SalesValueImpl olap = new SalesValueImpl();
        
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

		olap.setOlapResult(new OlapLineChart(olap, olap.new SalesAmountTotalChartResult()));
		
        olap.setFromDate(fromDate);
        olap.setThruDate(thruDate);
        
        olap.putParameter(TypeOlap.DATE_TYPE, dateType);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.TAX_FLAG, taxFlag);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.SHOW, show);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.TYPE, map);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.CURRENCY, currency);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.OLAP_QUANTITY, quantity);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.PARTY, partyId);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.SALESMAN, listSalesmanId);  
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.FLAG, flag);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.ORGANIZATION, organization);
        
		Map<String, Object> result = olap.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

		return result;
	}
	
	public static Map<String, Object> SAOrderTotal(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String salesAdminId = userLogin.getString("partyId"); 
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String dateType= (String) context.get("dateType");
		Boolean taxFlag = (Boolean) context.get("taxFlag");		
		String flag= "sa";
		String show = (String) context.get("show");
		List<?> type = (List<?>) context.get("type[]");
		Map<String, String> map = new HashMap<String, String>();
		
		for(Object s : type) {
			String tmp = (String) s;
			tmp = (String) context.get(tmp);
			map.put((String) s, tmp);
		}
		
		String currency = (String) context.get("currency");
		Boolean quantity = (Boolean) context.get("quantity");
		SalesValueImpl olap = new SalesValueImpl();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setOlapResult(new OlapLineChart(olap, olap.new SalesAmountTotalChartResult()));
		
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		
		olap.putParameter(TypeOlap.DATE_TYPE, dateType);
		olap.putParameter(com.olbius.salesmtl.report.SalesOlap.TAX_FLAG, taxFlag);
		olap.putParameter(com.olbius.salesmtl.report.SalesOlap.SHOW, show);
		olap.putParameter(com.olbius.salesmtl.report.SalesOlap.TYPE, map);
		olap.putParameter(com.olbius.salesmtl.report.SalesOlap.CURRENCY, currency);
		olap.putParameter(com.olbius.salesmtl.report.SalesOlap.OLAP_QUANTITY, quantity);
		olap.putParameter(com.olbius.salesmtl.report.SalesOlap.SALESMAN, salesAdminId);  
		olap.putParameter(com.olbius.salesmtl.report.SalesOlap.FLAG, flag);
		olap.putParameter(com.olbius.salesmtl.report.SalesOlap.ORGANIZATION, organization);
		
		Map<String, Object> result = olap.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> OrderTotalSADM(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
//		String salesAdminId = userLogin.getString("partyId"); 
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String dateType= (String) context.get("dateType");
        Boolean taxFlag = (Boolean) context.get("taxFlag");		
        String show = (String) context.get("show");
        List<?> type = (List<?>) context.get("type[]");
        Map<String, String> map = new HashMap<String, String>();
        LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
		List<String> listEmployee = FastList.newInstance();
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
		List<Object> listSalesAdmin = new ArrayList<Object>(listEmployee);
        
        for(Object s : type) {
        	String tmp = (String) s;
        	tmp = (String) context.get(tmp);
        	map.put((String) s, tmp);
        }
        
        String currency = (String) context.get("currency");
        Boolean quantity = (Boolean) context.get("quantity");
        SalesValueImpl olap = new SalesValueImpl();
        
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

		olap.setOlapResult(new OlapLineChart(olap, olap.new SalesAmountTotalChartResult()));
		
        olap.setFromDate(fromDate);
        olap.setThruDate(thruDate);
        
        olap.putParameter(TypeOlap.DATE_TYPE, dateType);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.TAX_FLAG, taxFlag);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.SHOW, show);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.TYPE, map);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.CURRENCY, currency);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.OLAP_QUANTITY, quantity);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.SALESMAN, listSalesAdmin);  
//        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.FLAG, flag);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.ORGANIZATION, organization);
        
		Map<String, Object> result = olap.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

		return result;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map<String, Object> salesmanOrderTotalDis(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String dateType= (String) context.get("dateType");
		String partyId = (String) context.get("partyId");
        Boolean taxFlag = (Boolean) context.get("taxFlag");		
        String flag= (String) context.get("flag");
        String show = (String) context.get("show");
        List<?> type = (List<?>) context.get("type[]");
        Map<String, String> map = new HashMap<String, String>();
        
//        LocalDispatcher dispatcher = ctx.getDispatcher();
//		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
//		List<String> listEmployee = null;
//		if (ServiceUtil.isSuccess(ccc)) {
//			listEmployee = (List<String>) ccc.get("listEmployee");
//		}
        
        String userLogId = (String) userLogin.get("partyId");
		List<String> listSupId = PartyWorker.getSupIdsByDistributor(delegator, userLogId);
		String supId = listSupId.get(0);
		List<String> tmp1 = PartyUtil.getDepartmentOfEmployee(delegator, supId, UtilDateTime.nowTimestamp(), null);
		String tmp_first = tmp1.get(0);
		Organization buildOrg = PartyUtil.buildOrg(delegator, tmp_first, true, false);
		List<GenericValue> listEmpl = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
		List<String> salesman = EntityUtil.getFieldListFromEntityList(listEmpl, "partyId", true);
				
		
		//get list salesman
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
		List<String> listEmployee = null;
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
        
//        GenericValue product = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", s), false);
//		String internalName = product.getString("storeName");
//        getSupIdsByDistributor
//        String managerOfPartyGroup = PartyUtil.getManagerbyOrg(partGroupId, delegator);
        
//        Map<String, Object> abc = DistributorServices.listSalesman(ctx, context);
//        List<EntityCondition> listAllConditions = FastList.newInstance();
//        List<String> parties = FastList.newInstance();
//        
//        if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
//        	parties = UtilMisc.toList(userLogin.getString("partyId"));
//		} else {
//			parties = EntityUtil.getFieldListFromEntityList(
//					PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
//					, "partyId", true);
//		}
//        
//		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
//		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
//				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
//		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
//				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
//        
//        List<String> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
        
        for(Object s : type) {
        	String tmp = (String) s;
        	tmp = (String) context.get(tmp);
        	map.put((String) s, tmp);
        }
        
        String currency = (String) context.get("currency");
        Boolean quantity = (Boolean) context.get("quantity");
        SalesValueImpl olap = new SalesValueImpl();
        
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

		olap.setOlapResult(new OlapLineChart(olap, olap.new SalesAmountTotalChartResult()));
		
        olap.setFromDate(fromDate);
        olap.setThruDate(thruDate);
        
        olap.putParameter(TypeOlap.DATE_TYPE, dateType);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.TAX_FLAG, taxFlag);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.SHOW, show);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.TYPE, map);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.CURRENCY, currency);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.OLAP_QUANTITY, quantity);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.PARTY, partyId);
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.SALESMAN, salesman);  
        olap.putParameter(com.olbius.salesmtl.report.SalesOlap.FLAG, flag);
        
		Map<String, Object> result = olap.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

		return result;
	}
	
	public static Map<String, Object> evaluateTopCustomerPieChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		TopCustomerBySalesmanImpl chart = new TopCustomerBySalesmanImpl();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
//        String partyId = (String) context.get("partyId");
        String level = "ARC";
        List<String> listSalesmanId = null;
        
        List<EntityCondition> listAllConditions = FastList.newInstance();
        List<String> parties = FastList.newInstance();
        
        if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
        	parties = UtilMisc.toList(userLogin.getString("partyId"));
        	
    		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
    		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
    		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
    				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
    		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
    				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
            
            listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		} else {
	        String userLogId = (String) userLogin.get("partyId");
	        listSalesmanId = PartyUtil.getListEmplMgrByParty(delegator, userLogId, UtilDateTime.nowTimestamp(), null);
		}
        chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
        chart.setFromDate(fromDate);
        chart.setThruDate(thruDate);
		
        TopCustomerBySalesmanImpl.EvaluateTopCusPie query = chart.new EvaluateTopCusPie();
        TopCustomerBySalesmanImpl.EvaluateTopCus3Pie pieResult = chart.new EvaluateTopCus3Pie(chart, query);
        
		chart.setOlapResult(pieResult);
		chart.putParameter("partyId", listSalesmanId); 
		
		chart.putParameter(TopCustomerBySalesmanImpl.LEVEL, level); 
        
		Map<String, Object> result = chart.execute(context);
        
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}	
	
	@SuppressWarnings("unused")
	public static Map<String, Object> evaluateTopCustomerDisPieChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		TopCustomerBySalesmanImpl chart = new TopCustomerBySalesmanImpl();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
//        String partyId = (String) context.get("partyId");
        
        String userLogId = (String) userLogin.get("partyId");
		List<String> listSupId = PartyWorker.getSupIdsByDistributor(delegator, userLogId);
		String supId = listSupId.get(0);
		List<String> tmp1 = PartyUtil.getDepartmentOfEmployee(delegator, supId, UtilDateTime.nowTimestamp(), null);
		String tmp_first = tmp1.get(0);
		Organization buildOrg = PartyUtil.buildOrg(delegator, tmp_first, true, false);
		List<GenericValue> listEmpl = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
		List<String> salesman = EntityUtil.getFieldListFromEntityList(listEmpl, "partyId", true);
        
        List<EntityCondition> listAllConditions = FastList.newInstance();
        List<String> parties = FastList.newInstance();
        
        if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
        	parties = UtilMisc.toList(userLogin.getString("partyId"));
		} else {
			parties = EntityUtil.getFieldListFromEntityList(
					PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
					, "partyId", true);
		}
        
		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
				EntityCondition.makeCondition(listAllConditions), null, null, null, false);
        
//        List<String> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
        
        chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
        chart.setFromDate(fromDate);
        chart.setThruDate(thruDate);
		
        TopCustomerBySalesmanImpl.EvaluateTopCusPie query = chart.new EvaluateTopCusPie();
        TopCustomerBySalesmanImpl.EvaluateTopCus3Pie pieResult = chart.new EvaluateTopCus3Pie(chart, query);
        
		chart.setOlapResult(pieResult);
		chart.putParameter("partyId", salesman); 
        
		Map<String, Object> result = chart.execute(context);
        
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}	
	
	public static Map<String, Object> evaluateTurnoverStatePieChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		EvaluateRevenuePieChartImpl chart = new EvaluateRevenuePieChartImpl(delegator);
		chart.setOlapResultType(OlapPieChart.class);
		
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String flag = (String) context.get("flag");
        List<String> listSalesmanId = null;
        
        if("DISTRIBUTOR".equals(flag)){
        	//get list salesman
        	List<EntityCondition> listAllConditions = FastList.newInstance();
        	List<String> parties = FastList.newInstance();
        	if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
        		parties = UtilMisc.toList(userLogin.getString("partyId"));
        	} else {
        		parties = EntityUtil.getFieldListFromEntityList(
        				PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
        				, "partyId", true);
        	}
        	listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
        	listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
        	listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
        			"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
        	List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
        			EntityCondition.makeCondition(listAllConditions), null, null, null, false);
        	listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
        }
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
			chart.setFromDate(fromDate);
			chart.setThruDate(thruDate);
		} else {
			chart.setFromDate(beginDate);
			chart.setThruDate(currentDate);
		}
		
        
		chart.putParameter(EvaluateRevenuePieChartImpl.ORG, organization);
		chart.putParameter(EvaluateRevenuePieChartImpl.SALESMAN, listSalesmanId); 
		chart.putParameter(EvaluateRevenuePieChartImpl.FLAG, flag);
        
		Map<String, Object> result = chart.execute(context);
        
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	public static Map<String, Object> evaluateTSPieGrid(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		EvaluateRevenuePieChartImpl grid = new EvaluateRevenuePieChartImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String flag = (String) context.get("flag");
		List<String> listSalesmanId = null;
		
		if("DISTRIBUTOR".equals(flag)){
			//get list salesman
			List<EntityCondition> listAllConditions = FastList.newInstance();
			List<String> parties = FastList.newInstance();
			if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
				parties = UtilMisc.toList(userLogin.getString("partyId"));
			} else {
				parties = EntityUtil.getFieldListFromEntityList(
						PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
						, "partyId", true);
			}
			listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
					"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
			List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
					EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
		}
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
		
		grid.putParameter(EvaluateRevenuePieChartImpl.ORG, organization);
		grid.putParameter(EvaluateRevenuePieChartImpl.SALESMAN, listSalesmanId); 
		grid.putParameter(EvaluateRevenuePieChartImpl.FLAG, flag);
		
		Map<String, Object> result = grid.execute();
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
//	public static Map<String, Object> evaluateSalesPPSAreaChartSM(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, GenericEntityException {
//		Delegator delegator = ctx.getDelegator();
//		GenericValue userLogin = (GenericValue) context.get("userLogin");
////		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//		String all = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", (Locale)context.get("locale"));
//		String total = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotal", (Locale)context.get("locale"));
//		SalesOlapImplv2 grid2 = new SalesOlapImplv2();
//		SalesOlapImplv2.PPSArea area = grid2.new PPSArea();
//		SalesOlapImplv2.PPS3Area area3 = grid2.new PPS3Area(grid2, area);
//		
//		grid2.setOlapResult(area3);
//		
//        Date fromDate = (Date) context.get("fromDate");
//        Date thruDate = (Date) context.get("thruDate");
//        String productStoreId = (String) context.get("productStore");
//        String orderStatus = (String) context.get("orderStatus");
//        String flagSM = (String)context.get("SM");
//        
//        //get list salesman
//  		List<EntityCondition> listAllConditions = FastList.newInstance();
//  		List<String> parties = FastList.newInstance();
//  		if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false)) {
//  			parties = UtilMisc.toList(userLogin.getString("partyId"));
//  		} else {
//  			parties = EntityUtil.getFieldListFromEntityList(PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis())), "partyId", true);
//  		}
//  		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//  		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
//  		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE", "roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
//  		List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
//  		List<String> listSalesmanId = EntityUtil.getFieldListFromEntityList(listSalesman, "partyId", true);
//		
//        grid2.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
//
//        grid2.setFromDate(fromDate);
//        grid2.setThruDate(thruDate);
//        grid2.putParameter(SalesOlapImplv2.ORDER_STATUS, orderStatus);
//        grid2.putParameter(SalesOlapImplv2.PRODUCT_STORE, productStoreId);
//        grid2.putParameter(SalesOlapImplv2.FLAGSM, flagSM);
//        grid2.putParameter(SalesOlapImplv2.SALESMAN, listSalesmanId); 
//		grid2.putParameter(SalesOlapImplv2.ALL, all);
//		grid2.putParameter(SalesOlapImplv2.TOTAL, total);
//        
//		Map<String, Object> result = grid2.execute();
//		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
//		return result;
//	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateSalesPPSAreaChartSM(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, GenericEntityException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOlapMultiImplv2 grid = new SalesOlapMultiImplv2(delegator);
		grid.setOlapResultType(OlapColumnChart.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> productStoreId = (List<String>) context.get("productStore[]");
		List<String> categoryId = (List<String>) context.get("category[]");
		List<String> status = (List<String>) context.get("orderStatus[]");
		String flagSM = "SM";
		
		//get list salesman
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
		List<String> listEmployee = null;
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
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

		grid.putParameter(SalesOlapMultiImplv2.PRODUCT_STORE, productStoreId);
		grid.putParameter(SalesOlapMultiImplv2.CATEGORY, categoryId);
		grid.putParameter(SalesOlapMultiImplv2.ORG, organization);
		grid.putParameter(SalesOlapMultiImplv2.ORDER_STATUS, status);
		grid.putParameter(SalesOlapMultiImplv2.FLAGSM, flagSM);
		grid.putParameter(SalesOlapMultiImplv2.PARTY, listEmployee); 
        
		Map<String, Object> result = grid.execute();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateSalesProDisAreaChart(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, GenericEntityException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userId = (String) userLogin.get("partyId");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOlapMultiImplv2 chart = new SalesOlapMultiImplv2(delegator);
		chart.setOlapResultType(OlapColumnChart.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> productStoreId = (List<String>) context.get("productStore[]");
		List<String> categoryId = (List<String>) context.get("category[]");
		String sortId = (String) context.get("sortId");
		List<String> status = (List<String>) context.get("orderStatus[]");
		String flagSM = "SM";
		
		List<String> listEmployee = PartyWorker.getSalesExecutiveIdsByDistributor(delegator, userId);
		
		String all = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", (Locale)context.get("locale"));
        
		String customTime = (String) context.get("customTime");
		List<String> agencyId = (List<String>) context.get("agency[]");
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
			chart.setFromDate(fromDate);
			chart.setThruDate(thruDate);
		} else {
			chart.setFromDate(beginDate);
			chart.setThruDate(currentDate);
		}
		chart.putParameter(SalesOlapMultiImplv2.PRODUCT_STORE, productStoreId);
		chart.putParameter(SalesOlapMultiImplv2.CATEGORY, categoryId);
		chart.putParameter(SalesOlapMultiImplv2.ORG, organization);
		chart.putParameter(SalesOlapMultiImplv2.SORT, sortId);
		chart.putParameter(SalesOlapMultiImplv2.ORDER_STATUS, status);
		chart.putParameter(SalesOlapMultiImplv2.PARTY, listEmployee);
		chart.putParameter(SalesOlapMultiImplv2.FLAGSM, flagSM);
		chart.putParameter(SalesOlapMultiImplv2.AGENCY_ID, agencyId);
		chart.putParameter(SalesOlapMultiImplv2.ALL, all);
	    
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateTurnoverPPSPCMTL(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOlapMultiImplv2 chart = new SalesOlapMultiImplv2(delegator);
		chart.setOlapResultType(OlapPieChart.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> productStoreId = (List<String>) context.get("productStore[]");
		List<String> categoryId = (List<String>) context.get("category[]");
		String sortId = (String) context.get("sortId");
		String status = (String) context.get("orderStatus");
		String flagSM = "SM";
		
		//get list salesman
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
		List<String> listEmployee = null;
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
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
			chart.setFromDate(fromDate);
			chart.setThruDate(thruDate);
		} else {
			chart.setFromDate(beginDate);
			chart.setThruDate(currentDate);
		}

		chart.putParameter(SalesOlapMultiImplv2.PRODUCT_STORE, productStoreId);
		chart.putParameter(SalesOlapMultiImplv2.CATEGORY, categoryId);
		chart.putParameter(SalesOlapMultiImplv2.ORG, organization);
		chart.putParameter(SalesOlapMultiImplv2.SORT, sortId);
		chart.putParameter(SalesOlapMultiImplv2.ORDER_STATUS, status);
		chart.putParameter(SalesOlapMultiImplv2.FLAGSM, flagSM);
		chart.putParameter(SalesOlapMultiImplv2.PARTY, listEmployee); 
		
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateTurnoverProDisPCMTL(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userId = (String) userLogin.get("partyId");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOlapMultiImplv2 chart = new SalesOlapMultiImplv2(delegator);
		chart.setOlapResultType(OlapPieChart.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> productStoreId = (List<String>) context.get("productStore[]");
		List<String> categoryId = (List<String>) context.get("category[]");
		String sortId = (String) context.get("sortId");
		List<String> status = (List<String>) context.get("orderStatus[]");
		String flagSM = "SM";
		
		List<String> listEmployee = PartyWorker.getSalesExecutiveIdsByDistributor(delegator, userId);
		
		String all = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", (Locale)context.get("locale"));
        
		
		String customTime = (String) context.get("customTime");
		List<String> agencyId = (List<String>) context.get("agency[]");
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
			chart.setFromDate(fromDate);
			chart.setThruDate(thruDate);
		} else {
			chart.setFromDate(beginDate);
			chart.setThruDate(currentDate);
		}
		chart.putParameter(SalesOlapMultiImplv2.PRODUCT_STORE, productStoreId);
		chart.putParameter(SalesOlapMultiImplv2.CATEGORY, categoryId);
		chart.putParameter(SalesOlapMultiImplv2.ORG, organization);
		chart.putParameter(SalesOlapMultiImplv2.SORT, sortId);
		chart.putParameter(SalesOlapMultiImplv2.ORDER_STATUS, status);
		chart.putParameter(SalesOlapMultiImplv2.PARTY, listEmployee);
		chart.putParameter(SalesOlapMultiImplv2.FLAGSM, flagSM);
		chart.putParameter(SalesOlapMultiImplv2.AGENCY_ID, agencyId);
		chart.putParameter(SalesOlapMultiImplv2.ALL, all);
		
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateSalesPCDAreaChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOlapImplByChannelMultiv2 chart = new SalesOlapImplByChannelMultiv2(delegator);
		chart.setOlapResultType(OlapColumnChart.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> storeChannelId = (List<String>) context.get("storeChannel[]");
		List<String> categoryId = (List<String>) context.get("category[]");
		String sortId = (String) context.get("sortId");
		String status = (String) context.get("orderStatus");
		String flagSM = "SM";
		
		//get list salesman
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALESMAN_EMPL"));
		List<String> listEmployee = null;
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
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
			chart.setFromDate(fromDate);
			chart.setThruDate(thruDate);
		} else {
			chart.setFromDate(beginDate);
			chart.setThruDate(currentDate);
		}
		
		chart.putParameter(SalesOlapImplByChannelMultiv2.STORE_CHANNEL, storeChannelId);
		chart.putParameter(SalesOlapImplByChannelMultiv2.CATEGORY, categoryId);
		chart.putParameter(SalesOlapImplByChannelMultiv2.ORDER_STATUS, status);
		chart.putParameter(SalesOlapImplByChannelMultiv2.ORG, organization);
		chart.putParameter(SalesOlapImplByChannelMultiv2.SORT, sortId);
		chart.putParameter(SalesOlapImplByChannelMultiv2.FLAGSM, flagSM);
		chart.putParameter(SalesOlapImplByChannelMultiv2.PARTY, listEmployee); 
        
		Map<String, Object> result = chart.execute();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateTurnoverPCPCMTL(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOlapImplByChannelMultiv2 chart = new SalesOlapImplByChannelMultiv2(delegator);
		chart.setOlapResultType(OlapPieChart.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> storeChannelId = (List<String>) context.get("storeChannel[]");
		List<String> categoryId = (List<String>) context.get("category[]");
		String sortId = (String) context.get("sortId");
		String status = (String) context.get("orderStatus");
		String flagSM = "SM";
		
		//get list salesman
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALESMAN_EMPL"));
		List<String> listEmployee = null;
		if (ServiceUtil.isSuccess(ccc)) {
			listEmployee = (List<String>) ccc.get("listEmployee");
		}
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
			chart.setFromDate(fromDate);
			chart.setThruDate(thruDate);
		} else {
			chart.setFromDate(beginDate);
			chart.setThruDate(currentDate);
		}
		
		chart.putParameter(SalesOlapImplByChannelMultiv2.STORE_CHANNEL, storeChannelId);
		chart.putParameter(SalesOlapImplByChannelMultiv2.CATEGORY, categoryId);
		chart.putParameter(SalesOlapImplByChannelMultiv2.ORDER_STATUS, status);
		chart.putParameter(SalesOlapImplByChannelMultiv2.ORG, organization);
		chart.putParameter(SalesOlapImplByChannelMultiv2.SORT, sortId);
		chart.putParameter(SalesOlapMultiImplv2.FLAGSM, flagSM);
		chart.putParameter(SalesOlapMultiImplv2.PARTY, listEmployee); 
		
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	// END CHART
	
	//prepare detail
	public static Date getEndOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    return calendar.getTime();
	}

	public static Date getStartOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListCompleteOrderToday(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		Date currentDate = new Date();
		Date currentFirstToday = getStartOfDay(currentDate);
		Date currentEndToday = getEndOfDay(currentDate);
		Timestamp currentFirstTimeToday = new Timestamp(currentFirstToday.getTime());
		Timestamp currentEndTimeToday = new Timestamp(currentEndToday.getTime());
		String positionType = (String) context.get("positionType");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String salesmanId = userLogin.getString("partyId");
		
		if(positionType == null) {
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", salesmanId));
		} else if(positionType != null && "DISTRIBUTORR".equals(positionType)) {
			List<String> smId = PartyWorker.getSalesExecutiveIdsByDistributor(delegator, salesmanId);
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", EntityOperator.IN, smId));
		} else if(positionType != null && "SA".equals(positionType)) {
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", salesmanId));
		} else if(positionType != null && "SADM".equals(positionType)){
			//get sales admin list by sales admin manager
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
			List<String> listEmployee = FastList.newInstance();
			if (ServiceUtil.isSuccess(ccc)) {
				listEmployee = (List<String>) ccc.get("listEmployee");
			}
			List<Object> listSalesAdmin = new ArrayList<Object>(listEmployee);
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", EntityOperator.IN, listSalesAdmin));
		}
		listAllConditions.add(EntityCondition.makeCondition("orderStatus", "ORDER_COMPLETED"));
		listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO , currentFirstTimeToday));
		listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO , currentEndTimeToday));
		
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
		if(positionType == null) {
			List<GenericValue> listCompleteOrder = delegator.findList("SimpleOrderDetail", cond, null, null, null, false);
			result.put("listCompleteOrder", listCompleteOrder);
		} else if(positionType != null && "DISTRIBUTORR".equals(positionType)) {
			List<GenericValue> listCompleteOrder = delegator.findList("SimpleOrderDetail2", cond, null, null, null, false);
			result.put("listCompleteOrder", listCompleteOrder);
		}  else if(positionType != null && "SA".equals(positionType) || positionType != null && "SADM".equals(positionType)) {
			List<GenericValue> listCompleteOrder = delegator.findList("SimpleOrderDetail", cond, null, null, null, false);
			result.put("listCompleteOrder", listCompleteOrder);
		}
		return result;
	} 
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListCancelOrderToday(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		Date currentDate = new Date();
		Date currentFirstToday = getStartOfDay(currentDate);
		Date currentEndToday = getEndOfDay(currentDate);
		Timestamp currentFirstTimeToday = new Timestamp(currentFirstToday.getTime());
		Timestamp currentEndTimeToday = new Timestamp(currentEndToday.getTime());
		String positionType = (String) context.get("positionType");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String salesmanId = userLogin.getString("partyId");
		
		if(positionType == null) {
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", salesmanId));
		} else if(positionType != null && "DISTRIBUTORR".equals(positionType)) {
			List<String> smId = PartyWorker.getSalesExecutiveIdsByDistributor(delegator, salesmanId);
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", EntityOperator.IN, smId));
		} else if(positionType != null && "SA".equals(positionType)) {
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", salesmanId));
		} else if(positionType != null && "SADM".equals(positionType)){
			//get sales admin list by sales admin manager
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
			List<String> listEmployee = FastList.newInstance();
			if (ServiceUtil.isSuccess(ccc)) {
				listEmployee = (List<String>) ccc.get("listEmployee");
			}
			List<Object> listSalesAdmin = new ArrayList<Object>(listEmployee);
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", EntityOperator.IN, listSalesAdmin));
		}
		listAllConditions.add(EntityCondition.makeCondition("orderStatus", "ORDER_CANCELLED"));
		listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO , currentFirstTimeToday));
		listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO , currentEndTimeToday));
		
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
		if(positionType == null) {
			List<GenericValue> listCancelOrder = delegator.findList("SimpleOrderDetail", cond, null, null, null, false);
			result.put("listCancelOrder", listCancelOrder);
		} else if(positionType != null && "DISTRIBUTORR".equals(positionType)) {
			List<GenericValue> listCancelOrder = delegator.findList("SimpleOrderDetail2", cond, null, null, null, false);
			result.put("listCancelOrder", listCancelOrder);
		}  else if(positionType != null && "SA".equals(positionType) || positionType != null && "SADM".equals(positionType)) {
			List<GenericValue> listCancelOrder = delegator.findList("SimpleOrderDetail", cond, null, null, null, false);
			result.put("listCancelOrder", listCancelOrder);
		}
		return result;
	} 
	
	//get in transit order list
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListIntransitOrderToday(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		Date currentDate = new Date();
		Date currentFirstToday = getStartOfDay(currentDate);
		Date currentEndToday = getEndOfDay(currentDate);
		Timestamp currentFirstTimeToday = new Timestamp(currentFirstToday.getTime());
		Timestamp currentEndTimeToday = new Timestamp(currentEndToday.getTime());
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String salesmanId = userLogin.getString("partyId");
		String positionType = (String) context.get("positionType");
		
		if(positionType == null) {
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", salesmanId));
		} else if(positionType != null && "DISTRIBUTORR".equals(positionType)) {
			List<String> smId = PartyWorker.getSalesExecutiveIdsByDistributor(delegator, salesmanId);
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", EntityOperator.IN, smId));
		} else if(positionType != null && "SA".equals(positionType)){
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", salesmanId));
		} else if(positionType != null && "SADM".equals(positionType)){
			//get sales admin list by sales admin manager
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
			List<String> listEmployee = FastList.newInstance();
			if (ServiceUtil.isSuccess(ccc)) {
				listEmployee = (List<String>) ccc.get("listEmployee");
			}
			List<Object> listSalesAdmin = new ArrayList<Object>(listEmployee);
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", EntityOperator.IN, listSalesAdmin));
		}
		listAllConditions.add(EntityCondition.makeCondition("orderStatus", "ORDER_APPROVED"));
		listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO , currentFirstTimeToday));
		listAllConditions.add(EntityCondition.makeCondition("orderDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO , currentEndTimeToday));
		
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
		if(positionType == null) {
			List<GenericValue> listIntransitOrder = delegator.findList("SimpleOrderDetail", cond, null, null, null, false);
			result.put("listIntransitOrder", listIntransitOrder);
		} else if(positionType != null && "DISTRIBUTORR".equals(positionType)) {
			List<GenericValue> listIntransitOrder = delegator.findList("SimpleOrderDetail2", cond, null, null, null, false);
			result.put("listIntransitOrder", listIntransitOrder);
		} else if(positionType != null && "SA".equals(positionType) || (positionType != null && "SADM".equals(positionType))) {
			List<GenericValue> listIntransitOrder = delegator.findList("SimpleOrderDetail", cond, null, null, null, false);
			result.put("listIntransitOrder", listIntransitOrder);
		}
		return result;
	} 
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListApproveOrderToday(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		String positionType = (String) context.get("positionType");
		GenericValue userLogin = (GenericValue)context.get("userLogin");

		if(positionType != null && "SADM".equals(positionType)){
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Map<String, Object> ccc = dispatcher.runSync("getListEmplMgrByParty", UtilMisc.toMap("mgrUserLoginId", userLogin.get("partyId"), "userLogin", userLogin, "roleTypeId", "SALES_EXECUTIVE"));
			List<String> listEmployee = FastList.newInstance();
			if (ServiceUtil.isSuccess(ccc)) {
				listEmployee = (List<String>) ccc.get("listEmployee");
			}
			List<Object> listSalesAdmin = new ArrayList<Object>(listEmployee);
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", EntityOperator.IN, listSalesAdmin));
		} else if (UtilValidate.isNotEmpty(positionType) && "SA".equals(positionType)){
			String salesAdminId = userLogin.getString("partyId");
			listAllConditions.add(EntityCondition.makeCondition("salesmanId", salesAdminId));
		}
		listAllConditions.add(EntityCondition.makeCondition("orderStatus", "ORDER_CREATED"));
		
		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
		if((positionType != null && "SA".equals(positionType)) || (positionType != null && "SADM".equals(positionType))) {
			List<GenericValue> listApproveOrder = delegator.findList("SimpleOrderDetail", cond, null, null, null, false);
			result.put("listApproveOrder", listApproveOrder);
		}
		return result;
	} 
	
	public static Map<String, Object> getNewAgencyList(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
//		GenericValue userLogin = (GenericValue) context.get("userLogin");
//		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		NewAgencyReportImpl grid = new NewAgencyReportImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		
		String getMonth = (String) context.get("monthh");
		String getQuarter = (String) context.get("quarterr");
		String getYear = (String) context.get("yearr");
		String typeTime = (String) context.get("typeTime");
		
		grid.putParameter(NewAgencyReportImpl.MONTHH, getMonth);
		grid.putParameter(NewAgencyReportImpl.QUARTERR, getQuarter);
		grid.putParameter(NewAgencyReportImpl.YEARR, getYear);
		grid.putParameter(NewAgencyReportImpl.TIME_TYPE, typeTime);

		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateAgencyChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		EvaluateAgency grid2 = new EvaluateAgency(delegator);
		grid2.setOlapResultType(OlapColumnChart.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String agencyId = (String) context.get("agencyId");
		String series = (String) context.get("series");
		
		grid2.setFromDate(fromDate);
		grid2.setThruDate(thruDate);
		grid2.putParameter(EvaluateAgency.AGENCY_ID, agencyId);
		grid2.putParameter(EvaluateAgency.SERIES, series);
		
		Map<String, Object> result = grid2.execute();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateSalesman(DispatchContext ctx, Map<String, ? extends Object> context) 
			throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		EvaluateSalesman chart = new EvaluateSalesman(delegator);
		chart.setOlapResultType(OlapColumnChart.class);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userId = (String) userLogin.get("partyId");
		String flag = (String) context.get("flag");
		List<String> listEmployee = FastList.newInstance();
		if("sup".equals(flag)){
			listEmployee = PartyWorker.getSalesmanIdsBySup(delegator, userId);
		} else {
			listEmployee = PartyWorker.getSalesExecutiveIdsByDistributor(delegator, userId);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		Date firstDate = null;
		Date lastDate = null;
		cal.set(Calendar.DAY_OF_MONTH, 1);
		firstDate = cal.getTime();
		
		Calendar c = Calendar.getInstance();
		c.setTime(firstDate);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		lastDate = c.getTime();
		
		chart.setFromDate(firstDate);
        chart.setThruDate(lastDate);
        
//        chart.putParameter(TypeOlap.DATE_TYPE, dateType);
        chart.putParameter(EvaluateSalesman.SALESMAN_ID, listEmployee); 
        
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

		return result;
	}
	
}