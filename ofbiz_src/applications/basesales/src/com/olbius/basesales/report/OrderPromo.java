package com.olbius.basesales.report;

import java.util.List;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class OrderPromo extends OlbiusBuilder {

	public static final String SALES_CHANNEL = "SALES_CHANNEL";
	public static final String CHANNEL_TYPE = "CHANNEL_TYPE";
	
	public OrderPromo(Delegator delegator) {
		super(delegator);
	}

	private OlbiusQuery query;
	
	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("productPromoId", "product_promo_id");
		addDataField("promoName", "promo_name");
		addDataField("store", "store_name");
		addDataField("order", "order_id");
		addDataField("created", "sales_created");
		addDataField("customerCode", "customer_code");
		addDataField("cusName", "cus_name");
		addDataField("voucher", "product_promo_code_id");
		addDataField("totalDiscountAmount", "total_discount_amount");
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		List<Object> productStoreId = (List<Object>) getParameter(SALES_CHANNEL);
		List<Object> channelId = (List<Object>) getParameter(CHANNEL_TYPE);
		Condition condition = new Condition();
		condition.andIn("psd.product_store_id", productStoreId).andIn("ed.enum_id", channelId)
		.andBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		
		query = new OlbiusQuery(getSQLProcessor());
		
		query.select("ppd.product_promo_id").select("ppd.promo_name")
		.select("psd.store_name").select("opf.order_id")
		.select("'[' || COALESCE(pd.party_code, 'CODE') || '] ' || COALESCE(pd.last_name, '') || ' ' || COALESCE(pd.middle_name, '') || ' ' || COALESCE(pd.first_name, '')", "sales_created")
		.select("pd2.party_code", "customer_code").select("COALESCE(pd2.last_name, '') || ' ' || COALESCE(pd2.middle_name, '') || ' ' || COALESCE(pd2.first_name, '')", "cus_name")
		.select("opf.product_promo_code_id").select("opf.total_discount_amount")
		.from("order_promo_fact", "opf")
		.join(Join.INNER_JOIN, "product_promo_dimension", "ppd", "ppd.dimension_id = opf.product_promo_dim_id")
		.join(Join.INNER_JOIN, "product_store_dimension", "psd", "psd.dimension_id = opf.store_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = opf.sales_created_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = opf.customer_dim_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", "ed", "ed.dimension_id = opf.channel_dim_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = opf.from_date")
		.where(condition)
		.orderBy("opf.order_id", OlbiusQuery.DESC);
		
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
}
