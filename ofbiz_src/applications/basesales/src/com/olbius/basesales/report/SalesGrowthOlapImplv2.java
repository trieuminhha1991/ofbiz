package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalesGrowthOlapImplv2 extends AbstractOlap {

	public static final String QUANTITY1 = "QUANTITY1";
	public static final String QUANTITY2 = "QUANTITY2";
	public static final String ALL = "ALL";
	public static final String PRODUCT_STORE = "PRODUCT_STORE";
	public static final String CHANNEL = "CHANNEL";
	public static final String ORGANIZATION = "ORGANIZATION";
	public static final String MONTH_F = "MONTH_F";
	public static final String MONTH_S = "MONTH_S";
	public static final String QUARTER_F = "QUARTER_F";
	public static final String QUARTER_S = "QUARTER_S";
	public static final String YEAR_F = "YEAR_F";
	public static final String YEAR_S = "YEAR_S";
	public static final String TYPEE = "TYPEE";
	public static final String TYPE_FILTER = "TYPE_FILTER";

	private OlbiusQuery newQuery;
	private OlbiusQuery fromQuery;
	private OlbiusQuery joinQuery;

	private void initQuery() {
		String productStoreId = (String) getParameter(PRODUCT_STORE);
		String organ = (String) getParameter(ORGANIZATION);
		String typeOfDate = (String) getParameter(TYPEE);
		String typeOfFilter = (String) getParameter(TYPE_FILTER);
		String monthNorStr = (String) getParameter(MONTH_F);
		String monthVSStr = (String) getParameter(MONTH_S);
		String quarterNorStr = (String) getParameter(QUARTER_F);
		String quarterVSStr = (String) getParameter(QUARTER_S);
		String yearNorStr = (String) getParameter(YEAR_F);
		String yearVSStr = (String) getParameter(YEAR_S);

		Integer monthNor = 0;
		Integer monthVS = 0;
		Integer quarterNor = 0;
		Integer quarterVS = 0;
		Integer yearNor = 0;
		Integer yearVS = 0;

		if (UtilValidate.isNotEmpty(monthNorStr)) {
			monthNor = Integer.parseInt(monthNorStr);
		}
		if (UtilValidate.isNotEmpty(monthVSStr)) {
			monthVS = Integer.parseInt(monthVSStr);
		}
		if (UtilValidate.isNotEmpty(quarterNorStr)) {
			quarterNor = Integer.parseInt(quarterNorStr);
		}
		if (UtilValidate.isNotEmpty(quarterVSStr)) {
			quarterVS = Integer.parseInt(quarterVSStr);
		}
		if (UtilValidate.isNotEmpty(yearNorStr)) {
			yearNor = Integer.parseInt(yearNorStr);
		}
		if (UtilValidate.isNotEmpty(yearVSStr)) {
			yearVS = Integer.parseInt(yearVSStr);
		}

		Condition conditionFrom = new Condition();
		Condition conditionJoin = new Condition();

		newQuery = new OlbiusQuery(getSQLProcessor());
		fromQuery = new OlbiusQuery(getSQLProcessor());
		joinQuery = new OlbiusQuery(getSQLProcessor());

		fromQuery.select("pd1.product_id").select("pd1.product_code").select("pd1.internal_name")
				.select("SUM(sales_order_fact.quantity)", "volume1").select("pgd1.party_id", "organ1")
				.select("sales_order_fact.product_store_dim_id", "store1",
						"STORE".equals(typeOfFilter) && !"all".equals(productStoreId))
				.from("sales_order_fact")
				.join(Join.INNER_JOIN, "product_dimension", "pd1", "pd1.dimension_id = sales_order_fact.product_dim_id")
				.join(Join.INNER_JOIN, "date_dimension", "dd1", "dd1.dimension_id = sales_order_fact.order_date_dim_id")
				.join(Join.INNER_JOIN, "party_dimension", "pgd1",
						"pgd1.dimension_id = sales_order_fact.party_from_dim_id")
				.join(Join.INNER_JOIN, "product_promo_dimension",
						"product_promo_dimension.dimension_id = sales_order_fact.promo_dim_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "ppd2",
						"ppd2.dimension_id = sales_order_fact.discount_dim_id")
				.where(conditionFrom).groupBy("pd1.dimension_id")
				.groupBy("store1", "STORE".equals(typeOfFilter) && !"all".equals(productStoreId)).groupBy("organ1");
		conditionFrom.andEQ("dd1.month_of_year", monthNor, monthNor != 0 && "MONTH_YEAR".equals(typeOfDate))
				.andEQ("dd1.year_name", yearNor, yearNor != 0)
				.andEQ("dd1.month_of_year", monthNor, monthNor != 0 && "MONTH".equals(typeOfDate))
				.andEQ("dd1.quarter_of_year", quarterNor, quarterNor != 0 && "QUARTER_YEAR".equals(typeOfDate))
				.andEQ("dd1.quarter_of_year", quarterNor, quarterNor != 0 && "QUARTER".equals(typeOfDate))
				.and("sales_order_fact.order_item_status <>'ITEM_CANCELLED'")
				.and("sales_order_fact.order_status = 'ORDER_COMPLETED'")
				.and("product_promo_dimension.product_promo_id IS NULL").and("ppd2.product_promo_id IS NULL");

		joinQuery.select("pd2.product_id").select("pd2.product_code").select("pgd2.party_id", "organ2")
				.select("pd2.internal_name").select("SUM(sof2.quantity)", "volume2")
				.select("sof2.product_store_dim_id", "store2",
						"STORE".equals(typeOfFilter) && !"all".equals(productStoreId))
				.from("sales_order_fact", "sof2")
				.join(Join.INNER_JOIN, "product_dimension", "pd2", "pd2.dimension_id = sof2.product_dim_id")
				.join(Join.INNER_JOIN, "date_dimension", "dd2", "dd2.dimension_id = sof2.order_date_dim_id")
				.join(Join.INNER_JOIN, "party_dimension", "pgd2", "pgd2.dimension_id = sof2.party_from_dim_id")
				.join(Join.INNER_JOIN, "product_promo_dimension",
						"product_promo_dimension.dimension_id = sof2.promo_dim_id")
				.join(Join.INNER_JOIN, "product_promo_dimension", "ppd2", "ppd2.dimension_id = sof2.discount_dim_id")
				.where(conditionJoin).groupBy("pd2.dimension_id")
				.groupBy("store2", "STORE".equals(typeOfFilter) && !"all".equals(productStoreId)).groupBy("organ2");
		conditionJoin.andEQ("dd2.month_of_year", monthVS, monthVS != 0 && "MONTH_YEAR".equals(typeOfDate))
				.andEQ("dd2.year_name", yearNor, yearNor != 0 && "MONTH_YEAR".equals(typeOfDate))
				.andEQ("dd2.month_of_year", monthVS, monthVS != 0 && "MONTH".equals(typeOfDate))
				.andEQ("dd2.year_name", yearVS, yearVS != 0 && "MONTH".equals(typeOfDate))
				.andEQ("dd2.quarter_of_year", quarterVS, quarterVS != 0 && "QUARTER_YEAR".equals(typeOfDate))
				.andEQ("dd2.year_name", yearNor, yearNor != 0 && "QUARTER_YEAR".equals(typeOfDate))
				.andEQ("dd2.quarter_of_year", quarterVS, quarterVS != 0 && "QUARTER".equals(typeOfDate))
				.andEQ("dd2.year_name", yearVS, yearVS != 0 && "QUARTER".equals(typeOfDate))
				.andEQ("dd2.year_name", yearVS, yearVS != 0 && "YEAR".equals(typeOfDate))
				.and("sof2.order_status = 'ORDER_COMPLETED'").and("sof2.order_item_status <>'ITEM_CANCELLED'")
				.and("product_promo_dimension.product_promo_id IS NULL").and("ppd2.product_promo_id IS NULL");

		newQuery.select("tmp.product_code").select("tmp.internal_name").select("tmp.volume1").select("join1.volume2")
				.select("psd.store_name", "STORE".equals(typeOfFilter) && !"all".equals(productStoreId))
				.from(fromQuery, "tmp");
		if ("STORE".equals(typeOfFilter) && "all".equals(productStoreId)) {
			newQuery.join(Join.INNER_JOIN, joinQuery, "join1",
					"tmp.product_code = join1.product_code and tmp.product_id = join1.product_id and tmp.organ1 = join1.organ2",
					"STORE".equals(typeOfFilter) && !"all".equals(productStoreId));
		} else {
			newQuery.join(Join.INNER_JOIN, joinQuery, "join1",
					"tmp.product_code = join1.product_code and tmp.product_id = join1.product_id and tmp.store1 = join1.store2 and tmp.organ1 = join1.organ2");
		}
		newQuery.join(Join.INNER_JOIN, "product_store_dimension", "psd", "tmp.store1 = psd.dimension_id",
				"STORE".equals(typeOfFilter) && !"all".equals(productStoreId));
		newQuery.where(Condition
				.makeEQ("psd.product_store_id", productStoreId,
						"STORE".equals(typeOfFilter) && !"all".equals(productStoreId))
				.and(Condition.makeEQ("tmp.organ1", organ)));
	}

	@Override
	protected OlbiusQuery getQuery() {
		if (newQuery == null) {
			initQuery();
		}
		return newQuery;
	}

	public class Test implements OlapResultQueryInterface {
		@Override
		public Object resultQuery(OlapQuery newQuery) {
			Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String, Object>>();
			String quantityO = (String) getParameter(QUANTITY1);
			String quantityT = (String) getParameter(QUANTITY2);
			tmp.put(quantityO, new HashMap<String, Object>());
			tmp.put(quantityT, new HashMap<String, Object>());
			try {
				ResultSet resultSet = newQuery.getResultSet();
				while (resultSet.next()) {
					int nu = 0;
					try {
						BigDecimal a = (BigDecimal) resultSet.getBigDecimal("volume1");
						BigDecimal b = (BigDecimal) resultSet.getBigDecimal("volume2");
						if (UtilValidate.isNotEmpty(a)) {
							tmp.get(quantityO).put(resultSet.getString("product_code"), a);
						} else {
							tmp.get(quantityO).put(resultSet.getString("product_code"), nu);
						}
						if (UtilValidate.isNotEmpty(b)) {
							tmp.get(quantityT).put(resultSet.getString("product_code"), b);
						} else {
							tmp.get(quantityT).put(resultSet.getString("product_code"), nu);
						}
					} catch (Exception e) {
						Debug.logError(e.getMessage(), Test.class.getName());
					}
				}
			} catch (Exception e) {

			}
			return tmp;
		}
	}

	public class Test3 extends AbstractOlapChart {
		public Test3(OlapInterface olap, OlapResultQueryInterface newQuery) {
			super(olap, newQuery);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;
			for (String key : map.keySet()) {
				if (yAxis.get(key) == null) {
					yAxis.put(key, new ArrayList<Object>());
				}
				for (String s : map.get(key).keySet()) {
					yAxis.get(key).add(map.get(key).get(s));
					if (!xAxis.contains(s)) {
						xAxis.add(s);
					}
				}
			}
		}

	}

	public class ResultGrid extends ReturnResultGrid {
		public ResultGrid() {
			addDataField("productStoreName");
			addDataField("productName");
			addDataField("quantity1");
			addDataField("quantity2");
			addDataField("pvp");
			addDataField("pvs");
			addDataField("stt");
		}

		@SuppressWarnings("unused")
		private String dateType;

		public void setDatetype(String dateType) {
			this.dateType = dateType;
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			String nu = "-";
			try {
				String all = (String) getParameter(ALL);
				String typeOfFilter = (String) getParameter(TYPE_FILTER);
				String productStoreId = (String) getParameter(PRODUCT_STORE);
				if ("STORE".equals(typeOfFilter) && "all".equals(productStoreId)) {
					map.put("productStoreName", all);
				} else {
					map.put("productStoreName", result.getString("store_name"));
				}
				map.put("productName", result.getString("internal_name"));
				BigDecimal a = (BigDecimal) result.getBigDecimal("volume2");
				BigDecimal b = (BigDecimal) result.getBigDecimal("volume1");
				if (UtilValidate.isNotEmpty(a)) {
					map.put("quantity2", a);
				} else {
					map.put("quantity2", nu);
				}
				if (UtilValidate.isNotEmpty(b)) {
					map.put("quantity1", b);
				} else {
					map.put("quantity1", nu);
				}
				BigDecimal c;
				if (a != null && b != null) {
					c = a.divide(b, 4, RoundingMode.HALF_UP);
					c = c.multiply(new BigDecimal(100));
				} else {
					c = new BigDecimal(0);
				}
				map.put("pvp", new DecimalFormat("##0.00").format(c));
				if (a != null && b != null) {
					BigDecimal pvs = a.subtract(b).divide(b, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
					map.put("pvs", new DecimalFormat("##0.00").format(pvs));
				}
			} catch (Exception e) {
				Debug.logError(e.getMessage(), ResultGrid.class.getName());
			}
			return map;
		}

	}
}
