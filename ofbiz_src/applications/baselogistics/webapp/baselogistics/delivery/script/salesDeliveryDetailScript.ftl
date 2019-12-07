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
			<#assign currentStatus = descStatus?if_exists>		
		</#if>
	</#list>
	<#assign hasDistributorRole = Static["com.olbius.basehr.util.SecurityUtil"].hasRole("DISTRIBUTOR", userLogin.partyId, delegator)/>
	<#if  hasDistributorRole == true>
		<#if delivery.shipmentDistributorId?has_content>
			<#assign currentStatus = StringUtil.wrapString(uiLabelMap.Received)>
		<#else>
			<#assign currentStatus = StringUtil.wrapString(uiLabelMap.BLNotYetReceive)>
		</#if>
		<#if delivery.statusId == 'DLV_CANCELLED' >
			<#assign currentStatus = StringUtil.wrapString(uiLabelMap.BLCancel)>
		</#if>
	</#if>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
</script>
<script type="text/javascript" src="/logresources/js/delivery/salesDeliveryDetail.js?v=1.1.1"></script>