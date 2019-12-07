import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil

/**
 * Created by user on 1/2/19.
 */
String billId = (String)parameters.billId
List<GenericValue> containers = delegator.findList("Container", EntityCondition.makeCondition("billId", billId), null, null, null, false)
List<String> containerIds = EntityUtil.getFieldListFromEntityList(containers, "containerId", true)
List<GenericValue> packingLists = delegator.findList("PackingListHeader", EntityCondition.makeCondition("containerId", EntityOperator.IN, containerIds), null, null, null, false);
List<String> agreementIds = EntityUtil.getFieldListFromEntityList(packingLists, "agreementId", true)
List<GenericValue> agreements = delegator.findList("Agreement", EntityCondition.makeCondition("agreementId", EntityOperator.IN, agreementIds), null, null, null, false);
if(UtilValidate.isNotEmpty(agreements)) {
    List<GenericValue> listDefaultCurrencyTerms = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreements.get(0).get("agreementId"), "termTypeId", "DEFAULT_PAY_CURRENCY")), null, null, null, false);
    if(listDefaultCurrencyTerms != null) {
        String currencyUomId = listDefaultCurrencyTerms.get(0).getString("textValue");
        context.currencyUomId = currencyUomId;
    }
}