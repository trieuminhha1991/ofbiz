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
import com.olbius.bi.olap.cache.dimension.PartyDimension;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class PurchaseOrderReport extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));

		putParameter("dateType", context.get("dateType"));

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

	private Object getParty(String partyId) {
		if (partyId != null) {
			return PartyDimension.D.getId(delegator, partyId);
		}
		return null;
	}

	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();

		OlbiusQuery tmp = makeQuery();

		List<Object> categories = getCategories((List<?>) getParameter("categories"));
		String organizationId = (String) getParameter("organizationId");

		query.select("pof.order_id").select("pof.order_date_dim_id").select("pof.product_dim_id")
				.select("pof.party_from_dim_id").select("pof.quantity").select("pof.quantity_uom_id")
				.select("pof.selected_amount").select("pof.weight_uom_dim_id").select("pof.status_dim_id")
				.from("purchase_order_fact", "pof")
				.where(Condition
						.makeBetween("pof.order_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("pof.party_to_dim_id", getParty(organizationId)))
				.groupBy("pof.order_id", "pof.order_date_dim_id", "pof.product_dim_id", "pof.party_from_dim_id",
						"pof.quantity", "pof.quantity_uom_id", "pof.status_dim_id", "pof.weight_uom_dim_id",
						"pof.selected_amount")
				.orderBy("pof.order_id");

		tmp.select("pof.order_id").select("pd.party_id", "party_from_id").select("pd.name", "party_from_name")
				.select("prd.product_id").select("prd.product_code").select("prd.product_name")
				.select("prd.primary_category_dim_id").select("dd.year_month_day", "order_date")
				.select("CASE WHEN prd.require_amount = 'Y' THEN (pof.quantity * pof.selected_amount) ELSE pof.quantity END AS quantity")
				.select("CASE WHEN prd.require_amount = 'Y' THEN cdw.description ELSE cd.description END AS quantity_uom")
				.select("sd.description", "order_status").from(query, "pof")
				.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = pof.party_from_dim_id")
				.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = pof.product_dim_id")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = pof.order_date_dim_id")
				.join(Join.INNER_JOIN, "currency_dimension", "cd", "cd.currency_id = pof.quantity_uom_id")
				.join(Join.INNER_JOIN, "currency_dimension", "cdw", "cdw.dimension_id = pof.weight_uom_dim_id")
				.join(Join.INNER_JOIN, "status_dimension", "sd", "sd.dimension_id = pof.status_dim_id");

		query = makeQuery();

		query.select("pof.*").select("catd.category_name").from(tmp, "pof")
				.join(Join.LEFT_OUTER_JOIN, "category_dimension", "catd",
						"catd.dimension_id = pof.primary_category_dim_id");
		if (!categories.isEmpty()){
			query.where(Condition.makeIn("pof.primary_category_dim_id", categories));
		}
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("order_id", "order_id");
		addDataField("order_date", "order_date");
		addDataField("order_status", "order_status");
		addDataField("quantity", "quantity");
		addDataField("quantity_uom", "quantity_uom");
		addDataField("product_id", "product_id");
		addDataField("product_code", "product_code");
		addDataField("product_name", "product_name");
		addDataField("category_name", "category_name");
		addDataField("party_from_id", "party_from_id");
		addDataField("party_from_name", "party_from_name");
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
				map.put("order_id", result.getString("order_id"));
				map.put("order_date", result.getString("order_date"));
				map.put("order_status", result.getString("order_status"));
				map.put("quantity", result.getBigDecimal("quantity"));
				map.put("quantity_uom", result.getString("quantity_uom"));
				map.put("product_id", result.getString("product_id"));
				map.put("product_code", result.getString("product_code"));
				map.put("product_name", result.getString("product_name"));
				map.put("category_name", result.getString("category_name"));
				map.put("party_from_id", result.getString("party_from_id"));
				map.put("party_from_name", result.getString("party_from_name"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return map;
		}
	}
}
