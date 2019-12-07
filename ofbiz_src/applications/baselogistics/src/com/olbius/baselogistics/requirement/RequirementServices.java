package com.olbius.baselogistics.requirement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
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
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.baselogistics.delivery.DeliveryItemSubject;
import com.olbius.baselogistics.delivery.DeliveryObserver;
import com.olbius.baselogistics.delivery.ItemSubject;
import com.olbius.baselogistics.delivery.Observer;
import com.olbius.baselogistics.util.LogisticsFacilityUtil;
import com.olbius.baselogistics.util.LogisticsPartyUtil;
import com.olbius.baselogistics.util.LogisticsProductUtil;
import com.olbius.product.util.InventoryUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.services.JqxWidgetSevices;
import com.olbius.util.FacilityUtil;

public class RequirementServices {
	public static final String module = RequirementServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resources = "AccountingUiLabels";
	public static final String resourceError = "BaseLogisticsErrorUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
	
    @SuppressWarnings("unused")
	private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
    @SuppressWarnings("unused")
	private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> createRequirement(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String org = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		Locale locale = (Locale)context.get("locale");
		
		String statusId = null;
		if (UtilValidate.isNotEmpty(context.get("statusId"))) {
			statusId = (String)context.get("statusId");
		} else {
			statusId = "REQ_CREATED";
		}
    	String listItemTmp = null;
    	if (UtilValidate.isNotEmpty(context.get("listProducts"))) {
    		listItemTmp = (String)context.get("listProducts");
		}
    	String facilityId = null;
    	if (UtilValidate.isNotEmpty(context.get("facilityId"))) {
    		facilityId = (String)context.get("facilityId");
    	}
    	String destFacilityId = null;
    	if (UtilValidate.isNotEmpty(context.get("destFacilityId"))) {
    		destFacilityId = (String)context.get("destFacilityId");
    	}
    	String eventId = null;
    	if (UtilValidate.isNotEmpty(context.get("eventId"))) {
    		eventId = (String)context.get("eventId");
    	}
    	
    	String requirementTypeId = (String)context.get("requirementTypeId");
    	BigDecimal estimatedBudget = null;
    	if (UtilValidate.isNotEmpty(context.get("estimatedBudget"))) {
    		estimatedBudget = (BigDecimal)context.get("estimatedBudget");
		}
    	
    	String description = null;
    	if (UtilValidate.isNotEmpty(context.get("description"))) {
    		description = (String)context.get("description"); 
		}
    	
    	Long requirementStartDate = (Long)context.get("requirementStartDate");
    	String currencyUomId = null;
    	if (UtilValidate.isNotEmpty(context.get("currencyUomId"))) {
    		currencyUomId = (String)context.get("currencyUomId");
		} else {
			GenericValue partyAcctg = null;
			try {
				partyAcctg = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", org));
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLErrorWhenCreateRequirement", locale));
			}
    		if (UtilValidate.isNotEmpty(partyAcctg)) currencyUomId = partyAcctg.getString("baseCurrencyUomId"); 
		}
    
    	String reasonEnumId = null;
    	if (UtilValidate.isNotEmpty(context.get("reasonEnumId"))) {
    		reasonEnumId = (String)context.get("reasonEnumId");
		}
    	
    	String contactMechId = null;
    	if (UtilValidate.isNotEmpty(context.get("contactMechId"))) {
    		contactMechId = (String)context.get("contactMechId");
    	} else {
    		if (UtilValidate.isNotEmpty(facilityId)) {
    			List<GenericValue> listContactMechs = FastList.newInstance();
    			try {
    				listContactMechs = LogisticsFacilityUtil.getFacilityContactMechs(delegator, facilityId, "SHIP_ORIG_LOCATION");
				} catch (GenericEntityException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLErrorWhenCreateRequirement", locale));
				}
    			if (!listContactMechs.isEmpty()){
    				contactMechId = listContactMechs.get(0).getString("contactMechId");
    			}
			}
    	}
    	
    	if (UtilValidate.isEmpty(contactMechId) && UtilValidate.isNotEmpty(facilityId)) {
    		GenericValue objFacility = null;
			try {
				objFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findOne Facility error!");
			}
			if (UtilValidate.isNotEmpty(objFacility.get("facilityCode"))) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLFacilityNotFoundOriginAddress", locale) + " " + objFacility.getString("facilityCode"));
			} else {
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLFacilityNotFoundOriginAddress", locale) + " " + facilityId);
			}
		}
    	
    	String destContactMechId = null;
    	if (UtilValidate.isNotEmpty(context.get("destContactMechId"))) {
    		destContactMechId = (String)context.get("destContactMechId");
    	} else {
    		if (UtilValidate.isNotEmpty(destFacilityId)) {
    			List<GenericValue> listContactMechs = FastList.newInstance();
    			try {
    				listContactMechs = LogisticsFacilityUtil.getFacilityContactMechs(delegator, destFacilityId, "SHIPPING_LOCATION");
				} catch (GenericEntityException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLErrorWhenCreateRequirement", locale));
				}
    			if (!listContactMechs.isEmpty()){
    				destContactMechId = listContactMechs.get(0).getString("contactMechId");
    			}
			}
    	}
    	if (UtilValidate.isEmpty(destContactMechId) && UtilValidate.isNotEmpty(destFacilityId)) {
			GenericValue objFacility = null;
			try {
				objFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findOne Facility error!");
			}
			if (UtilValidate.isNotEmpty(objFacility.get("facilityCode"))) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLFacilityNotFoundShippingAddress", locale) + " " + objFacility.getString("facilityCode"));
			} else {
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLFacilityNotFoundShippingAddress", locale) + " " + facilityId);
			}
		}
    	GenericValue requirement = delegator.makeValue("Requirement");
    	String requirementId = delegator.getNextSeqId("Requirement"); 
    	requirement.put("requirementId", requirementId);
    	requirement.put("requirementTypeId", requirementTypeId);
    	requirement.put("facilityId", facilityId);
    	requirement.put("contactMechId", contactMechId);
		requirement.put("destFacilityId", destFacilityId);
		requirement.put("destContactMechId", destContactMechId);
    	requirement.put("description", description);
    	requirement.put("statusId", statusId);
    	requirement.put("eventId", eventId);
    	requirement.put("currencyUomId", currencyUomId);
    	requirement.put("reasonEnumId", reasonEnumId);
    	requirement.put("estimatedBudget", estimatedBudget);
    	requirement.put("requiredByDate", UtilDateTime.nowTimestamp());
    	requirement.put("requirementStartDate", new Timestamp(requirementStartDate));
    	requirement.put("createdDate", UtilDateTime.nowTimestamp());
    	requirement.put("createdByUserLogin", userLogin.getString("userLoginId"));
    	try {
    		delegator.create(requirement);
		} catch (GenericEntityException e) {
			String errMsg = UtilProperties.getMessage(resourceError, "BLErrorWhenCreateRequirement", locale) + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	
    	// update requirement role
    	GenericValue requirementRole = delegator.makeValue("RequirementRole");
    	requirementRole.put("requirementId", requirementId);
    	requirementRole.put("roleTypeId", "INTERNAL_ORGANIZATIO");
    	requirementRole.put("partyId", org);
    	requirementRole.put("fromDate", UtilDateTime.nowTimestamp());
    	try {
    		delegator.createOrStore(requirementRole);
		} catch (GenericEntityException e) {
			String errMsg = UtilProperties.getMessage(resourceError, "BLErrorWhenCreateRequirement", locale) + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	
    	// create requirement role with customer
    	String customerId = (String) context.get("customerId");
    	if (UtilValidate.isNotEmpty(customerId)) {
    		GenericValue requirementRoleCustomer = delegator.makeValue("RequirementRole");
    		requirementRoleCustomer.put("requirementId", requirementId);
    		requirementRoleCustomer.put("partyId", customerId);
    		requirementRoleCustomer.put("roleTypeId", "CUSTOMER");
    		requirementRoleCustomer.put("fromDate", UtilDateTime.nowTimestamp());
        	try {
        		delegator.createOrStore(requirementRoleCustomer);
    		} catch (GenericEntityException e) {
    			String errMsg = UtilProperties.getMessage(resourceError, "BLErrorWhenCreateRequirement", locale) + e.toString();
    			Debug.logError(e, errMsg, module);
    			return ServiceUtil.returnError(errMsg);
    		}
    	}
    	
    	List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItemTmp);
		} catch (ParseException e1) {
			Debug.logError(e1.toString(), module);
			return ServiceUtil.returnError("JqxWidgetSevices convert json to List error!");
		}
		Boolean checkHasItem = false;
		if (UtilValidate.isNotEmpty(listProducts) && !listProducts.isEmpty()) {
			for (Map<String, Object> item : listProducts){
				BigDecimal quantity = BigDecimal.ZERO;				
				if (item.containsKey("quantity")){
					quantity = new BigDecimal((String)item.get("quantity"));
				}
				String productId = null;
				if (item.containsKey("productId")){
					productId = (String)item.get("productId");
				}
				if (UtilValidate.isNotEmpty(productId) && quantity.compareTo(BigDecimal.ZERO) > 0) {
					// create requirement item
					checkHasItem = true;
					GenericValue objProduct = null;
					try {
						objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS: findOne Product error!");
					}
					GenericValue requirementItem = delegator.makeValue("RequirementItem");
					requirementItem.put("productId", productId);
					if (item.containsKey("uomId")){
						if (ProductUtil.isWeightProduct(delegator, productId)) {
							requirementItem.put("weightUomId", item.get("uomId"));
							requirementItem.put("quantityUomId", objProduct.get("quantityUomId"));
							requirementItem.put("weight", quantity);
							requirementItem.put("quantity", BigDecimal.ONE);
						} else {
							requirementItem.put("quantityUomId", item.get("uomId"));
							requirementItem.put("weightUomId", objProduct.get("weightUomId"));
							requirementItem.put("quantity", quantity);
						}
					}
					if (item.containsKey("expireDate")){
	    				requirementItem.put("expireDate", new Timestamp(new Long((String)item.get("expireDate"))));
	    			}
//					if (item.containsKey("fromExpiredDate")){
//						mapItems.put("fromExpiredDate", (String)item.get("fromExpiredDate"));
//					}
//					if (item.containsKey("toExpiredDate")){
//						mapItems.put("toExpiredDate", (String)item.get("toExpiredDate"));
//					}
					BigDecimal unitCost = BigDecimal.ZERO;
					if (item.containsKey("unitCost")){
						if (UtilValidate.isNotEmpty(item.get("unitCost"))) {
							unitCost = new BigDecimal((String)item.get("unitCost"));
						}
					}
					requirementItem.put("unitCost", unitCost);
					
					if (item.containsKey("description")) {
						requirementItem.put("description", (String)item.get("description"));
					}
	    			requirementItem.put("requirementId", requirementId);
	    			requirementItem.put("statusId", statusId);
	    			delegator.setNextSubSeqId(requirementItem, "reqItemSeqId", 5, 1);
	    			requirementItem.put("currencyUomId", currencyUomId);
	    			try {
						delegator.create(requirementItem);
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLErrorWhenCreateRequirement", locale));
					}
				}
			}
    	}
		if (!checkHasItem){
			Debug.logError(UtilProperties.getMessage(resourceError, "BLItemNotFoundToAddToRequirement", (Locale)context.get("locale")), module);
			return ServiceUtil.returnError("JqxWidgetSevices convert json to List error!");
		}
    	
        Map<String, Object> result = FastMap.newInstance();
    	result.put("requirementId", requirementId);
		
        // calculate total
        BigDecimal grandTotal = RequirementWorker.calculateReuqirementGrandTotal(delegator, requirementId);
        requirement.put("grandTotal", grandTotal);
        try {
			delegator.store(requirement);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling store requirement: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	return result;
	}
	
	public static Map<String,Object> createNotifyRequirement(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String messages = (String)context.get("messages");
		String roleTypeProperties = (String)context.get("roleTypeProperties");
		String header = "";
		String action = "";
//		String target = "";
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<String> listPartyTos = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, roleTypeProperties), delegator);
		if (listPartyTos.isEmpty()){
	    	return ServiceUtil.returnError("OLBUS: Party to receive notify not found.");
	    }
		GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		GenericValue requirementType = delegator.findOne("RequirementType", false, UtilMisc.toMap("requirementTypeId", requirement.getString("requirementTypeId")));
		header = UtilProperties.getMessage(resource, "Has", (Locale)context.get("locale"))+ " " + StringUtil.wrapString((String)requirementType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(resource, messages, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(resource, "RequirementId", (Locale)context.get("locale")) +": [" +requirementId+"]";
		action = "viewRequirementDetail?requirementId="+requirementId;
//		target = "requirementId="+requirementId;	
		LocalDispatcher dispatcher = ctx.getDispatcher();
		for (String partyId : listPartyTos) {
    		Map<String, Object> mapContext = new HashMap<String, Object>();
    		mapContext.put("partyId", partyId);
    		mapContext.put("action", action);
    		mapContext.put("targetLink", "");
    		mapContext.put("header", header);
    		mapContext.put("ntfType", "ONE");
    		mapContext.put("userLogin", userLogin);
    		mapContext.put("openTime", UtilDateTime.nowTimestamp());
    		try {
    			dispatcher.runSync("createNotification", mapContext);
    		} catch (GenericServiceException e) {
    			e.printStackTrace();
    		}
		}
		
		result.put("requirementId", requirementId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRequirements(DispatchContext ctx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String org = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String requirementTypeId = null;
    	if (parameters.get("requirementTypeId") != null && parameters.get("requirementTypeId").length > 0){
    		requirementTypeId = (String)parameters.get("requirementTypeId")[0];
    		if (UtilValidate.isNotEmpty(requirementTypeId)){
    			EntityCondition typeCond = null;
    			if ("EXPORT_REQUIREMENT".equals(requirementTypeId)){
    				typeCond = EntityCondition.makeCondition("requirementTypeId", EntityOperator.IN, UtilMisc.toList(requirementTypeId, "PAY_REQUIREMENT"));
    			} else if ("RECEIVE_REQUIREMENT".equals(requirementTypeId)){
    				typeCond = EntityCondition.makeCondition("requirementTypeId", EntityOperator.IN, UtilMisc.toList(requirementTypeId, "BORROW_REQUIREMENT"));
    			} else {
    				typeCond = EntityCondition.makeCondition("requirementTypeId", EntityOperator.EQUALS, requirementTypeId);
    			}
	    		listAllConditions.add(typeCond);
    		}
    	}
    	String statusId = null;
    	if (parameters.get("statusId") != null && parameters.get("statusId").length > 0){
    		statusId = (String)parameters.get("statusId")[0];
    		if (UtilValidate.isNotEmpty(statusId)){
    			EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId);
        		listAllConditions.add(statusCond);
    		}
    	}
    	String reasonEnumId = null;
    	if (parameters.get("reasonEnumId") != null && parameters.get("reasonEnumId").length > 0){
    		reasonEnumId = (String)parameters.get("reasonEnumId")[0];
    		if (UtilValidate.isNotEmpty(reasonEnumId)){
    			EntityCondition cond = EntityCondition.makeCondition("reasonEnumId", EntityOperator.EQUALS, reasonEnumId);
    			listAllConditions.add(cond);
    		}
    	}
    	String eventId = null;
    	if (parameters.get("eventId") != null && parameters.get("eventId").length > 0){
    		eventId = (String)parameters.get("eventId")[0];
    		if (UtilValidate.isNotEmpty(eventId)){
    			EntityCondition cond = EntityCondition.makeCondition("eventId", EntityOperator.EQUALS, eventId);
    			listAllConditions.add(cond);
    		}
    	}
    	Security security = ctx.getSecurity();
    	List<GenericValue> listRequirements = new ArrayList<GenericValue>();
    	if (listSortFields.isEmpty()){
    		listSortFields.add("-requirementId");
    	}
        if (com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "ADMIN", "MODULE", "ACC_REQUIREMENT") || com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "UPDATE", "MODULE", "LOG_REQUIREMENT") || com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "ADMIN", "MODULE", "LOGISTICS")){
        	EntityCondition notCreateByCond = EntityCondition.makeCondition("createdByUserLogin", EntityOperator.NOT_EQUAL, userLogin.getString("userLoginId"));
        	EntityCondition createByCond = EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, userLogin.getString("userLoginId"));
        	EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("REQ_CANCELLED"));
        	EntityCondition notCreatedAndStatus = EntityCondition.makeCondition(UtilMisc.toList(notCreateByCond, statusCond), EntityOperator.AND);
        	EntityCondition createdAndStatus = EntityCondition.makeCondition(UtilMisc.toList(notCreatedAndStatus, createByCond), EntityOperator.OR);
    		listAllConditions.add(createdAndStatus);
        	listRequirements = delegator.findList("RequirementDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
        } else {
        	EntityCondition createByCond = EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, userLogin.getString("userLoginId"));
        	List<String> listFacilityIds = FacilityUtil.getFacilityManages(delegator, userLogin);
        	EntityCondition facilityCond = EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityIds);
        	EntityCondition allCond = EntityCondition.makeCondition(UtilMisc.toList(facilityCond, createByCond), EntityOperator.OR);
	    	listAllConditions.add(allCond);
	    	listRequirements = delegator.findList("RequirementDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
        }
    	List<GenericValue> filter = new ArrayList<GenericValue>();
    	for (GenericValue req : listRequirements){
    		List<GenericValue> listByRoles = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("partyId", org, "roleTypeId", "INTERNAL_ORGANIZATIO", "requirementId", req.getString("requirementId"))), null, null, null, false);
    		listByRoles = EntityUtil.filterByDate(listByRoles);
    		if (!listByRoles.isEmpty()){
    			filter.add(req);
    		}
    	}
    	listRequirements = new ArrayList<GenericValue>();
    	if (!filter.isEmpty()){
    		listRequirements.addAll(filter);
    	}
		Map<String, Object> result = FastMap.newInstance();
		result.put("listIterator", listRequirements); 
		return result;
	}
	
	@SuppressWarnings({"unchecked"})
	public static Map<String, Object> jqGetRequirementItems(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String requirementId = null;
    	if (parameters.get("requirementId") != null && parameters.get("requirementId").length > 0){
    		requirementId = parameters.get("requirementId")[0];
    	}
    	if (requirementId != null && !"".equals(requirementId)){
    		listAllConditions.add(EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId));
    	}
    	List<GenericValue> listReqItems = delegator.findList("RequirementItemDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	successResult.put("listIterator", listReqItems);
    	return successResult;
	}
	
	public static Map<String, Object> getRequirementItems(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	String requirementId = (String)context.get("requirementId");
    	Locale locale = (Locale)context.get("locale");
    	Map<String, Object> successResult = FastMap.newInstance();
    	List<EntityCondition> listConds = new ArrayList<EntityCondition>();
    	EntityCondition idConds = EntityCondition.makeCondition("requirementId", requirementId);
    	EntityCondition statusConds = EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("REQ_CANCELLED", "REQ_REJECTED"));
    	listConds.add(idConds);
    	
    	
    	GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
    	if (UtilValidate.isEmpty(requirement)) {
    		return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLRequirementNotFound", locale));
		}
    	String facilityId = requirement.getString("facilityId");
    	/*if (UtilValidate.isEmpty(facilityId)) {
    		return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLFacilityRequiredNotFound", locale));
		}*/
    	String statusId = requirement.getString("statusId");
    	if (UtilValidate.isNotEmpty(statusId)){
    		if (!statusId.equals("REQ_CANCELLED") && !statusId.equals("REQ_REJECTED")){
    			listConds.add(statusConds);
    		}
    	}
    	List<GenericValue> listReqItems = new ArrayList<GenericValue>();
    	String requirementTypeId = requirement.getString("requirementTypeId");
		listReqItems = delegator.findList("RequirementItemDetail", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
    	
    	
    	List<Map<String, Object>> listRequirementItems = new ArrayList<Map<String, Object>>();
    	if (UtilValidate.isNotEmpty(facilityId)){
    		for (GenericValue item : listReqItems) {
    			String reqItemSeqId = item.getString("reqItemSeqId");
    			List<EntityCondition> conds = FastList.newInstance();
    			conds.addAll(listConds);
    			conds.add(EntityCondition.makeCondition("reqItemSeqId", reqItemSeqId));
    			GenericValue productFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", item.getString("productId"), "facilityId", facilityId));
        		Map<String, Object> map = FastMap.newInstance();
        		map.putAll(item);
    			if (UtilValidate.isNotEmpty(productFacility)){
        			map.put("expRequired", productFacility.getString("expRequired"));
            		map.put("mnfRequired", productFacility.getString("mnfRequired"));
            		map.put("lotRequired", productFacility.getString("lotRequired"));
        		}
    			if ("REQ_COMPLETED".equals(statusId) && "EXPORT_REQUIREMENT".equals(requirementTypeId)){
	    			if (UtilValidate.isNotEmpty(item.get("issuedQuantity"))) {
	    				BigDecimal convert = LogisticsProductUtil.getConvertPackingNumber(delegator, item.getString("productId"), item.getString("quantityUomId"), item.getString("baseQuantityUomId"));
	    				map.put("issuedQuantity", item.getBigDecimal("issuedQuantity").divide(convert, 0, RoundingMode.HALF_UP));
					}
	    			if (UtilValidate.isNotEmpty(item.get("issuedWeight"))) {
	    				BigDecimal convert = LogisticsProductUtil.getConvertWeightNumber(delegator, item.getString("productId"), item.getString("weightUomId"), item.getString("baseWeightUomId"));
	    				map.put("issuedWeight", item.getBigDecimal("issuedWeight").divide(convert, 0, RoundingMode.HALF_UP));
	    			}
    			}
    			List<Map<String, Object>> listDetail = FastList.newInstance();
    			if ("REQ_COMPLETED".equals(statusId)){
    				if ("RECEIVE_REQUIREMENT".equals(requirementTypeId)){
            			List<GenericValue> listItems = delegator.findList("RequirementItemShipmentReceiptGroupByExpMnfLot", EntityCondition.makeCondition(conds, EntityOperator.AND), null, null, null, false);
            			if (!listItems.isEmpty()){
            				listDetail.addAll(listItems);
            			}
            		} else if ("EXPORT_REQUIREMENT".equals(requirementTypeId) || "COMBINE_PRODUCT".equals(requirementTypeId)){
            			List<GenericValue> listItems = delegator.findList("RequirementItemIssuanceGroupByExpMnfLot", EntityCondition.makeCondition(conds, EntityOperator.AND), null, null, null, false);
            			if (!listItems.isEmpty()){
            				listDetail.addAll(listItems);
            			}
            		}
    			}
    			
    			map.put("rowDetail", listDetail);
				
    			listRequirementItems.add(map);
			}
    		successResult.put("listRequirementItems", listRequirementItems);
    	} else {
    		successResult.put("listRequirementItems", listReqItems);
    	}
    	return successResult;
	}
	
	public static Map<String, Object> getReasonByRequirementTypeId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String requirementTypeId = (String)context.get("requirementTypeId");
    	List<GenericValue> listEnumTypes = delegator.findList("RequirementEnumType", EntityCondition.makeCondition("requirementTypeId", requirementTypeId), UtilMisc.toSet("enumTypeId"), null, null, false);
    	listEnumTypes = EntityUtil.filterByDate(listEnumTypes);
    	List<GenericValue> listEnumReasons = new ArrayList<GenericValue>();
    	if (!listEnumTypes.isEmpty()){
    		if ("TRANSFER_REQUIREMENT".equals(requirementTypeId)) {
    			GenericValue userLogin = (GenericValue) context.get("userLogin");
    			if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, false)) {
    				List<GenericValue> listEnums = delegator.findList("Enumeration", EntityCondition.makeCondition("enumId", "IMPORT_FROM_DEPOSIT_WAREHOUSES"), null, null, null, false);
        			if (!listEnums.isEmpty()){
        				listEnumReasons.addAll(listEnums);
        			}
        			successResult.put("listEnumReasons", listEnumReasons);
        	    	return successResult;
    			}
			}
    		for (GenericValue item : listEnumTypes) {
    			EntityCondition cond1 = EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "RECEIVE_DISAGGR");
    			EntityCondition cond2 = EntityCondition.makeCondition("enumId", EntityOperator.NOT_EQUAL, "RECEIVE_AGGREGATED");
    			EntityCondition cond3 = EntityCondition.makeCondition("enumTypeId", item.getString("enumTypeId"));
    			List<EntityCondition> allConds = new ArrayList<EntityCondition>();
    			allConds.add(cond3);
    			allConds.add(cond2);
    			allConds.add(cond1);
    			List<GenericValue> listEnums = delegator.findList("Enumeration", EntityCondition.makeCondition(allConds), null, null, null, false);
    			if (!listEnums.isEmpty()){
    				listEnumReasons.addAll(listEnums);
    			}
			}
    	}
    	successResult.put("listEnumReasons", listEnumReasons);
    	return successResult;
	}
	public static class MTLUtil {
		final static List<String> groupPermissions_GT = Arrays.asList("HRMADMIN",
																"SALES_MANAGER",
																"SALESADMIN_MANAGER",
																"SALESADMIN_GT",
																"SALES_CSM_GT",
																"SALES_RSM_GT",
																"SALES_ASM_GT",
																"SALESSUP_GT",
																"SALESMAN_GT",
																"DISTRIBUTOR_ADMIN");
		final static List<String> groupPermissions_MT = Arrays.asList("HRMADMIN",
																"SALES_MANAGER",
																"SALESADMIN_MANAGER",
																"SALESADMIN_MT",
																"SALES_CSM_MT",
																"SALES_RSM_MT",
																"SALES_ASM_MT",
																"SALESSUP_MT",
																"SALESMAN_MT",
																"DISTRIBUTOR_ADMIN");
		public static boolean hasSecurityGroupPermission(Delegator delegator, String groupId, GenericValue userLogin, boolean above) {
			boolean result = false;
			EntityListIterator userLoginSecurityGroups = null;
			try {
				String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				
				List<String> groupPermissions = FastList.newInstance();
				if (groupPermissions_GT.contains(groupId)) {
					groupPermissions = groupPermissions_GT;
				} else if (groupPermissions_MT.contains(groupId)) {
					groupPermissions = groupPermissions_MT;
				}
				
				if (UtilValidate.isNotEmpty(userLogin)) {
					List<EntityCondition> conditions = FastList.newInstance();
					if (above) {
						List<String> groupIds = FastList.newInstance();
						for (String s : groupPermissions) {
							groupIds.add(s);
							if (s.contains(groupId)) {
								break;
							}
						}
						conditions.add(EntityCondition.makeCondition("groupId", EntityJoinOperator.IN, groupIds));
					} else {
						conditions.add(EntityCondition.makeCondition("groupId", EntityJoinOperator.EQUALS, groupId));
					}
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("userLoginId", userLogin.get("userLoginId"), "organizationId", organizationId)));
					EntityFindOptions findOptions = new EntityFindOptions();
					findOptions.setMaxRows(1);
					findOptions.setLimit(1);
					userLoginSecurityGroups = delegator.find("UserLoginSecurityGroup",
							EntityCondition.makeCondition(conditions), null, null, null, findOptions);
					if (userLoginSecurityGroups.getResultsTotalSize() != 0) {
						result = true;
					}
				}
			} catch (Exception e) {
				result = false;
			} finally {
				if (userLoginSecurityGroups != null) {
					try {
						userLoginSecurityGroups.close();
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
			}
			return result;
		}
	}
	
	
	public static Map<String, Object> logisticsSendRequirementNotify(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String requirementId = (String)context.get("requirementId");
    	String roleTypeId = (String)context.get("roleTypeId");
    	GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
    	String requirementTypeId = requirement.getString("requirementTypeId");
    	GenericValue requirementType = delegator.findOne("RequirementType", false, UtilMisc.toMap("requirementTypeId", requirementTypeId));
    	String header = ""; 
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	List<String> listPartyByRoles = com.olbius.basehr.util.SecurityUtil.getPartiesByRoles(roleTypeId, delegator);
    	Locale locale = (Locale)context.get("locale");
    	for (String partyId : listPartyByRoles){
			String mess = null;
			if ("REQ_CREATED".equals(requirement.getString("statusId"))){
				mess = "HasBeenCreated";
	    	} if ("REQ_PROPOSED".equals(requirement.getString("statusId"))){
	    		mess = "HasBeenProposed";
	    	} else if ("REQ_CONFIRMED".equals(requirement.getString("statusId"))) {
	    		mess = "HasBeenConfirmed";
	    	} else if ("REQ_APPROVED".equals(requirement.getString("statusId"))) {
	    		mess = "HasBeenApproved";
	    	} else if ("REQ_REJECTED".equals(requirement.getString("statusId"))) {
	    		mess = "HasBeenRejected";
	    	} else if ("REQ_CANCELLED".equals(requirement.getString("statusId"))) {
	    		mess = "HasBeenCancelled";
	    	} else if ("REQ_COMPLETED".equals(requirement.getString("statusId"))){
	    		mess = "HasBeenCompleted";
	    	}
	    	header = UtilProperties.getMessage(resource, "Has", locale)+ " " 
    				+ StringUtil.wrapString((String)requirementType.get("description", locale)).toString().toLowerCase() + " " 
    				+ StringUtil.wrapString(UtilProperties.getMessage(resource, mess, locale)).toString().toLowerCase() + ", " 
    				+ UtilProperties.getMessage(resource, "RequirementId", locale) +": [" +requirementId+"]";
			Map<String, Object> mapContext = new HashMap<String, Object>();
			mapContext.put("action", "viewRequirementDetail?requirementId="+requirementId);
			mapContext.put("partyId", partyId);
			mapContext.put("targetLink", "");
			mapContext.put("header", header);
			mapContext.put("ntfType", "ONE");
			mapContext.put("userLogin", userLogin);
			try {
				dispatcher.runSync("createNotification", mapContext);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: logisticsSendRequirementNotify error! " + e.toString());
			}
		}
    	Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementId", requirementId);
		return mapReturn;
	}
	
	public static Map<String, Object> changeRequirementStatus(DispatchContext ctx, Map<String, Object> context){
    	Delegator delegator = ctx.getDelegator();
    	String requirementId = (String)context.get("requirementId");
    	String statusId = (String)context.get("statusId");
    	String originFacilityId = (String)context.get("facilityId");
    	String contactMechId = (String)context.get("contactMechId");
    	String noteInfo = (String)context.get("noteInfo");
    	String createNotification = (String)context.get("createNotification");
    	String changeAssocsStatus = (String)context.get("changeAssocsStatus");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue requirement = null;
    	String oldStatusId = null;
    	String requirementTypeId = null;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling changeRequirementStatus service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError("OLBIUS: changeRequirementStatus error! " + e.toString());
		}
		
		oldStatusId = requirement.getString("statusId");
		requirementTypeId = requirement.getString("requirementTypeId");
		if (UtilValidate.isNotEmpty(requirement)){
			requirement.put("statusId", statusId);
			if (UtilValidate.isNotEmpty(originFacilityId)){
				requirement.put("facilityId", originFacilityId);
			}
			if (UtilValidate.isNotEmpty(contactMechId)){
				requirement.put("contactMechId", contactMechId);
			}
			try {
				requirement.store();
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error calling changeRequirementStatus service: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError("OLBIUS: changeRequirementStatus error! " + e.toString());
			}
		} 
		
		if (UtilValidate.isNotEmpty(noteInfo)){
			try {
				Map<String, Object> map = dispatcher.runSync("createNote", UtilMisc.toMap("userLogin", userLogin, "note", noteInfo, "partyId", userLogin.getString("partyId")));
				String noteId = (String)map.get("noteId");
				try {
					dispatcher.runSync("createRequirementNote", UtilMisc.toMap("userLogin", userLogin, "requirementId", requirementId, "noteId", noteId));
				} catch (GenericServiceException e){
					return ServiceUtil.returnError("OLBIUS: runsync service createRequirementNote error!");
				}
			} catch (GenericServiceException e){
				return ServiceUtil.returnError("OLBIUS: runsync service createNote error!");
			}
		}
    	
    	GenericValue requirementStatus = delegator.makeValue("RequirementStatus");
    	requirementStatus.put("requirementId", requirementId);
    	requirementStatus.put("statusId", statusId);
    	requirementStatus.put("statusDate", UtilDateTime.nowTimestamp());
		requirementStatus.put("statusUserLogin", userLogin.get("userLoginId"));
    	try {	
    		delegator.createOrStore(requirementStatus);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling changeRequirementStatus service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError("OLBIUS: changeRequirementStatus error! " + e.toString());
		}
    	try {
    		String changeItemStatus = (String)context.get("setItemStatus");
    		if ((UtilValidate.isEmpty(changeItemStatus) || (UtilValidate.isNotEmpty("changeItemStatus") && changeItemStatus.equals("Y")))){
    			Map<String, Object> mapTmp = UtilMisc.toMap("requirementId", requirementId, "statusId", statusId, "userLogin", (GenericValue)context.get("userLogin"));
    			dispatcher.runSync("changeAllRequirementItemStatus", mapTmp);
    		}
		} catch (GenericServiceException e) {
			String errMsg = "Fatal error calling changeRequirementStatus service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError("change requirement item status error");
		}
    	successResult.put("requirementId", requirementId);
    	successResult.put("oldStatusId", oldStatusId);
    	successResult.put("statusId", statusId);
    	successResult.put("requirementTypeId", requirementTypeId);
    	successResult.put("createNotification", createNotification);
    	successResult.put("changeAssocsStatus", changeAssocsStatus);
    	successResult.put("reasonEnumId", requirement.getString("reasonEnumId"));
    	return successResult;
    }
	
	public static Map<String, Object> changeRequirementItemStatus(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String reqItemSeqId = (String)context.get("reqItemSeqId");
		String oldStatusId = null;
		GenericValue requirementItem = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
		oldStatusId = requirementItem.getString("statusId");
    	String statusId = (String)context.get("statusId");
    	
    	requirementItem.put("statusId", statusId);
    	delegator.store(requirementItem);
    	
		successResult.put("requirementId", requirementId);
		successResult.put("reqItemSeqId", reqItemSeqId);
    	successResult.put("oldStatusId", oldStatusId);
    	successResult.put("statusId", statusId);
    	return successResult;
	}
	
	public static Map<String, Object> changeAllRequirementItemStatus(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String requirementId = (String)context.get("requirementId");
    	String statusId = (String)context.get("statusId");
    	
    	List<String> listStatusCond = new ArrayList<String>();
    	List<EntityCondition> listConditions = FastList.newInstance();
    	
    	listStatusCond.add("REQ_CREATED");
    	listStatusCond.add("REQ_PROPOSED");
    	listStatusCond.add("REQ_APPROVED");
    	
    	listConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, listStatusCond));
    	listConditions.add(EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId));
    	
    	List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(listConditions, EntityOperator.AND), null, null, null, false);
    	for (GenericValue item : listReqItems) {
    			Map<String, Object> mapInput = UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", item.getString("reqItemSeqId"), "statusId", statusId, "userLogin", (GenericValue)context.get("userLogin"));
    			try {
    				dispatcher.runSync("changeRequirementItemStatus", mapInput);
    			} catch (GenericServiceException e) {
    				return ServiceUtil.returnError("change requirement item status error");
    			}
		}
		successResult.put("requirementId", requirementId);
    	return successResult;
	}
	

	@SuppressWarnings("unchecked")
	public static Map<String, Object> receiveProductFromRequirement(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String requirementId = (String)context.get("requirementId");
    	String facilityId = null;
    	
    	if (UtilValidate.isNotEmpty(context.get("facilityId"))) {
    		facilityId = (String)context.get("facilityId"); 
		}
    	String contactMechId = null;
    	if (UtilValidate.isNotEmpty(context.get("contactMechId"))) {
    		contactMechId = (String)context.get("contactMechId");
    	}
    	
		String listItems = null;
		if (UtilValidate.isNotEmpty(context.get("listRequirementItems"))) {
			listItems = (String)context.get("listRequirementItems");
		}
		
		String listPrAttrs = null;
		if (UtilValidate.isNotEmpty(context.get("listProductAttributes"))) {
			listPrAttrs = (String)context.get("listProductAttributes");
		}
		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		if (UtilValidate.isEmpty(facilityId)) {
			if (UtilValidate.isNotEmpty(requirement.get("facilityId"))) {
				facilityId = requirement.getString("facilityId");
			}
		}
		if (UtilValidate.isEmpty(contactMechId)) {
			if (UtilValidate.isNotEmpty(requirement.get("contactMechId"))) {
				contactMechId = requirement.getString("contactMechId");
			}
		}
		
		List<Map<String, Object>> listProducts = FastList.newInstance();
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItems);
		} catch (ParseException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: receiveProductFromRequirement - JqxWidgetSevices.convert error!");
		}
		
		List<Map<String, Object>> listProductAttrs = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listPrAttrs)) {
			try {
				listProductAttrs = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listPrAttrs);
			} catch (ParseException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: receiveProductFromRequirement - JqxWidgetSevices.convert error!");
			}
		}
		
		if (!listProducts.isEmpty() && ("REQ_APPROVED".equals(requirement.getString("statusId")) || "REQ_EXPORTED".equals(requirement.getString("statusId")) && "CHANGEDATE_REQUIREMENT".equals(requirement.getString("requirementTypeId"))) && UtilValidate.isNotEmpty(contactMechId) && UtilValidate.isNotEmpty(facilityId)){
			String reasonEnumId = requirement.getString("reasonEnumId");
			String shipmentTypeId = null;
			List<GenericValue> listShipmentAndEnums = delegator.findList("ShipmentTypeEnumDetail", EntityCondition.makeCondition(UtilMisc.toMap("enumId", reasonEnumId, "parentTypeId", "INCOMING_SHIPMENT")), null, null, null, false);
			listShipmentAndEnums = EntityUtil.filterByDate(listShipmentAndEnums);
			if (!listShipmentAndEnums.isEmpty()){
				shipmentTypeId = listShipmentAndEnums.get(0).getString("shipmentTypeId");
			} else {
				return ServiceUtil.returnError("OLBIUS: cannot get shipment type will be created with this reason enum " + reasonEnumId);
			}
			
			Map<String, Object> mapAttributes = FastMap.newInstance();
			for (Map<String, Object> reqItem : listProducts){
				String reqItemSeqId = null;
				if (reqItem.containsKey("reqItemSeqId")){
					reqItemSeqId = (String)reqItem.get("reqItemSeqId");
				}
				String productId = null;
				if (reqItem.containsKey("productId")){
					productId = (String)reqItem.get("productId");
				}
				BigDecimal quantity = BigDecimal.ZERO;
				String quantityStr = null;
				if (reqItem.containsKey("quantity")){
					quantityStr = (String)reqItem.get("quantity");
					quantity = new BigDecimal(quantityStr);
				}
				
				if (!listProductAttrs.isEmpty()) {
					List<Map<String, Object>> listAttributes = FastList.newInstance();
					for (Map<String, Object> map : listProductAttrs) {
						if (map.containsKey("productId")){
							String prId = (String)map.get("productId");
							if (UtilValidate.isNotEmpty(prId) && productId.equals(prId)) {
								listAttributes.add(map);
							}
						}
					}
					mapAttributes.put(productId, listAttributes);
				}
				
				if (UtilValidate.isNotEmpty(productId)) {
					if (UtilValidate.isNotEmpty(reqItemSeqId)) {
						// update
						
						GenericValue reqItemDB = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
						if ("REQ_EXPORTED".equals(requirement.getString("statusId")) && "CHANGEDATE_REQUIREMENT".equals(requirement.getString("requirementTypeId"))){
							reqItemDB.put("actualExecutedQuantity",quantity);
							reqItemDB.put("actualReceivedQuantity",quantity);
						} else {
							BigDecimal actualExecutedQuantity = BigDecimal.ZERO;
							BigDecimal actualExecutedWeight = BigDecimal.ZERO;
							if (ProductUtil.isWeightProduct(delegator, productId)){
								actualExecutedQuantity = BigDecimal.ONE;
								actualExecutedWeight = quantity;
							} else {
								actualExecutedQuantity = quantity;
							}
							if (UtilValidate.isNotEmpty(reqItemDB.getBigDecimal("actualExecutedQuantity"))){
								actualExecutedQuantity = reqItemDB.getBigDecimal("actualExecutedQuantity").add(actualExecutedQuantity);
							} 
							if (UtilValidate.isNotEmpty(reqItemDB.getBigDecimal("actualExecutedWeight"))){
								actualExecutedWeight = reqItemDB.getBigDecimal("actualExecutedWeight").add(actualExecutedWeight);
							}
							BigDecimal quantityCreated = reqItemDB.getBigDecimal("quantity");
							if (actualExecutedQuantity.compareTo(quantityCreated) > 0){
								return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BLProductHasBeenReceived", (Locale)context.get("locale")));
							}
							reqItemDB.put("actualExecutedQuantity",actualExecutedQuantity);
							reqItemDB.put("actualExecutedWeight", actualExecutedWeight);
							reqItemDB.put("actualReceivedQuantity",quantity);
						}
						if (quantity.compareTo(BigDecimal.ZERO) <= 0){
							reqItemDB.put("statusId", "REQ_COMPLETED");
						}
						delegator.store(reqItemDB);
					} else {
						// create new
					}
				}
			}
			
			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			Map<String, Object> mapShipment = UtilMisc.toMap("requirementId", requirementId, "shipmentTypeId", shipmentTypeId, "userLogin", system);
			String shipmentId = null;
			try {
				Map<String, Object> mapResultShipment = dispatcher.runSync("createShipmentFromRequirement", mapShipment);
				if (ServiceUtil.isError(mapResultShipment)){
					return ServiceUtil.returnError("OLBIUS: createShipmentFromRequirement error: requirementId - " + requirementId);
				}
				shipmentId = (String)mapResultShipment.get("shipmentId");
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: createShipmentFromRequirement error " + e.toString());
			}
			List<GenericValue> listReqShipments = FastList.newInstance();
			EntityCondition cond1 = EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId);
			EntityCondition cond2 = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(cond1);
			conds.add(cond2);
			try {
				listReqShipments = delegator.findList("RequirementShipment", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findList RequirementItemShipment error!");
			}
			if (UtilValidate.isNotEmpty(listReqShipments)) {
				for (GenericValue item : listReqShipments) {
					
					BigDecimal quantityShip = item.getBigDecimal("quantity");
					BigDecimal weightShip = item.getBigDecimal("weight");
					String reqItemSeqId = item.getString("reqItemSeqId");
					
					GenericValue objRequirementItem = null;
					try {
						objRequirementItem = delegator.findOne("RequirementItem", false,
								UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS: findOne RequirementItem error!");
					}
					String productId = objRequirementItem.getString("productId");
					
					List<Map<String, Object>> listAttribues = FastList.newInstance();
					if (mapAttributes.containsKey(productId)){
						listAttribues = (List<Map<String, Object>>)mapAttributes.get(productId);
					}
					
					if (!listAttribues.isEmpty()){
						// xu ly nhap sp co HSD, NSX, Lo SX
						BigDecimal quantityFree = quantityShip;
						if (ProductUtil.isWeightProduct(delegator, productId)) {
							quantityFree = weightShip;
						} 
						for (Map<String, Object> mapAttr : listAttribues) {
							Map<String, Object> attributes = FastMap.newInstance();
							String qtyStr = null;
							if (mapAttr.containsKey("quantity") && mapAttr.containsKey("productId")){
								qtyStr = (String)mapAttr.get("quantity");
								if (UtilValidate.isNotEmpty(qtyStr)) {
									BigDecimal quantityP = new BigDecimal(qtyStr); // quantity tuong ung se la can nang voi san pham can nang
									quantityFree = quantityFree.subtract(quantityP);
									if (quantityP.compareTo(BigDecimal.ZERO) > 0){
										for (String key : mapAttr.keySet()) {
											if ("expireDate".equals(key)){
												if (UtilValidate.isNotEmpty(mapAttr.get("expireDate"))) {
													String expStr = (String)mapAttr.get("expireDate");
													if (UtilValidate.isNotEmpty(expStr)) {
														Long expL = new Long (expStr);
														if (UtilValidate.isNotEmpty(expL)) {
															Timestamp exp = new Timestamp(expL);
															if (UtilValidate.isNotEmpty(exp)) {
																attributes.put(key, exp);
															}
														}
													}
												}
											} else if ("datetimeManufactured".equals(key)){
												if (UtilValidate.isNotEmpty(mapAttr.get("datetimeManufactured"))) {
													String expStr = (String)mapAttr.get("datetimeManufactured");
													if (UtilValidate.isNotEmpty(expStr)) {
														Long expL = new Long (expStr);
														if (UtilValidate.isNotEmpty(expL)) {
															Timestamp exp = new Timestamp(expL);
															if (UtilValidate.isNotEmpty(exp)) {
																attributes.put(key, exp);
															}
														}
													}
												}
											} else if ("lotId".equals(key)){ 
												String lotId = (String)mapAttr.get(key);
												GenericValue objLot = null;
												try {
													objLot = delegator.findOne("Lot", false,
															UtilMisc.toMap("lotId", lotId));
												} catch (GenericEntityException e) {
													Debug.logError(e.toString(), module);
													return ServiceUtil.returnError("OLBIUS: findOne Lot error!");
												}
												if (UtilValidate.isEmpty(objLot)) {
													// create new lot
													objLot = delegator.makeValue("Lot");
													objLot.put("lotId", lotId);
													objLot.put("creationDate", UtilDateTime.nowTimestamp());
													delegator.create(objLot); 
												}
												attributes.put(key, lotId);
											} else {
												attributes.put(key, mapAttr.get(key));
											}
										}
										String shipmentItemSeqId = item.getString("shipmentItemSeqId");
										Map<String, Object> map = FastMap.newInstance();
										map.put("reqItemSeqId", reqItemSeqId);
										map.put("requirementId", requirementId);
										map.put("productId", productId);
										map.put("shipmentItemSeqId", shipmentItemSeqId);
										map.put("quantityAccepted", quantityP);
										if (ProductUtil.isWeightProduct(delegator, productId)) {
											map.put("amountAccepted", quantityP);
											map.put("quantityAccepted", BigDecimal.ONE);
										} 
										map.put("quantityExcess", BigDecimal.ZERO);
										map.put("quantityRejected", BigDecimal.ZERO);
										map.put("quantityQualityAssurance", BigDecimal.ZERO);
										map.put("ownerPartyId", company);
										map.put("statusId", null);
										map.put("userLogin", system);
										map.put("facilityId", facilityId);
										map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
										map.put("unitCost", objRequirementItem.getBigDecimal("unitCost"));
										map.put("purCost", BigDecimal.ZERO);
										map.put("shipmentId", shipmentId);
										map.putAll(attributes);
										try {
											dispatcher.runSync("receiveInventoryProduct", map);
										} catch (GenericServiceException e) {
											Debug.logError(e.toString(), module);
											return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
										}
									}
								}
							}
						}
						if (quantityFree.compareTo(BigDecimal.ZERO) < 0){
							return ServiceUtil.returnError("OLBIUS: receiveProductFromRequirement - quantity not true!");
						} else if (quantityFree.compareTo(BigDecimal.ZERO) > 0){
							String shipmentItemSeqId = item.getString("shipmentItemSeqId");
							Map<String, Object> map = FastMap.newInstance();
							map.put("reqItemSeqId", reqItemSeqId);
							map.put("requirementId", requirementId);
							map.put("productId", productId);
							map.put("quantityAccepted", quantityFree);
							if (ProductUtil.isWeightProduct(delegator, productId)) {
								map.put("amountAccepted", quantityFree);
								map.put("quantityAccepted", BigDecimal.ONE);
							} 
							map.put("shipmentItemSeqId", shipmentItemSeqId);
							map.put("quantityExcess", BigDecimal.ZERO);
							map.put("quantityRejected", BigDecimal.ZERO);
							map.put("quantityQualityAssurance", BigDecimal.ZERO);
							map.put("ownerPartyId", company);
							map.put("statusId", null);
							map.put("userLogin", system);
							map.put("facilityId", facilityId);
							map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
							map.put("unitCost", objRequirementItem.getBigDecimal("unitCost"));
							map.put("purCost", BigDecimal.ZERO);
							map.put("shipmentId", shipmentId);
							try {
								dispatcher.runSync("receiveInventoryProduct", map);
							} catch (GenericServiceException e) {
								Debug.logError(e.toString(), module);
								return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
							}
						}
					} else {
						Map<String, Object> map = FastMap.newInstance();
						map.put("reqItemSeqId", reqItemSeqId);
						map.put("requirementId", requirementId);
						map.put("productId", productId);
						map.put("quantityAccepted", quantityShip);
						if (ProductUtil.isWeightProduct(delegator, productId)) {
							map.put("amountAccepted", weightShip);
							map.put("quantityAccepted", BigDecimal.ONE);
						} 
						String shipmentItemSeqId = item.getString("shipmentItemSeqId");
						map.put("shipmentItemSeqId", shipmentItemSeqId);
						
						map.put("quantityExcess", BigDecimal.ZERO);
						map.put("quantityRejected", BigDecimal.ZERO);
						map.put("quantityQualityAssurance", BigDecimal.ZERO);
						map.put("ownerPartyId", company);
						map.put("statusId", null);
						map.put("userLogin", system);
						map.put("facilityId", facilityId);
						map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
						map.put("unitCost", objRequirementItem.getBigDecimal("unitCost"));
						map.put("purCost", BigDecimal.ZERO);
						map.put("shipmentId", shipmentId);
						try {
							dispatcher.runSync("receiveInventoryProduct", map);
						} catch (GenericServiceException e) {
							Debug.logError(e.toString(), module);
							return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
						}
					}
				}
			}
				
			if (!"RECEIVE_DISAGGR".equals(reasonEnumId) && !"RECEIVE_AGGREGATED".equals(reasonEnumId) && !"RECEIVE_EXCHANGED".equals(reasonEnumId) && !"RECEIVE_STOCKEVENT".equals(reasonEnumId) && !"CHANGEDATE_PRODOUTOFDATE".equals(reasonEnumId)){
				try {
		    		dispatcher.runSync("createInvoiceFromShipmentRequirement", UtilMisc.toMap("requirementId", requirementId, "shipmentId", shipmentId, "userLogin", system));
				} catch (GenericServiceException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS - createInvoiceFromShipmentRequirement");
				}
			}
			boolean check = checkRequirementToComplete(delegator, requirementId);
			if (check){
				Map<String, Object> mapUpdateReq = UtilMisc.toMap("requirementId", requirementId, "statusId", "REQ_COMPLETED", "facilityId", facilityId, "contactMechId", contactMechId, "userLogin", (GenericValue)context.get("userLogin"));
				try {
					dispatcher.runSync("changeRequirementStatus", mapUpdateReq);
				} catch (GenericServiceException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS: changeRequirementStatus error!");
				}
			} else {
				Map<String, Object> mapUpdateReq = UtilMisc.toMap("requirementId", requirementId, "statusId", "REQ_RECEIVED", "facilityId", facilityId, "contactMechId", contactMechId, "userLogin", (GenericValue)context.get("userLogin"));
				try {
					dispatcher.runSync("changeRequirementStatus", mapUpdateReq);
				} catch (GenericServiceException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS: changeRequirementStatus error requirementId= " + requirementId);
				}
			}
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementId", requirementId);
		return mapReturn;
	}

	public static Map<String, Object> createShipmentFromRequirement(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String requirementId = (String)context.get("requirementId");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue requirement = null;
    	String shipmentId = null;
    	String shipmentTypeId = (String)context.get("shipmentTypeId");
		try {
			requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		} catch (GenericEntityException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS - createShipmentFromRequirement - findOne Requirement error!");
		}
		if (UtilValidate.isNotEmpty(requirement)){
			String partyIdFrom = null;
	    	// get organization or requirement
	    	List<GenericValue> listReqRoles = FastList.newInstance();
			try {
				listReqRoles = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId, "roleTypeId", "INTERNAL_ORGANIZATIO")), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS - createShipmentFromRequirement - findList RequirementRole error!");
			}
	    	listReqRoles = EntityUtil.filterByDate(listReqRoles);
	    	if (listReqRoles.isEmpty()){
	    		return ServiceUtil.returnError("OLBIUS: Cannot find organization of this Requirement " + requirementId);
	    	}
	    	partyIdFrom = listReqRoles.get(0).getString("partyId");
        	// Create shipment from requirement
        	Map<String, Object> mapCreateShipment = FastMap.newInstance();
        	GenericValue shipmentType = null;
			try {
				shipmentType = delegator.findOne("ShipmentType", false, UtilMisc.toMap("shipmentTypeId", shipmentTypeId));
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS - createShipmentFromRequirement - findOne ShipmentType error!");
			}
        	if (UtilValidate.isNotEmpty(shipmentType.getString("parentTypeId"))){
        		if ("INCOMING_SHIPMENT".equals(shipmentType.getString("parentTypeId"))){
        			mapCreateShipment.put("destFacilityId", requirement.getString("facilityId"));
                	mapCreateShipment.put("destContactMechId", requirement.getString("contactMechId"));
                	mapCreateShipment.put("partyIdTo", partyIdFrom);
                	mapCreateShipment.put("statusId", "PURCH_SHIP_CREATED");
        		} else if ("OUTGOING_SHIPMENT".equals(shipmentType.getString("parentTypeId"))){
        			mapCreateShipment.put("originFacilityId", requirement.getString("facilityId"));
                	mapCreateShipment.put("originContactMechId", requirement.getString("contactMechId"));
                	mapCreateShipment.put("partyIdFrom", partyIdFrom);
                	mapCreateShipment.put("statusId", "SHIPMENT_INPUT");
        		}
        	}
        	mapCreateShipment.put("shipmentTypeId", shipmentTypeId);
        	mapCreateShipment.put("primaryRequirementId", requirementId);
        	mapCreateShipment.put("estimatedShipDate", requirement.getTimestamp("requirementStartDate"));
        	mapCreateShipment.put("estimatedArrivalDate", requirement.getTimestamp("requirementStartDate"));
        	mapCreateShipment.put("estimatedShipCost", requirement.getBigDecimal("estimatedBudget"));
        	mapCreateShipment.put("currencyUomId", requirement.getString("currencyUomId"));
        	mapCreateShipment.put("defaultWeightUomId", "WT_kg");
        	mapCreateShipment.put("userLogin", userLogin);
        	Map<String, Object> mapShipment = FastMap.newInstance();
        	try {
        		mapShipment = dispatcher.runSync("createShipment", mapCreateShipment);
        		if (UtilValidate.isNotEmpty(mapShipment.get("shipmentId"))){
        			shipmentId = (String)mapShipment.get("shipmentId");
        		}
    		} catch (GenericServiceException e) {
    			Debug.logError(e.toString(), module);
    			return ServiceUtil.returnError("OLBIUS - createShipmentFromRequirement - createShipment error!");
    		}
    		// Create shipment item map to requirement item
    		EntityCondition cond1 = EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId);
    		EntityCondition cond2 = EntityCondition.makeCondition("actualExecutedQuantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
    		List<EntityCondition> conds = FastList.newInstance();
    		conds.add(cond1);
    		conds.add(cond2);
    		List<GenericValue> listRequirementItems;
			try {
				listRequirementItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS - createShipmentFromRequirement - findList RequirementItem error!");
			} 
    		if (!listRequirementItems.isEmpty()){
    			for (GenericValue item : listRequirementItems){
    				// Create shipment item
    				Map<String, Object> mapShipmentItem = FastMap.newInstance();
    				mapShipmentItem.put("shipmentId", shipmentId);
    				mapShipmentItem.put("productId", item.getString("productId"));
    				if ("INCOMING_SHIPMENT".equals(shipmentType.getString("parentTypeId"))){
    					mapShipmentItem.put("quantity", (BigDecimal)item.get("quantity"));
    					mapShipmentItem.put("weight", (BigDecimal)item.get("weight"));
    				} else {
    					mapShipmentItem.put("quantity", (BigDecimal)item.get("actualExecutedQuantity"));
    					mapShipmentItem.put("weight", (BigDecimal)item.get("actualExecutedWeight"));
    				}
    				mapShipmentItem.put("userLogin", userLogin);
    				String shipmentItemSeqId = null;
    				try {
    					Map<String, Object> mapShipmentItemResult = dispatcher.runSync("createShipmentItem", mapShipmentItem);
    					shipmentItemSeqId = (String)mapShipmentItemResult.get("shipmentItemSeqId");
					} catch (GenericServiceException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS - createShipmentFromRequirement - createShipmentItem error!");
					}
    				// Create mapping
    				GenericValue requirementItemShipment = delegator.makeValue("RequirementShipment");
    				requirementItemShipment.put("shipmentId", shipmentId);
    				requirementItemShipment.put("shipmentItemSeqId", shipmentItemSeqId);
    				requirementItemShipment.put("requirementId", requirementId);
    				requirementItemShipment.put("reqItemSeqId", item.getString("reqItemSeqId"));
    				requirementItemShipment.put("quantity", (BigDecimal)item.get("actualExecutedQuantity"));
    				requirementItemShipment.put("weight", (BigDecimal)item.get("actualExecutedWeight"));
    				try {
						delegator.create(requirementItemShipment);
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS - createShipmentFromRequirement - create RequirementShipment error!");
					}
    			}
    		} 
		} else {
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLRequirementNotFound", (Locale)context.get("locale")));
		}
    	successResult.put("requirementId", requirementId);
    	successResult.put("shipmentId", shipmentId);
    	return successResult;
	 }
	
	public static Map<String, Object> createInvoiceFromShipmentRequirement(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String shipmentId = (String) context.get("shipmentId");
        String requirementId= (String) context.get("requirementId");
        try {
        	GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
            String invoiceTypeId;
            // get the return header
            GenericValue requirement = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);                                                  
            String reasonEnumId = requirement.getString("reasonEnumId");
            List<GenericValue> listInvItemTypes = delegator.findList("EnumerationInvoiceItemType", EntityCondition.makeCondition(UtilMisc.toMap("enumId", reasonEnumId)), null, null, null, false);
            listInvItemTypes = EntityUtil.filterByDate(listInvItemTypes);
            String invoiceItemTypeId = null;
            if (!listInvItemTypes.isEmpty()){
            	invoiceItemTypeId = listInvItemTypes.get(0).getString("invoiceItemTypeId");
            } else {
            	return ServiceUtil.returnError("OLBIUS: Invoice item type not found for the reason of this requirement " + requirementId);
            }
            List<GenericValue> listInvTypes = delegator.findList("EnumerationInvoiceType", EntityCondition.makeCondition(UtilMisc.toMap("enumId", reasonEnumId)), null, null, null, false);
            listInvTypes = EntityUtil.filterByDate(listInvTypes);
            if (!listInvTypes.isEmpty()){
            	invoiceTypeId = listInvTypes.get(0).getString("invoiceTypeId");
            } else {
            	return ServiceUtil.returnError("OLBIUS: Invoice type not found for the reason of this requirement " + requirementId);
            }
            String partyId = null;
            List<GenericValue> listByRoles = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", "INTERNAL_ORGANIZATIO", "requirementId", requirementId)), null, null, null, false);
    		listByRoles = EntityUtil.filterByDate(listByRoles);
    		if (!listByRoles.isEmpty()){
    			partyId = listByRoles.get(0).getString("partyId");
    		} else {
    			return ServiceUtil.returnError("OLBIUS: Cannot find party to assign invoice");
    		}
            Map<String, Object> input = UtilMisc.<String, Object>toMap("invoiceTypeId", invoiceTypeId, "statusId", "INVOICE_IN_PROCESS");
            GenericValue shipmentType = delegator.findOne("ShipmentType", false, UtilMisc.toMap("shipmentTypeId", shipment.getString("shipmentTypeId")));
            List<GenericValue> billItems = new ArrayList<GenericValue>();
            if ("INCOMING_SHIPMENT".equals(shipmentType.getString("parentTypeId"))){
            	billItems = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
            	input.put("partyId", partyId);
            	input.put("partyIdFrom", "_NA_");
    		} else if ("OUTGOING_SHIPMENT".equals(shipmentType.getString("parentTypeId"))){
    			billItems = delegator.findList("ItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)), null, null, null, false);
    			input.put("partyId", "_NA_");
    			input.put("partyIdFrom", partyId);
    		}
            input.put("currencyUomId", requirement.get("currencyUomId"));
            input.put("invoiceDate", UtilDateTime.nowTimestamp());
            input.put("description", requirement.get("description"));
            input.put("userLogin", userLogin);

            // call the service to create the invoice
            Map<String, Object> serviceResults = dispatcher.runSync("createInvoice", input);
            if (ServiceUtil.isError(serviceResults)) {
                return ServiceUtil.returnError("OLBIUS: Create invoice from requirement error", null, null, serviceResults);
            }
            String invoiceId = (String) serviceResults.get("invoiceId");
            
        	if (billItems.isEmpty()){
        		return ServiceUtil.returnError("OLBIUS: bill items not found for requirementId " + requirementId);
        	}
            for (GenericValue item : billItems) {
                boolean shipmentReceiptFound = false;
                boolean itemIssuanceFound = false;
                if ("ShipmentReceipt".equals(item.getEntityName())) {
                    shipmentReceiptFound = true;
                } else if ("ItemIssuance".equals(item.getEntityName())) {
                    itemIssuanceFound = true;
                } else {
                    Debug.logError("Unexpected entity " + item + " of type " + item.getEntityName(), module);
                }
                GenericValue requirementItem = null;
                if (shipmentReceiptFound) {
                	requirementItem = item.getRelatedOne("RequirementItem", true);
                } else if (itemIssuanceFound) {
                    GenericValue shipmentItem = item.getRelatedOne("ShipmentItem", true);
                    GenericValue requirementShipment = EntityUtil.getFirst(shipmentItem.getRelated("RequirementShipment", null, null, false));
                    requirementItem = requirementShipment.getRelatedOne("RequirementItem", true);
                }
                if (requirementItem == null) continue; 
                GenericValue product = requirementItem.getRelatedOne("Product", true);
                String requireAmount = product.getString("requireAmount");
                
                BigDecimal unitCost = requirementItem.getBigDecimal("unitCost");

                BigDecimal quantity = BigDecimal.ZERO;
                if (shipmentReceiptFound) {
                	if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
                		quantity = item.getBigDecimal("amountAccepted"); 
                	} else
                		quantity = item.getBigDecimal("quantityAccepted"); 
                } else if (itemIssuanceFound) {
                	if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
                		quantity = item.getBigDecimal("weight");
                	} else
                		quantity = item.getBigDecimal("quantity"); 
                }
                String invoiceItemSeqId = null;
                // create the invoice item for this shipment receipt
                input = UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemTypeId", invoiceItemTypeId, "quantity", quantity);
                input.put("amount", unitCost);
                input.put("productId", requirementItem.get("productId"));
                input.put("taxableFlag", product.get("taxable"));
                input.put("description", requirementItem.get("description"));
                input.put("userLogin", userLogin);
                serviceResults = dispatcher.runSync("createInvoiceItem", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError("OLBIUS: create invoice item error", null, null, serviceResults);
                }
                invoiceItemSeqId = (String) serviceResults.get("invoiceItemSeqId");
                input = UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", requirementItem.get("reqItemSeqId"),
                        "invoiceId", invoiceId);
                input.put("invoiceItemSeqId", invoiceItemSeqId); // turn the int into a string with ("" + int) hack
                input.put("quantity", quantity);
                input.put("amount", unitCost);
                input.put("userLogin", userLogin);
                if (shipmentReceiptFound) {
                    input.put("shipmentReceiptId", item.get("receiptId"));
                }
                serviceResults = dispatcher.runSync("createRequirementItemBilling", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError("OLBIUS: createRequirementItemBilling error", null, null, serviceResults);
                }
            }

            // return the invoiceId
            Map<String, Object> results = ServiceUtil.returnSuccess();
            results.put("invoiceId", invoiceId);
            if (!"RECEIVE_OTHER".equals(reasonEnumId)){
                // Set the invoice to READY
                serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", "INVOICE_READY", "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError("OLBIUS: setInvoiceStatus error", null, null, serviceResults);
                }
            	GenericValue invoice = delegator.findOne("Invoice", false, UtilMisc.toMap("invoiceId", invoiceId));
            	invoice.set("statusId", "INVOICE_PAID");
            	invoice.set("newStatusId", "INV_PAID_NEW");
            	invoice.set("isVerified", "Y");
            	invoice.store();
            }
            return results;
        } catch (GenericServiceException e) {
            return ServiceUtil.returnError("OLBIUS: create invoice from requirement error" + e.getMessage());
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("OLBIUS: create invoice from requirement error" + e.getMessage());
        }
    }

    public static Map<String, Object> createAndVerifyPaymentFromInvoice(DispatchContext ctx, Map<String, Object> context) {
	    Delegator delegator = ctx.getDelegator();
	    LocalDispatcher dispatcher = ctx.getDispatcher();
	    String invoiceId = (String) context.get("invoiceId");
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
        try {
            GenericValue invoice = EntityUtil.getFirst(delegator.findList("InvoiceAndTotalAmountView", EntityCondition.makeCondition("invoiceId", invoiceId), null, null, null, false));
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("paymentMethodTypeId", "CASH"));
            conds.add(EntityCondition.makeCondition("partyId", invoice.get("partyIdFrom")));
            GenericValue paymentMethod = EntityUtil.getFirst(delegator.findList("PaymentMethod", EntityCondition.makeCondition(conds), null, null, null, false));
            BigDecimal amount = invoice.getBigDecimal("totalAmount");
            Map<String, Object> input = UtilMisc.<String, Object>toMap("userLogin", userLogin, "amount", amount, "statusId", "PMNT_RECEIVED");
            input.put("currencyUomId", invoice.getString("currencyUomId"));
            input.put("partyIdTo", invoice.get("partyIdFrom"));
            input.put("partyIdFrom", invoice.get("partyId"));
            input.put("paymentMethodTypeId", "CASH");
            input.put("paymentMethodId", paymentMethod.get("paymentMethodId"));
            input.put("paymentTypeId", "CUSTOMER_PAYMENT");
            input.put("effectiveDate", UtilDateTime.nowTimestamp());
            input.put("paidDate", UtilDateTime.nowTimestamp());
            return dispatcher.runSync("createPayment", input);
        } catch (GenericEntityException | GenericServiceException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
    }
	
	public static Map<String, Object> exportProductFromRequirement(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String requirementId = (String)context.get("requirementId");
    	String facilityId = (String)context.get("facilityId");
    	String contactMechId = (String)context.get("contactMechId");
		String listItems = (String)context.get("listRequirementItems");
		GenericValue userLogin = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
		JSONArray listItemTmp = JSONArray.fromObject(listItems);
		GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		
		if ("REQ_APPROVED".equals(requirement.getString("statusId"))){
			// create shipment 
			String reasonEnumId = requirement.getString("reasonEnumId");
			String shipmentTypeId = null;
			List<GenericValue> listShipmentAndEnums = delegator.findList("ShipmentTypeEnum", EntityCondition.makeCondition(UtilMisc.toMap("enumId", reasonEnumId)), null, null, null, false);
			listShipmentAndEnums = EntityUtil.filterByDate(listShipmentAndEnums);
			if (!listShipmentAndEnums.isEmpty()){
				shipmentTypeId = listShipmentAndEnums.get(0).getString("shipmentTypeId");
			} else {
				return ServiceUtil.returnError("OLBIUS: cannot get shipment type will be created with this reason enum " + reasonEnumId);
			}
			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			Map<String, Object> mapShipment = UtilMisc.toMap("requirementId", requirementId, "shipmentTypeId", shipmentTypeId, "userLogin", system);
			String shipmentId = null;
			try {
				Map<String, Object> mapResultShipment = dispatcher.runSync("createShipmentFromRequirement", mapShipment);
				shipmentId = (String)mapResultShipment.get("shipmentId");
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: createShipmentFromRequirement error " + e.toString());
			}
			// store 
			for (int j = 0; j < listItemTmp.size(); j++){
				JSONObject reqItem = listItemTmp.getJSONObject(j);
				String reqItemSeqId = reqItem.getString("reqItemSeqId");
				GenericValue reqItemDB = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
				BigDecimal exportQuantity = new BigDecimal(reqItem.getString("exportQuantity"));
				BigDecimal unitCost = new BigDecimal(reqItem.getString("unitCost"));
				String productId = reqItemDB.getString("productId");
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				String requireAmount = product.getString("requireAmount");
				String baseWeightUomId = product.getString("weightUomId");
				String weightUomId = reqItemDB.getString("weightUomId");
				String quantityUomId = reqItemDB.getString("quantityUomId");
				String baseQuantityUomId = product.getString("quantityUomId");
				
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					if (UtilValidate.isNotEmpty(baseWeightUomId) && UtilValidate.isNotEmpty(weightUomId)) {
						exportQuantity = exportQuantity.multiply(LogisticsProductUtil.getConvertWeightNumber(delegator, productId, weightUomId, baseWeightUomId));
					}
					if (UtilValidate.isNotEmpty(reqItemDB.getBigDecimal("actualExecutedQuantity"))){
						reqItemDB.put("actualExecutedWeight", reqItemDB.getBigDecimal("actualExecutedWeight").add(exportQuantity));
					} else {
						reqItemDB.put("actualExecutedWeight", exportQuantity);
					}
					if (UtilValidate.isNotEmpty(reqItemDB.getBigDecimal("actualExecutedQuantity"))){
						reqItemDB.put("actualExecutedQuantity", reqItemDB.getBigDecimal("actualExecutedQuantity").add(BigDecimal.ONE));
					} else {
						reqItemDB.put("actualExecutedQuantity", BigDecimal.ONE);
					}
				} else {
					BigDecimal convert = ProductUtil.getConvertPackingNumber(delegator, productId, quantityUomId, baseQuantityUomId);
					exportQuantity = exportQuantity.multiply(convert);
					if (UtilValidate.isNotEmpty(reqItemDB.getBigDecimal("actualExecutedQuantity"))){
						reqItemDB.put("actualExecutedQuantity", reqItemDB.getBigDecimal("actualExecutedQuantity").add(exportQuantity));
					} else {
						reqItemDB.put("actualExecutedQuantity", exportQuantity);
					}
				}
				
				reqItemDB.put("unitCost", unitCost);
				delegator.store(reqItemDB);
				
				Map<String, Object> mapShipmentItem = FastMap.newInstance();
				mapShipmentItem.put("shipmentId", shipmentId);
				mapShipmentItem.put("productId", productId);
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					mapShipmentItem.put("quantity", BigDecimal.ONE);
					mapShipmentItem.put("weight", exportQuantity);
				} else {
					mapShipmentItem.put("quantity", exportQuantity);
				}
				mapShipmentItem.put("userLogin", system);
				String shipmentItemSeqId = null;
				try {
					Map<String, Object> mapShipmentItemResult = dispatcher.runSync("createShipmentItem", mapShipmentItem);
					shipmentItemSeqId = (String)mapShipmentItemResult.get("shipmentItemSeqId");
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS - Create shipment item error");
				}
				// Create mapping
				GenericValue requirementItemShipment = delegator.makeValue("RequirementShipment");
				requirementItemShipment.put("shipmentId", shipmentId);
				requirementItemShipment.put("shipmentItemSeqId", shipmentItemSeqId);
				requirementItemShipment.put("requirementId", requirementId);
				requirementItemShipment.put("reqItemSeqId", reqItemDB.getString("reqItemSeqId"));
				requirementItemShipment.put("quantity", exportQuantity);
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					requirementItemShipment.put("quantity", BigDecimal.ONE);
					requirementItemShipment.put("weight", exportQuantity);
				} else {
					requirementItemShipment.put("quantity", exportQuantity);
				}
				delegator.create(requirementItemShipment);
				
				// create issues
				Map<String, Object> mapIssuance = FastMap.newInstance();
				mapIssuance.put("shipmentItemSeqId", shipmentItemSeqId);
				mapIssuance.put("shipmentId", shipmentId);
				mapIssuance.put("inventoryItemId", reqItem.getString("inventoryItemId"));
				mapIssuance.put("quantity", exportQuantity);
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					mapIssuance.put("quantity", BigDecimal.ONE);
					mapIssuance.put("weight", exportQuantity);
				} else {
					mapIssuance.put("quantity", exportQuantity);
				}
				mapIssuance.put("requirementId", requirementId);
				mapIssuance.put("reqItemSeqId", reqItemSeqId);
				mapIssuance.put("issuedByUserLoginId", userLogin.getString("userLoginId"));
				mapIssuance.put("userLogin", userLogin);
				mapIssuance.put("affectAccounting", true);
				try {
					dispatcher.runSync("createItemIssuance", mapIssuance);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS - Create item issuance error");
				}
			}
			
			// update shipment to packed
			mapShipment = FastMap.newInstance();
			mapShipment.put("userLogin", userLogin);
			mapShipment.put("shipmentId", shipmentId);
			mapShipment.put("statusId", "SHIPMENT_PACKED");
			try {
				dispatcher.runSync("updateShipment", mapShipment);
			} catch (GenericServiceException e){
				return ServiceUtil.returnError("OLBIUS: updateShipment error " + e.toString());
			}
			// update shipment to shipped
			mapShipment = FastMap.newInstance();
			mapShipment.put("userLogin", userLogin);
			mapShipment.put("shipmentId", shipmentId);
			mapShipment.put("statusId", "SHIPMENT_SHIPPED");
			try {
				dispatcher.runSync("updateShipment", mapShipment);
			} catch (GenericServiceException e){
				return ServiceUtil.returnError("OLBIUS: updateShipment error " + e.toString());
			}
			List<GenericValue> listShipmentItems = delegator.findList("ShipmentItem", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)), null, null, null, false);
			for (GenericValue item : listShipmentItems){
				List<GenericValue> listItemIssuance = delegator.findList("ItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", item.getString("shipmentItemSeqId"))), null, null, null, false);
				for (GenericValue issue : listItemIssuance){
					Map<String, Object> mapDetail = FastMap.newInstance();
					List<GenericValue> list = delegator.findList("RequirementShipment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "requirementId", requirementId, "shipmentItemSeqId", item.getString("shipmentItemSeqId"))), null, null, null, false);
					if (!list.isEmpty()){
						String reqItemSeqId = list.get(0).getString("reqItemSeqId");
						mapDetail.put("reqItemSeqId", reqItemSeqId);
						mapDetail.put("requirementId", requirementId);
					}
					String productId = item.getString("productId");
					GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					String requireAmount = product.getString("requireAmount");
					if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
						mapDetail.put("amountOnHandDiff", item.getBigDecimal("weight").negate());
						mapDetail.put("quantityOnHandDiff", new BigDecimal(-1));
						mapDetail.put("availableToPromiseDiff", new BigDecimal(-1));
					} else {
						mapDetail.put("quantityOnHandDiff", new BigDecimal(issue.getString("quantity")).negate());
						mapDetail.put("availableToPromiseDiff", new BigDecimal(issue.getString("quantity")).negate());
					}
					mapDetail.put("userLogin", userLogin);
					mapDetail.put("shipmentId", shipmentId);
					mapDetail.put("shipmentItemSeqId", item.getString("shipmentItemSeqId"));
					mapDetail.put("inventoryItemId", issue.getString("inventoryItemId"));
					mapDetail.put("itemIssuanceId", issue.getString("itemIssuanceId"));
					
					try {
						Map<String, Object> mapDetailResult = dispatcher.runSync("createInventoryItemDetail", mapDetail);
						String inventoryItemDetailSeqId = (String)mapDetailResult.get("inventoryItemDetailSeqId");
						GenericValue invDetail = delegator.findOne("InventoryItemDetail", false, UtilMisc.toMap("inventoryItemId", issue.getString("inventoryItemId"), "inventoryItemDetailSeqId", inventoryItemDetailSeqId));
						invDetail.put("reasonEnumId", requirement.getString("reasonEnumId"));
						delegator.store(invDetail);
					} catch (GenericServiceException e){
						return ServiceUtil.returnError("OLBIUS: createInventoryItemDetail error " + e.toString());
					}
				}
			}
			
			if (!"EXPORT_DISAGGREGATED".equals(reasonEnumId) && !"EXPORT_AGGREGATED".equals(reasonEnumId) && !"EXPORT_EXCHANGED".equals(reasonEnumId)){
				try {
		    		dispatcher.runSync("createInvoiceFromShipmentRequirement", UtilMisc.toMap("requirementId", requirementId, "shipmentId", shipmentId, "userLogin", userLogin));
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS - createInvoiceFromShipmentRequirement");
				}
			}
			
			Map<String, Object> mapUpdateReq = UtilMisc.toMap("requirementId", requirementId, "statusId", "REQ_COMPLETED", "facilityId", facilityId, "contactMechId", contactMechId, "userLogin", (GenericValue)context.get("userLogin"));
			try {
				dispatcher.runSync("changeRequirementStatus", mapUpdateReq);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: changeRequirementStatus error " + e.toString());
			}
			
			Map<String, Object> mapSendNotifyReq = UtilMisc.toMap("requirementId", requirementId, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleTypeId.receiveMsg.requirement.approved"), "userLogin", (GenericValue)context.get("userLogin"));
			try {
				dispatcher.runSync("logisticsSendRequirementNotify", mapSendNotifyReq);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: logisticsSendRequirementNotify error " + e.toString());
			}
		} else {
			return ServiceUtil.returnError("OLBIUS: requirement has been exported");
		}
		
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementId", requirementId);
		return mapReturn;
	}
	
	public static Map<String, Object> updateFacilityToRequirement(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String requirementId = (String)context.get("requirementId");
    	String facilityId = (String)context.get("facilityId");
    	String contactMechId = (String)context.get("contactMechId");
		GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		if (UtilValidate.isNotEmpty(contactMechId) && UtilValidate.isNotEmpty(facilityId)){
			requirement.put("facilityId", facilityId);
			requirement.put("contactMechId", contactMechId);
			delegator.store(requirement);
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementId", requirementId);
		return mapReturn;
	}
	
	public static Map<String,Object> sendNotifyToLogStorekeeperNewRequirement(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	String requirementId = (String)context.get("requirementId");
    	Delegator delegator = ctx.getDelegator();
    	String facilityId = null;
    	GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
    	facilityId = requirement.getString("facilityId");
    	List<String> listPartyIds = LogisticsPartyUtil.getStorekeeperOfFacility(delegator, facilityId);
    	String messages = "HasBeenAssignToYourFacility";
    	String action = "";
    	GenericValue requirementType = delegator.findOne("RequirementType", false, UtilMisc.toMap("requirementTypeId", requirement.getString("requirementTypeId")));
    	if (!listPartyIds.isEmpty()){
			LocalDispatcher dispatcher = ctx.getDispatcher();
			action = "viewRequirementDetail?requirementId="+requirementId;
			for (String partyId : listPartyIds) {
				Map<String, Object> mapContext = new HashMap<String, Object>();
				String header = UtilProperties.getMessage(resource, "Has", (Locale)context.get("locale"))+ " " + StringUtil.wrapString((String)requirementType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(resource, messages, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(resource, "RequirementId", (Locale)context.get("locale")) +": [" +requirementId+"]" + ", "+ UtilProperties.getMessage(resource, "FacilityId", (Locale)context.get("locale")) +": [" +facilityId+"]";
//				String target = "requirementId="+requirementId;
        		mapContext.put("partyId", partyId);
        		mapContext.put("action", action);
        		mapContext.put("targetLink", "");
        		mapContext.put("header", header);
        		mapContext.put("ntfType", "ONE");
        		mapContext.put("userLogin", (GenericValue)context.get("userLogin"));
        		mapContext.put("openTime", UtilDateTime.nowTimestamp());
        		try {
        			dispatcher.runSync("createNotification", mapContext);
        		} catch (GenericServiceException e) {
        			e.printStackTrace();
        		}
			}
    	}
    	return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getRequirementItemToTransfer(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	String requirementId = (String)context.get("requirementId");
    	List<EntityCondition> listConds = new ArrayList<EntityCondition>();
    	EntityCondition idConds = EntityCondition.makeCondition("requirementId", requirementId);
    	listConds.add(idConds);
    	Boolean getAllStatus = (Boolean)context.get("getAllStatus");
    	if (UtilValidate.isNotEmpty(getAllStatus) && !getAllStatus){
    		EntityCondition statusConds = EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("REQ_CANCELLED", "REQ_REJECTED"));
    		listConds.add(statusConds);
    	}
    	List<GenericValue> listRequirementItems = new ArrayList<GenericValue>();
    	List<GenericValue> listReqItems = delegator.findList("RequirementItemAndTransfer", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
    	if (!listReqItems.isEmpty()){
    		for (GenericValue item : listReqItems) {
				BigDecimal quantityCreated = BigDecimal.ZERO;
				BigDecimal quantityCreate = BigDecimal.ZERO;
				
				GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", item.getString("productId"))); 
				String requireAmount = objProduct.getString("requireAmount");
				Boolean reqAmount = false;
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					reqAmount = true;
				}
				BigDecimal quantity = item.getBigDecimal("quantity");
				if (reqAmount) quantity = item.getBigDecimal("weight");
				List<GenericValue> listTransferItemByReq = delegator.findList("TransferRequirement", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
				if (!listTransferItemByReq.isEmpty()){
					for (GenericValue reqItem : listTransferItemByReq) {
						// TODO may be must check quantity uom convert
						if (item.getString("requirementId").equals(reqItem.getString("requirementId")) && item.getString("reqItemSeqId").equals(reqItem.getString("reqItemSeqId"))){
							quantityCreated = quantityCreated.add(reqItem.getBigDecimal("quantity"));
							if (reqAmount) quantityCreated = quantityCreated.add(reqItem.getBigDecimal("weight"));
						}
					}
				}
				quantityCreate = quantity.subtract(quantityCreated);
				if (quantityCreate.compareTo(BigDecimal.ZERO) > 0){
					item.put("quantityCreate", quantityCreate);
					item.put("quantityCreated", quantityCreated);
					listRequirementItems.add(item);
				}
			}
    	}
    	Map<String, Object> successResult = FastMap.newInstance();
    	successResult.put("listRequirementItems", listRequirementItems);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getItemOfMultiReqToTransfer(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	List<Object> listIdTmp = (List<Object>)context.get("listRequirementIds");
    	Boolean isJson = false;
    	if (!listIdTmp.isEmpty()){  
    		if (listIdTmp.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	List<Map<String, String>> listMapReqIds = new ArrayList<Map<String, String>>();
    	List<String> listReqIds = new ArrayList<String>();
    	if (isJson){
    		String stringJson = "["+(String)listIdTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("requirementId")){
					listReqIds.add(item.getString("requirementId"));
				}
			}
    	} else {
    		listMapReqIds = (List<Map<String, String>>)context.get("listRequirementIds");
    		for (Map<String, String> item : listMapReqIds) {
    			listReqIds.add(item.get("requirementId"));
			}
    	}
    	List<EntityCondition> listConds = new ArrayList<EntityCondition>();
    	EntityCondition idConds = EntityCondition.makeCondition("requirementId",EntityOperator.IN, listReqIds);
    	listConds.add(idConds);
		EntityCondition statusConds = EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("REQ_CANCELLED", "REQ_REJECTED"));
		listConds.add(statusConds);
		List<GenericValue> listRequirementItems = new ArrayList<GenericValue>();
    	List<GenericValue> listReqItems = delegator.findList("RequirementItemAndTransfer", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
    	if (!listReqItems.isEmpty()){
    		for (GenericValue item : listReqItems) {
				BigDecimal quantityCreated = BigDecimal.ZERO;
				BigDecimal quantityCreate = BigDecimal.ZERO;
				BigDecimal quantity = item.getBigDecimal("quantity");
				List<GenericValue> listTransferItemByReq = delegator.findList("TransferRequirement", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", item.get("requirementId"))), null, null, null, false);
				if (!listTransferItemByReq.isEmpty()){
					for (GenericValue reqItem : listTransferItemByReq) {
						// TODO may be must check quantity uom convert
						if (item.getString("requirementId").equals(reqItem.getString("requirementId")) && item.getString("reqItemSeqId").equals(reqItem.getString("reqItemSeqId"))){
							quantityCreated = quantityCreated.add(reqItem.getBigDecimal("quantity"));
						}
					}
				}
				quantityCreate = quantity.subtract(quantityCreated);
				if (quantityCreate.compareTo(BigDecimal.ZERO) > 0){
					item.put("quantityCreate", quantityCreate);
					item.put("quantityCreated", quantityCreated);
					listRequirementItems.add(item);
				}
			}
    	}
    	Map<String, Object> successResult = FastMap.newInstance();
    	successResult.put("listRequirementItems", listRequirementItems);
    	return successResult;
	}
	
	public static Map<String, Object> updateRequirementFromDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String deliveryId = (String)context.get("deliveryId");
    	GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
    	String transferId = delivery.getString("transferId");
    	if (UtilValidate.isNotEmpty(transferId)){
    		List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
    		if (!listDeliveryItems.isEmpty()){
    			for (GenericValue item : listDeliveryItems) {
    				BigDecimal deliveredQuantity = item.getBigDecimal("actualDeliveredQuantity");
    				BigDecimal deliveredAmount = item.getBigDecimal("actualDeliveredAmount");
					String transferItemSeqId = item.getString("fromTransferItemSeqId");
					if (UtilValidate.isNotEmpty(transferItemSeqId)){
						List<GenericValue> listRequirementTransfers = delegator.findList("TransferRequirement", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId)), null, null, null, false);
						if (!listRequirementTransfers.isEmpty()){
							for (GenericValue reqItem : listRequirementTransfers) {
								GenericValue requirementItem = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", reqItem.getString("requirementId"), "reqItemSeqId", reqItem.getString("reqItemSeqId")));
								Map<String, Object> mapParam = FastMap.newInstance();
								mapParam.put("userLogin", (GenericValue)context.get("userLogin"));
								BigDecimal executedQty = BigDecimal.ZERO;
								BigDecimal executedWeight = BigDecimal.ZERO;
								if (UtilValidate.isNotEmpty(requirementItem.getBigDecimal("actualExecutedQuantity"))){
									mapParam.put("actualExecutedQuantity", deliveredQuantity.add(requirementItem.getBigDecimal("actualExecutedQuantity")));
									executedQty = deliveredQuantity.add(requirementItem.getBigDecimal("actualExecutedQuantity"));
								} else {
									mapParam.put("actualExecutedQuantity", deliveredQuantity);
									executedQty = deliveredQuantity;
								}
								if (UtilValidate.isNotEmpty(requirementItem.getBigDecimal("actualExecutedWeight"))){
									mapParam.put("actualExecutedWeight", deliveredQuantity.add(requirementItem.getBigDecimal("actualExecutedWeight")));
									executedWeight = deliveredAmount.add(requirementItem.getBigDecimal("actualExecutedWeight"));
								} else {
									mapParam.put("actualExecutedWeight", deliveredAmount);
									executedWeight = deliveredAmount;
								}
								mapParam.put("requirementId", reqItem.getString("requirementId"));
								mapParam.put("reqItemSeqId", reqItem.getString("reqItemSeqId"));
								
								if (executedQty.compareTo(requirementItem.getBigDecimal("quantity")) == 0 || (executedWeight.compareTo(requirementItem.getBigDecimal("weight"))== 0)){
									mapParam.put("statusId", "REQ_COMPLETED");
								}
								try {
									dispatcher.runSync("updateRequirementItem", mapParam);
								} catch (GenericServiceException e){
									return ServiceUtil.returnError("OLBIUS update requirement item error!");
								}
							}
						}
					}
				}
    		}
    	}
    	return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unused")
	public static Map<String, Object> updateRequirementItem(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher =  ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String sequenceId = (String) context.get("reqItemSeqId");
		String statusId = (String) context.get("statusId");
		String requirementId = (String) context.get("requirementId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		
		try {
			if(sequenceId == null){
				try {
					context.remove("reqItemSeqId");
					dispatcher.runSync("addRequirementItem", context);
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else{
				GenericValue item = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", sequenceId));
				if (item != null){
						if(quantity.compareTo(BigDecimal.ZERO) == 0 || quantity == null){
							context.put("statusId", "REQ_CANCELLED");
						} 
						item.setNonPKFields(context);
						delegator.store(item);
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: update requirementitem error! " + e.getMessage());
		}
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("requirementId", requirementId);
		return result;
	}
	
	public static Map<String,Object> checkRequirementStatus(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String requirementId = (String)context.get("requirementId");
		List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
		Boolean allCreated = true;
		Boolean allProposed = true;
		Boolean allExported = true;
		Boolean allReceived = true;
		Boolean allApproved = true;
		Boolean allConfirmed = true;
		Boolean allCompleted = true;
		Boolean allCancelled = true;
		Boolean allRejected = true;
		String newReqStatusId = null;
		if (!listReqItems.isEmpty()){
			for (GenericValue item : listReqItems) {
				if (!"REQ_REJECTED".equals(item.getString("statusId"))){
					allRejected = false;
					if (!"REQ_CANCELLED".equals(item.getString("statusId"))){
						allCancelled = false;
						if (!"REQ_COMPLETED".equals(item.getString("statusId"))){
							allCompleted = false;
							if (!"REQ_EXPORTED".equals(item.getString("statusId"))){
								allExported = false;
								if (!"REQ_RECEIVED".equals(item.getString("statusId"))){
									allReceived = false;
									if (!"REQ_APPROVED".equals(item.getString("statusId"))){
										allApproved = false;
										if (!"REQ_CONFIRMED".equals(item.getString("statusId"))){
											allConfirmed = false;
											if (!"REQ_PROPOSED".equals(item.getString("statusId"))){
												allProposed = false;
												if (!"REQ_CREATED".equals(item.getString("statusId"))){
													allCreated = false;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if (allRejected){
			newReqStatusId ="REQ_REJECTED";
		} else if (allCancelled){
			newReqStatusId ="REQ_CANCELLED";
		} else if (allCompleted){
			newReqStatusId ="REQ_COMPLETED";
		} else if (allExported){
			newReqStatusId ="REQ_EXPORTED";
		} else if (allReceived){
			newReqStatusId ="REQ_RECEIVED";
		} else if (allApproved){
			newReqStatusId ="REQ_APPROVED";
		} else if (allConfirmed){
			newReqStatusId ="REQ_CONFIRMED";
		}  else if (allProposed){
			newReqStatusId ="REQ_PROPOSED";
		} else if (allCreated){
			newReqStatusId = "REQ_CREATED";
		}
		
		GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		if (!requirement.getString("statusId").equals(newReqStatusId)){
			try {
				Map<String, Object> mapUpdateReq = UtilMisc.toMap("requirementId", requirementId, "statusId", newReqStatusId, "setItemStatus", "N", "userLogin", (GenericValue)context.get("userLogin"));
				dispatcher.runSync("changeRequirementStatus", mapUpdateReq);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: update requirement status error");
			}
		}
		result.put("requirementId", requirementId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> changeInventoryLabelFromRequirement(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
    	String inventoryItemLabelId = (String)context.get("inventoryItemLabelId");
    	String inventoryItemLabelTypeId = null;
    	Delegator delegator = ctx.getDelegator();
    	GenericValue invItemLabel = delegator.findOne("InventoryItemLabel", false, UtilMisc.toMap("inventoryItemLabelId", inventoryItemLabelId));
    	if (UtilValidate.isNotEmpty(invItemLabel)){
    		inventoryItemLabelTypeId = invItemLabel.getString("inventoryItemLabelTypeId");
    	} else {
    		return ServiceUtil.returnError("OLBIUS: inventoryItemLabelTypeId not found!");
    	}
		List<Object> mapInvs = (List<Object>)context.get("listInventoryItems");
    	Boolean isJson = false;
    	if (!mapInvs.isEmpty()){
    		if (mapInvs.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	List<Map<String, Object>> listInventoryItems = new ArrayList<Map<String,Object>>();
    	if (isJson){
			String stringJson = "["+(String)mapInvs.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("inventoryItemId")){
					mapItems.put("inventoryItemId", item.getString("inventoryItemId"));
				}
				if (item.containsKey("changeQuantity")){
					mapItems.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("requirementId")){
					mapItems.put("requirementId", item.getString("requirementId"));
				}
				if (item.containsKey("reqItemSeqId")){
					mapItems.put("reqItemSeqId", item.getString("reqItemSeqId"));
				}
				listInventoryItems.add(mapItems);
			}
    	} else {
    		listInventoryItems = (List<Map<String, Object>>)context.get("listInventoryItems");
    	}
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	LocalDispatcher dispatcher = ctx.getDispatcher();	
    	if (!listInventoryItems.isEmpty()){
    		for (Map<String, Object> item : listInventoryItems) {
    			String invId = (String)item.get("inventoryItemId");
    			BigDecimal quantity = new BigDecimal((String)item.get("quantity"));
    			GenericValue invItem = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", invId));
    			BigDecimal qoh = invItem.getBigDecimal("quantityOnHandTotal");
    			if (quantity.compareTo(qoh) == 0){
    				if (UtilValidate.isNotEmpty(item.get("inventoryItemId"))){
    					try {
    						dispatcher.runSync("createInventoryItemLabelAppl", UtilMisc.toMap("userLogin", userLogin, "inventoryItemId", invId, "inventoryItemLabelId", inventoryItemLabelId, "inventoryItemLabelTypeId", inventoryItemLabelTypeId));
    					} catch (GenericServiceException e) {
    						return ServiceUtil.returnError("OLBIUS: createInventoryItemLabelAppl error");
    					}
    				}
    				if (UtilValidate.isNotEmpty(item.get("requirementId")) && UtilValidate.isNotEmpty(item.get("reqItemSeqId"))){
    					String requirementId = (String)item.get("requirementId");
    					String reqItemSeqId = (String)item.get("reqItemSeqId");
    					Map<String, Object> mapParam = FastMap.newInstance();
    					mapParam.put("userLogin", (GenericValue)context.get("userLogin"));
    					BigDecimal executedQty = BigDecimal.ZERO;
    					GenericValue requirementItem = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
    					if (UtilValidate.isNotEmpty(requirementItem.getBigDecimal("actualExecutedQuantity"))){
    						mapParam.put("actualExecutedQuantity", quantity.add(requirementItem.getBigDecimal("actualExecutedQuantity")));
    						executedQty = quantity.add(requirementItem.getBigDecimal("actualExecutedQuantity"));
    					} else {
    						mapParam.put("actualExecutedQuantity", quantity);
    						executedQty = quantity;
    					}
    					mapParam.put("requirementId", requirementId);
    					mapParam.put("reqItemSeqId", reqItemSeqId);
    					
    					if (executedQty.compareTo(requirementItem.getBigDecimal("quantity")) == 0){
    						mapParam.put("statusId", "REQ_COMPLETED");
    					}
    					try {
    						dispatcher.runSync("updateRequirementItem", mapParam);
    					} catch (GenericServiceException e){
    						return ServiceUtil.returnError("OLBIUS update requirement item error!");
    					}
    				}
    			} else {
    				if (quantity.compareTo(qoh) < 0){
    					// Split inventory item
    					if (UtilValidate.isNotEmpty(item.get("requirementId")) && UtilValidate.isNotEmpty(item.get("reqItemSeqId"))){
        					// Subtract
    						String requirementId = (String)item.get("requirementId");
        					String reqItemSeqId = (String)item.get("reqItemSeqId");
	    					GenericValue tmpInvDetail = delegator.makeValue("InventoryItemDetail");
			                tmpInvDetail.set("inventoryItemId", invId);
			                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
			                tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
			                tmpInvDetail.set("availableToPromiseDiff", quantity.negate());
			                tmpInvDetail.set("quantityOnHandDiff", quantity.negate());
			                tmpInvDetail.set("requirementId", requirementId);
			                tmpInvDetail.set("reqItemSeqId", reqItemSeqId);
			                tmpInvDetail.create();
			                // Create new
			                Map<String, Object> mapNewInv = invItem.getAllFields();
			                mapNewInv.put("inventoryItemId", null);
			                mapNewInv.put("quantityOnHandTotal", null);
			                mapNewInv.put("availableToPromiseTotal", null);
			                mapNewInv.put("userLogin", (GenericValue)context.get("userLogin"));
			                String inventoryItemId = null;
			                try {
								Map<String, Object> mapInvReturn = dispatcher.runSync("createInventoryItem", mapNewInv);
								inventoryItemId = (String)mapInvReturn.get("inventoryItemId");
								GenericValue tmpInvDetailNew = delegator.makeValue("InventoryItemDetail");
								tmpInvDetailNew.set("inventoryItemId", inventoryItemId);
								tmpInvDetailNew.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
								tmpInvDetailNew.set("effectiveDate", UtilDateTime.nowTimestamp());
								tmpInvDetailNew.set("quantityOnHandDiff", quantity);
								tmpInvDetailNew.set("availableToPromiseDiff", quantity);
								tmpInvDetailNew.set("requirementId", requirementId);
								tmpInvDetailNew.set("reqItemSeqId", reqItemSeqId);
								tmpInvDetailNew.create();
								try {
		    						dispatcher.runSync("createInventoryItemLabelAppl", UtilMisc.toMap("userLogin", userLogin, "inventoryItemId", inventoryItemId, "inventoryItemLabelId", inventoryItemLabelId, "inventoryItemLabelTypeId", inventoryItemLabelTypeId));
		    					} catch (GenericServiceException e) {
		    						return ServiceUtil.returnError("OLBIUS: createInventoryItemLabelAppl error");
		    					}
								Map<String, Object> mapParam = FastMap.newInstance();
		    					mapParam.put("userLogin", (GenericValue)context.get("userLogin"));
		    					BigDecimal executedQty = BigDecimal.ZERO;
		    					GenericValue requirementItem = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
		    					if (UtilValidate.isNotEmpty(requirementItem.getBigDecimal("actualExecutedQuantity"))){
		    						mapParam.put("actualExecutedQuantity", quantity.add(requirementItem.getBigDecimal("actualExecutedQuantity")));
		    						executedQty = quantity.add(requirementItem.getBigDecimal("actualExecutedQuantity"));
		    					} else {
		    						mapParam.put("actualExecutedQuantity", quantity);
		    						executedQty = quantity;
		    					}
		    					mapParam.put("requirementId", requirementId);
		    					mapParam.put("reqItemSeqId", reqItemSeqId);
		    					
		    					if (executedQty.compareTo(requirementItem.getBigDecimal("quantity")) == 0){
		    						mapParam.put("statusId", "REQ_COMPLETED");
		    					}
		    					try {
		    						dispatcher.runSync("updateRequirementItem", mapParam);
		    					} catch (GenericServiceException e){
		    						return ServiceUtil.returnError("OLBIUS update requirement item error!");
		    					}
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: create inventory item error!");
							}
    					} else {
    						return ServiceUtil.returnError("OLBIUS: cannot change inventoryitemdetail with none requirement!");
    					}
    				} else {
    					return ServiceUtil.returnError("OLBIUS: cannot change this inventory item because it has been decrease quantity in facility!");
    				}
    			}
			}
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	return mapReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> createDeliveryFromRequirement(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	//Get Parameters
    	String requirementId = (String)context.get("requirementId");
    	GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
    	
    	String originFacilityId = (String)requirement.get("facilityId");
    	String destFacilityId = (String)requirement.get("destFacilityId");
    	String deliveryId = delegator.getNextSeqId("Delivery");
    	
//    	String partyIdFrom = (String)originFacility.get("ownerPartyId");
//    	String partyIdTo = (String)destFacility.get("ownerPartyId");
		String statusId = null;
		String itemStatusId = null;
		statusId = "DLV_CREATED";
		itemStatusId = "DELI_ITEM_CREATED";
    	String destContactMechId = null;
    	String originContactMechId = null;
    	if(UtilValidate.isNotEmpty((String)requirement.get("contactMechId"))) originContactMechId = (String)requirement.get("contactMechId");
    	if(UtilValidate.isNotEmpty((String)requirement.get("destContactMechId"))) destContactMechId = (String)requirement.get("destContactMechId");
    	String deliveryTypeId = null;
    	String reasonEnumId = requirement.getString("reasonEnumId");
    	if (UtilValidate.isEmpty(reasonEnumId)) return ServiceUtil.returnError("OLBIUS: Reason for this requirement " + requirementId + " not found!");
    	List<GenericValue> deliveryEnums = delegator.findList("DeliveryTypeEnum", EntityCondition.makeCondition(UtilMisc.toMap("enumId", reasonEnumId)), null, null, null, false);
    	if(deliveryEnums.isEmpty()){ 
    		deliveryTypeId = (String)context.get("deliveryTypeId");
    	} else {
    		deliveryTypeId = deliveryEnums.get(0).getString("deliveryTypeId");
    	}
    	if (UtilValidate.isEmpty(deliveryTypeId)) return ServiceUtil.returnError("OLBIUS: Delivery type not found!");
    	
    	//Make Delivery
    	GenericValue delivery = delegator.makeValue("Delivery");
    	Timestamp deliveryDate = requirement.getTimestamp("requirementStartDate");
    	Timestamp estimatedStartDate = null;
    	if (UtilValidate.isNotEmpty((Timestamp)(context.get("estimatedStartDate")))) estimatedStartDate = (Timestamp)(context.get("estimatedStartDate"));
    	if (UtilValidate.isEmpty(estimatedStartDate)) estimatedStartDate = requirement.getTimestamp("requiredByDate");
    	Timestamp estimatedArrivalDate = null;
    	if (UtilValidate.isNotEmpty((Timestamp)(context.get("estimatedArrivalDate")))) estimatedArrivalDate = (Timestamp)(context.get("estimatedArrivalDate"));
    	if (UtilValidate.isEmpty(estimatedArrivalDate)) estimatedArrivalDate = requirement.getTimestamp("requiredByDate");
    	
    	String defaultWeightUomId = null;
    	if (UtilValidate.isNotEmpty((String)context.get("defaultWeightUomId"))) defaultWeightUomId = (String)context.get("defaultWeightUomId");
    	if (UtilValidate.isEmpty(defaultWeightUomId)) defaultWeightUomId = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "default.weight.uom"); 
    	
    	delivery.put("deliveryDate", deliveryDate);
    	delivery.put("estimatedStartDate", estimatedStartDate);
    	delivery.put("estimatedArrivalDate", estimatedArrivalDate);
    	delivery.put("defaultWeightUomId", defaultWeightUomId);
    	Timestamp createDate = UtilDateTime.nowTimestamp();
    	delivery.put("createDate", createDate);
//    	delivery.put("partyIdFrom", partyIdFrom);
//    	delivery.put("partyIdTo", partyIdTo);
    	delivery.put("deliveryTypeId", deliveryTypeId);
    	delivery.put("originContactMechId", originContactMechId);
    	delivery.put("originFacilityId", originFacilityId);
    	delivery.put("destFacilityId", destFacilityId);
    	delivery.put("destContactMechId", destContactMechId);
    	delivery.put("deliveryId", deliveryId);
    	delivery.put("requirementId", requirementId);
    	delivery.put("statusId", statusId);
    	delivery.create();
    	
    	Boolean isJson = false;
    	List<Object> listItemTmp = (List<Object>)context.get("listRequirementItems");
    	if (!listItemTmp.isEmpty()){  
    		if (listItemTmp.get(0) instanceof String) isJson = true;
    	}
    	List<Map<String, String>> listProducts = new ArrayList<Map<String,String>>();
    	if (isJson){
    		String stringJson = "["+(String)listItemTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				HashMap<String, String> mapItems = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("requirementId")){
					mapItems.put("requirementId", item.getString("requirementId"));
				}
				if (item.containsKey("reqItemSeqId")){
					mapItems.put("reqItemSeqId", item.getString("reqItemSeqId"));
				}
				if (item.containsKey("expireDate")){
					mapItems.put("expireDate", item.getString("expireDate"));
				}
				if (item.containsKey("actualExpireDate")){
					mapItems.put("actualExpireDate", item.getString("actualExpireDate"));
				}
				if (item.containsKey("actualManufacturedDate")){
					mapItems.put("actualManufacturedDate", item.getString("actualManufacturedDate"));
				}
				if (item.containsKey("actualDeliveredQuantity")){
					mapItems.put("actualDeliveredQuantity", item.getString("actualDeliveredQuantity"));
				}
				if (item.containsKey("actualExportedQuantity")){
					mapItems.put("actualExportedQuantity", item.getString("actualExportedQuantity"));
				}
				if (item.containsKey("quantity")){
					mapItems.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("statusId")){
					mapItems.put("statusId", item.getString("statusId"));
				}
				listProducts.add(mapItems);
			}
    	} else {
    		listProducts = (List<Map<String, String>>)context.get("listRequirementItems");
    	}
    	
    	//Make Delivery Item
    	if (!listProducts.isEmpty()){
    		for (Map<String, String> item : listProducts){
    			GenericValue deliveryItem = delegator.makeValue("DeliveryItem");
    			deliveryItem.put("deliveryId", deliveryId);
		        delegator.setNextSubSeqId(deliveryItem, "deliveryItemSeqId", 5, 1);
    			deliveryItem.put("fromReqItemSeqId", item.get("reqItemSeqId"));
    			deliveryItem.put("fromRequirementId", item.get("requirementId"));
    			if (item.get("quantity") instanceof String) {
    				deliveryItem.put("quantity", BigDecimal.valueOf(Double.parseDouble(item.get("quantity"))));
    			} else {
    				deliveryItem.put("quantity", item.get("quantity"));
    			}
    			deliveryItem.put("actualExportedQuantity", null);
    			deliveryItem.put("actualDeliveredQuantity", null);
    			deliveryItem.put("statusId", itemStatusId);
    			deliveryItem.create();
    		}
    	} else {
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource, "NoProductSelected", (Locale)context.get("locale")));
    	}
    	
    	//Create DeliveryStatus
		String userLoginId = (String)userLogin.get("userLoginId");
		GenericValue deliveryStatus = delegator.makeValue("DeliveryStatus");
		deliveryStatus.put("deliveryStatusId", delegator.getNextSeqId("DeliveryStatus"));
		deliveryStatus.put("deliveryId", deliveryId);
		deliveryStatus.put("statusId", statusId);
		deliveryStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
		deliveryStatus.put("statusUserLogin", userLoginId);
		delegator.createOrStore(deliveryStatus);	
		
    	result.put("deliveryId", deliveryId);
    	return result;
	}
	
	public static Map<String,Object> quickCreateDeliveryFromRequirement(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	String deliveryId = null;
    	//Get Parameters
    	String requirementId = (String)context.get("requirementId");
    	List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	try {
    		Map<String, Object> mapTmp = dispatcher.runSync("createDeliveryFromRequirement", UtilMisc.toMap("userLogin", (GenericValue)context.get("userLogin"), "requirementId", requirementId, "listRequirementItems", listReqItems));
    		deliveryId = (String)mapTmp.get("deliveryId");
		} catch (GenericServiceException e){
			return ServiceUtil.returnError("OLBIUS: runsync createDeliveryFromRequirement service error!");
		}
    	result.put("deliveryId", deliveryId);
    	return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetRequirementDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	if (parameters.get("requirementId") != null && parameters.get("requirementId").length >= 1){
    		String requirementId = (String)parameters.get("requirementId")[0];
    		if (requirementId != null && !"".equals(requirementId)){
        		mapCondition.put("requirementId", requirementId);
        	}
    	}

    	if (parameters.get("statusId") != null && parameters.get("statusId").length > 0){
    		String statusId = (String)parameters.get("statusId")[0];
        	if (statusId != null && !"".equals(statusId)){
        		mapCondition.put("statusId", statusId);
        	}
    	}
    	String deliveryId = null;
    	if (parameters.get("deliveryId") != null && parameters.get("deliveryId").length >= 1){
    		deliveryId = (String)parameters.get("deliveryId")[0];
    	}
    	
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	// condition for transfer only
    	listAllConditions.add(EntityCondition.makeCondition("requirementId", EntityJoinOperator.NOT_EQUAL, null));
    	List<GenericValue> listDeliveries = new ArrayList<GenericValue>();
    	try {
    		listDeliveries = delegator.findList("DeliveryDetail", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
    		GenericValue deliveryToSelect = null;
    		if (deliveryId != null && !"".equals(deliveryId)){
    			deliveryToSelect = delegator.findOne("DeliveryDetail", false, UtilMisc.toMap("deliveryId", deliveryId));
    		}
    		if (!listDeliveries.isEmpty() && deliveryToSelect != null){
    			listDeliveries.set(0, deliveryToSelect);
    		}
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetRequirementDelivery service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listDeliveries);
    	return successResult;
	}
	
	public static Map<String,Object> autoCompletedDeliveryByRequirement(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	//Get Parameters
    	String requirementId = (String)context.get("requirementId");
    	List<GenericValue> listDeliveries = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
    	for (GenericValue dlv : listDeliveries) {
    		String deliveryId = dlv.getString("deliveryId");
    		dlv.set("statusId", "DLV_APPROVED");
    		List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
	    	for (GenericValue item : listDlvItems){
	    		item.set("statusId", "DELI_ITEM_APPROVED");
	    		delegator.store(item);
	    	}
	    	delegator.store(dlv);
    	}
    	List<GenericValue> shipments = delegator.findList("Shipment", EntityCondition.makeCondition(UtilMisc.toMap("primaryRequirementId", requirementId)), null, null, null, false);
    	if (shipments.isEmpty()) return ServiceUtil.returnError("OLBIUS: shipment not found!");
    	GenericValue shipment = shipments.get(0);
    	String shipmentId = shipment.getString("shipmentId");
    	List<GenericValue> listShipmentItems = delegator.findList("ShipmentItem", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)), null, null, null, false);
    	for (GenericValue dlv : listDeliveries) {
    		Observer o = new DeliveryObserver();
    		ItemSubject is = new DeliveryItemSubject();
    		is.attach(o);
			String deliveryId = dlv.getString("deliveryId");
			List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
	    	for (GenericValue item : listDlvItems){
	    		String shipmentItemSeqId = null;
	    		String reqItemSeqId = item.getString("fromReqItemSeqId");
	    		GenericValue reqItem = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
	    		for (GenericValue smtItem : listShipmentItems){
	    			if (reqItem.getString("productId").equals(smtItem.getString("productId"))){
	    				shipmentItemSeqId = smtItem.getString("shipmentItemSeqId");
	    				break;
	    			}
	    		}
	    		List<GenericValue> listItemIssuances = delegator.findList("ItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", shipmentItemSeqId)), null, null, null, false);
	    		String inventoryItemId = null;
	    		if (!listItemIssuances.isEmpty()){
	    			inventoryItemId = listItemIssuances.get(0).getString("inventoryItemId");
	    		}
	    		item.put("inventoryItemId", inventoryItemId);
	    		GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
	    		item.put("actualExpireDate", inv.getTimestamp("expireDate"));
	    		item.put("actualManufacturedDate", inv.getTimestamp("datetimeManufactured"));
	    		delegator.store(item);
	    		Map<String, Object> parameters = FastMap.newInstance();
	    		parameters.put("deliveryId", deliveryId);
	    		parameters.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
	    		parameters.put("delegator", delegator);
	    		parameters.put("actualExportedQuantity", item.getBigDecimal("quantity"));
	    		is.updateExportedQuantity(parameters);
	    	}
	    	dlv.set("shipmentId", shipmentId);
	    	delegator.store(dlv);
		}
    	for (GenericValue dlv : listDeliveries) {
    		Observer o = new DeliveryObserver();
    		ItemSubject is = new DeliveryItemSubject();
    		is.attach(o);
			String deliveryId = dlv.getString("deliveryId");
			List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
	    	for (GenericValue item : listDlvItems){
	    		Map<String, Object> parameters = FastMap.newInstance();
	    		parameters.put("deliveryId", deliveryId);
	    		parameters.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
	    		parameters.put("delegator", delegator);
	    		parameters.put("actualDeliveredQuantity", item.getBigDecimal("quantity"));
	    		is.updateDeliveredQuantity(parameters);
	    	}
		}
    	result.put("requirementId", requirementId);
    	return result;
	}
	
	
	public static Map<String,Object> quickCompleteRequirementReceive(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	//Get Parameters
    	String requirementId = (String)context.get("requirementId");
    	GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
    	if (UtilValidate.isEmpty(requirement)) return ServiceUtil.returnError("OLBIUS: requirement not found!");
    	String destFacilityId = requirement.getString("destFacilityId");
    	List<GenericValue> listRequirementItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
    	if (listRequirementItems.isEmpty()) return ServiceUtil.returnError("OLBIUS: requirement item not found!");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String deliveryId = null;
    	try {
			Map<String, Object> dlvTmp = dispatcher.runSync("quickCreateDeliveryFromRequirement", UtilMisc.toMap("userLogin", (GenericValue)context.get("userLogin"), "requirementId", requirementId));
			deliveryId = (String)dlvTmp.get("deliveryId");
		} catch (GenericServiceException e){
			return ServiceUtil.returnError("OLBIUS: runsync quickCreateDeliveryFromRequirement service error!");
		}
    	for (GenericValue item : listRequirementItems) {
    		String reqItemSeqId = item.getString("reqItemSeqId");
    		List<GenericValue> dlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromRequirementId", requirementId, "fromReqItemSeqId", reqItemSeqId)), null, null, null, false);
    		if (dlvItems.isEmpty()) return ServiceUtil.returnError("OLBIUS: delivery item not found!");
    		GenericValue dlvItem = dlvItems.get(0);
    		Observer o = new DeliveryObserver();
    		ItemSubject is = new DeliveryItemSubject();
    		is.attach(o);
    		Timestamp expireDate = item.getTimestamp("expireDate");
    		if (UtilValidate.isEmpty(expireDate)) return ServiceUtil.returnError("OLBIUS: expireDate not found!");
    		dlvItem.put("actualExpireDate", expireDate);
    		delegator.store(dlvItem);
    		Map<String, Object> parameters = FastMap.newInstance();
    		parameters.put("deliveryId", deliveryId);
    		parameters.put("deliveryItemSeqId", dlvItem.getString("deliveryItemSeqId"));
    		parameters.put("delegator", delegator);
    		parameters.put("actualExportedQuantity", item.getBigDecimal("quantity"));
    		is.updateExportedQuantity(parameters);
    		Map<String, Object> mapInv = FastMap.newInstance();
    		mapInv.put("productId", (String)item.get("productId"));
			mapInv.put("facilityId", destFacilityId);
			mapInv.put("inventoryItemTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "inventoryItemTypeId"));
			mapInv.put("userLogin", (GenericValue)context.get("userLogin"));
			mapInv.put("expireDate", expireDate);
			mapInv.put("datetimeReceived", UtilDateTime.nowTimestamp());
//			GenericValue shelfLife = delegator.findOne("ProductAttribute", false, UtilMisc.toMap("attrName", "SHELFLIFE", "productId", (String)item.get("productId")));
//			String attValue = null;
//			if (UtilValidate.isEmpty(shelfLife)){
//				attValue = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "shelfLife");
//			} else {
//				attValue = shelfLife.getString("attrValue");
//			}
//			BigDecimal deviation = new BigDecimal(attValue);
			Map<String, Object> mapDate = LogisticsProductUtil.getProductAttributeDate(delegator, destFacilityId, (String)item.get("productId"), expireDate);
			mapInv.put("datetimeManufactured", mapDate.get("manufacturedDate"));
			mapInv.put("unitCost", item.get("unitCost"));
			mapInv.put("purCost", BigDecimal.ZERO);
			mapInv.put("quantityAccepted", item.getBigDecimal("quantity"));
			mapInv.put("requirementId", requirementId);
			mapInv.put("reqItemSeqId", reqItemSeqId);
			mapInv.put("quantityRejected", BigDecimal.ZERO);
			mapInv.put("quantityExcess", BigDecimal.ZERO);
			mapInv.put("quantityQualityAssurance", BigDecimal.ZERO);
			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			mapInv.put("userLogin", system);
			try {
				dispatcher.runSync("receiveInventoryProduct", mapInv);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: receive inventory error " + e.toString());
			}
    		parameters.put("actualDeliveredQuantity", item.getBigDecimal("quantity"));
    		is.updateDeliveredQuantity(parameters);
    		
    		Map<String, Object> mapItemUpdate = FastMap.newInstance();
    		mapItemUpdate.put("actualExecutedQuantity", item.getBigDecimal("quantity"));
    		mapItemUpdate.put("requirementId", requirementId);
    		mapItemUpdate.put("reqItemSeqId", reqItemSeqId);
    		try {
				dispatcher.runSync("updateRequirementItem", UtilMisc.toMap("userLogin", (GenericValue)context.get("userLogin"), "actualExecutedQuantity", item.getBigDecimal("quantity"), "requirementId", requirementId, "reqItemSeqId", reqItemSeqId, "statusId", "REQ_COMPLETED"));
			} catch (GenericServiceException e){
				return ServiceUtil.returnError("OLBIUS: runsync updateRequirementItem service error!");
			}
    		delegator.store(item);
		}
    	result.put("requirementId", requirementId);
    	return result;
	}
	
	public static Map<String, Object> sendNotificationBackToRequirementCreatedParty(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String requirementId = (String)context.get("requirementId");
    	GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
    	String requirementTypeId = requirement.getString("requirementTypeId");
    	GenericValue requirementType = delegator.findOne("RequirementType", false, UtilMisc.toMap("requirementTypeId", requirementTypeId));
    	String header = ""; 
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
//		String targetLink = "requirementId="+requirementId;
		Map<String, Object> mapContext = new HashMap<String, Object>();
		GenericValue userLoginCreated = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", requirement.getString("createdByUserLogin")));
		String statusId = requirement.getString("statusId");
		String mss = "";
		if ("REQ_APPROVED".equals(statusId)){
			mss = "HasBeenApproved";
		} else if ("REQ_PROPOSED".equals(statusId)){
			mss = "HasBeenProposed";
		} else if ("REQ_CONFIRMED".equals(statusId)){
			mss = "HasBeenConfirmed";
		} else if ("REQ_REJECTED".equals(statusId)){
			mss = "HasBeenRejected";
		} else if ("REQ_COMPLETED".equals(statusId)){
			mss = "HasBeenCompleted";
		} else if ("REQ_CANCELLED".equals(statusId)){
			mss = "HasBeenCancelled";
		}
		header = UtilProperties.getMessage(resource, "Has", (Locale)context.get("locale"))+ " " + StringUtil.wrapString((String)requirementType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(resource, mss, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(resource, "RequirementId", (Locale)context.get("locale")) +": [" +requirementId+"]";
		mapContext.put("partyId", userLoginCreated.getString("partyId"));
		Security security = ctx.getSecurity();
		if (com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLoginCreated, "VIEW", "MODULE", "LOG_REQUIREMENT")) {
			mapContext.put("action", "viewRequirementDetail?requirementId="+requirementId);
		} else {
			mapContext.put("action", "viewReqDeliveryOrder?requirementId="+requirementId);
		}
		mapContext.put("targetLink", "");
		mapContext.put("header", header);
		mapContext.put("ntfType", "ONE");
		mapContext.put("userLogin", userLogin);
		try {
			dispatcher.runSync("createNotification", mapContext);
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: sendNotificationBackToCreatedParty error! " + e.toString());
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementId", requirementId);
		return mapReturn;
	}
	
	public static Map<String, Object> createRequirementNote(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String requirementId = (String)context.get("requirementId");
    	String noteId = (String)context.get("noteId");
    	GenericValue reqNote = delegator.findOne("RequirementNote", false, UtilMisc.toMap("requirementId", requirementId, "noteId", noteId));
    	if (UtilValidate.isEmpty(reqNote)){
    		reqNote = delegator.makeValue("RequirementNote");
    		reqNote.put("requirementId", requirementId);
    		reqNote.put("noteId", noteId);
    		delegator.createOrStore(reqNote);
    	} else {
    		return ServiceUtil.returnError("OLBIUS: Note existed");
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementId", requirementId);
		mapReturn.put("noteId", noteId);
		return mapReturn;
	}
	
	public static Map<String, Object> createRequirementReceiveInventoryAfterDisaggregate(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		EntityCondition cond1 = EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId);
		EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "REQ_CANCELLED");
		List<EntityCondition> listConds = UtilMisc.toList(cond1, cond2);
		List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(listConds), null, null, null, false);
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> map = FastMap.newInstance();
		GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		try {
			// auto create new requirement
			List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> listProductAssocs = new ArrayList<Map<String, Object>>();
			List<String> listProductIds = new ArrayList<String>();
			for (GenericValue item : listReqItems) {
				String productId = item.getString("productId");
				GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				String requireAmount = objProduct.getString("requireAmount");
				
				BigDecimal quantity = item.getBigDecimal("quantity");
				BigDecimal weight = item.getBigDecimal("weight");
				List<GenericValue> listProductChilds = LogisticsProductUtil.getListProdConfigItemProduct(delegator, productId);
				
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					BigDecimal amountUnit = LogisticsProductUtil.getProductConfigAmount(delegator, productId);
					quantity = weight.divide(amountUnit, RoundingMode.HALF_UP);
				}
				
				for (GenericValue pr : listProductChilds) {
					Map<String, Object> mapReqItem = FastMap.newInstance();
					
					String productIdChild = pr.getString("productId");
					mapReqItem.put("productId", productIdChild);
					if (!listProductIds.contains(productIdChild)) listProductIds.add(productIdChild);
					String requireAmountChild = pr.getString("requireAmount");
					if (UtilValidate.isNotEmpty(requireAmountChild) && "Y".equals(requireAmountChild)) {
						if (UtilValidate.isNotEmpty(pr.getBigDecimal("quantity"))) {
							if (UtilValidate.isNotEmpty(pr.getBigDecimal("amount"))) {
								mapReqItem.put("quantity", quantity.multiply(pr.getBigDecimal("amount").multiply(pr.getBigDecimal("quantity"))).toString());
							} else {
								mapReqItem.put("quantity", quantity.multiply(pr.getBigDecimal("quantity")).toString());
							}
						}
						mapReqItem.put("uomId", pr.getString("weightUomId"));
					} else {
						if (UtilValidate.isNotEmpty(pr.getBigDecimal("quantity"))) {
							mapReqItem.put("quantity", quantity.multiply(pr.getBigDecimal("quantity")).toString());
						}
						mapReqItem.put("uomId", pr.getString("quantityUomId"));
					}
					
					List<GenericValue> listAverageCost = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(UtilMisc.toMap("productId", pr.getString("productId"),  "facilityId", requirement.getString("facilityId"), "organizationPartyId", company, "productAverageCostTypeId", "SIMPLE_AVG_COST")), null, null, null, false);
					listAverageCost = EntityUtil.filterByDate(listAverageCost);
					if (!listAverageCost.isEmpty()){
						mapReqItem.put("unitCost", listAverageCost.get(0).getBigDecimal("averageCost").toString());
					} else {
						mapReqItem.put("unitCost", BigDecimal.ZERO.toString());
					}
					listProducts.add(mapReqItem);
				}
				Map<String, Object> mapAssocs = FastMap.newInstance();
				mapAssocs.put(productId, listProductChilds);
				listProductAssocs.add(mapAssocs);
			}
			List<Map<String, Object>> listProductTotals = new ArrayList<Map<String, Object>>();
			for (String productId : listProductIds) {
				BigDecimal quantityTotal = BigDecimal.ZERO;
				Map<String, Object> obj = FastMap.newInstance();
				for (Map<String, Object> map1 : listProducts) {
					if (map1.containsKey("productId") && productId.contentEquals((String)map1.get("productId"))) {
						obj.putAll(map1);
						quantityTotal = quantityTotal.add(new BigDecimal((String)map1.get("quantity")));
					}
				}
				obj.put("productId", productId);
				obj.put("quantity", quantityTotal.toString());
				listProductTotals.add(obj);
			}
			
			map.put("listProducts", listProductTotals);
			
			map.put("statusId", "REQ_CREATED");
			map.put("originFacilityId", requirement.get("facilityId"));
			map.put("requiredByDate", requirement.getTimestamp("requiredByDate").getTime());
			map.put("requirementStartDate", requirement.getTimestamp("requirementStartDate").getTime());
			map.put("requirementTypeId", "RECEIVE_REQUIREMENT");
			map.put("reasonEnumId", "RECEIVE_DISAGGR");
			map.put("userLogin", userLogin);
			Map<String, Object> returnMap = dispatcher.runSync("createNewRequirement", map);
			String requirementIdNew = (String)returnMap.get("requirementId");
			
			// auto approve requirement
			Map<String, Object> map2 = FastMap.newInstance();
			map2.put("statusId", "REQ_APPROVED");
			map2.put("requirementId", requirementIdNew);
			map2.put("userLogin", userLogin);
			try {
				dispatcher.runSync("changeRequirementStatus", map2);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: " + "changeRequirementStatus" + " error");
			}
			
			// Create association
			for (GenericValue item : listReqItems) {
				String prOrgId = item.getString("productId");
				String reqItemSeqId = item.getString("reqItemSeqId");
				for (Map<String, Object> assoc : listProductAssocs) {
					if (assoc.containsKey(prOrgId)){
						@SuppressWarnings("unchecked")
						List<GenericValue> listChilds = (List<GenericValue>)assoc.get(prOrgId);
						for (GenericValue prChild : listChilds) {
							List<GenericValue> listTmps = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("productId", prChild.getString("productId"), "requirementId", requirementIdNew)), null, null, null, false);
							for (GenericValue itemTmp : listTmps) {
								Map<String, Object> itemAssoc = FastMap.newInstance();
								itemAssoc.put("requirementId", requirementId);
								itemAssoc.put("reqItemSeqId", reqItemSeqId);
								itemAssoc.put("toRequirementId", requirementIdNew);
								itemAssoc.put("toReqItemSeqId", itemTmp.getString("reqItemSeqId"));
								itemAssoc.put("reqItemAssocTypeId", "DISAGGREGATED");
								itemAssoc.put("quantity", prChild.getBigDecimal("quantity"));
								itemAssoc.put("userLogin", userLogin);
								try {
									dispatcher.runSync("createRequirementItemAssoc", itemAssoc);
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError("OLBIUS: " + "createRequirementItemAssoc" + " error");
								}
							}
							
						}
					}
				}
			}
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: " + "createNewRequirement" + " error");
		}
		
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementId", requirementId);
		return mapReturn;
	}
	
	public static Map<String, Object> createRequirementExportInventoryToAggregate(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		EntityCondition cond1 = EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId);
		EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "REQ_CANCELLED");
		List<EntityCondition> listConds = UtilMisc.toList(cond1, cond2);
		List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(listConds), null, null, null, false);
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> map = FastMap.newInstance();
		GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		try {
			// auto create new requirement
			List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> listProductAssocs = new ArrayList<Map<String, Object>>();
			List<String> listProductIds = new ArrayList<String>();
			for (GenericValue item : listReqItems) {
				String productId = item.getString("productId");
				GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				String requireAmount = objProduct.getString("requireAmount");
				
				BigDecimal quantity = item.getBigDecimal("quantity");
				BigDecimal weight = item.getBigDecimal("weight");
				List<GenericValue> listProductChilds = LogisticsProductUtil.getListProdConfigItemProduct(delegator, productId);
				
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					BigDecimal amountUnit = LogisticsProductUtil.getProductConfigAmount(delegator, productId);
					quantity = weight.divide(amountUnit, RoundingMode.HALF_UP);
				}
				
				for (GenericValue pr : listProductChilds) {
					Map<String, Object> mapReqItem = FastMap.newInstance();
					String productIdChild = pr.getString("productId");
					mapReqItem.put("productId", productIdChild);
					if (!listProductIds.contains(productIdChild)) listProductIds.add(productIdChild);
					String requireAmountChild = pr.getString("requireAmount");
					if (UtilValidate.isNotEmpty(requireAmountChild) && "Y".equals(requireAmountChild)) {
						if (UtilValidate.isNotEmpty(pr.getBigDecimal("quantity"))) {
							if (UtilValidate.isNotEmpty(pr.getBigDecimal("amount"))) {
								mapReqItem.put("quantity", quantity.multiply(pr.getBigDecimal("amount").multiply(pr.getBigDecimal("quantity"))).toString());
							} else {
								mapReqItem.put("quantity", quantity.multiply(pr.getBigDecimal("quantity")).toString());
							}
						}
						mapReqItem.put("uomId", pr.getString("weightUomId"));
					} else {
						if (UtilValidate.isNotEmpty(pr.getBigDecimal("quantity"))) {
							mapReqItem.put("quantity", quantity.multiply(pr.getBigDecimal("quantity")).toString());
						}
						mapReqItem.put("uomId", pr.getString("quantityUomId"));
					}
					List<GenericValue> listAverageCost = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(UtilMisc.toMap("productId", pr.getString("productId"),  "facilityId", requirement.getString("facilityId"), "organizationPartyId", company, "productAverageCostTypeId", "SIMPLE_AVG_COST")), null, null, null, false);
					listAverageCost = EntityUtil.filterByDate(listAverageCost);
					if (!listAverageCost.isEmpty()){
						mapReqItem.put("unitCost", listAverageCost.get(0).getBigDecimal("averageCost").toString());
					} else {
						mapReqItem.put("unitCost", BigDecimal.ZERO.toString());
					}
					listProducts.add(mapReqItem);
				}
				Map<String, Object> mapAssocs = FastMap.newInstance();
				mapAssocs.put(productId, listProductChilds);
				listProductAssocs.add(mapAssocs);
			}
			
			List<Map<String, Object>> listProductTotals = new ArrayList<Map<String, Object>>();
			for (String productId : listProductIds) {
				BigDecimal quantityTotal = BigDecimal.ZERO;
				Map<String, Object> obj = FastMap.newInstance();
				for (Map<String, Object> map1 : listProducts) {
					if (map1.containsKey("productId") && productId.contentEquals((String)map1.get("productId"))) {
						obj.putAll(map1);
						quantityTotal = quantityTotal.add(new BigDecimal((String)map1.get("quantity")));
					}
				}
				obj.put("productId", productId);
				obj.put("quantity", quantityTotal.toString());
				listProductTotals.add(obj);
			}
			
			map.put("listProducts", listProductTotals);
			map.put("statusId", "REQ_CREATED");
			map.put("originFacilityId", requirement.get("facilityId"));
			map.put("requiredByDate", requirement.getTimestamp("requiredByDate").getTime());
			map.put("requirementStartDate", requirement.getTimestamp("requirementStartDate").getTime());
			map.put("requirementTypeId", "EXPORT_REQUIREMENT");
			map.put("reasonEnumId", "EXPORT_AGGREGATED");
			map.put("userLogin", userLogin);
			Map<String, Object> returnMap = dispatcher.runSync("createNewRequirement", map);
			String requirementIdNew = (String)returnMap.get("requirementId");
			
			// Create association
			for (GenericValue item : listReqItems) {
				String prOrgId = item.getString("productId");
				String reqItemSeqId = item.getString("reqItemSeqId");
				for (Map<String, Object> assoc : listProductAssocs) {
					if (assoc.containsKey(prOrgId)){
						@SuppressWarnings("unchecked")
						List<GenericValue> listChilds = (List<GenericValue>)assoc.get(prOrgId);
						for (GenericValue prChild : listChilds) {
							List<GenericValue> listTmps = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("productId", prChild.getString("productId"), "requirementId", requirementIdNew)), null, null, null, false);
							for (GenericValue itemTmp : listTmps) {
								Map<String, Object> itemAssoc = FastMap.newInstance();
								itemAssoc.put("requirementId", requirementIdNew);
								itemAssoc.put("reqItemSeqId", itemTmp.getString("reqItemSeqId"));
								itemAssoc.put("toRequirementId", requirementId);
								itemAssoc.put("toReqItemSeqId", reqItemSeqId);
								itemAssoc.put("reqItemAssocTypeId", "AGGREGATED");
								itemAssoc.put("quantity", prChild.getBigDecimal("quantity"));
								itemAssoc.put("userLogin", userLogin);
								try {
									dispatcher.runSync("createRequirementItemAssoc", itemAssoc);
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError("OLBIUS: " + "createRequirementItemAssoc" + " error");
								}
							}
							
						}
					}
				}
			}
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: " + "createNewRequirement" + " error");
		}
		
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementId", requirementId);
		return mapReturn;
	}
	
	public static Map<String, Object> getRequirementAssocFroms(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		List<GenericValue> listItemAssocs = delegator.findList("RequirementItemAssoc", EntityCondition.makeCondition("toRequirementId", requirementId), null, null, null, false);
		List<String> requirementIdFroms = EntityUtil.getFieldListFromEntityList(listItemAssocs, "requirementId", true);
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementIdFroms", requirementIdFroms);
		return mapReturn;
	}
	
	public static Map<String, Object> getRequirementAssocTos(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		List<GenericValue> listItemAssocs = delegator.findList("RequirementItemAssoc", EntityCondition.makeCondition("requirementId", requirementId), null, null, null, false);
		List<String> requirementIdTos = EntityUtil.getFieldListFromEntityList(listItemAssocs, "toRequirementId", true);
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementIdTos", requirementIdTos);
		return mapReturn;
	}
	
	public static Map<String, Object> changeRequirementAssocsStatus(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String statusId = (String)context.get("statusId");
				
		List<GenericValue> listItemAssocFroms = delegator.findList("RequirementItemAssoc", EntityCondition.makeCondition("toRequirementId", requirementId), null, null, null, false);
		List<String> requirementIdFroms = EntityUtil.getFieldListFromEntityList(listItemAssocFroms, "requirementId", true);
		List<GenericValue> listItemAssocTos = delegator.findList("RequirementItemAssoc", EntityCondition.makeCondition("requirementId", requirementId), null, null, null, false);
		List<String> requirementIdTos = EntityUtil.getFieldListFromEntityList(listItemAssocTos, "toRequirementId", true);
		List<String> listAllAssocIds = new ArrayList<String>();
		listAllAssocIds.addAll(requirementIdFroms);
		listAllAssocIds.addAll(requirementIdTos);
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		for (String reqId : listAllAssocIds) {
			try {
				Map<String, Object> map = FastMap.newInstance();
				map.put("requirementId", reqId);
				map.put("statusId", statusId);
				map.put("userLogin", userLogin);
				map.put("createNotification", "N");
				map.put("changeAssocsStatus", "N");
				dispatcher.runSync("changeRequirementStatus", map);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: " + "changeRequirementStatus" + " error");
			}
		}
		return ServiceUtil.returnSuccess();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> exportProductFromRequirementNormal(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String requirementId = (String)context.get("requirementId");
    	String facilityId = null;
    	Locale locale = (Locale)context.get("locale");
    	if (UtilValidate.isNotEmpty(context.get("facilityId"))) {
    		facilityId = (String)context.get("facilityId"); 
		}
    	String contactMechId = null;
    	if (UtilValidate.isNotEmpty(context.get("contactMechId"))) {
    		contactMechId = (String)context.get("contactMechId");
    	}
    	
		String listItems = null;
		if (UtilValidate.isNotEmpty(context.get("listRequirementItems"))) {
			listItems = (String)context.get("listRequirementItems");
		}
		
		String listPrAttrs = null;
		if (UtilValidate.isNotEmpty(context.get("listProductAttributes"))) {
			listPrAttrs = (String)context.get("listProductAttributes");
		}
		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		if (UtilValidate.isEmpty(facilityId)) {
			if (UtilValidate.isNotEmpty(requirement.get("facilityId"))) {
				facilityId = requirement.getString("facilityId");
			}
		}
		if (UtilValidate.isEmpty(contactMechId)) {
			if (UtilValidate.isNotEmpty(requirement.get("contactMechId"))) {
				contactMechId = requirement.getString("contactMechId");
			}
		}
		
		List<Map<String, Object>> listProducts = FastList.newInstance();
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItems);
		} catch (ParseException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: receiveProductFromRequirement - JqxWidgetSevices.convert error!");
		}
		List<Map<String, Object>> listProductAttrs = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listPrAttrs)) {
			try {
				listProductAttrs = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listPrAttrs);
			} catch (ParseException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: receiveProductFromRequirement - JqxWidgetSevices.convert error!");
			}
		}
		if (!listProducts.isEmpty() && ("REQ_APPROVED".equals(requirement.getString("statusId")) || ("REQ_RECEIVED".equals(requirement.getString("statusId")) && "CHANGEDATE_REQUIREMENT".equals(requirement.getString("requirementTypeId"))))&& UtilValidate.isNotEmpty(contactMechId) && UtilValidate.isNotEmpty(facilityId)){
			// store 
			Map<String, Object> mapAttributes = FastMap.newInstance();
			for (Map<String, Object> reqItem : listProducts){
				String reqItemSeqId = null;
				if (reqItem.containsKey("reqItemSeqId")){
					reqItemSeqId = (String)reqItem.get("reqItemSeqId");
				}
				String productId = null;
				if (reqItem.containsKey("productId")){
					productId = (String)reqItem.get("productId");
				}
				BigDecimal exportQuantity = BigDecimal.ZERO;
				String quantityStr = null;
				if (reqItem.containsKey("quantity")){
					quantityStr = (String)reqItem.get("quantity");
					exportQuantity = new BigDecimal(quantityStr);
				}
				if (!listProductAttrs.isEmpty()) {
					List<Map<String, Object>> listAttributes = FastList.newInstance();
					for (Map<String, Object> map : listProductAttrs) {
						if (map.containsKey("productId")){
							String prId = (String)map.get("productId");
							if (UtilValidate.isNotEmpty(prId) && productId.equals(prId)) {
								listAttributes.add(map);
							}
						}
					}
					mapAttributes.put(productId, listAttributes);
				}
				if (UtilValidate.isNotEmpty(productId)) {
					if (UtilValidate.isNotEmpty(reqItemSeqId)) {
						GenericValue reqItemDB = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
						BigDecimal unitCost = reqItemDB.getBigDecimal("unitCost");
						if ("REQ_RECEIVED".equals(requirement.getString("statusId")) && "CHANGEDATE_REQUIREMENT".equals(requirement.getString("requirementTypeId"))){
							reqItemDB.put("actualExecutedQuantity", exportQuantity);
							reqItemDB.put("actualExportedQuantity", exportQuantity);
						} else {
							GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
							String requireAmount = product.getString("requireAmount");
							String baseWeightUomId = product.getString("weightUomId");
							String weightUomId = reqItemDB.getString("weightUomId");
							String quantityUomId = reqItemDB.getString("quantityUomId");
							String baseQuantityUomId = product.getString("quantityUomId");
							
							if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
								if (UtilValidate.isNotEmpty(baseWeightUomId) && UtilValidate.isNotEmpty(weightUomId)) {
									exportQuantity = exportQuantity.multiply(LogisticsProductUtil.getConvertWeightNumber(delegator, productId, weightUomId, baseWeightUomId));
								}
								if (UtilValidate.isNotEmpty(reqItemDB.getBigDecimal("actualExecutedQuantity"))){
									reqItemDB.put("actualExecutedWeight", reqItemDB.getBigDecimal("actualExecutedWeight").add(exportQuantity));
								} else {
									reqItemDB.put("actualExecutedWeight", exportQuantity);
								}
								if (UtilValidate.isNotEmpty(reqItemDB.getBigDecimal("actualExecutedQuantity"))){
									reqItemDB.put("actualExecutedQuantity", reqItemDB.getBigDecimal("actualExecutedQuantity").add(BigDecimal.ONE));
								} else {
									reqItemDB.put("actualExecutedQuantity", BigDecimal.ONE);
								}
							} else {
								BigDecimal convert = ProductUtil.getConvertPackingNumber(delegator, productId, quantityUomId, baseQuantityUomId);
								exportQuantity = exportQuantity.multiply(convert);
								if (UtilValidate.isNotEmpty(reqItemDB.getBigDecimal("actualExecutedQuantity"))){
									reqItemDB.put("actualExecutedQuantity", reqItemDB.getBigDecimal("actualExecutedQuantity").add(exportQuantity));
								} else {
									reqItemDB.put("actualExecutedQuantity", exportQuantity);
								}
							}
							reqItemDB.put("actualExportedQuantity", exportQuantity);
						}
						
						reqItemDB.put("unitCost", unitCost);
						delegator.store(reqItemDB);
					}
				}
			}
			
			// create shipment 
			String reasonEnumId = requirement.getString("reasonEnumId");
			String shipmentTypeId = null;
			List<GenericValue> listShipmentAndEnums = delegator.findList("ShipmentTypeEnumDetail", EntityCondition.makeCondition(UtilMisc.toMap("enumId", reasonEnumId, "parentTypeId", "OUTGOING_SHIPMENT")), null, null, null, false);
			listShipmentAndEnums = EntityUtil.filterByDate(listShipmentAndEnums);
			if (!listShipmentAndEnums.isEmpty()){
				shipmentTypeId = listShipmentAndEnums.get(0).getString("shipmentTypeId");
			} else {
				return ServiceUtil.returnError("OLBIUS: cannot get shipment type will be created with this reason enum " + reasonEnumId);
			}
			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			Map<String, Object> mapShipment = UtilMisc.toMap("requirementId", requirementId, "shipmentTypeId", shipmentTypeId, "userLogin", system);
			String shipmentId = null;
			try {
				Map<String, Object> mapResultShipment = dispatcher.runSync("createShipmentFromRequirement", mapShipment);
				shipmentId = (String)mapResultShipment.get("shipmentId");
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: createShipmentFromRequirement error " + e.toString());
			}
			
			List<GenericValue> listShipmentItems = FastList.newInstance();
			try {
				EntityCondition cond1 = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
				EntityCondition cond2 = EntityCondition.makeCondition("quantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(cond1);
				conds.add(cond2);
				listShipmentItems = delegator.findList("ShipmentItem", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findList ShipmentItem error!");
			}
			if (listShipmentItems.isEmpty()) return ServiceUtil.returnError("OLBIUS: ShipmentItem not found!");
			
			for (GenericValue smtItem : listShipmentItems) {
				String shipmentItemSeqId = smtItem.getString("shipmentItemSeqId");
				
				List<GenericValue> listReqShipment = FastList.newInstance();
				try {
					EntityCondition condsm = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
					EntityCondition condsmit = EntityCondition.makeCondition("shipmentItemSeqId", EntityOperator.EQUALS, shipmentItemSeqId);
					List<EntityCondition> condsmt = FastList.newInstance();
					condsmt.add(condsm);
					condsmt.add(condsmit);
					listReqShipment = delegator.findList("RequirementShipment", EntityCondition.makeCondition(condsmt), null, null, null, false);
				} catch (GenericEntityException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS: findList RequirementShipment error!");
				}
				String reqItemSeqId = null;
				if (!listReqShipment.isEmpty()) reqItemSeqId = listReqShipment.get(0).getString("reqItemSeqId");
				if (UtilValidate.isEmpty(reqItemSeqId)) {
					return ServiceUtil.returnError("OLBIUS: RequirementShipment not found! requirementId= " + requirementId + "; shipmentId =" + shipmentId);
				} 
				String productId = smtItem.getString("productId");
				List<Map<String, Object>> listAttribues = FastList.newInstance();
				if (mapAttributes.containsKey(productId)){
					listAttribues = (List<Map<String, Object>>)mapAttributes.get(productId);
				}
				// create issues
				Map<String, Object> mapIssuance = FastMap.newInstance();
				mapIssuance.put("shipmentItemSeqId", shipmentItemSeqId);
				mapIssuance.put("shipmentId", shipmentId);
				BigDecimal quantity = smtItem.getBigDecimal("quantity");
				if (ProductUtil.isWeightProduct(delegator, productId)) {
					quantity = smtItem.getBigDecimal("weight");
				}
				
				List<Map<String, Object>> listInvs = FastList.newInstance();
				if (!listAttribues.isEmpty()){
					for (Map<String, Object> map : listAttribues) {
						Map<String, Object> attributes = FastMap.newInstance();
						attributes.put("productId", productId);
						attributes.put("facilityId", facilityId);
						attributes.put("ownerPartyId", company);
						
						String qtyStr = null;
						if (map.containsKey("quantity") && map.containsKey("productId")){
							qtyStr = (String)map.get("quantity");
							if (UtilValidate.isNotEmpty(qtyStr)) {
								BigDecimal quantityP = new BigDecimal(qtyStr);
								if (quantityP.compareTo(BigDecimal.ZERO) > 0){
									for (String key : map.keySet()) {
										if ("expireDate".equals(key)){
											if (UtilValidate.isNotEmpty(map.get("expireDate"))) {
												String expStr = (String)map.get("expireDate");
												if (UtilValidate.isNotEmpty(expStr)) {
													Long expL = new Long (expStr);
													if (UtilValidate.isNotEmpty(expL)) {
														Timestamp exp = new Timestamp(expL);
														if (UtilValidate.isNotEmpty(exp)) {
															attributes.put(key, exp);
														}
													}
												}
											}
										} else if ("datetimeManufactured".equals(key)){
											if (UtilValidate.isNotEmpty(map.get("datetimeManufactured"))) {
												String expStr = (String)map.get("datetimeManufactured");
												if (UtilValidate.isNotEmpty(expStr)) {
													Long expL = new Long (expStr);
													if (UtilValidate.isNotEmpty(expL)) {
														Timestamp exp = new Timestamp(expL);
														if (UtilValidate.isNotEmpty(exp)) {
															attributes.put(key, exp);
														}
													}
												}
											}
										} else {
											attributes.put(key, map.get(key));
										}
									}
									List<Map<String, Object>> listInvTmps = InventoryUtil.getInventoryItemsForQuantity(delegator, attributes, quantityP);
									if (!listInvTmps.isEmpty()){
										listInvs.addAll(listInvTmps);
									}
								}
							}
						}
					}
				} else {
					Map<String, Object> attributes = FastMap.newInstance();
					attributes.put("productId", productId);
					attributes.put("facilityId", facilityId);
					attributes.put("ownerPartyId", company);
					List<String> orderBy = FastList.newInstance();
					orderBy.add("expireDate");
					listInvs = InventoryUtil.getInventoryItemsForQuantity(delegator, attributes, quantity, orderBy);
				}
				
				if (listInvs.isEmpty()) return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLInventoryNotEnough", locale) + ": " + productId + " - QOH = 0 < " + quantity.toString());
				BigDecimal qohTotal = BigDecimal.ZERO;
				for (Map<String, Object> map : listInvs) {
					BigDecimal qoh = (BigDecimal)map.get("quantity");
					qohTotal = qohTotal.add(qoh);
				}
				if (qohTotal.compareTo(quantity) < 0) return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLInventoryNotEnough", locale) + ": " + productId + " - QOH = 0 < " + quantity.toString());
				
				for (Map<String, Object> map : listInvs) {
					BigDecimal qoh = (BigDecimal)map.get("quantity");
					String inventoryItemId = (String)map.get("inventoryItemId");
					
					mapIssuance.put("inventoryItemId", inventoryItemId);
					if (ProductUtil.isWeightProduct(delegator, productId)) {
						mapIssuance.put("quantity", BigDecimal.ONE);
						mapIssuance.put("weight", qoh);
					} else {
						mapIssuance.put("quantity", qoh);
					}
					mapIssuance.put("requirementId", requirementId);
					mapIssuance.put("reqItemSeqId", reqItemSeqId);
					mapIssuance.put("issuedByUserLoginId", userLogin.getString("userLoginId"));
					mapIssuance.put("userLogin", userLogin);
					mapIssuance.put("affectAccounting", true);
					try {
						dispatcher.runSync("createItemIssuance", mapIssuance);
					} catch (GenericServiceException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS - Create item issuance error!");
					}
				}
			}
			
			// update shipment to packed
			mapShipment = FastMap.newInstance();
			mapShipment.put("userLogin", userLogin);
			mapShipment.put("shipmentId", shipmentId);
			mapShipment.put("statusId", "SHIPMENT_PACKED");
			try {
				dispatcher.runSync("updateShipment", mapShipment);
			} catch (GenericServiceException e){
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: updateShipment error! shipmentId=" + shipmentId);
			}
			// update shipment to shipped
			mapShipment = FastMap.newInstance();
			mapShipment.put("userLogin", userLogin);
			mapShipment.put("shipmentId", shipmentId);
			mapShipment.put("statusId", "SHIPMENT_SHIPPED");
			try {
				dispatcher.runSync("updateShipment", mapShipment);
			} catch (GenericServiceException e){
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: updateShipment error! shipmentId=" + shipmentId);
			}
			
			for (GenericValue item : listShipmentItems){
				List<GenericValue> listItemIssuance = delegator.findList("ItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", item.getString("shipmentItemSeqId"))), null, null, null, false);
				for (GenericValue issue : listItemIssuance){
					Map<String, Object> mapDetail = FastMap.newInstance();
					List<GenericValue> list = delegator.findList("RequirementShipment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "requirementId", requirementId, "shipmentItemSeqId", item.getString("shipmentItemSeqId"))), null, null, null, false);
					if (!list.isEmpty()){
						String reqItemSeqId = list.get(0).getString("reqItemSeqId");
						mapDetail.put("reqItemSeqId", reqItemSeqId);
						mapDetail.put("requirementId", requirementId);
					}
					String productId = item.getString("productId");
					if (ProductUtil.isWeightProduct(delegator, productId)) {
						mapDetail.put("amountOnHandDiff", issue.getBigDecimal("weight").negate());
						mapDetail.put("quantityOnHandDiff", new BigDecimal(-1));
						mapDetail.put("availableToPromiseDiff", new BigDecimal(-1));
					} else {
						mapDetail.put("quantityOnHandDiff", new BigDecimal(issue.getString("quantity")).negate());
						mapDetail.put("availableToPromiseDiff", new BigDecimal(issue.getString("quantity")).negate());
					}
					mapDetail.put("userLogin", userLogin);
					mapDetail.put("shipmentId", shipmentId);
					mapDetail.put("shipmentItemSeqId", item.getString("shipmentItemSeqId"));
					mapDetail.put("inventoryItemId", issue.getString("inventoryItemId"));
					mapDetail.put("itemIssuanceId", issue.getString("itemIssuanceId"));
					mapDetail.put("reasonEnumId", requirement.getString("reasonEnumId"));
					
					try {
						dispatcher.runSync("createInventoryItemDetail", mapDetail);
					} catch (GenericServiceException e){
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS: createInventoryItemDetail error !");
					}
				}
			}
			
			if (!"EXPORT_DISAGGREGATED".equals(reasonEnumId)
                    && !"EXPORT_AGGREGATED".equals(reasonEnumId)
                    && !"EXPORT_EXCHANGED".equals(reasonEnumId)
                    && !"CHANGEDATE_PRODOUTOFDATE".equals(reasonEnumId)
//                    && !"EXPORT_CANCEL".equals(reasonEnumId)
                    ){
				try {
		    		dispatcher.runSync("createInvoiceFromShipmentRequirement", UtilMisc.toMap("requirementId", requirementId, "shipmentId", shipmentId, "userLogin", userLogin));
				} catch (GenericServiceException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS - createInvoiceFromShipmentRequirement error! requirementId= " + requirementId);
				}
			}
			
			boolean check = checkRequirementToComplete(delegator, requirementId);
			if (check){
				Map<String, Object> mapUpdateReq = UtilMisc.toMap("requirementId", requirementId, "statusId", "REQ_COMPLETED", "facilityId", facilityId, "contactMechId", contactMechId, "userLogin", (GenericValue)context.get("userLogin"));
				try {
					dispatcher.runSync("changeRequirementStatus", mapUpdateReq);
				} catch (GenericServiceException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS: changeRequirementStatus error requirementId= " + requirementId);
				}
			} else {
				Map<String, Object> mapUpdateReq = UtilMisc.toMap("requirementId", requirementId, "statusId", "REQ_EXPORTED", "facilityId", facilityId, "contactMechId", contactMechId, "userLogin", (GenericValue)context.get("userLogin"));
				try {
					dispatcher.runSync("changeRequirementStatus", mapUpdateReq);
				} catch (GenericServiceException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS: changeRequirementStatus error requirementId= " + requirementId);
				}
			}
		} else {
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLRequirementHasBeenExported", locale));
		}
		
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("requirementId", requirementId);
		return mapReturn;
	}
	
	//edit by thanhdt
	@SuppressWarnings("unused")
	public static Map<String, Object> updateRequirementAndItem(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher =ctx.getDispatcher(); 
		Map<String, Object> requirement = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String requirementId = (String) context.get("requirementId");
		String reqDescription = (String) context.get("description");
		Long requirementStartDate = Long.parseLong((String) context.get("requirementStartDate"));
		JSONArray listProducts = JSONArray.fromObject(context.get("listProducts"));
		
		List<Map<String, Object>> productArr = FastList.newInstance();
		for(int i = 0; i< listProducts.size(); i++){
			JSONObject objectJSON = JSONObject.fromObject(listProducts.get(i));
			Map<String, Object> product = new HashMap<String, Object>();
			product.put("userLogin", userLogin);
			product.put("reqItemSeqId", objectJSON.get("sequenceId"));
			product.put("productId", objectJSON.get("productId"));
			product.put("quantity", BigDecimal.valueOf(Double.parseDouble(objectJSON.get("quantity").toString())));
			product.put("unitCost", BigDecimal.valueOf(Double.parseDouble(objectJSON.get("unitCost").toString())));
			product.put("quantityUomId", objectJSON.get("uomId"));
			product.put("description", objectJSON.get("description"));
			product.put("requirementId", requirementId);
			if(UtilValidate.isNotEmpty(objectJSON.get("statusId"))) {
				product.put("statusId", objectJSON.get("statusId"));
			}
			productArr.add(product);
		}
		
		//Calculate grandTotal of requirement
		Double grandTotal = 0.0;
		for(Map<String, Object> x : productArr) {
			if(!String.valueOf(x.get("statusId")).equals("REQ_CANCELLED") && !String.valueOf(x.get("statusId")).equals("REQ_REJECTED") ) {
				grandTotal += ( Double.valueOf(String.valueOf(x.get("quantity"))) * Double.valueOf(String.valueOf(x.get("unitCost"))) );
			}
		}
		
		requirement.put("userLogin", userLogin);
		requirement.put("requirementId", requirementId);	
		if(UtilValidate.isNotEmpty(context.get("statusId"))) {
			requirement.put("statusId",context.get("statusId"));
		}
		
		requirement.put("reason", reqDescription);
		requirement.put("grandTotal", BigDecimal.valueOf(grandTotal));
		requirement.put("requirementStartDate", new Timestamp(requirementStartDate));
		try {
			dispatcher.runSync("updateRequirement", requirement);
		} catch (GenericServiceException e1) {
			String errMsg = "OLBIUS: Fatal error when run Service updateRequirement: " + e1.toString();
			Debug.logError(e1, errMsg, module);
			e1.printStackTrace();
			return ServiceUtil.returnError(errMsg);
			
		}
		
		for(Map	<String, Object> item : productArr){
			try {
				dispatcher.runSync("updateRequirementItem", item);
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when run Service updateRequirementItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				e.printStackTrace();
				return ServiceUtil.returnError(errMsg);
			}
		}
		
		result.put("requirementId", requirementId);
		return result;
	}
	
	@SuppressWarnings("unused")
	public static Map<String, Object> addRequirementItem(DispatchContext ctx, Map<String, Object> context){
		
		Map<String, Object> result = new HashMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String requirementId = (String)context.get("requirementId");
		GenericValue item = delegator.makeValue("RequirementItem");
		String statusId = null;
		if (UtilValidate.isNotEmpty(context.get("statusId"))) {
			statusId = (String) context.get("statusId");
		}
		item.put("requirementId", requirementId);
		delegator.setNextSubSeqId(item, "reqItemSeqId", 5, 1);
		
		item.put("quantity", context.get("quantity"));
		item.put("unitCost", context.get("unitCost"));
		item.put("quantityUomId",context.get("quantityUomId"));
		
		if (UtilValidate.isNotEmpty(statusId)) {
			item.put("statusId", statusId);
		} else {
			item.put("statusId", "REQ_CREATED");
		}
		item.put("productId", context.get("productId"));
		item.put("description", context.get("description"));
		try {
			item.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
				String errMsg = "Fatal error calling addRequirementItem service: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError("OLBIUS: addRequirementItem error! " + e.toString());
		}
		
		result.put("requirementId", requirementId);
		return result;
	}
	
	public static boolean checkRequirementToComplete(Delegator delegator, String requirementId){
		GenericValue objRequirement = null;
		try {
			objRequirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne Requirement: " + e.toString();
			Debug.logError(e, errMsg, module);
			return false;
		}
		String reasonEnumId = objRequirement.getString("reasonEnumId");
		List<GenericValue> listShipmentTypeEnum = FastList.newInstance();
		List<EntityCondition> conds = FastList.newInstance();
		conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("enumId", reasonEnumId));
		conds.add(EntityUtil.getFilterByDateExpr());
		boolean check = false;
		try {
			listShipmentTypeEnum = delegator.findList("ShipmentTypeEnum", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList ShipmentTypeEnum: " + e.toString();
			Debug.logError(e, errMsg, module);
			return false;
		}
		if (!listShipmentTypeEnum.isEmpty()){
			if (listShipmentTypeEnum.size() == 1) return true;
			int size = listShipmentTypeEnum.size();
			List<GenericValue> listRequirementShipment = FastList.newInstance();
			conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("requirementId", requirementId));
			try {
				listRequirementShipment = delegator.findList("RequirementShipment", EntityCondition.makeCondition(conds), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList RequirementShipment: " + e.toString();
				Debug.logError(e, errMsg, module);
				return false;
			}
			if (!listRequirementShipment.isEmpty()){
				List<String> shipmentIds = EntityUtil.getFieldListFromEntityList(listRequirementShipment, "shipmentId", true);
				if (!shipmentIds.isEmpty()){
					if (size <= shipmentIds.size()) return true;
				}
			}
			return false;
		}
		return check;
	}
}