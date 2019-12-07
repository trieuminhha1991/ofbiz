/*
 * ReportAccessories - The Accessories for Olbius Report
 */

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.util.SalesPartyUtil;

Map<String, Object> LocalData = FastMap.newInstance();

/*
 * ReportAccessories - uoms_PRODUCT_PACKING
 */
dummy = delegator.findByAnd("Uom", UtilMisc.toMap("uomTypeId", "PRODUCT_PACKING"), null, true);
def uoms_PRODUCT_PACKING = "[";
def mapUoms_PRODUCT_PACKING = "{";
flag = false;
for (value in dummy) {
	if (flag) {
		uoms_PRODUCT_PACKING += ",";
		mapUoms_PRODUCT_PACKING += ",";
	}
	uoms_PRODUCT_PACKING += "{ value: " + "\'" + value.get("uomId") + "\'" + ", text: " + "\'" + value.get("description", locale) + "\'" + " }";
	mapUoms_PRODUCT_PACKING += value.get("uomId") + ":\'" + value.get("description", locale) + "\'";
	flag = true;
}
uoms_PRODUCT_PACKING += "]";
mapUoms_PRODUCT_PACKING += "}";
LocalData.uoms_PRODUCT_PACKING = uoms_PRODUCT_PACKING;
LocalData.mapUoms_PRODUCT_PACKING = mapUoms_PRODUCT_PACKING;

def conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "CATALOG_CATEGORY")));
conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.NOT_EQUAL, "BROWSE_ROOT"));
dummy = delegator.findList("ProductCategory",
		EntityCondition.makeCondition(conditions), null, null, null, true);
def categories = "[";
flag = false;
for (value in dummy) {
	if (flag) {
		categories += ",";
	}
	categories += "{ value: " + "\'" + value.get("productCategoryId") + "\'" + ", text: " + "\'" + value.get("categoryName") + "\'" + " }";
	flag = true;
}
categories += "]";
LocalData.categories = categories;

/*
 * ReportAccessories - facilities
 */
def ownerPartyId = userLogin.getString("partyId");
if (!SalesPartyUtil.isDistributor(delegator, ownerPartyId)) {
	ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
}
dummy = delegator.findByAnd("Facility", UtilMisc.toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", ownerPartyId), null, true);
def facilities = "[";
flag = false;
for (value in dummy) {
	if (flag) {
		facilities += ",";
	}
	facilities += "{ value: " + "\'" + value.get("facilityId") + "\'" + ", text: " + "\'" + value.get("facilityName") + "\'" + " }";
	flag = true;
}
facilities += "]";
LocalData.facilities = facilities;

/*
 * ReportAccessories - enumerations_SALES_METHOD_CHANNEL
 */
dummy = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, true);
def enumerations_SALES_METHOD_CHANNEL = "[";
flag = false;
for (value in dummy) {
	if (flag) {
		enumerations_SALES_METHOD_CHANNEL += ",";
	}
	enumerations_SALES_METHOD_CHANNEL += "{ value: " + "\'" + value.get("enumId") + "\'" + ", text: " + "\'" + value.get("description", locale) + "\'" + " }";
	flag = true;
}
enumerations_SALES_METHOD_CHANNEL += "]";
LocalData.enumerations_SALES_METHOD_CHANNEL = enumerations_SALES_METHOD_CHANNEL;

/*
 * ReportAccessories - enumerations_ORDER_SALES_CHANNEL
 */
dummy = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "ORDER_SALES_CHANNEL"), null, true);
def enumerations_ORDER_SALES_CHANNEL = "[";
flag = false;
for (value in dummy) {
	if (flag) {
		enumerations_ORDER_SALES_CHANNEL += ",";
	}
	enumerations_ORDER_SALES_CHANNEL += "{ value: " + "\'" + value.get("enumId") + "\'" + ", text: " + "\'" + value.get("description", locale) + "\'" + " }";
	flag = true;
}
enumerations_ORDER_SALES_CHANNEL += "]";
LocalData.enumerations_ORDER_SALES_CHANNEL = enumerations_ORDER_SALES_CHANNEL;

/*
 * ReportAccessories - returnReasons
 */
dummy = delegator.findByAnd("ReturnReason", null, null, true);
def returnReasons = "[";
flag = false;
for (value in dummy) {
	if (flag) {
		returnReasons += ",";
	}
	returnReasons += "{ value: " + "\'" + value.get("returnReasonId") + "\'" + ", text: " + "\'" + value.get("description", locale) + "\'" + " }";
	flag = true;
}
returnReasons += "]";
LocalData.returnReasons = returnReasons;

/*
 * ReportAccessories - returnItemTypes
 */
dummy = delegator.findByAnd("ReturnItemType", null, null, true);
def returnItemTypes = "[";
flag = false;
for (value in dummy) {
	if (flag) {
		returnItemTypes += ",";
	}
	returnItemTypes += "{ value: " + "\'" + value.get("returnItemTypeId") + "\'" + ", text: " + "\'" + value.get("description", locale) + "\'" + " }";
	flag = true;
}
returnItemTypes += "]";
LocalData.returnItemTypes = returnItemTypes;

context.LocalData = LocalData;