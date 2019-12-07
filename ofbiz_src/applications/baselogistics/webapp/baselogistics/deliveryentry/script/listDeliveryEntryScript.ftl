<@jqGridMinimumLib />
<style>
	.bootbox{
	    z-index: 990009 !important;
	}
	.modal-backdrop{
	    z-index: 890009 !important;
	}
	 .loading-container{
	 	z-index: 999999 !important;
	 }
</style> 
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var curShipmentStatusId = "SHIPMENT_INPUT";
	<#assign localeStr = "VI" />
	<#if locale = "en">
	<#assign localeStr = "EN" /> 
	</#if>
	<#assign statusDEes = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELI_ENTRY_STATUS"), null, null, null, false)/>
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	var statusDataDE = new Array();
	var statusAllStatusDE = new Array();
	<#list statusDEes as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get("description",locale))>
		<#if item.statusId != 'DELI_ENTRY_CANCELLED'>
			statusDataDE.push(row);
		</#if>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${descStatus?if_exists}';
		statusAllStatusDE.push(row);
	</#list>
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var uomData = new Array();
	<#list uoms as item>
		var row = {};
		<#assign descUom = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
		row['weightUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${descUom?if_exists}';
		row['abbreviation'] = '${item.abbreviation?if_exists}';
		uomData.push(row);
	</#list>
	
	<#assign uomConversions = delegator.findList("UomConversion", null, null, null, null, false) />
	var uomConvertData = new Array();
	<#list uomConversions as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['uomIdTo'] = "${item.uomIdTo}";
		row['conversionFactor'] = "${item.conversionFactor}";
		uomConvertData[${item_index}] = row;
	</#list>
	
	var addZero = function(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	};
	function formatFullDate (value) {
		if (value) {
			var dateStr = "";
			dateStr += addZero(value.getDate()) + '/';
			dateStr += addZero(value.getMonth()+1) + '/';
			dateStr += addZero(value.getFullYear()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	};
	
	var delivererPartyData = [];
	$.ajax({
		type: "POST",
		url: "getPartyByRoleAndRelationship",
		async: false,
		data: {
			roleTypeId: "LOG_DELIVERER",
		},
		success: function (res){
			delivererPartyData = res.listParties;
		}
	});
	
	if (delivererPartyData.length > 0){
		for (var i = 0; i < delivererPartyData.length; i ++){
			var fullName = null;
			if (delivererPartyData[i].lastName){
				if (fullName){
					fullName = fullName + ' ' + delivererPartyData[i].lastName;
				} else {
					fullName = delivererPartyData[i].lastName;
				}
			}
			if (delivererPartyData[i].middleName){
				if (fullName){
					fullName = fullName + ' ' + delivererPartyData[i].middleName;
				} else {
					fullName = delivererPartyData[i].middleName;
				}		
			}
			if (delivererPartyData[i].firstName){
				if (fullName){
					fullName = fullName + ' ' + delivererPartyData[i].firstName;
				} else {
					fullName = delivererPartyData[i].firstName;
				}	
			}
			delivererPartyData[i]["description"] = fullName;
		}
	}
	
	var driverPartyData = [];
	$.ajax({
		type: "POST",
		url: "getPartyByRoleAndRelationship",
		async: false,
		data: {
			roleTypeId: "LOG_DRIVER",
		},
		success: function (res){
			driverPartyData = res.listParties;
		}
	});
	if (driverPartyData.length > 0){
		for (var i = 0; i < driverPartyData.length; i ++){
			var fullName = null;
			if (driverPartyData[i].lastName){
				if (fullName){
					fullName = fullName + ' ' + driverPartyData[i].lastName;
				} else {
					fullName = driverPartyData[i].lastName;
				}
			}
			if (driverPartyData[i].middleName){
				if (fullName){
					fullName = fullName + ' ' + driverPartyData[i].middleName;
				} else {
					fullName = driverPartyData[i].middleName;
				}		
			}
			if (driverPartyData[i].firstName){
				if (fullName){
					fullName = fullName + ' ' + driverPartyData[i].firstName;
				} else {
					fullName = driverPartyData[i].firstName;
				}	
			}
			driverPartyData[i]["description"] = fullName;
		}
	}
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.CannotAfterNow = "${StringUtil.wrapString(uiLabelMap.CannotAfterNow)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.BSYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseProduct)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.CommonEdit = "${StringUtil.wrapString(uiLabelMap.CommonEdit)}";
	uiLabelMap.AreYouSureDetele = "${StringUtil.wrapString(uiLabelMap.AreYouSureDetele)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	
	uiLabelMap.ViewDetailInNewPage = "${StringUtil.wrapString(uiLabelMap.ViewDetailInNewPage)}";
	uiLabelMap.BSViewDetail = "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
</script>
<script type="text/javascript" src="/logresources/js/deliveryentry/listDeliveryEntry.js"></script>