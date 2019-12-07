package com.olbius.basehr.employment.helper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.CommonUtil;

public class AgreementHelper {
	public static final String AGREEMENT_ROLE_REPRESENTATIVE = "REPRESENTATIVE";
	public static List<String> getAgreementType(Delegator delegator, String parentTypeId) throws GenericEntityException{
		List<GenericValue> agreementType = delegator.findByAnd("AgreementType", UtilMisc.toMap("parentTypeId", parentTypeId), null, false);
		if(UtilValidate.isNotEmpty(agreementType)){
			List<String> agreementTypeList = EntityUtil.getFieldListFromEntityList(agreementType, "agreementTypeId", true);
			return agreementTypeList;
		}
		return null;
	}

	public static String getAgreementPeriod(Delegator delegator,
			String agreementId) throws GenericEntityException {
		GenericValue agreementPeriodAtt = delegator.findOne("AgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName", "agreementPeriod"), false);
		if(agreementPeriodAtt != null){
			return agreementPeriodAtt.getString("attrValue");
		}
		return null;
	}

	public static void createOrStoreAgreementAttribute(Delegator delegator,
			String agreementId, String agreementDuration) throws GenericEntityException {
		GenericValue agreementAtt = delegator.makeValue("AgreementAttribute");
		agreementAtt.put("agreementId", agreementId);
		agreementAtt.put("attrName", "agreementPeriod");
		agreementAtt.put("attrValue", agreementDuration);
		delegator.createOrStore(agreementAtt);
	}

	public static BigDecimal getAgreementTerm(Delegator delegator, String agreementId, String termTypeId) throws GenericEntityException {
		List<GenericValue> agreementTerm = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("termTypeId", termTypeId, "agreementId", agreementId), null, false);
		if(UtilValidate.isNotEmpty(agreementTerm)){
			return agreementTerm.get(0).getBigDecimal("termValue");
		}
		return null;
	}

	public static String getAgreementRepresent(Delegator delegator, String agreementId) throws GenericEntityException {
		List<GenericValue> agreementRep = delegator.findByAnd("AgreementRole", UtilMisc.toMap("agreementId", agreementId, "roleTypeId", "REPRESENTATIVE"), null, false);
		if(UtilValidate.isNotEmpty(agreementRep)){
			return agreementRep.get(0).getString("partyId");
		}
		return null;
	}

	public static void createOrUpdateAgreementTerm(LocalDispatcher dispatcher, Delegator delegator, String agreementId, 
			String termTypeId, BigDecimal termValue, String textValue, Timestamp fromDate, Timestamp thruDate, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		List<GenericValue> agreementTermList = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("agreementId", agreementId, "termTypeId", termTypeId), null, false);
		if(UtilValidate.isNotEmpty(agreementTermList)){
			GenericValue agreementTerm = agreementTermList.get(0);
			agreementTerm.set("termValue", termValue);
			agreementTerm.set("textValue", textValue);
			agreementTerm.set("fromDate", fromDate);
			agreementTerm.set("thruDate", thruDate);
			agreementTerm.store();
		}else{
			dispatcher.runSync("createAgreementTerm", 
					UtilMisc.toMap("agreementId", agreementId, "termTypeId", termTypeId, "termValue", termValue, 
							"fromDate", fromDate, "thruDate", thruDate, "userLogin", userLogin));
		}
	}

	public static void createAgreementTermAttr(Delegator delegator,
			String agreementTermId, String attrName, String attrValue) throws GenericEntityException {
		GenericValue agreementAttr = delegator.makeValue("AgreementTermAttribute");
		agreementAttr.put("agreementTermId", agreementTermId);
		agreementAttr.put("attrName", attrName);
		agreementAttr.put("attrValue", attrValue);
		delegator.createOrStore(agreementAttr);
	}

	public static Map<String, Object> updateAgreementAllowanceTerm(LocalDispatcher dispatcher, Delegator delegator,
			GenericValue userLogin, String agreementId, Map<String, BigDecimal> allowanceMap, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException, GenericServiceException {
		Set<String> allowanceNewSet = allowanceMap.keySet();
		List<GenericValue> allowanceExistsList = delegator.findByAnd("AgreementTermAllowanceAndAttr", 
				UtilMisc.toMap("agreementId", agreementId, "termTypeId", "ALLOWANCE_TERM", "attrName", "ALLOWANCE_CODE"), null, false);
		
		Map<String, Object> resultService;
		Map<String, Object> retMap = FastMap.newInstance();
		for(String allowanceCode: allowanceNewSet){
			BigDecimal termValue = allowanceMap.get(allowanceCode);
			List<GenericValue> allowanceCodeExists = EntityUtil.filterByCondition(allowanceExistsList, EntityCondition.makeCondition("attrValue", allowanceCode));
			if(UtilValidate.isNotEmpty(allowanceCodeExists)){
				String agreementTermId = allowanceExistsList.get(0).getString("agreementTermId");
				resultService = dispatcher.runSync("updateAgreementTerm", UtilMisc.toMap("agreementTermId", agreementTermId, "termValue", termValue, "userLogin", userLogin));
				if(!ServiceUtil.isSuccess(resultService)){
					retMap.put(ModelService.RESPONSE_MESSAGE, "error");
					retMap.put(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return retMap;
				}
			}else{
				resultService = dispatcher.runSync("createAgreementTerm", 
						UtilMisc.toMap("agreementId", agreementId, "termTypeId", "ALLOWANCE_TERM", "termValue", termValue, 
								"fromDate", fromDate, "thruDate", thruDate, "userLogin", userLogin));
				if(ServiceUtil.isSuccess(resultService)){
					String agreementTermId = (String)resultService.get("agreementTermId");
					createAgreementTermAttr(delegator, agreementTermId, "ALLOWANCE_CODE", allowanceCode);
				}else{
					retMap.put(ModelService.RESPONSE_MESSAGE, "error");
					retMap.put(ModelService.ERROR_MESSAGE, CommonUtil.getErrorMessageFromService(resultService));
					return retMap;
				}
			}
		}
		for(GenericValue allowanceCode: allowanceExistsList){
			String attrValue = allowanceCode.getString("attrValue");
			if(!allowanceNewSet.contains(attrValue)){
				String agreementTermId = allowanceCode.getString("agreementTermId");
				GenericValue agreementTerm = delegator.findOne("AgreementTerm", UtilMisc.toMap("agreementTermId", agreementTermId), false);
				List<GenericValue> agreementTermAttr = delegator.findByAnd("AgreementTermAttribute", UtilMisc.toMap("agreementTermId", agreementTermId), null, false);
				for(GenericValue tempGv: agreementTermAttr){
					tempGv.remove();
				}
				agreementTerm.remove();
			}
		}
		return retMap;
	}

}
