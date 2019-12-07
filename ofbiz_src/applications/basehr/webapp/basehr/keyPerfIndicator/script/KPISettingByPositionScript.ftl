<script type="text/javascript">
if(typeof(globalVar) == 'undefined'){
	globalVar = {};
}
globalVar.setKPIByPosWindow = "${setKPIByPosWindow}";
globalVar.emplPositionTypeArr = [
	<#if emplPositionTypeList?has_content>
		<#list emplPositionTypeList as emplPositionType>
		{
			emplPositionTypeId: "${emplPositionType.emplPositionTypeId}",
			description: '${emplPositionType.description}'
		},
		</#list>
	</#if>
];
if(typeof(uiLabelMap) == 'undefined'){
	uiLabelMap = {};
}
uiLabelMap.EmplPositionTypeListKPI = '${StringUtil.wrapString(uiLabelMap.EmplPositionTypeListKPI)}';
uiLabelMap.DoubleClickToChoose = '${StringUtil.wrapString(uiLabelMap.DoubleClickToChoose)}';
uiLabelMap.CommonList = '${StringUtil.wrapString(uiLabelMap.CommonList)}';
uiLabelMap.EmployeeSelectedShort = '${StringUtil.wrapString(uiLabelMap.EmployeeSelectedShort)}';
uiLabelMap.clickToAddParty = '${StringUtil.wrapString(uiLabelMap.clickToAddParty)}';
uiLabelMap.clickRemovePartySelected = '${StringUtil.wrapString(uiLabelMap.clickRemovePartySelected)}';
uiLabelMap.PositionNotSettingKPI = '${StringUtil.wrapString(uiLabelMap.PositionNotSettingKPI)}';
uiLabelMap.KPINotSelect = '${StringUtil.wrapString(uiLabelMap.KPINotSelect)}';
uiLabelMap.NotManageEmployeeHaveEmplPositionType = '${StringUtil.wrapString(uiLabelMap.NotManageEmployeeHaveEmplPositionType)}';
uiLabelMap.NoPartyChoose = '${StringUtil.wrapString(uiLabelMap.NoPartyChoose)}';
uiLabelMap.HrCommonOr = '${StringUtil.wrapString(uiLabelMap.HrCommonOr)}';
uiLabelMap.EmplHaveAssignKPI = '${StringUtil.wrapString(uiLabelMap.EmplHaveAssignKPI)}';
uiLabelMap.ConfirmSettingKPIByPositionFirst = '${StringUtil.wrapString(uiLabelMap.ConfirmSettingKPIByPositionFirst)}';
uiLabelMap.ConfirmSettingKPIByPositionSecond = '${StringUtil.wrapString(uiLabelMap.ConfirmSettingKPIByPositionSecond)}';
uiLabelMap.KPIWeigth = '${StringUtil.wrapString(uiLabelMap.KPIWeigth)}';
uiLabelMap.CommonSave = '${StringUtil.wrapString(uiLabelMap.CommonSave)}';
</script>