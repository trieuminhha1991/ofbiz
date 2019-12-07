package com.olbius.jobanalysis.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.jobanalysis.entity.JobRequisitionEntity;

public class JobAnalysisServices {

	public static final String module = JobAnalysisServices.class.getName();
	public static final String resource = "hrolbiusUiLabels";
	public static final String resourceNoti = "NotificationUiLabels";

	/**
	 * Add a job requisition
	 * 
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createRequistion(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		// Get parameters
		String jobLocation = (String) context.get("jobLocation");
		String age = (String) context.get("age");
		String jobRequestId = (String) context.get("jobRequestId");
		Long noOfResources = (Long) context.get("noOfResources");
		String gender = (String) context.get("gender");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String examTypeEnumId = (String) context.get("examTypeEnumId");
		String statusId = (String) context.get("statusId");
		Long experienceMonths = (Long) context.get("experienceMonths");
		Long experienceYears = (Long) context.get("experienceYears");
		String jobPostingTypeEnumId = (String) context
				.get("jobPostingTypeEnumId");

		List<String> qualifications = (List<String>) context
				.get("qualifications");
		List<String> skillTypeIds = (List<String>) context.get("skillTypeIds");

		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String jobRequisitionId = delegator
				.getNextSeqId("JobRequisition");

		// Insert JobRequisition
		GenericValue requisition = delegator.makeValue("JobRequisition");
		requisition.set("jobRequisitionId", jobRequisitionId);
		requisition.set("jobRequestId", jobRequestId);
		requisition.set("jobLocation", jobLocation);
		requisition.set("age", age);
		requisition.set("noOfResources", noOfResources);
		requisition.set("gender", gender);
		requisition.set("fromDate", fromDate);
		requisition.set("statusId", statusId);
		requisition.set("thruDate", thruDate);
		requisition.set("examTypeEnumId", examTypeEnumId);
		requisition.set("experienceMonths", experienceMonths);
		requisition.set("experienceYears", experienceYears);
		requisition.set("jobPostingTypeEnumId", jobPostingTypeEnumId);
		try {
			requisition.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "createError", new Object[] { e.getMessage() }, locale));
		}

		// Insert JobRequisitionSkill
		for (String skillTypeId : skillTypeIds) {
			GenericValue olbiusJobRequisitionSkill = delegator
					.makeValue("JobRequisitionSkill");
			olbiusJobRequisitionSkill.set("jobRequisitionId", jobRequisitionId);
			olbiusJobRequisitionSkill.set("skillTypeId", skillTypeId);
			try {
				olbiusJobRequisitionSkill.create();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				Debug.log(e.getMessage(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
	                    "createError", new Object[] { e.getMessage() }, locale));
			}
		}

		// Insert JobRequisitionQualification
		for (String partyQualTypeId : qualifications) {
			GenericValue olbiusJobRequisitionQualType = delegator
					.makeValue("JobRequisitionQualType");
			olbiusJobRequisitionQualType.set("jobRequisitionId", jobRequisitionId);
			olbiusJobRequisitionQualType.set("partyQualTypeId", partyQualTypeId);
			try {
				olbiusJobRequisitionQualType.create();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				Debug.log(e.getMessage(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
	                    "createError", new Object[] { e.getMessage() }, locale));
			}
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceNoti, "createSuccessfully", locale));
	}

	/**
	 * Find job requisition
	 * 
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> findRequistion(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> tempResult = FastMap.newInstance();
		List<JobRequisitionEntity> requisitions = new ArrayList<JobRequisitionEntity>();
		List<GenericValue> skillGenricList = new ArrayList<GenericValue>();
		List<String> skillStringList = new ArrayList<String>();

		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		if (((Map<String, Object>) context.get("inputFields"))
				.get("skillTypeId") != null) {
			if (((Map<String, Object>) context.get("inputFields"))
					.get("skillTypeId") instanceof String) {
				skillStringList.add((String) ((Map<String, Object>) context
						.get("inputFields")).get("skillTypeId"));
			} else {
				skillStringList = (List<String>) ((Map<String, Object>) context
						.get("inputFields")).get("skillTypeId");
			}
		}

		try {
			tempResult = dispatcher.runSync("performFind", context);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "findError", new Object[] { e.getMessage() }, locale));
		}

		EntityListIterator listIt = (EntityListIterator) tempResult
				.get("listIt");
		GenericValue item = null;
		while (listIt != null && (item = listIt.next()) != null) {
			JobRequisitionEntity requisition = new JobRequisitionEntity();
			String jobRequisitionId = (String) item.get("jobRequisitionId");
			try {
				skillGenricList = delegator.findList("JobRequisitionSkill",
						EntityCondition.makeCondition("jobRequisitionId",
								jobRequisitionId), null, null, null, false);
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				Debug.log(e1.getMessage(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
	                    "findError", new Object[] { e1.getMessage() }, locale));
			}
			List<String> skillConverseList = new ArrayList<String>();
			for (GenericValue skillTypeIdItem : skillGenricList) {
				skillConverseList.add(skillTypeIdItem.getString("skillTypeId"));
			}
			if (skillConverseList.containsAll(skillStringList)) {
				try {
					List<GenericValue> skillTypes = delegator.findList(
							"JobRequisitionSkillTypeView", EntityCondition
									.makeCondition("jobRequisitionId",
											jobRequisitionId), null, null,
							null, false);
					List<GenericValue> qualifications = delegator.findList(
							"JobRequisitionQualTypeView", EntityCondition
									.makeCondition("jobRequisitionId",
											jobRequisitionId), null, null,
							null, false);
					requisition.setExperienceMonths(item
							.getLong("experienceMonths"));
					requisition.setExperienceYears(item
							.getLong("experienceYears"));
					requisition.setJobPostingType(item
							.getString("jobPostingTypeEnumId"));
					requisition.setJobRequisitionId(item
							.getString("jobRequisitionId"));
					requisition.setJobRequestId(item
							.getString("jobRequestId"));
					requisition.setExamTypeEnumId(item
							.getString("examTypeEnumId"));
					requisition.setQualifications(qualifications);
					requisition.setSkillTypeIds(skillTypes);
					requisition.setFromDate(item.getTimestamp("fromDate"));
					requisition.setThruDate(item.getTimestamp("thruDate"));
					requisition.setStatusId(item.getString("statusId"));
					requisitions.add(requisition);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					Debug.log(e.getMessage(), module);
					return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
		                    "findError", new Object[] { e.getMessage() }, locale));
				}
			}
		}
		if (listIt != null) {
			try {
				listIt.close();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		result.put("listIt", requisitions);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
				resourceNoti, "findSuccessfully", locale));
		return result;
	}

	public static Map<String, Object> updateRequistion(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		// Get parameters
		Locale locale = (Locale) context.get("locale");
		String jobLocation = (String) context.get("jobLocation");
		String jobRequestId = (String) context.get("jobRequestId");
		String age = (String) context.get("age");
		Long noOfResources = (Long) context.get("noOfResources");
		String gender = (String) context.get("gender");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String examTypeEnumId = (String) context.get("examTypeEnumId");
		Long experienceMonths = (Long) context.get("experienceMonths");
		Long experienceYears = (Long) context.get("experienceYears");
		String jobPostingTypeEnumId = (String) context
				.get("jobPostingTypeEnumId");

		List<String> qualifications = (List<String>) context
				.get("qualifications");
		List<String> skillTypeIds = (List<String>) context.get("skillTypeIds");

		Delegator delegator = ctx.getDelegator();
		String jobRequisitionId = (String) context.get("jobRequisitionId");
		// Update requisition
		GenericValue requisitionOld = null;
		try {
			requisitionOld = delegator
					.findOne("JobRequisition", UtilMisc.toMap(
							"jobRequisitionId", jobRequisitionId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "updateError", new Object[] { e.getMessage() }, locale));
		}
		requisitionOld.put("jobLocation", jobLocation);
		requisitionOld.put("age", age);
		requisitionOld.put("jobRequestId", jobRequestId);
		requisitionOld.put("noOfResources", noOfResources);
		requisitionOld.put("gender", gender);
		requisitionOld.put("fromDate", fromDate);
		requisitionOld.put("thruDate", thruDate);
		requisitionOld.put("examTypeEnumId", examTypeEnumId);
		requisitionOld.put("experienceMonths", experienceMonths);
		requisitionOld.put("experienceYears", experienceYears);
		requisitionOld.put("jobPostingTypeEnumId", jobPostingTypeEnumId);
		try {
			requisitionOld.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "updateError", new Object[] { e.getMessage() }, locale));
		}
		// delete skill type
		try {
			delegator
					.removeByCondition("JobRequisitionSkill",
							EntityCondition.makeCondition("jobRequisitionId",
									jobRequisitionId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "removeError", new Object[] { e.getMessage() }, locale));
		}
		
		try {
			delegator
					.removeByCondition("JobRequisitionQualType",
							EntityCondition.makeCondition("jobRequisitionId",
									jobRequisitionId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "removeError", new Object[] { e.getMessage() }, locale));
		}
		
		// Insert RequisitionSkill
		for (String skillTypeId : skillTypeIds) {
			GenericValue olbiusJobRequisitionSkill = delegator
					.makeValue("JobRequisitionSkill");
			olbiusJobRequisitionSkill.set("jobRequisitionId", jobRequisitionId);
			olbiusJobRequisitionSkill.set("skillTypeId", skillTypeId);
			try {
				olbiusJobRequisitionSkill.create();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				Debug.log(e.getMessage(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
	                    "createError", new Object[] { e.getMessage() }, locale));
			}
		}

		// Insert ReqisitionQualification
		for (String partyQualTypeId : qualifications) {
			GenericValue olbiusJobRequisitionQualType = delegator
					.makeValue("JobRequisitionQualType");
			olbiusJobRequisitionQualType.set("jobRequisitionId", jobRequisitionId);
			olbiusJobRequisitionQualType.set("partyQualTypeId", partyQualTypeId);
			try {
				olbiusJobRequisitionQualType.create();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				Debug.log(e.getMessage(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
	                    "createError", new Object[] { e.getMessage() }, locale));
			}
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
				resourceNoti, "updateSuccessfully", locale));
		return result;
	}
	
}
