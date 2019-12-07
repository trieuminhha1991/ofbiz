package com.olbius.acc.report.dashboard.query;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.jdbc.SQLProcessor;

import com.olbius.acc.report.incomestatement.query.IncomeOlapConstant;
import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class IndexAccountingOlapImpl extends TypeOlap implements IncomeOlapConstant{
	public static final String MODULE = IndexAccountingOlapImpl.class.getName();
	
	protected OlbiusQuery query;
	private SQLProcessor processor;
	
	public IndexAccountingOlapImpl(SQLProcessor processor, String organizationPartyId, Delegator delegator){
		Condition cond = new Condition();
		List<Object> listAccountCode = FastList.newInstance();
		listAccountCode.add("511");
		listAccountCode.add("521");
		listAccountCode.add("632");
		query = new OlbiusQuery(processor);
		Date curDate = new Date(System.currentTimeMillis());
		Timestamp curTime = new Timestamp(curDate.getTime());
		Timestamp startMonth = UtilDateTime.getMonthStart(curTime);		

		cond.and(Condition.makeIn("gad.account_code", listAccountCode));
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(startMonth), getSqlDate(curTime)));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		query.from("acctg_trans_fact", "atf")
		.select("gad.account_code", "accountCode")
		.select("sum(atf.amount)", "amount")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")		
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id")
		.join(Join.INNER_JOIN, "gl_account_relationship", "gar", "atf.gl_account_dim_id = gar.dimension_id ")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "gar.parent_dim_id = gad.dimension_id")
		.where(cond)
		.groupBy("account_code")
		.orderBy("account_code", OlbiusQuery.DESC);
	}
	
	public List<Map<String, Object>> getObject() {
		List<Map<String, Object>> listMap = new FastList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ResultSet resultSet = query.getResultSet();
			while(resultSet.next()) {
				map = new HashMap<String, Object>();
				map.put("amount", resultSet.getString("amount"));
				map.put("accountCode", resultSet.getString("accountCode"));
				listMap.add(map);
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return listMap;
	}
}
