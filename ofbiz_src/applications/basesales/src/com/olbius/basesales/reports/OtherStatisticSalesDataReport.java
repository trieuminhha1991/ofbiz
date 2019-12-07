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

public class OtherStatisticSalesDataReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("orderStatusId", (String) context.get("orderStatusId"));
		putParameter("fileName", "OtherStatisticSalesDataReport"); // cache the specific file
		
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
		
		String vendor_id = (String) getParameter("vendor_id");
		String orderStatusId = (String) getParameter("orderStatusId");

		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		query.select("SOF.order_id")
			.select("SOF.order_date")
			.select("PSD.product_store_id")
			.select("PSD.store_name")
			.select("CREATD.party_code", "creator_id")
			.select("CREATD.name", "creator_name")
			.select("SUM(SOF.sub_total_amount)", "sub_total_amount")
			.select("SUM(SOF.total_amount)", "total_amount")
			.select("COUNT(SOF.order_item_seq_id)", "num_item")
			.from("sales_order_new_fact", "SOF")
			.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "SOF.product_store_dim_id = PSD.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "CREATD", "SOF.creator_dim_id = CREATD.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "VENDD", "SOF.vendor_dim_id = VENDD.dimension_id", !isViewRepOrg || !isViewRepParner)
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOF.entry_date_dim_id = ETYDIM.dimension_id")
			.groupBy("SOF.order_id")
			.groupBy("SOF.order_date")
			.groupBy("PSD.product_store_id")
			.groupBy("PSD.store_name")
			.groupBy("CREATD.party_code")
			.groupBy("CREATD.name")
			.orderBy("SOF.order_date DESC")
			.where(Condition.makeBetween("entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
					.andEQ("SOF.is_promo", "N")
					.andEQ("VENDD.party_id", vendor_id, isViewRepOrg && !isViewRepParner)
					.andNotEQ("VENDD.party_id", vendor_id, !isViewRepOrg && isViewRepParner)
					.andEQ("SOF.order_status_id", orderStatusId, orderStatusId != null)
					.and(creatorCondStore, creatorCondStore != null));
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("order_id", "order_id");
		addDataField("order_date", "order_date");
		addDataField("product_store_id", "product_store_id");
		addDataField("store_name", "store_name");
		addDataField("creator_id", "creator_id");
		addDataField("creator_name", "creator_name");
		addDataField("sub_total_amount", "sub_total_amount");
		addDataField("total_amount", "total_amount");
		addDataField("num_item", "num_item");
	}
	
}
