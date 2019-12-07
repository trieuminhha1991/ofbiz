package com.olbius.basehr.report.salary.query;

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

public class OrgSalarySumOlapImpl  extends OlbiusBuilder{
	private OlbiusQuery query;
	private  List<Map<String, String>> payrollItemTypeList;
	private List<Map<String, String>> payrollFormulaByCharList;
	public OrgSalarySumOlapImpl(Delegator delegator) {
		super(delegator);
		ResultPayrollItemType result = new ResultPayrollItemType(getSQLProcessor(), "Y");
		payrollItemTypeList = result.getListPayrollItemType();
		ResultPayrollFormulaByChar resultPayrollFormulaByChar = new ResultPayrollFormulaByChar(getSQLProcessor(), "ORG_PAID");
		payrollFormulaByCharList = resultPayrollFormulaByChar.getListPayrollFormulaByChar();
	}

	protected void initQuery() {
		query = new OlbiusQuery(getSQLProcessor());
		//Condition relCondition = new Condition();
		//Get parameters
		String depId = (String)getParameter("organization");
		String parentId = (String)getParameter("parentId");
		String customTime = (String)getParameter("customTime");
		
		ReportDate reportDate = FactoryReportDate.getReportDate(customTime);
		reportDate.setFromDate(getSqlDate(fromDate));
		reportDate.setThruDate(getSqlDate(thruDate));
		
		query.distinct();
		query.select("orgRel.parent_dim_id", "party_dim_id")
		.select("pd_parent.party_id", "party_id")
		.select("pd_parent.party_code", "party_code")
		.select("totalChild.from_date",  "from_date")
		.select("totalChild.thru_date", "thru_date")
		.select("pd_parent.name", "department_name")
		.from("party_internal_org_rel_dim as orgRel");
		if(depId != null && parentId == null){
			query.where(Condition.make("pd_parent.party_id", Condition.EQ, depId));
		}else if(parentId != null){
			Condition whereCond = Condition.make("dd1.date_value ", "<=", reportDate.getThruDate());
			whereCond.and(Condition.make("dd2.date_value ", ">=", reportDate.getFromDate()).or("dd2.date_value is null"));
			whereCond.and("ptyRel.is_direct_child", Condition.EQ, "Y")
			.and("parent.party_id", Condition.EQ, parentId);
			
			OlbiusQuery allChildQuery = new OlbiusQuery();
			allChildQuery.select("parent.party_id", "parent_party_id")
			.select("child.party_id", "child_party_id")
			.select("ptyRel.parent_dim_id", "parent_dim_id")
			.select("ptyRel.dimension_id", "child_dim_id")
			.from("party_internal_org_rel_dim", "ptyRel")
			.join(Join.INNER_JOIN, "party_dimension", "parent", "ptyRel.parent_dim_id = parent.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "child", "ptyRel.dimension_id = child.dimension_id")
			.join(Join.INNER_JOIN, "date_dimension", "dd1", "dd1.dimension_id = ptyRel.from_date_dim_id")
			.join(Join.INNER_JOIN, "date_dimension", "dd2", "dd2.dimension_id = ptyRel.thru_date_dim_id")
			.where(whereCond);
			query.join(Join.INNER_JOIN, allChildQuery, "listChild", "listChild.child_dim_id = orgRel.parent_dim_id");
		}
		query.join(Join.INNER_JOIN, "party_dimension", "pd_parent", "orgRel.parent_dim_id = pd_parent.dimension_id");
		
		Condition payrollCond = Condition.make("dd1.date_value", "<=" , reportDate.getThruDate());
		payrollCond.and("dd2.date_value", ">=", reportDate.getFromDate());
		OlbiusQuery payrollQuery = new OlbiusQuery();
		payrollQuery.select("pfact.payroll_table_id", "payroll_table_id")
		.select("pfact.department_dim_id", "department_dim_id")
		.select("ptyGroup.name", "department_name")
		.select("dd1.date_value", "from_date")
		.select("dd2.date_value", "thru_date")
		.select("sum(case when (pfd.payroll_characteristic_id = 'INCOME') then pfact.amount else 0 end)", "total_income")
		.select("sum(case when (pfd.payroll_characteristic_id = 'ORG_PAID') then pfact.amount else 0 end)", "org_paid")
		.from("payroll_fact", "pfact")
		.join(Join.INNER_JOIN, "payroll_formula_dimension", "pfd", "pfact.code_dim_id = pfd.dimension_id")
		.join(Join.LEFT_OUTER_JOIN, "payroll_item_type_dimension", "item", "pfd.payroll_item_type_id = item.payroll_item_type_id")
		//.join(Join.INNER_JOIN, "payroll_characteristic_dimension", "characteristic", "characteristic.payroll_characteristic_id = pfd.payroll_characteristic_id")
		.join(Join.INNER_JOIN, "party_dimension", "ptyGroup", "pfact.department_dim_id = ptyGroup.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd1", "pfact.from_date_dim = dd1.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd2", "pfact.thru_date_dim = dd2.dimension_id")
		.where(payrollCond)
		.groupBy("pfact.payroll_table_id, pfact.department_dim_id, ptyGroup.name")
		.groupBy("dd1.date_value")
		.groupBy("dd2.date_value");
		
		for(Map<String, String> payrollItemType: payrollItemTypeList){
			String payrollItemTypeId = payrollItemType.get("payroll_item_type_id");
			String alias = payrollItemTypeId + "_item";
			payrollQuery.select("sum(case when (item.payroll_item_type_id = '" + payrollItemTypeId 
					+ "' OR item.parent_type_id = '" + payrollItemTypeId + "') then pfact.amount else 0 end)", alias);
			query.select("sum(totalChild." + alias + ")", alias);
		}
		for(Map<String, String> payrollFormulaByChar: payrollFormulaByCharList){
			String code = payrollFormulaByChar.get("code");
			String alias = code + "_code";
			payrollQuery.select("sum(case when (pfd.code = '" + code + "') then pfact.amount else 0 end)", alias);
			query.select("sum(totalChild." + alias + ")", alias);
		}
		query.select("sum(totalChild.total_income)", "total_income")
		.select("sum(totalChild.org_paid)", "org_paid");
		query.join(Join.INNER_JOIN, payrollQuery, "totalChild", "totalChild.department_dim_id = orgRel.dimension_id");
		query.groupBy("orgRel.parent_dim_id")
		.groupBy("pd_parent.party_id")
		.groupBy("pd_parent.party_code")
		.groupBy("pd_parent.name")
		.groupBy("totalChild.from_date")
		.groupBy("totalChild.thru_date");
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
		addDataField("partyId", "party_id");
		addDataField("fromDate", "from_date");
		addDataField("thruDate", "thru_date");
		addDataField("departmentName", "department_name");
		addDataField("partyCode", "party_code");
		addDataField("totalIncome", "total_income");
		addDataField("totalOrgPaid", "org_paid");
		for(Map<String, String> payrollItemType: payrollItemTypeList){
			String payrollItemTypeId = payrollItemType.get("payroll_item_type_id");
			addDataField(payrollItemTypeId + "_item", payrollItemTypeId + "_item");
		}
		for(Map<String, String> payrollFormulaByChar: payrollFormulaByCharList){
			String code = payrollFormulaByChar.get("code");
			addDataField(code + "_code", code + "_code");
		}
	}
}
