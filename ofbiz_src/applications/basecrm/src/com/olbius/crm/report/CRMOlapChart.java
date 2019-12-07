package com.olbius.crm.report;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class CRMOlapChart extends OlbiusBuilder {
	public CRMOlapChart(Delegator delegator) {
		super(delegator);
	}
	
	public static final String LIMITT = "LIMITT";
	public static final String SORTT = "SORTT";
	public static final String ORG = "ORG";
	
	private OlbiusQuery query;
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapPieChart) {
			addXAxis("product_code");
			addYAxis("quantity");
		}
	}
	
	private void initQuery() {
		query = OlbiusQuery.make(getSQLProcessor());
		Long limit = (Long) getParameter(LIMITT);
		String organ = (String) getParameter(ORG);
		Boolean sort = (Boolean) getParameter(SORTT);
		
		Condition condition = new Condition();
		condition.and("product_dimension.internal_name is not null")
		.andEQ("organ.party_id", organ)
		.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		
		query.from("communication_event_fact")
		.select("COUNT(communication_event_fact.product_discussed_dim_id)", "quantity")
		.select("product_dimension.internal_name")
		.select("product_dimension.product_code")
		
		.join(Join.INNER_JOIN, "date_dimension", "communication_event_fact.entry_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "communication_event_fact.product_discussed_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "caller", "caller.dimension_id = communication_event_fact.party_from_dim_id")
		.join(Join.INNER_JOIN, "party_person_relationship", "caller.dimension_id = party_person_relationship.person_dim_id")
		.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id")
		.where(condition)
		.groupBy("product_dimension.product_code")
		.groupBy("product_dimension.internal_name");
		String orderType = "ASC";
		if (sort == true){
			orderType = "DESC";
		}
		query.orderBy("quantity", orderType).limit(limit);
		
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	} 
}
