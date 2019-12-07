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
import com.olbius.bi.olap.chart.OlapPieChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.security.util.SecurityUtil;

public class ChartPieSynTorSalesReport extends OlbiusOlapService {
	private String xAsisName;
	private String yAsisName;
	private final String BY_PRODUCT_STORE = "PRODUCT_STORE";
	private final String BY_SALES_CHANNEL = "SALES_CHANNEL";
	private final String BY2_SALES_VALUE = "SALES_VALUE";
	private final String BY2_SALES_VOLUME = "SALES_VOLUME";
	private final String BY2_ORDER_VOLUME = "ORDER_VOLUME";

	private OlbiusQuery query;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		//putParameter("dateType", (String) context.get("dateType"));
		putParameter("filterTypeId", (String) context.get("filterTypeId"));
		putParameter("filterTypeId2", (String) context.get("filterTypeId2"));
		putParameter("fileName", "ChartPieSynTorSalesReport"); // cache the specific file
		
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
		
		String filterTypeId = (String) getParameter("filterTypeId");
		String filterTypeId2 = (String) getParameter("filterTypeId2");
		if (UtilValidate.isEmpty(filterTypeId) || (!BY_PRODUCT_STORE.equals(filterTypeId) && !BY_SALES_CHANNEL.equals(filterTypeId))) {
			filterTypeId = BY_PRODUCT_STORE;
		}
		
		if (UtilValidate.isEmpty(filterTypeId2) || (!BY2_SALES_VALUE.equals(filterTypeId2) && !BY2_SALES_VOLUME.equals(filterTypeId2) && !BY2_ORDER_VOLUME.equals(filterTypeId2))) {
			filterTypeId = BY2_SALES_VALUE;
		}
		
		switch (filterTypeId) {
			case BY_PRODUCT_STORE:
				xAsisName = "store_name";
				break;
			case BY_SALES_CHANNEL:
				xAsisName = "sales_method_channel_name";
				break;
			default:
				break;
		}
		switch (filterTypeId2) {
			case BY2_SALES_VALUE:
				yAsisName = "total_amount";
				break;
			case BY2_SALES_VOLUME:
				yAsisName = "total_quantity";
				break;
			case BY2_ORDER_VOLUME:
				yAsisName = "total_qty_order";
				break;
			default:
				break;
		}
		
		/*Condition creatorCond = null;
		if (isViewRepOrg && !isViewRepParner) creatorCond = ReportSalesUtils.makeCondFindByCreatorSO2(delegator, (String) getParameter("userLoginPartyId"));
		*/

		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		//if (creatorCond == null) {
			query.select("PSD.product_store_id", BY_PRODUCT_STORE.equals(filterTypeId))
			.select("PSD.store_name", BY_PRODUCT_STORE.equals(filterTypeId))
			.select("SCHAND.enum_id", "sales_method_channel_enum_id", BY_SALES_CHANNEL.equals(filterTypeId))
			.select("SCHAND.description", "sales_method_channel_name", BY_SALES_CHANNEL.equals(filterTypeId))
			.select("SUM(SOF.num_order)", "total_qty_order", BY2_ORDER_VOLUME.equals(filterTypeId2))
			.select("SUM(SOF.total_quantity)", "total_quantity", BY2_SALES_VOLUME.equals(filterTypeId2))
			.select("SUM(SOF.total_amount)", "total_amount", BY2_SALES_VALUE.equals(filterTypeId2))
			.from("sales_order_roll_store_fact", "SOF")
			.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "SOF.product_store_dim_id = PSD.dimension_id", BY_PRODUCT_STORE.equals(filterTypeId))
			.join(Join.INNER_JOIN, "enumeration_dimension", "SCHAND", "SOF.sales_method_channel_dim_id = SCHAND.dimension_id", BY_SALES_CHANNEL.equals(filterTypeId))
			.groupBy("PSD.product_store_id", BY_PRODUCT_STORE.equals(filterTypeId))
			.groupBy("PSD.store_name", BY_PRODUCT_STORE.equals(filterTypeId))
			.groupBy("SCHAND.enum_id", BY_SALES_CHANNEL.equals(filterTypeId))
			.groupBy("SCHAND.description", BY_SALES_CHANNEL.equals(filterTypeId))
			.where(Condition.makeBetween("SOF.date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("SOF.vendor_dim_id", vendor_dim_id, isViewRepOrg && !isViewRepParner)
						.andNotEQ("SOF.vendor_dim_id", vendor_dim_id, !isViewRepOrg && isViewRepParner)
						.and(creatorCondStore, creatorCondStore != null));
		/*} else {
			if (!BY2_ORDER_VOLUME.equals(filterTypeId2)) {
				query.select("PSD.product_store_id", BY_PRODUCT_STORE.equals(filterTypeId))
					.select("PSD.store_name", BY_PRODUCT_STORE.equals(filterTypeId))
					.select("SCHAND.enum_id", "sales_method_channel_enum_id", BY_SALES_CHANNEL.equals(filterTypeId))
					.select("SCHAND.description", "sales_method_channel_name", BY_SALES_CHANNEL.equals(filterTypeId))
					.select("SUM(SOF.total_quantity)", "total_quantity", BY2_SALES_VOLUME.equals(filterTypeId2))
					.select("SUM(SOF.total_amount)", "total_amount", BY2_SALES_VALUE.equals(filterTypeId2))
					.from("sales_order_new_fact", "SOF")
					.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "SOF.product_store_dim_id = PSD.dimension_id", BY_PRODUCT_STORE.equals(filterTypeId))
					.join(Join.INNER_JOIN, "enumeration_dimension", "SCHAND", "SOF.sales_method_channel_dim_id = SCHAND.dimension_id", BY_SALES_CHANNEL.equals(filterTypeId))
					.groupBy("PSD.product_store_id", BY_PRODUCT_STORE.equals(filterTypeId))
					.groupBy("PSD.store_name", BY_PRODUCT_STORE.equals(filterTypeId))
					.groupBy("SCHAND.enum_id", BY_SALES_CHANNEL.equals(filterTypeId))
					.groupBy("SCHAND.description", BY_SALES_CHANNEL.equals(filterTypeId))
					.where(Condition.makeBetween("SOF.entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
								.andEQ("SOF.is_promo", "N")
								.andEQ("SOF.vendor_dim_id", vendor_dim_id, isViewRepOrg && !isViewRepParner)
								.andNotEQ("SOF.vendor_dim_id", vendor_dim_id, !isViewRepOrg && isViewRepParner)
								.andEQ("SOF.order_status_id", "ORDER_COMPLETED")
								.and(creatorCond, creatorCond != null));
			} else {
				OlbiusQuery query_inner = makeQuery();
				query_inner.distinct()
						.select("SOF.order_id")
						.select("PSD.product_store_id", BY_PRODUCT_STORE.equals(filterTypeId))
						.select("PSD.store_name", BY_PRODUCT_STORE.equals(filterTypeId))
						.select("SCHAND.enum_id", "sales_method_channel_enum_id", BY_SALES_CHANNEL.equals(filterTypeId))
						.select("SCHAND.description", "sales_method_channel_name", BY_SALES_CHANNEL.equals(filterTypeId))
						.from("sales_order_new_fact", "SOF")
						.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "SOF.product_store_dim_id = PSD.dimension_id", BY_PRODUCT_STORE.equals(filterTypeId))
						.join(Join.INNER_JOIN, "enumeration_dimension", "SCHAND", "SOF.sales_method_channel_dim_id = SCHAND.dimension_id", BY_SALES_CHANNEL.equals(filterTypeId))
						.where(Condition.makeBetween("SOF.entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
								.andEQ("SOF.is_promo", "N")
								.andEQ("SOF.vendor_dim_id", vendor_dim_id, isViewRepOrg && !isViewRepParner)
								.andNotEQ("SOF.vendor_dim_id", vendor_dim_id, !isViewRepOrg && isViewRepParner)
								.andEQ("SOF.order_status_id", "ORDER_COMPLETED")
								.and(creatorCond, creatorCond != null));
				
				query.select("COUNT(TOL.order_id)", "total_qty_order")
					.select("TOL.product_store_id", BY_PRODUCT_STORE.equals(filterTypeId))
					.select("TOL.store_name", BY_PRODUCT_STORE.equals(filterTypeId))
					.select("TOL.sales_method_channel_enum_id", BY_SALES_CHANNEL.equals(filterTypeId))
					.select("TOL.sales_method_channel_name", BY_SALES_CHANNEL.equals(filterTypeId))
					.from(query_inner, "TOL")
					.groupBy("TOL.product_store_id", BY_PRODUCT_STORE.equals(filterTypeId))
					.groupBy("TOL.store_name", BY_PRODUCT_STORE.equals(filterTypeId))
					.groupBy("TOL.sales_method_channel_enum_id", BY_SALES_CHANNEL.equals(filterTypeId))
					.groupBy("TOL.sales_method_channel_name", BY_SALES_CHANNEL.equals(filterTypeId));
			}
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
			if (xAsisName == null) xAsisName = "store_name";
			if (yAsisName == null) yAsisName = "total_amount";
			addXAxis(xAsisName);
			addYAxis(yAsisName);
		} else if (getOlapResult() instanceof OlapPieChart) {
			if (xAsisName == null) xAsisName = "store_name";
			if (yAsisName == null) yAsisName = "total_amount";
			addXAxis(xAsisName);
			addYAxis(yAsisName);
		}
	}
}
