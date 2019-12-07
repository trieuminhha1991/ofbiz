package com.olbius.basesales.reports;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.security.util.SecurityUtil;

public class ChartLineSynQtyOrderReport extends OlbiusOlapService {
	private String seriesName;
	private final String BY_PRODUCT_STORE = "PRODUCT_STORE";
	private final String BY_SALES_CHANNEL = "SALES_CHANNEL";
	private final String BY_SUBSIDIARY = "SUBSIDIARY";

	private OlbiusQuery query;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@SuppressWarnings("unchecked")
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		putParameter("filterTypeId", (String) context.get("filterTypeId"));
		putParameter("productStoreIds", (List<String>) context.get("productStoreIds[]"));
		putParameter("salesChannelIds", (List<String>) context.get("salesChannelIds[]"));
		putParameter("partySubsidiaryIds", (List<String>) context.get("partySubsidiaryIds[]"));
		putParameter("fileName", "ChartLineSynQtyOrderReport"); // cache the specific file
		
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

	@SuppressWarnings({ "unchecked" })
	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();

		String vendor_id = (String) getParameter("vendor_id");
		String dateType = getDateType((String) getParameter("dateType"));
		String columDateType = "ETYDIM.".concat(dateType);
		String filterTypeId = (String) getParameter("filterTypeId");
		if (UtilValidate.isEmpty(filterTypeId) 
				|| (!BY_PRODUCT_STORE.equals(filterTypeId) 
						&& !BY_SALES_CHANNEL.equals(filterTypeId))
						&& !BY_SUBSIDIARY.equals(filterTypeId)) {
			filterTypeId = BY_PRODUCT_STORE;
		}
		
		List<Object> productStoreIds = (List<Object>) getParameter("productStoreIds");
		List<Object> salesChannelIds = (List<Object>) getParameter("salesChannelIds");
		List<Object> partySubsidiaryIds = (List<Object>) getParameter("partySubsidiaryIds");
		switch (filterTypeId) {
			case BY_PRODUCT_STORE:
				seriesName = "product_store_id";
				break;
			case BY_SALES_CHANNEL:
				seriesName = "sales_method_channel_enum_id";
				break;
			case BY_SUBSIDIARY: 
				seriesName = "vendor_code";
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
			.select("VENDD.party_id", "vendor_id", BY_SUBSIDIARY.equals(filterTypeId))
			.select("VENDD.party_code", "vendor_code", BY_SUBSIDIARY.equals(filterTypeId))
			.select("SUM(SOF.num_order)", "total_qty_order")
			.select(columDateType, "date_type")
			.from("sales_order_roll_store_fact", "SOF")
			.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "SOF.product_store_dim_id = PSD.dimension_id", BY_PRODUCT_STORE.equals(filterTypeId))
			.join(Join.INNER_JOIN, "enumeration_dimension", "SCHAND", "SOF.sales_method_channel_dim_id = SCHAND.dimension_id", BY_SALES_CHANNEL.equals(filterTypeId))
			.join(Join.INNER_JOIN, "party_dimension", "VENDD", "SOF.vendor_dim_id = VENDD.dimension_id")
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOF.date_dim_id = ETYDIM.dimension_id")
			.where(Condition.makeBetween("SOF.date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
					.andEQ("VENDD.party_id", vendor_id, isViewRepOrg && !isViewRepParner)
					.andNotEQ("VENDD.party_id", vendor_id, !isViewRepOrg && isViewRepParner)
					.andIn("PSD.product_store_id", productStoreIds, UtilValidate.isNotEmpty(productStoreIds))
					.andIn("SCHAND.enum_id", productStoreIds, UtilValidate.isNotEmpty(salesChannelIds))
					.andIn("VENDD.party_id", partySubsidiaryIds, UtilValidate.isNotEmpty(partySubsidiaryIds))
					.and(creatorCondStore, creatorCondStore != null))
			.groupBy("PSD.product_store_id", BY_PRODUCT_STORE.equals(filterTypeId))
			.groupBy("PSD.store_name", BY_PRODUCT_STORE.equals(filterTypeId))
			.groupBy("SCHAND.enum_id", BY_SALES_CHANNEL.equals(filterTypeId))
			.groupBy("SCHAND.description", BY_SALES_CHANNEL.equals(filterTypeId))
			.groupBy("VENDD.party_id", BY_SUBSIDIARY.equals(filterTypeId))
			.groupBy("VENDD.party_code", BY_SUBSIDIARY.equals(filterTypeId))
			.groupBy(columDateType);
		/*} else {
			OlbiusQuery query_inner = makeQuery();
			query_inner.distinct()
					.select("SOF.order_id")
					.select("PSD.product_store_id", BY_PRODUCT_STORE.equals(filterTypeId))
					.select("PSD.store_name", BY_PRODUCT_STORE.equals(filterTypeId))
					.select("SCHAND.enum_id", "sales_method_channel_enum_id", BY_SALES_CHANNEL.equals(filterTypeId))
					.select("SCHAND.description", "sales_method_channel_name", BY_SALES_CHANNEL.equals(filterTypeId))
					.select("VENDD.party_id", "vendor_id", BY_SUBSIDIARY.equals(filterTypeId))
					.select("VENDD.party_code", "vendor_code", BY_SUBSIDIARY.equals(filterTypeId))
					.select(columDateType, "date_type")
					.from("sales_order_new_fact", "SOF")
					.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "SOF.product_store_dim_id = PSD.dimension_id", BY_PRODUCT_STORE.equals(filterTypeId))
					.join(Join.INNER_JOIN, "enumeration_dimension", "SCHAND", "SOF.sales_method_channel_dim_id = SCHAND.dimension_id", BY_SALES_CHANNEL.equals(filterTypeId))
					.join(Join.INNER_JOIN, "party_dimension", "VENDD", "SOF.vendor_dim_id = VENDD.dimension_id")
					.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOF.entry_date_dim_id = ETYDIM.dimension_id")
					.where(Condition.makeBetween("SOF.entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
							.andEQ("SOF.is_promo", "N")
							.andEQ("VENDD.party_id", vendor_id, isViewRepOrg && !isViewRepParner)
							.andNotEQ("VENDD.party_id", vendor_id, !isViewRepOrg && isViewRepParner)
							.andEQ("SOF.order_status_id", "ORDER_COMPLETED")
							.andIn("PSD.product_store_id", productStoreIds, UtilValidate.isNotEmpty(productStoreIds))
							.andIn("SCHAND.enum_id", productStoreIds, UtilValidate.isNotEmpty(salesChannelIds))
							.andIn("VENDD.party_id", partySubsidiaryIds, UtilValidate.isNotEmpty(partySubsidiaryIds))
							.and(creatorCond, creatorCond != null));
			
			query.select("COUNT(TOL.order_id)", "total_qty_order")
				.select("TOL.product_store_id", BY_PRODUCT_STORE.equals(filterTypeId))
				.select("TOL.store_name", BY_PRODUCT_STORE.equals(filterTypeId))
				.select("TOL.sales_method_channel_enum_id", BY_SALES_CHANNEL.equals(filterTypeId))
				.select("TOL.sales_method_channel_name", BY_SALES_CHANNEL.equals(filterTypeId))
				.select("TOL.date_type")
				.from(query_inner, "TOL")
				.groupBy("TOL.product_store_id", BY_PRODUCT_STORE.equals(filterTypeId))
				.groupBy("TOL.store_name", BY_PRODUCT_STORE.equals(filterTypeId))
				.groupBy("TOL.sales_method_channel_enum_id", BY_SALES_CHANNEL.equals(filterTypeId))
				.groupBy("TOL.sales_method_channel_name", BY_SALES_CHANNEL.equals(filterTypeId))
				.groupBy("TOL.vendor_id", BY_SUBSIDIARY.equals(filterTypeId))
				.groupBy("TOL.vendor_code", BY_SUBSIDIARY.equals(filterTypeId))
				.groupBy("TOL.date_type");
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
	public void prepareResultBuilder() {
		if(getOlapResult() instanceof OlapLineChart) {
			String dateType = getDateType((String) getParameter("dateType"));
			getOlapResult().putParameter(DATE_TYPE, dateType);
		}
	};
	
	@Override
	public void prepareResultChart() {
		if (getOlapResult() instanceof OlapLineChart) {
			if (seriesName != null) addSeries(seriesName);
			addXAxis("date_type");
			addYAxis("total_qty_order");
		}
	}
}
