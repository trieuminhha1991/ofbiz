package com.olbius.salesmtl.report;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class EvaluateAgency extends OlbiusBuilder{
	public static final String AGENCY_ID = "AGENCY_ID";
	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String SERIES = "SERIES";
	
	public EvaluateAgency(Delegator delegator) {
		super(delegator);
	}
	
	private OlbiusQuery query;
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			addSeries("party_id");
			addXAxis("year_and_month");
			String series = (String) getParameter(SERIES);
			if("series1".equals(series)){
				addYAxis("order_volume");
			} else {
				addYAxis("value_sales");
			}
		}
	}
	
	private void initQuery() {
		String agencyId = (String) getParameter(AGENCY_ID);
		String series = (String) getParameter(SERIES);
		Condition condition = new Condition();
		query = new OlbiusQuery(getSQLProcessor());
		
		condition.and("ppd.product_promo_id is null").andEQ("pd.party_id", agencyId).andBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		
		query.select("pd.party_id").select("count(distinct order_id)", "order_volume", "series1".equals(series))
		.select("sum(sof.total)", "value_sales", "series2".equals(series)).select("dd.year_and_month")
		.from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = sof.party_to_dim_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", "ppd", "ppd.dimension_id = sof.discount_dim_id")
		.where(condition)
		.groupBy("pd.party_id").groupBy("dd.year_and_month").orderBy("dd.year_and_month");
	}
}
