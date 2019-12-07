package com.olbius.basehr.report;

import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import javolution.io.Struct;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChartColPartyBarReportHR extends OlbiusOlapService {
	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		List<?> groups = (List<?>) context.get("group[]");
		putParameter("fileName", "ChartBarPartyBirthReport"); // cache the specific file
		putParameter("group[]",groups);
        putParameter("dateType", context.get("dateType"));
        putParameter("gender", context.get("gender"));
        putParameter("olapType", context.get("olapType"));
		
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
		Integer filterTop = (Integer) getParameter("filterTop");
		String filterSort = (String) getParameter("filterSort");
		Boolean cur = false;
		if(UtilValidate.isNotEmpty(getParameter("cur"))) cur = (Boolean) getParameter("cur");
		String group = (String) getParameter("group");
		Boolean gender = false;
		if(UtilValidate.isNotEmpty(getParameter("gender"))) gender = (Boolean) getParameter("gender");
        List<Object> groups = (List<Object>) getParameter("group[]");

        dateType = getDateType(dateType);
        // sql khong su dung
		/* sql*/
		String sql = "SELECT YEAR(NOW()) - YEAR(birth_date_dimension.date_value) AS _age, count(DISTINCT person_dim_id) AS _count, %GENDER% FROM person_relationship_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id"
				+ " AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM person_relationship_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)"
				+ " INNER JOIN party_dimension ON person_dim_id = party_dimension.dimension_id"
				+ " INNER JOIN date_dimension AS birth_date_dimension ON birth_date_dim_id = birth_date_dimension.dimension_id"
				+ " %JOIN_GROUP%"
				+ " WHERE role_type_id = 'EMPLOYEE' %AND_GENDER% %AND_GROUP%"
				+ " GROUP BY _age, %_GENDER% ORDER BY _age, %_GENDER%";
		/*end sql*/
		query.select("YEAR(NOW()) - YEAR(birth_date_dimension.date_value", "_age");
		query.select("count(DISTINCT person_dim_id)", "_count")	;
		if(gender){
			query.select("gender, CASE WHEN gender=\'M\' THEN \'Male\' WHEN gender=\'F\' THEN \'Female\' "
					+ "WHEN gender IS NULL THEN \'Other\' ELSE gender END AS _gender");
		}
		query.from("person_relationship_fact");
		String joinConditionDate = "date_dim_id = date_dimension.dimension_id ";
		joinConditionDate = joinConditionDate + " AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM person_relationship_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)";
		query.join(Join.INNER_JOIN, "date_dimension", joinConditionDate);
		query.join(Join.INNER_JOIN,"party_dimension", "person_dim_id = party_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "date_dimension AS birth_date_dimension", "birth_date_dim_id = birth_date_dimension.dimension_id");
		if(UtilValidate.isNotEmpty(group)){
			query.join(Join.INNER_JOIN, " party_dimension", "group_dim_id = party_dimension.dimension_id");
		}
		Condition cd = new Condition();
		cd.andEQ("role_type_id", "EMPLOYEE");
		if(UtilValidate.isNotEmpty(gender)){
			cd.and("gender is NOT NULL AND birth_date_dimension.date_value is NOT NULL");
		}
		if(UtilValidate.isNotEmpty(group)){
			cd.andEQ("party_dimension.party_id", group);
		}
        query.where(cd);
        query.groupBy("_age", "gender");
        query.orderBy("_age", "ASC");
        query.orderBy("gender", "ASC");
        return query;

	}
	@Override
	public void prepareResultChart() {
		if (getOlapResult() instanceof OlapColumnChart) {
			addXAxis("_age");
			addYAxis("_count");
		}
	}
}
