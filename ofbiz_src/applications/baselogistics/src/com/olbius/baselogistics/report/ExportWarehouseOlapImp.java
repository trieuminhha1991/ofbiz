package com.olbius.baselogistics.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.olap.bi.accounting.AccountingOlap;

@SuppressWarnings("unused")
public class ExportWarehouseOlapImp extends AbstractOlap implements AccountingOlap{
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String FACILITY_ID = "FACILITY_ID";
	public static final String ENUM_ID = "ENUM_ID";
	public static final String CATEGORY_ID = "CATEGORY_ID";
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	private OlbiusQuery query;
	private String dateType;
	@SuppressWarnings("unchecked")
	private void initQuery(){
		String userLoginId = (String) getParameter(USER_LOGIN_ID);
		dateType = (String) getParameter(DATE_TYPE);
		List<Object> productId = (List<Object>) getParameter(PRODUCT_ID);
		List<Object> facilityId = (List<Object>) getParameter(FACILITY_ID);
		List<Object> enumId = (List<Object>) getParameter(ENUM_ID);
		List<Object> categoryId = (List<Object>) getParameter(CATEGORY_ID);
		
		dateType = getDateType(dateType);
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		
		OlbiusQuery tmpQuery = OlbiusQuery.make();
		Condition conditionTmp = new Condition();
		
		tmpQuery.from("inventory_item_fact")
		.select("date_dimension.dimension_id")
		.select("product_dimension.product_code")
		.select("product_dimension.internal_name")
		.select("inventory_item_fact.datetime_manufactured")
		.select("inventory_item_fact.expire_date")  
		.select("inventory_item_fact.lot_id")  
		.select("sum(inventory_item_fact.quantity_on_hand_total)", "quantityOnHandTotal")
		.select("currency_dimension.currency_id", "uom_id")
		.select("facility_dimension.facility_name")
		.select("category_dimension.category_name")
		.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id")
		.join(Join.LEFT_OUTER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id")
		.join(Join.LEFT_OUTER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'")
		.join(Join.INNER_JOIN, "currency_dimension", null, "inventory_item_fact.uom_dim_id = currency_dimension.dimension_id")
		.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+userLoginId+"'")
		.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+userLoginId+"'");
		conditionTmp.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		conditionTmp.and(Condition.make("inventory_item_fact.quantity_on_hand_total != 0 OR inventory_item_fact.available_to_promise_total >= 0")); 
		if (categoryId != null){
			conditionTmp.and(Condition.makeIn("category_dimension.category_id", categoryId, categoryId != null));
		}
		if (facilityId != null){
			conditionTmp.and(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null));
		}
		if(productId != null){
			conditionTmp.and(Condition.makeIn("product_dimension.product_code", productId, productId!=null));
		}
		conditionTmp.and(Condition.makeEQ("inventory_item_fact.inventory_type", "EXPORT"));
		conditionTmp.and(Condition.make("inventory_item_fact.quantity_on_hand_total != 0"));
		tmpQuery.where(conditionTmp)
		.groupBy("date_dimension.dimension_id")
		.groupBy("product_dimension.product_code")
		.groupBy("product_dimension.internal_name")
		.groupBy("inventory_item_fact.datetime_manufactured")
		.groupBy("inventory_item_fact.expire_date")
		.groupBy("inventory_item_fact.lot_id")
		.groupBy("uom_id")
		.groupBy("facility_dimension.facility_name")
		.groupBy("category_dimension.category_name")
		.orderBy("date_dimension.".concat(dateType), OlbiusQuery.DESC);
		
		
		query.select("tmp.product_code")
		.select("date_dimension.".concat(dateType))
		.select("tmp.internal_name")
		.select("tmp.datetime_manufactured")
		.select("tmp.expire_date")
		.select("tmp.lot_id")
		.select("tmp.uom_id")
		.select("tmp.facility_name")
		.select("tmp.quantityOnHandTotal")
		.select("array_to_string(array_agg(tmp.category_name), ',')", "category_name")
		.from(tmpQuery, "tmp")
		.join(Join.INNER_JOIN, "date_dimension", "tmp.dimension_id = date_dimension.dimension_id")
		.groupBy("date_dimension.".concat(dateType))
		.groupBy("tmp.product_code")
		.groupBy("tmp.internal_name")
		.groupBy("tmp.datetime_manufactured")
		.groupBy("tmp.expire_date")
		.groupBy("tmp.lot_id")
		.groupBy("tmp.uom_id")
		.groupBy("tmp.facility_name")
		.groupBy("tmp.quantityOnHandTotal")
		.orderBy("date_dimension.".concat(dateType), OlbiusQuery.DESC);
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}

	public class ResultOutReport extends ReturnResultGrid{
		public ResultOutReport(){
			addDataField("date");
			addDataField("facilityName");
			/*addDataField("orderId");
			addDataField("returnId");  
			addDataField("transferId");*/
			addDataField("productId");
			addDataField("internalName");
			addDataField("datetimeManufactured");
			addDataField("expireDate");
			addDataField("lotId");
			addDataField("quantityOnHandTotal");
			addDataField("uomId");
			addDataField("categoryName");
		}
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
			try {
				Timestamp datetimeManufactured = result.getTimestamp("datetime_manufactured");
				Timestamp expireDate = result.getTimestamp("expire_date");
				String datetimeManufacturedStr = "";
				String expireDateStr = "";
				if(UtilValidate.isNotEmpty(datetimeManufactured)){
					datetimeManufacturedStr = formatDate.format(datetimeManufactured);
				}
				if(UtilValidate.isNotEmpty(expireDate)){
					expireDateStr = formatDate.format(expireDate);
				}
				
				/*String deliveryTransferId = result.getString("delivery_transfer_id");
				String deliveryId = result.getString("delivery_id");
				if(UtilValidate.isEmpty(deliveryId)){
					deliveryId = deliveryTransferId;
				}*/
				
				BigDecimal quantityOnHandTotal = result.getBigDecimal("quantityOnHandTotal");
				if(quantityOnHandTotal.intValue() < 0){
					quantityOnHandTotal = new BigDecimal(quantityOnHandTotal.intValue() * (-1));
				}
				
				map.put("date", result.getString(dateType));
				map.put("productId", result.getString("product_code"));
				map.put("internalName", result.getString("internal_name")); 
				map.put("datetimeManufactured", datetimeManufacturedStr); 
				map.put("expireDate", expireDateStr);
				map.put("lotId", result.getString("lot_id"));
				/*map.put("orderId", result.getString("order_id")); 
				map.put("returnId", result.getString("return_id"));
				map.put("transferId", result.getString("transfer_id"));*/
				map.put("quantityOnHandTotal", quantityOnHandTotal);
				map.put("uomId", result.getString("uom_id"));
				map.put("facilityName", result.getString("facility_name"));
				map.put("categoryName", result.getString("category_name"));
			} catch (Exception e) {
				Debug.logError(e, module);
			}
			return map;
		}
		
	}
}
