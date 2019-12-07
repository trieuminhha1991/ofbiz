package com.olbius.crm.report;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.ReturnResultChartInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class TopCallerChartImpl extends OlbiusBuilder {
	public TopCallerChartImpl(Delegator delegator) {
		super(delegator);
	}

	public static final String ORG = "ORG";
	public static final String STATUS_CALL = "STATUS_CALL";
	public static final String QUANTITY = "QUANTITY";

	private OlbiusQuery query;
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			addXAxis("caller_id");
			addYAxis("times");
			String description = (String) getParameter(QUANTITY);
			((ReturnResultChartInterface) getOlapResult().getResultQuery()).setSeriesDefaultName(description);
		}
	}

	private void initQuery() {
		query = OlbiusQuery.make(getSQLProcessor());
		String statusCall = (String) getParameter(STATUS_CALL);
		String organization = (String) getParameter(ORG);
		Condition condition = new Condition();
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.andEQ("organ.party_id", organization);
		
		query.select("caller.party_id", "caller_id").select("COALESCE(caller.last_name, '') || ' ' || COALESCE(caller.middle_name, '') || ' ' || COALESCE(caller.first_name, '')", "caller_name")
		.select("count(communication_event_id)", "times")
		.from("communication_event_fact")
		.join(Join.INNER_JOIN, "party_dimension", "caller", "caller.dimension_id = communication_event_fact.party_from_dim_id")
		.join(Join.INNER_JOIN, "date_dimension", "communication_event_fact.entry_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_person_relationship", "caller.dimension_id = party_person_relationship.person_dim_id")
		.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id")
		.where(condition).groupBy("caller.party_id").groupBy("caller_name");
		if(statusCall.equals("C")){
			query.orderBy("times", OlbiusQuery.DESC);
		}else{
			query.orderBy("times", OlbiusQuery.ASC);
		}
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	}
}
