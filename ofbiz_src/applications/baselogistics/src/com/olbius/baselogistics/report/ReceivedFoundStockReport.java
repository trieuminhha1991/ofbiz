package com.olbius.baselogistics.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.util.FacilityUtil;

import javolution.util.FastList;

public class ReceivedFoundStockReport extends OlbiusOlapService{
	
	private OlbiusQuery query;
	private GenericValue userLogin = null;
	private String module = ExportedTotalReport.class.getName();
	
	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		putParameter("dateType", (String) context.get("dateType"));
		putParameter("facility", context.get("facility[]"));
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
		OlbiusQuery tmp = makeQuery();
		OlbiusQuery tmp2 = makeQuery();
		List<Object> facilities = getFacilities((List<?>) getParameter("facility"));
		if (facilities.isEmpty()){
			List<String> listFacilityIds = FastList.newInstance();
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
			
			facilities = getFacilities(listFacilityIds);
		}
		String dateType = getDateType((String) getParameter("dateType"));
		String slt = "facility_dim_id, product_dim_id";
		boolean flag = true;
		
		tmp.select(new Sum("CASE WHEN prd.require_amount = 'Y' THEN iif.amount_on_hand_total ELSE iif.quantity_on_hand_total END"), "quantity")
				.from("inventory_item_fact", "iif")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "iif.inventory_date_dim_id = dd.dimension_id")
				.join(Join.INNER_JOIN, "product_dimension", "prd", "iif.product_dim_id = prd.dimension_id")
				.where(Condition
						.makeBetween("iif.inventory_date_dim_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
						.andIn("iif.facility_dim_id", facilities)
						.andEQ("prd.product_type", "FINISHED_GOOD")
						.andEQ("iif.inventory_change_type", "STOCKING")
						.andEQ("iif.inventory_type", "RECEIVE"));
		tmp.select(dateType).select(slt, flag).groupBy(slt, flag).groupBy(dateType);
			
		tmp2.select("tmp.*")
		.select("fd.facility_id")
		.select("fd.facility_name")
		.select("fd.facility_code")
		.select("prd.product_id").select("prd.product_name").select("prd.product_code")
		.select("CASE WHEN prd.require_amount = 'Y' THEN prd.weight_uom_dim_id ELSE prd.uom_dim_id END as uom_id")
		.from(tmp, "tmp")
		.join(Join.INNER_JOIN, "facility_dimension", "fd", "fd.dimension_id = tmp.facility_dim_id")
		.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = tmp.product_dim_id")
		.where(Condition.make("tmp.".concat(dateType).concat(" is not null")));
		
		query.select("tmp.facility_id")
		.select("tmp.facility_dim_id") 
		.select("tmp.product_dim_id")
		.select("tmp.quantity")
		.select("tmp.".concat(dateType))
		.select("tmp.product_code") 
		.select("tmp.product_id")
		.select("tmp.product_name")
		.select("tmp.facility_name")
		.select("tmp.facility_code")
		.select("ud.currency_id", "uom_id").select("ud.description", "uom_name")
		.join(Join.INNER_JOIN, "currency_dimension", "ud", "ud.dimension_id = tmp.uom_id")
		.from(tmp2, "tmp");
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		String dateType = getDateType((String) getParameter("dateType"));
		addDataField("dateTime", dateType);
		addDataField("quantity", "quantity");
		addDataField("facility_name", "facility_name");
		addDataField("facility_code", "facility_code");
		addDataField("product", "product_name");
		addDataField("facility_id", "facility_id");
		addDataField("product_code", "product_code");
		addDataField("uom", "uom_name");
	}
}