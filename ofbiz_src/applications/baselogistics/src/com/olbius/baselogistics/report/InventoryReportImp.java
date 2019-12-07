package com.olbius.baselogistics.report;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.olap.bi.accounting.AccountingOlap;

public class InventoryReportImp extends AbstractOlap implements AccountingOlap{

	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String FACILITY_ID = "FACILITY_ID";
	public static final String CATEGORY_ID = "CATEGORY_ID";
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	public static final String CHECK_NPP = "CHECK_NPP";
	private OlbiusQuery olbiusQuery;
	private String dateType;
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		dateType = (String) getParameter(DATE_TYPE);
		List<Object> productId = (List<Object>) getParameter(PRODUCT_ID);
		List<Object> facilityId = (List<Object>) getParameter(FACILITY_ID);
		List<Object> categoryId = (List<Object>) getParameter(CATEGORY_ID);
		String userLoginId = (String) getParameter(USER_LOGIN_ID);
		String checkNPP = (String) getParameter(CHECK_NPP);
		dateType = getDateType(dateType);
		
		OlbiusQuery tmpQuery = OlbiusQuery.make();
		Condition condition = new Condition();
		
		tmpQuery.distinctOn("facility_dim_id", "product_dim_id", "expire_date_dim_id, uom_dim_id, date_dim_id")
				.distinctOn(dateType, dateType != null)
				.distinctOn("date_value", dateType == null)
				.select("*").from("facility_fact")
				.join(Join.INNER_JOIN, "date_dimension", "date_dim_id = date_dimension.dimension_id")
				.where(Condition.makeBetween("date_value", getSqlDate(fromDate), getSqlDate(thruDate)))
				.orderBy("date_value", OlbiusQuery.DESC, dateType == null)
				.orderBy(dateType, OlbiusQuery.DESC, dateType != null);
		
		olbiusQuery = OlbiusQuery.make(getSQLProcessor());
		
		OlbiusQuery tmpQueryNotCategory = OlbiusQuery.make();
		
		tmpQueryNotCategory.select("SUM(inventory_total)", "inventoryTotal")
		.select("SUM(available_to_promise_total)", "availableToPromiseTotal")
		.select("product_dimension.product_code")
		.select("product_dimension.internal_name")
		.select("currency_dimension.currency_id")
		.select("date_dimension.dimension_id")
		.select("date_expire_dimension.date_value", "expire_date")
		.select("facility_dimension.facility_name")
		.select("category_dimension.category_name")
		.from(tmpQuery, "tmp")
		.join(Join.INNER_JOIN, "date_dimension", "tmp.date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "date_expire_dimension", "tmp.expire_date_dim_id = date_expire_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "tmp.product_dim_id = product_dimension.dimension_id")
		.join(Join.LEFT_OUTER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id")
		.join(Join.LEFT_OUTER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'")
		.join(Join.INNER_JOIN, "facility_dimension", "tmp.facility_dim_id = facility_dimension.dimension_id")
		.join(Join.INNER_JOIN, "currency_dimension", "tmp.uom_dim_id = currency_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+userLoginId+"'", checkNPP.equals("NPP_FALSE") == true)
		.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_type_id = "+"'PARTY_GROUP'", checkNPP.equals("NPP_TRUE") == true);
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if (facilityId != null){
			condition.and(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null));
		}
		if(productId != null){
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId!=null));
		}
		if (categoryId != null){
			condition.and(Condition.makeIn("category_dimension.category_id", categoryId, categoryId != null));
		}
		condition.and(Condition.make("available_to_promise_total != 0 OR inventory_total != 0"));
		tmpQueryNotCategory.where(condition)
		.groupBy("product_dimension.product_code")
		.groupBy("product_dimension.internal_name")
		.groupBy("date_dimension.dimension_id")
		.groupBy("expire_date")
		.groupBy("currency_dimension.currency_id")
		.groupBy("facility_dimension.facility_name")
		.groupBy("category_dimension.category_name")
		.orderBy("facility_dimension.facility_name")
		.orderBy("date_dimension.".concat(dateType), OlbiusQuery.DESC);
		
		olbiusQuery.select("queryTmp.inventoryTotal")
		.select("queryTmp.availableToPromiseTotal")
		.select("queryTmp.product_code")
		.select("queryTmp.internal_name")
		.select("queryTmp.currency_id")
		.select("queryTmp.expire_date")
		.select("queryTmp.facility_name")
		.select("date_dimension.".concat(dateType))
		.select("array_to_string(array_agg(queryTmp.category_name), ',')", "category_name")
		.from(tmpQueryNotCategory, "queryTmp")
		.join(Join.INNER_JOIN, "date_dimension", "queryTmp.dimension_id = date_dimension.dimension_id")
		.groupBy("queryTmp.inventoryTotal")
		.groupBy("queryTmp.availableToPromiseTotal")
		.groupBy("queryTmp.product_code")
		.groupBy("queryTmp.internal_name")
		.groupBy("queryTmp.currency_id")
		.groupBy("queryTmp.expire_date")
		.groupBy("queryTmp.facility_name")
		.groupBy("date_dimension.".concat(dateType))
		.orderBy("date_dimension.".concat(dateType), OlbiusQuery.DESC);
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(olbiusQuery == null) {
			initQuery();
		}
		return olbiusQuery;
	}

	public class ResultOutReport extends ReturnResultGrid{
		
		public ResultOutReport() {
			addDataField("date");
			addDataField("facilityId");
			addDataField("productCode");
			addDataField("internalName");
			addDataField("expireDate");
			/*addDataField("dateLife");*/
			addDataField("inventoryTotal");
			addDataField("availableToPromiseTotal");
			addDataField("currencyId");
			addDataField("categoryName");
		}
		
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
			/*Date currentDate = new Date();*/
			try {
				Timestamp expireDate = result.getTimestamp("expire_date");
				String expireDateStr = "";
				if(UtilValidate.isNotEmpty(expireDate)){
					expireDateStr = formatDate.format(expireDate);
				}
				/*int dateLife = UtilDateTime.getIntervalInDays(new Timestamp(currentDate.getTime()), expireDate);*/
				map.put("date", result.getString(dateType));
				map.put("facilityId", result.getString("facility_name"));
				map.put("productCode", result.getString("product_code"));
				map.put("internalName", result.getString("internal_name"));
				map.put("expireDate", expireDateStr);
				/*map.put("dateLife", dateLife);*/
				map.put("inventoryTotal", result.getBigDecimal("inventoryTotal"));
				map.put("availableToPromiseTotal", result.getBigDecimal("availableToPromiseTotal"));
				map.put("currencyId", result.getString("currency_id"));
				map.put("categoryName", result.getString("category_name"));
			} catch (Exception e) {
				Debug.log(e, module);
			}
			return map;
		}
		
	}

}
