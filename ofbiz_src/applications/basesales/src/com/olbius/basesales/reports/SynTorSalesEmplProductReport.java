package com.olbius.basesales.reports;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.security.util.SecurityUtil;

public class SynTorSalesEmplProductReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private List<Map<String, Object>> columnsData;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("groupByExName", (String) context.get("groupByExName"));
		putParameter("orderStatusId", (String) context.get("orderStatusId"));
		putParameter("fileName", "SynTorSalesEmplProductReport"); // cache the specific file
		
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
		columnsData = getColumnData();
		String groupByExName = (String) getParameter("groupByExName");
		String groupByColumn = "total_amount";
		if ("byQuantity".equals(groupByExName)) groupByColumn = "total_quantity";
		
		OlbiusQuery query = makeQuery();
		
		String vendor_id = (String) getParameter("vendor_id");
		String orderStatusId = (String) getParameter("orderStatusId");

		Condition creatorCondStore = null;
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		query.select("EMPLD.party_id")
			.select("EMPLD.name")
			.from("sales_order_new_fact", "SOF")
			.join(Join.INNER_JOIN, "product_dimension", "PRODD", "SOF.product_dim_id = PRODD.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "EMPLD", "SOF.sales_empl_dim_id = EMPLD.dimension_id")
			.join(Join.INNER_JOIN, "party_dimension", "VENDD", "SOF.vendor_dim_id = VENDD.dimension_id", !isViewRepOrg || !isViewRepParner)
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOF.entry_date_dim_id = ETYDIM.dimension_id")
			.groupBy("EMPLD.party_id")
			.groupBy("EMPLD.name")
			.where(Condition.makeBetween("entry_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andEQ("SOF.is_promo", "N")
						.andEQ("VENDD.party_id", vendor_id, isViewRepOrg && !isViewRepParner)
						.andNotEQ("VENDD.party_id", vendor_id, !isViewRepOrg && isViewRepParner)
						.andEQ("SOF.order_status_id", orderStatusId, orderStatusId != null)
						.and(creatorCondStore, creatorCondStore != null));
		
		if (UtilValidate.isNotEmpty(columnsData)) {
			for (Map<String, Object> prod : columnsData) {
				String productId = (String) prod.get("productId");
				query.select("SUM(CASE WHEN PRODD.product_id = '" + productId + "' THEN SOF." + groupByColumn + " ELSE 0 END)", "PROD" + productId);
			}
		}
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("party_id", "party_id");
		addDataField("name", "name");
		
		if (UtilValidate.isNotEmpty(columnsData)) {
			for (Map<String, Object> prod : columnsData) {
				String productId = "PROD" + (String) prod.get("productId");
				addDataField(productId, productId);
			}
		}
	}
	
	private List<Map<String, Object>> getColumnData(){
		return ReportSalesUtils.getProductSalesOrder(this.delegator);
	};
}
