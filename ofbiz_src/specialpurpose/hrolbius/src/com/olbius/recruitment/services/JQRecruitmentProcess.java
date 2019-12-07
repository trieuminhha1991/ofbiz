package com.olbius.recruitment.services;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.PartyUtil;

public class JQRecruitmentProcess {
	
	public static final String module = JQRecruitmentProcess.class.getName();
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListRecruitmentProcess(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("workEffortTypeId", "RECRUITMENT_PROCESS");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("WorkEffort", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListRecruitmentProcess service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListOfficeRecruitmentProcess(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("workEffortTypeId", "RECRUITMENT_PROCESS");
		mapCondition.put("workEffortPurposeTypeId", "RP_OFFICEEMPL");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("WorkEffort", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListRecruitmentProcess service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSaleRecruitmentProcess(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("workEffortTypeId", "RECRUITMENT_PROCESS");
		mapCondition.put("workEffortPurposeTypeId", "RP_SALEEMPL");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("WorkEffort", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListSaleRecruitmentProcess service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	public static Map<String, Object> jqGetListRecruitmentRound(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listGenericValue = null;
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("workEffortIdFrom", (String)context.get("workEffortId"));
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listGenericValue = delegator.findList("WorkEffortAssocToView", tmpConditon, null, null, null, false);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListRecruitmentRound service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listGenericValue", listGenericValue);
		return successResult;
    }
	
	public static Map<String, Object> getEmailParameters(DispatchContext ctx, Map<String, Object> context) {
		//Get Parameters
		String workEffortIdTo = (String)context.get("workEffortIdTo");
		
		Delegator delegator = ctx.getDelegator();
		try {
			//Get WorkEffortFrom And WorkEffortTo
			List<GenericValue> workEffortToList = delegator.findByAnd("WorkEffortAssocToView", UtilMisc.toMap("workEffortIdTo", workEffortIdTo), null, false);
			GenericValue workEffortTo = workEffortToList.get(0);
			
			//Get WorkEffortAttr
			String emplPositionTypeId  = PartyUtil.getWorkEffortAttr(delegator, workEffortTo.getString("workEffortIdFrom"), "emplPositionTypeId", true);
			String ctEmplPositionTypeId  = PartyUtil.getWorkEffortAttr(delegator, workEffortTo.getString("workEffortIdFrom"), "ctEmplPositionTypeId", true);
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			GenericValue ctEmplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", ctEmplPositionTypeId), false);
			String ctMobile  = PartyUtil.getWorkEffortAttr(delegator, workEffortTo.getString("workEffortIdFrom"), "ctMobile", true);
			String ctEmail  = PartyUtil.getWorkEffortAttr(delegator, workEffortTo.getString("workEffortIdFrom"), "ctEmail", true);
			String ctPartyId  = PartyUtil.getWorkEffortAttr(delegator, workEffortTo.getString("workEffortIdFrom"), "ctPartyId", true);
			
			Map<String, Object> returnSuccess = ServiceUtil.returnSuccess();
			Timestamp estimatedStartDate = workEffortTo.getTimestamp("estimatedStartDate");
			Calendar cal = Calendar.getInstance();
			cal.setTime(estimatedStartDate);
			//FIXME
			String fromDate = cal.get(Calendar.HOUR_OF_DAY) + " Giờ " + cal.get(Calendar.MINUTE) + " Phút " + " - Ngày " + cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR); 
			returnSuccess.put("fromDate", fromDate);
			returnSuccess.put("address", workEffortTo.getString("locationDesc"));
			returnSuccess.put("emplPositionTypeId", emplPositionType.getString("description"));
		    String partyName = PartyHelper.getPartyName(delegator, ctPartyId, true);
			String contact = partyName + "-" + ctEmplPositionType.getString("description");
			contact = contact + " / ";
			contact = contact + "Mobile: " + ctMobile + " / ";
			contact = contact + "Email: " + ctEmail;
		    returnSuccess.put("contact", contact);
		    return returnSuccess;
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
    }
}
