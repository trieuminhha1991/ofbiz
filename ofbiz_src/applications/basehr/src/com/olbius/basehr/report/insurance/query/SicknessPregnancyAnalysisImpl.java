package com.olbius.basehr.report.insurance.query;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SicknessPregnancyAnalysisImpl extends OlbiusBuilder{
	public static final String YEAR = "YEAR";
	public static final String MONTH = "MONTH";
	public static final String OPTION = "OPTION";
	
	private static String option;
	private OlbiusQuery query;
	
	public SicknessPregnancyAnalysisImpl(Delegator delegator) {
		super(delegator);
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		query = OlbiusQuery.make(getSQLProcessor());
		Condition cond = new Condition();
		List<Object> listYear = (List<Object>) getParameter(YEAR);
		List<Object> listMonth = (List<Object>) getParameter(MONTH);
		option = (String) getParameter(OPTION);
		
		
		query.from("party_insurance_fact", "pif")
		.select("dd.year_name")
		.select("dd.month_name")
		.select("SUM( pif.allowance_amount )", "total", option.equals("syn"))
		.select("pif.allowance_amount", option.equals("det"))
		.join(Join.INNER_JOIN, "date_dimension", "dd", "pif.from_date_leave_dim = dd.dimension_id")
		.join(Join.INNER_JOIN, "ins_alw_benef_class_typ_dim", "iabctd", "pif.benefit_class_type_dim_id = iabctd.dimension_id")
		.groupBy("dd.year_name", option.equals("syn"))
		.groupBy("dd.month_name", option.equals("syn"))
		.where(cond);
		
		if(UtilValidate.isNotEmpty(listMonth)){
			cond.andIn("dd.month_of_year", listMonth);
		}
		if(UtilValidate.isNotEmpty(listYear)){
			cond.andIn("dd.year_name", listYear);
		}
		cond.andEQ("iabctd.benefit_class_type_id", "SICKNESS_PREGNANCY");
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
		addXAxis("year_name");
		if(option.equals("det")){
			addYAxis("allowance_amount");
			addSeries("month_name");
		}else{
			addYAxis("total");
		}
	}
}
