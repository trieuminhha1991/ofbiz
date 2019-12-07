package com.olbius.salesmtl.report;

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
import com.olbius.bi.olap.query.join.Join;

public class TopCustomerBySalesmanImpl extends AbstractOlap{
	public static final String LEVEL = "LEVEL";
	private OlbiusQuery query;
	
	private void initQuery() {
		Condition condition = new Condition();
		String level = (String) getParameter(LEVEL);
		String confirm = (String) getParameter("confirm");
		String partyId = null;
		List<Object> partyIdList = null;
		if("SM".equals(confirm)){
			partyId = (String) getParameter("partyId");
		} else {
			partyIdList = (List<Object>) getParameter("partyId");
		}
		Integer limit = (Integer) getParameter("limit");
		query = new OlbiusQuery(getSQLProcessor());
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.and("sales_order_fact.order_status = 'ORDER_COMPLETED'");
		condition.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'");
		if("SM".equals(confirm)){
			if(UtilValidate.isNotEmpty(partyId)){
				condition.andEQ("saex.party_id", partyId);
			}
		} else {
			if(UtilValidate.isNotEmpty(partyIdList)){
				condition.and(Condition.makeIn("saex.party_id", partyIdList));
			}
		}
		
		query.select("cus.party_id", "cus_id")
		.select("COALESCE(cus.last_name, '') || ' ' || COALESCE(cus.middle_name, '') || ' ' || COALESCE(cus.first_name, '')", "cus_name")
		.select("COALESCE(cus.last_name, '') || ' ' || COALESCE(cus.middle_name, '') || ' ' || COALESCE(cus.first_name, '') || ' - ' || COALESCE(cus.party_code, '')", "cus_name_chart")
		.select("sum(total)", "turnover_value")
		.select("cus1.name", "cus1_name")
		.select("COALESCE(cus1.name, '') || ' - ' || COALESCE(cus1.party_code, '')", "cus1_name_chart")
		.select("cus1.party_id", "cus1_id").select("cus.party_type_id", "party_type")
		.from("sales_order_fact")
		.join(Join.INNER_JOIN, "date_dimension", null, "date_dimension.dimension_id = sales_order_fact.order_date_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "saex", "saex.dimension_id = sales_order_fact.sale_executive_party_dim_id")
		.join(Join.LEFT_OUTER_JOIN, "party_dimension", "cus", "cus.dimension_id = sales_order_fact.party_to_dim_id AND cus.party_type_id = 'PERSON'")
		.join(Join.LEFT_OUTER_JOIN, "party_dimension", "cus1", "cus1.dimension_id = sales_order_fact.party_to_dim_id AND cus1.party_type_id != 'PERSON'")
		.where(condition)
		.groupBy("cus.dimension_id").groupBy("cus1.dimension_id").orderBy("turnover_value", OlbiusQuery.DESC);
		if(UtilValidate.isNotEmpty(limit)){
			query.limit(limit);
		}
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	public class EvaluateTopCusPie implements OlapResultQueryInterface {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			try {
				ResultSet resultSet = query.getResultSet();
				while(resultSet.next()) {
//					String cusOut = resultSet.getString("cus_name");
//					String cusId = resultSet.getString("cus_id");
//					String cus1Id = resultSet.getString("cus1_id");
					String cusName = resultSet.getString("cus_name_chart");
					String cus1Name = resultSet.getString("cus1_name_chart");
					String type = resultSet.getString("party_type");
					BigDecimal volume = resultSet.getBigDecimal("turnover_value");
					if("PERSON".equals(type)){
						if(map.get(cusName)==null) {
							map.put(cusName, new HashMap<String, Object>());
						}
						map.put(cusName, volume);
					}else{
						if(map.get(cus1Name)==null) {
							map.put(cus1Name, new HashMap<String, Object>());
						}
						map.put(cus1Name, volume);
					}
					
				}
			} catch (GenericEntityException | SQLException e) {
				e.printStackTrace();
			}
			
			return map;
		}
	}
	
	public class EvaluateTopCus3Pie extends AbstractOlapChart {

		public EvaluateTopCus3Pie(OlapInterface olap, OlapResultQueryInterface query) {
			super(olap, query);
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
	
	public class ResultCusTurnoverByS extends ReturnResultGrid{

		public ResultCusTurnoverByS() {
			addDataField("stt");
			addDataField("cusId");
			addDataField("cusName");
			addDataField("orderValue");
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				String cusId = result.getString("cus_id");
				String cus1Id = result.getString("cus1_id");
				String cusName = result.getString("cus_name");
				String cus1Name = result.getString("cus1_name");
				String type = result.getString("party_type");
				if("PERSON".equals(type)){
					map.put("cusId", cusId);
					map.put("cusName", cusName);
				}else{
					map.put("cusId", cus1Id);
					map.put("cusName", cus1Name);
				}
				BigDecimal valueCus = result.getBigDecimal("turnover_value");
				map.put("orderValue", valueCus);
			} catch (Exception e) {
				Debug.logError(e.getMessage(), ResultCusTurnoverByS.class.getName());
			}
			return map;
		}
	}
	
}
