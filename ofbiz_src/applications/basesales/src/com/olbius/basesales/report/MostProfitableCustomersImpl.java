package com.olbius.basesales.report;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class MostProfitableCustomersImpl extends OlbiusBuilder {

	public MostProfitableCustomersImpl(Delegator delegator) {
		super(delegator);
	}

	public static final String CHANNEL = "CHANNEL";
	public static final String PRODUCT_STORE = "PRODUCT_STORE";
	public static final String ORG = "ORG";

	private OlbiusQuery query;
	
	@SuppressWarnings("unchecked")
	@Override
	public void prepareResultGrid() {
		List<Object> productStoreId = (List<Object>) getParameter(PRODUCT_STORE);
		List<Object> channelId = (List<Object>) getParameter(CHANNEL);
		addDataField("stt");
		addDataField("customerId", "cus_id");
		addDataField("customerCode", "cus_code");
		addDataField("customerName", "cus_name");
		if(UtilValidate.isNotEmpty(channelId)){
			addDataField("channelName", "channel_name", new ReturnResultCallback<String>() {
				@Override
				public String get(Object object) {
					if(!"all_object".equals(object)){ 
						return (String) object; 
					} else{ 
						return "-"; 
					}
				}
			});
		} else {
			addDataField("channelName");
		}
		if(UtilValidate.isNotEmpty(productStoreId)){
			addDataField("storeName", "store_name", new ReturnResultCallback<String>() {
				@Override
				public String get(Object object) {
					if(!"all_object".equals(object)){ 
						return (String) object; 
					} else{ 
						return "-"; 
					}
				}
			});
		} else {
			addDataField("storeName");
		}
			
		addDataField("total1", "value1");
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		List<Object> productStoreId = (List<Object>) getParameter(PRODUCT_STORE);
		List<Object> channelId = (List<Object>) getParameter(CHANNEL);
		String organ = (String) getParameter(ORG);
		
		query = new OlbiusQuery(getSQLProcessor());

		Condition condition = new Condition();
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		if(UtilValidate.isNotEmpty(productStoreId)){
			condition.andIn("pstore.product_store_id", productStoreId, productStoreId != null);
		}
		if(UtilValidate.isNotEmpty(channelId)){
			condition.andIn("channel.enum_id", channelId, channelId != null);
		}
		condition.andEQ("organ.party_id", organ);
		condition.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED' AND customer.party_id <> '_NA_' AND sales_order_fact.order_status = 'ORDER_COMPLETED'");
		
		query.select("customer.party_code", "cus_code").select("customer.party_id", "cus_id");
		if(UtilValidate.isNotEmpty(channelId)){
			query.select("channel.description", "channel_name");
		}
		if(UtilValidate.isNotEmpty(productStoreId)){
			query.select("pstore.store_name", "store_name");
		}
		query.select("customer.description", "cus_name")
		.select("sum(sales_order_fact.total + sales_order_fact.discount_amount)", "value1")
		.from("sales_order_fact")
		.join(Join.INNER_JOIN, "party_dimension", "customer", "sales_order_fact.party_to_dim_id = customer.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id")
		.join(Join.INNER_JOIN, "product_store_dimension", "pstore", "pstore.dimension_id = sales_order_fact.product_store_dim_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", "channel", "channel.dimension_id = sales_order_fact.sales_method_channel_enum_dim_id")
		.join(Join.INNER_JOIN,"date_dimension","sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
		.where(condition).groupBy("customer.party_id").groupBy("customer.party_code").groupBy("customer.description");
		if(UtilValidate.isNotEmpty(channelId)){
			query.groupBy("channel.description");
		}
		if(UtilValidate.isNotEmpty(productStoreId)){
			query.groupBy("pstore.store_name");
		}
		query.orderBy("value1", OlbiusQuery.DESC);
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
}
