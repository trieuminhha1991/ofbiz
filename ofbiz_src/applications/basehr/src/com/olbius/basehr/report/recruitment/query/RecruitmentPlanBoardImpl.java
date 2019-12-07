package com.olbius.basehr.report.recruitment.query;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class RecruitmentPlanBoardImpl extends OlbiusBuilder{
	public RecruitmentPlanBoardImpl(Delegator delegator) {
		super(delegator);
	}

	public static final String RECUIT = "RECRUIT";
	
	private OlbiusQuery query;
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		List<Object> recruitmentPlanId = (List<Object>) getParameter(RECUIT);
		
		query.distinct();
		query.from("recruitment_fact", "rf")
		.select("rpd.recruitment_plan_id")
		.select("rpd.recruitment_plan_name")
		.select("pd.party_id")
		.select("COALESCE( pd.last_name, '' ) || ' ' || COALESCE( pd.middle_name, '' ) || ' ' || COALESCE( pd.first_name, '' )", "party_name")
		.select("rf.job_title")
		.select("rf.role_description")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "rf.plan_board_id = pd.dimension_id")
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
		addDataField("recruitment_plan_name","recruitment_plan_name");
		addDataField("party_id","party_id");
		addDataField("party_name","party_name");
		addDataField("job_title","job_title");
		addDataField("role_description","role_description");
	}
	
}
