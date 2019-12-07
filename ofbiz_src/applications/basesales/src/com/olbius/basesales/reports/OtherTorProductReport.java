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
import com.olbius.bi.olap.chart.AbstractOlapChart;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.security.util.SecurityUtil;

public class OtherTorProductReport extends OlbiusOlapService {

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
		putParameter("fileName", "OtherTorProductReport"); // cache the specific file
		
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
		
		String dateType = getDateType((String) getParameter("dateType"));
		String columDateType = "ETYDIM.".concat(dateType);
		
		boolean isFilterDateType = true;
		if (getOlapResult() instanceof AbstractOlapChart) isFilterDateType = false;

		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		OlbiusQuery innerQuery = makeQuery();
		innerQuery.select("SOF.product_dim_id")
			.select("SUM(SOF.total_quantity)", "total_quantity")
			.select("SUM(SOF.total_selected_amount)", "total_selected_amount")
			.select("SUM(SOF.total_amount)", "total_amount")
			.select(columDateType, isFilterDateType)
			.from("sales_order_roll_store_and_product_fact", "SOF")
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOF.date_dim_id = ETYDIM.dimension_id")
			.groupBy("SOF.product_dim_id")
			.groupBy(columDateType, isFilterDateType)
			.where(Condition.makeBetween("SOF.date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
					.andEQ("SOF.vendor_dim_id", vendor_dim_id, isViewRepOrg && !isViewRepParner)
					.andNotEQ("SOF.vendor_dim_id", vendor_dim_id, !isViewRepOrg && isViewRepParner)
					.and(creatorCondStore, creatorCondStore != null));
		
		query.select("PRODD.product_id")
			.select("PRODD.product_code")
			.select("PRODD.product_name")
			.select("CATED.category_id", "category_name")
			.select("TMP.total_quantity")
			.select("TMP.total_selected_amount")
			.select("TMP.total_amount")
			.select("TMP.".concat(dateType), isFilterDateType)
			.from(innerQuery, "TMP")
			.join(Join.INNER_JOIN, "product_dimension", "PRODD", "TMP.product_dim_id = PRODD.dimension_id")
			.join(Join.INNER_JOIN, "category_dimension", "CATED", "PRODD.primary_category_dim_id = CATED.dimension_id");
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("product_id", "product_id");
		addDataField("product_code", "product_code");
		addDataField("product_name", "product_name");
		addDataField("category_name", "category_name");
		addDataField("total_quantity", "total_quantity");
		addDataField("total_selected_amount", "total_selected_amount");
		addDataField("total_amount", "total_amount");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			//addSeries("category_name");
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
