<script>
<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PURCH_SHIP_STATUS"), null, null, null, false)/>
var statusData = [];
<#list statuses as item>
	var row = {};
	<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
	row['statusId'] = "${item.statusId}";
	row['description'] = "${descStatus?if_exists}";
	statusData[${item_index}] = row;
</#list>

</script>

<script type="text/javascript" src="/salesmtlresources/js/shipment/purchaseShipmentDis.js?v=0.0.1"></script>