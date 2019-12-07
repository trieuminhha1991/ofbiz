package com.olbius.baselogistics.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.CategoryDimension;
import com.olbius.bi.olap.cache.dimension.PartyDimension;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Max;
import com.olbius.bi.olap.query.function.Min;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class InventoryCustomer extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));

		putParameter("dateType", context.get("dateType"));

		putParameter("product", context.get("product[]"));

		putParameter("categories", context.get("categories[]"));

		putParameter("parties", context.get("parties[]"));

		putParameter("userLogin", context.get("userLogin"));

	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private List<Object> getParties(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = PartyDimension.D.getId(delegator, (String) x);
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
		OlbiusQuery query0 = makeQuery();
		OlbiusQuery query1 = makeQuery();
		OlbiusQuery query2 = makeQuery();

		List<Object> products = getProducts((List<?>) getParameter("product"));
		List<Object> categories = getCategories((List<?>) getParameter("categories"));

		List<?> partyIds = (List<?>) getParameter("parties");

		if (UtilValidate.isEmpty(partyIds)) {
			try {
				GenericValue userLogin = (GenericValue) getParameter("userLogin");
				String userLoginPartyId = userLogin.getString("partyId");
				if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
					partyIds = PartyWorker.getCustomerIdsBySup(delegator, userLoginPartyId);
				} else if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
					partyIds = PartyWorker.getCustomerIdsBySalesExecutive(delegator, userLoginPartyId);
				} else if (SalesPartyUtil.isDistributor(delegator, userLoginPartyId)) {

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		List<Object> parties = getParties(partyIds);
		String dateType = getDateType((String) getParameter("dateType"));

		query0.select(new Sum("dif.actual_delivered_quantity"), "actual_delivered_quantity")
				.select("dif.product_dim_id").select("dif.party_to_dim_id")
				.from("delivery_item_fact", "dif").where(
						Condition
								.makeBetween("actual_arrival_date_dim_id", getSqlTime(getFromDate()),
										getSqlTime(getThruDate()))
								.andEQ("dif.delivery_type_id", "DELIVERY_SALES")
								.andEQ("dif.status_id", "DELI_ITEM_DELIVERED").andIn("dif.product_dim_id", products)
								.andIn("dif.party_to_dim_id", parties))
				.groupBy("dif.product_dim_id").groupBy("dif.party_to_dim_id");

		query1.select("picf.inventory_date_dim_id").select("pd.product_code").select("pd.product_name")
				.select("pd2.party_code").select("pd2.name").select("pcr.category_dim_id").select("picf.product_dim_id")
				.select("picf.party_dim_id").from("product_inventory_customer_fact", "picf")
				.where(Condition
						.makeBetween("picf.inventory_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andIn("picf.product_dim_id", products).andIn("picf.party_dim_id", parties))
				.join(Join.INNER_JOIN, "product_dimension", "pd", "pd.dimension_id = picf.product_dim_id")
				.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = picf.party_dim_id")
				.join(Join.LEFT_OUTER_JOIN, "product_category_relationship", "pcr",
						"pcr.product_dim_id = picf.product_dim_id")
				.orderBy("picf.party_dim_id", OlbiusQuery.DESC);

		query2 = makeQuery();

		query2.select("temp.*").select("catd.category_name", "category_name").from(query1, "temp")
				.join(Join.INNER_JOIN, "category_dimension", "catd", "catd.dimension_id = temp.category_dim_id")
				.where(Condition.makeEQ("catd.category_type", "CATALOG_CATEGORY").andIn("temp.category_dim_id",
						categories));

		query1 = makeQuery();

		query1.select(
				"case when SUM(dif.actual_delivered_quantity) <> 0 then SUM(dif.actual_delivered_quantity) else 0 end",
				"receive").select("temp.*")
				.from(query2, "temp")
				.join(Join.LEFT_OUTER_JOIN, query0, "dif",
						"dif.party_to_dim_id = temp.party_dim_id AND dif.product_dim_id = temp.product_dim_id")
				.groupBy("temp.product_code").groupBy("temp.product_name").groupBy("temp.category_name")
				.groupBy("temp.party_code").groupBy("temp.name").groupBy("temp.party_dim_id")
				.groupBy("temp.product_dim_id").groupBy("temp.category_dim_id").groupBy("temp.inventory_date_dim_id");

		query2 = makeQuery();

		query2.select(new Sum("receive"), "receive").select("temp.product_code").select("temp.product_name")
				.select("temp.category_name").select("temp.party_code").select("temp.name").select("temp.party_dim_id")
				.select("temp.product_dim_id").select("dd.".concat(dateType)).from(query1, "temp")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = temp.inventory_date_dim_id")
				.groupBy("temp.product_code").groupBy("temp.product_name").groupBy("temp.category_name")
				.groupBy("temp.party_code").groupBy("temp.name").groupBy("temp.party_dim_id")
				.groupBy("temp.product_dim_id").groupBy("dd.".concat(dateType)).orderBy("temp.party_code");

		return query2;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("receive", "receive");
		addDataField("product_code", "product_code");
		addDataField("product_name", "product_name");
		addDataField("category_name", "category_name");
		addDataField("party_code", "party_code");
		addDataField("name", "name");
		addDataField("inventoryF");
		addDataField("inventoryL");
		addDataField("export");
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
				BigDecimal receive = result.getBigDecimal("receive");
				map.put("receive", receive);
				map.put("product_code", result.getString("product_code"));
				map.put("product_name", result.getString("product_name"));
				map.put("category_name", result.getString("category_name"));
				map.put("party_code", result.getString("party_code"));
				map.put("name", result.getString("name"));

				OlbiusQuery query = makeQuery();

				String dateType = getDateType((String) getParameter("dateType"));
				query.select(new Min("date_value"), "min").select(new Max("date_value"), "max").from("date_dimension")
						.where(Condition.makeEQ(dateType, map.get("dateTime")));

				java.sql.Date min = null;
				java.sql.Date max = null;

				try {
					ResultSet resultSet = query.getResultSet();
					if (resultSet.next()) {
						min = resultSet.getDate("min");
						max = resultSet.getDate("max");
					}
				} finally {
					query.close();
				}

				OlbiusQuery queryMax = makeQuery();

				queryMax.select(new Sum("quantity"), "total").from("product_inventory_customer_fact")
						.where(Condition.make("inventory_date_dim_id", Condition.LESS_EQ,
								getSqlTime(getSqlThruDate(max))))
						.groupBy("inventory_date_dim_id").orderBy("inventory_date_dim_id", OlbiusQuery.DESC).limit(1);

				OlbiusQuery queryMin = makeQuery();

				queryMin.select(new Sum("quantity"), "total").from("product_inventory_customer_fact")
						.where(Condition.make("inventory_date_dim_id", Condition.LESS_EQ,
								getSqlTime(getSqlThruDate(min))))
						.groupBy("inventory_date_dim_id").orderBy("inventory_date_dim_id", OlbiusQuery.DESC).limit(1);

				queryMax.where().andEQ("party_dim_id", result.getLong("party_dim_id")).andEQ("product_dim_id",
						result.getLong("product_dim_id"));
				queryMin.where().andEQ("party_dim_id", result.getLong("party_dim_id")).andEQ("product_dim_id",
						result.getLong("product_dim_id"));

				BigDecimal inventoryL = BigDecimal.ZERO;
				BigDecimal inventoryF = BigDecimal.ZERO;
				try {
					ResultSet resultSet = queryMax.getResultSet();
					if (resultSet.next()) {
						inventoryL = resultSet.getBigDecimal("total");
					}
				} finally {
					query.close();
				}
				try {
					ResultSet resultSet = queryMin.getResultSet();
					if (resultSet.next()) {
						inventoryF = resultSet.getBigDecimal("total");
					}
				} finally {
					query.close();
				}
				map.put("inventoryL", inventoryL);
				map.put("inventoryF", inventoryF);
				if (inventoryL != null && inventoryF != null & receive != null) {
					map.put("export", inventoryL.subtract(inventoryF).add(receive));
				} else {
					map.put("export", null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}
	}
}
