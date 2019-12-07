package com.olbius.acc.report.expensestatement;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.OlbiusReturnResultCallback;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ExpenseOlapImpl extends OlbiusBuilder{
	
	public static final String MODULE = ExpenseOlapImpl.class.getName();
	public static final String EXPENSE_CODE = "641";
	public static final String EXPENSE_CODE2 = "642";
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String ORG_PARTY_ID = "organizationPartyId";
	
	protected OlbiusQuery query;
	private String dateType;
	private Delegator delegator;
	protected String organizationPartyId;
	
	public Delegator getDelegator() {
		return delegator;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}
	
	public ExpenseOlapImpl(Delegator delegator) {
		super(delegator);
		this.delegator = delegator;
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("glAccountId",  "glAccountId");
		addDataField("transTime", new OlbiusReturnResultCallback<String>("transTime") {

			@Override
			public String get(Map<String, Object> map) {
				String[] tmp = ((String)map.get("transTime")).split("-");
				String transTime = "";
				if(tmp != null) {
					for(int i = tmp.length -1; i >= 0; i--) {
						transTime += tmp[i];
						transTime += "/";
					}
					transTime = transTime.substring(0, transTime.length()-1);
				}
				return transTime;
			}
		});
		addDataField("amount", "amount", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});		
	}	
	
	private List<Object> getGlAccount(){
		Account acc = AccountBuilder.buildAccount(EXPENSE_CODE, this.delegator);
		List<Account> listAccount = acc.getListChild();
		List<Object> listAccountCode = FastList.newInstance();
		for(Account item : listAccount) {
			AccountEntity accEntity = item.getAcc();
		 	if(accEntity.isLeaf()) {
		 		listAccountCode.add(accEntity.getAccountCode());
		 	}
		}
		
		acc = AccountBuilder.buildAccount(EXPENSE_CODE2, this.delegator);
		listAccount = acc.getListChild();
		for(Account item : listAccount) {
			AccountEntity accEntity = item.getAcc();
		 	if(accEntity.isLeaf()) {
		 		listAccountCode.add(accEntity.getAccountCode());
		 	}
		}
		return listAccountCode;
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		List<Object> listAccountCode = FastList.newInstance();
		query = new OlbiusQuery(getSQLProcessor());
		dateType = (String) getParameter(DATE_TYPE);
		organizationPartyId = (String) getParameter(ORG_PARTY_ID);
		dateType = getDateType(dateType);
		listAccountCode = getGlAccount();
		Condition cond = new Condition();
		cond.and(Condition.makeIn("gad.gl_account_id", listAccountCode));
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		query.from("acctg_trans_fact", "atf")
		.select("dd.".concat(dateType), "transTime")
		.select("gad.gl_account_id", "glAccountId")
		.select("sum(atf.amount)", "amount")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "atf.gl_account_dim_id = gad.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "opg", "atf.organization_party_dim_id = opg.dimension_id")
		.where(cond)
		.groupBy("transTime")
		.groupBy("glAccountId");
	}
	

	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
}