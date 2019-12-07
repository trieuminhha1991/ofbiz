package com.olbius.baselogistics.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.cache.dimension.CategoryDimension;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class ExportInventoryCancel extends OlbiusOlapService {
	
	private OlbiusQuery query;
	public static final String enum_reason_id = "EXPORT_CANCEL";
	@Override
	public void prepareParameters(DispatchContext dctx,
			Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		
		putParameter("facility", context.get("facility[]"));
		putParameter("product", context.get("product[]"));
		putParameter("category", context.get("category[]"));
		
		putParameter("reasonEnumId", "EXPORT_CANCEL");
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
	
	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();

		List<Object> facilities = getFacilities((List<?>) getParameter("facility"));
		List<Object> products = getProducts((List<?>) getParameter("product"));
		List<Object> categories = getCategories((List<?>) getParameter("category"));
		
		String reasonEnumId = enum_reason_id;

		String dateType = getDateType((String) getParameter("dateType"));

		query.select("CASE WHEN pd.require_amount = 'Y' THEN iif.amount_on_hand_total * (-1) ELSE iif.quantity_on_hand_total * (-1) END AS quantity_on_hand_total")
			.select("iif.inventory_type")
			.select("fd.facility_name")
			.select("pd.product_code").select("pd.product_name")
			.select("dd1.date_value", "inventory_date").select("dd1.".concat(dateType))
			.select("dd2.date_value", "expire_date")
			.select("dd3.date_value", "manufactured_date")
			.select("enum.description", "reason_enum_id")
			.select("lot.lot_id", "lot_id")
			.select("uom.description", "quantity_uom_id")
			.select("cate.category_name", "category_id")
			.from("inventory_item_fact", "iif")
			.join(Join.INNER_JOIN, "facility_dimension", "fd", "fd.dimension_id = iif.facility_dim_id")
			.join(Join.INNER_JOIN, "product_dimension", "pd", "pd.dimension_id = iif.product_dim_id")
			.join(Join.INNER_JOIN, "date_dimension", "dd1", "dd1.dimension_id = iif.inventory_date_dim_id")
			.join(Join.INNER_JOIN, "date_dimension", "dd2", "dd2.dimension_id = iif.expire_date_dim_id")
			.join(Join.INNER_JOIN, "date_dimension", "dd3", "dd3.dimension_id = iif.manufactured_date_dim_id")
			.join(Join.INNER_JOIN, "enumeration_dimension", "enum", "enum.dimension_id = iif.enumeration_dim_id")
			.join(Join.INNER_JOIN, "lot_dimension", "lot", "lot.dimension_id = iif.lot_dim_id")
			.join(Join.INNER_JOIN, "uom_dimension", "uom", "uom.dimension_id = iif.quantity_uom_dim_id")
			.join(Join.INNER_JOIN, "category_dimension", "cate", "cate.dimension_id = iif.category_dim_id")
			.where(Condition.makeBetween("inventory_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
					.andIn("facility_dim_id", facilities).andIn("product_dim_id", products).andIn("iif.category_dim_id", categories).andEQ("enum.enum_id", reasonEnumId).and("enum.description is not null", true));

		return query;
	}
	
	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("quantity_on_hand_total", "quantity_on_hand_total");
		addDataField("facility_name", "facility_name");
		addDataField("product_code", "product_code");
		addDataField("product_name", "product_name");
		addDataField("inventory_date", "inventory_date");
		addDataField("expire_date", "expire_date");
		addDataField("manufactured_date", "manufactured_date");
		addDataField("reason_enum_id", "reason_enum_id");
		addDataField("lot_id", "lot_id");
		addDataField("quantity_uom_id", "quantity_uom_id");
		addDataField("category_id", "category_id");
	}
}
