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

public class TorReturnProdStoreReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		/*putParameter("xAxisName", (String) context.get("xAxisName"));*/
		putParameter("returnStatusId", (String) context.get("returnStatusId"));
		putParameter("fileName", "TorReturnProdStoreReport"); // cache the specific file
		
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

		boolean isFilterDateType = true;
		if (getOlapResult() instanceof AbstractOlapChart) isFilterDateType = false;

		String vendor_id = (String) getParameter("vendor_id");
		String dateType = getDateType((String) getParameter("dateType"));
		String columDateType = "ETYDIM.".concat(dateType);
		String returnStatusId = (String) getParameter("returnStatusId");

		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		query.select("PSD.product_store_id")
			.select("PSD.store_name")
			.select("COUNT(DISTINCT RSOF.return_id)", "num_order")
			.select("SUM(RSOF.received_quantity)", "received_quantity")
			.select("SUM(RSOF.received_amount)", "received_amount")
			.select("SUM(RSOF.return_total_amount)", "return_total_amount")
			.select(columDateType, isFilterDateType)
			.from("return_sales_order_new_fact", "RSOF")
			.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "RSOF.product_store_dim_id = PSD.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "VENDD", "RSOF.vendor_dim_id = VENDD.dimension_id", !isViewRepOrg || !isViewRepParner)
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "RSOF.entry_date_dim_id = ETYDIM.dimension_id")
			.groupBy("PSD.product_store_id")
			.groupBy("PSD.store_name")
			.groupBy(columDateType, isFilterDateType)
			.where(Condition.makeBetween("entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("RSOF.is_promo", "N")
						.andEQ("VENDD.party_id", vendor_id, isViewRepOrg && !isViewRepParner)
						.andNotEQ("VENDD.party_id", vendor_id, !isViewRepOrg && isViewRepParner)
						.andEQ("RSOF.return_status_id", returnStatusId, returnStatusId != null)
						.and(creatorCondStore, creatorCondStore != null));
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("product_store_id", "product_store_id");
		addDataField("store_name", "store_name");
		addDataField("num_order", "num_order");
		addDataField("received_quantity", "received_quantity");
		addDataField("received_amount", "received_amount");
		addDataField("return_total_amount", "return_total_amount");
	}
	
	@Override
	public void prepareResultChart() {
		if(getOlapResult() instanceof OlapColumnChart) {
			//addSeries("store_name");
			addXAxis("store_name");
			addYAxis("return_total_amount");
		}
		/*else if(getOlapResult() instanceof OlapPieChart) {
			String xAxisName = (String) getParameter("xAxisName");
			if (UtilValidate.isEmpty(xAxisName)) xAxisName = "product_code";
			addXAxis(xAxisName);
			addYAxis("total_amount");
		}*/
	}

	/*@Override
	protected OlapResultQueryInterface returnResultGrid() {
		return new ReturnResultGrid();
	}

	private class ReturnResultGrid extends ReturnResultGridEx {

		@Override
		protected Map<String, Object> getObject(ResultSet result) {
			Map<String, Object> map = super.getObject(result);
			try {
				map.put("product_id", result.getString("product_id"));
				map.put("product_code", result.getString("product_code"));
				map.put("product_name", result.getString("product_name"));
				map.put("product_store_id", result.getString("product_store_id"));
				map.put("store_name", result.getString("store_name"));
				map.put("category_name", result.getString("category_name"));
				map.put("total_quantity", result.getString("total_quantity"));
				map.put("total_amount", result.getString("total_amount"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return map;
		}
	}*/
}
