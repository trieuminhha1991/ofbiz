package com.olbius.basehr.report.recruitment.query;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class RecruitmentAnalysisCostImpl extends OlbiusBuilder{
	public RecruitmentAnalysisCostImpl(Delegator delegator) {
		super(delegator);
	}

	public static final String RECRUIT = "RECRUIT";
	public static final String RECRUITCOSTCAT = "RECRUITCOSTCAT";
	
	private OlbiusQuery query;
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		query = OlbiusQuery.make(getSQLProcessor());
		Condition condition = new Condition();
		List<Object> recuitmentPlanId = (List<Object>) getParameter(RECRUIT);
		List<Object> recruitCostCat = (List<Object>) getParameter(RECRUITCOSTCAT);
		OlbiusQuery query2 = OlbiusQuery.make(getSQLProcessor());
		
		query2.from("recruitment_fact", "rf")
		.select("rpd.recruitment_plan_id")
		.select("rf.recruit_cost_cat_type_id")
		.select("rf.recruit_cost_cat_name")
		.select("rf.recruit_cost_item_type_id")
		.select("rf.recruit_cost_item_name")
		.select("rf.amount")
		.join(Join.INNER_JOIN, "recruitment_plan_dimension", "rpd", "rf.recruitment_plan_id = rpd.dimension_id")
		.groupBy("rpd.recruitment_plan_id")
		.groupBy("rf.recruit_cost_cat_type_id")
		.groupBy("rf.recruit_cost_cat_name")
		.groupBy("rf.recruit_cost_item_type_id")
		.groupBy("rf.recruit_cost_item_name")
		.groupBy("rf.amount")
		.where(condition);
		
		if(UtilValidate.isNotEmpty(recuitmentPlanId)){
			condition.andIn("rpd.recruitment_plan_id", recuitmentPlanId);
		}
		
		if(UtilValidate.isNotEmpty(recruitCostCat)){
			condition.andIn("rf.recruit_cost_cat_type_id", recruitCostCat);
		}
		
		query.from(query2, "tmp")
		.select("tmp.recruit_cost_item_name")
		.select("tmp.recruit_cost_cat_type_id")
		.select("sum(tmp.amount)", "total")
		.select("tmp.recruit_cost_cat_name")
		.groupBy("tmp.recruit_cost_item_name")
		.groupBy("tmp.recruit_cost_cat_type_id")
		.groupBy("tmp.recruit_cost_cat_name");
	}
	@Override
	protected OlapQuery getQuery() {
		if(query == null){
			initQuery();
		}
		return query;
	}
	
	@Override
	public void prepareResultChart() {
		addXAxis("recruit_cost_cat_name");
		addSeries("recruit_cost_item_name");
		addYAxis("total");
	}
	
}
