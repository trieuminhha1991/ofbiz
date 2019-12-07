package com.olbius.basesales.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
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

import javolution.util.FastMap;

public class SynTorProductProdStoreReport extends OlbiusOlapService {

	private OlbiusQuery query;
	private List<Map<String, Object>> columnProductStores;
	private Boolean isViewRepOrg = true;
	private Boolean isViewRepParner = false;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		putParameter("xAxisName", (String) context.get("xAxisName"));
		putParameter("orderStatusId", (String) context.get("orderStatusId"));
		putParameter("fileName", "SynTorProductProdStoreReport"); // cache the specific file
		
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
		columnProductStores = getProductStore();
		
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
		creatorCondStore = ReportSalesUtils.makeCondFindByProdStoreRole(delegator, (String) getParameter("userLoginPartyId"), "SOF", isViewRepParner);
		
		query.select("PRODD.product_id")
			.select("PRODD.product_code")
			.select("PRODD.product_name")
			.from("sales_order_roll_store_and_product_fact", "SOF")
			.join(Join.INNER_JOIN, "product_dimension", "PRODD", "SOF.product_dim_id = PRODD.dimension_id")
			.join(Join.INNER_JOIN, "product_store_dimension", "PSD", "SOF.product_store_dim_id = PSD.dimension_id")
			.join(Join.INNER_JOIN, "date_dimension", "ETYDIM", "SOF.date_dim_id = ETYDIM.dimension_id")
			.groupBy("PRODD.product_id")
			.groupBy("PRODD.product_code")
			.groupBy("PRODD.product_name")
			.where(Condition.makeBetween("date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
							.andEQ("SOF.vendor_dim_id", vendor_dim_id, isViewRepOrg && !isViewRepParner)
							.andNotEQ("SOF.vendor_dim_id", vendor_dim_id, !isViewRepOrg && isViewRepParner)
							.and(creatorCondStore, creatorCondStore != null));
		
		if (UtilValidate.isNotEmpty(columnProductStores)) {
			for (Map<String, Object> prodStore : columnProductStores) {
				String storeId = (String) prodStore.get("productStoreId");
				query.select("SUM(CASE WHEN PSD.product_store_id = '" + storeId + "' THEN SOF.total_amount ELSE 0 END)", storeId);
			}
		}
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		//String dateType = getDateType((String) getParameter("dateType"));
		//addDataField("dateTime", dateType);
		addDataField("product_id", "product_id");
		addDataField("product_code", "product_code");
		addDataField("product_name", "product_name");
		
		if (UtilValidate.isNotEmpty(columnProductStores)) {
			for (Map<String, Object> prodStore : columnProductStores) {
				String storeId = (String) prodStore.get("productStoreId");
				addDataField(storeId, storeId);
			}
		}
	}
	
	private List<Map<String, Object>> getProductStore(){
		List<Map<String, Object>> resultValue = new ArrayList<Map<String, Object>>();
		try {
			List<GenericValue> prodStoreDimension = this.delegator.findByAnd("ProductStoreDimension", null, null, false);
			if (UtilValidate.isNotEmpty(prodStoreDimension)) {
				for (GenericValue prodStore : prodStoreDimension) {
					Map<String, Object> itemMap = FastMap.newInstance();
					itemMap.put("productStoreId", prodStore.get("productStoreId"));
					itemMap.put("storeName", prodStore.get("storeName"));
					resultValue.add(itemMap);
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError("Error when query ", module);
		}
		
		return resultValue;
	};
}
