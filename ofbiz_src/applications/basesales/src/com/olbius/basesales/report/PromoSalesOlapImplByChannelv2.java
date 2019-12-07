package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.ofbiz.base.util.Debug;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.Query;

public class PromoSalesOlapImplByChannelv2 extends AbstractOlap {

	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String STORE_CHANNEL = "STORE_CHANNEL";

	private final ResultReport test2 = new ResultReport();

	private Query query;
	
//	public PromoSalesOlapImplByChannelv2() {
//		OlapGrid grid = new OlapGrid(this, test2);
//		setOlapResult(grid);
//	}
	
	private void initQuery() {
		
//		String dateType = (String) getParameter(DATE_TYPE);
		String storeChannelId = (String) getParameter(STORE_CHANNEL);

//		dateType = getDateType(dateType);
		
//		test2.setDatetype(dateType);
		
		

		query = new Query(getSQLProcessor());

		query.setFrom("sales_order_fact", null);
//		query.addSelect("date_dimension.".concat(dateType));
		query.addSelect("product_promo_dimension.promo_name");
		query.addSelect("party_dimension.description");
		query.addSelect("product_dimension.product_name");
		query.addSelect("sum(sales_order_fact.quantity)", "quantity1");

		query.addInnerJoin("date_dimension", null, "sales_order_fact.order_date_dim_id = date_dimension.dimension_id");
		query.addInnerJoin("product_dimension", null, "sales_order_fact.product_dim_id = product_dimension.dimension_id");
		query.addInnerJoin("product_promo_dimension", null, "sales_order_fact.promo_dim_id = product_promo_dimension.dimension_id");
		query.addInnerJoin("party_dimension", null, "sales_order_fact.party_to_dim_id = party_dimension.dimension_id");
		
		query.addConditionBetweenObj("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		query.addConditionEQ("sales_order_fact.sales_method_channel_enum_id", storeChannelId, storeChannelId != null);
		query.addCondition("product_promo_dimension.product_promo_id is not null");
		
		query.addGroupBy("product_dimension.dimension_id");
		query.addGroupBy("party_dimension.dimension_id");
		query.addGroupBy("product_promo_dimension.dimension_id");
	}
	
	@Override
	protected Query getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}


	public class ResultReport extends ReturnResultGrid {

//		private String dateType;
//
//		public void setDatetype(String dateType) {
//			this.dateType = dateType;
//		}
		
		public ResultReport() {
			test2.addDataField("promoName");
			test2.addDataField("party");
			test2.addDataField("productName");
			test2.addDataField("quantity1");
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				String partyResult = result.getString("description");
				String promoResult = result.getString("promo_name");
				String productNameResult = result.getString("product_name");
				BigDecimal quantityResult = result.getBigDecimal("quantity1");
//				map.put("date", result.getString(dateType));
					map.put("promoName", promoResult);
					map.put("party", partyResult);
					map.put("productName", productNameResult);
					map.put("quantity1", quantityResult);
			} catch (Exception e) {
				Debug.logError(e.getMessage(), ResultReport.class.getName());
			}
			return map;
		}

	}
}
