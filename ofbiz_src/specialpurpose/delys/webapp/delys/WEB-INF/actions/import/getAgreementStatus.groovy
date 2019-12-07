
import java.lang.reflect.GenericArrayType;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

GenericValue agreement = delegator.findOne("Agreement", false, UtilMisc.toMap("agreementId", agreementId));
List<GenericValue> listStatus = new ArrayList<GenericValue>();
if (agreement != null){
	String statusId = (String)agreement.get("statusId");
	if("AGREEMENT_CREATED".equals(statusId)) {
			exprOrList = [];
			expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_CREATED");
			exprOrList.add(expr);
			expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_APPROVED");
			exprOrList.add(expr);
			expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_CANCELLED");
			exprOrList.add(expr);
			Cond = EntityCondition.makeCondition(exprOrList, EntityOperator.OR);
			listStatus = delegator.findList("StatusItem", Cond, null, null, null, false);
	} else {
		if("AGREEMENT_APPROVED".equals(statusId)) {
			exprOrList = [];
			expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_APPROVED");
			exprOrList.add(expr);
			expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_SENT");
			exprOrList.add(expr);
			expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_CANCELLED");
			exprOrList.add(expr);
			Cond = EntityCondition.makeCondition(exprOrList, EntityOperator.OR);
			listStatus = delegator.findList("StatusItem", Cond, null, null, null, false);
		} else {
			if("AGREEMENT_SENT".equals(statusId)) {	
				exprOrList = [];
				expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_SENT");
				exprOrList.add(expr);
				expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_PROCESSING");
				exprOrList.add(expr);
				expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_CANCELLED");
				exprOrList.add(expr);
				Cond = EntityCondition.makeCondition(exprOrList, EntityOperator.OR);
				listStatus = delegator.findList("StatusItem", Cond, null, null, null, false);
			} else {
				if("AGREEMENT_PROCESSING".equals(statusId)) {
					exprOrList = [];
					expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_PROCESSING");
					exprOrList.add(expr);
					expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_COMPLETED");
					exprOrList.add(expr);
					Cond = EntityCondition.makeCondition(exprOrList, EntityOperator.OR);
					listStatus = delegator.findList("StatusItem", Cond, null, null, null, false);
				}
			}
		}
	}
}
context.listStatus = listStatus;