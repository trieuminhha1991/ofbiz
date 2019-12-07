package com.olbius.basesales.report;

import java.util.List;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.ReturnResultChartInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class CSReportChartImpl extends OlbiusBuilder {

	public CSReportChartImpl(Delegator delegator) {
		super(delegator);
	}

	public static final String ORG = "ORG";
	public static final String CATEGORY = "CATEGORY";
	public static final String GROUP = "GROUP";
	public static final String QUANTITY = "QUANTITY";

	private OlbiusQuery queryChart;
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			addXAxis("pr_code");
			addYAxis("value_total");
			String description = (String) getParameter(QUANTITY);
			((ReturnResultChartInterface) getOlapResult().getResultQuery()).setSeriesDefaultName(description);
		}
	}

	@SuppressWarnings("unchecked")
	private void initQuery() {
		List<Object> classificationGroup = (List<Object>) getParameter(GROUP);
		String organ = (String) getParameter(ORG);
		
		queryChart = new OlbiusQuery(getSQLProcessor());

		Condition condition = new Condition();
		
		condition.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'").and("sales_order_fact.total != '0'");
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.andEQ("organ.party_id", organ);
		if(classificationGroup != null){
			condition.and(Condition.makeIn("party_class_group_dimension.party_classification_group_id", classificationGroup, classificationGroup != null));
		}
		
		queryChart.select("product_dimension.product_code", "pr_code").select("sum(sales_order_fact.total + sales_order_fact.tax - sales_order_fact.discount_amount)", " value_total")
		.from("sales_order_fact")
		.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = sales_order_fact.order_date_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id")
		.join(Join.INNER_JOIN, "party_classification_fact", "sales_order_fact.party_to_dim_id = party_classification_fact.party_dim_id")
		.join(Join.INNER_JOIN, "party_class_group_dimension", "party_classification_fact.party_class_group_dim_id = party_class_group_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "product_dimension.dimension_id = sales_order_fact.product_dim_id")
		.where(condition)
		.groupBy("product_dimension.dimension_id").orderBy("value_total", OlbiusQuery.DESC).limit(10);
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(queryChart == null) {
			initQuery();
		}
		return queryChart;
	}
}