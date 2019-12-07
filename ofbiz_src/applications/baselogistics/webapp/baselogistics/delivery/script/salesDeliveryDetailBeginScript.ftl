<script>
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	if (globalVar === undefined) var globalVar = {};
	globalVar.currencyUomId = '${currencyUomId}';

	uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.UpdateError = "${StringUtil.wrapString(uiLabelMap.UpdateError)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
</script>
<script type="text/javascript" src="/logresources/js/delivery/salesDeliveryDetailBegin.js?v=1.1.1"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js?v=1.1.1"></script>
