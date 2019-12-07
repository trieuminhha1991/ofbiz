/*******************************************************************************
 * Licensed to the Ache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Ache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.ache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/

package com.olbius.crm;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;

import com.meterware.pseudoserver.HttpRequest;
import com.olbius.recruitment.helper.ApplicantServiceHelper;
import com.olbius.services.JqxWidgetSevices;

public class Contact {
	public static final String module = Contact.class.getName();
	public static final String resourceError = "MarketingUiLabels";
	public static final String resourceErrorOl = "DelysMarketingUiLabels";

	//get customer person
	public static Map<String, Object> getListContacts(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher(); 
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
		int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
	    int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
	    int start = pagesize * pageNum;
	    int end = start + pagesize;
		try {
			Set<String> fields = FastSet.newInstance();
			fields.add("partyId");
			fields.add("firstName");
			fields.add("lastName");
			fields.add("middleName");
			fields.add("birthDate");
			fields.add("contactNumber");
			opts.setDistinct(true);
			listSortFields.add("partyId ASC");
			List<EntityCondition> roletype = FastList.newInstance();
			roletype.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PHONE_MOBILE"));
			roletype.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
			roletype.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "END_USER_CUSTOMER"));
			EntityCondition tmprole = EntityCondition.makeCondition(
					roletype, EntityJoinOperator.OR);
			listAllConditions.add(tmprole);
			listAllConditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
			EntityCondition tmpConditon = EntityCondition.makeCondition(
					listAllConditions, EntityJoinOperator.AND);
			EntityListIterator tmpcontacts = delegator.find("PersonAndContactMechDetail", tmpConditon, null, fields, null, opts);
			List<GenericValue> contacts = tmpcontacts.getPartialList(start, end);
			List<Map<String, Object>> res = FastList.newInstance();
			Map<String,Object> tmp = FastMap.newInstance();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String tmp2 = "";
			for(GenericValue contact : contacts){
				Map<String,Object> o = FastMap.newInstance();
				tmp2 = contact.getString("partyId"); 
				o.put("partyId", tmp2);
				o.put("firstName", contact.getString("firstName"));
				o.put("lastName", contact.getString("lastName"));
				o.put("middleName", contact.getString("middleName"));
				o.put("birthDate", contact.getString("birthDate"));
				o.put("contactNumber", contact.getString("contactNumber"));
				tmp = FastMap.newInstance();
//				/*get other phone*/
//				tmp.put("userLogin", userLogin);
//				tmp.put("partyId", tmp2);
//				tmp.put("contactMechPurposeTypeId", "OTHER_PHONE");
//				tmp = dispatcher.runSync("getPartyTelephone", tmp);
//				o.put("otherPhone", tmp);
//				/*get phone home*/
//				tmp = FastMap.newInstance();
//				tmp.put("userLogin", userLogin);
//				tmp.put("partyId", tmp2);
//				tmp.put("contactMechPurposeTypeId", "PHONE_HOME");
//				tmp = dispatcher.runSync("getPartyTelephone", tmp);
//				o.put("phoneHome", tmp);
//				/*get phone mobile*/
//				tmp = FastMap.newInstance();
//				tmp.put("userLogin", userLogin);
//				tmp.put("partyId", tmp2);
//				tmp.put("contactMechPurposeTypeId", "PHONE_MOBILE");
//				tmp = dispatcher.runSync("getPartyTelephone", tmp);
//				o.put("phoneMobile", tmp);
				/*get email*/
				tmp = FastMap.newInstance();
				tmp.put("userLogin", userLogin);
				tmp.put("partyId", tmp2);
				tmp.put("contactMechPurposeTypeId", "PERSONAL_EMAIL");
				tmp = dispatcher.runSync("getPartyEmail", tmp);
				o.put("email",(String) tmp.get("emailAddress"));
				/*get permanent residence*/
				tmp = FastMap.newInstance();
				tmp.put("userLogin", userLogin);
				tmp.put("partyId", tmp2);
				tmp.put("contactMechPurposeTypeId", "PERMANENT_RESIDENCE");
				tmp = dispatcher.runSync("getPartyPostalAddress", tmp);
				o.put("permanentResidence",tmp);
				/*get current residence*/
				tmp = FastMap.newInstance();
				tmp.put("userLogin", userLogin);
				tmp.put("partyId", tmp2);
				tmp.put("contactMechPurposeTypeId", "CURRENT_RESIDENCE");
				tmp = dispatcher.runSync("getPartyPostalAddress", tmp);
				o.put("currentResidence",tmp);
				res.add(o);
			}
			successResult.put("listIterator", res);
			successResult.put("TotalRows", String.valueOf(tmpcontacts.getCompleteList().size()));
			tmpcontacts.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return successResult;
	}
	

	public static List<Map<String, Object>> distinctContact(List<GenericValue> input) {
		List<Map<String, Object>> contacts = FastList.newInstance();
		String currentPartyId = "";
		Map<String, Object> temp = FastMap.newInstance();
		for (GenericValue e : input) {
			String partyId = e.getString("partyId");
			if (currentPartyId == "" || !currentPartyId.equals(partyId)) {
				if (currentPartyId != "" && currentPartyId != partyId) {
					contacts.add(temp);
				}
				currentPartyId = partyId;
				GenericValue cur = (GenericValue) e.clone();
				Map<String, Object> tmpCt = cleanContact(cur);
				temp = tmpCt;
			} else if (currentPartyId.equals(partyId)) {
				setContact(temp, e);
			}
		}
		contacts.add(temp);
		return contacts;
	}

	public static Map<String, Object> cleanContact(GenericValue cur) {
		Map<String, Object> tmpCt = FastMap.newInstance();
		String partyId = cur.getString("partyId");
		String firstname = cur.getString("firstName");
		String middlename = cur.getString("middleName");
		String lastname = cur.getString("lastName");
		String groupname = cur.getString("groupName");
		String email = cur.getString("infoString");
		String pa = cur.getString("city");
		String ct = cur.getString("address1");
		String phone = cur.getString("contactNumber");
		String birth = cur.getString("birthDate");
		List<String> eli = new ArrayList<String>();
		List<String> pali = new ArrayList<String>();
		List<String> ctli = new ArrayList<String>();
		List<String> phli = new ArrayList<String>();
		if (email != null && email != "") {
			eli.add(email);
		}
		if (pa != null && pa != "") {
			pali.add(pa);
		}
		if (ct != null && ct != "") {
			ctli.add(ct);
		}
		if (phone != null && phone != "") {
			phli.add(phone);
		}
		if (firstname != null && firstname != "") {
			tmpCt.put("firstName", firstname);
		}
		if (middlename != null && middlename != "") {
			tmpCt.put("middleName", middlename);
		}
		if (lastname != null && lastname != "") {
			tmpCt.put("lastName", lastname);
		}
		if (groupname != null && groupname != "") {
			tmpCt.put("groupName", groupname);
		}

		tmpCt.put("partyId", partyId);
		tmpCt.put("birthDate", birth);
		tmpCt.put("infoString", eli);
		tmpCt.put("city", pali);
		tmpCt.put("address1", ctli);
		tmpCt.put("contactNumber", phli);
		return tmpCt;
	}

	public static void setContact(Map<String, Object> temp, GenericValue e) {
		String email = e.getString("infoString");
		String pa = e.getString("city");
		String ct = e.getString("address1");
		String phone = e.getString("contactNumber");
		List<String> eli = (ArrayList<String>) temp.get("infoString");
		List<String> pali = (ArrayList<String>) temp.get("city");
		List<String> ctli = (ArrayList<String>) temp.get("address1");
		List<String> phli = (ArrayList<String>) temp.get("contactNumber");
		if (email != null && email != "") {
			eli.add(email);
		}
		if (pa != null && pa != "") {
			pali.add(pa);

		}
		if (ct != null && ct != "") {
			ctli.add(ct);
		}
		if (phone != null && phone != "") {
			phli.add(phone);
		}
		temp.put("infoString", eli);
		temp.put("city", pali);
		temp.put("address1", ctli);
		temp.put("contactNumber", phli);
	}
	//get customer group delys
	public static Map<String, Object> getListGroupContacts(
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
			listSortFields.add("partyIdFrom ASC");
			listAllConditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE"));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "DELYS_CUSTOMER_GT"));
			listAllConditions.add(EntityCondition.makeCondition("statusIdFrom", EntityOperator.EQUALS, "PARTY_ENABLED"));
			listAllConditions.add(EntityCondition.makeCondition("statusIdTo", EntityOperator.EQUALS, "PARTY_ENABLED"));
			EntityListIterator listIter = delegator.find("PartyOwnerAndContactMechDetail", EntityCondition.makeCondition(listAllConditions), null, null, null, opts);
			List<GenericValue> contacts = listIter.getPartialList(start, end);
			List<Map<String, Object>> res = FastList.newInstance();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			StringBuilder tmp2 = new StringBuilder();
			Map<String,Object> tmp = FastMap.newInstance();
			Map<String, Object> o = FastMap.newInstance();
			for(GenericValue contact : contacts){
				o = FastMap.newInstance();
				tmp2 = new StringBuilder(contact.getString("partyIdTo"));
				o.put("firstName", contact.getString("firstName"));
				o.put("lastName", contact.getString("lastName"));
				o.put("middleName", contact.getString("middleName"));
				o.put("groupName", contact.getString("groupName"));
				o.put("partyIdTo", tmp2.toString());
				o.put("partyIdFrom", contact.getString("partyIdFrom"));
				o.put("contactNumber", contact.getString("contactNumber"));
				/*get current residence*/
				tmp = FastMap.newInstance();
				tmp.put("userLogin", userLogin);
				tmp.put("partyId", tmp2.toString());
				tmp.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				tmp = dispatcher.runSync("getPartyPostalAddress", tmp);
				o.put("address",tmp);
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
	public static Map<String, Object> getListCustomerMT(
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
			listSortFields.add("partyId ASC");
			listAllConditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE"));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER_MT"));
			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));
			EntityListIterator listIter = delegator.find("PartyGroupAndContactMechDetail", EntityCondition.makeCondition(listAllConditions), null, null, null, opts);
			List<GenericValue> contacts = listIter.getPartialList(start, end);
			List<Map<String, Object>> res = FastList.newInstance();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			StringBuilder tmp2 = new StringBuilder();
			Map<String,Object> tmp = FastMap.newInstance();
			Map<String, Object> o = FastMap.newInstance();
			for(GenericValue contact : contacts){
				o = FastMap.newInstance();
				tmp2 = new StringBuilder(contact.getString("partyId"));
				o.put("groupName", contact.getString("groupName"));
				o.put("partyId", contact.getString("partyId"));
				o.put("contactNumber", contact.getString("contactNumber"));
				/*get current residence*/
				tmp = FastMap.newInstance();
				tmp.put("userLogin", userLogin);
				tmp.put("partyId", tmp2.toString());
				tmp.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				tmp = dispatcher.runSync("getPartyPostalAddress", tmp);
				o.put("address",tmp);
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
	//group
	public static List<Map<String, Object>> distinctContactGroup(List<GenericValue> input) {
		List<Map<String, Object>> contacts = FastList.newInstance();
		String currentPartyId = "";
		Map<String, Object> temp = FastMap.newInstance();
		for (GenericValue e : input) {
			String partyId = e.getString("partyIdFrom");
			if (currentPartyId == "" || !currentPartyId.equals(partyId)) {
				if (currentPartyId != "" && currentPartyId != partyId) {
					contacts.add(temp);
				}
				currentPartyId = partyId;
				GenericValue cur = (GenericValue) e.clone();
				Map<String, Object> tmpCt = cleanContactGroup(cur);
				temp = tmpCt;
			} else if (currentPartyId.equals(partyId)) {
				setContact(temp, e);
			}
		}
		contacts.add(temp);
		return contacts;
	}

	public static Map<String, Object> cleanContactGroup(GenericValue cur) {
		Map<String, Object> tmpCt = FastMap.newInstance();
		String partyIdFrom = cur.getString("partyIdFrom");
		String partyIdTo = cur.getString("partyIdTo");
		String firstname = cur.getString("firstName");
		String middlename = cur.getString("middleName");
		String lastname = cur.getString("lastName");
		String groupname = cur.getString("groupName");
		String email = cur.getString("infoString");
		String pa = cur.getString("city");
		String ct = cur.getString("address1");
		String phone = cur.getString("contactNumber");
		String birth = cur.getString("birthDate");
		List<String> eli = new ArrayList<String>();
		List<String> pali = new ArrayList<String>();
		List<String> ctli = new ArrayList<String>();
		List<String> phli = new ArrayList<String>();
		if (email != null && email != "") {
			eli.add(email);
		}
		if (pa != null && pa != "") {
			pali.add(pa);
		}
		if (ct != null && ct != "") {
			ctli.add(ct);
		}
		if (phone != null && phone != "") {
			phli.add(phone);
		}
		tmpCt.put("firstName", firstname);
		tmpCt.put("middleName", middlename);
		tmpCt.put("lastName", lastname);
		tmpCt.put("groupName", groupname);
		tmpCt.put("partyIdFrom", partyIdFrom);
		tmpCt.put("partyIdTo", partyIdTo);
		tmpCt.put("birthDate", birth);
		tmpCt.put("infoString", eli);
		tmpCt.put("city", pali);
		tmpCt.put("address1", ctli);
		tmpCt.put("contactNumber", phli);
		return tmpCt;
	}
	public static Map<String, Object> createCustomerCrm(DispatchContext ctx, Map<String, Object> context)
			throws ParseException {
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Date birthDate = (Date)JqxWidgetSevices.convert("java.sql.Date", (String)context.get("birthDate"));
		String lastName = (String)context.get("lastName");
		String middleName = (String)context.get("middleName");
		String firstName = (String)context.get("firstName");
		String gender = (String)context.get("gender");
		String birthPlace = (String)context.get("birthPlace");
		String height = (String)context.get("height");
		String weight = (String)context.get("weight");
		String idNumber = (String)context.get("idNumber");
		String idIssuePlace = (String)context.get("idIssuePlace");
		Date idIssueDate = (Date)JqxWidgetSevices.convert("java.sql.Date", (String)context.get("idIssueDate"));
		String maritalStatus = (String)context.get("maritalStatus");
		String numberChildren = (String)context.get("numberChildren");
		String ethnicOrigin = (String)context.get("ethnicOrigin");
		String religion = (String)context.get("religion");
		String nativeLand = (String)context.get("nativeLand");
//		String homeTel = (String)context.get("homeTel");
		String mobile = (String)context.get("mobile");
//		String diffTel = (String)context.get("diffTel");
		String email = (String)context.get("email");
		String prAddress = (String)context.get("prAddress");
		String prCountry = (String)context.get("prCountry");
		String prProvince = (String)context.get("prProvince");
		String prDistrict = (String)context.get("prDistrict");
		String prWard = (String)context.get("prWard");
		String crCountry = (String)context.get("crCountry");
		String crAddress = (String)context.get("crAddress");
		String crProvince = (String)context.get("crProvince");
		String crDistrict = (String)context.get("crDistrict");
		String crWard = (String)context.get("crWard");
		
		try{
			/*create party*/
			if(firstName == null && middleName == null && lastName == null){
				firstName = "Guest";
				lastName = "Customer";
			}
			Map<String, Object> createApplicantCtx = FastMap.newInstance();
			createApplicantCtx.put("birthDate", birthDate);
			createApplicantCtx.put("lastName", lastName);
			createApplicantCtx.put("middleName", middleName);
			createApplicantCtx.put("firstName", firstName);
			createApplicantCtx.put("gender", gender);
			createApplicantCtx.put("birthPlace", birthPlace);
			createApplicantCtx.put("height", height);
			createApplicantCtx.put("weight", weight);
			createApplicantCtx.put("idNumber", idNumber);
			createApplicantCtx.put("idIssuePlace", idIssuePlace);
			createApplicantCtx.put("idIssueDate", idIssueDate);
			createApplicantCtx.put("maritalStatus", maritalStatus);
			createApplicantCtx.put("numberChildren", numberChildren);
			createApplicantCtx.put("ethnicOrigin", ethnicOrigin);
			createApplicantCtx.put("religion", religion);
			createApplicantCtx.put("nativeLand", nativeLand);
			createApplicantCtx.put("userLogin", userLogin);
			Map<String, Object> createPersonRs = dispatcher.runSync("createPerson", ServiceUtil.setServiceFields(dispatcher, "createPerson", context, userLogin, null, null));
			/*create party role*/
			Map<String, Object> createPartyRoleCtx = FastMap.newInstance();
			String partyId = (String) createPersonRs.get("partyId");
			createPartyRoleCtx.put("partyId", partyId);
			createPartyRoleCtx.put("roleTypeId", "END_USER_CUSTOMER");
			createPartyRoleCtx.put("userLogin", userLogin);
			dispatcher.runSync("createPartyRole", createPartyRoleCtx);
			/*create contact mech*/
			Map<String, Object> createPartyContactCtx = FastMap.newInstance();
//			createPartyContactCtx.put("homeTel", homeTel);
			createPartyContactCtx.put("mobile", mobile);
//			createPartyContactCtx.put("diffTel", diffTel);
			createPartyContactCtx.put("email", email);
			createPartyContactCtx.put("prAddress", prAddress);
			createPartyContactCtx.put("prCountry", prCountry);
			createPartyContactCtx.put("prProvince", prProvince);
			createPartyContactCtx.put("prDistrict", prDistrict);
			createPartyContactCtx.put("prWard", prWard);
			createPartyContactCtx.put("crCountry", crCountry);
			createPartyContactCtx.put("crProvince", crProvince);
			createPartyContactCtx.put("crDistrict", crDistrict);
			createPartyContactCtx.put("crWard", crWard);
			createPartyContactCtx.put("userLogin", userLogin);
			createPartyContactCtx.put("partyId", createPersonRs.get("partyId"));
			createPartyContactCtx.put("crAddress", crAddress);
			ApplicantServiceHelper.createPartyContact(dispatcher, createPartyContactCtx, true);
		}catch(Exception e){
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysMarketingUiLabels", "createCustomerError", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysMarketingUiLabels", "createCustomerSuccess", locale));
	}
	public static Map<String, Object> deleteCustomerCrm(DispatchContext ctx, Map<String, Object> context)
			throws ParseException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try{
			/*create party*/
			String partyId = (String) context.get("partyId");
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			if(party != null){
				party.set("statusId", "PARTY_DISABLED");
				party.store();
			}
		}catch(Exception e){
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysMarketingUiLabels", "deleteCustomerError", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysMarketingUiLabels", "deleteCustomerSuccess", locale));
	}
}
