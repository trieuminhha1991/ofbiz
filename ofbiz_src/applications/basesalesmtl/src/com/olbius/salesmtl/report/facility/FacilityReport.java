package com.olbius.salesmtl.report.facility;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Case;
import com.olbius.bi.olap.query.function.Max;
import com.olbius.bi.olap.query.function.Min;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class FacilityReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private ReturnResultGridFacility result = new ReturnResultGridFacility();

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));

		putParameter("dateType", context.get("dateType"));

		putParameter("group", context.get("group[]"));

		putParameter("facility", context.get("facility[]"));

		putParameter("product", context.get("product[]"));
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

	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();

		OlbiusQuery tmp = makeQuery();

		List<Object> facilities = getFacilities((List<?>) getParameter("facility"));
		List<Object> products = getProducts((List<?>) getParameter("product"));
		String dateType = getDateType((String) getParameter("dateType"));
		List<?> groups = (List<?>) getParameter("group");

		String slt = "";
		boolean flag = false;
		if (groups != null) {
			for (Object x : groups) {
				String s = (String) x;
				switch (s) {
				case "facility":
					s = "facility_dim_id";
					break;
				case "product":
					s = "product_dim_id";
					break;
				case "uom":
					s = "quantity_uom_dim_id";
					break;
				default:
					continue;
				}
				if (flag) {
					slt += ",";
				}
				slt += s;
				flag = true;
			}
		}
		flag = !slt.isEmpty();

		tmp.select("inventory_date_dim_id").from("inventory_item_fact", "iif")
				.select(new Sum("iif.inventory_type ='EXPORT'", "-quantity_on_hand_total", "0"), "")
				.select(new Sum("iif.inventory_type ='RECEIVE'", "quantity_on_hand_total", "0"), "")

				.select(new Sum("iif.inventory_type ='EXPORT'",
						"-(CASE WHEN prd.require_amount = 'Y' THEN iif.amount_on_hand_total ELSE iif.quantity_on_hand_total END)",
						"0"), "export")
				.select(new Sum("iif.inventory_type ='RECEIVE'",
						"(CASE WHEN prd.require_amount = 'Y' THEN iif.amount_on_hand_total ELSE iif.quantity_on_hand_total END)",
						"0"), "receive")
				.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = iif.product_dim_id")
				.where(Condition
						.makeBetween("iif.inventory_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andIn("iif.facility_dim_id", facilities).andIn("iif.product_dim_id", products))
				.groupBy("iif.inventory_date_dim_id");

		query.select(dateType).select(new Sum("iif.export"), "export").select(new Sum("iif.receive"), "receive")
				.from(tmp, "iif")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "iif.inventory_date_dim_id = dd.dimension_id")
				.groupBy(dateType);

		tmp.select(slt, flag).groupBy(slt, flag);
		query.select(slt, flag).groupBy(slt, flag);

		tmp = makeQuery();
		tmp.from(query, "ref")
				.extend(makeQuery().distinct().select(dateType).from("date_dimension").where(
						Condition.makeBetween("dimension_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))),
						"ddex", true);

		tmp.select("ddex.".concat(dateType))
				.select(new Case().when("ref.".concat(dateType).concat(" = ddex.").concat(dateType), "ref.receive")
						.els("0"), "receive")
				.select(new Case().when("ref.".concat(dateType).concat(" = ddex.").concat(dateType), "ref.export")
						.els("0"), "export");

		tmp.select(slt, flag).where(Condition.orCondition(Condition.make("ref.".concat(dateType).concat(" <= ddex.").concat(dateType)), Condition.make("ref.".concat(dateType).concat(" >= ddex.").concat(dateType))));
		
		query = makeQuery();
		query.from(tmp, "query");
		query.select(dateType).select(new Sum("receive"), "receive").select(new Sum("export"), "export")
				.groupBy(dateType);

		query.select(slt, flag).groupBy(slt, flag);

		tmp = makeQuery();

		tmp.select("tmp.*").from(query, "tmp");

		if (groups != null) {
			for (Object x : groups) {
				String s = (String) x;
				switch (s) {
				case "facility":
					tmp.select("fd.facility_id", "fd.facility_name", "party_dimension.party_id")
							.select("party_dimension.name", "partyName");
					tmp.join(Join.INNER_JOIN, "facility_dimension", "fd", "fd.dimension_id = tmp.facility_dim_id");
					tmp.join(Join.LEFT_OUTER_JOIN, "party_dimension",
							"party_dimension.dimension_id = fd.owner_party_dim_id");
					tmp.orderBy("fd.facility_id");
					break;
				case "product":
					tmp.select("prd.product_id").select("prd.product_name");
					tmp.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = tmp.product_dim_id");
					tmp.orderBy("prd.product_id");
					break;
				case "uom":
					tmp.select("ud.uom_id").select("ud.description", "uom_name");
					tmp.join(Join.INNER_JOIN, "uom_dimension", "ud", "ud.dimension_id = tmp.quantity_uom_dim_id");
					tmp.orderBy("ud.uom_id");
					break;
				default:
					continue;
				}
			}
		}
		tmp.orderBy(dateType);

		return tmp;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("export", "export");
		addDataField("receive", "receive");
		addDataField("inventory");
		addDataField("inventoryP");
		List<?> groups = (List<?>) getParameter("group");
		if (groups != null) {
			for (Object x : groups) {
				String s = (String) x;
				switch (s) {
				case "facility":
					s = "facility_name";
					addDataField("party", "partyName");
					break;
				case "product":
					s = "product_name";
					break;
				case "uom":
					s = "uom_name";
					break;
				default:
					continue;
				}
				addDataField((String) x, s);
			}
		}
		result.fromDate = getSqlTime(getFromDate());
		result.thruDate = getSqlTime(getThruDate());
	}

	@Override
	protected OlapResultQueryInterface returnResultGrid() {
		return result;
	}

	private class ReturnResultGridFacility extends ReturnResultGridEx {

		private long fromDate;
		private long thruDate;

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = super.getObject(result);

			try {
				String dateType = getDateType((String) getParameter("dateType"));
				Object tmp = map.get("dateTime");

				OlbiusQuery query = makeQuery();

				query.select(new Min("date_value"), "min").select(new Max("date_value"), "max").from("date_dimension")
						.where(Condition.makeEQ(dateType, tmp).andBetween("dimension_id", fromDate, thruDate));

				java.sql.Date min = null;
				java.sql.Date max = null;

				ResultSet resultSet = null;
				try {
					resultSet = query.getResultSet();
					if (resultSet.next()) {
						min = resultSet.getDate("min");
						max = resultSet.getDate("max");
					}
				} finally {
					if (resultSet != null) {
						resultSet.close();
					}
				}

				OlbiusQuery queryMax = makeQuery();

				queryMax.distinctOn("ff.facility_dim_id", "ff.product_dim_id", "ff.manufactured_date_dim_id",
						"ff.expire_date_dim_id", "ff.uom_dim_id").select("*")
						.select("CASE WHEN prd.require_amount = 'Y' THEN inventory_total_aoh ELSE inventory_total END AS total")
						.from("facility_fact", "ff")
						.join(Join.INNER_JOIN, "product_dimension", "prd", "ff.product_dim_id = prd.dimension_id")
						.where(Condition.make("ff.date_dim_id", Condition.LESS_EQ, getSqlTime(getSqlThruDate(max))))
						.orderBy("ff.facility_dim_id", "ff.product_dim_id", "ff.manufactured_date_dim_id", "ff.expire_date_dim_id",
								"ff.uom_dim_id")
						.orderBy("ff.date_dim_id", OlbiusQuery.DESC);

				OlbiusQuery queryMin = makeQuery();

				queryMin.distinctOn("ff.facility_dim_id", "ff.product_dim_id", "ff.manufactured_date_dim_id",
						"ff.expire_date_dim_id", "ff.uom_dim_id").select("*")
						.select("CASE WHEN prd.require_amount = 'Y' THEN inventory_total_aoh ELSE inventory_total END AS total")
						.from("facility_fact", "ff")
						.join(Join.INNER_JOIN, "product_dimension", "prd", "ff.product_dim_id = prd.dimension_id")
						.where(Condition.make("date_dim_id", Condition.LESS_EQ, getSqlTime(getSqlFromDate(min))))
						.orderBy("ff.facility_dim_id", "ff.product_dim_id", "ff.manufactured_date_dim_id", "ff.expire_date_dim_id",
								"ff.uom_dim_id")
						.orderBy("ff.date_dim_id", OlbiusQuery.DESC);

				List<?> groups = (List<?>) getParameter("group");
				if (groups != null) {
					for (Object x : groups) {
						String s = (String) x;
						switch (s) {
						case "facility":
							queryMax.where().andEQ("facility_dim_id", result.getLong("facility_dim_id"));
							queryMin.where().andEQ("facility_dim_id", result.getLong("facility_dim_id"));
							break;
						case "product":
							queryMax.where().andEQ("product_dim_id", result.getLong("product_dim_id"));
							queryMin.where().andEQ("product_dim_id", result.getLong("product_dim_id"));
							break;
						case "uom":
							queryMax.where().andEQ("ff.uom_dim_id", result.getLong("quantity_uom_dim_id"));
							queryMin.where().andEQ("ff.uom_dim_id", result.getLong("quantity_uom_dim_id"));
							break;
						default:
							continue;
						}
					}
				}

				try {
					resultSet = makeQuery().select(new Sum("total"), "total").from(queryMax, "tmp")
							.getResultSet();
					if (resultSet.next()) {
						if (UtilValidate.isNotEmpty(resultSet.getBigDecimal("total"))) {
							map.put("inventory", resultSet.getBigDecimal("total"));
						} else {
							map.put("inventory", 0);
						}
					} else {
						map.put("inventory", 0);
					}
				} finally {
					if (resultSet != null) {
						resultSet.close();
					}
				}

				try {
					resultSet = makeQuery().select(new Sum("total"), "total").from(queryMin, "tmp")
							.getResultSet();
					if (resultSet.next()) {
						if (UtilValidate.isNotEmpty(resultSet.getBigDecimal("total"))) {
							map.put("inventoryP", resultSet.getBigDecimal("total"));
						} else {
							map.put("inventoryP", 0);
						}
					} else {
						map.put("inventoryP", 0);
					}
				} finally {
					if (resultSet != null) {
						resultSet.close();
					}
				}

			} catch (Exception e) {
				Debug.logError(e, getModule());
			}

			return map;
		}

	}

}
