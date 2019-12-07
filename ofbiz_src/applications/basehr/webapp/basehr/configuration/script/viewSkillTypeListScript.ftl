<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script>
	
	<#assign parentSkillTypes = delegator.findList("SkillType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, null), null, null, null, false)/>
	var parentSkillTypeData = [];
	<#list parentSkillTypes as item>
		var row = {};
		<#assign desc = StringUtil.wrapString(item.get('description', locale))>
		row['skillTypeId'] = "${item.skillTypeId?if_exists}";
		row['description'] = "${desc?if_exists}";
		parentSkillTypeData.push(row);
	</#list>
	
	var checkUpdate = false;
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
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
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	
</script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/hrresources/js/configuration/viewSkillTypeList.js"></script>