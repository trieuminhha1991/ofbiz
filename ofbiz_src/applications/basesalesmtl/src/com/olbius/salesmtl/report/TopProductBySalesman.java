package com.olbius.salesmtl.report;

import java.util.List;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class TopProductBySalesman extends OlbiusBuilder{
	public static final String SALESMAN = "SALESMAN";
	public static final String MONTHH = "MONTHH";
	public static final String YEARR = "YEARR";
	
	public TopProductBySalesman(Delegator delegator) {
		super(delegator);
	}
	
	private OlbiusQuery query;
	
	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("product_code", "product_code");
		addDataField("product_name", "product_name");
		addDataField("sales_volume", "sales_volume");
		addDataField("sales_value", "sales_value");
	}
	
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
		
		condition.andEQ("sof.order_status", "ORDER_COMPLETED").and("sof.order_item_status", "ITEM_COMPLETED")
		.and("ppd.product_promo_id is null").andEQ("dd.month_of_year", queryMonth).andEQ("dd.year_name", queryYear).andIn("pd.paty_id", salesmanList);
		
		query.select("prd.product_code").select("prd.product_name").select("sum(sof.quantity)", "sales_volume").select("sum(sof.total)", "sales_value")
		.from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = sof.sale_executive_party_dim_id")
		.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = sof.product_dim_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", "ppd", "ppd.dimension_id = sof.promo_dim_id")
		.groupBy("prd.dimension_id").orderBy("sales_volume", OlbiusQuery.DESC);
	}
}
