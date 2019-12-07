package com.olbius.salesmtl.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.chart.XAxis;
import com.olbius.bi.olap.grid.ReturnResultGrid;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class OrderVolumeImpl extends AbstractOlap{
	public static final String ORG = "ORG";
	public static final String TURNOVER = "TURNOVER";
	public static final String CHART = "CHART";
	public static final String SALESMAN = "SALESMAN";
	
	private OlbiusQuery query;
	
	private void initQuery() {
		Condition condition = new Condition();
		String partyId = (String) getParameter("partyId");
		String isChart = (String) getParameter(CHART);
		List<Object> salesmanList = (List<Object>) getParameter(SALESMAN);
		query = new OlbiusQuery(getSQLProcessor());
		
		condition.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
		condition.and("sales_order_fact.order_status = 'ORDER_COMPLETED'");
		condition.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'");
		condition.and("saex.party_id is not null");
		if(UtilValidate.isNotEmpty(partyId)){
			condition.and(Condition.makeEQ("saex.party_id", partyId, partyId != null));
		}
		if(UtilValidate.isNotEmpty(salesmanList)){
			condition.andIn("saex.party_id", salesmanList);
		}
		
		query.select("saex.party_id").select("sum(total)", "turnover_value")
		.from("sales_order_fact")
		.join(Join.INNER_JOIN, "date_dimension", null, "sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "saex", "saex.dimension_id = sales_order_fact.sale_executive_party_dim_id");
		query.where(condition)
		.groupBy("saex.dimension_id").orderBy("turnover_value", OlbiusQuery.DESC);
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	public class SaEx extends ReturnResultGrid{

		public SaEx() {
			addDataField("stt");
			addDataField("staffId");
			addDataField("orderValue");
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("staffId", result.getString("party_id"));
				map.put("orderValue", result.getBigDecimal("turnover_value"));
			} catch (Exception e) {
				Debug.logError(e.getMessage(), SaEx.class.getName());
			}
			return map;
		}
	}
	
	public class TSColumn implements OlapResultQueryInterface {
		@Override
		public Object resultQuery(OlapQuery queryChart) {
			Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String, Object>>();
			XAxis axis = new XAxis();
			axis.setData(tmp);
			String quantityO = (String) getParameter(TURNOVER);
			tmp.put(quantityO, new HashMap<String, Object>());
			try{
				ResultSet resultSet = queryChart.getResultSet();
				while (resultSet.next()) {
					int zero = 0;
					try {
						axis.getXAxis().add(resultSet.getString("party_id"));
						BigDecimal quantityR = resultSet.getBigDecimal("turnover_value");
						if(UtilValidate.isNotEmpty(quantityR)){
							tmp.get(quantityO).put(resultSet.getString("party_id"),quantityR);
						}else{
							tmp.get(quantityO).put(resultSet.getString("party_id"), zero);
						}
					} catch (Exception e) {
						Debug.logError(e.getMessage(), TSColumn.class.getName());
					}
				}
			} catch(Exception e) {
			
			}
			return axis;
		}
	}
	
}
