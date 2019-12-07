package com.olbius.baselogistics.report;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.chart.OlapPieChart;

import javolution.util.FastMap;

public class LogOlapChartServices {
	public static Map<String, Object> generalImportReciveChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {

		Delegator delegator = ctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String facilityId = (String) context.get("facilityId");
		String productId = (String) context.get("productId");
		String dateType= (String) context.get("dateType");
		Locale locale = (Locale) context.get("locale");
		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		LOGOlapChart olap = new LOGOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		
		olap.setThruDate(thruDate);
		try {
			olap.productReceiveQOH(dateType, facilityId, productId, ownerPartyId, locale);
			
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> reportReceiveWarehouseChartLineOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String dateType= (String) context.get("dateType");
		List<Object> facilityId = (List<Object>) context.get("facilityId[]");
		List<Object> categoryId = (List<Object>) context.get("categoryId[]"); 
		
		Integer filterTop = (Integer) context.get("filterTop");
		String filterSort = (String) context.get("filterSort");
		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		ReceiveChartImpl olapChart = new ReceiveChartImpl(delegator);
		olapChart.setOlapResultType(OlapLineChart.class);
		olapChart.setFromDate(fromDate);
		olapChart.setThruDate(thruDate);
		olapChart.putParameter(ReceiveChartImpl.CATEGORY_ID, categoryId);
		olapChart.putParameter(ReceiveChartImpl.FILTER_SORT, filterSort);
		olapChart.putParameter(ReceiveChartImpl.FILTER_TOP, filterTop);
		olapChart.putParameter(ReceiveChartImpl.FACILITY_ID, facilityId);
		olapChart.putParameter(ReceiveChartImpl.OWNER_PARTY_ID, ownerPartyId);
		olapChart.putParameter(ReceiveChartImpl.DATE_TYPE, dateType);
		
		Map<String, Object> result = olapChart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> logReportInventoryChartLine(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String dateType= (String) context.get("dateType");
		List<Object> productId= (List<Object>) context.get("productId[]");
		List<Object> facilityId = (List<Object>) context.get("facilityId[]");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		InventoryChartImpl olapChart = new InventoryChartImpl(delegator);
		olapChart.setOlapResultType(OlapLineChart.class);
		olapChart.setFromDate(fromDate);
		olapChart.setThruDate(thruDate);
		olapChart.putParameter(InventoryChartImpl.DATE_TYPE, dateType);
		olapChart.putParameter(InventoryChartImpl.PRODUCT_ID, productId);
		olapChart.putParameter(InventoryChartImpl.FACILITY_ID, facilityId);
		olapChart.putParameter(InventoryChartImpl.OWNER_PARTY_ID, ownerPartyId);
		
		Map<String, Object> result = olapChart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> reportExportWarehouseChartLineOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String dateType = (String) context.get("dateType");
		List<Object> productId = (List<Object>) context.get("productId[]");
		List<Object> facilityId = (List<Object>) context.get("facilityId[]");
		/*List<Object> enumId = (List<Object>) context.get("enumId[]");*/
		List<Object> categoryId = (List<Object>) context.get("categoryId[]");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		
		ExportChartImpl olapChart = new ExportChartImpl(delegator);
		olapChart.setOlapResultType(OlapLineChart.class);
		olapChart.setFromDate(fromDate);
		olapChart.setThruDate(thruDate);
		olapChart.putParameter(ExportChartImpl.CATEGORY_ID, categoryId);
		olapChart.putParameter(ExportChartImpl.PRODUCT_ID, productId);
		olapChart.putParameter(ExportChartImpl.FACILITY_ID, facilityId);
		olapChart.putParameter(ExportChartImpl.OWNER_PARTY_ID, ownerPartyId);
		olapChart.putParameter(ExportChartImpl.DATE_TYPE, dateType);
		
		
		
		/*LOGOlapChart olap = new LOGOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);*/
		/*try {
			olap.reportExportWarehouseChartLine(dateType, productId, facilityId, ownerPartyId, categoryId);
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}*/
		/*Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());*/
		
		
		
		Map<String, Object> result = olapChart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> receiveWarehouseChartOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		
        List<Object> productId = (List<Object>) context.get("productId[]");
        List<Object> facilityId = (List<Object>) context.get("facilityId[]");
        List<Object> enumId = (List<Object>) context.get("enumId[]");
        List<Object> categoryId = (List<Object>) context.get("categoryId[]");
        
        String limitId = (String) context.get("limitId");
        String olapType = (String) context.get("olapType");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		LOGOlapChart olap = new LOGOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		try {  
			if(olapType.equals("EXPORT")){
				String filterTypeId = (String) context.get("filterTypeIdExport");
				olap.exportWarehouseChart(productId, facilityId, ownerPartyId, categoryId, enumId, limitId, filterTypeId);
			}
			if(olapType.equals("RECEIVE")){
				String filterTypeId = (String) context.get("filterTypeIdReceive");
				olap.receiveWarehouseChart(productId, facilityId, ownerPartyId, categoryId, limitId, filterTypeId);
			}
			if(olapType.equals("INVENTORY")){
				String filterTypeId = (String) context.get("filterTypeIdInventory");
				olap.inventoryWarehouseChart(productId, facilityId, limitId, filterTypeId, ownerPartyId);
			}
			/*if(olapType.equals("TYPE_BOOK")){
				String filterTypeId = (String) context.get("filterTypeIdBook");
				olap.inventoryBookChart(productId, facilityId, limitId, filterTypeId, ownerPartyId);
			}*/
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> returnProductCharOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		
        List<Object> productId = (List<Object>) context.get("productId[]");
        List<Object> facilityId = (List<Object>) context.get("facilityId[]");
        List<Object> enumId = (List<Object>) context.get("enumId[]");
        List<Object> categoryId = (List<Object>) context.get("categoryId[]");
        List<Object> returnReasonId = (List<Object>) context.get("returnReasonId[]");
        
        String limitId = (String) context.get("limitId");
        String filterTypeId = (String) context.get("filterTypeId");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		LOGOlapChart olap = new LOGOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		
		try {
			olap.returnProductChart(productId, facilityId, ownerPartyId, categoryId, enumId, returnReasonId, limitId, filterTypeId);
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> returnProductReportPieChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String filterTypeId = (String) context.get("filterTypeId");
		String checkNPP = (String) context.get("checkNPP");
		
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		ReturnProductPieImpl chart = new ReturnProductPieImpl();
		
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        
        chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

        chart.setFromDate(fromDate);
        chart.setThruDate(thruDate);
		
        OlapPieChart resultPie = new OlapPieChart(chart, chart.new ResultOutReport());
        
        chart.setOlapResult(resultPie);
        
		chart.putParameter(ReturnProductPieImpl.USER_LOGIN_ID, organization);
		chart.putParameter(ReturnProductPieImpl.LOCALE, locale);
		chart.putParameter(ReturnProductPieImpl.FILTER_TYPE_ID, filterTypeId);
		chart.putParameter(ReturnProductPieImpl.CHECK_NPP, checkNPP);
		Map<String, Object> result = chart.execute(context);
        
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> exportWarehouseReportPieChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String filterTypeId = (String) context.get("filterTypeId");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		ExportWarehouseReportPieImpl chart = new ExportWarehouseReportPieImpl();
		
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        
        chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

        chart.setFromDate(fromDate);
        chart.setThruDate(thruDate);
		
        OlapPieChart resultPie = new OlapPieChart(chart, chart.new ResultOutReport());
        
        chart.setOlapResult(resultPie);
        
		chart.putParameter(ExportWarehouseReportPieImpl.USER_LOGIN_ID, organization);
		chart.putParameter(ExportWarehouseReportPieImpl.LOCALE, locale);
		chart.putParameter(ExportWarehouseReportPieImpl.FILTER_TYPE_ID, filterTypeId);
		Map<String, Object> result = chart.execute(context);
        
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> receiveWarehouseReportPieChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String filterTypeId = (String) context.get("filterTypeId");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		ReceiveWarehouseReportPieImpl chart = new ReceiveWarehouseReportPieImpl();
		
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        
        chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

        chart.setFromDate(fromDate);
        chart.setThruDate(thruDate);
		
        OlapPieChart resultPie = new OlapPieChart(chart, chart.new ResultOutReport());
        
        chart.setOlapResult(resultPie);
        
		chart.putParameter(ReceiveWarehouseReportPieImpl.USER_LOGIN_ID, organization);
		chart.putParameter(ReceiveWarehouseReportPieImpl.LOCALE, locale);
		chart.putParameter(ReceiveWarehouseReportPieImpl.FILTER_TYPE_ID, filterTypeId);
		Map<String, Object> result = chart.execute(context);
        
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	/*public static Map<String, Object> receiveProductWarehouseReportPieChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		ReceiveProductWarehouseReportPieImpl chart = new ReceiveProductWarehouseReportPieImpl();
		
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        String filterTypeId = (String) context.get("filterTypeId");
        
        chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

        chart.setFromDate(fromDate);
        chart.setThruDate(thruDate);
		
        OlapPieChart resultPie = new OlapPieChart(chart, chart.new ResultOutReport());
        
        chart.setOlapResult(resultPie);
        
		chart.putParameter(ReceiveProductWarehouseReportPieImpl.USER_LOGIN_ID, organization);
		chart.putParameter(ReceiveProductWarehouseReportPieImpl.LOCALE, locale);
		chart.putParameter(ReceiveProductWarehouseReportPieImpl.FILTER_TYPE_ID, filterTypeId);
		Map<String, Object> result = chart.execute(context);
        
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}*/
	
	
	public static Map<String, Object> facilityReportPieOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String olapType = (String) context.get("olapType");
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		GeneralPieChartReportLOG chart = new GeneralPieChartReportLOG();
        chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
        chart.setFromDate(fromDate);
        chart.setThruDate(thruDate);
        
        OlapPieChart resultPie = new OlapPieChart(chart, chart.new ResultOutReport());
        if(olapType.equals("RECEIVE") || olapType.equals("EXPORT")) { 
        	chart.setOlapResult(resultPie);
        }
        if(olapType.equals("INVENTORY")) { 
        	chart.setOlapResult(resultPie);
        }
		chart.putParameter(GeneralPieChartReportLOG.USER_LOGIN_ID, organization);
		chart.putParameter(GeneralPieChartReportLOG.FILTER_TYPE_ID, olapType);
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> inventoryNotityChartOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		Date fromDate = new Date();
		Date thruDate = new Date();
		
        List<Object> productId = (List<Object>) context.get("productId[]");
        List<Object> facilityId = (List<Object>) context.get("facilityId[]");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		LOGOlapChart olap = new LOGOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		
		try {
			olap.inventoryNotityChartOlap(productId, facilityId, ownerPartyId);
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> olapChartPieProportionSalesReturn(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		ChartPieProportionSalesReturn chart = new ChartPieProportionSalesReturn();
        chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
        chart.setFromDate(fromDate);
        chart.setThruDate(thruDate);
        
        chart.putParameter("ownerPartyId", company);
		
        
        OlapPieChart resultPie = new OlapPieChart(chart, chart.new ResultOutReport());
        chart.setOlapResult(resultPie);
        
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> olapChartPieProportionPurchaseReturn(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		ChartPieProportionPurchaseReturn chart = new ChartPieProportionPurchaseReturn();
		chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		chart.setFromDate(fromDate);
		chart.setThruDate(thruDate);
		
		chart.putParameter("ownerPartyId", company);
		
		
		OlapPieChart resultPie = new OlapPieChart(chart, chart.new ResultOutReport());
		chart.setOlapResult(resultPie);
		
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> olapChartPieProportionSalesReturnReason(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		ChartPieProportionSalesReturnReason chart = new ChartPieProportionSalesReturnReason(); 
		chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		chart.setFromDate(fromDate);
		chart.setThruDate(thruDate);
		
		chart.putParameter("ownerPartyId", company);
		
		
		OlapPieChart resultPie = new OlapPieChart(chart, chart.new ResultOutReport());
		chart.setOlapResult(resultPie);
		
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> olapChartPieProportionPurchaseReturnReason(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		ChartPieProportionPurchaseReturnReason chart = new ChartPieProportionPurchaseReturnReason();
		chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		chart.setFromDate(fromDate);
		chart.setThruDate(thruDate);
		
		chart.putParameter("ownerPartyId", company);
		
		
		OlapPieChart resultPie = new OlapPieChart(chart, chart.new ResultOutReport());
		chart.setOlapResult(resultPie);
		
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
