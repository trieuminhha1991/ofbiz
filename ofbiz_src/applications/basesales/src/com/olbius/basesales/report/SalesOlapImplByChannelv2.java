package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
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

public class SalesOlapImplByChannelv2 extends AbstractOlap {
	public static final String STORE_CHANNEL = "STORE_CHANNEL";
	public static final String ORG = "ORG";
	public static final String QUANTITY = "QUANTITY";
	public static final String TOTAL = "TOTAL";
	public static final String ORDER_STATUS = "ORDER_STATUS";

	private OlbiusQuery query2;
	
	private void initQuery() {
		
		String storeChannelId = (String) getParameter(STORE_CHANNEL);
		String organ = (String) getParameter(ORG);
		String orderStatus = (String) getParameter(ORDER_STATUS);

		query2 = OlbiusQuery.make(getSQLProcessor());
		
		Condition condition = new Condition();
		
		query2.from("product_dimension")
		.select("party_dimension.party_id")
		.select("product_dimension.product_code")
		.select("product_dimension.product_name")
		.select("enumeration_dimension.description", "channelQ")
		.select("sum(sales_order_fact.quantity)", "Quantity")
		.select("sum(sales_order_fact.total)", "Total")
		.join(Join.INNER_JOIN, "sales_order_fact", "product_dimension.dimension_id = sales_order_fact.product_dim_id")
		.join(Join.INNER_JOIN,"date_dimension","date_dimension.dimension_id = sales_order_fact.order_date_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.party_from_dim_id = party_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sales_order_fact.promo_dim_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_order_fact.sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
		.where(condition)
		.groupBy("product_dimension.dimension_id")
		.groupBy("channelQ")
		.groupBy("party_dimension.dimension_id");
		
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if(storeChannelId != null){
			condition.and(Condition.makeEQ("enumeration_dimension.enum_id", storeChannelId, storeChannelId != null));
		}
		condition.and(Condition.makeEQ("party_dimension.party_id", organ));
		condition.and(Condition.makeEQ("sales_order_fact.order_status", orderStatus));
		if("ORDER_CANCELLED".equals(orderStatus)){
			condition.and(Condition.makeEQ("sales_order_fact.order_status", orderStatus, orderStatus != null));
			condition.and(Condition.make("sales_order_fact.order_item_status = 'ITEM_CANCELLED'"));
		} else {
			condition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
		}
		condition.and(Condition.make("product_promo_dimension.product_promo_id IS NULL"));
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query2 == null) {
			initQuery();
		}
		return query2;
	}
	
	public class PCColumn implements OlapResultQueryInterface {
		@Override
		public Object resultQuery(OlapQuery query2) {
			Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String, Object>>();
			String quantityO = (String) getParameter(QUANTITY);
			tmp.put(quantityO, new HashMap<String, Object>());
			try{
				ResultSet resultSet = query2.getResultSet();
				while (resultSet.next()) {
					int zero = 0;
					try {
						BigDecimal quantityR = (BigDecimal) resultSet.getBigDecimal("Quantity");
						if(UtilValidate.isNotEmpty(quantityR)){
							tmp.get(quantityO).put(resultSet.getString("product_code"),quantityR);
						}else{
							tmp.get(quantityO).put(resultSet.getString("product_code"), zero);
						}
					} catch (Exception e) {
						Debug.logError(e.getMessage(), PCColumn.class.getName());
					}
				}
			} catch(Exception e) {
			
			}
			return tmp;
		}
	}

	public class PC3Column extends AbstractOlapChart {
		public PC3Column(OlapInterface olap, OlapResultQueryInterface query2) {
			super(olap, query2);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;
			for(String key : map.keySet()) {
				if(yAxis.get(key) == null) {
					yAxis.put(key, new ArrayList<Object>());
				}
				for(String s : map.get(key).keySet()) {
					yAxis.get(key).add(map.get(key).get(s));
					if(!xAxis.contains(s)) {
						xAxis.add(s);
					}
				}
			}
		}
	}
	
	public class PCArea implements OlapResultQueryInterface {
		@Override
		public Object resultQuery(OlapQuery query2) {
			Map<String, Map<String, Object>> tmp2 = new HashMap<String, Map<String, Object>>();
			String TotalO = (String) getParameter(TOTAL);
			tmp2.put(TotalO, new HashMap<String, Object>());
			try{
				ResultSet resultSet2 = query2.getResultSet();
				while (resultSet2.next()) {
					int zero = 0;
					try {
						BigDecimal totalR = (BigDecimal) resultSet2.getBigDecimal("Total");
						if(UtilValidate.isNotEmpty(totalR)){
							tmp2.get(TotalO).put(resultSet2.getString("product_code"), totalR);
						}else{
							tmp2.get(TotalO).put(resultSet2.getString("product_code"), zero);
						}
					} catch (Exception e) {
						Debug.logError(e.getMessage(), PCArea.class.getName());
					}
				}
			} catch(Exception e) {
			
			}
			return tmp2;
		}
	}

	public class PC3Area extends AbstractOlapChart {
		public PC3Area(OlapInterface olap, OlapResultQueryInterface query2) {
			super(olap, query2);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;
			for(String key : map.keySet()) {
				if(yAxis.get(key) == null) {
					yAxis.put(key, new ArrayList<Object>());
				}
				for(String s : map.get(key).keySet()) {
					yAxis.get(key).add(map.get(key).get(s));
					if(!xAxis.contains(s)) {
						xAxis.add(s);
					}
				}
			}
		}
	}

	public class ResultReport extends ReturnResultGrid {
		public ResultReport() {
			addDataField("channel");
			addDataField("productId");
			addDataField("productName");
			addDataField("Quantity");
			addDataField("Total");
			addDataField("stt");
		}
		
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			try {
				BigDecimal quantityResult = result.getBigDecimal("Quantity");
				BigDecimal totalResult = result.getBigDecimal("Total");
				String channelResult = result.getString("sales_method_channel_enum_id");
				GenericValue channell = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", channelResult), false);
				String channelName = channell.getString("description");
				String storeChannelId = (String) getParameter(STORE_CHANNEL);
				map.put("channel", channelName);
				if(UtilValidate.isEmpty(channelResult)){
					map.put("channel", storeChannelId);
				}
				map.put("productId", result.getString("product_code"));
				map.put("productName", result.getString("product_name"));
				if(UtilValidate.isNotEmpty(quantityResult)){
					map.put("Quantity", quantityResult);
				} else{
					map.put("Quantity", "-");
				}
				if(UtilValidate.isNotEmpty(quantityResult)){
					map.put("Total", totalResult);
				} else{
					map.put("Total", "-");
				}
			} catch (Exception e) {
				Debug.logError(e.getMessage(), ResultReport.class.getName());
			}
			return map;
		}

	}
}