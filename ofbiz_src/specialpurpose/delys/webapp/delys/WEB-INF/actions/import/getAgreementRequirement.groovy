import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

List<GenericValue> listTmp = new ArrayList<GenericValue>();
if (!listAgreements.isEmpty()){
	for (GenericValue agr : listAgreements){
		List<GenericValue> listReqByAgreements = delegator.findList("AgreementRequirement", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", (String)agr.get("agreementId"))), null, null, null, false);
		if (!listReqByAgreements.isEmpty()){
			listTmp.add(agr);
		}
	}
}
if (!listTmp.isEmpty()){
	listAgreements.removeAll(listTmp);
}
context.listAgreements = listAgreements;
