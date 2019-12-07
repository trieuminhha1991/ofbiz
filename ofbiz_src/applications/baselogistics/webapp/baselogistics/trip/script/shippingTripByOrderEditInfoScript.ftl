<@jqOlbCoreLib hasValidator=true />
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	var localeStr = "VI";
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	
	<#assign shippingTripId = parameters.shippingTripId?if_exists/>
	<#assign shippingTrip = delegator.findOne("ShippingTrip", {"shippingTripId" : shippingTripId}, false)/>
	var startDateTimeOld = "${shippingTrip.startDateTime?if_exists}";
	var finishedDateTimeOld = "${shippingTrip.finishedDateTime?if_exists}";
	var description = "${shippingTrip.description?if_exists}";
	var shipCost= "${shippingTrip.tripCost?if_exists}";
	var costCustomerPaid = "${shippingTrip.costCustomerPaid?if_exists}";
	var faciData = {};
	<#assign deliDataList = delegator.findList("ShippingTripDeliveryOrderItemView", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("shippingTripId", shippingTripId?if_exists), null, null, null, false)/>
	var listOrderOld = new Array();

	<#assign deliData = deliDataList?first />
	<#assign deliData = delegator.findOne("Delivery", {"deliveryId" : deliData.deliveryId?if_exists}, false)/>
	faciData['facilityId'] = '${deliData.originFacilityId}';
	<#assign faciId = deliData.originFacilityId />
	<#assign faciData = delegator.findOne("Facility", {"facilityId" : faciId}, false)/>
	faciData['facilityName'] = '${StringUtil.wrapString(faciData.facilityName)}';
	var facility = new Array();
	facility.push(faciData);
	var shipperPartyData = new Array();
	<#assign partyName = delegator.findOne("PartyNameView", {"partyId" : shippingTrip.shipperId?if_exists}, false)/>
	
	var row = {};
    row['lastName'] = "${StringUtil.wrapString(partyName.lastName?if_exists)}";
    row['middleName'] = "${StringUtil.wrapString(partyName.middleName?if_exists)}";
    row['firstName'] = "${StringUtil.wrapString(partyName.firstName?if_exists)}";
    var fullName = null;
    if (row.lastName){
      if (fullName){
        fullName = fullName + ' ' + row.lastName;
      } else {
        fullName = row.lastName;
      }
    }
    if (row.middleName){
      if (fullName){
        fullName = fullName + ' ' + row.middleName;
      } else {
        fullName = row.middleName;
      }
    }
    if (row.firstName){
      if (fullName){
        fullName = fullName + ' ' + row.firstName;
      } else {
        fullName = row.firstName;
      }
    }
	shipperPartyData['shipperId'] = "${shippingTrip.shipperId}";
	shipperPartyData['shipperName'] = fullName;
	
	<#assign statusDEes = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELI_ENTRY_STATUS"), null, null, null, false)/>
	
	var statusDataDE = new Array();
	<#list statusDEes as item>
		<#if item.statusId != 'DELI_ENTRY_CANCELLED' &&  item.statusId != 'DELI_ENTRY_COMPLETED' &&  item.statusId != 'DELI_ENTRY_CREATED' && item.statusId != 'DELI_ENTRY_SCHEDULED'>
			var row = {};
			<#assign descStatus = StringUtil.wrapString(item.get("description",locale))>
			row['statusId'] = '${item.statusId}';
			row['description'] = '${descStatus?if_exists}';
			statusDataDE.push(row);
		</#if>
	</#list>
</script>
