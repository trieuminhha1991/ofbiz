package com.olbius.basesales.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.report.PromoSalesOlapImplByChannelv2;
import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.olap.bi.accounting.AccountingOlapImpl;
import com.olbius.olap.bi.sales.SalesOrderCountOlap;
import com.olbius.olap.bi.sales.SalesTotalOlap;

public class SalesGridServices {
	//GRID
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateSales(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesOlapMultiImplv2 grid = new SalesOlapMultiImplv2(delegator);
		grid.setOlapResultType(OlapGrid.class);
		List<String> productStoreId = (List<String>) context.get("productStore[]");
		List<String> categoryId = (List<String>) context.get("category[]");
		String status = (String) context.get("orderStatus");
//		String flag = (String) context.get("flag");
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
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
		
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateSalesByChannel(DispatchContext ctx, Map<String, ? extends Object> context)
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
		String status = (String) context.get("orderStatus");
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

		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateSalesGrowth(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesGrowthOlapImplv2 grid = new SalesGrowthOlapImplv2();
//		grid.setOlapResultType(OlapGrid.class);
		SalesGrowthOlapImplv2.ResultGrid resultG = grid.new ResultGrid();
		OlapGrid olapGrid = new OlapGrid(grid, resultG);
		grid.setOlapResult(olapGrid);
		
//        Date fromDate = (Date) context.get("fromDate");
//        Date thruDate = (Date) context.get("thruDate");
//        Date fromDate2 = (Date) context.get("fromDate2");
//        Date thruDate2 = (Date) context.get("thruDate2");
		String productStoreId = (String) context.get("productStore");
        String channelId = (String) context.get("channel");
        String typeOfDate = (String) context.get("typee");
        String typeOfFilter = (String) context.get("type2");

        String month1 = (String) context.get("monthh");
        String month2 = (String) context.get("monthh2");
        String quarter1 = (String) context.get("quarterr");
        String quarter2 = (String) context.get("quarterr2");
        String year1 = (String) context.get("yearr");
        String year2 = (String) context.get("yearr2");
        String all = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", (Locale)context.get("locale"));
		
        grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
        grid.putParameter(SalesGrowthOlapImplv2.PRODUCT_STORE, productStoreId);
        grid.putParameter(SalesGrowthOlapImplv2.CHANNEL, channelId); 
        grid.putParameter(SalesGrowthOlapImplv2.ALL, all);
        grid.putParameter(SalesGrowthOlapImplv2.MONTH_F, month1);
        grid.putParameter(SalesGrowthOlapImplv2.MONTH_S, month2);
        grid.putParameter(SalesGrowthOlapImplv2.QUARTER_F, quarter1);
        grid.putParameter(SalesGrowthOlapImplv2.QUARTER_S, quarter2);
        grid.putParameter(SalesGrowthOlapImplv2.YEAR_F, year1);
        grid.putParameter(SalesGrowthOlapImplv2.YEAR_S, year2);
        grid.putParameter(SalesGrowthOlapImplv2.TYPEE, typeOfDate);
        grid.putParameter(SalesGrowthOlapImplv2.TYPE_FILTER, typeOfFilter);
        grid.putParameter(SalesGrowthOlapImplv2.ORGANIZATION, organization);
		Map<String, Object> result = grid.execute();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateSalesCustomer(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SalesCustomerOlapImplv2 grid = new SalesCustomerOlapImplv2(delegator);
		grid.setOlapResultType(OlapGrid.class);
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String store = (String) context.get("store");
		String status = (String) context.get("orderStatus");
		List<String> channel = (List<String>) context.get("channel[]");
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
		
		grid.putParameter(SalesCustomerOlapImplv2.PRODUCT_STORE, store);
		grid.putParameter(SalesCustomerOlapImplv2.ORDER_STATUS, status);
		grid.putParameter(SalesCustomerOlapImplv2.ORGANIZATION, organization);
		grid.putParameter(SalesCustomerOlapImplv2.CHANNEL, channel);
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateSalesPByRegion(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		String total0 = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotalK", (Locale)context.get("locale"));
		SalesProductOlapImplByRegionv2 grid = new SalesProductOlapImplByRegionv2(delegator);
		grid.setOlapResultType(OlapGrid.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String region = (String) context.get("region");
		String status = (String) context.get("orderStatus");
//		
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		grid.putParameter(SalesProductOlapImplByRegionv2.REGION, region);
		grid.putParameter(SalesProductOlapImplByRegionv2.ORDER_STATUS, status);
		grid.putParameter(SalesProductOlapImplByRegionv2.TOTAL, total0);
		
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> calculateActualExportInventory(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		String total0 = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotalK", (Locale)context.get("locale"));
		SalesExportActual grid = new SalesExportActual(delegator);
		grid.setOlapResultType(OlapGrid.class);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		grid.putParameter(SalesExportActual.userLoginId, userLogin.getString("userLoginId"));
		
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateOrder(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String deliveryStatus = UtilProperties.getMessage("BaseLogisticsUiLabels", "Delivered", (Locale)context.get("locale"));

		OrderReportImpl grid = new OrderReportImpl();
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));		
		OrderReportImpl.OrderResult orderR = grid.new OrderResult();
		OlapGrid olapGrid = new OlapGrid(grid, orderR);
		
		grid.setOlapResult(olapGrid);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		Date fromDate2 = (Date) context.get("fromDate2");
		Date thruDate2 = (Date) context.get("thruDate2");
		List<String> status = (List<String>) context.get("orderStatus[]");
		List<String> channel = (List<String>) context.get("channel[]");
		String filterType = (String) context.get("filterType");
		
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		
		grid.putParameter(OrderReportImpl.ORG, organization);
		grid.putParameter(OrderReportImpl.ORDER_STATUS, status);
		grid.putParameter(OrderReportImpl.CHANNEL, channel);
		grid.putParameter(OrderReportImpl.FROM_DATE_2, fromDate2);
		grid.putParameter(OrderReportImpl.THRU_DATE_2, thruDate2);
		grid.putParameter(OrderReportImpl.CONT, context);
		grid.putParameter(OrderReportImpl.DEL_STATUS, deliveryStatus);
		grid.putParameter(OrderReportImpl.FILTER_DATE, filterType); 
		
		Map<String, Object> result = grid.execute((Map<String, Object>) context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateReturnOrder(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String deliveryStatus = UtilProperties.getMessage("BaseLogisticsUiLabels", "Delivered", (Locale)context.get("locale"));

		OrderReportImpl grid = new OrderReportImpl();
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));		
		OrderReportImpl.OrderResult orderR = grid.new OrderResult();
		OlapGrid olapGrid = new OlapGrid(grid, orderR);
		
		grid.setOlapResult(olapGrid);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		Date fromDate2 = (Date) context.get("fromDate2");
		Date thruDate2 = (Date) context.get("thruDate2");
		List<String> status = (List<String>) context.get("orderStatus[]");
		List<String> channel = (List<String>) context.get("channel[]");
		String filterType = (String) context.get("filterType");
		String flagRO = "RO";
		
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		
		grid.putParameter(OrderReportImpl.ORG, organization);
		grid.putParameter(OrderReportImpl.ORDER_STATUS, status);
		grid.putParameter(OrderReportImpl.CHANNEL, channel);
		grid.putParameter(OrderReportImpl.FROM_DATE_2, fromDate2);
		grid.putParameter(OrderReportImpl.THRU_DATE_2, thruDate2);
		grid.putParameter(OrderReportImpl.CONT, context);
		grid.putParameter(OrderReportImpl.DEL_STATUS, deliveryStatus);
		grid.putParameter(OrderReportImpl.FILTER_DATE, filterType); 
		grid.putParameter(OrderReportImpl.FLAGRO, flagRO); 
		
		Map<String, Object> result = grid.execute((Map<String, Object>) context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateSalesSynthesisReport(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		SynTurnRepByStoreOldVerOlapImpl grid = new SynTurnRepByStoreOldVerOlapImpl();
		
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		OlapGrid gridResult = new OlapGrid(grid, grid.new TuReSto());
		grid.setOlapResult(gridResult);
		
		@SuppressWarnings("unchecked")
		List<String> status = (List<String>) context.get("orderStatus[]");
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
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
        grid.putParameter(SynTurnRepByStoreOldVerOlapImpl.ORDER_STATUS, status);
        grid.putParameter(SynTurnRepByStoreOldVerOlapImpl.ORGANIZATION, organization);
        grid.putParameter(SynTurnRepByStoreOldVerOlapImpl.CONT, context);
        
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateSalesSynthesisReportByStaff(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		SynthesisTurnoverReportByStaffOlapImpl grid = new SynthesisTurnoverReportByStaffOlapImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		
		List<String> status = (List<String>) context.get("orderStatus[]"); 
		String channel = (String) context.get("channel");
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String company = UtilProperties.getMessage("BaseSalesUiLabels", "BSCompany", (Locale)context.get("locale"));
		String total0 = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotal", (Locale)context.get("locale"));
		String quantity0 = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity", (Locale)context.get("locale"));
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
		
		grid.putParameter(SynthesisTurnoverReportByStaffOlapImpl.ORDER_STATUS, status);
		grid.putParameter(SynthesisTurnoverReportByStaffOlapImpl.CHANNEL, channel);
		grid.putParameter(SynthesisTurnoverReportByStaffOlapImpl.QUANTITYDES, quantity0);
		grid.putParameter(SynthesisTurnoverReportByStaffOlapImpl.TOTALDES, total0);
		grid.putParameter(SynthesisTurnoverReportByStaffOlapImpl.DESCRIPTION, company); 
		
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateSalesSynthesisReportBySalesAdmin(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		SynthesisTurnoverReportByStaffOlapImpl grid = new SynthesisTurnoverReportByStaffOlapImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		List<String> status = (List<String>) context.get("orderStatus[]"); 
		String channel = (String) context.get("channel");
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String company = UtilProperties.getMessage("BaseSalesUiLabels", "BSCompany", (Locale)context.get("locale"));
		String total0 = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotal", (Locale)context.get("locale"));
		String quantity0 = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity", (Locale)context.get("locale"));
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
		
		grid.putParameter(SynthesisTurnoverReportByStaffOlapImpl.ORDER_STATUS, status);
		grid.putParameter(SynthesisTurnoverReportByStaffOlapImpl.CHANNEL, channel);
		grid.putParameter(SynthesisTurnoverReportByStaffOlapImpl.QUANTITYDES, quantity0);
		grid.putParameter(SynthesisTurnoverReportByStaffOlapImpl.TOTALDES, total0);
		grid.putParameter(SynthesisTurnoverReportByStaffOlapImpl.DESCRIPTION, company); 
		
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateSalesSynthesisReportBySalesExecutive(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		SynthesisTurnoverReportBySalesExecutiveOlapImpl grid = new SynthesisTurnoverReportBySalesExecutiveOlapImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);

		List<String> status = (List<String>) context.get("orderStatus[]");
		String partyId = (String) context.get("partyId");
		String channel = (String) context.get("channel");
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String company = UtilProperties.getMessage("BaseSalesUiLabels", "BSCompany", (Locale)context.get("locale"));
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

        grid.putParameter(SynthesisTurnoverReportBySalesExecutiveOlapImpl.ORDER_STATUS, status);
        grid.putParameter(SynthesisTurnoverReportBySalesExecutiveOlapImpl.CHANNEL, channel);
        grid.putParameter(SynthesisTurnoverReportBySalesExecutiveOlapImpl.DESCRIPTION, company);
        grid.putParameter("partyId", partyId);
        
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateCustomerSatisfaction(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		CustomerSatisfactionReportImpl grid = new CustomerSatisfactionReportImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> loyaltyGroupId = (List<String>) context.get("loyaltyGroup[]");
		List<String> storeId = (List<String>) context.get("store[]");
		List<String> channelId = (List<String>) context.get("channel[]");
		String quantity0 = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity", (Locale)context.get("locale"));
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
		
		grid.putParameter(CustomerSatisfactionReportImpl.GROUP, loyaltyGroupId);
		grid.putParameter(CustomerSatisfactionReportImpl.ORG, organization);
		grid.putParameter(CustomerSatisfactionReportImpl.SALES_CHANNEL, storeId);
		grid.putParameter(CustomerSatisfactionReportImpl.CHANNEL_TYPE, channelId);
		grid.putParameter(CustomerSatisfactionReportImpl.QUANTITY, quantity0);

		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateMostProfitableCustomer(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		MostProfitableCustomersImpl grid = new MostProfitableCustomersImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> productStoreId = (List<String>) context.get("productStore[]");
		List<String> channelId = (List<String>) context.get("channel[]");
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
		
		grid.putParameter(MostProfitableCustomersImpl.PRODUCT_STORE, productStoreId);
		grid.putParameter(MostProfitableCustomersImpl.CHANNEL, channelId);
		grid.putParameter(MostProfitableCustomersImpl.ORG, organization);

		Map<String, Object> result = grid.execute((Map<String, Object>) context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateEffectiveSales(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		EvaluateEffectiveSalesImpl grid = new EvaluateEffectiveSalesImpl();
		
		String year = (String) context.get("yearr");
		
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		grid.putParameter(EvaluateEffectiveSalesImpl.YEARR, year);
		grid.putParameter(EvaluateEffectiveSalesImpl.ORG, organization);

		Map<String, Object> result = grid.execute((Map<String, Object>) context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateEffectiveSalesOut(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		EvaluateEffectiveSalesOutImpl grid = new EvaluateEffectiveSalesOutImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);

		String year = (String) context.get("yearr");
		String month = (String) context.get("monthh");
		
		grid.putParameter(EvaluateEffectiveSalesOutImpl.MONTHH, month);
		grid.putParameter(EvaluateEffectiveSalesOutImpl.YEARR, year);
		grid.putParameter(EvaluateEffectiveSalesOutImpl.ORG, organization);
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
//	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateEffectiveSalesIn(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		EvaluateEffectiveSalesOutImpl grid = new EvaluateEffectiveSalesOutImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		
		String year = (String) context.get("yearr");
		String month = (String) context.get("monthh");
		List<Object> products = (List<Object>) context.get("products[]");
		String flagSales = "IN";
		
		grid.putParameter(EvaluateEffectiveSalesOutImpl.MONTHH, month);
		grid.putParameter(EvaluateEffectiveSalesOutImpl.YEARR, year);
		grid.putParameter(EvaluateEffectiveSalesOutImpl.ORG, organization);
		grid.putParameter(EvaluateEffectiveSalesOutImpl.FLAGIN, flagSales); 
		grid.putParameter(EvaluateEffectiveSalesOutImpl.PRODUCTS, products); 
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateEffectiveSalesInDis(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		EvaluateEffectiveSalesOutImpl grid = new EvaluateEffectiveSalesOutImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		String disId = userLogin.getString("partyId");

		String year = (String) context.get("yearr");
		String month = (String) context.get("monthh");
		String flagSales = "IN";
		Boolean dis = true;
		
		grid.putParameter(EvaluateEffectiveSalesOutImpl.MONTHH, month);
		grid.putParameter(EvaluateEffectiveSalesOutImpl.YEARR, year);
		grid.putParameter(EvaluateEffectiveSalesOutImpl.ORG, organization);
		grid.putParameter(EvaluateEffectiveSalesOutImpl.FLAGIN, flagSales); 
		grid.putParameter(EvaluateEffectiveSalesOutImpl.BOO_DIS, dis); 
		grid.putParameter(EvaluateEffectiveSalesOutImpl.DIS_ID, disId);  
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateSpecialPromotion(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ProductPromoExtImpl grid = new ProductPromoExtImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String flagPromo = "DISPLAY";
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
		
		grid.putParameter(ProductPromoExtImpl.ORG, organization);
		grid.putParameter(ProductPromoExtImpl.FLAGPROMO, flagPromo); 
		
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> evaluateAccumulationPromotion(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ProductPromoExtImpl grid = new ProductPromoExtImpl(delegator);
		grid.setOlapResultType(OlapGrid.class);
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String flagPromo = "ACCUMULATION";
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
		
		grid.putParameter(ProductPromoExtImpl.ORG, organization);
		grid.putParameter(ProductPromoExtImpl.FLAGPROMO, flagPromo); 

		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> statisticOrderPromo(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		OrderPromo grid = new OrderPromo(delegator);
		grid.setOlapResultType(OlapGrid.class);
		List<String> productStoreId = (List<String>) context.get("productStore[]");
		List<String> channelId = (List<String>) context.get("channel[]");
		String customTime = (String) context.get("customTime");
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
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
		grid.putParameter(OrderPromo.SALES_CHANNEL, productStoreId);
		grid.putParameter(OrderPromo.CHANNEL_TYPE, channelId);
		
		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	//END GRID
	
	//CHART
		@SuppressWarnings("unchecked")
		public static Map<String, Object> evaluateSalesPPSColumnChart(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException, ParseException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			SalesOlapMultiImplv2 chart = new SalesOlapMultiImplv2(delegator);
			chart.setOlapResultType(OlapColumnChart.class);
			List<String> productStoreId = (List<String>) context.get("productStore[]");
			List<String> categoryId = (List<String>) context.get("category[]");
			String status = (String) context.get("orderStatus");
			Date fromDate = (Date) context.get("fromDate");
			Date thruDate = (Date) context.get("thruDate");
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
			chart.putParameter(SalesOlapMultiImplv2.ORDER_STATUS, status);
			
			Map<String, Object> result = chart.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> evaluateSalesPPSAreaChart(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException, ParseException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			
			SalesOlapMultiImplv2 grid2 = new SalesOlapMultiImplv2(delegator);
			
			grid2.setOlapResultType(OlapColumnChart.class);
			
			Date fromDate = (Date) context.get("fromDate");
			Date thruDate = (Date) context.get("thruDate");
			List<String> productStoreId = (List<String>) context.get("productStore[]");
			List<String> categoryId = (List<String>) context.get("category[]");
			String status = (String) context.get("orderStatus");
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
				grid2.setFromDate(fromDate);
				grid2.setThruDate(thruDate);
			} else {
				grid2.setFromDate(beginDate);
				grid2.setThruDate(currentDate);
			}

	        grid2.putParameter(SalesOlapMultiImplv2.PRODUCT_STORE, productStoreId);
			grid2.putParameter(SalesOlapMultiImplv2.CATEGORY, categoryId);
			grid2.putParameter(SalesOlapMultiImplv2.ORG, organization);
			grid2.putParameter(SalesOlapMultiImplv2.ORDER_STATUS, status);
	        
			Map<String, Object> result = grid2.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> evaluateTurnoverPPSPieChart(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException, ParseException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			
			SalesOlapMultiImplv2 grid2 = new SalesOlapMultiImplv2(delegator);
			
			grid2.setOlapResultType(OlapPieChart.class);
			
			Date fromDate = (Date) context.get("fromDate");
			Date thruDate = (Date) context.get("thruDate");
			List<String> productStoreId = (List<String>) context.get("productStore[]");
			List<String> categoryId = (List<String>) context.get("category[]");
			String status = (String) context.get("orderStatus");
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
				grid2.setFromDate(fromDate);
				grid2.setThruDate(thruDate);
			} else {
				grid2.setFromDate(beginDate);
				grid2.setThruDate(currentDate);
			}

	        grid2.putParameter(SalesOlapMultiImplv2.PRODUCT_STORE, productStoreId);
			grid2.putParameter(SalesOlapMultiImplv2.CATEGORY, categoryId);
			grid2.putParameter(SalesOlapMultiImplv2.ORG, organization);
			grid2.putParameter(SalesOlapMultiImplv2.ORDER_STATUS, status);
	        
			Map<String, Object> result = grid2.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> evaluateTurnoverPPSPieChart2(DispatchContext ctx, Map<String, ? extends Object> context)
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
			
			Map<String, Object> result = chart.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> evaluateTurnoverPCPieChart(DispatchContext ctx, Map<String, ? extends Object> context)
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
	        
			Map<String, Object> result = chart.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> evaluateSalesPCColumnChart(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException, ParseException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//			String quantity = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity", (Locale)context.get("locale"));
			SalesOlapImplByChannelMultiv2 chart = new SalesOlapImplByChannelMultiv2(delegator);
			chart.setOlapResultType(OlapColumnChart.class);
			
	        Date fromDate = (Date) context.get("fromDate");
	        Date thruDate = (Date) context.get("thruDate");
	        String status = (String) context.get("orderStatus");
	        String customTime = (String) context.get("customTime");
	        List<String> storeChannelId = (List<String>) context.get("storeChannel[]");
			List<String> categoryId = (List<String>) context.get("category[]");
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
	        
			Map<String, Object> result = chart.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> evaluateSalesPCAreaChart(DispatchContext ctx, Map<String, ? extends Object> context)
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
			String status = (String) context.get("orderStatus");
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
	        
			Map<String, Object> result = chart.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> evaluateSalesTopProduct(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException, ParseException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			SalesOlapTopProductImplv2 grid = new SalesOlapTopProductImplv2(delegator);
			grid.setOlapResultType(OlapGrid.class);
			
			Date fromDate = (Date) context.get("fromDate");
			Date thruDate = (Date) context.get("thruDate");
			String topProduct = (String) context.get("topProduct");
			String statusSales = (String) context.get("statusSales");
			String filterType = (String) context.get("filterType");
			String status = (String) context.get("orderStatus");
			String storeChannelId = (String) context.get("storeChannel");
			String all = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", (Locale)context.get("locale"));
			List<String> categoryId = (List<String>) context.get("category[]");
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
			
			grid.putParameter(SalesOlapTopProductImplv2.TOP_PRODUCT, topProduct);
			grid.putParameter(SalesOlapTopProductImplv2.STATUS_SALES, statusSales);
			grid.putParameter(SalesOlapTopProductImplv2.ORDER_STATUS, status);
			grid.putParameter(SalesOlapTopProductImplv2.STORE_CHANNEL, storeChannelId);
			grid.putParameter(SalesOlapTopProductImplv2.CATEGORY, categoryId);
			grid.putParameter(SalesOlapTopProductImplv2.ORG, organization);
			grid.putParameter(SalesOlapTopProductImplv2.ALL, all);
			grid.putParameter(SalesOlapTopProductImplv2.FILTER_TYPE, filterType);
			
			Map<String, Object> result = grid.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		public static Map<String, Object> evaluateSalesGrowthChart(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException {
			Delegator delegator = ctx.getDelegator();
//			String quantityO = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity1", (Locale)context.get("locale"));
//			String quantityT = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity2", (Locale)context.get("locale"));
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			
			SalesGrowthOlapImplv2 chart = new SalesGrowthOlapImplv2();
//			chart.setOlapResultType(OlapColumnChart.class);
			SalesGrowthOlapImplv2.Test test = chart.new Test();
			SalesGrowthOlapImplv2.Test3 test3 = chart.new Test3(chart, test);
			chart.setOlapResult(test3);
			chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
			
			String productStoreId = (String) context.get("productStore");
	        String channelId = (String) context.get("channel");
	        String typeOfDate = (String) context.get("typee");
	        String typeOfFilter = (String) context.get("type2");

	        String month1 = (String) context.get("monthh");
	        String month2 = (String) context.get("monthh2");
	        String quarter1 = (String) context.get("quarterr");
	        String quarter2 = (String) context.get("quarterr2");
	        String year1 = (String) context.get("yearr");
	        String year2 = (String) context.get("yearr2");
			
	        chart.putParameter(SalesGrowthOlapImplv2.PRODUCT_STORE, productStoreId);
	        chart.putParameter(SalesGrowthOlapImplv2.CHANNEL, channelId); 
	        chart.putParameter(SalesGrowthOlapImplv2.MONTH_F, month1);
	        chart.putParameter(SalesGrowthOlapImplv2.MONTH_S, month2);
	        chart.putParameter(SalesGrowthOlapImplv2.QUARTER_F, quarter1);
	        chart.putParameter(SalesGrowthOlapImplv2.QUARTER_S, quarter2);
	        chart.putParameter(SalesGrowthOlapImplv2.YEAR_F, year1);
	        chart.putParameter(SalesGrowthOlapImplv2.YEAR_S, year2);
	        chart.putParameter(SalesGrowthOlapImplv2.TYPEE, typeOfDate);
	        chart.putParameter(SalesGrowthOlapImplv2.TYPE_FILTER, typeOfFilter);
	        chart.putParameter(SalesGrowthOlapImplv2.ORGANIZATION, organization);
			Map<String, Object> result = chart.execute();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		public static Map<String, Object> evaluateSalesPByRegionColumnChart(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException {
			Delegator delegator = ctx.getDelegator();
			String total = UtilProperties.getMessage("BaseSalesUiLabels", "BSTotalK", (Locale)context.get("locale"));
			SalesProductOlapImplByRegionv2 grid = new SalesProductOlapImplByRegionv2(delegator);
			grid.setOlapResultType(OlapColumnChart.class);
	        Date fromDate = (Date) context.get("fromDate");
	        Date thruDate = (Date) context.get("thruDate");
	        String region = (String) context.get("region");
	        String status = (String) context.get("orderStatus");
			
	        grid.setFromDate(fromDate);
	        grid.setThruDate(thruDate);
	        grid.putParameter(SalesProductOlapImplByRegionv2.REGION, region);
	        grid.putParameter(SalesProductOlapImplByRegionv2.ORDER_STATUS, status);
	        grid.putParameter(SalesProductOlapImplByRegionv2.TOTAL, total);
			Map<String, Object> result = grid.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
//		public static Map<String, Object> evaluateTurnoverSynthesisPieChart(DispatchContext ctx, Map<String, ? extends Object> context)
//				throws GenericServiceException, ParseException {
//			Delegator delegator = ctx.getDelegator();
//			GenericValue userLogin = (GenericValue) context.get("userLogin");
//			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//			SynthesisChartImpl chart = new SynthesisChartImpl();
//			
//			Date fromDate = (Date) context.get("fromDate");
//	        Date thruDate = (Date) context.get("thruDate");
//	        String filter1 = (String) context.get("filter1");
//	        String filter2 = (String) context.get("filter2");
//	        String customTime = (String) context.get("customTime");
//	        Date currentDate = new Date();
//			Calendar cal = Calendar.getInstance();
//			cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
//			cal.clear(Calendar.MINUTE);
//			cal.clear(Calendar.SECOND);
//			cal.clear(Calendar.MILLISECOND);
//			Date beginDate = null;
//			
//			int monthInput = cal.get(Calendar.MONTH);
//			int quarterInput = (monthInput / 3) + 1;
//			int yearInput = cal.get(Calendar.YEAR);
//			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//			if("dd".equals(customTime)){
//				beginDate = currentDate;
//			} else if ("ww".equals(customTime)){
//				cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
//				beginDate = cal.getTime();
//			} else if ("mm".equals(customTime)){
//				cal.set(Calendar.DAY_OF_MONTH, 1);
//				beginDate = cal.getTime();
//			} else if ("qq".equals(customTime)){
//				if(quarterInput == 1){
//					beginDate = sdf.parse("01/01/" + yearInput);
//				} else if (quarterInput == 2){
//					beginDate = sdf.parse("01/04/" + yearInput);
//				} else if (quarterInput == 3){
//					beginDate = sdf.parse("01/07/" + yearInput);
//				} else if (quarterInput == 4){
//					beginDate = sdf.parse("01/10/" + yearInput);
//				}
//			} else if("yy".equals(customTime)){
//				beginDate = sdf.parse("01/01/" + yearInput);
//			}
//	        
//			if("oo".equals(customTime)){
//				chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
//				chart.setFromDate(fromDate);
//				chart.setThruDate(thruDate);
//			} else {
//				chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
//				chart.setFromDate(beginDate);
//				chart.setThruDate(currentDate);
//			}
//	        
//			
//	        SynthesisPie query = chart.new SynthesisPie();
//	        Synthesis3Pie pieResult = chart.new Synthesis3Pie(chart, query);
//	        
//			chart.setOlapResult(pieResult);
//			chart.putParameter(SynthesisChartImpl.ORG, organization);
//			chart.putParameter(SynthesisChartImpl.FILTER1, filter1);
//			chart.putParameter(SynthesisChartImpl.FILTER2, filter2);
//	        
//			Map<String, Object> result = chart.execute(context);
//	        
//			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
//			return result;
//		}
		
		public static Map<String, Object> evaluateTurnoverSynthesisPieChart(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException, ParseException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			SynthesisChart chart = new SynthesisChart(delegator);
			chart.setOlapResultType(OlapPieChart.class);
			
			Date fromDate = (Date) context.get("fromDate");
			Date thruDate = (Date) context.get("thruDate");
			String filter1 = (String) context.get("filter1");
	        String filter2 = (String) context.get("filter2");
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
			chart.putParameter(SynthesisChartImpl.ORG, organization);
			chart.putParameter(SynthesisChartImpl.FILTER1, filter1);
			chart.putParameter(SynthesisChartImpl.FILTER2, filter2);
	        
			Map<String, Object> result = chart.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> topProductColumn(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException, ParseException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			SalesOlapTopProductImplv2 chart = new SalesOlapTopProductImplv2(delegator);
			chart.setOlapResultType(OlapColumnChart.class);
			
			Date fromDate = (Date) context.get("fromDate");
			Date thruDate = (Date) context.get("thruDate");
			String topProduct = (String) context.get("topProduct");
			String statusSales = (String) context.get("statusSales");
			String status = (String) context.get("orderStatus");
			String storeChannelId = (String) context.get("storeChannel");
			String filterType = (String) context.get("filterType");
			String all = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", (Locale)context.get("locale"));
			List<String> categoryId = (List<String>) context.get("category[]");
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
			
			chart.putParameter(SalesOlapTopProductImplv2.TOP_PRODUCT, topProduct);
			chart.putParameter(SalesOlapTopProductImplv2.STATUS_SALES, statusSales);
			chart.putParameter(SalesOlapTopProductImplv2.ORDER_STATUS, status);
			chart.putParameter(SalesOlapTopProductImplv2.STORE_CHANNEL, storeChannelId);
			chart.putParameter(SalesOlapTopProductImplv2.CATEGORY, categoryId);
			chart.putParameter(SalesOlapTopProductImplv2.ORG, organization);
			chart.putParameter(SalesOlapTopProductImplv2.ALL, all);
			chart.putParameter(SalesOlapTopProductImplv2.FILTER_TYPE, filterType);
	        
			Map<String, Object> result = chart.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> evaluateCustomerSatisfactionChart(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException, ParseException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			String quantity0 = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity", (Locale)context.get("locale"));
			CSReportChartImpl chart = new CSReportChartImpl(delegator);
			chart.setOlapResultType(OlapColumnChart.class);
			
	        Date fromDate = (Date) context.get("fromDate");
	        Date thruDate = (Date) context.get("thruDate");
	        List<String> loyaltyGroupId = (List<String>) context.get("loyaltyGroup[]");
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

			chart.putParameter(CSReportChartImpl.GROUP, loyaltyGroupId);
			chart.putParameter(CSReportChartImpl.ORG, organization);
			chart.putParameter(CSReportChartImpl.QUANTITY, quantity0);
	        
			Map<String, Object> result = chart.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		public static Map<String, Object> evaluateSaex(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException, ParseException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			String quantity0 = UtilProperties.getMessage("BaseSalesUiLabels", "BSValueTotal", (Locale)context.get("locale"));
			TopSaexChartImpl chart = new TopSaexChartImpl(delegator);
			chart.setOlapResultType(OlapColumnChart.class);
			
	        Date fromDate = (Date) context.get("fromDate");
	        Date thruDate = (Date) context.get("thruDate");
	        String status = (String) context.get("status");
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
			
			chart.putParameter(TopSaexChartImpl.STATUS, status);
			chart.putParameter(TopSaexChartImpl.ORG, organization);
			chart.putParameter(TopSaexChartImpl.VALUE, quantity0);
	        
			Map<String, Object> result = chart.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		public static Map<String, Object> evaluateLoyaltyGroupPieChart(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			CSPieChartImpl chart = new CSPieChartImpl();
			
			Date fromDate = (Date) context.get("fromDate");
	        Date thruDate = (Date) context.get("thruDate");
	        
	        chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
	        chart.setFromDate(fromDate);
	        chart.setThruDate(thruDate);
			
	        CSPieChartImpl.EvaluateLoyaltyGroup query = chart.new EvaluateLoyaltyGroup();
	        CSPieChartImpl.EvaluateLoyaltyGroup3Pie pieResult = chart.new EvaluateLoyaltyGroup3Pie(chart, query);
	        
			chart.setOlapResult(pieResult);
			chart.putParameter(SynthesisChartImpl.ORG, organization);
	        
			Map<String, Object> result = chart.execute(context);
	        
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		
		public static Map<String, Object> evaluateLoyaltyGroupValuePieChart(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			CSPieChartImpl chart = new CSPieChartImpl();
			
			Date fromDate = (Date) context.get("fromDate");
	        Date thruDate = (Date) context.get("thruDate");
	        
	        chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
	        chart.setFromDate(fromDate);
	        chart.setThruDate(thruDate);
			
	        CSPieChartImpl.EvaluateLoyaltyGroupValue query = chart.new EvaluateLoyaltyGroupValue();
	        CSPieChartImpl.EvaluateLoyaltyGroupValue3Pie pieResult = chart.new EvaluateLoyaltyGroupValue3Pie(chart, query);
	        
			chart.setOlapResult(pieResult);
			chart.putParameter(SynthesisChartImpl.ORG, organization);
	        
			Map<String, Object> result = chart.execute(context);
	        
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;
		}
		//END CHART
		
		//DASHBOARD
		public static Map<String, Object> salesOrderTotal(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException {
			
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

			Date fromDate = (Date) context.get("fromDate");
	        Date thruDate = (Date) context.get("thruDate");
	        String dateType= (String) context.get("dateType");
			
	        Boolean taxFlag = (Boolean) context.get("taxFlag");		
	        
	        String show = (String) context.get("show");
	        
	        List<?> type = (List<?>) context.get("type[]");
	        
	        Map<String, Object> map = new HashMap<String, Object>();
	        
	        for(Object s : type) {
	        	String tmp = (String) s;
	        	map.put((String) s, context.get(tmp));
	        }
	        
	        String currency = (String) context.get("currency");
	        
	        Boolean quantity = (Boolean) context.get("quantity");
	        
	        SalesTotalOlap olap = new SalesTotalOlap();
	        
			olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

			olap.setOlapResult(new OlapLineChart(olap, olap.new SalesAmountTotalChartResult()));
			
	        olap.setFromDate(fromDate);
	        olap.setThruDate(thruDate);
	        
	        olap.putParameter(TypeOlap.DATE_TYPE, dateType);
	        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.TAX_FLAG, taxFlag);
	        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.SHOW, show);
	        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.TYPE, map);
	        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.CURRENCY, currency);
	        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.OLAP_QUANTITY, quantity);
	        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.ORGAN, organization);
	        
			Map<String, Object> result = olap.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

			return result;
		}
		
		//test new version
		@SuppressWarnings("unchecked")
		public static Map<String, Object> salesSalesValue(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			SalesValueSynthesisImpl chart = new SalesValueSynthesisImpl(delegator);
			chart.setOlapResultType(OlapLineChart.class);
			Date fromDate = (Date) context.get("fromDate");
	        Date thruDate = (Date) context.get("thruDate");
	        String dateType= (String) context.get("dateType");
	        String currency = (String) context.get("currency");
	        String filter = (String) context.get("filter");
	        String filterLevel = (String) context.get("level");
	        String chartType = "SALES_VALUE";
	        List<String> store = (List<String>) context.get("store[]");
	        List<String> channel = (List<String>) context.get("channel[]");
	        List<String> branchId =(List<String>) context.get("region[]");
	        chart.setFromDate(fromDate);
	        chart.setThruDate(thruDate);
	        
	        chart.putParameter(TypeOlap.DATE_TYPE, dateType);
	        chart.putParameter(SalesValueSynthesisImpl.CHART_TYPE, chartType);
	        chart.putParameter(SalesValueSynthesisImpl.CURRENCY, currency);
	        chart.putParameter(SalesValueSynthesisImpl.ORGANIZATION, organization);
	        chart.putParameter(SalesValueSynthesisImpl.PRODUCT_STORE, store);
	        chart.putParameter(SalesValueSynthesisImpl.CHANNEL, channel);
	        chart.putParameter(SalesValueSynthesisImpl.FILTER, filter);
	        chart.putParameter(SalesValueSynthesisImpl.BRANCH, branchId);
	        chart.putParameter(SalesValueSynthesisImpl.LEVEL, filterLevel);
	        
			Map<String, Object> result = chart.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

			return result;
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> salesOrderVolume(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException {
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			SalesValueSynthesisImpl chart = new SalesValueSynthesisImpl(delegator);
			chart.setOlapResultType(OlapLineChart.class);
			Date fromDate = (Date) context.get("fromDate");
	        Date thruDate = (Date) context.get("thruDate");
	        String dateType= (String) context.get("dateType");
	        String currency = (String) context.get("currency");
	        String filter = (String) context.get("filter");
	        String filterLevel = (String) context.get("level");
	        String chartType = "ORDER_VOLUME";
	        List<String> store = (List<String>) context.get("store[]");
	        List<String> channel = (List<String>) context.get("channel[]");
	        List<String> branchId =(List<String>) context.get("region[]");
	        chart.setFromDate(fromDate);
	        chart.setThruDate(thruDate);
	        
	        chart.putParameter(TypeOlap.DATE_TYPE, dateType);
	        chart.putParameter(SalesValueSynthesisImpl.CHART_TYPE, chartType);
	        chart.putParameter(SalesValueSynthesisImpl.CURRENCY, currency);
	        chart.putParameter(SalesValueSynthesisImpl.ORGANIZATION, organization);
	        chart.putParameter(SalesValueSynthesisImpl.PRODUCT_STORE, store);
	        chart.putParameter(SalesValueSynthesisImpl.CHANNEL, channel);
	        chart.putParameter(SalesValueSynthesisImpl.FILTER, filter);
	        chart.putParameter(SalesValueSynthesisImpl.BRANCH, branchId);
	        chart.putParameter(SalesValueSynthesisImpl.LEVEL, filterLevel);
	        
			Map<String, Object> result = chart.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

			return result;
		}
		
		public static Map<String, Object> salesOrderCount(DispatchContext ctx, Map<String, ? extends Object> context)
				throws GenericServiceException {
			
			Delegator delegator = ctx.getDelegator();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

			Date fromDate = (Date) context.get("fromDate");
	        Date thruDate = (Date) context.get("thruDate");
	        
	        String dateType= (String) context.get("dateType");
			
	        List<?> type = (List<?>) context.get("type[]");
	        
	        Map<String, String> map = new HashMap<String, String>();
	        
	        if(type != null) {
	        	for(Object s : type) {
	            	String tmp = (String) s;
	            	tmp = (String) context.get(tmp);
	            	if(tmp != null) {
	            		map.put((String) s, tmp);
	            	}
	            }
	        }
	        
	        SalesOrderCountOlap olap = new SalesOrderCountOlap();
	        
			olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

			olap.setOlapResult(new OlapLineChart(olap, olap.new SalesOrderCountChartResult()));
			
	        olap.setFromDate(fromDate);
	        olap.setThruDate(thruDate);
	        
	        olap.putParameter(TypeOlap.DATE_TYPE, dateType);
	        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.ORGAN, organization);
	        olap.putParameter(com.olbius.olap.bi.sales.SalesOlap.TYPE, map);
	        
			Map<String, Object> result = olap.execute(context);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

			return result;
		}
		//END DASHBOARD
	
	public static Map<String, Object> evaluatePromoSalesByChannel(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		
		PromoSalesOlapImplByChannelv2 grid = new PromoSalesOlapImplByChannelv2();
		
		com.olbius.basesales.report.PromoSalesOlapImplByChannelv2.ResultReport test2 = grid.new ResultReport();
		
		OlapGrid olapGrid = new OlapGrid(grid, test2);
		
		grid.setOlapResult(olapGrid);
		
		Boolean orig = (Boolean) context.get("orig");		
		if(orig == null) {
			orig = false;
		}
		
		String dateType= (String) context.get("dateType");
		if(dateType == null) {
			dateType = "DAY";
		}
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		
		String storeChannelId = (String) context.get("storeChannel");
		
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		
		grid.putParameter(PromoSalesOlapImplByChannelv2.DATE_TYPE, dateType);
		
		grid.putParameter(PromoSalesOlapImplByChannelv2.STORE_CHANNEL, storeChannelId);
		
		grid.putParameter(AccountingOlapImpl.ORIG, orig);
		
		Map<String, Object> result = grid.execute();
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateTurnoverByCustomer(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String userInput = userLogin.getString("userLoginId");
		
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userInput);
		
		SalesOlapByCustomerMultiImpl grid = new SalesOlapByCustomerMultiImpl();
		
		SalesOlapByCustomerMultiImpl.ResultReport res = grid.new ResultReport();
		
		OlapGrid olapGrid = new OlapGrid(grid, res);
		
		grid.setOlapResult(olapGrid);
		
		Boolean orig = (Boolean) context.get("orig");		
		if(orig == null) {
			orig = false;
		}
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		
		List<String> storeChannelId = (List<String>) context.get("storeChannel[]");
		List<String> productStoreId = (List<String>) context.get("productStore[]");
		
//		String sortId = (String) context.get("sortId");
		
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
//		
		grid.putParameter(SalesOlapByCustomerMultiImpl.STORE_CHANNEL, storeChannelId);
		
		grid.putParameter(SalesOlapByCustomerMultiImpl.PRODUCT_STORE, productStoreId);
//		
		grid.putParameter(SalesOlapByCustomerMultiImpl.ORGANIZATION, organization);
//		
//		grid.putParameter(SalesOlapByCustomerMultiImpl.SORT, sortId);
		
		Map<String, Object> result = grid.execute((Map<String, Object>) context);
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
