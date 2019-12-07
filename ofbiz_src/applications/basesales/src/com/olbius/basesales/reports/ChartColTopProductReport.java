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

public class ChartColTopProductReport extends OlbiusOlapService {
	private final String BY_PRODUCT = "PRODUCT";
	private final String BY_SALES_EXEC = "SALES_EXECUTIVE";
	private String xAsisName = "";
	
	private OlbiusQuery query;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		//putParameter("dateType", (String) context.get("dateType"));
		putParameter("filterTop", (Integer) context.get("filterTop"));
		putParameter("filterSort", (String) context.get("filterSort"));
		putParameter("filterSalesChannel", (String) context.get("filterSalesChannel"));
		putParameter("filterType", (String) context.get("filterType"));
		putParameter("fileName", "ChartColTopProductReport"); // cache the specific file
		
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
		String filterSalesChannel = (String) getParameter("filterSalesChannel");
		String filterType = (String) getParameter("filterType");
		
		if (BY_SALES_EXEC.equals(filterType)) {
			xAsisName = "party_code";
		} else {
			filterType = BY_PRODUCT;
			xAsisName = "product_code";
		}
		
		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		query.select("PRODD.product_id", BY_PRODUCT.equals(filterType))
			.select("PRODD.product_code", BY_PRODUCT.equals(filterType))
			.select("EXECD.party_id", BY_SALES_EXEC.equals(filterType))
			.select("EXECD.party_code", BY_SALES_EXEC.equals(filterType))
			.select("SUM(SOF.total_amount)", "total_amount")
			.from("sales_order_new_fact", "SOF")
			.join(Join.INNER_JOIN, "product_dimension", "PRODD", "SOF.product_dim_id = PRODD.dimension_id", BY_PRODUCT.equals(filterType))
			.join(Join.INNER_JOIN, "enumeration_dimension", "SCHAND", "SOF.sales_method_channel_dim_id = SCHAND.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "EXECD", "SOF.sales_exec_dim_id = EXECD.dimension_id", BY_SALES_EXEC.equals(filterType))
			.join(Join.INNER_JOIN, "party_dimension", "VENDD", "SOF.vendor_dim_id = VENDD.dimension_id", !isViewRepOrg || !isViewRepParner)
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOF.entry_date_dim_id = ETYDIM.dimension_id")
			.groupBy("PRODD.product_id", BY_PRODUCT.equals(filterType))
			.groupBy("PRODD.product_code", BY_PRODUCT.equals(filterType))
			.groupBy("EXECD.party_id", BY_SALES_EXEC.equals(filterType))
			.groupBy("EXECD.party_code", BY_SALES_EXEC.equals(filterType))
			.where(Condition.makeBetween("SOF.entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("SOF.is_promo", "N")
						.andEQ("VENDD.party_id", vendor_id, isViewRepOrg && !isViewRepParner)
						.andNotEQ("VENDD.party_id", vendor_id, !isViewRepOrg && isViewRepParner)
						.andEQ("SOF.order_status_id", "ORDER_COMPLETED")
						.andEQ("SCHAND.enum_id", filterSalesChannel, UtilValidate.isNotEmpty(filterSalesChannel))
						.and(creatorCondStore, creatorCondStore != null))
			.orderBy("SUM(SOF.total_amount) " + filterSort)
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
			addXAxis(xAsisName);
			addYAxis("total_amount");
		}
	}
}
