package com.olbius.basehr.report;

import com.olbius.bi.olap.chart.OlapColumnChart;
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

public class ChartColPartyMemberReport extends OlbiusOlapService {
	private OlbiusQuery query;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = true;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		//putParameter("dateType", (String) context.get("dateType"));
		putParameter("filterTop", (Integer) context.get("filterTop"));
		putParameter("filterSort", (String) context.get("filterSort"));
		putParameter("filterProductStore", (String) context.get("filterProductStore"));
		putParameter("fileName", "ChartColPartyMemberReport"); // cache the specific file
        putParameter("cur", context.get("cur"));
        putParameter("dateType", context.get("dateType"));
        putParameter("olapType", context.get("olapType"));
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
//		if (SecurityUtil.getOlbiusSecurity(dctx.getSecurity()).olbiusHasPermission(userLogin, null, "MODULE", "SALES_REPORT_ALLORG")) {
//			if ("Y".equals(viewPartner)) {
//				isViewRepOrg = false;
//				isViewRepParner = true;
//			} else if ("A".equals(viewPartner)) {
//				isViewRepOrg = true;
//				isViewRepParner = true;
//			}
//		}
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
        List<Object> groups = (List<Object>) getParameter("group[]");

        dateType = getDateType(dateType);
        String sql = "SELECT count(DISTINCT person_dim_id) AS _count, %DATE_TYPE%, party_dimension.party_id FROM person_relationship_fact"
                + " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id"
                + " %AND_CUR%"
                + " INNER JOIN party_dimension ON group_dim_id = party_dimension.dimension_id"
                + " WHERE role_type_id = 'EMPLOYEE' %AND_GROUP% %AND_DATE%"
                + " GROUP BY %DATE_TYPE%, party_dimension.party_id ORDER BY %DATE_TYPE%, party_dimension.party_id";
        query.select("count(DISTINCT PERSON_DIM_ID)",  "_count");
        query.select(dateType, "dateType");
        query.select("party_dimension.party_id");
        query.from("person_relationship_fact");
        String joinConditionDate = "date_dim_id = date_dimension.dimension_id ";
        if(cur) {
            joinConditionDate = joinConditionDate + "AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM person_relationship_fact"
                    + " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)";
        }
        query.join(Join.INNER_JOIN, "date_dimension", joinConditionDate);
        query.join(Join.INNER_JOIN, "party_dimension", "group_dim_id = party_dimension.dimension_id");
        Condition cd = new Condition();
        cd.andEQ("role_type_id", "EMPLOYEE");
        if(UtilValidate.isNotEmpty(group))
            cd.andEQ("party_dimension.party_id", group);
        else if(UtilValidate.isNotEmpty(groups)) {
            cd.andIn("party_dimension.party_id", groups);
        }
        if(cur) {
            cd.and("AND date_dimension.date_value = (SELECT max(date_dimension.date_value) FROM person_relationship_fact"
                    + " INNER JOIN date_dimension ON date_dim_id = date_dimension.dimension_id)");
        }
        else {
            cd.andBetween("date_dimension.date_value", getFromDate(), getThruDate());
        }
        query.where(cd);
        query.groupBy(dateType, "party_dimension.party_id");
        query.orderBy(dateType, "ASC");
        query.orderBy("party_dimension.party_id", "ASC");
        query.limit(filterTop);
        return query;
	}

	/*@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("product_store_id", "product_store_id");
		addDataField("store_name", "store_name");
		addDataField("total_quantity", "total_quantity");
		addDataField("total_amount", "total_amount");
	}*/
	
	@Override
	public void prepareResultChart() {
		if (getOlapResult() instanceof OlapColumnChart) {
			//addSeries("store_name");
			addXAxis("party_id");
			addYAxis("_count");
		}
	}
}
