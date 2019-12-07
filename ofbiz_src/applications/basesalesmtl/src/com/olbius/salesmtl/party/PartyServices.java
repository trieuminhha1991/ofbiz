package com.olbius.salesmtl.party;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.olbius.common.util.EntityMiscUtil;
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
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;

import javolution.util.FastList;
import javolution.util.FastSet;

public class PartyServices {
	
	public static final String module = PartyServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
	
    @SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> jqGetPartiesDistributor(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	try {
    		String orgId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		if (UtilValidate.isNotEmpty(orgId)) {
    			listAllConditions.add(EntityCondition.makeCondition("partyIdTo", orgId));
    			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "DISTRIBUTOR"));
    			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
    			listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "DISTRIBUTOR_REL"));
    			listAllConditions.add(EntityUtil.getFilterByDateExpr());
    			listIterator = delegator.find("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetPartiesDistributor service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String, Object> getListDistributorIdBySup(DispatchContext ctx, Map<String, Object> context) {
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
    	List<String> listDistributorIds = null;
    	String supervisorId = (String) context.get("partyId");
    	try {
    		List<String> deptIds = PartyWorker.getDeptByManager(delegator, supervisorId);
    		if (UtilValidate.isNotEmpty(deptIds)) {
    			List<EntityCondition> listAllCondition = FastList.newInstance();
    			listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, deptIds));
    			listAllCondition.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALESSUP_DEPT", "roleTypeIdTo", "DISTRIBUTOR", "partyRelationshipTypeId", "DISTRIBUTION")));
    			listAllCondition.add(EntityUtil.getFilterByDateExpr());
    			listDistributorIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListDistributorIdBySup service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listDistributorIds", listDistributorIds);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> partyAddress(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			if (parameters.containsKey("partyId")) {
				String partyId = parameters.get("partyId")[0];
				if (parameters.containsKey("contactMechPurposeTypeId")) {
					String contactMechPurposeTypeId = parameters.get("contactMechPurposeTypeId")[0];
					if (UtilValidate.isNotEmpty(contactMechPurposeTypeId)) {
						listAllConditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityJoinOperator.EQUALS, contactMechPurposeTypeId));
					}
				}
				Set<String> listSelectFields = null;
				String distinctAddress = SalesUtil.getParameter(parameters, "distinctAddress");
				if ("Y".equals(distinctAddress)) {
					listSelectFields = FastSet.newInstance();
					listSelectFields.add("contactMechId");
					listSelectFields.add("toName");
					listSelectFields.add("attnName");
					listSelectFields.add("address1");
					listSelectFields.add("address2");
					listSelectFields.add("postalCodeExt");
					listSelectFields.add("countryGeoId");
					listSelectFields.add("countryGeoName");
					listSelectFields.add("stateProvinceGeoId");
					listSelectFields.add("stateProvinceGeoName");
					listSelectFields.add("districtGeoId");
					listSelectFields.add("districtGeoName");
					listSelectFields.add("wardGeoId");
					listSelectFields.add("wardGeoName");
					listSelectFields.add("roadGeoId");
					listSelectFields.add("city");
					listSelectFields.add("directions");
					listSelectFields.add("postalCode");
					listSelectFields.add("postalCodeGeoId");
					listSelectFields.add("geoPointId");
					listSelectFields.add("longitude");
					listSelectFields.add("latitude");
					listSelectFields.add("yearsWithContactMech");
					listSelectFields.add("allowSolicitation");
					listSelectFields.add("extension");
					listSelectFields.add("verified");
					listSelectFields.add("monthsWithContactMech");
					listSelectFields.add("comments");
					listSelectFields.add("contactMechPurposeTypeId");
					listSelectFields.add("thruDate");
					listSelectFields.add("fromDate");
					
					opts.setDistinct(true);
				}
				listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
				EntityListIterator listIterator = delegator.find("PartyAddressPurpose",
						EntityCondition.makeCondition(listAllConditions), null, listSelectFields, listSortFields, opts);
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> listCheckInHistory(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
            Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listSortFields.add("-checkInDate");
            EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters,successResult,delegator,"CheckInHistoryDetails",
                    EntityCondition.makeCondition(listAllConditions),null,null,listSortFields,opts);
			successResult.put("listIterator", listIterator);
		} catch (Exception e) {
			String errMsg = "Fatal error calling JQGetListCheckInHistory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	public static Map<String, Object> deletePartyContactMechAndPurpose(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String contactMechId = (String) context.get("contactMechId");
		
		List<EntityCondition> contactMechCond = FastList.newInstance();
		contactMechCond.add(EntityCondition.makeCondition("partyId", partyId));
		contactMechCond.add(EntityCondition.makeCondition("contactMechId", contactMechId));
		contactMechCond.add(EntityUtil.getFilterByDateExpr());
		try {
			//delete contactMech
			List<GenericValue> contactMech = delegator.findList("PartyContactMech", EntityCondition.makeCondition(contactMechCond, EntityOperator.AND), null, null, null, false);
			for(GenericValue contact : contactMech) {
				contact.set("thruDate", UtilDateTime.nowTimestamp());
				delegator.createOrStore(contact);
			}
			
			//delete contactMechPurpose
			List<GenericValue> contactMechPurpose = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(contactMechCond, EntityOperator.AND), null, null, null, false);
			for(GenericValue purpose: contactMechPurpose) {
				purpose.set("thruDate", UtilDateTime.nowTimestamp());
				delegator.createOrStore(purpose);
			}
			
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling deletePartyContactMechAndPurpose service: " + e.toString();
			Debug.logError(e, errMsg, module);
			e.printStackTrace();
		}
		
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetPartyTypeByChannel(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	//GenericValue userLogin = (GenericValue) context.get("userLogin");
    	try {
    		String salesMethodChannelEnumId = SalesUtil.getParameter(parameters, "salesMethodChannelEnumId");
    		
    		listAllConditions.add(EntityCondition.makeCondition("enumId", salesMethodChannelEnumId));
    		listAllConditions.add(EntityUtil.getFilterByDateExpr());
    		listIterator = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "PartyTypeEnumAssocDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetPartyTypeByChannel service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetSalesmanByProductStore(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		//GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String productStoreId = SalesUtil.getParameter(parameters, "productStoreId");
			
			listAllConditions.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			listAllConditions.add(EntityUtil.getFilterByDateExpr("storeFromDate", "storeThruDate"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr("partyRelFromDate", "partyRelThruDate"));
			opts.setDistinct(true);
			listIterator = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ProductStoreRoleAndRelSalesman", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetSalesmanByProductStore service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
}
