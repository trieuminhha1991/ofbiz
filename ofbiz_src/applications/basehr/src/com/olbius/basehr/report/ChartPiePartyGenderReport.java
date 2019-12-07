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

import javax.rmi.CORBA.Util;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChartPiePartyGenderReport extends OlbiusOlapService {
	private String xAsisName;
	private String yAsisName;

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		putParameter("group", (String) context.get("group"));
		List<?> groups = (List<?>) context.get("group[]");
		putParameter("group[]",groups);
		putParameter("fileName", "ChartPiePartyGenderReport"); // cache the specific file

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

		query.select("count(DISTINCT person_dim_id)", "_count")
		.select("CASE WHEN gender=\'M\' THEN \'Male\' WHEN gender=\'F\' THEN \'Female\' WHEN gender IS NULL THEN \'Other\' " +
				"ELSE gender END AS _gender")
		.from("person_relationship_fact");
		String joinConditionDate = "date_dim_id = date_dimension.dimension_id ";
		joinConditionDate = joinConditionDate + " AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM person_relationship_fact"
				+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)";
		query.join(Join.INNER_JOIN, "date_dimension", joinConditionDate)
		.join(Join.INNER_JOIN, "party_dimension", "person_dim_id = party_dimension.dimension_id");

		if(UtilValidate.isNotEmpty(group)){
			query.join(Join.INNER_JOIN, "party_dimension", "group_dim_id = party_dimension.dimension_id");
		}
		Condition cd = new Condition();
		cd.andEQ("role_type_id", "EMPLOYEE");
		if(UtilValidate.isNotEmpty(group)) {
			cd.and("AND party_dimension.party_id = ?");
		}
		query.where(cd);
		query.groupBy("_gender");
		query.orderBy("_gender", "ASC");
		return query;
	}

	@Override
	public void prepareResultChart() {
		if (getOlapResult() instanceof OlapPieChart) {
			addXAxis("_gender");
			addYAxis("_count");
		}
	}
}
