package com.olbius.basesales.reports;

import java.util.Date;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.security.util.SecurityUtil;

public class OtherTorProductCategoryReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		putParameter("xAxisName", (String) context.get("xAxisName"));
		//putParameter("parent_category_id", (String) context.get("parent_category_id"));
		putParameter("orderStatusId", (String) context.get("orderStatusId"));
		putParameter("fileName", "OtherTorProductCategoryReport"); // cache the specific file
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String viewPartner = (String) context.get("viewPartner");
		putParameter("viewPartner", viewPartner);
		if (SecurityUtil.getOlbiusSecurity(dctx.getSecurity()).olbiusHasPermission(userLogin, null, "ENTITY", "ETTSALES_REPORT_PARTNER")) {
			if ("Y".equals(viewPartner)) {
				isViewRepOrg = false;
				isViewRepParner = true;
			} else if ("A".equals(viewPartner)) {
				isViewRepOrg = true;
				isViewRepParner = true;
			}
		}
		putParameter("vendor_id", SalesUtil.getCurrentOrganization(delegator, userLogin));
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
		//String parent_category_id = (String) getParameter("parent_category_id");
		String vendor_id = (String) getParameter("vendor_id");
		String orderStatusId = (String) getParameter("orderStatusId");
		
		//if (parent_category_id != null) conds.andEQ("PCATED.category_id", parent_category_id);

		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		query.select("CATED.category_id")
			.select("CATED.category_name")
			.select("SUM(SOF.total_quantity)", "total_quantity")
			.select("SUM(SOF.total_selected_amount)", "total_selected_amount")
			.select("SUM(SOF.total_amount)", "total_amount")
			.from("category_dimension", "CATED")
			.join(Join.INNER_JOIN, "product_category_member_rel", "PCR", "CATED.dimension_id = PCR.category_dim_id")
			.join(Join.INNER_JOIN, "sales_order_new_fact", "SOF", "PCR.product_dim_id = SOF.product_dim_id AND ((PCR.thru_date_dim_id = '-1' AND SOF.order_date_dim_id > PCR.from_date_dim_id) OR (SOF.order_date_dim_id BETWEEN PCR.from_date_dim_id AND PCR.thru_date_dim_id))")
			.join(Join.INNER_JOIN, "party_dimension", "VENDD", "SOF.vendor_dim_id = VENDD.dimension_id", !isViewRepOrg || !isViewRepParner)
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOF.entry_date_dim_id = ETYDIM.dimension_id")
			.groupBy("CATED.category_id")
			.groupBy("CATED.category_name")
			.where(Condition.makeBetween("entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
					.andEQ("SOF.is_promo", "N")
					.andEQ("CATED.category_type", "CATALOG_CATEGORY")
					.andEQ("VENDD.party_id", vendor_id, isViewRepOrg && !isViewRepParner)
					.andNotEQ("VENDD.party_id", vendor_id, !isViewRepOrg && isViewRepParner)
					.andEQ("SOF.order_status_id", orderStatusId, orderStatusId != null)
					.and(creatorCondStore, creatorCondStore != null));
		
		/*
		.select("PCATED.category_id", "parent_category_id")
		.join(Join.LEFT_OUTER_JOIN, "category_relationship", "CATER", "PCR.category_dim_id = CATER.dimension_id AND (CATER.thru_date_dim_id is null OR CATER.thru_date_dim_id = '-1')")
		.join(Join.LEFT_OUTER_JOIN, "category_dimension", "PCATED", "CATER.parent_dim_id = PCATED.dimension_id AND PCATED.category_type = 'CATALOG_CATEGORY'")
		.groupBy("PCATED.category_id")
		*/
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		//String dateType = getDateType((String) getParameter("dateType"));
		//addDataField("dateTime", dateType);
		addDataField("category_id", "category_id");
		addDataField("category_name", "category_name");
		//addDataField("parent_category_id", "parent_category_id");
		addDataField("total_quantity", "total_quantity");
		addDataField("total_selected_amount", "total_selected_amount");
		addDataField("total_amount", "total_amount");
	}
}
