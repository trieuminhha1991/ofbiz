package com.olbius.crm.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
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

public class CommunicationCampaignOlapImpl extends AbstractOlap{
	private OlbiusQuery query;
	private OlbiusQuery querySum;
	private List<String> type;
	public static final String ORG = "ORG";
	
	private List<String> getType() {
		if(type == null) {
			ResultEnumType enumType = new ResultEnumType(getSQLProcessor());
			type = enumType.getListResultEnumType();
		}
		return type;
	}
	
	Map<String, Object> sum = FastMap.newInstance();
	
	private void initQuery() {
		String marketingCampaignId = (String) getParameter("marketingCampaignId");
		Boolean isChart = (Boolean) getParameter("isChart");
		OlbiusQuery queryTmp = new OlbiusQuery();
		Condition cond = new Condition();
		String organ = (String) getParameter(ORG);
		
		if (isChart == false){
			queryTmp.select("c.marketing_campaign_id", "marketing_campaign_id")
			.select("c.result_enum_id", "result_enum_id")
			.select("COUNT(c.result_enum_type_id)", "total")
			.select("c.result_enum_type_id", "result_enum_type_id")
			.from("communication_campaign_fact", "c")
			.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = entry_date_dim_id")
			.join(Join.INNER_JOIN, "party_person_relationship", "c.party_from_dim_id = party_person_relationship.person_dim_id")
			.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id")
			.where(cond)
			.groupBy("c.result_enum_type_id").groupBy("c.marketing_campaign_id").groupBy("c.result_enum_id ");
			
			cond.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)));
			cond.and(Condition.make("c.marketing_campaign_id is not null"));
			cond.and(Condition.make("c.result_enum_type_id is not null"));
			cond.and(Condition.makeEQ("organ.party_id", organ));
			
			query = new OlbiusQuery(getSQLProcessor());
			query.from(queryTmp, "tmp").select("result_enum_type_id").select("result_enum_id")
			.groupBy("result_enum_type_id").groupBy("result_enum_id").orderBy("result_enum_type_id").orderBy("result_enum_id");
			
			List<String> type = getType();
			for(String s: type) {
				query.select("sum(case when marketing_campaign_id = '"+s+"' then total else 0 end)", "x_" + s);
			}
		} else {
			query = new OlbiusQuery(getSQLProcessor());
			query.select("COUNT(c.result_enum_type_id)", "total")
			.select("c.result_enum_type_id", "result_enum_type_id")
			.from("communication_campaign_fact", "c")
			.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = entry_date_dim_id")
			.join(Join.INNER_JOIN, "party_person_relationship", "c.party_from_dim_id = party_person_relationship.person_dim_id")
			.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id");
			cond.andBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate));
			cond.andEQ("c.marketing_campaign_id", marketingCampaignId, !"all".equals(marketingCampaignId));
			cond.and("c.result_enum_type_id is not null");
			cond.and(Condition.makeEQ("organ.party_id", organ));
			query.where(cond)
			.groupBy("c.result_enum_type_id");
		}
	}
	
	private void initQuerySum() throws GenericDataSourceException, GenericEntityException, SQLException{
		querySum = new OlbiusQuery(getSQLProcessor());
		String organ = (String) getParameter(ORG);
		querySum.select("DISTINCT(marketing_campaign_id)")
				.select("COUNT(party_from_dim_id)", "c1")
				.from("communication_campaign_fact").where(Condition.make("marketing_campaign_id is NOT NULL"))
				.join(Join.INNER_JOIN, "date_dimension", "date_dimension.dimension_id = entry_date_dim_id")
				.join(Join.INNER_JOIN, "party_person_relationship", "communication_campaign_fact.party_from_dim_id = party_person_relationship.person_dim_id")
				.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
				.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id")
				.where(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate)).andEQ("organ.party_id", organ))
				.groupBy("marketing_campaign_id");
		ResultSet resultSetSum = querySum.getResultSet();
		while (resultSetSum.next()) {
			sum.put("x_" + resultSetSum.getString("marketing_campaign_id"), resultSetSum.getBigDecimal("c1"));
		}
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	public class CommunicationCampaign extends ReturnResultGrid{
		public CommunicationCampaign() {
			addDataField("result_enum_type_id");
			addDataField("result_enum_id");
			List<String> type = getType();
			for(String s: type) {
				addDataField("x_" + s);
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
					map.put("x_" + s, result.getBigDecimal("x_" + s));
				}
			} catch (Exception e) {
				Debug.logError(e.getMessage(), CommunicationCampaign.class.getName());
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
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					querySum.close();
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				}
			}
			map.put("sumMap", sum);
		}
		return map;
	}
	
	public class PieResult extends AbstractOlapChart {
		public PieResult(OlapInterface olap, OlapResultQueryInterface query) {
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
	
	public class PieOlapResultQuery implements OlapResultQueryInterface {
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
