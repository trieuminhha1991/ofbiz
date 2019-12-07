package com.olbius.crm.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDataSourceException;
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

import javolution.util.FastMap;

public class CommunicationEmployeeOlapImpl extends AbstractOlap{
	private OlbiusQuery query;
	private OlbiusQuery querySum;
	private OlbiusQuery querySum2;
	private OlbiusQuery querySum3;
	private List<String> type;
	public static final String ORG = "ORG";
	
	private List<String> getType() {
		if(type == null) {
			ResultEmployee empl = new ResultEmployee(getSQLProcessor());
			type = empl.getListEmployee();
		}
		return type;
	}
	
	Map<String, Object> sum = FastMap.newInstance();
	Map<String, Object> sumC = FastMap.newInstance();
	Map<String, Object> sumU = FastMap.newInstance();
	
	private void initQuery() {
		String partyId = (String) getParameter("partyId");
		Boolean isChart = (Boolean) getParameter("isChart");
		OlbiusQuery queryTmp = new OlbiusQuery();
		Condition cond = new Condition();
		String organ = (String) getParameter(ORG);
		if (isChart == false){
			queryTmp.select("p1.party_id", "party_id_from")
			.select("COUNT(p1.party_id)", "c1")
			.select("communication_event_fact.result_enum_type_id", "result_enum_type_id")
			.select("communication_event_fact.result_enum_id", "result_enum_id")
			.from("communication_event_fact", null)
			.join(Join.INNER_JOIN, "party_dimension", "p1", "p1.dimension_id = party_from_dim_id")
			.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = entry_date_dim_id")
			.join(Join.INNER_JOIN, "party_person_relationship", "p1.dimension_id = party_person_relationship.person_dim_id")
			.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id")
			.where(cond)
			.groupBy("result_enum_type_id").groupBy("p1.party_id ").groupBy("result_enum_id").orderBy("result_enum_type_id");
			
			cond.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).and(Condition.makeEQ("status_call", "Y")));
			cond.and(Condition.makeEQ("organ.party_id", organ));
			query = new OlbiusQuery(getSQLProcessor());
			query.from(queryTmp, "tmp").select("result_enum_type_id").select("result_enum_id")
			.groupBy("result_enum_type_id").groupBy("result_enum_id").orderBy("result_enum_type_id").orderBy("result_enum_id");
			
			List<String> type = getType();
			
			for(String s: type) {
				query.select("sum(case when party_id_from = '"+s+"' then c1 else 0 end)", s);
			}
		} else {
			query = new OlbiusQuery(getSQLProcessor());
			query.select("COUNT(p1.party_id)", "total")
			.select("communication_event_fact.result_enum_type_id", "result_enum_type_id")
			.from("communication_event_fact", null)
			.join(Join.INNER_JOIN, "party_dimension", "p1", "p1.dimension_id = party_from_dim_id")
			.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = entry_date_dim_id")
			.join(Join.INNER_JOIN, "party_person_relationship", "p1.dimension_id = party_person_relationship.person_dim_id")
			.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id");
			cond.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
			cond.andEQ("status_call", "Y");
			cond.andEQ("p1.party_id", partyId, !"all".equals(partyId));
			cond.and(Condition.makeEQ("organ.party_id", organ));
			query.where(cond)
			.groupBy("result_enum_type_id").orderBy("result_enum_type_id");
		}
	}
	
	private void initQuerySum() throws GenericDataSourceException, GenericEntityException, SQLException{
		String organ = (String) getParameter(ORG);
		querySum = new OlbiusQuery(getSQLProcessor());
		querySum.select("DISTINCT(caller.party_id)", "party_id_from")
				.select("COUNT(party_from_dim_id)", "c1")
				.from("communication_event_fact").where(Condition.make("party_from_dim_id is NOT NULL"))
				.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = entry_date_dim_id")
				.join(Join.INNER_JOIN, "party_dimension", "caller", "caller.dimension_id = communication_event_fact.party_from_dim_id")
				.join(Join.INNER_JOIN, "party_person_relationship", "caller.dimension_id = party_person_relationship.person_dim_id")
				.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id")
				.where(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).and(Condition.makeEQ("status_call", "Y")).andEQ("organ.party_id", organ))
				.groupBy("party_id_from");
		ResultSet resultSetSum = querySum.getResultSet();
		while (resultSetSum.next()) {
			sum.put(resultSetSum.getString("party_id_from"), resultSetSum.getBigDecimal("c1"));
		}
	}
	
	private void initQuerySumContacted() throws GenericDataSourceException, GenericEntityException, SQLException{
		String organ = (String) getParameter(ORG);
		querySum2 = new OlbiusQuery(getSQLProcessor());
		querySum2.select("DISTINCT(caller.party_id) AS party_id_from, COUNT(parent_type_id) as c2")
		.from("communication_event_fact").where(Condition.make("party_from_dim_id is NOT NULL"))
		.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = entry_date_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "caller", "caller.dimension_id = communication_event_fact.party_from_dim_id")
		.join(Join.INNER_JOIN, "party_person_relationship", "caller.dimension_id = party_person_relationship.person_dim_id")
		.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id")
		.where(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).andEQ("parent_type_id", "CONTACTED").and(Condition.makeEQ("status_call", "Y")).andEQ("organ.party_id", organ))
		.groupBy("party_id_from");
		ResultSet resultSetSum = querySum2.getResultSet();
		while (resultSetSum.next()) {
			sumC.put(resultSetSum.getString("party_id_from"), resultSetSum.getBigDecimal("c2"));
		}
	}
	
	private void initQuerySumUncontacted() throws GenericDataSourceException, GenericEntityException, SQLException{
		String organ = (String) getParameter(ORG);
		querySum3 = new OlbiusQuery(getSQLProcessor());
		querySum3.select("DISTINCT(caller.party_id) as party_id_from, COUNT(parent_type_id) as c3")
		.from("communication_event_fact").where(Condition.make("party_from_dim_id is NOT NULL"))
		.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = entry_date_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "caller", "caller.dimension_id = communication_event_fact.party_from_dim_id")
		.join(Join.INNER_JOIN, "party_person_relationship", "caller.dimension_id = party_person_relationship.person_dim_id")
		.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id")
		.where(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).andEQ("parent_type_id", "UNCONTACTED").and(Condition.makeEQ("status_call", "Y")).andEQ("organ.party_id", organ))
		.groupBy("party_id_from");
		ResultSet resultSetSum = querySum3.getResultSet();
		while (resultSetSum.next()) {
			sumU.put(resultSetSum.getString("party_id_from"), resultSetSum.getBigDecimal("c3"));
		}
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	public class CommunicationEmployee extends ReturnResultGrid{
		public CommunicationEmployee() {
			addDataField("result_enum_type_id");
			addDataField("result_enum_id");
			List<String> type = getType();
			for(String s: type) {
				addDataField(s);
			}
		}

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("result_enum_type_id", result.getString("result_enum_type_id"));
				map.put("result_enum_id", result.getString("result_enum_id"));
				List<String> type = getType();
				for(String s: type) {
					map.put(s, result.getBigDecimal(s));
				}
			} catch (Exception e) {
				Debug.logError(e.getMessage(), CommunicationEmployee.class.getName());
			}
			return map;
		}
	}
	
	@Override
	public Map<String, Object> execute() {
		Map<String, Object> map = super.execute();
		if(!isChart() && getParameter(INIT) != null) {
			try {
				initQuerySum();
				initQuerySumContacted();
				initQuerySumUncontacted();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					querySum.close();
					querySum2.close();
					querySum3.close();
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				}
			}
			map.put("sumMap", sum);
			map.put("sumMapContacted", sumC);
			map.put("sumMapUncontacted", sumU);
		}
		return map;
	}
	
	public class PieEmployeeResult extends AbstractOlapChart {
		public PieEmployeeResult(OlapInterface olap, OlapResultQueryInterface query) {
			super(olap, query);
		}

		@Override
		public boolean isChart() {
			return true;
		}

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
	
	public class PieEmployeeOlapResultQuery implements OlapResultQueryInterface {
		Locale locale = null;
		@Override
		public Object resultQuery(OlapQuery query) {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				ResultSet resultSet = query.getResultSet();
				while (resultSet.next()) {
					map.put(resultSet.getString("result_enum_type_id"), resultSet.getBigDecimal("total"));
				}
				return map;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		public Locale getLocale(){
			return this.locale;
		}
		public void setLocale(Locale locale){
			this.locale = locale;
		}
	}
}
