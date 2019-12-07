package com.olbius.basesales.report;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class CustomerSatisfactionReportImpl extends OlbiusBuilder {
	public CustomerSatisfactionReportImpl(Delegator delegator) {
		super(delegator);
	}

	public static final String ORG = "ORG";
	public static final String GROUP = "GROUP";
	public static final String SALES_CHANNEL = "SALES_CHANNEL";
	public static final String CHANNEL_TYPE = "CHANNEL_TYPE";
	public static final String QUANTITY = "QUANTITY";

	private OlbiusQuery query;
	
	@SuppressWarnings("unchecked")
	@Override
	public void prepareResultGrid() {
		List<Object> storeId = (List<Object>) getParameter(SALES_CHANNEL);
		List<Object> channelId = (List<Object>) getParameter(CHANNEL_TYPE);
		addDataField("stt");
		if(UtilValidate.isNotEmpty(channelId)){
			addDataField("channel", "channel_type");
		}
		if(UtilValidate.isNotEmpty(storeId)){
			addDataField("store", "store_name");
		}
		addDataField("classificationGroup", "customer_group");
		addDataField("productId", "product_code");
		addDataField("productName", "product_name");
		addDataField("volume", "volume");
		addDataField("unit", "quantity_uom");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapPieChart) {
			addXAxis("product_code");
			addYAxis("volume");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		List<Object> classificationGroup = (List<Object>) getParameter(GROUP);
		List<Object> storeId = (List<Object>) getParameter(SALES_CHANNEL);
		List<Object> channelId = (List<Object>) getParameter(CHANNEL_TYPE);
		String organ = (String) getParameter(ORG);
		
		query = new OlbiusQuery(getSQLProcessor());

		Condition condition = new Condition();
		condition.and("sof.order_item_status <> 'ITEM_CANCELLED'").and("sof.total != '0'")
		.andBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate))
		.andEQ("org.party_id", organ)
		.andIn("pcgd.party_classification_group_id", classificationGroup, classificationGroup != null)
		.andIn("psd.product_store_id", storeId, UtilValidate.isNotEmpty(storeId))
		.andIn("ed.enum_id", channelId, UtilValidate.isNotEmpty(channelId));
		
		query.select("pcgd.party_classification_group_id").select("pcgd.description", "customer_group").select("pd.product_code")
		.select("pd.product_name").select("sum(sof.quantity)", "volume").select("sof.quantity_uom")
		.select("psd.store_name", UtilValidate.isNotEmpty(storeId)).select("ed.description", "channel_type", UtilValidate.isNotEmpty(channelId))
		.from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "party_classification_fact", "pcf", "pcf.party_dim_id = sof.party_to_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "org", "org.dimension_id = sof.party_from_dim_id")
		.join(Join.INNER_JOIN, "party_class_group_dimension", "pcgd", "pcf.party_class_group_dim_id = pcgd.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "pd", "pd.dimension_id = sof.product_dim_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
		.join(Join.INNER_JOIN, "product_store_dimension", "psd", "psd.dimension_id = sof.product_store_dim_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", "ed", "ed.dimension_id = sof.sales_method_channel_enum_dim_id")
		.where(condition)
		.groupBy("pcgd.party_classification_group_id").groupBy("pcgd.description").groupBy("pd.product_code")
		.groupBy("pd.product_name").groupBy("sof.quantity_uom").groupBy("psd.store_name", UtilValidate.isNotEmpty(storeId)).groupBy("channel_type", UtilValidate.isNotEmpty(channelId)).orderBy("pcgd.party_classification_group_id, volume DESC");
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
}
