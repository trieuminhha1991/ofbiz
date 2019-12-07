package com.olbius.bi.olap;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.ofbiz.entity.model.ModelUtil;

import com.olbius.bi.olap.query.OlbiusQueryInterface;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

/**
 * @author Nguyen Ha
 *
 */
public class TypeOlap {

	public static final String DATE_TYPE = "DATE_TYPE";

	protected java.sql.Date getSqlDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		java.sql.Date sqlDate = new java.sql.Date(calendar.getTimeInMillis());
		return sqlDate;
	}
	
	protected Timestamp getSqlFromDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Timestamp sqlDate = new Timestamp(calendar.getTimeInMillis());
		return sqlDate;
	}
	
	protected Timestamp getSqlThruDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Timestamp sqlDate = new Timestamp(calendar.getTimeInMillis());
		return sqlDate;
	}

	protected String getDateType(String dateType) {
		if (dateType == null || dateType.isEmpty() || dateType.equals(OlapInterface.DAY)) {
			dateType = "year_month_day";
		}
		if (dateType.equals(OlapInterface.MONTH)) {
			dateType = "year_and_month";
		}
		if (dateType.equals(OlapInterface.YEAR)) {
			dateType = "year_name";
		}
		if (dateType.equals(OlapInterface.WEEK)) {
			dateType = "week_and_year";
		}
		if (dateType.equals(OlapInterface.QUARTER)) {
			dateType = "quarter_and_year";
		}
		return dateType;
	}

	protected String dot(String table, String column) {
		return dot(table, column, false);
	}

	protected String dot(String table, String column, boolean flag) {
		return entity(table, flag).concat(".").concat(entity(column, flag));
	}

	protected String entity(String name) {
		return entity(name, true);
	}

	protected String entity(String name, boolean flag) {
		if (flag) {
			return ModelUtil.javaNameToDbName(name);
		}
		return name;
	}

	protected Condition eq(String s, String s2) {
		return Condition.make(s + Condition.EQ + s2);
	}

	protected void joinDimension(OlbiusQueryInterface query, String column, String dimension, String as, boolean flag, boolean entity, Object... condition) {
		
		Condition cond = eq(dot(query.getFrom(), entity(column, entity)), dot(entity(dimension, entity), "dimension_id"));
		
		for(int i = 0; i + 2 < condition.length; i = i + 3) {
			
			String col = (String) condition[i];
			
			String op = (String) condition[i+1];
			
			Object val = condition[i+2];
			
			cond.and(col, op, val);
			
		}
		
		query.join(Join.INNER_JOIN, entity(dimension, entity), as,
				cond, flag);
	}

	protected String check(String s, String check) {
		return s.concat(" ").concat(check);
	}
	
	protected String isNull(String s) {
		return check(s, "IS NULL");
	}
	
	protected String notNull(String s) {
		return check(s, "IS NOT NULL");
	}
	
}
