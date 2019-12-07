package com.olbius.basehr.report.insurance.query;

import java.sql.Date;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.basehr.insurance.helper.InsuranceHelper;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.OlbiusReturnResultCallback;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class InsBenefitSicknessPregnancyImpl extends OlbiusBuilder{
	private OlbiusQuery query;
	private Locale locale;
	
	public InsBenefitSicknessPregnancyImpl(Delegator delegator, Locale locale) {
		super(delegator);
		// TODO Auto-generated constructor stub
		this.locale = locale;
	}

	private void initQuery(){
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		query.from("party_insurance_fact", "pif")
		.select("pif.empl_leave_dim_id")
		.select("pd.party_id")
		.select("COALESCE( pd.last_name, '' ) || ' ' || COALESCE( pd.middle_name, '' ) || ' ' || COALESCE( pd.first_name, '' )", "cus1_name")
		.select("iabtd.benefit_type_id")
		.select("iabtd.description")
		.select("pif.insurance_social_nbr")
		.select("pif.insurance_salary")
		.select("dd.date_value","from_date")
		.select("dd1.date_value", "thru_date")
		.select("pif.accumulated_leave")
		.select("pif.total_day_leave")
		.select("pif.allowance_amount")
		.select("pif.comment")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pif.party_dim_id = pd.dimension_id")
		.join(Join.INNER_JOIN, "ins_allowance_benefit_type_dim", "iabtd", "pif.benefit_type_dim_id = iabtd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "pif.from_date_leave_dim = dd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd1", "pif.thru_date_leave_dim = dd1.dimension_id")
		.join(Join.INNER_JOIN, "ins_alw_benef_class_typ_dim", "iabctd", "pif.benefit_class_type_dim_id = iabctd.dimension_id")
		.where(condition);
		
		condition.and("dd.date_value", "<=", getSqlDate(thruDate));
		condition.and("dd1.date_value", ">=", getSqlDate(fromDate));
		condition.andEQ("iabctd.benefit_class_type_id", "SICKNESS_PREGNANCY");
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
		addDataField("partyName", "cus1_name");
		addDataField("insSocialNbr", "insurance_social_nbr");
		addDataField("insuranceSalary","insurance_salary");
		addDataField("insParticipatePeriod", new OlbiusReturnResultCallback<String>("from_date", "party_id") {

			@Override
			public String get(Map<String, Object> map) {
				GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
				Date fromDate =(Date) map.get("from_date");
				String partyId = (String) map.get("party_id");
				String insParticipatePeriod = "";
				try {
					insParticipatePeriod = InsuranceHelper.getDescInsParticipatePeriod(delegator, partyId, fromDate, locale);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// TODO Auto-generated method stub
				return insParticipatePeriod;
			}
		});
		addDataField("totalDayLeave", "total_day_leave");
		addDataField("accumulatedLeave", "accumulated_leave");
		addDataField("allowanceAmount", "allowance_amount");
		addDataField("comment", "comment");
		addDataField("fromDate", "from_date");
		addDataField("thruDate", "thru_date");
		addDataField("description", "description");
	}
}
