package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalesOlapImplByChannelMultiv2 extends OlbiusBuilder {
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String STORE_CHANNEL = "STORE_CHANNEL";
	public static final String ORDER_STATUS = "ORDER_STATUS";
	public static final String ORG = "ORG";
	public static final String CATEGORY = "CATEGORY";
	public static final String PARTY = "PARTY";
	public static final String FLAGSM = "FLAGSM";

	private OlbiusQuery query2;
	
	public SalesOlapImplByChannelMultiv2(Delegator delegator) {
		super(delegator);
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("channel", "channelQ");
		addDataField("category", "category_name");
		addDataField("productId", "product_code");
		addDataField("productName", "internal_name");
		addDataField("Quantity", "Quantity", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){
					return (BigDecimal) object;
				}else{
					return new BigDecimal(0);
				}
			}
		});
		addDataField("Total", "Total", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){
					return (BigDecimal) object;
				}else{
					return new BigDecimal(0);
				}
			}
		});
		addDataField("unit", "unit");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			addSeries("channelQ");
			addXAxis("product_code");
			addYAxis("Total");
		}
		
		if(getOlapResult() instanceof OlapPieChart) {
			addXAxis("description");
			addYAxis("Total");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		List<Object> storeChannelId = (List<Object>) getParameter(STORE_CHANNEL);
		List<Object> categoryId = (List<Object>) getParameter(CATEGORY);
		String organ = (String) getParameter(ORG);
		String status = (String) getParameter(ORDER_STATUS);
		String flagSM = (String) getParameter(FLAGSM);
		List<Object> salesmanList = (List<Object>) getParameter(PARTY);

		query2 = OlbiusQuery.make(getSQLProcessor());
		
		Condition condition = new Condition();
		
		query2.from("sales_order_fact")
		.select("party_dimension.party_id")
		.select("product_dimension.product_code")
		.select("product_dimension.internal_name")
		.select("category_dimension.category_name")
		.select("enumeration_dimension.description", "channelQ")
		.select("sum(sales_order_fact.quantity)", "Quantity")
		.select("sum(sales_order_fact.total)", "Total")
		.select("sales_order_fact.quantity_uom", "unit")
		.select("enumeration_dimension.description || '-' || product_dimension.product_code", "description")
		.join(Join.INNER_JOIN, "product_dimension", "product_dimension.dimension_id = sales_order_fact.product_dim_id")
		.join(Join.INNER_JOIN,"date_dimension","date_dimension.dimension_id = sales_order_fact.order_date_dim_id");
		if(UtilValidate.isNotEmpty(flagSM)){
			query2.join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.sale_executive_party_dim_id = party_dimension.dimension_id");
		}else{
			query2.join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.party_from_dim_id = party_dimension.dimension_id");
		}
		query2.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_order_fact.sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
		.where(condition)
		.groupBy("party_dimension.party_id")
		.groupBy("channelQ").groupBy("product_dimension.product_code").groupBy("product_dimension.internal_name")
		.groupBy("category_dimension.category_name").groupBy("unit")
		.groupBy("party_dimension.dimension_id");
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		if(UtilValidate.isNotEmpty(storeChannelId) && storeChannelId.toArray().length == 1 && "all".equals(storeChannelId.get(0))){
			
		} else {
			condition.and(Condition.makeIn("enumeration_dimension.enum_id", storeChannelId, !"all".equals(storeChannelId)));
		}
		if(UtilValidate.isNotEmpty(flagSM)){
			if(salesmanList.isEmpty()){

			}else{
				condition.and(Condition.makeIn("party_dimension.party_id", salesmanList));
			}
		}else{
			condition.and(Condition.makeEQ("party_dimension.party_id", organ));
		}
		condition.andEQ("sales_order_fact.order_status", status, "all".equals(status)).and("category_dimension.category_id not like '%TAX%'")
		.and("product_category_relationship.thru_dim_date = -1");
//		condition.and(Condition.make("sales_order_fact.return_id isnull"));
		if("ORDER_CANCELLED".equals(status)){
			condition.and(Condition.make("sales_order_fact.order_item_status = 'ITEM_CANCELLED'"));
		} else {
			condition.and(Condition.make("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'"));
		}
		if(UtilValidate.isNotEmpty(categoryId) && categoryId.toArray().length == 1 && "all".equals(categoryId.get(0))){
			
		} else {
			if(UtilValidate.isNotEmpty(categoryId)) {
				condition.and(Condition.makeIn("category_dimension.category_id", categoryId, !"all".equals(categoryId)));
			}
		}
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query2 == null) {
			initQuery();
		}
		return query2;
	}
}
