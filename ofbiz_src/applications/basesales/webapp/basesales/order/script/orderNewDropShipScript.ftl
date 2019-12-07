<script type="text/javascript">
	var isFavorDelivery = <#if isFavorDelivery?exists && isFavorDelivery>true<#else>false</#if>;
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSSupplierId = '${StringUtil.wrapString(uiLabelMap.BSSupplierId)}';
	uiLabelMap.BSFullName = '${StringUtil.wrapString(uiLabelMap.BSFullName)}';
	uiLabelMap.BSFacilityId = '${StringUtil.wrapString(uiLabelMap.BSFacilityId)}';
	uiLabelMap.BSFacilityName = '${StringUtil.wrapString(uiLabelMap.BSFacilityName)}';
</script>
<script type="text/javascript" src="/salesresources/js/order/orderNewDropShip.js"></script>