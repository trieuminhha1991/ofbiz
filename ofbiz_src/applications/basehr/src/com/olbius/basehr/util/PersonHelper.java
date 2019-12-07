package com.olbius.basehr.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.olbius.basehr.util.MultiOrganizationUtil;

public class PersonHelper{
	public static final String module = PersonHelper.class.getName();
	
	public static String getNationality(String partyId, Delegator delegator) {
		String nationality = "";
		try {
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			if(!UtilValidate.isEmpty(person.getString("nationality"))) {
				GenericValue genNationality = delegator.findOne("Nationality", UtilMisc.toMap("nationalityId", person.getString("nationality")), false);
				nationality = genNationality.getString("description");
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}
		return nationality;
	}
	
	public static String getBirthDate(String partyId, Delegator delegator) {
		String birthDateStr = "";
		try {
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			if(!UtilValidate.isEmpty(person.getString("birthDate"))) {
				Date birthDate = person.getDate("birthDate");
				Calendar cal = Calendar.getInstance();
				cal.setTime(birthDate);
				birthDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}
		return birthDateStr;
	}
	
	public static String getIDNumber(String partyId, Delegator delegator) {
		String idNumber = "";
		try {
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			if(!UtilValidate.isEmpty(person.getString("idNumber"))) {
				idNumber = person.getString("idNumber");
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}
		return idNumber;
	}
	
	public static String getIDIssueDate(String partyId, Delegator delegator) {
		String issueDateStr = "";
		try {
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			if(!UtilValidate.isEmpty(person.getString("idIssueDate"))) {
				Date birthDate = person.getDate("idIssueDate");
				Calendar cal = Calendar.getInstance();
				cal.setTime(birthDate);
				issueDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}
		return issueDateStr;
	}
	
	public static String getIDIssuePlace(String partyId, Delegator delegator) {
		String issuePlaceStr = "";
		try {
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			if(!UtilValidate.isEmpty(person.getString("idIssuePlace"))) {
				issuePlaceStr = person.getString("idIssuePlace");
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}
		return issuePlaceStr;
	}
	
	public static Timestamp getDateEmplJoinOrg(Delegator delegator, String partyId) throws GenericEntityException{
		Map<String, String> mapConds = FastMap.newInstance();
		mapConds.put("partyIdTo", partyId);
		//mapConds.put("partyIdFrom", MultiOrganizationUtil.getCurrentOrganization(delegator));
		mapConds.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
		//mapConds.put("roleTypeIdTo", "EMPLOYEE");
		List<GenericValue> employmentList = delegator.findByAnd("Employment", mapConds, UtilMisc.toList("fromDate"), false);
		if(UtilValidate.isNotEmpty(employmentList)){
			Timestamp fromDate = employmentList.get(0).getTimestamp("fromDate");
			return fromDate; 
		}
		return null;
	}
	
	public static Timestamp getDateEmplLeaveOrg(Delegator delegator, String partyId) throws GenericEntityException{
		Map<String, String> mapConds = FastMap.newInstance();
		mapConds.put("partyIdTo", partyId);
		mapConds.put("partyIdFrom", MultiOrganizationUtil.getCurrentOrganization(delegator));
		mapConds.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
		mapConds.put("roleTypeIdTo", "EMPLOYEE");
		List<GenericValue> employmentList = delegator.findByAnd("Employment", mapConds, UtilMisc.toList("-fromDate"), false);
		if(UtilValidate.isNotEmpty(employmentList)){
			return employmentList.get(0).getTimestamp("thruDate");
		}
		return null;
	}

	public static String getPersonFamilyRelationship(Delegator delegator, String partyId, String partyRelationshipTypeId) throws GenericEntityException{
		List<GenericValue> familyBackground = delegator.findByAnd("PersonFamilyBackground", 
				UtilMisc.toMap("partyId", partyId, "partyRelationshipTypeId", partyRelationshipTypeId), null, false);
		if(UtilValidate.isNotEmpty(familyBackground)){
			String familyPersonId = familyBackground.get(0).getString("partyFamilyId");
			return familyPersonId;
		}
		return null;
	}
}
