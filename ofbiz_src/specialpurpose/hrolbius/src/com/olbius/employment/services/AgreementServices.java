package com.olbius.employment.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.EntityUtil;

public class AgreementServices {
	
	public static String MODULE = AgreementServices.class.getName();

	public static Map<String, Object> updatePartyRelationshipThruDate(DispatchContext dctx, Map<String, Object> context){
		//Get delegator
		Delegator delegator = dctx.getDelegator();
		
		//Get parameters
		String agreementId = (String)context.get("agreementId");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		List<GenericValue> listPartyRela;
		try {
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			List<GenericValue> listWorkTerm = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("agreementId", agreement.getString("agreementId"), "termTypeId", "WORK_TERM"), null, false);
			if(!UtilValidate.isEmpty(listWorkTerm)) {
				String partyIdFrom = listWorkTerm.get(0).getString("textValue");
				String roleTypeIdFrom = "INTERNAL_ORGANIZATIO";
				String partyIdTo = agreement.getString("partyIdTo");
				String roleTypeIdTo = agreement.getString("roleTypeIdTo");
				listPartyRela = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo)), null, null, null, false);
				List<GenericValue> listActivePartyRela = EntityUtil.filterByDate(listPartyRela);
				if(!UtilValidate.isEmpty(listActivePartyRela)) {
					for(GenericValue item : listActivePartyRela) {
						item.set("thruDate", thruDate);
		        		item.store();
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), MODULE);
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateEmploymentThruDate(DispatchContext dctx, Map<String, Object> context){
		//Get delegator
		Delegator delegator = dctx.getDelegator();
		
		//Get parameters
		String agreementId = (String)context.get("agreementId");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		
		List<GenericValue> listPartyRela;
		try {
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			GenericValue agreementAttr = delegator.findOne("AgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName", "representFor"), false);
			if(agreementAttr != null) {
				String partyIdFrom = agreementAttr.getString("attrValue");
				String roleTypeIdFrom = "INTERNAL_ORGANIZATIO";
				String partyIdTo = agreement.getString("partyIdTo");
				String roleTypeIdTo = agreement.getString("roleTypeIdTo");
				listPartyRela = delegator.findList("Employment", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo)), null, null, null, false);
				List<GenericValue> listActivePartyRela = EntityUtil.filterByDate(listPartyRela);
				if(!UtilValidate.isEmpty(listActivePartyRela)) {
					for(GenericValue item : listActivePartyRela) {
						item.set("thruDate", thruDate);
		        		item.store();
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), MODULE);
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateEmplPositionFulfillmentThruDate(DispatchContext dctx, Map<String, Object> context){
		//Get delegator
		Delegator delegator = dctx.getDelegator();
		
		//Get parameters
		String agreementId = (String) context.get("agreementId");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		List<GenericValue> listEmplPosition;
		try {
			List<GenericValue> listWorkTerm = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("agreementId", agreementId, "termTypeId", "WORK_TERM"), null, false);
			List<GenericValue> listPosTerm = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("agreementId", agreementId, "termTypeId", "JOB_POSITION_TERM"), null, false);
			if(!UtilValidate.isEmpty(listPosTerm) && !UtilValidate.isEmpty(listWorkTerm)) {
				String partyIdFrom = listWorkTerm.get(0).getString("textValue");
				String emplPositionTypeId = listPosTerm.get(0).getString("textValue");
				Set<String> setEmplPosition = FastSet.newInstance();
				
				listEmplPosition = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "partyId", partyIdFrom), null, false);
				for(GenericValue item : listEmplPosition) {
					setEmplPosition.add(item.getString("emplPositionId"));
				}
		        List<GenericValue> listEmplPositionFul = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition("emplPositionId", EntityJoinOperator.IN, setEmplPosition), null, null, null, false);
		        List<GenericValue> listActiveEmplPositionFul = EntityUtil.filterByDate(listEmplPositionFul);
		        if(!UtilValidate.isEmpty(listActiveEmplPositionFul)) {
		        	for(GenericValue item: listActiveEmplPositionFul) {
		        		item.set("thruDate", thruDate);
		        		item.store();
		        	}
		        }
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), MODULE);
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateAgreementThruDate(DispatchContext dctx, Map<String, Object> context){
		//Get delegator
		Delegator delegator = dctx.getDelegator();
		
		//Get parameters
		String agreementId = (String) context.get("agreementId");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		try {
		 	GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
	        if(!UtilValidate.isEmpty(agreement)) {
	        	agreement.set("thruDate", thruDate);
	        	agreement.store();
        		List<GenericValue> listAgreementTerm = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("agreementId", agreementId), null, false);
        		if(!UtilValidate.isEmpty(listAgreementTerm)) {
        			for(GenericValue term: listAgreementTerm) {
        				if(term.getTimestamp("thruDate").after(thruDate)) {
        					term.put("thruDate", thruDate);
        					term.store();
        				}
        			}
	        	}
	        }
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), MODULE);
		}
		return ServiceUtil.returnSuccess();
	}
}
