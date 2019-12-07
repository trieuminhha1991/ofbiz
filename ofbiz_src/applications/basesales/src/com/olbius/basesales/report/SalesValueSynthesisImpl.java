package com.olbius.basesales.report;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalesValueSynthesisImpl extends OlbiusBuilder {
	public static final String CHART_TYPE = "CHART_TYPE";
	public static final String PRODUCT_STORE = "PRODUCT_STORE";
	public static final String ORGANIZATION = "ORGANIZATION";
	public static final String CURRENCY = "CURRENCY";
	public static final String STATISTICS = "STATISTICS";
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String CHANNEL = "CHANNEL";
	public static final String FILTER = "FILTER";
	public static final String BRANCH = "BRANCH";
	public static final String LEVEL = "LEVEL";
	
	private String dateType;
	
	public SalesValueSynthesisImpl(Delegator delegator) {
		super(delegator);
	}

	private OlbiusQuery query;
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	public void prepareResultBuilder() {
		if(getOlapResult() instanceof OlapLineChart) {
			getOlapResult().putParameter(DATE_TYPE, dateType);
		}
	};
	
	@Override
	public void prepareResultChart() {
		String filterFlag2 = (String) getParameter(FILTER);
		String chartType2 = (String) getParameter(CHART_TYPE);
		if(getOlapResult() instanceof OlapLineChart) {
			if("PRODUCT_STORE".equals(filterFlag2)){
				addSeries("product_store_id");
			} else if("CHANNEL".equals(filterFlag2)){
				addSeries("channel_id");
			} else if("REGION".equals(filterFlag2)){
				addSeries("branch_id");
			} else if("LEVEL".equals(filterFlag2)){
				addSeries("arc");
			}
			addXAxis("date_typee");
			if("SALES_VALUE".equals(chartType2)){
				addYAxis("value_total");
			} else if ("ORDER_VOLUME".equals(chartType2)){
				addYAxis("order_volume");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		String chartType = (String) getParameter(CHART_TYPE);
		String filterFlag = (String) getParameter(FILTER);
		List<Object> productStoreId = (List<Object>) getParameter(PRODUCT_STORE);
		List<Object> channelId = (List<Object>) getParameter(CHANNEL);
		List<Object> branchId = (List<Object>) getParameter(BRANCH);
		String organ = (String) getParameter(ORGANIZATION);
		String currency = (String) getParameter(CURRENCY);
		String filterLevel = (String) getParameter(LEVEL);
		Condition condition = new Condition();
		dateType = (String) getParameter(DATE_TYPE);
		dateType = getDateType(dateType);
		if (dateType != null) {
			dateType = getDateType(dateType);
		}
		
		query = new OlbiusQuery(getSQLProcessor());
		
		query.select("date_dimension.".concat(dateType), "date_typee").select("SUM(total)", "value_total", "SALES_VALUE".equals(chartType))
			.select("count(distinct order_id)", "order_volume", "ORDER_VOLUME".equals(chartType))
			.select("product_store_dimension.product_store_id", "PRODUCT_STORE".equals(filterFlag))
			.select("channel.enum_id", "channel_id", "CHANNEL".equals(filterFlag))
			.select("branch.party_id", "branch_id", "REGION".equals(filterFlag))
			.select("level_name.party_id", "arc", "LEVEL".equals(filterFlag))
			.from("sales_order_fact")
			.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
			.join(Join.INNER_JOIN, "product_store_dimension", "product_store_dimension.dimension_id = sales_order_fact.product_store_dim_id", "PRODUCT_STORE".equals(filterFlag))
			.join(Join.INNER_JOIN, "enumeration_dimension", "channel", "channel.dimension_id = sales_order_fact.sales_method_channel_enum_dim_id", "CHANNEL".equals(filterFlag))
			.join(Join.INNER_JOIN, "party_dimension", "branch", "branch.dimension_id = sales_order_fact.party_from_dim_id", "REGION".equals(filterFlag))
			.join(Join.INNER_JOIN, "level_relationship", "level", "level.salesman_id = sales_order_fact.sale_executive_party_dim_id", "LEVEL".equals(filterFlag))
			.join(Join.INNER_JOIN, "party_dimension", "level_name", "level_name.dimension_id = level.asm_dep", "LEVEL".equals(filterFlag) && "ASM".equals(filterLevel))
			.join(Join.INNER_JOIN, "party_dimension", "level_name", "level_name.dimension_id = level.rsm_dep", "LEVEL".equals(filterFlag) && "RSM".equals(filterLevel))
			.join(Join.INNER_JOIN, "party_dimension", "level_name", "level_name.dimension_id = level.csm_dep", "LEVEL".equals(filterFlag) && "CSM".equals(filterLevel))
			.join(Join.INNER_JOIN, "currency_dimension", "currency_dimension.dimension_id = sales_order_fact.currency_dim_id")
			.join(Join.INNER_JOIN, "party_dimension", "organ", "sales_order_fact.party_from_dim_id = organ.dimension_id")
			.join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sales_order_fact.promo_dim_id")
			.where(condition)
			.groupBy("date_dimension.".concat(dateType), dateType != null).groupBy("product_store_dimension.product_store_id", "PRODUCT_STORE".equals(filterFlag))
			.groupBy("channel.enum_id", "CHANNEL".equals(filterFlag)).groupBy("branch.party_id", "REGION".equals(filterFlag))
			.groupBy("level_name.party_id", "LEVEL".equals(filterFlag))
			.orderBy("date_dimension.".concat(dateType), dateType != null).orderBy("value_total", "SALES_VALUE".equals(chartType));
		
		condition.andEQ("currency_dimension.currency_id", currency, currency != null)
		.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).and("product_promo_dimension.product_promo_id IS NULL")
		.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'").and("sales_order_fact.order_status = 'ORDER_COMPLETED'")
		.andEQ("organ.party_id", organ, organ != null && !"LEVEL".equals(filterFlag));
		if(UtilValidate.isNotEmpty(productStoreId) && productStoreId.toArray().length != 0){
			condition.andIn("product_store_dimension.product_store_id", productStoreId, productStoreId.toArray().length != 0);
		}
		if(UtilValidate.isNotEmpty(channelId) && channelId.toArray().length != 0) {
			condition.andIn("channel.enum_id", channelId, channelId.toArray().length != 0);
		}
		if(UtilValidate.isNotEmpty(branchId) && branchId.toArray().length != 0) {
			condition.andIn("branch.party_id", branchId, branchId.toArray().length != 0);
		}
	}
}
