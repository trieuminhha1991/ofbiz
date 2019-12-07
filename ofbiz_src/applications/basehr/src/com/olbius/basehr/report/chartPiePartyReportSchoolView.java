package com.olbius.basehr.report;

import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class chartPiePartyReportSchoolView extends OlbiusOlapService {
	private String xAsisName;
	private String yAsisName;

	private OlbiusQuery query;

	public final static String EDU_SYS = "EDU_SYS";
	public final static String CLASSIFICATION = "CLASSIFICATION";
	public final static String SCHOOL = "SCHOOL";
	public final static String STUDY_MODE = "STUDY_MODE";
	public final static String MAJOR = "MAJOR";

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		putParameter("group", (String) context.get("group"));
		List<?> groups = (List<?>) context.get("group[]");
		putParameter("group[]",groups);
		putParameter("fileName", "ChartPieHRPartySchoolReport"); // cache the specific file
		putParameter("schoolType", context.get("schoolType"));

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		putParameter("userLoginPartyId", userLogin.get("partyId"));
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();

		String dateType = (String) getParameter("dateType");
		Boolean cur = false;
		if(UtilValidate.isNotEmpty(getParameter("cur"))) cur = (Boolean) getParameter("cur");
		String group = (String) getParameter("group");
		List<Object> groups = (List<Object>) getParameter("group[]");

		dateType = getDateType(dateType);

		String type = (String) getParameter("schoolType");
		type = getSchoolType(type);

		query.select("count(DISTINCT person_dim_id)",  "_count");
		query.select(type, "type");
		query.select("CASE WHEN type IS NULL THEN \'Other\' ELSE type END AS _school");
		query.from("person_relationship_fact");

		String joinConditionDate = "date_dim_id = date_dimension.dimension_id ";
		joinConditionDate = joinConditionDate + " AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM person_relationship_fact" +
				" INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)";
		query.join(Join.INNER_JOIN, "date_dimension", joinConditionDate);
		query.join(Join.INNER_JOIN, "party_dimension", "person_dim_id = party_dimension.dimension_id");

		if(UtilValidate.isNotEmpty(group)){
			query.select("INNER JOIN party_dimension ON group_dim_id = party_dimension.dimension_id");
		}
		Condition cd = new Condition();
		cd.andEQ("role_type_id", "EMPLOYEE");
		if(UtilValidate.isNotEmpty(group)) {
			cd.and("AND party_dimension.party_id = ?");
		}
		query.where(cd);
		query.groupBy("_school");
		query.orderBy("_school");
		return query;
	}

	@Override
	public void prepareResultChart() {
		if (getOlapResult() instanceof OlapPieChart) {
			addXAxis("_school");
			addYAxis("_count");
		}
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
}
