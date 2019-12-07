package com.olbius.baselogistics.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.util.FacilityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ReportServices {
	
	public static final String module = ReportServices.class.getName();
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetInventoryByProduct(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");

		List<EntityCondition> listHavingConditions = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		List<GenericValue> listInvs = new ArrayList<GenericValue>();
		List<Map<String, Object>> listReturns = new ArrayList<Map<String, Object>>();

		try {
			if (listSortFields.isEmpty()) {
				listSortFields.add("datetimeReceived");
			}
			
			if (listSortFields.contains("-numberDayInv")) {
				listSortFields.remove("-numberDayInv");
				listSortFields.add("datetimeReceived");
			} else if (listSortFields.contains("numberDayInv")) {
				listSortFields.remove("numberDayInv");
				listSortFields.add("-datetimeReceived");
			}

			if (UtilValidate.isNotEmpty(listAllConditions)) {
				for (EntityCondition cond : listAllConditions) {
					if (cond.toString().contains("qohEA")) {
						listHavingConditions.add(cond);
					} else {
						conditions.add(cond);
					}
				}
			}
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Security security = ctx.getSecurity();
			List<String> listFacilityIds = FastList.newInstance();
	    	boolean transferAdmin = com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "ADMIN", "MODULE", "LOG_FACILITY");
	    	if (!transferAdmin) {
				listFacilityIds = FacilityUtil.getFacilityManages(delegator, userLogin);
	    	} else {
	    		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
	    		List<GenericValue> listFacility = FastList.newInstance();
				try {
					listFacility = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", company), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList Facility: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (!listFacility.isEmpty()){
					listFacilityIds = EntityUtil.getFieldListFromEntityList(listFacility, "facilityId", true);
				}
	    	}
	    	
	    	if (!listFacilityIds.isEmpty()){
				listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityIds));
			}
	    	listInvs = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "InventoryItemTotalByProductFull", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if (!listInvs.isEmpty()) {
				for (GenericValue tmp : listInvs) {
					Map<String, Object> map = FastMap.newInstance();
					map.putAll(tmp);
					String productId = tmp.getString("productId");
					
					String primaryUPC =  null;
					List<Map<String, Object>> listUPCs = FastList.newInstance();
					List<GenericValue> listGoodIdentification = delegator.findList("GoodIdentification",
							EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
					if (!listGoodIdentification.isEmpty()){
						for (GenericValue item : listGoodIdentification) {
							if (UtilValidate.isNotEmpty(item.get("iupprm"))) {
								if ((Long)item.get("iupprm") == 1){
									primaryUPC = listGoodIdentification.get(0).getString("idValue");
								}
							}
							Map<String, Object> mapSKU = FastMap.newInstance();
							mapSKU.put("idValue", item.get("idValue"));
							listUPCs.add(mapSKU);
						}
						map.put("primaryUPC", primaryUPC);
						map.put("listUPCs", listUPCs);
					}
					
					int numberDayInv = 0;
					Timestamp datetimeReceived = tmp.getTimestamp("datetimeReceived");
					Timestamp now = new Timestamp(System.currentTimeMillis());
					if (UtilValidate.isNotEmpty(datetimeReceived)) {
						numberDayInv = UtilDateTime.getIntervalInDays(datetimeReceived, now);
					}
					map.put("numberDayInv", numberDayInv);

					BigDecimal qohEA = tmp.getBigDecimal("qohEA");
					BigDecimal quantityConvert = tmp.getBigDecimal("quantityConvert");
					if (UtilValidate.isEmpty(quantityConvert) || quantityConvert.equals(BigDecimal.ZERO)) {
						quantityConvert = BigDecimal.ONE;
					}
					BigDecimal qohQC = qohEA.divide(quantityConvert, 1, RoundingMode.HALF_UP);

					map.put("qohQC", qohQC);
					map.put("quantityConvert", quantityConvert);

					listReturns.add(map);
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: jqGetInventoryByProduct error");
		}
		
		successResult.put("listIterator", listReturns);
		return successResult;
	}
}
