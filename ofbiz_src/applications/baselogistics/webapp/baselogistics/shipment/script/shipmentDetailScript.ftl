<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript">
	var localeStr = "VI";
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	
	<#assign shipmentId = parameters.shipmentId?if_exists/>
	var shipmentId = '${shipmentId?if_exists}';
	<#assign shipment = delegator.findOne("ShipmentAndContactMechDetail", {"shipmentId" : parameters.shipmentId?if_exists}, false)/>
	
	var shipment = {};
	shipment['shipmentId'] = '${shipment.shipmentId?if_exists}';
	shipment['originFacilityId'] = '${shipment.originFacilityId?if_exists}';
	shipment['destinationFacilityId'] = '${shipment.destinationFacilityId?if_exists}';
	shipment['originAddress'] = '${StringUtil.wrapString(shipment.originAddress?if_exists)}';
	shipment['destAddress'] = '${StringUtil.wrapString(shipment.destAddress?if_exists)}';
	shipment['originFacilityName'] = '${StringUtil.wrapString(shipment.originFacilityName?if_exists)}';
	shipment['destFacilityName'] = '${StringUtil.wrapString(shipment.destFacilityName?if_exists)}';
	shipment['originContactMechId'] = '${shipment.originContactMechId?if_exists}';
	shipment['destinationContactMechId'] = '${shipment.destinationContactMechId?if_exists}';
	shipment['estimatedShipCost'] = '${shipment.estimatedShipCost?if_exists}';
	shipment['estimatedShipDate'] = '${shipment.estimatedShipDate?if_exists}';
	shipment['estimatedArrivalDate'] = '${shipment.estimatedArrivalDate?if_exists}';
	shipment['currencyUomId'] = '${shipment.currencyUomId?if_exists}';	
	shipment['statusId'] = '${shipment.statusId?if_exists}';	
	shipment['actualShipDate'] = '${shipment.actualShipDate?if_exists}';
	shipment['actualArrivalDate'] = '${shipment.actualArrivalDate?if_exists}';
	
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false) />
	var quantityUomData = new Array();
	<#list quantityUoms as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		quantityUomData.push(row);
	</#list>
	
	<#assign shipmentMethods = delegator.findList("ShipmentMethodType", null, null, null, null, false) />
	var shipmentMethodData = new Array();
	<#list shipmentMethods as item>
		var row = {};
		row['shipmentMethodTypeId'] = "${item.shipmentMethodTypeId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		shipmentMethodData.push(row);
	</#list>
	
	<#assign shipmentStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "SHIPMENT_STATUS")), null, null, null, false) />
	var statusData = new Array();
	<#list shipmentStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		statusData.push(row);
	</#list>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.CanNotAfterArrivalDate = "${StringUtil.wrapString(uiLabelMap.CanNotAfterArrivalDate)}";
	uiLabelMap.CanNotBeforeShipDate = "${StringUtil.wrapString(uiLabelMap.CanNotBeforeShipDate)}";
	uiLabelMap.CannotAfterNow = "${StringUtil.wrapString(uiLabelMap.CannotAfterNow)}";
	
</script>