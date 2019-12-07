package com.olbius.basesales.report;

import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ProductPromoExtImpl extends OlbiusBuilder {
	public static final String ORG = "ORG";
	public static final String PARTY = "PARTY";
	public static final String FLAGSM = "FLAGSM";
	public static final String FLAGPROMO = "FLAGPROMO";
	
	public ProductPromoExtImpl(Delegator delegator) {
		super(delegator);
	}

	private OlbiusQuery query2;
	private OlbiusQuery queryJoin;
	
	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("product_promo_id",  "product_promo_id");
		addDataField("product_promo_name", "promo_name");
		addDataField("user_limit", "use_limit_per_promotion");
		addDataField("count_cus", "count_cus", new ReturnResultCallback<Object>() {
			@Override
			public Object get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (Long) object; }else{ return "-"; }
			}
		});
		addDataField("count_pass", "count_pass", new ReturnResultCallback<Object>() {
			@Override
			public Object get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (Long) object; }else{ return "-"; }
			}
		});
	}
	
	@Override
	public void prepareResultChart() {
//		if(getOlapResult() instanceof OlapColumnChart) {
//			addSeries("store_name");
//			addXAxis("product_code");
//			addYAxis("Total");
//		}
		 
		if(getOlapResult() instanceof OlapPieChart) {
			addXAxis("description");
			addYAxis("Total");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void initQuery() {
		String organ = (String) getParameter(ORG);
		String flagPromo = (String) getParameter(FLAGPROMO);
		List<Object> salesmanList = (List<Object>) getParameter(PARTY);
		
		queryJoin = new OlbiusQuery(getSQLProcessor());
		query2 = new OlbiusQuery(getSQLProcessor());

		Condition condition = new Condition();
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		condition.and(Condition.makeEQ("product_promo_ext_dimension.organization_party_id", organ));
		if("DISPLAY".equals(flagPromo)){
			condition.and("product_promo_type_id = 'PROMO_EXHIBITION'");
		} else if("ACCUMULATION".equals(flagPromo)){
			condition.and("product_promo_type_id = 'PROMO_ACCUMULATION'");
		}
		query2.from("product_promo_ext_dimension")
		.select("promo_name, product_promo_id, use_limit_per_promotion, count (distinct party_id) as count_cus, count(result_enum_id) as count_pass")   
		.join(Join.INNER_JOIN,"date_dimension","product_promo_ext_dimension.from_date_dim = date_dimension.dimension_id")
		.where(condition);
		query2.groupBy("product_promo_ext_dimension.product_promo_id")
		.groupBy("product_promo_ext_dimension.promo_name")
		.groupBy("product_promo_ext_dimension.use_limit_per_promotion");
		
		
//		select count(distinct customer_relationship.person_dim_id), group_dim_id
//		from customer_relationship
//		GROUP by group_dim_id
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query2 == null) {
			initQuery();
		}
		return query2;
	}
	
	//create column chart
//	public class CSColumnTest implements OlapResultQueryInterface {
//		@Override
//		public Object resultQuery(OlapQuery queryChart) {
//			Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String, Object>>();
//			XAxis axis = new XAxis();
//			axis.setData(tmp);
//			try{
//				ResultSet resultSet = queryChart.getResultSet();
//				while (resultSet.next()) {
//					int zero = 0;
//					try {
//						String store =  resultSet.getString("product_store_id");
//						String product = resultSet.getString("product_code");
//						if(tmp.get(store) == null){
//							tmp.put(store, new HashMap<String, Object>());
//						}
//						axis.getXAxis().add(product);
//						BigDecimal valueResult = resultSet.getBigDecimal("Total");
//						if(UtilValidate.isNotEmpty(valueResult)){
//							tmp.get(store).put(product, valueResult);
//						}else{
//							tmp.get(store).put(product, zero);
//						}
//					} catch (Exception e) {
//						Debug.logError(e.getMessage(), CSColumnTest.class.getName());
//					}
//				}
//			} catch(Exception e) {
//			
//			}
//			return axis;
//		}
//	}
	
	//create pie chart
//	public class EvaluateTurnoverPieChart implements OlapResultQueryInterface {
//		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
//		
//		@Override
//		public Object resultQuery(OlapQuery queryChart) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			try {
//				ResultSet resultSet = queryChart.getResultSet();
//				while(resultSet.next()) {
//					String productCode = resultSet.getString("product_code");
//					String store = resultSet.getString("product_store_id");
//					String description = productCode + "-"+ store;
//					BigDecimal valueResult = resultSet.getBigDecimal("Total");
//						if(map.get(description)==null) {
//							map.put(description, new HashMap<String, Object>());
//						}
//						map.put(description, valueResult);
//				}
//			} catch (GenericEntityException | SQLException e) {
//				e.printStackTrace();
//			}
//			
//			return map;
//		}
//	}
	
//	public class EvaluateTurnoverPieChart2 extends AbstractOlapChart {
//
//		public EvaluateTurnoverPieChart2(OlapInterface olap, OlapResultQueryInterface queryChart) {
//			super(olap, queryChart);
//		}
//
//		@Override
//		public boolean isChart() {
//			return true;
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		protected void result(Object object) {
//			Map<String, Object> tmp = ( Map<String, Object>) object;
//			for(String s : tmp.keySet()) {
//				List<Object> list = new ArrayList<Object>();
//				list.add(tmp.get(s));
//				yAxis.put(s, list);
//				xAxis.add(s);
//			}
//		}
//	}

}
