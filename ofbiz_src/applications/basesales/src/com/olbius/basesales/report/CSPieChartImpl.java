package com.olbius.basesales.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class CSPieChartImpl extends AbstractOlap {

	public static final String ORG = "ORG";
	public static final String CATEGORY = "CATEGORY";
	public static final String GROUP = "GROUP";
	public static final String QUANTITY = "QUANTITY";

	private OlbiusQuery queryChart;

	private void initQuery() {
//		List<Object> classificationGroup = (List<Object>) getParameter(GROUP);
		String organ = (String) getParameter(ORG);
		
		queryChart = new OlbiusQuery(getSQLProcessor());

		Condition condition = new Condition();
		
		condition.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'").and("sales_order_fact.total != '0'");
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.andEQ("organ.party_id", organ);

		queryChart.select("count(distinct party_dimension.party_id) as quantity, sum(sales_order_fact.total) as loyalty_value, CASE WHEN party_class_group_dimension.party_classification_group_id IS not null THEN party_class_group_dimension.party_classification_group_id ELSE 'OTHER' END AS loyalty_group")
		.from("sales_order_fact")
		.join(Join.INNER_JOIN, "party_dimension", "sales_order_fact.party_to_dim_id = party_dimension.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = sales_order_fact.order_date_dim_id")
		.join(Join.LEFT_OUTER_JOIN, "party_classification_fact", "party_dimension.dimension_id = party_classification_fact.party_dim_id")
		.join(Join.LEFT_OUTER_JOIN, "party_class_group_dimension", "party_classification_fact.party_class_group_dim_id = party_class_group_dimension.dimension_id")
		.join(Join.LEFT_OUTER_JOIN, "party_dimension", "organ", "sales_order_fact.party_from_dim_id = organ.dimension_id")
		.where(condition)
		.groupBy("party_class_group_dimension.dimension_id");
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(queryChart == null) {
			initQuery();
		}
		return queryChart;
	}
	
	public class EvaluateLoyaltyGroup implements OlapResultQueryInterface {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		@Override
		public Object resultQuery(OlapQuery queryChart) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				ResultSet resultSet = queryChart.getResultSet();
				while(resultSet.next()) {
					String cusName = resultSet.getString("loyalty_group");
					if(UtilValidate.isEmpty(cusName)){
						cusName = "Other";
					}
					BigDecimal volume = resultSet.getBigDecimal("quantity");
						if(map.get(cusName)==null) {
							map.put(cusName, new HashMap<String, Object>());
						}
						map.put(cusName, volume);
				}
			} catch (GenericEntityException | SQLException e) {
				e.printStackTrace();
			}
			
			return map;
		}
	}
	
	public class EvaluateLoyaltyGroup3Pie extends AbstractOlapChart {

		public EvaluateLoyaltyGroup3Pie(OlapInterface olap, OlapResultQueryInterface queryChart) {
			super(olap, queryChart);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
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
	
	public class EvaluateLoyaltyGroupValue implements OlapResultQueryInterface {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		@Override
		public Object resultQuery(OlapQuery queryChart) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				ResultSet resultSet = queryChart.getResultSet();
				while(resultSet.next()) {
					String cusName = resultSet.getString("loyalty_group");
					BigDecimal volume = resultSet.getBigDecimal("loyalty_value");
						if(map.get(cusName)==null) {
							map.put(cusName, new HashMap<String, Object>());
						}
						map.put(cusName, volume);
				}
			} catch (GenericEntityException | SQLException e) {
				e.printStackTrace();
			}
			
			return map;
		}
	}
	
	public class EvaluateLoyaltyGroupValue3Pie extends AbstractOlapChart {

		public EvaluateLoyaltyGroupValue3Pie(OlapInterface olap, OlapResultQueryInterface queryChart) {
			super(olap, queryChart);
		}

		@Override
		public boolean isChart() {
			return true;
		}

		@SuppressWarnings("unchecked")
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
}