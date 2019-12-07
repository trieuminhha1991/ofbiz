package com.olbius.baselogistics.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.util.FacilityUtil;

import javolution.util.FastList;

public class ChartColTopReceivePurchase extends OlbiusOlapService {
	private OlbiusQuery query;
	private GenericValue userLogin = null;
	
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("filterTop", (Integer) context.get("filterTop"));
		putParameter("filterSort", (String) context.get("filterSort"));
		putParameter("filterProductStore", (String) context.get("filterProductStore"));
		putParameter("filterPrimaryCategory", (String) context.get("filterPrimaryCategory"));
		userLogin = (GenericValue) context.get("userLogin");
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}

	private List<Object> getFacilities(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = FacilityDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}
	
	private OlbiusQuery init() {

		OlbiusQuery query = makeQuery();

		Integer filterTop = (Integer) getParameter("filterTop");
		String filterSort = (String) getParameter("filterSort");
		if (UtilValidate.isEmpty(filterTop)) filterTop = 5;
		if (!"DESC".equals(filterSort) && !"ASC".equals(filterSort)) filterSort = "DESC";
		String filterProductStore = (String) getParameter("filterProductStore");
		String filterPrimaryCategory = (String) getParameter("filterPrimaryCategory");
		List<String> listFacilityIds = FastList.newInstance();
		List<Object> facilities = FastList.newInstance();
		if ("ALL".equals(filterProductStore)){
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

			Boolean hasRole = com.olbius.basehr.util.SecurityUtil.hasRole("LOG_ADMIN", userLogin.getString("partyId"), delegator);
			if (!hasRole){
				hasRole = com.olbius.basehr.util.SecurityUtil.hasRole("ACC_EMPLOYEE", userLogin.getString("partyId"), delegator);
			}
			if (hasRole){ 
				List<GenericValue> listFas = FastList.newInstance();
				try {
					listFas = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", company), null, null, null, false);
				} catch (GenericEntityException e) {
					Debug.logError(e.toString(), module);
					return null;
				}
				if (!listFas.isEmpty()){
					listFacilityIds = EntityUtil.getFieldListFromEntityList(listFas, "facilityId", true);
				}
			} else  { 
				listFacilityIds = FacilityUtil.getFacilityManages(delegator, userLogin);
			}
		} else {
			listFacilityIds.add(filterProductStore);
		}
		facilities = getFacilities(listFacilityIds);
		query.select("pr.product_id")
		.select("pr.product_code")
		.select(new Sum("CASE WHEN pr.require_amount = 'Y' THEN iif.amount_on_hand_total ELSE iif.quantity_on_hand_total END"), "quantity")
		.from("inventory_item_fact", "iif")
		.join(Join.INNER_JOIN, "product_dimension", "pr", "iif.product_dim_id = pr.dimension_id")
		.join(Join.INNER_JOIN, "category_dimension", "ct", "iif.root_category_dim_id = ct.dimension_id")
		.groupBy("pr.product_id")
		.groupBy("pr.product_code")
		.where(Condition.makeBetween("iif.inventory_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
					.andEQ("ct.category_id", filterPrimaryCategory, UtilValidate.isNotEmpty(filterPrimaryCategory))
					.andIn("iif.facility_dim_id", facilities)
					.andEQ("iif.inventory_change_type", "PURCHASE_ORDER")
					.andEQ("pr.product_type", "FINISHED_GOOD")
					.andEQ("iif.inventory_type", "RECEIVE"))
		.orderBy("quantity " + filterSort)
		.limit(filterTop);
		
		return query;
	}

	@Override
	public void prepareResultChart() {
		if (getOlapResult() instanceof OlapColumnChart) {
			addXAxis("product_code");
			addYAxis("quantity");
		}
	}
}