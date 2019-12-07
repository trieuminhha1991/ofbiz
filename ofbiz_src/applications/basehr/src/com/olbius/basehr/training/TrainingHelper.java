package com.olbius.basehr.training;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PropertiesUtil;

public class TrainingHelper {

	public static boolean checkPartyRegisterTraining(Delegator delegator,
			String trainingCourseId, String partyId) throws GenericEntityException {
		GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
		if("Y".equals(trainingCourse.getString("isPublic"))){
			return true;
		}
		List<GenericValue> listPartyAllowedAtt = delegator.findByAnd("TrainingCoursePartyAttendance", UtilMisc.toMap("trainingCourseId", trainingCourseId), null, false);
		List<String> listPartyId = EntityUtil.getFieldListFromEntityList(listPartyAllowedAtt, "partyId", true);
		if(listPartyId.contains(partyId)){
			return true;
		}
		return false;
	}
	
	public static boolean isEditableTraining(Delegator delegator, Security security, GenericValue userLogin, String trainingCourseId) throws GenericEntityException{
		GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
		if(trainingCourse == null){
			return false;
		}
		String statusId = trainingCourse.getString("statusId");
		if(security.hasEntityPermission("HR_TRAINING", "_UPDATE", userLogin) && ("TRAINING_PLANNED".equals(statusId) || "TRAINING_PLANNED_REJ".equals(statusId))){
			return true;
		}
		return false;
	}

	public static void sendNotifyApprTrainingRegister(LocalDispatcher dispatcher, Delegator delegator, 
			String partyId, String statusId, String trainingCourseId, String trainingCourseName,
			GenericValue userLogin, Locale locale, TimeZone timeZone) throws GenericServiceException, GenericEntityException {
		List<String> roles = FastList.newInstance();
		roles.add(PropertiesUtil.HRM_REC_SPEC);
		roles.add(PropertiesUtil.HRM_ROLE);
		String action = "ViewListEmplRegisterTraining";
		String targetLink = "trainingCourseId=" + trainingCourseId;
		GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
		String header = UtilProperties.getMessage("BaseHRTrainingUiLabels", "TrainingCourseRegisterNotify",
				UtilMisc.toMap("fullName", PartyUtil.getPersonName(delegator, partyId), "statusDesc", status.get("description"), "trainingCourseName", trainingCourseName), locale);
		CommonUtil.sendNotifyByRoles(dispatcher, locale, roles, userLogin, header, action, targetLink);
	}
}
