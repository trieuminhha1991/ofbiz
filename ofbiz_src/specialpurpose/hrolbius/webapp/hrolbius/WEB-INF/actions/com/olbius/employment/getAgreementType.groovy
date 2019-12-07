import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;

import org.ofbiz.entity.*;

import com.olbius.util.PartyUtil;

import java.util.List;

agreementId = parameters.agreementId;

agreementType = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), true);
AgreementType=null;
agreementTypeId = null;
if(UtilValidate.isNotEmpty(agreementType)){
	agreementTypeId = agreementType.getString("agreementTypeId");
}

if(agreementTypeId){
	if(agreementTypeId == 'TRIAL_AGREEMENT'){
		AgreementType = 'TrialAgreement';
	}
	if(agreementTypeId == 'EMPLOYMENT_AGREEMENT'){
		AgreementType = 'EmploymentAgreement';
	}
}

context.AgreementType = AgreementType;
