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
import com.olbius.bi.olap.query.join.Join;

public class ReturnProductPOPieImpl extends AbstractOlap {
	private OlbiusQuery query;
	public static final String USER_LOGIN_ID = "USER_LOGIN_ID";
	public static final String LOCALE = "LOCALE";
	public static final String FILTER_TYPE_ID = "FILTER_TYPE_ID";
	public static final String CATEGORY_ID = "CATEGORY_ID";

	private void initQuery() {
		String partyFacilityId = (String) getParameter(USER_LOGIN_ID);
		String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
		String categoryId = (String) getParameter(CATEGORY_ID);
		Condition condition = new Condition();
		if (filterTypeId.equals("FILTER_CATALOG")) {
			if (UtilValidate.isNotEmpty(categoryId)) {
				query = new OlbiusQuery(getSQLProcessor());
				query.from("return_item_fact").select("sum(return_item_fact.return_quantity)", "totalQuantity")
						.select("product_dimension.internal_name");
				query.join(Join.INNER_JOIN, "date_dimension", null, "status_date_dim_id = date_dimension.dimension_id");
				query.join(Join.INNER_JOIN, "product_dimension", null,
						"product_dim_id = product_dimension.dimension_id");
				query.join(Join.INNER_JOIN, "facility_dimension", null,
						"facility_dim_id = facility_dimension.dimension_id");
				query.join(Join.INNER_JOIN, "product_category_relationship", null,
						"product_category_relationship.product_dim_id = product_dimension.dimension_id");
				query.join(Join.INNER_JOIN, "category_dimension", null,
						"product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'");
				query.join(Join.LEFT_OUTER_JOIN, "party_dimension", "facility_party_id",
						"facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "
								+ "'" + partyFacilityId + "'");
				condition.and(
						Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
				condition.and(Condition.makeEQ("return_item_fact.return_header_type_id", "VENDOR_RETURN"));
				if (categoryId != null) {
					condition.and(Condition.makeEQ("category_dimension.category_id", categoryId, categoryId != null));
				}
				query.where(condition);
				query.groupBy("product_dimension.internal_name");
			} else {
				query = new OlbiusQuery(getSQLProcessor());
				query.from("return_item_fact").select("sum(return_item_fact.return_quantity)", "totalQuantity")
						.select("category_dimension.category_name");
				query.join(Join.INNER_JOIN, "date_dimension", null, "status_date_dim_id = date_dimension.dimension_id");
				query.join(Join.INNER_JOIN, "product_dimension", null,
						"product_dim_id = product_dimension.dimension_id");
				query.join(Join.INNER_JOIN, "facility_dimension", null,
						"facility_dim_id = facility_dimension.dimension_id");
				query.join(Join.INNER_JOIN, "product_category_relationship", null,
						"product_category_relationship.product_dim_id = product_dimension.dimension_id");
				query.join(Join.INNER_JOIN, "category_dimension", null,
						"product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'");
				query.join(Join.LEFT_OUTER_JOIN, "party_dimension", "facility_party_id",
						"facility_dimension.owner_party_dim_id = facility_party_id.dimension_id AND facility_party_id.party_id = "
								+ "'" + partyFacilityId + "'");
				condition.and(
						Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
				condition.and(Condition.makeEQ("return_item_fact.return_header_type_id", "VENDOR_RETURN"));
				query.where(condition);
				query.groupBy("category_dimension.category_name");
			}
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
			/* String locale = (String) getParameter(LOCALE); */
			String filterTypeId = (String) getParameter(FILTER_TYPE_ID);
			String categoryId = (String) getParameter(CATEGORY_ID);
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				ResultSet resultSet = query.getResultSet();
				if (filterTypeId.equals("FILTER_CATALOG")) {
					if (UtilValidate.isNotEmpty(categoryId)) {
						while (resultSet.next()) {
							String internalName = resultSet.getString("internal_name");
							map.put(internalName, resultSet.getBigDecimal("totalQuantity"));
						}
					} else {
						while (resultSet.next()) {
							String categoryName = resultSet.getString("category_name");
							map.put(categoryName, resultSet.getBigDecimal("totalQuantity"));
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
