package com.olbius.basepo.report;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

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
import com.olbius.bi.olap.chart.OlapPieChart;

public class POOlapChartServices {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> purchaseOrderChart(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		List<Object> productId = (List<Object>) context.get("productId[]");
		List<Object> statusId = (List<Object>) context.get("statusId[]");
		List<Object> categoryId = (List<Object>) context.get("categoryId[]");
		String limitId = (String) context.get("limitId");
		String filterTypeId = (String) context.get("filterTypeId");
		POOlapChart olap = new POOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		try {
			olap.purchaseOrderChart(productId, statusId, limitId, filterTypeId, categoryId, ownerPartyId);
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
	public static Map<String, Object> purchaseOrderChartLineOlap(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String dateType = (String) context.get("dateType");
		List<Object> productId = (List<Object>) context.get("productId[]");
		List<Object> statusId = (List<Object>) context.get("statusId[]");
		List<Object> categoryId = (List<Object>) context.get("categoryId[]");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Integer filterTop = (Integer) context.get("filterTop");
		String filterSort = (String) context.get("filterSort");
		
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		POOlapChart olap = new POOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		try {
			olap.purchaseOrderChartLine(dateType, productId, statusId, ownerPartyId, categoryId, filterTop, filterSort);
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

	public static Map<String, Object> purchaseOrderReportPieChart(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String filterTypeId = (String) context.get("filterTypeId");
		String categoryId = (String) context.get("categoryId");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator,
				userLogin.getString("userLoginId"));
		PurchaseOrderPieImpl chart = new PurchaseOrderPieImpl();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		chart.setFromDate(fromDate);
		chart.setThruDate(thruDate);
		OlapPieChart resultPie = new OlapPieChart(chart, chart.new ResultOutReport());
		chart.setOlapResult(resultPie);
		chart.putParameter(PurchaseOrderPieImpl.USER_LOGIN_ID, organization);
		chart.putParameter(PurchaseOrderPieImpl.LOCALE, locale);
		chart.putParameter(PurchaseOrderPieImpl.FILTER_TYPE_ID, filterTypeId);
		chart.putParameter(PurchaseOrderPieImpl.CATEGORY_ID, categoryId);
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> returnProductCharOlapPO(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<Object> productId = (List<Object>) context.get("productId[]");
		List<Object> facilityId = (List<Object>) context.get("facilityId[]");
		List<Object> categoryId = (List<Object>) context.get("categoryId[]");
		List<Object> returnReasonId = (List<Object>) context.get("returnReasonId[]");
		String limitId = (String) context.get("limitId");
		String filterTypeId = (String) context.get("filterTypeId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		POOlapChart olap = new POOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		try {
			olap.returnProductChartPO(productId, facilityId, ownerPartyId, categoryId, returnReasonId, limitId,
					filterTypeId);
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

	public static Map<String, Object> returnProductReportPOPieChart(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String filterTypeId = (String) context.get("filterTypeId");
		String categoryId = (String) context.get("categoryId");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator,
				userLogin.getString("userLoginId"));
		ReturnProductPOPieImpl chart = new ReturnProductPOPieImpl();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		chart.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		chart.setFromDate(fromDate);
		chart.setThruDate(thruDate);
		OlapPieChart resultPie = new OlapPieChart(chart, chart.new ResultOutReport());
		chart.setOlapResult(resultPie);
		chart.putParameter(ReturnProductPOPieImpl.USER_LOGIN_ID, organization);
		chart.putParameter(ReturnProductPOPieImpl.LOCALE, locale);
		chart.putParameter(ReturnProductPOPieImpl.FILTER_TYPE_ID, filterTypeId);
		chart.putParameter(ReturnProductPOPieImpl.CATEGORY_ID, categoryId);
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
