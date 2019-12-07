package com.olbius.basepos.report;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.ReturnResultChartInterface;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class BestSeller extends OlbiusBuilder{
	public BestSeller(Delegator delegator) {
		super(delegator);
	}

	public static String resource = "BasePosUiLabels";
	public static final String SORTT = "SORTT";
	public static final String LIMITT = "LIMITT";
	public static final String ORG = "ORG";
	public static final String STORE = "STORE";
	public static final String TYPE_CHART = "TYPE_CHART";
	
	private OlbiusQuery query;
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			String typeChart = (String) getParameter(TYPE_CHART);
			((ReturnResultChartInterface) getOlapResult().getResultQuery()).setSeriesDefaultName("-");
			addXAxis("product_code");
			if (typeChart.equals("quantity")){
				addYAxis("_quantity");
			} else if (typeChart.equals("price")){
				addYAxis("_ext_price");
			}
		}
	}
	
	private void initQuery() {
		query = OlbiusQuery.make(getSQLProcessor());
		Boolean sort = (Boolean) getParameter(SORTT);
		Long limitt = (Long) getParameter(LIMITT);
		String productStoreId = (String) getParameter(STORE);
		String org = (String) getParameter(ORG);
		String typeChart = (String) getParameter(TYPE_CHART);

		Condition condition = new Condition();
		
		query.from("sales_order_fact");
		query.select("product_dimension.product_code");
		query.select("product_dimension.internal_name");
		query.select("sum(quantity)", "_quantity");
		query.select("sum(total)", "_ext_price");
		
		query.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "currency_dimension", null, "currency_dim_id = currency_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", null, "party_from_dim_id = party_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_store_dimension", null, "product_store_dim_id = product_store_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "enumeration_dimension", null, "sales_method_channel_enum_dim_id = enumeration_dimension.dimension_id");
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
//		condition.andEQ("enumeration_dimension.enum_id", "SMCHANNEL_POS");
		condition.andEQ("sales_order_fact.order_status", "ORDER_COMPLETED");
		condition.and("sales_order_fact.return_id IS NULL");
		condition.andEQ("product_store_dimension.product_store_id", productStoreId, !"all".equals(productStoreId));
		condition.andEQ("party_dimension.party_id", org, org != null);
		query.where(condition);
		
		query.groupBy("product_dimension.product_code");
		query.groupBy("product_dimension.internal_name");
		
		String orderType = "DESC";
		if (sort == true){
			orderType = "ASC";
		}
		
		if (typeChart.equals("quantity")){
			query.orderBy("_quantity", orderType);
		} else if (typeChart.equals("price")){
			query.orderBy("_ext_price", orderType);
		}
		
		query.limit(limitt);
	}
	
	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	}
}
