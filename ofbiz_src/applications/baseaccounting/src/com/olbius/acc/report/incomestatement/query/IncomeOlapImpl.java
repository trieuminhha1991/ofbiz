package com.olbius.acc.report.incomestatement.query;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.entity.Delegator;

import com.olbius.acc.report.expensestatement.ExpenseOlapImpl;
import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.OlbiusReturnResultCallback;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class IncomeOlapImpl extends OlbiusBuilder implements IncomeOlapConstant{
	
	public IncomeOlapImpl(Delegator delegator) {
		super(delegator);
		// TODO Auto-generated constructor stub
		this.delegator = delegator;
	}

	protected String hasTransTime;

	public void setHasTransTime(String hasTransTime) {
		this.hasTransTime = hasTransTime;
	}
	
	public static final String MODULE = ExpenseOlapImpl.class.getName();
	
	protected OlbiusQuery query;
	protected String dateType;
	protected String customerId;
	protected String categoryId;
	protected String productId;
	protected String organizationPartyId;
	protected Delegator delegator;
	
	protected List<Object> getGlAccount(){
		List<Object> listAccountCode = FastList.newInstance();
		Account acc = null;
		List<Account> listAccount = null;
		acc = AccountBuilder.buildAccount(EXPENSE_CODE, delegator);
		listAccount = acc.getListChild();
		for(Account item : listAccount) {
			AccountEntity accEntity = item.getAcc();
		 	//if(accEntity.isLeaf()) {
		 		listAccountCode.add(accEntity.getGlAccountId());
		 	//}
		}
		
		acc = AccountBuilder.buildAccount(INCOME_CODE, delegator);
		listAccount = acc.getListChild();
		for(Account item : listAccount) {
			AccountEntity accEntity = item.getAcc();
		 	//if(accEntity.isLeaf()) {
		 		listAccountCode.add(accEntity.getGlAccountId());
		 	//}
		}
		return listAccountCode;
	}
	
	private void initQuery(){
		Condition cond = new Condition();
		
		query = new OlbiusQuery(getSQLProcessor());
		dateType = (String) getParameter(DATATYPE);
		customerId = (String) getParameter(CUSTOMER_ID);
		categoryId = (String) getParameter(CATEGORY_ID);
		productId = (String) getParameter(PRODUCT_ID);
		organizationPartyId = (String) getParameter(ORG_PARTY_ID);
		dateType = getDateType(dateType);
		if(productId != null) {
			cond.and(Condition.makeEQ("pd.product_id", productId));
		}
		if(categoryId != null) {
			cond.and(Condition.makeEQ("cd.category_id", categoryId));
		}
		if(customerId != null) {
			cond.and(Condition.makeEQ("pgd.party_id", customerId));
		}
		cond.and(Condition.makeIn("gad.gl_account_id", getGlAccount()));
		//cond.and(Condition.makeEQ("cd.category_type", "CATALOG_CATEGORY"));
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		query.from("acctg_trans_fact", "atf")
		.select("dd.".concat(dateType), "transTime")
		.select("gad.gl_account_id", "glAccountId")
		.select("pgd.party_id", "partyId")
		.select("pd.product_id", "productId")
		.select("cd.category_id", "categoryId")
		.select("sum(atf.amount)", "amount")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "atf.gl_account_dim_id = gad.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "pgd", "atf.party_dim_id = pgd.dimension_id" )		
		.join(Join.INNER_JOIN, "product_dimension", "pd", "atf.product_dim_id = pd.dimension_id" )
		.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "pd.dimension_id = pcr.product_dim_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrf", "pcr.from_dim_date = dpcrf.dimension_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrt", "pcr.thru_dim_date = dpcrt.dimension_id" )
		.join(Join.INNER_JOIN, "category_dimension", "cd", "cd.dimension_id = pcr.category_dim_id AND cd.category_type = 'CATALOG_CATEGORY' AND dd.date_value >= dpcrf.date_value AND (dpcrt.date_value IS NULL OR dpcrt.date_value >= dd.date_value) " )
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id")
		.where(cond)
		.groupBy("transTime")
		.groupBy("glAccountId")
		.groupBy("partyId")
		.groupBy("productId")
		.groupBy("categoryId")
		.groupBy("transTime", false)
		.orderBy("transTime", OlbiusQuery.DESC);
	}
	
	public Delegator getDelegator() {
		return delegator;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("partyId", "partyId");
		addDataField("productId", "productId");
		addDataField("categoryId","categoryId");
		addDataField("glAccountId","glAccountId");
		addDataField("amount","amount");
		addDataField("transTime", new OlbiusReturnResultCallback<String>("transTime") {

			@Override
			public String get(Map<String, Object> map) {
				String transTime = "";
				if(map.get("transTime") != null && map.get("transTime") instanceof String){
					String[] tmp = ((String)map.get("transTime")).split("-");
					if(tmp != null) {
						for(int i = tmp.length -1; i >= 0; i--) {
							transTime += tmp[i];
							transTime += "/";
						}
						transTime = transTime.substring(0, transTime.length()-1);
					}
				}
				return transTime;
			}
		});
	}
}
