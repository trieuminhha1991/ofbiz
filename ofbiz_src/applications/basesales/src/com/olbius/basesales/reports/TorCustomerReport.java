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

public class TorCustomerReport extends OlbiusOlapService {

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
		putParameter("fileName", "TorCustomerReport"); // cache the specific file
		
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
		
		query.select("PSD.product_store_id")
			.select("PSD.store_name")
			.select("CUSTD.party_id", "customer_id")
			.select("CUSTD.party_code", "customer_code")
			.select("CUSTD.name", "customer_name")
			.select("SUM(SOF.total_quantity)", "total_quantity")
			.select("SUM(SOF.total_selected_amount)", "total_selected_amount")
			.select("SUM(SOF.total_amount)", "total_amount")
			.select(columDateType, isFilterDateType)
			.from("product_dimension", "PRODD")
			.join(Join.INNER_JOIN, "sales_order_new_fact", "SOF", "PRODD.dimension_id = SOF.product_dim_id")
			.join(Join.INNER_JOIN, "party_dimension", "CUSTD", "SOF.customer_dim_id = CUSTD.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "VENDD", "SOF.vendor_dim_id = VENDD.dimension_id", !isViewRepOrg || !isViewRepParner)
			.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "SOF.product_store_dim_id = PSD.dimension_id")
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOF.entry_date_dim_id = ETYDIM.dimension_id")
			.groupBy("CUSTD.party_id")
			.groupBy("CUSTD.party_code")
			.groupBy("CUSTD.name")
			.groupBy("PSD.product_store_id")
			.groupBy("PSD.store_name")
			.groupBy(columDateType, isFilterDateType)
			.where(Condition.makeBetween("entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
							.andEQ("SOF.is_promo", "N")
							.andEQ("VENDD.party_id", vendor_id, isViewRepOrg && !isViewRepParner)
							.andNotEQ("VENDD.party_id", vendor_id, !isViewRepOrg && isViewRepParner)
							.and("CUSTD.party_id <> '_NA_'")
							.and("CUSTD.party_id is not null")
							.andEQ("SOF.order_status_id", orderStatusId, orderStatusId != null)
							.and(creatorCondStore, creatorCondStore != null));
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("customer_id", "customer_id");
		addDataField("customer_code", "customer_code");
		addDataField("customer_name", "customer_name");
		addDataField("product_store_id", "product_store_id");
		addDataField("store_name", "store_name");
		addDataField("total_quantity", "total_quantity");
		addDataField("total_selected_amount", "total_selected_amount");
		addDataField("total_amount", "total_amount");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			//addSeries("sales_method_channel_name");
			addXAxis("customer_code");
			addYAxis("total_amount");
		}
		/*else if(getOlapResult() instanceof OlapPieChart) {
			String xAxisName = (String) getParameter("xAxisName");
			if (UtilValidate.isEmpty(xAxisName)) xAxisName = "customer_id";
			addXAxis(xAxisName);
			addYAxis("total_amount");
		}*/
	}
}
