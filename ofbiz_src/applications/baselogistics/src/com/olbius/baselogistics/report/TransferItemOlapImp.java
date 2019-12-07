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
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.olap.bi.accounting.AccountingOlap;

public class TransferItemOlapImp extends AbstractOlap implements AccountingOlap{
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String FACILITY_ID = "FACILITY_ID";
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	private OlbiusQuery query;
	private String dateType;
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		String userLoginId = (String) getParameter(USER_LOGIN_ID);
		dateType = (String) getParameter(DATE_TYPE);
		List<Object> productId = (List<Object>) getParameter(PRODUCT_ID);
		List<Object> facilityId = (List<Object>) getParameter(FACILITY_ID);
		
		dateType = getDateType(dateType);
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
			query.from("transfer_item_fact")
			.select("date_dimension.".concat(dateType))
			.select("transfer_item_fact.transfer_id")
			.select("transfer_item_fact.transfer_type_id")
			.select("product_dimension.product_code")
			.select("product_dimension.internal_name")
			.select("transfer_item_fact.datetime_manufactured")
			.select("transfer_item_fact.expire_date")  
			.select("transfer_item_fact.lot_id")  
			.select("transfer_item_fact.quantity")
			.select("status_dimension.status_id")
			.select("transfer_item_fact.actual_exported_quantity")
			.select("currency_dimension.description", "uomId")
			.select("transfer_item_fact.delivery_id")
			.select("transfer_item_fact.delivery_status_id")
			.select("party_organization.description", "partyName")
			.select("origin_facility_dimension.facility_name", "originFacilityName")
			.select("dest_facility_dimension.facility_name", "destFacilityName");
			query.join(Join.INNER_JOIN, "date_dimension", null, "create_date_dim_id = date_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "currency_dimension", null, "transfer_item_fact.quantity_uom_dim_id = currency_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "status_dimension", null, "transfer_item_fact.status_transfer_dim_id = status_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "facility_dimension", "origin_facility_dimension", "origin_facility_dim_id = origin_facility_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "facility_dimension", "dest_facility_dimension", "dest_facility_dim_id = dest_facility_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "origin_facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+userLoginId+"'");
			query.join(Join.INNER_JOIN, "party_dimension", "party_organization", "transfer_item_fact.party_dim_id = party_organization.dimension_id");
			condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			if (facilityId != null){
				condition.and(Condition.makeIn("origin_facility_dimension.facility_id", facilityId, facilityId != null));
			}
			if(productId != null){
				condition.and(Condition.makeIn("product_dimension.product_code", productId, productId!=null));
			}
			query.where(condition)
			.orderBy("date_dimension.".concat(dateType), OlbiusQuery.DESC)
			.orderBy("transfer_item_fact.transfer_id")
			.orderBy("origin_facility_dimension.facility_name");
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
			addDataField("transferId"); 
			addDataField("transferTypeId");
			addDataField("originFacilityName");
			addDataField("destFacilityName");
			addDataField("productCode");
			addDataField("internalName");
			addDataField("datetimeManufactured");
			addDataField("expireDate");
			addDataField("quantity");
			addDataField("statusId");
			addDataField("deliveryId");
			addDataField("deliveryStatusId");
			addDataField("actualExportedQuantity");
			addDataField("uomId");
			addDataField("partyName");
			addDataField("lotId");
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
				map.put("date", result.getString(dateType));
				map.put("transferId", result.getString("transfer_id"));
				map.put("transferTypeId", result.getString("transfer_type_id"));
				map.put("originFacilityName", result.getString("originFacilityName"));
				map.put("destFacilityName", result.getString("destFacilityName"));
				map.put("productCode", result.getString("product_code"));
				map.put("internalName", result.getString("internal_name"));
				map.put("datetimeManufactured", datetimeManufacturedStr); 
				map.put("expireDate", expireDateStr);
				map.put("quantity", result.getBigDecimal("quantity"));
				map.put("statusId", result.getString("status_id"));
				map.put("deliveryId", result.getString("delivery_id"));
				map.put("deliveryStatusId", result.getString("delivery_status_id"));
				map.put("actualExportedQuantity", result.getBigDecimal("actual_exported_quantity"));
				map.put("uomId", result.getString("uomId"));
				map.put("partyName", result.getString("partyName"));
				map.put("lotId", result.getString("lot_id"));
			} catch (Exception e) {
				Debug.logError(e, module);
			}
			return map;
		}
		
	}

}
