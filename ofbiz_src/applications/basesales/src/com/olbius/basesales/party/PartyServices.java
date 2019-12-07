package com.olbius.basesales.party;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
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
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.contact.ContactMechWorker;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.basesales.util.MTLUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;

public class PartyServices {
	public static final String module = PartyServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    public static String RESOURCE_PROPERTIES = "basesales.properties";
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListEmployeeByOrg(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	// Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			Set<String> selectFields = FastSet.newInstance();
			selectFields.add("partyId");
			selectFields.add("partyCode");
			selectFields.add("fullName");
			opts.setDistinct(true);
			listIterator = delegator.find("PartyEmployeeDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, selectFields, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListEmployeeByOrg service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetCustomersByProductStore(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		//LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		boolean isFind = false;
    		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
            String productStoreId = SalesUtil.getParameter(parameters, "productStoreId");
			if (securityOlb.olbiusHasPermission(userLogin, "VIEW_CUSTOMER_ALL", "ENTITY", "SALESORDER")) {
				isFind = true;
			} else if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, false)){
				isFind = true;
				List<String> distOfSupervisors = PartyWorker.distributorOfSupervisorEnable(delegator, userLogin);
				//Map<String, Object> distOfSupervisors = dispatcher.runSync("distributorOfSupervisorEnable", UtilMisc.toMap("supervisorId", userLogin.get("partyId")));
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, distOfSupervisors));
			} else if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false)) {
                isFind = true;
                GenericValue gv = delegator.findOne("PartyDistributor",UtilMisc.toMap("partyId",productStoreId),false);
                if (UtilValidate.isNotEmpty(gv)) {
                    List<String> custOfSalesmans = PartyWorker.getCustOfSalesman(delegator, userLogin);
                    listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, custOfSalesmans));
                }
            }
    		if (isFind && UtilValidate.isNotEmpty(productStoreId)) {
    			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
    			if (productStore != null) {
    				Set<String> listSelectFields = null;
    				if ("Y".equals(productStore.getString("includeOtherCustomer"))){
    					listSelectFields = new HashSet<String>();
    					listSelectFields.add("partyId");
    					listSelectFields.add("partyCode");
    					listSelectFields.add("partyName");
    					listSelectFields.add("telecomId");
    					listSelectFields.add("telecomName");
    					listSelectFields.add("postalAddressName");
    					opts.setDistinct(true);
    				} else {
    					listAllConditions.add(EntityCondition.makeCondition("productStoreId", productStoreId));
    				}
    				listAllConditions.add(EntityCondition.makeCondition("payToPartyId", productStore.get("payToPartyId")));
    				listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", productStore.get("salesMethodChannelEnumId")));
    				listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
    				listAllConditions.add(EntityUtil.getFilterByDateExpr());
    				listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "ProductStoreRoleAndPartyContactTemp", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSelectFields, listSortFields, opts);
    			}
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetCustomersByProductStore service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetCustomersBySeller(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Security security = ctx.getSecurity();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	EntityListIterator listIterator = null;
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	// Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		boolean isFind = false;
    		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
    		if (securityOlb.olbiusHasPermission(userLogin, "VIEW_CUSTOMER_ALL", "ENTITY", "SALESORDER")) {
    			isFind = true;
    		} else if (securityOlb.olbiusHasPermission(userLogin, "VIEW_CUSTOMER_ROLE", "ENTITY", "SALESORDER")) {
    			isFind = true;
    			List<String> customerIds = PartyWorker.getCustomerIdsBySalesExecutive(delegator, userLogin.getString("partyId"));
    			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, customerIds));
    		}
    		List<String> productStoreIdsSell = EntityUtil.getFieldListFromEntityList(com.olbius.basesales.product.ProductStoreWorker.getListProductStoreSell(delegator, userLogin), "productStoreId", true);
    		if (isFind && UtilValidate.isNotEmpty(productStoreIdsSell)) {
    			Set<String> listSelectFields = UtilMisc.toSet("partyId", "partyCode", "fullName");
    			opts.setDistinct(true);
				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIdsSell));
				listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
				listAllConditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "ProductStoreRoleAndParty", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSelectFields, listSortFields, opts);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetCustomersByProductStore service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	public static Map<String, Object> jqGetCustomersOfSalesman(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		// Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			boolean isFind = false;
			OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
			if (securityOlb.olbiusHasPermission(userLogin, "VIEW_CUSTOMER_ALL", "ENTITY", "SALESORDER")) {
				isFind = true;
			} else if (securityOlb.olbiusHasPermission(userLogin, "VIEW_CUSTOMER_ROLE", "ENTITY", "SALESORDER")) {
				isFind = true;
				List<String> customerIds = PartyWorker.getCustomerIdsBySalesmanId(delegator, userLogin.getString("partyId"));
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, customerIds));
			}
			List<String> productStoreIdsSell = EntityUtil.getFieldListFromEntityList(com.olbius.basesales.product.ProductStoreWorker.getListProductStoreSell(delegator, userLogin), "productStoreId", true);
			if (isFind && UtilValidate.isNotEmpty(productStoreIdsSell)) {
				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIdsSell));
				listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				listIterator = delegator.find("ProductStoreRoleAndParty", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetCustomersByProductStore service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetAgreementsByCustomer(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
    			String customerId = parameters.get("partyId")[0];
    			if (UtilValidate.isNotEmpty(customerId)) {
    				String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    				listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", customerId));
    				listAllConditions.add(EntityCondition.makeCondition("partyIdTo", organizationId));
    				listAllConditions.add(EntityCondition.makeCondition("statusId", "AGREEMENT_APPROVED"));
    				listAllConditions.add(EntityUtil.getFilterByDateExpr());
    				listIterator = delegator.find("Agreement", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    			}
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetAgreementsByCustomer service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> jqGetPartiesReceiveByCustomer(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
    			String partyId = parameters.get("partyId")[0];
    			if (UtilValidate.isNotEmpty(partyId)) {
    				Set<String> listSelectFields = UtilMisc.toSet("partyId", "partyCode", "fullName");
    				opts.setDistinct(true);
    				
    				List<EntityCondition> conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
					conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "CUSTOMER"));
					conds.add(EntityCondition.makeCondition("roleTypeIdTo", "CHILD_MEMBER"));
					conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "OWNER"));
					conds.add(EntityUtil.getFilterByDateExpr());
					listIterator = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "PartyToAndPartyNameDetail", EntityCondition.makeCondition(conds), null, listSelectFields, listSortFields, opts);
					if (UtilValidate.isEmpty(listIterator)) {
						List<GenericValue> listMember = FastList.newInstance();
	    				GenericValue partyDetail = delegator.findOne("PartyFullNameDetail", UtilMisc.toMap("partyId", partyId), true);
	    				Set<String> listPartyIds = FastSet.newInstance();
	    				listPartyIds.add(partyId);
	    				if (UtilValidate.isNotEmpty(partyDetail)) {
	    					/* parameter is HO_GIA_DINH, get CAC_THANH_VIEN
	    					List<EntityCondition> listAllCondition = FastList.newInstance();
	    					listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", partyDetail.get("partyId")));
	    					listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", "CHILD"));
	    					List<String> listMemberIds = EntityUtil.getFieldListFromEntityList(
	    									delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false), 
	    									"partyIdTo", true);
	    					if (listMemberIds != null && listMemberIds.size() > 0) {
	    						List<GenericValue> listMemberTmp = delegator.findList("PartyFullNameDetail", EntityCondition.makeCondition("partyId", EntityOperator.IN, listMemberIds), null, null, null, false);
	    						if (listMemberTmp != null && listMemberTmp.size() > 0) listMember.addAll(listMemberTmp);
	    					}
	    					 */
							// parameter is THANH_VIEN_DAI_DIEN, get HO_GIA_DINH, THANH_VIEN_KHAC
	    					List<String> roleTypeIdsFrom = SalesUtil.getPropertyProcessedMultiKey(delegator, "role.representative");
	    					List<String> relationshipTypeIds = SalesUtil.getPropertyProcessedMultiKey(delegator, "party.rel.representative");
	    					List<EntityCondition> listAllCondition = FastList.newInstance();
	    					listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", partyDetail.get("partyId")));
	    					listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleTypeIdsFrom));
	    					listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, relationshipTypeIds));
	    					List<String> listOrgIds = EntityUtil.getFieldListFromEntityList(
	    									delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false), 
	    									"partyIdTo", true);
	    					if (UtilValidate.isNotEmpty(listOrgIds)) {
	    						listPartyIds.addAll(listOrgIds);
	    						
	    						List<String> relationshipTypeIdsMember = SalesUtil.getPropertyProcessedMultiKey(delegator, "party.rel.family.member");
	    						listAllCondition.clear();
	        					listAllCondition.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listOrgIds));
	        					listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, relationshipTypeIdsMember));
	        					List<String> listMemberIds = EntityUtil.getFieldListFromEntityList(
	    								delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false), 
	    								"partyIdFrom", true);
	        					if (listMemberIds != null && listMemberIds.size() > 0) {
	        						listPartyIds.addAll(listMemberIds);
	        					}
	    					} else {
	    						// Dai dien cua SCHOOL, BUSINESSES
	    						listAllCondition.clear();
	    						listAllCondition.add(EntityCondition.makeCondition("partyIdTo", partyDetail.get("partyId")));
	        					listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleTypeIdsFrom));
	        					listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, relationshipTypeIds));
	        					listOrgIds = EntityUtil.getFieldListFromEntityList(
	        									delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false), 
	        									"partyIdFrom", true);
	        					if (UtilValidate.isNotEmpty(listOrgIds)) {
	        						listPartyIds.addAll(listOrgIds);
	        					}
	    					}
	    				}
						List<EntityCondition> listConds = FastList.newInstance();
						listConds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
						listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", "CUSTOMER"));
						listConds.add(EntityCondition.makeCondition("roleTypeIdTo", "CONSIGNEE"));
						listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "CUSTOMER_REL"));
						listConds.add(EntityUtil.getFilterByDateExpr());
						List<String> listOrgIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship",
								EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, opts, false),"partyIdTo",true);

						if (UtilValidate.isNotEmpty(listOrgIds)) {
							listPartyIds.addAll(listOrgIds);
						}
						if (UtilValidate.isNotEmpty(listPartyIds)) {
							List<GenericValue> listMemberTmp = delegator.findList("PartyFullNameDetail",
									EntityCondition.makeCondition(
											EntityCondition.makeCondition("partyId", EntityOperator.IN, UtilMisc.toList(listPartyIds)),EntityOperator.AND,
											EntityCondition.makeCondition("statusId", "PARTY_ENABLED")),listSelectFields, null, null, false);
							if (listMemberTmp != null && listMemberTmp.size() > 0) listMember.addAll(listMemberTmp);
						}
	    				listIterator = listMember;
	    				successResult.put("TotalRows", String.valueOf(listMember.size()));
					}
    			}
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetPartiesReceiveByCustomer service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> jqGetPartiesSupplier(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = new ArrayList<GenericValue>();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		List<String> listPartyIds = com.olbius.basehr.util.SecurityUtil.getPartiesByRoles("SUPPLIER", delegator);
			if (UtilValidate.isNotEmpty(listPartyIds)) {
				List<GenericValue> listMemberTmp = delegator.findList("PartyFullNameDetail", EntityCondition.makeCondition("partyId", EntityOperator.IN, UtilMisc.toList(listPartyIds)), null, null, null, false);
				if (listMemberTmp != null && listMemberTmp.size() > 0) listIterator.addAll(listMemberTmp);
			}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetPartiesSupplier service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }

    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListParties(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		if (parameters.containsKey("isGroup") && parameters.get("isGroup").length > 0) {
    			String isGroup = parameters.get("isGroup")[0];
    			if ("Y".equals(isGroup)) {
    				List<String> partyGroupTypeIds = SalesPartyUtil.getDescendantPartyTypeIds("PARTY_GROUP", delegator);
    				listAllConditions.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.IN, partyGroupTypeIds));
    			}
    		}
    		listAllConditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
    		listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PartyFullNameDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListParties service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> jqGetFacilitiesByProductStore(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = new ArrayList<GenericValue>();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("productStoreId") && parameters.get("productStoreId").length > 0) {
    			String productStoreId = parameters.get("productStoreId")[0];
    			if (UtilValidate.isNotEmpty(productStoreId)) {
    				// set the default view cart on add for this store
    	            GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
    	            if (UtilValidate.isNotEmpty(productStore)) {
    	            	String billFromVendorPartyId = productStore.getString("payToPartyId");
    	            	if (UtilValidate.isNotEmpty(billFromVendorPartyId)) {
    	            		listIterator = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", billFromVendorPartyId), null, null, null, false);
    	            	}
    	            }
    			}
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetFacilitiesByProductStore service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    /*@SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> jqGetSalesExecutiveByCustomer(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
    			String partyId = parameters.get("partyId")[0];
    			if (UtilValidate.isNotEmpty(partyId)) {
					List<String> listMemberIds = SalesPartyUtil.getSalesExecutiveIdsOrderByCustomer(delegator, partyId);
					if (listMemberIds != null && listMemberIds.size() > 0) {
						listIterator = delegator.findList("PartyFullNameDetail", EntityCondition.makeCondition("partyId", EntityOperator.IN, listMemberIds), null, null, null, false);
					}
				}
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetSalesExecutiveByCustomer service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }*/
    
    @SuppressWarnings({ "unchecked"})
    public static Map<String, Object> jqGetSalesExecutiveOrderByCustomer(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	GenericValue userLogin =(GenericValue) context.get("userLogin");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
    			String partyId = parameters.get("partyId")[0];
    			if (UtilValidate.isNotEmpty(partyId)) {
					List<String> listMemberIds = SalesPartyUtil.getSalesExecutiveIdsOrderByCustomer(delegator, partyId);
					if (listMemberIds != null && listMemberIds.size() > 0) {
						listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listMemberIds));
						listIterator = delegator.findList("PartyFullNameDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
					} else {
						listMemberIds = SalesPartyUtil.getSalesExecutiveIdsOrderByOrganization(delegator, userLogin);
						if (listMemberIds != null && listMemberIds.size() > 0) {
							listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listMemberIds));
							listIterator = delegator.findList("PartyFullNameDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
						}
					}
				}
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetSalesExecutiveOrderByCustomer service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetShippingAddressByPartyReceive(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
    			String partyId = parameters.get("partyId")[0];
    			if (UtilValidate.isNotEmpty(partyId)) {
    				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "contactMechTypeId", "POSTAL_ADDRESS")));
    				listAllConditions.add(EntityUtil.getFilterByDateExpr());
    				listAllConditions.add(EntityUtil.getFilterByDateExpr("purposeFromDate", "purposeThruDate"));
    				Set<String> selectFields = new HashSet<String>();
    				selectFields.add("contactMechId");
    				selectFields.add("toName");
    				selectFields.add("attnName");
    				selectFields.add("address1");
    				selectFields.add("address2");
    				selectFields.add("city");
    				selectFields.add("stateProvinceGeoId");
    				selectFields.add("postalCode");
    				selectFields.add("countryGeoId");
    				selectFields.add("districtGeoId");
    				selectFields.add("wardGeoId");
    				selectFields.add("fromDate");
    				opts.setDistinct(true);
    				
    				Map<String, Object> listConditionAfterProcess = SalesUtil.processSplitListAllCondition(delegator, listAllConditions, "PartyContactDetailByPurpose");
    				List<EntityCondition> listAllConditionOnIn = (List<EntityCondition>) listConditionAfterProcess.get("listAllConditionOnIn");
    				List<Map<String, Object>> listMapConditionOutOf = (List<Map<String, Object>>) listConditionAfterProcess.get("listMapConditionOutOf");
    				
    				if (UtilValidate.isEmpty(listSortFields)) {
    					listSortFields.add("-fromDate");
    				}
    				
    				List<GenericValue> listShippingContactMech = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(listAllConditionOnIn, EntityOperator.AND), selectFields, listSortFields, opts, false);
    				if (listShippingContactMech != null) {
						for (GenericValue shippingAddress : listShippingContactMech) {
							Map<String, Object> itemMap = FastMap.newInstance();
							itemMap.put("contactMechId", shippingAddress.get("contactMechId"));
							itemMap.put("toName", shippingAddress.get("toName"));
							itemMap.put("attnName", shippingAddress.get("attnName"));
							itemMap.put("address1", shippingAddress.get("address1"));
							itemMap.put("address2", shippingAddress.get("address2"));
							itemMap.put("city", shippingAddress.get("city"));
							itemMap.put("stateProvinceGeoId", shippingAddress.get("stateProvinceGeoId"));
							itemMap.put("postalCode", shippingAddress.get("postalCode"));
							
							String countryGeoId = shippingAddress.getString("countryGeoId");
							itemMap.put("countryGeoId", countryGeoId);
							itemMap.put("countryGeoName", countryGeoId);
							
							String stateProvinceGeoId = shippingAddress.getString("stateProvinceGeoId");
							itemMap.put("stateProvinceGeoId", stateProvinceGeoId);
							itemMap.put("stateProvinceGeoName", stateProvinceGeoId);
							
							String districtGeoId = shippingAddress.getString("districtGeoId");
							itemMap.put("districtGeoId", districtGeoId);
							itemMap.put("districtGeoName", districtGeoId);
							
							String wardGeoId = shippingAddress.getString("wardGeoId");
							itemMap.put("wardGeoId", wardGeoId);
							itemMap.put("wardGeoName", wardGeoId);
							
							itemMap.put("countryGeoName", ContactMechWorker.getGeoName(delegator, countryGeoId));
							itemMap.put("stateProvinceGeoName", ContactMechWorker.getGeoName(delegator, stateProvinceGeoId));
							itemMap.put("districtGeoName", ContactMechWorker.getGeoName(delegator, districtGeoId));
							itemMap.put("wardGeoName", ContactMechWorker.getGeoName(delegator, wardGeoId));
							listIterator.add(itemMap);
						}
						
						if (UtilValidate.isNotEmpty(listMapConditionOutOf)) {
							listIterator = EntityMiscUtil.filterMapFromMapCond(listIterator, listMapConditionOutOf);
						}
						if (UtilValidate.isNotEmpty(listSortFields)) {
							listIterator = EntityMiscUtil.sortList(listIterator, listSortFields);
						}
					}
    			}
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetShippingAddressByPartyReceive service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetShippingAddressFullNameByReceiver(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String partyId = SalesUtil.getParameter(parameters, "partyId");
    		if (UtilValidate.isNotEmpty(partyId)) {
				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")));
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				
				Set<String> selectFields = new HashSet<String>();
				selectFields.add("contactMechId");
				selectFields.add("toName");
				selectFields.add("attnName");
				selectFields.add("address1");
				selectFields.add("address2");
				selectFields.add("city");
				selectFields.add("stateProvinceGeoId");
				selectFields.add("postalCode");
				selectFields.add("countryGeoId");
				selectFields.add("districtGeoId");
				selectFields.add("wardGeoId");
				selectFields.add("fullName");
				opts.setDistinct(true);
				
				listIterator = delegator.find("PartyContactMechPurposeAndPostalAddress", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetShippingAddressFullNameByReceiver service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> jqGetShippingMethodByCustomerAndStore(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Locale locale = (Locale) context.get("locale");
    	try {
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0 
    				&& parameters.containsKey("productStoreId") && parameters.get("productStoreId").length > 0 ) {
    			String partyId = parameters.get("partyId")[0];
    			String productStoreId = parameters.get("productStoreId")[0];
    			if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(productStoreId)) {
    				GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
    				if (productStore != null) {
    					List<GenericValue> listSearchResult = new ArrayList<GenericValue>();
    					List<GenericValue> shippingMethods = null;
    			        try {
    			            shippingMethods = delegator.findByAnd("ProductStoreShipmentMethView", UtilMisc.toMap("productStoreId", productStore.get("productStoreId")), UtilMisc.toList("sequenceNumber"), true);
    			        } catch (GenericEntityException e) {
    			            Debug.logError(e, "Unable to get ProductStore shipping methods", module);
    			            return ServiceUtil.returnError("Unable to get ProductStore shipping methods");
    			        }
    			        // clone the list for concurrent modification
    			        List<GenericValue> carrierShipmentMethodList = UtilMisc.makeListWritable(shippingMethods);
    				    if (UtilValidate.isEmpty(carrierShipmentMethodList)) {
    				    	GenericValue itemNoShippingMethod = delegator.makeValue("ProductStoreShipmentMethView");
        					itemNoShippingMethod.put("shipmentMethodTypeId", "NO_SHIPPING");
        					itemNoShippingMethod.put("partyId", "_NA_");
        					
        					GenericValue shipmentMethodType = delegator.findOne("ShipmentMethodType", UtilMisc.toMap("shipmentMethodTypeId", "NO_SHIPPING"), false);
        					if (shipmentMethodType != null) {
        						itemNoShippingMethod.put("description", shipmentMethodType.get("description", locale));
        					}
        					listSearchResult.add(itemNoShippingMethod);
    				    } else {
    				    	listSearchResult.addAll(carrierShipmentMethodList);
    				    }
    				    
    				    if (listSearchResult != null) {
    				    	Set<String> shippingMethodIdsSet = FastSet.newInstance();
							for (GenericValue carrierShipmentMethod : listSearchResult) {
								String shippingMethod = carrierShipmentMethod.getString("shipmentMethodTypeId") + "@" + carrierShipmentMethod.getString("partyId");
								if (!shippingMethodIdsSet.contains(shippingMethod)) {
									Map<String, Object> itemMap = FastMap.newInstance();
									itemMap.put("shipmentMethodTypeId", carrierShipmentMethod.get("shipmentMethodTypeId"));
									itemMap.put("partyId", carrierShipmentMethod.get("partyId"));
									itemMap.put("description", carrierShipmentMethod.get("description", locale));
									itemMap.put("shippingMethod", shippingMethod);
									listIterator.add(itemMap);
									shippingMethodIdsSet.add(shippingMethod);
								}
							}
						}
    				}
    			}
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetShippingMethodByCustomerAndStore service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    @SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> jqGetPaymentMethodByCustomerAndStore(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Locale locale = (Locale) context.get("locale");
    	try {
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0 
    				&& parameters.containsKey("productStoreId") && parameters.get("productStoreId").length > 0 ) {
    			String partyId = parameters.get("partyId")[0];
    			String productStoreId = parameters.get("productStoreId")[0];
    			if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(productStoreId)) {
    				GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
    				GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
    				
    				// load payment method open all
					List<String> listPaymentApprove = SalesUtil.getPropertyProcessedMultiKey(delegator, "product.store.payment.method.open");
					List<GenericValue> productStorePaymentSettingList = productStore.getRelated("ProductStorePaymentSetting", null, null, true);
					
					EntityCondition condShowToCustomer = EntityCondition.makeCondition(EntityCondition.makeCondition("showToCustomer", "Y"), EntityOperator.OR, EntityCondition.makeCondition("showToCustomer", null));
					productStorePaymentSettingList = EntityUtil.filterByCondition(productStorePaymentSettingList, condShowToCustomer);
					
					if (UtilValidate.isNotEmpty(listPaymentApprove)) {
						productStorePaymentSettingList = EntityUtil.filterByCondition(productStorePaymentSettingList, EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.IN, listPaymentApprove));
					}
					Iterator<GenericValue> productStorePaymentSettingIter = productStorePaymentSettingList.iterator();
					while (productStorePaymentSettingIter.hasNext()) {
						GenericValue productStorePaymentSetting = productStorePaymentSettingIter.next();
						GenericValue paymentMethodType = productStorePaymentSetting.getRelatedOne("PaymentMethodType", false);
						Map<String, Object> productStorePaymentMethodTypeIdMap = FastMap.newInstance();
						productStorePaymentMethodTypeIdMap.put("paymentMethodTypeId", productStorePaymentSetting.get("paymentMethodTypeId"));
						//productStorePaymentMethodTypeIdMap.put("value", true);
						productStorePaymentMethodTypeIdMap.put("description", paymentMethodType.get("description", locale));
						listIterator.add(productStorePaymentMethodTypeIdMap);
					}
					
					// load payment method for special party
					//List<GenericValue> paymentMethodList = null;
    				//if (party != null) paymentMethodList = EntityUtil.filterByDate(party.getRelated("PaymentMethod", null, UtilMisc.toList("paymentMethodTypeId"), false), true);
					//if (UtilValidate.isNotEmpty(paymentMethodList)) {
						// TODO
					//}
    			}
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetShippingMethodByCustomerAndStore service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    /**
     * API for LOG module
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> getPartiesSalesReceiveNotifyOrder(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String productStoreId = (String) context.get("productStoreId");
    	String statusId = (String) context.get("statusId");
    	List<String> listReturn = new ArrayList<String>();
    	
    	if (UtilValidate.isEmpty(productStoreId)) {
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSProductStoreIdMustNotBeEmpty", locale));
    	}
    	try {
    		if ("ORDER_HOLD".equals(statusId)) {
    			List<String> listRoleTypeId = SalesUtil.processKeyProperty(EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "roleTypeId.receiveMsg.order.held", delegator)); 
        		List<EntityCondition> listAllCondition = FastList.newInstance();
        		listAllCondition.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, listRoleTypeId));
        		listAllCondition.add(EntityCondition.makeCondition("productStoreId", productStoreId));
        		listReturn = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false), 
        						"partyId", true);
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getPartiesSalesReceiveNotifyOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	if (listReturn == null) listReturn = new ArrayList<String>();
    	successResult.put("partyIds", listReturn);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListOrganizationPartyAcctg(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
			listIterator = delegator.find("PartyAcctgPrefAndGroup", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrganizationPartyAcctg service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPartyFullName(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		listIterator = delegator.find("PartyFullNameDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListPartyFullName service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    // com.olbius.basesales.product.ProductWorker.convertCustomType(Delegator, String, String, String, GenericValue, boolean)
    public static Map<String, Object> checkAndConvertRelCustomer(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String partyId = (String) context.get("partyId");
    	
    	try {
    		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    		List<EntityCondition> listAllCondition = FastList.newInstance();
    		String organization = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		String partyRelContactCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "party.rel.contact.customer", delegator);
    		
    		// Kiem tra co phai khach hang tiem nang khong?
    		String roleContact = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "role.contact.customer", delegator);
    		String roleOrgToCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "role.org.to.customer", delegator);
    		
			listAllCondition.clear();
			listAllCondition.add(EntityUtil.getFilterByDateExpr());
			listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", partyId));
			listAllCondition.add(EntityCondition.makeCondition("partyIdTo", organization));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", roleContact));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", roleOrgToCustomer));
			listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelContactCustomer));
			List<GenericValue> listContactRels = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false);
			if (UtilValidate.isNotEmpty(listContactRels)) {
				// is contact customer
				
				// CONTACT to INDIVIDUAL
				listAllCondition.clear();
				String channelRetail = SalesUtil.getPropertyValue(delegator, "sales.method.channel.enum.id.telesales");
				listAllCondition.add(EntityCondition.makeCondition("payToPartyId", organization));
				listAllCondition.add(EntityCondition.makeCondition("salesMethodChannelEnumId", channelRetail));
				List<GenericValue> listProductStoreRetail = delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false);
				if (UtilValidate.isEmpty(listProductStoreRetail)) {
					return successResult;
				}
				String partyRelCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "party.rel.sales.rep.to.customer", delegator);
				
				// Find and thru khach hang tiem nang
				for (GenericValue contactRel : listContactRels) {
					contactRel.put("thruDate", nowTimestamp);
				}
				delegator.storeAll(listContactRels);
				
				// Add khach hang ban le
				String roleIndividualCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "role.individual.customer", delegator);
				GenericValue findPartyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleIndividualCustomer), false);
				if (findPartyRole == null) delegator.create("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleIndividualCustomer));
				listAllCondition.clear();
				listAllCondition.add(EntityUtil.getFilterByDateExpr());
				listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", partyId));
				listAllCondition.add(EntityCondition.makeCondition("partyIdTo", organization));
				listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", roleIndividualCustomer));
				listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", roleOrgToCustomer));
				listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelCustomer));
				List<GenericValue> findListPartyRelIndividualCustomer = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false);
				if (UtilValidate.isEmpty(findListPartyRelIndividualCustomer)) {
					GenericValue partyRelCustomerNew = delegator.makeValue("PartyRelationship");
					partyRelCustomerNew.set("partyIdFrom", partyId);
					partyRelCustomerNew.set("partyIdTo", organization);
					partyRelCustomerNew.set("roleTypeIdFrom", roleIndividualCustomer);
					partyRelCustomerNew.set("roleTypeIdTo", roleOrgToCustomer);
					partyRelCustomerNew.set("fromDate", nowTimestamp);
					partyRelCustomerNew.set("partyRelationshipTypeId", partyRelCustomer);
					delegator.create(partyRelCustomerNew);
				}
			}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling convertRelCustomer service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	return successResult;
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListCustomerGroup(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String partyTypeId = SalesUtil.getPropertyValue(delegator, "group.party.type.customer");
			listAllConditions.add(EntityCondition.makeCondition("partyTypeId", partyTypeId));
			listIterator = delegator.find("PartyFullNameDetailSimple", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListCustomerGroup service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings({ "unchecked"})
    public static Map<String, Object> jqGetListPartyMemeberMT(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String partyGroupId = null;
    		if (parameters.containsKey("groupId") && parameters.get("groupId").length > 0) {
    			partyGroupId = parameters.get("groupId")[0];
    		}
    		if (UtilValidate.isNotEmpty(partyGroupId)) {
    			String partyRelationshipTypeId = "GROUP_ROLLUP";
    			listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", partyGroupId));
				listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-fromDate");
				}
				listIterator = delegator.find("PartyToAndPartyNameDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPartyMemeber service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String, Object> jqGetListCustomerMT(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
			listAllConditions.add(EntityCondition.makeCondition("partyTypeId", "CUSTOMER_CHAIN_GROUP"));
			listIterator = delegator.find("PartyFullNameDetailSimple", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListCustomerGroup service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    
    public static Map<String, Object> createCustomerGroup(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSCreateSuccessful", locale));
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String partyId = (String)context.get("partyId");
		String partyCode = (String)context.get("partyCode");
		String groupName = (String)context.get("groupName");
		String partyTypeId = SalesUtil.getPropertyValue(delegator, "group.party.type.customer");
		String statusId = "PARTY_ENABLED";
		String customerGroupRoleType = SalesUtil.getPropertyValue(delegator, "group.role.from.parent.member");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		try {
			List<EntityCondition> listConds = FastList.newInstance();
			if (UtilValidate.isNotEmpty(partyId)) {
				listConds.add(EntityCondition.makeCondition("partyCode", partyId));
				listConds.add(EntityCondition.makeCondition("partyId", partyId));
			}
			listConds.add(EntityCondition.makeCondition("partyCode", partyCode));
			listConds.add(EntityCondition.makeCondition("partyId", partyCode));
			List<GenericValue> parties = delegator.findList("Party", EntityCondition.makeCondition(listConds, EntityOperator.OR), null, null, null, false);
			if(UtilValidate.isNotEmpty(parties)){
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSGroupIdHasExisted", locale));
			}
			
			if (UtilValidate.isEmpty(partyId)) {
				partyId = "CUSTGRP" + delegator.getNextSeqId("Party");
			}
			
			GenericValue groupParty = delegator.makeValue("Party");
			groupParty.set("partyId", partyId);
			groupParty.set("partyCode", partyCode);
			groupParty.set("partyTypeId", partyTypeId);
			groupParty.set("description", groupName);
			groupParty.set("statusId", statusId);
			groupParty.set("createdDate", nowTimestamp);
			groupParty.set("createdByUserLogin", userLogin.get("userLoginId"));
			delegator.create(groupParty);
			
			GenericValue groupPartyGroup = delegator.makeValue("PartyGroup");
			groupPartyGroup.set("partyId", partyId);
			groupPartyGroup.set("groupName", groupName);
			delegator.create(groupPartyGroup);
			
			GenericValue customerGroupRole = delegator.makeValue("PartyRole");
			customerGroupRole.set("partyId", partyId);
			customerGroupRole.set("roleTypeId", customerGroupRoleType);
			delegator.create(customerGroupRole);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createCustomerGroup service: " + e.toString();
    		Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
    
    public static Map<String, Object> createCustomerMT(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSCreateSuccessful", locale));
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyCode = null;
		String partyId = null;
		String groupName = (String)context.get("groupName");
		String description = (String)context.get("description");
		String partyTypeId = "CUSTOMER_CHAIN_GROUP";
		String statusId = "PARTY_DISABLED";
		String roleType = "CUSTOMER_CHAIN_GROUP";
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		try {
			List<EntityCondition> listConds = FastList.newInstance();
			if (UtilValidate.isNotEmpty(context.get("partyCode"))) {
				partyCode = (String) context.get("partyCode");
				listConds.add(EntityCondition.makeCondition("partyCode", partyCode));
				List<GenericValue> parties = delegator.findList("Party", EntityCondition.makeCondition(listConds, EntityOperator.OR), null, null, null, false);
				if(UtilValidate.isNotEmpty(parties)){
					return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSGroupIdHasExisted", locale));
				}
			}
			
			partyId = "CHAIN" + delegator.getNextSeqId("Party");
			if (!UtilValidate.isNotEmpty(partyCode)) {
				partyCode = partyId;
			}
			
			GenericValue party = delegator.makeValue("Party");
			party.set("partyId", partyId);
			party.set("partyCode", partyCode);
			party.set("partyTypeId", partyTypeId);
			party.set("description", description);
			party.set("statusId", statusId);
			party.set("createdDate", nowTimestamp);
			party.set("createdByUserLogin", userLogin.get("userLoginId"));
			delegator.create(party);
			
			GenericValue partyGroup = delegator.makeValue("PartyGroup");
			partyGroup.set("partyId", partyId);
			partyGroup.set("groupName", groupName);
			delegator.create(partyGroup);
			
			GenericValue partyRole = delegator.makeValue("PartyRole");
			partyRole.set("partyId", partyId);
			partyRole.set("roleTypeId", roleType);
			delegator.create(partyRole);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createCustomerGroup service: " + e.toString();
    		Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
    
    public static Map<String, Object> updateCustomerMT(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSUpdateSuccessful", locale));
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String)context.get("partyId");
		String partyCode = (String)context.get("partyCode");
		String groupName = (String)context.get("groupName");
		String description = (String)context.get("description");
		
		try {
			if (!UtilValidate.isNotEmpty(partyCode)) {
				partyCode = partyId;
			}
			GenericValue party = EntityUtil.getFirst(delegator.findList("Party",  EntityCondition.makeCondition("partyId", partyId), null, null, null, Boolean.FALSE));
			if (!party.get("partyCode").equals(partyCode)){
				List<EntityCondition> listConds = FastList.newInstance();
				if (UtilValidate.isNotEmpty(partyCode)) {
					listConds.add(EntityCondition.makeCondition("partyCode", partyCode));
					List<GenericValue> parties = delegator.findList("Party", EntityCondition.makeCondition(listConds, EntityOperator.OR), null, null, null, false);
					if(UtilValidate.isNotEmpty(parties)){
						return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSGroupIdHasExisted", locale));
					}
				}
			}
			party.set("partyCode", partyCode);
			party.set("description", description);
			delegator.store(party);
			GenericValue partyGroup = EntityUtil.getFirst(delegator.findList("PartyGroup",  EntityCondition.makeCondition("partyId", partyId), null, null, null, Boolean.FALSE));
			partyGroup.set("groupName", groupName);
			delegator.store(partyGroup);
			
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createCustomerGroup service: " + e.toString();
    		Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
    public static Map<String, Object> changeStatusCustomerMT(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSUpdateSuccessful", locale));
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String statusId = (String)context.get("statusId");
		String partyId = (String)context.get("partyId");
		try {
			
			GenericValue party = EntityUtil.getFirst(delegator.findList("Party",  EntityCondition.makeCondition("partyId", partyId), null, null, null, Boolean.FALSE));
			party.set("statusId", statusId);
			delegator.store(party);
			
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createCustomerGroup service: " + e.toString();
    		Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
    public static Map<String, Object> removePartyMT(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSCreateSuccessful", locale));
		
		String cMemberr = (String) context.get("cMemberr");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		try {
			JSONObject listJson = JSONObject.fromObject(cMemberr);
			if (UtilValidate.isNotEmpty(listJson)) {
				String partyIdFrom = (String) listJson.get("partyIdFrom");
				String partyIdTo = (String) listJson.get("partyIdTo");
				String roleFrom = (String) listJson.get("roleTypeIdFrom");
				String roleTo = (String) listJson.get("roleTypeIdTo");
				Timestamp fromDate = new Timestamp(Long.parseLong(listJson.getString("fromDate")));
				Timestamp thruDateNew = nowTimestamp;
				GenericValue member = delegator.findOne("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", roleFrom, "roleTypeIdTo", roleTo, "fromDate", fromDate), false);		
				if (member == null) {
					return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource_error, "BSRecordHasIdIsNotFound", UtilMisc.toList(partyIdTo), locale));
				}
				if (fromDate.after(thruDateNew)) {
					member.set("thruDate", fromDate);
				} else {
					member.set("thruDate", thruDateNew);
				}
				member.store();
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling removePartyMember service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
    
    public static Map<String, Object> createRelaCustomerMT(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSCreateSuccessful", locale));
		
		String memberList = (String) context.get("aRoleType");
		String partyRelationshipTypeId = "GROUP_ROLLUP";
		String roleTypeIdParent = "CUSTOMER_CHAIN_GROUP";
		String roleTypeIdChild = "CHILD_MEMBER";
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		try {
			JSONObject listJson = JSONObject.fromObject(memberList);
			if(UtilValidate.isNotEmpty(listJson)){
				String partyFrom = (String) listJson.get("groupId");
				String partyMemberId = (String) listJson.get("memberId");
				
				List<EntityCondition> listConds = FastList.newInstance();
				listConds.add(EntityCondition.makeCondition("partyIdFrom", partyFrom));
				listConds.add(EntityCondition.makeCondition("partyIdTo", partyMemberId));
				listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
				listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdParent));
				listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdChild));
				listConds.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> checkRelationshipExisted = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
				if (UtilValidate.isNotEmpty(checkRelationshipExisted)) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSThisRecordIsAlreadyExists", locale));
				}
				
				GenericValue checkMemberHasRole = delegator.findOne("PartyRole", UtilMisc.<String, Object>toMap("partyId", partyMemberId, "roleTypeId", roleTypeIdChild), false);
				if (checkMemberHasRole == null) {
					GenericValue childRole = delegator.makeValue("PartyRole");
					childRole.set("partyId", partyMemberId);
					childRole.set("roleTypeId", roleTypeIdChild);
					delegator.create(childRole);
				}
				
				GenericValue roleType = delegator.makeValue("PartyRelationship");
				roleType.set("partyIdFrom", partyFrom);
				roleType.set("partyIdTo", partyMemberId);
				roleType.set("partyRelationshipTypeId", partyRelationshipTypeId);
				roleType.set("roleTypeIdFrom", roleTypeIdParent);
				roleType.set("roleTypeIdTo", roleTypeIdChild);
				roleType.set("fromDate", fromDate);
				delegator.create(roleType);
			}
	    } catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createRelaCustomerGroup service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
    
    public static Map<String, Object> createRelaCustomerGroup(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSCreateSuccessful", locale));
		
		String memberList = (String) context.get("aRoleType");
		String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "group.party.rel.member");
		String roleTypeIdParent = SalesUtil.getPropertyValue(delegator, "group.role.from.parent.member");
		String roleTypeIdChild = SalesUtil.getPropertyValue(delegator, "group.role.to.child.member");
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		try {
			JSONObject listJson = JSONObject.fromObject(memberList);
			if(UtilValidate.isNotEmpty(listJson)){
				String partyFrom = (String) listJson.get("groupId");
				String partyMemberId = (String) listJson.get("memberId");
				
				List<EntityCondition> listConds = FastList.newInstance();
				listConds.add(EntityCondition.makeCondition("partyIdFrom", partyFrom));
				listConds.add(EntityCondition.makeCondition("partyIdTo", partyMemberId));
				listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
				listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdParent));
				listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdChild));
				listConds.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> checkRelationshipExisted = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
				if (UtilValidate.isNotEmpty(checkRelationshipExisted)) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSThisRecordIsAlreadyExists", locale));
				}
				
				GenericValue checkMemberHasRole = delegator.findOne("PartyRole", UtilMisc.<String, Object>toMap("partyId", partyMemberId, "roleTypeId", roleTypeIdChild), false);
				if (checkMemberHasRole == null) {
					GenericValue childRole = delegator.makeValue("PartyRole");
					childRole.set("partyId", partyMemberId);
					childRole.set("roleTypeId", roleTypeIdChild);
					delegator.create(childRole);
				}
				
				GenericValue roleType = delegator.makeValue("PartyRelationship");
				roleType.set("partyIdFrom", partyFrom);
				roleType.set("partyIdTo", partyMemberId);
				roleType.set("partyRelationshipTypeId", partyRelationshipTypeId);
				roleType.set("roleTypeIdFrom", roleTypeIdParent);
				roleType.set("roleTypeIdTo", roleTypeIdChild);
				roleType.set("fromDate", fromDate);
				delegator.create(roleType);
			}
	    } catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createRelaCustomerGroup service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
    
    @SuppressWarnings({ "unchecked"})
    public static Map<String, Object> jqGetListPartyMemeber(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String partyGroupId = null;
    		if (parameters.containsKey("groupId") && parameters.get("groupId").length > 0) {
    			partyGroupId = parameters.get("groupId")[0];
    		}
    		if (UtilValidate.isNotEmpty(partyGroupId)) {
    			String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "group.party.rel.member");
    			listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", partyGroupId));
				listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-fromDate");
				}
				listIterator = delegator.find("PartyToAndPartyNameDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPartyMemeber service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String, Object> removePartyMember(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSCreateSuccessful", locale));
		
		String cMemberr = (String) context.get("cMemberr");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		try {
			JSONObject listJson = JSONObject.fromObject(cMemberr);
			if (UtilValidate.isNotEmpty(listJson)) {
				String partyIdFrom = (String) listJson.get("partyIdFrom");
				String partyIdTo = (String) listJson.get("partyIdTo");
				String roleFrom = (String) listJson.get("roleTypeIdFrom");
				String roleTo = (String) listJson.get("roleTypeIdTo");
				Timestamp fromDate = new Timestamp(Long.parseLong(listJson.getString("fromDate")));
				Timestamp thruDateNew = nowTimestamp;
				GenericValue member = delegator.findOne("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", roleFrom, "roleTypeIdTo", roleTo, "fromDate", fromDate), false);		
				if (member == null) {
					return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource_error, "BSRecordHasIdIsNotFound", UtilMisc.toList(partyIdTo), locale));
				}
				if (fromDate.after(thruDateNew)) {
					member.set("thruDate", fromDate);
				} else {
					member.set("thruDate", thruDateNew);
				}
				member.store();
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling removePartyMember service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> addPartyToPartyClassification(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	try {
    		String partyClassificationGroupId = (String) context.get("partyClassificationGroupId");
    		String fromDateStr = (String) context.get("fromDate");
    		String thruDateStr = (String) context.get("thruDate");
    		List<String> listCustomers = (List<String>) context.get("listCustomers[]");
    		
    		Timestamp fromDate = null;
            Timestamp thruDate = null;
            try {
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
            	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
            }
            
            List<GenericValue> tobeStored = new LinkedList<GenericValue>();
            for (String customerId : listCustomers) {
            	GenericValue partyClass = delegator.makeValue("PartyClassification", 
            			UtilMisc.toMap("partyId", customerId, 
            					"partyClassificationGroupId", partyClassificationGroupId,
            					"fromDate", fromDate, 
            					"thruDate", thruDate));
            	tobeStored.add(partyClass);
            }
            delegator.storeAll(tobeStored);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling addPartyToPartyClassification service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	return successResult;
    }
    
    public static Map<String, Object> calcPartyContactTempData(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	try {
    		boolean isRunning = true;
    		int index = 0;
    		int size = 100;
    		
    		List<EntityCondition> listConditions = FastList.newInstance();
    		/*listConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "CUSTOMER"));
    		listConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
    		listConditions.add(EntityUtil.getFilterByDateExpr());
    		listConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "CUSTOMER_REL"));*/
    		listConditions.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
    		EntityCondition mainCond = EntityCondition.makeCondition(listConditions);
    		
    		EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
    		List<String> sortByFields = FastList.newInstance();
    		sortByFields.add("-fromDate");
    		while (isRunning) {
    			int iIndex = index * size + 1;
    			/*EntityFindOptions opts = new EntityFindOptions();
    			opts.setOffset(lowIndex);
    			opts.setLimit(size);*/
    			//opts.setMaxRows(size);
    			
    			//List<GenericValue> listParties = delegator.findList("PartyFromAndPartyNameDetail", mainCond, null, null, opts, false);
    			//List<GenericValue> listParties = delegator.findList("PartyRole", mainCond, null, null, opts, false);
    			List<GenericValue> listParties = null;
    			
    			EntityListIterator iterator = null;
    			try {
    				iterator = delegator.find("PartyRole", mainCond, null, null, null, opts);
	    			listParties = iterator.getPartialList(iIndex, size);
    			} catch(Exception e) {
    				Debug.logWarning("Error when select", module);
    			} finally {
					if (iterator != null) {
						iterator.close();
					}
				}
    			if (UtilValidate.isEmpty(listParties)) {
    				isRunning = false;
    				continue;
    			}
    			
    			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
    			for (GenericValue partyRel : listParties) {
    				String partyId = partyRel.getString("partyId");
    				GenericValue partyTmp = delegator.findOne("PartyContactTempData", UtilMisc.toMap("partyId", partyId), false);
    				if (partyTmp == null) {
    					// create
    					GenericValue party = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyId), false);
    					if (party == null) {
    						continue;
    					}
    					
    					GenericValue newPartyContact = delegator.makeValue("PartyContactTempData");
    					newPartyContact.put("partyId", partyId);
    					newPartyContact.put("partyCode", party.get("partyCode"));
    					newPartyContact.put("partyName", party.get("fullName"));
    					
    					List<EntityCondition> tmpConds = FastList.newInstance();
    					tmpConds.add(EntityCondition.makeCondition("partyId", partyId));
    					tmpConds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_PHONE"));
    					tmpConds.add(EntityUtil.getFilterByDateExpr());
    					// check telecomNumber
    					List<String> ptyTelecomPurpose = EntityUtil.getFieldListFromEntityList(
    							delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(tmpConds), null, sortByFields, null, false)
								, "contactMechId", true); //EntityOperator.IN, UtilMisc.toList("PHONE_WORK", "PHONE_HOME", "PHONE_SHIPPING", "PRIMARY_PHONE")
    					if (UtilValidate.isNotEmpty(ptyTelecomPurpose)) {
    						String telecomContactId = ptyTelecomPurpose.get(0);
    						GenericValue telecomContact = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", telecomContactId), false);
    						if (telecomContact != null) {
    							StringBuilder telecomName = new StringBuilder();
    							telecomName.append(UtilFormatOut.ifNotEmpty(telecomContact.getString("countryCode"), "", "-"));
    							telecomName.append(UtilFormatOut.checkNull(telecomContact.getString("contactNumber")));
    							newPartyContact.set("telecomName", telecomName.toString());
    						}
    						newPartyContact.put("telecomId", telecomContactId);
    					}
    					// check postalAddress
    					tmpConds.clear();
    					tmpConds.add(EntityCondition.makeCondition("partyId", partyId));
    					tmpConds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
    					tmpConds.add(EntityUtil.getFilterByDateExpr());
    					List<String> ptyPostalPurpose = EntityUtil.getFieldListFromEntityList(
    							delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(tmpConds), null, sortByFields, null, false)
								, "contactMechId", true); //, EntityOperator.IN, UtilMisc.toList("PRIMARY_LOCATION", "SHIPPING_LOCATION")
    					if (UtilValidate.isNotEmpty(ptyPostalPurpose)) {
    						String postalAddressContactId = ptyPostalPurpose.get(0);
    						/*GenericValue postalAddress = delegator.findOne("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", postalAddressContactId), false);
    						newPartyContact.put("postalAddressId", postalAddressContactId);
    						if (postalAddress != null) {
        						newPartyContact.put("postalAddressName", postalAddress.get("fullName"));
    						}*/
    						String postalAddressName = PartyWorker.getFullNamePostalAddress(delegator, postalAddressContactId);
    						newPartyContact.set("postalAddressId", postalAddressContactId);
    						newPartyContact.set("postalAddressName", postalAddressName);
    					}
    					tobeStored.add(newPartyContact);
    				} else {
    					// update
    					GenericValue party = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyId), false);
    					if (party == null) {
    						continue;
    					}
    					
    					partyTmp.put("partyCode", party.get("partyCode"));
    					partyTmp.put("partyName", party.get("fullName"));
    					
    					// check telecomNumber
    					List<EntityCondition> tmpConds = FastList.newInstance();
    					tmpConds.add(EntityCondition.makeCondition("partyId", partyId));
    					tmpConds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_PHONE"));
    					tmpConds.add(EntityUtil.getFilterByDateExpr());
    					List<String> ptyTelecomPurpose = EntityUtil.getFieldListFromEntityList(
    							delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(tmpConds), null, null, null, false)
								, "contactMechId", true); //EntityOperator.IN, UtilMisc.toList("PHONE_WORK", "PHONE_HOME", "PHONE_SHIPPING", "PRIMARY_PHONE")
    					if (UtilValidate.isNotEmpty(ptyTelecomPurpose)) {
    						String telecomContactId = ptyTelecomPurpose.get(0);
    						GenericValue telecomContact = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", telecomContactId), false);
    						if (telecomContact != null) {
    							StringBuilder telecomName = new StringBuilder();
    							telecomName.append(UtilFormatOut.ifNotEmpty(telecomContact.getString("countryCode"), "", "-"));
    							telecomName.append(UtilFormatOut.checkNull(telecomContact.getString("contactNumber")));
    							partyTmp.set("telecomName", telecomName.toString());
    						}
    						partyTmp.put("telecomId", telecomContactId);
    					}
    					// check postalAddress
    					tmpConds.clear();
    					tmpConds.add(EntityCondition.makeCondition("partyId", partyId));
    					tmpConds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
    					tmpConds.add(EntityUtil.getFilterByDateExpr());
    					List<String> ptyPostalPurpose = EntityUtil.getFieldListFromEntityList(
    							delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(tmpConds), null, null, null, false)
								, "contactMechId", true); //, EntityOperator.IN, UtilMisc.toList("PRIMARY_LOCATION", "SHIPPING_LOCATION")
    					if (UtilValidate.isNotEmpty(ptyPostalPurpose)) {
    						String postalAddressContactId = ptyPostalPurpose.get(0);
    						/*GenericValue postalAddress = delegator.findOne("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", postalAddressContactId), false);
    						partyTmp.put("postalAddressId", postalAddressContactId);
    						if (postalAddress != null) {
    							partyTmp.put("postalAddressName", postalAddress.get("fullName"));
    						} else {
    							partyTmp.put("postalAddressName", "");
    						}*/
    						String postalAddressName = PartyWorker.getFullNamePostalAddress(delegator, postalAddressContactId);
    						partyTmp.set("postalAddressId", postalAddressContactId);
    						partyTmp.set("postalAddressName", postalAddressName);
    					}
    					tobeStored.add(partyTmp);
    				}
    			}
    			delegator.storeAll(tobeStored);
    			index++;
    		}
	    } catch (Exception e) {
			String errMsg = "Fatal error calling calcPartyContactTempData service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	return successResult;
    }
    
    public static Map<String, Object> updatePartyContactTempData (DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	try {
    		String infoUpdate = (String) context.get("infoUpdate");
    		String partyIdTmp = (String) context.get("partyId");
    		String contactMechId = (String) context.get("contactMechId");

    		List<String> partyIds = FastList.newInstance();
    		if (UtilValidate.isNotEmpty(partyIdTmp)) partyIds.add(partyIdTmp);
    		
    		if ("PostalAddress".equals(infoUpdate)) {
    			List<EntityCondition> tmpConds = FastList.newInstance();
				tmpConds.add(EntityCondition.makeCondition("contactMechId", contactMechId));
				tmpConds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
				tmpConds.add(EntityUtil.getFilterByDateExpr());
				partyIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(tmpConds), null, null, null, false), "partyId", true);
    		}
    		
    		for (String partyId : partyIds) {
    			GenericValue partyContactTempData = delegator.findOne("PartyContactTempData", UtilMisc.toMap("partyId", partyId), false);
        		if (partyContactTempData != null && UtilValidate.isNotEmpty(infoUpdate)) {
        			if ("PartyCode".equals(infoUpdate)) {
        				GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
        				String partyCodeNew = party.getString("partyCode");
        				if (partyCodeNew == null) partyCodeNew = party.getString("partyId");
        				if (!partyCodeNew.equals(partyContactTempData.getString("partyCode"))) {
        					partyContactTempData.set("partyCode", partyCodeNew);
        					delegator.store(partyContactTempData);
        				}
        			} else if ("GroupName".equals(infoUpdate)) {
        				GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
        				String partyName = partyGroup.getString("groupName");
        				if (partyName == null || !partyName.equals(partyContactTempData.getString("partyName"))) {
        					partyContactTempData.set("partyName", partyGroup.getString("groupName"));
            				delegator.store(partyContactTempData);
        				}
        			} else if ("PersonName".equals(infoUpdate)) {
        				GenericValue partyGroup = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
        				if (partyGroup != null) {
        					StringBuilder partyName = new StringBuilder();
        					partyName.append(UtilFormatOut.ifNotEmpty(partyGroup.getString("lastName"), "", " "));
        					partyName.append(UtilFormatOut.ifNotEmpty(partyGroup.getString("middleName"), "", " "));
        					partyName.append(UtilFormatOut.ifNotEmpty(partyGroup.getString("firstName"), "", " "));
        					
        					partyContactTempData.set("partyName", partyName.toString());
            				delegator.store(partyContactTempData);
        				}
        			} else if ("PostalAddress".equals(infoUpdate)) {
        				GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
        				if (postalAddress != null) {
        					String postalAddressName = PartyWorker.getFullNamePostalAddress(delegator, contactMechId);
        					partyContactTempData.set("postalAddressName", postalAddressName);
            				delegator.store(partyContactTempData);
        				}
        			} else if ("TelecomNumber".equals(infoUpdate)) {
        				GenericValue telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId), false);
        				if (telecomNumber != null) {
							StringBuilder telecomName = new StringBuilder();
							telecomName.append(UtilFormatOut.ifNotEmpty(telecomNumber.getString("countryCode"), "", "-"));
							telecomName.append(UtilFormatOut.checkNull(telecomNumber.getString("contactNumber")));
							partyContactTempData.set("telecomName", telecomName.toString());
							delegator.store(partyContactTempData);
        				}
        			} else if ("PartyContactMechPurpose".equals(infoUpdate)) {
        				String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");
        				if ("PRIMARY_LOCATION".equals(contactMechPurposeTypeId) || "PRIMARY_PHONE".equals(contactMechPurposeTypeId)) {
        					Timestamp thruDate = (Timestamp) context.get("thruDate");
            				Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            				if (thruDate != null && nowTimestamp.compareTo(thruDate) >= 0) {
            					if (contactMechId.equals(partyContactTempData.get("telecomId"))) {
            						partyContactTempData.set("telecomId", null);
            						partyContactTempData.set("telecomName", null);
                				} else if (contactMechId.equals(partyContactTempData.get("postalAddressId"))) {
                					partyContactTempData.set("postalAddressId", null);
                					partyContactTempData.set("postalAddressName", null);
                				}
            				} else {
            					List<String> sortByFields = FastList.newInstance();
            		    		sortByFields.add("-fromDate");
            					if ("PRIMARY_LOCATION".equals(contactMechPurposeTypeId)) {
            						// check postalAddress
            						List<EntityCondition> tmpConds = FastList.newInstance();
                					tmpConds.add(EntityCondition.makeCondition("partyId", partyId));
                					tmpConds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
                					tmpConds.add(EntityUtil.getFilterByDateExpr());
                					List<String> ptyPostalPurpose = EntityUtil.getFieldListFromEntityList(
                							delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(tmpConds), null, sortByFields, null, false)
            								, "contactMechId", true); //, EntityOperator.IN, UtilMisc.toList("PRIMARY_LOCATION", "SHIPPING_LOCATION")
                					if (UtilValidate.isNotEmpty(ptyPostalPurpose)) {
                						String postalAddressContactId = ptyPostalPurpose.get(0);
                						String postalAddressName = PartyWorker.getFullNamePostalAddress(delegator, postalAddressContactId);
                						partyContactTempData.set("postalAddressId", postalAddressContactId);
            							partyContactTempData.set("postalAddressName", postalAddressName);
                					}
            					} else if ("PRIMARY_PHONE".equals(contactMechPurposeTypeId)) {
            						// check telecomNumber
                					List<EntityCondition> tmpConds = FastList.newInstance();
                					tmpConds.add(EntityCondition.makeCondition("partyId", partyId));
                					tmpConds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_PHONE"));
                					tmpConds.add(EntityUtil.getFilterByDateExpr());
                					List<String> ptyTelecomPurpose = EntityUtil.getFieldListFromEntityList(
                							delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(tmpConds), null, sortByFields, null, false)
            								, "contactMechId", true); //EntityOperator.IN, UtilMisc.toList("PHONE_WORK", "PHONE_HOME", "PHONE_SHIPPING", "PRIMARY_PHONE")
                					if (UtilValidate.isNotEmpty(ptyTelecomPurpose)) {
                						String telecomContactId = ptyTelecomPurpose.get(0);
                						GenericValue telecomContact = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", telecomContactId), false);
                						if (telecomContact != null) {
                							StringBuilder telecomName = new StringBuilder();
                							telecomName.append(UtilFormatOut.ifNotEmpty(telecomContact.getString("countryCode"), "", "-"));
                							telecomName.append(UtilFormatOut.checkNull(telecomContact.getString("contactNumber")));
                							partyContactTempData.set("telecomName", telecomName.toString());
                						}
                						partyContactTempData.set("telecomId", telecomContactId);
                					}
            					}
            				}
            				delegator.store(partyContactTempData);
        				}
        			}
        		} else if (UtilValidate.isNotEmpty(infoUpdate)) {
        			// create
        			if ("PartyRole".equals(infoUpdate)) {
        				GenericValue party = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyId), false);
        				String partyCodeNew = party.getString("partyCode");
        				if (partyCodeNew == null) partyCodeNew = party.getString("partyId");
        				
        				partyContactTempData = delegator.makeValue("PartyContactTempData");
        				partyContactTempData.put("partyId", partyId);
        				if (!partyCodeNew.equals(partyContactTempData.getString("partyCode"))) {
        					partyContactTempData.put("partyCode", partyCodeNew);
        				}
        				
        				List<String> sortByFields = FastList.newInstance();
    		    		sortByFields.add("-fromDate");
    		    		
    		    		partyContactTempData.put("partyName", party.get("fullName"));
    					
    					List<EntityCondition> tmpConds = FastList.newInstance();
    					tmpConds.add(EntityCondition.makeCondition("partyId", partyId));
    					tmpConds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_PHONE"));
    					tmpConds.add(EntityUtil.getFilterByDateExpr());
    					// check telecomNumber
    					List<String> ptyTelecomPurpose = EntityUtil.getFieldListFromEntityList(
    							delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(tmpConds), null, sortByFields, null, false)
								, "contactMechId", true); //EntityOperator.IN, UtilMisc.toList("PHONE_WORK", "PHONE_HOME", "PHONE_SHIPPING", "PRIMARY_PHONE")
    					if (UtilValidate.isNotEmpty(ptyTelecomPurpose)) {
    						String telecomContactId = ptyTelecomPurpose.get(0);
    						GenericValue telecomContact = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", telecomContactId), false);
    						if (telecomContact != null) {
    							StringBuilder telecomName = new StringBuilder();
    							telecomName.append(UtilFormatOut.ifNotEmpty(telecomContact.getString("countryCode"), "", "-"));
    							telecomName.append(UtilFormatOut.checkNull(telecomContact.getString("contactNumber")));
    							partyContactTempData.set("telecomName", telecomName.toString());
    						}
    						partyContactTempData.put("telecomId", telecomContactId);
    					}
    					// check postalAddress
    					tmpConds.clear();
    					tmpConds.add(EntityCondition.makeCondition("partyId", partyId));
    					tmpConds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
    					tmpConds.add(EntityUtil.getFilterByDateExpr());
    					List<String> ptyPostalPurpose = EntityUtil.getFieldListFromEntityList(
    							delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(tmpConds), null, sortByFields, null, false)
								, "contactMechId", true); //, EntityOperator.IN, UtilMisc.toList("PRIMARY_LOCATION", "SHIPPING_LOCATION")
    					if (UtilValidate.isNotEmpty(ptyPostalPurpose)) {
    						String postalAddressContactId = ptyPostalPurpose.get(0);
    						String postalAddressName = PartyWorker.getFullNamePostalAddress(delegator, postalAddressContactId);
    						partyContactTempData.set("postalAddressId", postalAddressContactId);
							partyContactTempData.set("postalAddressName", postalAddressName);
    					}
        				
        				delegator.create(partyContactTempData);
         			}
        		}
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling updatePartyContactTempData service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
    }
    
    @SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListFacilityByOrg(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		//Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		try {
			String orgId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			if (UtilValidate.isNotEmpty(orgId)) {
				listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", orgId));
				listIterator = delegator.find("Facility", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListFacilityByOrg service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPartyAndGroupCustomer(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
			List<EntityCondition> condsOr = FastList.newInstance();
			condsOr.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
			condsOr.add(EntityCondition.makeCondition("roleTypeId", "PARENT_MEMBER"));
			condsOr.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER_CHAIN_GROUP"));
			listAllConditions.add(EntityCondition.makeCondition(condsOr, EntityOperator.OR));
			
			listIterator = delegator.find("PartyAndRoleFullNameSimple", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListPartyAndGroupCustomer service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }

	public static Map<String, Object> jqGetDistributorOfSalesman(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			boolean isFind = false;
			OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
			if (securityOlb.olbiusHasPermission(userLogin, "VIEW_CUSTOMER_ALL", "ENTITY", "SALESORDER")) {
				isFind = true;
			} else if (securityOlb.olbiusHasPermission(userLogin, "VIEW_CUSTOMER_ROLE", "ENTITY", "SALESORDER")) {
				isFind = true;
				List<String> distributorIds = PartyWorker.getDistributorIdsBySalesmanId(delegator, userLogin.getString("partyId"));

				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, distributorIds));
			}
			listAllConditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
			//listIterator = delegator.find("PartyDistributor", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PartyDistributor", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetDistributorBySalesmanId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
}
