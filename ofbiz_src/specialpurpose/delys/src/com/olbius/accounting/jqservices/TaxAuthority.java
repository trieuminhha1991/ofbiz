package com.olbius.accounting.jqservices;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.SalesPartyUtil;

public class TaxAuthority {
	public static final String module = TaxAuthority.class.getName();
	 	
		@SuppressWarnings("unchecked")
		public static Map<String, Object> jqGetListTaxAuthorities(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	listSortFields.add("taxAuthGeoId");
	    	listSortFields.add("taxAuthPartyId");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("TaxAuthority", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListTaxAuthorities service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 
	 	@SuppressWarnings("unchecked")
		public static Map<String, Object> jqGetListGeo(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("Geo", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListGeo service: " + e.toString();
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
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("PartyNameView", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListParty service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	@SuppressWarnings("unchecked")
		public static Map<String, Object> jqGetListPartiesRoleTypes(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
	    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
	    	String roleTypes = parameters.get("roleTypes")[0];
	    	
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		JSONArray roles = JSONArray.fromObject(roleTypes);
	    		List<EntityCondition> tmp = FastList.newInstance();
	    		for(int i = 0; i < roles.size(); i++){
	    			String role = roles.getString(i);
	    			tmp.add(EntityCondition.makeCondition("roleTypeId", role));
	    		}
	    		listAllConditions.add(EntityCondition.makeCondition(tmp, EntityOperator.OR));
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("PartyRoleAndPartyDetail", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListParty service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	
	 	@SuppressWarnings("unchecked")
	 	public static Map<String, Object> jqGetListTaxAuthorityCategories(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("taxAuthPartyId", parameters.get("taxAuthPartyId")[0]);
	    	mapCondition.put("taxAuthGeoId", parameters.get("taxAuthGeoId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("TaxAuthorityCategory", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListTaxAuthorityCategories service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	
	 	@SuppressWarnings("unchecked")
	 	public static Map<String, Object> jqGetListProductCategories(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("ProductCategory", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListProductCategories service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	
	 	@SuppressWarnings("unchecked")
	 	public static Map<String, Object> jqListTaxAuthorityAssocs(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("taxAuthPartyId", parameters.get("taxAuthPartyId")[0]);
	    	mapCondition.put("taxAuthGeoId", parameters.get("taxAuthGeoId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("TaxAuthorityAssoc", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListTaxAuthorityAssocs service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	
	 	@SuppressWarnings("unchecked")
	 	public static Map<String, Object> jqListTaxAuthorityGLAcounts(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("taxAuthPartyId", parameters.get("taxAuthPartyId")[0]);
	    	mapCondition.put("taxAuthGeoId", parameters.get("taxAuthGeoId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("TaxAuthorityGlAccount", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListTaxAuthorityGLAcounts service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	
	 	@SuppressWarnings("unchecked")
	 	public static Map<String, Object> jqListTaxAuthorityGLAccounts(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("organizationPartyId", parameters.get("organizationPartyId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		if(UtilValidate.isNotEmpty(listAllConditions)){
	    			for(int i  = 0; i < listAllConditions.size();i++){
	    				String listCondStr = listAllConditions.get(i).toString().trim();
	    				String fieldCond = listCondStr.split(" ")[0];
	    				int index = listCondStr.indexOf("%");
	    				if(index != -1){
	    					String tmpValue = listCondStr.substring(index + 1, listCondStr.length() - 1);
	    					if(fieldCond.equals("glAccountId")){
	    	    				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("accountCode",EntityJoinOperator.LIKE,"%" + tmpValue + "%"),listAllConditions.get(i)),EntityJoinOperator.OR));
	    	    				listAllConditions.remove(i);
	    	    				break;
	    					}else continue;
	    				}
	    			}
	    		}	
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("TaxAuthorityGlAccountAndGeo", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListTaxAuthorityGLAcounts service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	
	 	@SuppressWarnings("unchecked")
	 	public static Map<String, Object> jqGetListGLAccounts(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("GlAccount", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListGLAccounts service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	
	 	@SuppressWarnings("unchecked")
	 	public static Map<String, Object> jqListTaxAuthorityRateProducts(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("taxAuthPartyId", parameters.get("taxAuthPartyId")[0]);
	    	mapCondition.put("taxAuthGeoId", parameters.get("taxAuthGeoId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("TaxAuthorityRateProduct", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListTaxAuthorityRateProducts service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	
	 	@SuppressWarnings("unchecked")
	 	public static Map<String, Object> jqGetListProductStores(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("ProductStore", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqGetListProductStores service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	
	 	@SuppressWarnings("unchecked")
	 	public static Map<String, Object> jqListTaxAuthorityParties(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("taxAuthPartyId", parameters.get("taxAuthPartyId")[0]);
	    	mapCondition.put("taxAuthGeoId", parameters.get("taxAuthGeoId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("PartyTaxAuthInfo", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListTaxAuthorityParties service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	
	 	
	 	public static Map<String,Object> jqcomboboxListParty(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
	 		Delegator delegator= ctx.getDelegator();
	 		
	 		String strKeySearch = (String)context.get("searchKey");
	 		List<EntityCondition> tmpInputCond = new ArrayList<EntityCondition>();
	 		if(!"".equals(strKeySearch)){
				
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleName"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
			}
	 		
	 		List<GenericValue> partyList= delegator.findList("PartyNameView", EntityCondition.makeCondition(tmpInputCond,EntityJoinOperator.OR), null, null, null, false);
	 		List<Map<String,Object>> partListResult= FastList.newInstance();
	 		if(UtilValidate.isNotEmpty(partyList)){
	 			for(GenericValue item:partyList){
	 				String typeParty= item.getString("partyTypeId");
	 				
	 				if("PERSON".equals(typeParty)){
	 					String fullName= " ";
	 					
	 					String firsName= item.getString("firstName");
	 					String middleName=item.getString("middleName");
	 					String lastName= item.getString("lastName");
	 					
	 					if(UtilValidate.isNotEmpty(firsName)){
	 						fullName=fullName+firsName;
	 					}
	 					if(UtilValidate.isNotEmpty(middleName)){
	 						fullName=fullName+" "+middleName;
	 					}
	 					
	 					if(UtilValidate.isNotEmpty(lastName)){
	 						fullName=fullName+" "+lastName;
	 					}
	 					Map<String,Object> newParty= new HashMap<String,Object>();
	 					
	 					newParty.put("partyId", item.getString("partyId"));
	 					newParty.put("partyName", fullName);
	 					partListResult.add(newParty);
	 					
	 				}else if("PARTY_GROUP".equals(typeParty)){
 						Map<String,Object> newParty= new HashMap<String,Object>();
	 					
	 					newParty.put("partyId", item.getString("partyId"));
	 					newParty.put("partyName", item.getString("groupName"));
	 					partListResult.add(newParty);
	 				}
	 			}
	 			
	 		}
	 		Map<String, Object> successResult = ServiceUtil.returnSuccess();
	 		successResult.put("listParties", partListResult);
	 		
	 		return successResult;
	 	}
	 	public static Map<String,Object> JQGetListGLAccountsDetail(DispatchContext dpct,Map<String,?extends Object> context){
	 		Delegator delegator = dpct.getDelegator();
	 		Locale locale = (Locale) context.get("locale");
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
	 		int pagenum = Integer.parseInt(parameters.get("pagenum")[0]);
	 		int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
	    	try {
				int start = pagenum*pagesize;
				int end = start + pagesize;
				listIterator = delegator.find("GlAccount", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
				List<GenericValue> listGlAcc = listIterator.getPartialList(start, end);
	    		successResult.put("listIterator", listIterator);
			} catch (Exception e) {
				String errMsg = "Fatal error calling JQGetListGLAccountsDetail service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	 		return successResult;
	 	}
	 	
	 	public static List<String> filterByPattern(String str,String key){
	 		Pattern pattern = Pattern.compile(key);
	 		Matcher m = pattern.matcher(str);
	 		List<String> resultFilter = FastList.newInstance();
	 		while(m.find()){
	 			resultFilter.add(m.group());
	 		}
	 		return resultFilter;
	 	}
}
