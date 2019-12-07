package com.olbius.basesales.reports;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.security.util.SecurityUtil;

public class SalesForecastReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		setFromDate(new Date(fromDate.getTime()));
		setThruDate(new Date(thruDate.getTime()));
		putParameter("dateType", (String) context.get("dateType"));
		putParameter("xAxisName", (String) context.get("xAxisName"));
		putParameter("fileName", "SalesForecastReport"); // cache the specific file
		
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

		Condition creatorCondStore = null;
		//creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "S4C");
		
		query.select("PRODD.product_id")
			.select("PRODD.product_code")
			.select("PRODD.product_name")
			.select("EXEPTY.party_code")
			.select("CTPD.custom_time_period_id")
			.select("S4C.sales_forecast_id")
			.select("S4C.sales_forecast_detail_seq_id")
			.select("S4C.parent_sales_forecast_id")
			.select("S4C.num_day")
			.select("S4C.currency_uom_id")
			.select("S4C.quantity_uom_id")
			.select("S4C.quantity")
			.select("S4C.amount")
			.select("S4C.actual_quantity")
			.select("S4C.actual_amount")
			.select("S4C.report_quantity")
			.select("S4C.report_amount")
			.from("sales_forecast_fact", "S4C")
			.join(Join.INNER_JOIN, "product_dimension", "PRODD", "S4C.product_dim_id = PRODD.dimension_id")
			.join(Join.INNER_JOIN, "custom_time_period_dimension", "CTPD", "S4C.custom_time_period_dim_id = CTPD.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "EXEPTY", "S4C.execute_party_dim_id = EXEPTY.dimension_id")
			.where(Condition.make("CTPD.from_date", ">=", this.fromDate)
							.and("CTPD.thru_date", "<=", this.thruDate)
							.andEQ("S4C.organization_party_dim_id", vendor_dim_id, isViewRepOrg && !isViewRepParner)
							.andNotEQ("S4C.organization_party_dim_id", vendor_dim_id, !isViewRepOrg && isViewRepParner)
							.and(creatorCondStore, creatorCondStore != null));
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("product_id", "product_id");
		addDataField("product_code", "product_code");
		addDataField("product_name", "product_name");
		addDataField("party_code", "party_code");
		addDataField("custom_time_period_id", "custom_time_period_id");
		addDataField("sales_forecast_id", "sales_forecast_id");
		addDataField("sales_forecast_detail_seq_id", "sales_forecast_detail_seq_id");
		addDataField("parent_sales_forecast_id", "parent_sales_forecast_id");
		addDataField("num_day", "num_day");
		addDataField("currency_uom_id", "currency_uom_id");
		addDataField("quantity_uom_id", "quantity_uom_id");
		addDataField("quantity", "quantity");
		addDataField("amount", "amount");
		addDataField("actual_quantity", "actual_quantity");
		addDataField("actual_amount", "actual_amount");
		addDataField("report_quantity", "report_quantity");
		addDataField("report_amount", "report_amount");
	}
	
}
