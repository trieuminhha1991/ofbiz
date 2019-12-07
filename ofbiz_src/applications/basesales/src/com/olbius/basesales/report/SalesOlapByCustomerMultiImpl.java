package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalesOlapByCustomerMultiImpl extends AbstractOlap {
	public static final String STORE_CHANNEL = "STORE_CHANNEL";
	public static final String PRODUCT_STORE = "PRODUCT_STORE";
	public static final String ORGANIZATION = "ORGANIZATION";
	
	private OlbiusQuery query;
	private OlbiusQuery fromQ; 
	private OlbiusQuery joinQ;
	
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		List<Object> channel = (List<Object>) getParameter(STORE_CHANNEL);
		List<Object> store = (List<Object>) getParameter(PRODUCT_STORE);
		String organization = (String) getParameter(ORGANIZATION);
		
		query = new OlbiusQuery(getSQLProcessor());
		fromQ = new OlbiusQuery(getSQLProcessor());
		joinQ = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		fromQ.select("product_dimension.product_id").select("product_dimension.product_code").select("product_store_dimension.store_name")
		.select("party_dimension.party_id").select("party_dimension.name").select("enumeration_dimension.description", "channel")
		.select("sum(sales_order_fact.quantity)", "volume").select("sum(sales_order_fact.total)", "value1")
		.from("sales_order_fact")
		.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.party_to_dim_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", "enumeration_dimension.dimension_id = sales_order_fact.sales_method_channel_enum_dim_id")
		.join(Join.INNER_JOIN, "product_dimension", "product_dimension.dimension_id = sales_order_fact.product_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id")
		.join(Join.INNER_JOIN, "product_store_dimension", "product_store_dimension.dimension_id = sales_order_fact.product_store_dim_id")
		.where(condition).groupBy("product_dimension.dimension_id").groupBy("party_dimension.dimension_id")
		.groupBy("enumeration_dimension.dimension_id").groupBy("product_store_dimension.dimension_id").orderBy("party_id");
		
		condition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
		condition.and(Condition.makeEQ("organ.party_id", organization));
		if(store != null){
			condition.and(Condition.makeIn("product_store_dimension.product_store_id", store, store != null));
		}
		
		joinQ.select("party_dimension.party_id").select("party_dimension.name").select("sum(sales_order_fact.total)", "value2")
		.from("sales_order_fact")
		.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.party_to_dim_id")
		.groupBy("party_dimension.dimension_id").orderBy("party_id");
		
		query.select("tmp.party_id").select("tmp.name").select("tmp.product_id").select("tmp.product_code")
		.select("tmp.volume").select("tmp.value1").select("join1.value2").select("tmp.channel").select("tmp.store_name")
		.from(fromQ, "tmp")
		.join(Join.INNER_JOIN, joinQ, "join1", "tmp.party_id = join1.party_id");
		
//		query.select("sale_order_fact.sales_method_channel_enum_id")
//		.select("product_store_dimension.store_name")
//		.select("sum(total)", "toto")
//		.select("sale_order_fact.product_store_dim_id")
//		.from("sale_order_fact")
//		.join(Join.INNER_JOIN, "product_store_dimension", "sale_order_fact.product_store_dim_id = product_store_dimension.dimension_id")
//		.groupBy("sale_order_fact.sales_method_channel_enum_id")
//		.groupBy("product_store_dimension.dimension_id")
//		.groupBy("sale_order_fact.product_store_dim_id");
		
//		query2 = new OlbiusQuery(getSQLProcessor());
		
		
		
//		query2.select("sale_order_fact.sales_method_channel_enum_id", "channell")
//		.select("product_store_dimension.store_name")
//		.select("party_person_dimension.party_id", "customerId")
//		.select("party_person_dimension.first_name").select("party_person_dimension.middle_name").select("party_person_dimension.last_name")
//		.select("sum(total)", "tata")
//		.select("sale_order_fact.product_store_dim_id")
//		.select("queryTot.toto")
//		.select("product_store_dimension.product_store_id")
//		.from("sale_order_fact")
//		.join(Join.INNER_JOIN, "product_store_dimension", "sale_order_fact.product_store_dim_id = product_store_dimension.dimension_id")
//		.join(Join.INNER_JOIN, "party_person_dimension", "sale_order_fact.party_to_dim_id = party_person_dimension.dimension_id")
//		.join(Join.INNER_JOIN, query, "queryTot", "queryTot.product_store_dim_id = sale_order_fact.product_store_dim_id")
//		.join(Join.INNER_JOIN,"date_dimension","sale_order_fact.order_date_dim_id = date_dimension.dimension_id")
//		.join(Join.INNER_JOIN,"party_group_dimension","sale_order_fact.party_from_dim_id = party_group_dimension.dimension_id")
//		.where(condition)
//		.groupBy("sale_order_fact.sales_method_channel_enum_id")
//		.groupBy("product_store_dimension.dimension_id")
//		.groupBy("party_person_dimension.dimension_id")
//		.groupBy("sale_order_fact.product_store_dim_id")
//		.groupBy("queryTot.toto");
		
//		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
//		if(store != null){
//			condition.and(Condition.makeIn("product_store_dimension.product_store_id", store, store != null));
//		}
//		if(channel != null){
//			condition.and(Condition.makeIn("sale_order_fact.sales_method_channel_enum_id", channel, channel != null));
//		}
//		condition.and(Condition.makeEQ("party_group_dimension.party_id", organization));
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}

	public class ResultReport extends ReturnResultGrid {
		public ResultReport() {
			addDataField("stt");
			addDataField("channelName");
			addDataField("storeName");
			addDataField("customerId");
			addDataField("customerName");
			addDataField("monetized");
			addDataField("percent");
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			
			try {
				String channelId = result.getString("channel");
				String storeName = result.getString("store_name");
				String customerId = result.getString("party_id");
				String customerName = result.getString("name");
				
				BigDecimal monetized = result.getBigDecimal("value1");
				BigDecimal total = result.getBigDecimal("value2");
				BigDecimal percentResult = monetized.divide(total, 4, RoundingMode.HALF_UP);
				percentResult = percentResult.multiply(new BigDecimal(100));
				
				map.put("channelName", channelId);
				map.put("storeName", storeName);
				map.put("customerId", customerId);
				map.put("customerName", customerName);
				map.put("monetized", monetized);
				map.put("percent", percentResult);
			} catch (Exception e) {
				Debug.logError(e.getMessage(), ResultReport.class.getName());
			}
			return map;
		}

	}
}