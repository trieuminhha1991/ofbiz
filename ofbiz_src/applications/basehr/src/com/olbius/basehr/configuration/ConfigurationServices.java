package com.olbius.basehr.configuration;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

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

import com.olbius.basehr.util.EntityConditionUtils;

public class ConfigurationServices {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListCustomTimePeriodPayroll(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<String> periodTypes = UtilMisc.toList("YEARLY", "MONTHLY");
    	listAllConditions.add(EntityCondition.makeCondition("periodTypeId", EntityJoinOperator.IN, periodTypes));
    	try {
			listIterator = delegator.find("CustomTimePeriod", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTrainingPurposeTypeJQ(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("description");
			}
			listIterator = delegator.find("TrainingPurposeType", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, 
					null, listSortFields, opts);
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListSkillTypeJQ(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("description");
			}
			listAllConditions.add(EntityCondition.makeCondition("parentTypeId", EntityJoinOperator.NOT_EQUAL, null));
			listIterator = delegator.find("SkillTypeAndParent", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, 
					null, listSortFields, opts);
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListParentSkillType(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		try {
			listSortFields.add("description");
			listAllConditions.add(EntityCondition.makeCondition("parentTypeId", null));
			listIterator = delegator.find("SkillType", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, 
					UtilMisc.toSet("skillTypeId", "parentTypeId", "description"), listSortFields, opts);
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplLeaveReasonType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	List<Map<String, Object>> listReturn = FastList.newInstance();
		listSortFields.add("description");
		try {
			List<GenericValue> listEmpleaveReasonType = delegator.findList("EmplLeaveReasonTypeAndSign", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, null, false);
			for(GenericValue empLeaveReasonType : listEmpleaveReasonType){
				Map<String, Object> tempMap = FastMap.newInstance();
							String isBenefitSalStr = (String)empLeaveReasonType.get("isBenefitSal");
							String isBenefitSocialInsStr = (String)empLeaveReasonType.get("isBenefitSocialIns");
							boolean isBenefitSal = false, isBenefitSocialIns = false;
							if("Y".equals(isBenefitSalStr)){
								isBenefitSal = true;
							}
							if("Y".equals(isBenefitSocialInsStr)){
									isBenefitSocialIns = true;
							}
							tempMap.put("emplLeaveReasonTypeId", empLeaveReasonType.get("emplLeaveReasonTypeId"));
							tempMap.put("emplTimekeepingSignId", empLeaveReasonType.get("emplTimekeepingSignId"));
							tempMap.put("description", empLeaveReasonType.get("description"));
							tempMap.put("sign", empLeaveReasonType.get("sign"));
							tempMap.put("rateBenefit", empLeaveReasonType.get("rateBenefit"));
							tempMap.put("isBenefitSal", isBenefitSal);
							tempMap.put("isBenefitSocialIns", isBenefitSocialIns);
							listReturn.add(tempMap);
			}
			totalRows = listReturn.size();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	public static Map<String, Object> createEmplLeaveReasonType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue emplLeaveReasonType = delegator.makeValue("EmplLeaveReasonType");
		emplLeaveReasonType.setNonPKFields(context);
		String emplLeaveReasonTypeId = delegator.getNextSeqId("EmplLeaveReasonType");
		emplLeaveReasonType.put("emplLeaveReasonTypeId", emplLeaveReasonTypeId);
		try {
			delegator.create(emplLeaveReasonType);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateEmplLeaveReasonType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String emplLeaveReasonTypeId = (String)context.get("emplLeaveReasonTypeId");
		try {
			GenericValue emplLeaveReasonType = delegator.findOne("EmplLeaveReasonType", UtilMisc.toMap("emplLeaveReasonTypeId", emplLeaveReasonTypeId), false);
			if(emplLeaveReasonType == null){
				return ServiceUtil.returnError("cannot found emplLeaveReasonId for update");
			}
			emplLeaveReasonType.setNonPKFields(context);
			emplLeaveReasonType.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
}
