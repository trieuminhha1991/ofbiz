package com.olbius.basehr.report.salary.query;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import com.olbius.basehr.util.date.FactoryReportDate;
import com.olbius.basehr.util.date.ReportDate;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalarySumOlapImpl extends OlbiusBuilder{
	private OlbiusQuery query;
	private  List<Map<String, String>> payrollItemTypeList;
	public SalarySumOlapImpl(Delegator delegator, Locale locale) {
		super(delegator);
		ResultPayrollItemType result = new ResultPayrollItemType(getSQLProcessor(), "Y");
		payrollItemTypeList = result.getListPayrollItemType();
	}
	
	private void initQuery(){
		//Get Query Object
		query = new OlbiusQuery(getSQLProcessor());
		OlbiusQuery incomeQuery = new OlbiusQuery();
		//Create Object Condition
		Condition condition = new Condition();
		//Get parameters
		String customTime = (String)getParameter("customTime");
		
		ReportDate reportDate = FactoryReportDate.getReportDate(customTime);
		reportDate.setFromDate(getSqlDate(fromDate));
		reportDate.setThruDate(getSqlDate(thruDate));
		
		condition.and("dd.date_value ", "<=", reportDate.getThruDate());
		condition.and("dd1.date_value ", ">=", reportDate.getFromDate());
		
		incomeQuery.select("payroll_table_id", "party_dim_id", "department_dim_id")
		.select("dd.date_value", "from_date")
		.select("dd1.date_value", "thru_date")
		.select("sum(case when (pfd.payroll_characteristic_id = 'INCOME') then pfact.amount else 0 end) as total_income")
		.select("sum(case when (pfd.payroll_characteristic_id = 'DEDUCTION') then pfact.amount else 0 end) as total_deduction");
		for(Map<String, String> payrollItemType: payrollItemTypeList){
			String payrollItemTypeId = payrollItemType.get("payroll_item_type_id");
			incomeQuery.select("sum(case when (pfd.payroll_item_type_id = '" + payrollItemTypeId 
					+ "' OR item.parent_type_id = '" + payrollItemTypeId + "') then pfact.amount else 0 end)", payrollItemTypeId + "_item");
		}
		incomeQuery.from("payroll_fact", "pfact")
		.join(Join.INNER_JOIN, "payroll_formula_dimension", "pfd", "pfact.code_dim_id = pfd.dimension_id")
		.join(Join.LEFT_OUTER_JOIN, "payroll_item_type_dimension", "item", "pfd.payroll_item_type_id = item.payroll_item_type_id")
		.join(Join.LEFT_OUTER_JOIN, "payroll_characteristic_dimension", "characteristic", "pfd.payroll_characteristic_id = characteristic.payroll_characteristic_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "pfact.from_date_dim = dd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd1", "pfact.thru_date_dim = dd1.dimension_id")
		.where(condition)
		.groupBy("pfact.payroll_table_id")
		.groupBy("pfact.party_dim_id")
		.groupBy("pfact.department_dim_id")
		.groupBy("dd.date_value")
		.groupBy("dd1.date_value");
		
		//Query data
		query.distinct();
		query.select("income.*")
		.select("income.total_income - income.total_deduction", "actual_receive")
		.select("COALESCE(pty.last_name, '') || ' ' || COALESCE(pty.middle_name, '') || ' ' || COALESCE(pty.first_name, '')", "full_name")
		.select("pty.party_code", "party_code")
		.select("pty.party_id", "party_id")
		.select("pty.first_name")
		.select("ptygroup.name","department_name")
		.from(incomeQuery, "income")
		.join(Join.INNER_JOIN, "party_dimension", "pty", "income.party_dim_id = pty.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "ptygroup", "income.department_dim_id = ptygroup.dimension_id")
		.orderBy("pty.first_name");
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
		addDataField("fullName", "full_name");
		addDataField("partyId", "party_id");
		addDataField("partyCode", "party_code");
		addDataField("totalIncome", "total_income");
		addDataField("totalDeduction", "total_deduction");
		addDataField("actualReceive", "actual_receive");
		addDataField("departmentName", "department_name");
		for(Map<String, String> payrollItemType: payrollItemTypeList){
			String payrollItemTypeId = payrollItemType.get("payroll_item_type_id");
			addDataField(payrollItemTypeId + "_item", payrollItemTypeId + "_item");
		}
	}
}
