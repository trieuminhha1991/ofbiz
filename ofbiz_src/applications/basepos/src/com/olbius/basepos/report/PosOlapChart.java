package com.olbius.basepos.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import java.math.*;

import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.InnerJoin;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.olap.AbstractOlap;
import com.olbius.olap.OlapInterface;

public class PosOlapChart extends AbstractOlap implements OlapInterface{
	public static String resource = "BasePosUiLabels";
	
	public void bestSellerChart(Integer limit, Boolean sort, String productStoreId, String typeChart, String org) throws GenericDataSourceException, GenericEntityException, SQLException {
		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		query.from("sales_order_fact");
		query.select("product_dimension.product_code");
		query.select("product_dimension.internal_name");
		query.select("sum(quantity)", "_quantity");
		query.select("sum(total)", "_ext_price");
		
		query.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "currency_dimension", null, "currency_dim_id = currency_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", null, "party_from_dim_id = party_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_store_dimension", null, "product_store_dim_id = product_store_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id");
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.andEQ("enumeration_dimension.enum_id", "SMCHANNEL_POS");
		condition.andEQ("sales_order_fact.order_status", "ORDER_COMPLETED");
		condition.and("sales_order_fact.return_id IS NULL");
		condition.andEQ("product_store_dimension.product_store_id", productStoreId, productStoreId != null);
		condition.andEQ("party_dimension.party_id", org, org != null);
		query.where(condition);
		
		query.groupBy("product_dimension.product_code");
		query.groupBy("product_dimension.internal_name");
		
		String orderType = "DESC";
		if (sort == true){
			orderType = "ASC";
		}
		
		if (typeChart.equals("quantity")){
			query.orderBy("_quantity", orderType);
		} else if (typeChart.equals("price")){
			query.orderBy("_ext_price", orderType);
		}
		
		query.limit(limit);
		
		ResultSet resultSet = query.getResultSet();
		while(resultSet.next()) {
			String _key = resultSet.getString("product_code");
			if(yAxis.get("test") == null) {
				yAxis.put("test", new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity = resultSet.getBigDecimal("_quantity");
			BigDecimal extPrice = resultSet.getBigDecimal("_ext_price");
			if (typeChart.equals("quantity")){
				yAxis.get("test").add(quantity);
			} else if (typeChart.equals("price")){
				yAxis.get("test").add(extPrice);
			} 
		}
	} 
	
	public void storeChart(Locale locale, String org) throws GenericDataSourceException, GenericEntityException, SQLException {
		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		query.from("sales_order_fact");
		query.select("product_store_dimension.product_store_id");
		query.select("product_store_dimension.store_name");
		query.select("sum(total)", "_ext_price");
		
		query.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "currency_dimension", null, "currency_dim_id = currency_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", null, "party_from_dim_id = party_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_store_dimension", null, "product_store_dim_id = product_store_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id");
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.andEQ("enumeration_dimension.enum_id", "SMCHANNEL_POS");
		condition.andEQ("sales_order_fact.order_status", "ORDER_COMPLETED");
		condition.and("sales_order_fact.return_id IS NULL");
		condition.andEQ("party_dimension.party_id", org, org != null);
		query.where(condition);
		
		query.groupBy("product_store_dimension.product_store_id");
		query.groupBy("product_store_dimension.store_name");
		
		query.orderBy("_ext_price");
		
		ResultSet resultSet = query.getResultSet();
		String price = UtilProperties.getMessage(resource, "BPOSExtPrice", locale);
		while(resultSet.next()) {
			String _key = resultSet.getString("product_store_id");
			if(yAxis.get(price) == null) {
				yAxis.put(price, new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal extPrice = resultSet.getBigDecimal("_ext_price");
			yAxis.get(price).add(extPrice);
		}
		
	} 
	
	public void categoryChart(Locale locale, String productStoreId, String org) throws GenericDataSourceException, GenericEntityException, SQLException {
		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		query.from("sales_order_fact", "sof");
		query.select("category_dimension.category_id");
		query.select("category_dimension.category_name");
		query.select("sum(total)", "_ext_price");
		
		query.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "currency_dimension", null, "currency_dim_id = currency_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", null, "party_from_dim_id = party_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_store_dimension", null, "product_store_dim_id = product_store_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "pcr.product_dim_id = sof.product_dim_id");
		query.join(Join.INNER_JOIN, "date_dimension", "from_date", "pcr.from_dim_date = from_date.dimension_id");
		query.join(Join.INNER_JOIN, "date_dimension", "thru_date", "pcr.thru_dim_date = thru_date.dimension_id");
		
		Join category = new InnerJoin();
		Condition condCate = new Condition();
		condCate.and("pcr.category_dim_id = category_dimension.dimension_id");
		condCate.andEQ("category_dimension.category_type", "CATALOG_CATEGORY");
		condCate.and("date_dimension.date_value >= from_date.date_value");
		condCate.and(Condition.make("thru_date.date_value IS NULL").or("thru_date.date_value >= date_dimension.date_value"));
		category.table("category_dimension").on(condCate);
		query.join(category);
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.andEQ("enumeration_dimension.enum_id", "SMCHANNEL_POS");
		condition.andEQ("sof.order_status", "ORDER_COMPLETED");
		condition.and("sof.return_id IS NULL");
		condition.andEQ("product_store_dimension.product_store_id", productStoreId, productStoreId != null);
		condition.andEQ("party_dimension.party_id", org, org != null);
		query.where(condition);
		
		query.groupBy("category_dimension.category_id");
		query.groupBy("category_dimension.category_name");
		
		query.orderBy("_ext_price");
		
		ResultSet resultSet = query.getResultSet();
		String price = UtilProperties.getMessage(resource, "BPOSExtPrice", locale);
		while(resultSet.next()) {
			String _key = resultSet.getString("category_name");
			if(yAxis.get(price) == null) {
				yAxis.put(price, new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal extPrice = resultSet.getBigDecimal("_ext_price");
			yAxis.get(price).add(extPrice);
		}
	} 
	
	public void returnChart(String facilityId, String partyId, String org, Integer limit) throws GenericDataSourceException, GenericEntityException, SQLException {
		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		query.from("return_order_fact");
		query.select("currency_dimension.currency_id");
		query.select("product_dimension.product_code");
		query.select("product_dimension.internal_name");
		query.select("sum(quantity)", "_quantity");
		query.select("sum(total)", "_total");
		
		query.join(Join.INNER_JOIN, "date_dimension", null, "return_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "currency_dimension", null, "currency_dim_id = currency_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "party", "party_dim_id = party.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "facility_dimension", "facility", "facility_dim_id = facility.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "owner_party", "facility.owner_party_dim_id = owner_party.dimension_id");
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.andEQ("facility.facility_id", facilityId, facilityId != null);
		condition.andEQ("party.party_id", partyId, partyId != null);
		condition.andEQ("owner_party.party_id", org, org != null);
		query.where(condition);
		
		query.groupBy("currency_dimension.currency_id");
		query.groupBy("product_dimension.product_code");
		query.groupBy("product_dimension.internal_name");
		
		query.orderBy("_total", "DESC");
		query.limit(limit);
		ResultSet resultSet = query.getResultSet();
		while(resultSet.next()) {
			String _key = resultSet.getString("product_code");
			if(yAxis.get("test") == null) {
				yAxis.put("test", new ArrayList<Object>());
			}
			xAxis.add(_key);
			yAxis.get("test").add(resultSet.getBigDecimal("_total"));
		}
	}
}
