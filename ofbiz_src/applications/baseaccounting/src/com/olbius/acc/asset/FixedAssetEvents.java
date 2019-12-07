package com.olbius.acc.asset;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.utils.ErrorUtils;
import com.olbius.services.JqxWidgetSevices;

import java.sql.Timestamp;

import javolution.util.FastList;
import javolution.util.FastMap;

public class FixedAssetEvents {
	
	public final static String module = FixedAssetEvents.class.getName();  
	
	@SuppressWarnings("unchecked")
	public static String addFAToPeriod(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
        boolean beganTransaction = false;
        boolean okay = true;
        String mess = "";
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        try {
			beganTransaction  = TransactionUtil.begin();
			//Get parameters
			List<Map<String, String>> listFixedAssets = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("listFixedAssets"));
			String customTimePeriodId = (String)parameters.get("customTimePeriodId");
			//Create InvoiceItem
			for(Map<String, String> item : listFixedAssets) {
				GenericValue faPeriod = delegator.makeValue("FixedAssetCustomTimePeriod");
				faPeriod.put("customTimePeriodId", customTimePeriodId);
				faPeriod.put("fixedAssetId", item.get("fixedAssetId"));
				faPeriod.put("amount", new BigDecimal(item.get("depreciation")));
				faPeriod.create();
			}
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			mess = e.getMessage();
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			mess = e.getMessage();
		}finally {
			if(!okay) {
				try {
						TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, mess);
						return "error";
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
					return "error";
				}
			}else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
					return "error";
				}
			}
		}
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String addFAToDecrement(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
       
		boolean beganTransaction = false;
        boolean okay = true;
        String mess = "";
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        try {
			beganTransaction  = TransactionUtil.begin();
			//Get parameters
			List<Map<String, String>> listFixedAssets = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("listFixedAssets"));
			String decrementId = (String)parameters.get("decrementId");
			//Create InvoiceItem
			if(UtilValidate.isNotEmpty(listFixedAssets))
			{
			for(Map<String, String> item : listFixedAssets) {
				GenericValue faDecrement = delegator.makeValue("FixedAssetDecrementItem");
				faDecrement.put("decrementId", decrementId);
				faDecrement.put("fixedAssetId", item.get("fixedAssetId"));
				faDecrement.create();
			}
			}else return "success";
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			mess = e.getMessage();
		} catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			mess = e.getMessage();
		}finally {
			if(!okay) {
				try {
						TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, mess);
						return "error";
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
					return "error";
				}
			}else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
					return "error";
				}
			}
		}
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return "success";
	}

	static final String FAM_MANAGER_ROLE = "FAM_MANAGER";
	public static String createPartyFixedAssetAssignment(Map<String, Object> paramMap, GenericValue userLogin, LocalDispatcher dispatcher) throws GenericServiceException {
	    Timestamp fromDate = (Timestamp) paramMap.get("dateAcquired");
        Timestamp allocatedDate = (Timestamp)paramMap.get("dateAcquired");
        String partyId = (String)paramMap.get("partyId");
        String fixedAssetId = (String) paramMap.get("fixedAssetId");
        dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", FAM_MANAGER_ROLE, "userLogin", userLogin));
        Map<String, Object> context = FastMap.newInstance();
        context.put("partyId", partyId);
        context.put("roleTypeId", FAM_MANAGER_ROLE);
        context.put("fixedAssetId", fixedAssetId);
        context.put("fromDate", fromDate);
        context.put("allocatedDate", allocatedDate);
        context.put("statusId", "FA_USING");
        context.put("userLogin", userLogin);
        dispatcher.runSync("createPartyFixedAssetAssignment", context);
        return "";
    }
	
	public static String createPartyFixedAssetAssignment(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String fromDateStr = (String)paramMap.get("fromDate");
		String thruDateStr = (String)paramMap.get("thruDate");
		String allocatedDateStr = (String)paramMap.get("allocatedDate");
		String partyId = (String)paramMap.get("partyId");
		String roleTypeId = (String)paramMap.get("roleTypeId");
		paramMap.put("fromDate", new Timestamp(Long.parseLong(fromDateStr)));
		paramMap.put("allocatedDate", new Timestamp(Long.parseLong(allocatedDateStr)));
		if(thruDateStr != null){
			paramMap.put("thruDate", new Timestamp(Long.parseLong(thruDateStr)));
		}
		try {
			TransactionUtil.begin();
			try {
				Map<String, Object> resultService = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId, "userLogin", userLogin));
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					return "error";
				}
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPartyFixedAssetAssignment", paramMap, userLogin, timeZone, locale);
				resultService = dispatcher.runSync("createPartyFixedAssetAssignment", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
			} catch (GenericServiceException e) {
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
				e.printStackTrace();
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success"; 
	}
	
	public static String createFixedAssetAndDep(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String dateOfIncreaseStr = (String)paramMap.get("dateOfIncrease");
		String dateAcquiredStr = (String)paramMap.get("dateAcquired");
		String datePurchaseStr = (String)paramMap.get("datePurchase");
		String purchaseCostStr = (String)paramMap.get("purchaseCost");
		String lifeDepAmountStr = (String) paramMap.get("lifeDepAmount");
		String remainingValueStr = (String)paramMap.get("remainingValue");
		String monthlyDepRateStr = (String)paramMap.get("monthlyDepRate");
		String yearlyDepRateStr = (String)paramMap.get("yearlyDepRate");
		String monthlyDepAmountStr = (String)paramMap.get("monthlyDepAmount");
		String yearlyDepAmountStr = (String)paramMap.get("yearlyDepAmount");
		String accumulatedDepStr = (String)paramMap.get("accumulatedDep");
		String quantityStr = (String)paramMap.get("quantity");
		String yearMadeStr = (String)paramMap.get("yearMade");
		String listAccompanyComponents = (String)paramMap.get("listAccompanyComponents");
		String receiptDateStr = (String)paramMap.get("receiptDate");
		if(dateAcquiredStr != null){
			Timestamp dateAcquired = new Timestamp(Long.parseLong(dateAcquiredStr));
			paramMap.put("dateAcquired", dateAcquired);
		}
		if(datePurchaseStr != null){
			Timestamp datePurchase = new Timestamp(Long.parseLong(datePurchaseStr));
			paramMap.put("datePurchase", datePurchase);
		}
		if(dateOfIncreaseStr != null){
			Timestamp dateOfIncrease = new Timestamp(Long.parseLong(dateOfIncreaseStr));
			paramMap.put("dateOfIncrease", dateOfIncrease);
		}
		if(purchaseCostStr != null){
			BigDecimal purchaseCost = new BigDecimal(purchaseCostStr);
			paramMap.put("purchaseCost", purchaseCost);
		}
		if(lifeDepAmountStr != null){
			BigDecimal lifeDepAmount = new BigDecimal(lifeDepAmountStr);
			paramMap.put("lifeDepAmount", lifeDepAmount);
		}
		if(remainingValueStr != null){
			BigDecimal remainingValue = new BigDecimal(remainingValueStr);
			paramMap.put("remainingValue", remainingValue);
		}
		if(monthlyDepRateStr != null){
			BigDecimal monthlyDepRate = new BigDecimal(monthlyDepRateStr);
			paramMap.put("monthlyDepRate", monthlyDepRate);
		}
		if(yearlyDepRateStr != null){
			BigDecimal yearlyDepRate = new BigDecimal(yearlyDepRateStr);
			paramMap.put("yearlyDepRate", yearlyDepRate);
		}
		if(monthlyDepAmountStr != null){
			BigDecimal monthlyDepAmount = new BigDecimal(monthlyDepAmountStr);
			paramMap.put("monthlyDepAmount", monthlyDepAmount);
		}
		if(yearlyDepAmountStr != null){
			BigDecimal yearlyDepAmount = new BigDecimal(yearlyDepAmountStr);
			paramMap.put("yearlyDepAmount", yearlyDepAmount);
		}
		if(accumulatedDepStr != null){
			BigDecimal accumulatedDep = new BigDecimal(accumulatedDepStr);
			paramMap.put("accumulatedDep", accumulatedDep);
		}
		if(quantityStr != null){
			paramMap.put("quantity", Long.parseLong(quantityStr));
		}
		if(yearMadeStr != null){
			paramMap.put("yearMade", Integer.parseInt(yearMadeStr));
		}
		if(receiptDateStr != null){
			Timestamp receiptDate = new Timestamp(Long.parseLong(receiptDateStr));
			paramMap.put("receiptDate", receiptDate);
		}
		try {
			TransactionUtil.begin();
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createFixedAsset", paramMap, userLogin, timeZone, locale);
				Map<String, Object> resultService = dispatcher.runSync("createFixedAsset", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String fixedAssetId = (String)resultService.get("fixedAssetId");
				context = ServiceUtil.setServiceFields(dispatcher, "createFixedAssetDep", paramMap, userLogin, timeZone, locale);
				context.put("fixedAssetId", fixedAssetId);
				resultService = dispatcher.runSync("createFixedAssetDep", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				Map<String, Object> partyFixedAssetCtx = FastMap.newInstance();
				partyFixedAssetCtx.putAll(paramMap);
				partyFixedAssetCtx.put("fixedAssetId", fixedAssetId);
				createPartyFixedAssetAssignment(partyFixedAssetCtx, userLogin, dispatcher);

				if(listAccompanyComponents != null){
					JSONArray accompanyComponentsArr = JSONArray.fromObject(listAccompanyComponents);
					Map<String, Object> fixedAssetAccompanyCtx = FastMap.newInstance();
					fixedAssetAccompanyCtx.put("userLogin", userLogin);
					fixedAssetAccompanyCtx.put("fixedAssetId", fixedAssetId);
					fixedAssetAccompanyCtx.put("locale", locale);
					fixedAssetAccompanyCtx.put("timeZone", timeZone);
					for(int i = 0; i < accompanyComponentsArr.size(); i++){
						JSONObject accompanyComponentJson = accompanyComponentsArr.getJSONObject(i);
						fixedAssetAccompanyCtx.put("componentName", accompanyComponentJson.get("componentName"));
						fixedAssetAccompanyCtx.put("unit", accompanyComponentJson.has("unit")? accompanyComponentJson.get("unit") : null);
						if(accompanyComponentJson.has("value")){
							String valueStr = accompanyComponentJson.getString("value");
							fixedAssetAccompanyCtx.put("value", new BigDecimal(valueStr));
						}else{
							fixedAssetAccompanyCtx.put("value", null);
						}
						if(accompanyComponentJson.has("quantity")){
							String quantityAccompamnyStr = accompanyComponentJson.getString("quantity");
							fixedAssetAccompanyCtx.put("quantity", Long.parseLong(quantityAccompamnyStr));
						}else{
							fixedAssetAccompanyCtx.put("quantity", null);
						}
						resultService = dispatcher.runSync("createFixedAssetAccompany", fixedAssetAccompanyCtx);
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
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
			}
		} catch (GenericTransactionException e1) {
			e1.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e1.getMessage());
		}
		return "success";
	}
	
	public static String createFixedAssetIncrease(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String dateArisingStr = (String)paramMap.get("dateArising");
		String dueDateStr = (String)paramMap.get("dueDate");
		if(dateArisingStr != null){
			Timestamp dateArising = new Timestamp(Long.parseLong(dateArisingStr));
			paramMap.put("dateArising", dateArising);
		}
		if(dueDateStr != null){
			Timestamp dueDate = new Timestamp(Long.parseLong(dueDateStr));
			paramMap.put("dueDate", dueDate);
		}
		try {
			TransactionUtil.begin();
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createFixedAssetIncrease", paramMap, userLogin, timeZone, locale);
				Map<String, Object> resultService = dispatcher.runSync("createFixedAssetIncrease", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String fixedAssetIncreaseId = (String)resultService.get("fixedAssetIncreaseId");
				String fixedAssetIncreaseItemParam = (String)paramMap.get("fixedAssetIncreaseItem");
				JSONArray fixedAssetIncreaseItemArr = JSONArray.fromObject(fixedAssetIncreaseItemParam);
				Map<String, Object> fixedAssetIncreaseItemMap = FastMap.newInstance();
				fixedAssetIncreaseItemMap.put("fixedAssetIncreaseId", fixedAssetIncreaseId);
				fixedAssetIncreaseItemMap.put("userLogin", userLogin);
				fixedAssetIncreaseItemMap.put("locale", locale);
				fixedAssetIncreaseItemMap.put("timeZone", timeZone);
				for(int i = 0; i < fixedAssetIncreaseItemArr.size(); i++){
					JSONObject fixedAssetIncreaseItemJson = fixedAssetIncreaseItemArr.getJSONObject(i);
					fixedAssetIncreaseItemMap.put("fixedAssetId", fixedAssetIncreaseItemJson.get("fixedAssetId"));
					fixedAssetIncreaseItemMap.put("debitGlAccountId", fixedAssetIncreaseItemJson.get("debitGlAccount"));
					fixedAssetIncreaseItemMap.put("creditGlAccountId", fixedAssetIncreaseItemJson.get("creditGlAccount"));
					resultService = dispatcher.runSync("createOrStoreFixedAssetIncreaseItem", fixedAssetIncreaseItemMap);
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
		} catch (GenericTransactionException e1) {
			e1.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e1.getMessage());
		}
		return "success";
	}
	
	public static String editFixedAssetIncrease(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String dateArisingStr = (String)paramMap.get("dateArising");
		String dueDateStr = (String)paramMap.get("dueDate");
		String fixedAssetIncreaseId = (String)paramMap.get("fixedAssetIncreaseId");
		if(dateArisingStr != null){
			Timestamp dateArising = new Timestamp(Long.parseLong(dateArisingStr));
			paramMap.put("dateArising", dateArising);
		}
		if(dueDateStr != null){
			Timestamp dueDate = new Timestamp(Long.parseLong(dueDateStr));
			paramMap.put("dueDate", dueDate);
		}
		try {
			TransactionUtil.begin();
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateFixedAssetIncrease", paramMap, userLogin, timeZone, locale);
				Map<String, Object> resultService = dispatcher.runSync("updateFixedAssetIncrease", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				/** fixedAssetIncreaseItem is existing in DB */ 
				List<GenericValue> fixedAssetIncreaseItemList = delegator.findList("FixedAssetIncreaseItem", EntityCondition.makeCondition("fixedAssetIncreaseId", fixedAssetIncreaseId), null, null, null, false);
				/** update or create fixedAssetIncreaseItem by request */
				List<String> fixedAssetIds = FastList.newInstance();/** list contain fixedAssetId in request */
				String fixedAssetIncreaseItemParam = (String)paramMap.get("fixedAssetIncreaseItem");
				JSONArray fixedAssetIncreaseItemArr = JSONArray.fromObject(fixedAssetIncreaseItemParam);
				Map<String, Object> fixedAssetIncreaseItemMap = FastMap.newInstance();
				fixedAssetIncreaseItemMap.put("fixedAssetIncreaseId", fixedAssetIncreaseId);
				fixedAssetIncreaseItemMap.put("userLogin", userLogin);
				fixedAssetIncreaseItemMap.put("locale", locale);
				fixedAssetIncreaseItemMap.put("timeZone", timeZone);
				for(int i = 0; i < fixedAssetIncreaseItemArr.size(); i++){
					JSONObject fixedAssetIncreaseItemJson = fixedAssetIncreaseItemArr.getJSONObject(i);
					String fixedAssetId = fixedAssetIncreaseItemJson.getString("fixedAssetId");
					fixedAssetIds.add(fixedAssetId);
					fixedAssetIncreaseItemMap.put("fixedAssetId", fixedAssetId);
					fixedAssetIncreaseItemMap.put("debitGlAccountId", fixedAssetIncreaseItemJson.get("debitGlAccount"));
					fixedAssetIncreaseItemMap.put("creditGlAccountId", fixedAssetIncreaseItemJson.get("creditGlAccount"));
					resultService = dispatcher.runSync("createOrStoreFixedAssetIncreaseItem", fixedAssetIncreaseItemMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				
				/** delete fixedAssetItemIncrease not in requested fixedAssetIds list */
				for(GenericValue fixedAssetItem: fixedAssetIncreaseItemList){
					String fixedAssetId = fixedAssetItem.getString("fixedAssetId");
					if(!fixedAssetIds.contains(fixedAssetId)){
						fixedAssetItem.remove();
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String createFixedAssetDecreaseAndItem(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String voucherDateStr = (String)paramMap.get("voucherDate");
		if(voucherDateStr != null){
			Timestamp voucherDate = new Timestamp(Long.parseLong(voucherDateStr));
			paramMap.put("voucherDate", voucherDate);
		}
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createFixedAssetDecrease", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("createFixedAssetDecrease", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String fixedAssetDecreaseId = (String)resultService.get("fixedAssetDecreaseId");
				Map<String, Object> fixedAssetDecreaseItemMap = FastMap.newInstance();
				fixedAssetDecreaseItemMap.put("fixedAssetDecreaseId", fixedAssetDecreaseId);
				fixedAssetDecreaseItemMap.put("userLogin", userLogin);
				fixedAssetDecreaseItemMap.put("timeZone", timeZone);
				fixedAssetDecreaseItemMap.put("locale", locale);
				String fixedAssetItemParam = (String)paramMap.get("fixedAssetItem");
				JSONArray fixedAssetItemArr = JSONArray.fromObject(fixedAssetItemParam);
				for(int i = 0; i < fixedAssetItemArr.size(); i++){
					JSONObject fixedAssetItemJson = fixedAssetItemArr.getJSONObject(i);
					fixedAssetDecreaseItemMap.put("fixedAssetId", fixedAssetItemJson.get("fixedAssetId"));
					fixedAssetDecreaseItemMap.put("depreciationGlAccount", fixedAssetItemJson.has("depreciationGlAccount")? fixedAssetItemJson.get("depreciationGlAccount"): null);
					fixedAssetDecreaseItemMap.put("remainValueGlAccount", fixedAssetItemJson.has("remainValueGlAccount")? fixedAssetItemJson.get("remainValueGlAccount"): null);
					if(fixedAssetItemJson.has("remainValue")){
						String remainValueStr = fixedAssetItemJson.getString("remainValue");
						fixedAssetDecreaseItemMap.put("remainValue", new BigDecimal(remainValueStr));
					}else{
						fixedAssetDecreaseItemMap.put("remainValue", null);
					}
					if(fixedAssetItemJson.has("accumulatedDepreciation")){
						String accumulatedDepreciationStr = fixedAssetItemJson.getString("accumulatedDepreciation");
						fixedAssetDecreaseItemMap.put("accumulatedDepreciation", new BigDecimal(accumulatedDepreciationStr));
					}else{
						fixedAssetDecreaseItemMap.put("accumulatedDepreciation", null);
					}
					resultService = dispatcher.runSync("createOrStoreFixedAssetDecreaseItem", fixedAssetDecreaseItemMap);
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
				return "error";
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
				return "error";
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String editFixedAssetDecreaseAndItem(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String voucherDateStr = (String)paramMap.get("voucherDate");
		String fixedAssetDecreaseId = (String)paramMap.get("fixedAssetDecreaseId");
		if(voucherDateStr != null){
			Timestamp voucherDate = new Timestamp(Long.parseLong(voucherDateStr));
			paramMap.put("voucherDate", voucherDate);
		}
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateFixedAssetDecrease", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("updateFixedAssetDecrease", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				/** fixedAssetDecreaseItem is existing in DB */ 
				List<GenericValue> fixedAssetDecreaseItemList = delegator.findList("FixedAssetDecreaseItem", EntityCondition.makeCondition("fixedAssetDecreaseId", fixedAssetDecreaseId), null, null, null, false);
				/** update or create fixedAssetDecreaseItem by request */
				List<String> fixedAssetIds = FastList.newInstance();/** list contain fixedAssetId in request */  
				Map<String, Object> fixedAssetDecreaseItemMap = FastMap.newInstance();
				fixedAssetDecreaseItemMap.put("fixedAssetDecreaseId", fixedAssetDecreaseId);
				fixedAssetDecreaseItemMap.put("userLogin", userLogin);
				fixedAssetDecreaseItemMap.put("timeZone", timeZone);
				fixedAssetDecreaseItemMap.put("locale", locale);
				String fixedAssetItemParam = (String)paramMap.get("fixedAssetItem");
				JSONArray fixedAssetItemArr = JSONArray.fromObject(fixedAssetItemParam);
				for(int i = 0; i < fixedAssetItemArr.size(); i++){
					JSONObject fixedAssetItemJson = fixedAssetItemArr.getJSONObject(i);
					String fixedAssetId = fixedAssetItemJson.getString("fixedAssetId");
					fixedAssetIds.add(fixedAssetId);
					fixedAssetDecreaseItemMap.put("fixedAssetId", fixedAssetId);
					fixedAssetDecreaseItemMap.put("depreciationGlAccount", fixedAssetItemJson.has("depreciationGlAccount")? fixedAssetItemJson.get("depreciationGlAccount"): null);
					fixedAssetDecreaseItemMap.put("remainValueGlAccount", fixedAssetItemJson.has("remainValueGlAccount")? fixedAssetItemJson.get("remainValueGlAccount"): null);
					if(fixedAssetItemJson.has("remainValue")){
						String remainValueStr = fixedAssetItemJson.getString("remainValue");
						fixedAssetDecreaseItemMap.put("remainValue", new BigDecimal(remainValueStr));
					}else{
						fixedAssetDecreaseItemMap.put("remainValue", null);
					}
					if(fixedAssetItemJson.has("accumulatedDepreciation")){
						String accumulatedDepreciationStr = fixedAssetItemJson.getString("accumulatedDepreciation");
						fixedAssetDecreaseItemMap.put("accumulatedDepreciation", new BigDecimal(accumulatedDepreciationStr));
					}else{
						fixedAssetDecreaseItemMap.put("accumulatedDepreciation", null);
					}
					resultService = dispatcher.runSync("createOrStoreFixedAssetDecreaseItem", fixedAssetDecreaseItemMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				/** delete fixedAssetItemDecrease not in requested fixedAssetIds list */
				for(GenericValue fixedAssetItem: fixedAssetDecreaseItemList){
					String fixedAssetId = fixedAssetItem.getString("fixedAssetId");
					if(!fixedAssetIds.contains(fixedAssetId)){
						fixedAssetItem.remove();
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
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
				TransactionUtil.rollback();
				return "error";
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
				return "error";
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String createFixedAssetDepreciationCalcAndItem(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String voucherDateStr = (String)paramMap.get("voucherDate");
		if(voucherDateStr != null){
			Timestamp voucherDate = new Timestamp(Long.parseLong(voucherDateStr));
			paramMap.put("voucherDate", voucherDate);
		}
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createFixedAssetDepreciationCalc", paramMap, userLogin, timeZone, locale);
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("createFixedAssetDepreciationCalc", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				String depreciationCalcId = (String)resultService.get("depreciationCalcId");
				Map<String, Object> fixedAssetDepreCalcItemMap = FastMap.newInstance();
				fixedAssetDepreCalcItemMap.put("depreciationCalcId", depreciationCalcId);
				fixedAssetDepreCalcItemMap.put("userLogin", userLogin);
				fixedAssetDepreCalcItemMap.put("timeZone", timeZone);
				fixedAssetDepreCalcItemMap.put("locale", locale);
				String fixedAssetItemParam = (String)paramMap.get("fixedAssetItem");
				JSONArray fixedAssetItemArr = JSONArray.fromObject(fixedAssetItemParam);
				for(int i = 0; i < fixedAssetItemArr.size(); i++){
					JSONObject fixedAssetItemJson = fixedAssetItemArr.getJSONObject(i);
					fixedAssetDepreCalcItemMap.put("fixedAssetId", fixedAssetItemJson.get("fixedAssetId"));
					fixedAssetDepreCalcItemMap.put("debitGlAccountId", fixedAssetItemJson.get("debitGlAccountId"));
					fixedAssetDepreCalcItemMap.put("creditGlAccountId", fixedAssetItemJson.get("creditGlAccountId"));
					String depreciationAmountStr = fixedAssetItemJson.getString("depreciationAmount");
					BigDecimal depreciationAmount = new BigDecimal(depreciationAmountStr);
					fixedAssetDepreCalcItemMap.put("depreciationAmount", depreciationAmount);
					resultService = dispatcher.runSync("createOrStoreFixedAssetDepreCalcItem", fixedAssetDepreCalcItemMap);
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
				TransactionUtil.rollback();
				return "error";
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
				return "error";
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String editFixedAssetDepreciationCalcAndItem(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String voucherDateStr = (String)paramMap.get("voucherDate");
		String depreciationCalcId = (String)paramMap.get("depreciationCalcId");
		if(voucherDateStr != null){
			Timestamp voucherDate = new Timestamp(Long.parseLong(voucherDateStr));
			paramMap.put("voucherDate", voucherDate);
		}
		try {
			try {
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateFixedAssetDepreciationCalc", paramMap, userLogin, timeZone, locale);
				/** fixedAssetDecreaseItem is existing in DB */ 
				List<GenericValue> fixedAssetDepreCalcItemList = delegator.findList("FixedAssetDepreCalcItem", EntityCondition.makeCondition("depreciationCalcId", depreciationCalcId), null, null, null, false);
				/** update or create fixedAssetDecreaseItem by request */
				List<String> fixedAssetIds = FastList.newInstance();/** list contain fixedAssetId in request */  
				TransactionUtil.begin();
				Map<String, Object> resultService = dispatcher.runSync("updateFixedAssetDepreciationCalc", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					TransactionUtil.rollback();
					return "error";
				}
				Map<String, Object> fixedAssetDepreCalcItemMap = FastMap.newInstance();
				fixedAssetDepreCalcItemMap.put("depreciationCalcId", depreciationCalcId);
				fixedAssetDepreCalcItemMap.put("userLogin", userLogin);
				fixedAssetDepreCalcItemMap.put("timeZone", timeZone);
				fixedAssetDepreCalcItemMap.put("locale", locale);
				String fixedAssetItemParam = (String)paramMap.get("fixedAssetItem");
				JSONArray fixedAssetItemArr = JSONArray.fromObject(fixedAssetItemParam);
				for(int i = 0; i < fixedAssetItemArr.size(); i++){
					JSONObject fixedAssetItemJson = fixedAssetItemArr.getJSONObject(i);
					String fixedAssetId = fixedAssetItemJson.getString("fixedAssetId");
					fixedAssetIds.add(fixedAssetId);
					fixedAssetDepreCalcItemMap.put("fixedAssetId", fixedAssetId);
					fixedAssetDepreCalcItemMap.put("debitGlAccountId", fixedAssetItemJson.get("debitGlAccountId"));
					fixedAssetDepreCalcItemMap.put("creditGlAccountId", fixedAssetItemJson.get("creditGlAccountId"));
					String depreciationAmountStr = fixedAssetItemJson.getString("depreciationAmount");
					BigDecimal depreciationAmount = new BigDecimal(depreciationAmountStr);
					fixedAssetDepreCalcItemMap.put("depreciationAmount", depreciationAmount);
					resultService = dispatcher.runSync("createOrStoreFixedAssetDepreCalcItem", fixedAssetDepreCalcItemMap);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
				/** delete fixedAssetDepreCalcItemList not in requested fixedAssetIds list */
				for(GenericValue fixedAssetItem: fixedAssetDepreCalcItemList){
					String fixedAssetId = fixedAssetItem.getString("fixedAssetId");
					if(!fixedAssetIds.contains(fixedAssetId)){
						fixedAssetItem.remove();
					}
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
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
	
	public static String getFixedAssetDecrReasonType(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			List<GenericValue> fixedAssetDecrReasonTypeList = delegator.findList("FixedAssetDecrReasonType", null, null, UtilMisc.toList("description"), null, false);
			request.setAttribute("fixedAssetDecrReasonTypeList", fixedAssetDecrReasonTypeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String getFixedAssetDecreaseItem(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String fixedAssetDecreaseId = request.getParameter("fixedAssetDecreaseId");
		try {
			List<GenericValue> fixedAssetDecreaseItemList = delegator.findList("FixedAssetDecreaseItemAndFA", EntityCondition.makeCondition("fixedAssetDecreaseId", fixedAssetDecreaseId), null, UtilMisc.toList("fixedAssetId"), null, false);
			request.setAttribute("fixedAssetDecreaseItemList", fixedAssetDecreaseItemList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	public static String getFixedAssetIncreaseItem(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String fixedAssetIncreaseId = request.getParameter("fixedAssetIncreaseId");
		try {
			List<GenericValue> fixedAssetIncreaseItemList = delegator.findList("FixedAssetIncreaseItemAndFA", EntityCondition.makeCondition("fixedAssetIncreaseId", fixedAssetIncreaseId), null, UtilMisc.toList("fixedAssetId"), null, false);
			request.setAttribute("fixedAssetIncreaseItemList", fixedAssetIncreaseItemList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String getFixedAssetDepreCalcItem(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String depreciationCalcId = request.getParameter("depreciationCalcId");
		try {
			List<GenericValue> fixedAssetDepreCalcItemList = delegator.findList("FixedAssetDepreCalcItemAndFA", EntityCondition.makeCondition("depreciationCalcId", depreciationCalcId), null, UtilMisc.toList("fixedAssetId"), null, false);
			request.setAttribute("fixedAssetDepreCalcItemList", fixedAssetDepreCalcItemList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String getFixedAssetGeneralInfo(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String fixedAssetId = request.getParameter("fixedAssetId");
		try {
			List<GenericValue> fixedAsset = delegator.findByAnd("FixedAssetAndDetail", UtilMisc.toMap("fixedAssetId", fixedAssetId), null, false);
			if(UtilValidate.isEmpty(fixedAsset)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, "Cannot find fixed asset to edit");
				return "error";
			}
			request.setAttribute("fixedAsset", fixedAsset.get(0));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String updateFixedAssetDepreciation(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String dateOfIncreaseStr = (String)paramMap.get("dateOfIncrease");
		String dateAcquiredStr = (String)paramMap.get("dateAcquired");
		String datePurchaseStr = (String)paramMap.get("datePurchase");
		String purchaseCostStr = (String)paramMap.get("purchaseCost");
		String lifeDepAmountStr = (String) paramMap.get("lifeDepAmount");
		String remainingValueStr = (String)paramMap.get("remainingValue");
		String monthlyDepRateStr = (String)paramMap.get("monthlyDepRate");
		String yearlyDepRateStr = (String)paramMap.get("yearlyDepRate");
		String monthlyDepAmountStr = (String)paramMap.get("monthlyDepAmount");
		String yearlyDepAmountStr = (String)paramMap.get("yearlyDepAmount");
		String accumulatedDepStr = (String)paramMap.get("accumulatedDep");
		if(dateAcquiredStr != null){
			Timestamp dateAcquired = new Timestamp(Long.parseLong(dateAcquiredStr));
			paramMap.put("dateAcquired", dateAcquired);
		}
		if(datePurchaseStr != null){
			Timestamp datePurchase = new Timestamp(Long.parseLong(datePurchaseStr));
			paramMap.put("datePurchase", datePurchase);
		}
		if(dateOfIncreaseStr != null){
			Timestamp dateOfIncrease = new Timestamp(Long.parseLong(dateOfIncreaseStr));
			paramMap.put("dateOfIncrease", dateOfIncrease);
		}
		if(purchaseCostStr != null){
			BigDecimal purchaseCost = new BigDecimal(purchaseCostStr);
			paramMap.put("purchaseCost", purchaseCost);
		}
		if(lifeDepAmountStr != null){
			BigDecimal lifeDepAmount = new BigDecimal(lifeDepAmountStr);
			paramMap.put("lifeDepAmount", lifeDepAmount);
		}
		if(remainingValueStr != null){
			BigDecimal remainingValue = new BigDecimal(remainingValueStr);
			paramMap.put("remainingValue", remainingValue);
		}
		if(monthlyDepRateStr != null){
			BigDecimal monthlyDepRate = new BigDecimal(monthlyDepRateStr);
			paramMap.put("monthlyDepRate", monthlyDepRate);
		}
		if(yearlyDepRateStr != null){
			BigDecimal yearlyDepRate = new BigDecimal(yearlyDepRateStr);
			paramMap.put("yearlyDepRate", yearlyDepRate);
		}
		if(monthlyDepAmountStr != null){
			BigDecimal monthlyDepAmount = new BigDecimal(monthlyDepAmountStr);
			paramMap.put("monthlyDepAmount", monthlyDepAmount);
		}
		if(yearlyDepAmountStr != null){
			BigDecimal yearlyDepAmount = new BigDecimal(yearlyDepAmountStr);
			paramMap.put("yearlyDepAmount", yearlyDepAmount);
		}
		if(accumulatedDepStr != null){
			BigDecimal accumulatedDep = new BigDecimal(accumulatedDepStr);
			paramMap.put("accumulatedDep", accumulatedDep);
		}
		try {
			TransactionUtil.begin();
			try {
				GenericValue fixedAsset = delegator.findOne("FixedAsset", UtilMisc.toMap("fixedAssetId", paramMap.get("fixedAssetId")), false);
				Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateFixedAsset", paramMap, userLogin, timeZone, locale);
				context.put("fixedAssetTypeId", fixedAsset.get("fixedAssetTypeId"));
				Map<String, Object> resultService = dispatcher.runSync("updateFixedAsset", context);
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					return "error";
				}
				context = ServiceUtil.setServiceFields(dispatcher, "updateFixedAssetDepreciation", paramMap, userLogin, timeZone, locale);
				resultService = dispatcher.runSync("updateFixedAssetDepreciation", context);
				if(!ServiceUtil.isSuccess(resultService)){
					TransactionUtil.rollback();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					return "error";
				}
				TransactionUtil.commit();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			} catch (GeneralServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			} catch (GenericServiceException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			} catch (GenericEntityException e) {
				e.printStackTrace();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
				TransactionUtil.rollback();
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String getFixedAssetIdAutoGenerate(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		EntityCondition cond = EntityCondition.makeCondition("fixedAssetId", EntityJoinOperator.LIKE, String.valueOf(year) + "%");
		try {
			List<GenericValue> fixedAssetList = delegator.findList("FixedAsset", cond, null, null, null, false);
			String seqId = String.valueOf(fixedAssetList.size() + 1);
			if(seqId.length() < 5){
				for(int i = seqId.length(); i < 5; i++){
					seqId = "0" + seqId;
				}
			}
			String fixedAssetId = String.valueOf(year) + "-" + seqId;
			request.setAttribute("fixedAssetId", fixedAssetId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return "error";
		}
		return "success";
	}
	
	public static String updateFixedAsset(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String partyId = (String)paramMap.get("partyId");
		String productStoreId = (String)paramMap.get("productStoreId");
		String receiptDateStr = (String)paramMap.get("receiptDate");
		if(receiptDateStr != null){
			Timestamp receiptDate = new Timestamp(Long.parseLong(receiptDateStr));
			paramMap.put("receiptDate", receiptDate);
		}
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateFixedAsset", paramMap, userLogin, timeZone, locale);
			context.put("partyId", partyId);
			context.put("productStoreId", productStoreId);
			Map<String, Object> resultService = dispatcher.runSync("updateFixedAsset", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return "success";
	}
	
	public static String getListFixedAssetItemGrid(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
 		Delegator delegator = (Delegator) request.getAttribute("delegator");
 		List<EntityCondition> listAllConditions = FastList.newInstance();
 		List<String> listSortFields = FastList.newInstance();
 		String dateStr = request.getParameter("date");
 		String month = request.getParameter("month");
 		String year = request.getParameter("year");
 		listSortFields.add("dateAcquired");
 		
 		Timestamp date = new Timestamp(System.currentTimeMillis());
		if(dateStr != null){
			date = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(dateStr)));
		}
		
		List<GenericValue> faUsedList = delegator.findList("FixedAssetDecreaseAndItem", 
				EntityCondition.makeCondition("voucherDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, date), null, null, null, false);
		if (UtilValidate.isNotEmpty(faUsedList)) {
			List<String> fixedAssetIds = EntityUtil.getFieldListFromEntityList(faUsedList, "fixedAssetId", true);
			listAllConditions.add(EntityCondition.makeCondition("fixedAssetId", EntityJoinOperator.NOT_IN, fixedAssetIds));
		}
 		
 		List<GenericValue> fixedAssetDepCalItemList = delegator.findList("FixedAssetDepreciationCalcAndItem",
				EntityCondition.makeCondition(EntityCondition.makeCondition("month", Integer.valueOf(month)),
						EntityJoinOperator.AND, EntityCondition.makeCondition("year", Integer.valueOf(year))), null, null, null, false);		
		if(UtilValidate.isNotEmpty(fixedAssetDepCalItemList)){
			List<String> fixedAssetIds = EntityUtil.getFieldListFromEntityList(fixedAssetDepCalItemList, "fixedAssetId", true);
			listAllConditions.add(EntityCondition.makeCondition("fixedAssetId", EntityJoinOperator.NOT_IN, fixedAssetIds));
		}			
	
		listAllConditions.add(EntityCondition.makeCondition("dateAcquired", EntityJoinOperator.LESS_THAN_EQUAL_TO, date));
 		
 		List<GenericValue> listFixedAsset = delegator.findList("FixedAssetAndDetail", 
 				EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
 		List<Map<String, Object>> listReturn = FastList.newInstance();
 		if (UtilValidate.isNotEmpty(listFixedAsset)) {
 			for (GenericValue item : listFixedAsset) {
 				Map<String, Object> map = FastMap.newInstance();
 				map.putAll(item);
 				map.put("groupName", item.getString("fullName"));
 				map.put("debitGlAccountId", item.getString("depGlAccountId"));
 				map.put("creditGlAccountId", item.getString("accDepGlAccountId"));
 				map.put("depreciationAmount", item.getBigDecimal("monthlyDepAmount"));
 				listReturn.add(map);
 			}
 		}
 		
 		request.setAttribute("listReturn", listReturn);
 		return "success";
 	}
}