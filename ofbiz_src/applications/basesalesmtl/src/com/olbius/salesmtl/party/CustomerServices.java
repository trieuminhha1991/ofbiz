package com.olbius.salesmtl.party;

import java.nio.ByteBuffer;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.olbius.acc.utils.UtilServices;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.salesmtl.util.SupUtil;

public class CustomerServices {
	
	public static final String module = CustomerServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
	
    public static Map<String,Object> getCustomerMarkersInArea(DispatchContext dpct, Map<String, Object> context) 
    		throws GenericEntityException{
		Delegator delegator = (Delegator) dpct.getDelegator();
		Map<String,Object> result = ServiceUtil.returnSuccess();
		EntityCondition cond = SupUtil.getMapCustomerCondition(dpct, context);
		List<Map<String, Object>> res = FastList.newInstance();
		try {
			if(cond != null) {
				Set<String> fieldsToSelect =  UtilMisc.toSet("partyIdFrom",
						"groupName", "latitude", "longitude", "address1");
				List<GenericValue> resList = delegator.findList("PartyGroupFromGeoView", cond, fieldsToSelect, UtilMisc.toList("partyIdTo"), null, false);
				for(GenericValue e : resList){
					Map<String, Object> o = FastMap.newInstance();
					o.putAll(e);
					o.put("customerId", e.getString("partyIdFrom"));
					res.add(o);
				}
				result.put("results", res);
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
    public static Map<String,Object> getPotentialCustomerMarkersInArea(DispatchContext dpct, Map<String, Object> context) 
    		throws GenericEntityException{
		Delegator delegator = (Delegator) dpct.getDelegator();
		Map<String,Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
		String districtGeoId = (String) context.get("districtGeoId");
		List<String> sups = (List<String>) context.get("sups");
		String partyIdTo = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		EntityCondition cond = SupUtil.getMapContactCondition(dpct, context);
		try {
			if(cond != null){
				Set<String> fieldsToSelect =  UtilMisc.toSet("partyIdFrom",
						"groupName", "latitude", "longitude", "address1");
				fieldsToSelect.addAll(UtilMisc.toSet("cityGeoName", "districtGeoName"));
				List<GenericValue> resList = delegator.findList("PartyGroupFromGeoView", cond, fieldsToSelect, UtilMisc.toList("partyIdFrom"), null, false);
				List<Map<String, Object>> res = FastList.newInstance();
				for(GenericValue e : resList){
					Map<String, Object> o = FastMap.newInstance();
					o.putAll(e);
					o.put("customerId", e.getString("partyIdFrom"));
					res.add(o);
				}
				result.put("results", res);
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
    public static Map<String,Object> getAllStoreInArea(DispatchContext dpct, Map<String, Object> context) 
    		throws GenericEntityException{
		Delegator delegator = (Delegator) dpct.getDelegator();
		Map<String,Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		LocalDispatcher dispatcher = dpct.getDispatcher();
		try {
			List<String> orgsw = PartyUtil.getDepartmentOfEmployee(delegator, partyId, UtilDateTime.nowTimestamp());
			if(UtilValidate.isNotEmpty(orgsw)){
				String curOrg = orgsw.get(0);
				Map<String, Object> input = FastMap.newInstance();
				input.put("userLogin", userLogin);
				input.put("partyId", curOrg);
				Map<String, Object> out = dispatcher.runSync("getOrganizationUnit", input);
				List<Map<String, Object>> listReturn = (List<Map<String, Object>>) out.get("listReturn");
				if(UtilValidate.isNotEmpty(listReturn)){
					List<String> org = FastList.newInstance();
					for(Map<String, Object> o : listReturn){
						String cur = (String) o.get("partyId");
						if(!org.contains(cur)){
							org.add(cur);
						}
					}
					if(UtilValidate.isNotEmpty(org)){
						context.put("sups", org);
					}
				}
				Map<String, Object> pc = getPotentialCustomerMarkersInArea(dpct, context);
				List<GenericValue> pcc = (List<GenericValue>) pc.get("results");
				Map<String, Object> cus = getCustomerMarkersInArea(dpct, context);
				List<GenericValue> cusc = (List<GenericValue>) cus.get("results");
				result.put("potentialCustomers", pcc);
				result.put("customers", cusc);
			}
		} catch (Exception e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
    
	public static Map<String, Object> updateCoordinateCustomer(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			String partyId = (String) context.get("partyId");
			String lat = (String) context.get("lat");
			String lng = (String) context.get("lng");
			String postalAddressId = (String) context.get("postalAddressId");
			String dataSourceId = (String) context.get("dataSourceId");
			String geoPointId = (String) context.get("geoPointId");
			
			Debug.log(module + "::updateCoordinateCustomer, partyId = " + partyId + ", lat = " + lat + ", lng = "
					+ ", lng = " + lng + ", postalAddressId = " + postalAddressId + ", dataSourceId = " + dataSourceId
					+ ", geoPointId = " + geoPointId);
			
			Double latitude = 15.7480949D;
			Double longitude = 101.4137231D;
			
			result.put("partyId", partyId);
			
			try {
    	        if (UtilValidate.isNotEmpty(lat)) {
    	        	latitude = Double.valueOf(lat);
    	        }
    	        if (UtilValidate.isNotEmpty(lng)) {
    	        	longitude = Double.valueOf(lng);
    	        }
            } catch (Exception e) {
            	Debug.logWarning("Error when format Double", module);
            	return ServiceUtil.returnError("Not valid latitude/longitude");
            }
			if(UtilValidate.isNotEmpty(geoPointId)) {
				GenericValue geoPoint = delegator.findOne("GeoPoint", UtilMisc.toMap("geoPointId", geoPointId), false);
				
				if(UtilValidate.isNotEmpty(geoPoint)) {
					geoPoint.put("latitude", latitude);
					geoPoint.put("longitude", longitude);
					geoPoint.put("dataSourceId", dataSourceId);
					
					delegator.store(geoPoint);
					
					//Map<String, Object> rs = dispatcher.runSync("indexAnOutlet", UtilMisc.toMap(
					Map<String, Object> rs = dispatcher.runSync("indexAPartyCustomer", UtilMisc.toMap(
							"indexName","customer",
							"partyId",partyId,
							"latitude", latitude,
							"longitude",longitude));
					
					result.put("message", "Update coordinate Success");
					return result; 
				}
			}
			
			if(UtilValidate.isNotEmpty(postalAddressId)) {
				GenericValue contact = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", postalAddressId), false);
				if(UtilValidate.isNotEmpty(contact)) {
					String gpId = contact.getString("geoPointId");
					//System.out.print("geoPointId" + gpId);
					if(UtilValidate.isNotEmpty(gpId)) {
						GenericValue geoPoint = delegator.findOne("GeoPoint", UtilMisc.toMap("geoPointId", gpId), false);
						
						if(UtilValidate.isNotEmpty(geoPoint)) {
							geoPoint.put("latitude", latitude);
							geoPoint.put("longitude", longitude);
							geoPoint.put("dataSourceId", dataSourceId);
							
							delegator.store(geoPoint);
							
							Map<String, Object> rs = dispatcher.runSync("indexAPartyCustomer", UtilMisc.toMap(
									"indexName","customer",
									"partyId",partyId,
									"latitude", latitude,
									"longitude",longitude));
							
							result.put("message", "Update coordinate Success");
							return result; 
						}
					} else {
						GenericValue newPoint = delegator.makeValue("GeoPoint");
						newPoint.put("geoPointId", delegator.getNextSeqId("GeoPoint"));
						newPoint.put("latitude", latitude);
						newPoint.put("longitude", longitude);
						newPoint.put("dataSourceId", dataSourceId);
						contact.put("geoPointId", newPoint.get("geoPointId"));
						delegator.create(newPoint);
						delegator.store(contact);
						
						Map<String, Object> rs = dispatcher.runSync("indexAPartyCustomer", UtilMisc.toMap(
								"indexName","customer",
								"partyId",partyId,
								"latitude", latitude,
								"longitude",longitude));
						
						result.put("message", "Update coordinate Success");
						return result; 
					}
				}
			}
			

			
			GenericValue pctd = delegator.findOne("PartyContactTempData", UtilMisc.toMap("partyId", partyId), false);
			
			if(UtilValidate.isNotEmpty(pctd)) {
				String  pAddressId = pctd.getString("postalAddressId");
				GenericValue contact = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", pAddressId), false);
				if(UtilValidate.isNotEmpty(contact)) {
					String gpId = contact.getString("geoPointId");
					if(UtilValidate.isNotEmpty(gpId)) {
						GenericValue geoPoint = delegator.findOne("GeoPoint", UtilMisc.toMap("geoPointId", gpId), false);
						
						if(UtilValidate.isNotEmpty(geoPoint)) {
							geoPoint.put("latitude", latitude);
							geoPoint.put("longitude", longitude);
							geoPoint.put("dataSourceId", dataSourceId);
							
							delegator.store(geoPoint);
							
							Map<String, Object> rs = dispatcher.runSync("indexAPartyCustomer", UtilMisc.toMap(
									"indexName","customer",
									"partyId",partyId,
									"latitude", latitude,
									"longitude",longitude));
							
							result.put("message", "Update coordinate Success");
							return result; 
						}
					} else {
						GenericValue newPoint = delegator.makeValue("GeoPoint");
						newPoint.put("geoPointId", delegator.getNextSeqId("GeoPoint"));
						newPoint.put("latitude", latitude);
						newPoint.put("longitude", longitude);
						newPoint.put("dataSourceId", dataSourceId);
						contact.put("geoPointId", newPoint.get("geoPointId"));
						delegator.create(newPoint);
						delegator.store(contact);
						
						Map<String, Object> rs = dispatcher.runSync("indexAPartyCustomer", UtilMisc.toMap(
								"indexName","customer",
								"partyId",partyId,
								"latitude", latitude,
								"longitude",longitude));
						
						result.put("message", "Update coordinate Success");
						return result; 
					}
				}
			} else {
				
				result.put("message", "Not found contact");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("message", "Not found contact");
		return result;
	}


	public static Map<String, Object> getOutletAddressGeoLocation(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		
		String partyId = (String)context.get("partyId");
		retSucc.put("partyId", partyId);
		Debug.log(module + "::getOutletAddressGeoLocation, partyId = " + partyId);
		
		try{
			
			List<GenericValue> outlets = delegator.findList("OutletAddressGeo", 
					EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)), 
					null, 
					null, 
					null, 
					false);
			Debug.log(module + "::getOutletAddressGeoLocation, partyId = " + partyId + ", GOT size = " + outlets.size());
			
			for(GenericValue gv: outlets){
				Debug.log(module + "::getOutletAddressGeoLocation, GOT " + gv.get("geoPointId"));
			}
			retSucc.put("outlets", outlets);
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		
		return retSucc;
	}
	
	public static Map<String, Object> getListCustomerOfSalesSup(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginPartyId = userLogin.getString("partyId");
		String supId = (String)context.get("supId");
		if(supId == null)
			supId = userLoginPartyId;
		
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		Debug.log(module + "::getListCustomerOfSalesSup, userLoginPartyId = " + userLoginPartyId + ", organizationPartyId = " + organizationId);
		
		List<EntityCondition> conds = new ArrayList<EntityCondition>();
		try{
			conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.EQUALS,supId));
			
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"PARTY_DISABLED"));
			
			List<GenericValue> lst = delegator.findList("PartyCustomerAddressGeoPointAndSalesman", 
					EntityCondition.makeCondition(conds), null,null,null, false);
			retSucc.put("results", lst);
			retSucc.put("totalRows", lst.size() + "");
			
			Debug.log(module + "::getListCustomerOfSalesSup, lst = " + lst.size());
			
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}
	
	public static Map<String, Object> getListPartyCustomers(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginPartyId = userLogin.getString("partyId");
		String partyId = (String)context.get("partyId");
		if(partyId == null)
			partyId = userLoginPartyId;
		
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		Debug.log(module + "::getListPartyCustomers, userLoginPartyId = " + userLoginPartyId + 
				", partyId = " + partyId + ", organizationPartyId = " + organizationId);
		
		List<EntityCondition> conds = new ArrayList<EntityCondition>();
		try{
			if(SalesPartyUtil.isSalessup(delegator, partyId)){
				Debug.log(module + "::getListPartyCustomers, userLoginPartyId = " + userLoginPartyId + 
						", partyId = " + partyId + " is a sales-sup, organizationPartyId = " + organizationId);
						
				conds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.EQUALS,partyId));
			}else if(SalesPartyUtil.isSalesman(delegator, partyId)){
				Debug.log(module + "::getListPartyCustomers, userLoginPartyId = " + userLoginPartyId + 
						", partyId = " + partyId + " is a salesman, organizationPartyId = " + organizationId);
				
				conds.add(EntityCondition.makeCondition("salesmanId", EntityOperator.EQUALS,partyId));
			}else{
				Debug.log(module + "::getListPartyCustomers, userLoginPartyId = " + userLoginPartyId + 
						", partyId = " + partyId + ", organizationPartyId = " + organizationId + " TAKE ALL?");
				// take all?
			}
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"PARTY_DISABLED"));
			
			List<GenericValue> lst = delegator.findList("PartyCustomerAddressGeoPointAndSalesman", 
					EntityCondition.makeCondition(conds), null,null,null, false);
			retSucc.put("results", lst);
			retSucc.put("totalRows", lst.size() + "");
			
			Debug.log(module + "::getListPartyCustomers, lst = " + lst.size());
			
		}catch(Exception ex){
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}

	public static Map<String, Object> getListAgent(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginPartyId = userLogin.getString("partyId");
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		
		List<String> sort = new ArrayList<String>();
		sort.add("createdDate");
		EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
		try {
			conditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
			conditions.add(EntityUtil.getFilterByDateExpr("fromDate","thruDate"));
			if (SalesPartyUtil.isSalesManager(delegator, userLogin.getString("partyId"))) {
				// new code
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap(
						"partyTypeId", "RETAIL_OUTLET",
						"partyIdTo", organizationId, 
						"roleTypeIdTo", "INTERNAL_ORGANIZATIO",
						"roleTypeIdFrom", "CUSTOMER", 
						"partyRelationshipTypeId", "CUSTOMER_REL")));
			} else {
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap(
						"partyTypeId", "RETAIL_OUTLET", 
						"partyIdTo", organizationId, 
						"roleTypeIdTo", "INTERNAL_ORGANIZATIO",
						"roleTypeIdFrom", "CUSTOMER", 
						"partyRelationshipTypeId", "CUSTOMER_REL")));
				if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
					conditions.add(EntityCondition.makeCondition("distributorId", EntityJoinOperator.IN, PartyWorker.getDistributorIdsBySup(delegator, userLoginPartyId)));
					Debug.log(module + "::getListAgent, userLogin is a SalesSup");
				} else if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
					conditions.add(EntityCondition.makeCondition("salesmanId", userLoginPartyId));
				} else if (SalesPartyUtil.isDistributor(delegator, userLoginPartyId)) {
					conditions.add(EntityCondition.makeCondition("distributorId", userLoginPartyId));
				}
			}
			
			List<GenericValue> listAgents = delegator.findList("PartyFromAndNameRelOutletDetail", EntityCondition.makeCondition(conditions), null, sort, findOptions, false);
			
			Debug.log(module + "::getListAgent, GOT " + listAgents.size() + " outlets");
			
			retSucc.put("results", listAgents);
			retSucc.put("totalRows", String.valueOf(listAgents.size()));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retSucc;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetCustomerRegistration(DispatchContext dpct, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = dpct.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listIterator = null;
		String[] isSelfReg = parameters.get("isSelfReg");
		String[] isAppAccount = parameters.get("isAppAccount");
		try {
			//List<String> routes = SupUtil.getAllRoute(delegator, userLoginId);
            List<GenericValue> emplInDepts = PartyUtil.getEmplInOrgAtPeriod(delegator,userLoginId);
            List<String> getListEmplIds = EntityUtil.getFieldListFromEntityList(emplInDepts,"partyCode",true); //TEMPORARY_PARTY ussing partyCode
			String partyId = userLogin.getString("partyId");
			List<String> parties = SupUtil.getSalesmanManagement(dpct, partyId);
			List<String> us = FastList.newInstance();
			List<GenericValue> uss = delegator.findList("UserLogin", EntityCondition.makeCondition("partyId", EntityOperator.IN, parties), null, null, null, false);
			for (GenericValue u : uss) {
				us.add(u.getString("userLoginId"));
			}
			List<EntityCondition> listCond = (List<EntityCondition>) context.get("listAllConditions");
			if(isAppAccount != null) listCond.add(EntityCondition.makeCondition("statusId", "PARTY_APPROVED"));
			else listCond.add(EntityCondition.makeCondition("statusId", "PARTY_CREATED"));
			if (!SalesPartyUtil.isSalesManager(delegator, userLoginId)
					&& !SalesPartyUtil.isCallCenterManager(delegator, userLoginId)
					&& !SalesPartyUtil.isCallCenter(delegator, userLoginId)
					&& !SalesPartyUtil.isSalesAdmin(delegator, userLoginId)
					&& !SalesPartyUtil.isSalesAdminManager(delegator, userLoginId)) {
				//listCond.add(EntityCondition.makeCondition("routeId", EntityOperator.IN, routes));
 				listCond.add(EntityCondition.makeCondition("salesmanId", EntityOperator.IN, getListEmplIds));
				listCond.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.IN, us));
				listCond.add(EntityCondition.makeCondition("lastUpdatedByUserLogin", EntityOperator.IN, us));
			}
			if(isSelfReg != null) listCond.add(EntityCondition.makeCondition("webappName", "mobilecustomer"));
			EntityCondition finalCond = EntityCondition.makeCondition(listCond);
			EntityFindOptions options = new EntityFindOptions();
			options.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
			listIterator = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "TemporaryPartyDetail", finalCond, null, null, UtilMisc.toList("-createdDate"), options);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> approveRequestNewCustomer(DispatchContext dpc, Map<String, Object> context) {
		Delegator delegator = dpc.getDelegator();
		LocalDispatcher dispatcher = dpc.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String customers = (String) context.get("customerId");

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			if (UtilValidate.isEmpty(customers)) {
				return ServiceUtil.returnError("Cannot find customer");
			}

			JSONArray customerArr = JSONArray.fromObject(customers);
			int size = customerArr.size();
			for (int i = 0; i < size; i++) {
				String customerId = customerArr.getString(i);
				Map<String, Object> in = FastMap.newInstance();
				GenericValue customer = delegator.findOne("TemporaryParty", UtilMisc.toMap("customerId", customerId), false);
				if (UtilValidate.isNotEmpty(customer)) {
					//String routeId = customer.getString("routeId");
					String created = customer.getString("createdByUserLogin");
					String productStoreId = customer.getString("productStoreId");
					String salesmanId = customer.getString("salesmanId");
					String stateProvinceGeoId = customer.getString("stateProvinceGeoId");
					String districtGeoId = customer.getString("districtGeoId");
					//String url = customer.getString("url");

					GenericValue us = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", created), false);
					if (UtilValidate.isEmpty(us)) {
						return ServiceUtil.returnError("No Created");
					}
					/*
					if (UtilValidate.isEmpty(routeId)) {
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSNoRoute", locale));
					}
					*/
					if (UtilValidate.isEmpty(stateProvinceGeoId) || UtilValidate.isEmpty(districtGeoId)) {
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSNoStateOrNoDistrict", locale));
					}
					/*
					if (UtilValidate.isEmpty(url)) {
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSNoUrl", locale));
					}
					*/
					if (UtilValidate.isEmpty(customer.get("partyTypeId"))) {
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSCustomerTypeMustNotBeEmpty", locale));
					}
					GenericValue productStore = null;
					if (UtilValidate.isEmpty(customer.get("productStoreId"))) {
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSPSProductStoreMustNotBeEmpty", locale));
					} else {
						productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", customer.get("productStoreId")), false);
					}
					if (UtilValidate.isEmpty(salesmanId)) salesmanId = us.getString("partyId");
					
					String customerPartyId = null;
					if ("SMCHANNEL_GT".equals(productStore.getString("salesMethodChannelEnumId"))) {
						// create customer is retail outlet (agent)
						
						/* old code
						// if productStoreId is null
						String distributorId = SupUtil.getDistributorRelated(delegator, salesmanId);
						// if productStoreId is not null
						GenericValue partyDistributor = EntityUtil.getFirst(delegator.findList("PartyDistributor", EntityCondition.makeCondition("partyCode", customer.get("productStoreId")), null, null, null, false));
						distributorId = partyDistributor.getString("partyId");
						*/
						String distributorId = productStore.getString("payToPartyId"); // new code
						
						String customerName = customer.getString("customerName");
						String groupName = customer.getString("officeSiteName");
						if (UtilValidate.isEmail(groupName)) {
							groupName = customerName;
						}
						String city = customer.getString("city");
						if (UtilValidate.isNotEmpty(city)) {
							GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
							if (UtilValidate.isNotEmpty(geo)) {
								city = geo.getString("geoName");
							} else {
								return ServiceUtil.returnError("Wrong city name");
							}
						}
						String postalCode = customer.getString("postalCode");
						if (UtilValidate.isEmpty(postalCode)) {
							postalCode = "100000";
						}
						Double latitude = customer.getDouble("latitude");
						Double longitude = customer.getDouble("longitude");

						if (UtilValidate.isNotEmpty(latitude) && UtilValidate.isNotEmpty(longitude)) {
							Map<String, Object> inll = FastMap.newInstance();
							inll.put("latitude", latitude);
							inll.put("longitude", longitude);
							inll.put("userLogin", userLogin);
							Map<String, Object> oull = dispatcher.runSync("createGeoPointDynamic", inll);
							String geoPointId = (String) oull.get("geoPointId");
							in.put("geoPointId", geoPointId);
						}
						JSONObject rep = new JSONObject();
						Date birthDate = customer.getDate("birthDay");
						if (UtilValidate.isNotEmpty(birthDate)) {
							long birth = birthDate.getTime();
							rep.put("birthDate", birth);
						}
						// prepare data for representative
						rep.put("gender", customer.get("gender"));
						rep.put("gender", customer.get("gender"));
						rep.put("partyFullName", customer.get("customerName"));
						rep.put("address1", customer.get("customerName"));
						rep.put("stateProvinceGeoId", customer.get("stateProvinceGeoId"));
						rep.put("countryGeoId", customer.get("countryGeoId"));
						rep.put("districtGeoId", customer.get("districtGeoId"));
						rep.put("wardGeoId", customer.get("wardGeoId"));
						rep.put("contactNumber", customer.get("contactNumber"));
						
						// prepare data for group vs rep
						in.put("userLogin", userLogin);
						in.put("groupName", groupName);
						in.put("officeSiteName", groupName);
						in.put("address1", customer.getString("address"));
						in.put("stateProvinceGeoId", customer.getString("stateProvinceGeoId"));
						in.put("countryGeoId", customer.getString("countryGeoId"));
						in.put("districtGeoId", customer.getString("districtGeoId"));
						in.put("wardGeoId", customer.getString("wardGeoId"));
						in.put("city", city);
						in.put("postalCode", postalCode);
						in.put("representative", rep.toString());
						in.put("salesmanId", salesmanId);
						in.put("distributorId", distributorId);
						in.put("contactNumber", customer.getString("phone"));
						in.put("logoImageUrl", customer.getString("url"));
						in.put("contactPerson", customer.getString("contactPerson"));
						in.put("comments", customer.getString("note"));
						if (UtilValidate.isNotEmpty(productStoreId)) {
							List<String> productStores = FastList.newInstance();
							in.put("productStores[]", productStores);
						}
						Map<String, Object> resultCreate = dispatcher.runSync("createAgent", in);
						if (ServiceUtil.isError(resultCreate)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreate));
						}
						
						customerPartyId = (String) resultCreate.get("partyId");
						Map<String, Object> inte = FastMap.newInstance();
						inte.put("userLogin", userLogin);
						inte.put("customerId", customerId);
						inte.put("statusId", "PARTY_APPROVED");
	                    inte.put("partyId", customerPartyId);
						dispatcher.runSync("updateRequestNewCustomer", inte);
						//SupUtil.createAndUpdateRouteStoreRelationship(delegator, dispatcher, routeId, partyId, true, true); //current not using PartyRelationship
						
					} else { // create customer MT =====================================================================
						
						//String distributorId = productStore.getString("payToPartyId"); // new code
						
						String customerName = customer.getString("customerName");
						String groupName = customer.getString("officeSiteName");
						if (UtilValidate.isEmail(groupName)) {
							groupName = customerName;
						}
						String city = customer.getString("city");
						if (UtilValidate.isNotEmpty(city)) {
							GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
							if (UtilValidate.isNotEmpty(geo)) {
								city = geo.getString("geoName");
							} else {
								return ServiceUtil.returnError("Wrong city name");
							}
						}
						String postalCode = customer.getString("postalCode");
						if (UtilValidate.isEmpty(postalCode)) {
							postalCode = "100000";
						}
						Double latitude = customer.getDouble("latitude");
						Double longitude = customer.getDouble("longitude");

						if (UtilValidate.isNotEmpty(latitude) && UtilValidate.isNotEmpty(longitude)) {
							Map<String, Object> inll = FastMap.newInstance();
							inll.put("latitude", latitude);
							inll.put("longitude", longitude);
							inll.put("userLogin", userLogin);
							Map<String, Object> oull = dispatcher.runSync("createGeoPointDynamic", inll);
							String geoPointId = (String) oull.get("geoPointId");
							in.put("geoPointId", geoPointId);
						}
						JSONObject rep = new JSONObject();
						Date birthDate = customer.getDate("birthDay");
						if (UtilValidate.isNotEmpty(birthDate)) {
							long birth = birthDate.getTime();
							rep.put("birthDate", birth);
						}
						// prepare data for representative
						rep.put("gender", customer.get("gender"));
						rep.put("partyFullName", customer.get("customerName"));
						rep.put("address1", customer.get("customerName"));
						rep.put("stateProvinceGeoId", customer.get("stateProvinceGeoId"));
						rep.put("countryGeoId", customer.get("countryGeoId"));
						rep.put("districtGeoId", customer.get("districtGeoId"));
						rep.put("wardGeoId", customer.get("wardGeoId"));
						rep.put("contactNumber", customer.get("contactNumber"));
						
						// prepare data for group vs rep
						in.put("userLogin", userLogin);
						in.put("partyTypeId", customer.get("partyTypeId"));
						in.put("groupName", groupName);
						in.put("officeSiteName", groupName);
						in.put("address1", customer.getString("address"));
						in.put("stateProvinceGeoId", customer.getString("stateProvinceGeoId"));
						in.put("countryGeoId", customer.getString("countryGeoId"));
						in.put("districtGeoId", customer.getString("districtGeoId"));
						in.put("wardGeoId", customer.getString("wardGeoId"));
						in.put("city", city);
						in.put("postalCode", postalCode);
						in.put("representative", rep.toString());
						in.put("salesmanId", salesmanId);
						//in.put("distributorId", distributorId);
						in.put("contactNumber", customer.getString("phone"));
						in.put("logoImageUrl", customer.getString("url"));
						//in.put("contactPerson", customer.getString("contactPerson"));
						in.put("comments", customer.getString("note"));
						if (UtilValidate.isNotEmpty(productStoreId)) {
							List<String> productStores = FastList.newInstance();
							productStores.add(productStoreId);
							in.put("productStores[]", productStores);
						}
						Map<String, Object> resultCreate = dispatcher.runSync("createMTCustomer", in);
						if (ServiceUtil.isError(resultCreate)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreate));
						}
						
						customerPartyId = (String) resultCreate.get("partyId");
						Map<String, Object> inte = FastMap.newInstance();
						inte.put("userLogin", userLogin);
						inte.put("customerId", customerId);
						inte.put("statusId", "PARTY_APPROVED");
	                    inte.put("partyId", customerPartyId);
						dispatcher.runSync("updateRequestNewCustomer", inte);
					}
					res.put("partyId", customerPartyId);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}

	public static Map<String,Object> updateRequestNewCustomer(DispatchContext dpct, Map<String, Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpct.getDelegator();
		String customerId = (String) context.get("customerId");
		String customerName = (String) context.get("customerName");
		try {
			GenericValue pa = delegator.findOne("TemporaryParty", UtilMisc.toMap("customerId", customerId), false);
			if (UtilValidate.isNotEmpty(pa)) {
				pa.setNonPKFields(context);
				
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String userLoginId = userLogin.getString("userLoginId");
				pa.set("lastUpdatedByUserLogin", userLoginId);
				
				pa.store();
			}
			res.put("customerId", customerId);
		} catch (Exception e) {
			Debug.logError("Can't create or update for this customer" + e.getMessage(), module);
			return ServiceUtil.returnError("Error when update customer = " + customerId + ", name = " + customerName);
		}
		return res;
	}
	
	public static Map<String, Object> approveRequestNewMobileCustomer(DispatchContext dtx,
			Map<String, Object> context) {
		Delegator delegator = dtx.getDelegator();
		LocalDispatcher dispatcher = dtx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String customers = (String) context.get("customerId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			if (UtilValidate.isEmpty(customers))
				return ServiceUtil.returnError("Cannot find customer");
			JSONArray customerArr = JSONArray.fromObject(customers);
			int size = customerArr.size();
			for (int i = 0; i < size; i++) {
				String customerId = customerArr.getString(i);
				Map<String, Object> in = FastMap.newInstance();
				GenericValue customer = delegator.findOne("TemporaryParty", UtilMisc.toMap("customerId", customerId),
						false);
				if (UtilValidate.isNotEmpty(customer)) {
					String userLoginId = customer.getString("userLoginId");
					String currentPassword = customer.getString("currentPassword");
					String productStoreId = customer.getString("productStoreId");
					String stateProvinceGeoId = customer.getString("stateProvinceGeoId");
					String districtGeoId = customer.getString("districtGeoId");
					if (UtilValidate.isEmpty(stateProvinceGeoId) || UtilValidate.isEmpty(districtGeoId)) {
						return ServiceUtil.returnError(
								UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSNoStateOrNoDistrict", locale));
					}
					GenericValue productStore = null;
					if (UtilValidate.isEmpty(customer.get("productStoreId"))) {
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels",
								"BSPSProductStoreMustNotBeEmpty", locale));
					} else {
						productStore = delegator.findOne("ProductStore",
								UtilMisc.toMap("productStoreId", productStoreId), false);
					}
					String customerPartyId = null;
					String contactMechId = null;
					if ("SMCHANNEL_GT".equals(productStore.getString("salesMethodChannelEnumId"))) {
						String distributorId = productStore.getString("payToPartyId");
						String customerName = customer.getString("customerName");
						String groupName = customer.getString("officeSiteName");
						if (UtilValidate.isEmail(groupName)) {
							groupName = customerName;
						}
						String city = customer.getString("city");
						if (UtilValidate.isNotEmpty(city)) {
							GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId),
									false);
							if (UtilValidate.isNotEmpty(geo)) {
								city = geo.getString("geoName");
							} else {
								return ServiceUtil.returnError("Wrong city name!");
							}
						}
						String postalCode = customer.getString("postalCode");
						if (UtilValidate.isEmpty(postalCode)) {
							postalCode = "100000";
						}

						Double latitude = UtilValidate.isEmpty(customer.getDouble("latitude"))?20:customer.getDouble("latitude");
						Double longitude = UtilValidate.isEmpty(customer.getDouble("longitude"))?100:customer.getDouble("longitude");
						if (UtilValidate.isNotEmpty(latitude) && UtilValidate.isNotEmpty(longitude)) {
							Map<String, Object> inll = FastMap.newInstance();
							inll.put("latitude", latitude);
							inll.put("longitude", longitude);
							inll.put("userLogin", userLogin);
							Map<String, Object> oull = dispatcher.runSync("createGeoPointDynamic", inll);
							String geoPointId = (String) oull.get("geoPointId");
							in.put("geoPointId", geoPointId);
						}
						JSONObject rep = new JSONObject();
						Date birthDate = customer.getDate("birthDay");
						if (UtilValidate.isNotEmpty(birthDate)) {
							long birth = birthDate.getTime();
							rep.put("birth", birth);
						}
						// prepare data for representative
						rep.put("gender", customer.get("gender"));
						rep.put("partyFullName", customer.get("customerName"));
						rep.put("address1", customer.get("customerName"));
						rep.put("stateProvinceGeoId", customer.get("stateProvinceGeoId"));
						rep.put("countryGeoId", customer.get("countryGeoId"));
						rep.put("districtGeoId", customer.get("districtGeoId"));
						rep.put("wardGeoid", customer.get("wardGeoId"));
						rep.put("contactNumber", customer.get("contactNumber"));

						// prepare data for group vs rep
						in.put("userLogin", userLogin);
						in.put("partyTypeId", customer.get("partyTypeId"));
						in.put("groupName", groupName);
						in.put("officeSiteName", groupName);
						in.put("address1", customer.getString("address"));
						in.put("stateProvinceGeoId", customer.getString("stateProvinceGeoId"));
						in.put("countryGeoId", customer.getString("districtGeoId"));
						in.put("wardGeoId", customer.getString("wardGeoId"));
						in.put("city", city);
						in.put("postalCode", postalCode);
						in.put("representative", rep.toString());
						in.put("distributorId", distributorId);
						in.put("contactNumber", customer.getString("phone"));
						in.put("logoImageUrl", customer.getString("url"));
						in.put("contactPerson", customer.getString("contactPerson"));
						in.put("comments", customer.getString("note"));
						in.put("statusId", "PARTY_ENABLED");
						if (UtilValidate.isNotEmpty(productStoreId)) {
							in.put("productStoreId", productStoreId);
						}
						Map<String, Object> resultCreate = dispatcher.runSync("createAgent", in);
						if (ServiceUtil.isError(resultCreate)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreate));
						}
						customerPartyId = (String) resultCreate.get("partyId");
						contactMechId = (String) resultCreate.get("contactMechId");
						Map<String, Object> inte = FastMap.newInstance();
						inte.put("userLogin", userLogin);
						inte.put("customerId", customerId);
						inte.put("statusId", "PARTY_APPROVED");
						inte.put("partyId", customerPartyId);
						dispatcher.runSync("updateRequestNewCustomer", inte);
					}else { // create customer MT =====================================================================

						String distributorId = productStore.getString("payToPartyId");
						String customerName = customer.getString("customerName");
						String groupName = customer.getString("officeSiteName");
						if (UtilValidate.isEmail(groupName)) {
							groupName = customerName;
						}
						String city = customer.getString("city");
						if (UtilValidate.isNotEmpty(city)) {
							GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId),
									false);
							if (UtilValidate.isNotEmpty(geo)) {
								city = geo.getString("geoName");
							} else {
								return ServiceUtil.returnError("Wrong city name!");
							}
						}
						String postalCode = customer.getString("postalCode");
						if (UtilValidate.isEmpty(postalCode)) {
							postalCode = "100000";
						}

						Double latitude = UtilValidate.isEmpty(customer.getDouble("latitude"))?20:customer.getDouble("latitude");
						Double longitude = UtilValidate.isEmpty(customer.getDouble("longitude"))?100:customer.getDouble("longitude");
						if (UtilValidate.isNotEmpty(latitude) && UtilValidate.isNotEmpty(longitude)) {
							Map<String, Object> inll = FastMap.newInstance();
							inll.put("latitude", latitude);
							inll.put("longitude", longitude);
							inll.put("userLogin", userLogin);
							Map<String, Object> oull = dispatcher.runSync("createGeoPointDynamic", inll);
							String geoPointId = (String) oull.get("geoPointId");
							in.put("geoPointId", geoPointId);
						}
						JSONObject rep = new JSONObject();
						Date birthDate = customer.getDate("birthDay");
						if (UtilValidate.isNotEmpty(birthDate)) {
							long birth = birthDate.getTime();
							rep.put("birth", birth);
						}
						// prepare data for representative
						rep.put("gender", customer.get("gender"));
						rep.put("partyFullName", customer.get("customerName"));
						rep.put("address1", customer.get("customerName"));
						rep.put("stateProvinceGeoId", customer.get("stateProvinceGeoId"));
						rep.put("countryGeoId", customer.get("countryGeoId"));
						rep.put("districtGeoId", customer.get("districtGeoId"));
						rep.put("wardGeoid", customer.get("wardGeoId"));
						rep.put("contactNumber", customer.get("contactNumber"));

						// prepare data for group vs rep
						in.put("userLogin", userLogin);
						in.put("partyTypeId", customer.get("partyTypeId"));
						in.put("groupName", groupName);
						in.put("officeSiteName", groupName);
						in.put("address1", customer.getString("address"));
						in.put("stateProvinceGeoId", customer.getString("stateProvinceGeoId"));
						in.put("countryGeoId", customer.getString("districtGeoId"));
						in.put("wardGeoId", customer.getString("wardGeoId"));
						in.put("city", city);
						in.put("postalCode", postalCode);
						in.put("representative", rep.toString());
						in.put("distributorId", distributorId);
						in.put("contactNumber", customer.getString("phone"));
						in.put("logoImageUrl", customer.getString("url"));
						//in.put("contactPerson", customer.getString("contactPerson"));
						in.put("comments", customer.getString("note"));
						in.put("statusId", "PARTY_ENABLED");
						if (UtilValidate.isNotEmpty(productStoreId)) {
							in.put("productStoreId", productStoreId);
						}
						Map<String, Object> resultCreate = dispatcher.runSync("createMTCustomer", in);
						if (ServiceUtil.isError(resultCreate)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreate));
						}
						customerPartyId = (String) resultCreate.get("partyId");
						contactMechId = (String) resultCreate.get("contactMechId");
						Map<String, Object> inte = FastMap.newInstance();
						inte.put("userLogin", userLogin);
						inte.put("customerId", customerId);
						inte.put("statusId", "PARTY_APPROVED");
						inte.put("partyId", customerPartyId);
						dispatcher.runSync("updateRequestNewCustomer", inte);
					}
					// create userLogin
					try{
					GenericValue userLoginToCreate = delegator.makeValue("UserLogin",
							UtilMisc.toMap("userLoginId", userLoginId));
					userLoginToCreate.set("requirePasswordChange", "N");
					userLoginToCreate.set("partyId", customerPartyId);
					userLoginToCreate.set("currentPassword", currentPassword);
					userLoginToCreate.set("enabled", "Y");
					userLoginToCreate.set("successiveFailedLogins", null);
					userLoginToCreate.set("lastLocale", "vi");
					userLoginToCreate.create();
					} catch(Exception e){
						// TODO Auto-generated catch block
						Debug.log(e.getMessage());
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSUserIsAlreadyApproved", locale));
					}
					// create role
					Map<String, Object> resultService = FastMap.newInstance();
					ArrayList<Object> roleParty = new ArrayList<>();
					roleParty.add("AGENT");
					roleParty.add("BILL_FROM_VENDOR");
					roleParty.add("BILL_TO_CUSTOMER");
					roleParty.add("CUSTOMER");
					roleParty.add("END_USER_CUSTOMER");
					roleParty.add("PLACING_CUSTOMER");
					roleParty.add("SHIP_TO_CUSTOMER");
					roleParty.add("SALES_EXECUTIVE");
					for(int a = 0; a<roleParty.size(); a++){
					resultService = dispatcher.runSync("createPartyRole",
							UtilMisc.toMap("partyId", customerPartyId, "roleTypeId", roleParty.get(a), "userLogin", userLogin));
					if (!ServiceUtil.isSuccess(resultService)) {
						return ServiceUtil.returnError("can't create party role");
					}
					}
					resultService = dispatcher.runSync("createPartyRelationship",
							UtilMisc.toMap("partyIdTo", customerPartyId, "roleTypeIdTo", "CUSTOMER", "partyIdFrom",
									"CUSTOMER_SERC", "roleTypeIdFrom", "SECURITY_GROUP", "partyRelationshipTypeId",
									"SECURITY_GROUP_REL", "fromDate", UtilDateTime.nowTimestamp(), "thruDate", null,
									"userLogin", userLogin));
					if (!ServiceUtil.isSuccess(resultService)) {
						return ServiceUtil.returnError("can't create party relationship");
					}
					// create contact mech
					GenericValue contactMech = delegator.makeValue("ContactMech");
					contactMech.set("contactMechId", delegator.getNextSeqId("ContactMech"));
					contactMech.set("contactMechTypeId", "POSTAL_ADDRESS");
					contactMech.create();
					if(!ServiceUtil.isSuccess(contactMech)){
						String errMsg = "Fatal error calling createPortOfDischarge service: ";
						return ServiceUtil.returnError(errMsg);
					}
					// create party contact mech
					GenericValue partyContactmech = delegator.makeValue("PartyContactMech", UtilMisc.toMap("partyId", customerPartyId));
					partyContactmech.set("contactMechId", contactMech.get("contactMechId"));
					partyContactmech.set("fromDate", UtilDateTime.nowTimestamp());
					partyContactmech.set("roleTypeId", "SHIP_TO_CUSTOMER");
					partyContactmech.set("allowSolicitation", "Y");
					partyContactmech.create();
					if (!ServiceUtil.isSuccess(partyContactmech)) {
						return ServiceUtil.returnError("error create party contact mech!");
					}
					// create party contactmechpurpose
					roleParty.clear();
					roleParty.add("PRIMARY_LOCATION");
					roleParty.add("SHIPPING_LOCATION");
					for(int a=0; a<roleParty.size(); a++){
					GenericValue partyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", customerPartyId));
					partyContactMechPurpose.set("contactMechId", contactMech.get("contactMechId"));
					partyContactMechPurpose.set("contactMechPurposeTypeId", roleParty.get(a));
					partyContactMechPurpose.set("fromDate", UtilDateTime.nowTimestamp());
					partyContactMechPurpose.create();
					if (!ServiceUtil.isSuccess(partyContactMechPurpose)) {
						return ServiceUtil.returnError("error create party contact mech!");
					}
					}
					// update lastOrg to UserLogin
					try {
						GenericValue userLoginNew = delegator.findOne("UserLogin",
								UtilMisc.toMap("userLoginId", userLoginId), false);
						userLoginNew.set("lastOrg",
								PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
						userLoginNew.store();
					} catch (Exception ex) {
						return ServiceUtil.returnError("can't update lastOrg to UserLogin" + ex.getMessage());
					}
					// add Userlogin to Securitygroup
					try {
						dispatcher.runSync("addUserLoginToSecurityGroupHR",
								UtilMisc.toMap("userLoginId", userLoginId, "groupId", "CUSTOMER", "fromDate",
										UtilDateTime.nowTimestamp(), "organizationId",
										PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")),
										"userLogin", userLogin));
					} catch (Exception ex) {
						return ServiceUtil.returnError(ex.getMessage());
					}
					// update postalAddressName to PartyContactTempData
					GenericValue partyContactTempData = delegator.findOne("PartyContactTempData", UtilMisc.toMap("partyId", customerPartyId), false);
					GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
					if (postalAddress != null) {
						String postalAddressName = PartyWorker.getFullNamePostalAddress(delegator, contactMechId);
						partyContactTempData.set("postalAddressName", postalAddressName);
						partyContactTempData.store();
					}
					res.put("partyId", customerPartyId);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
}
