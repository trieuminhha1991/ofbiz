import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;


fieldSelect = ["agreementId", "attrValue"] as Set
List<GenericValue> listAgreementNotBill = delegator.findList("AgreementAndAgreementAttribute", EntityCondition.makeCondition(UtilMisc.toMap("statusId", "AGREEMENT_SENT", "agreementTypeId", "PURCHASE_AGREEMENT","attrName", "AGREEMENT_NAME")), fieldSelect, null, null, false);
context.listAgreementNotBill = listAgreementNotBill;
