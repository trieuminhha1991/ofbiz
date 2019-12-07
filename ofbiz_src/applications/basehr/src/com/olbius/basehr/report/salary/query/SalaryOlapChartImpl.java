package com.olbius.basehr.report.salary.query;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;

import com.olbius.basehr.util.PartyUtil;
import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SalaryOlapChartImpl extends AbstractOlap{
	public static final String FROMDATE = "FROMDATE";
	public static final String EMPLOYEE = "EMPLOYEE";
	public static final String PAYROLLTABLE = "PAYROLLTABLE";
	public static final String ORG = "ORG";
	public static final String ROOT = "ROOT";
	public static final String DEPARTMENT = "DEPARTMENT";
	private OlbiusQuery query;
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		List<Object> orgId = (List<Object>) getParameter(EMPLOYEE);
		List<Object> fromDateList = (List<Object>) getParameter(FROMDATE);
		String org = (String) getParameter(ORG);
		String rootOrg = (String) getParameter(ROOT);
		List<Object> departmentId = (List<Object>) getParameter(DEPARTMENT);
		query = OlbiusQuery.make(getSQLProcessor());
		Condition condition = new Condition();
		
		if(org.equals(rootOrg)){
			query.distinct();
			query.from("payroll_fact", "pf")
			.select("pd.party_id", "party_id")
			.select("sum(pf.value) as total")
			.join(Join.INNER_JOIN, "party_dimension", "pd", "pf.department_dim_id = pd.dimension_id")
			.join(Join.INNER_JOIN, "date_dimension", "dd", "pf.from_date_dim = dd.dimension_id")
			.groupBy("pd.party_id")
			.where(condition);
			condition.and(Condition.makeIn("dd.date_value", fromDateList));
			condition.and(Condition.makeIn("pd.party_id", departmentId));
			condition.and(Condition.makeEQ("pf.code", "LUONG_THUC_TE"));
		}else{
			query.from("payroll_fact", "pf")
			.select("pd.party_id", "party_id")
			.select("sum(pf.value) as total")
			.select("pd.party_id")
			.join(Join.INNER_JOIN, "party_dimension", "pd", "pf.party_dim_id = pd.dimension_id")
			.join(Join.INNER_JOIN, "date_dimension", "dd", "pf.from_date_dim = dd.dimension_id")
			.groupBy("pd.party_id")
			.where(condition);
			condition.and(Condition.makeIn("dd.date_value", fromDateList));
			condition.and(Condition.makeIn("pd.party_id", orgId));
			condition.and(Condition.makeEQ("pf.code", "LUONG_THUC_TE"));
		}
		
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null){
			initQuery();
		}
		return query;
	}
	
	public class SalaryComparePie implements OlapResultQueryInterface{

		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Object> map = new HashMap<String, Object>();
			GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
			try {
				ResultSet resultSet = query.getResultSet();
				while(resultSet.next()){
					String partyId = resultSet.getString("party_id");
					String partyName = PartyUtil.getPartyName(delegator, partyId);
					map.put(partyName, resultSet.getLong("total"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return map;
		}
		
	}
	
	public class SalaryComparePieOut extends AbstractOlapChart{

		public SalaryComparePieOut(OlapInterface olap,
				OlapResultQueryInterface query) {
			super(olap, query);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Object> tmp = ( Map<String, Object>) object;
			for (String s : tmp.keySet()) {
				List<Object> list = new ArrayList<Object>();
				list.add(tmp.get(s));
				yAxis.put(s, list);
				xAxis.add(s);
			}
		}
		
	}
	
}
