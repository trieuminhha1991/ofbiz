package com.olbius.basepos.report;


import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.ReturnResultChartInterface;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class TopStore extends OlbiusBuilder{
	public TopStore(Delegator delegator) {
		super(delegator);
	}
	
	private OlbiusQuery query;

	public static String resource = "BasePosUiLabels";
	public static final String ORG = "ORG";
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			((ReturnResultChartInterface) getOlapResult( ).getResultQuery()).setSeriesDefaultName("-");
			addXAxis("product_store_id");
			addYAxis("_ext_price");
		}
	}
	
	private void initQuery() {
		query = OlbiusQuery.make(getSQLProcessor());
		Condition condition = new Condition();
		String org = (String) getParameter(ORG);
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate))
//		.andEQ("enumeration_dimension.enum_id", "SMCHANNEL_POS")
		.andEQ("sales_order_fact.order_status", "ORDER_COMPLETED")
		.and("sales_order_fact.return_id IS NULL")
		.andEQ("party_dimension.party_id", org, org != null);
		
		query.from("sales_order_fact")
		.select("product_store_dimension.product_store_id")
		.select("product_store_dimension.store_name")
		.select("sum(total)", "_ext_price")
		.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "currency_dimension", null, "currency_dim_id = currency_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", null, "party_from_dim_id = party_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_store_dimension", null, "product_store_dim_id = product_store_dimension.dimension_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id")
		.where(condition)
		.groupBy("product_store_dimension.product_store_id").groupBy("product_store_dimension.store_name").orderBy("_ext_price");
	}
	
	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	}
}
