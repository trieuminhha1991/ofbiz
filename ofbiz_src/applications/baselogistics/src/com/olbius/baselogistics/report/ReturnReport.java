package com.olbius.baselogistics.report;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.CategoryDimension;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.entity.cache.OlbiusCache;

public class ReturnReport extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {

		setFromDate((Date) context.get("fromDate"));

		setThruDate((Date) context.get("thruDate"));

		putParameter("dateType", context.get("dateType"));

		putParameter("returnReasonId", context.get("returnReasonId[]"));

		putParameter("facility", context.get("facility[]"));

		putParameter("product", context.get("product[]"));

		putParameter("categories", context.get("categories[]"));

		putParameter("channels", context.get("channels[]"));

		putParameter("locale", context.get("locale"));
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private static final OlbiusCache<Long> ENUMERATIONS = new OlbiusCache<Long>() {
		@Override
		public Long loadCache(Delegator delegator, String key) throws Exception {
			GenericValue value = EntityUtil
					.getFirst(delegator.findByAnd("EnumerationDimension", UtilMisc.toMap("enum_id", key), null, false));
			return value != null ? value.getLong("dimensionId") : -1;
		}

	};

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

	private List<Object> getCategories(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = CategoryDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}

	private List<Object> getProducts(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = ProductDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}

	private List<Object> getEnumerations(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = ENUMERATIONS.get(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}

	@SuppressWarnings("unchecked")
	private OlbiusQuery init() {
		OlbiusQuery query = makeQuery();
		List<Object> facilities = getFacilities((List<?>) getParameter("facility"));
		List<Object> products = getProducts((List<?>) getParameter("product"));
		List<Object> categories = getCategories((List<?>) getParameter("categories"));
		List<Object> channels = getEnumerations((List<?>) getParameter("channels"));
		String dateType = getDateType((String) getParameter("dateType"));
		List<Object> returnReasonId = (List<Object>) getParameter("returnReasonId");

		Condition condition = new Condition();
		condition
				.and(Condition.makeBetween("status_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate())));
		condition.and(Condition.makeIn("rrd.return_reason_id", returnReasonId));
		condition.and(Condition.makeIn("rif.facility_dim_id", facilities));
		condition.and(Condition.makeIn("rif.product_dim_id", products));
		condition.and(Condition.makeIn("pcr.category_dim_id", categories));
		condition.and(Condition.makeIn("pcr.product_store_dim_id", channels));

		condition.and(Condition.makeEQ("rif.return_header_type_id", "CUSTOMER_RETURN"));

		query.from("return_item_fact", "rif").select("dd.".concat(dateType)).select("pd.product_code")
				.select("pd.internal_name").select("rif.return_quantity").select("rif.order_id")
				.select("sd.description", "statusId").select("rrd.return_reason_id")
				.select("party_from_dimension.name", "party_from_name")
				.select("party_organization.name", "party_to_name").select("rif.return_item_type_id")
				.select("cd2.description", "quantity_uom_id").select("fd.facility_name")
				.select("sd2.description", "status").select("ed.description", "product_store")
				.select("array_to_string(array_agg(cd.category_name), ',')", "category_name")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "status_date_dim_id = dd.dimension_id")
				.join(Join.INNER_JOIN, "product_dimension", "pd", "product_dim_id = pd.dimension_id")
				.join(Join.INNER_JOIN, "enumeration_dimension", "ed", "product_store_dim_id = ed.dimension_id")
				.join(Join.INNER_JOIN, "status_dimension", "sd", "status_dim_id = sd.dimension_id")
				.join(Join.INNER_JOIN, "return_reason_dimension", "rrd", "return_reason_dim_id = rrd.dimension_id")
				.join(Join.INNER_JOIN, "facility_dimension", "fd", "facility_dim_id = fd.dimension_id")
				.join(Join.LEFT_OUTER_JOIN, "currency_dimension", "cd2", "cd2.currency_id = rif.quantity_uom_id")
				.join(Join.LEFT_OUTER_JOIN, "status_dimension", "sd2", "sd2.status_id = rif.status_header_id")
				.join(Join.LEFT_OUTER_JOIN, "product_category_relationship", "pcr",
						"pcr.product_dim_id = pd.dimension_id")
				.join(Join.LEFT_OUTER_JOIN, "category_dimension", "cd",
						"pcr.category_dim_id = cd.dimension_id AND cd.category_type = 'CATALOG_CATEGORY'")
				.join(Join.INNER_JOIN, "party_dimension", "party_organization",
						"rif.to_party_dim_id = party_organization.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "party_from_dimension",
						"rif.from_party_dim_id = party_from_dimension.dimension_id");
		query.where(condition).groupBy("dd.".concat(dateType)).groupBy("pd.product_code").groupBy("pd.internal_name")
				.groupBy("rif.return_quantity").groupBy("rif.order_id").groupBy("statusId")
				.groupBy("rrd.return_reason_id").groupBy("party_from_name").groupBy("party_to_name")
				.groupBy("rif.return_item_type_id").groupBy("cd2.description").groupBy("fd.facility_name")
				.groupBy("status").groupBy("product_store").orderBy("dd.".concat(dateType), OlbiusQuery.DESC)
				.orderBy("product_store");

		return query;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("category_name", "category_name");
		addDataField("product_code", "product_code");
		addDataField("internal_name", "internal_name");
		addDataField("order_id", "order_id");
		addDataField("return_quantity", "return_quantity");
		addDataField("quantity_uom_id", "quantity_uom_id");
		addDataField("return_reason_id", "return_reason_id");
		addDataField("party_from_name", "party_from_name");
		addDataField("party_to_name", "party_to_name");
		addDataField("facility_name", "facility_name");
		addDataField("return_item_type_id", "return_item_type_id");
		addDataField("product_store", "product_store");
		addDataField("status", "status");
	}

	@Override
	protected OlapResultQueryInterface returnResultGrid() {
		return new ReturnResultGridFacility();
	}

	private class ReturnResultGridFacility extends ReturnResultGridEx {
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = super.getObject(result);
			try {
				map.put("product_code", result.getString("product_code"));
				map.put("internal_name", result.getString("internal_name"));
				map.put("category_name", result.getString("category_name"));
				map.put("order_id", result.getString("order_id"));
				map.put("return_quantity", result.getBigDecimal("return_quantity"));
				map.put("quantity_uom_id", result.getString("quantity_uom_id"));
				map.put("return_reason_id", result.getString("return_reason_id"));
				map.put("party_from_name", result.getString("party_from_name"));
				map.put("party_to_name", result.getString("party_to_name"));
				map.put("return_item_type_id", result.getString("return_item_type_id"));
				map.put("facility_name", result.getString("facility_name"));
				map.put("status", result.getString("status"));
				map.put("product_store", result.getString("product_store"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}
	}

}
