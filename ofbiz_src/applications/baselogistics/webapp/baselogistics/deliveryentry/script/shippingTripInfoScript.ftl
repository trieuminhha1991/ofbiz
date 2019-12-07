<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	var localeStr = "VI";
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>;
	var company = '${company?if_exists}';
	<#assign departments = Static["com.olbius.basehr.util.PartyUtil"].getDepartmentOfEmployee(delegator, userLogin.get("partyId"), nowTimestamp)!/>;
	
	var faciData = new Array();
	<#assign manager = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "roleType.manager")>
	<#assign listRoles = []>
	<#assign listRoles = listRoles + [manager]>
	<#assign listTypes = []>
	<#assign listTypes = listTypes + ["WAREHOUSE"]>
	
	<#assign facis = Static['com.olbius.baselogistics.util.LogisticsPartyUtil'].getFacilityByRolesAndFacilityTypesAndOwner(delegator, userLogin.partyId?if_exists, listRoles, listTypes, company)! />;
	<#list facis as item>
		var row = {};
		<#assign descFac = StringUtil.wrapString(item.get("facilityName",locale)?if_exists)/>
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['description'] = "${descFac?if_exists?replace('\n', ' ')}";
		faciData.push(row);
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
	
	var delivererPartyData = [];
	var driverPartyData = [];
	var departmentData = [];
	<#list departments as depId>
		var partyObj = {};
		partyObj["partyId"] = "${depId}";
		departmentData.push(partyObj); 
		<#assign vehicles = delegator.findList("FixedAsset", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("fixedAssetTypeId", "TRANSINS_TANFS", "partyId", depId?if_exists, "statusId", "FA_USING")), null, null, null, false) />
		var vehicleData = new Array();
		<#list vehicles as item>
			var row = {};
			<#assign descVehicles = StringUtil.wrapString(item.get('fixedAssetName', locale)?if_exists)/>
			row['fixedAssetId'] = '${item.fixedAssetId?if_exists}';
			row['description'] = '${descVehicles?if_exists}';
			vehicleData.push(row);
		</#list>
		
		<#assign deliverers = Static["com.olbius.basehr.util.PartyUtil"].getEmployeeHasRoleInDepartment(delegator, depId, "LOG_DELIVERER", "EMPLOYEE", nowTimestamp)!/>;
		<#assign drivers = Static["com.olbius.basehr.util.PartyUtil"].getEmployeeHasRoleInDepartment(delegator, depId, "LOG_DELIVERER", "EMPLOYEE", nowTimestamp)!/>;
		<#if deliverers?has_content>
			<#list deliverers as deliveredId>
				var row = {};
				<#assign partyName = delegator.findOne("PartyNameView", {"partyId" : deliveredId?if_exists}, false)/>
				row['lastName'] = "${partyName.lastName?if_exists}";
				row['middleName'] = "${partyName.middleName?if_exists}";
				row['firstName'] = "${partyName.firstName?if_exists}";
				row['partyId'] = "${deliveredId?if_exists}";
				delivererPartyData.push(row);
			</#list>
		</#if>
		
		<#if drivers?has_content>
			<#list drivers as driverId>
				var row = {};
				<#assign partyName2 = delegator.findOne("PartyNameView", {"partyId" : driverId?if_exists}, false)/>
				row['lastName'] = "${partyName2.lastName?if_exists}";
				row['middleName'] = "${partyName2.middleName?if_exists}";
				row['firstName'] = "${partyName2.firstName?if_exists}";
				row['partyId'] = "${driverId?if_exists}";
				driverPartyData.push(row);
			</#list>
		</#if>
	</#list>
	
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
	
	<#assign shipmentTypes = delegator.findList("ShipmentType", null, null, null, null, false) />
	var shipmentTypeDataTmp = new Array();
	var shipmentTypeData = new Array();
	var shipmentParent = new Array();
	<#list shipmentTypes as item>
		<#if item.parentTypeId?has_content>
			shipmentParent.push('${item.parentTypeId}');
		</#if>
	</#list>
	
	<#list shipmentTypes as item>
		if (shipmentParent.indexOf('${item.shipmentTypeId}') == -1){
			<#assign description = StringUtil.wrapString(item.get("description",locale))>
			var row = {};
			row['shipmentTypeId'] = '${item.shipmentTypeId}';
			row['description'] = '${description?if_exists}';
			shipmentTypeData.push(row);
			<#if item.shipmentTypeId == 'SALES_SHIPMENT'>
				shipmentTypeDataTmp.push(row);
			</#if>
		}
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["SHIPMENT_STATUS","PURCH_SHIP_STATUS"]), null, null, null, false) />
	var statusData = new Array();
	<#list statuses as item>
	    <#assign description = StringUtil.wrapString(item.get("description",locale))>
		var row = {};
		row['statusId'] = '${item.statusId}';
		row['description'] = "${description?if_exists}";
		statusData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		<#assign descTmp = StringUtil.wrapString(item.abbreviation) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${descTmp?if_exists}';
		weightUomData[${item_index}] = row;
	</#list>
	
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
	
	var curFacilityId = "${parameters.facilityId?if_exists}";
	var curFromDate = "${parameters.fromDate?if_exists}";
	var curThruDate = "${parameters.thruDate?if_exists}";
	var curShipmentStatusId = "SHIPMENT_SHIPPED";
	var getShipmentUrl = "";
	var curDeliveryEntryId = '${parameters.deliveryEntryId?if_exists}';
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${uiLabelMap.CannotBeforeNow}";
	uiLabelMap.CannotAfterNow = "${uiLabelMap.CannotAfterNow}";
	uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
	uiLabelMap.DAYouNotYetChooseProduct = "${uiLabelMap.DAYouNotYetChooseProduct}";

    uiLabelMap.CostMustBePositiveNumbers = '${StringUtil.wrapString(uiLabelMap.CostMustBePositiveNumbers)}';
	uiLabelMap.EndDateMustBeAfterStartDate = '${StringUtil.wrapString(uiLabelMap.EndDateMustBeAfterStartDate)}';
	uiLabelMap.StartDateMustBeAfterNow = '${StringUtil.wrapString(uiLabelMap.StartDateMustBeAfterNow)}';
	uiLabelMap.StartDateMustBeAfterEndDate = '${StringUtil.wrapString(uiLabelMap.StartDateMustBeAfterEndDate)}';
	
	uiLabelMap.CommonCancel = "${uiLabelMap.CommonCancel}";
	uiLabelMap.OK = "${uiLabelMap.OK}";
</script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/logresources/js/deliveryentry/shippingTripNewShippingTripInfo.js"></script>
