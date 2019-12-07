package com.olbius.salesmtl;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.olbius.service.annotations.Validate;

import com.sun.org.apache.bcel.internal.generic.CodeExceptionGen;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.administration.util.CrabEntity;
import com.olbius.administration.util.UniqueUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.util.CRMUtils;
import com.olbius.basesales.util.NotificationUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.crm.CallcenterServices;
import com.olbius.salesmtl.util.MTLUtil;
import com.olbius.salesmtl.util.SupUtil;
import com.olbius.salesmtl.util.ExceptionExtend.HasContractException;
import com.olbius.salesmtl.util.RouteUtils;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SupervisorServices {
	public static final String module = SupervisorServices.class.getName();
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
	public static final String resource = "BaseSalesUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels";
	
	public static Map<String, Object> getListSalesmanOfSupervisor(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		String supervisorId = (String)context.get("supervisorId");
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = (String)userLogin.getString("userLoginId");
		Debug.log(module + "::getListSalesmanOfSupervisor, userLoginId = " + userLoginId + ", supervisorId = " + supervisorId);
		try{
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyCode", EntityOperator.EQUALS,userLoginId));
			
			if(supervisorId == null){
				
				List<GenericValue> u = delegator.findList("Party", 
						EntityCondition.makeCondition(conds),null,null,null,false);
				if(u != null && u.size() > 0)
					supervisorId = u.get(0).getString("partyId");
			}
			
			conds.clear();
			conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.EQUALS,supervisorId));
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"PARTY_ENABLED"));
			
			List<GenericValue> lst = delegator.findList("PartySalesman", 
					EntityCondition.makeCondition(conds), null,null,null, false);
			
			retSucc.put("listSalesman", lst);
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}
	
	public static Map<String, Object> getListSalesmanManagedByUserLogin(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = (String)userLogin.getString("userLoginId");
		String partyId = (String)userLogin.getString("partyId");
		
		Debug.log(module + "::getListSalesmanManagedByUserLogin, userLoginId = " + userLoginId + ", partyId = " + partyId);
	
		try{
			
			List<EntityCondition> conds = FastList.newInstance();
				
			if(SalesPartyUtil.isSalesAdmin(delegator, partyId)){
				Debug.log(module + "::getListSalesmanManagedByUserLogin, login " + partyId + " is sales-admin");
				
				List<EntityCondition> tconds = FastList.newInstance();
				tconds.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SELLER"));
				tconds.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
				List<GenericValue> lstProducStore = delegator.findList("ProductStoreRole", 
						EntityCondition.makeCondition(tconds), null,null,null,false);
				List<String> pdsId = FastList.newInstance();
				for(GenericValue p: lstProducStore)
					pdsId.add(p.getString("productStoreId"));
				
				tconds.clear();
				tconds.add(EntityCondition.makeCondition("productStoreId",EntityOperator.IN,pdsId));
				tconds.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"CUSTOMER"));
				
				List<GenericValue> lstDistributor = delegator.findList("ProductStoreRole", 
						EntityCondition.makeCondition(tconds), null,null,null,false);
				List<String> disId = FastList.newInstance();
				for(GenericValue d: lstDistributor){
					disId.add(d.getString("partyId"));
					Debug.log(module + "::getListSalesmanManagedByUserLogin, add distributor " + d.getString("partyId"));
				}
				
				
				conds.add(EntityCondition.makeCondition("distributorId",EntityOperator.IN,disId));
				
			}else if(SalesPartyUtil.isSalessup(delegator, partyId)){
				Debug.log(module + "::getListSalesmanManagedByUserLogin, login " + partyId + " is sales-sup");
				conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.EQUALS,partyId));
			}else if(SalesPartyUtil.isSalesman(delegator, partyId)){
				Debug.log(module + "::getListSalesmanManagedByUserLogin, login " + partyId + " is salesman");
				conds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
			}else{
				// take all?
				Debug.log(module + "::getListSalesmanManagedByUserLogin, login " + partyId + " take all");
			}
			//conds.clear();
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"PARTY_ENABLED"));
			
			List<GenericValue> lst = delegator.findList("PartySalesman", 
					EntityCondition.makeCondition(conds), null,null,null, false);
			
			Debug.log(module + "::getListSalesmanManagedByUserLogin, login " + partyId + ", result lst = " + lst.size());
			retSucc.put("listSalesman", lst);
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}
	
	/*@SuppressWarnings("unused")
	public static Map<String, Object> listCustomTimePeriod(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	List<GenericValue> listCustomTimePeriod = FastList.newInstance();
    	try {
    		List<EntityCondition> listAllConditions = FastList.newInstance();
        	String periodTypeId = (String) context.get("periodTypeId");
        	String parentPeriodId = (String) context.get("parentPeriodId");
        	String customTimePeriodId = (String) context.get("customTimePeriodId");
        	String periodTypeModule = (String) context.get("periodTypeModule"); // modules as: sales, accountant, ... default is search all
        	Set<String> listSortFields = FastSet.newInstance();
        	listSortFields.add("customTimePeriodId");
        	listSortFields.add("parentPeriodId");
        	listSortFields.add("periodName");
        	listSortFields.add("isClosed");
    		boolean isSearch = true;
    		if (UtilValidate.isNotEmpty(parentPeriodId)) {
    			if ("nullField".equals(parentPeriodId)) {
    				listAllConditions.add(EntityCondition.makeCondition("parentPeriodId", null));
    			} else {
    				listAllConditions.add(EntityCondition.makeCondition("parentPeriodId", parentPeriodId));
    			}
			}
    		if (UtilValidate.isNotEmpty(periodTypeModule)) {
				if ("sales".equals(periodTypeModule)) {
					List<String> listPeriodTypeId = SalesmtlUtil.getListCustomTimePeriodSalesInProperties(delegator);
					if (UtilValidate.isNotEmpty(periodTypeId)) {
						if (listPeriodTypeId.contains(periodTypeId)) {
							listAllConditions.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
						} else {
							isSearch = false;
						}
					} else {
						listAllConditions.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, listPeriodTypeId));
					}
				}
			}
    		if (isSearch) {
    			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions);
    			listCustomTimePeriod = delegator.findList("CustomTimePeriod",
    					tmpConditon, listSortFields, UtilMisc.toList("fromDate"), null, false);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listCustomTimePeriod", listCustomTimePeriod);
    	return result;
    }*/
	
	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> listCustomerKey(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	EntityListIterator listIterator = null;
    	try {
    		GenericValue userLogin = (GenericValue)context.get("userLogin");
    		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        	List<String> listSortFields = (List<String>) context.get("listSortFields");
        	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        	String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		listAllConditions.add(EntityCondition.makeCondition("partyIdFrom4", userLogin.getString("partyId")));
    		listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", companyId));
    		listAllConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("roleTypeIdTo"), EntityOperator.LIKE, EntityFunction.UPPER("CUSTOMER_GT")));
    		listAllConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("roleTypeIdFrom"), EntityOperator.LIKE, EntityFunction.UPPER("SUPPLIER")));
    		listAllConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyRelationshipTypeId"), EntityOperator.LIKE, EntityFunction.UPPER("KEY_PERSON")));
    		listIterator = delegator.find("PartyRelAndCK",
    				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
    	return result;
    }*/
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCustomers(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	EntityListIterator listIterator = null;
    	try {
    		GenericValue userLogin = (GenericValue)context.get("userLogin");
    		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        	List<String> listSortFields = (List<String>) context.get("listSortFields");
        	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    		listAllConditions.add(EntityCondition.makeCondition("partyIdFrom3", userLogin.getString("partyId")));
    		listIterator = delegator.find("PartyRelAndListCustomer",
    				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
    	return result;
    }
	
	// TODOCHANGE delete
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCustomerKeyItem(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	try {
    		GenericValue userLogin = (GenericValue)context.get("userLogin");
        	HttpServletRequest request = (HttpServletRequest)context.get("request");
        	String cTP = request.getParameter("customTimePeriodId");
        	GenericValue customTP = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", cTP), false);
        	Date fromDateCTP = customTP.getDate("fromDate");
        	Date thruDateCTP = customTP.getDate("thruDate");
        	
    		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        	List<String> listSortFields = (List<String>) context.get("listSortFields");
        	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        	String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		listAllConditions.add(EntityCondition.makeCondition("partyIdFrom4", userLogin.getString("partyId")));
    		listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", companyId));
    		listAllConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("roleTypeIdTo"), EntityOperator.LIKE, EntityFunction.UPPER("CUSTOMER_GT")));
    		listAllConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("roleTypeIdFrom"), EntityOperator.LIKE, EntityFunction.UPPER("SUPPLIER")));
    		listAllConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyRelationshipTypeId"), EntityOperator.LIKE, EntityFunction.UPPER("KEY_PERSON")));
    		listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN, new Timestamp(fromDateCTP.getTime())));
    		listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, new Timestamp(thruDateCTP.getTime())));
    		listIterator = delegator.find("PartyRelAndCK",
    				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
    	return result;
    }
	
	// TODOCHANGE delete
	public static Map<String, Object> createCustomerKeyss(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String listCustomerKeys = (String)context.get("listCustomerKeys");
			JSONArray listCustomerKeysJson = JSONArray.fromObject(listCustomerKeys);
			List<String> listEr =  new ArrayList<String>();
			String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			for (int i = 0; i < listCustomerKeysJson.size(); i++) {
				JSONObject item = listCustomerKeysJson.getJSONObject(i);
					Timestamp thruDateCkeck = new Timestamp(Long.parseLong(item.getString("thruDate")));
					Timestamp fromDateCheck = new Timestamp(Long.parseLong(item.getString("fromDate")));
					List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
					conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDateCheck));
					conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, thruDateCkeck));
					conditionList.add(EntityCondition.makeCondition("partyIdFrom", companyId));
					conditionList.add(EntityCondition.makeCondition("partyIdTo", item.getString("partyIdTo")));
					conditionList.add(EntityCondition.makeCondition("roleTypeIdFrom", "SUPPLIER"));
					conditionList.add(EntityCondition.makeCondition("roleTypeIdTo", "CUSTOMER_GT"));
					conditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "KEY_PERSON"));
					List<GenericValue> listCheck = delegator.findList("PartyRelationship",
							EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
					if(UtilValidate.isEmpty(listCheck)){
						GenericValue customerkey = delegator.makeValue("PartyRelationship");
						customerkey.put("partyIdTo", item.getString("partyIdTo"));
						customerkey.put("partyIdFrom", companyId);
						customerkey.put("statusId", item.getString("relStatusId"));
						customerkey.put("roleTypeIdFrom",item.getString("roleTypeIdFrom"));
						customerkey.put("roleTypeIdTo", item.getString("roleTypeIdTo"));
						customerkey.put("partyRelationshipTypeId", "KEY_PERSON");
						customerkey.put("fromDate", fromDateCheck);
						customerkey.put("thruDate", thruDateCkeck);
						delegator.create(customerkey);
					}else{
						listEr.add(item.getString("partyIdTo"));
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> listAgents(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator dummy = null;
		List<Map<String, Object>> agents = FastList.newInstance();
		String TotalRows = "0";
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize + 1;
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			opts.setDistinct(true);
			List<EntityCondition> conditions = FastList.newInstance();
			if (parameters.containsKey("sD")) {
				if ("N".equals(parameters.get("sD")[0])) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
				}
			}
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			
			if (SalesPartyUtil.isSalesManager(delegator, userLogin.getString("partyId"))) {
				// new code
				String partyIdFrom = SalesUtil.getParameter(parameters, "partyIdFrom");
				if (UtilValidate.isNotEmpty(partyIdFrom)) {
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap(
							"partyTypeId", "RETAIL_OUTLET", "roleTypeIdFrom", "CUSTOMER", "partyRelationshipTypeId", "CUSTOMER_REL")));
					conditions.add(EntityCondition.makeCondition("partyIdTo", partyIdFrom));
				} else {
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyTypeId", "RETAIL_OUTLET", 
							"partyIdTo", organizationId, "roleTypeIdTo", "INTERNAL_ORGANIZATIO",
							"roleTypeIdFrom", "CUSTOMER", "partyRelationshipTypeId", "CUSTOMER_REL")));
				}
			} else {
				conditions.add(EntityCondition
						.makeCondition(UtilMisc.toMap("partyTypeId", "RETAIL_OUTLET", "partyIdTo", organizationId, "roleTypeIdTo", "INTERNAL_ORGANIZATIO",
								"roleTypeIdFrom", "CUSTOMER", "partyRelationshipTypeId", "CUSTOMER_REL")));
				if (MTLUtil.hasSecurityGroupPermission(delegator, "DISTRIBUTOR_ADMIN", userLogin, false)) {
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentsOfDistributor(delegator, userLogin.get("partyId"))));
				} else  if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false)) {
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentOfSalesman(delegator, userLogin)));
				} else if (!MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, true) || !MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, true)) {
					conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentOfSupervisor(delegator, userLogin)));
				}
			}
			
			conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));
			dummy = delegator.find("PartyFromAndNameRelOutletDetail", EntityCondition.makeCondition(conditions), null, null, listSortFields, opts);
			TotalRows = String.valueOf(dummy.getResultsTotalSize());
			List<GenericValue> parties = null;
			if (pagesize != 0) {
				parties = dummy.getPartialList(start, pagesize);
			} else {
				parties = dummy.getCompleteList();
			}
			if (parties != null) {
				for (GenericValue x : parties) {
					Map<String, Object> party = FastMap.newInstance();
					party.put("partyId", x.get("partyId"));
					party.put("partyCode", x.get("partyCode"));
					party.put("statusId", x.get("statusId"));
					party.put("groupName", x.get("groupName"));
					party.put("preferredCurrencyUomId", x.get("preferredCurrencyUomId"));
					party.put("officeSiteName", x.get("officeSiteName"));
					
					party.put("postalAddressName", x.get("postalAddressName"));
					party.put("telecomNumber", x.get("telecomNumber"));
					
					Map<String, Object> getPartyInfo = dispatcher.runSync("getPartyInformation",
							UtilMisc.toMap("partyId", x.get("partyId"), "userLogin", userLogin));
					Map<String, Object> partyInfo = (Map<String, Object>) getPartyInfo.get("partyInfo");
					party.put("contactNumber", (String) partyInfo.get("contactNumber"));
					party.put("emailAddress", (String) partyInfo.get("emailAddress"));
					List<Map<String, Object>> listAddress = (List<Map<String, Object>>) partyInfo.get("listAddress");
					if (UtilValidate.isNotEmpty(listAddress)) {
						for (Map<String, Object> m : listAddress) {
							if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {	
								party.put("address1", (String) m.get("address1"));
								break;
							}
						}
					}
					party.putAll(getDistributor(delegator, x.get("partyId")));
					party.putAll(getSalesman(delegator, x.get("partyId")));
					agents.add(party);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dummy != null) {
				dummy.close();
			}
		}
		result.put("listIterator", agents);
		result.put("TotalRows", TotalRows);
		return result;
	}*/
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listAgents(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
            result = SupervisorWorkers.getListAgentsInner(ctx.getDispatcher(), delegator, userLogin, parameters, listAllConditions, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling listAgents service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return result;
	}

	public static Map<String, Object> getListPartyCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginPartyId = userLogin.getString("partyId");
		Boolean isSearch = true;
		List<EntityCondition> conds = FastList.newInstance();
		if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
			conds.add(EntityCondition.makeCondition("supervisorId", userLoginPartyId));
		} else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
			List<String> listSupIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
			if (UtilValidate.isEmpty(listSupIds)) {
				isSearch = false;
			} else if (listSupIds.size() == 1) {
				conds.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
			} else {
				conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
			}
		} else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
			List<String> listSupIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
			if (UtilValidate.isEmpty(listSupIds)) {
				isSearch = false;
			} else if (listSupIds.size() == 1) {
				conds.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
			} else {
				conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
			}
		} else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
			List<String> listSupIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
			if (UtilValidate.isEmpty(listSupIds)) {
				isSearch = false;
			} else if (listSupIds.size() == 1) {
				conds.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
			} else {
				conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
			}
		} else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
			//nothing
			isSearch = false;
		}else{
			isSearch = false;
		}

		if(isSearch){
			listAllConditions.add(EntityCondition.makeCondition(conds, EntityOperator.AND));
		}
		String routeId = SalesUtil.getParameter(parameters, "routeId");
		listAllConditions.add(EntityCondition.makeCondition("routeId", routeId));
		try {
			List<GenericValue> listPartyCustomer = delegator.findList("RouteAndPartyCustomerView", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
			result.put("listIterator", listPartyCustomer);
		} catch (Exception e) {
			String errMsg = "Fatal error calling list party customer service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return result;
	}
	
	public static EntityListIterator getListAgentsInner(Delegator delegator, GenericValue userLogin, Map<String, String[]> parameters, 
			List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) throws GenericEntityException {
		String userLoginPartyId = userLogin.getString("partyId");
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		opts.setDistinct(true);
		
		
		if (parameters.containsKey("sD")) {
			if ("N".equals(parameters.get("sD")[0])) {
				listAllConditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
			}
		}
		listAllConditions.add(EntityUtil.getFilterByDateExpr());
		
		if (SalesPartyUtil.isSalesManager(delegator, userLogin.getString("partyId"))
                || SalesPartyUtil.isSalesAdmin(delegator, userLogin.getString("partyId"))
                || SalesPartyUtil.isSalesAdminManager(delegator, userLogin.getString("partyId"))) {
			// new code
			String partyIdFrom = SalesUtil.getParameter(parameters, "partyIdFrom");
			if (UtilValidate.isNotEmpty(partyIdFrom)) {
				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap(
						"partyTypeId", "RETAIL_OUTLET", 
						"roleTypeIdFrom", "CUSTOMER", 
						"partyRelationshipTypeId", "CUSTOMER_REL")));
				listAllConditions.add(EntityCondition.makeCondition("partyIdTo", partyIdFrom));
			} else {
				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap(
						"partyTypeId", "RETAIL_OUTLET", 
						"partyIdTo", organizationId, 
						"roleTypeIdTo", "INTERNAL_ORGANIZATIO",
						"roleTypeIdFrom", "CUSTOMER", 
						"partyRelationshipTypeId", "CUSTOMER_REL")));
			}
		} else {
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap(
					"partyTypeId", "RETAIL_OUTLET", 
					"partyIdTo", organizationId, 
					"roleTypeIdTo", "INTERNAL_ORGANIZATIO",
					"roleTypeIdFrom", "CUSTOMER", 
					"partyRelationshipTypeId", "CUSTOMER_REL")));
			if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
				listAllConditions.add(EntityCondition.makeCondition("distributorId", EntityJoinOperator.IN, PartyWorker.getDistributorIdsBySup(delegator, userLoginPartyId)));
			} else if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
				listAllConditions.add(EntityCondition.makeCondition("salesmanId", userLoginPartyId));
			} else if (SalesPartyUtil.isDistributor(delegator, userLoginPartyId)) {
				listAllConditions.add(EntityCondition.makeCondition("distributorId", userLoginPartyId));
			}
		}
		EntityListIterator listIterator = delegator.find("PartyFromAndNameRelOutletDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		return listIterator;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListOutletByRouter(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		EntityListIterator listIterator = null;
		try {
			opts.setDistinct(true);
			
			String routerId = SalesUtil.getParameter(parameters, "routerId");
			if (UtilValidate.isNotEmpty(routerId)) {
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
                listAllConditions.add(EntityCondition.makeCondition("routeId",routerId));
				listIterator = EntityMiscUtil.processIterator(parameters,result,delegator,"RouteCustomerAndPartyCustomer",
                        EntityCondition.makeCondition(listAllConditions),null,null,listSortFields,opts);
				//listIterator = delegator.find("PartyToAndNameRelOutletDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling listAgents service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	public static List<String> agentOfSupervisor(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		List<String> distributorOfSupervisor = DistributorServices.distributorOfSupervisor(delegator, userLogin);
		conditions.add(EntityCondition.makeCondition("distributorId", EntityJoinOperator.IN, distributorOfSupervisor));
		List<GenericValue> partyCustomers = delegator.findList("PartyCustomer",
						EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		return EntityUtil.getFieldListFromEntityList(partyCustomers, "partyId", true);
	}
	public static List<String> agentOfSalesman(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyTypeId", "RETAIL_OUTLET"));
        conditions.add(EntityCondition.makeCondition("salesmanId", userLogin.get("partyId")));
		List<GenericValue> partyCustomers = delegator.findList("PartyCustomer",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		return EntityUtil.getFieldListFromEntityList(partyCustomers, "partyId", true);
	}
	public static List<String> agentsOfDistributor(Delegator delegator, Object partyIdTo) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
        conditions.add(EntityCondition.makeCondition("partyTypeId", "RETAIL_OUTLET"));
        conditions.add(EntityCondition.makeCondition("salesmanId", partyIdTo));
		List<GenericValue> partyCustomers = delegator.findList("PartyCustomer",
						EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		return EntityUtil.getFieldListFromEntityList(partyCustomers, "partyId", true);
	}
	private static Map<String, Object> getDistributor(Delegator delegator, Object partyIdFrom) throws GenericEntityException {
		Map<String, Object> distributor = FastMap.newInstance();
		GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyIdFrom), false);
		if (UtilValidate.isNotEmpty(partyCustomer)) {
			String partyIdTo = partyCustomer.getString("distributorId");
            GenericValue dist = delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId", partyIdTo), false);
            if (UtilValidate.isNotEmpty(dist)) {
                distributor.put("distributorId", partyIdTo);
                distributor.put("distributorCode", dist.getString("partyCode"));
                distributor.put("distributor", dist.getString("fullName"));
            }
		}
		return distributor;
	}
	private static Map<String, Object> getSalesman(Delegator delegator, Object partyIdTo) throws GenericEntityException {
	    Map<String, Object> salesman = FastMap.newInstance();
        GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyIdTo), false);
        if (UtilValidate.isNotEmpty(partyCustomer) && UtilValidate.isNotEmpty(partyCustomer.getString("salesmanId"))) {
            String salesmanId = partyCustomer.getString("salesmanId");
            GenericValue partySalesman = delegator.findOne("PartySalesman", UtilMisc.toMap("partyId", salesmanId), false);
            if (UtilValidate.isNotEmpty(partySalesman)) {
                salesman.put("salesmanId", salesmanId);
                salesman.put("salesman", partySalesman.getString("fullName"));
            }
        }
        return salesman;
	}
	
	public static Map<String, Object> loadAgentInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> agentInfo = FastMap.newInstance();
			String partyId = (String) context.get("partyId");
			GenericValue partyGroup = delegator.findOne("PartyAndPartyGroupDetail", UtilMisc.toMap("partyId", partyId), false);
            GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyId), false);
			agentInfo.put("partyId", partyGroup.get("partyId"));
			agentInfo.put("statusId", partyGroup.get("statusId"));
			agentInfo.put("partyCode", partyGroup.get("partyCode"));
			agentInfo.put("groupName", partyGroup.get("groupName"));
			agentInfo.put("officeSiteName", partyGroup.get("officeSiteName"));
			agentInfo.put("logoImageUrl", partyGroup.get("logoImageUrl"));
			agentInfo.put("comments", partyGroup.get("comments"));
			agentInfo.put("currencyUomId", partyGroup.get("preferredCurrencyUomId"));
            agentInfo.put("visitFrequencyTypeId", partyCustomer.get("visitFrequencyTypeId"));
			List<EntityCondition> conditions = FastList.newInstance();
			//	get taxAuthInfos
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM", "taxAuthPartyId", "VNM_TAX")));
			List<GenericValue> partyTaxAuthInfos = delegator.findList("PartyTaxAuthInfo",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyTaxId", "fromDate"), null, null, false);
			if (UtilValidate.isNotEmpty(partyTaxAuthInfos)) {
				GenericValue partyTaxAuthInfo = EntityUtil.getFirst(partyTaxAuthInfos);
				agentInfo.put("taxAuthInfos", partyTaxAuthInfo.get("partyTaxId"));
				agentInfo.put("taxAuthInfosfromDate", partyTaxAuthInfo.getTimestamp("fromDate").getTime());
			}
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
			List<GenericValue> partyAndContactMechs = delegator.findList("PartyContactGeoDetailByPurpose",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			List<Map<String, Object>> contacts = FastList.newInstance();
			for (GenericValue x : partyAndContactMechs) {
				switch (x.getString("contactMechTypeId")) {
				case "TELECOM_NUMBER":
					if(x.getString("contactMechPurposeTypeId").equals("PRIMARY_PHONE")){
						agentInfo.put("contactNumber", x.get("contactNumber"));
						agentInfo.put("contactNumberId", x.get("contactMechId"));
					} else {
						Map<String, Object> o = FastMap.newInstance();
						o.put("contactMechId", x.getString("contactMechId"));
						o.put("name", x.getString("askForName"));
						o.put("phone", x.getString("contactNumber"));
						contacts.add(o);
						agentInfo.put("contacts", contacts);
					}
					break;
				case "EMAIL_ADDRESS":
					agentInfo.put("infoString", x.get("infoString"));
					agentInfo.put("infoStringId", x.get("contactMechId"));
					break;
				case "POSTAL_ADDRESS":
					if(x.getString("contactMechPurposeTypeId").equals("PRIMARY_LOCATION")){
						agentInfo.put("contactMechId", x.get("contactMechId"));
						agentInfo.put("address1", x.get("address1"));
						if ("Y".equals(context.get("detail"))) {
							agentInfo.put("wardGeoId", x.getString("wardGeoName"));
							agentInfo.put("districtGeoId", x.getString("districtGeoName"));
							agentInfo.put("stateProvinceGeoId", x.getString("stateProvinceGeoName"));
							agentInfo.put("countryGeoId", x.getString("countryGeoName"));
							String address = x.getString("address1") + ", " +
									x.getString("wardGeoName") + ", " +
									x.getString("districtGeoName") + ", " +
									x.getString("stateProvinceGeoName") + ", " +
									x.getString("countryGeoName");
							if (UtilValidate.isNotEmpty(address)) {
								address = address.replaceAll(", null, ", ", ");
								address = address.replaceAll(", null,", ", ");
							}
							agentInfo.put("address", address);
						} else {
							agentInfo.put("wardGeoId", x.get("wardGeoId"));
							agentInfo.put("districtGeoId", x.get("districtGeoId"));
							agentInfo.put("stateProvinceGeoId", x.get("stateProvinceGeoId"));
							agentInfo.put("countryGeoId", x.get("countryGeoId"));
							agentInfo.put("addressId", x.get("contactMechId"));
						}
						agentInfo.put("geoPointId", x.get("geoPointId"));
						agentInfo.put("latitude", x.get("latitude"));
						agentInfo.put("longitude", x.get("longitude"));
					}
					break;
				default:
					break;
				}
			}
			//	get representative
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", "CUSTOMER",
					"roleTypeIdFrom", "OWNER", "partyRelationshipTypeId", "OWNER")));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
			if (UtilValidate.isNotEmpty(partyRelationships)) {
				String representativeId = (String) EntityUtil.getFirst(partyRelationships).get("partyIdFrom");
				agentInfo.put("representative", DistributorServices.getRepresentative(delegator, representativeId, context.get("detail")));
			}
			agentInfo.putAll(getDistributor(delegator, partyId));
			agentInfo.putAll(getSalesman(delegator, partyId));
			agentInfo.put("routes", listRoutesOfCustomer(delegator, partyId));
			result.put("agentInfo", agentInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<String> listRoutesOfCustomer(Delegator delegator, Object partyId) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr("relationFromDate","relationThruDate"));
		conditions.add(EntityUtil.getFilterByDateExpr("ctmFromDate","ctmThruDate"));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdFrom", "ROUTE", "roleTypeIdTo", "CUSTOMER",
				"contactMechPurposeTypeId", "PRIMARY_LOCATION")));
		List<GenericValue> partyGroupGeoViews = delegator.findList("PartyGroupGeoView",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
		return EntityUtil.getFieldListFromEntityList(partyGroupGeoViews, "partyIdFrom", true);
	}
	
	public static Map<String, Object> createAgent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = null;
		String postalAddressId = null;
		String statusId = (String) context.get("statusId");
		if(statusId == null) statusId = "PARTY_DISABLED";
		String partyTypeId = (String) context.get("partyTypeId");
		
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			//partyId = (String) context.get("partyId");
			String partyCode = (String) context.get("partyCode");
			String partyFullName = (String) context.get("groupName");
			try {
				UniqueUtil.checkPartyCode(delegator, partyId, partyCode);
			} catch (Exception e) {
				ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSUserIsAlreadyExists", locale));
			}
			
			// create party
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			if(partyTypeId != null && partyTypeId.equals("END_CUSTOMER")) partyId = "KHC" + delegator.getNextSeqId("Party");
			else partyId = "RO" + organizationId + delegator.getNextSeqId("Party");
			
			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			partyGroup.put("partyId", partyId);
			partyGroup.put("partyTypeId", partyTypeId);
			partyGroup.put("statusId", statusId);
			partyGroup.put("preferredCurrencyUomId", context.get("currencyUomId"));
			Map<String, Object> resultCreateOutlet = dispatcher.runSync("createPartyGroup", partyGroup);
			if (ServiceUtil.isError(resultCreateOutlet)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateOutlet));
			}
			
			// store party code, because service "createPartyGroup" not store field "partyCode"
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", (partyCode != null ? partyCode : partyId)), EntityCondition.makeCondition("partyId", partyId));

			// create roles
			createAgentRole(delegator, partyId, userLogin);
			
			// create relationships
			createAgentRelationship(dispatcher, partyId, organizationId, userLogin);

			// create postal address
			String address1 = (String) context.get("address1");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String districtGeoId = (String) context.get("districtGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String geoPointId = (String) context.get("geoPointId");
			String postalCode = (String) context.get("postalCode");
			String toName = (String) context.get("toName");
			String attnName = (String) context.get("attnName");
			postalAddressId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
					"POSTAL_ADDRESS", toName, attnName, 
					"PRIMARY_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId, districtGeoId , wardGeoId, stateProvinceGeoId, postalCode, geoPointId, userLogin);
			
			if (UtilValidate.isNotEmpty(postalAddressId)) {
				// add others purpose into postal address
				dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, 
						"contactMechId", postalAddressId, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "userLogin", userLogin));
			}
			
			// create telecom number 
			String contactNumber = (String) context.get("contactNumber");
			String telecomId = CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE", contactNumber, partyFullName, partyId, userLogin);
			CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, telecomId, "PHONE_SHIPPING", partyId, userLogin);

			//createOrStoreAdditionalContact(dispatcher, delegator, context.get("contactPerson"), partyId, userLogin);
			
			// create email
			CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);

			// create tax info
			String taxAuthInfos = (String) context.get("taxAuthInfos");
			String defaultTaxAuthGeoId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.geo.id");
			String defaultTaxAuthPartyId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.party.id");
			Map<String, Object> resultTaxInfo = dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", defaultTaxAuthGeoId, "taxAuthPartyId", defaultTaxAuthPartyId, "partyTaxId", taxAuthInfos, "userLogin", userLogin));
			if (ServiceUtil.isError(resultTaxInfo)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultTaxInfo));
			}
			
			// create party representative
			String representativeParams = (String) context.get("representative");
			Map<String, Object> resultCreateRep = DistributorServices.createRepresentativeAdvance(delegator, dispatcher, partyId, representativeParams, userLogin, "CUSTOMER", postalAddressId);
			//DistributorServices.createRepresentative(delegator, dispatcher, partyId, (String) context.get("representative"), "CUSTOMER", userLogin);
			if (ServiceUtil.isError(resultCreateRep)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateRep));
			}
			//String representativeId = (String) resultCreateRep.get("representativeId");
			
			// create relationship with distributor
			String distributorId = (String) context.get("distributorId");
            String routeId = (String) context.get("routeId");
            String salesExecutiveId = (String) context.get("salesmanId");
            String visitFrequencyTypeId = (String) context.get("visitFrequencyTypeId");
            dispatcher.runSync("createOrUpdatePartyCustomer", UtilMisc.toMap("partyId", partyId, "distributorId", distributorId, "salesmanId", salesExecutiveId, "salesMethodChannelEnumId", "SMCHANNEL_GT", "userLogin", userLogin, "visitFrequencyTypeId", visitFrequencyTypeId));
            if (UtilValidate.isNotEmpty(distributorId)) {
				dispatcher.runSync("updateDistributorProvideAgent", UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", distributorId,
                        "routeId", routeId, "salesmanId", salesExecutiveId, "userLogin", userLogin));
			}
			
			// create relationship with sales executive
			if (UtilValidate.isNotEmpty(salesExecutiveId)) {
				dispatcher.runSync("updateSalesmanProvideAgent", UtilMisc.toMap("partyIdTo", partyId, "partyIdFrom", salesExecutiveId, "userLogin", userLogin));
			}
			
			// add party to distributor's product store
			//List<String> productStores = (List<String>) context.get("productStores[]");
			/*List<String> productStores = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductStore", UtilMisc.toMap("payToPartyId", distributorId), null, false), "productStoreId", true);
			if(UtilValidate.isNotEmpty(productStores)){
				for (String prodStoreId : productStores) {
					dispatcher.runSync("createProductStoreRole", UtilMisc.toMap("partyId", partyId, "productStoreId", prodStoreId, "roleTypeId", "CUSTOMER", "userLogin", userLogin));
				}
			}*/
			String productStoreId = (String) context.get("productStoreId");
			if(UtilValidate.isNotEmpty(productStoreId)){
				dispatcher.runSync("createProductStoreRole", UtilMisc.toMap("partyId", partyId, "productStoreId", productStoreId, "roleTypeId", "CUSTOMER", "userLogin", userLogin));
			}
			
			// create relationship with router
			if (UtilValidate.isNotEmpty(routeId)) {
				SupUtil.createAndUpdateRouteStoreRelationship(delegator, dispatcher, routeId, partyId, true, true);
			}
			
			// send notification
			String header = "";
	     	String state = "open";
	     	String action = "AgentDetail?" + "partyId=" + partyId;
	     	String targetLink = "";
	     	String ntfType = "ONE";
	     	String sendToGroup = "N";
	     	String sendrecursive = "Y";
			header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSNewAgentNotify", locale) + " [" + partyCode + "]";
			List<String> salesAdminManagerIds = SalesPartyUtil.getPartiesByRoles(delegator, UtilMisc.toList("SALESADMIN_MANAGER"), true, userLogin);
			NotificationUtil.sendNotify(dispatcher, locale, salesAdminManagerIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		successResult.put("partyId", partyId);
		successResult.put("contactMechId", postalAddressId);
		return successResult;
	}
	
	public static void createOrStoreAdditionalContact(LocalDispatcher dispatcher, Delegator delegator, Object contactPerson, String partyId, GenericValue userLogin) throws GenericServiceException, GenericEntityException {
		if(UtilValidate.isNotEmpty(contactPerson)){
			JSONArray tmCont = JSONArray.fromObject(contactPerson);
			for(int i = 0; i < tmCont.size(); i++){
				JSONObject o = tmCont.getJSONObject(i);
				Object contactMechId = null;
				if (o.containsKey("contactMechId")) {
					contactMechId = CallcenterServices.cleanString(o.get("contactMechId"));
				}
				Object phone = o.get("phone");
				if (o.containsKey("phone")) {
					phone = CallcenterServices.cleanString(o.get("phone"));
				}
				Object name = o.get("name");
				if (o.containsKey("name")) {
					name = CallcenterServices.cleanString(o.get("name"));
				}
				if (UtilValidate.isNotEmpty(phone) && UtilValidate.isNotEmpty(name)) {
					if (UtilValidate.isNotEmpty(contactMechId)) {
						GenericValue contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", contactMechId), false);
						if (UtilValidate.isNotEmpty(contactMech)) {
							CallcenterServices.updateContactMechTelecomNumber(dispatcher, delegator, contactMechId.toString(), "TELECOM_NUMBER",
									"PHONE_WORK", phone, name, partyId, userLogin);
						} else {
							CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_WORK",
									phone, name, partyId, userLogin);
						}
					} else {
						CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_WORK",
								phone, name, partyId, userLogin);
					}
				}
			}
		}
	}

	private static void createAgentRole(Delegator delegator, String partyId, GenericValue userLogin) throws GenericServiceException, GenericEntityException {
		List<String> roles = Arrays.asList("AGENT", "CUSTOMER", "BILL_TO_CUSTOMER", "END_USER_CUSTOMER", "PLACING_CUSTOMER", "SHIP_TO_CUSTOMER");
		
		List<GenericValue> toBeStored = new LinkedList<GenericValue>();
		for (String r : roles) {
			//dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", r, "userLogin", userLogin));
			toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", r)));
		}
		delegator.storeAll(toBeStored);
	}
	
	private static void createAgentRelationship(LocalDispatcher dispatcher, String partyId, Object organizationId, GenericValue userLogin) throws GenericServiceException {
		// relationship from distributor to organization
		dispatcher.runSync("createPartyRelationship", UtilMisc.toMap(
						"partyIdFrom", partyId, "partyIdTo", organizationId, 
						"roleTypeIdFrom", "CUSTOMER", "roleTypeIdTo", "INTERNAL_ORGANIZATIO", 
						"partyRelationshipTypeId", "CUSTOMER_REL", "userLogin", userLogin));
	}
	
	public static Map<String, Object> updateAgent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			String partyId = (String) context.get("partyId");
			
			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			partyGroup.put("preferredCurrencyUomId", context.get("currencyUomId"));
			result = dispatcher.runSync("updatePartyGroup", partyGroup);
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", context.get("partyCode")),
					EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			updateAgentRelationship(dispatcher, delegator, partyId, organizationId, userLogin);
			
			String contactMechId = CallcenterServices.updateContactMechTelecomNumber(dispatcher, delegator, (String)context.get("contactNumberId"), "TELECOM_NUMBER",
					"PRIMARY_PHONE", context.get("contactNumber"), context.get("groupName"), partyId, userLogin);
			CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, contactMechId, "PHONE_SHIPPING", partyId, userLogin);
			CallcenterServices.updateContactMechEmail(dispatcher, delegator, (String)context.get("infoStringId"), "EMAIL_ADDRESS",
					context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);
			
			createOrStoreAdditionalContact(dispatcher, delegator, context.get("contactPerson"), partyId, userLogin);
			
			if (UtilValidate.isEmpty(context.get("taxAuthInfosfromDate"))) {
				dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM",
						"taxAuthPartyId", "VNM_TAX", "partyTaxId", context.get("taxAuthInfos"), "userLogin", userLogin));
			} else {
				Long taxAuthInfosfromDate = (Long) context.get("taxAuthInfosfromDate");
				dispatcher.runSync("updatePartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM",
						"taxAuthPartyId", "VNM_TAX", "partyTaxId", context.get("taxAuthInfos"),
						"fromDate", new Timestamp(taxAuthInfosfromDate), "userLogin", userLogin));
			}
			
			DistributorServices.updateRepresentative(delegator, dispatcher, partyId, (String) context.get("representative"), "CUSTOMER", userLogin);
			
			if (UtilValidate.isNotEmpty(context.get("distributorId"))) {
				dispatcher.runSync("updateDistributorProvideAgent",
						UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", context.get("distributorId"), "userLogin", userLogin));
			}
			if (UtilValidate.isNotEmpty(context.get("salesmanId"))) {
				dispatcher.runSync("updateSalesmanProvideAgent",
						UtilMisc.toMap("partyIdTo", partyId, "partyIdFrom", context.get("salesmanId"), "userLogin", userLogin));
			}
			//update visitFrequencyTypeId
            String distributorId = (String) context.get("distributorId"); //dư thừa
            String salesExecutiveId = (String) context.get("salesmanId"); //dư thừa
            String visitFrequencyTypeId = (String) context.get("visitFrequencyTypeId");
            dispatcher.runSync("createOrUpdatePartyCustomer", UtilMisc.toMap("partyId", partyId, "distributorId", distributorId, "salesmanId", salesExecutiveId, "userLogin", userLogin, "visitFrequencyTypeId", visitFrequencyTypeId));
			
			result.put("partyId", partyId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	private static void updateAgentRelationship(LocalDispatcher dispatcher, Delegator delegator, String partyId, Object organizationId,
			GenericValue userLogin) throws GenericServiceException, GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "CUSTOMER",
						"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "CUSTOMER_REL")));
		List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		boolean exists = false;
		for (GenericValue x : partyRelationships) {
			if (x.get("partyIdTo").equals(organizationId)) {
				exists = true;
				continue;
			}
			x.set("thruDate", new Timestamp(System.currentTimeMillis()));
			x.store();
		}
		if (!exists) {
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", "CUSTOMER",
							"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "CUSTOMER_REL",
							"userLogin", userLogin));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listGTSupervisorDepartment(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	try {
    		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        	List<String> listSortFields = (List<String>) context.get("listSortFields");
        	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    		GenericValue userLogin = (GenericValue) context.get("userLogin");
    		String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		Organization buildOrg = PartyUtil.buildOrg(delegator, companyId, true, false);
    		List<String> parties = FastList.newInstance();
    		if (MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, true) || MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, true)) {
    			parties = EntityUtil.getFieldListFromEntityList(buildOrg.getAllDepartmentList(delegator), "partyId", true);
			} else {
				parties = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
			}
    		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
    		listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityJoinOperator.EQUALS, "SALESSUP_DEPT_GT"));
    		EntityListIterator listIterator = delegator.find("PartyToAndPartyNameDetail",
    				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listMTSupervisorDepartment(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts =(EntityFindOptions) context.get("opts");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Organization buildOrg = PartyUtil.buildOrg(delegator, companyId, true, false);
			List<String> parties = FastList.newInstance();
			if (MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, true) || MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, true)) {
				parties = EntityUtil.getFieldListFromEntityList(buildOrg.getAllDepartmentList(delegator), "partyId", true);
			} else {
				parties = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
			}
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityJoinOperator.EQUALS, "SALESSUP_DEPT_MT"));
			EntityListIterator listIterator = delegator.find("PartyToAndPartyNameDetail",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> rejectAgent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			String partyId = (String) context.get("partyId");
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("partyIdFrom", partyId));
			List<GenericValue> agreements = delegator.findList("Agreement", EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(agreements)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSRejectWasFaildedThisRetailOutletHasAAgreement", locale));
			}
			
			// thru date in relationships
			conditions.clear();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("partyIdFrom", partyId),
					EntityCondition.makeCondition("partyIdTo", partyId)
					), EntityJoinOperator.OR));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.NOT_EQUAL, "OWNER"));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(partyRelationships)) {
				for (GenericValue x : partyRelationships) {
					x.set("thruDate", nowTimestamp);
				}
				delegator.storeAll(partyRelationships);
			}
			
			// thru date in product store roles
			conditions.clear();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("partyId", partyId));
			List<GenericValue> productStoreRoles = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(productStoreRoles)) {
				for (GenericValue x : productStoreRoles) {
					x.set("thruDate", nowTimestamp);
				}
				delegator.storeAll(productStoreRoles);
			}
			
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			if (party != null) {
				party.set("statusId", "PARTY_DISABLED");
				delegator.store(party);
			}
            RouteUtils.unbindCustomerToRoute(delegator,partyId);
            RouteUtils.removeRouteScheduleDetailDateByCustomer(delegator,partyId);
            RouteUtils.removeDeliveryClusterCustomerByCustomer(delegator,partyId);
		} catch (Exception e) {
			result = ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSAgentHasContract", locale));
		}
		return result;
	}
	
	public static Map<String, Object> setAgentStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
	    Delegator delegator = ctx.getDelegator();
	    String partyId = (String) context.get("partyId");
	    String statusId = (String) context.get("statusId");
	    Locale locale = (Locale) context.get("locale");
	    try {
	    	GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyId), false);
	        if (partyCustomer.get("statusId") == null) { // old records
	        	partyCustomer.set("statusId", "PARTY_ENABLED");
	        } else {
	        	partyCustomer.set("statusId", statusId);
	        } 
	        partyCustomer.store();
	        ctx.getDispatcher().runSync("setPartyStatus", context);
	        } catch (GenericEntityException e1) {
	            Debug.logError(e1, e1.getMessage(), module);
	            return ServiceUtil.returnError(e1.getMessage() );
	        } catch (GenericServiceException e2) {
	            Debug.logError(e2, e2.getMessage(), module);
	            return ServiceUtil.returnError( e2.getMessage());
	        }
	    Map<String, Object> results = ServiceUtil.returnSuccess();
	    return results;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listMTCustomers(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> listIterator = null;
		try {
			if (parameters.containsKey("sD")) {
				if ("N".equals(parameters.get("sD")[0])) {
                    listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
                    listAllConditions.add(EntityCondition.makeCondition("partyTypeId", EntityJoinOperator.NOT_EQUAL,"END_CUSTOMER"));
				}
			}
            listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", "SMCHANNEL_MT"));
			listSortFields.add("partyCode");
			listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "PartyCustomerFullDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

    public static Map<String, Object> listMTGroupCustomer(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<GenericValue> listIterator = null;
        try {
            if (parameters.containsKey("sD")) {
                if ("N".equals(parameters.get("sD")[0])) {
                    listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
                }
            }
            listAllConditions.add(EntityCondition.makeCondition("partyTypeId", "CUSTOMER_CHAIN_GROUP"));
            listSortFields.add("partyCode");
            listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "Party", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("listIterator", listIterator);
        return result;
    }

    public static Map<String, Object> listMTCustomersByGroup(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<GenericValue> listIterator = null;
        try {
            if (parameters.containsKey("sD")) {
                if ("N".equals(parameters.get("sD")[0])) {
                    listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
                }
            }
            String groupId = null;
            if (parameters.containsKey("groupId") && parameters.get("groupId").length > 0) {
                groupId = (String) parameters.get("groupId")[0];
                if (UtilValidate.isNotEmpty(groupId)) {
                    listAllConditions.add(EntityCondition.makeCondition("groupId", groupId));
                }
            }
            String countryGeoId = null, stateProvinceGeoId = null, districtGeoId = null, wardGeoId = null;
            if (parameters.containsKey("countryGeoId") && parameters.get("countryGeoId").length > 0) {
                countryGeoId = (String) parameters.get("countryGeoId")[0];
                if (UtilValidate.isNotEmpty(countryGeoId)) {
                    listAllConditions.add(EntityCondition.makeCondition("countryGeoId", countryGeoId));
                }
            }
            if (parameters.containsKey("stateProvinceGeoId") && parameters.get("stateProvinceGeoId").length > 0) {
                stateProvinceGeoId = (String) parameters.get("stateProvinceGeoId")[0];
                if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
                    listAllConditions.add(EntityCondition.makeCondition("stateProvinceGeoId", stateProvinceGeoId));
                }
            }
            if (parameters.containsKey("districtGeoId") && parameters.get("districtGeoId").length > 0) {
                districtGeoId = (String) parameters.get("districtGeoId")[0];
                if (UtilValidate.isNotEmpty(districtGeoId)) {
                    listAllConditions.add(EntityCondition.makeCondition("districtGeoId", districtGeoId));
                }
            }
            if (parameters.containsKey("wardGeoId") && parameters.get("wardGeoId").length > 0) {
                wardGeoId = (String) parameters.get("wardGeoId")[0];
                if (UtilValidate.isNotEmpty(wardGeoId)) {
                    listAllConditions.add(EntityCondition.makeCondition("wardGeoId", wardGeoId));
                }
            }

            listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", "SMCHANNEL_MT"));
            listSortFields.add("partyCode");
            listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "PartyCustomerDetailSimple", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("listIterator", listIterator);
        return result;
    }

    public static Map<String, Object> updateSalessupSalesmanMTCustomer(DispatchContext ctx, Map<String, Object> context){
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String[] customerIdArr = new String[]{};
        List<String> customerIds = FastList.newInstance();
        String salesmanId = (String) context.get("salesmanId");
        String salessupId = (String) context.get("salessupId");
        String salessupDeptId = SalesPartyUtil.getOrgIdManagedByParty(delegator, salessupId);
        String customerIdStr = (String) context.get("customerIds");
        List<EntityCondition> conds = FastList.newInstance();
        List<GenericValue> genericValues = FastList.newInstance();
        if (UtilValidate.isNotEmpty(customerIdStr)) {
            customerIdArr = customerIdStr.split(",");
        }
        for (String customerId : customerIdArr) {
            customerIds.add(customerId);
        }
        try {
            if (UtilValidate.isNotEmpty(salessupDeptId)) {
                //remove old relationship
                conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, customerIds));
                conds.add(EntityUtil.getFilterByDateExpr());
                conds.add(EntityCondition.makeCondition("roleTypeIdTo", "SALESSUP_DEPT"));
                conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "CUSTOMER"));
                genericValues = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
                for (GenericValue pr: genericValues) {
                    pr.set("thruDate", UtilDateTime.nowTimestamp());
                }
                delegator.storeAll(genericValues);

                conds.clear();
                genericValues.clear();
                conds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, customerIds));
                conds.add(EntityUtil.getFilterByDateExpr());
                conds.add(EntityCondition.makeCondition("roleTypeIdTo", "CUSTOMER"));
                conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "SALES_EXECUTIVE"));
                genericValues = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
                for (GenericValue pr: genericValues) {
                    pr.set("thruDate", UtilDateTime.nowTimestamp());
                }
                delegator.storeAll(genericValues);

                //create new relationship
                for (String customerId : customerIds) {
                    dispatcher.runSync("createPartyRelationship", UtilMisc.toMap(
                            "partyIdFrom", customerId, "partyIdTo", salessupDeptId,
                            "roleTypeIdFrom", "CUSTOMER", "roleTypeIdTo", "SALESSUP_DEPT",
                            "partyRelationshipTypeId", "CUSTOMER_REL", "userLogin", userLogin));
                    dispatcher.runSync("createPartyRelationship", UtilMisc.toMap(
                            "partyIdFrom", salesmanId, "partyIdTo", customerId,
                            "roleTypeIdFrom", "SALES_EXECUTIVE", "roleTypeIdTo", "CUSTOMER",
                            "partyRelationshipTypeId", "SALES_REP_REL", "userLogin", userLogin));
                }

                //update partyCustomer
                genericValues.clear();
                conds.clear();
                conds.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, customerIds));
                genericValues = delegator.findList("PartyCustomer", EntityCondition.makeCondition(conds), null, null, null, false);
                for (GenericValue gv: genericValues) {
                    gv.set("supervisorId", salessupId);
                    gv.set("salesmanId", salesmanId);
                }
                delegator.storeAll(genericValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BLCreateDeliveryClusterError", locale));
        }
        return successResult;
    }

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createMTCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = null;
		String postalAddressId = null;
		
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			//partyId = (String) context.get("partyId");
			String partyCode = (String) context.get("partyCode");
			String partyFullName = (String) context.get("groupName"); 
			String statusId = (String) context.get("statusId");
			if(statusId == null) statusId = "PARTY_DISABLED";
			try {
				UniqueUtil.checkPartyCode(delegator, partyId, partyCode);
			} catch (Exception e) {
				ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSUserIsAlreadyExists", locale));
			}
			
			// create party
			String partyTypeId = (String) context.get("partyTypeId");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			if(partyTypeId != null && partyTypeId.equals("END_CUSTOMER")) partyId = "KHC";
			else partyId = "KH";
			switch (partyTypeId) {
			case "SUPERMARKET":
				partyId += "SM";
				break;
			case "COFFEE_HOUSE":
				partyId += "CF";
				break;
			case "SCHOOL":
				partyId += "SC";
				break;
			case "TRADE_CENTER":
				partyId += "TC";
				break;
			case "HOSPITAL":
				partyId += "HO";
				break;
			default:
				break;
			}
			partyId += delegator.getNextSeqId("Party");
			
			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			partyGroup.put("partyId", partyId);
			partyGroup.put("partyTypeId", partyTypeId);
			partyGroup.put("statusId", statusId);
			partyGroup.put("preferredCurrencyUomId", context.get("currencyUomId"));
			Map<String, Object> resultCreateCustomer = dispatcher.runSync("createPartyGroup", partyGroup);
			if (ServiceUtil.isError(resultCreateCustomer)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateCustomer));
			}
			
			// store party code, because service "createPartyGroup" not store field "partyCode"
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", (partyCode != null ? partyCode : partyId)), EntityCondition.makeCondition("partyId", partyId));

			// create roles
			createMTCustomerRole(dispatcher, partyId, userLogin);
			dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", partyId, "roleTypeId", "CHILD_MEMBER", "userLogin", userLogin));

			// create relationship with representative offices
			String representativeOfficeId = (String) context.get("representativeOfficeId");
			if (UtilValidate.isNotEmpty(representativeOfficeId)) {
				dispatcher.runSync("createPartyRelationship",
						UtilMisc.toMap(
								"partyIdFrom", representativeOfficeId, "partyIdTo", partyId,
								"roleTypeIdFrom", "CUSTOMER", "roleTypeIdTo", "CHILD_MEMBER",
								"partyRelationshipTypeId", "OWNER", "userLogin", userLogin));
			}else{
				// create relationships
				createMTCustomerRelationship(dispatcher, partyId, organizationId, userLogin);
			}

			// create postal address
			String address1 = (String) context.get("address1");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String districtGeoId = (String) context.get("districtGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String geoPointId = (String) context.get("geoPointId");
			String postalCode = (String) context.get("postalCode");
			String toName = (String) context.get("toName");
			String attnName = (String) context.get("attnName");
			postalAddressId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
					"POSTAL_ADDRESS", toName, attnName, 
					"PRIMARY_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId, districtGeoId , wardGeoId, stateProvinceGeoId, postalCode, geoPointId, userLogin);

			if (UtilValidate.isNotEmpty(postalAddressId)) {
				// add others purpose into postal address
				dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, 
						"contactMechId", postalAddressId, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "userLogin", userLogin));
			}
			
			// create telecom number 
			String contactNumber = (String) context.get("contactNumber");
			String telecomId = CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE", contactNumber, partyFullName, partyId, userLogin);
			CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, telecomId, "PHONE_SHIPPING", partyId, userLogin);

			//createOrStoreAdditionalContact(dispatcher, delegator, context.get("contactPerson"), partyId, userLogin);
			
			// create email
			CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);

			// create tax info
			String taxAuthInfos = (String) context.get("taxAuthInfos");
			String defaultTaxAuthGeoId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.geo.id");
			String defaultTaxAuthPartyId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.party.id");
			Map<String, Object> resultTaxInfo = dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", defaultTaxAuthGeoId, "taxAuthPartyId", defaultTaxAuthPartyId, "partyTaxId", taxAuthInfos, "userLogin", userLogin));
			if (ServiceUtil.isError(resultTaxInfo)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultTaxInfo));
			}
			
			// create party representative
			String representativeParams = (String) context.get("representative");
			Map<String, Object> resultCreateRep = DistributorServices.createRepresentativeAdvance(delegator, dispatcher, partyId, representativeParams, userLogin, "CUSTOMER", postalAddressId);
			//DistributorServices.createRepresentative(delegator, dispatcher, partyId, (String) context.get("representative"), "CUSTOMER", userLogin);
			if (ServiceUtil.isError(resultCreateRep)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateRep));
			}
			//String representativeId = (String) resultCreateRep.get("representativeId");
			
			// update PartyCustomer and supervisor of customer MT
			String salesExecutiveId = (String) context.get("salesmanId");
			String supervisorId = (String) context.get("supervisorId");
			if (UtilValidate.isEmpty(supervisorId)) {
				GenericValue supervisorGV = delegator.findOne("PartySalesman", UtilMisc.toMap("partyId", salesExecutiveId), false);
				if (supervisorGV != null) supervisorId = supervisorGV.getString("supervisorId");
			}
			dispatcher.runSync("createOrUpdatePartyCustomer", UtilMisc.toMap("partyId", partyId, "partyCode", partyCode, "supervisorId", supervisorId, "salesmanId", salesExecutiveId, "salesMethodChannelEnumId", "SMCHANNEL_MT", "userLogin", userLogin));
			if (UtilValidate.isNotEmpty(supervisorId)) {
				dispatcher.runSync("updateSupervisorMTCustomer", UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", supervisorId, "userLogin", userLogin));
			}
			
			// create relationship with sales executive
			if (UtilValidate.isNotEmpty(salesExecutiveId)) {
				dispatcher.runSync("updateSalesmanProvideAgent", UtilMisc.toMap("partyIdTo", partyId, "partyIdFrom", salesExecutiveId, "userLogin", userLogin));
			}

			// add party to company's product store
			// List<String> productStores = (List<String>) context.get("productStores[]");
			String productStoreId = (String) context.get("productStoreId");
			if(UtilValidate.isNotEmpty(productStoreId)){
					dispatcher.runSync("createProductStoreRoleOlb", UtilMisc.toMap("partyId", partyId, "fromDate", new Timestamp(System.currentTimeMillis()),
							"productStoreId", productStoreId, "roleTypeId", "CUSTOMER", "userLogin", userLogin));
			}
			
			// create relationship with router
			String routeId = (String) context.get("routeId");
			if (UtilValidate.isNotEmpty(routeId)) {
				SupUtil.createAndUpdateRouteStoreRelationship(delegator, dispatcher, routeId, partyId, true, true);
			}
			//create consignee
			String consigneeId = (String) context.get("consigneeId");
			if(UtilValidate.isNotEmpty(consigneeId)) {

				dispatcher.runSync("createPartyRelationship",
						UtilMisc.toMap(
								"partyIdFrom", partyId, "partyIdTo", consigneeId,
								"roleTypeIdFrom", "CUSTOMER", "roleTypeIdTo", "CONSIGNEE",
								"partyRelationshipTypeId", "CUSTOMER_REL", "userLogin", userLogin));
			}
			// send notification
			String header = "";
	     	String state = "open";
	     	String action = "MTCustomerDetail?" + "partyId=" + partyId;
	     	String targetLink = "";
	     	String ntfType = "ONE";
	     	String sendToGroup = "N";
	     	String sendrecursive = "Y";
			header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSNewMTCustomerNotify", locale) + " [" + (partyCode!=null?partyCode:partyId) + "]";
			List<String> salesAdminManagerIds = SalesPartyUtil.getPartiesByRoles(delegator, UtilMisc.toList("SALESADMIN_MANAGER"), true, userLogin);
			NotificationUtil.sendNotify(dispatcher, locale, salesAdminManagerIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		successResult.put("partyId", partyId);
		successResult.put("contactMechId", postalAddressId);
		return successResult;
	}

	private static void createMTCustomerRole(LocalDispatcher dispatcher, String partyId, GenericValue userLogin)
			throws GenericServiceException {
		
		// list role for MTCustomer
		List<String> roles = Arrays.asList("BILL_TO_CUSTOMER", "CUSTOMER", "END_USER_CUSTOMER", "PLACING_CUSTOMER", "SHIP_TO_CUSTOMER", "CUSTOMER_MT");
		for (String r : roles) {
			dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", partyId, "roleTypeId", r, "userLogin", userLogin));
		}
	}
	private static void createMTCustomerRelationship(LocalDispatcher dispatcher, String partyId, Object organizationId,
			GenericValue userLogin) throws GenericServiceException {
		dispatcher.runSync("createPartyRelationship",
				UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", "CUSTOMER",
						"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "CUSTOMER_REL",
						"userLogin", userLogin));
	}
	public static Map<String, Object> updateMTCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			String partyId = (String) context.get("partyId");
			
			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			partyGroup.put("preferredCurrencyUomId", context.get("currencyUomId"));
			result = dispatcher.runSync("updatePartyGroup", partyGroup);
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			party.set("partyCode", context.get("partyCode"));
			party.store();
			
			String contactMechId = CallcenterServices.updateContactMechTelecomNumber(dispatcher, delegator, (String)context.get("contactNumberId"), "TELECOM_NUMBER",
					"PRIMARY_PHONE", context.get("contactNumber"), context.get("groupName"), partyId, userLogin);
			CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, contactMechId, "PHONE_SHIPPING", partyId, userLogin);
			
			CallcenterServices.updateContactMechEmail(dispatcher, delegator, (String)context.get("infoStringId"), "EMAIL_ADDRESS",
					context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);
			
			if (UtilValidate.isEmpty(context.get("taxAuthInfosfromDate"))) {
				dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM",
						"taxAuthPartyId", "VNM_TAX", "partyTaxId", context.get("taxAuthInfos"), "userLogin", userLogin));
			} else {
				Long taxAuthInfosfromDate = (Long) context.get("taxAuthInfosfromDate");
				dispatcher.runSync("updatePartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM",
						"taxAuthPartyId", "VNM_TAX", "partyTaxId", context.get("taxAuthInfos"),
						"fromDate", new Timestamp(taxAuthInfosfromDate), "userLogin", userLogin));
			}
			DistributorServices.updateRepresentative(delegator, dispatcher, partyId, (String) context.get("representative"), "CUSTOMER", userLogin);

			if (UtilValidate.isNotEmpty(context.get("representativeOfficeId"))) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdFrom", "CUSTOMER",
						"roleTypeIdTo", "CHILD_MEMBER", "partyRelationshipTypeId", "OWNER")));
				List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				boolean exists = false;
				for (GenericValue x : partyRelationships) {
					if (x.get("partyIdFrom").equals(context.get("representativeOfficeId"))) {
						exists = true;
						continue;
					}
					x.set("thruDate", new Timestamp(System.currentTimeMillis()));
					x.store();
				}
				if (!exists) {
					conditions.clear();
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", organizationId, "partyIdFrom", partyId, "roleTypeIdFrom", "CUSTOMER",
							"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "CUSTOMER_REL")));
					List<GenericValue> partyRelationships2 = delegator.findList("PartyRelationship",
							EntityCondition.makeCondition(conditions), null, null, null, false);
					for (GenericValue x : partyRelationships2) {
						x.set("thruDate", new Timestamp(System.currentTimeMillis()));
						x.store();
					}
					dispatcher.runSync("createPartyRelationship",
							UtilMisc.toMap(
									"partyIdFrom", context.get("representativeOfficeId"), "partyIdTo", partyId,
									"roleTypeIdFrom", "CUSTOMER", "roleTypeIdTo", "CHILD_MEMBER",
									"partyRelationshipTypeId", "OWNER", "userLogin", userLogin));
				}
			}

			if (UtilValidate.isNotEmpty(context.get("supervisorId"))) {
				dispatcher.runSync("updateSupervisorMTCustomer",
						UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", context.get("supervisorId"), "userLogin", userLogin));
			}
			if (UtilValidate.isNotEmpty(context.get("salesmanId"))) {
				dispatcher.runSync("updateSalesmanProvideAgent",
						UtilMisc.toMap("partyIdTo", partyId, "partyIdFrom", context.get("salesmanId"), "userLogin", userLogin));
			}
			dispatcher.runSync("createOrUpdatePartyCustomer",
                    UtilMisc.toMap("partyId", partyId, "supervisorId", context.get("supervisorId"), "salesmanId", context.get("salesmanId"),
							 "partyCode", context.get("partyCode"), "fullName", context.get("groupName"), "userLogin", userLogin));
			result.put("partyId", partyId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> rejectMTCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String partyId = (String) context.get("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, partyId));
			List<GenericValue> agreements = delegator.findList("Agreement",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(agreements)) {
				Locale locale = (Locale) context.get("locale");
				throw new HasContractException(UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMTCustomerHasContract", locale));
			}
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, partyId),
					EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.EQUALS, partyId)
					), EntityJoinOperator.OR));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.NOT_EQUAL, "OWNER"));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : partyRelationships) {
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				x.store();
			}
		} catch (HasContractException e) {
			result = ServiceUtil.returnError(e.getMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> setMTStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
	    Delegator delegator = ctx.getDelegator();
	    String partyId = (String) context.get("partyId");
	    String statusId = (String) context.get("statusId");
	    Locale locale = (Locale) context.get("locale");
	    try {
	    	GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyId), false);
	        if (partyCustomer.get("statusId") == null) { // old records
	        	partyCustomer.set("statusId", "PARTY_ENABLED");
	        } else {
	        	partyCustomer.set("statusId", statusId);
	        } 
	        partyCustomer.store();
	        ctx.getDispatcher().runSync("setPartyStatus", context);
	        } catch (GenericEntityException e1) {
	            Debug.logError(e1, e1.getMessage(), module);
	            return ServiceUtil.returnError(e1.getMessage() );
	        } catch (GenericServiceException e2) {
	            Debug.logError(e2, e2.getMessage(), module);
	            return ServiceUtil.returnError( e2.getMessage());
	        }
	    Map<String, Object> results = ServiceUtil.returnSuccess();
	    return results;
	}
	public static Map<String, Object> updateSupervisorMTCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object partyIdTo = context.get("partyIdTo"); //supervisorId
			Object partyIdFrom = context.get("partyIdFrom"); //customerId
            String managerId = (String)partyIdTo;
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom, "roleTypeIdFrom", "CUSTOMER",
					"roleTypeIdTo", "SALESSUP_DEPT", "partyRelationshipTypeId", "CUSTOMER_REL")));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationDmsLog",
							EntityCondition.makeCondition(conditions), null, null, null, false);
			boolean exists = false;
			List<String> departmentIds = PartyUtil.getDepartmentOfEmployee(delegator, (String) partyIdTo, UtilDateTime.nowTimestamp());
			if(UtilValidate.isNotEmpty(departmentIds)) managerId = departmentIds.get(0);
                for (GenericValue x : partyRelationships) {
                    if (x.get("partyIdTo").equals(managerId)) {
                        exists = true;
                        continue;
                    }
                    x.set("thruDate", new Timestamp(System.currentTimeMillis()));
                    x.store();
                }
			if (!exists) {
				LocalDispatcher dispatcher = ctx.getDispatcher();
				dispatcher.runSync("createPartyRelationDmsLog",
						UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", managerId, "roleTypeIdFrom", "CUSTOMER",
								"roleTypeIdTo", "SALESSUP_DEPT", "partyRelationshipTypeId", "CUSTOMER_REL",
                                "logTypeId", "CUSTOMER_SUP","userLogin", context.get("userLogin")));
				
				//	thruDate all salesman
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyIdFrom, "roleTypeIdFrom", "SALES_EXECUTIVE",
						"roleTypeIdTo", "CUSTOMER", "partyRelationshipTypeId", "SALES_REP_REL")));
				partyRelationships = delegator.findList("PartyRelationDmsLog",
								EntityCondition.makeCondition(conditions), null, null, null, false);
				if (partyRelationships.size()>0)
					for (GenericValue x : partyRelationships) {
						x.set("thruDate", new Timestamp(System.currentTimeMillis()));
						x.store();
					}
                GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyIdFrom), false);
                partyCustomer.set("supervisorId", partyIdTo);
                partyCustomer.set("salesmanId", null);
                partyCustomer.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> loadMTCustomerInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> MTCustomerInfo = FastMap.newInstance();
			String partyId = (String) context.get("partyId");
			GenericValue partyGroup = delegator.findOne("PartyAndPartyGroupDetail", UtilMisc.toMap("partyId", partyId), false);
			MTCustomerInfo.put("partyId", partyGroup.get("partyId"));
			MTCustomerInfo.put("statusId", partyGroup.get("statusId"));
			MTCustomerInfo.put("partyCode", partyGroup.get("partyCode"));
			MTCustomerInfo.put("partyTypeId", partyGroup.get("partyTypeId"));
			MTCustomerInfo.put("groupName", partyGroup.get("groupName"));
			MTCustomerInfo.put("officeSiteName", partyGroup.get("officeSiteName"));
			MTCustomerInfo.put("logoImageUrl", partyGroup.get("logoImageUrl"));
			MTCustomerInfo.put("comments", partyGroup.get("comments"));
			MTCustomerInfo.put("currencyUomId", partyGroup.get("preferredCurrencyUomId"));

			List<EntityCondition> conditions = FastList.newInstance();
			//	get taxAuthInfos
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM", "taxAuthPartyId", "VNM_TAX")));
			List<GenericValue> partyTaxAuthInfos = delegator.findList("PartyTaxAuthInfo",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyTaxId", "fromDate"), null, null, false);
			if (UtilValidate.isNotEmpty(partyTaxAuthInfos)) {
				GenericValue partyTaxAuthInfo = EntityUtil.getFirst(partyTaxAuthInfos);
				MTCustomerInfo.put("taxAuthInfos", partyTaxAuthInfo.get("partyTaxId"));
				MTCustomerInfo.put("taxAuthInfosfromDate", partyTaxAuthInfo.getTimestamp("fromDate").getTime());
			}
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
			List<GenericValue> partyAndContactMechs = delegator.findList("PartyAndContactMech",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : partyAndContactMechs) {
				switch (x.getString("contactMechTypeId")) {
				case "TELECOM_NUMBER":
					MTCustomerInfo.put("contactNumber", x.get("tnContactNumber"));
					MTCustomerInfo.put("contactNumberId", x.get("contactMechId"));
					break;
				case "EMAIL_ADDRESS":
					MTCustomerInfo.put("infoString", x.get("infoString"));
					MTCustomerInfo.put("infoStringId", x.get("contactMechId"));
					break;
				case "POSTAL_ADDRESS":
					MTCustomerInfo.put("address1", x.get("paAddress1"));
					if ("Y".equals(context.get("detail"))) {
						MTCustomerInfo.put("wardGeoId", DistributorServices.getGeoName(delegator, x.get("paWardGeoId")));
						MTCustomerInfo.put("districtGeoId", DistributorServices.getGeoName(delegator, x.get("paDistrictGeoId")));
						MTCustomerInfo.put("stateProvinceGeoId", DistributorServices.getGeoName(delegator, x.get("paStateProvinceGeoId")));
						MTCustomerInfo.put("countryGeoId", DistributorServices.getGeoName(delegator, x.get("paCountryGeoId")));
						GenericValue postalAddress = delegator.findOne("PostalAddressAndGeo", UtilMisc.toMap("contactMechId", x.get("contactMechId")), false);
						if (UtilValidate.isNotEmpty(postalAddress)) {
							String address = postalAddress.getString("address1") + ", " + 
									postalAddress.getString("wardGeoName") + ", " + 
									postalAddress.getString("districtGeoName") + ", " + 
									postalAddress.getString("stateProvinceGeoName") + ", " + 
									postalAddress.getString("countryGeoName");
							if (UtilValidate.isNotEmpty(address)) {
								address = address.replaceAll(", null, ", ", ");
								address = address.replaceAll(", null,", ", ");
							}
							MTCustomerInfo.put("address", address);
						}
					} else {
						MTCustomerInfo.put("wardGeoId", x.get("paWardGeoId"));
						MTCustomerInfo.put("districtGeoId", x.get("paDistrictGeoId"));
						MTCustomerInfo.put("stateProvinceGeoId", x.get("paStateProvinceGeoId"));
						MTCustomerInfo.put("countryGeoId", x.get("paCountryGeoId"));
						MTCustomerInfo.put("addressId", x.get("contactMechId"));
					}
					break;
				default:
					break;
				}
			}
			
			//	get representative
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", "CUSTOMER",
					"roleTypeIdFrom", "OWNER", "partyRelationshipTypeId", "OWNER")));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
			if (UtilValidate.isNotEmpty(partyRelationships)) {
				String representativeId = (String) EntityUtil.getFirst(partyRelationships).get("partyIdFrom");
				MTCustomerInfo.put("representative", DistributorServices.getRepresentative(delegator, representativeId, context.get("detail")));
			}
			//	get representative offices
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", "CHILD_MEMBER",
					"roleTypeIdFrom", "CUSTOMER", "partyRelationshipTypeId", "OWNER")));
			List<GenericValue> partyRelationshipOffices = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
			if (UtilValidate.isNotEmpty(partyRelationshipOffices)) {
				String representativeOfficeId = (String) EntityUtil.getFirst(partyRelationshipOffices).get("partyIdFrom");
				MTCustomerInfo.put("representativeOfficeId", representativeOfficeId);
			}
			// get productStoreId
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER")));
			List<GenericValue> productStores = delegator.findList("ProductStoreRole",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("productStoreId"), null, null, false);
			if (UtilValidate.isNotEmpty(productStores)) {
				String productStoreId = (String) EntityUtil.getFirst(productStores).get("productStoreId");
				MTCustomerInfo.put("productStoreId", productStoreId);
			}
			//	get supervisor
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("partyId", partyId));
			List<GenericValue> partyCustomers = delegator.findList("PartyCustomer", EntityCondition.makeCondition(conditions), null,null, null, false);
			if (partyCustomers.size()>0) {
				String supervisorId = partyCustomers.get(0).getString("supervisorId");
				String salesmanId = partyCustomers.get(0).getString("salesmanId");
				if (UtilValidate.isNotEmpty(supervisorId)) {
					MTCustomerInfo.put("supervisorId", supervisorId);
					MTCustomerInfo.put("supervisor", PartyUtil.getPartyName(delegator, supervisorId));
				}
				if (UtilValidate.isNotEmpty(salesmanId)) {
					MTCustomerInfo.put("salesmanId", salesmanId);
				}
			}
			MTCustomerInfo.putAll(getSalesman(delegator, partyId));
			result.put("MTCustomerInfo", MTCustomerInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> getRepresentativeOfAgent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			String partyId = (String) context.get("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", "CUSTOMER",
					"roleTypeIdFrom", "OWNER", "partyRelationshipTypeId", "OWNER")));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
			if (UtilValidate.isNotEmpty(partyRelationships)) {
				String representativeId = (String) EntityUtil.getFirst(partyRelationships).get("partyIdFrom");
				result.put("representative", DistributorServices.getRepresentative(delegator, representativeId, context.get("detail")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> createNotifyMarketRequirement(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			GenericValue userLogin = (GenericValue)context.get("userLogin");
			Object requirementId = context.get("requirementId");
			GenericValue requirement = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
			List<String> parties = managers(delegator, userLogin);
			Locale locale = (Locale) context.get("locale");
			String header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "HaveRequestWithrawalFromMarket", locale) + " [" + requirementId + "]";
			String action = "viewDisRequirementDetail";
			if ("IMPORT_FROM_DEPOSIT_WAREHOUSES".equals(requirement.get("reasonEnumId"))) {
				if ("REQRETURN_ASMAPPROVED".equals(requirement.get("statusId"))) {
					if (MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin) || MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin)) {
						parties = managers(delegator, userLogin, "SALES_MANAGER");
					}
				}
				header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "HaveRequestTransferFromDipositWarehouse", locale) + " [" + requirementId + "]";
				action = "viewTransferRequirementDetail";
			}
			dispatcher.runSync("createNotification",
					UtilMisc.toMap("partiesList", parties, "targetLink", "requirementId=" + requirementId, "sendToSender", "Y",
							"action", action, "header", header, "ntfType", "ONE", "userLogin", userLogin));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static List<String> managers(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
		List<String> parties = PartyUtil.getManagerOfEmpl(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()), userLogin.getString("userLoginId"));
		List<String> roles = SecurityUtil.getCurrentRoles(userLogin.getString("partyId"), delegator);
		if (!roles.contains("MANAGER")) {
			List<String> managers = FastList.newInstance();
			for (String s : parties) {
				managers.addAll(PartyUtil.getManagerOfEmpl(delegator, s, new Timestamp(System.currentTimeMillis()), PartyUtil.getUserLoginByParty(delegator, s).getString("userLoginId")));
			}
			return managers;
		}
		return parties;
	}
	private static List<String> managers(Delegator delegator, GenericValue userLogin, String groupId) throws GenericEntityException {
		List<String> parties = PartyUtil.getManagerOfEmpl(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()), userLogin.getString("userLoginId"));
		List<String> roles = SecurityUtil.getCurrentRoles(userLogin.getString("partyId"), delegator);
		if (!roles.contains("MANAGER")) {
			List<String> managers = FastList.newInstance();
			for (String s : parties) {
				managers.addAll(PartyUtil.getManagerOfEmpl(delegator, s, new Timestamp(System.currentTimeMillis()), PartyUtil.getUserLoginByParty(delegator, s).getString("userLoginId")));
			}
			return filterBySecurityGroupId(delegator, managers, groupId);
		}
		return filterBySecurityGroupId(delegator, parties, groupId);
	}
	private static List<String> filterBySecurityGroupId(Delegator delegator, List<String> row, String groupId) throws GenericEntityException {
		List<String> parties = FastList.newInstance();
		if (UtilValidate.isNotEmpty(row)) {
			for (String s : row) {
				if (MTLUtil.partyHasSecurityGroupPermission(delegator, groupId, s)) {
					parties.add(s);
				}
			}
			if (UtilValidate.isEmpty(parties)) {
				List<String> managers = FastList.newInstance();
				for (String s : row) {
					managers.addAll(PartyUtil.getManagerOfEmpl(delegator, s, new Timestamp(System.currentTimeMillis()), PartyUtil.getUserLoginByParty(delegator, s).getString("userLoginId")));
				}
				return filterBySecurityGroupId(delegator, managers, groupId);
			}
		}
		return parties;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listDisRequirements(DispatchContext ctx, Map<String,?extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	try {
    		GenericValue userLogin = (GenericValue)context.get("userLogin");
    		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        	List<String> listSortFields = (List<String>) context.get("listSortFields");
        	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        	List<String> currentDeparments = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
        	List<Object> departmentsRollup = FastList.newInstance();
    		for (String s : currentDeparments) {
    			Organization buildOrg = PartyUtil.buildOrg(delegator, s, true, false);
    			departmentsRollup.add(s);
    			List<String> departmentsChild = EntityUtil.getFieldListFromEntityList(
    					buildOrg.getAllDepartmentList(delegator), "partyId", true);
    			if (UtilValidate.isNotEmpty(departmentsChild)) {
    				departmentsRollup.addAll(departmentsChild);
    			}
    		}
    		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    		if (parameters.containsKey("requirementTypeId")) {
    			if (parameters.containsKey("reasonEnumId") && UtilValidate.isNotEmpty(parameters.get("reasonEnumId")[0])) {
    				listAllConditions.add(EntityCondition.makeCondition("reasonEnumId", EntityJoinOperator.EQUALS, parameters.get("reasonEnumId")[0]));
				} else {
					if (UtilValidate.isNotEmpty(departmentsRollup)) {
						listAllConditions.add(EntityCondition.makeCondition("departmentPartyId", EntityJoinOperator.IN, departmentsRollup));
					}
				}
    			listAllConditions.add(EntityCondition.makeCondition("requirementTypeId", EntityJoinOperator.EQUALS, parameters.get("requirementTypeId")[0]));
        		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
        		EntityListIterator listIterator = delegator.find("RequirementPartyDetail",
        				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        		result.put("listIterator", listIterator);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	return result;
    }
	public static Map<String, Object> setRequirementStatus(DispatchContext ctx, Map<String,?extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue)context.get("userLogin");
			boolean createNotifyAscent = true;
			String statusId = (String) context.get("statusId");
			Object requirementId = context.get("requirementId");
			Object reasonEnumId = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false).get("reasonEnumId");
			//	check Permission
			switch (statusId) {
			case "REQRETURN_ASMAPPROVED":
				if (!(MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, false)
						||MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, false))) {
					throw new Exception(UtilProperties.getMessage("BaseHREmployeeUiLabels", "HaveNoPermitToApprovalBusinessProposal", locale));
				}
				break;
			case "REQRETURN_RSMAPPROVED":
				if (!(MTLUtil.hasSecurityGroupPermission(delegator, "SALES_RSM_GT", userLogin, false)
						||MTLUtil.hasSecurityGroupPermission(delegator, "SALES_RSM_MT", userLogin, false))) {
					throw new Exception(UtilProperties.getMessage("BaseHREmployeeUiLabels", "HaveNoPermitToApprovalBusinessProposal", locale));
				}
				break;
			case "REQRETURN_CSMAPPROVED":
				if (!(MTLUtil.hasSecurityGroupPermission(delegator, "SALES_CSM_GT", userLogin, false)
						||MTLUtil.hasSecurityGroupPermission(delegator, "SALES_CSM_MT", userLogin, false))) {
					throw new Exception(UtilProperties.getMessage("BaseHREmployeeUiLabels", "HaveNoPermitToApprovalBusinessProposal", locale));
				}
				break;
			case "REQRETURN_SMAPPROVED":
				if (!(MTLUtil.hasSecurityGroupPermission(delegator, "SALES_MANAGER", userLogin, false))) {
					throw new Exception(UtilProperties.getMessage("BaseHREmployeeUiLabels", "HaveNoPermitToApprovalBusinessProposal", locale));
				}
				break;
			case "REQRETURN_APPROVED":
				if ("RETURN_OUT_OF_DATE_REQ".equals(reasonEnumId) || "IMPORT_FROM_DEPOSIT_WAREHOUSES".equals(reasonEnumId)) {
					if ("IMPORT_FROM_DEPOSIT_WAREHOUSES".equals(reasonEnumId)) {
						//	send notify to Log
						String header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "HaveRequestTransferFromDipositWarehouse", locale) + " [" + requirementId + "]";
						String roleTypeId = EntityUtilProperties.getPropertyValue("basesales.properties", "roleTypeId.receiveMsg.returnorder.approved", delegator);
						List<String> parties = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, roleTypeId, delegator);
						dispatcher.runSync("createNotification",
								UtilMisc.toMap("partiesList", parties, "targetLink", "requirementId=" + requirementId, "sendToSender", "Y",
										"action", "viewRequirementDetail", "header", header, "ntfType", "ONE", "userLogin", userLogin));
					}
					if (!(MTLUtil.hasSecurityGroupPermission(delegator, "HRMADMIN", userLogin, false))) {
						throw new Exception(UtilProperties.getMessage("BaseHREmployeeUiLabels", "HaveNoPermitToApprovalBusinessProposal", locale));
					}
				} else {
					if (!(MTLUtil.hasSecurityGroupPermission(delegator, "SALES_MANAGER", userLogin, false))) {
						throw new Exception(UtilProperties.getMessage("BaseHREmployeeUiLabels", "HaveNoPermitToApprovalBusinessProposal", locale));
					}
				}
				createNotifyAscent = false;
				break;
			case "REQRETURN_CANCELLED":
				createNotifyAscent = false;
				break;
			case "REQRETURN_COMPLETED":
				createNotifyAscent = false;
				break;
			default:
				break;
			}
			//	updateRequirement
			dispatcher.runSync("updateRequirement", UtilMisc.toMap("requirementId", requirementId, "statusId", statusId, "userLogin", userLogin));
			//	createNotifyMarketRequirement
			if (createNotifyAscent) {
				dispatcher.runSync("createNotifyMarketRequirement", UtilMisc.toMap("requirementId", requirementId, "userLogin", userLogin));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> executeWithdrawalRequirement(DispatchContext ctx, Map<String,?extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue)context.get("userLogin");
			if (!(MTLUtil.hasSecurityGroupPermission(delegator, "HRMADMIN", userLogin, false))) {
				throw new Exception(UtilProperties.getMessage("BaseHREmployeeUiLabels", "HaveNoPermitToApprovalBusinessProposal", locale));
			}
			Object distributorId = context.get("distributorId");
			Object requirementId = context.get("requirementId");
			Object destinationFacilityId = context.get("destinationFacilityId");
			String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			GenericValue requirement = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
			Object requirementIdCANCEL = null;
			if ("Destroyed".equals(context.get("type")) && UtilValidate.isNotEmpty(destinationFacilityId)) {
				//	auto createRequirement EXPORT_CANCEL
				result = dispatcher.runSync("createRequirement",
						UtilMisc.toMap("requirementTypeId", "EXPORT_REQUIREMENT", "facilityId", destinationFacilityId, "statusId", "REQ_APPROVED",
								"currencyUomId", requirement.get("currencyUomId"), "reasonEnumId", "EXPORT_CANCEL", "requirementStartDate",
								new Timestamp(System.currentTimeMillis()), "requiredByDate", new Timestamp(System.currentTimeMillis()),
								"userLogin", userLogin));
				requirementIdCANCEL = result.get("requirementId");
				
				// update requirement role
		    	GenericValue requirementRole = delegator.makeValue("RequirementRole");
		    	requirementRole.put("requirementId", requirementIdCANCEL);
		    	requirementRole.put("roleTypeId", "INTERNAL_ORGANIZATIO");
		    	requirementRole.put("partyId", companyId);
		    	requirementRole.put("fromDate", UtilDateTime.nowTimestamp());
		    	delegator.createOrStore(requirementRole);
			}
			//	create ReturnHeader
			result = dispatcher.runSync("createReturnHeader",
					UtilMisc.toMap("returnHeaderTypeId", "CUSTOMER_RETURN", "statusId", "RETURN_REQUESTED", "createdBy", userLogin.get("userLoginId"),
							"fromPartyId", distributorId, "toPartyId", companyId, "entryDate", new Timestamp(System.currentTimeMillis()),
							"destinationFacilityId", destinationFacilityId, "needsInventoryReceive", "Y", "currencyUomId", requirement.get("currencyUomId"),
							"userLogin", userLogin));
			Object returnId = result.get("returnId");
			List<GenericValue> requirementItems = delegator.findList("RequirementItem",
					EntityCondition.makeCondition("requirementId", EntityJoinOperator.EQUALS, requirementId), null, null, null, false);
			//	createReturnItem
			for (GenericValue x : requirementItems) {
				result = dispatcher.runSync("createReturnItemWithoutOrder",
						UtilMisc.toMap("returnId", returnId, "returnPrice", x.getBigDecimal("unitCost"), "returnReasonId", "RTN_EXPIRED", "returnTypeId", "RTN_REFUND", "returnItemTypeId",
								"RET_FPROD_ITEM", "productId", x.get("productId"), "description", x.get("description"), "statusId", "RETURN_REQUESTED",
								"expectedItemStatus", "INV_RETURNED", "returnQuantity", x.getBigDecimal(("quantity")), "quantityUomId", x.get("quantityUomId"),
								"userLogin", userLogin));
				Object returnItemSeqId = result.get("returnItemSeqId");
				//	createReturnRequirementCommitment
				dispatcher.runSync("createReturnRequirementCommitment",
						UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId, "requirementId", requirementId, "reqItemSeqId",
								x.get("reqItemSeqId"), "quantity", x.getBigDecimal(("quantity")), "userLogin", userLogin));
				if ("Destroyed".equals(context.get("type"))) {
					//	auto createRequirementItem EXPORT_CANCEL
					result = dispatcher.runSync("createRequirementItem",
							UtilMisc.toMap("requirementId", requirementIdCANCEL, "productId", x.get("productId"), "statusId", "REQ_APPROVED", "quantity", x.getBigDecimal(("quantity")),
									"currencyUomId", requirement.get("currencyUomId"), "quantityUomId", x.get("quantityUomId"), "unitCost", x.getBigDecimal("unitCost"),
									"description", x.get("description"), "userLogin", userLogin));
					Object reqItemSeqIdCANCEL = result.get("reqItemSeqId");
					//	createRequirementItemAssoc
					dispatcher.runSync("createRequirementItemAssoc",
							UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", x.get("reqItemSeqId"), "toRequirementId",
									requirementIdCANCEL, "toReqItemSeqId", reqItemSeqIdCANCEL, "reqItemAssocTypeId", "EXECUTION",
									"quantity", x.getBigDecimal(("quantity")), "userLogin", userLogin));
				}
			}
			
			//	setRequirementStatus
			dispatcher.runSync("setRequirementStatus",
					UtilMisc.toMap("requirementId", requirementId, "statusId", "REQRETURN_COMPLETED", "userLogin", userLogin));
			if ("Destroyed".equals(context.get("type")) && UtilValidate.isNotEmpty(requirementIdCANCEL)){
				try {
					dispatcher.runSync("quickCreateDeliveryFromRequirement", UtilMisc.toMap("userLogin", (GenericValue)context.get("userLogin"), "requirementId", requirementIdCANCEL));
				} catch (GenericServiceException e){
					return ServiceUtil.returnError("OLBIUS: runsync quickCreateDeliveryFromRequirement service error!");
				}
			}
			
			if ("Destroyed".equals(context.get("type"))) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", distributorId, "roleTypeIdFrom", "SALESSUP_DEPT",
						"roleTypeIdTo", "DISTRIBUTOR", "partyRelationshipTypeId", "DISTRIBUTION")));
				List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
								EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
				List<String> parties = EntityUtil.getFieldListFromEntityList(partyRelationships, "partyIdFrom", true);
				String header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "NewReturnOrderNotifyForSales", locale) + " [" + returnId + "]";
				dispatcher.runSync("createNotification",
						UtilMisc.toMap("partiesList", parties, "targetLink", "returnId=" + returnId, "sendToSender", "Y",
								"action", "CustomerReturnDetailForSup", "header", header, "ntfType", "ONE", "userLogin", userLogin));
				
				header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "ThereAreRequestRemoveProductFromaDepositWarehouse", locale) + " [" + requirementId + "]";
				dispatcher.runSync("createNotification",
						UtilMisc.toMap("partiesList", parties, "targetLink", "requirementId=" + requirementId, "sendToSender", "Y",
								"action", "viewRemoveRequirementDetail", "header", header, "ntfType", "ONE", "userLogin", userLogin));
			} else {
				String header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "NewReturnOrderNotifyForLog", locale) + " [" + returnId + "]";
				String roleTypeId = EntityUtilProperties.getPropertyValue("basesales.properties", "roleTypeId.receiveMsg.returnorder.approved", delegator);
				List<String> parties = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, roleTypeId, delegator);
				dispatcher.runSync("createNotification",
						UtilMisc.toMap("partiesList", parties, "targetLink", "returnId=" + returnId, "sendToSender", "Y",
								"action", "viewReturnOrder", "header", header, "ntfType", "ONE", "userLogin", userLogin));
			}
			result.clear();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductReturn(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	       	List<String> listSortFields = (List<String>) context.get("listSortFields");
	       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	       	GenericValue userLogin = (GenericValue)context.get("userLogin");
	       	listAllConditions.add(EntityCondition.makeCondition("destinationFacilityId", EntityJoinOperator.IN, getDepositFacilityBySup(delegator, userLogin)));
	       	listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("returnHeaderTypeId", "CUSTOMER_RETURN", "statusId", "RETURN_REQUESTED")));
	       	EntityListIterator listIterator = delegator.find("ReturnHeaderDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
	    	result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
	}
	public static List<String> getDepositFacilityBySup(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, DistributorServices.distributorOfSupervisor(delegator, userLogin)));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", companyId)));
		List<GenericValue> facilities = delegator.findList("FacilityAndFacilityParty",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("facilityId"), null, null, false);
		return EntityUtil.getFieldListFromEntityList(facilities, "facilityId", true);
	}
	
	public static Map<String, Object> updateReturnHeaderForSales(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> fakeMap = FastMap.newInstance();
			fakeMap.putAll(context);
			fakeMap.put("userLogin", system);
			result = dispatcher.runSync("updateReturnHeader", fakeMap);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
    	return result;
	}
	public static Map<String, Object> salesReceiveReturn(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			//	logisticsReceiveReturn
			result = dispatcher.runSync("logisticsReceiveReturn", context);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCheckInventoryAgents(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	try {
    		GenericValue userLogin = (GenericValue)context.get("userLogin");
    		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        	List<String> listSortFields = (List<String>) context.get("listSortFields");
        	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        	
        	if (MTLUtil.hasSecurityGroupPermission(delegator, "DISTRIBUTOR_ADMIN", userLogin, false)) {
        		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentsOfDistributor(delegator, userLogin.get("partyId"))));
			} else  if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false)) {
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentOfSalesman(delegator, userLogin)));
			} else if (!MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, true) || !MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, true)) {
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentOfSupervisor(delegator, userLogin)));
			}
        	
    		EntityListIterator listIterator = delegator.find("CustomerHasInventoryDetail",
    				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	return result;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCheckInventoryAgentsUpGrade(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	try {
    		GenericValue userLogin = (GenericValue)context.get("userLogin");
    		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        	List<String> listSortFields = (List<String>) context.get("listSortFields");
        	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        	
        	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        	
        	if (MTLUtil.hasSecurityGroupPermission(delegator, "DISTRIBUTOR_ADMIN", userLogin, false)) {
        		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentsOfDistributor(delegator, userLogin.get("partyId"))));
			} else  if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false)) {
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentOfSalesman(delegator, userLogin)));
			} else if (!MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, true) || !MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, true)) {
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentOfSupervisor(delegator, userLogin)));
			}
			listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", "SMCHANNEL_GT"));
        	listSortFields.add("-fromDate");
        	
    		EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "InventoryOfCustomerDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	return result;
    }

    @SuppressWarnings("unchecked")
	public static Map<String, Object> listCheckInventoryMT(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	try {
    		GenericValue userLogin = (GenericValue)context.get("userLogin");
    		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        	List<String> listSortFields = (List<String>) context.get("listSortFields");
        	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");

            listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", "SMCHANNEL_MT"));
        	listSortFields.add("-fromDate");
    		EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "InventoryOfCustomerDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	return result;
    }
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listInventoryOfAgents(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts =(EntityFindOptions) context.get("opts");
			
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			String partyId = parameters.get("partyId")[0];
			
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
			listSortFields.add("-fromDate");
			EntityListIterator listIterator = delegator.find("InventoryOfCustomerDetail",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<GenericValue> getCostAccBase(Delegator delegator, GenericValue userLogin, String invoiceItemTypeId) throws GenericEntityException {
		String userLoginId = userLogin.getString("userLoginId");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		List<String> departmentIds = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
		List<EntityCondition> conditions = FastList.newInstance();
		if (UtilValidate.isNotEmpty(invoiceItemTypeId)) {
			conditions.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityJoinOperator.EQUALS, invoiceItemTypeId));
		}
		conditions.add(EntityCondition.makeCondition("organizationPartyId", EntityJoinOperator.EQUALS, organizationPartyId));
		conditions.add(EntityCondition.makeCondition("departmentId", EntityJoinOperator.IN, departmentIds));
		List<GenericValue> costAccMapDepartmentDetails = delegator.findList("CostAccMapDepartmentDetail",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		return costAccMapDepartmentDetails;
	}
	public static List<String> getAllTotalChildInvoiceItemTypeId(Delegator delegator, String rootTypeId, List<String> listChilds) throws GenericEntityException{
		List<GenericValue> listTmp = new ArrayList<GenericValue>();
		listTmp = delegator.findList("InvoiceItemType", EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", rootTypeId)), null, null, null, false);
		if (listTmp.isEmpty()){
			GenericValue itemType = delegator.findOne("InvoiceItemType", false, UtilMisc.toMap("invoiceItemTypeId", rootTypeId));
			if (UtilValidate.isNotEmpty(itemType) && itemType.get("defaultGlAccountId") != null){
				listChilds.add(rootTypeId);
			}
		} else {
			GenericValue itemType = delegator.findOne("InvoiceItemType", false, UtilMisc.toMap("invoiceItemTypeId", rootTypeId));
			if (UtilValidate.isNotEmpty(itemType) && itemType.get("defaultGlAccountId") != null){
				listChilds.add(rootTypeId);
			}
			for (GenericValue item : listTmp){
				getAllTotalChildInvoiceItemTypeId(delegator, item.getString("invoiceItemTypeId"), listChilds);
			}
		}
		return listChilds;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listTempAgents(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("customerId");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("customerName", EntityJoinOperator.NOT_EQUAL, null));
			EntityListIterator listIterator = delegator.find("TempAgent",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e){
			e.printStackTrace();
		}
    	return result;
    }
	
	public static Map<String, Object> importAgent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			
			EntityListIterator tempAgents = delegator.find("TempAgent",
					EntityCondition.makeCondition("customerName", EntityJoinOperator.NOT_EQUAL, null), null, null, null, null);
			GenericValue customer = null;
			Map<String, Object> mapCreateAgent = FastMap.newInstance();
			while((customer = tempAgents.next()) != null) {
				String partyId = "RO" + organizationId + delegator.getNextSeqId("Party");
				mapCreateAgent.clear();
				mapCreateAgent.put("partyId", partyId);
				mapCreateAgent.put("groupName", customer.get("customerName"));
				mapCreateAgent.put("groupNameLocal", customer.get("customerName"));
				mapCreateAgent.put("officeSiteName", customer.get("website"));
				mapCreateAgent.put("comments", customer.get("comments"));
				mapCreateAgent.put("partyTypeId", "RETAIL_OUTLET");
				mapCreateAgent.put("statusId", "PARTY_DISABLED");
				mapCreateAgent.put("preferredCurrencyUomId", "VND");
				dispatcher.runSync("createPartyGroup", mapCreateAgent);
				delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", partyId),
						EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
				
				//	createPartyRole
				List<String> roles = Arrays.asList("AGENT", "CUSTOMER", "BILL_TO_CUSTOMER", "END_USER_CUSTOMER", "PLACING_CUSTOMER", "SHIP_TO_CUSTOMER");
				for (String s : roles) {
					delegator.create("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", s));
				}
				createAgentRelationship(dispatcher, partyId, organizationId, userLogin);
				
				String contactMechId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
						"POSTAL_ADDRESS", customer.get("customerName"), customer.get("customerName"),
						"SHIPPING_LOCATION", partyId, customer.get("address1"),
						customer.get("countryGeoId"), customer.get("stateProvinceGeoId"),
						customer.get("districtGeoId"), customer.get("wardGeoId"),
						customer.get("stateProvinceGeoId"), "70000", null, userLogin);
				if (UtilValidate.isNotEmpty(contactMechId)) {
					CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, contactMechId, "POSTAL_ADDRESS",
							customer.get("customerName"), customer.get("customerName"), "PRIMARY_LOCATION", partyId,
							customer.get("address1"), customer.get("countryGeoId"),
							customer.get("stateProvinceGeoId"), customer.get("districtGeoId"),
							customer.get("wardGeoId"), customer.get("stateProvinceGeoId"), "70000", null, userLogin);
				}
				contactMechId = CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE",
						customer.get("phoneNumber"), customer.get("customerName"), partyId, userLogin);
				if (UtilValidate.isNotEmpty(contactMechId)) {
					CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, contactMechId, "PHONE_SHIPPING", partyId, userLogin);
				}
				
				CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", (String) customer.get("emailAddress"),
						"PRIMARY_EMAIL", partyId, userLogin);
				
				mapCreateAgent.clear();
				mapCreateAgent.putAll(CallcenterServices.demarcatePersonName(customer.getString("representative")));
				mapCreateAgent.put("partyId", partyId + "WN");
				mapCreateAgent.put("statusId", "PARTY_ENABLED");
				mapCreateAgent.put("birthDate", customer.get("representativeBirthDate"));
				mapCreateAgent.put("userLogin", userLogin);
				String representativeId = (String) dispatcher.runSync("createPerson", mapCreateAgent).get("partyId");
				roles = Arrays.asList("OWNER", "SELLER");
				for (String s : roles) {
					delegator.create("PartyRole", UtilMisc.toMap("partyId", representativeId, "roleTypeId", s));
				}
				dispatcher.runSync("createPartyRelationship",
						UtilMisc.toMap("partyIdFrom", representativeId, "partyIdTo", partyId, "roleTypeIdFrom", "OWNER",
								"roleTypeIdTo", "CUSTOMER", "partyRelationshipTypeId", "OWNER", "userLogin", userLogin));
				
				CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE",
						customer.get("representativePhoneNumber"), customer.get("representative"), representativeId, userLogin);
			}
			result.put("size", String.valueOf(tempAgents.getResultsTotalSize()));
			dispatcher.runSync("deleteAllTempAgent", UtilMisc.toMap("userLogin", userLogin));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> deleteAllTempAgent(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericDataSourceException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		SQLProcessor processor = new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz"));
		try {
			processor.getConnection();
			processor.executeUpdate("DELETE FROM temp_agent");
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		} finally {
			processor.close();
		}
		return result;
	}
	public static Map<String, Object> updateTempAgent(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericDataSourceException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue tempAgent = delegator.makeValidValue("TempAgent", context);
			tempAgent.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> deleteTempAgent(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericDataSourceException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue tempAgent = delegator.makeValidValue("TempAgent", context);
			tempAgent.remove();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> listGTSupervisor(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        try {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
            Organization buildOrg = PartyUtil.buildOrg(delegator, companyId, true, false);
            List<String> parties = FastList.newInstance();
            if (MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, true) || MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, true)) {
                parties = EntityUtil.getFieldListFromEntityList(buildOrg.getAllDepartmentList(delegator), "partyId", true);
            } else {
                parties = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
            }
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.IN, parties));
            conds.add(EntityCondition.makeCondition("roleTypeIdTo", "SALESSUP_DEPT_GT"));
            List<GenericValue> supervisorDepts = delegator.findList("PartyRelationship",
                    EntityCondition.makeCondition(conds), null, null, null, false);
            List<String> supervisorDeptIds = EntityUtil.getFieldListFromEntityList(supervisorDepts, "partyIdTo", true);
            listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId","MANAGER"));
            listAllConditions.add(EntityCondition.makeCondition("roleTypeIdFrom","MANAGER"));
            listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo","INTERNAL_ORGANIZATIO"));
            listAllConditions.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.IN,supervisorDeptIds));
            EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters,result,delegator,"PartyFromAndPartyNameDetail",
                    EntityCondition.makeCondition(listAllConditions),null,null,listSortFields,opts);
            result.put("listIterator", listIterator);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> listMTSupervisor(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        try {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
            Organization buildOrg = PartyUtil.buildOrg(delegator, companyId, true, false);
            List<String> parties = FastList.newInstance();
            if (MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, true) || MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, true)) {
                parties = EntityUtil.getFieldListFromEntityList(buildOrg.getAllDepartmentList(delegator), "partyId", true);
            } else {
                parties = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
            }
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.IN, parties));
            conds.add(EntityCondition.makeCondition("roleTypeIdTo", "SALESSUP_DEPT_MT"));
            List<GenericValue> supervisorDepts = delegator.findList("PartyRelationship",
                    EntityCondition.makeCondition(conds), null, null, null, false);
            List<String> supervisorDeptIds = EntityUtil.getFieldListFromEntityList(supervisorDepts, "partyIdTo", true);
            listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId","MANAGER"));
            listAllConditions.add(EntityCondition.makeCondition("roleTypeIdFrom","MANAGER"));
            listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo","INTERNAL_ORGANIZATIO"));
            listAllConditions.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.IN,supervisorDeptIds));
            EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters,result,delegator,"PartyFromAndPartyNameDetail",
                    EntityCondition.makeCondition(listAllConditions),null,null,listSortFields,opts);
            result.put("listIterator", listIterator);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> listMTSalesmanBySupervisor(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = (Delegator) ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        EntityFindOptions opt = (EntityFindOptions) context.get("opts");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginPartyId = userLogin.getString("partyId");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator list = null;
        try {
            String supervisorId = null;
            if (parameters.containsKey("quotaTypeId") && parameters.get("quotaTypeId").length > 0) {
                supervisorId = (String) parameters.get("supervisorId")[0];
                if (UtilValidate.isNotEmpty(supervisorId)) {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", supervisorId));
                }
            }
            opt.setDistinct(true);
            if (UtilValidate.isEmpty(listSortFields)) {
                listSortFields.add("partyCode");
            }
            listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"),
                    EntityCondition.makeCondition("statusId", null)), EntityOperator.OR));
            EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
            List<String> fieldToSelect = UtilMisc.toList("partyId", "partyCode", "fullName");
            list = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PartySalesman", cond, null, UtilMisc.toSet(fieldToSelect), listSortFields, opt);
            successResult.put("listIterator", list);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error get listMTSalsemanBySupervisor");
        }
        return successResult;
    }

	public static Map<String, Object> listOpponentInfos(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> listIterator = null;
		String userLoginPartyId = userLogin.getString("partyId");
		try {
			boolean isSearch = true;
			if (SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)
				|| SalesPartyUtil.isSalesManager(delegator, userLoginPartyId)) {
				isSearch = true;
			} else if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
				listAllConditions.add(EntityCondition.makeCondition("createdByPartyId", userLoginPartyId));
			} else if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
				List<String> listSalesmanIds = PartyWorker.getSalesmanIdsBySup(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listSalesmanIds)){
					isSearch = false;
				}else {
					listAllConditions.add(EntityCondition.makeCondition("createdByPartyId", EntityOperator.IN, listSalesmanIds));
				}
			} else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
				List<String> listSupIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listSupIds)) {
					isSearch = false;
				} else {
					listAllConditions.add(EntityCondition.makeCondition("createdByPartyId", EntityOperator.IN, listSupIds));
				}
			} else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
				List<String> listSupIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listSupIds)) {
					isSearch = false;
				} else {
					listAllConditions.add(EntityCondition.makeCondition("createdByPartyId", EntityOperator.IN, listSupIds));
				}
			} else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
				List<String> listSupIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listSupIds)) {
					isSearch = false;
				} else {
					listAllConditions.add(EntityCondition.makeCondition("createdByPartyId", EntityOperator.IN, listSupIds));
				}
			} else {
				isSearch = false;
			}
			if (isSearch) {
				listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "OpponentEventAndPartyGroup", EntityCondition.makeCondition(listAllConditions),
						null, null, listSortFields, opts);
			}
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			String errMsg = "Fatal error calling listOpponentInfos service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return result;
	}

	public static Map<String, Object> loadOpponentInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> OpponentInfo = FastMap.newInstance();
			String opponentEventId = (String) context.get("opponentEventId");
			String partyId = (String) context.get("partyId");
			GenericValue partyGroup = delegator.findOne("OpponentEventAndPartyGroup", UtilMisc.toMap("opponentEventId", opponentEventId, "partyId", partyId), false);
				OpponentInfo.put("opponentEventId", partyGroup.get("opponentEventId"));
				OpponentInfo.put("partyId", partyGroup.get("partyId"));
				OpponentInfo.put("comment", partyGroup.get("comment"));
				OpponentInfo.put("description", partyGroup.get("description"));
				OpponentInfo.put("image", partyGroup.get("image"));
				OpponentInfo.put("createdByPartyId", partyGroup.get("createdByPartyId"));
				OpponentInfo.put("groupName", partyGroup.get("groupName"));
			result.put("OpponentInfo", OpponentInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> getPartyTypeOpponent(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> listIterator = null;
		try {
			listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "PartyTypeOpponent", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static Map<String,Object> submitInfoOpponentSetting(DispatchContext dpct,Map<String, Object> context){
		Delegator delegator = dpct.getDelegator();
		String partyId = (String) context.get("partyId");
		String comment = (String) context.get("comment");
		String description = (String) context.get("description");
		String image = (String) context.get("image");
		Map<String,Object> status = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			GenericValue opp = delegator.makeValue("OpponentEvent");
			String opponentEventId = delegator.getNextSeqId("OpponentEvent");
			opp.set("opponentEventId", opponentEventId);
			opp.set("partyId", partyId);
			opp.set("comment", comment);
			opp.set("description", description);
			opp.set("image", image);
			opp.set("createdByPartyId", userLogin.get("partyId"));
			opp.create();
			status.put("opponentEventId", opponentEventId);
		} catch (Exception e) {
			Debug.logError("can't not create Infomartion of Opponent in DB" + e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return status;
	}

	public static Map<String,Object> submitInfoOpponentType(DispatchContext dpct,Map<String, Object> context){
		Delegator delegator = dpct.getDelegator();
		LocalDispatcher dispatcher = dpct.getDispatcher();
		String partyCode = (String) context.get("partyCode");
		String partyName = (String) context.get("partyName");
		Map<String,Object> status = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, null, context.get("partyCode"));
			//create party
			GenericValue party = delegator.makeValue("Party");
			String partyId = "OP" + delegator.getNextSeqId("Party");
				party.put("partyId", partyId);
				party.put("partyCode", partyCode);
				party.put("partyTypeId", "PARTY_GROUP");
				party.put("statusId", "PARTY_DISABLED");
				party.put("description", partyName);
				party.create();
			//create party_group
			GenericValue partyGroup = delegator.makeValue("PartyGroup");
				partyGroup.put("partyId", partyId);
				partyGroup.put("groupName", partyName);
				partyGroup.create();
			//create party_role
			GenericValue gv = delegator.makeValue("PartyRole");
				gv.put("partyId", partyId);
				gv.put("roleTypeId", "COM_SUPPLIER");
				gv.create();
			status.put("partyId", partyCode);
		} catch (Exception e) {
			Debug.logError("can't not create Opponent type in DB" + e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return status;
	}

	public static Map<String, Object> listAgentSamples(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();

		opts.setLimit(10);
		opts.setMaxRows(10);
		try {
			List<GenericValue> listCustomerGT = delegator.findList("PartyCustomerDetailView", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);

			if (listCustomerGT != null) {
				for (GenericValue itemAgent: listCustomerGT) {
					Map<String, Object> itemMap = itemAgent.getAllFields();
					String personId = itemAgent.getString("partyId")+"WN";
					Map<String, Object> representatives = DistributorServices.getRepresentative(delegator, personId, "Y");
					GenericValue genderItem = delegator.findOne("Gender", UtilMisc.toMap("genderId", representatives.get("gender")), false);
					itemMap.put("representative.partyFullName", representatives.get("partyFullName"));
					itemMap.put("representative.birthDate", representatives.get("birthDate"));
					itemMap.put("representative.gender", genderItem!=null?genderItem.getString("description"):representatives.get("gender"));
					itemMap.put("representative.contactNumber", representatives.containsKey("contactNumber")?representatives.get("contactNumber"):null);
					itemMap.put("representative.infoString", representatives.containsKey("infoString")?representatives.get("infoString"):null);
					listIterator.add(itemMap);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> createAgentImports(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String sizeProducts = parameters.get("sizeCustomers")[0];
			int len = Integer.parseInt(sizeProducts);
			for (int i=0; i < len; i++){
				String sequence = parameters.get("customers["+i+"][sequence]")[0];
				String partyCode = parameters.get("customers["+i+"][partyCode]")[0];
				String groupName = parameters.get("customers["+i+"][groupName]")[0];
				String groupNameLocal = parameters.get("customers["+i+"][groupNameLocal]")[0];
				String contactNumber = parameters.get("customers["+i+"][contactNumber]")[0];
				String infoString = parameters.get("customers["+i+"][infoString]")[0];
				String taxAuthInfos = parameters.get("customers["+i+"][taxAuthInfos]")[0];
				String postalCode = parameters.get("customers["+i+"][postalCode]")[0];
				String distributorId = parameters.get("customers["+i+"][distributorId]")[0];
				String salesmanId = parameters.get("customers["+i+"][salesmanId]")[0];
				String routeId = parameters.get("customers["+i+"][routeId]")[0];
				String toName = parameters.get("customers["+i+"][toName]")[0];
				String attnName = parameters.get("customers["+i+"][attnName]")[0];
				String address1 = parameters.get("customers["+i+"][address1]")[0];
				String wardGeoName = parameters.get("customers["+i+"][wardGeoId]")[0];
				String districtGeoName = parameters.get("customers["+i+"][districtGeoId]")[0];
				String stateProvinceGeoId = parameters.get("customers["+i+"][stateProvinceGeoId]")[0];
				String countryGeoId = parameters.get("customers["+i+"][countryGeoId]")[0];
				String currencyUomId = parameters.get("customers["+i+"][currencyUomId]")[0];
				String visitFrequencyTypeId = parameters.get("customers["+i+"][visitFrequencyTypeId]")[0];
				String representative = parameters.get("customers["+i+"][representative]")[0];
				String districtGeoId = null;
				String wardGeoId = null;
				if (countryGeoId != null && stateProvinceGeoId != null){
					if (districtGeoName != null){
						List<EntityCondition> conds = FastList.newInstance();
						conds.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE, "%" + stateProvinceGeoId+ "%"));
						conds.add(EntityCondition.makeCondition("geoName", districtGeoName));
						conds.add(EntityCondition.makeCondition("geoTypeId", "DISTRICT"));
						List<GenericValue> gvs = delegator.findList("Geo", EntityCondition.makeCondition(conds, EntityOperator.AND), null, null, null, false);
						if (gvs.size() != 0){
							districtGeoId  = gvs.get(0).getString("geoId");
							if (districtGeoName != null) {
								conds.clear();
								conds.add(EntityCondition.makeCondition("geoId", EntityOperator.LIKE, "%" + districtGeoId + "%"));
								conds.add(EntityCondition.makeCondition("geoName", wardGeoName));
								conds.add(EntityCondition.makeCondition("geoTypeId", "WARD"));
								List<GenericValue> gvWards = delegator.findList("Geo", EntityCondition.makeCondition(conds, EntityOperator.AND), null, null, null, false);
								if (gvWards.size() != 0)
									wardGeoId = gvWards.get(0).getString("geoId");
							}
						}
					}
				}

				Map<String, Object> dataCtx = FastMap.newInstance();
				dataCtx.put("partyCode", partyCode);
				dataCtx.put("groupName", groupName);
				dataCtx.put("groupNameLocal", groupNameLocal);
				dataCtx.put("contactNumber", contactNumber);
				dataCtx.put("infoString", infoString);
				dataCtx.put("taxAuthInfos", taxAuthInfos);
				dataCtx.put("postalCode", postalCode);
				dataCtx.put("distributorId", distributorId);
				dataCtx.put("salesmanId", salesmanId);
				dataCtx.put("routeId", routeId);
				dataCtx.put("toName", toName);
				dataCtx.put("attnName", attnName);
				dataCtx.put("address1", address1);
				dataCtx.put("wardGeoId", wardGeoId);
				dataCtx.put("districtGeoId", districtGeoId);
				dataCtx.put("stateProvinceGeoId", stateProvinceGeoId);
				dataCtx.put("countryGeoId", countryGeoId);
				dataCtx.put("currencyUomId", currencyUomId);
				dataCtx.put("visitFrequencyTypeId", visitFrequencyTypeId);
				dataCtx.put("representative", representative);
				dataCtx.put("userLogin", userLogin);
				Map<String, String> validate = checkValidateAgentFromExcel(dataCtx, locale, delegator);
				if (validate.get("statusValidate") == "false"){
					dataCtx.put("statusImport","error");
					dataCtx.put("message",validate.get("message"));
					dataCtx.put("sequence",sequence);
					listIterator.add(dataCtx);
					continue;
				}
				try {
					Map<String, Object>	result = dispatcher.runSync("createAgentFromFileExcel", dataCtx);
					if(result.containsKey("partyId")){
						dataCtx.put("statusImport","success");
						dataCtx.put("message", UtilProperties.getMessage("BaseSalesUiLabels", "BSCreateSuccessful", locale));
					}else{
						dataCtx.put("statusImport","error");
						dataCtx.put("message",result.get("message"));
					}
				}catch (Exception e){
					dataCtx.put("statusImport","error");
					dataCtx.put("message",UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSErrorImportDataRequireFields", locale));
				}
				dataCtx.put("sequence",sequence);
				listIterator.add(dataCtx);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, String> checkValidInputData(String str){
		Map<String, String> results = FastMap.newInstance();
		if (UtilValidate.isEmpty(str)){
			results.put("isValidStatus", "empty");
		}else{
			if (str.equals("_NA_")){
				results.put("isValidStatus", "notValid");
			}else{
				results.put("isValidStatus", "valid");
			}
		}
		return results;
	}

	public static Map<String, String> checkValidateAgentFromExcel(Map<String, Object> dataCtx, Locale locale, Delegator delegator){
		Map<String, String> results = FastMap.newInstance();
		String message = "";
		if (!UtilValidate.isEmpty((String) dataCtx.get("partyCode"))){
			if(dataCtx.get("partyCode").equals("_NA_")){
				message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSAgentCodeIsNotValid", locale) + "\n";
			}else{
				try {
					UniqueUtil.checkPartyCode(delegator, null, dataCtx.get("partyCode"));
				} catch (Exception e) {
					message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSAgentCodeIsAlreadyExists", locale) + "\n";
				}
			}
		}
		if(UtilValidate.isEmpty((String) dataCtx.get("groupName"))) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSCustomerNameNotYetAvailable", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("contactNumber")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("DmsUiLabels", "DmsNotEnterAPhoneNumber", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("contactNumber")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSPhoneIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("infoString")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSEmailIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("taxAuthInfos")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSTaxCodeIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("postalCode")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSPostalCodeIsNotValid", locale) + "\n";
		}
		if (UtilValidate.isEmpty((String) dataCtx.get("distributorId")) ){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSIsEmptyDistributor", locale) + "\n";
		}else{
			List<GenericValue> gvDistributor = null;
			List<GenericValue> gvSalesman = null;
			List<GenericValue> gvRoute = null;
			try {
				gvDistributor= delegator.findByAnd("PartyDistributor", UtilMisc.toMap("partyId", dataCtx.get("distributorId"),"statusId", "PARTY_ENABLED"), null, false);
				if(gvDistributor.size() > 0){
					if(!UtilValidate.isEmpty((String) dataCtx.get("salesmanId"))){
						gvSalesman = delegator.findByAnd("PartySalesman", UtilMisc.toMap("partyId", dataCtx.get("salesmanId"), "distributorId", dataCtx.get("distributorId"),"statusId", "PARTY_ENABLED"), null, false);
						if(gvSalesman.size()>0){
							if(!UtilValidate.isEmpty((String) dataCtx.get("routeId"))){
								gvRoute = delegator.findByAnd("Route", UtilMisc.toMap("routeId", dataCtx.get("routeId"), "executorId", dataCtx.get("salesmanId"),"statusId", "ROUTE_ENABLED"), null, false);
								if (gvRoute.size() == 0){
									message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSNotFoundRouteInTheRequirement", locale) + "\n";
								}
							}else{
								message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSIsEmptyRoute", locale) + "\n";
							}
						}else{
							message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSNotFoundSalesmanInTheRequirement", locale) + "\n";
						}
					}else{
						message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSIsEmptySalesman", locale) + "\n";
					}
				}else{
					message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSNotFoundDistributorInTheRequirement", locale) + "\n";
				}
			} catch (GenericEntityException e) {
				message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSFaultyDSRInformation", locale) + "\n";
			}
		}
		if(checkValidInputData((String) dataCtx.get("toName")).get("isValidStatus").equals("notValid")) {
				message += UtilProperties.getMessage("BaseSalesUiLabels", "BSPartyReceive", locale) + ": " +
						UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSEnterAlphabeticCharacters", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("attnName")).get("isValidStatus").equals("notValid")) {
				message += UtilProperties.getMessage("BaseSalesUiLabels", "BSOtherInfo", locale) + ": " +
						UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSEnterAlphabeticCharacters", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("address1")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSAddress1", locale) + ": " +
					UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSEnterCharactersIsNotValid", locale) + "\n";
		}
		if(UtilValidate.isEmpty(dataCtx.get("countryGeoId")) || UtilValidate.isEmpty(dataCtx.get("stateProvinceGeoId")) || UtilValidate.isEmpty(dataCtx.get("districtGeoId")) ||
				UtilValidate.isEmpty(dataCtx.get("wardGeoId")) || UtilValidate.isEmpty(dataCtx.get("address1"))){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "EnterCorrectAddress", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("currencyUomId")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSCurrencyUomIsNotValid", locale) + "\n";
		}
		JSONObject representativeObj = JSONObject.fromObject(dataCtx.get("representative"));
		if(checkValidInputData(representativeObj.getString("partyFullName")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BERepresentative", locale) + ": " +
					UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSPartyFullNameIsNotValid", locale) + "\n";
		}
		if(checkValidInputData(representativeObj.getString("gender")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BERepresentative", locale) + ": " +
					UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSGenderIsNotValid", locale) + "\n";
		}
		if(checkValidInputData(representativeObj.getString("isValidBirthDate")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BERepresentative", locale) + ": " +
					UtilProperties.getMessage("WebPosSettingUiLabels", "SettingBirthDateNotValid", locale) + "\n";
		}
		if(checkValidInputData(representativeObj.getString("contactNumber")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BERepresentative", locale) + ": " +
					UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSPhoneIsNotValid", locale) + "\n";
		}
		if(checkValidInputData(representativeObj.getString("infoString")).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BERepresentative", locale) + ": " +
					UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSEmailIsNotValid", locale) + "\n";
		}
		if (message.length()>0){
			results.put("statusValidate", "false");
		}else{
			results.put("statusValidate", "true");
		}
		results.put("message", message);
		return results;
	}

	public static Map<String, Object> createAgentFromFileExcel(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = null;
		Map<String, Object> messageError = FastMap.newInstance();
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			//partyId = (String) context.get("partyId");
			String partyCode = context.get("partyCode")!=""? (String) context.get("partyCode"):null;
			String partyFullName = (String) context.get("groupName");
			try {
				UniqueUtil.checkPartyCode(delegator, partyId, partyCode);
			} catch (Exception e) {
				messageError.put("message", UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSUserIsAlreadyExists", locale));
				return messageError;
			}
			// create party
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			partyId = "RO" + organizationId + delegator.getNextSeqId("Party");
			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			partyGroup.put("partyId", partyId);
			partyGroup.put("partyTypeId", "RETAIL_OUTLET");
			partyGroup.put("statusId", "PARTY_DISABLED");
			partyGroup.put("preferredCurrencyUomId", context.get("currencyUomId"));
			Map<String, Object> resultCreateOutlet = dispatcher.runSync("createPartyGroup", partyGroup);
			if (ServiceUtil.isError(resultCreateOutlet)) {
				messageError.put("message", ServiceUtil.getErrorMessage(resultCreateOutlet));
				return messageError;
			}

			// store party code, because service "createPartyGroup" not store field "partyCode"
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", (partyCode != null ? partyCode : partyId)), EntityCondition.makeCondition("partyId", partyId));

			// create roles
			createAgentRole(delegator, partyId, userLogin);

			// create relationships
			createAgentRelationship(dispatcher, partyId, organizationId, userLogin);

			// create postal address
			String address1 = (String) context.get("address1");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String districtGeoId = (String) context.get("districtGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String geoPointId = (String) context.get("geoPointId");
			String postalCode = (String) context.get("postalCode");
			String toName = (String) context.get("toName");
			String attnName = (String) context.get("attnName");
			String postalAddressId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
					"POSTAL_ADDRESS", toName, attnName,
					"PRIMARY_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId, districtGeoId , wardGeoId, stateProvinceGeoId, postalCode, geoPointId, userLogin);

			if (UtilValidate.isNotEmpty(postalAddressId)) {
				// add others purpose into postal address
				dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId,
						"contactMechId", postalAddressId, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "userLogin", userLogin));
			}

			// create telecom number
			String contactNumber = (String) context.get("contactNumber");
			if(contactNumber != null && !contactNumber.isEmpty()) {
				String telecomId = CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE", contactNumber, partyFullName, partyId, userLogin);
				if (UtilValidate.isNotEmpty(telecomId)) {
					CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, telecomId, "PHONE_SHIPPING", partyId, userLogin);
				}
			}
			//createOrStoreAdditionalContact(dispatcher, delegator, context.get("contactPerson"), partyId, userLogin);

			// create email
			CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);

			// create tax info
			String taxAuthInfos = (String) context.get("taxAuthInfos");
			String defaultTaxAuthGeoId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.geo.id");
			String defaultTaxAuthPartyId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.party.id");
			Map<String, Object> resultTaxInfo = dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", defaultTaxAuthGeoId, "taxAuthPartyId", defaultTaxAuthPartyId, "partyTaxId", taxAuthInfos, "userLogin", userLogin));
			if (ServiceUtil.isError(resultTaxInfo)) {
				messageError.put("message", ServiceUtil.getErrorMessage(resultTaxInfo));
				return messageError;
			}
			// create party representative
			String representativeParams = (String) context.get("representative");
			Map<String, Object> resultCreateRep = DistributorServices.createRepresentativeAdvance(delegator, dispatcher, partyId, representativeParams, userLogin, "CUSTOMER", postalAddressId);
			//DistributorServices.createRepresentative(delegator, dispatcher, partyId, (String) context.get("representative"), "CUSTOMER", userLogin);
			if (ServiceUtil.isError(resultCreateRep)) {
				messageError.put("message", ServiceUtil.getErrorMessage(resultCreateRep));
				return messageError;
			}
			//String representativeId = (String) resultCreateRep.get("representativeId");

			// create relationship with distributor
			String distributorId = (String) context.get("distributorId");
			String routeId = (String) context.get("routeId");
			String salesExecutiveId = (String) context.get("salesmanId");
			String visitFrequencyTypeId = (String) context.get("visitFrequencyTypeId");
			dispatcher.runSync("createOrUpdatePartyCustomer", UtilMisc.toMap("partyId", partyId, "distributorId", distributorId, "salesmanId", salesExecutiveId, "salesMethodChannelEnumId", "SMCHANNEL_GT", "userLogin", userLogin, "visitFrequencyTypeId", visitFrequencyTypeId));
			if (UtilValidate.isNotEmpty(distributorId)) {
				dispatcher.runSync("updateDistributorProvideAgent", UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", distributorId,
						"routeId", routeId, "salesmanId", salesExecutiveId, "userLogin", userLogin));
			}

			// create relationship with sales executive
			if (UtilValidate.isNotEmpty(salesExecutiveId)) {
				dispatcher.runSync("updateSalesmanProvideAgent", UtilMisc.toMap("partyIdTo", partyId, "partyIdFrom", salesExecutiveId, "userLogin", userLogin));
			}

			// add party to distributor's product store
			//List<String> productStores = (List<String>) context.get("productStores[]");
			List<String> productStores = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductStore", UtilMisc.toMap("payToPartyId", distributorId), null, false), "productStoreId", true);
			if(UtilValidate.isNotEmpty(productStores)){
				for (String prodStoreId : productStores) {
					dispatcher.runSync("createProductStoreRole", UtilMisc.toMap("partyId", partyId, "productStoreId", prodStoreId, "roleTypeId", "CUSTOMER", "userLogin", userLogin));
				}
			}

			// create relationship with router
			if (UtilValidate.isNotEmpty(routeId)) {
				SupUtil.createAndUpdateRouteStoreRelationship(delegator, dispatcher, routeId, partyId, true, true);
			}

			// send notification
			String header = "";
			String state = "open";
			String action = "AgentDetail?" + "partyId=" + partyId;
			String targetLink = "";
			String ntfType = "ONE";
			String sendToGroup = "N";
			String sendrecursive = "Y";
			header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSNewAgentNotify", locale) + " [" + partyCode + "]";
			List<String> salesAdminManagerIds = SalesPartyUtil.getPartiesByRoles(delegator, UtilMisc.toList("SALESADMIN_MANAGER"), true, userLogin);
			NotificationUtil.sendNotify(dispatcher, locale, salesAdminManagerIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		} catch (Exception e) {
			Debug.logError(e, module);
			messageError.put("message", UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
			return messageError;
		}
		successResult.put("partyId", partyId);
		return successResult;
	}

	public static Map<String, Object> listMTCustomerSamples(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		opts.setLimit(10);
		opts.setMaxRows(10);
		try {
			List<GenericValue> listCustomerGT = delegator.findList("PartyMTCustomerDetailView", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
			if (listCustomerGT != null) {
				for (GenericValue itemAgent: listCustomerGT) {
					Map<String, Object> itemMap = itemAgent.getAllFields();
					List<GenericValue> listProductStores = delegator.findList("PartyCustomerAndProductStore", EntityCondition.makeCondition(UtilMisc.toMap("partyId", itemAgent.getString("partyId"))),
							null, null, null, false);
					GenericValue partyTypeName = delegator.findOne("PartyType", UtilMisc.toMap("partyTypeId", itemAgent.get("partyTypeId")), false);
					itemMap.put("partyTypeName", partyTypeName.get("description"));
					String productStores = "";
					for(GenericValue gv : listProductStores){
						productStores += "" + gv.getString("storeName")+", ";
					}
					String personId = itemAgent.getString("partyId")+"WN";
					Map<String, Object> representatives = DistributorServices.getRepresentative(delegator, personId, "Y");
					GenericValue genderItem = delegator.findOne("Gender", UtilMisc.toMap("genderId", representatives.get("gender")), false);
					itemMap.put("representative.partyFullName", representatives.get("partyFullName"));
					itemMap.put("representative.birthDate", representatives.get("birthDate"));
					itemMap.put("representative.gender", genderItem!=null?genderItem.getString("description"):representatives.get("gender"));
					itemMap.put("representative.contactNumber", representatives.containsKey("contactNumber")?representatives.get("contactNumber"):null);
					itemMap.put("representative.infoString", representatives.containsKey("infoString")?representatives.get("infoString"):null);
					itemMap.put("productStores", productStores);
					listIterator.add(itemMap);
				}
			}

		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> createMTCustomerImports(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String sizeCustomers = parameters.get("sizeCustomers")[0];
			int len = Integer.parseInt(sizeCustomers);
			for (int i=0; i < len; i++){
				String sequence = parameters.get("customers["+i+"][sequence]")[0];
				String partyCode = parameters.get("customers["+i+"][partyCode]")[0];
				String groupName = parameters.get("customers["+i+"][groupName]")[0];
				String groupNameLocal = parameters.get("customers["+i+"][groupNameLocal]")[0];
				String officeSiteName = parameters.get("customers["+i+"][officeSiteName]")[0];
				String taxAuthInfos = parameters.get("customers["+i+"][taxAuthInfos]")[0];
				String currencyUomId = parameters.get("customers["+i+"][currencyUomId]")[0];
				String comments = parameters.get("customers["+i+"][comments]")[0];
				String supervisorId = parameters.get("customers["+i+"][supervisorId]")[0];
				String salesmanId = parameters.get("customers["+i+"][salesmanId]")[0];
				String routeId = parameters.get("customers["+i+"][routeId]")[0];
				String productStores = parameters.get("customers["+i+"][productStores]")[0];
				String partyTypeId = parameters.get("customers["+i+"][partyTypeId]")[0];
				String countryGeoId = parameters.get("customers["+i+"][countryGeoId]")[0];
				String stateProvinceGeoId = parameters.get("customers["+i+"][stateProvinceGeoId]")[0];
				String districtGeoId = parameters.get("customers["+i+"][districtGeoId]")[0];
				String wardGeoId = parameters.get("customers["+i+"][wardGeoId]")[0];
				String address1 = parameters.get("customers["+i+"][address1]")[0];
				String contactNumber = parameters.get("customers["+i+"][contactNumber]")[0];
				String infoString = parameters.get("customers["+i+"][infoString]")[0];
				String representative = parameters.get("customers["+i+"][representative]")[0];

				String[] arrProductStore = productStores.split(",");
				List<String> listProductStore = FastList.newInstance();
				if(arrProductStore.length>0)
					for (int k=0; k<arrProductStore.length; k++){
						if(UtilValidate.isNotEmpty(arrProductStore[k]))
							listProductStore.add(arrProductStore[k]);
					}

				Map<String, Object> dataCtx = FastMap.newInstance();
				dataCtx.put("partyCode", partyCode);
				dataCtx.put("groupName", groupName);
				dataCtx.put("groupNameLocal", groupNameLocal);
				dataCtx.put("officeSiteName", officeSiteName);
				dataCtx.put("taxAuthInfos", taxAuthInfos);
				dataCtx.put("currencyUomId", currencyUomId);
				dataCtx.put("comments", comments);
				dataCtx.put("supervisorId", supervisorId);
				dataCtx.put("salesmanId", salesmanId);
				dataCtx.put("routeId", routeId);
				dataCtx.put("productStores[]", listProductStore);
				dataCtx.put("partyTypeId", partyTypeId);
				dataCtx.put("countryGeoId", countryGeoId);
				dataCtx.put("stateProvinceGeoId", stateProvinceGeoId);
				dataCtx.put("districtGeoId", districtGeoId);
				dataCtx.put("wardGeoId", wardGeoId);
				dataCtx.put("address1", address1);
				dataCtx.put("contactNumber", contactNumber);
				dataCtx.put("infoString", infoString);
				dataCtx.put("representative", representative);
				dataCtx.put("userLogin", userLogin);
				Map<String, String> validate = checkValidateMTCustomerFromExcel(dataCtx, locale, delegator, productStores);
				if (validate.get("statusValidate") == "false"){
					dataCtx.put("statusImport","error");
					dataCtx.put("message",validate.get("message"));
					dataCtx.put("sequence",sequence);
					listIterator.add(dataCtx);
					continue;
				}
				try {
					Map<String, Object>	result = dispatcher.runSync("createMTCustomerFromFileExcel", dataCtx);
					if(result.containsKey("partyId")){
						dataCtx.put("statusImport","success");
						dataCtx.put("message", UtilProperties.getMessage("BaseSalesUiLabels", "BSCreateSuccessful", locale));
					}else{
						dataCtx.put("statusImport","error");
						dataCtx.put("message",result.get("message"));
					}
				}catch (Exception e){
					dataCtx.put("statusImport","error");
					dataCtx.put("message",UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSErrorImportDataRequireFields", locale));
				}
				dataCtx.put("sequence",sequence);
				listIterator.add(dataCtx);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, String> checkValidateMTCustomerFromExcel(Map<String, Object> dataCtx, Locale locale, Delegator delegator, String productStores){
		Map<String, String> results = FastMap.newInstance();
		String message = "";
        if(UtilValidate.isNotEmpty((String) dataCtx.get("partyCode"))) {
            if (checkValidInputData((String) dataCtx.get("partyCode")).get("isValidStatus").equals("notValid")) {
                message += UtilProperties.getMessage("BaseSalesUiLabels", "BSCustomerId", locale) + ": " +
                        UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
            } else {
                try {
                    List<GenericValue> partyCheck = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", dataCtx.get("partyCode")), null, false);
                    if (UtilValidate.isNotEmpty(partyCheck)) {
                        message += UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSCustomerHaveExists", UtilMisc.toMap("partyId", dataCtx.get("partyCode")), locale) + "\n";
                    }
                } catch (Exception e) {
                    message += UtilProperties.getMessage("BaseSalesUiLabels", "BSCustomerId", locale) + ": " +
                            UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
                }
            }
        }
		if(checkValidInputData((String) dataCtx.get("groupName")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSCustomerName", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("officeSiteName")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSOfficeSiteName", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("taxAuthInfos")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "PartyTaxAuthInfos", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("currencyUomId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSCurrencyUomId", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if (checkValidInputData((String) dataCtx.get("partyTypeId")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSCustomerType", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("partyTypeId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSCustomerType", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
        if (checkValidInputData((String) dataCtx.get("supervisorId")).get("isValidStatus").equals("empty")) {
            message += UtilProperties.getMessage("BaseSalesUiLabels", "BSSupervisor", locale) + ": " +
                    UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
        }else if(checkValidInputData((String) dataCtx.get("supervisorId")).get("isValidStatus").equals("notValid")){
            message += UtilProperties.getMessage("BaseSalesUiLabels", "BSSupervisor", locale) + ": " +
                    UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
        }else{
        	if(UtilValidate.isNotEmpty((String) dataCtx.get("salesmanId"))) {
				List<EntityCondition> listAllConditions = FastList.newInstance();
				listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
				listAllConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
				listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
				listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", (String) dataCtx.get("supervisorId")));
				listAllConditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				try {
					List<GenericValue> sups = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
					if(sups.size()>0){
						List<String> supervisors = FastList.newInstance();
						for(GenericValue sup : sups){
							supervisors.add(sup.getString("partyIdTo"));
						}
						listAllConditions.clear();
						listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN,  supervisors));
						List<GenericValue> listSalesman = delegator.findList("PartySalesman", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
						boolean existSalesman = false;
						if (listSalesman.size()>0){
							for (GenericValue sm : listSalesman){
								if(((String) dataCtx.get("salesmanId")).equals(sm.getString("fullName"))){
									dataCtx.put("salesmanId", sm.getString("partyId"));
									existSalesman = true;
								}
							}
							if (!existSalesman){
								message += UtilProperties.getMessage("BaseSalesUiLabels", "BSSalesman", locale) + ": " +
										UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
							}else{
								if(UtilValidate.isNotEmpty((String) dataCtx.get("routeId"))) {
									List<String> salesmans = FastList.newInstance();
									for (GenericValue salesman : listSalesman) {
										salesmans.add(salesman.getString("partyId"));
									}
									listAllConditions.clear();
									listAllConditions.add(EntityCondition.makeCondition("executorId", EntityOperator.IN, salesmans));
									listAllConditions.add(EntityCondition.makeCondition("statusId", "ROUTE_ENABLED"));
									List<GenericValue> listRoute = delegator.findList("Route", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
									boolean existRoute = false;
									if (listRoute.size() > 0) {
										for (GenericValue route : listRoute) {
											if (((String) dataCtx.get("routeId")).equals(route.getString("routeName"))) {
												dataCtx.put("routeId", route.getString("routeId"));
												existRoute = true;
											}
										}
										if (!existRoute) {
											message += UtilProperties.getMessage("BaseSalesUiLabels", "BSRoute", locale) + ": " +
													UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
										}
									}else{
										dataCtx.put("salesmanId", "");
									}
								}
							}
						}else{
							dataCtx.put("salesmanId", "");
							dataCtx.put("routeId", "");
						}
					}
				} catch (GenericEntityException e) {
					message += UtilProperties.getMessage("BaseSalesUiLabels", "BSSalesman", locale) + ", " +
							UtilProperties.getMessage("BaseSalesUiLabels", "BSRoute", locale) +": " +
							UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
				}
			}
		}
		if (checkValidInputData(productStores).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSSalesChannel", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData(productStores).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSSalesChannel", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
        if (checkValidInputData((String) dataCtx.get("countryGeoId")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSCountry", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("countryGeoId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSCountry", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if (checkValidInputData((String) dataCtx.get("stateProvinceGeoId")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSStateProvince", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("stateProvinceGeoId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSStateProvince", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("districtGeoId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSCounty", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("wardGeoId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSWard", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if (checkValidInputData((String) dataCtx.get("address1")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSAddress1", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("address1")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSAddress1", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if (checkValidInputData((String) dataCtx.get("contactNumber")).get("isValidStatus").equals("empty")) {
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSPhoneNumber", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotEmpty", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("contactNumber")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSPhoneNumber", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) dataCtx.get("infoString")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSEmailAddress", locale) + ": " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}

		JSONObject representativeObj = JSONObject.fromObject(dataCtx.get("representative"));
		if(checkValidInputData((String) representativeObj.get("gender")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSRepresentativeGender", locale) + " (" +
					UtilProperties.getMessage("BaseSalesUiLabels", "BSRepresentative", locale) + "): " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData(representativeObj.get("birthDate") == null?"":""+representativeObj.get("birthDate")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSRepresentativeBirthday", locale) + " (" +
					UtilProperties.getMessage("BaseSalesUiLabels", "BSRepresentative", locale) + "): " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) representativeObj.get("countryGeoId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSCountry", locale) + " (" +
					UtilProperties.getMessage("BaseSalesUiLabels", "BSRepresentative", locale) + "): " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) representativeObj.get("stateProvinceGeoId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSStateProvince", locale) + " (" +
					UtilProperties.getMessage("BaseSalesUiLabels", "BSRepresentative", locale) + "): " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) representativeObj.get("districtGeoId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSCounty", locale) + " (" +
					UtilProperties.getMessage("BaseSalesUiLabels", "BSRepresentative", locale) + "): " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) representativeObj.get("wardGeoId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSWard", locale) + " (" +
					UtilProperties.getMessage("BaseSalesUiLabels", "BSRepresentative", locale) + "): " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) representativeObj.get("address1")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSAddress1", locale) + " (" +
					UtilProperties.getMessage("BaseSalesUiLabels", "BSRepresentative", locale) + "): " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) representativeObj.get("contactNumber")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSPhoneNumber", locale) + " (" +
					UtilProperties.getMessage("BaseSalesUiLabels", "BSRepresentative", locale) + "): " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if(checkValidInputData((String) representativeObj.get("infoString")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesUiLabels", "BSEmailAddress", locale) + " (" +
					UtilProperties.getMessage("BaseSalesUiLabels", "BSRepresentative", locale) + "): " +
					UtilProperties.getMessage("BaseHRDirectoryUiLabels", "HRIsNotValid", locale) + "\n";
		}
		if (message.length()>0){
			results.put("statusValidate", "false");
		}else{
			results.put("statusValidate", "true");
		}
		results.put("message", message);
		return results;
	}


	public static Map<String, Object> createMTCustomerFromFileExcel(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = null;
		Map<String, Object> messageError = FastMap.newInstance();
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			String partyCode = context.get("partyCode")!=""? (String) context.get("partyCode"):null;
			String partyFullName = (String) context.get("groupName");
			try {
				UniqueUtil.checkPartyCode(delegator, partyId, partyCode);
			} catch (Exception e) {
				messageError.put("message", UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSUserIsAlreadyExists", locale));
				return messageError;
			}
			// create party
			String partyTypeId = (String) context.get("partyTypeId");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			partyId = "KH";
			switch (partyTypeId) {
				case "SUPERMARKET":
					partyId += "SM";
					break;
				case "COFFEE_HOUSE":
					partyId += "CF";
					break;
				case "SCHOOL":
					partyId += "SC";
					break;
				case "TRADE_CENTER":
					partyId += "TC";
					break;
				case "HOSPITAL":
					partyId += "HO";
					break;
				default:
					break;
			}
			partyId += delegator.getNextSeqId("Party");
			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			partyGroup.put("partyId", partyId);
			partyGroup.put("partyTypeId", partyTypeId);
			partyGroup.put("statusId", "PARTY_DISABLED");
			partyGroup.put("preferredCurrencyUomId", context.get("currencyUomId"));
			Map<String, Object> resultCreateCustomer = dispatcher.runSync("createPartyGroup", partyGroup);
			if (ServiceUtil.isError(resultCreateCustomer)) {
				messageError.put("message", ServiceUtil.getErrorMessage(resultCreateCustomer));
				return messageError;
			}

			// store party code, because service "createPartyGroup" not store field "partyCode"
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", (partyCode != null ? partyCode : partyId)), EntityCondition.makeCondition("partyId", partyId));
			// create roles
			createMTCustomerRole(dispatcher, partyId, userLogin);
			// create relationships
			createMTCustomerRelationship(dispatcher, partyId, organizationId, userLogin);
			// create postal address
			String address1 = (String) context.get("address1");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String districtGeoId = (String) context.get("districtGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String geoPointId = (String) context.get("geoPointId");
			String postalCode = (String) context.get("postalCode");
			String toName = (String) context.get("toName");
			String attnName = (String) context.get("attnName");
			String postalAddressId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
					"POSTAL_ADDRESS", toName, attnName,
					"PRIMARY_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId, districtGeoId , wardGeoId, stateProvinceGeoId, postalCode, geoPointId, userLogin);

			if (UtilValidate.isNotEmpty(postalAddressId)) {
				// add others purpose into postal address
				dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId,
						"contactMechId", postalAddressId, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "userLogin", userLogin));
			}

			// create telecom number
			String contactNumber = (String) context.get("contactNumber");
			if(contactNumber != null && !contactNumber.isEmpty()){
				String telecomId = CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE", contactNumber, partyFullName, partyId, userLogin);
				CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, telecomId, "PHONE_SHIPPING", partyId, userLogin);
			}
			//createOrStoreAdditionalContact(dispatcher, delegator, context.get("contactPerson"), partyId, userLogin);

			// create email
			CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);

			// create tax info
			String taxAuthInfos = (String) context.get("taxAuthInfos");
			String defaultTaxAuthGeoId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.geo.id");
			String defaultTaxAuthPartyId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.party.id");
			Map<String, Object> resultTaxInfo = dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", defaultTaxAuthGeoId, "taxAuthPartyId", defaultTaxAuthPartyId, "partyTaxId", taxAuthInfos, "userLogin", userLogin));
			if (ServiceUtil.isError(resultTaxInfo)) {
				messageError.put("message", ServiceUtil.getErrorMessage(resultTaxInfo));
				return messageError;
			}

			// create party representative
			String representativeParams = (String) context.get("representative");
			Map<String, Object> resultCreateRep = DistributorServices.createRepresentativeAdvance(delegator, dispatcher, partyId, representativeParams, userLogin, "CUSTOMER", postalAddressId);
			//DistributorServices.createRepresentative(delegator, dispatcher, partyId, (String) context.get("representative"), "CUSTOMER", userLogin);
			if (ServiceUtil.isError(resultCreateRep)) {
				messageError.put("message", ServiceUtil.getErrorMessage(resultCreateRep));
				return messageError;
			}
			//String representativeId = (String) resultCreateRep.get("representativeId");

			// update PartyCustomer and supervisor of customer MT
			String salesExecutiveId = (String) context.get("salesmanId");
			String supervisorId = (String) context.get("supervisorId");
			dispatcher.runSync("createOrUpdatePartyCustomer", UtilMisc.toMap("partyId", partyId, "partyCode", partyCode, "supervisorId", supervisorId, "salesmanId", salesExecutiveId, "salesMethodChannelEnumId", "SMCHANNEL_MT", "userLogin", userLogin));
			if (UtilValidate.isNotEmpty(supervisorId)) {
				dispatcher.runSync("updateSupervisorMTCustomer", UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", supervisorId, "userLogin", userLogin));
			}

			// create relationship with sales executive
			if (UtilValidate.isNotEmpty(salesExecutiveId)) {
				dispatcher.runSync("updateSalesmanProvideAgent", UtilMisc.toMap("partyIdTo", partyId, "partyIdFrom", salesExecutiveId, "userLogin", userLogin));
			}

			// add party to company's product store
			List<String> productStores = (List<String>) context.get("productStores[]");
			if(UtilValidate.isNotEmpty(productStores)){
				for (String s : productStores) {
					dispatcher.runSync("createProductStoreRoleOlb",
							UtilMisc.toMap("partyId", partyId, "fromDate", new Timestamp(System.currentTimeMillis()),
									"productStoreId", s, "roleTypeId", "CUSTOMER", "userLogin", userLogin));
				}
			}

			// create relationship with router
			String routeId = (String) context.get("routeId");
			if (UtilValidate.isNotEmpty(routeId)) {
				SupUtil.createAndUpdateRouteStoreRelationship(delegator, dispatcher, routeId, partyId, true, true);
			}

			// send notification
			String header = "";
			String state = "open";
			String action = "MTCustomerDetail?" + "partyId=" + partyId;
			String targetLink = "";
			String ntfType = "ONE";
			String sendToGroup = "N";
			String sendrecursive = "Y";
			header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSNewMTCustomerNotify", locale) + " [" + (partyCode!=null?partyCode:partyId) + "]";
			List<String> salesAdminManagerIds = SalesPartyUtil.getPartiesByRoles(delegator, UtilMisc.toList("SALESADMIN_MANAGER"), true, userLogin);
			NotificationUtil.sendNotify(dispatcher, locale, salesAdminManagerIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		} catch (Exception e) {
			Debug.logError(e, module);
			messageError.put("message", UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
			return messageError;
		}
		successResult.put("partyId", partyId);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listMTRepresentativeOffices(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		List<GenericValue> listIterator = null;
		try {
			listSortFields.add("partyCode");
			if (parameters.containsKey("statusId") && parameters.get("statusId").length > 0){
				listAllConditions.add(EntityCondition.makeCondition("statusId", parameters.get("statusId")[0]));
			}
			listAllConditions.add(EntityCondition.makeCondition("partyIdTo", organizationId));
			listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "RepresentativeParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> JQGetListMTConsignee(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		List<GenericValue> listIterator = null;
		try {
			listSortFields.add("partyCode");
			if (parameters.containsKey("statusId") && parameters.get("statusId").length > 0){
				listAllConditions.add(EntityCondition.makeCondition("statusId", parameters.get("statusId")[0]));
			}
			listAllConditions.add(EntityCondition.makeCondition("partyIdTo", organizationId));
			listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "ShippingCustomerGroup", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static Map<String, Object> loadMTConsigneeInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        try {
            Map<String, Object> MTCustomerInfo = FastMap.newInstance();
            String partyId = (String) context.get("partyId");
            GenericValue partyGroup = delegator.findOne("PartyAndPartyGroupDetail", UtilMisc.toMap("partyId", partyId), false);
            MTCustomerInfo.put("partyId", partyGroup.get("partyId"));
            MTCustomerInfo.put("statusId", partyGroup.get("statusId"));
            MTCustomerInfo.put("partyCode", partyGroup.get("partyCode"));
            MTCustomerInfo.put("groupName", partyGroup.get("groupName"));
            MTCustomerInfo.put("officeSiteName", partyGroup.get("officeSiteName"));
            MTCustomerInfo.put("logoImageUrl", partyGroup.get("logoImageUrl"));
            MTCustomerInfo.put("comments", partyGroup.get("comments"));

            List<EntityCondition> conditions = FastList.newInstance();
            conditions.clear();
            conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
            conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
            List<GenericValue> partyAndContactMechs = delegator.findList("PartyAndContactMech",
                    EntityCondition.makeCondition(conditions), null, null, null, false);
            for (GenericValue x : partyAndContactMechs) {
                switch (x.getString("contactMechTypeId")) {
                    case "TELECOM_NUMBER":
                        MTCustomerInfo.put("contactNumber", x.get("tnContactNumber"));
                        MTCustomerInfo.put("contactNumberId", x.get("contactMechId"));
                        break;
                    case "EMAIL_ADDRESS":
                        MTCustomerInfo.put("infoString", x.get("infoString"));
                        MTCustomerInfo.put("infoStringId", x.get("contactMechId"));
                        break;
                    case "POSTAL_ADDRESS":
                        MTCustomerInfo.put("address1", x.get("paAddress1"));
                        if ("Y".equals(context.get("detail"))) {
                            MTCustomerInfo.put("wardGeoId", DistributorServices.getGeoName(delegator, x.get("paWardGeoId")));
                            MTCustomerInfo.put("districtGeoId", DistributorServices.getGeoName(delegator, x.get("paDistrictGeoId")));
                            MTCustomerInfo.put("stateProvinceGeoId", DistributorServices.getGeoName(delegator, x.get("paStateProvinceGeoId")));
                            MTCustomerInfo.put("countryGeoId", DistributorServices.getGeoName(delegator, x.get("paCountryGeoId")));
                            GenericValue postalAddress = delegator.findOne("PostalAddressAndGeo", UtilMisc.toMap("contactMechId", x.get("contactMechId")), false);
                            if (UtilValidate.isNotEmpty(postalAddress)) {
                                String address = postalAddress.getString("address1") + ", " +
                                        postalAddress.getString("wardGeoName") + ", " +
                                        postalAddress.getString("districtGeoName") + ", " +
                                        postalAddress.getString("stateProvinceGeoName") + ", " +
                                        postalAddress.getString("countryGeoName");
                                if (UtilValidate.isNotEmpty(address)) {
                                    address = address.replaceAll(", null, ", ", ");
                                    address = address.replaceAll(", null,", ", ");
                                }
                                MTCustomerInfo.put("address", address);
                            }
                        } else {
                            MTCustomerInfo.put("wardGeoId", x.get("paWardGeoId"));
                            MTCustomerInfo.put("districtGeoId", x.get("paDistrictGeoId"));
                            MTCustomerInfo.put("stateProvinceGeoId", x.get("paStateProvinceGeoId"));
                            MTCustomerInfo.put("countryGeoId", x.get("paCountryGeoId"));
                            MTCustomerInfo.put("addressId", x.get("contactMechId"));
                        }
                        break;
                    default:
                        break;
                }
            }
            conditions.clear();
            result.put("MTCustomerInfo", MTCustomerInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	public static Map<String, Object> listMTCustomerRepresentative(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String partyId = (String) parameters.get("partyId")[0];
		List<GenericValue> listIterator = null;
		try {
			listSortFields.add("partyCodeTo");
			listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", partyId));
			listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "MTCustomerRepresentatives", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static Map<String, Object> loadMTRepresentativeOfficeInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> MTCustomerInfo = FastMap.newInstance();
			String partyId = (String) context.get("partyId");
			GenericValue partyGroup = delegator.findOne("PartyAndPartyGroupDetail", UtilMisc.toMap("partyId", partyId), false);
			MTCustomerInfo.put("partyId", partyGroup.get("partyId"));
			MTCustomerInfo.put("statusId", partyGroup.get("statusId"));
			MTCustomerInfo.put("partyCode", partyGroup.get("partyCode"));
			MTCustomerInfo.put("groupName", partyGroup.get("groupName"));
			MTCustomerInfo.put("officeSiteName", partyGroup.get("officeSiteName"));
			MTCustomerInfo.put("logoImageUrl", partyGroup.get("logoImageUrl"));
			MTCustomerInfo.put("comments", partyGroup.get("comments"));
			MTCustomerInfo.put("currencyUomId", partyGroup.get("preferredCurrencyUomId"));

			List<EntityCondition> conditions = FastList.newInstance();
			//	get taxAuthInfos
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM", "taxAuthPartyId", "VNM_TAX")));
			List<GenericValue> partyTaxAuthInfos = delegator.findList("PartyTaxAuthInfo",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyTaxId", "fromDate"), null, null, false);
			if (UtilValidate.isNotEmpty(partyTaxAuthInfos)) {
				GenericValue partyTaxAuthInfo = EntityUtil.getFirst(partyTaxAuthInfos);
				MTCustomerInfo.put("taxAuthInfos", partyTaxAuthInfo.get("partyTaxId"));
				MTCustomerInfo.put("taxAuthInfosfromDate", partyTaxAuthInfo.getTimestamp("fromDate").getTime());
			}
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
			List<GenericValue> partyAndContactMechs = delegator.findList("PartyAndContactMech",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : partyAndContactMechs) {
				switch (x.getString("contactMechTypeId")) {
					case "TELECOM_NUMBER":
						MTCustomerInfo.put("contactNumber", x.get("tnContactNumber"));
						MTCustomerInfo.put("contactNumberId", x.get("contactMechId"));
						break;
					case "EMAIL_ADDRESS":
						MTCustomerInfo.put("infoString", x.get("infoString"));
						MTCustomerInfo.put("infoStringId", x.get("contactMechId"));
						break;
					case "POSTAL_ADDRESS":
						MTCustomerInfo.put("address1", x.get("paAddress1"));
						if ("Y".equals(context.get("detail"))) {
							MTCustomerInfo.put("wardGeoId", DistributorServices.getGeoName(delegator, x.get("paWardGeoId")));
							MTCustomerInfo.put("districtGeoId", DistributorServices.getGeoName(delegator, x.get("paDistrictGeoId")));
							MTCustomerInfo.put("stateProvinceGeoId", DistributorServices.getGeoName(delegator, x.get("paStateProvinceGeoId")));
							MTCustomerInfo.put("countryGeoId", DistributorServices.getGeoName(delegator, x.get("paCountryGeoId")));
							GenericValue postalAddress = delegator.findOne("PostalAddressAndGeo", UtilMisc.toMap("contactMechId", x.get("contactMechId")), false);
							if (UtilValidate.isNotEmpty(postalAddress)) {
								String address = postalAddress.getString("address1") + ", " +
										postalAddress.getString("wardGeoName") + ", " +
										postalAddress.getString("districtGeoName") + ", " +
										postalAddress.getString("stateProvinceGeoName") + ", " +
										postalAddress.getString("countryGeoName");
								if (UtilValidate.isNotEmpty(address)) {
									address = address.replaceAll(", null, ", ", ");
									address = address.replaceAll(", null,", ", ");
								}
								MTCustomerInfo.put("address", address);
							}
						} else {
							MTCustomerInfo.put("wardGeoId", x.get("paWardGeoId"));
							MTCustomerInfo.put("districtGeoId", x.get("paDistrictGeoId"));
							MTCustomerInfo.put("stateProvinceGeoId", x.get("paStateProvinceGeoId"));
							MTCustomerInfo.put("countryGeoId", x.get("paCountryGeoId"));
							MTCustomerInfo.put("addressId", x.get("contactMechId"));
						}
						break;
					default:
						break;
				}
			}
			conditions.clear();
			result.put("MTCustomerInfo", MTCustomerInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> createMTRepresentativeOffice(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = null;
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			//partyId = (String) context.get("partyId");
			String partyCode = (String) context.get("partyCode");
			String partyFullName = (String) context.get("groupName");

			try {
				UniqueUtil.checkPartyCode(delegator, partyId, partyCode);
			} catch (Exception e) {
				ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSUserIsAlreadyExists", locale));
			}

			// create party
			String partyTypeId = "SUPERMARKET";
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			partyId = "KH";
			partyId += delegator.getNextSeqId("Party");

			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			partyGroup.put("partyId", partyId);
			partyGroup.put("partyTypeId", partyTypeId);
			partyGroup.put("statusId", "PARTY_DISABLED");
			partyGroup.put("preferredCurrencyUomId", context.get("currencyUomId"));
			Map<String, Object> resultCreateCustomer = dispatcher.runSync("createPartyGroup", partyGroup);
			if (ServiceUtil.isError(resultCreateCustomer)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateCustomer));
			}

			// store party code, because service "createPartyGroup" not store field "partyCode"
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", (partyCode != null ? partyCode : partyId)), EntityCondition.makeCondition("partyId", partyId));

			// create roles
			List<String> roles = Arrays.asList("BILL_TO_CUSTOMER", "CUSTOMER", "END_USER_CUSTOMER", "PLACING_CUSTOMER", "SHIP_TO_CUSTOMER", "CUSTOMER_MT", "CHILD_MEMBER");
			for (String r : roles) {
				dispatcher.runSync("createPartyRole",
						UtilMisc.toMap("partyId", partyId, "roleTypeId", r, "userLogin", userLogin));
			}

			// create relationships
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", "CUSTOMER_MT",
							"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "CUSTOMER_REL",
							"userLogin", userLogin));

			// create postal address
			String address1 = (String) context.get("address1");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String districtGeoId = (String) context.get("districtGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String geoPointId = (String) context.get("geoPointId");
			String postalCode = (String) context.get("postalCode");
			String toName = (String) context.get("toName");
			String attnName = (String) context.get("attnName");
			String postalAddressId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
					"POSTAL_ADDRESS", toName, attnName,
					"PRIMARY_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId, districtGeoId , wardGeoId, stateProvinceGeoId, postalCode, geoPointId, userLogin);

			if (UtilValidate.isNotEmpty(postalAddressId)) {
				// add others purpose into postal address
				dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId,
						"contactMechId", postalAddressId, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "userLogin", userLogin));
			}

			// create telecom number
			String contactNumber = (String) context.get("contactNumber");
			String telecomId = CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE", contactNumber, partyFullName, partyId, userLogin);
			CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, telecomId, "PHONE_SHIPPING", partyId, userLogin);

			//createOrStoreAdditionalContact(dispatcher, delegator, context.get("contactPerson"), partyId, userLogin);

			// create email
			CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);

			// create tax info
			String taxAuthInfos = (String) context.get("taxAuthInfos");
			String defaultTaxAuthGeoId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.geo.id");
			String defaultTaxAuthPartyId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.party.id");
			Map<String, Object> resultTaxInfo = dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", defaultTaxAuthGeoId, "taxAuthPartyId", defaultTaxAuthPartyId, "partyTaxId", taxAuthInfos, "userLogin", userLogin));
			if (ServiceUtil.isError(resultTaxInfo)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultTaxInfo));
			}
			// send notification
			String header = "";
			String state = "open";
			String action = "MTRepresentativeOfficeDetail?" + "partyId=" + partyId;
			String targetLink = "";
			String ntfType = "ONE";
			String sendToGroup = "N";
			String sendrecursive = "Y";
			header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSNewMTCustomerNotify", locale) + " [" + (partyCode!=null?partyCode:partyId) + "]";
			List<String> salesAdminManagerIds = SalesPartyUtil.getPartiesByRoles(delegator, UtilMisc.toList("SALESADMIN_MANAGER"), true, userLogin);
			NotificationUtil.sendNotify(dispatcher, locale, salesAdminManagerIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		successResult.put("partyId", partyId);
		return successResult;
	}

	public static Map<String, Object> updateMTRepresentativeOffice(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			String partyId = (String) context.get("partyId");

			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			partyGroup.put("preferredCurrencyUomId", context.get("currencyUomId"));
			result = dispatcher.runSync("updatePartyGroup", partyGroup);
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			party.set("partyCode", context.get("partyCode"));
			party.store();

			String contactMechId = CallcenterServices.updateContactMechTelecomNumber(dispatcher, delegator, (String)context.get("contactNumberId"), "TELECOM_NUMBER",
					"PRIMARY_PHONE", context.get("contactNumber"), context.get("groupName"), partyId, userLogin);
			CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, contactMechId, "PHONE_SHIPPING", partyId, userLogin);

			CallcenterServices.updateContactMechEmail(dispatcher, delegator, (String)context.get("infoStringId"), "EMAIL_ADDRESS",
					context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);

			if (UtilValidate.isEmpty(context.get("taxAuthInfosfromDate"))) {
				dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM",
						"taxAuthPartyId", "VNM_TAX", "partyTaxId", context.get("taxAuthInfos"), "userLogin", userLogin));
			} else {
				Long taxAuthInfosfromDate = (Long) context.get("taxAuthInfosfromDate");
				dispatcher.runSync("updatePartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM",
						"taxAuthPartyId", "VNM_TAX", "partyTaxId", context.get("taxAuthInfos"),
						"fromDate", new Timestamp(taxAuthInfosfromDate), "userLogin", userLogin));
			}
			result.put("partyId", partyId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> getPartyStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> partyMap = FastMap.newInstance();
			String partyId = (String) context.get("partyId");
			GenericValue party= delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			partyMap.put("partyId", party.get("partyId"));
			partyMap.put("statusId", party.get("statusId"));
			result.put("partyMap", partyMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    public static Map<String, Object> createNewMemberOffice(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyIdFrom = (String) context.get("partyIdFrom");
		String partyIdTo = (String) context.get("partyIdTo");
        try {
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", "CUSTOMER",
							"roleTypeIdTo", "CHILD_MEMBER", "partyRelationshipTypeId", "OWNER",
							"userLogin", userLogin));
        } catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
        }
        successResult.put("partyId", partyIdTo);
        return successResult;
    }

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listMTCustomerNotRepresentatives(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> listIterator = null;
		try {
			if (parameters.containsKey("sD")) {
				if ("N".equals(parameters.get("sD")[0])) {
					listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
				}
			}
            listAllConditions.add(EntityCondition.makeCondition("partyIdTo",null));
			listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "MTCustomerAndPartyTo", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}


	public static Map<String, Object> createMTConsignee(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = null;
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			//partyId = (String) context.get("partyId");
			String partyCode = (String) context.get("partyCode");
			String partyFullName = (String) context.get("groupName");

			try {
				UniqueUtil.checkPartyCode(delegator, partyId, partyCode);
			} catch (Exception e) {
				ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSUserIsAlreadyExists", locale));
			}

			// create party
			String partyTypeId = "PARTY_GROUP";
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			partyId = "SHPC";
			partyId += delegator.getNextSeqId("Party");

			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			partyGroup.put("partyId", partyId);
			partyGroup.put("partyTypeId", partyTypeId);
			partyGroup.put("statusId", "PARTY_DISABLED");
			Map<String, Object> resultCreateCustomer = dispatcher.runSync("createPartyGroup", partyGroup);
			if (ServiceUtil.isError(resultCreateCustomer)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateCustomer));
			}

			// store party code, because service "createPartyGroup" not store field "partyCode"
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", (partyCode != null ? partyCode : partyId)), EntityCondition.makeCondition("partyId", partyId));

			List<String> roles = Arrays.asList("CONSIGNEE");
			for (String r : roles) {
				dispatcher.runSync("createPartyRole",
						UtilMisc.toMap("partyId", partyId, "roleTypeId", r, "userLogin", userLogin));
			}

//			// create relationships
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", "CONSIGNEE",
							"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "CUSTOMER_REL",
							"userLogin", userLogin));

			// create postal address
			String address1 = (String) context.get("address1");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String districtGeoId = (String) context.get("districtGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String geoPointId = (String) context.get("geoPointId");
			String postalCode = (String) context.get("postalCode");
			String toName = (String) context.get("toName");
			String attnName = (String) context.get("attnName");
			String postalAddressId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
					"POSTAL_ADDRESS", toName, attnName,
					"PRIMARY_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId, districtGeoId , wardGeoId, stateProvinceGeoId, postalCode, geoPointId, userLogin);

			if (UtilValidate.isNotEmpty(postalAddressId)) {
				// add others purpose into postal address
				dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId,
						"contactMechId", postalAddressId, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "userLogin", userLogin));
			}

			// create telecom number
			String contactNumber = (String) context.get("contactNumber");
			String telecomId = CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE", contactNumber, partyFullName, partyId, userLogin);
			CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, telecomId, "PHONE_SHIPPING", partyId, userLogin);

			//createOrStoreAdditionalContact(dispatcher, delegator, context.get("contactPerson"), partyId, userLogin);

			// create email
			CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);

			// send notification
			String header = "";
			String state = "open";
			String action = "MTConsignee?" + "partyId=" + partyId;
			String targetLink = "";
			String ntfType = "ONE";
			String sendToGroup = "N";
			String sendrecursive = "Y";

			header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSNewMTConsigneeNotify", locale) + " [" + (partyCode!=null?partyCode:partyId) + "]";
			List<String> notifiee = SalesPartyUtil.getPartiesByRoles(delegator, UtilMisc.toList("SALES_MANAGER"), true, userLogin);
			NotificationUtil.sendNotify(dispatcher, locale, notifiee, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
			notifiee = SalesPartyUtil.getPartiesByRoles(delegator, UtilMisc.toList("SALESADMIN_MANAGER"), true, userLogin);
			NotificationUtil.sendNotify(dispatcher, locale, notifiee, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
			notifiee = SalesPartyUtil.getPartiesByRoles(delegator, UtilMisc.toList("SALESSUP_MT"), true, userLogin);
			NotificationUtil.sendNotify(dispatcher, locale, notifiee, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);

		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		successResult.put("partyId", partyId);
		return successResult;
	}

	public static Map<String, Object> updateMTConsignee(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			String partyId = (String) context.get("partyId");
			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			result = dispatcher.runSync("updatePartyGroup", partyGroup);

			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			party.set("partyCode", context.get("partyCode"));
			party.store();

			String contactMechId = CallcenterServices.updateContactMechTelecomNumber(dispatcher, delegator, (String)context.get("contactNumberId"), "TELECOM_NUMBER",
					"PRIMARY_PHONE", context.get("contactNumber"), context.get("groupName"), partyId, userLogin);
			CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, contactMechId, "PHONE_SHIPPING", partyId, userLogin);

			CallcenterServices.updateContactMechEmail(dispatcher, delegator, (String)context.get("infoStringId"), "EMAIL_ADDRESS",
					context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);
			result.put("partyId", partyId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> rejectMTConsignee(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String partyId = (String) context.get("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, partyId));
			List<GenericValue> agreements = delegator.findList("Agreement",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(agreements)) {
				Locale locale = (Locale) context.get("locale");
				throw new HasContractException(UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSMTCustomerHasContract", locale));
			}
			conditions.clear();
			/*conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, partyId),
					EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.EQUALS, partyId)
			), EntityJoinOperator.OR));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.NOT_EQUAL, "OWNER"));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : partyRelationships) {
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				x.store();
			}*/
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			if (party != null) {
				party.set("statusId", "PARTY_DISABLED");
				delegator.store(party);
			}
		} catch (HasContractException e) {
			result = ServiceUtil.returnError(e.getMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> listProductInventExpCusGT(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            GenericValue userLogin = (GenericValue)context.get("userLogin");
            List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts =(EntityFindOptions) context.get("opts");

            Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");

            if (MTLUtil.hasSecurityGroupPermission(delegator, "DISTRIBUTOR_ADMIN", userLogin, false)) {
                listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentsOfDistributor(delegator, userLogin.get("partyId"))));
            } else  if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false)) {
                listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentOfSalesman(delegator, userLogin)));
            } else if (!MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, true) || !MTLUtil.hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, true)) {
                listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, agentOfSupervisor(delegator, userLogin)));
            }
            listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", "SMCHANNEL_GT"));
            listSortFields.add("-fromDate");

            EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "ProductInventoryExpOfCustomerDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
            result.put("listIterator", listIterator);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object> listProductInventExpCusMT(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            GenericValue userLogin = (GenericValue)context.get("userLogin");
            List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts =(EntityFindOptions) context.get("opts");
            Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");

            listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", "SMCHANNEL_MT"));
            listSortFields.add("-fromDate");
            EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "ProductInventoryExpOfCustomerDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
            result.put("listIterator", listIterator);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return result;
    }
	public static Map<String, Object> JQGetListMTCustomerConsignee(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String partyId = (String) parameters.get("partyId")[0];
		List<GenericValue> listIterator = null;
		try {
			listSortFields.add("partyCodeFrom");
			listAllConditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
//            listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "CUSTOMER_REL"));
//            listAllConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "CUSTOMER"));
//            listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", "CONSIGNEE"));
			listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "MTCustomerRelationship", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static Map<String, Object> JQGetListMTCustomerNotHaveConsignee(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
//		String partyId = (String) parameters.get("partyId")[0];
		List<GenericValue> listIterator = null;
		try {
			listSortFields.add("partyCodeFrom");
            listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", null));
            listAllConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", null));
			listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "MTCustomerRelationship", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static Map<String, Object> addConsigneeToCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) context.get("partyIdFrom");
		String consigneeId = (String) context.get("partyIdTo");
		try {
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap(
							"partyIdFrom", partyId, "partyIdTo", consigneeId,
							"roleTypeIdFrom", "CUSTOMER", "roleTypeIdTo", "CONSIGNEE",
							"partyRelationshipTypeId", "CUSTOMER_REL", "userLogin", userLogin));
		} catch (GenericServiceException e1) {
			e1.printStackTrace();
		}
		successResult.put("partyId", partyId);
		return successResult;
	}

	public static Map<String, Object> removeCustomerFromConsignee(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) context.get("partyIdFrom");
		String consigneeId = (String) context.get("partyIdTo");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "CUSTOMER",
				"partyIdTo", consigneeId, "roleTypeIdTo", "CONSIGNEE", "partyRelationshipTypeId", "CUSTOMER_REL")));
		List<GenericValue> partyRelationships = null;
		try {
			partyRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			boolean exists = false;
			for (GenericValue x : partyRelationships) {
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				x.store();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("partyIdFrom", partyId);
		successResult.put("partyIdTo", consigneeId);
		return successResult;
	}

	public static Map<String, Object> loadAgreementDOrAInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> conds = FastList.newInstance();
		Set<String> listSelectFields = new HashSet<String>();
		List<String> listSortFields = FastList.newInstance();
		try {
			Map<String, Object> agreementInfo = FastMap.newInstance();
			String typePDF = (String) context.get("typePDF");
			String entityName = "";
			if(typePDF.equals("distributor")){
				entityName = "TermTypeAttrAgreementDistributor";
			}else if (typePDF.equals("agent")){
				entityName = "TermTypeAttrAgreementAgent";
			}
			listSortFields.add("attrName");
			listSelectFields.add("attrName");
			listSelectFields.add("attrValue");
			listSelectFields.add("attrValueTree2");
			List<GenericValue> listTerms = delegator.findList(entityName, EntityCondition.makeCondition(conds, EntityOperator.AND), listSelectFields, listSortFields, null, false);
			agreementInfo.put("listTerms", listTerms);
			result.put("agreementInfo", agreementInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> loadEndCustomerInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> EndCustomerInfo = FastMap.newInstance();
			String partyId = (String) context.get("partyId");
			GenericValue partyGroup = delegator.findOne("PartyAndPartyGroupDetail", UtilMisc.toMap("partyId", partyId), false);
			EndCustomerInfo.put("partyId", partyGroup.get("partyId"));
			EndCustomerInfo.put("statusId", partyGroup.get("statusId"));
			EndCustomerInfo.put("partyCode", partyGroup.get("partyCode"));
			EndCustomerInfo.put("partyTypeId", partyGroup.get("partyTypeId"));
			EndCustomerInfo.put("groupName", partyGroup.get("groupName"));
			EndCustomerInfo.put("officeSiteName", partyGroup.get("officeSiteName"));
			EndCustomerInfo.put("logoImageUrl", partyGroup.get("logoImageUrl"));
			EndCustomerInfo.put("comments", partyGroup.get("comments"));
			EndCustomerInfo.put("currencyUomId", partyGroup.get("preferredCurrencyUomId"));

			List<EntityCondition> conditions = FastList.newInstance();
			//	get taxAuthInfos
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM", "taxAuthPartyId", "VNM_TAX")));
			List<GenericValue> partyTaxAuthInfos = delegator.findList("PartyTaxAuthInfo",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyTaxId", "fromDate"), null, null, false);
			if (UtilValidate.isNotEmpty(partyTaxAuthInfos)) {
				GenericValue partyTaxAuthInfo = EntityUtil.getFirst(partyTaxAuthInfos);
				EndCustomerInfo.put("taxAuthInfos", partyTaxAuthInfo.get("partyTaxId"));
				EndCustomerInfo.put("taxAuthInfosfromDate", partyTaxAuthInfo.getTimestamp("fromDate").getTime());
			}
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
			List<GenericValue> partyAndContactMechs = delegator.findList("PartyAndContactMech",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : partyAndContactMechs) {
				switch (x.getString("contactMechTypeId")) {
					case "TELECOM_NUMBER":
						EndCustomerInfo.put("contactNumber", x.get("tnContactNumber"));
						EndCustomerInfo.put("contactNumberId", x.get("contactMechId"));
						break;
					case "EMAIL_ADDRESS":
						EndCustomerInfo.put("infoString", x.get("infoString"));
						EndCustomerInfo.put("infoStringId", x.get("contactMechId"));
						break;
					case "POSTAL_ADDRESS":
						EndCustomerInfo.put("address1", x.get("paAddress1"));
						if ("Y".equals(context.get("detail"))) {
							EndCustomerInfo.put("wardGeoId", DistributorServices.getGeoName(delegator, x.get("paWardGeoId")));
							EndCustomerInfo.put("districtGeoId", DistributorServices.getGeoName(delegator, x.get("paDistrictGeoId")));
							EndCustomerInfo.put("stateProvinceGeoId", DistributorServices.getGeoName(delegator, x.get("paStateProvinceGeoId")));
							EndCustomerInfo.put("countryGeoId", DistributorServices.getGeoName(delegator, x.get("paCountryGeoId")));
							GenericValue postalAddress = delegator.findOne("PostalAddressAndGeo", UtilMisc.toMap("contactMechId", x.get("contactMechId")), false);
							if (UtilValidate.isNotEmpty(postalAddress)) {
								String address = postalAddress.getString("address1") + ", " +
										postalAddress.getString("wardGeoName") + ", " +
										postalAddress.getString("districtGeoName") + ", " +
										postalAddress.getString("stateProvinceGeoName") + ", " +
										postalAddress.getString("countryGeoName");
								if (UtilValidate.isNotEmpty(address)) {
									address = address.replaceAll(", null, ", ", ");
									address = address.replaceAll(", null,", ", ");
								}
								EndCustomerInfo.put("address", address);
							}
						} else {
							EndCustomerInfo.put("wardGeoId", x.get("paWardGeoId"));
							EndCustomerInfo.put("districtGeoId", x.get("paDistrictGeoId"));
							EndCustomerInfo.put("stateProvinceGeoId", x.get("paStateProvinceGeoId"));
							EndCustomerInfo.put("countryGeoId", x.get("paCountryGeoId"));
							EndCustomerInfo.put("addressId", x.get("contactMechId"));
						}
						break;
					default:
						break;
				}
			}

			//	get representative
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", "CUSTOMER",
					"roleTypeIdFrom", "OWNER", "partyRelationshipTypeId", "OWNER")));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
			if (UtilValidate.isNotEmpty(partyRelationships)) {
				String representativeId = (String) EntityUtil.getFirst(partyRelationships).get("partyIdFrom");
				EndCustomerInfo.put("representative", DistributorServices.getRepresentative(delegator, representativeId, context.get("detail")));
			}
			//	get representative offices
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", "CHILD_MEMBER",
					"roleTypeIdFrom", "CUSTOMER", "partyRelationshipTypeId", "OWNER")));
			List<GenericValue> partyRelationshipOffices = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
			if (UtilValidate.isNotEmpty(partyRelationshipOffices)) {
				String representativeOfficeId = (String) EntityUtil.getFirst(partyRelationshipOffices).get("partyIdFrom");
				EndCustomerInfo.put("representativeOfficeId", representativeOfficeId);
			}
			// get productStoreId
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER")));
			List<GenericValue> productStores = delegator.findList("ProductStoreRole",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("productStoreId"), null, null, false);
			if (UtilValidate.isNotEmpty(productStores)) {
				String productStoreId = (String) EntityUtil.getFirst(productStores).get("productStoreId");
				EndCustomerInfo.put("productStoreId", productStoreId);
			}
			//	get supervisor
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("partyId", partyId));
			List<GenericValue> partyCustomers = delegator.findList("PartyCustomer", EntityCondition.makeCondition(conditions), null,null, null, false);
			if (partyCustomers.size()>0) {
				String supervisorId = partyCustomers.get(0).getString("supervisorId");
				String salesmanId = partyCustomers.get(0).getString("salesmanId");
				if (UtilValidate.isNotEmpty(supervisorId)) {
					EndCustomerInfo.put("supervisorId", supervisorId);
					EndCustomerInfo.put("supervisor", PartyUtil.getPartyName(delegator, supervisorId));
				}
				if (UtilValidate.isNotEmpty(salesmanId)) {
					EndCustomerInfo.put("salesmanId", salesmanId);
				}
			}
			EndCustomerInfo.putAll(getSalesman(delegator, partyId));
			result.put("EndCustomerInfo", EndCustomerInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listEndCustomers(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> listIterator = null;
		try {
			if (parameters.containsKey("sD")) {
				if ("N".equals(parameters.get("sD")[0])) {
                    listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
				}
			}
            listAllConditions.add(EntityCondition.makeCondition("partyTypeId", "END_CUSTOMER"));
			listSortFields.add("-partyCode");
			listIterator = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "PartyCustomerFullDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}
}
