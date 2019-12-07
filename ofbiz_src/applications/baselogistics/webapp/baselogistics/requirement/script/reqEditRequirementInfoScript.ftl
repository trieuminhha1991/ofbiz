<#if security.hasEntityPermission("PARTYDIST", "_VIEW", session)>
	<#assign urlDistributor = "JQGetListDistributor&sD=N" />
</#if>
<script src="/crmresources/js/generalUtils.js"></script>
<style type="text/css">
.destFaContainer {
    display: none;
}
</style>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var localeStr = "VI";
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>

	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.CannotAfterNow = "${StringUtil.wrapString(uiLabelMap.CannotAfterNow)}";
	uiLabelMap.FacilityRequired = "${StringUtil.wrapString(uiLabelMap.FacilityRequired)}";
	uiLabelMap.FacilityFrom = "${StringUtil.wrapString(uiLabelMap.FacilityFrom)}";
	uiLabelMap.FacilityTo = "${StringUtil.wrapString(uiLabelMap.FacilityTo)}";
	uiLabelMap.RequestFromFacility = "${StringUtil.wrapString(uiLabelMap.RequestFromFacility)}";
	uiLabelMap.BLFacilityId = "${StringUtil.wrapString(uiLabelMap.BLFacilityId)}";
	uiLabelMap.BLFacilityName = "${StringUtil.wrapString(uiLabelMap.BLFacilityName)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	
</script>
<script type="text/javascript" src="/logresources/js/requirement/reqEditRequirementInfo.js?v=1.1.1"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>