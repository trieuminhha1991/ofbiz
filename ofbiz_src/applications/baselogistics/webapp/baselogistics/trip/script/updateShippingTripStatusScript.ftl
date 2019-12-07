<@jqGridMinimumLib />
<@jqOlbCoreLib hasCore=false hasValidator=true hasGrid=true/>
<script type="text/javascript">
$.jqx.theme = 'olbius';
theme = $.jqx.theme;

var localeStr = "VI";
<#assign localeStr = "VI" />
<#if locale = "en">
	<#assign localeStr = "EN" />
	localeStr = "EN";
</#if>
  <#assign trip = delegator.findOne("ShippingTrip", {"shippingTripId" : shippingTripId?if_exists}, false)/>
  <#assign partyName = delegator.findOne("PartyNameView", {"partyId" : trip.shipperId?if_exists}, false)/>
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
	var tripStatus = "${trip.statusId}";
	<#assign statusDEes = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "TRIP_STATUS"), null, null, null, false)/>
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	var statusDataDE = new Array();
	var statusAllStatusDE = new Array();
	<#list statusDEes as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get("description",locale))>
		<#if item.statusId != 'TRIP_CANCELLED'>
			statusDataDE.push(row);
		</#if>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${descStatus?if_exists}';
		statusAllStatusDE.push(row);
	</#list>
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.Done = "${uiLabelMap.Done}";
	uiLabelMap.OrderId = "${uiLabelMap.OrderId}";
	uiLabelMap.CustomerName = "${uiLabelMap.CustomerName}";
	uiLabelMap.BLDmsCustomerAddress = "${uiLabelMap.BLDmsCustomerAddress}";
	uiLabelMap.Done = "${uiLabelMap.Done}";
	uiLabelMap.Canceled = "${uiLabelMap.Canceled}";
	uiLabelMap.SequenceId = '${uiLabelMap.SequenceId}';
	uiLabelMap.PackCode = '${uiLabelMap.PackCode}';
	uiLabelMap.BLProductStoreId = '${uiLabelMap.BLProductStoreId}';
	uiLabelMap.ShipAfterDate = '${uiLabelMap.ShipAfterDate}';
	uiLabelMap.ShipBeforeDate = '${uiLabelMap.ShipBeforeDate}';
	uiLabelMap.Status = '${uiLabelMap.Status}';
    uiLabelMap.BLCompleted='${StringUtil.wrapString(uiLabelMap.BLCompleted)}';
    uiLabelMap.OrderId='${StringUtil.wrapString(uiLabelMap.OrderId)}';
	uiLabelMap.AreYouSureUpdate = '${uiLabelMap.AreYouSureUpdate}';
	uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
	uiLabelMap.DAYouNotYetChooseProduct = "${uiLabelMap.DAYouNotYetChooseProduct}";
	uiLabelMap.HasErrorWhenProcess = "${uiLabelMap.HasErrorWhenProcess}";
	uiLabelMap.SuccessfulWhenCreate = "${uiLabelMap.SuccessfulWhenCreate}";
	uiLabelMap.SuccessfulWhenUpdate = "${uiLabelMap.SuccessfulWhenUpdate}";
  var shippingTrip = {};
  shippingTrip['shippingTripId'] = "${trip.shippingTripId}";
  shippingTrip['shipper'] = fullName;
  shippingTrip['tripCost'] = "${trip.tripCost}";
  shippingTrip['costCustomerPaid'] = "${trip.costCustomerPaid}";
  shippingTrip['startDateTime'] = "${trip.startDateTime}";
  shippingTrip['finishedDateTime'] = "${trip.finishedDateTime}";
  shippingTrip['description'] = "${trip.description?if_exists}";
</script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.core.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.util.js"></script>

<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>