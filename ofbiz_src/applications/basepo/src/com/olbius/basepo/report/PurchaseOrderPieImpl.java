package com.olbius.basepo.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;

public class PurchaseOrderPieImpl extends AbstractOlap {
	private OlbiusQuery query;
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	public static final String LOCALE = "LOCALE";
	public static final String FILTER_TYPE_ID = "FILTER_TYPE_ID";
	public static final String CATEGORY_ID = "CATEGORY_ID";

	private void initQuery() {
		String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
		String ownerPartyId = (String) getParameter(USER_LOGIN_ID);
		Condition condition = new Condition();
		if (filterTypeId.equals("FILTER_CHANEL")) {
			query = new OlbiusQuery(getSQLProcessor());
			query.from("purchase_order_fact").select("sum(purchase_order_fact.quantity)", "totalQuantity")
					.select("enumeration_dimension.description");
			query.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "status_dimension", null, "status_dim_id = status_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "enumeration_dimension", null,
					"product_store_dim_id = enumeration_dimension.dimension_id");
			query.join(Join.INNER_JOIN, "party_dimension", "party_organization",
					"purchase_order_fact.party_to_dim_id = party_organization.dimension_id AND party_organization.party_id = "
							+ "'" + ownerPartyId + "'");
			condition.and(
					Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			condition.and(Condition.make("enumeration_dimension.enum_id is not null"));
			condition.and(Condition.make("status_dimension.status_id != 'ITEM_CANCELLED'"));
			query.where(condition);
			query.groupBy("enumeration_dimension.description");
		}
		if (filterTypeId.equals("FILTER_CATALOG")) {
			query = new OlbiusQuery(getSQLProcessor());
			query.select(new Sum("CASE WHEN prd.require_amount = 'Y' THEN iif.amount_on_hand_total ELSE iif.quantity_on_hand_total END"), "totalQuantity")
				.select("cat.category_name")
				.from("inventory_item_fact", "iif")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "iif.inventory_date_dim_id = dd.dimension_id")
				.join(Join.INNER_JOIN, "product_dimension", "prd", "iif.product_dim_id = prd.dimension_id")
				.join(Join.INNER_JOIN, "category_dimension", "cat", "iif.root_category_dim_id = cat.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "pty", "pty.dimension_id = iif.owner_party_dim_id")
				.where(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate))
						.andEQ("prd.product_type", "FINISHED_GOOD")
						.andEQ("iif.inventory_type", "RECEIVE")
						.andEQ("iif.inventory_change_type", "PURCHASE_ORDER")
						.andEQ("pty.party_id", ownerPartyId));
			query.groupBy("cat.dimension_id");
		}
	}

	@Override
	protected OlbiusQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	}

	public class ResultOutReport implements OlapResultQueryInterface {
		@Override
		public Object resultQuery(OlapQuery query) {
			String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				ResultSet resultSet = query.getResultSet();
				if (filterTypeId.equals("FILTER_CHANEL")) {
					while (resultSet.next()) {
						String productStoreId = resultSet.getString("description");
						map.put(productStoreId, resultSet.getBigDecimal("totalQuantity"));
					}
				}
				if (filterTypeId.equals("FILTER_CATALOG")) {
					while (resultSet.next()) {
						String categoryName = resultSet.getString("category_name");
						map.put(categoryName, resultSet.getBigDecimal("totalQuantity"));
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