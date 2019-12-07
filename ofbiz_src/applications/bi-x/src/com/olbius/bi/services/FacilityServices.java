package com.olbius.bi.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.olap.facility.FacilityOlap;

public class FacilityServices {
	
	public final static String module = FacilityServices.class.getName();
	
	public static Map<String, Object> facilityProduct(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {

		Delegator delegator = ctx.getDelegator();

		Date fromDate = (Date) context.get("fromDate");
		
		Date thruDate = (Date) context.get("thruDate");
		
		String facilityId = (String) context.get("facilityId");
		
		String productId = (String) context.get("productId");
		
		String olapType = (String) context.get("olapType");
		
		String dateType = (String) context.get("dateType");
		
		String geoId = (String) context.get("geoId");
		
		String geoType = (String) context.get("geoType");
		
		FacilityOlap olap = OlapServiceFactory.FACILITY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		
		olap.setThruDate(thruDate);
		
		
		try {
			
			if(olapType.equals(FacilityOlap.TYPE_RECEIVE)) {
				olap.productReceiveQOH(facilityId, productId, dateType, geoId, geoType);
			}
			if(olapType.equals(FacilityOlap.TYPE_EXPORT)) {
				olap.productExportQOH(facilityId, productId, dateType, geoId, geoType);
			}
			if(olapType.equals(FacilityOlap.TYPE_INVENTORY)) {
				olap.productInventoryQOH(facilityId, productId, dateType, geoId, geoType);
			}
			if(olapType.equals(FacilityOlap.TYPE_BOOK)) {
				olap.productBookATP(facilityId, productId, dateType, geoId, geoType);
			}
			if(olapType.equals(FacilityOlap.TYPE_AVAILABLE)) {
				olap.productInventoryATP(facilityId, productId, dateType, geoId, geoType);
			}
			
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
	
	public static Map<String, Object> facilityProductDelivery(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {

		Delegator delegator = ctx.getDelegator();

		Date fromDate = (Date) context.get("fromDate");
		
		Date thruDate = (Date) context.get("thruDate");
		
		String productId = (String) context.get("productId");
		
		String geoId = (String) context.get("geoId");
		
		String geoType = (String) context.get("geoType");
		
		Boolean facilityFlag = (Boolean) context.get("facilityFlag");
		
		if(facilityFlag == null) {
			facilityFlag = false;
		}
		
		FacilityOlap olap = OlapServiceFactory.FACILITY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		
		olap.setThruDate(thruDate);
		
		
		try {
			olap.productDelivery(productId, geoId, geoType, facilityFlag);
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
	
	public static Map<String, Object> getFacilityId(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();

		List<GenericValue> values = null;

		try {
			values = delegator.findList("FacilityDimension", null, null, UtilMisc.toList("facilityId"), null, false);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		}
		
		List<String> facilityId = new ArrayList<String>();
		
		for (GenericValue value : values) {
			facilityId.add(value.getString("facilityId"));
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("facility", facilityId);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> getGeoType(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();

		List<GenericValue> values = null;

		try {
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			values = delegator.findList("FacilityGeo", null, UtilMisc.toSet("geoType"), UtilMisc.toList("geoType"), options, false);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		}
		
		List<String> geoType = new ArrayList<String>();
		
		for (GenericValue value : values) {
			geoType.add(value.getString("geoType"));
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("geoType", geoType);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> getGeoId(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();

		String geoType = (String) context.get("geoType");
		
		List<GenericValue> values = null;

		try {
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			values = delegator.findList("FacilityGeo", EntityCondition.makeCondition("geoType", EntityOperator.EQUALS, geoType), UtilMisc.toSet("geoId"), UtilMisc.toList("geoId"), options, false);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		}
		
		List<String> geoId = new ArrayList<String>();
		
		for (GenericValue value : values) {
			geoId.add(value.getString("geoId"));
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("geo", geoId);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
