package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class EvaluateEffectiveSalesOutImpl extends OlbiusBuilder {
	public EvaluateEffectiveSalesOutImpl(Delegator delegator) {
		super(delegator);
	}

	public static final String YEARR = "YEARR";
	public static final String MONTHH = "MONTHH";
	public static final String ORG = "ORG";
	public static final String FLAGIN = "FLAGIN";
	public static final String BOO_DIS = "BOO_DIS";
	public static final String DIS_ID = "DIS_ID";
	public static final String PRODUCTS = "products";

	// private List<String> type;
	// private List<String> getType() {
	// if(type == null) {
	// ResultProductStore enumType = new ResultProductStore(getSQLProcessor());
	// type = enumType.getListResultStore();
	// }
	// return type;
	// }
	private List<Map<String, String>> type;

	private List<Map<String, String>> getType() {
		if (type == null) {
			ResultProductStore enumType = new ResultProductStore(getSQLProcessor());
			type = enumType.getListResultStore();
		}
		return type;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("se_code", "se_code");
		addDataField("se_name", "se_full_name");
		
		List<Map<String, String>> type = getType();
		for (int i = 0; i < type.size(); i++) {
			String productId = (String) type.get(i).get("product_id");
			addDataField(productId + "_a", productId + "_a", new ReturnResultCallback<Object>() {
				@Override
				public Object get(Object object) {
					if (UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))) {
						return (BigDecimal) object;
					} else {
						return "-";
					}
				}
			});
			addDataField(productId + "_e", productId + "_e", new ReturnResultCallback<Object>() {
				@Override
				public Object get(Object object) {
					if (UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))) {
						return (BigDecimal) object;
					} else {
						return "-";
					}
				}
			});
			addDataField(productId + "_p", productId + "_p", new ReturnResultCallback<Object>() {
				@Override
				public Object get(Object object) {
					if (UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))) {
						return (BigDecimal) object;
					} else {
						return "-";
					}
				}
			});
		}
	}

	private OlbiusQuery query;
	

	@SuppressWarnings("unchecked")
	private OlbiusQuery initQuery() {
		
		String yearr = (String) getParameter(YEARR);
		String monthh = (String) getParameter(MONTHH);
		String organization = (String) getParameter(ORG);
		String flagIn = (String) getParameter(FLAGIN);
		Boolean dis = (Boolean) getParameter(BOO_DIS);
		String disId = (String) getParameter(DIS_ID);
		List<Object> products = (List<Object>) getParameter(PRODUCTS);
		int salesYear = Integer.parseInt(yearr);
		int salesMonth = Integer.parseInt(monthh);

		query = new OlbiusQuery(getSQLProcessor());
		OlbiusQuery fromQuery = new OlbiusQuery(getSQLProcessor());
		OlbiusQuery joinQuery = new OlbiusQuery(getSQLProcessor());

		Condition condition = new Condition();
		condition.and("pd.product_id is NOT NULL").and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'")
				.and("se.party_code is NOT NULL").and("sales_order_fact.order_status = 'ORDER_COMPLETED'");
		condition.andEQ("dd.year_name", salesYear);
		condition.andEQ("dd.month_of_year", salesMonth);
		condition.andIn("pd.product_id", products);
		Condition condition2 = new Condition();
		condition2.and("sfdj.sales_forecast_id is NOT NULL").andEQ("ddj.year_name", salesYear)
				.andEQ(" ddj.month_of_year", salesMonth).andEQ("organ_join.party_id", organization).andIn("pdj.product_id", products);

		if (UtilValidate.isNotEmpty(dis) && dis == true) {
			condition2.and("sfdj.type = 'SALES_IN'");
			condition2.andEQ("sej.party_id", disId);
		} else {
			if ("IN".equals(flagIn)) {
				condition2.and("sfdj.type = 'SALES_IN'");
			} else {
				condition2.and("sfdj.type = 'SALES_OUT'");
			}
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
		;
		List<Map<String, String>> type = getType();

		for (int i = 0; i < type.size(); i++) {
			String productId = (String) type.get(i).get("product_id");
			query.select(
					"sum(case when QUERYFROM.product_id = '" + productId + "' then QUERYFROM.real_quantity else 0 end)",
					'"' + productId + "_a" + '"');
			query.select(
					"sum(case when QUERYJOIN.pro_id = '" + productId + "' then QUERYJOIN.expected_quantity else 0 end)",
					'"' + productId + "_e" + '"');
		}
		query.from(fromQuery, "QUERYFROM")
				.join(Join.RIGHT_OUTER_JOIN, joinQuery, "QUERYJOIN",
						"(QUERYFROM.se_id = QUERYJOIN.internal_party_dim_id AND QUERYFROM.product_id = QUERYJOIN.pro_id AND QUERYJOIN.month_of_year = QUERYFROM.month_of_year AND QUERYJOIN.year_name = QUERYFROM.year_name AND QUERYJOIN.pro_id = QUERYFROM.product_id AND QUERYJOIN.pro_code = QUERYFROM.product_code AND QUERYFROM.se_party = QUERYJOIN.sej_party)")
				.groupBy("se_code").groupBy("se_full_name").groupBy("se_group_name");

		OlbiusQuery temp = new OlbiusQuery(getSQLProcessor());
		temp.select("temp.*");
		for (int i = 0; i < type.size(); i++) {
			String productId = (String) type.get(i).get("product_id");
			temp.select("CASE WHEN \"" + productId + "_a\" > 0 AND \"" + productId + "_e\" > 0 THEN \"" + productId + "_a\" / \"" + productId + "_e\" * 100 ELSE 0 END", "\"" + productId + "_p\"");
		}
		temp.from(query, "temp");

		return temp;
	}

	@Override
	protected OlbiusQuery getQuery() {
		if (query == null) {
			query = initQuery();
		}
		return query;
	}
}
