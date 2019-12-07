import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil

GenericValue agreement = delegator.findOne("Agreement", false, UtilMisc.toMap("agreementId", agreementId));
Timestamp agreementDate = new Timestamp(System.currentTimeMillis());
Timestamp agreementFromDate = new Timestamp(System.currentTimeMillis());
Timestamp agreementThruDate = new Timestamp(System.currentTimeMillis());
String partyIdFrom = "";
String partyIdTo = "";
if (agreement != null){
	
	partyIdFrom = (String)agreement.get("partyIdFrom");
	partyIdTo = (String)agreement.get("partyIdTo");
	agreementDate = (Timestamp)agreement.get("agreementDate");
	agreementFromDate = (Timestamp)agreement.get("fromDate");
	if(agreement.get("thruDate") != null){
		agreementThruDate = (Timestamp)agreement.get("thruDate");
	}
}
context.partyIdFrom = partyIdFrom;
context.partyIdTo = partyIdTo;
context.agreementDate = agreementDate;
context.agreementFromDate = agreementFromDate;
context.agreementThruDate = agreementThruDate;
