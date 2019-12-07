package com.olbius.employee.helper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class EmployeeHelper {
	 public static GenericValue getJobTransProposal(Delegator delegator, String jobTransferProposalId) throws GenericEntityException{
		 GenericValue jobTransProposal = delegator.findOne("JobTransferProposal", UtilMisc.toMap("jobTransferProposalId", jobTransferProposalId), false);
		 String type = jobTransProposal.getString("jobTransferProposalTypeId");
		 if("TRANSFER_DEPT".equalsIgnoreCase(type)){
			 return delegator.findOne("JobTransferProposalAndDeptTransfer", UtilMisc.toMap("jobTransferProposalId", jobTransferProposalId), false); 
		 }else if("TRANSFER_POSITION".equalsIgnoreCase(type)){
			 return delegator.findOne("JobTransferProposalAndPositionTransfer", UtilMisc.toMap("jobTransferProposalId", jobTransferProposalId), false); 
		 }
		 return delegator.findOne("JobTransferProposal", UtilMisc.toMap("jobTransferProposalId", jobTransferProposalId), false);		 
	 }
	 
	 public static void createEmplRelatedInfo(LocalDispatcher dispatcher, String partyId, Map<String, Object> parameterMap, 
			 	 GenericValue system, TimeZone timeZone, Locale locale) throws GenericServiceException{
		Map<String, Object> educationProcessMap = FastMap.newInstance();
		Map<String, Object> workingProcessMap = FastMap.newInstance();
		Map<String, Object> familyBackgroundMap = FastMap.newInstance();
		Map<String, Object> skillTypeMap = FastMap.newInstance();
		
		ModelService educationProcessService = dispatcher.getDispatchContext().getModelService("createPersonEducation");
		ModelService workingProcessService = dispatcher.getDispatchContext().getModelService("createPersonWorkingProcess");
		ModelService familyBackGroundService = dispatcher.getDispatchContext().getModelService("createPersonFamilyBackground");
		ModelService partySkillService = dispatcher.getDispatchContext().getModelService("HRCreatePartySkill");
		
		int educationCount = 0;
		int workingCount = 0;
		int familyBackgroundCount = 0;
		int skillTypeCount = 0;
		for(String parameterName: parameterMap.keySet()){
			int rowDelimiterIndex = (parameterName != null? parameterName.indexOf(UtilHttp.MULTI_ROW_DELIMITER): -1);
			if(rowDelimiterIndex > 0){
				String param = parameterName.substring(0, rowDelimiterIndex);
				if(partySkillService.getInParamNames().contains(param)){
					skillTypeMap.put(parameterName, parameterMap.get(parameterName));
				}else if(educationProcessService.getInParamNames().contains(param)){
					educationProcessMap.put(parameterName, parameterMap.get(parameterName));
				}else if(workingProcessService.getInParamNames().contains(param)){
					workingProcessMap.put(parameterName, parameterMap.get(parameterName));
				}else if (familyBackGroundService.getInParamNames().contains(param)) {
					familyBackgroundMap.put(parameterName, parameterMap.get(parameterName));
				}
			}
		}
		if(UtilValidate.isNotEmpty(educationProcessMap)){
			educationCount = UtilHttp.getMultiFormRowCount(educationProcessMap);
		}
		if(UtilValidate.isNotEmpty(workingProcessMap)){
			workingCount = UtilHttp.getMultiFormRowCount(workingProcessMap);
		}
		if(UtilValidate.isNotEmpty(familyBackgroundMap)){
			familyBackgroundCount = UtilHttp.getMultiFormRowCount(familyBackgroundMap);
		}
		if(UtilValidate.isNotEmpty(skillTypeMap)){
			skillTypeCount = UtilHttp.getMultiFormRowCount(skillTypeMap);
		}
		if(educationCount > 0){
			for(int i = 0; i < educationCount; i++){
				Map<String, Object> createEducationCtx = FastMap.newInstance();
				createEducationCtx.put("userLogin", system);
				createEducationCtx.put("partyId", partyId);
				String currSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				for(Entry<String, Object>entry: educationProcessMap.entrySet()){
					if(entry.getKey().endsWith(currSuffix)){
						createEducationCtx.put(entry.getKey().substring(0, entry.getKey().indexOf(currSuffix)), entry.getValue());
					}
				}
				dispatcher.runSync("createPersonEducation", educationProcessService.makeValid(createEducationCtx, ModelService.IN_PARAM, true, null, timeZone, locale));
			}
		}
		if(workingCount > 0){
			for(int i = 0; i < workingCount; i++){
				Map<String, Object> createWorkingCtx = FastMap.newInstance();
				createWorkingCtx.put("userLogin", system);
				createWorkingCtx.put("partyId", partyId);
				String currSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				for(Entry<String, Object>entry: workingProcessMap.entrySet()){
					if(entry.getKey().endsWith(currSuffix)){
						createWorkingCtx.put(entry.getKey().substring(0, entry.getKey().indexOf(currSuffix)), entry.getValue());
					}
				}
				dispatcher.runSync("createPersonWorkingProcess", workingProcessService.makeValid(createWorkingCtx, ModelService.IN_PARAM, true, null, timeZone, locale));
			}
		}
		if(familyBackgroundCount > 0){
			for(int i = 0; i < familyBackgroundCount; i++){
				Map<String, Object> familyBackgroundCtx = FastMap.newInstance();
				familyBackgroundCtx.put("partyId", partyId);
				familyBackgroundCtx.put("userLogin", system);
				String currSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				for(Entry<String, Object> entry: familyBackgroundMap.entrySet()){
					if(entry.getKey().endsWith(currSuffix)){
						familyBackgroundCtx.put(entry.getKey().substring(0, entry.getKey().indexOf(currSuffix)), entry.getValue());
					}
				}
				dispatcher.runSync("createPersonFamilyBackground", familyBackGroundService.makeValid(familyBackgroundCtx, ModelService.IN_PARAM, true, null, timeZone, locale));
			}
		}
		if(skillTypeCount > 0){
			for(int i = 0; i < skillTypeCount; i++){
				Map<String, Object> createSkillTypeCtx = FastMap.newInstance();
				createSkillTypeCtx.put("partyId", partyId);
				createSkillTypeCtx.put("userLogin", system);
				String currSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				for(Entry<String, Object> entry: skillTypeMap.entrySet()){
					if(entry.getKey().endsWith(currSuffix)){
						createSkillTypeCtx.put(entry.getKey().substring(0, entry.getKey().indexOf(currSuffix)), entry.getValue());
					}
				}
				dispatcher.runSync("HRCreatePartySkill", partySkillService.makeValid(createSkillTypeCtx, ModelService.IN_PARAM, true, null, timeZone, locale));
			}
		}
	 }

	public static void createEmployeeFamily(LocalDispatcher dispatcher,
			List<Map<String, Object>> personFamily, GenericValue userLogin, String partyId) throws GenericServiceException {
		// TODO Auto-generated method stub
		for(Map<String, Object> tempMap: personFamily){
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("partyId", partyId);
			ctxMap.put("firstNameFamily", tempMap.get("firstName"));
			ctxMap.put("middleNameFamily", tempMap.get("middleName"));
			ctxMap.put("lastNameFamily", tempMap.get("lastName"));
			ctxMap.put("partyRelationshipTypeId", tempMap.get("partyRelationshipTypeId"));
			ctxMap.put("birthDate", tempMap.get("birthDate"));
			ctxMap.put("occupation", tempMap.get("occupation"));
			ctxMap.put("placeWork", tempMap.get("placeWork"));
			ctxMap.put("phoneNumber", tempMap.get("phoneNumber"));
			ctxMap.put("emergencyContact", tempMap.get("emergencyContact"));
			ctxMap.put("userLogin", userLogin);
			dispatcher.runSync("createPersonFamilyBackground", ctxMap);
		}
	}

	public static void createEmployeeEducation(LocalDispatcher dispatcher,
			List<Map<String, Object>> personEducation, GenericValue userLogin, String partyId, TimeZone timeZone, Locale locale) throws GenericServiceException, GeneralServiceException {
		// TODO Auto-generated method stub
		for(Map<String, Object> tempMap: personEducation){
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.putAll(tempMap);
			ctxMap.put("partyId", partyId);
			dispatcher.runSync("createPersonEducation", ServiceUtil.setServiceFields(dispatcher, "createPersonEducation", 
					ctxMap, userLogin, timeZone, locale));
		}
	}

	public static void createEmployeeWorkingProcess(LocalDispatcher dispatcher,
			List<Map<String, Object>> personWorkingProcess, GenericValue userLogin, 
			String partyId, TimeZone timeZone, Locale locale) throws GenericServiceException, GeneralServiceException {
		// TODO Auto-generated method stub
		for(Map<String, Object> tempMap: personWorkingProcess){
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.putAll(tempMap);
			ctxMap.put("partyId", partyId);
			dispatcher.runSync("createPersonWorkingProcess", ServiceUtil.setServiceFields(dispatcher, "createPersonWorkingProcess", 
					ctxMap, userLogin, timeZone, locale));
		}
	}

	public static void createEmployeeContact(LocalDispatcher dispatcher,
			Map<String, Object> parameterMap, String partyId,
			GenericValue userLogin) throws GenericServiceException {
		// TODO Auto-generated method stub
		Map<String, Object> contactInfoMap = FastMap.newInstance();
		contactInfoMap.put("partyId", partyId);
		contactInfoMap.put("phone_mobile", parameterMap.get("mobile"));
		contactInfoMap.put("phone_home", parameterMap.get("homeTel"));
		contactInfoMap.put("primaryEmailAddress", parameterMap.get("email"));
		contactInfoMap.put("address1_PermanentResidence", parameterMap.get("prAddress"));
		contactInfoMap.put("permanentResidence_countryGeoId", parameterMap.get("prCountry"));
		contactInfoMap.put("permanentResidence_stateProvinceGeoId", parameterMap.get("prProvince"));
		contactInfoMap.put("permanentResidence_districtGeoId", parameterMap.get("prDistrict"));
		contactInfoMap.put("permanentResidence_wardGeoId", parameterMap.get("prWard"));
		contactInfoMap.put("address1_CurrResidence", parameterMap.get("crAddress"));
		contactInfoMap.put("currResidence_countryGeoId", parameterMap.get("crCountry"));
		contactInfoMap.put("currResidence_stateProvinceGeoId", parameterMap.get("crProvince"));
		contactInfoMap.put("currResidence_districtGeoId", parameterMap.get("crDistrict"));
		contactInfoMap.put("currResidence_wardGeoId", parameterMap.get("crWard"));
		contactInfoMap.put("userLogin", userLogin);
		dispatcher.runSync("createEmplContactInfo", contactInfoMap);
	}

	public static String updateEmplTerminationStatus(Delegator delegator,
			String emplTerminationProposalId, String requestId) throws GenericEntityException {
		GenericValue workFlowRequest = delegator.findOne("WorkFlowRequest", UtilMisc.toMap("requestId", requestId), false);
		String processStatusId = workFlowRequest.getString("processStatusId");
		GenericValue workFlowProcessStatus = delegator.findOne("WorkFlowProcessStatus", UtilMisc.toMap("processStatusId", processStatusId), false);
		String statusId = workFlowProcessStatus.getString("statusId");
		GenericValue emplTermiationProposal = delegator.findOne("EmplTerminationProposal", UtilMisc.toMap("emplTerminationProposalId", emplTerminationProposalId), false);
		String statusUpdateId = null;
		if("WORK_FLOW_COMPPLETE".equals(statusId)){
			emplTermiationProposal.set("statusId", "TER_PPSL_ACCEPTED");
			emplTermiationProposal.store();
			statusUpdateId = "TER_PPSL_ACCEPTED";
		}else if("WORK_FLOW_DENIED".equals(statusId)){
			emplTermiationProposal.set("statusId", "TER_PPSL_REJECTED");
			emplTermiationProposal.store();
			statusUpdateId = "TER_PPSL_REJECTED";
		}else{
			emplTermiationProposal.set("statusId", "TER_PPSL_IN_PROCESS");
			emplTermiationProposal.store();
			statusUpdateId = "TER_PPSL_IN_PROCESS";
		}
		return statusUpdateId;
	}

}
