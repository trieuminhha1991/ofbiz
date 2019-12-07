package com.olbius.olap.bi.sales;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalesTotalOlap extends AbstractSalesOlap implements SalesOlap {

	private OlbiusQuery query;

	private String dateType;
	
	private String show;
	
	public SalesTotalOlap() {
		setModule(SalesTotalOlap.class.getName());
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

		query = new OlbiusQuery(getSQLProcessor());

		dateType = (String) getParameter(DATE_TYPE);

		dateType = getDateType(dateType);

		if (dateType != null) {
			dateType = getDateType(dateType);
		}

		show = (String) getParameter(SHOW);

		String currency = (String) getParameter(CURRENCY);
		
		String organization = (String) getParameter(ORGAN);
		
		boolean taxFlag = getParameter(TAX_FLAG) != null ? (Boolean) getParameter(TAX_FLAG) : false;

		boolean quantity = getParameter(OLAP_QUANTITY) != null ? (Boolean) getParameter(OLAP_QUANTITY) : false;
		
		boolean promo = getParameter(PROMO) != null ? (Boolean) getParameter(PROMO) : false;
		
		Map<String, Object> type = (Map<String, Object>) getParameter(TYPE);

		query.from("sales_order_fact").select("sum(total)", "total", !taxFlag && !quantity).select("sum(total+tax)", "total", taxFlag  && !quantity)
				.select("date_dimension.".concat(dateType), null, dateType != null)
				.select(_PRODUCT, quantity).select("sum(quantity)", "total", quantity);

		for (String s : type.keySet()) {
			String id = getStatistic(s);
			if(s.equals("PARTY_TO")) id += " as party_id_to";
			else if(s.equals("PARTY_FROM")) id += " as party_id_from";
			query.select(id);
		}

		query.join(Join.INNER_JOIN, "date_dimension", "sales_order_fact.order_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_store_dimension", "product_store_dimension.dimension_id = sales_order_fact.product_store_dim_id", type.containsKey(TYPE_PRODUCT_STORE));
		query.join(Join.INNER_JOIN, "party_dimension", "party_group_from_dimension", "party_group_from_dimension.dimension_id = sales_order_fact.party_from_dim_id AND party_group_from_dimension.party_type_id not like \'PERSON\'", type.containsKey(TYPE_PARTY_FROM));
		query.join(Join.INNER_JOIN, "party_dimension", "party_group_to_dimension",
				"party_group_to_dimension.dimension_id = sales_order_fact.party_to_dim_id", type.containsKey(TYPE_PARTY_TO));
		query.join(Join.INNER_JOIN, "currency_dimension", "currency_dimension.dimension_id = sales_order_fact.currency_dim_id");
		query.join(Join.INNER_JOIN, "product_dimension", "product_dimension.dimension_id = sales_order_fact.product_dim_id",
				type.containsKey(TYPE_PRODUCT) || quantity);
		query.join(Join.INNER_JOIN, "enumeration_dimension", "sales_channel_enum", "sales_channel_enum.dimension_id = sales_order_fact.sales_channel_enum_dim_id",
				type.containsKey(TYPE_CHANNEL));
		query.join(Join.INNER_JOIN, "enumeration_dimension", "sales_method_channel_enum", "sales_method_channel_enum.dimension_id = sales_order_fact.sales_method_channel_enum_dim_id",
				type.containsKey(TYPE_METHOD_CHANNEL));
		query.join(Join.INNER_JOIN, "product_promo_dimension", null, "product_promo_dimension.dimension_id = sales_order_fact.promo_dim_id", !promo && quantity);
		query.join(Join.INNER_JOIN, "party_dimension", "organ", "sales_order_fact.party_from_dim_id = organ.dimension_id");

		Condition condition = Condition.makeEQ("currency_dimension.currency_id", currency, currency != null);
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.and("product_promo_dimension.product_promo_id IS NULL", !promo && quantity);
		condition.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'");
		condition.andEQ("sales_order_fact.order_status", "ORDER_COMPLETED");
		condition.andEQ("organ.party_id", organization, organization != null);  
		
		for (String s : type.keySet()) {
			if (type.get(s) != null) {
				String id = getStatistic(s);
				if(type.get(s) instanceof List) {
					condition.andIn(id, (List<Object>) type.get(s));
				} else {
					condition.andEQ(id, type.get(s));
				}
			}
		}

		query.where(condition);

		query.groupBy("date_dimension.".concat(dateType), dateType != null);
		query.groupBy(_PRODUCT, quantity);

		for (String s : type.keySet()) {
			String id = getStatistic(s);
			query.groupBy(id);
		}

		query.orderBy("date_dimension.".concat(dateType), dateType != null);
		query.orderBy("total");
	}

	public class SalesAmountTotalChartResult implements OlapResultQueryInterface {

		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
			try {
				ResultSet resultSet = query.getResultSet();
				if(dateType != null) {
					while (resultSet.next()) {

						String key = resultSet.getString(getStatistic(show).split("\\.")[1]);

						if (map.get(key) == null) {
							map.put(key, new HashMap<String, Object>());
						}

						map.get(key).put(resultSet.getString(dateType), resultSet.getBigDecimal("total"));
					}
				} else {
					String key = getStatistic(show).split("\\.")[1];
					while(resultSet.next()) {
						String _key = resultSet.getString(key);
						
						if (map.get("column") == null) {
							map.put("column", new HashMap<String, Object>());
						}
						
						map.get("column").put(_key, resultSet.getBigDecimal("total"));
						
					}
				}
				
			} catch(Exception e) {
				Debug.logError(e, getModule());
			}
			return map;
		}

	}

	@Override
	public void prepareResult() {
		getOlapResult().putParameter(DATE_TYPE, dateType);
	}

}
