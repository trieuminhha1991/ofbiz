<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
	
	<#assign varianceReasons = delegator.findList("VarianceReason", null, null, null, null, false)/>
	var reasonData = [];
	<#list varianceReasons as item>
		var row = {};
		<#assign descReason = StringUtil.wrapString(item.get('description', locale))>
		row['varianceReasonId'] = "${item.varianceReasonId}";
		row['description'] = "${descReason?if_exists}";
		row['negativeNumber'] = "${item.negativeNumber?if_exists}";
		reasonData.push(row);
	</#list>
	var checkUpdate = false;
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
	
	var yesNoData = [];
	var yes = {
		typeId: "Y",
		description: "${StringUtil.wrapString(uiLabelMap.Decrease)}",
	}
	var no = {
		typeId: "N",
		description: "${StringUtil.wrapString(uiLabelMap.Increase)}",
	}
	yesNoData.push(no);
	yesNoData.push(yes);
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.CommonDelete = "${StringUtil.wrapString(uiLabelMap.CommonDelete)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}";
	uiLabelMap.CheckLinkedData = "${StringUtil.wrapString(uiLabelMap.CheckLinkedData)}";
	uiLabelMap.NotifiDeleteSucess = "${StringUtil.wrapString(uiLabelMap.NotifiDeleteSucess)}";
	uiLabelMap.NotifiUpdateSucess = "${StringUtil.wrapString(uiLabelMap.NotifiUpdateSucess)}";
	
	
</script>
<script type="text/javascript" src="/logresources/js/inventory/listVarianceReasons.js"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>