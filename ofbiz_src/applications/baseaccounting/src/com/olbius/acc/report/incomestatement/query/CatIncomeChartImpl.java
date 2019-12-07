package com.olbius.acc.report.incomestatement.query;

import org.ofbiz.entity.Delegator;
import com.olbius.bi.olap.query.OlapQuery;

public class CatIncomeChartImpl extends QueryChartIncomeStatement{

	/*private OlbiusQuery query;
	protected Delegator delegator;
	protected String organizationPartyId;*/
	
	public CatIncomeChartImpl(Delegator delegator,String dimension) {
		super(delegator, dimension);
	}
	
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
		.select("cd.category_id", "categoryId")
		.select("sum(atf.amount)", "amount")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "atf.transaction_dim_date = dd.dimension_id")
		.join(Join.INNER_JOIN, "gl_account_dimension", "gad", "atf.gl_account_dim_id = gad.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "pd", "atf.product_dim_id = pd.dimension_id" )
		.join(Join.INNER_JOIN, "product_category_relationship", "pcr", "pd.dimension_id = pcr.product_dim_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrf", "pcr.from_dim_date = dpcrf.dimension_id" )
		.join(Join.INNER_JOIN, "date_dimension", "dpcrt", "pcr.thru_dim_date = dpcrt.dimension_id" )
		.join(Join.INNER_JOIN, "category_dimension", "cd", "cd.dimension_id = pcr.category_dim_id AND cd.category_type = 'CATALOG_CATEGORY' AND dd.date_value >= dpcrf.date_value AND (dpcrt.date_value ISNULL OR dpcrt.date_value >= dd.date_value) " )		
		.join(Join.INNER_JOIN, "party_dimension", "opg", "opg.dimension_id = atf.organization_party_dim_id")
		.where(cond)
		.groupBy("categoryId");
	}*/
	
	/*public Delegator getDelegator() {
		return delegator;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}*/	
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	/*public class CatIncomeQuery implements OlapResultQueryInterface {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			try {
				ResultSet resultSet = query.getResultSet();
				while(resultSet.next()) {
					BigDecimal amount = resultSet.getBigDecimal("amount").negate();
					String category = resultSet.getString("categoryId");
					map.put(category, amount);
				}
			} catch (GenericEntityException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return map;
		}
	}
	
	public class CatIncomePie extends AbstractOlapChart {

		public CatIncomePie(OlapInterface olap, OlapResultQueryInterface query) {
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
