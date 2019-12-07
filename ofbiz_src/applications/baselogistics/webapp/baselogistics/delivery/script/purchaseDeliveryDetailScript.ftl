<script>
	<#assign currentStatusId = delivery.statusId?if_exists>
	<#assign currentStatus = "">
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		statusData[${item_index}] = row;
		<#if item.statusId == currentStatusId>
			<#if 'DLV_EXPORTED' == item.statusId>
				<#assign currentStatus = StringUtil.wrapString(uiLabelMap.Shipping)>;
	        <#elseif 'DLV_DELIVERED' == item.statusId>
	        	<#assign currentStatus = StringUtil.wrapString(uiLabelMap.Completed)>;
	        <#else> 
				<#assign currentStatus = descStatus?if_exists>	
	        </#if>
		</#if>
	</#list>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
</script>