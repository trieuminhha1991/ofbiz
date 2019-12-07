package com.olbius.acc.equip;

import com.olbius.acc.utils.ErrorUtils;
import com.olbius.basehr.util.SecurityUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

public class EquipmentEvents {
	public final static String module = EquipmentEvents.class.getName();
	
	public static String createEquipment(HttpServletRequest request, HttpServletResponse response) throws ParseException{
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String unitPriceStr = (String)paramMap.get("unitPrice");
		String quantityStr = (String)paramMap.get("quantity");
		int quantity = Integer.parseInt(quantityStr);
		if(unitPriceStr != null){
			paramMap.put("unitPrice", new BigDecimal(unitPriceStr));
		}
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createEquipment", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("createEquipment", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					return "error";
				}
				String equipmentId = (String)resultService.get("equipmentId");
				String equipmentPartyItemParam = (String)paramMap.get("equipmentPartyItem");
				String equipmentStoreItemParam = (String)paramMap.get("equipmentStoreItem");
				String equipmentPartnerItemParam = (String)paramMap.get("equipmentPartnerItem");
				int quantityUsed = 0;
				if(equipmentPartyItemParam != null){
					JSONArray equipmentPartyItemArr = JSONArray.fromObject(equipmentPartyItemParam);
					Map<String, Object> equipmentPartyItemMap = FastMap.newInstance();
					equipmentPartyItemMap.put("equipmentId", equipmentId);
					equipmentPartyItemMap.put("userLogin", userLogin);
					equipmentPartyItemMap.put("timeZone", timeZone);
					equipmentPartyItemMap.put("locale", locale);
					for(int i = 0; i < equipmentPartyItemArr.size(); i++){
						JSONObject equipmentPartyItemJson = equipmentPartyItemArr.getJSONObject(i);
						int tempQty = Integer.parseInt(equipmentPartyItemJson.getString("quantity"));
						quantityUsed += tempQty;
						equipmentPartyItemMap.put("partyId", equipmentPartyItemJson.get("partyId"));
						equipmentPartyItemMap.put("quantity", tempQty);
						
						Timestamp fromDate = null;
						Timestamp thruDate = null;
						String fromDateStr = equipmentPartyItemJson.getString("fromDate");
						if (equipmentPartyItemJson.containsKey("thruDate")) {
							String thruDateStr = equipmentPartyItemJson.getString("thruDate");
							if (UtilValidate.isNotEmpty(thruDateStr)) {
								thruDate = new Timestamp(Long.parseLong(thruDateStr));
							}
						}
						if (UtilValidate.isNotEmpty(fromDateStr)) {
							fromDate = new Timestamp(Long.parseLong(fromDateStr));
						}
						equipmentPartyItemMap.put("fromDate", fromDate);
						equipmentPartyItemMap.put("thruDate", thruDate);
						
						resultService = dispatcher.runSync("createOrStoreEquipmentParty", equipmentPartyItemMap);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				
				if(equipmentPartnerItemParam != null){
					JSONArray equipmentPartnerItemArr = JSONArray.fromObject(equipmentPartnerItemParam);
					Map<String, Object> equipmentPartnerItemMap = FastMap.newInstance();
					equipmentPartnerItemMap.put("equipmentId", equipmentId);
					equipmentPartnerItemMap.put("userLogin", userLogin);
					equipmentPartnerItemMap.put("timeZone", timeZone);
					equipmentPartnerItemMap.put("locale", locale);
					for(int i = 0; i < equipmentPartnerItemArr.size(); i++){
						JSONObject equipmentPartnerItemJson = equipmentPartnerItemArr.getJSONObject(i);
						int tempQty = Integer.parseInt(equipmentPartnerItemJson.getString("quantity"));
						quantityUsed += tempQty;
						equipmentPartnerItemMap.put("partyId", equipmentPartnerItemJson.get("partyId"));
						equipmentPartnerItemMap.put("quantity", tempQty);
						
						Timestamp fromDate = null;
						Timestamp thruDate = null;
						String fromDateStr = equipmentPartnerItemJson.getString("fromDate");
						if (equipmentPartnerItemJson.containsKey("thruDate")) {
							String thruDateStr = equipmentPartnerItemJson.getString("thruDate");
							if (UtilValidate.isNotEmpty(thruDateStr)) {
								thruDate = new Timestamp(Long.parseLong(thruDateStr));
							}
						}
						if (UtilValidate.isNotEmpty(fromDateStr)) {
							fromDate = new Timestamp(Long.parseLong(fromDateStr));
						}
						equipmentPartnerItemMap.put("fromDate", fromDate);
						equipmentPartnerItemMap.put("thruDate", thruDate);
						
						resultService = dispatcher.runSync("createOrStoreEquipmentParty", equipmentPartnerItemMap);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				
				if(equipmentStoreItemParam != null){
					JSONArray equipmentStoreItemArr = JSONArray.fromObject(equipmentStoreItemParam);
					Map<String, Object> equipmentStoreItemMap = FastMap.newInstance();
					equipmentStoreItemMap.put("equipmentId", equipmentId);
					equipmentStoreItemMap.put("userLogin", userLogin);
					equipmentStoreItemMap.put("timeZone", timeZone);
					equipmentStoreItemMap.put("locale", locale);
					for(int i = 0; i < equipmentStoreItemArr.size(); i++){
						JSONObject equipmentStoreItemJson = equipmentStoreItemArr.getJSONObject(i);
						int tempQty = Integer.parseInt(equipmentStoreItemJson.getString("quantity"));
						Timestamp fromDate = null;
						Timestamp thruDate = null;
						String fromDateStr = equipmentStoreItemJson.getString("fromDate");
						if (equipmentStoreItemJson.containsKey("thruDate")) {
							String thruDateStr = equipmentStoreItemJson.getString("thruDate");
							if (UtilValidate.isNotEmpty(thruDateStr)) {
								thruDate = new Timestamp(Long.parseLong(thruDateStr));
							}
						}
						if (UtilValidate.isNotEmpty(fromDateStr)) {
							fromDate = new Timestamp(Long.parseLong(fromDateStr));
						}
						equipmentStoreItemMap.put("fromDate", fromDate);
						equipmentStoreItemMap.put("thruDate", thruDate);
						
						quantityUsed += tempQty;
						equipmentStoreItemMap.put("productStoreId", equipmentStoreItemJson.get("productStoreId"));
						equipmentStoreItemMap.put("quantity", tempQty);
						
						resultService = dispatcher.runSync("createOrStoreEquipmentProductStore", equipmentStoreItemMap);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				if(quantityUsed != quantity){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "EquipmentQtyMustBeEqualOrgUsedQty", locale));
					TransactionUtil.rollback();
					return "error";
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				return "error";
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				return "error";
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String updateEquipment(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String unitPriceStr = (String)paramMap.get("unitPrice");
		String equipmentId = (String)paramMap.get("equipmentId");
		String quantityStr = (String)paramMap.get("quantity");
		int quantity = Integer.parseInt(quantityStr);
		if (unitPriceStr != null) {
			paramMap.put("unitPrice", new BigDecimal(unitPriceStr));
		}
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateEquipment", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("updateEquipment", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					return "error";
				}
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("equipmentId", equipmentId));
				List<EntityCondition> dateConds = FastList.newInstance();
				dateConds.add(EntityCondition.makeCondition("thruDate", null));
				dateConds.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(System.currentTimeMillis())));
				conds.add(EntityCondition.makeCondition(dateConds, EntityJoinOperator.OR));
				
				/** equipmentParty is existing in DB */ 
				List<GenericValue> equipmentPartyList = delegator.findList("EquipmentParty", EntityCondition.makeCondition(conds), null, null, null, false);
				/** update or create equipmentParty by request */
				List<String> partyIds = FastList.newInstance();/** list contain partyId in request */

				/** equipmentStore is existing in DB */ 
				List<GenericValue> equipmentStoreList = delegator.findList("EquipmentProductStore", EntityCondition.makeCondition(conds), null, null, null, false);
				/** update or create equipmentStore by request */
				List<String> productStoreIds = FastList.newInstance();/** list contain productStoreId in request */
				String equipmentPartyItemParam = (String)paramMap.get("equipmentPartyItem");
				String equipmentPartnerItemParam = (String)paramMap.get("equipmentPartnerItem");
				String equipmentStoreItemParam = (String)paramMap.get("equipmentStoreItem");
				int quantityUsed = 0;
				if(equipmentPartyItemParam != null){
					JSONArray equipmentPartyItemArr = JSONArray.fromObject(equipmentPartyItemParam);
					Map<String, Object> equipmentPartyItemMap = FastMap.newInstance();
					equipmentPartyItemMap.put("equipmentId", equipmentId);
					equipmentPartyItemMap.put("userLogin", userLogin);
					equipmentPartyItemMap.put("timeZone", timeZone);
					equipmentPartyItemMap.put("locale", locale);
					for(int i = 0; i < equipmentPartyItemArr.size(); i++){
						JSONObject equipmentPartyItemJson = equipmentPartyItemArr.getJSONObject(i);
						
						Timestamp fromDate = null;
						Timestamp thruDate = null;
						String fromDateStr = equipmentPartyItemJson.getString("fromDate");
						if (equipmentPartyItemJson.containsKey("thruDate")) {
							String thruDateStr = equipmentPartyItemJson.getString("thruDate");
							if (UtilValidate.isNotEmpty(thruDateStr)) {
								thruDate = new Timestamp(Long.parseLong(thruDateStr));
							}
						}
						if (UtilValidate.isNotEmpty(fromDateStr)) {
							fromDate = new Timestamp(Long.parseLong(fromDateStr));
						}
						equipmentPartyItemMap.put("fromDate", fromDate);
						equipmentPartyItemMap.put("thruDate", thruDate);
						
						int tempQty = Integer.parseInt(equipmentPartyItemJson.getString("quantity"));
						String partyId = equipmentPartyItemJson.getString("partyId"); 
						quantityUsed += tempQty;
						partyIds.add(partyId);
						equipmentPartyItemMap.put("partyId", partyId);
						equipmentPartyItemMap.put("quantity", tempQty);
						resultService = dispatcher.runSync("createOrStoreEquipmentParty", equipmentPartyItemMap);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				
				if(equipmentPartnerItemParam != null){
					JSONArray equipmentPartnerItemArr = JSONArray.fromObject(equipmentPartnerItemParam);
					Map<String, Object> equipmentPartnerItemMap = FastMap.newInstance();
					equipmentPartnerItemMap.put("equipmentId", equipmentId);
					equipmentPartnerItemMap.put("userLogin", userLogin);
					equipmentPartnerItemMap.put("timeZone", timeZone);
					equipmentPartnerItemMap.put("locale", locale);
					for(int i = 0; i < equipmentPartnerItemArr.size(); i++){
						JSONObject equipmentPartyItemJson = equipmentPartnerItemArr.getJSONObject(i);
						
						Timestamp fromDate = null;
						Timestamp thruDate = null;
						String fromDateStr = equipmentPartyItemJson.getString("fromDate");
						if (equipmentPartyItemJson.containsKey("thruDate")) {
							String thruDateStr = equipmentPartyItemJson.getString("thruDate");
							if (UtilValidate.isNotEmpty(thruDateStr)) {
								thruDate = new Timestamp(Long.parseLong(thruDateStr));
							}
						}
						if (UtilValidate.isNotEmpty(fromDateStr)) {
							fromDate = new Timestamp(Long.parseLong(fromDateStr));
						}
						equipmentPartnerItemMap.put("fromDate", fromDate);
						equipmentPartnerItemMap.put("thruDate", thruDate);
						
						int tempQty = Integer.parseInt(equipmentPartyItemJson.getString("quantity"));
						String partyId = equipmentPartyItemJson.getString("partyId"); 
						quantityUsed += tempQty;
						partyIds.add(partyId);
						equipmentPartnerItemMap.put("partyId", partyId);
						equipmentPartnerItemMap.put("quantity", tempQty);
						resultService = dispatcher.runSync("createOrStoreEquipmentParty", equipmentPartnerItemMap);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				
				if(equipmentStoreItemParam != null){
					JSONArray equipmentStoreItemArr = JSONArray.fromObject(equipmentStoreItemParam);
					Map<String, Object> equipmentStoreItemMap = FastMap.newInstance();
					equipmentStoreItemMap.put("equipmentId", equipmentId);
					equipmentStoreItemMap.put("userLogin", userLogin);
					equipmentStoreItemMap.put("timeZone", timeZone);
					equipmentStoreItemMap.put("locale", locale);
					for(int i = 0; i < equipmentStoreItemArr.size(); i++){
						JSONObject equipmentStoreItemJson = equipmentStoreItemArr.getJSONObject(i);
						
						Timestamp fromDate = null;
						Timestamp thruDate = null;
						String fromDateStr = equipmentStoreItemJson.getString("fromDate");
						if (equipmentStoreItemJson.containsKey("thruDate")) {
							String thruDateStr = equipmentStoreItemJson.getString("thruDate");
							if (UtilValidate.isNotEmpty(thruDateStr)) {
								thruDate = new Timestamp(Long.parseLong(thruDateStr));
							}
						}
						if (UtilValidate.isNotEmpty(fromDateStr)) {
							fromDate = new Timestamp(Long.parseLong(fromDateStr));
						}
						equipmentStoreItemMap.put("fromDate", fromDate);
						equipmentStoreItemMap.put("thruDate", thruDate);
						
						int tempQty = Integer.parseInt(equipmentStoreItemJson.getString("quantity"));
						String productStoreId = equipmentStoreItemJson.getString("productStoreId");
						quantityUsed += tempQty;
						productStoreIds.add(productStoreId);
						equipmentStoreItemMap.put("productStoreId", productStoreId);
						equipmentStoreItemMap.put("quantity", tempQty);
						resultService = dispatcher.runSync("createOrStoreEquipmentProductStore", equipmentStoreItemMap);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				if(quantityUsed != quantity){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "EquipmentQtyMustBeEqualOrgUsedQty", locale));
					TransactionUtil.rollback();
					return "error";
				}
				/** delete equipmentParty not in requested equipmentIds list */
				for(GenericValue equipmentParty: equipmentPartyList){
					String partyId = equipmentParty.getString("partyId");
					if(!partyIds.contains(partyId)){
						equipmentParty.set("thruDate", new Timestamp(System.currentTimeMillis()));
						equipmentParty.store();
					}
				}

				/** delete equipmentStore not in requested equipmentIds list */
				for(GenericValue equipmentProductStore: equipmentStoreList){
					String productStoreId = equipmentProductStore.getString("productStoreId");
					if(!productStoreIds.contains(productStoreId)){
						equipmentProductStore.set("thruDate", new Timestamp(System.currentTimeMillis()));
						equipmentProductStore.store();
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				return "error";
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				return "error";
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String getListEquipmentType(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> equipmentTypeList = delegator.findByAnd("EquipmentType", null, UtilMisc.toList("description"), false);
			request.setAttribute("equipmentTypeList", equipmentTypeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	};
	
	public static String createEquipmentIncreaseAndItem(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String dateArisingStr = (String)paramMap.get("dateArising");
		if(dateArisingStr != null){
			Timestamp dateArising = new Timestamp(Long.parseLong(dateArisingStr));
			paramMap.put("dateArising", dateArising);
		}
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createEquipmentIncrease", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("createEquipmentIncrease", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String equipmentIncreaseId = (String)resultService.get("equipmentIncreaseId");
				Map<String, Object> equipmentIncreaseItemMap = FastMap.newInstance();
				equipmentIncreaseItemMap.put("equipmentIncreaseId", equipmentIncreaseId);
				equipmentIncreaseItemMap.put("userLogin", userLogin);
				equipmentIncreaseItemMap.put("timeZone", timeZone);
				equipmentIncreaseItemMap.put("locale", locale);
				
				String equipmentIncreaseItemParam = (String)paramMap.get("equipmentIncreaseItem");
				JSONArray equipmentIncreaseItemArr = JSONArray.fromObject(equipmentIncreaseItemParam);
				for(int i = 0; i < equipmentIncreaseItemArr.size(); i++){
					JSONObject equipmentIncreaseItemJson = equipmentIncreaseItemArr.getJSONObject(i);
					equipmentIncreaseItemMap.put("equipmentId", equipmentIncreaseItemJson.get("equipmentId"));
					equipmentIncreaseItemMap.put("allocationTimes", Integer.parseInt(equipmentIncreaseItemJson.getString("allocationTimes")));
					equipmentIncreaseItemMap.put("debitGlAccountId", equipmentIncreaseItemJson.has("debitGlAccountId")? equipmentIncreaseItemJson.get("debitGlAccountId"): null);
					equipmentIncreaseItemMap.put("costGlAccountId", equipmentIncreaseItemJson.has("costGlAccountId")? equipmentIncreaseItemJson.get("costGlAccountId"): null);
					equipmentIncreaseItemMap.put("depAmount", BigDecimal.valueOf(equipmentIncreaseItemJson.getDouble(("depAmount"))));
					resultService = dispatcher.runSync("createOrStoreEquipmentIncreaseItem", equipmentIncreaseItemMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String updateEquipmentIncreaseAndItem(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String dateArisingStr = (String)paramMap.get("dateArising");
		String equipmentIncreaseId = (String)paramMap.get("equipmentIncreaseId");
		if(dateArisingStr != null){
			Timestamp dateArising = new Timestamp(Long.parseLong(dateArisingStr));
			paramMap.put("dateArising", dateArising);
		}
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateEquipmentIncrease", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("updateEquipmentIncrease", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				Map<String, Object> equipmentIncreaseItemMap = FastMap.newInstance();
				equipmentIncreaseItemMap.put("equipmentIncreaseId", equipmentIncreaseId);
				equipmentIncreaseItemMap.put("userLogin", userLogin);
				equipmentIncreaseItemMap.put("timeZone", timeZone);
				equipmentIncreaseItemMap.put("locale", locale);
				
				String equipmentIncreaseItemParam = (String)paramMap.get("equipmentIncreaseItem");
				JSONArray equipmentIncreaseItemArr = JSONArray.fromObject(equipmentIncreaseItemParam);
				
				/** equipmentItem is existing in DB */ 
				List<GenericValue> equipmentIncreaseItemList = delegator.findList("EquipmentIncreaseItem", EntityCondition.makeCondition("equipmentIncreaseId", equipmentIncreaseId), null, null, null, false);
				/** update or create equipmentItem by request */
				List<String> equipmentIds = FastList.newInstance();/** list contain partyId in request */
				for(int i = 0; i < equipmentIncreaseItemArr.size(); i++){
					JSONObject equipmentIncreaseItemJson = equipmentIncreaseItemArr.getJSONObject(i);
					String equipmentId = equipmentIncreaseItemJson.getString("equipmentId");
					equipmentIds.add(equipmentId);
					equipmentIncreaseItemMap.put("equipmentId", equipmentId);
					equipmentIncreaseItemMap.put("allocationTimes", Integer.parseInt(equipmentIncreaseItemJson.getString("allocationTimes")));
					equipmentIncreaseItemMap.put("debitGlAccountId", equipmentIncreaseItemJson.has("debitGlAccountId")? equipmentIncreaseItemJson.get("debitGlAccountId"): null);
					equipmentIncreaseItemMap.put("costGlAccountId", equipmentIncreaseItemJson.has("costGlAccountId")? equipmentIncreaseItemJson.get("costGlAccountId"): null);
					equipmentIncreaseItemMap.put("depAmount", BigDecimal.valueOf(equipmentIncreaseItemJson.getDouble(("depAmount"))));
					resultService = dispatcher.runSync("createOrStoreEquipmentIncreaseItem", equipmentIncreaseItemMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				delegator.removeAll(EntityUtil.filterByCondition(equipmentIncreaseItemList, EntityCondition.makeCondition("equipmentId", EntityJoinOperator.NOT_IN, equipmentIds)));
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			} 
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String createEquipmentDecreaseAndItem(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String dateArisingStr = (String)paramMap.get("dateArising");
		if(dateArisingStr != null){
			Timestamp dateArising = new Timestamp(Long.parseLong(dateArisingStr));
			paramMap.put("dateArising", dateArising);
		}
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createEquipmentDecrease", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("createEquipmentDecrease", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String equipmentDecreaseId = (String)resultService.get("equipmentDecreaseId");
				Map<String, Object> equipmentDecreaseItemMap = FastMap.newInstance();
				equipmentDecreaseItemMap.put("equipmentDecreaseId", equipmentDecreaseId);
				equipmentDecreaseItemMap.put("userLogin", userLogin);
				equipmentDecreaseItemMap.put("timeZone", timeZone);
				equipmentDecreaseItemMap.put("locale", locale);
				
				String equipmentDecreaseItemParam = (String)paramMap.get("equipmentDecreaseItem");
				JSONArray equipmentDecreaseItemArr = JSONArray.fromObject(equipmentDecreaseItemParam);
				for(int i = 0; i < equipmentDecreaseItemArr.size(); i++){
					JSONObject equipmentDecreaseItemJson = equipmentDecreaseItemArr.getJSONObject(i);
					equipmentDecreaseItemMap.put("equipmentId", equipmentDecreaseItemJson.get("equipmentId"));
					equipmentDecreaseItemMap.put("quantityDecrease", Integer.parseInt(equipmentDecreaseItemJson.getString("quantityDecrease")));
					equipmentDecreaseItemMap.put("quantityInUse", Integer.parseInt(equipmentDecreaseItemJson.getString("quantityInUse")));
					equipmentDecreaseItemMap.put("decreaseReason", equipmentDecreaseItemJson.has("decreaseReason")? equipmentDecreaseItemJson.get("decreaseReason"): null);
					equipmentDecreaseItemMap.put("lossGlAccountId", equipmentDecreaseItemJson.get("lossGlAccountId"));
					equipmentDecreaseItemMap.put("remainValue", new BigDecimal(equipmentDecreaseItemJson.getString("remainValue")));
					resultService = dispatcher.runSync("createOrStoreEquipmentDecreaseItem", equipmentDecreaseItemMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String updateEquipmentDecreaseAndItem(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String dateArisingStr = (String)paramMap.get("dateArising");
		String equipmentDecreaseId = (String)paramMap.get("equipmentDecreaseId");
		if(dateArisingStr != null){
			Timestamp dateArising = new Timestamp(Long.parseLong(dateArisingStr));
			paramMap.put("dateArising", dateArising);
		}
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateEquipmentDecrease", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("updateEquipmentDecrease", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				Map<String, Object> equipmentDecreaseItemMap = FastMap.newInstance();
				equipmentDecreaseItemMap.put("equipmentDecreaseId", equipmentDecreaseId);
				equipmentDecreaseItemMap.put("userLogin", userLogin);
				equipmentDecreaseItemMap.put("timeZone", timeZone);
				equipmentDecreaseItemMap.put("locale", locale);
				/** equipmentItem is existing in DB */ 
				List<GenericValue> equipmentDecreaseItemList = delegator.findList("EquipmentDecreaseItem", EntityCondition.makeCondition("equipmentDecreaseId", equipmentDecreaseId), null, null, null, false);
				/** update or create equipmentItem by request */
				List<String> equipmentIds = FastList.newInstance();/** list contain partyId in request */
				String equipmentDecreaseItemParam = (String)paramMap.get("equipmentDecreaseItem");
				JSONArray equipmentDecreaseItemArr = JSONArray.fromObject(equipmentDecreaseItemParam);
				for(int i = 0; i < equipmentDecreaseItemArr.size(); i++){
					JSONObject equipmentDecreaseItemJson = equipmentDecreaseItemArr.getJSONObject(i);
					String equipmentId = equipmentDecreaseItemJson.getString("equipmentId");
					equipmentIds.add(equipmentId);
					equipmentDecreaseItemMap.put("equipmentId", equipmentId);
					equipmentDecreaseItemMap.put("quantityDecrease", Integer.parseInt(equipmentDecreaseItemJson.getString("quantityDecrease")));
					equipmentDecreaseItemMap.put("quantityInUse", Integer.parseInt(equipmentDecreaseItemJson.getString("quantityInUse")));
					equipmentDecreaseItemMap.put("decreaseReason", equipmentDecreaseItemJson.has("decreaseReason")? equipmentDecreaseItemJson.get("decreaseReason"): null);
					equipmentDecreaseItemMap.put("lossGlAccountId", equipmentDecreaseItemJson.get("lossGlAccountId"));
					equipmentDecreaseItemMap.put("remainValue", new BigDecimal(equipmentDecreaseItemJson.getString("remainValue")));
					resultService = dispatcher.runSync("createOrStoreEquipmentDecreaseItem", equipmentDecreaseItemMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				delegator.removeAll(EntityUtil.filterByCondition(equipmentDecreaseItemList, EntityCondition.makeCondition("equipmentId", EntityJoinOperator.NOT_IN, equipmentIds)));
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			} 
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String getEquipmentIncreaseItem(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String equipmentIncreaseId = request.getParameter("equipmentIncreaseId");
		try {
			List<GenericValue> equipmentIncreaseItemList = delegator.findList("EquipmentIncreaseAndItem", EntityCondition.makeCondition("equipmentIncreaseId", equipmentIncreaseId), null, UtilMisc.toList("equipmentName"), null, false);
			request.setAttribute("equipmentIncreaseItemList", equipmentIncreaseItemList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	public static String getEquipmentDecreaseItem(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String equipmentDecreaseId = request.getParameter("equipmentDecreaseId");
		try {
			List<GenericValue> equipmentDecreaseItemListTmp = delegator.findList("EquipmentDecreaseItemAndEquipment", EntityCondition.makeCondition("equipmentDecreaseId", equipmentDecreaseId), null, UtilMisc.toList("equipmentName"), null, false);
			List<Map<String, Object>> equipmentDecreaseItemList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(equipmentDecreaseItemListTmp)) {
				for (GenericValue item : equipmentDecreaseItemListTmp) {
					Map<String, Object> map = FastMap.newInstance();
					map.putAll(item);
					String equipmentId = item.getString("equipmentId");
					BigDecimal quantityInUse = new BigDecimal(item.getInteger("quantityInUse"));
					BigDecimal quantityDecrease = new BigDecimal(item.getInteger("quantityDecrease"));
					
					//get debitGlAccountId
					List<GenericValue> equipmentIncreaseItemList = delegator.findList("EquipmentIncreaseItem", EntityCondition.makeCondition("equipmentId", equipmentId), null, null, null, false);
					String debitGlAccountId = "";
					if (UtilValidate.isNotEmpty(equipmentIncreaseItemList)) {
						debitGlAccountId = equipmentIncreaseItemList.get(0).getString("debitGlAccountId");
					}
					map.put("debitGlAccountId", debitGlAccountId);
					
					//get allocatedValue
					List<GenericValue> equipmentAllocateAndItemList = delegator.findList("EquipmentAllocateAndItem", EntityCondition.makeCondition("equipmentId", equipmentId), null, UtilMisc.toList("-voucherDate"), null, false);
					BigDecimal allocatedAmount = BigDecimal.ZERO;
					for (GenericValue equipmentAllocate: equipmentAllocateAndItemList) {
						allocatedAmount = allocatedAmount.add(equipmentAllocate.getBigDecimal("allocatedAmount"));
					}
					map.put("allocatedValue", allocatedAmount.multiply(quantityDecrease).divide(quantityInUse, 2, RoundingMode.HALF_UP));
					
					equipmentDecreaseItemList.add(map);
				}
			}
			request.setAttribute("equipmentDecreaseItemList", equipmentDecreaseItemList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}

	public static String getEquipmentPartyAndStoreAndPostedInfo(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String equipmentId = request.getParameter("equipmentId");
		try {
			List<EntityCondition> conds = FastList.newInstance();
			List<EntityCondition> dateConds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("equipmentId", equipmentId));
			dateConds.add(EntityCondition.makeCondition("thruDate", null));
			dateConds.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(System.currentTimeMillis())));
			conds.add(EntityCondition.makeCondition(dateConds, EntityJoinOperator.OR));
			List<GenericValue> equipmentAllPartyList = delegator.findList("EquipmentAndParty", EntityCondition.makeCondition(conds), null, UtilMisc.toList("groupName"), null, false);
			List<GenericValue> equipmentStoreList = delegator.findList("EquipmentAndProductStore", EntityCondition.makeCondition(conds), null, UtilMisc.toList("storeName"), null, false);
			List<GenericValue> equipmentPartnerList = FastList.newInstance();
			List<GenericValue> equipmentPartyList = FastList.newInstance();
			Boolean isPosted = EquipmentUtils.isEquipmentPosted(delegator, equipmentId);
			List<GenericValue> equipmentPartyAndStoreList = FastList.newInstance();
			for(GenericValue partyEntity:equipmentAllPartyList){
				String partyId = partyEntity.getString("partyId");
				if(SecurityUtil.hasRole("PARTNER", partyId, delegator)){
					equipmentPartnerList.add(partyEntity);
				} else {
					equipmentPartyList.add(partyEntity);
				}
			}
			equipmentPartyAndStoreList.addAll(equipmentStoreList);
			equipmentPartyAndStoreList.addAll(equipmentAllPartyList);
			request.setAttribute("equipmentPartyList", equipmentPartyList);
			request.setAttribute("equipmentPartnerList", equipmentPartnerList);
			request.setAttribute("equipmentProductStoreList", equipmentStoreList);
			request.setAttribute("equipmentPartyAndStoreList", equipmentPartyAndStoreList);
			request.setAttribute("isPosted", isPosted);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String getListEquipmentAllocItemGrid(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<String> listSortFields = FastList.newInstance();
		String dateStr = request.getParameter("date");
		String month = request.getParameter("month");
		String year = request.getParameter("year");
		listSortFields.add("equipmentName");
		Timestamp date = new Timestamp(System.currentTimeMillis());
		if(dateStr != null){
			date = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(dateStr)));
		}
		
		List<GenericValue> equipmentUsedList = delegator.findList("EquipmentDecreaseItemSumByDate",
				EntityCondition.makeCondition("dateArising", EntityJoinOperator.LESS_THAN_EQUAL_TO, date),
				UtilMisc.toSet("equipmentId", "quantityDecrease", "quantity"), null, null, false);
		if (UtilValidate.isNotEmpty(equipmentUsedList)) {
			Set<String> equipmentIds = FastSet.newInstance();
			for (GenericValue item : equipmentUsedList) {
				if ((new BigDecimal(item.getInteger("quantityDecrease"))).compareTo(item.getBigDecimal("quantity")) >= 0) {
					equipmentIds.add(item.getString("equipmentId"));
				}
			}
			listAllConditions.add(EntityCondition.makeCondition("equipmentId", EntityJoinOperator.NOT_IN, equipmentIds));
		}
		
		List<GenericValue> equipmentAllocateItemList = delegator.findList("EquipmentAllocateAndItem",
				EntityCondition.makeCondition(EntityCondition.makeCondition("month", Integer.valueOf(month)),
						EntityJoinOperator.AND, EntityCondition.makeCondition("year", Integer.valueOf(year))), null, null, null, false);
		if(UtilValidate.isNotEmpty(equipmentAllocateItemList)){
			List<String> equipmentIds = EntityUtil.getFieldListFromEntityList(equipmentAllocateItemList, "equipmentId", true);
			listAllConditions.add(EntityCondition.makeCondition("equipmentId", EntityJoinOperator.NOT_IN, equipmentIds));
		}			
		
		listAllConditions.add(EntityCondition.makeCondition("dateArising", EntityJoinOperator.LESS_THAN_EQUAL_TO, date));
		
		List<GenericValue> listReturn = delegator.findList("EquipmentAndTypeAndAbilityAllocate",
				EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
		
		Map<String, Object> mapReturn = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(listReturn)) {
			for (GenericValue item : listReturn) {
				String equipmentId = item.getString("equipmentId");
				List<GenericValue> equipmentAllPartyList = delegator.findList("EquipmentAndParty",
						EntityCondition.makeCondition("equipmentId", equipmentId), null, UtilMisc.toList("groupName"), null, false);
				List<GenericValue> equipmentStoreList = delegator.findList("EquipmentAndProductStore",
						EntityCondition.makeCondition("equipmentId", equipmentId), null, UtilMisc.toList("storeName"), null, false);
				List<GenericValue> equipmentPartyAndStoreList = FastList.newInstance();
				equipmentPartyAndStoreList.addAll(equipmentStoreList);
				equipmentPartyAndStoreList.addAll(equipmentAllPartyList);
				mapReturn.put(equipmentId, equipmentPartyAndStoreList);
			}
		}
		
		request.setAttribute("listReturn", listReturn);
		request.setAttribute("mapReturn", mapReturn);
		return "success";
	}
	
	public static String getEquipmentQuantityInUse(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String equipmentDecreaseId = request.getParameter("equipmentDecreaseId");
		String equipmentId = request.getParameter("equipmentId");
		
		try {
			//get debitGlAccountId
			List<GenericValue> equipmentIncreaseItemList = delegator.findList("EquipmentIncreaseItem", EntityCondition.makeCondition("equipmentId", equipmentId), null, null, null, false);
			String debitGlAccountId = "";
			if (UtilValidate.isNotEmpty(equipmentIncreaseItemList)) {
				debitGlAccountId = equipmentIncreaseItemList.get(0).getString("debitGlAccountId");
			}
			request.setAttribute("debitGlAccountId", debitGlAccountId);
			
			//get quantityInUse
			int quantityInUse = 0;
			if (UtilValidate.isNotEmpty(equipmentDecreaseId)) {
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("equipmentId", equipmentId));
				conds.add(EntityCondition.makeCondition("equipmentDecreaseId", EntityJoinOperator.NOT_EQUAL, equipmentDecreaseId));
				
				List<GenericValue> equipmentDecreaseItemList = delegator.findList("EquipmentDecreaseItem", EntityCondition.makeCondition(conds), null, null, null, false);
				GenericValue equipment = delegator.findOne("Equipment", UtilMisc.toMap("equipmentId", equipmentId), false);
				BigDecimal quantityBig = equipment.getBigDecimal("quantity");
				int quantityDecrease = 0;
				for (GenericValue equipmentDecreaseItem: equipmentDecreaseItemList) {
					quantityDecrease += equipmentDecreaseItem.getInteger("quantityDecrease");
				}
				int quantity = quantityBig.intValue();
				quantityInUse = quantity - quantityDecrease;
			} else {
				GenericValue equipmentTotal = delegator.findOne("EquipmentAndDecrTotalAndRemain", UtilMisc.toMap("equipmentId", equipmentId), false);
				if (UtilValidate.isNotEmpty(equipmentTotal)) {
					quantityInUse = equipmentTotal.getBigDecimal("quantityInUse").intValue();
				}
			}
			request.setAttribute("quantityInUse", quantityInUse);
			
			//get allocatedValue
			List<GenericValue> equipmentAllocateAndItemList = delegator.findList("EquipmentAllocateAndItem", EntityCondition.makeCondition("equipmentId", equipmentId), null, UtilMisc.toList("-voucherDate"), null, false);
			BigDecimal allocatedAmount = BigDecimal.ZERO;
			for (GenericValue equipmentAllocate: equipmentAllocateAndItemList) {
				allocatedAmount = allocatedAmount.add(equipmentAllocate.getBigDecimal("allocatedAmount"));
			}
			request.setAttribute("allocatedValue", allocatedAmount);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String createEquipmentAllocateAndItem(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String voucherDateStr = (String)paramMap.get("voucherDate");
		if(voucherDateStr != null){
			paramMap.put("voucherDate", new Timestamp(Long.parseLong(voucherDateStr)));
		}
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createEquipmentAllocate", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("createEquipmentAllocate", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String equipmentAllocateId = (String)resultService.get("equipmentAllocateId");
				String equipmentAllocItemParam = (String)paramMap.get("equipmentAllocItem");
				JSONArray equipmentAllocItemJsonArr = JSONArray.fromObject(equipmentAllocItemParam);
				Map<String, Object> equipmentAllocItemMap = FastMap.newInstance();
				equipmentAllocItemMap.put("equipmentAllocateId", equipmentAllocateId);
				equipmentAllocItemMap.put("userLogin", userLogin);
				equipmentAllocItemMap.put("locale", locale);
				equipmentAllocItemMap.put("timeZone", timeZone);
				for(int i = 0; i < equipmentAllocItemJsonArr.size(); i++){
					JSONObject equipmentAllocItemJson = equipmentAllocItemJsonArr.getJSONObject(i);
					equipmentAllocItemMap.put("equipmentId", equipmentAllocItemJson.get("equipmentId"));
					equipmentAllocItemMap.put("allocatedAmount", new BigDecimal(equipmentAllocItemJson.getString("allocatedAmount")));
					equipmentAllocItemMap.put("allocationAmountUsing", new BigDecimal(equipmentAllocItemJson.getString("allocationAmountUsing")));
					resultService = dispatcher.runSync("createOrStoreEquipmentAllocItem", equipmentAllocItemMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				
				String equipmentAllocItemPartyParam = (String)paramMap.get("equipmentAllocItemParty");
				JSONArray equipmentAllocItemPartyJsonArr = JSONArray.fromObject(equipmentAllocItemPartyParam);
				Map<String, Object> equipmentAllocItemPartyMap = FastMap.newInstance();
				equipmentAllocItemPartyMap.put("equipmentAllocateId", equipmentAllocateId);
				equipmentAllocItemPartyMap.put("userLogin", userLogin);
				equipmentAllocItemPartyMap.put("locale", locale);
				equipmentAllocItemPartyMap.put("timeZone", timeZone);
				Map<String, Double> equipmentAllocItemPercentMap = FastMap.newInstance();
				for(int i = 0; i < equipmentAllocItemPartyJsonArr.size(); i++){
					equipmentAllocItemPartyMap.remove("productStoreId");
					equipmentAllocItemPartyMap.remove("partyId");
					JSONObject equipmentAllocItemJson = equipmentAllocItemPartyJsonArr.getJSONObject(i);
					String equipmentId = equipmentAllocItemJson.getString("equipmentId");
					Double allocatedPercent = Double.parseDouble(equipmentAllocItemJson.getString("allocatedPercent"));
					Double totalPercent = equipmentAllocItemPercentMap.get(equipmentId) != null?(allocatedPercent + equipmentAllocItemPercentMap.get(equipmentId)) : allocatedPercent;
					equipmentAllocItemPercentMap.put(equipmentId, totalPercent);
					equipmentAllocItemPartyMap.put("equipmentId", equipmentId);
					equipmentAllocItemPartyMap.put("allocatedPercent", allocatedPercent);
					String serviceName = null;
					if(equipmentAllocItemJson.has("partyId")){
						equipmentAllocItemPartyMap.put("partyId", equipmentAllocItemJson.get("partyId"));
						serviceName = "createOrStoreEquipmentAllocItemParty";
					}else if(equipmentAllocItemJson.has("productStoreId")){
						equipmentAllocItemPartyMap.put("productStoreId", equipmentAllocItemJson.get("productStoreId"));
						serviceName = "createOrStoreEquipmentAllocItemStore";
					}
					equipmentAllocItemPartyMap.put("costGlAccountId", equipmentAllocItemJson.get("costGlAccountId"));
					equipmentAllocItemPartyMap.put("creditGlAccountId", equipmentAllocItemJson.get("creditGlAccountId"));
					equipmentAllocItemPartyMap.put("debitGlAccountId", equipmentAllocItemJson.get("debitGlAccountId"));
					if(serviceName != null){
						resultService = dispatcher.runSync(serviceName, equipmentAllocItemPartyMap);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				for(Entry<String, Double> entry: equipmentAllocItemPercentMap.entrySet()){
					if(entry.getValue() != 100){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "EquipmentAllocItemPartyNotEqual100", UtilMisc.toMap("equipmentId", entry.getKey()), locale));
						TransactionUtil.rollback();
						return "error";
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String updateEquipmentAllocateAndItemParty(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String equipmentAllocateId = (String)paramMap.get("equipmentAllocateId");
		String updateVoucherNbr = (String)paramMap.get("updateVoucherNbr");
		String voucherDateStr = (String)paramMap.get("voucherDate");
		if(voucherDateStr != null){
			paramMap.put("voucherDate", new Timestamp(Long.parseLong(voucherDateStr)));
		}
		paramMap.put("voucherNbr", updateVoucherNbr);
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateEquipmentAllocate", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("updateEquipmentAllocate", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				List<GenericValue> equipmentAllocItemPartyList = delegator.findByAnd("EquipmentAllocItemParty", UtilMisc.toMap("equipmentAllocateId", equipmentAllocateId), null, false);
				List<GenericValue> equipmentAllocItemStoreList = delegator.findByAnd("EquipmentAllocItemStore", UtilMisc.toMap("equipmentAllocateId", equipmentAllocateId), null, false);
				String equipmentAllocItemPartyParam = (String)paramMap.get("equipmentAllocItemParty");
				JSONArray equipmentAllocItemPartyJsonArr = JSONArray.fromObject(equipmentAllocItemPartyParam);
				Map<String, Object> equipmentAllocItemPartyMap = FastMap.newInstance();
				equipmentAllocItemPartyMap.put("equipmentAllocateId", equipmentAllocateId);
				equipmentAllocItemPartyMap.put("userLogin", userLogin);
				equipmentAllocItemPartyMap.put("locale", locale);
				equipmentAllocItemPartyMap.put("timeZone", timeZone);
				Map<String, Double> equipmentAllocItemPercentMap = FastMap.newInstance();
				List<Map<String, String>> equipAllocItemPartyIds = FastList.newInstance();
				List<Map<String, String>> equipAllocItemProductStoreIds = FastList.newInstance();
				for(int i = 0; i < equipmentAllocItemPartyJsonArr.size(); i++){
					equipmentAllocItemPartyMap.remove("productStoreId");
					equipmentAllocItemPartyMap.remove("partyId");
					JSONObject equipmentAllocItemJson = equipmentAllocItemPartyJsonArr.getJSONObject(i);
					String equipmentId = equipmentAllocItemJson.getString("equipmentId");
					String serviceName = null;
					if(equipmentAllocItemJson.has("partyId")){
						serviceName = "createOrStoreEquipmentAllocItemParty";
						String partyId = equipmentAllocItemJson.getString("partyId");
						Map<String, String> tempMap = FastMap.newInstance();
						tempMap.put("partyId", partyId);
						tempMap.put("equipmentId", equipmentId);
						equipAllocItemPartyIds.add(tempMap);
						equipmentAllocItemPartyMap.put("partyId", partyId);
					}else if(equipmentAllocItemJson.has("productStoreId")){
						serviceName = "createOrStoreEquipmentAllocItemStore";
						String productStoreId = equipmentAllocItemJson.getString("productStoreId");
						Map<String, String> tempMap = FastMap.newInstance();
						tempMap.put("productStoreId", productStoreId);
						tempMap.put("equipmentId", equipmentId);
						equipAllocItemProductStoreIds.add(tempMap);
						equipmentAllocItemPartyMap.put("productStoreId", productStoreId);
					}
					Double allocatedPercent = Double.parseDouble(equipmentAllocItemJson.getString("allocatedPercent"));
					Double totalPercent = equipmentAllocItemPercentMap.get(equipmentId) != null?(allocatedPercent + equipmentAllocItemPercentMap.get(equipmentId)) : allocatedPercent;
					equipmentAllocItemPercentMap.put(equipmentId, totalPercent);
					equipmentAllocItemPartyMap.put("equipmentId", equipmentId);
					equipmentAllocItemPartyMap.put("allocatedPercent", allocatedPercent);
					equipmentAllocItemPartyMap.put("costGlAccountId", equipmentAllocItemJson.getString("costGlAccountId"));
					equipmentAllocItemPartyMap.put("creditGlAccountId", equipmentAllocItemJson.getString("creditGlAccountId"));
					equipmentAllocItemPartyMap.put("debitGlAccountId", equipmentAllocItemJson.getString("debitGlAccountId"));
					if(serviceName != null){
						resultService = dispatcher.runSync(serviceName, equipmentAllocItemPartyMap);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				for(Entry<String, Double> entry: equipmentAllocItemPercentMap.entrySet()){
					if(entry.getValue() != 100){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "EquipmentAllocItemPartyNotEqual100", UtilMisc.toMap("equipmentId", entry.getKey()), locale));
						TransactionUtil.rollback();
						return "error";
					}
				}
				for(GenericValue equipmentAllocItemParty: equipmentAllocItemPartyList){
					boolean delete = true;
					for(Map<String, String> tempMap: equipAllocItemPartyIds){
						if(tempMap.get("partyId").equals(equipmentAllocItemParty.get("partyId"))
								&& tempMap.get("equipmentId").equals(equipmentAllocItemParty.get("equipmentId"))){
							delete = false;
							break;
						}
					}
					if(delete){
						equipmentAllocItemParty.remove();
					}
				}
				for(GenericValue equipmentAllocItemStore: equipmentAllocItemStoreList){
					boolean delete = true;
					for(Map<String, String> tempMap: equipAllocItemProductStoreIds){
						if(tempMap.get("productStoreId").equals(equipmentAllocItemStore.get("productStoreId"))
								&& tempMap.get("equipmentId").equals(equipmentAllocItemStore.get("equipmentId"))){
							delete = false;
							break;
						}
					}
					if(delete){
						equipmentAllocItemStore.remove();
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
			} 
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return "success";
	}
	
	public static String getEquipmentAllocateAndItemAndParty(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String equipmentAllocateId = request.getParameter("equipmentAllocateId");
		try {
			List<GenericValue> equipmentAllocItemPartyList = delegator.findList("EquipmentAllocItemPartyAndDetail", EntityCondition.makeCondition("equipmentAllocateId", equipmentAllocateId), null, UtilMisc.toList("equipmentName"), null, false);
			List<GenericValue> equipmentAllocItemStoreList = delegator.findList("EquipmentAllocItemStoreAndDetail", EntityCondition.makeCondition("equipmentAllocateId", equipmentAllocateId), null, UtilMisc.toList("equipmentName"), null, false);
			List<GenericValue> equipmentAllocItemAndEquipmentList = delegator.findList("EquipmentAllocItemAndEquipment", EntityCondition.makeCondition("equipmentAllocateId", equipmentAllocateId), null, UtilMisc.toList("equipmentName"), null, false);
			equipmentAllocItemPartyList.addAll(equipmentAllocItemStoreList);
			request.setAttribute("equipmentAllocItemStoreAndPartyList", equipmentAllocItemPartyList);
			request.setAttribute("equipmentList", equipmentAllocItemAndEquipmentList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String getEquipmentIdAutoGenerate(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		EntityCondition cond = EntityCondition.makeCondition("equipmentId", EntityJoinOperator.LIKE, String.valueOf(year) + "%");
		try {
			List<GenericValue> equipmentList = delegator.findList("Equipment", cond, null, null, null, false);
			String seqId = String.valueOf(equipmentList.size() + 1);
			if(seqId.length() < 5){
				for(int i = seqId.length(); i < 5; i++){
					seqId = "0" + seqId;
				}
			}
			String equipmentId = String.valueOf(year) + "-" + seqId;
			request.setAttribute("equipmentId", equipmentId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
}
