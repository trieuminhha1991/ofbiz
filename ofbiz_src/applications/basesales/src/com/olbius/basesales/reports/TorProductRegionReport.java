package com.olbius.basesales.reports;

import java.util.Date;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.security.util.SecurityUtil;

public class TorProductRegionReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		//putParameter("xAxisName", (String) context.get("xAxisName"));
		putParameter("orderStatusId", (String) context.get("orderStatusId"));
		putParameter("fileName", "TorProductRegionReport"); // cache the specific file
		
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
		String dateType = getDateType((String) getParameter("dateType"));
		String columDateType = "ETYDIM.".concat(dateType);
		String orderStatusId = (String) getParameter("orderStatusId");
		
		boolean isFilterDateType = true;
		if (getOlapResult() instanceof AbstractOlapChart) isFilterDateType = false;

		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		query.select("PRODD.product_id")
			.select("PRODD.product_code")
			.select("PRODD.product_name")
			.select("ARESD.region_geo_id", "region_id")
			.select("ARESD.region_name", "region_name")
			.select("CATED.category_name")
			.select("SUM(SOF.total_quantity)", "total_quantity")
			.select("SUM(SOF.total_selected_amount)", "total_selected_amount")
			.select("SUM(SOF.total_amount)", "total_amount")
			.select(columDateType, isFilterDateType)
			.from("product_dimension", "PRODD")
			.join(Join.INNER_JOIN, "sales_order_new_fact", "SOF", "PRODD.dimension_id = SOF.product_dim_id")
			.join(Join.INNER_JOIN, "address_dimension", "ARESD", "SOF.address_dim_id = ARESD.dimension_id")
			.join(Join.INNER_JOIN, "category_dimension", "CATED", "PRODD.primary_category_dim_id = CATED.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "VENDD", "SOF.vendor_dim_id = VENDD.dimension_id", !isViewRepOrg || !isViewRepParner)
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOF.entry_date_dim_id = ETYDIM.dimension_id")
			.groupBy("PRODD.product_id")
			.groupBy("PRODD.product_code")
			.groupBy("PRODD.product_name")
			.groupBy("ARESD.region_geo_id")
			.groupBy("ARESD.region_name")
			.groupBy("CATED.category_name")
			.groupBy(columDateType, isFilterDateType)
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
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("product_id", "product_id");
		addDataField("product_code", "product_code");
		addDataField("product_name", "product_name");
		addDataField("region_id", "region_id");
		addDataField("region_name", "region_name");
		addDataField("category_name", "category_name");
		addDataField("total_quantity", "total_quantity");
		addDataField("total_selected_amount", "total_selected_amount");
		addDataField("total_amount", "total_amount");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			addSeries("region_name");
			addXAxis("product_code");
			addYAxis("total_amount");
		}
		/*else if(getOlapResult() instanceof OlapPieChart) {
			String xAxisName = (String) getParameter("xAxisName");
			if (UtilValidate.isEmpty(xAxisName)) xAxisName = "product_code";
			addXAxis(xAxisName);
			addYAxis("total_amount");
		}*/
	}
}
