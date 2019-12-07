package com.olbius.basehr.report.workprocess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapDate;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

import javolution.util.FastMap;

public class PartyOlapImpl extends AbstractOlap{
	public static final String GROUP = "GROUP";
	public static final String TYPE = "TYPE";
	public static final String ROOT = "ROOT";
	
	private String dateType;
	private OlbiusQuery query;
	
	@SuppressWarnings("unchecked")
	private void initQuery(){
		query = OlbiusQuery.make(getSQLProcessor());
		Condition condition = new Condition();
		Condition condition1 = new Condition();
		List<Object> group = (List<Object>) getParameter(GROUP);
		String type = (String) getParameter(TYPE);
		String flag = "";
		
		if(UtilValidate.isNotEmpty(type)){
			flag = type;
			if(type.equals("") || type.equals("MONTH")){
				dateType = "year_and_month";
			}else if(type.equals("DAY")){
				dateType = "year_month_day";
			}else if(type.equals("WEEK")){
				dateType = "week_and_year";
			}else if(type.equals("QUARTER")){
				dateType = "quarter_and_year";
			}else if(type.equals("YEAR")){
				dateType = "year_name";
			}
		}
		
		query.from("party_person_relationship", "ppr")
		.select("dd.year_month_day", "fromDate" ,(flag.equals("DAY")))
		.select("dd.year_and_month", "fromDate" , (flag.equals("") || flag.equals("MONTH")))
		.select("dd.week_and_year",  "fromDate" , flag.equals("WEEK"))
		.select("dd.quarter_and_year", "fromDate" , flag.equals("QUARTER"))
		.select("dd.year_name",  "fromDate" , flag.equals("YEAR"))
		.select("dd1.year_month_day", "thruDate" ,(flag.equals("DAY")))
		.select("dd1.year_and_month", "thruDate" , (flag.equals("") || flag.equals("MONTH")))
		.select("dd1.week_and_year",  "thruDate" , flag.equals("WEEK"))
		.select("dd1.quarter_and_year", "thruDate" , flag.equals("QUARTER"))
		.select("dd1.year_name",  "thruDate" , flag.equals("YEAR"))
		.select("count(DISTINCT pd.party_id)", "count")
		.select("dd2.year_month_day", "curDate" ,(flag.equals("DAY")))
		.select("dd2.year_and_month", "curDate" , (flag.equals("") || flag.equals("MONTH")))
		.select("dd2.week_and_year",  "curDate" , flag.equals("WEEK"))
		.select("dd2.quarter_and_year", "curDate" , flag.equals("QUARTER"))
		.select("dd2.year_name",  "curDate" , flag.equals("YEAR"))
		.join(Join.INNER_JOIN, "party_dimension", "pd", "ppr.person_dim_id = pd.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd1", "ppr.group_dim_id = pd1.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "ppr.from_date_dim_id = dd.dimension_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd1", "ppr.thru_date_dim_id = dd1.dimension_id")
		.join(Join.INNER_JOIN, "person_relationship_fact", "prf", 
		"ppr.person_dim_id = prf.person_dim_id and ppr.group_dim_id = prf.group_dim_id and ppr.role_type_id = prf.role_type_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd2", "prf.date_dim_id = dd2.dimension_id")
		.groupBy("dd.year_and_month", (flag.equals("") || flag.equals("MONTH")))
		.groupBy("dd.year_month_day", flag.equals("DAY"))
		.groupBy("dd.week_and_year", flag.equals("WEEK"))
		.groupBy("dd.quarter_and_year", flag.equals("QUARTER"))
		.groupBy("dd.year_name", flag.equals("YEAR"))
		.groupBy("dd1.year_and_month", (flag.equals("") || flag.equals("MONTH")))
		.groupBy("dd1.year_month_day", flag.equals("DAY"))
		.groupBy("dd1.week_and_year", flag.equals("WEEK"))
		.groupBy("dd1.quarter_and_year", flag.equals("QUARTER"))
		.groupBy("dd1.year_name", flag.equals("YEAR"))
		.groupBy("dd2.year_and_month", (flag.equals("") || flag.equals("MONTH")))
		.groupBy("dd2.year_month_day", flag.equals("DAY"))
		.groupBy("dd2.week_and_year", flag.equals("WEEK"))
		.groupBy("dd2.quarter_and_year", flag.equals("QUARTER"))
		.groupBy("dd2.year_name", flag.equals("YEAR"))
		.where(condition);
		
		if(UtilValidate.isNotEmpty(group)){
			condition.andIn("pd1.party_id", group);
		}
		if(UtilValidate.isNotEmpty(getSqlDate(fromDate))){
			condition.and("dd2.date_Value", ">=", getSqlDate(fromDate));
		}
		if(UtilValidate.isNotEmpty(getSqlDate(thruDate))){
			condition.and("dd2.date_value", "<=", getSqlDate(thruDate));
		}
		
		condition1.and("dd1.year_month_day = dd2.year_month_day", flag.equals("DAY"));
		condition1.and("dd1.year_and_month = dd2.year_and_month", (flag.equals("") || flag.equals("MONTH")));
		condition1.and("dd1.week_and_year = dd2.week_and_year", flag.equals("WEEK"));
		condition1.and("dd1.quarter_and_year = dd2.quarter_and_year", flag.equals("QUARTER"));
		condition1.and("dd1.year_name = dd2.year_name", flag.equals("YEAR"));
		
		condition1.or("dd.year_month_day = dd2.year_month_day", flag.equals("DAY"));
		condition1.or("dd.year_and_month = dd2.year_and_month", (flag.equals("") || flag.equals("MONTH")));
		condition1.or("dd.week_and_year = dd2.week_and_year", flag.equals("WEEK"));
		condition1.or("dd.quarter_and_year = dd2.quarter_and_year", flag.equals("QUARTER"));
		condition1.or("dd.year_name = dd2.year_name", flag.equals("YEAR"));
		condition.and(condition1);
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null){
			initQuery();
		}
		return query;
	}
	
	public class PartyFluctColumn implements OlapResultQueryInterface{

		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String,Object>>();
			tmp.put("IN", new HashMap<String, Object>());
			tmp.put("OUT", new HashMap<String, Object>());
			try {
				ResultSet resultSet = query.getResultSet();
				Map<String, Object> maptmp = FastMap.newInstance();
				Map<String, Object> maptmp1 = FastMap.newInstance();
				while(resultSet.next()){
					String fromDate = resultSet.getString("fromDate");
					String thruDate = resultSet.getString("thruDate");
					String curDate = resultSet.getString("curDate");
					
					long count = resultSet.getLong("count");
					if(fromDate.equals(curDate)){
						maptmp.put(curDate, count);
					}else if(thruDate.equals(curDate)){
						maptmp1.put(curDate, count);
					}
				}
				tmp.get("IN").putAll(maptmp);
				tmp.get("OUT").putAll(maptmp1);
			} catch (GenericDataSourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return tmp;
		}
		
	} 
	public class PartyFluctColumnOut extends AbstractOlapChart{

		public PartyFluctColumnOut(OlapInterface olap, OlapResultQueryInterface query) {
			super(olap, query);
		}
		public String formatStringIntoSqlDateString(String s, String dateType){
			String name = "";
			SimpleDateFormat month_year = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
			SimpleDateFormat month_year_day = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			
			Date date = null;
			try {
				date = sdf.parse(s);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(dateType.equals("year_and_month")){
				name = month_year.format(date);
			}else if(dateType.equals("year_month_day")){
				name = month_year_day.format(date);
			}else if(dateType.equals("week_and_year")){
				String[] tmp = s.split("-");
				name = "Week " + tmp[1] + ", " + tmp[0]; 
			}else if(dateType.equals("quarter_and_year")){
				String[] tmp = s.split("-");
				name = "Quarter " + tmp[1] + ", " + tmp[0];
 			}else if(dateType.equals("year_name")){
 				name = s;
 			}
			
			return name;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void result(Object object) {
			Map<String, Map<String, Object>> map = (Map<String, Map<String,Object>>) object;
			try {
				OlapDate olapDate = new OlapDate();
				olapDate.SQLProcessor(getSQLProcessor());
				olapDate.setFromDate(getSqlDate(fromDate));
				olapDate.setThruDate(getSqlDate(thruDate));
				if(UtilValidate.isNotEmpty(dateType)){
					xAxis = olapDate.getValues(dateType);
				}
				yAxis = new TreeMap<String, List<Object>>();
				
				for (String key : map.keySet()) {
					if(yAxis.get(key) == null) {
						yAxis.put(key, new ArrayList<Object>());
					}
				}
				for (String s : xAxis) {
					for(String key : map.keySet()){
						if(key != null){
							if(map.get(key).get(s) != null){
								yAxis.get(key).add(map.get(key).get(s));
							}else{
								yAxis.get(key).add(new Integer(0));
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
