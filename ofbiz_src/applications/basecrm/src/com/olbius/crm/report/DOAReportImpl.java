package com.olbius.crm.report;

import java.util.Date;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class DOAReportImpl extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		
		putParameter("dateType", context.get("dateType"));
		putParameter("organization", organization);
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}
	
	private OlbiusQuery init() {
		OlbiusQuery query0 = makeQuery();
		Condition condition = new Condition();
		String organization = (String) getParameter("organization");
		
		condition.and(Condition.make("product_dimension.internal_name is not null"))
		.and(Condition.makeEQ("organ.party_id", organization))
		.andBetween("date_dimension.dimension_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()));
		
		
		query0.from("communication_event_fact")
		.select("COUNT(communication_event_fact.product_discussed_dim_id)", "abc")
		.select("product_dimension.internal_name")
		.select("product_dimension.product_code").select("communication_event_fact.result_enum_id").select("ed.description")
		.join(Join.INNER_JOIN, "date_dimension", "communication_event_fact.entry_date_dim_id = date_dimension.dimension_id")
		.join(Join.INNER_JOIN, "product_dimension", "communication_event_fact.product_discussed_dim_id = product_dimension.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "caller", "caller.dimension_id = communication_event_fact.party_from_dim_id")
		.join(Join.INNER_JOIN, "party_person_relationship", "caller.dimension_id = party_person_relationship.person_dim_id")
		.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
		.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id")
		.join(Join.INNER_JOIN, "enumeration_dimension", "ed", "ed.enum_id = communication_event_fact.result_enum_id")
		.where(condition)
		.groupBy("product_dimension.product_code").groupBy("product_dimension.internal_name").groupBy("communication_event_fact.result_enum_id")
		.groupBy("ed.description")
		.orderBy("product_dimension.product_code");
		
		return query0;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("stt");
		addDataField("productId", "product_code");
		addDataField("productName", "internal_name");
		addDataField("quantity", "abc");
		addDataField("reason", "description");
	}
}


//import java.sql.ResultSet;
//import java.util.HashMap;
//import java.util.Map;
//import org.ofbiz.base.util.Debug;
//
//import com.olbius.bi.olap.AbstractOlap;
//import com.olbius.bi.olap.grid.ReturnResultGrid;
//import com.olbius.bi.olap.query.OlbiusQuery;
//import com.olbius.bi.olap.query.OlapQuery;
//import com.olbius.bi.olap.query.condition.Condition;
//import com.olbius.bi.olap.query.join.Join;
//
//public class DOAReportImpl extends AbstractOlap {
//
//	public static final String ORG = "ORG";
//
//	private OlbiusQuery query;
//
//	private void initQuery() {
//
//		Condition condition = new Condition();
//		String organization = (String) getParameter(ORG);
//		query = OlbiusQuery.make(getSQLProcessor());
//
//		query.from("communication_event_fact")
//				.select("COUNT(communication_event_fact.product_discussed_dim_id)", "abc")
//				.select("product_dimension.internal_name")
//				.select("product_dimension.product_code")
//				.join(Join.INNER_JOIN, "date_dimension", "communication_event_fact.entry_date_dim_id = date_dimension.dimension_id")
//				.join(Join.INNER_JOIN, "product_dimension", "communication_event_fact.product_discussed_dim_id = product_dimension.dimension_id")
//				.join(Join.INNER_JOIN, "party_dimension", "caller", "caller.dimension_id = communication_event_fact.party_from_dim_id")
//				.join(Join.INNER_JOIN, "party_person_relationship", "caller.dimension_id = party_person_relationship.person_dim_id")
//				.join(Join.INNER_JOIN, "party_group_relationship", "party_person_relationship.group_dim_id = party_group_relationship.dimension_id")
//				.join(Join.INNER_JOIN, "party_dimension", "organ", "organ.dimension_id = party_group_relationship.parent_dim_id")
//				.where(condition)
//				.groupBy("product_dimension.dimension_id")
//				.orderBy("product_dimension.product_code");
//
//		condition.and(Condition.make("product_dimension.internal_name notnull"));
//		condition.and(Condition.makeEQ("organ.party_id", organization));
//	}
//
//	@Override
//	protected OlapQuery getQuery() {
//		if (query == null) {
//			initQuery();
//		}
//		return query;
//	}
//
//	public class ResultDOAReport extends ReturnResultGrid {
//
//		public ResultDOAReport() {
//			addDataField("stt");
//			addDataField("productId");
//			addDataField("productName");
//			addDataField("quantity");
//		}
//
//		@Override
//		protected Map<String, Object> getObject(ResultSet result) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			try {
//				map.put("productId", result.getString("product_code"));
//				map.put("productName", result.getString("internal_name"));
//				map.put("quantity", result.getInt("abc"));
//			} catch (Exception e) {
//				Debug.logError(e.getMessage(), ResultDOAReport.class.getName());
//			}
//			return map;
//		}
//	}
//}
