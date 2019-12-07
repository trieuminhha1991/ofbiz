package com.olbius.perfreview.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.DateUtil;
import com.olbius.util.PartyUtil;

@SuppressWarnings("unused")
public class PerfReviewServices {

	public static final String RESOURCE = "hrolbiusUiLabels";
	public static final String resourceNoti = "NotificationUiLabels";
	public static final String MODULE = PerfReviewServices.class.getName();

	public static Map<String, Object> createPerfReview(DispatchContext dpctx,
			Map<String, Object> context) {
		
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dpctx.getDelegator();
		String perfReviewId = delegator.getNextSeqId("PerfReview");
		context.put("perfReviewId", perfReviewId);
		
		try {
			insertPerfReview(dpctx, context);
			insertPerfReviewItem(dpctx, context);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "createError",
					new Object[] { e.getMessage() }, locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(
				resourceNoti, "createSuccessfully", locale));
	}
	
	public static Map<String, Object> createKPI(DispatchContext dpctx,
			Map<String, Object> context) throws Exception {
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dpctx.getDelegator();
		String KPIId = delegator.getNextSeqId("PerfReviewKPI");
		context.put("KPIId", KPIId);
		
		try {
			String emplPositionTypeId = (String) context.get("emplPositionTypeId");
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Timestamp thruDate = (Timestamp) context.get("thruDate");
			List<GenericValue> perfReviewKPI;
			perfReviewKPI = delegator.findList("PerfReviewKPI", EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId), null,null, null, false);
			if (perfReviewKPI.isEmpty()){
				if (DateUtil.checkDateTime(fromDate, thruDate)){
					insertPerfReviewKPI(dpctx, context);
				} else return ServiceUtil.returnError("Error Date Time");
			} else return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "KPIExists", locale));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "createError",
					new Object[] { e.getMessage() }, locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(
				resourceNoti, "createSuccessfully", locale));
		
	}
	
	
	/*
	 * update KPI
	 * @param DispatchContext 
	 * @param Map<?.?> Context
	 * @return 
	 * @throws Exception
	 * 
	 * */
	public static Map<String,Object> updateKPIHR(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		String KPIId = (String) context.get("KPIId");
		String description = (String) context.get("description");
		String emplPositionTypeId = (String) context.get("emplPositionTypeId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		
		try {
			if(UtilValidate.isNotEmpty(KPIId)){
				try {
					GenericValue KPIRecord = delegator.findOne("PerfReviewKPI",false, UtilMisc.toMap("KPIId", KPIId));
					KPIRecord.set("description", description);
					KPIRecord.set("emplPositionTypeId", emplPositionTypeId);
					KPIRecord.set("fromDate", fromDate);
					KPIRecord.set("thruDate", thruDate);
					KPIRecord.store();
				} catch (Exception e) {
					e.printStackTrace();
					return ServiceUtil.returnError("Error update KPI cause : " + e.getMessage());
					// TODO: handle exception
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal erro when update KPI cause : " + e.getMessage());
			// TODO: handle exception
		}
		return ServiceUtil.returnSuccess();
	}
	
	
	public static Map<String, Object> createJobRating(DispatchContext dpctx,
			Map<String, Object> context) throws Exception {
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dpctx.getDelegator();
		String jobRatingId = delegator.getNextSeqId("ListJobRating");
		context.put("jobRatingId", jobRatingId);
		
		try {
			insertJobRating(dpctx, context);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "createError",
					new Object[] { e.getMessage() }, locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(
				resourceNoti, "createSuccessfully", locale));
		
	}
	
	public static Map<String, Object> createStandardRating(DispatchContext dpctx,
			Map<String, Object> context) throws Exception {
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dpctx.getDelegator();
		String standardRatingId = delegator.getNextSeqId("standardRatingId");
		context.put("standardRatingId", standardRatingId);
		
		try {
			String emplPositionTypeId = (String) context.get("emplPositionTypeId");
			String weight = (String) context.get("weight");
			if (Integer.parseInt(weight) <= 0) return ServiceUtil.returnError("Job Weight must be bigger than 0");
			else {
				if ((getGeneralJobWeight(dpctx) + getPersonalJobWeight(dpctx, emplPositionTypeId) + Integer.parseInt(weight))<=100){
					insertStandardRating(dpctx, context);
				} else return ServiceUtil.returnError("Total of job weight must be smaller than 100");
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "createError",
					new Object[] { e.getMessage() }, locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(
				resourceNoti, "createSuccessfully", locale));
		
	}
	
	public static Map<String, Object> createGeneralStandardRating(DispatchContext dpctx,
			Map<String, Object> context) throws Exception {
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dpctx.getDelegator();
		String standardRatingId = delegator.getNextSeqId("StandardRating");
		context.put("standardRatingId", standardRatingId);
		
		try {
			String weight = (String) context.get("weight");
			if (Integer.parseInt(weight) <= 0) return ServiceUtil.returnError("Job Weight must be bigger than 0");
			else {
				if ((getGeneralJobWeight(dpctx) + Integer.parseInt(weight))<=100){
					insertStandardRating(dpctx, context);
				} else return ServiceUtil.returnError("Total of job weight must be smaller than 100");
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "createError",
					new Object[] { e.getMessage() }, locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(
				resourceNoti, "createSuccessfully", locale));
		
	}
	
	public static Map<String, Object> deleteStandardRating(DispatchContext dpctx,
			Map<String, Object> context) throws Exception {
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dpctx.getDelegator();
		try {
			String standardRatingId = (String) context.get("standardRatingId");
			String emplPositionTypeId = (String) context.get("emplPositionTypeId");
			if(emplPositionTypeId != null){
				List<GenericValue> listStandarRating = delegator.findList("StandardRating",EntityCondition.makeCondition("standardRatingId",standardRatingId),null,null,null,false);
				for(GenericValue sr : listStandarRating){
					if(sr.getString("standardRatingId").equals(standardRatingId)){
						if(!UtilValidate.isEmpty(sr.getString("emplPositionTypeId"))){
							sr.remove();
						}else{
							return ServiceUtil.returnError(UtilProperties.getMessage(
									resourceNoti, "You not have permisson remove General Standard Rating",
									 locale));
						}
					}
				}
			}else{
				List<GenericValue> listStandarRating = delegator.findList("StandardRating",EntityCondition.makeCondition("standardRatingId",standardRatingId),null,null,null,false);
				for(GenericValue sr : listStandarRating){
					if(sr.getString("standardRatingId").equals(standardRatingId)){
							sr.remove();
					}
				}
			}
			List<GenericValue> listJobRating = delegator.findList("ListJobRating", EntityCondition.makeCondition("standardRatingId", standardRatingId), null, null, null, false);
			if(UtilValidate.isNotEmpty(listJobRating)){
				for (GenericValue item : listJobRating){
					item.remove();
				}
			}
		} catch (GenericEntityException e){
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "removeError",
					new Object[] { e.getMessage() }, locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceNoti, "removeSuccessfully", locale));
	}
	
	public static Map<String, Object> deleteKPI(DispatchContext dpctx,
			Map<String, Object> context) throws Exception {
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dpctx.getDelegator();
		try {
			String KPIId = (String) context.get("KPIId");
			String emplPositionTypeId = (String) context.get("emplPositionTypeId");
			GenericValue removeKPI = delegator.makeValue("PerfReviewKPI");
			removeKPI.set("KPIId", KPIId);
			removeKPI.remove();
			List<GenericValue> listStandardRating = delegator.findList("StandardRating", EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId), null, null, null, false);
			for (GenericValue item : listStandardRating){
				item.remove();
				List<GenericValue> listJobRating = delegator.findList("ListJobRating", EntityCondition.makeCondition("standardRatingId", item.getString("standardRatingId")), null, null, null, false);
				for (GenericValue itemDetail : listJobRating){
					itemDetail.remove();
				}
			}
		} catch (GenericEntityException e){
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "removeError",
					new Object[] { e.getMessage() }, locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceNoti, "removeSuccessfully", locale));
	}
	
	public static Map<String, Object> createRatingEmpl(
			DispatchContext dpctx, Map<String, Object> context) throws GenericEntityException {

		Delegator delegator = dpctx.getDelegator();
		//Init var
		String targetLink = "";
		String header = "";
		String notiToPartyId = "";
		String action ="";
		String state="open";
		Timestamp dateTime = new Timestamp(new Date().getTime());
		
		Locale locale = (Locale) context.get("locale");
		// Get parameters
		String employeePartyId = (String) context.get("employeePartyId");
		List<GenericValue> listMarkEmpl = delegator.findList("ListMarkRatingEmployee", EntityCondition.makeCondition("employeePartyId", employeePartyId), null, null, null, false);
		
		String emplPositionTypeId = null;
		List<GenericValue> emplPositionId = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition("partyId", employeePartyId), null, null, null, false);
		if (emplPositionId.size()>0){
			List<GenericValue> emplPositionType = delegator.findList("EmplPosition", EntityCondition.makeCondition("emplPositionId", emplPositionId.get(0).getString("emplPositionId")), null, null, null, false);
			if (emplPositionType.size()>0){
				emplPositionTypeId = emplPositionType.get(0).getString("emplPositionTypeId");
			}
		}
		
		String status = null;
		List<GenericValue> listKPI = delegator.findList("PerfReviewKPI", EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId), null, null, null, false);
		if (!listKPI.isEmpty()){
			status = listKPI.get(0).getString("jobStatus");
		}
		
		Map<String,Object> result=FastMap.newInstance();
		if ((checkKPI(dpctx, emplPositionTypeId))&&(status.equals("AWT_APPROVED"))){
			if (listMarkEmpl.isEmpty()){
				header="Nhân viên tự đánh giá";
				action="ListStandardRatingEmpl";
				targetLink="employeePartyId="+employeePartyId+";"+"emplPositionTypeId="+emplPositionTypeId;
				notiToPartyId=employeePartyId;
				result.put("header", header);
				result.put("notiToId", notiToPartyId);
				result.put("state", state);
				result.put("dateTime", dateTime);
				result.put("targetLink", targetLink);
				result.put("action", action);
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
						resourceNoti, "createSuccessfully", locale));
				return result;
			} else return ServiceUtil.returnError("Error!! Employee is rated");
		} else return ServiceUtil.returnError("Error!! KPI is not enough to rating.");
		
	}
	
	public static Map<String, Object> ratingEmployee(DispatchContext dpctx,
			Map<String, Object> context) throws Exception {
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dpctx.getDelegator();
		Map<String,Object> result;
		try {
			result = insertMarkRatingEmployee(dpctx, context);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					resourceNoti, "rateError",
					new Object[] { e.getMessage() }, locale));
		}
		return result;
		
	}
	
	public static Map<String, Object> proposeKPI(
			DispatchContext dpctx, Map<String, Object> context) throws Exception {

		Delegator delegator = dpctx.getDelegator();
		//Init var
		String targetLink = "";
		String header = "";
		String notiToPartyId = "";
		String action ="";
		String state="open";
		Timestamp dateTime = new Timestamp(new Date().getTime());
		
		// Get parameters
		String emplPositionTypeId = (String) context.get("emplPositionTypeId");
		Locale locale = (Locale) context.get("locale");

		Map<String,Object> result=FastMap.newInstance();
		if (checkKPI(dpctx, emplPositionTypeId) == true){
				header="Phê duyệt KPI ["+emplPositionTypeId+"]";
				action="CheckKPI";
				targetLink="emplPositionTypeId="+emplPositionTypeId;
				notiToPartyId= PartyUtil.getCEO(delegator);
				result.put("header", header);
				result.put("notiToId", notiToPartyId);
				result.put("state", state);
				result.put("dateTime", dateTime);
				result.put("targetLink", targetLink);
				result.put("action", action);
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
						resourceNoti, "proposeSuccessfully", locale));
				return result;
		} else return ServiceUtil.returnError("Error!! KPI is not enough to propose.");
		
	}
	
	
	/**
	 * Insert into PerfReview
	 * 
	 * @param dpctx
	 * @param context
	 * @throws GenericEntityException
	 */
	public static void insertPerfReview(DispatchContext dpctx,
			Map<String, ? extends Object> context)
			throws GenericEntityException {

		// Get request parameters
		String employeePartyId = (String) context.get("employeePartyId");
		String managerPartyId = (String) context.get("managerPartyId");
		String employeeRoleTypeId = (String) context.get("employeeRoleTypeId");
		String managerRoleTypeId = (String) context.get("managerRoleTypeId");
		String paymentId = (String) context.get("paymentId");
		String perfReviewId = (String) context.get("perfReviewId");
		String emplPositionId = (String) context.get("emplPositionId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");

		Delegator delegator = dpctx.getDelegator();
		
		GenericValue perfReview = delegator.makeValue("PerfReview");
		perfReview.set("employeePartyId", employeePartyId);
		perfReview.set("employeeRoleTypeId", employeeRoleTypeId);
		perfReview.set("perfReviewId", perfReviewId);
		perfReview.set("managerPartyId", managerPartyId);
		perfReview.set("managerRoleTypeId", managerRoleTypeId);
		perfReview.set("paymentId", paymentId);
		perfReview.set("emplPositionId", emplPositionId);
		perfReview.set("fromDate", fromDate);
		perfReview.set("thruDate", thruDate);
		perfReview.create();
	}

	public static void insertPerfReviewItem(DispatchContext dpctx,
			Map<String, ? extends Object> context)
			throws GenericEntityException {
		
		// Get Request Parameters
		String emplPositionId = (String) context.get("emplPositionId");
		String employeePartyId = (String) context.get("employeePartyId");
		String employeeRoleTypeId = (String) context.get("employeeRoleTypeId");
		String perfReviewId = (String) context.get("perfReviewId");

		Delegator delegator = dpctx.getDelegator();
		List<GenericValue> perfReviewItems = new ArrayList<GenericValue>();
		perfReviewItems = delegator.findList("PerfReviewItemTypeEmplPosition", EntityCondition.makeCondition("emplPositionId", emplPositionId), null,null, null, false);
		
		int seqNumber = 0;
		for (GenericValue item : perfReviewItems) {
			GenericValue perfReviewItem = delegator.makeValue("PerfReviewItem");
			seqNumber = seqNumber+1;
			String perfReviewItemSeqId = "0000";
			perfReviewItemSeqId = perfReviewItemSeqId + seqNumber;
			perfReviewItem.set("employeePartyId", employeePartyId);
			perfReviewItem.set("employeeRoleTypeId", employeeRoleTypeId);
			perfReviewItem.set("perfReviewId", perfReviewId);
			perfReviewItem.set("perfReviewItemSeqId",perfReviewItemSeqId);
			perfReviewItem.set("perfReviewItemTypeId",
					item.get("perfReviewItemTypeId"));
			perfReviewItem.create();
		}
	}
	
	public static void insertPerfReviewKPI(DispatchContext dpctx,
			Map<String, ? extends Object> context)
			throws GenericEntityException {
		// Get Request Parameters
		String KPIId = (String) context.get("KPIId");
		String description = (String) context.get("description");
		String emplPositionTypeId = (String) context.get("emplPositionTypeId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		
		Delegator delegator = dpctx.getDelegator();	
		GenericValue addKPI = delegator.makeValue("PerfReviewKPI");
		
		addKPI.set("KPIId", KPIId);
		addKPI.set("createdBy", ((GenericValue) context.get("userLogin")).getString("partyId"));
		addKPI.set("description", description);
		addKPI.set("emplPositionTypeId", emplPositionTypeId);
		addKPI.set("fromDate", fromDate);
		addKPI.set("thruDate", thruDate);
		addKPI.set("jobStatus", "IWT_INIT");
		addKPI.create();
	}
	
	public static void insertJobRating(DispatchContext dpctx,
			Map<String, ? extends Object> context)
			throws GenericEntityException {
		// Get Request Parameters
		String jobRatingId = (String) context.get("jobRatingId");
		String standardRatingId = (String) context.get("standardRatingId");
		String jobRequirement = (String) context.get("jobRequirement");
		String jobIntent = (String) context.get("jobIntent");
		String jobTime = (String) context.get("jobTime");
		
		Delegator delegator = dpctx.getDelegator();
		GenericValue addJobRating = delegator.makeValue("ListJobRating");
		addJobRating.set("jobRatingId", jobRatingId);
		addJobRating.set("standardRatingId", standardRatingId);
		addJobRating.set("jobRequirement", jobRequirement);
		addJobRating.set("jobIntent", jobIntent);
		addJobRating.set("jobTime", jobTime);
		addJobRating.create();
	}
	
	public static void insertStandardRating(DispatchContext dpctx,
			Map<String, ? extends Object> context)
			throws GenericEntityException {
		// Get Request Parameters
		String standardRatingId = (String) context.get("standardRatingId");
		String emplPositionTypeId = (String) context.get("emplPositionTypeId");
		String standardName = (String) context.get("standardName");
		String weight = (String) context.get("weight");
		

		Delegator delegator = dpctx.getDelegator();
		GenericValue addStandardRating = delegator.makeValue("StandardRating");
		addStandardRating.set("standardRatingId", standardRatingId);
		addStandardRating.set("emplPositionTypeId", emplPositionTypeId);
		addStandardRating.set("standardName", standardName);
		addStandardRating.set("weight", weight);
		if (emplPositionTypeId != null){
			addStandardRating.set("standardType", "Personal");
		} else 
			addStandardRating.set("standardType", "General");
		addStandardRating.create();
	}
	
	public static Map<String, Object> insertMarkRatingEmployee(
			DispatchContext dpctx, Map<String, Object> context) throws Exception {
		
		//Init var
		String targetLink = "";
		String header = "";
		String notiToPartyId = "";
		String action ="";
		String state="open";
		Timestamp dateTime = new Timestamp(new Date().getTime());
		
		Delegator delegator = dpctx.getDelegator();
		// Get Request Parameters
		String employeePartyId = (String) context.get("employeePartyId");
		String emplPositionTypeId = null;
		List<GenericValue> emplPositionType = delegator.findList("EmplPosition", EntityCondition.makeCondition("partyId", employeePartyId), null, null, null, false);
		if (emplPositionType.size()>0){
			emplPositionTypeId = emplPositionType.get(0).getString("emplPositionTypeId");
		}
		
		String managerOfEmplId = PartyUtil.getManagerOfEmpl(delegator, employeePartyId);
		
		Locale locale = (Locale) context.get("locale");
		List<String> listStandardRatingId = null;
		if(context.get("standardRatingId") instanceof List){
			listStandardRatingId=(List<String>) context.get("standardRatingId");
		}
		List<String> listJobMark = null;
		if(context.get("jobMark") instanceof List){
			listJobMark=(List<String>) context.get("jobMark");
		}
		List<String> listJobResult = null;
		if(context.get("jobResult") instanceof List){
			listJobResult=(List<String>) context.get("jobResult");
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		String ratingType;
		if (employeePartyId.equals(partyId)) ratingType = "Self-Rating";
		else ratingType = "Leader-Rating";
		
		for (int i=0; i<listStandardRatingId.size(); i++){
			GenericValue addMarkRatingEmpl = delegator.makeValue("ListMarkRatingEmployee");
			String ratingEmplId = delegator.getNextSeqId("ratingEmplId");
			addMarkRatingEmpl.set("ratingEmplId", ratingEmplId);
			addMarkRatingEmpl.set("employeePartyId", employeePartyId);
			addMarkRatingEmpl.set("standardRatingId", listStandardRatingId.get(i));
			addMarkRatingEmpl.set("jobMark", listJobMark.get(i));
			addMarkRatingEmpl.set("jobResult", listJobResult.get(i));
			addMarkRatingEmpl.set("ratingType", ratingType);
			addMarkRatingEmpl.create();
		}
		
		Map<String,Object> result=FastMap.newInstance();
		if (partyId.equals(managerOfEmplId)){
			header="Kết quả đánh giá nhân viên";
			action="ListResultRatingEmpl";
			targetLink="employeePartyId="+employeePartyId+";"+"emplPositionTypeId="+emplPositionTypeId;
			notiToPartyId= employeePartyId;
			result.put("header", header);
			result.put("notiToId", notiToPartyId);
			result.put("state", state);
			result.put("dateTime", dateTime);
			result.put("targetLink", targetLink);
			result.put("action", action);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
					resourceNoti, "rateSuccessfully", locale));
		} else {
			header="Đánh giá nhân viên ["+employeePartyId+"]";
			action="ListStandardRatingEmpl";
			targetLink="employeePartyId="+employeePartyId+";"+"emplPositionTypeId="+emplPositionTypeId;
			notiToPartyId= managerOfEmplId;
			result.put("header", header);
			result.put("notiToId", notiToPartyId);
			result.put("state", state);
			result.put("dateTime", dateTime);
			result.put("targetLink", targetLink);
			result.put("action", action);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
					resourceNoti, "rateSuccessfully", locale));
		}
		
		return result;
		
	}
	
	public static int getGeneralJobWeight(DispatchContext dpctx) throws GenericEntityException {
		Delegator delegator = dpctx.getDelegator();
		List<GenericValue> listGeneralJobWeight;
		listGeneralJobWeight = delegator.findList("StandardRating", EntityCondition.makeCondition("standardType", "General"),null, null, null, false);
		int generalJobWeightTotal = 0;
		for (int i=0; i<listGeneralJobWeight.size(); i++){
			generalJobWeightTotal += Integer.parseInt(listGeneralJobWeight.get(i).getString("weight"));
		}
		return generalJobWeightTotal;
	}
	
	public static int getPersonalJobWeight(DispatchContext dpctx, String emplPositionTypeId) throws GenericEntityException {
		Delegator delegator = dpctx.getDelegator();
		List<GenericValue> listPersonalJobWeight;
		listPersonalJobWeight = delegator.findList("StandardRating", EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId),null, null, null, false);
		int personalJobWeightTotal = 0;
		for (int i=0; i<listPersonalJobWeight.size(); i++){
			personalJobWeightTotal += Integer.parseInt(listPersonalJobWeight.get(i).getString("weight"));
		}
		return personalJobWeightTotal;
	}
	
	//Function to check KPI 
	public static boolean checkKPI (DispatchContext dpctx,String emplPositionTypeId) throws GenericEntityException {
		Delegator delegator = dpctx.getDelegator();
		boolean check = false;
		List<GenericValue> listGeneral = delegator.findList("StandardRating", EntityCondition.makeCondition("standardType", "General"),null, null, null, false);
		ArrayList<String> generalStandardRating = new ArrayList<String>();
		for (int i=0; i<listGeneral.size(); i++){
			generalStandardRating.add(i, listGeneral.get(i).getString("standardRatingId"));
		}
		
		int num1=0;
		for (String id1:generalStandardRating){
			List<GenericValue> listGeneralJobRating = delegator.findList("ListJobRating", EntityCondition.makeCondition("standardRatingId", id1), null, null, null, false);
			if(listGeneralJobRating.size()==0) num1++;
		}
		
		if (emplPositionTypeId != null){
			List<GenericValue> listPersonal = delegator.findList("StandardRating", EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId),null, null, null, false);
			ArrayList<String> standardRating = new ArrayList<String>();
			for (int i=0; i<listPersonal.size(); i++){
				standardRating.add(i, listPersonal.get(i).getString("standardRatingId"));
			}
			
			int num2=0;
			for (String id2:standardRating){
			List<GenericValue> listJobRating = delegator.findList("ListJobRating", EntityCondition.makeCondition("standardRatingId", id2), null, null, null, false);
			if(listJobRating.size()==0) num2++;
			}
			
			if ((getGeneralJobWeight(dpctx)+getPersonalJobWeight(dpctx, emplPositionTypeId))==100&&(num1 == 0)&&(num2==0)){
				check = true;
			}
		}
		return check;
	}
	
	
	public static Map<String, Object> updateEmplOverallRating(DispatchContext dpctx, Map<String, Object> context){
		
		//Get Parameters
		String employeePartyId = (String) context.get("employeePartyId");
		String employeeRoleTypeId = (String) context.get("employeeRoleTypeId");
		String perfReviewId = (String) context.get("perfReviewId");
		
		Delegator delegator = dpctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		List<GenericValue> perfReviewItemRates = new ArrayList<GenericValue>();
		GenericValue perfReview = null;
		
		List<EntityExpr> exprs = UtilMisc.toList(
				EntityCondition.makeCondition("employeePartyId", employeePartyId),
				EntityCondition.makeCondition("employeeRoleTypeId", employeeRoleTypeId),
				EntityCondition.makeCondition("perfReviewId", perfReviewId));
		EntityCondition condition =  EntityCondition.makeCondition(exprs);
		
		try {
			perfReviewItemRates = delegator.findList("EmplPerfReviewItemRate", condition, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "findError", new Object[] {e.getMessage()}, locale));
		}
		Double emplOverallRating = 0d;
		for(GenericValue item : perfReviewItemRates){
			Double weight =  item.getDouble("weight");
			Long rate = item.getLong("rate");
			emplOverallRating += weight*rate;
		}
		
		try {
			perfReview = delegator.findOne("PerfReview", UtilMisc.toMap("employeePartyId", employeePartyId, "employeeRoleTypeId", employeeRoleTypeId, "perfReviewId", perfReviewId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "findError", new Object[] {e.getMessage()}, locale));
		}
		
		perfReview.put("employeePartyId", employeePartyId);
		perfReview.put("employeeRoleTypeId", employeeRoleTypeId);
		perfReview.put("perfReviewId", perfReviewId);
		perfReview.put("emplOverallRating", emplOverallRating);
		
		try {
			perfReview.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "updateError", new Object[] {e.getMessage()}, locale));
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceNoti, "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> updateMgrOverallRating(DispatchContext dpctx, Map<String, Object> context){
		
		//Get Parameters
		String employeePartyId = (String) context.get("employeePartyId");
		String employeeRoleTypeId = (String) context.get("employeeRoleTypeId");
		String perfReviewId = (String) context.get("perfReviewId");
		
		Delegator delegator = dpctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		List<GenericValue> perfReviewItemRates = new ArrayList<GenericValue>();
		GenericValue perfReview = null;
		
		List<EntityExpr> exprs = UtilMisc.toList(
				EntityCondition.makeCondition("employeePartyId", employeePartyId),
				EntityCondition.makeCondition("employeeRoleTypeId", employeeRoleTypeId),
				EntityCondition.makeCondition("perfReviewId", perfReviewId));
		EntityCondition condition =  EntityCondition.makeCondition(exprs);
		
		try {
			perfReviewItemRates = delegator.findList("MgrPerfReviewItemRate", condition, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "findError", new Object[] {e.getMessage()}, locale));
		}
		Double mgrOverallRating = 0d;
		for(GenericValue item : perfReviewItemRates){
			Double weight =  item.getDouble("weight");
			Long rate = item.getLong("rate");
			mgrOverallRating += weight*rate;
		}
		
		try {
			perfReview = delegator.findOne("PerfReview", UtilMisc.toMap("employeePartyId", employeePartyId, "employeeRoleTypeId", employeeRoleTypeId, "perfReviewId", perfReviewId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "findError", new Object[] {e.getMessage()}, locale));
		}
		
		perfReview.put("employeePartyId", employeePartyId);
		perfReview.put("employeeRoleTypeId", employeeRoleTypeId);
		perfReview.put("perfReviewId", perfReviewId);
		perfReview.put("mgrOverallRating", mgrOverallRating);
		
		try {
			perfReview.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, "updateError", new Object[] {e.getMessage()}, locale));
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceNoti, "updateSuccessfully", locale));
	}
	
	/*
	 * get List KPI JQX
	 * 
	 * */
	public static Map<String,Object> JQgetListKPI(DispatchContext dpct , Map<String,Object> context) {
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listKPI = null;
		try {
			String partyId = ((GenericValue) context.get("userLogin")).getString("partyId");
			if(!PartyUtil.isAdmin(partyId, delegator)){
				listAllConditions.add(EntityCondition.makeCondition("createdBy",((GenericValue) context.get("userLogin")).getString("partyId")));
			};
			if(UtilValidate.isNotEmpty(listSortFields)){
				listKPI = delegator.find("PerfReviewKPI", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);	
			}else{
				List<String> orderBy = FastList.newInstance();
				listKPI = delegator.find("PerfReviewKPI", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, null, opts);				}
			if(UtilValidate.isNotEmpty(listKPI)){
				result.put("listIterator", listKPI);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return result;
	}
	
	
	/*
	 * get List General Standarad Rating JQX
	 * 
	 * */
	public static Map<String,Object> JQgetListStandardRating(DispatchContext dpct , Map<String,Object> context) {
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listStandardRating = null;
		try {
			listAllConditions.add(EntityCondition.makeCondition("standardType",EntityOperator.EQUALS,"General"));
			if(UtilValidate.isNotEmpty(listSortFields)){
				listStandardRating = delegator.find("StandardRating", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);	
			}else{
				List<String> orderBy = FastList.newInstance();
				listStandardRating = delegator.find("StandardRating", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, null, opts);				}
			if(UtilValidate.isNotEmpty(listStandardRating)){
				result.put("listIterator", listStandardRating);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return result;
	}
	
	
	
	/*
	 * get List Standarad Rating JQX
	 * 
	 * */
	public static Map<String,Object> JQgetListStandardRt(DispatchContext dpct , Map<String,Object> context) {
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		String emplPositionTypeId = (String) parameters.get("emplPositionTypeId")[0];
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listStandardRating = null;
		try {
			EntityCondition en1 =  EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId);
			EntityCondition en2 =  EntityCondition.makeCondition("emplPositionTypeId", null);
			EntityCondition en = EntityCondition.makeCondition(EntityJoinOperator.OR, en1, en2);
			listAllConditions.add(en);
			if(UtilValidate.isNotEmpty(listSortFields)){
				listStandardRating = delegator.find("StandardRating",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);		
			}else{
				listStandardRating = delegator.find("StandardRating",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);	
			}
			if(UtilValidate.isNotEmpty(listStandardRating)){
				result.put("listIterator", listStandardRating);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return result;
	}
	
	/*
	 * get List ProposeKPI JQX
	 * 
	 * */
	public static Map<String,Object> JQgetListProposeKPI(DispatchContext dpct , Map<String,Object> context) {
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listStandardRating = null;
		try {
			EntityCondition condStatus = EntityCondition.makeCondition("jobStatus","IWT_INIT");
			EntityCondition condCreatedBy = EntityCondition.makeCondition("createdBy",((GenericValue) context.get("userLogin")).getString("partyId"));
			listAllConditions.add(condStatus);
			listAllConditions.add(condCreatedBy);
			if(UtilValidate.isNotEmpty(listSortFields)){
				listStandardRating = delegator.find("PerfReviewKPI",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);		
			}else{
				listStandardRating = delegator.find("PerfReviewKPI",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);	
			}
			if(UtilValidate.isNotEmpty(listStandardRating)){
				result.put("listIterator", listStandardRating);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return result;
	}
	
	
	/*
	 * JQgetListJobRating JQX
	 * 
	 * */
	public static Map<String,Object> JQgetListJobRating(DispatchContext dpct , Map<String,Object> context) {
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator jobRatingList = null;
		
		String standardRatingId = (String) parameters.get("standardRatingId")[0];
		
		try {
			EntityCondition condStatus = EntityCondition.makeCondition("standardRatingId",standardRatingId);
			listAllConditions.add(condStatus);
			if(UtilValidate.isNotEmpty(listSortFields)){
				jobRatingList = delegator.find("ListJobRating",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);		
			}else{
				jobRatingList = delegator.find("ListJobRating",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);	
			}
			if(UtilValidate.isNotEmpty(jobRatingList)){
				result.put("listIterator", jobRatingList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return result;
	}
	
	/*
	 * JQgetListJobRating JQX
	 * 
	 * */
	public static Map<String,Object> JQgetListGeneralJobRating(DispatchContext dpct , Map<String,Object> context) {
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator jobRatingList = null;
		
		String standardRatingId = (String) parameters.get("standardRatingId")[0];
		
		try {
			EntityCondition condStatus = EntityCondition.makeCondition("standardRatingId",standardRatingId);
			listAllConditions.add(condStatus);
			if(UtilValidate.isNotEmpty(listSortFields)){
				jobRatingList = delegator.find("ListJobRating",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);		
			}else{
				jobRatingList = delegator.find("ListJobRating",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);	
			}
			if(UtilValidate.isNotEmpty(jobRatingList)){
				result.put("listIterator", jobRatingList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return result;
	}
	@SuppressWarnings({ "unchecked" })
	public static Map<String,Object> getListCriteriaAndDetail(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		opts.setDistinct(true);
		String fromDatefilter = "";
		String thruDatefilter = "";
		try {
				fromDatefilter = parameters.get("fromDatefilter")[0];
				thruDatefilter = parameters.get("thruDatefilter")[0];
				if(!fromDatefilter.equals("null")){
					Timestamp fromDate = new Timestamp(Long.parseLong(fromDatefilter));
					if(!thruDatefilter.equals("null")){
						Timestamp thruDate = new Timestamp(Long.parseLong(thruDatefilter));
						if(fromDate != null){
							if(thruDate != null){
								listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
							}else{
								listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
																	EntityJoinOperator.OR,
																	EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));
							}
						}
					}
				}
				
			listAllConditions.add(EntityCondition.makeConditionDate("fromDate", "thruDate"));
			listIterator = delegator.find("PerfCriteria", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getListCriteriaChild(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		opts.setDistinct(true);
		try {
			String criteriaIdParent = null;
			if(UtilValidate.isNotEmpty(parameters) || parameters.containsKey("criteriaIdParent") && parameters.get("criteriaIdParent").length > 0){
				criteriaIdParent = parameters.get("criteriaIdParent")[0];
			}
			if(UtilValidate.isNotEmpty(criteriaIdParent)){
				listAllConditions.add(EntityCondition.makeCondition("parentTypeId", criteriaIdParent));
				listIterator = delegator.find("PerfCriteria", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String,Object> createNewChildCriteria(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		String criteriaName = (String) context.get("criteriaName");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String parentTypeId = (String) context.get("parentTypeId");
		try {
			String criteriaId = delegator.getNextSeqId("PerfCriteria");
			GenericValue ChildCriteria = delegator.makeValue("PerfCriteria");
			if(UtilValidate.isNotEmpty(thruDate)){
				ChildCriteria.set("criteriaId", criteriaId);
				ChildCriteria.set("criteriaName", criteriaName);
				ChildCriteria.set("fromDate", fromDate);
				ChildCriteria.set("thruDate", thruDate);
				ChildCriteria.set("parentTypeId", parentTypeId);
				ChildCriteria.create();
			}else{
				ChildCriteria.set("criteriaId", criteriaId);
				ChildCriteria.set("criteriaName", criteriaName);
				ChildCriteria.set("fromDate", fromDate);
				ChildCriteria.set("thruDate", null);
				ChildCriteria.set("parentTypeId", parentTypeId);
				ChildCriteria.create();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		return successResult;
	}
	public static Map<String,Object> createParentCriteria(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		String criteriaName = (String) context.get("criteriaName");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		try {
			String criteriaId = delegator.getNextSeqId("PerfCriteria");
			GenericValue ChildCriteria = delegator.makeValue("PerfCriteria");
			if(UtilValidate.isNotEmpty(thruDate)){
				ChildCriteria.set("criteriaId", criteriaId);
				ChildCriteria.set("criteriaName", criteriaName);
				ChildCriteria.set("fromDate", fromDate);
				ChildCriteria.set("thruDate", thruDate);
				ChildCriteria.create();
			}else{
				ChildCriteria.set("criteriaId", criteriaId);
				ChildCriteria.set("criteriaName", criteriaName);
				ChildCriteria.set("fromDate", fromDate);
				ChildCriteria.set("thruDate", null);
				ChildCriteria.create();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		return successResult;
	}
	public static Map<String,Object> deleteCriteria(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		String criteriaId = (String) context.get("criteriaId");
		Timestamp now = UtilDateTime.nowTimestamp();
		try {
			GenericValue CriteriaToDelete = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
			CriteriaToDelete.set("thruDate", now);
			CriteriaToDelete.store();
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		return successResult;
	}
}
