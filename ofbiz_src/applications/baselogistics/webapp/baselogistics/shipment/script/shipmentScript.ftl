<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/logresources/js/shipment/shipment.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript">
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
	</#if>
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>

	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "WEIGHT_MEASURE")), null, null, null, false) />
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		weightUomData.push(row);
	</#list>
	
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false) />
	var quantityUomData = new Array();
	<#list weightUoms as item>
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
</script>