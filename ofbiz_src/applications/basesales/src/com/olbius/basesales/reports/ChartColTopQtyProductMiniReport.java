package com.olbius.basesales.reports;

import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.security.util.SecurityUtil;

public class ChartColTopQtyProductMiniReport extends OlbiusOlapService {
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
		putParameter("fileName", "ChartColTopQtyProductMiniReport"); // cache the specific file
		
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
			Debug.logError("ERROR: Fatal error query party dimension of vendor: vendor_id=" + vendor_id, QtyOrderProductStoreReport.class.getName());
		}
		
		Integer filterTop = (Integer) getParameter("filterTop");
		String filterSort = (String) getParameter("filterSort");
		if (UtilValidate.isEmpty(filterTop)) filterTop = 5;
		if (!"DESC".equals(filterSort) && !"ASC".equals(filterSort)) filterSort = "DESC";
		String filterProductStore = (String) getParameter("filterProductStore");
		
		/*Condition creatorCond = null;
		if (isViewRepOrg && !isViewRepParner) creatorCond = ReportSalesUtils.makeCondFindByCreatorSO2(delegator, (String) getParameter("userLoginPartyId"));
		*/
		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		//if (creatorCond == null) {
			query.select("PRODD.product_id")
			.select("PRODD.product_code")
			.select("SUM(SOF.total_quantity)", "total_quantity")
			.from("sales_order_roll_store_and_product_fact", "SOF")
			.join(Join.INNER_JOIN, "product_dimension", "PRODD", "SOF.product_dim_id = PRODD.dimension_id")
			.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "SOF.product_store_dim_id = PSD.dimension_id")
			.groupBy("PRODD.product_id")
			.groupBy("PRODD.product_code")
			.where(Condition.makeBetween("SOF.date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("SOF.vendor_dim_id", vendor_dim_id, isViewRepOrg && !isViewRepParner)
						.andNotEQ("SOF.vendor_dim_id", vendor_dim_id, !isViewRepOrg && isViewRepParner)
						.andEQ("PSD.product_store_id", filterProductStore, UtilValidate.isNotEmpty(filterProductStore))
						.and(creatorCondStore, creatorCondStore != null))
			.orderBy("SUM(SOF.total_quantity) " + filterSort)
			.limit(filterTop);
		/*} else {
			query.select("PRODD.product_id")
			.select("PRODD.product_code")
			.select("SUM(SOF.total_quantity)", "total_quantity")
			.from("sales_order_new_fact", "SOF")
			.join(Join.INNER_JOIN, "product_dimension", "PRODD", "SOF.product_dim_id = PRODD.dimension_id")
			.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "SOF.product_store_dim_id = PSD.dimension_id")
			.groupBy("PRODD.product_id")
			.groupBy("PRODD.product_code")
			.where(Condition.makeBetween("SOF.entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("SOF.is_promo", "N")
						.andEQ("SOF.vendor_dim_id", vendor_dim_id, isViewRepOrg && !isViewRepParner)
						.andNotEQ("SOF.vendor_dim_id", vendor_dim_id, !isViewRepOrg && isViewRepParner)
						.andEQ("SOF.order_status_id", "ORDER_COMPLETED")
						.andEQ("PSD.product_store_id", filterProductStore, UtilValidate.isNotEmpty(filterProductStore))
						.and(creatorCond, creatorCond != null)
						.and(creatorCondStore, creatorCondStore != null))
			.orderBy("SUM(SOF.total_quantity) " + filterSort)
			.limit(filterTop);
		}*/
		
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
			addXAxis("product_code");
			addYAxis("total_quantity");
		}
	}
}
