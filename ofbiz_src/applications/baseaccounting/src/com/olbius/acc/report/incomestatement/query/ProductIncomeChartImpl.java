package com.olbius.acc.report.incomestatement.query;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.query.OlapQuery;

public class ProductIncomeChartImpl extends QueryChartIncomeStatement {

	public ProductIncomeChartImpl(Delegator delegator,String dimension) {
		super(delegator,dimension);
	}

	/*private OlbiusQuery query;
	protected Delegator delegator;
	protected String organizationPartyId;*/

	/*protected List<Object> getGlAccount(){
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
	}*/
		
	/*private void initQuery() {
		query = OlbiusQuery.make(getSQLProcessor());
		organizationPartyId = (String) getParameter(ORG_PARTY_ID);
		Condition cond = new Condition();
		cond.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeIn("gad.gl_account_id", getGlAccount()));
		cond.and(Condition.makeEQ("opg.party_id", organizationPartyId));
		query.from("acctg_trans_fact", "atf")
		.select("pd.product_id", "productId")
		.select("-sum(atf.amount)", "amount")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "atf.gl_account_dim_id = gad.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "pd", "atf.product_dim_id = pd.dimension_id" )
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id")
		.where(cond)
		.groupBy("productId");
	}*/
	
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
	
	
	/*public class ProductIncomeQuery implements OlapResultQueryInterface {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			try {
				ResultSet resultSet = query.getResultSet();
				while(resultSet.next()) {
					BigDecimal amount = resultSet.getBigDecimal("amount").negate();
					String productId = resultSet.getString("productId");
					map.put(productId, amount);
				}
			} catch (GenericEntityException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return map;
		}
	}*/
	
	/*public class ProductIncomePie extends AbstractOlapChart {

		public ProductIncomePie(OlapInterface olap, OlapResultQueryInterface query) {
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
	}*/

}
