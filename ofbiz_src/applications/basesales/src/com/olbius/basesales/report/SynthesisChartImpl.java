package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SynthesisChartImpl extends AbstractOlap {

	public static final String FILTER1 = "FILTER1";
	public static final String FILTER2 = "FILTER2";
	public static final String ORG = "ORG";

	private OlbiusQuery query2;
	
	private void initQuery() {
		
		String organization = (String) getParameter(ORG);
		String filter1 = (String) getParameter(FILTER1);
		String filter2 = (String) getParameter(FILTER2);
//		String channelId = (String) getParameter(CHANNEL);
		
		query2 = OlbiusQuery.make(getSQLProcessor());
		
		if(UtilValidate.areEqual(filter1, "channel")){
			if(UtilValidate.areEqual(filter2, "ordervolume")){
				query2.from("sales_order_fact")
				.select("count(DISTINCT order_id)", "volume")
				.select("enumeration_dimension.enum_id", "channelQ")
				.join(Join.INNER_JOIN, "party_dimension", "sales_order_fact.party_from_dim_id = party_dimension.dimension_id")
				.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_order_fact.sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
				.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "sales_order_fact.discount_dim_id = product_promo_dimension.dimension_id")
				.where(Condition.makeEQ("party_dimension.party_id", organization).andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")).and("product_promo_dimension.product_promo_id IS NULL"))
				.groupBy("channelQ");
			} else {
				query2.from("sales_order_fact")
				.select("enumeration_dimension.description", "channelQ")
				.select("sum(quantity)", "volume")
				.select("sum(total)", "value")
				.join(Join.INNER_JOIN, "party_dimension", "sales_order_fact.party_from_dim_id = party_dimension.dimension_id")
				.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_order_fact.sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
				.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "sales_order_fact.discount_dim_id = product_promo_dimension.dimension_id")
				.where(Condition.makeEQ("party_dimension.party_id", organization).andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")).and("product_promo_dimension.product_promo_id IS NULL"))
				.groupBy("channelQ");
			}
		} else {
			if(UtilValidate.areEqual(filter2, "ordervolume")){
				query2.from("sales_order_fact")
				.select("count(DISTINCT order_id)", "volume")
				.select("product_store_dimension.product_store_id", "store_id")
				.join(Join.INNER_JOIN, "party_dimension", "sales_order_fact.party_from_dim_id = party_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_store_dimension", "sales_order_fact.product_store_dim_id = product_store_dimension.dimension_id")
				.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "sales_order_fact.discount_dim_id = product_promo_dimension.dimension_id")
				.where(Condition.makeEQ("party_dimension.party_id", organization).andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")).and("product_promo_dimension.product_promo_id IS NULL"))
				.groupBy("store_id");
			} else {
				query2.from("sales_order_fact")
				.select("product_store_dimension.product_store_id", "store_id")
				.select("sum(quantity)", "volume")
				.select("sum(total)", "value")
				.join(Join.INNER_JOIN, "product_store_dimension", "sales_order_fact.product_store_dim_id = product_store_dimension.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "sales_order_fact.party_from_dim_id = party_dimension.dimension_id")
				.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "sales_order_fact.discount_dim_id = product_promo_dimension.dimension_id")
				.where(Condition.makeEQ("party_dimension.party_id", organization).andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")).and("product_promo_dimension.product_promo_id IS NULL"))
				.groupBy("store_id");
			}
		}
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query2 == null) {
			initQuery();
		}
		return query2;
	}
	
	public class SynthesisPie implements OlapResultQueryInterface {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		@Override
		public Object resultQuery(OlapQuery query2) {
			Map<String, Object> map = new HashMap<String, Object>();
			String filter1 = (String) getParameter(FILTER1);
			String filter2 = (String) getParameter(FILTER2);
			
			try {
				ResultSet resultSet = query2.getResultSet();
				while(resultSet.next()) {
					//channel - volume/value
					if(("channel".equals(filter1) && "salesvolume".equals(filter2)) || ("channel".equals(filter1) && "salesvalue".equals(filter2))){
						String channelOut = resultSet.getString("channelQ");
						BigDecimal volume = (BigDecimal) resultSet.getBigDecimal("volume");
						BigDecimal value = (BigDecimal) resultSet.getBigDecimal("value");
						if(map.get(channelOut)==null) {
							map.put(channelOut, new HashMap<String, Object>());
						}
						if(UtilValidate.areEqual(filter2, "salesvolume")){
							map.put(channelOut, volume);
						} else if(UtilValidate.areEqual(filter2, "salesvalue")){
							map.put(channelOut, value);
						}
					//store - volume/value
					} else if(("productstore".equals(filter1) && "salesvolume".equals(filter2)) || ("productstore".equals(filter1) && "salesvalue".equals(filter2))){
						String storeOut = resultSet.getString("store_id");
						BigDecimal volume = (BigDecimal) resultSet.getBigDecimal("volume");
						BigDecimal value = (BigDecimal) resultSet.getBigDecimal("value");
						GenericValue storeResult = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", storeOut), false);
						String storeName = storeResult.getString("storeName");
						if(map.get(storeName)==null) {
							map.put(storeName, new HashMap<String, Object>());
						}
						if(UtilValidate.areEqual(filter2, "salesvolume")){
							map.put(storeName, volume);
						} else {
							map.put(storeName, value);
						}
					} else if("productstore".equals(filter1) && "ordervolume".equals(filter2)){
						String storeOut = resultSet.getString("store_id");
						BigDecimal volume = resultSet.getBigDecimal("volume");
						GenericValue storeResult = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", storeOut), false);
						String storeName = storeResult.getString("storeName");
						if(map.get(storeName)==null) {
							map.put(storeName, new HashMap<String, Object>());
						}
						map.put(storeName, volume);
					} else if("channel".equals(filter1) && "ordervolume".equals(filter2)){
						String channelOut = resultSet.getString("channelQ");
						BigDecimal volume = resultSet.getBigDecimal("volume");
						GenericValue channelResult = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", channelOut), false);
						String channelName = channelResult.getString("description");
						if(map.get(channelName)==null) {
							map.put(channelName, new HashMap<String, Object>());
						}
						map.put(channelName, volume);
					}
				}
			} catch (GenericEntityException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return map;
		}
	}
	
	public class Synthesis3Pie extends AbstractOlapChart {

		public Synthesis3Pie(OlapInterface olap, OlapResultQueryInterface query2) {
			super(olap, query2);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@Override
		protected void result(Object object) {
			Map<String, Object> tmp = ( Map<String, Object>) object;
			for(String s : tmp.keySet()) {
				List<Object> list = new ArrayList<Object>();
				list.add(tmp.get(s));
				yAxis.put(s, list);
				xAxis.add(s);
			}
		}
	}
	
	public class ResultReport extends ReturnResultGrid {

		public ResultReport() {
			addDataField("channel");
			addDataField("volume");
		}
		
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
//			String productStoreId = (String) getParameter(PRODUCT_STORE);
//			String all = (String) getParameter(ALL);
//			try {
//				if(productStoreId != null){
//					map.put("productStoreName", result.getString("store_name"));
//				} else {
//					map.put("productStoreName", all);
//				}
//				map.put("productName", result.getString("product_name"));
//				map.put("quantity1", result.getBigDecimal("quantity1"));
//				map.put("total1", result.getBigDecimal("total1"));
//			} catch (Exception e) {
//				Debug.logError(e.getMessage(), ResultReport.class.getName());
//			}
			return map;
		}
	}
}
