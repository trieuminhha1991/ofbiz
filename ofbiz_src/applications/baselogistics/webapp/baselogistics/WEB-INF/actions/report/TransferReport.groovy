import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import javolution.util.FastList;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

def dummy = delegator.findByAnd("Facility", null, null, false);
def facilities = "[";
def flag = false;
for(value in dummy) {
    if(flag) {
    	facilities += ",";
    }
    facilities += "{ facilityId: " + "\'" + value.get("facilityId") + "\'" + ", facilityName: " + "\'" + value.get("facilityName") + "\'" + " }";
    flag = true;
}
facilities += "]";
context.facilities = facilities;

def conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "CATALOG_CATEGORY")));
conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.NOT_EQUAL, "BROWSE_ROOT"));
dummy = delegator.findList("ProductCategory",
		EntityCondition.makeCondition(conditions), null, null, null, true);
def categories = "[";
flag = false;
for(value in dummy) {
    if(flag) {
    	categories += ",";
    }
    categories += "{ productCategoryId: " + "\'" + value.get("productCategoryId") + "\'" + ", categoryName: " + "\'" + value.get("categoryName") + "\'" + " }";
    flag = true;
}
categories += "]";
context.categories = categories;

dummy = delegator.findByAnd("TransferType", null, null, false);
def transferTypes = "[";
def mapTransferType = "{";
flag = false;
for(value in dummy) {
    if(flag) {
    	transferTypes += ",";
    	mapTransferType += ",";
    }
    transferTypes += "{ value: " + "\'" + value.get("transferTypeId") + "\'" + ", text: " + "\'" + value.get("description", locale) + "\'" + " }";
    mapTransferType += value.get("transferTypeId") + ":" + "\'" + value.get("description", locale) + "\'";
    flag = true;
}
transferTypes += "]";
mapTransferType += "}";
context.transferTypes = transferTypes;
context.mapTransferType = mapTransferType;

dummy = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "TRANSFER_ITEM_STATUS"), null, false);
def transferStatusItems = "[";
def mapTransferStatusItem = "{";
flag = false;
for(value in dummy) {
    if(flag) {
    	transferStatusItems += ",";
    	mapTransferStatusItem += ",";
    }
    transferStatusItems += "{ value: " + "\'" + value.get("statusId") + "\'" + ", text: " + "\'" + value.get("description", locale) + "\'" + " }";
    mapTransferStatusItem += value.get("statusId") + ":" + "\'" + value.get("description", locale) + "\'";
    flag = true;
}
transferStatusItems += "]";
mapTransferStatusItem += "}";
context.transferStatusItems = transferStatusItems;
context.mapTransferStatusItem = mapTransferStatusItem;

dummy = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "DELIVERY_ITEM_STATUS"), null, false);
def deliveryStatusItems = "[";
def mapDeliveryStatusItem = "{";
flag = false;
for(value in dummy) {
	if(flag) {
		deliveryStatusItems += ",";
		mapDeliveryStatusItem += ",";
	}
	deliveryStatusItems += "{ value: " + "\'" + value.get("statusId") + "\'" + ", text: " + "\'" + value.get("description", locale) + "\'" + " }";
	mapDeliveryStatusItem += value.get("statusId") + ":" + "\'" + value.get("description", locale) + "\'";
	flag = true;
}
deliveryStatusItems += "]";
mapDeliveryStatusItem += "}";
context.deliveryStatusItems = deliveryStatusItems;
context.mapDeliveryStatusItem = mapDeliveryStatusItem;