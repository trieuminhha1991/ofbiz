package com.olbius.olap.bi.sales;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalesOrderCountOlap extends AbstractSalesOlap{

	private OlbiusQuery query;
	
	private String dateType;
	
	public SalesOrderCountOlap() {
		setModule(SalesOrderCountOlap.class.getName());
	}	
	
	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	private void initQuery() {
		
		Map<String, String> type = (Map<String, String>) getParameter(TYPE);
		
		if(type.get(TYPE_PARTY_FROM) == null) {
			return;
		}
		
		query = new OlbiusQuery(getSQLProcessor());

		dateType = (String) getParameter(DATE_TYPE);
		
		String organization = (String) getParameter(ORGAN);

		dateType = getDateType(dateType);

		if (dateType != null) {
			dateType = getDateType(dateType);
		}

		query.from("sales_order_fact").select("COUNT(DISTINCT order_id)", "_count").select(_PARTY_FROM, "party_from")
				.select(_PARTY_TO, "party_to")
				.select("date_dimension.".concat(dateType));

		query.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "party_group_from_dimension", "party_group_from_dimension.dimension_id = sales_order_fact.party_from_dim_id");
		query.join(Join.INNER_JOIN, "party_dimension", "party_group_to_dimension", "party_group_to_dimension.dimension_id = sales_order_fact.party_to_dim_id");
		query.join(Join.INNER_JOIN, "product_store_dimension", "product_store_dimension.dimension_id = sales_order_fact.product_store_dim_id", type.get(TYPE_PRODUCT_STORE) != null);
		query.join(Join.INNER_JOIN, "party_dimension", "organ", "sales_order_fact.party_from_dim_id = organ.dimension_id");
		
		Condition condition = Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		
		condition.andEQ(_PARTY_FROM, type.get(TYPE_PARTY_FROM));
		condition.andEQ(_PARTY_TO, type.get(TYPE_PARTY_TO), type.get(TYPE_PARTY_TO) != null);
		if("PRODUCT_STORE".equals(TYPE_PRODUCT_STORE)){
			condition.andEQ(_PRODUCT_STORE, type.get(TYPE_PRODUCT_STORE), type.get(TYPE_PRODUCT_STORE) != null);
		}
		condition.andEQ(_CHANNEL, type.get(TYPE_CHANNEL), type.get(TYPE_CHANNEL) != null);
		condition.andEQ(_METHOD_CHANNEL, type.get(TYPE_METHOD_CHANNEL), type.get(TYPE_METHOD_CHANNEL) != null);
		condition.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'");
		condition.andEQ("organ.party_id", organization, organization != null);
		
		query.where(condition);

		query.groupBy("date_dimension.".concat(dateType));
		query.groupBy(_PARTY_FROM);
		query.groupBy(_PARTY_TO);

		query.orderBy("date_dimension.".concat(dateType));
		query.orderBy("_count");
	}

	@Override
	public void prepareResult() {
		getOlapResult().putParameter(DATE_TYPE, dateType);
	}
	
	public class SalesOrderCountChartResult implements OlapResultQueryInterface{

		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
			try {
				
				ResultSet resultSet = query.getResultSet();
				
				while(resultSet.next()) {
					
					String key = resultSet.getString("party_to");
					
					if(map.get(key)==null) {
						map.put(key, new HashMap<String, Object>());
					}
					
					map.get(key).put(resultSet.getString(dateType), resultSet.getBigDecimal("_count"));
				}
				
			} catch(Exception e) {
				Debug.logError(e, getModule());
			}
			return map;
		}
		
	}
	
}
