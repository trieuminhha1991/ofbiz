package com.olbius.salesmtl.report;


import java.util.List;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class EvaluateSalesman extends OlbiusBuilder{
	public static final String SALESMAN_ID = "SALESMAN_ID";

	public EvaluateSalesman(Delegator delegator) {
		super(delegator);
	}
	
	private OlbiusQuery query;
	private OlbiusQuery fromQuery;
	private OlbiusQuery joinQuery;
	private OlbiusQuery join2;
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			addSeries("de2");
			addXAxis("sm3");
			addYAxis("value2");
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		Condition fromCondition = new Condition();
		Condition joinCondition = new Condition();
		List<Object> salesmanId = (List<Object>) getParameter(SALESMAN_ID);
		
		query = new OlbiusQuery(getSQLProcessor());
		fromQuery = new OlbiusQuery(getSQLProcessor());
		joinQuery = new OlbiusQuery(getSQLProcessor());
		join2 = new OlbiusQuery(getSQLProcessor());
		
		
		fromCondition.andIn("pd.party_id", salesmanId).andEQ("sfd.type", "SALES_OUT").andBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		joinCondition.andIn("pd2.party_id", salesmanId);
		
		fromQuery.select("pd.party_code", "sm").select("(case when sum( sfd.target_value ) is null then 0 else sum( sfd.target_value ) end)", "value_total")
		.select("cast('target' as text)", "de")
		.select("dd.year_and_month", "ynm")
		.from("sales_forecast_dimension", "sfd")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sfd.from_date_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = sfd.internal_party_dim_id")
		.where(fromCondition)
		.groupBy("pd.party_code").groupBy("dd.year_and_month")
		.orderBy("dd.year_and_month");
		
		join2.select("*").from("date_dimension").where(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		
		joinQuery.select("pd2.party_code", "sm")
		.select("(case when sum( s1.total ) is null then 0 else sum( s1.total ) end)", "value_total")
		.select("dd2.year_and_month", "ynm").select("cast('actual' as text)", "de")
		.from("sales_order_fact", "s1")
		.join(Join.INNER_JOIN, join2, "dd2", "dd2.dimension_id = s1.order_date_dim_id and s1.order_status = 'ORDER_COMPLETED'")
		.join(Join.RIGHT_OUTER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = s1.sale_executive_party_dim_id")
		.where(joinCondition)
		.groupBy("pd2.party_code").groupBy("dd2.year_and_month").orderBy("dd2.year_and_month");
		
		query.select("COALESCE(a1.sm, a2.sm)", "sm3")
		.select("COALESCE(a1.value_total, a2.value_total)", "value2")
		.select("COALESCE(a1.ynm, a2.ynm)", "year_and_month")
		.select("COALESCE(a1.de, a2.de)", "de2")
		.from(fromQuery, "a1")
		.join(Join.FULL_OUTER_JOIN, joinQuery, "a2", "1=0")
		.groupBy("year_and_month").groupBy("a1.value_total").groupBy("a2.value_total").groupBy("a1.sm").groupBy("a2.sm").groupBy("a1.de")
		.groupBy("a2.de")
		.orderBy("year_and_month");
	}
}
