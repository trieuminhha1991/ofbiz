package com.olbius.olap.accounting;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.query.InnerJoin;
import com.olbius.bi.olap.query.Join;
import com.olbius.bi.olap.query.Query;
import com.olbius.olap.AbstractOlap;

public class AccountingOlapImpl extends AbstractOlap implements AccountingOlap {

	private String group;
	
	private List<String> groups;
	
	private int limit;
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public void evaluateAcc(boolean dateFlag, boolean productFlag, boolean categoryFlag, boolean groupFlag, String product, String code, String currency, String debitCreditFlag, String dateType, boolean orig, boolean level, boolean distrib, boolean sort) throws GenericDataSourceException, GenericEntityException, SQLException {
		
		dateType = getDateType(dateType);
		
		Query query = new Query();
		
		query.setFrom("acctg_trans_fact", null);
		query.addSelect("date_dimension.".concat(dateType), null, dateFlag);
		query.addSelect("party_dimension.party_id", null, true);
		query.addSelect("gl_account_relationship.parent_dim_id", null, true);
		query.addSelect("orig_currency_uom_dim_id", "currency_uom_dim_id", null, orig);
		query.addSelect("debit_credit_flag", null, true);
		query.addSelect("gl_account_dimension.account_code", null, true);
		query.addSelect("product_dimension.product_id", null, productFlag);
		query.addSelect("product_dimension.internal_name", null, productFlag);
		query.addSelect("category_dimension.category_id", null, categoryFlag);
		query.addSelect("category_dimension.category_name", null, categoryFlag);
		query.addSelect("sum(orig_amount)", "sum(amount)", "_amount", orig);
		
		query.addInnerJoin("date_dimension", null, "transaction_dim_date = date_dimension.dimension_id", true);
		query.addInnerJoin("product_dimension", null, "product_dim_id = product_dimension.dimension_id", productFlag || categoryFlag);
		query.addInnerJoin("product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id", categoryFlag);
		query.addInnerJoin("date_dimension", "category_from_date_dimension", "product_category_relationship.from_dim_date = category_from_date_dimension.dimension_id", categoryFlag);
		query.addInnerJoin("date_dimension", "category_thru_date_dimension", "product_category_relationship.thru_dim_date = category_thru_date_dimension.dimension_id", categoryFlag);
		query.addInnerJoin("category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id"
				+ " AND category_dimension.category_type = \'CATALOG_CATEGORY\'"
				+ " AND ((date_dimension.date_value >= category_from_date_dimension.date_value)"
				+ " AND (category_thru_date_dimension.date_value IS NULL OR category_thru_date_dimension.date_value >= date_dimension.date_value))", categoryFlag);
		query.addInnerJoin("gl_account_relationship", null, "gl_account_relationship.dimension_id = gl_account_dim_id", true);
		query.addInnerJoin("gl_account_dimension", null, "gl_account_relationship.parent_dim_id = gl_account_dimension.dimension_id", true);
		
		Query tmp = new Query();
		
		tmp.setFrom("party_group_relationship", null);
		tmp.addSelect("party_group_relationship.dimension_id", null, true);
		tmp.addSelect("party_group_relationship.parent_dim_id", null, true);
		tmp.addSelect("from_date_dimension.date_value", "from_date", true);
		tmp.addSelect("thru_date_dimension.date_value", "thru_date", true);
		
		tmp.addInnerJoin("date_dimension", "from_date_dimension", "from_date_dim_id = from_date_dimension.dimension_id", true);
		tmp.addInnerJoin("date_dimension", "thru_date_dimension", "thru_date_dim_id = thru_date_dimension.dimension_id", true);
		
		tmp.addCondition("party_group_relationship.relationship_type = \'DISTRIBUTION\'", !orig && level && !distrib);
		
		tmp.addCondition("party_group_relationship.relationship_type != 'DISTRIBUTION' OR party_group_relationship.relationship_type IS NULL"
				+ " AND party_group_relationship.parent_dim_id != party_group_relationship.dimension_id", !orig && !level && !distrib);
		
		tmp.addCondition("party_group_relationship.parent_dim_id = party_group_relationship.dimension_id", !orig && distrib);
		
		
		Join join = new InnerJoin();
		
		join.setTable("(" +tmp.toString()+ ")", "group_relationship");
		
		join.addCondition("organization_party_dim_id = group_relationship.dimension_id", orig);
		join.addCondition("party_dim_id = group_relationship.dimension_id", !orig);
		join.addCondition("(date_dimension.date_value >= group_relationship.from_date"
				+ " AND (group_relationship.thru_date IS NULL OR date_dimension.date_value < group_relationship.thru_date))", true);
		
		query.addJoin(join, true);
		
		query.addInnerJoin("party_dimension", null, "group_relationship.parent_dim_id = party_dimension.dimension_id", true);
		query.addInnerJoin("currency_dimension", null, "currency_dimension.dimension_id = orig_currency_uom_dim_id", orig);
		query.addInnerJoin("currency_dimension", null, "currency_dimension.dimension_id = currency_uom_dim_id", !orig);
		
		query.addCondition("gl_account_dimension.account_code = ?", true);
		query.addCondition("currency_dimension.currency_id = ?", true);
		query.addConditionBetween("date_dimension.date_value", "?", "?", true);
		query.addCondition("debit_credit_flag = ?", true);
		query.addCondition("party_dimension.party_id = ?", group!=null && !group.isEmpty());
		query.addCondition("product_dimension.product_id = ?", productFlag && product!=null && !product.isEmpty());
		query.addCondition("category_dimension.category_id = ?", categoryFlag && product!=null && !product.isEmpty());
		query.addConditionIn("party_dimension.party_id", groups, groups!=null && !groups.isEmpty());
		
		query.addGroupBy("date_dimension.".concat(dateType), dateFlag);
		query.addGroupBy("product_dimension.product_id", productFlag);
		query.addGroupBy("product_dimension.internal_name", productFlag);
		query.addGroupBy("category_dimension.category_id", categoryFlag);
		query.addGroupBy("category_dimension.category_name", categoryFlag);
		query.addGroupBy("party_dimension.party_id", true);
		query.addGroupBy("gl_account_relationship.parent_dim_id", true);
		query.addGroupBy("orig_currency_uom_dim_id", orig);
		query.addGroupBy("currency_uom_dim_id", !orig);
		query.addGroupBy("debit_credit_flag", true);
		query.addGroupBy("gl_account_dimension.account_code", true);
		
		query.addOrderBy("date_dimension.".concat(dateType), dateFlag);
		query.addOrderBy("_amount", sort, true);
		query.limit(limit);
		
		getSQLProcessor().prepareStatement(query.toString());
		
		getSQLProcessor().setValue(code);
		getSQLProcessor().setValue(currency);
		getSQLProcessor().setValue(getSqlDate(fromDate));
		getSQLProcessor().setValue(getSqlDate(thruDate));
		getSQLProcessor().setValue(debitCreditFlag);
		
		if(group!=null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
		}
		
		if((categoryFlag || productFlag) && product!=null && !product.isEmpty()) {
			getSQLProcessor().setValue(product);
		}
		
		getSQLProcessor().executeQuery();
		
		ResultSet resultSet = getSQLProcessor().getResultSet();
		
		if(dateFlag) {
			Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>();
			
			while(resultSet.next()) {
				String key;
				if(productFlag && !groupFlag) {
					key = resultSet.getString("product_id");
					if(key==null || key.isEmpty()) {
						key = resultSet.getString("internal_name");
					}
				} else if(categoryFlag && !groupFlag) {
					key = resultSet.getString("category_id");
					if(key==null || key.isEmpty()) {
						key = resultSet.getString("category_name");
					}
				} else {
					key = resultSet.getString("party_id");
				}
				if(map.get(key)==null) {
					map.put(key, new HashMap<String, Object>());
				}
				if(debitCreditFlag.equals("D")) {
					map.get(key).put(resultSet.getString(dateType), resultSet.getBigDecimal("_amount"));
				} else if(debitCreditFlag.equals("C")) {
					map.get(key).put(resultSet.getString(dateType), resultSet.getBigDecimal("_amount").multiply(new BigDecimal(-1)));
				}
			}
			
			axis(map, dateType);
		} else {
			while(resultSet.next()) {
				String key;
				
				String _key = null;
				
				if(productFlag && !groupFlag) {
					key = resultSet.getString("product_id");
					if(key==null || key.isEmpty()) {
						key = resultSet.getString("internal_name");
					}
					_key = resultSet.getString("party_id");
				} else if(categoryFlag && !groupFlag) {
					key = resultSet.getString("category_name");
					if(key==null || key.isEmpty()) {
						key = resultSet.getString("category_id");
					}
					_key = resultSet.getString("party_id");
				} else {
					key = resultSet.getString("party_id");
					if(product == null || product.isEmpty()) {
						_key = "total";
					} else {
						if(productFlag) {
							_key = resultSet.getString("product_id");
							if(key==null || key.isEmpty()) {
								_key = resultSet.getString("internal_name");
							}
						}
						if(categoryFlag) {
							_key = resultSet.getString("category_id");
							if(key==null || key.isEmpty()) {
								_key = resultSet.getString("category_name");
							}
						}
					}
				}
				
				if(yAxis.get(_key) == null) {
					yAxis.put(_key, new ArrayList<Object>());
				}
				if(debitCreditFlag.equals("D")) {
					xAxis.add(key);
					yAxis.get(resultSet.getString("party_id")).add(resultSet.getBigDecimal("_amount"));
				} else if(debitCreditFlag.equals("C")) {
					xAxis.add(0, key);
					yAxis.get(_key).add(0, resultSet.getBigDecimal("_amount").multiply(new BigDecimal(-1)));
				}
				
			}
		}
		
	}

	@Override
	public void setGroup(List<String> groups) {
		this.groups = groups;
	}
	
}
