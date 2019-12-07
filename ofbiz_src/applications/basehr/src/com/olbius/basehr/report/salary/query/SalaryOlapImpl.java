package com.olbius.basehr.report.salary.query;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.payroll.worker.PayrollWorker;
import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalaryOlapImpl extends AbstractOlap{
	public static final String PAYROLLTABLE = "PAYROLLTABLE";
	public static final String EMPLOYEE = "EMLOYEE";
	public static final String FROMDATE = "FROMDATE";
	private OlbiusQuery query;
	
	private Locale locale;
	private TimeZone timeZone;
	private DispatchContext ctx;
	
	public SalaryOlapImpl(Locale locale, TimeZone timeZone, DispatchContext ctx){
		this.locale = locale;
		this.timeZone = timeZone;
		this.ctx = ctx;
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		List<Object> employeeId = (List<Object>) getParameter(EMPLOYEE);
		
		query.distinct();
		query.from("payroll_fact", "pf")
		.select("pf.payroll_table_id")
		.select("pd.party_id","partyId")
		.select("COALESCE( pd.last_name, '' ) || ' ' || COALESCE( pd.middle_name, '' ) || ' ' || COALESCE( pd.first_name, '' )", "cus1_name")
		.select("pd1.party_id","departId")
		.select("dd.date_value", "fromDate")
		.select("dd1.date_value", "thruDate")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pf.party_dim_id = pd.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd1", "pf.department_dim_id = pd1.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "pf.from_date_dim = dd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd1", "pf.thru_date_dim = dd1.dimension_id")
		.where(condition);
		
		if(UtilValidate.isNotEmpty(employeeId)){
			condition.and(Condition.makeIn("pd.party_id", employeeId));
		}
		condition.and(Condition.makeEQ("dd.date_value", getSqlDate(fromDate)));
		
	}
	@Override
	protected OlapQuery getQuery() {
		if(query == null){
			initQuery();
		}
		return query;
	}
	
	public class getData extends ReturnResultGrid{
		public getData(){
			addDataField("LUONG_CO_BAN");
			addDataField("LUONG_THUC_TE");
			addDataField("LUONG_THEM_GIO");
			addDataField("PHU_CAP");
			addDataField("THUONG");
			addDataField("KHAU_TRU");
			addDataField("LUONG_THUC_LINH");
			addDataField("partyId");
			addDataField("partyName");
		}
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = FastMap.newInstance();
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			try {
				String partyId = result.getString("partyId");
				String departId = result.getString("departId");
				String payrollTableId = result.getString("payroll_table_id");
				List<GenericValue> payrollRecordCode = delegator.findByAnd("PayrollTableCode", UtilMisc.toMap("payrollTableId", payrollTableId), UtilMisc.toList("code"), false);
				List<String> payrollRecordCodeList = EntityUtil.getFieldListFromEntityList(payrollRecordCode, "code", true);
				Timestamp fromDate = result.getTimestamp("fromDate");
				Timestamp thruDate = result.getTimestamp("thruDate");
				EntityCondition commonConds = EntityCondition.makeCondition(EntityCondition.makeCondition("code", EntityOperator.IN, payrollRecordCodeList),
						EntityJoinOperator.AND,
						EntityCondition.makeCondition("code", EntityJoinOperator.NOT_IN, UtilMisc.toList("TI_LE_HUONG_LUONG", "TI_LE_TRO_CAP")));
				
				List<GenericValue> listAllFormula = delegator.findList("PayrollFormula", commonConds, null, null, null, false);
				
				String emplTimeSheetId = PayrollWorker.getEmplTimesheetByPayrollTable(delegator, payrollTableId);
				Map<String, Object> tempMap = PayrollWorker.getPayrollTableRecordOfPartyInfo(ctx, payrollTableId, emplTimeSheetId, partyId, departId, 
						locale, timeZone, fromDate, thruDate, listAllFormula);
				map.put("LUONG_CO_BAN", tempMap.get("LUONG_CO_BAN"));
				map.put("LUONG_THUC_TE", tempMap.get("LUONG_CO_BAN_payrollItemType"));
				map.put("LUONG_THEM_GIO", tempMap.get("LUONG_THEM_GIO_payrollItemType"));
				map.put("PHU_CAP", tempMap.get("PHU_CAP_payrollItemType"));
				map.put("THUONG", tempMap.get("THUONG_payrollItemType"));
				map.put("KHAU_TRU", tempMap.get("DEDUCTION_payrollChar"));
				map.put("LUONG_THUC_LINH", tempMap.get("realSalaryPaid"));
				map.put("partyId", partyId);
				map.put("partyName", result.getString("cus1_name"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}
		
	}
}
