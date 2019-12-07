package com.olbius.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.text.SimpleDateFormat;
import com.olbius.util.SalesPartyUtil;

public class SalesPartyServices {
	public static final String RSN_PRTRELTYPE_SALESEMP = "party.relationship.type.sales.employee";
	public static final String RSN_PRTROLE_SALESMAN_GT_DL = "party.role.salesman.gt.delys";
	public static final String RSN_PRTROLE_EMPLOYEE = "party.role.employee";
	public static final String RSN_PRTRELTYPE_EMPLOYMENT = "party.relationship.type.employment";
	public static final String RSN_PRTROLE_INTERNAL_ORG = "party.role.internal.org";
	public static final String RESOURCE_DL = "delys.properties";
	public static final String RSN_PRTROLE_ROUTE_DL = "party.role.route.delys";
	public static final String module = SalesPartyServices.class.getName();
	public static final String resource = "DelysAdminUiLabels";
	public static final String resource_error = "DelysAdminErrorUiLabels";
	
	private static String coreProcessPartyNames(Map<String, Object> listParty) {
   		String listName = "";
   		boolean isFirst = true;
		for (Map.Entry<String, Object> item : listParty.entrySet()) {
			if (UtilValidate.isNotEmpty(item.getValue())) {
				if (isFirst) {
					listName += item.getValue();
				} else {
					listName += ", " +item.getValue();
				}
				isFirst = false;
			} else {
				if (isFirst) {
					listName += item.getKey();
				} else {
					listName += ", " + item.getKey();
				}
				isFirst = false;
			}
		}
		return listName;
   	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListSalesman(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator2 = null;
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	//List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	opts.setDistinct(true);
		try {
			String partyId = userLogin.getString("partyId");
			if (UtilValidate.isNotEmpty(userLogin) && UtilValidate.isNotEmpty(partyId)) {
				if (SalesPartyUtil.isSupervisorEmployee(userLogin, delegator)) {
					listIterator2 = SalesPartyUtil.getIteratorSalesmanPersonBySup(delegator, partyId, null, listAllConditions, opts);
					//listIterator2 = SalesPartyUtil.getListSalesmanActiveBySupervisor(partyId, null, null, opts, delegator);
				} else if (SalesPartyUtil.isSalesAdminManagerEmployee(userLogin, delegator) || SalesPartyUtil.isSalesAdminEmployee(userLogin, delegator)) {
					listIterator2 = SalesPartyUtil.getIteratorSalesmanPersonBySA(delegator, partyId, null, opts);
					// listIterator = SalesPartyUtil.getIteratorSupPersonBySA(delegator, partyId, listSortFields, opts);
				} else if (SalesPartyUtil.isAsmEmployee(userLogin, delegator)) {
					listIterator2 = SalesPartyUtil.getIteratorSalesmanPersonByAsm(delegator, partyId, null, listAllConditions, opts);
				} else if (SalesPartyUtil.isRsmEmployee(userLogin, delegator)) {
					listIterator2 = SalesPartyUtil.getIteratorSalesmanPersonByRsm(delegator, partyId, null, listAllConditions, opts);
				} else if (SalesPartyUtil.isCsmEmployee(userLogin, delegator)) {
					listIterator2 = SalesPartyUtil.getIteratorSalesmanPersonByCsm(delegator, partyId, null, listAllConditions, opts);
				}
			}
			if (listIterator2 != null) {
				List<GenericValue> listIterator3 = listIterator2.getCompleteList();
				listIterator2.close();
				listIterator3 = EntityUtil.filterByAnd(listIterator3, listAllConditions);
				for (GenericValue salesmanItemGv : listIterator3) {
					Map<String, Object> salesmanItemMap = salesmanItemGv.getAllFields();
					String listSupName = "";
					String listAsmName = "";
					Map<String, Object> listSupNameMap = FastMap.newInstance();
					Map<String, Object> listAsmNameMap = FastMap.newInstance();
					List<GenericValue> listSupDeptGv = SalesPartyUtil.getListSupDeptBySalesman(delegator, salesmanItemGv.getString("partyId"));
					if (listSupDeptGv != null) {
						for (GenericValue supDeptGv : listSupDeptGv) {
							List<GenericValue> result2 = SalesPartyUtil.getListManagerPersonByDept(delegator, supDeptGv.getString("partyIdTo"));
							if (result2 != null) {
								for (GenericValue item2 : result2) {
									if (!listSupNameMap.containsKey(item2.getString("partyId"))) {
										listSupNameMap.put(item2.getString("partyId"), item2.getString("fullName"));
									}
								}
							}
							
							List<GenericValue> result3 = SalesPartyUtil.getListAsmPersonBySupDept(delegator, supDeptGv.getString("partyIdTo"));
							if (result3 != null) {
								for (GenericValue item2 : result3) {
									if (!listAsmNameMap.containsKey(item2.getString("partyId"))) {
										listAsmNameMap.put(item2.getString("partyId"), item2.getString("fullName"));
									}
								}
							}
						}
					}
					if (UtilValidate.isNotEmpty(listSupNameMap)) {
						listSupName = coreProcessPartyNames(listSupNameMap);
					}
					if (UtilValidate.isNotEmpty(listAsmNameMap)) {
						listAsmName = coreProcessPartyNames(listAsmNameMap);
					}
					salesmanItemMap.put("supName", listSupName);
					salesmanItemMap.put("asmName", listAsmName);
					listIterator.add(salesmanItemMap);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesman service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetListSalesmanByDist(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
   		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	opts.setDistinct(true);
   		try {
   			/*if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId")[0])) {
				String partyId = parameters.get("partyId")[0];
				// <PartyRelationship partyIdFrom="NPP_TUANMINH" partyIdTo="salesman1" roleTypeIdFrom="DELYS_DISTRIBUTOR" roleTypeIdTo="DELYS_SALESMAN_GT" fromDate="2014-03-21 16:07:33.0" partyRelationshipTypeId="SALES_EMPLOYMENT"/>
				
				// get salesman
				List<String> listSalesmanId = EntityUtil.getFieldListFromEntityList(
						EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "DELYS_DISTRIBUTOR","roleTypeIdTo", "DELYS_SALESMAN_GT", "partyRelationshipTypeId", "SALES_EMPLOYMENT"), null, false)),
						"partyIdTo", true);
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listSalesmanId));
				EntityCondition tmpCondition = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
				listIterator = delegator.find("PartyNameView", tmpCondition, null, null, listSortFields, opts);
			}*/
   			String partyId = null;
   			if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId")[0])) {
				partyId = parameters.get("partyId")[0];
   			}
   			if (partyId != null) {
   				listIterator = SalesPartyUtil.getIteratorSalesmanPersonByDis(delegator, partyId, listSortFields, listAllConditions, opts);
   			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesmanByDist service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetListCustomerByDist(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	List<GenericValue> listIterator = null;
   		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	opts.setDistinct(true);
   		try {
   			String partyId = null;
   			if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId")[0])) {
				partyId = parameters.get("partyId")[0];
   			}
   			if (partyId != null) {
   				listIterator = SalesPartyUtil.getListCustomerByDis(delegator, partyId, listAllConditions, listSortFields, opts);
   			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCustomerByDist service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetListCustomerBySalesman(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
   		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	opts.setDistinct(true);
   		try {
   			String partyId = null;
   			if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId")[0])) {
				partyId = parameters.get("partyId")[0];
   			}
   			if (partyId != null) {
   				listIterator = SalesPartyUtil.getIteratorCustomerBySalesman(delegator, partyId, listAllConditions, listSortFields, opts);
   			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCustomerByDist service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListCustomerDirectBySup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "DAUpdatesuccess", locale));
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	opts.setDistinct(true);
       	GenericValue userLogin = (GenericValue) context.get("userLogin");
       	try {
       		String partyId = null;
   			if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId")[0])) {
				partyId = parameters.get("partyId")[0];
   			}
   			if (UtilValidate.isEmpty(partyId) && UtilValidate.isNotEmpty(userLogin)) {
   				partyId = userLogin.getString("partyId");
   			}
   			if (partyId != null) {
   				listIterator = SalesPartyUtil.getIteratorCustomerDirectBySup(delegator, partyId, listAllConditions, listSortFields, opts);
   			}
       	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCustomerDirectBySup service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
       	successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListCustomers(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	List<GenericValue> partyIds = SalesPartyUtil.getListCustomerBySup(delegator, userLogin.getString("partyId"), listAllConditions, listSortFields, opts);
    	successResult.put("listIterator", partyIds);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListCustomers2(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
//    	String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator);
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("partyIdFrom3", userLogin.getString("partyId")));
    		listIterator = delegator.find("PartyRelAndListCustomer", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListCustomerKey2 service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static Map<String, Object> jqGetListCustomersKeyByAsm(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
    		EntityCondition tmpConditon = null;
    		List<String> listSup = SalesPartyUtil.getListSupPersonIdByAsm(delegator, userLogin.getString("partyId"));
    		listAllConditions.add(EntityCondition.makeCondition("partyIdFrom4", EntityJoinOperator.IN, listSup));
    		listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "KEY_PERSON"));
    		listAllConditions.add(EntityCondition.makeCondition("relStatusId", "KEYPERRE_CREATED"));
    		tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
    		listIterator = delegator.find("PartyRelAndCK", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCustomersKeyByAsm service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> jqGetListSalesmanOfUserLogin(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
   		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	@SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	@SuppressWarnings("unchecked")
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	opts.setDistinct(true);
       	try {
			String partyId = null;
			if(UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId")[0])){
				partyId = parameters.get("partyId")[0];
			}
			if(partyId != null){
				listIterator = SalesPartyUtil.getIteratorSalesmanPersonBySup(delegator, partyId, listSortFields, listAllConditions, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesmanOfUserLogin service: " + e.toString();
			Debug.logError(e, errMsg, module);
			// TODO: handle exception
		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
	}
	public static Map<String, Object> jqGetListCustomerOfUserLogin(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	List<GenericValue> listIterator = null;
   		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	@SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	@SuppressWarnings("unchecked")
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	opts.setDistinct(true);
       	try {
			String partyId = null;
			if(UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId")[0])){
				partyId = parameters.get("partyId")[0];
			}
			if(partyId != null){
				listIterator = SalesPartyUtil.getListCustomerBySup(delegator, partyId, listAllConditions, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCustomerOfUserLogin service: " + e.toString();
			Debug.logError(e, errMsg, module);
			// TODO: handle exception
		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
	}
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetSalesmanDetails(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	opts.setDistinct(true);
    	String deptId = null;
    	String roleTypeIdFrom = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_GT_DL, delegator);
    	String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
    	String partyRelTypeIdSalesEmp = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMP, delegator);
    	try{
    		String partyIdTo = userLogin.getString("partyId");
    		List<GenericValue> listDepts = SalesPartyUtil.getListDeptByEmployee(delegator, partyIdTo);
    		List<String> deptIds = EntityUtil.getFieldListFromEntityList(listDepts, "partyIdFrom", true);
				if(deptIds.size()==1){
					deptId = deptIds.get(0);
				}
			List<EntityCondition> listConds = FastList.newInstance();
			listConds.add(EntityCondition.makeCondition("partyIdTo", deptId));
			listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
			listConds.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
			listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelTypeIdSalesEmp));
			List<GenericValue> SalesmanDetails = delegator.findList("PartyRelationship",EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
			for (GenericValue g : SalesmanDetails) {
				Map<String, Object> salesmanItemMap = FastMap.newInstance();
				salesmanItemMap.putAll(g);
				String partyIdFrom = g.getString("partyIdFrom");
				List<EntityCondition> listConds1 = FastList.newInstance();
				listConds1.add(EntityCondition.makeCondition("partyIdTo", deptId));
				listConds1.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
				listConds1.add(EntityCondition.makeCondition("roleTypeIdTo",internalOrgRole));
				listConds1.add(EntityCondition.makeCondition("partyRelationshipTypeId",partyRelTypeIdSalesEmp));
				listConds1.add(EntityCondition.makeCondition("partyId",partyIdFrom));
				List<GenericValue> SalesmanFullNames = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listConds1, EntityOperator.AND), null, null, null, false);
				if (UtilValidate.isNotEmpty(SalesmanFullNames)) {
					GenericValue SalesmanFullName = EntityUtil.getFirst(SalesmanFullNames);
					String salesmanFullName = SalesmanFullName.getString("fullName");
					salesmanItemMap.put("fullName", salesmanFullName);
				}
				listIterator.add(salesmanItemMap);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetSalesmanDetails service: " + e.toString();
			Debug.logError(e, errMsg, module);
			// TODO: handle exception
		}
		successResult.put("listIterator", listIterator);
    	return successResult;
	}
	public static Map<String, Object> jqGetListSalesManNoJob(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String roleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_GT_DL, delegator);
    	String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
    	String partyRelTypeIdSalesEmp = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMP, delegator);
    	String roleTypeId1 = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_EMPLOYEE, delegator);
    	Timestamp tmp = UtilDateTime.nowTimestamp();
    	List<String> partyIdFroms = new ArrayList<String>();
    	List<String> partyIdTos = new ArrayList<String>();
    	try {
    		List<String> listsalesmanFullName = FastList.newInstance();
     		List<EntityCondition> listConds = FastList.newInstance();
    		listConds.add(EntityCondition.makeCondition("roleTypeId",roleTypeId));
    		List<GenericValue> SalesManGts = delegator.findList("PartyRole", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
    		List<String> partyIdSaleManGts = EntityUtil.getFieldListFromEntityList(SalesManGts, "partyId", true);
    		for (String s : partyIdSaleManGts) {
    			List<EntityCondition> listConds1 = FastList.newInstance();
    			listConds1.add(EntityCondition.makeCondition("partyIdTo", s));
    			listConds1.add(EntityCondition.makeCondition("roleTypeIdTo",roleTypeId1));
    			listConds1.add(EntityCondition.makeCondition("roleTypeIdFrom",internalOrgRole));
				List<GenericValue> SalesmanPartyIdTos = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds1, EntityOperator.AND), null, null, null, false);
				boolean add = false;
				String partyIdTo = "";
				for (GenericValue x : SalesmanPartyIdTos) {
					Timestamp thruDate = x.getTimestamp("thruDate");
					partyIdTo = x.getString("partyIdTo");
					if (UtilValidate.isEmpty(thruDate)) {
						break;
					}
					if(!thruDate.before(tmp)){
						break;
					}
					if (thruDate.before(tmp)) {
						add = true;
					}
				}
				if (add) {
					partyIdTos.add(partyIdTo);
				}
				List<EntityCondition> listConds2 = FastList.newInstance();
				listConds2.add(EntityCondition.makeCondition("partyIdFrom",s));
				listConds2.add(EntityCondition.makeCondition("roleTypeIdFrom",roleTypeId));
				listConds2.add(EntityCondition.makeCondition("roleTypeIdTo",internalOrgRole));
				List<GenericValue> SalesmanPartyIdFroms = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, null, false);
				String partyIdFrom = "";
				for (GenericValue x : SalesmanPartyIdFroms) {
					Timestamp thruDate = x.getTimestamp("thruDate");
					partyIdFrom = x.getString("partyIdFrom");
					if(UtilValidate.isEmpty(thruDate)){
						break;
					}
					if(!thruDate.before(tmp)){
						break;
					}
					if(thruDate.before(tmp)){
						add=true;
					}
				}
				if(add){
					partyIdFroms.add(partyIdFrom);
				}
				
    		}
				for(String partyIdTo : partyIdTos){
						Map<String, Object> Maptmp = FastMap.newInstance();
						List<EntityCondition> listConds3 = FastList.newInstance();
						listConds3.add(EntityCondition.makeCondition("roleTypeIdFrom",roleTypeId));
						listConds3.add(EntityCondition.makeCondition("partyId", partyIdTo));
						listConds3.add(EntityCondition.makeCondition("roleTypeIdTo",internalOrgRole));
						List<GenericValue> SalesmanFullNames = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listConds3, EntityOperator.AND), null, null, null, false);
						if(UtilValidate.isNotEmpty(SalesmanFullNames)){
							GenericValue SalesmanFullName = EntityUtil.getFirst(SalesmanFullNames);
							String salesmanFullName = SalesmanFullName.getString("fullName");
							Maptmp.put("partyId", partyIdTo);
							Maptmp.put("fullName", salesmanFullName);
							listIterator.add(Maptmp);
						}
				}
				
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesManNoJob service: " + e.toString();
			Debug.logError(e, errMsg, module);
			// TODO: handle exception
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	public static Map<String, Object> jqCreateNewSalesman(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String listData = (String) context.get("listdata");
		JSONArray arr = new JSONArray();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String roleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_GT_DL, delegator);
    	String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
    	String roleTypeId1 = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_EMPLOYEE, delegator);
    	String partyRelationShipTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_EMPLOYMENT, delegator);
    	String partyRelationShipTypeId1 = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMP, delegator);
    	try {
    		String userLoginString = userLogin.getString("partyId"); 
    		if(UtilValidate.isNotEmpty(listData)){
    			arr = JSONArray.fromObject(listData);
    		}
    		if(UtilValidate.isNotEmpty(arr)){
    			for(int i=0;i<arr.size();i++){
    				Timestamp arrivalDateThruDate = new Timestamp(i);
    				JSONObject obj = arr.getJSONObject(i);
    				SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    				Date fromDate = obj.get("fromDate") != null ?  (Date)formatDate.parse((String) obj.get("fromDate")) : null;
    				Timestamp arrivalDateFromDate = new Timestamp(fromDate.getTime());
    				Date thruDate = obj.get("thruDate") != null ? (Date)formatDate.parse((String) obj.get("thruDate")) : null;
    				if(obj.get("thruDate") != null){
    					arrivalDateThruDate = new Timestamp(thruDate.getTime());
    				}
    				List<GenericValue> listDepts = SalesPartyUtil.getListDeptByEmployee(delegator, userLoginString);
    				GenericValue listDept = EntityUtil.getFirst(listDepts);
    				String DeptId = listDept.getString("partyIdFrom");
    				GenericValue SalesmanGt = delegator.makeValue("PartyRelationship");
    				if(obj.get("thruDate") != null) {
    					SalesmanGt.set("partyIdFrom", obj.get("partyId"));
        				SalesmanGt.set("partyIdTo", DeptId);
        				SalesmanGt.set("roleTypeIdFrom", roleTypeId);
        				SalesmanGt.set("roleTypeIdTo", internalOrgRole);
        				SalesmanGt.set("fromDate", arrivalDateFromDate);
        				SalesmanGt.set("thruDate", arrivalDateThruDate);
        				SalesmanGt.set("partyRelationshipTypeId", partyRelationShipTypeId1);
        				SalesmanGt.create();
    				}
    				else{
    					SalesmanGt.set("partyIdFrom", obj.get("partyId"));
        				SalesmanGt.set("partyIdTo", DeptId);
        				SalesmanGt.set("roleTypeIdFrom", roleTypeId);
        				SalesmanGt.set("roleTypeIdTo", internalOrgRole);
        				SalesmanGt.set("fromDate", arrivalDateFromDate);
        				SalesmanGt.set("thruDate", null);
        				SalesmanGt.set("partyRelationshipTypeId", partyRelationShipTypeId1);
        				SalesmanGt.create();
    				}
    				
    				GenericValue SalesmanEm = delegator.makeValue("PartyRelationship");
    				if(obj.get("thruDate") != null){
        				SalesmanEm.set("partyIdFrom", DeptId);
        				SalesmanEm.set("partyIdTo", obj.get("partyId"));
        				SalesmanEm.set("roleTypeIdFrom", internalOrgRole);
        				SalesmanEm.set("roleTypeIdTo", roleTypeId1);
        				SalesmanEm.set("fromDate", arrivalDateFromDate);
        				SalesmanEm.set("thruDate", arrivalDateThruDate);
        				SalesmanEm.set("partyRelationshipTypeId", partyRelationShipTypeId);
        				SalesmanEm.create();
    				}
    				else {
        				SalesmanEm.set("partyIdFrom", DeptId);
        				SalesmanEm.set("partyIdTo", obj.get("partyId"));
        				SalesmanEm.set("roleTypeIdFrom", internalOrgRole);
        				SalesmanEm.set("roleTypeIdTo", roleTypeId1);
        				SalesmanEm.set("fromDate", arrivalDateFromDate);
        				SalesmanEm.set("thruDate", null);
        				SalesmanEm.set("partyRelationshipTypeId", partyRelationShipTypeId);
        				SalesmanEm.create();
    				}
    			}
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqCreateNewSalesman service: " + e.toString();
			Debug.logError(e, errMsg, module);
			// TODO: handle exception
		}
    	return successResult;
	}
	public static Map<String, Object> jqGetListSalesmanToTransfer(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String roleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_GT_DL, delegator);
		String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		String partyRelationShipTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMP, delegator);
		Timestamp thruDate = UtilDateTime.nowTimestamp();
		try {
			List<EntityCondition> listConds = FastList.newInstance();
			listConds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
			listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeId));
			listConds.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
			listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationShipTypeId));
			List<GenericValue> tmpSupSales = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
			GenericValue tmpSupSale = EntityUtil.getFirst(tmpSupSales);
			String supervisor = tmpSupSale.getString("partyIdTo");
			List<EntityCondition> listConds1 = FastList.newInstance();
			listConds1.add(EntityCondition.makeCondition("partyIdTo", supervisor));
			listConds1.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeId));
			listConds1.add(EntityCondition.makeCondition("roleTypeIdTo",internalOrgRole));
			listConds1.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationShipTypeId));
			listConds1.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.NOT_EQUAL, partyId));
			List<EntityCondition> listConds2 = FastList.newInstance();
			listConds2.add(EntityCondition.makeCondition("thruDate", null));
			listConds2.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, thruDate));
			listConds1.add(EntityCondition.makeCondition(listConds2, EntityOperator.OR));
			List<GenericValue> listSalesmanToTransfers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds1, EntityOperator.AND), null, null, null, false);
			for (GenericValue g : listSalesmanToTransfers) {
				Map<String, Object> SalesmanToTransfer = FastMap.newInstance();
				String partyIdFrom = g.getString("partyIdFrom");
				List<GenericValue> tmpSalesmans = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom), null, null, null, false);
				GenericValue tmpSaleman = EntityUtil.getFirst(tmpSalesmans);
				String SalemantoTransfer = tmpSaleman.getString("fullName");
				SalesmanToTransfer.put("fullName", SalemantoTransfer);
				SalesmanToTransfer.put("partyId", partyIdFrom);
				listIterator.add(SalesmanToTransfer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String, Object> jqRouteTransfer(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String routeId = (String) context.get("routeId");
		String partyIdTransfer = (String) context.get("partyIdTransfer");
		String partyIdToTransfer = (String) context.get("partyIdToTransfer");
		String roleTypeIdTo = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ROUTE_DL, delegator);
		String roleTypeIdFrom = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_GT_DL, delegator);
		Timestamp now = UtilDateTime.nowTimestamp();
		try {
			List<EntityCondition> listConds = FastList.newInstance();
			listConds.add(EntityCondition.makeCondition("partyIdFrom", partyIdTransfer));
			listConds.add(EntityCondition.makeCondition("partyIdTo", routeId));
			listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
			listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
			List<EntityCondition> listConds1 = FastList.newInstance();
			listConds1.add(EntityCondition.makeCondition("thruDate", null));
			listConds1.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, now));
			listConds.add(EntityCondition.makeCondition(listConds1, EntityOperator.OR));
			List<GenericValue> listSalesmanTransferRoute = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
			GenericValue SalesmanTransferRoute = EntityUtil.getFirst(listSalesmanTransferRoute);
			SalesmanTransferRoute.set("thruDate", now);
			SalesmanTransferRoute.store();
			
			List<EntityCondition> listConds2 = FastList.newInstance();
			listConds2.add(EntityCondition.makeCondition("partyIdFrom", partyIdToTransfer));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
			List<EntityCondition> listConds3 = FastList.newInstance();
			listConds3.add(EntityCondition.makeCondition("thruDate", null));
			listConds3.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, now));
			listConds2.add(EntityCondition.makeCondition(listConds3, EntityOperator.OR));
			List<GenericValue> RouteChecks = delegator.findList("RouteDetail", EntityCondition.makeCondition("routeId", routeId), null, null, null, false);
			List<GenericValue> checks = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, null, false);
			for (GenericValue g : checks) {
				List<GenericValue> RouteExists = delegator.findList("RouteDetail", EntityCondition.makeCondition("routeId",g.getString("partyIdTo")), null, null, null, false);
				for (GenericValue RouteExist : RouteExists) {
					for (GenericValue RouteCheck : RouteChecks) {
						if(RouteCheck.getString("scheduleRoute").equals(RouteExist.getString("scheduleRoute"))){
							return ServiceUtil.returnError(UtilProperties.getMessage("DelysAdminUiLabels", "DASchedulingConflicts", locale));
						}
					}
				}
			}
			
			GenericValue SalesmanToTransfer = delegator.makeValue("PartyRelationship");
			SalesmanToTransfer.set("partyIdFrom", partyIdToTransfer);
			SalesmanToTransfer.set("partyIdTo", routeId);
			SalesmanToTransfer.set("roleTypeIdFrom", roleTypeIdFrom);
			SalesmanToTransfer.set("roleTypeIdTo", roleTypeIdTo);
			SalesmanToTransfer.set("fromDate", now);
			SalesmanToTransfer.set("thruDate", null);
			SalesmanToTransfer.set("partyRelationshipTypeId", "GROUP_ROLLUP");
			SalesmanToTransfer.create();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		return successResult;
	}
	public static Map<String, Object> jqGetListSupervisorsBySA(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		@SuppressWarnings({ "unchecked", "unused" })
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		@SuppressWarnings({ "unused", "unchecked" })
		List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	GenericValue userLogin = (GenericValue) context.get("userLogin");
       	opts.setDistinct(true);
       	try {
       		if(UtilValidate.isNotEmpty(userLogin)){
       			String partyIdSA = userLogin.getString("partyId");
       			listIterator = SalesPartyUtil.getIteratorSupPersonBySA(delegator, partyIdSA, null, opts);
       		}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
       	successResult.put("listIterator", listIterator);
       	return successResult;
	}
	public static Map<String, Object> jqGetSalesmanOfSup(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		opts.setDistinct(true);
		String roleTypeIdTo = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		String partyRelationshipTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMP, delegator);
		Timestamp now = UtilDateTime.nowTimestamp();
		try {
			String partyId = null;
			if(UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId")[0])){
				partyId = parameters.get("partyId")[0];
				List<EntityCondition> listConds = FastList.newInstance();
				listConds.add(EntityCondition.makeCondition("partyId", partyId));
				listConds.add(EntityCondition.makeCondition("roleTypeIdFrom","MANAGER"));
				listConds.add(EntityCondition.makeCondition("roleTypeIdTo",roleTypeIdTo));
				listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
				List<GenericValue> GroupOfManagers = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, opts, false);
				GenericValue GroupOfManager = EntityUtil.getFirst(GroupOfManagers);
				String SupGroupId = GroupOfManager.getString("partyIdTo");
				if(UtilValidate.isNotEmpty(SupGroupId)){
					List<EntityCondition> listConds1 = FastList.newInstance();
					listConds1.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL,partyId));
					listConds1.add(EntityCondition.makeCondition("partyIdFrom", SupGroupId));
					listConds1.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdTo));
					listConds1.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
					listConds1.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
					List<EntityCondition> listConds2 = FastList.newInstance();
					listConds2.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, now));
					listConds2.add(EntityCondition.makeCondition("thruDate", null));
					listConds1.add(EntityCondition.makeCondition(listConds2, EntityOperator.OR));
					List<GenericValue> listSalesmanOfSup = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds1, EntityOperator.AND), null, null, opts, false);
					for (GenericValue g : listSalesmanOfSup) {
						Map<String, Object> Tmp = FastMap.newInstance();
						String partyIdSalesman = g.getString("partyIdTo");
						Timestamp fromDate = g.getTimestamp("fromDate");
						Timestamp thruDate = g.getTimestamp("thruDate");
						List<EntityCondition> listConds3 = FastList.newInstance();
						listConds3.add(EntityCondition.makeCondition("partyRelationshipTypeId", "SALES_EMPLOYEE"));
						listConds3.add(EntityCondition.makeCondition("partyId", partyIdSalesman));
						List<GenericValue> ListSalesManOfSup = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listConds3, EntityOperator.AND), null, null, opts, false);
						GenericValue SalesManOfSup = EntityUtil.getFirst(ListSalesManOfSup);
						String SalesmanName = SalesManOfSup.getString("fullName");
						Date SalesmanDate = SalesManOfSup.getDate("birthDate");
						Tmp.put("fullName", SalesmanName);
						Tmp.put("partyId", partyIdSalesman);
						Tmp.put("fromDate", fromDate);
						Tmp.put("thruDate", thruDate);
						Tmp.put("birthDate", SalesmanDate);
						listIterator.add(Tmp);
					} 
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		successResult.put("listIterator", listIterator);
		return successResult;
		
	}
	public static Map<String, Object> jqGetSupToTransfer(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		opts.setDistinct(true);
		String roleTypeIdTo = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		try {
			String partyId = null;
			EntityListIterator listIterator1 = null;
			if(UtilValidate.isNotEmpty(userLogin)){
				partyId = userLogin.getString("partyId");
				listIterator1 = SalesPartyUtil.getIteratorSupPersonBySA(delegator, partyId, null, opts);
				List<GenericValue> listIterator2 = listIterator1.getCompleteList();
				for (GenericValue g : listIterator2) {
					Map<String, Object> Tmp = FastMap.newInstance();
					String ManagerId = g.getString("partyId");
					String ManagerName = g.getString("fullName");
					List<EntityCondition> listConds = FastList.newInstance();
					listConds.add(EntityCondition.makeCondition("partyId",g.getString("partyId")));
					listConds.add(EntityCondition.makeCondition("roleTypeIdFrom","MANAGER"));
					listConds.add(EntityCondition.makeCondition("roleTypeIdTo",roleTypeIdTo));
					listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
					List<GenericValue> SupGroupIds = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, opts, false);
					GenericValue SupGroupId = EntityUtil.getFirst(SupGroupIds);
					String SupId = SupGroupId.getString("partyIdTo");
					Tmp.put("SupGroupId", SupId);
					Tmp.put("ManagerId", ManagerId);
					Tmp.put("ManagerName", ManagerName);
					listIterator.add(Tmp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String, Object> RepositionSalesman(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String data = (String) context.get("data");
		String roleTypeIdTo = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		JSONArray arr = new JSONArray();
		try {
			if(UtilValidate.isNotEmpty(data)){
				arr = JSONArray.fromObject(data);
			}
			if(UtilValidate.isNotEmpty(arr)){
				for(int i=0;i<arr.size();i++){
					JSONObject obj = arr.getJSONObject(i);
					String SalesmanId = obj.getString("SalesmanId");
					String ManagerId = obj.getString("ManagerId");
					String SupGroupIdToTransfer = obj.getString("SupGroupIdToTransfer");
					List<EntityCondition> listConds = FastList.newInstance();
					listConds.add(EntityCondition.makeCondition("partyId",ManagerId));
					listConds.add(EntityCondition.makeCondition("roleTypeIdFrom","MANAGER"));
					listConds.add(EntityCondition.makeCondition("roleTypeIdTo",roleTypeIdTo));
					listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
					List<GenericValue> SupGroupIds = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
					GenericValue SupGroupId = EntityUtil.getFirst(SupGroupIds);
					String supGroupId = SupGroupId.getString("partyIdTo");
					Timestamp now =UtilDateTime.nowTimestamp();
					
					List<EntityCondition> listConds1 = FastList.newInstance();
					listConds1.add(EntityCondition.makeCondition("partyIdFrom", SalesmanId));
					listConds1.add(EntityCondition.makeCondition("partyIdTo",supGroupId));
					listConds1.add(EntityCondition.makeCondition("roleTypeIdFrom","DELYS_SALESMAN_GT"));
					listConds1.add(EntityCondition.makeCondition("roleTypeIdTo",roleTypeIdTo));
					listConds1.add(EntityCondition.makeCondition("partyRelationshipTypeId","SALES_EMPLOYEE"));
					List<GenericValue> SalesmanOfSupToRemoves1 = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds1, EntityOperator.AND), null, null, null, false);
					GenericValue SalesmanOfSupToRemove1 = EntityUtil.getFirst(SalesmanOfSupToRemoves1);
					SalesmanOfSupToRemove1.set("thruDate", now);
					SalesmanOfSupToRemove1.store();
					
					List<EntityCondition> listConds2 = FastList.newInstance();
					listConds2.add(EntityCondition.makeCondition("partyIdFrom",supGroupId));
					listConds2.add(EntityCondition.makeCondition("partyIdTo",SalesmanId));
					listConds2.add(EntityCondition.makeCondition("roleTypeIdFrom",roleTypeIdTo));
					listConds2.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));	
					listConds2.add(EntityCondition.makeCondition("partyRelationshipTypeId","EMPLOYMENT"));
					List<GenericValue> SalesmanOfSupToRemoves2 = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, null, false);
					GenericValue SalesmanOfSupToRemove2 = EntityUtil.getFirst(SalesmanOfSupToRemoves2);
					SalesmanOfSupToRemove2.set("thruDate",now);
					SalesmanOfSupToRemove2.store();
				
					GenericValue SalesmanOfSupToTransfer1 = delegator.makeValue("PartyRelationship");
					SalesmanOfSupToTransfer1.set("partyIdFrom", SalesmanId);
					SalesmanOfSupToTransfer1.set("partyIdTo", SupGroupIdToTransfer);
					SalesmanOfSupToTransfer1.set("roleTypeIdFrom", "DELYS_SALESMAN_GT");
					SalesmanOfSupToTransfer1.set("roleTypeIdTo", roleTypeIdTo);
					SalesmanOfSupToTransfer1.set("partyRelationshipTypeId","SALES_EMPLOYEE");
					SalesmanOfSupToTransfer1.set("fromDate",now);
					SalesmanOfSupToTransfer1.set("thruDate", null);
					SalesmanOfSupToTransfer1.create();
					
					GenericValue SalesmanOfSupToTransfer2 = delegator.makeValue("PartyRelationship");
					SalesmanOfSupToTransfer2.set("partyIdFrom", SupGroupIdToTransfer);
					SalesmanOfSupToTransfer2.set("partyIdTo", SalesmanId);
					SalesmanOfSupToTransfer2.set("roleTypeIdFrom", roleTypeIdTo);
					SalesmanOfSupToTransfer2.set("roleTypeIdTo", "EMPLOYEE");
					SalesmanOfSupToTransfer2.set("partyRelationshipTypeId", "EMPLOYMENT");
					SalesmanOfSupToTransfer2.set("fromDate", now);
					SalesmanOfSupToTransfer2.set("thruDate", null);
					SalesmanOfSupToTransfer2.create();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		return successResult;
	}
	public static Map<String, Object> jqGetListProduct(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listIterator = new ArrayList<GenericValue>();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		opts.setDistinct(true);
		List<String> listProductStr = new ArrayList<String>();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String partyId = null;
			if(UtilValidate.isNotEmpty(userLogin)){
				partyId = userLogin.getString("partyId");
				List<GenericValue> listProductStoreOwner = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "OWNER"), null, false);
				List<GenericValue> listProductStoreCatalog = new ArrayList<GenericValue>();
				List<GenericValue> listProductStoreCategoryProduct = new ArrayList<GenericValue>();
				if (listProductStoreOwner != null) {
					for (GenericValue storeItem : listProductStoreOwner) {
						List<GenericValue> listCatalogTemp = delegator.findByAnd("ProductStoreCatalog", UtilMisc.toMap("productStoreId", storeItem.getString("productStoreId")), null, false);
						if (listCatalogTemp != null) {
							listProductStoreCatalog.addAll(listCatalogTemp);
						}
					}
					for (GenericValue catalogItem : listProductStoreCatalog) {
						List<GenericValue> listCategoryProductTemp = delegator.findByAnd("ProdCatalogCategoryAndProduct", UtilMisc.toMap("prodCatalogId", catalogItem.getString("prodCatalogId")), null, false);
						listProductStoreCategoryProduct.addAll(listCategoryProductTemp);
					}
					for (GenericValue categoryProductItem : listProductStoreCategoryProduct) {
						if (!listProductStr.contains(categoryProductItem.getString("productId"))) {
							listProductStr.add(categoryProductItem.getString("productId"));
						}
					}
					listAllConditions.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductStr));
					listIterator = delegator.findList("Product", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, opts, false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String, Object> jqGetListProductStoreAndDetail(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listIterator = new ArrayList<GenericValue>();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		opts.setDistinct(true);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String partyId = null;
			if(UtilValidate.isNotEmpty(userLogin)){
				partyId = userLogin.getString("partyId");
				listAllConditions.add(EntityCondition.makeCondition("partyId",partyId));
				listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "OWNER"));
				listIterator = delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String, Object> jqGetOwnerStoreInformation(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		opts.setDistinct(true);
		try {
			String partyId = null;
			if(parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId"))){
				partyId = parameters.get("partyId")[0];
			}
			List<EntityCondition> listConds = FastList.newInstance();
			listConds.add(EntityCondition.makeCondition("partyIdTo",partyId));
			listConds.add(EntityCondition.makeCondition("roleTypeIdFrom","OWNER"));
			listConds.add(EntityCondition.makeCondition("roleTypeIdTo","DELYS_CUSTOMER_GT"));
			listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "OWNER"));
			List<GenericValue> listStoreName = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, opts, false);
			GenericValue StoreName = EntityUtil.getFirst(listStoreName);
			
			Map<String, Object> tmpMap = FastMap.newInstance();
			String OwnerStoreId = StoreName.getString("partyIdFrom");
			List<EntityCondition> listConds1 = FastList.newInstance();
			listConds1.add(EntityCondition.makeCondition("partyId",OwnerStoreId));
			listConds1.add(EntityCondition.makeCondition("partyIdTo",partyId));
			listConds1.add(EntityCondition.makeCondition("partyRelationshipTypeId","OWNER"));
			List<GenericValue> listIterator1 = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listConds1, EntityOperator.AND), null, listSortFields, opts, false);
			GenericValue Iterator1 = EntityUtil.getFirst(listIterator1);
			GenericValue Iterator2 = delegator.findOne("Person", UtilMisc.toMap("partyId", OwnerStoreId), false);
			tmpMap.put("partyId", OwnerStoreId);
			tmpMap.put("fullName", Iterator1.getString("fullName"));
			tmpMap.put("birthDate", Iterator2.getDate("birthDate"));
			tmpMap.put("fromDate", Iterator1.getTimestamp("fromDate"));
			listIterator.add(tmpMap);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String,Object> jqGetListStoreParty(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		opts.setDistinct(true);
		try {
			listIterator = delegator.find("PartyNameAllView", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts); 
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
			// TODO: handle exception
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
}
