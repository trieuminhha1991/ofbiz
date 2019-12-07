<script type="text/javascript">

	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "INV_NON_SER_STTS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		statusData.push(row);
	</#list>

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.FromDateMustBeLesserThanThruDate = "${StringUtil.wrapString(uiLabelMap.FromDateMustBeLesserThanThruDate)}";
	uiLabelMap.SearchByNameOrId = "${StringUtil.wrapString(uiLabelMap.SearchByNameOrId)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.CommonCancel	= "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.NameOfImagesMustBeLessThan50Character = "${StringUtil.wrapString(uiLabelMap.NameOfImagesMustBeLessThan50Character)}";
	uiLabelMap.Location	= "${StringUtil.wrapString(uiLabelMap.Location)}";
	uiLabelMap.Inventory = "${StringUtil.wrapString(uiLabelMap.Inventory)}";
	uiLabelMap.Edit	= "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.ConfirmDeleteLocationDetails = "${StringUtil.wrapString(uiLabelMap.ConfirmDeleteLocationDetails)}";
	uiLabelMap.OK	= "${StringUtil.wrapString(uiLabelMap.OK)}";
	
</script>