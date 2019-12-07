package com.olbius.baselogistics.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.Query;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.olap.AbstractOlap;

public class GeneralOlapFacilityImp extends AbstractOlap implements FacilityOlapLOG{
	private void sqlProductQuery(String col, List<String> facilityId, List<String> productId, String inventoryType, String dateType, List<String> geoId, List<String> geoType) throws GenericDataSourceException, GenericEntityException, SQLException {
		
		if(dateType != null) {
			dateType = getDateType(dateType);
		}
		
		Query query = new Query(getSQLProcessor());
		
		query.setFrom("inventory_item_fact");
		
		query.addSelect("sum(" + col + ")", "total");
		
		query.addSelect("facility_id", null, facilityId != null && !facilityId.isEmpty());
		
		query.addSelect("facility_id", null, dateType == null && (productId != null && !productId.isEmpty()) && (geoType == null || geoType.isEmpty()));
		
		query.addSelect("product_code");
		
		query.addSelect("geo_id", null, geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty());
		
		query.addSelect("geo_id", null, dateType == null && (productId != null && !productId.isEmpty()) && (geoType != null && !geoType.isEmpty()));
		
		query.addSelect(dateType, null, dateType != null);
		
		query.addInnerJoin("date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id");
		
		query.addInnerJoin("product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		
		query.addInnerJoin("facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id", facilityId != null && !facilityId.isEmpty());
		
		query.addInnerJoin("facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id", dateType == null && (productId != null && !productId.isEmpty()) && (geoType == null || geoType.isEmpty()));
		
		query.addInnerJoin("facility_geo", null, "facility_dim_id = facility_geo.dimension_id", geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty());
		
		query.addInnerJoin("facility_geo", null, "facility_dim_id = facility_geo.dimension_id", dateType == null && (productId != null && !productId.isEmpty()) && (geoType != null && !geoType.isEmpty()));
		
		query.addConditionEQ("inventory_type", inventoryType);
		
		query.addConditionIn("facility_dimension.facility_id", facilityId, facilityId != null && !facilityId.isEmpty());
		
		query.addConditionIn("product_dimension.product_code", productId, productId != null && !productId.isEmpty());
		
		query.addConditionIn("facility_geo.geo_type", geoType, geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty());
		
		query.addConditionIn("facility_geo.geo_type", geoType, dateType == null && (productId != null && !productId.isEmpty()) && (geoType != null && !geoType.isEmpty()));
		
		query.addConditionIn("facility_geo.geo_id", geoId, geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty());
		
		query.addConditionBetweenObj("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		
		query.addGroupBy("product_code");
		
		query.addGroupBy(dateType, dateType != null);
		
		query.addGroupBy("facility_id", facilityId != null && !facilityId.isEmpty());
		
		query.addGroupBy("facility_id", dateType == null && (productId != null && !productId.isEmpty()) && (geoType == null || geoType.isEmpty()));
		
		query.addGroupBy("geo_id", geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty());
		
		query.addGroupBy("geo_id", dateType == null && (productId != null && !productId.isEmpty()) && (geoType != null && !geoType.isEmpty()));
		
		query.addOrderBy("product_code");
		
		query.addOrderBy(dateType, dateType != null);
		
		ResultSet resultSet = query.getResultSet();
		
		if(dateType != null) {
			Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>();
			
			while(resultSet.next()) {
				String product = resultSet.getString("product_code");
				if(map.get(product)==null) {
					map.put(product, new HashMap<String, Object>());
				}
				if(inventoryType.equals(TYPE_RECEIVE)) {
					map.get(product).put(resultSet.getString(dateType), resultSet.getInt("total"));
				}
				if(inventoryType.equals(TYPE_EXPORT)) {
					map.get(product).put(resultSet.getString(dateType), -resultSet.getInt("total"));
				}
			}
			
			axis(map, dateType);
			
		} else {
			
			String key = null;
			
			if(productId == null || productId.isEmpty()) {
				key = "product_code";
			} else if(geoType == null || geoType.isEmpty()) {
				key = "facility_id";
			} else if(geoType != null && !geoType.isEmpty()) {
				key = "geo_id";
			}
			
			while(resultSet.next()) {
				String _key = resultSet.getString(key);
				Object total = null;
				if(inventoryType.equals(TYPE_RECEIVE)) {
					total = resultSet.getInt("total");
				}
				if(inventoryType.equals(TYPE_EXPORT)) {
					total = -resultSet.getInt("total");
				}
				xAxis.add(_key);
				List<Object> tmp = new ArrayList<Object>();
				tmp.add(total);
				yAxis.put(_key, tmp);
			}
		}
		
	}
	
	private void sqlProductInventory(String col, List<String> facilityId, List<String> productId, String dateType, List<String> geoId, List<String> geoType) throws GenericDataSourceException, GenericEntityException, SQLException {
		
		if(dateType != null) {
			dateType = getDateType(dateType);
		}
		
		OlbiusQuery tmpQuery = OlbiusQuery.make();
		
		tmpQuery.distinctOn("facility_dim_id", "product_dim_id", "expire_date_dim_id").distinctOn(dateType, dateType != null).distinctOn("date_value", dateType == null)
			.select("*").from("facility_fact").join(Join.INNER_JOIN, "date_dimension", "date_dim_id = date_dimension.dimension_id")
			.where(Condition.makeBetween("date_value", getSqlDate(fromDate), getSqlDate(thruDate))).orderBy("date_value", OlbiusQuery.DESC, dateType == null)
			.orderBy(dateType, OlbiusQuery.DESC, dateType != null);
		
		OlbiusQuery olbiusQuery = OlbiusQuery.make(getSQLProcessor());
		
		boolean facility = (facilityId != null && !facilityId.isEmpty()) || (dateType == null && (productId != null && !productId.isEmpty()) && (geoType == null || geoType.isEmpty()));
		
		boolean geo = (geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty()) || (dateType == null && (productId != null && !productId.isEmpty()) && (geoType != null && !geoType.isEmpty()));
		
		olbiusQuery.select("SUM(" + col +")", "total").select("product_code").select(dateType, dateType != null).from(tmpQuery, "tmp")
			.select("facility_id", facility).select("geo_id", geo)
			.join(Join.INNER_JOIN, "product_dimension", "product_dim_id = product_dimension.dimension_id")
			.join(Join.INNER_JOIN, "facility_dimension", "product_dim_id = product_dimension.dimension_id", facility)
			.join(Join.INNER_JOIN, "facility_geo", "product_dim_id = product_dimension.dimension_id", geo)
			.where(Condition.makeEQ("facility_dimension.facility_id", facilityId, facilityId != null && !facilityId.isEmpty())
					.andEQ("product_dimension.product_code", productId, productId != null && !productId.isEmpty())
					.andEQ("facility_geo.geo_type", geoType, geo)
					.andEQ("facility_geo.geo_id", geoId, geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty()))
			.groupBy("product_code").groupBy(dateType, dateType != null).groupBy("date_value", dateType == null)
			.groupBy("facility_id", facility).groupBy("geo_id", geo).orderBy(dateType, dateType != null).orderBy("product_code");
		
		ResultSet resultSet = olbiusQuery.getResultSet();
		if(dateType != null) {
			Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>();
			
			while(resultSet.next()) {
				String product = resultSet.getString("product_code");
				if(map.get(product)==null) {
					map.put(product, new HashMap<String, Object>());
				}
				map.get(product).put(resultSet.getString(dateType), resultSet.getInt("total"));
			}
			
			axis(map, dateType);
			
		} else {
			String key = null;
			
			if(productId == null || productId.isEmpty()) {
				key = "product_code";
			} else if(geoType == null || geoType.isEmpty()) {
				key = "facility_id";
			} else if(geoType != null && !geoType.isEmpty()) {
				key = "geo_id";
			}
			
			while(resultSet.next()) {
				String _key = resultSet.getString(key);
				Object total = resultSet.getInt("total");
				xAxis.add(_key);
				List<Object> tmp = new ArrayList<Object>();
				tmp.add(total);
				yAxis.put(_key, tmp);
			}
		}
		
	}
	

	@Override
	public void productReceiveQOH(List<String> facilityId,
			List<String> productId, String dateType, List<String> geoId,
			List<String> geoType) throws GenericDataSourceException,
			GenericEntityException, SQLException {
		sqlProductQuery("quantity_on_hand_total", facilityId, productId, TYPE_RECEIVE, dateType, geoId, geoType);
	}

	@Override
	public void productExportQOH(List<String> facilityId,
			List<String> productId, String dateType, List<String> geoId,
			List<String> geoType) throws GenericDataSourceException,
			GenericEntityException, SQLException {
		sqlProductQuery("quantity_on_hand_total", facilityId, productId, TYPE_EXPORT, dateType, geoId, geoType);
	}

	@Override
	public void productInventoryQOH(List<String> facilityId,
			List<String> productId, String dateType, List<String> geoId,
			List<String> geoType) throws GenericDataSourceException,
			GenericEntityException, SQLException {
		sqlProductInventory("inventory_total", facilityId, productId, dateType, geoId, geoType);
	}

	@Override
	public void productBookATP(List<String> facilityId, List<String> productId,
			String dateType, List<String> geoId, List<String> geoType)
			throws GenericDataSourceException, GenericEntityException,
			SQLException {
		sqlProductQuery("available_to_promise_total", facilityId, productId, TYPE_EXPORT, dateType, geoId, geoType);
	}

	@Override
	public void productInventoryATP(List<String> facilityId,
			List<String> productId, String dateType, List<String> geoId,
			List<String> geoType) throws GenericDataSourceException,
			GenericEntityException, SQLException {
		sqlProductInventory("available_to_promise_total", facilityId, productId, dateType, geoId, geoType);
		
	}
}
