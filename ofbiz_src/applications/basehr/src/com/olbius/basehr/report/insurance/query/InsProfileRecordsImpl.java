package com.olbius.basehr.report.insurance.query;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.OlbiusReturnResultCallback;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class InsProfileRecordsImpl extends OlbiusBuilder{
	public static final String PARTYID = "PARTYID";
	private OlbiusQuery query;
	
	public InsProfileRecordsImpl(Delegator delegator) {
		super(delegator);
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		List<Object> partyId = (List<Object>) getParameter(PARTYID);
		
		query.from("party_insurance_profile_fact", "pipf")
		.select("dd.date_value", "from_date")
		.select("dd1.date_value", "thru_date")
		.select("pd.dimension_id")
		.select("pd.party_id")
		.select("COALESCE( pd.last_name, '' ) || ' ' || COALESCE( pd.middle_name, '' ) || ' ' || COALESCE( pd.first_name, '' )", "cus1_name")
		.select("epf.empl_position_dim_id")
		.select("epd.empl_position_id")
		.select("epd.empl_position_type_id")
		.select("eptd.description")
		.select("pipf.allowance_seniority")
		.select("pipf.allowance_seniority_exces")
		.select("pipf.allowance_position")
		.select("pipf.allowance_salary")
		.select("pipf.allowance_other")
		.select("pipf.amount")
		.select("sum(distinct case when(itd.insurance_type_id = 'BHXH') then pipf.employer_rate else 0 end)", "XHNV")
		.select("sum(distinct case when(itd.insurance_type_id = 'BHXH') then pipf.employee_rate else 0 end)", "XHCT")
		.select("sum(distinct case when(itd.insurance_type_id = 'BHYT') then pipf.employer_rate else 0 end)", "YTNV")
		.select("sum(distinct case when(itd.insurance_type_id = 'BHYT') then pipf.employee_rate else 0 end)", "YTCT")
		.select("sum(distinct case when(itd.insurance_type_id = 'BHTN') then pipf.employer_rate else 0 end)", "TNNV")
		.select("sum(distinct case when(itd.insurance_type_id = 'BHTN') then pipf.employee_rate else 0 end)", "TNCT")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "pipf.from_date_dim = dd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd1", "pipf.thru_date_dim = dd1.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pipf.party_dim_id = pd.dimension_id")
		.join(Join.INNER_JOIN, "empl_position_fact", "epf", "pipf.party_dim_id = epf.party_dim_id")
		.join(Join.INNER_JOIN, "empl_position_dimension", "epd", "epf.empl_position_dim_id = epd.dimension_id")
		.join(Join.INNER_JOIN, "empl_position_type_dimension", "eptd", "epd.empl_position_type_id = eptd.empl_position_type_id")
		.join(Join.INNER_JOIN, "insurance_type_dimension", "itd", "pipf.insurance_type_dim_id = itd.dimension_id")
		.groupBy("dd.date_value")
		.groupBy("dd1.date_value")
		.groupBy("pd.dimension_id")
		.groupBy("pd.party_id")
		.groupBy("epf.empl_position_dim_id")
		.groupBy("epd.empl_position_id")
		.groupBy("epd.empl_position_type_id")
		.groupBy("eptd.description")
		.groupBy("pipf.allowance_seniority")
		.groupBy("pipf.allowance_seniority_exces")
		.groupBy("pipf.allowance_position")
		.groupBy("pipf.allowance_salary")
		.groupBy("pipf.amount")
		.groupBy("pipf.allowance_other")
		.where(condition);
		
		if(UtilValidate.isNotEmpty(partyId)){
			condition.andIn("pd.party_id", partyId);
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
		addDataField("fromDate", "from_date");
		addDataField("thruDate", "thru_date");
		addDataField("partyName", "cus1_name");
		addDataField("emplPositionType", "description");
		addDataField("amount", "amount");
		addDataField("seniority", "allowance_seniority");
		addDataField("seniorityExces", "allowance_seniority_exces");
		addDataField("position", "allowance_position");
		addDataField("salary", "allowance_salary");
		addDataField("other", "allowance_other");
		addDataField("BHXH", new OlbiusReturnResultCallback<Double>("amount", "XHNV", "XHCT") {

			@Override
			public Double get(Map<String, Object> map) {
				double amount = 0;
				if(map.containsKey("amount") && UtilValidate.isNotEmpty(map.get("amount"))){
					amount = ((java.math.BigDecimal) map.get("amount")).doubleValue();
				}
				double x = (double) map.get("XHNV");
				double y = (double) map.get("XHCT");
				
				
				return amount * x + amount * y;
			}
			
		});
		addDataField("BHYT", new OlbiusReturnResultCallback<Double>("amount", "YTNV", "YTCT") {

			@Override
			public Double get(Map<String, Object> map) {
				double amount = 0;
				if(map.containsKey("amount") && UtilValidate.isNotEmpty(map.get("amount"))){
					amount = ((java.math.BigDecimal) map.get("amount")).doubleValue();
				}
				double x = (double) map.get("YTNV");
				double y = (double) map.get("YTCT");
				return amount * x + amount * y;
			}

		});
		
		addDataField("BHTN", new OlbiusReturnResultCallback<Double>("amount", "TNNV", "TNCT") {

			@Override
			public Double get(Map<String, Object> map) {
				double amount = 0;
				if(map.containsKey("amount") && UtilValidate.isNotEmpty(map.get("amount"))){
					amount = ((java.math.BigDecimal) map.get("amount")).doubleValue();
				}
				double x = (double) map.get("TNNV");
				double y = (double) map.get("TNCT");
				return amount * x + amount * y;
			}
			
		});
		
	}
	
}
