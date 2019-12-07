package com.olbius.procurement;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.registry.infomodel.User;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.engine.GenericEngine;


public class SupplierServices {
	public static final String module = SupplierServices.class.getName();
	public static final String resource = "DelysProcurementLabels";
    public static final String resourceError = "widgetErrorUiLabels";

    
    public static Map<String, Object> getListSupplier(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
		int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
	    int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
	    int start = pagesize * pageNum;
	    int end = start + pagesize;
		try {
			opts.setDistinct(true);
			List<String> listSortFields = (List<String>) context
					.get("listSortFields");
			listSortFields.add("statusId ASC");
			List<EntityCondition> role = FastList.newInstance();
			role.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"));
			role.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "VENDOR"));
			listAllConditions.add(EntityCondition.makeCondition(role, EntityOperator.OR));
			EntityListIterator listIter = delegator.find("PartyGroupAndPartyRoleStatus", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			List<GenericValue> tmpcontacts = listIter.getPartialList(start, end);
			String tmp2 = "";
			Map<String, Object> tmp = FastMap.newInstance();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<Map<String, Object>> res = FastList.newInstance(); 
			for(GenericValue contact : tmpcontacts){
				Map<String,Object> o = FastMap.newInstance();
				tmp2 = contact.getString("partyId"); 
				o.put("partyId", tmp2);
				o.put("groupName", contact.getString("groupName"));
//				o.put("roleTypeId", contact.getString("roleTypeId"));
				o.put("statusId", contact.getString("statusId"));
				tmp = FastMap.newInstance();
				/*get other phone*/
				tmp.put("userLogin", userLogin);
				tmp.put("partyId", tmp2);
				tmp.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
				tmp = dispatcher.runSync("getPartyTelephone", tmp);
				o.put("phone", tmp);
				tmp = FastMap.newInstance();
				/*get fax*/
				tmp.put("userLogin", userLogin);
				tmp.put("partyId", tmp2);
				tmp.put("contactMechPurposeTypeId", "FAX_NUMBER");
				tmp = dispatcher.runSync("getPartyTelephone", tmp);
				o.put("fax", tmp);
				tmp = FastMap.newInstance();
				tmp.put("userLogin", userLogin);
				tmp.put("partyId", tmp2);
				tmp.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
				tmp = dispatcher.runSync("getPartyEmail", tmp);
				o.put("email", tmp.get("emailAddress"));
				tmp = FastMap.newInstance();
				tmp.put("userLogin", userLogin);
				tmp.put("partyId", tmp2);
				tmp.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				tmp = dispatcher.runSync("getPartyPostalAddress", tmp);
				o.put("address", tmp);
				res.add(o);
			}
			successResult.put("listIterator", res);
			successResult.put("TotalRows", String.valueOf(listIter.getCompleteList().size()));
			listIter.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return successResult;
	}
    public static Map<String, Object> createSupplier(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String address = (String) context.get("address");
		String countryGeoId = (String) context.get("country");
		String stateProvinceGeoId = (String) context.get("province");
		String districtGeoId = (String) context.get("district");
		String wardGeoId = (String) context.get("ward");
		
		String phone = (String) context.get("phone");
		String fax = (String) context.get("fax");
		String email = (String) context.get("email");
		String description = (String) context.get("description");
		String content = (String) context.get("comments");
		String groupName = (String) context.get("groupName");
		String preferredCurrencyUomId = (String) context.get("preferredCurrencyUomId");
		String roleTypeId = (String) context.get("roleTypeId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try{
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
			Map<String, Object> input = FastMap.newInstance();
			input.put("userLogin", userLogin);
			input.put("comments", content);
			input.put("description", description);
			input.put("partyTypeId", "PARTY_GROUP");
			input.put("preferredCurrencyUomId", preferredCurrencyUomId);
			input.put("groupName", groupName);
			input = dispatcher.runSync("createPartyGroup", input);
			String partyId = (String) input.get("partyId");
			input = FastMap.newInstance();
			input.put("userLogin", system);
			input.put("partyId", partyId);
			input.put("address", address);
			input.put("country", countryGeoId);
			input.put("province", stateProvinceGeoId);
			input.put("district", districtGeoId);
			input.put("ward", wardGeoId);
			dispatcher.runSync("createSupplierAddress", input);
			input = FastMap.newInstance();
			input.put("userLogin", system);
			input.put("partyId", partyId);
			input.put("email", email);
			dispatcher.runSync("createSupplierEmail", input);
			input = FastMap.newInstance();
			input.put("userLogin", system);
			input.put("partyId", partyId);
			input.put("phone", phone);
			dispatcher.runSync("createSupplierTelecom", input);
			input = FastMap.newInstance();
			input.put("userLogin", system);
			input.put("partyId", partyId);
			input.put("fax", fax);
			dispatcher.runSync("createSupplierFax", input);
			input = FastMap.newInstance();
			input.put("userLogin", system);
			input.put("partyId", partyId);
			input.put("roleTypeId", roleTypeId);
			dispatcher.runSync("createPartyRole", input);
		}catch(Exception e){
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierError", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierSuccess", locale));
    }
    
    /*create supplier email*/
    public static Map<String, Object> createSupplierEmail(DispatchContext dctx, Map<String, ? extends Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Locale locale = (Locale) context.get("locale");
    	String partyId = (String) context.get("partyId");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> res = ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierContactSuccess", locale));
    	try {
    		Map<String, Object> tmp = FastMap.newInstance();
			String email = (String) context.get("email");
			if(!UtilValidate.isEmpty(email)) {
				tmp = FastMap.newInstance();
				tmp.put("emailAddress", email);
				tmp.put("userLogin", userLogin);
				tmp = dispatcher.runSync("createEmailAddress", tmp);
				String ctm = (String) tmp.get("contactMechId");
				if(ServiceUtil.isSuccess(tmp)) {
					tmp = FastMap.newInstance();
					tmp.put("partyId", partyId);
					tmp.put("contactMechId", ctm);
					tmp.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
					tmp.put("userLogin", userLogin);
					tmp = dispatcher.runSync("createPartyContactMech", tmp);
					res.put("contactMechId", tmp.get("tmp"));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierContactError", locale));
		}
    	return res;
    }
    /*create supplier phone*/
    public static Map<String, Object> createSupplierTelecom(DispatchContext dctx, Map<String, ? extends Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Locale locale = (Locale) context.get("locale");
    	String partyId = (String) context.get("partyId");
    	String phone = (String) context.get("phone");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> res = ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierContactSuccess", locale));;
    	try {
			if(!UtilValidate.isEmpty(phone)) {
				Map<String, Object> tmp = FastMap.newInstance();
				Map<String, Object> createOtherPhoneCtx = FastMap.newInstance();
				createOtherPhoneCtx.put("contactNumber", phone);
				createOtherPhoneCtx.put("userLogin", userLogin);
				Map<String, Object> createOtherPhoneRs = dispatcher.runSync("createTelecomNumber", createOtherPhoneCtx);
				if(ServiceUtil.isSuccess(createOtherPhoneRs)) {
					Map<String, Object> createPartyContactCtx = FastMap.newInstance();
					createPartyContactCtx.put("partyId", partyId);
					createPartyContactCtx.put("contactMechId", createOtherPhoneRs.get("contactMechId"));
					createPartyContactCtx.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
					createPartyContactCtx.put("userLogin", userLogin);
					tmp = dispatcher.runSync("createPartyContactMech", createPartyContactCtx);
					res.put("contactMechId", tmp.get("contactMechId"));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierContactError", locale));
		}
    	return res;
    }
    public static Map<String, Object> createSupplierFax(DispatchContext dctx, Map<String, ? extends Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Locale locale = (Locale) context.get("locale");
    	String partyId = (String) context.get("partyId");
    	String fax = (String) context.get("fax");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> res = ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierContactSuccess", locale));;
    	try {
			if(!UtilValidate.isEmpty(fax)) {
				Map<String, Object> createOtherPhoneCtx = FastMap.newInstance();
				createOtherPhoneCtx.put("contactNumber", fax);
				createOtherPhoneCtx.put("userLogin", userLogin);
				Map<String, Object> createOtherPhoneRs = dispatcher.runSync("createTelecomNumber", createOtherPhoneCtx);
				if(ServiceUtil.isSuccess(createOtherPhoneRs)) {
					Map<String, Object> createPartyContactCtx = FastMap.newInstance();
					createPartyContactCtx.put("partyId", partyId);
					createPartyContactCtx.put("contactMechId", createOtherPhoneRs.get("contactMechId"));
					createPartyContactCtx.put("contactMechPurposeTypeId", "FAX_NUMBER");
					createPartyContactCtx.put("userLogin", userLogin);
					Map<String, Object> tmp = dispatcher.runSync("createPartyContactMech", createPartyContactCtx);
					res.put("contactMechId", tmp.get("contactMechId"));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierContactError", locale));
		}
    	return res;
    }
    public static Map<String, Object> createSupplierAddress(DispatchContext dctx, Map<String, ? extends Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Locale locale = (Locale) context.get("locale");
    	String partyId = (String) context.get("partyId");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> res = ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierAddressSuccess", locale));
    	try {
    		//Create PostalAddress(purpose: 'PERMANENT_RESIDENCE')
    		Map<String, Object> createPrPostalAddressCtx = FastMap.newInstance();
    		String prAddress1 = (String) context.get("address");
    		String prCountryGeoId = (String) context.get("country");
    		String prStateProvinceGeoId = (String) context.get("province");
    		String prDistrictGeoId = (String)context.get("district");
    		String prWardGeoId = (String)context.get("ward");
    		
    		createPrPostalAddressCtx.put("address1", prAddress1);
    		createPrPostalAddressCtx.put("countryGeoId", prCountryGeoId);
    		createPrPostalAddressCtx.put("stateProvinceGeoId", prStateProvinceGeoId);
    		createPrPostalAddressCtx.put("districtGeoId", prDistrictGeoId);
    		createPrPostalAddressCtx.put("wardGeoId", prWardGeoId);
    		createPrPostalAddressCtx.put("userLogin", userLogin);
    		Map<String, Object> createPrCtRs = dispatcher.runSync("createContactMechGeo", createPrPostalAddressCtx);
    		if(ServiceUtil.isSuccess(createPrCtRs)) {
    			Map<String, Object> createPartyContactCtx = FastMap.newInstance();
    			createPartyContactCtx.put("partyId", partyId);
    			createPartyContactCtx.put("contactMechId", createPrCtRs.get("contactMechId"));
    			createPartyContactCtx.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
    			createPartyContactCtx.put("userLogin", userLogin);
    			Map<String, Object> tmp = dispatcher.runSync("createPartyContactMech", createPartyContactCtx);
    			res.put("contactMechId", tmp.get("contactMechId"));
    		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierAddressError", locale));
		}
    	return res;
    }
    public static Map<String, Object> updateSupplier(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		
		String phone = (String) context.get("phone");
		String fax = (String) context.get("fax");
		String email = (String) context.get("email");
		String description = (String) context.get("description");
		String content = (String) context.get("comments");
		String groupName = (String) context.get("groupName");
		String partyId = (String) context.get("partyId");
		String statusId = (String) context.get("statusId");
		String preferredCurrencyUomId = (String) context.get("preferredCurrencyUomId");
		try{
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
			Map<String, Object> input = FastMap.newInstance();
			input.put("userLogin", system);
			input.put("partyId", partyId);
			input.put("statusId", statusId);
			input.put("comments", content);
			input.put("description", description);
			input.put("partyTypeId", "PARTY_GROUP");
			input.put("preferredCurrencyUomId", preferredCurrencyUomId);
			input.put("groupName", groupName);
			input = dispatcher.runSync("updatePartyGroup", input);
			input = FastMap.newInstance();
			input.put("userLogin", system);
			input.put("partyId", partyId);
			input.put("email", email);
			dispatcher.runSync("updateSupplierEmail", input);
			input = FastMap.newInstance();
			input.put("userLogin", system);
			input.put("partyId", partyId);
			input.put("phone", phone);
			dispatcher.runSync("updateSupplierTelecom", input);
			input = FastMap.newInstance();
			input.put("userLogin", system);
			input.put("partyId", partyId);
			input.put("fax", fax);
			dispatcher.runSync("updateSupplierFax", input);
		}catch(Exception e){
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierError", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierSuccess", locale));
    }
    public static Map<String, Object> updateSupplierEmail(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String partyId = (String) context.get("partyId");
		String email = (String) context.get("email");
		try{
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
			Map<String, Object> input = FastMap.newInstance();
			if(!UtilValidate.isEmpty(email)){
				input = FastMap.newInstance();
				input.put("userLogin", system);
				input.put("partyId", partyId);
				input.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
				input = dispatcher.runSync("getPartyEmail", input);
				String old = (String) input.get("emailAddress");
				if(!old.equals(email)){
					input = FastMap.newInstance();
					Timestamp nowtimestamp = UtilDateTime.nowTimestamp();
					List<EntityCondition> cond = FastList.newInstance();
					cond.add(EntityCondition.makeCondition("partyId", partyId));
					cond.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "EMAIL_ADDRESS"));
					cond.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(nowtimestamp)));
					List<GenericValue> lt = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(cond, EntityOperator.AND), null, null, null, false);
					for(GenericValue e : lt){
						input = FastMap.newInstance();
						input.put("userLogin", system);
						input.put("partyId", partyId);
						input.put("contactMechId", e.getString("contactMechId"));
						input.put("contactMechPurposeTypeId", e.getString("contactMechPurposeTypeId"));
						input.put("fromDate", e.get("fromDate"));
						dispatcher.runSync("deletePartyContactMechPurpose", input);
					}
					input.put("userLogin", system);
					input.put("partyId", partyId);
					input.put("email", email);
					dispatcher.runSync("createSupplierEmail", input);
				}	
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierEmailError", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "updateSupplierEmailSuccess", locale));
    }
    public static Map<String, Object> updateSupplierTelecom(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String partyId = (String) context.get("partyId");
		String phone = (String) context.get("phone");
		try{
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
			if(!UtilValidate.isEmpty(phone)){
				Map<String, Object> input = FastMap.newInstance();
				input.put("userLogin", system);
				input.put("partyId", partyId);
				input.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
				input = dispatcher.runSync("getPartyTelephone", input);
				String oldphone = (String) input.get("contactNumber");
				if(!oldphone.equals(phone)){
					Timestamp nowtimestamp = UtilDateTime.nowTimestamp();
					List<EntityCondition> cond = FastList.newInstance();
					cond.add(EntityCondition.makeCondition("partyId", partyId));
					cond.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_PHONE"));
					cond.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(nowtimestamp)));
					List<GenericValue> lt = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(cond, EntityOperator.AND), null, null, null, false);
					for(GenericValue e : lt){
						input = FastMap.newInstance();
						input.put("userLogin", system);
						input.put("partyId", partyId);
						input.put("contactMechId", e.getString("contactMechId"));
						input.put("contactMechPurposeTypeId", e.getString("contactMechPurposeTypeId"));
						input.put("fromDate", e.get("fromDate"));
						dispatcher.runSync("deletePartyContactMechPurpose", input);
					}
					input = FastMap.newInstance();
					input.put("userLogin", system);
					input.put("partyId", partyId);
					input.put("phone", phone);
					dispatcher.runSync("createSupplierTelecom", input);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierTelecomError", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "updateSupplierTelecomSuccess", locale));
    }
    public static Map<String, Object> updateSupplierFax(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String partyId = (String) context.get("partyId");
		String fax = (String) context.get("fax");
		try{
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
			if(!UtilValidate.isEmpty(fax)){
				Map<String, Object> tmp = FastMap.newInstance();
				tmp.put("userLogin", system);
				tmp.put("partyId", partyId);
				tmp.put("contactMechPurposeTypeId", "FAX_NUMBER");
				tmp = dispatcher.runSync("getPartyTelephone", tmp);
				String oldphone = (String) tmp.get("contactNumber");
				if(!oldphone.equals(fax)){
					Map<String, Object> input = FastMap.newInstance();
					Timestamp nowtimestamp = UtilDateTime.nowTimestamp();
					List<EntityCondition> cond = FastList.newInstance();
					cond.add(EntityCondition.makeCondition("partyId", partyId));
					cond.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "FAX_NUMBER"));
					cond.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(nowtimestamp)));
					List<GenericValue> lt = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(cond, EntityOperator.AND), null, null, null, false);
					for(GenericValue e : lt){
						input = FastMap.newInstance();
						input.put("userLogin", system);
						input.put("partyId", partyId);
						input.put("contactMechId", e.getString("contactMechId"));
						input.put("contactMechPurposeTypeId", e.getString("contactMechPurposeTypeId"));
						input.put("fromDate", e.get("fromDate"));
						dispatcher.runSync("deletePartyContactMechPurpose", input);
					}
					input = FastMap.newInstance();
					input.put("userLogin", system);
					input.put("partyId", partyId);
					input.put("fax", fax);
					dispatcher.runSync("createSupplierFax", input);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierTelecomError", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "updateSupplierTelecomSuccess", locale));
    }
    public static Map<String, Object> updateSupplierAddress(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String partyId = (String) context.get("partyId");
		String address = (String) context.get("address");
		String countryGeoId = (String) context.get("country");
		String stateProvinceGeoId = (String) context.get("province");
		String districtGeoId = (String) context.get("district");
		String wardGeoId = (String) context.get("ward");
		try{
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
			Map<String, Object> tmp = FastMap.newInstance();
			tmp.put("userLogin", system);
			tmp.put("partyId", partyId);
			tmp.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
			tmp = dispatcher.runSync("getPartyPostalAddress", tmp);
			String addr = (String) tmp.get("address1");
			String oldCountry = (String) tmp.get("countryGeoId");
			String oldState = (String) tmp.get("stateProvinceGeoId");
			GenericValue e = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", tmp.get("contactMechId")), false);
			String oldDis = e.getString("districtGeoId");
			String oldWard = e.getString("wardGeoId");
			if(!addr.equals(address) || !oldCountry.equals(countryGeoId) 
					|| !oldState.equals(stateProvinceGeoId) || !oldDis.equals(districtGeoId)
					|| !oldWard.equals(wardGeoId)){
				List<EntityCondition> cond = FastList.newInstance();
				cond.add(EntityCondition.makeCondition("partyId", partyId));
				cond.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
				Timestamp nowtimestamp = UtilDateTime.nowTimestamp();
				cond.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(nowtimestamp)));
				List<GenericValue> lt = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(cond, EntityOperator.AND), null, null, null, false);
				Map<String, Object> input = FastMap.newInstance();
				for(GenericValue em : lt){
					input = FastMap.newInstance();
					input.put("userLogin", system);
					input.put("partyId", partyId);
					input.put("contactMechId", em.getString("contactMechId"));
					input.put("contactMechPurposeTypeId", em.getString("contactMechPurposeTypeId"));
					input.put("fromDate", em.get("fromDate"));
					dispatcher.runSync("deletePartyContactMechPurpose", input);
				}
				input = FastMap.newInstance();
				input.put("userLogin", system);
				input.put("partyId", partyId);
				input.put("address", address);
				input.put("country", countryGeoId);
				input.put("province", stateProvinceGeoId);
				input.put("district", districtGeoId);
				input.put("ward", wardGeoId);
				dispatcher.runSync("createSupplierAddress", input);
			}
		}catch(Exception e){
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createSupplierTelecomError", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "updateSupplierTelecomSuccess", locale));
    }
}
