<@jqGridMinimumLib/>
<#include "listLocationScript.ftl"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.full.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.edit2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.js"></script>
<script type="text/javascript" src="/logresources/js/Underscore1.8.3.js"></script>
<script type="text/javascript" src="/logresources/js/miscUtil.js"></script>
<#assign localeStr = "VI" />
<#if locale = "en">
    <#assign localeStr = "EN" />
</#if>
<#assign listLocationFacilityType = delegator.findList("LocationFacilityType", null, null, null, null, false) />
<#assign listVarianceReason = delegator.findList("VarianceReason", null, null, null, null, false) />
<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
<#-- <#assign listLocationFacilityType = delegator.findList("LocationFacilityType", null, null, null, null, false) /> -->
<script>
	var locationFacilityTypeData = [
	   	<#if listLocationFacilityType?exists>
	   		<#list listLocationFacilityType as item>
	   			{
	   				locationFacilityTypeId: "${item.locationFacilityTypeId?if_exists}",
	   				<#assign s = StringUtil.wrapString(item.get("description", locale)?if_exists)/>
	   				description: "${s}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.DuplicateLocationCode = "${StringUtil.wrapString(uiLabelMap.DuplicateLocationCode)}";
	uiLabelMap.ContainSpecialSymbol = "${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}";
	uiLabelMap.UpdateError = "${StringUtil.wrapString(uiLabelMap.UpdateError)}";
	uiLabelMap.wgaddsuccess = "${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}";
	uiLabelMap.NeedReposition = "${StringUtil.wrapString(uiLabelMap.NeedReposition)}";
	uiLabelMap.Location = "${StringUtil.wrapString(uiLabelMap.Location)}";
	uiLabelMap.LocationType	= "${StringUtil.wrapString(uiLabelMap.LocationType)}";
	uiLabelMap.Description	= "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.Contain = "${StringUtil.wrapString(uiLabelMap.Contain)}";
	uiLabelMap.LogProduct	= "${StringUtil.wrapString(uiLabelMap.LogProduct)}";
	uiLabelMap.QuantitySumTotal	= "${StringUtil.wrapString(uiLabelMap.QuantitySumTotal)}";
	uiLabelMap.DivideLocation	= "${StringUtil.wrapString(uiLabelMap.DivideLocation)}";
	uiLabelMap.AddChildLocationType	= "${StringUtil.wrapString(uiLabelMap.AddChildLocationType)}";
	uiLabelMap.CommonRemove	= "${StringUtil.wrapString(uiLabelMap.CommonRemove)}";
	uiLabelMap.CreateNewLocationType	= "${StringUtil.wrapString(uiLabelMap.CreateNewLocationType)}";
	uiLabelMap.Edit	= "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	uiLabelMap.StatusLocationHasItems = "${StringUtil.wrapString(uiLabelMap.StatusLocationHasItems)}";
	uiLabelMap.ChooseLocation = "${StringUtil.wrapString(uiLabelMap.ChooseLocation)}";
	uiLabelMap.LocationNotHasProduct = "${StringUtil.wrapString(uiLabelMap.LocationNotHasProduct)}";
	
	uiLabelMap.ChooseLocationToMove	= "${StringUtil.wrapString(uiLabelMap.ChooseLocationToMove)}";
	uiLabelMap.OK	= "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.wgpagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}";
	uiLabelMap.wgpagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}";
	uiLabelMap.wgpagerrangestring = "${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)}";
	uiLabelMap.wgpagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
	uiLabelMap.wgpagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
	uiLabelMap.wgsortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
	uiLabelMap.wgsortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
	uiLabelMap.wgsortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
	uiLabelMap.wgemptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
	uiLabelMap.wgfilterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
	uiLabelMap.wgfilterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
	uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	uiLabelMap.wgdragDropToGroupColumn = "${StringUtil.wrapString(uiLabelMap.wgdragDropToGroupColumn)}";
	uiLabelMap.wgtodaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
	uiLabelMap.wgclearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
</script>
<script type="text/javascript" src="/logresources/js/facility/locationFacility.js?v=0.0.1"></script>