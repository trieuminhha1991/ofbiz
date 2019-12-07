package com.olbius.basesales.report;

import java.util.List;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class EvaluateRevenuePieChartImpl extends OlbiusBuilder {
	public static final String ORG = "ORG";
	public static final String SALESMAN = "SALESMAN";
	public static final String FLAG = "FLAG";
	
	public EvaluateRevenuePieChartImpl(Delegator delegator) {
		super(delegator);
	}
	
	private OlbiusQuery queryChart;
	
	@Override
	public void prepareResultGrid() {
		addDataField("state", "state");
		addDataField("value1", "value1");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapPieChart) {
			addXAxis("state");
			addYAxis("value1");
		}
	}

	@SuppressWarnings("unchecked")
	private void initQuery() {
		String organ = (String) getParameter(ORG);
		String flag = (String) getParameter(FLAG);
		List<Object> salesmanList = (List<Object>) getParameter(SALESMAN);
		
		queryChart = new OlbiusQuery(getSQLProcessor());

		Condition condition = new Condition();
		
		condition.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'").and("sales_order_fact.order_status='ORDER_COMPLETED'");
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		if("DISTRIBUTOR".equals(flag)){
			condition.makeIn("salesman.party_id", salesmanList);
		} else {
			condition.andEQ("organ.party_id", organ);
		}
		condition.and("geo_dimension.geo_name is not null");

		queryChart.select("sum(sales_order_fact.total)", "value1")
		.select("geo_dimension.geo_name", "state")
		.from("sales_order_fact")
		.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = sales_order_fact.party_to_dim_id")
		.join(Join.INNER_JOIN, "geo_dimension", null, "pd2.state_dim_id = geo_dimension.dimension_id");
		if("DISTRIBUTOR".equals(flag)){
			queryChart.join(Join.INNER_JOIN, "party_dimension", "salesman", "salesman.dimension_id = sales_order_fact.sale_executive_party_dim_id");
		} else {
			queryChart.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id");
		}
		queryChart.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = sales_order_fact.order_date_dim_id")
		.where(condition)
		.groupBy("geo_dimension.dimension_id");
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(queryChart == null) {
			initQuery();
		}
		return queryChart;
	}
	
}