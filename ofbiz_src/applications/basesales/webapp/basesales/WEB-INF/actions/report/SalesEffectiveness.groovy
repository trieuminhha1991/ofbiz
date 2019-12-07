import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import javolution.util.FastList;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import com.olbius.basesales.util.SalesPartyUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;

context.ORG = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

if (SalesPartyUtil.isDistributor(delegator, userLogin.getString("partyId"))) {
	context.BOO_DIS = "true";
	context.DIS_ID = userLogin.getString("partyId");
}

def dummy = delegator.findByAnd("Product", UtilMisc.toMap("productTypeId", "FINISHED_GOOD"), null, true);
def products = "[";
def mapUomId = "{";
def flag = false;
for(value in dummy) {
    if(flag) {
        products += ",";
        mapUomId += ",";
    }
    products += "{ productId: " + "\'" + value.get("productId") + "\'" + ", productCode: " + "\'" + value.get("productCode") + "\'" + ", productName: " + "\'" + value.get("productName") + "\'" + " }";
    def uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", value.get("quantityUomId")), false);
    if (uom) {
    	mapUomId += value.get("productId") + ": \'" +  uom.get("description", locale) + "\'" ;
    }
    flag = true;
}
products += "]";
mapUomId += "}";
context.products = products;
context.mapUomId = mapUomId;

dummy = delegator.findByAnd("CustomTimePeriod", UtilMisc.toMap("periodTypeId", "SALES_YEAR"), null, true);
def salesYear = "[";
flag = false;
for(value in dummy) {
    if(flag) {
    	salesYear += ",";
    }
    salesYear += "{ value: " + "\'" + value.get("periodName") + "\'" + ", text: " + "\'" + value.get("periodName") + "\'" + " }";
    flag = true;
}
salesYear += "]";
context.salesYear = salesYear;