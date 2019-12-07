package com.olbius.basehr.report.recruitment.query;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlbiusQueryInterface;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class RecruitmentOlapImpl extends OlbiusBuilder{
	public static final String RECRUIT = "RECRUIT";
	public static final String STATUS = "STATUS";
	private OlbiusQuery query;
	
	public RecruitmentOlapImpl(Delegator delegator) {
		super(delegator);
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		List<Object> recruitmentPlanId = (List<Object>) getParameter(RECRUIT);
		List<Object> statusId_list = (List<Object>) getParameter(STATUS);
		
		query.distinct();
		query.from("recruitment_fact", "rf")
		.select("rpd.recruitment_plan_id")
		.select("rpd.recruitment_plan_name")
		.select("pd.party_id", "party_id")
		.select("COALESCE( pd.last_name, '' ) || ' ' || COALESCE( pd.middle_name, '' ) || ' ' || COALESCE( pd.first_name, '' )", "candidate_name")
		.select("pd.gender")
		.select("pd.phone_number", "phone")
		.select("pd.primary_address", "address")
		.select("dd.date_value", "dateOfBirth")
		.select("sd.status_id", "status_id")
		.select("sd.description", "description")
		.select("avg(rf.point)", "point")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "rf.party_dim_id = pd.dimension_id")
		.join(Join.LEFT_OUTER_JOIN, "date_dimension", "dd", "pd.birth_date_dim_id = dd.dimension_id")
		.join(Join.INNER_JOIN, "status_dimension", "sd", "rf.status_recruit_dim_id = sd.dimension_id")
		.join(Join.INNER_JOIN, "recruitment_plan_dimension", "rpd", "rf.recruitment_plan_dim_id = rpd.dimension_id")
		.groupBy("rpd.recruitment_plan_id")
		.groupBy("rpd.recruitment_plan_name")
		.groupBy("pd.party_id")
		.groupBy("pd.gender")
		.groupBy("pd.phone_number")
		.groupBy("pd.primary_address")
		.groupBy("dd.date_value")
		.groupBy("sd.status_id")
		.groupBy("sd.description")
		.groupBy("pd.last_name")
		.groupBy("pd.middle_name")
		.groupBy("pd.first_name")
		.where(condition);
		
		if(UtilValidate.isNotEmpty(recruitmentPlanId)){
			condition.andIn("rpd.recruitment_plan_id", recruitmentPlanId);
		}
		condition.andIn("sd.status_id", statusId_list);
	}
	@Override
	protected OlbiusQueryInterface getQuery() {
		if(query == null){
			initQuery();
		}
		return query;
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("recruitmentPlanId", "recruitment_plan_id");
		addDataField("recruitmentPlanName", "recruitment_plan_name");
		addDataField("partyId", "party_id");
		addDataField("partyName", "candidate_name");
		addDataField("gender");
		addDataField("phoneNumber", "phone");
		addDataField("address", "address");
		addDataField("dateOfBirth");
		addDataField("statusId", "status_id");
		addDataField("description", "description");
		addDataField("point", "point");
	}
}
