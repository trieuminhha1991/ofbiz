package com.olbius.salesmtl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.util.SecurityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

public class SalesStatementServices {
	public static final String module = SalesStatementServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
	
    public static Map<String, Object> removeStatementOfDisabledNPP(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String parentSalesStatementId = (String) context.get("salesStatementId");
		
		try {
			if (UtilValidate.isNotEmpty(parentSalesStatementId)) {
				List<GenericValue> salesStatements = delegator.findList("SalesStatement", EntityCondition.makeCondition("parentSalesStatementId", parentSalesStatementId), null, null, null, false);
				for ( GenericValue salesStatement: salesStatements){
					String partyId = (String) salesStatement.get("internalPartyId");
					GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
					if (party.get("statusId").equals("PARTY_DISABLED")){
						List<GenericValue> statementDetails = delegator.findList("SalesStatementDetail", EntityCondition.makeCondition("salesStatementId", salesStatement.get("salesStatementId")), null, null, null, false);
						for (GenericValue item : statementDetails){
							item.remove();
						}
						salesStatement.remove();
					}
				}
					
			}

		} catch (Exception e) {
			String errMsg = "Fatal error calling createSalesStatementCustom service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("salesStatementId", parentSalesStatementId);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSalesStatementType(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
			listIterator = delegator.find("SalesStatementType", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesStatementType service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListSalesStatement(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		try {
			//check permission
			if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALESSTATEMENT_VIEW")) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " don't have view permission!", module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSYouHavenotViewPermission", locale));
			}
			String tid = null;
			if (parameters.containsKey("tid") && parameters.get("tid").length > 0) {
				tid = parameters.get("tid")[0];
			}
			if (UtilValidate.isNotEmpty(tid)) {
				listAllConditions.add(EntityCondition.makeCondition("salesStatementTypeId", tid));
				List<String> periodTypeIds = SalesUtil.getCurrentCustomTimePeriodTypeSales(delegator);
				// [old code: get parent and children] if (periodTypeIds != null) listAllConditions.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, periodTypeIds));
				if (periodTypeIds != null) {
					listAllConditions.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, periodTypeIds));
					listAllConditions.add(EntityCondition.makeCondition("parentSalesStatementId", null));
				}
				listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"))));
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-fromDate");
					listSortFields.add("-thruDate");
				}
				listIterator = delegator.find("SalesStatementAndCustomTimePeriod", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesStatement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> createSalesStatement(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		//check permission
		if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALESSTATEMENT_NEW")) {
			Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " don't have create permission!", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSYouHavenotCreatePermission", locale));
		}
		
		String salesStatementId = (String) context.get("salesStatementId");
		String organizationPartyId = (String) context.get("organizationPartyId");
		String internalPartyId = (String) context.get("internalPartyId");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		/*String parentSalesForecastId = (String) context.get("parentSalesForecastId");
		String quotaAmount = (String) context.get("quotaAmount");
		String forecastAmount = (String) context.get("forecastAmount");
		String bestCastAmount = (String) context.get("bestCastAmount");
		String closedAmount = (String) context.get("closedAmount");
		String percentOfQuotaForecast = (String) context.get("percentOfQuotaForecast");
		String percentOfQuotaClosed = (String) context.get("percentOfQuotaClosed");
		String pinelineAmount = (String) context.get("pinelineAmount");*/
		String salesStatementTypeId = (String) context.get("salesStatementTypeId");
		String parentSalesStatementId = (String) context.get("parentSalesStatementId");
		String salesStatementName = (String) context.get("salesStatementName");
		String currencyUomId = (String) context.get("currencyUomId");
		String salesForecastId = (String) context.get("salesForecastId");
		
		try {
			if (UtilValidate.isNotEmpty(salesStatementId)) {
				GenericValue sf = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", salesStatementId), false);
				if (sf != null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSThisRecordIsAlreadyExists", locale));
			}
			if (UtilValidate.isEmpty(organizationPartyId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSOrganizationIdMustNotBeEmpty", locale));
			}
			/*if (UtilValidate.isEmpty(internalPartyId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSInternalPartyIdMustNotBeEmpty", locale));
			}*/
			if (UtilValidate.isEmpty(customTimePeriodId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSSalesCustomTimePeriodIdMustNotBeEmpty", locale));
			}
			
			if (UtilValidate.isEmpty(internalPartyId)) internalPartyId = organizationPartyId;
		
			List<EntityCondition> findCondsSMT = FastList.newInstance();
			findCondsSMT.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
			findCondsSMT.add(EntityCondition.makeCondition("internalPartyId", internalPartyId));
			findCondsSMT.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
			findCondsSMT.add(EntityCondition.makeCondition("salesStatementTypeId", salesStatementTypeId));
			List<GenericValue> listSMT = delegator.findList("SalesStatement", EntityCondition.makeCondition(findCondsSMT, EntityOperator.AND), null, null, null, false);
			if (UtilValidate.isNotEmpty(listSMT)) {
				String errorStr = UtilProperties.getMessage(resource_error, "BSThisRecordIsAlreadyExists", locale);
				return ServiceUtil.returnError(errorStr);
			}
			
			if (UtilValidate.isEmpty(salesStatementId)) salesStatementId = delegator.getNextSeqId("SalesStatement");
			GenericValue salesStatement = delegator.makeValue("SalesStatement");
			salesStatement.set("salesStatementId", salesStatementId);
			salesStatement.set("salesStatementTypeId", salesStatementTypeId);
			salesStatement.setNonPKFields(context);
			salesStatement.set("internalPartyId", internalPartyId);
			salesStatement.set("createdBy", userLogin.get("userLoginId"));
			salesStatement.set("modifiedBy", userLogin.get("userLoginId"));
			salesStatement.set("statusId", "SALES_SM_CREATED");
			salesStatement.set("parentSalesStatementId", parentSalesStatementId);
			salesStatement.set("salesStatementName", salesStatementName);
			salesStatement.set("currencyUomId", currencyUomId);
			salesStatement.set("salesForecastId", salesForecastId);
			salesStatement.create();
		} catch (Exception e) {
			String errMsg = "Fatal error calling createSalesStatementCustom service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("salesStatementId", salesStatementId);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetOrganizationUnitManager(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		//Locale locale = (Locale) context.get("locale");
		//Security security = ctx.getSecurity();
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		//List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		//List<String> listSortFields = (List<String>) context.get("listSortFields");
		//EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		try {
			GenericValue salesStatement = null;
			if (parameters.containsKey("salesStatementId") && parameters.get("salesStatementId").length > 0) {
				String salesStatementId = parameters.get("salesStatementId")[0];
				salesStatement = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", salesStatementId), false);
			}
			
			String productIdsStr = null;
			if (parameters.containsKey("productIds") && parameters.get("productIds").length > 0) {
				productIdsStr = parameters.get("productIds")[0];
			}
			
			List<String> productIds = new ArrayList<String>();
			if (productIdsStr != null) {
				JSONArray jsonArray = new JSONArray();
				if (UtilValidate.isNotEmpty(productIdsStr)) {
					jsonArray = JSONArray.fromObject(productIdsStr);
				}
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0; i < jsonArray.size(); i++) {
						productIds.add(jsonArray.getString(i));
					}
				}
			}
			
			if (UtilValidate.isNotEmpty(salesStatement)) {
				List<GenericValue> listProduct = ProductWorker.getListProduct(delegator, productIds);
				List<String> deptIds = com.olbius.basehr.util.PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"));
				if (UtilValidate.isNotEmpty(deptIds)) {
					for (String deptId : deptIds) {
						Map<String, Object> tempMap = FastMap.newInstance();
						Organization buildOrg = PartyUtil.buildOrg(delegator, deptId, false, false);
						Map<String, Object> rootNbrEmplMap = getNbrEmplOfOrganization(dispatcher, delegator, userLogin, buildOrg, listIterator, salesStatement, listProduct);
						int rootNbrEmpl = (Integer) rootNbrEmplMap.get("childNbrEmpl");
						tempMap.put("partyId", deptId);
						tempMap.put("partyCode", deptId);
						tempMap.put("partyName", PartyHelper.getPartyName(delegator, deptId, false));
						tempMap.put("partyIdFrom", "-1");
						tempMap.put("totalEmployee", rootNbrEmpl);
						tempMap.put("expanded", true);
						listIterator.add(tempMap);
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetOrganizationUnitManager service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNbrEmplOfOrganization(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, Organization parentOrg, 
			List<Map<String, Object>> list, GenericValue salesStatement, List<GenericValue> listProduct) throws GenericEntityException, GenericServiceException {
		List<GenericValue> orgDirectChild = parentOrg.getDirectChildList(delegator);
		List<String> productIds = EntityUtil.getFieldListFromEntityList(listProduct, "productId", true);
		//List<GenericValue> employeeDirect = parentOrg.getDirectEmployee(delegator);
		int parentNumberEmpl = 0; //employeeDirect.size();
		Map<String, Object> returnMap = FastMap.newInstance();
		
		String parentPartyId = parentOrg.getOrg().getString("partyId");
		List<String> internalPartyIds = new ArrayList<String>();
		if ("SALES_IN".equals(salesStatement.getString("salesStatementTypeId"))) {
			// find distributors
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyIdFrom", parentPartyId));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "SALESSUP_DEPT"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "DISTRIBUTOR"));
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "DISTRIBUTION"));
			conds.add(EntityUtil.getFilterByDateExpr());
			internalPartyIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
		} else if ("SALES_OUT".equals(salesStatement.getString("salesStatementTypeId"))) {
			// find sales executive
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyIdTo", parentPartyId));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "SALES_EXECUTIVE"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYEE"));
			conds.add(EntityUtil.getFilterByDateExpr());
			internalPartyIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds, EntityOperator.AND), UtilMisc.toSet("partyIdFrom"), null, null, false), "partyIdFrom", true);
		}
		
		EntityCondition coreCond = EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), EntityOperator.AND, EntityCondition.makeCondition("parentSalesStatementId", salesStatement.get("salesStatementId")));
		if (UtilValidate.isNotEmpty(internalPartyIds)) {
			for (String distributorId : internalPartyIds) {
				Map<String, Object> tempMap2 = FastMap.newInstance();
				tempMap2.put("partyId", distributorId);
				tempMap2.put("partyIdFrom", parentPartyId);
				tempMap2.put("partyName", PartyHelper.getPartyName(delegator, distributorId, false));
				tempMap2.put("isPerson", "Y");
				tempMap2.put("totalEmployee", null);
				for (GenericValue product : listProduct) {
					tempMap2.put("prodCode_" + product.getString("productId"), null);
				}
				
				List<GenericValue> salesStatementDetailItems = delegator.findList("SalesStatementItemDetail", 
						EntityCondition.makeCondition(coreCond, EntityOperator.AND, EntityCondition.makeCondition("internalPartyId", distributorId)), null, null, null, false);
				if (UtilValidate.isNotEmpty(salesStatementDetailItems)) {
					for (GenericValue salesStatementItem : salesStatementDetailItems) {
						String productId = salesStatementItem.getString("productId");
						String productCategoryId = salesStatementItem.getString("productCategoryId");
						String id = productId + "@" + productCategoryId;
						BigDecimal quantity = salesStatementItem.getBigDecimal("quantity");
						BigDecimal amount = salesStatementItem.getBigDecimal("amount");
						BigDecimal actualQuantity = salesStatementItem.getBigDecimal("actualQuantity");
						BigDecimal actualAmount = salesStatementItem.getBigDecimal("actualAmount");
						if (returnMap.containsKey(id)) {
							Map<String, Object> itemValue = (Map<String, Object>) returnMap.get(id);
							if (itemValue.get("quantity") != null) quantity = quantity.add((BigDecimal) itemValue.get("quantity"));
							if (itemValue.get("amount") != null) amount = amount.add((BigDecimal) itemValue.get("amount"));
							if (itemValue.get("actualQuantity") != null) actualQuantity = actualQuantity.add((BigDecimal) itemValue.get("actualQuantity"));
							if (itemValue.get("actualAmount") != null) actualAmount = actualAmount.add((BigDecimal) itemValue.get("actualAmount"));
							
							itemValue.put("quantity", quantity);
							itemValue.put("amount", amount);
							itemValue.put("actualQuantity", actualQuantity);
							itemValue.put("actualAmount", actualAmount);
						} else {
							Map<String, Object> itemValue = FastMap.newInstance();
							itemValue.put("productId", productId);
							itemValue.put("productCategoryId", productCategoryId);
							itemValue.put("quantity", quantity);
							itemValue.put("amount", amount);
							itemValue.put("actualQuantity", actualQuantity);
							itemValue.put("actualAmount", actualAmount);
							returnMap.put(id, itemValue);
						}
						/*tempMap2.put("productId", productId);
						tempMap2.put("productCategoryId", productCategoryId);*/
						tempMap2.put("prodCode_" + productId, salesStatementItem.getBigDecimal("quantity"));
						/*tempMap2.put("quantityUomId", salesStatementItem.get("quantityUomId"));
						tempMap2.put("amount", amount);
						tempMap2.put("actualQuantity", actualQuantity);
						tempMap2.put("actualAmount", actualAmount);*/
					}
				}
				
				list.add(tempMap2);
			}
			parentNumberEmpl += internalPartyIds.size();
		}
		if (UtilValidate.isNotEmpty(orgDirectChild)){
			for(GenericValue child: orgDirectChild){
				Map<String, Object> tempMap = FastMap.newInstance();
				String childPartyId = child.getString("partyId");
				tempMap.put("partyId", childPartyId);
				tempMap.put("partyIdFrom", parentPartyId);
				tempMap.put("partyName", PartyHelper.getPartyName(delegator, childPartyId, false));
				for (GenericValue product : listProduct) {
					tempMap.put("prodCode_" + product.getString("productId"), null);
					tempMap.put("actual_" + product.getString("productId"), null);
					tempMap.put("percent_" + product.getString("productId"), null);
				}
				Organization orgChild = PartyUtil.buildOrg(delegator, childPartyId, false, false);
				Map<String, Object> childNbrEmplMap = getNbrEmplOfOrganization(dispatcher, delegator, userLogin, orgChild, list, salesStatement, listProduct);
				int childNbrEmpl = (Integer) childNbrEmplMap.get("childNbrEmpl");
				parentNumberEmpl += childNbrEmpl;
				tempMap.put("totalEmployee", childNbrEmpl);
				tempMap.put("isPerson", "N");
				list.add(tempMap);
			}
		}
		returnMap.put("childNbrEmpl", parentNumberEmpl);
		return returnMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetOrganizationUnitManager2(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		try {
			GenericValue salesStatement = null;
			if (parameters.containsKey("salesStatementId") && parameters.get("salesStatementId").length > 0) {
				String salesStatementId = parameters.get("salesStatementId")[0];
				salesStatement = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", salesStatementId), false);
			}
			
			String productIdsStr = null;
			if (parameters.containsKey("productIds") && parameters.get("productIds").length > 0) {
				productIdsStr = parameters.get("productIds")[0];
			}
			
			List<String> productIds = new ArrayList<String>();
			if (productIdsStr != null) {
				JSONArray jsonArray = new JSONArray();
				if (UtilValidate.isNotEmpty(productIdsStr)) {
					jsonArray = JSONArray.fromObject(productIdsStr);
				}
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0; i < jsonArray.size(); i++) {
						productIds.add(jsonArray.getString(i));
					}
				}
			}
			
			if (UtilValidate.isNotEmpty(salesStatement)) {
				List<GenericValue> listProduct = ProductWorker.getListProduct(delegator, productIds);
				List<String> deptIds = com.olbius.basehr.util.PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"));
				if (UtilValidate.isNotEmpty(deptIds)) {
					for (String deptId : deptIds) {
						Organization buildOrg = PartyUtil.buildOrg(delegator, deptId, false, false);
						getNbrEmplOfOrganization2(dispatcher, delegator, userLogin, buildOrg, listIterator, salesStatement, listProduct);
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetOrganizationUnitManager service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("TotalRows", String.valueOf(listIterator.size()));
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNbrEmplOfOrganization2(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, Organization parentOrg, 
			List<Map<String, Object>> list, GenericValue salesStatement, List<GenericValue> listProduct) throws GenericEntityException, GenericServiceException {
		List<GenericValue> orgDirectChild = parentOrg.getDirectChildList(delegator);
		List<String> productIds = EntityUtil.getFieldListFromEntityList(listProduct, "productId", true);
		//List<GenericValue> employeeDirect = parentOrg.getDirectEmployee(delegator);
		//int parentNumberEmpl = 0; //employeeDirect.size();
		Map<String, Object> returnMap = FastMap.newInstance();
		
		String parentPartyId = parentOrg.getOrg().getString("partyId");
		List<String> internalPartyIds = new ArrayList<String>();
		if ("SALES_IN".equals(salesStatement.getString("salesStatementTypeId"))) {
			// find distributors
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyIdFrom", parentPartyId));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "SALESSUP_DEPT"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "DISTRIBUTOR"));
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "DISTRIBUTION"));
			conds.add(EntityUtil.getFilterByDateExpr());
			internalPartyIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
		} else if ("SALES_OUT".equals(salesStatement.getString("salesStatementTypeId"))) {
			// find sales executive
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyIdTo", parentPartyId));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "SALES_EXECUTIVE"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYEE"));
			conds.add(EntityUtil.getFilterByDateExpr());
			internalPartyIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds, EntityOperator.AND), UtilMisc.toSet("partyIdFrom"), null, null, false), "partyIdFrom", true);
		}
		
		EntityCondition coreCond = EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), EntityOperator.AND, EntityCondition.makeCondition("parentSalesStatementId", salesStatement.get("salesStatementId")));
		if (UtilValidate.isNotEmpty(internalPartyIds)) {
			for (String distributorId : internalPartyIds) {
				GenericValue partyFullName = EntityUtil.getFirst(delegator.findByAnd("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", distributorId), null, false));
				
				Map<String, Object> tempMap2 = FastMap.newInstance();
				tempMap2.put("partyId", distributorId);
				tempMap2.put("statusId", partyFullName.get("statusId"));
				tempMap2.put("partyIdFrom", parentPartyId);
				tempMap2.put("partyCode", partyFullName != null ? partyFullName.getString("partyCode") : "");
				tempMap2.put("partyName", partyFullName != null ? partyFullName.getString("fullName") : "");
				//tempMap2.put("partyName", PartyHelper.getPartyName(delegator, distributorId, false));
				tempMap2.put("isPerson", "Y");
				tempMap2.put("totalEmployee", null);
				for (GenericValue product : listProduct) {
					tempMap2.put("prodCode_" + product.getString("productId"), null);
				}
				
				List<GenericValue> salesStatementDetailItems = delegator.findList("SalesStatementItemDetail", 
						EntityCondition.makeCondition(coreCond, EntityOperator.AND, EntityCondition.makeCondition("internalPartyId", distributorId)), null, null, null, false);
				if (UtilValidate.isNotEmpty(salesStatementDetailItems)) {
					for (GenericValue salesStatementItem : salesStatementDetailItems) {
						String productId = salesStatementItem.getString("productId");
						String productCategoryId = salesStatementItem.getString("productCategoryId");
						String id = productId + "@" + productCategoryId;
						BigDecimal quantity = salesStatementItem.getBigDecimal("quantity");
						BigDecimal amount = salesStatementItem.getBigDecimal("amount");
						BigDecimal actualQuantity = salesStatementItem.getBigDecimal("actualQuantity");
						BigDecimal actualAmount = salesStatementItem.getBigDecimal("actualAmount");
						if (returnMap.containsKey(id)) {
							Map<String, Object> itemValue = (Map<String, Object>) returnMap.get(id);
							if (itemValue.get("quantity") != null) quantity = quantity.add((BigDecimal) itemValue.get("quantity"));
							if (itemValue.get("amount") != null) amount = amount.add((BigDecimal) itemValue.get("amount"));
							if (itemValue.get("actualQuantity") != null) actualQuantity = actualQuantity.add((BigDecimal) itemValue.get("actualQuantity"));
							if (itemValue.get("actualAmount") != null) actualAmount = actualAmount.add((BigDecimal) itemValue.get("actualAmount"));
							
							itemValue.put("quantity", quantity);
							itemValue.put("amount", amount);
							itemValue.put("actualQuantity", actualQuantity);
							itemValue.put("actualAmount", actualAmount);
						} else {
							Map<String, Object> itemValue = FastMap.newInstance();
							itemValue.put("productId", productId);
							itemValue.put("productCategoryId", productCategoryId);
							itemValue.put("quantity", quantity);
							itemValue.put("amount", amount);
							itemValue.put("actualQuantity", actualQuantity);
							itemValue.put("actualAmount", actualAmount);
							returnMap.put(id, itemValue);
						}
						/*tempMap2.put("productId", productId);
						tempMap2.put("productCategoryId", productCategoryId);*/
						tempMap2.put("prodCode_" + productId, salesStatementItem.getBigDecimal("quantity"));
						/*tempMap2.put("quantityUomId", salesStatementItem.get("quantityUomId"));
						tempMap2.put("amount", amount);
						tempMap2.put("actualQuantity", actualQuantity);
						tempMap2.put("actualAmount", actualAmount);*/
					}
				}
				
				list.add(tempMap2);
			}
			//parentNumberEmpl += internalPartyIds.size();
		}
		if (UtilValidate.isNotEmpty(orgDirectChild)){
			for(GenericValue child: orgDirectChild){
				String childPartyId = child.getString("partyId");
				/*Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("partyId", childPartyId);
				tempMap.put("partyIdFrom", parentPartyId);
				tempMap.put("partyName", PartyHelper.getPartyName(delegator, childPartyId, false));
				for (GenericValue product : listProduct) {
					tempMap.put("prodCode_" + product.getString("productId"), null);
					tempMap.put("actual_" + product.getString("productId"), null);
					tempMap.put("percent_" + product.getString("productId"), null);
				}*/
				Organization orgChild = PartyUtil.buildOrg(delegator, childPartyId, false, false);
				getNbrEmplOfOrganization2(dispatcher, delegator, userLogin, orgChild, list, salesStatement, listProduct);
				//int childNbrEmpl = (Integer) childNbrEmplMap.get("childNbrEmpl");
				//parentNumberEmpl += childNbrEmpl;
				/*tempMap.put("totalEmployee", childNbrEmpl);
				tempMap.put("isPerson", "N");
				list.add(tempMap);*/
			}
		}
		//returnMap.put("childNbrEmpl", parentNumberEmpl);
		return returnMap;
	}
	
	// REPORT
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetOrganizationUnitManagerReport(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		//Locale locale = (Locale) context.get("locale");
		//Security security = ctx.getSecurity();
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		//List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		//List<String> listSortFields = (List<String>) context.get("listSortFields");
		//EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		try {
			GenericValue salesStatement = null;
			if (parameters.containsKey("salesStatementId") && parameters.get("salesStatementId").length > 0) {
				String salesStatementId = parameters.get("salesStatementId")[0];
				salesStatement = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", salesStatementId), false);
			}
			
			String productIdsStr = null;
			if (parameters.containsKey("productIds") && parameters.get("productIds").length > 0) {
				productIdsStr = parameters.get("productIds")[0];
			}
			
			List<String> productIds = new ArrayList<String>();
			if (productIdsStr != null) {
				JSONArray jsonArray = new JSONArray();
				if (UtilValidate.isNotEmpty(productIdsStr)) {
					jsonArray = JSONArray.fromObject(productIdsStr);
				}
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0; i < jsonArray.size(); i++) {
						productIds.add(jsonArray.getString(i));
					}
				}
			}
			
			if (UtilValidate.isNotEmpty(salesStatement)) {
				List<GenericValue> listProduct = ProductWorker.getListProduct(delegator, productIds);
				
				if ("SALES_IN".equals(salesStatement.getString("salesStatementTypeId"))) {
					GenericValue salesForecastParent = EntityUtil.getFirst(
							delegator.findByAnd("SalesForecast", UtilMisc.toMap("organizationPartyId", salesStatement.get("organizationPartyId"), 
									"internalPartyId", salesStatement.get("internalPartyId"), "customTimePeriodId", salesStatement.get("customTimePeriodId")), null, false));
					if (salesForecastParent != null) {
						List<GenericValue> salesForecastDetail = delegator.findByAnd("SalesForecastDetail", UtilMisc.toMap("salesForecastId", salesForecastParent.get("salesForecastId")), null, false);
						
						Map<String, Object> tempMap0 = FastMap.newInstance();
						for (GenericValue product : listProduct) {
							String productId = product.getString("productId");
							BigDecimal quantity = null;
							BigDecimal actualQuantity = null;
							BigDecimal percentSales = null;
							
							GenericValue sales4cItem = EntityUtil.getFirst(EntityUtil.filterByAnd(salesForecastDetail, UtilMisc.toMap("productId", productId)));
							if (sales4cItem != null) {
								quantity = sales4cItem.getBigDecimal("quantity");
							}
							
							tempMap0.put("prodCode_" + product.getString("productId"), quantity);
							tempMap0.put("actual_" + product.getString("productId"), actualQuantity);
							tempMap0.put("percent_" + product.getString("productId"), percentSales);
						}
						
						tempMap0.put("partyId", salesStatement.getString("internalPartyId"));
						tempMap0.put("partyCode", salesStatement.getString("internalPartyId"));
						tempMap0.put("partyName", PartyHelper.getPartyName(delegator, salesStatement.getString("internalPartyId"), false) + " (Sales Forecast)");
						tempMap0.put("partyIdFrom", "-1");
						tempMap0.put("totalEmployee", null);
						tempMap0.put("levelTree", -1);
						tempMap0.put("expanded", true);
						listIterator.add(tempMap0);
					}
				}
				
				List<String> deptIds = com.olbius.basehr.util.PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"));
				if (UtilValidate.isNotEmpty(deptIds)) {
					for (String deptId : deptIds) {
						Map<String, Object> tempMap = FastMap.newInstance();
						Organization buildOrg = PartyUtil.buildOrg(delegator, deptId, false, false);
						int levelTree = 1;
						Map<String, Object> rootNbrEmplMap = getNbrEmplOfOrganizationReport(dispatcher, delegator, userLogin, buildOrg, listIterator, salesStatement, listProduct, levelTree);

						String roundSales = SalesUtil.getPropertyValue(delegator, "round.percent.sales.statement");
						Map<String, Object> targetMap2 = (Map<String, Object>) rootNbrEmplMap.get("targetMap");
						Map<String, Object> actualMap2 = (Map<String, Object>) rootNbrEmplMap.get("actualMap");
						for (GenericValue product : listProduct) {
							String productId = product.getString("productId");
							BigDecimal quantity = (BigDecimal) targetMap2.get("prodCode_" + productId);
							BigDecimal actualQuantity = (BigDecimal) actualMap2.get("actual_" + productId);
							BigDecimal percentSales = BigDecimal.ZERO;
							if (actualQuantity != null && quantity != null) percentSales = actualQuantity.divide(quantity, Integer.parseInt(roundSales) + 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
							
							tempMap.put("prodCode_" + product.getString("productId"), quantity);
							tempMap.put("actual_" + product.getString("productId"), actualQuantity);
							tempMap.put("percent_" + product.getString("productId"), percentSales);
						}
						
						int rootNbrEmpl = (Integer) rootNbrEmplMap.get("childNbrEmpl");
						tempMap.put("partyId", deptId);
						tempMap.put("partyCode", deptId);
						tempMap.put("partyName", PartyHelper.getPartyName(delegator, deptId, false));
						tempMap.put("partyIdFrom", "-1");
						tempMap.put("totalEmployee", rootNbrEmpl);
						tempMap.put("levelTree", 0);
						tempMap.put("expanded", true);
						listIterator.add(tempMap);
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetOrganizationUnitManager service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNbrEmplOfOrganizationReport(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, Organization parentOrg, 
			List<Map<String, Object>> list, GenericValue salesStatement, List<GenericValue> listProduct, Integer levelTree) throws GenericEntityException, GenericServiceException {
		List<GenericValue> orgDirectChild = parentOrg.getDirectChildList(delegator);
		List<String> productIds = EntityUtil.getFieldListFromEntityList(listProduct, "productId", true);
		//List<GenericValue> employeeDirect = parentOrg.getDirectEmployee(delegator);
		int parentNumberEmpl = 0; //employeeDirect.size();
		Map<String, Object> returnMap = FastMap.newInstance();
		String roundSales = SalesUtil.getPropertyValue(delegator, "round.percent.sales.statement");
		
		String parentPartyId = parentOrg.getOrg().getString("partyId");
		List<String> internalPartyIds = new ArrayList<String>();
		if ("SALES_IN".equals(salesStatement.getString("salesStatementTypeId"))) {
			// find distributors
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyIdFrom", parentPartyId));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "SALESSUP_DEPT"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "DISTRIBUTOR"));
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "DISTRIBUTION"));
			conds.add(EntityUtil.getFilterByDateExpr());
			internalPartyIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
		} else if ("SALES_OUT".equals(salesStatement.getString("salesStatementTypeId"))) {
			// find sales executive
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyIdTo", parentPartyId));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "SALES_EXECUTIVE"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYEE"));
			conds.add(EntityUtil.getFilterByDateExpr());
			internalPartyIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds, EntityOperator.AND), UtilMisc.toSet("partyIdFrom"), null, null, false), "partyIdFrom", true);
		}
		Map<String, Object> targetMapTmp = FastMap.newInstance();
		Map<String, Object> actualMapTmp = FastMap.newInstance();
		EntityCondition coreCond = EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), EntityOperator.AND, EntityCondition.makeCondition("parentSalesStatementId", salesStatement.get("salesStatementId")));
		if (UtilValidate.isNotEmpty(internalPartyIds)) {
			for (String distributorId : internalPartyIds) {
				GenericValue partyFullName = EntityUtil.getFirst(delegator.findByAnd("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", distributorId), null, false));
				Map<String, Object> tempMap2 = FastMap.newInstance();
				tempMap2.put("partyId", distributorId);
				tempMap2.put("partyIdFrom", parentPartyId);
				tempMap2.put("partyCode", partyFullName != null ? partyFullName.getString("partyCode") : "");
				tempMap2.put("partyName", partyFullName != null ? partyFullName.getString("fullName") : "");
				//tempMap.put("partyName", PartyHelper.getPartyName(delegator, distributorId, false));
				tempMap2.put("isPerson", "Y");
				tempMap2.put("totalEmployee", null);
				tempMap2.put("levelTree", levelTree);
				for (GenericValue product : listProduct) {
					tempMap2.put("prodCode_" + product.getString("productId"), null);
					tempMap2.put("actual_" + product.getString("productId"), null);
					tempMap2.put("percent_" + product.getString("productId"), null);
				}
				
				List<GenericValue> salesStatementDetailItems = delegator.findList("SalesStatementItemDetail", 
						EntityCondition.makeCondition(coreCond, EntityOperator.AND, EntityCondition.makeCondition("internalPartyId", distributorId)), null, null, null, false);
				if (UtilValidate.isNotEmpty(salesStatementDetailItems)) {
					for (GenericValue salesStatementItem : salesStatementDetailItems) {
						String productId = salesStatementItem.getString("productId");
						//String productCategoryId = salesStatementItem.getString("productCategoryId");
						BigDecimal quantity = salesStatementItem.getBigDecimal("quantity");
						//BigDecimal amount = salesStatementItem.getBigDecimal("amount");
						BigDecimal actualQuantity = salesStatementItem.getBigDecimal("actualQuantity");
						//BigDecimal actualAmount = salesStatementItem.getBigDecimal("actualAmount");
						tempMap2.put("prodCode_" + productId, quantity);
						tempMap2.put("actual_" + productId, actualQuantity);
						BigDecimal percentSales = BigDecimal.ZERO;
						if (actualQuantity != null && quantity != null) percentSales = actualQuantity.divide(quantity, Integer.parseInt(roundSales) + 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
						tempMap2.put("percent_" + productId, percentSales);
						
						if (targetMapTmp.containsKey("prodCode_" + productId) && targetMapTmp.get("prodCode_" + productId) != null) {
							BigDecimal targetBig = (BigDecimal) targetMapTmp.get("prodCode_" + productId);
							targetBig = targetBig.add(quantity);
							targetMapTmp.put("prodCode_" + productId, targetBig);
						} else {
							targetMapTmp.put("prodCode_" + productId, quantity);
						}
						if (actualMapTmp.containsKey("actual_" + productId) && actualMapTmp.get("actual_" + productId) != null) {
							BigDecimal actualBig = (BigDecimal) actualMapTmp.get("actual_" + productId);
							actualBig = actualBig.add(actualQuantity);
							actualMapTmp.put("actual_" + productId, actualBig);
						} else {
							actualMapTmp.put("actual_" + productId, actualQuantity);
						}
					}
				}
				
				list.add(tempMap2);
			}
			parentNumberEmpl += internalPartyIds.size();
		}
		if (UtilValidate.isNotEmpty(orgDirectChild)){
			for(GenericValue child: orgDirectChild){
				String childPartyId = child.getString("partyId");
				GenericValue partyFullName = EntityUtil.getFirst(delegator.findByAnd("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", childPartyId), null, false));
				
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("partyId", childPartyId);
				tempMap.put("partyIdFrom", parentPartyId);
				tempMap.put("partyCode", partyFullName != null ? partyFullName.getString("partyCode") : "");
				tempMap.put("partyName", partyFullName != null ? partyFullName.getString("fullName") : "");
				//tempMap.put("partyName", PartyHelper.getPartyName(delegator, childPartyId, false));
				tempMap.put("levelTree", levelTree);
				Organization orgChild = PartyUtil.buildOrg(delegator, childPartyId, false, false);
				Map<String, Object> childNbrEmplMap = getNbrEmplOfOrganizationReport(dispatcher, delegator, userLogin, orgChild, list, salesStatement, listProduct, levelTree + 1);
				Map<String, Object> targetMap2 = (Map<String, Object>) childNbrEmplMap.get("targetMap");
				Map<String, Object> actualMap2 = (Map<String, Object>) childNbrEmplMap.get("actualMap");
				for (GenericValue product : listProduct) {
					String productId = product.getString("productId");
					BigDecimal quantity = (BigDecimal) targetMap2.get("prodCode_" + productId);
					BigDecimal actualQuantity = (BigDecimal) actualMap2.get("actual_" + productId);
					if (quantity != null) {
						if (targetMapTmp.containsKey("prodCode_" + productId) && targetMapTmp.get("prodCode_" + productId) != null) {
							BigDecimal targetBig = (BigDecimal) targetMapTmp.get("prodCode_" + productId);
							targetBig = targetBig.add(quantity);
							targetMapTmp.put("prodCode_" + productId, targetBig);
						} else {
							targetMapTmp.put("prodCode_" + productId, quantity);
						}
					}
					if (actualQuantity != null) {
						if (actualMapTmp.containsKey("actual_" + productId) && actualMapTmp.get("actual_" + productId) != null) {
							BigDecimal actualBig = (BigDecimal) actualMapTmp.get("actual_" + productId);
							actualBig = actualBig.add(actualQuantity);
							actualMapTmp.put("actual_" + productId, actualBig);
						} else {
							actualMapTmp.put("actual_" + productId, actualQuantity);
						}
					}
					BigDecimal percentSales = BigDecimal.ZERO;
					if (actualQuantity != null && quantity != null) percentSales = actualQuantity.divide(quantity, Integer.parseInt(roundSales) + 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
					
					tempMap.put("prodCode_" + product.getString("productId"), quantity);
					tempMap.put("actual_" + product.getString("productId"), actualQuantity);
					tempMap.put("percent_" + product.getString("productId"), percentSales);
				}
				int childNbrEmpl = (Integer) childNbrEmplMap.get("childNbrEmpl");
				parentNumberEmpl += childNbrEmpl;
				tempMap.put("totalEmployee", childNbrEmpl);
				tempMap.put("isPerson", "N");
				list.add(tempMap);
			}
		}
		returnMap.put("childNbrEmpl", parentNumberEmpl);
		returnMap.put("targetMap", targetMapTmp);
		returnMap.put("actualMap", actualMapTmp);
		return returnMap;
	}
	// END REPORT
	
	public static Map<String, Object> calculateSalesStatement(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	String salesStatementId = (String) context.get("salesStatementId");
    	try {
    		if (UtilValidate.isEmpty(salesStatementId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSSalesSettlementIdMustNotBeEmpty", locale));
			}
			GenericValue salesStatement = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", salesStatementId), false);
			if (salesStatement == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSSalesStatementHasIdIsNotFound", UtilMisc.toList(salesStatementId), locale));
			
			
			EntityFindOptions opts = new EntityFindOptions();
    		opts.setDistinct(true);
    		
    		// NVBH/PG, SUP, ASM, RSM, CSM, NBD
			String customTimePeriodId = salesStatement.getString("customTimePeriodId");
    		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
    		Timestamp fromDate = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
    		Timestamp thruDate = new Timestamp(customTimePeriod.getDate("thruDate").getTime());
    		List<EntityCondition> listAllCondition = new ArrayList<EntityCondition>();
    		listAllCondition.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			listAllCondition.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, thruDate)));
			
			if ("SALES_IN".equals(salesStatement.getString("salesStatementTypeId"))) {
				// calculate for NPP, ASM, ...
				
    			List<EntityCondition> condsItem = new ArrayList<EntityCondition>();
				List<GenericValue> listSalesStatement = delegator.findByAnd("SalesStatement", UtilMisc.toMap("parentSalesStatementId", salesStatementId), null, false);
				for (GenericValue item : listSalesStatement) {
					String itemPartyId = item.getString("internalPartyId");
					
					// calculate NPP
					if (SalesPartyUtil.isDistributor(delegator, itemPartyId)) {
						List<GenericValue> listStatementDetail = delegator.findByAnd("SalesStatementDetail", UtilMisc.toMap("salesStatementId", item.getString("salesStatementId")), null, false);
						condsItem.clear();
						condsItem.addAll(listAllCondition);
						condsItem.add(EntityCondition.makeCondition(UtilMisc.toMap("customerId", itemPartyId, "statusId", "ORDER_COMPLETED")));
						List<GenericValue> listOrder = delegator.findList("OrderHeaderAndOrderRoleFromTo", EntityCondition.makeCondition(condsItem), null, null, opts, false);
						
						calculateQuantityOrderComplete(delegator, listStatementDetail, listOrder);
						//item.set("statusId", "SALES_SM_COMPLETED");
						delegator.storeAll(listStatementDetail);
					}
				}
				//delegator.storeAll(listSalesStatement);
			} else if ("SALES_OUT".equals(salesStatement.getString("salesStatementTypeId"))) {
				// calculate for Salesman/PG, SUP, ASM, ...
				
				List<EntityCondition> condsItem = new ArrayList<EntityCondition>();
				List<GenericValue> listSalesStatement = delegator.findByAnd("SalesStatement", UtilMisc.toMap("parentSalesStatementId", salesStatementId), null, false);
				for (GenericValue item : listSalesStatement) {
					String itemPartyId = item.getString("internalPartyId");
					
					// calculate Salesman
					if (SalesPartyUtil.isSalesman(delegator, itemPartyId)) {
						List<GenericValue> listStatementDetail = delegator.findByAnd("SalesStatementDetail", UtilMisc.toMap("salesStatementId", item.getString("salesStatementId")), null, false);
						condsItem.clear();
						condsItem.addAll(listAllCondition);
						condsItem.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", "SALES_EXECUTIVE", "partyId", itemPartyId, "statusId", "ORDER_COMPLETED")));
						List<GenericValue> listOrder = delegator.findList("OrderHeaderAndRoles", EntityCondition.makeCondition(condsItem), UtilMisc.toSet("orderId"), null, opts, false);
						
						calculateQuantityOrderComplete(delegator, listStatementDetail, listOrder);
						//item.set("statusId", "SALES_SM_COMPLETED");
						delegator.storeAll(listStatementDetail);
					}
				}
				//delegator.storeAll(listSalesStatement);
			}
			//salesStatement.set("statusId", "SALES_SM_COMPLETED");
			//delegator.store(salesStatement);
			
	    } catch (Exception e) {
			String errMsg = "Fatal error calling calculateSalesStatement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	return successResult;
	}
	
	private static void calculateQuantityOrderComplete(Delegator delegator,  List<GenericValue> listStatementDetail, List<GenericValue> listOrder) throws GenericEntityException {
		for (GenericValue itemStatementDetail : listStatementDetail) {
			itemStatementDetail.put("actualQuantity", BigDecimal.ZERO);
			itemStatementDetail.put("actualAmount", BigDecimal.ZERO);
		}
		for (GenericValue itemOrder : listOrder) {
			List<GenericValue> listOrderItem = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", itemOrder.getString("orderId"), "statusId", "ITEM_COMPLETED"), null, false);
			for (GenericValue itemStatementDetail : listStatementDetail) {
				BigDecimal totalQuantity = itemStatementDetail.getBigDecimal("actualQuantity");
				BigDecimal totalAmount = itemStatementDetail.getBigDecimal("actualAmount");
				if (totalQuantity == null) totalQuantity = BigDecimal.ZERO;
				if (totalAmount == null) totalAmount = BigDecimal.ZERO;
				List<GenericValue> listOrderItemFilter = EntityUtil.filterByAnd(listOrderItem, UtilMisc.toMap("productId", itemStatementDetail.getString("productId")));
				for (GenericValue itemOi : listOrderItemFilter) {
					if (itemOi.getBigDecimal("quantity") != null) {
						if (itemOi.getBigDecimal("cancelQuantity") != null) {
							totalQuantity = totalQuantity.add((itemOi.getBigDecimal("quantity").subtract(itemOi.getBigDecimal("cancelQuantity"))));
						} else {
							totalQuantity = totalQuantity.add((itemOi.getBigDecimal("quantity")));
						}
					}
					if (itemOi.getBigDecimal("alternativeQuantity") != null && itemOi.getBigDecimal("alternativeUnitPrice") != null) {
						totalAmount = totalAmount.add((itemOi.getBigDecimal("alternativeQuantity").multiply(itemOi.getBigDecimal("alternativeUnitPrice"))));
					}
				}
				itemStatementDetail.put("actualQuantity", totalQuantity);
				itemStatementDetail.put("actualAmount", totalAmount);
			}
		}
		return;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> pushSalesStatementToCriteria(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String salesStatementId = (String) context.get("salesStatementId");
		String criteriaId = (String) context.get("criteriaId");
		try {
			if (UtilValidate.isEmpty(salesStatementId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSSalesSettlementIdMustNotBeEmpty", locale));
			}
			if (UtilValidate.isEmpty(criteriaId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSCriteriaIdMustNotBeEmpty", locale));
			}
			GenericValue salesStatement = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", salesStatementId), false);
			if (salesStatement == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSSalesStatementHasIdIsNotFound", UtilMisc.toList(salesStatementId), locale));
			GenericValue perfCriteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
			if (perfCriteria == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSCriteriaHasIdIsNotFound", UtilMisc.toList(criteriaId), locale));
			
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			
			// NVBH/PG, SUP, ASM, RSM, CSM, NBD
			String customTimePeriodId = salesStatement.getString("customTimePeriodId");
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			Timestamp fromDate = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
			Timestamp thruDate = new Timestamp(customTimePeriod.getDate("thruDate").getTime());
			List<EntityCondition> listAllCondition = new ArrayList<EntityCondition>();
			listAllCondition.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			listAllCondition.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, thruDate)));
			
			if ("SALES_IN".equals(salesStatement.getString("salesStatementTypeId"))) {
				// calculate for NPP, ASM, ...
				
				// calculate NPP
				List<GenericValue> listSalesStatement = delegator.findByAnd("SalesStatement", UtilMisc.toMap("parentSalesStatementId", salesStatementId), null, false);
				for (GenericValue item : listSalesStatement) {
					String itemPartyId = item.getString("internalPartyId");
					
					// calculate NPP
					if (SalesPartyUtil.isDistributor(delegator, itemPartyId)) {
						/*List<GenericValue> listStatementDetail = delegator.findByAnd("SalesStatementDetail", UtilMisc.toMap("salesStatementId", item.getString("salesStatementId")), null, false);
						List<GenericValue> listOrder = delegator.findByAnd("OrderHeaderAndOrderRoleFromTo", UtilMisc.toMap("customerId", itemPartyId, "statusId", "ORDER_COMPLETED"), null, false);
						
						calculateQuantityOrderComplete(delegator, listStatementDetail, listOrder);
						//item.set("statusId", "SALES_SM_COMPLETED");
						delegator.storeAll(listStatementDetail);*/
						Map<String, Object> resultValue = calculateSalesStatementToCriteria(delegator, dispatcher, item, perfCriteria, userLogin, locale);
						if (ServiceUtil.isError(resultValue)) {
							return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
						}
					}
				}
				//delegator.storeAll(listSalesStatement);
				
				// calculate ASM
				Map<String, Object> resultValueSalesASM = dispatcher.runSync("getListEmplMgrByParty", 
						UtilMisc.<String, Object>toMap("mgrUserLoginId", userLogin.getString("userLoginId"), "roleTypeId", "ASM_EMPL", "userLogin", userLogin));
				if (ServiceUtil.isError(resultValueSalesASM)) {
					return ServiceUtil.returnError((String) resultValueSalesASM.get(ModelService.ERROR_MESSAGE));
				}
				List<String> listSalesAsmIds = (List<String>) resultValueSalesASM.get("listEmployee");
				if (UtilValidate.isNotEmpty(listSalesAsmIds)) {
					for (String asmId : listSalesAsmIds) {
						if (checkHasCriteria(delegator, asmId, criteriaId, fromDate)) {
							List<String> distributorIds = PartyWorker.getDistributorIdsByAsm(delegator, asmId);
							if (UtilValidate.isNotEmpty(distributorIds)) {
								List<GenericValue> listSalesStatementSups = delegator.findList("SalesStatement", 
										EntityCondition.makeCondition(EntityCondition.makeCondition("parentSalesStatementId", salesStatementId), EntityOperator.AND, 
												EntityCondition.makeCondition(EntityCondition.makeCondition("internalPartyId", EntityOperator.IN, distributorIds))), null, null, null, false);
								Map<String, Object> resultValue = calculateSalesStatementToCriteria(delegator, dispatcher, asmId, listSalesStatementSups, perfCriteria, fromDate, thruDate, userLogin, locale);
								if (ServiceUtil.isError(resultValue)) {
									return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
								}
							}
						}
					}
				}
			} else if ("SALES_OUT".equals(salesStatement.getString("salesStatementTypeId"))) {
				// calculate for Salesman/PG, SUP, ASM, ...
				
				// calculate Salesman/PG
				List<GenericValue> listSalesStatement = delegator.findByAnd("SalesStatement", UtilMisc.toMap("parentSalesStatementId", salesStatementId), null, false);
				for (GenericValue item : listSalesStatement) {
					String itemPartyId = item.getString("internalPartyId");
					
					// calculate Salesman
					if (SalesPartyUtil.isSalesman(delegator, itemPartyId)) {
						Map<String, Object> resultValue = calculateSalesStatementToCriteria(delegator, dispatcher, item, perfCriteria, userLogin, locale);
						if (ServiceUtil.isError(resultValue)) {
							return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
						}
					}
				}
				
				// calculate SUP
				Map<String, Object> resultValueSalesSUP = dispatcher.runSync("getListEmplMgrByParty", 
						UtilMisc.<String, Object>toMap("mgrUserLoginId", userLogin.getString("userLoginId"), "roleTypeId", "SALESSUP_EMPL", "userLogin", userLogin));
				if (ServiceUtil.isError(resultValueSalesSUP)) {
					return ServiceUtil.returnError((String) resultValueSalesSUP.get(ModelService.ERROR_MESSAGE));
				}
				List<String> listSalesSupIds = (List<String>) resultValueSalesSUP.get("listEmployee");
				if (UtilValidate.isNotEmpty(listSalesSupIds)) {
					for (String supId : listSalesSupIds) {
						if (checkHasCriteria(delegator, supId, criteriaId, fromDate)) {
							List<String> salesmanIds = PartyWorker.getSalesmanIdsBySup(delegator, supId);
							if (UtilValidate.isNotEmpty(salesmanIds)) {
								List<GenericValue> listSalesStatementSups = delegator.findList("SalesStatement", 
										EntityCondition.makeCondition(EntityCondition.makeCondition("parentSalesStatementId", salesStatementId), EntityOperator.AND, 
												EntityCondition.makeCondition(EntityCondition.makeCondition("internalPartyId", EntityOperator.IN, salesmanIds))), null, null, null, false);
								Map<String, Object> resultValue = calculateSalesStatementToCriteria(delegator, dispatcher, supId, listSalesStatementSups, perfCriteria, fromDate, thruDate, userLogin, locale);
								if (ServiceUtil.isError(resultValue)) {
									return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
								}
							}
						}
					}
				}
				
				// calculate ASM
				Map<String, Object> resultValueSalesASM = dispatcher.runSync("getListEmplMgrByParty", 
						UtilMisc.<String, Object>toMap("mgrUserLoginId", userLogin.getString("userLoginId"), "roleTypeId", "ASM_EMPL", "userLogin", userLogin));
				if (ServiceUtil.isError(resultValueSalesASM)) {
					return ServiceUtil.returnError((String) resultValueSalesASM.get(ModelService.ERROR_MESSAGE));
				}
				List<String> listSalesAsmIds = (List<String>) resultValueSalesASM.get("listEmployee");
				if (UtilValidate.isNotEmpty(listSalesAsmIds)) {
					for (String asmId : listSalesAsmIds) {
						if (checkHasCriteria(delegator, asmId, criteriaId, fromDate)) {
							List<String> salesmanIds = PartyWorker.getSalesmanIdsByAsm(delegator, asmId);
							if (UtilValidate.isNotEmpty(salesmanIds)) {
								List<GenericValue> listSalesStatementSups = delegator.findList("SalesStatement", 
										EntityCondition.makeCondition(EntityCondition.makeCondition("parentSalesStatementId", salesStatementId), EntityOperator.AND, 
												EntityCondition.makeCondition(EntityCondition.makeCondition("internalPartyId", EntityOperator.IN, salesmanIds))), null, null, null, false);
								Map<String, Object> resultValue = calculateSalesStatementToCriteria(delegator, dispatcher, asmId, listSalesStatementSups, perfCriteria, fromDate, thruDate, userLogin, locale);
								if (ServiceUtil.isError(resultValue)) {
									return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling calculateSalesStatement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return successResult;
	}
	
	private static boolean checkHasCriteria(Delegator delegator, String partyId, String criteriaId, Timestamp fromDate) throws GenericEntityException {
		boolean hasCriteria = false;
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyId", partyId));
		listConds.add(EntityCondition.makeCondition("criteriaId", criteriaId));
		listConds.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(fromDate)));
		GenericValue partyPerfCriteria = EntityUtil.getFirst(delegator.findList("PartyPerfCriteria", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false));
		
		if (partyPerfCriteria != null) {
			hasCriteria = true;
		}
		
		return hasCriteria;
	}
	
	private static Map<String, Object> calculateSalesStatementToCriteria(Delegator delegator, LocalDispatcher dispatcher, GenericValue salesStatement, GenericValue perfCriteria, GenericValue userLogin, Locale locale) throws GenericEntityException, GenericServiceException {
		String partyId = salesStatement.getString("internalPartyId");
		BigDecimal actualAmount = BigDecimal.ZERO;
		String criteriaId = perfCriteria.getString("criteriaId");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", salesStatement.getString("customTimePeriodId")), false);
		Timestamp fromDate = null;
		if (customTimePeriod != null) fromDate = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
		
		// find party perf criteria
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyId", partyId));
		listConds.add(EntityCondition.makeCondition("criteriaId", criteriaId));
		listConds.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(fromDate)));
		GenericValue partyPerfCriteria = EntityUtil.getFirst(delegator.findList("PartyPerfCriteria", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false));
		
		if (partyPerfCriteria == null) {
			return successResult;
		}
		
		Timestamp dateReviewed = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
		
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		List<GenericValue> listStatementDetail = delegator.findByAnd("SalesStatementDetail", UtilMisc.toMap("salesStatementId", salesStatement.getString("salesStatementId")), null, false);
		for (GenericValue itemStatementDetail : listStatementDetail) {
			if (itemStatementDetail.get("actualAmount") != null) actualAmount = actualAmount.add(itemStatementDetail.getBigDecimal("actualAmount"));
			if ("KPI_SKU_SALE".equals(criteriaId)) {
				Map<String, Object> itemTmp = FastMap.newInstance();
				itemTmp.put("productId", itemStatementDetail.getString("productId"));
				itemTmp.put("quantityActual", itemStatementDetail.getBigDecimal("actualQuantity"));
				listProducts.add(itemTmp);
			}
		}
		
		Map<String, Object> contextMap = UtilMisc.<String, Object>toMap(
				"periodTypeId", perfCriteria.get("periodTypeId"),
				"partyId", partyId, 
				"criteriaId", criteriaId,
				"fromDate", partyPerfCriteria.get("fromDate"),
				"result", actualAmount,
				"dateReviewed", dateReviewed,
				"userLogin", userLogin, 
				"locale", locale);
		if ("KPI_SKU_SALE".equals(criteriaId)) {
			contextMap.put("listProducts", listProducts);
		}
		
		Map<String, Object> resultValue = dispatcher.runSync("updateKPIForEmp", contextMap);
		if (ServiceUtil.isError(resultValue)) {
			return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
		}
		successResult.put("actualAmount", actualAmount);
		return successResult;
	}
	
	private static Map<String, Object> calculateSalesStatementToCriteria(Delegator delegator, LocalDispatcher dispatcher, String partyId, List<GenericValue> listSalesStatement, GenericValue perfCriteria, Timestamp fromDate, Timestamp thruDate, GenericValue userLogin, Locale locale) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(listSalesStatement)) return ServiceUtil.returnSuccess();
		
		//String partyId = salesStatement.getString("internalPartyId");
		BigDecimal actualAmount = BigDecimal.ZERO;
		String criteriaId = perfCriteria.getString("criteriaId");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		// find party perf criteria
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyId", partyId));
		listConds.add(EntityCondition.makeCondition("criteriaId", criteriaId));
		listConds.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(fromDate)));
		GenericValue partyPerfCriteria = EntityUtil.getFirst(delegator.findList("PartyPerfCriteria", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false));
		
		if (partyPerfCriteria == null) {
			return successResult;
		}
		
		Timestamp dateReviewed = fromDate;
		
		Map<String, Object> productAndQtyMap = FastMap.newInstance();
		for (GenericValue salesStatement : listSalesStatement) {
			List<GenericValue> listStatementDetail = delegator.findByAnd("SalesStatementDetail", UtilMisc.toMap("salesStatementId", salesStatement.getString("salesStatementId")), null, false);
			for (GenericValue itemStatementDetail : listStatementDetail) {
				if (itemStatementDetail.get("actualAmount") != null) actualAmount = actualAmount.add(itemStatementDetail.getBigDecimal("actualAmount"));
				if ("KPI_SKU_SALE".equals(criteriaId)) {
					String productId = itemStatementDetail.getString("productId");
					BigDecimal quantityActual = itemStatementDetail.getBigDecimal("actualQuantity");
					if (productAndQtyMap.containsKey(productId)) {
						BigDecimal existQuantity = (BigDecimal) productAndQtyMap.get(productId);
						if (quantityActual != null) existQuantity = existQuantity.add(quantityActual);
					} else {
						if (quantityActual == null) quantityActual = BigDecimal.ZERO;
						productAndQtyMap.put(productId, quantityActual);
					}
				}
			}
		}
		
		Map<String, Object> contextMap = UtilMisc.<String, Object>toMap(
				"periodTypeId", perfCriteria.get("periodTypeId"),
				"partyId", partyId, 
				"criteriaId", criteriaId,
				"fromDate", partyPerfCriteria.get("fromDate"),
				"result", actualAmount,
				"dateReviewed", dateReviewed,
				"userLogin", userLogin, 
				"locale", locale);
		if ("KPI_SKU_SALE".equals(criteriaId)) {
			List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
			for (Map.Entry<String, Object> item : productAndQtyMap.entrySet()) {
				Map<String, Object> newTmp = FastMap.newInstance();
				newTmp.put("productId", item.getKey());
				newTmp.put("quantityActual", (BigDecimal) item.getValue());
				listProducts.add(newTmp);
			}
			contextMap.put("listProducts", listProducts);
		}
		
		Map<String, Object> resultValue = dispatcher.runSync("updateKPIForEmp", contextMap);
		if (ServiceUtil.isError(resultValue)) {
			return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
		}
		return successResult;
	}
	
	public static Map<String, Object> pushSalesStatementToCriteriaTarget(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String salesStatementId = (String) context.get("salesStatementId");
		//String criteriaId = "KPI_TURNOVER_SALE";
		//String criteriaId2 = "KPI_SKU_SALE";
		try {
			if (UtilValidate.isEmpty(salesStatementId)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSSalesSettlementIdMustNotBeEmpty", locale));
			}
			//if (UtilValidate.isEmpty(criteriaId)) {
			//	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSCriteriaIdMustNotBeEmpty", locale));
			//}
			GenericValue salesStatement = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", salesStatementId), false);
			if (salesStatement == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSSalesStatementHasIdIsNotFound", UtilMisc.toList(salesStatementId), locale));
			//GenericValue perfCriteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
			//if (perfCriteria == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSCriteriaHasIdIsNotFound", UtilMisc.toList(criteriaId), locale));

			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			
			// NVBH/PG, SUP, ASM, RSM, CSM, NBD
			String customTimePeriodId = salesStatement.getString("customTimePeriodId");
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			Timestamp fromDate = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
			Timestamp thruDate = new Timestamp(customTimePeriod.getDate("thruDate").getTime());
			List<EntityCondition> listAllCondition = new ArrayList<EntityCondition>();
			listAllCondition.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			listAllCondition.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, thruDate)));
			
			if ("SALES_IN".equals(salesStatement.getString("salesStatementTypeId"))) {
				// calculate for NPP, ASM, ...
				
				List<GenericValue> listSalesStatement = delegator.findByAnd("SalesStatement", UtilMisc.toMap("parentSalesStatementId", salesStatementId), null, false);
				for (GenericValue item : listSalesStatement) {
					String itemPartyId = item.getString("internalPartyId");
					
					// calculate NPP
					if (SalesPartyUtil.isDistributor(delegator, itemPartyId)) {
						Map<String, Object> resultValue = calculateSalesStatementToCriteriaTarget(delegator, dispatcher, item, userLogin, locale);
						if (ServiceUtil.isError(resultValue)) {
							return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
						}
					}
				}
				//delegator.storeAll(listSalesStatement);
			} else if ("SALES_OUT".equals(salesStatement.getString("salesStatementTypeId"))) {
				// calculate for Salesman/PG, SUP, ASM, ...
				
				/*List<GenericValue> listSalesStatement = delegator.findByAnd("SalesStatement", UtilMisc.toMap("parentSalesStatementId", salesStatementId), null, false);
				for (GenericValue item : listSalesStatement) {
					String itemPartyId = item.getString("internalPartyId");
					
					// calculate Salesman
					if (SalesPartyUtil.isSalesman(delegator, itemPartyId)) {
						Map<String, Object> resultValue = calculateSalesStatementToCriteria(delegator, dispatcher, item, perfCriteria, userLogin, locale);
						if (ServiceUtil.isError(resultValue)) {
							return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
						}
					}
				}*/
				List<GenericValue> listSalesStatement = delegator.findByAnd("SalesStatement", UtilMisc.toMap("parentSalesStatementId", salesStatementId), null, false);
				for (GenericValue item : listSalesStatement) {
					String itemPartyId = item.getString("internalPartyId");

					// calculate Salesman
					if (SalesPartyUtil.isSalesman(delegator, itemPartyId)) {
						Map<String, Object> resultValue = calculateSalesStatementToCriteriaTarget(delegator, dispatcher, item, userLogin, locale);
						if (ServiceUtil.isError(resultValue)) {
							return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
						}
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling calculateSalesStatement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return successResult;
	}
	
	private static Map<String, Object> calculateSalesStatementToCriteriaTarget(Delegator delegator, LocalDispatcher dispatcher, GenericValue salesStatement, GenericValue userLogin, Locale locale) throws GenericEntityException, GenericServiceException {
		String partyId = salesStatement.getString("internalPartyId");
		BigDecimal targetAmount = BigDecimal.ZERO;
		//String criteriaId = perfCriteria.getString("criteriaId");
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", salesStatement.getString("customTimePeriodId")), false);
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		if (customTimePeriod != null) {
			fromDate = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
			thruDate = new Timestamp(customTimePeriod.getDate("thruDate").getTime());
		}
		if (fromDate == null || thruDate == null) {
			return ServiceUtil.returnError("Custom time period not found");
		}
		// find party perf criteria
		/*List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyId", partyId));
		listConds.add(EntityCondition.makeCondition("criteriaId", criteriaId));
		listConds.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(fromDate)));
		GenericValue partyPerfCriteria = EntityUtil.getFirst(delegator.findList("PartyPerfCriteria", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false));
		
		if (partyPerfCriteria == null) {
			return successResult;
		}*/
		
		//Timestamp dateReviewed = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
		
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		List<GenericValue> listStatementDetail = delegator.findByAnd("SalesStatementDetail", UtilMisc.toMap("salesStatementId", salesStatement.getString("salesStatementId")), null, false);
		for (GenericValue itemStatementDetail : listStatementDetail) {
			String productId = itemStatementDetail.getString("productId");
			String quantityUomId = itemStatementDetail.getString("quantityUomId");
			if (itemStatementDetail.get("amount") != null) {
				targetAmount = targetAmount.add(itemStatementDetail.getBigDecimal("amount"));
				// add to list
				Map<String, Object> newItem = FastMap.newInstance();
				newItem.put("productId", productId);
				newItem.put("uomId", quantityUomId);
				newItem.put("quantityTarget", itemStatementDetail.getBigDecimal("quantity"));
				listProducts.add(newItem);
			} else if (itemStatementDetail.get("quantity") != null) {
				BigDecimal quantity = itemStatementDetail.getBigDecimal("quantity");
				if (quantity != null) {
					BigDecimal price = BigDecimal.ZERO;
					GenericValue defaultPrice = EntityUtil.getFirst(delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId, "termUomId", quantityUomId, "productPriceTypeId", "DEFAULT_PRICE"), null, false));
					if (defaultPrice != null) {
						price = defaultPrice.getBigDecimal("price");
					}
					targetAmount = targetAmount.add(quantity.multiply(price));
					// add to list
					Map<String, Object> newItem = FastMap.newInstance();
					newItem.put("productId", productId);
					newItem.put("uomId", quantityUomId);
					newItem.put("quantityTarget", quantity);
					listProducts.add(newItem);
				}
			}
		}
		
		String criteriaId1 = "KPI_TURNOVER_SALE";
		String periodTypeId1 = "MONTHLY";
		String uomId = "KM_VND";
		Map<String, Object> contextMap = UtilMisc.<String, Object>toMap(
				"periodTypeId", periodTypeId1,
				"partyId", partyId, 
				"criteriaId", criteriaId1,
				"fromDate", fromDate,
				"thruDate", thruDate,
				"uomId", uomId,
				"target", targetAmount,
				"userLogin", userLogin, 
				"locale", locale);
		Map<String, Object> resultValue = dispatcher.runSync("createPartyPerfCriteria", contextMap);
		if (ServiceUtil.isError(resultValue)) {
			return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
		}

		/*String criteriaId2 = "KPI_SKU_SALE";
		String periodTypeId2 = "MONTHLY";
		String enumIdKpiCalc2 = "KPI_CALC_MIN";
		Map<String, Object> contextMap2 = UtilMisc.<String, Object>toMap(
				"enumIdKpiCalc", enumIdKpiCalc2,
				"periodTypeId", periodTypeId2,
				"partyId", partyId, 
				"criteriaId", criteriaId2,
				"fromDate", fromDate,
				"thruDate", thruDate,
				"uomId", uomId,
				"target", targetAmount,
				"listProducts", listProducts,
				"userLogin", userLogin, 
				"locale", locale);
		Map<String, Object> resultValue2 = dispatcher.runSync("createPartyPerfCriteria", contextMap2);
		if (ServiceUtil.isError(resultValue2)) {
			return ServiceUtil.returnError((String) resultValue2.get(ModelService.ERROR_MESSAGE));
		}
		*/
		
		return successResult;
	}
	
	/** Service for changing the status on an product quotation */
	public static Map<String, Object> setSalesStatementStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String salesStatementId = (String) context.get("salesStatementId");
        String statusId = (String) context.get("statusId");
        String changeReason = (String) context.get("changeReason");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");

        Security security = ctx.getSecurity();
        if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SALESSTATEMENT_APPROVE")) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission",locale));
        }
        
        GenericValue salesStatement = null;
        try {
        	salesStatement = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", salesStatementId), false);
        	if (salesStatement == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSRecordIsNotFound", locale));
			}

            // first save off the old status
        	successResult.put("oldStatusId", salesStatement.get("statusId"));

        	if (salesStatement.getString("statusId").equals(statusId)) {
        		return successResult;
        	}
            /* check status valid change
            try {
                Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", salesStatement.getString("statusId"), "statusIdTo", statusId);
                GenericValue statusChange = delegator.findOne("StatusValidChange", statusFields, true);
                if (statusChange == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, 
                            "BSErrorCouldNotChangeQuotationStatusStatusIsNotAValidChange", locale) + ": [" + statusFields.get("statusId") + "] -> [" + statusFields.get("statusIdTo") + "]");
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorCouldNotChangeQuotationStatus",locale) + e.getMessage() + ").");
            }*/

            List<GenericValue> tobeStored = new LinkedList<GenericValue>();
        	// update the current status
            salesStatement.set("statusId", statusId);
            tobeStored.add(salesStatement);
            
            // update sales statement children
            List<GenericValue> listSalesStatementChildren = delegator.findByAnd("SalesStatement", UtilMisc.toMap("parentSalesStatementId", salesStatement.get("salesStatementId")), null, false);
            if (UtilValidate.isNotEmpty(listSalesStatementChildren)) {
            	for (GenericValue salesStatementChild : listSalesStatementChildren) {
            		salesStatementChild.set("statusId", statusId);
            		tobeStored.add(salesStatementChild);
            	}
            }

            // now create a status change
            GenericValue salesStatementStatus = delegator.makeValue("SalesStatementStatus");
            salesStatementStatus.put("salesStatementStatusId", delegator.getNextSeqId("SalesStatementStatus"));
            salesStatementStatus.put("statusId", statusId);
            salesStatementStatus.put("salesStatementId", salesStatementId);
            salesStatementStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
            salesStatementStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
            salesStatementStatus.put("changeReason", changeReason);
            tobeStored.add(salesStatementStatus);
            
            delegator.storeAll(tobeStored);
            
        } catch (GenericEntityException e) {
        	Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
        }
    	
        successResult.put("salesStatementId", salesStatementId);
        return successResult;
    }
}
