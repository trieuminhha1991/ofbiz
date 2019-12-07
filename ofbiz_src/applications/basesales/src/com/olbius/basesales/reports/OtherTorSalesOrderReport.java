package com.olbius.basesales.reports;

import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.security.util.SecurityUtil;

public class OtherTorSalesOrderReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("orderStatusId", (String) context.get("orderStatusId"));
		putParameter("fileName", "OtherTorSalesOrderReport"); // cache the specific file
		
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
		
		Long vendor_dim_id = null;
		String vendor_id = (String) getParameter("vendor_id");
		try {
			GenericValue vendor = EntityUtil.getFirst(this.delegator.findByAnd("PartyDimension", UtilMisc.toMap("partyId", vendor_id), null, false));
			if (vendor != null) vendor_dim_id = vendor.getLong("dimensionId");
		} catch (GenericEntityException e) {
			Debug.logError("ERROR: Fatal error query party dimension of vendor: vendor_id=" + vendor_id, TorProductStoreReport.class.getName());
		}
		String orderStatusId = (String) getParameter("orderStatusId");

		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		OlbiusQuery innerQuery = makeQuery();
		innerQuery.select("SOF.order_id")
			.select("SOF.order_date")
			.select("SOF.product_store_dim_id")
			.select("SOF.sgc_customer_id", "customer_id")
			.select("SUM(SOF.return_quantity)", "return_quantity")
			.select("SUM(SOF.total_quantity)", "total_quantity")
			.select("SUM(SOF.total_selected_amount)", "total_selected_amount")
			.select("SUM(SOF.discount_amount)", "discount_amount")
			.select("SUM(SOF.sub_total_amount)", "sub_total_amount")
			.select("SUM(SOF.total_amount)", "total_amount")
			.from("sales_order_new_fact", "SOF")
			.groupBy("SOF.order_id")
			.groupBy("SOF.order_date")
			.groupBy("SOF.product_store_dim_id")
			.groupBy("SOF.sgc_customer_id")
			.where(Condition.makeBetween("SOF.entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
					.andEQ("SOF.is_promo", "N")
					.andEQ("SOF.vendor_dim_id", vendor_dim_id, isViewRepOrg && !isViewRepParner)
					.andNotEQ("SOF.vendor_dim_id", vendor_dim_id, !isViewRepOrg && isViewRepParner)
					.andEQ("SOF.order_status_id", orderStatusId, orderStatusId != null)
					.and(creatorCondStore, creatorCondStore != null));
		
		query.select("TMP.order_id")
			.select("TMP.order_date")
			.select("PSD.product_store_id")
			.select("PSD.store_name")
			.select("TMP.customer_id")
			.select("TMP.return_quantity")
			.select("TMP.total_quantity")
			.select("TMP.total_selected_amount")
			.select("TMP.discount_amount")
			.select("TMP.sub_total_amount")
			.select("TMP.total_amount")
			.from(innerQuery, "TMP")
			.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "TMP.product_store_dim_id = PSD.dimension_id");
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("order_id", "order_id");
		addDataField("order_date", "order_date");
		addDataField("product_store_id", "product_store_id");
		addDataField("store_name", "store_name");
		addDataField("customer_id", "customer_id");
		addDataField("return_quantity", "return_quantity");
		addDataField("total_quantity", "total_quantity");
		addDataField("total_selected_amount", "total_selected_amount");
		addDataField("discount_amount", "discount_amount");
		addDataField("sub_total_amount", "sub_total_amount");
		addDataField("total_amount", "total_amount");
	}
	
}
