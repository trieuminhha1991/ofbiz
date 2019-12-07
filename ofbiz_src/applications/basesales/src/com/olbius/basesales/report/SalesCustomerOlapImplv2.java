package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalesCustomerOlapImplv2 extends OlbiusBuilder {
	public SalesCustomerOlapImplv2(Delegator delegator) {
		super(delegator);
	}

	public static final String ORDER_STATUS = "ORDER_STATUS";
	public static final String ORGANIZATION = "ORGANIZATION";
	public static final String PRODUCT_STORE = "PRODUCT_STORE";
	public static final String PARTY = "PARTY";
	public static final String FLAGSM = "FLAGSM";
	public static final String CHANNEL = "CHANNEL";
	
	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("channelName", "channel");
		addDataField("storeName", "store_name");
		addDataField("customerId", "party_id");
		addDataField("customerName", "name");
		addDataField("monetized", "value1");
		addDataField("percent", "percent");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapPieChart) {
			addXAxis("description");
			addYAxis("Total");
		}
	}
	
	private OlbiusQuery query;
	private OlbiusQuery fromQ; 
	private OlbiusQuery joinQ;
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		String store = (String) getParameter(PRODUCT_STORE);
		String status = (String) getParameter(ORDER_STATUS);
		String organization = (String) getParameter(ORGANIZATION);
		String flagSM = (String) getParameter(FLAGSM);
		List<Object> salesmanList = (List<Object>) getParameter(PARTY);
		List<Object> channel = (List<Object>) getParameter(CHANNEL);
		
		query = new OlbiusQuery(getSQLProcessor());
		fromQ = new OlbiusQuery(getSQLProcessor());
		joinQ = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		fromQ.select("product_store_dimension.store_name")
		.select("party_dimension.party_id").select("party_dimension.name").select("enumeration_dimension.description", "channel")
		.select("sum(sales_order_fact.total)", "value1").select("organ.party_id", "organ1")
		.from("sales_order_fact")
		.join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.party_to_dim_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", "enumeration_dimension.dimension_id = sales_order_fact.sales_method_channel_enum_dim_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sales_order_fact.promo_dim_id");
		if(UtilValidate.isNotEmpty(flagSM)){
			fromQ.join(Join.INNER_JOIN, "party_dimension", "organ", "sales_order_fact.sale_executive_party_dim_id = organ.dimension_id");
		}else{
			fromQ.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id");
		}
		fromQ.join(Join.INNER_JOIN, "product_store_dimension", "product_store_dimension.dimension_id = sales_order_fact.product_store_dim_id")
		.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = sales_order_fact.order_date_dim_id")
		.where(condition).groupBy("party_dimension.dimension_id").groupBy("organ.dimension_id")
		.groupBy("enumeration_dimension.dimension_id").groupBy("product_store_dimension.dimension_id").orderBy("party_id");
		
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
//		condition.and(Condition.make("sales_order_fact.return_id isnull"));
		condition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
		if(UtilValidate.isNotEmpty(flagSM)){
			condition.andIn("organ.party_id", salesmanList);
		}else{
			condition.and(Condition.makeEQ("organ.party_id", organization));
		}
		condition.and(Condition.makeEQ("sales_order_fact.order_status", status));
		condition.and(Condition.make("product_promo_dimension.product_promo_id IS NULL"));
		if(store != null){
			condition.and(Condition.makeEQ("product_store_dimension.product_store_id", store, store != null));
		}
		if(UtilValidate.isNotEmpty(channel) && !"all".equals(channel)){
			condition.and(Condition.makeIn("enumeration_dimension.enum_id", channel, channel != null));
		}
		
		joinQ.select("sum(sales_order_fact.total)", "value2").select("organ.party_id", "organ2")
		.from("sales_order_fact").join(Join.INNER_JOIN, "party_dimension", "party_dimension.dimension_id = sales_order_fact.party_to_dim_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", "enumeration_dimension.dimension_id = sales_order_fact.sales_method_channel_enum_dim_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sales_order_fact.promo_dim_id");
		if(UtilValidate.isNotEmpty(flagSM)){
			joinQ.join(Join.INNER_JOIN, "party_dimension", "organ", "sales_order_fact.sale_executive_party_dim_id = organ.dimension_id");
		}else{
			joinQ.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id");
		}
		joinQ.join(Join.INNER_JOIN, "product_store_dimension", "product_store_dimension.dimension_id = sales_order_fact.product_store_dim_id")
		.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = sales_order_fact.order_date_dim_id")
		.where(condition).groupBy("organ.dimension_id");
		
		query.select("tmp.party_id").select("tmp.name").select("tmp.value1/join1.value2*100", "percent")
		.select("tmp.value1").select("join1.value2").select("tmp.channel").select("tmp.store_name")
		.from(fromQ, "tmp")
		.join(Join.INNER_JOIN, joinQ, "join1", "tmp.organ1 = join1.organ2");
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