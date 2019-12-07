package com.olbius.basesales.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class SalesEffectiveness extends OlbiusOlapService {

	private OlbiusQuery queryX;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {

		putParameter("forecastType", context.get("forecastType"));

		putParameter("ORG", context.get("ORG"));

		putParameter("BOO_DIS", context.get("BOO_DIS"));

		putParameter("DIS_ID", context.get("DIS_ID"));

		putParameter("YEAR", context.get("YEAR"));

		putParameter("MONTH", context.get("MONTH"));

		putParameter("product", context.get("product[]"));

	}

	@Override
	protected OlapQuery getQuery() {
		if (queryX == null) {
			queryX = init();
		}
		return queryX;
	}

	@SuppressWarnings("unchecked")
	private OlbiusQuery init() {
		OlbiusQuery query = makeQuery();
		OlbiusQuery fromQuery = makeQuery();
		OlbiusQuery joinQuery = makeQuery();

		String forecastType = (String) getParameter("forecastType");
		String organization = (String) getParameter("ORG");
		String dis = (String) getParameter("BOO_DIS");
		String disId = (String) getParameter("DIS_ID");
		int salesYear = Integer.parseInt((String) getParameter("YEAR"));
		int salesMonth = Integer.parseInt((String) getParameter("MONTH"));

		Condition condition = new Condition();
		condition.and("pd.product_id is NOT NULL").and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")
				.and("se.party_code is NOT NULL").and("sales_order_fact.order_status = 'ORDER_COMPLETED'");
		condition.andEQ("dd.year_name", salesYear);
		condition.andEQ("dd.month_of_year", salesMonth);
		Condition condition2 = new Condition();
		condition2.and("sfdj.sales_forecast_id is NOT NULL").andEQ("ddj.year_name", salesYear)
				.andEQ(" ddj.month_of_year", salesMonth).andEQ("organ_join.party_id", organization);

		if ("true".equals(dis)) {
			condition2.and("sfdj.type = 'SALES_IN'");
			condition2.andEQ("sej.party_id", disId);
		} else {
			condition2.andEQ("sfdj.type", forecastType);
		}

		fromQuery.distinct().select("pd.product_id").select("se.dimension_id", "se_id").select("se.party_code")
				.select("COALESCE(se.last_name, '') || ' ' || COALESCE(se.middle_name, '') || ' ' || COALESCE(se.first_name, '')",
						"se_name")
				.select("se.party_id", "se_party").select("pd.product_code").select("pd.internal_name")
				.select("sales_order_fact.product_dim_id").select("SUM(sales_order_fact.quantity)", "real_quantity")
				.select("dd.month_of_year").select("dd.year_name").from("sales_order_fact")
				.join(Join.RIGHT_OUTER_JOIN, "product_dimension", "pd",
						"sales_order_fact.product_dim_id = pd.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "organ",
						"organ.dimension_id = sales_order_fact.party_from_dim_id")
				.join(Join.INNER_JOIN, "party_dimension", "se",
						"se.dimension_id = sales_order_fact.sale_executive_party_dim_id")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sales_order_fact.order_date_dim_id")
				.where(condition).groupBy("pd.dimension_id").groupBy("dd.dimension_id")
				.groupBy("sales_order_fact.product_dim_id").groupBy("se.dimension_id");

		joinQuery.select("sfdj.product_dim_id", "pro_dim").select("sfdj.quantity", "expected_quantity")
				.select("sfdj.custom_time_period_id").select("sfdj.internal_party_dim_id")
				.select("sej.party_id", "sej_party").select("sej.party_code")
				.select("COALESCE(sej.name, '') || ' ' || COALESCE(sej.last_name, '') || ' ' || COALESCE(sej.middle_name, '') || ' ' || COALESCE(sej.first_name, '')",
						"sej_name")
				.select("sej.name", "sej_name_group").select("ddj.month_of_year").select("ddj.year_name")
				.select("pdj.product_id", "pro_id").select("pdj.product_code", "pro_code")
				.select("pdj.internal_name", "pro_name").from("sales_forecast_dimension", "sfdj")
				.join(Join.INNER_JOIN, "date_dimension", "ddj", "ddj.dimension_id = sfdj.from_date_dim_id")
				.join(Join.INNER_JOIN, "party_dimension", "organ_join",
						"organ_join.dimension_id = sfdj.organization_party_dim_id")
				.join(Join.INNER_JOIN, "party_dimension", "sej", "sej.dimension_id = sfdj.internal_party_dim_id")
				.join(Join.INNER_JOIN, "product_dimension", "pdj", "pdj.dimension_id = sfdj.product_dim_id")
				.where(condition2).orderBy("ddj.month_of_year", OlbiusQuery.ASC);

		query.select("QUERYJOIN.party_code", "se_code").select("QUERYJOIN.sej_name", "se_full_name")
				.select("sej_name_group", "se_group_name");
		OlbiusQuery temp = makeQuery();
		temp.select("temp.*");
		List<Object> product = (List<Object>) getParameter("product");
		for (Object x : product) {
			String productId = x.toString();
			query.select(
					"sum(case when QUERYFROM.product_id = '" + productId + "' then QUERYFROM.real_quantity else 0 end)",
					'"' + productId + "_a" + '"');
			query.select(
					"sum(case when QUERYJOIN.pro_id = '" + productId + "' then QUERYJOIN.expected_quantity else 0 end)",
					'"' + productId + "_e" + '"');
			temp.select("CASE WHEN \"" + productId + "_a\" > 0 AND \"" + productId + "_e\" > 0 THEN \"" + productId
					+ "_a\" / \"" + productId + "_e\" * 100 ELSE 0 END", "\"" + productId + "_p\"");
		}
		query.from(fromQuery, "QUERYFROM")
				.join(Join.RIGHT_OUTER_JOIN, joinQuery, "QUERYJOIN",
						"(QUERYFROM.se_id = QUERYJOIN.internal_party_dim_id AND QUERYFROM.product_id = QUERYJOIN.pro_id AND QUERYJOIN.month_of_year = QUERYFROM.month_of_year AND QUERYJOIN.year_name = QUERYFROM.year_name AND QUERYJOIN.pro_id = QUERYFROM.product_id AND QUERYJOIN.pro_code = QUERYFROM.product_code AND QUERYFROM.se_party = QUERYJOIN.sej_party)")
				.groupBy("se_code").groupBy("se_full_name").groupBy("se_group_name");

		temp.from(query, "temp");

		return temp;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void prepareResultGrid() {
		addDataField("se_code", "se_code");
		addDataField("se_name", "se_full_name");
		List<Object> product = (List<Object>) getParameter("product");
		for (Object x : product) {
			String productId = x.toString();
			addDataField(productId + "_a", productId + "_a");
			addDataField(productId + "_e", productId + "_e");
			addDataField(productId + "_p", productId + "_p");
		}
	}

	@Override
	protected OlapResultQueryInterface returnResultGrid() {
		return new ReturnResultGridFacility();
	}

	private class ReturnResultGridFacility extends ReturnResultGridEx {
		@SuppressWarnings("unchecked")
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = super.getObject(result);
			try {
				map.put("se_code", result.getString("se_code"));
				map.put("se_name", result.getString("se_full_name"));
				List<Object> product = (List<Object>) getParameter("product");
				for (Object x : product) {
					String productId = x.toString();
					map.put(productId + "_a", result.getString(productId + "_a"));
					map.put(productId + "_e", result.getString(productId + "_e"));
					map.put(productId + "_p", result.getString(productId + "_p"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return map;
		}
	}

}
