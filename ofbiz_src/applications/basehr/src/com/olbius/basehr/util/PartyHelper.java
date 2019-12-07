package com.olbius.basehr.util;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

public class PartyHelper {
	
	public static final String module = PartyHelper.class.getName();
	
	private PartyHelper() {
	}
	
	public static String getPartyName(GenericValue partyObject) {
        return getPartyName(partyObject, false);
    }

    public static String getPartyName(Delegator delegator, String partyId, boolean lastNameFirst) {
        GenericValue partyObject = null;
        try {
            partyObject = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", partyId), false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding PartyNameView in getPartyName", module);
        }
        if (partyObject == null) {
            return partyId;
        } else {
            return formatPartyNameObject(partyObject, lastNameFirst);
        }
    }
    
    public static String getPartyName(Delegator delegator, String partyId, boolean lastNameFirst, boolean hasMiddleName) {
    	GenericValue partyObject = null;
        try {
            partyObject = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", partyId), false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding PartyNameView in getPartyName", module);
        }
        if (partyObject == null) {
            return partyId;
        } else {
            return formatPartyNameObject(partyObject, lastNameFirst, hasMiddleName);
        }
    }
    
    public static String getFomulaNamePayroll(Delegator delegator, String code){
    	String name=null;
    	try {
			GenericValue payroll= delegator.findOne("PayrollFormula", UtilMisc.toMap("code", code), false);
			name=payroll.getString("name");
			if(name==null){
				return code;
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return name;
    }

    public static String getPartyName(GenericValue partyObject, boolean lastNameFirst) {
        if (partyObject == null) {
            return "";
        }
        if ("PartyGroup".equals(partyObject.getEntityName()) || "Person".equals(partyObject.getEntityName())) {
            return formatPartyNameObject(partyObject, lastNameFirst);
        } else {
            String partyId = null;
            try {
                partyId = partyObject.getString("partyId");
            } catch (IllegalArgumentException e) {
                Debug.logError(e, "Party object does not contain a party ID", module);
            }

            if (partyId == null) {
                Debug.logWarning("No party ID found; cannot get name based on entity: " + partyObject.getEntityName(), module);
                return "";
            } else {
                return getPartyName(partyObject.getDelegator(), partyId, lastNameFirst);
            }
        }
    }

    public static String formatPartyNameObject(GenericValue partyValue, boolean lastNameFirst) {
        if (partyValue == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        ModelEntity modelEntity = partyValue.getModelEntity();
        if (modelEntity.isField("firstName") && modelEntity.isField("middleName") && modelEntity.isField("lastName")) {
            if (lastNameFirst) {
                if (UtilFormatOut.checkNull(partyValue.getString("lastName")) != null) {
                    result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
                    if (partyValue.getString("firstName") != null) {
                        result.append(" ");
                    }
                }
                result.append(UtilFormatOut.checkNull(partyValue.getString("firstName")));
            } else {
                result.append(UtilFormatOut.ifNotEmpty(partyValue.getString("firstName"), "", " "));
                result.append(UtilFormatOut.ifNotEmpty(partyValue.getString("middleName"), "", " "));
                result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
            }
        }
        if (modelEntity.isField("groupName") && partyValue.get("groupName") != null) {
            result.append(partyValue.getString("groupName"));
        }
        return result.toString();
    }
    
    public static String formatPartyNameObject(GenericValue partyValue, boolean lastNameFirst, boolean hasMiddleName) {
        if (partyValue == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        ModelEntity modelEntity = partyValue.getModelEntity();
        if (modelEntity.isField("firstName") && modelEntity.isField("middleName") && modelEntity.isField("lastName")) {
            if (lastNameFirst && hasMiddleName) {
                if (UtilFormatOut.checkNull(partyValue.getString("lastName")) != null) {
                    result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
                    if (partyValue.getString("middleName") != null) {
                        result.append(" ");
                    }
                }
                result.append(UtilFormatOut.checkNull(partyValue.getString("middleName")));
                if (partyValue.getString("firstName") != null) {
                    result.append(" ");
                }
                result.append(UtilFormatOut.checkNull(partyValue.getString("firstName")));
            } else if(lastNameFirst && !hasMiddleName){
                formatPartyNameObject(partyValue, lastNameFirst);
            }else if(!lastNameFirst && hasMiddleName) {
            	formatPartyNameObject(partyValue, lastNameFirst);
            }else if(!lastNameFirst && !hasMiddleName) {
            	result.append(UtilFormatOut.ifNotEmpty(partyValue.getString("firstName"), "", " "));
                result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
            }
        }
        if (modelEntity.isField("groupName") && partyValue.get("groupName") != null) {
            result.append(partyValue.getString("groupName"));
        }
        return result.toString();
    }
    
    public static String getPartyPostalAddress(String partyId, String purpose, Delegator delegator) {
    	//Get dispatcher
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		
		GenericValue userLogin = null;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}
		
		//get contact
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("contactMechPurposeTypeId", purpose);
		inputMap.put("partyId", partyId);
		inputMap.put("userLogin", userLogin);
		Map<String, Object> result = null;
		try {
			result = dispatcher.runSync("getPartyPostalAddress", inputMap);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
		}
		String address = (String)result.get("address1");
		return address;
    }
    
    public static String getPartyTelephone(String partyId, String purpose, Delegator delegator) {
    	//Get dispatcher
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		
		GenericValue userLogin = null;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
		}
		
		//get contact
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("contactMechPurposeTypeId", purpose);
		inputMap.put("partyId", partyId);
		inputMap.put("userLogin", userLogin);
		Map<String, Object> result = null;
		try {
			result = dispatcher.runSync("getPartyTelephone", inputMap);
		} catch (GenericServiceException e) {
			Debug.log(e.getMessage(), module);
		}
		String countryCode = (String)result.get("countryCode");
		String areaCode = (String)result.get("areaCode");
		String contactNumber = (String)result.get("contactNumber");
		if(contactNumber == null || areaCode == null || contactNumber == null) {
			return null; 
		}else {
			return "(+" + (countryCode != null ? countryCode : "") + "-" + (areaCode != null ? areaCode : "") + ")-" + (contactNumber != null ? contactNumber : "");
		}
    }
    
    public static GenericValue getCurrAgreementOfEmpl(Delegator delegator, String employeeId) throws GenericEntityException{
    	return getAgreementOfEmplAtTime(delegator, employeeId, UtilDateTime.nowTimestamp());
    }
    
    public static GenericValue getAgreementOfEmplAtTime(Delegator delegator, String employeeId, Timestamp moment) throws GenericEntityException{
    	List<GenericValue> listEmplAgreementTyp = delegator.findByAnd("AgreementType", UtilMisc.toMap("parentTypeId", "EMPLOYMENT_AGREEMENT"), null, false);
    	List<String> listEmplAgreementTypeId = EntityUtil.getFieldListFromEntityList(listEmplAgreementTyp, "agreementTypeId", true);
    	List<EntityCondition> conditions = FastList.newInstance();
    	conditions.add(EntityCondition.makeCondition("agreementTypeId", EntityJoinOperator.IN, listEmplAgreementTypeId));
    	conditions.add(EntityUtil.getFilterByDateExpr(moment));
    	conditions.add(EntityCondition.makeCondition("partyIdTo", employeeId));
    	conditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
    	//TODO need add status of agreement
    	List<GenericValue> emplAgreement = delegator.findList("Agreement", EntityCondition.makeCondition(conditions), null, null, null, false);
    	if(UtilValidate.isNotEmpty(emplAgreement)){
    		return emplAgreement.get(0);
    	}
    	return null;
    }
}
