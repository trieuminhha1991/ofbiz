package com.olbius.olap.bi.accounting;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.Query;

public class AccountingOlapImpl extends AbstractOlap implements AccountingOlap {

	public static final String GROUP = "GROUP";
	public static final String GROUPS = "GROUPS";
	public static final String DATE_FLAG = "DATE_FLAG";
	public static final String PRODUCT_FLAG = "PRODUCT_FLAG";
	public static final String CATEGORY_FLAG = "CATEGORY_FLAG";
	public static final String GROUP_FLAG = "GROUP_FLAG";
	public static final String PRODUCT = "PRODUCT";
	public static final String CODE = "CODE";
	public static final String CURRENCY = "CURRENCY";
	public static final String DEBIT_CREDIT_FLAG = "DEBIT_CREDIT_FLAG";
	public static final String ORIG = "ORIG";
	public static final String LEVEL = "LEVEL";
	public static final String DISTRIB = "DISTRIB";
	public static final String SORT = "SORT";

	private final Test2 test2 = new Test2();

	private Query query;
	
	public AccountingOlapImpl() {
//		OlapLineChart chart = new OlapLineChart(this, new Test());
//		setOlapResult(chart);
		
		OlapGrid grid = new OlapGrid(this, test2);
		setOlapResult(grid);
	}

	private void initQuery() {
		
		String dateType = (String) getParameter(DATE_TYPE);

		dateType = getDateType(dateType);
		
		test2.setDatetype(dateType);
		
		test2.addDataField("date");
		test2.addDataField("party");
		test2.addDataField("accountCode");
		test2.addDataField("currency");
		test2.addDataField("debitCredit");
		test2.addDataField("product");
		test2.addDataField("category");
		test2.addDataField("amount");

		query = new Query(getSQLProcessor());

		/*query.setFrom("acctg_trans_fact", null);
		query.addSelect("date_dimension.".concat(dateType));
		query.addSelect("party_group_dimension.party_id");
		query.addSelect("gl_account_dimension.account_code");
		query.addSelect("currency_dimension.currency_id");
		query.addSelect("debit_credit_flag");
		query.addSelect("product_dimension.product_id");
		query.addSelect("product_dimension.internal_name");
		query.addSelect("category_dimension.category_id");
		query.addSelect("category_dimension.category_name");
		query.addSelect("sum(orig_amount)", "sum(amount)", "_amount", (Boolean) getParameter(ORIG));

		query.addInnerJoin("date_dimension", null, "transaction_dim_date = date_dimension.dimension_id");
		query.addInnerJoin("product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.addInnerJoin("product_category_relationship", null,
				"product_category_relationship.product_dim_id = product_dimension.dimension_id");
		query.addInnerJoin("date_dimension", "category_from_date_dimension",
				"product_category_relationship.from_dim_date = category_from_date_dimension.dimension_id");
		query.addInnerJoin("date_dimension", "category_thru_date_dimension",
				"product_category_relationship.thru_dim_date = category_thru_date_dimension.dimension_id");
		query.addInnerJoin("category_dimension", null,
				"product_category_relationship.category_dim_id = category_dimension.dimension_id"
						+ " AND category_dimension.category_type = \'CATALOG_CATEGORY\'"
						+ " AND ((date_dimension.date_value >= category_from_date_dimension.date_value)"
						+ " AND (category_thru_date_dimension.date_value ISNULL OR category_thru_date_dimension.date_value >= date_dimension.date_value))");
		query.addInnerJoin("gl_account_dimension", null,
				"acctg_trans_fact.gl_account_dim_id = gl_account_dimension.dimension_id");

		query.addInnerJoin("party_group_dimension", null,
				"acctg_trans_fact.party_dim_id = party_group_dimension.dimension_id");
		query.addInnerJoin("currency_dimension", null, "currency_dimension.dimension_id = orig_currency_uom_dim_id",
				(Boolean) getParameter(ORIG));
		query.addInnerJoin("currency_dimension", null, "currency_dimension.dimension_id = currency_uom_dim_id",
				!(Boolean) getParameter(ORIG));

		query.addConditionBetweenObj("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));

		query.addGroupBy("date_dimension.".concat(dateType));
		query.addGroupBy("product_dimension.product_id");
		query.addGroupBy("product_dimension.internal_name");
		query.addGroupBy("category_dimension.category_id");
		query.addGroupBy("category_dimension.category_name");
		query.addGroupBy("party_group_dimension.party_id");
		query.addGroupBy("gl_account_dimension.account_code");
		query.addGroupBy("currency_dimension.currency_id");
		query.addGroupBy("debit_credit_flag");

		query.addOrderBy("date_dimension.".concat(dateType));*/
		
		Query tmp = new Query();
		
		tmp.setFrom("acctg_trans_fact");
		tmp.addSelect("date_dimension.".concat(dateType));
		tmp.addSelect("acctg_trans_fact.product_dim_id");
		tmp.addSelect("category_dimension.dimension_id", "category_dim_id");
		tmp.addSelect("party_dim_id");
		tmp.addSelect("gl_account_dim_id");
		tmp.addSelect("currency_uom_dim_id");
		tmp.addSelect("debit_credit_flag");
		tmp.addSelect("orig_amount", "amount", "_amount", (Boolean) getParameter(ORIG));
		
		tmp.addInnerJoin("date_dimension", null, "transaction_dim_date = date_dimension.dimension_id");
		tmp.addInnerJoin("product_category_relationship", null, "product_category_relationship.product_dim_id = acctg_trans_fact.product_dim_id");
		tmp.addInnerJoin("date_dimension", "category_from_date_dimension", "product_category_relationship.from_dim_date = category_from_date_dimension.dimension_id");
		tmp.addInnerJoin("date_dimension", "category_thru_date_dimension", "product_category_relationship.thru_dim_date = category_thru_date_dimension.dimension_id");
		tmp.addInnerJoin("category_dimension", null,
				"product_category_relationship.category_dim_id = category_dimension.dimension_id"
						+ " AND category_dimension.category_type = \'CATALOG_CATEGORY\'"
						+ " AND ((date_dimension.date_value >= category_from_date_dimension.date_value)"
						+ " AND (category_thru_date_dimension.date_value IS NULL OR category_thru_date_dimension.date_value >= date_dimension.date_value))");
	
		tmp.addConditionBetweenObj("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));;
		
		Query tmp2 = new Query();
		
		tmp2.setFrom(tmp, "tmp");
		
		tmp2.addSelect("sum(_amount)", "_amount");
		tmp2.addSelect(dateType);
		tmp2.addSelect("product_dim_id");
		tmp2.addSelect("category_dim_id");
		tmp2.addSelect("party_dim_id");
		tmp2.addSelect("gl_account_dim_id");
		tmp2.addSelect("currency_uom_dim_id");
		tmp2.addSelect("debit_credit_flag");
		
		tmp2.addGroupBy(dateType);
		tmp2.addGroupBy("product_dim_id");
		tmp2.addGroupBy("category_dim_id");
		tmp2.addGroupBy("party_dim_id");
		tmp2.addGroupBy("gl_account_dim_id");
		tmp2.addGroupBy("currency_uom_dim_id");
		tmp2.addGroupBy("debit_credit_flag");
		
		query.setFrom(tmp2, "tmp2");
		query.addSelect("*");
		
		query.addInnerJoin("product_dimension",	"product_dim_id = product_dimension.dimension_id");
		query.addInnerJoin("gl_account_dimension",	"gl_account_dim_id = gl_account_dimension.dimension_id ");
		query.addInnerJoin("party_group_dimension",	"party_dim_id = party_group_dimension.dimension_id ");
		query.addInnerJoin("currency_dimension",	"currency_dimension.dimension_id = currency_uom_dim_id");
		query.addInnerJoin("category_dimension",	"category_dimension.dimension_id = category_dim_id");
		
		query.addOrderBy(dateType);
	}
	
	@Override
	protected Query getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}

	public class Test implements OlapResultQueryInterface {

		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
			try {
				ResultSet resultSet = query.getResultSet();
				while (resultSet.next()) {
					/*
					 * String key; if(productFlag && !groupFlag) { key =
					 * resultSet.getString("internal_name"); if(key==null ||
					 * key.isEmpty()) { key = resultSet.getString("product_id");
					 * } } else if(categoryFlag && !groupFlag) { key =
					 * resultSet.getString("category_name"); if(key==null ||
					 * key.isEmpty()) { key =
					 * resultSet.getString("category_id"); } } else { key =
					 * resultSet.getString("party_id"); } if(map.get(key)==null)
					 * { map.put(key, new HashMap<String, Object>()); }
					 * if(debitCreditFlag.equals("D")) {
					 * map.get(key).put(resultSet.getString(dateType),
					 * resultSet.getBigDecimal("_amount")); } else
					 * if(debitCreditFlag.equals("C")) {
					 * map.get(key).put(resultSet.getString(dateType),
					 * resultSet.getBigDecimal("_amount").multiply(new
					 * BigDecimal(-1))); }
					 */
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return map;
		}
	}

	private class Test2 extends ReturnResultGrid {

		private String dateType;

		public void setDatetype(String dateType) {
			this.dateType = dateType;
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				String debitCreditFlag = result.getString("debit_credit_flag");

				map.put("date", result.getString(dateType));
				map.put("party", result.getString("party_id"));
				map.put("accountCode", result.getString("account_code"));
				map.put("currency", result.getString("currency_id"));
				map.put("debitCredit", debitCreditFlag);
				map.put("product", result.getString("internal_name"));
				map.put("category", result.getString("category_name"));

				if (debitCreditFlag.equals("D")) {
					map.put("amount", result.getBigDecimal("_amount"));
				} else if (debitCreditFlag.equals("C")) {
					map.put("amount", result.getBigDecimal("_amount").multiply(new BigDecimal(-1)));
				}
			} catch (Exception e) {
				Debug.logError(e.getMessage(), Test2.class.getName());
			}
			return map;
		}

	}
}
