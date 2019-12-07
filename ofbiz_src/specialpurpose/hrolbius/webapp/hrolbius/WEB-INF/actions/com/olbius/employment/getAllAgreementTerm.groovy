import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.agreement.AgreementTermData;


public static List<AgreementTermData> getAgreementTermAndLevel(Delegator delegator, String agreementId, String rootTermTypeId, List<GenericValue> termTypeList, int level){
	List<AgreementTermData> retList = FastList.newInstance();
	if(termTypeList != null){
		for(GenericValue termType: termTypeList){
			String termTypeId = termType.getString("termTypeId");
			List<GenericValue> agreementTermList = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("agreementId", agreementId, "termTypeId", termTypeId), null, false);
			List<GenericValue> childTermType = delegator.findByAnd("TermType", UtilMisc.toMap("parentTypeId", termTypeId), null,false);
			AgreementTermData termData = new AgreementTermData(termTypeId, termType.getString("description"), rootTermTypeId, level);
			retList.add(termData);
			if(UtilValidate.isNotEmpty(agreementTermList)){
				GenericValue agreementTerm = agreementTermList.get(0);
				String textValue = agreementTerm.getString("textValue");
				if(textValue != null){
					termData.setTextValue(textValue);
					termData.setAgreementTermId(agreementTerm.getString("agreementTermId"));
				}	
			}
			if(UtilValidate.isNotEmpty(childTermType)){
				termData.setHasChild(true);
				List<AgreementTermData> tempList = getAgreementTermAndLevel(delegator, agreementId, rootTermTypeId, childTermType, level + 1);
				if(UtilValidate.isNotEmpty(tempList)){
					retList.addAll(tempList);
				}
			}else{
				termData.setHasChild(false);
			}
		}
	}
	return retList;
	
}
if(agreement){
	String agreementTypeId = agreement.getString("agreementTypeId");
	String rootTermType = null;
	if("EMPLOYMENT_AGREEMENT".equals(agreementTypeId)){
		rootTermType = "EMPLOYEMENT_TERM";
	}else if("TRIAL_AGREEMENT".equals(agreementTypeId)){
		rootTermType = "PROBATION_TERM";
	} 
	if(rootTermType != null){
		List<GenericValue> childTermType = delegator.findByAnd("TermType", UtilMisc.toMap("parentTypeId", rootTermType), UtilMisc.toList("termTypeId"), false);
		List<AgreementTermData> agreementTermList = getAgreementTermAndLevel(delegator, agreement.agreementId, rootTermType, childTermType, 1);
		context.agreementTermList = agreementTermList; 
	}
}