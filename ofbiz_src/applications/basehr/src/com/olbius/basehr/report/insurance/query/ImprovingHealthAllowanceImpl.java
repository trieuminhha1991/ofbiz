package com.olbius.basehr.report.insurance.query;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ImprovingHealthAllowanceImpl extends OlbiusBuilder{

	public static final String MONTH = "MONTH";
	public static final String YEAR = "YEAR";
	
	private OlbiusQuery query;
	
	public ImprovingHealthAllowanceImpl(Delegator delegator) {
		super(delegator);
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		query = OlbiusQuery.make(getSQLProcessor());
		Condition cond = new Condition();
		List<Object> listYear = (List<Object>) getParameter(YEAR);
		List<Object> listMonth = (List<Object>) getParameter(MONTH);
		
		OlbiusQuery query1 = OlbiusQuery.make(getSQLProcessor());
		
		query1.from("party_insurance_fact", "pif")
		.select("dd.year_name")
		.select("dd.month_name")
		.select("SUM( pif.allowance_amount )", "sum")
		.join(Join.INNER_JOIN, "custom_time_period_dimension", "ctpd", "pif.custom_time_period_dim_id = ctpd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "ctpd.from_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "ins_alw_benef_class_typ_dim", "iabctd", "pif.benefit_class_type_dim_id = iabctd.dimension_id")
		.groupBy("dd.year_name")
		.groupBy("dd.month_name")
		.where(cond);
		
		if(UtilValidate.isNotEmpty(listMonth)){
			cond.andIn("dd.month_of_year", listMonth);
		}
		if(UtilValidate.isNotEmpty(listYear)){
			cond.andIn("dd.year_name", listYear);
		}
		cond.andEQ("iabctd.benefit_class_type_id", "HEALTH_IMPROVEMENT");
		
		query.from(query1, "tmp")
		.select("tmp.year_name")
		.select("sum(tmp.sum)", "total")
		.groupBy("tmp.year_name");
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
		addYAxis("total");
	}
}
