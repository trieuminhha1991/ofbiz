package com.olbius.recruitment.helper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;
import com.olbius.util.RoleHelper;
import com.olbius.util.SecurityUtil;

public class RecruitmentPlanServiceHelper implements RoleTyle {
	
	public static final String module = RecruitmentPlanServiceHelper.class.getName();
	public static final String resource = "hrolbiusUiLabels";
	public static final String resourceNoti = "NotificationUiLabels";
	
	public static Map<String, Object> createRecruitmentPlan(DispatchContext dpctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = dpctx.getDelegator();

		//Get parameters
		String partyId = (String)context.get("partyId");
		String year = (String)context.get("year");
		String reason = (String)context.get("reason");
		Timestamp scheduleDate = (Timestamp)context.get("scheduleDate");
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String statusId = "RPH_INIT";
		GenericValue recruitmentPlanHeader = delegator.makeValue("RecruitmentPlanHeader");
		
		//Create recruitmentPlanHeader
		recruitmentPlanHeader.put("partyId", partyId);
		recruitmentPlanHeader.put("year", year);
		recruitmentPlanHeader.put("scheduleDate", scheduleDate);
		recruitmentPlanHeader.put("reason", reason);
		recruitmentPlanHeader.put("statusId", statusId);
		recruitmentPlanHeader.put("actorPartyId", userLogin.get("partyId"));
		recruitmentPlanHeader.put("actorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
		recruitmentPlanHeader.put("creatorPartyId", userLogin.getString("partyId"));
		recruitmentPlanHeader.put("creatorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
		try {
			recruitmentPlanHeader.create();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[] { e.getMessage() }, locale));
		}
		
		//Return Result
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("partyId", partyId);
		result.put("year", year);
		return result;
	}
	
	public static Map<String, Object> createRecruitmentPlanDT(DispatchContext dpctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = dpctx.getDelegator();

		//Get parameters
		String partyId = (String)context.get("partyId");
		String year = (String)context.get("year");
		String reason = (String)context.get("reason");
		Timestamp scheduleDate = (Timestamp)context.get("scheduleDate");
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String statusId = "RPH_INIT";
		GenericValue recruitmentPlanHeader = delegator.makeValue("RecruitmentPlanDTHeader");
		
		//Create recruitmentPlanHeader
		recruitmentPlanHeader.put("partyId", partyId);
		recruitmentPlanHeader.put("year", year);
		recruitmentPlanHeader.put("scheduleDate", scheduleDate);
		recruitmentPlanHeader.put("reason", reason);
		recruitmentPlanHeader.put("statusId", statusId);
		recruitmentPlanHeader.put("actorPartyId", userLogin.get("partyId"));
		recruitmentPlanHeader.put("actorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
		recruitmentPlanHeader.put("creatorPartyId", userLogin.getString("partyId"));
		recruitmentPlanHeader.put("creatorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
		try {
			recruitmentPlanHeader.create();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "createError", new Object[] { e.getMessage() }, locale));
		}
		
		//Return Result
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("partyId", partyId);
		result.put("year", year);
		return result;
	}
	
	public static Map<String, Object> createOrUpdateRecruitmentPlan(DispatchContext dpctx, Map<String, Object> context) throws Exception {

		Delegator delegator = dpctx.getDelegator();

		// Get parameters
		String emplPositionTypeId = (String) context.get("emplPositionTypeId");
		String partyId = (String)context.get("partyId");
		String year = (String)context.get("year");
		Long firstMonth = (Long) context.get("firstMonth");
		Long secondMonth = (Long) context.get("secondMonth");
		Long thirdMonth = (Long) context.get("thirdMonth");
		Long fourthMonth = (Long) context.get("fourthMonth");
		Long fifthMonth = (Long) context.get("fifthMonth");
		Long sixthMonth = (Long) context.get("sixthMonth");
		Long seventhMonth = (Long) context.get("seventhMonth");
		Long eighthMonth = (Long) context.get("eighthMonth");
		Long ninthMonth = (Long) context.get("ninthMonth");
		Long tenthMonth = (Long) context.get("tenthMonth");
		Long eleventhMonth = (Long) context.get("eleventhMonth");
		Long twelfthMonth = (Long) context.get("twelfthMonth");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		//If recruitment plan header is not exists
		GenericValue recruitmentPlanHeader = delegator.findOne("RecruitmentPlanHeader", UtilMisc.toMap("partyId", partyId, "year", year), false);
		if(SecurityUtil.hasRole(SUP_GT_ROLE, partyId, delegator) || SecurityUtil.hasRole(SUP_MT_ROLE, partyId, delegator)) {
			if(UtilValidate.isEmpty(recruitmentPlanHeader)){
				GenericValue newEntity = delegator.makeValue("RecruitmentPlanHeader");
				newEntity.set("partyId", partyId);
				newEntity.set("year", year);
				newEntity.put("scheduleDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				newEntity.put("statusId", PlanStatus.RPH_ACCEPTED.toString());
				newEntity.put("actorPartyId", userLogin.get("partyId"));
				newEntity.put("actorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
				newEntity.put("creatorPartyId", userLogin.getString("partyId"));
				newEntity.put("creatorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
				newEntity.create();
			}else {
				recruitmentPlanHeader.put("statusId", PlanStatus.RPH_ACCEPTED.toString());
				recruitmentPlanHeader.store();
			}
		}
		
		GenericValue recruitmentPlan = delegator.findOne("RecruitmentPlan", UtilMisc.toMap("partyId", partyId, "year", year, "emplPositionTypeId", emplPositionTypeId), false);
		if(UtilValidate.isEmpty(recruitmentPlan)){
			//If recruitment plan is not exists, CREATE
			GenericValue newEntity = delegator.makeValue("RecruitmentPlan");
			newEntity.set("emplPositionTypeId", emplPositionTypeId);
			newEntity.set("partyId", partyId);
			newEntity.set("year", year);
			newEntity.set("firstMonth", firstMonth);
			newEntity.set("secondMonth", secondMonth);
			newEntity.set("thirdMonth", thirdMonth);
			newEntity.set("fourthMonth", fourthMonth);
			newEntity.set("fifthMonth", fifthMonth);
			newEntity.set("sixthMonth", sixthMonth);
			newEntity.set("seventhMonth", seventhMonth);
			newEntity.set("eighthMonth", eighthMonth);
			newEntity.set("ninthMonth", ninthMonth);
			newEntity.set("tenthMonth", tenthMonth);
			newEntity.set("eleventhMonth", eleventhMonth);
			newEntity.set("twelfthMonth", twelfthMonth);
			newEntity.create();
		}else{
			//If recruitment plan is exists, UPDATE
			recruitmentPlan.put("firstMonth", firstMonth);
			recruitmentPlan.put("secondMonth", secondMonth);
			recruitmentPlan.put("thirdMonth", thirdMonth);
			recruitmentPlan.put("fourthMonth", fourthMonth);
			recruitmentPlan.put("fifthMonth", fifthMonth);
			recruitmentPlan.put("sixthMonth", sixthMonth);
			recruitmentPlan.put("seventhMonth", seventhMonth);
			recruitmentPlan.put("eighthMonth", eighthMonth);
			recruitmentPlan.put("ninthMonth", ninthMonth);
			recruitmentPlan.put("tenthMonth", tenthMonth);
			recruitmentPlan.put("eleventhMonth", eleventhMonth);
			recruitmentPlan.put("twelfthMonth", twelfthMonth);
			recruitmentPlan.store();
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("emplPositionTypeId", emplPositionTypeId);
		result.put("partyId", partyId);
		result.put("year", year);
		return result;
	}
	
	public static Map<String, Object> createOrUpdateRecruitmentPlanDT(DispatchContext dpctx, Map<String, Object> context) throws Exception {

		Delegator delegator = dpctx.getDelegator();

		// Get parameters
		String emplPositionTypeId = (String) context.get("emplPositionTypeId");
		String partyId = (String)context.get("partyId");
		String year = (String)context.get("year");
		Long firstMonth = (Long) context.get("firstMonth");
		Long secondMonth = (Long) context.get("secondMonth");
		Long thirdMonth = (Long) context.get("thirdMonth");
		Long fourthMonth = (Long) context.get("fourthMonth");
		Long fifthMonth = (Long) context.get("fifthMonth");
		Long sixthMonth = (Long) context.get("sixthMonth");
		Long seventhMonth = (Long) context.get("seventhMonth");
		Long eighthMonth = (Long) context.get("eighthMonth");
		Long ninthMonth = (Long) context.get("ninthMonth");
		Long tenthMonth = (Long) context.get("tenthMonth");
		Long eleventhMonth = (Long) context.get("eleventhMonth");
		Long twelfthMonth = (Long) context.get("twelfthMonth");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Long firstMonthReq = 0l;
		Long secondMonthReq = 0l;
		Long thirdMonthReq = 0l;
		Long fourthMonthReq = 0l;
		Long fifthMonthReq = 0l;
		Long sixthMonthReq = 0l;
		Long seventhMonthReq = 0l;
		Long eighthMonthReq = 0l;
		Long ninthMonthReq = 0l;
		Long tenthMonthReq = 0l;
		Long eleventhMonthReq = 0l;
		Long twelfthMonthReq = 0l;
		
		//If Recruitment Plan Header is not exists
		GenericValue recruitmentPlanHeader = delegator.findOne("RecruitmentPlanDTHeader", UtilMisc.toMap("partyId", partyId, "year", year), false);
		if(UtilValidate.isEmpty(recruitmentPlanHeader)){
			GenericValue newEntity = delegator.makeValue("RecruitmentPlanDTHeader");
			newEntity.set("partyId", partyId);
			newEntity.set("year", year);
			newEntity.put("scheduleDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
			newEntity.put("statusId", PlanStatus.RPH_INIT.toString());
			newEntity.put("actorPartyId", userLogin.get("partyId"));
			newEntity.put("actorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
			newEntity.put("creatorPartyId", userLogin.getString("partyId"));
			newEntity.put("creatorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
			newEntity.create();
		}
		
		GenericValue recruitmentPlan = delegator.findOne("RecruitmentPlanDT", UtilMisc.toMap("partyId", partyId, "year", year, "emplPositionTypeId", emplPositionTypeId), false);
		if(UtilValidate.isEmpty(recruitmentPlan)){
			//If Recruitment Plan is not exists, CREATE
			GenericValue recruitmentPlanView = delegator.findOne("RecruitmentPlanView", UtilMisc.toMap(UtilMisc.toMap("partyId", partyId, "year", Long.toString(Long.parseLong(year)-1), "emplPositionTypeId", emplPositionTypeId)), false);
			if(!UtilValidate.isEmpty(recruitmentPlanView)) {
				if(firstMonth - recruitmentPlanView.getLong("twelfthMonth") > 0) {
					firstMonthReq = firstMonth - recruitmentPlanView.getLong("twelfthMonth");
				}
			}
			if(secondMonth - firstMonth > 0) {
				secondMonthReq = secondMonth - firstMonth;
			}
			if(thirdMonth - secondMonth > 0) {
				thirdMonthReq = thirdMonth - secondMonth;
			}
			if(fourthMonth - thirdMonth > 0) {
				fourthMonthReq = fourthMonth - thirdMonth;
			}
			if(fifthMonth - fourthMonth > 0) {
				fifthMonthReq = fifthMonth - fourthMonth;
			}
			if(sixthMonth - fifthMonth > 0) {
				sixthMonthReq = sixthMonth - fifthMonth;
			}
			if(seventhMonth - sixthMonth > 0) {
				seventhMonthReq = seventhMonth - sixthMonth;
			}
			if(eighthMonth - seventhMonth > 0) {
				eighthMonthReq = eighthMonth - seventhMonth;
			}
			if(ninthMonth - eighthMonth > 0) {
				ninthMonthReq = ninthMonth - eighthMonth;
			}
			if(tenthMonth - ninthMonth > 0) {
				tenthMonthReq = tenthMonth - ninthMonth;
			}
			if(eleventhMonth - tenthMonth > 0) {
				eleventhMonthReq = eleventhMonth - tenthMonth;
			}
			if(twelfthMonth - eleventhMonth > 0) {
				twelfthMonthReq = twelfthMonth - eleventhMonth;
			}
			GenericValue newEntity = delegator.makeValue("RecruitmentPlanDT");
			newEntity.set("emplPositionTypeId", emplPositionTypeId);
			newEntity.set("partyId", partyId);
			newEntity.set("year", year);
			newEntity.set("firstMonth", firstMonthReq);
			newEntity.set("secondMonth", secondMonthReq);
			newEntity.set("thirdMonth", thirdMonthReq);
			newEntity.set("fourthMonth", fourthMonthReq);
			newEntity.set("fifthMonth", fifthMonthReq);
			newEntity.set("sixthMonth", sixthMonthReq);
			newEntity.set("seventhMonth", seventhMonthReq);
			newEntity.set("eighthMonth", eighthMonthReq);
			newEntity.set("ninthMonth", ninthMonthReq);
			newEntity.set("tenthMonth", tenthMonthReq);
			newEntity.set("eleventhMonth", eleventhMonthReq);
			newEntity.set("twelfthMonth", twelfthMonthReq);
			newEntity.create();
		}else{
			//If Recruitment Plan is exists, UPDATE
			//If Recruitment Plan is not exists, CREATE
			GenericValue recruitmentPlanView = delegator.findOne("RecruitmentPlanView", UtilMisc.toMap(UtilMisc.toMap("partyId", partyId, "year", Long.toString(Long.parseLong(year)-1), "emplPositionTypeId", emplPositionTypeId)), false);
			if(!UtilValidate.isEmpty(recruitmentPlanView)) {
				if(firstMonth - recruitmentPlanView.getLong("twelfthMonth") > 0) {
					firstMonthReq = firstMonth - recruitmentPlanView.getLong("twelfthMonth");
				}
			}
			if(secondMonth - firstMonth > 0) {
				secondMonthReq = secondMonth - firstMonth;
			}
			if(thirdMonth - secondMonth > 0) {
				thirdMonthReq = thirdMonth - secondMonth;
			}
			if(fourthMonth - thirdMonth > 0) {
				fourthMonthReq = fourthMonth - thirdMonth;
			}
			if(fifthMonth - fourthMonth > 0) {
				fifthMonthReq = fifthMonth - fourthMonth;
			}
			if(sixthMonth - fifthMonth > 0) {
				sixthMonthReq = sixthMonth - fifthMonth;
			}
			if(seventhMonth - sixthMonth > 0) {
				seventhMonthReq = seventhMonth - sixthMonth;
			}
			if(eighthMonth - seventhMonth > 0) {
				eighthMonthReq = eighthMonth - seventhMonth;
			}
			if(ninthMonth - eighthMonth > 0) {
				ninthMonthReq = ninthMonth - eighthMonth;
			}
			if(tenthMonth - ninthMonth > 0) {
				tenthMonthReq = tenthMonth - ninthMonth;
			}
			if(tenthMonth - ninthMonth > 0) {
				eleventhMonthReq = tenthMonth - ninthMonth;
			}
			if(twelfthMonth - eleventhMonth > 0) {
				twelfthMonthReq = twelfthMonth - eleventhMonth;
			}
			recruitmentPlan.put("firstMonth", firstMonthReq);
			recruitmentPlan.put("secondMonth", secondMonthReq);
			recruitmentPlan.put("thirdMonth", thirdMonthReq);
			recruitmentPlan.put("fourthMonth", fourthMonthReq);
			recruitmentPlan.put("fifthMonth", fifthMonthReq);
			recruitmentPlan.put("sixthMonth", sixthMonthReq);
			recruitmentPlan.put("seventhMonth", seventhMonthReq);
			recruitmentPlan.put("eighthMonth", eighthMonthReq);
			recruitmentPlan.put("ninthMonth", ninthMonthReq);
			recruitmentPlan.put("tenthMonth", tenthMonthReq);
			recruitmentPlan.put("eleventhMonth", eleventhMonthReq);
			recruitmentPlan.put("twelfthMonth", twelfthMonthReq);
			recruitmentPlan.store();
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("emplPositionTypeId", emplPositionTypeId);
		result.put("partyId", partyId);
		result.put("year", year);
		return result;
	}
	
	public static Map<String, Object> updateRecruitmentPlanHeader(DispatchContext dpctx, Map<String, Object> context) throws Exception{
		//Get parameters
		String partyId = (String)context.get("partyId");
		String year = (String)context.get("year");
		String statusId = (String)context.get("statusId");
		String reason = (String)context.get("reason");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dpctx.getDelegator();
		LocalDispatcher dispatcher = dpctx.getDispatcher();

		//Update RecruitmentPlanHeader
		GenericValue oldValue = delegator.findOne("RecruitmentPlanHeader", UtilMisc.toMap("partyId", partyId, "year", year), false);
		oldValue.put("actorPartyId", userLogin.getString("partyId"));
		oldValue.put("actorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
		oldValue.put("statusId", statusId);
		oldValue.put("reason", reason);
		oldValue.store();
		
		switch (PlanStatus.valueOf(statusId)) {
		case RPH_PROPOSED:
			if(MANAGER_ROLE.equals(RoleHelper.getCurrentRole(userLogin, delegator))){
				Map<String, Object> createNotiCtx = FastMap.newInstance();
				createNotiCtx.put("partyId", PartyUtil.getHrmAdmin(delegator));
				createNotiCtx.put("header", "Duyệt kế hoạch tuyển dụng phòng " + PartyHelper.getPartyName(delegator, partyId, false) + " năm " + year);
				createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				createNotiCtx.put("userLogin", userLogin);
				createNotiCtx.put("action", "CheckRecruitmentPlan");
				createNotiCtx.put("targetLink", "partyId=" + partyId + ";year=" + year);
				createNotiCtx.put("state", "open");
				createNotiCtx.put("ntfType", "ONE");
				dispatcher.runSync("createNotification", createNotiCtx);
			}
			break;
		case RPH_ACCEPTED:
			if(CEO_ROLE.equals(RoleHelper.getCurrentRole(userLogin, delegator))){
				//If CEO Accept
				Map<String, Object> createNotiCtx = FastMap.newInstance();
				List<String> partiesList = new ArrayList<String>();
				partiesList.add(PartyUtil.getHrmAdmin(delegator));
				partiesList.add(PartyUtil.getManagerbyOrg(partyId, delegator));
				createNotiCtx.put("partiesList", partiesList);
				createNotiCtx.put("header", "Kết quả xét duyệt kế hoạch tuyển dụng phòng " + PartyHelper.getPartyName(delegator, partyId, false) + " năm " + year);
				createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				createNotiCtx.put("userLogin", userLogin);
				createNotiCtx.put("action", "FindRecruitmentPlan");
				createNotiCtx.put("state", "open");
				createNotiCtx.put("ntfType", "ONE");
				createNotiCtx.put("targetLink", "partyId=" + partyId + ";year=" + year);
				dispatcher.runSync("createNotification", createNotiCtx);
			}
			break;
		case RPH_REJECTED:
			if(HRM_ROLE.equals(RoleHelper.getCurrentRole(userLogin, delegator))){
				Map<String, Object> createNotiCtx = FastMap.newInstance();
				List<String> partiesList = new ArrayList<String>();
				partiesList.add(PartyUtil.getManagerbyOrg(partyId, delegator));
				createNotiCtx.put("partiesList", partiesList);
				createNotiCtx.put("header", "Kết quả xét duyệt kế hoạch tuyển dụng phòng " + PartyHelper.getPartyName(delegator, partyId, false) + " năm " + year);
				createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				createNotiCtx.put("userLogin", userLogin);
				createNotiCtx.put("action", "FindRecruitmentPlan");
				createNotiCtx.put("state", "open");
				createNotiCtx.put("ntfType", "ONE");
				createNotiCtx.put("targetLink", "partyId=" + partyId + ";year=" + year);
				dispatcher.runSync("createNotification", createNotiCtx);
			}
			break;
		default:
			break;
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateSaleRecruitmentPlanHeader(DispatchContext dpctx, Map<String, Object> context) throws Exception{
		//Get parameters
		String partyId = (String)context.get("partyId");
		String year = (String)context.get("year");
		String statusId = (String)context.get("statusId");
		String reason = (String)context.get("reason");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dpctx.getDelegator();
		LocalDispatcher dispatcher = dpctx.getDispatcher();

		//Update RecruitmentPlanHeader
		GenericValue oldValue = delegator.findOne("RecruitmentPlanHeader", UtilMisc.toMap("partyId", partyId, "year", year), false);
		oldValue.put("actorPartyId", userLogin.getString("partyId"));
		oldValue.put("actorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
		oldValue.put("statusId", statusId);
		oldValue.put("reason", reason);
		oldValue.store();
		
		Organization tree = PartyUtil.buildOrg(delegator, partyId, true, false);
		List<GenericValue> listChild = tree.getDirectChildList(delegator);
		
		String managerId = PartyUtil.getManagerbyOrg(partyId, delegator);
		Map<String, Object> createNotiCtx = FastMap.newInstance();
		
		switch (PlanStatus.valueOf(statusId)) {
		case RPH_PROPOSED:
			if(SecurityUtil.hasRole(NBD_ROLE, userLogin.getString("partyId"), delegator)) {
				createNotiCtx.put("partyId", PartyUtil.getCEO(delegator));
				createNotiCtx.put("header", "Duyệt kế hoạch tuyển dụng phòng " + PartyHelper.getPartyName(delegator, partyId, false) + " năm " + year);
				createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				createNotiCtx.put("userLogin", userLogin);
				createNotiCtx.put("action", "FindSaleRecruitmentPlan");
				createNotiCtx.put("targetLink", "partyId=" + partyId + ";year=" + year);
				createNotiCtx.put("state", "open");
				createNotiCtx.put("ntfType", "ONE");
				dispatcher.runSync("createNotification", createNotiCtx);
			}else {
				String childDeptId = PartyUtil.getOrgByManager(userLogin, delegator);
				GenericValue parentDept = PartyUtil.getParentOrgOfDepartmentCurr(delegator, childDeptId);
				String parentManagerId = PartyUtil.getManagerbyOrg(parentDept.getString("partyIdFrom"), delegator);
				createNotiCtx.put("partyId", parentManagerId);
				createNotiCtx.put("header", "Duyệt kế hoạch tuyển dụng phòng " + PartyHelper.getPartyName(delegator, partyId, false) + " năm " + year);
				createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				createNotiCtx.put("userLogin", userLogin);
				createNotiCtx.put("action", "FindSaleRecruitmentPlan");
				createNotiCtx.put("targetLink", "partyId=" + partyId + ";year=" + year);
				createNotiCtx.put("state", "open");
				createNotiCtx.put("ntfType", "ONE");
				dispatcher.runSync("createNotification", createNotiCtx);
			}
			break;
		case RPH_ACCEPTED:
			createNotiCtx.put("partyId", managerId);
			createNotiCtx.put("header", "Duyệt kế hoạch tuyển dụng phòng " + PartyHelper.getPartyName(delegator, partyId, false) + " năm " + year);
			createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
			createNotiCtx.put("userLogin", userLogin);
			createNotiCtx.put("action", "FindSaleRecruitmentPlan");
			createNotiCtx.put("targetLink", "partyId=" + partyId + ";year=" + year);
			createNotiCtx.put("state", "open");
			createNotiCtx.put("ntfType", "ONE");
			dispatcher.runSync("createNotification", createNotiCtx);
			if(!UtilValidate.isEmpty(listChild)) {
				for(GenericValue item: listChild) {
					GenericValue childValue = delegator.findOne("RecruitmentPlanHeader", UtilMisc.toMap("partyId", item.getString("partyId"), "year", year), false);
					if(!UtilValidate.isEmpty(childValue)) {
						Map<String, Object> ctx = com.olbius.util.MapUtils.copy(context);
						ctx.put("partyId", item.get("partyId"));
						updateSaleRecruitmentPlanHeader(dpctx, ctx);
					}
				}
			}
			break;
		case RPH_REJECTED:
			//Sale reject
			createNotiCtx.put("partyId", managerId);
			createNotiCtx.put("header", "Duyệt kế hoạch tuyển dụng phòng " + PartyHelper.getPartyName(delegator, partyId, false) + " năm " + year);
			createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
			createNotiCtx.put("userLogin", userLogin);
			createNotiCtx.put("action", "FindSaleRecruitmentPlan");
			createNotiCtx.put("targetLink", "partyId=" + partyId + ";year=" + year);
			createNotiCtx.put("state", "open");
			createNotiCtx.put("ntfType", "ONE");
			dispatcher.runSync("createNotification", createNotiCtx);
			break;
		default:
			break;
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateRecruitmentPlanDTHeader(DispatchContext dpctx, Map<String, Object> context) throws Exception{
		//Get parameters
		String partyId = (String)context.get("partyId");
		String year = (String)context.get("year");
		String statusId = (String)context.get("statusId");
		String reason = (String)context.get("reason");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dpctx.getDelegator();

		//Update RecruitmentPlanHeader
		GenericValue oldValue = delegator.findOne("RecruitmentPlanDTHeader", UtilMisc.toMap("partyId", partyId, "year", year), false);
		oldValue.put("actorPartyId", userLogin.getString("partyId"));
		oldValue.put("actorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
		oldValue.put("statusId", statusId);
		oldValue.put("reason", reason);
		oldValue.store();
		
		//Send a notification to ASM,RSM,CSM
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateSaleRecruitmentPlanDTHeader(DispatchContext dpctx, Map<String, Object> context) throws Exception{
		//Get parameters
		String partyId = (String)context.get("partyId");
		String year = (String)context.get("year");
		String statusId = (String)context.get("statusId");
		String reason = (String)context.get("reason");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dpctx.getDelegator();

		//Update RecruitmentPlanHeader
		GenericValue oldValue = delegator.findOne("RecruitmentPlanDTHeader", UtilMisc.toMap("partyId", partyId, "year", year), false);
		oldValue.put("actorPartyId", userLogin.getString("partyId"));
		oldValue.put("actorRoleTypeId", RoleHelper.getCurrentRole(userLogin, delegator));
		oldValue.put("statusId", statusId);
		oldValue.put("reason", reason);
		oldValue.store();
		
		Organization tree = PartyUtil.buildOrg(delegator, partyId, true, false);
		List<GenericValue> listChild = tree.getDirectChildList(delegator);
		
		switch (PlanStatus.valueOf(statusId)) {
		case RPH_PROPOSED:
			break;
		case RPH_ACCEPTED:
			if(!UtilValidate.isEmpty(listChild)) {
				for(GenericValue item: listChild) {
					GenericValue childValue = delegator.findOne("RecruitmentPlanDTHeader", UtilMisc.toMap("partyId", item.getString("partyId"), "year", year), false);
					if(!UtilValidate.isEmpty(childValue)) {
						Map<String, Object> ctx = com.olbius.util.MapUtils.copy(context);
						ctx.put("partyId", item.get("partyId"));
						updateSaleRecruitmentPlanDTHeader(dpctx, ctx);
					}
				}
			}
			break;
		case RPH_REJECTED:
			break;
		default:
			break;
		}
		
		return ServiceUtil.returnSuccess();
	}
}
enum PlanStatus{
	RPH_INIT,
	RPH_ACCEPTED,
	RPH_PROPOSED,
	RPH_REJECTED
}