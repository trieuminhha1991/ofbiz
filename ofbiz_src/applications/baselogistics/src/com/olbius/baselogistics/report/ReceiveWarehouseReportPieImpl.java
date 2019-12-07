package com.olbius.baselogistics.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ReceiveWarehouseReportPieImpl extends AbstractOlap{
	private OlbiusQuery query;
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	public static final String LOCALE = "LOCALE";
	public static final String FILTER_TYPE_ID = "FILTER_TYPE_ID";
	private void initQuery(){
		String partyFacilityId = (String) getParameter(USER_LOGIN_ID);
		String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
		Condition condition = new Condition();
		if(filterTypeId.equals("FILTER_CHANEL")){
			query = new OlbiusQuery(getSQLProcessor());
			query.from("inventory_item_fact")
			.select("sum(inventory_item_fact.quantity_on_hand_total)", "totalQuantity")
			.select("enumeration_dimension.enum_id");
			query.join(Join.INNER_JOIN, "date_dimension", null, "inventory_item_fact.inventory_date_dim_id = date_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "party_dimension", "party_dimension", "inventory_item_fact.owner_party_dim_id = party_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "enumeration_dimension", null, "inventory_item_fact.enum_dim_id = enumeration_dimension.dimension_id");
			condition.and(Condition.makeEQ("party_dimension.party_id", partyFacilityId));
			condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
			condition.and(Condition.make("enumeration_dimension.enum_id is not null"));
			query.where(condition);
			query.groupBy("enumeration_dimension.enum_id");
		}
		if(filterTypeId.equals("FILTER_CATALOG")){
			query = new OlbiusQuery(getSQLProcessor());
			query.from("inventory_item_fact")
			.select("sum(inventory_item_fact.quantity_on_hand_total)", "totalQuantity")
			.select("category_dimension.category_id");
			query.join(Join.INNER_JOIN, "date_dimension", null, "inventory_item_fact.inventory_date_dim_id = date_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "product_dimension", null, "inventory_item_fact.product_dim_id = product_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "party_dimension", "party_dimension", "inventory_item_fact.owner_party_dim_id = party_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'");
			condition.and(Condition.makeEQ("party_dimension.party_id", partyFacilityId));
			condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
			query.where(condition);
			query.groupBy("category_dimension.category_id");
		}
	}
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}

	
	public class ResultOutReport implements OlapResultQueryInterface{
		@Override
		public Object resultQuery(OlapQuery query) {
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			/*String locale = (String) getParameter(LOCALE);*/
			String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				ResultSet resultSet = query.getResultSet();
				if(filterTypeId.equals("FILTER_CHANEL")){
					while(resultSet.next()) {
						String productStoreId = resultSet.getString("enum_id");
						GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", productStoreId), false);
						if(enumeration != null){
							productStoreId = enumeration.getString("description");
						}else{
							GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
							if(productStore != null){
								productStoreId = productStore.getString("storeName");
							}
						}
						BigDecimal totalQuantity = resultSet.getBigDecimal("totalQuantity");
						map.put(productStoreId, totalQuantity);
					}
				}
				if(filterTypeId.equals("FILTER_CATALOG")){
					while(resultSet.next()) {
						String categoryId = resultSet.getString("category_id");
						GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId), false);
						if(productCategory != null){
							categoryId = productCategory.getString("categoryName");
						}
						BigDecimal totalQuantity = resultSet.getBigDecimal("totalQuantity");
						map.put(categoryId, totalQuantity);
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