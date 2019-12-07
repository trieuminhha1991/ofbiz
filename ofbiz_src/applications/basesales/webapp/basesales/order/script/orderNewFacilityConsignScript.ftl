<script type="text/javascript">
	var isShipFromFacilityConsign = <#if isShipFromFacilityConsign?exists && isShipFromFacilityConsign>true<#else>false</#if>;
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSSupplierId = '${StringUtil.wrapString(uiLabelMap.BSSupplierId)}';
	uiLabelMap.BSDistributorId = '${StringUtil.wrapString(uiLabelMap.BSDistributorId)}';
	uiLabelMap.BSFullName = '${StringUtil.wrapString(uiLabelMap.BSFullName)}';
	uiLabelMap.BSFacilityId = '${StringUtil.wrapString(uiLabelMap.BSFacilityId)}';
	uiLabelMap.BSFacilityName = '${StringUtil.wrapString(uiLabelMap.BSFacilityName)}';
</script>
<script type="text/javascript" src="/salesresources/js/order/orderNewFacilityConsign.js"></script>