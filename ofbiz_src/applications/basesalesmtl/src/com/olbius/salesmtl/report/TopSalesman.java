package com.olbius.salesmtl.report;

import java.util.List;
import java.util.Locale;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class TopSalesman extends OlbiusBuilder{
	public static final String ORG = "ORG";
	public static final String TURNOVER = "TURNOVER";
	public static final String CHART = "CHART";
	public static final String SALESMAN = "SALESMAN";
	public static final String MONTHH = "MONTHH";
	public static final String YEARR = "YEARR";
	public static final String LIMIT_SALESMAN = "LIMIT_SALESMAN";
	public static final String FLAG_TYPE_PERSON = "FLAG_TYPE_PERSON";
	
	public TopSalesman(Delegator delegator) {
		super(delegator);
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			String turnoverString = (String) getParameter(TURNOVER);
			Boolean flagPersonType = (Boolean) getParameter(FLAG_TYPE_PERSON);
			if(flagPersonType != null && flagPersonType == true){
				addXAxis("customer_code");
			} else {
				addXAxis("salesman_code");
			}
			addYAxis("total_value");
			setSeriesDefaultName(turnoverString);
		}
	}
	
	private OlbiusQuery query;
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		Condition condition = new Condition();
		List<Object> salesmanList = (List<Object>) getParameter(SALESMAN);
		query = new OlbiusQuery(getSQLProcessor());
		String getYear = (String) getParameter(YEARR);
		String getMonth = (String) getParameter(MONTHH);
		int queryYear = Integer.parseInt(getYear);
		int queryMonth = Integer.parseInt(getMonth);
		int limitSalesman = Integer.parseInt((String) getParameter(LIMIT_SALESMAN));
		Boolean flagPersonType2 = (Boolean) getParameter(FLAG_TYPE_PERSON);
		
		condition.andEQ("dd.month_of_year", queryMonth).andEQ("dd.year_name", queryYear)
		.andEQ("sof.order_status", "ORDER_COMPLETED").andEQ("sof.order_item_status", "ITEM_COMPLETED").and("ppd.product_promo_id is null")
		.andIn("pd.party_id", salesmanList, (UtilValidate.isNotEmpty(salesmanList) && salesmanList.toArray().length >= 1)).and("pd.party_id is not null");
		
		query.select("sum(sof.total)", "total_value").select("pd.party_code", "salesman_code", flagPersonType2 == null).select("pd2.party_code", "customer_code", flagPersonType2 != null && flagPersonType2 == true)
		.from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = sof.sale_executive_party_dim_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", "ppd", "ppd.dimension_id = sof.promo_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = sof.party_to_dim_id", flagPersonType2 != null && flagPersonType2 == true)
		.where(condition)
		.groupBy("salesman_code", flagPersonType2 == null)
		.groupBy("customer_code", flagPersonType2 != null && flagPersonType2 == true)
		.orderBy("total_value", OlbiusQuery.DESC);
		if(UtilValidate.isNotEmpty(limitSalesman) && limitSalesman != 1000){
			query.limit(limitSalesman);
		}
	}
}
