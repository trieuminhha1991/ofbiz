<@jqGridMinimumLib />
<#if useCss?if_exists == "N">
<#else>
<style type="text/css">
	.span6 label{
		text-align:right;
		width:300px;
	} 
	.ace-file-input .remove {
		right: 20px;
		padding-left: 2px;
	}
	.ace-file-input label span {
		margin-right: 110px;
		padding-right: 50px;
	}
	.ace-file-input {
		position: relative;
		height: 25px;
		line-height: 25px;
		margin-bottom: 0px;
	}
</style>
</#if>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	<#assign countries = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY")), null, null, null, false) />
	var countryData = new Array();
	<#list countries as item>
		var row = {};
		row['geoId'] = "${item.geoId}";
		row['description'] = "${StringUtil.wrapString(item.geoName?if_exists)}";
		countryData[${item_index}] = row;
	</#list>

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.OnlyNumberInput = "${StringUtil.wrapString(uiLabelMap.OnlyNumberInput)}";
	uiLabelMap.PhoneNumberMustInOto9 = "${StringUtil.wrapString(uiLabelMap.PhoneNumberMustInOto9)}";
	uiLabelMap.PhoneNumberMustBeContain10or11character = "${StringUtil.wrapString(uiLabelMap.PhoneNumberMustBeContain10or11character)}";
	
</script>
<script type="text/javascript" src="/logresources/js/facility/facilityNewFacilityAddress.js?v=1.1.1"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>