<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var shipmentTypeId = <#if defaultShipmentTypeId?exists>'${defaultShipmentTypeId}'<#else>null</#if>;
	var shipmentMethodTypeId = <#if defaultShipmentMethodTypeId?exists>'${defaultShipmentMethodTypeId}'<#else>null</#if>;
	
	<#assign shipmentMethods = delegator.findList("ShipmentMethodType", null, null, null, null, false) />
	var shipmentMethodData = new Array();
	<#list shipmentMethods as item>
		var row = {};
		row['shipmentMethodTypeId'] = "${item.shipmentMethodTypeId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		shipmentMethodData.push(row);
	</#list>
	
	<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "CURRENCY_MEASURE")), null, null, null, false)>
	var currencyUomData = [];
	<#list currencyUoms as item>
		var row = {};
		<#assign descCur = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = "${item.uomId}";
		row['abbreviation'] = "${item.abbreviation}";
		row['description'] = "${descCur?if_exists}";
		currencyUomData.push(row);
	</#list>
	
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
	</#if>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${uiLabelMap.CannotBeforeNow}";
	uiLabelMap.CanNotAfterEstimatedArrivalDate = "${uiLabelMap.CanNotAfterEstimatedArrivalDate}";
	uiLabelMap.CanNotBeforeEstimatedShipDate = "${uiLabelMap.CanNotBeforeEstimatedShipDate}";
	uiLabelMap.ValueMustBeGreateThanZero = "${uiLabelMap.ValueMustBeGreateThanZero}";
	
</script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/logresources/js/shipment/newShipmentInfo.js"></script>
