package com.olbius.basepo.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.CategoryDimension;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.cache.dimension.PartyDimension;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class POReturnReport extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));

		putParameter("dateType", context.get("dateType"));

		putParameter("facility", context.get("facility[]"));

		putParameter("product", context.get("product[]"));

		putParameter("categories", context.get("categories[]"));

		GenericValue userLogin = (GenericValue) context.get("userLogin");

		putParameter("organizationId",
				MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));

	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
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

	private Object getParty(String partyId) {
		if (partyId != null) {
			return PartyDimension.D.getId(delegator, partyId);
		}
		return null;
	}

	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();

		OlbiusQuery tmp = makeQuery();

		List<Object> facilities = getFacilities((List<?>) getParameter("facility"));
		List<Object> products = getProducts((List<?>) getParameter("product"));
		List<Object> categories = getCategories((List<?>) getParameter("categories"));
		String organizationId = (String) getParameter("organizationId");

		query.select("rif.return_id").select("rif.status_date_dim_id").select("rif.product_dim_id")
				.select("rif.received_quantity")
				.select("rif.return_quantity").select("rif.quantity_uom_id").select("rif.to_party_dim_id")
				.select("rif.from_party_dim_id").select("rif.facility_dim_id").select("rif.order_id")
				.select("rif.return_reason_dim_id").select("rif.status_dim_id").from("return_item_fact", "rif")
				.where(Condition
						.makeBetween("rif.status_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("rif.from_party_dim_id", getParty(organizationId)).andIn("rif.product_dim_id", products)
						.andIn("rif.facility_dim_id", facilities));

		tmp.select("rif.return_id").select("rif.return_quantity").select("rif.order_id").select("rif.received_quantity")
				.select("dd.year_month_day", "return_date").select("prd.product_id").select("prd.product_code")
				.select("prd.product_name").select("prd.primary_category_dim_id")
				.select("cd.description", "quantity_uom").select("pd.party_id", "to_party_id")
				.select("pd.name", "to_party_name").select("fd.facility_id").select("fd.facility_name")
				.select("rrd.description", "return_reason").select("sd.description", "return_status").from(query, "rif")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = rif.status_date_dim_id")
				.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = rif.product_dim_id")
				.join(Join.INNER_JOIN, "currency_dimension", "cd", "cd.currency_id = rif.quantity_uom_id")
				.join(Join.INNER_JOIN, "facility_dimension", "fd", "fd.dimension_id = rif.facility_dim_id")
				.join(Join.INNER_JOIN, "return_reason_dimension", "rrd", "rrd.dimension_id = rif.return_reason_dim_id")
				.join(Join.INNER_JOIN, "status_dimension", "sd", "sd.dimension_id = rif.status_dim_id")
				.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = rif.to_party_dim_id");

		query = makeQuery();

		query.select("rif.*").select("catd.category_name").from(tmp, "rif")
				.join(Join.LEFT_OUTER_JOIN, "category_dimension", "catd",
						"catd.dimension_id = rif.primary_category_dim_id")
				.where(Condition.makeIn("rif.primary_category_dim_id", categories));

		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("return_id", "return_id");
		addDataField("order_id", "order_id");
		addDataField("return_date", "return_date");
		addDataField("return_status", "return_status");
		addDataField("product_id", "product_id");
		addDataField("product_code", "product_code");
		addDataField("product_name", "product_name");
		addDataField("category_name", "category_name");
		addDataField("return_quantity", "return_quantity");
		addDataField("received_quantity", "received_quantity");
		addDataField("quantity_uom", "quantity_uom");
		addDataField("return_reason", "return_reason");
		addDataField("facility_id", "facility_id");
		addDataField("facility_name", "facility_name");
		addDataField("to_party_id", "to_party_id");
		addDataField("to_party_name", "to_party_name");
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
				map.put("return_id", result.getString("return_id"));
				map.put("order_id", result.getString("order_id"));
				map.put("return_date", result.getString("return_date"));
				map.put("return_status", result.getString("return_status"));
				map.put("product_id", result.getString("product_id"));
				map.put("product_code", result.getString("product_code"));
				map.put("product_name", result.getString("product_name"));
				map.put("category_name", result.getString("category_name"));
				map.put("return_quantity", result.getBigDecimal("return_quantity"));
				map.put("received_quantity", result.getBigDecimal("received_quantity"));
				map.put("quantity_uom", result.getString("quantity_uom"));
				map.put("facility_id", result.getString("facility_id"));
				map.put("facility_name", result.getString("facility_name"));
				map.put("to_party_id", result.getString("to_party_id"));
				map.put("to_party_name", result.getString("to_party_name"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return map;
		}
	}
}
