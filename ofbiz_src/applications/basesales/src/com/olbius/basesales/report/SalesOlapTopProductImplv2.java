package com.olbius.basesales.report;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.ReturnResultChartInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalesOlapTopProductImplv2 extends OlbiusBuilder {
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String TOP_PRODUCT = "TOP_PRODUCT";
	public static final String STATUS_SALES = "STATUS_SALES";
	public static final String ORDER_STATUS = "ORDER_STATUS";
	public static final String QUANTITY = "QUANTITY";
	public static final String STORE_CHANNEL = "STORE_CHANNEL";
	public static final String CATEGORY = "CATEGORY";
	public static final String ORG = "ORG";
	public static final String ALL = "ALL";
	public static final String PARTY = "PARTY";
	public static final String FLAGSM = "FLAGSM";
	public static final String FILTER_TYPE = "FILTER_TYPE";

	public SalesOlapTopProductImplv2(Delegator delegator) {
		super(delegator);
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("channel", "channelQ", new ReturnResultCallback<String>() {
			@Override
			public String get(Object object) {
				if(!"allchannel".equals(object)){ 
					return (String) object; 
				} else{ 
					String all = (String) getParameter(ALL);
					return all; 
				}
			}
		});
		String filterType2 = (String) getParameter(FILTER_TYPE);
		if("product_".equals(filterType2)){
			addDataField("category", "category");
			addDataField("productId", "product_code");
			addDataField("productName", "internal_name");
		} else if ("staff_".equals(filterType2)){
			addDataField("staff_code", "staff_code");
			addDataField("staff_name", "full_name");
		} else if ("state_".equals(filterType2)){
			addDataField("geo_code", "geo_code");
			addDataField("geo_name", "geo_name");
		}
		addDataField("quantity1", "Quantity");
		addDataField("total1", "Total");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			if(!"all".equals(getParameter(STORE_CHANNEL)))  {
				addSeries("channelQ");
			} else {
				String all = (String) getParameter(ALL);
				((ReturnResultChartInterface) getOlapResult().getResultQuery()).setSeriesDefaultName(all);
			}
			String filterType3 = (String) getParameter(FILTER_TYPE);
			if("product_".equals(filterType3)){
				addXAxis("product_code");
			} else if ("staff_".equals(filterType3)){
				addXAxis("staff_code");
			} else if ("state_".equals(filterType3)){
				addXAxis("geo_code");
			}
			addYAxis("Total");
			
		}
	}

	private OlbiusQuery query2;
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		Integer topProduct1 = new Integer((String) getParameter(TOP_PRODUCT));
		String statusSales1 = (String) getParameter(STATUS_SALES);
		String status = (String) getParameter(ORDER_STATUS);
		String storeChannelId = (String) getParameter(STORE_CHANNEL);
		List<Object> categoryId = (List<Object>) getParameter(CATEGORY);
		String organ = (String) getParameter(ORG);
		String flagSM = (String) getParameter(FLAGSM);
		List<Object> salesmanList = (List<Object>) getParameter(PARTY);
		String filterType = (String) getParameter(FILTER_TYPE);
		String popular = "F";
		Condition condition = new Condition();
		
		query2 = new OlbiusQuery(getSQLProcessor());
		
		query2.from("sales_order_fact")
			.select("product_dimension.product_code", "product_".equals(filterType))
			.select("product_dimension.internal_name", "product_".equals(filterType))
			.select("gd.geo_code", "state_".equals(filterType))
			.select("gd.geo_name", "state_".equals(filterType))
			.select("pd2.party_code", "staff_code", "staff_".equals(filterType))
			.select("COALESCE(pd2.last_name, '') || ' ' || COALESCE(pd2.middle_name, '') || ' ' || COALESCE(pd2.first_name, '')", "full_name", "staff_".equals(filterType))
			.select("sum(quantity)", "Quantity")
			.select("category_dimension.category_name", "category", "product_".equals(filterType))
			.select("sum(total)", "Total");
		if(!"all".equals(storeChannelId)){
			query2.select("enumeration_dimension.description", "channelQ");
		} else {
			query2.select("'allchannel' as channelQ");
		}
		query2.join(Join.INNER_JOIN, "date_dimension", "order_date_dim_id = date_dimension.dimension_id")
			.join(Join.INNER_JOIN, "product_dimension", "product_dim_id = product_dimension.dimension_id", "product_".equals(filterType));
		if(UtilValidate.isNotEmpty(flagSM)){
			query2.join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.sale_executive_party_dim_id = party_dimension.dimension_id");
		}else{
			query2.join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.party_from_dim_id = party_dimension.dimension_id");
		}
		query2.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_order_fact.sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
			.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id", "product_".equals(filterType))
			.join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sales_order_fact.discount_dim_id")
			.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id", "product_".equals(filterType))
			.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = sales_order_fact.sale_executive_party_dim_id", "staff_".equals(filterType))
			.join(Join.INNER_JOIN, "party_dimension", "pd3", "pd3.dimension_id = sales_order_fact.party_to_dim_id", "state_".equals(filterType))
			.join(Join.INNER_JOIN, "geo_dimension", "gd", "gd.dimension_id = pd3.state_dim_id", "state_".equals(filterType))
			.where(condition)
			.groupBy("pd2.party_code", "staff_".equals(filterType))
			.groupBy("full_name", "staff_".equals(filterType))
			.groupBy("gd.geo_code", "state_".equals(filterType))
			.groupBy("gd.geo_name", "state_".equals(filterType))
			.groupBy("product_dimension.product_code", "product_".equals(filterType))
			.groupBy("product_dimension.internal_name", "product_".equals(filterType))
			.groupBy("category", "product_".equals(filterType));
			
		if(!"all".equals(storeChannelId)){
			query2.groupBy("enumeration_dimension.dimension_id");
		}
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate))
		.andEQ("sales_order_fact.order_status", status, status != null);
		if(UtilValidate.isNotEmpty(flagSM)){
			condition.andIn("party_dimension.party_id", salesmanList);
		}else{
			condition.andEQ("party_dimension.party_id", organ);
		}
		condition.and("category_dimension.category_id not like '%TAX%'", "product_".equals(filterType));
		if(!"all".equals(storeChannelId)){
			condition.andEQ("enumeration_dimension.enum_id", storeChannelId);
		}
		if(UtilValidate.isNotEmpty(categoryId) && "product_".equals(filterType)) {
			condition.andIn("category_dimension.category_id", categoryId, categoryId != null);
		}
		if("ORDER_CANCELLED".equals(status)){
			condition.and("sales_order_fact.order_item_status = 'ITEM_CANCELLED'");
		} else {
			condition.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'");
		}
		condition.and("product_promo_dimension.product_promo_id IS NULL")
		.and("pd2.party_id is not null", "staff_".equals(filterType)).and("product_category_relationship.thru_dim_date = -1");
		if(statusSales1.equals(popular)){
			query2.orderBy("Total", OlbiusQuery.DESC);
		} else {
			query2.orderBy("Total", OlbiusQuery.ASC);
		}
		query2.limit(topProduct1);
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query2 == null) {
			initQuery();
		}
		return query2;
	}
}