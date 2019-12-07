package com.olbius.crm;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.security.Security;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.loyalty.LoyaltyUtil;
import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.CRMUtils;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.administration.util.UniqueUtil;
import com.olbius.basehr.util.*;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

public class CallcenterServices {
	public static final String module = CallcenterServices.class.getName();
	public static final String resource = "MarketingUiLabels";
	
	public static Map<String, Object> getContacts(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();
		try {
			String query = (String) context.get("searchKey");
			if (UtilValidate.isNotEmpty(query)) {
				String cStr = (String) context.get("conditions");
				JSONArray conditions = null;
				if (UtilValidate.isNotEmpty(cStr)) {
					conditions = JSONArray.fromObject(cStr);
				}
				int size = conditions.size() ;
				if(size > 4 || UtilValidate.isEmpty(query)){
					return ServiceUtil.returnError(UtilProperties.getMessage("CallCenterUiLabels", "InvalidCondition", locale));
				}
				if(conditions.contains("agreementId")){
					List<GenericValue> resAgr = searchAgreement(delegator, query, conditions);
					if(!resAgr.isEmpty()){
						List<GenericValue> resPer = searchPersonByAgreement(delegator, resAgr);
						List<GenericValue> resGroup = searchGroupByAgreement(delegator, resAgr);
						resPer.addAll(resGroup);
						List<Map<String, Object>> out = distinctCustomer(resPer);
						result.put("results", out);
					}else{
						List<Map<String, Object>> out = FastList.newInstance();
						result.put("results", out);
					}
				}else {
					List<GenericValue> resPer = searchPerson(delegator, query, conditions);
					List<GenericValue> resGroup = searchGroup(context, delegator, query, conditions);
					resPer.addAll(resGroup);
					List<Map<String, Object>> out = distinctCustomer(resPer);
					result.put("results", out);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("CallCenterUiLabels", "CannotGetCustomer", locale));
		}
		return result;
	}
	public static List<GenericValue> searchAgreement(Delegator delegator, String query, JSONArray conditions)
			throws GenericEntityException{
		List<EntityCondition> listCondition = FastList.newInstance();
		String cond1 = "%" + query + "%";
		List<GenericValue> resPer;
		if(conditions.contains("agreementId")){
			List<EntityCondition> dummyConditions = FastList.newInstance();
			dummyConditions.add(EntityCondition.makeCondition("agreementId", EntityComparisonOperator.LIKE, cond1));
			dummyConditions.add(EntityCondition.makeCondition("agreementCode", EntityComparisonOperator.LIKE, cond1));
			listCondition.add(EntityCondition.makeCondition(dummyConditions, EntityJoinOperator.OR));
		}else{
			resPer = FastList.newInstance();
			return resPer;
		}
		EntityFindOptions options = new EntityFindOptions();
		options.setLimit(5);
		resPer = delegator.findList("Agreement", EntityCondition.makeCondition(listCondition), null, null, options, false);
		return resPer;
	}
	public static List<GenericValue> searchPersonByAgreement(Delegator delegator, List<GenericValue> agreements) {
		List<GenericValue> resPer = FastList.newInstance();
		try {
			List<EntityCondition> listCondition = FastList.newInstance();
			Set<String> party = FastSet.newInstance();
			for(GenericValue agreement : agreements){
				String partyId = agreement.getString("partyIdFrom");
				if(!party.contains(partyId)){
					listCondition.add(EntityCondition.makeCondition("partyIdFrom", partyId));
					listCondition.add(EntityCondition.makeCondition("partyCode", partyId));
				}
			}
			EntityFindOptions options = new EntityFindOptions();
			options.setMaxRows(5);
			List<EntityCondition> end = FastList.newInstance();
			end.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.NOT_EQUAL, "POSTAL_ADDRESS"));
			end.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("CONTACT", "CUSTOMER", "INDIVIDUAL_CUSTOMER")));
			end.add(EntityCondition.makeCondition("partyTypeIdFrom", "PERSON"));
			end.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			end.add(EntityCondition.makeCondition(listCondition, EntityOperator.OR));
			end.add(EntityUtil.getFilterByDateExpr());
			Set<String> fields = UtilMisc.toSet("partyIdFrom", "firstName", "middleName", "lastName", "birthDate", "contactMechTypeId");
			fields.add("partyTypeIdFrom");
			fields.add("contactMechPurposeTypeId");
			fields.add("contactNumber");
			fields.add("areaCode");
			fields.add("countryCode");
			fields.add("roleTypeIdFrom");
			fields.add("partyCode");
			resPer = delegator.findList("PersonAndContactMechDetail", EntityCondition.makeCondition(end), fields, null, options, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resPer;
	}
	public static List<GenericValue> searchGroupByAgreement(Delegator delegator, List<GenericValue> agreements){
		List<GenericValue> resPer = FastList.newInstance();
		try {
			List<EntityCondition> listCondition = FastList.newInstance();
			Set<String> party = FastSet.newInstance();
			for(GenericValue agreement : agreements){
				String partyId = agreement.getString("partyIdFrom");
				if(!party.contains(partyId)){
					listCondition.add(EntityCondition.makeCondition("partyIdFrom", partyId));
					listCondition.add(EntityCondition.makeCondition("partyCode", partyId));
				}
			}
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			options.setMaxRows(5);
			List<EntityCondition> end = FastList.newInstance();
			end.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("CONTACT", "CUSTOMER", "INDIVIDUAL_CUSTOMER")));
			end.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.NOT_EQUAL, "POSTAL_ADDRESS"));
			end.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			end.add(EntityCondition.makeCondition("partyTypeIdFrom", EntityOperator.IN, UtilMisc.toList("SCHOOL", "BUSINESSES")));
			end.add(EntityCondition.makeCondition(listCondition, EntityOperator.OR));
			end.add(EntityUtil.getFilterByDateExpr());
			Set<String> fields = UtilMisc.toSet("partyIdFrom", "groupName", "contactMechTypeId", "contactMechPurposeTypeId", "contactNumber", "areaCode");
			fields.add("partyCode");
			fields.add("countryCode");
			fields.add("roleTypeIdFrom");
			resPer = delegator.findList("PartyGroupAndContactMechDetail", EntityCondition.makeCondition(end), fields, null, options, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resPer;
	}
	@SuppressWarnings({ "unchecked" })
	public static List<Map<String, Object>> distinctCustomer(List<GenericValue> input) {
		List<Map<String, Object>> output = FastList.newInstance();
		Map<String, Object> tmp = FastMap.newInstance();
		for(GenericValue e : input){
			Map<String, Object> n = FastMap.newInstance();
			String key = e.getString("partyIdFrom");
			n.put("partyIdFrom", key);
			if(!tmp.containsKey(key)){
				if(e.containsKey("groupName")){
					n.put("groupName", e.getString("groupName"));
				}else if(e.containsKey("firstName")){
					n.put("firstName", e.getString("firstName"));
				}
				if(e.containsKey("middleName")){
					n.put("middleName", e.getString("middleName"));
				}
				if(e.containsKey("lastName")){
					n.put("lastName", e.getString("lastName"));
				}
				if(e.containsKey("partyTypeIdFrom")){
					n.put("partyTypeIdFrom", e.getString("partyTypeIdFrom"));
				}
				if(e.containsKey("birthDate")){
					n.put("birthDate", e.getString("birthDate"));
				}
				if(e.containsKey("roleTypeIdFrom")){
					n.put("roleTypeIdFrom", e.getString("roleTypeIdFrom"));
				}
				if(e.containsKey("partyCode")){
					n.put("partyCode", e.getString("partyCode"));
				}
				String contactNumber = e.getString("contactNumber");
				if(!UtilValidate.isEmpty(contactNumber)){
					String type = e.getString("contactMechPurposeTypeId");
					List<Map<String, Object>> tm = FastList.newInstance();
					Map<String, Object> cntm = FastMap.newInstance();
					cntm.put(type, e.getString("contactNumber"));
					tm.add(cntm);
					n.put("contactNumber", tm);
				}
			}else{
				n = (Map<String, Object>) tmp.get(key);
				String contactNumber = e.getString("contactNumber");
				if(!UtilValidate.isEmpty(contactNumber)){
					if(n.containsKey("contactNumber")){
						String type = e.getString("contactMechPurposeTypeId");
						List<Map<String, Object>> tm = (List<Map<String, Object>>) n.get("contactNumber");
						Map<String, Object> cntm = FastMap.newInstance();
						cntm.put(type, e.getString("contactNumber"));
						tm.add(cntm);
						n.put("contactNumber", tm);
					}else{
						String type = e.getString("contactMechPurposeTypeId");
						List<Map<String, Object>> tm = FastList.newInstance();
						Map<String, Object> cntm = FastMap.newInstance();
						cntm.put(type, e.getString("contactNumber"));
						tm.add(cntm);
						n.put("contactNumber", tm);
					}
				}
			}
			tmp.put(key, n);
		}
		for (Entry<String, Object> entry : tmp.entrySet()){
			output.add((Map<String, Object>)entry.getValue());
		}
		return output;
	}
	public static List<GenericValue> searchPerson(Delegator delegator, String query, JSONArray conditions){
		List<GenericValue> resPer = FastList.newInstance();
		try {
			List<EntityCondition> listCondition = FastList.newInstance();
			int size = conditions.size();
			if(!conditions.contains("fullName")){
				String cond1 = "%" + query + "%";
				for(int i = 0; i < size; i++){
					String field = conditions.getString(i);
					if(!field.equals("partyId")){
						listCondition.add(EntityCondition.makeCondition(field, EntityComparisonOperator.LIKE, cond1));
					}else{
						List<EntityCondition> pt = FastList.newInstance();
						pt.add(EntityCondition.makeCondition("partyCode", EntityComparisonOperator.LIKE, cond1));
						pt.add(EntityCondition.makeCondition("partyIdFrom", EntityComparisonOperator.LIKE, cond1));
						listCondition.add(EntityCondition.makeCondition(pt, EntityOperator.OR));
					}
				}
			}else{
				if(query.contains(" ")){
					String[] tmp = query.split(" ");
					for(String q : tmp){
						String qstr = "%" + q.toUpperCase() + "%";
						listCondition.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityComparisonOperator.LIKE, qstr));
						listCondition.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleName"), EntityComparisonOperator.LIKE, qstr));
						listCondition.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityComparisonOperator.LIKE, qstr));
					}
				}else{
					String cond1 = "%" + query.toUpperCase() + "%";
					listCondition.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityComparisonOperator.LIKE, cond1));
					listCondition.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleName"), EntityComparisonOperator.LIKE, cond1));
					listCondition.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityComparisonOperator.LIKE, cond1));
				}
			}
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			options.setMaxRows(5);
			List<EntityCondition> endCond = FastList.newInstance();
			EntityCondition cond = EntityCondition.makeCondition(listCondition, EntityComparisonOperator.OR);
			endCond.add(cond);
			endCond.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("CONTACT", "CUSTOMER", "INDIVIDUAL_CUSTOMER")));
			endCond.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			endCond.add(EntityCondition.makeCondition("partyTypeIdFrom", "PERSON"));
			endCond.add(EntityUtil.getFilterByDateExpr());
//			endCond.add(EntityCondition.makeCondition(UtilMisc.toList(
//					EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, null),
//					EntityCondition.makeCondition("contactMechTypeId", EntityOperator.NOT_EQUAL, "POSTAL_ADDRESS")
//					), EntityOperator.OR));
			Set<String> fields = UtilMisc.toSet("partyIdFrom", "firstName", "middleName", "lastName", "birthDate", "contactMechTypeId");
			fields.add("partyTypeIdFrom");
			fields.add("contactMechPurposeTypeId");
			fields.add("contactNumber");
			fields.add("areaCode");
			fields.add("countryCode");
			fields.add("roleTypeIdFrom");
			fields.add("partyCode");
			EntityCondition enc = EntityCondition.makeCondition(endCond);
			resPer = delegator.findList("PersonAndContactMechDetail", enc, fields, null, options, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resPer;
	}
	public static List<GenericValue> searchGroup(Map<String, ? extends Object> context, Delegator delegator, String query, JSONArray conditions){
		List<GenericValue> resPer = FastList.newInstance();
		try {			
			List<EntityCondition> listCondition = FastList.newInstance();
			int size = conditions.size();
			if(!conditions.contains("fullName")){
				String cond1 = "%" + query + "%";
				for(int i = 0; i < size; i++){
					String field = conditions.getString(i);
					if(!field.equals("partyId")){
						listCondition.add(EntityCondition.makeCondition(field, EntityComparisonOperator.LIKE, cond1));
					}else{
//						listCondition.add(EntityCondition.makeCondition("partyCode", EntityComparisonOperator.LIKE, cond1));
						List<EntityCondition> pt = FastList.newInstance();
						pt.add(EntityCondition.makeCondition("partyCode", EntityComparisonOperator.LIKE, cond1));
						pt.add(EntityCondition.makeCondition("partyIdFrom", EntityComparisonOperator.LIKE, cond1));
						listCondition.add(EntityCondition.makeCondition(pt, EntityOperator.OR));
					}
				}
			}else{
				if(query.contains(" ")){
					String[] tmp = query.split(" ");
					for(String q : tmp){
						String qstr = "%" + q.toUpperCase() + "%";
						listCondition.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityComparisonOperator.LIKE, qstr));
					}
				}else{
					String cond1 = "%" + query.toUpperCase() + "%";
					listCondition.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityComparisonOperator.LIKE, cond1));
				}
			}
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			options.setMaxRows(5);
			List<EntityCondition> endCond = FastList.newInstance();
			EntityCondition cond = EntityCondition.makeCondition(listCondition, EntityComparisonOperator.OR);
			endCond.add(cond);
			endCond.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("CONTACT", "CUSTOMER", "INDIVIDUAL_CUSTOMER")));
			endCond.add(EntityCondition.makeCondition("partyTypeIdFrom", EntityOperator.IN, UtilMisc.toList("SCHOOL", "BUSINESSES")));
			endCond.add(EntityUtil.getFilterByDateExpr());
			endCond.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			Set<String> fields = UtilMisc.toSet("partyIdFrom", "groupName", "contactMechTypeId", "contactMechPurposeTypeId", "contactNumber", "areaCode");
			fields.add("countryCode");
			fields.add("roleTypeIdFrom");
			fields.add("partyCode");
			resPer = delegator.findList("PartyGroupAndContactMechDetail", EntityCondition.makeCondition(endCond), fields, null, options, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resPer;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> JqxGetScheduleCommunication(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			String condition = listAllConditions.toString();
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			//	get only today schedule
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Timestamp entryDateStartDay = new Timestamp(cal.getTimeInMillis());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			Timestamp entryDateEndDay = new Timestamp(cal.getTimeInMillis());
			opts.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", userLogin.getString("partyId")));
			listAllConditions.add(EntityCondition.makeCondition("statusId", "COM_SCHEDULED"));
			if (!condition.contains("entryDate")) {
				listAllConditions.add(EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN_EQUAL_TO, entryDateStartDay));
				listAllConditions.add(EntityCondition.makeCondition("entryDate", EntityOperator.LESS_THAN_EQUAL_TO, entryDateEndDay));
			}
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("+entryDate");
			EntityListIterator listIter = delegator.find("CommunicationEventDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			result.put("listIterator", listIter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> listChildrenInFamily(DispatchContext dctx, Map<String, Object> context){
		Map<String,Object> result= ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		List<Map<String, Object>> listChildren = FastList.newInstance();
		try{
			String partyId = (String)context.get("partyId");
			String includeRepresentative = (String)context.get("includeRepresentative");
			if (UtilValidate.isNotEmpty(partyId)) {
				List<String> listPartyFamilyId = CRMServices.getFamilyOfPerson(partyId, delegator);
				EntityFindOptions options = new EntityFindOptions();
				options.setMaxRows(1);
				for (String s : listPartyFamilyId) {
					List<Map<String, Object>> listChildrenId = FastList.newInstance();
					if ("true".equals(includeRepresentative)) {
						listChildrenId = getAllMemberOfFamily(s, delegator);
					} else {
						listChildrenId = CRMServices.getMemberOfFamily(s, delegator);
					}
					for (Map<String, Object> m : listChildrenId) {
						String partyIdFrom = (String) m.get("partyIdFrom");
						GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyIdFrom), false);
						if (UtilValidate.isNotEmpty(person)) {
							Map<String, Object> child = FastMap.newInstance();
							child.putAll(person);
							GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", m.get("roleTypeIdFrom")), false);
							child.put("roleTypeIdFrom", m.get("roleTypeIdFrom"));
							child.put("roleTypeFrom", roleType.get("description", locale));
							child.put("familyId", s);
							child.put("partyFullName", CRMServices.getPartyName(delegator, partyIdFrom));
							List<GenericValue> icomm = delegator.findList("CommunicationEventBrandProduct",
									EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyId", partyIdFrom),
																EntityCondition.makeCondition("currentBrandId", EntityOperator.NOT_EQUAL, null))),
									null, UtilMisc.toList("-productUsingHistoryId"), options, false);
							int size = icomm.size();
							if(size == 1){
								GenericValue cur =  icomm.get(0);
								child.put("currentBrandId", cur.getString("currentBrandId"));
								child.put("currentProductId", cur.getString("currentProductId"));
								child.put("currentBrandName", cur.getString("currentBrandName"));
								child.put("currentProductName", cur.getString("currentProductName"));
								child.put("previousBrandName", cur.getString("previousBrandName"));
								child.put("previousProductName", cur.getString("previousProductName"));
							}
							listChildren.add(child);
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		result.put("listChildren", listChildren);
		return result;
	}
	private static List<Map<String, Object>> getAllMemberOfFamily(Delegator delegator, List<String> families) throws GenericEntityException {
		List<Map<String, Object>> listMember = FastList.newInstance();
		for (String x : families) {
			listMember.addAll(getAllMemberOfFamily(x, delegator));
		}
		return listMember;
	}
	private static List<Map<String, Object>> getAllMemberOfFamily (String partyId, Delegator delegator)
			throws GenericEntityException {
		List<Map<String, Object>> listMember = FastList.newInstance();
		List<EntityCondition> listConditions = FastList.newInstance();
		listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", "HOUSEHOLD")));
		List<GenericValue> listFamily = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(listConditions), null, UtilMisc.toList("-fromDate"), null, false);
		String representativeMemberId = "";
		for (GenericValue x : listFamily) {
			Map<String, Object> member = FastMap.newInstance();
			member.put("partyIdFrom", x.getString("partyIdFrom"));
			member.put("roleTypeIdFrom", x.getString("roleTypeIdFrom"));
			if ("REPRESENTATIVE".equals(x.getString("roleTypeIdFrom"))) {
				representativeMemberId = x.getString("partyIdFrom");
			}
			listMember.add(member);
		}
		if (UtilValidate.isNotEmpty(listMember)) {
			Map<String, Object> substitute = listMember.get(0);
			Map<String, Object> member = FastMap.newInstance();
			member.put("partyIdFrom", representativeMemberId);
			member.put("roleTypeIdFrom", "REPRESENTATIVE");
			listMember.set(0, member);
			listMember.add(substitute);
		}
		Set<Map<String, Object>> set = FastSet.newInstance();
		set.addAll(listMember);
		listMember.clear();
		listMember.addAll(set);
		return listMember;
	}
	
	public static Map<String, Object> jqxGetAddressFamily(DispatchContext dctx, Map<String, Object> context)
			throws GenericEntityException, GenericServiceException{
		Map<String,Object> result= ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String partyId = (String)context.get("partyId");
		List<Map<String, Object>> listAddress = FastList.newInstance();
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				List<EntityCondition> conditions = FastList.newInstance();
				//	get PRIMARY_LOCATION to compare with SHIPPING_LOCATION
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")));
				List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				String primaryLocationId = "";
				if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
					GenericValue primaryLocation = EntityUtil.getFirst(listPartyContactMechPurpose);
					primaryLocationId = primaryLocation.getString("contactMechId");
				}
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")));
				listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				List<String> listContactMechId = EntityUtil.getFieldListFromEntityList(listPartyContactMechPurpose, "contactMechId", true);
				if (UtilValidate.isEmpty(listContactMechId) && UtilValidate.isNotEmpty(primaryLocationId)) {
					listContactMechId = addPrimaryToShippingAddress(dispatcher, userLogin, partyId, primaryLocationId);
				}
				List<GenericValue> listPostalAddress = delegator.findList("PostalAddressAndGeo",
						EntityCondition.makeCondition("contactMechId", EntityJoinOperator.IN, listContactMechId), null, null, null, false);
				for (GenericValue x : listPostalAddress) {
					Map<String,Object> mapAddress= FastMap.newInstance();
					String contactMechId = x.getString("contactMechId");
					if (contactMechId.equals(primaryLocationId)) {
						mapAddress.put("note", UtilProperties.getMessage("DmsUiLabels", "DmsPrimaryAddress", locale));
					}
					mapAddress.put("contactMechId", contactMechId);
					mapAddress.put("countryGeoId", x.getString("countryGeoId"));
					mapAddress.put("stateProvinceGeoId", x.getString("stateProvinceGeoId"));
					mapAddress.put("districtGeoId", x.getString("districtGeoId"));
					mapAddress.put("wardGeoId", x.getString("wardGeoId"));
					mapAddress.put("countryGeo", x.getString("countryGeoName"));
					mapAddress.put("stateProvinceGeo", x.getString("stateProvinceGeoName"));
					mapAddress.put("districtGeo", x.getString("districtGeoName"));
					mapAddress.put("wardGeo", x.getString("wardGeoName"));
					mapAddress.put("address1", x.getString("address1"));
					mapAddress.put("postalCode", x.getString("postalCode"));
					mapAddress.put("latitude", x.getString("latitude"));
					mapAddress.put("longitude", x.getString("longitude"));
					listAddress.add(mapAddress);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listAddress", listAddress);
		return result;
	}
	private static List<String> addPrimaryToShippingAddress(LocalDispatcher dispatcher, GenericValue userLogin, String partyId, String contactMechId)
			throws GenericServiceException {
		dispatcher.runSync("createPartyContactMechPurpose",
				UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "contactMechId", contactMechId, 
						"userLogin", userLogin));
		return UtilMisc.toList(contactMechId);
	}
	
	public static Map<String, Object> searchProducts(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		try {
			List<EntityCondition> listAllConditions = FastList.newInstance();
			List<String> listSortFields = FastList.newInstance();
			EntityFindOptions opts =(EntityFindOptions) context.get("opts");
			Locale locale = (Locale) context.get("locale");
			String query = (String) context.get("query");
			if(UtilValidate.isNotEmpty(query)){
				query = "%" + query + "%";
				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("productName", EntityOperator.LIKE, query),
													  EntityCondition.makeCondition("productId", EntityOperator.LIKE, query)), EntityOperator.OR));
			}
			boolean hasVirtualProd = false;
			List<String> productCategoryIds = FastList.newInstance();
			List<String> prodCatalogIds = FastList.newInstance();
			List<GenericValue> tmpCatalog = delegator.findList("ProdCatalog", null, null, null, null, false);
			for(GenericValue e : tmpCatalog){
				String ct = e.getString("prodCatalogId");
				prodCatalogIds.add(ct);
			}
			if (prodCatalogIds != null) {
				for (String prodCatalogIdItem : prodCatalogIds) {
					List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
					if (listCategory != null) {
						for (GenericValue categoryItem : listCategory) {
							productCategoryIds.addAll(ProductWorker.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId"), "CATALOG_CATEGORY"));
						}
					}
				}
			}
			if (UtilValidate.isNotEmpty(productCategoryIds)) {
				List<EntityCondition> mainCondList = FastList.newInstance();
                mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
                boolean activeOnly = true;
                Timestamp introductionDateLimit = (Timestamp) context.get("introductionDateLimit");
                Timestamp releaseDateLimit = (Timestamp) context.get("releaseDateLimit");
                Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
                if (activeOnly) {
                    mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
                }
                if (introductionDateLimit != null) {
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
                }
                if (releaseDateLimit != null) {
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
                }
                EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
                List<GenericValue> listProduct = delegator.findList("ProductAndCategoryMember", mainCond, null, null, opts, false);
                if (listProduct != null) {
					for (GenericValue itemProd : listProduct) {
						GenericValue itemProduct = itemProd.getRelatedOne("Product", false);
						if ("Y".equals(itemProduct.getString("isVirtual"))) {
							String colorCode = null;
							String attrName = EntityUtilProperties.getPropertyValue("dms.properties", "productAttrName.displayColor", delegator);
							if (UtilValidate.isNotEmpty(attrName)) {
								List<GenericValue> productAttrs = itemProduct.getRelated("ProductAttribute", UtilMisc.toMap("attrName", attrName), null, true);
							if (productAttrs != null && productAttrs.size() > 0) {
								GenericValue productAttr = productAttrs.get(0);
								if (productAttr != null) colorCode = productAttr.getString("attrValue");
							}
							}
							if (hasVirtualProd) listIterator.add(processGeneralProd(delegator, locale, itemProduct, null, null));
							List<GenericValue> listVariantProductAssoc = SalesUtil.getVirtualVariantAssocs(itemProduct);
							if (listVariantProductAssoc != null){
								for (GenericValue itemVariantProductAssoc : listVariantProductAssoc){
									GenericValue itemVariantProduct = itemVariantProductAssoc.getRelatedOne("AssocProduct", false);
									listIterator.add(processGeneralProd(delegator, locale, itemVariantProduct, itemProduct.getString("productId"), colorCode));
								}
							}
						} else {
							listIterator.add(processGeneralProd(delegator, locale, itemProduct, null, null));
						}
					}
                }
                listIterator = EntityMiscUtil.filterMap(listIterator, listAllConditions);
                listIterator = EntityMiscUtil.sortList(listIterator, listSortFields);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("results", listIterator);
		return result;
    }
	private static Map<String, Object> processGeneralProd(Delegator delegator, Locale locale, GenericValue product, String parentProductId, String colorCode)
			throws GenericEntityException {
		StringBuilder internalNameSearch = new StringBuilder();
		internalNameSearch.append("[");
		internalNameSearch.append(product.get("productId"));
		internalNameSearch.append("]");
		internalNameSearch.append(" ");
		internalNameSearch.append(product.get("internalName"));
		Map<String, Object> row = new HashMap<String, Object>();
		row.put("parentProductId", parentProductId);
		row.put("productId", product.get("productId"));
		row.put("productName", product.get("productName"));
		row.put("internalName", product.get("internalName"));
		row.put("internalNameSearch", internalNameSearch.toString());
		row.put("quantityUomId", product.getString("quantityUomId"));
		row.put("isVirtual", product.get("isVirtual"));
		row.put("isVariant", product.get("isVariant"));
		if (colorCode != null) {
			row.put("colorCode", colorCode);
		}
		StringBuffer features = new StringBuffer();
		if ("Y".equals(product.getString("isVariant"))) {
			List<GenericValue> productFeaturesAppl = product.getRelated("ProductFeatureAppl", UtilMisc.toMap("productFeatureApplTypeId", "STANDARD_FEATURE"), null, false);
			if (productFeaturesAppl != null) {
				Iterator<GenericValue> featureIterator = productFeaturesAppl.iterator();
				while (featureIterator.hasNext()) {
					GenericValue featureApplItem = featureIterator.next();
					GenericValue feature = featureApplItem.getRelatedOne("ProductFeature", true);
					features.append(feature.get("description", locale));
					if (featureIterator.hasNext()) {
						features.append(", ");
					}
				}
			}
		}
		row.put("features", features.toString());
		// column: packingUomId
		EntityCondition condsItem = EntityCondition.makeCondition(UtilMisc.toMap("productId", product.get("productId"), "uomToId", product.get("quantityUomId")));
		EntityFindOptions optsItem = new EntityFindOptions();
		optsItem.setDistinct(true);
		List<GenericValue> listConfigPacking = FastList.newInstance();
		listConfigPacking.addAll(delegator.findList("ConfigPackingAndUom", condsItem, null, null, optsItem, false));
		List<Map<String, Object>> listQuantityUomIdByProduct = new ArrayList<Map<String, Object>>();
		for (GenericValue conPackItem : listConfigPacking) {
			Map<String, Object> packingUomIdMap = FastMap.newInstance();
			packingUomIdMap.put("description", conPackItem.getString("descriptionFrom"));
			packingUomIdMap.put("uomId", conPackItem.getString("uomFromId"));
			listQuantityUomIdByProduct.add(packingUomIdMap);
		}
		GenericValue quantityUom = delegator.findOne("Uom", UtilMisc.toMap("uomId", product.get("quantityUomId")), false);
		if (quantityUom != null) {
			Map<String, Object> packingUomIdMap = FastMap.newInstance();
			packingUomIdMap.put("description", quantityUom.getString("description"));
			packingUomIdMap.put("uomId", quantityUom.getString("uomId"));
			listQuantityUomIdByProduct.add(packingUomIdMap);
		}
		row.put("packingUomIds", listQuantityUomIdByProduct);
		return row;
	}
	
	public static Map<String, Object> getLiabilityAgreementCustomer(DispatchContext dctx, Map<String, ? extends Object> context) {
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		try{
			Set<String> tmp = FastSet.newInstance();
			List<GenericValue> agre = delegator.findList("Agreement", EntityCondition.makeCondition("partyIdFrom", partyId), null, null, null, false);
			for(GenericValue e : agre){
				String ag = e.getString("partyIdTo");
				if(tmp.isEmpty() || !tmp.contains(ag)){
					tmp.add(ag);
				}
			}
			List<Map<String, Object>> listLiability = FastList.newInstance();
			for(String t : tmp){
				Map<String, Object> inp = FastMap.newInstance();
				inp.putAll(context);
				inp.put("organizationPartyId", t);
				Map<String, Object> out = dispatcher.runSync("getLiabilityParty", inp);
				listLiability.add(out);
			}
			result.put("results", listLiability);
		} catch (Exception e){
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("CallCenterUiLabels", "CannotGetLiability", locale));
		}
		return result;
	}
	
	public static Map<String, Object> listMemberInFamilyDropdown(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException{
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String partyId = (String)context.get("partyId");
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
		String partyTypeId = party.getString("partyTypeId");
		List<Map<String, Object>> listMember = FastList.newInstance();
		if ("PERSON".equals(partyTypeId)) {
			List<String> listPartyFamilyId = CRMServices.getFamilyOfPerson(partyId, delegator);
			for (String s : listPartyFamilyId) {
				List<Map<String, Object>> listMemberId = getAllMemberOfFamily(s, delegator);
				for (Map<String, Object> m : listMemberId) {
					String partyIdFrom = (String) m.get("partyIdFrom");
					Map<String, Object> memberInfo = FastMap.newInstance();
					memberInfo.put("partyId", partyIdFrom);
					memberInfo.put("partyFullName", CRMServices.getPartyName(delegator, partyIdFrom));
					listMember.add(memberInfo);
				}
			}
		} else {
			GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
			if (UtilValidate.isNotEmpty(partyGroup)) {
				Map<String, Object> memberInfo = FastMap.newInstance();
				memberInfo.put("partyId", partyGroup.getString("partyId"));
				memberInfo.put("partyFullName", partyGroup.getString("groupName"));
				listMember.add(memberInfo);
			}
		}
		result.put("listMemberInFamilyDropdown", listMember);
		return result;
	}
	
	public static Map<String, Object> checkPermissionWithCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Security se = ctx.getSecurity();
		Map<String, Object>  permission = FastMap.newInstance();
		boolean access = false;
		if (se.hasPermission("CALLCAMPAIGN_ADMIN", userLogin)) {
			access = true;
		} else {
			String partyId = (String) context.get("partyId");
			String userLoginPartyId = userLogin.getString("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", userLoginPartyId, "partyIdTo", partyId, "roleTypeIdFrom",
					"CALLCENTER_EMPL", "roleTypeIdTo", "CONTACT")));
			try {
				List<GenericValue> partyCampaignRelationships = delegator.findList("PartyCampaignRelationship",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(partyCampaignRelationships)) {
					access = false;
				} else {
					access = true;
					GenericValue partyCampaignRelationship = EntityUtil.getFirst(partyCampaignRelationships);
					permission.put("marketingCampaignId", partyCampaignRelationship.get("marketingCampaignId"));
					permission.put("roleTypeIdFrom", partyCampaignRelationship.get("roleTypeIdFrom"));
					permission.put("roleTypeIdTo", partyCampaignRelationship.get("roleTypeIdTo"));
					permission.put("fromDate", partyCampaignRelationship.getTimestamp("fromDate").getTime());
				}
			} catch (GenericEntityException e) {
				access = false;
				e.printStackTrace();
			}
		}
		permission.put("access", access);
		result.put("permission", permission);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCommunications(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
			Security se = dctx.getSecurity();
			opts.setDistinct(true);
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyIdCaller = userLogin.getString("partyId");
			if(UtilValidate.isNotEmpty(parameters.get("partyId"))){
				List<EntityCondition> tmp = FastList.newInstance();
				String partyId = (String) parameters.get("partyId")[0];
				tmp.add(EntityCondition.makeCondition("partyIdFrom", partyId));
				tmp.add(EntityCondition.makeCondition("partyIdTo", partyId));
				listAllConditions.add(EntityCondition.makeCondition(tmp, EntityOperator.OR));
				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", "COM_COMPLETE"),
						EntityCondition.makeCondition("statusId", null)), EntityOperator.OR));
			}
			if(UtilValidate.isNotEmpty(parameters.get("onlyCaller"))){
				String onlyCaller = (String) parameters.get("onlyCaller")[0];
				if(onlyCaller.equals("Y")){
					int i = 0;
					List<EntityCondition> listC = FastList.newInstance();
					List<Map<String, Object>> listMapConditions = CRMUtils.makeListConditions(listAllConditions);
					boolean snake = false;
					for (Map<String, Object> m : listMapConditions) {
						String field = (String) m.get("fieldName");
						String value = (String) m.get("value");
						if(field.equals("fullName")){
							List<EntityCondition> ftmp1 = FastList.newInstance();
							ftmp1.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyFullNameFrom"), EntityOperator.LIKE,"%"+ value.toUpperCase()+"%"));
							ftmp1.add(EntityCondition.makeCondition("partyIdTo", partyIdCaller));
							List<EntityCondition> ftmp2 = FastList.newInstance();
							ftmp2.add(EntityCondition.makeCondition("partyIdFrom", partyIdCaller));
							ftmp2.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyFullNameTo"), EntityOperator.LIKE,"%"+ value.toUpperCase()+"%"));

							listC.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(ftmp1),
									EntityCondition.makeCondition(ftmp2)), EntityOperator.OR));
						}else if(field.equals("customerPartyId")){
							snake = true;
							List<EntityCondition> ftmp1 = FastList.newInstance();
							ftmp1.add(EntityCondition.makeCondition("partyCodeFrom", EntityOperator.LIKE, "%" + value + "%"));
							ftmp1.add(EntityCondition.makeCondition("partyIdTo", partyIdCaller));
							List<EntityCondition> ftmp2 = FastList.newInstance();
							ftmp2.add(EntityCondition.makeCondition("partyIdFrom", partyIdCaller));
							ftmp2.add(EntityCondition.makeCondition("partyCodeTo", EntityOperator.LIKE,"%" + value + "%"));
							listC.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(ftmp1),
									EntityCondition.makeCondition(ftmp2)), EntityOperator.OR));
						}else if(field.equals("callerPartyId")){
							continue;
						}else{
							listC.add(listAllConditions.get(i));
						}
						i++;
					}
					listAllConditions = FastList.newInstance();
					listAllConditions.addAll(listC);
					List<EntityCondition> tmp = FastList.newInstance();
					if(!snake){
						if(!se.hasPermission("CALLCAMPAIGN_ADMIN", userLogin)){
							tmp.add(EntityCondition.makeCondition("partyIdFrom", partyIdCaller));
							tmp.add(EntityCondition.makeCondition("partyIdTo", partyIdCaller));
						}else{
							if(UtilValidate.isNotEmpty(parameters.get("callerPartyId"))){
								String callerPartyId = (String) parameters.get("callerPartyId")[0];
								List<String> callers = Arrays.asList(callerPartyId.split("\\|LOVE\\|"));
								if (UtilValidate.isNotEmpty(callers.get(0))) {
									tmp.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.IN, callers));
									tmp.add(EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.IN, callers));
								}
							}
						}
						listAllConditions.add(EntityCondition.makeCondition(tmp, EntityOperator.OR));
					}
					listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL ,"COM_SCHEDULED"),
							EntityCondition.makeCondition("statusId", null)), EntityOperator.OR));
				}
			}
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("entryDate DESC");
			EntityCondition endCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
			EntityListIterator listIter = delegator.find("CommunicationEventDetail", endCond, null, null, listSortFields, opts);
			result.put("listIterator", listIter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> getUserInfoEditable(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> resultParty = FastMap.newInstance();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyId = (String) context.get("partyId");
			GenericValue partyInfo = null;
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			resultParty.put("partyCode", party.get("partyCode"));
			if (UtilValidate.isNotEmpty(party)) {
				String partyTypeId = party.getString("partyTypeId");
				String dataSourceId = party.getString("dataSourceId");
				if (partyTypeId.equals("PERSON")) {
					partyInfo = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
					if (UtilValidate.isNotEmpty(partyInfo)) {
						resultParty.putAll(partyInfo);
						List<String> listFamilyId = CRMServices.getFamilyOfPerson(partyId, delegator);
						resultParty.put("listFamilyId", listFamilyId);
					}
				} else {
					partyInfo = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
					if (UtilValidate.isNotEmpty(partyInfo)) {
						resultParty.putAll(partyInfo);
						String partyRepresentativeId = CRMServices.getPartyRepresentative(partyInfo.getString("partyId"), delegator);
						GenericValue personRepresentative = delegator.findOne("Person", UtilMisc.toMap("partyId", partyRepresentativeId), false);
						Map<String, Object> representative = FastMap.newInstance();
						Map<String, Object> getContactMechRepresentative = dispatcher.runSync("getContactMechOfParty", UtilMisc.toMap("partyId", partyRepresentativeId, "userLogin", userLogin));
						representative.putAll(getContactMechRepresentative);
						representative.putAll(personRepresentative);
						resultParty.put("representative", representative);
					}
					if (partyTypeId.equals("SCHOOL")) {
						GenericValue student = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", partyId, "attrName", "Student"), false);
						if(!UtilValidate.isEmpty(student)){
							resultParty.put("student", student.getString("attrValue"));
						}
						GenericValue teacher = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", partyId, "attrName", "Teacher"), false);
						if(!UtilValidate.isEmpty(teacher)){
							resultParty.put("teacher", teacher.getString("attrValue"));
						}
					}
				}
				Map<String, Object> getContactMechParty = dispatcher.runSync("getContactMechOfParty", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
				@SuppressWarnings("unchecked")
				Map<String, Object> infoContactMechParty = (Map<String, Object>) getContactMechParty.get("infoContactMechParty");
				resultParty.putAll(infoContactMechParty);
				resultParty.put("primaryPhoneUsing", getPhoneTypeUsingOfParty(delegator, partyId, "PRIMARY_PHONE"));
				resultParty.put("shippingPhoneUsing", getPhoneTypeUsingOfParty(delegator, partyId, "PHONE_SHIPPING"));
				resultParty.put("partyTypeId", partyTypeId);
				resultParty.put("dataSourceId", dataSourceId);
				resultParty.put("partyRole", dispatcher.runSync("getCustomerRole",
						UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdTo", "INTERNAL_ORGANIZATIO", "userLogin", userLogin)).get("partyRole"));
				resultParty.put("ownerEmployee", ownerEmployee(delegator, partyId));
			}
		}catch(Exception e){
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("CallCenterUiLabels", "CannotGetUserInfo", locale));
		}
		result.put("results", resultParty);
		return result;
	}
	private static Map<String, Object> ownerEmployee(Delegator delegator, String partyIdTo) throws GenericEntityException {
		Map<String, Object> ownerEmployee = FastMap.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdFrom", "CALLCENTER_EMPL")));
		List<GenericValue> partyCampaignRelationships = delegator.findList("PartyCampaignRelationship",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom", "marketingCampaignId", "fromDate"), null, null, false);
		if (UtilValidate.isNotEmpty(partyCampaignRelationships)) {
			GenericValue partyCampaignRelationship = EntityUtil.getFirst(partyCampaignRelationships);
			ownerEmployee.putAll(partyCampaignRelationship);
			ownerEmployee.put("fromDate", partyCampaignRelationship.getTimestamp("fromDate").getTime());
		} else {
			ownerEmployee.put("partyIdFrom", "N_A");
		}
		return ownerEmployee;
	}
	private static String getPhoneTypeUsingOfParty(Delegator delegator, String partyId, String contactMechPurposeTypeId) throws GenericEntityException {
		List<EntityCondition> listConditions = FastList.newInstance();
		listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", contactMechPurposeTypeId)));
		List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
				EntityCondition.makeCondition(listConditions), null, null, null, false);
		if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
			GenericValue partyContactMechPurpose = EntityUtil.getFirst(listPartyContactMechPurpose);
			String contactMechId = partyContactMechPurpose.getString("contactMechId");
			listConditions.clear();
			listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId)));
			listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
					EntityCondition.makeCondition(listConditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
				partyContactMechPurpose = EntityUtil.getFirst(listPartyContactMechPurpose);
				for (GenericValue x : listPartyContactMechPurpose) {
					if (!"PHONE_SHIPPING".equals(x.getString("contactMechPurposeTypeId")) && !"PRIMARY_PHONE".equals(x.getString("contactMechPurposeTypeId"))) {
						return x.getString("contactMechPurposeTypeId");
					}
				}
			}
		}
		return null;
	}
	
	public static Map<String, Object> getContactMechOfParty(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> infoContactMechParty = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listPrimaryLocation = FastList.newInstance();
		List<Map<String, Object>> listShippingLocation = FastList.newInstance();
		try {
			String partyId = (String) context.get("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
					EntityCondition.makeCondition(conditions), null, UtilMisc.toList("fromDate DESC"), null, false);
			for (GenericValue x : listPartyContactMechPurpose) {
				Map<String, Object> mapAddress = FastMap.newInstance();
				String contactMechId = x.getString("contactMechId");
				String contactMechPurposeTypeId = x.getString("contactMechPurposeTypeId");
				GenericValue telecomNumber = null;
				GenericValue postalAddress = null;
				GenericValue contactMech = null;
				switch (contactMechPurposeTypeId) {
				case "PRIMARY_EMAIL":
					contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", contactMechId), false);
					infoContactMechParty.put("emailAddress", contactMech.getString("infoString"));
					infoContactMechParty.put("emailAddressId", contactMechId);
					break;
				case "FACEBOOK_URL":
					contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", contactMechId), false);
					infoContactMechParty.put("facebook", contactMech.getString("infoString"));
					infoContactMechParty.put("facebookId", contactMechId);
					break;
				case "PHONE_HOME":
					telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId), false);
					infoContactMechParty.put("phoneHome", telecomNumber.getString("contactNumber"));
					infoContactMechParty.put("phoneHomeId", contactMechId);
					break;
				case "PHONE_WORK":
					telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId), false);
					infoContactMechParty.put("phoneWork", telecomNumber.getString("contactNumber"));
					infoContactMechParty.put("phoneWorkId", contactMechId);
					break;
				case "PHONE_MOBILE":
					telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId), false);
					infoContactMechParty.put("phoneMobile", telecomNumber.getString("contactNumber"));
					infoContactMechParty.put("phoneMobileId", contactMechId);
					break;
				case "PHONE_SHIPPING":
					telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId), false);
					infoContactMechParty.put("phoneShipping", telecomNumber.getString("contactNumber"));
					break;
				case "PRIMARY_PHONE":
					telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId), false);
					infoContactMechParty.put("primaryPhone", telecomNumber.getString("contactNumber"));
					break;
				case "PRIMARY_LOCATION":
					postalAddress = delegator.findOne("PostalAddressAndGeo", UtilMisc.toMap("contactMechId", contactMechId), false);
					String primaryLocation = "";
					primaryLocation = postalAddress.getString("address1") + ", " + 
									postalAddress.getString("wardGeoName") + ", " + 
									postalAddress.getString("districtGeoName") + ", " + 
									postalAddress.getString("stateProvinceGeoName") + ", " + 
									postalAddress.getString("countryGeoName");
					if (UtilValidate.isNotEmpty(primaryLocation)) {
						primaryLocation = primaryLocation.replaceAll(", null, ", ", ");
						primaryLocation = primaryLocation.replaceAll(", null,", ", ");
					}
					mapAddress.put("contactMechId", postalAddress.getString("contactMechId"));
					mapAddress.put("countryGeoId", postalAddress.getString("countryGeoId"));
					mapAddress.put("provinceGeoId", postalAddress.getString("stateProvinceGeoId"));
					mapAddress.put("districtGeoId", postalAddress.getString("districtGeoId"));
					mapAddress.put("wardGeoId", postalAddress.getString("wardGeoId"));
					mapAddress.put("address", postalAddress.getString("address1"));
					mapAddress.put("primaryLocation", primaryLocation);
					mapAddress.put("primaryLocationId", contactMechId);
					listPrimaryLocation.add(mapAddress);
					break;
				case "SHIPPING_LOCATION":
					postalAddress = delegator.findOne("PostalAddressAndGeo", UtilMisc.toMap("contactMechId", contactMechId), false);
					String shippingLocation = "";
					shippingLocation = postalAddress.getString("address1") + ", " + 
									postalAddress.getString("wardGeoName") + ", " + 
									postalAddress.getString("districtGeoName") + ", " + 
									postalAddress.getString("stateProvinceGeoName") + ", " + 
									postalAddress.getString("countryGeoName");
					if (UtilValidate.isNotEmpty(shippingLocation)) {
						shippingLocation = shippingLocation.replaceAll(", null, ", ", ");
						shippingLocation = shippingLocation.replaceAll(", null,", ", ");
					}
					mapAddress.put("contactMechId", postalAddress.getString("contactMechId"));
					mapAddress.put("countryGeoId", postalAddress.getString("countryGeoId"));
					mapAddress.put("provinceGeoId", postalAddress.getString("stateProvinceGeoId"));
					mapAddress.put("districtGeoId", postalAddress.getString("districtGeoId"));
					mapAddress.put("wardGeoId", postalAddress.getString("wardGeoId"));
					mapAddress.put("address", postalAddress.getString("address1"));
					mapAddress.put("shippingLocation", shippingLocation);
					mapAddress.put("shippingLocationId", contactMechId);
					listShippingLocation.add(mapAddress);
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		infoContactMechParty.put("listPrimaryLocation", listPrimaryLocation);
		infoContactMechParty.put("listShippingLocation", listShippingLocation);
		result.put("infoContactMechParty", infoContactMechParty);
		return result;
	}
	
	public static Map<String, Object> saveMember(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			if (UtilValidate.isNotEmpty((String) context.get("children"))) {
				List<String> listMemberId = FastList.newInstance();
				listMemberId.add((String) context.get("partyId"));
				JSONArray childrenArray = JSONArray.fromObject((String) context.get("children"));
				for(int i = 0; i< childrenArray.size(); i++){
					JSONObject o = childrenArray.getJSONObject(i);
					String personId = null;
					if (o.containsKey("partyId")) {
						personId = cleanString(o.getString("partyId"));
					}
					if (context.get("representativeMemberId").equals(personId)) {
						continue;
					}
					String partyFullName = null;
					if (o.containsKey("partyFullName")) {
						partyFullName = cleanString(o.getString("partyFullName"));
					}
					String gender = null;
					if (o.containsKey("gender")) {
						gender = cleanString(o.getString("gender"));
					}
					String roleTypeIdFrom = null;
					if (o.containsKey("roleTypeIdFrom")) {
						roleTypeIdFrom = cleanString(o.getString("roleTypeIdFrom"));
					}
					if ("REPRESENTATIVE".equals(roleTypeIdFrom)) {
						continue;
					}
					if (UtilValidate.isEmpty(partyFullName) || UtilValidate.isEmpty(roleTypeIdFrom)) {
						continue;
					}
					Long birthDateL = null;
					if (o.containsKey("birthDate")) {
						if (o.get("birthDate") instanceof Long) {
							birthDateL = o.getLong("birthDate");
						}
					}
					Map<String, Object> rs = dispatcher.runSync("createOrStoreMemberStep2",
							UtilMisc.toMap("partyId", personId, "familyId", (String) context.get("familyId"), "partyFullName", partyFullName,
									"roleTypeIdFrom", roleTypeIdFrom, "gender", gender, "birthDate", birthDateL, "userLogin", userLogin));
					personId = (String) rs.get("partyId");
					listMemberId.add(personId);
				}
				if (UtilValidate.isNotEmpty(listMemberId)) {
					List<EntityCondition> conditions = FastList.newInstance();
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.NOT_IN, listMemberId));
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", (String) context.get("familyId"), "roleTypeIdTo", "HOUSEHOLD")));
					List<GenericValue> listMemberWillDelete = delegator.findList("PartyRelationship",
							EntityCondition.makeCondition(conditions), null, null, null, false);
					for (GenericValue x : listMemberWillDelete) {
						x.set("thruDate", new Timestamp(System.currentTimeMillis()));
						delegator.store(x);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static String cleanString(Object originalString ) {
		String pureString = null;
		if (UtilValidate.isNotEmpty(originalString)) {
			if (!UtilMisc.toList("null", "undefined").contains(originalString)) {
				pureString = originalString.toString().trim();
			}
		}
		return pureString;
	}
	
	public static Map<String, Object> createOrStoreMemberStep2(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) context.get("partyId");
		try {
			String familyId = (String) context.get("familyId");
			String partyFullName = (String) context.get("partyFullName");
			String gender = (String) context.get("gender");
			String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
			Object birthDateOjb = context.get("birthDate");
			Date birthDate = null;
			if (birthDateOjb instanceof Long) {
				Long birthDateL = (Long) context.get("birthDate");
				if (UtilValidate.isNotEmpty(birthDateL)) {
					birthDate = new java.sql.Date(birthDateL);
				}
			} else {
				birthDate = (Date) context.get("birthDate");
			}
			partyId = createPartyAndRelationship(delegator, dispatcher, userLogin, familyId, partyId, roleTypeIdFrom);
			Map<String, Object> mapPerson = FastMap.newInstance();
			mapPerson.put("partyId", partyId);
			mapPerson.put("gender", gender);
			mapPerson.put("birthDate", birthDate);
			mapPerson.putAll(demarcatePersonName(partyFullName));
			GenericValue person = delegator.makeValue("Person", mapPerson);
			delegator.createOrStore(person);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		result.put("partyId", partyId);
		return result;
	}
	private static String createPartyAndRelationship(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String familyId, String partyId, String roleTypeIdFrom)
			throws GenericEntityException, GenericServiceException {
		//	check party available
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
		//	if not available create new it
		if (UtilValidate.isEmpty(party)) {
			partyId = delegator.getNextSeqId("Party");
			delegator.create("Party", UtilMisc.toMap("partyId", partyId, "partyTypeId", "PERSON", "statusId", "PARTY_ENABLED"));
		}
		//	make relationship with family
		dispatcher.runSync("addMemberToFamily", UtilMisc.toMap("partyId", partyId, "familyId", familyId, "roleTypeIdFrom", roleTypeIdFrom, "userLogin", userLogin));
		return partyId;
	}
	public static Map<String, Object> processName(DispatchContext dpc, Map<String, Object> context){
		String fullName = (String) context.get("fullName");
		return demarcatePersonName(fullName);
	}
	public static Map<String, Object> demarcatePersonName(String partyFullName) {
		Map<String,Object> result = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(partyFullName)) {
			String[] arrayName = partyFullName.split("\\s+");
			switch (arrayName.length) {
			case 1:
				result.put("firstName", arrayName[0].trim());
				result.put("middleName", null);
				result.put("lastName", null);
				break;
			case 2:
				result.put("lastName", arrayName[0].trim());
				result.put("middleName", null);
				result.put("firstName", arrayName[1].trim());
				break;
			case 3:
				result.put("lastName", arrayName[0].trim());
				result.put("middleName", arrayName[1].trim());
				result.put("firstName", arrayName[2].trim());
				break;
			default:
				if (arrayName.length > 3) {
					result.put("lastName", arrayName[0].trim());
					result.put("middleName", arrayName[1].trim());
					String firstName = "";
					for (int i = 2; i < arrayName.length; i++) {
						firstName += arrayName[i] + " ";
					}
					result.put("firstName", firstName.trim());
				}
				break;
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> checkDuplicateNumberPhone(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		boolean isDuplicate = false;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String contactNumber = (String) context.get("contactNumber");
			
			List<GenericValue> listTelecomNumber = delegator.findList("TelecomNumber",
					EntityCondition.makeCondition("contactNumber", EntityJoinOperator.EQUALS, contactNumber), null, null, null, false);
			if (UtilValidate.isNotEmpty(listTelecomNumber)) {
				isDuplicate = true;
				GenericValue telecomNumber = EntityUtil.getFirst(listTelecomNumber);
				String contactMechId = telecomNumber.getString("contactMechId");
				List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
						EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contactMechId)), null, null, null, false);
				GenericValue partyContactMechPurpose = EntityUtil.getFirst(listPartyContactMechPurpose);
				String partyId = partyContactMechPurpose.getString("partyId");
				result.put("partyId", partyId);
				List<String> listFamily = CRMServices.getFamilyOfPerson(partyId, delegator);
				if (UtilValidate.isNotEmpty(listFamily)) {
					result.put("familyId", listFamily.get(0));
				}
				result.put("partyFullName", CRMServices.getPartyName(delegator, partyId));
				Map <String, Object> getContactMechOfParty = dispatcher.runSync("getContactMechOfParty",
						UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
				Map<String,Object> infoContactMechParty = (Map<String, Object>) getContactMechOfParty.get("infoContactMechParty");
				Map<String,Object> partyInfo = FastMap.newInstance();
				partyInfo.put("partyFullName", CRMServices.getPartyName(delegator, partyId));
				partyInfo.putAll(infoContactMechParty);
				
				List<Map<String, Object>> listPrimaryLocation = (List<Map<String, Object>>) infoContactMechParty.get("listPrimaryLocation");
				if (UtilValidate.isNotEmpty(listPrimaryLocation)) {
					Map<String, Object> primaryLocation = listPrimaryLocation.get(0);
					String address = (String) primaryLocation.get("primaryLocation");
					partyInfo.put("address", address);
				}
				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				if (UtilValidate.isNotEmpty(person)) {
					partyInfo.putAll(person);
				}
				result.put("partyInfo", partyInfo);
 			}
		} catch (Exception e) {
			isDuplicate = false;
			e.printStackTrace();
		}
		result.put("isDuplicate", isDuplicate);
		return result;
	}
	
	public static Map<String, Object> addMemberToFamily(DispatchContext dctx, Map<String, Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		try {
			String familyId = (String)context.get("familyId");
			String partyId = (String)context.get("partyId");
			String roleTypeIdFrom = (String)context.get("roleTypeIdFrom");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", familyId, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", "HOUSEHOLD")));
			List<GenericValue> listPartyRelationship = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isEmpty(listPartyRelationship)) {
				List<GenericValue> checkRole = delegator.findList("PartyRole",
						EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeIdFrom)), null, null, null, false);
				if (UtilValidate.isEmpty(checkRole)) {
					dispatcher.runSync("createPartyRole",
							UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeIdFrom,"userLogin", userLogin));
				}
				dispatcher.runSync("createPartyRelationship",
						UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", familyId, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", "HOUSEHOLD", "partyRelationshipTypeId", "PARTNERSHIP", "userLogin", userLogin));
			}
			//	check other relationship in this family
			//	deletePartyRelationship
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", familyId, "roleTypeIdTo", "HOUSEHOLD")));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.NOT_EQUAL, roleTypeIdFrom));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.NOT_EQUAL, "REPRESENTATIVE"));
			listPartyRelationship = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(listPartyRelationship)) {
				for (GenericValue x : listPartyRelationship) {
					dispatcher.runSync("deletePartyRelationship",
						UtilMisc.toMap("partyIdFrom", x.getString("partyIdFrom"), "partyIdTo", x.getString("partyIdTo"), "roleTypeIdFrom", x.getString("roleTypeIdFrom"),
								"roleTypeIdTo", x.getString("roleTypeIdTo"), "fromDate", x.getTimestamp("fromDate"), "userLogin", userLogin));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> createContactFamily(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Long birthDateL = (Long) context.get("birthDate");
			Long idIssueDateL = (Long) context.get("idIssueDate");
			//Flag for customer POS
			String isCustomerPOS = (String) context.get("isCustomerPOS");
			//	String staffContract = (String) context.get("staffContract");
			java.sql.Date birthDate = null;
			if (UtilValidate.isNotEmpty(birthDateL)) {
				birthDate = new java.sql.Date(birthDateL);
			}
			java.sql.Date idIssueDate = null;
			if (UtilValidate.isNotEmpty(idIssueDateL)) {
				idIssueDate = new java.sql.Date(idIssueDateL);
			}
			Map<String, Object> mapCreateContact = FastMap.newInstance();
			mapCreateContact.putAll(demarcatePersonName((String) context.get("fullName")));
			mapCreateContact.put("gender", context.get("gender"));
			mapCreateContact.put("birthDate", birthDate);
			mapCreateContact.put("idNumber", context.get("idNumber"));
			mapCreateContact.put("idIssueDate", idIssueDate);
			mapCreateContact.put("idIssuePlace", context.get("idIssuePlace"));
			mapCreateContact.put("statusId", "PARTY_ENABLED");
			mapCreateContact.put("userLogin", userLogin);

			String partyId = "KH" + organizationId + delegator.getNextSeqId("Party");
			mapCreateContact.put("partyId", partyId);
			Map<String, Object> resultCreatePerson = dispatcher.runSync("createPerson", mapCreateContact);
			partyId = (String) resultCreatePerson.get("partyId");
			String partyCode = (String) context.get("partyCode");
			delegator.storeByCondition("Party", UtilMisc.toMap("dataSourceId", context.get("dataSourceId"), "partyCode", (partyCode!=null?partyCode:partyId)),
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
			
			createPartyRole(dispatcher, partyId, userLogin);
			
			String roleTypeId = "CONTACT";
			String partyRelationshipTypeId = "CONTACT_REL";
			if ("Y".equals(isCustomerPOS)){
				roleTypeId = "INDIVIDUAL_CUSTOMER";
				partyRelationshipTypeId = "CUSTOMER_REL";
			}
			
			String familyId = null;
			Map<String, Object> resultCreateFamily = dispatcher.runSync("createPartyGroup",
					UtilMisc.toMap("partyTypeId", "FAMILY", "groupName", "Family" + (String) context.get("fullName"), "statusId", "PARTY_ENABLED", "userLogin", userLogin));
			familyId = (String) resultCreateFamily.get("partyId");

			//	createPartyRole REPRESENTATIVE for person
			dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", partyId, "roleTypeId", "REPRESENTATIVE", "userLogin", userLogin));
			//	createPartyRole HOUSEHOLD for family if not available
			GenericValue familyRoleType = delegator.makeValue("PartyRole",
					UtilMisc.toMap("partyId", familyId, "roleTypeId", "HOUSEHOLD"));
			delegator.createOrStore(familyRoleType);
			//	createPartyRelationship between person and family
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", familyId, "roleTypeIdFrom", "REPRESENTATIVE", "roleTypeIdTo", "HOUSEHOLD",
							"partyRelationshipTypeId", "PARTNERSHIP", "userLogin", userLogin));
			//	createPartyRelationship between staffContract and person
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", userLogin.getString("partyId"), "roleTypeIdFrom", roleTypeId, "roleTypeIdTo", "SALES_EXECUTIVE",
							"partyRelationshipTypeId", "PARTNERSHIP", "userLogin", userLogin));
			//	createPartyRelationship between person and company
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", roleTypeId, "roleTypeIdTo", "INTERNAL_ORGANIZATIO",
							"partyRelationshipTypeId", partyRelationshipTypeId, "userLogin", userLogin));
			
			if (UtilValidate.isEmpty(isCustomerPOS)){
				createPartyCampaignRelationship(dispatcher, partyId, (String) context.get("ownerEmployee"), userLogin);
			}
			
			String askForName = null;
			if ("M".equals(context.get("gender"))) {
				askForName = "Mr " + (String) context.get("fullName");
			} else {
				askForName = "Mrs " + (String) context.get("fullName");
			}
			
			String PHONE_HOME = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_HOME",
					(String) context.get("phoneHome"), askForName, partyId, userLogin);
			String PHONE_WORK = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_WORK",
					(String) context.get("officeHome"), askForName, partyId, userLogin);
			String PHONE_MOBILE = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_MOBILE",
					(String) context.get("mobilePhone"), askForName, partyId, userLogin);
			addPrimaryAndShippingPhone(dispatcher, (String) context.get("primaryPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE, partyId,
					"PRIMARY_PHONE", userLogin);
			addPrimaryAndShippingPhone(dispatcher, (String) context.get("shippingPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE, partyId,
					"PHONE_SHIPPING", userLogin);
			createContactMechEmail(dispatcher, "EMAIL_ADDRESS", (String) context.get("email"), "PRIMARY_EMAIL", partyId, userLogin);
			createContactMechFacebook(dispatcher, "WEB_ADDRESS", context.get("facebook"), "FACEBOOK_URL", partyId, userLogin);
			createAddress(dispatcher, delegator, userLogin, partyId, (String) context.get("receiverName"), (String) context.get("contactAddress"), (String) context.get("otherInfo"));

			if (UtilValidate.isNotEmpty((String) context.get("children"))) {
				JSONArray childrenArray = JSONArray.fromObject((String) context.get("children"));
				for(int i = 0; i< childrenArray.size(); i++){
					JSONObject o = childrenArray.getJSONObject(i);
					String partyFullName = null;
					if (o.containsKey("partyFullName")) {
						partyFullName = cleanString(o.getString("partyFullName"));
					}
					String gender = null;
					if (o.containsKey("gender")) {
						gender = cleanString(o.getString("gender"));
					}
					String roleTypeIdFrom = null;
					if (o.containsKey("roleTypeIdFrom")) {
						roleTypeIdFrom = cleanString(o.getString("roleTypeIdFrom"));
					}
					if ("REPRESENTATIVE".equals(roleTypeIdFrom)) {
						continue;
					}
					if (UtilValidate.isEmpty(partyFullName) || UtilValidate.isEmpty(roleTypeIdFrom)) {
						continue;
					}
					birthDateL = null;
					birthDate = null;
					if (o.containsKey("birthDate")) {
						birthDateL = o.getLong("birthDate");
					}
					if (UtilValidate.isNotEmpty(birthDateL)) {
						birthDate = new java.sql.Date(birthDateL);
					}
					mapCreateContact.clear();
					mapCreateContact.putAll(demarcatePersonName(partyFullName));
					mapCreateContact.put("gender", gender);
					mapCreateContact.put("birthDate", birthDate);
					mapCreateContact.put("statusId", "PARTY_ENABLED");
					mapCreateContact.put("userLogin", userLogin);
					String personId = null;
					resultCreatePerson = dispatcher.runSync("createPerson", mapCreateContact);
					personId = (String) resultCreatePerson.get("partyId");
					//	createPartyRole for person
					dispatcher.runSync("createPartyRole",
							UtilMisc.toMap("partyId", personId, "roleTypeId", roleTypeIdFrom, "userLogin", userLogin));
					//	createPartyRelationship between person and family
					dispatcher.runSync("createPartyRelationship",
							UtilMisc.toMap("partyIdFrom", personId, "partyIdTo", familyId, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", "HOUSEHOLD",
									"partyRelationshipTypeId", "PARTNERSHIP", "userLogin", userLogin));
				}
			}
			result.put("partyId", partyId);
			result.put("familyId", familyId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> updateContactFamily(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			Long birthDateL = (Long) context.get("birthDate");
			Long idIssueDateL = (Long) context.get("idIssueDate");
			java.sql.Date birthDate = null;
			if (UtilValidate.isNotEmpty(birthDateL)) {
				birthDate = new java.sql.Date(birthDateL);
			}
			java.sql.Date idIssueDate = null;
			if (UtilValidate.isNotEmpty(idIssueDateL)) {
				idIssueDate = new java.sql.Date(idIssueDateL);
			}
			//	updatePerson
			Map<String, Object> mapUpdateContact = FastMap.newInstance();
			mapUpdateContact.put("partyId", context.get("partyId"));
			mapUpdateContact.putAll(demarcatePersonName((String) context.get("fullName")));
			mapUpdateContact.put("gender", context.get("gender"));
			mapUpdateContact.put("birthDate", birthDate);
			mapUpdateContact.put("idNumber", context.get("idNumber"));
			mapUpdateContact.put("idIssueDate", idIssueDate);
			mapUpdateContact.put("idIssuePlace", context.get("idIssuePlace"));
			mapUpdateContact.put("userLogin", userLogin);
			delegator.storeByCondition("Party", UtilMisc.toMap("dataSourceId", context.get("dataSourceId"), "partyCode", context.get("partyCode")),
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", context.get("partyId"))));
			dispatcher.runSync("updatePerson", mapUpdateContact);
			String askForName = null;
			if ("M".equals(context.get("gender"))) {
				askForName = "Mr " + (String) context.get("fullName");
			} else {
				askForName = "Mrs " + (String) context.get("fullName");
			}
			//	create or update PHONE
			String PHONE_HOME = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneHomeId"), "TELECOM_NUMBER", "PHONE_HOME",
					(String) context.get("phoneHome"), askForName, (String) context.get("partyId"), userLogin);
			String PHONE_WORK = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneWorkId"), "TELECOM_NUMBER", "PHONE_WORK",
					(String) context.get("officeHome"), askForName, (String) context.get("partyId"), userLogin);
			String PHONE_MOBILE = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneMobileId"), "TELECOM_NUMBER", "PHONE_MOBILE",
					(String) context.get("mobilePhone"), askForName, (String) context.get("partyId"), userLogin);
			updatePrimaryAndShippingPhone(dispatcher, delegator, (String) context.get("primaryPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE,
					(String) context.get("partyId"), "PRIMARY_PHONE", userLogin);
			updatePrimaryAndShippingPhone(dispatcher, delegator, (String) context.get("shippingPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE,
					(String) context.get("partyId"), "PHONE_SHIPPING", userLogin);
			updateContactMechEmail(dispatcher, delegator, (String) context.get("emailAddressId"), "EMAIL_ADDRESS",
					(String) context.get("email"), "PRIMARY_EMAIL", (String) context.get("partyId"), userLogin);
			updateContactMechFacebook(dispatcher, delegator, (String) context.get("facebookId"), "WEB_ADDRESS",
					(String) context.get("facebook"), "FACEBOOK_URL", (String) context.get("partyId"), userLogin);
			saveAddress(dispatcher, delegator, userLogin, (String) context.get("partyId"), (String) context.get("receiverName"), (String) context.get("contactAddress"), (String) context.get("otherInfo"));
			
			if (UtilValidate.isNotEmpty((String) context.get("children"))) {
				List<String> listMemberId = FastList.newInstance();
				listMemberId.add((String) context.get("partyId"));
				JSONArray childrenArray = JSONArray.fromObject((String) context.get("children"));
				for(int i = 0; i< childrenArray.size(); i++){
					JSONObject o = childrenArray.getJSONObject(i);
					String personId = null;
					if (o.containsKey("partyId")) {
						personId = cleanString(o.getString("partyId"));
					}
					if (context.get("partyId").equals(personId)) {
						continue;
					}
					String partyFullName = null;
					if (o.containsKey("partyFullName")) {
						partyFullName = cleanString(o.getString("partyFullName"));
					}
					String gender = null;
					if (o.containsKey("gender")) {
						gender = cleanString(o.getString("gender"));
					}
					String roleTypeIdFrom = null;
					if (o.containsKey("roleTypeIdFrom")) {
						roleTypeIdFrom = cleanString(o.getString("roleTypeIdFrom"));
					}
					if ("REPRESENTATIVE".equals(roleTypeIdFrom)) {
						continue;
					}
					if (UtilValidate.isEmpty(partyFullName) || UtilValidate.isEmpty(roleTypeIdFrom)) {
						continue;
					}
					birthDateL = null;
					if (o.containsKey("birthDate")) {
						birthDateL = o.getLong("birthDate");
					}
					Map<String, Object> rs = dispatcher.runSync("createOrStoreMemberStep2",
							UtilMisc.toMap("partyId", personId, "familyId", (String) context.get("familyId"), "partyFullName", partyFullName,
									"roleTypeIdFrom", roleTypeIdFrom, "gender", gender, "birthDate", birthDateL, "userLogin", userLogin));
					personId = (String) rs.get("partyId");
					listMemberId.add(personId);
				}
				if (UtilValidate.isNotEmpty(listMemberId)) {
					List<EntityCondition> conditions = FastList.newInstance();
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.NOT_IN, listMemberId));
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", (String) context.get("familyId"), "roleTypeIdTo", "HOUSEHOLD")));
					List<GenericValue> listMemberWillDelete = delegator.findList("PartyRelationship",
							EntityCondition.makeCondition(conditions), null, null, null, false);
					for (GenericValue x : listMemberWillDelete) {
					x.set("thruDate", new Timestamp(System.currentTimeMillis()));
						delegator.store(x);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> createContactPersonal(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Long birthDateL = (Long) context.get("birthDate");
			Long idIssueDateL = (Long) context.get("idIssueDate");
			//Flag for customer POS
			String isCustomerPOS = (String) context.get("isCustomerPOS");
			//	String staffContract = (String) context.get("staffContract");
			java.sql.Date birthDate = null;
			if (UtilValidate.isNotEmpty(birthDateL)) {
				birthDate = new java.sql.Date(birthDateL);
			}
			java.sql.Date idIssueDate = null;
			if (UtilValidate.isNotEmpty(idIssueDateL)) {
				idIssueDate = new java.sql.Date(idIssueDateL);
			}
			Map<String, Object> mapCreateContact = FastMap.newInstance();
			mapCreateContact.putAll(demarcatePersonName((String) context.get("fullName")));
			mapCreateContact.put("gender", context.get("gender"));
			mapCreateContact.put("birthDate", birthDate);
			mapCreateContact.put("idNumber", context.get("idNumber"));
			mapCreateContact.put("idIssueDate", idIssueDate);
			mapCreateContact.put("idIssuePlace", context.get("idIssuePlace"));
			mapCreateContact.put("statusId", "PARTY_ENABLED");
			mapCreateContact.put("userLogin", userLogin);

			String partyId = "KH" + organizationId + delegator.getNextSeqId("Party");
			mapCreateContact.put("partyId", partyId);
			Map<String, Object> resultCreatePerson = dispatcher.runSync("createPerson", mapCreateContact);
			partyId = (String) resultCreatePerson.get("partyId");
			String partyCode = (String) context.get("partyCode");
			delegator.storeByCondition("Party", UtilMisc.toMap("dataSourceId", context.get("dataSourceId"), "partyCode", (partyCode!=null?partyCode:partyId)),
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
			
			createPartyRole(dispatcher, partyId, userLogin);
			
			String roleTypeId = "CONTACT";
			String partyRelationshipTypeId = "CONTACT_REL";
			if ("Y".equals(isCustomerPOS)){
				roleTypeId = "INDIVIDUAL_CUSTOMER";
				partyRelationshipTypeId = "CUSTOMER_REL";
			}
			
			//	createPartyRelationship between staffContract and person
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", userLogin.getString("partyId"), "roleTypeIdFrom", roleTypeId, "roleTypeIdTo", "SALES_EXECUTIVE",
							"partyRelationshipTypeId", "PARTNERSHIP", "userLogin", userLogin));
			//	createPartyRelationship between person and company
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", roleTypeId, "roleTypeIdTo", "INTERNAL_ORGANIZATIO",
							"partyRelationshipTypeId", partyRelationshipTypeId, "userLogin", userLogin));
			
			if (UtilValidate.isEmpty(isCustomerPOS)){
				createPartyCampaignRelationship(dispatcher, partyId, (String) context.get("ownerEmployee"), userLogin);
			}
			
			String askForName = null;
			if ("M".equals(context.get("gender"))) {
				askForName = "Mr " + (String) context.get("fullName");
			} else {
				askForName = "Mrs " + (String) context.get("fullName");
			}
			
			String PHONE_HOME = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_HOME",
					(String) context.get("phoneHome"), askForName, partyId, userLogin);
			String PHONE_WORK = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_WORK",
					(String) context.get("officeHome"), askForName, partyId, userLogin);
			String PHONE_MOBILE = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_MOBILE",
					(String) context.get("mobilePhone"), askForName, partyId, userLogin);
			addPrimaryAndShippingPhone(dispatcher, (String) context.get("primaryPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE, partyId,
					"PRIMARY_PHONE", userLogin);
			addPrimaryAndShippingPhone(dispatcher, (String) context.get("shippingPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE, partyId,
					"PHONE_SHIPPING", userLogin);
			createContactMechEmail(dispatcher, "EMAIL_ADDRESS", (String) context.get("email"), "PRIMARY_EMAIL", partyId, userLogin);
			createContactMechFacebook(dispatcher, "WEB_ADDRESS", context.get("facebook"), "FACEBOOK_URL", partyId, userLogin);
			createAddress(dispatcher, delegator, userLogin, partyId, (String) context.get("receiverName"), (String) context.get("contactAddress"), (String) context.get("otherInfo"));

			result.put("partyId", partyId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> updateContactPersonal(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			Long birthDateL = (Long) context.get("birthDate");
			Long idIssueDateL = (Long) context.get("idIssueDate");
			java.sql.Date birthDate = null;
			if (UtilValidate.isNotEmpty(birthDateL)) {
				birthDate = new java.sql.Date(birthDateL);
			}
			java.sql.Date idIssueDate = null;
			if (UtilValidate.isNotEmpty(idIssueDateL)) {
				idIssueDate = new java.sql.Date(idIssueDateL);
			}
			//	updatePerson
			Map<String, Object> mapUpdateContact = FastMap.newInstance();
			mapUpdateContact.put("partyId", context.get("partyId"));
			mapUpdateContact.putAll(demarcatePersonName((String) context.get("fullName")));
			mapUpdateContact.put("gender", context.get("gender"));
			mapUpdateContact.put("birthDate", birthDate);
			mapUpdateContact.put("idNumber", context.get("idNumber"));
			mapUpdateContact.put("idIssueDate", idIssueDate);
			mapUpdateContact.put("idIssuePlace", context.get("idIssuePlace"));
			mapUpdateContact.put("userLogin", userLogin);
			delegator.storeByCondition("Party", UtilMisc.toMap("dataSourceId", context.get("dataSourceId"), "partyCode", context.get("partyCode")),
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", context.get("partyId"))));
			dispatcher.runSync("updatePerson", mapUpdateContact);
			String askForName = null;
			if ("M".equals(context.get("gender"))) {
				askForName = "Mr " + (String) context.get("fullName");
			} else {
				askForName = "Mrs " + (String) context.get("fullName");
			}
			//	create or update PHONE
			String PHONE_HOME = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneHomeId"), "TELECOM_NUMBER", "PHONE_HOME",
					(String) context.get("phoneHome"), askForName, (String) context.get("partyId"), userLogin);
			String PHONE_WORK = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneWorkId"), "TELECOM_NUMBER", "PHONE_WORK",
					(String) context.get("officeHome"), askForName, (String) context.get("partyId"), userLogin);
			String PHONE_MOBILE = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneMobileId"), "TELECOM_NUMBER", "PHONE_MOBILE",
					(String) context.get("mobilePhone"), askForName, (String) context.get("partyId"), userLogin);
			updatePrimaryAndShippingPhone(dispatcher, delegator, (String) context.get("primaryPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE,
					(String) context.get("partyId"), "PRIMARY_PHONE", userLogin);
			updatePrimaryAndShippingPhone(dispatcher, delegator, (String) context.get("shippingPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE,
					(String) context.get("partyId"), "PHONE_SHIPPING", userLogin);
			updateContactMechEmail(dispatcher, delegator, (String) context.get("emailAddressId"), "EMAIL_ADDRESS",
					(String) context.get("email"), "PRIMARY_EMAIL", (String) context.get("partyId"), userLogin);
			updateContactMechFacebook(dispatcher, delegator, (String) context.get("facebookId"), "WEB_ADDRESS",
					(String) context.get("facebook"), "FACEBOOK_URL", (String) context.get("partyId"), userLogin);
			saveAddress(dispatcher, delegator, userLogin, (String) context.get("partyId"), (String) context.get("receiverName"), (String) context.get("contactAddress"), (String) context.get("otherInfo"));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> createContactBusiness(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Long birthDateL = (Long) context.get("birthDate");
			Long idIssueDateL = (Long) context.get("idIssueDate");
			//	String staffContract = (String) context.get("staffContract");
			String partyIdAvalible = (String) context.get("partyIdAvalible");
			java.sql.Date birthDate = null;
			if (UtilValidate.isNotEmpty(birthDateL)) {
				birthDate = new java.sql.Date(birthDateL);
			}
			java.sql.Date idIssueDate = null;
			if (UtilValidate.isNotEmpty(idIssueDateL)) {
				idIssueDate = new java.sql.Date(idIssueDateL);
			}
			Map<String, Object> mapCreateContact = FastMap.newInstance();
			//	createPartyGroup for corporation
			String partyId = "KH" + organizationId + "DN" + delegator.getNextSeqId("Party");
			Map<String, Object> resultCreateCorporation = dispatcher.runSync("createPartyGroup",
					UtilMisc.toMap("partyId", partyId, "comments", context.get("comments"), "groupName", context.get("groupName"), "officeSiteName", context.get("officeSiteName"),
							"partyTypeId", "BUSINESSES", "statusId", "PARTY_ENABLED", "logoImageUrl", context.get("path"), "userLogin", userLogin));
			partyId = (String) resultCreateCorporation.get("partyId");
			String partyCode = (String) context.get("partyCode");
			delegator.storeByCondition("Party", UtilMisc.toMap("dataSourceId", context.get("dataSourceId"), "partyCode", (partyCode!=null?partyCode:partyId)),
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));

			createPartyRole(dispatcher, partyId, userLogin);

			//	createPerson Represent for corporation
			mapCreateContact.clear();
			mapCreateContact.putAll(demarcatePersonName((String) context.get("fullName")));
			mapCreateContact.put("gender", context.get("gender"));
			mapCreateContact.put("birthDate", birthDate);
			mapCreateContact.put("idNumber", context.get("idNumber"));
			mapCreateContact.put("idIssueDate", idIssueDate);
			mapCreateContact.put("idIssuePlace", context.get("idIssuePlace"));
			mapCreateContact.put("statusId", "PARTY_ENABLED");
			mapCreateContact.put("userLogin", userLogin);
			String representativeMemberId = null;
			if (UtilValidate.isEmpty(partyIdAvalible)) {
				Map<String, Object> resultCreatePerson = dispatcher.runSync("createPerson", mapCreateContact);
				representativeMemberId = (String) resultCreatePerson.get("partyId");
			} else {
				mapCreateContact.put("partyId", partyIdAvalible);
				dispatcher.runSync("updatePerson", mapCreateContact);
				representativeMemberId = partyIdAvalible;
			}
			//	createPartyRole REPRESENTATIVE for person
			dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", representativeMemberId, "roleTypeId", "REPRESENTATIVE", "userLogin", userLogin));
			//	createPartyRole ORGANIZATION_UNIT for corporation
			dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", partyId, "roleTypeId", "ORGANIZATION_UNIT", "userLogin", userLogin));
			//	createPartyRelationship between person and corporation
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", representativeMemberId, "partyIdTo", partyId, "roleTypeIdFrom", "REPRESENTATIVE", "roleTypeIdTo", "ORGANIZATION_UNIT",
							"partyRelationshipTypeId", "EMPLOYEE", "userLogin", userLogin));
			//	createPartyRelationship between corporation and staff
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", userLogin.getString("partyId"), "roleTypeIdFrom", "CONTACT", "roleTypeIdTo", "SALES_EXECUTIVE",
							"partyRelationshipTypeId", "PARTNERSHIP", "userLogin", userLogin));
			//	createPartyRelationship between person and company
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", "CONTACT", "roleTypeIdTo", "INTERNAL_ORGANIZATIO",
							"partyRelationshipTypeId", "CONTACT_REL", "userLogin", userLogin));

			createPartyCampaignRelationship(dispatcher, partyId, (String) context.get("ownerEmployee"), userLogin);

			String askForName = null;
			if ("M".equals(context.get("gender"))) {
				askForName = "Mr " + (String) context.get("fullName");
			} else {
				askForName = "Mrs " + (String) context.get("fullName");
			}
			String PHONE_HOME = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_HOME",
					(String) context.get("phoneHome"), (String) context.get("groupName"), partyId, userLogin);
			String PHONE_WORK = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_WORK",
					(String) context.get("officeHome"), (String) context.get("groupName"), partyId, userLogin);
			String PHONE_MOBILE = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_MOBILE",
					(String) context.get("mobilePhone"), (String) context.get("groupName"), partyId, userLogin);
			addPrimaryAndShippingPhone(dispatcher, (String) context.get("primaryPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE, partyId,
					"PRIMARY_PHONE", userLogin);
			addPrimaryAndShippingPhone(dispatcher, (String) context.get("shippingPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE, partyId,
					"PHONE_SHIPPING", userLogin);
			createContactMechEmail(dispatcher, "EMAIL_ADDRESS", (String) context.get("email"), "PRIMARY_EMAIL", partyId, userLogin);
			createContactMechFacebook(dispatcher, "WEB_ADDRESS", context.get("facebook"), "FACEBOOK_URL", partyId, userLogin);
			String PHONE_MOBILE_PRE = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_MOBILE",
					(String) context.get("sdt"), askForName, representativeMemberId, userLogin);
			addPrimaryAndShippingPhone(dispatcher, "PHONE_MOBILE", "", "", PHONE_MOBILE_PRE, representativeMemberId, "PRIMARY_PHONE", userLogin);
			createAddress(dispatcher, delegator, userLogin, partyId, (String) context.get("receiverName"), (String) context.get("contactAddress"), (String) context.get("otherInfo"));
			result.put("partyId", partyId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> updateContactBusiness(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			Long birthDateL = (Long) context.get("birthDate");
			Long idIssueDateL = (Long) context.get("idIssueDate");
			java.sql.Date birthDate = null;
			if (UtilValidate.isNotEmpty(birthDateL)) {
				birthDate = new java.sql.Date(birthDateL);
			}
			java.sql.Date idIssueDate = null;
			if (UtilValidate.isNotEmpty(idIssueDateL)) {
				idIssueDate = new java.sql.Date(idIssueDateL);
			}
			//	update corporation
			delegator.storeByCondition("Party", UtilMisc.toMap("dataSourceId", context.get("dataSourceId"), "partyCode", context.get("partyCode")),
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", context.get("partyId"))));
			dispatcher.runSync("updatePartyGroup",
						UtilMisc.toMap("partyId", context.get("partyId"), "groupName", context.get("groupName"), "comments", context.get("comments"),
								"officeSiteName", context.get("officeSiteName"), "logoImageUrl", context.get("logoImageUrl"), "userLogin", userLogin));
			//	create or update contactMech
			
			String PHONE_HOME = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneHomeId"), "TELECOM_NUMBER", "PHONE_HOME",
					(String) context.get("phoneHome"), (String) context.get("groupName"), (String) context.get("partyId"), userLogin);
			String PHONE_WORK = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneWorkId"), "TELECOM_NUMBER", "PHONE_WORK",
					(String) context.get("officeHome"), (String) context.get("groupName"), (String) context.get("partyId"), userLogin);
			String PHONE_MOBILE = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneMobileId"), "TELECOM_NUMBER", "PHONE_MOBILE",
					(String) context.get("mobilePhone"), (String) context.get("groupName"), (String) context.get("partyId"), userLogin);
			updatePrimaryAndShippingPhone(dispatcher, delegator, (String) context.get("primaryPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE,
					(String) context.get("partyId"), "PRIMARY_PHONE", userLogin);
			updatePrimaryAndShippingPhone(dispatcher, delegator, (String) context.get("shippingPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE,
					(String) context.get("partyId"), "PHONE_SHIPPING", userLogin);
			updateContactMechEmail(dispatcher, delegator, (String) context.get("emailAddressId"), "EMAIL_ADDRESS", (String) context.get("email"), "PRIMARY_EMAIL",
					(String) context.get("partyId"), userLogin);
			updateContactMechFacebook(dispatcher, delegator, (String) context.get("facebookId"), "WEB_ADDRESS",
					(String) context.get("facebook"), "FACEBOOK_URL", (String) context.get("partyId"), userLogin);
			saveAddress(dispatcher, delegator, userLogin, (String) context.get("partyId"), (String) context.get("receiverName"), (String) context.get("contactAddress"), (String) context.get("otherInfo"));
			//	updatePerson
			Map<String, Object> mapUpdateContact = FastMap.newInstance();
			mapUpdateContact.put("partyId", context.get("representativeMemberId"));
			mapUpdateContact.putAll(demarcatePersonName((String) context.get("fullName")));
			mapUpdateContact.put("gender", context.get("gender"));
			mapUpdateContact.put("birthDate", birthDate);
			mapUpdateContact.put("idNumber", context.get("idNumber"));
			mapUpdateContact.put("idIssueDate", idIssueDate);
			mapUpdateContact.put("idIssuePlace", (String) context.get("idIssuePlace"));
			mapUpdateContact.put("userLogin", userLogin);
			dispatcher.runSync("updatePerson", mapUpdateContact);
			
			String askForName = null;
			if ("M".equals(context.get("gender"))) {
				askForName = "Mr " + (String) context.get("fullName");
			} else {
				askForName = "Mrs " + (String) context.get("fullName");
			}
			if (UtilValidate.isNotEmpty((String) context.get("representativeMemberPhoneId"))) {
				updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("representativeMemberPhoneId"), "TELECOM_NUMBER", "PHONE_MOBILE",
						(String) context.get("sdt"), askForName, (String) context.get("representativeMemberId"), userLogin);
			} else {
				String PHONE_MOBILE_PRE = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_MOBILE",
						(String) context.get("sdt"), askForName, (String) context.get("representativeMemberId"), userLogin);
				addPrimaryAndShippingPhone(dispatcher, "PHONE_MOBILE", "", "", PHONE_MOBILE_PRE, (String) context.get("representativeMemberId"),
						"PRIMARY_PHONE", userLogin);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> createContactSchool(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Long birthDateL = (Long) context.get("birthDate");
			Long idIssueDateL = (Long) context.get("idIssueDate");
			//	String staffContract = (String) context.get("staffContract");
			java.sql.Date birthDate = null;
			if (UtilValidate.isNotEmpty(birthDateL)) {
				birthDate = new java.sql.Date(birthDateL);
			}
			java.sql.Date idIssueDate = null;
			if (UtilValidate.isNotEmpty(idIssueDateL)) {
				idIssueDate = new java.sql.Date(idIssueDateL);
			}
			//	createPartyGroup for corporation
			String partyId = "KH" + organizationId + "MN" + delegator.getNextSeqId("Party");
			Map<String, Object> resultCreateCorporation = dispatcher.runSync("createPartyGroup",
					UtilMisc.toMap("partyId", partyId, "comments", context.get("comments"), "groupName", context.get("groupName"), "officeSiteName",
							context.get("officeSiteName"), "partyTypeId", "SCHOOL", "statusId", "PARTY_ENABLED", "userLogin", userLogin));
			partyId = (String) resultCreateCorporation.get("partyId");
			String partyCode = (String) context.get("partyCode");
			delegator.storeByCondition("Party", UtilMisc.toMap("dataSourceId", context.get("dataSourceId"), "partyCode", (partyCode!=null?partyCode:partyId)),
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));

			createPartyRole(dispatcher, partyId, userLogin);

			//	createPerson Represent for corporation
			Map<String, Object> mapCreateContact = FastMap.newInstance();
			mapCreateContact.putAll(demarcatePersonName((String) context.get("fullName")));
			mapCreateContact.put("gender", context.get("gender"));
			mapCreateContact.put("birthDate", birthDate);
			mapCreateContact.put("idNumber", context.get("idNumber"));
			mapCreateContact.put("idIssueDate", idIssueDate);
			mapCreateContact.put("idIssuePlace", context.get("idIssuePlace"));
			mapCreateContact.put("statusId", "PARTY_ENABLED");
			mapCreateContact.put("userLogin", userLogin);

			String representativeMemberId = null;
			Map<String, Object> resultCreatePerson = dispatcher.runSync("createPerson", mapCreateContact);
			representativeMemberId = (String) resultCreatePerson.get("partyId");
			//	createPartyRole REPRESENTATIVE for person
			dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", representativeMemberId, "roleTypeId", "REPRESENTATIVE", "userLogin", userLogin));
			//	createPartyRole ORGANIZATION_UNIT for corporation
			dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", partyId, "roleTypeId", "ORGANIZATION_UNIT", "userLogin", userLogin));
			//	createPartyRelationship between person and corporation
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", representativeMemberId, "partyIdTo", partyId, "roleTypeIdFrom", "REPRESENTATIVE", "roleTypeIdTo", "ORGANIZATION_UNIT",
							"partyRelationshipTypeId", "EMPLOYEE", "userLogin", userLogin));
			//	createPartyRelationship between corporation and staff
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", userLogin.getString("partyId"), "roleTypeIdFrom", "CONTACT", "roleTypeIdTo", "SALES_EXECUTIVE",
							"partyRelationshipTypeId", "PARTNERSHIP", "userLogin", userLogin));
			//	createPartyAttribute number teacher
			dispatcher.runSync("createOrStorePartyAttribute",
					UtilMisc.toMap("partyId", partyId, "attrName", "Teacher", "attrValue", (String) context.get("numberTeacher"), "userLogin", userLogin));
			//	createPartyAttribute number student
			dispatcher.runSync("createOrStorePartyAttribute",
					UtilMisc.toMap("partyId", partyId, "attrName", "Student", "attrValue", (String) context.get("numberStudent"), "userLogin", userLogin));
			//	createPartyRelationship between person and company
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", "CONTACT", "roleTypeIdTo", "INTERNAL_ORGANIZATIO",
							"partyRelationshipTypeId", "CONTACT_REL", "userLogin", userLogin));
			createPartyCampaignRelationship(dispatcher, partyId, (String) context.get("ownerEmployee"), userLogin);

			String askForName = null;
			if ("M".equals(context.get("gender"))) {
				askForName = "Mr " + (String) context.get("fullName");
			} else {
				askForName = "Mrs " + (String) context.get("fullName");
			}
			String PHONE_HOME = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_HOME",
					(String) context.get("phoneHome"), (String) context.get("groupName"), partyId, userLogin);
			String PHONE_WORK = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_WORK",
					(String) context.get("officeHome"), (String) context.get("groupName"), partyId, userLogin);
			String PHONE_MOBILE = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_MOBILE",
					(String) context.get("mobilePhone"), (String) context.get("groupName"), partyId, userLogin);
			addPrimaryAndShippingPhone(dispatcher, (String) context.get("primaryPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE, partyId,
					"PRIMARY_PHONE", userLogin);
			addPrimaryAndShippingPhone(dispatcher, (String) context.get("shippingPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE, partyId,
					"PHONE_SHIPPING", userLogin);
			createContactMechEmail(dispatcher, "EMAIL_ADDRESS", (String) context.get("email"), "PRIMARY_EMAIL", partyId, userLogin);
			createContactMechFacebook(dispatcher, "WEB_ADDRESS", context.get("facebook"), "FACEBOOK_URL", partyId, userLogin);
			String PHONE_MOBILE_PRE = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_MOBILE",
					(String) context.get("sdt"), askForName, representativeMemberId, userLogin);
			addPrimaryAndShippingPhone(dispatcher, "PHONE_MOBILE", "", "", PHONE_MOBILE_PRE, representativeMemberId, "PRIMARY_PHONE", userLogin);
			createAddress(dispatcher, delegator, userLogin, partyId, (String) context.get("receiverName"), (String) context.get("contactAddress"), (String) context.get("otherInfo"));
			result.put("partyId", partyId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> updateContactSchool(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			Long birthDateL = (Long) context.get("birthDate");
			Long idIssueDateL = (Long) context.get("idIssueDate");
			java.sql.Date birthDate = null;
			if (UtilValidate.isNotEmpty(birthDateL)) {
				birthDate = new java.sql.Date(birthDateL);
			}
			java.sql.Date idIssueDate = null;
			if (UtilValidate.isNotEmpty(idIssueDateL)) {
				idIssueDate = new java.sql.Date(idIssueDateL);
			}
			//	update corporation
			delegator.storeByCondition("Party", UtilMisc.toMap("dataSourceId", context.get("dataSourceId"), "partyCode", context.get("partyCode")),
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", context.get("partyId"))));
			dispatcher.runSync("updatePartyGroup",
					UtilMisc.toMap("partyId", context.get("partyId"), "groupName", context.get("groupName"), "comments", context.get("comments"),
							"officeSiteName", context.get("officeSiteName"), "userLogin", userLogin));
			//	update number teacher n student
			dispatcher.runSync("createOrStorePartyAttribute",
					UtilMisc.toMap("partyId", context.get("partyId"), "attrName", "Teacher", "attrValue", context.get("numberTeacher"), "userLogin", userLogin));
				dispatcher.runSync("createOrStorePartyAttribute",
						UtilMisc.toMap("partyId", context.get("partyId"), "attrName", "Student", "attrValue", context.get("numberStudent"), "userLogin", userLogin));
			//	create or update contactMech
			String PHONE_HOME = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneHomeId"), "TELECOM_NUMBER", "PHONE_HOME",
					(String) context.get("phoneHome"), (String) context.get("groupName"), (String) context.get("partyId"), userLogin);
			String PHONE_WORK = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneWorkId"), "TELECOM_NUMBER", "PHONE_WORK",
					(String) context.get("officeHome"), (String) context.get("groupName"), (String) context.get("partyId"), userLogin);
			String PHONE_MOBILE = updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("phoneMobileId"), "TELECOM_NUMBER", "PHONE_MOBILE",
					(String) context.get("mobilePhone"), (String) context.get("groupName"), (String) context.get("partyId"), userLogin);
			updatePrimaryAndShippingPhone(dispatcher, delegator, (String) context.get("primaryPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE, (String) context.get("partyId"),
					"PRIMARY_PHONE", userLogin);
			updatePrimaryAndShippingPhone(dispatcher, delegator, (String) context.get("shippingPhone"), PHONE_HOME, PHONE_WORK, PHONE_MOBILE, (String) context.get("partyId"),
					"PHONE_SHIPPING", userLogin);
			updateContactMechEmail(dispatcher, delegator, (String) context.get("emailAddressId"), "EMAIL_ADDRESS", (String) context.get("email"), "PRIMARY_EMAIL",
					(String) context.get("partyId"), userLogin);
			updateContactMechFacebook(dispatcher, delegator, (String) context.get("facebookId"), "WEB_ADDRESS",
					(String) context.get("facebook"), "FACEBOOK_URL", (String) context.get("partyId"), userLogin);
			saveAddress(dispatcher, delegator, userLogin, (String) context.get("partyId"), (String) context.get("receiverName"), (String) context.get("contactAddress"), (String) context.get("otherInfo"));
			//	updatePerson
			Map<String, Object> mapUpdateContact = FastMap.newInstance();
			mapUpdateContact.put("partyId", (String) context.get("representativeMemberId"));
			mapUpdateContact.putAll(demarcatePersonName((String) context.get("fullName")));
			mapUpdateContact.put("gender", (String) context.get("gender"));
			mapUpdateContact.put("birthDate", birthDate);
			mapUpdateContact.put("idNumber", (String) context.get("idNumber"));
			mapUpdateContact.put("idIssueDate", idIssueDate);
			mapUpdateContact.put("idIssuePlace", (String) context.get("idIssuePlace"));
			mapUpdateContact.put("userLogin", userLogin);
			dispatcher.runSync("updatePerson", mapUpdateContact);
			
			String askForName = null;
			if ("M".equals(context.get("gender"))) {
				askForName = "Mr " + (String) context.get("fullName");
			} else {
				askForName = "Mrs " + (String) context.get("fullName");
			}
			if (UtilValidate.isNotEmpty((String) context.get("representativeMemberPhoneId"))) {
				updateContactMechTelecomNumber(dispatcher, delegator, (String) context.get("representativeMemberPhoneId"), "TELECOM_NUMBER", "PHONE_MOBILE", (String) context.get("sdt"), askForName, (String) context.get("representativeMemberId"), userLogin);
			} else {
				String PHONE_MOBILE_PRE = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_MOBILE", (String) context.get("sdt"), askForName, (String) context.get("representativeMemberId"), userLogin);
				addPrimaryAndShippingPhone(dispatcher, "PHONE_MOBILE", "", "", PHONE_MOBILE_PRE, (String) context.get("representativeMemberId"), "PRIMARY_PHONE", userLogin);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static String createContactMechTelecomNumber(LocalDispatcher dispatcher, Object contactMechTypeId, Object contactMechPurposeTypeId,
			Object contactNumber, Object askForName, Object personId, GenericValue userLogin)
					throws GenericServiceException, GenericEntityException {
		Map<String, Object> mapCreateContact = FastMap.newInstance();
		if (UtilValidate.isEmpty(contactNumber)) {
			return null;
		}
		//	createTelecomNumber
		mapCreateContact.clear();
		mapCreateContact.put("contactNumber", contactNumber);
		mapCreateContact.put("askForName", askForName);
		mapCreateContact.put("userLogin", userLogin);
		Map<String, Object> resultCreateTelecomNumber = dispatcher.runSync("createTelecomNumber", mapCreateContact);
		String contactMechId = (String) resultCreateTelecomNumber.get("contactMechId");
		//	createPartyContactMech TELECOM_NUMBER
		mapCreateContact.clear();
		mapCreateContact.put("partyId", personId);
		mapCreateContact.put("contactMechId", contactMechId);
		mapCreateContact.put("allowSolicitation", "Y");
		mapCreateContact.put("userLogin", userLogin);
		dispatcher.runSync("createPartyContactMech", mapCreateContact);
		
		//	createPartyContactMechPurpose
		List<GenericValue> listContactMech = cancelPartyContactMechPurpose(dispatcher, dispatcher.getDelegator(), contactMechPurposeTypeId, personId, userLogin);
		cancelPartyContactMech(dispatcher, dispatcher.getDelegator(), listContactMech, personId, userLogin);
		mapCreateContact.clear();
		mapCreateContact.put("partyId", personId);
		mapCreateContact.put("contactMechId", contactMechId);
		mapCreateContact.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
		mapCreateContact.put("userLogin", userLogin);
		dispatcher.runSync("createPartyContactMechPurpose", mapCreateContact);
		
		return contactMechId;
	}
	public static String createPartyContactMechPurpose(LocalDispatcher dispatcher, Delegator delegator, String contactMechId, Object contactMechPurposeTypeId,
			Object partyId, GenericValue userLogin)
					throws GenericServiceException, GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId)));
		List<GenericValue> partyContactMechPurposes = delegator.findList("PartyContactMechPurpose",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		cancelPartyContactMechPurpose(dispatcher, delegator, contactMechPurposeTypeId, partyId, userLogin);
		if (UtilValidate.isEmpty(partyContactMechPurposes)) {
			Map<String, Object> mapCreateContact = FastMap.newInstance();
			//	createPartyContactMechPurpose
			mapCreateContact.clear();
			mapCreateContact.put("partyId", partyId);
			mapCreateContact.put("contactMechId", contactMechId);
			mapCreateContact.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			mapCreateContact.put("userLogin", userLogin);
			dispatcher.runSync("createPartyContactMechPurpose", mapCreateContact);
		}
		return contactMechId;
	}
	
	public static List<GenericValue> cancelPartyContactMechPurpose(LocalDispatcher dispatcher, Delegator delegator, Object contactMechPurposeTypeId,
			Object partyId, GenericValue userLogin)
					throws GenericServiceException, GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", contactMechPurposeTypeId)));
		List<GenericValue> partyContactMechPurposes = delegator.findList("PartyContactMechPurpose",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		Timestamp thruDate = new Timestamp(System.currentTimeMillis());
		if (!UtilValidate.isEmpty(partyContactMechPurposes)) {
			for ( GenericValue item : partyContactMechPurposes){
				item.put("thruDate",thruDate );
				item.store();
			}
		}
		return partyContactMechPurposes;
	}
	
	public static void cancelPartyContactMech(LocalDispatcher dispatcher, Delegator delegator, List<GenericValue> listContactMech,
			Object partyId, GenericValue userLogin)
					throws GenericServiceException, GenericEntityException {
		Timestamp thruDate = new Timestamp(System.currentTimeMillis());
		if (!UtilValidate.isEmpty(listContactMech)) {
			for ( GenericValue item : listContactMech){
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechId", item.get("contactMechId"))));
				List<GenericValue> partyContactMech = delegator.findList("PartyContactMech",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				for( GenericValue temp : partyContactMech){
					temp.put("thruDate",thruDate );
					temp.store();
				}
			}
		}
	}
	
	private static void updatePrimaryAndShippingPhone(LocalDispatcher dispatcher, Delegator delegator, String phone, String PHONE_HOME, String PHONE_WORK, String PHONE_MOBILE,
			String personId, String contactMechPurposeTypeId, GenericValue userLogin)
					throws GenericEntityException, GenericServiceException {
		Map<String, Object> mapCreateContact = FastMap.newInstance();
		String contactMechId = null;
		if (UtilValidate.isNotEmpty(phone)) {
			switch (phone) {
			case "PHONE_HOME":
				contactMechId = PHONE_HOME;
				break;
			case "PHONE_WORK":
				contactMechId = PHONE_WORK;
				break;
			case "PHONE_MOBILE":
				contactMechId = PHONE_MOBILE;
				break;
			default:
				break;
			}
			if (UtilValidate.isNotEmpty(contactMechId)) {
				//	check has change
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", personId, "contactMechPurposeTypeId", contactMechPurposeTypeId)));
				List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(listPartyContactMechPurpose)) {
					mapCreateContact.clear();
					mapCreateContact.put("partyId", personId);
					mapCreateContact.put("contactMechId", contactMechId);
					mapCreateContact.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
					mapCreateContact.put("userLogin", userLogin);
					dispatcher.runSync("createPartyContactMechPurpose", mapCreateContact);
				} else {
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contactMechId, "partyId", personId, "contactMechPurposeTypeId", contactMechPurposeTypeId)));
					List<GenericValue> listPartyContactMechPurposeCorporeality = delegator.findList("PartyContactMechPurpose",
							EntityCondition.makeCondition(conditions), null, null, null, false);
					if (UtilValidate.isEmpty(listPartyContactMechPurposeCorporeality)) {
						for (GenericValue x : listPartyContactMechPurpose) {
							mapCreateContact.clear();
							mapCreateContact.put("partyId", personId);
							mapCreateContact.put("contactMechId", x.getString("contactMechId"));
							mapCreateContact.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
							mapCreateContact.put("userLogin", userLogin);
							dispatcher.runSync("deletePartyContactMechPurposeIfExists", mapCreateContact);
						}
						mapCreateContact.clear();
						mapCreateContact.put("partyId", personId);
						mapCreateContact.put("contactMechId", contactMechId);
						mapCreateContact.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
						mapCreateContact.put("userLogin", userLogin);
						dispatcher.runSync("createPartyContactMechPurpose", mapCreateContact);
					}
				}
			}
		}
	}
	public static void addPrimaryAndShippingPhone(LocalDispatcher dispatcher, String phone, String PHONE_HOME, String PHONE_WORK, String PHONE_MOBILE,
			String personId, String contactMechPurposeTypeId, GenericValue userLogin)
					throws GenericServiceException {
		Map<String, Object> mapCreateContact = FastMap.newInstance();
		String contactMechId = null;
		if (UtilValidate.isNotEmpty(phone)) {
			switch (phone) {
			case "PHONE_HOME":
				contactMechId = PHONE_HOME;
				break;
			case "PHONE_WORK":
				contactMechId = PHONE_WORK;
				break;
			case "PHONE_MOBILE":
				contactMechId = PHONE_MOBILE;
				break;
			default:
				break;
			}
			if (UtilValidate.isNotEmpty(contactMechId)) {
				mapCreateContact.put("partyId", personId);
				mapCreateContact.put("contactMechId", contactMechId);
				mapCreateContact.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
				mapCreateContact.put("userLogin", userLogin);
				dispatcher.runSync("createPartyContactMechPurpose", mapCreateContact);
			}
		}
	}
	public static String updateContactMechEmail(LocalDispatcher dispatcher, Delegator delegator, String contactMechId, Object contactMechTypeId, Object infoString,
			Object contactMechPurposeTypeId, Object personId, GenericValue userLogin)
					throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(infoString)) {
			//	return contactMechId;
		}
		if (UtilValidate.isEmpty(contactMechId)) {
			contactMechId = createContactMechEmail(dispatcher, contactMechTypeId, infoString, contactMechPurposeTypeId, personId, userLogin);
		} else {
			delegator.storeByCondition("ContactMech", UtilMisc.toMap("infoString", infoString), EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contactMechId)));
		}
		return contactMechId;
	}
	public static String createContactMechEmail(LocalDispatcher dispatcher, Object contactMechTypeId, Object infoString,
			Object contactMechPurposeTypeId, Object personId, GenericValue userLogin)
					throws GenericServiceException, GenericEntityException {
		Map<String, Object> mapCreateContact = FastMap.newInstance();
		if (UtilValidate.isEmpty(infoString)) {
			return null;
		}
		//	createPartyContactMech EMAIL_ADDRESS
		mapCreateContact.clear();
		mapCreateContact.put("partyId", personId);
		mapCreateContact.put("contactMechTypeId", contactMechTypeId);
		mapCreateContact.put("infoString", infoString);
		mapCreateContact.put("allowSolicitation", "Y");
		mapCreateContact.put("userLogin", userLogin);
		
		
		//	cancel old ContactMech 
		List<GenericValue> listContactMech = cancelPartyContactMechPurpose(dispatcher, dispatcher.getDelegator(), contactMechPurposeTypeId, personId, userLogin);
		cancelPartyContactMech(dispatcher, dispatcher.getDelegator(), listContactMech, personId, userLogin);
		
		// create ContactMech and ContactMechPurpose
		Map<String, Object> resultCreatePartyContactMech = dispatcher.runSync("createPartyContactMech", mapCreateContact);
		String contactMechId = (String) resultCreatePartyContactMech.get("contactMechId");
		mapCreateContact.clear();
		mapCreateContact.put("partyId", personId);
		mapCreateContact.put("contactMechId", contactMechId);
		mapCreateContact.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
		mapCreateContact.put("userLogin", userLogin);
		dispatcher.runSync("createPartyContactMechPurpose", mapCreateContact);
		
		return contactMechId;
	}
	public static String updateContactMechFacebook(LocalDispatcher dispatcher, Delegator delegator, String contactMechId, Object contactMechTypeId, Object infoString,
			Object contactMechPurposeTypeId, Object personId, GenericValue userLogin)
					throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(infoString)) {
			//	return contactMechId;
		}
		if (UtilValidate.isEmpty(contactMechId)) {
			contactMechId = createContactMechFacebook(dispatcher, contactMechTypeId, infoString, contactMechPurposeTypeId, personId, userLogin);
		} else {
			delegator.storeByCondition("ContactMech", UtilMisc.toMap("infoString", infoString), EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contactMechId)));
		}
		return contactMechId;
	}
	public static String createContactMechFacebook(LocalDispatcher dispatcher, Object contactMechTypeId, Object infoString,
			Object contactMechPurposeTypeId, Object personId, GenericValue userLogin)
					throws GenericServiceException {
		Map<String, Object> mapCreateContact = FastMap.newInstance();
		if (UtilValidate.isEmpty(infoString)) {
			return null;
		}
		//	createPartyContactMech WEB_ADDRESS
		mapCreateContact.clear();
		mapCreateContact.put("partyId", personId);
		mapCreateContact.put("contactMechTypeId", contactMechTypeId);
		mapCreateContact.put("infoString", infoString);
		mapCreateContact.put("allowSolicitation", "Y");
		mapCreateContact.put("userLogin", userLogin);
		Map<String, Object> resultCreatePartyContactMech = dispatcher.runSync("createPartyContactMech", mapCreateContact);
		String contactMechId = (String) resultCreatePartyContactMech.get("contactMechId");
		
		//	createPartyContactMechPurpose
		mapCreateContact.clear();
		mapCreateContact.put("partyId", personId);
		mapCreateContact.put("contactMechId", contactMechId);
		mapCreateContact.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
		mapCreateContact.put("userLogin", userLogin);
		dispatcher.runSync("createPartyContactMechPurpose", mapCreateContact);
		
		return contactMechId;
	}
	public static String createContactMechPostalAddress(LocalDispatcher dispatcher, Delegator delegator, String contactMechId, Object contactMechTypeId, Object toName, Object attnName,
			Object contactMechPurposeTypeId, Object personId, Object address1, Object countryGeoId, Object stateProvinceGeoId, Object districtGeoId, Object wardGeoId, Object city, Object postalCode, Object geoPointId, GenericValue userLogin)
					throws GenericEntityException, GenericServiceException {
		Map<String, Object> mapCreateContact = FastMap.newInstance();
		//	createPostalAddress
		if (UtilValidate.isEmpty(address1) || UtilValidate.isEmpty(city)) { // || UtilValidate.isEmpty(postalCode)
			return contactMechId;
		}
		if (UtilValidate.isEmpty(contactMechId)) {
			mapCreateContact.clear();
			mapCreateContact.put("toName", toName);
			mapCreateContact.put("attnName", attnName);
			mapCreateContact.put("city", city);
			mapCreateContact.put("postalCode", postalCode);
			mapCreateContact.put("address1", address1);
			mapCreateContact.put("countryGeoId", countryGeoId);
			mapCreateContact.put("stateProvinceGeoId", stateProvinceGeoId);
			mapCreateContact.put("districtGeoId", districtGeoId);
			mapCreateContact.put("wardGeoId", wardGeoId);
			mapCreateContact.put("geoPointId", geoPointId);
			mapCreateContact.put("userLogin", userLogin);

			Map<String, Object> resultCreatePostalAddress = dispatcher.runSync("createPostalAddress", mapCreateContact);
			contactMechId = (String) resultCreatePostalAddress.get("contactMechId");
			//	createPartyContactMech POSTAL_ADDRESS
			mapCreateContact.clear();
			mapCreateContact.put("partyId", personId);
			mapCreateContact.put("contactMechId", contactMechId);
			mapCreateContact.put("allowSolicitation", "Y");
			mapCreateContact.put("userLogin", userLogin);
			dispatcher.runSync("createPartyContactMech", mapCreateContact);

			if (UtilValidate.isNotEmpty(contactMechPurposeTypeId)) {
				mapCreateContact.clear();
				mapCreateContact.put("partyId", personId);
				mapCreateContact.put("contactMechId", contactMechId);
				mapCreateContact.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
				mapCreateContact.put("userLogin", userLogin);
				dispatcher.runSync("createPartyContactMechPurpose", mapCreateContact);
			}
		} else {
			//	createPartyContactMech
			List<EntityCondition> contactMechCond = FastList.newInstance();
			contactMechCond.add(EntityCondition.makeCondition("partyId", personId));
			contactMechCond.add(EntityCondition.makeCondition("contactMechId", contactMechId));
			contactMechCond.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listPartyContactMech = delegator.findList("PartyContactMech",
					EntityCondition.makeCondition(contactMechCond), null, null, null, false);
			if (UtilValidate.isEmpty(listPartyContactMech)|| (UtilValidate.isNotEmpty(listPartyContactMech.get(0).get("thruDate")))) {
				mapCreateContact.clear();
				mapCreateContact.put("partyId", personId);
				mapCreateContact.put("contactMechId", contactMechId);
				mapCreateContact.put("allowSolicitation", "Y");
				mapCreateContact.put("userLogin", userLogin);
				dispatcher.runSync("createPartyContactMech", mapCreateContact);
			}
			//	createPartyContactMechPurpose
			List<EntityCondition> contactMechPurposeCond = FastList.newInstance();
			contactMechPurposeCond.add(EntityCondition.makeCondition("partyId", personId));
			contactMechPurposeCond.add(EntityCondition.makeCondition("contactMechId", contactMechId));
			contactMechPurposeCond.add(EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId));
			contactMechPurposeCond.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listPartyContactMechPurposes = delegator.findList("PartyContactMechPurpose",
					EntityCondition.makeCondition(contactMechPurposeCond), null, null, null, false);

			if (UtilValidate.isEmpty(listPartyContactMechPurposes)|| (UtilValidate.isNotEmpty(listPartyContactMechPurposes.get(0).get("thruDate")))) {
				mapCreateContact.clear();
				mapCreateContact.put("partyId", personId);
				mapCreateContact.put("contactMechId", contactMechId);
				mapCreateContact.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
				mapCreateContact.put("userLogin", userLogin);
				dispatcher.runSync("createPartyContactMechPurpose", mapCreateContact);
			}
		}
		return contactMechId;
	}
	@Deprecated
	public static String createContactMechPostalAddress(LocalDispatcher dispatcher, Delegator delegator, String contactMechId, Object contactMechTypeId, Object toName, Object attnName,
			Object contactMechPurposeTypeId, Object personId, Object address1, Object countryGeoId, Object stateProvinceGeoId, Object districtGeoId, Object wardGeoId, Object city, Object postalCode, GenericValue userLogin)
					throws GenericEntityException, GenericServiceException {
		return createContactMechPostalAddress(dispatcher, delegator, contactMechId, contactMechTypeId, toName, attnName, contactMechPurposeTypeId, personId, address1, countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId, city, postalCode, null, userLogin);
	}
	public static String updateContactMechPostalAddress(LocalDispatcher dispatcher, Delegator delegator, String contactMechId, Object contactMechTypeId, Object toName, Object attnName,
			Object contactMechPurposeTypeId, Object personId, Object address1, Object countryGeoId, Object stateProvinceGeoId, Object districtGeoId, Object wardGeoId, Object city, Object postalCode, Object geoPointId, GenericValue userLogin)
					throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(contactMechId)) {
			if (UtilValidate.isEmpty(address1)) {
				return contactMechId;
			}
			contactMechId = createContactMechPostalAddress(dispatcher, delegator, contactMechId, contactMechTypeId, toName, attnName, contactMechPurposeTypeId, personId, address1, countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId, city, postalCode, geoPointId, userLogin);
		} else {
			if (UtilValidate.isEmpty(address1)) {
				return contactMechId;
			}
			Map<String, Object> address = FastMap.newInstance();
			address.put("toName", UtilValidate.isNotEmpty(toName)?toName:null);
			address.put("attnName", UtilValidate.isNotEmpty(attnName)?attnName:null);
			address.put("address1", UtilValidate.isNotEmpty(address1)?address1:null);
			address.put("city", UtilValidate.isNotEmpty(city)?city:null);
			address.put("postalCode", UtilValidate.isNotEmpty(postalCode)?postalCode:null);
			address.put("countryGeoId", UtilValidate.isNotEmpty(countryGeoId)?countryGeoId:null);
			address.put("stateProvinceGeoId", UtilValidate.isNotEmpty(stateProvinceGeoId)?stateProvinceGeoId:null);
			address.put("districtGeoId", UtilValidate.isNotEmpty(districtGeoId)?districtGeoId:null);
			address.put("wardGeoId", UtilValidate.isNotEmpty(wardGeoId)?wardGeoId:null);
			address.put("geoPointId", UtilValidate.isNotEmpty(geoPointId)?geoPointId:null);
			delegator.storeByCondition("PostalAddress", address,
					EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contactMechId)));
		}
		return contactMechId;
	}
	@Deprecated
	public static String updateContactMechPostalAddress(LocalDispatcher dispatcher, Delegator delegator, String contactMechId, Object contactMechTypeId, Object toName, Object attnName,
			Object contactMechPurposeTypeId, Object personId, Object address1, Object countryGeoId, Object stateProvinceGeoId, Object districtGeoId, Object wardGeoId, Object city, Object postalCode, GenericValue userLogin)
					throws GenericEntityException, GenericServiceException {
		return updateContactMechPostalAddress(dispatcher, delegator, contactMechId, contactMechTypeId, toName, attnName, contactMechPurposeTypeId, personId, address1, countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId, city, postalCode, null, userLogin);
	}
	private static void cleanPartyContactMechPurpose(LocalDispatcher dispatcher, Delegator delegator, List<String> listContactMechid, String contactMechPurposeTypeId, String partyId, GenericValue userLogin)
			throws GenericEntityException, GenericServiceException {
		List<EntityCondition> conditions = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listContactMechid)) {
			conditions.add(EntityCondition.makeCondition("contactMechId", EntityJoinOperator.NOT_IN, listContactMechid));
		}
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", contactMechPurposeTypeId)));
		List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
		EntityCondition.makeCondition(conditions), null, null, null, false);
		Map<String, Object> mapCreateContact = FastMap.newInstance();
		for (GenericValue x : listPartyContactMechPurpose) {
			mapCreateContact.clear();
			mapCreateContact.put("partyId", x.getString("partyId"));
			mapCreateContact.put("contactMechId", x.getString("contactMechId"));
			mapCreateContact.put("contactMechPurposeTypeId", x.getString("contactMechPurposeTypeId"));
			mapCreateContact.put("userLogin", userLogin);
			dispatcher.runSync("deletePartyContactMechPurposeIfExists", mapCreateContact);
		}
	}
	private static void createAddress(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String partyId, String receiverName, String contactAddress, String otherInfo)
			throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isNotEmpty(contactAddress)) {
			JSONArray primaryAddressArray = JSONArray.fromObject(contactAddress);
			for(int i = 0; i< primaryAddressArray.size(); i++){
				JSONObject o = primaryAddressArray.getJSONObject(i);
				String address1 = null;
				if (o.containsKey("address1")) {
					address1 = o.getString("address1");
				}
				String wardGeoId = null;
				if (o.containsKey("wardGeoId")) {
					wardGeoId = cleanString(o.getString("wardGeoId"));
				}
				String districtGeoId = null;
				if (o.containsKey("districtGeoId")) {
					districtGeoId = cleanString(o.getString("districtGeoId"));
				}
				String stateProvinceGeoId = null;
				if (o.containsKey("stateProvinceGeoId")) {
					stateProvinceGeoId = cleanString(o.getString("stateProvinceGeoId"));
				}
				String countryGeoId = null;
				if (o.containsKey("countryGeoId")) {
					countryGeoId = cleanString(o.getString("countryGeoId"));
				}
				String postalCode = null;
				if (o.containsKey("postalCode")) {
					postalCode = o.getString("postalCode");
				}
				if (UtilValidate.isEmpty(postalCode)) {
					postalCode = "70000";
				}
				String contactMechId = createContactMechPostalAddress(dispatcher, delegator, "", "POSTAL_ADDRESS", receiverName, otherInfo, "SHIPPING_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId, getGeoName(delegator, stateProvinceGeoId), postalCode, userLogin);
				if (i == 0 && UtilValidate.isNotEmpty(contactMechId)) {
					createContactMechPostalAddress(dispatcher, delegator, contactMechId, "POSTAL_ADDRESS", receiverName, otherInfo, "PRIMARY_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId, getGeoName(delegator, stateProvinceGeoId), postalCode, userLogin);
				}
			}
		}
	}
	private static void saveAddress(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String partyId, String receiverName, String contactAddress, String otherInfo)
			throws GenericEntityException, GenericServiceException {
		//	check PRIMARY_LOCATION available
		boolean hasPrimaryLocation = true;
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")));
		List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("contactMechId"), null, null, false);

		String oldPrimaryAddress = null;
		if (UtilValidate.isEmpty(listPartyContactMechPurpose)) {
			hasPrimaryLocation = false;
		} else {
			for (GenericValue x : listPartyContactMechPurpose) {
				if (listPartyContactMechPurpose.indexOf(x) == 0) {
					oldPrimaryAddress = x.getString("contactMechId");
				}
			}
		}
		List<String> listContactMechid = FastList.newInstance();
		if (UtilValidate.isNotEmpty(contactAddress)) {
			JSONArray shippingAddressArray = JSONArray.fromObject(contactAddress);
			for(int i = 0; i< shippingAddressArray.size(); i++){
				JSONObject o = shippingAddressArray.getJSONObject(i);
				String contactMechId = null;
				if (o.containsKey("contactMechId")) {
					contactMechId = cleanString(o.getString("contactMechId"));
					GenericValue contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", contactMechId), false);
					if (UtilValidate.isEmpty(contactMech)) {
						contactMechId = null;
					}
				}
				String address1 = null;
				if (o.containsKey("address1")) {
					address1 = o.getString("address1");
				}
				String wardGeoId = null;
				if (o.containsKey("wardGeoId")) {
					wardGeoId = cleanString(o.getString("wardGeoId"));
				}
				String districtGeoId = null;
				if (o.containsKey("districtGeoId")) {
					districtGeoId = cleanString(o.getString("districtGeoId"));
				}
				String stateProvinceGeoId = null;
				if (o.containsKey("stateProvinceGeoId")) {
					stateProvinceGeoId = cleanString(o.getString("stateProvinceGeoId"));
				}
				String countryGeoId = null;
				if (o.containsKey("countryGeoId")) {
					countryGeoId = cleanString(o.getString("countryGeoId"));
				}
				String postalCode = null;
				if (o.containsKey("postalCode")) {
					postalCode = o.getString("postalCode");
				}
				if (UtilValidate.isEmpty(postalCode)) {
					postalCode = "70000";
				}
				if (UtilValidate.isEmpty(contactMechId)) {
					contactMechId = createContactMechPostalAddress(dispatcher, delegator, "", "POSTAL_ADDRESS", receiverName, otherInfo, "SHIPPING_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId,
							districtGeoId, wardGeoId, getGeoName(delegator, stateProvinceGeoId), postalCode, userLogin);
					listContactMechid.add(contactMechId);
				} else {
					contactMechId = updateContactMechPostalAddress(dispatcher, delegator, contactMechId, "POSTAL_ADDRESS", receiverName, otherInfo, "SHIPPING_LOCATION", partyId, address1, countryGeoId,
							stateProvinceGeoId, districtGeoId, wardGeoId, getGeoName(delegator, stateProvinceGeoId), postalCode, userLogin);
					listContactMechid.add(contactMechId);
				}
				if (!hasPrimaryLocation) {
					hasPrimaryLocation = true;
					createContactMechPostalAddress(dispatcher, delegator, contactMechId, "POSTAL_ADDRESS", receiverName, otherInfo, "PRIMARY_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId,
							districtGeoId, wardGeoId, getGeoName(delegator, stateProvinceGeoId), postalCode, userLogin);
				} else {
					String note = null;
					if (o.containsKey("note")) {
						note = o.getString("note");
					}
					if (UtilValidate.isNotEmpty(note)) {
						if (!contactMechId.equals(oldPrimaryAddress)) {
							createContactMechPostalAddress(dispatcher, delegator, contactMechId, "POSTAL_ADDRESS", receiverName, otherInfo, "PRIMARY_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId,
									districtGeoId, wardGeoId, getGeoName(delegator, stateProvinceGeoId), postalCode, userLogin);
							cleanPartyContactMechPurpose(dispatcher, delegator, UtilMisc.toList(contactMechId), "PRIMARY_LOCATION", partyId, userLogin);
						}
					}
				}
			}
		}
		cleanPartyContactMechPurpose(dispatcher, delegator, listContactMechid, "SHIPPING_LOCATION", partyId, userLogin);
	}
	public static String getGeoName(Delegator delegator, String geoId)
			throws GenericEntityException {
		String geoName = "";
		if (UtilValidate.isNotEmpty(geoId)) {
			GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
			if (UtilValidate.isNotEmpty(geo)) {
				geoName = geo.getString("geoName");
			}
		}
		return geoName;
	}
	public static String updateContactMechTelecomNumber(LocalDispatcher dispatcher, Delegator delegator, String contactMechId, Object contactMechTypeId, Object contactMechPurposeTypeId,
			Object contactNumber, Object askForName, Object personId, GenericValue userLogin)
					throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(contactNumber)) {
			//	return contactMechId;
		}
		if (UtilValidate.isEmpty(contactMechId)) {
			contactMechId = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", contactMechPurposeTypeId, contactNumber, askForName, personId, userLogin);
		} else {
			delegator.storeByCondition("TelecomNumber", UtilMisc.toMap("askForName", askForName, "contactNumber", contactNumber), EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contactMechId)));
		}
		return contactMechId;
	}
	
	public static Map<String, Object> loadReasonListByReasonTypeId (DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String resultEnumId = (String)context.get("resultEnumId");
		List<String> orderBy = new ArrayList<String>();
		List<GenericValue> listReasonClaim = delegator.findList("EnumTypeChildAndEnum",
				EntityCondition.makeCondition(UtilMisc.toMap("childEnumTypeId", resultEnumId)), null, orderBy, null, false);
		if ("COMM_INBOUND_RESULT".equals(resultEnumId)) {
			listReasonClaim = delegator.findList("Enumeration",EntityCondition.makeCondition(UtilMisc.toMap("enumTypeId", resultEnumId)), null, orderBy, null, false);
		}
		result.put("listReasonClaim", listReasonClaim);
		return result;
	}
	
	/* create an issue from customer */
	public static Map<String, Object> raiseCustomerIssue(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String partyId = (String) context.get("partyId");
			String eventType = (String) context.get("communicationEventTypeId");
			String content = (String) context.get("content");
			//	product discussing
			String subject = (String) context.get("subject");
			String subjectSchedule = (String) context.get("subjectSchedule");
			String resultEnumTypeId = (String) context.get("resultEnumTypeId");
			String resultEnumId = (String) context.get("resultEnumId");
			String subjectEnumId = (String) context.get("subjectEnumId");
			String currentBrandId = (String) context.get("currentBrandId");
			String currentProductId = (String) context.get("currentProductId");
			String previousProductId = (String) context.get("previousProductId");
			String previousBrandId = (String) context.get("previousBrandId");
			String productDiscussedId = (String) context.get("productDiscussedId");
			Long nextCallSchedule = (Long) context.get("nextCallSchedule");
			String type = (String) context.get("type");
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			String isCallout = "Y";
			Timestamp currentDate = new Timestamp(System.currentTimeMillis()); 
			Map<String, Object> tmp = FastMap.newInstance();
			tmp.put("userLogin", userLogin);
			tmp.put("communicationEventTypeId", eventType);
			if(UtilValidate.isNotEmpty(nextCallSchedule)){
				Map<String, Object> schedule = FastMap.newInstance();
				schedule.putAll(tmp);
				schedule.put("partyId", partyId);
				schedule.put("entryDate", new Timestamp(nextCallSchedule));
				schedule.put("subjectEnumId", subjectSchedule);
				schedule.put("userLogin", userLogin);
				dispatcher.runSync("scheduleCommunication", schedule);
			}

			if(type.equals("receive")){
				isCallout = "N";
				tmp.put("partyIdFrom", partyId);
				tmp.put("partyIdTo", userLogin.get("partyId"));
			}else{
				tmp.put("partyIdTo", partyId);
				tmp.put("partyIdFrom", userLogin.get("partyId"));
			}
			tmp.put("communicationEventId",
					delegator.getNextSeqId("CommunicationEvent"));
			tmp.put("content", content);
			tmp.put("resultEnumTypeId", resultEnumTypeId);
			tmp.put("currentBrandId", currentBrandId);
			tmp.put("previousBrandId", previousBrandId);
			tmp.put("currentProductId", currentProductId);
			tmp.put("previousProductId", previousProductId);
			tmp.put("productDiscussedId", productDiscussedId);
			tmp.put("statusId", "COM_COMPLETE");
			tmp.put("subjectEnumId", subjectEnumId);
			if(UtilValidate.isNotEmpty(resultEnumId)){
				tmp.put("resultEnumId", resultEnumId);
			}
			tmp.put("subject", subject);
			tmp.put("entryDate", currentDate);
			tmp.put("isCallOut", isCallout);
			dispatcher.runSync("createCommunicationCRM", tmp);
			result.put("status", "success");
			if (UtilValidate.isNotEmpty(marketingCampaignId)) {
				String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
				String roleTypeIdTo = (String) context.get("roleTypeIdTo");
				Long fromDateL = (Long) context.get("fromDate");
				Timestamp fromDate = null;
				if (UtilValidate.isNotEmpty(fromDateL)) {
					fromDate = new Timestamp(fromDateL);
				}
				if ("send".equals(type)) {
					//	check status of PartyCampaignRelationship before update
					List<EntityCondition> conditions = FastList.newInstance();
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyIdFrom", userLogin.get("partyId"), "partyIdTo", partyId, 
							"roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo, "fromDate", fromDate, "statusId", "CONTACT_COMPLETED")));
					List<GenericValue> check = delegator.findList("PartyCampaignRelationship",
							EntityCondition.makeCondition(conditions), null, null, null, false);
					
					if (UtilValidate.isEmpty(check)) {
						if ("O".equals(resultEnumTypeId)) {
							switch (checkCountContactFalse(delegator, partyId)) {
							case 0:
							case 1:
							case 2:
									dispatcher.runSync("updatePartyCampaignRelationship",
											UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyIdFrom", userLogin.get("partyId"),
													"partyIdTo", partyId, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo,
													"fromDate", fromDate, "statusId", "CONTACT_INPROGRESS", "userLogin", userLogin));
								break;
							case 3:
									dispatcher.runSync("updatePartyCampaignRelationship",
											UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyIdFrom", userLogin.get("partyId"),
													"partyIdTo", partyId, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo,
													"fromDate", fromDate, "statusId", "CONTACT_FAIL", "userLogin", userLogin));
								break;
							default:
									dispatcher.runSync("updatePartyCampaignRelationship",
											UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyIdFrom", userLogin.get("partyId"),
													"partyIdTo", partyId, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo,
													"fromDate", fromDate, "statusId", "CONTACT_FAIL", "userLogin", userLogin));
								break;
							}
						} else {
								dispatcher.runSync("updatePartyCampaignRelationship",
										UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyIdFrom", userLogin.get("partyId"), "partyIdTo", partyId, 
												"roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo, "fromDate", fromDate,
												"statusId", "CONTACT_COMPLETED", "userLogin", userLogin));
						}
					}
				}
				// Edit by VietTB : Update Is Call Status
				dispatcher.runSync("updatePartyCampaignRelationship",
						UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "partyIdFrom", userLogin.get("partyId"),
								"partyIdTo", partyId, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", roleTypeIdTo,
								"fromDate", fromDate, "isCall", "Y", "userLogin", userLogin));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	private static Integer checkCountContactFalse(Delegator delegator, String partyId)
			throws GenericEntityException {
		List<GenericValue> communicationEvents = delegator.findList("CommunicationEvent",
				EntityCondition.makeCondition(UtilMisc.toMap("communicationEventTypeId", "PHONE_COMMUNICATION", "partyIdTo", partyId,
						"isCallOut", "Y", "resultEnumTypeId", "I")), null, null, null, false);
		return communicationEvents.size();
	}
	
	public static Map<String, Object> scheduleCommunication(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			Timestamp entryDate = (Timestamp) context.get("entryDate");
			String subjectEnumId = (String) context.get("subjectEnumId");
			String partyId = (String) context.get("partyId");
			String communicationEventTypeId = (String) context.get("communicationEventTypeId");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> schedule = FastMap.newInstance();
			String communicationEventId = delegator.getNextSeqId("CommunicationEvent");
			if(UtilValidate.isEmpty(communicationEventTypeId)){
				communicationEventTypeId = "PHONE_COMMUNICATION";
			}
			schedule.put("communicationEventId", communicationEventId);
			schedule.put("communicationEventTypeId", communicationEventTypeId);
			schedule.put("partyIdTo", partyId);
			schedule.put("partyIdFrom", userLogin.get("partyId"));
			schedule.put("entryDate", entryDate);
			schedule.put("statusId", "COM_SCHEDULED");
			schedule.put("subjectEnumId", subjectEnumId);
			schedule.put("userLogin", userLogin);
			dispatcher.runSync("createCommunicationCRM", schedule);
			result.put("communicationEventId", communicationEventId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> createCommunication(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue communicationEvent = delegator.makeValidValue("CommunicationEvent", context);
			communicationEvent.create();
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("CallCenterUiLabels", "CannotCreateCommEvent", locale));
		}
		return result;
	}
	public static Map<String, Object> updateCommunication(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue communicationEvent = delegator.makeValidValue("CommunicationEvent", context);
			communicationEvent.store();
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("CallCenterUiLabels", "CannotCreateCommEvent", locale));
		}
		return result;
	}
	
	public static Map<String, Object> checkCallSchedule(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Security se = ctx.getSecurity();
		try{
			String[] freq = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
			Calendar cal = Calendar.getInstance();
			int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
			String dayOfMonthStr = String.valueOf(dayOfMonth);
			List<String> term = FastList.newInstance();
			for(String e : freq){
				term.add(e);
			}
			term.add(dayOfMonthStr);
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			String partyId = userLogin.getString("partyId");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			GenericValue e = null;
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Timestamp entryDate = new Timestamp(cal.getTimeInMillis());
			String entityname = "AgreementAndAgreementTerm";
			List<EntityCondition> listC = UtilMisc.toList(
					EntityCondition.makeCondition("termTypeId", "DELIVER_DATE_FREQUEN"),
					EntityCondition.makeCondition("partyIdTo", organizationId),
					EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("entryDate", EntityOperator.LESS_THAN, entryDate),
							EntityCondition.makeCondition("entryDate", EntityOperator.EQUALS, null)), EntityOperator.OR),
					EntityCondition.makeCondition("textValue", EntityOperator.IN, term));
			if(!se.hasPermission("CALLCAMPAIGN_ADMIN", userLogin)){
				listC.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr("fromDate", "thruDate")));
				listC.add(EntityCondition.makeCondition("callerPartyId", partyId));
				entityname = "AgreementAndAgreementTermCampaign";
			}
			EntityCondition condition = EntityCondition.makeCondition(listC);
			EntityListIterator list = delegator.find(entityname, condition,
										null, UtilMisc.toSet("agreementId","partyIdFrom"), null, opts);
			while((e = list.next()) != null){
				Map<String, Object> obj = FastMap.newInstance();
				obj.put("userLogin", userLogin);
				obj.put("subjectEnumId", "COM_SCHEDULE_AGR");
				obj.put("partyId", e.getString("partyIdFrom"));
				obj.put("entryDate", entryDate);
				dispatcher.runSync("scheduleCommunication", obj);
			}
			result.put("result", "SUCCESS");
		} catch (Exception e){
			e.printStackTrace();
			result.put("result", "ERROR");
		}
		return result;
	}
	
	public static Map<String, Object> getProductBySupplier(DispatchContext ctx, Map<String, ? extends Object> context){
		  Map<String, Object> result = FastMap.newInstance();
		  Delegator delegator = ctx.getDelegator();
		  Locale locale = (Locale) context.get("locale");
		  try{
			  String partyId = (String) context.get("partyId");
			  List<GenericValue> res = delegator.findList("SupplierProductAndProduct",
					EntityCondition.makeCondition("partyId", partyId), UtilMisc.toSet("productId", "productName"), null, null, false);
			  result.put("results", res);
		  } catch (Exception e){
			  e.printStackTrace();
			  return ServiceUtil.returnError(UtilProperties.getMessage("CallCenterUiLabels", "MissingCondition", locale));
		  }
		  return result;
	}
	
	public static Map<String, Object> createUsingHistory(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue inset = delegator.makeValidValue("ProductUsingHistory", context);
			String productUsi = delegator.getNextSeqId("ProductUsingHistory");
			inset.set("productUsingHistoryId", productUsi);
			delegator.create(inset);
			result.put("productUsingHistoryId", productUsi);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("CallcenterUiLabels", "CannotCreateProductUsing", locale));
		}
		return result;
	}
	
	private static void createPartyRole(LocalDispatcher dispatcher, String partyId, GenericValue userLogin)
			throws GenericServiceException {
		//	createPartyRole CONTACT for corporation
		dispatcher.runSync("createPartyRole",
				UtilMisc.toMap("partyId", partyId, "roleTypeId", "CONTACT", "userLogin", userLogin));
		//	createPartyRole BILL_TO_CUSTOMER for corporation
		dispatcher.runSync("createPartyRole",
				UtilMisc.toMap("partyId", partyId, "roleTypeId", "BILL_TO_CUSTOMER", "userLogin", userLogin));
		//	createPartyRole END_USER_CUSTOMER for corporation
		dispatcher.runSync("createPartyRole",
				UtilMisc.toMap("partyId", partyId, "roleTypeId", "END_USER_CUSTOMER", "userLogin", userLogin));
		//	createPartyRole PLACING_CUSTOMER for corporation
		dispatcher.runSync("createPartyRole",
				UtilMisc.toMap("partyId", partyId, "roleTypeId", "PLACING_CUSTOMER", "userLogin", userLogin));
		//	createPartyRole SHIP_TO_CUSTOMER for corporation
		dispatcher.runSync("createPartyRole",
				UtilMisc.toMap("partyId", partyId, "roleTypeId", "SHIP_TO_CUSTOMER", "userLogin", userLogin));
//		createPartyRole INDIVIDUAL_CUSTOMER for corporation
		dispatcher.runSync("createPartyRole",
				UtilMisc.toMap("partyId", partyId, "roleTypeId", "INDIVIDUAL_CUSTOMER", "userLogin", userLogin));
		//	createPartyRole CUSTOMER for person
		dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER", "userLogin", userLogin));
	}
	private static void createPartyCampaignRelationship(LocalDispatcher dispatcher, String partyId, String ownerEmployee, GenericValue userLogin)
			throws GenericServiceException {
		//	createPartyCampaignRelationship INDEFINITE_CAMPAIGN
		Map<String, Object> in = FastMap.newInstance();
		in.put("userLogin", userLogin);
		in.put("marketingCampaignId", "INDEFINITE_CAMPAIGN");
		if (UtilValidate.isNotEmpty(ownerEmployee)) {
			in.put("partyIdFrom", ownerEmployee);
		} else {
			in.put("partyIdFrom", userLogin.getString("partyId"));
		}
		in.put("partyIdTo", partyId);
		in.put("roleTypeIdFrom", "CALLCENTER_EMPL");
		in.put("roleTypeIdTo", "CONTACT");
		in.put("fromDate", new Timestamp(System.currentTimeMillis()));
		in.put("thruDate", null);
		in.put("statusId", "CONTACT_ASSIGNED");
		dispatcher.runSync("createPartyCampaignRelationship", in);
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPayment(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) (context.get("listAllConditions") != null ?  context.get("listAllConditions") : FastList.newInstance());
		List<EntityCondition> listConditions = new FastList<EntityCondition>();
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");    	
    	String strOrganizationPartyId = (String) context.get("organizationPartyId");
    	String strPartyId = (String) context.get("partyId");
		List<EntityCondition> tmpArPaymentType = FastList.newInstance();
		tmpArPaymentType.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "RECEIPT"));
		tmpArPaymentType.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strPartyId));
		tmpArPaymentType.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, strOrganizationPartyId));
		EntityCondition tmpArPayment = EntityCondition.makeCondition( tmpArPaymentType, EntityJoinOperator.AND);
		listConditions.add(tmpArPayment);
		List<EntityCondition> tmpApPaymentType = FastList.newInstance();
		tmpApPaymentType.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "DISBURSEMENT"));
		tmpApPaymentType.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strOrganizationPartyId));
		tmpApPaymentType.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, strPartyId));
		EntityCondition tmpApPayment = EntityCondition.makeCondition( tmpApPaymentType, EntityJoinOperator.AND);		
		listConditions.add(tmpApPayment);
		EntityCondition tmpConditon = EntityCondition.makeCondition(listConditions,EntityJoinOperator.OR);
		
		listAllConditions.add(tmpConditon);
		
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("PaymentAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions =  (List<EntityCondition>) (context.get("listAllConditions") != null ? (List<EntityCondition>) context.get("listAllConditions") : FastList.newInstance());
		List<EntityCondition> listConditions = new FastList<EntityCondition>();
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");    	
    	String strOrganizationPartyId = (String) context.get("organizationPartyId");
    	String strPartyId = (String) context.get("partyId");
		List<EntityCondition> tmpAPInvoiceType = FastList.newInstance();
		tmpAPInvoiceType.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"));
		tmpAPInvoiceType.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strPartyId));
		tmpAPInvoiceType.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, strOrganizationPartyId));
		EntityCondition tmpApInvoice = EntityCondition.makeCondition( tmpAPInvoiceType, EntityJoinOperator.AND);
		listConditions.add(tmpApInvoice);
		List<EntityCondition> tmpArInvoiceType = FastList.newInstance();
		tmpArInvoiceType.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "SALES_INVOICE"));
		tmpArInvoiceType.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strOrganizationPartyId));
		tmpArInvoiceType.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, strPartyId));
		EntityCondition tmpArInvoice = EntityCondition.makeCondition( tmpArInvoiceType, EntityJoinOperator.AND);		
		listConditions.add(tmpArInvoice);
		EntityCondition tmpConditon = EntityCondition.makeCondition(listConditions,EntityJoinOperator.OR);
		
		listAllConditions.add(tmpConditon);
		
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    public static Map<String, Object> getLoyaltyPoint(DispatchContext ctx, Map<String, Object> context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String partyId = (String) context.get("partyId");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			BigDecimal loyaltyPoint = LoyaltyUtil.getTotalPoint(delegator, partyId, userLogin);
			result.put("loyaltyPoint", loyaltyPoint);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("error");
		}
		return result;
    }
    public static Map<String, Object> getPartyClassificationGroup(DispatchContext ctx, Map<String, Object> context) {
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	try {
    		List<EntityCondition> conditions = FastList.newInstance();
    		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
    		conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, context.get("partyId")));
    		List<GenericValue> partyClassifications = delegator.findList("PartyClassification",
    				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyClassificationGroupId"), null, null, false);
    		if (UtilValidate.isNotEmpty(partyClassifications)) {
				result.put("value", EntityUtil.getFirst(partyClassifications).get("partyClassificationGroupId"));
			}
    	} catch (Exception e) {
    		return ServiceUtil.returnError("error");
    	}
    	return result;
    }
    
	public static Map<String, Object> checkPartyCode(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			result.put("check", "true");
		} catch (Exception e) {
			result.put("check", "false");
		}
		return result;
	}
	public static Map<String, Object> fixPartyCode(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			EntityListIterator parties = delegator.find("Party", null, null, null, null, null);
			GenericValue party = null;
			while((party = parties.next()) != null){
				if (UtilValidate.isEmpty(party.get("partyCode"))) {
					party.set("partyCode", party.get("partyId") + "CODE");
					party.store();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> updatePartyGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			Object partyCode = context.get("partyCode");
			context.remove("partyCode");
			dispatcher.runSync("updatePartyGroup", context);
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", partyCode), EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, context.get("partyId")));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> createPartyGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			Object partyCode = context.get("partyCode");
			context.remove("partyCode");
			result = dispatcher.runSync("createPartyGroup", context);
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", partyCode), EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, result.get("partyId")));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> importCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			
			EntityListIterator tempCustomers = delegator.find("TempCustomer",
					EntityCondition.makeCondition("partyFullName", EntityJoinOperator.NOT_EQUAL, null), null, null, null, null);
			GenericValue customer = null;
			Map<String, Object> mapCreateContact = FastMap.newInstance();
			while((customer = tempCustomers.next()) != null) {
				String partyFullName = cleanString(customer.getString("partyFullName"));
				Date birthDate = null;
				if (customer.get("birthDate") instanceof Date) {
					birthDate = customer.getDate("birthDate");
				}
				mapCreateContact.clear();
				mapCreateContact.putAll(demarcatePersonName(partyFullName));
				mapCreateContact.put("birthDate", birthDate);
				mapCreateContact.put("statusId", "PARTY_ENABLED");
				mapCreateContact.put("userLogin", userLogin);
				String partyId = "KH" + organizationId + delegator.getNextSeqId("Party");
				mapCreateContact.put("partyId", partyId);
				Map<String, Object> resultCreatePerson = dispatcher.runSync("createPerson", mapCreateContact);
				partyId = (String) resultCreatePerson.get("partyId");
				delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", partyId),
						EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
				
				//	createPartyRole CONTACT for corporation
				delegator.create("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CONTACT"));
				//	createPartyRole BILL_TO_CUSTOMER for corporation
				delegator.create("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "BILL_TO_CUSTOMER"));
				//	createPartyRole END_USER_CUSTOMER for corporation
				delegator.create("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "END_USER_CUSTOMER"));
				//	createPartyRole PLACING_CUSTOMER for corporation
				delegator.create("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "PLACING_CUSTOMER"));
				//	createPartyRole SHIP_TO_CUSTOMER for corporation
				delegator.create("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "SHIP_TO_CUSTOMER"));
				//	createPartyRole CUSTOMER for person
				delegator.create("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER"));
				
				String familyId = null;
				Map<String, Object> resultCreateFamily = dispatcher.runSync("createPartyGroup",
						UtilMisc.toMap("partyTypeId", "FAMILY", "groupName", "Family" + partyFullName, "statusId", "PARTY_ENABLED", "userLogin", userLogin));
				familyId = (String) resultCreateFamily.get("partyId");
				
				//	createPartyRole REPRESENTATIVE for person
				delegator.create("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "REPRESENTATIVE"));
				//	createPartyRole HOUSEHOLD for family if not available
				delegator.create("PartyRole", UtilMisc.toMap("partyId", familyId, "roleTypeId", "HOUSEHOLD"));
				//	createPartyRelationship between person and family
				delegator.create("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", familyId, "roleTypeIdFrom", "REPRESENTATIVE", "roleTypeIdTo", "HOUSEHOLD",
						"partyRelationshipTypeId", "PARTNERSHIP", "fromDate", new Timestamp(System.currentTimeMillis())));
				//	createPartyRelationship between person and company
				delegator.create("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", "CONTACT", "roleTypeIdTo", "INTERNAL_ORGANIZATIO",
						"partyRelationshipTypeId", "CONTACT_REL", "fromDate", new Timestamp(System.currentTimeMillis())));
				
				String phoneHome = customer.getString("phoneHome");;
				String PHONE_HOME = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_HOME",
						phoneHome, partyFullName, partyId, userLogin);
				String officeHome = customer.getString("phoneWork");
				String PHONE_WORK = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_WORK",
						officeHome, partyFullName, partyId, userLogin);
				String mobilePhone = customer.getString("phoneMobile");
				String PHONE_MOBILE = createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PHONE_MOBILE",
						mobilePhone, partyFullName, partyId, userLogin);
				
				String primaryPhone = "";
				if (UtilValidate.isNotEmpty(PHONE_HOME)) {
					primaryPhone = "PHONE_HOME";
				} else if (UtilValidate.isNotEmpty(PHONE_WORK)) {
					primaryPhone = "PHONE_WORK";
				} else if (UtilValidate.isNotEmpty(PHONE_MOBILE)) {
					primaryPhone = "PHONE_MOBILE";
				}
				addPrimaryAndShippingPhone(dispatcher, primaryPhone, PHONE_HOME, PHONE_WORK, PHONE_MOBILE, partyId, "PRIMARY_PHONE", userLogin);
				addPrimaryAndShippingPhone(dispatcher, primaryPhone, PHONE_HOME, PHONE_WORK, PHONE_MOBILE, partyId, "PHONE_SHIPPING", userLogin);
				createContactMechEmail(dispatcher, "EMAIL_ADDRESS", customer.get("emailAddress"), "PRIMARY_EMAIL", partyId, userLogin);
				createContactMechFacebook(dispatcher, "WEB_ADDRESS", customer.get("facebook"), "FACEBOOK_URL", partyId, userLogin);
				
				String address1 = customer.getString("address1");
				String wardGeoId = customer.getString("wardGeoId");
				String districtGeoId = customer.getString("districtGeoId");
				String stateProvinceGeoId = customer.getString("stateProvinceGeoId");
				String countryGeoId = customer.getString("countryGeoId");
				String postalCode = "70000";
				String contactMechId = createContactMechPostalAddress(dispatcher, delegator, "", "POSTAL_ADDRESS", partyFullName, partyFullName, "SHIPPING_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId, getGeoName(delegator, stateProvinceGeoId), postalCode, userLogin);
				createContactMechPostalAddress(dispatcher, delegator, contactMechId, "POSTAL_ADDRESS", partyFullName, partyFullName, "PRIMARY_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId, getGeoName(delegator, stateProvinceGeoId), postalCode, userLogin);
				
				//	create child1
				String childName1 = customer.getString("childName1");
				if (UtilValidate.isNotEmpty(childName1)) {
					birthDate = null;
					if (customer.get("childName1birthDate") instanceof Date) {
						birthDate = customer.getDate("childName1birthDate");
					}
					mapCreateContact.clear();
					mapCreateContact.putAll(demarcatePersonName(childName1));
					mapCreateContact.put("birthDate", birthDate);
					mapCreateContact.put("statusId", "PARTY_ENABLED");
					mapCreateContact.put("userLogin", userLogin);
					String personId = null;
					resultCreatePerson = dispatcher.runSync("createPerson", mapCreateContact);
					personId = (String) resultCreatePerson.get("partyId");
					
					//	createPartyRole for person
					delegator.create("PartyRole", UtilMisc.toMap("partyId", personId, "roleTypeId", "CHILDREN"));
					//	createPartyRelationship between person and family
					delegator.create("PartyRelationship", UtilMisc.toMap("partyIdFrom", personId, "partyIdTo", familyId, "roleTypeIdFrom", "CHILDREN", "roleTypeIdTo", "HOUSEHOLD",
							"partyRelationshipTypeId", "PARTNERSHIP", "fromDate", new Timestamp(System.currentTimeMillis())));
				}
				
				//	create child2
				String childName2 = customer.getString("childName2");
				if (UtilValidate.isNotEmpty(childName2)) {
					birthDate = null;
					if (customer.get("childName2birthDate") instanceof Date) {
						birthDate = customer.getDate("childName2birthDate");
					}
					mapCreateContact.clear();
					mapCreateContact.putAll(demarcatePersonName(childName2));
					mapCreateContact.put("birthDate", birthDate);
					mapCreateContact.put("statusId", "PARTY_ENABLED");
					mapCreateContact.put("userLogin", userLogin);
					String personId = null;
					resultCreatePerson = dispatcher.runSync("createPerson", mapCreateContact);
					personId = (String) resultCreatePerson.get("partyId");
					//	createPartyRole for person
					delegator.create("PartyRole", UtilMisc.toMap("partyId", personId, "roleTypeId", "CHILDREN"));
					//	createPartyRelationship between person and family
					delegator.create("PartyRelationship", UtilMisc.toMap("partyIdFrom", personId, "partyIdTo", familyId, "roleTypeIdFrom", "CHILDREN", "roleTypeIdTo", "HOUSEHOLD",
							"partyRelationshipTypeId", "PARTNERSHIP", "fromDate", new Timestamp(System.currentTimeMillis())));
				}
			}
			result.put("size", String.valueOf(tempCustomers.getResultsTotalSize()));
			dispatcher.runSync("deleteAllTempCustomer", UtilMisc.toMap("userLogin", userLogin));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> deleteAllTempCustomer(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericDataSourceException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		SQLProcessor processor = new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz"));
		try {
			processor.getConnection();
			processor.executeUpdate("DELETE FROM temp_customer");
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		} finally {
			processor.close();
		}
		return result;
	}
	public static Map<String, Object> updateTempCustomer(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericDataSourceException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue tempCustomer = delegator.makeValidValue("TempCustomer", context);
			tempCustomer.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> deleteTempCustomer(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericDataSourceException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue tempCustomer = delegator.makeValidValue("TempCustomer", context);
			tempCustomer.remove();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listTempCustomers(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("customerId");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("partyFullName", EntityJoinOperator.NOT_EQUAL, null));
			EntityListIterator listIterator = delegator.find("TempCustomer",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e){
			e.printStackTrace();
		}
    	return result;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductDiscuss(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("productName");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("productTypeId", EntityJoinOperator.EQUALS, "FINISHED_GOOD"));
			EntityListIterator listIterator = delegator.find("Product",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPurchaseHistory(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
			if (parameters.containsKey("partyId")) {
				String partyId = (String) parameters.get("partyId")[0];
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				listSortFields.add("-thruDate");
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "ITEM_CANCELLED"));
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
				EntityListIterator listIterator = delegator.find("OrderAndCustomerSynthetic",
						EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				result.put("listIterator", listIterator);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> loadPartiesAndConsideration(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			String partyId = (String) context.get("partyId");
			List<Map<String, Object>> info = FastList.newInstance();
			List<String> families = CRMServices.getFamilyOfPerson(partyId, delegator);
			List<Map<String, Object>> members = getAllMemberOfFamily(delegator, families);
			for (Map<String, Object> x : members) {
				Map<String, Object> member = FastMap.newInstance();
				Object memberId = x.get("partyIdFrom");
				if (UtilValidate.isNotEmpty(memberId)) {
					Map<String, Object> memberConsideration = FastMap.newInstance();
					Object memberRole = x.get("roleTypeIdFrom");
					GenericValue person = delegator.findOne("PersonAndPartyGroup", UtilMisc.toMap("partyId", memberId), false);
					GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", memberRole), false);
					member.put("memberId", memberId);
					member.put("memberName", person.get("partyFullName"));
					
					memberConsideration.put("gender", person.get("gender"));
					memberConsideration.put("birthDate", person.get("birthDate"));
					memberConsideration.put("description", person.get("description"));
					memberConsideration.put("memberRole", roleType.get("description", locale));
					
					member.put("memberConsideration", memberConsideration);
					info.add(member);
				}
			}
			result.put("info", info);
		} catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
}