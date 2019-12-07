package com.olbius.basesales.report;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.ReturnResultChartInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class TopSaexChartImpl extends OlbiusBuilder {

	public TopSaexChartImpl(Delegator delegator) {
		super(delegator);
	}

	public static final String ORG = "ORG";
	public static final String STATUS = "STATUS";
	public static final String VALUE = "VALUE";

	private OlbiusQuery queryChart;
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			addXAxis("saex_code");
			addYAxis("value_total");
			String all = (String) getParameter(VALUE);
			((ReturnResultChartInterface) getOlapResult().getResultQuery()).setSeriesDefaultName(all);
		}
	}

	private void initQuery() {
		String organ = (String) getParameter(ORG);
		String status = (String) getParameter(STATUS);
		queryChart = new OlbiusQuery(getSQLProcessor());

		Condition condition = new Condition();
		
		condition.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'").and("sales_order_fact.total != '0'");
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.andEQ("organ.party_id", organ);
		condition.and("sales_order_fact.order_status = 'ORDER_COMPLETED'");
		condition.and("saex.party_id is not null");

		queryChart.select("saex.party_id", "saex_code")
		.select("sum(sales_order_fact.total + sales_order_fact.tax - sales_order_fact.discount_amount)", " value_total")
		.from("sales_order_fact")
		.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = sales_order_fact.order_date_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "saex", "saex.dimension_id = sales_order_fact.sale_executive_party_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id")
		.where(condition)
		.groupBy("saex.dimension_id");
		if(status.equals("Y")){
			queryChart.orderBy("value_total", OlbiusQuery.DESC).limit(10);
		}else {
			queryChart.orderBy("value_total", OlbiusQuery.ASC).limit(10);
		}
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(queryChart == null) {
			initQuery();
		}
		return queryChart;
	}
}