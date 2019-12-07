package com.olbius.basehr.report;

import java.sql.ResultSet;
import java.util.*;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.security.util.SecurityUtil;

public class ChartPiePositionViewReport extends OlbiusOlapService {
	private String xAsisName;
	private String yAsisName;
	public final static String POSITION = "POSITION";
	public final static String POSITION_TYPE = "POSITION_TYPE";

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		putParameter("group", (String) context.get("group"));
		List<?> groups = (List<?>) context.get("group[]");
		putParameter("group[]",groups);
		putParameter("fileName", "ChartPieHRPositionReport"); // cache the specific file
		putParameter("type", context.get("type"));

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

		String type = (String) getParameter("type");
		type = getPositionType(type);

		query.select("count(DISTINCT EMPL_POSITION_FACT.PARTY_DIM_ID)",  "_count");
		query.select(type, "type");
		query.from("empl_position_fact");
		query.select(dateType, "dateType");
		String joinConditionDate = "date_dim_id = date_dimension.dimension_id ";
		joinConditionDate = joinConditionDate + "AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM empl_position_fact"
					+ " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)";

		query.join(Join.INNER_JOIN, "date_dimension", joinConditionDate);
		query.join(Join.INNER_JOIN, "empl_position_dimension", "empl_position_dim_id = empl_position_dimension.dimension_id");
		String joinConditionPartyDim = "empl_position_fact.party_dim_id = person_relationship_fact.person_dim_id";
		joinConditionPartyDim = joinConditionPartyDim + " AND empl_position_fact.date_dim_id = person_relationship_fact.date_dim_id";
		query.join(Join.INNER_JOIN, "person_relationship_fact", joinConditionPartyDim);
		Condition cd = new Condition();
		cd.andEQ("role_type_id", "EMPLOYEE");
		if(UtilValidate.isNotEmpty(group)) {
			query.join(Join.INNER_JOIN, "party_dimension", "group_dim_id = party_dimension.dimension_id");
			cd.andEQ("party_dimension.party_id", group);
		}
		query.where(cd);
		query.groupBy(type);
		query.orderBy(type);
		return query;
	}

	@Override
	public void prepareResultChart() {
		if (getOlapResult() instanceof OlapPieChart) {
			addXAxis("type");
			addYAxis("_count");
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
}
