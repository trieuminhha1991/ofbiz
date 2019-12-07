package com.olbius.salesmtl.distributor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.baselogistics.util.LogisticsFacilityUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.util.FacilityUtil;

import javolution.util.FastList;
import javolution.util.FastSet;

public class ReceiveServices {
	public static final String module = ReceiveServices.class.getName();
	public static final String resource = "widgetUiLabels";
	public static final String resourceError = "widgetErrorUiLabels";
	public static final String resource_error = "OrderUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetShipmentPurchDis(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Set<String> listSelectFields = FastSet.newInstance();
		List<GenericValue> listIterator = null;
		listAllConditions.add(EntityCondition.makeCondition("shipmentTypeId", "PURCH_DIS_SHIPMENT"));
		List<String> listFacilityIds = FastList.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			listFacilityIds = LogisticsFacilityUtil.getFacilityAllowedView(delegator, userLogin);
		} catch (GenericEntityException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: jqGetShipmentPurchDis error!");
		}
		if (!listFacilityIds.isEmpty()){
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.IN, listFacilityIds));
			conds.add(EntityCondition.makeCondition("destinationFacilityId", EntityOperator.IN, listFacilityIds));
			listAllConditions.add(EntityCondition.makeCondition(conds, EntityOperator.OR));
			try {
				listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "Shipment", 
						EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSelectFields, listSortFields, opts);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: jqGetShipmentPurchDis error!");
			}
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListDeliverySalesDisReceive(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		listAllConditions.add(EntityCondition.makeCondition("deliveryTypeId", "DELIVERY_SALES"));
		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("DLV_EXPORTED", "DLV_DELIVERED")));
		listAllConditions.add(EntityCondition.makeCondition("shipmentDistributorId", EntityOperator.EQUALS, null));
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		listAllConditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
		if (!listSortFields.isEmpty()){
			listSortFields.add("-deliveryId");
		}
		try {
			listIterator = delegator.find("Delivery", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: jqGetListDeliverySalesDisReceive error!");
		}
		
		result.put("listIterator", listIterator);
		return result;
	}
}
