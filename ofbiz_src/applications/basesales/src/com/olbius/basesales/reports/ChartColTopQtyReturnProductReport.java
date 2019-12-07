package com.olbius.basesales.reports;

import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.security.util.SecurityUtil;

public class ChartColTopQtyReturnProductReport extends OlbiusOlapService {
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
		putParameter("fileName", "ChartColTopQtyReturnProductReport"); // cache the specific file
		
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
		//String dateType = getDateType((String) getParameter("dateType"));
		//String columDateType = "ETYDIM.".concat(dateType);
		Integer filterTop = (Integer) getParameter("filterTop");
		String filterSort = (String) getParameter("filterSort");
		if (UtilValidate.isEmpty(filterTop)) filterTop = 5;
		if (!"DESC".equals(filterSort) && !"ASC".equals(filterSort)) filterSort = "DESC";
		String filterProductStore = (String) getParameter("filterProductStore");
		
		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "RSOF");
		
		query.select("PRODD.product_id")
			.select("PRODD.product_code")
			.select("SUM(RSOF.received_quantity)", "received_quantity")
			.from("return_sales_order_new_fact", "RSOF")
			.join(Join.INNER_JOIN, "product_dimension", "PRODD", "RSOF.product_dim_id = PRODD.dimension_id")
			.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "RSOF.product_store_dim_id = PSD.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "VENDD", "RSOF.vendor_dim_id = VENDD.dimension_id", !isViewRepOrg || !isViewRepParner)
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "RSOF.entry_date_dim_id = ETYDIM.dimension_id")
			.groupBy("PRODD.product_id")
			.groupBy("PRODD.product_code")
			.where(Condition.makeBetween("RSOF.entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("RSOF.is_promo", "N")
						.andEQ("VENDD.party_id", vendor_id, isViewRepOrg && !isViewRepParner)
						.andNotEQ("VENDD.party_id", vendor_id, !isViewRepOrg && isViewRepParner)
						.andEQ("RSOF.return_status_id", "RETURN_COMPLETED")
						.andEQ("PSD.product_store_id", filterProductStore, UtilValidate.isNotEmpty(filterProductStore))
						.and(creatorCondStore, creatorCondStore != null))
			.orderBy("SUM(RSOF.return_quantity) " + filterSort)
			.limit(filterTop);
		
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
			addYAxis("received_quantity");
		}
	}
}