package com.olbius.basehr.report.recruitment.query;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class RecruitmentCostImpl extends OlbiusBuilder{

	public static final String RECRUIT = "RECRUIT";
	private OlbiusQuery query;

	@SuppressWarnings("unchecked")
	private void initQuery(){
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		List<Object> recruitmentPlanId = (List<Object>) getParameter(RECRUIT);
		
		query.distinct();
		query.from("recruitment_fact", "rf")
		.select("rpd.recruitment_plan_name")
		.select("rpd.recruitment_plan_id")
		.select("rf.recruit_cost_item_name")
		.select("rf.amount")
		.select("rf.recruit_cost_comment")
		.join(Join.INNER_JOIN, "recruitment_plan_dimension", "rpd", "rf.recruitment_plan_id = rpd.dimension_id")
		.where(condition);
		
		if(UtilValidate.isNotEmpty(recruitmentPlanId)){
			condition.andIn("rpd.recruitment_plan_id", recruitmentPlanId);
		}
	}
	public RecruitmentCostImpl(Delegator delegator) {
		super(delegator);
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
		addDataField("planName","recruitment_plan_name");
		addDataField("costName","recruit_cost_item_name");
		addDataField("amount","amount");
		addDataField("comment","recruit_cost_comment");
	}
}
