<@jqGridMinimumLib />

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>

<@jqOlbCoreLib hasComboBox=true hasValidator=true/>

<#assign picklistBinStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "PICKBIN_STATUS"}, null, false)/>
<script type="text/javascript">	
	var picklistBinStatusData = [
	<#if picklistBinStatuses?exists><#list picklistBinStatuses as statusItem>{
		statusId: '${statusItem.statusId}', description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},</#list></#if>];
	
	var getStatusDescription = function (statusId) {
		for (var x in picklistBinStatusData) {
			if (picklistBinStatusData[x].statusId == statusId) return picklistBinStatusData[x].description;
		}
		return statusId;
	}
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.BLCreateSalesDelivery = "${StringUtil.wrapString(uiLabelMap.BLCreateSalesDelivery)}";
	uiLabelMap.UpdateError = "${StringUtil.wrapString(uiLabelMap.UpdateError)}";
	
</script>
<script type="text/javascript" src="/logresources/js/picklist/listPicklistBins.js?v=0.0.1"></script>