package com.olbius.basesales.report;

import java.math.BigDecimal;
import org.ofbiz.base.util.UtilValidate;
import com.olbius.bi.olap.grid.OlapGridBuilder;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class EvaluateEffectiveSalesImpl extends OlapGridBuilder {

	public static final String YEARR = "YEARR";
	public static final String ORG = "ORG";

	private OlbiusQuery query;
	private OlbiusQuery fromQuery;
	private OlbiusQuery joinQuery;
	private OlbiusQuery dummyQuery;
	
	public EvaluateEffectiveSalesImpl() {
		super();
		addDataField("stt");
		addDataField("productCode",  "pro_code");
		addDataField("productName", "pro_name");
		addDataField("jan", "jan", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("janr", "janr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("janp", "janp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("feb", "feb", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("febr", "febr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("febp", "febp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("mar", "mar", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("marr", "marr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("marp", "marp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("apr", "apr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("aprr", "aprr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("aprp", "aprp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("may", "may", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("mayr", "mayr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("mayp", "mayp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("jun", "jun", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("junr", "junr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("junp", "junp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("jul", "jul", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("julr", "julr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("julp", "julp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("aug", "aug", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("augr", "augr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("augp", "augp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("sep", "sep", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("sepr", "sepr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("sepp", "sepp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("oct", "oct", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("octr", "octr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("octp", "octp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("nov", "nov", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("novr", "novr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("novp", "novp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("dec", "dec", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("decr", "decr", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
		addDataField("decp", "decp", new ReturnResultCallback<Object>() { 
			@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
				return (BigDecimal) object;}else{return "-";}}
		});
	}
	
	private void initQuery() {
		String yearr = (String) getParameter(YEARR);
		String organization = (String) getParameter(ORG);
		
		int salesYear = Integer.parseInt(yearr);
		
		query = new OlbiusQuery(getSQLProcessor());
		fromQuery = new OlbiusQuery(getSQLProcessor());
		joinQuery = new OlbiusQuery(getSQLProcessor());
		dummyQuery = new OlbiusQuery(getSQLProcessor());

		Condition condition = new Condition();
		condition.and("pd.product_id is NOT NULL").and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'").and("sales_order_fact.order_status = 'ORDER_COMPLETED'");
		condition.andEQ("organ.party_id", organization);
		condition.andEQ("dd.year_name", salesYear);

		Condition condition2 = new Condition();
		condition2.and("sfdj.sales_forecast_id is NOT NULL").andEQ("ddj.year_name", salesYear).and("sfdj.type = 'SALES_FORECAST'");
		
		fromQuery.distinct().select("pd.product_id")
		.select("pd.product_code").select("pd.internal_name").select("sales_order_fact.product_dim_id")
		.select("SUM(sales_order_fact.quantity)", "real_quantity").select("dd.month_of_year").select("dd.year_name")
		.from("sales_order_fact")
		.join(Join.RIGHT_OUTER_JOIN, "product_dimension", "pd", "sales_order_fact.product_dim_id = pd.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = sales_order_fact.party_from_dim_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sales_order_fact.order_date_dim_id")
		.where(condition)
		.groupBy("pd.dimension_id").groupBy("dd.dimension_id").groupBy("sales_order_fact.product_dim_id");
		
		joinQuery.select("sfdj.product_dim_id", "pro_dim").select("sfdj.quantity", "expected_quantity").select("sfdj.custom_time_period_id")
		.select("ddj.month_of_year").select("ddj.year_name").select("pdj.product_id", "pro_id").select("pdj.product_code", "pro_code").select("pdj.internal_name", "pro_name")
		.from("sales_forecast_dimension", "sfdj")
		.join(Join.INNER_JOIN, "date_dimension", "ddj", "ddj.dimension_id = sfdj.from_date_dim_id")
		.join(Join.INNER_JOIN, "product_dimension", "pdj", "pdj.dimension_id = sfdj.product_dim_id")
		.where(condition2)
		.orderBy("ddj.month_of_year", OlbiusQuery.ASC);
		
		dummyQuery.select("QUERYJOIN.pro_id").select("QUERYJOIN.pro_code").select("QUERYJOIN.pro_name")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 1 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "jan").select("SUM(CASE WHEN QUERYFROM.month_of_year = 1 THEN QUERYFROM.real_quantity ELSE 0 END)", "janr")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 2 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "feb").select("SUM(CASE WHEN QUERYFROM.month_of_year = 2 THEN QUERYFROM.real_quantity ELSE 0 END)", "febr")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 3 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "mar").select("SUM(CASE WHEN QUERYFROM.month_of_year = 3 THEN QUERYFROM.real_quantity ELSE 0 END)", "marr")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 4 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "apr").select("SUM(CASE WHEN QUERYFROM.month_of_year = 4 THEN QUERYFROM.real_quantity ELSE 0 END)", "aprr")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 5 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "may").select("SUM(CASE WHEN QUERYFROM.month_of_year = 5 THEN QUERYFROM.real_quantity ELSE 0 END)", "mayr")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 6 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "jun").select("SUM(CASE WHEN QUERYFROM.month_of_year = 6 THEN QUERYFROM.real_quantity ELSE 0 END)", "junr")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 7 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "jul").select("SUM(CASE WHEN QUERYFROM.month_of_year = 7 THEN QUERYFROM.real_quantity ELSE 0 END)", "julr")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 8 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "aug").select("SUM(CASE WHEN QUERYFROM.month_of_year = 8 THEN QUERYFROM.real_quantity ELSE 0 END)", "augr")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 9 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "sep").select("SUM(CASE WHEN QUERYFROM.month_of_year = 9 THEN QUERYFROM.real_quantity ELSE 0 END)", "sepr")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 10 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "oct").select("SUM(CASE WHEN QUERYFROM.month_of_year = 10 THEN QUERYFROM.real_quantity ELSE 0 END)", "octr")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 11 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "nov").select("SUM(CASE WHEN QUERYFROM.month_of_year = 11 THEN QUERYFROM.real_quantity ELSE 0 END)", "novr")
		.select("SUM(CASE WHEN QUERYJOIN.month_of_year = 12 THEN QUERYJOIN.expected_quantity ELSE 0 END)", "dec").select("SUM(CASE WHEN QUERYFROM.month_of_year = 12 THEN QUERYFROM.real_quantity ELSE 0 END)", "decr")
		.from(fromQuery, "QUERYFROM")
		.join(Join.RIGHT_OUTER_JOIN, joinQuery, "QUERYJOIN", "(QUERYJOIN.pro_dim = QUERYFROM.product_dim_id AND QUERYJOIN.month_of_year = QUERYFROM.month_of_year AND QUERYJOIN.year_name = QUERYFROM.year_name AND QUERYJOIN.pro_id = QUERYFROM.product_id AND QUERYJOIN.pro_code = QUERYFROM.product_code)")
		.groupBy("QUERYJOIN.pro_id").groupBy("QUERYJOIN.pro_code").groupBy("QUERYJOIN.pro_name");
		
		query.select("temp.*")
		.select("CASE WHEN jan > 0 AND janr > 0 THEN janr / jan * 100 ELSE 0 END", "janp")
		.select("CASE WHEN feb > 0 AND febr > 0 THEN febr / feb * 100 ELSE 0 END", "febp")
		.select("CASE WHEN mar > 0 AND marr > 0 THEN marr / mar * 100 ELSE 0 END", "marp")
		.select("CASE WHEN apr > 0 AND aprr > 0 THEN aprr / apr * 100 ELSE 0 END", "aprp")
		.select("CASE WHEN may > 0 AND mayr > 0 THEN mayr / may * 100 ELSE 0 END", "mayp")
		.select("CASE WHEN jun > 0 AND junr > 0 THEN junr / jun * 100 ELSE 0 END", "junp")
		.select("CASE WHEN jul > 0 AND julr > 0 THEN julr / jul * 100 ELSE 0 END", "julp")
		.select("CASE WHEN aug > 0 AND augr > 0 THEN augr / aug * 100 ELSE 0 END", "augp")
		.select("CASE WHEN sep > 0 AND sepr > 0 THEN sepr / sep * 100 ELSE 0 END", "sepp")
		.select("CASE WHEN oct > 0 AND octr > 0 THEN octr / oct * 100 ELSE 0 END", "octp")
		.select("CASE WHEN nov > 0 AND novr > 0 THEN novr / nov * 100 ELSE 0 END", "novp")
		.select("CASE WHEN dec > 0 AND decr > 0 THEN decr / dec * 100 ELSE 0 END", "decp")
		.from(dummyQuery, "temp");
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
}
