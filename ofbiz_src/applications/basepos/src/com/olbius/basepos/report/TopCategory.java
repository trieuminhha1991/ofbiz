package com.olbius.basepos.report;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.ReturnResultChartInterface;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.InnerJoin;
import com.olbius.bi.olap.query.join.Join;

public class TopCategory extends OlbiusBuilder{
	public TopCategory(Delegator delegator) {
		super(delegator);
	}
	
	public static String resource = "BasePosUiLabels";
	public static final String ORG = "ORG";
	public static final String STORE = "STORE";
	
	private OlbiusQuery query;
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			((ReturnResultChartInterface) getOlapResult( ).getResultQuery()).setSeriesDefaultName("-");
			addXAxis("category_name");
			addYAxis("_ext_price");
		}
	}
	
	private void initQuery() {
		query = OlbiusQuery.make(getSQLProcessor());
		Condition condition = new Condition();
		String productStoreId = (String) getParameter(STORE);
		String org = (String) getParameter(ORG);
		
		query.from("sales_order_fact", "sof").select("category_dimension.category_id").select("category_dimension.category_name")
		.select("sum(total)", "_ext_price")
		.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "currency_dimension", null, "currency_dim_id = currency_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", null, "party_from_dim_id = party_dimension.dimension_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_store_dimension", null, "product_store_dim_id = product_store_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "pcr.product_dim_id = sof.product_dim_id")
		.join(Join.INNER_JOIN, "date_dimension", "from_date", "pcr.from_dim_date = from_date.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "thru_date", "pcr.thru_dim_date = thru_date.dimension_id");
		
		Join category = new InnerJoin();
		Condition condCate = new Condition();
		condCate.and("pcr.category_dim_id = category_dimension.dimension_id")
		.andEQ("category_dimension.category_type", "CATALOG_CATEGORY")
		.and("date_dimension.date_value >= from_date.date_value")
		.and(Condition.make("thru_date.date_value IS NULL").or("thru_date.date_value >= date_dimension.date_value"));
		category.table("category_dimension").on(condCate);
		query.join(category);
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate))
		.andEQ("enumeration_dimension.enum_id", "SMCHANNEL_POS")
		.andEQ("sof.order_status", "ORDER_COMPLETED")
		.and("sof.return_id IS NULL")
		.andEQ("product_store_dimension.product_store_id", productStoreId, !"all".equals(productStoreId))
		.andEQ("party_dimension.party_id", org, org != null);
		query.where(condition);
		
		query.groupBy("category_dimension.category_id").groupBy("category_dimension.category_name").orderBy("_ext_price");
	}
	
	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	} 
	
}
