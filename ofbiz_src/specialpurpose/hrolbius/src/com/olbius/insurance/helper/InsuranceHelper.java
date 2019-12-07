package com.olbius.insurance.helper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.olbius.util.DateUtil;
import com.olbius.util.PartyUtil;
import com.olbius.util.PersonHelper;

public class InsuranceHelper {
	public static Double getEmplRateContribution(Delegator delegator) throws GenericEntityException{
		List<GenericValue> allInsuranceType = delegator.findByAnd("InsuranceType", null, null, false);
		List<String> insuranceTypeList = EntityUtil.getFieldListFromEntityList(allInsuranceType, "insuranceTypeId", true);
		return getEmplRateContribution(delegator, insuranceTypeList);
	}
	
	public static Double getEmplRateContribution(Delegator delegator, List<String> insuranceTypeList) throws GenericEntityException{
		if(insuranceTypeList == null){
			return null;
		}
		Double retVal = 0d;
		for(String insuranceTypeId: insuranceTypeList){
			GenericValue insuranceType = delegator.findOne("InsuranceType", UtilMisc.toMap("insuranceTypeId", insuranceTypeId), false);
			Double employeeRate = insuranceType.getDouble("employeeRate");
			retVal += employeeRate;
		}
		return retVal;
	}

	public static String getInsuranceTypeParticipateInDecl(Delegator delegator, String insuranceDelarationId, String partyId) throws GenericEntityException {
		List<GenericValue> partyInsuranceDeclInsuranceType = delegator.findByAnd("PartyInsuranceDeclInsuranceType", 
				UtilMisc.toMap("insuranceDelarationId", insuranceDelarationId, "partyId", partyId), null, false);
		List<String> insuranceTypeIdList = EntityUtil.getFieldListFromEntityList(partyInsuranceDeclInsuranceType, "insuranceTypeId", true);
		if(insuranceTypeIdList != null){
			return StringUtils.join(insuranceTypeIdList, ", ");
		}
		return null;
	}

	public static String getPartyInsuranceStatus(Delegator delegator, String partyId, Timestamp timestamp) throws GenericEntityException{
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("statusDatetime", EntityJoinOperator.LESS_THAN_EQUAL_TO, timestamp));
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		List<GenericValue> partyInsuranceStt = delegator.findList("PartyInsuranceSocialStatus", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-statusDatetime"), null, false);
		if(UtilValidate.isNotEmpty(partyInsuranceStt)){
			return partyInsuranceStt.get(0).getString("statusId");
		}
		return null;
	}

	public static void createPartyParticipateInsurance(DispatchContext dctx, Map<String, Object> context, String insuranceTypeId, Timestamp fromDate) throws GenericEntityException, GenericServiceException {
		String partyId = (String)context.get("partyId");
		String insuranceParticipateTypeId = (String)context.get("insuranceParticipateTypeId");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		GenericValue insuranceParticipateType = delegator.findOne("InsuranceParticipateType", UtilMisc.toMap("insuranceParticipateTypeId", insuranceParticipateTypeId), false);
		if(insuranceParticipateType != null && "Y".equals(insuranceParticipateType.getString("isChangeStatusParticipate"))){
			String statusId = getStatusParticipateInsByInsuranceParticipateType(delegator, insuranceParticipateTypeId);
			if(statusId != null){
				Map<String, Object> ctxMap = FastMap.newInstance();
				ctxMap.put("statusId", statusId);
				ctxMap.put("partyId", partyId);
				ctxMap.put("fromDate", fromDate);
				ctxMap.put("insuranceTypeId", insuranceTypeId);
				ctxMap.put("userLogin", userLogin);
				dispatcher.runSync("createPartyParticipateInsurance", ctxMap);
			}
		}
	}
	
	public static Map<String, Object> checkStatusSocialInsSttIsValidToDecl(DispatchContext dctx, Map<String, Object> context, 
			Timestamp thruDate, Timestamp fromDate, String declarationTypeDesc) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		String insuranceParticipateTypeId = (String)context.get("insuranceParticipateTypeId");
		GenericValue insuranceParticipateType = delegator.findOne("InsuranceParticipateType", UtilMisc.toMap("insuranceParticipateTypeId", insuranceParticipateTypeId), false);
		String partyId = (String)context.get("partyId");
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale)context.get("locale");
		if(insuranceParticipateType != null && "Y".equals(insuranceParticipateType.getString("isChangeStatusParticipate"))){
			List<EntityCondition> conds = FastList.newInstance();
			//FIXME hard fix insuranceType is BHXH
			conds.add(EntityCondition.makeCondition("insuranceTypeId", "BHXH"));
			conds.add(EntityCondition.makeCondition("thruDate", null));
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			List<GenericValue> checkEtt = delegator.findList("PartyParticipateInsurance", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(checkEtt)){
				GenericValue partyParticipateCurr = checkEtt.get(0);
				Timestamp fromDateCurr = partyParticipateCurr.getTimestamp("fromDate");
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDateCurr);
				if(!fromDate.after(fromDateCurr)){
					retMap.put(ModelService.ERROR_MESSAGE, 
							UtilProperties.getMessage("InsuranceUiLabels", "CannotInsuranceDeclaredBeforDate", 
									UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(cal), "personName", PartyUtil.getPersonName(delegator, partyId)), locale));
					return retMap;
				}
				String currStatusId = partyParticipateCurr.getString("statusId");
				String newStatusId = getStatusParticipateInsByInsuranceParticipateType(delegator, insuranceParticipateTypeId);								
				if(currStatusId != null && newStatusId != null){				
					GenericValue currStatus = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", currStatusId), false);
					GenericValue newStatus = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", newStatusId), false);
					if(currStatus != null && newStatus != null && currStatus.getString("statusTypeId").equals(newStatus.getString("statusTypeId"))){
						retMap.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("InsuranceUiLabels", "CannotInsuranceDeclared_PrevDecl", 
								UtilMisc.toMap("declarationType", declarationTypeDesc, "personName", PartyUtil.getPersonName(delegator, partyId), "currStatus", currStatus.getString("description")),locale));
						return retMap;
					}
				}
			}
		}
		return null;
	}
	
	private static String getStatusParticipateInsByInsuranceParticipateType(Delegator delegator, String insuranceParticipateTypeId) throws GenericEntityException {
		String statusId = null;
		if("STOP_PARTICIPATE".equals(insuranceParticipateTypeId) || "SUSPEND_MOVE".equals(insuranceParticipateTypeId)){
			statusId = "STOP_PARTICIPATE";
		}else{
			String participateType = InsuranceHelper.getRootParticipateType(delegator, insuranceParticipateTypeId);
			if("PARTICIPATE".equals(participateType)){
				statusId = "PARTICIPATING";
			}else if("REDUCE_PARTICIPATE".equals(participateType)){
				statusId = "SUSPEND_PARTICIPATE";
			}
		}
		return statusId;
	}
	
	private static String getRootParticipateType(Delegator delegtor, String insuranceParticipateTypeId) throws GenericEntityException {
		GenericValue insuranceParticipateType = delegtor.findOne("InsuranceParticipateType", UtilMisc.toMap("insuranceParticipateTypeId", insuranceParticipateTypeId), false);
		String parentTypeId = insuranceParticipateType.getString("parentTypeId");
		if(parentTypeId == null){
			return insuranceParticipateTypeId;
		}else{
			return getRootParticipateType(delegtor, parentTypeId);
		}
	}

	public static void updatePartyParticipateInsuranceAfterCreate(DispatchContext dctx, Map<String, Object> context, GenericValue newEntity) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		String partyId = (String)context.get("partyId");
		EntityCondition commonConds = EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", partyId),
																	EntityJoinOperator.AND,
																	EntityCondition.makeCondition("insuranceTypeId", insuranceTypeId));
		EntityCondition expireEnttConds = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, fromDate),
																		EntityJoinOperator.AND,
																		EntityCondition.makeCondition("thruDate", null));
		//list contain records must update thruDate
		List<GenericValue> partyParticipateInsExpire = delegator.findList("PartyParticipateInsurance", 
				EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, expireEnttConds), null, UtilMisc.toList("fromDate"), null, false);
		Timestamp thruDate = UtilDateTime.getDayEnd(fromDate, -1L);
		for(GenericValue tempGv: partyParticipateInsExpire){
			tempGv.set("thruDate", thruDate);
			tempGv.store();
		}
		//
		List<GenericValue> ettFromDateGreatNewEtt = delegator.findList("PartyParticipateInsurance", 
				EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, 
						EntityCondition.makeCondition("fromDate", EntityJoinOperator.GREATER_THAN, fromDate)), null, UtilMisc.toList("fromDate"), null, false);
		if(UtilValidate.isNotEmpty(ettFromDateGreatNewEtt)){
			Timestamp tempThruDate = ettFromDateGreatNewEtt.get(0).getTimestamp("fromDate");
			tempThruDate = UtilDateTime.getDayEnd(tempThruDate, -1L);
			newEntity.set("thruDate", tempThruDate);
			newEntity.store();
		}
	}

	public static BigDecimal getPartyInsuranceSocialSalary(Delegator delegator, String partyId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException {
		List<GenericValue> emplPosList = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
		BigDecimal retVal = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(emplPosList)){
			GenericValue emplPos = emplPosList.get(0);
			String emplPositionTypeId = emplPos.getString("emplPositionTypeId");
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
			conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
			conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityJoinOperator.OR,
												EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));
			List<GenericValue> emplPosTypeInsuranceSalaryList = delegator.findList("EmplPosTypeInsuranceSalary", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(emplPosTypeInsuranceSalaryList)){
				GenericValue emplPosTypeInsuranceSalary = emplPosTypeInsuranceSalaryList.get(0);
				retVal = getTotalInsuranceSalary(emplPosTypeInsuranceSalary);
			}
		}
		return retVal;
	}
	
	public static BigDecimal getTotalInsuranceSalary(GenericValue emplPosTypeInsuranceSalary){
		BigDecimal retVal = BigDecimal.ZERO;
		BigDecimal insuranceSalary = emplPosTypeInsuranceSalary.getBigDecimal("insuranceSalary");
		BigDecimal allowanceOther = emplPosTypeInsuranceSalary.getBigDecimal("allowanceOther");
		BigDecimal allowancePosition = emplPosTypeInsuranceSalary.getBigDecimal("allowancePosition");
		Double allowanceSeniority = emplPosTypeInsuranceSalary.getDouble("allowanceSeniority");
		Double allowanceSeniorityExces = emplPosTypeInsuranceSalary.getDouble("allowanceSeniorityExces");
		retVal = retVal.add(insuranceSalary);
		if(allowanceOther != null){
			retVal = retVal.add(allowanceOther);
		}
		if(allowancePosition != null){
			retVal = retVal.add(allowancePosition);
		}
		if(allowanceSeniority != null){
			BigDecimal tmp = insuranceSalary.multiply(BigDecimal.valueOf(allowanceSeniority));
			retVal = retVal.add(tmp);
		}
		if(allowanceSeniorityExces != null){
			BigDecimal tmp = insuranceSalary.multiply(BigDecimal.valueOf(allowanceSeniorityExces));
			retVal = retVal.add(tmp);
		}
		return retVal;
	}

	public static String getInsuranceTemplatePath(Delegator delegator, String contentTypeId) throws GenericEntityException {
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityUtil.getFilterByDateExpr());
		conds.add(EntityCondition.makeCondition("insuranceContentTypeId", contentTypeId));
		List<GenericValue> insuranceContentList = delegator.findList("InsuranceContent", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
		if(UtilValidate.isNotEmpty(insuranceContentList)){
			String contentId = insuranceContentList.get(0).getString("contentId");
			GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
			if(content != null){
				String dataResourceId = content.getString("dataResourceId");
				GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
				return dataResource.getString("objectInfo");
			}
		}
		return null;
	}

	public static String getParentRelation(Delegator delegator, String partyId) throws GenericEntityException{
		String fatherPartyId = PersonHelper.getPersonFamilyRelationship(delegator, partyId, "FAMILY_FATHER");
		if(fatherPartyId != null){
			return fatherPartyId;
		}
		String motherPartyId = PersonHelper.getPersonFamilyRelationship(delegator, partyId, "FAMILY_MOTHER");
		if(motherPartyId != null){
			return motherPartyId;
		}
		String guardianPartyId = PersonHelper.getPersonFamilyRelationship(delegator, partyId, "FAMILY_GUARDIAN");
		if(guardianPartyId != null){
			return guardianPartyId;
		}
		return null;	
	}
}
