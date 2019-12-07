/*
 * ReportAccessories - The Accessories for Olbius Report
 */

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.util.SalesPartyUtil;

Map<String, Object> LocalData = FastMap.newInstance();

/*
 * ReportAccessories - facilities
 */
def ownerPartyId = userLogin.getString("partyId");
if (!SalesPartyUtil.isDistributor(delegator, ownerPartyId)) {
	ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
}
def dummy = delegator.findByAnd("Facility", UtilMisc.toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", ownerPartyId), null, true);
def facilities = "[";
def flag = false;
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
 * ReportAccessories - categories
 */
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
 * ReportAccessories - statusItems_ORDER_ITEM
 */
dummy = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "ORDER_ITEM_STATUS"), null, true);
def statusItems_ORDER_ITEM = "[";
flag = false;
for (value in dummy) {
	if (flag) {
		statusItems_ORDER_ITEM += ",";
	}
	statusItems_ORDER_ITEM += "{ value: " + "\'" + value.get("statusId") + "\'" + ", text: " + "\'" + value.get("description", locale) + "\'" + " }";
	flag = true;
}
statusItems_ORDER_ITEM += "]";
LocalData.statusItems_ORDER_ITEM = statusItems_ORDER_ITEM;

/*
 * ReportAccessories - suppliers
 */
dummy = delegator.findByAnd("PartySupplierDetail", null, null, true);
def suppliers = "[";
def mapSuppliers = "{";
flag = false;
for (value in dummy) {
	if (flag) {
		suppliers += ",";
		mapSuppliers += ",";
	}
	suppliers += "{ value: " + "\'" + value.get("partyId") + "\'" + ", text: " + "\'" + value.get("groupName") + "\'" + " }";
	mapSuppliers += value.get("partyId") + ":\'" + value.get("groupName") + "\'";
	flag = true;
}
suppliers += "]";
mapSuppliers += "}";
LocalData.suppliers = suppliers;
LocalData.mapSuppliers = mapSuppliers;

context.LocalData = LocalData;