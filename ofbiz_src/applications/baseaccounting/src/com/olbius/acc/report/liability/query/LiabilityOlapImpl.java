package com.olbius.acc.report.liability.query;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;

import com.olbius.acc.report.liability.entity.DefinitionOlap;
import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;

public class LiabilityOlapImpl extends AbstractOlap implements DefinitionOlap {
	private String getStatistic(String type) {
		return TYPE_PRODUCT_STORE.equals(type)? _PRODUCT_STORE
				:TYPE_ORGANIZATION.equals(type)? TYPE_ORGANIZATION
					:TYPE_FACILITY.equals(type) ? _FACILITY
							: TYPE_PARTY_FROM.equals(type) ? _PARTY_FROM
								: TYPE_CHANNEL.equals(type) ? _CHANNEL
									:TYPE_SUPPLIER.equals(type)? _SUPPLIER	
											:TYPE_EMPLOYEE.equals(type)? _EMPLOYEE	
													:TYPE_GL_ACCOUNT.equals(type)? _GL_ACCOUNT	
															: TYPE_PRODUCT.equals(type) ? _PRODUCT : null;																
	}
	
	private OlbiusQuery query;
	
	private void initQuery() {
		Map<String, String> type = (Map<String, String>) getParameter(TYPE);
		query = new OlbiusQuery(getSQLProcessor());
		OlbiusQuery query1 = new OlbiusQuery(getSQLProcessor());
		
		query1.from("invoice_payment_gl_fact", null);	
		query1.select("sum(pt_amount)","pt_amount");
		//query1.select("party_group_dimension.description");
		query1.select("CASE WHEN date_dimension.date_value IS NOT NULL THEN date_dimension.date_value ELSE date_dim.date_value END", "invoice_date");
		query1.select("party_dimension.party_id", null);
		query1.select("CASE WHEN currency_dimension.currency_id IS NOT NULL THEN currency_dimension.currency_id ELSE currency_dim.currency_id END", "currency_id");
		query1.select("sum(inv_total_amount)","inv_total_amount");
		query1.select("sum(pa_amount_applied)", "applied_amount");	
		query1.select("inv_invoice_id");	
		query1.select("gl_account_dimension.gl_account_id");
		query1.select("gl_account_dim.gl_account_id", "p_gl_account_id");
		query1.select("CASE WHEN party_dimension.description IS NULL THEN party_dimension.last_name || ' ' || COALESCE(party_dimension.middle_name,'') || ' ' ||  party_dimension.first_name ELSE party_dimension.description END AS party_name");
		
		query1.join("INNER JOIN","date_dimension","inv_invoice_date_dim_id = date_dimension.dimension_id");
		query1.join("INNER JOIN","date_dimension AS date_dim","pt_effective_date_dim_id = date_dim.dimension_id");
		query1.join("INNER JOIN","currency_dimension","inv_currency_uom_dim_id = currency_dimension.dimension_id");
		query1.join("INNER JOIN","currency_dimension AS currency_dim","pt_currency_uom_dim_id = currency_dim.dimension_id");
		query1.join("INNER JOIN","party_dimension","inv_party_diff_dim_id = party_dimension.dimension_id");
		query1.join("INNER JOIN","party_dimension AS party_group_dim","inv_org_party_dim_id = party_group_dim.dimension_id");
		query1.join("INNER JOIN","gl_account_dimension ","inv_gl_account_dim_id = gl_account_dimension.dimension_id");
		query1.join("INNER JOIN","gl_account_dimension AS gl_account_dim","pt_gl_account_dim_id = gl_account_dim.dimension_id");
		
		Condition condition = new Condition();
		Condition condition1 = new Condition();
		Condition condition2 = new Condition();
		Condition condition12 = new Condition();
		Condition condition3 = new Condition();
		Condition condition4 = new Condition();
		Condition condition5 = new Condition();
		Condition condition6 = new Condition();
		Condition condition7 = new Condition();
		Condition condition8 = new Condition();
		Condition condition9 = new Condition();
		Condition condition89 = null;
		condition1 = condition1.make("gl_account_dimension.gl_account_id = '" + type.get("GL_ACCOUNT") + "'");
		condition2 = condition2.make("gl_account_dim.gl_account_id  = '" + type.get("GL_ACCOUNT") + "'");
		condition12 = condition12.orCondition(condition1,condition2);
		condition3 = condition3.make("gl_account_dimension.gl_account_id IS NULL AND gl_account_dim.gl_account_id IS NOT NULL");
		condition4 = condition4.make("gl_account_dimension.gl_account_id IS NOT NULL and gl_account_dim.gl_account_id IS NULL");
		condition5 = condition5.make("gl_account_dimension.gl_account_id = gl_account_dim.gl_account_id");
		condition6 = condition6.make("party_group_dim.party_id='" + type.get("ORGANIZATION") + "'");
		if (type.get("SUPPLIER") != null)
		{
			condition89 = new Condition();
			condition89 = condition8.make("party_dimension.party_id = '" + type.get("SUPPLIER") + "'");
//			condition9 = condition8.make("party_person_dimension.party_id = '" + type.get("SUPPLIER") + "'");
//			condition89 = condition89.orCondition(condition8, condition9);
		}
		condition7 = condition7.orCondition(condition3, condition4);
		condition7 = condition7.orCondition(condition7, condition5);		
		condition = condition.andCondition(condition7, condition6);
		condition = condition.andCondition(condition, condition12);
		if (condition89 != null)
		condition = condition.andCondition(condition, condition89);
		query1.where(condition);

		query1.groupBy("date_dimension.date_value");
		query1.groupBy("date_dim.date_value");
		query1.groupBy("currency_dimension.currency_id");
		query1.groupBy("currency_dim.currency_id");
		query1.groupBy("party_dimension.party_id");
		query1.groupBy("party_dimension.description");    
        query1.groupBy("party_dimension.first_name"); 
		query1.groupBy("party_dimension.middle_name"); 
		query1.groupBy("party_dimension.last_name"); 
		query1.groupBy("inv_total_amount");
		query1.groupBy("inv_invoice_id");
		query1.groupBy("gl_account_dimension.gl_account_id");
		query1.groupBy("gl_account_dim.gl_account_id");
		query1.groupBy("invoice_payment_gl_fact.party_type_id");
						
		query1.orderBy("party_dimension.party_id");
		
		query.from("("+query1.toString() +")", "TMP");
		query.select("sum(TMP.inv_total_amount)", "_total_amount");
		query.select("sum(TMP.applied_amount)", "_applied_amount");
		query.select("SUM(TMP.pt_amount)", "_pt_amount"); 
		query.select("TMP.party_id", "party_id");
		query.select("TMP.party_name", "party_name");
//		query.select("TMP.gl_account_id", "gl_account_id");
		query.select("CASE WHEN TMP.gl_account_id IS NULL THEN TMP.p_gl_account_id ELSE TMP.gl_account_id END", "gl_account_id");
		query.select("TMP.currency_id", "currency_id");
		
		Condition conQuery = new Condition();
		conQuery = conQuery.andBetween("TMP.invoice_date", getSqlDate(fromDate), getSqlDate(thruDate));
		query.where(conQuery);
		//conditionBetweenObj("TMP.invoice_date", getSqlDate(fromDate), getSqlDate(thruDate));
		
		query.groupBy("TMP.party_id");
		query.groupBy("TMP.party_name");
		query.groupBy("TMP.gl_account_id");
		query.groupBy("TMP.p_gl_account_id");
		query.groupBy("TMP.currency_id");
		query.orderBy("TMP.party_id");
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	//data for chart
	public class LiabilityChart implements OlapResultQueryInterface {
		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
			try {
				ResultSet resultSet = query.getResultSet();
				while (resultSet.next()) {
				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}
	}
	
	//data for grid
	public class LiabilityGrid extends ReturnResultGrid{

		public LiabilityGrid() {
			addDataField("partyId");
			addDataField("partyName");
			addDataField("glAccountId");
			addDataField("paymentApplied");
			addDataField("totalAmount");
			addDataField("appliedAmount");
			addDataField("liabilityAmount");
			addDataField("currency");
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("partyId", result.getString("party_id"));
				String strPartyName = result.getString("party_name");				
				map.put("partyName", strPartyName);				
				BigDecimal totalAmount = result.getBigDecimal("_total_amount");				
				BigDecimal appliedAmount = result.getBigDecimal("_applied_amount");
				if (totalAmount == null ) totalAmount = BigDecimal.ZERO;
				if (appliedAmount == null ) appliedAmount = BigDecimal.ZERO;
				BigDecimal ptAmount = result.getBigDecimal("_pt_amount");
				if (ptAmount == null ) ptAmount = BigDecimal.ZERO;
				BigDecimal paymentApplied = BigDecimal.ZERO;
				if (appliedAmount.compareTo(BigDecimal.ZERO) >= 0)
					paymentApplied = ptAmount.subtract(appliedAmount);
				else paymentApplied = ptAmount;
				
				if (paymentApplied.compareTo(BigDecimal.ZERO) < 0)
					paymentApplied = BigDecimal.ZERO;
				BigDecimal liabilityAmount = BigDecimal.ZERO;
				if (liabilityAmount.compareTo(BigDecimal.ZERO) >= 0)
					liabilityAmount = totalAmount.subtract(appliedAmount);
				else liabilityAmount = totalAmount;
				map.put("totalAmount", totalAmount);
				map.put("paymentApplied", paymentApplied);
				map.put("appliedAmount", appliedAmount);
				map.put("liabilityAmount", liabilityAmount);
				map.put("currency", result.getString("currency_id"));
				map.put("glAccountId", result.getString("gl_account_id"));
			} catch (Exception e) {
				Debug.logError(e.getMessage(), LiabilityGrid.class.getName());
			}
			return map;
		}
	}
}
