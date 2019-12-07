package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.InnerJoin;
import com.olbius.bi.olap.query.join.Join;

public class SalesOlapImplv2 extends AbstractOlap {

	public static final String DATE_TYPE = "DATE_TYPE";
	public static final String PRODUCT_STORE = "PRODUCT_STORE";
	public static final String ORG = "ORG";
	public static final String ALL = "ALL";
	public static final String QUANTITY = "QUANTITY";
	public static final String TOTAL = "TOTAL";
	public static final String SORT = "SORT";
	public static final String ORDER_STATUS = "ORDER_STATUS";
	public static final String FLAGSM = "FLAGSM";
	public static final String SALESMAN = "SALESMAN";

	private OlbiusQuery query2;
	
	private void initQuery() {
		
		String productStoreId = (String) getParameter(PRODUCT_STORE);
		String organ = (String) getParameter(ORG);
		String orderStatus = (String) getParameter(ORDER_STATUS);
		String flagSM = (String) getParameter(FLAGSM);
		List<Object> salesmanList = (List<Object>) getParameter(SALESMAN);
		
		query2 = OlbiusQuery.make(getSQLProcessor());
		query2.from("sales_order_fact").select("product_dimension.product_id").select("product_dimension.product_code").select("product_store_dimension.store_name")
		.select("product_dimension.product_name").select("sum(quantity)", "quantity1")
		.select("sum(total)", "total1").join(Join.INNER_JOIN, "date_dimension", "order_date_dim_id = date_dimension.dimension_id");
		
		Join join = new InnerJoin();
		join.table("product_dimension").on(Condition.make("product_dim_id = product_dimension.dimension_id"));
		
		Condition condition = new Condition();
		
		query2.join(join)
		.join(Join.INNER_JOIN, "product_store_dimension", "product_store_dim_id = product_store_dimension.dimension_id");
		if("SM".equals(flagSM)){
			query2.join(Join.INNER_JOIN, "party_dimension", "salesman", "sales_order_fact.sale_executive_party_dim_id = salesman.dimension_id");
		}else{
			query2.join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.party_from_dim_id = party_dimension.dimension_id");
		}
		
		query2.where(condition)
		.groupBy("product_dimension.dimension_id").groupBy("product_store_dimension.store_name")
		.groupBy("party_dimension.dimension_id").orderBy("total1");
		
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
		if(productStoreId != null){
			condition.and(Condition.makeEQ("product_store_dimension.product_store_id", productStoreId, productStoreId != null));
		}
		if("SM".equals(flagSM)){
			condition.and(Condition.makeIn("salesman.party_id", salesmanList));
		}else{
			condition.and(Condition.makeEQ("party_dimension.party_id", organ));
		}
		condition.and(Condition.makeEQ("sales_order_fact.order_status", orderStatus));
		condition.and(Condition.make("sales_order_fact.return_id is null"));
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query2 == null) {
			initQuery();
		}
		return query2;
	}
	
	public class PPSPie implements OlapResultQueryInterface {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		@Override
		public Object resultQuery(OlapQuery query2) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			try {
				ResultSet resultSet = query2.getResultSet();
				while(resultSet.next()) {
					String productIdOut = resultSet.getString("product_code");
					BigDecimal quantityR = (BigDecimal) resultSet.getBigDecimal("quantity1");
//					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productIdOut), false);
//					String internalName = product.getString("internalName");
					if(map.get(productIdOut)==null) {
						map.put(productIdOut, new HashMap<String, Object>());
					}
//					map.get(internalName).put(resultSet.getString("product_code"), quantityR);
					map.put(productIdOut, quantityR);
				}
			} catch (GenericEntityException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return map;
		}
	}
	
	public class PPS3Pie extends AbstractOlapChart {

		public PPS3Pie(OlapInterface olap, OlapResultQueryInterface query2) {
			super(olap, query2);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@Override
		protected void result(Object object) {
			Map<String, Object> tmp = ( Map<String, Object>) object;
			for(String s : tmp.keySet()) {
				List<Object> list = new ArrayList<Object>();
				list.add(tmp.get(s));
				yAxis.put(s, list);
				xAxis.add(s);
			}
		}
	}
	
	public class PPSPie2 implements OlapResultQueryInterface {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		@Override
		public Object resultQuery(OlapQuery query2) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			try {
				ResultSet resultSet = query2.getResultSet();
				while(resultSet.next()) {
					String productIdOut = resultSet.getString("product_code");
					BigDecimal totalR = (BigDecimal) resultSet.getBigDecimal("total1");
//					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productIdOut), false);
//					String internalName = product.getString("internalName");
					if(map.get(productIdOut)==null) {
						map.put(productIdOut, new HashMap<String, Object>());
					}
					map.put(productIdOut, totalR);
				}
			} catch (GenericEntityException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return map;
		}
	}
	
	public class PPS3Pie2 extends AbstractOlapChart {

		public PPS3Pie2(OlapInterface olap, OlapResultQueryInterface query2) {
			super(olap, query2);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@Override
		protected void result(Object object) {
			Map<String, Object> tmp = ( Map<String, Object>) object;
			for(String s : tmp.keySet()) {
				List<Object> list = new ArrayList<Object>();
				list.add(tmp.get(s));
				yAxis.put(s, list);
				xAxis.add(s);
			}
		}
	}
	
	public class PPSColumn implements OlapResultQueryInterface {
		@Override
		public Object resultQuery(OlapQuery query2) {
			Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String, Object>>();
			String quantityO = (String) getParameter(QUANTITY);
			tmp.put(quantityO, new HashMap<String, Object>());
			try{
				ResultSet resultSet = query2.getResultSet();
				while (resultSet.next()) {
					int zero = 0;
					try {
						BigDecimal quantityR = (BigDecimal) resultSet.getBigDecimal("quantity1");
						if(UtilValidate.isNotEmpty(quantityR)){
							tmp.get(quantityO).put(resultSet.getString("product_code"),quantityR);
						}else{
							tmp.get(quantityO).put(resultSet.getString("product_code"), zero);
						}
					} catch (Exception e) {
						Debug.logError(e.getMessage(), PPSColumn.class.getName());
					}
				}
			} catch(Exception e) {
			
			}
			return tmp;
		}
	}

	public class PPS3Column extends AbstractOlapChart {
		public PPS3Column(OlapInterface olap, OlapResultQueryInterface query2) {
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
	
	public class PPSArea implements OlapResultQueryInterface {
		@Override
		public Object resultQuery(OlapQuery query2) {
			Map<String, Map<String, Object>> tmp2 = new HashMap<String, Map<String, Object>>();
			String totalO = (String) getParameter(TOTAL);
			tmp2.put(totalO, new HashMap<String, Object>());
			try{
				ResultSet resultSet2 = query2.getResultSet();
				while (resultSet2.next()) {
					int zero = 0;
					try {
						BigDecimal totalR = (BigDecimal) resultSet2.getBigDecimal("total1");
						if(UtilValidate.isNotEmpty(totalR)){
							tmp2.get(totalO).put(resultSet2.getString("product_code"), totalR);
						}else{
							tmp2.get(totalO).put(resultSet2.getString("product_code"), zero);
						}
					} catch (Exception e) {
						Debug.logError(e.getMessage(), PPSArea.class.getName());
					}
				}
			} catch(Exception e) {
			
			}
			return tmp2;
		}
	}

	public class PPS3Area extends AbstractOlapChart {
		public PPS3Area(OlapInterface olap, OlapResultQueryInterface query2) {
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

	public class ResultReport extends ReturnResultGrid {

		public ResultReport() {
			addDataField("productStoreName");
			addDataField("percent");
			addDataField("productName");
			addDataField("quantity1");
			addDataField("total1");
			addDataField("stt");
		}
		
		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			String productStoreId = (String) getParameter(PRODUCT_STORE);
			String all = (String) getParameter(ALL);
			try {
				if(productStoreId != null){
					map.put("productStoreName", result.getString("store_name"));
				} else {
					map.put("productStoreName", all);
				}
				map.put("productName", result.getString("product_name"));
				map.put("quantity1", result.getBigDecimal("quantity1"));
				map.put("total1", result.getBigDecimal("total1"));
			} catch (Exception e) {
				Debug.logError(e.getMessage(), ResultReport.class.getName());
			}
			return map;
		}
	}
}
