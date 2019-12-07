package com.olbius.basehr.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.olap.AbstractOlap;

public class HROlapImpl extends AbstractOlap implements PartyOlap, HROlap{

	private String group;
	private Locale locale;
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	@Override
	public void personBirth(boolean gender) throws GenericDataSourceException,
			GenericEntityException, SQLException {
		String sql = "SELECT YEAR(NOW()) - YEAR(birth_date_dimension.date_value) AS _age, count(DISTINCT person_dim_id) AS _count, %GENDER% FROM person_relationship_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id"
				+ " AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM person_relationship_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)"
				+ " INNER JOIN party_dimension ON person_dim_id = party_dimension.dimension_id"
				+ " INNER JOIN date_dimension AS birth_date_dimension ON birth_date_dim_id = birth_date_dimension.dimension_id"
				+ " %JOIN_GROUP%"
				+ " WHERE role_type_id = 'EMPLOYEE' %AND_GENDER% %AND_GROUP%"
				+ " GROUP BY _age, %_GENDER% ORDER BY _age, %_GENDER%";
		if(gender) {
			sql = sql.replaceAll("%GENDER%", "gender, CASE WHEN gender=\'M\' THEN \'Male\' WHEN gender=\'F\' THEN \'Female\' "
					+ "WHEN gender IS NULL THEN \'Other\' ELSE gender END AS _gender");
			sql = sql.replaceAll("%AND_GENDER%", "AND gender is NOT NULL AND birth_date_dimension.date_value is NOT NULL");
			sql = sql.replaceAll("%_GENDER%", "gender");
		} else {
			sql = sql.replaceAll(", %GENDER%", "");
			sql = sql.replaceAll(" %AND_GENDER%", "");
			sql = sql.replaceAll(", %_GENDER%", "");
		}
		if(group != null && !group.isEmpty()) {
			sql = sql.replaceAll("%JOIN_GROUP%", "INNER JOIN party_dimension ON group_dim_id = party_dimension.dimension_id");
			sql = sql.replaceAll("%AND_GROUP%", "AND party_dimension.party_id = ?");
		} else {
			sql = sql.replaceAll(" %JOIN_GROUP%", "");
			sql = sql.replaceAll(" %AND_GROUP%", "");
		}
		getSQLProcessor().prepareStatement(sql);
		
		if(group!=null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
			group = null;
		}
		
		getSQLProcessor().executeQuery();
		
		ResultSet resultSet = getSQLProcessor().getResultSet();
		
		Map<String, Map<String, Integer>> map = new HashMap<String, Map<String,Integer>>();
		
		Map<String, Integer> map2 = new HashMap<String, Integer>();
		
		xAxis = new ArrayList<String>();
		
		yAxis = new TreeMap<String, List<Object>>();
		
		String maleLabel = UtilProperties.getMessage("PartyOlapUiLabels", "gender_male", locale);
		String femaleLabel = UtilProperties.getMessage("PartyOlapUiLabels", "gender_female", locale);
		String otherLabel = UtilProperties.getMessage("PartyOlapUiLabels", "other", locale);
		
		if(gender) {
			while(resultSet.next()) {
				String _gender = resultSet.getString("_gender");
				if(_gender.equals("Female")) {
					_gender = femaleLabel;
				} else if(_gender.equals("Male")) {
					_gender = maleLabel;
				} else if(_gender.equals("Other")) {
					_gender = otherLabel;
				}
				if(map.get(_gender)==null) {
					map.put(_gender, new HashMap<String, Integer>());
				}
				String age = Integer.toString(resultSet.getInt("_age"));
				map.get(_gender).put(age, resultSet.getInt("_count"));
				if(xAxis.isEmpty() ||  !xAxis.get(xAxis.size()-1).equals(age)) {
					xAxis.add(age);
				}
			}
			for(String key : map.keySet()) {
				if(key != null) {
					yAxis.put(key, new ArrayList<Object>());
					for(String s: xAxis) {
						if(map.get(key).get(s) == null) {
							yAxis.get(key).add(new Integer(0));
						} else {
							if(key.equals(femaleLabel)) {
								yAxis.get(key).add(-map.get(key).get(s));
							} else {
								yAxis.get(key).add(map.get(key).get(s));
							}
						}
					}
				}
			}
		} else {
			while(resultSet.next()) {
				String age = Integer.toString(resultSet.getInt("_age"));
				if(age.equals("0")) {
					age = otherLabel;
				}
				map2.put(age, resultSet.getInt("_count"));
				xAxis.add(age);
			}
			for(String key : map2.keySet()) {
				if(key != null) {
					yAxis.put(key, new ArrayList<Object>());
					yAxis.get(key).add(map2.get(key));
				}
			}
		}
		
	}
	
	@Override
	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public void setGroup(List<?> list, boolean olap, Delegator delegator)
			throws GenericEntityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gender() throws GenericDataSourceException,
			GenericEntityException, SQLException {
		String sql = "SELECT count(DISTINCT person_dim_id) AS _count, CASE WHEN gender=\'M\' THEN \'Male\' WHEN gender=\'F\' THEN \'Female\'"
				+ " WHEN gender IS NULL THEN \'Other\' ELSE gender END AS _gender FROM person_relationship_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id"
				+ " AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM person_relationship_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)"
				+ " INNER JOIN party_dimension ON person_dim_id = party_dimension.dimension_id"
				+ " %JOIN_GROUP%"
				+ " WHERE role_type_id = 'EMPLOYEE' %AND_GROUP%"
				+ " GROUP BY _gender ORDER BY _gender";
		if(group != null && !group.isEmpty()) {
			sql = sql.replaceAll("%JOIN_GROUP%", "INNER JOIN party_dimension ON group_dim_id = party_dimension.dimension_id");
			sql = sql.replaceAll("%AND_GROUP%", "AND party_dimension.party_id = ?");
		} else {
			sql = sql.replaceAll(" %JOIN_GROUP%", "");
			sql = sql.replaceAll(" %AND_GROUP%", "");
		}
		
		getSQLProcessor().prepareStatement(sql);
		
		if(group!=null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
			group = null;
		}
		
		getSQLProcessor().executeQuery();
		
		ResultSet resultSet = getSQLProcessor().getResultSet();
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		xAxis = new ArrayList<String>();
		
		String maleLabel = UtilProperties.getMessage("PartyOlapUiLabels", "gender_male", locale);
		String femaleLabel = UtilProperties.getMessage("PartyOlapUiLabels", "gender_female", locale);
		String otherLabel = UtilProperties.getMessage("PartyOlapUiLabels", "other", locale);
		
		while(resultSet.next()) {
			String gender = resultSet.getString("_gender");
			if(gender.equals("Female")) {
				gender = femaleLabel;
			} else if(gender.equals("Male")) {
				gender = maleLabel;
			} else if(gender.equals("Other")) {
				gender = otherLabel;
			}
			int count = resultSet.getInt("_count");
			map.put(gender, count);
			xAxis.add(gender);
		}
		
		yAxis = new HashMap<String, List<Object>>();
		
		for(String key : map.keySet()) {
			if(key != null) {
				yAxis.put(key, new ArrayList<Object>());
				yAxis.get(key).add(map.get(key));
			}
		}
	}

	@Override
	public void member(String dateType, boolean cur, List<?> groups)
			throws GenericDataSourceException, GenericEntityException,
			SQLException {
		
		dateType = getDateType(dateType);
		
		String sql = "SELECT count(DISTINCT person_dim_id) AS _count, %DATE_TYPE%, party_dimension.party_id FROM person_relationship_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id"
				+ " %AND_CUR%"
				+ " INNER JOIN party_dimension ON group_dim_id = party_dimension.dimension_id"
				+ " WHERE role_type_id = 'EMPLOYEE' %AND_GROUP% %AND_DATE%"
				+ " GROUP BY %DATE_TYPE%, party_dimension.party_id ORDER BY %DATE_TYPE%, party_dimension.party_id";
		
		if(group != null && !group.isEmpty()) {
			sql = sql.replaceAll("%AND_GROUP%", "AND party_dimension.party_id = ?");
		} else if(groups != null && !groups.isEmpty()) {
			String s = "AND party_dimension.party_id IN (";
			for(int i = 0; i < groups.size(); i++) {
				s+= "?";
				if(i < groups.size()-1) {
					s+=",";
				} else {
					s+=")";
				}
			}
			sql = sql.replaceAll("%AND_GROUP%", s);
		} else {
			sql = sql.replaceAll(" %AND_GROUP%", "");
		}
		
		sql = sql.replaceAll("%DATE_TYPE%", dateType);
		
		if(cur) {
			sql = sql.replaceAll("%AND_CUR%", "AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM person_relationship_fact"
					+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)");
			sql = sql.replaceAll(" %AND_DATE%", "");
		} else {
			sql = sql.replaceAll(" %AND_CUR%", "");
			sql = sql.replaceAll("%AND_DATE%", "AND (date_dimension.date_value BETWEEN ? AND ?)");
			
		}
		
		getSQLProcessor().prepareStatement(sql);
		
		if(group!=null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
			group = null;
		} else if(groups != null && !groups.isEmpty()) {
			for(Object obj : groups) {
				getSQLProcessor().setValue((String)obj);
			}
		}
		
		if(!cur) {
			getSQLProcessor().setValue(getSqlDate(fromDate));
			getSQLProcessor().setValue(getSqlDate(thruDate));
		}
		
		getSQLProcessor().executeQuery();
		
		ResultSet resultSet = getSQLProcessor().getResultSet();
		
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>();
		
		while(resultSet.next()) {
			String party = resultSet.getString("party_id");
			if(map.get(party)==null) {
				map.put(party, new HashMap<String, Object>());
			}
			map.get(party).put(resultSet.getString(dateType), resultSet.getInt("_count"));
		}
		if(cur) {
			
			xAxis = new ArrayList<String>();
			
			yAxis = new HashMap<String, List<Object>>();
			
			yAxis.put("member", new ArrayList<Object>());
			
			for(String key : map.keySet()) {
				xAxis.add(key);
				if(!map.get(key).isEmpty()) {
					for(String s : map.get(key).keySet()) {
						if(map.get(key).get(s)!= null) {
							yAxis.get("member").add(map.get(key).get(s));
						} else {
							yAxis.get("member").add(new Integer(0));
						}
					}
				}
			}
		} else {
			axis(map, dateType);
		}
	}
	
	@Override
	public void personOlap(String dateType, boolean ft)
			throws GenericDataSourceException, GenericEntityException,
			SQLException {
		
		dateType = getDateType(dateType);
		
		String sql = "SELECT count(DISTINCT person_dim_id) AS _count, %DATE_TYPE%, party_dimension.party_id FROM party_person_relationship"
				+ " INNER JOIN date_dimension ON %DATE_ID% = date_dimension.dimension_id"
				+ " INNER JOIN party_dimension ON group_dim_id = party_dimension.dimension_id"
				+ " WHERE role_type_id = 'EMPLOYEE' AND (date_dimension.date_value BETWEEN ? AND ?) %AND_GROUP%"
				+ " GROUP BY %DATE_TYPE%, party_dimension.party_id ORDER BY %DATE_TYPE%, party_dimension.party_id";
		if(group != null && !group.isEmpty()) {
			sql = sql.replaceAll("%AND_GROUP%", "AND party_dimension.party_id = ?");
		} else {
			sql = sql.replaceAll(" %AND_GROUP%", "");
		}
		
		sql = sql.replaceAll("%DATE_TYPE%", dateType);
		
		if(ft) {
			sql = sql.replaceAll("%DATE_ID%", "from_date_dim_id");
		} else {
			sql = sql.replaceAll("%DATE_ID%", "thru_date_dim_id");
		}
		
		getSQLProcessor().prepareStatement(sql);
		
		getSQLProcessor().setValue(getSqlDate(fromDate));
		getSQLProcessor().setValue(getSqlDate(thruDate));
		
		if(group!=null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
			group = null;
		}
		
		getSQLProcessor().executeQuery();
		
		ResultSet resultSet = getSQLProcessor().getResultSet();
		
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String,Object>>();
		
		while(resultSet.next()) {
			String party = resultSet.getString("party_id");
			if(map.get(party)==null) {
				map.put(party, new HashMap<String, Object>());
			}
			map.get(party).put(resultSet.getString(dateType), resultSet.getInt("_count"));
		}
		
		axis(map, dateType);
	}

	private String getSchoolType(String type) {
		if(type == null || type.isEmpty() || type.equals(SCHOOL)) {
			type = "school_id";
		}
		if(type.equals(EDU_SYS)) {
			type = "education_system_type_id";
		}
		if(type.equals(STUDY_MODE)) {
			type = "study_mode_type_id";
		}
		if(type.equals(MAJOR)) {
			type = "major_id";
		}
		if(type.equals(CLASSIFICATION)) {
			type = "classification_type_id";
		}
		return type;
	}
	
	@Override
	public void school(String type) throws GenericDataSourceException,
			GenericEntityException, SQLException {
		
		type = getSchoolType(type);
		
		String sql = "SELECT count(DISTINCT person_dim_id) AS _count, CASE WHEN %SCH% IS NULL THEN \'Other\' ELSE %SCH% END AS _school"
				+ " FROM person_relationship_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id"
				+ " AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM person_relationship_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)"
				+ " INNER JOIN party_dimension ON person_dim_id = party_dimension.dimension_id"
				+ " %JOIN_GROUP%"
				+ " WHERE role_type_id = 'EMPLOYEE' %AND_GROUP%"
				+ " GROUP BY _school ORDER BY _school";
		if(group != null && !group.isEmpty()) {
			sql = sql.replaceAll("%JOIN_GROUP%", "INNER JOIN party_dimension ON group_dim_id = party_dimension.dimension_id");
			sql = sql.replaceAll("%AND_GROUP%", "AND party_dimension.party_id = ?");
		} else {
			sql = sql.replaceAll(" %JOIN_GROUP%", "");
			sql = sql.replaceAll(" %AND_GROUP%", "");
		}
		
		sql = sql.replaceAll("%SCH%", type);
		
		getSQLProcessor().prepareStatement(sql);
		
		if(group!=null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
			group = null;
		}
		
		getSQLProcessor().executeQuery();
		
		ResultSet resultSet = getSQLProcessor().getResultSet();
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		xAxis = new ArrayList<String>();
		
		String otherLabel = UtilProperties.getMessage("PartyOlapUiLabels", "other", locale);
		
		while(resultSet.next()) {
			String tmp = resultSet.getString("_school");
			if(tmp.equals("Other")) {
				tmp = otherLabel;
			}
			int count = resultSet.getInt("_count");
			map.put(tmp, count);
			xAxis.add(tmp);
		}
		
		yAxis = new HashMap<String, List<Object>>();
		
		for(String key : map.keySet()) {
			if(key != null) {
				yAxis.put(key, new ArrayList<Object>());
				yAxis.get(key).add(map.get(key));
			}
		}
	}

	@Override
	public void position(String type) throws GenericDataSourceException,
			GenericEntityException, SQLException {
		
		type = getPositionType(type);
		
		String sql = "SELECT count(DISTINCT empl_position_fact.party_dim_id) AS _count, %TYPE% FROM empl_position_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id"
				+ " AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM empl_position_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)"
				+ " INNER JOIN empl_position_dimension ON empl_position_dim_id = empl_position_dimension.dimension_id"
				+ " INNER JOIN person_relationship_fact ON empl_position_fact.party_dim_id = person_relationship_fact.person_dim_id"
				+ " AND empl_position_fact.date_dim_id = person_relationship_fact.date_dim_id"
				+ " %JOIN_GROUP%"
				+ " WHERE role_type_id = 'EMPLOYEE' %AND_GROUP%"
				+ " GROUP BY %TYPE% ORDER BY %TYPE%";
		if(group != null && !group.isEmpty()) {
			sql = sql.replaceAll("%JOIN_GROUP%", "INNER JOIN party_dimension ON group_dim_id = party_dimension.dimension_id");
			sql = sql.replaceAll("%AND_GROUP%", "AND party_dimension.party_id = ?");
		} else {
			sql = sql.replaceAll(" %JOIN_GROUP%", "");
			sql = sql.replaceAll(" %AND_GROUP%", "");
		}

		sql = sql.replaceAll("%TYPE%", type);
		
		getSQLProcessor().prepareStatement(sql);
		
		if(group!=null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
			group = null;
		}
		
		getSQLProcessor().executeQuery();
		
		ResultSet resultSet = getSQLProcessor().getResultSet();
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		xAxis = new ArrayList<String>();
		
		while(resultSet.next()) {
			String tmp = resultSet.getString(type);
			int count = resultSet.getInt("_count");
			map.put(tmp, count);
			xAxis.add(tmp);
		}
		
		yAxis = new HashMap<String, List<Object>>();
		
		for(String key : map.keySet()) {
			if(key != null) {
				yAxis.put(key, new ArrayList<Object>());
				yAxis.get(key).add(map.get(key));
			}
		}
	}
	
	private String getPositionType(String type) {
		if(type == null || type.isEmpty() || type.equals(POSITION)) {
			type = "empl_position_id";
		}
		if(type.equals(POSITION_TYPE)) {
			type = "empl_position_type_id";
		}
		return type;
	}

	private String getTimeTrackerId(String id) {
		if(id == null || id.isEmpty() || id.equals("START")) {
			id = "start_time";
		} else if(id.equals("END")) {
			id = "end_time";
		}
		return id;
	}
	
	private String[] getTimeTrackerId(String id, String[] _id) {
		String[] strings = new String[2];
		if(id == null || id.isEmpty() || id.equals("START")) {
			strings[0] = "start_time";
			strings[1] = _id[0];
		} else if(id.equals("END")) {
			strings[0] = "end_time";
			strings[1] = _id[1];
		}
		return strings;
	}
	
	@Override
	public void timeTracker(String timeId) throws GenericDataSourceException,
			GenericEntityException, SQLException {
		
		timeId = getTimeTrackerId(timeId);
		
		String sql = "SELECT max(%TIME_ID%) AS _max, min(%TIME_ID%) AS _min, CAST(avg(%TIME_ID%) AS time) AS _avg, year_month_day"
				+ " FROM attendance_tracker_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id"
				+ " %JOIN_GROUP%"
				+ " WHERE (date_dimension.date_value BETWEEN ? AND ?) %AND_GROUP%"
				+ " GROUP BY year_month_day ORDER BY year_month_day";
		if(group != null && !group.isEmpty()) {
			sql = sql.replaceAll("%JOIN_GROUP%", "INNER JOIN person_relationship_fact ON attendance_tracker_fact.party_dim_id = person_relationship_fact.person_dim_id"
					+ " AND attendance_tracker_fact.date_dim_id = person_relationship_fact.date_dim_id"
					+ " INNER JOIN party_dimension ON person_relationship_fact.group_dim_id = party_dimension.dimension_id");
			sql = sql.replaceAll("%AND_GROUP%", "AND party_dimension.party_id = ?");
		} else {
			sql = sql.replaceAll(" %JOIN_GROUP%", "");
			sql = sql.replaceAll(" %AND_GROUP%", "");
		}
		
		sql = sql.replaceAll("%TIME_ID%", timeId);
		
		getSQLProcessor().prepareStatement(sql);
		
		getSQLProcessor().setValue(getSqlDate(fromDate));
		getSQLProcessor().setValue(getSqlDate(thruDate));
		
		if(group!=null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
			group = null;
		}
		
		getSQLProcessor().executeQuery();
		
		ResultSet resultSet = getSQLProcessor().getResultSet();
		
		xAxis = new ArrayList<String>();
		
		yAxis = new HashMap<String, List<Object>>();
		
		while(resultSet.next()) {
			Time max = resultSet.getTime("_max");
			Time min = resultSet.getTime("_min");
			Time avg = resultSet.getTime("_avg");
			String date = resultSet.getString("year_month_day");
			
			xAxis.add(date);
			
			List<Object> list = new ArrayList<Object>();
			
			list.add(date);
			list.add(avg.toString());
			
			List<Object> list2 = new ArrayList<Object>();
			
			list2.add(date);
			list2.add(min.toString());
			list2.add(max.toString());
			
			if(yAxis.get("averages") == null) {
				yAxis.put("averages", new ArrayList<Object>());
			}
			
			yAxis.get("averages").add(list);
			
			if(yAxis.get("ranges") == null) {
				yAxis.put("ranges", new ArrayList<Object>());
			}
			
			yAxis.get("ranges").add(list2);
			
		}
		
	}

	@Override
	public void onTime(String timeId) throws GenericDataSourceException,
			GenericEntityException, SQLException {
		
		String[] _time = getTimeTrackerId(timeId, new String[]{"s_time", "e_time"});
		
		String sql = "SELECT COUNT(party_dim_id) AS _count, CASE WHEN %_TIME_ID% > %TIME_ID% THEN \'early\'"
				+ " WHEN %_TIME_ID% < %TIME_ID% THEN \'last\' ELSE \'on\' END AS _status, year_month_day"
				+ " FROM attendance_tracker_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id"
				+ " %JOIN_GROUP%"
				+ " WHERE (date_dimension.date_value BETWEEN ? AND ?) %AND_GROUP%"
				+ " GROUP BY year_month_day, _status ORDER BY year_month_day";
		if(group != null && !group.isEmpty()) {
			sql = sql.replaceAll("%JOIN_GROUP%", "INNER JOIN person_relationship_fact ON attendance_tracker_fact.party_dim_id = person_relationship_fact.person_dim_id"
					+ " AND attendance_tracker_fact.date_dim_id = person_relationship_fact.date_dim_id"
					+ " INNER JOIN party_dimension ON person_relationship_fact.group_dim_id = party_dimension.dimension_id");
			sql = sql.replaceAll("%AND_GROUP%", "AND party_dimension.party_id = ?");
		} else {
			sql = sql.replaceAll(" %JOIN_GROUP%", "");
			sql = sql.replaceAll(" %AND_GROUP%", "");
		}
		
		sql = sql.replaceAll("%TIME_ID%", _time[0]);
		sql = sql.replaceAll("%_TIME_ID%", _time[1]);
		
		getSQLProcessor().prepareStatement(sql);
		
		getSQLProcessor().setValue(getSqlDate(fromDate));
		getSQLProcessor().setValue(getSqlDate(thruDate));
		
		if(group!=null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
			group = null;
		}
		
		getSQLProcessor().executeQuery();
		
		ResultSet resultSet = getSQLProcessor().getResultSet();
		
		xAxis = new ArrayList<String>();
		
		yAxis = new HashMap<String, List<Object>>();
		
		while(resultSet.next()) {
			int count = resultSet.getInt("_count");
			String status = resultSet.getString("_status");
			String date = resultSet.getString("year_month_day");
			
			if(!xAxis.contains(date)) {
				xAxis.add(date);
			}
			
			if(yAxis.get(status) == null) {
				yAxis.put(status, new ArrayList<Object>());
			}
			yAxis.get(status).add(count);
		}
	}

	
	@Override
	public void salaryStructure(String party_person) throws GenericDataSourceException,
			GenericEntityException, SQLException {

        String sql =  "SELECT "
                    +       "code_name AS _type, "
                    +       "SUM(_value) AS _sum "
                    + "FROM fact_payroll AS fp "
                    +	    "INNER JOIN date_dimension AS dd "
                    +           "ON dd.dimension_id = fp.dim_from_date_id "
                    +       "%JOIN_PERSON%"
                    +       "%JOIN_GROUP%"
                    + "WHERE "
                    +       "(dd.date_value BETWEEN ? AND ?) "
                    +       "AND payroll_characteristic_id=? "
                    + 	    "AND fp.period_type_id=? "
                    +	    "%WHERE_PERSON%"
                    +	    "%WHERE_GROUP%"
                    + "GROUP BY code_name"
                    ;

        if(party_person != null && !party_person.isEmpty()) {
            sql = sql.replaceAll("%JOIN_PERSON%",
                    "INNER JOIN party_dimension as ppd "
                    +   "ON ppd.dimension_id = fp.dim_party_person_id ");

            sql = sql.replaceAll("%WHERE_PERSON%", "AND ppd.party_id = ? ");

            sql = sql.replaceAll("%JOIN_GROUP%", "");
            sql = sql.replaceAll("%WHERE_GROUP%", "");
        } else {
            sql = sql.replaceAll("%JOIN_PERSON%", "");
            sql = sql.replaceAll("%WHERE_PERSON%", "");

            if (group != null && !group.isEmpty()) {
                sql = sql.replaceAll("%JOIN_GROUP%",
                        "INNER JOIN person_relationship_fact AS prf "
                        +   "ON prf.person_dim_id = fp.dim_party_person_id "
                        +   "AND prf.date_dim_id = fp.dim_from_date_id "
                        + "INNER JOIN party_dimension AS pgd "
                        +   "ON pgd.dimension_id = prf.group_dim_id ");

                sql = sql.replaceAll("%WHERE_GROUP%", "AND pgd.party_id = ? ");

            } else {
                sql = sql.replaceAll("%JOIN_GROUP%", "");
                sql = sql.replaceAll("%WHERE_GROUP%", "");
            }
        }

        getSQLProcessor().prepareStatement(sql);

        // set date parameter
        getSQLProcessor().setValue(getSqlDate(fromDate));
        getSQLProcessor().setValue(getSqlDate(thruDate));

        // fixed payroll_characteristic_id='INCOME'
        getSQLProcessor().setValue("INCOME");
        
        // fixed period_type_id = 'MONTHLY'
        getSQLProcessor().setValue("MONTHLY");


        if(party_person != null && !party_person.isEmpty()) {
            getSQLProcessor().setValue(party_person);
        } else if(group!=null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
			group = null;
		}
		
		getSQLProcessor().executeQuery();
		
		ResultSet resultSet = getSQLProcessor().getResultSet();
		
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		
		xAxis = new ArrayList<String>();
		
		while(resultSet.next()) {
			String _type = resultSet.getString("_type");
			BigDecimal _sum = resultSet.getBigDecimal("_sum");
			map.put(_type, _sum);
			xAxis.add(_type);
		}
		
		yAxis = new HashMap<String, List<Object>>();
		
		for(String key : map.keySet()) {
			if(key != null) {
				yAxis.put(key, new ArrayList<Object>());
				yAxis.get(key).add(map.get(key));
			}
		}
	}
	
	@Override
	public void salaryRange() throws GenericDataSourceException,
			GenericEntityException, SQLException {

		String sql = "SELECT "
				+ "MAX(sum_income) AS _max, "
				+ "MIN(sum_income) AS _min, "
				+ "AVG(sum_income) AS _avg, "
				+ "year_month_day "
				+ "FROM ( "
				+ "SELECT "
				+ "SUM(_value) AS sum_income, "
				+ "year_month_day "
				+ "FROM fact_payroll "
				+ "INNER JOIN date_dimension ON dim_from_date_id = date_dimension.dimension_id "
				+ "%JOIN_GROUP%"
				+ "WHERE "
				+ "(date_dimension.date_value BETWEEN ? AND ?) "
				+ "AND payroll_characteristic_id=? "
				+ "AND period_type_id=? "
				+ "%AND_GROUP%"
				+ "GROUP BY dim_party_person_id, year_month_day "
				+ ") AS sum_income_per_person "
				+ "GROUP BY year_month_day ";

		if (group != null && !group.isEmpty()) {
			sql = sql.replaceAll("%JOIN_GROUP%",
					"INNER JOIN person_relationship_fact "
							+ "ON fact_payroll.dim_party_person_id = person_relationship_fact.person_dim_id "
							+ "AND fact_payroll.dim_from_date_id = person_relationship_fact.date_dim_id "
							+ " INNER JOIN party_dimension "
							+ "ON person_relationship_fact.group_dim_id = party_dimension.dimension_id ");

			sql = sql.replaceAll("%AND_GROUP%", "AND party_dimension.party_id = ? ");

		} else {
			sql = sql.replaceAll("%JOIN_GROUP%", "");
			sql = sql.replaceAll("%AND_GROUP%", "");
		}

		getSQLProcessor().prepareStatement(sql);

		// set date parameter
		getSQLProcessor().setValue(getSqlDate(fromDate));
		getSQLProcessor().setValue(getSqlDate(thruDate));

		// fixed payroll_characteristic_id='INCOME'
		getSQLProcessor().setValue("INCOME");

		// fixed period_type_id = 'MONTHLY'
		getSQLProcessor().setValue("MONTHLY");


		if (group != null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
			group = null;
		}

		getSQLProcessor().executeQuery();

		ResultSet resultSet = getSQLProcessor().getResultSet();

		xAxis = new ArrayList<String>();

		yAxis = new HashMap<String, List<Object>>();

		while (resultSet.next()) {
			BigDecimal max = resultSet.getBigDecimal("_max");
			BigDecimal min = resultSet.getBigDecimal("_min");
			BigDecimal avg = resultSet.getBigDecimal("_avg");
			String date = resultSet.getString("year_month_day");

			xAxis.add(date);

			List<Object> list = new ArrayList<Object>();

			list.add(date);
			list.add(avg);

			List<Object> list2 = new ArrayList<Object>();

			list2.add(date);
			list2.add(min);
			list2.add(max);

			if (yAxis.get("averages") == null) {
				yAxis.put("averages", new ArrayList<Object>());
			}

			yAxis.get("averages").add(list);

			if (yAxis.get("ranges") == null) {
				yAxis.put("ranges", new ArrayList<Object>());
			}

			yAxis.get("ranges").add(list2);

		}
	}

	@Override
	public void salaryRangeByPosition(String dateType) throws GenericDataSourceException,
			GenericEntityException, SQLException {

		dateType = getDateType(dateType);

		String sql = "SELECT "
					+     "MAX(sum_income) AS _max, "
					+     "MIN(sum_income) AS _min, "
					+     "AVG(sum_income) AS _avg, "
					+     "empl_position_type_id "
					+ "FROM ( "
					+     "SELECT "
					+         "SUM(_value) AS sum_income, "
					+		  "epd.empl_position_type_id "
					+     "FROM fact_payroll AS fp "
					+	  "INNER JOIN date_dimension AS dd "
                    +       "ON dd.dimension_id = fp.dim_from_date_id "
//                      join: empl_position_fact
                    +     "INNER JOIN empl_position_fact AS epf "
                    +       "ON epf.party_dim_id = fp.dim_party_person_id "
                    +       "AND epf.date_dim_id = fp.dim_from_date_id "
                    +     "INNER JOIN empl_position_dimension AS epd "
                    +       "ON epd.dimension_id = epf.empl_position_dim_id "
//                      join: person_relationship_fact
                    +     "%JOIN_GROUP%"
					+     "WHERE "
					+         "(dd.date_value BETWEEN ? AND ?) "
					+     "AND fp.payroll_characteristic_id=? "
					+ 	  "AND fp.period_type_id=? "
					+	  "%WHERE_GROUP%"
					+     "GROUP BY fp.dim_party_person_id, epd.empl_position_type_id "
					+ ") AS sum_income_per_person "
					+ "GROUP BY empl_position_type_id "
					;

		if(group != null && !group.isEmpty()) {
			sql = sql.replaceAll("%JOIN_GROUP%",
                    "INNER JOIN person_relationship_fact AS prf "
					+ 		"ON prf.person_dim_id = fp.dim_party_person_id "
					+ 		"AND prf.date_dim_id = fp.dim_from_date_id "
					+ "INNER JOIN party_dimension as pgd "
					+ 		"ON pgd.dimension_id  = prf.group_dim_id ");

			sql = sql.replaceAll("%WHERE_GROUP%", "AND pgd.party_id = ? ");

		} else {
			sql = sql.replaceAll("%JOIN_GROUP%", "");
			sql = sql.replaceAll("%WHERE_GROUP%", "");
		}

		getSQLProcessor().prepareStatement(sql);

		// set date parameter
		getSQLProcessor().setValue(getSqlDate(fromDate));
		getSQLProcessor().setValue(getSqlDate(thruDate));

		// fixed payroll_characteristic_id='INCOME'
		getSQLProcessor().setValue("INCOME");

		// fixed period_type_id = 'MONTHLY'
		getSQLProcessor().setValue("MONTHLY");


		if(group!=null && !group.isEmpty()) {
			getSQLProcessor().setValue(group);
			group = null;
		}

		getSQLProcessor().executeQuery();

		ResultSet resultSet = getSQLProcessor().getResultSet();

		xAxis = new ArrayList<String>();

		yAxis = new HashMap<String, List<Object>>();

        List<BigDecimal> max = new ArrayList<BigDecimal>();
        List<BigDecimal> min = new ArrayList<BigDecimal>();
        List<BigDecimal> avg = new ArrayList<BigDecimal>();

		while(resultSet.next()) {
            BigDecimal _max = resultSet.getBigDecimal("_max");
            BigDecimal _min = resultSet.getBigDecimal("_min");
            BigDecimal _avg = resultSet.getBigDecimal("_avg");

            String empl_position_type_id = resultSet.getString("empl_position_type_id");

			xAxis.add(empl_position_type_id);

            max.add(_max);
            min.add(_min);
            avg.add(_avg);
		}

        yAxis.put("max", new ArrayList<Object>());
        yAxis.get("max").addAll(max);

        yAxis.put("min", new ArrayList<Object>());
        yAxis.get("min").addAll(min);

        yAxis.put("avg", new ArrayList<Object>());
        yAxis.get("avg").addAll(avg);

	}
}
