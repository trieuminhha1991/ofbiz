package com.olbius.basehr.payroll.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class PayrollFormulaServices {
	public static final String module = PayrollServices.class.getName();
    public static final String resource = "hrolbiusUiLabels";
    public static final String resourceNoti = "NotificationUiLabels";
	public static Map<String, Object> deletePayrollFormula(DispatchContext dctx, Map<String, Object> context){
    	Locale locale = (Locale)context.get("locale");
    	String code = (String)context.get("code");
    	Delegator delegator = dctx.getDelegator();
    	try {
    		GenericValue formula = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", code), false);
    		if(formula == null){
    			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToDelete", locale));
    		}
			List<GenericValue> allFormula = delegator.findList("PayrollFormula", EntityCondition.makeCondition("code", EntityOperator.NOT_EQUAL, code), null, null, null, false);
			String codeFunction = code + "()";
			for(GenericValue tempGv: allFormula){
				String function = tempGv.getString("function");
				if(function != null && function.contains(codeFunction)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotDeleteCodeExistsInAnotherCode", UtilMisc.toMap("code", code, "codeExists", tempGv.getString("code")), locale));
				}
			}
			List<GenericValue> payrollTableCodes = delegator.findByAnd("PayrollTableCode", UtilMisc.toMap("code", code), null, false);
			if(UtilValidate.isNotEmpty(payrollTableCodes)){
				GenericValue payrollTableCode = payrollTableCodes.get(0);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotDeleteCodeExistInPayrollTable", UtilMisc.toMap("payrollTableId", payrollTableCode.getString("payrollTableId"), "code", code), locale));
			}
			formula.remove();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
    }
	
	/*
     * Description: Create new formula
     * */
    public static Map<String, Object> createPayrollFormula(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	String strCode = (String)context.get("code");
    	String strName = (String)context.get("name");
    	//String strInvoiceItemTypeId = (String)context.get("invoiceItemTypeId");
    	String strFunctionJson = (String)context.get("functionJson");
    	String strFunctionType = (String)context.get("functionType");
    	String strMaxValue = (String)context.get("maxValue");
    	String strDescription = (String)context.get("description");
    	String functionRelated = (String)context.get("functionRelated");
    	String strFunction = (String)context.get("function");
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	try {
    		GenericValue checkFormula = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", strCode), false);
    		if(UtilValidate.isNotEmpty(checkFormula)){
    			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CodeFormulaIsExists", locale));
    		}
    		if(!CommonUtil.containsValidCharacter(strCode)){
    			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CodeContainsInvalidLetters", locale));
    		}
    		if(functionRelated != null){
    			String[] functionRelateArr = functionRelated.split(",");
    			Set<String> functionRelateSet = new HashSet<String>();
    			if(Collections.addAll(functionRelateSet, functionRelateArr)){
    				functionRelated = StringUtils.join(functionRelateSet, ",");
    			}
    		}
	    	String function = "";
	    	if(UtilValidate.isNotEmpty(strFunctionJson)){
	    		JSONObject json = JSONObject.fromObject(strFunctionJson);
	    		String statement = json.getString("statement");
	    		Object conds_true = json.get("if_true");
	    		Object conds_false = json.get("if_false");
	    		function += "if(" + statement + "){";
	    		
	    		if(conds_true != null){
	    			if(conds_true instanceof JSONObject){
	    				function += getReturnValueFromJSON((JSONObject)conds_true);
	    			}	
	    			else{
	        			function += "return \"" + conds_true + "\";";
	        		}
	    		}
	    		function += "}";
	    		function += "else{";
	    		if(conds_false != null){
	    			if(conds_false instanceof JSONObject){
	    				function += getReturnValueFromJSON((JSONObject)conds_false);
	    			}else{
	    				function += "return \"" + conds_false + "\";";
	    			}
	    		}
	    		function += "}";
	    	}else if(UtilValidate.isNotEmpty(strFunction)){
	    		function = strFunction;
	    	}else{
	    		return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels.xml", 
	                    "formulaIsEmptyOrNotValid", locale));
	    	}
	    	GenericValue tempPayrollFormula = delegator.makeValue("PayrollFormula");
	    	tempPayrollFormula.setNonPKFields(context);
	    	Map<String, Object> entityMap = FastMap.newInstance();
	    	entityMap.put("code", strCode); 
	    	entityMap.put("name", strName);
			/*"invoiceItemTypeId", strInvoiceItemTypeId,*/
	    	entityMap.put("function", function); 
	    	entityMap.put("functionType", strFunctionType);
	    	entityMap.put("maxValue", strMaxValue); 
	    	entityMap.put("description", strDescription);
	    	entityMap.put("functionRelated", functionRelated);
	    	tempPayrollFormula.setAllFields(entityMap, false, null, null);
    		tempPayrollFormula.create();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, 
                    UtilProperties.getMessage(resourceNoti, "createSuccessfully", locale));
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "createError", new Object[] { e.getMessage() }, locale));
		}
		return result;
    }
    
    private static String getReturnValueFromJSON(JSONObject json) {
    	String retVal = "";
    	String statement = json.getString("statement");
		Object conds_true = json.get("if_true");
		Object conds_false = json.get("if_false");
		retVal += "if(" + statement + "){";
		if(conds_true != null){
			if(conds_true instanceof JSONObject){
				retVal += getReturnValueFromJSON((JSONObject)conds_true);
			}else{
				retVal += "return \"" + conds_true + "\";";
			}
		}
		retVal += "}";
		retVal += "else{";
		if(conds_false != null){
			if(conds_false instanceof JSONObject){
				retVal += getReturnValueFromJSON((JSONObject)conds_false);
			}else{
				retVal += "return \"" + conds_false + "\";";
			}
		}
		retVal += "}";
		return retVal;
	}
    
    /*
     * Description: Update formula
     * */
    public static Map<String, Object> updatePayrollFormula(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	String strCode = (String)context.get("code");
    	//String strName = (String)context.get("name");
    	//String strInvoiceItemTypeId = (String)context.get("invoiceItemTypeId");
    	//String strFunctionType = (String)context.get("functionType");
    	//String strMaxValue = (String)context.get("maxValue");
    	//String strDescription = (String)context.get("description");
    	String functionRelated = (String)context.get("functionRelated");
    	String strFunctionJson = (String)context.get("functionJson");
    	String strFunction = (String)context.get("function");
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	try {
    		GenericValue tempPayrollFormula = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", strCode),false);
    		if(tempPayrollFormula == null){
    			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToUpdate", locale));
    		}
    		/*tempPayrollFormula.put("name", strName);
    		tempPayrollFormula.put("function", strFunction);
    		tempPayrollFormula.put("functionType", strFunctionType);
    		tempPayrollFormula.put("maxValue", strMaxValue);
    		tempPayrollFormula.put("description", strDescription);*/
    		if(functionRelated != null){
    			String[] functionRelateArr = functionRelated.split(",");
    			Set<String> functionRelateSet = new HashSet<String>();
    			if(Collections.addAll(functionRelateSet, functionRelateArr)){
    				functionRelated = StringUtils.join(functionRelateSet, ",");
    			}
    		}
    		String function = "";
	    	if(UtilValidate.isNotEmpty(strFunctionJson)){
	    		JSONObject json = JSONObject.fromObject(strFunctionJson);
	        	
	    		String statement = json.getString("statement");
	    		Object conds_true = json.get("if_true");
	    		Object conds_false = json.get("if_false");
	    		function += "if(" + statement + "){";
	    		
	    		if(conds_true != null){
	    			if(conds_true instanceof JSONObject){
	    				function += getReturnValueFromJSON((JSONObject)conds_true);
	    			}	
	    			else{
	        			function += "return \"" + conds_true + "\";";
	        		}
	    		}
	    		function += "}";
	    		function += "else{";
	    		if(conds_false != null){
	    			if(conds_false instanceof JSONObject){
	    				function += getReturnValueFromJSON((JSONObject)conds_false);
	    			}else{
	    				function += "return \"" + conds_false + "\";";
	    			}
	    		}
	    		function += "}";
	    	}else if(UtilValidate.isNotEmpty(strFunction)){
	    		function = strFunction;
	    	}else{
	    		return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels.xml", 
	                    "formulaIsEmptyOrNotValid", locale));
	    	}
	    	tempPayrollFormula.setNonPKFields(context);
	    	tempPayrollFormula.set("function", function);
	    	tempPayrollFormula.set("functionRelated", functionRelated);
    		tempPayrollFormula.store();
    		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
    		result.put(ModelService.SUCCESS_MESSAGE, 
                    UtilProperties.getMessage(resourceNoti, "updateSuccessfully", locale));
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "updateError", new Object[] { e.getMessage() }, locale));
		}
		return result;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getFormulaIncome(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("-createdStamp");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("payrollCharacteristicId", "INCOME"));
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
	
		try {
			listIterator = delegator.find("PayrollFormula", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getFormulaDeduction(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("-createdStamp");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("payrollCharacteristicId", "TAX_DEDUCTION"),
															EntityOperator.OR, 
															EntityCondition.makeCondition("payrollCharacteristicId", "DEDUCTION")));
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
	
		try {
			listIterator = delegator.find("PayrollFormula", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getFormulaOther(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("-createdStamp");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("payrollCharacteristicId", null));
		
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
	
		try {
			listIterator = delegator.find("PayrollFormula", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getAllFormula(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("payrollCharacteristicId");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("PayrollFormula", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String, Object> getPartyPayrollFormulaDetail(DispatchContext dctx, Map<String, Object> context){
    	//LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	String partyId = (String)context.get("partyId");
    	String payrollTableId = (String)context.get("payrollTableId");
    	String fromDateStr = (String)context.get("fromDate");
    	Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    
    	try {
    		List<GenericValue> payrollRecordCode = delegator.findByAnd("PayrollTableCode", UtilMisc.toMap("payrollTableId", payrollTableId), 
					UtilMisc.toList("code"), false);
			List<String> payrollRecordCodeList = EntityUtil.getFieldListFromEntityList(payrollRecordCode, "code", true);
			EntityCondition commonConds = EntityCondition.makeCondition("code", EntityOperator.IN, payrollRecordCodeList);
			EntityCondition payrollCharConds = EntityCondition.makeCondition("payrollCharacteristicId", EntityOperator.IN, UtilMisc.toList("INCOME", "DEDUCTION"));
			
			List<GenericValue> listFormulaIncomeAndDeduction = delegator.findList("PayrollFormula", 
					EntityCondition.makeCondition(commonConds, EntityOperator.AND, payrollCharConds), null, 
					UtilMisc.toList("payrollCharacteristicId"), null, false);
			//List<GenericValue> listFormulaDeduction = delegator.findList("PayrollFormula", EntityCondition.makeCondition(commonConds, EntityOperator.AND, deductionCondition), null, null, null, false);
						
			for(GenericValue tempFormula: listFormulaIncomeAndDeduction){
				Map<String, Object> tempMap = FastMap.newInstance();
				String code = tempFormula.getString("code");
				GenericValue partyPayrollTable = delegator.findOne("PayrollTable", UtilMisc.toMap("payrollTableId", payrollTableId, "fromDate", fromDate, "partyId", partyId, "code", code), false);
				BigDecimal value = partyPayrollTable.getBigDecimal("value");
				Double amount = value.doubleValue();
				tempMap.put("partyId", partyId);
				tempMap.put("code", code);
				tempMap.put("amount", amount);
				tempMap.put("codeName", tempFormula.get("name"));
				String payrollCharacteristicId = tempFormula.getString("payrollCharacteristicId");
				if(payrollCharacteristicId != null){
					GenericValue payrollCharacteristic = delegator.findOne("PayrollCharacteristic", UtilMisc.toMap("payrollCharacteristicId", payrollCharacteristicId), false);
					tempMap.put("payrollCharacteristicId", payrollCharacteristic.getString("description"));
				}
				String statusId = partyPayrollTable.getString("statusId");
				if(statusId != null){
					GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
					tempMap.put("statusId", status.getString("description"));
				}
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	Map<String, Object> retMap = FastMap.newInstance();
    	retMap.put("listData", listReturn);
    	return retMap;
    }
    
    /*create party formula invoice item type*/
	
	public static Map<String, Object> createPartyFormulaInvoiceItemType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
		String code = (String)context.get("code");
		String partyListIdStr= (String)context.get("partyListId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		
		if(partyId == null && partyListIdStr == null){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "NoPartyFoundForFormulaInvoiceItemType", locale));
		}
		JSONArray partyIdListJson = JSONArray.fromObject(partyListIdStr);
		List<String> partyListId = FastList.newInstance();
		for(int i = 0; i < partyIdListJson.size(); i++){
			partyListId.add(partyIdListJson.getJSONObject(i).getString("partyId"));
		}
		if(fromDate == null){
			fromDate = UtilDateTime.nowTimestamp();
		}
		fromDate = UtilDateTime.getDayStart(fromDate);
		context.put("fromDate", fromDate);
		try {
			if(partyListId != null){
				for(String tempPartyId: partyListId){
					List<GenericValue> checkEntityValue =  delegator.findByAnd("PartyPayrollFormulaInvoiceItemType", UtilMisc.toMap("partyId", tempPartyId, "invoiceItemTypeId", invoiceItemTypeId, "code", code),null, false);
					if(UtilValidate.isNotEmpty(checkEntityValue)){
						GenericValue invoiceItemType = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId), false);
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "PartyFormulaInvoiceItemTypeExists",UtilMisc.toMap("departmentName", PartyHelper.getPartyName(delegator, tempPartyId, false), "code", code, "invoiceItemType", invoiceItemType.getString("description")), locale));
					}
					GenericValue partyPayrollFormulaInvoiceItemType = delegator.makeValue("PartyPayrollFormulaInvoiceItemType");
					partyPayrollFormulaInvoiceItemType.setAllFields(context, false, null, null);
					partyPayrollFormulaInvoiceItemType.set("partyId", tempPartyId);
					partyPayrollFormulaInvoiceItemType.create();
				}
			}else if(partyId != null){
				List<GenericValue> checkEntityValue =  delegator.findByAnd("PartyPayrollFormulaInvoiceItemType", UtilMisc.toMap("partyId", partyId, "invoiceItemTypeId", invoiceItemTypeId, "code", code),null, false);
				if(checkEntityValue != null){
					GenericValue invoiceItemType = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId), false);
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "PartyFormulaInvoiceItemTypeExists",UtilMisc.toMap("departmentName", PartyHelper.getPartyName(delegator, partyId, false), "code", code, "invoiceItemType", invoiceItemType.getString("description")), locale));
				}
				GenericValue partyPayrollFormulaInvoiceItemType = delegator.makeValue("PartyPayrollFormulaInvoiceItemType");
				partyPayrollFormulaInvoiceItemType.setAllFields(context, false, null, null);
				partyPayrollFormulaInvoiceItemType.create();
			}
		//partyPayrollFormulaInvoiceItemType.set("fromDate", UtilDateTime.nowTimestamp());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
	}
	
	public static Map<String, Object> updatePartyFormulaInvoiceItemType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		String partyId =(String)context.get("partyId");
		String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
		String code = (String)context.get("code");
		try {
			GenericValue updateEntity = delegator.findOne("PartyPayrollFormulaInvoiceItemType", UtilMisc.toMap("partyId", partyId, 
															"code", code, "invoiceItemTypeId", invoiceItemTypeId, "fromDate", fromDate), false);
			if(updateEntity == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToUpdate", locale));
			}
			Timestamp thruDate = (Timestamp)context.get("thruDate");
			if(thruDate != null){
				thruDate = UtilDateTime.getDayEnd(thruDate);
				updateEntity.set("thruDate", thruDate);
				updateEntity.store();
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> deletePartyFormulaInvoiceItemType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
		String code = (String)context.get("code");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue deleteEntity = delegator.findOne("PartyPayrollFormulaInvoiceItemType", UtilMisc.toMap("partyId", partyId, "invoiceItemTypeId", invoiceItemTypeId, "code", code, "fromDate", fromDate), false);
			if(deleteEntity == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToDelete", locale));
			}
			deleteEntity.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	
	/* get party formular invoice item type */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyFormulaInvoiceItemTypeJQ(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("code ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		/*Integer pagesize = (Integer)context.get("pagesize");
		Integer pagenum = (Integer)context.get("pagenum");*/
		try {
			listIterator = delegator.find("PartyPayrollFormulaInvoiceItemTypeDetail", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getPartyFormulaInvoiceItemType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		int pagesize = (Integer)context.get("pagesize");
		int pagenum = (Integer)context.get("pagenum");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String partyId = (String)context.get("partyId");
		EntityCondition dateConds = EntityConditionUtils.makeDateConds(fromDate, thruDate);
		int start = pagesize * pagenum;
		int end = start + pagesize;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(dateConds);
		try {
			if(partyId != null){
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
				List<GenericValue> allDept = buildOrg.getAllDepartmentList(delegator);
				List<String> allDeptId = EntityUtil.getFieldListFromEntityList(allDept, "partyId", true);
				if(allDeptId == null){
					allDeptId = FastList.newInstance();
				}
				allDeptId.add(partyId);
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, allDeptId));
			}
			List<GenericValue> listGv = delegator.findList("PartyPayrollFormulaInvoiceItemTypeDetail", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("partyId", "code"), null, false);
			int totalRows = listGv.size();
			if(end > totalRows){
				end = totalRows;
			}
			listGv = listGv.subList(start, end);			
			for(GenericValue tempGv: listGv){
				Map<String, Object> tempMap = FastMap.newInstance();
				String tempPartyId = tempGv.getString("partyId");
				String subsidiary = PartyUtil.getSubsidiaryOfPartyGroup(delegator, tempPartyId);
				if(subsidiary != null){
					tempMap.put("orgId", subsidiary);
					tempMap.put("orgName", PartyUtil.getPartyName(delegator, subsidiary));
				}
				tempMap.put("partyId", tempPartyId);
				tempMap.put("groupName", tempGv.get("groupName"));
				tempMap.put("description", tempGv.get("description"));
				tempMap.put("code", tempGv.get("code"));
				tempMap.put("invoiceItemTypeId", tempGv.get("invoiceItemTypeId"));
				Timestamp tempFromDate = tempGv.getTimestamp("fromDate");
				if(tempFromDate != null){
					tempMap.put("fromDate", tempFromDate.getTime());
				}
				Timestamp tempThruDate = tempGv.getTimestamp("thruDate");
				if(tempThruDate != null){
					tempMap.put("thruDate", tempThruDate.getTime());
				}
				listReturn.add(tempMap);
			}
			successResult.put("listReturn", listReturn);
			successResult.put("totalRows", totalRows);
		} catch (GenericEntityException e){
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	/*get formular for jqxgrid*/
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getFormulas(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		listSortFields.add("-createdStamp");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		try {
			listIterator = delegator.find("PayrollFormula", tmpCond, null, null,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyPayrollAmountJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String partyId = request.getParameter("partyId");
		String monthStr = request.getParameter("month");
		String yearStr = request.getParameter("year");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		EntityListIterator listIterator = null;
		Security security = dctx.getSecurity();
		Locale locale = (Locale)context.get("locale");
		if(partyId != null){
			if(!partyId.equals(userLogin.getString("partyId"))){
				if(!security.hasEntityPermission("HR_MGRPAYROLL", "_UPDATE", userLogin)){
					return ServiceUtil.returnError(UtilProperties.getMessage("PartyUiLabels", "PartyNoAccess", locale));
				}
			}
		}else{
			partyId = userLogin.getString("partyId");
		}
		Calendar cal = Calendar.getInstance();
		String payrollCharacteristicId = request.getParameter("payrollCharacteristicId");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
		listAllConditions.add(EntityCondition.makeCondition("payrollCharacteristicId", payrollCharacteristicId));
		try {
			Long month = monthStr != null? Long.parseLong(monthStr) : cal.get(Calendar.MONTH);
			Long year = yearStr != null? Long.parseLong(yearStr) : cal.get(Calendar.YEAR);
			listAllConditions.add(EntityCondition.makeCondition("invoiceId", EntityJoinOperator.NOT_EQUAL, null));
			listAllConditions.add(EntityCondition.makeCondition("month", month));
			listAllConditions.add(EntityCondition.makeCondition("year", year));
			listAllConditions.add(EntityCondition.makeCondition("amount", EntityJoinOperator.GREATER_THAN, BigDecimal.ZERO));
			listIterator = delegator.find("PayrollTableRecordPartyAmountAndFormula", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
}
