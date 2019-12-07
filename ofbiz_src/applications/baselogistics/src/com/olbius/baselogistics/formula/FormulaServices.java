package com.olbius.baselogistics.formula;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
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
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.baselogistics.util.JsonUtil;
import com.olbius.baselogistics.util.LogisticsFacilityUtil;
import com.olbius.baselogistics.util.LogisticsOrderUtil;
import com.olbius.baselogistics.util.LogisticsProductUtil;
import com.olbius.baselogistics.util.LogisticsUtil;
import com.olbius.product.util.ProductUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FormulaServices {
	public static final String module = FormulaServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
    public static final String resource_error = "BaseLogisticsErrorUiLabels";
    
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jQGetListFormulaProducts(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("productId");
		}
		String formulaTypeId = null;
		if (parameters.containsKey("formulaTypeId") && parameters.get("formulaTypeId").length > 0) {
			formulaTypeId = (String)parameters.get("formulaTypeId")[0];
			listAllConditions.add(EntityCondition.makeCondition("formulaTypeId", EntityJoinOperator.EQUALS, formulaTypeId));
		}
		List<GenericValue> list = new ArrayList<GenericValue>();
		try {	
			listIterator = delegator.find("FormulaProductDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null,
					listSortFields, opts);
			list = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jQGetListFormulaProducts service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError("OLBIUS: "+ errMsg);
		}
		successResult.put("listIterator", list);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jQGetParameterAndProductStores(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("productStoreId");
		}
		String productStoreId = null;
		if (parameters.containsKey("productStoreId") && parameters.get("productStoreId").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("productStoreId")[0])) {
				productStoreId = (String)parameters.get("productStoreId")[0];
				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityJoinOperator.EQUALS, productStoreId));
			}
		}
		if (UtilValidate.isEmpty(productStoreId)) {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String companyStr = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<String> listStoreIds = new ArrayList<String>();
			try {
				List<GenericValue> listProductStoreRoles = delegator.findList("ProductStoreRole",
						EntityCondition.makeCondition(UtilMisc.toMap("partyId", companyStr, "roleTypeId", "OWNER")), null, null, null, false);
				if (!listProductStoreRoles.isEmpty()) {
					listStoreIds = EntityUtil.getFieldListFromEntityList(listProductStoreRoles, "productStoreId", true);
				}
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS: get store error!" + e.toString());
			}
			listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityJoinOperator.IN, listStoreIds));
		}
		List<GenericValue> list = new ArrayList<GenericValue>();
		try {	
			listIterator = delegator.find("FormulaParameterProductStoreDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null,
					listSortFields, opts);
			list = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jQGetParameterAndProductStores service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError("OLBIUS: "+ errMsg);
		}
		successResult.put("listIterator", list);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jQGetListFormulas(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<GenericValue> list = new ArrayList<GenericValue>();
		String formulaTypeId = null;
		if (parameters.containsKey("formulaTypeId") && parameters.get("formulaTypeId").length > 0) {
			formulaTypeId = (String)parameters.get("formulaTypeId")[0];
			listAllConditions.add(EntityCondition.makeCondition("formulaTypeId", EntityJoinOperator.EQUALS, formulaTypeId));
		}
		try {	
			listIterator = delegator.find("Formula", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null,
					listSortFields, opts);
			list = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jQGetListFormulas service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError("OLBIUS: "+ errMsg);
		}
		successResult.put("listIterator", list);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jQGetListFormulaParameters(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<GenericValue> list = new ArrayList<GenericValue>();
		String parameterTypeId = null;
		if (parameters.containsKey("parameterTypeId") && parameters.get("parameterTypeId").length > 0) {
			parameterTypeId = (String)parameters.get("parameterTypeId")[0];
			listAllConditions.add(EntityCondition.makeCondition("parameterTypeId", EntityJoinOperator.EQUALS, parameterTypeId));
		}
		try {	
			listIterator = delegator.find("FormulaParameter", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null,
					listSortFields, opts);
			list = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jQGetListFormulaParameters service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError("OLBIUS: "+ errMsg);
		}
		successResult.put("listIterator", list);
		return successResult;
	}
	
	public static Map<String, Object> createFormula (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String formulaValue = (String)context.get("formulaValue");
		String formulaTypeId = (String)context.get("formulaTypeId");
		String formulaName = (String)context.get("formulaName");
		String formulaCode = (String)context.get("formulaCode");
		String description = (String)context.get("description");
		GenericValue formula = delegator.makeValue("Formula");
		String formulaId = delegator.getNextSeqId("Formula");
		formula.set("formulaValue", formulaValue);
		formula.set("formulaTypeId", formulaTypeId);
		formula.set("formulaName", formulaName);
		formula.set("formulaCode", formulaCode);
		formula.set("formulaId", formulaId);
		formula.set("description", description);
		formula.set("statusId", "FML_ACTIVATED");
		
		try {
			EntityCondition cond2 = EntityCondition.makeCondition("formulaCode", EntityOperator.EQUALS, formulaCode);
			List<GenericValue> listExistedByCode = delegator.findList("Formula", EntityCondition.makeCondition(cond2), null, null, null, false);
			if (!listExistedByCode.isEmpty()) return ServiceUtil.returnError("OLBIUS_FORMULA_CODE_EXISTED");
		} catch (GenericEntityException e1) {
			return ServiceUtil.returnError("OLBIUS: find Formula error! " + e1.toString());
		}
		
		try {
			delegator.create(formula);
			
			// create history
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> map = FastMap.newInstance();
			map.put("formulaId", formula.getString("formulaId"));
			map.put("formulaValue", formula.getString("formulaValue"));
			map.put("formulaCode", formula.getString("formulaCode"));
			map.put("formulaName", formula.getString("formulaName"));
			map.put("formulaTypeId", formula.getString("formulaTypeId"));
			map.put("description", formula.getString("description"));
			map.put("statusId", formula.getString("statusId"));
			map.put("userLogin", userLogin);
			try {
				LocalDispatcher dispatcher = ctx.getDispatcher();
				dispatcher.runSync("createFormulaHistory", map);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: createFormulaHistory error! " + e.toString());
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Create Formula error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("formulaId", formulaId);
		return successResult;
	}
	
	public static Map<String, Object> updateFormula (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();

		String formulaId = (String)context.get("formulaId");
		String formulaValue = (String)context.get("formulaValue");
		String formulaCode = (String)context.get("formulaCode");
		String formulaName = (String)context.get("formulaName");
		String formulaTypeId = (String)context.get("formulaTypeId");
		String description = (String)context.get("description");
		String statusId = (String)context.get("statusId");
		
		try {
			GenericValue formula = delegator.findOne("Formula", false, UtilMisc.toMap("formulaId", formulaId));
			String formulaCodeInit = formula.getString("formulaCode");
			if (!formulaCode.equals(formulaCodeInit)) {
				List<GenericValue> listByCode = delegator.findList("Formula",
						EntityCondition.makeCondition("formulaCode", EntityOperator.EQUALS, formulaCode), null, null, null, false);
				if (!listByCode.isEmpty()) return ServiceUtil.returnError("OLBIUS_FORMULA_CODE_EXISTED");
			}
			// save history
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> map = FastMap.newInstance();
			map.put("formulaId", formula.getString("formulaId"));
			map.put("formulaValue", formula.getString("formulaValue"));
			map.put("formulaCode", formula.getString("formulaCode"));
			map.put("formulaName", formula.getString("formulaName"));
			map.put("formulaTypeId", formula.getString("formulaTypeId"));
			map.put("description", formula.getString("description"));
			map.put("statusId", formula.getString("statusId"));
			map.put("userLogin", userLogin);
			try {
				LocalDispatcher dispatcher = ctx.getDispatcher();
				dispatcher.runSync("createFormulaHistory", map);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: createFormulaHistory error! " + e.toString());
			}
			
			if (UtilValidate.isNotEmpty(formula)){
				formula.set("formulaValue", formulaValue);
				formula.set("formulaCode", formulaCode);
				formula.set("formulaName", formulaName);
				formula.set("description", description);
				formula.set("formulaTypeId", formulaTypeId);
				formula.set("statusId", statusId);
				delegator.store(formula);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Update Formula error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("formulaId", formulaId);
		return successResult;
	}
	
	public static Map<String, Object> deleteFormula (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String formulaId = (String)context.get("formulaId");
		try {
			GenericValue formula = delegator.findOne("Formula", false, UtilMisc.toMap("formulaId", formulaId));
			if (UtilValidate.isNotEmpty(formula)){
				List<GenericValue> listHistory = delegator.findList("FormulaHistory",
						EntityCondition.makeCondition("formulaId", EntityOperator.EQUALS, formulaId), null, null, null, false);
				try {
					delegator.removeAll(listHistory);
					delegator.removeValue(formula);
				} catch (GenericEntityException e) {
					return ServiceUtil.returnError("OLBIUS_FORMULA_IN_USING " + e.toString());
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Delete Formula error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> createFormulaParameter (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String parameterValue = (String)context.get("parameterValue");
		String parameterTypeId = (String)context.get("parameterTypeId");
		String parameterCode = (String)context.get("parameterCode");
		String parameterName = (String)context.get("parameterName");
		String defaultValue = (String)context.get("defaultValue");
		String description = (String)context.get("description");
		GenericValue formulaParameter = delegator.makeValue("FormulaParameter");
		String parameterId = delegator.getNextSeqId("FormulaParameter");
		formulaParameter.set("parameterValue", parameterValue);
		formulaParameter.set("parameterTypeId", parameterTypeId);
		formulaParameter.set("parameterId", parameterId);
		formulaParameter.set("defaultValue", defaultValue);
		formulaParameter.set("parameterCode", parameterCode);
		formulaParameter.set("parameterName", parameterName);
		formulaParameter.set("description", description);
		formulaParameter.set("statusId", "PAR_ACTIVATED");
		try {
			delegator.create(formulaParameter);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Create FormulaParameter error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("parameterId", parameterId);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createFormulaParameterTotal (DispatchContext ctx, Map<String, ? extends Object> context){
		String parameterValue = (String)context.get("parameterValue");
		String parameterTypeId = (String)context.get("parameterTypeId");
		String parameterCode = (String)context.get("parameterCode");
		String parameterName = (String)context.get("parameterName");
		String defaultValue = (String)context.get("defaultValue");
		String description = (String)context.get("description");
		Long fromDate = (Long)context.get("fromDate");
		Long thruDate = (Long)context.get("thruDate");
		Delegator delegator = ctx.getDelegator();
		
		try {
			EntityCondition cond1 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAR_DEACTIVATED");
			EntityCondition cond2 = EntityCondition.makeCondition("parameterCode", EntityOperator.EQUALS, parameterCode);
			List<EntityCondition> allConds = UtilMisc.toList(cond1, cond2);
			List<GenericValue> listExistedByCode = delegator.findList("FormulaParameter", EntityCondition.makeCondition(allConds), null, null, null, false);
			if (!listExistedByCode.isEmpty()) return ServiceUtil.returnError("OLBIUS_PARAMETER_CODE_EXISTED");
		} catch (GenericEntityException e1) {
			return ServiceUtil.returnError("OLBIUS: find FormulaParameter error! " + e1.toString());
		}
		
		List<Object> listItemTmp = (List<Object>)context.get("listProductStoreIds");
    	Boolean isJson = false;
    	if (!listItemTmp.isEmpty()){
    		if (listItemTmp.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	List<String> listProductStoreIds = new ArrayList<String>();
    	if (isJson){
    		String stringJson = "["+(String)listItemTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productStoreId")){
					if (!listProductStoreIds.contains(item.getString("productStoreId"))) listProductStoreIds.add(item.getString("productStoreId"));
				}
			}
    	}
    	Map<String, Object> mapCreateParam = FastMap.newInstance();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
		mapCreateParam.put("parameterValue", parameterValue);
		mapCreateParam.put("parameterTypeId", parameterTypeId);
		mapCreateParam.put("parameterCode", parameterCode);
		mapCreateParam.put("defaultValue", defaultValue);
		mapCreateParam.put("parameterName", parameterName);
		mapCreateParam.put("description", description);
		mapCreateParam.put("userLogin", userLogin);
		String parameterId = null;
		try {
			Map<String, Object> map = dispatcher.runSync("createFormulaParameter", mapCreateParam);
			parameterId = (String)map.get("parameterId");
			if (UtilValidate.isNotEmpty(parameterId) && !listProductStoreIds.isEmpty()) {
				for (String productStoreId : listProductStoreIds) {
					Map<String, Object> mapCreateParamStore = FastMap.newInstance();
					mapCreateParamStore.put("parameterId", parameterId);
					mapCreateParamStore.put("productStoreId", productStoreId);
					if (UtilValidate.isNotEmpty(fromDate)) {
						mapCreateParamStore.put("fromDate", fromDate);
					}
					if (UtilValidate.isNotEmpty(thruDate)) {
						mapCreateParamStore.put("thruDate", thruDate);
					}
					mapCreateParamStore.put("parameterValue", parameterValue);
					mapCreateParamStore.put("userLogin", userLogin);
					try {
						dispatcher.runSync("createFormulaParameterProductStore", mapCreateParamStore);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: createFormulaParameterProductStore error! " + e.toString());
					}
				}
			}
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: createFormulaParameter error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> updateFormulaParameter (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();

		String parameterId = (String)context.get("parameterId");
		String parameterValue = (String)context.get("parameterValue");
		String parameterName = (String)context.get("parameterName");
		String parameterTypeId = (String)context.get("parameterTypeId");
		String parameterCode = (String)context.get("parameterCode");
		String defaultValue = (String)context.get("defaultValue");
		String description = (String)context.get("description");
		
		try {
			GenericValue formulaParameter = delegator.findOne("FormulaParameter", false, UtilMisc.toMap("parameterId", parameterId));
			if (UtilValidate.isNotEmpty(formulaParameter)){
				// check duplicate code
				String curCode = formulaParameter.getString("parameterCode");
				if (UtilValidate.isNotEmpty(parameterCode) && UtilValidate.isNotEmpty(curCode)) {
					if (!curCode.equals(parameterCode)) {
						EntityCondition cond1 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAR_DEACTIVATED");
						EntityCondition cond2 = EntityCondition.makeCondition("parameterCode", EntityOperator.EQUALS, parameterCode);
						List<EntityCondition> allConds = UtilMisc.toList(cond1, cond2);
						List<GenericValue> listExistedByCode = delegator.findList("FormulaParameter", EntityCondition.makeCondition(allConds), null, null, null, false);
						if (!listExistedByCode.isEmpty()) return ServiceUtil.returnError("OLBIUS_PARAMETER_CODE_EXISTED");
					}
				}
				
				formulaParameter.set("parameterValue", parameterValue);
				formulaParameter.set("parameterName", parameterName);
				formulaParameter.set("parameterTypeId", parameterTypeId);
				formulaParameter.set("parameterCode", parameterCode);
				formulaParameter.set("defaultValue", defaultValue);
				formulaParameter.set("description", description);
				delegator.store(formulaParameter);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Update FormulaParameter error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> deleteFormulaParameter (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String parameterId = (String)context.get("parameterId");
		try {
			GenericValue formulaParameter = delegator.findOne("FormulaParameter", false, UtilMisc.toMap("parameterId", parameterId));
			if (UtilValidate.isNotEmpty(formulaParameter)){
				delegator.removeValue(formulaParameter);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS_PARAMETER_IN_USING " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("parameterId", parameterId);
		return successResult;
	}
	
	public static Map<String, Object> createFormulaProduct (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String formulaId = (String)context.get("formulaId");
		String productId = (String)context.get("productId");
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
			fromDate = new Timestamp((Long)context.get("fromDate"));
		} else {
			fromDate = UtilDateTime.nowTimestamp();
		}
		
		if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
			thruDate = new Timestamp((Long)context.get("thruDate"));
		}
		
		GenericValue formula = delegator.makeValue("FormulaProduct");
		formula.set("formulaId", formulaId);
		formula.set("productId", productId);
		formula.set("fromDate", fromDate);
		formula.set("thruDate", thruDate);
		try {
			delegator.create(formula);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Create FormulaProduct error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> updateFormulaProduct (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();

		String formulaId = (String)context.get("formulaId");
		String productId = (String)context.get("productId");

		String quantityUomId = (String)context.get("quantityUomId");
		Long fromDateStr = (Long)context.get("fromDate");
		Long thruDateStr = (Long)context.get("thruDate");
		
		try {
			List<GenericValue> listFormulaProducts = delegator.findList("FormulaProduct",
					EntityCondition.makeCondition(UtilMisc.toMap("formulaId", formulaId, "productId", productId)), null, null, null, false);
			if (UtilValidate.isNotEmpty(listFormulaProducts)){
				for (GenericValue formula : listFormulaProducts) {
					if (UtilValidate.isNotEmpty(fromDateStr)) {
						formula.set("fromDate", new Timestamp(fromDateStr));
					}
					if (UtilValidate.isNotEmpty(thruDateStr)) {
						formula.set("thruDate", new Timestamp(thruDateStr));
					}
					formula.set("quantityUomId", quantityUomId);
					delegator.store(formula);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Update FormulaProduct error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> deleteFormulaProduct (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String formulaId = (String)context.get("formulaId");
		String productId = (String)context.get("productId");
		Long fromDate = (Long)context.get("fromDate");
		
		try {
			GenericValue formula = delegator.findOne("FormulaProduct", false, UtilMisc.toMap("formulaId", formulaId, "productId", productId, "fromDate", new Timestamp(fromDate)));
			if (UtilValidate.isNotEmpty(formula)){
				formula.set("thruDate", UtilDateTime.nowTimestamp());
				delegator.store(formula);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Delete FormulaProduct error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> createFormulaParameterProductStore (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String parameterValue = (String)context.get("parameterValue");
		String parameterId = (String)context.get("parameterId");
		String productStoreId = (String)context.get("productStoreId");
		
		Long fromDate = (Long) context.get("fromDate");
		Long thruDate = (Long) context.get("thruDate");
		
		GenericValue formulaParameterStore = delegator.makeValue("FormulaParameterProductStore");
		formulaParameterStore.set("parameterValue", parameterValue);
		formulaParameterStore.set("parameterId", parameterId);
		formulaParameterStore.set("productStoreId", productStoreId);
		
		if (UtilValidate.isNotEmpty(fromDate)) {
			formulaParameterStore.set("fromDate", new Timestamp(fromDate));
		} else {
			formulaParameterStore.set("fromDate", UtilDateTime.nowTimestamp());
		}
		
		if (UtilValidate.isNotEmpty(thruDate)) {
			formulaParameterStore.set("thruDate", new Timestamp(thruDate));
		}
		
		try {
			delegator.create(formulaParameterStore);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Create FormulaParameter error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> updateFormulaParameterProductStore (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();

		String parameterValue = (String)context.get("parameterValue");
		String parameterId = (String)context.get("parameterId");
		String productStoreId = (String)context.get("productStoreId");
		
		Long fromDate = (Long) context.get("fromDate");
		Long thruDate = (Long) context.get("thruDate");
		
		try {
			GenericValue formulaParameterStore = delegator.findOne("FormulaParameterProductStore", false, UtilMisc.toMap("parameterId", parameterId, "productStoreId", productStoreId, "fromDate", new Timestamp(fromDate)));
			if (UtilValidate.isNotEmpty(formulaParameterStore)){
				formulaParameterStore.set("parameterValue", parameterValue);
				formulaParameterStore.set("parameterId", parameterId);
				formulaParameterStore.set("productStoreId", productStoreId);
				formulaParameterStore.set("fromDate", new Timestamp(fromDate));
				if (UtilValidate.isNotEmpty(thruDate)) {
					formulaParameterStore.set("thruDate", new Timestamp(thruDate));
				}
				delegator.store(formulaParameterStore);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Update FormulaParameterProductStore error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> deleteFormulaParameterProductStore (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String parameterId = (String)context.get("parameterId");
		String productStoreId = (String)context.get("productStoreId");
		Long fromDate = (Long)context.get("fromDate");
		try {
			GenericValue formulaParameter = delegator.findOne("FormulaParameterProductStore", false, UtilMisc.toMap("parameterId", parameterId, "productStoreId", productStoreId, "fromDate", new Timestamp(fromDate)));
			if (UtilValidate.isNotEmpty(formulaParameter)){
				formulaParameter.put("thruDate", UtilDateTime.nowTimestamp());
				delegator.store(formulaParameter);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Delete FormulaParameterProductStore error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> createFormulaParameterApply (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String formulaId = (String)context.get("formulaId");
		String parameterId = (String)context.get("parameterId");
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
			fromDate = new Timestamp((Long)context.get("fromDate"));
		} else {
			fromDate = UtilDateTime.nowTimestamp();
		}
		if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
			thruDate = new Timestamp((Long)context.get("thruDate"));
		} 
		GenericValue formula = delegator.makeValue("FormulaProduct");
		formula.set("formulaId", formulaId);
		formula.set("parameterId", parameterId);
		formula.set("fromDate", fromDate);
		formula.set("thruDate", thruDate);
		try {
			delegator.create(formula);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Create createFormulaParameterApply error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> updateFormulaParameterApply (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();

		String formulaId = (String)context.get("formulaId");
		String parameterId = (String)context.get("parameterId");

		Long fromDateStr = (Long)context.get("fromDate");
		Long thruDateStr = (Long)context.get("thruDate");
		
		try {
			List<GenericValue> listFormulaParameterApplys = delegator.findList("FormulaParameterApply",
					EntityCondition.makeCondition(UtilMisc.toMap("formulaId", formulaId, "parameterId", parameterId)), null, null, null, false);
			if (UtilValidate.isNotEmpty(listFormulaParameterApplys)){
				for (GenericValue formulaParam : listFormulaParameterApplys) {
					if (UtilValidate.isNotEmpty(fromDateStr)) {
						formulaParam.set("fromDate", new Timestamp(fromDateStr));
					}
					if (UtilValidate.isNotEmpty(thruDateStr)) {
						formulaParam.set("thruDate", new Timestamp(thruDateStr));
					}
					delegator.store(formulaParam);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: updateFormulaParameterApply error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> deleteFormulaParameterApply (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String formulaId = (String)context.get("formulaId");
		String parameterId = (String)context.get("parameterId");
		Long fromDate = (Long)context.get("fromDate");
		
		try {
			GenericValue formulaParameterApply = delegator.findOne("FormulaParameterApply", false, UtilMisc.toMap("formulaId", formulaId, "parameterId", parameterId, "fromDate", fromDate));
			if (UtilValidate.isNotEmpty(formulaParameterApply)){
				formulaParameterApply.set("thruDate", UtilDateTime.nowTimestamp());
				delegator.store(formulaParameterApply);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Delete FormulaParameterApply error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> checkParameterUsing (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String parameterId = (String)context.get("parameterId");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String areUsing = "N";
		try {
			GenericValue formulaParameter = delegator.findOne("FormulaParameter", false, UtilMisc.toMap("parameterId", parameterId));
			if (UtilValidate.isNotEmpty(formulaParameter)){
				// check required data
				List<GenericValue> listFormulaParameterApplys = delegator.findList("FormulaParameterApply",
						EntityCondition.makeCondition("parameterId", EntityOperator.EQUALS, parameterId), null, null, null, false);
				listFormulaParameterApplys = EntityUtil.filterByDate(listFormulaParameterApplys);
				if (!listFormulaParameterApplys.isEmpty()){
					Boolean check = false;
					for (GenericValue appl : listFormulaParameterApplys) {
						String formulaId = appl.getString("formularId");
						GenericValue formula = delegator.findOne("Formula", false, UtilMisc.toMap("formulaId", formulaId));
						if (UtilValidate.isNotEmpty(formula) && "FML_DEACTIVATED".equals(formula.getString("statusId"))) {
							check = true;
							break;
						}
					}
					if (check) {
						areUsing = "Y";
					}
				}
				
				// thruDate with product store relation
				List<GenericValue> listProductStoreParameters = delegator.findList("FormulaParameterProductStore",
						EntityCondition.makeCondition("parameterId", EntityOperator.EQUALS, parameterId), null, null, null, false);
				listProductStoreParameters = EntityUtil.filterByDate(listProductStoreParameters);
				if (!listProductStoreParameters.isEmpty()){
					areUsing = "Y";
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: Delete FormulaParameter error! " + e.toString());
		}
		successResult.put("areUsing", areUsing);
		return successResult;
	}
	
	public static Map<String, Object> changeFormulaParameterStatus (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String parameterId = (String)context.get("parameterId");
		String statusId = (String)context.get("statusId");
		String oldStatusId = null;
		try {
			GenericValue parameter = delegator.findOne("FormulaParameter", false, UtilMisc.toMap("parameterId", parameterId));
			if (UtilValidate.isNotEmpty(parameter)) {
				oldStatusId = parameter.getString("statusId");
				if ("PAR_DEACTIVATED".equals(statusId)) {
					try {
						LocalDispatcher dispatcher = ctx.getDispatcher();
						Map<String, Object> map = FastMap.newInstance();
						GenericValue userLogin = (GenericValue) context.get("userLogin"); 
						map.put("parameterId", parameterId);
						map.put("userLogin", userLogin);
						Map<String, Object> mapCheck = dispatcher.runSync("checkParameterUsing", map);
						String areUsing = (String)mapCheck.get("areUsing");
						if (UtilValidate.isNotEmpty(areUsing) && "Y".equals(areUsing)) {
							return ServiceUtil.returnError("OLBIUS_PARAMETER_IN_USING");
						}
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: checkParameterUsing error! " + e.toString());
					}
				}
				parameter.put("statusId", statusId);
				delegator.store(parameter);
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: findOne FormulaParameter error! " + e.toString());
		}
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("oldStatusId", oldStatusId);
		successResult.put("parameterId", parameterId);
		successResult.put("statusId", statusId);
		return successResult;
	}
	
	public static Map<String, Object> changeFormulaStatus (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String formulaId = (String)context.get("formulaId");
		String statusId = (String)context.get("statusId");
		String oldStatusId = null;
		try {
			GenericValue formula = delegator.findOne("Formula", false, UtilMisc.toMap("formulaId", formulaId));
			if (UtilValidate.isNotEmpty(formula)) {
				// save history 
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				Map<String, Object> map = FastMap.newInstance();
				map.put("formulaId", formula.getString("formulaId"));
				map.put("formulaValue", formula.getString("formulaValue"));
				map.put("formulaCode", formula.getString("formulaCode"));
				map.put("formulaName", formula.getString("formulaName"));
				map.put("formulaTypeId", formula.getString("formulaTypeId"));
				map.put("description", formula.getString("description"));
				map.put("statusId", formula.getString("statusId"));
				map.put("userLogin", userLogin);
				try {
					LocalDispatcher dispatcher = ctx.getDispatcher();
					dispatcher.runSync("createFormulaHistory", map);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: createFormulaHistory error! " + e.toString());
				}
				// store new value
				oldStatusId = formula.getString("statusId");
				formula.put("statusId", statusId);
				delegator.store(formula);
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: findOne Formula error! " + e.toString());
		}
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("oldStatusId", oldStatusId);
		successResult.put("formulaId", formulaId);
		successResult.put("statusId", statusId);
		return successResult;
	}
	
	public static Map<String, Object> createFormulaHistory (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String formulaId = (String)context.get("formulaId");
		String formulaValue = (String)context.get("formulaValue");
		String formulaCode = (String)context.get("formulaCode");
		String formulaName = (String)context.get("formulaName");
		String formulaTypeId = (String)context.get("formulaTypeId");
		String description = (String)context.get("description");
		String statusId = (String)context.get("statusId");
		
		try {
			GenericValue formula = delegator.findOne("Formula", false, UtilMisc.toMap("formulaId", formulaId));
			if (UtilValidate.isNotEmpty(formula)) {
				GenericValue formulaHistory = delegator.makeValue("FormulaHistory"); 
				formulaHistory.put("formulaId", formulaId);
				formulaHistory.put("formulaValue", formulaValue);
				formulaHistory.put("formulaCode", formulaCode);
				formulaHistory.put("formulaName", formulaName);
				formulaHistory.put("description", description);
				formulaHistory.put("formulaTypeId", formulaTypeId);
				formulaHistory.put("statusId", statusId);
				
				formulaHistory.put("changeDate", UtilDateTime.nowTimestamp());
				delegator.create(formulaHistory);
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: findOne Formula error! " + e.toString());
		}
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("formulaId", formulaId);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateProductFacilityParameterValues (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		
		List<Map<String, Object>> listData = (List<Map<String, Object>>)context.get("listData");
		
		if (!listData.isEmpty()){
			for (Map<String, Object> map : listData) {
				String parameterId = (String)map.get("parameterId");
				String parameterValue = (String)map.get("parameterValue");
				String productId = (String)map.get("productId");
				String facilityId = (String)map.get("facilityId");
				
				try {
					GenericValue paramProductFacility = delegator.findOne("ProductFacilityParameter", false, UtilMisc.toMap("productId", productId, "facilityId", facilityId, "parameterId", parameterId));
					if (UtilValidate.isEmpty(paramProductFacility)) {
						GenericValue paramPF = delegator.makeValue("ProductFacilityParameter"); 
						paramPF.put("parameterId", parameterId);
						paramPF.put("parameterValue", parameterValue);
						paramPF.put("productId", productId);
						paramPF.put("facilityId", facilityId);
						delegator.create(paramPF);
					} else {
						paramProductFacility.put("parameterValue", parameterValue);
						delegator.store(paramProductFacility);
					}
				} catch (GenericEntityException e) {
					return ServiceUtil.returnError("OLBIUS: create or store ProductFacilityParameter error! " + e.toString());
				}
				
			}
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateSystemParameterValueByProductStore (DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		String productStoreId = (String)context.get("productStoreId"); 
		try {
			// get all facility by store
			List<String> listFacilityIds = new ArrayList<String>();
			listFacilityIds = LogisticsFacilityUtil.getListFacilityByProductStore(delegator, productStoreId);
			
			// get all product by store
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Map<String, Object> map = FastMap.newInstance();
			map.put("productStoreId", productStoreId);
			map.put("userLogin", userLogin);
			List<String> listProductIds = FastList.newInstance();
			try {
				Map<String, Object> mapReturn = dispatcher.runSync("getListProductIdByProductStoreId", map);
				listProductIds = (List<String>)mapReturn.get("listProductIds");
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: getListProductIdByProductStoreId error! " + e.toString());
			}
			
			if (!listFacilityIds.isEmpty() ){
				// get system parameter
				List<GenericValue> listPrameters = delegator.findList("FormulaParameter",
						EntityCondition.makeCondition("parameterTypeId", EntityOperator.EQUALS, "PARAM_SYSTEM"), null, null, null, false);
				
				if (!listPrameters.isEmpty()){
					List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
					for (String facilityId : listFacilityIds) {
						for (String productId : listProductIds) {
							GenericValue productFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", productId, "facilityId", facilityId));
							for (GenericValue parameter : listPrameters) {
								String parameterId = parameter.getString("parameterId");
								String parameterCode = parameter.getString("parameterCode");
								String parameterValue = null;
								Map<String, Object> mapTmp = FastMap.newInstance();
								mapTmp.put("productId", productId);
								mapTmp.put("facilityId", facilityId);
								mapTmp.put("parameterId", parameterId);
								switch (parameterCode) {
								case "QOO":
									BigDecimal quantityOnOrder = LogisticsOrderUtil.getProductQuantityOnPurchaseOrder(delegator, productId, facilityId);
									parameterValue = quantityOnOrder.toString();
									mapTmp.put("parameterValue", parameterValue);
									listData.add(mapTmp);
									break;
								case "QOH":
									if (UtilValidate.isNotEmpty(productFacility)) {
										parameterValue = productFacility.getBigDecimal("lastInventoryCount").toString();
									} else {
										parameterValue = BigDecimal.ZERO.toString();
									}
									mapTmp.put("parameterValue", parameterValue);
									listData.add(mapTmp);
									break;
								case "QTYLPER":
									List<GenericValue> listLongParams = delegator.findList("FormulaParameter",
											EntityCondition.makeCondition("parameterCode", EntityOperator.EQUALS, "LPERIOD"), null, null, null, false);
									if (!listLongParams.isEmpty()) {
										GenericValue paramLongPeriod = listLongParams.get(0);
										String paramLValue = (String)paramLongPeriod.get("parameterValue");
										BigDecimal numberLDay = new BigDecimal(paramLValue);
										BigDecimal quantityLSales = LogisticsOrderUtil.getQuantitySalesInTimePeriod(delegator, productId, facilityId, numberLDay);
										parameterValue = quantityLSales.toString();
									} else {
										parameterValue = BigDecimal.ZERO.toString();
									}
									
									mapTmp.put("parameterValue", parameterValue);
									listData.add(mapTmp);
									break;
								case "QTYSPER":
									List<GenericValue> listShortParams = delegator.findList("FormulaParameter",
											EntityCondition.makeCondition("parameterCode", EntityOperator.EQUALS, "SPERIOD"), null, null, null, false);
									if (!listShortParams.isEmpty()){
										GenericValue paramShortPeriod = listShortParams.get(0);
										String paramSValue = (String)paramShortPeriod.get("parameterValue");
										BigDecimal numberSDay = new BigDecimal(paramSValue);
										BigDecimal quantitySSales = LogisticsOrderUtil.getQuantitySalesInTimePeriod(delegator, productId, facilityId, numberSDay);
										parameterValue = quantitySSales.toString();
									} else {
										parameterValue = BigDecimal.ZERO.toString();
									}
									mapTmp.put("parameterValue", parameterValue);
									listData.add(mapTmp);
									break;
								default:
									break;
								}
							}
						}
					}
					
					if (!listData.isEmpty()) {
						Map<String, Object> data = FastMap.newInstance(); 
						data.put("userLogin", userLogin);
						data.put("listData", listData);
						try {
							dispatcher.runSync("updateProductFacilityParameterValues", data);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: updateProductFacilityParameterValue error! " + e.toString());
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: updateSystemParameterValueByProductStore error! " + e.toString());
		}
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> autoCalculateAndCreatePurchaseOrder (DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		
		String company = (String)context.get("partyId");
		GenericValue userLogin = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
		
		EntityCondition cond1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, company);
		EntityCondition cond2 = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "OWNER");
		
		List<String> productStoreIds = new ArrayList<String>();
		try {
			List<GenericValue> listProductStoreRoles = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2)), null, null, null, false);
			productStoreIds = EntityUtil.getFieldListFromEntityList(listProductStoreRoles, "productStoreId", true);
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("OLBIUS: get product store error! " + e.toString());
		}
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			if (!productStoreIds.isEmpty()){
				for (String productStoreId : productStoreIds) {
					List<String> facilityIds = new ArrayList<String>();
					facilityIds = LogisticsFacilityUtil.getListFacilityByProductStore(delegator, productStoreId);
					if (!facilityIds.isEmpty()) {
						for (String facilityId : facilityIds) {
							Map<String, Object> map = FastMap.newInstance();
							map.put("facilityId", facilityId);
							map.put("userLogin", userLogin);
							map.put("partyId", company);
							try {
								dispatcher.runSync("createPurchaseOrderAuto", map);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: calculateProductToPurchase error! " + e.toString());
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("OLBIUS: get entity error! " + e.toString());
		}
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createPurchaseOrderAuto (DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = (String)context.get("partyId");
		
		String facilityId = (String)context.get("facilityId");
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		
		// contact mech
		List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
		String contactMechId = null;
		try {
			listContactMechs = LogisticsFacilityUtil.getFacilityContactMechs(delegator, facilityId, "SHIPPING_LOCATION");
			if (!listContactMechs.isEmpty()){
				contactMechId = listContactMechs.get(0).getString("contactMechId");
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: getContactMechs error!");
		}
		
		// supplier product
		String supplierId = null;
		try {
			List<GenericValue> listSuppliers = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", company, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "roleTypeIdTo", "SUPPLIER")), null, null, null, false);
			listSuppliers = EntityUtil.filterByDate(listSuppliers);
			if (!listSuppliers.isEmpty()) {
				supplierId = listSuppliers.get(0).getString("partyIdTo");
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: getContactMechs error!");
		}
		
		// currency
		String currencyUomId = null;
		if (UtilValidate.isNotEmpty(supplierId)) {
			try {
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("userLogin", userLogin);
				mapTmp.put("partyId", supplierId);
				Map<String, Object> mapReturn = dispatcher.runSync("getSupplierCurrencyUom", mapTmp);
				List<GenericValue> listCurrencyUoms = (List<GenericValue>)mapReturn.get("listCurrencyUoms");
				if (!listCurrencyUoms.isEmpty()) currencyUomId = listCurrencyUoms.get(0).getString("uomId");
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: getSupplierCurrencyUom error! " + e.toString());
			}
		}
		
		// time TODO config distance time to ship
		Timestamp shipBeforeDateTmp = UtilDateTime.nowTimestamp();
		Timestamp shipAfterDateTmp = UtilDateTime.nowTimestamp();
		Timestamp now = UtilDateTime.nowTimestamp();
		shipBeforeDateTmp.setTime(now.getTime() + 25*60*60*1000);
		shipAfterDateTmp.setTime(now.getTime() + 24*60*60*1000);
		
		Long shipBeforeDate = shipBeforeDateTmp.getTime();
		Long shipAfterDate = shipAfterDateTmp.getTime();
		
		List<Map<String, Object>> orderItems = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> map = FastMap.newInstance();
			map.put("facilityId", facilityId);
			map.put("formulaTypeId", "CAL_PRODUCT_PURCHASE");
			map.put("userLogin", userLogin);
			Map<String, Object> mapReturn = dispatcher.runSync("calculateProductFacilityByFormula", map);
			
			listProducts = (List<Map<String, Object>>)mapReturn.get("listProducts");
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: calculateProductByFormula error! " + e.toString());
		}
		
		if (!listProducts.isEmpty()){
			for (Map<String, Object> product : listProducts) {
				Map<String, Object> item = FastMap.newInstance();
				String productId = (String)product.get("productId");
				String requireAmount = (String)product.get("requireAmount");
				BigDecimal quantity = (BigDecimal)product.get("quantity");
				item.put("productId", productId);
				item.put("quantity", quantity);
				item.put("quantityUomId", product.get("quantityUomId"));
				item.put("purchaseUomId", product.get("purchaseUomId"));
				item.put("weightUomId", product.get("weightUomId"));
				BigDecimal lastPrice = BigDecimal.ZERO;
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					lastPrice = ProductUtil.getLastPriceBySupplierProductAndQuantity(delegator, productId, supplierId, currencyUomId, (String)product.get("weightUomId"), BigDecimal.ONE);
				} else {
					lastPrice = ProductUtil.getLastPriceBySupplierProductAndQuantity(delegator, productId, supplierId, currencyUomId, (String)product.get("purchaseUomId"), quantity);
				}
				item.put("lastPrice", lastPrice);
				orderItems.add(item);
			}
		}
		
		// init purchase
		
		if (!orderItems.isEmpty()){
			String orderItemStrs = JsonUtil.convertListMapToJSON(orderItems);
			try {
				ShoppingCart cart = null;
				Map<String, Object> resultValue = dispatcher.runSync("initPurchaseOrderService", UtilMisc.toMap(
						"partyIdFrom", supplierId, 
						"orderItems", orderItemStrs,
						"salesMethodChannelEnumId", null, 
						"contactMechId", contactMechId, 
						"currencyUomId", currencyUomId,
						"shipBeforeDate", shipBeforeDate.toString(), 
						"shipAfterDate", shipAfterDate.toString(), 
						"originFacilityId", facilityId,
						"customTimePeriodId", null, 
						"productPlanId", null, 
						"userLogin", userLogin));
				if (ServiceUtil.isError(resultValue)) {
					throw new Exception(ServiceUtil.getErrorMessage(resultValue));
				}
				cart = (ShoppingCart) resultValue.get("shoppingCart");

				CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
				boolean areOrderItemsExploded = CheckOutEvents.explodeOrderItems(delegator, cart);

				String orderId = "";
				Map<String, Object> callResult = checkOutHelper.createOrder(userLogin, null, null, null,
						areOrderItemsExploded, null, null);
				if (!ServiceUtil.isError(callResult)) {
					orderId = (String) callResult.get("orderId");
				} else {
					throw new Exception(ServiceUtil.getErrorMessage(callResult));
				}
				
			} catch (Exception e) {
				return ServiceUtil.returnError("OLBIUS: CreatePurchaseOrder error!");
			} 
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> calculateProductFacilityByFormula (DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		String formulaTypeId = (String)context.get("formulaTypeId");
		
		List<Map<String, Object>> listReturns = new ArrayList<Map<String, Object>>();
		
		List<GenericValue> listProductFacilitys = delegator.findList("ProductFacility",
				EntityCondition.makeCondition("facilityId", facilityId), null, null, null, false);
    	for (GenericValue prFac : listProductFacilitys) {
    		GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", prFac.getString("productId")));
    		
    		String requireAmount = product.getString("requireAmount");
    		String purchaseUomId = product.getString("purchaseUomId");
    		String weightUomId = product.getString("weightUomId");
    		String baseUomId = product.getString("quantityUomId");
    		String productId = product.getString("productId");
    		
    		// get formula for product
    		GenericValue formula = LogisticsProductUtil.getFormulaForProduct(delegator, productId, formulaTypeId);
			if (UtilValidate.isEmpty(formula)) break;
			String formulaValue = formula.getString("formulaValue");
			
			String[] parameterCodes = formulaValue.split("\\+|\\-|\\*|\\/|\\.|\\(|\\)");
			List<String> listCodes = new ArrayList<String>();
			Map<String, Object> mapParamValue = FastMap.newInstance();
			for (String parameterCode : parameterCodes) {
				parameterCode = parameterCode.trim();
				if (UtilValidate.isNotEmpty(parameterCode)) {
					EntityCondition cond1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAR_ACTIVATED");
					EntityCondition cond2 = EntityCondition.makeCondition("parameterCode", EntityOperator.EQUALS, parameterCode);
					List<GenericValue> listParams = delegator.findList("FormulaParameter",
							EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2)), null, null, null, false);
					if (!listParams.isEmpty()) {
						String paramId = listParams.get(0).getString("parameterId");
						listCodes.add(parameterCode);
						GenericValue productFacilityParam = delegator.findOne("ProductFacilityParameter", false, UtilMisc.toMap("productId", productId, "facilityId", facilityId, "parameterId", paramId));
						if (UtilValidate.isNotEmpty(productFacilityParam)) {
							String paramValue = productFacilityParam.getString("parameterValue");
							BigDecimal valueInt = new BigDecimal(paramValue);
							mapParamValue.put(parameterCode, valueInt);
						} else {
							if ("PARAM_SYSTEM".equals(listParams.get(0).getString("parameterTypeId"))) {
								BigDecimal valueInt = BigDecimal.ZERO;
								mapParamValue.put(parameterCode, valueInt);
							} else {
								// found by store
								List<GenericValue> listParamStore = delegator.findList("FormulaParameterProductStore",
										EntityCondition.makeCondition("parameterId", EntityOperator.EQUALS, paramId), null, null, null, false);
								listParamStore = EntityUtil.filterByDate(listParamStore);
								if (!listParamStore.isEmpty()) {
									BigDecimal valueInt = new BigDecimal(listParamStore.get(0).getString("parameterValue"));
									mapParamValue.put(parameterCode, valueInt);
								} else {
									BigDecimal valueInt = new BigDecimal(listParams.get(0).getString("parameterValue"));
									mapParamValue.put(parameterCode, valueInt);
								}
							}
						}
					}
				}
			}
			
			ScriptEngineManager sem = new ScriptEngineManager();
			ScriptEngine engine = sem.getEngineByName("javascript");
			for (String code : listCodes) {
				if (UtilValidate.isNotEmpty(mapParamValue.get(code))) {
					BigDecimal val = (BigDecimal)mapParamValue.get(code);
					engine.put(code, val.intValue());
				}
			}
			BigDecimal quantity = BigDecimal.ZERO;
			try {
				Double formulaResult = (Double) engine.eval(formulaValue);
				quantity = new BigDecimal(formulaResult);
			} catch (ScriptException e) {
				e.printStackTrace();
				quantity = BigDecimal.ZERO;
			}
			quantity = quantity.setScale(0, RoundingMode.HALF_UP);
			if (quantity.compareTo(BigDecimal.ZERO) > 0){
				Map<String, Object> map = FastMap.newInstance();
				map.put("productId", productId);
				map.put("productName", prFac.getString("productName"));
				map.put("productCode", prFac.getString("productCode"));
				map.put("requireAmount", product.getString("requireAmount"));
				map.put("productWeight", product.getString("productWeight"));
				map.put("purchaseUomId", purchaseUomId);
				map.put("quantityUomId", baseUomId);
				map.put("weightUomId", weightUomId);
				
				if (UtilValidate.isNotEmpty(purchaseUomId)){
					if ((UtilValidate.isEmpty(requireAmount) || "N".equals(requireAmount))) {
						BigDecimal convert = ProductUtil.getConvertPackingNumber(delegator, productId, purchaseUomId, baseUomId);
			    		if (UtilValidate.isNotEmpty(quantity) && convert.compareTo(BigDecimal.ZERO) > 0){
		    				BigDecimal x = quantity.divide(convert, RoundingMode.UP);
		    				BigDecimal y = x.setScale(0, RoundingMode.HALF_UP);
		    				if (y.compareTo(BigDecimal.ONE) >= 0){
			    				map.put("quantity", y);
			    			} else {
			    				map.put("quantity", quantity);
			    			}
			    		}
					} else {
						map.put("quantity", quantity);
					}
	    		} else {
	    			map.put("quantity", quantity);
	    		}
				listReturns.add(map);
			}
		}
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("listProducts", listReturns);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createParameterProductStoreMulti (DispatchContext ctx, Map<String, ? extends Object> context){
		String parameterValue = (String)context.get("parameterValue");
		Long fromDate = (Long)context.get("fromDate");
		Long thruDate = (Long)context.get("thruDate");
		
		List<Object> listItemTmp = (List<Object>)context.get("listProductStoreIds");
    	Boolean isJson = false;
    	if (!listItemTmp.isEmpty()){
    		if (listItemTmp.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	List<String> listProductStoreIds = new ArrayList<String>();
    	if (isJson){
    		String stringJson = "["+(String)listItemTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productStoreId")){
					if (!listProductStoreIds.contains(item.getString("productStoreId"))) listProductStoreIds.add(item.getString("productStoreId"));
				}
			}
    	}
    	
    	List<Object> listItemTmp2 = (List<Object>)context.get("listParameterIds");
    	Boolean isJson2 = false;
    	if (!listItemTmp2.isEmpty()){
    		if (listItemTmp2.get(0) instanceof String){
    			isJson2 = true;
    		}
    	}
    	List<String> listParameterIds = new ArrayList<String>();
    	if (isJson2){
    		String stringJson2 = "["+(String)listItemTmp2.get(0)+"]";
			JSONArray lists2 = JSONArray.fromObject(stringJson2);
			for (int i = 0; i < lists2.size(); i++){
				JSONObject item = lists2.getJSONObject(i);
				if (item.containsKey("parameterId")){
					if (!listParameterIds.contains(item.getString("parameterId"))) listParameterIds.add(item.getString("parameterId"));
				}
			}
    	}
    	
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
		if (!listParameterIds.isEmpty() && !listProductStoreIds.isEmpty()) {
			for (String productStoreId : listProductStoreIds) {
				for (String parameterId : listParameterIds) {
					Map<String, Object> mapCreateParamStore = FastMap.newInstance();
					mapCreateParamStore.put("parameterId", parameterId);
					mapCreateParamStore.put("productStoreId", productStoreId);
					if (UtilValidate.isNotEmpty(fromDate)) {
						mapCreateParamStore.put("fromDate", fromDate);
					}
					if (UtilValidate.isNotEmpty(thruDate)) {
						mapCreateParamStore.put("thruDate", thruDate);
					}
					mapCreateParamStore.put("parameterValue", parameterValue);
					mapCreateParamStore.put("userLogin", userLogin);
					try {
						dispatcher.runSync("createFormulaParameterProductStore", mapCreateParamStore);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: createFormulaParameterProductStore error! " + e.toString());
					}
				}
			}
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createFormulaProductMulti (DispatchContext ctx, Map<String, ? extends Object> context){
		Long fromDate = (Long)context.get("fromDate");
		Long thruDate = (Long)context.get("thruDate");
		
		List<Object> listItemTmp = (List<Object>)context.get("listProductIds");
    	Boolean isJson = false;
    	if (!listItemTmp.isEmpty()){
    		if (listItemTmp.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	List<String> listProductIds = new ArrayList<String>();
    	if (isJson){
    		String stringJson = "["+(String)listItemTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")){
					if (!listProductIds.contains(item.getString("productId"))) listProductIds.add(item.getString("productId"));
				}
			}
    	}
    	
    	List<Object> listItemTmp2 = (List<Object>)context.get("listFormulaIds");
    	Boolean isJson2 = false;
    	if (!listItemTmp2.isEmpty()){
    		if (listItemTmp2.get(0) instanceof String){
    			isJson2 = true;
    		}
    	}
    	List<String> listFormulaIds = new ArrayList<String>();
    	if (isJson2){
    		String stringJson2 = "["+(String)listItemTmp2.get(0)+"]";
			JSONArray lists2 = JSONArray.fromObject(stringJson2);
			for (int i = 0; i < lists2.size(); i++){
				JSONObject item = lists2.getJSONObject(i);
				if (item.containsKey("formulaId")){
					if (!listFormulaIds.contains(item.getString("formulaId"))) listFormulaIds.add(item.getString("formulaId"));
				}
			}
    	}
    	
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
		if (!listFormulaIds.isEmpty() && !listProductIds.isEmpty()) {
			for (String productId : listProductIds) {
				for (String formulaId : listFormulaIds) {
					Map<String, Object> mapCreate = FastMap.newInstance();
					mapCreate.put("formulaId", formulaId);
					mapCreate.put("productId", productId);
					if (UtilValidate.isNotEmpty(fromDate)) {
						mapCreate.put("fromDate", fromDate);
					}
					if (UtilValidate.isNotEmpty(thruDate)) {
						mapCreate.put("thruDate", thruDate);
					}
					mapCreate.put("userLogin", userLogin);
					try {
						dispatcher.runSync("createFormulaProduct", mapCreate);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: createFormulaParameterProductStore error! " + e.toString());
					}
				}
			}
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
}