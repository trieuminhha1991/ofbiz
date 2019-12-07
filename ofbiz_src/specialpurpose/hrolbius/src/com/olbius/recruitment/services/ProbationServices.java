package com.olbius.recruitment.services;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.recruitment.helper.RoleTyle;
import com.olbius.services.JqxWidgetSevices;
import com.olbius.util.PartyHelper;
import com.olbius.util.PartyUtil;
import com.olbius.util.SecurityUtil;

public class ProbationServices implements RoleTyle{
	
	public static final String module = ProbationServices.class.getName();
	
	public static Map<String, Object> approveOfferProbation(DispatchContext dpctx, Map<String, Object> context) {
		//Get Dispatcher
		LocalDispatcher dispatcher = dpctx.getDispatcher();
		
		//Get Delegator
		Delegator delegator = dispatcher.getDelegator();
		
		//Get parameters
		String offerProbationId = (String)context.get("offerProbationId");
		String statusId = (String)context.get("statusId");
		String comment = (String)context.get("comment");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<String> listCurrentRole = SecurityUtil.getCurrentRoles(userLogin.getString("partyId"), delegator);
		String approverRole = EMPL_ROLE;
		for (String item : listCurrentRole) {
			if(item.equals(CEO_ROLE)) {
				approverRole = CEO_ROLE;
				break;
			}else if (item.equals(HRM_ROLE)) {
				approverRole = HRM_ROLE;
				break;
			}else if (item.equals(MANAGER_ROLE)) {
				approverRole = MANAGER_ROLE;
				break;
			}
		}
		
		//Update Offer Probation
		Map<String, Object> updateOfferProbationCtx = FastMap.newInstance();
		updateOfferProbationCtx.put("offerProbationId", offerProbationId);
		updateOfferProbationCtx.put("approverPartyId", userLogin.getString("partyId"));
		updateOfferProbationCtx.put("approverRoleTypeId", approverRole);
		updateOfferProbationCtx.put("statusId", statusId);
		updateOfferProbationCtx.put("comment", comment);
		updateOfferProbationCtx.put("userLogin", userLogin);
		try {
			dispatcher.runSyncIgnore("updateOfferProbation", updateOfferProbationCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> proposeOfferProbation(DispatchContext dpctx, Map<String, Object> context) {
		//Get Dispatcher
		LocalDispatcher dispatcher = dpctx.getDispatcher();
		
		//Get Delegator
		Delegator delegator = dpctx.getDelegator();
		
		//Get parameters
		String offerProbationId = (String)context.get("offerProbationId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String approverRoleTypeId = (String)context.get("approverRoleTypeId");
		String workEffortId = (String)context.get("workEffortId");
		String partyId = (String)context.get("partyId");
		
		//Send Notification
		Map<String, Object> createNotiRS = null;
		if(approverRoleTypeId.equals(HRM_ROLE) && "_NA_".equals(workEffortId)) {
			Map<String, Object> createNotiCtx = FastMap.newInstance();
			createNotiCtx.put("partyId", PartyUtil.getCEO(delegator));
			createNotiCtx.put("header", "Phê duyệt tờ trình tuyển dụng nhân viên " + PartyHelper.getPartyName(delegator, partyId, true, true));
			createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
			createNotiCtx.put("userLogin", userLogin);
			createNotiCtx.put("action", "FindDirectedInduction");
			createNotiCtx.put("targetLink", "partyId=" + partyId);
			createNotiCtx.put("state", "open");
			createNotiCtx.put("ntfType", "ONE");
			try {
				createNotiRS = dispatcher.runSync("createNotification", createNotiCtx);
			} catch (GenericServiceException e) {
				Debug.log(e.getMessage(), module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}else if(approverRoleTypeId.equals(HRM_ROLE) && !"_NA_".equals(workEffortId)){
			Map<String, Object> createNotiCtx = FastMap.newInstance();
			createNotiCtx.put("partyId", PartyUtil.getCEO(delegator));
			createNotiCtx.put("header", "Phê duyệt tờ trình tuyển dụng nhân viên " + PartyHelper.getPartyName(delegator, partyId, true, true));
			createNotiCtx.put("dateTime", new Timestamp(Calendar.getInstance().getTimeInMillis()));
			createNotiCtx.put("userLogin", userLogin);
			createNotiCtx.put("action", "FindInductedAppl?workEffortId=" + workEffortId);
			createNotiCtx.put("targetLink", "partyId=" + partyId);
			createNotiCtx.put("state", "open");
			createNotiCtx.put("ntfType", "ONE");
			try {
				createNotiRS = dispatcher.runSync("createNotification", createNotiCtx);
			} catch (GenericServiceException e) {
				Debug.log(e.getMessage(), module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		
		//Update Offer Probation
		Map<String, Object> updateOfferProbationCtx = FastMap.newInstance();
		updateOfferProbationCtx.put("offerProbationId", offerProbationId);
		updateOfferProbationCtx.put("statusId", "PROB_PROPOSED");
		updateOfferProbationCtx.put("userLogin", userLogin);
		try {
			if(createNotiRS != null && ServiceUtil.isSuccess(createNotiRS)) {
				dispatcher.runSyncIgnore("updateOfferProbation", updateOfferProbationCtx);
			}
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createProbationaryReport(DispatchContext dpctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = dpctx.getDelegator();
		
		//Get Dispatcher
		LocalDispatcher dispatcher = dpctx.getDispatcher();
		
		//Get parameters
		String offerProbationId = (String)context.get("offerProbationId");
		String education = (String)context.get("education");
		String completedJob = (String)context.get("completedJob");
		String advantageAndDisadvantage = (String)context.get("advantageAndDisadvantage");
		String futureJob = (String)context.get("futureJob");
		String jobProposal = (String)context.get("jobProposal");
		String policyProposal = (String)context.get("policyProposal");
		String eduWishes = (String)context.get("eduWishes");
		String listProbReviewItemsStr = (String)context.get("listProbReviewItems");
		Date fromDate = (Date)context.get("fromDate");
		Date thruDate = (Date)context.get("thruDate");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		//Create ProbationaryReport
		GenericValue probReport = delegator.makeValue("ProbationaryReport");
		String probReportId = delegator.getNextSeqId("ProbationaryReport");
		probReport.put("probReportId", probReportId);
		probReport.put("offerProbationId", offerProbationId);
		probReport.put("education", education);
		probReport.put("completedJob", completedJob);
		probReport.put("advantageAndDisadvantage", advantageAndDisadvantage);
		probReport.put("futureJob", futureJob);
		probReport.put("jobProposal", jobProposal);
		probReport.put("policyProposal", policyProposal);
		probReport.put("eduWishes", eduWishes);
		probReport.put("fromDate", fromDate);
		probReport.put("thruDate", thruDate);
		probReport.put("reportDate", new Date(Calendar.getInstance().getTimeInMillis()));
		//Create ProbationaryReview
		GenericValue probReview = delegator.makeValue("ProbationaryReview");
		String probReviewId = delegator.getNextSeqId("ProbationaryReview");
		probReview.put("probReviewId", probReviewId);
		probReview.put("probReportId", probReportId);
		probReview.put("partyId", userLogin.get("partyId"));
		probReview.put("roleTypeId", EMPL_ROLE);
		try {
			probReport.create();
			probReview.create();
			List<Map<String, Object>> listProbReviewItems = (List<Map<String,Object>>)JqxWidgetSevices.convert("java.util.List", listProbReviewItemsStr);
			for(Map<String, Object> item: listProbReviewItems) {
				GenericValue probReviewItem = delegator.makeValue("ProbationaryReviewItem");
				String probReviewItemId = delegator.getNextSeqId("ProbationaryReviewItem");
				probReviewItem.put("probReviewItemId", probReviewItemId);
				probReviewItem.put("probReviewItemTypeId", item.get("probReviewItemTypeId"));
				probReviewItem.put("resultId", item.get("resultId"));
				probReviewItem.put("comment", item.get("comment"));
				probReviewItem.put("probReviewId", probReviewId);
				delegator.setNextSubSeqId(probReviewItem, "probReviewItemSeqId", 5, 1);
				probReviewItem.create();
			}
			
			//Update Probation
			Map<String, Object> updateOfferProbationCtx = FastMap.newInstance();
			updateOfferProbationCtx.put("offerProbationId", offerProbationId);
			updateOfferProbationCtx.put("statusId", "PROB_RP_CREATED");
			updateOfferProbationCtx.put("userLogin", userLogin);
			dispatcher.runSync("updateOfferProbation", updateOfferProbationCtx);
		} catch (ParseException e) {
			Debug.log(e.getMessage(), module);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("probReportId", probReportId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createLeaderReview(DispatchContext dpctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = dpctx.getDelegator();
		
		//Get dispatcher
		LocalDispatcher dispatcher = dpctx.getDispatcher();
		
		//Get parameters
		String offerProbationId = (String)context.get("offerProbationId");
		String comment = (String)context.get("comment");
		String resultId = (String)context.get("resultId");
		Long extTime = (Long)context.get("extTime");
		String assignedTask = (String)context.get("assignedTask");
		String futureEdu = (String)context.get("futureEdu");
		String otherReq = (String)context.get("otherReq");
		String listProbReviewItemsStr = (String)context.get("listProbReviewItems");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		//Create ProbationaryReport
		List<GenericValue> probReport = null;
		try {
			probReport = (List<GenericValue>) delegator.findByAnd("ProbationaryReport", UtilMisc.toMap("offerProbationId", offerProbationId), null, false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getMessage(), module);
		}
		String probReportId = probReport.get(0).getString("probReportId");
		//Create ProbationaryReview
		GenericValue probReview = delegator.makeValue("ProbationaryReview");
		String probReviewId = delegator.getNextSeqId("ProbationaryReview");
		probReview.put("probReviewId", probReviewId);
		probReview.put("probReportId", probReportId);
		probReview.put("partyId", userLogin.get("partyId"));
		probReview.put("roleTypeId", MANAGER_ROLE);
		probReview.put("resultId", resultId);
		probReview.put("comment", comment);
		if(resultId.equals("RR_EXTTIME")) {
			probReview.put("extTime", extTime);
		}else if(resultId.equals("RR_ACCEPTED")) {
			probReview.put("assignedTask", assignedTask);
			probReview.put("futureEdu", futureEdu);
			probReview.put("otherReq", otherReq);
		}
		try {
			probReview.create();
			List<Map<String, Object>> listProbReviewItems = (List<Map<String,Object>>)JqxWidgetSevices.convert("java.util.List", listProbReviewItemsStr);
			for(Map<String, Object> item: listProbReviewItems) {
				GenericValue probReviewItem = delegator.makeValue("ProbationaryReviewItem");
				String probReviewItemId = delegator.getNextSeqId("ProbationaryReviewItem");
				probReviewItem.put("probReviewItemId", probReviewItemId);
				probReviewItem.put("probReviewItemTypeId", item.get("probReviewItemTypeId"));
				probReviewItem.put("resultId", item.get("resultId"));
				probReviewItem.put("comment", item.get("comment"));
				probReviewItem.put("probReviewId", probReviewId);
				delegator.setNextSubSeqId(probReviewItem, "probReviewItemSeqId", 5, 1);
				probReviewItem.create();
			}
			
			//Update Probation
			Map<String, Object> updateOfferProbationCtx = FastMap.newInstance();
			updateOfferProbationCtx.put("offerProbationId", offerProbationId);
			updateOfferProbationCtx.put("statusId", "PROB_RP_LREVIEWED");
			updateOfferProbationCtx.put("userLogin", userLogin);
			dispatcher.runSync("updateOfferProbation", updateOfferProbationCtx);
		} catch (ParseException e) {
			Debug.log(e.getMessage(), module);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("probReviewId", probReviewId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateLeaderReview(DispatchContext dpctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = dpctx.getDelegator();
		
		//Get parameters
		String resultId = (String)context.get("resultId");
		String comment = (String)context.get("comment");
		String probReviewId = (String)context.get("probReviewId");
		Long extTime = (Long)context.get("extTime");
		String assignedTask = (String)context.get("assignedTask");
		String futureEdu = (String)context.get("futureEdu");
		String otherReq = (String)context.get("otherReq");
		String listProbReviewItemsStr = (String)context.get("listProbReviewItems");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			//Update ProbationaryReview
			GenericValue probReview = delegator.findOne("ProbationaryReview", UtilMisc.toMap("probReviewId", probReviewId), false);
			probReview.put("partyId", userLogin.get("partyId"));
			probReview.put("resultId", resultId);
			probReview.put("comment", comment);
			if(resultId.equals("RR_EXTTIME")) {
				probReview.put("extTime", extTime);
			}else if(resultId.equals("RR_ACCEPTED")) {
				probReview.put("assignedTask", assignedTask);
				probReview.put("futureEdu", futureEdu);
				probReview.put("otherReq", otherReq);
				probReview.put("extTime", null);
			}else if(resultId.equals("RR_REJECTED")) {
				probReview.put("extTime", null);
			}
			probReview.store();
			List<Map<String, Object>> listProbReviewItems = (List<Map<String,Object>>)JqxWidgetSevices.convert("java.util.List", listProbReviewItemsStr);
			for(Map<String, Object> item: listProbReviewItems) {
				GenericValue probReviewItem = delegator.findOne("ProbationaryReviewItem", UtilMisc.toMap("probReviewItemId", item.get("probReviewItemId")), false);
				probReviewItem.put("resultId", item.get("resultId"));
				probReviewItem.put("comment", item.get("comment"));
				probReviewItem.store();
			}
			
		} catch (ParseException e) {
			Debug.log(e.getMessage(), module);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	
	public static Map<String, Object> createHrmReview(DispatchContext dpctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = dpctx.getDelegator();
		
		//Get dispatcher
		LocalDispatcher dispatcher = dpctx.getDispatcher();
		
		//Get parameters
		String offerProbationId = (String)context.get("offerProbationId");
		String resultId = (String)context.get("resultId");
		Long extTime = (Long)context.get("extTime");
		String comment = (String)context.get("comment");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
		//Create ProbationaryReview
		List<GenericValue> probReport = null;
		try {
			probReport = (List<GenericValue>) delegator.findByAnd("ProbationaryReport", UtilMisc.toMap("offerProbationId", offerProbationId), null, false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getMessage(), module);
		}
		String probReportId = probReport.get(0).getString("probReportId");
		GenericValue probReview = delegator.makeValue("ProbationaryReview");
		String probReviewId = delegator.getNextSeqId("ProbationaryReview");
		probReview.put("probReviewId", probReviewId);
		probReview.put("probReportId", probReportId);
		probReview.put("partyId", userLogin.get("partyId"));
		probReview.put("roleTypeId", HRM_ROLE);
		probReview.put("comment", comment);
		probReview.put("resultId", resultId);
		if(resultId.equals("RR_EXTTIME")) {
			probReview.put("extTime", extTime);
		}
		try {
			probReview.create();
			
			//Update Probation
			Map<String, Object> updateOfferProbationCtx = FastMap.newInstance();
			updateOfferProbationCtx.put("offerProbationId", offerProbationId);
			updateOfferProbationCtx.put("statusId", "PROB_RP_HREVIEWED");
			updateOfferProbationCtx.put("userLogin", userLogin);
			dispatcher.runSync("updateOfferProbation", updateOfferProbationCtx);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("probReviewId", probReviewId);
		return result;
	}
	
	public static Map<String, Object> updateHrmReview(DispatchContext dpctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = dpctx.getDelegator();
		
		//Get parameters
		String resultId = (String)context.get("resultId");
		String probReviewId = (String)context.get("probReviewId");
		Long extTime = (Long)context.get("extTime");
		String comment = (String)context.get("comment");
		
		try {
			GenericValue probReview = delegator.findOne("ProbationaryReview", UtilMisc.toMap("probReviewId", probReviewId), false);
			probReview.put("comment", comment);
			probReview.put("resultId", resultId);
			if(resultId.equals("RR_ACCEPTED")) {
				probReview.put("extTime", null);
			}else if(resultId.equals("RR_REJECTED")) {
				probReview.put("extTime", null);
			}else if(resultId.equals("RR_EXTTIME")) {
				probReview.put("extTime", extTime);
			}
			probReview.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	
	public static Map<String, Object> createCeoReview(DispatchContext dpctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = dpctx.getDelegator();
		
		//Get dispatcher
		LocalDispatcher dispatcher = dpctx.getDispatcher();
		
		//Get parameters
		String offerProbationId = (String)context.get("offerProbationId");
		String resultId = (String)context.get("resultId");
		Long extTime = (Long)context.get("extTime");
		String comment = (String)context.get("comment");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
		//Create ProbationaryReview
		List<GenericValue> probReport = null;
		try {
			probReport = (List<GenericValue>) delegator.findByAnd("ProbationaryReport", UtilMisc.toMap("offerProbationId", offerProbationId), null, false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getMessage(), module);
		}
		String probReportId = probReport.get(0).getString("probReportId");
		GenericValue probReview = delegator.makeValue("ProbationaryReview");
		String probReviewId = delegator.getNextSeqId("ProbationaryReview");
		probReview.put("probReviewId", probReviewId);
		probReview.put("probReportId", probReportId);
		probReview.put("partyId", userLogin.get("partyId"));
		probReview.put("roleTypeId", CEO_ROLE);
		probReview.put("comment", comment);
		probReview.put("resultId", resultId);
		if(resultId.equals("RR_EXTTIME")) {
			probReview.put("extTime", extTime);
		}
		try {
			probReview.create();
			
			//Update Probation
			Map<String, Object> updateOfferProbationCtx = FastMap.newInstance();
			updateOfferProbationCtx.put("offerProbationId", offerProbationId);
			updateOfferProbationCtx.put("statusId", "PROB_RP_CREVIEWED");
			updateOfferProbationCtx.put("userLogin", userLogin);
			dispatcher.runSync("updateOfferProbation", updateOfferProbationCtx);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("probReviewId", probReviewId);
		return result;
	}
	
	public static Map<String, Object> updateCeoReview(DispatchContext dpctx, Map<String, Object> context) {
		//Get delegator
		Delegator delegator = dpctx.getDelegator();
		
		//Get parameters
		String resultId = (String)context.get("resultId");
		String probReviewId = (String)context.get("probReviewId");
		Long extTime = (Long)context.get("extTime");
		String comment = (String)context.get("comment");
		
		try {
			GenericValue probReview = delegator.findOne("ProbationaryReview", UtilMisc.toMap("probReviewId", probReviewId), false);
			probReview.put("comment", comment);
			probReview.put("resultId", resultId);
			if(resultId.equals("RR_ACCEPTED")) {
				probReview.put("extTime", null);
			}else if(resultId.equals("RR_REJECTED")) {
				probReview.put("extTime", null);
			}else if(resultId.equals("RR_EXTTIME")) {
				probReview.put("extTime", extTime);
			}
			probReview.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
}
