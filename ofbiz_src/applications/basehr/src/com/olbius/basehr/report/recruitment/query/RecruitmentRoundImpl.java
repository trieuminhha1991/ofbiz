package com.olbius.basehr.report.recruitment.query;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class RecruitmentRoundImpl extends OlbiusBuilder{
	public static final String RECRUIT = "RECRUIT";
	private OlbiusQuery query;
	
	public RecruitmentRoundImpl(Delegator delegator) {
		super(delegator);
	}

	@SuppressWarnings("unchecked")
	private void initQuery(){
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		List<Object> recruitmentPlanId = (List<Object>) getParameter(RECRUIT);
		
		query.distinct();
		query.from("recruitment_fact", "rf")
		.select("rpd.recruitment_plan_id")
		.select("rf.recruitment_plan_name")
		.select("round_order")
		.select("round_name")
		.select("round_comment")
		.join(Join.INNER_JOIN, "recruitment_plan_dimension", "rpd", "rf.recruitment_plan_id = rpd.dimension_id")
		.where(condition);
		
		if(UtilValidate.isNotEmpty(recruitmentPlanId)){
			condition.andIn("rpd.recruitment_plan_id", recruitmentPlanId);
		}
	}
	@Override
	protected OlapQuery getQuery() {
		if(query == null){
			initQuery();
		}
		return query;
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("planName", "recruitment_plan_name");
		addDataField("roundName", "round_name");
		addDataField("roundOrder", "round_order");
		addDataField("roundComment", "round_comment");
	}
}
