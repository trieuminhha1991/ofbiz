package com.olbius.basepos.report;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import com.olbius.bi.olap.chart.OlapColumnChart;

import javolution.util.FastMap;

public class PosOlapChartServices {
	
	public static Map<String, Object> bestSellerChart(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		Integer limit = (Integer) context.get("limit");
		if(limit == null) {
			limit = 0;
		}
		Boolean sort = (Boolean) context.get("sort");		
		if(sort == null) {
			sort = false;
		}
		String productStoreId = (String) context.get("productStoreId");
		String typeChart = (String) context.get("typeChart");
		
		PosOlapChart olap = new PosOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		try {
			olap.bestSellerChart(limit, sort, productStoreId, typeChart, organization);
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
	
	public static Map<String, Object> bestSellerChartv2(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		Long limit = (Long) context.get("limit");
        if(limit == null) {
        	limit = (long) 0;
        }
        Boolean sort = (Boolean) context.get("sort");		
        if(sort == null) {
        	sort = false;
        }
        String productStoreId = (String) context.get("productStoreId");
        String typeChart = (String) context.get("typeChart");
        
		BestSeller olap = new BestSeller(delegator);
		olap.setOlapResultType(OlapColumnChart.class);
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
			olap.setFromDate(fromDate);
			olap.setThruDate(thruDate);
		} else {
			olap.setFromDate(beginDate);
			olap.setThruDate(currentDate);
		}
		olap.putParameter(BestSeller.ORG, organization);
		olap.putParameter(BestSeller.LIMITT, limit);
		olap.putParameter(BestSeller.STORE, productStoreId);
		olap.putParameter(BestSeller.TYPE_CHART, typeChart);
		olap.putParameter(BestSeller.SORTT, sort);
		
		Map<String, Object> result = olap.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> storeChart(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		
		PosOlapChart olap = new PosOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		try {
			olap.storeChart(locale, organization);
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
	
	public static Map<String, Object> categoryChart(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String productStoreId = (String) context.get("productStoreId");
		
		PosOlapChart olap = new PosOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		try {
			olap.categoryChart(locale, productStoreId, organization);
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
	
	public static Map<String, Object> categoryChartv2(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		TopCategory olap = new TopCategory(delegator);
		olap.setOlapResultType(OlapColumnChart.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String productStoreId = (String) context.get("productStoreId");
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
			olap.setFromDate(fromDate);
			olap.setThruDate(thruDate);
		} else {
			olap.setFromDate(beginDate);
			olap.setThruDate(currentDate);
		}
		olap.putParameter(TopCategory.ORG, organization);
		olap.putParameter(TopCategory.STORE, productStoreId);
		
		Map<String, Object> result = olap.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	
	public static Map<String, Object> storeChartv2(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		TopStore olap = new TopStore(delegator);
		olap.setOlapResultType(OlapColumnChart.class);
		
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
			olap.setFromDate(fromDate);
			olap.setThruDate(thruDate);
		} else {
			olap.setFromDate(beginDate);
			olap.setThruDate(currentDate);
		}
		
		olap.putParameter(TopStore.ORG, organization);
		Map<String, Object> result = olap.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	public static Map<String, Object> returnChart(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String facilityId = (String) context.get("facilityId");
		String partyId = (String) context.get("partyId");
		Integer limit = (Integer) context.get("limit");
		if(limit == null) {
			limit = 0;
		}
		
		PosOlapChart olap = new PosOlapChart();
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		try {
			olap.returnChart(facilityId, partyId, organization, limit);
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
	
	public static Map<String, Object> returnChartv2(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, ParseException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ReturnProduct chart = new ReturnProduct(delegator);
		chart.setOlapResultType(OlapColumnChart.class);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String facilityId = (String) context.get("facilityId");
		String partyId = (String) context.get("partyId");
		Long limit = (Long) context.get("limit");
		if(limit == null) {
        	limit =(long) 0;
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
		
		chart.putParameter(ReturnProduct.ORG, organization);
		chart.putParameter(ReturnProduct.PARTY_ID, partyId);
		chart.putParameter(ReturnProduct.FACILITY, facilityId);
		chart.putParameter(ReturnProduct.LIMITT, limit);
		Map<String, Object> result = chart.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
