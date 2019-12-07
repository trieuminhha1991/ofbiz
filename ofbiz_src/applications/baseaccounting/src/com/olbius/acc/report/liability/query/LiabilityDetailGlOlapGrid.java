package com.olbius.acc.report.liability.query;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.olap.grid.AbstractOlapGrid;
import com.olbius.olap.grid.OlapGridInterface;

public class LiabilityDetailGlOlapGrid extends AbstractOlapGrid{
	public final static String TYPE_PRODUCT_STORE = "PRODUCT_STORE";
	public final static String TYPE_PARTY_FROM = "PARTY_FROM";
	public final static String TYPE_CHANNEL = "CHANNEL";
	public final static String TYPE_PRODUCT = "PRODUCT";
	public final static String TYPE_FACILITY = "FACILITY";
	public final static String TYPE_SUPPLIER = "SUPPLIER";
	public final static String TYPE_EMPLOYEE = "EMPLOYEE";
	public final static String TYPE_GL_ACCOUNT = "GL_ACCOUNT";
	
	private final static String _PRODUCT_STORE = "product_store_dimension.product_store_id";
	private final static String _PARTY_FROM = "party_person_dimension.party_id";
	private final static String _CHANNEL = "sales_order_fact.sales_channel_enum_id";
	private final static String _PRODUCT = "product_dimension.product_id";
	private final static String _FACILITY = "facility_dimension.facility_id";
	private final static String _SUPPLIER = "party_group_dimension.party_id";
	private final static String _EMPLOYEE = "employee.party_id";
	private final static String _GL_ACCOUNT = "gl_account_dimension.gl_account_id";
	private String getStatistic(String type) {
		return TYPE_PRODUCT_STORE.equals(type)? _PRODUCT_STORE
				:TYPE_FACILITY.equals(type) ? _FACILITY
 					: TYPE_PARTY_FROM.equals(type) ? _PARTY_FROM
								: TYPE_CHANNEL.equals(type) ? _CHANNEL
									:TYPE_SUPPLIER.equals(type)? _SUPPLIER	
											:TYPE_EMPLOYEE.equals(type)? _EMPLOYEE	
													:TYPE_GL_ACCOUNT.equals(type)? _GL_ACCOUNT	
															: TYPE_PRODUCT.equals(type) ? _PRODUCT : null;
	}
	
	public void reportLiabilityDetail(Map<String, String> type) throws GenericDataSourceException, GenericEntityException, SQLException {
		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		query.from("invoice_payment_gl_fact");
		query.select("date_dimension.date_value", "invoice_date");
		query.select("currency_dimension.currency_id");
		query.select("inv_total_amount");
		query.select("inv_invoice_id");
		query.select("sum(pa_amount_applied)", "appliedAmount");
		query.select("(CURRENT_DATE - date_dimension.date_value)", "count_day");
		query.select("gl_account_dimension.gl_account_id");

		query.join("INNER JOIN","date_dimension","inv_invoice_date_dim_id = date_dimension.dimension_id");		
		query.join("INNER JOIN","currency_dimension","inv_currency_uom_dim_id = currency_dimension.dimension_id");		
		query.join("INNER JOIN","party_dimension","inv_party_diff_dim_id = party_dimension.dimension_id");
		query.join("INNER JOIN","party_dimension AS party_group_dim","inv_org_party_dim_id = party_group_dim.dimension_id");
		query.join("INNER JOIN","gl_account_dimension ","inv_gl_account_dim_id = gl_account_dimension.dimension_id");
		query.join("INNER JOIN","gl_account_dimension AS gl_account_dim","pt_gl_account_dim_id = gl_account_dim.dimension_id");
	
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
		condition1 = condition1.make("gl_account_dimension.gl_account_id = '" + type.get("GL_ACCOUNT") + "'");
		condition2 = condition2.make("gl_account_dim.gl_account_id  = '" + type.get("GL_ACCOUNT") + "'");
		condition12 = condition12.orCondition(condition1,condition2);
		condition3 = condition3.make("gl_account_dimension.gl_account_id IS NULL AND gl_account_dim.gl_account_id IS NOT NULL");
		condition4 = condition4.make("gl_account_dimension.gl_account_id IS NOT NULL and gl_account_dim.gl_account_id IS NULL");
		condition5 = condition5.make("gl_account_dimension.gl_account_id = gl_account_dim.gl_account_id");
		condition6 = condition6.make("party_group_dim.party_id='" + type.get("ORGANIZATION") + "'");
		if (type.get("SUPPLIER") != null)
		{
			condition8 = condition8.make("party_dimension.party_id = '" + type.get("SUPPLIER") + "'");
		}
		condition7 = condition7.orCondition(condition3, condition4);
		condition7 = condition7.orCondition(condition7, condition5);		
		condition = condition.andCondition(condition7, condition6);
		condition = condition.andCondition(condition, condition12);
		if (condition8 != null)
		condition = condition.andCondition(condition, condition8);
		query.where(condition);

		query.groupBy("date_dimension.date_value");
		query.groupBy("currency_dimension.currency_id");
		query.groupBy("party_dimension.party_id");
		query.groupBy("inv_total_amount");
		query.groupBy("inv_invoice_id");
		query.groupBy("gl_account_dimension.gl_account_id");
		
		//Note order by party_person_dimension.party_id			
		query.orderBy("party_dimension.party_id");		

		ResultSet resultSet = query.getResultSet();
		setId("id");
		//add data field
		addDataField("currency","string");
		addDataField("liabilityYear1","number");
		addDataField("liabilityYear2","number");
		addDataField("liabilityYear3","number");
		addDataField("liabilityYear4","number");
		addDataField("liabilityYear5","number");
		
		BigDecimal liabilityYear0 = BigDecimal.ZERO;
		BigDecimal liabilityYear1 = BigDecimal.ZERO;
		BigDecimal liabilityYear2 = BigDecimal.ZERO;
		BigDecimal liabilityYear3 = BigDecimal.ZERO;
		BigDecimal liabilityYear4 = BigDecimal.ZERO;
		BigDecimal liabilityYear5 = BigDecimal.ZERO;
		BigDecimal condValue0 = new BigDecimal("15");
		BigDecimal condValue1 = new BigDecimal("30");
		BigDecimal condValue2 = new BigDecimal("60");
		BigDecimal condValue3 = new BigDecimal("90");
		BigDecimal condValue4 = new BigDecimal("180");
		BigDecimal condValue5 = new BigDecimal("360");
		String currency = "";
		Map<String, Object> map = FastMap.newInstance();
		while (resultSet.next()) {
			BigDecimal totalAmount = resultSet.getBigDecimal("inv_total_amount");
			BigDecimal appliedAmount = resultSet.getBigDecimal("appliedAmount");
			if (totalAmount == null ) totalAmount = BigDecimal.ZERO;
			if (appliedAmount == null ) appliedAmount = BigDecimal.ZERO;			
			BigDecimal liabilityAmount = totalAmount.subtract(appliedAmount);
			BigDecimal countDay = resultSet.getBigDecimal("count_day");
			if (countDay == null ) countDay = BigDecimal.ZERO;	
			currency = resultSet.getString("currency_id");
			
			if (countDay.compareTo(condValue0) == -1){
				liabilityYear0 = liabilityYear0.add(liabilityAmount);
			}
			
			if (countDay.compareTo(condValue0) == 1 && countDay.compareTo(condValue1) == -1){
				liabilityYear1 = liabilityYear1.add(liabilityAmount);
			}
			
			if ((countDay.compareTo(condValue1) == 1)&&(countDay.compareTo(condValue2) == -1)){
				liabilityYear2 = liabilityYear2.add(liabilityAmount);
			}
			
			if ((countDay.compareTo(condValue2) == 1)&&(countDay.compareTo(condValue3) == -1)){
				liabilityYear3 = liabilityYear3.add(liabilityAmount);
			}
			
			if ((countDay.compareTo(condValue3) == 1)&&(countDay.compareTo(condValue4) == -1)){
				liabilityYear4 = liabilityYear4.add(liabilityAmount);
			}
			
			if ((countDay.compareTo(condValue3) == 1)){
				liabilityYear5 = liabilityYear5.add(liabilityAmount);
			}			
		}
		
		map.put("liabilityYear0", liabilityYear0);
		map.put("liabilityYear1", liabilityYear1);
		map.put("liabilityYear2", liabilityYear2);
		map.put("liabilityYear3", liabilityYear3);
		map.put("liabilityYear4", liabilityYear4);
		map.put("liabilityYear05", liabilityYear5);
		map.put("currency", currency);
		addData(map);
	}
	
}
