package com.olbius.basehr.kpiperfreview.events;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.CommonUtil;

public class KeyPerfIndicatorEvents {
	public static String createKeyPerfIndicator(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String isApplAllParty = request.getParameter("isApplAllParty");
		String isApplAllPosType = request.getParameter("isApplAllPosType");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		
		try {
			try {
				TransactionUtil.begin();
				paramMap.put("fromDate", UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr))));
				if(thruDateStr != null){
					paramMap.put("thruDate", UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr))));
				}
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createKeyPerfIndicator", paramMap, userLogin, timeZone, locale);
				Map<String, Object> resultService = dispatcher.runSync("createKeyPerfIndicator", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
				String keyPerfIndicatorId = (String)resultService.get("keyPerfIndicatorId");
				if(!"Y".equals(isApplAllParty)){
					String partyIds = (String)paramMap.get("partyIds");
					JSONArray partyIdJson = JSONArray.fromObject(partyIds);
					partyIdJson = JSONArray.fromObject(CommonUtil.getAllPartyByParent(delegator, partyIdJson.get(0).toString()));
					Map<String, Object> partyApplMap = FastMap.newInstance();
					partyApplMap.put("locale", locale);
					partyApplMap.put("userLogin", userLogin);
					partyApplMap.put("timeZone", timeZone);
					partyApplMap.put("keyPerfIndicatorId", keyPerfIndicatorId);
					for(int i = 0; i < partyIdJson.size(); i++){
						partyApplMap.put("partyId", partyIdJson.get(i));
						resultService = dispatcher.runSync("createKeyPerfIndPartyAppl", partyApplMap);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
							return "error";
						}
					}
				}
				if(!"Y".equals(isApplAllPosType)){
					String emplPositionTypeIds = (String)paramMap.get("emplPositionTypeIds");
					JSONArray emplPositionTypeJson = JSONArray.fromObject(emplPositionTypeIds);
					Map<String, Object> emplPositionTypeMap = FastMap.newInstance();
					emplPositionTypeMap.put("locale", locale);
					emplPositionTypeMap.put("userLogin", userLogin);
					emplPositionTypeMap.put("timeZone", timeZone);
					emplPositionTypeMap.put("keyPerfIndicatorId", keyPerfIndicatorId);
					for(int i = 0; i < emplPositionTypeJson.size(); i++){
						emplPositionTypeMap.put("emplPositionTypeId", emplPositionTypeJson.get(i));
						resultService = dispatcher.runSync("createKeyPerfIndPositionTypeAppl", emplPositionTypeMap);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
							return "error";
						}
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE,UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getKeyPerfIndicatorByPartyInPeriod(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		if(partyId != null && fromDateStr != null && thruDateStr != null){
			Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
			Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDate));
			conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
													EntityJoinOperator.OR,
													EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate)));
			conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isApplAllParty", "Y"),
					EntityJoinOperator.OR,
					EntityCondition.makeCondition("partyId", partyId)));
			try {
				List<GenericValue> listReturn = delegator.findList("KeyPerfIndicatorAndPartyFull", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
				request.setAttribute("listReturn", listReturn);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		return "success";
	}
	public static String createKeyPerfIndPartyTarget(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String fromDateStr = (String)paramMap.get("fromDate");
		String thruDateStr = (String)paramMap.get("thruDate");
		Timestamp fromDate = UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr)));
		Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		paramMap.put("fromDate", fromDate);
		paramMap.put("thruDate", thruDate);
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createKeyPerfIndPartyTarget", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("createKeyPerfIndPartyTarget", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return "error";
				}
				String partyTargetId = (String)resultService.get("partyTargetId");
				String targetItemParam = (String)paramMap.get("targetItem");
				JSONArray targetItemJson = JSONArray.fromObject(targetItemParam);
				context.clear();
				context.put("userLogin", userLogin);
				context.put("partyTargetId", partyTargetId);
				context.put("locale", locale);
				context.put("timeZone", timeZone);
				for(int i = 0; i < targetItemJson.size(); i++){
					JSONObject targetItemObj = targetItemJson.getJSONObject(i);
					context.put("keyPerfIndicatorId", targetItemObj.getString("keyPerfIndicatorId"));
					context.put("target", new BigDecimal(targetItemObj.getString("target")));
					context.put("weight", Double.parseDouble(targetItemObj.getString("weight")));
					context.put("uomId", targetItemObj.getString("uomId"));
					resultService = dispatcher.runSync("createKeyPerfIndPartyTargetItem", context);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	public static String createKPIAllocateTarget(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String parentPartyTargetId = request.getParameter("parentPartyTargetId");
		String partyTargetName = request.getParameter("partyTargetName");
		String partyId = request.getParameter("partyId");
		try {
			GenericValue parentPartyTarget = delegator.findOne("KeyPerfIndPartyTarget", UtilMisc.toMap("partyTargetId", parentPartyTargetId), false);
			if(parentPartyTarget == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "NoTargetToAllocate", locale));
				return "error";
			}
			String partyTargetId = null;
			Map<String, Object> resultService = null;
			List<GenericValue> partyTargetChildList = delegator.findByAnd("KeyPerfIndPartyTarget", UtilMisc.toMap("partyId", partyId, "parentPartyTargetId", parentPartyTargetId), null, false);
			TransactionUtil.begin();
			if(UtilValidate.isNotEmpty(partyTargetChildList)){
				GenericValue partyTargetChild = partyTargetChildList.get(0);
				partyTargetChild.put("partyTargetName", partyTargetName);
				partyTargetChild.store();
				partyTargetId = partyTargetChild.getString("partyTargetId");
			}else{
				Map<String, Object> context = parentPartyTarget.getFields(UtilMisc.toList("periodTypeId", "fromDate", "thruDate", "statusId"));
				context.put("locale", locale);
				context.put("timeZone", timeZone);
				context.put("userLogin", userLogin);
				context.put("partyId", partyId);
				context.put("parentPartyTargetId", parentPartyTargetId);
				context.put("partyTargetName", partyTargetName);
				resultService = dispatcher.runSync("createKeyPerfIndPartyTarget", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				partyTargetId = (String)resultService.get("partyTargetId");
			}
			Map<String, Object> targetItemMap = FastMap.newInstance();
			targetItemMap.put("locale", locale);
			targetItemMap.put("timeZone", timeZone);
			targetItemMap.put("userLogin", userLogin);
			targetItemMap.put("partyTargetId", partyTargetId);
			String keyPerfIndicatorId = request.getParameter("keyPerfIndicatorId");
			String targetStr = request.getParameter("target");
			String weightStr = request.getParameter("weight");
			GenericValue keyPerfIndPartyTargetItem = delegator.findOne("KeyPerfIndPartyTargetItem", UtilMisc.toMap("keyPerfIndicatorId", keyPerfIndicatorId, "partyTargetId", parentPartyTargetId), false);
			targetItemMap.put("keyPerfIndicatorId", keyPerfIndicatorId);
			targetItemMap.put("target", new BigDecimal(targetStr));
			targetItemMap.put("weight", Double.parseDouble(weightStr));
			targetItemMap.put("uomId", keyPerfIndPartyTargetItem.get("uomId"));
			resultService = dispatcher.runSync("createKeyPerfIndPartyTargetItem", targetItemMap);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
				TransactionUtil.rollback();
				return "error";
			}
			
			TransactionUtil.commit();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
}
