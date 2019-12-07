<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#assign localeStr = "VI" />

<#if locale = "en">
    <#assign localeStr = "EN" />
<#elseif locale= "en_US">
	<#assign localeStr = "EN" />
</#if>
<script>
	<#assign pickbinStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "PICKBIN_STATUS"}, null, false)/>
	var pickbinStatusData = [
		<#if pickbinStatuses?exists>
			<#list pickbinStatuses as statusItem>
				{
					statusId: "${statusItem.statusId}", 
					description: "${StringUtil.wrapString(statusItem.get("description", locale))}"
				},
			</#list>
		</#if>
	];
	
	var mapPickbinStatus = {
		<#if pickbinStatuses?exists>
			<#list pickbinStatuses as statusItem>
				"${statusItem.statusId}": "${StringUtil.wrapString(statusItem.get("description", locale))}",
			</#list>
		</#if>
	}
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BLYouNotUpdateEmployeeYet = "${StringUtil.wrapString(uiLabelMap.BLYouNotUpdateEmployeeYet)}";
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.HasErrorWhenProcess = "${StringUtil.wrapString(uiLabelMap.HasErrorWhenProcess)}";
	uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
	uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
	uiLabelMap.fieldRequired = "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}";
	uiLabelMap.updateError = "${StringUtil.wrapString(uiLabelMap.updateError)}";
	uiLabelMap.updateSuccess = "${StringUtil.wrapString(uiLabelMap.updateSuccess)}";
	uiLabelMap.BLSearchProductInPicklistBin = "${StringUtil.wrapString(uiLabelMap.BLSearchProductInPicklistBin)}";
	uiLabelMap.BSProductId = "${StringUtil.wrapString(uiLabelMap.BSProductId)}";
	uiLabelMap.BSProductName = "${StringUtil.wrapString(uiLabelMap.BSProductName)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.DmsDownloadPicklist = "${StringUtil.wrapString(uiLabelMap.DmsDownloadPicklist)}";
	
	uiLabelMap.AreYouSureSave = '${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}';
	uiLabelMap.CommonCancel = '${StringUtil.wrapString(uiLabelMap.CommonCancel)}';
	uiLabelMap.OK = '${StringUtil.wrapString(uiLabelMap.OK)}';
	uiLabelMap.AreYouSureCreate = '${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}';
	uiLabelMap.UpdateError = '${StringUtil.wrapString(uiLabelMap.UpdateError)}';
</script>
<@jqOlbCoreLib />
<script type="text/javascript" src="/logresources/js/picklist/PicklistBin.js?v=0.0.1"></script>