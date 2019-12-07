package com.olbius.basehr.report.salary.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import com.olbius.basehr.util.date.FactoryReportDate;
import com.olbius.basehr.util.date.ReportDate;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class CodeSalarySumOlapImpl extends OlbiusBuilder{
	
	public CodeSalarySumOlapImpl(Delegator delegator) {
		super(delegator);
		// TODO Auto-generated constructor stub
	}

	private OlbiusQuery query;
	
	private List<Map<String, String>> getPayrollItemTypeList(){
		List<Map<String, String>> retList = new ArrayList<Map<String,String>>();
		return retList;
	}
	
	protected void initQuery() {
		//Get Query Object
		query = new OlbiusQuery(getSQLProcessor());
		OlbiusQuery incomeQuery = new OlbiusQuery();
		//Create Object Condition
		Condition condition = new Condition();
		//Get parameters
		String customTime = (String) getParameter("customTime");
		
		ReportDate reportDate = FactoryReportDate.getReportDate(customTime);
		reportDate.setFromDate(getSqlDate(fromDate));
		reportDate.setThruDate(getSqlDate(thruDate));
		
		condition.and("dd.date_value ", ">=", reportDate.getFromDate());
		condition.and("dd1.date_value ", "<=", reportDate.getThruDate());
			
		incomeQuery.select("payroll_table_id", "party_dim_id");
		List<Map<String, String>> payrollItemTypeList = getPayrollItemTypeList();
		for(Map<String, String> payrollItemType: payrollItemTypeList){
			String payrollItemTypeId = payrollItemType.get("payrollItemTypeId");
			incomeQuery.select("sum(case when (item.payroll_item_type_id = '" + payrollItemTypeId 
					+ "' OR item.parent_type_id = '" + payrollItemTypeId + "') then pf.amount else 0 end)", payrollItemTypeId + "_item");
		}
		incomeQuery.from("payroll_fact", "pf")
		.join(Join.INNER_JOIN, "payroll_item_type_dimension", "item", "pf.payroll_item_type_dim_id = item.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "pf.from_date_dim = dd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd1", "pf.thru_date_dim = dd1.dimension_id")
		.groupBy("pf.payroll_table_id, pf.party_dim_id");
		//Query data
		query.distinct();
		query.select("income.*")
		.select("COALESCE(pty.last_name, '') || ' ' || COALESCE(pty.middle_name, '') || ' ' || COALESCE(pty.first_name, '') AS full_name")
		.select("pty.party_code")
		.from(incomeQuery, "income")
		.join(Join.INNER_JOIN, "party_dimension", "pty", "pf.party_dim_id = pty.dimension_id");
		
		
		query.select("dd.date_value", "fromDate")
		.select("dd1.date_value", "thruDate")
		.select("pf.name", "name")
		.select("sum(pf.value)", "value")
		.select("sd.description", "description")
		.from("payroll_fact", "pf")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pf.party_dim_id = pd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "pf.from_date_dim = dd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd1", "pf.thru_date_dim = dd1.dimension_id")
		.join(Join.INNER_JOIN, "status_dimension", "sd", "pf.status_dim_id = sd.dimension_id")
		.where(condition)
		.groupBy("dd.date_value", "dd1.date_value", "pf.name", "sd.description");
		
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
		addDataField("fromDate", "fromDate");
		addDataField("thruDate", "thruDate");
		addDataField("name", "name");
		addDataField("value", "value");
		addDataField("description", "description");
	}
}