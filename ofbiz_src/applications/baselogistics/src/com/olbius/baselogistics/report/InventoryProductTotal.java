package com.olbius.baselogistics.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class InventoryProductTotal extends OlbiusOlapService {
	public static final String resource = "BaseLogisticsUiLabels.xml";
	
	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		putParameter("facility", context.get("facility[]"));
		putParameter("product", context.get("product[]"));
		putParameter("filterTop", (Integer) context.get("filterTop"));
		putParameter("filterSort", (String) context.get("filterSort"));
		putParameter("locale", (Locale) context.get("locale"));
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		putParameter("userLogin", userLogin);
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	private OlbiusQuery init() {
		GenericValue userLogin = (GenericValue) getParameter("userLogin");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String dateType = getDateType((String) getParameter("dateType"));
		
		String[] date1 = getSqlDate(fromDate).toString().split("-");
		String fromDateValue = "\'" + date1[0] + "-" + date1[1] + "-" + date1[2] + "\'";
		
		String[] date2 = getSqlDate(thruDate).toString().split("-");
		String thruDateValue = "\'" + date2[0] + "-" + date2[1] + "-" + date2[2] + "\'";
		
		List<Object> productIds = (List<Object>) getParameter("product");
		List<Object> facilityIds = (List<Object>) getParameter("facility");
		
		String productIdStr = null;
		String facilityIdStr = null;
		if (facilityIds != null){
			facilityIdStr = "(";
			for (Object object : facilityIds) {
				facilityIdStr = facilityIdStr + "'"+ object.toString() +"',";
			}
			facilityIdStr = facilityIdStr.substring(0, facilityIdStr.length()-1);
			facilityIdStr = facilityIdStr + ")";
		}
		if(productIds != null){
			productIdStr = "(";
			for (Object object : productIds) {
				productIdStr = productIdStr + "'"+ object.toString() +"',";
			}
			productIdStr = productIdStr.substring(0, productIdStr.length()-1);
			productIdStr = productIdStr + ")";
		}
		
		Integer filterTop = (Integer) getParameter("filterTop");
		
		OlbiusQuery queryProduct = new OlbiusQuery(getSQLProcessor());
		queryProduct = new OlbiusQuery(getSQLProcessor());
		Condition conditionPR = new Condition();
		queryProduct.from("inventory_item_fact")
			.select("product_dimension.product_code");
		queryProduct.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id");
		queryProduct.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		queryProduct.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id");
		queryProduct.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
		queryProduct.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+ownerPartyId+"'");
		conditionPR.and(Condition.make("date_dimension.date_value >= " + fromDateValue))
		.and(Condition.make(thruDateValue + " >= date_dimension.date_value"));
		if (facilityIdStr != null){
			conditionPR.and(Condition.make("facility_dimension.facility_id IN " + facilityIdStr));
		}
		if(productIdStr != null){
			conditionPR.and(Condition.make("product_dimension.product_code IN " + productIdStr));
		}
		String filterSort = (String) getParameter("filterSort");
		if ("EXPORT_DESC".equals(filterSort)){
			conditionPR.and(Condition.make("inventory_item_fact.inventory_type = 'EXPORT'"))
			.and(Condition.make("inventory_item_fact.quantity_on_hand_total < 0"));
			queryProduct.where(conditionPR);
			queryProduct.groupBy("date_dimension.".concat(dateType))
			.groupBy("product_dimension.product_code")
			.groupBy("facility_dimension.facility_id")
			.groupBy("inventory_type")
			.groupBy("quantity_on_hand_total")
			.orderBy("quantity_on_hand_total ASC")
			.limit(filterTop);
		} else if ("EXPORT_ASC".equals(filterSort)){
			conditionPR.and(Condition.make("inventory_item_fact.inventory_type = 'EXPORT'"))
			.and(Condition.make("inventory_item_fact.quantity_on_hand_total < 0"));
			queryProduct.where(conditionPR);
			queryProduct.groupBy("date_dimension.".concat(dateType))
			.groupBy("product_dimension.product_code")
			.groupBy("facility_dimension.facility_id")
			.groupBy("inventory_type")
			.groupBy("quantity_on_hand_total")
			.orderBy("quantity_on_hand_total DESC")
			.limit(filterTop);
		} else if ("RECEIVE_DESC".equals(filterSort)){
			conditionPR.and(Condition.make("inventory_item_fact.inventory_type = 'RECEIVE'"))
			.and(Condition.make("inventory_item_fact.quantity_on_hand_total > 0"));
			queryProduct.where(conditionPR);
			queryProduct.groupBy("date_dimension.".concat(dateType))
			.groupBy("product_dimension.product_code")
			.groupBy("facility_dimension.facility_id")
			.groupBy("inventory_type")
			.groupBy("quantity_on_hand_total")
			.orderBy("quantity_on_hand_total DESC")
			.limit(filterTop);
		} else if ("RECEIVE_ASC".equals(filterSort)){
			conditionPR.and(Condition.make("inventory_item_fact.inventory_type = 'RECEIVE'"))
			.and(Condition.make("inventory_item_fact.quantity_on_hand_total > 0"));
			queryProduct.where(conditionPR);
			queryProduct.groupBy("date_dimension.".concat(dateType))
			.groupBy("product_dimension.product_code")
			.groupBy("facility_dimension.facility_id")
			.groupBy("inventory_type")
			.groupBy("quantity_on_hand_total")
			.orderBy("quantity_on_hand_total ASC")
			.limit(filterTop);
		}
		String productIdStr2 = "(";
		Boolean check = false;
		try {
			ResultSet resultSetProduct = queryProduct.getResultSet();
			while(resultSetProduct.next()) {
				check = true;
				String prId = resultSetProduct.getString("product_code");
				productIdStr2 = productIdStr2 + "'"+ prId +"',";
			}
		} catch (GenericEntityException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (check) {
			productIdStr = null;
			productIdStr2 = productIdStr2.substring(0, productIdStr2.length()-1);
			productIdStr2 = productIdStr2 + ")";
			productIdStr = productIdStr2;
		}
		
		OlbiusQuery queryExport = new OlbiusQuery(getSQLProcessor());
		queryExport = new OlbiusQuery(getSQLProcessor());
		Condition conditionER = new Condition();
		queryExport.from("inventory_item_fact")
			.select("date_dimension.".concat(dateType) + ", product_dimension.product_id, facility_dimension.facility_id, inventory_item_fact.inventory_type, "
					+ "sum(ABS(inventory_item_fact.quantity_on_hand_total))", "quantity");
		queryExport.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id");
		queryExport.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		queryExport.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id");
		queryExport.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
		queryExport.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+ownerPartyId+"'");
		conditionER.and(Condition.make("date_dimension.date_value >= " + fromDateValue))
		.and(Condition.make(thruDateValue + " >= date_dimension.date_value"))
		.and(Condition.make("inventory_item_fact.inventory_type = 'EXPORT'"))
		.and(Condition.make("inventory_item_fact.quantity_on_hand_total < 0"));
		
		if (facilityIdStr != null){
			conditionER.and(Condition.make("facility_dimension.facility_id IN " + facilityIdStr));
		}
		if(productIdStr != null){
			conditionER.and(Condition.make("product_dimension.product_code IN " + productIdStr));
		}
		
		queryExport.where(conditionER);
		queryExport.groupBy("date_dimension.".concat(dateType))
		.groupBy("product_dimension.product_id")
		.groupBy("facility_dimension.facility_id")
		.groupBy("inventory_type");
		
		OlbiusQuery queryReceive = new OlbiusQuery(getSQLProcessor());
		queryReceive = new OlbiusQuery(getSQLProcessor());
		Condition conditionRE = new Condition();
		queryReceive.from("inventory_item_fact")
			.select("date_dimension.".concat(dateType) + ", product_dimension.product_id, facility_dimension.facility_id, inventory_item_fact.inventory_type,"
					+ "sum(inventory_item_fact.quantity_on_hand_total)", "quantity");
		queryReceive.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id");
		queryReceive.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		queryReceive.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id");
		queryReceive.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
		queryReceive.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+ownerPartyId+"'");
		conditionRE.and(Condition.make("date_dimension.date_value >= " + fromDateValue))
		.and(Condition.make(thruDateValue + " >= date_dimension.date_value"))
		.and(Condition.make("inventory_item_fact.inventory_type = 'RECEIVE'"))
		.and(Condition.make("inventory_item_fact.quantity_on_hand_total > 0"));
		if (facilityIdStr != null){
			conditionRE.and(Condition.make("facility_dimension.facility_id IN " + facilityIdStr));
		}
		if(productIdStr != null){
			conditionRE.and(Condition.make("product_dimension.product_code IN " + productIdStr));
		}
		queryReceive.where(conditionRE);
		queryReceive.groupBy("date_dimension.".concat(dateType))
		.groupBy("product_dimension.product_id")
		.groupBy("facility_dimension.facility_id")
		.groupBy("inventory_type");
		
		OlbiusQuery finalQuery = new OlbiusQuery(getSQLProcessor());
		OlbiusQuery unionQuery = new OlbiusQuery(getSQLProcessor());
		unionQuery.select("*")
		.from("(" + queryReceive + ") as x union (" + queryExport + ")")
		.orderBy(dateType);
		
		finalQuery.select(dateType)
		.select("product_id")
		.select("facility_id")
		.select("inventory_type")
		.select("quantity")
		.from(unionQuery, "foo");
		if ("EXPORT_DESC".equals(filterSort)){
			finalQuery.orderBy("inventory_type ASC")
			.orderBy("quantity DESC");
		} else if ("EXPORT_ASC".equals(filterSort)){
			finalQuery.orderBy("inventory_type ASC")
			.orderBy("quantity ASC");
		} else if ("RECEIVE_DESC".equals(filterSort)){
			finalQuery.orderBy("inventory_type DESC")
			.orderBy("quantity DESC");
		} else if ("RECEIVE_ASC".equals(filterSort)){
			finalQuery.orderBy("inventory_type DESC")
			.orderBy("quantity ASC");
		}
//		finalQuery.limit(filterTop);
		return finalQuery;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("product_id", "product_id");
		addDataField("quantity", "quantity");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			addSeries("inventory_type");
			addXAxis("product_id");
			addYAxis("quantity");
		}
	}

}
