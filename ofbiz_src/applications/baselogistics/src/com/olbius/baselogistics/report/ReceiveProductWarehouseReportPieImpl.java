package com.olbius.baselogistics.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ReceiveProductWarehouseReportPieImpl extends AbstractOlap{
	private OlbiusQuery query;
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	public static final String LOCALE = "LOCALE";
	public static final String FILTER_TYPE_ID = "FILTER_TYPE_ID";
	private void initQuery(){
		String partyFacilityId = (String) getParameter(USER_LOGIN_ID);
		String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
		Condition condition = new Condition();
		if(filterTypeId.equals("RECEIVE")){    
			query = new OlbiusQuery(getSQLProcessor());
			query.from("inventory_item_fact")
			.select("sum(inventory_item_fact.quantity_on_hand_total)", "totalQuantity")
			.select("product_dimension.internal_name");
			query.join(Join.INNER_JOIN, "date_dimension", null, "inventory_item_fact.inventory_date_dim_id = date_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "party_dimension", "party_dimension", "inventory_item_fact.owner_party_dim_id = party_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "product_dimension", null, "inventory_item_fact.product_dim_id = product_dimension.dimension_id");
			condition.and(Condition.makeEQ("party_dimension.party_id", partyFacilityId));
			condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "RECEIVE"));
			query.where(condition);
			query.groupBy("product_dimension.internal_name");
		}
		if(filterTypeId.equals("EXPORT")){  
			query = new OlbiusQuery(getSQLProcessor());
			query.from("inventory_item_fact")
			.select("sum(inventory_item_fact.quantity_on_hand_total)", "totalQuantity")
			.select("product_dimension.internal_name");
			query.join(Join.INNER_JOIN, "date_dimension", null, "inventory_item_fact.inventory_date_dim_id = date_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "party_dimension", "party_dimension", "inventory_item_fact.owner_party_dim_id = party_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "product_dimension", null, "inventory_item_fact.product_dim_id = product_dimension.dimension_id");
			condition.and(Condition.makeEQ("party_dimension.party_id", partyFacilityId));
			condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			condition.and(Condition.makeEQ("inventory_item_fact.inventory_type", "EXPORT"));
			query.where(condition);
			query.groupBy("product_dimension.internal_name");
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
			Map<String, Object> map = new HashMap<String, Object>();
			String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
			try {
				ResultSet resultSet = query.getResultSet();
				if(filterTypeId.equals("RECEIVE")){
					while(resultSet.next()) {
						String productName = resultSet.getString("internal_name");
						BigDecimal totalQuantity = resultSet.getBigDecimal("totalQuantity");
						map.put(productName, totalQuantity);
					}
				}
				if(filterTypeId.equals("EXPORT")){
					while(resultSet.next()) {
						String productName = resultSet.getString("internal_name");
						BigDecimal totalQuantity = resultSet.getBigDecimal("totalQuantity");
						int totalQuantityInt = totalQuantity.intValue();
						if(totalQuantityInt < 0){
							totalQuantityInt = totalQuantityInt * (-1);
						}
						map.put(productName, totalQuantityInt);
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
