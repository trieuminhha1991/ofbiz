package com.olbius.basesales.reports;

import java.util.Date;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
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

public class ChartPieSynTorSalesChannelReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		//putParameter("dateType", (String) context.get("dateType"));
		//putParameter("filterTypeId", (String) context.get("filterTypeId"));
		//putParameter("filterTypeId2", (String) context.get("filterTypeId2"));
		putParameter("fileName", "ChartPieSynTorSalesChannelReport"); // cache the specific file
		
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

		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		query.select("SCHAND.enum_id", "sales_method_channel_enum_id")
			.select("SCHAND.description", "sales_method_channel_name")
			.select("SUM(SOF.total_quantity)", "total_quantity")
			.select("SUM(SOF.total_amount)", "total_amount")
			.from("sales_order_new_fact", "SOF")
			.join(Join.INNER_JOIN, "enumeration_dimension", "SCHAND", "SOF.sales_method_channel_dim_id = SCHAND.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "VENDD", "SOF.vendor_dim_id = VENDD.dimension_id", !isViewRepOrg || !isViewRepParner)
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOF.entry_date_dim_id = ETYDIM.dimension_id")
			.groupBy("SCHAND.enum_id")
			.groupBy("SCHAND.description")
			.where(Condition.makeBetween("entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("SOF.is_promo", "N")
						.andEQ("VENDD.party_id", vendor_id, isViewRepOrg && !isViewRepParner)
						.andNotEQ("VENDD.party_id", vendor_id, !isViewRepOrg && isViewRepParner)
						.andEQ("SOF.order_status_id", "ORDER_COMPLETED")
						.and(creatorCondStore, creatorCondStore != null));
		
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
			addXAxis("sales_method_channel_name");
			addYAxis("total_amount");
		} else if (getOlapResult() instanceof OlapPieChart) {
			addXAxis("sales_method_channel_name");
			addYAxis("total_amount");
		}
	}
}
