package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalesProductOlapImplByRegionv2 extends OlbiusBuilder {

	public static final String REGION = "REGION";
	public static final String ORDER_STATUS = "ORDER_STATUS";
	public static final String TOTAL = "TOTAL";
	
	public SalesProductOlapImplByRegionv2(Delegator delegator) {
		super(delegator);
	}
	
	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("productId", "product_code");
		addDataField("productName", "internal_name");
		addDataField("Quantity", "Quantity");
		addDataField("Total", "Total");
		addDataField("unit", "unit");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			setSeriesDefaultName((String) getParameter(TOTAL));
			addXAxis("product_code");
			addYAxis("Total");
		}
	}
	
	private OlbiusQuery query2;
	
	public void initQuery() {
		String region = (String) getParameter(REGION);
		String status = (String) getParameter(ORDER_STATUS);
		
		Condition cond =  new Condition();

		query2 = new OlbiusQuery(getSQLProcessor());
		query2.select("pd.product_code")
		.select("pd.product_id")
		.select("pd.internal_name")
		.select("sum(sof.quantity)", "Quantity")
		.select("sum(sof.total)","Total")
		.select("sof.party_from_dim_id")
		.select("pgd.party_id")
		.select("sof.quantity_uom", "unit")
		.from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "product_dimension", "pd", "sof.product_dim_id = pd.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "pgd", "sof.party_from_dim_id = pgd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "sof.order_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sof.promo_dim_id")
		.where(cond)
		.groupBy("pd.product_code")
		.groupBy("pd.product_id")
		.groupBy("pd.internal_name")
		.groupBy("sof.party_from_dim_id")
		.groupBy("pgd.party_id")
		.groupBy("sof.quantity_uom", "unit");
		
		cond.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		cond.and(Condition.makeEQ("pgd.party_id", region));
		cond.and(Condition.makeEQ("sof.order_status", status, status != null));
		cond.and(Condition.make("product_promo_dimension.product_promo_id IS NULL"));
//		cond.and(Condition.make("sof.order_item_status <> 'ITEM_CANCELLED'"));
		if("ORDER_CANCELLED".equals(status)){
			cond.and(Condition.makeEQ("sof.order_status", status, status != null));
			cond.and(Condition.make("sof.order_item_status = 'ITEM_CANCELLED'"));
		} else {
			cond.and(Condition.make("sof.order_item_status <> 'ITEM_CANCELLED'"));
		}
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query2 == null) {
			initQuery();
		}
		return query2;
	}
	
	public class Test implements OlapResultQueryInterface {

		@Override
		public Object resultQuery(OlapQuery query2) {
			Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String, Object>>();
			try{
				ResultSet resultSet = query2.getResultSet();
				while (resultSet.next()) {
					try {
						String product = resultSet.getString("internal_name");
						if(tmp.get(product)==null) {
						    tmp.put(product, new HashMap<String, Object>());
						   }
						tmp.get(product).put("Quantity", resultSet.getBigDecimal("Quantity"));
					} catch (Exception e) {
						Debug.logError(e.getMessage(), Test.class.getName());
					}
				}
			} catch(Exception e) {
				
			}
			return tmp;
		}
	}

	public class Test3 extends AbstractOlapChart {
		public Test3(OlapInterface olap, OlapResultQueryInterface query2) {
			super(olap, query2);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;
			for(String key : map.keySet()) {
				if(yAxis.get(key) == null) {
					yAxis.put(key, new ArrayList<Object>());
				}
				for(String s : map.get(key).keySet()) {
					yAxis.get(key).add(map.get(key).get(s));
					if(!xAxis.contains(s)) {
						xAxis.add(s);
					}
				}
			}
		}
	}
	
	public class PieChart implements OlapResultQueryInterface{
		@Override
		public Object resultQuery(OlapQuery query2) {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				ResultSet resultSet = query2.getResultSet();
				if(resultSet.next()) {
					map.put("Quantity", resultSet.getBigDecimal("Quantity"));
				}
				return map;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public class PieResult extends AbstractOlapChart {

		public PieResult(OlapInterface olap, OlapResultQueryInterface query2) {
			super(olap, query2);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Object> map = ( Map<String, Object>) object;
			for(String s : map.keySet()) {
				List<Object> list = new ArrayList<Object>();
				list.add(map.get(s));
				yAxis.put(s, list);
				xAxis.add(s);
			}
		}
		
	}
	
	public class TestColumn implements OlapResultQueryInterface {

		@Override
		public Object resultQuery(OlapQuery query2) {
			String total0 = (String) getParameter(TOTAL);
			
			Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String, Object>>();
			tmp.put(total0, new HashMap<String, Object>());
			try{
				ResultSet resultSet = query2.getResultSet();
				while (resultSet.next()) {
					int nu = 0;
					try {
						BigDecimal a = (BigDecimal) resultSet.getBigDecimal("Total");
						if(UtilValidate.isNotEmpty(a)){
							tmp.get(total0).put(resultSet.getString("product_code"), a);
						}else{
							tmp.get(total0).put(resultSet.getString("product_code"), nu);
						}
					} catch (Exception e) {
						Debug.logError(e.getMessage(), Test.class.getName());
					}
				}
			} catch(Exception e) {
				
			}
			return tmp;
		}
		
	}

	public class Test3Column extends AbstractOlapChart {

		public Test3Column(OlapInterface olap, OlapResultQueryInterface query2) {
			super(olap, query2);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;
			for(String key : map.keySet()) {
				if(yAxis.get(key) == null) {
					yAxis.put(key, new ArrayList<Object>());
				}
				for(String s : map.get(key).keySet()) {
					yAxis.get(key).add(map.get(key).get(s));
					if(!xAxis.contains(s)) {
						xAxis.add(s);
					}
				}
			}
		}
		
	}

	public class ProReg extends ReturnResultGrid {
		public ProReg() {
			setDatetype(dateType);
			addDataField("productId");
			addDataField("idForDatNV");
			addDataField("productName");
			addDataField("Quantity");
			addDataField("Total");
		}

		private String dateType;

		public void setDatetype(String dateType) {
			this.dateType = dateType;
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("productId", result.getString("product_code"));
				map.put("idForDatNV", result.getString("product_id"));
				map.put("productName", result.getString("internal_name"));
				map.put("Total", result.getBigDecimal("Total"));
				map.put("Quantity", result.getBigDecimal("Quantity"));
			} catch (Exception e) {
				Debug.logError(e.getMessage(), ProReg.class.getName());
			}
			return map;
		}
	}
}