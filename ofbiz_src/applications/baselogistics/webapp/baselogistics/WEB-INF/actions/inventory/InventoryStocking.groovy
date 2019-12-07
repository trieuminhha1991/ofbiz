import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.Delegator;
import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import java.text.SimpleDateFormat;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.security.util.SecurityUtil;

import org.ofbiz.base.util.UtilDateTime;

import com.olbius.basehr.util.PartyUtil;

GenericValue stockEvent = delegator.findOne("StockEvent", UtilMisc.toMap("eventId", parameters.eventId), false);
context.stockEvent = stockEvent;

def activeTab = parameters.activeTab?parameters.activeTab:"upload-tab";
boolean isThru = false;
Timestamp thruDate = stockEvent.getTimestamp("thruDate");
if (UtilValidate.isNotEmpty(thruDate)) {
	if (UtilDateTime.nowTimestamp().after(thruDate)) {
		isThru = true;
		if (UtilValidate.isEmpty(activeTab)) {
			if (SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "APPROVE", "MODULE",
					"INVENTORY_STOCKING")) {
				activeTab = "aggregation-tab";
			} else {
				activeTab = "phieukk-tab";
			}
		}
	}
}
context.isThru = isThru;
context.activeTab = activeTab;

String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
context.organizationId = organizationId;
context.organizationName = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", organizationId), true).get("groupName");

def location = parameters.location;
context.location = location;
List<EntityCondition> conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS, stockEvent.get("eventId")));
conditions.add(EntityCondition.makeCondition("location", EntityJoinOperator.EQUALS, location));
List<GenericValue> stockEventItem = delegator.findList("StockEventItemDetail", EntityCondition.makeCondition(conditions),
		null, UtilMisc.toList("eventItemSeqId"), null, false);

context.stockEventItemStatus = delegator.findOne("StockEventItemStatus", UtilMisc.toMap("eventId", stockEvent.get("eventId"), "location", location), false);

GenericValue stockEventDetail = delegator.findOne("StockEventDetail", UtilMisc.toMap("eventId", parameters.eventId, "facilityId", stockEvent.facilityId), false);
if (stockEventDetail) {
	context.facilityName = stockEventDetail.facilityName;
}

SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
context.entryDate = format.format(UtilDateTime.nowTimestamp());
context.stockEventItem = stockEventItem;


conditions.clear();
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("eventId", stockEvent.get("eventId"), "location", location, "roleTypeId", "STOCKING_INPUT")));
List<GenericValue> dummy = delegator.findList("StockEventItemRole", EntityCondition.makeCondition(conditions),
		null, null, null, false);
def partyInput = "";
for (GenericValue x : dummy) {
	partyInput += PartyUtil.getPartyName(delegator, x.getString("partyId")) + ", ";
}
if (UtilValidate.isNotEmpty(partyInput)) {
	partyInput = partyInput.substring(0, partyInput.length() - 2);
}
context.partyInput = partyInput;

conditions.clear();
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("eventId", stockEvent.get("eventId"), "location", location, "roleTypeId", "STOCKING_COUNT")));
dummy = delegator.findList("StockEventItemRole", EntityCondition.makeCondition(conditions),
		null, null, null, false);
def partyCount = "";
for (GenericValue x : dummy) {
	partyCount += PartyUtil.getPartyName(delegator, x.getString("partyId")) + ", ";
}
if (UtilValidate.isNotEmpty(partyCount)) {
	partyCount = partyCount.substring(0, partyCount.length() - 2);
}
context.partyCount = partyCount;

conditions.clear();
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("eventId", stockEvent.get("eventId"), "location", location, "roleTypeId", "STOCKING_SCAN")));
dummy = delegator.findList("StockEventItemRole", EntityCondition.makeCondition(conditions),
		null, null, null, false);
def partyScan = "";
for (GenericValue x : dummy) {
	partyScan += PartyUtil.getPartyName(delegator, x.getString("partyId")) + ", ";
}
if (UtilValidate.isNotEmpty(partyScan)) {
	partyScan = partyScan.substring(0, partyScan.length() - 2);
}
context.partyScan = partyScan;

conditions.clear();
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("eventId", stockEvent.get("eventId"), "location", location, "roleTypeId", "STOCKING_CHECK")));
dummy = delegator.findList("StockEventItemRole", EntityCondition.makeCondition(conditions),
		null, null, null, false);
def partyCheck = "";
for (GenericValue x : dummy) {
	partyCheck += PartyUtil.getPartyName(delegator, x.getString("partyId")) + ", ";
}
if (UtilValidate.isNotEmpty(partyCheck)) {
	partyCheck = partyCheck.substring(0, partyCheck.length() - 2);
}
context.partyCheck = partyCheck;


dummy = delegator.findList("VarianceReason", null, null, null, null, true);
def varianceReasons = "[";
def mapVarianceReason = "{";
flag = false;
for (value in dummy) {
	if (flag) {
		varianceReasons += ",";
		mapVarianceReason += ",";
	}
	varianceReasons += "{ value: " + "\'" + value.get("varianceReasonId") + "\'" + ", text: " + "\'" + value.get("description", locale) + "\'" + ", negativeNumber: " + "\'" + value.get("negativeNumber") + "\'" + " }";
	mapVarianceReason += value.get("varianceReasonId") + ":\'" + value.get("description", locale) + "\'";
	flag = true;
}
varianceReasons += "]";
mapVarianceReason += "}";
context.varianceReasons = varianceReasons;
context.mapVarianceReason = mapVarianceReason;