<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.core.js"></script>
<script type="text/javascript">
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	var listShipmentSelected = [];
	var listPackSelected = [];
	var listShipmentItemSelected = [];
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
	uiLabelMap.DAYouNotYetChooseProduct = "${uiLabelMap.DAYouNotYetChooseProduct}";
	uiLabelMap.HasErrorWhenProcess = "${uiLabelMap.HasErrorWhenProcess}";
	uiLabelMap.SuccessfulWhenCreate = "${uiLabelMap.SuccessfulWhenCreate}";
</script>
<script type="text/javascript" src="/logresources/js/trip/newTripTemplate.js"></script>
