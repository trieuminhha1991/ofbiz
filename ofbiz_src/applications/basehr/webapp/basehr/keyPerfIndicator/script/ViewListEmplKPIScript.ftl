<script type="text/javascript">
if(typeof(globalVar) == 'undefined'){
	var globalVar = {};
}
globalVar.listEmplKPIWindow = "${listEmplKPIWindow}";

<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
	if(typeof(globalVar.expandTreeId) == 'undefined'){
		globalVar.expandTreeId = "${expandTreeId}";		
	}
<#else>
	<#assign expandTreeId="">
</#if>
<#if security.hasEntityPermission("HR_KPIPERF", "_UPDATE", session)>
globalVar.updatePermission = true;
</#if>
<#if security.hasEntityPermission("HR_KPIPERF", "_DELETE", session)>
globalVar.deletePermission = true;
</#if>
if(typeof(uiLabelMap) == 'undefined'){
	uiLabelMap = {};
}

uiLabelMap.ListKPIAssignForEmpl = '${StringUtil.wrapString(uiLabelMap.ListKPIAssignForEmpl)}';
uiLabelMap.AssignKPIForEmpl = '${StringUtil.wrapString(uiLabelMap.AssignKPIForEmpl)}';
uiLabelMap.CommonFromDate = '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}';
uiLabelMap.CommonThruDate = '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}';
uiLabelMap.CommonStatus = '${StringUtil.wrapString(uiLabelMap.CommonStatus)}';
uiLabelMap.CommonEmployee = '${StringUtil.wrapString(uiLabelMap.CommonEmployee)}';
uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.ExpireDateMustGreaterOrEqualThanEffectiveDate = "${StringUtil.wrapString(uiLabelMap.ExpireDateMustGreaterOrEqualThanEffectiveDate)}";
uiLabelMap.AssignKPIConfirm = "${StringUtil.wrapString(uiLabelMap.AssignKPIConfirm)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.EmployeeSelected = "${StringUtil.wrapString(uiLabelMap.EmployeeSelected)}";
uiLabelMap.NoPartyChoose = "${StringUtil.wrapString(uiLabelMap.NoPartyChoose)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeListSelected = "${StringUtil.wrapString(uiLabelMap.EmployeeListSelected)}";
</script>