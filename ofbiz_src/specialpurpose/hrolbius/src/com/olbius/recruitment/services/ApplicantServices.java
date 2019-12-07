package com.olbius.recruitment.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;


public class ApplicantServices {
	
	public static final String module = ApplicantServices.class.getName();
	public static final String resourceNoti = "NotificationUiLabels";
	
	public static Map<String, Object> createOfferProbation(DispatchContext dpctx, Map<String, Object> context) {
		Delegator delegator = dpctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		//Get parameters
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String partyIdWork = (String)context.get("partyIdWork");
		String workEffortId = (String)context.get("workEffortId");
		String partyId = (String)context.get("partyId");
		Long basicSalary = (Long)context.get("basicSalary");
		Timestamp inductedStartDate = (Timestamp)context.get("inductedStartDate");
		Timestamp inductedCompletionDate = (Timestamp)context.get("inductedCompletionDate");
		Long percentBasicSalary = (Long)context.get("percentBasicSalary");
		Long otherAllowance = (Long)context.get("otherAllowance");
		Long phoneAllowance = (Long)context.get("phoneAllowance");
		Long trafficAllowance = (Long)context.get("trafficAllowance");
		String comment = (String)context.get("comment");
		String statusId = (String)context.get("statusId");
		//Create OfferProbation
		GenericValue offerProbation = delegator.makeValue("OfferProbation");
		String offerProbationId = delegator.getNextSeqId("OfferProbation");
		offerProbation.put("offerProbationId", offerProbationId);
		offerProbation.put("emplPositionTypeId", emplPositionTypeId);
		offerProbation.put("partyIdWork", partyIdWork);
		offerProbation.put("partyId", partyId);
		offerProbation.put("basicSalary", basicSalary);
		offerProbation.put("inductedStartDate", inductedStartDate);
		offerProbation.put("inductedCompletionDate", inductedCompletionDate);
		offerProbation.put("percentBasicSalary", percentBasicSalary);
		offerProbation.put("comment", comment);
		offerProbation.put("statusId", statusId);
		offerProbation.put("trafficAllowance", trafficAllowance);
		offerProbation.put("phoneAllowance", phoneAllowance);
		offerProbation.put("otherAllowance", otherAllowance);
		try {
			if(!workEffortId.equals("_NA_")) {
				List<GenericValue> workEffortToList = delegator.findByAnd("WorkEffortAssocToView", UtilMisc.toMap("workEffortIdTo", workEffortId), null, false);
				offerProbation.put("workEffortId", EntityUtil.getFirst(workEffortToList).getString("workEffortIdFrom"));
			}else {
				offerProbation.put("workEffortId", workEffortId);
			}
			offerProbation.create();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[]{e.getMessage()}, locale));
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("offerProbationId", offerProbationId);
		return result;
	}
}
