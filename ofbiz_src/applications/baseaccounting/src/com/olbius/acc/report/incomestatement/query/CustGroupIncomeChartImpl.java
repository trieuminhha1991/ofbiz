package com.olbius.acc.report.incomestatement.query;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class CustGroupIncomeChartImpl extends AbstractOlap implements IncomeOlapConstant{

	private OlbiusQuery query;
	protected Delegator delegator;
	protected String organizationPartyId;

	protected List<Object> getGlAccount(){
		List<Object> listAccountCode = FastList.newInstance();
		Account acc = null;
		List<Account> listAccount = null;
		acc = AccountBuilder.buildAccount("511", delegator);
		listAccount = acc.getListChild();
		for(Account item : listAccount) {
			AccountEntity accEntity = item.getAcc();
		 	if(accEntity.isLeaf()) {
		 		listAccountCode.add(accEntity.getGlAccountId());
		 	}
		}
		
		return listAccountCode;
	}	
	
	private void initQuery() {
		query = OlbiusQuery.make(getSQLProcessor());
		organizationPartyId = (String) getParameter(ORG_PARTY_ID);
		Condition cond = new Condition();
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeIn("gad.gl_account_id", getGlAccount()));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		query.from("acctg_trans_fact", "atf")
		.select("pcgd.party_classification_group_id", "groupId")
		.select("sum(atf.amount)", "amount")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "atf.gl_account_dim_id = gad.dimension_id")
		.join(Join.INNER_JOIN, "party_classification_fact", "pcf", "atf.party_dim_id = pcf.party_dim_id" )
		.join(Join.INNER_JOIN, "party_class_group_dimension", "pcgd", "pcgd.dimension_id = pcf.party_class_group_dim_id" )
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id")
		.where(cond)
		.groupBy("groupId");
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
	
	public class CustGroupIncomeQuery implements OlapResultQueryInterface {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			try {
				ResultSet resultSet = query.getResultSet();
				while(resultSet.next()) {
					BigDecimal amount = resultSet.getBigDecimal("amount").negate();
					String group = resultSet.getString("groupId");
					map.put(group, amount);
				}
			} catch (GenericEntityException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return map;
		}
	}
	
	public class CustGroupIncomePie extends AbstractOlapChart {

		public CustGroupIncomePie(OlapInterface olap, OlapResultQueryInterface query) {
			super(olap, query);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@Override
		protected void result(Object object) {
			Map<String, Object> tmp = ( Map<String, Object>) object;
			for(String s : tmp.keySet()) {
				List<Object> list = new ArrayList<Object>();
				list.add(tmp.get(s));
				yAxis.put(s, list);
				xAxis.add(s);
			}
		}
	}

}
