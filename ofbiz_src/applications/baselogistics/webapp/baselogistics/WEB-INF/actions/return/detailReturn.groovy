/*
* Prepare for sup return Note
*/
import java.util.ArrayList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import com.olbius.util.*;
import java.util.Calendar;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import com.olbius.baselogistics.util.*;
import org.ofbiz.base.util.UtilFormatOut;
import java.math.RoundingMode;
import com.olbius.baselogistics.delivery.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.condition.EntityOperator;
	
locale = context.get("locale");
returnId = parameters.returnId;
returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId",returnId), false);
statusId = returnHeader.getString("statusId");
String currency = returnHeader.getString("currencyUomId");
String fromPartyId = returnHeader.getString("fromPartyId");
String partyIdTo = returnHeader.getString("toPartyId");

String originFacilityId = returnHeader.getString("destinationFacilityId");
fromParty = delegator.findOne("PartyFullNameDetail", UtilMisc.toMap("partyId", fromPartyId), false);
originFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", originFacilityId), false);

String originFacilityName = "";
if (originFacility != null) {
	originFacilityName = originFacility.getString("facilityName");
}

Map<String, Object> mapAddressTmp = dispatcher.runSync("getFacilityContactMechs", [facilityId: originFacilityId, contactMechPurposeTypeId: "SHIP_ORIG_LOCATION", userLogin: userLogin]);
List<GenericValue> listFacilityContactMechs = (List<GenericValue>)mapAddressTmp.get("listFacilityContactMechs");
String originFacilityAddress = "";
if (!listFacilityContactMechs.isEmpty()) {
	 originFacilityAddress = listFacilityContactMechs.get(0).get("address1");
}
Timestamp createDate = returnHeader.getTimestamp("entryDate");

String fullName = "";
GenericValue supplier = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyIdTo), false); 
if(supplier != null){
	fullName = supplier.groupName;
}

listItem = new ArrayList<Map<String, Object>>();
listReturnItem = delegator.findList("ReturnItem", EntityCondition.makeCondition([returnId : returnId]), null, null, null, false);
BigDecimal total = BigDecimal.ZERO;
for (GenericValue item in listReturnItem) {
	GenericValue returnItem = delegator.findOne("ReturnItemDetail", UtilMisc.toMap("returnId", returnId, "returnItemSeqId", item.getString("returnItemSeqId")), false);
	Map<String, Object> mapItem = FastMap.newInstance();
	mapItem.putAll(returnItem);
	String requireAmount = returnItem.getString("requireAmount");
	if (requireAmount != null && "Y".equals(requireAmount)) {
		GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", returnItem.getString("weigthUomId")), false);
		mapItem.weigthUomId = uom.getString("description");
	}
	BigDecimal totalItem = returnItem.getBigDecimal("returnQuantity").multiply(returnItem.getBigDecimal("returnPrice"));
	if ("SUP_RETURN_SHIPPED".equals(returnHeader.statusId) || "SUP_RETURN_COMPLETED".equals(returnHeader.statusId)) {
		if (returnItem.getBigDecimal("receivedQuantity") != null){
			totalItem = returnItem.getBigDecimal("receivedQuantity").multiply(returnItem.getBigDecimal("returnPrice"));
		}
	}
	GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", returnItem.getString("quantityUomId")), false);
	mapItem.quantityUomId = uom.getString("description");
	mapItem.total = totalItem;
	total = total.add(totalItem);
	listItem.add(mapItem);
}

context.fromParty = fromParty;
context.total = total;
context.listItem = listItem;
context.returnId = returnId;
context.returnHeader = returnHeader;
context.partyIdTo = partyIdTo;
context.fullName = fullName;
context.createDate = createDate;
context.currency = currency;
context.facilityName = originFacilityName;
context.statusId = statusId;    
context.originFacility = originFacility;    