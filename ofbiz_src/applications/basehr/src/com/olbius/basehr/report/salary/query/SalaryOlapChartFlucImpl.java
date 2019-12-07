package com.olbius.basehr.report.salary.query;

import java.util.List;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalaryOlapChartFlucImpl extends OlbiusBuilder {

	public static final String DEPATMENTID = "DEPATMENTID";
	public static final String FROMDATE = "FROMDATE";
	public static final String ROOT = "ROOT";
	private OlbiusQuery query;
	
	public SalaryOlapChartFlucImpl(Delegator delegator) {
		super(delegator);
	}
	
	private void initQuery(){
		query = OlbiusQuery.make(getSQLProcessor());
		Condition condition = new Condition();
		String departId = (String) getParameter(DEPATMENTID);
		String rootOrg = (String) getParameter(ROOT);
		List<Object> fromDateList = (List<Object>) getParameter(FROMDATE);

		query.select("dd.year_name")
		.select("sum(pf.value)", "quantity")
		.select("pd.party_id")
		.from("payroll_fact", "pf")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pf.org_dim_id = pd.dimension_id", departId.equals(rootOrg))
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pf.department_dim_id = pd.dimension_id", !departId.equals(rootOrg))
		.join(Join.INNER_JOIN, "date_dimension", "dd", "pf.from_date_dim = dd.dimension_id")
		.groupBy("pd.party_id")
		.groupBy("dd.year_name")
		.where(condition);
		
		condition.andEQ("pd.party_id", departId);
		condition.andEQ("pf.code", "LUONG_THUC_TE");
		condition.andIn("dd.year_name", fromDateList);
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
	}
	
	@Override
	public void prepareResultChart() {
		addXAxis("year_name");
		addYAxis("quantity");
	}
}
