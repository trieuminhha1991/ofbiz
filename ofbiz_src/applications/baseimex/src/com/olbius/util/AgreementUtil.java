package com.olbius.util;

import java.util.List;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;


import javolution.util.FastList;

public class AgreementUtil {
	
	public static final String module = AgreementUtil.class.getName();
	public static List<String> getAgreementTermTextValue(Delegator delegator, String agreementId, String termTypeId) throws GenericEntityException{
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("agreementId", agreementId));
		conds.add(EntityCondition.makeCondition("termTypeId", termTypeId));
		
		List<GenericValue> listAgreementTerm = FastList.newInstance();
		try {
			listAgreementTerm = delegator.findList("AgreementTerm", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList AgreementTerm: " + e.toString();
			Debug.logError(e, errMsg, module);
			return FastList.newInstance();
		}
		List<String> values = EntityUtil.getFieldListFromEntityList(listAgreementTerm, "textValue", true);
		return values;
	}
	
	public static String getAgreementPartyAddress(Delegator delegator, String agreementId, String partyId, String contactMechPurposeTypeId) throws GenericEntityException{
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("agreementId", agreementId));
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId));
		
		List<GenericValue> listTmps = FastList.newInstance();
		try {
			listTmps = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList AgreementPartyCTMPurpose: " + e.toString();
			Debug.logError(e, errMsg, module);
			return "";
		}
		String address = null;
		if (!listTmps.isEmpty()){
			listTmps = EntityUtil.filterByDate(listTmps);
			List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(listTmps, "contactMechId", true);
			if (!contactMechIds.isEmpty()){
				GenericValue add = null;
				try {
					add = delegator.findOne("PostalAddressFullNameDetail", false, UtilMisc.toMap("contactMechId", contactMechIds.get(0)));
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findOne PostalAddressFullNameDetail: " + e.toString();
					Debug.logError(e, errMsg, module);
					return "";
				}
				if (UtilValidate.isNotEmpty(add)) {
					address = add.getString("fullName");
				}
			}
		}
		return address;
	}
	
	public static String getAgreementPartyPhone(Delegator delegator, String agreementId, String partyId, String contactMechPurposeTypeId) throws GenericEntityException{
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("agreementId", agreementId));
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId));
		
		List<GenericValue> listTmps = FastList.newInstance();
		try {
			listTmps = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList AgreementPartyCTMPurpose: " + e.toString();
			Debug.logError(e, errMsg, module);
			return "";
		}
		String address = null;
		if (!listTmps.isEmpty()){
			listTmps = EntityUtil.filterByDate(listTmps);
			List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(listTmps, "contactMechId", true);
			if (!contactMechIds.isEmpty()){
				GenericValue add = null;
				try {
					add = delegator.findOne("TelecomNumber", false, UtilMisc.toMap("contactMechId", contactMechIds.get(0)));
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findOne TelecomNumber: " + e.toString();
					Debug.logError(e, errMsg, module);
					return "";
				}
				if (UtilValidate.isNotEmpty(add)) {
					address = add.getString("contactNumber");
				}
			}
		}
		return address;
	}
	
	public static String getAgreementPartyGeoAddress(Delegator delegator, String agreementId, String partyId, String contactMechPurposeTypeId, String geoTypeId) throws GenericEntityException{
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("agreementId", agreementId));
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId));
		
		List<GenericValue> listTmps = FastList.newInstance();
		try {
			listTmps = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList AgreementPartyCTMPurpose: " + e.toString();
			Debug.logError(e, errMsg, module);
			return "";
		}
		String address = null;
		if (!listTmps.isEmpty()){
			listTmps = EntityUtil.filterByDate(listTmps);
			List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(listTmps, "contactMechId", true);
			if (!contactMechIds.isEmpty()){
				GenericValue add = null;
				try {
					add = delegator.findOne("PostalAddressFullNameDetail", false, UtilMisc.toMap("contactMechId", contactMechIds.get(0)));
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findOne PostalAddressFullNameDetail: " + e.toString();
					Debug.logError(e, errMsg, module);
					return "";
				}
				if (UtilValidate.isNotEmpty(add)) {
					if ("GEOCOUTRY".equals(geoTypeId)){
						address = add.getString("countryGeoName");
					} else if ("GEOPROVINCE".equals(geoTypeId)){
						address = add.getString("stateProvinceGeoName");
					} else if ("GEODISTRICT".equals(geoTypeId)){
						address = add.getString("districtGeoName");
					} else if ("GEOWARD".equals(geoTypeId)){
						address = add.getString("wardGeoName");
					}
				}
			}
		}
		return address;
	}
}
