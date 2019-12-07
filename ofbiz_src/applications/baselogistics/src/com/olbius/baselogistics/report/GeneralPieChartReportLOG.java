package com.olbius.baselogistics.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

import javolution.util.FastList;

public class GeneralPieChartReportLOG extends AbstractOlap{
	private OlbiusQuery query;
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	public static final String FILTER_TYPE_ID = "FILTER_TYPE_ID";
	
	GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
	
	private void initQuery(){
		String partyFacilityId = (String) getParameter(USER_LOGIN_ID);
		String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
		Condition condition = new Condition();
		List<Object> facilities = getFacilities((List<?>) getParameter("facility"));
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		List<String> listFacilityIds = FastList.newInstance();

		List<GenericValue> listFas = FastList.newInstance();
		try {
			listFas = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", partyFacilityId, "primaryFacilityGroupId", "FACILITY_INTERNAL")), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e.toString(), module);
		}
		if (!listFas.isEmpty()){
			listFacilityIds = EntityUtil.getFieldListFromEntityList(listFas, "facilityId", true);
			facilities = getFacilities(listFacilityIds);
		}
		
		if (!filterTypeId.equals("INVENTORY")){
			query = new OlbiusQuery(getSQLProcessor());
			query.from("inventory_item_fact")
			.select("sum(inventory_item_fact.quantity_on_hand_total)", "inventoryTotal")
			.select("facility_dimension.facility_id")
			.select("facility_dimension.facility_name");
			query.join(Join.INNER_JOIN, "date_dimension", null, "inventory_item_fact.inventory_date_dim_id = date_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "party_dimension", "party_dimension", "inventory_item_fact.owner_party_dim_id = party_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "facility_dimension", null, "inventory_item_fact.facility_dim_id = facility_dimension.dimension_id");
			condition.and(Condition.makeEQ("party_dimension.party_id", partyFacilityId));
			condition.and(Condition.makeIn("inventory_item_fact.facility_dim_id", facilities));
			condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			if(filterTypeId.equals("RECEIVE")){
				condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
			}
			if(filterTypeId.equals("EXPORT")){
				condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "EXPORT"));
			}
			query.where(condition);
			query.groupBy("facility_dimension.facility_id")
			.groupBy("facility_dimension.facility_name");
		} else {
			
			query = OlbiusQuery.make(getSQLProcessor());
			query.select("pff.facility_dim_id").select("facility_dimension.facility_name")
			.select("SUM(quantity)", "inventoryTotal")
			.from("product_facility_fact", "pff")
			.join(Join.INNER_JOIN, "date_dimension", "date_dim_id = date_dimension.dimension_id")
			.join(Join.INNER_JOIN, "facility_dimension", "pff.facility_dim_id = facility_dimension.dimension_id");
			condition.and(Condition.makeBetween("date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			condition.and(Condition.makeIn("pff.facility_dim_id", facilities));
			query.where(condition)
			.groupBy("pff.facility_dim_id")
			.groupBy("facility_dimension.facility_name");
		}
	}
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}

	private List<Object> getFacilities(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = FacilityDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}
	
	public class ResultOutReport implements OlapResultQueryInterface{
		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Object> map = new HashMap<String, Object>();
			String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
			try {
				ResultSet resultSet = query.getResultSet();
				if(!filterTypeId.equals("INVENTORY")){
					while(resultSet.next()) {
						String facilityName = resultSet.getString("facility_name");
						if(UtilValidate.isNotEmpty(facilityName)){
							BigDecimal totalQuantity = resultSet.getBigDecimal("inventoryTotal");
							int totalQuantityInt = totalQuantity.intValue();
							if(filterTypeId.equals("EXPORT")){
								if(totalQuantityInt < 0){
									totalQuantityInt = totalQuantityInt*(-1);
								}
							}
							map.put(facilityName, totalQuantityInt);
						}
					}
				}else{
					while(resultSet.next()) {
						String facilityName = resultSet.getString("facility_name");
						if(UtilValidate.isNotEmpty(facilityName)){
							BigDecimal totalQuantity = resultSet.getBigDecimal("inventoryTotal");
							int totalQuantityInt = totalQuantity.intValue();
							if(totalQuantityInt < 0){
								totalQuantityInt = totalQuantityInt*(-1);
							}
							map.put(facilityName, totalQuantityInt);
						}
					}
				}
			} catch (GenericDataSourceException e) {
				e.printStackTrace();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return map;
		}
		
	}
}
