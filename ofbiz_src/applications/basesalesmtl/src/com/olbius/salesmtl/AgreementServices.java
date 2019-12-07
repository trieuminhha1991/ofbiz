package com.olbius.salesmtl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.administration.util.CrabEntity;
import com.olbius.administration.util.UniqueUtil;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.salesmtl.util.MTLUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class AgreementServices {
	public static final String module = AgreementServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels";
	
	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> listPartyTo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<String> listCustomerId = SecurityUtil.getPartiesByRoles("INTERNAL_ORGANIZATIO", delegator);
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listCustomerId));
			listIterator = delegator.find("PartyGroup", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}*/
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListSalesRepresentative(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		Map<String, Object> successReturn = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "SALES_REP"));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYEE"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			listIterator = delegator.find("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesRepresentative service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successReturn.put("listIterator", listIterator);
		return successReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listRepresentTo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<String> partyIds = SecurityUtil.getPartiesByRoles("SALES_REP", delegator);
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
			listIterator = delegator.find("PartyAndPerson", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	public static Map<String, Object> createAgreementDistributor(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			//	createAgreement
			UniqueUtil.checkAgreementCode(delegator, context.get("agreementCode"), context.get("agreementId"));
			
			Long agreementDateL = (Long) context.get("agreementDate");
			Long fromDateL = (Long) context.get("fromDate");
			Long thruDateL = (Long) context.get("thruDate");
			Timestamp agreementDate = null;
			Timestamp fromDate = null;
			Timestamp thruDate = null;
			if (UtilValidate.isNotEmpty(agreementDateL)) {
				agreementDate = new Timestamp(agreementDateL);
			}
			if (UtilValidate.isNotEmpty(fromDateL)) {
				fromDate = new Timestamp(fromDateL);
			}
			if (UtilValidate.isNotEmpty(thruDateL)) {
				thruDate = new Timestamp(thruDateL);
			}
			context.remove("agreementDate");
			context.remove("fromDate");
			context.remove("thruDate");
			Map<String, Object> agreement = CrabEntity.fastMaking(delegator, "Agreement", context);
			agreement.put("agreementDate", agreementDate);
			agreement.put("fromDate", fromDate);
			agreement.put("thruDate", thruDate);
			agreement.put("roleTypeIdFrom", agreementRoleTypeIdFrom(delegator, context.get("partyIdFrom")));
			agreement.put("roleTypeIdTo", "INTERNAL_ORGANIZATIO");
			agreement.put("agreementTypeId", "SALES_AGREEMENT");
			agreement.put("statusId", "AGREEMENT_CREATED");
			result = dispatcher.runSync("createAgreement", agreement);
			String agreementId = (String) result.get("agreementId");
			
			dispatcher.runSync("createAgreementRole", UtilMisc.toMap("agreementId", agreementId, "partyId", context.get("representativeId"),
					"roleTypeId", "SALES_REP", "userLogin", context.get("userLogin")));
			
			Locale locale = (Locale) context.get("locale");
			String header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSNewAgreementNotify", locale) + " [" + context.get("agreementCode") + "]";;
			dispatcher.runSync("createNotification",
					UtilMisc.toMap("partyId", "OLBSADM", "targetLink", "agreementId=" + agreementId, "sendToSender", "Y",
							"action", "AgreementDetail", "header", header, "ntfType", "ONE", "userLogin", context.get("userLogin")));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	private static String agreementRoleTypeIdFrom(Delegator delegator, Object partyId) throws GenericEntityException {
		String roleTypeIdFrom = "";
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
		String partyTypeId = party.getString("partyTypeId");
		switch (partyTypeId) {
		case "RETAIL_OUTLET":
			roleTypeIdFrom = "CUSTOMER";
			break;
		default:
			roleTypeIdFrom = "DISTRIBUTOR";
			break;
		}
		return roleTypeIdFrom;
	}
	public static Map<String, Object> updateAgreementDistributor(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			//	createAgreement
			UniqueUtil.checkAgreementCode(delegator, context.get("agreementCode"), context.get("agreementId"));
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Long agreementDateL = (Long) context.get("agreementDate");
			Long fromDateL = (Long) context.get("fromDate");
			Long thruDateL = (Long) context.get("thruDate");
			Timestamp agreementDate = null;
			Timestamp fromDate = null;
			Timestamp thruDate = null;
			if (UtilValidate.isNotEmpty(agreementDateL)) {
				agreementDate = new Timestamp(agreementDateL);
			}
			if (UtilValidate.isNotEmpty(fromDateL)) {
				fromDate = new Timestamp(fromDateL);
			}
			if (UtilValidate.isNotEmpty(thruDateL)) {
				thruDate = new Timestamp(thruDateL);
			}
			Map<String, Object> agreement = CrabEntity.fastMaking(delegator, "Agreement", context);
			agreement.put("agreementDate", agreementDate);
			agreement.put("fromDate", fromDate);
			agreement.put("thruDate", thruDate);
			agreement.put("statusId", "AGREEMENT_MODIFIED");
			dispatcher.runSync("updateAgreement", agreement);
			
			Locale locale = (Locale) context.get("locale");
			String header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSModifyAgreementNotify", locale);
			header += ": " + userLogin.getString("partyId");
			dispatcher.runSync("createNotification",
					UtilMisc.toMap("partyId", "OLBSADM", "targetLink", "agreementId=" + context.get("agreementId"), "sendToSender", "Y",
							"action", "AgreementDetail", "header", header, "ntfType", "ONE", "userLogin", userLogin));
			
			result.put("agreementId", context.get("agreementId"));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listAgreementDistributor(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<EntityCondition> conditions = (List<EntityCondition>) context.get("listAllConditions");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("partyIdFrom")) {
				String partyIdFrom = (String) parameters.get("partyIdFrom")[0];
				if (UtilValidate.isNotEmpty(partyIdFrom)) {
					conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, partyIdFrom));
				}
			}
			String userLoginPartyId = userLogin.getString("partyId");
			if (SalesPartyUtil.isSalesEmployee(delegator, userLoginPartyId)) {
				if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
					conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.IN, DistributorServices.distributorOfSupervisor(delegator, userLogin)));
				}
			} else if (SalesPartyUtil.isDistributor(delegator, userLoginPartyId)) {
				conditions.add(EntityCondition.makeCondition("partyIdFrom", userLoginPartyId));
			}
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "DISTRIBUTOR", "agreementTypeId", "SALES_AGREEMENT")));
			listIterator = delegator.find("AgreementAndPartyFromDetail", EntityCondition.makeCondition(conditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listAgreementAgent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<EntityCondition> conditions = (List<EntityCondition>) context.get("listAllConditions");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("partyIdFrom")) {
				String partyIdFrom = (String) parameters.get("partyIdFrom")[0];
				if (UtilValidate.isNotEmpty(partyIdFrom)) {
					conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, partyIdFrom));
				}
			}
			if (MTLUtil.hasSecurityGroupPermission(delegator, "DISTRIBUTOR_ADMIN", userLogin, false)) {
				conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.IN, SupervisorServices.agentsOfDistributor(delegator, userLogin.get("partyId"))));
			} else if (!MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, true) || !MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, true)) {
				conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.IN, SupervisorServices.agentOfSupervisor(delegator, userLogin)));
			}
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyFromTypeId", "RETAIL_OUTLET", "roleTypeIdFrom", "CUSTOMER",
					"agreementTypeId", "SALES_AGREEMENT")));
			listIterator = delegator.find("AgreementAndPartyFromDetail", EntityCondition.makeCondition(conditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	public static Map<String, Object> checkAgreementCode(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			UniqueUtil.checkAgreementCode(delegator, context.get("agreementCode"), context.get("agreementId"));
			result.put("check", "true");
		} catch (Exception e) {
			result.put("check", "false");
		}
		return result;
	}
	public static Map<String, Object> approveAgreement(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String agreementId = (String) context.get("agreementId");
			dispatcher.runSync("updateAgreement",
						UtilMisc.toMap("agreementId", agreementId, "statusId", "AGREEMENT_APPROVED", "userLogin", userLogin));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> loadAgreementInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> agreementInfo = FastMap.newInstance();
			String agreementId = (String) context.get("agreementId");
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			agreementInfo.putAll(agreement);
			agreementInfo.put("partyTypeFrom", getPartyType(delegator, agreement.get("partyIdFrom")));
			//	get representative person
			List<GenericValue> agreementRoles = delegator.findList("AgreementRole",
					EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "roleTypeId", "SALES_REP")),
					UtilMisc.toSet("partyId"), null, null, false);
			if (UtilValidate.isNotEmpty(agreementRoles)) {
				agreementInfo.put("representativeId", EntityUtil.getFirst(agreementRoles).get("partyId"));
			}
			result.put("agreementInfo", agreementInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static String getPartyType(Delegator delegator, Object partyId) throws GenericEntityException {
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
		return party.getString("partyTypeId");
	}
	
	public static Map<String, Object> uploadFileScanAgreement(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			String agreementId = (String) context.get("agreementId");
			context.remove("agreementId");
			//	uploadFile
			result = dispatcher.runSync("jackrabbitUploadFile", context);
			//	createDataResource
			result = dispatcher.runSync("createDataResource", UtilMisc.toMap("dataResourceTypeId", "LINK", "dataTemplateTypeId", "NONE",
					"statusId", "CTNT_AVAILABLE", "mimeTypeId", "text/xml", "objectInfo", result.get("path"), "isPublic", "N",
					"userLogin", context.get("userLogin")));
			//	createContent
			result = dispatcher.runSync("createContent", UtilMisc.toMap("dataResourceId", result.get("dataResourceId"), "userLogin", context.get("userLogin")));
			//	createAgreementContent
			dispatcher.runSync("createAgreementContent", UtilMisc.toMap("agreementId", agreementId, "contentId", result.get("contentId"),
						"statusId", "CTNT_AVAILABLE","userLogin", context.get("userLogin")));
			result.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> loadFileScanAgreement(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String agreementId = (String) context.get("agreementId");
			List<GenericValue> agreementContents = delegator.findList("AgreementContentAndDataResource",
					EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "statusId", "CTNT_AVAILABLE")),
					UtilMisc.toSet("objectInfo"), null, null, false);
			List<Object> fileScanAgreement = EntityUtil.getFieldListFromEntityList(agreementContents, "objectInfo", true);
			result.put("fileScanAgreement", fileScanAgreement);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> createAgreementFromPromoExtReg(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	String agreementId = null;
    	try {
    		// get info of registration of promotion extend
    		String agreementCode = (String) context.get("agreementCode");
    		String regProductPromoId = (String) context.get("regProductPromoId");
    		String regProductPromoRuleId = (String) context.get("regProductPromoRuleId");
    		String regFromDateStr = (String) context.get("regFromDate");
    		String customerId = (String) context.get("customerId");
    		String agreementDateStr = (String) context.get("agreementDate");
    		String fromDateStr = (String) context.get("fromDate");
    		String thruDateStr = (String) context.get("thruDate");
    		
    		Timestamp regFromDate = null;
    		Timestamp agreementDate = null;
    		Timestamp fromDate = null;
    		Timestamp thruDate = null;
    		try {
    	        if (UtilValidate.isNotEmpty(regFromDateStr)) {
    	        	Long regFromDateL = Long.parseLong(regFromDateStr);
    	        	regFromDate = new Timestamp(regFromDateL);
    	        }
    	        if (UtilValidate.isNotEmpty(agreementDateStr)) {
    	        	Long agreementDateL = Long.parseLong(agreementDateStr);
    	        	agreementDate = new Timestamp(agreementDateL);
    	        }
    	        if (UtilValidate.isNotEmpty(fromDateStr)) {
    	        	Long fromDateL = Long.parseLong(fromDateStr);
    	        	fromDate = new Timestamp(fromDateL);
    	        }
    	        if (UtilValidate.isNotEmpty(thruDateStr)) {
    	        	Long thruDateL = Long.parseLong(thruDateStr);
    	        	thruDate = new Timestamp(thruDateL);
    	        }
            } catch (Exception e) {
            	Debug.logWarning("Error when format date time", module);
            }
    		
    		GenericValue promoExtRegister = delegator.findOne("ProductPromoExtRegister", 
    				UtilMisc.toMap("productPromoId", regProductPromoId, 
    						"productPromoRuleId", regProductPromoRuleId, 
    						"partyId", customerId,
    						"fromDate", regFromDate), false);
    		
    		if (promoExtRegister == null) {
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSThisRegisterNotAvaiable", locale));
    		}
    		
    		if (UtilValidate.isNotEmpty(promoExtRegister.get("agreementId"))) {
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "ThisRegistrationHadAgreement", locale));
    		}
    		
    		if (UtilValidate.isNotEmpty(agreementCode)) {
    			List<GenericValue> agreementCheckExists = delegator.findByAnd("Agreement", UtilMisc.toMap("agreementCode", agreementCode), null, false);
        		if (UtilValidate.isNotEmpty(agreementCheckExists)) {
        			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSAgreementCodeHasExisted", locale));
        		}
    		}
    		
    		String agreementTypeId = null;
    		String agreementItemTypeId = null;
    		String agreementTermParentId = null;
    		GenericValue productPromoExt = delegator.findOne("ProductPromoExt", UtilMisc.toMap("productPromoId", promoExtRegister.getString("productPromoId")), false);
    		if (productPromoExt == null) {
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSProductPromoHasIdIsNotFound", locale));
    		}
    		if ("PROMO_EXHIBITION".equals(productPromoExt.getString("productPromoTypeId"))) {
    			agreementTypeId = "PROMO_EXHIBITION_AGREEMENT";
    			agreementItemTypeId = "AGREEMENT_EXHIBIT";
    			agreementTermParentId = "PROMO_EXHIBITION_TERM";
    		} else if ("PROMO_ACCUMULATION".equals(productPromoExt.getString("productPromoTypeId"))){
    			agreementTypeId = "PROMO_ACCUMULATION_AGREEMENT";
    			agreementItemTypeId = "AGREEMENT_ACCUMULATION";
    			agreementTermParentId = "PROMO_ACCUMULATION_TERM";
    		}
    		
    		// create exhibition agreement
    		String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		Map<String, Object> agreementContextMap = UtilMisc.<String, Object>toMap(
    					"partyIdFrom", customerId,
    					"partyIdTo", organizationId,
    					"roleTypeIdFrom", "CUSTOMER",
    					"roleTypeIdTo", "INTERNAL_ORGANIZATIO",
    					"agreementTypeId", agreementTypeId,
    					"agreementCode", agreementCode,
    					"statusId", "AGREEMENT_CREATED",
    					"agreementDate", agreementDate,
    					"fromDate", fromDate,
    					"thruDate", thruDate,
    					"userLogin", userLogin,
    					"locale", locale
    				);
    		Map<String, Object> resultCreateAgree = dispatcher.runSync("createAgreement", agreementContextMap);
    		if (ServiceUtil.isError(resultCreateAgree)) {
    			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateAgree));
    		}
    		
    		agreementId = (String) resultCreateAgree.get("agreementId");
    		
    		GenericValue agreementNew = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
    		if (agreementNew != null && UtilValidate.isEmpty(agreementNew.get("agreementCode"))) {
    			agreementNew.set("agreementCode", agreementId);
    			delegator.store(agreementNew);
    		}
    		
    		// create agreement items
    		String currencyUomId = SalesUtil.getCurrentCurrencyUom(delegator);
    		String agreementText = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSExhibitionPromotion", locale);
    		Map<String, Object> resultCreateItem = dispatcher.runSync("createAgreementItem", 
    				UtilMisc.toMap("agreementId", agreementId, 
    						"agreementItemTypeId", agreementItemTypeId,
    						"currencyUomId", currencyUomId, 
    						"agreementText", agreementText, 
    						"userLogin", userLogin));
    		if (ServiceUtil.isError(resultCreateItem)) {
    			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateItem));
    		}
    		// create agreement promotion exhibition apply
    		String agreementItemSeqId = (String) resultCreateItem.get("agreementItemSeqId");
    		Map<String, Object> resultCreateItemAppl = dispatcher.runSync("createAgreementPromoExtAppl", 
    				UtilMisc.toMap("agreementId", agreementId, 
    						"agreementItemSeqId", agreementItemSeqId, 
    						"productPromoId", regProductPromoId, 
    						"productPromoRuleId", regProductPromoRuleId,
    						"fromDate", fromDate, 
    						"userLogin", userLogin,
    						"locale", locale));
    		if (ServiceUtil.isError(resultCreateItemAppl)) {
    			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateItem));
    		}
    		
    		// create agreement terms
    		if (UtilValidate.isNotEmpty(agreementTermParentId)) {
    			List<GenericValue> allTerms = com.olbius.basehr.employment.services.AgreementServices.getAllTerm(ctx, agreementTermParentId);
    			if (UtilValidate.isNotEmpty(allTerms)) {
    				for (GenericValue termType : allTerms) {
    					String termTypeId = termType.getString("termTypeId");
    					
    					// get defaultValues of termType's textValue
    					List<EntityCondition> listCond = FastList.newInstance();
    					listCond.add(EntityCondition.makeCondition("termTypeId", termTypeId));
    					listCond.add(EntityCondition.makeCondition("attrName", EntityOperator.LIKE, "defaultValue%"));
    					
    					EntityFindOptions findOpts = new EntityFindOptions();
    					findOpts.setDistinct(true);
    					
    					List<GenericValue> termTypeAttrs = delegator.findList("TermTypeAttr", EntityCondition.makeCondition(listCond), null, UtilMisc.toList("attrName"), findOpts, false);
    					if (termTypeAttrs != null) {
    						for (GenericValue termTypeAttr : termTypeAttrs) {
    							String textValue = termTypeAttr.getString("attrValue");
    							if (textValue != null) {
    								dispatcher.runSync("createAgreementTerm", UtilMisc.toMap("agreementId", agreementId, 
    										"termTypeId", termTypeId, 
    										"textValue", textValue,
    										"fromDate", fromDate,
    										"thruDate", thruDate,
    										"userLogin", userLogin,
    										"locale", locale));
    							}
    						}
    					}
    				}
    			}
    		}
    		
			// create agreement role
			List<String> supPartyIds = PartyWorker.getSupIdsByCustomer(delegator, customerId);
			if (UtilValidate.isNotEmpty(supPartyIds)) {
				for (String supId : supPartyIds) {
					dispatcher.runSync("createAgreementRole", UtilMisc.toMap("agreementId", agreementId, 
							"partyId", supId, 
							"roleTypeId", "SALESSUP_EMPL", 
							"userLogin", userLogin));
				}
			}
			
			// store agreementId to register
			promoExtRegister.set("agreementId", agreementId);
			delegator.store(promoExtRegister);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling createAgreementFromPromoExtReg service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
    	
    	successResult.put("agreementId", agreementId);
    	return successResult;
    }
}
