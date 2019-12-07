package com.olbius.acc.report.expensestatement;

import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.services.OlbiusOlapService;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExpenseStatementOlapImplReport extends OlbiusOlapService {

	private OlbiusQuery query;
	public static final String EXPENSE_CODE = "641";
	public static final String EXPENSE_CODE2 = "642";
	
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organizationId;
		try {
			organizationId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			putParameter("organizationId", organizationId);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();
		
		String dateType = getDateType((String) getParameter("dateType"));
		String organizationPartyId = (String) getParameter("organizationId");
		if (dateType.equals("year_month_day")) dateType = "transaction_date";
		
		List<Object> listAccountCode = getGlAccount();
		Condition cond = new Condition();
		cond.and(Condition.makeIn("gl_account_id", listAccountCode));
		cond.and(Condition.makeBetween("transaction_date", getFromDate(), getThruDate()));
		cond.and(Condition.makeEQ("organization_party_id", organizationPartyId));
		query.from("acctg_document_list_fact")
			.select(dateType)
			.select("gl_account_id", "glAccountId")
			.select("account_name", "accountName")
			.select("sum(dr_amount - cr_amount)", "amount")
			.where(cond)
			.groupBy(dateType)
			.groupBy("account_name")
			.groupBy("gl_account_id");
		
		return query;
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

	@Override
	public void prepareResultGrid() {
		addDataField("glAccountId", "glAccountId");
		addDataField("accountName", "accountName");
		String dateType = getDateType((String) getParameter("dateType"));
		if (dateType.equals("year_month_day")) {
			addDataField("transTime", "transaction_date", new ReturnResultCallback<String>() {
				@Override
				public String get(Object object) {
					if(UtilValidate.isNotEmpty(object)){
						SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
						java.sql.Date time = (java.sql.Date)object;
						return sp.format(time);
					}
					return null;
				}
			});
		} else {
			addDataField("transTime", dateType);	
		}
		
		addDataField("amount", "amount");	
	}
}