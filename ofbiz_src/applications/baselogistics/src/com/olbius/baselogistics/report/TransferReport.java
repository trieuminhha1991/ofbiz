package com.olbius.baselogistics.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
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

public class TransferReport extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));

		putParameter("dateType", context.get("dateType"));

		putParameter("origin_facility", context.get("origin_facility[]"));

		putParameter("dest_facility", context.get("dest_facility[]"));

		putParameter("product", context.get("product[]"));

		putParameter("status_transfer_id", context.get("status_transfer_id[]"));

		putParameter("categories", context.get("categories[]"));

	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private static final OlbiusCache<Long> STATUS_DIMENSION = new OlbiusCache<Long>() {

		@Override
		public Long loadCache(Delegator delegator, String key) throws Exception {
			GenericValue value = EntityUtil
					.getFirst(delegator.findByAnd("StatusDimension", UtilMisc.toMap("statusId", key), null, false));
			return value != null ? value.getLong("dimensionId") : -1;
		}

	};

	private List<Object> getStatusIds(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = STATUS_DIMENSION.get(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
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

	private OlbiusQuery init() {
		OlbiusQuery query = makeQuery();
		OlbiusQuery query2 = makeQuery();

		List<Object> origin_facilities = getFacilities((List<?>) getParameter("origin_facility"));
		List<Object> dest_facilities = getFacilities((List<?>) getParameter("dest_facility"));
		List<Object> products = getProducts((List<?>) getParameter("product"));
		List<Object> status_transfer_id = getStatusIds((List<?>) getParameter("status_transfer_id"));
		List<Object> categories = getCategories((List<?>) getParameter("categories"));
		String dateType = getDateType((String) getParameter("dateType"));

		query2.select("actual_exported_quantity").select("quantity").select("transfer_id")
				.select("actual_delivered_quantity")
				.select("tif.transfer_type_id").select("tif.status_id").select("tif.delivery_id")
				.select("tif.delivery_status_id").select("tif.transfer_date").select("tif.datetime_manufactured")
				.select("tif.expire_date").select("tif.lot_id").select("dd.date_value", "create_date")
				.select("dd.".concat(dateType)).select("fd1.facility_name", "origin_facility")
				.select("fd2.facility_name", "dest_facility").select("pd.product_code", "product_code")
				.select("pd.product_name", "product_name").select("cd.description", "quantity_uom")
				.select("sd.status_id", "status_transfer_id").select("pcr.category_dim_id", "category_dim_id")
				.from("transfer_item_fact", "tif")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = tif.create_date_dim_id")
				.join(Join.INNER_JOIN, "facility_dimension", "fd1", "fd1.dimension_id = tif.origin_facility_dim_id")
				.join(Join.INNER_JOIN, "facility_dimension", "fd2", "fd2.dimension_id = tif.dest_facility_dim_id")
				.join(Join.INNER_JOIN, "product_dimension", "pd", "pd.dimension_id = tif.product_dim_id")
				.join(Join.INNER_JOIN, "currency_dimension", "cd", "cd.dimension_id = tif.quantity_uom_dim_id")
				.join(Join.INNER_JOIN, "status_dimension", "sd", "sd.dimension_id = tif.status_transfer_dim_id")
				.join(Join.LEFT_OUTER_JOIN, "product_category_relationship", "pcr",
						"pcr.product_dim_id = tif.product_dim_id")
				.where(Condition.makeBetween("create_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andIn("origin_facility_dim_id", origin_facilities)
						.andIn("dest_facility_dim_id", dest_facilities).andIn("tif.product_dim_id", products)
						.andIn("tif.status_transfer_dim_id", status_transfer_id,
								UtilValidate.isNotEmpty(status_transfer_id)))
				.orderBy("dd.".concat(dateType));

		query.select("temp.*").select("catd.category_name", "category_name").from(query2, "temp")
				.join(Join.INNER_JOIN, "category_dimension", "catd", "catd.dimension_id = temp.category_dim_id")
				.where(Condition.makeEQ("catd.category_type", "CATALOG_CATEGORY").or("catd.category_type IS NULL")
						.andIn("tmp.category_dim_id", categories, UtilValidate.isNotEmpty(categories)));
		return query;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("transfer_id", "transfer_id");
		addDataField("quantity", "quantity");
		addDataField("actual_exported_quantity", "actual_exported_quantity");
		addDataField("actual_delivered_quantity", "actual_delivered_quantity");
		addDataField("transfer_type_id", "transfer_type_id");
		addDataField("status_id", "status_id");
		addDataField("delivery_id", "delivery_id");
		addDataField("delivery_status_id", "delivery_status_id");
		addDataField("transfer_date", "transfer_date");
		addDataField("datetime_manufactured", "datetime_manufactured");
		addDataField("expire_date", "expire_date");
		addDataField("lot_id", "lot_id");
		addDataField("origin_facility", "origin_facility");
		addDataField("dest_facility", "dest_facility");
		addDataField("product_code", "product_code");
		addDataField("product_name", "product_name");
		addDataField("category_name", "category_name");
		addDataField("quantity_uom", "quantity_uom");
		addDataField("status_transfer_id", "status_transfer_id");
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
				map.put("quantity", result.getBigDecimal("quantity"));
				map.put("actual_exported_quantity", result.getBigDecimal("actual_exported_quantity"));
				map.put("actual_delivered_quantity", result.getBigDecimal("actual_delivered_quantity"));
				map.put("transfer_id", result.getString("transfer_id"));
				map.put("transfer_type_id", result.getString("transfer_type_id"));
				map.put("status_id", result.getString("status_id"));
				map.put("delivery_id", result.getString("delivery_id"));
				map.put("delivery_status_id", result.getString("delivery_status_id"));
				map.put("transfer_date", result.getDate("transfer_date"));
				map.put("datetime_manufactured", result.getDate("datetime_manufactured"));
				map.put("expire_date", result.getDate("expire_date"));
				map.put("lot_id", result.getString("lot_id"));
				map.put("origin_facility", result.getString("origin_facility"));
				map.put("dest_facility", result.getString("dest_facility"));
				map.put("product_code", result.getString("product_code"));
				map.put("product_name", result.getString("product_name"));
				map.put("category_name", result.getString("category_name"));
				map.put("quantity_uom", result.getString("quantity_uom"));
				map.put("status_transfer_id", result.getString("status_transfer_id"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return map;
		}
	}
}
