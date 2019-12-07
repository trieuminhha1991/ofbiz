package com.olbius.baselogistics.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.Query;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.olap.AbstractOlap;
import com.olbius.olap.OlapInterface;
import com.sun.org.apache.bcel.internal.generic.Select;
public class LOGOlapChart extends AbstractOlap implements OlapInterface{
	public static final String resource = "BaseLogisticsUiLabels.xml";
	public void reportReceiveWarehouseChartLine(String dateType, List<Object> productId, List<Object> facilityId, String ownerPartyId, List<Object> categoryId) throws GenericDataSourceException, GenericEntityException, SQLException {
		dateType = getDateType(dateType);
		/*GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");*/
		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
			query.from("inventory_item_fact")
			.select("date_dimension.".concat(dateType))
			.select("product_dimension.product_code")
			.select("facility_dimension.facility_id")
			.select("category_dimension.category_id", categoryId != null)
			.select("sum(inventory_item_fact.quantity_on_hand_total)", "quantityOnHandTotal");
			query.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id", categoryId != null);
			query.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'", categoryId != null);
			query.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
			query.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+ownerPartyId+"'");
			/*condition.and(Condition.makeEQ("party_dimension.party_id", ownerPartyId));
			condition.and(Condition.makeEQ("facility_party.party_id", ownerPartyId));*/
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
			condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
			query.where(condition);
			query.groupBy("date_dimension.".concat(dateType))
			.groupBy("product_dimension.product_code")
			.groupBy("category_dimension.category_id", categoryId != null)
			.groupBy("facility_dimension.facility_id")
			.orderBy("date_dimension.".concat(dateType));
			
		ResultSet resultSet = query.getResultSet();
		
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>();
		
		while(resultSet.next()) {
			String productIdOut = resultSet.getString("product_code");
			String internalName = productIdOut;
			/*GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productIdOut), false);
			if(product != null){
				internalName = product.getString("internalName");
			}*/
			if(map.get(internalName)==null) {
				map.put(productIdOut, new HashMap<String, Object>());
			}
			int quantityOnHandTotal = resultSet.getInt("quantityOnHandTotal");
			map.get(productIdOut).put(resultSet.getString(dateType), quantityOnHandTotal);
		}
		
		axis(map, dateType);
	}
	public void productReceiveQOH(String dateType, String facilityId, String productId, String ownerPartyId, Locale locale) throws GenericDataSourceException, GenericEntityException, SQLException {
		dateType = getDateType(dateType);
		String titleExport = UtilProperties.getMessage(resource, "Export", locale); 
		String titleReceive = UtilProperties.getMessage(resource, "Receive", locale); 
		String titleInventory = UtilProperties.getMessage(resource, "Inventory", locale); 
		OlbiusQuery queryReceive = new OlbiusQuery(getSQLProcessor());
		queryReceive = new OlbiusQuery(getSQLProcessor());
		Condition conditionReceive = new Condition();
		queryReceive.from("inventory_item_fact")
			.select("date_dimension.".concat(dateType))
			.select("product_dimension.product_id")
			.select("facility_dimension.facility_id")
			.select("sum(inventory_item_fact.quantity_on_hand_total)", "quantityOnHandTotal");
		queryReceive.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id");
		queryReceive.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		queryReceive.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id");
		queryReceive.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
		queryReceive.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+ownerPartyId+"'");
		conditionReceive.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			if (facilityId != null){
				conditionReceive.and(Condition.makeEQ("facility_dimension.facility_id", facilityId, facilityId != null));
			}
			if(productId != null){
				conditionReceive.and(Condition.makeEQ("product_dimension.product_id", productId, productId!=null));
			}
			conditionReceive.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
			queryReceive.where(conditionReceive);
			queryReceive.groupBy("date_dimension.".concat(dateType))
			.groupBy("product_dimension.product_id")
			.groupBy("facility_dimension.facility_id")
			.orderBy("date_dimension.".concat(dateType));
			
		
		OlbiusQuery queryExport = new OlbiusQuery(getSQLProcessor());
		queryExport = new OlbiusQuery(getSQLProcessor());
		Condition conditionExport = new Condition();
			queryExport.from("inventory_item_fact")
				.select("date_dimension.".concat(dateType))
				.select("product_dimension.product_id")
				.select("facility_dimension.facility_id")
				.select("sum(inventory_item_fact.quantity_on_hand_total)", "quantityOnHandTotal");
			queryExport.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id")
			.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id")
			.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'")
			.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+ownerPartyId+"'");
			conditionExport.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
				if (facilityId != null){
					conditionExport.and(Condition.makeEQ("facility_dimension.facility_id", facilityId, facilityId != null));
				}
				if(productId != null){
					conditionExport.and(Condition.makeEQ("product_dimension.product_id", productId, productId!=null));
				}
				conditionExport.and(Condition.makeEQ("inventory_item_fact.inventory_type", "EXPORT"));
				queryExport.where(conditionExport);
				queryExport.groupBy("date_dimension.".concat(dateType))
				.groupBy("product_dimension.product_id")
				.groupBy("facility_dimension.facility_id")
				.orderBy("date_dimension.".concat(dateType));
				
			OlbiusQuery queryInven = new OlbiusQuery(getSQLProcessor());
			
			OlbiusQuery tmpQuery = OlbiusQuery.make();
			
			String[] date2 = getSqlDate(thruDate).toString().split("-");
			String thruDateValue = "\'" + date2[0] + "-" + date2[1] + "-" + date2[2] + "\'";
			String[] date1 = getSqlDate(fromDate).toString().split("-");
			String fromDateValue = "\'" + date1[0] + "-" + date1[1] + "-" + date1[2] + "\'";
			tmpQuery.distinctOn("facility_fact.date_dim_id")
					.distinctOn("manufactured_date_dim_id")
					.distinctOn("expire_date_dim_id")
					.select("facility_fact.*")
					.select("facility_id").select("product_id") 
					.select("date_dimension.".concat(dateType))
					.from("facility_fact")
					.join(Join.INNER_JOIN, "date_dimension", "facility_fact.date_dim_id = date_dimension.dimension_id")
					.join(Join.INNER_JOIN, "facility_dimension", "facility_fact.facility_dim_id = facility_dimension.dimension_id")
					.join(Join.INNER_JOIN, "product_dimension", "facility_fact.product_dim_id = product_dimension.dimension_id")
					.where(Condition.make("date_value <= " + fromDateValue)
							.and(Condition.makeEQ("facility_dimension.facility_id", facilityId, facilityId != null))
							.and(Condition.makeEQ("product_dimension.product_id", productId, productId != null))
							.and(Condition.make("facility_fact.available_to_promise_total != 0 OR facility_fact.inventory_total != 0")))
					.orderBy("date_dim_id", OlbiusQuery.DESC);
			
			OlbiusQuery ttmpQuery = OlbiusQuery.make();
			ttmpQuery.distinct()
			.select("expire_date_dim_id")
			.select("manufactured_date_dim_id")
			.select("facility_dim_id")
			.select("product_id")
			.select("facility_id")
			.select("product_dim_id")
			.select("max( date_dim_id ) as date_dim_max")
			.select("max(".concat(dateType).concat(") as ").concat(dateType))
			.from(tmpQuery, "ttmp")
			.groupBy("expire_date_dim_id")
			.groupBy("manufactured_date_dim_id")
			.groupBy("facility_dim_id")
			.groupBy("product_dim_id")
			.groupBy("product_id")
			.groupBy("facility_id")
			.orderBy("max( date_dim_id )", OlbiusQuery.DESC);

			OlbiusQuery queryTmp2 = new OlbiusQuery(getSQLProcessor());
			queryTmp2.select("SUM(inventory_total)", "inventoryTotal")
			.select("product_id")
			.select("facility_id")
			.select(dateType)
			.from(ttmpQuery, "tmp")
			.join(Join.INNER_JOIN, "facility_fact as ff", " tmp.expire_date_dim_id = ff.expire_date_dim_id and ff.manufactured_date_dim_id = tmp.manufactured_date_dim_id and ff.date_dim_id = tmp.date_dim_max and ff.facility_dim_id = tmp.facility_dim_id and ff.product_dim_id = tmp.product_dim_id")
			.groupBy("product_id")
			.groupBy("facility_id")
			.groupBy(dateType);
			
			queryInven.select("max(".concat(dateType).concat(") as ").concat(dateType))
			.select("product_id")
			.select("facility_id")
			.select("sum( inventoryTotal ) as inventoryTotal")
			.from(queryTmp2, "aaa")
			.groupBy("product_id")
			.groupBy("facility_id");

		OlbiusQuery subQuerry = new OlbiusQuery(getSQLProcessor());
		OlbiusQuery subQuerry1 = new OlbiusQuery(getSQLProcessor());
		OlbiusQuery subQuerry2 = new OlbiusQuery(getSQLProcessor());
		OlbiusQuery subQuerry3 = new OlbiusQuery(getSQLProcessor());
		OlbiusQuery subQuerry4 = new OlbiusQuery(getSQLProcessor());
		
		subQuerry1.select(""+dateType+", year_name, dimension_id")
		.from("date_dimension as DD")
		.where (Condition.make("DD.date_value between "+fromDateValue+ " and " + thruDateValue))
		.groupBy(dateType, "year_name", "dimension_id");
		
		subQuerry2.select("product_dimension.product_id, facility_dimension.facility_id, sum( II.quantity_on_hand_total ) as SU, DD.".concat(dateType) + ", DD.year_name")
		.from("date_dimension as DD, inventory_item_fact as II")
		.join(Join.INNER_JOIN, "facility_dimension", "II.facility_dim_id = facility_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "II.product_dim_id = product_dimension.dimension_id")
		.groupBy("product_dimension.product_id, facility_dimension.facility_id, DD.".concat(dateType)+", DD.year_name");
		
		Condition tmpCondition = new Condition();
		tmpCondition.and("(DD.".concat(dateType)+", DD.year_name, DD.dimension_id) in(" + subQuerry1.toString() + ")")
		.and("II.inventory_type in(\'EXPORT\', \'RECEIVE\')")
		.and("II.inventory_date_dim_id = DD.dimension_id");
		if (facilityId != null){
			tmpCondition.and(Condition.make("facility_dimension.facility_id = \'" + facilityId + "\'"));
		}
		if(productId != null){
			tmpCondition.and(Condition.make("product_dimension.product_id = \'" + productId + "\'"));
		}
		subQuerry2.where(tmpCondition);
		
		subQuerry3.select("\'" + productId + "\' as product_id, \'" + facilityId + "\' as facility_id, \'0\' as SU, " + dateType + ", year_name")
		.from("date_dimension as dd")
		.where(Condition.make("dd.date_value between "+fromDateValue+ " and " + thruDateValue));
			
		subQuerry4.select("*")
		.from("((" +subQuerry2.toString() + ") union (" + subQuerry3 + ")) as abc")
		.orderBy("abc.".concat(dateType));
		
		subQuerry.select("gg.product_id, gg.facility_id, gg.".concat(dateType)+ ", gg.year_name, sum( gg.su ) as total")
		.from(subQuerry4, "gg")
		.groupBy("gg.product_id")
		.groupBy("gg.facility_id")
		.groupBy("gg.".concat(dateType))
		.groupBy("gg.year_name")
		.orderBy(dateType);
		
		ResultSet resultSetReceive = queryReceive.getResultSet();
		ResultSet resultSetExport = queryExport.getResultSet();
		ResultSet resultSetFirstDayInven = queryInven.getResultSet();
		ResultSet resultSetInvenByDay = subQuerry.getResultSet();
		
		int fromInventoryTotal = 0;
		while(resultSetFirstDayInven.next()) {
			fromInventoryTotal = resultSetFirstDayInven.getInt("inventoryTotal");
		}
		
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>();
		int i = 0;
		while(resultSetInvenByDay.next()) {
			String inven = titleInventory;
			int total = resultSetInvenByDay.getInt("total");
			if (i != 0){
				fromInventoryTotal = fromInventoryTotal + total;
			}
			if(map.get(inven)==null) {
				map.put(inven, new HashMap<String, Object>());
			}
			map.get(inven).put(resultSetInvenByDay.getString(dateType), fromInventoryTotal);
			i = i + 1;
		}
		
		while(resultSetReceive.next()) {
			String receive = titleReceive;    
			int quantityOnHandTotal = resultSetReceive.getInt("quantityOnHandTotal");
			if(map.get(receive)==null) {
				map.put(receive, new HashMap<String, Object>());
			}
			map.get(receive).put(resultSetReceive.getString(dateType), quantityOnHandTotal);
		}
		
		while(resultSetExport.next()) {
			String export = titleExport;
			int quantityOnHandTotal = resultSetExport.getInt("quantityOnHandTotal");
			if(quantityOnHandTotal < 0){
				quantityOnHandTotal = quantityOnHandTotal * (-1); 
			}
			if(map.get(export)==null) {
				map.put(export, new HashMap<String, Object>());
			}
			map.get(export).put(resultSetExport.getString(dateType), quantityOnHandTotal);
		}
		
		axis(map, dateType);
	}
	
	public void reportInventoryChartLine(String dateType, List<Object> productId,  List<Object> facilityId, String ownerPartyId) throws GenericDataSourceException, GenericEntityException, SQLException {
			if(dateType != null) {
				dateType = getDateType(dateType);
			}
			OlbiusQuery tmpQuery = OlbiusQuery.make();
			
			tmpQuery.distinctOn("facility_dim_id", "product_dim_id", "expire_date_dim_id")
					.distinctOn(dateType, dateType != null)
					.distinctOn("date_value", dateType == null)
					.select("*").from("facility_fact")
					.join(Join.INNER_JOIN, "date_dimension", "date_dim_id = date_dimension.dimension_id")
					.where(Condition.makeBetween("date_value", getSqlDate(fromDate), getSqlDate(thruDate))).orderBy("date_value", OlbiusQuery.DESC, dateType == null)
					.orderBy(dateType, OlbiusQuery.DESC, dateType != null);
			
			OlbiusQuery olbiusQuery = OlbiusQuery.make(getSQLProcessor());
			
			boolean facility = (UtilValidate.isNotEmpty(facilityId)) || (UtilValidate.isNotEmpty(dateType) && UtilValidate.isNotEmpty(productId));
			
			olbiusQuery.distinctOn("product_id", dateType == null)
				.select("SUM(inventory_total)", "total")
				.select("product_dimension.product_code")
				.select(dateType, dateType != null)
				.select("date_value", dateType == null)
				.select("facility_id", facility)
				.from(tmpQuery, "tmp")
				.join(Join.INNER_JOIN, "product_dimension", "product_dim_id = product_dimension.dimension_id")
				.join(Join.INNER_JOIN, "facility_dimension", "facility_dim_id = facility_dimension.dimension_id", facility)
				.where(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null && !facilityId.isEmpty())
						.andIn("product_dimension.product_id", productId, productId != null && !productId.isEmpty()))
				.groupBy("product_dimension.product_code")
				.groupBy("facility_id", facility)
				.groupBy(dateType, dateType != null)
				.groupBy("date_value", dateType == null)
				.orderBy(dateType, dateType != null)
				.orderBy("product_dimension.product_code")
				.orderBy("date_value", OlbiusQuery.DESC, dateType == null);
			
		ResultSet resultSet = olbiusQuery.getResultSet();
		
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>();
		
		while(resultSet.next()) {
			String productIdOut = resultSet.getString("product_code");
			String internalName = productIdOut;
			if(map.get(internalName)==null) {
				map.put(productIdOut, new HashMap<String, Object>());
			}
			BigDecimal quantityOnHandTotalBig = resultSet.getBigDecimal("total");
			int quantityOnHandTotal = quantityOnHandTotalBig.intValue();
			if(quantityOnHandTotal < 0){
				quantityOnHandTotal = quantityOnHandTotal * (-1); 
			}
			map.get(productIdOut).put(resultSet.getString(dateType), quantityOnHandTotal);
		}
		
		axis(map, dateType);
	}
	
	public void reportExportWarehouseChartLine(String dateType, List<Object> productId, List<Object> facilityId, String ownerPartyId, List<Object> categoryId) throws GenericDataSourceException, GenericEntityException, SQLException {
		dateType = getDateType(dateType);
		/*GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");*/
		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
			query.from("inventory_item_fact")
			.select("date_dimension.".concat(dateType))
			.select("product_dimension.product_code")
			.select("category_dimension.category_id", categoryId != null)
			.select("facility_dimension.facility_id")
			.select("sum(inventory_item_fact.quantity_on_hand_total)", "quantityOnHandTotal");
			query.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id", categoryId != null);
			query.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'", categoryId != null);
			query.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
			query.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+ownerPartyId+"'");
			/*condition.and(Condition.makeEQ("party_dimension.party_id", ownerPartyId));
			condition.and(Condition.makeEQ("facility_party.party_id", ownerPartyId));*/
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
			condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "EXPORT"));
			query.where(condition);
			query.groupBy("date_dimension.".concat(dateType))
			.groupBy("product_dimension.product_code")
			.groupBy("category_dimension.category_id", categoryId != null)
			.groupBy("facility_dimension.facility_id")
			.orderBy("date_dimension.".concat(dateType));
			
		ResultSet resultSet = query.getResultSet();
		
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>();
		
		while(resultSet.next()) {
			String productIdOut = resultSet.getString("product_code");
			/*GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productIdOut), false);*/
			String internalName = productIdOut;
			/*if(product != null){
				internalName = product.getString("internalName");
			}*/
			if(map.get(internalName)==null) {
				map.put(productIdOut, new HashMap<String, Object>());
			}
			int quantityOnHandTotal = resultSet.getInt("quantityOnHandTotal") * (-1);
			map.get(productIdOut).put(resultSet.getString(dateType), quantityOnHandTotal);
		}
		
		axis(map, dateType);
	}
	
	public void receiveWarehouseChart(List<Object> productId, List<Object> facilityId, String ownerPartyId, List<Object> categoryId, String limitId, String filterTypeId) throws GenericDataSourceException, GenericEntityException, SQLException {
		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		query.from("inventory_item_fact")
		.select("product_dimension.product_code")
		.select("sum(inventory_item_fact.quantity_on_hand_total)", "quantity");
		query.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id", categoryId != null);
		query.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'", categoryId != null);
		query.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
		query.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+ownerPartyId+"'");
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
		condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
		query.where(condition);
		/*query.groupBy("facility_dimension.facility_id")*/
		query.groupBy("product_dimension.product_code");
		/*.groupBy("enumeration_dimension.enum_id", enumId != null)
		.groupBy("category_dimension.category_id", categoryId != null);*/
		if(filterTypeId.equals("FILTER_MIN")){ 
			query.orderBy("quantity", OlbiusQuery.ASC, true);
		}
		if(filterTypeId.equals("FILTER_MAX")){
			query.orderBy("quantity", OlbiusQuery.DESC, true);
		}
		if(limitId != null){
			int	limitIdInt = Integer.parseInt(limitId);
			query.limit(limitIdInt);
		}
		ResultSet resultSet = query.getResultSet();
		
		while(resultSet.next()) {
			String _key = resultSet.getString("product_code");
			/*GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", _key), false);*/
			/*String internalName = product.getString("internalName");*/
			if(yAxis.get("test") == null) {
				yAxis.put("test", new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity = resultSet.getBigDecimal("quantity");
			yAxis.get("test").add(quantity);
		}
		
	} 
	
	public void exportWarehouseChart(List<Object> productId, List<Object> facilityId, String ownerPartyId, List<Object> categoryId, List<Object> enumId, String limitId, String filterTypeId) throws GenericDataSourceException, GenericEntityException, SQLException {
		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		/*GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");*/
		Condition condition = new Condition();
		query.from("inventory_item_fact")
		.select("product_dimension.product_code")
		/*.select("facility_dimension.facility_id")*/
		/*.select("enumeration_dimension.enum_id", enumId != null)
		.select("category_dimension.category_id", categoryId != null)*/
		.select("sum(inventory_item_fact.quantity_on_hand_total)", "quantity");
		query.join(Join.INNER_JOIN, "date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "enumeration_dimension", null, "enum_dim_id = enumeration_dimension.dimension_id", enumId != null);
		query.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id", categoryId != null);
		query.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'", categoryId != null);
		query.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
		query.join(Join.INNER_JOIN, "party_dimension", "party_organization", "inventory_item_fact.owner_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+ownerPartyId+"'");
		/*condition.and(Condition.makeEQ("party_dimension.party_id", ownerPartyId));
		condition.and(Condition.makeEQ("facility_party.party_id", ownerPartyId));*/
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if (facilityId != null){
			condition.and(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null));
		}
		if(productId != null){
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId!=null));
		}
		if (enumId != null){
			condition.and(Condition.makeIn("enumeration_dimension.enum_id", enumId, enumId != null));
		}
		if (categoryId != null){
			condition.and(Condition.makeIn("category_dimension.category_id", categoryId, categoryId != null));
		}
		condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "EXPORT"));
		query.where(condition);
		/*query.groupBy("facility_dimension.facility_id")*/
		/*.groupBy("enumeration_dimension.enum_id", enumId != null)
		.groupBy("category_dimension.category_id", categoryId != null)*/
		query.groupBy("product_dimension.product_code");
		if(filterTypeId.equals("FILTER_MIN")){ 
			query.orderBy("quantity", OlbiusQuery.DESC, true);
		}
		if(filterTypeId.equals("FILTER_MAX")){
			query.orderBy("quantity", OlbiusQuery.ASC, true);
		}
		if(limitId != null){
			int	limitIdInt = Integer.parseInt(limitId);
			query.limit(limitIdInt);
		}
		ResultSet resultSet = query.getResultSet();
		
		while(resultSet.next()) {
			String _key = resultSet.getString("product_code");
			/*GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", _key), false);*/
			/*String internalName = product.getString("internalName");*/
			if(yAxis.get("test") == null) {
				yAxis.put("test", new ArrayList<Object>());
			}
			xAxis.add(_key);
			int quantityInt = resultSet.getBigDecimal("quantity").intValue();
			int quantity = quantityInt;
			if(quantityInt < 0){
				quantity = quantityInt * (-1);
			}
			yAxis.get("test").add(quantity);
		}
		
	}
	
	public void inventoryWarehouseChart(List<Object> productId, List<Object> facilityId, String limitId, String filterTypeId, String ownerPartyId) throws GenericDataSourceException, GenericEntityException, SQLException {
		String dateType = "DAY";
		if(dateType != null) {
			dateType = getDateType(dateType);
		}
			
		OlbiusQuery olbiusQuery = OlbiusQuery.make(getSQLProcessor());	
		OlbiusQuery tmpQuery = OlbiusQuery.make();
		Condition condition = new Condition();
		
		tmpQuery.distinctOn("facility_dim_id", "product_dim_id", "date_dim_id")
				.distinctOn(dateType, dateType != null)
				.distinctOn("date_value", dateType == null)
				.select("*").from("facility_fact")
				.join(Join.INNER_JOIN, "date_dimension", "date_dim_id = date_dimension.dimension_id")
				.where(Condition.makeBetween("date_value", getSqlDate(fromDate), getSqlDate(thruDate)))
				.orderBy("date_value", OlbiusQuery.DESC, dateType == null)
				.orderBy(dateType, OlbiusQuery.DESC, dateType != null);
		
		olbiusQuery = OlbiusQuery.make(getSQLProcessor());
		
		
		olbiusQuery.select("SUM(inventory_total)", "inventoryTotal")
		.select("product_dimension.product_code")
		.select("facility_dimension.facility_id", facilityId != null)
		.select("date_dimension.".concat(dateType))
		.from(tmpQuery, "tmp")
		.join(Join.INNER_JOIN, "date_dimension", "tmp.date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "tmp.product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "facility_dimension", "tmp.facility_dim_id = facility_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
		if (facilityId != null){
			condition.and(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null));
		}
		if(productId != null){
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId!=null));
		}
		olbiusQuery.where(condition)
		.groupBy("product_dimension.product_code")
		.groupBy("facility_dimension.facility_id", facilityId != null)
		.groupBy("date_dimension.".concat(dateType));
		if(filterTypeId.equals("FILTER_MIN")){ 
			olbiusQuery.orderBy("inventoryTotal", OlbiusQuery.DESC, true);
		}
		if(filterTypeId.equals("FILTER_MAX")){
			olbiusQuery.orderBy("inventoryTotal", OlbiusQuery.ASC, true);
		}
		if(limitId != null){
			int	limitIdInt = Integer.parseInt(limitId);
			olbiusQuery.limit(limitIdInt);
		}
			
		ResultSet resultSet = olbiusQuery.getResultSet();
		
		while(resultSet.next()) {
			String _key = resultSet.getString("product_code");
			if(yAxis.get("test") == null) {
				yAxis.put("test", new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity = resultSet.getBigDecimal("inventoryTotal");
			int quantityInt = quantity.intValue();
			if(quantityInt < 0){
				quantityInt = quantityInt * (-1);
			}
			yAxis.get("test").add(quantityInt);
		}
	}
	
	public void inventoryBookChart(List<Object> productId, List<Object> facilityId, String limitId, String filterTypeId, String ownerPartyId) throws GenericDataSourceException, GenericEntityException, SQLException {
		String dateType = "DAY";
		String inventoryType = "EXPORT";
		Condition condition = new Condition();
		if(dateType != null) {
			dateType = getDateType(dateType);
		}
			
		/*OlbiusQuery olbiusQuery = OlbiusQuery.make(getSQLProcessor());	
		OlbiusQuery tmpQuery = OlbiusQuery.make();
		Condition condition = new Condition();
		
		tmpQuery.distinctOn("facility_dim_id", "product_dim_id", "date_dim_id")
				.distinctOn(dateType, dateType != null)
				.distinctOn("date_value", dateType == null)
				.select("*").from("facility_fact")
				.join(Join.INNER_JOIN, "date_dimension", "date_dim_id = date_dimension.dimension_id")
				.where(Condition.makeBetween("date_value", getSqlDate(fromDate), getSqlDate(thruDate)))
				.orderBy("date_value", OlbiusQuery.DESC, dateType == null)
				.orderBy(dateType, OlbiusQuery.DESC, dateType != null);
		
		olbiusQuery = OlbiusQuery.make(getSQLProcessor());
		
		
		olbiusQuery.select("SUM(inventory_total)", "inventoryTotal")
		.select("product_dimension.product_code")
		.select("facility_dimension.facility_id", facilityId != null)
		.select("date_dimension.".concat(dateType))
		.from(tmpQuery, "tmp")
		.join(Join.INNER_JOIN, "date_dimension", "tmp.date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "tmp.product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "facility_dimension", "tmp.facility_dim_id = facility_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
		if (facilityId != null){
			condition.and(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null));
		}
		if(productId != null){
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId!=null));
		}
		olbiusQuery.where(condition)
		.groupBy("product_dimension.product_code")
		.groupBy("facility_dimension.facility_id", facilityId != null)
		.groupBy("date_dimension.".concat(dateType));
		if(filterTypeId.equals("FILTER_MIN")){ 
			olbiusQuery.orderBy("inventoryTotal", OlbiusQuery.DESC, true);
		}
		if(filterTypeId.equals("FILTER_MAX")){
			olbiusQuery.orderBy("inventoryTotal", OlbiusQuery.ASC, true);
		}
		if(limitId != null){
			int	limitIdInt = Integer.parseInt(limitId);
			olbiusQuery.limit(limitIdInt);
		}
			
		ResultSet resultSet = olbiusQuery.getResultSet();
		
		while(resultSet.next()) {
			String _key = resultSet.getString("product_code");
			if(yAxis.get("test") == null) {
				yAxis.put("test", new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity = resultSet.getBigDecimal("inventoryTotal");
			int quantityInt = quantity.intValue();
			if(quantityInt < 0){
				quantityInt = quantityInt * (-1);
			}
			yAxis.get("test").add(quantityInt);
		}*/
		
		
		
		OlbiusQuery olbiusQuery = OlbiusQuery.make(getSQLProcessor());	
		olbiusQuery.select("SUM(available_to_promise_total)", "inventoryTotal")
		.select("facility_dimension.facility_id", facilityId != null)
		.select("product_dimension.product_code")
		.select("date_dimension.".concat(dateType))
		.from("inventory_item_fact")
		.join(Join.INNER_JOIN, "date_dimension", "inventory_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "facility_dimension", "facility_dim_id = facility_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "facility_dim_id = facility_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
		condition.and(Condition.makeEQ("inventory_type", inventoryType));
		if (facilityId != null){
			condition.and(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null));
		}
		if(productId != null){
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId!=null));
		}
		olbiusQuery.where(condition)
		.groupBy("product_dimension.product_code")
		.groupBy("facility_dimension.facility_id", facilityId != null)
		.groupBy("date_dimension.".concat(dateType));
		if(filterTypeId.equals("FILTER_MIN")){ 
			olbiusQuery.orderBy("inventoryTotal", OlbiusQuery.DESC, true);
		}
		if(filterTypeId.equals("FILTER_MAX")){
			olbiusQuery.orderBy("inventoryTotal", OlbiusQuery.ASC, true);
		}
		if(limitId != null){
			int	limitIdInt = Integer.parseInt(limitId);
			olbiusQuery.limit(limitIdInt);
		}
		ResultSet resultSet = olbiusQuery.getResultSet();
		
		while(resultSet.next()) {
			String _key = resultSet.getString("product_code");
			if(yAxis.get("test") == null) {
				yAxis.put("test", new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity = resultSet.getBigDecimal("inventoryTotal");
			int quantityInt = quantity.intValue();
			if(quantityInt < 0){
				quantityInt = quantityInt * (-1);
			}
			yAxis.get("test").add(quantityInt);
		}
		
		
		/*Query query = new Query(getSQLProcessor());
		
		query.setFrom("inventory_item_fact");
		
		query.addSelect("sum(" + col + ")", "total");
		
		query.addSelect("facility_id", null, facilityId != null && !facilityId.isEmpty());
		
		query.addSelect("facility_id", null, dateType == null && (productId != null && !productId.isEmpty()) && (geoType == null || geoType.isEmpty()));
		
		query.addSelect("product_id");
		
		query.addSelect("geo_id", null, geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty());
		
		query.addSelect("geo_id", null, dateType == null && (productId != null && !productId.isEmpty()) && (geoType != null && !geoType.isEmpty()));
		
		query.addSelect(dateType, null, dateType != null);
		
		query.addInnerJoin("date_dimension", null, "inventory_date_dim_id = date_dimension.dimension_id");
		
		query.addInnerJoin("product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		
		query.addInnerJoin("facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id", facilityId != null && !facilityId.isEmpty());
		
		query.addInnerJoin("facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id", dateType == null && (productId != null && !productId.isEmpty()) && (geoType == null || geoType.isEmpty()));
		
		query.addInnerJoin("facility_geo", null, "facility_dim_id = facility_geo.dimension_id", geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty());
		
		query.addInnerJoin("facility_geo", null, "facility_dim_id = facility_geo.dimension_id", dateType == null && (productId != null && !productId.isEmpty()) && (geoType != null && !geoType.isEmpty()));
		
		query.addConditionEQ("inventory_type", inventoryType);
		
		query.addConditionEQ("facility_dimension.facility_id", facilityId, facilityId != null && !facilityId.isEmpty());
		
		query.addConditionEQ("product_dimension.product_id", productId, productId != null && !productId.isEmpty());
		
		query.addConditionEQ("facility_geo.geo_type", geoType, geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty());
		
		query.addConditionEQ("facility_geo.geo_type", geoType, dateType == null && (productId != null && !productId.isEmpty()) && (geoType != null && !geoType.isEmpty()));
		
		query.addConditionEQ("facility_geo.geo_id", geoId, geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty());
		
		query.addConditionBetweenObj("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		
		query.addGroupBy("product_id");
		
		query.addGroupBy(dateType, dateType != null);
		
		query.addGroupBy("facility_id", facilityId != null && !facilityId.isEmpty());
		
		query.addGroupBy("facility_id", dateType == null && (productId != null && !productId.isEmpty()) && (geoType == null || geoType.isEmpty()));
		
		query.addGroupBy("geo_id", geoId != null && !geoId.isEmpty() && geoType != null && !geoType.isEmpty());
		
		query.addGroupBy("geo_id", dateType == null && (productId != null && !productId.isEmpty()) && (geoType != null && !geoType.isEmpty()));
		
		query.addOrderBy("product_id");
		
		query.addOrderBy(dateType, dateType != null);
		
		ResultSet resultSet = query.getResultSet();
		
		if(dateType != null) {
			Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>();
			
			while(resultSet.next()) {
				String product = resultSet.getString("product_id");
				if(map.get(product)==null) {
					map.put(product, new HashMap<String, Object>());
				}
				if(inventoryType.equals(TYPE_RECEIVE)) {
					map.get(product).put(resultSet.getString(dateType), resultSet.getInt("total"));
				}
				if(inventoryType.equals(TYPE_EXPORT)) {
					map.get(product).put(resultSet.getString(dateType), -resultSet.getInt("total"));
				}
			}
			
			axis(map, dateType);
			
		} else {
			
			String key = null;
			
			if(productId == null || productId.isEmpty()) {
				key = "product_id";
			} else if(geoType == null || geoType.isEmpty()) {
				key = "facility_id";
			} else if(geoType != null && !geoType.isEmpty()) {
				key = "geo_id";
			}
			
			while(resultSet.next()) {
				String _key = resultSet.getString(key);
				Object total = null;
				if(inventoryType.equals(TYPE_RECEIVE)) {
					total = resultSet.getInt("total");
				}
				if(inventoryType.equals(TYPE_EXPORT)) {
					total = -resultSet.getInt("total");
				}
				xAxis.add(_key);
				List<Object> tmp = new ArrayList<Object>();
				tmp.add(total);
				yAxis.put(_key, tmp);
			}
		}*/
	}
	
	public void returnProductChart(List<Object> productId, List<Object> facilityId, String ownerPartyId, List<Object> categoryId, List<Object> enumId, List<Object> returnReasonId, String limitId, String filterTypeId) throws GenericDataSourceException, GenericEntityException, SQLException {
		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		/*GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");*/
		Condition condition = new Condition();
		query.from("return_item_fact")
		.select("product_dimension.product_code") 
		.select("return_reason_dimension.return_reason_id") 
		.select("enumeration_dimension.enum_id", enumId != null)
		.select("category_dimension.category_id", categoryId != null)
		.select("facility_dimension.facility_id")
		.select("sum(return_item_fact.return_quantity)", "quantity");
		query.join(Join.INNER_JOIN, "date_dimension", null, "status_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "facility_dimension", null, "facility_dim_id = facility_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "return_reason_dimension", null, "return_reason_dim_id = return_reason_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "enumeration_dimension", null, "product_store_dim_id = enumeration_dimension.dimension_id", enumId != null);
		query.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id", categoryId != null);
		query.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'", categoryId != null);
		query.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
		query.join(Join.INNER_JOIN, "party_dimension", "party_organization", "return_item_fact.to_party_dim_id = party_organization.dimension_id AND party_organization.party_id = "+"'"+ownerPartyId+"'");
		/*condition.and(Condition.makeEQ("party_dimension.party_id", ownerPartyId));
		condition.and(Condition.makeEQ("facility_party.party_id", ownerPartyId));*/
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if(returnReasonId != null){
			condition.and(Condition.makeIn("return_reason_dimension.return_reason_id", returnReasonId, returnReasonId != null));
		}
		if (facilityId != null){
			condition.and(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null));
		}
		if(productId != null){
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId!=null));
		}
		if (enumId != null){
			condition.and(Condition.makeIn("enumeration_dimension.enum_id", enumId, enumId != null));
		}
		if (categoryId != null){
			condition.and(Condition.makeIn("category_dimension.category_id", categoryId, categoryId != null));
		}
		condition.and(Condition.makeEQ("return_item_fact.return_header_type_id", "CUSTOMER_RETURN"));
		query.where(condition);
		query.groupBy("product_dimension.product_code")
		.groupBy("facility_dimension.facility_id")
		.groupBy("enumeration_dimension.enum_id", enumId != null)
		.groupBy("category_dimension.category_id", categoryId != null)
		.groupBy("return_reason_dimension.return_reason_id");
		if(filterTypeId.equals("FILTER_MIN")){ 
			query.orderBy("quantity", OlbiusQuery.ASC, true);
		}
		if(filterTypeId.equals("FILTER_MAX")){
			query.orderBy("quantity", OlbiusQuery.DESC, true);
		}
		if(limitId != null){
			int	limitIdInt = Integer.parseInt(limitId);
			query.limit(limitIdInt);
		}
		query.orderBy("facility_dimension.facility_id");
		ResultSet resultSet = query.getResultSet();
		
		while(resultSet.next()) {
			String _key = resultSet.getString("product_code");
			/*GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", _key), false);
			String internalName = product.getString("internalName");*/
			if(yAxis.get("test") == null) {
				yAxis.put("test", new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity = resultSet.getBigDecimal("quantity");
			yAxis.get("test").add(quantity);
		}
		
	} 
	
	public void facilityReportPieByFacility(List<Object> facilityId, String ownerPartyId, String olapType) throws GenericDataSourceException, GenericEntityException, SQLException {
		String dateType = "DAY";
		if(dateType != null) {
			dateType = getDateType(dateType);
		}
			
		OlbiusQuery query = OlbiusQuery.make(getSQLProcessor());	
		Condition condition = new Condition();
		
		query = new OlbiusQuery(getSQLProcessor());
		query.from("inventory_item_fact")
		.select("sum(inventory_item_fact.quantity_on_hand_total)", "inventoryTotal")
		.select("facility_dimension.facility_name");
		query.join(Join.INNER_JOIN, "date_dimension", null, "inventory_item_fact.inventory_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "party_dimension", "inventory_item_fact.owner_party_dim_id = party_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "facility_dimension", null, "inventory_item_fact.facility_dim_id = facility_dimension.dimension_id");
		condition.and(Condition.makeEQ("party_dimension.party_id", ownerPartyId));
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
		query.where(condition);
		query.groupBy("facility_dimension.facility_name");
			
		ResultSet resultSet = query.getResultSet();
		
		/*while(resultSet.next()) {
			String _key = resultSet.getString("facility_name");
			if(yAxis.get("test") == null) {
				yAxis.put(_key, new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity = resultSet.getBigDecimal("inventoryTotal");
			yAxis.get("test").add(quantity);
		}*/
		
		while(resultSet.next()) {
			String _key = resultSet.getString("facility_name");
			Object total = resultSet.getInt("total");
			xAxis.add(_key);
			List<Object> tmp = new ArrayList<Object>();
			tmp.add(total);
			yAxis.put(_key, tmp);
		}
	}
	
	public void inventoryNotityChartOlap(List<Object> productId, List<Object> facilityId, String ownerPartyId) throws GenericDataSourceException, GenericEntityException, SQLException {
		/*SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");*/
		String dateType = "DAY";
		if(dateType != null) {
			dateType = getDateType(dateType);
		}
		/*Date dateCurrent = new Date();
		String dateCurrentStr = formatDate.format(new Timestamp(dateCurrent.getTime()));*/
		OlbiusQuery olbiusQuery = OlbiusQuery.make(getSQLProcessor());	
		OlbiusQuery tmpQuery = OlbiusQuery.make();
		Condition condition = new Condition();
		
		tmpQuery.distinctOn("facility_dim_id", "product_dim_id", "date_dim_id")
				.distinctOn(dateType, dateType != null)
				.distinctOn("date_value", dateType == null)
				.select("*").from("facility_fact")
				.join(Join.INNER_JOIN, "date_dimension", "date_dim_id = date_dimension.dimension_id")
				.where(Condition.makeBetween("date_value", getSqlDate(fromDate), getSqlDate(thruDate)))
				.orderBy("date_value", OlbiusQuery.DESC, dateType == null)
				.orderBy(dateType, OlbiusQuery.DESC, dateType != null);
		
		olbiusQuery = OlbiusQuery.make(getSQLProcessor());
		
		
		olbiusQuery.select("SUM(inventory_total)", "inventoryTotal")
		.select("product_dimension.product_code")
		.select("facility_dimension.facility_id", facilityId != null)
		.select("date_dimension.".concat(dateType))
		.from(tmpQuery, "tmp")
		.join(Join.INNER_JOIN, "date_dimension", "tmp.date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "tmp.product_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "facility_dimension", "tmp.facility_dim_id = facility_dimension.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "expire_date_dimension", "tmp.expire_date_dim_id = expire_date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "facility_party_id", "facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "+"'"+ownerPartyId+"'");
		if (facilityId != null){
			condition.and(Condition.makeIn("facility_dimension.facility_id", facilityId, facilityId != null));
		}
		if(productId != null){
			condition.and(Condition.makeIn("product_dimension.product_code", productId, productId!=null));
		}
		condition.and(Condition.make("product_dayn IS NOT NULL"));
		condition.and(Condition.make("DATE_PART('day', age(expire_date_dimension.date_value, CURRENT_DATE)) <= 20"));
		condition.and(Condition.make("DATE_PART('day', age(expire_date_dimension.date_value, CURRENT_DATE)) > 0"));
		olbiusQuery.where(condition)
		.groupBy("product_dimension.product_code")
		.groupBy("facility_dimension.facility_id", facilityId != null)
		.groupBy("date_dimension.".concat(dateType))
		.orderBy("inventoryTotal", OlbiusQuery.DESC, true);
		ResultSet resultSet = olbiusQuery.getResultSet();
		
		while(resultSet.next()) {
			String _key = resultSet.getString("product_code");
			if(yAxis.get("test") == null) {
				yAxis.put("test", new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity = resultSet.getBigDecimal("inventoryTotal");
			int quantityInt = quantity.intValue();
			if(quantityInt < 0){
				quantityInt = quantityInt * (-1);
			}
			yAxis.get("test").add(quantityInt);
		}
		
	} 
}
