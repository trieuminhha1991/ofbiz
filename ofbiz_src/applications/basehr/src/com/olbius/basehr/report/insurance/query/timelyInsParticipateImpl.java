package com.olbius.basehr.report.insurance.query;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.OlbiusReturnResultCallback;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class timelyInsParticipateImpl extends OlbiusBuilder{
	
	public static final String STATUS = "STATUS";
	public static final String ORG = "ORG";
	private OlbiusQuery query;
	
	public timelyInsParticipateImpl(Delegator delegator) {
		super(delegator);
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		query = OlbiusQuery.make(getSQLProcessor());
		Condition condition = new Condition();
		String status = (String) getParameter(STATUS);
		List<Object> org = (List<Object>) getParameter(ORG);
		
		query.from("person_insurance_fact", "p")
		.select("pd.party_id")
		.select("COALESCE( pd.last_name, '' ) || ' ' || COALESCE( pd.middle_name, '' ) || ' ' || COALESCE( pd.first_name, '' )", "cus1_name")
		.select("dd.date_value")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "p.party_dim_id = pd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "p.date_participate_ins_dim = dd.dimension_id")
		.where(condition);
		
		if(UtilValidate.isNotEmpty(status)){
			if(status.equals("b")){
				condition.and("dd.date_value", "<=", getSqlDate(fromDate));
			}else{
				condition.and("dd.date_value", ">=", getSqlDate(fromDate));
			}
		}
		
		if(UtilValidate.isNotEmpty(org)){
			condition.andIn("pd.party_id", org);
		}
	};
	
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null){
			initQuery();
		}
		return query;
	}
	
	@Override
	public void prepareResultChart() {
		addXAxis("party_id");
		addYAxis("date_value");
	}
}
